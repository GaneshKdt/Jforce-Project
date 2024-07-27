package com.nmims.controllers;


import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.time.DateUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.ibm.icu.text.SimpleDateFormat;
import com.nmims.beans.ExamsAssessmentsBean;
import com.nmims.beans.MettlListResponseBean;
import com.nmims.beans.MettlResponseBean;
import com.nmims.beans.ResponseBean;
import com.nmims.beans.UpgradAssessmentExamBean;
import com.nmims.daos.UpgradAssessmentDao;
import com.nmims.daos.UpgradResultProcessingDao;
import com.nmims.helpers.MettlHelper;
import com.nmims.helpers.UpgradHelper;
import com.nmims.services.UpgradAssessmentService;
import com.nmims.services.UpgradResultProcessingService;

@CrossOrigin(origins = "*", allowedHeaders = "*")
@Controller
@RequestMapping("/admin")
public class UpgradAssessmentsController extends BaseController{

	@Value("#{'${ACAD_YEAR_LIST}'.split(',')}")
	private List<String> ACAD_YEAR_LIST;
	
	@Value("#{'${ACAD_MONTH_LIST}'.split(',')}") 
	private List<String> ACAD_MONTH_LIST; 
	
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
	UpgradAssessmentDao upgradAssessmentDao;
	
	@Autowired
	UpgradHelper upgradHelper;
	
	@Autowired
	UpgradAssessmentService assessmentService;
	
	@Autowired
	UpgradResultProcessingService resultsService;

	@Autowired
	@Qualifier("mbaxMettlHelper")
	MettlHelper mettlHelper;
	
	private static final Logger logger = LoggerFactory.getLogger(UpgradAssessmentsController.class);


	@RequestMapping(value="/mbaxExamAssessmentsPanelForm",method=RequestMethod.GET)
	public ModelAndView examAssessmentPanelForm(HttpServletRequest request,HttpServletResponse response) {
		if(!checkSession(request, response)){
			redirectToPortalApp(response);
			return null;
		}
		ModelAndView mv = new ModelAndView("/mbax/MBAXAddAssessment");
		ArrayList<ExamsAssessmentsBean> examsAssessmentsBeansList = upgradAssessmentDao.getExamAssessments();
		ArrayList<MettlResponseBean> mettlAssessmentResponseBeanList = new ArrayList<MettlResponseBean>();
		createMettlResponse(mettlAssessmentResponseBeanList);
		mv.addObject("assessmentList",mettlAssessmentResponseBeanList);
		mv.addObject("exam_assessments",examsAssessmentsBeansList);
		mv.addObject("batchList",resultsService.getBatchList(119));
		return mv;
	}
	
