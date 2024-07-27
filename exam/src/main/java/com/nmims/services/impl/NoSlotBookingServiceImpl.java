package com.nmims.services.impl;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.fasterxml.jackson.core.type.TypeReference;
import com.nmims.assembler.ObjectConverter;
import com.nmims.beans.MBAExamBookingRequest;
import com.nmims.beans.MBALiveSettings;
import com.nmims.beans.MBAPaymentRequest;
import com.nmims.beans.NoSlotBookingBean;
import com.nmims.beans.StudentExamBean;
import com.nmims.beans.StudentSubjectConfigExamBean;
import com.nmims.daos.DashboardDAO;
import com.nmims.daos.MBAWXLiveSettingsDAO;
import com.nmims.daos.MBAWXPaymentsDao;
import com.nmims.daos.NoSlotBookingDAOInterface;
import com.nmims.daos.StudentDAO;
import com.nmims.daos.StudentTestDAO;
import com.nmims.helpers.MBAPaymentHelper;
import com.nmims.helpers.MailSender;
import com.nmims.services.NoSlotBookingServiceInterface;

@Service
public class NoSlotBookingServiceImpl implements NoSlotBookingServiceInterface{
	@Autowired
	private DashboardDAO dashboardDAO;
	
	@Autowired
	private MBAWXLiveSettingsDAO mbawxLiveSettingsDAO;
	
	@Autowired
	private MBAWXPaymentsDao mbaWxPaymentsDao;
	
	@Autowired
	private NoSlotBookingDAOInterface noSlotBookingDAO;
	
	@Autowired
	private StudentDAO studentDAO;
	
	@Autowired
	private StudentTestDAO studentTestDAO;
	
	@Autowired
	private MBAPaymentHelper paymentHelper;
	
	@Autowired
	private MailSender mailSender;
	
	private static final String SUBJECT_PROJECT_DMS = "Digital Marketing Strategy";
	private static final int CPS_PROJECT_PDDM_JUL2021_RETAIL = 148;
	
	private static final String TIMEBOUND_USER_MAPPING_ROLE_RESIT = "Resit";
	
	private static final String LIVE_SETTINGS_TYPE_PROJECT_REGISTRATION = "Project Registration";
	private static final String LIVE_SETTINGS_TYPE_PROJECT_RE_REGISTRATION = "Project Re-Registration";
	private static final List<String> noSlotBookingTypes = new ArrayList<>(Arrays.asList(LIVE_SETTINGS_TYPE_PROJECT_REGISTRATION, 
																						LIVE_SETTINGS_TYPE_PROJECT_RE_REGISTRATION));
	
	private static final int DIGITAL_MARKETING_STRATEGY_MAX_SCORE_TOTAL = 100;
	private static final double DIGITAL_MARKETING_STRATEGY_PASS_SCORE = 50.00;
	
	private static final String NOSLOT_BOOKINGS_STATUS_TRUE = "Y";
	private static final String NOSLOT_BOOKINGS_STATUS_FALSE = "N";
	
	private static final String PAYMENT_SOURCE_WEBAPP = "WebApp";
	private static final String PAYMENT_SOURCE_MOBILE = "Mobile";
	
	private static final String TRAN_STATUS_INITIATED = "Initiated";
	private static final String TRAN_STATUS_PAYMENT_SUCCESSFUL = "Payment Successful";
	private static final String TRAN_STATUS_MANUALLY_APPROVED = "Online Payment Manually Approved";
	private static final String TRAN_STATUS_EXPIRED = "Expired";
	
	private static final String MODIFIED_BY_AUTO_BOOKING_SCHEDULER = "Auto Booking Scheduler";
	
	@Value("${PDDM_PROJECT_REGISTRATION_CHARGES}")
	private String PDDM_PROJECT_REGISTRATION_CHARGES;
	
	@Value("${PDDM_PROJECT_RE_REGISTRATION_CHARGES}")
	private String PDDM_PROJECT_RE_REGISTRATION_CHARGES;
	
	private static final Logger logger = LoggerFactory.getLogger(NoSlotBookingServiceImpl.class);
	
	@Override
	public Map<String, Object> studentProjectRegistrationEligibility(final Long timeboundId, final String sapid) {
		StudentSubjectConfigExamBean studentTimebound = checkStudentTimeboundProjectReg(timeboundId);						//Getting the student timebound details
		final boolean isRoleResit = userTimeboundRoleResit(sapid, timeboundId);												//Checking timebound role
		final Integer consumerProgramStructureId = pddmProjectCpsIdByStudentRole(sapid, isRoleResit, studentTimebound.getAcadYear(), studentTimebound.getAcadMonth());
		
		//Getting the details of the active project registration or re-registration
		MBALiveSettings projectRegLiveSettings = projectRegDetailsByTimeboundRole(consumerProgramStructureId, timeboundId, sapid, studentTimebound, isRoleResit);
		
		//Check if the student has already paid for the active registration
		boolean isPaid = checkSuccessfulNoSlotBooking(sapid, timeboundId, projectRegLiveSettings.getType(), NOSLOT_BOOKINGS_STATUS_TRUE);
		
		Map<String, Object> responseMap = new HashMap<>();
		responseMap.put("isEligible", isTypeProjectRegOrReReg(projectRegLiveSettings.getType()));
		responseMap.put("isPaid", isPaid);
		responseMap.put("acadMonth", studentTimebound.getAcadMonth());
		responseMap.put("acadYear", studentTimebound.getAcadYear());
		responseMap.put("startTime", projectRegLiveSettings.getStartTime());
		responseMap.put("endTime", projectRegLiveSettings.getEndTime());
		responseMap.put("subject", SUBJECT_PROJECT_DMS);
		responseMap.put("type", projectRegLiveSettings.getType());
		return responseMap;
	}
	
