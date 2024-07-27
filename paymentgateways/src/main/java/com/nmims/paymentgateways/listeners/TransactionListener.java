package com.nmims.paymentgateways.listeners;

import java.util.ArrayList;

import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.google.gson.JsonObject;
import com.nmims.paymentgateways.bean.TransactionStatusBean;
import com.nmims.paymentgateways.bean.TransactionsBean;
import com.nmims.paymentgateways.dao.TransactionDAO;
import com.nmims.paymentgateways.helper.ExceptionHelper;
import com.nmims.paymentgateways.helper.PaytmHelper;
import com.nmims.paymentgateways.helper.PayuHelper;
import com.nmims.paymentgateways.helper.RazorpayHelper;

@Component
public class TransactionListener {
	
	@Value( "${SERVER}" )
	private String SERVER;

	@Value( "${ENVIRONMENT}" )
	private String ENVIRONMENT;
	
	@Autowired
	private TransactionDAO transactionDAO;
	
	@Autowired
	private PaytmHelper paytmHelper;
	
	@Autowired
	private PayuHelper payuHelper;
	
	@Autowired
	private RazorpayHelper razorpayHelper;
	
	public static ExceptionHelper exceptionHelper = new ExceptionHelper();
	
	private final String PAYTM = "paytm";
	private final String PAYU = "payu";
	private final String RAZORPAY = "razorpay";
	private final String UPDATEDBY = "Auto batch job";
	
