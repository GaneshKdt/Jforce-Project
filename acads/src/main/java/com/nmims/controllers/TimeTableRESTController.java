package com.nmims.controllers;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.nmims.assemblers.StudentTimeTableDtoAssembler;
import com.nmims.beans.ContentAcadsBean;
import com.nmims.beans.EventBean;
import com.nmims.beans.ExamBookingTransactionAcadsBean;
import com.nmims.beans.ExamOrderAcadsBean;
import com.nmims.beans.FacultyAcadsBean;
import com.nmims.beans.ParallelSessionBean;
import com.nmims.beans.ProgramSubjectMappingAcadsBean;
import com.nmims.beans.SessionDayTimeAcadsBean;
import com.nmims.beans.StudentAcadsBean;
import com.nmims.beans.VideoContentAcadsBean;
import com.nmims.daos.ContentDAO;
import com.nmims.daos.FacultyDAO;
import com.nmims.daos.TimeTableDAO;
import com.nmims.daos.VideoContentDAO;
import com.nmims.dto.StudentTimeTableDto;
import com.nmims.factory.ContentFactory.StudentType;
import com.nmims.interfaces.ContentInterface;
import com.nmims.services.StudentCourseMappingService;
import com.nmims.services.StudentService;
import com.nmims.services.TimeTableService;

@RestController
@CrossOrigin(origins = "*", allowedHeaders = "*")
@RequestMapping("m")
public class TimeTableRESTController extends BaseController{

	@Autowired(required=false)
	ApplicationContext act;

	@Autowired
	TimeTableService timeTableService;
	
	@Autowired
	StudentService studentService;
	
	private static final DateFormat sdf = new SimpleDateFormat("dd-MMM-yyyy");
    private static final DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss");
	private ArrayList<String> programList = null;
	private ArrayList<String> subjectList = null; 
	private ArrayList<String> facultyList = null;
	private final int pageSize = 20;
	private ArrayList<ProgramSubjectMappingAcadsBean> programSubjectMappingList = null;

	private static final Logger logger = LoggerFactory.getLogger(TimeTableController.class);
	private ArrayList<String> yearList = new ArrayList<String>(Arrays.asList( 
			"2015","2016","2017")); 


	@Value( "${CURRENT_ACAD_MONTH}" )
	private String CURRENT_ACAD_MONTH;

	@Value( "${CURRENT_ACAD_YEAR}" )
	private String CURRENT_ACAD_YEAR;
	
	@Value("#{'${ACAD_YEAR_LIST}'.split(',')}")
	private List<String> ACAD_YEAR_LIST;
	
	@Value("${TIMEBOUND_PORTAL_LIST}")
	private List<String> TIMEBOUND_PORTAL_LIST;

	private HashMap<String, FacultyAcadsBean> mapOfFacultyIdAndFacultyRecord = null;
	
	@Autowired
	StudentCourseMappingService studentCourseMappingService;
	
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
	

	@RequestMapping(value = "/mstudentTimeTableByPssId",  method = RequestMethod.POST, consumes = "application/json", produces = "application/json")
	public ResponseEntity<List<StudentTimeTableDto>> mstudentTimeTableByPssId(@RequestBody StudentAcadsBean input) {
		HttpHeaders headers = new HttpHeaders();
		headers.add("Content-Type", "application/json");
		TimeTableDAO dao = (TimeTableDAO)act.getBean("timeTableDAO");
		ArrayList<SessionDayTimeAcadsBean> scheduledSessionList = new ArrayList<SessionDayTimeAcadsBean>();
		ArrayList<StudentTimeTableDto> sessionTimeTableDto = new ArrayList<StudentTimeTableDto>();

		try {
			String sapId = input.getSapid();
			StudentAcadsBean student =  dao.getSingleStudentsData(sapId);
			StudentAcadsBean studentRegistrationData = new StudentAcadsBean();
			List<Integer> currentSemPSSId = input.getCurrentSemPSSId();

			//Check student registration and return reg details
			studentRegistrationData = timeTableService.checkStudentRegistration(sapId, student);

			boolean isCourseMappingAvailable = dao.isCourseMappingApplicableForCurrentAcadCycle(student.getSapid());

			ExamOrderAcadsBean examOrderForSession = null;
			if (studentRegistrationData != null) {
				try {
					examOrderForSession = dao.getExamOrderByYearMonth(studentRegistrationData.getYear(), studentRegistrationData.getMonth());
				} catch (Exception e) {
					  
				}
			}

			if(studentRegistrationData == null || examOrderForSession == null){
				if (isCourseMappingAvailable) {
					//Even if student not register for current cycle Waived-in subject is applicable
					scheduledSessionList = dao.getAllSessionsByCourseMapping(student.getSapid());
					//sessionTimeTableDto.addAll(transferSessionBeanToDto(scheduledSessionList));
					sessionTimeTableDto.addAll(StudentTimeTableDtoAssembler.transferSessionBeanToDto(scheduledSessionList));
					return new ResponseEntity<List<StudentTimeTableDto>>(sessionTimeTableDto, headers,  HttpStatus.OK);
				}
				return new ResponseEntity<List<StudentTimeTableDto>>(sessionTimeTableDto, headers,  HttpStatus.OK);
			}

			student.setProgram(studentRegistrationData.getProgram());
			student.setSem(studentRegistrationData.getSem());
			String year = studentRegistrationData.getYear();
			String month = studentRegistrationData.getMonth();
			String sapid = student.getSapid();
			String sem = studentRegistrationData.getSem();
			String consumerProgramStructureId = student.getConsumerProgramStructureId();
			
			scheduledSessionList = timeTableService.getAllScheduledSessionsForPG(sapid, year, month, consumerProgramStructureId, sem, currentSemPSSId);
			//sessionTimeTableDto.addAll(transferSessionBeanToDto(scheduledSessionList));
			sessionTimeTableDto.addAll(StudentTimeTableDtoAssembler.transferSessionBeanToDto(scheduledSessionList));
			return new ResponseEntity<List<StudentTimeTableDto>>(sessionTimeTableDto, headers,  HttpStatus.OK);

		} catch (Exception e) {
			  
		}
		return new ResponseEntity<List<StudentTimeTableDto>>(sessionTimeTableDto, headers,  HttpStatus.OK);
	}

