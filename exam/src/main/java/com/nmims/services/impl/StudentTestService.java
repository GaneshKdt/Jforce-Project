package com.nmims.services.impl;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Service;

import com.ibm.icu.text.SimpleDateFormat;
import com.nmims.beans.ProgramSubjectMappingExamBean;
import com.nmims.beans.StudentsTestDetailsExamBean;
import com.nmims.beans.TestExamBean;
import com.nmims.daos.NoSlotBookingDAOInterface;
import com.nmims.daos.StudentTestDAO;
import com.nmims.daos.TestDAO;
import com.nmims.daos.TestDAOForRedis;
import com.nmims.services.StudentTestServiceInterface;

@Service
public class StudentTestService implements StudentTestServiceInterface {

	@Autowired
	StudentTestDAO studentTestDao;

	@Autowired
	TestDAO testDao;
	
	@Autowired
	TestDAOForRedis daoForRedis;
	
	@Autowired
	private NoSlotBookingDAOInterface noSlotBookingDAO;

	@Value("${CURRENT_MBAWX_ACAD_YEAR}")
	String current_mbawx_acad_year;

	@Value("${CURRENT_MBAWX_ACAD_MONTH}")
	String current_mbawx_acad_month;

	private static final String COPY_CASE_REMARK = "Marked For Copy Case.";
	
	private static final String CPS_PROJECT_PDDM_JUL2021_RETAIL = "148";
	private static final String SUBJECT_DIGITAL_MARKETING_STRATEGY = "Digital Marketing Strategy";
	private static final String TEST_TYPE_PROJECT = "Project";
	
	private static final int RE_EXAM_DMS_MAX_SCORE = 100;
	
	private static final String BOOKING_TYPE_PROJECT_REGISTRATION = "Project Registration";
	private static final String BOOKING_TYPE_PROJECT_RE_REGISTRATION = "Project Re-Registration";
	
	private static final String NOSLOT_BOOKING_STATUS_BOOKED = "Y";
	
	private static final String TIMEBOUND_USER_MAPPING_ROLE_RESIT = "Resit";
	
	private static final Logger logger = LoggerFactory.getLogger(StudentTestService.class);
	
	@Override
	public List<TestExamBean> getTestDataForTODO(Long timeboundid, String sapid) throws Exception{
		
		List<TestExamBean> tests = new ArrayList<>();
		int count =studentTestDao.checkRole(timeboundid.toString(),sapid);
		if (count > 0)
		{
			String sessionPlanId = studentTestDao.getSessionPlan( timeboundid.toString());
		
		List<String> sessionPlanModules = studentTestDao.getSessionPlanModuleList( sessionPlanId );
		List<String> referenceId = studentTestDao.getTestLiveSettings( sessionPlanModules );
		List<String> testids = studentTestDao.getTestConfigurations( referenceId );
		
		List<TestExamBean> pendingTest = studentTestDao.getPendingTest( testids );
		List<TestExamBean> ongoingTest = studentTestDao.getOngoingTest( testids );
		List<TestExamBean> extendedTest = studentTestDao.getExtendedTest( testids, sapid );

		tests.addAll(pendingTest);
		tests.addAll(ongoingTest);
		tests.addAll(extendedTest);
		
		List<TestExamBean> studentTestDetails = studentTestDao.getTestDetails( sapid );

		final Map<Long,TestExamBean> studentDetailsMap = studentTestDetails.stream().collect(Collectors.toMap(TestExamBean::getTestId,
                Function.identity()));
		
		tests.stream()
			.filter(test -> studentDetailsMap.containsKey(test.getId()))
			.forEach(test -> {
				test.setAttempt( studentDetailsMap.get(test.getId()).getAttempt() );
				test.setTestStartedOn( studentDetailsMap.get(test.getId()).getTestStartedOn() );
				test.setRemainingTime( studentDetailsMap.get(test.getId()).getRemainingTime() );
				test.setTestEndedOn( studentDetailsMap.get(test.getId()).getTestEndedOn() );
				test.setTestCompleted( studentDetailsMap.get(test.getId()).getTestCompleted() );
				test.setScore( studentDetailsMap.get(test.getId()).getScore() );
				test.setScoreInInteger( studentDetailsMap.get(test.getId()).getScoreInInteger()) ;
				test.setNoOfQuestionsAttempted( studentDetailsMap.get(test.getId()).getNoOfQuestionsAttempted() );
				test.setCurrentQuestion( studentDetailsMap.get(test.getId()).getCurrentQuestion() );
			});
		
		tests.stream()
			.filter(test -> !studentDetailsMap.containsKey(test.getId()))
			.forEach(test -> {
				test.setAttempt( 0 );
				test.setRemainingTime( 0 );
				test.setScore( 0.0 );
				test.setTestCompleted( null );
			});

		tests.removeIf(test -> ("Y".equals(test.getTestCompleted()))
								|| (	checkIfTestPddmProject(test.getSubject(), test.getTestType()) 
										&& !checkProjectRegBookingStatus(sapid, timeboundid, test.getMaxScore())	));
		}
		else if(checkResitStudentForProjectDMS(timeboundid, sapid)) {
			//Fetching reExam Project tests for Resit Students (PDDM Q4 - Digital Marketing Strategy)
			tests = projectReExamForResit(timeboundid, sapid);
			
			tests.removeIf(test -> checkIfTestPddmProject(test.getSubject(), test.getTestType()) 
								&& !checkProjectRegBookingStatus(sapid, timeboundid, test.getMaxScore()));
		}
		
		return tests;
	}