	@RequestMapping(value="/mbaxExamAssessmentsPanel",method=RequestMethod.POST)
	public ModelAndView examAssessmentPanel(ExamsAssessmentsBean examsAssessmentsBean,HttpServletRequest request,HttpServletResponse response) {
		if(!checkSession(request, response)){
			redirectToPortalApp(response);
			return null;
		}
		String userId = (String)request.getSession().getAttribute("userId");
		ModelAndView mv = new ModelAndView("/mbax/MBAXAddAssessment");
		
		String timeBoundId = upgradAssessmentDao.getTimeBoundId(examsAssessmentsBean.getSubject(), examsAssessmentsBean.getBatch_id());
		
		if(timeBoundId != null) {
			MettlHelper.checkAndExtendAssessmentTimeIfRequired(examsAssessmentsBean);
			examsAssessmentsBean.setTimebound_id(timeBoundId);
			examsAssessmentsBean.setCreatedBy(userId);
			examsAssessmentsBean.setLastModifiedBy(userId);
			int mappingExists = upgradAssessmentDao.checkIfAssessmentTimeBoundMappingExists(examsAssessmentsBean);
			if(mappingExists > 0) {
				request.setAttribute("error","true");
				request.setAttribute("errorMessage","Already Exists in Portal.");
			} else if(mappingExists < 0) {
				request.setAttribute("error","true");
				request.setAttribute("errorMessage","Unable to find if mapping exists");
			} else if(mappingExists == 0) {
				String result="";
				int assessmentExists = upgradAssessmentDao.checkIfAssessmentExists(examsAssessmentsBean);
				if(assessmentExists > 0) {
					
					if(examsAssessmentsBean.getMax_score().equalsIgnoreCase("100")) {
						
//						This logic will not be used anymore as two schedules share the same timetable entry
//						result = examsAssessmentsDAO.checkIfSlotExistsInTimeTable(examsAssessmentsBean);
//						if("success".equalsIgnoreCase(result)) {
							result = upgradAssessmentDao.insertIntoExamScheduleAndAssessmentTimebound(examsAssessmentsBean);
							if("success".equalsIgnoreCase(result)) {
								//get id from exam_schedules
								int id =upgradAssessmentDao.getExamScheduleId(examsAssessmentsBean);
								examsAssessmentsBean.setId(""+id);
								//update timetable
								
								try {
									String exam_start_date_time = examsAssessmentsBean.getExam_start_date_time();
									SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
									Date startDate = sdf.parse(exam_start_date_time);
									String  exam_end_date_time = sdf.format(DateUtils.addMinutes(startDate, examsAssessmentsBean.getDuration()));
									examsAssessmentsBean.setExam_end_date_time(exam_end_date_time);
								} catch (ParseException e) {
									// TODO Auto-generated catch block
									e.printStackTrace();
								}
								result=upgradAssessmentDao.updateScheduleIdInTimeTable(examsAssessmentsBean);
							}
//						}
						
					}else {
						result = upgradAssessmentDao.insertIntoExamScheduleAndAssessmentTimebound(examsAssessmentsBean);
					}
					
				}else if(assessmentExists == 0) {
					
					if(examsAssessmentsBean.getMax_score().equalsIgnoreCase("100")) {
//						result = examsAssessmentsDAO.checkIfSlotExistsInTimeTable(examsAssessmentsBean);
//						if("success".equalsIgnoreCase(result)) {
							result = upgradAssessmentDao.insertIntoExamScheduleAndAssessmentAndAssessmentTimebound(examsAssessmentsBean);
							if("success".equalsIgnoreCase(result)) {
								//get id from exam_schedules
								int id =upgradAssessmentDao.getExamScheduleId(examsAssessmentsBean);
								examsAssessmentsBean.setId(""+id);
								//update timetable
								
								try {
									String exam_start_date_time = examsAssessmentsBean.getExam_start_date_time();
									SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
									Date startDate = sdf.parse(exam_start_date_time);
									String  exam_end_date_time = sdf.format(DateUtils.addMinutes(startDate, examsAssessmentsBean.getDuration()));
									examsAssessmentsBean.setExam_end_date_time(exam_end_date_time);
								} catch (ParseException e) {
									// TODO Auto-generated catch block
									e.printStackTrace();
								}
								result=upgradAssessmentDao.updateScheduleIdInTimeTable(examsAssessmentsBean);
							}
//						}
						
					}else {
						result = upgradAssessmentDao.insertIntoExamScheduleAndAssessmentAndAssessmentTimebound(examsAssessmentsBean);
					}
				    
				}
				if("success".equalsIgnoreCase(result)) {
					request.setAttribute("success","true");
					request.setAttribute("successMessage","Successfully assessment created in portal");
				}else {
					request.setAttribute("error","true");
					request.setAttribute("errorMessage",result);
				}
			}
			
			
		}else {
			request.setAttribute("error","true");
			request.setAttribute("errorMessage","Null Timebound Id found try again");
		}
		
		ArrayList<ExamsAssessmentsBean> examsAssessmentsBeansList = upgradAssessmentDao.getExamAssessments();
		ArrayList<MettlResponseBean> mettlAssessmentResponseBeanList = new ArrayList<MettlResponseBean>();
		createMettlResponse(mettlAssessmentResponseBeanList);
		mv.addObject("assessmentList",mettlAssessmentResponseBeanList);
		mv.addObject("exam_assessments",examsAssessmentsBeansList);
		mv.addObject("batchList",resultsService.getBatchList(119));
		return mv;
	}
	
