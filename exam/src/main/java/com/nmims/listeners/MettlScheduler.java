/**
 * 
 */
package com.nmims.listeners;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import com.nmims.beans.BatchExamBean;
import com.nmims.beans.MettlRegisterCandidateBean;
import com.nmims.beans.MettlRegisterCandidateReportBean;
import com.nmims.beans.MettlResponseBean;
import com.nmims.controllers.MettlController;
import com.nmims.daos.ExamsAssessmentsDAO;
import com.nmims.daos.MettlDAO;
import com.nmims.helpers.DateHelper;
import com.nmims.helpers.MailSender;
import com.nmims.helpers.MettlHelper;
import com.nmims.services.MettlTeeMarksService;

import org.springframework.http.HttpEntity;	
import org.springframework.http.HttpHeaders;	
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.client.RestTemplate;

import com.google.gson.Gson;
import com.google.gson.JsonElement;	
import com.google.gson.JsonObject;	
import com.google.gson.JsonParser;
import com.nmims.beans.MettlSSOInfoBean;
import com.nmims.daos.MettlTeeDAO;

/**
 * @author vil_m  
 *
 */
@Service("mettlScheduler")
public class MettlScheduler {
	
	private static final String KEY_ERROR = "error";
	private static final String KEY_SUCCESS = "success";
	
	private static final String CONTENT_TYPE = "Content-Type";
	private static final String CONTENT_TYPE_JSON = "application/json";
	private static final String DATE_FORMAT_1 = "yyyy-MM-dd";

	private static final Logger logger = LoggerFactory.getLogger("examRegisterPG");
	public static final Logger pullTimeBoundMettlMarksLogger =LoggerFactory.getLogger("pullTimeBoundMettlMarks");

	@Autowired
	ApplicationContext act;

	@Value("${SERVER}")
	private String SERVER;

	@Value("${ENVIRONMENT}")
	private String ENVIRONMENT;
	
	@Value("${PG_METTL_PUBLIC_KEY}")
	private String PG_METTL_PUBLIC_KEY;
	
	@Value("${PG_METTL_PRIVATE_KEY}")
	private String PG_METTL_PRIVATE_KEY;
	
	@Value("${MettlBaseUrl}")
	private String MettlBaseUrl;
	
	@Value("${START_METTL_TEST_PROD}")
	private String START_METTL_TEST_PROD;
	
	@Value("${END_METTL_TEST_PROD}")
	private String END_METTL_TEST_PROD;
	
	@Value("${PG_NO_IMAGE_URL}")
	private String PG_NO_IMAGE_URL;
	
	@Value("${LIST_SPECIAL_NEEDS_STUDENTS_URL}")
	private String LIST_SPECIAL_NEEDS_STUDENTS_URL;
	
	//extra time (in minutes), for Special Need Students
	@Value("${SPECIAL_NEEDS_STUDENTS_COMPENSATORY_TIME}")
	private String SPECIAL_NEEDS_STUDENTS_COMPENSATORY_TIME;
	
	@Autowired
	MettlDAO mettlDAO;
	
	@Autowired
	MettlHelper mettlHelper;
	
	@Autowired
	MailSender mailer;
	
	@Autowired
	ExamsAssessmentsDAO examsAssessmentsDAO;
	
	@Autowired
	MettlTeeMarksService mettlTeeMarksService;