	@Override
	public Map<String, Object> projectRegistrationDetails(final Long timeboundId, final String sapid) {
		final boolean isRoleResit = userTimeboundRoleResit(sapid, timeboundId);											//Checking timebound role
		if(isRoleResit)
			throw new IllegalArgumentException("Project Registration not eligible for Resit student: " + sapid);
		
		StudentSubjectConfigExamBean studentTimebound = checkStudentTimeboundProjectReg(timeboundId);					//Getting the student timebound details
		final Integer consumerProgramStructureId = pddmProjectCpsIdByStudentRole(sapid, isRoleResit, studentTimebound.getAcadYear(), studentTimebound.getAcadMonth());
		
		//Getting details of active Project Registration
		MBALiveSettings projectRegLiveSettings = projectRegLiveSettings(consumerProgramStructureId, studentTimebound.getAcadMonth(), studentTimebound.getAcadYear(), 
																		studentTimebound.getExamMonth(), studentTimebound.getExamYear());
		
		boolean isLive = LIVE_SETTINGS_TYPE_PROJECT_REGISTRATION.equals(projectRegLiveSettings.getType());				//Checking if Project Registration is Live
		if(isLive) {
			//Check if the student has already paid for the active Project Registration
			boolean isBooked = checkBookingStatus(timeboundId, sapid, LIVE_SETTINGS_TYPE_PROJECT_REGISTRATION);
			
			return noSlotBookingDetailsMap(isLive, isBooked, studentTimebound.getAcadMonth(), studentTimebound.getAcadYear(), 
											projectRegLiveSettings.getStartTime(), projectRegLiveSettings.getEndTime(), 
											SUBJECT_PROJECT_DMS, LIVE_SETTINGS_TYPE_PROJECT_REGISTRATION, PDDM_PROJECT_REGISTRATION_CHARGES);
		}
		
		throw new IllegalArgumentException("Project Registration not live for student " + sapid);
	}
	
	@Override
	public Map<String, Object> projectReRegistrationDetails(final Long timeboundId, final String sapid) {
		StudentSubjectConfigExamBean studentTimebound = checkStudentTimeboundProjectReg(timeboundId);					//Getting the student timebound details
		final boolean isRoleResit = userTimeboundRoleResit(sapid, timeboundId);											//Checking timebound role
		final Integer consumerProgramStructureId = pddmProjectCpsIdByStudentRole(sapid, isRoleResit, studentTimebound.getAcadYear(), studentTimebound.getAcadMonth());
		
		//Getting details of active Project Re-Registration
		MBALiveSettings projectReRegLiveSettings = projectReRegLiveSettings(consumerProgramStructureId, timeboundId, sapid, studentTimebound.getAcadMonth(), studentTimebound.getAcadYear(), 
																			studentTimebound.getExamMonth(), studentTimebound.getExamYear());
		
		boolean isLive = LIVE_SETTINGS_TYPE_PROJECT_RE_REGISTRATION.equals(projectReRegLiveSettings.getType());			//Checking if Project Re-Registration is Live
		if(isLive) {
			//Check if the student has already paid for the active Project Re-Registration
			boolean isBooked = checkBookingStatus(timeboundId, sapid, LIVE_SETTINGS_TYPE_PROJECT_RE_REGISTRATION);
			
			return noSlotBookingDetailsMap(isLive, isBooked, studentTimebound.getAcadMonth(), studentTimebound.getAcadYear(), 
											projectReRegLiveSettings.getStartTime(), projectReRegLiveSettings.getEndTime(), SUBJECT_PROJECT_DMS, 
											LIVE_SETTINGS_TYPE_PROJECT_RE_REGISTRATION, PDDM_PROJECT_RE_REGISTRATION_CHARGES);
		}
		
		throw new IllegalArgumentException("Project Re-Registration not live for student " + sapid);
	}
	
	@Override
	public String saveNoSlotBooking(final String sapid, final Long timeboundId, final String type, final String amount, 
									final String paymentOption, final String source) {
		validateNoSlotBookingAmount(type, amount);
		
		String paymentSource = PAYMENT_SOURCE_WEBAPP.equals(source) ? source : PAYMENT_SOURCE_MOBILE;
		String paymentDescription = type + " for student " + sapid;
		String trackId = sapid + System.currentTimeMillis();
		
		noSlotBookingPaymentRecord(sapid, timeboundId, type, paymentOption, trackId, amount, paymentDescription, paymentSource);
		return trackId;
	}
	
