package com.nmims.controllers;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.TimeUnit;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

import com.nmims.beans.EventBean;
import com.nmims.beans.ExamBookingTransactionAcadsBean;
import com.nmims.beans.ExamOrderAcadsBean;
import com.nmims.beans.FacultyAcadsBean;
import com.nmims.beans.ProgramSubjectMappingAcadsBean;
import com.nmims.beans.SessionDayTimeAcadsBean;
import com.nmims.beans.StudentAcadsBean;
import com.nmims.beans.VideoContentAcadsBean;
import com.nmims.daos.FacultyDAO;
import com.nmims.daos.SessionTracksDAO;
import com.nmims.daos.TimeTableDAO;
import com.nmims.daos.VideoContentDAO;
import com.nmims.services.StudentService;
import com.nmims.services.TimeTableService;

@Controller
@CrossOrigin(origins = "*", allowedHeaders = "*")
@RequestMapping("/student")
public class StudentTimeTableController extends BaseController{
	
	@Autowired
	SessionTracksDAO sessionTracksDao;
	
	@Autowired(required = false)
	ApplicationContext act;
	
	@Autowired
	TimeTableService timeTableService;
	
	@Autowired
	StudentService studentService;
	
	@Value("${SERVER_PATH}")
	private String SERVER_PATH;
	
	private HashMap<String, FacultyAcadsBean> mapOfFacultyIdAndFacultyRecord = null;
	private ArrayList<ProgramSubjectMappingAcadsBean> programSubjectMappingList = null;
	
	public HashMap<String, FacultyAcadsBean> mapOfFacultyIdAndFacultyRecord() {
		FacultyDAO facultyDao = (FacultyDAO) act.getBean("facultyDAO");
		ArrayList<FacultyAcadsBean> listOfAllFaculties = facultyDao.getAllFacultyRecords();
		if (this.mapOfFacultyIdAndFacultyRecord == null) {
			this.mapOfFacultyIdAndFacultyRecord = new HashMap<String, FacultyAcadsBean>();
			for (FacultyAcadsBean faculty : listOfAllFaculties) {
				this.mapOfFacultyIdAndFacultyRecord.put(faculty.getFacultyId(), faculty);
			}
		}
		return mapOfFacultyIdAndFacultyRecord;
	}
	
	public ArrayList<ProgramSubjectMappingAcadsBean> getProgramSubjectMappingList(){
		if(this.programSubjectMappingList == null || this.programSubjectMappingList.size() == 0){
			TimeTableDAO dao = (TimeTableDAO)act.getBean("timeTableDAO");
			this.programSubjectMappingList = dao.getProgramSubjectMappingList();
		}
		return programSubjectMappingList;
	}
	
	public static long getDateDiff(Date date1, Date date2, TimeUnit timeUnit) {
		long diffInMillies = date2.getTime() - date1.getTime();
		return timeUnit.convert(diffInMillies, TimeUnit.MILLISECONDS);
	}
	
