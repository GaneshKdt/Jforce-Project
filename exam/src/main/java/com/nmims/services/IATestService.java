package com.nmims.services;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.nmims.beans.ResponseBean;
import com.nmims.beans.ResultDomain;
import com.nmims.beans.SectionBean;
import com.nmims.beans.StudentQuestionResponseExamBean;
import com.nmims.beans.StudentsTestDetailsExamBean;
import com.nmims.beans.TEEResultBean;
import com.nmims.beans.TestExamBean;
import com.nmims.beans.TestQuestionExamBean;
import com.nmims.daos.TestDAO;

@Service("iaTestService")
public class IATestService {
	@Autowired
	private StudentTestServiceInterface studentTestService;
	
	@Autowired
	private TestDAO testDAO;
	
	private static final int QUESTION_TYPE_DESCRIPTIVE = 4;
	
	private static final String TYPE_ASSIGNMENT = "Assignment";
	private static final String TYPE_PROJECT = "Project";
	private static final String TYPE_TEST = "Test";
	
	private static final String BOD_ACTIVE_TRUE = "Y";
	private static final String BOD_ACTIVE_FALSE = "N";
	
	private static final Logger logger = LoggerFactory.getLogger(IATestService.class);
	
	public List<TestExamBean> getAllLiveTestsBySapId(String sapId) throws Exception {
		List<TestExamBean> testsList = testDAO.getTestsBySapIdNTimeBoundIds(sapId);
		
		//Fetching reExam Project tests for Resit Students (PDDM Q4 - Digital Marketing Strategy)
		List<TestExamBean> resitReExamTestList = studentTestService.projectReExamListForResitStudent(sapId);
		testsList.addAll(resitReExamTestList);
		
		testsList.removeIf(test -> studentTestService.checkIfTestPddmProject(test.getSubject(), test.getTestType())
									&& !studentTestService.getStudentTimeboundAndCheckProjectRegStatus(sapId, test.getMaxScore(), test.getReferenceId()));
		return testsList;
	}
	
	
	public LinkedHashMap<String, List<TestQuestionExamBean>> setSectionForTestQuestion( List<TestQuestionExamBean> testQuestionsList ){
		
		LinkedHashMap<String, List<TestQuestionExamBean>> hashmap =  new LinkedHashMap<String, List<TestQuestionExamBean>>();
		try {				
			Integer count = 0;
			for(TestQuestionExamBean bean : testQuestionsList) {
					count++;
					bean.setSrNoForSections(count);
					if( hashmap.containsKey(bean.getSectionName()) ) {
						List<TestQuestionExamBean> templist = hashmap.get(bean.getSectionName());
						templist.add(bean);
						hashmap.put(bean.getSectionName(), templist);
					}else{
						List<TestQuestionExamBean> templist = new ArrayList<TestQuestionExamBean>();
						templist.add(bean);
						hashmap.put(bean.getSectionName(), templist);
					}  
				
			}
			
	//		for (String i : hashmap.keySet()) {
	//		} 
			return hashmap;
		}catch(Exception e) {
			
			return hashmap;
		}
	}
	
	public  List<SectionBean> getApplicableSectionList(Long testId){
		List<SectionBean> list = new ArrayList<SectionBean>();
		try {
			list = testDAO.getApplicableSectionList(testId);
			return list;
		}catch (Exception e) {
			// TODO: handle exception
			
			return list;
		}
	}
	
	/**
	 * Iterates the CopyCase results list, if the question type is descriptive, 
	 * replaces the answer stored in text format with the descriptive answer URL of the student present in answers table.
	 * @param copyCaseList - list of Copy Case Result bean
	 */
	public void storeAnswerUrlForCopyCases(List<ResultDomain> copyCaseList) {
		try {
			copyCaseList.stream()
						.filter(copyCase -> checkQuestionTypeDescriptive(copyCase.getQuestionId()))			//checks if the question type is descriptive
						.forEach(copyCase -> storeDescriptiveAnswerUrl(copyCase));							//stores the descriptive answer URL
		}
		catch(Exception ex) {
			logger.error("Error while storing descriptive answerUrl for CopyCase students, Exception thrown: ", ex);
		}
	}
	
