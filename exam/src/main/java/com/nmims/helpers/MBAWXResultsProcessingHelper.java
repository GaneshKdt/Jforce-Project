package com.nmims.helpers;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.nmims.beans.MBAWXExamResultForSubject;
import com.nmims.beans.StudentExamBean;
import com.nmims.beans.StudentsTestDetailsExamBean;
import com.nmims.daos.TestDAO;
import com.nmims.daos.UpgradResultProcessingDao;

@Component
public class MBAWXResultsProcessingHelper {
	

//	private String IA_MAX_SCORE = "70";
	private int TEE_PASS_SCORE = 12;
	private int RESIT_PASS_SCORE = 50;
//	private String RESIT_EXAM_TEE_SCORE = "100";
	private String TOTAL_SCORE_MAX = "100";
	
	
	@Autowired
	TestDAO testDAO;

	@Autowired
	private UpgradResultProcessingDao upgradResultProcessingDao;
	
	private static final List<Integer> mscAIMLOpsPssIdListBestOf2  = Arrays.asList(1965, 1980, 1981, 1982, 1984, 1985, 1986, 1983, 1989 );
	private static final List<Integer> mscAIMLOpsPssIdListBestOf3  = Arrays.asList(1961, 1962, 1963, 1964, 1966, 1969, 1975, 1987);
	private static final List<Integer> mscAIPssIdListBestOf3  = Arrays.asList( 2481, 2413, 2405,
																			   2499, 
																			   2494,
																			   2505,
																			   2500
																			   //2483, 2557, 2407
																			   );
	
	
	public List<StudentsTestDetailsExamBean> getIAForSubjectWithBestOf7Marked(Long timeboundId, String sapid) {
		// This function returns all IA for sapid, timeboundId marking the subjects that are selected for Best Of 7
		
		// TODO : Add logic to catch Copy Case, etc and return the data accordingly.
		
		// never returns null.
		List<StudentsTestDetailsExamBean> attemptedTestsBySapidNSubject = 
				testDAO.getApplicableFinishedTestsWithAttemptDetailsBySapidNSubject(timeboundId, sapid);

// START Commented by Abhay For remove copycase check for bestof logic
//		List<StudentsTestDetailsBean> listOfResultsWithNoCC = new ArrayList<StudentsTestDetailsBean>();
//		for (StudentsTestDetailsBean studentsTestDetailsBean : attemptedTestsBySapidNSubject) {
//			if(!"CopyCase".equals(studentsTestDetailsBean.getAttemptStatus())) {
//				listOfResultsWithNoCC.add(studentsTestDetailsBean);
//			}
//		}
//	END	Commented by Abhay For remove copycase check for bestof logic

		StudentExamBean student = testDAO.getSingleStudentsData(sapid);
		int numberOfSubjectsForBestOf = 7;
		if(student != null && "M.Sc. (AI & ML Ops)".equals(student.getProgram())) {
			Integer pssid = testDAO.getPssIdFromTimeboundId(timeboundId, sapid);
			
			if(mscAIMLOpsPssIdListBestOf2.contains(pssid) || timeboundId == 1090 || timeboundId==1099 || timeboundId==1302 || timeboundId==1343) {
				numberOfSubjectsForBestOf = 2;
			} else if(mscAIMLOpsPssIdListBestOf3.contains(pssid) ) {
				numberOfSubjectsForBestOf = 3;	
			} else {
				numberOfSubjectsForBestOf = 4;
			}
		} else if(student != null && (
				   "154".equals(student.getConsumerProgramStructureId()) 
				|| "155".equals(student.getConsumerProgramStructureId())
				|| "158".equals(student.getConsumerProgramStructureId()) 
				)) {
			
			Integer pssid = testDAO.getPssIdFromTimeboundId(timeboundId, sapid);
				
			if(mscAIPssIdListBestOf3.contains(pssid) || timeboundId == 973  ) {
				numberOfSubjectsForBestOf = 3;	
			} else {
				numberOfSubjectsForBestOf = 2;
			}
		} else if(student != null && ( "160".equals(student.getConsumerProgramStructureId()) ) ) {
			numberOfSubjectsForBestOf = 5;
		}
		
		if (attemptedTestsBySapidNSubject.size() > 0) {
			Map<Long, StudentsTestDetailsExamBean> selectedForBestOf7Calculation = new HashMap<>();
			
			List<StudentsTestDetailsExamBean> selectedAttempts = attemptedTestsBySapidNSubject;
			
			if (attemptedTestsBySapidNSubject.size() > numberOfSubjectsForBestOf) {
				// Sort results in descending order by score
				List<StudentsTestDetailsExamBean> descScoreSortedList = sortTestResultsByScoreDesc(attemptedTestsBySapidNSubject);

				// Get the 7 subjects with the highest score
				selectedAttempts = descScoreSortedList.subList(0, numberOfSubjectsForBestOf);
			}
			
			// Add to a ma
			for (StudentsTestDetailsExamBean b : selectedAttempts) {
				selectedForBestOf7Calculation.put(b.getId(), b);
			}

			for (StudentsTestDetailsExamBean a : attemptedTestsBySapidNSubject) {
				if (selectedForBestOf7Calculation.containsKey(a.getId())) {
					a.setScoreSelectedForBestOf7(true);
				}
			}
		}
		return attemptedTestsBySapidNSubject;
	}
	
