package com.nmims.services;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import com.nmims.beans.BatchExamBean;
import com.nmims.beans.DissertationResultBean;
import com.nmims.beans.EmbaGradePointBean;
import com.nmims.beans.MBAWXExamResultForSubject;
import com.nmims.beans.MBAWXPassFailStatus;
import com.nmims.beans.StudentSubjectConfigExamBean;
import com.nmims.beans.TEEResultBean;
import com.nmims.beans.TestExamBean;
import com.nmims.beans.TimeBoundUserMapping;
import com.nmims.daos.DissertationResultProcessingDaoInterface;
import com.nmims.daos.ExamsAssessmentsDAO;
import com.nmims.dto.DissertationResultProcessingDTO;
import com.nmims.interfaces.DissertationFilterService;
import com.nmims.interfaces.DissertationResultProcessingService;
import com.nmims.util.StringUtility;

@Service
public class DissertationResultProcessingServiceImplForQ7 implements DissertationResultProcessingService{

	@Autowired
	DissertationResultProcessingDaoInterface mscResultDaoInterface;
	
	@Autowired
	ExamsAssessmentsDAO examsAssessmentsDAO;
	
	@Autowired
	DissertationFilterService filterService;
	
	private static final Logger dissertationLogs = (Logger) LoggerFactory.getLogger("dissertationResultProcess");

	private String subject = "Masters Dissertation Part - I";
	private int semQ7 = 7;
	
	
	@Override
	public List<Integer> getConsumerAndSubjectId() {
		
		
		List<TEEResultBean> responseResultBean =  mscResultDaoInterface.getConsumerAndSubjectId(subject,semQ7);
			return responseResultBean.stream().map(key -> key.getConsumerProgramStructureId())
					.collect(Collectors.toList());

	}
	

	
	@Override
	public List<BatchExamBean> getBatchList(List<Integer> masterKeys) {
		
		//getting batchList by consumerStructureId
		String consumerStructureKeys = masterKeys.stream().map(Object::toString).collect(Collectors.joining(","));
		
				
		List<BatchExamBean> batchList = mscResultDaoInterface.getBatchList(consumerStructureKeys,semQ7);

		return batchList;

	}

	@Override
	public List<TEEResultBean> getEligibleIAStudents(String timeboundId,List<DissertationResultBean> errorList) {
	
		dissertationLogs.info("Process Started for timeBoundId "+" "+timeboundId);
		//Getting mapped Student on timebound Id from timebound_subject_config
		List<TimeBoundUserMapping> timboundUser = mscResultDaoInterface.getMappedStudent(timeboundId);	
		dissertationLogs.info("No of user found on timeboundId "+" "+timboundUser.size());
		
		//getting student config overall data and creating hashmap as id as a key and current object as value 
		String subjectId = mscResultDaoInterface.getStudentSubjectConfig(timeboundId);
		dissertationLogs.info("Pss Id for timebound - "+timeboundId+ "is"+" "+subjectId);
		
		//getting program overall data and creating hashmap as id as a key and current object as value 
		DissertationResultProcessingDTO program = mscResultDaoInterface.getProgram(subjectId);
		dissertationLogs.info("Porgram Id and passScore"+ program.getId()+" "+ program.getPassScore());
	
		//filtering to collect eligible student
		return filterService.filterIAEligibleStudent(timboundUser,subjectId,program,errorList);
	}
	

