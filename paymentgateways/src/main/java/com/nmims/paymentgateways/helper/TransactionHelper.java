package com.nmims.paymentgateways.helper;

import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.ModelAndView;

import com.nmims.paymentgateways.bean.TransactionStatusBean;
import com.nmims.paymentgateways.bean.TransactionsBean;

@Component
public class TransactionHelper {

	private final String PAYTM = "paytm";
	private final String PAYU = "payu";
	private final String CCAVENUE = "ccavenue";
	private final String RAZORPAY = "razorpay";
	private final String BILLDESK = "billdesk";

	
	@Value("${RETURN_URL}")
	private String RETURN_URL;

	@Autowired
	private PaytmHelper paytmHelper;

	@Autowired
	private PayuHelper payuHelper;

	@Autowired
	private RazorpayHelper razorpayHelper;
	
	@Autowired
	private BillDeskHelper billDeskHelper;
	
	@Autowired
	private CCAvenueHelper ccAvenueHelper;
	
	public static ExceptionHelper exceptionHelper = new ExceptionHelper();
	
	private static final Logger paymentSchedulerLogger = LoggerFactory.getLogger("payment_scheduler");
	
	
	public String generateCheckSum(TransactionsBean transactionsBean) {

		if (PAYTM.equalsIgnoreCase(transactionsBean.getPayment_option())) {
			// paytm checksum generate
			return paytmHelper.generateCheckSum(transactionsBean, RETURN_URL);
		} else if (PAYU.equalsIgnoreCase(transactionsBean.getPayment_option())) {
			// payu checksum generate
			return payuHelper.generateCheckSum(transactionsBean, RETURN_URL);
		} else if (BILLDESK.equalsIgnoreCase(transactionsBean.getPayment_option())) {
			//billdesk checksum generate
			return billDeskHelper.generateCheckSum(transactionsBean, RETURN_URL);
		}
		else if(CCAVENUE.equalsIgnoreCase(transactionsBean.getPayment_option())){
			//ccAvenue checksum generate
			return ccAvenueHelper.generateCheckSum(transactionsBean, RETURN_URL);
		}
		else {
			// exception invalid payment option
			exceptionHelper.createInfoLog("TransactionHelper: Invalid payment gateway found while generateCheckSum.");
			return "Invalid payment gateway found while generateCheckSum";
		}
	}

	public ModelAndView createModelAndViewData(TransactionsBean transactionsBean) {

		if (PAYTM.equalsIgnoreCase(transactionsBean.getPayment_option())) {
			// paytm view model generate
			return paytmHelper.createModelAndViewData(transactionsBean, RETURN_URL);
		} else if (PAYU.equalsIgnoreCase(transactionsBean.getPayment_option())) {
			// payu view model generate
			return payuHelper.createModelAndViewData(transactionsBean, RETURN_URL);
		}else if(CCAVENUE.equalsIgnoreCase(transactionsBean.getPayment_option())) {		
			return ccAvenueHelper.createModelAndViewData(transactionsBean, RETURN_URL);
		

		} else if (RAZORPAY.equalsIgnoreCase(transactionsBean.getPayment_option())) {
			// razorpay model generate
			return razorpayHelper.createModelAndViewData(transactionsBean, RETURN_URL);
		} else if(BILLDESK.equalsIgnoreCase(transactionsBean.getPayment_option())) {
			//billdesk view model generate
			return billDeskHelper.createModelAndViewData(transactionsBean, RETURN_URL);
		} else {
			//exception invalid payment option
			exceptionHelper.createInfoLog("TransactionHelper: Invalid payment gateway found while createModelAndViewData.");
			return null;
		}
	}

	public String checkErrorInTransaction(HttpServletRequest request) {
		TransactionsBean transactionsBean_session = (TransactionsBean) request.getSession()
				.getAttribute("transactionsBean");
		if (PAYTM.equalsIgnoreCase(transactionsBean_session.getPayment_option())) {
			// paytm checkErrorInTransaction
			return paytmHelper.checkErrorInTransaction(request);
		} else if (PAYU.equalsIgnoreCase(transactionsBean_session.getPayment_option())) {
			// payu checkErrorInTransaction
			return payuHelper.checkErrorInTransaction(request);
		} else if (RAZORPAY.equalsIgnoreCase(transactionsBean_session.getPayment_option())) {
			// razorpay checkErrorInTransaction
			return razorpayHelper.checkErrorInTransaction(request);
		}  else if (BILLDESK.equalsIgnoreCase(transactionsBean_session.getPayment_option())) {
			//billdesk checkErrorInTransaction
			return billDeskHelper.checkErrorInTransaction(request);
		}
			else if(CCAVENUE.equalsIgnoreCase(transactionsBean_session.getPayment_option())) {
				//ccavenue checkErrorInTransaction
				return ccAvenueHelper.checkErrorInTransaction(request);
		}
		else {
			//exception invalid payment option
			exceptionHelper.createInfoLog("TransactionHelper: Invalid payment gateway found while checking Error in Transaction.");
			return "Invalid payment gateway found";
		}
	}