	@Override
	public MBAExamBookingRequest noSlotBookingPaymentDetails(final String sapid, final String timeboundId, final String type, 
															final String trackId, final boolean isWeb) {
		List<MBAPaymentRequest> paymentRequestList = getInitiatedPaymentRecords(sapid, trackId);		//Payment records with Initiated transaction status
		
		MBAExamBookingRequest bookingRequest = createBookingRequest(sapid, timeboundId, type, isWeb, paymentRequestList.get(0));	//creates a booking request
		return paymentCheckSumDetails(bookingRequest);													//Generates checkSum and provides callback URL
	}
	
	@Override
	@Transactional
	public void noSlotBookingCallbackTransactions(HttpServletRequest request, MBAExamBookingRequest paymentRequest) {
		paymentHelper.createResponseBean(request, paymentRequest);
		String errorMessage = paymentHelper.checkErrorInPayment(request, paymentRequest);
		
		if(Objects.nonNull(errorMessage))
			throw new IllegalArgumentException(errorMessage);
		
		processSuccessfulTransaction(paymentRequest);
	}
	
	@Override
	public List<Map<String, String>> noSlotBookingStatus(final String sapid, final Long timeboundId, final String trackId) {
		List<MBAPaymentRequest> paymentRecords = mbaWxPaymentsDao.getPaymentDetailsBySapidTrackId(sapid, trackId);
		if(paymentRecords.isEmpty())
			throw new IllegalArgumentException("No payment records found for sapid: " + sapid + " and trackId: " + trackId);
		
		return paymentRecords.stream()
							 .map(paymentRecord -> getNoSlotBookingDetails(sapid, timeboundId, paymentRecord.getPaymentType(), paymentRecord.getId(), paymentRecord.getTranStatus()))
							 .collect(Collectors.toList());								//The booking details are inserted in a Map and stored in a List
	}
	
	@Override
	@Transactional
	public void processNoSlotBookingPaymentStatus() {
		//Get Initiated transactions prior to 10 minutes from the current DateTime
		List<MBAPaymentRequest> noSlotBookingTransactions = mbaWxPaymentsDao.getAllTransactionsByTypeStatus(noSlotBookingTypes, TRAN_STATUS_INITIATED);
		
		for(MBAPaymentRequest transaction: noSlotBookingTransactions) {
			if(isPaymentSuccessful(transaction)) {				//check if the transaction is successful
				if(trackIdConflictTransactions(transaction.getTrackId())) {
					processNoSlotBookingTransaction(transaction, transaction.getSapid(), TRAN_STATUS_MANUALLY_APPROVED, transaction.getId(),
													NOSLOT_BOOKINGS_STATUS_FALSE, MODIFIED_BY_AUTO_BOOKING_SCHEDULER);
					continue;
				}
				
				processNoSlotBookingTransaction(transaction, transaction.getSapid(), TRAN_STATUS_PAYMENT_SUCCESSFUL, transaction.getId(),
												NOSLOT_BOOKINGS_STATUS_TRUE, MODIFIED_BY_AUTO_BOOKING_SCHEDULER);
				
				sendTransactionSuccessMail(transaction.getSapid(), transaction.getPaymentType(), transaction.getTrackId(), transaction.getAmount());
			}
		}
	}
	
	@Override
	public void processNoSlotBookingExpiredPayments() {
		//Get Initiated transactions exceeding 180 minutes transaction dateTime from the current dateTime
		List<MBAPaymentRequest> noSlotBookingExpiredTransactions = mbaWxPaymentsDao.getExpiredTransactionsByTypeStatus(noSlotBookingTypes, TRAN_STATUS_INITIATED);
		
		noSlotBookingExpiredTransactions.stream()
										.forEach(transaction -> markNoSlotBookingPaymentExpired(transaction.getId(), TRAN_STATUS_EXPIRED, MODIFIED_BY_AUTO_BOOKING_SCHEDULER));
	}
	
	@Override
	public void sendNoSlotBookingTransExceptionMail(final Exception exception, final String trackId) {
		try {
			StringWriter stringWriter = new StringWriter();
			exception.printStackTrace(new PrintWriter(stringWriter));
			
			mailSender.transExceptionMail("Error saving NoSlot booking transaction with trackId: " + trackId, stringWriter.toString());
		}
		catch(Exception ex) {
			logger.error("Error while sending NoSlot booking transaction error stackTrace mail for trackId: {}, Exception thrown:", trackId, ex);
		}
	}
	
	/**
	 * Gets the timebound details using the timeboundId, and subject name is obtained using the pssId fetched from the timebound. 
	 * If the subject name does not match the Digital Marketing Strategy, an error is thrown with an error message.
	 * ConsumerProgramStructure ID of the student is fetched and the obtained details are returned.
	 * @param timeboundId - timeboundId of the student
	 * @return bean containing the timebound details
	 */
	private StudentSubjectConfigExamBean checkStudentTimeboundProjectReg(final Long timeboundId) {
		StudentSubjectConfigExamBean studentTimebound = noSlotBookingDAO.getTimeboundDetails(timeboundId);
		
		String subjectName = dashboardDAO.getSubjectNameByPSSId(studentTimebound.getPrgm_sem_subj_id());
		if(!SUBJECT_PROJECT_DMS.equals(subjectName))				//checks if the subject name matches Digital Marketing Strategy
			throw new IllegalArgumentException("Subject: " + subjectName + " does not have Project Registration!");
		
		return studentTimebound;
	}
	
