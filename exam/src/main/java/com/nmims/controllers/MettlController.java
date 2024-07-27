/**
 * 
 */
package com.nmims.controllers;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.nmims.beans.MettlRegisterCandidateBean;
import com.nmims.beans.MettlRegisterCandidateReportBean;
import com.nmims.beans.MettlScheduleAPIBean;
import com.nmims.beans.MettlScheduleExamBean;
import com.nmims.beans.ResponseBean;
import com.nmims.daos.MettlDAO;
import com.nmims.helpers.DateHelper;
import com.nmims.helpers.MailSender;
import com.nmims.helpers.MettlHelper;
import com.nmims.listeners.MettlScheduler;

/**
 * @author vil_m 
 *
 */
@Controller
public class MettlController extends BaseController {

	// start - PROGRAMMING Constants
	public static final String KEY_ERROR = "error";
	public static final String KEY_SUCCESS = "success";

	private static final String CONTENT_TYPE = "Content-Type";
	private static final String CONTENT_TYPE_JSON = "application/json";
	private static final String DATE_FORMAT_1 = "yyyy-MM-dd";

	public static final String CHAR_DASH = "-";

	public static final Integer REGISTRATION_CANDIDATE_BATCHSIZE_ONE = 1;
	public static final Integer RETRY_FOR_ERROR_AT_METTL = 1;
	// end - PROGRAMMING Constants

	// start - DB Constants
	private static final String CONSTANT_Y = "Y";

	public static final Integer REGISTRATION_STATUS_PASS = 0;
	public static final Integer REGISTRATION_STATUS_FAIL = 1;

	// end - DB Constants

	public static final Logger logger = LoggerFactory.getLogger("examRegisterPG");

	@Autowired(required = false)
	ApplicationContext act;

	@Value("${PG_METTL_PUBLIC_KEY}")
	private String PG_METTL_PUBLIC_KEY;

	@Value("${PG_METTL_PRIVATE_KEY}")
	private String PG_METTL_PRIVATE_KEY;

	@Value("${MettlBaseUrl}")
	private String MettlBaseUrl;

	@Value("${START_METTL_TEST_DEMO}")
	private String START_METTL_TEST_DEMO;

	@Value("${END_METTL_TEST_DEMO}")
	private String END_METTL_TEST_DEMO;

	@Value("${START_METTL_TEST_PROD}")
	private String START_METTL_TEST_PROD;

	@Value("${END_METTL_TEST_PROD}")
	private String END_METTL_TEST_PROD;

	@Value("${GRADED_NOTIFICATION_METTL_TEST_PROD}")
	private String GRADED_NOTIFICATION_METTL_TEST_PROD;

	@Value("${RESUME_ENABLED_METTL_TEST_PROD}")
	private String RESUME_ENABLED_METTL_TEST_PROD;

	@Value("${PG_NO_IMAGE_URL}")
	private String PG_NO_IMAGE_URL;
	
	@Value("${LIST_SPECIAL_NEEDS_STUDENTS_URL}")
	private String LIST_SPECIAL_NEEDS_STUDENTS_URL;
	
	//extra time (in minutes), for Special Need Students
	@Value("${SPECIAL_NEEDS_STUDENTS_COMPENSATORY_TIME}")
	private String SPECIAL_NEEDS_STUDENTS_COMPENSATORY_TIME;
	
	@Value("${SERVER}")
	private String SERVER;
	
	@Autowired
	MettlDAO mettlDAO;

	@Autowired
	MettlHelper mettlHelper;

	@Autowired
	MailSender mailer;
	
	@Autowired
	MettlScheduler mettlScheduler;

	@RequestMapping(value = "/m/createAssessment", method = RequestMethod.GET, produces = "application/json")
	public ResponseEntity<ResponseBean> createAssessment() {
		ResponseBean responseBean = null;
		HttpHeaders headers = null;
		try {
			responseBean = (ResponseBean) new ResponseBean();
			headers = new HttpHeaders();
			headers.add("Content-Type", "application/json");

			// Demo - Authentication failed/Signature mismatch
			// mettlHelper.createAssessment("fb554940-a2f2-457c-a0c8-73058923a40b",
			// "16ee0dfb-c038-4abb-a080-ca34615f29fb");
			// Demo - The API key is not authorized for requested resource/action
			// mettlHelper.createAssessment("fb554940a2f2457ca0c873058923a40b",
			// "16ee0dfbc0384abba080ca34615f29fb");

			// Shiv - Production keys
			// mettlHelper.createAssessment("72cdd358e82c495e8ff640b2e9efc561",
			// "e62cdb6cbf3340208a56d779eeb3e388");
			mettlHelper.createAssessment("72cdd358-e82c-495e-8ff6-40b2e9efc561",
					"e62cdb6c-bf33-4020-8a56-d779eeb3e388");

			responseBean.setStatus(KEY_SUCCESS);
		} catch (Exception e) {
			

			responseBean.setStatus(KEY_ERROR);
			responseBean.setMessage("Error generating createAssessment.");
		}
		return new ResponseEntity<ResponseBean>(responseBean, headers, HttpStatus.OK);
	}
 
//	 @RequestMapping(value="/m/createSchedule", method = RequestMethod.GET , produces="application/json")
		public ResponseEntity<ResponseBean> createSchedule() {
			logger.info("Entering createSchedule");
			Boolean isError = Boolean.FALSE;
			int rowsProcessed = 0;
			int totalRows = -1;
			String userId = null;
			String sourceAppName = null;
			String message = null;
			ResponseBean responseBean = null;
			HttpHeaders headers = null;
			MettlScheduleExamBean queryBean = null;
			MettlScheduleExamBean dbBean = null;
			List<MettlScheduleExamBean> assessmentList = null;
			try {
				responseBean = (ResponseBean) new ResponseBean();
				responseBean.setStatus(KEY_SUCCESS);
				headers = new HttpHeaders();
				headers.add(CONTENT_TYPE, CONTENT_TYPE_JSON);

				userId = "System";
				sourceAppName = "NMIMSDummyTestApp";
				mettlHelper.setBaseUrl(MettlBaseUrl);
				mettlHelper.setPrivateKey(PG_METTL_PRIVATE_KEY);
				mettlHelper.setPublicKey(PG_METTL_PUBLIC_KEY);

				assessmentList = mettlDAO.fetchAssessmentsList();
				totalRows = assessmentList.size();
				logger.info("Total Schedules to be created : " + totalRows);

				for (int y = 0; y < totalRows; y++) {
					message = "---------Processing Schedule (Current,Total) : (" + y + "," + totalRows + ")";
					logger.info(message);
					queryBean = assessmentList.get(y);

					// For PRODUCTION
					// queryBean = fixedTimeSchedule();
					queryBean.setScheduleType(MettlScheduleAPIBean.FIXED);
					queryBean.setFixedAccessOptionSW(MettlScheduleAPIBean.EXACTTIME);

					queryBean.setTestStartNotificationUrl(START_METTL_TEST_PROD);
					queryBean.setTestFinishNotificationUrl(END_METTL_TEST_PROD);
					queryBean.setTestGradedNotificationUrl(GRADED_NOTIFICATION_METTL_TEST_PROD);
					queryBean.setTestResumeEnabledForExpiredTestURL(RESUME_ENABLED_METTL_TEST_PROD);

					dbBean = mettlHelper.createSchedule(queryBean, sourceAppName);
					if (KEY_ERROR.equalsIgnoreCase(dbBean.getStatus())) {
						responseBean.setStatus(KEY_ERROR);
						message = "Schedule(s) creation Failed for (Assessment,CustomURLid) : ("
								+ queryBean.getAssessmentId() + "," + queryBean.getCustomUrlId() + ") "
								+ dbBean.getMessage();
						break;
					} else {
						dbBean.setActive(MettlController.CONSTANT_Y);
						dbBean.setStartTime(queryBean.getDate() + " " + queryBean.getStartTime());
						dbBean.setEndTime(queryBean.getDate() + " " + queryBean.getEndTime());
						dbBean.setCreatedBy(userId);
						dbBean.setLastModifiedBy(userId);
						isError = mettlDAO.saveSchedule(dbBean, userId);
						if (!isError) {
							responseBean.setStatus(KEY_ERROR);
							message = "Save failed in DB.";
							break;
						}
					}
					rowsProcessed++;
				}
				if (rowsProcessed == assessmentList.size()) {
					message = "Total Schedule(s) created : " + rowsProcessed;
				}
				logger.info(message);
				responseBean.setMessage(message);
			} catch (Exception e) {
				
				responseBean.setStatus(KEY_ERROR);
				responseBean.setMessage("Schedule(s) creation Failed for (Assessment,CustomURLid) : ("
						+ queryBean.getAssessmentId() + "," + queryBean.getCustomUrlId() + ") with " + e.getMessage());
			} finally {
				assessmentList.clear();
				assessmentList = null;
				sourceAppName = null;
				dbBean = null;
				queryBean = null;
			}
			logger.info("Exiting createSchedule");
			return new ResponseEntity<ResponseBean>(responseBean, headers, HttpStatus.OK);
		}
		
//		@RequestMapping(value = "/m/createScheduleWithWaitingRoom", method = RequestMethod.GET, produces = "application/json")
		public ResponseEntity<ResponseBean> createScheduleWithWaitingRoom() {
			logger.info(" Entering createScheduleWithWaitingRoom ");
			Boolean isError = Boolean.FALSE;
			int rowsProcessed = 0;
			int totalRows = -1;
			String userId = null;
			String sourceAppName = null;
			String message = null;
			ResponseBean responseBean = null;
			HttpHeaders headers = null;
			MettlScheduleExamBean queryBean = null;
			MettlScheduleExamBean dbBean = null;
			List<MettlScheduleExamBean> assessmentList = null;
			try {
				responseBean = (ResponseBean) new ResponseBean();
				responseBean.setStatus(KEY_SUCCESS);
				headers = new HttpHeaders();
				headers.add(CONTENT_TYPE, CONTENT_TYPE_JSON);

				userId = "System";
				sourceAppName = "NMIMSDummyTestApp";
				mettlHelper.setBaseUrl(MettlBaseUrl);
				mettlHelper.setPrivateKey(PG_METTL_PRIVATE_KEY);
				mettlHelper.setPublicKey(PG_METTL_PUBLIC_KEY);

				assessmentList = mettlDAO.fetchAssessmentsListWaitingRoom();
				totalRows = assessmentList.size();
				logger.info("Total Schedules to be created : {}", totalRows);

				for (int y = 0; y < totalRows; y++) {
					message = "---------Processing Schedule (Current,Total) : (" + y + "," + totalRows + ")";
					logger.info(message);
					queryBean = assessmentList.get(y);

					// For PRODUCTION
					// queryBean = fixedTimeSchedule();

					// commenting because METTL waiting API requires DURATION or SCHEDULED as type
					// not fixed or always on
//							queryBean.setFixedAccessOptionSW(MettlScheduleAPIBean.EXACTTIME);

					// required test link type for enabling waiting room
					queryBean.setTestLinkType(MettlScheduleAPIBean.SCHEDULED);
					queryBean.setScheduleType(MettlScheduleAPIBean.FIXED);
					
					queryBean.setAudioProctoring(Boolean.TRUE);

					queryBean.setTestStartNotificationUrl(START_METTL_TEST_PROD);
					queryBean.setTestFinishNotificationUrl(END_METTL_TEST_PROD);
					queryBean.setTestGradedNotificationUrl(GRADED_NOTIFICATION_METTL_TEST_PROD);
					queryBean.setTestResumeEnabledForExpiredTestURL(RESUME_ENABLED_METTL_TEST_PROD);

					dbBean = mettlHelper.createScheduleWithWaitingRoom(queryBean, sourceAppName);
					if (KEY_ERROR.equalsIgnoreCase(dbBean.getStatus())) {
						responseBean.setStatus(KEY_ERROR);
						message = "Schedule(s) creation Failed for (Assessment,CustomURLid) : ("
								+ queryBean.getAssessmentId() + "," + queryBean.getCustomUrlId() + ") "
								+ dbBean.getMessage();
						break;
					} else {
						dbBean.setActive(MettlController.CONSTANT_Y);
						dbBean.setStartTime(queryBean.getDate() + " " + queryBean.getStartTime());
						dbBean.setEndTime(queryBean.getDate() + " " + queryBean.getEndTime());
						// reporting start and finish time
						dbBean.setReportingStartTime(queryBean.getDate() + " " + queryBean.getReportingStartTime());
						dbBean.setReportingFinishTime(queryBean.getDate() + " " + queryBean.getReportingFinishTime());
						
						dbBean.setScheduleEndTime(queryBean.getDate() + " " + queryBean.getScheduleEndTime());
						
						dbBean.setCreatedBy(userId);
						dbBean.setLastModifiedBy(userId);
						isError = mettlDAO.saveScheduleWaitingRoom(dbBean, userId);
						if (!isError) {
							responseBean.setStatus(KEY_ERROR);
							message = "Save failed in DB.";
							break;
						}
					}
					rowsProcessed++;
				}
				if (rowsProcessed == assessmentList.size()) {
					message = "Total Schedule(s) created : " + rowsProcessed;
				}
				logger.info(message);
				responseBean.setMessage(message);
			} catch (Exception e) {

				responseBean.setStatus(KEY_ERROR);
				responseBean.setMessage("Schedule(s) creation Failed for (Assessment,CustomURLid) : ("
						+ queryBean.getAssessmentId() + "," + queryBean.getCustomUrlId() + ") with " + e.getMessage());
			} finally {
				assessmentList.clear();
				assessmentList = null;
				sourceAppName = null;
				dbBean = null;
				queryBean = null;
			}
			logger.info(" Exiting createScheduleWithWaitingRoom ");
			return new ResponseEntity<ResponseBean>(responseBean, headers, HttpStatus.OK);
		}

