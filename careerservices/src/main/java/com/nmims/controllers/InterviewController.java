package com.nmims.controllers;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.codehaus.jettison.json.JSONArray;
import org.codehaus.jettison.json.JSONException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import com.google.gson.Gson;
import com.nmims.beans.InterviewBean;
import com.nmims.beans.InterviewFeedbackBean;
import com.nmims.beans.ResponseCareerservicesBean;
import com.nmims.beans.StudentCareerservicesBean;
import com.nmims.daos.InterviewDAO;
import com.nmims.daos.ProgressDetailsDAO;
import com.nmims.helpers.ExcelHelper;
import com.nmims.helpers.ZoomManager;

@Controller
public class InterviewController extends BaseController{
	
	@Autowired
	ProgressDetailsDAO progressDAO;
	
	@Autowired
	InterviewDAO interviewDAO;

	private static final Logger logger = LoggerFactory.getLogger(InterviewController.class);
 
	public boolean checkLogin(HttpServletRequest request) {

		String sapid = (String) request.getSession().getAttribute("userId");
		if(sapid != null) {
			if(request.getSession().getAttribute("student_careerservices") != null) {
				return true;
			}
		}
		return false;
	}
	
	public boolean checkCSAccess(HttpServletRequest request) {
		
		StudentCareerservicesBean student = (StudentCareerservicesBean) request.getSession().getAttribute("student_careerservices");
		if(student == null) {
			return false;
		}
		if(student.isPurchasedOtherPackages()) {
			return true;
		}
		return false;
	}
	
	@RequestMapping(value = "/uploadInterviewDetailsForm", method = {RequestMethod.GET, RequestMethod.POST})
	public String uploadInterviewDetailsForm(HttpServletRequest request, HttpServletResponse respnse, Model m) {

		InterviewBean interviewBean = new InterviewBean();
		m.addAttribute("interviewBean", interviewBean);
		return "uploadInterviewDetails";
	}


	@RequestMapping(value = "/uploadInterviewDetails", method = {RequestMethod.GET, RequestMethod.POST})
	public ModelAndView uploadInterviewDetails(InterviewBean interviewBean, BindingResult result,HttpServletRequest request){
		ModelAndView modelAndView = new ModelAndView("uploadInterviewDetails");
		
		try{
			String userId = (String)request.getSession().getAttribute("userId");
			ExcelHelper excelHelper = new ExcelHelper();

			List<InterviewBean> errorBeanList = new ArrayList<InterviewBean>();
			List<InterviewBean> facultyDatesList = new ArrayList<InterviewBean>();
			List<InterviewBean> errorList = new ArrayList<InterviewBean>();
			List<InterviewBean> successList = new ArrayList<InterviewBean>();
			
			excelHelper.readInterviewAvailabilityExcel(interviewBean, interviewDAO.getAllFaculties(), userId, facultyDatesList, errorList);
			if(errorBeanList.size() > 0){
				request.setAttribute("errorBeanList", errorBeanList);
				return modelAndView;
			}
			
			for(InterviewBean bean: facultyDatesList) {
				boolean facultyFree = interviewDAO.checkFacultyFree(bean);
				boolean clashing = interviewDAO.checkFreeSlots(bean);
				if(facultyFree) {
					if(clashing) {
						interviewDAO.updateInterviewDates(bean);
						successList.add(bean);
					}else {
						bean.setErrorMessage("There can not be more than two slots a time. ");
						bean.setErrorRecord(true);
						errorList.add(bean);
					}
				}else {
					bean.setErrorMessage("One faculty can only have one slot at a time. ");
					bean.setErrorRecord(true);
					errorList.add(bean);
				}

			}
						
			modelAndView.addObject("successList", successList);
			
			if(errorList.size() > 0) {
				request.setAttribute("error", "true");
				request.setAttribute("errorMessage", "Error in inserting " + errorList.size() + " rows.");
				modelAndView.addObject("errorList", errorList);
			}
			
			if(successList.size() > 0){
				request.setAttribute("success", "true");
				request.setAttribute("successMessage", successList.size()+" rows were successfully inserted");
			}
			
		}catch(Exception e){
			logger.info("in InterviewController class got exception : "+e.getMessage());
			request.setAttribute("error", "true");
			request.setAttribute("errorMessage", "Error in inserting rows.");
		}
		modelAndView.addObject("allInterview",interviewDAO.getAllInterview());
		return modelAndView;
	}

