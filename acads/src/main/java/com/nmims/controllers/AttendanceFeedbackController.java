package com.nmims.controllers;

import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.google.gson.JsonObject;
import com.nmims.beans.EndPointBean;
import com.nmims.beans.PageAcads;
import com.nmims.beans.ParticipantReportBean;
import com.nmims.beans.ParticipantsListBean;
import com.nmims.beans.ProgramSubjectMappingAcadsBean;
import com.nmims.beans.SessionAttendanceFeedbackAcads;
import com.nmims.beans.SessionDayTimeAcadsBean;
import com.nmims.beans.SessionReviewBean;
import com.nmims.beans.StudentAcadsBean;
import com.nmims.beans.UserAuthorizationBean;
import com.nmims.daos.AttendanceFeedbackDAO;
import com.nmims.daos.FacultyDAO;
import com.nmims.daos.ReportsDAO;
import com.nmims.daos.SessionQueryAnswerDAO;
import com.nmims.daos.TimeTableDAO;
import com.nmims.helpers.ConferenceBookingClient;
import com.nmims.helpers.WebExMeetingManager;
import com.nmims.helpers.ZoomManager;
import com.nmims.listeners.SessionRecordingScheduler;
import com.nmims.services.AttendanceFeedbackService;
import com.nmims.services.AttendnaceReportService;
import com.nmims.services.StudentService;
import com.nmims.util.ContentUtil;

@Controller
@CrossOrigin(origins = "*", allowedHeaders = "*")
@RequestMapping("/admin")
public class AttendanceFeedbackController extends BaseController {

	@Autowired(required = false)
	ApplicationContext act;

	@Autowired
	private AttendanceFeedbackDAO attendanceFeedbackDAO;
	@Autowired
	private ConferenceBookingClient conferenceBookingClient;
	@Autowired
	private WebExMeetingManager webExManager;
	@Autowired
	private ZoomManager zoomManger;
	
	@Autowired
	private AttendanceFeedbackService attendanceService;
	
	@Autowired
	ReportsDAO rDao;
	
	@Autowired
	AttendnaceReportService attendanceReportService;

	@Autowired
	StudentService studentService;

	@Value("${WEB_EX_API_URL}")
	private String WEB_EX_API_URL;

	@Value("${WEB_EX_LOGIN_API_URL}")
	private String WEB_EX_LOGIN_API_URL;

	@Value("${WEBEX_ID}")
	private String WEBEX_ID;

	@Value("${WEBEX_PASS}")
	private String WEBEX_PASS;

	@Value("${MAX_WEBEX_USERS}")
	private int MAX_WEBEX_USERS2;
	
	@Value( "${CURRENT_ACAD_MONTH}" )
	private String CURRENT_ACAD_MONTH;

	@Value( "${CURRENT_ACAD_YEAR}" )
	private String CURRENT_ACAD_YEAR;
	
	@Value( "${CURRENT_MBAWX_ACAD_MONTH}" )
	private String CURRENT_MBAWX_ACAD_MONTH;

	@Value( "${CURRENT_MBAWX_ACAD_YEAR}" )
	private String CURRENT_MBAWX_ACAD_YEAR;

	private int MAX_WEBEX_USERS = 1000;

	@Value("${SESSION_ATTENDANCE_BUFFER}")
	private int SESSION_ATTENDANCE_BUFFER;

	@Value("#{'${ACAD_YEAR_LIST}'.split(',')}")
	private List<String> ACAD_YEAR_LIST;

	@Value("#{'${ACAD_MONTH_LIST}'.split(',')}")
	private List<String> ACAD_MONTH_LIST;
	
	@Value("${TIMEBOUND_PORTAL_LIST}")
	private List<String> TIMEBOUND_PORTAL_LIST;
	
	@Value("${ICLCRESTRICTED_USER_LIST}")
	private List<String> ICLCRESTRICTED_USER_LIST;
	
	private ArrayList<String> programList = null;
	private ArrayList<String> subjectList = null;
	private ArrayList<String> facultyName = null;
	private ArrayList<String> facultyList = null;
	private ArrayList<EndPointBean> endPointList = null;
	private ArrayList<String> sessionList = null;
	private Map<String, String> subjectCodeMap = null;
	private final int pageSize = 10;

	private static final Logger logger = LoggerFactory.getLogger(AttendanceFeedbackController.class);
	private ArrayList<String> yearList = new ArrayList<String>(Arrays.asList("2015", "2016", "2017"));
	private HashMap<String, ProgramSubjectMappingAcadsBean> subjectProgramMap = null;

	private HashMap<String, ArrayList<String>> programAndProgramStructureAndSubjectsMap = new HashMap<>();
	
	private ArrayList<String> nonPG_ProgramList = new ArrayList<String>(Arrays.asList("BBA", "B.Com", "PD - WM","PD - DM",
			"M.Sc. (App. Fin.)", "CP-WL", "BBA-BA"));

	public ArrayList<ProgramSubjectMappingAcadsBean> getSubjectProgramList() {
		TimeTableDAO dao = (TimeTableDAO) act.getBean("timeTableDAO");
		ArrayList<ProgramSubjectMappingAcadsBean> subjectProgramList = dao.getSubjectProgramList();
		return subjectProgramList;
	}

	@ModelAttribute("subjectList")
	public ArrayList<String> getSubjectList() {
		if (this.subjectList == null) {
			TimeTableDAO dao = (TimeTableDAO) act.getBean("timeTableDAO");
			this.subjectList = dao.getAllSubjects();
		}
		return subjectList;
	}
	
	public Map<String, String> getsubjectCodeMap() {
		if (this.subjectCodeMap == null) {
			TimeTableDAO dao = (TimeTableDAO) act.getBean("timeTableDAO");
			this.subjectCodeMap = dao.getsubjectCodeMapForReport();
		}
		return subjectCodeMap;
	}

	/*
	 * 
	 * 
	 * added by stef on 6-Nov
	 * 
	 * @ModelAttribute("sessionList") public ArrayList<String> getSessionList(){
	 * if(this.sessionList == null){ SessionQueryAnswerDAO dao =
	 * (SessionQueryAnswerDAO)act.getBean("sessionQueryAnswerDAO"); this.sessionList
	 * = dao.getSessionList(); } return sessionList; }
	 */

	@ModelAttribute("facultyName")
	public ArrayList<String> getFacultyNameList() {
		if (this.facultyName == null) {
			FacultyDAO dao = (FacultyDAO) act.getBean("facultyDAO");
			this.facultyName = dao.getFacultyNameList();
		}
		return facultyName;

	}

	public ArrayList<String> getProgramList() {
		if (this.programList == null) {
			TimeTableDAO dao = (TimeTableDAO) act.getBean("timeTableDAO");
			this.programList = dao.getAllPrograms();
		}
		return programList;
	}

	public ArrayList<String> getFacultyList() {
		// if(this.facultyList == null){
		TimeTableDAO dao = (TimeTableDAO) act.getBean("timeTableDAO");
		this.facultyList = dao.getAllFaculties();
		// }
		return facultyList;
	}

	public ArrayList<EndPointBean> getEndPointList() {
		if (this.endPointList == null) {
			TimeTableDAO dao = (TimeTableDAO) act.getBean("timeTableDAO");
			this.endPointList = dao.getAllFacultyRoomEndPoints();
		}
		return endPointList;
	}