	@RequestMapping(value = "/viewSessionsTimelineByPSSId", method = {RequestMethod.POST})
	public ResponseEntity<List<StudentTimeTableDto>> viewSessionsTimelineByPSSId(@RequestBody StudentAcadsBean input) {

		HttpHeaders headers = new HttpHeaders();
		headers.add("Content-Type", "application/json");
		TimeTableDAO dao = (TimeTableDAO)act.getBean("timeTableDAO");

		ArrayList<SessionDayTimeAcadsBean> scheduledSessionList = new ArrayList<SessionDayTimeAcadsBean>();
		ArrayList<StudentTimeTableDto> sessionTimelineDtos = new ArrayList<StudentTimeTableDto>();
		try {
			String sapId = input.getSapid();
			StudentAcadsBean student =  dao.getSingleStudentsData(sapId);
			StudentAcadsBean studentRegistrationData = new StudentAcadsBean();
			List<Integer> currentSemPSSId = input.getCurrentSemPSSId();

			//Check student registration and return reg details
			studentRegistrationData = timeTableService.checkStudentRegistration(sapId, student);

			boolean isCourseMappingAvailable = dao.isCourseMappingApplicableForCurrentAcadCycle(student.getSapid());

			ExamOrderAcadsBean examOrderForSession = null;
			if (studentRegistrationData != null) {
				try {
					examOrderForSession = dao.getExamOrderByYearMonth(studentRegistrationData.getYear(), studentRegistrationData.getMonth());
				} catch (Exception e) {
					  
				}
			}


			if(studentRegistrationData == null || examOrderForSession == null){

				if (isCourseMappingAvailable) {
					//Even if student not register for current cycle Waived-in subject is applicable
					scheduledSessionList = dao.getAllSessionsByCourseMapping(student.getSapid());
					sessionTimelineDtos.addAll(StudentTimeTableDtoAssembler.transferSessionTimelineBeanToDto(scheduledSessionList,sapId, dao));
					
					//sessionTimelineDtos = getVideosForSessionList(sessionTimelineDtos,student);
					
					return new ResponseEntity<List<StudentTimeTableDto>>(sessionTimelineDtos, headers, HttpStatus.OK);
				}

				return new ResponseEntity<List<StudentTimeTableDto>>(sessionTimelineDtos, headers,  HttpStatus.OK);
			}

			//For Old students incomplete data//
			//Take program from Registration data and not Student data. 
			student.setProgram(studentRegistrationData.getProgram());
			student.setSem(studentRegistrationData.getSem());
			String year = studentRegistrationData.getYear();
			String month = studentRegistrationData.getMonth();
			String sapid = student.getSapid();
			String sem = studentRegistrationData.getSem();
			String consumerProgramStructureId = student.getConsumerProgramStructureId();
			
			scheduledSessionList = timeTableService.getAllScheduledSessionsForPG(sapid, year, month, consumerProgramStructureId, sem, currentSemPSSId);
			sessionTimelineDtos.addAll(StudentTimeTableDtoAssembler.transferSessionTimelineBeanToDto(scheduledSessionList,sapId, dao));
			//if(scheduledSessionList != null && scheduledSessionList.size() != 0){
				
				//sessionTimelineDtos = getVideosForSessionList(sessionTimelineDtos,studentRegistrationData);
				
				return new ResponseEntity<List<StudentTimeTableDto>>(sessionTimelineDtos, headers, HttpStatus.OK);
			//}

		} catch (Exception e) {
			  
		}
		return new ResponseEntity<List<StudentTimeTableDto>>(sessionTimelineDtos, headers, HttpStatus.OK);
	}

	@RequestMapping(value = "/studentTimeTableUpcomingHomeByPSSId",  method = RequestMethod.POST, consumes = "application/json", produces = "application/json")
	public  ResponseEntity<List<SessionDayTimeAcadsBean>> mstudentTimeTableUpcomingHomeByPSSId(@RequestBody StudentAcadsBean input) {
		HttpHeaders headers = new HttpHeaders();
		headers.add("Content-Type", "application/json");
		TimeTableDAO dao = (TimeTableDAO)act.getBean("timeTableDAO");
		List<SessionDayTimeAcadsBean> finalScheduledSessionList = new ArrayList<SessionDayTimeAcadsBean>();
		ArrayList<SessionDayTimeAcadsBean> scheduledSessionList = new ArrayList<SessionDayTimeAcadsBean>();

		try {
			String sapId = input.getSapid();
			StudentAcadsBean student =  dao.getSingleStudentsData(sapId);
			List<Integer> currentSemPSSId = input.getCurrentSemPSSId();

			ArrayList<String> currentSemPSSIdString = new ArrayList<>(currentSemPSSId.size());
			for (Integer myInt : currentSemPSSId) { 
				currentSemPSSIdString.add(String.valueOf(myInt)); 
			}
			
			scheduledSessionList.addAll(getAcademicCalendar(student,dao,currentSemPSSIdString));

			//If Session list is > 10 then send only 1st 10 sessions
			if (scheduledSessionList.size() > 10) {
				finalScheduledSessionList = scheduledSessionList.subList(0, 10);
			}else {
				finalScheduledSessionList = scheduledSessionList;
			}
			return new ResponseEntity<List<SessionDayTimeAcadsBean>>(finalScheduledSessionList, headers, HttpStatus.OK);
		} catch (Exception e) {
			  
		}

		return new ResponseEntity<List<SessionDayTimeAcadsBean>>(finalScheduledSessionList, headers, HttpStatus.OK);
	}
	
	
	@RequestMapping(value = "/studentTimeTable",  method = RequestMethod.POST, consumes = "application/json", produces = "application/json")
	public ResponseEntity<List<SessionDayTimeAcadsBean>> mstudentTimeTable(@RequestBody StudentAcadsBean input) {
		HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Type", "application/json");
        TimeTableDAO dao = (TimeTableDAO)act.getBean("timeTableDAO");
        ArrayList<SessionDayTimeAcadsBean> scheduledSessionList = new ArrayList<SessionDayTimeAcadsBean>();
        try {
			String sapId = input.getSapid();
			StudentAcadsBean student =  dao.getSingleStudentsData(sapId);
			StudentAcadsBean studentRegistrationData = new StudentAcadsBean();
			
			if("EPBM".equalsIgnoreCase(student.getProgram()) || "MPDV".equalsIgnoreCase(student.getProgram())) {
				studentRegistrationData = dao.getStudentRegistrationDataForExecutive(sapId);			
			}else{
				studentRegistrationData = dao.getStudentRegistrationDataNew(sapId);
			}
			
			boolean isCourseMappingAvailable = dao.isCourseMappingApplicableForCurrentAcadCycle(student.getSapid());
			
			if(studentRegistrationData == null){
				if (isCourseMappingAvailable) {
					//Even if student not register for current cycle Waived-in subject is applicable
					scheduledSessionList = dao.getAllSessionsByCourseMapping(student.getSapid());
					return new ResponseEntity<List<SessionDayTimeAcadsBean>>(scheduledSessionList, headers,  HttpStatus.OK);
				}
				return new ResponseEntity<List<SessionDayTimeAcadsBean>>(scheduledSessionList, headers,  HttpStatus.OK);
			}
			
			student.setProgram(studentRegistrationData.getProgram());
			student.setSem(studentRegistrationData.getSem());
			String year = studentRegistrationData.getYear();
			String month = studentRegistrationData.getMonth();
			
			scheduledSessionList = timeTableService.getAllScheduledSessions(student, year, month);
			return new ResponseEntity<List<SessionDayTimeAcadsBean>>(scheduledSessionList, headers,  HttpStatus.OK);
			
		} catch (Exception e) {
			  
		}
        return new ResponseEntity<List<SessionDayTimeAcadsBean>>(scheduledSessionList, headers,  HttpStatus.OK);
	}
	