	@Override
	public List<DissertationResultBean> getDissertationQ7Scores(String timeboundId,List<TEEResultBean> eligibleIAStudents,List<DissertationResultBean> errorList,String loggedInUser) {
	
//		SELECT tst.*, t.startDate, t.endDate , COALESCE(tst.score,0) as scoreInInteger , t.showResultsToStudents, 
//		t.testName FROM exam.test_student_testdetails tst " + 
//		" INNER JOIN exam.test_testid_configuration_mapping ttcm ON tst.testId = ttcm.testId" + 
//		" INNER JOIN acads.sessionplan_module spm ON spm.id = ttcm.referenceId" + 
//		" INNER JOIN acads.sessionplanid_timeboundid_mapping stm ON stm.sessionPlanId=spm.sessionPlanId" + 
//		" INNER JOIN lti.student_subject_config ssc ON ssc.id=stm.timeboundId " + 
//		" INNER JOIN exam.program_sem_subject pss ON pss.id = ssc.prgm_sem_subj_id" + 
//		" INNER JOIN exam.test t ON t.id= tst.testId" + 
//		" WHERE stm.timeboundId = ? and tst.sapid=? "
//		+ " and spm.topic <> 'Generic Module For Session Plan ' group by t.id"
		
	List<DissertationResultBean> finalInsertMarksList = new ArrayList<DissertationResultBean>();
	
	eligibleIAStudents.stream().forEach(sapId -> {
		dissertationLogs.info("process start for sapid  "+" "+sapId.getSapid());
		//getting sessionPlan  by timebound and sapid from timebound_user_mapping 
		int timeBoundId = mscResultDaoInterface.getTimboundDetails(sapId.getSapid(),timeboundId);
		dissertationLogs.info("timebound_subject_config_id from timebound_user_mapping  "+" "+timeBoundId);
		
		//getting sessionPlanId from sessionPlanId_timeBoundId_mapping by timeBoundId
		int sessionPlanId = mscResultDaoInterface.getSessionPlanId(timeBoundId);
		dissertationLogs.info("sessionPlanId for"+" "+sapId.getSapid()+" is "+sessionPlanId);

		//getting refernceIds from sessionPlanModules by sessionPlanId
		List<Integer> refrenceId = mscResultDaoInterface.getSessionIds(sessionPlanId);
		dissertationLogs.info("refrenceIds for"+" "+sapId.getSapid()+" is "+refrenceId);

	
		//Creating commaseprated String of refernce Ids
		String refId = StringUtility.generateCommaSeprateStringByInteger(refrenceId);
	
		//Getting testIds from test_testid_configuration_mapping by refernce Id
		List<Integer> testIds = mscResultDaoInterface.getTestIds(refId);
		dissertationLogs.info("testIds for"+" "+sapId.getSapid()+" is "+testIds);

		//Creating commaseprated String of testId
		String commaSepratedTestId = StringUtility.generateCommaSeprateStringByInteger(testIds);
	
		//getting testScore by testIds from test_student_testdetails
		List<DissertationResultProcessingDTO> iaTestScore = mscResultDaoInterface.getTestScores(sapId.getSapid(),commaSepratedTestId);
		dissertationLogs.info("iaTestScore for"+" "+sapId.getSapid()+" is "+iaTestScore);

		List<TestExamBean> examTest = mscResultDaoInterface.getExamTest(commaSepratedTestId);
	
		finalInsertMarksList.addAll(filterService.filterIAScores(sapId, iaTestScore, examTest, loggedInUser));

	});

	return finalInsertMarksList;

}
	
	@Override
	public int upsertMarks(List<DissertationResultBean> insertMarksData) {
	
		return mscResultDaoInterface.upsertMarks(insertMarksData);
		
	}

	@Override
	public List<DissertationResultBean> getNotProcessedList(String timeBoundId) {
		return mscResultDaoInterface.getNotProcessedList(timeBoundId);
	}

