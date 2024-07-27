package com.nmims.controllers;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import com.google.gson.Gson;
import com.nmims.beans.CSResponse;
import com.nmims.beans.FeatureTypes;
import com.nmims.beans.PersonCareerservicesBean;
import com.nmims.beans.SessionAttendance;
import com.nmims.beans.SessionDayTimeBean;
import com.nmims.beans.SessionFeedbackAnswers;
import com.nmims.beans.SessionFeedbackQuestionsModelBean;
import com.nmims.beans.StudentCareerservicesBean;
import com.nmims.beans.StudentEntitlement;
import com.nmims.beans.ZoomGetJoinURLModelBean;
import com.nmims.daos.EntitlementActivationDAO;
import com.nmims.daos.SessionAttendanceDao;
import com.nmims.daos.SessionFeedbackDAO;
import com.nmims.daos.SessionsDAO;
import com.nmims.helpers.ZoomManager;

@Controller
public class AttendanceFeedbackController extends CSPortalBaseController {

	@Autowired(required=false)
	ApplicationContext act;
	
	@Autowired
	private ZoomManager zoomManger;

	@Autowired
	EntitlementActivationDAO entitlementActivationDAO;

	@Autowired
	SessionFeedbackDAO sessionFeedbackDAO;
	
	@Autowired
	SessionsDAO sessionsDAO;

	Gson gson = new Gson();

	private static final Logger logger = LoggerFactory.getLogger(AttendanceFeedbackController.class);
 
	public boolean checkSession(HttpServletRequest request, HttpServletResponse respnse){
		String userId = (String)request.getSession().getAttribute("userId");

		if(userId != null){
			return true;
		}else{
			request.setAttribute("error", "true");
			request.setAttribute("errorMessage", "Session Expired! Please login again.");
			return false;
		}
	}
	
	//web
	@RequestMapping(value = "/attendScheduledSession", method = RequestMethod.GET)
	public ModelAndView attendScheduledSession(HttpServletRequest request, HttpServletResponse response) throws Exception{
		
		String joinFor = (String) request.getParameter("joinFor");
		if(joinFor != null && joinFor.equals("Admin")) {
			return attendSessionForAdmin(request, response);
		}
		
		if(!checkLogin(request)) {
			return new ModelAndView("redirect:../studentportal/home");
		}
		
		SessionAttendanceDao attendanceDao = (SessionAttendanceDao)act.getBean("SessionAttendanceDAO");
		SessionAttendance attendance =  new SessionAttendance();
		StudentCareerservicesBean student = new StudentCareerservicesBean();

		student = (StudentCareerservicesBean)request.getSession().getAttribute("student_careerservices");
		
		String id = request.getParameter("id");

		attendance.setDevice("WebApp");
		
		SessionDayTimeBean session = attendanceDao.findScheduledSessionById(id);
		
		if(session != null) {
			session.setDevice(attendance.getDevice());
		}
		
		ZoomGetJoinURLModelBean zoomGetJoinURLModelBean = getJoinSessionUrl(student, session);
		
		String url = zoomGetJoinURLModelBean.getJoinURL();
		
		if(zoomGetJoinURLModelBean.getStatus().equals("success")) {
			ModelAndView modelnView = new ModelAndView("sessionRedirect"); 
			modelnView.addObject("tempurl", url);
			return modelnView;
		}else {
			ModelAndView modelnView = new ModelAndView("redirect:viewScheduledSession?id=" + id + "&errorMessage=" + zoomGetJoinURLModelBean.getMessage()); 
			modelnView.addObject("tempurl", url);
			return modelnView;
		}
	}

	private ModelAndView attendSessionForAdmin(HttpServletRequest request, HttpServletResponse response){

		StudentCareerservicesBean student = new StudentCareerservicesBean();
		PersonCareerservicesBean person = (PersonCareerservicesBean) request.getSession().getAttribute("user_careerservices");
		
		student.setEmailId(person.getEmail());

		if ("".equalsIgnoreCase(student.getEmailId()) || student.getEmailId()==null) {
			student.setEmailId("notavailable@mail.com");
		}
		
		student.setFirstName(person.getFirstName());
		student.setLastName(person.getLastName());

		String id = request.getParameter("id");

		SessionAttendanceDao attendanceDao = (SessionAttendanceDao)act.getBean("SessionAttendanceDAO");
		SessionDayTimeBean session = attendanceDao.findScheduledSessionById(id);
		
		ZoomGetJoinURLModelBean zoomGetJoinURLModelBean = getJoinSessionUrlForAdmin(student, session);
		
		String url = zoomGetJoinURLModelBean.getJoinURL();
		
		ModelAndView modelnView = new ModelAndView("sessionRedirect"); 
		modelnView.addObject("tempurl", url);
		return modelnView;
	}