	@RequestMapping(value = "/attendScheduledSessionOld", method = { RequestMethod.GET, RequestMethod.POST })
	public ModelAndView attendScheduledSessionOld(HttpServletRequest request, HttpServletResponse response)
			throws Exception {

		String forMobile = request.getParameter("forMobile");
		StudentAcadsBean student = null;
		TimeTableDAO dao = (TimeTableDAO) act.getBean("timeTableDAO");
		SessionAttendanceFeedbackAcads attendance = new SessionAttendanceFeedbackAcads();

		if ("true".equalsIgnoreCase(forMobile)) {
			// Called from Mobile App, Student details not available in session
			String sapid = request.getParameter("sapid");
			String email = request.getParameter("email");
			String firstName = request.getParameter("firstName");
			String lastName = request.getParameter("lastName");
			String mobile = request.getParameter("mobile");

			student = new StudentAcadsBean();
			student.setSapid(sapid);
			student.setEmailId(email);
			student.setMobile(mobile);
			student.setFirstName(firstName);
			student.setLastName(lastName);
			attendance.setDevice("MobileApp");

		} else {
			// Called via Desktop
			if (!checkSession(request, response)) {
				return new ModelAndView("studentPortalRediret");
			}

			student = (StudentAcadsBean) request.getSession().getAttribute("student_acads");// Take from session
			attendance.setDevice("WebApp");
		}

		String id = request.getParameter("id");
		String joinFor = request.getParameter("joinFor");

		/* String joinForParameter = request.getParameter("joinFor"); */
		SessionDayTimeAcadsBean session = dao.findScheduledSessionById(id);
		session.setDevice(attendance.getDevice());

		String acadDateFormat = ContentUtil.prepareAcadDateFormat(session.getMonth(), session.getYear());
		attendance = dao.checkSessionAttendance(student.getSapid(), id, acadDateFormat); 

		if (attendance != null) {
			// Student has already joined session. Send him to same session again.
			return joinSessionUsingAlternateFacultyConfiguration(student, session, attendance.getMeetingKey(),
					attendance.getMeetingPwd(), attendance.getFacultyId(), "Y", request);
		}

		if (!"ANY".equalsIgnoreCase(joinFor)) {
			
			// If Orientation Session Do Not check limit
			if (session.getSubject().equalsIgnoreCase("Orientation")) {
				return joinSession(student, session, session.getFacultyId(), "N", request);
			}
			
			// Student wants to join specific faculty session
			return joinPreferredFacultySession(joinFor, student, session, request);
		}

		boolean hasParallelAlternateSession = false;
		boolean hasParallelAlternateSession2 = false;
		boolean hasParallelAlternateSession3 = false;

		if (!StringUtils.isBlank(session.getAltMeetingKey())) {
			hasParallelAlternateSession = true;
		}
		if (!StringUtils.isBlank(session.getAltMeetingKey2())) {
			hasParallelAlternateSession2 = true;
		}
		if (!StringUtils.isBlank(session.getAltMeetingKey3())) {
			hasParallelAlternateSession3 = true;
		}

		int noOfUsersJoined = dao.findUsersJoined(id);

		boolean originalSessionAvailable = noOfUsersJoined < MAX_WEBEX_USERS;
		boolean firstAlternateSessionAvailable = hasParallelAlternateSession && (noOfUsersJoined < (2 * MAX_WEBEX_USERS));
		boolean secondAlternateSessionAvailable = hasParallelAlternateSession2 && (noOfUsersJoined < (3 * MAX_WEBEX_USERS));
		boolean thirdAlternateSessionAvailable = hasParallelAlternateSession3 && (noOfUsersJoined < (4 * MAX_WEBEX_USERS));

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
			// All Classes Full, Divide any new students among running sessions now for
			// additional buffer capacity
			SessionAttendanceFeedbackAcads sessionWithLeastAttendance = dao.getSessionWithLeastNumberOfAttendees(id);
			if ((sessionWithLeastAttendance.getNumberOfAttendees() - MAX_WEBEX_USERS) < SESSION_ATTENDANCE_BUFFER) {
				return joinSessionUsingAlternateFacultyConfiguration(student, session,
						sessionWithLeastAttendance.getMeetingKey(), sessionWithLeastAttendance.getMeetingPwd(),
						sessionWithLeastAttendance.getFacultyId(), "N", request);
			} else {
				return classFull(session);
			}
		}

		/*
		 * if((noOfUsersJoined >= MAX_WEBEX_USERS)){//Not joined yet but class is full
		 * 
		 * //boolean hasParallelAlternateSession =
		 * dao.checkIfHasParallelAlternateSession(id,"ALT1");//ALT1,ALT2,ALT3 :-
		 * alternate faculty 1,2,3 and so on
		 * 
		 * 
		 * 
		 * 
		 * if(!hasParallelAlternateSession){//If no alternate session then show class
		 * full return classFull(session); }else if(hasParallelAlternateSession &&
		 * (noOfUsersJoined >= ((2 *MAX_WEBEX_USERS) + 25))){ //Has parallel session but
		 * that is also Full
		 * 
		 * if(!hasParallelAlternateSession2){//If no alternate session 2 then show class
		 * full return classFull(session); }else if(hasParallelAlternateSession2 &&
		 * (noOfUsersJoined >= ((3 *MAX_WEBEX_USERS) + 50))){ //Has 2nd parallel session
		 * but that is also Full if(!hasParallelAlternateSession3){//If no alternate
		 * session 3 then show class full return classFull(session); }else
		 * if(hasParallelAlternateSession3 && (noOfUsersJoined >= ((4 *MAX_WEBEX_USERS)
		 * + 75))){ //Has 3rd parallel session but that is also Full return
		 * classFull(session); }else{ //3rd parallel session is not yet full, allow to
		 * join return
		 * joinSessionUsingAlternateFacultyConfiguration(student,userId,session,session.
		 * getAltMeetingKey3(),session.getAltMeetingPwd3(),session.getAltFacultyId3());
		 * //For Third threshold// } }else{ //2nd parallel session is not yet full,
		 * allow to join return
		 * joinSessionUsingAlternateFacultyConfiguration(student,userId,session,session.
		 * getAltMeetingKey2(),session.getAltMeetingPwd2(),session.getAltFacultyId2());
		 * //For Second threshold//
		 * 
		 * } }else{ //Has 2nd parallel session, which is not yet full return
		 * joinSessionUsingAlternateFacultyConfiguration(student,userId,session,session.
		 * getAltMeetingKey(),session.getAltMeetingPwd(),session.getAltFacultyId());
		 * //For First threshold// } }else if(attendedPreviousSession){ //Attended
		 * previous session ModelAndView modelnView = new
		 * ModelAndView("alreadyAttended"); return modelnView; }else{ return
		 * joinSession(student, session, userId, session.getFacultyId());//Didn't join
		 * session yet and class is not yet full }
		 */