	/**
	 * Checks if the role of user in timebound mapping is Resit.
	 * @param userId
	 * @param timeboundId
	 * @return timebound mapping count with role Resit
	 */
	private boolean userTimeboundRoleResit(final String userId, final Long timeboundId) {
		int timeboundMappingCount = noSlotBookingDAO.getTimeboundMappingCountByUserIdTimeboundIdRole(userId, timeboundId, TIMEBOUND_USER_MAPPING_ROLE_RESIT);
		return timeboundMappingCount > 0;
	}
	
	/**
	 * Retrieving the CPS ID of the student using the passed academic month and year.
	 * @param sapid - studentNo
	 * @param roleResit - boolean value indicating if the user role is Resit
	 * @param acadYear
	 * @param acadMonth
	 * @return consumerProgramStructure ID of the student
	 */
	private Integer pddmProjectCpsIdByStudentRole(final String sapid, final boolean roleResit, final String acadYear, final String acadMonth) {
		if(roleResit)									//For Resit Student, return the Project (DMS) consumerProgramStructure ID
			return CPS_PROJECT_PDDM_JUL2021_RETAIL;
		
		final Integer cpsId = noSlotBookingDAO.getCPSIdBySapidYearMonth(sapid, acadYear, acadMonth);
		if(CPS_PROJECT_PDDM_JUL2021_RETAIL != cpsId)
			throw new IllegalArgumentException("Student timeboundId: " + cpsId + " does not have Project Registration!");
		
		return cpsId;
	}
	
	/**
	 * Fetches Project Registration and Project Re-Registration details by timebound role of student.
	 * For student with timebound role Resit, only the Project Re-Registration details are fetched.
	 * @param consumerProgramStructureId
	 * @param timeboundId
	 * @param sapid - studentNo
	 * @param studentTimebound - student timebound details
	 * @param roleResit - boolean value indicating if the user timebound role is Resit
	 * @return Project Registration/Re-Registration details
	 */
	private MBALiveSettings projectRegDetailsByTimeboundRole(final Integer consumerProgramStructureId, final Long timeboundId, final String sapid, 
															final StudentSubjectConfigExamBean studentTimebound, final boolean roleResit) {
		if(roleResit)						//For students with timebound role Resit, fetch only Project Re-Registration details
			return projectReRegLiveSettings(consumerProgramStructureId, timeboundId, sapid, studentTimebound.getAcadMonth(), 
											studentTimebound.getAcadYear(), studentTimebound.getExamMonth(), studentTimebound.getExamYear());
		
		return projectRegOrReRegLive(consumerProgramStructureId, timeboundId, sapid, studentTimebound.getAcadMonth(), 
									studentTimebound.getAcadYear(), studentTimebound.getExamMonth(), studentTimebound.getExamYear());
	}
	
	/**
	 * Gets the Project Registration details if active,
	 * else checks if the student is eligible for Project Re-Registration and gets the details.
	 * @param consumerProgramStructureId
	 * @param timeboundId
	 * @param sapid - student No.
	 * @param acadMonth
	 * @param acadYear
	 * @param examMonth
	 * @param examYear
	 * @return Project Registration or Re-Registration details
	 */
	private MBALiveSettings projectRegOrReRegLive(final Integer consumerProgramStructureId, final Long timeboundId, final String sapid, 
												final String acadMonth, final String acadYear, final String examMonth, final String examYear) {
		try {
			return mbawxLiveSettingsDAO.liveSettingsTypeStartEndTime(acadYear, acadMonth, examYear, examMonth, consumerProgramStructureId, 
																	LIVE_SETTINGS_TYPE_PROJECT_REGISTRATION);
		}
		catch(DataAccessException ex) {						//DataAccess Exception thrown when no records found
			logger.error("Project Registration not active for student: {} with cpsId: {} and timeboundId: {}", 
						sapid, consumerProgramStructureId, timeboundId);
			return projectReRegLiveSettings(consumerProgramStructureId, timeboundId, sapid, acadMonth, acadYear, examMonth, examYear);
		}
	}
	
	/**
	 * Gets the active Project Registration details.
	 * @param consumerProgramStructureId
	 * @param acadMonth
	 * @param acadYear
	 * @param examMonth
	 * @param examYear
	 * @return Project Registration details
	 */
	private MBALiveSettings projectRegLiveSettings(final Integer consumerProgramStructureId, final String acadMonth, 
													final String acadYear, final String examMonth, final String examYear) {
		try {
			return mbawxLiveSettingsDAO.liveSettingsTypeStartEndTime(acadYear, acadMonth, examYear, examMonth, consumerProgramStructureId, 
																	LIVE_SETTINGS_TYPE_PROJECT_REGISTRATION);
		}
		catch(DataAccessException ex) {						//DataAccess Exception thrown when no records found
			logger.error("Project Registration not active for cpsId: {}", consumerProgramStructureId);
			return new MBALiveSettings();
		}
	}
	