	public List<StudentsTestDetailsExamBean> getMbaXIAForSubjectWithBestOf7Marked(Long timeboundId, String sapid) {
		// This function returns all IA for sapid, timeboundId marking the subjects that are selected for Best Of 7
		
		// TODO : Add logic to catch Copy Case, etc and return the data accordingly.
		
		// never returns null.
		Integer pssId = upgradResultProcessingDao.getPssIdByTimeboundId(timeboundId); // added by Abhay For BOP subject
		List<StudentsTestDetailsExamBean> attemptedTestsBySapidNSubject = 
				testDAO.getMbaXApplicableFinishedTestsWithAttemptDetailsBySapidNSubject(timeboundId, sapid);


		List<StudentsTestDetailsExamBean> listOfResultsWithNoCC = new ArrayList<StudentsTestDetailsExamBean>();
		for (StudentsTestDetailsExamBean studentsTestDetailsBean : attemptedTestsBySapidNSubject) {
			if(!"CopyCase".equals(studentsTestDetailsBean.getAttemptStatus())) {
				listOfResultsWithNoCC.add(studentsTestDetailsBean);
			}
		}
		
		if (listOfResultsWithNoCC.size() > 0) {
			Map<Long, StudentsTestDetailsExamBean> selectedForBestOf7Calculation = new HashMap<>();
			
			List<StudentsTestDetailsExamBean> selectedAttempts = listOfResultsWithNoCC;
			

			// Sort results in descending order by score
			List<StudentsTestDetailsExamBean> descScoreSortedList = sortTestResultsByScoreDesc(listOfResultsWithNoCC);
			
			if (listOfResultsWithNoCC.size() > 4) {

				// Get the 7 subjects with the highest score
				selectedAttempts = descScoreSortedList.subList(0, 4);
			}
			
			if(pssId == 1789) { // added by Abhay For BOP subject
				// Get the 1 subjects with the highest score For BOP subject added by Abhay
				selectedAttempts = descScoreSortedList.subList(0, 1);
			}
			
			// Add to a ma
			for (StudentsTestDetailsExamBean b : selectedAttempts) {
				selectedForBestOf7Calculation.put(b.getId(), b);
			}

			for (StudentsTestDetailsExamBean a : listOfResultsWithNoCC) {
				if (selectedForBestOf7Calculation.containsKey(a.getId())) {
					a.setScoreSelectedForBestOf7(true);
				}
			}
		}
		return attemptedTestsBySapidNSubject;
	}
	
	private List<StudentsTestDetailsExamBean> sortTestResultsByScoreDesc(List<StudentsTestDetailsExamBean> attempts) {

		List<StudentsTestDetailsExamBean> descScoreSortedList = new LinkedList<>();
		descScoreSortedList.addAll(attempts);

		Comparator<StudentsTestDetailsExamBean> compareByScore = new Comparator<StudentsTestDetailsExamBean>() {
			@Override
			public int compare(StudentsTestDetailsExamBean o1, StudentsTestDetailsExamBean o2) {
				return o1.getScoreInInteger().compareTo(o2.getScoreInInteger());
			}
		};

		// Sort all subjects in desc order of score
		Collections.sort(descScoreSortedList, compareByScore.reversed());

		return descScoreSortedList;
	}
	
