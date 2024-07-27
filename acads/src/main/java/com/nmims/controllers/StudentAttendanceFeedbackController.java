package com.nmims.controllers;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

import com.nmims.beans.PageAcads;
import com.nmims.beans.SessionAttendanceFeedbackAcads;
import com.nmims.beans.SessionDayTimeAcadsBean;
import com.nmims.beans.StudentAcadsBean;
import com.nmims.daos.AttendanceFeedbackDAO;
import com.nmims.daos.TimeTableDAO;
import com.nmims.helpers.ZoomManager;
import com.nmims.services.AttendanceFeedbackService;
import com.nmims.util.ContentUtil;

@Controller
@RequestMapping("/student")
@CrossOrigin(origins = "*", allowedHeaders = "*")
public class StudentAttendanceFeedbackController extends BaseController {
	
	@Autowired(required = false)
	ApplicationContext act;

	@Autowired
	private AttendanceFeedbackDAO attendanceFeedbackDAO;

	@Autowired
	private ZoomManager zoomManger;
	
	@Autowired
	private AttendanceFeedbackService attendanceService;
	
	@Value("#{'${ACAD_YEAR_LIST}'.split(',')}")
	private List<String> ACAD_YEAR_LIST;

	@Value("#{'${ACAD_MONTH_LIST}'.split(',')}")
	private List<String> ACAD_MONTH_LIST;
	
	private ArrayList<String> subjectList = null;
	private final int pageSize = 10;
	
	@ModelAttribute("subjectList")
	public ArrayList<String> getSubjectList() {
		if (this.subjectList == null) {
			TimeTableDAO dao = (TimeTableDAO) act.getBean("timeTableDAO");
			this.subjectList = dao.getAllSubjects();
		}
		return subjectList;
	}
	
	@RequestMapping(value = "/attendScheduledSession", method = { RequestMethod.GET, RequestMethod.POST })
	public ModelAndView attendScheduledSession(HttpServletRequest request, HttpServletResponse response) throws Exception {

		StudentAcadsBean student = null;
		TimeTableDAO dao = (TimeTableDAO) act.getBean("timeTableDAO");
		SessionAttendanceFeedbackAcads attendance = new SessionAttendanceFeedbackAcads();

		student = (StudentAcadsBean) request.getSession().getAttribute("student_acads");
		attendance.setDevice("WebApp");
		
		String id = request.getParameter("id");
		String joinFor = request.getParameter("joinFor");
		
		//adding student sapId in cookie
		Cookie studentCookie= new Cookie("st_userId", student.getSapid());
		studentCookie.setMaxAge(60*60*4);
		response.addCookie(studentCookie);
		
		//adding sessionId in cookie
		Cookie sessionCookie= new Cookie("st_sessionId", id);
		sessionCookie.setMaxAge(60*60*4);
		response.addCookie(sessionCookie);

		SessionDayTimeAcadsBean session = dao.findScheduledSessionById(id);
		session.setDevice(attendance.getDevice());

		String acadDateFormat = ContentUtil.prepareAcadDateFormat(session.getMonth(), session.getYear());
		
		//adding acadDateFormat in cookie
		Cookie acadDateFormatCookie= new Cookie("st_acadDateFormat", acadDateFormat);
		acadDateFormatCookie.setMaxAge(60*60*4);
		response.addCookie(acadDateFormatCookie);
		
		attendance = dao.checkSessionAttendance(student.getSapid(), id, acadDateFormat);

		if (attendance != null) {
			// Student has already joined session. Send him to same session again.
			return reAttendSessionWeb(attendance.getJoinurl());
			
			// Commented by Somesh on 18-09-2021, as now redirect via join-url in case of re-joining
			// return joinSessionUsingAlternateFacultyConfiguration(student, session, attendance.getMeetingKey(),
			//				attendance.getMeetingPwd(), attendance.getFacultyId(), "Y" , request);
		}

		if (!"ANY".equalsIgnoreCase(joinFor)) {
			
			// If Orientation Session Do Not check limit
			/*
			if (session.getSubject().equalsIgnoreCase("Orientation")) {
				return joinSession(student, session, session.getFacultyId(), "N", request);
			}
			*/
			
			// Student wants to join specific faculty session
			return joinPreferredFacultySessionV2(joinFor, student, session, request);
		}
		
		boolean originalSessionAvailable = false;
		boolean firstAlternateSessionAvailable = false;
		boolean secondAlternateSessionAvailable = false;
		boolean thirdAlternateSessionAvailable = false;
		
		if (!StringUtils.isBlank(session.getMeetingKey())) {
			int remainingSeatsForMain = dao.getAttendanceByMeetingKey(session.getMeetingKey());
			if (remainingSeatsForMain > 0) {
				originalSessionAvailable = true;
			}
		}
		
		if (!StringUtils.isBlank(session.getAltMeetingKey())) {
			int remainingSeatsForAlt1 = dao.getAttendanceByMeetingKey(session.getAltMeetingKey());
			if (remainingSeatsForAlt1 > 0) {
				firstAlternateSessionAvailable = true;
			}
		}
		if (!StringUtils.isBlank(session.getAltMeetingKey2())) {
			int remainingSeatsForAlt2 = dao.getAttendanceByMeetingKey(session.getAltMeetingKey2());
			if (remainingSeatsForAlt2 > 0) {
				secondAlternateSessionAvailable = true;
			}
		}
		if (!StringUtils.isBlank(session.getAltMeetingKey3())) {
			int remainingSeatsForAlt3 = dao.getAttendanceByMeetingKey(session.getAltMeetingKey3());
			if (remainingSeatsForAlt3 > 0) {
				thirdAlternateSessionAvailable = true;
			}
		}
		
		if (originalSessionAvailable) {
			return joinSession(student, session, session.getFacultyId(), "N", request);
		} else if (firstAlternateSessionAvailable) {
			return joinSessionUsingAlternateFacultyConfiguration(student, session, session.getAltMeetingKey(),
					session.getAltMeetingPwd(), session.getAltFacultyId(), "N", request); // For First threshold//
		} else if (secondAlternateSessionAvailable) {
			return joinSessionUsingAlternateFacultyConfiguration(student, session, session.getAltMeetingKey2(),
					session.getAltMeetingPwd2(), session.getAltFacultyId2(), "N", request); // For Second threshold//
		} else if (thirdAlternateSessionAvailable) {
			return joinSessionUsingAlternateFacultyConfiguration(student, session, session.getAltMeetingKey3(),
					session.getAltMeetingPwd3(), session.getAltFacultyId3(), "N", request); // For Third threshold//
		} else {
			return classFull(session);
		}
	}
	
