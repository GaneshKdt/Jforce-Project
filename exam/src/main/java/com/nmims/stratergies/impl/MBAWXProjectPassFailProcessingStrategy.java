package com.nmims.stratergies.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import com.nmims.beans.EmbaPassFailBean;
import com.nmims.beans.StudentsTestDetailsExamBean;
import com.nmims.beans.TEEResultBean;
import com.nmims.daos.ExamsAssessmentsDAO;
import com.nmims.services.ITimeboundProjectPassFailService;
import com.nmims.stratergies.ITimeboundProjectPassFailProcessingStrategy;

/**
 * 
 * @author Siddheshwar_Khanse
 *
 */
@Service("mbawxProjectPassFailProcessingStrategy")
public class MBAWXProjectPassFailProcessingStrategy implements ITimeboundProjectPassFailProcessingStrategy{

	public static final Logger timeboundProjectMarksUploadLogger = LoggerFactory.getLogger("timeboundProjectMarksUpload");
	
	@Autowired
	@Qualifier("mbawxProjectPassFailService")
	private ITimeboundProjectPassFailService mbawxProjectPassFailService ;
	
	@Autowired
	private ExamsAssessmentsDAO examsAssessmentsDAO;
	
	private static final int SIMULATION_PASSING_PERCENTAGE=40;
	private static final int COMPXM_PASSING_PERCENTAGE=40;
	private static final int OVERALL_MAX_SCORE=100;
	private static final int OVERALL_PASSING_PERCENTAGE=50;
	private static final int OVERALL_PASSING_SCORE = (OVERALL_MAX_SCORE*OVERALL_PASSING_PERCENTAGE)/100;
	
	@Override
	public Map<String, List<EmbaPassFailBean>> processTimeboundStudentsProjectPassFail(List<TEEResultBean> eligibleStudentsForProjectPassFail, String loggedInUser) {
		timeboundProjectMarksUploadLogger.info("MBAWXProjectPassFailProcessingStrategy.processTimeboundStudentsProjectPassFail() - START");
		Map<String, List<EmbaPassFailBean>> resultProcessedMap = new HashMap<String, List<EmbaPassFailBean>>();
		List<EmbaPassFailBean> finalListforPassFail = new ArrayList<EmbaPassFailBean>();
		List<EmbaPassFailBean> unsuccessfulPassFail = new ArrayList<EmbaPassFailBean>();

		//Step 1] Iterate eligible students list and prepare final pass fail process list.
		for(TEEResultBean student : eligibleStudentsForProjectPassFail) {
			//A] Populate pass fail bean.
			EmbaPassFailBean studentFinalMarks = this.populatePassFailBean(student,loggedInUser);
			try {
				//B] Calculate IA Marks for a subject of a student.
				this.calculateIAScoresForStudentSubject(unsuccessfulPassFail, studentFinalMarks);

				//C] Check student is pass or fail in a particular subject.
				this.isPassCheck(studentFinalMarks);
				
				if(StringUtils.isEmpty(studentFinalMarks.getStatus()) ||  (!studentFinalMarks.getStatus().equalsIgnoreCase("NA")) ) {
					finalListforPassFail.add(studentFinalMarks);
				}
			}catch (Exception e) {
				timeboundProjectMarksUploadLogger.error("Exception occured while processing '"+student.getSapid()+"' Error Message:"+e.getStackTrace());
				studentFinalMarks.setFailReason("Exception occured. Error Message:"+e.getMessage());
				unsuccessfulPassFail.add(studentFinalMarks);
			}
		}//for loop
		
		//Add successfully processed and failed to process project pass fail result list to the map.
		resultProcessedMap.put("SuccessedList", finalListforPassFail);
		resultProcessedMap.put("FailedList", unsuccessfulPassFail);

		timeboundProjectMarksUploadLogger.info("MBAWXProjectPassFailProcessingStrategy.processTimeboundStudentsProjectPassFail() - END");
		//return result map
		return resultProcessedMap;
	}//processTimeboundStudentsProjectPassFail(-)
	
	//Copy TEEResultBean data to the EmbaPassFailBean
	private EmbaPassFailBean populatePassFailBean(TEEResultBean resultBean, String loggedInUser) {
		EmbaPassFailBean passFailBean = new EmbaPassFailBean();
		
		passFailBean.setTimeboundId(resultBean.getTimebound_id());
		passFailBean.setSapid(resultBean.getSapid());
		passFailBean.setSimulation_max_score(resultBean.getSimulation_max_score());
		passFailBean.setCompXM_max_score(resultBean.getCompXM_max_score());
		passFailBean.setProcessed(resultBean.getProcessed());
		passFailBean.setPrgm_sem_subj_id(resultBean.getPrgm_sem_subj_id());
		passFailBean.setSimulation_score(resultBean.getSimulation_score());
		passFailBean.setCompXM_score(resultBean.getCompXM_score());
		passFailBean.setTeeScore((Integer)Math.round(resultBean.getSimulation_score()+resultBean.getCompXM_score()));
		passFailBean.setSimulation_status(resultBean.getSimulation_status());
		passFailBean.setCompXM_status(resultBean.getCompXM_status());
		passFailBean.setGrade(null);
		passFailBean.setPoints(null);
		passFailBean.setCreatedBy(loggedInUser);
		passFailBean.setLastModifiedBy(loggedInUser);
		
		//return pass-fail bean
		return passFailBean;
	}
	