	// WORKS
	MettlScheduleExamBean fixedTimeSchedule() {
		MettlScheduleExamBean dummyBean = null;
		// String[][] candidatesA = null;
		dummyBean = new MettlScheduleExamBean();

		dummyBean.setAssessmentId("404027");
		dummyBean.setCustomUrlId("Dummy14"); // setName

		dummyBean.setScheduleType(MettlScheduleAPIBean.FIXED);
		dummyBean.setDate("2020-07-07");
		dummyBean.setStartsOnDate("Tue, 07 Jul 2020");
		dummyBean.setStartTime("15:00:00");
		dummyBean.setEndTime2("16:00:00");
		dummyBean.setFixedAccessOptionSW(MettlScheduleAPIBean.EXACTTIME);

		dummyBean.setTestStartNotificationUrl(START_METTL_TEST_PROD);
		dummyBean.setTestFinishNotificationUrl(END_METTL_TEST_PROD);
		dummyBean.setTestGradedNotificationUrl(GRADED_NOTIFICATION_METTL_TEST_PROD);
		dummyBean.setTestResumeEnabledForExpiredTestURL(RESUME_ENABLED_METTL_TEST_PROD);
		return dummyBean;
	}

	// WORKS.
	MettlScheduleExamBean alwaysOnTimeSchedule() {
		MettlScheduleExamBean dummyBean = null;
		// String[][] candidatesA = null;
		dummyBean = new MettlScheduleExamBean();

		dummyBean.setAssessmentId("404027");
		dummyBean.setCustomUrlId("Dummy15"); // setName
		dummyBean.setDate("2020-07-07");// For table columns
		dummyBean.setStartTime("16:00:00");// For table columns
		dummyBean.setEndTime("17:00:00");// For table columns

		dummyBean.setTestStartNotificationUrl(START_METTL_TEST_DEMO);
		dummyBean.setTestFinishNotificationUrl(END_METTL_TEST_DEMO);
		dummyBean.setTestGradedNotificationUrl(GRADED_NOTIFICATION_METTL_TEST_PROD);
		dummyBean.setTestResumeEnabledForExpiredTestURL(RESUME_ENABLED_METTL_TEST_PROD);
		return dummyBean;
	}


	//@Deprecated
	/*@RequestMapping(value = "/m/registerCandidate", method = RequestMethod.GET, produces = "application/json")
	public ResponseEntity<ResponseBean> registerCandidate() {
		ResponseBean responseBean = null;
		HttpHeaders headers = null;
		MettlRegisterCandidateBean[] arr = null;
		MettlRegisterCandidateBean regCandiBean = null;
		List<MettlRegisterCandidateBean> regCandiBeanList = null;
		List<MettlRegisterCandidateBean> temp_regCandiBeanList = null;
		Boolean isError = Boolean.FALSE;
		MettlRegisterCandidateBean[] dataArr = null;
		String scheduleAccessKey = null;
		String sapId = null;
		int rowsProcessed = 0;
		int rowsWithError = 0;
		String message = null;
		String booked = null;
		String examDate = null;
		String examTime = null;
		String examEndTime = null;
		String year = null;
		String month = null;
		String subject = null;
		String emailAddress = null;
		String trackId = null;
		String testTaken = null;
		String firstName = null;
		String lastName = null;
		String examStartDateTime = null;
		String examEndDateTime = null;
		String accessStartDateTime = null;
		String accessEndDateTime = null;
		String sifySubjectCode = null;
		String scheduleName = null;
		String scheduleAccessURL = null;
		Boolean openLinkFlag = Boolean.FALSE;
		String pgUrlMettl = null;
		MettlRegisterCandidateBean candidateBean = null;
		Boolean finalSuccess = Boolean.TRUE;
		final int retryFinal = MettlController.RETRY_FOR_ERROR_AT_METTL;// On Error, number of times to retry sending
																		// same data.
		int retry = 0;
		final int retryFinalRemoteServer = 1;// On Error, number of times to retry if error at Remote Server.
		int retryRemoteServer = 0;
		int retriedRows = 0;
		Boolean retryAtMettl = Boolean.FALSE;
		Boolean configErrorMettl = Boolean.FALSE;// InvalidKey or UnSupportedEncoding errors, complete STOP of program.
		String nextDayDate = null;
		try {
			responseBean = (ResponseBean) new ResponseBean();
			responseBean.setStatus(MettlController.KEY_SUCCESS);
			headers = new HttpHeaders();
			headers.add(MettlController.CONTENT_TYPE, MettlController.CONTENT_TYPE_JSON);

			mettlHelper.setBaseUrl(MettlBaseUrl);
			mettlHelper.setPrivateKey(PG_METTL_PRIVATE_KEY);
			mettlHelper.setPublicKey(PG_METTL_PUBLIC_KEY);

			// Prepare Data
			//regCandiBeanList = new ArrayList<MettlRegisterCandidateBean>(); 
			//for(int a = 9; a < 10; a++) { 
			//regCandiBean = new MettlRegisterCandidateBean();
			//regCandiBean.setFirstName("Brian"+(a+1));
			//regCandiBean.setLastName("Niles"+(a+1));
			//regCandiBean.setSapId("77115000650");
			//regCandiBean.setScheduleAccessKey("1zuzjbnksg");
			//regCandiBean.setEmailAddress("niles"+(a+1)+"@xyz.com");
			//regCandiBean.setRegistrationImage("https://studentzone-ngasce.nmims.edu/StudentDocuments/0010o00002UtqM2/0010o00002UtqM2_bJuw_Picture.JPG"); 
			//regCandiBean.setCandidateImage("https://studentzone-ngasce.nmims.edu/StudentDocuments/0010o00002UtqM2/0010o00002UtqM2_bJuw_Picture.JPG"); 
			//regCandiBean.setOpenLinkFlag(Boolean.FALSE);
			//regCandiBean.setScheduleAccessURL("https://tests.mettl.com/authenticateKey/1zuzjbnksg");
			//regCandiBean.setSubject("Corporate Finance"); 
			//regCandiBean.setBooked("Y");
			//regCandiBean.setMonth("Jun"); 
			//regCandiBean.setYear("2020");
			//regCandiBean.setExamDate("2020-07-15"); 
			//regCandiBean.setExamTime("17:00:00");
			//regCandiBean.setExamEndTime("19:30:00");

			//regCandiBean.setTrackId("771150006501592693824712");
			//regCandiBean.setTestTaken(null); 
			//regCandiBean.setSifySubjectCode("102");
			//regCandiBean.setExamStartDateTime("2020-07-15 17:00:00");
			//regCandiBean.setExamEndDateTime("2020-07-15 18:00:00");
			//regCandiBean.setAccessStartDateTime("2020-07-15 17:00:00");
			//regCandiBean.setAccessEndDateTime("2020-07-15 22:00:00");
			//regCandiBean.setScheduleName("DummySchName");
			//regCandiBeanList.add(regCandiBean); 
			//}

			nextDayDate = fetchDate();
			regCandiBeanList = mettlDAO.fetchAll_Candidates_SchedulesList(nextDayDate);
			int batchSize = MettlController.REGISTRATION_CANDIDATE_BATCHSIZE_ONE;// 20

			logger.info("Total Candidates to be Registered : " + regCandiBeanList.size());

			if (batchSize == MettlController.REGISTRATION_CANDIDATE_BATCHSIZE_ONE) {
				for (int d = 0; d < regCandiBeanList.size(); d++) {
					message = "Adding Element : (S,F,R) (" + rowsProcessed + "," + rowsWithError + "," + retriedRows
							+ ") - " + d;
					logger.info(message);
					candidateBean = regCandiBeanList.get(d);
					scheduleAccessKey = candidateBean.getScheduleAccessKey();
					sapId = candidateBean.getSapId();
					emailAddress = candidateBean.getEmailAddress();
					subject = candidateBean.getSubject();
					booked = candidateBean.getBooked();
					year = candidateBean.getYear();
					month = candidateBean.getMonth();
					examDate = candidateBean.getExamDate();
					examTime = candidateBean.getExamTime();
					examEndTime = candidateBean.getExamEndTime();
					trackId = candidateBean.getTrackId();
					testTaken = candidateBean.getTestTaken();
					firstName = candidateBean.getFirstName();
					lastName = candidateBean.getLastName();
					examStartDateTime = candidateBean.getExamStartDateTime();
					examEndDateTime = candidateBean.getExamEndDateTime();
					accessStartDateTime = candidateBean.getAccessStartDateTime();
					accessEndDateTime = candidateBean.getAccessEndDateTime();
					sifySubjectCode = candidateBean.getSifySubjectCode();
					scheduleName = candidateBean.getScheduleName();
					scheduleAccessURL = candidateBean.getScheduleAccessURL();
					openLinkFlag = candidateBean.getOpenLinkFlag();
					if (openLinkFlag) {
						candidateBean.setCandidateImage(PG_NO_IMAGE_URL);
						candidateBean.setRegistrationImage(PG_NO_IMAGE_URL);
					}

					retryAtMettl = Boolean.FALSE;
					retry = retryFinal;
					retryRemoteServer = retryFinalRemoteServer;
					do {
						arr = mettlHelper.registerCandidate(candidateBean, scheduleAccessKey);
						if (arr.length == 1) {
							if (null != arr[0].getStatus()
									&& arr[0].getStatus().equalsIgnoreCase(MettlController.KEY_ERROR)) {
								logger.error("Error : " + d + " - " + arr[0].getMessage());

								message = arr[0].getMessage().replace("\"", "");
								if (message.contains("SHA Error") || message.contains("E000")
										|| message.contains("E607") || message.contains("E401")) {
									// E000-Some Error Occurred, E607-Candidate registration limit exceeded,
									// E401-Authentication failed/Signature mismatch
									configErrorMettl = Boolean.TRUE;
									finalSuccess = Boolean.FALSE;
									break;
								}
								responseBean.setStatus(arr[0].getStatus());
								responseBean.setMessage(arr[0].getMessage());
								mettlDAO.saveCandidateRegisteredMettlInfo(sapId, scheduleAccessKey,
										MettlController.REGISTRATION_STATUS_FAIL,
										(arr[0].getStatus() + "|" + replace(arr[0].getMessage())), null);
								finalSuccess = Boolean.FALSE;
								rowsWithError++;
								// isError = Boolean.TRUE;
								// break;
								if (message.contains("E028")) {
									if (retry != 0) {
										candidateBean.setCandidateImage(PG_NO_IMAGE_URL);
										candidateBean.setRegistrationImage(PG_NO_IMAGE_URL);
										retryAtMettl = Boolean.TRUE;
										retry = retry - 1;
										retriedRows = retriedRows + 1;
										message = "RETRY...Adding Element : (S,F,R) (" + rowsProcessed + ","
												+ rowsWithError + "," + retriedRows + ") - " + d;
										logger.info(message);
									} else {
										retryAtMettl = Boolean.FALSE;
									}
								} else {
									// retryAtMettl = Boolean.FALSE;
									if (retryRemoteServer != 0) {
										retryAtMettl = Boolean.TRUE;
										retryRemoteServer = retryRemoteServer - 1;
										retriedRows = retriedRows + 1;
										message = "RETRY...Adding Element : (S,F,R) (" + rowsProcessed + ","
												+ rowsWithError + "," + retriedRows + ") - " + d;
										logger.info(message);
									} else {
										retryAtMettl = Boolean.FALSE;
									}
								}
							} else {
								pgUrlMettl = arr[0].getUrl();
								mettlDAO.saveCandidateRegisteredMettlInfo(sapId, scheduleAccessKey,
										MettlController.REGISTRATION_STATUS_PASS,
										(arr[0].getStatus() + "|" + replace(arr[0].getMessage())), pgUrlMettl);
								mettlDAO.updateExamBooking(sapId, emailAddress, subject, booked, year, month, examDate,
										examTime, examEndTime);
								if (openLinkFlag || retryAtMettl) {
									pgUrlMettl = scheduleAccessURL;
								}
								mettlDAO.saveScheduleInfo(sapId, emailAddress, subject, year, month, trackId, testTaken,
										firstName, lastName, examStartDateTime, examEndDateTime, accessStartDateTime,
										accessEndDateTime, sifySubjectCode, scheduleName, scheduleAccessKey,
										pgUrlMettl);
								rowsProcessed++;
								retryAtMettl = Boolean.FALSE;
							}
						}
					} while (retryAtMettl);

					if (configErrorMettl) {
						message = "STOP...Adding Element : (S,F,R) (" + rowsProcessed + "," + rowsWithError + ","
								+ retriedRows + ") - " + d;
						logger.info(message);
						break;
					}
				}

				if (finalSuccess) {
					message = "Total Candidates Registered : Count (Success/Failure/Retried) : (" + rowsProcessed + "/"
							+ rowsWithError + "/" + retriedRows + ")";
					logger.info(message);
					responseBean.setStatus(MettlController.KEY_SUCCESS);
					responseBean.setMessage(message);
				} else {
					message = "Partial Success/Failure/Retried : Count (Success/Failure/Retried) : (" + rowsProcessed
							+ "/" + rowsWithError + "/" + retriedRows + ")";
					logger.info(message);
					responseBean.setStatus(MettlController.KEY_ERROR);
					responseBean.setMessage(message);
				}

				// Sending Email
				if (!regCandiBeanList.isEmpty()) {
					emailRegisterCandidateReport(nextDayDate);
				} else {
					message = "No Email Sent, since no candidates to be registered for the date, " + nextDayDate + ".";
					logger.info(message);
				}
			} else {
				int startIndex = 0;
				int endIndex = -1;
				dataArr = new MettlRegisterCandidateBean[regCandiBeanList.size()];
				temp_regCandiBeanList = new ArrayList<MettlRegisterCandidateBean>();

				int listSize = regCandiBeanList.size();
				int batchCount = listSize / batchSize;
				int remainCount = listSize % batchSize;

				endIndex = startIndex + batchSize;
				scheduleAccessKey = "1zuzjbnksg";// accessKey of a Schedule
				for (int b = 0; b < batchCount; b++) {
					logger.info("Batch : " + b);
					for (int j = startIndex; j < endIndex; j++) {
						logger.info("Adding Element : " + j);
						// if(1 == (endIndex - j)) {
						// }
						temp_regCandiBeanList.add(regCandiBeanList.get(j));
					}
					arr = mettlHelper.registerCandidate(temp_regCandiBeanList, scheduleAccessKey);
					temp_regCandiBeanList.clear();
					if (arr.length == 1) {
						if (null != arr[0].getStatus()
								&& arr[0].getStatus().equalsIgnoreCase(MettlController.KEY_ERROR)) {
							logger.error("Error in Batch : " + b + " - " + arr[0].getMessage());
							responseBean.setStatus(arr[0].getStatus());
							responseBean.setMessage(arr[0].getMessage());
							isError = Boolean.TRUE;
							break;
						} else {
							copyArray(arr, dataArr);
							startIndex = endIndex;
							endIndex = startIndex + batchSize;
						}
					} else {
						copyArray(arr, dataArr);
						startIndex = endIndex;
						endIndex = startIndex + batchSize;
					}
				}
				if (remainCount > 0 && !isError) {
					temp_regCandiBeanList.clear();
					endIndex = startIndex + remainCount;
					for (int k = startIndex; k < endIndex; k++) {
						logger.info("Remaining Element(s) : " + k);
						temp_regCandiBeanList.add(regCandiBeanList.get(k));
					}
					arr = mettlHelper.registerCandidate(temp_regCandiBeanList, scheduleAccessKey);
					if (arr.length == 1) {
						if (null != arr[0].getStatus()
								&& arr[0].getStatus().equalsIgnoreCase(MettlController.KEY_ERROR)) {
							logger.error("Error in Last few Elements : " + arr[0].getMessage());
							responseBean.setStatus(arr[0].getStatus());
							responseBean.setMessage(arr[0].getMessage());
							isError = Boolean.TRUE;
						} else {
							copyArray(arr, dataArr);
						}
					} else {
						copyArray(arr, dataArr);
					}
				}
				if (!isError) {
					responseBean.setStatus(MettlController.KEY_SUCCESS);
					responseBean.setMessage("Total Candidates Registered : " + dataArr.length);
					logger.info("Total Candidates Registered : " + dataArr.length);
				}
			}
	*/
			/*
			 * arr = mettlHelper.registerCandidate(regCandiBeanList,
			 * scheduleAccessKey);//accessKey of a Schedule
			 * 
			 * 
			 * if(arr.length == 1) { responseBean.setStatus(arr[0].getStatus());
			 * responseBean.setMessage(arr[0].getMessage()); } else { for(int z = 0; z <
			 * arr.length; z++) {  }
			 * responseBean.setStatus(KEY_SUCCESS);
			 * responseBean.setMessage("Total Candidates Registered : " + arr.length); }
			 */
	/*	} catch (Exception e) {
			
			responseBean.setStatus(MettlController.KEY_ERROR);
			responseBean.setMessage("Failure in registering Candidates : " + e.getMessage());
			logger.error("Failure in registering Candidates : " + e.getMessage());
		} finally {
			// emailRegisterCandidateReport(nextDayDate);

			message = null;
			booked = null;
			examDate = null;
			examTime = null;
			examEndTime = null;
			year = null;
			month = null;
			subject = null;
			emailAddress = null;
			scheduleAccessKey = null;
			sapId = null;
			candidateBean = null;
			trackId = null;
			testTaken = null;
			firstName = null;
			lastName = null;
			examStartDateTime = null;
			examEndDateTime = null;
			accessStartDateTime = null;
			accessEndDateTime = null;
			sifySubjectCode = null;
			scheduleName = null;
		}
		return new ResponseEntity<ResponseBean>(responseBean, headers, HttpStatus.OK);
	}*/