	@Override
	public List<TestExamBean> getFinishedTestDataForTODO(Long timeboundid, String sapid) throws Exception {

		String sessionPlanId = studentTestDao.getSessionPlan( timeboundid.toString() );
		
		List<String> sessionPlanModules = studentTestDao.getSessionPlanModuleList( sessionPlanId );
		List<String> referenceId = studentTestDao.getTestLiveSettings( sessionPlanModules );
		List<String> testids = studentTestDao.getTestConfigurations( referenceId );
		List<TestExamBean> tests = studentTestDao.getFinishedTest( testids );
		List<TestExamBean> studentTestDetails = studentTestDao.getTestDetails( sapid );
		
		final Map<Long,TestExamBean> studentDetailsMap = studentTestDetails.stream().collect(Collectors.toMap(TestExamBean::getTestId,
                Function.identity()));

		tests.stream()
			.filter(test -> studentDetailsMap.containsKey(test.getId()))
			.forEach(test -> {
				test.setAttempt( studentDetailsMap.get(test.getId()).getAttempt() );
				test.setTestStartedOn( studentDetailsMap.get(test.getId()).getTestStartedOn() );
				test.setRemainingTime( studentDetailsMap.get(test.getId()).getRemainingTime() );
				test.setTestEndedOn( studentDetailsMap.get(test.getId()).getTestEndedOn() );
				test.setTestCompleted( studentDetailsMap.get(test.getId()).getTestCompleted() );
				test.setScore( studentDetailsMap.get(test.getId()).getScore() );
				test.setScoreInInteger( studentDetailsMap.get(test.getId()).getScoreInInteger()) ;
				test.setNoOfQuestionsAttempted( studentDetailsMap.get(test.getId()).getNoOfQuestionsAttempted() );
				test.setCurrentQuestion( studentDetailsMap.get(test.getId()).getCurrentQuestion() );
			});
		
		tests.removeIf(test -> ( "N".equals(test.getTestCompleted())) );
		
		return tests;
	}

