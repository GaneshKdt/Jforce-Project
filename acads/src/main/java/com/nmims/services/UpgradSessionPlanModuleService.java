package com.nmims.services;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.stereotype.Service;

import com.nmims.beans.ResponseAcadsBean;
import com.nmims.beans.SessionPlanModuleBean;
import com.nmims.beans.TestAcadsBean;
import com.nmims.beans.UpgradTestQuestionBean;
import com.nmims.daos.SessionPlanDAO;
import com.nmims.daos.TestDAO;
import com.nmims.daos.UpgradAssessmentDao;

@Service
public class UpgradSessionPlanModuleService {

	@Autowired
	UpgradAssessmentDao upgradAssessmentDao;

	@Autowired
	TestDAO testDao;
	
	@Autowired
	ApplicationContext act;
	
	public ResponseAcadsBean saveSessionPlanModuleService( SessionPlanModuleBean module){
		ResponseAcadsBean responseBean = new ResponseAcadsBean();
		// call Store Procedure		
		upgradAssessmentDao.callProcedureUpgradSessionplanidTimeboundidMapping();
			
		SessionPlanDAO dao = (SessionPlanDAO) act.getBean("sessionPlanDAO");
				//Save Session plan module
				long saveModule =  dao.insertUpgradModuleDetails(module);
				if(saveModule==0) {
					responseBean.setStatus("fail");
					responseBean.setMessage("Error in saving SessionPlanModule to DB");
					responseBean.setCode(422);
					return responseBean ;
				}else {
					module.getTest().setReferenceId(module.getSessionModuleNo());
					return saveUpgradAddTest(module.getTest(), module.getTest().getTestQuestions());
				}	
			
	}
	
	
	
	public ResponseAcadsBean saveUpgradAddTest(TestAcadsBean test, List<UpgradTestQuestionBean> testQuestions){
		ResponseAcadsBean responseBean = new ResponseAcadsBean();
		try {
			
			test.setCreatedBy("Upgrad");
			test.setLastModifiedBy("Upgrad");
			test.setActive("Y");
			test.setConsumerTypeIdFormValue(test.getConsumerTypeId());
			test.setProgramStructureIdFormValue(test.getProgramStructureId());
			test.setProgramIdFormValue(test.getProgramId());
			
			if("old".equalsIgnoreCase(test.getApplicableType())) {
				test.setReferenceId(0);
			}
			
			
			//Checks for startDate endDate Validity start
			boolean isStartDateEndDateValid = checkStartDateEndDateValid(test);
			//Checks for startDate endDate Validity end
			if(!isStartDateEndDateValid) {
				responseBean.setStatus("fail");
				responseBean.setMessage("Invalid Start End Date");
				responseBean.setCode(422);
				return responseBean ;
			}
			// save new test
				long saveTest = upgradAssessmentDao.saveUpgradTest(test);
				
				String saveTestQuestions = saveUpgradTestQuestions(testQuestions,test.getTestId());
				if(saveTest==0 || "false".equals(saveTestQuestions)) {
					responseBean.setStatus("fail");
					responseBean.setMessage("Error in saving MCQ Question to DB");
					responseBean.setCode(422);
					return responseBean;
				
				}else {
					//test.setId(saveTest);
					
					test.setId(test.getTestId());
					String createTestIdConfigurationMappingsError = createTestIdConfigurationMappings(testDao,upgradAssessmentDao, test);
					
					if(StringUtils.isBlank(createTestIdConfigurationMappingsError)) {
						//dao.insertMCQInPost(test);
						responseBean.setStatus("success");
						responseBean.setMessage("Module and Test Created Successfully.");
						responseBean.setCode(200);
						return responseBean ;
					}else {
						responseBean.setStatus("fail");
						responseBean.setMessage("Error in saving test to DB createTestIdConsumerTypeIdMappingsError : "+createTestIdConfigurationMappingsError);
						responseBean.setCode(422);
						return responseBean;
					}
				}		
		} catch (Exception e) {
			  
			responseBean.setStatus("fail");
			responseBean.setMessage("Error in calling saveUpgradAddTest : "+e.getMessage());
			responseBean.setCode(422);
			return responseBean;
		}
		
	}
	
