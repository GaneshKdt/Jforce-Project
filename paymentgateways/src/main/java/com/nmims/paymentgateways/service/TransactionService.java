package com.nmims.paymentgateways.service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.servlet.ModelAndView;
import com.nmims.paymentgateways.bean.PaymentOptionsBean;
import com.nmims.paymentgateways.bean.TransactionStatusBean;
import com.nmims.paymentgateways.bean.TransactionsBean;
import com.nmims.paymentgateways.dao.PaymentOptionsDAO;
import com.nmims.paymentgateways.dao.TransactionDAO;
import com.nmims.paymentgateways.helper.RazorpayHelper;
import com.nmims.paymentgateways.helper.TransactionHelper;

@Component
public class TransactionService {

	@Autowired
	private TransactionDAO transactionDAO;

	@Autowired
	private TransactionHelper transactionHelper;
	
	@Value("${SERVER_PATH}")
	private String SERVER_PATH;

	@Autowired
	private PaymentOptionsDAO paymentOptionsDAO;

	@Autowired
	private RazorpayHelper razorpayHelper;
	
	private final List<String> paymentStatus = new ArrayList<String>(Arrays.asList("Payment Initiated","Payment Successfull","Payment Failed","Payment Pending")); 
	
	private static final Logger payment_logs = LoggerFactory.getLogger("payment_logs");
	private static final Logger paymentSchedulerLogger = LoggerFactory.getLogger("payment_scheduler");
	
	public ModelAndView selectGatewayStageTransaction(TransactionsBean transactionsBean, HttpServletRequest request) {
		request.getSession().setAttribute("track_id", transactionsBean.getTrack_id());
	
		ArrayList<PaymentOptionsBean> paymentOptionsList = paymentOptionsDAO.getActivePaymentGateway();

		// removes payment option from list if bean is from exam booking
		// and exam booking is not set active in the table
		
		
		if ("Exam_Booking".equalsIgnoreCase(transactionsBean.getType()))
			paymentOptionsList.removeIf(paymentOption -> !"Y".equalsIgnoreCase(paymentOption.getExambookingActive()));

		if("USD".equalsIgnoreCase(transactionsBean.getCurrency()))
			paymentOptionsList.removeIf(k -> !"ccavenue".equalsIgnoreCase(k.getName()));
		else
			paymentOptionsList.removeIf(k -> "ccavenue".equalsIgnoreCase(k.getName()));
		
		System.out.println(paymentOptionsList.toString());
		ModelAndView mv = new ModelAndView("payment");
		mv.addObject("payment_options", paymentOptionsList);
		mv.addObject("track_id", transactionsBean.getTrack_id());
		mv.addObject("response_method", transactionsBean.getResponse_method());
		mv.addObject("sapid", transactionsBean.getSapid());
		mv.addObject("type", transactionsBean.getType());
		mv.addObject("amount", transactionsBean.getAmount());
		mv.addObject("description", transactionsBean.getDescription());
		// mv.addObject("payment_option", transactionsBean.getPayment_option());
		mv.addObject("source", transactionsBean.getSource());
		mv.addObject("portal_return_url", transactionsBean.getPortal_return_url());
		mv.addObject("created_by", transactionsBean.getCreated_by());
		mv.addObject("updated_by", transactionsBean.getUpdated_by());
		mv.addObject("mobile", transactionsBean.getMobile());
		mv.addObject("email_id", transactionsBean.getEmail_id());
		mv.addObject("first_name", transactionsBean.getFirst_name());
		return mv;
	}