	@RequestMapping(value = "/viewSessionsTimeline", method = {RequestMethod.POST})
	public ResponseEntity<List<SessionDayTimeAcadsBean>> mviewSessionsTimeline(@RequestBody StudentAcadsBean input) {
		
		HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Type", "application/json");
        TimeTableDAO dao = (TimeTableDAO)act.getBean("timeTableDAO");
        
        ArrayList<SessionDayTimeAcadsBean> scheduledSessionList = new ArrayList<SessionDayTimeAcadsBean>();
		try {
			String sapId = input.getSapid();
			StudentAcadsBean student =  dao.getSingleStudentsData(sapId);
			StudentAcadsBean studentRegistrationData = new StudentAcadsBean();

			if("EPBM".equalsIgnoreCase(student.getProgram()) || "MPDV".equalsIgnoreCase(student.getProgram())) {
				studentRegistrationData = dao.getStudentRegistrationDataForExecutive(sapId);			
			}else{
				studentRegistrationData = dao.getStudentRegistrationDataNew(sapId);
			}
			
			boolean isCourseMappingAvailable = dao.isCourseMappingApplicableForCurrentAcadCycle(student.getSapid());
			
			if(studentRegistrationData == null){
				
				if (isCourseMappingAvailable) {
					//Even if student not register for current cycle Waived-in subject is applicable
					scheduledSessionList = dao.getAllSessionsByCourseMapping(student.getSapid());
				//	scheduledSessionList = getVideosForSessionList(scheduledSessionList,student);
					return new ResponseEntity<List<SessionDayTimeAcadsBean>>(scheduledSessionList, headers, HttpStatus.OK);
				}
				
				return new ResponseEntity<List<SessionDayTimeAcadsBean>>(scheduledSessionList, headers,  HttpStatus.OK);
			}

			//For Old students incomplete data//
			//Take program from Registration data and not Student data. 
			student.setProgram(studentRegistrationData.getProgram());
			student.setSem(studentRegistrationData.getSem());
			String year = studentRegistrationData.getYear();
			String month = studentRegistrationData.getMonth();
			
			scheduledSessionList = timeTableService.getAllScheduledSessions(student, year, month);
			if(scheduledSessionList != null && scheduledSessionList.size() != 0){
				//scheduledSessionList = getVideosForSessionList(scheduledSessionList,studentRegistrationData);
				return new ResponseEntity<List<SessionDayTimeAcadsBean>>(scheduledSessionList, headers, HttpStatus.OK);
			}
			
		} catch (Exception e) {
			  
		}
		return new ResponseEntity<List<SessionDayTimeAcadsBean>>(scheduledSessionList, headers, HttpStatus.OK);
	}