	//API
	@RequestMapping(value = "/attendScheduledSession", method = RequestMethod.POST, consumes = "application/json",produces = "application/json")
	public ResponseEntity<String> attendScheduledSession(HttpServletRequest request, HttpServletResponse response, @RequestBody Map<String, String> requestParams) throws Exception{
		HttpHeaders headers = new HttpHeaders();
		headers.add("Content-Type", "application/json");

		CSResponse csResponse = new CSResponse();
		
		SessionAttendanceDao attendanceDao = (SessionAttendanceDao)act.getBean("SessionAttendanceDAO");
		String forMobile = requestParams.get("forMobile");
		StudentCareerservicesBean student = null;
		SessionAttendance attendance = new SessionAttendance();
		
		if("true".equalsIgnoreCase(forMobile)) {
			//Called from Mobile App, Student details not available in session
			String sapid = requestParams.get("sapid");
			String email = requestParams.get("email");
			String firstName = requestParams.get("firstName");
			String lastName = requestParams.get("lastName");
			String mobile = requestParams.get("mobile");
			
			student = new StudentCareerservicesBean();
			student.setSapid(sapid);
			student.setEmailId(email);
			student.setMobile(mobile);
			student.setFirstName(firstName);
			student.setLastName(lastName);
			attendance.setDevice("MobileApp");

		}
		
		String id = requestParams.get("id");
		
		/*String joinForParameter = requestParams.get("joinFor"); */
		SessionDayTimeBean session = attendanceDao.findScheduledSessionById(id);

		session.setDevice(attendance.getDevice());
//		attendance = attendanceDao.checkSessionAttendance(student.getSapid(), id);
		ZoomGetJoinURLModelBean zoomGetJoinURLModelBean = getJoinSessionUrl(student, session);
		
		String url = zoomGetJoinURLModelBean.getJoinURL();
		
		if(url == null || url.equals("")) {
			csResponse.setStatusFailure();
			csResponse.setMessage(zoomGetJoinURLModelBean.getMessage());
			return new ResponseEntity<String>(gson.toJson(csResponse),headers, HttpStatus.OK);
		}else {
			csResponse.setStatusSuccess();
			csResponse.setMessage(zoomGetJoinURLModelBean.getMessage());
			csResponse.setResponse(url);
			return new ResponseEntity<String>(gson.toJson(csResponse),headers, HttpStatus.OK);
		}
	}


	@RequestMapping(value = "/addAttendanceForPreviousSession", method = RequestMethod.GET)
	public ModelAndView addAttendanceForPreviousSession(HttpServletRequest request, HttpServletResponse response) throws Exception{
		if(!checkLogin(request)) {
			return new ModelAndView("redirect:../studentportal/home");
		}
		
		SessionAttendanceDao attendanceDao = (SessionAttendanceDao)act.getBean("SessionAttendanceDAO");
		SessionAttendance attendance =  new SessionAttendance();
		StudentCareerservicesBean student = new StudentCareerservicesBean();

		student = (StudentCareerservicesBean)request.getSession().getAttribute("student_careerservices");
		
		String id = request.getParameter("id");
		
		attendance.setDevice("WebApp");
		
		SessionDayTimeBean session = attendanceDao.findScheduledSessionById(id);
		if(session != null) {
			session.setDevice(attendance.getDevice());
		}
		ModelAndView modelnView;
		if(!recordAttendance(student, session)) {
			modelnView = new ModelAndView("redirect:viewScheduledSession?id=" + id); 
			modelnView.addObject("errorMessage","Can't Activate session!");
		}else {
			modelnView = new ModelAndView("redirect:viewScheduledSession?id=" + id); 
			modelnView.addObject("successMessage", "Session attendance added!");
		}
		
		return modelnView;
	}
	

