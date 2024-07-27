package com.nmims.controllers;

import java.io.IOException;
import java.util.HashMap;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.nmims.beans.SessionAttendanceFeedbackAcads;
import com.nmims.beans.SessionDayTimeAcadsBean;
import com.nmims.beans.StudentAcadsBean;
import com.nmims.daos.AttendanceFeedbackDAO;
import com.nmims.daos.TimeTableDAO;
import com.nmims.helpers.ZoomManager;
import com.nmims.services.AttendnaceReportService;
import com.nmims.util.ContentUtil;

@RestController
@CrossOrigin(origins = "*", allowedHeaders = "*")
@RequestMapping("m")
public class AttendanceFeedbackRestController extends BaseController {
	
	
	@Autowired(required = false)
	ApplicationContext act;

	@Autowired
	private AttendanceFeedbackDAO attendanceFeedbackDAO;

	@Autowired
	private ZoomManager zoomManger;
	
	@Autowired
	AttendnaceReportService attendanceReportService;
	
	@Value("${MAX_WEBEX_USERS}")
	private int MAX_WEBEX_USERS2;
	
	@Value( "${CURRENT_ACAD_YEAR}" )
	private String CURRENT_ACAD_YEAR;


	private int MAX_WEBEX_USERS = 1000;
	
	@Value("${SESSION_ATTENDANCE_BUFFER}")
	private int SESSION_ATTENDANCE_BUFFER;

	
	@CrossOrigin(origins="*", allowedHeaders="*")
	//temporary for mobile to be deleted later start
	@PostMapping(value = "/attendScheduledSessionForReact", consumes = "application/json",produces = "application/json")
	public ResponseEntity<HashMap<String,String>> m_attendScheduledSessionForReact(HttpServletRequest request, HttpServletResponse response, @RequestBody SessionDayTimeAcadsBean session) throws Exception{
		ResponseEntity<HashMap<String,String>> resp = zoomSessionJoin(request, response, session);
		return resp;
	}
	
