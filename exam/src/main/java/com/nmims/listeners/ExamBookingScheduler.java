
package com.nmims.listeners;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
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
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.context.ServletConfigAware;

import com.google.common.base.Throwables;
import com.google.gson.JsonObject;
import com.nmims.beans.AssignmentFileBean;
import com.nmims.beans.ExamBookingTransactionBean;
import com.nmims.beans.ExamCenterBean;
import com.nmims.beans.StudentExamBean;
import com.nmims.beans.StudentMarksBean;
import com.nmims.beans.TransactionsBean;
import com.nmims.beans.TransactionsExamBean;
import com.nmims.controllers.ExamBookingController;
import com.nmims.daos.ExamBookingDAO;
import com.nmims.daos.ExamCenterDAO;
import com.nmims.daos.ProjectSubmissionDAO;
import com.nmims.helpers.ExamBookingHelper;
import com.nmims.helpers.MailSender;
import com.nmims.helpers.PaymentHelper;
import com.nmims.helpers.XMLParser;
import com.nmims.stratergies.ExamRegistrationRealTimeStrategy;

@Service("examBookingScheduler")
public class ExamBookingScheduler implements ApplicationContextAware, ServletConfigAware{

	@Value( "${SECURE_SECRET}" )
	private String SECURE_SECRET; // secret key;
	@Value( "${ACCOUNT_ID}" )
	private String ACCOUNT_ID;

	@Value( "${SERVER}" )
	private String SERVER;

	@Value( "${ENVIRONMENT}" )
	private String ENVIRONMENT;
	
	@Value("${SERVER_PATH}")
	private String SERVER_PATH;
	
	@Autowired
	ExamBookingHelper examBookingHelper;
	
	@Autowired
	PaymentHelper paymentHelper;
	
	@Autowired
	private RestTemplate restTemplate;
	
	@Autowired
	private  ExamRegistrationRealTimeStrategy examRegistrationRealTimeStrategy;

	@Autowired
	public ExamBookingController examBookingController;
	
	private static ApplicationContext act = null;
	private static ServletConfig sc = null;
	private int examFeesPerSubjectFirstAttempt = 500;
	private int examFeesPerSubjectResitAttempt = 600;
	
	private final String ONLINE_PAYMENT_SUCCESS = "Online Payment Successfull";
	private final String ONLINE_PAYMENT_FAILED = "Transaction Failed"; 
	private final String ONLINE_PAYMENT_INITIATED = "Online Payment Initiated"; 
	
	private final String PAYMENT_SUCCESS = "Payment Successfull";
	private final String PAYMENT_FAILED = "Payment Failed";
	private final String PAYMENT_PENDING = "Payment Pending";
	private final String PAYMENT_INVALID_REQUEST = "Invalid Request";

	private static final Logger logger = LoggerFactory.getLogger("examBookingPayments");
	public static final Logger examRegsiterPGlogger = LoggerFactory.getLogger("examRegisterPG");
	
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



	@Scheduled(fixedDelay=5*60*1000)
	public void clearOnHoldSeats(){


		if(!"tomcat6".equalsIgnoreCase(SERVER) || !"PROD".equalsIgnoreCase(ENVIRONMENT)){
			return;
		}


		ExamBookingDAO dao = (ExamBookingDAO)act.getBean("examBookingDAO");
		boolean isExamRegistraionLive = dao.isConfigurationLivePost1Day("Exam Registration");
		if(!isExamRegistraionLive){
			return;
		}
		ArrayList<ExamBookingTransactionBean> response = dao.getExpiredTransaction();
		for (ExamBookingTransactionBean examBookingTransactionBean : response) {
			try {
				if("paytm".equalsIgnoreCase(examBookingTransactionBean.getPaymentOption())) {
					JsonObject jsonObj = paymentHelper.getPaytmTransactionStatus(examBookingTransactionBean.getTrackId());
					if(jsonObj != null) {
						examBookingTransactionBean.setTranStatus(jsonObj.get("STATUS").getAsString());
					}else {
						examBookingTransactionBean.setTranStatus("Null Error Response from paytm");
					}
				}else if("billdesk".equalsIgnoreCase(examBookingTransactionBean.getPaymentOption())) {
					String paymentResponse = paymentHelper.getBillDeskTransactionStatus(examBookingTransactionBean.getTrackId());
					if(paymentResponse != null) {
						try {
							String[] responseList = paymentResponse.split("\\|");
							if("0002".equalsIgnoreCase(responseList[15])) {
								examBookingTransactionBean.setTranStatus("Pending from billdesk");
							}
							else if("0300".equalsIgnoreCase(responseList[15])) {
								examBookingTransactionBean.setTranStatus("Success from billdesk");
							}else {
								examBookingTransactionBean.setTranStatus("Invalid response from billdesk");
							}
						}
						catch (Exception e) {
							// TODO: handle exception
							examBookingTransactionBean.setTranStatus("Invalid response from billdesk : " + e.getMessage());
						}
					}else {
						examBookingTransactionBean.setTranStatus("Null Error Response from billdesk");
					}
				}else if("payu".equalsIgnoreCase(examBookingTransactionBean.getPaymentOption())) {
					//PaymentHelper paymentHelper = new PaymentHelper();
					
					JsonObject jsonObj = paymentHelper.getPayuTransactionStatus(examBookingTransactionBean.getTrackId());
					String STATUS = jsonObj.get("status").getAsString();
					try {
						JsonObject transaction_detail = jsonObj.get("transaction_details").getAsJsonObject();
						transaction_detail = transaction_detail.get(examBookingTransactionBean.getTrackId()).getAsJsonObject();
						String MSG = transaction_detail.get("status").getAsString();
						if("1".equalsIgnoreCase(STATUS) && "success".equalsIgnoreCase(MSG)) {
							examBookingTransactionBean.setTranStatus("Success from payu");
						}else {
							examBookingTransactionBean.setTranStatus("Failed or pending from payu : " + MSG);
						}
					}
					catch (Exception e) {
						// TODO: handle exception
						examBookingTransactionBean.setTranStatus("Invalid response from payu");
					}
				}
				else {
					TransactionsBean transactionsBean = new TransactionsBean();
					transactionsBean.setTrack_id(examBookingTransactionBean.getTrackId());
					transactionsBean.setPayment_option(examBookingTransactionBean.getPaymentOption());
					
					String url = SERVER_PATH+"paymentgateways/m/getTransactionStatus";
					HttpHeaders headers = new HttpHeaders();
					
					headers.setAccept(Arrays.asList(MediaType.APPLICATION_JSON) );
					try {
						HttpEntity<TransactionsBean> entity = new HttpEntity<TransactionsBean>(transactionsBean, headers);
						transactionsBean = restTemplate.exchange(url,  HttpMethod.POST,entity, TransactionsBean.class).getBody();
						
						logger.info("received response from gateway for track id : {}, {}", examBookingTransactionBean.getTrackId(), transactionsBean.toString());
						String STATUS = transactionsBean.getTransaction_status();
						
						examBookingTransactionBean.setTranStatus(STATUS);
						
					} catch (Exception e) {
						// TODO: handle exception
					}
				}
//				else {
//					try {
//						XMLParser parser = new XMLParser();
//						String xmlResponse = parser.queryTransactionStatus(examBookingTransactionBean.getTrackId(), ACCOUNT_ID, SECURE_SECRET);
//						parser.parseResponse(xmlResponse, examBookingTransactionBean);
//						String transactionType = examBookingTransactionBean.getTransactionType();
//						String status = examBookingTransactionBean.getStatus();
//		
//						if(("Authorized".equalsIgnoreCase(transactionType) || "Captured".equalsIgnoreCase(transactionType))&& "Processed".equalsIgnoreCase(status)){
//							examBookingTransactionBean.setTranStatus(status);
//						}else {
//							examBookingTransactionBean.setTranStatus(xmlResponse);
//						}
//					}
//					catch (Exception e) {
//						// TODO: handle exception
//						examBookingTransactionBean.setTranStatus("Error: " + e.getMessage());
//					}
//				}
			}
			catch (Exception e) {
				// TODO: handle exception
				examBookingTransactionBean.setTranStatus("Error outside: " + e.getMessage());
			}
			
		}
		dao.clearOldOnlineInitiationTransactionPeriodically(response);
		if(response != null && response.size() > 0) {
			MailSender mailSender = (MailSender)act.getBean("mailer");
			mailSender.sendBookingExpiredEmailForBooking(response);
		}
		
	}