	/**
	 * Fetches max attempt of test.
	 * @param testId - ID of the test
	 * @return maximum test attempts
	 */
	public int testMaxAttempts(final Long testId) {
		try {
			return testDAO.getMaxAttemptById(testId);
		}
		catch(Exception ex) {
			logger.error("Error while fetching max attempts of testId: {}, Exception thrown: ", testId, ex);
			return 0;
		}
	}
	
	/**
	 * Fetches questions and sections from test ID passed as a parameter.
	 * If sections aren't present, the questions are mapped to a placeholder section name (Section 1).
	 * Else the questions are mapped to their particular sections.
	 * @param testId - ID of the test
	 * @return Map containing section-wise questions list
	 */
	public Map<String, List<TestQuestionExamBean>> sectionQuestionsList(final Long testId) {
		try {
			final List<TestQuestionExamBean> questionsList = testDAO.getQuestionsByTestId(testId);											//List of test questions
			final Map<Integer, String> questionTypeMap = testDAO.getTestQuestionTypeMap();													//Question type Map
			questionsList.forEach(questionBean -> questionBean.setTypeInString(questionTypeMap.get(questionBean.getType())));				//set question type for each questions
			
			final Map<Integer, String> sectionIdName = testSectionDetails(testId);															//Test sections ID-Name Map
			if(sectionIdName.isEmpty())																										//Checks if sections exist
				return Collections.singletonMap("Section 1", questionsList);																//Create a singleton Map with placeholder section Name as key
			
			return sectionIdName.entrySet()																														//sectionMap entry set
								.stream()																														//stream over set
								.collect(Collectors.toMap(	Entry::getValue,																					//collect Map value as key
															section -> questionsList.stream()																	//stream over questions list
																					.filter(question -> section.getKey().equals(question.getSectionId()))		//filter questions not matching section
																					.collect(Collectors.toList()), 												//collect as a List
															(questionList1, questionList2) -> Stream.of(questionList1, questionList2)							//merge function for duplicate keys
																									.flatMap(Collection::stream)								//collects both stream values in one stream
																									.collect(Collectors.toList())	));							//collect as a List
		}
		catch(Exception ex) {
			logger.error("Error while obtaining section-wise questions list using testId: {}, Exception thrown: ", testId, ex);
			return new HashMap<>();
		}
	}
	
	/**
	 * Fetches list of Benefit of Doubt applied questions.
	 * @param testId - ID of the test
	 * @return list of question IDs
	 */
	public List<Integer> bodAppliedQuestions(final Long testId) {
		try {
			return testDAO.getBodQuestionsByTestIdActive(testId, BOD_ACTIVE_TRUE);
		}
		catch(Exception ex) {
			logger.error("Error while obtaining BoD applied questions list using testId: {}, Exception thrown: ", testId, ex);
			return new ArrayList<>();
		}
	}
	
	/**
	 * Fetches test type, end date and duration from the testID passed as a parameter.
	 * And depending upon the test type checks if the test is completed.
	 * @param testId - ID of the test
	 * @return boolean value indicating if the test is completed
	 */
	public boolean checkIfTestCompleted(final Long testId) {
		try {
			final TestExamBean testBean = testDAO.getTestTypeEndDateDurationByTestId(testId);
			return checkTestCompletedByType(testBean.getEndDate(), testBean.getDuration(), testBean.getTestType());
		}
		catch(Exception ex) {
			logger.error("Error while checking if test is completed for testId: {}, Exception thrown: ", testId, ex);
			return false;
		}
	}
	
	/**
	 * Checks if the test results are live using the test ID passed as a parameter.
	 * @param testId - ID of the test
	 * @return boolean value indicating if the test results are live
	 */
	public boolean checkIfTestResultsLive(final Long testId) {
		try {
			final int resultsLiveCount = testDAO.checkTestResultsLive(testId);
			return resultsLiveCount > 0;
		}
		catch(Exception ex) {
			logger.error("Error while checking if the results are live for testId: {}, Exception thrown: ", testId, ex);
			return false;
		}
	}
	
