package com.nmims.services;

import java.text.SimpleDateFormat;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang3.StringUtils;
import org.json.simple.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import com.nmims.beans.EmbaGradePointBean;
import com.nmims.beans.EmbaPassFailBean;
import com.nmims.beans.ResponseBean;
import com.nmims.beans.StudentSubjectConfigExamBean;
import com.nmims.beans.TEEResultBean;
import com.nmims.beans.TestExamBean;
import com.nmims.beans.UpgradAssessmentExamBean;
import com.nmims.beans.UpgradQuestionAnsweredDetailsBean;

import com.nmims.beans.UpgradTestQuestionExamBean;
import com.nmims.beans.UpgradTestQuestionOptionExamBean;
import com.nmims.daos.ExamsAssessmentsDAO;
import com.nmims.daos.TestDAO;
import com.nmims.daos.UpgradAssessmentDao;

@Service("assessmentService")
public class UpgradAssessmentService {

	
	@Autowired
	UpgradAssessmentDao upgradAssessmentDao;

	@Autowired
	ExamsAssessmentsDAO examsAssessmentsDAO;
	
	@Autowired
	TestDAO testDao;
	


	@Value("${CURRENT_MBAX_ACAD_YEAR}")
	private Integer CURRENT_MBAX_ACAD_YEAR;
	
	@Value("${CURRENT_MBAX_ACAD_MONTH}")
	private String CURRENT_MBAX_ACAD_MONTH;
	
	private Integer totalMarksCount = 0 ;
	
	public ResponseBean saveUpdateAssessmentScores(List<UpgradAssessmentExamBean> testDetails){
		ResponseBean responseBean = new ResponseBean();
		
		try {
			
		for(UpgradAssessmentExamBean testBean : testDetails) {
			
			if(testBean.getAttempt() == 1) {
				testBean.setAttemptStatus("Attempted");
			}else {
				testBean.setAttemptStatus("NA");
			}
			
			
			if("Y".equals(testBean.getPlagiarised())) {
				testBean = setPlagiarisedData(testBean) ;
			}
			
				
			upgradAssessmentDao.insertUpgradAssessmentScores(testBean);
			for(UpgradQuestionAnsweredDetailsBean bean : testBean.getTestQuestionsAnsDetails()) {
				if(bean.getStudentAnswer() != null && !bean.getStudentAnswer().isEmpty()) {
					Integer questionTypeId = upgradAssessmentDao.getQuestionTypeById(bean.getQuestionNo(), testBean.getTestId());
					Integer marksObtained ;
					if(questionTypeId  == 1 || questionTypeId == 2 || questionTypeId == 5) {
						marksObtained = 0;
					}else {
						marksObtained = Integer.parseInt( bean.getMarksObtained());
					}
					upgradAssessmentDao.insertUpgradAssessmentDetails(bean, testBean.getSapid(), testBean.getTestId(), marksObtained);	
				}
				
			}
			Integer timeboundId = upgradAssessmentDao.getTimeboundIdByTestId(testBean.getTestId());
			upgradAssessmentDao.updateMarksAndMarksHistoryProcessedFlag(timeboundId,testBean.getSapid());
			upgradAssessmentDao.updatePassFailIsResultLiveFlag(timeboundId,testBean.getSapid());
		}
			responseBean.setStatus("success");
			responseBean.setCode(200);
			responseBean.setMessage("AssessmentScores Created Successfully.");
			return responseBean;
		
		}catch(Exception e) {
			
			responseBean.setStatus("fail");
			responseBean.setMessage(e.getMessage());
			responseBean.setCode(422);
			return responseBean;
		}
		
	}
	
	public ResponseBean deleteAssessmentScores(List<UpgradAssessmentExamBean> testDetails) {
		ResponseBean responseBean = new ResponseBean();
		try {
			for(UpgradAssessmentExamBean testBean:testDetails) {
				upgradAssessmentDao.deleteAssessmentScoreCascade(testBean);
			}
			responseBean.setStatus("success");
			responseBean.setMessage("AssessmentScores Deleted Successfully.");
			responseBean.setCode(200);
			return responseBean;
		}catch(Exception e) {
			responseBean.setStatus("fail");
			responseBean.setMessage("Error in deleting AssessmentScore Details");
			responseBean.setCode(422);
			
			return responseBean;
		}
	}
	