	/*public void synchronizePendingTransactions() {
		
		if(!"tomcat4".equalsIgnoreCase(SERVER) || !"PROD".equalsIgnoreCase(ENVIRONMENT)){
			System.out.println("Not running synchronizePendingTransactions scheduler since this is not tomcat4. This is "+SERVER);
			return;
		}
		
		try {
			
			ArrayList<TransactionsBean> successList = new ArrayList<TransactionsBean>();
			ArrayList<TransactionsBean> failedList = new ArrayList<TransactionsBean>();
			
			ArrayList<TransactionsBean> initiatedList = transactionDAO.getInitiatedPaymentList();
			
			if(initiatedList != null && initiatedList.size() > 0) {
				
				for(TransactionsBean transactionsBean : initiatedList) {
					try {
						transactionsBean.setUpdated_by(UPDATEDBY);
						if(PAYTM.equalsIgnoreCase(transactionsBean.getPayment_option())) {
							
							JsonObject jsonObj = paytmHelper.getTransactionStatus(transactionsBean.getTrack_id());
							String STATUS = jsonObj.get("STATUS").getAsString();
							if("TXN_SUCCESS".equalsIgnoreCase(STATUS)) {
								
								if(jsonObj.get("BANKNAME") != null) {
									transactionsBean.setBank_name(jsonObj.get("BANKNAME").getAsString());
								}
								if(jsonObj.get("BANKTXNID") != null) {
									transactionsBean.setPayment_id(jsonObj.get("BANKTXNID").getAsString());
								}
								if(jsonObj.get("PAYMENTMODE") != null) {
									transactionsBean.setResponse_payment_method(jsonObj.get("PAYMENTMODE").getAsString());
								}
								transactionsBean.setMerchant_ref_no(jsonObj.get("ORDERID").getAsString());
								transactionsBean.setResponse_message(jsonObj.get("RESPMSG").getAsString());
								transactionsBean.setResponse_code(jsonObj.get("RESPCODE").getAsString());
								transactionsBean.setResponse_amount(jsonObj.get("TXNAMOUNT").getAsString());
								transactionsBean.setTransaction_id(jsonObj.get("TXNID").getAsString());
								successList.add(transactionsBean);
								//transactionDAO.markAsSuccessTransaction(transactionsBean);
								
							}
							else if("TXN_FAILURE".equalsIgnoreCase(STATUS)) {
								if(jsonObj.get("RESPCODE") != null) {
									transactionsBean.setResponse_code(jsonObj.get("RESPCODE").getAsString());
								}
								if(jsonObj.get("RESPMSG") != null) {
									transactionsBean.setResponse_message(jsonObj.get("RESPMSG").getAsString());
									transactionsBean.setError(jsonObj.get("RESPMSG").getAsString());
								}
								//transactionDAO.markAsFailedTransaction(transactionsBean);
								failedList.add(transactionsBean);
							}
							
						}
						else if(PAYU.equalsIgnoreCase(transactionsBean.getPayment_option())) {
							
							JsonObject jsonObj = payuHelper.getTransactionStatus(transactionsBean.getTrack_id());
							String STATUS = jsonObj.get("status").getAsString();
							try {
								JsonObject transaction_detail = jsonObj.get("transaction_details").getAsJsonObject();
								transaction_detail = transaction_detail.get(transactionsBean.getTrack_id()).getAsJsonObject();
								String MSG = transaction_detail.get("status").getAsString();
								if("1".equalsIgnoreCase(STATUS) && "success".equalsIgnoreCase(MSG)) {
									
									if(jsonObj.get("bankcode") != null) {
										transactionsBean.setBank_name(jsonObj.get("bankcode").getAsString());
									}
									if(jsonObj.get("bank_ref_num") != null) {
										transactionsBean.setPayment_id(jsonObj.get("bank_ref_num").getAsString());
									}
									if(jsonObj.get("mode") != null) {
										transactionsBean.setResponse_payment_method(jsonObj.get("mode").getAsString());
									}
									if(jsonObj.get("error") != null) {
										transactionsBean.setResponse_code(jsonObj.get("error").getAsString());
									}
									if(jsonObj.get("amt") != null) {
										transactionsBean.setResponse_amount(jsonObj.get("amt").getAsString());
									}
									transactionsBean.setMerchant_ref_no(jsonObj.get("txnid").getAsString());
									transactionsBean.setResponse_message(jsonObj.get("status").getAsString());
									transactionsBean.setTransaction_id(jsonObj.get("mihpayid").getAsString());
									//transactionDAO.markAsSuccessTransaction(transactionsBean);
									successList.add(transactionsBean);
									
								}
								else if("0".equalsIgnoreCase(STATUS) || ("1".equalsIgnoreCase(STATUS) && "failure".equalsIgnoreCase(MSG))) {
									if(jsonObj.get("error_code") != null) {
										transactionsBean.setResponse_code(jsonObj.get("error_code").getAsString());
									}
									if(jsonObj.get("field9") != null) {
										transactionsBean.setResponse_message(jsonObj.get("field9").getAsString());
										transactionsBean.setError(jsonObj.get("field9").getAsString());
									}
									//transactionDAO.markAsFailedTransaction(transactionsBean);
									failedList.add(transactionsBean);
								}
							}
							catch (Exception e) {
								// TODO: handle exception
								if("0".equalsIgnoreCase(STATUS)) {
									if(jsonObj.get("msg") != null) {
										transactionsBean.setResponse_message(jsonObj.get("msg").getAsString());
										transactionsBean.setError(jsonObj.get("msg").getAsString());
									}
									else {
										transactionsBean.setError("Invalid response from payu");
									}
									//transactionDAO.markAsFailedTransaction(transactionsBean);
									failedList.add(transactionsBean);
								}
							}
						} else if(RAZORPAY.equalsIgnoreCase(transactionsBean.getPayment_option())) {
							JsonObject payment= razorpayHelper.getTransactionStatus(transactionsBean.getTrack_id());
							if(payment.has("error")) {
//								transactionsBean.setError(payment.get("error").getAsString());
//								failedList.add(transactionsBean);
							} else if("captured".equalsIgnoreCase(payment.get("status").getAsString())) {
								if(payment.get("bank") != null)
									transactionsBean.setBank_name(payment.get("bank").getAsString());
								transactionsBean.setPayment_id(payment.get("id").getAsString());
								transactionsBean.setResponse_method(payment.get("method").getAsString());
								transactionsBean.setMerchant_ref_no(transactionsBean.getTrack_id());
								transactionsBean.setResponse_message(payment.get("orderStatus").getAsString());
								transactionsBean.setResponse_amount(String.valueOf(payment.get("amount").getAsInt() / 100));
								transactionsBean.setTransaction_id(payment.get("order_id").getAsString());
								successList.add(transactionsBean);
							} else if("failed".equalsIgnoreCase(payment.get("status").getAsString())) {
								transactionsBean.setResponse_message(payment.get("error_description").getAsString());
								transactionsBean.setError(payment.get("error_source").getAsString() +  " - "+payment.get("error_step").getAsString());
								failedList.add(transactionsBean);
							}
						}
						
					}
					catch (Exception e) {
						// TODO: handle exception
						exceptionHelper.createLog(e);
					}
				}
				
				//success update
				if(successList.size() > 0) {
					for (TransactionsBean tmp_transactionBean : successList) {
						try {
							transactionDAO.markAsSuccessTransaction(tmp_transactionBean);
							transactionDAO.markSuccessInPortal(tmp_transactionBean.getTrack_id());
						}
						catch (Exception e) {
							// TODO: handle exception
							exceptionHelper.createLog(e);
						}
					}
				}
				
				//failer update
				if(failedList.size() > 0) {
					for (TransactionsBean tmp_transactionBean : failedList) {
						try {
							transactionDAO.markAsFailedTransaction(tmp_transactionBean);
							transactionDAO.markFailedInPortal(tmp_transactionBean.getTrack_id());
						}
						catch (Exception e) {
							// TODO: handle exception
							exceptionHelper.createLog(e);
						}
					}
				}
				
			}
			
		}
		catch (Exception e) {
			// TODO: handle exception
			exceptionHelper.createLog(e);
		}
		
	}*/
	
}