	/*@Deprecated
	@Scheduled(cron = "0 0 20 * * *")
	public void cronRegisterCandidate() throws Exception {
		System.out.println("(CRON) Entering cronRegisterCandidate (Server,Environment) : ("+SERVER+","+ENVIRONMENT+")");
		
        if(!"tomcat6".equalsIgnoreCase(SERVER) || !"PROD".equalsIgnoreCase(ENVIRONMENT)) {
            System.out.println("(CRON) Exiting cronRegisterCandidate. Required (Server,Environment) : (tomcat6,PROD)");
        } else {
        	String msg = null;
        	msg = "(CRON) ........ registerCandidate ..1..(Server,Environment) : ("+SERVER+","+ENVIRONMENT+")";
			System.out.println(msg);
			logger.info(msg);
			
			backupTodayExam();
			
			msg = "(CRON) ........ registerCandidate ..2..(Server,Environment) : ("+SERVER+","+ENVIRONMENT+")";
			System.out.println(msg);
			logger.info(msg);
			
			msg = "Entering registerCandidate";
			System.out.println(msg);
			logger.info(msg);
			
			MettlRegisterCandidateBean[] arr = null;
			List<MettlRegisterCandidateBean> regCandiBeanList = null;
			//Boolean isError = Boolean.FALSE;
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
			final int retryFinal = MettlController.RETRY_FOR_ERROR_AT_METTL;//On Error, number of times to retry sending same data.
			int retry = 0;
			final int retryFinalRemoteServer = 1;//On Error, number of times to retry if error at Remote Server.
			int retryRemoteServer = 0;
			int retriedRows = 0;
			Boolean retryAtMettl = Boolean.FALSE;
			Boolean configErrorMettl = Boolean.FALSE;//InvalidKey or UnSupportedEncoding errors, complete STOP of program.
			String nextDayDate = null;
			try {
				mettlHelper.setBaseUrl(MettlBaseUrl);
				mettlHelper.setPrivateKey(PG_METTL_PRIVATE_KEY);
				mettlHelper.setPublicKey(PG_METTL_PUBLIC_KEY);
				
				nextDayDate = fetchDate();
				regCandiBeanList = mettlDAO.fetchAll_Candidates_SchedulesList(nextDayDate);
				int batchSize = MettlController.REGISTRATION_CANDIDATE_BATCHSIZE_ONE;//20
				
				message = "Total Candidates to be Registered : "+regCandiBeanList.size();
				logger.info(message);
				System.out.println(message);
				
				if(batchSize == MettlController.REGISTRATION_CANDIDATE_BATCHSIZE_ONE) {
					for(int d = 0; d < regCandiBeanList.size(); d++) {
						message = "Adding Element : (S,F,R) ("+ rowsProcessed + "," + rowsWithError + "," + retriedRows +") - "+d;
						logger.info(message);
						System.out.println(message); 
						candidateBean = regCandiBeanList.get(d);
						scheduleAccessKey = candidateBean.getScheduleAccessKey();
						sapId = candidateBean.getSapId();
						emailAddress = candidateBean.getEmailAddress();
						subject = candidateBean.getSubject();
						booked  = candidateBean.getBooked();
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
						if(openLinkFlag) {
							candidateBean.setCandidateImage(PG_NO_IMAGE_URL);
							candidateBean.setRegistrationImage(PG_NO_IMAGE_URL);
						}

						retryAtMettl = Boolean.FALSE;
						retry = retryFinal;
						retryRemoteServer = retryFinalRemoteServer;
						do {
							arr = mettlHelper.registerCandidate(candidateBean, scheduleAccessKey);
							if(arr.length == 1) {
								if(null != arr[0].getStatus() && arr[0].getStatus().equalsIgnoreCase(MettlScheduler.KEY_ERROR)) {
									logger.error("Error : "+d + " - "+arr[0].getMessage());
									
									message = arr[0].getMessage().replace("\"","");
									if (message.contains("SHA Error") || message.contains("E000") || message.contains("E607") || message.contains("E401")) {
										//E000-Some Error Occurred, E607-Candidate registration limit exceeded, E401-Authentication failed/Signature mismatch
										configErrorMettl = Boolean.TRUE;
										finalSuccess = Boolean.FALSE;
										break;
									}
									
									mettlDAO.saveCandidateRegisteredMettlInfo(sapId, scheduleAccessKey, MettlController.REGISTRATION_STATUS_FAIL, (arr[0].getStatus() + "|" + replace(arr[0].getMessage())), null);
									finalSuccess = Boolean.FALSE;
									rowsWithError++;
									//isError = Boolean.TRUE;
									//break;
									if (message.contains("E028")) {
										if(retry != 0) {
											candidateBean.setCandidateImage(PG_NO_IMAGE_URL);
											candidateBean.setRegistrationImage(PG_NO_IMAGE_URL);
											retryAtMettl = Boolean.TRUE;
											retry = retry - 1;
											retriedRows = retriedRows + 1;
											message = "RETRY...Adding Element : (S,F,R) ("+ rowsProcessed + "," + rowsWithError + "," + retriedRows +") - "+d;
											logger.info(message);
											System.out.println(message);
										} else {
											retryAtMettl = Boolean.FALSE;
										}
									} else {
										//retryAtMettl = Boolean.FALSE;
										if(retryRemoteServer != 0) {
											retryAtMettl = Boolean.TRUE;
											retryRemoteServer = retryRemoteServer - 1;
											retriedRows = retriedRows + 1;
											message = "RETRY...Adding Element : (S,F,R) ("+ rowsProcessed + "," + rowsWithError + "," + retriedRows +") - "+d;
											logger.info(message);
											System.out.println(message);
										} else {
											retryAtMettl = Boolean.FALSE;
										}
									}
								} else {
									pgUrlMettl = arr[0].getUrl();
									mettlDAO.saveCandidateRegisteredMettlInfo(sapId, scheduleAccessKey, MettlController.REGISTRATION_STATUS_PASS,
											(arr[0].getStatus() + "|" + replace(arr[0].getMessage())), pgUrlMettl);
									mettlDAO.updateExamBooking(sapId, emailAddress, subject, booked, year, month, examDate,
											examTime, examEndTime);
									if(openLinkFlag || retryAtMettl) {
										pgUrlMettl = scheduleAccessURL;
									}
									mettlDAO.saveScheduleInfo(sapId, emailAddress, subject, year, month, trackId, testTaken,
											firstName, lastName, examStartDateTime, examEndDateTime, accessStartDateTime,
											accessEndDateTime, sifySubjectCode, scheduleName, scheduleAccessKey, pgUrlMettl);
									rowsProcessed++;
									retryAtMettl = Boolean.FALSE;
								}
							}
						} while(retryAtMettl);
						
						if(configErrorMettl) {
							message = "STOP...Adding Element : (S,F,R) ("+ rowsProcessed + "," + rowsWithError + "," + retriedRows +") - "+d;
							logger.info(message);
							System.out.println(message);
							break;
						}
					}
					
					if(finalSuccess) {
						message = "Total Candidates Registered : Count (Success/Failure/Retried) : (" + rowsProcessed + "/" + rowsWithError + "/" + retriedRows + ")";
						logger.info(message);
						System.out.println(message);
					} else {
						message = "Partial Success/Failure/Retried : Count (Success/Failure/Retried) : ("+ rowsProcessed + "/" + rowsWithError + "/" + retriedRows + ")";
						logger.info(message);
						System.out.println(message);
					}
					
					//Sending Email
					if(!regCandiBeanList.isEmpty()) {
						emailRegisterCandidateReport(nextDayDate);
					} else {
						message = "No Email Sent, since no candidates to be registered for the date, "+nextDayDate+".";
						logger.info(message);
						System.out.println(message);
					}
				}
			} catch (Exception e) {
				
				message = "Failure in registering Candidates : " +e.getMessage();
				logger.error(message);
				System.out.println(message);
			} finally {
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
			System.out.println("(CRON) Exiting cronRegisterCandidate (Server,Environment) : ("+SERVER+","+ENVIRONMENT+")");
        }
	}*/
	
//	@Scheduled(cron = "0 0 20 * * *")
	public void cronRegisterCandidate() throws Exception {
		System.out.println("(CRON) Entering cronRegisterCandidate (Server,Environment) : ("+SERVER+","+ENVIRONMENT+")");
		
        if(!"tomcat6".equalsIgnoreCase(SERVER) || !"PROD".equalsIgnoreCase(ENVIRONMENT)) {
            System.out.println("(CRON) Exiting cronRegisterCandidate. Required (Server,Environment) : (tomcat6,PROD)");
        } else {
        	String msg = null;
        	msg = "(CRON) ........ registerCandidate ..1..(Server,Environment) : ("+SERVER+","+ENVIRONMENT+")";
			System.out.println(msg);
			logger.info(msg);
			
			backupTodayExam();
			
			msg = "(CRON) ........ registerCandidate ..2..(Server,Environment) : ("+SERVER+","+ENVIRONMENT+")";
			System.out.println(msg);
			logger.info(msg);
			
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

			try {
				msg = "(CRON) ........ fetching specialNeeds student's list";
				System.out.println(msg);
				logger.info(msg);
				specialNeedStudentsList = this.fetchSpecialNeedsStudents(); // new ArrayList<String>();
				msg = "Special Needs Students : "+specialNeedStudentsList;
				System.out.println(msg);
				logger.info(msg);
				
				msg = "(CRON) ........ registerCandidate ..3.....entering registerCandidate";
				System.out.println(msg);	
				logger.info(msg);

				strBuf = new StringBuffer();

				this.mettlHelper.setBaseUrl(MettlBaseUrl);
				this.mettlHelper.setPrivateKey(PG_METTL_PRIVATE_KEY);
				this.mettlHelper.setPublicKey(PG_METTL_PUBLIC_KEY);

				try {
					pDate = fetchDate();
					msg = "(CRON) ........ registerCandidate ..3..for the Date : " + pDate;
					System.out.println(msg);
					logger.info(msg);

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
						System.out.println(message);
					}
				} catch (Exception e) {
					//responseBean.setStatus(MettlController.KEY_ERROR);
					logger.error("Failure : " + e.getMessage());
					strBuf.append("| for (Date, Status, Message) : (" + pDate + ", " + MettlController.KEY_ERROR + ", "
							+ e.getMessage() + ")");
				} finally {
					regCandiBeanList.clear();
					regCandiBeanList = null;

					logger.info("Finished Processing ------------for the Date : " + pDate);
					strBuf.append("| for (Date, Status, Message) : (" + pDate + ", " + MettlController.KEY_SUCCESS
							+ ", Finished" + ")");
				}

			} catch (Exception e) {
				//responseBean.setStatus(MettlController.KEY_ERROR);
				logger.error("Setup Failure : " + e.getMessage());
				strBuf.append("Setup Failure : " + e.getMessage());
			} finally {

				//responseBean.setMessage(strBuf.toString());

				emptyStringBuffer(strBuf);
				
				if(null != specialNeedStudentsList) {
					specialNeedStudentsList.clear();
					specialNeedStudentsList = null;
				}

				msg = "FINISHING ------------registerCandidate------------";
				System.out.println(msg);
				logger.info(msg);
			}
			
			System.out.println("(CRON) Exiting cronRegisterCandidate (Server,Environment) : ("+SERVER+","+ENVIRONMENT+")");
        }
	}

	
	@Scheduled(cron = "0 0 20 * * *")
	public void cronRegisterCandidateWaitingRoom() throws Exception {
		System.out.println("(CRON) Entering cronRegisterCandidateWaitingRoom (Server,Environment) : ("+SERVER+","+ENVIRONMENT+")");
		
	    if(!"tomcat6".equalsIgnoreCase(SERVER) || !"PROD".equalsIgnoreCase(ENVIRONMENT)) {
	        System.out.println("(CRON) Exiting cronRegisterCandidateWaitingRoom. Required (Server,Environment) : (tomcat6,PROD)");
	    } else {
	    	String msg = null;
	    	msg = "(CRON) ........ registerCandidate ..1..(Server,Environment) : ("+SERVER+","+ENVIRONMENT+")";
			System.out.println(msg);
			logger.info(msg);
			
			backupTodayExamWaitingRoom();
			
			msg = "(CRON) ........ registerCandidate ..2..(Server,Environment) : ("+SERVER+","+ENVIRONMENT+")";
			System.out.println(msg);
			logger.info(msg);
			
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
	
			try {
				msg = "(CRON) ........ fetching specialNeeds student's list";
				System.out.println(msg);
				logger.info(msg);
				specialNeedStudentsList = this.fetchSpecialNeedsStudents(); // new ArrayList<String>();
				msg = "Special Needs Students : "+specialNeedStudentsList;
				System.out.println(msg);
				logger.info(msg);
				
				msg = "(CRON) ........ registerCandidate ..3.....entering registerCandidate";
				System.out.println(msg);	
				logger.info(msg);
	
				strBuf = new StringBuffer();
	
				this.mettlHelper.setBaseUrl(MettlBaseUrl);
				this.mettlHelper.setPrivateKey(PG_METTL_PRIVATE_KEY);
				this.mettlHelper.setPublicKey(PG_METTL_PUBLIC_KEY);
	
				try {
					pDate = fetchDate();
					msg = "(CRON) ........ registerCandidate ..3..for the Date : " + pDate;
					System.out.println(msg);
					logger.info(msg);
	
					regCandiBeanList = this.fetchAll_Candidates_Waiting_Room(this.mettlDAO, pDate);
	
					this.processAllCandidatesWaitingRoom(this.mettlDAO, this.mettlHelper, pDate, batchSize, retryFinal,
							retryFinalRemoteServer, noImageURL, regCandiBeanList, maxCompensatoryTime,
							specialNeedStudentsList);
	
					// Sending Email
//					if (!regCandiBeanList.isEmpty()) {
						emailRegisterCandidateReport(pDate);
//					} else {
//						message = "No Email Sent, since no candidates to be registered for the date, " + pDate + ".";
//						logger.info(message);
//						System.out.println(message);
//					}
				} catch (Exception e) {
					//responseBean.setStatus(MettlController.KEY_ERROR);
					logger.error("Failure : " + e.getMessage());
					strBuf.append("| for (Date, Status, Message) : (" + pDate + ", " + MettlController.KEY_ERROR + ", "
							+ e.getMessage() + ")");
				} finally {
					regCandiBeanList.clear();
					regCandiBeanList = null;
	
					logger.info("Finished Processing ------------for the Date : " + pDate);
					strBuf.append("| for (Date, Status, Message) : (" + pDate + ", " + MettlController.KEY_SUCCESS
							+ ", Finished" + ")");
				}
	
			} catch (Exception e) {
				//responseBean.setStatus(MettlController.KEY_ERROR);
				logger.error("Setup Failure : " + e.getMessage());
				strBuf.append("Setup Failure : " + e.getMessage());
			} finally {
	
				//responseBean.setMessage(strBuf.toString());
	
				emptyStringBuffer(strBuf);
				
				if(null != specialNeedStudentsList) {
					specialNeedStudentsList.clear();
					specialNeedStudentsList = null;
				}
	
				msg = "FINISHING ------------registerCandidate------------";
				System.out.println(msg);
				logger.info(msg);
			}
			
			System.out.println("(CRON) Exiting cronRegisterCandidate (Server,Environment) : ("+SERVER+","+ENVIRONMENT+")");
	    }
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
					logger.info(message);
					System.out.println(message);

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
										logger.info(message);
										System.out.println(message);
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
										logger.info(message);
										System.out.println(message);
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
						logger.info(message);
						System.out.println(message);
						
						message = "REGISTER CANDIDATE -(Date, Total) (" + registerForDate + ", "
								+ totalCandidates + ") (S, F, R) (" + rowsProcessed + ", "
								+ rowsWithError + ", " + retriedRows + ") - " + d + " -STOP-";
						logger.info(message);
						System.out.println(message);
						
						message = "ABRUPT STOP................................";
						logger.info(message);
						System.out.println(message);
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

	protected void processAllCandidatesWaitingRoom(final MettlDAO mettlDAO, MettlHelper mettlHelper, final String registerForDate,
			final int batchSize, final int retryFinal, final int retryFinalRemoteServer, final String noImageUrl,
			final List<MettlRegisterCandidateBean> regCandiBeanList, final String maxCompensatoryTime,
			final List<String> specialNeedStudentsList) {
	
		logger.info(
				"processAllCandidatesWaitingRoom : (mettlDAO, mettlHelper, registerForDate, batchSize, retryFinal, retryFinalRemoteServer, noImageUrl, maxCompensatoryTime)  ("
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
			logger.info("processAllCandidatesWaitingRoom : Total Candidates to be Registered : " + totalCandidates);
	
			if (MettlController.REGISTRATION_CANDIDATE_BATCHSIZE_ONE == batchSize) {
				for (int d = 0; d < totalCandidates; d++) {
					message = "REGISTER CANDIDATE -(Date, Total) (" + registerForDate + ", " + totalCandidates
							+ ") (S, F, R) (" + rowsProcessed + ", " + rowsWithError + ", " + retriedRows + ") - " + d;
					logger.info(message);
					System.out.println(message);
	
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
										logger.info(message);
										System.out.println(message);
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
										logger.info(message);
										System.out.println(message);
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
						logger.info(message);
						System.out.println(message);
						
						message = "REGISTER CANDIDATE -(Date, Total) (" + registerForDate + ", "
								+ totalCandidates + ") (S, F, R) (" + rowsProcessed + ", "
								+ rowsWithError + ", " + retriedRows + ") - " + d + " -STOP-";
						logger.info(message);
						System.out.println(message);
						
						message = "ABRUPT STOP................................";
						logger.info(message);
						System.out.println(message);
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
		logger.info("saveStatusWaitingRoom : (mettlDAO, candidateBean, status, message) (" + mettlDAO + ", " + candidateBean + ", "
				+ status + ", " + message + ")");
		
		this.saveStatusWaitingRoom(mettlDAO, candidateBean, status, message, null, null);
	}

	protected List<MettlRegisterCandidateBean> fetchAll_Candidates(final MettlDAO mettlDAO, final String pDate) {
		logger.info("fetchAll_Candidates : (mettlDAO, pDate)  (" + mettlDAO + ", " + pDate + ")");
		System.out.println("fetchAll_Candidates : (mettlDAO, pDate)  (" + mettlDAO + ", " + pDate + ")");

		MettlRegisterCandidateBean regCandiBean = null;
		List<MettlRegisterCandidateBean> regCandiBeanList = null;

		// Prepare Data
		/*regCandiBeanList = new ArrayList<MettlRegisterCandidateBean>(); 
		 for (int a = 518; a < 520; a++) { 
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
	
	/*@Deprecated
	String fetchDate() {
		//String currentDate = null;
		String nextDayDate = null;
		SimpleDateFormat sdf = new SimpleDateFormat(MettlScheduler.DATE_FORMAT_1);
		//Date dt = new Date(timeInLong);
		//currentDate = sdf.format(System.currentTimeMillis());
		nextDayDate = sdf.format(System.currentTimeMillis() + (1*24*60*60*1000));//dt
		System.out.println("NextDay Date : " +nextDayDate);
		logger.info("NextDay Date : " +nextDayDate);
		sdf = null;
		//nextDayDate = "2020-07-15";
		return nextDayDate;
	}*/
	
	protected List<MettlRegisterCandidateBean> fetchAll_Candidates_Waiting_Room(final MettlDAO mettlDAO, final String pDate) {
		logger.info("fetchAll_Candidates_Waiting_Room : (mettlDAO, pDate)  (" + mettlDAO + ", " + pDate + ")");
		System.out.println("fetchAll_Candidates_Waiting_Room : (mettlDAO, pDate)  (" + mettlDAO + ", " + pDate + ")");
	
		MettlRegisterCandidateBean regCandiBean = null;
		List<MettlRegisterCandidateBean> regCandiBeanList = null;
	
		// Prepare Data
		/*regCandiBeanList = new ArrayList<MettlRegisterCandidateBean>(); 
		 for (int a = 518; a < 520; a++) { 
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
			logger.info("fetchAll_Candidates_Waiting_Room : Total Candidates : " + regCandiBeanList.size());
		}
		return regCandiBeanList;
	}

	String fetchDate() {
		String nextDayDate = null;
		nextDayDate = DateHelper.addDays(DateHelper.DATE_FORMATTER_1, 1);//"2020-07-15"
		System.out.println("NextDay Date : " + nextDayDate);
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
	
	void backupTodayExam() {
		//String[] timeArr = new String[] {"10:00:00", "17:00:00"};
		String[] timeArr = new String[] {"09:00:00", "13:00:00", "17:00:00"};
		String backupDate = null;
		try {
			backupDate = fetchCurrentDate();
			for(int i = 0; i < timeArr.length; i++) {
				backupDelete(backupDate + " " + timeArr[i]);
			}
		} catch (Exception e) {
			
			logger.error("backupTodayExam : " +e.getMessage());
			System.out.println("backupTodayExam : " +e.getMessage());
		} finally {
			backupDate = null;
			timeArr = null;
		}
	}
	
	void backupTodayExamWaitingRoom() {
		//String[] timeArr = new String[] {"10:00:00", "17:00:00"};
		String[] timeArr = new String[] {"09:00:00", "13:00:00", "17:00:00"};
		String backupDate = null;
		try {
			backupDate = fetchCurrentDate();
			for(int i = 0; i < timeArr.length; i++) {
				backupDeleteWaitingRoom(backupDate + " " + timeArr[i]);
			}
		} catch (Exception e) {
			
			logger.error("backupDeleteWaitingRoom : " +e.getMessage());
			System.out.println("backupDeleteWaitingRoom : " +e.getMessage());
		} finally {
			backupDate = null;
			timeArr = null;
		}
	}
	
	/*@Deprecated
	String fetchCurrentDate() {
		String currentDate = null;
		SimpleDateFormat sdf = new SimpleDateFormat(MettlScheduler.DATE_FORMAT_1);
		currentDate = sdf.format(System.currentTimeMillis());
		System.out.println("Current Date : " +currentDate);
		logger.info("Current Date : " +currentDate);
		sdf = null;
		return currentDate;
	}*/
	
	String fetchCurrentDate() {
		return DateHelper.addDays(DateHelper.DATE_FORMATTER_1, 0);
	}
	
	void backupDelete(String examStartDateTime) {
		MettlRegisterCandidateBean bean = null;
		List<MettlRegisterCandidateBean> beanList = null;
		boolean isSuccess = Boolean.FALSE;
		String message  = null;
		int rows = -1;
		
		try {
			bean = new MettlRegisterCandidateBean();
			bean.setExamStartDateTime(examStartDateTime);
			
			message = "Backup PGScheduleInfo ("+examStartDateTime+")...";
			System.out.println(message);
			logger.info(message);
			
			mettlDAO.start_Transaction_U_PR("backupPGScheduleInfo");
			beanList = mettlDAO.fetchAll_ScheduleInfo(bean);
			if(null != beanList && !beanList.isEmpty()) {
				message = "Backup PGScheduleInfo ("+examStartDateTime+","+beanList.size()+")";
				System.out.println(message);
				logger.info(message);
				isSuccess = mettlDAO.batchSaveScheduleInfo(beanList);
				if(isSuccess) {
					mettlDAO.end_Transaction(Boolean.TRUE);//Only for backup 
					message = "Delete from PGScheduleInfo ("+examStartDateTime+","+beanList.size()+")";
					System.out.println(message);
					logger.info(message);
					rows = mettlDAO.deleteScheduleInfo(bean);//starts it own transaction.
					message = "Deleted................... ("+examStartDateTime+","+rows+")";
					System.out.println(message);
					logger.info(message);
				} else {
					mettlDAO.end_Transaction(Boolean.FALSE);
					message = "Nothing Backed up nor Deleted!";
					System.out.println(message);
					logger.info(message);
				}
			} else {
				message = "Nothing Found!";
				System.out.println(message);
				logger.info(message);
				mettlDAO.end_Transaction(Boolean.FALSE);
			}
		} catch(Exception e) {
			
			logger.error("backupDelete : "+ e.getMessage());
			mettlDAO.end_Transaction(Boolean.FALSE);
		}
	}
	
	void backupDeleteWaitingRoom(String examStartDateTime) {
		MettlRegisterCandidateBean bean = null;
		List<MettlRegisterCandidateBean> beanList = null;
		boolean isSuccess = Boolean.FALSE;
		String message  = null;
		int rows = -1;
		
		try {
			bean = new MettlRegisterCandidateBean();
			bean.setExamStartDateTime(examStartDateTime);
			
			message = "Backup PGScheduleInfo ("+examStartDateTime+")...";
			System.out.println(message);
			logger.info(message);
			
			mettlDAO.start_Transaction_U_PR("backupPGScheduleInfo");
			beanList = mettlDAO.fetchAll_ScheduleInfo_Waiting_Room(bean);
			if(null != beanList && !beanList.isEmpty()) {
				message = "Backup backupDeleteWaitingRoom ("+examStartDateTime+","+beanList.size()+")";
				System.out.println(message);
				logger.info(message);
				isSuccess = mettlDAO.batchSaveScheduleInfoWaitingRoom(beanList);
				if(isSuccess) {
					mettlDAO.end_Transaction(Boolean.TRUE);//Only for backup 
					message = "Delete from PGScheduleInfo ("+examStartDateTime+","+beanList.size()+")";
					System.out.println(message);
					logger.info(message);
					rows = mettlDAO.deleteScheduleInfo(bean);//starts it own transaction.
					message = "Deleted................... ("+examStartDateTime+","+rows+")";
					System.out.println(message);
					logger.info(message);
				} else {
					mettlDAO.end_Transaction(Boolean.FALSE);
					message = "Nothing Backed up nor Deleted!";
					System.out.println(message);
					logger.info(message);
				}
			} else {
				message = "Nothing Found!";
				System.out.println(message);
				logger.info(message);
				mettlDAO.end_Transaction(Boolean.FALSE);
			}
		} catch(Exception e) {
			
			logger.error("backupDelete : "+ e.getMessage());
			mettlDAO.end_Transaction(Boolean.FALSE);
		}
	}
	
	void emailRegisterCandidateReport(String nextDayDate) {
		List<MettlRegisterCandidateReportBean> list1 = null;
		List<MettlRegisterCandidateReportBean> list2 = null;
		
		StringBuffer strBuf = null;
		String emailMessage = null;
		try {
			//MailSender mailSender = (MailSender)act.getBean("mailer");
			
			list1 = mettlDAO.fetchAll_Candidates_SummaryList(nextDayDate);
			list2 = mettlDAO.fetchAll_Candidates_ErrorList(nextDayDate);
			
			strBuf = new StringBuffer();
			strBuf.append("Dear Team, <br>");
			strBuf.append("Register Candidate, <br><br>");
			
			strBuf.append("<style type=text/css>");
			strBuf.append(".tg  {border-collapse:collapse;border-spacing:0;}");
			strBuf.append(".tg td{border-color:black;border-style:solid;border-width:1px;font-family:Arial, sans-serif;font-size:14px;");
			strBuf.append("overflow:hidden;padding:10px 5px;word-break:normal;}");
			strBuf.append(".tg th{border-color:black;border-style:solid;border-width:1px;font-family:Arial, sans-serif;font-size:14px;");
			strBuf.append("font-weight:normal;overflow:hidden;padding:10px 5px;word-break:normal;}");
			strBuf.append(".tg .tg-af47{background-color:#ffffff;border-color:inherit;color:#000000;text-align:center;vertical-align:top}");
			strBuf.append(".tg .tg-vvj0{background-color:#ecf4ff;border-color:inherit;color:#000000;font-weight:bold;text-align:center;vertical-align:top}");
			strBuf.append("</style>");
			
			if(null != list1 && !list1.isEmpty()) {
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
				strBuf.append("<td class=tg-af47>").append(list1.get(0).getTotalCandidatesExamBookings()).append("</td>");
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
			
			if(null != list2) {
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
				if(list2.isEmpty()) {
					strBuf.append("<tr>");
					strBuf.append("<td class=tg-af47 colspan=8>No Error(s)</td>");
					strBuf.append("</tr>");
				} else {
					for(MettlRegisterCandidateReportBean rptBean : list2) {
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
	
	//------//
	/*CREATE DEFINER=`root`@`localhost` PROCEDURE `getall_candidates_schedules`(
	IN examdateIN varchar(24))
	BEGIN
	
	SELECT
		`bk`.`firstName`,
		#`bk`.`lastName`,
	    #IFNULL(`bk`.`lastName`, '.') `lastName`,
	    IFNULL(if(trim(`bk`.`lastName`) = '','.',trim(`bk`.`lastName`)), '.') `lastName`,
		`bk`.`studentid`,
		#`bk`.`emailId`,
	    trim(`bk`.`emailId`) `emailId`,
		`bk`.`imageUrl`,
	     IFNULL(`bk`.`imageUrl`, '0') `openLinkFlag`,
	    `esm`.`schedule_accessUrl` AS `schedule_accessUrl`,
		`esm`.`schedule_accessKey`,
	    `bk`.`subject`,
		`bk`.`year`,
		`bk`.`month`,
		`bk`.`booked`,
		`bk`.`examDate`,
		`bk`.`examTime`,
		`bk`.`examEndTime`,
		`bk`.`trackId`,
		`bk`.`testTaken`,
		`pga`.`sifyCode` AS `sifySubjectCode`,
		date_format(`esm`.`exam_start_date_time`, '%Y-%m-%d %H:%i:%s') AS `examStartDateTime`,
		date_format(`esm`.`exam_end_date_time`, '%Y-%m-%d %H:%i:%s') AS `examEndDateTime`,
		date_format((`esm`.`exam_start_date_time` - INTERVAL 0 MINUTE), '%Y-%m-%d %H:%i:%s') AS `accessStartDateTime`,
		date_format((`esm`.`exam_end_date_time` + INTERVAL 3 HOUR), '%Y-%m-%d %H:%i:%s') AS `accessEndDateTime`,
		`esm`.`schedule_name` AS `scheduleName`
	FROM
		(SELECT
	    	`assessments_id`, `schedule_accessKey`, `schedule_name`,
	        `exam_start_date_time`,`exam_end_date_time`,`schedule_accessUrl`
		FROM
	    	`exam`.`exams_schedule_mettl`
		WHERE
	    	`exam_start_date_time` LIKE concat(examdateIN,'%')) `esm`
	    	INNER JOIN
		`exam`.`pg_assessment` `pga` ON `esm`.`assessments_id` = `pga`.`assessmentId`
	    	INNER JOIN
		`exam`.`program_sem_subject` `pss` ON `pss`.`sifySubjectCode` = `pga`.`sifyCode`
	    	AND `pss`.`active` = 'Y'
	    	INNER JOIN
		(SELECT
	    	`eb`.`booked`,
	        	`eb`.`year`,
	        	`eb`.`month`,
	        	`eb`.`examMode`,
	        	`eb`.`subject`,
	        	`eb`.`centerId`,
	        	`eb`.`examDate`,
	        	`eb`.`examTime`,
	        	`eb`.`examEndTime`,
				`eb`.`trackId`,
				`eb`.`testTaken`,
	        	`s`.*
		FROM
	    	`exam`.`exambookings` `eb`
		INNER JOIN (SELECT
	    	`s2`.`sapid` AS studentid,
	        	`s2`.`firstName`,
	        	`s2`.`lastName`,
	        	`s2`.`imageUrl`,
	        	`s2`.`mobile`,
	        	`s2`.`emailId`,
	        	`s2`.`consumerProgramStructureId`
		FROM
	    	`exam`.`students` `s2`
		WHERE
	    	`sem` = (SELECT
	            	MAX(sem)
	        	FROM
	            	`exam`.`students` `s3`
	        	WHERE
	            	s2.sapid = s3.sapid)
		GROUP BY sapid) s ON s.studentid = eb.sapid
		WHERE
	    	eb.booked = 'Y' AND eb.year = 2020 and (eb.emailId is null or eb.emailId = '')
	        	AND eb.month = 'Sep'
	        	AND eb.examMode = 'Online'
	        	AND `eb`.`subject` NOT IN ('Project' , 'Module 4 - Project', 'Simulation: Mimic Pro', 'Simulation: Mimic Social')
	        	AND `eb`.`centerId` <> '-1'
	        	AND `eb`.`examDate` = examdateIN) `bk` ON `bk`.`consumerProgramStructureId` = `pss`.`consumerProgramStructureId`
	    	AND `bk`.`subject` = `pss`.`subject`
	    	AND `esm`.`schedule_name` = (CONCAT(`pss`.`sifySubjectCode`,
	        	DATE_FORMAT(`bk`.`examDate`, '%w%m%e%Y'),
	        	DATE_FORMAT(`bk`.`examTime`, '%H%i%s'),
	        	DATE_FORMAT(`bk`.`examEndTime`, '%H%i%s')));
	
	END
	*/
	//------//
	
	@Scheduled(cron="0 1 1 * * *")	
	public void putDataIntoPGScheduleInfoMettl() {
		logger.info("(CRON) Entering putDataIntoPGScheduleInfoMettl (Server,Environment) : ("+SERVER+","+ENVIRONMENT+")");
		if(!SERVER.equals("TomcatContent")) {
			logger.info("(CRON) Exiting putDataIntoPGScheduleInfoMettl. Required (Server) : (TomcatContent)");
			return;
		}
		try {	
			String url = "https://studentzone-ngasce.nmims.edu/exam/m/getGDMettlData";	
			MettlTeeDAO mettlTeeDAO = (MettlTeeDAO) act.getBean("mettlTeeDAO");	
			/*if(!mettlTeeDAO.moveDataToHistory()){	
				return;	
			}*/	
			HttpHeaders headers =  new HttpHeaders();	
			headers.add("Accept", "application/json");	
			headers.add("Content-Type", "application/json");	
			RestTemplate restTemplate = new RestTemplate();	
			HttpEntity<String> entity = new HttpEntity<String>("",headers);	
			ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.GET, entity, String.class);	
			JsonObject jsonObj = new JsonParser().parse(response.getBody()).getAsJsonObject();	
			if ("200".equalsIgnoreCase(response.getStatusCode().toString())) {	
				System.out.println("----------->>>>>>>> inside success");	
				if(!"success".equalsIgnoreCase(jsonObj.get("status").getAsString())) {	
					return;	
				}	
				for (JsonElement datas : jsonObj.get("data").getAsJsonArray()) {	
					JsonObject data = datas.getAsJsonObject();	
					MettlSSOInfoBean mettlSSOInfoBean = new MettlSSOInfoBean();	
					mettlSSOInfoBean.setSubject(data.get("subject").getAsString());	
					mettlSSOInfoBean.setYear(data.get("year").getAsString());	
					mettlSSOInfoBean.setMonth(data.get("month").getAsString());	
					mettlSSOInfoBean.setTrackId(data.get("trackId").getAsString());	
					mettlSSOInfoBean.setSapid(data.get("sapid").getAsString());	
					//mettlSSOInfoBean.setTestTaken(data.get("testTaken").getAsString());	
					if(data.get("firstname") != null) {	
						mettlSSOInfoBean.setFirstname(data.get("firstname").getAsString());	
					}	
					if(data.get("lastname") != null) {	
						mettlSSOInfoBean.setLastname(data.get("lastname").getAsString());	
					}	
					mettlSSOInfoBean.setEmailId(data.get("emailId").getAsString());	
					mettlSSOInfoBean.setExamStartDateTime(data.get("examStartDateTime").getAsString());	
					mettlSSOInfoBean.setExamEndDateTime(data.get("examEndDateTime").getAsString());	
					mettlSSOInfoBean.setAccessStartDateTime(data.get("accessStartDateTime").getAsString());	
					mettlSSOInfoBean.setAccessEndDateTime(data.get("accessEndDateTime").getAsString());	
					mettlSSOInfoBean.setSifySubjectCode(data.get("sifySubjectCode").getAsString());	
					mettlSSOInfoBean.setScheduleName(data.get("scheduleName").getAsString());	
					mettlSSOInfoBean.setAcessKey(data.get("acessKey").getAsString());	
					mettlSSOInfoBean.setJoinURL(data.get("joinURL").getAsString());	
					//mettlSSOInfoBean.setCreatedBy(data.get("createdBy").getAsString());	
					//mettlSSOInfoBean.setCreatedDateTime(data.get("createdDateTime").getAsString());	
					mettlTeeDAO.insertIntoPGScheduleInfo(mettlSSOInfoBean);	
					System.out.println("---------->>>>>> data.get(\"acessKey\").getAsString() : " + data.get("acessKey").getAsString());	
				}	
			}else {	
				System.out.println("----------->>>>>>>> inside failed");	
			}	
		}	
		catch (Exception e) {	
			// TODO: handle exception	
			e.printStackTrace();	
		}	
	}
	
	@Scheduled(cron = "0 0 20 * * 0,5,6")  //Run Friday,Saturday,Sunday at 08:00pm
	public void checkTimeBoundStudentsAttemptStatus()
	{
		if(!"tomcat6".equalsIgnoreCase(SERVER) || !"PROD".equalsIgnoreCase(ENVIRONMENT)) {
			pullTimeBoundMettlMarksLogger.info("Not Running checkTimeBoundStudentsAttemptStatus, not found Tomcat6 and Prod Environment");
		} 
		else 
		{
			String todayDate=fetchCurrentDate();
			mettlTeeMarksService.checkAttemptStatus(todayDate);
		}
	}
	
	

}