	@Scheduled(fixedDelay=13*60*1000)
	public void doAutoBookingForConflictTransactions(){

		if(!"tomcat6".equalsIgnoreCase(SERVER) || !"PROD".equalsIgnoreCase(ENVIRONMENT)){
			return;
		}
		
		logger.info("doAutoBookingForConflictTransactions started.");
		ExamBookingDAO dao = (ExamBookingDAO)act.getBean("examBookingDAO");
		ExamCenterDAO ecDao = (ExamCenterDAO)act.getBean("examCenterDAO");
		boolean isExamActiveForCurrentExamYearMonth = false;
		
		boolean isExamRegistraionLive = dao.isConfigurationLivePost1Day("Exam Registration");
		if (!isExamRegistraionLive) {
			isExamActiveForCurrentExamYearMonth = dao.checkIfExamTimetableActiveForCurrentExamYearMonth();
			
			if (!isExamActiveForCurrentExamYearMonth)
				return;
		}
		

		int noOfConflictTransactions = 0;
		int noOfPendingRefunds = 0;
		double totalConflictAmount = 0;
		double refundDueAmount = 0;

		try{

			ArrayList<ExamBookingTransactionBean> unSuccessfulExamBookings = dao.getAllUnSuccessfulExamBookings();

			logger.info("doAutoBookingForConflictTransactions number of bookings to process : " + unSuccessfulExamBookings.size());
			
			ArrayList<ExamBookingTransactionBean> successfulExamBookings = new ArrayList<>();
			ArrayList<ExamBookingTransactionBean> transactionFailedExamBookings = new ArrayList<>();
			ArrayList<ExamBookingTransactionBean> transactionPendingExamBookings = new ArrayList<>();
			
			for (int i = 0; i < unSuccessfulExamBookings.size(); i++) {
				try {
					ExamBookingTransactionBean bean = unSuccessfulExamBookings.get(i);

					logger.info("doAutoBookingForConflictTransactions checking status for : " + i + "/"
							+ unSuccessfulExamBookings.size() + " trackid " + bean.getTrackId());

					String trackId = bean.getTrackId();
					TransactionsBean transactionsBean = new TransactionsBean();
					transactionsBean.setTrack_id(bean.getTrackId());
					transactionsBean.setPayment_option(bean.getPaymentOption());

					String url = SERVER_PATH + "paymentgateways/m/getTransactionStatus";
					HttpHeaders headers = new HttpHeaders();

					headers.setAccept(Arrays.asList(MediaType.APPLICATION_JSON));

					HttpEntity<TransactionsBean> entity = new HttpEntity<TransactionsBean>(transactionsBean, headers);
					
					transactionsBean = restTemplate.exchange(url, HttpMethod.POST, entity, TransactionsBean.class)
							.getBody();

					logger.info("received response from gateway for track id : {}, {}", bean.getTrackId(),
							transactionsBean);
					
					String STATUS = transactionsBean.getTransaction_status();
					if (PAYMENT_SUCCESS.equalsIgnoreCase(STATUS)) {
						try {
							bean.setTranStatus(ONLINE_PAYMENT_SUCCESS);
							bean.setTransactionID(transactionsBean.getTransaction_id());
							bean.setMerchantRefNo(transactionsBean.getMerchant_ref_no());
							bean.setResponseCode(transactionsBean.getResponse_code());
							bean.setResponseMessage(transactionsBean.getResponse_message());
							bean.setRespAmount(transactionsBean.getResponse_amount());
							bean.setRespPaymentMethod(transactionsBean.getResponse_payment_method());
							bean.setPaymentID(transactionsBean.getPayment_id());
							bean.setBankName(transactionsBean.getBank_name());
							bean.setRespTranDateTime(transactionsBean.getResponse_transaction_date_time());
							bean.setDescription("Exam fees for " + bean.getSapid());
							successfulExamBookings.add(bean);

							logger.info("" + " doAutoBookingForConflictTransactions paytm success " + " bean : " + bean
									+ " respObject : " + transactionsBean.toString());

						} catch (Exception e) {
							// TODO: handle exception
							logger.info("" + " doAutoBookingForConflictTransactions paytm exception : " + e.getMessage()
									+ " trackId : " + bean.getTrackId() + " respObject : "
									+ transactionsBean.toString());
						}
					} else if (PAYMENT_FAILED.equalsIgnoreCase(STATUS)) {
						bean.setError(transactionsBean.getError());
						bean.setTranStatus(ONLINE_PAYMENT_FAILED);
						transactionFailedExamBookings.add(bean);
					} else if ("PENDING".equalsIgnoreCase(STATUS)) {
						bean.setError(transactionsBean.getError());
						bean.setTranStatus(ONLINE_PAYMENT_INITIATED);
						transactionPendingExamBookings.add(bean);
					} else if("Invalid Request".equalsIgnoreCase(STATUS)) {
						bean.setError(transactionsBean.getError());
						bean.setTranStatus(ONLINE_PAYMENT_FAILED);
						transactionFailedExamBookings.add(bean);
					} else {
						bean.setError("Null API Response");
						bean.setTranStatus(ONLINE_PAYMENT_INITIATED);
						transactionPendingExamBookings.add(bean);
						logger.info(""
								+ " doAutoBookingForConflictTransactions paytm null object returned from API : TrackID "
								+ bean.getTrackId());
					}
					logger.info("" + " doAutoBookingForConflictTransactions transaction failed " + " bean : " + bean
							+ " respObject : " + transactionsBean);
				} catch (Exception e) {
					StringWriter errors = new StringWriter();
					e.printStackTrace(new PrintWriter(errors));
					logger.info(" checking status  exception : " + errors);
				}
			}


			logger.info(" doAutoBookingForConflictTransactions number of successful : " + successfulExamBookings.size() );
			logger.info("  list of failed transactions size " + transactionFailedExamBookings.size() );
			
			logger.info("doAutoBookingForConflictTransactions transactionPendingExamBookings list of pending transactions " + transactionPendingExamBookings.size());
			try {
				dao.markTransactionsPending(transactionPendingExamBookings);
				logger.info(" successfully mark list of pending transactions " + transactionPendingExamBookings.size());

			}catch (Exception e) {
				StringWriter errors = new StringWriter();
				e.printStackTrace(new PrintWriter(errors));
				logger.error("transactionPendingExamBookings Error with DB Call : " + errors);
			}
			
			ArrayList<ExamBookingTransactionBean> successfulButCenterNotAvailableExamBookings = new ArrayList<>();
			ArrayList<ExamBookingTransactionBean> successfulButAlreadyBookedExamBookings = new ArrayList<>();
			
			HashMap<String,String> corporateCenterUserMapping = new HashMap<String,String>();
			corporateCenterUserMapping = ecDao.getCorporateCenterUserMapping();//Get details of Students mapped to Corporate Exam Centers
			String maxTimeTableLiveYearMonth = dao.getMaxTimeTableLiveYearMonth();
			logger.info(" doAutoBookingForConflictTransactions add successful transaction : " + successfulExamBookings.size());
			for (int i = 0; i < successfulExamBookings.size(); i++) {

				try {
					ExamBookingTransactionBean bean = successfulExamBookings.get(i);

					logger.info(" doAutoBookingForConflictTransactions add successful transaction : " + i+"/"+successfulExamBookings.size()+" trackid "+bean.getTrackId() );
					String sapid = bean.getSapid();
					String trackId = bean.getTrackId();
//					StudentExamBean student = dao.getSingleStudentWithValidity(sapid); commented by Abhay
					StudentExamBean student = dao.getSingleStudentDataWithInValidity(sapid, maxTimeTableLiveYearMonth );
					
					if(corporateCenterUserMapping.containsKey(student.getSapid())){
						student.setCorporateExamCenterStudent(true);
						student.setMappedCorporateExamCenterId(corporateCenterUserMapping.get(sapid));
					}else{
						student.setCorporateExamCenterStudent(false);
					}
					

					ArrayList<ExamBookingTransactionBean> subjectsCentersList = dao.getSubjectsCentersForTrackId(trackId);
					ArrayList<String> subjectsCenters = new ArrayList<>();
					ArrayList<String> subjects = new ArrayList<>();

					for (int j = 0; j < subjectsCentersList.size(); j++) {

						subjectsCenters.add(subjectsCentersList.get(j).getSubject() 
								+"|"+subjectsCentersList.get(j).getCenterId() 
								+ "|" + subjectsCentersList.get(j).getExamDate() 
								+ "|" + subjectsCentersList.get(j).getExamTime());

						//subjectsCenters.add(subjectsCentersList.get(j).getSubject()+"|"+subjectsCentersList.get(j).getCenterId() +"|"+subjectsCentersList.get(j).getExamTime());
						subjects.add(subjectsCentersList.get(j).getSubject());
					}

//					ArrayList<String> alreadyBooked = dao.getSubjectsBookedForStudent(sapid);
					List<ExamBookingTransactionBean> alreadyBooked = dao.getAlreadyBookedSubjectsForStudent(sapid);
					boolean subjectAlreadyBooked = false;
					for (int j = 0; j < subjectsCentersList.size(); j++) {
//						if(alreadyBooked.contains(subjectsCentersList.get(j).getSubject())){
//							subjectAlreadyBooked = true;
//
//							logger.info(""
//								+ " doAutoBookingForConflictTransactions add successful transaction "
//								+ " bean : " + bean
//								+ " conflict found : " + subjectsCentersList.get(j).getSubject()
//							);
//						}
						
						for( ExamBookingTransactionBean b : alreadyBooked ) {
							if(b.getSubject().equals(subjectsCentersList.get(j).getSubject()) && !b.getTrackId().equals(trackId) ) {
								subjectAlreadyBooked = true;
								logger.info(""
										+ " doAutoBookingForConflictTransactions add successful transaction "
										+ " bean : " + bean
										+ " conflict found : " + subjectsCentersList.get(j).getSubject()
									);
							}
						}
					}
					if(subjectAlreadyBooked){
						logger.info(" subjectAlreadyBooked : " + i+"/"+successfulExamBookings.size()+" trackid "+bean.getTrackId()+" subject "+bean.getSubject() );
						bean.setAction("REFUND");
						successfulButAlreadyBookedExamBookings.add(bean);
						totalConflictAmount += Double.parseDouble(bean.getRespAmount());
						refundDueAmount += Double.parseDouble(bean.getRespAmount());
						noOfConflictTransactions++;
						noOfPendingRefunds++;
						dao.updateSeatsForAlreadyBookedConflictUsingSingleConnection(bean);
						continue;
					}

					boolean centerStillAvailable = checkIfCenterStillAvailable(subjectsCenters, student);
					logger.info(" centerStillAvailable : "+centerStillAvailable+"  " + i+"/"+successfulExamBookings.size()+" trackid "+bean.getTrackId());
					if(!centerStillAvailable){
						bean.setAction("APPROVE_FOR_REBOOKING");
						successfulButCenterNotAvailableExamBookings.add(bean);
						totalConflictAmount += Double.parseDouble(bean.getRespAmount());
						noOfConflictTransactions++;
						logger.info("doAutoBookingForConflictTransactions center not available - bean : " + i+"/"+successfulExamBookings.size()+" trackid "+bean.getTrackId());
						continue;
					}else {
						List<ExamBookingTransactionBean> examBookings = dao.updateSeatsForConflictUsingSingleConnection(bean);
						examRegsiterPGlogger.info("Real Time Registartion called from scheduler method"+dao.getIsExtendedExamRegistrationLiveForRealTime());
						if(dao.getIsExtendedExamRegistrationLiveForRealTime()) {
							examRegsiterPGlogger.info("Real Time Registartion called");
						examRegistrationRealTimeStrategy.registrationOnMettlAndReleaseBooking(examBookings, null);
						}
						MailSender mailSender = (MailSender)act.getBean("mailer");
						mailSender.sendBookingSummaryEmailForConflictBooking(student, examBookings, getExamCenterIdNameMap(),ecDao);
						examBookingHelper.createAndUploadHallTicketAndExamFeeReceipt(sapid, student.isCorporateExamCenterStudent());
						
						logger.info("doAutoBookingForConflictTransactions mark success : " + i+"/"+successfulExamBookings.size()+" trackid "+bean.getTrackId());
					}

				} catch (Exception e) {
					StringWriter errors = new StringWriter();
					e.printStackTrace(new PrintWriter(errors));
					
					// check if this size is overflowing
					if(successfulExamBookings.size() > i+1) {
						logger.error(""
							+ "doAutoBookingForConflictTransactions exception : " + errors.toString() 
							+ "\n bean : " + successfulExamBookings.get(i) 
						);
					}
					logger.error(i+"/"+successfulExamBookings.size()+" doAutoBookingForConflictTransactions exception : " + errors.toString());
				}
			}
			
			logger.info(" start markTransactionsFailed " + transactionFailedExamBookings.size() );
			
			dao.markTransactionsFailed(transactionFailedExamBookings);
			logger.info(" Successfull Completed list of failed transactions " + transactionFailedExamBookings.size() );
			
			if((successfulButCenterNotAvailableExamBookings != null && successfulButCenterNotAvailableExamBookings.size() > 0)
				|| (successfulButAlreadyBookedExamBookings != null && successfulButAlreadyBookedExamBookings.size() > 0 )
			   )
			{
				MailSender mailSender = (MailSender)act.getBean("mailer");
				mailSender.sendConflictsEmail(successfulButCenterNotAvailableExamBookings, successfulButAlreadyBookedExamBookings);
				dao.updateConfilctTransactionDetails(successfulButCenterNotAvailableExamBookings, successfulButAlreadyBookedExamBookings);
				

				logger.info( " successfulButCenterNotAvailableExamBookings " + successfulButCenterNotAvailableExamBookings.size() );
				logger.info(" successfulButAlreadyBookedExamBookings " + successfulButAlreadyBookedExamBookings.size() );
			}
			
			if(successfulButCenterNotAvailableExamBookings != null && successfulButCenterNotAvailableExamBookings.size() > 0 ) {
			sc.getServletContext().setAttribute("successfulButCenterNotAvailableExamBookings", successfulButCenterNotAvailableExamBookings);
			}
			if(successfulButAlreadyBookedExamBookings != null && successfulButAlreadyBookedExamBookings.size() > 0 ) {
			sc.getServletContext().setAttribute("successfulButAlreadyBookedExamBookings", successfulButAlreadyBookedExamBookings);
			}
			sc.getServletContext().setAttribute("conflictAmount", totalConflictAmount);
			sc.getServletContext().setAttribute("noOfConflictTransactions", noOfConflictTransactions);
			sc.getServletContext().setAttribute("refundDueAmount", refundDueAmount);
			sc.getServletContext().setAttribute("noOfPendingRefunds", noOfPendingRefunds);
		}catch(Exception e){
			StringWriter errors = new StringWriter();
			e.printStackTrace(new PrintWriter(errors));
			logger.error(" Exception  Occur : " + errors.toString());
		}
	}
	
// Changes Added by stef
  /*@Scheduled(fixedDelay=15*60*1000)
	public void doAutoBookingForProjectConflictTransactions(){

		if(!"tomcat6".equalsIgnoreCase(SERVER) || !"PROD".equalsIgnoreCase(ENVIRONMENT)){
			return;
		}
		ExamBookingDAO dao = (ExamBookingDAO)act.getBean("examBookingDAO");
		ExamCenterDAO ecDao = (ExamCenterDAO)act.getBean("examCenterDAO");
		ProjectSubmissionDAO pDao = (ProjectSubmissionDAO)act.getBean("projectSubmissionDAO");
		boolean isExamRegistraionLive = dao.isConfigurationLivePost1Day("Exam Registration");
		boolean isProjectRegistraionLive = pDao.isProjectLive("Project");//added new **************
		if(!isExamRegistraionLive && !isProjectRegistraionLive){
			return;
		}
		

		int noOfConflictTransactions = 0;
		int noOfPendingRefunds = 0;
		double totalConflictAmount = 0;
		double refundDueAmount = 0;

		try{
			ArrayList<ExamBookingTransactionBean> unSuccessfulExamBookings = new ArrayList<>();
			ArrayList<ExamBookingTransactionBean> successfulExamBookings = new ArrayList<>();
			ArrayList<ExamBookingTransactionBean> transactionFailedExamBookings = new ArrayList<>();
	if(isExamRegistraionLive){
		unSuccessfulExamBookings =dao.getAllUnSuccessfulExamBookings();
	}		
			
	if(isProjectRegistraionLive && !isExamRegistraionLive){
		 unSuccessfulExamBookings =dao.getAllUnSuccessfulProjectBookings();//added new ********************
	}
			

			for (int i = 0; i < unSuccessfulExamBookings.size(); i++) {
				ExamBookingTransactionBean bean = unSuccessfulExamBookings.get(i);
				String trackId = bean.getTrackId();
				XMLParser parser = new XMLParser();
				String xmlResponse = parser.queryTransactionStatus(trackId, ACCOUNT_ID, SECURE_SECRET);
				parser.parseResponse(xmlResponse, bean);
				String transactionType = bean.getTransactionType();
				String status = bean.getStatus();
				String error = bean.getError();
				String errorCode = bean.getErrorCode();

				if(("Authorized".equalsIgnoreCase(transactionType) || "Captured".equalsIgnoreCase(transactionType))&& "Processed".equalsIgnoreCase(status)){
					successfulExamBookings.add(bean);
				}else if("AuthFailed".equalsIgnoreCase(transactionType)&& "Processed".equalsIgnoreCase(status)){
					
					transactionFailedExamBookings.add(bean);
				}else if("3".equals(errorCode) && ("Invalid Refrence No".equals(error) || "Invalid Reference No".equals(error))){
					transactionFailedExamBookings.add(bean);
				}
			}

			ArrayList<ExamBookingTransactionBean> successfulButCenterNotAvailableExamBookings = new ArrayList<>();
			ArrayList<ExamBookingTransactionBean> successfulButAlreadyBookedExamBookings = new ArrayList<>();
			
			HashMap<String,String> corporateCenterUserMapping = new HashMap<String,String>();
			corporateCenterUserMapping = ecDao.getCorporateCenterUserMapping();//Get details of Students mapped to Corporate Exam Centers
			String projectLastDate = "";
			for (int i = 0; i < successfulExamBookings.size(); i++) {

				try {
					ExamBookingTransactionBean bean = successfulExamBookings.get(i);
					String sapid = bean.getSapid();
					String trackId = bean.getTrackId();
					StudentBean student = dao.getSingleStudentWithValidity(sapid);
					
					if(corporateCenterUserMapping.containsKey(student.getSapid())){
						student.setCorporateExamCenterStudent(true);
						student.setMappedCorporateExamCenterId(corporateCenterUserMapping.get(sapid));
					}else{
						student.setCorporateExamCenterStudent(false);
					}
					

					ArrayList<ExamBookingTransactionBean> subjectsCentersList = dao.getSubjectsCentersForTrackId(trackId);
					ArrayList<String> subjectsCenters = new ArrayList<>();
					ArrayList<String> subjects = new ArrayList<>();

					for (int j = 0; j < subjectsCentersList.size(); j++) {

						subjectsCenters.add(subjectsCentersList.get(j).getSubject() 
								+"|"+subjectsCentersList.get(j).getCenterId() 
								+ "|" + subjectsCentersList.get(j).getExamDate() 
								+ "|" + subjectsCentersList.get(j).getExamTime());


						//subjectsCenters.add(subjectsCentersList.get(j).getSubject()+"|"+subjectsCentersList.get(j).getCenterId() +"|"+subjectsCentersList.get(j).getExamTime());
						subjects.add(subjectsCentersList.get(j).getSubject());
					}

					//List<TimetableBean> timeTableList = dao.getTimetableListForGivenSubjects(subjects, program, studentProgramStructure);
	ArrayList<String> alreadyBooked = new ArrayList<>();
	
	if(isExamRegistraionLive){
		 alreadyBooked = dao.getSubjectsBookedForStudent(sapid);
	}
		if(isProjectRegistraionLive && !isExamRegistraionLive){
			 alreadyBooked = dao.getProjectBookedForStudent(sapid); //added new ***************************
				AssignmentFileBean assignmnetFile = new AssignmentFileBean();
				assignmnetFile.setSubject("Project");
				assignmnetFile = pDao.findById(assignmnetFile);
				String endDate=assignmnetFile.getEndDate();
				endDate = endDate.replaceAll("T", " ");
				projectLastDate = endDate.substring(0, 10);
		}

					boolean subjectAlreadyBooked = false;
					for (int j = 0; j < subjectsCentersList.size(); j++) {
						if(alreadyBooked.contains(subjectsCentersList.get(j).getSubject())){
							subjectAlreadyBooked = true;
						}
					}
					if(subjectAlreadyBooked){
						bean.setAction("REFUND");
						successfulButAlreadyBookedExamBookings.add(bean);
						totalConflictAmount += Double.parseDouble(bean.getRespAmount());
						refundDueAmount += Double.parseDouble(bean.getRespAmount());
						noOfConflictTransactions++;
						noOfPendingRefunds++;
						continue;
					}

					boolean centerStillAvailable = checkIfCenterStillAvailable(subjectsCenters, student);
					if(!centerStillAvailable){
						bean.setAction("APPROVE_FOR_REBOOKING");
						successfulButCenterNotAvailableExamBookings.add(bean);
						totalConflictAmount += Double.parseDouble(bean.getRespAmount());
						noOfConflictTransactions++;
						continue;
					}else{
						List<ExamBookingTransactionBean> examBookings = dao.updateSeatsForConflictUsingSingleConnection(bean);
						MailSender mailSender = (MailSender)act.getBean("mailer");
						if(isProjectRegistraionLive && !isExamRegistraionLive){
						   mailSender.sendBookingSummaryEmailForProjectConflictBooking(student, examBookings, getExamCenterIdNameMap(),projectLastDate);
						}else if(!isProjectRegistraionLive && isExamRegistraionLive){
							mailSender.sendBookingSummaryEmailForConflictBooking(student, examBookings, getExamCenterIdNameMap(),ecDao);
						}else{
							mailSender.sendBookingSummaryEmailForConflictBooking(student, examBookings, getExamCenterIdNameMap(),ecDao);
							mailSender.sendBookingSummaryEmailForProjectConflictBooking(student, examBookings, getExamCenterIdNameMap(),projectLastDate);
						}
						
						examBookingHelper.createAndUploadHallTicketAndExamFeeReceipt(sapid, student.isCorporateExamCenterStudent());
					}

				} catch (Exception e) {
					
				}
			}
			
			dao.markTransactionsFailed(transactionFailedExamBookings);
			
			if((successfulButCenterNotAvailableExamBookings != null && successfulButCenterNotAvailableExamBookings.size() > 0)
				|| (successfulButAlreadyBookedExamBookings != null && successfulButAlreadyBookedExamBookings.size() > 0 )
			   )
			{
				MailSender mailSender = (MailSender)act.getBean("mailer");
				mailSender.sendConflictsEmail(successfulButCenterNotAvailableExamBookings, successfulButAlreadyBookedExamBookings);
				
				dao.updateConfilctTransactionDetails(successfulButCenterNotAvailableExamBookings, successfulButAlreadyBookedExamBookings);
			}

			sc.getServletContext().setAttribute("successfulButCenterNotAvailableExamBookings", successfulButCenterNotAvailableExamBookings);
			sc.getServletContext().setAttribute("successfulButAlreadyBookedExamBookings", successfulButAlreadyBookedExamBookings);
			sc.getServletContext().setAttribute("conflictAmount", totalConflictAmount);
			sc.getServletContext().setAttribute("noOfConflictTransactions", noOfConflictTransactions);
			sc.getServletContext().setAttribute("refundDueAmount", refundDueAmount);
			sc.getServletContext().setAttribute("noOfPendingRefunds", noOfPendingRefunds);

		}catch(Exception e){
			
		}
	}*/
	
	
	@Scheduled(fixedDelay=20*60*1000)
	public void doAutoBookingForProjectConflictTransactions(){

		if(!"tomcat6".equalsIgnoreCase(SERVER) || !"PROD".equalsIgnoreCase(ENVIRONMENT)){
			return;
		}
		logger.info("Started autobooking scheduler for project...");
		
		ExamBookingDAO dao = (ExamBookingDAO)act.getBean("examBookingDAO");
		ProjectSubmissionDAO pDao = (ProjectSubmissionDAO)act.getBean("projectSubmissionDAO");
		boolean isProjectRegistraionLive = pDao.isProjectLive("Project");//added new **************
		boolean isModuleProjectRegistraionLive = pDao.isProjectLive("Module 4 - Project");//added new **************
		if(!isProjectRegistraionLive && !isModuleProjectRegistraionLive){
			return;
		}
		logger.info("isProjectRegistraionLive..."+isProjectRegistraionLive);
		logger.info("isModuleProjectRegistraionLive..."+isModuleProjectRegistraionLive);
		int noOfConflictTransactions = 0;
		int noOfPendingRefunds = 0;
		double totalConflictAmount = 0;
		double refundDueAmount = 0;

		try{
			ArrayList<ExamBookingTransactionBean> unSuccessfulExamBookings = new ArrayList<>();
			ArrayList<ExamBookingTransactionBean> successfulExamBookings = new ArrayList<>();
			ArrayList<ExamBookingTransactionBean> transactionFailedExamBookings = new ArrayList<>();
			List<ExamBookingTransactionBean> transactionPendingExamBookings = new ArrayList<>(); 
			
			if(isProjectRegistraionLive || isModuleProjectRegistraionLive){
				 unSuccessfulExamBookings =dao.getAllUnSuccessfulProjectBookings();//added new ********************
			}
			logger.info("number of bookings to process: "+unSuccessfulExamBookings.size());
			pendingPaymentsLoop :  for (int i = 0; i < unSuccessfulExamBookings.size(); i++) {
				ExamBookingTransactionBean bean = unSuccessfulExamBookings.get(i);
				logger.info(" {} {} booking transaction status : {}, booked : {} ", bean.getSapid(),bean.getTrackId(),
						bean.getTranStatus(), bean.getBooked());
				String trackId = bean.getTrackId();
				
				/*XMLParser parser = new XMLParser();
				String xmlResponse = parser.queryTransactionStatus(trackId, ACCOUNT_ID, SECURE_SECRET);
				
				try{
				parser.parseResponse(xmlResponse, bean);
				}catch(Exception e ){
					
				}
				String transactionType = bean.getTransactionType();
				String status = bean.getStatus();
				String error = bean.getError();
				String errorCode = bean.getErrorCode();
				
				if(("Authorized".equalsIgnoreCase(transactionType) || "Captured".equalsIgnoreCase(transactionType))&& "Processed".equalsIgnoreCase(status)){
					successfulExamBookings.add(bean);
				}else if("AuthFailed".equalsIgnoreCase(transactionType)&& "Processed".equalsIgnoreCase(status)){
					
					transactionFailedExamBookings.add(bean);
				}else if("3".equals(errorCode) && ("Invalid Refrence No".equals(error) || "Invalid Reference No".equals(error))){
					transactionFailedExamBookings.add(bean);
				}else{
					transactionIncompletedExamBookings.add(bean);
				}*/
				TransactionsExamBean txnExamBean = new TransactionsExamBean();
				String url = SERVER_PATH+"paymentgateways/m/getTransactionStatus";
				HttpHeaders headers = new HttpHeaders();
				
				txnExamBean.setTrack_id(trackId);
				headers.setAccept(Arrays.asList(MediaType.APPLICATION_JSON));
				
				try {
//				txnExamBean.setPayment_option(bean.getPaymentOption());
					HttpEntity<TransactionsExamBean> entity = new HttpEntity<TransactionsExamBean>(txnExamBean,headers);
					
					txnExamBean = restTemplate.exchange(url, HttpMethod.POST, entity, TransactionsExamBean.class).getBody();
					
				} catch (Exception e) {
					logger.info(" {} {} ERROR while fetching transaction details from {} : {} ", bean.getSapid(), bean.getTrackId(),
							url, Throwables.getStackTraceAsString(e));
					continue pendingPaymentsLoop;
				}
				
				logger.info(" {} {} : response from payment gateways : {}", bean.getSapid(), bean.getTrackId(), txnExamBean);

				if(PAYMENT_SUCCESS.equalsIgnoreCase(txnExamBean.getTransaction_status())) {

					bean.setResponseMessage(txnExamBean.getResponse_message());
					bean.setTransactionID(txnExamBean.getTransaction_id());
					bean.setRequestID(txnExamBean.getRequest_id());
					bean.setMerchantRefNo(txnExamBean.getMerchant_ref_no());
					bean.setSecureHash(txnExamBean.getSecure_hash());
					bean.setRespAmount(txnExamBean.getResponse_amount());
					bean.setDescription(txnExamBean.getDescription());
					bean.setResponseCode(txnExamBean.getResponse_code());
					bean.setRespPaymentMethod(txnExamBean.getResponse_payment_method());
					bean.setPaymentID(txnExamBean.getPayment_id());
					bean.setRespTranDateTime(txnExamBean.getResponse_transaction_date_time());
					bean.setPaymentOption(txnExamBean.getPayment_option());
					bean.setBankName(txnExamBean.getBank_name());

					successfulExamBookings.add(bean);
				}
				else if(PAYMENT_FAILED.equalsIgnoreCase(txnExamBean.getTransaction_status())) {
					bean.setError(txnExamBean.getError());
					bean.setPaymentOption(txnExamBean.getPayment_option());
					transactionFailedExamBookings.add(bean);
				}
				else if(PAYMENT_PENDING.equalsIgnoreCase(txnExamBean.getTransaction_status())) {
					bean.setError(txnExamBean.getError());
					bean.setPaymentOption(txnExamBean.getPayment_option());
					transactionPendingExamBookings.add(bean);
				}
				else if(PAYMENT_INVALID_REQUEST.equalsIgnoreCase(txnExamBean.getTransaction_status())) {
					bean.setError(txnExamBean.getError());
					transactionFailedExamBookings.add(bean);
				}

			}
			ArrayList<ExamBookingTransactionBean> successfulButAlreadyBookedProjectExamBookings = new ArrayList<>();
			
			logger.info("number of successful : "+successfulExamBookings.size());
			String projectLastDate = "";
			for (int i = 0; i < successfulExamBookings.size(); i++) {

				try {
						ExamBookingTransactionBean bean = successfulExamBookings.get(i);
						String sapid = bean.getSapid();
						String trackId = bean.getTrackId();
						StudentExamBean student = dao.getSingleStudentWithValidity(sapid);
						
						ArrayList<String> alreadyBooked = new ArrayList<>();
				
						if(isProjectRegistraionLive && "Project".equals(bean.getSubject())){
							alreadyBooked = dao.getProjectBookedForStudent(sapid); //added new ***************************
							AssignmentFileBean assignmnetFile = new AssignmentFileBean();
							assignmnetFile.setSubject("Project");
							assignmnetFile.setConsumerProgramStructureId(student.getConsumerProgramStructureId());
//							assignmnetFile = pDao.findById(assignmnetFile); // Commented to make two cycle live
							assignmnetFile.setMonth(bean.getMonth());
							assignmnetFile.setYear(bean.getYear());
							assignmnetFile = pDao.findProjectGuidelinesForApplicableCycle(assignmnetFile);
							String endDate=assignmnetFile.getEndDate();
							endDate = endDate.replaceAll("T", " ");
							projectLastDate = endDate.substring(0, 10);
						}
						
						if(isModuleProjectRegistraionLive && "Module 4 - Project".equals(bean.getSubject())) {
							alreadyBooked.addAll(dao.getModuleProjectBookedForStudent(sapid)); //added new ***************************
							AssignmentFileBean assignmnetFile = new AssignmentFileBean();
							assignmnetFile.setSubject("Module 4 - Project");
							assignmnetFile.setConsumerProgramStructureId(student.getConsumerProgramStructureId());
//							assignmnetFile = pDao.findById(assignmnetFile); // Commented to make two cycle live
							assignmnetFile.setMonth(bean.getMonth());
							assignmnetFile.setYear(bean.getYear());
							assignmnetFile = pDao.findProjectGuidelinesForApplicableCycle(assignmnetFile);
							String endDate=assignmnetFile.getEndDate();
							endDate = endDate.replaceAll("T", " ");
							projectLastDate = endDate.substring(0, 10);
						}
	
						boolean subjectAlreadyBooked = false;
				
							if(alreadyBooked.contains("Project") || alreadyBooked.contains("Module 4 - Project")){
								subjectAlreadyBooked = true;
								logger.info(" {} {} subject already booked : {} ", bean.getSapid(),bean.getTrackId(),subjectAlreadyBooked);
							}
							
						if(subjectAlreadyBooked){
							bean.setAction("REFUND");
							successfulButAlreadyBookedProjectExamBookings.add(bean);
							totalConflictAmount += Double.parseDouble(bean.getRespAmount());
							refundDueAmount += Double.parseDouble(bean.getRespAmount());
							noOfConflictTransactions++;
							noOfPendingRefunds++;
						    dao.updateSeatsForAlreadyBookedConflictUsingSingleConnection(bean);
							continue;
						}

			
						List<ExamBookingTransactionBean> examBookings = dao.updateSeatsForConflictUsingSingleConnection(bean);
						logger.info("creating fee receipt for successful payment...");
						logger.info("for student:"+sapid+" and trackId:"+trackId);
						examBookingHelper.createAndUploadProjectFeeReceipt(sapid,trackId,bean.getSubject());  
						logger.info("sending mail");  
						MailSender mailSender = (MailSender)act.getBean("mailer");
						if(isProjectRegistraionLive){
						   mailSender.sendBookingSummaryEmailForProjectConflictBooking(student, examBookings, getExamCenterIdNameMap(),projectLastDate);
						}
					

					} catch (Exception e) {
						logger.info(e.getMessage());  
				}
			}
			
			//dao.markTransactionsFailed(transactionFailedExamBookings);
			logger.info("Failed transactions size:"+transactionFailedExamBookings.size());
			transactionFailedExamBookings.forEach((transactionFaildBean)->{
				try {
					dao.markTransactionsFailed(transactionFaildBean);
				}catch (Exception e) {
					logger.info("Exception while marking project failed transaction for:'"+transactionFaildBean.getTrackId()+"' "+e);
				}
			});
			
			logger.info("Pending transactions size:"+transactionPendingExamBookings.size());
			transactionPendingExamBookings.forEach((transactionPendingBean)->{
				try {
					dao.updatePendingTxnDetails(transactionPendingBean);
				} catch (Exception e) {
					logger.info("Exception while marking project pending transaction for:'"+transactionPendingBean.getTrackId()+"' "+e);
				}
			});
			
			if(successfulButAlreadyBookedProjectExamBookings != null && successfulButAlreadyBookedProjectExamBookings.size() > 0)
				{
					MailSender mailSender = (MailSender)act.getBean("mailer");
					mailSender.sendProjectConflictsEmail(successfulButAlreadyBookedProjectExamBookings);
					//dao.updateConfilctTransactionDetails(new ArrayList<ExamBookingTransactionBean>(), successfulButAlreadyBookedProjectExamBookings);
					dao.updateProjectConfilctTransactionDetails(successfulButAlreadyBookedProjectExamBookings);
				}
		
			
			sc.getServletContext().setAttribute("successfulButAlreadyBookedProjectExamBookings", successfulButAlreadyBookedProjectExamBookings);
			sc.getServletContext().setAttribute("conflictAmountProject", totalConflictAmount);
			sc.getServletContext().setAttribute("noOfConflictTransactionsProject", noOfConflictTransactions);
			sc.getServletContext().setAttribute("refundDueAmountProject", refundDueAmount);
			sc.getServletContext().setAttribute("noOfPendingRefundsProject", noOfPendingRefunds);

		}catch(Exception e){
			logger.info(e.getMessage()); 
		}
	}

