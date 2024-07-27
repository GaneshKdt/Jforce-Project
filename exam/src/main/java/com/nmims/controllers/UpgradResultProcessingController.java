package com.nmims.controllers;


import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.ibm.icu.text.SimpleDateFormat;
import com.nmims.beans.EmbaPassFailBean;
import com.nmims.beans.ExamsAssessmentsBean;
import com.nmims.beans.MBAXMarksBean;
import com.nmims.beans.MBAXMarksPreviewBean;
import com.nmims.beans.MettlResponseBean;
import com.nmims.beans.ResponseBean;
import com.nmims.beans.StudentExamBean;
import com.nmims.beans.StudentSubjectConfigExamBean;
import com.nmims.beans.TEEResultBean;
import com.nmims.beans.TestExamBean;
import com.nmims.beans.UpgradAssessmentExamBean;
import com.nmims.daos.UpgradAssessmentDao;
import com.nmims.daos.UpgradResultProcessingDao;
import com.nmims.helpers.MettlHelper;
import com.nmims.helpers.UpgradHelper;
import com.nmims.services.UpgradAssessmentService;
import com.nmims.services.UpgradResultProcessingService;

@Controller
@RequestMapping("/admin")
public class UpgradResultProcessingController extends BaseController{

	@Value("#{'${ACAD_YEAR_LIST}'.split(',')}")
	private List<String> ACAD_YEAR_LIST;
	
	@Value("#{'${ACAD_MONTH_LIST}'.split(',')}") 
	private List<String> ACAD_MONTH_LIST; 
	
	@Value("#{'${CURRENT_YEAR_LIST}'.split(',')}")
	private List<String> CURRENT_YEAR_LIST; 
	
	@Value("${MettlBaseUrl}")
	private String MettlBaseUrl;

	@Value("${MettlPrivateKey}")
	private String MettlPrivateKey;

	@Value("${MettlPublicKey}")
	private String MettlPublicKey;

	@Value("${POST_DATA_TO_UPGRAD_URL}")
	private String PostDataToUpgradURL;
	
	@Autowired
	UpgradResultProcessingDao upgradResultProcessingDao;

	@Autowired
	@Qualifier("mbaxMettlHelper")
	MettlHelper mettlHelper;
	
	@Autowired
	UpgradAssessmentDao upgradAssessmentDao;
	
	@Autowired
	UpgradHelper upgradHelper;
	
	@Autowired
	UpgradAssessmentService assessmentService;
	
	@Autowired
	UpgradResultProcessingService resultsService;


	private ArrayList<String> examMonthList = new ArrayList<String>(Arrays.asList("Jan", "Feb","Mar", "Apr" , "May" , "Jun", "Jul", "Aug","Sep", "Oct", "Nov", "Dec" )); 


	private static final Logger logger = LoggerFactory.getLogger(UpgradResultProcessingController.class);

	
	@RequestMapping(value="/upgradExamResultsProcessingChecklist",method={RequestMethod.GET})
	public ModelAndView upgradExamResultsProcessingChecklist(HttpServletRequest request, HttpServletResponse response){
		if(!checkSession(request, response)){
			redirectToPortalApp(response);
			return null;
		}
		ModelAndView mav=new ModelAndView("mbax/MBAXExamResultsProcessingChecklist");
		return mav;
	}

	@RequestMapping(value = "/readMettlMarksFromAPIFormMBAX",method = {RequestMethod.GET})
	public ModelAndView readMettlMarksFromAPIForm(@ModelAttribute MettlResponseBean mettlResponseBean,HttpServletRequest request, HttpServletResponse response){
		if(!checkSession(request, response)){
			redirectToPortalApp(response);
			return null;
		}
		ModelAndView mav=new ModelAndView("mbax/MBAXReadMettlScore");
		mav.addObject("batches",resultsService.getBatchList(119));
		mav.addObject("mettlResponseBean", mettlResponseBean);
		return mav;
	}