	//Calculate the student unique IA marks of a subject.
	private void calculateIAScoresForStudentSubject(List<EmbaPassFailBean> unsuccessfulPassFail, EmbaPassFailBean studentFinalMarks) {
		//Get unique IA marks for given subject for a student.
		List<StudentsTestDetailsExamBean> iaMarks = examsAssessmentsDAO.getUniqueIAScoresForStudentSubject(studentFinalMarks.getSapid(),studentFinalMarks.getTimeboundId());
		
		boolean calculateIA = true;
		int iaScore = 0;
		double iaScoreInDecimal = 0.0;
		int attemptedIA = 0;

		// Iterate list of IA's
		for (StudentsTestDetailsExamBean test : iaMarks) {
			if (test.getShowResult().equalsIgnoreCase("N") || test.getShowResultsToStudents().equalsIgnoreCase("N")) {
				studentFinalMarks.setFailReason("ShowResult/ShowResultsToStudents flag not live");
				unsuccessfulPassFail.add(studentFinalMarks);
				studentFinalMarks.setStatus("NA");
				calculateIA = false;
				continue;
			} else {
				// Increment the calculated IA's attempt to find out is student attempted
				// required no of IA's
				attemptedIA++;
				// Calculate sum of IA score
				iaScoreInDecimal = iaScoreInDecimal + test.getScore();
			}
		} // for loop

		// Set calculated IA marks attempt to bean
		studentFinalMarks.setAttemptedIA(attemptedIA);
		// Round up the floating IA marks to the integer marks. e.g 12.5 -> 13
		iaScore = (int) Math.round((iaScoreInDecimal));

		// If IA marks calculated successfully set it to the bean.
		if (calculateIA)
			studentFinalMarks.setIaScore("" + iaScore);
		
	}//calculateIAScoresForStudentSubject(-,-)
	
	/* Check the student is passed or failed based on the below criteria.
	 * A] The student has to pass in all component[IA, Simulation, CompXM] separately.
	 * 	a] The student required to attempt required no of IA's i.e 1 then only will be considered as pass otherwise fail.
	 *  b] The student has to attempt the each component[Simulation and CompXM] and score 40% of MAX_SCORE[SIM=30,CXM=50] then only will be considered as pass otherwise fail.
	 * B] The student has to get 50% overall score as [IA score + Simulation score + CompXM score] out of 100[IA=20+SIM=30+CXM=50] then only will be considered as pass otherwise fail.
	 */
	private void isPassCheck(EmbaPassFailBean studentFinalMarks) {
		if((!StringUtils.isEmpty(studentFinalMarks.getStatus())) &&  (studentFinalMarks.getStatus().equalsIgnoreCase("NA")) ) {
		}
		else {
			boolean isSimulationPassed=false;
			boolean isCompXMPassed=false;
			boolean isPassedInIA=false;
			boolean isOverallPassed=false;
			String failReason="";
			int SIMULATION_PASSING_SCORE=(studentFinalMarks.getSimulation_max_score()*SIMULATION_PASSING_PERCENTAGE)/100;
			int COMPXM_PASSING_SCORE=(studentFinalMarks.getCompXM_max_score()*COMPXM_PASSING_PERCENTAGE)/100;
			
			//Simulation component passing criteria - student has to attempt simulation and should get 40% of SIMULATION_MAX_SCORE[30] 
			if(studentFinalMarks.getSimulation_status().equalsIgnoreCase("Not Attempted"))
				failReason="Simulation is not attempted.";
			else if(studentFinalMarks.getSimulation_score() >= 0) {
				isSimulationPassed=true;
			}else
				failReason=failReason+" Simulation score is less than "+SIMULATION_PASSING_SCORE;
			
			//CompXM component passing criteria - student has to attempt compXM and should get 40% of COMPXM_MAX_SCORE[50] 
			if(studentFinalMarks.getCompXM_status().equalsIgnoreCase("Not Attempted"))
				failReason=failReason+" CompXM is not attempted.";
			else if(studentFinalMarks.getCompXM_score() >= 0) {
				isCompXMPassed=true;
			}else
				failReason=failReason+" CompXM score is less than "+COMPXM_PASSING_SCORE;
			
			//The student has to attempt minimum or exactly 1 IA's
			if(studentFinalMarks.getAttemptedIA() >= 1 && !StringUtils.isEmpty(studentFinalMarks.getIaScore()))
				isPassedInIA=true;
			else
				failReason=failReason+" Required no of IA's not attempted. ";
			
			//If the student passed in the all component[IA, Simulation, CompXM] then calculate is passed overall with 50% of 100[IA=20+SIM=30+CXM=50] marks.  
			if(isSimulationPassed && isCompXMPassed && isPassedInIA)
				isOverallPassed = Math
						.round((studentFinalMarks.getSimulation_score() + studentFinalMarks.getCompXM_score()
								+ Integer.parseInt(studentFinalMarks.getIaScore()))) >= OVERALL_PASSING_SCORE;
			
			//Mark student as passed only if it passed in overall percentage. We are marking Overall passed only if student passed in all component[IA, Simulation, CompXM]. 					
			if(isOverallPassed)	
				studentFinalMarks.setIsPass("Y");
			else {
				studentFinalMarks.setIsPass("N");
				studentFinalMarks.setFailReason("Total less than 50/"+failReason);
			}	
		}//else block
	}//isPassCheck(-)

}