	private ArrayList<SessionDayTimeAcadsBean> getAcademicCalendar(StudentAcadsBean student, TimeTableDAO dao, ArrayList<String> currentSemPSSId) {
		ArrayList<SessionDayTimeAcadsBean> scheduledSessionList = new ArrayList<>();
		ArrayList<SessionDayTimeAcadsBean> commonscheduledSessionList = new ArrayList<SessionDayTimeAcadsBean>();
		ArrayList<SessionDayTimeAcadsBean> allScheduledSessionList = new ArrayList<SessionDayTimeAcadsBean>();
		StudentAcadsBean studentRegistrationData = new StudentAcadsBean();

		//Check student registration and return reg details
		studentRegistrationData = timeTableService.checkStudentRegistration(student.getSapid(), student);

		boolean isCourseMappingAvailable = dao.isCourseMappingApplicableForCurrentAcadCycle(student.getSapid());
		
		ExamOrderAcadsBean examOrderForSession = null;
		if (studentRegistrationData != null) {
			try {
				examOrderForSession = dao.getExamOrderByYearMonth(studentRegistrationData.getYear(), studentRegistrationData.getMonth());
			} catch (Exception e) {
				  
			}
		}
		
		if(studentRegistrationData == null || examOrderForSession == null){
			if (isCourseMappingAvailable) {
				allScheduledSessionList = dao.getAllSessionsByCourseMapping(student.getSapid());			
				return scheduledSessionList; //Course Mapping session are available even student is not registered
			}

			return scheduledSessionList; //No sessions if student has not registered for current academic cycle
		}

		if(studentRegistrationData != null){
			student.setProgram(studentRegistrationData.getProgram());
			student.setSem(studentRegistrationData.getSem());
		}

		//Commented By Somesh as getting PSS from session
		/*
		ArrayList<String> subjects = getSubjectsForStudent(student);
		//Remove WaiveOff Subject from applicable Subject list
		subjects.removeAll(student.getWaivedOffSubjects());
		 */

		scheduledSessionList = dao.getScheduledSessionForStudentsByCPSIdV3(studentRegistrationData.getYear(), studentRegistrationData.getMonth(), currentSemPSSId);

		//Adding sessions by course mapping
		//scheduledSessionList.addAll(pDao.getAllSessionsByCourseMapping(student.getSapid()));

		//Added for sorting


//		if(!"BBA".equalsIgnoreCase(student.getProgram()) && !"B.Com".equalsIgnoreCase(student.getProgram())){
//			//			Commented by Somesh as now session will coming on CPS id
//			//scheduledSessionList = pDao.getScheduledSessionForStudents(subjects,student);
//
//			//Get common sessions
//			commonscheduledSessionList = (ArrayList<SessionDayTimeBean>)dao.getCommonSessionsSemesterBased(student.getSem(),student.getProgram(),student.getConsumerProgramStructureId());
//		}else {
//			//Get Common Sessions for UG
//			commonscheduledSessionList = (ArrayList<SessionDayTimeBean>)dao.getCommonSessionsSemesterBasedForUG(student.getSem(),student.getProgram(),student.getConsumerProgramStructureId());
//		}
		
		commonscheduledSessionList = dao.getUpcomingCommonSessionsFromCommonQuickSessions(student.getConsumerProgramStructureId(), studentRegistrationData.getYear(), 
				studentRegistrationData.getMonth(), student.getSem());


		if (commonscheduledSessionList.size() > 0) {
			allScheduledSessionList.addAll(commonscheduledSessionList);
		} 

		allScheduledSessionList.addAll(scheduledSessionList);
		
		if(allScheduledSessionList.size() > 0) {
			Collections.sort(allScheduledSessionList, new Comparator<SessionDayTimeAcadsBean>() {
				@Override
				public int compare(SessionDayTimeAcadsBean sBean1, SessionDayTimeAcadsBean sBean2) {
					return sBean1.getDate().compareTo(sBean2.getDate());
				}
			});
		}
		return allScheduledSessionList;
	}
	
	private ArrayList<StudentTimeTableDto> transferSessionBeanToDto(ArrayList<SessionDayTimeAcadsBean> sessionList){
		ArrayList<StudentTimeTableDto> scheduledSessionList = new ArrayList<StudentTimeTableDto>();
		try {
			sessionList.forEach(session -> {
				StudentTimeTableDto sessionDto = new StudentTimeTableDto();
				sessionDto.setId(session.getId());
				sessionDto.setPrgmSemSubId(session.getPrgmSemSubId());
				sessionDto.setDate(session.getDate());
				sessionDto.setStartTime(session.getStartTime());
				sessionDto.setDay(session.getDay());
				sessionDto.setSubject(session.getSubject());
				sessionDto.setSessionName(session.getSessionName());
				sessionDto.setMonth(session.getMonth());
				sessionDto.setYear(session.getYear());
				sessionDto.setFacultyId(session.getFacultyId());
				sessionDto.setEndTime(session.getEndTime());
				sessionDto.setTrack(session.getTrack());
				sessionDto.setFirstName(session.getFirstName());
				sessionDto.setLastName(session.getLastName());
				sessionDto.setIsCancelled(session.getIsCancelled());
				sessionDto.setReasonForCancellation(session.getReasonForCancellation());
				sessionDto.setFacultyName(session.getFacultyName());
				scheduledSessionList.add(sessionDto);
			});			
		} catch (Exception e) {
			// TODO: handle exception
		}
		return scheduledSessionList;
	}
		
	@PostMapping(value = "/studentTimeTableExams", consumes = "application/json", produces = "application/json")
	public  ResponseEntity<List<ExamBookingTransactionAcadsBean>> mstudentTimeTableExams(@RequestBody StudentAcadsBean student) { 
		HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Type", "application/json"); 
//		ModelAndView modelnView = new ModelAndView("studentAcadCalendar");
//		if(!checkSession(request, respnse)){
//			return new ModelAndView("studentPortalRediret");
//		}
		List<ExamBookingTransactionAcadsBean> bookedExams = null;
		try{
			TimeTableDAO dao = (TimeTableDAO)act.getBean("timeTableDAO");
			//Start Get Registered Exam Dates  
			bookedExams = dao.getBookedExams(student.getSapid());
			//End Get Registered Exam Dates 
				return new ResponseEntity<List<ExamBookingTransactionAcadsBean>>(bookedExams, headers,  HttpStatus.OK);
		}catch(Exception e)
		{
			  
		}
		return new ResponseEntity<List<ExamBookingTransactionAcadsBean>>(bookedExams, headers,  HttpStatus.OK);
	}
	