	private ArrayList<MettlResponseBean> createMettlResponse(ArrayList<MettlResponseBean> mettlAssessmentResponseBeanList){
		try {
			JsonObject jsonResponse = mettlHelper.getAssessments();
			if(jsonResponse != null) {
				String status = jsonResponse.get("status").getAsString();
				if("SUCCESS".equalsIgnoreCase(status)) {
					JsonArray assessmentList = jsonResponse.get("assessments").getAsJsonArray();
					for (JsonElement assessmentElement : assessmentList) {
						JsonObject assessmentObject = assessmentElement.getAsJsonObject();
						MettlResponseBean tmp_responseBean = new MettlResponseBean();
						tmp_responseBean.setAssessments_id(assessmentObject.get("id").getAsInt());
						tmp_responseBean.setName(assessmentObject.get("name").getAsString());
						tmp_responseBean.setCustomAssessmentName(assessmentObject.get("name").getAsString());
						mettlAssessmentResponseBeanList.add(tmp_responseBean);
					}
					
				}
			}
			return mettlAssessmentResponseBeanList;
		} catch (Exception e) {
			// TODO: handle exception
			
			return mettlAssessmentResponseBeanList;
		}
	}
	
	@RequestMapping(value="/getMBAXScheduleFromAssessmentId",method=RequestMethod.GET)
	public @ResponseBody MettlListResponseBean getScheduleFromAssessmentId(HttpServletRequest request){
		MettlListResponseBean responseBean = new MettlListResponseBean();
		ArrayList<MettlResponseBean> mettlResponseBeanList = new ArrayList<MettlResponseBean>();
		if(request.getParameter("id") == null) {
			responseBean.setStatus("error");
			responseBean.setMessage("Assessment Id missing");
			return responseBean;
		}
		int assessment_id = Integer.parseInt(request.getParameter("id").toString());
		try {
			JsonObject jsonResponse = mettlHelper.getSchedulesFromAssessmentId(assessment_id);
			if(jsonResponse != null) {
				String status = jsonResponse.get("status").getAsString();
				if("SUCCESS".equalsIgnoreCase(status)) {
					JsonArray assessmentList = jsonResponse.get("schedules").getAsJsonArray();
					for (JsonElement assessmentElement : assessmentList) {
						JsonObject assessmentObject = assessmentElement.getAsJsonObject();
						MettlResponseBean tmp_responseBean = new MettlResponseBean();
						tmp_responseBean.setAssessments_id(assessment_id);
						tmp_responseBean.setSchedule_id(assessmentObject.get("id").getAsString());
						tmp_responseBean.setSchedule_name(assessmentObject.get("name").getAsString());
						tmp_responseBean.setSchedule_accessKey(assessmentObject.get("accessKey").getAsString());
						tmp_responseBean.setSchedule_accessUrl(assessmentObject.get("accessUrl").getAsString());
						tmp_responseBean.setSchedule_status(assessmentObject.get("status").getAsString());
						if(assessmentObject.get("scheduleWindow") != null) {
							JsonObject scheduleWindow = assessmentObject.get("scheduleWindow").getAsJsonObject();
							if(!scheduleWindow.get("startsOnDate").isJsonNull()) {
								tmp_responseBean.setStartOnDate(scheduleWindow.get("startsOnDate").getAsString());
							}
							if(!scheduleWindow.get("startsOnTime").isJsonNull()) {
								tmp_responseBean.setStartsOnTime(scheduleWindow.get("startsOnTime").getAsString());
							}
							if(!scheduleWindow.get("endsOnDate").isJsonNull()) {
								tmp_responseBean.setEndsOnDate(scheduleWindow.get("endsOnDate").getAsString());
							}
							if(!scheduleWindow.get("endsOnTime").isJsonNull()) {
								tmp_responseBean.setEndsOnTime(scheduleWindow.get("endsOnTime").getAsString());
							}
						}
						JsonObject scheduleJsonResponse = mettlHelper.getScheduleDetailsFromAccessKey(tmp_responseBean.getSchedule_accessKey());
						if(scheduleJsonResponse != null) {
							String scheduleStatus = scheduleJsonResponse.get("status").getAsString();
							if("SUCCESS".equalsIgnoreCase(scheduleStatus)) {
								JsonObject schedule = scheduleJsonResponse.get("schedule").getAsJsonObject();
								if(schedule.get("assessmentDetails") != null) {
									JsonObject assessmentDetails = schedule.get("assessmentDetails").getAsJsonObject();
									if(!assessmentDetails.get("duration").isJsonNull()) {
										tmp_responseBean.setDuration(assessmentDetails.get("duration").getAsInt());
									}
								}
							}
						}
						mettlResponseBeanList.add(tmp_responseBean);
					}
					responseBean.setMettlResponseBeans(mettlResponseBeanList);
					responseBean.setStatus("success");
					return responseBean;
				}else {
					// error response from mettl
					JsonObject error = jsonResponse.get("error").getAsJsonObject();
					responseBean.setStatus("error");
					responseBean.setMessage("Code : " + error.get("code").getAsString() + " | message : " + error.get("message").getAsString());
					return responseBean;
				}
			}
			responseBean.setStatus("error");
			responseBean.setMessage("Invalid or null response found");
			return responseBean; 
		}
		catch (Exception e) {
			// TODO: handle exception
			responseBean.setStatus("error");
			responseBean.setMessage("Error: " + e.getMessage());
			return responseBean; 
		}
		
	}

//	to be deleted. api shifted to rest controller
//	@RequestMapping(value = "/m/viewTestDetailsForStudentsMBAX", method = RequestMethod.POST)
//	public ResponseEntity<ResponseBean> getStudentIATestDetails(
//			@RequestParam("sapId") String sapid,
//			@RequestParam("testId") Long testId){
//		
//		HttpHeaders headers = new HttpHeaders();
//		headers.add("Content-Type", "application/json");
//		ResponseBean responseBean = assessmentService.getStudentIATestDetailsService(sapid, testId);
//		return  new ResponseEntity<ResponseBean>(responseBean,headers, HttpStatus.OK);
//	} 
	
