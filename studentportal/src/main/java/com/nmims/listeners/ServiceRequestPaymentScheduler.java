package com.nmims.listeners;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.ServletConfig;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.context.ServletConfigAware;

import com.google.common.base.Throwables;
import com.google.gson.JsonObject;
import com.nmims.beans.AdhocPaymentStudentPortalBean;
import com.nmims.beans.RazorpayTransactionBean;
import com.nmims.beans.ServiceRequestStudentPortal;
import com.nmims.beans.StudentStudentPortalBean;
import com.nmims.beans.TransactionsBean;
import com.nmims.controllers.ServiceRequestController;
import com.nmims.daos.ServiceRequestDao;
import com.nmims.helpers.MailSender;
import com.nmims.helpers.PaymentHelper;
import com.nmims.helpers.XMLParser;

@Component("serviceRequestPaymentScheduler")
public class ServiceRequestPaymentScheduler implements ApplicationContextAware, ServletConfigAware{

	@Value( "${SECURE_SECRET}" )
	private String SECURE_SECRET; // secret key;
	@Value( "${ACCOUNT_ID}" )
	private String ACCOUNT_ID;

	@Value( "${SERVER}" )
	private String SERVER;

	@Value( "${ENVIRONMENT}" )
	private String ENVIRONMENT;
	
	@Value("${REFUND_STATUS_GATEWAY_URL}")
	private String REFUND_STATUS_GATEWAY_URL;
	
	@Value("${GATEWAY_TRANSACTION_STATUS}")
	private String GATEWAY_TRANSACTION_STATUS;
	
	@Value("${SERVER_PATH}")
	private String SERVER_PATH;
	
	private final String PAYMENT_SUCCESS = "Payment Successfull";
	private final String PAYMENT_FAILED = "Payment Failed";
	private final String PAYMENT_PENDING = "Payment Pending";
	private final String PAYMENT_INVALID_REQUEST = "Invalid Request";

	private static ApplicationContext act = null;
	private static ServletConfig sc = null;
	
	private String RAZORPAY = "razorpay";
	@Autowired
	private ServiceRequestDao serviceRequestDao;
	
	@Autowired
	private PaymentHelper paymentHelper;
	
	private RestTemplate restTempalte = new RestTemplate();
	
	private static final Logger razorpayLogger = LoggerFactory.getLogger("razorpay_payments");
	private static final Logger PAYMENTS_LOGGER = LoggerFactory.getLogger("gateway_payments");
		
	@Override
	public void setServletConfig(ServletConfig sc) {
		this.sc = sc;
	}