	/**
	 * Applying Benefit of Doubt for the passed test questionId,
	 * and re-run results for students who were assigned the questionId if test results live.
	 * Also checks if student TEE marks are present for the timebound ID and marks the processed flag as N.
	 * @param testId - ID of the test
	 * @param questionId - ID of the question
	 * @param userId - ID of the user
	 * @return count of results re-run students
	 */
	@Transactional
	public Map<String, Boolean> applyingBenefitOfBoubt(final Long testId, final Long questionId, final String userId) {
		Map<String, Boolean> responseMap = new HashMap<>();
		boolean testCompleted = checkIfTestCompleted(testId);															//Checks if test is completed
		if(!testCompleted)
			throw new IllegalArgumentException("Cannot apply BoD as test is not completed for testId: " + testId);
		
		int recordsUpdated = insertActiveBenefitOfDoubt(testId, questionId, userId);									//inserts/updates BoD record
		logger.info("{} active Benefit of Doubt records inserted for testId: {} and questionId: {} by user: {}", recordsUpdated, testId, questionId, userId);
		responseMap.put("benefitOfDoubt", true);
		
		boolean resultsLive = checkIfTestResultsLive(testId);															//Checks if results are live
		logger.info("Test results live: {} for testId: {}", resultsLive, testId);
		if(resultsLive) {
			int studentResultsReRun = reRunTestResults(testId, questionId, userId);										//re-run results for students with assigned questionId
			logger.info("Successfully applied BoD and re-run results for {} students of testId: {} and questionId: {} by user: {}", studentResultsReRun, testId, questionId, userId);
			responseMap.put("resultsLive", true);
			return responseMap;
		}
		
		unmarkTEEProcessedForTestQuestionStudents(testId, questionId, userId);											//Check TEE processed for students and unmark the processed flag
		logger.info("TEE processed flag unmarked for students of testId: {} with questionId: {}", testId, questionId);
		responseMap.put("resultsLive", false);
		return responseMap;
	}
	
	/**
	 * Removing Benefit of Doubt for the passed test questionId
	 * and re-run results for students who were assigned the questionId if test results live.
	 * Also checks if student TEE marks are present for the timebound ID and marks the processed flag as N.
	 * @param testId - ID of the test
	 * @param questionId - ID of the question
	 * @param userId - ID of the user
	 * @return count of results re-run students
	 */
	@Transactional
	public Map<String, Boolean> removingBenefitOfBoubt(final Long testId, final Long questionId, final String userId) {
		Map<String, Boolean> responseMap = new HashMap<>();
		boolean testCompleted = checkIfTestCompleted(testId);															//Checks if test is completed
		if(!testCompleted)
			throw new IllegalArgumentException("Cannot remove BoD as test is not completed for testId: " + testId);
		
		int recordsUpdated = testDAO.updateBenefitOfDoubt(testId, questionId, BOD_ACTIVE_FALSE, userId);				//updates BoD record
		logger.info("{} inactive Benefit of Doubt records updated for testId: {} and questionId: {} by user: {}", recordsUpdated, testId, questionId, userId);
		responseMap.put("benefitOfDoubt", true);
		
		boolean resultsLive = checkIfTestResultsLive(testId);															//Checks if results are live
		logger.info("Test results live: {} for testId: {}", resultsLive, testId);
		if(resultsLive) {
			int studentResultsReRun = reRunTestResults(testId, questionId, userId);										//re-run results for students with assigned questionId
			logger.info("Successfully removed BoD and re-run results for {} students of testId: {} and questionId: {} by user: {}", studentResultsReRun, testId, questionId, userId);
			
			responseMap.put("resultsLive", true);
			return responseMap;
		}
		
		unmarkTEEProcessedForTestQuestionStudents(testId, questionId, userId);											//Check TEE processed for students and unmark the processed flag
		logger.info("TEE processed flag unmarked for students of testId: {} with questionId: {}", testId, questionId);
		responseMap.put("resultsLive", false);
		return responseMap;
	}
	