	/*@Scheduled(fixedDelay=5*60*1000)
	public void getAmountMismatch(){

		try {
			ExamBookingDAO dao = (ExamBookingDAO)act.getBean("examBookingDAO");
			ArrayList<ExamBookingTransactionBean> getConfirmedBookingsForMonthAndYear = dao.getConfirmedBookingsForMonthAndYear("2016", "Dec");
			HashMap<String,ArrayList<String>> mapOfTrackIdAndSubjectList = new HashMap<String,ArrayList<String>>();
			HashMap<String,String> mapOfTrackIdAndAmount = new HashMap<String, String>();
			for(ExamBookingTransactionBean examBean : getConfirmedBookingsForMonthAndYear){
				String trackId = examBean.getTrackId();

				if(!mapOfTrackIdAndSubjectList.containsKey(trackId)){
					ArrayList<String> subjectList = new ArrayList<String>();
					subjectList.add(examBean.getSubject());
					mapOfTrackIdAndSubjectList.put(trackId, subjectList);
				}else{
					ArrayList<String> subjectListFromMap = mapOfTrackIdAndSubjectList.get(trackId); //If trackid exists
					subjectListFromMap.add(examBean.getSubject());
					mapOfTrackIdAndSubjectList.put(trackId, subjectListFromMap);
				}
				if(!mapOfTrackIdAndAmount.containsKey(trackId)){
					mapOfTrackIdAndAmount.put(examBean.getTrackId(),examBean.getAmount());
				}

			}
			for (String key : mapOfTrackIdAndSubjectList.keySet()) {
				String sapid = key.substring(0, 11);
				ArrayList<String> subjectList = mapOfTrackIdAndSubjectList.get(key);
				int amountApplicable = amountApplicable(sapid,subjectList);
				int amountFromMap = Integer.parseInt(mapOfTrackIdAndAmount.get(key));
				if(amountFromMap!=amountApplicable){
				}
			}
		} catch (Exception e) {
			
		}





	}*/
	public int amountApplicable(String sapid,ArrayList<String> subjectList){
		ExamBookingDAO dao = (ExamBookingDAO)act.getBean("examBookingDAO");
		ArrayList<StudentMarksBean> writtenAttemptsList = (ArrayList<StudentMarksBean>)dao.getWrittenAttempts(sapid);
		HashMap<String, String> subejctWrittenScoreMap = new HashMap<>();
		for (StudentMarksBean marksBean : writtenAttemptsList) {//This will store data if student ever had written attempt, for deciding exam fees
			if(marksBean.getWritenscore() != null ){
				subejctWrittenScoreMap.put(marksBean.getSubject(), marksBean.getWritenscore());
			}

		}
		int totalAmount = 0;
		for(String subject : subjectList){
			String writtenScore = subejctWrittenScoreMap.get(subject);
			if(writtenScore == null || "".equals(writtenScore.trim())){
				totalAmount += examFeesPerSubjectFirstAttempt;
			}else{
				totalAmount += examFeesPerSubjectResitAttempt;
			}
		}
		return totalAmount;
	}
	
