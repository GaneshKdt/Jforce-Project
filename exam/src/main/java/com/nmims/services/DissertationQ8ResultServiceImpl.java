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
import com.nmims.daos.DissertationResultProcessingDaoInterface;
import com.nmims.dto.DissertationResultProcessingDTO;
import com.nmims.interfaces.DissertationQ8FilterService;
import com.nmims.interfaces.DissertationQ8ResultDaoInterface;
import com.nmims.interfaces.DissertationQ8ResultService;
import com.nmims.util.StringUtility;

@Service
public class DissertationQ8ResultServiceImpl  implements DissertationQ8ResultService{

	@Autowired
	DissertationQ8ResultDaoInterface dissertationQ8DaoService;
	
	@Autowired
	DissertationResultProcessingDaoInterface dissertationResultDao;
	
	@Autowired
	DissertationQ8FilterService dissertationQ8FilterService;
	

	
	private final static String SUBJECT = "Masters Dissertation Part - II";
	private final static int SEM = 8;
	
	private static final Logger dissertationLogs = (Logger) LoggerFactory.getLogger("dissertationResultProcess");
	
	@Override
	public List<Integer> getConsumerAndSubjectId() {
		List<TEEResultBean> responseBean= dissertationQ8DaoService.getConsumerAndSubjectId(SUBJECT,SEM);
		 return responseBean.stream().map(key -> key.getConsumerProgramStructureId())
				.collect(Collectors.toList());
	}

	@Override
	public List<BatchExamBean> getBatchList(List<Integer> consumerProgramStructureId) {
		String masterKeys = consumerProgramStructureId.stream().map(Object::toString).collect(Collectors.joining(","));
		return dissertationQ8DaoService.getBatchList(masterKeys,SEM);
	}

	@Override
	public List<DissertationResultBean> getDissertationQ8Scores(String timebound_id,
			List<TEEResultBean> eligibleIAStudents, List<DissertationResultBean> errorList, String loggerInUser) {
		List<DissertationResultBean> finalUpserList =  new ArrayList<DissertationResultBean>();
		
		eligibleIAStudents.stream().forEach(id -> {
			
			//getting sessionPlan  by timebound and sapid from timebound_user_mapping 
			int timeBoundId = dissertationResultDao.getTimboundDetails(id.getSapid(),timebound_id);
			dissertationLogs.info("Dissertation Q8 : timebound_subject_config_id from timebound_user_mapping  "+" "+timeBoundId);
			
			//getting sessionPlanId from sessionPlanId_timeBoundId_mapping by timeBoundId
			int sessionPlanId = dissertationResultDao.getSessionPlanId(timeBoundId);
			dissertationLogs.info(" Dissertation Q8 :  sessionPlanId for"+" "+id.getSapid()+" is "+sessionPlanId);

			//getting refernceIds from sessionPlanModules by sessionPlanId
			List<Integer> refrenceId = dissertationResultDao.getSessionIds(sessionPlanId);
			dissertationLogs.info("Dissertation Q8 : refrenceIds for"+" "+id.getSapid()+" is "+refrenceId);

		
			//Creating commaseprated String of refernce Ids
			String refId = StringUtility.generateCommaSeprateStringByInteger(refrenceId);
		
			//Getting testIds from test_testid_configuration_mapping by refernce Id
			List<Integer> testIds = dissertationResultDao.getTestIds(refId);
			dissertationLogs.info("Dissertation Q8 : testIds for"+" "+id.getSapid()+" is "+testIds);

			//Creating commaseprated String of testId
			String commaSepratedTestId = StringUtility.generateCommaSeprateStringByInteger(testIds);
		
			//getting testScore by testIds from test_student_testdetails
			List<DissertationResultProcessingDTO> iaTestScore = dissertationResultDao.getTestScores(id.getSapid(),commaSepratedTestId);
			dissertationLogs.info("Dissertation Q8 : iaTestScore for"+" "+id.getSapid()+" is "+iaTestScore);

			List<TestExamBean> examTest = dissertationResultDao.getExamTest(commaSepratedTestId);
			
			//filtering IA Score on sapid and testId
			finalUpserList.add(dissertationQ8FilterService.filterDissertationQ8IAScores(id,iaTestScore,examTest,loggerInUser));
			//finalInsertMarksList.addAll(filterService.filterIAScores(sapId, iaTestScore, examTest, loggedInUser));
			
			
			
		});
		
		
		return finalUpserList;
	}

	@Override
	public int upsertIntoMarks(List<DissertationResultBean> upsertList) {
		//insert into marks
		return dissertationQ8DaoService.upsertIntoMarks(upsertList);
	}

	@Override
	public List<DissertationResultBean> getQ8MarksList(String timebound_id) {
		
		return dissertationQ8DaoService.getQ8MarksList(timebound_id);
	}

	@Override
	public List<DissertationResultBean> processUpsertList(List<DissertationResultBean> upsertListForStaging,String loggedInUser) {
		 //passFail logic
		upsertListForStaging.stream().forEach(score ->{
			int passing_score = score.getComponent_c_max_score()/2;
			score.setTotal((int)Math.round(score.getComponent_c_score()));
			score.setCreatedBy(loggedInUser);
			score.setLastModifiedBy(loggedInUser);
			score.setIsPass("N");
			
			//if component status is not attempted than reason will be set accordingly
			if ("Not Attempted".equalsIgnoreCase(score.getComponent_c_status())) {
				score.setFailReason("Component C Not Attempted");
				score.setIsPass("N");
				return;
			}

			//if total score is greater than max score student will be considered as fail
			if (score.getTotal() > score.getComponent_c_max_score()) {
				score.setFailReason("Component C Marks greater than Max Marks");
				score.setIsPass("N");
				return;
			}

			//if total score is greater than passing score student will be considered as pass
			if (score.getTotal() >= passing_score) {
				score.setIsPass("Y");
			} else {
				score.setFailReason("Below than Passing Marks");
				score.setIsPass("N");
			}
			
		});
	return upsertListForStaging;
		
	}

