package com.nmims.stratergies.impl;

import java.util.HashSet;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.nmims.beans.ExamBookingTransactionBean;
import com.nmims.beans.MettlRegisterCandidateBean;
import com.nmims.controllers.MettlController;
import com.nmims.daos.ExamCenterDAO;
import com.nmims.daos.MettlDAO;
import com.nmims.helpers.MettlHelper;
import com.nmims.stratergies.ExamRegistrationRealTimeStrategy;

@Service("examRegistrationRealTimeStrategyImpl")
public class ExamRegistrationRealTimeStrategyImpl implements ExamRegistrationRealTimeStrategy{
	
	private static final Integer REGISTRATION_CANDIDATE_BATCHSIZE_ONE = 1;
	private static final Integer RETRY_FOR_ERROR_AT_METTL = 1;
	public static final String KEY_ERROR = "error";
	public static final String KEY_SUCCESS = "success";
	public static final Integer REGISTRATION_STATUS_PASS = 0;
	public static final Integer REGISTRATION_STATUS_FAIL = 1;
	
	@Value("${SPECIAL_NEEDS_STUDENTS_COMPENSATORY_TIME}")
	private String SPECIAL_NEEDS_STUDENTS_COMPENSATORY_TIME;
	@Value("${PG_NO_IMAGE_URL}")
	private String PG_NO_IMAGE_URL;
	@Value("${PG_METTL_PUBLIC_KEY}")
	private String PG_METTL_PUBLIC_KEY;
	@Value("${PG_METTL_PRIVATE_KEY}")
	private String PG_METTL_PRIVATE_KEY;
	@Value("${MettlBaseUrl}")
	private String MettlBaseUrl;
	
	@Autowired
	public MettlController mettlController;

	@Autowired
	MettlDAO mettlDAO;
	
	@Autowired
	MettlHelper mettlHelper;
	
	@Autowired
	ExamCenterDAO examDao;
	
	public static final Logger logger = LoggerFactory.getLogger("examRegisterPG");
	
	public void registrationOnMettlAndReleaseBooking(List<ExamBookingTransactionBean> currentTransactionBean,List<ExamBookingTransactionBean> toReleaseBookingsList)
	{
		try
		{
			if(currentTransactionBean!=null && currentTransactionBean.size()>0)
			{
				HashSet<String> examDateSet = new HashSet<String>();
				for(ExamBookingTransactionBean examBean:currentTransactionBean) {
					examDateSet.add(examBean.getExamDate());
				}
				String examYear=currentTransactionBean.get(0).getYear();
				String examMonth=currentTransactionBean.get(0).getMonth();
				String sapid=currentTransactionBean.get(0).getSapid();
				logger.info("Start registering student on mettl real time:sapid:"+sapid+",examYear:"+examYear+",examMonth:"+examMonth);
				registerStudentOnMettl(examDateSet,sapid,examMonth,examYear);
				logger.info("End registering student on mettl real time:sapid:"+sapid+",examYear:"+examYear+",examMonth:"+examMonth);
			}
			if(toReleaseBookingsList!=null && toReleaseBookingsList.size()>0)
			{
				logger.info("Start releasing booking real time:sapid:"+toReleaseBookingsList.get(0).getSapid());
				releaseBooking(toReleaseBookingsList);
				logger.info("End releasing booking real time:sapid:"+toReleaseBookingsList.get(0).getSapid());
			}
		}
		catch (Exception e) {
			logger.error("Exception in registrationOnMettlAndReleaseBooking is : " + e.getMessage());
		}
	}
	