	void emailRegisterCandidateReport(String nextDayDate) {
		List<MettlRegisterCandidateReportBean> list1 = null;
		List<MettlRegisterCandidateReportBean> list2 = null;

		StringBuffer strBuf = null;
		String emailMessage = null;
		try {
			// MailSender mailSender = (MailSender)act.getBean("mailer");

			list1 = mettlDAO.fetchAll_Candidates_SummaryList(nextDayDate);
			list2 = mettlDAO.fetchAll_Candidates_ErrorList(nextDayDate);

			strBuf = new StringBuffer();
			strBuf.append("Dear Team, <br>");
			strBuf.append("Register Candidate, <br><br>");

			strBuf.append("<style type=text/css>");
			strBuf.append(".tg  {border-collapse:collapse;border-spacing:0;}");
			strBuf.append(
					".tg td{border-color:black;border-style:solid;border-width:1px;font-family:Arial, sans-serif;font-size:14px;");
			strBuf.append("overflow:hidden;padding:10px 5px;word-break:normal;}");
			strBuf.append(
					".tg th{border-color:black;border-style:solid;border-width:1px;font-family:Arial, sans-serif;font-size:14px;");
			strBuf.append("font-weight:normal;overflow:hidden;padding:10px 5px;word-break:normal;}");
			strBuf.append(
					".tg .tg-af47{background-color:#ffffff;border-color:inherit;color:#000000;text-align:center;vertical-align:top}");
			strBuf.append(
					".tg .tg-vvj0{background-color:#ecf4ff;border-color:inherit;color:#000000;font-weight:bold;text-align:center;vertical-align:top}");
			strBuf.append("</style>");

			if (null != list1 && !list1.isEmpty()) {
				strBuf.append("<table class=tg>");
				strBuf.append("<thead>");
				strBuf.append("<tr>");
				strBuf.append("<th class=tg-vvj0 colspan=2>Exam Bookings</th>");
				strBuf.append("<th class=tg-vvj0>Processed</th>");
				strBuf.append("<th class=tg-vvj0 colspan=2>Slot 1</th>");
				strBuf.append("<th class=tg-vvj0 colspan=2>Slot 2</th>");
				strBuf.append("<th class=tg-vvj0 colspan=2>Slot 3</th>");
				strBuf.append("<th class=tg-vvj0 colspan=2>Un Processed</th>");
				strBuf.append("</tr>");
				strBuf.append("</thead>");
				strBuf.append("<tbody>");
				strBuf.append("<tr>");
				strBuf.append("<td class=tg-vvj0>Date(yyyy-MM-dd)</td>");
				strBuf.append("<td class=tg-vvj0>Total Candidate(s)#</td>");
				strBuf.append("<td class=tg-vvj0>Total Candidate(s)#</td>");
				strBuf.append("<td class=tg-vvj0>Success#</td>");
				strBuf.append("<td class=tg-vvj0>Error#</td>");
				strBuf.append("<td class=tg-vvj0>Success#</td>");
				strBuf.append("<td class=tg-vvj0>Error#</td>");
				strBuf.append("<td class=tg-vvj0>Success#</td>");
				strBuf.append("<td class=tg-vvj0>Error#</td>");
				strBuf.append("<td class=tg-vvj0>Error(s)#</td>");
				strBuf.append("<td class=tg-vvj0>Failure(s)#</td>");
				strBuf.append("</tr>");
				strBuf.append("<tr>");
				strBuf.append("<td class=tg-af47>").append(list1.get(0).getExamDate()).append("</td>");
				strBuf.append("<td class=tg-af47>").append(list1.get(0).getTotalCandidatesExamBookings())
						.append("</td>");
				strBuf.append("<td class=tg-af47>").append(list1.get(0).getTotalCandidates()).append("</td>");
				strBuf.append("<td class=tg-af47>").append(list1.get(0).getSuccessSlot1()).append("</td>");
				strBuf.append("<td class=tg-af47>").append(list1.get(0).getErrorSlot1()).append("</td>");
				strBuf.append("<td class=tg-af47>").append(list1.get(0).getSuccessSlot2()).append("</td>");
				strBuf.append("<td class=tg-af47>").append(list1.get(0).getErrorSlot2()).append("</td>");
				strBuf.append("<td class=tg-af47>").append(list1.get(0).getSuccessSlot3()).append("</td>");
				strBuf.append("<td class=tg-af47>").append(list1.get(0).getErrorSlot3()).append("</td>");
				strBuf.append("<td class=tg-af47>").append(list1.get(0).getTotalErrors()).append("</td>");
				strBuf.append("<td class=tg-af47>").append(list1.get(0).getTotalFailures()).append("</td>");
				strBuf.append("</tr>");
				strBuf.append("</tbody>");
				strBuf.append("</table>");
				strBuf.append("<br>");
			}

			if (null != list2) {
				strBuf.append("<table class=tg>");
				strBuf.append("<thead>");
				strBuf.append("<tr>");
				strBuf.append("<th class=tg-vvj0>SAP Id</th>");
				strBuf.append("<th class=tg-vvj0>Subject</th>");
				strBuf.append("<th class=tg-vvj0>Assessment Id</th>");
				strBuf.append("<th class=tg-vvj0>Exam Date(yyyy-MM-dd)</th>");
				strBuf.append("<th class=tg-vvj0>Start Time</th>");
				strBuf.append("<th class=tg-vvj0>End Time</th>");
				strBuf.append("<th class=tg-vvj0>ScheduleAccessKey</th>");
				strBuf.append("<th class=tg-vvj0>Error</th>");
				strBuf.append("</tr>");
				strBuf.append("</thead>");
				strBuf.append("<tbody>");
				if (list2.isEmpty()) {
					strBuf.append("<tr>");
					strBuf.append("<td class=tg-af47 colspan=8>No Error(s)</td>");
					strBuf.append("</tr>");
				} else {
					for (MettlRegisterCandidateReportBean rptBean : list2) {
						strBuf.append("<tr>");
						strBuf.append("<td class=tg-af47>").append(rptBean.getSapId()).append("</td>");
						strBuf.append("<td class=tg-af47>").append(rptBean.getSubject()).append("</td>");
						strBuf.append("<td class=tg-af47>").append(rptBean.getAssessmentId()).append("</td>");
						strBuf.append("<td class=tg-af47>").append(rptBean.getExamDate()).append("</td>");
						strBuf.append("<td class=tg-af47>").append(rptBean.getExamTime()).append("</td>");
						strBuf.append("<td class=tg-af47>").append(rptBean.getExamEndTime()).append("</td>");
						strBuf.append("<td class=tg-af47>").append(rptBean.getScheduleAccessKey()).append("</td>");
						strBuf.append("<td class=tg-af47>").append(rptBean.getMessage()).append("</td>");
						strBuf.append("</tr>");
					}
				}
				strBuf.append("</tbody>");
				strBuf.append("</table>");
			}
			strBuf.append("<br>Thanks & Regards,<br>");
			strBuf.append("NMIMS GLOBAL ACCESS SCHOOL FOR CONTINUING EDUCATION");
			strBuf.append("<br><br><br><br><br><br><br>").append(Calendar.getInstance().getTimeInMillis());
			emailMessage = strBuf.toString();

			mailer.emailReportRegisterCandidate(emailMessage, nextDayDate);
		} catch (Exception e) {
			
		} finally {
			emailMessage = null;
			clearList(list2);
			clearList(list1);
			emptyStringBuffer(strBuf);
		}
	}

	public static void clearList(List<MettlRegisterCandidateReportBean> argList) {
		if (null != argList) {
			argList.clear();
		}
	}