	public int updateMbaXPassFailGradePointsByTimeBoundIds(List<EmbaPassFailBean> mbaXPassFailData, String userId) {

			List<EmbaPassFailBean> updatedMbaXPassFailList = new ArrayList<EmbaPassFailBean>();

			for (EmbaPassFailBean studentData : mbaXPassFailData) {
				EmbaGradePointBean gradePointData  = new EmbaGradePointBean();
				if(studentData.getPssId() == 1789) { // added by Abhay for BOP subject
					float total_marks = 50;
					float scored =  Integer.parseInt(studentData.getTotal());
					int totalScore  = (int)((scored / total_marks) * 100);		//calculate percentage	
					gradePointData = examsAssessmentsDAO.getGradeAndPointByTotalScore(totalScore);		//get grades and point by percentage
				}else {
					int totalScore  = Integer.parseInt(studentData.getTotal());		//total Score to get grade And points				
					gradePointData = examsAssessmentsDAO.getGradeAndPointByTotalScore(totalScore);		//get grades and point by totalScore
				}
				studentData.setGrade(gradePointData.getGrade());
				studentData.setPoints(gradePointData.getPoints());										//set grade points				
				
				updatedMbaXPassFailList.add(studentData);
			}
			upgradAssessmentDao.updateMbaXGradeAndPointsList(updatedMbaXPassFailList, userId);		//update grade points list in pass fail
			
			return updatedMbaXPassFailList.size();
		
	}
	public String getTimeBoundIdsByBatchIdAndAcadYearAndAcadMonth(TEEResultBean resultBean) {	
	

			ArrayList<StudentSubjectConfigExamBean> subjectList = examsAssessmentsDAO.getSubjectByBatchIdAndAcadYearAndAcadMonth(resultBean.getBatchId(),resultBean.getCurrent_acad_year(),resultBean.getCurrent_acad_month());
			StringBuilder sb = new StringBuilder();		
			String commaSeparatedTimeBoundIds = "";
			
			if(subjectList.size() > 0) {
				for(StudentSubjectConfigExamBean sscBean : subjectList ) {
					
					 sb.append(sscBean.getId()).append(",");
					 
				}
				commaSeparatedTimeBoundIds = sb.deleteCharAt(sb.length() - 1).toString();
			}else {
				commaSeparatedTimeBoundIds = "0";
			}
			
		
		return commaSeparatedTimeBoundIds;
	}
	
	public ResponseEntity<ResponseBean> getMbaPassFailList(){
		ResponseBean responseBean = new ResponseBean(); 
 
		responseBean  = upgradAssessmentDao.getMbaXPassFailData();
		return new ResponseEntity(responseBean, HttpStatus.OK);	

	}
	
	public List<TestExamBean> getAllMBAXTestForLiveSettingService(){
		try {
			return upgradAssessmentDao.getAllMBAXTestForLiveSetting();
		}catch(Exception e) {
		   
		   return null;
		}
		
		
	}


	public UpgradAssessmentExamBean getCombinedDetailsOfScoreAndAssignmentDetails(Long testId){
//	public void getCombinedDetailsOfScoreAndAssignmentDetails(){
		ArrayList<UpgradAssessmentExamBean> testDetails = upgradAssessmentDao.getCombinedDetailsOfScoreAndAssignmentDetails(testId);
		
		UpgradAssessmentExamBean data = new UpgradAssessmentExamBean();
		data.setTestDetails(testDetails);
		return data;
	}
	
	
	
	public boolean updateShowResultForMBAXTestService( Long testId, Integer referenceId) {
		try {
			   upgradAssessmentDao.updateMarksProcessedFlagNForAllByTestId(testId);
			   upgradAssessmentDao.updatePassFailIsResultLiveFlagNForAllByTestId(testId);
			   upgradAssessmentDao.updateShowResultForMBAXTest(testId, referenceId);
			   
			   return true;	 
		}catch(Exception e) {
			
			return false;
		}
		
	}
	
	public boolean updateHideResultForMBAXTestService( Long testId, Integer referenceId) {
		try {
			upgradAssessmentDao.updateMarksProcessedFlagNForAllByTestId(testId);
			upgradAssessmentDao.updatePassFailIsResultLiveFlagNForAllByTestId(testId);
			upgradAssessmentDao.updateHideResultForMBAXTest(testId, referenceId);
			   return true;	 
		}catch(Exception e) {
			
			return false;
		}
	}
	

	public List<UpgradAssessmentExamBean> getBatchDetailsService(Integer examYear, String examMonth, Integer acadYear, String acadMonth){
		List<UpgradAssessmentExamBean> batchList = new ArrayList<>();
		try {
		  batchList =	upgradAssessmentDao.getBatchDetails(examYear, examMonth, acadYear, acadMonth);
		 return batchList;
		}catch(Exception e) {
			
			return batchList;
		}
		
	}
	