	public TransactionsBean createResponseBean(HttpServletRequest request) {
		TransactionsBean transactionsBean_session = (TransactionsBean) request.getSession().getAttribute("transactionsBean");
		if(PAYTM.equalsIgnoreCase(transactionsBean_session.getPayment_option())) {
			//paytm checkErrorInTransaction
			return paytmHelper.createResponseBean(request);
		}
		else if(PAYU.equalsIgnoreCase(transactionsBean_session.getPayment_option())) {
			//payu checkErrorInTransaction
			return payuHelper.createResponseBean(request);
		} else if(BILLDESK.equalsIgnoreCase(transactionsBean_session.getPayment_option())) {
			//billdesk creating response bean
			return billDeskHelper.createResponseBean(request);
		}
		else if(RAZORPAY.equalsIgnoreCase(transactionsBean_session.getPayment_option())) {
			//razorpay create response bean
			return razorpayHelper.createResponseBean(request);
		}
		else if(CCAVENUE.equalsIgnoreCase(transactionsBean_session.getPayment_option())) {
			//payu checkErrorInTransaction
			return ccAvenueHelper.createResponseBean(request);
		}
		else {
			//exception invalid payment option
			exceptionHelper.createInfoLog("TransactionHelper: Invalid payment gateway found while checking Error in Transaction.");
			return null;
		}
	}
	public TransactionsBean getTransactionStatus(TransactionsBean transactionsBean,List<String> paymentStatus) {
		paymentSchedulerLogger.info("TransactionHelper.getTransactionStatus()");
		if(PAYTM.equalsIgnoreCase(transactionsBean.getPayment_option())) {
			return paytmHelper.getTransactionStatus(transactionsBean, paymentStatus);
		}
		else if(CCAVENUE.equalsIgnoreCase(transactionsBean.getPayment_option())) {
			return ccAvenueHelper.getTransactionStatus(transactionsBean, paymentStatus);
		}
		else if(RAZORPAY.equalsIgnoreCase(transactionsBean.getPayment_option())) {
			return razorpayHelper.getTransactionStatus(transactionsBean, paymentStatus);
		
		} else if(BILLDESK.equalsIgnoreCase(transactionsBean.getPayment_option())) {
			return billDeskHelper.getTransactionStatus(transactionsBean, paymentStatus);
		} else {
			paymentSchedulerLogger.info("Invalid payment option found for track_id:"+transactionsBean.getTrack_id());
			transactionsBean.setTransaction_status(paymentStatus.get(0));
			transactionsBean.setError("Invalid payment option found");
			return transactionsBean;
		}
	}

	public TransactionStatusBean initiateRefund(String track_id, String transaction_id,String amount,TransactionsBean transaction) {
		TransactionStatusBean bean=new TransactionStatusBean();
		try {
			if ("razorpay".equalsIgnoreCase(transaction.getPayment_option())) {
				return razorpayHelper.initiateRefund(track_id, transaction_id, amount);
			}
			else if("ccavenue".equalsIgnoreCase(transaction.getPayment_option())) {
				return ccAvenueHelper.refundIntitate(track_id, transaction_id, amount);
			}

		}
		catch(Exception e) {
			paymentSchedulerLogger.info("Error in refund"+ e.getMessage());
			return bean;
		}
		return bean;
	}
	
	public TransactionStatusBean getRefundStatus(String merchantRefNo,String refundId,String payment_option) {	
		TransactionStatusBean bean=new TransactionStatusBean();
		if ("razorpay".equalsIgnoreCase(payment_option)) {
			return razorpayHelper.getRefundStatus(merchantRefNo, refundId);
		} else if ("ccavenue".equalsIgnoreCase(payment_option)) {
			return ccAvenueHelper.getRefundStatus(merchantRefNo, refundId);
		} else {
			return bean;
		}
	}

	public Map<String,String> getTransactionStatusByTrackID(String trackd) {
		// TODO Auto-generated method stub
		return ccAvenueHelper.getTransactionStatusByTrackID(trackd);
	}
}