	/**
	 * Fetches question attempts details:
	 * How many students attempted the test?
	 * How many students got the mentioned question?
	 * How many students selected the correct/wrong options?
	 * How many students attempted/not-attempted the question?
	 * @param testId - ID of the test
	 * @param questionId - ID of the test question
	 * @param questionType - type of the question
	 * @param testMaxAttempts - max attempts of the test
	 * @return Map containing the question attempts details
	 */
	public Map<String, Integer> questionAttemptDataMap(final Long testId, final Long questionId, final Integer questionType, final Integer testMaxAttempts) {
		Map<String, Integer> attemptDataMap = new HashMap<>();
		try {
			int studentAttemptCount = testDAO.getStudentAttemptCountByTestId(testId);																					//count of student attempted the test
			logger.info("{} students attempted the test with id: {}", studentAttemptCount, testId);
			
			Set<String> questionApplicableStudentSet = questionApplicableStudents(testId, questionId);																	//students assigned the questionId
			int questionApplicableStudentCount = questionApplicableStudentSet.size();																					//count of students assigned the questionId
			logger.info("{} students are applicable for questionId: {} of testId: {}", questionApplicableStudentCount, questionId, testId);
			
			List<StudentQuestionResponseExamBean> studentAnswerAttemptsList = testDAO.getAnswerAttemptsByTestIdQuestionId(testId, questionId);							//list of student test answers
			
			if((questionType == 1 || questionType == 2 || questionType == 5) && testMaxAttempts == 1) {																	//Type: SS, MS, TF and maxAttempt: 1
				List<String> correctOptionIdList = testDAO.getCorrectOptionsByQuestionId(questionId);
				
				//Storing student IDs who selected a wrong answer
				Set<String> wrongSelectionStudentSet = studentAnswerAttemptsList.stream()
																				.filter(answer -> !correctOptionIdList.contains(answer.getAnswer()))					//filter students who selected the wrong answer
																				.map(StudentQuestionResponseExamBean::getSapid)											//process sapid from the answer bean
																				.collect(Collectors.toSet());															//storing in Set to avoid duplicates
				
				//A Map is used to determine count of correct selection students, to avoid edge cases of students selecting 2 correct options from a total of 3 (for example)
				Map<String, Long> studentAnswersCountMap = studentAnswerAttemptsList.stream()
																					.filter(answer -> !wrongSelectionStudentSet.contains(answer.getSapid()))			//filter students present in wrong answer student list
																					.map(StudentQuestionResponseExamBean::getSapid)										//process sapid from the answer bean
																					.collect(Collectors.groupingBy(Function.identity(), Collectors.counting()));		//store in Map with no. of answers selected
				
				//using Collections frequency, count of correct selection for each student can be determined
				int correctSelectionStudentCount = Collections.frequency(studentAnswersCountMap.values(), (long) correctOptionIdList.size());							//right answer(s) selected student count
				logger.info("{} students selected the correct option(s) for questionId: {}", correctSelectionStudentCount, questionId);
				
				//wrong selection student count is a total of students who selected a wrong answer and students who did not select all the required correct options 
				int wrongSelectionStudentCount = wrongSelectionStudentSet.size() + (studentAnswersCountMap.size() - correctSelectionStudentCount);						//wrong answer(s) selected student count
				logger.info("{} students selected an wrong option(s) for questionId: {}", wrongSelectionStudentCount, questionId);
				
				attemptDataMap.put("right-selection", correctSelectionStudentCount);
				attemptDataMap.put("wrong-selection", wrongSelectionStudentCount);
				attemptDataMap.put("not-attempted", questionApplicableStudentCount - (correctSelectionStudentCount + wrongSelectionStudentCount));
			}
			else {
				int studentCount = studentAnswerAttemptsList.stream()
															.map(StudentQuestionResponseExamBean::getSapid)																//process sapid from the answer bean
															.collect(Collectors.toSet())																				//storing in Set to avoid duplicates
															.size();																									//no. of distinct student who attempted the questions
				
				attemptDataMap.put("question-attempted", studentCount);
				attemptDataMap.put("not-attempted", questionApplicableStudentCount - studentCount);
			}
			
			attemptDataMap.put("test-attempted", studentAttemptCount);
			attemptDataMap.put("applicable-students", questionApplicableStudentCount);
		}
		catch(Exception ex) {
			logger.error("Error while obtaining question attempt count data for testId: {} and questionId: {}, Exception thrown: ", testId, questionId, ex);
		}
		
		return attemptDataMap;
	}
	