	@Scheduled(fixedDelay=240*60*1000)
	public void sendConflictEmail(){


		if(!"tomcat6".equalsIgnoreCase(SERVER) || !"PROD".equalsIgnoreCase(ENVIRONMENT)){
			return;
		}
		ExamBookingDAO dao = (ExamBookingDAO)act.getBean("examBookingDAO");
		boolean isExamRegistraionLive = dao.isConfigurationLivePost1Day("Exam Registration");
		if(!isExamRegistraionLive){
			return;
		}
		ArrayList<ExamBookingTransactionBean> successfulButCenterNotAvailableExamBookings = (ArrayList<ExamBookingTransactionBean>)sc.getServletContext().getAttribute("successfulButCenterNotAvailableExamBookings");
		ArrayList<ExamBookingTransactionBean> successfulButAlreadyBookedExamBookings = (ArrayList<ExamBookingTransactionBean>)sc.getServletContext().getAttribute("successfulButAlreadyBookedExamBookings");

		if(
				(successfulButCenterNotAvailableExamBookings != null && successfulButCenterNotAvailableExamBookings.size() > 0) || 
				(successfulButAlreadyBookedExamBookings != null && successfulButAlreadyBookedExamBookings.size() > 0)
				){
			MailSender mailSender = (MailSender)act.getBean("mailer");
			mailSender.sendConflictsEmail(successfulButCenterNotAvailableExamBookings, successfulButAlreadyBookedExamBookings);
		}

	}

