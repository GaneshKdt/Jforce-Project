package com.nmims.services;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;


import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.nmims.beans.BatchExamBean;
import com.nmims.beans.EmbaPassFailBean;
import com.nmims.beans.MettlListResponseBean;
import com.nmims.beans.MettlResponseBean;
import com.nmims.beans.ResponseBean;
import com.nmims.beans.StudentMarksBean;
import com.nmims.beans.StudentSubjectConfigExamBean;
import com.nmims.beans.StudentsTestDetailsExamBean;
import com.nmims.beans.TEEResultBean;
import com.nmims.beans.TEEResultStudentDetailsBean;
import com.nmims.beans.TestExamBean;
import com.nmims.beans.UpgradAssessmentExamBean;
import com.nmims.daos.ExamsAssessmentsDAO;
import com.nmims.daos.StudentMarksDAO;
import com.nmims.daos.TestDAO;
import com.nmims.daos.UpgradAssessmentDao;
import com.nmims.daos.UpgradResultProcessingDao;
import com.nmims.helpers.ExcelHelper;
import com.nmims.helpers.MettlHelper;

@Service("upgradResultProcessingService")
public class UpgradResultProcessingService {
	private static final Logger logger = LoggerFactory.getLogger(UpgradResultProcessingService.class);

	@Autowired
	UpgradAssessmentDao upgradAssessmentDao;
	@Autowired
	UpgradResultProcessingDao upgradResultProcessingDao;
	
	@Autowired
	TestDAO testDao;
	
	private Integer MINIACOUNT = 4; 
	
	private Integer TEEPASSMARKS_WITH_IA = 24; 

	private Integer TEEMAXMARKS_WITH_IA = 60; 
	
	private Integer TEEMAXMARKS_PROJECT_WITH_IA = 75; 
	
	private Integer TEEMAXMARKS = 100; 
	
	private Integer PASS_SCORE = 50;


	public ResponseEntity<HashMap<String, String>> updateMBAXSubjectAsRIANV(String subject,String status,String timebound_id,String sapid,String schedule_id,int prgm_sem_subj_id,String lastModifiedBy) {

		logger.info("update function invoked 6 val key : "+status+" - "+timebound_id+" - "+sapid+" - "+schedule_id+" - "+subject+" - " +prgm_sem_subj_id);
		HashMap<String, String> response = new  HashMap<String, String>();
		
		try{
			TEEResultBean tbean= new TEEResultBean();
			tbean.setSapid(sapid);
			tbean.setSchedule_id(schedule_id);
			tbean.setTimebound_id(timebound_id);
			tbean.setLastModifiedBy(lastModifiedBy);
			tbean.setStatus(status);
			tbean.setPrgm_sem_subj_id(prgm_sem_subj_id);
			List<TEEResultBean>  studentMarks = upgradResultProcessingDao.readMBAXScoreFromTeeMarks(tbean);
			logger.info("studentMarks list size  before ria/nv  "+studentMarks.size());

			if(studentMarks.size()>0){
				for(TEEResultBean  student:studentMarks){
					//if students score status is attempted then store the score in the previous_score column before update.
					if("RIA".equalsIgnoreCase(student.getStatus()) 
							|| "NV".equalsIgnoreCase(student.getStatus()) 
							|| "AB".equalsIgnoreCase(student.getStatus())
							|| "Not Attempted".equalsIgnoreCase(student.getStatus())) {
						continue;
					}
					int j = upgradResultProcessingDao.saveMbaxStudentMarksBeforeRIANV(student);
					if(j<0){
						logger.error("error in upgradResultProcessingDao.saveMbaxStudentMarksBeforeRIANV() ");
						response.put("Status", "Fail"); 
						return new ResponseEntity<HashMap<String, String>>(response, HttpStatus.OK); 
					}
				}
			}
			int rows=0;
			if(status.equalsIgnoreCase("Score")){
				logger.info("Status for update : "+status);
				tbean.setScore(Integer.parseInt(upgradResultProcessingDao.getMbaxStudentPreviousScore(tbean)));
				logger.info("update to previous score start : ");
					rows = upgradResultProcessingDao.updateMbaxSubjectScore(tbean,"Attempted");
					logger.info("update to previous score end : ");
			}else{
				logger.info("Status for update : "+status);
				tbean.setScore(0);
				rows =  upgradResultProcessingDao.updateMbaxSubjectScore(tbean,status); 
			}
			logger.info("total rows updated : "+rows);
			if(rows>0){
				response.put("Status", "Success"); 
				if(status.equalsIgnoreCase("Score")){
					response.put("score", ""+tbean.getScore()); 
				}else {
					response.put("score", status); 
				}
				return new ResponseEntity<HashMap<String, String>>(response, HttpStatus.OK);
			}else{
				logger.info("entered final else condition as rows updated in nil ");
				response.put("Status", "Fail"); 
				response.put("FailReason", "Unable to update tee score.");
				return new ResponseEntity<HashMap<String, String>>(response,HttpStatus.OK);
			}
		}catch(Exception e){
			
			response.put("Status", "Fail"); 
			return new ResponseEntity<HashMap<String, String>>(response, HttpStatus.OK);
		}
	}
	
	
	public HashMap<String,String> getSubjectMap(int key,HttpServletRequest request) {
		HashMap<String,String> subjectList = upgradResultProcessingDao.getSubjectListForMasterKey(key);
		request.getSession().setAttribute("subjectList", subjectList);
		 return subjectList;
	}
	
