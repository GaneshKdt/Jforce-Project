package com.nmims.controllers;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.time.DateUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
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
import com.nmims.beans.BatchExamBean;
import com.nmims.beans.ExamsAssessmentsBean;
import com.nmims.beans.MettlListResponseBean;
import com.nmims.beans.MettlRegisterCandidateBean;
import com.nmims.beans.MettlResponseBean;
import com.nmims.beans.MettlScheduleExamBean;
import com.nmims.beans.ScheduleCreationBean;
import com.nmims.beans.StudentExamBean;
import com.nmims.beans.StudentSubjectConfigExamBean;
import com.nmims.beans.TEEResultBean;
import com.nmims.daos.ExamsAssessmentsDAO;
import com.nmims.factory.MettlAssessmentsFactory;
import com.nmims.helpers.DateHelper;
import com.nmims.helpers.MettlHelper;
import com.nmims.interfaces.MettlAssessments;
import com.nmims.services.MettlTeeMarksService;

@Controller
@CrossOrigin(origins = "*", allowedHeaders = "*")
public class ExamController extends BaseController{
	
	@Autowired(required=false)
	ApplicationContext act;
	
	@Value( "${CURRENT_BATCHES_LIST}" )
	private String[] CURRENT_BATCHES_LIST;


	@Autowired
	@Qualifier("mbaWxMettlHelper")
	MettlHelper mbawxMettlHelper;
	
	@Autowired
	@Qualifier("mscMettlHelper")
	MettlHelper mscMettlHelper;
	
	@Autowired
	@Qualifier("pddmMettlHelper")
	MettlHelper pddmMettlHelper;
	
	@Value("${CURRENT_MBAWX_ACAD_MONTH}")
	private String CURRENT_MBAWX_ACAD_MONTH;
	
	@Value("${CURRENT_MBAWX_ACAD_YEAR}")
	private String CURRENT_MBAWX_ACAD_YEAR;
	
	@Value("${CURRENT_PDDM_ACAD_MONTH}")
	private String CURRENT_PDDM_ACAD_MONTH;
	
	@Value("${CURRENT_PDDM_ACAD_YEAR}")
	private String CURRENT_PDDM_ACAD_YEAR;
	
	@Value("${ACAD_MONTH_LIST}")
	private String ACAD_MONTH_LIST;
	
	@Value("${ACAD_YEAR_LIST}")
	private String ACAD_YEAR_LIST;
	
	@Autowired
	MettlAssessmentsFactory mettlAssessmentsFactory;
	
	@Autowired
	MettlTeeMarksService mettlTeeMarksService;
	
	@Autowired
	ExamsAssessmentsDAO examsAssessmentsDAO;
	
	private static final Logger logger = LoggerFactory.getLogger("examRegisterPG");
	