	public static void emptyStringBuffer(StringBuffer strBuf) {
		if (null != strBuf) {
			strBuf.delete(0, strBuf.length() - 1);
		}
	}

	@Deprecated
	void copyArray(MettlRegisterCandidateBean[] sourceArr, MettlRegisterCandidateBean[] destArr) {
		for (int s = 0; s < sourceArr.length; s++) {
			destArr[s] = sourceArr[s];
		}
	}

	/*@Deprecated
	String fetchDate() {
		// String currentDate = null;
		String nextDayDate = null;
		SimpleDateFormat sdf = new SimpleDateFormat(MettlController.DATE_FORMAT_1);
		// Date dt = new Date(timeInLong);
		// currentDate = sdf.format(System.currentTimeMillis());
		nextDayDate = sdf.format(System.currentTimeMillis() + (1 * 24 * 60 * 60 * 1000));// dt
		logger.info("NextDay Date : " + nextDayDate);
		sdf = null;
		// nextDayDate = "2020-07-15";
		return nextDayDate;
	}*/
	
	String fetchDate() {
		String nextDayDate = null;
		nextDayDate = DateHelper.addDays(DateHelper.DATE_FORMATTER_1, 1);//"2020-07-15"
		logger.info("NextDay Date : " + nextDayDate);
		return nextDayDate;
	}

	String replace(String arg) {
		String d = null;
		d = arg.replace("(", "");
		d = d.replace(")", "");
		d = d.replace("'", "");
		return d;
	}

	@RequestMapping(value = "/m/backupPGScheduleInfo", method = RequestMethod.GET, produces = "application/json")
	public ResponseEntity<ResponseBean> backupPGScheduleInfo(@ModelAttribute MettlRegisterCandidateBean bean) {
		ResponseBean responseBean = null;
		HttpHeaders headers = null;
		List<MettlRegisterCandidateBean> beanList = null;
		boolean isSuccess = Boolean.FALSE;
		try {
			responseBean = (ResponseBean) new ResponseBean();
			responseBean.setStatus(KEY_SUCCESS);
			headers = new HttpHeaders();
			headers.add(CONTENT_TYPE, CONTENT_TYPE_JSON);

			logger.info("Backup PGScheduleInfo for " + bean.getExamStartDateTime());
			mettlDAO.start_Transaction_U_PR("backupPGScheduleInfo");
			beanList = mettlDAO.fetchAll_ScheduleInfo(bean);
			isSuccess = mettlDAO.batchSaveScheduleInfo(beanList);
			mettlDAO.end_Transaction(Boolean.TRUE);

			responseBean.setMessage("Backup of PGScheduleInfo : " + isSuccess);
		} catch (Exception e) {
			mettlDAO.end_Transaction(Boolean.FALSE);
			
			responseBean.setStatus(KEY_ERROR);
			responseBean.setMessage("Failure in backingup ScheduleInfo : " + e.getMessage());
			logger.error("Failure in backingup ScheduleInfo : " + e.getMessage());
		}
		return new ResponseEntity<ResponseBean>(responseBean, headers, HttpStatus.OK);
	}

	@RequestMapping(value = "/m/deletePGScheduleInfo", method = RequestMethod.GET, produces = "application/json")
	public ResponseEntity<ResponseBean> deletePGScheduleInfo(@ModelAttribute MettlRegisterCandidateBean bean) {
		ResponseBean responseBean = null;
		HttpHeaders headers = null;
		try {
			responseBean = (ResponseBean) new ResponseBean();
			responseBean.setStatus(KEY_SUCCESS);
			headers = new HttpHeaders();
			headers.add(CONTENT_TYPE, CONTENT_TYPE_JSON);

			// bean.getExamStartDateTime());
			logger.info("Delete from PGScheduleInfo for " + bean.getExamStartDateTime());
			mettlDAO.deleteScheduleInfo(bean);
			responseBean.setMessage(bean.getMessage());
		} catch (Exception e) {
			
			responseBean.setStatus(KEY_ERROR);
			responseBean.setMessage("Failure in deleting ScheduleInfo : " + e.getMessage());
			logger.error("Failure in deleting ScheduleInfo : " + e.getMessage());
		}
		return new ResponseEntity<ResponseBean>(responseBean, headers, HttpStatus.OK);
	}
	
	//Register 1 Candidate 
	
//	@RequestMapping(value = "/m/register1Candidate", method = RequestMethod.GET, produces = "application/json")
	public ResponseEntity<ResponseBean> register1Candidate() {

		final int batchSize = MettlController.REGISTRATION_CANDIDATE_BATCHSIZE_ONE;
		final int retryFinal = MettlController.RETRY_FOR_ERROR_AT_METTL;// On Error, number of times to retry sending
																		// same data.
		final int retryFinalRemoteServer = 1;// On Error, number of times to retry if error at Remote Server.

		final String maxCompensatoryTime = SPECIAL_NEEDS_STUDENTS_COMPENSATORY_TIME;
		
		final String noImageURL = this.PG_NO_IMAGE_URL;
		String pDate = null;
		String message = null;
		StringBuffer strBuf = null;

		List<MettlRegisterCandidateBean> regCandiBeanList = null;
		List<String> specialNeedStudentsList = null;

		ResponseBean responseBean = null;
		HttpHeaders headers = null;

		try {
			logger.info("FETCHING ------------specialNeeds Student's List------------");
			//fetch Special Needs Students List
			specialNeedStudentsList = this.fetchSpecialNeedsStudents(); //new ArrayList<String>();
			
			logger.info("STARTING ------------register1Candidate------------");

			strBuf = new StringBuffer();

			responseBean = (ResponseBean) new ResponseBean();
			responseBean.setStatus(MettlController.KEY_SUCCESS);
			headers = new HttpHeaders();
			headers.add(MettlController.CONTENT_TYPE, MettlController.CONTENT_TYPE_JSON);

			this.mettlHelper.setBaseUrl(MettlBaseUrl);
			this.mettlHelper.setPrivateKey(PG_METTL_PRIVATE_KEY);
			this.mettlHelper.setPublicKey(PG_METTL_PUBLIC_KEY);

			try {
				pDate = fetchDate();
				logger.info("Registering ------------for the Date : " + pDate);

				regCandiBeanList = this.fetchAll_Candidates(this.mettlDAO, pDate);

				this.processAllCandidates(this.mettlDAO, this.mettlHelper, pDate, batchSize, retryFinal,
						retryFinalRemoteServer, noImageURL, regCandiBeanList, maxCompensatoryTime,
						specialNeedStudentsList);

				// Sending Email
				if (!regCandiBeanList.isEmpty()) {
					emailRegisterCandidateReport(pDate);
				} else {
					message = "No Email Sent, since no candidates to be registered for the date, " + pDate + ".";
					logger.info(message);
				}
			} catch (Exception e) {
				responseBean.setStatus(MettlController.KEY_ERROR);
				logger.error("Failure : " + e.getMessage());
				strBuf.append("| for (Date, Status, Message) : (" + pDate + ", " + MettlController.KEY_ERROR + ", "
						+ e.getMessage() + ")");
			} finally {
				regCandiBeanList.clear();
				regCandiBeanList = null;

				logger.info("Finished Processing ------------for the Date : " + pDate);
				System.out.println("Finished Processing ------------for the Date : " + pDate);
				strBuf.append("| for (Date, Status, Message) : (" + pDate + ", " + MettlController.KEY_SUCCESS
						+ ", Finished" + ")");
			}

		} catch (Exception e) {
			responseBean.setStatus(MettlController.KEY_ERROR);
			logger.error("Setup Failure : " + e.getMessage());
			strBuf.append("Setup Failure : " + e.getMessage());
		} finally {

			responseBean.setMessage(strBuf.toString());

			emptyStringBuffer(strBuf);
			
			if(null != specialNeedStudentsList) {
				specialNeedStudentsList.clear();
				specialNeedStudentsList = null;
			}

			logger.info("FINISHING ------------register1Candidate------------");
		}
		return new ResponseEntity<ResponseBean>(responseBean, headers, HttpStatus.OK);
	}
	
	@RequestMapping(value = "/m/register1CandidateWaitingRoom", method = RequestMethod.GET, produces = "application/json")
	public ResponseEntity<ResponseBean> register1CandidateWaitingRoom() {
		
		final int batchSize = MettlController.REGISTRATION_CANDIDATE_BATCHSIZE_ONE;
		final int retryFinal = MettlController.RETRY_FOR_ERROR_AT_METTL;// On Error, number of times to retry sending
		// same data.
		final int retryFinalRemoteServer = 1;// On Error, number of times to retry if error at Remote Server.
		
		final String maxCompensatoryTime = SPECIAL_NEEDS_STUDENTS_COMPENSATORY_TIME;
		
		final String noImageURL = this.PG_NO_IMAGE_URL;
		String pDate = null;
		String message = null;
		StringBuffer strBuf = null;
		
		List<MettlRegisterCandidateBean> regCandiBeanList = null;
		List<String> specialNeedStudentsList = null;
		
		ResponseBean responseBean = null;
		HttpHeaders headers = null;
		
		try {
			logger.info("FETCHING ------------specialNeeds Student's List------------");
			//fetch Special Needs Students List
			specialNeedStudentsList = this.fetchSpecialNeedsStudents(); //new ArrayList<String>();
			
			logger.info("STARTING ------------register1Candidate------------");
			
			strBuf = new StringBuffer();
			
			responseBean = (ResponseBean) new ResponseBean();
			responseBean.setStatus(MettlController.KEY_SUCCESS);
			headers = new HttpHeaders();
			headers.add(MettlController.CONTENT_TYPE, MettlController.CONTENT_TYPE_JSON);
			
			this.mettlHelper.setBaseUrl(MettlBaseUrl);
			this.mettlHelper.setPrivateKey(PG_METTL_PRIVATE_KEY);
			this.mettlHelper.setPublicKey(PG_METTL_PUBLIC_KEY);
			
			try {
				pDate = fetchDate();
				logger.info("Registering ------------for the Date : " + pDate);
				
				regCandiBeanList = this.fetchAll_Candidates_Waiting_Room(this.mettlDAO, pDate);
				
				this.processAllCandidatesWithWaitingRoom(this.mettlDAO, this.mettlHelper, pDate, batchSize, retryFinal,
						retryFinalRemoteServer, noImageURL, regCandiBeanList, maxCompensatoryTime,
						specialNeedStudentsList);
				
				// Sending Email
				if (!regCandiBeanList.isEmpty()) {
					emailRegisterCandidateReport(pDate);
				} else {
					message = "No Email Sent, since no candidates to be registered for the date, " + pDate + ".";
					logger.info(message);
				}
			} catch (Exception e) {
				responseBean.setStatus(MettlController.KEY_ERROR);
				logger.error("Failure : " + e.getMessage());
				strBuf.append("| for (Date, Status, Message) : (" + pDate + ", " + MettlController.KEY_ERROR + ", "
						+ e.getMessage() + ")");
			} finally {
				regCandiBeanList.clear();
				regCandiBeanList = null;
				
				logger.info("Finished Processing ------------for the Date : " + pDate);
				System.out.println("Finished Processing ------------for the Date : " + pDate);
				strBuf.append("| for (Date, Status, Message) : (" + pDate + ", " + MettlController.KEY_SUCCESS
						+ ", Finished" + ")");
			}
			
		} catch (Exception e) {
			responseBean.setStatus(MettlController.KEY_ERROR);
			logger.error("Setup Failure : " + e.getMessage());
			strBuf.append("Setup Failure : " + e.getMessage());
		} finally {
			
			responseBean.setMessage(strBuf.toString());
			
			emptyStringBuffer(strBuf);
			
			if(null != specialNeedStudentsList) {
				specialNeedStudentsList.clear();
				specialNeedStudentsList = null;
			}
			
			logger.info("FINISHING ------------register1Candidate------------");
		}
		return new ResponseEntity<ResponseBean>(responseBean, headers, HttpStatus.OK);
	}