	@RequestMapping(value = "/addAttendanceForPreviousSession", method = RequestMethod.POST, consumes = "application/json",produces = "application/json")
	public ResponseEntity<String> addAttendanceForPreviousSession(HttpServletRequest request, HttpServletResponse response, @RequestBody Map<String, String> requestParams) throws Exception{
		HttpHeaders headers = new HttpHeaders();
		headers.add("Content-Type", "application/json");

		SessionAttendanceDao attendanceDao = (SessionAttendanceDao)act.getBean("SessionAttendanceDAO");
		String forMobile = requestParams.get("forMobile");
		StudentCareerservicesBean student = null;
		SessionAttendance attendance = new SessionAttendance();
		
		if("true".equalsIgnoreCase(forMobile)) {
			//Called from Mobile App, Student details not available in session
			String sapid = requestParams.get("sapid");
			String email = requestParams.get("email");
			String firstName = requestParams.get("firstName");
			String lastName = requestParams.get("lastName");
			String mobile = requestParams.get("mobile");
			
			student = new StudentCareerservicesBean();
			student.setSapid(sapid);
			student.setEmailId(email);
			student.setMobile(mobile);
			student.setFirstName(firstName);
			student.setLastName(lastName);
			attendance.setDevice("MobileApp");

		}
		
		String id = requestParams.get("id");
		
		SessionDayTimeBean session = attendanceDao.findScheduledSessionById(id);

		session.setDevice(attendance.getDevice());


		CSResponse csResponse = new CSResponse();
		
		if(!recordAttendance(student, session)) {
			csResponse.setStatusFailure();
			csResponse.setMessage("Can't Activate session for user");
		}else {
			csResponse.setStatusSuccess();
			csResponse.setMessage("Session attendance added for user");
		}
		return ResponseEntity.ok(gson.toJson(csResponse));
	}
	
	//adds attendance for this sapid, also adds to student viewed webinars, etc
	private boolean recordAttendance(StudentCareerservicesBean student,SessionDayTimeBean session) {

		String sapid = student.getSapid();
		String sessionId = session.getId();
		
		if(session.getIsCancelled().equalsIgnoreCase("Y")) {
			return false;
		}
		//get the latest entitlement 
		//check if student has attended this session
		
		SessionAttendanceDao attendanceDao = (SessionAttendanceDao)act.getBean("SessionAttendanceDAO");
		StudentEntitlement entitlement = entitlementActivationDAO.getApplicableEntitlementForPurchase(sapid, FeatureTypes.CAREER_FORUM);
	
		//if the entitlement couldn't be found
		if(entitlement == null) {
			return false;
		}
		
		int activationsPossible = entitlementActivationDAO.activationsCurrentlyPossible(entitlement);
		boolean attended = entitlementActivationDAO.checkIfStudentActivatedSessionWithThisId(sapid, sessionId);

		//if no activations are possible and the user hasn't viewed the session before, return
		if(activationsPossible <= 0 && !attended) {
			return false;
		}
		
		//record attendance if not attended
		if(!attended) {
			int attendanceFeedbackId = attendanceDao.recordAttendance(session, student.getSapid(), entitlement.getPurchaseId());
			if(attendanceFeedbackId == 0) {
				return false;
			}
			entitlementActivationDAO.consumeActivation(entitlement, session.getId(), attendanceFeedbackId);
		}
		
		
		return true;
	}
	
	private ZoomGetJoinURLModelBean getJoinSessionUrlForAdmin(StudentCareerservicesBean student, SessionDayTimeBean session) {
		ZoomGetJoinURLModelBean zoomGetJoinURLModelBean = new ZoomGetJoinURLModelBean();
		
		try {
			zoomGetJoinURLModelBean = zoomManger.registrantsForWebinar(session, student, session.getMeetingKey());
		} catch (IOException e) {
			zoomGetJoinURLModelBean.setMessage("Couldn`t join session. Exception occured");
			zoomGetJoinURLModelBean.setStatus("fail");
			logger.info("in getJoinSessionUrlForAdmin got exception : "+e.getMessage());
		}
		return zoomGetJoinURLModelBean;
	}
	