	public ArrayList<InterviewBean> setErrorRecord(InterviewBean bean){
		ArrayList<InterviewBean> recordClash = new ArrayList<InterviewBean>();
		InterviewBean record = new InterviewBean();
		if(StringUtils.isBlank(bean.getErrorMessage())) {
			record.setErrorRecord(true);
			record.setErrorMessage("Error: Slot Clash - There can not be more than 2 interviews at a given time.");
		}else{
			record.setErrorRecord(true);
			record.setErrorMessage(bean.getErrorMessage());
		}
		recordClash.add(record);
		return recordClash;
	}
	
	@RequestMapping(value = "/practiceInterview", method = RequestMethod.GET)
	public String practiceInterview(HttpServletRequest request, Model model) {
		if(!checkLogin(request)) {
			return "redirect:../studentportal/home";
		}
		if(!checkCSAccess(request)) {
			return "redirect:showAllProducts"; 
		}
		
		String sapid = (String)request.getSession().getAttribute("userId");
		InterviewBean interviewBean = new InterviewBean();
		
		interviewDAO.setTerminated();
		
		ArrayList<InterviewBean> interviews = interviewDAO.getAllInterview();
		ArrayList<InterviewBean> studentInterview = interviewDAO.getStudentInterview(sapid);
		
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		Calendar calendar = Calendar.getInstance();
		Date date = new Date();
		
		String monthYear = new SimpleDateFormat("MMM YYYY").format( calendar.getTime() );
		String today = sdf.format( date );

		interviewBean.setSapid(sapid);
		interviewBean.setPackageId(interviewDAO.getPackageId(sapid));
		
		JSONArray jsonFormatedInterviewList = new JSONArray();
		String jsonFormatedInterviews = new Gson().toJson(interviews);
		
		try {
			jsonFormatedInterviewList = new JSONArray( jsonFormatedInterviews );
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			logger.info("in InterviewController class got exception : "+e.getMessage());
		}
		
		model.addAttribute("studentDetails", interviewDAO.getStudentDetails(interviewBean));		
		model.addAttribute("interviewList", jsonFormatedInterviewList);
		model.addAttribute("studentInterview",studentInterview);
		model.addAttribute("monthYear",monthYear);
		model.addAttribute("today",today);
		
		return "portal/interview/practiceInterview";
	}

	@RequestMapping(value = "/m/schedulePracticeInterviewSession", method = RequestMethod.POST, 
			produces = "application/json", consumes = "application/json")
	public ResponseEntity<InterviewBean> schedulePracticeInterviewSession(HttpServletRequest request,  @RequestBody InterviewBean bean) {
		
		HttpHeaders headers = new HttpHeaders();
		headers.add("Content-Type", "application/json");
		
		InterviewBean interviewDetails = new InterviewBean();
		
		boolean isInterviewSchedulePossible = false;
		boolean isInterviewApplicable = false;
		String interviewScheduleDate = "";
		
		try {
			isInterviewSchedulePossible = interviewDAO.checkIfInterviewSchedulePossible(bean);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			logger.info("in InterviewController class got exception : "+e.getMessage());
		}
		
		try {
			interviewScheduleDate = interviewDAO.getScheduledInterviewDate( bean.getSapid() );
			isInterviewApplicable = interviewDAO.checkIfSecondInterviewApplicable( bean, interviewScheduleDate );
		} catch (Exception e) {
			// TODO Auto-generated catch block
			logger.info("in InterviewController class got exception : "+e.getMessage());
			isInterviewApplicable = true;
		}

		if( isInterviewSchedulePossible ) {
			
			if( isInterviewApplicable ) {
				
				try {
					
					updateInterviewDetails( bean );
					
					interviewDetails = interviewDAO.getInterviewDetails( bean.getInterviewId() );
					
					bean.setSuccessMessage("Your interview for date "+ interviewDetails.getDate() + " at "+ interviewDetails.getStartTime() +
							" has been successfully scheduled.");

					bean.setStatus("success");
					bean.setErrorRecord(false);
					
				} catch (Exception e) {
					// TODO Auto-generated catch block
					logger.info("in InterviewController class got exception : "+e.getMessage());
					bean.setStatus("failer");
					bean.setErrorRecord(true);
					bean.setErrorMessage("An error occured while scheduling the interview : Error : <br>"+e.getMessage());
				}
				
			}else {

				bean.setStatus("failer");
				bean.setErrorRecord(true);
				bean.setErrorMessage( "Please note that there has be to a gap of <b>15 days</b> in between of two interviews. "
						+ "Please schedule another interview that is <b>post 15 days</b> of your first interview. " );
			}
				
		}else {
			
			bean.setStatus("failer");
			bean.setErrorRecord(true);
			bean.setErrorMessage( "Please note that you have already consumed both your interview slots." );
			
		}

		return new ResponseEntity<>(bean, headers, HttpStatus.OK);
		
	}