	@RequestMapping(value = "/viewScheduledSession", method = RequestMethod.POST, consumes = "application/json", produces = "application/json")
	public ResponseEntity<List<ParallelSessionBean>> mviewScheduledSession(@RequestParam("id") String idString,
			@RequestBody StudentAcadsBean student) throws Exception {
//		ModelAndView modelnView = new ModelAndView("viewScheduledSession");
		HttpHeaders headers = new HttpHeaders();
		headers.add("Content-Type", "application/json");

		String userId = student.getSapid();
		ContentDAO cDao = (ContentDAO) act.getBean("contentDAO");

		String id = idString;
		/* String classFullMessage = request.getParameter("classFullMessage"); */
		TimeTableDAO dao = (TimeTableDAO) act.getBean("timeTableDAO");
		SessionDayTimeAcadsBean session = dao.findScheduledSessionById(id);
		
		String sessionIds="33139,33140,33141,33142,33143,33144";
		if (session.getHasModuleId()==null && sessionIds.contains(id)) {
			session.setSubject("Ascend: A Masterclass Series");
			session.setSessionName("Topic 5");
		}
		
		SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
		Date sessiondate = df.parse(session.getDate());
		Date currdate = new Date();
		if (sessiondate.before(currdate)) {
			session.setTimeboundId(dao.getTimeboundIdByModuleID(session.getModuleId()));
			// session.setVideosOfSession(vdao.getVideosForSession(session.getId()));
		}
//		HashMap<String, Integer> facultyIdAndRemSeatsMap = dao.getMapOfFacultyIdAndRemainingSeats(id, session);
		HashMap<String, Integer> facultyIdAndRemSeatsMap = dao.getMapOfFacultyIdAndRemainingSeatsV2(session);
		List<ParallelSessionBean> response = new ArrayList<>();
		HashMap<String, FacultyAcadsBean> listOfAllFaculties = mapOfFacultyIdAndFacultyRecord();
		
		Iterator it = facultyIdAndRemSeatsMap.entrySet().iterator();
		int i = 1;
		while (it.hasNext()) {
			Map.Entry pair = (Map.Entry) it.next();
			ParallelSessionBean parallelSessionBean = new ParallelSessionBean();
			parallelSessionBean.setFacultyId(pair.getKey().toString());
			parallelSessionBean.setSeats(pair.getValue().toString());
			FacultyAcadsBean facultyBean = listOfAllFaculties.get(pair.getKey());
			parallelSessionBean.setFirstName(facultyBean.getFirstName());
			parallelSessionBean.setLastName(facultyBean.getLastName());
			parallelSessionBean.setEmail(facultyBean.getEmail());
			parallelSessionBean.setActive(facultyBean.getActive());
			parallelSessionBean.setMobile(facultyBean.getMobile());
			parallelSessionBean.setCreatedBy(facultyBean.getCreatedBy());
			parallelSessionBean.setCreatedDate(facultyBean.getCreatedDate());
			parallelSessionBean.setLastModifiedBy(facultyBean.getLastModifiedBy());
			parallelSessionBean.setLastModifiedDate(facultyBean.getLastModifiedDate());
			parallelSessionBean.setSessionBean(session);
			
			if(session.getFacultyId().equalsIgnoreCase(pair.getKey().toString())) {
				parallelSessionBean.setJoinFor("HOST");
			}else if(session.getAltFacultyId().equalsIgnoreCase(pair.getKey().toString())) {
				parallelSessionBean.setJoinFor("ALTFACULTYID");
			}else if(session.getAltFacultyId2().equalsIgnoreCase(pair.getKey().toString())) {
				parallelSessionBean.setJoinFor("ALTFACULTYID2");
			}else if(session.getAltFacultyId3().equalsIgnoreCase(pair.getKey().toString())) {
				parallelSessionBean.setJoinFor("ALTFACULTYID3");
			}
			
			response.add(parallelSessionBean);

			it.remove(); // avoids a ConcurrentModificationException

		}

//		String sessionDate = session.getDate();
//		String sessionTime = session.getStartTime();
//
//		Date sessionDateTime = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(sessionDate + " " + sessionTime);
//		long minutesToSession = getDateDiff(new Date(), sessionDateTime, TimeUnit.MINUTES);
//		long minutesAfterSession = getDateDiff(sessionDateTime, new Date(), TimeUnit.MINUTES);


//for(int i = 0; i < facultyIdAndRemSeatsMap_list.size(); i++) {
//	 String s = listOfAllFaculties.get(facultyIdAndRemSeatsMap_list.get[i]);
//
//
//}

//		if(minutesToSession < 60 && minutesToSession > -120){
//			modelnView.addObject("enableAttendButton", "true");
//
//			response.put("enableAttendButton", session_list);
//			response.put("showQueryButton", session_list);
//			response.put("sessionOver", session_list);
//		}
//
//
//		if(minutesAfterSession > 120){
//			if(!"Guest Lecture".equalsIgnoreCase(session.getSessionName())){// added Temporary To hide Post My Query button for Guest Lecture
//				modelnView.addObject("showQueryButton", "true");
//			}
//			modelnView.addObject("sessionOver", "true");
//
//		}
//
//		String joinUrl = "";
//		String name = "Coordinator";
//		String email = "notavailable@mail.com";
//		String mobile = "0000000";
//		if(student != null){
//			name = student.getFirstName() + " "+ student.getLastName();
//			email = student.getEmailId() != null ? student.getEmailId() : "notavailable@mail.com";
//			mobile = student.getMobile() != null ? student.getMobile() : "0000000";
//		}
//
//		joinUrl = WEB_EX_API_URL + "?AT=JM&MK="+session.getMeetingKey()
//				+"&AN="+URLEncoder.encode(name, "UTF-8")
//				+"&AE="+URLEncoder.encode(email, "UTF-8")
//				+"&CO="+URLEncoder.encode(mobile, "UTF-8")
//				+"&PW="+session.getMeetingPwd();

//		modelnView.addObject("joinUrl", joinUrl);
//
//		String hostUrl = WEB_EX_LOGIN_API_URL+ "?AT=LI&WID="+session.getHostId()+"&PW="+session.getHostPassword()+"&BU="+WEB_EX_API_URL+"?MK="+session.getMeetingKey()+"%26AT=HM%26Rnd="+Math.random();
//		modelnView.addObject("hostUrl", hostUrl);
//		modelnView.addObject("SERVER_PATH", SERVER_PATH);
//
		return new ResponseEntity<List<ParallelSessionBean>>(response, headers, HttpStatus.OK);
	}
	