	@Override
	public List<TestExamBean> getTestDataForCalendar(String sapid) throws Exception {
		// TODO Auto-generated method stub
		
		List<String> timeboundIds = studentTestDao.getTimeboundId(sapid);
		
		List<String> currentCycleTimeboundids = timeboundIds.stream() 
				.filter(id ->{
					try {
						return studentTestDao.checkIfTimeboundOfCurrentCycle(id, current_mbawx_acad_year, current_mbawx_acad_month);
					} catch (Exception e) {
						// TODO Auto-generated catch block
						return false;
					}
				})
				.collect(Collectors.toList());

		List<String> sessionPlanIds = currentCycleTimeboundids.stream()
				.map(id -> {
					try {
						return studentTestDao.getSessionPlan(id);
					} catch (Exception e) {
						// TODO Auto-generated catch block
						return null;
					}
					
				})
				.collect(Collectors.toList());

		List<String> sessionPlanModules = new ArrayList<>();
		sessionPlanIds.stream()
				.forEach(id->{
					try {
						sessionPlanModules.addAll(studentTestDao.getSessionPlanModuleList(id));
					} catch (Exception e) {
						// TODO Auto-generated catch block
					}
				});

		List<String> referenceId = studentTestDao.getTestLiveSettings( sessionPlanModules );
		List<TestExamBean> tests = studentTestDao.getTestForReferenceId( referenceId );
		List<TestExamBean> studentTestDetails = studentTestDao.getTestDetails( sapid );
		List<TestExamBean> studentTestExtendedDetails = studentTestDao.getStudentTestExtendedDetails( sapid );
		
		final Map<Long,TestExamBean> studentDetailsMap = studentTestDetails.stream().collect(Collectors.toMap(TestExamBean::getTestId,
                Function.identity()));
		final Map<Long,TestExamBean>  studentTestExtended = studentTestExtendedDetails.stream().collect(Collectors.toMap(TestExamBean::getTestId,
                Function.identity()));
		
		tests.stream()
			.filter(test -> studentDetailsMap.containsKey(test.getId()))
			.forEach(test -> {
				test.setAttemptStatus( studentDetailsMap.get(test.getId()).getAttemptStatus() );
				test.setAttempt( studentDetailsMap.get(test.getId()).getAttempt() );
				test.setTestStartedOn( studentDetailsMap.get(test.getId()).getTestStartedOn() );
				test.setRemainingTime( studentDetailsMap.get(test.getId()).getRemainingTime() );
				test.setTestCompleted( studentDetailsMap.get(test.getId()).getTestCompleted() );
				test.setScore( studentDetailsMap.get(test.getId()).getScore() );
				test.setScoreInInteger( studentDetailsMap.get(test.getId()).getScoreInInteger()) ;
				test.setNoOfQuestionsAttempted( studentDetailsMap.get(test.getId()).getNoOfQuestionsAttempted() );
				test.setCurrentQuestion( studentDetailsMap.get(test.getId()).getCurrentQuestion() );
			});
		
		tests.stream()
			.filter(test -> !studentDetailsMap.containsKey(test.getId()))
			.forEach(test -> {
				test.setAttempt( 0 );
				test.setRemainingTime( 0 );
				test.setScore( 0.0 );
				test.setTestCompleted( null );
			});

		tests.stream()
			.filter(test -> studentTestExtended.containsKey(test.getId()))
			.forEach(test -> {
				test.setTestEndedOn(studentTestExtended.get(test.getId()).getExtendedEndTime());
			});

		tests.stream()
			.forEach(test -> {
				try {
					test.setAttemptStatus(getAttemptStatus(test));
				} catch (ParseException e) {
					// TODO Auto-generated catch block
				}
			});
		
		return tests;
	}

	private String getAttemptStatus(TestExamBean test) throws ParseException {

		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		String attemptStatus = "";
		
		Calendar calendar = Calendar.getInstance();
		
		Date date = new Date();
		Date endDate = format.parse(test.getEndDate().replace("T", " "));
		
		if( !StringUtils.isBlank(test.getTestStartedOn()))
			calendar.setTime(format.parse(test.getTestStartedOn()));
		else
			calendar.setTime(format.parse(test.getEndDate().replace("T", " ")));
		
		calendar.add(Calendar.MINUTE, test.getDuration());
		
		Date testEndTime = calendar.getTime();

		if(date.after(endDate) && StringUtils.isBlank(test.getAttemptStatus()))
			attemptStatus = "Not Attempted";
		else if(date.before(endDate) && StringUtils.isBlank(test.getAttemptStatus()))
			attemptStatus = "Upcoming";
		else if("N".equals(test.getTestCompleted()) && date.before(testEndTime))
			attemptStatus = "Resume";
		else 
			attemptStatus = test.getAttemptStatus();
		
		return attemptStatus;
		
	}
	