	/**
	 * Fetches student question attempt details,
	 * adds the option value of the selected option for single-select, multi-select and true-false questions,
	 * and test name of the test.
	 * @param testId - ID of the test
	 * @param questionId - ID of the test question
	 * @param questionType - type of the question
	 * @return List of student question attempt details
	 */
	public List<StudentQuestionResponseExamBean> questionDetails(final Long testId, final Long questionId, final Integer questionType) {
		try {
			final String testName = testDAO.getTestNameById(testId);																					//Store test name
			
			if(questionType == 1 || questionType == 2 || questionType == 5) {																			//Type: SS, MS, TF
				final Map<Integer, String> questionOptionDataMap = testDAO.getOptionDataByQuestionId(questionId);										//Option Map
				
				List<StudentQuestionResponseExamBean> questionAttemptsList = testDAO.getAnswerAttemptsByTestIdQuestionId(testId, questionId);			//Answer attempt details
				questionAttemptsList.forEach(questionAttempt -> {
					Integer optionSelected = Integer.valueOf(questionAttempt.getAnswer());
					questionAttempt.setTestName(testName);																								//Adding test name
					questionAttempt.setAnswer(questionOptionDataMap.get(optionSelected));																//Adding Option value selected by student
				});
				
				return questionAttemptsList;
			}
			
			List<StudentQuestionResponseExamBean> attemptsDataList = testDAO.getAnswerAttemptDataByTestIdQuestionId(testId, questionId);				//Answer attempt details
			attemptsDataList.forEach(attempt -> attempt.setTestName(testName));																			//Adding test name
			
			return attemptsDataList;
		}
		catch(Exception ex) {
			logger.info("Error while obtaining student attempts data for testId: {} and questionId: {}, Exception thrown: ", testId, questionId, ex);
			return new ArrayList<>();
		}
	}
	
	/**
	 * Fetches details regarding options selected by each user 
	 * and returns the count for each option for single-select, multi-select and true-false question type.
	 * @param testId - ID of the test
	 * @param questionId - ID of the question
	 * @param questionType - type of question
	 * @return Map containing user count for each option of question
	 */
	public Map<String, Integer> optionSelectedDataMap(final Long testId, final Long questionId, final Integer questionType) {
		try {
			if(questionType == 1 || questionType == 2 || questionType == 5) {																								//Type: SS, MS, TF
				Map<Integer, String> questionOptionDataMap = testDAO.getOptionDataByQuestionId(questionId);
				return questionOptionDataMap.entrySet()
											.stream()
											.collect(Collectors.toMap(option -> "No. of students who selected Option: " + option.getValue(), 								//Option value
																	  option -> testDAO.getOptionSelectedCount(testId, questionId, String.valueOf(option.getKey()))));		//count of users who selected the given option
			}
		}
		catch(Exception ex) {
			logger.error("Error while obtaining question option count data for testId: {} and questionId: {}, Exception thrown: ", testId, questionId, ex);
		}
		
		return new HashMap<>();
	}
	
	/**
	 * Fetches timebound ID using testId and sapid and checks if TEE marks is present for the timebound ID.
	 * Marks the processed flag to false in tee_marks table.
	 * @param testId - ID of the test
	 * @param sapid - student No.
	 * @param userId - ID of the user
	 * @return bean containing status and errorMessage
	 */
	public ResponseBean unmarkTEEProcessed(final Long testId, final String sapid, final String userId) {
		ResponseBean response = new ResponseBean();
		TEEResultBean bean = testDAO.getStudentsTimeboundIdFromTest(testId, sapid);																	//get student timeboundId and sapId
		logger.info("Timebound ID: {} fetched of student: {} and testId: {}", bean.getTimebound_id(), sapid, testId);
		
		if(!bean.getSapid().isEmpty() && !bean.getTimebound_id().isEmpty()) {
			boolean teeMarksPresent = testDAO.checkIfTEEMarksPresent(bean);																			//check if students TEE score present
			logger.info("TEE marks present: {} of student: {} and timeboundId: {}", teeMarksPresent, sapid, bean.getTimebound_id());
			
			if(teeMarksPresent) {
				boolean updatedProcessFlag = testDAO.updateProcessedFlagInTEEMarks(bean, userId);													//marks the processed flag as N
				logger.info("Record updated in tee_marks with processed flag as N: {}", updatedProcessFlag);
				
				if(!updatedProcessFlag) {
					response.setStatus("error");
					response.setMessage("Error in updating processed Flag.");
					return response;
				}
			}
		}
		else {
			response.setStatus("error");
			response.setMessage("Error in getting timeboundID.");
			return response;
		}
		
		response.setStatus("success");
		return response;
	}
	