	/**
	 * Checks if the student is eligible for Project Re-Registration and gets the details if active
	 * @param consumerProgramStructureId
	 * @param timeboundId
	 * @param sapid
	 * @param acadMonth
	 * @param acadYear
	 * @param examMonth
	 * @param examYear
	 * @return Project Re-Registration details
	 */
	private MBALiveSettings projectReRegLiveSettings(final Integer consumerProgramStructureId, final Long timeboundId, final String sapid, 
													final String acadMonth, final String acadYear, final String examMonth, final String examYear) {
		try {
			boolean isEligible = studentEligibleForProjectReExam(timeboundId, sapid);			//Checks if student is eligible for Project Re-exam
			if(isEligible)
				return mbawxLiveSettingsDAO.liveSettingsTypeStartEndTime(acadYear, acadMonth, examYear, examMonth, consumerProgramStructureId, 
																		LIVE_SETTINGS_TYPE_PROJECT_RE_REGISTRATION);
		}
		catch(DataAccessException ex) {						//DataAccess Exception thrown when no records found
			logger.error("Project Re-Registration not active for student: {} with cpsId: {} and timeboundId: {}", 
						sapid, consumerProgramStructureId, timeboundId);
		}
		
		return new MBALiveSettings();
	}
	
	/**
	 * Checks if the student is eligible for Project Re-exam by getting the regular tests conducted for the timeboundId, 
	 * checking if the max scores of the tests combined is 100 marks and if student has obtained below 50 marks (Pass Score) in the tests combined.
	 * @param timeboundId - timeboundId of the student
	 * @param sapid - student No.
	 * @return boolean value indicating if the student is eligible for Project Re-Registration
	 */
	private boolean studentEligibleForProjectReExam(final Long timeboundId, final String sapid) {
		Map<Integer, Integer> applicableTestScoreMap = getScheduledTestsForTimebound(timeboundId);
		logger.info("Applicable IAs: {} fetched from timeboundId: {}", applicableTestScoreMap, timeboundId);
		
		//IA max scores are rounded together and stored as total marks
		Optional<Integer> iaMaxScoreSum = applicableTestScoreMap.values()
																.stream()
																.reduce(Integer::sum);
		logger.info("Total marks of applicable IAs: {} for timeboundId: {}", iaMaxScoreSum, timeboundId);
		
		boolean regularTestsLive = iaMaxScoreSum.isPresent() && iaMaxScoreSum.get() == DIGITAL_MARKETING_STRATEGY_MAX_SCORE_TOTAL;
		if(regularTestsLive) {
			double studentMarks = studentTestDAO.getStudentTotalScoreByTestIds(applicableTestScoreMap.keySet(), sapid);
			return studentMarks < DIGITAL_MARKETING_STRATEGY_PASS_SCORE;
		}
		
		return regularTestsLive;
	}
	
	/**
	 * IAs Applicable for a particular timebound are fetched and returned.
	 * @param timeboundId - TimeboundId for which the tests are to be fetched
	 * @return Map of testIds and their respective max scores
	 */
	private Map<Integer, Integer> getScheduledTestsForTimebound(final Long timeboundId) {
		//List of sessionPlanId as timeboundId not primary key in sessionPlan_timebound_mapping table
		List<Integer> sessionPlanIdList = noSlotBookingDAO.getSessionPlanByTimebound(timeboundId);
		return studentTestDAO.getApplicableTestsMaxScore(sessionPlanIdList);
	}
	
	private boolean isTypeProjectRegOrReReg(final String type) {
		return LIVE_SETTINGS_TYPE_PROJECT_REGISTRATION.equals(type) || LIVE_SETTINGS_TYPE_PROJECT_RE_REGISTRATION.equals(type);
	}
	
	/**
	 * Checks if the student has paid for the active booking.
	 * @param timeboundId - timeboundId of the student
	 * @param sapid - student No.
	 * @param type - noSlot booking type
	 * @return boolean value indicating if the student has successfully booked for the active booking
	 */
	private boolean checkBookingStatus(final Long timeboundId, final String sapid, final String type) {
		try {
			List<NoSlotBookingBean> projectRegBookings = noSlotBookingDAO.getNoSlotBookingBySapidTimeboundType(timeboundId, sapid, type);
			return projectRegBookings.stream()
									 .anyMatch(booking -> NOSLOT_BOOKINGS_STATUS_TRUE.equals(booking.getStatus()));
		}
		catch(DataAccessException ex) {								//DataAccess Exception thrown when no records found
			logger.error("DataAccess Exception while fetching NoSlot bookings for sapid: {} with timeboundId: {} and type: {}", sapid, timeboundId, type);
			return false;
		}
	}
	
	/**
	 * Creating a Map with the required NoSlot booking details
	 * @param isLive - indicates if the booking type is live
	 * @param isBooked - indicates if booked successfully by the student
	 * @param acadMonth - academic month
	 * @param acadYear - academic year
	 * @param startTime - start time of the noSlot booking
	 * @param endTime - end time of the noSlot booking
	 * @param subject - subject name
	 * @param type - noSlot booking type
	 * @param charges - amount charged
	 * @return Map containing booking details
	 */
	private Map<String, Object> noSlotBookingDetailsMap(final boolean isLive, final boolean isBooked, final String acadMonth, final String acadYear,
														final Date startTime, final Date endTime, final String subject, final String type, final String charges) {
		Map<String, Object> responseMap = new HashMap<>();
		responseMap.put("live", isLive);
		responseMap.put("booked", isBooked);
		responseMap.put("acadMonth", acadMonth);
		responseMap.put("acadYear", acadYear);
		responseMap.put("startDateTime", startTime);
		responseMap.put("endDateTime", endTime);
		responseMap.put("subject", subject);
		responseMap.put("type", type);
		responseMap.put("charges", charges);
		return responseMap;
	}
	