	public String saveUpgradTestQuestions(  List<UpgradTestQuestionBean> testQuestions, Long testId){
		try {
			upgradAssessmentDao.insertUpgradTestQuestions(testQuestions, testId);
			return "true";	
		}catch (Exception e) {
			  
			return "false";
		}
		
	}
	
	private boolean checkStartDateEndDateValid(TestAcadsBean test) throws Exception {

		String startDate = test.getStartDate();
		String endDate = test.getEndDate();
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");
		Date sDate = sdf.parse(startDate.replaceAll("T", " "));
		Date cDate = new Date();
		Date eDate = sdf.parse(endDate.replaceAll("T", " "));
		
			if(eDate.before(sDate)) {
				//throw new Exception("Error in test config, EndDate : "+eDate+" is before StartDate : "+sDate);
				return false;
			}
		return true;
	}
	
	private String createTestIdConfigurationMappings(TestDAO dao,UpgradAssessmentDao upgradAssessmentDao, TestAcadsBean test) {
		
//		List<Long> configIds = getConfigIdsForTestLiveSettings(dao,test); commented by Abhay
		if( test.getMaxScore() > 10 ) {
			test.setIaType("Project");
		}else {
			test.setIaType("Assignment");
		}
		return upgradAssessmentDao.insertUpgradTestIdNConfigurationMappings(test);
	}

	private List<Long> getConfigIdsForTestLiveSettings(TestDAO testDao, TestAcadsBean test) {
		
		List<Long> returnEmptyList = new ArrayList<>();
		
		
		if("module".equalsIgnoreCase(test.getApplicableType()) ) {
			
			List<Long> moduleIds = testDao.getUpgradModuleIdByProgramConfigYearMonthBatchIdNId(
					 test.getConsumerTypeId()
					 ,test.getProgramStructureId()
					 ,test.getProgramId()
					 ,test.getAcadYear()
					 ,test.getAcadMonth()
					 ,test.getReferenceId()
					 ,test.getSubject()
					 ,test.getModuleBatchId() 
					);
	
			//return dao.insertTestIdNConfigurationMappings(test,moduleIds);
			return moduleIds;
		}else if("batch".equalsIgnoreCase(test.getApplicableType())) {
				//1. Get programSemSubjectIds
				ArrayList<String> programSemSubjectIds =
						testDao.getUpgradProgramSemSubjectIdsBySubjectNProgramConfig(test.getProgramId()
						 ,test.getProgramStructureId()
						 ,test.getConsumerTypeId()
						 ,test.getSubject());
				if(programSemSubjectIds.isEmpty()) {
					return returnEmptyList;
				}
				
				//2. get timeboundIds by programSemSubjectIds
				List<Long> timeboundIds = 
						testDao.getUpgradTimeboundIdsByProgramSemSubjectIdsNBatchId(programSemSubjectIds,test.getAcadYear(),test.getAcadMonth(),test.getModuleBatchId());
				
				if(timeboundIds.isEmpty()) {
					return returnEmptyList;
				}
				
				return timeboundIds;
				
			}else {
				return returnEmptyList;
				
			}
	}
	
	public ResponseAcadsBean deleteSessionPlanModuleCascadeSevice( SessionPlanModuleBean module) {
		ResponseAcadsBean responseBean = new ResponseAcadsBean();
		
		try {
			upgradAssessmentDao.deleteSessionPlanModuleCascade(module);
			responseBean.setStatus("success");
			responseBean.setMessage("Module and Test Deleted Successfully.");
			responseBean.setCode(200);
			return responseBean;
		}catch(Exception e) {
			responseBean.setStatus("fail");
			responseBean.setMessage("Error in deleting Module And Test");
			responseBean.setCode(422);
			  
			return responseBean ;
		}
	}

	
}