	@RequestMapping(value = "/viewStudentTimeTable", method = {RequestMethod.GET, RequestMethod.POST})
	public ModelAndView viewStudentTimeTable(HttpServletRequest request, HttpServletResponse respnse, Model m) {
		ModelAndView modelnView = new ModelAndView("studentAcadCalendar");

		if(!checkSession(request, respnse)){
			return new ModelAndView("studentPortalRediret");
		}
		
		try{
			ArrayList<SessionDayTimeAcadsBean> scheduledSessionListFromToday = new ArrayList<SessionDayTimeAcadsBean>();
			ArrayList<SessionDayTimeAcadsBean> scheduledSessionList = new ArrayList<SessionDayTimeAcadsBean>();			
			StudentAcadsBean student = (StudentAcadsBean)request.getSession().getAttribute("student_acads");
			String sapId = (String)request.getSession().getAttribute("userId_acads");
			TimeTableDAO dao = (TimeTableDAO)act.getBean("timeTableDAO");
			SessionTracksDAO trackDao = (SessionTracksDAO) act.getBean("sessionTracksDao");
			//Check student registration and return reg details
			StudentAcadsBean studentRegistrationForAcademicSession = timeTableService.checkStudentRegistration(sapId, student);
			
			//Start Get Registered Exam Dates
			List<ExamBookingTransactionAcadsBean> bookedExams = dao.getBookedExams(student.getSapid());
			request.setAttribute("bookedExams", bookedExams);
			//End Get Registered Exam Dates

			//Start Get Key Events Dates
			List<EventBean> eventsList = dao.getEventsList();
			request.setAttribute("eventsList", eventsList);
			//End Get Key Events Dates
			/**
			 * Commented by Somesh ( As Waived in subject directly coming from Student Course Mapping Table)
			 */
			//boolean isCourseMappingAvailable = dao.isCourseMappingApplicableForCurrentAcadCycle(student.getSapid());
			List<Integer> currentSemPSSId = (List<Integer>) request.getSession().getAttribute("currentSemPSSId");
			
			ExamOrderAcadsBean examOrderForSession = null;
			if (studentRegistrationForAcademicSession != null) {
				try {
					examOrderForSession = dao.getExamOrderByYearMonth(studentRegistrationForAcademicSession.getYear(), studentRegistrationForAcademicSession.getMonth());
				} catch (Exception e) {
					  
				}
			}
			
			if(studentRegistrationForAcademicSession == null || examOrderForSession == null){
				
			/*	if (isCourseMappingAvailable) {
					scheduledSessionList = dao.getAllSessionsByCourseMapping(student.getSapid());
					scheduledSessionListFromToday = dao.getAllSessionsByCourseMappingFromToday(student.getSapid());
					
					modelnView.addObject("scheduledSessionList", scheduledSessionList);
					modelnView.addObject("scheduledSessionListFromToday", scheduledSessionListFromToday);
					
					request.getSession().setAttribute("studentSessionList", scheduledSessionList);
					request.getSession().setAttribute("scheduledSessionListFromToday", scheduledSessionListFromToday);
					
					return modelnView;
				}*/
				
				request.setAttribute("error", "true");
				request.setAttribute("errorMessage", "Academic Calendar is not live currently.");
				modelnView.addObject("scheduledSessionList",new ArrayList<SessionDayTimeAcadsBean>());
				return modelnView;
			}
			
			//For Old students incomplete data//
			//Take program from Registration data and not Student data. 
			student.setProgram(studentRegistrationForAcademicSession.getProgram()); 
			student.setSem(studentRegistrationForAcademicSession.getSem());
			String year = studentRegistrationForAcademicSession.getYear();
			String month =studentRegistrationForAcademicSession.getMonth();
			String consumerProgramStructureId = student.getConsumerProgramStructureId();
			String sem = studentRegistrationForAcademicSession.getSem();
			String sapid = student.getSapid();
			
			scheduledSessionList = timeTableService.getAllScheduledSessionsForPG(sapid, year, month, consumerProgramStructureId, sem, currentSemPSSId);
			
			//To be discussed
			scheduledSessionListFromToday = timeTableService.getScheduledSessionsFromToday(student, year, month);
			
			modelnView.addObject("scheduledSessionList", scheduledSessionList);
			modelnView.addObject("scheduledSessionListFromToday", scheduledSessionListFromToday);
			modelnView.addObject("trackDetails", trackDao.getAllTracksDetails());
			request.getSession().setAttribute("trackDetails", trackDao.getAllTracksDetails());
			request.getSession().setAttribute("studentSessionList", scheduledSessionList);//To avoid fetching again
			request.getSession().setAttribute("scheduledSessionListFromToday", scheduledSessionListFromToday);
			
		}catch (Exception e) {
			  
		}
		return modelnView;
	}
	