	// Register All Students

//	@RequestMapping(value = "/m/registerAllCandidates", method = RequestMethod.GET, produces = "application/json")
	public ResponseEntity<ResponseBean> registerAllCandidates(@RequestParam String examYear,
			@RequestParam String examMonth) {

		final int batchSize = MettlController.REGISTRATION_CANDIDATE_BATCHSIZE_ONE;
		final int retryFinal = MettlController.RETRY_FOR_ERROR_AT_METTL;// On Error, number of times to retry sending
																		// same data.
		final int retryFinalRemoteServer = 1;// On Error, number of times to retry if error at Remote Server.
		
		final String maxCompensatoryTime = SPECIAL_NEEDS_STUDENTS_COMPENSATORY_TIME;

		int dateListSize = -1;

		final String noImageURL = this.PG_NO_IMAGE_URL;
		String pDate = null;
		String message = null;
		StringBuffer strBuf = null;

		List<String> dateList = null;
		List<MettlRegisterCandidateBean> regCandiBeanList = null;
		List<String> specialNeedStudentsList = null;

		ResponseBean responseBean = null;
		HttpHeaders headers = null;

		try {
			logger.info("FETCHING ------------specialNeeds Student's List------------");
			//fetch Special Needs Students List
			specialNeedStudentsList = this.fetchSpecialNeedsStudents(); //new ArrayList<String>();
			
			logger.info("STARTING ------------registerAllCandidates------------");

			strBuf = new StringBuffer();

			responseBean = (ResponseBean) new ResponseBean();
			responseBean.setStatus(MettlController.KEY_SUCCESS);
			headers = new HttpHeaders();
			headers.add(MettlController.CONTENT_TYPE, MettlController.CONTENT_TYPE_JSON);

			this.mettlHelper.setBaseUrl(MettlBaseUrl);
			this.mettlHelper.setPrivateKey(PG_METTL_PRIVATE_KEY);
			this.mettlHelper.setPublicKey(PG_METTL_PUBLIC_KEY);

			dateList = this.fetchDates(this.mettlDAO, examYear, examMonth);
			dateListSize = dateList.size();

			for (int idx = 0; idx < dateListSize; idx++) {

				try {
					pDate = dateList.get(idx);
					logger.info("Registering ------------for the Date : " + pDate);

					regCandiBeanList = this.fetchAll_Candidates(this.mettlDAO, pDate);

					this.processAllCandidates(this.mettlDAO, this.mettlHelper, pDate, batchSize, retryFinal,
							retryFinalRemoteServer, noImageURL, regCandiBeanList, maxCompensatoryTime,
							specialNeedStudentsList);

					// Sending Email
					if (!regCandiBeanList.isEmpty()) {
						emailRegisterCandidateReport(pDate);
					} else {
						message = "No Email Sent, since no candidates to be registered for the date, " + pDate + ".";
						logger.info(message);
					}
				} catch (Exception e) {
					responseBean.setStatus(MettlController.KEY_ERROR);
					logger.error("Failure : " + e.getMessage());
					strBuf.append("| for (Date, Status, Message) : (" + pDate + ", " + MettlController.KEY_ERROR + ", "
							+ e.getMessage() + ")");
				} finally {
					regCandiBeanList.clear();
					regCandiBeanList = null;

					logger.info("Finished Processing ------------for the Date : " + pDate);
					System.out.println("Finished Processing ------------for the Date : " + pDate);
					strBuf.append("| for (Date, Status, Message) : (" + pDate + ", " + MettlController.KEY_SUCCESS
							+ ", Finished" + ")");
				}
			}

		} catch (Exception e) {
			responseBean.setStatus(MettlController.KEY_ERROR);
			logger.error("Setup Failure : " + e.getMessage());
			strBuf.append("Setup Failure : " + e.getMessage());
		} finally {

			responseBean.setMessage(strBuf.toString());

			dateList.clear();
			emptyStringBuffer(strBuf);
			
			if(null != specialNeedStudentsList) {
				specialNeedStudentsList.clear();
				specialNeedStudentsList = null;
			}

			logger.info("FINISHING ------------registerAllCandidates------------");
		}

		return new ResponseEntity<ResponseBean>(responseBean, headers, HttpStatus.OK);
	}
	
	@RequestMapping(value = "/m/registerAllCandidatesWithWaitingRoom", method = RequestMethod.GET, produces = "application/json")
	public ResponseEntity<ResponseBean> registerAllCandidatesWithWaitingRoom(@RequestParam String examYear,
			@RequestParam String examMonth) {
		
		final int batchSize = MettlController.REGISTRATION_CANDIDATE_BATCHSIZE_ONE;
		final int retryFinal = MettlController.RETRY_FOR_ERROR_AT_METTL;// On Error, number of times to retry sending
		// same data.
		final int retryFinalRemoteServer = 1;// On Error, number of times to retry if error at Remote Server.
		
		final String maxCompensatoryTime = SPECIAL_NEEDS_STUDENTS_COMPENSATORY_TIME;
		
		int dateListSize = -1;
		
		final String noImageURL = this.PG_NO_IMAGE_URL;
		String pDate = null;
		String message = null;
		StringBuffer strBuf = null;
		
		List<String> dateList = null;
		List<MettlRegisterCandidateBean> regCandiBeanList = null;
		List<String> specialNeedStudentsList = null;
		
		ResponseBean responseBean = null;
		HttpHeaders headers = null;
		
		try {
			logger.info("FETCHING ------------specialNeeds Student's List------------");
			//fetch Special Needs Students List
			specialNeedStudentsList = this.fetchSpecialNeedsStudents(); //new ArrayList<String>();
			
			logger.info("STARTING ------------registerAllCandidatesWithWaitingRoom------------");
			
			strBuf = new StringBuffer();
			
			responseBean = (ResponseBean) new ResponseBean();
			responseBean.setStatus(MettlController.KEY_SUCCESS);
			headers = new HttpHeaders();
			headers.add(MettlController.CONTENT_TYPE, MettlController.CONTENT_TYPE_JSON);
			
			this.mettlHelper.setBaseUrl(MettlBaseUrl);
			this.mettlHelper.setPrivateKey(PG_METTL_PRIVATE_KEY);
			this.mettlHelper.setPublicKey(PG_METTL_PUBLIC_KEY);
			
			dateList = this.fetchDates(this.mettlDAO, examYear, examMonth);
			dateListSize = dateList.size();
			
			for (int idx = 0; idx < dateListSize; idx++) {
				
				try {
					pDate = dateList.get(idx);
					logger.info("Registering ------------for the Date : " + pDate);
					
					regCandiBeanList = this.fetchAll_Candidates_Waiting_Room(this.mettlDAO, pDate);
					
					this.processAllCandidatesWithWaitingRoom(this.mettlDAO, this.mettlHelper, pDate, batchSize, retryFinal,
							retryFinalRemoteServer, noImageURL, regCandiBeanList, maxCompensatoryTime,
							specialNeedStudentsList);
					
					// Sending Email
					if (!regCandiBeanList.isEmpty()) {
						emailRegisterCandidateReport(pDate);
					} else {
						message = "No Email Sent, since no candidates to be registered for the date, " + pDate + ".";
						logger.info(message);
					}
				} catch (Exception e) {
					responseBean.setStatus(MettlController.KEY_ERROR);
					logger.error("Failure : " + e.getMessage());
					strBuf.append("| for (Date, Status, Message) : (" + pDate + ", " + MettlController.KEY_ERROR + ", "
							+ e.getMessage() + ")");
				} finally {
					regCandiBeanList.clear();
					regCandiBeanList = null;
					
					logger.info("Finished Processing ------------for the Date : " + pDate);
					System.out.println("Finished Processing ------------for the Date : " + pDate);
					strBuf.append("| for (Date, Status, Message) : (" + pDate + ", " + MettlController.KEY_SUCCESS
							+ ", Finished" + ")");
				}
			}
			
		} catch (Exception e) {
			responseBean.setStatus(MettlController.KEY_ERROR);
			logger.error("Setup Failure : " + e.getMessage());
			strBuf.append("Setup Failure : " + e.getMessage());
		} finally {
			
			responseBean.setMessage(strBuf.toString());
			
			dateList.clear();
			emptyStringBuffer(strBuf);
			
			if(null != specialNeedStudentsList) {
				specialNeedStudentsList.clear();
				specialNeedStudentsList = null;
			}
			
			logger.info("FINISHING ------------registerAllCandidates------------");
		}
		
		return new ResponseEntity<ResponseBean>(responseBean, headers, HttpStatus.OK);
	}
	
	@GetMapping("/m/runCronRegisterCandidateWaitingRoom")
	@ResponseBody String runCronRegisterCandidateWaitingRoom() {
		if(!"tomcat6".equalsIgnoreCase(SERVER)){
			return " Not running runCronRegisterCandidateWaitingRoom since server isn't tomcat6 it is : " +  SERVER;
		}
		try {
			mettlScheduler.cronRegisterCandidateWaitingRoom();
		} catch (Exception e) {
			return e.getMessage();
		}
		return "Finished";
	}

	protected MettlRegisterCandidateBean[] registerCandidateAtMettl(MettlHelper mettlHelper,
			final MettlRegisterCandidateBean candidateBean) {
		logger.info("registerCandidateAtMettl : (mettlHelper, candidateBean)  (" + mettlHelper + ", " + candidateBean
				+ ")");

		MettlRegisterCandidateBean[] arr = null;

		arr = mettlHelper.registerCandidate(candidateBean, candidateBean.getScheduleAccessKey(),
				candidateBean.getCompensatoryTime(), candidateBean.isCompensatoryTimeFlag());

		return arr;
	}

	protected void processAllCandidates(final MettlDAO mettlDAO, MettlHelper mettlHelper, final String registerForDate,
			final int batchSize, final int retryFinal, final int retryFinalRemoteServer, final String noImageUrl,
			final List<MettlRegisterCandidateBean> regCandiBeanList, final String maxCompensatoryTime,
			final List<String> specialNeedStudentsList) {

		logger.info(
				"processAllCandidates : (mettlDAO, mettlHelper, registerForDate, batchSize, retryFinal, retryFinalRemoteServer, noImageUrl, maxCompensatoryTime)  ("
						+ mettlDAO + ", " + mettlHelper + ", " + registerForDate + "," + batchSize + ", " + retryFinal
						+ ", " + retryFinalRemoteServer + ", " + noImageUrl + ", " + maxCompensatoryTime + ")");

		int totalCandidates = 0;
		int rowsProcessed = 0;
		int rowsWithError = 0;
		int retry = 0;
		int retryRemoteServer = 0;
		int retriedRows = 0;
		Boolean finalSuccess = Boolean.TRUE;
		Boolean openLinkFlag = Boolean.FALSE;
		Boolean retryAtMettl = Boolean.FALSE;
		Boolean configErrorMettl = Boolean.FALSE;// InvalidKey or UnSupportedEncoding errors, complete STOP of program.
		String message = null;
		String scheduleAccessURL = null;
		String pgUrlMettl = null;
		MettlRegisterCandidateBean[] arr = null;
		MettlRegisterCandidateBean candidateBean = null;

		try {
			totalCandidates = regCandiBeanList.size();
			logger.info("processAllCandidates : Total Candidates to be Registered : " + totalCandidates);

			if (MettlController.REGISTRATION_CANDIDATE_BATCHSIZE_ONE == batchSize) {
				for (int d = 0; d < totalCandidates; d++) {
					message = "REGISTER CANDIDATE -(Date, Total) (" + registerForDate + ", " + totalCandidates
							+ ") (S, F, R) (" + rowsProcessed + ", " + rowsWithError + ", " + retriedRows + ") - " + d;
					System.out.println(message);
					logger.info(message);

					candidateBean = regCandiBeanList.get(d);
					scheduleAccessURL = candidateBean.getScheduleAccessURL();
					openLinkFlag = candidateBean.getOpenLinkFlag();
					if (openLinkFlag) {
						candidateBean.setCandidateImage(noImageUrl);// (PG_NO_IMAGE_URL);
						candidateBean.setRegistrationImage(noImageUrl);// (PG_NO_IMAGE_URL);
					}
					//for testing only.
					//if(d < 2) {
						//specialNeedStudentsList.add(candidateBean.getSapId());
					//}
					if (null != specialNeedStudentsList && specialNeedStudentsList.contains(candidateBean.getSapId())) {
						candidateBean.setCompensatoryTimeFlag(Boolean.TRUE.booleanValue());
						candidateBean.setCompensatoryTime(maxCompensatoryTime);

						message = "REGISTER CANDIDATE -(Date, Special Needs Student SapId) (" + registerForDate + ", "
								+ candidateBean.getSapId() + ")";
						System.out.println(message);
						logger.info(message);
					}

					retryAtMettl = Boolean.FALSE;
					retry = retryFinal;
					retryRemoteServer = retryFinalRemoteServer;
					do {
						arr = this.registerCandidateAtMettl(mettlHelper, candidateBean);

						if (arr.length == 1) {
							if (null != arr[0].getStatus()
									&& arr[0].getStatus().equalsIgnoreCase(MettlController.KEY_ERROR)) {
								logger.error("Error : " + d + " - " + arr[0].getMessage());

								message = arr[0].getMessage().replace("\"", "");
								if (message.contains("SHA Error") || message.contains("E000")
										|| message.contains("E607") || message.contains("E401")) {
									// E000-Some Error Occurred, E607-Candidate registration limit exceeded,
									// E401-Authentication failed/Signature mismatch
									configErrorMettl = Boolean.TRUE;
									finalSuccess = Boolean.FALSE;
									break;
								}

								// message = (arr[0].getStatus() + "|" + this.replace(arr[0].getMessage()));
								this.saveStatus(mettlDAO, candidateBean, arr[0].getStatus(),
										(arr[0].getStatus() + "|" + this.replace(arr[0].getMessage())));

								finalSuccess = Boolean.FALSE;
								rowsWithError++;

								if (message.contains("E028")) {
									if (retry != 0) {
										candidateBean.setCandidateImage(noImageUrl);// (PG_NO_IMAGE_URL);
										candidateBean.setRegistrationImage(noImageUrl);// (PG_NO_IMAGE_URL);
										retryAtMettl = Boolean.TRUE;
										retry = retry - 1;
										retriedRows = retriedRows + 1;
										message = "REGISTER CANDIDATE -(Date, Total) (" + registerForDate + ", "
												+ totalCandidates + ") (S, F, R) (" + rowsProcessed + ", "
												+ rowsWithError + ", " + retriedRows + ") - " + d + " -RETRY-";
										System.out.println(message);
										logger.info(message);
									} else {
										retryAtMettl = Boolean.FALSE;
									}
								} else {
									// retryAtMettl = Boolean.FALSE;
									if (retryRemoteServer != 0) {
										retryAtMettl = Boolean.TRUE;
										retryRemoteServer = retryRemoteServer - 1;
										retriedRows = retriedRows + 1;
										message = "REGISTER CANDIDATE -(Date, Total) (" + registerForDate + ", "
												+ totalCandidates + ") (S, F, R) (" + rowsProcessed + ", "
												+ rowsWithError + ", " + retriedRows + ") - " + d + " -RETRY-";
										System.out.println(message);
										logger.info(message);
									} else {
										retryAtMettl = Boolean.FALSE;
									}
								}
							} else {
								message = (arr[0].getStatus() + "|" + this.replace(arr[0].getMessage()));
								if (openLinkFlag || retryAtMettl) {
									pgUrlMettl = scheduleAccessURL;
								} else {
									pgUrlMettl = arr[0].getUrl();
								}
								this.saveStatus(mettlDAO, candidateBean, arr[0].getStatus(), message, arr[0].getUrl(),
										pgUrlMettl);
								rowsProcessed++;
								retryAtMettl = Boolean.FALSE;
							}
						}
					} while (retryAtMettl);

					if (configErrorMettl) {
						message = "ABRUPT STOP................................";
						System.out.println(message);
						logger.info(message);
						
						message = "REGISTER CANDIDATE -(Date, Total) (" + registerForDate + ", "
								+ totalCandidates + ") (S, F, R) (" + rowsProcessed + ", "
								+ rowsWithError + ", " + retriedRows + ") - " + d + " -STOP-";
						System.out.println(message);
						logger.info(message);
						
						message = "ABRUPT STOP................................";
						System.out.println(message);
						logger.info(message);
						break;
					}
				}
			}
		} finally {
			if (finalSuccess) {
				message = "Total Candidates Registered : Count (Date, Success/Failure/Retried) : (" + registerForDate
						+ ", " + rowsProcessed + "/" + rowsWithError + "/" + retriedRows + ")";
				logger.info(message);
			} else {
				message = "Partial Success/Failure/Retried : Count (Date, Success/Failure/Retried) : ("
						+ registerForDate + ", " + rowsProcessed + "/" + rowsWithError + "/" + retriedRows + ")";
				logger.info(message);
			}

			message = null;
			scheduleAccessURL = null;
			candidateBean = null;
		}
	}
	