	public void registerStudentOnMettl(HashSet<String> examDateSet,String sapid,String examMonth,String examYear)
	{
		final int batchSize = REGISTRATION_CANDIDATE_BATCHSIZE_ONE;
		final int retryFinal = RETRY_FOR_ERROR_AT_METTL;// On Error, number of times to retry sending
		final int retryFinalRemoteServer = RETRY_FOR_ERROR_AT_METTL;// On Error, number of times to retry if error at Remote Server.
		final String maxCompensatoryTime = SPECIAL_NEEDS_STUDENTS_COMPENSATORY_TIME;
		final String noImageURL = PG_NO_IMAGE_URL;
		
		List<MettlRegisterCandidateBean> regCandiBeanList = null;
		List<String> specialNeedStudentsList = null;

		try {
			logger.info("FETCHING ------------specialNeeds Student's List------------");
			//fetch Special Needs Students List
			specialNeedStudentsList = mettlController.fetchSpecialNeedsStudents(); //new ArrayList<String>();
			
			logger.info("STARTING ------------getOne_Candidate_Schedules------------");

			mettlHelper.setBaseUrl(MettlBaseUrl);
			mettlHelper.setPrivateKey(PG_METTL_PRIVATE_KEY);
			mettlHelper.setPublicKey(PG_METTL_PUBLIC_KEY);

			for(String examDate:examDateSet)
			{
				try
				{
					logger.info("Registering ------------for the Date : " + examDate + " for sapid:"+sapid);
					regCandiBeanList = getOne_Candidate_Schedules(examDate,sapid,examMonth,examYear);
					if(regCandiBeanList!=null && regCandiBeanList.size()>0) {
					this.processOneCandidateForOneSchedule(examDate, batchSize, retryFinal,
							retryFinalRemoteServer, noImageURL, regCandiBeanList, maxCompensatoryTime,
							specialNeedStudentsList);	
					}
					else {
						logger.info("Not running processOneCandidateForOneSchedule since no records found for:sapid:"+sapid+",examDate:"+examDate+",examYear:"+examYear+",examMonth:"+examMonth);
					}
				}
				catch (Exception e) {
					logger.error("Exception is : " + e.getMessage());
				} finally {
					regCandiBeanList.clear();
					regCandiBeanList = null;
					logger.info("Finished Processing ------------for the Date : " + examDate);
				}
			}
		}
		catch (Exception e) 
		{
			logger.error("Exception is : " + e.getMessage());
		} 
		finally 
		{
			if(null != specialNeedStudentsList) 
			{
				specialNeedStudentsList.clear();
				specialNeedStudentsList = null;
			}

			logger.info("FINISHING --------------getOne_Candidate_Schedules----------");
		}
	}
	
	public void releaseBooking(List<ExamBookingTransactionBean> toReleaseBookingsList)
	{
		try
		{
			archiveReleasedBookingForOneSapid(toReleaseBookingsList);
		}
		catch(Exception e)
		{
			logger.error("Error while release booking is "+e.getMessage());
		}
	}
	
	private void archiveReleasedBookingForOneSapid(List<ExamBookingTransactionBean> toReleaseBookingsList)
	{
		for(ExamBookingTransactionBean examBean:toReleaseBookingsList)
		{
			String examStartDateTime=examBean.getExamDate()+" "+examBean.getExamTime();
			boolean archiveFlag=examDao.archiveReleasedExamBooking(examBean.getSapid(), examBean.getSubject(),examBean.getYear(),examBean.getMonth(),examBean.getTrackId(),examStartDateTime);
			if(archiveFlag){
				boolean deleteFlag=examDao.deleteReleasedExamBooking(examBean.getSapid(), examBean.getSubject(),examBean.getYear(),examBean.getMonth(),examBean.getTrackId(),examStartDateTime);
				if(deleteFlag)
					logger.info("Successfully archived released booking records for Sapid:"+examBean.getSapid()+",Subject:"+examBean.getSubject()+",Year:"+examBean.getYear()+",Month:"+examBean.getMonth()+",trackid:"+examBean.getTrackId()+",startDateTime:"+examStartDateTime);
				else
					logger.error("Error while deleting records for sapid:"+examBean.getSapid()+",Subject:"+examBean.getSubject()+",Year:"+examBean.getYear()+",Month:"+examBean.getMonth()+",trackid:"+examBean.getTrackId()+",startDateTime:"+examStartDateTime);
			}
			else {
			logger.error("Error while archiving exam booking for Sapid:"+examBean.getSapid()+",Subject:"+examBean.getSubject()+",Year:"+examBean.getYear()+",Month:"+examBean.getMonth()+",trackid:"+examBean.getTrackId()+",startDateTime:"+examStartDateTime);
			}
		}
	}
	