	@RequestMapping(value = "/viewSessionsTimeline", method = {RequestMethod.GET, RequestMethod.POST})
	public ModelAndView viewSessionsTimeline(HttpServletRequest request, HttpServletResponse respnse, Model m) {
		ModelAndView modelnView = new ModelAndView("sessionsTimeline");
		if(!checkSession(request, respnse)){
			return new ModelAndView("studentPortalRediret");
		}
		try{
			ArrayList<SessionDayTimeAcadsBean> scheduledSessionList = new ArrayList<SessionDayTimeAcadsBean>();
			
			TimeTableDAO dao = (TimeTableDAO)act.getBean("timeTableDAO");
			String sapId = (String)request.getSession().getAttribute("userId_acads");
			StudentAcadsBean student = (StudentAcadsBean)request.getSession().getAttribute("student_acads");
			
			//Check student registration and return reg details
			StudentAcadsBean studentRegistrationForAcademicSession = timeTableService.checkStudentRegistration(sapId, student);
			
			/**
			 * Commented by Somesh ( As Waived in subject directly coming from Student Course Mapping Table)
			 */
			//Waived-in subject of current cycle
			//boolean isCourseMappingAvailable = dao.isCourseMappingApplicableForCurrentAcadCycle(student.getSapid());
			List<Integer> currentSemPSSId = (List<Integer>) request.getSession().getAttribute("currentSemPSSId");
			
			ExamOrderAcadsBean examOrderForSession = null;
			if (studentRegistrationForAcademicSession != null) {
				try {
					examOrderForSession = dao.getExamOrderByYearMonth(studentRegistrationForAcademicSession.getYear(), studentRegistrationForAcademicSession.getMonth());
				} catch (Exception e) {
					  
				}
			}
			
			if(studentRegistrationForAcademicSession == null || examOrderForSession == null){
				
				/*if (isCourseMappingAvailable) {
					//Even if student not register for current cycle Waived-in subject is applicable
					scheduledSessionList = dao.getAllSessionsByCourseMapping(student.getSapid());
					scheduledSessionList = getVideosForSessionList(scheduledSessionList,student);
					modelnView.addObject("scheduledSessionList", scheduledSessionList);
					request.getSession().setAttribute("studentSessionList", scheduledSessionList);
					return modelnView;
				}*/
				
				request.setAttribute("error", "true");
				request.setAttribute("errorMessage", "Academic Calendar is not live currently.");
				modelnView.addObject("scheduledSessionList",new ArrayList<SessionDayTimeAcadsBean>());
				return modelnView;
			}
			
			//For Old students incomplete data//
			//Take program from Registration data and not Student data. 
			student.setProgram(studentRegistrationForAcademicSession.getProgram());
			student.setSem(studentRegistrationForAcademicSession.getSem());
			String year = studentRegistrationForAcademicSession.getYear();
			String month = studentRegistrationForAcademicSession.getMonth();
			String consumerProgramStructureId = student.getConsumerProgramStructureId();
			String sem = studentRegistrationForAcademicSession.getSem();
			String sapid = student.getSapid();
			
			scheduledSessionList = timeTableService.getAllScheduledSessionsForPG(sapid, year, month, consumerProgramStructureId, sem, currentSemPSSId);
			if(scheduledSessionList != null && scheduledSessionList.size() != 0){
				scheduledSessionList = getVideosForSessionList(scheduledSessionList, studentRegistrationForAcademicSession);
				modelnView.addObject("scheduledSessionList", scheduledSessionList);
				return modelnView;
			}
			
		}catch (Exception e) {
			  
		}
		
		return modelnView;
	}

	private ArrayList<SessionDayTimeAcadsBean> getVideosForSessionList(ArrayList<SessionDayTimeAcadsBean> scheduledSessionList, StudentAcadsBean student){
		VideoContentDAO dao = (VideoContentDAO) act.getBean("videoContentDAO");
		TimeTableDAO tDao = (TimeTableDAO)act.getBean("timeTableDAO");
		List<VideoContentAcadsBean> videos=null;
		HashMap<String, String> getStudentSessionMap = new HashMap<>();
		getStudentSessionMap = tDao.getAttendanceForSessionMap(student.getSapid());
		
		String key = "";
		for(SessionDayTimeAcadsBean bean:scheduledSessionList) {
			videos = new ArrayList<>();
			videos= dao.getVideosForSession(bean.getId());
			bean.setVideosOfSession(videos);
			key = student.getSapid() + " - "+bean.getId();
			
			if (getStudentSessionMap.containsKey(key)) {
				bean.setAttended("Yes");
			}else{
				bean.setAttended("No");
			}
			
		}
		return scheduledSessionList;
	}
	