	@Override
	public List<DissertationResultBean> processUpsertList(List<DissertationResultBean> upsertList,String loggerInUser) {

	upsertList.stream().forEach(sapid ->{
		//Double component_a_score = sapid.getComponent_a_score();
			double component_b_score =  sapid.getComponent_b_score();
			String component_a_status = sapid.getComponent_a_status();
			String component_b_status = sapid.getComponent_b_status();
			int component_b_max_score =  sapid.getComponent_b_max_score();
			int component_b_passingScore = component_b_max_score/2;
			sapid.setIsPass("N");
			
			sapid.setCreatedBy(loggerInUser);
			sapid.setLastModifiedBy(loggerInUser);
			if("Not Attempted".equalsIgnoreCase(component_a_status) && "Attempted".equalsIgnoreCase(component_b_status)) {
				sapid.setIsPass("N");
				sapid.setFailReason("Component_A Not Attempted");
				//sapid.setGrade("UnSatisFactory");
			}
			
			if("Attempted".equalsIgnoreCase(component_a_status) && "Not Attempted".equalsIgnoreCase(component_b_status)) {
				sapid.setIsPass("N");
				sapid.setFailReason("Component_B Not Attempted");
				//sapid.setGrade("UnSatisFactory");
			}
			if("Not Attempted".equalsIgnoreCase(component_a_status) && "Not Attempted".equalsIgnoreCase(component_b_status)) {
				sapid.setIsPass("N");
				sapid.setFailReason("Component_A & Component_B Both Not Attempted");
				//sapid.setGrade("UnSatisFactory");
			}
			
			if("Attempted".equalsIgnoreCase(component_b_status)) {
				if(component_b_max_score < component_b_score) {
					sapid.setIsPass("N");
					sapid.setFailReason("Component_B marks is greater than max marks");
					//sapid.setGrade("UnSatisFactory");
				}
			}
			
			if("Attempted".equalsIgnoreCase(component_b_status)) {
				if(component_b_score < component_b_passingScore) {
					sapid.setIsPass("N");
					sapid.setFailReason("Component_B marks is less than passing marks");
					//sapid.setGrade("UnSatisFactory");
				}
			}
			
			
			if ("Attempted".equalsIgnoreCase(component_a_status) && "Attempted".equalsIgnoreCase(component_b_status)) {
				if(component_b_score <= component_b_max_score) {
					if(component_b_score>=component_b_passingScore) {
						sapid.setIsPass("Y");
						sapid.setFailReason(null);
						sapid.setGrade(null);
						sapid.setGradePoints(0);
					}
					
				}
			}
	
		});
		
		return upsertList;
	}

	@Override
	public int upsertPassFailStaging(List<DissertationResultBean> upsertList) {
		return mscResultDaoInterface.upsertPassFailStaging(upsertList);
		
	}

	@Override
	@Transactional(readOnly = false,propagation = Propagation.REQUIRED,rollbackFor = Exception.class)
	public int transferToPassFail(List<DissertationResultBean> transferList,String timeBoundId) throws SQLException {

		//insert staging list in passFail
		int insertCount = mscResultDaoInterface.transferrToPassFailQ7(transferList);
		
		//delete all the data from staging table
		int deleteStagingData = mscResultDaoInterface.deleteStagingData(timeBoundId);
		
		//if inserted count and delete count does'nt match each steps will be rollback
		if(insertCount != deleteStagingData ) {
			throw new SQLException();
			
		}
		return insertCount;
	}

	@Override
	public int updateUpsertList(List<DissertationResultBean> transferListForStaging) {
		
		int updateCount =mscResultDaoInterface.updateUpsertList(transferListForStaging);
		return updateCount;
	}


	@Override
	public MBAWXPassFailStatus mapPassFailBean(String sapid, String timeboundId) {
		MBAWXPassFailStatus bean  = new MBAWXPassFailStatus();
		try {
		
		DissertationResultBean resultBean =  mscResultDaoInterface.getDissertationResult(sapid,timeboundId);
		bean.setSapid(String.valueOf(resultBean.getSapid()));
		int  totalMarks = (int) (Math.round(resultBean.getComponent_a_score())+ Math.round(resultBean.getComponent_b_score()));
		int totalMaxMarks = resultBean.getComponent_a_max_score()+resultBean.getComponent_b_max_score();	
		bean.setTotal(String.valueOf(totalMarks));
		bean.setFailReason(resultBean.getFailReason());
		bean.setIaScore(String.valueOf(totalMarks));
		bean.setIsPass(resultBean.getIsPass());
		bean.setMax_score(String.valueOf(totalMaxMarks));
		bean.setIsResultLive(resultBean.getIsResultLive());
		}catch(Exception e) {
			dissertationLogs.info("Error Found for "+"  "+timeboundId+" " +e.getMessage());
			return bean;
		}
		
		return bean;
		
	}


