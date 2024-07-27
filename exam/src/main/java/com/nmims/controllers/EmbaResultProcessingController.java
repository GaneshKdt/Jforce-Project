package com.nmims.controllers;


import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.StringJoiner;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import com.google.common.base.Throwables;
import com.google.gson.JsonObject;
import com.nmims.beans.BatchExamBean;
import com.nmims.beans.EmbaGradePointBean;
import com.nmims.beans.EmbaPassFailBean;
import com.nmims.beans.ExamsAssessmentsBean;
import com.nmims.beans.MettlResponseBean;
import com.nmims.beans.Q7Q8DissertationResultBean;
import com.nmims.beans.StudentExamBean;
import com.nmims.beans.StudentsTestDetailsExamBean;
import com.nmims.beans.TEEResultBean;
import com.nmims.daos.AssignmentsDAO;
import com.nmims.daos.ExamsAssessmentsDAO;
import com.nmims.dto.TEEResultDTO;
import com.nmims.factory.ITimeboundProjectPassFailFactory;
import com.nmims.helpers.ExcelHelper;
import com.nmims.helpers.MettlHelper;
import com.nmims.interfaces.IEmbaPassFailReportService;
import com.nmims.services.EmbaPassFailService;
import com.nmims.services.ITimeboundProjectPassFailService;
import com.nmims.services.PDDMPassFail;
import com.nmims.services.UpgradAssessmentService;
import com.nmims.services.impl.ProjectPassFailMscAIService;
import com.nmims.stratergies.impl.IAComponentPassFailStrategy;
import com.nmims.stratergies.impl.MSCAIMLOpsIAComponentPassFailStrategy;


@Controller
@RequestMapping("/admin")
public class EmbaResultProcessingController  extends BaseController{

	private int MBAWX_TEE_PASS_SCORE = 12;
	private int MBAWX_TEE_PROJECT_PASS_SCORE = 24;
	private int MBAWX_PASS_SCORE = 50;
	
	@Value("${MettlBaseUrl}")
	private String MettlBaseUrl;

	@Value("${MettlPrivateKey}")
	private String MettlPrivateKey;

	@Value("${MettlPublicKey}")
	private String MettlPublicKey;

	@Autowired
	ExamsAssessmentsDAO examsAssessmentsDAO;
	
	@Autowired
	UpgradAssessmentService assessmentService;
	
	@Autowired
	PDDMPassFail pddmPassFail;
	
	@Autowired
	private ProjectPassFailMscAIService projectPassFailMscAI;
	
	@Autowired
	private EmbaPassFailService epfService;

	@Autowired
	private IAComponentPassFailStrategy iaComponentPassFailStrategy;
	
	@Autowired
	private MSCAIMLOpsIAComponentPassFailStrategy mscAIMLOpsIAComponentPassFailStrategy;
	
	@Autowired
	private ITimeboundProjectPassFailFactory timeboundProjectPassFailFactory;
	
	@Autowired
	private ExcelHelper excelHelper;
	
	@Value("#{'${ACAD_YEAR_LIST}'.split(',')}")
	private List<String> ACAD_YEAR_LIST;
	
	@Value("#{'${ACAD_MONTH_LIST}'.split(',')}") 
	private List<String> ACAD_MONTH_LIST; 
	
	@Value("#{'${SAS_EXAM_MONTH_LIST}'.split(',')}")
	private List<String> SAS_EXAM_MONTH_LIST;
	
	@Value("#{'${CURRENT_YEAR_LIST}'.split(',')}")
	private ArrayList<String> currentYearList; 
	
	@Autowired
	AssignmentsDAO asignmentsDAO;
	
	@Autowired
	IEmbaPassFailReportService EmbaPassFailReportService;
	
	public static final Logger pullTimeBoundMettlMarksLogger =LoggerFactory.getLogger("pullTimeBoundMettlMarks");
	public static final Logger timeboundProjectMarksUploadLogger = LoggerFactory.getLogger("timeboundProjectMarksUpload");

	private static final List<String> PRODUCT_TYPES = Arrays.asList("MBA - WX","M.Sc. (AI)");
	
	static ArrayList<BatchExamBean> batchList;
	
	private ArrayList<BatchExamBean> getBatchList() {
		if(batchList == null) {
			batchList = examsAssessmentsDAO.getBatchesListForMbaWx();
		}
		
		return batchList;
	}
	
	@RequestMapping(value="/embaExamResultProcessingChecklist",method={RequestMethod.GET})
	public ModelAndView embaExamResultProcessingChecklist(HttpServletRequest request, HttpServletResponse response){
		if(!checkSession(request, response)){
			redirectToPortalApp(response);
			return null;
		}
		ModelAndView mav=new ModelAndView("embaExamResultsProcessingChecklist");
		return mav;
	}

	//Step 1: Create Assessments and schedules (class = ExamController mapping= /exam/examAssessmentPanelForm)

	//Step 2 : Retrive data from mettl api student and schedule wise

	@RequestMapping(value = "/readMettlMarksFromAPIForm",method = {RequestMethod.GET})
	public ModelAndView readMettlMarksFromAPIForm(@ModelAttribute MettlResponseBean mettlResponseBean,HttpServletRequest request, HttpServletResponse response){
		if(!checkSession(request, response)){
			redirectToPortalApp(response);
			return null;
		}
		ModelAndView mav = new ModelAndView("EmbaReadMettlScore");
		mav.addObject("batches", getBatchList());
		mav.addObject("mettlResponseBean", mettlResponseBean);
		mav.addObject("acadYear", ACAD_YEAR_LIST);
		mav.addObject("acadMonth", ACAD_MONTH_LIST);
		return mav;
	}

	@RequestMapping(value="/readMettlMarksFromAPI",method= {RequestMethod.POST})
	public ModelAndView readMettlMarksFromAPI(HttpServletRequest request,@ModelAttribute MettlResponseBean mettlResponseBean, HttpServletResponse response) {
		if(!checkSession(request, response)){
			redirectToPortalApp(response);
			return null;
		}
		ModelAndView mav=new ModelAndView("EmbaReadMettlScore");
		mav.addObject("acadYear", ACAD_YEAR_LIST);
		mav.addObject("acadMonth", ACAD_MONTH_LIST);
		pullTimeBoundMettlMarksLogger.info("EmbaResultProcessingController.readMettlMarksFromAPI() - START");
		pullTimeBoundMettlMarksLogger.info("Selected Form Values:\n Assessment Id:"+mettlResponseBean.getAssessments_id()+" \n Scheduled Id:"+mettlResponseBean.getSchedule_id()
		+"\n Batch Id:"+mettlResponseBean.getBatchId()+" \n TimeBound Id:"+mettlResponseBean.getTimebound_id());
		
		if(StringUtils.isBlank(""+mettlResponseBean.getAssessments_id()) || StringUtils.isBlank(mettlResponseBean.getSchedule_id()) || StringUtils.isBlank(mettlResponseBean.getBatchId()) || StringUtils.isBlank(mettlResponseBean.getTimebound_id())) {
			request.setAttribute("error", "true");
			request.setAttribute("errorMessage", "Kindly select all field values. All fields are Mandatory");
			return mav;
		}
		//Get program sem subject id mapped to timeBound id.
		int prgm_sem_subj_id = examsAssessmentsDAO.getProgramSemSubjectIdFromTimeboundId(mettlResponseBean.getTimebound_id()); // setting prgm_sem_subj_id from timebound Id
		pullTimeBoundMettlMarksLogger.info("The mapped program sem subject id for timeBoundId '"+mettlResponseBean.getTimebound_id()+"' is:"+prgm_sem_subj_id);
		
		if(prgm_sem_subj_id==0) {
			request.setAttribute("error", "true");
			request.setAttribute("errorMessage", "Program Sem Subject Id not found.");
			pullTimeBoundMettlMarksLogger.info("Program Sem Subject Id not found for:"+mettlResponseBean.getTimebound_id());
			return mav;
		}
		mettlResponseBean.setPrgm_sem_subj_id(prgm_sem_subj_id); 
		String loggedInUser =(String)request.getSession().getAttribute("userId");
		ArrayList<MettlResponseBean> mettlResponseBeanList = new ArrayList<MettlResponseBean>();
		ArrayList<String> studentsNotFound = new ArrayList<String>();
		ArrayList<String> studentsErrorFound = new ArrayList<String>();
		try {
			//using schedule and timebound ids get the scheduleDetails.
			ExamsAssessmentsBean scheduleDetails = examsAssessmentsDAO.getScheduleKeyFromScheduleIdAndTimeBoundId(mettlResponseBean.getSchedule_id(),mettlResponseBean.getTimebound_id());
			pullTimeBoundMettlMarksLogger.info("The schedule access Key is '"+scheduleDetails.getSchedule_accessKey()+"' and max score is '"+scheduleDetails.getMax_score()+"'"
					+" for schedule id:"+mettlResponseBean.getSchedule_id()+" and timeBoundID:"+mettlResponseBean.getTimebound_id());
			
			String scheduleAccessKey = scheduleDetails.getSchedule_accessKey();
			String programType = examsAssessmentsDAO.getProgarmTypeFromScheduleId(mettlResponseBean.getSchedule_id());
			pullTimeBoundMettlMarksLogger.info("The program type for schedule id:"+mettlResponseBean.getSchedule_id()+" is:"+programType);
			
			//get list of all student details for a particular batch and timbound id.
			ArrayList<StudentExamBean> studentDetailsList = new ArrayList<StudentExamBean>();
			if("100".equals(scheduleDetails.getMax_score())) {
				pullTimeBoundMettlMarksLogger.info("getReExamStudentDetails based on batch id, timeBound Id and program type.");
				studentDetailsList = examsAssessmentsDAO.getReExamStudentDetails(mettlResponseBean.getBatchId(),mettlResponseBean.getTimebound_id()); //get all Resit student details for a particular selected batchID and timeBoundID
				pullTimeBoundMettlMarksLogger.info("Re-Exam student details count:"+studentDetailsList.size());
			}else {
				pullTimeBoundMettlMarksLogger.info("isResultProcessed:"+mettlResponseBean.isResultProcessed());
				studentDetailsList = examsAssessmentsDAO.getAllStudentDetailsForBatchAndTimebound(mettlResponseBean.getBatchId(),mettlResponseBean.getTimebound_id(), programType, mettlResponseBean.isResultProcessed()); //get all student details for a particular selected batchID and timeBoundID
				pullTimeBoundMettlMarksLogger.info("Student details count:"+studentDetailsList.size());
			}
			/*
			 * For every student in the list get test results using mettl api.
			 * Test results are obtained for a particular scheduleAccessKey. 
			 * Overall status from mettl = success and status of test = completed then student data inserted with status attempted.
			 * Overall status from mettl = success and status of test != completed then student data inserted with status not attempted.
			 * Overall status from mettl = error then student emailId added in studentsErrorFound list.
			 * Overall response from mettl = null then student added in studentsNotFound list.
			 */
			for(StudentExamBean student:studentDetailsList) {
				MettlHelper mettlHelper = getMettlHelperFromProgramType(programType);
				pullTimeBoundMettlMarksLogger.info("Get single student test status for access key:"+scheduleAccessKey+" and student email:"+student.getEmailId());
				JsonObject jsonResponse = mettlHelper.getSingleStudentTestStatusForASchedule(scheduleAccessKey,student.getEmailId());
				pullTimeBoundMettlMarksLogger.info("Json response for email:"+student.getEmailId()+" is:"+jsonResponse);
				if(jsonResponse != null) {
					String status = jsonResponse.get("status").getAsString();
					MettlResponseBean tmp_responseBean = new MettlResponseBean();
					pullTimeBoundMettlMarksLogger.info("Mettl pull status:"+status+" for student email:"+student.getEmailId());
					if("SUCCESS".equalsIgnoreCase(status)) {
						JsonObject candidateObject = jsonResponse.get("candidate").getAsJsonObject();
						tmp_responseBean.setEmail(candidateObject.get("email").getAsString()); //setting emailID
						tmp_responseBean.setSchedule_id(mettlResponseBean.getSchedule_id()); //setting scheduleId
						tmp_responseBean.setTimebound_id(mettlResponseBean.getTimebound_id());//setting TimeBoundId
						tmp_responseBean.setPrgm_sem_subj_id(mettlResponseBean.getPrgm_sem_subj_id());//setting ProgramSemSubjectId
						JsonObject testObject = candidateObject.get("testStatus").getAsJsonObject();
						
						if("AccessRevoked".equalsIgnoreCase(testObject.get("status").getAsString())) {
							pullTimeBoundMettlMarksLogger.info("AccessRevoked for a student email:"+student.getEmailId());
							studentsErrorFound.add(student.getEmailId());
						} else {
							if("Completed".equalsIgnoreCase(testObject.get("status").getAsString())) {
								JsonObject resultObject = testObject.get("result").getAsJsonObject();
								tmp_responseBean.setTotalMarks(Integer.parseInt(""+Math.round(resultObject.get("totalMarks").getAsDouble()))); //setting total marks
								tmp_responseBean.setStatus("Attempted");
							}else {
								tmp_responseBean.setTotalMarks(0);
								tmp_responseBean.setStatus("Not Attempted");
							}
							tmp_responseBean.setSapid(student.getSapid());//setting sapid
							tmp_responseBean.setStudent_name(student.getFirstName()+" "+student.getLastName()); //setting name
							tmp_responseBean.setCreatedBy(loggedInUser); //auditTrails
							tmp_responseBean.setLastModifiedBy(loggedInUser);//auditTrails
							
							pullTimeBoundMettlMarksLogger.info("Mettl Result Bean Details:"+tmp_responseBean);
							mettlResponseBeanList.add(tmp_responseBean);
						}
					}else if("ERROR".equalsIgnoreCase(status)){
						//need proper error message if student not given the exam
						studentsErrorFound.add(student.getEmailId());
					}

				}else {
					// overall error response 
					studentsNotFound.add(student.getEmailId());
				}
			}
			pullTimeBoundMettlMarksLogger.info("Error Found Students email:"+studentsErrorFound);
			pullTimeBoundMettlMarksLogger.info("Students Not Found email:"+studentsNotFound);
			
			ArrayList<String> errorList = new ArrayList<String>();
			/*
			 * For every student in the mettlResponseBeanList upsert scores in history then in marks table. 
			 * History Table : 1. check if record exists for  sapid - timebound_id - schedule_id -  prgm_sem_subj_id.
			 * 				   2. If not exist then insert else update.
			 * TeeMarks Table: 1. check if record exists for  sapid - prgm_sem_subj_id.
			 * 				   2. If not exist then insert else update.
			 */
			if(!mettlResponseBeanList.isEmpty()) { 
				errorList = examsAssessmentsDAO.upsertTeeMarks(mettlResponseBeanList); 

				if(errorList.isEmpty()) { 
					request.setAttribute("success", "true");
					request.setAttribute("successMessage", "Marks upserted successfully  for "+mettlResponseBeanList.size()+" students"); 
					pullTimeBoundMettlMarksLogger.info("Marks upserted successfully  for "+mettlResponseBeanList.size()+" students");
				}else { 
					request.setAttribute("error", "true");
					request.setAttribute("errorMessage",
							"Failed to upsert marks for "+errorList.size()+" students \n emailIDs : "+errorList);
					pullTimeBoundMettlMarksLogger.info("Failed to upsert marks for "+errorList.size()+" students \n emailIDs : "+errorList);
				}

			}else {
				// error response 
				request.setAttribute("error", "true");
				request.setAttribute("errorMessage", "Null or empty result found for upsert");
				pullTimeBoundMettlMarksLogger.info("mettl responseBean list dosen't contain any records.");
				return mav;
			}
		}
		catch (Exception e) {
			request.setAttribute("error", "true");
			request.setAttribute("errorMessage", e.getMessage());
			pullTimeBoundMettlMarksLogger.error("Error in processing the mettl pull, error message:"+e.getStackTrace());
			
			return mav; 
		}
		pullTimeBoundMettlMarksLogger.info("EmbaResultProcessingController.readMettlMarksFromAPI() - END");
		return mav; 
	} 