	/**
	 * Checks if the booking type is valid for the provided booking type.
	 * @param type - noSlot booking type
	 * @param amount - booking amount
	 */
	private void validateNoSlotBookingAmount(final String type, final String amount) {
		boolean validTypeAmount = checkTypeAmount(type, amount);			//Checks if the booking amount is valid
		if(!validTypeAmount)
			throw new IllegalArgumentException("Provided amount improper!");
	}
	
	/**
	 * Checks the amount against the booking type.
	 * @param type - noSlot booking type
	 * @param amount - booking amount
	 * @return boolean value indicating if the amount is valid
	 */
	private boolean checkTypeAmount(final String type, final String amount) {
		switch(type) {
			case LIVE_SETTINGS_TYPE_PROJECT_REGISTRATION:
				return PDDM_PROJECT_REGISTRATION_CHARGES.equals(amount);
			case LIVE_SETTINGS_TYPE_PROJECT_RE_REGISTRATION:
				return PDDM_PROJECT_RE_REGISTRATION_CHARGES.equals(amount);
			default:
				return false;
		}
	}
	
	/**
	 * Payment record is inserted with the transaction status as Initiated and payment option as selected by the user.
	 * A noSlot booking record is also inserted with status as false.
	 * @param sapid - student No.
	 * @param timeboundId - timebound of the student
	 * @param type - noSlot booking type
	 * @param paymentOption - payment option selected by the user
	 * @param trackId - tracking ID
	 * @param amount - booking amount charged
	 * @param description - payment description
	 * @param source - device used for payment
	 */
	private void noSlotBookingPaymentRecord(final String sapid, final Long timeboundId, final String type, final String paymentOption,
											final String trackId, final String amount, final String description, final String source) {
		//Inserting Payment record with transaction status as Initiated
		long paymentRecordId = mbaWxPaymentsDao.insertPaymentRecord(type, sapid, paymentOption, trackId, amount, TRAN_STATUS_INITIATED, description, source);
		logger.info("Payment record with ID: {} and tranStatus: {} inserted successfully for student: {} with trackId: {}", 
					paymentRecordId, TRAN_STATUS_INITIATED, sapid, trackId);
		
		//Inserting NoSlot booking record with status as false 
		int noOfRowsUpdated = noSlotBookingDAO.insertNoSlotBooking(sapid, timeboundId, type, paymentRecordId, NOSLOT_BOOKINGS_STATUS_FALSE);
		logger.info("No of records inserted in noslot_bookings table: {} for sapid: {}, timeboundId: {} and type: {}", 
					noOfRowsUpdated, sapid, timeboundId, type);
	}
	
	/**
	 * List of payment records with transaction status as Initiated.
	 * @param sapid - student No.
	 * @param trackId - tracking ID
	 * @return payment record List
	 */
	private List<MBAPaymentRequest> getInitiatedPaymentRecords(final String sapid, final String trackId) {
		try {
			return mbaWxPaymentsDao.getPaymentRecordsBySapidTrackIdTranStatus(sapid, trackId, TRAN_STATUS_INITIATED);
		}
		catch(DataAccessException ex) {								//DataAccess Exception thrown when no records found
			logger.error("No Payment records found for sapid: {} and trackId: {} with transaction status: {}", sapid, trackId, TRAN_STATUS_INITIATED);
			throw new IllegalArgumentException("Payment attempt already made");
		}
	}
	
	/**
	 * Creating a booking request using the provided payment record details.
	 * @param sapid - student No.
	 * @param timeboundId - timeboundId of the student
	 * @param type - noSlot booking type
	 * @param isWeb - device used for payment
	 * @param paymentRequest - payment record details
	 * @return booking request details
	 */
	private MBAExamBookingRequest createBookingRequest(final String sapid, final String timeboundId, final String type,
														final boolean isWeb, final MBAPaymentRequest paymentRequest) {
		MBAExamBookingRequest bookingRequest = ObjectConverter.convertObjToXXX(paymentRequest, new TypeReference<MBAExamBookingRequest>() {});
		bookingRequest.setSapid(sapid);
		bookingRequest.setTimeboundId(timeboundId);
		bookingRequest.setPaymentType(type);
		bookingRequest.setWeb(isWeb);
		
		return bookingRequest;
	}
	
	/**
	 * Generates CheckSum and callback URL required for payment.
	 * @param paymentRequest - noSlot booking payment details bean
	 * @return booking request containing the required payment details
	 */
	private MBAExamBookingRequest paymentCheckSumDetails(MBAExamBookingRequest paymentRequest) {
		StudentExamBean student = studentDAO.getStudentInfo(paymentRequest.getSapid());			//Student details
		
		//Generates checkSum, provide callback URL and other necessary details for payment
		String checkSumResponse = paymentHelper.generateCommonCheckSum(paymentRequest, student, paymentRequest.getPaymentType());
		if(!checkSumResponse.equalsIgnoreCase("true"))								//throws an error if the response is not true
			throw new IllegalArgumentException(checkSumResponse);

		return paymentRequest;
	}
	