	// Changes Added by stef
	@Scheduled(fixedDelay=240*60*1000)
	public void sendProjectConflictEmail(){


		if(!"tomcat6".equalsIgnoreCase(SERVER) || !"PROD".equalsIgnoreCase(ENVIRONMENT)){
			return;
		}
		
		ProjectSubmissionDAO pDao = (ProjectSubmissionDAO)act.getBean("projectSubmissionDAO");

		boolean isProjectRegistrationLive = pDao.isProjectLive("Project");//added new ***********************************
		boolean isModuleProjectRegistrationLive = pDao.isProjectLive("Module 4 - Project");//added new ***********************************
		if(!isProjectRegistrationLive && !isModuleProjectRegistrationLive){
			return;
		}
		
		
		ArrayList<ExamBookingTransactionBean> successfulButAlreadyBookedProjectExamBookings = (ArrayList<ExamBookingTransactionBean>)sc.getServletContext().getAttribute("successfulButAlreadyBookedProjectExamBookings");

		if(
				(successfulButAlreadyBookedProjectExamBookings != null && successfulButAlreadyBookedProjectExamBookings.size() > 0) ){
			MailSender mailSender = (MailSender)act.getBean("mailer");
			mailSender.sendProjectConflictsEmail(successfulButAlreadyBookedProjectExamBookings);
		}

	}   
	
	
	public Map<String, String> getExamCenterIdNameMap(){
		ExamCenterDAO dao = (ExamCenterDAO)act.getBean("examCenterDAO");
		return dao.getExamCenterIdNameMap();
	}

