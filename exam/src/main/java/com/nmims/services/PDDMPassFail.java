package com.nmims.services;

import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.nmims.beans.EmbaPassFailBean;
import com.nmims.beans.StudentsTestDetailsExamBean;
import com.nmims.beans.TEEResultBean;
import com.nmims.daos.ExamsAssessmentsDAO;

@Service
public class PDDMPassFail {
	
	@Autowired
	private ExamsAssessmentsDAO examsAssessmentsDAO;
	
	private static final Logger logger = LoggerFactory.getLogger("pddm-passfail-process");
	
	public void processPassFail(TEEResultBean resultBean, List<EmbaPassFailBean> finalListforPassFail,
			List<TEEResultBean> studentsListEligibleForPassFail,String loggedInUser,
			List<EmbaPassFailBean> unsuccessfulPassFail  ){
		logger.info("loggedInUser "+loggedInUser+" studentsListEligibleForPassFail size "+studentsListEligibleForPassFail.size());
		for(TEEResultBean student : studentsListEligibleForPassFail) {
	
			EmbaPassFailBean studentFinalMarks = new EmbaPassFailBean();
			
			studentFinalMarks.setTimeboundId(student.getTimebound_id());
			studentFinalMarks.setAssessmentName(student.getAssessmentName());
			studentFinalMarks.setSchedule_id(student.getSchedule_id());
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
			boolean iaAttempted = false; 
			
			if(70 == Integer.parseInt(studentFinalMarks.getMax_score())) {
			List<StudentsTestDetailsExamBean> iaMarks = examsAssessmentsDAO.getIAScoresForStudentSubject(student.getSapid(),student.getTimebound_id());  
			logger.info(" iaMarks size "+iaMarks.size()+" sapid "+student.getSapid()+" Timebound_id "+student.getTimebound_id());
			
			if(iaMarks.size() > 0) { 
				boolean calculateIA =true;
				iaAttempted = true;
				for(StudentsTestDetailsExamBean test : iaMarks) {
					if(test.getShowResult().equalsIgnoreCase("N") || test.getShowResultsToStudents().equalsIgnoreCase("N") ) {
						logger.info("Internal Assessment result is not live for record Sapid "+student.getSapid()+" Timebound_id "+student.getTimebound_id()+" TestId "+test.getTestId());
						calculateIA=false;
					}
				}
			
				if(calculateIA) {
					Integer iaScores= iaMarks.stream() .mapToInt(x -> (int) Math.round(x.getScore())).sum();
					studentFinalMarks.setIaScore(""+iaScores);
			    }else {
			    	studentFinalMarks.setFailReason("Internal Assessment result is not live");
					unsuccessfulPassFail.add(studentFinalMarks);
					studentFinalMarks.setStatus("NA");
			    	continue;
			    }
			}else {
				iaAttempted = false;
				studentFinalMarks.setIaScore("0");
			}
				
			}	
			
			isPassCheck(studentFinalMarks, iaAttempted);//check is student pass or fail
			if(!studentFinalMarks.getStatus().equalsIgnoreCase("NA")) {
				finalListforPassFail.add(studentFinalMarks);
			}
		}
		
		logger.info("loggedInUser "+loggedInUser+" unsuccessfulPassFail "+unsuccessfulPassFail.size()+" finalListforPassFail size "+finalListforPassFail.size());
	
	}
	
	
	private void isPassCheck(EmbaPassFailBean studentFinalMarks, boolean iaAttempted) {
		switch(studentFinalMarks.getStatus()) {
				case "RIA" :
					studentFinalMarks.setFailReason("Marked RIA in TEE");
					studentFinalMarks.setIsPass("N");
					logger.info(" Marked RIA in TEE sapid "+studentFinalMarks.getSapid()+" Timebound_id "+studentFinalMarks.getTimeboundId());
					break;
				case "NV" :
					studentFinalMarks.setFailReason("Marked NV in TEE");
					studentFinalMarks.setIsPass("N");
					logger.info(" Marked NV in TEE sapid "+studentFinalMarks.getSapid()+" Timebound_id "+studentFinalMarks.getTimeboundId());
					break;
				case "AB" :
					studentFinalMarks.setFailReason("Marked Absent in TEE");
					studentFinalMarks.setIsPass("N");
					logger.info(" Marked Absent in TEE sapid "+studentFinalMarks.getSapid()+" Timebound_id "+studentFinalMarks.getTimeboundId());
					break;
				case "Attempted" :
					logger.info(" Attempted sapid "+studentFinalMarks.getSapid()+" TimeboundId "+studentFinalMarks.getTimeboundId()+" IaScore "+studentFinalMarks.getIaScore());
					if(70 == Integer.parseInt(studentFinalMarks.getMax_score())) {
						regularExam(studentFinalMarks,iaAttempted);
					}else if(100 == Integer.parseInt(studentFinalMarks.getMax_score())) {
						reExam(studentFinalMarks);
					}
					break;
				default :
					logger.info(" Status "+studentFinalMarks.getStatus()+" Not Found sapid "+studentFinalMarks.getSapid()+" TimeboundId "+studentFinalMarks.getTimeboundId());
					break;
		  } 
		
	}
	
	private void regularExam(EmbaPassFailBean studentFinalMarks, boolean iaAttempted) {
		if (iaAttempted) {
			int total = Integer.parseInt(studentFinalMarks.getIaScore()) + studentFinalMarks.getTeeScore(); ;
			
			logger.info(" Attempted sapid "+studentFinalMarks.getSapid()+" TimeboundId "+studentFinalMarks.getTimeboundId()+" total "+total);
			if(total >= 50) {
				studentFinalMarks.setIsPass("Y");
			}else {
				studentFinalMarks.setIsPass("N");
				studentFinalMarks.setFailReason("Total less than 50");
			}
		}else {
			logger.info(" Marked Absent in Internal Assessment sapid "+studentFinalMarks.getSapid()+" TimeboundId "+studentFinalMarks.getTimeboundId()+" IaScore "+studentFinalMarks.getIaScore());
			studentFinalMarks.setIsPass("N");
			studentFinalMarks.setFailReason("Marked Absent in Internal Assessment");
		}
	}
	
	private void reExam(EmbaPassFailBean studentFinalMarks) {
		int total = studentFinalMarks.getTeeScore(); 
		logger.info(" Attempted sapid "+studentFinalMarks.getSapid()+" TimeboundId "+studentFinalMarks.getTimeboundId()+" total "+total);
		if(total >= 50) {
			studentFinalMarks.setIsPass("Y");
		}else {
			studentFinalMarks.setIsPass("N");
			studentFinalMarks.setFailReason("Total less than 50");
		}
	}
	
	
}