	private MettlHelper getMettlHelperFromProgramType(String programType) {
		if("MBA - WX".equals(programType)) {
			return (MettlHelper) act.getBean("mbaWxMettlHelper");
		} else if("M.Sc. (AI & ML Ops)".equals(programType) || "M.Sc. (AI)".equals(programType) ) {
			return (MettlHelper) act.getBean("mscMettlHelper");
		} else if("Modular PD-DM".equals(programType)) {
			return (MettlHelper) act.getBean("pddmMettlHelper");
		} else {
			// fallback
			return (MettlHelper) act.getBean("mbaWxMettlHelper");
		}
	}

	//Step 3: Mark RIA NV Student wise

	@RequestMapping(value = "/markRIANVCasesEmbaForm",method = {RequestMethod.GET})
	public ModelAndView markRIANVCasesEmbaForm(HttpServletRequest request, HttpServletResponse response,@ModelAttribute TEEResultBean resultBean){
		if(!checkSession(request, response)){
			redirectToPortalApp(response);
			return null;
		}
		ModelAndView mav = new ModelAndView("EmbaRiaNv");
		mav.addObject("batches", getBatchList());
		mav.addObject("resultBean", resultBean);
		return mav;
	}


	@RequestMapping(value = "/embaSearchScoresForRIANV",method = {RequestMethod.POST})
	public ModelAndView embaSearchScoresForRIANV(HttpServletRequest request, HttpServletResponse response,@ModelAttribute TEEResultBean resultBean){
		if(!checkSession(request, response)){
			redirectToPortalApp(response);
			return null;
		}
		ModelAndView mav = new ModelAndView("EmbaRiaNv");
		mav.addObject("batches", getBatchList());
		mav.addObject("resultBean", resultBean);
		List<TEEResultBean> teeScores = examsAssessmentsDAO.readScoreFromTeeMarks(resultBean);	//get students scores from teeMarks table
		mav.addObject("teeScores", teeScores);
		mav.addObject("rowCount", teeScores.size());
		return mav;
	}

	//based on check box selected update students score for the subject.
	@RequestMapping(value = "/updateEmbaSubjectAsRIANV",method = {RequestMethod.POST})
	public ResponseEntity<HashMap<String, String>> updateEmbaSubjectAsRIANV(@RequestParam String subject,
			@RequestParam String status,
			@RequestParam String timebound_id,
			@RequestParam String sapid,
			@RequestParam String schedule_id,
			@RequestParam int prgm_sem_subj_id,
			HttpServletRequest request, HttpServletResponse rsponse) throws SQLException{
		if(!checkSession(request, rsponse)){
			redirectToPortalApp(rsponse);
			return null;
		}
		HashMap<String, String> response = new  HashMap<String, String>();
		
		try{
			String lastModifiedBy = (String)request.getSession().getAttribute("userId");
			TEEResultBean tbean= new TEEResultBean();
			tbean.setSapid(sapid);
			tbean.setSchedule_id(schedule_id);
			tbean.setTimebound_id(timebound_id);
			tbean.setLastModifiedBy(lastModifiedBy);
			tbean.setStatus(status);
			tbean.setPrgm_sem_subj_id(prgm_sem_subj_id);
			List<TEEResultBean>  studentMarks = examsAssessmentsDAO.readScoreFromTeeMarks(tbean);

			if(studentMarks.size()>0){
				for(TEEResultBean  student:studentMarks){
					//if students score status is attempted then store the score in the previous_score column before update.
					if("RIA".equalsIgnoreCase(student.getStatus()) 
							|| "NV".equalsIgnoreCase(student.getStatus()) 
							|| "AB".equalsIgnoreCase(student.getStatus())
							|| "Not Attempted".equalsIgnoreCase(student.getStatus())) {
						continue;
					}
					int j = examsAssessmentsDAO.saveEmbaStudentMarksBeforeRIANV(student);
					if(j<0){
						response.put("Status", "Fail"); 
						return new ResponseEntity<HashMap<String, String>>(response, HttpStatus.OK); 
					}
				}
			}
			int rows=0;
			if(status.equalsIgnoreCase("Score")){
				tbean.setScore(Integer.parseInt(examsAssessmentsDAO.getEmbaStudentPreviousScore(tbean)));
				/*
				 * boolean state = NumberUtils.isParsable(status) ;
				 */

				//if(state){

					rows = examsAssessmentsDAO.updateEmbaSubjectScore(tbean,"Attempted");
				//}

			}else{
				tbean.setScore(0);
				rows =  examsAssessmentsDAO.updateEmbaSubjectScore(tbean,status); 
			}
			if(rows>0){
				response.put("Status", "Success"); 
				response.put("score", status); 
				return new ResponseEntity<HashMap<String, String>>(response, HttpStatus.OK);
			}else{
				response.put("Status", "Fail"); 
				response.put("FailReason", "Unable to update tee score.");
				return new ResponseEntity<HashMap<String, String>>(response,HttpStatus.OK);
			}
		}catch(Exception e){
			
			response.put("Status", "Fail"); 
			return new ResponseEntity<HashMap<String, String>>(response, HttpStatus.OK);
		}
	}

	//Step 4: View scores pulled from mettl api

	@RequestMapping(value = "/viewMettlScoresForm",method = {RequestMethod.GET})
	public ModelAndView viewMettlScoresForm(@ModelAttribute TEEResultBean mettlResponseBean,HttpServletRequest request, HttpServletResponse response){
		if(!checkSession(request, response)){
			redirectToPortalApp(response);
			return null;
		}
		ModelAndView mav = new ModelAndView("EmbaViewMettlScore");
		mav.addObject("acadYear", ACAD_YEAR_LIST);
		mav.addObject("acadMonth", ACAD_MONTH_LIST);
		mav.addObject("batches", getBatchList());
		mav.addObject("mettlResponseBean", mettlResponseBean);
		return mav;
	}

	@RequestMapping(value = "/viewMettlScores",method = {RequestMethod.POST})
	public ModelAndView viewMettlScores(HttpServletRequest request, HttpServletResponse response,@ModelAttribute TEEResultBean mettlResponseBean){
		if(!checkSession(request, response)){
			redirectToPortalApp(response);
			return null;
		}
		ModelAndView mav = new ModelAndView("EmbaViewMettlScore");
		mav.addObject("acadYear", ACAD_YEAR_LIST);
		mav.addObject("acadMonth", ACAD_MONTH_LIST);
		mav.addObject("batches", getBatchList());
		mav.addObject("mettlResponseBean", mettlResponseBean);
		ArrayList<TEEResultBean> scoreList =  (ArrayList<TEEResultBean>) examsAssessmentsDAO.readScoreFromTeeMarks( mettlResponseBean);
		mav.addObject("scoreListSize", scoreList.size());
		mav.addObject("scoreList", scoreList);
		request.getSession().setAttribute("scoreList",scoreList);
		return mav;
	}

	@RequestMapping(value = "/embaMettlScoresDownload",method = {RequestMethod.POST})
	public ModelAndView embaMettlScoresDownload(HttpServletRequest request,HttpServletResponse response,@ModelAttribute TEEResultBean resultBean){
		if(!checkSession(request, response)){
			redirectToPortalApp(response);
		}
		ArrayList<TEEResultBean> scoreList = (ArrayList<TEEResultBean>)request.getSession().getAttribute("scoreList");
		return new ModelAndView("embaMettlScoresView","scoreList",scoreList);
	}

	//Step 5: Insert AB Records (class = StudentMarksController  mapping = /insertABRecordsFormMBAWX)

	//Step 6: Run passfail trigger for batch - timeboundId - schedule
	//on opening the passfail page show count and details of all students with processed flag set to N.  
	@RequestMapping(value = "/embaPassFailTriggerForm",method = {RequestMethod.GET})
	public ModelAndView embaPassFailTriggerForm(HttpServletRequest request, HttpServletResponse response,@ModelAttribute TEEResultBean resultBean){
		if(!checkSession(request, response)){
			redirectToPortalApp(response);
			return null;
		}
		ModelAndView mav = new ModelAndView("EmbaPassFailTrigger");
		mav.addObject("acadYear", ACAD_YEAR_LIST);
		mav.addObject("acadMonth", ACAD_MONTH_LIST);
		ArrayList<TEEResultBean> studentsNotProcessed = examsAssessmentsDAO.getAllStudentNotProcessedList();
//		mav.addObject("batches", getBatchList());
		mav.addObject("resultBean", resultBean);
		mav.addObject("studentsNotProcessedSize", studentsNotProcessed.size());
		mav.addObject("studentsNotProcessed", studentsNotProcessed);
		mav.addObject("studentsListEligibleForPassFailSize", 0);
		mav.addObject("unsuccessfulPassFailSize", 0);
		request.getSession().setAttribute("studentsNotProcessed", studentsNotProcessed);
		return mav;
	}
	