	@RequestMapping(value = "/studentTimeTableEvents",  method = RequestMethod.POST, consumes = "application/json", produces = "application/json")
	public  ResponseEntity<List<EventBean>> mstudentTimeTableEvents(@RequestBody StudentAcadsBean student) {
		HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Type", "application/json"); 
		List<EventBean> eventsList = null;
		try{
			TimeTableDAO dao = (TimeTableDAO)act.getBean("timeTableDAO");
			//Start Get Key Events Dates  
			eventsList = dao.getEventsList();
				return new ResponseEntity<List<EventBean>>(eventsList, headers,  HttpStatus.OK);
		}catch(Exception e)
		{
			  
		}
		return new ResponseEntity<List<EventBean>>(eventsList, headers,  HttpStatus.OK);
	}
	

//	//For Old students incomplete data//
//	//Take program from Registration data and not Student data. 
//	student.setProgram(studentRegistrationData.getProgram());
//	student.setSem(studentRegistrationData.getSem());
//	String year = studentRegistrationData.getYear();
//	String month =studentRegistrationData.getMonth();
//	ArrayList<String> subjects = getSubjectsForStudent(student);
////scheduledSessionList = dao.getScheduledSessionForStudents(subjects,student,year,month);
//// Commented as getting Nullpointer Exception 
////If it is in session, don't fetch from Database.
////scheduledSessionList = (ArrayList<SessionDayTimeBean>)request.getSession().getAttribute("studentSessionList");
//
//
//
////if(scheduledSessionList != null && scheduledSessionList.size() != 0){
////	modelnView.addObject("scheduledSessionList", scheduledSessionList);
////	return modelnView;
////}
//if("EPBM".equalsIgnoreCase(student.getProgram()) || "MPDV".equalsIgnoreCase(student.getProgram())) { 
//	scheduledSessionList.addAll(dao.getScheduledSessionForStudentsForExecutive(subjects,student,year,month));
//}else {
//	ArrayList<SessionDayTimeBean> commonscheduledSessionList = new ArrayList<SessionDayTimeBean>();
//	if (TIMEBOUND_PORTAL_LIST.contains(student.getConsumerProgramStructureId())) {
//		commonscheduledSessionList = dao.getCommonSessionsForMBAWx(student, year, month, "Upcoming");
//	}else if(!"BBA".equalsIgnoreCase(student.getProgram()) && !"B.Com".equalsIgnoreCase(student.getProgram())){
//		commonscheduledSessionList = dao.getCommonSessionsSemesterBasedFromToday(studentRegistrationData.getSem(),studentRegistrationData.getProgram(),student.getConsumerProgramStructureId());
//	}else if("BBA".equalsIgnoreCase(student.getProgram()) || "B.Com".equalsIgnoreCase(student.getProgram())) {
//		//Get Common Sessions for UG
//		commonscheduledSessionList = (ArrayList<SessionDayTimeBean>)dao.getCommonSessionsSemesterBasedForUG(student.getSem(),student.getProgram(),student.getConsumerProgramStructureId());
//	}
//	
//	if(commonscheduledSessionList!=null && commonscheduledSessionList.size()>0){
//		scheduledSessionList.addAll(commonscheduledSessionList);
//	}
////<<<<<<< HEAD
////	if (TIMEBOUND_PORTAL_LIST.contains(student.getConsumerProgramStructureId())) {
////		sql.append(" and s.hasModuleId = 'Y'"); 
////		}else{
////			sql.append(" and s.hasModuleId is null");
////		}
//	
//	scheduledSessionList.addAll(dao.getUpcomingScheduledSessionForStudentsByCPSIdV1(student, subjects));
//	
//	if(!"BBA".equalsIgnoreCase(student.getProgram()) && !"B.Com".equalsIgnoreCase(student.getProgram())){
//		//Commented By Somesh as Added session configurable
//		//scheduledSessionList.addAll(dao.mgetScheduledSessionForStudentsFromToday(subjects,input.getCenterName(),student.getConsumerProgramStructureId(), student.getSapid()));					
//	}
//
//}
////=======
////	scheduledSessionList.addAll(dao.mgetScheduledSessionForStudentsFromToday(subjects,input.getCenterName()));
////	}
////	
////	}
////
////>>>>>>> branch 'development' of https://ngasce@bitbucket.org/ngasce/acads.git
// 
//
////modelnView.addObject("scheduledSessionList", scheduledSessionList);
////modelnView.addObject("scheduledSessionListFromToday", scheduledSessionListFromToday);
////request.getSession().setAttribute("studentSessionList", scheduledSessionList);//To avoid fetching again
////request.getSession().setAttribute("scheduledSessionListFromToday", scheduledSessionListFromToday);
//return new ResponseEntity<List<SessionDayTimeBean>>(scheduledSessionList, headers,  HttpStatus.OK);
//
//}catch(Exception e)
//{
//  
//}
//return new ResponseEntity<List<SessionDayTimeBean>>(scheduledSessionList, headers,  HttpStatus.OK);
//}