		// return new ModelAndView("redirect:" + joinUrl);
	}

	/*
	 * @CrossOrigin(origins="http://10.100.100.90:3000", allowedHeaders="*")
	 * //temporary for mobile to be deleted later start
	 * 
	 * @RequestMapping(value = "/m/attendScheduledSession", method =
	 * RequestMethod.GET, consumes = "application/json",produces =
	 * "application/json") public ResponseEntity<HashMap<String,String>>
	 * m_attendScheduledSession(HttpServletRequest request, HttpServletResponse
	 * response) throws Exception{ HttpHeaders headers = new HttpHeaders();
	 * headers.add("Content-Type", "application/json");
	 * 
	 * String forMobile = request.getParameter("forMobile"); StudentBean student =
	 * null; SessionAttendanceFeedback attendance = new SessionAttendanceFeedback();
	 * TimeTableDAO dao = (TimeTableDAO)act.getBean("timeTableDAO");
	 * HashMap<String,String> responseObj = new HashMap<>();
	 * 
	 * if("true".equalsIgnoreCase(forMobile)) { //Called from Mobile App, Student
	 * details not available in session String sapid =
	 * request.getParameter("sapid"); String email = request.getParameter("email");
	 * String firstName = request.getParameter("firstName"); String lastName =
	 * request.getParameter("lastName"); String mobile =
	 * request.getParameter("mobile");
	 * 
	 * student = new StudentBean(); student.setSapid(sapid);
	 * student.setEmailId(email); student.setMobile(mobile);
	 * student.setFirstName(firstName); student.setLastName(lastName);
	 * attendance.setDevice("MobileApp");
	 * 
	 * }else{ //Called via Desktop // if(!checkSession(request, response)){ //
	 * return new ModelAndView("studentPortalRediret"); // }
	 * 
	 * // student = (StudentBean)request.getSession().getAttribute("student_acads");//Take
	 * from session
	 * 
	 * String sapid = request.getParameter("sapid"); String email =
	 * request.getParameter("email"); String firstName =
	 * request.getParameter("firstName"); String lastName =
	 * request.getParameter("lastName"); String mobile =
	 * request.getParameter("mobile");
	 * 
	 * student = new StudentBean(); student.setSapid(sapid);
	 * student.setEmailId(email); student.setMobile(mobile);
	 * student.setFirstName(firstName); student.setLastName(lastName);
	 * attendance.setDevice("WebApp");
	 * 
	 * }
	 * 
	 * 
	 * 
	 * String id = request.getParameter("id"); String joinFor =
	 * request.getParameter("joinFor");
	 * 
	 * 
	 * String joinForParameter = request.getParameter("joinFor"); SessionDayTimeBean
	 * session = dao.findScheduledSessionById(id);
	 * 
	 * session.setDevice(attendance.getDevice()); attendance =
	 * dao.checkSessionAttendance(student.getSapid(), id);
	 * 
	 * 
	 * if(attendance != null){ //Student has already joined session. Send him to
	 * same session again. return
	 * joinSessionUsingAlternateFacultyConfiguration(student,session,attendance.
	 * getMeetingKey(),attendance.getMeetingPwd(),attendance.getFacultyId()); }
	 * 
	 * if(!"ANY".equalsIgnoreCase(joinFor)){ //Student wants to join specific
	 * faculty session return joinPreferredFacultySession(joinFor, student, session,
	 * request); }
	 * 
	 * boolean hasParallelAlternateSession = false; boolean
	 * hasParallelAlternateSession2 = false; boolean hasParallelAlternateSession3 =
	 * false;
	 * 
	 * if(!StringUtils.isBlank(session.getAltMeetingKey())){
	 * hasParallelAlternateSession = true; }
	 * if(!StringUtils.isBlank(session.getAltMeetingKey2())){
	 * hasParallelAlternateSession2 = true; }
	 * if(!StringUtils.isBlank(session.getAltMeetingKey3())){
	 * hasParallelAlternateSession3 = true; }
	 * 
	 * int noOfUsersJoined = dao.findUsersJoined(id);
	 * 
	 * boolean originalSessionAvailable = noOfUsersJoined < MAX_WEBEX_USERS; boolean
	 * firstAlternateSessionAvailable = hasParallelAlternateSession &&
	 * (noOfUsersJoined < (2 * MAX_WEBEX_USERS) ); boolean
	 * secondAlternateSessionAvailable = hasParallelAlternateSession2 &&
	 * (noOfUsersJoined < (3 * MAX_WEBEX_USERS) ); boolean
	 * thirdAlternateSessionAvailable = hasParallelAlternateSession3 &&
	 * (noOfUsersJoined < (4 * MAX_WEBEX_USERS) );
	 * 
	 * 
	 * 
	 * if(originalSessionAvailable){ HashMap<String, String> joinurl =
	 * joinSession2(student, session, session.getFacultyId());
	 * 
	 * return new ResponseEntity<HashMap<String,String>>(joinurl, headers,
	 * HttpStatus.OK); }else if(firstAlternateSessionAvailable){ return
	 * joinSessionUsingAlternateFacultyConfiguration(student,session,session.
	 * getAltMeetingKey(),session.getAltMeetingPwd(),session.getAltFacultyId());
	 * //For First threshold// }else if(secondAlternateSessionAvailable){ return
	 * joinSessionUsingAlternateFacultyConfiguration(student,session,session.
	 * getAltMeetingKey2(),session.getAltMeetingPwd2(),session.getAltFacultyId2());
	 * //For Second threshold// }else if(thirdAlternateSessionAvailable){ return
	 * joinSessionUsingAlternateFacultyConfiguration(student,session,session.
	 * getAltMeetingKey3(),session.getAltMeetingPwd3(),session.getAltFacultyId3());
	 * //For Third threshold// }else{ //All Classes Full, Divide any new students
	 * among running sessions now for additional buffer capacity
	 * SessionAttendanceFeedback sessionWithLeastAttendance =
	 * dao.getSessionWithLeastNumberOfAttendees(id);
	 * if((sessionWithLeastAttendance.getNumberOfAttendees() - MAX_WEBEX_USERS) <
	 * SESSION_ATTENDANCE_BUFFER){ return
	 * joinSessionUsingAlternateFacultyConfiguration(student,session,
	 * sessionWithLeastAttendance.getMeetingKey(),sessionWithLeastAttendance.
	 * getMeetingPwd(),sessionWithLeastAttendance.getFacultyId()); }else{ return
	 * classFull(session); } }
	 * 
	 * return new ResponseEntity<HashMap<String,String>>(responseObj,headers,
	 * HttpStatus.CONFLICT);
	 * 
	 * //return null;
	 * 
	 * if((noOfUsersJoined >= MAX_WEBEX_USERS)){//Not joined yet but class is full
	 * 
	 * //boolean hasParallelAlternateSession =
	 * dao.checkIfHasParallelAlternateSession(id,"ALT1");//ALT1,ALT2,ALT3 :-
	 * alternate faculty 1,2,3 and so on
	 * 
	 * 
	 * 
	 * 
	 * if(!hasParallelAlternateSession){//If no alternate session then show class
	 * full return classFull(session); }else if(hasParallelAlternateSession &&
	 * (noOfUsersJoined >= ((2 *MAX_WEBEX_USERS) + 25))){ //Has parallel session but
	 * that is also Full
	 * 
	 * if(!hasParallelAlternateSession2){//If no alternate session 2 then show class
	 * full return classFull(session); }else if(hasParallelAlternateSession2 &&
	 * (noOfUsersJoined >= ((3 *MAX_WEBEX_USERS) + 50))){ //Has 2nd parallel session
	 * but that is also Full if(!hasParallelAlternateSession3){//If no alternate
	 * session 3 then show class full return classFull(session); }else
	 * if(hasParallelAlternateSession3 && (noOfUsersJoined >= ((4 *MAX_WEBEX_USERS)
	 * + 75))){ //Has 3rd parallel session but that is also Full return
	 * classFull(session); }else{ //3rd parallel session is not yet full, allow to
	 * join return
	 * joinSessionUsingAlternateFacultyConfiguration(student,userId,session,session.
	 * getAltMeetingKey3(),session.getAltMeetingPwd3(),session.getAltFacultyId3());
	 * //For Third threshold// } }else{ //2nd parallel session is not yet full,
	 * allow to join return
	 * joinSessionUsingAlternateFacultyConfiguration(student,userId,session,session.
	 * getAltMeetingKey2(),session.getAltMeetingPwd2(),session.getAltFacultyId2());
	 * //For Second threshold//
	 * 
	 * } }else{ //Has 2nd parallel session, which is not yet full return
	 * joinSessionUsingAlternateFacultyConfiguration(student,userId,session,session.
	 * getAltMeetingKey(),session.getAltMeetingPwd(),session.getAltFacultyId());
	 * //For First threshold// } }else if(attendedPreviousSession){ //Attended
	 * previous session ModelAndView modelnView = new
	 * ModelAndView("alreadyAttended"); return modelnView; }else{ return
	 * joinSession(student, session, userId, session.getFacultyId());//Didn't join
	 * session yet and class is not yet full }
	 * 
	 * 
	 * 
	 * //return new ModelAndView("redirect:" + joinUrl); }
	 */

	// end
	
	private ModelAndView joinPreferredFacultySession(String joinFor, StudentAcadsBean student, SessionDayTimeAcadsBean session,
			HttpServletRequest request) {

		TimeTableDAO dao = (TimeTableDAO) act.getBean("timeTableDAO");
		HashMap<String, Integer> facultyIdAndRemSeatsMap = dao.getMapOfFacultyIdAndRemainingSeatsV2(session);
		int availableSeats = 0;
		switch (joinFor) {
		case "HOST":
			availableSeats = facultyIdAndRemSeatsMap.get(session.getFacultyId()); // checking Map.containsKey for first
																					// time attend session record not
																					// present in session_attendance
																					// table
			if (availableSeats > 0) {
				return joinSession(student, session, session.getFacultyId(), "N", request);
			}
			break;
		case "ALTFACULTYID":
			availableSeats = facultyIdAndRemSeatsMap.get(session.getAltFacultyId());
			if (availableSeats > 0) {
				return joinSessionUsingAlternateFacultyConfiguration(student, session, session.getAltMeetingKey(),
						session.getAltMeetingPwd(), session.getAltFacultyId(), "N", request);
			}
			break;
		case "ALTFACULTYID2":
			availableSeats = facultyIdAndRemSeatsMap.get(session.getAltFacultyId2());
			if (availableSeats > 0) {
				return joinSessionUsingAlternateFacultyConfiguration(student, session, session.getAltMeetingKey2(),
						session.getAltMeetingPwd2(), session.getAltFacultyId2(), "N", request);
			}
			break;
		case "ALTFACULTYID3":
			availableSeats = facultyIdAndRemSeatsMap.get(session.getAltFacultyId3());
			if (availableSeats > 0) {
				return joinSessionUsingAlternateFacultyConfiguration(student, session, session.getAltMeetingKey3(),
						session.getAltMeetingPwd3(), session.getAltFacultyId3(), "N", request);
			}
			break;
		}

		// If it reached here, then it means seats are not available
		setError(request, "Class is full for chosen faculty. Please join another faculty session");
		return new ModelAndView("forward:/viewStudentTimeTable?id=" + String.valueOf(session.getId()));
	}
	
	// Common private method made to
	private ModelAndView joinSessionUsingAlternateFacultyConfiguration(StudentAcadsBean student, SessionDayTimeAcadsBean session,
			String meetingKey, String meetingPassWord, String facultyId, String isReAttended, HttpServletRequest request) {
		session.setMeetingKey(meetingKey);
		session.setMeetingPwd(meetingPassWord);

		return joinSession(student, session, facultyId, isReAttended, request);
	}
	
	private HashMap<String, String> joinPreferredFacultySessionForMobile(String joinFor, StudentAcadsBean student, SessionDayTimeAcadsBean session) {

		TimeTableDAO dao = (TimeTableDAO) act.getBean("timeTableDAO");
		HashMap<String, Integer> facultyIdAndRemSeatsMap = dao.getMapOfFacultyIdAndRemainingSeats(session.getId(),session);
		int availableSeats = 0;
		switch (joinFor) {
		case "HOST":
			availableSeats = facultyIdAndRemSeatsMap.get(session.getFacultyId()); // checking Map.containsKey for first
																				  // time attend session record not
																				  // present in session_attendance table
			if (availableSeats > 0) {
				return joinSessionForMobile(student, session, session.getFacultyId(), "N");
			}
			break;
		case "ALTFACULTYID":
			availableSeats = facultyIdAndRemSeatsMap.get(session.getAltFacultyId());
			if (availableSeats > 0) {
				return joinSessionUsingAlternateFacultyConfiguration2(student,session,session.getAltMeetingKey(),
						session.getAltMeetingPwd(),session.getAltFacultyId(), "N");
			}
			break;
		case "ALTFACULTYID2":
			availableSeats = facultyIdAndRemSeatsMap.get(session.getAltFacultyId2());
			if (availableSeats > 0) {
				return joinSessionUsingAlternateFacultyConfiguration2(student, session, session.getAltMeetingKey2(),
						session.getAltMeetingPwd2(), session.getAltFacultyId2(), "N");
			}
			break;
		case "ALTFACULTYID3":
			availableSeats = facultyIdAndRemSeatsMap.get(session.getAltFacultyId3());
			if (availableSeats > 0) {
				return joinSessionUsingAlternateFacultyConfiguration2(student, session, session.getAltMeetingKey3(),
						session.getAltMeetingPwd3(), session.getAltFacultyId3(), "N");
			}
			break;
		}

		// If it reached here, then it means seats are not available
		HashMap<String, String> responseObj = new HashMap<>();
		responseObj.put("status", "error");
		responseObj.put("message", "Class is full for chosen faculty. Please join another faculty session");
		return responseObj;
	}
	
	private HashMap<String, String> joinSessionUsingAlternateFacultyConfiguration2(StudentAcadsBean student, SessionDayTimeAcadsBean session,
			String meetingKey, String meetingPassWord, String facultyId, String isReAttended) {
		session.setMeetingKey(meetingKey);
		session.setMeetingPwd(meetingPassWord);
		
		return joinSessionForMobile(student, session, facultyId, isReAttended);
	}

	private ModelAndView joinSession(StudentAcadsBean student, SessionDayTimeAcadsBean session, String facultyId, 
										String isReAttended, HttpServletRequest request) {
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
			tempurl = zoomManger.registrantsForWebinar(session, student, session.getMeetingKey());
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
//		modelnView.addObject("name", name);
//		modelnView.addObject("email", email);
//		modelnView.addObject("mobile", mobile);
//		modelnView.addObject("WEB_EX_API_URL", WEB_EX_API_URL);
		modelnView.addObject("tempurl", tempurl);

		return modelnView;
	}

	private HashMap<String, String> joinSessionForMobile(StudentAcadsBean student, SessionDayTimeAcadsBean session, String facultyId, String isReAttended) {

		String firstName = "";
		String lastName = "";
		String emailId = "";

		HashMap<String, String> tempurl = new HashMap<String, String>();
		if (student != null) {
			firstName = !StringUtils.isBlank(student.getFirstName()) ? student.getFirstName() : ".";
			lastName = !StringUtils.isBlank(student.getLastName()) ? student.getLastName() : ".";
			emailId = !StringUtils.isBlank(student.getEmailId()) ? student.getEmailId() : firstName+"postmaster@nmims.edu";
			
			student.setFirstName(firstName);
			student.setLastName(lastName);
			student.setEmailId(emailId);
		} else {
			return tempurl;
		}

		try {
			tempurl = zoomManger.registerForSessiongByMobile(session, student, session.getMeetingKey());
		} catch (IOException e) {
			  
			tempurl.put("status", "error");
			return tempurl;
		}
		
		if (!tempurl.isEmpty()) {
			try {
				attendanceFeedbackDAO.recordAttendance(session, student.getSapid(), facultyId, tempurl.get("join_url"));
				if (!isReAttended.equalsIgnoreCase("Y")) {
					attendanceFeedbackDAO.updateSessionAttendanceCounter(session.getMeetingKey());
				}
			} catch (Exception e) {
				  
			}
		} else {
			tempurl.put("status", "error");
			tempurl.put("message", "Error while connecting to the zoom");
		}

		/*
		 * ModelAndView modelnView = new ModelAndView("sessionRedirect");
		 * modelnView.addObject("joinFor", joinFor); modelnView.addObject("tempurl", tempurl);
		 */

		return tempurl;
	}

	@RequestMapping(value = "/joinFullMeeting", method = { RequestMethod.GET, RequestMethod.POST })
	public ModelAndView joinFullMeeting(HttpServletRequest request, HttpServletResponse response,
			@ModelAttribute SessionDayTimeAcadsBean session) throws Exception {

		if (!checkSession(request, response)) {
			return new ModelAndView("studentPortalRediret");
		}

		String name = "Coordinator";
		String email = "notavailable@mail.com";
		String mobile = "0000000";

		StudentAcadsBean student = (StudentAcadsBean) request.getSession().getAttribute("student_acads");

		if (student != null) {
			name = student.getFirstName() + " " + student.getLastName();
			email = student.getEmailId() != null ? student.getEmailId() : "notavailable@mail.com";
			mobile = student.getMobile() != null ? student.getMobile() : "0000000";
		}

		// Dont record attendance when student tries his luck for full meeting. He may
		// not get inside session
		// attendanceFeedbackDAO.recordAttendance(session, userId, facultyId);

		ModelAndView modelnView = new ModelAndView("sessionRedirect");
		modelnView.addObject("session", session);
		modelnView.addObject("name", name);
		modelnView.addObject("email", email);
		modelnView.addObject("mobile", mobile);
		modelnView.addObject("WEB_EX_API_URL", WEB_EX_API_URL);

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

	@RequestMapping(value = "/searchAttendanceFeedbackForm", method = { RequestMethod.GET, RequestMethod.POST })
	public String searchAttendanceFeedbackForm(HttpServletRequest request, HttpServletResponse respnse, Model m) {

		SessionAttendanceFeedbackAcads attendanceFeedback = new SessionAttendanceFeedbackAcads();
		FacultyDAO dao = (FacultyDAO) act.getBean("facultyDAO");
		Map<String, String> facultyIdMap = dao.getFacultyMap();
		request.getSession().setAttribute("facultyIdMap", facultyIdMap);
		m.addAttribute("searchBean", attendanceFeedback);
		m.addAttribute("yearList", ACAD_YEAR_LIST);
		m.addAttribute("facultyIdMap", facultyIdMap);

		return "searchAttendanceFeedback";
	}

	@RequestMapping(value = "/searchAttendanceFeedback", method = RequestMethod.POST)
	public ModelAndView searchAttendanceFeedback(HttpServletRequest request, HttpServletResponse response,
			@ModelAttribute SessionAttendanceFeedbackAcads searchBean) {
		ModelAndView modelnView = new ModelAndView("searchAttendanceFeedback");

		request.getSession().setAttribute("searchBean_acads", searchBean);
		PageAcads<SessionAttendanceFeedbackAcads> page = new PageAcads<SessionAttendanceFeedbackAcads>();

//		if ((searchBean.getYear().equalsIgnoreCase(CURRENT_ACAD_YEAR) && searchBean.getMonth().equalsIgnoreCase(CURRENT_ACAD_MONTH))
//				|| (searchBean.getYear().equalsIgnoreCase("2021") && (searchBean.getMonth().equalsIgnoreCase("Jan") || searchBean.getMonth().equalsIgnoreCase("Apr")))
//				|| (searchBean.getYear().equalsIgnoreCase("2020") && (searchBean.getMonth().equalsIgnoreCase("Jul") || searchBean.getMonth().equalsIgnoreCase("Oct")))) {
//			page = attendanceService.getAttendance(1, Integer.MAX_VALUE, searchBean, getAuthorizedCodes(request));
//		}else {
//			page = attendanceService.getAttendanceFromHistory(1, Integer.MAX_VALUE, searchBean, getAuthorizedCodes(request));
//		}
		
//		page=attendanceService.getAttendanceDetails(searchBean, CURRENT_ACAD_YEAR, CURRENT_ACAD_MONTH, getAuthorizedCodes(request));

//		List<SessionAttendanceFeedback> attendanceList = page.getPageItems();
		List<SessionAttendanceFeedbackAcads> attendanceList = attendanceService.getAttendanceDetailsNew(searchBean, getAuthorizedCodes(request));
		


		modelnView.addObject("page", page);
		/* modelnView.addObject("rowCount", page.getRowCount()); */
		modelnView.addObject("searchBean", searchBean);
		modelnView.addObject("yearList", ACAD_YEAR_LIST);

		if (attendanceList == null || attendanceList.size() == 0) {
			request.setAttribute("error", "true");
			request.setAttribute("errorMessage", "No Attendance Records Found.");
		}else {
			request.getSession().setAttribute("attendanceList", attendanceList);
			/* modelnView.addObject("attendanceList", attendanceList); */
			

			try {
				List<SessionAttendanceFeedbackAcads> getSubjectFacultyWiseAverage = new ArrayList<SessionAttendanceFeedbackAcads>();
				HashMap<String, SessionAttendanceFeedbackAcads> mapOfSubjectFacultySessionWiseAverage = new HashMap<String, SessionAttendanceFeedbackAcads>();
//				if ((searchBean.getYear().equalsIgnoreCase(CURRENT_ACAD_YEAR) && searchBean.getMonth().equalsIgnoreCase(CURRENT_ACAD_MONTH))
//						|| (searchBean.getYear().equalsIgnoreCase("2021") && (searchBean.getMonth().equalsIgnoreCase("Jan") || searchBean.getMonth().equalsIgnoreCase("Apr")))
//						|| (searchBean.getYear().equalsIgnoreCase("2020") && (searchBean.getMonth().equalsIgnoreCase("Jul") || searchBean.getMonth().equalsIgnoreCase("Oct")))) {
//					getSubjectFacultyWiseAverage = attendanceService.getSubjectFacultyWiseAverage(searchBean);
//					mapOfSubjectFacultySessionWiseAverage = attendanceService.getMapOfSubjectFacultySessionWiseAverage(searchBean,request);
//				}else {
//					getSubjectFacultyWiseAverage = attendanceService.getSubjectFacultyWiseAverageFromHistory(searchBean);
//					mapOfSubjectFacultySessionWiseAverage = attendanceService.getMapOfSubjectFacultySessionWiseAverageFromHistory(searchBean, request);
//				}
				
				getSubjectFacultyWiseAverage=attendanceService.getListOfSubjectFacultyWiseFeedbackAverage(searchBean, CURRENT_ACAD_YEAR, CURRENT_ACAD_MONTH, request);
				mapOfSubjectFacultySessionWiseAverage=attendanceService.getMapOfSubjectFacultySessionWiseAverage(searchBean, CURRENT_ACAD_YEAR, CURRENT_ACAD_MONTH, request);
				
				modelnView.addObject("rowCount", getSubjectFacultyWiseAverage.size());
				modelnView.addObject("getSubjectFacultyWiseAverage", getSubjectFacultyWiseAverage);
				modelnView.addObject("mapOfSubjectFacultySessionWiseAverage", mapOfSubjectFacultySessionWiseAverage);

				if (getSubjectFacultyWiseAverage.isEmpty()) {
					setError(request, "No Record Found");
				}
				request.getSession().setAttribute("getSubjectFacultyWiseAverage", getSubjectFacultyWiseAverage);
				request.getSession().setAttribute("mapOfSubjectFacultySessionWiseAverage", mapOfSubjectFacultySessionWiseAverage);

			} catch (Exception e) {
				  
				setError(request, "Error Occurse While retriving Details " + e.getMessage());
				return searchAttendanceFeedback(request, response, searchBean);
			}
		}
		
		return modelnView;
	}
	
	@RequestMapping(value = "/downloadAttendanceFeedback", method = { RequestMethod.GET, RequestMethod.POST })
	public ModelAndView downloadAttendanceFeedback(HttpServletRequest request, HttpServletResponse response) {
		
		if (!checkSession(request, response)) {
			return new ModelAndView("login");
		}
		
		request.getSession().setAttribute("ICLCRESTRICTED_USER_LIST", ICLCRESTRICTED_USER_LIST);
		SessionAttendanceFeedbackAcads searchBean = (SessionAttendanceFeedbackAcads) request.getSession().getAttribute("searchBean_acads");
		List<SessionAttendanceFeedbackAcads> attendanceList = (ArrayList<SessionAttendanceFeedbackAcads>) request.getSession().getAttribute("attendanceList");
		UserAuthorizationBean userAuthorization = (UserAuthorizationBean) request.getSession().getAttribute("userAuthorization");
		String roles = "";
		if (userAuthorization != null) {
			roles = (userAuthorization.getRoles() != null && !"".equals(userAuthorization.getRoles()))
					? userAuthorization.getRoles()
					: roles;
		}
		// Calulate Session Wise Average FeedBack
		HashMap<String, SessionAttendanceFeedbackAcads> mapOfSessionIdAndFeedBackBean = new HashMap<String, SessionAttendanceFeedbackAcads>();
		
		// Calculate Subject ,Faculty , Session wise Average Feedback
		HashMap<String, SessionAttendanceFeedbackAcads> mapOfSubjectFacultySessionWiseAverage = new HashMap<String, SessionAttendanceFeedbackAcads>();		
//		if ((searchBean.getYear().equalsIgnoreCase(CURRENT_ACAD_YEAR) && searchBean.getMonth().equalsIgnoreCase(CURRENT_ACAD_MONTH))
//				|| (searchBean.getYear().equalsIgnoreCase("2021") && (searchBean.getMonth().equalsIgnoreCase("Jan") || searchBean.getMonth().equalsIgnoreCase("Apr")))
//				|| (searchBean.getYear().equalsIgnoreCase("2020") && (searchBean.getMonth().equalsIgnoreCase("Jul") || searchBean.getMonth().equalsIgnoreCase("Oct")))) {
//			mapOfSessionIdAndFeedBackBean = attendanceService.getMapOfSessionIdAndFeedBackBean();
//			mapOfSubjectFacultySessionWiseAverage = attendanceService .getMapOfSubjectFacultySessionWiseAverage(searchBean, request);
//		}else {
//			mapOfSessionIdAndFeedBackBean = attendanceService.getMapOfSessionIdAndFeedBackBeanFromHistory();
//			mapOfSubjectFacultySessionWiseAverage = attendanceService .getMapOfSubjectFacultySessionWiseAverageFromHistory(searchBean, request);
//		}
		
		mapOfSessionIdAndFeedBackBean=attendanceService.getMapOfSessionIdAndFeedBackBean(searchBean, CURRENT_ACAD_YEAR, CURRENT_ACAD_MONTH, request);
		mapOfSubjectFacultySessionWiseAverage=attendanceService.getMapOfSubjectFacultySessionWiseAverage(searchBean, CURRENT_ACAD_YEAR, CURRENT_ACAD_MONTH, request);
		
		@SuppressWarnings("unchecked")
		ArrayList<SessionAttendanceFeedbackAcads>sessionList=(ArrayList<SessionAttendanceFeedbackAcads>) request.getSession().getAttribute("sessionList");


		HashMap<String, SessionAttendanceFeedbackAcads> mapOfSessionWiseAttendaceCount=new HashMap<>();
		mapOfSessionWiseAttendaceCount=attendanceService.getMapOfSessionWiseAttendaceCount(searchBean, sessionList, studentService.getListOfLiveSessionAccessMasterKeys(TIMEBOUND_PORTAL_LIST));
		
		request.setAttribute("roles", roles);
		request.setAttribute("mapOfSessionIdAndFeedBackBean", mapOfSessionIdAndFeedBackBean);
		request.setAttribute("mapOfSubjectFacultySessionWiseAverage", mapOfSubjectFacultySessionWiseAverage);
		request.setAttribute("mapOfSessionWiseAttendaceCount", mapOfSessionWiseAttendaceCount);
		return new ModelAndView("attendanceFeedbackExcelView", "attendanceList", attendanceList);
	}



	@RequestMapping(value = "/viewFacultyFeedback", method = { RequestMethod.GET, RequestMethod.POST })
	public String viewFacultyFeedback(HttpServletRequest request, HttpServletResponse respnse, Model m) {

		String id = request.getParameter("id");
		TimeTableDAO dao = (TimeTableDAO) act.getBean("timeTableDAO");
		SessionDayTimeAcadsBean session = dao.findScheduledSessionById(id);
		m.addAttribute("session", session);
		String userIdOfFaculty = (String) request.getSession().getAttribute("userId_acads");
		List<SessionAttendanceFeedbackAcads> facultyFeedbackList = attendanceFeedbackDAO.getFacultyFeedbackForSession(id,
				userIdOfFaculty);
		m.addAttribute("facultyFeedbackList", facultyFeedbackList);
		m.addAttribute("rowCount", facultyFeedbackList != null ? facultyFeedbackList.size() + "" : "0");

		double q1Average = 0.0;
		double q2Average = 0.0;
		double q3Average = 0.0;
		double q4Average = 0.0;
		double q5Average = 0.0;
		double q6Average = 0.0;
		double q7Average = 0.0;
		double q8Average = 0.0;

		if (facultyFeedbackList != null && facultyFeedbackList.size() > 0) {
			for (SessionAttendanceFeedbackAcads sessionAttendanceFeedback : facultyFeedbackList) {
				q1Average += Integer.parseInt(!StringUtils.isEmpty(sessionAttendanceFeedback.getQ1Response())
						? sessionAttendanceFeedback.getQ1Response()
						: "0");
				q2Average += Integer.parseInt(!StringUtils.isEmpty(sessionAttendanceFeedback.getQ2Response())
						? sessionAttendanceFeedback.getQ2Response()
						: "0");
				q3Average += Integer.parseInt(!StringUtils.isEmpty(sessionAttendanceFeedback.getQ3Response())
						? sessionAttendanceFeedback.getQ3Response()
						: "0");
				q4Average += Integer.parseInt(!StringUtils.isEmpty(sessionAttendanceFeedback.getQ4Response())
						? sessionAttendanceFeedback.getQ4Response()
						: "0");
				q5Average += Integer.parseInt(!StringUtils.isEmpty(sessionAttendanceFeedback.getQ5Response())
						? sessionAttendanceFeedback.getQ5Response()
						: "0");
				q6Average += Integer.parseInt(!StringUtils.isEmpty(sessionAttendanceFeedback.getQ6Response())
						? sessionAttendanceFeedback.getQ6Response()
						: "0");
				q7Average += Integer.parseInt(!StringUtils.isEmpty(sessionAttendanceFeedback.getQ7Response())
						? sessionAttendanceFeedback.getQ7Response()
						: "0");
				q8Average += Integer.parseInt(!StringUtils.isEmpty(sessionAttendanceFeedback.getQ8Response())
						? sessionAttendanceFeedback.getQ8Response()
						: "0");
			}

			q1Average = q1Average / facultyFeedbackList.size();
			q2Average = q2Average / facultyFeedbackList.size();
			q3Average = q3Average / facultyFeedbackList.size();
			q4Average = q4Average / facultyFeedbackList.size();
			q5Average = q5Average / facultyFeedbackList.size();
			q6Average = q6Average / facultyFeedbackList.size();
			q7Average = q7Average / facultyFeedbackList.size();
			q8Average = q8Average / facultyFeedbackList.size();

			m.addAttribute("q1Average", q1Average);
			m.addAttribute("q2Average", q2Average);
			m.addAttribute("q3Average", q3Average);
			m.addAttribute("q4Average", q4Average);
			m.addAttribute("q5Average", q5Average);
			m.addAttribute("q6Average", q6Average);
			m.addAttribute("q7Average", q7Average);
			m.addAttribute("q8Average", q8Average);
		} else {
			setError(request, "No Feedback Records available for this session yet.");
		}

		return "searchFacultyFeedback";
	}
	/*
	 * added by stef on 6-Nov
	 * 
	 * 
	 * 
	 * @RequestMapping(value = "/studentAttendanceReport", method =
	 * {RequestMethod.GET ,RequestMethod.POST}) public ModelAndView
	 * studentAttendanceReport(HttpServletRequest request, HttpServletResponse
	 * response, @ModelAttribute SessionAttendanceFeedback searchBean){
	 * 
	 * ModelAndView modelnView = new ModelAndView("studentAttendanceReport");
	 * 
	 * request.getSession().setAttribute("searchBean_acads", searchBean);
	 * 
	 * 
	 * modelnView.addObject("searchBean", searchBean);
	 * modelnView.addObject("yearList", yearList);
	 * modelnView.addObject("subjectList", subjectList);
	 * modelnView.addObject("sessionList",sessionList); return modelnView;
	 * 
	 * }
	 * 
	 * 
	 * @RequestMapping(value = "/downloadStudentAttendance", method =
	 * {RequestMethod.GET, RequestMethod.POST}) public ModelAndView
	 * downloadStudentAttendance(HttpServletRequest request, HttpServletResponse
	 * response,@ModelAttribute SessionAttendanceFeedback searchBean) {
	 * 
	 * request.getSession().getAttribute("searchBean_acads");
	 * Page<SessionAttendanceFeedback> page = attendanceFeedbackDAO.getAttendance(1,
	 * Integer.MAX_VALUE, searchBean,getAuthorizedCodes(request));
	 * List<SessionAttendanceFeedback> attendanceList = page.getPageItems();
	 * List<SessionAttendanceFeedback> attendanceList =
	 * (ArrayList<SessionAttendanceFeedback>)request.getSession().getAttribute(
	 * "attendanceList");
	 * 
	 * UserAuthorizationBean userAuthorization =
	 * (UserAuthorizationBean)request.getSession().getAttribute("userAuthorization")
	 * ; String roles=""; if(userAuthorization != null){ roles =
	 * (userAuthorization.getRoles() != null &&
	 * !"".equals(userAuthorization.getRoles())) ? userAuthorization.getRoles() :
	 * roles; }
	 * 
	 * request.setAttribute("roles",roles);
	 * request.getSession().setAttribute("searchBean_acads", searchBean);
	 * 
	 * return new
	 * ModelAndView("attendanceFeedbackExcelView","attendanceList",attendanceList);
	 * }
	 */

	public static void main(String[] args) {
		String name = "Session 1 (Repeat)";
		String sessionName = name.substring(0, name.indexOf(" (Repeat)"));
	}
	

//	@CrossOrigin(origins="*", allowedHeaders="*")
//	//temporary for mobile to be deleted later start
//	@RequestMapping(value = "/m/attendScheduledSessionForReact", method = RequestMethod.POST, consumes = "application/json",produces = "application/json")
//	public ResponseEntity<HashMap<String,String>> m_attendScheduledSessionForReact(HttpServletRequest request, HttpServletResponse response,@RequestBody SessionDayTimeBean session) throws Exception{
//		ResponseEntity<HashMap<String,String>> resp = zoomSessionJoin(request,session);
//		return resp;
//	}
	
	//temporary for mobile to be deleted later start
//	public ResponseEntity<HashMap<String, String>> zoomSessionJoin(HttpServletRequest request,SessionDayTimeBean sessionFromPage) {
//		HttpHeaders headers = new HttpHeaders();
//		headers.add("Content-Type", "application/json");
//
//		//String forMobile = request.getParameter("forMobile");
//		StudentBean student = null;
//		SessionAttendanceFeedback attendance = new SessionAttendanceFeedback();
//		TimeTableDAO dao = (TimeTableDAO) act.getBean("timeTableDAO");
//		HashMap<String, String> responseObj = new HashMap<>();
//		String isReAttended = "N";
//
//		if ("true".equalsIgnoreCase(sessionFromPage.getForMobile())) {
//			// Called from Mobile App, Student details not available in session
//			String sapid = sessionFromPage.getSapId();
//			String email = sessionFromPage.getEmail();
//			String firstName = sessionFromPage.getFirstName();
//			String lastName = sessionFromPage.getLastName();
//			String mobile = sessionFromPage.getMobile();
//
//			student = new StudentBean();
//			student.setSapid(sapid);
//			student.setEmailId(email);
//			student.setMobile(mobile);
//			student.setFirstName(firstName);
//			student.setLastName(lastName);
//			attendance.setDevice("MobileApp");
//
//		} else {
//			// Called via Desktop
////			if(!checkSession(request, response)){
////				return new ModelAndView("studentPortalRediret");
////			}
//
////			student = (StudentBean)request.getSession().getAttribute("student_acads");//Take from session
//
//			String sapid = sessionFromPage.getSapId();
//			String email = sessionFromPage.getEmail();
//			String firstName = sessionFromPage.getFirstName();
//			String lastName = sessionFromPage.getLastName();
//			String mobile = sessionFromPage.getMobile();
//
//			student = new StudentBean();
//			student.setSapid(sapid);
//			student.setEmailId(email);
//			student.setMobile(mobile);
//			student.setFirstName(firstName);
//			student.setLastName(lastName);
//			attendance.setDevice("WebApp");
//
//		}
//
//		String id = sessionFromPage.getId();
//		String joinFor = request.getParameter("joinFor");
//
//		/* String joinForParameter = request.getParameter("joinFor"); */
//		SessionDayTimeBean session = dao.findScheduledSessionById(id);
//
//		session.setDevice(attendance.getDevice());
//		attendance = dao.checkSessionAttendance(student.getSapid(), id);
//		if (attendance != null) {
//			//Student has already joined session. Send him to same session again.
//			HashMap<String, String> joinurl = reAttendSessionMobile(attendance.getJoinurl());
//			return new ResponseEntity<HashMap<String,String>>(joinurl, headers, HttpStatus.OK);
//		}
//
//		/*
//		 * if(attendance != null){ //Student has already joined session. Send him to
//		 * same session again. return
//		 * joinSessionUsingAlternateFacultyConfiguration(student,session,attendance.
//		 * getMeetingKey(),attendance.getMeetingPwd(),attendance.getFacultyId()); }
//		 * 
//		 * if(!"ANY".equalsIgnoreCase(joinFor)){ //Student wants to join specific
//		 * faculty session return joinPreferredFacultySession(joinFor, student, session,
//		 * request); }
//		 */
//
//		boolean hasParallelAlternateSession = false;
//		boolean hasParallelAlternateSession2 = false;
//		boolean hasParallelAlternateSession3 = false;
//
//		if (!StringUtils.isBlank(session.getAltMeetingKey())) {
//			hasParallelAlternateSession = true;
//		}
//		if (!StringUtils.isBlank(session.getAltMeetingKey2())) {
//			hasParallelAlternateSession2 = true;
//		}
//		if (!StringUtils.isBlank(session.getAltMeetingKey3())) {
//			hasParallelAlternateSession3 = true;
//		}
//
//		int noOfUsersJoined = dao.findUsersJoined(id);
//
//		boolean originalSessionAvailable = noOfUsersJoined < MAX_WEBEX_USERS;
//		boolean firstAlternateSessionAvailable = hasParallelAlternateSession
//				&& (noOfUsersJoined < (2 * MAX_WEBEX_USERS));
//		boolean secondAlternateSessionAvailable = hasParallelAlternateSession2
//				&& (noOfUsersJoined < (3 * MAX_WEBEX_USERS));
//		boolean thirdAlternateSessionAvailable = hasParallelAlternateSession3
//				&& (noOfUsersJoined < (4 * MAX_WEBEX_USERS));
//
//		if (originalSessionAvailable) {
//			HashMap<String, String> joinurl = joinSessionForMobile(student, session, session.getFacultyId(), isReAttended);
//			return new ResponseEntity<HashMap<String, String>>(joinurl, headers, HttpStatus.OK);
//		}
//		return new ResponseEntity<HashMap<String, String>>(responseObj, headers, HttpStatus.CONFLICT);
//	}
	
		// temporary for mobile to be deleted later start
//		@RequestMapping(value = "/m/attendScheduledSessionold", method = RequestMethod.GET, consumes = "application/json",produces = "application/json")
//		public ResponseEntity<HashMap<String,String>> m_attendScheduledSessionOld(HttpServletRequest request, HttpServletResponse response) throws Exception{
//			HttpHeaders headers = new HttpHeaders();
//			headers.add("Content-Type", "application/json");
//			
//			String forMobile = request.getParameter("forMobile");
//			StudentBean student = null;
//			SessionAttendanceFeedback attendance = new SessionAttendanceFeedback();
//			TimeTableDAO dao = (TimeTableDAO)act.getBean("timeTableDAO");
//			HashMap<String,String> responseObj = new HashMap<>();
//			
//			if("true".equalsIgnoreCase(forMobile)) {
//				//Called from Mobile App, Student details not available in session
//				String sapid = request.getParameter("sapid");
//				String email = request.getParameter("email");
//				String firstName = request.getParameter("firstName");
//				String lastName = request.getParameter("lastName");
//				String mobile = request.getParameter("mobile");
//				
//				student = new StudentBean();
//				student.setSapid(sapid);
//				student.setEmailId(email);
//				student.setMobile(mobile);
//				student.setFirstName(firstName);
//				student.setLastName(lastName);
//				attendance.setDevice("MobileApp");
//
//			}
//
//			String id = request.getParameter("id");
//			String joinFor = request.getParameter("joinFor");
//			
//			/*String joinForParameter = request.getParameter("joinFor"); */
//			SessionDayTimeBean session = dao.findScheduledSessionById(id);
//
//			session.setDevice(attendance.getDevice());
//			attendance = dao.checkSessionAttendance(student.getSapid(), id);
//			
//			if(attendance != null){
//				//Student has already joined session. Send him to same session again.
//				HashMap<String, String> joinurl = joinSessionUsingAlternateFacultyConfiguration2(student,session,attendance.getMeetingKey(),
//													attendance.getMeetingPwd(),attendance.getFacultyId(), "Y");
//				return new ResponseEntity<HashMap<String,String>>(joinurl, headers, HttpStatus.OK);
//			}
//			
//			if(!"ANY".equalsIgnoreCase(joinFor)){
//				
//				// If Orientation Session Do Not check limit
//				if (session.getSubject().equalsIgnoreCase("Orientation")) {
//					HashMap<String, String> joinurl = joinSessionForMobile(student, session,  session.getFacultyId(), "N");
//					return new ResponseEntity<HashMap<String,String>>(joinurl, headers, HttpStatus.OK);
//				}
//				
//				//Student wants to join specific faculty session
//				HashMap<String, String> joinurl = joinPreferredFacultySessionForMobile(joinFor, student, session);
//				return new ResponseEntity<HashMap<String,String>>(joinurl, headers, HttpStatus.OK);
//			}
//
//			boolean hasParallelAlternateSession = false;
//			boolean hasParallelAlternateSession2 = false;
//			boolean hasParallelAlternateSession3 = false;
//
//			if(!StringUtils.isBlank(session.getAltMeetingKey())){
//				hasParallelAlternateSession = true;
//			}
//			if(!StringUtils.isBlank(session.getAltMeetingKey2())){
//				hasParallelAlternateSession2 = true;
//			}
//			if(!StringUtils.isBlank(session.getAltMeetingKey3())){
//				hasParallelAlternateSession3 = true;
//			}
//
//			int noOfUsersJoined = dao.findUsersJoined(id);
//			
//			boolean originalSessionAvailable = noOfUsersJoined < MAX_WEBEX_USERS;
//			boolean firstAlternateSessionAvailable = hasParallelAlternateSession && (noOfUsersJoined < (2 * MAX_WEBEX_USERS) );
//			boolean secondAlternateSessionAvailable = hasParallelAlternateSession2 && (noOfUsersJoined < (3 * MAX_WEBEX_USERS) );
//			boolean thirdAlternateSessionAvailable = hasParallelAlternateSession3 && (noOfUsersJoined < (4 * MAX_WEBEX_USERS) );
//
//			if(originalSessionAvailable){
//				HashMap<String, String> joinurl = joinSessionForMobile(student, session, session.getFacultyId(), "N");
//				return new ResponseEntity<HashMap<String,String>>(joinurl, headers, HttpStatus.OK);
//				
//			}else if(firstAlternateSessionAvailable){
//				HashMap<String, String> joinurl = joinSessionUsingAlternateFacultyConfiguration2(student,session,session.getAltMeetingKey(),
//						session.getAltMeetingPwd(),session.getAltFacultyId(), "N");
//				return new ResponseEntity<HashMap<String,String>>(joinurl, headers, HttpStatus.OK);
//				
//			}else if(secondAlternateSessionAvailable) {
//				HashMap<String, String> joinurl = joinSessionUsingAlternateFacultyConfiguration2(student,session,session.getAltMeetingKey2(),
//						session.getAltMeetingPwd2(),session.getAltFacultyId2(), "N");
//				return new ResponseEntity<HashMap<String,String>>(joinurl, headers, HttpStatus.OK);
//				
//			}else if(thirdAlternateSessionAvailable) {
//				HashMap<String, String> joinurl = joinSessionUsingAlternateFacultyConfiguration2(student,session,session.getAltMeetingKey3(),
//						session.getAltMeetingPwd3(),session.getAltFacultyId3(), "N");
//				return new ResponseEntity<HashMap<String,String>>(joinurl, headers, HttpStatus.OK);
//				
//			}else {
//				// All Classes Full, Divide any new students among running sessions now for
//				// additional buffer capacity
//				SessionAttendanceFeedback sessionWithLeastAttendance = dao.getSessionWithLeastNumberOfAttendees(id);
//				if ((sessionWithLeastAttendance.getNumberOfAttendees() - MAX_WEBEX_USERS) < SESSION_ATTENDANCE_BUFFER) {
//					HashMap<String, String> joinurl = joinSessionUsingAlternateFacultyConfiguration2(student, session,
//							sessionWithLeastAttendance.getMeetingKey(), sessionWithLeastAttendance.getMeetingPwd(),
//							sessionWithLeastAttendance.getFacultyId(), "N");
//					return new ResponseEntity<HashMap<String,String>>(joinurl, headers, HttpStatus.OK);
//				}else {
//					HashMap<String, String> responseData = new HashMap<>();
//					responseObj.put("status", "error");
//					return new ResponseEntity<HashMap<String,String>>(responseData, headers, HttpStatus.OK);
//				}
//			}
//
////			return new ResponseEntity<HashMap<String,String>>(responseObj,headers, HttpStatus.CONFLICT);
//			
//			//return null;
//
//			/*if((noOfUsersJoined >= MAX_WEBEX_USERS)){//Not joined yet but class is full
//
//				//boolean hasParallelAlternateSession = dao.checkIfHasParallelAlternateSession(id,"ALT1");//ALT1,ALT2,ALT3  :- alternate faculty 1,2,3 and so on
//
//
//
//
//				if(!hasParallelAlternateSession){//If no alternate session then show class full
//					return classFull(session);
//				}else if(hasParallelAlternateSession && (noOfUsersJoined >= ((2 *MAX_WEBEX_USERS) + 25))){
//					//Has parallel session but that is also Full
//
//					if(!hasParallelAlternateSession2){//If no alternate session 2 then show class full
//						return classFull(session);
//					}else if(hasParallelAlternateSession2 && (noOfUsersJoined >= ((3 *MAX_WEBEX_USERS) + 50))){
//						//Has 2nd parallel session but that is also Full
//						if(!hasParallelAlternateSession3){//If no alternate session 3 then show class full
//							return classFull(session);
//						}else if(hasParallelAlternateSession3 && (noOfUsersJoined >= ((4 *MAX_WEBEX_USERS) + 75))){
//							//Has 3rd parallel session but that is also Full
//							return classFull(session);
//						}else{
//							//3rd parallel session is not yet full, allow to join
//							return joinSessionUsingAlternateFacultyConfiguration(student,userId,session,session.getAltMeetingKey3(),session.getAltMeetingPwd3(),session.getAltFacultyId3()); //For Third threshold//
//						}
//					}else{
//						//2nd parallel session is not yet full, allow to join
//						return joinSessionUsingAlternateFacultyConfiguration(student,userId,session,session.getAltMeetingKey2(),session.getAltMeetingPwd2(),session.getAltFacultyId2()); //For Second threshold//
//
//					}
//				}else{
//					//Has 2nd parallel session, which is not yet full
//					return joinSessionUsingAlternateFacultyConfiguration(student,userId,session,session.getAltMeetingKey(),session.getAltMeetingPwd(),session.getAltFacultyId()); //For First threshold//
//				}
//			}else if(attendedPreviousSession){
//				//Attended previous session
//				ModelAndView modelnView = new ModelAndView("alreadyAttended"); 
//				return modelnView;
//			}else{
//				return joinSession(student, session, userId, session.getFacultyId());//Didn't join session yet and class is not yet full
//			}*/
//
//
//
//			//return new ModelAndView("redirect:" + joinUrl);
//		}
//
//		//end
		@RequestMapping(value = "/searchAttendanceFeedbackForMbaWxForm", method = { RequestMethod.GET, RequestMethod.POST })
		public String searchAttendanceFeedbackForMbaWxForm(HttpServletRequest request, HttpServletResponse respnse, Model m) {

			SessionAttendanceFeedbackAcads attendanceFeedback = new SessionAttendanceFeedbackAcads();
			FacultyDAO dao = (FacultyDAO) act.getBean("facultyDAO");
			Map<String, String> facultyIdMap = dao.getFacultyMap();
			request.getSession().setAttribute("facultyIdMap", facultyIdMap);
			m.addAttribute("searchBean", attendanceFeedback);
			m.addAttribute("yearList", ACAD_YEAR_LIST);			
			m.addAttribute("acadsMonthList", ACAD_MONTH_LIST);
			
			m.addAttribute("facultyIdMap", facultyIdMap);

			return "searchAttendanceFeedbackForMbaWx";
		}

		@RequestMapping(value = "/searchAttendanceFeedbackForMbaWx", method = RequestMethod.POST)
		public ModelAndView searchAttendanceFeedbackForMbaWx(HttpServletRequest request, HttpServletResponse response,
				@ModelAttribute SessionAttendanceFeedbackAcads searchBean) {
			
			ModelAndView modelnView = new ModelAndView("searchAttendanceFeedbackForMbaWx");
			
			request.getSession().setAttribute("searchBean_acads", searchBean);

			PageAcads<SessionAttendanceFeedbackAcads> page = attendanceFeedbackDAO.getAttendanceForMbaWx(1, Integer.MAX_VALUE, searchBean,
					getAuthorizedCodes(request));
			List<SessionAttendanceFeedbackAcads> sessionattendanceList = page.getPageItems();

			modelnView.addObject("page", page);
			/* modelnView.addObject("rowCount", page.getRowCount()); */
			modelnView.addObject("searchBean", searchBean);

			if (sessionattendanceList == null || sessionattendanceList.size() == 0) {
				request.setAttribute("error", "true");
				request.setAttribute("errorMessage", "No Session Attendance For MBA wx Records Found.");
			}
			request.getSession().setAttribute("attendanceForMbaWxList", sessionattendanceList);
			/* modelnView.addObject("attendanceList", attendanceList); */
			modelnView.addObject("yearList", ACAD_YEAR_LIST);
			modelnView.addObject("acadsMonthList", ACAD_MONTH_LIST);

			try {
				List<SessionAttendanceFeedbackAcads> getSubjectFacultyWiseAverage = attendanceFeedbackDAO
						.getSubjectFacultyWiseAverage(searchBean);

				HashMap<String, SessionAttendanceFeedbackAcads> mapOfSubjectFacultySessionWiseAverage = attendanceFeedbackDAO
						.getMapOfSubjectFacultySessionWiseAverage(searchBean, request);

				modelnView.addObject("rowCount", getSubjectFacultyWiseAverage.size());
				modelnView.addObject("getSubjectFacultyWiseAverage", getSubjectFacultyWiseAverage);
				modelnView.addObject("mapOfSubjectFacultySessionWiseAverage", mapOfSubjectFacultySessionWiseAverage);

				if (getSubjectFacultyWiseAverage.isEmpty()) {
					setError(request, "No Record Found");
				}
				request.getSession().setAttribute("getSubjectFacultyWiseAverage", getSubjectFacultyWiseAverage);
				request.getSession().setAttribute("mapOfSubjectFacultySessionWiseAverage",
						mapOfSubjectFacultySessionWiseAverage);

			} catch (Exception e) {
				  
				setError(request, "Error Occurse While retriving Details " + e.getMessage());
				return searchAttendanceFeedback(request, response, searchBean);
			}

			return modelnView;
		}
		
		@RequestMapping(value = "/downloadSessionAttendanceReportForMbaWx", method = { RequestMethod.GET, RequestMethod.POST })
		public ModelAndView downloadSessionAttendanceReportForMbaWx(HttpServletRequest request, HttpServletResponse response) {
			SessionAttendanceFeedbackAcads searchBean = (SessionAttendanceFeedbackAcads) request.getSession().getAttribute("searchBean_acads");
			
			List<SessionAttendanceFeedbackAcads> sessionattendanceList = new ArrayList<SessionAttendanceFeedbackAcads>();
			try {
				sessionattendanceList = (ArrayList<SessionAttendanceFeedbackAcads>) request.getSession()
					.getAttribute("attendanceForMbaWxList");
			}catch(Exception e) {
				  
			}
			UserAuthorizationBean userAuthorization = (UserAuthorizationBean) request.getSession()
					.getAttribute("userAuthorization");
			String roles = "";
			if (userAuthorization != null) {
				roles = (userAuthorization.getRoles() != null && !"".equals(userAuthorization.getRoles()))
						? userAuthorization.getRoles()
						: roles;
			}
			// Calulate Session Wise Average FeedBack
			HashMap<String, SessionAttendanceFeedbackAcads> mapOfSessionIdAndFeedBackBean = attendanceFeedbackDAO
					.getMapOfSessionIdAndFeedBackBean();

			// Calculate Subject ,Faculty , Session wise Average Feedback
			HashMap<String, SessionAttendanceFeedbackAcads> mapOfSubjectFacultySessionWiseAverage = attendanceFeedbackDAO
					.getMapOfSubjectFacultySessionWiseAverage(searchBean, request);

			request.setAttribute("roles", roles);
			request.setAttribute("mapOfSessionIdAndFeedBackBean", mapOfSessionIdAndFeedBackBean);
			request.setAttribute("mapOfSubjectFacultySessionWiseAverage", mapOfSubjectFacultySessionWiseAverage);
			return new ModelAndView("sessionAttendanceForMbaWxExcelView", "sessionattendanceList", sessionattendanceList);
		}
/**
 * 
	Commented by Riya as mapping is shifted in StudentAttendanceFeedbackController
 */

		/*@RequestMapping(value = "/attendScheduledSession", method = { RequestMethod.GET, RequestMethod.POST })
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
			/*	return joinPreferredFacultySessionV2(joinFor, student, session, request);
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
		}*/
		
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
		
		private HashMap<String, String> joinPreferredFacultySessionForMobileV2(String joinFor, StudentAcadsBean student, SessionDayTimeAcadsBean session) {

			TimeTableDAO dao = (TimeTableDAO) act.getBean("timeTableDAO");		
			int availableSeats = 0;
			switch (joinFor) {
			case "HOST":
				availableSeats = dao.getAttendanceByMeetingKey(session.getMeetingKey());
				if (availableSeats > 0) {
					return joinSessionForMobile(student, session, session.getFacultyId(), "N");
				}
				break;
			case "ALTFACULTYID":
				availableSeats = dao.getAttendanceByMeetingKey(session.getAltMeetingKey());
				if (availableSeats > 0) {
					return joinSessionUsingAlternateFacultyConfiguration2(student,session,session.getAltMeetingKey(),
							session.getAltMeetingPwd(),session.getAltFacultyId(), "N");
				}
				break;
			case "ALTFACULTYID2":
				availableSeats = dao.getAttendanceByMeetingKey(session.getAltMeetingKey2());
				if (availableSeats > 0) {
					return joinSessionUsingAlternateFacultyConfiguration2(student, session, session.getAltMeetingKey2(),
							session.getAltMeetingPwd2(), session.getAltFacultyId2(), "N");
				}
				break;
			case "ALTFACULTYID3":
				availableSeats = dao.getAttendanceByMeetingKey(session.getAltMeetingKey3());
				if (availableSeats > 0) {
					return joinSessionUsingAlternateFacultyConfiguration2(student, session, session.getAltMeetingKey3(),
							session.getAltMeetingPwd3(), session.getAltFacultyId3(), "N");
				}
				break;
			}

			// If it reached here, then it means seats are not available
			HashMap<String, String> responseObj = new HashMap<>();
			responseObj.put("status", "error");
			responseObj.put("message", "Class is full. Please join another faculty session");
			return responseObj;
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
			request.getSession().setAttribute("userId",userId);
			StudentAcadsBean student=attendanceFeedbackDAO.getSingleStudentsData(userId);
			request.getSession().setAttribute("student_acads",student);

			SessionAttendanceFeedbackAcads pendingFeedback = new SessionAttendanceFeedbackAcads();
			
			try {
				pendingFeedback = attendanceService.getPostSessionFeedback(userId, sessionId, acadDateFormat);
		
				ModelAndView modelnView = new ModelAndView("postSessionFeedback");
				modelnView.addObject("feedback", pendingFeedback);
				return modelnView;
			}catch (Exception e) {
				// TODO: handle exception
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
				// TODO: handle exception
			return new ModelAndView("studentPortalRediret");
			}
		}
	    
		@GetMapping("/viewSessionParticipantsReportForm")
	 	public ModelAndView viewSessionParticipantsReportForm(HttpServletRequest request) throws Exception{
	 		ModelAndView modelAndView=new ModelAndView("webinarReport");
	 		//List<ParticipantReportBean> sessionAttendanceList= new ArrayList<ParticipantReportBean>();
	 		ParticipantReportBean participantReportBean=new ParticipantReportBean();
	 		
//			Map<String, String> facultyIdMap = attendanceReportService.getFacultyMap();
//	 		request.getSession().setAttribute("facultyIdMap", facultyIdMap);
	 		modelAndView.addObject("rowCount", -1);
	 		modelAndView.addObject("participantReportBean", participantReportBean);
	 		
	 		SessionDayTimeAcadsBean searchBean = new SessionDayTimeAcadsBean();
	 		modelAndView.addObject("searchBean", searchBean);
	 		modelAndView.addObject("yearList", ACAD_YEAR_LIST);
	 		//modelAndView.addObject("facultyIdMap", facultyIdMap);
	 		modelAndView.addObject("subjectCodeMap", getsubjectCodeMap());
	            
	 		return modelAndView;
	 	}
		
		@PostMapping("/viewSessionParticipantsReport")
	 	public ModelAndView viewSessionParticipantsReport(HttpServletRequest request, @ModelAttribute SessionDayTimeAcadsBean searchBean) throws Exception{
	 		ModelAndView modelAndView=new ModelAndView("webinarReport");
	 		
	 		//Map<String, String> facultyIdMap=(Map<String, String>) request.getSession().getAttribute("facultyIdMap");
	 		
	 		List<ParticipantReportBean> sessionAttendanceList= new ArrayList<ParticipantReportBean>();
	 		sessionAttendanceList=attendanceReportService.getReportDetails(searchBean);
	 		
	 		request.getSession().setAttribute("sessionAttendanceList", sessionAttendanceList);
	 		modelAndView.addObject("sessionAttendanceList", sessionAttendanceList);
	 		modelAndView.addObject("rowCount", sessionAttendanceList.size());
	 		modelAndView.addObject("searchBean", searchBean);
	 		//modelAndView.addObject("facultyIdMap", facultyIdMap);
	 		modelAndView.addObject("yearList", ACAD_YEAR_LIST);
	 		modelAndView.addObject("subjectCodeMap", getsubjectCodeMap());
	 		return modelAndView;
	 	}
		
		@GetMapping(value="/exportexcel")
	 	public ModelAndView downloadSessionParticipantsReportReport(HttpServletRequest request) throws Exception{
			List<ParticipantReportBean> sessionAttendanceList=(List<ParticipantReportBean>) request.getSession().getAttribute("sessionAttendanceList");
			return new ModelAndView("sessionAttendanceExcelView", "sessionAttendanceList", sessionAttendanceList);
		}
	
}