	protected void processAllCandidatesWithWaitingRoom(final MettlDAO mettlDAO, MettlHelper mettlHelper, final String registerForDate,
			final int batchSize, final int retryFinal, final int retryFinalRemoteServer, final String noImageUrl,
			final List<MettlRegisterCandidateBean> regCandiBeanList, final String maxCompensatoryTime,
			final List<String> specialNeedStudentsList) {
		
		logger.info(
				"processAllCandidates : (mettlDAO, mettlHelper, registerForDate, batchSize, retryFinal, retryFinalRemoteServer, noImageUrl, maxCompensatoryTime)  ("
						+ mettlDAO + ", " + mettlHelper + ", " + registerForDate + "," + batchSize + ", " + retryFinal
						+ ", " + retryFinalRemoteServer + ", " + noImageUrl + ", " + maxCompensatoryTime + ")");
		
		int totalCandidates = 0;
		int rowsProcessed = 0;
		int rowsWithError = 0;
		int retry = 0;
		int retryRemoteServer = 0;
		int retriedRows = 0;
		Boolean finalSuccess = Boolean.TRUE;
		Boolean openLinkFlag = Boolean.FALSE;
		Boolean retryAtMettl = Boolean.FALSE;
		Boolean configErrorMettl = Boolean.FALSE;// InvalidKey or UnSupportedEncoding errors, complete STOP of program.
		String message = null;
		String scheduleAccessURL = null;
		String pgUrlMettl = null;
		MettlRegisterCandidateBean[] arr = null;
		MettlRegisterCandidateBean candidateBean = null;
		
		try {
			totalCandidates = regCandiBeanList.size();
			logger.info("processAllCandidates : Total Candidates to be Registered : " + totalCandidates);
			
			if (MettlController.REGISTRATION_CANDIDATE_BATCHSIZE_ONE == batchSize) {
				for (int d = 0; d < totalCandidates; d++) {
					message = "REGISTER CANDIDATE -(Date, Total) (" + registerForDate + ", " + totalCandidates
							+ ") (S, F, R) (" + rowsProcessed + ", " + rowsWithError + ", " + retriedRows + ") - " + d;
					System.out.println(message);
					logger.info(message);
					
					candidateBean = regCandiBeanList.get(d);
					scheduleAccessURL = candidateBean.getScheduleAccessURL();
					openLinkFlag = candidateBean.getOpenLinkFlag();
					if (openLinkFlag) {
						candidateBean.setCandidateImage(noImageUrl);// (PG_NO_IMAGE_URL);
						candidateBean.setRegistrationImage(noImageUrl);// (PG_NO_IMAGE_URL);
					}
					//for testing only.
					//if(d < 2) {
					//specialNeedStudentsList.add(candidateBean.getSapId());
					//}
					if (null != specialNeedStudentsList && specialNeedStudentsList.contains(candidateBean.getSapId())) {
						candidateBean.setCompensatoryTimeFlag(Boolean.TRUE.booleanValue());
						candidateBean.setCompensatoryTime(maxCompensatoryTime);
						
						message = "REGISTER CANDIDATE -(Date, Special Needs Student SapId) (" + registerForDate + ", "
								+ candidateBean.getSapId() + ")";
						System.out.println(message);
						logger.info(message);
					}
					
					retryAtMettl = Boolean.FALSE;
					retry = retryFinal;
					retryRemoteServer = retryFinalRemoteServer;
					do {
						arr = this.registerCandidateAtMettl(mettlHelper, candidateBean);
						
						if (arr.length == 1) {
							if (null != arr[0].getStatus()
									&& arr[0].getStatus().equalsIgnoreCase(MettlController.KEY_ERROR)) {
								logger.error("Error : " + d + " - " + arr[0].getMessage());
								
								message = arr[0].getMessage().replace("\"", "");
								if (message.contains("SHA Error") || message.contains("E000")
										|| message.contains("E607") || message.contains("E401")) {
									// E000-Some Error Occurred, E607-Candidate registration limit exceeded,
									// E401-Authentication failed/Signature mismatch
									configErrorMettl = Boolean.TRUE;
									finalSuccess = Boolean.FALSE;
									break;
								}
								
								// message = (arr[0].getStatus() + "|" + this.replace(arr[0].getMessage()));
								this.saveStatusWaitingRoom(mettlDAO, candidateBean, arr[0].getStatus(),
										(arr[0].getStatus() + "|" + this.replace(arr[0].getMessage())));
								
								finalSuccess = Boolean.FALSE;
								rowsWithError++;
								
								if (message.contains("E028")) {
									if (retry != 0) {
										candidateBean.setCandidateImage(noImageUrl);// (PG_NO_IMAGE_URL);
										candidateBean.setRegistrationImage(noImageUrl);// (PG_NO_IMAGE_URL);
										retryAtMettl = Boolean.TRUE;
										retry = retry - 1;
										retriedRows = retriedRows + 1;
										message = "REGISTER CANDIDATE -(Date, Total) (" + registerForDate + ", "
												+ totalCandidates + ") (S, F, R) (" + rowsProcessed + ", "
												+ rowsWithError + ", " + retriedRows + ") - " + d + " -RETRY-";
										System.out.println(message);
										logger.info(message);
									} else {
										retryAtMettl = Boolean.FALSE;
									}
								} else {
									// retryAtMettl = Boolean.FALSE;
									if (retryRemoteServer != 0) {
										retryAtMettl = Boolean.TRUE;
										retryRemoteServer = retryRemoteServer - 1;
										retriedRows = retriedRows + 1;
										message = "REGISTER CANDIDATE -(Date, Total) (" + registerForDate + ", "
												+ totalCandidates + ") (S, F, R) (" + rowsProcessed + ", "
												+ rowsWithError + ", " + retriedRows + ") - " + d + " -RETRY-";
										System.out.println(message);
										logger.info(message);
									} else {
										retryAtMettl = Boolean.FALSE;
									}
								}
							} else {
								message = (arr[0].getStatus() + "|" + this.replace(arr[0].getMessage()));
								if (openLinkFlag || retryAtMettl) {
									pgUrlMettl = scheduleAccessURL;
								} else {
									pgUrlMettl = arr[0].getUrl();
								}
								this.saveStatusWaitingRoom(mettlDAO, candidateBean, arr[0].getStatus(), message, arr[0].getUrl(),
										pgUrlMettl);
								rowsProcessed++;
								retryAtMettl = Boolean.FALSE;
							}
						}
					} while (retryAtMettl);
					
					if (configErrorMettl) {
						message = "ABRUPT STOP................................";
						System.out.println(message);
						logger.info(message);
						
						message = "REGISTER CANDIDATE -(Date, Total) (" + registerForDate + ", "
								+ totalCandidates + ") (S, F, R) (" + rowsProcessed + ", "
								+ rowsWithError + ", " + retriedRows + ") - " + d + " -STOP-";
						System.out.println(message);
						logger.info(message);
						
						message = "ABRUPT STOP................................";
						System.out.println(message);
						logger.info(message);
						break;
					}
				}
			}
		} finally {
			if (finalSuccess) {
				message = "Total Candidates Registered : Count (Date, Success/Failure/Retried) : (" + registerForDate
						+ ", " + rowsProcessed + "/" + rowsWithError + "/" + retriedRows + ")";
				logger.info(message);
			} else {
				message = "Partial Success/Failure/Retried : Count (Date, Success/Failure/Retried) : ("
						+ registerForDate + ", " + rowsProcessed + "/" + rowsWithError + "/" + retriedRows + ")";
				logger.info(message);
			}
			
			message = null;
			scheduleAccessURL = null;
			candidateBean = null;
		}
	}