	@Override
	public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
		this.act = applicationContext;
	}

	public static ApplicationContext getApplicationContext() {
		return act;
	}


	@Scheduled(fixedDelay=60*60*1000)
	public void synchronizePendingRefundTransactions(){
		
		//System.out.println("Auto Conflict Correction Scheduler started running for SERVICE REQUEST");

		//System.out.println("Server = "+SERVER);
		//System.out.println("synchronizePendingTransactions scheduler for "+ENVIRONMENT); 

		if(!"tomcat4".equalsIgnoreCase(SERVER) || !"PROD".equalsIgnoreCase(ENVIRONMENT)){
			//System.out.println("Not running synchronizePendingTransactions scheduler since this is not tomcat4. This is "+SERVER);
			return;
		}
		
		//System.out.println("---=========>>>> refund schedular logic ");
		
		try {
			//ArrayList<AdhocPaymentBean> paidList =  new ArrayList<>();
			//ArrayList<AdhocPaymentBean> expiredList =  new ArrayList<>();
			ArrayList<AdhocPaymentStudentPortalBean> refundInitiatedList =  serviceRequestDao.getListOfInitiatedRefundTransaction();
			////System.out.println("refundPendingCount : " + refundInitiatedList.size());
			if(refundInitiatedList != null  && refundInitiatedList.size() > 0){
				for (AdhocPaymentStudentPortalBean refundPayment : refundInitiatedList) {
					try {
						if("paytm".equalsIgnoreCase(refundPayment.getPaymentOption())) {
							//System.out.println("==========>>>> inside paytm refunPayment");
							//PaymentHelper paymentHelper = new PaymentHelper();
							JsonObject jsonObj = paymentHelper.refundStatus(refundPayment.getMerchantRefNo(), refundPayment.getRefId());
							JsonObject body = jsonObj.get("body").getAsJsonObject();
							JsonObject resultInfo = body.get("resultInfo").getAsJsonObject();
							String STATUS =  resultInfo.get("resultStatus").getAsString();
							//System.out.println("Status : " + STATUS);
							//System.out.println("resultInfo : " + resultInfo);
							//System.out.println("body : " + body);
							if("TXN_SUCCESS".equalsIgnoreCase(STATUS)) {
								//System.out.println("refund Amount: " + refundPayment.getRefundAmount());
								//System.out.println("merchantRefNo. Amount: " + refundPayment.getMerchantRefNo());
								//System.out.println("refund Id : " + refundPayment.getId());
								if(serviceRequestDao.updatePendingRefundTransaction(refundPayment.getId(),"success") > 0) {
									StudentStudentPortalBean student = serviceRequestDao.getSingleStudentsData(refundPayment.getSapId());
									if("Exam Registration".equals(refundPayment.getFeesType())){
										serviceRequestDao.updateExamBookingAmountForRefund(refundPayment);
									}else if("Exam Registration MBA - WX".equals(refundPayment.getFeesType())){
										serviceRequestDao.updateMBAWXExamBookingAmountForRefund(refundPayment);
									}else if("Exam Registration MBA - X".equals(refundPayment.getFeesType())){
										serviceRequestDao.updateMBAXExamBookingAmountForRefund(refundPayment);
									}else if("Service Request".equals(refundPayment.getFeesType())){
										serviceRequestDao.updateServiceRequestAmountForRefund(refundPayment);
										ServiceRequestStudentPortal srBean = serviceRequestDao.getServiceRequestBySrId(refundPayment.getId());
										serviceRequestDao.insertServiceRequestStatusHistory(srBean, "Update");
									}else if("PCP Booking".equals(refundPayment.getFeesType())){
										serviceRequestDao.updatePCPBookingAmountForRefund(refundPayment);
									}else if("Assignment Fees".equals(refundPayment.getFeesType())){
										serviceRequestDao.updateAssignmentFeesAmountForRefund(refundPayment);
									}
									//System.out.println(refundPayment);
									//System.out.println(student);
									MailSender mailSender = (MailSender) act.getBean("mailer");
									
									mailSender.sendRefundEmail(student, refundPayment);
								}
							}else if("TXN_FAILURE".equals(STATUS)) {
								//System.out.println("fail transaction");
								serviceRequestDao.updatePendingRefundTransaction(refundPayment.getId(),"fail");
							}
						}
						else if("payu".equalsIgnoreCase(refundPayment.getPaymentOption())) {
							//System.out.println("==========>>>> inside payu refunPayment : " + refundPayment.getRefundId());
							//PaymentHelper paymentHelper = new PaymentHelper();
							JsonObject jsonObj = paymentHelper.payuRefundStatus(refundPayment.getRefundId());
							//System.out.println("=====>>>>>> payu refund response Data");
							//System.out.println(jsonObj.toString());
							String STATUS =  jsonObj.get("status").getAsString();
							JsonObject transaction_detail = jsonObj.get("transaction_details").getAsJsonObject();
							//For nested response object added twice
							transaction_detail = transaction_detail.get(refundPayment.getRefundId()).getAsJsonObject();
							transaction_detail = transaction_detail.get(refundPayment.getRefundId()).getAsJsonObject();
							String MSG = transaction_detail.get("status").getAsString();
							//System.out.println("Status : " + STATUS);
							//if(STATUS.equals("1") && MSG.equalsIgnoreCase("success")) {	//need to add success status
							if("1".equals(STATUS) && "success".equalsIgnoreCase(MSG)) {
								//System.out.println("refund Amount: " + refundPayment.getRefundAmount());
								//System.out.println("merchantRefNo. Amount: " + refundPayment.getMerchantRefNo());
								//System.out.println("refund Id : " + refundPayment.getId());
								if(serviceRequestDao.updatePendingRefundTransaction(refundPayment.getId(),"success") > 0) {
									StudentStudentPortalBean student = serviceRequestDao.getSingleStudentsData(refundPayment.getSapId());
									if("Exam Registration".equals(refundPayment.getFeesType())){
										serviceRequestDao.updateExamBookingAmountForRefund(refundPayment);
									}else if("Exam Registration MBA - WX".equals(refundPayment.getFeesType())){
										serviceRequestDao.updateMBAWXExamBookingAmountForRefund(refundPayment);
									}else if("Exam Registration MBA - X".equals(refundPayment.getFeesType())){
										serviceRequestDao.updateMBAXExamBookingAmountForRefund(refundPayment);
									}else if("Service Request".equals(refundPayment.getFeesType())){
										serviceRequestDao.updateServiceRequestAmountForRefund(refundPayment);
										
										ServiceRequestStudentPortal srBean = serviceRequestDao.getServiceRequestBySrId(refundPayment.getId());
										serviceRequestDao.insertServiceRequestStatusHistory(srBean, "Update");
									}else if("PCP Booking".equals(refundPayment.getFeesType())){
										serviceRequestDao.updatePCPBookingAmountForRefund(refundPayment);
									}else if("Assignment Fees".equals(refundPayment.getFeesType())){
										serviceRequestDao.updateAssignmentFeesAmountForRefund(refundPayment);
									}
									//System.out.println(refundPayment);
									//System.out.println(student);
									MailSender mailSender = (MailSender) act.getBean("mailer");
									
									mailSender.sendRefundEmail(student, refundPayment);
								}
							}else if("failure".equalsIgnoreCase(MSG)) {
								//System.out.println("fail transaction");
								serviceRequestDao.updatePendingRefundTransaction(refundPayment.getId(),"fail");
							}
						} else if(RAZORPAY.equalsIgnoreCase(refundPayment.getPaymentOption())) {
							razorpayLogger.info("Starting refund transaction for track id : {} and refund id : {}", 
									refundPayment.getMerchantRefNo(),refundPayment.getRefundId());
							RazorpayTransactionBean refundBean = new RazorpayTransactionBean();
							Map<String, String> requestBody = new HashMap<String, String>();
							requestBody.put("merchantRefNo", refundPayment.getMerchantRefNo());
							requestBody.put("refundId", refundPayment.getRefundId());
							razorpayLogger.info("created request body : {} for url : {}",requestBody.toString(), REFUND_STATUS_GATEWAY_URL);
							ResponseEntity<RazorpayTransactionBean>  refundEntity = restTempalte.postForEntity(REFUND_STATUS_GATEWAY_URL, requestBody, RazorpayTransactionBean.class);
							razorpayLogger.info("received response from URL : {}", refundEntity.toString());
							refundBean = refundEntity.getBody();
							razorpayLogger.info("refund status : {}", refundBean.getRefundStatus());
							razorpayLogger.info("refund was for type : {} so updating db accordinly", refundPayment.getFeesType());
							if("processed".equalsIgnoreCase(refundBean.getRefundStatus())) {
								if(serviceRequestDao.updatePendingRefundTransaction(refundPayment.getId(),"success") > 0) {
									StudentStudentPortalBean student = serviceRequestDao.getSingleStudentsData(refundPayment.getSapId());
									if("Exam Registration".equals(refundPayment.getFeesType())){
										serviceRequestDao.updateExamBookingAmountForRefund(refundPayment);
									}else if("Exam Registration MBA - WX".equals(refundPayment.getFeesType())){
										serviceRequestDao.updateMBAWXExamBookingAmountForRefund(refundPayment);
									}else if("Exam Registration MBA - X".equals(refundPayment.getFeesType())){
										serviceRequestDao.updateMBAXExamBookingAmountForRefund(refundPayment);
										
										ServiceRequestStudentPortal srBean = serviceRequestDao.getServiceRequestBySrId(refundPayment.getId());
										serviceRequestDao.insertServiceRequestStatusHistory(srBean, "Update");
									}else if("Service Request".equals(refundPayment.getFeesType())){
										serviceRequestDao.updateServiceRequestAmountForRefund(refundPayment);
									}else if("PCP Booking".equals(refundPayment.getFeesType())){
										serviceRequestDao.updatePCPBookingAmountForRefund(refundPayment);
									}else if("Assignment Fees".equals(refundPayment.getFeesType())){
										serviceRequestDao.updateAssignmentFeesAmountForRefund(refundPayment);
									}
									//System.out.println(refundPayment);
									//System.out.println(student);
									MailSender mailSender = (MailSender) act.getBean("mailer");
									
									mailSender.sendRefundEmail(student, refundPayment);
								}
							} else if("failed".equalsIgnoreCase(refundBean.getRefundStatus())) {
								serviceRequestDao.updatePendingRefundTransaction(refundPayment.getId(),"fail");
							}
						}
					}
					catch (Exception e) {
						//e.printStackTrace();
						// TODO: handle exception
					}
					
				}
			}
		}
		catch (Exception e) {
			// TODO: handle exception
			//e.printStackTrace();
		}
		
	}


	@Scheduled(fixedDelay=60*60*1000)
	public void synchronizePendingTransactions(){
		
		//System.out.println("Auto Conflict Correction Scheduler started running for SERVICE REQUEST");

		//System.out.println("Server = "+SERVER);
		//System.out.println("synchronizePendingTransactions scheduler for "+ENVIRONMENT); 

		if(!"tomcat4".equalsIgnoreCase(SERVER) || !"PROD".equalsIgnoreCase(ENVIRONMENT)){
			//System.out.println("Not running synchronizePendingTransactions scheduler since this is not tomcat4. This is "+SERVER);
			return;
		}
		
		try {
			PAYMENTS_LOGGER.info(" runnning synchronizePendingTransactions START ");
			ArrayList<ServiceRequestStudentPortal> paidSRList =  new ArrayList<>();
			ArrayList<ServiceRequestStudentPortal> expiredSRList =  new ArrayList<>();
			
			ArrayList<ServiceRequestStudentPortal> paymentInitiatedSRList =  serviceRequestDao.getPaymentInitiatedSRList();
			PAYMENTS_LOGGER.info("initiated payments found : {}", paymentInitiatedSRList.size());
			
			if(paymentInitiatedSRList != null  && paymentInitiatedSRList.size() > 0){
				for (ServiceRequestStudentPortal serviceRequest : paymentInitiatedSRList) {
					//System.out.println("---=====>>>> serviceRequest.getPaymentOption() : " + serviceRequest.getPaymentOption());
					try {
						if("paytm".equalsIgnoreCase(serviceRequest.getPaymentOption())) { 
							//paytm logic to check transaction status
							//PaymentHelper paymentHelper = new PaymentHelper();
							JsonObject jsonObj = paymentHelper.getPaytmTransactionStatus(serviceRequest.getTrackId());
							String STATUS = jsonObj.get("STATUS").getAsString();
							if("TXN_SUCCESS".equalsIgnoreCase(STATUS)) {
								try {
									serviceRequest.setRequestStatus(ServiceRequestStudentPortal.REQUEST_STATUS_SUBMITTED);
									serviceRequest.setTransactionID(jsonObj.get("TXNID").getAsString());
									serviceRequest.setMerchantRefNo(jsonObj.get("ORDERID").getAsString());
									serviceRequest.setResponseCode(jsonObj.get("RESPCODE").getAsString());
									serviceRequest.setResponseMessage(jsonObj.get("RESPMSG").getAsString());
									serviceRequest.setRespAmount(jsonObj.get("TXNAMOUNT").getAsString());
									serviceRequest.setRespPaymentMethod(jsonObj.get("PAYMENTMODE").getAsString());
									if(jsonObj.get("BANKTXNID") != null) {
										serviceRequest.setPaymentID(jsonObj.get("BANKTXNID").getAsString());
									}
									if(jsonObj.get("BANKNAME") != null) {
										serviceRequest.setBankName(jsonObj.get("BANKNAME").getAsString());
									}
									paidSRList.add(serviceRequest);
								}
								catch(Exception e) {
									//System.out.println("Paytm payment mode :: Error in setting service request bean for successful transaction .");
								}
							
							}else if("TXN_FAILURE".equalsIgnoreCase(STATUS)) {
								serviceRequest.setRequestStatus(ServiceRequestStudentPortal.REQUEST_STATUS_PAYMENT_FAILED);
								serviceRequest.setError(jsonObj.get("RESPMSG").getAsString());
								expiredSRList.add(serviceRequest);
							}
						}
						else if("payu".equalsIgnoreCase(serviceRequest.getPaymentOption())) {
							//PaymentHelper paymentHelper = new PaymentHelper();
							JsonObject jsonObj = paymentHelper.getPayuTransactionStatus(serviceRequest.getTrackId());
							//System.out.println("---==========>>>>> payu response");
							//System.out.println(jsonObj.toString());
							String STATUS = jsonObj.get("status").getAsString();
							//System.out.println("--====== >>>>> response transaction details");
							//System.out.println(jsonObj.toString());
							try {
							JsonObject transaction_detail = jsonObj.get("transaction_details").getAsJsonObject();
							transaction_detail = transaction_detail.get(serviceRequest.getTrackId()).getAsJsonObject();
							String MSG = transaction_detail.get("status").getAsString();
							if("1".equalsIgnoreCase(STATUS) && "success".equalsIgnoreCase(MSG)) {
								serviceRequest.setRequestStatus(ServiceRequestStudentPortal.REQUEST_STATUS_SUBMITTED);
								serviceRequest.setTransactionID(transaction_detail.get("mihpayid").getAsString());
								serviceRequest.setMerchantRefNo(transaction_detail.get("txnid").getAsString());
								serviceRequest.setResponseCode(transaction_detail.get("status").getAsString());
								serviceRequest.setResponseMessage(transaction_detail.get("status").getAsString());
								serviceRequest.setRespAmount(transaction_detail.get("amt").getAsString());
								serviceRequest.setRespPaymentMethod(transaction_detail.get("mode").getAsString());
								if(transaction_detail.get("bank_ref_num") != null) {
									serviceRequest.setPaymentID(transaction_detail.get("bank_ref_num").getAsString());
								}
								if(transaction_detail.get("bankcode") != null) {
									serviceRequest.setBankName(transaction_detail.get("bankcode").getAsString());
								}
								paidSRList.add(serviceRequest);
							}else if("0".equalsIgnoreCase(STATUS) || ("1".equalsIgnoreCase(STATUS) && "failure".equalsIgnoreCase(MSG))) {
								serviceRequest.setRequestStatus(ServiceRequestStudentPortal.REQUEST_STATUS_PAYMENT_FAILED);
								//serviceRequest.setError(transaction_detail.get("error_Message").getAsString());
								serviceRequest.setError(MSG);
								expiredSRList.add(serviceRequest);
							}
							}catch(Exception e) {
								//System.out.println("Payu payment mode ::Error in getting transaction_details");
								if("0".equalsIgnoreCase(STATUS)) {
									serviceRequest.setRequestStatus(ServiceRequestStudentPortal.REQUEST_STATUS_PAYMENT_FAILED);
									//serviceRequest.setError(transaction_detail.get("error_Message").getAsString());
									if(jsonObj.get("msg") != null) {
										serviceRequest.setError(jsonObj.get("msg").getAsString());
									}
									else {
										serviceRequest.setError("Invalid Response from payu");
									}
									expiredSRList.add(serviceRequest);
								}
							}
							
						} 
						else if("billdesk".equalsIgnoreCase(serviceRequest.getPaymentOption())) {
							String trackId = serviceRequest.getTrackId();
							
							PAYMENTS_LOGGER.info(" {} billdesk option found, fetching transaction Status from billdesk API", trackId);
							
							try {
								
								String billdeskResponse = paymentHelper.getBillDeskTransactionStatus(trackId);
								
								PAYMENTS_LOGGER.info(" {} response received from billdesk API : " + billdeskResponse, trackId);
								
								if(billdeskResponse != null) {
									String[] response = billdeskResponse.split("\\|");
									String tranStatus = response[15];
									
									if("0300".equalsIgnoreCase(tranStatus)) {
										
										serviceRequest.setRequestStatus(ServiceRequestStudentPortal.REQUEST_STATUS_SUBMITTED);
										serviceRequest.setTransactionID(response[4]);
										serviceRequest.setMerchantRefNo(response[2]);
										serviceRequest.setResponseCode(tranStatus);
										serviceRequest.setResponseMessage("Success");
										serviceRequest.setRespAmount(response[5]);
										serviceRequest.setRespPaymentMethod(response[6]);
										serviceRequest.setPaymentID(response[7]);
										serviceRequest.setBankName(response[6]);
										
										paidSRList.add(serviceRequest);
										
									} else if("0399".equalsIgnoreCase(tranStatus)) {
										serviceRequest.setRequestStatus(ServiceRequestStudentPortal.REQUEST_STATUS_PAYMENT_FAILED);
										serviceRequest.setError("Payment Failed");
										expiredSRList.add(serviceRequest);
									} else if("NA".equalsIgnoreCase(tranStatus)) {
										serviceRequest.setRequestStatus(ServiceRequestStudentPortal.REQUEST_STATUS_PAYMENT_FAILED);
										serviceRequest.setError("Error: [e.g. Txn not found/ Invalid checksum/ Invalid Request IP etc]");
										expiredSRList.add(serviceRequest);
									} else if("0002".equalsIgnoreCase(tranStatus) || "0001".equalsIgnoreCase(tranStatus)) {
										PAYMENTS_LOGGER.info("{} Transaction in pending / error at billdesk : {}", trackId, tranStatus);	
									} else {
										serviceRequest.setRequestStatus(ServiceRequestStudentPortal.REQUEST_STATUS_PAYMENT_FAILED);
										serviceRequest.setError(trackId + " Invalid status received from billdesk : " + tranStatus);
										PAYMENTS_LOGGER.info("{} Invalid status received from billdesk : {}", trackId, tranStatus);
									}
								} else {
									PAYMENTS_LOGGER.info("{} received null API response from billdesk", trackId);
								}
							} catch (Exception e) {
								PAYMENTS_LOGGER.error("{} Error while fetching payments from billdesk : {}", trackId, Throwables.getStackTraceAsString(e));
							}
						}  else {
							String trackId = serviceRequest.getTrackId();
							PAYMENTS_LOGGER.info("{} fetching payment status from payment gateways.", trackId);
							try {
								TransactionsBean transactionsBean =  paymentHelper.getTransactionStatusGateway(trackId);
								PAYMENTS_LOGGER.info("{} Received payment status from gateway : {}", trackId, transactionsBean);
								
								if(PAYMENT_SUCCESS.equalsIgnoreCase(transactionsBean.getTransaction_status())) {
										serviceRequest.setRequestStatus(ServiceRequestStudentPortal.REQUEST_STATUS_SUBMITTED);
										serviceRequest.setTransactionID(transactionsBean.getTransaction_id());
										serviceRequest.setMerchantRefNo(transactionsBean.getMerchant_ref_no());
										serviceRequest.setResponseCode(transactionsBean.getResponse_code());
										serviceRequest.setResponseMessage(transactionsBean.getResponse_message());
										serviceRequest.setRespAmount(transactionsBean.getResponse_amount());
										serviceRequest.setRespPaymentMethod(transactionsBean.getResponse_payment_method());
										serviceRequest.setPaymentID(transactionsBean.getPayment_id());
										serviceRequest.setBankName(transactionsBean.getBank_name());
										paidSRList.add(serviceRequest);
								
								}else if(PAYMENT_FAILED.equalsIgnoreCase(transactionsBean.getTransaction_status())) {
									serviceRequest.setRequestStatus(ServiceRequestStudentPortal.REQUEST_STATUS_PAYMENT_FAILED);
									serviceRequest.setError(transactionsBean.getError());
									expiredSRList.add(serviceRequest);
									
								} else if(PAYMENT_PENDING.equalsIgnoreCase(transactionsBean.getTransaction_status())) {
									
								PAYMENTS_LOGGER.info("{} Transaction still in pending state", trackId);
								
								}else if(PAYMENT_INVALID_REQUEST.equalsIgnoreCase(transactionsBean.getTransaction_status())) {
									serviceRequest.setRequestStatus(ServiceRequestStudentPortal.REQUEST_STATUS_PAYMENT_FAILED);
									serviceRequest.setError(transactionsBean.getError());
									expiredSRList.add(serviceRequest);
									
								} else {
									serviceRequest.setRequestStatus(ServiceRequestStudentPortal.REQUEST_STATUS_PAYMENT_FAILED);
									serviceRequest.setError("Invalid payment status : " + transactionsBean.getTransaction_status());
									expiredSRList.add(serviceRequest);
								}
							} catch (Exception e) {
								PAYMENTS_LOGGER.error("{} error updated transaction status from gateway : {}", trackId, Throwables.getStackTraceAsString(e));
							}
							
							
//							//System.out.println("--------> inside else for hdfc condition");
//							String trackId = serviceRequest.getTrackId();
//							//System.out.println("Checking status for "+trackId);
//							XMLParser parser = new XMLParser();
//							String xmlResponse = parser.queryTransactionStatus(trackId, ACCOUNT_ID, SECURE_SECRET);
//							//System.out.println("xmlResponse = "+xmlResponse);
//							parser.parseResponse(xmlResponse, serviceRequest);
//							String transactionType = serviceRequest.getTransactionType();
//							String status = serviceRequest.getStatus();
//							
//							if(("Authorized".equalsIgnoreCase(transactionType) || "Captured".equalsIgnoreCase(transactionType))&& "Processed".equalsIgnoreCase(status)){
//								serviceRequest.setRequestStatus(ServiceRequestStudentPortal.REQUEST_STATUS_SUBMITTED);
//								paidSRList.add(serviceRequest);
//								//System.out.println("SAPID: "+serviceRequest.getSapId() + " Amount: "+serviceRequest.getAmount());
//							}else{
//								serviceRequest.setRequestStatus(ServiceRequestStudentPortal.REQUEST_STATUS_PAYMENT_FAILED);
//								expiredSRList.add(serviceRequest);
//							}
						}
					}
					catch (Exception e) {
						PAYMENTS_LOGGER.error("{} Error trying to update sr payments : {}", serviceRequest.getTrackId(), Throwables.getStackTraceAsString(e));
					}
					
				}
				
				if(paidSRList.size() > 0){
					//Mark SR as Payment Successful
					ServiceRequestController controller = act.getBean(ServiceRequestController.class);
					//System.out.println("Controller: "+controller);
					for (ServiceRequestStudentPortal serviceRequest : paidSRList) {
						serviceRequestDao.updateSRTransactionDetailsFromAPI(serviceRequest);
						ServiceRequestStudentPortal bean = serviceRequestDao.getServiceRequestBySrId(serviceRequest.getId());
						serviceRequestDao.insertServiceRequestStatusHistory(bean, "Update");
						StudentStudentPortalBean student = serviceRequestDao.getSingleStudentsData(serviceRequest.getSapId());
						controller.handlePostPaymentAction(student, serviceRequest);
					}
				}
				
				if(expiredSRList.size() > 0){
					//Mark SR as Payment UnSuccessful
					for (ServiceRequestStudentPortal serviceRequest : expiredSRList) {
						serviceRequestDao.markServiceRequestFailed(serviceRequest);
						ServiceRequestStudentPortal bean = serviceRequestDao.getServiceRequestBySrId(serviceRequest.getId());
						serviceRequestDao.insertServiceRequestStatusHistory(bean, "Update");
					}
				}
			}

		} catch (Exception e) {
			PAYMENTS_LOGGER.error("Error running  synchronizePendingTransactions : {}",Throwables.getStackTraceAsString(e));
		}
		
		PAYMENTS_LOGGER.info(" runnning synchronizePendingTransactions END ");
	}
	/*@Scheduled(fixedDelay=5*60*1000)
	public void checkIfMarksheetForSameRecordCreated(){
		MailSender mailer = (MailSender)act.getBean("mailer");
		//System.out.println("Listener to check marksheet for same year,month and sem started");
		MailSender mailHelper = new MailSender();
		ArrayList<ServiceRequest> getListOfMarksheetsIssuedTwiceInTwentyFourHours = serviceRequestDao.getListOfMarksheetsIssuedTwiceInTwentyFourHours();
		if(getListOfMarksheetsIssuedTwiceInTwentyFourHours!=null && getListOfMarksheetsIssuedTwiceInTwentyFourHours.size()>0){
			mailer.sendProcessRefundMail(getListOfMarksheetsIssuedTwiceInTwentyFourHours.get(0));
		}
		
	}*/

	

}