	@RequestMapping(value="/admin/getAssessmentList",method=RequestMethod.POST)
	public @ResponseBody MettlListResponseBean getAssessmentList(@RequestBody MettlResponseBean mettlResponseBean) {
		MettlListResponseBean responseBean = new MettlListResponseBean();
		ArrayList<MettlResponseBean> mettlResponseBeanList = new ArrayList<MettlResponseBean>();
		try {

			JsonObject jsonResponse = mbawxMettlHelper.getAssessments();
			if(jsonResponse != null) {
				String status = jsonResponse.get("status").getAsString();
				if("SUCCESS".equalsIgnoreCase(status)) {
					JsonArray assessmentList = jsonResponse.get("assessments").getAsJsonArray();
					for (JsonElement assessmentElement : assessmentList) {
						JsonObject assessmentObject = assessmentElement.getAsJsonObject();
						MettlResponseBean tmp_responseBean = new MettlResponseBean();
						tmp_responseBean.setAssessments_id(assessmentObject.get("id").getAsInt());
						tmp_responseBean.setName(assessmentObject.get("name").getAsString());
						tmp_responseBean.setCustomAssessmentName(assessmentObject.get("customAssessmentName").getAsString());
						mettlResponseBeanList.add(tmp_responseBean);
					}
					responseBean.setMettlResponseBeans(mettlResponseBeanList);
					responseBean.setStatus("success");
					return responseBean;
				}else {
					// error response from mettl
					responseBean.setStatus("error");
					responseBean.setMessage("Error response from mettl");
					return responseBean;
				}
			}
			else {
				// error response 
				responseBean.setStatus("error");
				responseBean.setMessage("Null or empty result found");
				return responseBean;
			}
		}
		catch (Exception e) {
			// TODO: handle exception
			responseBean.setStatus("error");
			responseBean.setMessage("Error: " + e.getMessage());
			return responseBean; 
		}
	} 
	@RequestMapping(value="/admin/getScheduleFromAssessmentId",method=RequestMethod.GET)
	public @ResponseBody MettlListResponseBean getScheduleFromAssessmentId(HttpServletRequest request){
		MettlListResponseBean responseBean = new MettlListResponseBean();
		ArrayList<MettlResponseBean> mettlResponseBeanList = new ArrayList<MettlResponseBean>();
		if(request.getParameter("id") == null) {
			responseBean.setStatus("error");
			responseBean.setMessage("Assessment Id missing");
			return responseBean;
		}
		if(request.getParameter("programType") == null) {
			responseBean.setStatus("error");
			responseBean.setMessage("Program Type missing");
			return responseBean;
		}
		int assessment_id = Integer.parseInt(request.getParameter("id").toString());
		try {
			MettlHelper mettlHelper;
			if("MBA - WX".equals(request.getParameter("programType"))) {
				mettlHelper = mbawxMettlHelper;
			} else if("M.Sc. (AI & ML Ops)".equals(request.getParameter("programType")) || "M.Sc. (AI)".equals(request.getParameter("programType")) ) {
				mettlHelper = mscMettlHelper;
			} else if("Modular PD-DM".equals(request.getParameter("programType"))) {
				mettlHelper = pddmMettlHelper;
			}else {
				responseBean.setStatus("error");
				responseBean.setMessage("Invalid Program Type entered!");
				return responseBean;
			}
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
	
	
	@RequestMapping(value="/admin/examAssessmentPanelForm",method=RequestMethod.GET)
	public ModelAndView examAssessmentPanelForm(HttpServletRequest request,HttpServletResponse response) {
		if(!checkSession(request, response)){
			redirectToPortalApp(response);
			return null;
		}
		ModelAndView mv = new ModelAndView("examsAssessmentsPanel");
		mv.addObject("acadYear", ACAD_YEAR_LIST);
		mv.addObject("acadMonth", ACAD_MONTH_LIST);
		ExamsAssessmentsDAO examsAssessmentsDAO = (ExamsAssessmentsDAO)act.getBean("examsAssessmentsDAO");
		ArrayList<ExamsAssessmentsBean> examsAssessmentsBeansList = examsAssessmentsDAO.getExamAssessments();
		mv.addObject("exam_assessments",examsAssessmentsBeansList);
		return mv;
	}
	
	@RequestMapping(value="/admin/examAssessmentPanel",method=RequestMethod.POST)
	public ModelAndView examAssessmentPanel(ExamsAssessmentsBean examsAssessmentsBean,HttpServletRequest request,HttpServletResponse response) {
		if(!checkSession(request, response)){
			redirectToPortalApp(response);
			return null;
		}
		String userId = (String)request.getSession().getAttribute("userId");
		ModelAndView mv = new ModelAndView("examsAssessmentsPanel");
		mv.addObject("acadYear", ACAD_YEAR_LIST);
		mv.addObject("acadMonth", ACAD_MONTH_LIST);
		ExamsAssessmentsDAO examsAssessmentsDAO = (ExamsAssessmentsDAO)act.getBean("examsAssessmentsDAO");
		String timeBoundId = examsAssessmentsDAO.getTimeBoundId(examsAssessmentsBean.getSubject(), examsAssessmentsBean.getBatch_id(), examsAssessmentsBean.getStartDate());
		
		if(timeBoundId != null) {
			
			examsAssessmentsBean.setTimebound_id(timeBoundId);
			examsAssessmentsBean.setCreatedBy(userId);
			examsAssessmentsBean.setLastModifiedBy(userId);
			int mappingExists = examsAssessmentsDAO.checkIfAssessmentTimeBoundMappingExists(examsAssessmentsBean);
			if(mappingExists > 0) {
				request.setAttribute("error","true");
				request.setAttribute("errorMessage","Already Exists in Portal.");
			}else if(mappingExists < 0) {
				request.setAttribute("error","true");
				request.setAttribute("errorMessage","Unable to find if mapping exists");
			}else if(mappingExists == 0) {
				MettlHelper.checkAndExtendAssessmentTimeIfRequired(examsAssessmentsBean);
				String result="";
				int assessmentExists = examsAssessmentsDAO.checkIfAssessmentExists(examsAssessmentsBean.getAssessments_id());
				if(assessmentExists > 0) {
					
					if(examsAssessmentsBean.getMax_score().equalsIgnoreCase("100")) {
						
//						This logic will not be used anymore as two schedules share the same timetable entry
//						result = examsAssessmentsDAO.checkIfSlotExistsInTimeTable(examsAssessmentsBean);
//						if("success".equalsIgnoreCase(result)) {
							result = examsAssessmentsDAO.insertIntoExamScheduleAndAssessmentTimebound(examsAssessmentsBean);
							if("success".equalsIgnoreCase(result)) {
								//get id from exam_schedules
								int id =examsAssessmentsDAO.getExamScheduleId(examsAssessmentsBean);
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
								result=examsAssessmentsDAO.updateScheduleIdInTimeTable(examsAssessmentsBean);
							}
//						}
						
					}else {
						result = examsAssessmentsDAO.insertIntoExamScheduleAndAssessmentTimebound(examsAssessmentsBean);
					}
					
				}else if(assessmentExists == 0) {
					
					if(examsAssessmentsBean.getMax_score().equalsIgnoreCase("100")) {
//						result = examsAssessmentsDAO.checkIfSlotExistsInTimeTable(examsAssessmentsBean);
//						if("success".equalsIgnoreCase(result)) {
							result = examsAssessmentsDAO.insertIntoExamScheduleAndAssessmentAndAssessmentTimebound(examsAssessmentsBean);
							if("success".equalsIgnoreCase(result)) {
								//get id from exam_schedules
								int id =examsAssessmentsDAO.getExamScheduleId(examsAssessmentsBean);
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
								result=examsAssessmentsDAO.updateScheduleIdInTimeTable(examsAssessmentsBean);
							}
//						}
						
					}else {
						result = examsAssessmentsDAO.insertIntoExamScheduleAndAssessmentAndAssessmentTimebound(examsAssessmentsBean);
					}
				    
				}
				if("success".equalsIgnoreCase(result)) {

					request.setAttribute("success","true");
					request.setAttribute("successMessage","Successfully assessment created in portal");
					
//					Post creation logic commented out.
//					boolean result2 = examsAssessmentsDAO.insertAssessmentInPost(examsAssessmentsBean);
//					if(result2) {
//						request.setAttribute("success","true");
//						request.setAttribute("successMessage","Successfully assessment created in portal");
//					} else {
//						request.setAttribute("error","true");
//						request.setAttribute("errorMessage","Successfully assessment created in portal. Post creation failed!");
//					}
				}else {
					request.setAttribute("error","true");
					request.setAttribute("errorMessage",result);
				}
			}
			
			
		}else {
			request.setAttribute("error","true");
			request.setAttribute("errorMessage","Null Timebound Id found try again");
		}
		
		ArrayList<ExamsAssessmentsBean> examsAssessmentsBeansList = examsAssessmentsDAO.getExamAssessments();
		mv.addObject("exam_assessments",examsAssessmentsBeansList);
		return mv;
	}
	
	@RequestMapping(value="/admin/uploadTEEMarksForm",method=RequestMethod.GET)
	public ModelAndView uploadTEEMarksForm(HttpServletRequest request,HttpServletResponse response) {
		if(!checkSession(request, response)){
			redirectToPortalApp(response);
			return null;
		}
		ModelAndView mv = new ModelAndView("uploadTEEMarks");
		ExamsAssessmentsDAO examsAssessmentsDAO = (ExamsAssessmentsDAO)act.getBean("examsAssessmentsDAO");
		ArrayList<TEEResultBean> teeResultBeans = (ArrayList<TEEResultBean>) examsAssessmentsDAO.getTeeMarksStudentList();
		TEEResultBean fileBean = new TEEResultBean();
		mv.addObject("fileBean",fileBean);
		mv.addObject("batches", CURRENT_BATCHES_LIST);
		mv.addObject("tee_marks",teeResultBeans);
		return mv;
	}
	
	@RequestMapping(value="/admin/uploadTEEMarks",method=RequestMethod.POST)
	public ModelAndView uploadTEEMarks(TEEResultBean teeResultBean , HttpServletRequest request,HttpServletResponse response) {
		if(!checkSession(request, response)){
			redirectToPortalApp(response);
			return null;
		}
		String userId = (String)request.getSession().getAttribute("userId");
		teeResultBean.setCreatedBy(userId);
		teeResultBean.setLastModifiedBy(userId);
		ModelAndView mv = new ModelAndView("uploadTEEMarks");
		String statusMessage = mbawxMettlHelper.addExcelMarksIntoPortal(teeResultBean);
		ExamsAssessmentsDAO examsAssessmentsDAO = (ExamsAssessmentsDAO)act.getBean("examsAssessmentsDAO");
		ArrayList<TEEResultBean> teeResultBeans = (ArrayList<TEEResultBean>) examsAssessmentsDAO.getTeeMarksStudentList();
		if(statusMessage.indexOf("Successfully recording created,") != -1) {
			request.setAttribute("success","true");
			request.setAttribute("successMessage",statusMessage);
		}else {
			request.setAttribute("error","true");
			request.setAttribute("errorMessage",statusMessage);
		}
		TEEResultBean fileBean = new TEEResultBean();
		mv.addObject("fileBean",fileBean);
		mv.addObject("batches", CURRENT_BATCHES_LIST);
		mv.addObject("tee_marks",teeResultBeans);
		return mv;
	}
	
	@RequestMapping(value="/admin/getAssessmentListByProgramType",method=RequestMethod.GET)
	public @ResponseBody MettlListResponseBean getAssessmentListByProgramType(HttpServletRequest request) {
		
		

		MettlListResponseBean responseBean = new MettlListResponseBean();
		if(request.getParameter("programType") == null) {
			responseBean.setStatus("error");
			responseBean.setMessage("Assessment Id missing");
			return responseBean;
		}
		try {
			ArrayList<MettlResponseBean> mettlAssessmentResponseBeanList = new ArrayList<MettlResponseBean>();
			
			MettlAssessments assessments= mettlAssessmentsFactory.getProductType(request.getParameter("programType"));
			mettlAssessmentResponseBeanList =  assessments.getAllAssessments();
			responseBean.setStatus("success");
			responseBean.setMettlResponseBeans(mettlAssessmentResponseBeanList);
			return responseBean; 
		} catch (Exception e) {
			// TODO: handle exception
			responseBean.setStatus("error");
			responseBean.setMessage("Error: " + e.getMessage());
			return responseBean; 
		}
	}
	
	@RequestMapping(value="/admin/getScheduleListByAssessment",method=RequestMethod.GET)
	public @ResponseBody ArrayList<MettlResponseBean> getScheduleListByAssessment(HttpServletRequest request) {
		if(request.getParameter("id") == null) {
			return new  ArrayList<MettlResponseBean>();
		}
		ExamsAssessmentsDAO examsAssessmentsDAO = (ExamsAssessmentsDAO)act.getBean("examsAssessmentsDAO");
		ArrayList<MettlResponseBean> scheduleList = examsAssessmentsDAO.getScheduleListByAssessmentId(Integer.parseInt(request.getParameter("id")),Integer.parseInt(request.getParameter("timeid")));
		return scheduleList;
	}
	
	/*
	 * @RequestMapping(value="/getMBAXScheduleListByAssessment",method=RequestMethod
	 * .GET) public @ResponseBody ArrayList<MettlResponseBean>
	 * getMBAXScheduleListByAssessment(HttpServletRequest request) {
	 * if(request.getParameter("id") == null) { return new
	 * ArrayList<MettlResponseBean>(); } ExamsAssessmentsDAO examsAssessmentsDAO =
	 * (ExamsAssessmentsDAO)act.getBean("examsAssessmentsDAO");
	 * ArrayList<MettlResponseBean> scheduleList =
	 * examsAssessmentsDAO.getMBAXScheduleListByAssessmentId(Integer.parseInt(
	 * request.getParameter("id")),Integer.parseInt(request.getParameter("timeid")))
	 * ; return scheduleList; }
	 */
	
	
	@RequestMapping(value="/admin/getSubjectListByBatchId",method=RequestMethod.GET)
	public @ResponseBody ArrayList<StudentSubjectConfigExamBean> getSubjectListByBatchId(HttpServletRequest request) {
		if(request.getParameter("id") == null) {
			return new  ArrayList<StudentSubjectConfigExamBean>();
		}
		ExamsAssessmentsDAO examsAssessmentsDAO = (ExamsAssessmentsDAO)act.getBean("examsAssessmentsDAO");
		ArrayList<StudentSubjectConfigExamBean> subjectList = examsAssessmentsDAO.getSubjectByBatchId(Integer.parseInt(request.getParameter("id")));
		return subjectList;
	}
	
	@RequestMapping(value="/admin/getAssessmentListByTimeBoundId",method=RequestMethod.GET)
	public @ResponseBody ArrayList<MettlResponseBean> getAssessmentListByTimeBoundId(HttpServletRequest request) {
		if(request.getParameter("id") == null) {
			return new  ArrayList<MettlResponseBean>();
		}
		ExamsAssessmentsDAO examsAssessmentsDAO = (ExamsAssessmentsDAO)act.getBean("examsAssessmentsDAO");
		ArrayList<MettlResponseBean> assessmentList = examsAssessmentsDAO.getAssessmentListByTimeBoundId(Integer.parseInt(request.getParameter("id")));
		return assessmentList;
	}
	
	/*
	 * @RequestMapping(value="/getMBAXAssessmentListByTimeBoundId",method=
	 * RequestMethod.GET) public @ResponseBody ArrayList<MettlResponseBean>
	 * getMBAXAssessmentListByTimeBoundId(HttpServletRequest request) {
	 * if(request.getParameter("id") == null) { return new
	 * ArrayList<MettlResponseBean>(); } ExamsAssessmentsDAO examsAssessmentsDAO =
	 * (ExamsAssessmentsDAO)act.getBean("examsAssessmentsDAO");
	 * ArrayList<MettlResponseBean> assessmentList =
	 * examsAssessmentsDAO.getMBXAssessmentListByTimeBoundId(Integer.parseInt(
	 * request.getParameter("id"))); return assessmentList; }
	 */

	
	@RequestMapping(value="/api/getTEEResultsBySapid",method=RequestMethod.POST)
	public @ResponseBody List<TEEResultBean> getTEEResultsBySapid(HttpServletRequest request, @RequestBody StudentExamBean input) {
		if(StringUtils.isBlank(input.getSapid())) {
			return new ArrayList<TEEResultBean>();
		}
		ExamsAssessmentsDAO examsAssessmentsDAO = (ExamsAssessmentsDAO)act.getBean("examsAssessmentsDAO");
		List<TEEResultBean> assessmentList = examsAssessmentsDAO.getAllTEEScoresForStudent(input.getSapid());
		return assessmentList;
	}
	
//	to be deleted api shifted to restcontroller
//	@RequestMapping(value = "/m/getTeeAssessmentsBySapid",  method = RequestMethod.POST, consumes = "application/json", produces = "application/json")
//	public  ResponseEntity<List<ExamsAssessmentsBean>> getTeeAssessmentsBySapid(@RequestBody StudentBean student) {
//		HttpHeaders headers = new HttpHeaders();
//        headers.add("Content-Type", "application/json"); 
//        ExamsAssessmentsDAO examsAssessmentsDAO = (ExamsAssessmentsDAO)act.getBean("examsAssessmentsDAO");
//        List<ExamsAssessmentsBean> teeAssessmentsList = new ArrayList<ExamsAssessmentsBean>();      
//       
//        try {
//        teeAssessmentsList = examsAssessmentsDAO.getTeeAssessmentsBySapid(student.getSapid());
//        }catch(Exception e) {
//        	
//        }
//		return new ResponseEntity<List<ExamsAssessmentsBean>>(teeAssessmentsList, headers,  HttpStatus.OK);
//	}
	
//	to be deleted, api shifted to rest controller
//	@RequestMapping(value = "/m/getUpcomingTeeAssessmentsBySapid",  method = RequestMethod.POST)
//	public  ResponseEntity<List<ExamsAssessmentsBean>> getUpcomingTeeAssessmentsBySapid(@RequestBody StudentBean student) {
//		HttpHeaders headers = new HttpHeaders();
//        headers.add("Content-Type", "application/json"); 
//        ExamsAssessmentsDAO examsAssessmentsDAO = (ExamsAssessmentsDAO)act.getBean("examsAssessmentsDAO");
//        List<ExamsAssessmentsBean> teeUpcomingAssessmentsList = new ArrayList<ExamsAssessmentsBean>();      
//        try {
//        	teeUpcomingAssessmentsList = examsAssessmentsDAO.getUpcomingTeeAssessmentsBySapid(student.getSapid());
//        }catch(Exception e) {
//        	
//        }
//		return new ResponseEntity<List<ExamsAssessmentsBean>>(teeUpcomingAssessmentsList, headers,  HttpStatus.OK);
//	}
	
	
//	to be deleted, api shifted to rest controller
//	@RequestMapping(value = "/m/getPendingTeeAssessmentsBySapid",  method = RequestMethod.POST)
//	public  ResponseEntity<List<ExamsAssessmentsBean>> getPendingTeeAssessmentsBySapid(@RequestBody StudentBean student) {
//		HttpHeaders headers = new HttpHeaders();
//        headers.add("Content-Type", "application/json"); 
//        ExamsAssessmentsDAO examsAssessmentsDAO = (ExamsAssessmentsDAO)act.getBean("examsAssessmentsDAO");
//        List<ExamsAssessmentsBean> teePendingAssessmentsList = new ArrayList<ExamsAssessmentsBean>();      
//        try {
//        	teePendingAssessmentsList = examsAssessmentsDAO.getPendingTeeAssessmentsBySapid(student.getSapid());
//        }catch(Exception e) {
//        	
//        }
//		return new ResponseEntity<List<ExamsAssessmentsBean>>(teePendingAssessmentsList, headers,  HttpStatus.OK);
//	}
	
	
//	to be deleted, api shifted to rest controller
//	@RequestMapping(value = "/m/getFinishedTeeAssessmentsBySapid",  method = RequestMethod.POST)
//	public  ResponseEntity<List<ExamsAssessmentsBean>> getFinishedTeeAssessmentsBySapid(@RequestBody StudentBean student) {
//		HttpHeaders headers = new HttpHeaders();
//        headers.add("Content-Type", "application/json"); 
//        ExamsAssessmentsDAO examsAssessmentsDAO = (ExamsAssessmentsDAO)act.getBean("examsAssessmentsDAO");
//        List<ExamsAssessmentsBean> teeFinishedAssessmentsList = new ArrayList<ExamsAssessmentsBean>();      
//        try {
//        	teeFinishedAssessmentsList = examsAssessmentsDAO.getFinishedTeeAssessmentsBySapid(student.getSapid());
//        }catch(Exception e) {
//        	
//        }
//		return new ResponseEntity<List<ExamsAssessmentsBean>>(teeFinishedAssessmentsList, headers,  HttpStatus.OK);
//	}
	
	
//	to be deleted, api shifted to rest controller
//	@RequestMapping(value = "/m/getTeeAssessmentsByExamScheduleId",  method = RequestMethod.POST, consumes = "application/json", produces = "application/json")
//	public  ResponseEntity<List<ExamsAssessmentsBean>> getTeeAssessmentsByExamScheduleId(@RequestParam("id") String id) {
//		HttpHeaders headers = new HttpHeaders();
//        headers.add("Content-Type", "application/json"); 
//        ExamsAssessmentsDAO examsAssessmentsDAO = (ExamsAssessmentsDAO)act.getBean("examsAssessmentsDAO");
//        List<ExamsAssessmentsBean> teeAssessmentList = new ArrayList<ExamsAssessmentsBean>();      
//       
//        try {
//        	teeAssessmentList = examsAssessmentsDAO.getTeeAssessmentsByExamScheduleId(id);
//        }catch(Exception e) {
//        	
//        }
//		return new ResponseEntity<List<ExamsAssessmentsBean>>(teeAssessmentList, headers,  HttpStatus.OK);
//	}
	
	@GetMapping(value="/admin/examAssessmentDetails")
	public String examAssessmentDetails(Map<String,Object> map,HttpServletRequest request,HttpServletResponse response) {
		List<ExamsAssessmentsBean> examsAssessmentsBeansList=null;
		
		if(!checkSession(request, response)){
			redirectToPortalApp(response);
			return null;
		}
		
		//Get all Exam Assessment details 
		examsAssessmentsBeansList = examsAssessmentsDAO.getExamAssessmentsForActiveBatches(CURRENT_MBAWX_ACAD_MONTH,CURRENT_MBAWX_ACAD_YEAR,CURRENT_PDDM_ACAD_MONTH,CURRENT_PDDM_ACAD_YEAR);
	
		//put model attribute and value in map
		map.put("exam_assessments",examsAssessmentsBeansList);
		
		//return logical view name
		return "examsAssessmentsDetails";
	}//examAssessmentDetails
	
	
	@RequestMapping(value = "/admin/extendExamAssessmentDateTime",  method = RequestMethod.POST , consumes="application/json", produces="application/json")
	public ResponseEntity<Map<String, String>> extendExamsAssessmentEndTime(@RequestBody ExamsAssessmentsBean examAssmtBean){
		Map<String, String> response =null;
		HttpHeaders headers = null;
		String result=null;
		
		response = new  HashMap<String, String>();
		headers = new HttpHeaders();
		//Add header content type as json
		headers.add("Content-Type", "application/json");
		try {
			//Validate extendedEndTime it is empty
			if("".equalsIgnoreCase(examAssmtBean.getExtendExamEndTime()) || examAssmtBean.getExtendExamEndTime().length() == 0) {
				response.put("Status", "Fail");
				response.put("message", "Please select Exam End Time.");
				return new ResponseEntity<Map<String, String>>(response, headers, HttpStatus.OK);
			}
			
			//Use DAO and call updateExamAssessmentDateTime() by passing assessment_id, extendedTime and end date in examAssmtBean 
			result=examsAssessmentsDAO.updateExamAssessmentEndTime(examAssmtBean);
			
			//If result is success then put Status as Success in response object
			if("success".equalsIgnoreCase(result))
				response.put("Status", "Success");
			
		}catch (Exception e) {
			
			response.put("Status", "Fail");
			response.put("message", "Error while updating details. Please retry");
		}
	
		return new ResponseEntity<Map<String, String>>(response, headers, HttpStatus.OK);
	}//extendExamsAssessmentEndTime
	
	@RequestMapping(value="/admin/scheduleCreationForm" , method=RequestMethod.GET)
	public ModelAndView scheduleCreationForm(HttpServletRequest request)
	{
		ModelAndView mv = new ModelAndView("scheduleCreation");
		mv.addObject("downloadExcel", false);
		mv.addObject("acadYear", ACAD_YEAR_LIST);
		mv.addObject("acadMonth", ACAD_MONTH_LIST);
		/*
		 * ArrayList<ExamsAssessmentsBean> examsAssessmentsBeansList =
		 * examsAssessmentsDAO.getExamAssessments();
		 * mv.addObject("exam_assessments",examsAssessmentsBeansList);
		 */
		return mv;
	}
	
	@RequestMapping(value="/admin/scheduleCreation" , method=RequestMethod.POST)
	public ModelAndView scheduleCreation(HttpServletRequest request,ScheduleCreationBean scheduleBean)
	{
		ModelAndView mv = new ModelAndView("scheduleCreation");
		mv.addObject("downloadExcel", false);
		mv.addObject("acadYear", ACAD_YEAR_LIST);
		mv.addObject("acadMonth", ACAD_MONTH_LIST);
		try
		{
			logger.info("Schedule Details: "+scheduleBean.toString());
			String userId = (String)request.getSession().getAttribute("userId");
			String timeBoundId = examsAssessmentsDAO.getTimeBoundId(scheduleBean.getSubject_id(), scheduleBean.getBatch_id(), scheduleBean.getStartDate());
			if(timeBoundId != null) 
			{	
				ExamsAssessmentsBean examsAssessmentsBean = new ExamsAssessmentsBean();
				examsAssessmentsBean.setAssessments_id(scheduleBean.getAssessments_id());
				examsAssessmentsBean.setTimebound_id(scheduleBean.getTimeboundId());
				int mappingExists = examsAssessmentsDAO.checkIfAssessmentTimeBoundMappingExists(examsAssessmentsBean);
				if(mappingExists > 0) {
					request.setAttribute("error","true");
					request.setAttribute("errorMessage","Already Exists in Portal.");
				}else if(mappingExists < 0) {
					request.setAttribute("error","true");
					request.setAttribute("errorMessage","Unable to find if mapping exists");
				}else if(mappingExists == 0) {
				MettlAssessments assessments= mettlAssessmentsFactory.getProductType(scheduleBean.getProgramType());
				String result=assessments.createSchedule(request,scheduleBean,userId);
				if("success".equalsIgnoreCase(result)) 
				{
					request.setAttribute("success","true");
					request.setAttribute("successMessage","Successfully schedule created and students registered in portal");
				}
				else
				{
					request.setAttribute("error","true");
					request.setAttribute("errorMessage",result);
					if(result.contains("Download Failed Registrations Excel"))
					{
						mv.addObject("downloadExcel", true);
					}
				}
				}
			}
			else
			{
				request.setAttribute("error","true");
				request.setAttribute("errorMessage","Null Timebound Id found try again");
			}
		}
		catch(Exception e)
		{
			logger.error("Error:"+e.getMessage());
			request.setAttribute("error","true");
			request.setAttribute("errorMessage",e.getMessage());
		}
		/*
		 * ArrayList<ExamsAssessmentsBean> examsAssessmentsBeansList =
		 * examsAssessmentsDAO.getExamAssessments();
		 * mv.addObject("exam_assessments",examsAssessmentsBeansList);
		 */
		return mv;
	}
	
	@RequestMapping(value="/admin/downloadFailedRegistrationsList",method = RequestMethod.GET)
	public ModelAndView downloadFailedRegistrationsList(HttpServletRequest request)
	{
		ModelAndView mv = new ModelAndView("FailedRegistrationsExcelView");
		ArrayList<MettlRegisterCandidateBean> userList = (ArrayList<MettlRegisterCandidateBean>)request.getSession().getAttribute("failedRegistrations");
		ExamsAssessmentsBean examBean = (ExamsAssessmentsBean)request.getSession().getAttribute("examDetails");
		String subjectName = (String)request.getSession().getAttribute("subjectName");
		String endTime = (String)request.getSession().getAttribute("endTime");
		mv.addObject("userList",userList);
		mv.addObject("examBean",examBean);
		mv.addObject("subjectName",subjectName);
		mv.addObject("endTime",endTime);
		return mv;
	}
	
	@RequestMapping(value="/admin/uploadRegistrationFailedMettlForm",method = RequestMethod.GET)
	public ModelAndView uploadRegistrationFailedMettlForm(HttpServletRequest request)
	{
		ModelAndView mv = new ModelAndView("uploadRegistrationFailed");
		String userId = (String)request.getSession().getAttribute("userId");
		mv.addObject("userId", userId);
		return mv;
	}
	
	@RequestMapping(value="/admin/downloadSuccessRegistrationsList",method = RequestMethod.GET)
	public ModelAndView downloadSuccessRegistrationsList(HttpServletRequest request)
	{
		ModelAndView mv = new ModelAndView("SuccessRegistrationsExcelView");
		ArrayList<MettlRegisterCandidateBean> userList = (ArrayList<MettlRegisterCandidateBean>)request.getSession().getAttribute("successfullUserList");
		mv.addObject("userList",userList);
		return mv;
	}
	
}