	public ArrayList<UpgradAssessmentExamBean> getCombinedDetailsOfScoreAndAssignmentDetailsToDisplayService(Long testId){
		ArrayList<UpgradAssessmentExamBean> ServiceList = new ArrayList<UpgradAssessmentExamBean>();
		try {
		ServiceList= upgradAssessmentDao.getCombinedDetailsOfScoreAndAssignmentDetailsToDisplay(testId);
		 return ServiceList;
		}catch(Exception e) {
			
			return null;
		}
	}
	
//	public HashMap<String, String> saveUpdateBeforeNormalizeService (Long testId){
//		HashMap<String,String> message = new HashMap<String,String>();
//		try {
//			upgradAssessmentDao.saveUpdateBeforeNormalize(testId);
//			message.put("success", "true");
//			return message;
//		}catch(Exception e) {
//			 message.put("error", "true");
//			 message.put("errorMessage", "Error in processing before Normalize Marks Obtained : "+e.getMessage());
//			 return message;
//		}
//		
//	}
	
	
	public HashMap<String, String> updateMarksObtainedService(UpgradAssessmentExamBean upgradBean){
		HashMap<String,String> message = new HashMap<String,String>();
		try {
			  
			if(upgradBean.getMarksObtained().isEmpty()) {
				message.put("error", "Updated MarksObtained is Empty");
				return message;
			}
			
			Integer validMarks = upgradAssessmentDao.getMarksForValidationInUpdateMarksObtained(upgradBean);
			if(   Integer.parseInt( upgradBean.getMarksObtained()) > validMarks ) {
				message.put("error", "Updated MarksObtained is Exceeded Question Marks Please Enter below the "+validMarks+" Or "+validMarks);
				return message;
			}
			
			upgradAssessmentDao.updateMarksObtained(upgradBean);
			totalMarksCount += Integer.parseInt( upgradBean.getMarksObtained());
			upgradAssessmentDao.updateUpgradAssesmentScore(upgradBean, totalMarksCount);
			totalMarksCount = 0;
			message.put("success", "Successfully MarksObtained Updated");
			return message;
		}catch(Exception e) {
			
			message.put("error", "MarksObtained Not Updated");
			return message;
		}
		
		
	}
	
	public ResponseBean getStudentIATestDetailsService(String sapid, Long testId) {
		ResponseBean successResponseBean = new ResponseBean();
		ResponseBean failResponseBean = new ResponseBean();
		try {
		
		List<UpgradTestQuestionExamBean> upgradTestQuestionList = new ArrayList<>();
		upgradTestQuestionList = upgradAssessmentDao.getTestQuestionDetails(testId,sapid);
		
		for(UpgradTestQuestionExamBean question : upgradTestQuestionList) {
			UpgradQuestionAnsweredDetailsBean testQuestionsAnsDetails = new UpgradQuestionAnsweredDetailsBean ();
			
			
			if(question.getQuestion_type() == 1  || question.getQuestion_type() == 2 || question.getQuestion_type() == 5) {
				
				List<UpgradTestQuestionOptionExamBean> optionList = upgradAssessmentDao.getOptionList(question.getQuestionNo());
				question.setTestQuestionOptions(optionList);
				
					for(UpgradTestQuestionOptionExamBean option : optionList ) {
							String selected = "N";
							List<UpgradQuestionAnsweredDetailsBean> studentAnswerList = upgradAssessmentDao.getStudentAnswerDetailsList(testId, sapid, question.getQuestionNo());
							
							for(UpgradQuestionAnsweredDetailsBean studentAnsList : studentAnswerList) {	
								if(option.getOptionId() == Integer.parseInt(studentAnsList.getStudentAnswer()) ) {
									selected = "Y";
								}
							}
							option.setSelected(selected);	
					}
					 Integer correctCount = upgradAssessmentDao.getCorrectCountForMulti(question.getQuestionNo().intValue());
					 Integer studentAnsCount = upgradAssessmentDao.getStudentAnswerCountForMulti(testId, question.getQuestionNo().intValue(), sapid);
					 	
					  if(correctCount == studentAnsCount ) {
					 		Integer isCorrectCount = upgradAssessmentDao.getIsCorrectCountForMulti(testId, question.getQuestionNo().intValue(), sapid);
					 		if(correctCount == isCorrectCount) {
					 			testQuestionsAnsDetails.setIsCorrect("Y");
					 		}else {
								testQuestionsAnsDetails.setIsCorrect("N");
							}
					 	}else {
							testQuestionsAnsDetails.setIsCorrect("N");
						}
				
					question.setTestQuestionsAnsDetails(testQuestionsAnsDetails);
			} else {
				UpgradQuestionAnsweredDetailsBean studentAnswer = upgradAssessmentDao.getStudentAnsDetails(testId, sapid, question.getQuestionNo() ); 
				question.setTestQuestionsAnsDetails(studentAnswer);
			}
			
		}
		
		successResponseBean.setUpgradAssessmentBean(upgradAssessmentDao.getStudentIATestDetails(sapid, testId));
		successResponseBean.setUpgradTestQuestionBean(upgradTestQuestionList);
		String acadsMonth = successResponseBean.getUpgradAssessmentBean().getAcadMonth(); 
		Integer acadsYear =  successResponseBean.getUpgradAssessmentBean().getAcadYear();
	
		if( !acadsMonth.equals(CURRENT_MBAX_ACAD_MONTH) && acadsYear != CURRENT_MBAX_ACAD_YEAR) {
			failResponseBean.setCode(421);
			failResponseBean.setStatus("fail");
			return failResponseBean;
		}
		successResponseBean.setCode(200);
		successResponseBean.setStatus("success");
		return successResponseBean;
		
		}catch(Exception e) {
			
			failResponseBean.setCode(422);
			failResponseBean.setStatus("fail");
			return failResponseBean;
		}
	}
	