	@Override
	public int upsertMarkstoStaging(List<DissertationResultBean> finalUpsertListForStaging) {
		
		return dissertationQ8DaoService.upsertMarkstoStaging(finalUpsertListForStaging);
	}

	@Override
	public List<DissertationResultBean> getGraceApplicabeStudent(String timebound_id) {
//filtring for graceapplicable student list if score is with 18 to below than 20
		 List<DissertationResultBean> failStudentList = dissertationQ8DaoService.getGraceApplicabeStudent(timebound_id);
		 return  failStudentList.stream().
				 filter(grace -> grace.getTotal() >= 18 && grace.getTotal()< 20)
				 .collect(Collectors.toList());
			
	}

	@Override
	public List<DissertationResultBean> applyGrace(List<DissertationResultBean> graceStudent) {
		//applying grace student list if score is with 18 to below than 20
		return graceStudent.stream().map(id ->{	
			if(id.getTotal() >= 18 && id.getTotal() < 20) {
				
				if(id.getTotal() == 18) {
					id.setGraceMarks(2);
					 id.setTotal(id.getTotal() + 2);
				}else if(id.getTotal() == 19) {
					id.setGraceMarks(1);
					 id.setTotal(id.getTotal() + 1);
				}
				id.setIsPass("Y");
				id.setFailReason(null);
			}
			return id;
		}).collect(Collectors.toList());
		 
	}

	@Override
	public int upsertGraceList(List<DissertationResultBean> processedGrace) {
		return dissertationQ8DaoService.upsertGraceList(processedGrace);
	}
	
	@Override
	public List<DissertationResultBean> getPassFailStaging(String timebound_id) {
		return dissertationQ8DaoService.getPassFailStaging(timebound_id);
	}

	@Override
	@Transactional(readOnly = false,propagation = Propagation.REQUIRED,rollbackFor = Exception.class)
	public int transferToPassFail(String timebound_id,List<DissertationResultBean> passFailList) throws SQLException {

		int passfailInsertCount = dissertationQ8DaoService.upsertIntoPassFail(passFailList);
		
		int deleteCountFromStaging = dissertationQ8DaoService.deleteFromStaging(timebound_id);
		
		//if insert count to passfail and delete count from staging doesn't match insert and delete will be revert
		if(passfailInsertCount != deleteCountFromStaging) {
			throw new SQLException("Insert and Delete Count Does'nt match for the current batch :"+timebound_id);
		}
		
		return passfailInsertCount;
	}

	@Override
	public int makeLive(String timebound_id) {
		
		return dissertationQ8DaoService.makeLive(timebound_id);
	}

	@Override
	public int updateMarksProccessed(List<DissertationResultBean> finalUpsertListForStaging) {
		// TODO Auto-generated method stub
		return dissertationQ8DaoService.updateMarksProccessed(finalUpsertListForStaging);
	}

	@Override
	public MBAWXPassFailStatus mapPassFailBean(String sapid, String timeboundId) {
		MBAWXPassFailStatus bean  = new MBAWXPassFailStatus();
		try {
			//mapping is done to set MBAWx status bean for student Side
			DissertationResultBean resultBean = dissertationQ8DaoService.getDissertationResult(sapid, timeboundId);
		
			bean.setSapid(String.valueOf(resultBean.getSapid()));
			int totalMarks = resultBean.getTotal();
			int totalMaxMarks = resultBean.getComponent_c_max_score();
			bean.setTotal(String.valueOf(totalMarks));
			bean.setFailReason(resultBean.getFailReason());
			bean.setIaScore(String.valueOf(totalMarks));
			bean.setIsPass(resultBean.getIsPass());
			bean.setMax_score(String.valueOf(totalMaxMarks));
			bean.setIsResultLive(resultBean.getIsResultLive());
			bean.setGraceMarks(String.valueOf(resultBean.getGraceMarks()));
		} catch (Exception e) {
			return bean;
		}

		return bean;
	}

	@Override
	public MBAWXExamResultForSubject getMastersDissertationResult(String sapid,
			StudentSubjectConfigExamBean timeboundSubject, MBAWXPassFailStatus passFailStatus, String hasIA,
			String hasTEE) {
	
		MBAWXExamResultForSubject dissertationResult = null;
		try {
			//mapping is done to set MBAWx Result Bean for student Side
			if ("Y".equalsIgnoreCase(hasIA) && "N".equalsIgnoreCase(hasTEE)
					&& "Y".equalsIgnoreCase(passFailStatus.getIsResultLive())) {
				
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
				dissertationResult.setGraceMarks(passFailStatus.getGraceMarks());
			}
		} catch (Exception e) {
		}
		return dissertationResult;
	}

	@Override
	public List<DissertationResultBean> applyGrade(List<DissertationResultBean> passFailStaging, String loggedInUser,
			List<EmbaGradePointBean> gradeList) {

		passFailStaging.stream().forEach(passFail ->{
			float total = passFail.getTotal();
	
			float maxScore = passFail.getComponent_c_max_score();
		
			int totalScore = (int)((total/maxScore)*100);
			
			EmbaGradePointBean gradePointData =  gradeList.stream()
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
	public List<EmbaGradePointBean> getAllGrades() {
		return  dissertationQ8DaoService.getAllGrade();
	}

	@Override
	public int upsertGrade(List<DissertationResultBean> upsertList) {
		return dissertationQ8DaoService.upsertGrade(upsertList);
	}
	

}

	