	//get students eligible for passfail using Timebound_id - Schedule_id.
	//Run passfail logic on eligible list.
	@RequestMapping(value = "/embaPassFailTriggerSearch",method = {RequestMethod.POST})
	public ModelAndView embaPassFailTriggerSearch(HttpServletRequest request, HttpServletResponse response,@ModelAttribute TEEResultBean resultBean){
		if(!checkSession(request, response)){
			redirectToPortalApp(response);
			return null;
		}
		String loggedInUser =(String)request.getSession().getAttribute("userId");

		ModelAndView mav = new ModelAndView("EmbaPassFailTrigger");
		mav.addObject("acadYear", ACAD_YEAR_LIST);
		mav.addObject("acadMonth", ACAD_MONTH_LIST);
		mav.addObject("batches", getBatchList());
		if(StringUtils.isBlank(resultBean.getAssessments_id()) || StringUtils.isBlank(resultBean.getSchedule_id()) || StringUtils.isBlank(resultBean.getBatchId()) || StringUtils.isBlank(resultBean.getTimebound_id())) {
			request.setAttribute("error", "true");
			request.setAttribute("errorMessage", "Kindly select all field values. All fields are Mandatory");
			return mav;
		}
		mav.addObject("resultBean", resultBean);
		ArrayList<TEEResultBean> studentsListEligibleForPassFail  = new ArrayList<TEEResultBean>(); 
		HashMap<String,ArrayList<TEEResultBean>> scheduleIdStudentListMap = new HashMap<String,ArrayList<TEEResultBean>>();
		int totalStudentsSize=0;
		if("0".equals(resultBean.getAssessments_id())  && "0".equals(resultBean.getSchedule_id()) ) {
			studentsListEligibleForPassFail = epfService.getIAComponentOnlyEligibleStudentsForPassFail(resultBean.getTimebound_id());
		}else {
			if("selectAll".equals(resultBean.getAssessments_id())  && "selectAll".equals(resultBean.getSchedule_id()))
			{
				ArrayList<TEEResultBean> studentsForPassFail  = new ArrayList<TEEResultBean>();
				ArrayList<String> scheduleIdsArr = examsAssessmentsDAO.getAllScheduleBasedOnTimeBoundId(resultBean.getTimebound_id());
				for(int i=0;i<=scheduleIdsArr.size()-1;i++)
				{
					resultBean.setSchedule_id(scheduleIdsArr.get(i));
					studentsForPassFail = examsAssessmentsDAO.getAllStudentsEligibleForPassFail(resultBean);
					totalStudentsSize = totalStudentsSize +studentsForPassFail.size();
					if(studentsForPassFail.size()>0)
					scheduleIdStudentListMap.put(resultBean.getSchedule_id(), studentsForPassFail);
				}
				resultBean.setSchedule_id("selectAll");
			}
			else
			{
				studentsListEligibleForPassFail = examsAssessmentsDAO.getAllStudentsEligibleForPassFail(resultBean); //score cannot be null or blank and status <> 'Not Attempted' or null
			}
		}
		
		if(totalStudentsSize>0)
		mav.addObject("studentsListEligibleForPassFailSize", totalStudentsSize);
		else
		mav.addObject("studentsListEligibleForPassFailSize", studentsListEligibleForPassFail.size());
		ArrayList<EmbaPassFailBean> finalListforPassFail = new ArrayList<EmbaPassFailBean>();
		ArrayList<EmbaPassFailBean> unsuccessfulPassFail = new ArrayList<EmbaPassFailBean>();
		HashMap<String,ArrayList<EmbaPassFailBean>> finalListForAllSchedules = new HashMap<String,ArrayList<EmbaPassFailBean>>();
		HashMap<String,ArrayList<EmbaPassFailBean>> unsuccessfulListForAllSchedules = new HashMap<String,ArrayList<EmbaPassFailBean>>();
		int finalListSize=0;
		int unsuccessfullListSize=0;
		if(studentsListEligibleForPassFail.size()>0 || totalStudentsSize>0) {
			try{
				if("0".equals(resultBean.getAssessments_id())  && "0".equals(resultBean.getSchedule_id()) ) {
					if(studentsListEligibleForPassFail.get(0).getPrgm_sem_subj_id()==1990) {
						//comment as pssid is of master dissertation q7 , for which a  new flow is implemented in DissertationResultProcessingController
						//mscAIMLOpsIAComponentPassFailStrategy.searchPassFail(resultBean, finalListforPassFail, studentsListEligibleForPassFail, loggedInUser, unsuccessfulPassFail);
					}
					else
						iaComponentPassFailStrategy.searchPassFail(resultBean, finalListforPassFail, studentsListEligibleForPassFail, loggedInUser, unsuccessfulPassFail);
				}else {
					if("selectAll".equals(resultBean.getAssessments_id())  && "selectAll".equals(resultBean.getSchedule_id())) {
						 for (Map.Entry<String,ArrayList<TEEResultBean>> entry : scheduleIdStudentListMap.entrySet()) 
						 {
							 ArrayList<EmbaPassFailBean> finalListBasedOnScheduleId = new ArrayList<EmbaPassFailBean>();
							 ArrayList<EmbaPassFailBean> unsuccessfulListBasedOnScheduleId = new ArrayList<EmbaPassFailBean>();
							 ArrayList<TEEResultBean> studentsForPassFail = entry.getValue();
							 if("Modular PD-DM".equals(request.getParameter("programType")) ) {
									pddmPassFail.processPassFail(resultBean,finalListforPassFail,studentsForPassFail,loggedInUser,unsuccessfulPassFail );
								}else {
									embaPassFailLogic(resultBean,examsAssessmentsDAO,finalListforPassFail,studentsForPassFail,loggedInUser,unsuccessfulPassFail );
								}
							 for(EmbaPassFailBean list: finalListforPassFail)
							{
								if(list.getSchedule_id().equalsIgnoreCase(entry.getKey()))
									finalListBasedOnScheduleId.add(list);
							}
							for(EmbaPassFailBean list: unsuccessfulPassFail)
							{
								if(list.getSchedule_id().equalsIgnoreCase(entry.getKey()))
									unsuccessfulListBasedOnScheduleId.add(list);
							}
							finalListSize = finalListSize + finalListBasedOnScheduleId.size();
							unsuccessfullListSize = unsuccessfullListSize + unsuccessfulListBasedOnScheduleId.size();
							if(finalListBasedOnScheduleId.size()>0)
							finalListForAllSchedules.put(entry.getKey(), finalListBasedOnScheduleId);
							if(unsuccessfulListBasedOnScheduleId.size()>0)
							unsuccessfulListForAllSchedules.put(entry.getKey(), unsuccessfulListBasedOnScheduleId);	 
						 }
					}
				else {
					if("Modular PD-DM".equals(request.getParameter("programType")) ) {
						pddmPassFail.processPassFail(resultBean,finalListforPassFail,studentsListEligibleForPassFail,loggedInUser,unsuccessfulPassFail );	
					}else {
						embaPassFailLogic(resultBean,examsAssessmentsDAO,finalListforPassFail,studentsListEligibleForPassFail,loggedInUser,unsuccessfulPassFail );	
						}
					}
				}
			}catch(Exception e) {
				request.setAttribute("error", "true");
				request.setAttribute("errorMessage", "Error in getting students scores");
				
			}
			request.getSession().setAttribute("finalListforPassFail", finalListforPassFail);
			request.setAttribute("success", "true");
			request.setAttribute("successMessage", "Found " + finalListforPassFail.size() + " students for passfail. Kindly check records in Table 1 and run passfail trigger." );
		}else {
			request.setAttribute("error", "true");
			request.setAttribute("errorMessage", "No students found for passfail");
		}
		if(unsuccessfulPassFail.size()>0 && unsuccessfulListForAllSchedules.size()==0) {
			request.setAttribute("error", "true");
			request.setAttribute("errorMessage", "Found " + unsuccessfulPassFail.size() + " students with insufficient data. Kindly check Table 2 below.");
		}
		if(finalListSize>0 || unsuccessfullListSize>0)
		{
			mav.addObject("finalListForAllSchedules",finalListForAllSchedules);
			mav.addObject("unsuccessfulListForAllSchedules",unsuccessfulListForAllSchedules);
			mav.addObject("displayFinalList",1);
			mav.addObject("displayUnsuccessfulList",1);
			
			request.getSession().setAttribute("finalListForAllSchedules", finalListForAllSchedules);
			request.getSession().setAttribute("unsuccessfulListForAllSchedules", unsuccessfulListForAllSchedules);
			if(unsuccessfullListSize>0)
			{
				request.setAttribute("error", "true");
				request.setAttribute("errorMessage", "Found " + unsuccessfullListSize + " students with insufficient data. Kindly check Unsuccessfull List tables for all schedules below." );
			}
			request.setAttribute("successMessage", "Found " + finalListSize + " students for passfail. Kindly check records in Final List tables for all schedules and run passfail trigger." );
			
		}
		mav.addObject("finalListforPassFail", finalListforPassFail);
		mav.addObject("finalListforPassFailSize", finalListforPassFail.size());
		mav.addObject("unsuccessfulPassFail", unsuccessfulPassFail);
		mav.addObject("unsuccessfulPassFailSize", unsuccessfulPassFail.size());
		request.getSession().setAttribute("unsuccessfulPassFail", unsuccessfulPassFail);
		return mav;
	}

	//After the passfail logic is run then only the students present in finalListForPassFail are upserted into mba_passfail and corresponding flags are updated in teeMarks and history table.
	@RequestMapping(value = "/embaPassFailTrigger",method = {RequestMethod.POST})
	public ModelAndView embaPassFailTrigger(HttpServletRequest request,HttpServletResponse response,@ModelAttribute TEEResultBean resultBean) {
		if(!checkSession(request, response)){ 
			redirectToPortalApp(response); 
		} 
		
		ModelAndView mv = new ModelAndView("EmbaPassFailTrigger");
		mv.addObject("acadYear", ACAD_YEAR_LIST);
		mv.addObject("acadMonth", ACAD_MONTH_LIST);
		ArrayList<EmbaPassFailBean> finalListforPassFail =  (ArrayList<EmbaPassFailBean>) request.getSession().getAttribute("finalListforPassFail"); 
		ArrayList<EmbaPassFailBean> unsuccessfulPassFail =  (ArrayList<EmbaPassFailBean>) request.getSession().getAttribute("unsuccessfulPassFail");
		HashMap<String,ArrayList<EmbaPassFailBean>> unsuccessfulListForAllSchedules = (HashMap<String,ArrayList<EmbaPassFailBean>>) request.getSession().getAttribute("unsuccessfulListForAllSchedules");
		
		try {
			
			if(finalListforPassFail.size()>0) {
				
				examsAssessmentsDAO.upsertEmbaPassFail(finalListforPassFail); // upsert passfail table
				request.setAttribute("success", "true"); 
				request.setAttribute("successMessage", "PassFail Trigger Completed For "+finalListforPassFail.size()+" records");
			}else {
				request.setAttribute("error", "true");
				request.setAttribute("errorMessage", "No students found for passfail");
			}
			if(unsuccessfulPassFail.size()>0 && (unsuccessfulListForAllSchedules==null || unsuccessfulListForAllSchedules.size()==0)) {
				request.setAttribute("error", "true");
				request.setAttribute("errorMessage", "Found " + unsuccessfulPassFail.size() + " students with insufficient data. Kindly check Table 2 below." );
				mv.addObject("unsuccessfulPassFail", unsuccessfulPassFail);
				mv.addObject("unsuccessfulPassFailSize", unsuccessfulPassFail.size());
			}
			if(unsuccessfulListForAllSchedules!=null && unsuccessfulListForAllSchedules.size()>0)
			{
				int size=0;
				mv.addObject("unsuccessfulListForAllSchedules",unsuccessfulListForAllSchedules);
				mv.addObject("displayUnsuccessfulList",1);
				for (Map.Entry<String,ArrayList<EmbaPassFailBean>> entry : unsuccessfulListForAllSchedules.entrySet())
				{
					size = size + entry.getValue().size(); 
				}
				request.setAttribute("error", "true");
				request.setAttribute("errorMessage", "Found " + size + " students with insufficient data. Kindly check Unsuccessfull List tables for all schedules below.");
			}

		}catch(Exception e) {
			
			request.setAttribute("error", "true");
			request.setAttribute("errorMessage", "Passfail Trigger Failed to Complete");
		}

//		mv.addObject("batches", getBatchList());
		mv.addObject("resultBean", resultBean);
		ArrayList<TEEResultBean> studentsNotProcessed =   examsAssessmentsDAO.getAllStudentNotProcessedList();
		mv.addObject("studentsNotProcessedSize", studentsNotProcessed.size());
		mv.addObject("studentsNotProcessed", studentsNotProcessed);
		return mv;
	}