	public List<TestExamBean> getTestDataForResources(String sapid, String referenceId) throws Exception{
		
		List<String> referenceIds = studentTestDao.getTestLiveSettingsForSingleId( referenceId );
		List<TestExamBean> tests = studentTestDao.getTestForReferenceId( referenceIds );
		List<TestExamBean> studentTestDetails = studentTestDao.getTestDetails( sapid );
		List<TestExamBean> studentTestExtendedDetails = studentTestDao.getStudentTestExtendedDetails( sapid );
		
		final Map<Long,TestExamBean> studentDetailsMap = studentTestDetails.stream().collect(Collectors.toMap(TestExamBean::getTestId,
                Function.identity()));
		final Map<Long,TestExamBean>  studentTestExtended = studentTestExtendedDetails.stream().collect(Collectors.toMap(TestExamBean::getTestId,
                Function.identity()));

		tests.stream()
			.filter(test -> studentDetailsMap.containsKey(test.getId()))
			.forEach(test -> {
				test.setAttemptStatus( studentDetailsMap.get(test.getId()).getAttemptStatus() );
				test.setAttempt( studentDetailsMap.get(test.getId()).getAttempt() );
				test.setTestStartedOn( studentDetailsMap.get(test.getId()).getTestStartedOn() );
				test.setTestCompleted( studentDetailsMap.get(test.getId()).getTestCompleted() );
				test.setScore( studentDetailsMap.get(test.getId()).getScore() );
				test.setScoreInInteger( studentDetailsMap.get(test.getId()).getScoreInInteger()) ;
				test.setNoOfQuestionsAttempted( studentDetailsMap.get(test.getId()).getNoOfQuestionsAttempted() );
				test.setCurrentQuestion( studentDetailsMap.get(test.getId()).getCurrentQuestion() );
			});

		tests.stream()
			.filter(test -> !studentDetailsMap.containsKey(test.getId()))
			.forEach(test -> {
				test.setAttempt( 0 );
				test.setRemainingTime( 0 );
				test.setScore( 0.0 );
				test.setTestCompleted( null );
			});

		tests.stream()
			.filter(test -> studentTestExtended.containsKey(test.getId()))
			.forEach(test -> {
				test.setStartDate(studentTestExtended.get(test.getId()).getExtendedStartTime());
				test.setEndDate(studentTestExtended.get(test.getId()).getExtendedEndTime());
			});

		return tests;
		
	}

	public List<TestExamBean> getArchiveTestDataForResources(String sapid, String referenceId) throws Exception{
		
		List<String> referenceIds = studentTestDao.getArchiveTestLiveSettingsForSingleId( referenceId );
		List<TestExamBean> tests = studentTestDao.getArchiveTestForReferenceId( referenceIds );
		List<TestExamBean> studentTestDetails = studentTestDao.getArchiveTestDetails( sapid );
		List<TestExamBean> studentTestExtendedDetails = studentTestDao.getArchiveStudentTestExtendedDetails( sapid );
		
		final Map<Long,TestExamBean> studentDetailsMap = studentTestDetails.stream().collect(Collectors.toMap(TestExamBean::getTestId,
                Function.identity()));
		final Map<Long,TestExamBean>  studentTestExtended = studentTestExtendedDetails.stream().collect(Collectors.toMap(TestExamBean::getTestId,
                Function.identity()));

		tests.stream()
			.filter(test -> studentDetailsMap.containsKey(test.getId()))
			.forEach(test -> {
				test.setAttemptStatus( studentDetailsMap.get(test.getId()).getAttemptStatus() );
				test.setAttempt( studentDetailsMap.get(test.getId()).getAttempt() );
				test.setTestStartedOn( studentDetailsMap.get(test.getId()).getTestStartedOn() );
				test.setTestCompleted( studentDetailsMap.get(test.getId()).getTestCompleted() );
				test.setScore( studentDetailsMap.get(test.getId()).getScore() );
				test.setScoreInInteger( studentDetailsMap.get(test.getId()).getScoreInInteger()) ;
				test.setNoOfQuestionsAttempted( studentDetailsMap.get(test.getId()).getNoOfQuestionsAttempted() );
				test.setCurrentQuestion( studentDetailsMap.get(test.getId()).getCurrentQuestion() );
			});

		tests.stream()
			.filter(test -> !studentDetailsMap.containsKey(test.getId()))
			.forEach(test -> {
				test.setAttempt( 0 );
				test.setRemainingTime( 0 );
				test.setScore( 0.0 );
				test.setTestCompleted( null );
			});

		tests.stream()
			.filter(test -> studentTestExtended.containsKey(test.getId()))
			.forEach(test -> {
				test.setStartDate(studentTestExtended.get(test.getId()).getExtendedStartTime());
				test.setEndDate(studentTestExtended.get(test.getId()).getExtendedEndTime());
			});

		
		return tests;
		
	}

	@Override
	public boolean checkIfTestPddmProject(final String subject, final String testType) {
		return SUBJECT_DIGITAL_MARKETING_STRATEGY.equals(subject) && TEST_TYPE_PROJECT.equals(testType);
	}