	private ZoomGetJoinURLModelBean getJoinSessionUrl(StudentCareerservicesBean student,SessionDayTimeBean session) {
		ZoomGetJoinURLModelBean zoomGetJoinURLModelBean = new ZoomGetJoinURLModelBean();

		if(student != null){
			
			if ("".equalsIgnoreCase(student.getLastName()) || student.getLastName()==null) {
				student.setLastName(" ");
			}
			
			if ("".equalsIgnoreCase(student.getEmailId()) || student.getEmailId()==null) {
				student.setEmailId("notavailable@mail.com");
			}
		}
		
		String tempurl = "";
		
		try {
			zoomGetJoinURLModelBean = zoomManger.registrantsForWebinar(session, student, session.getMeetingKey());
		} catch (IOException e) {
			zoomGetJoinURLModelBean.setMessage("Couldn`t join session. Exception occured");
			zoomGetJoinURLModelBean.setStatus("fail");
			logger.info("in getJoinSessionUrl got exception : "+e.getMessage());
			return zoomGetJoinURLModelBean;
		}
		if(!recordAttendance(student, session)) {
			zoomGetJoinURLModelBean.setJoinURL("");
			zoomGetJoinURLModelBean.setStatus("fail");
			zoomGetJoinURLModelBean.setMessage("Couldn`t record student attendance");
			return zoomGetJoinURLModelBean;
		}
		return zoomGetJoinURLModelBean;
	}
	
	
//	
	//web
		@RequestMapping(value = "/m/submitSessionFeedback", method = RequestMethod.GET)
		public String submitSessionFeedback(HttpServletRequest request, Model model, HttpServletResponse response, @RequestParam("sessionId") String sessionId) throws Exception{
			if(!checkLogin(request)) {
				return "redirect:../studentportal/home";
			}
			String sapid = (String)request.getSession().getAttribute("userId");
			model.addAttribute("sessionId", sessionId);
			boolean attended = entitlementActivationDAO.checkIfStudentActivatedSessionWithThisId(sapid, sessionId);
			if(attended) {
				return "portal/career_forum/feedback";
			}else {
				return "redirect:viewScheduledSession?id=" + sessionId;
			}
		}
		
		@RequestMapping(value = "/submitSessionFeedback", method = RequestMethod.POST, consumes = "application/json",produces = "application/json")
		public ResponseEntity<String> submitSessionFeedback(@RequestBody SessionFeedbackAnswers feedback){
			CSResponse csResponse = new CSResponse();
			if(feedback.getSapid() == null) {
				csResponse.setMessage("no sapid");
				csResponse.setStatusFailure();
				return ResponseEntity.ok(gson.toJson(csResponse));
			}
			if(feedback.getSessionId() == null) {
				csResponse.setMessage("no sessionId");
				csResponse.setStatusFailure();
				return ResponseEntity.ok(gson.toJson(csResponse));
			}
			String sapid = feedback.getSapid();
			String sessionId = feedback.getSessionId();
			boolean attended = entitlementActivationDAO.checkIfStudentActivatedSessionWithThisId(sapid, sessionId);
			if(!attended) {
				csResponse.setMessage("student hasn't activated this session yet!");
				csResponse.setStatusFailure();
				return ResponseEntity.ok(gson.toJson(csResponse));
			}

			if(feedback.getSuccessfullyAttended()) {
				if(feedback.getAnswers() == null) {
					csResponse.setMessage("no answers");
					csResponse.setStatusFailure();
					return ResponseEntity.ok(gson.toJson(csResponse));
				}
				if(sessionFeedbackDAO.addSessionFeedback(feedback)) {
					csResponse.setMessage("Feedback recorded successfully.");
					csResponse.setStatusSuccess();
				}else {
					csResponse.setMessage("Feedback recording failed.");
					csResponse.setStatusFailure();
				}
			}else {
				sessionFeedbackDAO.addSessionFeedbackForNotSuccessfullyViewed(feedback);
				csResponse.setMessage("Feedback recorded successfully.");
				csResponse.setStatusSuccess();
			}
			return ResponseEntity.ok(gson.toJson(csResponse));
		}