	private List<MettlRegisterCandidateBean> getOne_Candidate_Schedules(String examDate,String sapid,String examMonth,String examYear) 
	{
		List<MettlRegisterCandidateBean> regCandiBeanList = null;
		regCandiBeanList = mettlDAO.getOne_Candidate_Schedules(examDate,sapid,examMonth,examYear);
		if (null != regCandiBeanList) {
			logger.info("getOne_Candidate_Schedules : Total Records : " + regCandiBeanList.size()+" for date:"+examDate);
		}
		return regCandiBeanList;
	}
	
	private void processOneCandidateForOneSchedule(final String registerForDate,
			final int batchSize, final int retryFinal, final int retryFinalRemoteServer, final String noImageUrl,
			final List<MettlRegisterCandidateBean> regCandiBeanList, final String maxCompensatoryTime,
			final List<String> specialNeedStudentsList) {

		logger.info(
				"processOneCandidateForOneSchedule : (registerForDate, batchSize, retryFinal, retryFinalRemoteServer, noImageUrl, maxCompensatoryTime)  ("
						+ registerForDate + "," + batchSize + ", " + retryFinal
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

				for (int d = 0; d < totalCandidates; d++) {
					message = "REGISTER CANDIDATE -(Date, Total) (" + registerForDate + ", " + totalCandidates
							+ ") (S, F, R) (" + rowsProcessed + ", " + rowsWithError + ", " + retriedRows + ") - " + d;
					logger.info(message);

					candidateBean = regCandiBeanList.get(d);
					scheduleAccessURL = candidateBean.getScheduleAccessURL();
					openLinkFlag = candidateBean.getOpenLinkFlag();
					if (openLinkFlag) {
						candidateBean.setCandidateImage(noImageUrl);// (PG_NO_IMAGE_URL);
						candidateBean.setRegistrationImage(noImageUrl);// (PG_NO_IMAGE_URL);
					}
					
					if (null != specialNeedStudentsList && specialNeedStudentsList.contains(candidateBean.getSapId())) {
						candidateBean.setCompensatoryTimeFlag(Boolean.TRUE.booleanValue());
						candidateBean.setCompensatoryTime(maxCompensatoryTime);

						message = "REGISTER CANDIDATE -(Date, Special Needs Student SapId) (" + registerForDate + ", "
								+ candidateBean.getSapId() + ")";
						logger.info(message);
					}

					retryAtMettl = Boolean.FALSE;
					retry = retryFinal;
					retryRemoteServer = retryFinalRemoteServer;
					do {
						arr = registerCandidateAtMettl(candidateBean);

						if (arr.length == 1) {
							if (null != arr[0].getStatus()
									&& arr[0].getStatus().equalsIgnoreCase(KEY_ERROR)) {
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
								this.saveRegistrationStatus(candidateBean, arr[0].getStatus(),
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
								this.saveRegistrationStatus(candidateBean, arr[0].getStatus(), message, arr[0].getUrl(),
										pgUrlMettl);
								rowsProcessed++;
								retryAtMettl = Boolean.FALSE;
							}
						}
					} while (retryAtMettl);

					if (configErrorMettl) {
						message = "ABRUPT STOP................................";
						
						logger.info(message);
						
						message = "REGISTER CANDIDATE -(Date, Total) (" + registerForDate + ", "
								+ totalCandidates + ") (S, F, R) (" + rowsProcessed + ", "
								+ rowsWithError + ", " + retriedRows + ") - " + d + " -STOP-";
						
						logger.info(message);
						
						message = "ABRUPT STOP................................";
						
						logger.info(message);
						break;
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
	
	private MettlRegisterCandidateBean[] registerCandidateAtMettl(
			final MettlRegisterCandidateBean candidateBean) {
		logger.info("registerCandidateAtMettl : (candidateBean)  (" + candidateBean
				+ ")");

		MettlRegisterCandidateBean[] arr = null;

		arr = mettlHelper.registerCandidate(candidateBean, candidateBean.getScheduleAccessKey(),
				candidateBean.getCompensatoryTime(), candidateBean.isCompensatoryTimeFlag());

		return arr;
	}
	
	
	private String replace(String arg) {
		String d = null;
		d = arg.replace("(", "");
		d = d.replace(")", "");
		d = d.replace("'", "");
		return d;
	}
	private void saveRegistrationStatus(MettlRegisterCandidateBean candidateBean, String status,
			String message) {
		logger.info("saveStatus : (candidateBean, status, message) (" + candidateBean + ", "
				+ status + ", " + message + ")");

		this.saveRegistrationStatus(candidateBean, status, message, null, null);
	}
	
	private void saveRegistrationStatus(MettlRegisterCandidateBean candidateBean, final String status,
			final String message, final String urlSuccess, final String urlGeneral) {
		logger.info("saveStatus : (candidateBean, status, message) (" + candidateBean + ", "
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
			
			if (KEY_ERROR.equalsIgnoreCase(status)) {
				
				logger.info("saveStatus : (sapId, scheduleAccessKey, status) (" + sapId + ", " + scheduleAccessKey
						+ ", " + REGISTRATION_STATUS_FAIL + ")");
				isSaved = mettlDAO.saveCandidateRegisteredMettlInfo(sapId, scheduleAccessKey,
						REGISTRATION_STATUS_FAIL, message, null);
				
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
								+ examEndTime + ", " + REGISTRATION_STATUS_PASS + ")");
				logger.info(
						"saveStatus : (trackId, testTaken, firstName, lastName, examStartDateTime, examEndDateTime, accessStartDateTime, accessEndDateTime, sifySubjectCode, scheduleName, urlSuccess, urlGeneral) ("
								+ trackId + ", " + testTaken + ", " + firstName + ", " + lastName + ", "
								+ examStartDateTime + ", " + examEndDateTime + ", " + accessStartDateTime + ", "
								+ accessEndDateTime + ", " + sifySubjectCode + ", " + scheduleName + ", " + urlSuccess
								+ ", " + urlGeneral + ")");

				isSaved = mettlDAO.saveCandidateRegisteredMettlInfo(sapId, scheduleAccessKey,
						REGISTRATION_STATUS_PASS, message, urlSuccess);

				isUpdated = mettlDAO.updateExamBooking(sapId, emailAddress, subject, booked, year, month, examDate, examTime,
						examEndTime);

				isSaved1 = mettlDAO.saveScheduleInfoWaitingRoom(sapId, emailAddress, subject, year, month, trackId, testTaken, firstName,
						lastName, examStartDateTime, examEndDateTime, accessStartDateTime, accessEndDateTime,reportingStartDateTime,
						reportingFinishDateTime,sifySubjectCode, scheduleName, scheduleAccessKey, urlGeneral, examCenterName);
				
				if(isSaved && isUpdated && isSaved1) {
					mettlDAO.end_Transaction(Boolean.TRUE);
					logger.info("Candidate successfully registered and saved in db:(sapId, scheduleAccessKey, emailAddress, subject, booked, year, month, examDate, examTime, examEndTime, status) ("
							+ sapId + ", " + scheduleAccessKey + ", " + emailAddress + ", " + subject + ", "
							+ booked + ", " + year + ", " + month + ", " + examDate + ", " + examTime + ", "
							+ examEndTime + ")");
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
			reportingStartDateTime= null;
			reportingFinishDateTime= null;
			sifySubjectCode = null;
			scheduleName = null;
		}
	}

}