	public  ArrayList<UpgradAssessmentExamBean>  getStudentAssessmentDetailsService(String sapid, Long testId){
		totalMarksCount = 0;
		ArrayList<UpgradAssessmentExamBean> studentAssessmentDetailsList = new ArrayList<UpgradAssessmentExamBean>();
		try {
			studentAssessmentDetailsList =  upgradAssessmentDao.getStudentAssessmentDetails(testId, sapid);
			for(UpgradAssessmentExamBean beanList :studentAssessmentDetailsList ) {
				if(beanList.getQuestionTypeId() == 1  || beanList.getQuestionTypeId() == 5  ) {
				UpgradAssessmentExamBean upgradAssessmentBean  = upgradAssessmentDao.getIsCorrectMarksForSingleAndTF(beanList.getQuestionNo(), Integer.parseInt(beanList.getStudentAnswer()));
				 beanList.setMarksObtained(upgradAssessmentBean.getMarksObtained());
				 beanList.setStudentAnswer(upgradAssessmentBean.getStudentAnswer());
				 totalMarksCount += Integer.parseInt(beanList.getMarksObtained());
				}else if(beanList.getQuestionTypeId() == 2) {
				 Integer correctCount = upgradAssessmentDao.getCorrectCountForMulti(beanList.getQuestionNo());
				 Integer studentAnsCount = upgradAssessmentDao.getStudentAnswerCountForMulti(beanList.getTestId(), beanList.getQuestionNo(), beanList.getSapid());
				 	
				  if(correctCount == studentAnsCount ) {
				 		Integer isCorrectCount = upgradAssessmentDao.getIsCorrectCountForMulti(beanList.getTestId(), beanList.getQuestionNo(), beanList.getSapid());
				 		if(correctCount == isCorrectCount) {
				 			String  marksObtained = upgradAssessmentDao.getMarksForCorrectMultiAnswer( beanList.getQuestionNo(), beanList.getTestId());	
				 			beanList.setMarksObtained(marksObtained);
				 		}
				 		
				 	}
				  totalMarksCount += Integer.parseInt(beanList.getMarksObtained());
				  
				  
				  List<String> optionDataList = upgradAssessmentDao.getOptionDataForMulti(beanList.getTestId(), beanList.getQuestionNo(), beanList.getSapid());
				  String studentAnswerCommaSepareted = String.join(",", optionDataList);
				  beanList.setStudentAnswer(studentAnswerCommaSepareted);
				}
			}
		}catch(Exception e) {
			
		}
		return studentAssessmentDetailsList;
	}

	/**
	 * Get total expected students count appeared for the test. 
	 * @param testId - holds testId of particular Test.
	 * @return Integer - returns count of students.
	 * @throws Exception throws if any problem occurs while executing logic.
	 */
	public Integer fetchExpectedStudentsForTest(Long testId) throws Exception{
		
		//Fetch total count of students appeared for the Test
		Integer count = upgradAssessmentDao.getExpectedStudentsForTest(testId);
		
		//return count
		return count;
	}

	protected UpgradAssessmentExamBean setPlagiarisedData(UpgradAssessmentExamBean testBean) {
		try {
			String COPY_CASE_REMARK = "Marked For Copy Case";
			String COPY_CASE_ATTEMPT_STATUS = "CopyCase";
			Long COPY_CASE_SCORE = (long) 0;
			testBean.setAttemptStatus(COPY_CASE_ATTEMPT_STATUS);
			testBean.setScore(COPY_CASE_SCORE);
			for(UpgradQuestionAnsweredDetailsBean bean : testBean.getTestQuestionsAnsDetails()) {
				Integer questionTypeId = upgradAssessmentDao.getQuestionTypeById(bean.getQuestionNo(), testBean.getTestId());
				if(questionTypeId  == 4) {
					bean.setRemark(COPY_CASE_REMARK);
				}
			}
			return testBean;	
		}catch (Exception e) {
			// TODO: handle exception
			
			return testBean;
		}
		
	}
	
	
}