	private ModelAndView joinSessionUsingAlternateFacultyConfiguration(StudentAcadsBean student, SessionDayTimeAcadsBean session,
			String meetingKey, String meetingPassWord, String facultyId, String isReAttended, HttpServletRequest request) {
		session.setMeetingKey(meetingKey);
		session.setMeetingPwd(meetingPassWord);

		return joinSession(student, session, facultyId, isReAttended, request);
	}

	private ModelAndView joinSession(StudentAcadsBean student, SessionDayTimeAcadsBean session, String facultyId, String isReAttended, HttpServletRequest request) {
		String tempurl = "";
		String firstName = "";
		String lastName = "";
		String emailId = "";
		
		if (student != null) {
		firstName = !StringUtils.isBlank(student.getFirstName()) ? student.getFirstName() : ".";
		lastName = !StringUtils.isBlank(student.getLastName()) ? student.getLastName() : ".";
		emailId = !StringUtils.isBlank(student.getEmailId()) ? student.getEmailId() : firstName+"postmaster@nmims.edu";
		
		student.setFirstName(firstName);
		student.setLastName(lastName);
		student.setEmailId(emailId);
		
		}else {
		setError(request, "Error in getting student details. Please login again to the portal and try again.");
		return new ModelAndView("forward:/viewStudentTimeTable?id=" + String.valueOf(session.getId()));
		}
		
		try {
			tempurl = zoomManger.registerForSession(session, student, session.getMeetingKey());
		} catch (IOException e) {
		  
		request.setAttribute("error", true);
		request.setAttribute("errorMessage", "Please try again...");
		}
		
		if (!StringUtils.isBlank(tempurl)) {
		try {
		attendanceFeedbackDAO.recordAttendance(session, student.getSapid(), facultyId, tempurl);
		if (!isReAttended.equalsIgnoreCase("Y")) {
		attendanceFeedbackDAO.updateSessionAttendanceCounter(session.getMeetingKey());
		}
		} catch (Exception e) {
		  
		}
		} else {
		setError(request, "Unable to Start Session. Please try again with an alternate Faculty Session / Contact NGASCE centers.");
		return new ModelAndView("forward:/viewStudentTimeTable?id=" + String.valueOf(session.getId()));
		}
		
		ModelAndView modelnView = new ModelAndView("sessionRedirect");
		modelnView.addObject("session", session);
		//Parameter for WEB_EX
		//modelnView.addObject("name", name);
		//modelnView.addObject("email", email);
		//modelnView.addObject("mobile", mobile);
		//modelnView.addObject("WEB_EX_API_URL", WEB_EX_API_URL);
		modelnView.addObject("tempurl", tempurl);
		
		return modelnView;
	}

	
	
	private ModelAndView classFull(SessionDayTimeAcadsBean session) {
		TimeTableDAO dao = (TimeTableDAO) act.getBean("timeTableDAO");

		ModelAndView modelnView = new ModelAndView("classFull");

		SessionDayTimeAcadsBean searchBean = new SessionDayTimeAcadsBean();
		searchBean.setSubject(session.getSubject());
		searchBean.setYear(session.getYear());
		searchBean.setMonth(session.getMonth());
		PageAcads<SessionDayTimeAcadsBean> page = dao.getScheduledSessionPage(1, pageSize, searchBean, "all");
		List<SessionDayTimeAcadsBean> scheduledSessionList = page.getPageItems();
		scheduledSessionList = dao.getScheduledSessionBySubject(session);
		modelnView.addObject("page", page);
		modelnView.addObject("rowCount", page.getRowCount());
		modelnView.addObject("searchBean", searchBean);
		modelnView.addObject("yearList", ACAD_YEAR_LIST);
		modelnView.addObject("subjectList", getSubjectList());
		modelnView.addObject("session", session);

		modelnView.addObject("scheduledSessionList", scheduledSessionList);

		return modelnView;
	}
	