	public ModelAndView processTransaction(TransactionsBean transactionsBean, HttpServletRequest request) {
		payment_logs.info("processing payment for " + transactionsBean);
		// session value set
		// request.getSession().setAttribute("trackId", transactionsBean.getTrack_id());
		// request.getSession().setAttribute("amount", transactionsBean.getAmount());
		// request.getSession().setAttribute("paymentOption",
		// transactionsBean.getPayment_option());
		// end of session value set
		String check_result = null;
		
		//Checking the student is international or local
		// Since razorpay checks signature after transaction is successful, we'll skip
		// generation
		// and create order instead if payment option is razorpay
		if (!"razorpay".equalsIgnoreCase(transactionsBean.getPayment_option())) {
			check_result = transactionHelper.generateCheckSum(transactionsBean);
		} else {
			Map<String, String> orderMap = razorpayHelper.createOrder(transactionsBean.getAmount(),
					transactionsBean.getTrack_id());
			if (!orderMap.containsKey("error")) {
				transactionsBean.setTransaction_id(orderMap.get("orderId"));
				check_result = "true";
			} else
				check_result = orderMap.get("error");
		}
		Float amount=Float.parseFloat(transactionsBean.getAmount());
		transactionsBean.setAmount(amount.toString());
		request.getSession().setAttribute("transactionsBean", transactionsBean);
		if (!"true".equalsIgnoreCase(check_result)) {
			// error while generating checksum
			transactionsBean.setTransaction_status(paymentStatus.get(2));
			transactionsBean.setError(check_result);
			return response(transactionsBean);
			// return new ModelAndView("redirect:" + transactionsBean.getPortal_return_url()
			// + "?trackId="+ transactionsBean.getTrack_id() +"&status=error&message=" +
			// check_result);
		}

		if (!transactionDAO.initiateTransaction(transactionsBean)) {
			// error while initiating payment record
			transactionsBean.setTransaction_status(paymentStatus.get(2));
			transactionsBean.setError("error while creating transaction recording");
			return response(transactionsBean);
			// return new ModelAndView("redirect:" + transactionsBean.getPortal_return_url()
			// + "?trackId="+ transactionsBean.getTrack_id() +"&status=error&message=error
			// while creating transaction recording");
		}

		ModelAndView paymentModelAndView = transactionHelper.createModelAndViewData(transactionsBean);
		if (paymentModelAndView == null) {
			// error while generating paymentModelAndView
			transactionsBean.setTransaction_status(paymentStatus.get(2));
			transactionsBean.setError("error while creating transaction model and view");
			return response(transactionsBean);
			// return new ModelAndView("redirect:" + transactionsBean.getPortal_return_url()
			// + "?trackId="+ transactionsBean.getTrack_id() +"&status=error&message=error
			// while creating transaction model and view");
		}
		return paymentModelAndView;

	}

	public ModelAndView responseTransaction(HttpServletRequest request) {
		
		TransactionsBean transactionsBean_session = (TransactionsBean) request.getSession()
				.getAttribute("transactionsBean");
		
		payment_logs.info("callback received for track id " + transactionsBean_session.getTrack_id());

		ModelAndView mv = new ModelAndView("payment_response");
		mv.addObject("payment_response", transactionsBean_session);
		String checkSumStatus = transactionHelper.checkErrorInTransaction(request);
		if (!"true".equalsIgnoreCase(checkSumStatus)) {
			// error checksum verify
			transactionsBean_session.setTransaction_status(paymentStatus.get(2));
			transactionsBean_session.setError(checkSumStatus);
			// razopay api returns error payload when it gets failed
			if (request.getParameterMap().containsKey("error[description]")
					&& "razorpay".equalsIgnoreCase(transactionsBean_session.getPayment_option()))
				transactionDAO.markAsFailedTransaction(transactionsBean_session);
			return response(transactionsBean_session);
		}

		TransactionsBean transactionsBean_session2 = transactionHelper.createResponseBean(request);
		if (transactionsBean_session2 == null) {
			transactionsBean_session.setError("Error while creating responseBean");
			transactionsBean_session.setTransaction_status(paymentStatus.get(2));
			return response(transactionsBean_session);
			// return new ModelAndView("redirect:" + portal_return_url + "?trackId="+
			// trackId_session +"&status=error&message=Error while creating responseBean");
		} else if ("Payment Pending".equalsIgnoreCase(transactionsBean_session2.getTransaction_status())) {
			return response(transactionsBean_session2);
		}
		TransactionsBean callbackBean = new TransactionsBean();
		try {
			callbackBean = transactionDAO.getPaymentOptionByTrackId(transactionsBean_session.getTrack_id());
			if (!paymentStatus.get(1).equalsIgnoreCase(callbackBean.getTransaction_status())) {
				if (!transactionDAO.markAsSuccessTransaction(transactionsBean_session2)) {
					// update error
					transactionsBean_session.setError("Error while updating transaction recording");
					transactionsBean_session.setTransaction_status(paymentStatus.get(2));
					return response(transactionsBean_session);
					// return new ModelAndView("redirect:" + portal_return_url + "?trackId="+
					// trackId_session +"&status=error&message=error while updating transaction
					// recording");
				} else {
					payment_logs.info("Payment was already marked as successful by webhook for track id "
							+ transactionsBean_session.getTrack_id());
				}
			}
		} catch (Exception e) {
			payment_logs.info("error while trying to fetch payment details by track id : "
					+ transactionsBean_session.getTrack_id());
		}
		transactionsBean_session2.setTransaction_status(paymentStatus.get(1));
		transactionsBean_session2.setError(null); // set null if there is no error
		mv.addObject("payment_response", transactionsBean_session2);
		return mv;
	}