	/**
	 * Stores the descriptive answer URL present in the answers table for both the CopyCase students.
	 * @param copyCase - copy case result bean
	 */
	private void storeDescriptiveAnswerUrl(ResultDomain copyCase) {
		String sapid1DqAnswerUrl = getStudentDescriptiveAnswerUrl(copyCase.getSapId1(), copyCase.getTestId(), copyCase.getQuestionId());
		String sapid2DqAnswerUrl = getStudentDescriptiveAnswerUrl(copyCase.getSapId2(), copyCase.getTestId(), copyCase.getQuestionId());
		
		if(!StringUtils.isEmpty(sapid1DqAnswerUrl))				//replaces the descriptive answer of sapid1 if present
			copyCase.setFirstTestDescriptiveAnswer(sapid1DqAnswerUrl);
		
		if(!StringUtils.isEmpty(sapid2DqAnswerUrl))				//replaces the descriptive answer of sapid2 if present
			copyCase.setSecondTestDescriptiveAnswer(sapid2DqAnswerUrl);
	}
	
	/**
	 * Checks if the question type is descriptive (4).
	 * @param questionId - id of the test question
	 * @return boolean value indicating if the question is of type 4
	 */
	private boolean checkQuestionTypeDescriptive(Long questionId) {
		try {
			return testDAO.getTypeByQuestionId(questionId) == QUESTION_TYPE_DESCRIPTIVE;
		}
		catch(EmptyResultDataAccessException ex) {
			return false;
		}
	}
	
	/**
	 * Gets the student' descriptive answer stored in the answers table.
	 * @param sapid - studentNo of the student
	 * @param testId - id of the test
	 * @param questionId - id of the test question
	 * @return student descriptive answer URL
	 */
	private String getStudentDescriptiveAnswerUrl(String sapid, Long testId, Long questionId) {
		try {
			return testDAO.getAnswersBySapidTestIdQuestionId(sapid, testId, questionId).get(0);
		}
		catch(EmptyResultDataAccessException ex) {
			return "";
		}
	}
	
	/**
	 * Fetches section ID and Name from test ID passed as a parameter.
	 * @param testId - ID of the test
	 * @return Map containing section ID and Name
	 */
	private Map<Integer, String> testSectionDetails(final Long testId) {
		try {
			return testDAO.getSectionIdNameMapByTestId(testId);
		}
		catch(Exception ex) {
			logger.error("Error while obtaining section details of test using testId: {}, Exception thrown: ", testId, ex);
			return new HashMap<>();
		}
	}
	