	private ModelAndView joinPreferredFacultySessionV2(String joinFor, StudentAcadsBean student, SessionDayTimeAcadsBean session,
			HttpServletRequest request) {

		TimeTableDAO dao = (TimeTableDAO) act.getBean("timeTableDAO");
		int availableSeats = 0;
		switch (joinFor) {
		case "HOST":
			availableSeats = dao.getAttendanceByMeetingKey(session.getMeetingKey());
			if (availableSeats > 0) {
				return joinSession(student, session, session.getFacultyId(), "N", request);
			}
			break;
		case "ALTFACULTYID":
			availableSeats = dao.getAttendanceByMeetingKey(session.getAltMeetingKey());
			if (availableSeats > 0) {
				return joinSessionUsingAlternateFacultyConfiguration(student, session, session.getAltMeetingKey(),
						session.getAltMeetingPwd(), session.getAltFacultyId(), "N", request);
			}
			break;
		case "ALTFACULTYID2":
			availableSeats = dao.getAttendanceByMeetingKey(session.getAltMeetingKey2());
			if (availableSeats > 0) {
				return joinSessionUsingAlternateFacultyConfiguration(student, session, session.getAltMeetingKey2(),
						session.getAltMeetingPwd2(), session.getAltFacultyId2(), "N", request);
			}
			break;
		case "ALTFACULTYID3":
			availableSeats = dao.getAttendanceByMeetingKey(session.getAltMeetingKey3());
			if (availableSeats > 0) {
				return joinSessionUsingAlternateFacultyConfiguration(student, session, session.getAltMeetingKey3(),
						session.getAltMeetingPwd3(), session.getAltFacultyId3(), "N", request);
			}
			break;
		}

		// If it reached here, then it means seats are not available
		setError(request, "Class is full. Please join another faculty session");
		return new ModelAndView("forward:/viewStudentTimeTable?id=" + String.valueOf(session.getId()));
	}
	
	private ModelAndView reAttendSessionWeb(String joinUrl) {
		ModelAndView modelnView = new ModelAndView("sessionRedirect");
		modelnView.addObject("tempurl", joinUrl);
		return modelnView;
	}
	
	@RequestMapping(value = "/getPostSessionFeedback", method = {RequestMethod.GET})
    public ModelAndView getSessionFeedback(HttpServletRequest request, HttpServletResponse response) {
    	String userId="";
    	String sessionId="";
    	String acadDateFormat="";
    	Cookie[] cookies=request.getCookies();
    	
    	for(Cookie userCookie: cookies) {

			if("st_userId".equals(userCookie.getName())) {
				userId=userCookie.getValue();
			}
			
			if("st_sessionId".equals(userCookie.getName())) {
				sessionId=userCookie.getValue();
			}
			
			if("st_acadDateFormat".equals(userCookie.getName())) {
				acadDateFormat=userCookie.getValue();
			}

    	}
    	if(!"".equals(userId) && !"".equals(sessionId) && !"".equals(acadDateFormat)) {
    		return checkForFeedbackAfterSessionEnd(userId, sessionId,acadDateFormat,request, response);
    	}
    	return new ModelAndView("studentPortalRediret");
    }
    
    private ModelAndView checkForFeedbackAfterSessionEnd(String userId,String sessionId, String acadDateFormat,HttpServletRequest request,HttpServletResponse response) {
		request.getSession().setAttribute("userId_acads",userId);
		StudentAcadsBean student=attendanceFeedbackDAO.getSingleStudentsData(userId);
		request.getSession().setAttribute("student_acads",student);

		SessionAttendanceFeedbackAcads pendingFeedback = new SessionAttendanceFeedbackAcads();
		
		try {
			pendingFeedback = attendanceService.getPostSessionFeedback(userId, sessionId, acadDateFormat);
			ModelAndView modelnView = new ModelAndView("postSessionFeedback");
			modelnView.addObject("feedback", pendingFeedback);
			return modelnView;
		}catch (Exception e) {
	    	return new ModelAndView("studentPortalRediret");
		}
	}
    
    @RequestMapping(value = "/savePostFeedback", method = RequestMethod.POST)
	public ModelAndView savePostFeedback(HttpServletRequest request, HttpServletResponse response, @ModelAttribute SessionAttendanceFeedbackAcads feedback){

		String userId = (String)request.getSession().getAttribute("userId_acads");
		feedback.setSapId(userId);
		feedback.setFeedbackGiven("Y");
		feedback.setCreatedBy(userId);
		feedback.setLastModifiedBy(userId);
		try {
			attendanceService.saveFeedback(feedback);
			return new ModelAndView("postFeedbackRedirectPortal");
		}catch (Exception e) {
			return new ModelAndView("studentPortalRediret");
		}
	}
	
}