	public HashMap<String,String> getBatchesMap(int key,HttpServletRequest request) {
		HashMap<String,String> batchList = upgradResultProcessingDao.getBatchListForMasterKey(key);
		request.getSession().setAttribute("batchList", batchList);
		return batchList;
	}
	
	public List<BatchExamBean> getBatchList(int key) {
		List<BatchExamBean> batchList = upgradResultProcessingDao.getBatchesListForMasterKey(key);
//		request.getSession().setAttribute("batchList", batchList);
		return batchList;
	}
	
	/*
	 * public int getTimeboundFromPSSBatch(int pssId,String
	 * batchId,HttpServletRequest request) { int timeboundId =
	 * upgradResultProcessingDao.getTimeBoundIdFromPSSIdBatchId( pssId, batchId);
	 * if(timeboundId == 0){ request.setAttribute("error", "true");
	 * request.setAttribute("errorMessage", "No timeboundId found."); } return
	 * timeboundId; }
	 */
	
	
	/*
	 * public void uploadUpgradAbsentExcel(HttpServletRequest request, TEEResultBean
	 * resultBean) { try{ String userId =
	 * (String)request.getSession().getAttribute("userId"); ExcelHelper excelHelper
	 * = new ExcelHelper(); //get timebound_id
	 * resultBean.setTimebound_id(""+upgradResultProcessingDao.
	 * getTimeBoundIdFromPSSId(resultBean.getPrgm_sem_subj_id())); ArrayList<List>
	 * resultList = excelHelper.readMBAXAbsentExcel(resultBean, userId);
	 * 
	 * List<TEEResultBean> successBeanList =
	 * (ArrayList<TEEResultBean>)resultList.get(0); List<TEEResultBean>
	 * errorBeanList = (ArrayList<TEEResultBean>)resultList.get(1);
	 * 
	 * if(errorBeanList.size() > 0){ request.setAttribute("errorBeanList",
	 * errorBeanList); }else {
	 * 
	 * upgradResultProcessingDao.batchUpdate(successBeanList); if(error == 0){
	 * request.setAttribute("success","true");
	 * request.setAttribute("successMessage",successBeanList.size()+" rows out of "+
	 * successBeanList.size()+" inserted successfully."); }else{
	 * request.setAttribute("error", "true");
	 * request.setAttribute("errorMessage","Error records were NOT inserted."); }
	 * 
	 * } }catch(Exception e){  request.setAttribute("error",
	 * "true"); request.setAttribute("errorMessage",
	 * "Error in inserting marks records."); } }
	 */
	
