package com.nmims.stratergies.impl;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.nmims.beans.EmbaPassFailBean;
import com.nmims.beans.StudentsTestDetailsExamBean;
import com.nmims.beans.TEEResultBean;
import com.nmims.daos.ExamsAssessmentsDAO;
import com.nmims.stratergies.IAComponentPassFailInterface;

@Service("iaComponentPassFailStrategy")
public class IAComponentPassFailStrategy implements IAComponentPassFailInterface {

	@Autowired
	ExamsAssessmentsDAO examsAssessmentsDAO;
	
	private static final int IA_MAX_SCORE_TOTAL = 100;
	private static final String ATTEMPT_STATUS_ABSENT = "AB";
	private static final String ATTEMPT_STATUS_ATTEMPTED = "Attempted";
	private static final String ATTEMPT_STATUS_NOT_ATTEMPTED = "NA";
	private static final String IA_SCORE_ZERO = "0";
	
	private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("YYYY-MM-dd HH:mm:ss");
	private static final Logger logger = LoggerFactory.getLogger(IAComponentPassFailStrategy.class);

	
	@Override
	public void searchPassFail(TEEResultBean resultBean, List<EmbaPassFailBean> finalListforPassFail,
			List<TEEResultBean> studentsListEligibleForPassFail, String loggedInUser,
			List<EmbaPassFailBean> unsuccessfulPassFail) {
		
		Map<Integer, Integer> applicableTestScoreMap = getScheduledTestsForTimebound(resultBean.getTimebound_id()); 	//Map of testIds and max scores of applicable IAs stored
		logger.info("Applicable IAs: {} fetched from timeboundId: {}", applicableTestScoreMap.toString(), resultBean.getTimebound_id());
		
		//100 marks IA's stored in a List
		List<Integer> hunderedMarksIaList = applicableTestScoreMap.keySet()
																.stream()
																.filter(testId -> applicableTestScoreMap.get(testId) == IA_MAX_SCORE_TOTAL)
																.collect(Collectors.toList());
		
		//IA's which are not of hundred marks are rounded together and their total marks stored
		Optional<Integer> totalMarksOfIA = applicableTestScoreMap.entrySet()
																.stream()
																.filter(applicableIaMap -> !hunderedMarksIaList.contains(applicableIaMap.getKey()))
																.map(Map.Entry::getValue)
																.reduce(Integer::sum);
		logger.info("Hundered marks IA List: {} and Total marks of remaining IAs: {}", hunderedMarksIaList, totalMarksOfIA);
		
		for(TEEResultBean student : studentsListEligibleForPassFail) {
			EmbaPassFailBean studentFinalMarks = new EmbaPassFailBean();
			logger.info("sapid= "+student.getSapid()+ " timebound = "+student.getTimebound_id());	
			
			studentFinalMarks.setTimeboundId(student.getTimebound_id());
			studentFinalMarks.setSchedule_id(null);
			studentFinalMarks.setSapid(student.getSapid());
			studentFinalMarks.setPrgm_sem_subj_id(student.getPrgm_sem_subj_id());
			studentFinalMarks.setCreatedBy(loggedInUser);
			studentFinalMarks.setLastModifiedBy(loggedInUser);
			studentFinalMarks.setStatus(ATTEMPT_STATUS_ABSENT);			//setting Attempt Status as Absent by default for student
			
			if(totalMarksOfIA.isPresent() && totalMarksOfIA.get() != IA_MAX_SCORE_TOTAL) {
				logger.error("Max score of applicable IAs: {} not equal to {}!", applicableTestScoreMap.toString(), IA_MAX_SCORE_TOTAL);
				studentFinalMarks.setFailReason("Total test marks not equal to " + IA_MAX_SCORE_TOTAL);
				studentFinalMarks.setStatus(ATTEMPT_STATUS_NOT_ATTEMPTED);
				unsuccessfulPassFail.add(studentFinalMarks);
				continue;
			}
			
			ArrayList<StudentsTestDetailsExamBean> iaMarks = examsAssessmentsDAO.getIAScoresForStudentSubject(student.getSapid(),student.getTimebound_id() );  
			if(iaMarks == null || iaMarks.size() == 0) {
				studentFinalMarks.setIaScore(IA_SCORE_ZERO);
//				studentFinalMarks.setMax_score(String.valueOf(totalMaxScore));			//Commented as Max score not being used for any further calculations
			}
			else {
				double totalMarks = 0.0;
				String hunderedMarksTestEndDate = "";		//variable used to store EndDate of 100 marks test
//				int maxScore = 0;					//Commented as Max score not being used for any further calculations
				List<String> resultsNotLiveTestName = new ArrayList<>();		//list to store testName of tests with results not live
				
				List<StudentsTestDetailsExamBean> attemptedHunderedMarksTestList = iaMarks.stream()				//collecting IAs which are present in applicable tests of 100 marks list
																							.filter(testDetails -> hunderedMarksIaList.contains(testDetails.getTestId().intValue()))
																							.collect(Collectors.toList());
				
				if(attemptedHunderedMarksTestList.isEmpty()) {			//Student attempted IA is not of 100 marks, hence each attempted IA is iterated and score is obtained
					for(StudentsTestDetailsExamBean studentTestMarks: iaMarks) {
						totalMarks += studentTestMarks.getScoreInInteger();
//						maxScore += studentTestMarks.getMaxScore();				//Commented as Max score not being used for any further calculations
						
						checkStudentAttemptAndShowResultStatus(studentTestMarks.getSapid(), studentTestMarks.getTestId(), studentTestMarks.getTestName(), studentTestMarks.getAttempt(), 
																studentTestMarks.getShowResult(), studentTestMarks.getShowResultsToStudents(), studentFinalMarks, resultsNotLiveTestName);
					}
				}
				else {			//Student attempted IA of 100 marks are iterated and score of the recent test is returned
					for(StudentsTestDetailsExamBean studentTestMarks: attemptedHunderedMarksTestList) {
						if(StringUtils.isBlank(hunderedMarksTestEndDate)) {
							totalMarks = studentTestMarks.getScoreInInteger();
							hunderedMarksTestEndDate = studentTestMarks.getEndDate();
							
							checkStudentAttemptAndShowResultStatus(studentTestMarks.getSapid(), studentTestMarks.getTestId(), studentTestMarks.getTestName(), studentTestMarks.getAttempt(), 
																	studentTestMarks.getShowResult(), studentTestMarks.getShowResultsToStudents(), studentFinalMarks, resultsNotLiveTestName);
						}
						else {		//Multiple 100 mark tests attempted by student, the score of latest IA is considered
							LocalDateTime storedTestEndDate = convertStringDateToDateTime(hunderedMarksTestEndDate);
							LocalDateTime currentTestEndDate = convertStringDateToDateTime(studentTestMarks.getEndDate());
							
							if(currentTestEndDate.isAfter(storedTestEndDate)) {
								totalMarks = studentTestMarks.getScoreInInteger();
								hunderedMarksTestEndDate = studentTestMarks.getEndDate();
								
								checkStudentAttemptAndShowResultStatus(studentTestMarks.getSapid(), studentTestMarks.getTestId(), studentTestMarks.getTestName(), studentTestMarks.getAttempt(), 
																		studentTestMarks.getShowResult(), studentTestMarks.getShowResultsToStudents(), studentFinalMarks, resultsNotLiveTestName);
							}
						}
					}
				}
				
				if(resultsNotLiveTestName.size() > 0) {
					logger.error("Results not live of tests: {} for student: {}", resultsNotLiveTestName.toString(), studentFinalMarks.getSapid());
					studentFinalMarks.setFailReason("Result not live of tests: " + String.join(", ", resultsNotLiveTestName));			//ShowResult/ShowResultsToStudents flag not live
					studentFinalMarks.setStatus(ATTEMPT_STATUS_NOT_ATTEMPTED);
					unsuccessfulPassFail.add(studentFinalMarks);
					continue;
				}
				
				int totalMarksInt = (int) Math.round(totalMarks);
				studentFinalMarks.setIaScore(String.valueOf(totalMarksInt));
				
				//Commented as only first record from the test list was considered
//				StudentsTestDetailsExamBean bean  = iaMarks.get(0);
//				if(bean.getAttempt() == 1) {}
//				studentFinalMarks.setIaScore(""+ (int) Math.round(bean.getScoreInInteger()));
//				studentFinalMarks.setMax_score(""+bean.getMaxScore());
			}
			logger.info("Attempt status: {} of Student: {} and total marks obtained: {}", studentFinalMarks.getStatus(), studentFinalMarks.getSapid(), studentFinalMarks.getIaScore());
			
			studentFinalMarks.setTeeScore(null);
			studentFinalMarks.setGrade(null);
			studentFinalMarks.setPoints(null);
			logger.info(" PassFailLogic MAXScore Score = "+student.getMax_score());
			
			
			Integer total = Integer.parseInt( studentFinalMarks.getIaScore() );
			if(total >= student.getPassScore()) {
				studentFinalMarks.setIsPass("Y");
			}else {
				studentFinalMarks.setIsPass("N");
				studentFinalMarks.setFailReason("Total less than " +  student.getPassScore());
			}
			logger.info("IsPass = "+studentFinalMarks.getIsPass());
			finalListforPassFail.add(studentFinalMarks);
		}
	}
	