	private void updateInterviewDetails( InterviewBean bean ) throws Exception{

		ZoomManager zoom = new ZoomManager();
		InterviewBean interviewDetails = new InterviewBean();
		
		String packageId = interviewDAO.getPackageId( bean.getSapid() );
		String featureId = interviewDAO.getFeatureId("Practice Interviews");
		String entitlementId = interviewDAO.getEntitlementId(packageId, featureId);
		String facultyId = interviewDAO.getFacultyId( bean.getInterviewId() );
		
		interviewDetails = interviewDAO.getInterviewDetails( bean.getInterviewId() );
		
		bean.setInterviewId( bean.getInterviewId() );
		bean.setDate( interviewDetails.getDate() );
		bean.setStartTime( interviewDetails.getStartTime() );
		bean.setEndTime( interviewDetails.getEndTime() );
		bean.setFacultyId(facultyId);
		bean.setSapid( bean.getSapid() );
		bean.setFeatureId(featureId);
		bean.setPackageId(packageId);
		bean.setEntitlementId(entitlementId);
		bean.setActivationsLeft(interviewDAO.getActivationsLeft( bean.getSapid() ,entitlementId));
		/*
		 * update host ID after testing complete
		 * */
		bean.setHostId("harsh.kumar.EXT@nmims.edu");
		bean.setHostPassword("");
		bean.setCreatedBy( bean.getSapid() );
		bean.setLastModifiedBy( bean.getSapid() );
		
		try {
			
			zoom.scheduleInterview(bean);
			bean.setBookingStatus("B");
			
		} catch (IOException e) {
			logger.info("in InterviewController class got exception : "+e.getMessage());
			bean.setBookingStatus("F");
		}

		interviewDAO.scheduleInterview(bean);
		
	}
	
	@RequestMapping(value = "/m/studentInterview", method = RequestMethod.POST, consumes = "application/json", produces = "application/json")
	public ResponseEntity<ResponseCareerservicesBean> m_studentInterview(HttpServletRequest request, @RequestBody InterviewBean bean) throws Exception {
		
		HttpHeaders headers = new HttpHeaders();
		headers.add("Content-Type", "application/json");
		ResponseCareerservicesBean responseBean = new ResponseCareerservicesBean();
		bean.setPackageId(interviewDAO.getPackageId(bean.getSapid()));
		
		responseBean.setStudentInterview(interviewDAO.getStudentInterview(bean.getSapid()));
		responseBean.setStudentDetails(interviewDAO.getStudentDetails(bean));

		return new ResponseEntity<>(responseBean ,headers, HttpStatus.OK);
	}

	@RequestMapping(value = "/m/getAllInterviews", method = RequestMethod.POST, consumes = "application/json", produces = "application/json")
	public ResponseEntity<ArrayList<InterviewBean>> m_interviews(HttpServletRequest request) throws Exception {
		
		HttpHeaders headers = new HttpHeaders();
		headers.add("Content-Type", "application/json");

		interviewDAO.setTerminated();
		ArrayList<InterviewBean> interviews = interviewDAO.getAllInterview();

		return new ResponseEntity<>(interviews, headers, HttpStatus.OK);
	}
	
	@RequestMapping(value = "/interviewFeedbackDashboard", method = RequestMethod.GET)
	public ModelAndView feedback( HttpServletRequest request, @RequestParam String facultyId ){
		
		ModelAndView modelAndView = new ModelAndView("feedbackDashboard");
		
		ArrayList<InterviewBean> pendingFeedbacks = interviewDAO.getPendingFeedback( facultyId );
		ArrayList<InterviewBean> feedbacks = interviewDAO.getFeedback( facultyId );
		
		modelAndView.addObject("pendingFeedbacks",pendingFeedbacks);
		modelAndView.addObject("feedbacks",feedbacks);
		modelAndView.addObject("type", "Practice Interview");
		
		return modelAndView;
	}
	
	
	