	public List<TEEResultBean> searchAbsentRecordsMBAX(HttpServletRequest request, TEEResultBean resultBean) {
		List<TEEResultBean> absentStudentList = new ArrayList<TEEResultBean>();
		try{
			String userId = (String)request.getSession().getAttribute("userId");
			String schedule_id;
			if("100".equals(resultBean.getMax_marks())) {
				schedule_id = upgradResultProcessingDao.getMaxMBAXScheduleForTimeBoundId(resultBean.getTimebound_id(), true);
			} else {
				schedule_id = upgradResultProcessingDao.getMaxMBAXScheduleForTimeBoundId(resultBean.getTimebound_id(), false);
			}
			if(!schedule_id.isEmpty()) {
				//get pss_id
				resultBean.setSchedule_id(schedule_id);

				if("100".equals(resultBean.getMax_marks())) {
					absentStudentList = upgradResultProcessingDao.getABRecordsFor100MarksExamMBAX(resultBean);
				} else {
					absentStudentList = upgradResultProcessingDao.getABRecordsMBAX(resultBean);
				}
				 if(absentStudentList == null || absentStudentList.size() == 0){
						request.setAttribute("error", "true");
						request.setAttribute("errorMessage", "No records found.");
					}else{
						for(TEEResultBean bean : absentStudentList) {
							bean.setSchedule_id(schedule_id); // setting schedule id for all absent students, schedule_id =  max schedule_id for the given timeboundID.
						}
						request.setAttribute("success","true");
						request.setAttribute("successMessage","Please download AB report for verification");
					}
				 request.getSession().setAttribute("absentStudentListMBAX", absentStudentList);
			}
		}catch(Exception e){
			
			request.setAttribute("error", "true");
			request.setAttribute("errorMessage", "Error in inserting marks records.");
		}
		return absentStudentList;
	}
	
	
	public int insertUpgradAbsentList(HttpServletRequest request,List<TEEResultBean> studentsList){
		String userId = (String) request.getSession().getAttribute("userId");
		int error =upgradResultProcessingDao.batchUpdate(studentsList,userId);
		if(error > 0){
			 error = upgradResultProcessingDao.batchUpdateForHistoryTable(studentsList,userId);
			 if(error > 0) {
				 request.setAttribute("success","true");

				 request.setAttribute("successMessage",studentsList.size()+" rows out of "+
						 studentsList.size()+" inserted successfully."); 
				}else{
				 request.setAttribute("error", "true");
				 request.setAttribute("errorMessage","Error records were NOT inserted."); 
				 }
		}else{
			 request.setAttribute("error", "true");
			 request.setAttribute("errorMessage","Error records were NOT inserted."); 
			 }
		return error;
	}

	public ArrayList<MettlResponseBean> getMBAXAssessmentsByTimeBoundId(HttpServletRequest request){
		ArrayList<MettlResponseBean> assessmentList = upgradAssessmentDao.getMBAXAssessmentListByTimeBoundId(Integer.parseInt(request.getParameter("id")));
	return assessmentList;
	}

	public List<MettlResponseBean> getMBAXScheduleByTimeBoundId(int assessment_id, int timebound_id){
		List<MettlResponseBean> assessmentList = upgradAssessmentDao.getMBAXScheduleListByAssessmentId(assessment_id, timebound_id);
	return assessmentList;
	}
	public ArrayList<StudentSubjectConfigExamBean> getMBAXSubjectListByBatchId(int batchId){
		ArrayList<StudentSubjectConfigExamBean> subjectList = upgradAssessmentDao.getMBAXSubjectByBatchId(batchId);
	return subjectList;
	}
	