	private boolean checkIfCenterStillAvailable( ArrayList<String> selectedCenters,StudentExamBean student) {
		ExamCenterDAO ecDao = (ExamCenterDAO)act.getBean("examCenterDAO");
		boolean centerStillAvailable = true;
		if("Offline".equals(student.getExamMode())){ //Since offline has no centers
			return true;
		}

		List<ExamCenterBean> availableCenters = ecDao.getAvailableCentersForRegularOnlineExam(student.getSapid(), 
				student.isCorporateExamCenterStudent(), 
				student.getMappedCorporateExamCenterId());//Logic is same now for regular and resit

		for (int i = 0; i < selectedCenters.size(); i++) {
			String subjectCenter = selectedCenters.get(i);

			String[] data = subjectCenter.split("\\|");

			String subject = data[0];
			String centerId = data[1];
			String examDate = data[2];
			String examStartTime = data[3];

			if("Project".equals(subject)){
				continue;//Center availability is not applicable for Project
			}
			if("Module 4 - Project".equals(subject)){
				continue;//Center availability is not applicable for Project
			}
			
			ArrayList<String> centerIdList = new ArrayList<>();
			for (ExamCenterBean center : availableCenters) {
				if(center.getDate().equals(examDate) && center.getStarttime().equals(examStartTime)){
					centerIdList.add(center.getCenterId());
				}
			}

			if(!centerIdList.contains(centerId)){
				centerStillAvailable = false;
				break;
			}
		}
		return centerStillAvailable;
	}