	@PostMapping(value = "/viewSessionsTimelineOld")
	public ResponseEntity<List<SessionDayTimeAcadsBean>> mviewSessionsTimelineOld(@RequestBody StudentAcadsBean input) {
		
		HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Type", "application/json");
        
        ArrayList<SessionDayTimeAcadsBean> scheduledSessionList = new ArrayList<SessionDayTimeAcadsBean>();
		ArrayList<SessionDayTimeAcadsBean> commonscheduledSessionList =  new ArrayList<SessionDayTimeAcadsBean>();
		ArrayList<SessionDayTimeAcadsBean> eventSessionList = new ArrayList<SessionDayTimeAcadsBean>();
		
		try{
			
			TimeTableDAO dao = (TimeTableDAO)act.getBean("timeTableDAO");
			String sapId = input.getSapid();
			StudentAcadsBean student =  dao.getSingleStudentsData(sapId);
			//StudentBean studentRegistrationData = dao.getStudentRegistrationData(sapId); need to discuss with sanket sir
			StudentAcadsBean studentRegistrationData = new StudentAcadsBean();
			
			try{
				studentRegistrationData = dao.getStudentRegistrationDataNew(sapId);
			}catch(NullPointerException e){
				  
			}
			
			if(studentRegistrationData == null){
				return new ResponseEntity<List<SessionDayTimeAcadsBean>>(scheduledSessionList, headers,  HttpStatus.OK);
			}

			//For Old students incomplete data//
			//Take program from Registration data and not Student data. 
			student.setProgram(studentRegistrationData.getProgram());
			student.setSem(studentRegistrationData.getSem());
			String year = studentRegistrationData.getYear();
			String month =studentRegistrationData.getMonth();
		
			//ArrayList<String> subjects = getSubjectsForStudent(student);
			ArrayList<String> subjects = studentCourseMappingService.getCurrentCycleSubjectsForSessions(student.getSapid(),month,year,student.getProgram());
			
			//Commented By Somesh as Added session configurable
			scheduledSessionList.addAll(dao.getScheduledSessionForStudentsByCPSIdV1(student,year,month, subjects));
			
			if(!"BBA".equalsIgnoreCase(student.getProgram()) && !"B.Com".equalsIgnoreCase(student.getProgram())){
				//Commented By Somesh as Added session configurable
				//scheduledSessionList.addAll(dao.getScheduledSessionForStudents(subjects,student,year,month));
				
				commonscheduledSessionList = (ArrayList<SessionDayTimeAcadsBean>)dao.getCommonSessionsSemesterBased(student.getSem(),student.getProgram(),student.getConsumerProgramStructureId());
				eventSessionList = (ArrayList<SessionDayTimeAcadsBean>)dao.getEventsRegisteredByStudent(sapId);
			}else {
				//Get Common Sessions for UG
				commonscheduledSessionList = (ArrayList<SessionDayTimeAcadsBean>)dao.getCommonSessionsSemesterBasedForUG(student.getSem(),student.getProgram(),student.getConsumerProgramStructureId());
			}
				
			if(commonscheduledSessionList!=null && commonscheduledSessionList.size()>0){
				scheduledSessionList.addAll(commonscheduledSessionList);
			}
			
			if(eventSessionList!=null && eventSessionList.size()>0){
				scheduledSessionList.addAll(eventSessionList);
			}
			
			if(scheduledSessionList != null && scheduledSessionList.size() != 0){
				//scheduledSessionList = getVideosForSessionList(scheduledSessionList,studentRegistrationData);
				return new ResponseEntity<List<SessionDayTimeAcadsBean>>(scheduledSessionList, headers,  HttpStatus.OK);
			}
			
			return new ResponseEntity<List<SessionDayTimeAcadsBean>>(scheduledSessionList, headers,  HttpStatus.OK);
		
		}catch(Exception e){
			  
			return new ResponseEntity<List<SessionDayTimeAcadsBean>>(scheduledSessionList, headers,  HttpStatus.OK);
		}	
	}
	