	@Override
	public boolean checkProjectRegBookingStatus(final String sapid, final long timeboundId, final int iaMaxScore) {
		try {
			String bookingType = (iaMaxScore != RE_EXAM_DMS_MAX_SCORE) ? BOOKING_TYPE_PROJECT_REGISTRATION : BOOKING_TYPE_PROJECT_RE_REGISTRATION;
			
			int bookedRecordsCount = noSlotBookingDAO.checkNoSlotBookingStatus(sapid, timeboundId, bookingType, NOSLOT_BOOKING_STATUS_BOOKED);
			logger.info("No of booked records: {} for sapid: {} with timeboundId: {} and bookingType: {}", bookedRecordsCount, sapid, timeboundId, bookingType);
			
			return bookedRecordsCount > 0;
		}
		catch(Exception ex) {
			logger.error("Error while checking Project Registration booking status of student: {} with timeboundId: {}, Exception thrown:", 
						sapid, timeboundId, ex);
			return false;
		}
	}

	@Override
	public boolean getStudentTimeboundAndCheckProjectRegStatus(String sapid, int iaMaxScore, int referenceId) {
		try {
			int timeboundId = testDao.getTimeboundIdByModuleId(referenceId);
			logger.info("TimeboundId: {} obtained of student: {} with test referenceId: {}", timeboundId, sapid, referenceId);
			
			return checkProjectRegBookingStatus(sapid, timeboundId, iaMaxScore);
		}
		catch(Exception ex) {
			logger.error("Error while getting the timeboundId of student: {} with test referenceId: {}, Exception thrown:", 
						sapid, referenceId, ex);
			return false;
		}
	}
	
	@Override
	public List<TestExamBean> projectReExamListForResitStudent(final String sapid) {
		try {
			final List<Long> timeboundIdList = studentTestDao.getTimeboundIdsBySapid(sapid);
			
			return timeboundIdList.stream()
							.filter(timeboundId -> checkResitStudentForProjectDMS(timeboundId, sapid))			//Filter out non Resit - PDDM Q4 students
							.flatMap(timeboundId -> projectReExamForResit(timeboundId, sapid).stream())			//fetching tests for Resit students
							.collect(Collectors.toList());														//using faltMap to collect List of tests in a single list
		}
		catch(Exception ex) {
			logger.error("Error while fetching project reExam tests for resit sapid: {}, Exception thrown: ", sapid, ex);
			return new ArrayList<>();
		}
	}
	
	@Override
	public List<TestExamBean> projectReExamListForModuleId(final String sapid, final Integer referenceId) {
		try {
			final Long timeboundId = studentTestDao.getTimeboundIdBySessionPlanModuleId(referenceId);
			
			//Fetching reExam tests for Resit - PDDM Q4 students
			if(checkResitStudentForProjectDMS(timeboundId, sapid))
				return projectReExamForResit(timeboundId, sapid);
		}
		catch(Exception ex) {
			logger.error("Error while fetching project reExam tests for resit sapid: {} and referenceId: {}, Exception thrown: ", 
						sapid, referenceId, ex);
		}
		
		return new ArrayList<>();
	}
	
	/**
	 * Checks if the student is mapped to the timebound with role Resit, 
	 * and the timebound is of PDDM Q4 (Digital Marketing Strategy)
	 * @param timeboundId
	 * @param sapid
	 * @return boolean value indicating if the timebound for subject DMS and user role Resit
	 */
	private boolean checkResitStudentForProjectDMS(final Long timeboundId, final String sapid) {
		try {
			final int timeboundMappingCount = noSlotBookingDAO.getTimeboundMappingCountByUserIdTimeboundIdRole(sapid, timeboundId, TIMEBOUND_USER_MAPPING_ROLE_RESIT);
			if(timeboundMappingCount < 1)
				return false;
			
			//Fetching the programSemSubject ID from the timebound and retrieving the consumerProgramStructureId and Program Name
			Long programSemSubjectId = studentTestDao.getPssIdByTimeboundId(timeboundId);
			ProgramSubjectMappingExamBean programSemSubjectDetails = studentTestDao.getCpsIdSubjectById(programSemSubjectId);
			
			return CPS_PROJECT_PDDM_JUL2021_RETAIL.equals(programSemSubjectDetails.getConsumerProgramStructureId())
					&& SUBJECT_DIGITAL_MARKETING_STRATEGY.equals(programSemSubjectDetails.getSubject());
		}
		catch(Exception ex) {
			logger.error("Error while checking student Resit role for subject: {}, Exception thrown: ", SUBJECT_DIGITAL_MARKETING_STRATEGY, ex);
			return false;
		}
	}
	
