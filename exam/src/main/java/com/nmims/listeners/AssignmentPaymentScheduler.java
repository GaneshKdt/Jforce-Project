package com.nmims.listeners;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

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
import org.springframework.web.client.RestTemplate;
import org.springframework.web.context.ServletConfigAware;

import com.nmims.beans.ExamBookingTransactionBean;
import com.nmims.beans.StudentExamBean;
import com.nmims.beans.TransactionsExamBean;
import com.nmims.daos.AssignmentsDAO;
import com.nmims.daos.TestDAO;
import com.nmims.helpers.ExamBookingHelper;
import com.nmims.helpers.MailSender;
import com.nmims.helpers.PaymentHelper;

@Service("assignmentPaymentScheduler")
public class AssignmentPaymentScheduler implements ApplicationContextAware, ServletConfigAware {

	@Value("${SECURE_SECRET}")
	private String SECURE_SECRET; // secret key;
	@Value("${ACCOUNT_ID}")
	private String ACCOUNT_ID;

	@Value("${SERVER}")
	private String SERVER;

	@Value("${ENVIRONMENT}")
	private String ENVIRONMENT;
	
	@Value("${SERVER_PATH}")
	private String SERVER_PATH;

	private static ApplicationContext act = null;
	private static ServletConfig sc = null;
	
	@Autowired
	ExamBookingHelper examBookingHelper;
	
	@Autowired
	private RestTemplate restTemplate;
	
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
	private static final Logger logger = LoggerFactory.getLogger("examBookingPayments");
	
	private final String PAYMENT_SUCCESS = "Payment Successfull";
	private final String PAYMENT_FAILED = "Payment Failed";
	private final String PAYMENT_PENDING = "Payment Pending";
	private final String PAYMENT_INVALID_REQUEST = "Invalid Request";
	
	@Scheduled(fixedDelay = 60 * 60 * 1000)
	public void doAutoBookingForConflictTransactions() {

		if (!"tomcat4".equalsIgnoreCase(SERVER) || !"PROD".equalsIgnoreCase(ENVIRONMENT)) {
			return;
		}
		logger.info("started assignment autobooking scheduler.");
		/*
		 * ExamBookingDAO dao = (ExamBookingDAO)act.getBean("examBookingDAO"); boolean
		 * isExamRegistraionLive =
		 * dao.isConfigurationLivePost1Day("Re-sit Exam Registation");
		 * if(!isExamRegistraionLive){ 
		 *  return; }
		 */

		AssignmentsDAO dao = (AssignmentsDAO) act.getBean("asignmentsDAO");
		try {

			ArrayList<ExamBookingTransactionBean> unSuccessfulExamBookings = dao.getAllUnSuccessfulAssignmentPayments();
			ArrayList<ExamBookingTransactionBean> successfulAssignmentPayments = new ArrayList<>();
			ArrayList<ExamBookingTransactionBean> transactionFailedList = new ArrayList<>();
			List<ExamBookingTransactionBean> transactionPendingList = new ArrayList<>();

			if (unSuccessfulExamBookings != null) {
			}
			logger.info("number of unsuccessful payments: "+unSuccessfulExamBookings.size());
			for (int i = 0; i < unSuccessfulExamBookings.size(); i++) {
				ExamBookingTransactionBean bean = unSuccessfulExamBookings.get(i);
				String trackId = bean.getTrackId();
				
				/*XMLParser parser = new XMLParser();
				String xmlResponse = parser.queryTransactionStatus(trackId, ACCOUNT_ID, SECURE_SECRET);
				parser.parseResponse(xmlResponse, bean);
				String transactionType = bean.getTransactionType();
				String status = bean.getStatus();
				String error = bean.getError();
				String errorCode = bean.getErrorCode();
				
				if (("Authorized".equalsIgnoreCase(transactionType) || "Captured".equalsIgnoreCase(transactionType))
						&& "Processed".equalsIgnoreCase(status)) {
					successfulAssignmentPayments.add(bean);
				} else if ("3".equals(errorCode)
						&& ("Invalid Refrence No".equals(error) || "Invalid Reference No".equals(error))) {
					transactionFailedList.add(bean);
				}*/
				
				TransactionsExamBean txnExamBean = new TransactionsExamBean();
				txnExamBean.setTrack_id(trackId);
//				txnExamBean.setPayment_option(bean.getPaymentOption());
				
				String url = SERVER_PATH+"paymentgateways/m/getTransactionStatus";
				
				HttpHeaders headers = new HttpHeaders();
				
				headers.setAccept(Arrays.asList(MediaType.APPLICATION_JSON));

				HttpEntity<TransactionsExamBean> entity = new HttpEntity<TransactionsExamBean>(txnExamBean,headers);


				txnExamBean = restTemplate.exchange(url, HttpMethod.POST, entity, TransactionsExamBean.class).getBody();
				
				logger.info("After Gateway call:"+trackId +" - "+txnExamBean);

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

					successfulAssignmentPayments.add(bean);
				}
				else if(PAYMENT_FAILED.equalsIgnoreCase(txnExamBean.getTransaction_status())) {
					bean.setError(txnExamBean.getError());
					bean.setPaymentOption(txnExamBean.getPayment_option());
					transactionFailedList.add(bean);
				}
				else if(PAYMENT_PENDING.equalsIgnoreCase(txnExamBean.getTransaction_status())) {
					bean.setError(txnExamBean.getError());
					bean.setPaymentOption(txnExamBean.getPayment_option());
					transactionPendingList.add(bean);
				}
				else if(PAYMENT_INVALID_REQUEST.equalsIgnoreCase(txnExamBean.getTransaction_status())) {
					bean.setError(txnExamBean.getError());
					transactionFailedList.add(bean);
				}

			}
			
            logger.info("Number of successful payments: "+successfulAssignmentPayments.size());
			for (int i = 0; i < successfulAssignmentPayments.size(); i++) {
				ExamBookingTransactionBean bean = successfulAssignmentPayments.get(i);
				String sapid = bean.getSapid();
				String trackId = bean.getTrackId();
				StudentExamBean student = dao.getSingleStudentWithValidity(sapid);
				List<ExamBookingTransactionBean> examBookings = dao.updateSeatsForConflictUsingSingleConnection(bean);
				dao.setPaymentStatusInQuickTable(bean);
				ArrayList<String> subjects = new ArrayList<String>();
				String examPeriod = null;
				for (ExamBookingTransactionBean examBookingTransactionBean : examBookings) {
					subjects.add(examBookingTransactionBean.getSubject());
					examPeriod = examBookingTransactionBean.getMonth() + "-" + examBookingTransactionBean.getYear();
				}
				try {
					examBookingHelper.createAndUploadAssignmentFeeReceipt(sapid,trackId);
				} catch (Exception e) { 
					logger.info(e.getMessage());
				} 
				logger.info("Sending mail..");
				MailSender mailSender = (MailSender) act.getBean("mailer");
				mailSender.sendAssignmentBookingSummaryEmail(student, subjects, examPeriod);
			}

			//dao.markTransactionsFailed(transactionFailedList);
			logger.info("Failed assignment transactions:"+transactionFailedList.size());
			transactionFailedList.forEach((transactionFaildBean)->{
				try {
					dao.markTransactionsFailed(transactionFaildBean);
				}catch (Exception e) {
					logger.info("Exception while marking failed transaction for:'"+transactionFaildBean.getTrackId()+"' "+e);
				}
			});
			
			logger.info("Pending assignment transactions:"+transactionPendingList.size());
			transactionPendingList.forEach((transactionPendingBean)->{
				try {
					dao.updatePendingTxnDetails(transactionPendingBean);
				} catch (Exception e) {
					logger.info("Exception while marking failed transaction for:'"+transactionPendingBean.getTrackId()+"' "+e);
				}
			});

		} catch (Exception e) {
			logger.info(e.getMessage());
		}

	}