	/**
	 * IAs Applicable for a particular timebound are fetched and returned.
	 * @param timeboundId - TimeboundId for which the tests are to be fetched
	 * @return Map of testIds and their respective max scores
	 */
	private Map<Integer, Integer> getScheduledTestsForTimebound(String timeboundId) {
		List<Integer> sessionPlanIdList = examsAssessmentsDAO.getSessionPlanByTimebound(timeboundId);	//List of sessionPlanId as timeboundId not primary key in sessionPlan_timebound_mapping table
		return examsAssessmentsDAO.getApplicableTestBySessionPlan(sessionPlanIdList);
	}

	/**
	 * Converting Date passed as a String Object into LocalDateTime Object using a DateTimeFormatter.
	 * If an Exception is thrown, the Minimum LocalDateTime Object is returned.
	 * @param dateTime - dateTime as a String
	 * @return LocalDateTime of the passed dateTime parameter
	 */
	private LocalDateTime convertStringDateToDateTime(String dateTime) {
		try {
			return LocalDateTime.parse(dateTime, formatter);
		}
		catch(Exception ex) {
			return LocalDateTime.MIN;
		}
	}
	
	/**
	 * The Attempted Status of the student is checked and stored in passed PassFail bean.
	 * If the showResult or showResultsOfStudents flag of the student is not Y, the testName is stored in an errorList for futher processing.
	 * @param sapid - studentNo of the student to be checked 
	 * @param testId - ID of the applicable IA
	 * @param testName - name of the IA
	 * @param isAttempted - flag indicating if the student has attempted the IA
	 * @param showResult - flag indicating if the IA result is to be displayed to the student 
	 * @param showResultsToStudents - flag indicating if the IA result is to be displayed to all eligible students
	 * @param studentPassFailBean - Pass Fail Bean used to stored the attempt status
	 * @param testResultsNotLiveList - error List used to store testName of IAs whose result flag is not live
	 */
	private void checkStudentAttemptAndShowResultStatus(String sapid, Long testId, String testName, int isAttempted, String showResult, String showResultsToStudents, 
														EmbaPassFailBean studentPassFailBean, List<String> testResultsNotLiveList) {
		if(isAttempted == 1)
			studentPassFailBean.setStatus(ATTEMPT_STATUS_ATTEMPTED);
		
		if(!"Y".equals(showResult) || !"Y".equals(showResultsToStudents)) {
			logger.error("Results not live for test: {} with flag showResultsToStudents: {} for student: {} with flag showResult: {}", 
						testId, showResultsToStudents, sapid, showResult);
			testResultsNotLiveList.add(testName);
		}
	}
}
