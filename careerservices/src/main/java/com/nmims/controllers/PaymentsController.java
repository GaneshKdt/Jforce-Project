package com.nmims.controllers;

import java.io.IOException;
import java.util.Locale;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import com.google.gson.Gson;
import com.nmims.beans.CSResponse;
import com.nmims.beans.PacakageAvailabilityBean;
import com.nmims.beans.PaymentDetails;
import com.nmims.beans.PaymentGatewayConstants;
import com.nmims.beans.PaymentInitiationModelBean;
import com.nmims.beans.UpgradePathDetails;
import com.nmims.daos.PackageApplicabilityDAO;
import com.nmims.daos.PaymentManagerDAO;
import com.nmims.helpers.DataValidationHelpers;
import com.nmims.helpers.SalesForceHelper_Packages;

@Controller
public class PaymentsController extends BaseController{


//	@Autowired
//	private StudentDataManagementDAO studentDataManagementDAO;
//	

	@Autowired 
	private PaymentManagerDAO paymentManagerDAO;

	@Autowired
	private PackageApplicabilityDAO packageApplicabilityDAO;
	
	@Autowired
	SalesForceHelper_Packages salesForceHelper;

	DataValidationHelpers validationHelpers = new DataValidationHelpers();
	private Gson gson = new Gson();

	private static final Logger logger = LoggerFactory.getLogger(PaymentsController.class);
 
	//Show user details; Check if student details complete. If all fine, proceed to checkout initialization
	@RequestMapping(value = "/applyForProduct", method = RequestMethod.GET)
	public String applyForProduct(HttpServletRequest request, Model model, @RequestParam("packageId") String packageId) {
		String sapid = (String)request.getSession().getAttribute("userId");
		
		if(!packageApplicabilityDAO.checkIfPackageApplicableForStudent(packageId, sapid)) {
			model.addAttribute("errorMessage", "This package is not applicable for you!");
			return "redirect:showAllProducts";
		}
		
		model.addAttribute("Package", packageApplicabilityDAO.getPackageFromId(packageId));
		return "portal/applyForPackage";
	}
	
	@RequestMapping(value = "/startCheckout", method = RequestMethod.GET)
	public String startCheckout(HttpServletRequest request, Model model, @RequestParam("packageId") String packageId) throws IOException{

		/*
		 * Double check all parameters like if the student is actually eligible for the package and if they are eligible for the package. 
		 * 	If yes; proceed to token generation and checkout for SF
		 * 	If no; return to CS homepage
		 */
		String cantPurchasePackageUrl = "showAllProducts";

		String sapid = (String)request.getSession().getAttribute("userId");

		PaymentInitiationModelBean paymentInitiationModelBean = new PaymentInitiationModelBean();
		paymentInitiationModelBean.setSource(PaymentGatewayConstants.PAYMENT_SOURCE_PORTAL_WEB);
		paymentInitiationModelBean.setSapid(sapid);
		paymentInitiationModelBean.setPackageId(packageId);
		doCheckOutValidations(paymentInitiationModelBean);
		model.addAttribute("errorMessage", paymentInitiationModelBean.getErrorMessage());
		//start checkout process if the student details are complete

		if(paymentInitiationModelBean.getSalesForceURL() == null) {
			paymentInitiationModelBean.setSalesForceURL(cantPurchasePackageUrl);
		}
		return "redirect:" + paymentInitiationModelBean.getSalesForceURL();
	}
	
	@RequestMapping(value = "/m/startCheckout", method = RequestMethod.POST, consumes = "application/json",produces = "application/json")
	public ResponseEntity<String> startCheckout(HttpServletRequest request, HttpServletResponse response, @RequestBody PaymentInitiationModelBean requestParams){
		CSResponse csResponse = new CSResponse();
		if(requestParams.getSapid() == null) {
			csResponse.setNoSapid();
			return ResponseEntity.ok(gson.toJson(csResponse));
		}else if(requestParams.getSapid().contentEquals("")) {
			csResponse.setInvalidSapid();
			return ResponseEntity.ok(gson.toJson(csResponse));
		}

		if(requestParams.getPackageId() == null) {
			csResponse.setNoPacakgeId();
			return ResponseEntity.ok(gson.toJson(csResponse));
		}else if(requestParams.getPackageId().contentEquals("")) {
			csResponse.setInvalidPacakgeId();
			return ResponseEntity.ok(gson.toJson(csResponse));
		}

		PaymentInitiationModelBean paymentInitiationModelBean = requestParams;

		paymentInitiationModelBean.setSource(PaymentGatewayConstants.PAYMENT_SOURCE_PORTAL_MOBILE);
		doCheckOutValidations(paymentInitiationModelBean);
		if(paymentInitiationModelBean.getSalesForceURL() == null) {
			csResponse.setStatusFailure();
		}else {
			csResponse.setStatusSuccess();
		}
		csResponse.setResponse(paymentInitiationModelBean);
		return ResponseEntity.ok(gson.toJson(csResponse));
	}
	