	@RequestMapping(value = "/viewScheduledSession", method = { RequestMethod.GET, RequestMethod.POST })
	public ModelAndView viewScheduledSession(HttpServletRequest request, HttpServletResponse response)
			throws Exception {
		ModelAndView modelnView = new ModelAndView("viewScheduledSession");

		String userId = (String) request.getSession().getAttribute("userId_acads");
		StudentAcadsBean student = (StudentAcadsBean) request.getSession().getAttribute("student_acads");
		
		String id = request.getParameter("id");
		String pssId = request.getParameter("pssId") != null ? request.getParameter("pssId") : "";
		List<Integer> liveSessionPssIdAccessList = (List<Integer>) request.getSession().getAttribute("liveSessionPssIdAccess_acads");
		String isSessionAccess = "false";
		String formatedDob = "";
		
		TimeTableDAO dao = (TimeTableDAO) act.getBean("timeTableDAO");
		SessionDayTimeAcadsBean session = dao.findScheduledSessionById(id);
		
		if (student != null) {
			Date dob = new SimpleDateFormat("yyyy-MM-dd").parse(student.getDob());
			formatedDob = new SimpleDateFormat("dd/MM/yyyy").format(dob);
		}

		String pssIdsCommaSeparated = "";
		if (liveSessionPssIdAccessList != null && liveSessionPssIdAccessList.size() > 0) {
			pssIdsCommaSeparated = StringUtils.join(liveSessionPssIdAccessList, ",");
		}
		
		if (pssIdsCommaSeparated.contains(pssId)) {
			isSessionAccess = "true";
		}else if(session.getSubject().equalsIgnoreCase("Orientation") || session.getSubject().equalsIgnoreCase("Assignment")) {
			isSessionAccess = "true";
		}else if(session.getSessionName().contains("Doubt Clearing")){
			isSessionAccess = "true";
		}

		if ("Y".equalsIgnoreCase(session.getHasModuleId())) {
			session.setSubject(session.getSubject() + " (MBA-WX)");
		}

		HashMap<String, Integer> facultyIdAndRemSeatsMap = dao.getMapOfFacultyIdAndRemainingSeatsV2(session);
		modelnView.addObject("facultyIdAndRemSeatsMap", facultyIdAndRemSeatsMap);
		modelnView.addObject("session", session);
		modelnView.addObject("pssId", pssId);

		String sessionDate = session.getDate();
		String sessionTime = session.getStartTime();
		
		Date sessionDateTime = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(sessionDate + " " + sessionTime);
		long minutesToSession = getDateDiff(new Date(), sessionDateTime, TimeUnit.MINUTES);
		long minutesAfterSession = getDateDiff(sessionDateTime, new Date(), TimeUnit.MINUTES);

		modelnView.addObject("userId", userId);
		modelnView.addObject("dob", formatedDob);
		modelnView.addObject("isSessionAccess", isSessionAccess);
		modelnView.addObject("enableAttendButton", "false");
		modelnView.addObject("showQueryButton", "false");
		modelnView.addObject("sessionOver", "false");
		modelnView.addObject("mapOfFacultyIdAndFacultyRecord", mapOfFacultyIdAndFacultyRecord());
		
		if (minutesToSession < 60 && minutesToSession > -120) {
			modelnView.addObject("enableAttendButton", "true");
		}

		if (minutesAfterSession > 120) {
			// Added Temporary To hide Post My Query button for Guest Lecture
			if (!"Guest Lecture".equalsIgnoreCase(session.getSessionName())) {
				modelnView.addObject("showQueryButton", "true");
			}
			modelnView.addObject("sessionOver", "true");
			modelnView.addObject("videoId", dao.getSessionVideoId(id, session.getFacultyId()));
			modelnView.addObject("altVideoId", dao.getSessionVideoId(id, session.getAltFacultyId()));
			modelnView.addObject("alt2VideoId", dao.getSessionVideoId(id, session.getAltFacultyId2()));
			modelnView.addObject("alt3videoId", dao.getSessionVideoId(id, session.getAltFacultyId3()));
		}

		ArrayList<ProgramSubjectMappingAcadsBean> subjectProgramList = new ArrayList<ProgramSubjectMappingAcadsBean>();
		//For Admin only
		if (student == null) {
			subjectProgramList = dao.getSubjectProgramListBySessionId(id);
		}
		modelnView.addObject("subjectProgramList", subjectProgramList);
		modelnView.addObject("SERVER_PATH", SERVER_PATH);

		return modelnView;
	}

}