	@RequestMapping(value = "/interviewFeedbackForm", method = RequestMethod.GET)
	public ModelAndView interviewFeedbackForm( @RequestParam( required = false ) String interviewId , HttpServletRequest request ) {
		
		ModelAndView modelAndView = new ModelAndView("portal/interview/interviewFeedback");
		InterviewFeedbackBean interviewFeeds = new InterviewFeedbackBean();
		LinkedHashMap<String,String> feedbackParam=new LinkedHashMap<String,String>();     
		
		feedbackParam.put("preparedness", "Preparedness");  
		feedbackParam.put("communication","Communication & Confidence"); 
		feedbackParam.put("listeningSkills","Listening Skills"); 
		feedbackParam.put("bodyLanguage","Body Language"); 
		feedbackParam.put("clarityOfThought","Clarity of Thought"); 
		feedbackParam.put("connect","Connect/Engage"); 
		feedbackParam.put("examples","Examples"); 
		
		ArrayList<String> rating = new ArrayList<String>(
				Arrays.asList("1","2","3","4","5"));
		
		if( !StringUtils.isBlank(interviewId) )
			try {
				interviewFeeds = interviewDAO.getFeedbackForInterview(interviewId);
			} catch (Exception e) {
				logger.info("in InterviewController class got exception : "+e.getMessage());
			}
		
		modelAndView.addObject("interviewFeeds",interviewFeeds);
		modelAndView.addObject("feedbackParam",feedbackParam);
		modelAndView.addObject("rating",rating);
		
		return modelAndView;
	}
	
	@RequestMapping(value = "/interviewFeedback", method = RequestMethod.POST)
	public ModelAndView interviewFeedback(HttpServletRequest request, InterviewFeedbackBean interviewFeeds) {
		
		String interviewId = request.getParameter("interviewId");
		String facultyId = request.getParameter("facultyId");
		
		try {
			interviewDAO.insertFeedback(interviewFeeds,interviewId);
			request.setAttribute("success","true");
			request.setAttribute("successMessage","Your feedback has been successfully submitted");
		}catch(Exception e) {
			request.setAttribute("interviewId",interviewId);
			request.setAttribute("error", "true");
			request.setAttribute("errorMessage", "There was an error while submitting your feedback");
			logger.info("in InterviewController class got exception : "+e.getMessage());
		}
		
		return feedback( request, facultyId );
	}
	@RequestMapping(value = "/viewInterviewDetails", method = RequestMethod.GET)
	public ModelAndView viewInterviewDetails(HttpServletRequest request, @RequestParam String interviewId ){
		
		ModelAndView modelAndView = new ModelAndView("viewInterviewDetails");
		InterviewFeedbackBean feedback = new InterviewFeedbackBean();
		InterviewBean interview = new InterviewBean();
		Date joinTime = null;

		String sapid = (String)request.getSession().getAttribute("userId");

		try {
			feedback = interviewDAO.getFeedbackForInterview(interviewId);
			feedback.setFeedbackProvided(true);
		}catch (Exception e) {
			feedback.setFeedbackProvided(false);
		}

		if(sapid.startsWith("77") || sapid.startsWith("79")) {

			try {
				interview = interviewDAO.getInterview(sapid, interviewId);
			} catch (Exception e) {
				// TODO Auto-generated catch block
			}	

			try {
				joinTime = getJoinTime(interview);
			} catch (ParseException e) {
				logger.info("in InterviewController class got exception : "+e.getMessage());
			}

			String packageId = interviewDAO.getPackageId(sapid);

			modelAndView.addObject("joinTime",joinTime);
			modelAndView.addObject("interview",interview);
			modelAndView.addObject("studentDetails", interviewDAO.getStudentDetails(sapid, packageId));
			modelAndView.addObject("feedback", feedback);
			modelAndView.addObject("role", "student");
			
		} else {

			try {
				interview = interviewDAO.getScheduledInterviewDetails(interviewId);
				interview.setFacultyName( interviewDAO.getFacultyName(interview.getFacultyId()) );
			} catch (Exception e) {
				// TODO Auto-generated catch block
			}
			
			modelAndView.addObject("feedback", feedback);
			modelAndView.addObject("interview",interview);
			modelAndView.addObject("role", "faculty");
			
		}
		
		return modelAndView;
	}
	
	@RequestMapping(value = "/m/updateInterviewAttendance", method = RequestMethod.POST, produces = "application/json", consumes = "application/json")
	public ResponseEntity<InterviewBean> updateInterviewAttendance(HttpServletRequest request,  @RequestBody InterviewBean bean) {
		
		try {
			interviewDAO.updateInterviewAttendance( bean.getSapid(), bean.getInterviewId());
			bean.setErrorRecord(false);
		}catch(Exception e) {
			bean.setErrorRecord(true);
			bean.setErrorMessage("An error occurred");
			logger.info("in InterviewController class got exception : "+e.getMessage());
		}
		return ResponseEntity.ok(bean);
	}
	