	/*private boolean checkIfCenterStillAvailable(StudentBean student, List<TimetableBean> timeTableList, ArrayList<String> selectedCenters) {
		String studentProgramStructure = student.getPrgmStructApplicable();
		Map<String, ArrayList<String>> subjectCenterIdListMap = new HashMap<String, ArrayList<String>>();
		ExamCenterDAO ecDao = (ExamCenterDAO)act.getBean("examCenterDAO");
		boolean centerStillAvailable = true;
		if("Jul2014".equals(studentProgramStructure) || "Jul2013".equals(studentProgramStructure)){
			subjectCenterIdListMap = ecDao.getAvailableCenterIDSForGivenSubjects(timeTableList);

			for (int i = 0; i < selectedCenters.size(); i++) {
				String subjectCenter = selectedCenters.get(i);
				String[] parts = subjectCenter.split("\\|");

				//String subject = subjectCenter.substring(0,subjectCenter.indexOf("|"));
				//String centerId = subjectCenter.substring(subjectCenter.indexOf("|")+1, subjectCenter.length() );

				String subject = parts[0];
				String centerId = parts[1];
				String startTime = parts[2];

				if("Project".equals(subject)){
					continue;//Center availability is not applicable for Project
				}

				ArrayList<String> centerIdList = subjectCenterIdListMap.get(subject + startTime);

				if(!centerIdList.contains(centerId)){
					centerStillAvailable = false;
					break;
				}
			}

			return centerStillAvailable;
		}else{
			return true;
		}
	}
	 */

	@Scheduled(fixedDelay=240*60*1000)
	public void findBookingsMismatch(){

		if(!"tomcat6".equalsIgnoreCase(SERVER) || !"PROD".equalsIgnoreCase(ENVIRONMENT)){
			return;
		}

		ExamBookingDAO dao = (ExamBookingDAO)act.getBean("examBookingDAO");
		boolean isExamRegistraionLive = dao.isConfigurationLivePost1Day("Exam Registration");
		if(!isExamRegistraionLive){
			return;
		}

		ExamCenterDAO ecDao = (ExamCenterDAO)act.getBean("examCenterDAO");
		try{

			ArrayList<ExamBookingTransactionBean> confirmedBookings = dao.getAllConfirmedBookings();
			List<ExamCenterBean> examCentersList = ecDao.getAllExamCenterSlots();

			HashMap<String, Integer> centerIdBookingsMap = new HashMap<>();

			int counter = 0;
			for (int i = 0; i < confirmedBookings.size(); i++) {
				ExamBookingTransactionBean bean = confirmedBookings.get(i);
				String centerId = bean.getCenterId();
				String examDate = bean.getExamDate();
				String examTime = bean.getExamTime();

				String key = centerId + examDate + examTime;
				if(!centerIdBookingsMap.containsKey(key)){
					centerIdBookingsMap.put(key, 1);
				}else{
					counter = centerIdBookingsMap.get(key);
					counter++;
					centerIdBookingsMap.put(key, counter);
				}
			} 

			String mismatch = "";
			ArrayList<ExamCenterBean> mismatchList = new ArrayList<>();
			for (int i = 0; i < examCentersList.size(); i++) {
				ExamCenterBean bean = examCentersList.get(i);
				String centerId = bean.getCenterId();
				String examDate = bean.getDate();
				String examTime = bean.getStarttime();
				int booked = bean.getBooked();
				String centerName = bean.getExamCenterName();
				String key = centerId + examDate + examTime;
				if(centerIdBookingsMap.containsKey(key)){
					counter = centerIdBookingsMap.get(key);

					if(booked != counter){
						mismatch = mismatch + "Center Id " + centerId + " Center: "+ centerName + " Date: "+ examDate 
								+ " Time: "+ examTime + " Students Booked: "+ counter +" Center Booked: "+ booked +  " \n";
						bean.setSlotsBooked(counter);
						mismatchList.add(bean);
					}
				}
			}
			if(mismatchList.size() > 0){
				MailSender mailSender = (MailSender)act.getBean("mailer");
				mailSender.sendBookingsMismatchEmail(mismatchList);
			}

		}catch(Exception e){
			
		}

	}
	
	
	
	
	