	@PostMapping(value = "/studentTimeTableUpcomingHome", consumes = "application/json", produces = "application/json")
	public  ResponseEntity<List<SessionDayTimeAcadsBean>> mstudentTimeTableUpcomingHome(@RequestBody StudentAcadsBean input) {
		
		HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Type", "application/json");
        TimeTableDAO dao = (TimeTableDAO)act.getBean("timeTableDAO");
        List<SessionDayTimeAcadsBean> finalScheduledSessionList = new ArrayList<SessionDayTimeAcadsBean>();
        ArrayList<SessionDayTimeAcadsBean> scheduledSessionList = new ArrayList<SessionDayTimeAcadsBean>();
        ArrayList<SessionDayTimeAcadsBean> allSessionsByCourseMapping = new ArrayList<SessionDayTimeAcadsBean>();
        
        try {
        	String sapId = input.getSapid();
			StudentAcadsBean student =  dao.getSingleStudentsData(sapId);
			StudentAcadsBean studentRegistrationData = new StudentAcadsBean();
			
			if("EPBM".equalsIgnoreCase(student.getProgram()) || "MPDV".equalsIgnoreCase(student.getProgram())) {
				studentRegistrationData = dao.getStudentRegistrationDataForExecutive(sapId);			
			}else{
				studentRegistrationData = timeTableService.checkStudentRegistration(sapId, student);
			}
			/**
			 * Commented by Somesh ( As Waived in subject directly coming from Student Course Mapping Table)
			 */
			/*boolean isCourseMappingAvailable = dao.isCourseMappingApplicableForCurrentAcadCycle(student.getSapid());
			
			if(studentRegistrationData == null){
				if (isCourseMappingAvailable) {
					//Even if student not register for current cycle Waived-in subject is applicable
					scheduledSessionList = dao.getAllSessionsByCourseMappingForUpcoming(student.getSapid());
					return new ResponseEntity<List<SessionDayTimeAcadsBean>>(scheduledSessionList, headers,  HttpStatus.OK);
				}
				return new ResponseEntity<List<SessionDayTimeAcadsBean>>(scheduledSessionList, headers,  HttpStatus.OK);
			}
			*/
			//If Registration is Blank, then session list will be empty
			if(studentRegistrationData == null){
				return new ResponseEntity<List<SessionDayTimeAcadsBean>>(scheduledSessionList, headers,  HttpStatus.OK);
			}
			//For Old students incomplete data//
			//Take program from Registration data and not Student data. 
			student.setProgram(studentRegistrationData.getProgram());
			student.setSem(studentRegistrationData.getSem());
			String year = studentRegistrationData.getYear();
			String month = studentRegistrationData.getMonth();
			
			//ArrayList<String> subjects = getSubjectsForStudent(student);
			ArrayList<String> subjects = new ArrayList<String>();
			if (TIMEBOUND_PORTAL_LIST.contains(student.getConsumerProgramStructureId())) 
				subjects = getSubjectsForStudent(student);
			else
				subjects = studentCourseMappingService.getCurrentCycleSubjectsForSessions(student.getSapid(),month,year,student.getProgram());
			
			//Remove WaiveOff Subject from applicable Subject list
			/*ArrayList<String> waivedOffSubjects = studentService.mgetWaivedOffSubjects(student);
			subjects.removeAll(waivedOffSubjects);*/
			
			if("EPBM".equalsIgnoreCase(student.getProgram()) || "MPDV".equalsIgnoreCase(student.getProgram())) { 
				scheduledSessionList.addAll(dao.getScheduledSessionForStudentsForExecutive(subjects,student,year,month));
			}else {
				ArrayList<SessionDayTimeAcadsBean> commonscheduledSessionList = new ArrayList<SessionDayTimeAcadsBean>();
				if (TIMEBOUND_PORTAL_LIST.contains(student.getConsumerProgramStructureId())) {
					commonscheduledSessionList = dao.getCommonSessionsForMBAWx(student, year, month, "Upcoming");
					
					for (SessionDayTimeAcadsBean bean : commonscheduledSessionList) {
						String sessionIds="33139,33140,33141,33142,33143,33144";
						if (sessionIds.contains(bean.getId())) {
							bean.setSubject("Ascend: A Masterclass Series");
							bean.setSessionName("Topic 5");
						}
					}
				}else if(!"BBA".equalsIgnoreCase(student.getProgram()) && !"B.Com".equalsIgnoreCase(student.getProgram())){
					commonscheduledSessionList = dao.getCommonSessionsSemesterBasedFromToday(studentRegistrationData.getSem(),studentRegistrationData.getProgram(),student.getConsumerProgramStructureId());
				}else if("BBA".equalsIgnoreCase(student.getProgram()) || "B.Com".equalsIgnoreCase(student.getProgram())) {
					//Get Common Sessions for UG
					commonscheduledSessionList = (ArrayList<SessionDayTimeAcadsBean>)dao.getCommonSessionsSemesterBasedForUG(student.getSem(),student.getProgram(),student.getConsumerProgramStructureId());
				}
				
				/* Commented by Somesh ( As Waived in subject directly coming from Student Course Mapping Table) */
				//allSessionsByCourseMapping = dao.getAllSessionsByCourseMappingForUpcoming(student.getSapid());
				
				if(commonscheduledSessionList!=null && commonscheduledSessionList.size()>0){
					scheduledSessionList.addAll(commonscheduledSessionList);
				}
				
				if (allSessionsByCourseMapping != null && allSessionsByCourseMapping.size() > 0) {
					scheduledSessionList.addAll(allSessionsByCourseMapping);
				}
				
				scheduledSessionList.addAll(dao.getUpcomingScheduledSessionForStudentsByCPSIdV2(student, subjects));
				
				//Added for sorting
				if(scheduledSessionList.size() > 0) {
					Collections.sort(scheduledSessionList, new Comparator<SessionDayTimeAcadsBean>() {
						@Override
						public int compare(SessionDayTimeAcadsBean sBean1, SessionDayTimeAcadsBean sBean2) {
							return sBean1.getDate().compareTo(sBean2.getDate());
						}
					});
				}

				//If Session list is > 10 then send only 1st 10 sessions
				if (scheduledSessionList.size() > 10) {
					finalScheduledSessionList = scheduledSessionList.subList(0, 10);
				}else {
					finalScheduledSessionList = scheduledSessionList;
				}
			}
			return new ResponseEntity<List<SessionDayTimeAcadsBean>>(finalScheduledSessionList, headers, HttpStatus.OK);
			
		} catch (Exception e) {
			  
		}
        
        return new ResponseEntity<List<SessionDayTimeAcadsBean>>(finalScheduledSessionList, headers, HttpStatus.OK);
	}
	
	public ArrayList<ProgramSubjectMappingAcadsBean> getProgramSubjectMappingList(){
		if(this.programSubjectMappingList == null || this.programSubjectMappingList.size() == 0){
			TimeTableDAO dao = (TimeTableDAO)act.getBean("timeTableDAO");
			this.programSubjectMappingList = dao.getProgramSubjectMappingList();
		}
		return programSubjectMappingList;
	}



	private ArrayList<String> getSubjectsForStudent(StudentAcadsBean student) {
		ArrayList<ProgramSubjectMappingAcadsBean> programSubjectMappingList = getProgramSubjectMappingList();
		ArrayList<String> subjects = new ArrayList<>();
		for (int i = 0; i < programSubjectMappingList.size(); i++) {
			ProgramSubjectMappingAcadsBean bean = programSubjectMappingList.get(i);

			if(
					bean.getPrgmStructApplicable().equals(student.getPrgmStructApplicable()) 
					&& bean.getProgram().equals(student.getProgram())
					&& bean.getSem().equals(student.getSem())
					&& !student.getWaivedOffSubjects().contains(bean.getSubject())//Subjects has not already cleared it
					){
				//Added temporary for PD - WM project lecture
				if (student.getProgram().equalsIgnoreCase("PD - WM") && bean.getSubject().equalsIgnoreCase("Module 4 - Project")) {
					subjects.add("Project");
				}else {
					subjects.add(bean.getSubject());
				}
			}
		}
		return subjects;
	}
	
	@RequestMapping(value = "/viewTimeTableFilter", method = {RequestMethod.POST})
	public ResponseEntity<List<SessionDayTimeAcadsBean>> viewTimeTableFilter(HttpServletRequest request, @RequestBody SessionDayTimeAcadsBean bean) {
		List<SessionDayTimeAcadsBean> allsessionList = (List<SessionDayTimeAcadsBean>) request.getSession().getAttribute("allsessions");
	    
		allsessionList =  timeTableService.getScheduledSessionPageFilterBySubjectCodeId(allsessionList, bean.getSubjectCodeId());
		
		allsessionList =  timeTableService.getScheduledSessionPageFilterByProgramId(allsessionList, bean.getProgramId());
		
		allsessionList =  timeTableService.getScheduledSessionPageFilterBySem(allsessionList, bean.getSem());
		
		allsessionList =  timeTableService.getScheduledSessionPageFilterByTrack(allsessionList, bean.getTrack());
		 
	    return new ResponseEntity<List<SessionDayTimeAcadsBean>>(allsessionList,HttpStatus.OK);
	}


}