	private void doCheckOutValidations(PaymentInitiationModelBean paymentInitiationModelBean) {
		try {
			getPackageApplicableForStudent(paymentInitiationModelBean);
		}catch (Exception e) {
			logger.info("in PaymentsController class got exception : "+e.getMessage());
			paymentInitiationModelBean.setPackageApplicable(false);
			paymentInitiationModelBean.setErrorMessage("Error checking package applicability. Please check the sapid.");
		}
		if(paymentInitiationModelBean.getSalesForcePackageId() == null) {
			paymentInitiationModelBean.setPackageApplicable(false);
			paymentInitiationModelBean.setErrorMessage("No applicable package with this id was found.");
			return;
		}else {
			paymentInitiationModelBean.setPackageApplicable(true);
		}

		//check if any pending transactions in db table
		paymentManagerDAO.checkIfStudentCanInitiateNewTransactions(paymentInitiationModelBean);

		if(paymentInitiationModelBean.isPaymentPreviouslyInitiated()) {
			paymentInitiationModelBean.setErrorMessage("Previous transaction still pending!");
			return;
		}

		//add new initiated transaction in db table
		try {

			//Get URL from salesforce
			salesForceHelper.getPaymentURL(paymentInitiationModelBean);

			if(!paymentManagerDAO.addInitializedTransaction(paymentInitiationModelBean)) {
				paymentInitiationModelBean.setErrorMessage("Error initiating transaction!");
				return;
			}

			if(checkIfEmptyOrNull(paymentInitiationModelBean.getSalesForceURL())) {
				paymentManagerDAO.changePaymentRecordStatus(
					PaymentGatewayConstants.PAYMENT_SALESFORCE_STATUS_INITIATION_FAILED, 
					paymentInitiationModelBean.getSource(),
					paymentInitiationModelBean.getErrorMessage(), 
					paymentInitiationModelBean.getSapid(), 
					paymentInitiationModelBean.getPaymentInitializedId(),
					"TRAN FAIL"
				);
				return;
			}
		}catch (Exception e) {
			logger.info("in PaymentsController class got exception : "+e.getMessage());
			paymentInitiationModelBean.setErrorMessage("Error initiating transaction!");
			return;
		}
	}

	private void getPackageApplicableForStudent(PaymentInitiationModelBean paymentInitiationModelBean) {
		for(UpgradePathDetails pathDetails : packageApplicabilityDAO.studentApplicablePackages(paymentInitiationModelBean.getSapid())) {
			for(PacakageAvailabilityBean applicablePackage: pathDetails.getPackages()) {
				if(applicablePackage.getPackageId().equals(paymentInitiationModelBean.getPackageId())) {
					paymentInitiationModelBean.setSalesForcePackageId(applicablePackage.getSalesForceUID());
				}
			}
		}
	}