		//API
		@RequestMapping(value = "/m/getFeedbackQuestions", method = RequestMethod.POST, consumes = "application/json",produces = "application/json")
		public ResponseEntity<String> getFeedbackQuestions(HttpServletRequest request, HttpServletResponse response, @RequestParam(name="sessionId", required=false) String sessionId, @RequestParam(name="sapid", required=false) String sapid ) throws Exception{
			

			CSResponse csResponse = new CSResponse();
			if(sapid == null) {
				csResponse.setMessage("no sapid");
				csResponse.setStatusFailure();
				return ResponseEntity.ok(gson.toJson(csResponse));
			}
			if(sessionId == null) {
				csResponse.setMessage("no sessionId");
				csResponse.setStatusFailure();
				return ResponseEntity.ok(gson.toJson(csResponse));
			}

			SessionFeedbackQuestionsModelBean responseBean = new SessionFeedbackQuestionsModelBean();
			responseBean.setSessionId(sessionId);
			responseBean.setSapid(sapid);
			boolean attended = entitlementActivationDAO.checkIfStudentActivatedSessionWithThisId(sapid, sessionId);
			responseBean.setSessionAttended(attended);
			responseBean.setSessionDetails(sessionsDAO.findScheduledSessionById(sessionId));
			
			if(!attended) {
				csResponse.setStatusFailure();
				csResponse.setMessage("Student hasn't viewed a session with this id yet!");
				return ResponseEntity.ok(gson.toJson(csResponse));
			}
			boolean feedbackSubmitted = sessionFeedbackDAO.checkIfStudentSubmittedFeedback(sapid, sessionId);
			if(feedbackSubmitted) {
				csResponse.setStatusFailure();
				csResponse.setMessage("Feedback already submitted");
				return ResponseEntity.ok(gson.toJson(csResponse));
			}
			csResponse.setStatusSuccess();
			responseBean.setFeedbackQuestions(sessionFeedbackDAO.getFeedbackQuestionsList(sessionId));
			csResponse.setResponse(responseBean);
			return ResponseEntity.ok(gson.toJson(csResponse));
		}
		//API
		@RequestMapping(value = "/getPendingFeedback", method = RequestMethod.POST, consumes = "application/json",produces = "application/json")
		public ResponseEntity<String> getPendingFeedback(HttpServletRequest request, HttpServletResponse response, @RequestParam(name="sapid", required=false) String sapid ) throws Exception{
			CSResponse csResponse = new CSResponse();
			if(sapid == null) {
				csResponse.setMessage("no sapid");
				csResponse.setStatusFailure();
				return ResponseEntity.ok(gson.toJson(csResponse));
			}
			List<SessionFeedbackQuestionsModelBean> responseBean = new ArrayList<SessionFeedbackQuestionsModelBean>();
			List<String> sessionWithPendingFeedback = sessionFeedbackDAO.getListOfSessionsWithoutFeedback(sapid);
			for (String sessionId : sessionWithPendingFeedback) {
				
				responseBean.add(getFeedbackStatusForSession(sapid, sessionId));
			}
			csResponse.setStatusSuccess();
			csResponse.setResponse(responseBean);
			return ResponseEntity.ok(gson.toJson(csResponse));
		}
		
		//API
		@RequestMapping(value = "/getNumberOfFeedbackPending", method = RequestMethod.POST, consumes = "application/json",produces = "application/json")
		public ResponseEntity<String> getNumberOfFeedbackPending(HttpServletRequest request, HttpServletResponse response, @RequestParam(name="sapid", required=false) String sapid ) throws Exception{
			CSResponse csResponse = new CSResponse();
			if(sapid == null) {
				csResponse.setMessage("no sapid");
				csResponse.setStatusFailure();
				return ResponseEntity.ok(gson.toJson(csResponse));
			}
			int numberOfFeedbackRequired = sessionFeedbackDAO.numberOfFeedbackRequired(sapid);
			
			csResponse.setStatusSuccess();
			csResponse.setResponse(numberOfFeedbackRequired);
			return ResponseEntity.ok(gson.toJson(csResponse));
		}
		
		private SessionFeedbackQuestionsModelBean getFeedbackStatusForSession(String sapid, String sessionId) {
			SessionFeedbackQuestionsModelBean sessionFeedbackQuestionsModelBean = new SessionFeedbackQuestionsModelBean();
			sessionFeedbackQuestionsModelBean.setSessionId(sessionId);
			sessionFeedbackQuestionsModelBean.setSessionDetails(sessionsDAO.findScheduledSessionById(sessionId));
			sessionFeedbackQuestionsModelBean.setFeedbackQuestions(sessionFeedbackDAO.getFeedbackQuestionsList(sessionId));
			return sessionFeedbackQuestionsModelBean;
		}
}
