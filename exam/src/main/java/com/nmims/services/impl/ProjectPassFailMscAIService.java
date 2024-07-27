package com.nmims.services.impl;

import java.util.ArrayList;

import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Service;

import com.nmims.beans.EmbaPassFailBean;
import com.nmims.beans.StudentsTestDetailsExamBean;
import com.nmims.beans.TEEResultBean;
import com.nmims.daos.ExamsAssessmentsDAO;

@Service("projectPassFailMscAIService")
public class ProjectPassFailMscAIService {

	public void projectPassFailLogicForMscAI(TEEResultBean resultBean, ExamsAssessmentsDAO examsAssessmentsDAO,
			ArrayList<EmbaPassFailBean> finalListforPassFail, ArrayList<TEEResultBean> studentsListEligibleForPassFail,
			String loggedInUser, ArrayList<EmbaPassFailBean> unsuccessfulPassFail) {
		
		for(TEEResultBean student : studentsListEligibleForPassFail) {
			
			EmbaPassFailBean studentFinalMarks = new EmbaPassFailBean();
			
			studentFinalMarks.setTimeboundId(student.getTimebound_id());
			studentFinalMarks.setSchedule_id(student.getSchedule_id());;
			studentFinalMarks.setSapid(student.getSapid());
			studentFinalMarks.setCreatedBy(loggedInUser);
			studentFinalMarks.setLastModifiedBy(loggedInUser);
			studentFinalMarks.setMax_score(student.getMax_score());
			studentFinalMarks.setStatus(student.getStatus());
			studentFinalMarks.setProcessed(student.getProcessed());
			studentFinalMarks.setPrgm_sem_subj_id(student.getPrgm_sem_subj_id());
			studentFinalMarks.setTeeScore(student.getScore());
			studentFinalMarks.setGrade(null);
			studentFinalMarks.setPoints(null);
			 
			if(student.getStatus().equalsIgnoreCase("Not Attempted")) {
				studentFinalMarks.setFailReason("Status is Not Attempted");
				unsuccessfulPassFail.add(studentFinalMarks);
				studentFinalMarks.setStatus("NA");
				continue;
			}
			
			// if max score is 50 then
			// get students IA marks for single subject
			// IA marks obtained if showResults to students and showResults flags for all applicable IA is set to Y and IA is not of generic module.
			if(50 == Integer.parseInt(student.getMax_score())) {
				ArrayList<StudentsTestDetailsExamBean> iaMarks = examsAssessmentsDAO.getIAScoresForStudentSubject(student.getSapid(),student.getTimebound_id());  
				boolean calculateIA =true;
				if(iaMarks.size() > 0) { 
					for(StudentsTestDetailsExamBean test : iaMarks) {
						if(test.getShowResult().equalsIgnoreCase("N") || test.getShowResultsToStudents().equalsIgnoreCase("N") ) {
							studentFinalMarks.setFailReason("ShowResult/ShowResultsToStudents flag not live");
							unsuccessfulPassFail.add(studentFinalMarks);
							studentFinalMarks.setStatus("NA");
							calculateIA=false;
						}
					}
				}
				if(calculateIA) {
					int iaScore=0;
					double iaScoreInDecimal=0.0;
					for(StudentsTestDetailsExamBean test : iaMarks) {
						if(test.getShowResult().equalsIgnoreCase("N") || test.getShowResultsToStudents().equalsIgnoreCase("N") ) {
							studentFinalMarks.setFailReason("ShowResult/ShowResultsToStudents flag not live");
							unsuccessfulPassFail.add(studentFinalMarks);
							studentFinalMarks.setStatus("NA");
							break;
						}else {
							iaScoreInDecimal = iaScoreInDecimal + test.getScore();
						}
					}
					iaScore = roundDoubleToInt(iaScoreInDecimal);
					studentFinalMarks.setIaScore("" + iaScore);

				}
				
			}//if
			
			isPassCheck(studentFinalMarks);//check is student pass or fail
			if(!studentFinalMarks.getStatus().equalsIgnoreCase("NA")) {
				finalListforPassFail.add(studentFinalMarks);
			}
		}
		
	}
	
	private void isPassCheck(EmbaPassFailBean studentFinalMarks) {
		
		if(studentFinalMarks.getStatus().equalsIgnoreCase("NA")) {
		}
		else if(studentFinalMarks.getStatus().equalsIgnoreCase("RIA") || studentFinalMarks.getStatus().equalsIgnoreCase("NV") 
				|| studentFinalMarks.getStatus().equalsIgnoreCase("AB") ) {
			
				studentFinalMarks.setIsPass("N");
			/*if(Integer.parseInt(studentFinalMarks.getMax_score()) == 50) {
			 studentFinalMarks.setFailReason("Total less than 50");
			}*/
		}else {
			int maxScore = Integer.parseInt(studentFinalMarks.getMax_score());
			if(50==maxScore) {
				int teeScore = studentFinalMarks.getTeeScore();
				int iaScore = parseIfNumericScore(studentFinalMarks.getIaScore());
				int total = teeScore + iaScore ;
				if(total >= 50) {
					studentFinalMarks.setIsPass("Y");
				}else {
					studentFinalMarks.setIsPass("N");
					studentFinalMarks.setFailReason("Total less than 50");
				}
			}
		}
	}//isPassCheck()
	
	private int parseIfNumericScore(String score) {
		if (!StringUtils.isBlank(score) && StringUtils.isNumeric(score)) {
			return Integer.parseInt(score);
		}
		return 0;
	}//parseIfNumericScore()
	
	private int roundDoubleToInt(double doubleValue) {
		return (int) Math.round(doubleValue);
	}//roundDoubleToInt()

}