	public ArrayList<TEEResultBean> getAllMBAXStudentNotProcessedList(HttpServletRequest request,TEEResultBean resultBean){
		ArrayList<TEEResultBean> studentsNotProcessed = upgradResultProcessingDao.getAllMBAXStudentNotProcessedList();
		request.getSession().setAttribute("studentsNotProcessed", studentsNotProcessed);
		return studentsNotProcessed;
	}
	
	
	public void mbaPassFailLogic(TEEResultBean resultBean,ArrayList<EmbaPassFailBean> finalListforPassFail,ArrayList<TEEResultBean> studentsListEligibleForPassFail,String loggedInUser,ArrayList<EmbaPassFailBean> unsuccessfulPassFail  ) {
		
		for(TEEResultBean student : studentsListEligibleForPassFail) {
	
			EmbaPassFailBean studentFinalMarks = new EmbaPassFailBean();
			logger.info("sapid= "+student.getSapid()+ " timebound = "+student.getTimebound_id());	
			
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
			logger.info("MAXScore Score = "+student.getMax_score());
			 
			if(student.getStatus().equalsIgnoreCase("Not Attempted")) { // if status is Not Attempted then passfail is not run for that student.
				studentFinalMarks.setFailReason("Status is Not Attempted");
				unsuccessfulPassFail.add(studentFinalMarks);
				studentFinalMarks.setStatus("NA");
				continue;
			}
			
			
			
//MBAX Considering Tests + Project	for IA Total	
			
			// if max score is 40 then
			// get students IA marks for single subject
			// IA marks obtained if showResults to students and showResults flags for all applicable IA is set to Y and IA is not of generic module.
			
//			if(40 == Integer.parseInt(student.getMax_score())) {
//				int project=0;
//				ArrayList<StudentsTestDetailsBean> iaMarks = upgradResultProcessingDao.getMBAXIAScoresForStudentSubject(student.getSapid(),student.getTimebound_id(),"Assignment");  
//				ArrayList<StudentsTestDetailsBean> projectScore = upgradResultProcessingDao.getMBAXIAScoresForStudentSubject(student.getSapid(),student.getTimebound_id(),"Project");  
//				if(projectScore.size()>0) {
//					project=projectScore.get(0).getScore();
//				}
//				studentFinalMarks.setProject(project);
//				boolean calculateIA =true;
//				if(iaMarks.size() > 0) { 
//					for(StudentsTestDetailsBean test : iaMarks) {
//						if(test.getShowResult().equalsIgnoreCase("N") || test.getShowResultsToStudents().equalsIgnoreCase("N") ) {
//							studentFinalMarks.setFailReason("ShowResult/ShowResultsToStudents flag not live");
//							unsuccessfulPassFail.add(studentFinalMarks);
//							studentFinalMarks.setStatus("NA");
//							calculateIA=false;
//						}
//					}
//				}
//				if(calculateIA) {
//					if(iaMarks.size() < 3 && iaMarks.size() > 0) { //for score list having less than 5 entires add all scores to get IA
//						int scoreFor3OrBelowTests=0;
//						for(StudentsTestDetailsBean test : iaMarks) {
//							if(test.getShowResult().equalsIgnoreCase("N") || test.getShowResultsToStudents().equalsIgnoreCase("N") ) {
//								studentFinalMarks.setFailReason("ShowResult/ShowResultsToStudents flag not live");
//								unsuccessfulPassFail.add(studentFinalMarks);
//								studentFinalMarks.setStatus("NA");
//								break;
//							}else {
//								scoreFor3OrBelowTests = scoreFor3OrBelowTests + test.getScore();
//							}
//						}
//						int finalIAScore= scoreFor3OrBelowTests+project;
//						studentFinalMarks.setIaScore(""+finalIAScore);
//						logger.info("IA Score = "+finalIAScore);
//					}else if(iaMarks.size()>=3) {
//						String iaScore = calculateBestOfScoresMBAX(iaMarks,3,project); // //for score list having more than 3 entires  consider best 3  for IA
//						studentFinalMarks.setIaScore(iaScore);
//						logger.info("Best Of 3 IA Score = "+iaScore);
//					}else if(iaMarks.size() ==0) { // if no ia is given then consider only project score.
//						int finalIAScore= project;
//						studentFinalMarks.setIaScore(""+finalIAScore);
//					}
//				}
//			}
			

//MBAX Considering Tests or Project for IA Total
			
			if(TEEMAXMARKS_WITH_IA == Integer.parseInt(student.getMax_score())) {
				int project=0;
				double projectInDecimal=0.0;
				
				//Project and Tests are different IA Types and have their own set of properties
				ArrayList<StudentsTestDetailsExamBean> iaMarks = upgradResultProcessingDao.getMBAXIAScoresForStudentSubject(student.getSapid(),student.getTimebound_id(),"Assignment");  
				ArrayList<StudentsTestDetailsExamBean> projectScore = upgradResultProcessingDao.getMBAXIAScoresForStudentSubject(student.getSapid(),student.getTimebound_id(),"Project");  
				//Consider either IA or Project
				boolean calculateIA=false;
				
				//Project and IA needs to be calculated separately and added to IA total
				if(projectScore.size()>0) {
					projectInDecimal=projectScore.get(0).getScore();
					project = roundDoubleToInt(projectInDecimal);
					studentFinalMarks.setProject(project);
					studentFinalMarks.setIaScore(""+project);
					logger.info("Project Score = "+studentFinalMarks.getIaScore());


				}
				
				else if (iaMarks.size()>0) {
					logger.info("TEST LOOP ");

					calculateIA=true;
					//if(iaMarks.size() > 0) { 
						for(StudentsTestDetailsExamBean test : iaMarks) {
							if(test.getShowResult().equalsIgnoreCase("N") || test.getShowResultsToStudents().equalsIgnoreCase("N") ) {
								studentFinalMarks.setFailReason("ShowResult/ShowResultsToStudents flag not live");
								unsuccessfulPassFail.add(studentFinalMarks);
								studentFinalMarks.setStatus("NA");
								calculateIA=false;
							}
						}
					//}
					if(calculateIA) {
						if(iaMarks.size() <= MINIACOUNT && iaMarks.size() > 0) { //for score list having less than MINIACOUNT entires add all scores to get IA
							int scoreFor4OrBelowTests=0;
							double scoreFor4OrBelowTestsInDecimal=0.0;
							for(StudentsTestDetailsExamBean test : iaMarks) {
								scoreFor4OrBelowTestsInDecimal = scoreFor4OrBelowTestsInDecimal + test.getScore();
							}
							scoreFor4OrBelowTests = roundDoubleToInt(scoreFor4OrBelowTestsInDecimal);
							int finalIAScore= scoreFor4OrBelowTests;
							studentFinalMarks.setIaScore(""+finalIAScore);
							logger.info("IA Score = "+finalIAScore);
						}else if(iaMarks.size()>= MINIACOUNT) {
							String iaScore = calculateBestOfScoresMBAX(iaMarks); // //for score list having more than 3 entires  consider best 3  for IA
							studentFinalMarks.setIaScore(iaScore);
							logger.info("Best Of 4 IA Score = "+iaScore);
						}
					}
				}
				
				
				
			}
			isPassCheckMBAX(studentFinalMarks);//check is student pass or fail
			logger.info("IsPass = "+studentFinalMarks.getIsPass());
			if(!studentFinalMarks.getStatus().equalsIgnoreCase("NA")) {
				finalListforPassFail.add(studentFinalMarks);
			}
		}
		logger.info("total students for passfail table insert = "+finalListforPassFail.size());
	
	}
	