	public ResponseEntity<HashMap<String, String>> zoomSessionJoin(HttpServletRequest request, HttpServletResponse response, SessionDayTimeAcadsBean sessionFromPage) {
		HttpHeaders headers = new HttpHeaders();
		headers.add("Content-Type", "application/json");

		//String forMobile = request.getParameter("forMobile");
		StudentAcadsBean student = null;
		SessionAttendanceFeedbackAcads attendance = new SessionAttendanceFeedbackAcads();
		TimeTableDAO dao = (TimeTableDAO) act.getBean("timeTableDAO");
		HashMap<String, String> responseObj = new HashMap<>();
		String isReAttended = "N";

		if ("true".equalsIgnoreCase(sessionFromPage.getForMobile())) {
			// Called from Mobile App, Student details not available in session
			String sapid = sessionFromPage.getSapId();
			String email = sessionFromPage.getEmail();
			String firstName = sessionFromPage.getFirstName();
			String lastName = sessionFromPage.getLastName();
			String mobile = sessionFromPage.getMobile();

			student = new StudentAcadsBean();
			student.setSapid(sapid);
			student.setEmailId(email);
			student.setMobile(mobile);
			student.setFirstName(firstName);
			student.setLastName(lastName);
			attendance.setDevice("MobileApp");

		} else {
			// Called via Desktop
//			if(!checkSession(request, response)){
//				return new ModelAndView("studentPortalRediret");
//			}

//			student = (StudentBean)request.getSession().getAttribute("student_acads");//Take from session

			String sapid = sessionFromPage.getSapId();
			String email = sessionFromPage.getEmail();
			String firstName = sessionFromPage.getFirstName();
			String lastName = sessionFromPage.getLastName();
			String mobile = sessionFromPage.getMobile();

			student = new StudentAcadsBean();
			student.setSapid(sapid);
			student.setEmailId(email);
			student.setMobile(mobile);
			student.setFirstName(firstName);
			student.setLastName(lastName);
			attendance.setDevice("WebApp");

		}

		String id = sessionFromPage.getId();
		String joinFor = request.getParameter("joinFor");
		
		//adding student sapid in cookie
		Cookie studentCookie= new Cookie("st_userId", student.getSapid());
		studentCookie.setMaxAge(60*60*4);
		studentCookie.setPath("/acads");
		response.addCookie(studentCookie);
		
		//adding sessionId in cookie
		Cookie sessionCookie= new Cookie("st_sessionId", id);
		sessionCookie.setMaxAge(60*60*4);
		sessionCookie.setPath("/acads");
		response.addCookie(sessionCookie);

		/* String joinForParameter = request.getParameter("joinFor"); */
		SessionDayTimeAcadsBean session = dao.findScheduledSessionById(id);

		session.setDevice(attendance.getDevice());
		
		String acadDateFormat = ContentUtil.prepareAcadDateFormat(session.getMonth(), session.getYear());
		
		//adding  acadDateFormat in cookie
		Cookie acadDateFormatCookie= new Cookie("st_acadDateFormat", acadDateFormat);
		acadDateFormatCookie.setMaxAge(60*60*4);
		acadDateFormatCookie.setPath("/acads");
		response.addCookie(acadDateFormatCookie);
		
		attendance = dao.checkSessionAttendance(student.getSapid(), id, acadDateFormat);
		if (attendance != null) {
			//Student has already joined session. Send him to same session again.
			HashMap<String, String> joinurl = reAttendSessionMobile(attendance.getJoinurl());
			return new ResponseEntity<HashMap<String,String>>(joinurl, headers, HttpStatus.OK);
		}

		/*
		 * if(attendance != null){ //Student has already joined session. Send him to
		 * same session again. return
		 * joinSessionUsingAlternateFacultyConfiguration(student,session,attendance.
		 * getMeetingKey(),attendance.getMeetingPwd(),attendance.getFacultyId()); }
		 * 
		 * if(!"ANY".equalsIgnoreCase(joinFor)){ //Student wants to join specific
		 * faculty session return joinPreferredFacultySession(joinFor, student, session,
		 * request); }
		 */

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
		boolean firstAlternateSessionAvailable = hasParallelAlternateSession
				&& (noOfUsersJoined < (2 * MAX_WEBEX_USERS));
		boolean secondAlternateSessionAvailable = hasParallelAlternateSession2
				&& (noOfUsersJoined < (3 * MAX_WEBEX_USERS));
		boolean thirdAlternateSessionAvailable = hasParallelAlternateSession3
				&& (noOfUsersJoined < (4 * MAX_WEBEX_USERS));

		if (originalSessionAvailable) {
			HashMap<String, String> joinurl = joinSessionForMobile(student, session, session.getFacultyId(), isReAttended);
			return new ResponseEntity<HashMap<String, String>>(joinurl, headers, HttpStatus.OK);
		}
		return new ResponseEntity<HashMap<String, String>>(responseObj, headers, HttpStatus.CONFLICT);
	}
	
	
	@GetMapping(value = "/attendScheduledSessionold", consumes = "application/json",produces = "application/json")
	public ResponseEntity<HashMap<String,String>> m_attendScheduledSessionOld(HttpServletRequest request, HttpServletResponse response) throws Exception{
		HttpHeaders headers = new HttpHeaders();
		headers.add("Content-Type", "application/json");
		
		String forMobile = request.getParameter("forMobile");
		StudentAcadsBean student = null;
		SessionAttendanceFeedbackAcads attendance = new SessionAttendanceFeedbackAcads();
		TimeTableDAO dao = (TimeTableDAO)act.getBean("timeTableDAO");
		HashMap<String,String> responseObj = new HashMap<>();
		
		if("true".equalsIgnoreCase(forMobile)) {
			//Called from Mobile App, Student details not available in session
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

		}

		String id = request.getParameter("id");
		String joinFor = request.getParameter("joinFor");
		
		/*String joinForParameter = request.getParameter("joinFor"); */
		SessionDayTimeAcadsBean session = dao.findScheduledSessionById(id);

		session.setDevice(attendance.getDevice());
		
		String acadDateFormat = ContentUtil.prepareAcadDateFormat(session.getMonth(), session.getYear());
		attendance = dao.checkSessionAttendance(student.getSapid(), id, acadDateFormat);
		
		if(attendance != null){
			//Student has already joined session. Send him to same session again.
			HashMap<String, String> joinurl = joinSessionUsingAlternateFacultyConfiguration2(student,session,attendance.getMeetingKey(),
												attendance.getMeetingPwd(),attendance.getFacultyId(), "Y");
			return new ResponseEntity<HashMap<String,String>>(joinurl, headers, HttpStatus.OK);
		}
		
		if(!"ANY".equalsIgnoreCase(joinFor)){
			
			// If Orientation Session Do Not check limit
			if (session.getSubject().equalsIgnoreCase("Orientation")) {
				HashMap<String, String> joinurl = joinSessionForMobile(student, session,  session.getFacultyId(), "N");
				return new ResponseEntity<HashMap<String,String>>(joinurl, headers, HttpStatus.OK);
			}
			
			//Student wants to join specific faculty session
			HashMap<String, String> joinurl = joinPreferredFacultySessionForMobile(joinFor, student, session);
			return new ResponseEntity<HashMap<String,String>>(joinurl, headers, HttpStatus.OK);
		}

		boolean hasParallelAlternateSession = false;
		boolean hasParallelAlternateSession2 = false;
		boolean hasParallelAlternateSession3 = false;

		if(!StringUtils.isBlank(session.getAltMeetingKey())){
			hasParallelAlternateSession = true;
		}
		if(!StringUtils.isBlank(session.getAltMeetingKey2())){
			hasParallelAlternateSession2 = true;
		}
		if(!StringUtils.isBlank(session.getAltMeetingKey3())){
			hasParallelAlternateSession3 = true;
		}

		int noOfUsersJoined = dao.findUsersJoined(id);
		
		boolean originalSessionAvailable = noOfUsersJoined < MAX_WEBEX_USERS;
		boolean firstAlternateSessionAvailable = hasParallelAlternateSession && (noOfUsersJoined < (2 * MAX_WEBEX_USERS) );
		boolean secondAlternateSessionAvailable = hasParallelAlternateSession2 && (noOfUsersJoined < (3 * MAX_WEBEX_USERS) );
		boolean thirdAlternateSessionAvailable = hasParallelAlternateSession3 && (noOfUsersJoined < (4 * MAX_WEBEX_USERS) );

		if(originalSessionAvailable){
			HashMap<String, String> joinurl = joinSessionForMobile(student, session, session.getFacultyId(), "N");
			return new ResponseEntity<HashMap<String,String>>(joinurl, headers, HttpStatus.OK);
			
		}else if(firstAlternateSessionAvailable){
			HashMap<String, String> joinurl = joinSessionUsingAlternateFacultyConfiguration2(student,session,session.getAltMeetingKey(),
					session.getAltMeetingPwd(),session.getAltFacultyId(), "N");
			return new ResponseEntity<HashMap<String,String>>(joinurl, headers, HttpStatus.OK);
			
		}else if(secondAlternateSessionAvailable) {
			HashMap<String, String> joinurl = joinSessionUsingAlternateFacultyConfiguration2(student,session,session.getAltMeetingKey2(),
					session.getAltMeetingPwd2(),session.getAltFacultyId2(), "N");
			return new ResponseEntity<HashMap<String,String>>(joinurl, headers, HttpStatus.OK);
			
		}else if(thirdAlternateSessionAvailable) {
			HashMap<String, String> joinurl = joinSessionUsingAlternateFacultyConfiguration2(student,session,session.getAltMeetingKey3(),
					session.getAltMeetingPwd3(),session.getAltFacultyId3(), "N");
			return new ResponseEntity<HashMap<String,String>>(joinurl, headers, HttpStatus.OK);
			
		}else {
			// All Classes Full, Divide any new students among running sessions now for
			// additional buffer capacity
			SessionAttendanceFeedbackAcads sessionWithLeastAttendance = dao.getSessionWithLeastNumberOfAttendees(id);
			if ((sessionWithLeastAttendance.getNumberOfAttendees() - MAX_WEBEX_USERS) < SESSION_ATTENDANCE_BUFFER) {
				HashMap<String, String> joinurl = joinSessionUsingAlternateFacultyConfiguration2(student, session,
						sessionWithLeastAttendance.getMeetingKey(), sessionWithLeastAttendance.getMeetingPwd(),
						sessionWithLeastAttendance.getFacultyId(), "N");
				return new ResponseEntity<HashMap<String,String>>(joinurl, headers, HttpStatus.OK);
			}else {
				HashMap<String, String> responseData = new HashMap<>();
				responseObj.put("status", "error");
				return new ResponseEntity<HashMap<String,String>>(responseData, headers, HttpStatus.OK);
			}
		}

//		return new ResponseEntity<HashMap<String,String>>(responseObj,headers, HttpStatus.CONFLICT);
		
		//return null;

		/*if((noOfUsersJoined >= MAX_WEBEX_USERS)){//Not joined yet but class is full

			//boolean hasParallelAlternateSession = dao.checkIfHasParallelAlternateSession(id,"ALT1");//ALT1,ALT2,ALT3  :- alternate faculty 1,2,3 and so on




			if(!hasParallelAlternateSession){//If no alternate session then show class full
				return classFull(session);
			}else if(hasParallelAlternateSession && (noOfUsersJoined >= ((2 *MAX_WEBEX_USERS) + 25))){
				//Has parallel session but that is also Full

				if(!hasParallelAlternateSession2){//If no alternate session 2 then show class full
					return classFull(session);
				}else if(hasParallelAlternateSession2 && (noOfUsersJoined >= ((3 *MAX_WEBEX_USERS) + 50))){
					//Has 2nd parallel session but that is also Full
					if(!hasParallelAlternateSession3){//If no alternate session 3 then show class full
						return classFull(session);
					}else if(hasParallelAlternateSession3 && (noOfUsersJoined >= ((4 *MAX_WEBEX_USERS) + 75))){
						//Has 3rd parallel session but that is also Full
						return classFull(session);
					}else{
						//3rd parallel session is not yet full, allow to join
						return joinSessionUsingAlternateFacultyConfiguration(student,userId,session,session.getAltMeetingKey3(),session.getAltMeetingPwd3(),session.getAltFacultyId3()); //For Third threshold//
					}
				}else{
					//2nd parallel session is not yet full, allow to join
					return joinSessionUsingAlternateFacultyConfiguration(student,userId,session,session.getAltMeetingKey2(),session.getAltMeetingPwd2(),session.getAltFacultyId2()); //For Second threshold//

				}
			}else{
				//Has 2nd parallel session, which is not yet full
				return joinSessionUsingAlternateFacultyConfiguration(student,userId,session,session.getAltMeetingKey(),session.getAltMeetingPwd(),session.getAltFacultyId()); //For First threshold//
			}
		}else if(attendedPreviousSession){
			//Attended previous session
			ModelAndView modelnView = new ModelAndView("alreadyAttended"); 
			return modelnView;
		}else{
			return joinSession(student, session, userId, session.getFacultyId());//Didn't join session yet and class is not yet full
		}*/



		//return new ModelAndView("redirect:" + joinUrl);
	}