	//for razorpay cancel url
	public ModelAndView cancelPayment(HttpServletRequest request) {
		TransactionsBean bean = (TransactionsBean) request.getSession().getAttribute("transactionsBean");

		if (!transactionDAO.markAsCancelledTransaction(bean.getTrack_id())) {
			bean.setError("Error While marking Transcation as Cancelled");
			bean.setTransaction_status(paymentStatus.get(2));
			return response(bean);
		}
		
		ModelAndView mav = new ModelAndView("payment_response");
		bean.setError("Payment cancelled!");
		bean.setTransaction_status(paymentStatus.get(2));
		mav.addObject("payment_response", bean);
		return mav;
	}

	private ModelAndView response(TransactionsBean transactionsBean) {
		if ("POST".equalsIgnoreCase(transactionsBean.getResponse_method())) {
			ModelAndView mv_error = new ModelAndView("payment_response");
			mv_error.addObject("payment_response", transactionsBean);
			return mv_error;
		}
		return new ModelAndView("redirect:" + transactionsBean.getPortal_return_url() + "?trackId="
				+ transactionsBean.getTrack_id() + "&status=error&message=" + transactionsBean.getError());
	}

	public TransactionStatusBean getTransactionStatus(String parameter) {
		return razorpayHelper.getTransactionStatusByTrackId(parameter);
	}

	public TransactionStatusBean initiateRefund(String track_id, String transaction_id, String amount) {
		TransactionsBean bean=transactionDAO.getPaymentDetailsByTrackId(track_id);
		return transactionHelper.initiateRefund(track_id, transaction_id, amount,bean);
	}
		

	public TransactionStatusBean getRefundStatus(String merchantRefNo, String refundId){
		TransactionStatusBean bean=new TransactionStatusBean();
			try {
				TransactionsBean payment_option= transactionDAO.getPaymentOptionByTrackId(refundId);
				return transactionHelper.getRefundStatus(merchantRefNo, refundId,payment_option.getPayment_option());
			}catch (Exception e) {
				e.printStackTrace();
				return bean;
			}
			
				
			
			
	}

	public TransactionsBean getTransactionStatus(TransactionsBean transactionsBean) {
		paymentSchedulerLogger.info("TransactionService.getTransactionStatus() - START");
		TransactionsBean txnBean =null;
		
		try {
			paymentSchedulerLogger.info("Fetching transaction details for track_id: "+transactionsBean.getTrack_id());
			txnBean = transactionDAO.getPaymentOptionByTrackId(transactionsBean.getTrack_id());
		} catch (Exception e) {
			paymentSchedulerLogger.error("Error while fetching transaction details for track_id: "+transactionsBean.getTrack_id()+"\nError Message: "+e.getMessage());
			transactionsBean.setError("Error: "+e.getMessage());
			transactionsBean.setTransaction_status("Invalid Request");
			return transactionsBean;
		}
		
		if(StringUtils.isEmpty(transactionsBean.getPayment_option())) {
			transactionsBean.setPayment_option(txnBean.getPayment_option());
		}
		
		TransactionsBean transactionBean = transactionHelper.getTransactionStatus(transactionsBean, paymentStatus);
		paymentSchedulerLogger.info("Transaction Details:"+transactionBean);
		
		if(!paymentStatus.get(1).equalsIgnoreCase(txnBean.getTransaction_status())) {
			paymentSchedulerLogger.info("Updating transaction details in gateways for track_id:"+txnBean.getTrack_id());
			transactionDAO.updateTransactionDetails(transactionBean);
		}
		
		paymentSchedulerLogger.info("TransactionService.getTransactionStatus() - END");
		return transactionBean;
	}

//	public ResponseEntity logPayments(JsonNode payload) {
//		return webhookLoggingHelper.logPayments(payload);
//	}

	public Map<String,String> getTransactionStatusByTrackID(String Trackd) {
		// TODO Auto-generated method stub
		return transactionHelper.getTransactionStatusByTrackID(Trackd);
	}
	
	public String getRazorpayTransactionStatus(String track_id) {
		return razorpayHelper.getOrderByReceipt(track_id).toString();

	}
}