	/**
	 * Fetching the project reExam test details for the student using the timeboundId and sapid.
	 * @param timeboundId
	 * @param sapid
	 * @return project reExam tests
	 */
	private List<TestExamBean> projectReExamForResit(final Long timeboundId, final String sapid) {
		try {
			String sessionPlanId = studentTestDao.getSessionPlan(String.valueOf(timeboundId));
			
			List<String> sessionPlanModules = studentTestDao.getSessionPlanModuleList(sessionPlanId);
			List<String> referenceId = studentTestDao.getTestLiveSettings(sessionPlanModules);
			
			//Fetching Project tests with maxScore of 100
			List<TestExamBean> testList = studentTestDao.getTestByReferenceIdMaxScore(referenceId, RE_EXAM_DMS_MAX_SCORE, TEST_TYPE_PROJECT);
			return studentTestDataList(sapid, testList);
		}
		catch(Exception ex) {
			logger.error("Error while fetching project reExam tests for resit sapid: {} and timeboundId: {}, Exception thrown: ", 
						sapid, timeboundId, ex);
			return new ArrayList<>();
		}
	}
	
	/**
	 * Checks if the test is extended for the student and replaces the startDate and endDate of the test.
	 * And fetches the test details of the test for the student.
	 * @param sapid - studentNo
	 * @param tests - list of tests
	 * @return test list containing the student test details
	 */
	private List<TestExamBean> studentTestDataList(final String sapid, final List<TestExamBean> tests) {
		return tests.stream()
					.map(test -> sapidTestExtendStartEndTime(test, sapid))				//Updates the startDate and endDate of extended tests
					.map(test -> testDetailsBySapid(test, sapid))						//Fetches the student test details of the test
					.collect(Collectors.toList());
	}
	
	/**
	 * Checks if the test is extended for the student and updates the startDate and endDate of the test with the extended start and end time.
	 * @param testBean - bean containing the test details
	 * @param sapid - studentNo
	 * @return test bean
	 */
	private TestExamBean sapidTestExtendStartEndTime(final TestExamBean testBean, final String sapid) {
		try {
			TestExamBean testExtendedBean = studentTestDao.getTestExtendedBySapidTestId(sapid, testBean.getId());
			
			if(Objects.nonNull(testExtendedBean.getExtendedStartTime()) && Objects.nonNull(testExtendedBean.getExtendedEndTime())) {
				testBean.setStartDate(testExtendedBean.getExtendedStartTime());
				testBean.setEndDate(testExtendedBean.getExtendedEndTime());
			}
		}
		catch(DataAccessException ex) {
			//DataAccessException thrown when no test extended records found for testId and sapid
		}
		
		return testBean;
	}
	
	/**
	 * Fetches the student test details of the test using the provided sapid and testId.
	 * @param testBean - bean containing the test details
	 * @param sapid - studentNo
	 * @return test bean
	 */
	private TestExamBean testDetailsBySapid(final TestExamBean testBean, final String sapid) {
		final int attempt = 1;
		try {
			StudentsTestDetailsExamBean studentTestDetails = studentTestDao.getStudentTestDetailsBySapidTestIdAttempt(sapid, testBean.getId(), attempt);
			
			testBean.setTestId(studentTestDetails.getTestId());
			testBean.setAttempt(studentTestDetails.getAttempt());
			testBean.setTestStartedOn(studentTestDetails.getTestStartedOn());
			testBean.setRemainingTime(studentTestDetails.getRemainingTime());
			testBean.setTestEndedOn(studentTestDetails.getTestEndedOn());
			testBean.setTestCompleted(studentTestDetails.getTestCompleted());
			testBean.setScore(studentTestDetails.getScore());
			testBean.setNoOfQuestionsAttempted(studentTestDetails.getNoOfQuestionsAttempted());
			testBean.setCurrentQuestion(studentTestDetails.getCurrentQuestion());
			testBean.setShowResult(studentTestDetails.getShowResult());
		}
		catch(DataAccessException ex) {
			//DataAccessException thrown when no test details records found for testId and sapid
			testBean.setTestId(testBean.getId());
			testBean.setAttempt(0);
			testBean.setRemainingTime(0);
			testBean.setScore(0.0);
		}
		
		return testBean;
	}
}