	 //Added for checking double booking cases for subject as well as slots
	  /* @Scheduled(fixedDelay=240*60*1000)
	public void sendDoubleBookingForSubjectSlotEmail(){


		if(!"tomcat6".equalsIgnoreCase(SERVER) || !"PROD".equalsIgnoreCase(ENVIRONMENT)){
			return;
		}
		
		try{
		ExamBookingDAO dao = (ExamBookingDAO)act.getBean("examBookingDAO");
		boolean isExamRegistraionLive = dao.isConfigurationLive("Exam Registration");
		boolean isExtendedExamRegistraionLive = dao.isExtendedExamRegistrationConfigurationLive("Exam Registration");
		if(!isExamRegistraionLive && !isExtendedExamRegistraionLive){

			return;
		}
		ArrayList<ExamBookingTransactionBean> successfulButSameSlotTwiceExamBookings = sameSlotTwiceBooking();
		ArrayList<ExamBookingTransactionBean> successfulButSameSubjectTwiceExamBookings = sameSubjectTwiceBooking() ;

		if(
				(successfulButSameSlotTwiceExamBookings != null && successfulButSameSlotTwiceExamBookings.size() > 0) || 
				(successfulButSameSubjectTwiceExamBookings != null && successfulButSameSubjectTwiceExamBookings.size() > 0)
				){
			MailSender mailSender = (MailSender)act.getBean("mailer");
			mailSender.sendTwiceBookedEmail(successfulButSameSlotTwiceExamBookings, successfulButSameSubjectTwiceExamBookings);
		}

		}catch(Exception e){
		
		}
		
		
		
	}*/
	 //Changes addded by stef
	@Scheduled(fixedDelay=30*60*1000)
	public void sendDoubleBookingForSubjectSlotEmail(){


		if(!"tomcat6".equalsIgnoreCase(SERVER) || !"PROD".equalsIgnoreCase(ENVIRONMENT)){
			return;
		}
		
		try{
		ExamBookingDAO dao = (ExamBookingDAO)act.getBean("examBookingDAO");
		ProjectSubmissionDAO pDao = (ProjectSubmissionDAO)act.getBean("projectSubmissionDAO");
		boolean isExamRegistraionLive = dao.isConfigurationLive("Exam Registration");
		boolean isExtendedExamRegistraionLive = dao.isExtendedExamRegistrationConfigurationLive("Exam Registration");
		boolean isProjectRegistrationLive = pDao.isProjectLive("Project");//added new ***********************************
		boolean isModuleProjectRegistrationLive = pDao.isProjectLive("Module 4 - Project");//added new ***********************************
		if(!isExamRegistraionLive && !isExtendedExamRegistraionLive && !isProjectRegistrationLive && !isModuleProjectRegistrationLive){
			return;
		}
		//ArrayList<ExamBookingTransactionBean> successfulButSameSlotTwiceExamBookings = sameSlotTwiceBooking(isExamRegistraionLive,isExtendedExamRegistraionLive,isProjectRegistrationLive);
		//ArrayList<ExamBookingTransactionBean> successfulButSameSubjectTwiceExamBookings = sameSubjectTwiceBooking(isExamRegistraionLive,isExtendedExamRegistraionLive,isProjectRegistrationLive) ;
		
		ArrayList<ExamBookingTransactionBean> doubleBookingList = sameSubjectSlotTwiceBooking(isExamRegistraionLive,isExtendedExamRegistraionLive,isProjectRegistrationLive, isModuleProjectRegistrationLive) ;
		
		/*if(
			(successfulButSameSlotTwiceExamBookings != null && successfulButSameSlotTwiceExamBookings.size() > 0) || 
			(successfulButSameSubjectTwiceExamBookings != null && successfulButSameSubjectTwiceExamBookings.size() > 0)
		){
			MailSender mailSender = (MailSender)act.getBean("mailer");
			mailSender.sendTwiceBookedEmail(successfulButSameSlotTwiceExamBookings, successfulButSameSubjectTwiceExamBookings);
		}*/
		
		if(doubleBookingList != null && doubleBookingList.size() > 0){
				MailSender mailSender = (MailSender)act.getBean("mailer");
				mailSender.sendTwiceBookedEmail(doubleBookingList);
			}

		}catch(Exception e){
		
		}
		
	}
	
	// changes added by stef
	/* private ArrayList<ExamBookingTransactionBean> sameSlotTwiceBooking(boolean examReg,boolean extendExamReg,boolean proj){
	
		ExamBookingDAO dao = (ExamBookingDAO)act.getBean("examBookingDAO");
		
		HashMap<String,ExamBookingTransactionBean> allBookingsMap = new HashMap<String, ExamBookingTransactionBean>();
		 ArrayList<ExamBookingTransactionBean> twiceBookings = new ArrayList<ExamBookingTransactionBean>();
		 try{
		ArrayList<ExamBookingTransactionBean> confirmedBookings = new ArrayList<ExamBookingTransactionBean>(); 
		if(examReg || extendExamReg){
			confirmedBookings = dao.getAllConfirmedBookings();
		}else if(proj){
			confirmedBookings = dao.getAllConfirmedProjectBookings();
		}
		 for(ExamBookingTransactionBean bean :confirmedBookings ){
			String key = bean.getSapid()+"|"+bean.getExamDate()+"|"+bean.getExamTime();
			if(!allBookingsMap.containsKey(key)){
				allBookingsMap.put(key, bean);
			}else{
				twiceBookings.add(bean);
			}
		 }
		 }catch(Exception e){
		  	
		 }
		return twiceBookings;
	}
	  private ArrayList<ExamBookingTransactionBean> sameSubjectTwiceBooking(boolean examReg,boolean extendExamReg,boolean proj){
		
		ExamBookingDAO dao = (ExamBookingDAO)act.getBean("examBookingDAO"); 
		ArrayList<ExamBookingTransactionBean> confirmedBookings = new ArrayList<ExamBookingTransactionBean>(); 
		if(examReg || extendExamReg){
			confirmedBookings = dao.getAllConfirmedBookings();
		}else if(proj){
			confirmedBookings = dao.getAllConfirmedProjectBookings();
		}
		 HashMap<String,ExamBookingTransactionBean> allBookingsMap = new HashMap<String, ExamBookingTransactionBean>();
		 ArrayList<ExamBookingTransactionBean> twiceBookings = new ArrayList<ExamBookingTransactionBean>();
		 for(ExamBookingTransactionBean bean :confirmedBookings ){
			String key = bean.getSapid()+"|"+bean.getSubject();
			if(!allBookingsMap.containsKey(key)){
				allBookingsMap.put(key, bean);
			}else{
				twiceBookings.add(bean);
			}
		 }
		return twiceBookings;
	}*/
	  private ArrayList<ExamBookingTransactionBean> sameSubjectSlotTwiceBooking(boolean examReg,boolean extendExamReg,boolean proj, boolean moduleProj){
			
			ExamBookingDAO dao = (ExamBookingDAO)act.getBean("examBookingDAO"); 
			ArrayList<ExamBookingTransactionBean> confirmedBookings = new ArrayList<ExamBookingTransactionBean>(); 
			ArrayList<ExamBookingTransactionBean> confirmedDoubleBookings = new ArrayList<ExamBookingTransactionBean>(); 
			if(examReg || extendExamReg){
				confirmedBookings = dao.getAllConfirmedBookings();
			}else if(proj || moduleProj){
				confirmedBookings = dao.getAllConfirmedProjectBookings();
			}
			 HashMap<String,ExamBookingTransactionBean> allBookingsMap = new HashMap<String, ExamBookingTransactionBean>();
			 ArrayList<String> twiceBookingStudentList = new ArrayList<String>();
			 for(ExamBookingTransactionBean bean :confirmedBookings ){
				String key = bean.getSapid()+"|"+bean.getSubject();
				String key2 = bean.getSapid()+"|"+bean.getExamDate()+"|"+bean.getExamTime();
				if(!allBookingsMap.containsKey(key)){
					allBookingsMap.put(key, bean);
				}else if(!twiceBookingStudentList.contains(bean.getSapid())){
					twiceBookingStudentList.add(bean.getSapid());
				}
				
				if(!allBookingsMap.containsKey(key2)){
					allBookingsMap.put(key2, bean);
				}else if(!twiceBookingStudentList.contains(bean.getSapid())){
					twiceBookingStudentList.add(bean.getSapid());
				}
			 }
			 
			 if(twiceBookingStudentList.size()>0 && twiceBookingStudentList != null){
				 if(examReg || extendExamReg){
					 confirmedDoubleBookings = dao.getConfirmedDoubleBooking(twiceBookingStudentList);
					}else if(proj){
						confirmedDoubleBookings = dao.getAllConfirmedDoubleProjectBookings(twiceBookingStudentList);
					}
			 }
			 
			 
			 
			return confirmedDoubleBookings;
		}
	//end

}