	/**
	 * The payment records for the specified student and trackId is marked as Payment Successful.
	 * The booking status for these payment records are marked as true.
	 * @param bookingRequest - bean containing the booking payment details
	 */
	private void processSuccessfulTransaction(final MBAExamBookingRequest bookingRequest) {
		int noOfRecordsUpdated = mbaWxPaymentsDao.updatePaymentRecords(bookingRequest, TRAN_STATUS_PAYMENT_SUCCESSFUL, 
																	bookingRequest.getSapid(), bookingRequest.getSapid());
		logger.info("{} payment records updated with tranStatus: {} for sapid: {} and trackId: {}", noOfRecordsUpdated, TRAN_STATUS_PAYMENT_SUCCESSFUL, 
					bookingRequest.getSapid(), bookingRequest.getTrackId());
		
		List<MBAPaymentRequest> paymentRecordList = mbaWxPaymentsDao.getPaymentDetailsBySapidTrackId(bookingRequest.getSapid(), bookingRequest.getTrackId());
		paymentRecordList.stream()
						 .filter(payment -> TRAN_STATUS_PAYMENT_SUCCESSFUL.equals(payment.getTranStatus()))
						 .forEach(payment -> processNoSlotBookingStatus(bookingRequest.getSapid(), bookingRequest.getTimeboundId(), 
								 										payment.getPaymentType(), payment.getId(), NOSLOT_BOOKINGS_STATUS_TRUE, 
								 										bookingRequest.getTrackId(), payment.getAmount()));
	}
	
	/**
	 * The booking status is marked as true for the booking record,
	 * and a successful transaction mail is sent to the student on successful transaction.
	 * @param sapid - student No.
	 * @param timeboundId - timeboundId of the student
	 * @param type - noSlot booking type
	 * @param paymentRecordId - ID of the payment record
	 * @param status - booking status
	 * @param trackId - tracking ID
	 * @param amount - booking amount
	 */
	private void processNoSlotBookingStatus(final String sapid, final String timeboundId, final String type, final Long paymentRecordId, 
											final String status, final String trackId, final String amount) {
		try {
			int noOfRecordsUpdated = noSlotBookingDAO.updateNoSlotBookingStatus(sapid, timeboundId, type, paymentRecordId, status);
			logger.info("NoSlot Booking status updated for {} records with sapid: {}, timeboundId: {}, type: {} and paymentRecordId: {}", 
						noOfRecordsUpdated, sapid, timeboundId, type, paymentRecordId);
			
			sendTransactionSuccessMail(sapid, type, trackId, amount);
		}
		catch(Exception ex) {
			logger.error("Error while updating the noSlot booking status as {} and sending transaction successful mail to student: {} " + 
						"with paymentRecordId: {} and trackId: {}, Exception thrown:", status, sapid, paymentRecordId, trackId, ex);
		}
	}
	
	/**
	 * Status of the NoSlot booking is fetched and the booking details are returned
	 * @param sapid - student No.
	 * @param timeboundId - timeboundId of the student
	 * @param type - noSlot booking type
	 * @param paymentRecordId - ID of the payment record
	 * @param paymentStatus - status of payment
	 * @return Map containing the noSlot booking details
	 */
	private Map<String, String> getNoSlotBookingDetails(final String sapid, final Long timeboundId, final String type, 
														final Long paymentRecordId, final String paymentStatus) {
		String bookingStatus = noSlotBookingDAO.getNoSlotBookingStatus(sapid, timeboundId, type, paymentRecordId);
		
		Map<String, String> bookingDetailsMap = new HashMap<>();
		bookingDetailsMap.put("sapid", sapid);
		bookingDetailsMap.put("type", type);
		bookingDetailsMap.put("paymentStatus", paymentStatus);
		bookingDetailsMap.put("bookingStatus", bookingStatus);
		return bookingDetailsMap;
	}
	
	/**
	 * Checks if the transaction is successful.
	 * @param paymentRequest - bean containing payment record
	 * @return boolean value indicating if the transaction was successful
	 */
	private boolean isPaymentSuccessful(MBAPaymentRequest paymentRequest) {
		String errorMessage = paymentHelper.checkTransactionStatus(paymentRequest);
		boolean isError = StringUtils.isBlank(errorMessage);
		if(isError)
			return paymentRequest.isSuccessFromGateway();
		
		logger.error(errorMessage);
		return isError;
	}
	
	/**
	 * Checks if the transaction has a conflict for the same noSlot booking.
	 * @param trackId - tracking ID
	 * @return boolean value indicating if the transaction has a conflict
	 */
	private boolean trackIdConflictTransactions(final String trackId) {
		List<NoSlotBookingBean> bookingRecordList = noSlotBookingDAO.noSlotBookingsByTrackId(trackId);
		
		long conflictTransactions = bookingRecordList.stream()
													 .filter(booking -> checkSuccessfulNoSlotBooking(booking.getSapid(), booking.getTimeboundId(), 
																									booking.getType(), NOSLOT_BOOKINGS_STATUS_TRUE))
													 .map(booking -> addConflictTransaction(trackId, booking.getId(), MODIFIED_BY_AUTO_BOOKING_SCHEDULER))
													 .count();
		
		logger.info("No of conflict transactions inserted: {} for trackId: {}", conflictTransactions, trackId);
		return conflictTransactions > 0L;
	}
	
