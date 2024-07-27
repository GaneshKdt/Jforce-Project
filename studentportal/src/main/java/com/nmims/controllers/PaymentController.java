/*package com.nmims.controllers;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.math.BigInteger;
import java.net.URLEncoder;
import java.security.MessageDigest;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang.RandomStringUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.web.servlet.view.RedirectView;

import com.google.gson.Gson;
import com.nmims.beans.AdhocPaymentBean;
import com.nmims.beans.ExamBookingTransactionBean;
import com.nmims.beans.ServiceRequest;
import com.nmims.beans.ServiceRequestDocumentBean;
import com.nmims.beans.StudentBean;
import com.nmims.beans.TransactionBean;
import com.nmims.beans.WalletBean;
import com.nmims.daos.PortalDao;
import com.nmims.daos.ServiceRequestDao;
import com.nmims.daos.WalletDAO;

@Controller
public class PaymentController extends BaseController{
	@Autowired
	ApplicationContext act;

	@Autowired
	WalletDAO walletDao;

	@Autowired
	ServiceRequestDao serviceRequestDao;

	@Value("${V3URL}")
	private String V3URL;

	@Value("${WALLET_API}")
	private String WALLET_API;

	@Value( "${SECURE_SECRET}" )
	private String SECURE_SECRET; // secret key;

	@Value("${WALLET_PARTIAL_PAYMENT_RETURN_URL}")
	private String WALLET_PARTIAL_PAYMENT_RETURN_URL;


	//This method will get called if student opts to pay only by his debit or credit card//
	@RequestMapping(value="/payUsingHDFCGateway",method={RequestMethod.GET,RequestMethod.POST})
	public ModelAndView payUsingHDFCGateway(HttpServletRequest request, HttpServletResponse response, ModelMap model){

		if(!checkSession(request, response)){
			return new ModelAndView("login");
		}

		WalletDAO wDao = (WalletDAO)act.getBean("walletDao");
		String requestId = (String)request.getParameter("requestId");
		TransactionBean transactionBean = (TransactionBean)wDao.getApiParameters(requestId);
		fillParametersInModelFromTransactionBean(model,transactionBean);

		return new ModelAndView(new RedirectView("pay"), model);
	}
	 
	 
	@RequestMapping(value="/makePaymentUsingNMWallet",method={RequestMethod.GET,RequestMethod.POST})
	public ModelAndView makePaymentUsingNMWallet(HttpServletRequest request, HttpServletResponse response){

		if(!checkSession(request, response)){
			return new ModelAndView("login");
		}

		ModelAndView modelnView = new ModelAndView("wallet/redirectToPaymentRequestor");
		
		String requestId = "";
		if(request.getParameter("requestId") == null || "".equals(request.getParameter("requestId"))){
			requestId = request.getParameter("MerchantRefNo");
		}else{
			requestId = request.getParameter("requestId");
		}
		
		TransactionBean transactionBean = (TransactionBean)walletDao.getApiParameters(requestId);
		WalletBean walletRecord = (WalletBean)walletDao.getWalletRecord(transactionBean.getSapid());
		populateWalletBeanWithTransactionBean(walletRecord,transactionBean);
		try{
			//Updated Wallet balance .//
			updateWalletBalance(walletRecord,WalletBean.DEBIT);
			
			long walletTransactionId = walletDao.updateWMoneyLoadAndInsertWTransactionAndUpdateWBalance(walletRecord, false);
			
			//Map the model with transaction bean parameters and post it to Return URL of original payment request.
			fillResponseParametersInModelWithTransactionBean(modelnView,transactionBean,walletTransactionId);
			
			return modelnView;
		}catch(Exception e){
			e.printStackTrace();
			//Common method to redirect to the back url with error response code.//	
			return sendToErrorPage(transactionBean.getReturnUrl(), "Technical Issue :" + e.getMessage(),"1");
		}
	}

	
	 
	
	@RequestMapping(value = "/redirectToPaymentRequestor", method = {RequestMethod.GET, RequestMethod.POST})
	public ModelAndView redirectToPaymentRequestor(HttpServletRequest request, HttpServletResponse respnse, ModelMap model) {
		if(!checkSession(request, respnse)){
			return new ModelAndView("studentPortalRediret");
		}
		return new ModelAndView("wallet/redirectToPaymentRequestor");
	}
	
	@RequestMapping(value="/sendToGatewayForShortfallPayment",method={RequestMethod.GET,RequestMethod.POST})
	public ModelAndView sendToGatewayForShortfallPayment(HttpServletRequest request, HttpServletResponse response,ModelMap model){

		if(!checkSession(request, response)){
			return new ModelAndView("login");
		}

		String requestId = (String)request.getParameter("requestId");
		PortalDao pDao = (PortalDao)act.getBean("portalDAO");
		TransactionBean transactionBean = (TransactionBean)walletDao.getApiParameters(requestId);
		WalletBean walletRecord = (WalletBean)walletDao.getWalletRecord(transactionBean.getSapid());
		double balanceAmountToBePaid = checkIfAmountIsGreaterThanWalletBalanceAndReturnDifference(walletRecord,transactionBean);
		
		populateWalletBeanWithTransactionBean(walletRecord,transactionBean);
		
		try{
			//Make entry in Wallet Money load table//
			StudentBean student = (StudentBean)pDao.getSingleStudentsData(transactionBean.getSapid());
			
			walletRecord.setAmount(balanceAmountToBePaid+"");
			fillPaymentParametersInMapWithWalletParameters(model, student, walletRecord,WALLET_PARTIAL_PAYMENT_RETURN_URL ,requestId);
			//Insert wallet money load transaction//
			walletDao.insertWMoneyLoad(walletRecord);
			return new ModelAndView(new RedirectView("pay"), model);
		}catch(Exception e){
			e.printStackTrace();
			return sendToErrorPage(transactionBean.getReturnUrl(), "Technical Issue :" + e.getMessage(),"1");
		}

	}


	@RequestMapping(value="/payResponseForWalletPartialPayment",method={RequestMethod.GET,RequestMethod.POST})
	public ModelAndView payResponseForWalletPartialPayment(HttpServletRequest request,HttpServletResponse response,ModelMap model, RedirectAttributes ra){
		if (!checkSession(request, response)) {
			return new ModelAndView("login");
		}
		//String trackId = (String) request.getParameter("requestId");
		
		
		String trackId =  request.getParameter("MerchantRefNo");
		String amount = request.getParameter("Amount");
		
		boolean isSuccessful = isTransactionSuccessful(request);
		boolean isHashMatching = isHashMatching(request);
		boolean isAmountMatching = isAmountMatching(request, amount);
		boolean isTrackIdMatching = isTrackIdMatching(request, trackId);
		String errorMessage = null;

		if (!isSuccessful) {
			errorMessage = "Error in processing payment. Error: " + request.getParameter("Error") + " Code: "
					+ request.getParameter("ResponseCode");
		}

		if (!isHashMatching) {
			errorMessage = "Error in processing payment. Error: Hashvalue not matching. Tampering in response found. Track ID: "
					+ trackId;
		}

		if (!isAmountMatching) {
			errorMessage = "Error in processing payment. Error: Fees " + amount + " not matching with amount paid "
					+ request.getParameter("Amount");
		}

		if (!isTrackIdMatching) {
			errorMessage = "Error in processing payment. Error: Track ID: " + trackId
					+ " not matching with Merchant Ref No. " + request.getParameter("MerchantRefNo");
		}
		if(errorMessage!=null){
			setError(request,errorMessage);
			return payOptions(request,response);
		}else{
			try{
				saveDetailsInWalletTransactionTable(request,response);
				
				return makePaymentUsingNMWallet(request,response);
			}catch(Exception e){
				e.printStackTrace();
				setError(request,errorMessage);
				return payOptions(request,response);
			}
			
			
		}
	}
	
	private void saveDetailsInWalletTransactionTable(
			HttpServletRequest request, HttpServletResponse response) throws Exception{

			String trackId = (String)request.getParameter("requestId");
			String userId = (String)request.getSession().getAttribute("userId");
			WalletBean bean = (WalletBean)walletDao.getWalletRecord(userId);
			
		
			
			bean.setTrackId(trackId);
			bean.setTid(trackId);
			bean.setResponseMessage(request.getParameter("ResponseMessage"));
			bean.setTranStatus(WalletBean.TRAN_STATUS_SUCCESSFUL);
			bean.setTransactionID(request.getParameter("TransactionID"));
			bean.setRequestID(request.getParameter("RequestID"));
			bean.setMerchantRefNo(request.getParameter("MerchantRefNo"));
			bean.setSecureHash(request.getParameter("SecureHash"));
			bean.setRespAmount(request.getParameter("Amount"));
			bean.setAmount(request.getParameter("Amount"));
			bean.setRespTranDateTime(request.getParameter("DateCreated"));
			bean.setTranDateTime(request.getParameter("DateCreated"));
			bean.setResponseCode(request.getParameter("ResponseCode"));
			bean.setRespPaymentMethod(request.getParameter("PaymentMethod"));
			bean.setIsFlagged(request.getParameter("IsFlagged"));
			bean.setPaymentID(request.getParameter("PaymentID"));
			bean.setError(request.getParameter("Error"));
			bean.setDescription(request.getParameter("Description"));
			bean.setWalletId(bean.getId()+"");
			bean.setUserId(userId);

			updateWalletBalance(bean,WalletBean.CREDIT);
			
			walletDao.updateWMoneyLoadAndInsertWTransactionAndUpdateWBalance(bean, true);

			
		


	}

	//This method handles the pay options and  creates wallet if he does not have one.
	@RequestMapping(value="/payOptions",method={RequestMethod.GET,RequestMethod.POST})
	public ModelAndView payOptions(HttpServletRequest request,HttpServletResponse response){

		if(!checkSession(request, response)){
			return new ModelAndView("login");
		}
		WalletDAO wDao = (WalletDAO)act.getBean("walletDao");


		ModelAndView modelNView = new ModelAndView("payOptions");

		String requestId = (String)request.getParameter("requestId");
		request.getSession().setAttribute("SECURE_SECRET", SECURE_SECRET);

		TransactionBean transactionBean = (TransactionBean)wDao.getApiParameters(requestId);
		String studentNumber = transactionBean.getSapid();
		WalletBean walletRecord = (WalletBean)wDao.getWalletRecord(studentNumber);
		//If wallet does not exist create new wallet//
		if(walletRecord == null){
			walletRecord = wDao.insertWalletRecordAndReturnRecord(studentNumber);

		}


		//This method checks if the wallet balance is lesser than the transaction amount and gives the additional amount to be paid.
		double balanceAmountToBePaid = checkIfAmountIsGreaterThanWalletBalanceAndReturnDifference(walletRecord,transactionBean);

		try{

			modelNView.addObject("walletRecord",walletRecord);
			modelNView.addObject("requestId",requestId);
			modelNView.addObject("balanceAmountToBePaid",balanceAmountToBePaid);
			modelNView.addObject("transactionAmount", transactionBean.getAmount());
			return modelNView;
		}catch(Exception e){
			e.printStackTrace();
			setError(request,e.getMessage());
			return modelNView;
		}
		}

	

	@RequestMapping(value="/cancelPayRequest",method={RequestMethod.GET,RequestMethod.POST})
	public ModelAndView cancelPayRequest(HttpServletRequest request, HttpServletResponse response, ModelMap model){

		String requestId = (String)request.getParameter("requestId");
		TransactionBean transactionBean = (TransactionBean)walletDao.getApiParameters(requestId);

		return sendToErrorPage(transactionBean.getReturnUrl()+"?PaymentMethod=Wallet", "Transaction Cancelled by User ", "1");
	}
	public void populateWalletBeanWithTransactionBean(WalletBean walletRecord,TransactionBean transactionBean){
		walletRecord.setSapid(transactionBean.getSapid());
		walletRecord.setTid(transactionBean.getRequestId());
		walletRecord.setTrackId(transactionBean.getReferenceNo());
		walletRecord.setAmount(transactionBean.getAmount());
		walletRecord.setDescription(transactionBean.getDescription());
		walletRecord.setMerchantRefNo(transactionBean.getReferenceNo());
		walletRecord.setRespAmount(transactionBean.getAmount());
		walletRecord.setWalletId(walletRecord.getId()+"");
		walletRecord.setUserId(transactionBean.getSapid());
		walletRecord.setPaymentID(transactionBean.getRequestId());
		walletRecord.setTid(transactionBean.getRequestId());
		walletRecord.setTransactionID(transactionBean.getRequestId());
		walletRecord.setRequestID(transactionBean.getRequestId());
		walletRecord.setPaymentID(transactionBean.getRequestId());
		walletRecord.setRequestID(transactionBean.getRequestId());
		walletRecord.setTransactionID(transactionBean.getRequestId());
		walletRecord.setTrackId(transactionBean.getReferenceNo());
	}
	public void fillResponseParametersInModelWithTransactionBean(ModelAndView model,TransactionBean transactionBean,long walletTransactionId){
		model.addObject("Error", null);
		model.addObject("ResponseCode", "0");
		model.addObject("RequestId",transactionBean.getRequestId());
		model.addObject("returnURL",transactionBean.getReturnUrl());
		model.addObject("PaymentMethod","Wallet");
		
		
		model.addObject("TransactionID", walletTransactionId+"");
		model.addObject("MerchantRefNo", transactionBean.getReferenceNo());
		model.addObject("Amount", transactionBean.getAmount());
		model.addObject("PaymentID", transactionBean.getRequestId());
		model.addObject("Description", transactionBean.getDescription());
		model.addObject("apiRequestId",transactionBean.getRequestId());
	}

	public ModelAndView sendToErrorPage(String returnURL,String errorMessage,String responseCode){
		ModelAndView modelnView = new ModelAndView("wallet/redirectToPaymentRequestor");
		
		modelnView.addObject("ResponseCode", responseCode);
		modelnView.addObject("Error", errorMessage);
		modelnView.addObject("returnURL",returnURL);

		return modelnView;

	}
	public void fillParametersInModelFromTransactionBean(ModelMap model,TransactionBean transactionBean){
		model.addAttribute("channel", "10");
		model.addAttribute("account_id", transactionBean.getAccountId());
		model.addAttribute("reference_no", transactionBean.getReferenceNo());
		model.addAttribute("amount", transactionBean.getAmount());
		model.addAttribute("mode", "LIVE");
		model.addAttribute("currency", "INR");
		model.addAttribute("currency_code", "INR");
		model.addAttribute("description", transactionBean.getDescription());// This
		// should
		// be
		// used
		// in
		// response
		model.addAttribute("return_url", transactionBean.getReturnUrl());
		model.addAttribute("name", transactionBean.getName());
		model.addAttribute("address", transactionBean.getAddress());
		model.addAttribute("city", transactionBean.getCity());
		model.addAttribute("country", "IND");
		model.addAttribute("postal_code", transactionBean.getPostalCode());
		model.addAttribute("phone", transactionBean.getPhone());
		model.addAttribute("email", transactionBean.getEmail());
		model.addAttribute("algo", "MD5");
		model.addAttribute("V3URL", V3URL);
		model.addAttribute("studentNumber", transactionBean.getSapid());
	}
}*/