	private void embaPassFailLogic(TEEResultBean resultBean,ExamsAssessmentsDAO examsAssessmentsDAO,ArrayList<EmbaPassFailBean> finalListforPassFail,ArrayList<TEEResultBean> studentsListEligibleForPassFail,String loggedInUser,ArrayList<EmbaPassFailBean> unsuccessfulPassFail  ) {
	
		for(TEEResultBean student : studentsListEligibleForPassFail) {
	
			EmbaPassFailBean studentFinalMarks = new EmbaPassFailBean();
			
			studentFinalMarks.setTimeboundId(student.getTimebound_id());
			studentFinalMarks.setSchedule_id(student.getSchedule_id());
			studentFinalMarks.setAssessmentName(student.getAssessmentName());
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
			studentFinalMarks.setConsumerProgramStructureId(student.getConsumerProgramStructureId());
			 
			if(student.getStatus().equalsIgnoreCase("Not Attempted")) { // if status is Not Attempted then passfail is not run for that student.
				studentFinalMarks.setFailReason("Status is Not Attempted");
				unsuccessfulPassFail.add(studentFinalMarks);
				studentFinalMarks.setStatus("NA");
				continue;
			}
			
			// if max score is 30 then
			// get students IA marks for single subject
			// IA marks obtained if showResults to students and showResults flags for all applicable IA is set to Y and IA is not of generic module.
			if(30 == Integer.parseInt(student.getMax_score())) {
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
					if(iaMarks.size() < 8 && iaMarks.size() > 0) { //for score list having less than 8 entires add all scores to get IA
						int scoreFor7OrBelowTests=0;
						double scoreFor7OrBelowTestsInDecimal=0.0;
						for(StudentsTestDetailsExamBean test : iaMarks) {
							if(test.getShowResult().equalsIgnoreCase("N") || test.getShowResultsToStudents().equalsIgnoreCase("N") ) {
								studentFinalMarks.setFailReason("ShowResult/ShowResultsToStudents flag not live");
								unsuccessfulPassFail.add(studentFinalMarks);
								studentFinalMarks.setStatus("NA");
								break;
							}else {
								scoreFor7OrBelowTestsInDecimal = scoreFor7OrBelowTestsInDecimal + test.getScore();
							}
						}
						scoreFor7OrBelowTests = roundDoubleToInt(scoreFor7OrBelowTestsInDecimal);
						studentFinalMarks.setIaScore(""+scoreFor7OrBelowTests);
					}else if(iaMarks.size()>=8) {
						String iaScore = calculateBestOf7Scores(iaMarks); // //for score list having more than 8 entires calculate best of 7 for IA
						studentFinalMarks.setIaScore(iaScore);
					}

				}
				
			} else if( ( 40 == Integer.parseInt(student.getMax_score()) ) && (student.getConsumerProgramStructureId() != 160)) { // 160 is masterkey for mbawx jul22 with updated bestofia
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

					int numberOfSubjectsForBestOf;
					
					List<Integer> pssIdListBestOf2 = new ArrayList<Integer>();
					pssIdListBestOf2.add(1965);
					pssIdListBestOf2.add(2402);
					pssIdListBestOf2.add(2410);
					pssIdListBestOf2.add(2478);
					pssIdListBestOf2.add(2403);
					pssIdListBestOf2.add(2411);
					pssIdListBestOf2.add(2479);
					pssIdListBestOf2.add(2404);
					pssIdListBestOf2.add(2412);
					pssIdListBestOf2.add(2480);
					pssIdListBestOf2.add(2408);
					pssIdListBestOf2.add(2510);
					pssIdListBestOf2.add(2484);
					pssIdListBestOf2.add(2407);
					pssIdListBestOf2.add(2483);
					pssIdListBestOf2.add(2557);
					pssIdListBestOf2.add(2409);
					pssIdListBestOf2.add(2416);
					pssIdListBestOf2.add(2485);
					
					pssIdListBestOf2.add(1980);
					pssIdListBestOf2.add(1981);

				// Added by manasi on 20-08-22 	for best of Q3
					pssIdListBestOf2.add(2486);
					pssIdListBestOf2.add(2417);
					pssIdListBestOf2.add(2489);
					pssIdListBestOf2.add(2420);
					pssIdListBestOf2.add(2488);
					pssIdListBestOf2.add(2419);

					//Added by manasi on 18-08-2022 for best of Q6
					pssIdListBestOf2.add(1982);
					pssIdListBestOf2.add(1984);
					pssIdListBestOf2.add(1985);
					pssIdListBestOf2.add(1986);
					pssIdListBestOf2.add(1983);
					pssIdListBestOf2.add(1989);
					

					
					
					List<Integer> pssIdListBestOf3 = new ArrayList<Integer>();
					pssIdListBestOf3.add(1961);
					pssIdListBestOf3.add(1962);
					pssIdListBestOf3.add(1963);
					pssIdListBestOf3.add(1964);
					pssIdListBestOf3.add(1966);
					pssIdListBestOf3.add(1969);
					pssIdListBestOf3.add(2413);
					pssIdListBestOf3.add(2405);
					pssIdListBestOf3.add(2481);
					pssIdListBestOf3.add(1975);
					
//					pssIdListBestOf3.add(2483);
//					pssIdListBestOf3.add(2557);
//					pssIdListBestOf3.add(2407);
					
					pssIdListBestOf3.add(1987);
					pssIdListBestOf3.add(2499);
					pssIdListBestOf3.add(2494);
					pssIdListBestOf3.add(2505);
					pssIdListBestOf3.add(2500);
										
					if(( pssIdListBestOf2.contains(student.getPrgm_sem_subj_id()) &&  !"973".equals(studentFinalMarks.getTimeboundId()) ) 
							|| "1090".equals(studentFinalMarks.getTimeboundId()) || "1099".equals(studentFinalMarks.getTimeboundId()) 
							|| "1302".equals(studentFinalMarks.getTimeboundId())
							|| "1343".equals(studentFinalMarks.getTimeboundId())
							) {
						numberOfSubjectsForBestOf = 2;
					} else if( pssIdListBestOf3.contains(student.getPrgm_sem_subj_id()) || "973".equals(studentFinalMarks.getTimeboundId()) ) {
						numberOfSubjectsForBestOf = 3;	
					} else {
						
						numberOfSubjectsForBestOf = student.getConsumerProgramStructureId().equals(154) 
															|| student.getConsumerProgramStructureId().equals(155) 
															|| student.getConsumerProgramStructureId().equals(158) ? 2  :  4;
					}

					if(iaMarks.size() <= numberOfSubjectsForBestOf && iaMarks.size() > 0) { //for score list having less than 4 entires add all scores to get IA
						int scoreFor6OrBelowTests=0;
						double scoreFor6OrBelowTestsInDecimal=0.0;
						for(StudentsTestDetailsExamBean test : iaMarks) {
							if(test.getShowResult().equalsIgnoreCase("N") || test.getShowResultsToStudents().equalsIgnoreCase("N") ) {
								studentFinalMarks.setFailReason("ShowResult/ShowResultsToStudents flag not live");
								unsuccessfulPassFail.add(studentFinalMarks);
								studentFinalMarks.setStatus("NA");
								break;
							}else {
								scoreFor6OrBelowTestsInDecimal = scoreFor6OrBelowTestsInDecimal + test.getScore();
							}
						}
						scoreFor6OrBelowTests = roundDoubleToInt(scoreFor6OrBelowTestsInDecimal);
						studentFinalMarks.setIaScore(""+scoreFor6OrBelowTests);
					}else if(iaMarks.size() >= numberOfSubjectsForBestOf) {
						String iaScore = calculateBestOf4ScoresForMSC(iaMarks, numberOfSubjectsForBestOf);
						studentFinalMarks.setIaScore(iaScore);
					}
				}
			
			//added logic for mbawx jul22 with updated bestofIAs by pranit 	
			} else if( ( 40 == Integer.parseInt(student.getMax_score()) ) && (student.getConsumerProgramStructureId() == 160)) { // 160 is masterkey for mbawx jul22 with updated bestofia
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

					int numberOfSubjectsForBestOf=5;

					if(iaMarks.size() <= numberOfSubjectsForBestOf && iaMarks.size() > 0) { //for score list having less than 4 entires add all scores to get IA
						int scoreFor6OrBelowTests=0;
						double scoreFor6OrBelowTestsInDecimal=0.0;
						for(StudentsTestDetailsExamBean test : iaMarks) {
							if(test.getShowResult().equalsIgnoreCase("N") || test.getShowResultsToStudents().equalsIgnoreCase("N") ) {
								studentFinalMarks.setFailReason("ShowResult/ShowResultsToStudents flag not live");
								unsuccessfulPassFail.add(studentFinalMarks);
								studentFinalMarks.setStatus("NA");
								break;
							}else {
								scoreFor6OrBelowTestsInDecimal = scoreFor6OrBelowTestsInDecimal + test.getScore();
							}
						}
						scoreFor6OrBelowTests = roundDoubleToInt(scoreFor6OrBelowTestsInDecimal);
						studentFinalMarks.setIaScore(""+scoreFor6OrBelowTests);
					}else if(iaMarks.size() > numberOfSubjectsForBestOf) {
						String iaScore = calculateBestOfScoreFromMarksListAndNoOfSubjectsForBestOf(iaMarks, numberOfSubjectsForBestOf);
						studentFinalMarks.setIaScore(iaScore);
					}
				}
			}//added logic for mbawx jul22 with updated bestofIAs by pranit 	
			
			isPassCheck(studentFinalMarks);//check is student pass or fail
			if(!studentFinalMarks.getStatus().equalsIgnoreCase("NA")) {
				finalListforPassFail.add(studentFinalMarks);
			}
		}
	
	}

	private String calculateBestOf7Scores(ArrayList<StudentsTestDetailsExamBean> iaMarks) {
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
		if(descScoreSortedList.size() > 7) {
			List<StudentsTestDetailsExamBean> best7Attempts = descScoreSortedList.subList(0, 7);
			int score = 0;
			double scoreInDecimal = 0.0;
			for(StudentsTestDetailsExamBean b : best7Attempts) {
				scoreInDecimal = scoreInDecimal + b.getScore();
			}
			score = roundDoubleToInt(scoreInDecimal);
			iaScore=String.valueOf(score);
		}
		return iaScore;
	}

	private String calculateBestOf4ScoresForMSC(ArrayList<StudentsTestDetailsExamBean> iaMarks, int numberOfSubjectsForBestOf) {
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
		if(descScoreSortedList.size() > numberOfSubjectsForBestOf) {
			List<StudentsTestDetailsExamBean> best4Attempts = descScoreSortedList.subList(0, numberOfSubjectsForBestOf);
			int score = 0;
			double scoreInDecimal = 0.0;
			for(StudentsTestDetailsExamBean b : best4Attempts) {
				scoreInDecimal = scoreInDecimal + b.getScore();
			}
			score = roundDoubleToInt(scoreInDecimal);
			iaScore=String.valueOf(score);
		}
		return iaScore;
	}

	private String calculateBestOfScoreFromMarksListAndNoOfSubjectsForBestOf(ArrayList<StudentsTestDetailsExamBean> iaMarks, int numberOfSubjectsForBestOf) {
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
		if(descScoreSortedList.size() > numberOfSubjectsForBestOf) {
			List<StudentsTestDetailsExamBean> bestAttempts = descScoreSortedList.subList(0, numberOfSubjectsForBestOf);
			int score = 0;
			double scoreInDecimal = 0.0;
			for(StudentsTestDetailsExamBean b : bestAttempts) {
				scoreInDecimal = scoreInDecimal + b.getScore();
			}
			score = roundDoubleToInt(scoreInDecimal);
			iaScore=String.valueOf(score);
		}
		return iaScore;
	}

	private int parseIfNumericScore(String score) {
		if (!StringUtils.isBlank(score) && StringUtils.isNumeric(score)) {
			return Integer.parseInt(score);
		}
		return 0;
	}
	
	private void isPassCheck(EmbaPassFailBean studentFinalMarks) {
	
		if(studentFinalMarks.getStatus().equalsIgnoreCase("NA")) {
		}else if(studentFinalMarks.getStatus().equalsIgnoreCase("RIA") || studentFinalMarks.getStatus().equalsIgnoreCase("NV") 
				|| studentFinalMarks.getStatus().equalsIgnoreCase("AB") ) {
				studentFinalMarks.setIsPass("N");
			 if(Integer.parseInt(studentFinalMarks.getMax_score()) == 30) {
				 studentFinalMarks.setFailReason("Total less than 50/Tee score less than 12");
			 }else if(Integer.parseInt(studentFinalMarks.getMax_score()) == 40) {
				 studentFinalMarks.setFailReason("Total less than 50/Tee score less than 16");
			 }if(Integer.parseInt(studentFinalMarks.getMax_score()) == 60 
			    || Integer.parseInt(studentFinalMarks.getMax_score()) == 80  
			    || Integer.parseInt(studentFinalMarks.getMax_score()) == 100 ) {
				 studentFinalMarks.setFailReason("Total less than 50");
			 }
		}else {
			int maxScore = Integer.parseInt(studentFinalMarks.getMax_score());
			if(30==maxScore) {
				int teeScore = studentFinalMarks.getTeeScore();
				int iaScore = parseIfNumericScore(studentFinalMarks.getIaScore());
				int total = teeScore + iaScore ;
				if(teeScore >= 12 && total >= 50) {
					studentFinalMarks.setIsPass("Y");
				}else {
					studentFinalMarks.setIsPass("N");
					studentFinalMarks.setFailReason("Total less than 50/Tee score less than 12");
				}
			}else if(60==maxScore) {
				int teeScore = studentFinalMarks.getTeeScore();
				int iaScore = parseIfNumericScore(studentFinalMarks.getIaScore());
				int total = teeScore + iaScore ;
				if(total >= 50) {
					studentFinalMarks.setIsPass("Y");
				}else {
					studentFinalMarks.setIsPass("N");
					studentFinalMarks.setFailReason("Total less than 50");
				}
			}else if(80==maxScore) {
				int teeScore = studentFinalMarks.getTeeScore();
				int iaScore = parseIfNumericScore(studentFinalMarks.getIaScore());
				int total = teeScore + iaScore ;
				if(total >= 50) {
					studentFinalMarks.setIsPass("Y");
				}else {
					studentFinalMarks.setIsPass("N");
					studentFinalMarks.setFailReason("Total less than 50");
				}
			}else if(40==maxScore) {
				int teeScore = studentFinalMarks.getTeeScore();
				int iaScore = parseIfNumericScore(studentFinalMarks.getIaScore());
				int total = teeScore + iaScore ;
				if(teeScore >= 16 && total >= 50) {
					studentFinalMarks.setIsPass("Y");
				}else {
					studentFinalMarks.setIsPass("N");
					studentFinalMarks.setFailReason("Total less than 50/Tee score less than 16");
				}
			}else if(100==maxScore) {
				int total = studentFinalMarks.getTeeScore();
				if(total >= 50) {
					studentFinalMarks.setIsPass("Y");
				}else {
					studentFinalMarks.setIsPass("N");
					studentFinalMarks.setFailReason("Total less than 50");
				}
			}
		}
	}


	//Step 7: Apply grace to eligible students

	@RequestMapping(value = "/embaGraceMarksForm",method = {RequestMethod.GET})
	public ModelAndView embaGraceMarksForm(HttpServletRequest request, HttpServletResponse response,@ModelAttribute TEEResultBean resultBean){
		if(!checkSession(request, response)){
			redirectToPortalApp(response);
			return null;
		}
		ModelAndView mav = new ModelAndView("EmbaGrace");
		mav.addObject("acadYear", ACAD_YEAR_LIST);
		mav.addObject("acadMonth", ACAD_MONTH_LIST);
		mav.addObject("batches", getBatchList());
		mav.addObject("resultBean", resultBean);
		return mav;
	}

	@RequestMapping(value = "/embaGraceMarksSearch",method = {RequestMethod.POST})
	public ModelAndView embaGraceMarksSearch(HttpServletRequest request, HttpServletResponse response,@ModelAttribute TEEResultBean resultBean){
		if(!checkSession(request, response)){
			redirectToPortalApp(response);
			return null;
		}
		ModelAndView mav = new ModelAndView("EmbaGrace");
		mav.addObject("acadYear", ACAD_YEAR_LIST);
		mav.addObject("acadMonth", ACAD_MONTH_LIST);
		mav.addObject("batches", getBatchList());
		mav.addObject("resultBean", resultBean);
		if(StringUtils.isBlank(resultBean.getAssessments_id()) || StringUtils.isBlank(resultBean.getSchedule_id()) || StringUtils.isBlank(resultBean.getBatchId()) || StringUtils.isBlank(resultBean.getTimebound_id())) {
			request.setAttribute("error", "true");
			request.setAttribute("errorMessage", "Kindly select all field values. All fields are Mandatory");
			return mav;
		}
		
		//get list of all failed students using timeboundId and scheduleId.
		ArrayList<EmbaPassFailBean> studentsListForGrace = examsAssessmentsDAO.getAllFailedStudentsForGrace(resultBean.getTimebound_id(),resultBean.getSchedule_id());
		if(studentsListForGrace.isEmpty()) {
			request.setAttribute("error", "true");
			request.setAttribute("errorMessage", "No students found for grace");
			return mav;
		}
		String loggedInUser =(String)request.getSession().getAttribute("userId");
		ArrayList<EmbaPassFailBean> finalListforGrace = new ArrayList<EmbaPassFailBean>();
		for(EmbaPassFailBean bean : studentsListForGrace) {
			int grace = calculateGraceMarks(bean);// checking is grace is applicable
			if(grace>0) {
				bean.setGraceMarks(""+grace);
				bean.setLastModifiedBy(loggedInUser);
				finalListforGrace.add(bean);
			}
		}
		mav.addObject("finalListforGraceSize", finalListforGrace.size());
		mav.addObject("finalListforGrace", finalListforGrace);
		if(finalListforGrace.size()>0) {
			request.setAttribute("success", "true");
			request.setAttribute("successMessage", "Found "+finalListforGrace.size()+" students eligible for grace");
		}else {
			request.setAttribute("error", "true");
			request.setAttribute("errorMessage", "No students found for grace");
			return mav;
		}
		request.getSession().setAttribute("finalListforGrace", finalListforGrace);
		return mav;
	}

	@RequestMapping(value = "/embaGraceMarks",method = {RequestMethod.POST})
	public ModelAndView embaGraceMarks(HttpServletRequest request,HttpServletResponse response,@ModelAttribute TEEResultBean resultBean) {
		if(!checkSession(request, response)){ 
			redirectToPortalApp(response); 
		} 
		ModelAndView mv = new ModelAndView("EmbaGrace");
		mv.addObject("acadYear", ACAD_YEAR_LIST);
		mv.addObject("acadMonth", ACAD_MONTH_LIST);
		try {
			ArrayList<EmbaPassFailBean> finalListforGrace  = (ArrayList<EmbaPassFailBean>)request.getSession().getAttribute("finalListforGrace");
			for(EmbaPassFailBean bean : finalListforGrace) { //applying grace adding grace marks to teeScore
				int grace = Integer.parseInt(bean.getGraceMarks());
				int teeScore = bean.getTeeScore();
				int finalTeeScore = teeScore + grace;
				bean.setTeeScore(finalTeeScore);
				bean.setIsPass("Y");
				bean.setFailReason("");
			}
			examsAssessmentsDAO.upsertEmbaPassFail(finalListforGrace); // upsert passfail table for grace marks
			request.setAttribute("success", "true");
			request.setAttribute("successMessage", "Grace Completed For "+finalListforGrace.size()+" records");
		}catch(Exception e) {
			
			request.setAttribute("error", "true");
			request.setAttribute("errorMessage", "Grace Failed to Complete");
		}
		mv.addObject("batches", getBatchList());
		mv.addObject("resultBean", resultBean);
		return mv;
	}

	private int calculateGraceMarks(EmbaPassFailBean studentFinalMarks) {
		boolean graceEligible = checkIfEligibleForGrace (studentFinalMarks);
		if(graceEligible) {
			if(30==Integer.parseInt(studentFinalMarks.getMax_score())) {
				int totalMarks = Integer.parseInt(studentFinalMarks.getIaScore()) + studentFinalMarks.getTeeScore();
				int teeScore = studentFinalMarks.getTeeScore();

				if((totalMarks == 48 && teeScore >= 10) || (totalMarks >= 48 && teeScore == 10)){
					return 2;
				}
				if((totalMarks == 49 && teeScore >= 11) || (totalMarks >= 49 && teeScore == 11)){
					return 1;
				}
			}
			if(40==Integer.parseInt(studentFinalMarks.getMax_score())) {
				int totalMarks = Integer.parseInt(studentFinalMarks.getIaScore()) + studentFinalMarks.getTeeScore();
				int teeScore = studentFinalMarks.getTeeScore();
				if((totalMarks == 48 && teeScore >= 14) || (totalMarks >= 48 && teeScore == 14)){
					return 2;
				}
				if((totalMarks == 49 && teeScore >= 15) || (totalMarks >= 49 && teeScore == 15)){
					return 1;
				}
			}
			if(100==Integer.parseInt(studentFinalMarks.getMax_score())) {
				int totalMarks = studentFinalMarks.getTeeScore();
				//breaks at 38 in assignment and 10 in TEE
				if(totalMarks == 48 ){
					return 2;
				}
				if(totalMarks == 49 ){
					return 1;
				}
			}
			if(70==Integer.parseInt(studentFinalMarks.getMax_score())) {
				int totalMarks = Integer.parseInt(studentFinalMarks.getIaScore()) + studentFinalMarks.getTeeScore();
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
		if(30==Integer.parseInt(studentFinalMarks.getMax_score())) {
			
			int iaScore = parseIfNumericScore(studentFinalMarks.getIaScore());
			totalMarks = iaScore + studentFinalMarks.getTeeScore();
			int teeScore = studentFinalMarks.getTeeScore();
			
			
			if ((totalMarks > 47 && (teeScore >= 10 && teeScore < 12)) || (teeScore > 11 && (totalMarks > 47 && totalMarks < 50 )) ) {
				return true;
			}else{
				return false;
			}
		}else if(100==Integer.parseInt(studentFinalMarks.getMax_score())) {
			totalMarks = studentFinalMarks.getTeeScore();
			if (totalMarks > 47 && totalMarks < 50  ) {
				return true;
			}else{
				return false;
			} 
		}else if(40 == Integer.parseInt(studentFinalMarks.getMax_score())) {
			
			int iaScore = parseIfNumericScore(studentFinalMarks.getIaScore());
			totalMarks = iaScore + studentFinalMarks.getTeeScore();
			int teeScore = studentFinalMarks.getTeeScore();
			
			if ((totalMarks >= 50 && (teeScore >= 14 && teeScore < 16)) || totalMarks == 48 || totalMarks == 49 ) {
				return true;
			}else{
				return false;
			} 
		}else if(70==Integer.parseInt(studentFinalMarks.getMax_score()) && !"Marked Absent in Internal Assessment".equals(studentFinalMarks.getFailReason())) {
			totalMarks = parseIfNumericScore(studentFinalMarks.getIaScore()) + studentFinalMarks.getTeeScore();
			if (totalMarks > 47 && totalMarks < 50  ) {
				return true;
			}else{
				return false;
			} 
		}

		return false;  
	}
	
	//code is commented for requirement of card - [17472_PDDM_Separate_Tab_with_Search_option_for_Search_student_and_search_marks_as_given_in_Retai]
	/*
	 @RequestMapping(value = "/embaPassFailReportForm",method = {RequestMethod.GET})
	public ModelAndView embaPassFailReportForm(HttpServletRequest request, HttpServletResponse response,@ModelAttribute TEEResultBean resultBean){
		if(!checkSession(request, response)){
			redirectToPortalApp(response);
			return null;
		}
		ModelAndView mav = new ModelAndView("EmbaPassFailReport");
		mav.addObject("batches", getBatchList());
		mav.addObject("resultBean", resultBean);
		return mav;
	}

	@RequestMapping(value = "/embaPassFailReport",method = {RequestMethod.POST})
	public ModelAndView embaPassFailReport(HttpServletRequest request, HttpServletResponse response,@ModelAttribute TEEResultBean resultBean){
		if(!checkSession(request, response)){
			redirectToPortalApp(response);
			return null;
		}
		ModelAndView mav = new ModelAndView("EmbaPassFailReport");
		mav.addObject("batches", getBatchList());
		mav.addObject("resultBean", resultBean);
		try {
			ArrayList<EmbaPassFailBean> passFailResultsList = examsAssessmentsDAO.getPassFailResultsForReport(resultBean, getAuthorizedCodes(request));
			mav.addObject("passFailResultsListSize", passFailResultsList.size());
			request.getSession().setAttribute("passFailResultsList",passFailResultsList);
			request.setAttribute("success", "true");
			request.setAttribute("successMessage", "Found "+passFailResultsList.size()+" passfail  records");
		}catch(Exception e) {
			
			request.setAttribute("error", "true");
			request.setAttribute("errorMessage", "Error in generating passfail report");
		}
		return mav;
	}
	  */
	//Step 8: Download /View Pass fail report

	@RequestMapping(value = "/embaPassFailReportForm",method = {RequestMethod.GET})
	public ModelAndView embaPassFailReportForm(HttpServletRequest request, HttpServletResponse response,@ModelAttribute TEEResultDTO resultBean){
		if(!checkSession(request, response)){
			redirectToPortalApp(response);
			return null;
		}
		ModelAndView mav = new ModelAndView("EmbaPassFailReport");
		//mav.addObject("batches", getBatchList());
		mav.addObject("resultBean", resultBean);
		mav.addObject("currentMonthList", SAS_EXAM_MONTH_LIST);
		mav.addObject("currentYearList", currentYearList);
		//mav.addObject("consumerType", asignmentsDAO.getConsumerTypeList());
		return mav;
	}

	@RequestMapping(value = "/embaPassFailReport",method = {RequestMethod.POST})
	public ModelAndView embaPassFailReport(HttpServletRequest request, HttpServletResponse response,@ModelAttribute TEEResultDTO resultBean){
		if(!checkSession(request, response)){
			redirectToPortalApp(response);
			return null;
		}
		ModelAndView mav = new ModelAndView("EmbaPassFailReport");
		mav.addObject("resultBean", resultBean);
		mav.addObject("currentMonthList", SAS_EXAM_MONTH_LIST);
		mav.addObject("currentYearList", currentYearList);
		try {
			ArrayList<EmbaPassFailBean> passFailResultsList=EmbaPassFailReportService.searchEmbaMarks(resultBean, getAuthorizedCodes(request));
			//ArrayList<EmbaPassFailBean> passFailResultsList = examsAssessmentsDAO.getPassFailResultForReport(resultBean, getAuthorizedCodes(request),masterKeys);
			Q7Q8DissertationResultBean dissertionReportBean = EmbaPassFailReportService.getDissertionReport(resultBean, getAuthorizedCodes(request));
			dissertionReportBean.setPassFailResultsList(passFailResultsList);
			mav.addObject("passFailResultsListSize", passFailResultsList.size());
			mav.addObject("Q7ResultList", dissertionReportBean.getQ7ResultList().size());
			mav.addObject("Q8ResultList", dissertionReportBean.getQ8ResultList().size());
			request.getSession().setAttribute("dissertionReportBean",dissertionReportBean);
			request.setAttribute("success", "true");
			request.setAttribute("successMessage", "Found "+passFailResultsList.size()+" passfail  records" + ", 		Found Masters Dissertation Part - I "
			+dissertionReportBean.getQ7ResultList().size()+" passfail  records, "  + "		Found Masters Dissertation Part - II "
					+dissertionReportBean.getQ8ResultList().size()+" passfail  records ");
		}catch(Exception e) {
			pullTimeBoundMettlMarksLogger.error("Error : in generating passfail report message :: ",Throwables.getStackTraceAsString(e));
			request.setAttribute("error", "true");
			request.setAttribute("errorMessage", "Error in generating passfail report");
		}
		return mav;
	}

	@RequestMapping(value = "/embaPassFailReportDownload",method = {RequestMethod.POST})
	public ModelAndView embaPassFailReportDownload(HttpServletRequest request,HttpServletResponse response,@ModelAttribute TEEResultBean resultBean){
		if(!checkSession(request, response)){
			redirectToPortalApp(response);
		}
		Q7Q8DissertationResultBean dissertionReportBean = (Q7Q8DissertationResultBean)request.getSession().getAttribute("dissertionReportBean");
		return new ModelAndView("embaPassFailReportView","dissertionReportBean",dissertionReportBean);
	}
	
	//Step 9: Make Results live schedule wise
	@RequestMapping(value = "/embaPassFailMakeLiveForm",method = {RequestMethod.GET})
	public ModelAndView embaPassFailMakeLiveForm(HttpServletRequest request, HttpServletResponse response,@ModelAttribute TEEResultBean resultBean){
		if(!checkSession(request, response)){
			redirectToPortalApp(response);
			return null;
		}
		ModelAndView mav = new ModelAndView("EmbaPassFailMakeLive");
		mav.addObject("acadYear", ACAD_YEAR_LIST);
		mav.addObject("acadMonth", ACAD_MONTH_LIST);
		mav.addObject("batches", getBatchList());
		mav.addObject("resultBean", resultBean);
		return mav;
	}
	@RequestMapping(value = "/embaPassFailMakeLive",method = {RequestMethod.POST})
	public ModelAndView embaPassFailMakeLive(HttpServletRequest request, HttpServletResponse response,@ModelAttribute TEEResultBean resultBean){
		if(!checkSession(request, response)){
			redirectToPortalApp(response);
			return null;
		}
		ModelAndView mav = new ModelAndView("EmbaPassFailMakeLive");
		mav.addObject("acadYear", ACAD_YEAR_LIST);
		mav.addObject("acadMonth", ACAD_MONTH_LIST);
		mav.addObject("batches", getBatchList());
		mav.addObject("resultBean", resultBean);
		if(StringUtils.isBlank(resultBean.getAssessments_id()) || StringUtils.isBlank(resultBean.getSchedule_id()) || StringUtils.isBlank(resultBean.getBatchId()) || StringUtils.isBlank(resultBean.getTimebound_id())) {
			request.setAttribute("error", "true");
			request.setAttribute("errorMessage", "Kindly select all field values. All fields are Mandatory");
			return mav;
		}
		try {
			int count = 0;
			if("0".equals(resultBean.getAssessments_id())  && "0".equals(resultBean.getSchedule_id()) ) {
				count = epfService.passFailResultsLiveForIAComponentSubject(resultBean.getTimebound_id());
			}else {
				count = examsAssessmentsDAO.passFailResultsForEmbaLive(resultBean);
			}
			request.setAttribute("success", "true");
			request.setAttribute("successMessage", "Result made live for "+count+" records");
		}catch(Exception e) {
			request.setAttribute("error", "true");
			request.setAttribute("errorMessage", "Error in make live for schedule id "+resultBean.getSchedule_id());
		}
		return mav;
	}

	@RequestMapping(value = "/embaAbsoluteGradingForm",method = {RequestMethod.GET})
	public ModelAndView embaAbsoluteGradingForm(HttpServletRequest request, HttpServletResponse response,@ModelAttribute TEEResultBean resultBean){
		if(!checkSession(request, response)){
			redirectToPortalApp(response);
			return null;
		}
		ModelAndView mav = new ModelAndView("AbsoluteGrading");
		mav.addObject("batches", getBatchList());
		mav.addObject("acadsYearList", ACAD_YEAR_LIST);
		mav.addObject("acadsMonthList", ACAD_MONTH_LIST);
		mav.addObject("resultBean", resultBean);
		return mav;
	}
	
	@RequestMapping(value = "/embaAbsoluteGrading",method = {RequestMethod.POST})
	public ModelAndView embaAbsoluteGrading(HttpServletRequest request,HttpServletResponse response,@ModelAttribute TEEResultBean resultBean) {
		if(!checkSession(request, response)){ 
			redirectToPortalApp(response); 
		} 		
		
		String userId = (String) request.getSession().getAttribute("userId");
		ModelAndView mav = new ModelAndView("AbsoluteGrading");
		mav.addObject("batches", getBatchList());
		mav.addObject("acadsYearList", ACAD_YEAR_LIST);
		mav.addObject("acadsMonthList", ACAD_MONTH_LIST);
		mav.addObject("resultBean", resultBean);

		String commaSeparatedTimeBoundIds = assessmentService.getTimeBoundIdsByBatchIdAndAcadYearAndAcadMonth(resultBean);
		
		try {
			List<EmbaPassFailBean> embaPassFailData =  examsAssessmentsDAO.getEmbaPassFailByTimeBoundId(commaSeparatedTimeBoundIds);
			List<EmbaPassFailBean> updatedEmbaPassFailList = new ArrayList<EmbaPassFailBean>();
			
			if(embaPassFailData != null && embaPassFailData.size() > 0 ){
	
				for (EmbaPassFailBean studentData : embaPassFailData) {
					if("Y".equals(studentData.getIsPass())) {
					int totalScore = Integer.parseInt(studentData.getTotal());
					
					EmbaGradePointBean gradePointData = examsAssessmentsDAO.getGradeAndPointByTotalScore(totalScore);	
					studentData.setGrade(gradePointData.getGrade());
					studentData.setPoints(gradePointData.getPoints());
					}else {
						studentData.setGrade("F");
						studentData.setPoints("0");
					}
					updatedEmbaPassFailList.add(studentData);
					
				}
				
				examsAssessmentsDAO.updateEmbaGradeAndPointsList(updatedEmbaPassFailList, userId);
				request.setAttribute("success", "true");
				request.setAttribute("successMessage", "Grade Points Update For "+embaPassFailData.size()+" records");
			}else {
				request.setAttribute("error", "true");
				request.setAttribute("errorMessage", "No Records Found");
			}
		}catch(Exception e) {
			
			request.setAttribute("error", "true");
			request.setAttribute("errorMessage", "Grade Points Failed to Complete");
		}
			
		List<EmbaPassFailBean> embaPassFailList =  examsAssessmentsDAO.getEmbaPassFailByTimeBoundId(commaSeparatedTimeBoundIds);		
		
		mav.addObject("embaPassFailListSize", embaPassFailList.size());
		mav.addObject("embaPassFailList", embaPassFailList);
		request.getSession().setAttribute("embaPassFailList", embaPassFailList);
		
		

		
		
		return mav;
	}
	
	@RequestMapping(value="/downloadEmbaAbsoluteGrading", method=RequestMethod.GET)
	public ModelAndView downloadEmbaAbsoluteGrading(HttpServletRequest request, HttpServletResponse response) {
		if(!checkSession(request, response)){
			return new ModelAndView("studentPortalRediret");
		}
		List<EmbaPassFailBean> embaPassFailList= (List<EmbaPassFailBean>)request.getSession().getAttribute("embaPassFailList");
		
		return new ModelAndView("embaPassFailExcelView","embaPassFailList",embaPassFailList);		
	}
	
	@RequestMapping(value="/uploadProjectMarksFormMBAWX",method=RequestMethod.GET)
	public ModelAndView uploadTEEMarksForm(HttpServletRequest request,HttpServletResponse response) {
		if(!checkSession(request, response)){
			redirectToPortalApp(response);
			return null;
		}
		ModelAndView mv = new ModelAndView("uploadProjectMarksMBAWX");
		mv.addObject("acadYear", ACAD_YEAR_LIST);
		mv.addObject("acadMonth", ACAD_MONTH_LIST);
		ExamsAssessmentsDAO examsAssessmentsDAO = (ExamsAssessmentsDAO)act.getBean("examsAssessmentsDAO");
		
		mv.addObject("fileBean", new TEEResultBean());
		//mv.addObject("batches", getBatchList());
		mv.addObject("tee_marks", examsAssessmentsDAO.getTeeMarksStudentListForCapstone());
		return mv;
	}
	
	@RequestMapping(value="/uploadProjectMarksMBAWX",method=RequestMethod.POST)
	public ModelAndView uploadTEEMarks(TEEResultBean teeResultBean, HttpServletRequest request,HttpServletResponse response) {
		if(!checkSession(request, response)){
			redirectToPortalApp(response);
			return null;
		}
		String userId = (String)request.getSession().getAttribute("userId");
		teeResultBean.setCreatedBy(userId);
		teeResultBean.setLastModifiedBy(userId);

		ModelAndView mv = new ModelAndView("uploadProjectMarksMBAWX");
		mv.addObject("acadYear", ACAD_YEAR_LIST);
		mv.addObject("acadMonth", ACAD_MONTH_LIST);
		mv.addObject("fileBean", teeResultBean);
		//mv.addObject("batches", getBatchList());
		
		MettlHelper mettlHelper = (MettlHelper) act.getBean("mbaWxMettlHelper");
		try {

			ExamsAssessmentsDAO examsAssessmentsDAO = (ExamsAssessmentsDAO)act.getBean("examsAssessmentsDAO");
			
			ArrayList<MettlResponseBean> marksToInsert = mettlHelper.getMarksToUpsertForProject(teeResultBean);

			if(marksToInsert != null) {

				List<String> errorList = examsAssessmentsDAO.upsertTeeMarksForProjectSubject(marksToInsert);
				
				if(errorList.isEmpty()) {
					return runPassFailTriggerForProject(teeResultBean, request, mv);
				}else { 
					request.setAttribute("error", "true");
					request.setAttribute("errorMessage", "Failed to upsert marks for "+errorList.size()+" students.\nSapids : "+errorList); 
				}
			} else {
				request.setAttribute("error", "true");
				request.setAttribute("errorMessage", "Error reading marks for from file!"); 
			}

			mv.addObject("tee_marks", examsAssessmentsDAO.getTeeMarksStudentListForCapstone());
		}catch (Exception e) {
			
		}
		return mv;
	}
	
	public ModelAndView runPassFailTriggerForProject(TEEResultBean resultBean, HttpServletRequest request, ModelAndView mav){
		
		String loggedInUser =(String)request.getSession().getAttribute("userId");

		ArrayList<TEEResultBean> studentsListEligibleForPassFail = examsAssessmentsDAO.getAllStudentsEligibleForProjectForPassFail(resultBean); //score cannot be null or blank and status <> 'Not Attempted' or null
		
		mav.addObject("studentsListEligibleForPassFailSize", studentsListEligibleForPassFail.size());
		ArrayList<EmbaPassFailBean> finalListforPassFail = new ArrayList<EmbaPassFailBean>();
		ArrayList<EmbaPassFailBean> unsuccessfulPassFail = new ArrayList<EmbaPassFailBean>();
		if(studentsListEligibleForPassFail.size()>0) {
			try{
				if("MBA - WX".equals(resultBean.getProgramType()))
					embaPassFailLogicForProject(resultBean,examsAssessmentsDAO,finalListforPassFail,studentsListEligibleForPassFail,loggedInUser,unsuccessfulPassFail );
				else if("M.Sc. (AI)".equals(resultBean.getProgramType()) || "M.Sc. (AI & ML Ops)".equals(resultBean.getProgramType()))
						projectPassFailMscAI.projectPassFailLogicForMscAI(resultBean,examsAssessmentsDAO,finalListforPassFail,studentsListEligibleForPassFail,loggedInUser,unsuccessfulPassFail );
				
			}catch(Exception e) {
				request.setAttribute("error", "true");
				request.setAttribute("errorMessage", "Error in getting students scores");
				
			}
			request.getSession().setAttribute("finalListforPassFail", finalListforPassFail);
			request.setAttribute("success", "true");
			request.setAttribute("successMessage", "Found " + finalListforPassFail.size() + " students for passfail. Kindly check records in Table 1 and run passfail trigger." );
		}else {
			request.setAttribute("error", "true");
			request.setAttribute("errorMessage", "No students found for passfail");
		}
		if(unsuccessfulPassFail.size()>0) {
			request.setAttribute("error", "true");
			request.setAttribute("errorMessage", "Found " + unsuccessfulPassFail.size() + " students with insufficient data. Kindly check Table 2 below." );
		}
		mav.addObject("finalListforPassFail", finalListforPassFail);
		mav.addObject("finalListforPassFailSize", finalListforPassFail.size());
		mav.addObject("unsuccessfulPassFail", unsuccessfulPassFail);
		mav.addObject("unsuccessfulPassFailSize", unsuccessfulPassFail.size());
		request.getSession().setAttribute("unsuccessfulPassFail", unsuccessfulPassFail);
		return mav;
	}
	


	private void embaPassFailLogicForProject(TEEResultBean resultBean,ExamsAssessmentsDAO examsAssessmentsDAO,ArrayList<EmbaPassFailBean> finalListforPassFail,ArrayList<TEEResultBean> studentsListEligibleForPassFail,String loggedInUser,ArrayList<EmbaPassFailBean> unsuccessfulPassFail  ) {
	
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
			
			// if max score is 30 then
			// get students IA marks for single subject
			// IA marks obtained if showResults to students and showResults flags for all applicable IA is set to Y and IA is not of generic module.
			if(60 == Integer.parseInt(student.getMax_score())) {
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
				
			} else if(80 == Integer.parseInt(student.getMax_score())) {
				ArrayList<StudentsTestDetailsExamBean> iaMarks = examsAssessmentsDAO.getIAScoresForStudentSubject(student.getSapid(),student.getTimebound_id());  
				boolean calculateIA = true;
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
					iaScore=roundDoubleToInt(iaScoreInDecimal);
					studentFinalMarks.setIaScore("" + iaScore);

				}
				
			} else {
			}
			
			isPassCheck(studentFinalMarks);//check is student pass or fail
			if(!studentFinalMarks.getStatus().equalsIgnoreCase("NA")) {
				finalListforPassFail.add(studentFinalMarks);
			}
		}
	
	}
	

	//After the passfail logic is run then only the students present in finalListForPassFail are upserted into mba_passfail and corresponding flags are updated in teeMarks and history table.
	@RequestMapping(value = "/embaPassFailTriggerForProject",method = {RequestMethod.POST})
	public ModelAndView embaPassFailTriggerForProject(HttpServletRequest request,HttpServletResponse response,@ModelAttribute TEEResultBean resultBean) {
		if(!checkSession(request, response)){ 
			redirectToPortalApp(response); 
		} 
		
		ModelAndView mv = new ModelAndView("uploadProjectMarksMBAWX");
		mv.addObject("acadYear", ACAD_YEAR_LIST);
		mv.addObject("acadMonth", ACAD_MONTH_LIST);
		mv.addObject("fileBean", new TEEResultBean());
		mv.addObject("batches", getBatchList());
		
		ArrayList<EmbaPassFailBean> finalListforPassFail =  (ArrayList<EmbaPassFailBean>) request.getSession().getAttribute("finalListforPassFail"); 
		ArrayList<EmbaPassFailBean> unsuccessfulPassFail =  (ArrayList<EmbaPassFailBean>) request.getSession().getAttribute("unsuccessfulPassFail");
		try {
			
			if(finalListforPassFail.size()>0) {
				
				examsAssessmentsDAO.upsertEmbaPassFail(finalListforPassFail); // upsert passfail table
				request.setAttribute("success", "true"); 
				request.setAttribute("successMessage", "Marks uploaded & PassFail Trigger Completed For "+finalListforPassFail.size()+" records");
			}else {
				request.setAttribute("error", "true");
				request.setAttribute("errorMessage", "No students found for passfail");
			}
			if(unsuccessfulPassFail.size()>0) {
				request.setAttribute("error", "true");
				request.setAttribute("errorMessage", "Found " + unsuccessfulPassFail.size() + " students with insufficient data. Kindly check table 2 below." );
				mv.addObject("unsuccessfulPassFail", unsuccessfulPassFail);
				mv.addObject("unsuccessfulPassFailSize", unsuccessfulPassFail.size());
			}
		}catch(Exception e) {
			
			request.setAttribute("error", "true");
			request.setAttribute("errorMessage", "Passfail Trigger Failed to Complete");
		}

		mv.addObject("batches", getBatchList());
		mv.addObject("resultBean", resultBean);
		return mv;
	}
	
	@GetMapping("/uploadCapstoneProjectMarksForm")
	public ModelAndView uploadCapstoneProjectMarksForm(HttpServletRequest request,HttpServletResponse response) {
		//Check session is alive else redirect to login page
		if(!checkSession(request, response)){
			redirectToPortalApp(response);
			return null;
		}
		timeboundProjectMarksUploadLogger.info("EmbaResultProcessingController.uploadCapstoneProjectMarksForm() - START");
		List<TEEResultBean> capstone_project_marks = null;
		ModelAndView mv = new ModelAndView("uploadCapstoneProjectMarks");
		mv.addObject("acadYear", ACAD_YEAR_LIST);
		mv.addObject("acadMonth", ACAD_MONTH_LIST);
		//Create empty list
		capstone_project_marks = new ArrayList<TEEResultBean>();
		
		//Get service instance based on the product type.
		ITimeboundProjectPassFailService timeboundProjectPassFailService = timeboundProjectPassFailFactory
				.getTimeboundProjectPassFailProcessingInstance(ITimeboundProjectPassFailFactory.MBAWX);
		
		try {
			//Get time bound student capstone project marks
			capstone_project_marks = timeboundProjectPassFailService.getTimeboundStudentProjectMarks("Capstone Project", null);
		}catch(Exception e){
			timeboundProjectMarksUploadLogger.error("Error in getting project proccessed student data. Error:"+e.getStackTrace());
		}
		//Set empty teeResultBean as modelAttribute and list of project marks in modelAndView
		mv.addObject("teeResultBean", new TEEResultBean());
		mv.addObject("productTypes",PRODUCT_TYPES);
		mv.addObject("capstone_project_marks", capstone_project_marks);
		
		//Set list of project marks and size of list in session scope.
		request.getSession().setAttribute("capstone_project_marks", capstone_project_marks);
		request.getSession().setAttribute("capstone_project_marks_size", capstone_project_marks.size());
		timeboundProjectMarksUploadLogger.info("EmbaResultProcessingController.uploadCapstoneProjectMarksForm() - END");
		//return modelAndView
		return mv;
	}//uploadCapstoneProjectMarksForm(-,-)
	
	@PostMapping("/uploadCapstoneProjectMarks")
	public ModelAndView uploadCapstoneProjectMarks(HttpServletRequest request,HttpServletResponse response, @ModelAttribute TEEResultBean teeResultBean ) {
		//Check session is alive else redirect to login page
		if(!checkSession(request, response)){
			redirectToPortalApp(response);
			return null;
		}
		timeboundProjectMarksUploadLogger.info("EmbaResultProcessingController.uploadCapstoneProjectMarks() - START");
		ModelAndView mv = new ModelAndView("uploadCapstoneProjectMarks");
		mv.addObject("acadYear", ACAD_YEAR_LIST);
		mv.addObject("acadMonth", ACAD_MONTH_LIST);
		ITimeboundProjectPassFailService timeboundProjectPassFailService = null;
		List<TEEResultBean> capstone_project_marks = null;
		List<String> errorList = null;
		
		//Get currently logged in user's id.
		String userId = (String)request.getSession().getAttribute("userId");
		timeboundProjectMarksUploadLogger.info("Selected form values:\nProgram type:"+teeResultBean.getProgramType()+"\nBatchId:"+teeResultBean.getBatchId()
			+"\nTimeboundId:"+teeResultBean.getTimebound_id());
		
		//Create empty lists
		capstone_project_marks = new ArrayList<TEEResultBean>();
		errorList =  new ArrayList<>();
		
		//Set currently logged-in userId for audit trial.
		teeResultBean.setCreatedBy(userId);
		teeResultBean.setLastModifiedBy(userId);
		timeboundProjectMarksUploadLogger.info("Currently logged-in userId:"+userId);
		
		//Get service instance based on the product type.
		timeboundProjectPassFailService = timeboundProjectPassFailFactory
				.getTimeboundProjectPassFailProcessingInstance(teeResultBean.getProgramType());

		try {
			//Read uploaded excel file and prepare bean's list 
			List<TEEResultBean> marksToInsert = excelHelper.getMarksToUpsertForProject(teeResultBean, errorList);
			//Start inserting the project marks if no error found while reading the uploaded students marks in excel else give error. 
			if(errorList.isEmpty() && marksToInsert.size()>0) {
				//Insert or update students project marks. 
				errorList = timeboundProjectPassFailService.upsertTimeboundStudentProjectMarks(marksToInsert);
				
				//Start pass-fail process if no error occurred while inserting the project marks else give error. 
				if(errorList.isEmpty()) {
					//Get eligible students list for pass-fail process.
					List<TEEResultBean> eligibleStudentsForProjectPassFail = timeboundProjectPassFailService
							.getEligibleStudentsForProjectPassFail(teeResultBean.getTimebound_id());
					
					List<EmbaPassFailBean> finalListforPassFail = new ArrayList<EmbaPassFailBean>();
					List<EmbaPassFailBean> unsuccessfulPassFail = new ArrayList<EmbaPassFailBean>();
					//If eligible students found then only start processing else give an error message.
					if(eligibleStudentsForProjectPassFail.size()>0) {
						//Process the eligible students pass-fail for project and prepare successes and failed lists.
						Map<String, List<EmbaPassFailBean>> passFailProcessesultMap = timeboundProjectPassFailService
								.processTimeboundStudentsProjectPassFail(eligibleStudentsForProjectPassFail, userId);
						
						//Get pass-fail processed success and failed lists and assign to appropriate list.
						finalListforPassFail = passFailProcessesultMap.get("SuccessedList");
						unsuccessfulPassFail = passFailProcessesultMap.get("FailedList");
						
						timeboundProjectMarksUploadLogger.info("Successfully project marks processed list:"+finalListforPassFail);
						timeboundProjectMarksUploadLogger.info("Failed to process project marks list:"+unsuccessfulPassFail);
						
						if(finalListforPassFail.size()>0) {
							//Set eligible students count in modeAndView
							mv.addObject("studentsListEligibleForPassFailSize", finalListforPassFail.size());
							//Set success message in request scope.
							request.setAttribute("success", "true");
							request.setAttribute("successMessage", "Found " + finalListforPassFail.size() + " students for passfail. Kindly check records in Table 1 and run passfail trigger." );
						}
						
						//If there is pass-fail processing failed records then set error message to request scope. 
						if(unsuccessfulPassFail.size()>0) {
							request.setAttribute("error", "true");
							request.setAttribute("errorMessage", "Found " + unsuccessfulPassFail.size() + " students with insufficient data. Kindly check Table 2 below." );
						}
						//Set pass-fail processing successes and failed records size
						mv.addObject("successfullyProcessedPassFailSize", finalListforPassFail.size());
						mv.addObject("failedPassFailProcessingSize", unsuccessfulPassFail.size());
						//Set pass-fail successfully processed list and failed records to session scope
						request.getSession().setAttribute("failedPassFailProcessingList", unsuccessfulPassFail);
						request.getSession().setAttribute("successfullyProcessedPassFailList", finalListforPassFail);
						
					}else {
						request.setAttribute("error", "true");
						request.setAttribute("errorMessage", "No eligible students found for project pass-fail.");
						timeboundProjectMarksUploadLogger.info("No eligible students found for project pass-fail.");
					}
					
				}else { 
					request.setAttribute("error", "true");
					request.setAttribute("errorMessage", "Failed to upsert marks for "+errorList.size()+" students.\nSapids with error message : "+errorList);
					timeboundProjectMarksUploadLogger.info("Failed to upsert marks for "+errorList.size());
				}
			} else {
				request.setAttribute("error", "true");
				request.setAttribute("errorMessage", "Please verify:"+errorList);
				timeboundProjectMarksUploadLogger.info("Error in uploaded excel:"+errorList);
			}
			
		}catch (Exception e) {
			timeboundProjectMarksUploadLogger.error("Exception occured while processing marks upload, Error message:"+e.getStackTrace());
			request.setAttribute("error", "true");
			request.setAttribute("errorMessage", "Error: "+e.getMessage()); 
		}
		finally {
			//Get newly inserted and existing time bound student capstone project marks
			capstone_project_marks = timeboundProjectPassFailService.getTimeboundStudentProjectMarks(null,
					Integer.parseInt(teeResultBean.getTimebound_id()));
			
			//Set searched parameter teeResultBean as modelAttribute and list of project marks in modelAndView
			mv.addObject("teeResultBean", teeResultBean);
			mv.addObject("productTypes",PRODUCT_TYPES);
			mv.addObject("capstone_project_marks", capstone_project_marks);
			//Set list of project marks and size of list in session scope.
			request.getSession().setAttribute("capstone_project_marks", capstone_project_marks);
			request.getSession().setAttribute("capstone_project_marks_size", capstone_project_marks.size());
		}
		timeboundProjectMarksUploadLogger.info("EmbaResultProcessingController.uploadCapstoneProjectMarks() - END");
		//return modelAnView
		return mv;
	}//uploadCapstoneProjectMarks(-,-,-)
	
	@SuppressWarnings("unchecked")
	@PostMapping("/triggerTimeboundProjectPassFail")
	public ModelAndView triggerTimeboundProjectPassFail(HttpServletRequest request,HttpServletResponse response,@ModelAttribute TEEResultBean resultBean) {
		//Check session is alive else redirect to login page
		if(!checkSession(request, response)){ 
			redirectToPortalApp(response); 
		} 
		timeboundProjectMarksUploadLogger.info("EmbaResultProcessingController.triggerTimeboundProjectPassFail() - START");
		ModelAndView mv = new ModelAndView("uploadCapstoneProjectMarks");
		mv.addObject("acadYear", ACAD_YEAR_LIST);
		mv.addObject("acadMonth", ACAD_MONTH_LIST);
		//Get pass-fail processing succeed and failed records list from session scope.  
		List<EmbaPassFailBean> finalListforPassFail =  (List<EmbaPassFailBean>) request.getSession().getAttribute("successfullyProcessedPassFailList"); 
		List<EmbaPassFailBean> unsuccessfulPassFail =  (List<EmbaPassFailBean>) request.getSession().getAttribute("failedPassFailProcessingList");
		try {
			//Start inserting pass-fail record if the succeed records found else give error.
			if(finalListforPassFail.size()>0) {
				//Get service instance based on the product type e.g MBA - (WX)
				ITimeboundProjectPassFailService timeboundProjectPassFailService = timeboundProjectPassFailFactory.getTimeboundProjectPassFailProcessingInstance(resultBean.getProgramType());
				//Insert or update processed pass-fail records. 
				List<String> errorList = timeboundProjectPassFailService.upsertTimeboundStudentProjectPassFail(finalListforPassFail);
				
				//If error occurs while inserting or updating pass-fail records the show error messages. 
				if(errorList.size()>0) {
					request.setAttribute("error", "true");
					request.setAttribute("errorMessage","Failed to upsert marks for "+errorList.size()+" students.\nSapids with error message: "+errorList);
					timeboundProjectMarksUploadLogger.info("Failed to upsert pass-fail records with errors: "+errorList.size());
				}
				if(finalListforPassFail.size()-errorList.size()>0) {
					//Set success message to the request scope.
					request.setAttribute("success", "true"); 
					request.setAttribute("successMessage", "Marks uploaded & PassFail Trigger Completed For "+(finalListforPassFail.size()-errorList.size())+" records");
				}
			}else {
				request.setAttribute("error", "true");
				request.setAttribute("errorMessage", "No students found for passfail");
			}
			//If there is pass-fail processing failed records then set error message to request scope and it's size to modelAndView.
			if(unsuccessfulPassFail.size()>0) {
				request.setAttribute("error", "true");
				request.setAttribute("errorMessage", "Found " + unsuccessfulPassFail.size() + " students with insufficient data. Kindly check table 2 below." );
				mv.addObject("failedPassFailProcessingSize", unsuccessfulPassFail.size());
			}
		}catch(Exception e) {
			timeboundProjectMarksUploadLogger.error("Passfail Trigger Failed to Complete. Error:"+e.getStackTrace());
			request.setAttribute("error", "true");
			request.setAttribute("errorMessage", "Passfail Trigger Failed to Complete: "+e.getMessage());
		}
		//Set searched bean to modelAndView.
		mv.addObject("teeResultBean", resultBean);
		mv.addObject("productTypes",PRODUCT_TYPES);
		
		timeboundProjectMarksUploadLogger.info("EmbaResultProcessingController.triggerTimeboundProjectPassFail() - END");
		//return modelAnView
		return mv;
	}//triggerTimeboundProjectPassFail(-,-,-)
	
	@SuppressWarnings("unchecked")
	@GetMapping("/downloadTimeboundProjectMarksReport")
	public ModelAndView downloadTimeboundProjectMarksReport(HttpServletRequest request, HttpServletResponse response) {
		//Check session is alive else redirect to login page
		if(!checkSession(request, response)){
			redirectToPortalApp(response);
			return null;
		}
		timeboundProjectMarksUploadLogger.info("EmbaResultProcessingController.downloadTimeboundProjectMarksReport() - START");
		//Get project marks record list from session scope.  
		List<TEEResultBean> capstone_project_marks = (List<TEEResultBean>)request.getSession().getAttribute("capstone_project_marks");
		timeboundProjectMarksUploadLogger.info("Redirected to the excel view to download project marks excel file.");
		//return modelAndView with viewName and modelName with modelData.
		return new ModelAndView("timeboundStudentProjectMarksReportExcelView","capstoneProjectMarksList",capstone_project_marks);
	}//downloadTimeboundProjectMarksReport(-,-)
	
	
	@RequestMapping(value = "/embaProjectGraceMarksForm",method = {RequestMethod.GET})
	public ModelAndView embaProjectGraceMarksForm(HttpServletRequest request, HttpServletResponse response,@ModelAttribute TEEResultBean resultBean){
		if(!checkSession(request, response)){
			redirectToPortalApp(response);
			return null;
		}
		ModelAndView mav = new ModelAndView("EmbaProjectGrace");
		mav.addObject("acadYear", ACAD_YEAR_LIST);
		mav.addObject("acadMonth", ACAD_MONTH_LIST);
		mav.addObject("batches", getBatchList());
		mav.addObject("resultBean", resultBean);
		return mav;
	}

	@RequestMapping(value = "/embaProjectGraceMarksSearch",method = {RequestMethod.POST})
	public ModelAndView embaProjectGraceMarksSearch(HttpServletRequest request, HttpServletResponse response,@ModelAttribute TEEResultBean resultBean){
		if(!checkSession(request, response)){
			redirectToPortalApp(response);
			return null;
		}
		ModelAndView mav = new ModelAndView("EmbaProjectGrace");
		mav.addObject("acadYear", ACAD_YEAR_LIST);
		mav.addObject("acadMonth", ACAD_MONTH_LIST);
		mav.addObject("batches", getBatchList());
		mav.addObject("resultBean", resultBean);
		if(StringUtils.isBlank(resultBean.getBatchId()) || StringUtils.isBlank(resultBean.getTimebound_id())) {
			request.setAttribute("error", "true");
			request.setAttribute("errorMessage", "Kindly select all field values. All fields are Mandatory");
			return mav;
		}
		
		//get list of all failed students using timeboundId and scheduleId.
		ArrayList<EmbaPassFailBean> studentsListForGrace = examsAssessmentsDAO.getAllFailedStudentsForGraceProject(resultBean.getTimebound_id());
		if(studentsListForGrace.isEmpty()) {
			request.setAttribute("error", "true");
			request.setAttribute("errorMessage", "No students found for grace");
			return mav;
		}
		String loggedInUser =(String)request.getSession().getAttribute("userId");
		ArrayList<EmbaPassFailBean> finalListforGrace = new ArrayList<EmbaPassFailBean>();
		for(EmbaPassFailBean bean : studentsListForGrace) {
			int grace = calculateProjectGraceMarks(bean);// checking is grace is applicable
			if(grace>0) {
				bean.setGraceMarks(""+grace);
				bean.setLastModifiedBy(loggedInUser);
				finalListforGrace.add(bean);
			}
		}
		mav.addObject("finalListforGraceSize", finalListforGrace.size());
		mav.addObject("finalListforGrace", finalListforGrace);
		if(finalListforGrace.size()>0) {
			request.setAttribute("success", "true");
			request.setAttribute("successMessage", "Found "+finalListforGrace.size()+" students eligible for grace");
		}else {
			request.setAttribute("error", "true");
			request.setAttribute("errorMessage", "No students found for grace");
			return mav;
		}
		request.getSession().setAttribute("finalListforGrace", finalListforGrace);
		return mav;
	}

	@RequestMapping(value = "/embaProjectGraceMarks",method = {RequestMethod.POST})
	public ModelAndView embaProjectGraceMarks(HttpServletRequest request,HttpServletResponse response,@ModelAttribute TEEResultBean resultBean) {
		if(!checkSession(request, response)){ 
			redirectToPortalApp(response); 
		} 
		ModelAndView mv = new ModelAndView("EmbaProjectGrace");
		mv.addObject("acadYear", ACAD_YEAR_LIST);
		mv.addObject("acadMonth", ACAD_MONTH_LIST);
		try {
			ArrayList<EmbaPassFailBean> finalListforGrace  = (ArrayList<EmbaPassFailBean>)request.getSession().getAttribute("finalListforGrace");
			for(EmbaPassFailBean bean : finalListforGrace) { //applying grace adding grace marks to teeScore
				int grace = Integer.parseInt(bean.getGraceMarks());
				int teeScore = bean.getTeeScore();
				int finalTeeScore = teeScore + grace;
				bean.setTeeScore(finalTeeScore);
				bean.setIsPass("Y");
				bean.setFailReason("");
			}
			examsAssessmentsDAO.upsertEmbaPassFail(finalListforGrace); // upsert passfail table for grace marks
			request.setAttribute("success", "true");
			request.setAttribute("successMessage", "Grace Completed For "+finalListforGrace.size()+" records");
		}catch(Exception e) {
			
			request.setAttribute("error", "true");
			request.setAttribute("errorMessage", "Grace Failed to Complete");
		}
		mv.addObject("batches", getBatchList());
		mv.addObject("resultBean", resultBean);
		return mv;
	}

	private int calculateProjectGraceMarks(EmbaPassFailBean studentFinalMarks) {
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
		if (totalMarks > 47 && totalMarks < MBAWX_PASS_SCORE) {
			return true;
		}
		return false;  
	}
	
	@RequestMapping(value = "/embaProjectPassFailMakeLiveForm",method = {RequestMethod.GET})
	public ModelAndView embaProjectPassFailMakeLiveForm(HttpServletRequest request, HttpServletResponse response,@ModelAttribute TEEResultBean resultBean){
		if(!checkSession(request, response)){
			redirectToPortalApp(response);
			return null;
		}
		ModelAndView mav = new ModelAndView("EmbaProjectPassFailMakeLive");
		mav.addObject("acadYear", ACAD_YEAR_LIST);
		mav.addObject("acadMonth", ACAD_MONTH_LIST);
		mav.addObject("batches", getBatchList());
		mav.addObject("resultBean", resultBean);
		return mav;
	}
	@RequestMapping(value = "/embaProjectPassFailMakeLive",method = {RequestMethod.POST})
	public ModelAndView embaProjectPassFailMakeLive(HttpServletRequest request, HttpServletResponse response,@ModelAttribute TEEResultBean resultBean){
		if(!checkSession(request, response)){
			redirectToPortalApp(response);
			return null;
		}
		ModelAndView mav = new ModelAndView("EmbaProjectPassFailMakeLive");
		mav.addObject("acadYear", ACAD_YEAR_LIST);
		mav.addObject("acadMonth", ACAD_MONTH_LIST);
		mav.addObject("batches", getBatchList());
		mav.addObject("resultBean", resultBean);
		if(StringUtils.isBlank(resultBean.getBatchId()) || StringUtils.isBlank(resultBean.getTimebound_id())) {
			request.setAttribute("error", "true");
			request.setAttribute("errorMessage", "Kindly select all field values. All fields are Mandatory");
			return mav;
		}
		try {
			int count = examsAssessmentsDAO.passFailResultsForEmbaProjectLive(resultBean);
			request.setAttribute("success", "true");
			request.setAttribute("successMessage", "Result made live for "+count+" records");
		}catch(Exception e) {
			request.setAttribute("error", "true");
			request.setAttribute("errorMessage", "Error in make live for schedule id "+resultBean.getSchedule_id());
		}
		return mav;
	}
	
	private int roundDoubleToInt(double doubleValue) {
		return (int) Math.round(doubleValue);
	}
	
	@RequestMapping(value = "/mettlStatusDashboardForm",method = {RequestMethod.GET})
	public ModelAndView mettlStatusDashboardForm(){
		ModelAndView mav = new ModelAndView("MbaWxExamDashboard");
		return mav;
	}
	
	@RequestMapping(value = "/mbaWxExamEmailLinkForm",method = {RequestMethod.GET})
	public ModelAndView mbaWxExamEmailLinkForm(){
		ModelAndView mav = new ModelAndView("sendExamLinkFromDashBoardMbaWx");
		return mav;
	}
	
}