	/**
	 * Checks if the booking is successful.
	 * @param sapid - student No.
	 * @param timeboundId - timeboundId of the student
	 * @param type - noSlot booking type
	 * @param bookingStatus - status of the booking
	 * @return boolean value indicating if the booking is successful
	 */
	private boolean checkSuccessfulNoSlotBooking(final String sapid, final Long timeboundId, final String type, final String bookingStatus) {
		int noOfBookings = noSlotBookingDAO.checkNoSlotBookingStatus(sapid, timeboundId, type, bookingStatus);
		logger.info("No of successful bookings: {} for sapid: {} with timeboundId: {} and type: {}", noOfBookings, sapid, timeboundId, type);
		
		return noOfBookings > 0;
	}
	
	/**
	 * Inserts the transaction conflict record.
	 * @param trackId - tracking ID
	 * @param bookingId - noSlot booking ID
	 * @param modifiedByUser - userId
	 * @return count of rows inserted
	 */
	private int addConflictTransaction(final String trackId, final long bookingId, final String modifiedByUser) {
		try {
			return noSlotBookingDAO.insertNoSlotBookingConflictTransaction(trackId, bookingId, modifiedByUser);
		}
		catch(Exception ex) {
			logger.error("Error while inserting NoSlot booking transaction conflict record for trackId: {} and bookingId: {}, Exception thrown:", 
						trackId, bookingId, ex);
			return 0;
		}
	}
	
	/**
	 * Update payment record for noSlot booking transactions
	 * @param payemntRecordId - ID of payment record
	 * @param tranStatus - transaction status
	 * @param modifiedByUser - userId
	 */
	private void markNoSlotBookingPaymentExpired(final long paymentRecordId, final String tranStatus, final String modifiedByUser) {
		try {
			int countOfRecordsUpdated = mbaWxPaymentsDao.updatePaymentRecord(paymentRecordId, tranStatus, modifiedByUser);
			logger.info("{} records of noSlot booking payment of paymentRecord ID: {} with transaction status: {} updated successfully.", 
						countOfRecordsUpdated, paymentRecordId, tranStatus);
		}
		catch(Exception ex) {
			logger.error("Error while updating noSlot booking payment record of ID: {} with tranStatus: {}, Exception thrown:", paymentRecordId, tranStatus, ex);
		}
	}
	
	/**
	 * Updating the payment and booking records for noSlot booking transaction.
	 * @param bookingRequest - bean containing noSlot booking details
	 * @param sapid - student No.
	 * @param tranStatus - transaction status
	 * @param paymentRecordId - ID of payment record
	 * @param bookingStatus - booking status
	 * @param modifiedByUser - userId
	 */
	private void processNoSlotBookingTransaction(final MBAPaymentRequest bookingRequest, final String sapid, final String tranStatus, 
												final long paymentRecordId, final String bookingStatus, final String modifiedByUser) {
		try {
			int noOfRecordsUpdated = mbaWxPaymentsDao.updatePaymentRecords(bookingRequest, tranStatus, sapid, modifiedByUser);
			logger.info("Payment record of {} records updated with transaction status: {} for trackId: {}", noOfRecordsUpdated, tranStatus, bookingRequest.getTrackId());
				
			int recordsUpdated = noSlotBookingDAO.updateNoSlotBookingStatusBySapidPaymentId(bookingStatus, modifiedByUser, sapid, paymentRecordId);
			logger.info("{} noSlot booking records updated with status: {} for student: {} with paymentRecordId: {}", 
						recordsUpdated, bookingStatus, sapid, paymentRecordId);
		}
		catch(Exception ex) {
			logger.error("Error while updating the payment and booking records for noSlot booking transaction for sapid: {} with paymentRecordId: {}, Exception thrown:", 
						sapid, paymentRecordId, ex);
		}
	}
	
	/**
	 * Send transaction mail to the student on successful transaction.
	 * @param sapid - student No.
	 * @param type - noSlot booking type
	 * @param trackId - tracking ID
	 * @param amount - booking amount
	 */
	private void sendTransactionSuccessMail(final String sapid, final String type, final String trackId, final String amount) {
		try {
			Map<String, Object> studentNameEmailId = studentDAO.getStudentNameEmailId(sapid);			//Map containing student name and email address
			String studentName = String.valueOf(studentNameEmailId.get("name"));
			String studentEmailId = String.valueOf(studentNameEmailId.get("emailId"));
			
			mailSender.sendNoSlotBookingTransactionMail(sapid, studentName, studentEmailId, type, trackId, amount, TRAN_STATUS_PAYMENT_SUCCESSFUL);
			logger.info("Successful transaction mail sent successfully to the student: {} with trackId: {} and type: {}", sapid, trackId, type);
		}
		catch(Exception ex) {
			logger.error("Error while fetching student details and sending NoSlot Booking transaction success mail to student: {} with trackId: {} and type: {}", sapid, trackId, type);
		}
	}
}