	/*
	 * Response URL for salesforce. 
	 * Key-word 'paymentResponse' is required by Mobile
	 */
		@RequestMapping(value = {"/paymentResponseSuccess", "/paymentResponseSuccessMobile"}, method = RequestMethod.GET)
		public String paymentResponseSuccess(HttpServletRequest request, HttpServletResponse response, Model model) {
	
			PaymentDetails paymentResponse = new PaymentDetails();
			paymentResponse.setSource(PaymentGatewayConstants.PAYMENT_SOURCE_PORTAL);
			try {
				salesForceHelper.managePaymentResponse(request, paymentResponse);
	
				paymentManagerDAO.changePaymentRecordStatus(
					PaymentGatewayConstants.PAYMENT_SALESFORCE_STATUS_PAYMENT_MADE, 
					PaymentGatewayConstants.PAYMENT_SOURCE_PORTAL,
					paymentResponse.getMessage(), 
					paymentResponse.getSapid(), 
					paymentResponse.getPaymentInitializationId(),
					paymentResponse.getMerchantTrackId()
				);
				model.addAttribute("successMessage", ""
						+ "Your payment was successful<br>"
						+ "Please wait while we process your transaction!");
				if((String)request.getSession().getAttribute("userId") != null) {
					String sapid = (String)request.getSession().getAttribute("userId");
					if(paymentResponse.getSapid().equals(sapid)) {
						resetStudentInSession(request, response);
						model.addAttribute("resetSessions", "true");
						return "redirect:showAllProducts";
					}
				}
				return "paymentSuccessPage";
			}catch (Exception e) {
				//This is to check for an exception. Add this exception to the records.
				logger.info("in PaymentsController class got exception : "+e.getMessage());
				paymentManagerDAO.changePaymentRecordStatus(
					PaymentGatewayConstants.PAYMENT_SALESFORCE_STATUS_ERROR, 
					PaymentGatewayConstants.PAYMENT_SOURCE_PORTAL, 
					"Error adding package for student", 
					paymentResponse.getSapid(), 
					paymentResponse.getPaymentInitializationId(),
					paymentResponse.getMerchantTrackId()
				);
				paymentResponse.setReasonForFail("Java Exception: " + e);
				paymentManagerDAO.addFailedProductIntimation(paymentResponse);
	
				model.addAttribute("errorMessage", paymentResponse.getReasonForFail());
				return "paymentFailurePage";
			}
		}
	
		@RequestMapping(value = {"/paymentResponseFailure", "/paymentResponseFailureMobile"}, method = RequestMethod.GET)
		public String paymentResponseFailure(HttpServletRequest request, Locale locale, Model model) {
			PaymentDetails paymentResponse = new PaymentDetails();
			salesForceHelper.getPaymentDetails(request, paymentResponse);
			paymentManagerDAO.changePaymentRecordStatus(
				PaymentGatewayConstants.PAYMENT_SALESFORCE_STATUS_PAYMENT_TRANSACTION_FAILED, 
				PaymentGatewayConstants.PAYMENT_SOURCE_PORTAL, 
				paymentResponse.getMessage(), 
				paymentResponse.getSapid(), 
				paymentResponse.getPaymentInitializationId(),
				paymentResponse.getMerchantTrackId()
			);
			model.addAttribute("infoMessage", "Payment Gateway message: <br>" + paymentResponse.getMessage());
			return "paymentFailurePage";
		}
	/*
	 *	--End-- 
	 */
	
	@RequestMapping(value = "/cancelPaymentForStudent", method = RequestMethod.POST, produces = "application/json", consumes = "application/json")
	public ResponseEntity<String> cancelPaymentForStudent(Locale locale, Model model, @RequestBody Map<String, String> requestParams) {
		CSResponse csResponse = new CSResponse();
		if(requestParams.get("paymentInitializationId") == null) {
			csResponse.setStatusFailure();
			csResponse.setMessage("no paymentInitializationId");
			return ResponseEntity.ok(gson.toJson(csResponse));
		}
		if(requestParams.get("sapid") == null) {
			csResponse.setStatusFailure();
			csResponse.setMessage("no sapid");
			return ResponseEntity.ok(gson.toJson(csResponse));
		}
		if(paymentManagerDAO.cancelPayment(requestParams.get("paymentInitializationId"), requestParams.get("sapid"))){
			csResponse.setStatusSuccess();
		}
		return ResponseEntity.ok(gson.toJson(csResponse));
	}

//	@RequestMapping(value = "/givePackageToStudent", method = RequestMethod.POST, produces = "application/json", consumes = "application/json")
//	public ResponseEntity<String> givePackageToStudent(Locale locale, @RequestBody PaymentDetails paymentResponse) {
//		CSResponse csResponse = new CSResponse();
//		paymentResponse.setSource("API");
//
//		try {
//			//just add a successful transaction for the student
//			boolean status = paymentManagerDAO.addSuccessfulTransaction(
//					paymentResponse.getSapid(), 
//					paymentResponse.getPackageId(), 
//					paymentResponse.getMerchantTrackId(), 
//					paymentResponse.getPaymentInitializationId()
//			);
//			if(status) {
//				csResponse.setStatusSuccess();
//				return ResponseEntity.ok(gson.toJson(csResponse));
//			}
//		} catch (Exception e) {
//			logger.info("in PaymentsController class got exception : "+e.getMessage());
//			csResponse.setMessage(e.getMessage());
//		}
//		csResponse.setStatusFailure();
//		
//		return ResponseEntity.ok(gson.toJson(csResponse));
//	}

}