	@RequestMapping(value="/readMettlMarksFromAPIMBAX",method= {RequestMethod.POST})
	public ModelAndView readMettlMarksFromAPI(HttpServletRequest request,@ModelAttribute MettlResponseBean mettlResponseBean, HttpServletResponse response) {
		if(!checkSession(request, response)){
			redirectToPortalApp(response);
			return null;
		}
		ModelAndView mav=new ModelAndView("mbax/MBAXReadMettlScore");
		mav.addObject("batches",resultsService.getBatchList(119));
		if(StringUtils.isBlank(""+mettlResponseBean.getAssessments_id()) || StringUtils.isBlank(mettlResponseBean.getSchedule_id()) || StringUtils.isBlank(mettlResponseBean.getBatchId()) || StringUtils.isBlank(mettlResponseBean.getTimebound_id())) {
			request.setAttribute("error", "true");
			request.setAttribute("errorMessage", "Kindly select all field values. All fields are Mandatory");
			return mav;
		}
		int prgm_sem_subj_id = upgradResultProcessingDao.getProgramSemSubjectIdFromTimeboundId(mettlResponseBean.getTimebound_id()); // setting prgm_sem_subj_id from timebound Id
		if(prgm_sem_subj_id==0) {
			request.setAttribute("error", "true");
			request.setAttribute("errorMessage", "Program Sem Subject Id not found.");
			return mav;
		}
		mettlResponseBean.setPrgm_sem_subj_id(prgm_sem_subj_id); 
		String loggedInUser =(String)request.getSession().getAttribute("userId");
		ArrayList<MettlResponseBean> mettlResponseBeanList = new ArrayList<MettlResponseBean>();
		ArrayList<String> studentsNotFound = new ArrayList<String>();
		ArrayList<String> studentsErrorFound = new ArrayList<String>();
		try {
			//using schedule and timebound ids get the scheduleAccessKey.
//			String scheduleAccessKey = upgradResultProcessingDao.getScheduleKeyFromScheduleIdAndTimeBoundId(mettlResponseBean.getSchedule_id(),mettlResponseBean.getTimebound_id());
			ExamsAssessmentsBean schedule = upgradResultProcessingDao.getScheduleFromScheduleIdAndTimeBoundId(mettlResponseBean.getSchedule_id(),mettlResponseBean.getTimebound_id());
			if(schedule == null || schedule.getSchedule_accessKey() == null) {
				
			}
			
			String scheduleAccessKey = schedule.getSchedule_accessKey();
			String max_marks = schedule.getMax_score();
			
			//get list of all student details for a particular batch and timbound id.
			ArrayList<StudentExamBean> studentDetailsList = new ArrayList<StudentExamBean>();
			if("100".equals(max_marks)) {
				studentDetailsList= upgradResultProcessingDao.getReExamStudentDetails(mettlResponseBean.getBatchId(),mettlResponseBean.getTimebound_id(),"MBA - X");
			}else {
				studentDetailsList= upgradResultProcessingDao.getAllStudentDetailsForBatchAndTimebound(mettlResponseBean.getBatchId(),mettlResponseBean.getTimebound_id(),"MBA - X"); //get all student details for a particular selected batchID and timeBoundID
			}
			
			/*
			 * For every student in the list get test results using mettl api.
			 * Test results are obtained for a particular scheduleAccessKey. 
			 * Overall status from mettl = success and status of test = completed then student data inserted with status attempted.
			 * Overall status from mettl = success and status of test != completed then student data inserted with status not attempted.
			 * Overall status from mettl = error then student emailId added in studentsErrorFound list.
			 * Overall response from mettl = null then student added in studentsNotFound list.
			 */

			SimpleDateFormat inputDateFormatter = new SimpleDateFormat("EEE, dd MMM yyyy HH:mm:ss z");
			SimpleDateFormat testDateFormatter = new SimpleDateFormat("yyyy-MM-dd");
			SimpleDateFormat testTimeFormatter = new SimpleDateFormat("HH:mm:ss");
			for(StudentExamBean student:studentDetailsList) {
				JsonObject jsonResponse = mettlHelper.getSingleStudentTestStatusForASchedule(scheduleAccessKey,student.getEmailId());
				if(jsonResponse != null) {
					String status = jsonResponse.get("status").getAsString();
					MettlResponseBean tmp_responseBean = new MettlResponseBean();
					tmp_responseBean.setMax_marks(max_marks);
					if("SUCCESS".equalsIgnoreCase(status)) {
						JsonObject candidateObject = jsonResponse.get("candidate").getAsJsonObject();
						tmp_responseBean.setEmail(candidateObject.get("email").getAsString()); //setting emailID
						tmp_responseBean.setSchedule_id(mettlResponseBean.getSchedule_id()); //setting scheduleId
						tmp_responseBean.setTimebound_id(mettlResponseBean.getTimebound_id());//setting TimeBoundId
						tmp_responseBean.setPrgm_sem_subj_id(mettlResponseBean.getPrgm_sem_subj_id());//setting ProgramSemSubjectId
						JsonObject testObject = candidateObject.get("testStatus").getAsJsonObject();
						
						// Access Revoked is returned from the Mettl API when student has been de-registered from Mettl
						if("AccessRevoked".equalsIgnoreCase(testObject.get("status").getAsString())) {
							studentsErrorFound.add(student.getEmailId());
						} else {
							if("Completed".equalsIgnoreCase(testObject.get("status").getAsString())) {
								String startDateString = testObject.get("startTime").getAsString();
								Date startTime = inputDateFormatter.parse(startDateString);

								String test_date = testDateFormatter.format(startTime);
								String test_time = testTimeFormatter.format(startTime);
								tmp_responseBean.setTest_date(test_date);
								tmp_responseBean.setTest_time(test_time);
								tmp_responseBean.setReport_link(testObject.get("htmlReport").getAsString());
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
			ArrayList<String> errorList = new ArrayList<String>();
			/*
			 * For every student in the mettlResponseBeanList upsert scores in history then in marks table. 
			 * History Table : 1. check if record exists for  sapid - timebound_id - schedule_id -  prgm_sem_subj_id.
			 * 				   2. If not exist then insert else update.
			 * TeeMarks Table: 1. check if record exists for  sapid - prgm_sem_subj_id.
			 * 				   2. If not exist then insert else update.
			 */
			if(!mettlResponseBeanList.isEmpty()) { 
				errorList = upgradResultProcessingDao.upsertMarks(mettlResponseBeanList); 

				if(errorList.isEmpty()) { 
					request.setAttribute("success", "true");
					request.setAttribute("successMessage", "Marks upserted successfully  for "+mettlResponseBeanList.size()+" students"); 
				}else { 
					request.setAttribute("error", "true");
					request.setAttribute("errorMessage",
							"Failed to upsert marks for "+errorList.size()+" students \n emailIDs : "+errorList); 
				}

			}else {
				// error response 
				request.setAttribute("error", "true");
				request.setAttribute("errorMessage", "Null or empty result found for upsert");
				return mav;
			}
		}
		catch (Exception e) {
			request.setAttribute("error", "true");
			request.setAttribute("errorMessage", e.getMessage());
			
			return mav; 
		}
		return mav; 
	}
	
	
	
	//Step 1: Upload marks start.
	@RequestMapping(value="/uploadMBAXMarksForm",method=RequestMethod.GET)
	public ModelAndView uploadMBAXMarksForm(HttpServletRequest request,HttpServletResponse response) {
		if(!checkSession(request, response)){
			redirectToPortalApp(response);
			return null;
		}
		ModelAndView mv = new ModelAndView("uploadMBAXMarks");
		ArrayList<MBAXMarksBean> mbaxMarksBeanList = (ArrayList<MBAXMarksBean>) upgradResultProcessingDao.getMBAXMarksStudentList();
		MBAXMarksBean fileBean = new MBAXMarksBean();
		mv.addObject("fileBean",fileBean);
		mv.addObject("mbax_marks",mbaxMarksBeanList);
		mv.addObject("batches",resultsService.getBatchesMap(119,request));
		return mv;
	}
	
	@RequestMapping(value="/uploadMBAXMarks",method=RequestMethod.POST)
	public ModelAndView uploadMBAXMarks(MBAXMarksBean mbaxMarksBean , HttpServletRequest request,HttpServletResponse response) {
		if(!checkSession(request, response)){
			redirectToPortalApp(response);
			return null;
		}
		String userId = (String)request.getSession().getAttribute("userId");
		mbaxMarksBean.setLastModifiedBy(userId);
		ModelAndView mv = new ModelAndView("uploadMBAXMarks");
		ArrayList<MBAXMarksBean> mbaxResultBeanErrorList = mettlHelper.addExcelMBAXMarksIntoPortal(mbaxMarksBean,request);
		ArrayList<MBAXMarksBean> mbaxMarksBeanList = (ArrayList<MBAXMarksBean>) upgradResultProcessingDao.getMBAXMarksStudentList();
		if(mbaxResultBeanErrorList == null) {
		//	logger.error("mbaxResultBeanErrorList is null");
			request.setAttribute("error","true");
			request.setAttribute("errorMessage","Error While creating recording");
		}
		else if(mbaxResultBeanErrorList.size() == 0) {
			request.setAttribute("success","true");
			request.setAttribute("successMessage","Successfully recording created : " + request.getAttribute("totalResult"));
	//		logger.info("Successfully recording created : " + request.getAttribute("totalResult"));
		} else {
			request.setAttribute("error","true");
			request.setAttribute("errorMessage","Some of record failed to create, count: " + mbaxResultBeanErrorList.size());
			request.setAttribute("error_list_flag","true");
			request.setAttribute("error_lists",mbaxResultBeanErrorList);
		//	logger.error("Some of record failed to create, count: "+ mbaxResultBeanErrorList.size());
		}
		MBAXMarksBean fileBean = new MBAXMarksBean();
		mv.addObject("fileBean",fileBean);
		mv.addObject("mbax_marks",mbaxMarksBeanList);
		mv.addObject("batches",resultsService.getBatchList(119));
		return mv;
	}
	
	@RequestMapping(value="/previewMBAXMarks", method=RequestMethod.POST, produces="application/json")
	public ResponseEntity<MBAXMarksPreviewBean> previewMBAXMarks(MBAXMarksBean mbaxMarksBean , HttpServletRequest request,HttpServletResponse response) {
		HttpHeaders headers = new HttpHeaders();
		headers.add("Content-Type","application/json");
		MBAXMarksPreviewBean mbaxResultBeanPreviewList = mettlHelper.previewExcelMBAXMarks(mbaxMarksBean,request);
		return new ResponseEntity(mbaxResultBeanPreviewList, headers, HttpStatus.OK);
	}
	//Step 1: Upload marks end.
	
	//Step 2: View scores uploaded into mbax_marks table start

		@RequestMapping(value = "/viewMettlScoresMBAXForm",method = {RequestMethod.GET})
		public ModelAndView viewMettlScoresMBAXForm(@ModelAttribute TEEResultBean mettlResponseBean,HttpServletRequest request, HttpServletResponse response){
			if(!checkSession(request, response)){
				redirectToPortalApp(response);
				return null;
			}
			ModelAndView mav = new ModelAndView("mbax/MBAXViewMettlScore");
			mav.addObject("batches",resultsService.getBatchList(119));
			mav.addObject("mettlResponseBean", mettlResponseBean);
			return mav;
		}

		@RequestMapping(value = "/viewMettlScoresMBAX",method = {RequestMethod.POST})
		public ModelAndView viewMettlScoresMBAX(HttpServletRequest request, HttpServletResponse response,@ModelAttribute TEEResultBean mettlResponseBean){
			if(!checkSession(request, response)){
				redirectToPortalApp(response);
				return null;
			}
			ModelAndView mav = new ModelAndView("mbax/MBAXViewMettlScore");
			mav.addObject("batches",resultsService.getBatchList(119));
			mav.addObject("mettlResponseBean", mettlResponseBean);
			ArrayList<TEEResultBean> scoreList =  (ArrayList<TEEResultBean>) upgradResultProcessingDao.readMBAXScoreFromTeeMarks( mettlResponseBean);
			mav.addObject("scoreListSize", scoreList.size());
			mav.addObject("scoreList", scoreList);
			request.getSession().setAttribute("scoreList",scoreList);
			return mav;
		}

		@RequestMapping(value = "/mbaxMettlScoresDownload",method = {RequestMethod.POST})
		public ModelAndView mbaxMettlScoresDownload(HttpServletRequest request,HttpServletResponse response,@ModelAttribute TEEResultBean resultBean){
			if(!checkSession(request, response)){
				redirectToPortalApp(response);
			}
			ArrayList<TEEResultBean> scoreList = (ArrayList<TEEResultBean>)request.getSession().getAttribute("scoreList");
			return new ModelAndView("mbaxMettlScoresView","scoreList",scoreList);
		}
	//Step 2: View scores uploaded into mbax_marks table end
	
	//Step 3: Mark RIA NV Student wise start
		@RequestMapping(value = "/markRIANVCasesMBAXForm",method = {RequestMethod.GET})
		public ModelAndView markRIANVCasesMBAXForm(HttpServletRequest request, HttpServletResponse response,@ModelAttribute TEEResultBean resultBean){
			if(!checkSession(request, response)){
				redirectToPortalApp(response);
				return null;
			}
			ModelAndView mav = new ModelAndView("mbax/MBAXRiaNv");
			mav.addObject("resultBean", resultBean);
			return mav;
		}

		@RequestMapping(value = "/mbaxSearchScoresForRIANV",method = {RequestMethod.POST})
		public ModelAndView mbaxSearchScoresForRIANV(HttpServletRequest request, HttpServletResponse response,@ModelAttribute TEEResultBean resultBean){
			if(!checkSession(request, response)){
				redirectToPortalApp(response);
				return null;
			}
			ModelAndView mav = new ModelAndView("mbax/MBAXRiaNv");
			mav.addObject("resultBean", resultBean);
			List<TEEResultBean> teeScores = upgradResultProcessingDao.readMBAXScoreFromTeeMarks(resultBean);	//get all subjects student scores from mbaxMarks table.
			mav.addObject("teeScores", teeScores);
			mav.addObject("rowCount", teeScores.size());
			return mav;
		}

		//based on check box selected update students score for the subject.
		@RequestMapping(value = "/updateMBAXSubjectAsRIANV",method = {RequestMethod.POST})
		public ResponseEntity<HashMap<String, String>> updateMBAXSubjectAsRIANV(@RequestParam String subject,
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
			String lastModifiedBy = (String)request.getSession().getAttribute("userId");
			ResponseEntity<HashMap<String, String>> response = resultsService.updateMBAXSubjectAsRIANV(subject,status,timebound_id,sapid,schedule_id,prgm_sem_subj_id,lastModifiedBy);
			return response;
		}
	//Step 3: Mark RIA NV Student wise end

	//Step 4: Upload Absent Students  start
		@RequestMapping(value = "/uploadUpgradAbsentListForm", method = {RequestMethod.GET})
		public ModelAndView uploadUpgradAbsentListForm(HttpServletRequest request, HttpServletResponse response, @ModelAttribute TEEResultBean resultBean) {
			if(!checkSession(request, response)){
				redirectToPortalApp(response);
				return null;
			}
			ModelAndView modelnView = new ModelAndView("mbax/MBAXAbsentUploadView");
			modelnView.addObject("resultBean", resultBean);
			modelnView.addObject("batches",resultsService.getBatchList(119));
			return modelnView;
		}

		@RequestMapping(value = "/searchABRecordsToInsertMBAX", method = {RequestMethod.POST})
		public ModelAndView searchABRecordsToInsertMBAX(HttpServletRequest request, HttpServletResponse response, @ModelAttribute TEEResultBean resultBean){
				ModelAndView modelnView = new ModelAndView("mbax/MBAXAbsentUploadView");
				if(!checkSession(request, response)){
					redirectToPortalApp(response);
					return null;
				}
				List<TEEResultBean> absentStudentList = resultsService.searchAbsentRecordsMBAX(request,resultBean);
				modelnView.addObject("rowCount", absentStudentList != null ? absentStudentList.size() : 0);
				modelnView.addObject("resultBean", resultBean);
				modelnView.addObject("batches",resultsService.getBatchList(119));
			return modelnView;
		}
		
		@RequestMapping(value = "/downloadABReportMBAX", method = {RequestMethod.GET, RequestMethod.POST})
	  	public ModelAndView downloadABReportMBAX(HttpServletRequest request, HttpServletResponse response) {
			if(!checkSession(request, response)){
				redirectToPortalApp(response);
				return null;
			}
	  		List<TEEResultBean> absentStudentList = (List<TEEResultBean>)request.getSession().getAttribute("absentStudentListMBAX");
	  		return new ModelAndView("absentReportExcelViewMBAX", "absentStudentList", absentStudentList);
	  	}

		@RequestMapping(value = "/insertABReportMBAX", method = {RequestMethod.GET})
	  	public ModelAndView insertABReportMBAX(HttpServletRequest request, HttpServletResponse response, @ModelAttribute TEEResultBean resultBean) {
			if(!checkSession(request, response)){
				redirectToPortalApp(response);
				return null;
			}
		//	HashMap<String,String> subjectList = (HashMap<String,String>)request.getSession().getAttribute("subjectList");
	  		ModelAndView modelnView = new ModelAndView("mbax/MBAXAbsentUploadView");
	  		List<TEEResultBean> studentsList = (List<TEEResultBean>) request.getSession().getAttribute("absentStudentListMBAX");
	  		 resultsService.insertUpgradAbsentList(request,studentsList);
	  		modelnView.addObject("rowCount", studentsList != null ? studentsList.size() : 0);
	  		modelnView.addObject("resultBean", resultBean);
			//modelnView.addObject("subjectList", subjectList);
			modelnView.addObject("batches",resultsService.getBatchList(119));
	  		return modelnView;
	  	}
		
    //Step 4: Upload Absent Students  end	
		
	//Step 5: Passfail Trigger  start:  Run passfail trigger for batch - timeboundId - schedule

		@RequestMapping(value="/getMBAXAssessmentListByTimeBoundId",method=RequestMethod.GET)
		public @ResponseBody ArrayList<MettlResponseBean> getMBAXAssessmentListByTimeBoundId(HttpServletRequest request) {
			if(request.getParameter("id") == null) {
				return new  ArrayList<MettlResponseBean>();
			}
			ArrayList<MettlResponseBean> assessmentList =  resultsService.getMBAXAssessmentsByTimeBoundId(request);
			return assessmentList;
		}

		@RequestMapping(value="/getMBAXSubjectListByBatchId",method=RequestMethod.GET)
		public @ResponseBody ArrayList<StudentSubjectConfigExamBean> getMBAXSubjectListByBatchId(HttpServletRequest request) {
			if(request.getParameter("id") == null) {
				return new  ArrayList<StudentSubjectConfigExamBean>();
			}
			int batchId = Integer.parseInt(request.getParameter("id"));
			ArrayList<StudentSubjectConfigExamBean> assessmentList =  resultsService.getMBAXSubjectListByBatchId(batchId);
			return assessmentList;
		}
		
		@RequestMapping(value="/getMBAXScheduleListByAssessment",method=RequestMethod.GET)
		public @ResponseBody List<MettlResponseBean> getMBAXScheduleListByAssessment(HttpServletRequest request) {
			if(request.getParameter("id") == null) {
				return new  ArrayList<MettlResponseBean>();
			}
			List<MettlResponseBean> scheduleList = resultsService.getMBAXScheduleByTimeBoundId(Integer.parseInt(request.getParameter("id")), Integer.parseInt(request.getParameter("timeid")));
			return scheduleList;
		}
		
		//on opening the passfail page show count and details of all students with processed flag set to N.  
		@RequestMapping(value = "/mbaxPassFailTriggerForm",method = {RequestMethod.GET})
		public ModelAndView mbaxPassFailTriggerForm(HttpServletRequest request, HttpServletResponse response,@ModelAttribute TEEResultBean resultBean){
			if(!checkSession(request, response)){
				redirectToPortalApp(response);
				return null;
			}
			ModelAndView mav = new ModelAndView("mbax/MBAXPassFailTrigger");
			ArrayList<TEEResultBean> studentsNotProcessed = resultsService.getAllMBAXStudentNotProcessedList(request,resultBean);
			mav.addObject("batches",resultsService.getBatchList(119));
			mav.addObject("resultBean", resultBean);
			mav.addObject("studentsNotProcessedSize", studentsNotProcessed.size());
			mav.addObject("studentsNotProcessed", studentsNotProcessed);
			mav.addObject("studentsListEligibleForPassFailSize", 0);
			mav.addObject("unsuccessfulPassFailSize", 0);
			return mav;
		}
		
		//get students eligible for passfail using Timebound_id - Schedule_id.
		//Run passfail logic on eligible list.
		@RequestMapping(value = "/mbaxPassFailTriggerSearch",method = {RequestMethod.POST})
		public ModelAndView mbaxPassFailTriggerSearch(HttpServletRequest request, HttpServletResponse response,@ModelAttribute TEEResultBean resultBean){
			if(!checkSession(request, response)){
				redirectToPortalApp(response);
				return null;
			}
			String loggedInUser =(String)request.getSession().getAttribute("userId");
	//		logger.info("loggedInUser = "+loggedInUser);
			ModelAndView mav = new ModelAndView("mbax/MBAXPassFailTrigger");
			mav.addObject("batches",resultsService.getBatchList(119));
			if(StringUtils.isBlank(resultBean.getAssessments_id()) || StringUtils.isBlank(resultBean.getSchedule_id()) || StringUtils.isBlank(resultBean.getBatchId()) || StringUtils.isBlank(resultBean.getTimebound_id())) {
	//			logger.info(resultBean.getAssessments_id()+"|"+resultBean.getSchedule_id()+"|"+resultBean.getBatchId()+"|"+resultBean.getTimebound_id());
				request.setAttribute("error", "true");
				request.setAttribute("errorMessage", "Kindly select all field values. All fields are Mandatory");
				return mav;
			}
			mav.addObject("resultBean", resultBean);
			ArrayList<TEEResultBean> studentsListEligibleForPassFail  = new ArrayList<TEEResultBean>(); 
			if("0".equals(resultBean.getAssessments_id())  && "0".equals(resultBean.getSchedule_id()) ) {
				studentsListEligibleForPassFail = resultsService.getEligibleStudentsForPassFailBOPSubject(resultBean.getTimebound_id());
			}else {
				studentsListEligibleForPassFail = upgradResultProcessingDao.getAllMBAXStudentsEligibleForPassFail(resultBean); //score cannot be null or blank and status <> 'Not Attempted' or null
			}
	//		logger.info("studentsListEligibleForPassFail = " +studentsListEligibleForPassFail.size());
			mav.addObject("studentsListEligibleForPassFailSize", studentsListEligibleForPassFail.size());
			ArrayList<EmbaPassFailBean> finalListforPassFail = new ArrayList<EmbaPassFailBean>();
			ArrayList<EmbaPassFailBean> unsuccessfulPassFail = new ArrayList<EmbaPassFailBean>();
			if(studentsListEligibleForPassFail.size()>0) {
				try{
					if("0".equals(resultBean.getAssessments_id())  && "0".equals(resultBean.getSchedule_id()) ) {
						resultsService.bopSubjectPassFailLogic(resultBean,finalListforPassFail,studentsListEligibleForPassFail,loggedInUser,unsuccessfulPassFail );
					}else {
						resultsService.mbaPassFailLogic(resultBean,finalListforPassFail,studentsListEligibleForPassFail,loggedInUser,unsuccessfulPassFail );	
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

		//After the passfail logic is run then only the students present in finalListForPassFail are upserted into mba_passfail and corresponding flags are updated in mbaxMarks and history table.
		@RequestMapping(value = "/mbaxPassFailTrigger",method = {RequestMethod.POST})
		public ModelAndView mbaxPassFailTrigger(HttpServletRequest request,HttpServletResponse response,@ModelAttribute TEEResultBean resultBean) {
			if(!checkSession(request, response)){ 
				redirectToPortalApp(response); 
			} 
			
			ModelAndView mv = new ModelAndView("mbax/MBAXPassFailTrigger");
			ArrayList<EmbaPassFailBean> finalListforPassFail =  (ArrayList<EmbaPassFailBean>) request.getSession().getAttribute("finalListforPassFail"); 
			ArrayList<EmbaPassFailBean> unsuccessfulPassFail =  (ArrayList<EmbaPassFailBean>) request.getSession().getAttribute("unsuccessfulPassFail");
			try {
				
				if(finalListforPassFail.size()>0) {
					upgradResultProcessingDao.upsertMBAXPassFail(finalListforPassFail); // upsert passfail table
					request.setAttribute("success", "true"); 
					request.setAttribute("successMessage", "PassFail Trigger Completed For "+finalListforPassFail.size()+" records");
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

			mv.addObject("batches",resultsService.getBatchList(119));
			mv.addObject("resultBean", resultBean);
			ArrayList<TEEResultBean> studentsNotProcessed = resultsService.getAllMBAXStudentNotProcessedList(request,resultBean);
			mv.addObject("studentsNotProcessedSize", studentsNotProcessed.size());
			mv.addObject("studentsNotProcessed", studentsNotProcessed);
			return mv;
		}
		
	//Step 5: Passfail Trigger  end
	
		
		//Step 7: Apply grace to eligible students

		@RequestMapping(value = "/mbaxGraceMarksForm",method = {RequestMethod.GET})
		public ModelAndView mbaxGraceMarksForm(HttpServletRequest request, HttpServletResponse response,@ModelAttribute TEEResultBean resultBean){
			if(!checkSession(request, response)){
				redirectToPortalApp(response);
				return null;
			}
			ModelAndView mav = new ModelAndView("mbax/MBAXGrace");
			mav.addObject("batches",resultsService.getBatchList(119));
			mav.addObject("resultBean", resultBean);
			return mav;
		}

		@RequestMapping(value = "/mbaxGraceMarksSearch",method = {RequestMethod.POST})
		public ModelAndView mbaxGraceMarksSearch(HttpServletRequest request, HttpServletResponse response,@ModelAttribute TEEResultBean resultBean){
			if(!checkSession(request, response)){
				redirectToPortalApp(response);
				return null;
			}
			ModelAndView mav = new ModelAndView("mbax/MBAXGrace");
			mav.addObject("batches",resultsService.getBatchList(119));
			mav.addObject("resultBean", resultBean);
			if(StringUtils.isBlank(resultBean.getAssessments_id()) || StringUtils.isBlank(resultBean.getSchedule_id()) || StringUtils.isBlank(resultBean.getBatchId()) || StringUtils.isBlank(resultBean.getTimebound_id())) {
				request.setAttribute("error", "true");
				request.setAttribute("errorMessage", "Kindly select all field values. All fields are Mandatory");
				return mav;
			}
			//get list of all failed students using timeboundId and scheduleId.
			ArrayList<EmbaPassFailBean> studentsListForGrace = upgradResultProcessingDao.getAllFailedMBAXStudentsForGrace(resultBean.getTimebound_id(),resultBean.getSchedule_id());
			if(studentsListForGrace.isEmpty()) {
				request.setAttribute("error", "true");
				request.setAttribute("errorMessage", "No students found for grace");
				return mav;
			}
	//		logger.info("studentsListForGrace = "+studentsListForGrace.size());
			String loggedInUser =(String)request.getSession().getAttribute("userId");
			ArrayList<EmbaPassFailBean> finalListforGrace = new ArrayList<EmbaPassFailBean>();
			for(EmbaPassFailBean bean : studentsListForGrace) {
				int grace = resultsService.calculateGraceMarks(bean);// checking is grace is applicable
				if(grace>0) {
					bean.setGraceMarks(""+grace);
					bean.setLastModifiedBy(loggedInUser);
					finalListforGrace.add(bean);
				}
			}
	//		logger.info("finalListforGrace = "+finalListforGrace.size());
			mav.addObject("finalListforGraceSize", finalListforGrace.size());
			mav.addObject("finalListforGrace", finalListforGrace);
			if(finalListforGrace.size()>0) {
				request.setAttribute("success", "true");
				request.setAttribute("successMessage", "Found "+finalListforGrace.size()+" students eligible for grace");
			}else {
	//			logger.info("No students found for grace ");
				request.setAttribute("error", "true");
				request.setAttribute("errorMessage", "No students found for grace");
				return mav;
			}
			request.getSession().setAttribute("finalListforGrace", finalListforGrace);
			return mav;
		}

		@RequestMapping(value = "/mbaxGraceMarks",method = {RequestMethod.POST})
		public ModelAndView mbaxGraceMarks(HttpServletRequest request,HttpServletResponse response,@ModelAttribute TEEResultBean resultBean) {
			if(!checkSession(request, response)){ 
				redirectToPortalApp(response); 
			} 
			ModelAndView mv = new ModelAndView("mbax/MBAXGrace");
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
				upgradResultProcessingDao.upsertMBAXPassFail(finalListforGrace); // upsert passfail table for grace marks
				request.setAttribute("success", "true");
				request.setAttribute("successMessage", "Grace Completed For "+finalListforGrace.size()+" records");
			}catch(Exception e) {
				
				request.setAttribute("error", "true");
				request.setAttribute("errorMessage", "Grace Failed to Complete");
			}
			mv.addObject("batches",resultsService.getBatchList(119));
			mv.addObject("resultBean", resultBean);
			return mv;
		}
		
		
		//Step 9: Make Results live schedule wise
		@RequestMapping(value = "/mbaxPassFailMakeLiveForm",method = {RequestMethod.GET})
		public ModelAndView mbaxPassFailMakeLiveForm(HttpServletRequest request, HttpServletResponse response,@ModelAttribute TEEResultBean resultBean){
			if(!checkSession(request, response)){
				redirectToPortalApp(response);
				return null;
			}
			ModelAndView mav = new ModelAndView("mbax/MBAXPassFailMakeLive");
			mav.addObject("batches",resultsService.getBatchList(119));
			mav.addObject("resultBean", resultBean);
			return mav;
		}
		@RequestMapping(value = "/mbaxPassFailMakeLive",method = {RequestMethod.POST})
		public ModelAndView mbaxPassFailMakeLive(HttpServletRequest request, HttpServletResponse response,@ModelAttribute TEEResultBean resultBean){
			if(!checkSession(request, response)){
				redirectToPortalApp(response);
				return null;
			}
			ModelAndView mav = new ModelAndView("mbax/MBAXPassFailMakeLive");
			mav.addObject("batches",resultsService.getBatchList(119));
			mav.addObject("resultBean", resultBean);
			if(StringUtils.isBlank(resultBean.getAssessments_id()) || StringUtils.isBlank(resultBean.getSchedule_id()) || StringUtils.isBlank(resultBean.getBatchId()) || StringUtils.isBlank(resultBean.getTimebound_id())) {
				request.setAttribute("error", "true");
				request.setAttribute("errorMessage", "Kindly select all field values. All fields are Mandatory");
				return mav;
			}
			try {
				int count = 0 ;
				
				if("0".equals(resultBean.getAssessments_id())  && "0".equals(resultBean.getSchedule_id()) ) {
					count = upgradResultProcessingDao.passFailResultsLiveForBOPSubject(Integer.parseInt(resultBean.getTimebound_id()));
				}else {
					count = upgradResultProcessingDao.passFailResultsForMBAXLive(resultBean);
					upgradResultProcessingDao.passFailResultsForMBAXScheduleLive(resultBean);
				}
				request.setAttribute("success", "true");
				request.setAttribute("successMessage", "Result made live for "+count+" records");
			}catch(Exception e) {
				request.setAttribute("error", "true");
				request.setAttribute("errorMessage", "Error in make live for schedule id "+resultBean.getSchedule_id());
			}
			return mav;
		}
		
		//Step 10: Download /View Pass fail report

		@RequestMapping(value = "/mbaxPassFailReportForm",method = {RequestMethod.GET})
		public ModelAndView mbaxPassFailReportForm(HttpServletRequest request, HttpServletResponse response,@ModelAttribute TEEResultBean resultBean){
			if(!checkSession(request, response)){
				redirectToPortalApp(response);
				return null;
			}
			ModelAndView mav = new ModelAndView("mbax/MBAXPassFailReport");
			mav.addObject("batches",resultsService.getBatchList(119));
			mav.addObject("resultBean", resultBean);
			return mav;
		}

		@RequestMapping(value = "/mbaxPassFailReport",method = {RequestMethod.POST})
		public ModelAndView mbaxPassFailReport(HttpServletRequest request, HttpServletResponse response,@ModelAttribute TEEResultBean resultBean){
			if(!checkSession(request, response)){
				redirectToPortalApp(response);
				return null;
			}
			ModelAndView mav = new ModelAndView("mbax/MBAXPassFailReport");
			mav.addObject("batches",resultsService.getBatchList(119));
			mav.addObject("resultBean", resultBean);
			try {
				ArrayList<EmbaPassFailBean> passFailResultsList = upgradResultProcessingDao.getMBAXPassFailResultsForReport(resultBean);
	//			logger.info("passFailResultsList.size()="+passFailResultsList.size());
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

		@RequestMapping(value = "/mbaxPassFailReportDownload",method = {RequestMethod.POST})
		public ModelAndView mbaxPassFailReportDownload(HttpServletRequest request,HttpServletResponse response,@ModelAttribute TEEResultBean resultBean){
			if(!checkSession(request, response)){
				redirectToPortalApp(response);
			}
			List<EmbaPassFailBean> passFailResultsList = (ArrayList<EmbaPassFailBean>)request.getSession().getAttribute("passFailResultsList");
			return new ModelAndView("mbaxPassFailReportView","passFailResultsList",passFailResultsList);
		}


	@RequestMapping(value = "/mbaxAbsoluteGradingForm",method = {RequestMethod.GET})
	public ModelAndView mbaxAbsoluteGradingForm(HttpServletRequest request, HttpServletResponse response,@ModelAttribute TEEResultBean resultBean){
		if(!checkSession(request, response)){
			redirectToPortalApp(response);
			return null;
		}
		ModelAndView mav = new ModelAndView("mbax/MBAXAbsoluteGrading");		
		mav.addObject("batches",resultsService.getBatchList(119));
		//mav.addObject("batches", new HashMap<String,String>());
		mav.addObject("acadsYearList", ACAD_YEAR_LIST);
		mav.addObject("acadsMonthList", ACAD_MONTH_LIST);
		mav.addObject("resultBean", resultBean);
		return mav;
	}
	
	@RequestMapping(value = "/mbaxAbsoluteGrading",method = {RequestMethod.POST})
	public ModelAndView mbaxAbsoluteGrading(HttpServletRequest request,HttpServletResponse response,@ModelAttribute TEEResultBean resultBean) {
		if(!checkSession(request, response)){ 
			redirectToPortalApp(response); 
		} 		
		
		String userId = (String) request.getSession().getAttribute("userId");
		ModelAndView mav = new ModelAndView("mbax/MBAXAbsoluteGrading");
		mav.addObject("batches",resultsService.getBatchList(119));
		mav.addObject("acadsYearList", ACAD_YEAR_LIST);
		mav.addObject("acadsMonthList", ACAD_MONTH_LIST);
		mav.addObject("resultBean", resultBean);
		
		String commaSeparatedTimeBoundIds = assessmentService.getTimeBoundIdsByBatchIdAndAcadYearAndAcadMonth(resultBean); //timeboundIds to get mba X pass fail data
		List<EmbaPassFailBean> mbaXPassFailData = upgradAssessmentDao.getMbaXPassFailByTimeBoundId(commaSeparatedTimeBoundIds);		//get pass fail data by timeBound Ids
		
		if(mbaXPassFailData != null && mbaXPassFailData.size() > 0 ){
			
			try {	
				int updatedMbaXPassFailListSize = assessmentService.updateMbaXPassFailGradePointsByTimeBoundIds(mbaXPassFailData, userId);
				request.setAttribute("success", "true");
				request.setAttribute("successMessage", "Grade Points Update For "+updatedMbaXPassFailListSize+" records");
			}catch(Exception e) {
				
				request.setAttribute("error", "true");
				request.setAttribute("errorMessage", "Grade Points Failed to Complete");
			}
	
		}else {
			request.setAttribute("error", "true");
			request.setAttribute("errorMessage", "No Records Found");
		}
		
		List<EmbaPassFailBean> updatedMbaXPassFailData = upgradAssessmentDao.getMbaXPassFailByTimeBoundId(commaSeparatedTimeBoundIds);		
		mav.addObject("mbaXPassFailDataSize", updatedMbaXPassFailData.size());
 		mav.addObject("mbaXPassFailData", updatedMbaXPassFailData);
 		request.getSession().setAttribute("updatedMbaXPassFailData", updatedMbaXPassFailData);
		return mav;
	}	

	
	/*
	 * @RequestMapping(value = "/upgradAddTest", method = RequestMethod.POST,
	 * consumes = "application/json", produces = "application/json") public
	 * ResponseEntity<ResponseBean> upgradAddTest(@RequestBody TestBean test) {
	 * assessmentService.saveUpgradAddTest(test);
	 * 
	 * }
	 */
	

	@RequestMapping(value = "/validateTestDetailsForm",   method = {RequestMethod.GET, RequestMethod.POST})
	public ModelAndView validateTestDetailsForm(HttpServletRequest request, HttpServletResponse response,
			@RequestParam(value = "error", required = false) String error, 
			@RequestParam( value ="errorMessage", required = false) String errorMessage,
			
			@RequestParam( value = "success", required = false) String success, 
			@RequestParam( value ="successMessage", required = false) String successMessage
			
			){

		ModelAndView m = new ModelAndView("/validateTestDetailsForm");
		
		  if(!checkSession(request, response)){
			  redirectToPortalApp(response); 
			  return null;
			  }
		  UpgradAssessmentExamBean upgradAssessmentBean = new  UpgradAssessmentExamBean ();
		m.addObject("upgradAssessmentBean",upgradAssessmentBean);
		m.addObject("examYearList",CURRENT_YEAR_LIST);
		m.addObject("examMonthList", examMonthList);
		m.addObject("acadsYearList", ACAD_YEAR_LIST);
		m.addObject("acadsMonthList", ACAD_MONTH_LIST);
		
		m.addObject("error", error);
		m.addObject("errorMessage", errorMessage);
		
		m.addObject("success", success);
		m.addObject("successMessage", successMessage);
		
		
		return m;
	}
	
	@RequestMapping(value = "/getSubjectDetails", method = RequestMethod.POST)
	public @ResponseBody List<UpgradAssessmentExamBean> getSubjectDetails(@RequestBody UpgradAssessmentExamBean upgradAssessmentBean) {
		return upgradAssessmentDao.getSubjectDetails( upgradAssessmentBean.getBatchId());

	}
	
	@RequestMapping(value = "/getBatchDetails", method = RequestMethod.POST)
	public @ResponseBody List<UpgradAssessmentExamBean> getBatchDetails( @RequestBody UpgradAssessmentExamBean upgradAssessmentBean ) {
		return  assessmentService.getBatchDetailsService(upgradAssessmentBean.getExamYear(), upgradAssessmentBean.getExamMonth(),  upgradAssessmentBean.getAcadYear(),
					upgradAssessmentBean.getAcadMonth());		
		

	}
	
	
	
	
	@GetMapping("/searchByTestId")
		public @ResponseBody ArrayList<UpgradAssessmentExamBean> searchByTestId(HttpServletRequest request, @RequestParam(value = "testId", required = true) Long testId) {
		Integer expectedStudentsCount = 0;
		ArrayList<UpgradAssessmentExamBean> UpgradAssessmentList  =   assessmentService.getCombinedDetailsOfScoreAndAssignmentDetailsToDisplayService(testId);
		request.getSession().setAttribute("UpgradAssessmentList", UpgradAssessmentList);
		
		try {
			expectedStudentsCount = assessmentService.fetchExpectedStudentsForTest(testId);
		}catch (Exception e) {
			logger.info(new Date() +" : UpgradResultProcessingController.searchByTestId() :"+e.getMessage()); 
		}
		
		if(UpgradAssessmentList!=null)
			UpgradAssessmentList.get(0).setExpectedSapIdCount(expectedStudentsCount);
		
		//return list
		return UpgradAssessmentList;

	   }
	 
	
	@RequestMapping(value = "/searchBySubjectForTest_ajax", method = RequestMethod.GET)
	   public @ResponseBody ArrayList<TestExamBean> subjectForTest(
			   @RequestParam(value = "examYear", required = true) Integer examYear,
			   @RequestParam(value = "examMonth", required = true) String examMonth,
			   @RequestParam(value = "acadYear", required = true) Integer acadYear,
			   @RequestParam(value = "acadMonth", required = true) String acadMonth,
			   @RequestParam(value = "subjectId", required = true) Integer pssId
			   
			   ) {

	    return (ArrayList<TestExamBean>) upgradAssessmentDao.FindBySubjectNameForTest(	examYear,
	    																			examMonth,
	    																			acadYear,
	    																			acadMonth,
	    																			pssId
	    																			);
	   }
	
	

	  
	  
	  @RequestMapping(value = "/getStudentAssessmentDetails", method = RequestMethod.GET) 
	  public @ResponseBody  ArrayList<UpgradAssessmentExamBean>  getStudentAssessmentDetails( @RequestParam(value = "sapid", required = true) String sapid,  @RequestParam(value = "testid", required = true) Long testId) {
		
		 return (ArrayList<UpgradAssessmentExamBean>)  assessmentService.getStudentAssessmentDetailsService(sapid, testId);

	  }
	 
	  
	  @RequestMapping(value = "/updateMarksObtained",   method = RequestMethod.POST , consumes="application/json", produces="application/json")
	  public ResponseEntity<HashMap<String, String>> updateMarksObtained(@RequestBody UpgradAssessmentExamBean upgradBean){
		  	HashMap<String, String> response = new  HashMap<String, String>();
			HttpHeaders headers = new HttpHeaders();
			headers.add("Content-Type", "application/json");
			
				HashMap<String,String> message = assessmentService.updateMarksObtainedService(upgradBean);
				if(message.containsKey("error")){
					
					response.put("Status", "Fail");
					response.put("Message", message.get("error"));
					return new ResponseEntity<HashMap<String, String>>(response, headers, HttpStatus.OK);
				}
				response.put("Status", "Success");
			
			return new ResponseEntity<HashMap<String, String>>(response, headers, HttpStatus.OK);
	  }
	  
	  @RequestMapping(value = "/downloadVerifyTestDataForUPGRAD", method = { RequestMethod.GET})
	  public ModelAndView downloadVerifyTestDataForUPGRAD(HttpServletRequest request, HttpServletResponse response) {
		
		  if(!checkSession(request, response)){
			  redirectToPortalApp(response);
			  return null;
			  }
		 
		  ArrayList<UpgradAssessmentExamBean> UpgradAssessmentList =  (ArrayList<UpgradAssessmentExamBean>) request.getSession().getAttribute("UpgradAssessmentList");
		  return new ModelAndView("verifyTestDataForUpgradExcelView", "UpgradAssessmentList", UpgradAssessmentList);
	  }
	
	
	
	@RequestMapping(value = "/validateTestDetailsStatusForm", produces = "application/json",  method = {RequestMethod.POST})
	public ModelAndView validateTestDetails(ModelMap model, HttpServletRequest request, HttpServletResponse response, @ModelAttribute UpgradAssessmentExamBean upgradAssessmentBean, Model m) {
//	    HashMap<String,String> message = assessmentService.saveUpdateBeforeNormalizeService(upgradAssessmentBean.getTestId());
//	    
//	    if(message.containsKey("error")) {
//	    	model.addAttribute("error", "true");
//	    	model.addAttribute("errorMessage", message.get("errorMessage"));
//	    	
//	    	 return new ModelAndView("redirect:/validateTestDetailsForm", model);
//	    }
//	    else {
		  
		    JsonObject responseJsonObject = new JsonObject();
		    HttpHeaders headers = new HttpHeaders();
			headers.add("Content-Type", "application/json");
			  
			try {
				UpgradAssessmentExamBean testData = assessmentService.getCombinedDetailsOfScoreAndAssignmentDetails(upgradAssessmentBean.getTestId());
				
			    Gson gson = new Gson();
			    String json = gson.toJson(testData); 
	
				String sessionId="1",courseId ="1";
				
				responseJsonObject = upgradHelper.postDataToUpgrad(json, PostDataToUpgradURL, courseId, sessionId);
				
				if(null != responseJsonObject.get("success")) {
					model.addAttribute("success", "true");
					model.addAttribute("successMessage", "Data Sent to Upgrad Successfully.");
				}
				if(null != responseJsonObject.get("errorList") || null != responseJsonObject.get("cause")) {
					model.addAttribute("error", "true");
					model.addAttribute("errorMessage", responseJsonObject.get("cause").toString());
					
				}
	
			}catch(Exception e) {
				model.addAttribute("error", "true");
				model.addAttribute("errorMessage", "Error in calling UPGRAD API.");
			}
		    
			return new ModelAndView("redirect:/validateTestDetailsForm", model);
	   // }
		
	}
	
	@RequestMapping(value = "/liveSettingMBAX", method = RequestMethod.GET)
	public String upgradViewMBAXAllTestForLiveSetting (HttpServletRequest request, HttpServletResponse response, Model m) {
		if(!checkSession(request, response)){ 
			redirectToPortalApp(response); 
		} 
		try {
			List<TestExamBean> ListMBAX = assessmentService.getAllMBAXTestForLiveSettingService();
			m.addAttribute("listMBAXTest", ListMBAX);
		}catch(Exception e) {
			
		}
		return "liveSettingsMBAX";
		
	}
	
	@RequestMapping(value = "/showResultsForMBAXTest", method =  RequestMethod.GET )
	  public String showResultForMBAXTest(HttpServletRequest request,
			   HttpServletResponse response,
			   Model m,
			   @RequestParam("testid") Long testId, @RequestParam("referenceId") Integer referenceId){

			
				 boolean testUpdated =  assessmentService.updateShowResultForMBAXTestService(testId, referenceId);

				 if(testUpdated) {
					 request.setAttribute("success", "true");
					 request.setAttribute("successMessage", "Result Status Updated   Successfully.");
				 }else {
					 request.setAttribute("error", "true");
					 request.setAttribute("errorMessage", "Failed To Update Result Status.");
				 }
			
			return upgradViewMBAXAllTestForLiveSetting(request, response, m);
	  }
	
	@RequestMapping(value = "/hideResultsForMBAXTest", method =  RequestMethod.GET )
	  public String hideResultForMBAXTest(HttpServletRequest request,
			   HttpServletResponse response,
			   Model m,
			   @RequestParam("testid") Long testId, @RequestParam("referenceId") Integer referenceId){
		  	
				 boolean testUpdated =  assessmentService.updateHideResultForMBAXTestService(testId, referenceId);

				 if(testUpdated) {
					 request.setAttribute("success", "true");
					 request.setAttribute("successMessage", "Result Status Updated   Successfully.");
				 }else {
					 request.setAttribute("error", "true");
					 request.setAttribute("errorMessage", "Failed To Update Result Status.");
				 }
			
			return upgradViewMBAXAllTestForLiveSetting(request, response, m);
	  }

	

	@RequestMapping(value = "/getMbaXPassFailData", produces = "application/json", method = {RequestMethod.GET})
	public ResponseEntity<ResponseBean> getMbaXPassFailData() {	
		
		return assessmentService.getMbaPassFailList();	
	}
	@RequestMapping(value="/downloadMbaXAbsoluteGrading", method=RequestMethod.GET)
	public ModelAndView downloadMbaXAbsoluteGrading(HttpServletRequest request, HttpServletResponse response) {
//		if(!checkSession(request, response)){
//			return new ModelAndView("studentPortalRediret");
//		}
		List<EmbaPassFailBean> updatedMbaXPassFailData= (List<EmbaPassFailBean>)request.getSession().getAttribute("updatedMbaXPassFailData");
		
		return new ModelAndView("mbaXPassFailExcelView","updatedMbaXPassFailData",updatedMbaXPassFailData);		

	}

	@RequestMapping(value="/uploadProjectMarksFormMBAX",method=RequestMethod.GET)
	public ModelAndView uploadTEEMarksForm(HttpServletRequest request,HttpServletResponse response) {
		if(!checkSession(request, response)){
			redirectToPortalApp(response);
			return null;
		}
		ModelAndView mv = new ModelAndView("mbax/uploadProjectMarks");
		mv.addObject("fileBean", new TEEResultBean());
		mv.addObject("batches", resultsService.getBatchList(119));
		mv.addObject("tee_marks", upgradResultProcessingDao.getTeeMarksStudentListForCapstone());
		return mv;
	}
	
	@RequestMapping(value="/uploadProjectMarksMBAX",method=RequestMethod.POST)
	public ModelAndView uploadTEEMarks(TEEResultBean teeResultBean, HttpServletRequest request,HttpServletResponse response) {
		if(!checkSession(request, response)){
			redirectToPortalApp(response);
			return null;
		}
		String userId = (String)request.getSession().getAttribute("userId");
		teeResultBean.setCreatedBy(userId);
		teeResultBean.setLastModifiedBy(userId);

		ModelAndView mv = new ModelAndView("mbax/uploadProjectMarks");
		mv.addObject("fileBean", teeResultBean);
		mv.addObject("batches", resultsService.getBatchList(119));
		try {

			
			ArrayList<MettlResponseBean> marksToInsert = mettlHelper.getMarksToUpsertForProject(teeResultBean);

			if(marksToInsert != null) {

				List<String> errorList = upgradResultProcessingDao.upsertTeeMarksForProjectSubject(marksToInsert);
				
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

			mv.addObject("tee_marks", upgradResultProcessingDao.getTeeMarksStudentListForCapstone());
		}catch (Exception e) {
			
		}
		return mv;
	}
	
	public ModelAndView runPassFailTriggerForProject(TEEResultBean resultBean, HttpServletRequest request, ModelAndView mav){
		
		String loggedInUser =(String)request.getSession().getAttribute("userId");

		ArrayList<TEEResultBean> studentsListEligibleForPassFail = upgradResultProcessingDao.getAllStudentsEligibleForProjectForPassFail(resultBean); //score cannot be null or blank and status <> 'Not Attempted' or null
		
		mav.addObject("studentsListEligibleForPassFailSize", studentsListEligibleForPassFail.size());
		ArrayList<EmbaPassFailBean> finalListforPassFail = new ArrayList<EmbaPassFailBean>();
		ArrayList<EmbaPassFailBean> unsuccessfulPassFail = new ArrayList<EmbaPassFailBean>();
		if(studentsListEligibleForPassFail.size()>0) {
			try{
				resultsService.passFailLogicForProject(resultBean, finalListforPassFail,studentsListEligibleForPassFail,loggedInUser,unsuccessfulPassFail );	
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

	//After the passfail logic is run then only the students present in finalListForPassFail are upserted into mba_passfail and corresponding flags are updated in teeMarks and history table.
	@RequestMapping(value = "/passFailTriggerForProjectMBAX",method = {RequestMethod.POST})
	public ModelAndView embaPassFailTriggerForProject(HttpServletRequest request,HttpServletResponse response,@ModelAttribute TEEResultBean resultBean) {
		if(!checkSession(request, response)){ 
			redirectToPortalApp(response); 
		} 
		
		ModelAndView mv = new ModelAndView("mbax/uploadProjectMarks");

		mv.addObject("fileBean", new TEEResultBean());
		mv.addObject("batches", resultsService.getBatchList(119));
		
		ArrayList<EmbaPassFailBean> finalListforPassFail =  (ArrayList<EmbaPassFailBean>) request.getSession().getAttribute("finalListforPassFail"); 
		ArrayList<EmbaPassFailBean> unsuccessfulPassFail =  (ArrayList<EmbaPassFailBean>) request.getSession().getAttribute("unsuccessfulPassFail");
		try {
			
			if(finalListforPassFail.size()>0) {
				
				upgradResultProcessingDao.upsertMBAXPassFail(finalListforPassFail); // upsert passfail table
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

		mv.addObject("batches", resultsService.getBatchList(119));
		mv.addObject("resultBean", resultBean);
		return mv;
	}
	

	@RequestMapping(value = "/projectGraceMarksFormMBAX",method = {RequestMethod.GET})
	public ModelAndView projectGraceMarksForm(HttpServletRequest request, HttpServletResponse response,@ModelAttribute TEEResultBean resultBean){
		if(!checkSession(request, response)){
			redirectToPortalApp(response);
			return null;
		}
		ModelAndView mav = new ModelAndView("mbax/projectGrace");
		mav.addObject("batches", resultsService.getBatchList(119));
		mav.addObject("resultBean", resultBean);
		return mav;
	}

	@RequestMapping(value = "/projectGraceMarksFormSearchMBAX",method = {RequestMethod.POST})
	public ModelAndView embaProjectGraceMarksSearch(HttpServletRequest request, HttpServletResponse response,@ModelAttribute TEEResultBean resultBean){
		if(!checkSession(request, response)){
			redirectToPortalApp(response);
			return null;
		}
		ModelAndView mav = new ModelAndView("mbax/projectGrace");
		mav.addObject("batches", resultsService.getBatchList(119));
		mav.addObject("resultBean", resultBean);
		if(StringUtils.isBlank(resultBean.getBatchId()) || StringUtils.isBlank(resultBean.getTimebound_id())) {
			request.setAttribute("error", "true");
			request.setAttribute("errorMessage", "Kindly select all field values. All fields are Mandatory");
			return mav;
		}
		
		//get list of all failed students using timeboundId and scheduleId.
		ArrayList<EmbaPassFailBean> studentsListForGrace = upgradResultProcessingDao.getAllFailedStudentsForGraceProject(resultBean.getTimebound_id());
		if(studentsListForGrace.isEmpty()) {
			request.setAttribute("error", "true");
			request.setAttribute("errorMessage", "No students found for grace");
			return mav;
		}
		String loggedInUser =(String)request.getSession().getAttribute("userId");
		ArrayList<EmbaPassFailBean> finalListforGrace = new ArrayList<EmbaPassFailBean>();
		for(EmbaPassFailBean bean : studentsListForGrace) {
			int grace = resultsService.calculateProjectGraceMarks(bean);// checking is grace is applicable
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

	@RequestMapping(value = "/projectGraceMarksMBAX",method = {RequestMethod.POST})
	public ModelAndView embaProjectGraceMarks(HttpServletRequest request,HttpServletResponse response,@ModelAttribute TEEResultBean resultBean) {
		if(!checkSession(request, response)){ 
			redirectToPortalApp(response); 
		} 
		ModelAndView mv = new ModelAndView("mbax/projectGrace");
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
			upgradResultProcessingDao.upsertMBAXPassFail(finalListforGrace); // upsert passfail table for grace marks
			request.setAttribute("success", "true");
			request.setAttribute("successMessage", "Grace Completed For "+finalListforGrace.size()+" records");
		}catch(Exception e) {
			
			request.setAttribute("error", "true");
			request.setAttribute("errorMessage", "Grace Failed to Complete");
		}
		mv.addObject("batches", resultsService.getBatchList(119));
		mv.addObject("resultBean", resultBean);
		return mv;
	}
	
	@RequestMapping(value = "/projectPassFailMakeLiveMBAXForm",method = {RequestMethod.GET})
	public ModelAndView embaProjectPassFailMakeLiveForm(HttpServletRequest request, HttpServletResponse response,@ModelAttribute TEEResultBean resultBean){
		if(!checkSession(request, response)){
			redirectToPortalApp(response);
			return null;
		}
		ModelAndView mav = new ModelAndView("mbax/projectPassFailMakeLive");
		mav.addObject("batches", resultsService.getBatchList(119));
		mav.addObject("resultBean", resultBean);
		return mav;
	}
	@RequestMapping(value = "/projectPassFailMakeLiveMBAX",method = {RequestMethod.POST})
	public ModelAndView projectPassFailMakeLiveMBAX(HttpServletRequest request, HttpServletResponse response,@ModelAttribute TEEResultBean resultBean){
		if(!checkSession(request, response)){
			redirectToPortalApp(response);
			return null;
		}
		ModelAndView mav = new ModelAndView("mbax/projectPassFailMakeLive");
		mav.addObject("batches", resultsService.getBatchList(119));
		mav.addObject("resultBean", resultBean);
		if(StringUtils.isBlank(resultBean.getBatchId()) || StringUtils.isBlank(resultBean.getTimebound_id())) {
			request.setAttribute("error", "true");
			request.setAttribute("errorMessage", "Kindly select all field values. All fields are Mandatory");
			return mav;
		}
		try {
			int count = upgradResultProcessingDao.passFailResultsForProjectLive(resultBean);
			request.setAttribute("success", "true");
			request.setAttribute("successMessage", "Result made live for "+count+" records");
		}catch(Exception e) {
			request.setAttribute("error", "true");
			request.setAttribute("errorMessage", "Error in make live for schedule id "+resultBean.getSchedule_id());
		}
		return mav;
	}

}