	private Date getJoinTime(InterviewBean bean) throws ParseException {
		
		SimpleDateFormat sdf =new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		
		Date date = sdf.parse(bean.getDate() + " " + bean.getStartTime());
		Calendar cal = Calendar.getInstance(); 
		cal.setTime(date); 
		cal.add(Calendar.HOUR, -1);
		Date joinTime = cal.getTime();
		
		return joinTime;
		
	}
	
	@RequestMapping(value = "/m/cancelInterview", method = RequestMethod.POST, 
			produces = "application/json", consumes = "application/json")
	public ResponseEntity<InterviewBean> cancelInterview(HttpServletRequest request, @RequestBody InterviewBean bean) {
		
		ZoomManager zoom = new ZoomManager();
		String packageId = "";
		String featureId = "";
		String entitlementId = "";

		//cancel the interview on zoom
		try {
			
			bean = interviewDAO.getScheduledInterviewDetails( bean.getInterviewId() );

			try {
				packageId = interviewDAO.getPackageId( bean.getSapid() );
			} catch (Exception e) {
				// TODO Auto-generated catch block
				logger.info("in InterviewController class got exception : "+e.getMessage());
			}
			try {
				featureId = interviewDAO.getFeatureId("Practice Interviews");
			} catch (Exception e) {
				// TODO Auto-generated catch block
				logger.info("in InterviewController class got exception : "+e.getMessage());
			}
			try {
				entitlementId = interviewDAO.getEntitlementId(packageId, featureId);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				logger.info("in InterviewController class got exception : "+e.getMessage());
			}
			
			zoom.cancelInterview(bean);

			bean.setIsCancelled("Y");
			bean.setReasonForCancellation("Interview cancellation.");
			bean.setEntitlementId( entitlementId );

			interviewDAO.updateInterviewCancellation(bean);

			bean.setStatus("success");
			
		} catch (IOException e) {
			
			logger.info("in InterviewController class got exception : "+e.getMessage());
			bean.setIsCancelled("N");
			bean.setStatus("failed");
			bean.setErrorMessage( e.getMessage() );
			
		}
		
		return ResponseEntity.ok(bean);
		
	}
	
	@RequestMapping(value = "/studentInterviewFeedback", method = RequestMethod.GET)
	public String studentFeedback(HttpServletRequest request, Model model, @RequestParam String interviewId) {
		
		if(!checkLogin(request)) {
			return "redirect:../studentportal/home";
		}
		if(!checkCSAccess(request)) {
			return "redirect:showAllProducts"; 
		}

		InterviewFeedbackBean bean = new InterviewFeedbackBean();
		try {
			bean = interviewDAO.getFeedbackForInterview(interviewId);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			logger.info("in InterviewController class got exception : "+e.getMessage());
		}
		
		String sapid = (String)request.getSession().getAttribute("userId");
		InterviewBean interviewBean = new InterviewBean();
		interviewBean.setSapid(sapid);
		interviewBean.setPackageId(interviewDAO.getPackageId(sapid));
		InterviewBean interview = new InterviewBean();
		
		try {
			interview = interviewDAO.getInterview(sapid, interviewId);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			logger.info("in InterviewController class got exception : "+e.getMessage());
		}
		
		model.addAttribute("studentDetails", interviewDAO.getStudentDetails(interviewBean));	
		model.addAttribute("feedback", bean);		
		model.addAttribute("interview", interview);	
		
		return "portal/interview/studentFeedback";
	}
	
	@RequestMapping(value = "/m/checkIfInterviewFeedbackProvided", method = RequestMethod.POST, produces = "application/json", consumes = "application/json")
	public ResponseEntity<InterviewBean> checkIfInterviewFeedbackProvided(HttpServletRequest request,  @RequestBody InterviewBean bean) {

		if( !interviewDAO.checkIfInterviewFeedbackProvided(bean) ) {
			bean.setStatus("failer");
			bean.setErrorRecord(true);
			bean.setErrorMessage("Feedback for the interview is yet to be evaluated.");
		}else {
			bean.setStatus("success");
			bean.setErrorRecord(false);
		}
		return ResponseEntity.ok(bean);
	}

}
