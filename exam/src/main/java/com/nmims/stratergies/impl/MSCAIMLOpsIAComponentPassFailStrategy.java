package com.nmims.stratergies.impl;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.nmims.beans.EmbaPassFailBean;
import com.nmims.beans.StudentsTestDetailsExamBean;
import com.nmims.beans.TEEResultBean;
import com.nmims.daos.ExamsAssessmentsDAO;
import com.nmims.stratergies.IAComponentPassFailInterface;

/**
 * 
 * @author Siddheshwar_Khanse
 *
 */
@Service("MSCAIMLOpsIAComponentPassFailStrategy")
public class MSCAIMLOpsIAComponentPassFailStrategy implements IAComponentPassFailInterface{
	
	private static final Logger logger = LoggerFactory.getLogger(MSCAIMLOpsIAComponentPassFailStrategy.class);
	
	@Autowired
	private ExamsAssessmentsDAO examsAssessmentsDAO;

	@Override
	public void searchPassFail(TEEResultBean resultBean, List<EmbaPassFailBean> finalListforPassFail,
			List<TEEResultBean> studentsListEligibleForPassFail, String loggedInUser,
			List<EmbaPassFailBean> unsuccessfulPassFail) {

		for(TEEResultBean student : studentsListEligibleForPassFail) {
			EmbaPassFailBean studentFinalMarks = new EmbaPassFailBean();
			logger.info("sapid= "+student.getSapid()+ " timebound = "+student.getTimebound_id());	
			
			studentFinalMarks.setTimeboundId(student.getTimebound_id());
			studentFinalMarks.setSchedule_id(null);
			studentFinalMarks.setSapid(student.getSapid());
			studentFinalMarks.setPrgm_sem_subj_id(student.getPrgm_sem_subj_id());
			studentFinalMarks.setCreatedBy(loggedInUser);
			studentFinalMarks.setLastModifiedBy(loggedInUser);
			List<StudentsTestDetailsExamBean> iaMarks = examsAssessmentsDAO.getUniqueIAScoresForStudentSubject(student.getSapid(),student.getTimebound_id() ); 
			double iaScoreInDecimal = 0.0;
			int attemptedIA = 0;
			double componentBMarks=0.0;
			
			for (StudentsTestDetailsExamBean test : iaMarks) {
				if (test.getShowResult().equalsIgnoreCase("N") || test.getShowResultsToStudents().equalsIgnoreCase("N")) {
					studentFinalMarks.setFailReason("ShowResult/ShowResultsToStudents flag not live");
					unsuccessfulPassFail.add(studentFinalMarks);
					studentFinalMarks.setStatus("NA");
					break;
				} else {
					if(test.getTestName().contains("Solution Component")) {
						componentBMarks = test.getScore();
					}
					
					attemptedIA++;
					iaScoreInDecimal = iaScoreInDecimal + test.getScore();
				}
			}
			if (!"NA".equals(studentFinalMarks.getStatus())) {
				logger.info(" PassFailLogic MSC AI ML Ops Score = " + iaScoreInDecimal);
				studentFinalMarks.setIaScore("" + (int) Math.round(iaScoreInDecimal));
				studentFinalMarks.setMax_score("" + iaMarks.get(0).getMaxScore());
				studentFinalMarks.setAttemptedIA(attemptedIA);
				studentFinalMarks.setStatus("Attempted");

				studentFinalMarks.setTeeScore(null);
				studentFinalMarks.setGrade(null);
				studentFinalMarks.setPoints(null);
				logger.info(" PassFailLogic MAXScore Score = " + student.getMax_score());

				//Integer total = Integer.parseInt(studentFinalMarks.getIaScore());
				if (attemptedIA >= 2 && componentBMarks >= 20.0) {
					studentFinalMarks.setIsPass("Y");
				} else {
					studentFinalMarks.setIsPass("N");
					studentFinalMarks.setFailReason("Solution Component marks less than " + 20 + " OR Required no.of IA not attempted.");
				}
				logger.info("IsPass = " + studentFinalMarks.getIsPass());
				finalListforPassFail.add(studentFinalMarks);
			}
		}
		
	}
	
}