	/**
	 * Checks if the test is completed using the current date time.
	 * If the type is Test, the current dateTime should be greater than endDate plus duration (minutes).
	 * If the type is Project or Assignment, the current dateTime should be greater than endDate.
	 * @param endDate - end date time of the test joining window
	 * @param duration - duration of the test in minutes
	 * @param testType - type of the test
	 * @return boolean value indicating if the test is completed
	 */
	private boolean checkTestCompletedByType(final String endDate, final int duration, final String testType) {
		final LocalDateTime endDateTime = LocalDateTime.parse(endDate, DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss"));								//Parsing test endDate as LocalDateTime
		
		switch(testType) {
			case TYPE_TEST:
				return LocalDateTime.now().isAfter(endDateTime.plusMinutes(duration));										//checks if current dateTime is after test endDate + duration in minutes
			case TYPE_PROJECT:
			case TYPE_ASSIGNMENT:
				return LocalDateTime.now().isAfter(endDateTime);															//checks if current dateTime is after test endDate (Project/Assignment test type)
			default:
				return false;
		}
	}
	
	/**
	 * Checks if Benefit of Doubt record exists, and updates or inserts an active BoD record accordingly.
	 * @param testId - ID of the test
	 * @param questionId - ID of the test question
	 * @param userId - ID of the user
	 * @return noOfRecords updated/inserted
	 */
	private int insertActiveBenefitOfDoubt(final Long testId, final Long questionId, final String userId) {
		int existingBodRecords = testDAO.checkBodByTestIdQuestionId(testId, questionId);									//checks if BoD record exists for questionId
		logger.info("No. of Benefit of Doubt records: {} found for testId: {} and questionId: {}", existingBodRecords, testId, questionId);
		
		if(existingBodRecords > 0)
			return testDAO.updateBenefitOfDoubt(testId, questionId, BOD_ACTIVE_TRUE, userId);								//updates record with active flag true
		
		return testDAO.insertBenefitOfDoubt(testId, questionId, BOD_ACTIVE_TRUE, userId);									//inserts a new BoD record for the questionId
	}
	
	/**
	 * Re-run results for students who were assigned the questionId passed as parameter.
	 * @param testId - ID of the test
	 * @param questionId - ID of the question
	 * @param userId - ID of the user
	 * @return no. of students whose results were re-run
	 */
	private int reRunTestResults(final Long testId, final Long questionId, final String userId) {
		List<StudentsTestDetailsExamBean> studentTestDetailsList = testDAO.getStudentTestAttemptQuestions(testId);																//List of test attempted students
		logger.info("{} no. of students attempted the test: {}", studentTestDetailsList.size(), testId);
		List<StudentsTestDetailsExamBean> applicableStudentList = studentTestDetailsList.stream()
																						.filter(testDetail -> Arrays.asList(testDetail.getTestQuestions().split(","))			//split questions by comma and store in list
																													.contains(String.valueOf(questionId)))						//check if question was assigned to student
																						.collect(Collectors.toList());															//store assigned students list
		logger.info("{} no. of students were applicable for questionId: {} of test: {}", applicableStudentList.size(), questionId, testId);
		
		return applicableStudentList.stream()
									.mapToInt(studentTestDetails -> calculateScoreReRunResults(testId, studentTestDetails.getId(), studentTestDetails.getSapid(), userId))		//stores each iteration count
									.sum();																																		//return count
	}
	
	/**
	 * Calculate test score of the student and update the scores.
	 * Check if TEE processed for student and unmark the processed flag.
	 * @param testId - ID of the test
	 * @param testDetailsId - ID of test detail
	 * @param sapid - Student No.
	 * @param userId - ID of the user
	 * @return count of student testDetails row updated
	 */
	private int calculateScoreReRunResults(final Long testId, final Long testDetailsId, final String sapid, final String userId) {
		double score = testDAO.caluclateTestScore(sapid, testId);															//Calculates the student test score
		logger.info("BoD score calculated of student: {} for testId: {} as {}", sapid, testId, score);
		
		int noOfRowsUpdated = testDAO.updateStudentTestScore(testDetailsId, score, userId);									//Update the test scores of the student
		logger.info("No. of test details records updated: {} for testId: {} and sapid: {} with score: {}", noOfRowsUpdated, testId, sapid, score);
		
		ResponseBean responseBean = unmarkTEEProcessed(testId, sapid, userId);												//Check TEE processed for student and unmark processed flag
		if("error".equals(responseBean.getStatus()))
			throw new IllegalArgumentException(responseBean.getMessage());
		
		return noOfRowsUpdated;
	}
	
	
	/**
	 * Fetches the students who attempted the test and were assigned the particular question,
	 * and checks if TEE marks is present for the student timebound ID 
	 * and marks the processed flag as N.
	 * @param testId - ID of the test
	 * @param questionId - ID of the test question
	 * @param userId - ID of the user
	 */
	private void unmarkTEEProcessedForTestQuestionStudents(final Long testId, final Long questionId, final String userId) {
		Set<String> applicableStudentSet = questionApplicableStudents(testId, questionId);									//Fetches list of test attempted student
		logger.info("{} students were applicable for questionId: {} of testId: {}", applicableStudentSet.size(), questionId, testId);
		
		applicableStudentSet.stream()
							.map(sapid -> unmarkTEEProcessed(testId, sapid, userId))											//checks TEE marks present and marks processed as N
							.forEach(response -> {																			//checks status of response returned
								if("error".equals(response.getStatus()))
									throw new IllegalArgumentException(response.getMessage());
							});
	}
	
	/**
	 * Fetches list of student test answers and returns distinct students who were assigned the questionId passed as parameter.
	 * @param testId - ID of the test
	 * @param questionId - ID of the test question
	 * @return students assigned the question
	 */
	private Set<String> questionApplicableStudents(final Long testId, final Long questionId) {
		List<StudentsTestDetailsExamBean> studentTestDetailList = testDAO.getStudentTestAttemptQuestions(testId);			//Fetches list of student answers
		logger.info("{} student attempt data fetched for testId: {}", studentTestDetailList.size(), testId);
		
		return studentTestDetailList.stream()
									.filter(testDetail -> Arrays.asList(testDetail.getTestQuestions().split(","))			//split questions by comma and store in list
																.contains(String.valueOf(questionId)))						//check if question was assigned to student
									.map(StudentsTestDetailsExamBean::getSapid)												//processing the student sapid from answer bean
									.collect(Collectors.toSet());															//storing in a Set to avoid duplicates
	}
}