	protected void saveStatus(MettlDAO mettlDAO, MettlRegisterCandidateBean candidateBean, final String status,
			final String message, final String urlSuccess, final String urlGeneral) {
		logger.info("saveStatus : (mettlDAO, candidateBean, status, message) (" + mettlDAO + ", " + candidateBean + ", "
				+ status + ", " + message + ")");

		String scheduleAccessKey = null;
		String sapId = null;
		String booked = null;
		String examDate = null;
		String examTime = null;
		String examEndTime = null;
		String year = null;
		String month = null;
		String subject = null;
		String emailAddress = null;
		String trackId = null;
		String testTaken = null;
		String firstName = null;
		String lastName = null;
		String examStartDateTime = null;
		String examEndDateTime = null;
		String accessStartDateTime = null;
		String accessEndDateTime = null;
		String sifySubjectCode = null;
		String scheduleName = null;
		String examCenterName = null;
		Boolean isSaved = Boolean.FALSE;
		Boolean isSaved1 = Boolean.FALSE;
		Boolean isUpdated = Boolean.FALSE;
		
		try {
			scheduleAccessKey = candidateBean.getScheduleAccessKey();
			sapId = candidateBean.getSapId();

			mettlDAO.start_Transaction_U_PR("saveStatus");
			
			if (MettlController.KEY_ERROR.equalsIgnoreCase(status)) {
				
				logger.info("saveStatus : (sapId, scheduleAccessKey, status) (" + sapId + ", " + scheduleAccessKey
						+ ", " + MettlController.REGISTRATION_STATUS_FAIL + ")");
				isSaved = mettlDAO.saveCandidateRegisteredMettlInfo(sapId, scheduleAccessKey,
						MettlController.REGISTRATION_STATUS_FAIL, message, null);
				
				mettlDAO.end_Transaction(isSaved);
			} else {
				emailAddress = candidateBean.getEmailAddress();
				subject = candidateBean.getSubject();
				booked = candidateBean.getBooked();
				year = candidateBean.getYear();
				month = candidateBean.getMonth();
				examDate = candidateBean.getExamDate();
				examTime = candidateBean.getExamTime();
				examEndTime = candidateBean.getExamEndTime();
				trackId = candidateBean.getTrackId();
				testTaken = candidateBean.getTestTaken();
				firstName = candidateBean.getFirstName();
				lastName = candidateBean.getLastName();
				examStartDateTime = candidateBean.getExamStartDateTime();
				examEndDateTime = candidateBean.getExamEndDateTime();
				accessStartDateTime = candidateBean.getAccessStartDateTime();
				accessEndDateTime = candidateBean.getAccessEndDateTime();
				sifySubjectCode = candidateBean.getSifySubjectCode();
				scheduleName = candidateBean.getScheduleName();
				examCenterName = candidateBean.getExamCenterName();
				
				logger.info(
						"saveStatus : (sapId, scheduleAccessKey, emailAddress, subject, booked, year, month, examDate, examTime, examEndTime, status) ("
								+ sapId + ", " + scheduleAccessKey + ", " + emailAddress + ", " + subject + ", "
								+ booked + ", " + year + ", " + month + ", " + examDate + ", " + examTime + ", "
								+ examEndTime + ", " + MettlController.REGISTRATION_STATUS_PASS + ")");
				logger.info(
						"saveStatus : (trackId, testTaken, firstName, lastName, examStartDateTime, examEndDateTime, accessStartDateTime, accessEndDateTime, sifySubjectCode, scheduleName, urlSuccess, urlGeneral) ("
								+ trackId + ", " + testTaken + ", " + firstName + ", " + lastName + ", "
								+ examStartDateTime + ", " + examEndDateTime + ", " + accessStartDateTime + ", "
								+ accessEndDateTime + ", " + sifySubjectCode + ", " + scheduleName + ", " + urlSuccess
								+ ", " + urlGeneral + ")");

				isSaved = mettlDAO.saveCandidateRegisteredMettlInfo(sapId, scheduleAccessKey,
						MettlController.REGISTRATION_STATUS_PASS, message, urlSuccess);

				isUpdated = mettlDAO.updateExamBooking(sapId, emailAddress, subject, booked, year, month, examDate, examTime,
						examEndTime);

				isSaved1 = mettlDAO.saveScheduleInfo(sapId, emailAddress, subject, year, month, trackId, testTaken, firstName,
						lastName, examStartDateTime, examEndDateTime, accessStartDateTime, accessEndDateTime,
						sifySubjectCode, scheduleName, scheduleAccessKey, urlGeneral, examCenterName);
				
				if(isSaved && isUpdated && isSaved1) {
					mettlDAO.end_Transaction(Boolean.TRUE);
				} else {
					mettlDAO.end_Transaction(Boolean.FALSE);
				}
			}
		} catch(Exception e) {
			mettlDAO.end_Transaction(Boolean.FALSE);
			throw e;
		} finally {
			booked = null;
			examDate = null;
			examTime = null;
			examEndTime = null;
			year = null;
			month = null;
			subject = null;
			emailAddress = null;
			scheduleAccessKey = null;
			sapId = null;
			trackId = null;
			testTaken = null;
			firstName = null;
			lastName = null;
			examStartDateTime = null;
			examEndDateTime = null;
			accessStartDateTime = null;
			accessEndDateTime = null;
			sifySubjectCode = null;
			scheduleName = null;
		}
	}
	
	protected void saveStatusWaitingRoom(MettlDAO mettlDAO, MettlRegisterCandidateBean candidateBean, final String status,
			final String message, final String urlSuccess, final String urlGeneral) {
		logger.info("saveStatus : (mettlDAO, candidateBean, status, message) (" + mettlDAO + ", " + candidateBean + ", "
				+ status + ", " + message + ")");
		
		String scheduleAccessKey = null;
		String sapId = null;
		String booked = null;
		String examDate = null;
		String examTime = null;
		String examEndTime = null;
		String year = null;
		String month = null;
		String subject = null;
		String emailAddress = null;
		String trackId = null;
		String testTaken = null;
		String firstName = null;
		String lastName = null;
		String examStartDateTime = null;
		String examEndDateTime = null;
		String accessStartDateTime = null;
		String accessEndDateTime = null;
		String reportingStartDateTime = null;
		String reportingFinishDateTime = null;
		String sifySubjectCode = null;
		String scheduleName = null;
		String examCenterName = null;
		Boolean isSaved = Boolean.FALSE;
		Boolean isSaved1 = Boolean.FALSE;
		Boolean isUpdated = Boolean.FALSE;
		
		try {
			scheduleAccessKey = candidateBean.getScheduleAccessKey();
			sapId = candidateBean.getSapId();
			
			mettlDAO.start_Transaction_U_PR("saveStatus");
			
			if (MettlController.KEY_ERROR.equalsIgnoreCase(status)) {
				
				logger.info("saveStatus : (sapId, scheduleAccessKey, status) (" + sapId + ", " + scheduleAccessKey
						+ ", " + MettlController.REGISTRATION_STATUS_FAIL + ")");
				isSaved = mettlDAO.saveCandidateRegisteredMettlInfo(sapId, scheduleAccessKey,
						MettlController.REGISTRATION_STATUS_FAIL, message, null);
				
				mettlDAO.end_Transaction(isSaved);
			} else {
				emailAddress = candidateBean.getEmailAddress();
				subject = candidateBean.getSubject();
				booked = candidateBean.getBooked();
				year = candidateBean.getYear();
				month = candidateBean.getMonth();
				examDate = candidateBean.getExamDate();
				examTime = candidateBean.getExamTime();
				examEndTime = candidateBean.getExamEndTime();
				trackId = candidateBean.getTrackId();
				testTaken = candidateBean.getTestTaken();
				firstName = candidateBean.getFirstName();
				lastName = candidateBean.getLastName();
				examStartDateTime = candidateBean.getExamStartDateTime();
				examEndDateTime = candidateBean.getExamEndDateTime();
				accessStartDateTime = candidateBean.getAccessStartDateTime();
				accessEndDateTime = candidateBean.getAccessEndDateTime();
				reportingStartDateTime = candidateBean.getReportStartDateTime();
				reportingFinishDateTime = candidateBean.getReportFinishDateTime();
				sifySubjectCode = candidateBean.getSifySubjectCode();
				scheduleName = candidateBean.getScheduleName();
				examCenterName = candidateBean.getExamCenterName();
				
				logger.info(
						"saveStatus : (sapId, scheduleAccessKey, emailAddress, subject, booked, year, month, examDate, examTime, examEndTime, status) ("
								+ sapId + ", " + scheduleAccessKey + ", " + emailAddress + ", " + subject + ", "
								+ booked + ", " + year + ", " + month + ", " + examDate + ", " + examTime + ", "
								+ examEndTime + ", " + MettlController.REGISTRATION_STATUS_PASS + ")");
				logger.info(
						"saveStatus : (trackId, testTaken, firstName, lastName, examStartDateTime, examEndDateTime, accessStartDateTime, accessEndDateTime, sifySubjectCode, scheduleName, urlSuccess, urlGeneral) ("
								+ trackId + ", " + testTaken + ", " + firstName + ", " + lastName + ", "
								+ examStartDateTime + ", " + examEndDateTime + ", " + accessStartDateTime + ", "
								+ accessEndDateTime + ", " + sifySubjectCode + ", " + scheduleName + ", " + urlSuccess
								+ ", " + urlGeneral + ")");
				
				isSaved = mettlDAO.saveCandidateRegisteredMettlInfo(sapId, scheduleAccessKey,
						MettlController.REGISTRATION_STATUS_PASS, message, urlSuccess);
				
				isUpdated = mettlDAO.updateExamBooking(sapId, emailAddress, subject, booked, year, month, examDate, examTime,
						examEndTime);
				
				isSaved1 = mettlDAO.saveScheduleInfoWaitingRoom(sapId, emailAddress, subject, year, month, trackId, testTaken, firstName,
						lastName, examStartDateTime, examEndDateTime, accessStartDateTime, accessEndDateTime,reportingStartDateTime,
						reportingFinishDateTime,sifySubjectCode, scheduleName, scheduleAccessKey, urlGeneral, examCenterName);
				
				if(isSaved && isUpdated && isSaved1) {
					mettlDAO.end_Transaction(Boolean.TRUE);
				} else {
					mettlDAO.end_Transaction(Boolean.FALSE);
				}
			}
		} catch(Exception e) {
			mettlDAO.end_Transaction(Boolean.FALSE);
			throw e;
		} finally {
			booked = null;
			examDate = null;
			examTime = null;
			examEndTime = null;
			year = null;
			month = null;
			subject = null;
			emailAddress = null;
			scheduleAccessKey = null;
			sapId = null;
			trackId = null;
			testTaken = null;
			firstName = null;
			lastName = null;
			examStartDateTime = null;
			examEndDateTime = null;
			accessStartDateTime = null;
			accessEndDateTime = null;
			sifySubjectCode = null;
			scheduleName = null;
		}
	}

	protected void saveStatus(MettlDAO mettlDAO, MettlRegisterCandidateBean candidateBean, String status,
			String message) {
		logger.info("saveStatus : (mettlDAO, candidateBean, status, message) (" + mettlDAO + ", " + candidateBean + ", "
				+ status + ", " + message + ")");

		this.saveStatus(mettlDAO, candidateBean, status, message, null, null);
	}
	
	protected void saveStatusWaitingRoom(MettlDAO mettlDAO, MettlRegisterCandidateBean candidateBean, String status,
			String message) {
		logger.info("saveStatus : (mettlDAO, candidateBean, status, message) (" + mettlDAO + ", " + candidateBean + ", "
				+ status + ", " + message + ")");
		
		this.saveStatusWaitingRoom(mettlDAO, candidateBean, status, message, null, null);
	}

	protected List<MettlRegisterCandidateBean> fetchAll_Candidates(final MettlDAO mettlDAO, final String pDate) {
		logger.info("fetchAll_Candidates : (mettlDAO, pDate)  (" + mettlDAO + ", " + pDate + ")");

		MettlRegisterCandidateBean regCandiBean = null;
		List<MettlRegisterCandidateBean> regCandiBeanList = null;

		// Prepare Data
		 /*regCandiBeanList = new ArrayList<MettlRegisterCandidateBean>(); 
		 for (int a = 0; a < 5; a++) { 
			 regCandiBean = new MettlRegisterCandidateBean();
			 regCandiBean.setFirstName("Abey" + (a + 1));
			 regCandiBean.setLastName("Mathews" + (a + 1));
			 regCandiBean.setSapId("441150006" + (a + 1));
			 regCandiBean.setScheduleAccessKey("51o3vra58g");//4zzwleo1kw
			 regCandiBean.setEmailAddress("mathews" + (a + 1) + "@xyz.com");
			 regCandiBean.setRegistrationImage("https://studentdocumentsngasce.s3-ap-south-1.amazonaws.com/StudentDocuments/0012j00000NNGux/Student_Photograph_WAXX.jpg"); 
			 regCandiBean.setCandidateImage("https://studentdocumentsngasce.s3-ap-south-1.amazonaws.com/StudentDocuments/0012j00000NNGux/Student_Photograph_WAXX.jpg"); 
			 regCandiBean.setOpenLinkFlag(Boolean.FALSE);
			 //regCandiBean.setScheduleAccessURL("https://tests.mettl.com/authenticateKey/4zzwleo1kw");
			 regCandiBean.setScheduleAccessURL("https://tests.mettl.pro/authenticateKey/51o3vra58g");
			 regCandiBean.setSubject("Corporate Finance"); 
			 regCandiBean.setBooked("Y");
			 regCandiBean.setMonth("Sep"); 
			 regCandiBean.setYear("2021");
			 regCandiBean.setExamDate("2021-09-03"); regCandiBean.setExamTime("17:00:00");
			 regCandiBean.setExamEndTime("19:30:00");
			 regCandiBean.setTrackId("771150006501592693824712");
			 regCandiBean.setTestTaken(null); 
			 regCandiBean.setSifySubjectCode("102");
			 regCandiBean.setExamStartDateTime(pDate + " 10:00:00");// 2021-09-03 10:00:00
			 regCandiBean.setExamEndDateTime(pDate + " 14:00:00");
			 regCandiBean.setAccessStartDateTime(pDate + " 10:00:00");
			 regCandiBean.setAccessEndDateTime(pDate + " 11:00:00");
			 regCandiBean.setScheduleName("DummySchName");
			 regCandiBeanList.add(regCandiBean); 
		}*/

		regCandiBeanList = mettlDAO.fetchAll_Candidates_SchedulesList(pDate);
		if (null != regCandiBeanList) {
			logger.info("fetchAll_Candidates : Total Candidates : " + regCandiBeanList.size());
		}
		return regCandiBeanList;
	}
	