	public ArrayList<TEEResultBean> getEligibleStudentsForPassFailBOPSubject(String timeboundId) {
		ArrayList<TEEResultBean> eligibleList = new ArrayList<TEEResultBean>();
		try {
			eligibleList = upgradResultProcessingDao.getEligibleStudentsForPassFailBOPSubject(Integer.parseInt(timeboundId));
			return eligibleList;
		}catch (Exception e) {
			// TODO: handle exception
			
			logger.error("Error in getEligibleStudentsForPassFailBOPSubject trigger : "+e.getMessage());
			return eligibleList;
		}
	}
	
	public void bopSubjectPassFailLogic(TEEResultBean resultBean,ArrayList<EmbaPassFailBean> finalListforPassFail,
			ArrayList<TEEResultBean> studentsListEligibleForPassFail,
			String loggedInUser,ArrayList<EmbaPassFailBean> unsuccessfulPassFail  ) {
		for(TEEResultBean student : studentsListEligibleForPassFail) {
			EmbaPassFailBean studentFinalMarks = new EmbaPassFailBean();
			logger.info("sapid= "+student.getSapid()+ " timebound = "+student.getTimebound_id());	
			
			studentFinalMarks.setTimeboundId(student.getTimebound_id());
			studentFinalMarks.setSchedule_id(null);
			studentFinalMarks.setSapid(student.getSapid());
			studentFinalMarks.setPrgm_sem_subj_id(student.getPrgm_sem_subj_id());
			studentFinalMarks.setCreatedBy(loggedInUser);
			studentFinalMarks.setLastModifiedBy(loggedInUser);
			
			ArrayList<StudentsTestDetailsExamBean> iaMarks = upgradResultProcessingDao.getMBAXIAScoresForStudentSubject(student.getSapid(),student.getTimebound_id(),"Project");  
			if(iaMarks == null || iaMarks.size() == 0) {
				continue ;
			}else {

				
				if(iaMarks.size() > 1) {
					String bestOfScore = calculateBestOfScoresMBAX(iaMarks, 1, 0);
					StudentsTestDetailsExamBean bean = getStudentBopBestOfTestDetail(iaMarks, bestOfScore);
					if(bean.getAttempt() == 1) {
						studentFinalMarks.setStatus("Attempted");
					}else {
						studentFinalMarks.setStatus("AB");
					}
					studentFinalMarks.setIaScore(bestOfScore);
					studentFinalMarks.setMax_score(""+bean.getMaxScore());
				}else {
					StudentsTestDetailsExamBean bean  = iaMarks.get(0);
					if(bean.getAttempt() == 1) {
						studentFinalMarks.setStatus("Attempted");
					}else {
						studentFinalMarks.setStatus("AB");
					}
					
					studentFinalMarks.setIaScore(""+roundDoubleToInt(bean.getScoreInInteger()));
					studentFinalMarks.setMax_score(""+bean.getMaxScore());
				}
				
				
			}
			
			studentFinalMarks.setTeeScore(null);
			studentFinalMarks.setGrade(null);
			studentFinalMarks.setPoints(null);
			logger.info("bopSubjectPassFailLogic MAXScore Score = "+student.getMax_score());
			
			
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
	
	private StudentsTestDetailsExamBean  getStudentBopBestOfTestDetail(ArrayList<StudentsTestDetailsExamBean> iaMarks, String bestOfScore) {
		for(StudentsTestDetailsExamBean bean : iaMarks ){
			if(bean.getScoreInInteger() == Integer.parseInt(bestOfScore)) {
				return bean;
			}
		}
		return iaMarks.get(0);
	}
	
	private String calculateBestOfScoresMBAX(ArrayList<StudentsTestDetailsExamBean> iaMarks, int limit, int project) {
		String iaScore ="0";
		List<StudentsTestDetailsExamBean>  descScoreSortedList = new LinkedList<>();
		descScoreSortedList.addAll(iaMarks);
		Comparator<StudentsTestDetailsExamBean> compareByScore = new Comparator<StudentsTestDetailsExamBean>() {
			@Override
			public int compare(StudentsTestDetailsExamBean o1, StudentsTestDetailsExamBean o2) {
				return o1.getScoreInInteger().compareTo(o2.getScoreInInteger());
			}
		};
		Collections.sort(descScoreSortedList, compareByScore.reversed());
		if(descScoreSortedList.size() > limit) {
			List<StudentsTestDetailsExamBean> bestAttempts = descScoreSortedList.subList(0, limit);
			int score = 0;
			double scoreInDecimal = 0.0;
			for(StudentsTestDetailsExamBean b : bestAttempts) {
				scoreInDecimal = scoreInDecimal + b.getScore();
			}
			score = roundDoubleToInt(scoreInDecimal);
			iaScore=String.valueOf(score+project); // added project score to IA
		}
		return iaScore;
	}
	
	
	private String calculateBestOfScoresMBAX(ArrayList<StudentsTestDetailsExamBean> iaMarks) {
		String iaScore ="0";
		List<StudentsTestDetailsExamBean>  descScoreSortedList = new LinkedList<>();
		descScoreSortedList.addAll(iaMarks);
		Comparator<StudentsTestDetailsExamBean> compareByScore = new Comparator<StudentsTestDetailsExamBean>() {
			@Override
			public int compare(StudentsTestDetailsExamBean o1, StudentsTestDetailsExamBean o2) {
				return o1.getScoreInInteger().compareTo(o2.getScoreInInteger());
			}
		};
		Collections.sort(descScoreSortedList, compareByScore.reversed());
		if(descScoreSortedList.size() > MINIACOUNT) {
			List<StudentsTestDetailsExamBean> bestAttempts = descScoreSortedList.subList(0, MINIACOUNT);
			int score = 0;
			double scoreInDecimal = 0.0;
			for(StudentsTestDetailsExamBean b : bestAttempts) {
				scoreInDecimal = scoreInDecimal + b.getScore();
			}
			score = roundDoubleToInt(scoreInDecimal);
			iaScore=String.valueOf(score); // added project score to IA
		}
		return iaScore;
	}

	private void isPassCheckMBAX(EmbaPassFailBean studentFinalMarks) {
		
		if(studentFinalMarks.getStatus().equalsIgnoreCase("NA")) {
			
		}else if(studentFinalMarks.getStatus().equalsIgnoreCase("RIA") || studentFinalMarks.getStatus().equalsIgnoreCase("NV") 
				|| studentFinalMarks.getStatus().equalsIgnoreCase("AB") ) {
			studentFinalMarks.setIsPass("N");
			studentFinalMarks.setFailReason("Total less than 50/ Tee score is less than 16");
		}else {
			int maxScore = Integer.parseInt(studentFinalMarks.getMax_score());
			if(TEEMAXMARKS_WITH_IA==maxScore &&  studentFinalMarks.getPrgm_sem_subj_id() != 1806 ) {
				int teeScore = studentFinalMarks.getTeeScore();
				int iaScore = parseIfNumericScoreMBAX(studentFinalMarks.getIaScore());
				int total = teeScore + iaScore;
				if(total>=PASS_SCORE && teeScore>=TEEPASSMARKS_WITH_IA) {
					studentFinalMarks.setIsPass("Y");
				}else {
					studentFinalMarks.setIsPass("N");
					studentFinalMarks.setFailReason("Total less than " + PASS_SCORE +  "/ Tee score is less than " + TEEPASSMARKS_WITH_IA);
				}
			}else if(TEEMAXMARKS==maxScore) {
				int total = studentFinalMarks.getTeeScore();
				if(total >= PASS_SCORE) {
					studentFinalMarks.setIsPass("Y");
				}else {
					studentFinalMarks.setIsPass("N");
					studentFinalMarks.setFailReason("Total less than " + PASS_SCORE);
				}
			} else if(  TEEMAXMARKS_WITH_IA==maxScore &&  studentFinalMarks.getPrgm_sem_subj_id() == 1806 ) {
				// old Capstone Project. Split is 75 TEE, 25 mini project
				// updated the weightage logic for Capstone Subject max marks 40 in Mini Project & 60 max marks in Capstone Simulation from Dec21 acad cycle
				int teeScore = studentFinalMarks.getTeeScore();
				int iaScore = parseIfNumericScoreMBAX(studentFinalMarks.getIaScore());
				int total = teeScore + iaScore;
				
				boolean passInTotalMarks = total>=PASS_SCORE;
				boolean passInTEEMarks = teeScore >= 0; // TODO: Add pass marks condition for TEE in Capstone condition
				if(passInTotalMarks && passInTEEMarks) {
					studentFinalMarks.setIsPass("Y");
				}else {
					studentFinalMarks.setIsPass("N");
					studentFinalMarks.setFailReason("Total less than " + PASS_SCORE);
				}
			}
		}
	}

	private int parseIfNumericScoreMBAX(String score) {
		if (!StringUtils.isBlank(score) && StringUtils.isNumeric(score)) {
			return Integer.parseInt(score);
		}
		return 0;
	}

	public int calculateGraceMarks(EmbaPassFailBean studentFinalMarks) {
		boolean graceEligible = checkIfEligibleForGrace (studentFinalMarks);
		if(graceEligible) {
			if(TEEMAXMARKS_WITH_IA==Integer.parseInt(studentFinalMarks.getMax_score())) {
				int totalMarks = Integer.parseInt(studentFinalMarks.getIaScore()) + studentFinalMarks.getTeeScore();
				if(totalMarks >= PASS_SCORE && studentFinalMarks.getTeeScore() == 22){
					return 2;
				}
				if(totalMarks >= PASS_SCORE && studentFinalMarks.getTeeScore() == 23){
					return 1;
				}
				if(totalMarks == 48 ){
					return 2;
				}
				if(totalMarks == 49 ){
					return 1;
				}
			}
			if(TEEMAXMARKS==Integer.parseInt(studentFinalMarks.getMax_score())) {
				int totalMarks = studentFinalMarks.getTeeScore();
				if(totalMarks == 48 ){
					return 2;
				}
				if(totalMarks == 49 ){
					return 1;
				}
			}
		}else{
			return 0;
		}
		return 0;
	}
	
	
	private boolean checkIfEligibleForGrace (EmbaPassFailBean studentFinalMarks){
		if(StringUtils.isBlank(""+studentFinalMarks.getTeeScore()) || studentFinalMarks.getTeeScore() == 0) {
			return false;
		}
		int totalMarks=0;
		if(TEEMAXMARKS_WITH_IA==Integer.parseInt(studentFinalMarks.getMax_score())) {
			
			int iaScore = parseIfNumericScoreMBAX(studentFinalMarks.getIaScore());
			totalMarks = iaScore + studentFinalMarks.getTeeScore();
			if ((totalMarks > 47 && (studentFinalMarks.getTeeScore() >= 22 && studentFinalMarks.getTeeScore() < TEEPASSMARKS_WITH_IA)) || (studentFinalMarks.getTeeScore() > 23 && (totalMarks > 47 && totalMarks < PASS_SCORE )) ) {
				return true;
			}else{
				return false;
			}
		}else if(TEEMAXMARKS==Integer.parseInt(studentFinalMarks.getMax_score())) {
			totalMarks = studentFinalMarks.getTeeScore();
			if (totalMarks > 47 && totalMarks < PASS_SCORE) {
				return true;
			}else{
				return false;
			} 
		}

		return false;  
	}
	


	public void passFailLogicForProject(TEEResultBean resultBean, ArrayList<EmbaPassFailBean> finalListforPassFail,ArrayList<TEEResultBean> studentsListEligibleForPassFail,String loggedInUser,ArrayList<EmbaPassFailBean> unsuccessfulPassFail  ) {
	
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
			
			if(TEEMAXMARKS_WITH_IA == Integer.parseInt(student.getMax_score()) ) {
				int project=0;
				double projectInDecimal=0.0;
				
				//Get Mini-Project Score
				ArrayList<StudentsTestDetailsExamBean> projectScore = upgradResultProcessingDao.getMBAXIAScoresForStudentSubject(student.getSapid(),student.getTimebound_id(),"Project");  
				
				//Project and IA needs to be calculated separately and added to IA total
				if(projectScore.size()>0) {
					projectInDecimal=projectScore.get(0).getScore();
					project = roundDoubleToInt(projectInDecimal);
					studentFinalMarks.setProject(project);
					studentFinalMarks.setIaScore(""+project);
					logger.info("Project Score = "+studentFinalMarks.getIaScore());
				}
			}
			isPassCheckMBAX(studentFinalMarks);//check is student pass or fail
			logger.info("IsPass = "+studentFinalMarks.getIsPass());
			if(!studentFinalMarks.getStatus().equalsIgnoreCase("NA")) {
				finalListforPassFail.add(studentFinalMarks);
			}
		}
		logger.info("total students for passfail table insert = "+finalListforPassFail.size());
	}
	

	public int calculateProjectGraceMarks(EmbaPassFailBean studentFinalMarks) {
		boolean graceEligible = checkIfEligibleForProjectGrace(studentFinalMarks);
		if(graceEligible) {
			int totalMarks = Integer.parseInt(studentFinalMarks.getIaScore()) + studentFinalMarks.getTeeScore();
			// Grace condition is only 50 marks total for project
			if(totalMarks == 48 ){
				return 2;
			}
			if(totalMarks == 49 ){
				return 1;
			}
		}else{
			return 0;
		}
		return 0;
	}

	private boolean checkIfEligibleForProjectGrace(EmbaPassFailBean studentFinalMarks){
		if(StringUtils.isBlank(""+studentFinalMarks.getTeeScore()) || studentFinalMarks.getTeeScore() == 0) {
			return false;
		}
		int totalMarks=0;
		int iaScore = parseIfNumericScore(studentFinalMarks.getIaScore());
		totalMarks = iaScore + studentFinalMarks.getTeeScore();
		// Grace condition is only 50 marks total for project
		if (totalMarks > 47 && totalMarks < PASS_SCORE) {
			return true;
		}
		return false;  
	}
	
	private int parseIfNumericScore(String score) {
		if (!StringUtils.isBlank(score) && StringUtils.isNumeric(score)) {
			return Integer.parseInt(score);
		}
		return 0;
	}
	private int roundDoubleToInt(double doubleValue) {
		return (int) Math.round(doubleValue);
	}

}