//	 Added by Somesh  
//	 checking Twice payment received for assignments and send email

	@Scheduled(fixedDelay = 24 * 60 * 60 * 1000)
	public void sendEmailForTwiceAssignmentPaymentReceived() {


		if (!"tomcat4".equalsIgnoreCase(SERVER) || !"PROD".equalsIgnoreCase(ENVIRONMENT)) {
			return;
		}

		AssignmentsDAO dao = (AssignmentsDAO) act.getBean("asignmentsDAO");
		MailSender mailSender = (MailSender) act.getBean("mailer");
		try {
			ArrayList<ExamBookingTransactionBean> successfulAssignmentPayment = dao.getSccessfulAssignmentPayment();

			for (ExamBookingTransactionBean bean : successfulAssignmentPayment) {

				String sapid = bean.getSapid();
				StudentExamBean student = dao.getSingleStudentsData(sapid);
				mailSender.sendEmailForTwicePaymentReceived(student, bean, dao);
			}

		} catch (Exception e) {
			
		}

	}

	// Scheduler to send email remainders for internal assessment tests start
	/*
	 * @Scheduled(fixedDelay=2*60*60*1000) public void sendEmailForTestsReminders(){
	 * 
	 * 
	 * 
	 * if(!"tomcat4".equalsIgnoreCase(SERVER) ||
	 * !"PROD".equalsIgnoreCase(ENVIRONMENT)){ 
	 * return; }
	 * 
	 * 
	 * TestDAO dao = (TestDAO)act.getBean("testDao"); MailSender mailSender =
	 * (MailSender)act.getBean("mailer"); try { List<TestBean>
	 * testsScheduledForTomorrow = dao.getTestsScheduledForTomorrowNEmailNotSent();
	 * 
	 * for (TestBean test : testsScheduledForTomorrow) {
	 * 
	 * getTestName()); String startDate = test.getStartDate(); String endDate =
	 * test.getEndDate();
	 * SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	 * SimpleDateFormat sdf2 = new SimpleDateFormat("dd-MMMM-yyyy HH:mm"); Date
	 * sDate = sdf.parse(startDate.replaceAll("T", " ")); //Date cDate = new Date();
	 * //Date eDate = sdf.parse(endDate.replaceAll("T", " ")); String newStartDate =
	 * sdf2.format(sDate);
	 * 
	 * String startDateForMail = sdf2.format(sDate);
	 * 
	 * test.setStartDate(newStartDate);
	 * 
	 * List<StudentBean> studentsApllicableForTest =
	 * dao.getStudentsEligibleForTestByTestid(test.getId());
	 * 
	 * for(StudentBean student : studentsApllicableForTest) {
	 * mailSender.sendEmailForTestsReminder(student, test, dao); }
	 * 
	 * }
	 * 
	 * } catch (Exception e) {  }
	 * 
	 */
	// Scheduler to send email remainders for internal assessment tests start

}