	@GetMapping("/mbaxExamAssessmentsDetails")
	public String examAssessmentData(HttpServletRequest request,HttpServletResponse response, Map<String,Object> map) {
		List<ExamsAssessmentsBean> examsAssessmentsBeansList=null;
		
		if(!checkSession(request, response)){
			redirectToPortalApp(response);
			return null;
		}
		
		//Use DAO and get all Exam Assessments details
		examsAssessmentsBeansList = upgradAssessmentDao.getExamAssessments();
		
		//add examsAssessmentsBeansList in map
		map.put("exam_assessments",examsAssessmentsBeansList);
		
		//return logical view name
		return "/mbax/MBAXUpdateExamAssessmentEndTime";
	}//examAssessmentData()
	
	
	//To update the Exam Assessment Exam End Time fro MBA-X
	@RequestMapping(value = "/updateExamAssessmentDateTime",  method = RequestMethod.POST , consumes="application/json", produces="application/json")
	public ResponseEntity<Map<String, String>> updateExamsAssessmentEndTime(@RequestBody ExamsAssessmentsBean examAssmtBean){
		Map<String, String> response =null;
		HttpHeaders headers = null;
		String result=null;
		
		response = new  HashMap<String, String>();
		headers = new HttpHeaders();
		//Add header content type as json
		headers.add("Content-Type", "application/json");
		
		try {
			
			//Validate extendedEndTime if it is empty then return error message
			if("".equalsIgnoreCase(examAssmtBean.getExtendExamEndTime()) || examAssmtBean.getExtendExamEndTime().length() == 0) {
				response.put("Status", "Fail");
				response.put("message", "Please select Exam End Time.");
				return new ResponseEntity<Map<String, String>>(response, headers, HttpStatus.OK);
			}

			//Use DAO and call updateExamAssessmentDateTime() by passing assessment_id, extendEndTime and end date in examAssmtBean  
			result=upgradAssessmentDao.updateExamAssessmentEndTime(examAssmtBean);
			
			//If result is success then put Status as Success in response object
			if("success".equalsIgnoreCase(result))
				response.put("Status", "Success");
			
		}catch (Exception e) {
			
			response.put("Status", "Fail");
			response.put("message", "Error while updating details. Please retry");
		}
	
		return new ResponseEntity<Map<String, String>>(response, headers, HttpStatus.OK);
	}//updateExamsAssessmentDateTime()
}