	public void setGraceAndTotal(MBAWXExamResultForSubject subjectResult) {
		

		// parse ia, tee and max score
		int teeScore = 0; 
		int maxScore = 0;
		int iaScore = 0;
		
		if(subjectResult.getResitScore() != null) {
			teeScore = parseIfNumericScore(subjectResult.getResitScore());
			maxScore = parseIfNumericScore(subjectResult.getResitScoreMax());
		} else {
			teeScore = parseIfNumericScore(subjectResult.getTeeScore());
			maxScore = parseIfNumericScore(subjectResult.getTeeScoreMax());
			iaScore = parseIfNumericScore(subjectResult.getIaScore());
		}
		
		boolean graceEligible = checkIfEligibleForGrace(teeScore, maxScore, iaScore);
		int graceMarks = 0;
		if(graceEligible) {
			graceMarks = calculateGraceMarks(teeScore, maxScore, iaScore);
			if(graceMarks > 0) {
				subjectResult.setGraceMarks(Integer.toString(graceMarks));
				// Add grace to TEE score
				teeScore = teeScore + graceMarks;
				subjectResult.setTeeScore(Integer.toString(teeScore));
			}
		}

		int total = iaScore + teeScore;
		subjectResult.setTotal(Integer.toString(total));
		subjectResult.setTotalMax(TOTAL_SCORE_MAX);
		// set the isPass Flag end
	}
	
	public void setIsPass(MBAWXExamResultForSubject subjectResult) {
		setIsPassForResitAndTee(subjectResult);
		// parse ia, tee and max score
		int teeScore = 0; 
		int maxScore = 0;
		int iaScore = 0;
		
		if(subjectResult.getResitScore() != null) {
			teeScore = parseIfNumericScore(subjectResult.getResitScore());
			maxScore = parseIfNumericScore(subjectResult.getResitScoreMax());
		} else {
			teeScore = parseIfNumericScore(subjectResult.getTeeScore());
			maxScore = parseIfNumericScore(subjectResult.getTeeScoreMax());
			iaScore = parseIfNumericScore(subjectResult.getIaScore());
		}
		
		// set the isPass Flag
		String isPass = (30 == maxScore && teeScore < 12) || (teeScore + iaScore) < 50 ? "N" : "Y";
		
		subjectResult.setIsPass(isPass);
	
	}

	public void setIsPassForResitAndTee(MBAWXExamResultForSubject subjectResult) {
		if(subjectResult.getTeeScore() != null) {
			int teeScore = parseIfNumericScore(subjectResult.getTeeScore());
			subjectResult.setTeeIsPass(teeScore >= TEE_PASS_SCORE ? "Y" : "N");
		}
		if(subjectResult.getResitScore() != null) {
			int resitScore = parseIfNumericScore(subjectResult.getResitScore());
			subjectResult.setResitIsPass(resitScore >= RESIT_PASS_SCORE ? "Y" : "N");
		}
	}
	
	private int calculateGraceMarks(int teeScore, int maxScore, int iaScore) {
		if(30 == maxScore) {
			int totalMarks = iaScore + teeScore;
			//breaks at 38 in assignment and 10 in TEE
			if(totalMarks > 50 && teeScore == 10){
				return 2;
			}
			if(totalMarks > 50 && teeScore == 11){
				return 1;
			}
			if(totalMarks == 48 ){
				return 2;
			}
			if(totalMarks == 49 ){
				return 1;
			}
		}
		if(100 == maxScore) {
			int totalMarks = teeScore;
			//breaks at 38 in assignment and 10 in TEE
			if(totalMarks == 48 ){
				return 2;
			}
			if(totalMarks == 49 ){
				return 1;
			}
		}
		return 0;
	}

	private boolean checkIfEligibleForGrace (int teeScore, int maxScore, int iaScore){
		int totalMarks=0;
		if (30 == maxScore) {
			totalMarks = iaScore + teeScore;
			if ((totalMarks > 47 && (teeScore >= 10 && teeScore < 12)) || (teeScore > 12 && (totalMarks > 47 && totalMarks < 50 )) ) {
				return true;
			} else {
				return false;
			}
		} else if(100 == maxScore) {
			totalMarks = teeScore;
			if (totalMarks > 47 && totalMarks < 50  ) {
				return true;
			} else {
				return false;
			} 
		}
		return false;  
	}
	
	private int parseIfNumericScore(String score) {
		if (!StringUtils.isBlank(score) && StringUtils.isNumeric(score)) {
			return Integer.parseInt(score);
		}
		return 0;
	}
}