	//end
	
	@GetMapping(value = "/attendScheduledSession", consumes = "application/json",produces = "application/json")
	public ResponseEntity<HashMap<String,String>> m_attendScheduledSession(HttpServletRequest request, HttpServletResponse response) throws Exception{
		HttpHeaders headers = new HttpHeaders();
		headers.add("Content-Type", "application/json");
		
		String forMobile = request.getParameter("forMobile");
		StudentAcadsBean student = null;
		SessionAttendanceFeedbackAcads attendance = new SessionAttendanceFeedbackAcads();
		TimeTableDAO dao = (TimeTableDAO)act.getBean("timeTableDAO");
		HashMap<String,String> responseObj = new HashMap<>();
		
		if("true".equalsIgnoreCase(forMobile)) {
			//Called from Mobile App, Student details not available in session
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

		}

		String id = request.getParameter("id");
		String joinFor = request.getParameter("joinFor");
		
		SessionDayTimeAcadsBean session = dao.findScheduledSessionById(id);

		session.setDevice(attendance.getDevice());
		
		String acadDateFormat = ContentUtil.prepareAcadDateFormat(session.getMonth(), session.getYear());
		attendance = dao.checkSessionAttendance(student.getSapid(), id, acadDateFormat);
		
		if(attendance != null){
			//Student has already joined session. Send him to same session again.
			HashMap<String, String> joinurl = reAttendSessionMobile(attendance.getJoinurl());
			return new ResponseEntity<HashMap<String,String>>(joinurl, headers, HttpStatus.OK);
			
			// Commented by Somesh on 18-09-2021, as now redirect via join-url in case of re-joining
			// HashMap<String, String> joinurl = joinSessionUsingAlternateFacultyConfiguration2(student,session,attendance.getMeetingKey(),
			//								attendance.getMeetingPwd(),attendance.getFacultyId(), "Y");
		}
		
		if(!"ANY".equalsIgnoreCase(joinFor)){
			
			// If Orientation Session Do Not check limit
			/*
			if (session.getSubject().equalsIgnoreCase("Orientation")) {
				HashMap<String, String> joinurl = joinSessionForMobile(student, session,  session.getFacultyId(), "N");
				return new ResponseEntity<HashMap<String,String>>(joinurl, headers, HttpStatus.OK);
			}
			*/
			
			//Student wants to join specific faculty session
			HashMap<String, String> joinurl = joinPreferredFacultySessionForMobileV2(joinFor, student, session);
			return new ResponseEntity<HashMap<String,String>>(joinurl, headers, HttpStatus.OK);
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
		
		if(originalSessionAvailable){
			HashMap<String, String> joinurl = joinSessionForMobile(student, session, session.getFacultyId(), "N");
			return new ResponseEntity<HashMap<String,String>>(joinurl, headers, HttpStatus.OK);
			
		}else if(firstAlternateSessionAvailable){
			HashMap<String, String> joinurl = joinSessionUsingAlternateFacultyConfiguration2(student,session,session.getAltMeetingKey(),
					session.getAltMeetingPwd(),session.getAltFacultyId(), "N");
			return new ResponseEntity<HashMap<String,String>>(joinurl, headers, HttpStatus.OK);
			
		}else if(secondAlternateSessionAvailable) {
			HashMap<String, String> joinurl = joinSessionUsingAlternateFacultyConfiguration2(student,session,session.getAltMeetingKey2(),
					session.getAltMeetingPwd2(),session.getAltFacultyId2(), "N");
			return new ResponseEntity<HashMap<String,String>>(joinurl, headers, HttpStatus.OK);
			
		}else if(thirdAlternateSessionAvailable) {
			HashMap<String, String> joinurl = joinSessionUsingAlternateFacultyConfiguration2(student,session,session.getAltMeetingKey3(),
					session.getAltMeetingPwd3(),session.getAltFacultyId3(), "N");
			return new ResponseEntity<HashMap<String,String>>(joinurl, headers, HttpStatus.OK);
			
		}else {
			HashMap<String, String> responseData = new HashMap<>();
			responseObj.put("status", "error");
			return new ResponseEntity<HashMap<String,String>>(responseData, headers, HttpStatus.OK);
		}
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
	
	private HashMap<String, String> joinSessionUsingAlternateFacultyConfiguration2(StudentAcadsBean student, SessionDayTimeAcadsBean session,
			String meetingKey, String meetingPassWord, String facultyId, String isReAttended) {
		session.setMeetingKey(meetingKey);
		session.setMeetingPwd(meetingPassWord);
		
		return joinSessionForMobile(student, session, facultyId, isReAttended);
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
	
	private HashMap<String, String> reAttendSessionMobile(String joinUrl) {
		HashMap<String, String> responseObj = new HashMap<>();
		responseObj.put("join_url", joinUrl);
		responseObj.put("status", "success");
		return responseObj;
	}
	
	@GetMapping(value="/updateParticipantsDetails")
	public String  participantsReportUpdate(@RequestParam String startDate, String endDate) throws Exception{
		attendanceReportService.fetchSessionAttendnaceFromZoomAndUpdateToDB(startDate, endDate);
		return "Execution of updateParticipantsDetails is Successful";
	}

}