	protected List<MettlRegisterCandidateBean> fetchAll_Candidates_Waiting_Room(final MettlDAO mettlDAO, final String pDate) {
		logger.info("fetchAll_Candidates : (mettlDAO, pDate)  (" + mettlDAO + ", " + pDate + ")");
		
		MettlRegisterCandidateBean regCandiBean = null;
		List<MettlRegisterCandidateBean> regCandiBeanList = null;
		
		// Prepare Data
		/*regCandiBeanList = new ArrayList<MettlRegisterCandidateBean>(); 
		 for (int a = 0; a < 5; a++) { 
			 regCandiBean = new MettlRegisterCandidateBean();
			 regCandiBean.setFirstName("Abey" + (a + 1));
			 regCandiBean.setLastName("Mathews" + (a + 1));
			 regCandiBean.setSapId("441150006" + (a + 1));
			 regCandiBean.setScheduleAccessKey("51o3vra58g");//4zzwleo1kw
			 regCandiBean.setEmailAddress("mathews" + (a + 1) + "@xyz.com");
			 regCandiBean.setRegistrationImage("https://studentdocumentsngasce.s3-ap-south-1.amazonaws.com/StudentDocuments/0012j00000NNGux/Student_Photograph_WAXX.jpg"); 
			 regCandiBean.setCandidateImage("https://studentdocumentsngasce.s3-ap-south-1.amazonaws.com/StudentDocuments/0012j00000NNGux/Student_Photograph_WAXX.jpg"); 
			 regCandiBean.setOpenLinkFlag(Boolean.FALSE);
			 //regCandiBean.setScheduleAccessURL("https://tests.mettl.com/authenticateKey/4zzwleo1kw");
			 regCandiBean.setScheduleAccessURL("https://tests.mettl.pro/authenticateKey/51o3vra58g");
			 regCandiBean.setSubject("Corporate Finance"); 
			 regCandiBean.setBooked("Y");
			 regCandiBean.setMonth("Sep"); 
			 regCandiBean.setYear("2021");
			 regCandiBean.setExamDate("2021-09-03"); regCandiBean.setExamTime("17:00:00");
			 regCandiBean.setExamEndTime("19:30:00");
			 regCandiBean.setTrackId("771150006501592693824712");
			 regCandiBean.setTestTaken(null); 
			 regCandiBean.setSifySubjectCode("102");
			 regCandiBean.setExamStartDateTime(pDate + " 10:00:00");// 2021-09-03 10:00:00
			 regCandiBean.setExamEndDateTime(pDate + " 14:00:00");
			 regCandiBean.setAccessStartDateTime(pDate + " 10:00:00");
			 regCandiBean.setAccessEndDateTime(pDate + " 11:00:00");
			 regCandiBean.setScheduleName("DummySchName");
			 regCandiBeanList.add(regCandiBean); 
		}*/
		
		regCandiBeanList = mettlDAO.fetchAll_Candidates_SchedulesList_Waiting_Room(pDate);
		if (null != regCandiBeanList) {
			logger.info("fetchAll_Candidates : Total Candidates : " + regCandiBeanList.size());
		}
		return regCandiBeanList;
	}

	protected List<String> fetchDates(MettlDAO mettlDAO, String examYear, String examMonth) {
		logger.info("fetchDates (ExamYear, ExamMonth) (" + examYear + ", " + examMonth + ")");
		List<String> dateList = null;

		dateList = mettlDAO.fetchAllDates(examYear, examMonth);
		if (null != dateList) {
			logger.info("fetchDates : Total Dates : " + dateList.size());
		}
		return dateList;
	}

	// create Partitions automatically
	@RequestMapping(value = "/m/changePartition", method = RequestMethod.GET, produces = "application/json")
	public ResponseEntity<ResponseBean> changePartition(@RequestParam String examYear, @RequestParam String examMonth) {
		Integer partitionCount = null;
		int partitionMapSize = 0;
		int partitionSize = 0;
		Boolean isPartitionDone = Boolean.FALSE;
		Map<String, String> partitionMap = null;
		// String message = null;
		StringBuffer strBuf = null;
		ResponseBean responseBean = null;
		HttpHeaders headers = null;

		try {
			logger.info("STARTING ------------changePartition------------");

			strBuf = new StringBuffer();
			responseBean = (ResponseBean) new ResponseBean();
			responseBean.setStatus(MettlController.KEY_SUCCESS);
			headers = new HttpHeaders();
			headers.add(MettlController.CONTENT_TYPE, MettlController.CONTENT_TYPE_JSON);

			partitionCount = fetchPartitionCount(mettlDAO);
			if (null != partitionCount && partitionCount.intValue() == 0) {
				partitionMap = this.buildPartitionMap(mettlDAO, examYear, examMonth);
				partitionMapSize = partitionMap.size();
				isPartitionDone = this.addPartitionToTable(mettlDAO, examYear, examMonth, partitionMap);

				logger.info("changePartition-------isPartitionDone-----" + isPartitionDone);

				if (isPartitionDone) {
					partitionCount = fetchPartitionCount(mettlDAO);
					partitionSize = partitionCount.intValue();

					if (partitionMapSize == partitionSize) {
						logger.info("changePartition-------Partition count matched-----(" + partitionMapSize + ","
								+ partitionSize + ")");
						
					} else {
						logger.info("changePartition-------Partition count match failure-----(" + partitionMapSize + ","
								+ partitionSize + ")");
						
					}
				}
			} else {
				logger.info("changePartition-------Partition Not Done, Existing Partition Count-----" + partitionCount);
				
			}

		} catch (Exception e) {
			responseBean.setStatus(MettlController.KEY_ERROR);
			logger.error("Partition Failure : " + e.getMessage());
			strBuf.append("Partition Failure : " + e.getMessage());
		} finally {
			if (null != partitionMap) {
				partitionMap.clear();
			}

			partitionMap = null;
			partitionCount = null;

			responseBean.setMessage(strBuf.toString());
			logger.info("FINISHING ------------changePartition------------");
		}
		return new ResponseEntity<ResponseBean>(responseBean, headers, HttpStatus.OK);
	}

	protected Integer fetchPartitionCount(MettlDAO mettlDAO) {
		Integer count = null;
		count = mettlDAO.fetchPartitionCount(MettlDAO.DB_SCHEMA_EXAM, MettlDAO.TABLE_PG_SCHEDULEINFO_METTL);
		return count;
	}

	protected Map<String, String> buildPartitionMap(MettlDAO mettlDAO, final String examYear, final String examMonth) {
		Map<String, String> map = null;
		List<String> dateList = null;

		dateList = this.fetchDates(mettlDAO, examYear, examMonth);
		map = this.buildPartitionNameMap(dateList);

		dateList.clear();
		return map;
	}

	protected Boolean addPartitionToTable(MettlDAO mettlDAO, final String examYear, final String examMonth,
			Map<String, String> partitionMap) {
		Boolean isPartitioned = Boolean.FALSE;
		isPartitioned = mettlDAO.createPartition(MettlDAO.TABLE_EXAMS_PG_SCHEDULEINFO_METTL,
				MettlDAO.C_REPORTING_START_TIME, partitionMap);
		return isPartitioned;
	}

	protected Map<String, String> buildPartitionNameMap(final List<String> dateList) {
		String tempDate = null;
		String previousDate = null;
		String dateNoDash = null;
		String firstDate = null;
		String lastDate = null;
		Map<String, String> map = null;

		/*
		 * (20210902, 2021-09-03) exam firstDate=2021-09-03| previousDate=2021-09-02
		 * (20210903, 2021-09-04) (20210904, 2021-09-05) (20210905, 2021-09-10)
		 * (20210910, 2021-09-11) (20210911, 2021-09-12) (20210912, 2021-09-17)
		 * (20210917, 2021-09-18) (20210918, 2021-09-19) exam lastDate=2021-09-19| +1
		 * (20210919, 2021-09-20) (20210920, null)
		 */

		map = new LinkedHashMap<String, String>();

		// exam's last date
		lastDate = dateList.get(dateList.size() - 1);
		// exam's first date
		firstDate = dateList.get(0);
		// exam's first date minus 1 day
		previousDate = MettlController.subtractDays(firstDate, 1);

		for (int idx = 0; idx < dateList.size(); idx++) {
			dateNoDash = MettlController.dateWithoutDash(previousDate);
			map.put(dateNoDash, dateList.get(idx));
			previousDate = dateList.get(idx);
		}
		// exams's last date plus 1 day
		tempDate = MettlController.addDays(lastDate, 1);
		dateNoDash = MettlController.dateWithoutDash(previousDate);
		map.put(dateNoDash, tempDate);
		previousDate = tempDate;
		// exams's last date plus 2 day
		 //tempDate = MettlController.addDays(lastDate, 2);
		//dateNoDash = MettlController.dateWithoutDash(previousDate); 
		 //map.put(dateNoDash, tempDate);
		 //previousDate = tempDate;

		dateNoDash = MettlController.dateWithoutDash(previousDate);
		map.put(dateNoDash, null);

		return map;
	}

	protected static String addDays(String date, long daysToAdd) {
		return DateHelper.addDays(DateHelper.DATE_FORMATTER_1, date, daysToAdd);
	}

	protected static String subtractDays(String date, long daysToMinus) {
		return DateHelper.subtractDays(DateHelper.DATE_FORMATTER_1, date, daysToMinus);
	}

	protected static String dateWithoutDash(String date) {
		String str = null;
		StringTokenizer stk = null;
		StringBuffer strBuf = null;

		strBuf = new StringBuffer();
		stk = new StringTokenizer(date, MettlController.CHAR_DASH, Boolean.FALSE.booleanValue());
		while (stk.hasMoreTokens()) {
			strBuf.append(stk.nextToken());
		}
		str = strBuf.toString();

		emptyStringBuffer(strBuf);
		strBuf = null;
		stk = null;
		return str;
	}
	
	@RequestMapping(value = "/m/testSpecialNeedsStudents", method = RequestMethod.GET, produces = "application/json")
	public ResponseEntity<ResponseBean> testSpecialNeedsStudents() {
		ResponseBean responseBean = null;
		HttpHeaders headers = null;
		List<String> listStudents = null;
		try {
			logger.info("entering testSpecialNeedsStudents");
			responseBean = (ResponseBean) new ResponseBean();
			responseBean.setStatus(MettlController.KEY_SUCCESS);
			headers = new HttpHeaders();
			headers.add(MettlController.CONTENT_TYPE, MettlController.CONTENT_TYPE_JSON);

			listStudents = this.fetchSpecialNeedsStudents();
			if (null != listStudents) {
				responseBean.setMessage("Special Needs Student's : " + listStudents.size());
			} else {
				responseBean.setMessage("Special Needs Student's : " + listStudents);
			}
		} catch (Exception e) {
			logger.error("error : " + e.getMessage());
		} finally {
			logger.info("exiting testSpecialNeedsStudents");
		}
		return new ResponseEntity<ResponseBean>(responseBean, headers, HttpStatus.OK);
	}

	public List<String> fetchSpecialNeedsStudents() {
		ResponseEntity<String> response = null;
		Gson gson = null;
		String url = null;
		JsonObject responseJsonObj = null;
		String temp_JSON = null;
		String[] temp_Array = null;
		List<String> listStudents = null;
		try {
			// "http://localhost:8080/studentportal/m/getApprovedStudentList";
			url = LIST_SPECIAL_NEEDS_STUDENTS_URL;

			response = this.getAtServer(url);
			if (null != response) {
				gson = new Gson();

				// logger.info("Response : " + response);
				logger.info("Response (body, status) : " + response.getBody() + ", " + response.getStatusCodeValue());
				if (HttpStatus.OK == response.getStatusCode()) {
					// create JSON like String
					//temp_JSON = "{" + "\"x\":" + response.getBody() + "}";
					//responseJsonObj = new JsonParser().parse(temp_JSON).getAsJsonObject();
					//logger.info("JSON object from JSON made here : " + responseJsonObj);
					//temp_Array = gson.fromJson(responseJsonObj.get("x").getAsJsonArray(), String[].class);
					
					responseJsonObj = new JsonParser().parse(response.getBody()).getAsJsonObject();
					logger.info("Response : " + responseJsonObj);
					temp_Array = gson.fromJson(responseJsonObj.get("listOfSapid").getAsJsonArray(), String[].class);
					
					logger.info("Special Students : "
							+ (null == temp_Array ? "null" : (temp_Array.length > 0 ? temp_Array.length : "Empty")));
					if (null != temp_Array) {
						listStudents = new ArrayList<String>();
						for (int i = 0; i < temp_Array.length; i++) {
							listStudents.add(temp_Array[i]);
						}
					}
				}
			}
		} catch (Exception ex) {
			logger.error("Exception --------" + ex.getMessage());
			throw ex;
		} finally {
			responseJsonObj = null;
			temp_JSON = null;
			temp_Array = null;

			response = null;
			url = null;
			gson = null;
		}
		return listStudents;
	}

	protected ResponseEntity<String> getAtServer(final String url) {
		String bodyAsStr = null;
		HttpHeaders headers = null;
		ResponseEntity<String> response = null;
		try {
			headers = new HttpHeaders();
			headers.add(CONTENT_TYPE, CONTENT_TYPE_JSON);

			response = this.getWithREST_API(url, bodyAsStr, headers);
		} finally {
			headers.clear();
			headers = null;
		}
		return response;
	}

	protected ResponseEntity<String> getWithREST_API(final String url, final String bodyAsStr,
			final HttpHeaders headers) {
		RestTemplate restTemplate = null;
		HttpEntity<String> entity = null;
		ResponseEntity<String> response = null;

		try {
			restTemplate = new RestTemplate();
			entity = new HttpEntity<String>(bodyAsStr, headers);
			response = restTemplate.exchange(url, HttpMethod.GET, entity, String.class);
		} finally {
			restTemplate = null;
			entity = null;
		}
		return response;
	}
}