	@Override
	public MBAWXExamResultForSubject getMastersDissertationResult(String sapid, StudentSubjectConfigExamBean timeboundSubject,
			MBAWXPassFailStatus passFailStatus, String hasIA, String hasTEE) {
		MBAWXExamResultForSubject dissertationResult = null;
		try {
		if("Y".equalsIgnoreCase(hasIA) && "N".equalsIgnoreCase(hasTEE) && "Y".equalsIgnoreCase(passFailStatus.getIsResultLive())) {
			dissertationResult = new MBAWXExamResultForSubject();
			dissertationResult.setTimeboundId(timeboundSubject.getId());
			dissertationResult.setAcadsMonth(timeboundSubject.getAcadMonth());
			dissertationResult.setAcadsYear(timeboundSubject.getAcadYear());
			dissertationResult.setExamMonth(timeboundSubject.getExamMonth());
			dissertationResult.setExamYear(timeboundSubject.getExamYear());
			dissertationResult.setSubject(timeboundSubject.getSubject());
			dissertationResult.setTerm(timeboundSubject.getSem());
			dissertationResult.setStartDate(timeboundSubject.getStartDate());
			dissertationResult.setSapid(sapid);
			dissertationResult.setShowResultsForIA("Y");
			dissertationResult.setPrgm_sem_subj_id(timeboundSubject.getPrgm_sem_subj_id());
			dissertationResult.setShowResults("Y");
			dissertationResult.setTotal(passFailStatus.getTotal());
			dissertationResult.setTotalMax(passFailStatus.getMax_score());
			dissertationResult.setIsPass(passFailStatus.getIsPass());
			dissertationResult.setIaScore(passFailStatus.getIaScore());
			dissertationResult.setIaScoreMax(passFailStatus.getMax_score());
		}
		}catch (Exception e) {
			dissertationLogs.info("Error Found  "+" "+ e.getMessage());
		}
		return dissertationResult;
	}


	@Override
	public int makeResultLive(String timebound_id) {
	
		return mscResultDaoInterface.makeResultLive(timebound_id);
	}


	@Override
	public List<DissertationResultBean> getStagedDissertationList(String timebound_id) {
		return mscResultDaoInterface.getStagedDissertationList(timebound_id);
	}



	@Override
	public List<DissertationResultBean> applyGradeForQ7(List<DissertationResultBean> passFailStaging,String loggedInUser,List<EmbaGradePointBean> grades) {
		 passFailStaging.stream().forEach(passFail ->{
			
			float total = (float) (passFail.getComponent_a_score() + passFail.getComponent_b_score());
			float maxScore = passFail.getComponent_a_max_score() + passFail.getComponent_b_max_score();
			int totalScore = (int) ((total/maxScore) * 100);
			EmbaGradePointBean gradePointData =  grades.stream()
					.filter(grade1-> totalScore >= Integer.parseInt(grade1.getMarksFrom())
					&& totalScore <= Integer.parseInt(grade1.getMarksTill()))
					.findFirst().get();
			
			passFail.setGrade(gradePointData.getGrade());
			passFail.setGradePoints(Float.parseFloat(gradePointData.getPoints()));
			passFail.setLastModifiedBy(loggedInUser);
		});
		 return passFailStaging;
		 
	}



	@Override
	public int upsertGradeInPassFailStaging(List<DissertationResultBean> processListForGrade) {
		
		return mscResultDaoInterface.upsertGradeInPassFailStaging(processListForGrade);
	}



	@Override
	public List<EmbaGradePointBean> getAllGrades() {
		// TODO Auto-generated method stub
		return mscResultDaoInterface.getAllGrades();
	}


	



}
