package com.nmims.controllers;


import bookingservice.wsdl.*;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.nmims.beans.*;
import com.nmims.daos.*;
import com.nmims.helpers.*;
import com.nmims.util.ContentUtil;

import org.apache.commons.lang3.StringUtils;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.http.*;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import com.google.gson.JsonObject;
import com.nmims.beans.ConsumerProgramStructureAcads;
import com.nmims.beans.EndPointBean;
import com.nmims.beans.FacultyAcadsBean;
import com.nmims.beans.FacultyCourseMappingBean;
import com.nmims.beans.FileAcadsBean;
import com.nmims.beans.MailAcadsBean;
import com.nmims.beans.PageAcads;
import com.nmims.beans.PersonAcads;
import com.nmims.beans.Post;
import com.nmims.beans.ProgramSubjectMappingAcadsBean;
import com.nmims.beans.SessionDayTimeAcadsBean;
import com.nmims.beans.StudentAcadsBean;
import com.nmims.beans.VideoContentAcadsBean;
import com.nmims.beans.WebinarPollsBean;
import com.nmims.beans.WebinarPollsListBean;
import com.nmims.beans.WebinarPollsQuestionsBean;
import com.nmims.daos.ConferenceDAO;
import com.nmims.daos.ContentDAO;
import com.nmims.daos.FacultyDAO;
import com.nmims.daos.LDAPDao;
import com.nmims.daos.NotificationDAO;
import com.nmims.daos.TimeTableDAO;
import com.nmims.daos.VideoContentDAO;
import com.nmims.helpers.AESencrp;
import com.nmims.helpers.ConferenceBookingClient;
import com.nmims.helpers.ExcelHelper;
import com.nmims.helpers.MailSender;
import com.nmims.helpers.SMSSender;
import com.nmims.helpers.WebExMeetingManager;
import com.nmims.helpers.ZoomManager;
import com.nmims.services.SchedulingService;

import bookingservice.wsdl.BandwidthOverride;
import bookingservice.wsdl.Conference;
import bookingservice.wsdl.ConferenceType;
import bookingservice.wsdl.EncryptionRequested;
import bookingservice.wsdl.ExtendOptionRequested;
import bookingservice.wsdl.ExternalConference;
import bookingservice.wsdl.WebEx;


@Controller
@CrossOrigin(origins = "*", allowedHeaders = "*")
@RequestMapping("/admin")
public class SchedulingController extends BaseController {


	@Autowired(required = false)
	ApplicationContext act;

	
	@Autowired
	private SMSSender smsSender;
	@Autowired
	private NotificationDAO notificationDAO;
	@Autowired
	private ConferenceDAO conferenceBookingDAO;
	@Autowired
	private ConferenceBookingClient conferenceBookingClient;
	@Autowired
	private WebExMeetingManager webExManager;
	@Autowired
	private ZoomManager zoomManger;
	@Autowired
	private SchedulingService schedulingService;
	@Value("${SERVER}")
	private String SERVER;

	@Value("${ENVIRONMENT}")
	private String ENVIRONMENT;

	@Value("${WEB_EX_API_URL}")
	private String WEB_EX_API_URL;

	@Value("${WEB_EX_LOGIN_API_URL}")
	private String WEB_EX_LOGIN_API_URL;

	@Value("${WEBEX_ID}")
	private String WEBEX_ID;

	@Value("${WEBEX_PASS}")
	private String WEBEX_PASS;

	@Value("${SERVER_PATH}")
	private String SERVER_PATH;
	
	@Value( "${CURRENT_ACAD_MONTH}" )
	private String CURRENT_ACAD_MONTH;

	@Value( "${CURRENT_ACAD_YEAR}" )
	private String CURRENT_ACAD_YEAR;

	@Value("${PARALLEL_SESSION1_HOSTID}")
	private String PARALLEL_SESSION1_HOSTID;
	@Value("${PARALLEL_SESSION1_HOSTPASSWORD}")
	private String PARALLEL_SESSION1_HOSTPASSWORD;

	@Value("${PARALLEL_SESSION2_HOSTID}")
	private String PARALLEL_SESSION2_HOSTID;
	@Value("${PARALLEL_SESSION2_HOSTPASSWORD}")
	private String PARALLEL_SESSION2_HOSTPASSWORD;

	@Value("${PARALLEL_SESSION3_HOSTID}")
	private String PARALLEL_SESSION3_HOSTID;
	@Value("${PARALLEL_SESSION3_HOSTPASSWORD}")
	private String PARALLEL_SESSION3_HOSTPASSWORD;

	@Value("${ZOOM_LOGIN_API_URL}")
	private String ZOOM_LOGIN_API_URL;

	@Value("#{'${ACAD_YEAR_LIST}'.split(',')}")
	private List<String> ACAD_YEAR_LIST;

	@Autowired
	SessionDayTimeAcadsBean sessionQA;
	
	private static final Logger loggerForSessionSMSs = LoggerFactory.getLogger("session_SMS");
	private static final Logger loggerForSessionScheduling = LoggerFactory.getLogger("sessionSchedulingService");
	
	// Map Of FacultyID and facultyFull name//

	private HashMap<String, FacultyAcadsBean> mapOfFacultyIdAndFacultyRecord = null;
	private HashMap<String, VideoContentAcadsBean> mapOfSessionIdAndVideoContentRecord = null;
	private ArrayList<String> programList = null;
	private ArrayList<String> subjectList = null;
	private ArrayList<String> facultyList = null;
	private ArrayList<EndPointBean> endPointList = null;
	private ArrayList<String> locationList = null;
	private ArrayList<String> subjectCodeList = null;
	private Map<String, String> subjectCodeMap = null;
	private ArrayList<ConsumerProgramStructureAcads> consumerTypesList = null;
	private final int pageSize = 10;
	private static final Logger logger = LoggerFactory.getLogger(SchedulingController.class);
	private ArrayList<String> yearList = new ArrayList<String>(Arrays.asList("2015", "2016", "2017"));
	private HashMap<String, ProgramSubjectMappingAcadsBean> subjectProgramMap = null;

	private HashMap<String, ProgramSubjectMappingAcadsBean> semSubjectNProgramSubjectBean = new HashMap<>();

	private ArrayList<String> studentSubjectListOfHavingSaSAndOtherProgramActive = null;
	private ArrayList<String> studentsSASSubjectListOfHavingSaSAndOtherProgramActive = null;
	private ArrayList<String> sasSubjectsList = null;
	private String endDate = null;
	
	private List<String> trackList = Arrays.asList( "Weekend Batch - Slow Track", "Weekend Batch - Fast Track", "Weekend Batch",  
													"WeekDay Batch", "WeekDay Batch - Slow Track", "WeekDay Batch - Fast Track",
													"Weekend Slow - Track 1", "Weekday Slow - Track 2", "Weekend Slow - Track 3",
													"Weekend Fast - Track 4", "Weekday Fast - Track 5","Weekday Batch - Track 1", 
													"Weekday Batch - Track 2", "Sem I - All Week - Track 5", "Sem II - All Week");

	private List<String> semList = Arrays.asList("1", "2", "3", "4", "5", "6");
	private List<String> weekDays = Arrays.asList("Monday", "Tuesday", "Wednesday", "Thursday", "Friday");
	private List<String> weekEndDays = Arrays.asList("Saturday", "Sunday");
	private List<String> allWeekDays = Arrays.asList("Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday", "Sunday");
	
	private List<SessionDayTimeAcadsBean> masterKeys = null;
	private List<SessionDayTimeAcadsBean> masterKeysWithSubjectCodes = null;
	private ArrayList<String> sessionTypeList = null;
    private Map<String, String> sessionTypesMap = null;
	
	public HashMap<String, ProgramSubjectMappingAcadsBean> getSemSubjectNProgramSubjectBeanMap() {
		if (this.semSubjectNProgramSubjectBean == null || this.semSubjectNProgramSubjectBean.size() == 0) {
			ContentDAO cDao = (ContentDAO) act.getBean("contentDAO");
			ArrayList<ProgramSubjectMappingAcadsBean> programSubjectMappingList = cDao.getProgramSubjectMappingList();

			semSubjectNProgramSubjectBean = new HashMap<>();
			for (int i = 0; i < programSubjectMappingList.size(); i++) {
				ProgramSubjectMappingAcadsBean bean = programSubjectMappingList.get(i);
				semSubjectNProgramSubjectBean.put(bean.getSem() + "-" + bean.getSubject(), bean);
			}
		}
		return semSubjectNProgramSubjectBean;
	}

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

	public ArrayList<ProgramSubjectMappingAcadsBean> getSubjectProgramList() {
		TimeTableDAO dao = (TimeTableDAO) act.getBean("timeTableDAO");
		ArrayList<ProgramSubjectMappingAcadsBean> subjectProgramList = dao.getSubjectProgramList();
		return subjectProgramList;
	}

	public ArrayList<String> getSubjectList() {
		if (this.subjectList == null) {
			TimeTableDAO dao = (TimeTableDAO) act.getBean("timeTableDAO");
			this.subjectList = dao.getAllSubjects();
		}
		return subjectList;
	}

	public ArrayList<String> getSubjectCodeList() {
		if (this.subjectCodeList == null) {
			TimeTableDAO dao = (TimeTableDAO) act.getBean("timeTableDAO");
			this.subjectCodeList = dao.getAllSubjectCodes();
		}
		return subjectCodeList;
	}

	public Map<String, String> getsubjectCodeMap() {
		if (this.subjectCodeMap == null) {
			TimeTableDAO dao = (TimeTableDAO) act.getBean("timeTableDAO");
			this.subjectCodeMap = dao.getsubjectCodeMap();
		}
		return subjectCodeMap;
	}
	
	public ArrayList<String> getLocationList() {
		if (this.locationList == null) {
			TimeTableDAO dao = (TimeTableDAO) act.getBean("timeTableDAO");
			this.locationList = dao.getAllLocations();
		}
		return locationList;
	}

	public ArrayList<ConsumerProgramStructureAcads> getConsumerTypesList() {
		if (this.consumerTypesList == null) {
			TimeTableDAO dao = (TimeTableDAO) act.getBean("timeTableDAO");
			this.consumerTypesList = dao.getConsumerTypes();
		}
		return consumerTypesList;
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

	public ArrayList<String> getStudentSubjectListOfHavingSaSAndOtherProgramActive(String month, String year) {
		if (this.studentSubjectListOfHavingSaSAndOtherProgramActive == null) {
			TimeTableDAO dao = (TimeTableDAO) act.getBean("timeTableDAO");
			this.studentSubjectListOfHavingSaSAndOtherProgramActive = dao
					.getStudentsSubjectListOfHavingSaSAndOtherProgramActive(month, year);
		}
		return studentSubjectListOfHavingSaSAndOtherProgramActive;
	}

	public ArrayList<String> getStudentsSASSubjectListOfHavingSaSAndOtherProgramActive(String month, String year) {
		if (this.studentsSASSubjectListOfHavingSaSAndOtherProgramActive == null) {
			TimeTableDAO dao = (TimeTableDAO) act.getBean("timeTableDAO");
			this.studentsSASSubjectListOfHavingSaSAndOtherProgramActive = dao
					.getStudentsSASSubjectListOfHavingSaSAndOtherProgramActive(month, year);
		}
		return studentsSASSubjectListOfHavingSaSAndOtherProgramActive;
	}

	public ArrayList<String> getSasSubjectsList() {
		if (this.sasSubjectsList == null) {
			TimeTableDAO dao = (TimeTableDAO) act.getBean("timeTableDAO");
			this.sasSubjectsList = dao.getAllSASSubjects();
		}
		return sasSubjectsList;
	}
	
	public String getEndDateForCycle() {
		if (this.endDate == null) {
			TimeTableDAO dao = (TimeTableDAO) act.getBean("timeTableDAO");
			this.endDate = dao.getAcademicCalendarEndDate();
		}
		return endDate;
	}

	public List<String> convertStringtoList(String commaSeparatedStr) {
		String[] commaSeparatedArr = commaSeparatedStr.split("\\s*,\\s*");
		List<String> result = new ArrayList<String>(Arrays.asList(commaSeparatedArr));
		return result;
	}
	
	public List<SessionDayTimeAcadsBean> getMasterKeys(){
		if (this.masterKeys == null) {
			TimeTableDAO dao = (TimeTableDAO) act.getBean("timeTableDAO");
			this.masterKeys = dao.getConsumerProgramStructureData();
		}
		return masterKeys;
	}
	
	public List<SessionDayTimeAcadsBean> getMasterKeysWithSubjectCodes(){
		if (this.masterKeysWithSubjectCodes == null) {
			TimeTableDAO dao = (TimeTableDAO) act.getBean("timeTableDAO");
			this.masterKeysWithSubjectCodes = dao.getConsumerProgramStructureSubjectCodeData();
		}
		return masterKeysWithSubjectCodes;
	}
	
	public HashMap<String, VideoContentAcadsBean> getMapOfSessionIdAndVideoContentRecord() {
		VideoContentDAO dao = (VideoContentDAO) act.getBean("videoContentDAO");
		List<VideoContentAcadsBean> listOfAllVideoContent = dao.getAllVideoContentList();
//		if (this.mapOfSessionIdAndVideoContentRecord == null) {
			this.mapOfSessionIdAndVideoContentRecord = new HashMap<String, VideoContentAcadsBean>();
			for (VideoContentAcadsBean video : listOfAllVideoContent) {
				this.mapOfSessionIdAndVideoContentRecord.put(String.valueOf(video.getSessionId()), video);
			}
//		}
		return mapOfSessionIdAndVideoContentRecord;

	}
	
	private HashMap<String, FacultyAcadsBean> mapOfFacultyIdAndFacultyDetails() {
		FacultyDAO facultyDao = (FacultyDAO) act.getBean("facultyDAO");
		ArrayList<FacultyAcadsBean> listOfAllFaculties = facultyDao.getAllFacultyRecords();
		HashMap<String, FacultyAcadsBean> mapOfFacultyIdAndFacultyRecord = new HashMap<String, FacultyAcadsBean>();
			for (FacultyAcadsBean faculty : listOfAllFaculties) {
				mapOfFacultyIdAndFacultyRecord.put(faculty.getFacultyId(), faculty);
			}
		return mapOfFacultyIdAndFacultyRecord;

	}
	
	public Map<String, String> getSessionTypesMap() {
		if (this.sessionTypesMap == null) {
			TimeTableDAO dao = (TimeTableDAO) act.getBean("timeTableDAO");
			this.sessionTypesMap = dao.getSessionTypesMap();
		}
		return sessionTypesMap;
	}
	
	public ArrayList<String> getSessionTypeList() {
		if (this.sessionTypeList == null) {
			TimeTableDAO dao = (TimeTableDAO) act.getBean("timeTableDAO");
			this.sessionTypeList = dao.getAllSessionTypes();
		}
		return sessionTypeList;
	}

	@RequestMapping(value = "/autoScheduleForm", method = { RequestMethod.GET, RequestMethod.POST })
	public ModelAndView autoScheduleForm(HttpServletRequest request, HttpServletResponse respnse, Model m) {

		try {

			TimeTableDAO dao = (TimeTableDAO) act.getBean("timeTableDAO");
			// Add logic of current year-month and logic of not taking session which are
			// alrady scheduled
			ArrayList<FacultyCourseMappingBean> sessionsToSchedule = dao.getAllSessionsToSchedule();
			m.addAttribute("pendingRecordsCount", sessionsToSchedule.size());

			List<SessionDayTimeAcadsBean> pendingConferenceList = conferenceBookingDAO.getPendingConferenceList();
			m.addAttribute("pendingConferenceCount", pendingConferenceList.size());
			m.addAttribute("pendingConferenceList", pendingConferenceList);

		} catch (Exception e) {
			request.setAttribute("error", "true");
			request.setAttribute("errorMessage", e.getMessage());
		}

		return new ModelAndView("autoScheduleSessions");
	}

	@RequestMapping(value = "/autoSchedule", method = { RequestMethod.GET, RequestMethod.POST })
	public ModelAndView autoSchedule(HttpServletRequest request, HttpServletResponse respnse, Model m) {
		ModelAndView modelnView = new ModelAndView("autoScheduleSessions");
		try {
			TimeTableDAO dao = (TimeTableDAO) act.getBean("timeTableDAO");
			// Add logic of current year-month and logic of not taking session which are
			// already scheduled
			ArrayList<FacultyCourseMappingBean> sessionsToSchedule = dao.getAllSessionsToSchedule();
			for (int i = 0; i < sessionsToSchedule.size(); i++) {
				FacultyCourseMappingBean bean = sessionsToSchedule.get(i);
				String id = bean.getId();
				String faculty1 = bean.getFacultyIdPref1();
				String faculty2 = bean.getFacultyIdPref2();
				String faculty3 = bean.getFacultyIdPref3();
				String subject = bean.getSubject();
				String session = bean.getSession();
				String duration = bean.getDuration();
				String isAdditionalSession = bean.getIsAdditionalSession();
				List<SessionDayTimeAcadsBean> availableDates = dao.getAvailableDatesForFaculty(faculty1, subject, isAdditionalSession);
				if (availableDates != null && availableDates.size() > 0) {
					SessionDayTimeAcadsBean sessionBean = availableDates.get(0);
					sessionBean.setFacultyId(faculty1);
					sessionBean.setSubject(subject);
					sessionBean.setSessionName(session);
					sessionBean.setDuration(duration);
					allocateAvailableRoom(sessionBean);
					dao.insertSession(sessionBean, id);

				} else {
					availableDates = dao.getAvailableDatesForFaculty(faculty2, subject, isAdditionalSession);
					if (availableDates != null && availableDates.size() > 0) {
						SessionDayTimeAcadsBean sessionBean = availableDates.get(0);
						sessionBean.setFacultyId(faculty2);
						sessionBean.setSubject(subject);
						sessionBean.setSessionName(session);

						allocateAvailableRoom(sessionBean);
						dao.insertSession(sessionBean, id);


					} else {
						availableDates = dao.getAvailableDatesForFaculty(faculty3, subject, isAdditionalSession);

						if (availableDates != null && availableDates.size() > 0) {
							SessionDayTimeAcadsBean sessionBean = availableDates.get(0);
							sessionBean.setFacultyId(faculty3);
							sessionBean.setSubject(subject);
							sessionBean.setSessionName(session);

							allocateAvailableRoom(sessionBean);

							dao.insertSession(sessionBean, id);

						} else {
							throw new Exception("No date available for " + subject + " : " + session);
						}
					}
				}
			}

			sessionsToSchedule = dao.getAllSessionsToSchedule();
			modelnView.addObject("pendingRecordsCount", sessionsToSchedule.size());
			request.setAttribute("success", "true");
			request.setAttribute("successMessage", "All pending sessions scheduled successfully");
		} catch (Exception e) {
			  
			request.setAttribute("error", "true");
			request.setAttribute("errorMessage", e.getMessage());
		}

		return modelnView;
	}

	// ArrayList<EndPointBean> endPointList = getEndPointList();
	private void allocateAvailableRoom(SessionDayTimeAcadsBean sessionBean) {

		TimeTableDAO dao = (TimeTableDAO) act.getBean("timeTableDAO");
		ArrayList<EndPointBean> endPointList = dao.getAvailableRoom(sessionBean);

		if (endPointList.size() > 0 && endPointList != null) {
			EndPointBean firstAvailableRoom = endPointList.get(0);
			sessionBean.setRoom(firstAvailableRoom.getName());
			sessionBean.setHostId(firstAvailableRoom.getHostId());
			sessionBean.setHostPassword(firstAvailableRoom.getHostPassword());
		} else {

			sessionBean.setErrorRecord(true);
			sessionBean.setErrorMessage("<br>  Room not Available for Date :" + sessionBean.getDate());

		}

	}

	private SessionDayTimeAcadsBean allocateAvailableRoomForBatchUpload(SessionDayTimeAcadsBean sessionBean) {

		TimeTableDAO dao = (TimeTableDAO) act.getBean("timeTableDAO");
		//ArrayList<String> sem1SubjectList = dao.getAllSubjectsSemWiseAndConsumerType("1",sessionBean.getCorporateName());
		ArrayList<String> coreSubjectList = dao.getAllCoreSubjects();
		ArrayList<EndPointBean> endPointList = dao.getAvailableRoomByLoaction2(sessionBean.getDate(),
				sessionBean.getStartTime(), sessionBean.getEndTime(), sessionBean.getFacultyLocation(), sessionBean.getSessionType());
		int endPointListSize = endPointList != null ? endPointList.size() : 0;
		EndPointBean firstAvailableRoom = new EndPointBean();

		String date = sessionBean.getDate();
		String time = sessionBean.getStartTime();
		Map<String, Integer> locationCapacityMap = new HashMap<>();
		int capacityOFHostLocation = dao.getCapacityOfLocation(sessionBean.getFacultyLocation());
		int noOfSessionAtSameDateTimeLocation = dao.getNoOfSessionAtSameDateTimeLocation(date, time, sessionBean.getFacultyLocation(), sessionBean.getId());
		int capacityForHost = capacityOFHostLocation - noOfSessionAtSameDateTimeLocation;
		if (capacityForHost < 1) {
			sessionBean.setHostId("1");// given garbage value to avoid null pointer in further steps, this value will not be saved in db.
			sessionBean.setErrorRecord(true);
			sessionBean.setErrorMessage(sessionBean.getErrorMessage()
					+ "<br>  Room not Available for Host Faculty on Date :" + sessionBean.getDate() + " Start Time : "
					+ sessionBean.getStartTime() + " at " + sessionBean.getFacultyLocation());
		//If available capacity is more than 0, then check further
		} else {
			//If If available capacity and host is blank then allocate new HostId
			if (StringUtils.isBlank(sessionBean.getHostId())) {
				if (endPointListSize > 0) {

					if (!coreSubjectList.contains(sessionBean.getSubject()) || "Y".equalsIgnoreCase(sessionBean.getHasModuleId())) {
						ArrayList<EndPointBean> endPointsForSem1 = new ArrayList<EndPointBean>();
						for (EndPointBean eb : endPointList) {
							if ("Zoom1@nmims.edu".equalsIgnoreCase(eb.getZoomUID()) || "Zoom2@nmims.edu".equalsIgnoreCase(eb.getZoomUID()) ||
									"Zoom3@nmims.edu".equalsIgnoreCase(eb.getZoomUID()) || "Zoom4@nmims.edu".equalsIgnoreCase(eb.getZoomUID())) {
								endPointsForSem1.add(eb);
							}
						}
						endPointList.removeAll(endPointsForSem1);
					}else{
						ArrayList<EndPointBean> endPointsForSem1 = new ArrayList<EndPointBean>();
						for (EndPointBean eb : endPointList) {
							if ("Zoom1@nmims.edu".equalsIgnoreCase(eb.getZoomUID()) || "Zoom2@nmims.edu".equalsIgnoreCase(eb.getZoomUID()) ||
									"Zoom3@nmims.edu".equalsIgnoreCase(eb.getZoomUID()) || "Zoom4@nmims.edu".equalsIgnoreCase(eb.getZoomUID())) {
								endPointsForSem1.add(eb);
							}
						}
						endPointList = endPointsForSem1;
						
						//If Zoom1, Zoom2, Zoom3, Zoom4 not available then allocate any free zoom Id
						if (endPointList.size() <= 0) {
							endPointList = dao.getAvailableRoomByLoaction2(sessionBean.getDate(),
									sessionBean.getStartTime(), sessionBean.getEndTime(), sessionBean.getFacultyLocation(), sessionBean.getSessionType());
							
							/*
							sessionBean.setHostId("1");// given garbage value to avoid null pointer in further steps, this
														// value will not be saved in db.
							sessionBean.setErrorRecord(true);
							sessionBean.setErrorMessage(sessionBean.getErrorMessage()
									+ "Zoom Rooms Not available for Sem 1 Subject " + sessionBean.getSubject()
									+ ".On Date :" + sessionBean.getDate() + " Start Time : " + sessionBean.getStartTime());
							return sessionBean;
							*/
						}
					}
					
					//If all endpoints are occupied from zoom5 to zoom32
					//Check again for zoom1 to zoom4 if any host is free
					if (endPointList.size() == 0) {
						ArrayList<EndPointBean> endPointList2 = dao.getAvailableRoomByLoaction2(sessionBean.getDate(),
								sessionBean.getStartTime(), sessionBean.getEndTime(), sessionBean.getFacultyLocation(), sessionBean.getSessionType());
						if (endPointList2.size() == 0) {
							sessionBean.setHostId("1");
							sessionBean.setErrorRecord(true);
							sessionBean.setErrorMessage(sessionBean.getErrorMessage()
									+ "<br>  Room not Available for Host Faculty on Date :" + sessionBean.getDate()
									+ " Start Time : " + sessionBean.getStartTime() + " at " + sessionBean.getFacultyLocation());
							return sessionBean;
						}else {
							endPointList = endPointList2;
						}
					}
					
					firstAvailableRoom = endPointList.get(0);

					sessionBean.setRoom(firstAvailableRoom.getName());
					sessionBean.setHostId(firstAvailableRoom.getHostId());
					sessionBean.setHostPassword(firstAvailableRoom.getHostPassword());
					sessionBean.setHostKey(firstAvailableRoom.getZoomUID());
					locationCapacityMap.put(sessionBean.getFacultyLocation(), capacityForHost - 1);
	
				} else {
					// Removed getLeastAllocatedRoom() by pranit on 26 Dec 18 for zoom
					// If HostId is already exits 
					// given garbage value to avoid null pointer in further steps, this value will not be saved in db.
					sessionBean.setHostId("1");
					sessionBean.setErrorRecord(true);
					sessionBean.setErrorMessage(sessionBean.getErrorMessage()
							+ "<br>  Room not Available for Host Faculty on Date :" + sessionBean.getDate()
							+ " Start Time : " + sessionBean.getStartTime() + " at " + sessionBean.getFacultyLocation());
				}
			}
		}
		
		/******* Checking For Alt Host Start *******/
		
		//If any parallel session and altFacultyId added and altHostId is not allocated then allocate new altHostId
		if (!StringUtils.isBlank(sessionBean.getAltFacultyId()) && StringUtils.isBlank(sessionBean.getAltHostId())) {
			capacityOFHostLocation = dao.getCapacityOfLocation(sessionBean.getAltFacultyLocation());
			int capacityForAltFaculty = 0;
			if (locationCapacityMap.containsKey(sessionBean.getAltFacultyLocation())) {
				capacityForAltFaculty = locationCapacityMap.get(sessionBean.getAltFacultyLocation());
			} else {
				noOfSessionAtSameDateTimeLocation = dao.getNoOfSessionAtSameDateTimeLocation(date, time,
						sessionBean.getAltFacultyLocation(), sessionBean.getId());
				capacityForAltFaculty = capacityOFHostLocation - noOfSessionAtSameDateTimeLocation;
				locationCapacityMap.put(sessionBean.getAltFacultyLocation(), capacityForAltFaculty);
			}
			
			if (capacityForAltFaculty < 1) {
				// given garbage value to avoid null pointer in further steps, this value will not be saved in db.
				sessionBean.setAltHostId("2");
				sessionBean.setAltHostKey("2");
				sessionBean.setErrorRecord(true);
				sessionBean.setErrorMessage(sessionBean.getErrorMessage()
						+ "<br>  Room not Available for Parallel Faculty 1 on Date :" + sessionBean.getDate()
						+ " Start Time : " + sessionBean.getStartTime() + " at " + sessionBean.getAltFacultyLocation());
			} else {

				endPointList = dao.getAvailableRoomByLoaction2(sessionBean.getDate(), sessionBean.getStartTime(),
						sessionBean.getEndTime(), sessionBean.getAltFacultyLocation(), sessionBean.getSessionType());
				endPointListSize = endPointList != null ? endPointList.size() : 0;
				if (endPointListSize > 0) {
					firstAvailableRoom = endPointList.get(0);

					if (sessionBean.getHostId().equalsIgnoreCase(firstAvailableRoom.getHostId())
							&& sessionBean.getHostPassword().equalsIgnoreCase(firstAvailableRoom.getHostPassword())) {
						if (endPointListSize > 1) {
							firstAvailableRoom = endPointList.get(1);
							sessionBean.setAltHostId(firstAvailableRoom.getHostId());
							sessionBean.setAltHostKey(firstAvailableRoom.getZoomUID());
							sessionBean.setAltHostPassword(firstAvailableRoom.getHostPassword());
							locationCapacityMap.put(sessionBean.getAltFacultyLocation(), capacityForAltFaculty - 1);
						} else {
							// given garbage value to avoid null pointer in further steps, this value will not be saved in db.
							sessionBean.setAltHostId("2");
							sessionBean.setAltHostKey("2");
							sessionBean.setErrorRecord(true);
							sessionBean.setErrorMessage(sessionBean.getErrorMessage()+ "<br>  Room not Available for Parallel Faculty 1 on Date :"
									+ sessionBean.getDate() + " Start Time : " + sessionBean.getStartTime() + " at " + sessionBean.getAltFacultyLocation());
						}
					} else {
						sessionBean.setAltHostId(firstAvailableRoom.getHostId());
						sessionBean.setAltHostKey(firstAvailableRoom.getZoomUID());
						sessionBean.setAltHostPassword(firstAvailableRoom.getHostPassword());
						locationCapacityMap.put(sessionBean.getAltFacultyLocation(), capacityForAltFaculty - 1);
					}
				} else {
					// given garbage value to avoid null pointer in further steps, this value will not be saved in db.
					sessionBean.setAltHostId("2");
					sessionBean.setAltHostKey("2");
					sessionBean.setErrorRecord(true);
					sessionBean.setErrorMessage(sessionBean.getErrorMessage() + "<br>  Room not Available for Parallel Faculty 1 on Date :"
								+ sessionBean.getDate() + " Start Time : " + sessionBean.getStartTime() + " at " + sessionBean.getAltFacultyLocation());
				}
			}
		}
		
		/******* Checking For Alt Host End *******/
		
		/******* Checking For Alt Host 2 Start *******/
		
		//If any parallel 2 session and altFacultyId2 added and altHostId2 is not allocated then allocate new altHostId
		if (!StringUtils.isBlank(sessionBean.getAltFacultyId2()) && StringUtils.isBlank(sessionBean.getAltHostId2())) {
			capacityOFHostLocation = dao.getCapacityOfLocation(sessionBean.getAltFaculty2Location());
			int capacityForAltFaculty2 = 0;
			if (locationCapacityMap.containsKey(sessionBean.getAltFaculty2Location())) {
				capacityForAltFaculty2 = locationCapacityMap.get(sessionBean.getAltFaculty2Location());
			} else {
				noOfSessionAtSameDateTimeLocation = dao.getNoOfSessionAtSameDateTimeLocation(date, time,
						sessionBean.getAltFaculty2Location(), sessionBean.getId());
				capacityForAltFaculty2 = capacityOFHostLocation - noOfSessionAtSameDateTimeLocation;
				locationCapacityMap.put(sessionBean.getAltFaculty2Location(), capacityForAltFaculty2);
			}
			
			if (capacityForAltFaculty2 < 1) {
				// given garbage value to avoid null pointer in further steps, this value will not be saved in db.
				sessionBean.setAltHostId2("3");
				sessionBean.setAltHostKey2("3");
				sessionBean.setErrorRecord(true);
				sessionBean.setErrorMessage(sessionBean.getErrorMessage()
						+ "<br>  Room not Available for Parallel Faculty 2 on Date :" + sessionBean.getDate()
						+ " Start Time : " + sessionBean.getStartTime() + " at " + sessionBean.getAltFacultyLocation());
			} else {

				endPointList = dao.getAvailableRoomByLoaction2(sessionBean.getDate(), sessionBean.getStartTime(),
						sessionBean.getEndTime(), sessionBean.getAltFaculty2Location(), sessionBean.getSessionType());
				endPointListSize = endPointList != null ? endPointList.size() : 0;
				if (endPointListSize > 0) {
					firstAvailableRoom = endPointList.get(0);

					if (sessionBean.getHostId().equalsIgnoreCase(firstAvailableRoom.getHostId())
							&& sessionBean.getHostPassword().equalsIgnoreCase(firstAvailableRoom.getHostPassword())) {
						if (endPointListSize > 1) {
							firstAvailableRoom = endPointList.get(1);
							if (sessionBean.getAltHostId().equalsIgnoreCase(firstAvailableRoom.getHostId())
									&& sessionBean.getAltHostPassword()
											.equalsIgnoreCase(firstAvailableRoom.getHostPassword())) {
								if (endPointListSize > 2) {
									firstAvailableRoom = endPointList.get(2);
									sessionBean.setAltHostId2(firstAvailableRoom.getHostId());
									sessionBean.setAltHostKey2(firstAvailableRoom.getZoomUID());
									sessionBean.setAltHostPassword2(firstAvailableRoom.getHostPassword());
									locationCapacityMap.put(sessionBean.getAltFaculty2Location(),capacityForAltFaculty2 - 1);
								} else {
									// given garbage value to avoid null pointer in further steps, this value will not be saved in db.
									sessionBean.setAltHostId2("3");
									sessionBean.setAltHostKey2("3");
									sessionBean.setErrorRecord(true);
									sessionBean.setErrorMessage(sessionBean.getErrorMessage() + "<br>  Room not Available for Parallel Faculty 2 on Date :"
											+ sessionBean.getDate() + " Start Time : " + sessionBean.getStartTime() + " at " + sessionBean.getAltFacultyLocation());
								}
							} else {
								sessionBean.setAltHostId2(firstAvailableRoom.getHostId());
								sessionBean.setAltHostKey2(firstAvailableRoom.getZoomUID());
								sessionBean.setAltHostPassword2(firstAvailableRoom.getHostPassword());
								locationCapacityMap.put(sessionBean.getAltFaculty2Location(), capacityForAltFaculty2 - 1);
							}
						} else {
							// given garbage value to avoid null pointer in further steps, this value will not be saved in db.
							sessionBean.setAltHostId2("3");
							sessionBean.setAltHostKey2("3");
							sessionBean.setErrorRecord(true);
							sessionBean.setErrorMessage(sessionBean.getErrorMessage() + "<br>  Room not Available for Parallel Faculty 2 on Date :"
									+ sessionBean.getDate() + " Start Time : " + sessionBean.getStartTime() + " at " + sessionBean.getAltFacultyLocation());
						}
					} else if (sessionBean.getAltHostId().equalsIgnoreCase(firstAvailableRoom.getHostId())
							&& sessionBean.getAltHostPassword()
									.equalsIgnoreCase(firstAvailableRoom.getHostPassword())) {
						if (endPointListSize > 1) {
							firstAvailableRoom = endPointList.get(1);
							sessionBean.setAltHostId2(firstAvailableRoom.getHostId());
							sessionBean.setAltHostKey2(firstAvailableRoom.getZoomUID());
							sessionBean.setAltHostPassword2(firstAvailableRoom.getHostPassword());
							locationCapacityMap.put(sessionBean.getAltFaculty2Location(), capacityForAltFaculty2 - 1);
						} else {
							// given garbage value to avoid null pointer in further steps, this value will not be saved in db.
							sessionBean.setAltHostId2("3");
							sessionBean.setAltHostKey2("3");
							sessionBean.setErrorRecord(true);
							sessionBean.setErrorMessage(sessionBean.getErrorMessage()+ "<br>  Room not Available for Parallel Faculty 2 on Date :"
									+ sessionBean.getDate() + " Start Time : " + sessionBean.getStartTime() + " at "+ sessionBean.getAltFacultyLocation());
						}
					} else {
						sessionBean.setAltHostId2(firstAvailableRoom.getHostId());
						sessionBean.setAltHostKey2(firstAvailableRoom.getZoomUID());
						sessionBean.setAltHostPassword2(firstAvailableRoom.getHostPassword());
						locationCapacityMap.put(sessionBean.getAltFaculty2Location(), capacityForAltFaculty2 - 1);
					}
				} else {
					// given garbage value to avoid null pointer in further steps, this value will not be saved in db.
					sessionBean.setAltHostId2("3");
					sessionBean.setAltHostKey2("3");
					sessionBean.setErrorRecord(true);
					sessionBean.setErrorMessage(
					sessionBean.getErrorMessage() + "<br>  Room not Available for Parallel Faculty 2 on Date :" + sessionBean.getDate() + " Start Time : " 
									+ sessionBean.getStartTime() + " at " + sessionBean.getAltFaculty2Location());
				}
			}
		}
		
		/******* Checking For Alt Host 2 End *******/
		
		/******* Checking For Alt Host 2 Start *******/
		
		//If any parallel 3 session and altFacultyId3 added and altHostId3 is not allocated then allocate new altHostId
		if (!StringUtils.isBlank(sessionBean.getAltFacultyId3()) && StringUtils.isBlank(sessionBean.getAltHostId3())) {
			capacityOFHostLocation = dao.getCapacityOfLocation(sessionBean.getAltFaculty3Location());
			int capacityForAltFaculty3 = 0;
			if (locationCapacityMap.containsKey(sessionBean.getAltFaculty3Location())) {
				capacityForAltFaculty3 = locationCapacityMap.get(sessionBean.getAltFaculty3Location());
			} else {
				noOfSessionAtSameDateTimeLocation = dao.getNoOfSessionAtSameDateTimeLocation(date, time,
						sessionBean.getAltFaculty3Location(), sessionBean.getId());
				capacityForAltFaculty3 = capacityOFHostLocation - noOfSessionAtSameDateTimeLocation;
				locationCapacityMap.put(sessionBean.getAltFaculty3Location(), capacityForAltFaculty3);
			}
			if (capacityForAltFaculty3 < 1) {
				// given garbage value to avoid null pointer in further steps, this value will not be saved in db.
				sessionBean.setAltHostId3("4");
				sessionBean.setAltHostKey3("4");
				sessionBean.setErrorRecord(true);
				sessionBean.setErrorMessage(
						sessionBean.getErrorMessage() + "<br>  Room not Available for Parallel Faculty 3 on Date :"
								+ sessionBean.getDate() + " Start Time : " + sessionBean.getStartTime() + " at "
								+ sessionBean.getAltFaculty2Location());
			} else {

				endPointList = dao.getAvailableRoomByLoaction2(sessionBean.getDate(), sessionBean.getStartTime(),
						sessionBean.getEndTime(), sessionBean.getAltFaculty3Location(), sessionBean.getSessionType());
				endPointListSize = endPointList != null ? endPointList.size() : 0;
				if (endPointListSize > 0) {
					firstAvailableRoom = endPointList.get(3);

					// start
					if (sessionBean.getHostId().equalsIgnoreCase(firstAvailableRoom.getHostId())
							&& sessionBean.getHostPassword().equalsIgnoreCase(firstAvailableRoom.getHostPassword())) {
						if (endPointListSize > 1) {
							firstAvailableRoom = endPointList.get(1);
							if (sessionBean.getAltHostId().equalsIgnoreCase(firstAvailableRoom.getHostId())
									&& sessionBean.getAltHostPassword()
											.equalsIgnoreCase(firstAvailableRoom.getHostPassword())) {
								if (endPointListSize > 2) {
									firstAvailableRoom = endPointList.get(2);
									if (sessionBean.getAltHostId2().equalsIgnoreCase(firstAvailableRoom.getHostId())
											&& sessionBean.getAltHostPassword2()
													.equalsIgnoreCase(firstAvailableRoom.getHostPassword())) {
										if (endPointListSize > 3) {
											firstAvailableRoom = endPointList.get(3);
											sessionBean.setAltHostId3(firstAvailableRoom.getHostId());
											sessionBean.setAltHostKey3(firstAvailableRoom.getZoomUID());
											sessionBean.setAltHostPassword3(firstAvailableRoom.getHostPassword());
											locationCapacityMap.put(sessionBean.getAltFaculty3Location(),capacityForAltFaculty3);
										} else {
											// given garbage value to avoid null pointer in further steps, this value will not be saved in db.
											sessionBean.setAltHostId3("4");
											sessionBean.setAltHostKey3("4");
											sessionBean.setErrorRecord(true);
											sessionBean.setErrorMessage(sessionBean.getErrorMessage() + "<br>  Room not Available for Parallel Faculty 3 on Date :"
													+ sessionBean.getDate() + " Start Time : " +sessionBean.getStartTime() + " at "
													+ sessionBean.getAltFaculty2Location());
										}
									} else {
										sessionBean.setAltHostId3(firstAvailableRoom.getHostId());
										sessionBean.setAltHostKey3(firstAvailableRoom.getZoomUID());
										sessionBean.setAltHostPassword3(firstAvailableRoom.getHostPassword());
										locationCapacityMap.put(sessionBean.getAltFaculty3Location(), capacityForAltFaculty3);
									}

								} else {
									// given garbage value to avoid null pointer infurther steps, this value will not be saved in db.
									sessionBean.setAltHostId3("4");
									sessionBean.setAltHostKey3("4");
									sessionBean.setErrorRecord(true);
									sessionBean.setErrorMessage(sessionBean.getErrorMessage()
											+ "<br>  Room not Available for Parallel Faculty 3 on Date :" + sessionBean.getDate() + " Start Time : " 
											+ sessionBean.getStartTime() + " at " + sessionBean.getAltFacultyLocation());
								}
							} else {
								sessionBean.setAltHostId3(firstAvailableRoom.getHostId());
								sessionBean.setAltHostKey3(firstAvailableRoom.getZoomUID());
								sessionBean.setAltHostPassword3(firstAvailableRoom.getHostPassword());
								locationCapacityMap.put(sessionBean.getAltFaculty3Location(), capacityForAltFaculty3);
							}
						} else {
							// given garbage value to avoid null pointer in further steps, this value will not be saved in db.
							sessionBean.setAltHostId3("4");
							sessionBean.setAltHostKey3("4");
							sessionBean.setErrorRecord(true);
							sessionBean.setErrorMessage(sessionBean.getErrorMessage() + "<br>  Room not Available for Parallel Faculty 3 on Date :"
									+ sessionBean.getDate() + " Start Time : " + sessionBean.getStartTime() + " at " + sessionBean.getAltFacultyLocation());
						}
					} else if (sessionBean.getAltHostId().equalsIgnoreCase(firstAvailableRoom.getHostId())
							&& sessionBean.getAltHostPassword().equalsIgnoreCase(firstAvailableRoom.getHostPassword())) {
						if (endPointListSize > 1) {

							firstAvailableRoom = endPointList.get(1);
							if (sessionBean.getAltHostId2().equalsIgnoreCase(firstAvailableRoom.getHostId())
									&& sessionBean.getAltHostPassword2()
											.equalsIgnoreCase(firstAvailableRoom.getHostPassword())) {
								if (endPointListSize > 2) {
									firstAvailableRoom = endPointList.get(2);
									sessionBean.setAltHostId3(firstAvailableRoom.getHostId());
									sessionBean.setAltHostKey3(firstAvailableRoom.getZoomUID());
									sessionBean.setAltHostPassword3(firstAvailableRoom.getHostPassword());
									locationCapacityMap.put(sessionBean.getAltFaculty3Location(),
											capacityForAltFaculty3);
								} else {
									// given garbage value to avoid null pointer in further steps, this value will not be saved in db.
									sessionBean.setAltHostId3("4");
									sessionBean.setAltHostKey3("4");
									sessionBean.setErrorRecord(true);
									sessionBean.setErrorMessage(sessionBean.getErrorMessage() + "<br>  Room not Available for Parallel Faculty 3 on Date :"
											+ sessionBean.getDate() + " Start Time : " + sessionBean.getStartTime()
											+ " at " + sessionBean.getAltFaculty2Location());
								}
							} else {
								sessionBean.setAltHostId3(firstAvailableRoom.getHostId());
								sessionBean.setAltHostKey3(firstAvailableRoom.getZoomUID());
								sessionBean.setAltHostPassword3(firstAvailableRoom.getHostPassword());
								locationCapacityMap.put(sessionBean.getAltFaculty3Location(), capacityForAltFaculty3);
							}

						} else {
							// given garbage value to avoid null pointer in further steps, this value will not be saved in db.
							sessionBean.setAltHostId3("4");
							sessionBean.setAltHostKey3("4");
							sessionBean.setErrorRecord(true);
							sessionBean.setErrorMessage(sessionBean.getErrorMessage() + "<br>  Room not Available for Parallel Faculty 3 on Date :"
									+ sessionBean.getDate() + " Start Time : " + sessionBean.getStartTime() + " at "
									+ sessionBean.getAltFacultyLocation());
						}
					} else if (sessionBean.getAltHostId2().equalsIgnoreCase(firstAvailableRoom.getHostId()) && sessionBean.getAltHostPassword2()
									.equalsIgnoreCase(firstAvailableRoom.getHostPassword())) {
						if (endPointListSize > 1) {
							firstAvailableRoom = endPointList.get(1);
							sessionBean.setAltHostId3(firstAvailableRoom.getHostId());
							sessionBean.setAltHostKey3(firstAvailableRoom.getZoomUID());
							sessionBean.setAltHostPassword3(firstAvailableRoom.getHostPassword());
							locationCapacityMap.put(sessionBean.getAltFaculty3Location(), capacityForAltFaculty3);
						} else {
							// given garbage value to avoid null pointer in further steps, this value will not be saved in db.
							sessionBean.setAltHostId3("4");
							sessionBean.setAltHostKey3("4");
							sessionBean.setErrorRecord(true);
							sessionBean.setErrorMessage(sessionBean.getErrorMessage() + "<br>  Room not Available for Parallel Faculty 3 on Date :"
									+ sessionBean.getDate() + " Start Time : " + sessionBean.getStartTime() + " at " + sessionBean.getAltFaculty2Location());
						}
					} else {
						sessionBean.setAltHostId3(firstAvailableRoom.getHostId());
						sessionBean.setAltHostKey3(firstAvailableRoom.getZoomUID());
						sessionBean.setAltHostPassword3(firstAvailableRoom.getHostPassword());
						locationCapacityMap.put(sessionBean.getAltFaculty3Location(), capacityForAltFaculty3);
					}
				} else {
					// given garbage value to avoid null pointer in further steps, this value will not be saved in db.
					sessionBean.setAltHostId3("4");
					sessionBean.setAltHostKey3("4");
					sessionBean.setErrorRecord(true);
					sessionBean.setErrorMessage(
					sessionBean.getErrorMessage() + "<br>  Room not Available for Parallel Faculty 3 on Date :"
							+ sessionBean.getDate() + " Start Time : " + sessionBean.getStartTime() + " at " + sessionBean.getAltFaculty2Location());
				}
			} // end of main else
		}
		
		/******* Checking For Alt Host 2 End *******/
		
		try {
			loggerForSessionScheduling.info("IN allocateAvailableRoomForBatchUpload \nSubject: "+sessionBean.getSubject()+", Session Name: "+sessionBean.getSessionName()
					+ "\nDate: "+sessionBean.getDate() +", Start Time: "+sessionBean.getStartTime()+", and in end have set "
					+ "\nHostiD " + sessionBean.getHostId() + " hostPass : " + sessionBean.getHostPassword() 
					+ "\naldHostid 1 : " + sessionBean.getAltHostId() + " altHostPassword " + sessionBean.getAltHostPassword()
					+ "\naldHostid 2 : " + sessionBean.getAltHostId2() + " altHostPassword2 " + sessionBean.getAltHostPassword2() 
					+ "\naldHostid 3 : " + sessionBean.getAltHostId3() + " altHostPassword3 " + sessionBean.getAltHostPassword3());
		} catch (Exception e) {
			loggerForSessionScheduling.info("IN allocateAvailableRoomForBatchUpload Error While fetching Data.");
		}
		
		return sessionBean;
	}
	
	
	
	
	
	
	
	
	
	
	

	@RequestMapping(value = "/bookTMSConference", method = { RequestMethod.GET, RequestMethod.POST })
	public ModelAndView bookTMSConference(HttpServletRequest request, HttpServletResponse respnse, Model m) {
		ModelAndView modelnView = new ModelAndView("autoScheduleSessions");
		List<SessionDayTimeAcadsBean> pendingConferenceList = conferenceBookingDAO.getPendingConferenceList();

		if (pendingConferenceList != null && (!pendingConferenceList.isEmpty())) {
			BandwidthOverride bandwidth = new BandwidthOverride();
			bandwidth.setBandwidth("8b/512kbps");

			for (int i = 0; i < pendingConferenceList.size(); i = i + 10) {
				int lastIndex = (pendingConferenceList.size() < i + 10 ? pendingConferenceList.size() : i + 10);
				List<SessionDayTimeAcadsBean> pendingConferenceSubList = pendingConferenceList.subList(i, lastIndex);

				List<Conference> conferenceWSDLList = new ArrayList<>(pendingConferenceSubList.size());
				for (SessionDayTimeAcadsBean conferenceBean : pendingConferenceSubList) {
					Conference conferenceWSDL = new Conference();
					conferenceWSDL.setConferenceId(-1);
					// conferenceWSDL.setOwnerId(10);
					conferenceWSDL.setConferenceType(ConferenceType.AUTOMATIC_CALL_LAUNCH);
					conferenceWSDL.setBandwidth("8b/512kbps");
					conferenceWSDL.setISDNBandwidth(bandwidth);
					conferenceWSDL.setIPBandwidth(bandwidth);
					conferenceWSDL.setEncrypted(EncryptionRequested.IF_POSSIBLE);
					conferenceWSDL.setTitle(conferenceBean.getSubject() + "-" + conferenceBean.getSessionName());
					conferenceWSDL
							.setStartTimeUTC(conferenceBean.getDate() + " " + conferenceBean.getStartTime() + "Z");
					conferenceWSDL.setEndTimeUTC(conferenceBean.getDate() + " " + conferenceBean.getEndTime() + "Z");
					conferenceWSDL.setShowExtendOption(ExtendOptionRequested.AUTOMATIC_BEST_EFFORT);

					// Add Web Ex
					ExternalConference extConf = new ExternalConference();
					WebEx webEx = new WebEx();
					webEx.setMeetingPassword("newuser");
					webEx.setJoinBeforeHostTime("00:30:00");
					extConf.setWebEx(webEx);
					conferenceWSDL.setExternalConference(extConf);

					// Add Rooms
					/*
					 * ArrayOfParticipant participants = new ArrayOfParticipant();
					 * 
					 * Participant participant = new Participant();
					 * participant.setParticipantCallType("TMS"); participant.setParticipantId(14);
					 * participants.getParticipant().add(participant);
					 * 
					 * participant = new Participant(); participant.setParticipantCallType("TMS");
					 * participant.setParticipantId(23);
					 * participants.getParticipant().add(participant);
					 * 
					 * participant = new Participant(); participant.setParticipantCallType("TMS");
					 * participant.setParticipantId(28);
					 * participants.getParticipant().add(participant);
					 * 
					 * conferenceWSDL.setParticipants(participants);
					 */

					/*
					 * Conference response = conferenceBookingClient.saveConference(conferenceWSDL);
					 * conferenceBean.setStatus("B");
					 * conferenceBean.setTmsConfId(response.getConferenceId());
					 * conferenceBean.setTmsConfLink(response.getWebConferenceAttendeeUri());
					 */
					conferenceWSDLList.add(conferenceWSDL);
				}

				List<Conference> conferenceResponse = conferenceBookingClient.saveConferences(conferenceWSDLList);
				for (Iterator confIterator = conferenceResponse.iterator(), beanIterator = pendingConferenceSubList
						.iterator(); confIterator.hasNext() && beanIterator.hasNext();) {
					Conference conference = (Conference) confIterator.next();
					SessionDayTimeAcadsBean conferenceBean = (SessionDayTimeAcadsBean) beanIterator.next();
					conferenceBean.setCiscoStatus("B");
					conferenceBean.setTmsConfId(conference.getConferenceId());
					conferenceBean.setTmsConfLink(conference.getWebConferenceAttendeeUri());
					if (null != conference.getExternalConference()
							&& null != conference.getExternalConference().getWebEx()) {
						conferenceBean.setMeetingKey(conference.getExternalConference().getWebEx().getMeetingKey());
						conferenceBean
								.setMeetingPwd(conference.getExternalConference().getWebEx().getMeetingPassword());
						conferenceBean.setJoinUrl(conference.getExternalConference().getWebEx().getJoinMeetingUrl());
						conferenceBean.setHostUrl(conference.getExternalConference().getWebEx().getHostMeetingUrl());
						conferenceBean.setHostKey(conference.getExternalConference().getWebEx().getHostKey());
						if (null != conference.getExternalConference().getWebEx().getTelephony()) {
							conferenceBean.setLocalTollNumber(conference.getExternalConference().getWebEx()
									.getTelephony().getLocalCallInTollNumber());
							conferenceBean.setLocalTollFree(conference.getExternalConference().getWebEx().getTelephony()
									.getLocalCallInTollFreeNumber());
							conferenceBean.setGlobalCallNumber(conference.getExternalConference().getWebEx()
									.getTelephony().getGlobalCallInNumberUrl());
							conferenceBean.setPstnDialNumber(
									conference.getExternalConference().getWebEx().getTelephony().getPstnDialInNumber());
							conferenceBean.setParticipantCode(conference.getExternalConference().getWebEx()
									.getTelephony().getParticipantAccessCode());
						}

					}

				}

				int[] result = conferenceBookingDAO.updateBookedConference(pendingConferenceSubList);
			}

		}
		return autoScheduleForm(request, respnse, m);
	}

	@RequestMapping(value = "/allocateRoomnHost", method = { RequestMethod.GET, RequestMethod.POST })
	public ModelAndView allocateRoomnHost(HttpServletRequest request, HttpServletResponse respnse, Model m)
			throws Exception {
		ModelAndView modelnView = new ModelAndView("autoScheduleSessions");
		List<SessionDayTimeAcadsBean> pendingConferenceList = conferenceBookingDAO.getAllConferenceList();

		for (int i = 0; i < pendingConferenceList.size(); i++) {
			SessionDayTimeAcadsBean session = pendingConferenceList.get(i);
			allocateAvailableRoom(session);
			conferenceBookingDAO.updateRooms(session);
		}

		request.setAttribute("success", "true");
		request.setAttribute("successMessage", "Allocated rooms successfully");
		return modelnView;
	}

	@RequestMapping(value = "/bookTrainingSessions", method = { RequestMethod.GET, RequestMethod.POST })
	public ModelAndView bookTrainingSessions(HttpServletRequest request, HttpServletResponse response, Model m) throws Exception {

		if (!checkSession(request, response)) {
			return new ModelAndView("studentPortalRediret");
		}
		
		List<SessionDayTimeAcadsBean> pendingConferenceList = conferenceBookingDAO.getPendingConferenceList();
		ArrayList<SessionDayTimeAcadsBean> errorList = new ArrayList<>();
		ArrayList<SessionDayTimeAcadsBean> webexCreatedSessionList = new ArrayList<>();

		if (pendingConferenceList != null && (!pendingConferenceList.isEmpty())) {
			if (ENVIRONMENT.equalsIgnoreCase("PROD")){
			for (int i = 0; i < pendingConferenceList.size(); i++) {
				SessionDayTimeAcadsBean session = pendingConferenceList.get(i);

				// Commented by Somesh For Zoom integration
				// webExManager.scheduleTrainingSession(session);
				
				zoomManger.scheduleSessions(session);

				if (session.isErrorRecord()) {
					errorList.add(session);
				} else {
					webexCreatedSessionList.add(session);
				}
			}
			
			try {
				conferenceBookingDAO.updateBookedConference(webexCreatedSessionList);
			} catch (Exception e) {
				  
				request.setAttribute("error", "true");
				request.setAttribute("errorMessage", webexCreatedSessionList.size() + " sessions were created in Zoom. "
						+ "Error while saving into DB");
				return autoScheduleForm(request, response, m);
			}

			if (errorList.size() > 0) {
				request.setAttribute("error", "true");
				request.setAttribute("errorMessage", errorList.size() + " sessions were not created in Zoom due to error.");
			}

			if (webexCreatedSessionList.size() > 0) {
				request.setAttribute("success", "true");
				request.setAttribute("successMessage", webexCreatedSessionList.size() + " sessions out of "
						+ pendingConferenceList.size() + " created in Zoom successfully");
			}
			
			} else {
				int[] result = conferenceBookingDAO.updateBookedConference(pendingConferenceList);
				if(result.length != 0) { 
					request.setAttribute("success","true");
					request.setAttribute("successMessage"," Sessions Not created in Zoom as it not Prod"); 
				} 
			}
		}

		return autoScheduleForm(request, response, m);
	}

	@RequestMapping(value = "/searchScheduledSessionForm", method = { RequestMethod.GET, RequestMethod.POST })
	public String searchScheduledSessionForm(HttpServletRequest request, HttpServletResponse respnse, Model m) {

		SessionDayTimeAcadsBean searchBean = new SessionDayTimeAcadsBean();
		m.addAttribute("searchBean", searchBean);
		m.addAttribute("yearList", ACAD_YEAR_LIST);
		m.addAttribute("subjectList", getSubjectList());
		m.addAttribute("locationList", getLocationList());
		m.addAttribute("consumerTypeList", getConsumerTypesList());
		m.addAttribute("subjectCodeMap", getsubjectCodeMap());
		return "searchScheduledSession";
	}


	@RequestMapping(value = "/searchScheduledSession", method = { RequestMethod.GET, RequestMethod.POST })
	public ModelAndView searchScheduledSession(HttpServletRequest request, HttpServletResponse response, @ModelAttribute SessionDayTimeAcadsBean searchBean) {

		ModelAndView modelnView = new ModelAndView("searchScheduledSession");
		TimeTableDAO dao = (TimeTableDAO)act.getBean("timeTableDAO");
		
		PageAcads<SessionDayTimeAcadsBean> page = new PageAcads<SessionDayTimeAcadsBean>();
		List<SessionDayTimeAcadsBean> scheduledSessionList = new ArrayList<SessionDayTimeAcadsBean>();
		
		String searchType = request.getParameter("searchType") == null ? "distinct" : request.getParameter("searchType");
		request.getSession().setAttribute("searchType", searchType);
		request.getSession().setAttribute("searchBean_acads", searchBean);	
		
		try {				
			page = schedulingService.getSessionDetailsPage(1, pageSize, searchBean, searchType);
			scheduledSessionList = page.getPageItems();
			
			if(scheduledSessionList == null || scheduledSessionList.size() == 0){
				request.setAttribute("error", "true");
				request.setAttribute("errorMessage", "No Records found.");
			}
		} catch (Exception e) {
			request.setAttribute("error", "true");
			request.setAttribute("errorMessage", "No Records found. Error while generating records");
		}
		
		modelnView.addObject("page", page);
		modelnView.addObject("semList", semList);
		modelnView.addObject("searchBean", searchBean);
		modelnView.addObject("searchType", searchType);
		modelnView.addObject("yearList", ACAD_YEAR_LIST);
		modelnView.addObject("rowCount", page.getRowCount());
		modelnView.addObject("subjectList", getSubjectList());
		modelnView.addObject("locationList", getLocationList());
		modelnView.addObject("subjectCodeMap", getsubjectCodeMap());
		modelnView.addObject("consumerTypeList", getConsumerTypesList());
		modelnView.addObject("scheduledSessionList", scheduledSessionList);
//		modelnView.addObject("mapOfFacultyIdAndFacultyRecord", mapOfFacultyIdAndFacultyRecord());
//		request.getSession().setAttribute("mapOfFacultyIdAndFacultyRecord", mapOfFacultyIdAndFacultyRecord);
		
		modelnView.addObject("mapOfFacultyIdAndFacultyRecord", mapOfFacultyIdAndFacultyDetails());
		request.getSession().setAttribute("mapOfFacultyIdAndFacultyRecord", mapOfFacultyIdAndFacultyDetails());
		
		return modelnView;
	}
	
	
	@RequestMapping(value = "/searchScheduledSessionPage", method = { RequestMethod.GET, RequestMethod.POST })
	public ModelAndView searchScheduledSessionPage(HttpServletRequest request, HttpServletResponse response) {

		ModelAndView modelnView = new ModelAndView("searchScheduledSession");
		TimeTableDAO dao = (TimeTableDAO)act.getBean("timeTableDAO");
		PageAcads<SessionDayTimeAcadsBean> page = new PageAcads<SessionDayTimeAcadsBean>();
		List<SessionDayTimeAcadsBean> scheduledSessionList = new ArrayList<SessionDayTimeAcadsBean>();
		SessionDayTimeAcadsBean searchBean = (SessionDayTimeAcadsBean)request.getSession().getAttribute("searchBean_acads");
		String searchType = (String) request.getSession().getAttribute("searchType");
		
		try {			
			int pageNo = Integer.parseInt(request.getParameter("pageNo"));
			page = schedulingService.getPage(pageNo, pageSize, searchBean, searchType);
		    scheduledSessionList = page.getPageItems();

			if (scheduledSessionList == null || scheduledSessionList.size() == 0) {
				request.setAttribute("error", "true");
				request.setAttribute("errorMessage", "No Sessions found.");
			}
			
		} catch (Exception e) {
			request.setAttribute("error", "true");
			request.setAttribute("errorMessage", "No Records found. Error while generating records");
		}
		
		modelnView.addObject("page", page);
		modelnView.addObject("searchBean", searchBean);
		modelnView.addObject("searchType", searchType);
		modelnView.addObject("yearList", ACAD_YEAR_LIST);
		modelnView.addObject("rowCount", page.getRowCount());
		modelnView.addObject("subjectList", getSubjectList());
		modelnView.addObject("locationList", getLocationList());
		modelnView.addObject("subjectCodeMap", getsubjectCodeMap());
		modelnView.addObject("consumerTypeList", getConsumerTypesList());
		modelnView.addObject("scheduledSessionList", scheduledSessionList);
		modelnView.addObject("mapOfFacultyIdAndFacultyRecord", mapOfFacultyIdAndFacultyRecord());
		request.getSession().setAttribute("mapOfFacultyIdAndFacultyRecord", mapOfFacultyIdAndFacultyRecord);
		return modelnView;
	}


	@RequestMapping(value = "/downloadScheduledSessions", method = { RequestMethod.GET, RequestMethod.POST })
	public ModelAndView downloadScheduledSessions(HttpServletRequest request, HttpServletResponse response) {
		
		List<SessionDayTimeAcadsBean> scheduledSessionList = new ArrayList<SessionDayTimeAcadsBean>();		
		try {

			SessionDayTimeAcadsBean searchBean = (SessionDayTimeAcadsBean) request.getSession().getAttribute("searchBean_acads");
			String searchType = (String) request.getSession().getAttribute("searchType");
			String tempSearchType = searchType != null ? searchType : "distinct";
//			ArrayList<ProgramSubjectMappingBean> subjectProgramList = getSubjectProgramList();
			scheduledSessionList = schedulingService.getReportListOfScheduledSession(searchBean, tempSearchType);
		} catch (Exception e) {
			request.setAttribute("error", "true");
			request.setAttribute("errorMessage", "No Records found. Error while Downloading Records");
		}
		
		return new ModelAndView("scheduledSessionExcelView", "scheduledSessionList", scheduledSessionList);
	}

	@RequestMapping(value = "/deleteScheduledSession", method = { RequestMethod.GET, RequestMethod.POST })
	public ModelAndView deleteScheduledSession(HttpServletRequest request, HttpServletResponse response) {

		if (!checkSession(request, response)) {
			return new ModelAndView("studentPortalRediret");
		}

		try {
			String id = request.getParameter("id");
			String consumerProgramStructureId = request.getParameter("consumerProgramStructureId");
			TimeTableDAO dao = (TimeTableDAO)act.getBean("timeTableDAO");
			
			loggerForSessionScheduling.info("In deleteScheduledSession id: " +id+ " and consumerProgramStructureId: "+consumerProgramStructureId);
			
			SessionDayTimeAcadsBean session = dao.getSessionById(id);
			
			// No consumerProgramStructureId for Orientation Session
			if ("Orientation".equals(session.getSubject()) && StringUtils.isBlank(consumerProgramStructureId)) {
				if ("PROD".equalsIgnoreCase(ENVIRONMENT)) {
					deleteMeeting(request, response);
				}
				dao.deleteScheduledSession(id);
				request.setAttribute("success","true");
				request.setAttribute("successMessage","Record deleted successfully from Database");
				return searchScheduledSession(request, response, new SessionDayTimeAcadsBean());
			}
			
			//Get count of applicable cpsid for Session 
			int sessionCount = dao.getSessionApplicableCount(id);
			loggerForSessionScheduling.info("In deleteScheduledSession sessionCount : "+sessionCount);
			if (sessionCount == 0) {
				return sendToErrorPage(request, session, "No Mapping found for this session.", "true");
			}
			
			int sizeOfCPSId = consumerProgramStructureId.split(",").length;
			loggerForSessionScheduling.info("In deleteScheduledSession sizeOfCPSId : "+sizeOfCPSId);
			
			if (sizeOfCPSId >= 1
					// if noOfsessions for count not match then create new session
					&& sessionCount > 1 && sessionCount != sizeOfCPSId						
					// MBA-Wx program applicable for only one program
					&& !consumerProgramStructureId.contains("111") && !consumerProgramStructureId.contains("151") 
					&& !consumerProgramStructureId.contains("160")
					// M.Sc. (AI & ML Ops) || M.Sc. (AI) program applicable for only one program
					&& !consumerProgramStructureId.contains("131") && !consumerProgramStructureId.contains("158")
					// PC-DS || PD-DS
					&& !consumerProgramStructureId.contains("154") && !consumerProgramStructureId.contains("155")) {
				
				boolean isDeleted = dao.deleteSessionMappings(id, consumerProgramStructureId, "N");
				if (isDeleted) {
					ArrayList<SessionDayTimeAcadsBean> programDetails = dao.getProgramDetails(consumerProgramStructureId);
					String message = "Session Deleted Successfully for ";
					for (SessionDayTimeAcadsBean bean : programDetails) {
						message = message + " , "+bean.getProgram()+ "-" + bean.getProgramStructure() + "-"+bean.getConsumerType();
					}
					request.setAttribute("success", "true");
					request.setAttribute("successMessage", message);
				} else {
					request.setAttribute("error", "true");
					request.setAttribute("errorMessage", "Error in deleting session mapping.");
				}
			} else {
				if ("PROD".equalsIgnoreCase(ENVIRONMENT)) {
					deleteMeeting(request, response);
				}
				boolean isDeleted = dao.deleteSessionMappings(id, consumerProgramStructureId, "Y");
				if (isDeleted) {
					request.setAttribute("success", "true");
					request.setAttribute("successMessage", "Session Deleted Successfully.");
				} else {
					request.setAttribute("error", "true");
					request.setAttribute("errorMessage", "Error in deleting session mapping.");
				}
			}
			
			/*
			if ( sizeOfCPSId != sessionCount && !session.getHasModuleId().equalsIgnoreCase("Y")) {
				int deleted = dao.deleteSessionSubjectMapping(id, consumerProgramStructureId);
				ArrayList<SessionDayTimeBean> programDetails = dao.getProgramDetails(consumerProgramStructureId);
				if(deleted > -1) {
					String message = "Session Deleted Successfully for ";
					for (SessionDayTimeBean bean : programDetails) {
						message = message + " , "+bean.getProgram()+ "-" + bean.getProgramStructure() + "-"+bean.getConsumerType();
					}
					request.setAttribute("success", "true");
					request.setAttribute("successMessage", message);
				}else {
					request.setAttribute("error", "true");
					request.setAttribute("errorMessage", "Error in deleting session mapping.");
				}
			}else {
				if ("PROD".equalsIgnoreCase(ENVIRONMENT)) {
					deleteMeeting(request, response);
				}
				dao.deleteScheduledSession(id);
				request.setAttribute("success","true");
				request.setAttribute("successMessage","Record deleted successfully from Database");
			}
			*/
		}catch(Exception e){
			loggerForSessionScheduling.info("Error in deleteScheduledSession : "+e.getMessage());
			request.setAttribute("error", "true");
			request.setAttribute("errorMessage", "Error in deleting Record.");
		}
		SessionDayTimeAcadsBean searchBean = (SessionDayTimeAcadsBean) request.getSession().getAttribute("searchBean_acads");
		if (searchBean == null) {
			searchBean = new SessionDayTimeAcadsBean();
		}
		loggerForSessionScheduling.info("Ended deleteScheduledSession");
		return searchScheduledSession(request, response, searchBean);
	}

	private void deleteMeeting(HttpServletRequest request, HttpServletResponse response) {

		try {
			// SessionDayTimeBean session =
			// conferenceBookingDAO.getSessionForRefresh(request.getParameter("id"));
			SessionDayTimeAcadsBean session = conferenceBookingDAO.getSessionForCheck(request.getParameter("id"));

			String successMessage = "";
			String errorMessage = "";

			if (session.getMeetingKey() != null && (!"".equals(session.getMeetingKey().trim()))) {
				// webExManager.deleteTrainingSession(session);
				zoomManger.deleteSession(session, session.getMeetingKey());
				if (!session.isErrorRecord()) {
					request.setAttribute("success", "true");
					successMessage = "Earlier Session deleted successfully<br>";
					request.setAttribute("successMessage", successMessage);
				} else {
					errorMessage = "Old meeting not deleted from Zoom: " + session.getErrorMessage() + " <br>";
					request.setAttribute("error", "true");
					request.setAttribute("errorMessage", errorMessage);
				}
			}

		} catch (Exception e) {
			loggerForSessionScheduling.info("Error in deleteMeeting : "+e.getMessage());
			request.setAttribute("error", "true");
			request.setAttribute("errorMessage", "Error in refreshing session: " + e.getMessage());
		}
	}

	@RequestMapping(value = "/editScheduledSession", method = {RequestMethod.GET, RequestMethod.POST})
	public ModelAndView editScheduledSession(HttpServletRequest request, HttpServletResponse response, @ModelAttribute SessionDayTimeAcadsBean sessionBean,Model m) {

		ModelAndView modelnView = new ModelAndView("addScheduledSession");

		String id = request.getParameter("id");
		String consumerProgramStructureId = request.getParameter("consumerProgramStructureId");
		
		TimeTableDAO dao = (TimeTableDAO) act.getBean("timeTableDAO");
		//SessionDayTimeBean session = dao.findScheduledSessionById(id);
		SessionDayTimeAcadsBean session = dao.findScheduledSessionBySessionId(id);
		
		m.addAttribute("session", session);
		request.getSession().setAttribute("sessionBeforeEdit", session);
		setOtherSessionsInModel(session, modelnView);
		modelnView.addObject("consumerProgramStructureId", sessionBean.getConsumerProgramStructureId());

		ArrayList<ConsumerProgramStructureAcads> consumerTypeList = getConsumerTypesList();
		if (consumerTypeList.contains("Retail")) {
			consumerTypeList.remove("Retail");
		}

		String isSingleSession = "false";
		int sizeOfCPSId = consumerProgramStructureId.split(",").length;
		
		if (sizeOfCPSId <= 1) {
			isSingleSession = "true";
		}
		modelnView.addObject("yearList", ACAD_YEAR_LIST);
		modelnView.addObject("subjectList", getSubjectList());
		modelnView.addObject("locationList", getLocationList());
		modelnView.addObject("consumerTypeList", consumerTypeList);
		modelnView.addObject("trackList", trackList);
		modelnView.addObject("subjectCodeList", getSubjectCodeList());
		modelnView.addObject("subjectCodeMap", getsubjectCodeMap());
		modelnView.addObject("sessionTypesMap", getSessionTypesMap());
		modelnView.addObject("masterKeysWithSubjectCodes", getMasterKeysWithSubjectCodes());
		
		request.setAttribute("isSingleSession", isSingleSession);
		request.getSession().setAttribute("edit", "true");

		return modelnView;
	}
	
	@RequestMapping(value = "/viewScheduledSessionOld", method = { RequestMethod.GET, RequestMethod.POST })
	public ModelAndView viewScheduledSessionOld(HttpServletRequest request, HttpServletResponse response) throws Exception {
		ModelAndView modelnView = new ModelAndView("viewScheduledSession");

		String userId = (String) request.getSession().getAttribute("userId_acads");
		ContentDAO cDao = (ContentDAO) act.getBean("contentDAO");
		StudentAcadsBean student = (StudentAcadsBean) request.getSession().getAttribute("student_acads");
		
		String id = request.getParameter("id");
		String pssId = request.getParameter("pssId") != null ? request.getParameter("pssId") : "";
		List<Integer> liveSessionPssIdAccessList = (List<Integer>) request.getSession().getAttribute("liveSessionPssIdAccess_acads");
		String isSessionAccess = "false";
		String formatedDob = "";
		
		/* String classFullMessage = request.getParameter("classFullMessage"); */
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

		HashMap<String, Integer> facultyIdAndRemSeatsMap = dao.getMapOfFacultyIdAndRemainingSeats(id, session);
		modelnView.addObject("facultyIdAndRemSeatsMap", facultyIdAndRemSeatsMap);
		modelnView.addObject("session", session);

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
		modelnView.addObject("videoId", dao.getSessionVideoId(id, session.getFacultyId()));
		modelnView.addObject("altVideoId", dao.getSessionVideoId(id, session.getAltFacultyId()));
		modelnView.addObject("alt2VideoId", dao.getSessionVideoId(id, session.getAltFacultyId2()));
		modelnView.addObject("alt3videoId", dao.getSessionVideoId(id, session.getAltFacultyId3()));
		if (minutesToSession < 60 && minutesToSession > -120) {
			modelnView.addObject("enableAttendButton", "true");
		}

		if (minutesAfterSession > 120) {
			if (!"Guest Lecture".equalsIgnoreCase(session.getSessionName())) {// added Temporary To hide Post My Query
																				// button for Guest Lecture
				modelnView.addObject("showQueryButton", "true");
			}
			modelnView.addObject("sessionOver", "true");

		}

		// Commented By Somesh For Zoom Integration

		/*
		 * StudentBean student =
		 * (StudentBean)request.getSession().getAttribute("student_acads"); String joinUrl =
		 * ""; String name = "Coordinator"; String email = "notavailable@mail.com";
		 * String mobile = "0000000"; if(student != null){ name = student.getFirstName()
		 * + " "+ student.getLastName(); email = student.getEmailId() != null ?
		 * student.getEmailId() : "notavailable@mail.com"; mobile = student.getMobile()
		 * != null ? student.getMobile() : "0000000"; }
		 * 
		 * joinUrl = WEB_EX_API_URL + "?AT=JM&MK="+session.getMeetingKey()
		 * +"&AN="+URLEncoder.encode(name, "UTF-8") +"&AE="+URLEncoder.encode(email,
		 * "UTF-8") +"&CO="+URLEncoder.encode(mobile, "UTF-8")
		 * +"&PW="+session.getMeetingPwd();
		 * 
		 * modelnView.addObject("joinUrl", joinUrl);
		 * 
		 * String hostUrl = WEB_EX_LOGIN_API_URL+
		 * "?AT=LI&WID="+session.getHostId()+"&PW="+session.getHostPassword()+"&BU="+
		 * WEB_EX_API_URL+"?MK="+session.getMeetingKey()+"%26AT=HM%26Rnd="+Math.random(); 
		 * modelnView.addObject("hostUrl",hostUrl); modelnView.addObject("SERVER_PATH", SERVER_PATH);
		 * //if(!StringUtils.isBlank(classFullMessage)){ //
		 * setError(request,classFullMessage); //}
		 * 
		 */
		
		ArrayList<ProgramSubjectMappingAcadsBean> subjectProgramList = dao.getSubjectProgramListBySessionId(id);
		modelnView.addObject("subjectProgramList", subjectProgramList);

		// For Students join URL
		String studentJoinUrlForHost = session.getJoinUrl();
		String studentJoinUrlForAltHost = session.getAltStudentJoinUrl();
		String studentJoinUrlForAlt2Host = session.getAl2tStudentJoinUrl();
		String studentJoinUrlForAlt3Host = session.getAlt3StudentJoinUrl();

		modelnView.addObject("joinUrl", studentJoinUrlForHost);
		modelnView.addObject("altJoinUrl", studentJoinUrlForAltHost);
		modelnView.addObject("alt2JoinUrl", studentJoinUrlForAlt2Host);
		modelnView.addObject("alt3JoinUrl", studentJoinUrlForAlt3Host);

		// For Hosts join URL
		String hostUrl = session.getHostUrl();
		String althostUrl = session.getAltHostJoinUrl();
		String alt2hostUrl = session.getAlt2HostJoinUrl();
		String alt3hostUrl = session.getAlt3HostJoinUrl();

		modelnView.addObject("hostUrl", hostUrl);
		modelnView.addObject("althostUrl", althostUrl);
		modelnView.addObject("alt2hostUrl", alt2hostUrl);
		modelnView.addObject("alt3hostUrl", alt3hostUrl);

		modelnView.addObject("SERVER_PATH", SERVER_PATH);
		
		//Added as Session Polls tab should be shown to main faculty not alternate faculty
		modelnView.addObject("nosessionPolls",session.getFacultyId().equalsIgnoreCase(userId));
		return modelnView;
	}
	
	
	
//	to be deleted, api shifted to rest controller
//	@RequestMapping(value = "/m/viewScheduledSession", method = RequestMethod.POST, consumes = "application/json", produces = "application/json")
//	public ResponseEntity<List<ParallelSessionBean>> mviewScheduledSession(@RequestParam("id") String idString,
//			@RequestBody StudentBean student) throws Exception {
////		ModelAndView modelnView = new ModelAndView("viewScheduledSession");
//		HttpHeaders headers = new HttpHeaders();
//		headers.add("Content-Type", "application/json");
//
//		String userId = student.getSapid();
//		ContentDAO cDao = (ContentDAO) act.getBean("contentDAO");
//
//		String id = idString;
//		/* String classFullMessage = request.getParameter("classFullMessage"); */
//		TimeTableDAO dao = (TimeTableDAO) act.getBean("timeTableDAO");
//		SessionDayTimeBean session = dao.findScheduledSessionById(id);
//		SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
//		Date sessiondate = df.parse(session.getDate());
//		Date currdate = new Date();
//		if (sessiondate.before(currdate)) {
//			session.setTimeboundId(dao.getTimeboundIdByModuleID(session.getModuleId()));
//			// session.setVideosOfSession(vdao.getVideosForSession(session.getId()));
//		}
////		HashMap<String, Integer> facultyIdAndRemSeatsMap = dao.getMapOfFacultyIdAndRemainingSeats(id, session);
//		HashMap<String, Integer> facultyIdAndRemSeatsMap = dao.getMapOfFacultyIdAndRemainingSeatsV2(session);
//		List<ParallelSessionBean> response = new ArrayList<>();
//		HashMap<String, FacultyBean> listOfAllFaculties = mapOfFacultyIdAndFacultyRecord();
//		
//		Iterator it = facultyIdAndRemSeatsMap.entrySet().iterator();
//		int i = 1;
//		while (it.hasNext()) {
//			Map.Entry pair = (Map.Entry) it.next();
//			ParallelSessionBean parallelSessionBean = new ParallelSessionBean();
//			parallelSessionBean.setFacultyId(pair.getKey().toString());
//			parallelSessionBean.setSeats(pair.getValue().toString());
//			FacultyBean facultyBean = listOfAllFaculties.get(pair.getKey());
//			parallelSessionBean.setFirstName(facultyBean.getFirstName());
//			parallelSessionBean.setLastName(facultyBean.getLastName());
//			parallelSessionBean.setEmail(facultyBean.getEmail());
//			parallelSessionBean.setActive(facultyBean.getActive());
//			parallelSessionBean.setMobile(facultyBean.getMobile());
//			parallelSessionBean.setCreatedBy(facultyBean.getCreatedBy());
//			parallelSessionBean.setCreatedDate(facultyBean.getCreatedDate());
//			parallelSessionBean.setLastModifiedBy(facultyBean.getLastModifiedBy());
//			parallelSessionBean.setLastModifiedDate(facultyBean.getLastModifiedDate());
//			parallelSessionBean.setSessionBean(session);
//			
//			if(session.getFacultyId().equalsIgnoreCase(pair.getKey().toString())) {
//				parallelSessionBean.setJoinFor("HOST");
//			}else if(session.getAltFacultyId().equalsIgnoreCase(pair.getKey().toString())) {
//				parallelSessionBean.setJoinFor("ALTFACULTYID");
//			}else if(session.getAltFacultyId2().equalsIgnoreCase(pair.getKey().toString())) {
//				parallelSessionBean.setJoinFor("ALTFACULTYID2");
//			}else if(session.getAltFacultyId3().equalsIgnoreCase(pair.getKey().toString())) {
//				parallelSessionBean.setJoinFor("ALTFACULTYID3");
//			}
//			
//			response.add(parallelSessionBean);
//
//			it.remove(); // avoids a ConcurrentModificationException
//
//		}
//
////		String sessionDate = session.getDate();
////		String sessionTime = session.getStartTime();
////
////		Date sessionDateTime = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(sessionDate + " " + sessionTime);
////		long minutesToSession = getDateDiff(new Date(), sessionDateTime, TimeUnit.MINUTES);
////		long minutesAfterSession = getDateDiff(sessionDateTime, new Date(), TimeUnit.MINUTES);
//
//
////for(int i = 0; i < facultyIdAndRemSeatsMap_list.size(); i++) {
////	 String s = listOfAllFaculties.get(facultyIdAndRemSeatsMap_list.get[i]);
////
////
////}
//
////		if(minutesToSession < 60 && minutesToSession > -120){
////			modelnView.addObject("enableAttendButton", "true");
////
////			response.put("enableAttendButton", session_list);
////			response.put("showQueryButton", session_list);
////			response.put("sessionOver", session_list);
////		}
////
////
////		if(minutesAfterSession > 120){
////			if(!"Guest Lecture".equalsIgnoreCase(session.getSessionName())){// added Temporary To hide Post My Query button for Guest Lecture
////				modelnView.addObject("showQueryButton", "true");
////			}
////			modelnView.addObject("sessionOver", "true");
////
////		}
////
////		String joinUrl = "";
////		String name = "Coordinator";
////		String email = "notavailable@mail.com";
////		String mobile = "0000000";
////		if(student != null){
////			name = student.getFirstName() + " "+ student.getLastName();
////			email = student.getEmailId() != null ? student.getEmailId() : "notavailable@mail.com";
////			mobile = student.getMobile() != null ? student.getMobile() : "0000000";
////		}
////
////		joinUrl = WEB_EX_API_URL + "?AT=JM&MK="+session.getMeetingKey()
////				+"&AN="+URLEncoder.encode(name, "UTF-8")
////				+"&AE="+URLEncoder.encode(email, "UTF-8")
////				+"&CO="+URLEncoder.encode(mobile, "UTF-8")
////				+"&PW="+session.getMeetingPwd();
//
////		modelnView.addObject("joinUrl", joinUrl);
////
////		String hostUrl = WEB_EX_LOGIN_API_URL+ "?AT=LI&WID="+session.getHostId()+"&PW="+session.getHostPassword()+"&BU="+WEB_EX_API_URL+"?MK="+session.getMeetingKey()+"%26AT=HM%26Rnd="+Math.random();
////		modelnView.addObject("hostUrl", hostUrl);
////		modelnView.addObject("SERVER_PATH", SERVER_PATH);
////
//		return new ResponseEntity<List<ParallelSessionBean>>(response, headers, HttpStatus.OK);
//	}

	
	@RequestMapping(value = "/loginIntoWebEx", method = { RequestMethod.GET, RequestMethod.POST })
	public ModelAndView loginIntoWebEx(HttpServletRequest request, HttpServletResponse response, Model m) throws Exception {

		ModelAndView modelnView = new ModelAndView("loginSessionRedirect");
		String id = request.getParameter("id");
		String type = request.getParameter("type");
		TimeTableDAO dao = (TimeTableDAO) act.getBean("timeTableDAO");
		SessionDayTimeAcadsBean sessionBean = dao.findScheduledSessionById(id);

//		Added for check meeting key is valid or invalid
		if (sessionBean.getIsVerified().equals("N")) {


			if (!StringUtils.isEmpty(sessionBean.getMeetingKey())) {
				String MeetingKey = sessionBean.getMeetingKey();
				String hostId = sessionBean.getHostId();
				String hostPass = sessionBean.getHostPassword();

				if (checkMeeting(request, response, hostId, hostPass, MeetingKey)) {
				} else {
					deleteAndCreateNewMeeting(request, response, "0");
				}
			}

			if (!StringUtils.isEmpty(sessionBean.getAltMeetingKey())) {
				String MeetingKey = sessionBean.getAltMeetingKey();
				String hostId = sessionBean.getAltHostId();
				String hostPass = sessionBean.getAltHostPassword();

				if (checkMeeting(request, response, hostId, hostPass, MeetingKey)) {
				} else {
					deleteAndCreateNewMeeting(request, response, "1");
				}
			}

			if (!StringUtils.isEmpty(sessionBean.getAltMeetingKey2())) {
				String MeetingKey = sessionBean.getAltMeetingKey2();
				String hostId = sessionBean.getAltHostId2();
				String hostPass = sessionBean.getAltHostPassword2();

				if (checkMeeting(request, response, hostId, hostPass, MeetingKey)) {
				} else {
					deleteAndCreateNewMeeting(request, response, "2");
				}
			}

			if (!StringUtils.isEmpty(sessionBean.getAltMeetingKey3())) {
				String MeetingKey = sessionBean.getAltMeetingKey3();
				String hostId = sessionBean.getAltHostId3();
				String hostPass = sessionBean.getAltHostPassword3();

				if (checkMeeting(request, response, hostId, hostPass, MeetingKey)) {
				} else {
					deleteAndCreateNewMeeting(request, response, "3");
				}
			}

			conferenceBookingDAO.updateIsVerified(sessionBean);

		}

		if ("0".equalsIgnoreCase(type)) {// Below 2 lines not needed, still added for consistency
			// Refreshing original session
			sessionBean.setHostId(sessionBean.getHostId());
			sessionBean.setHostPassword(sessionBean.getHostPassword());
		} else if ("1".equalsIgnoreCase(type)) {
			// Refreshing first parallel session
			sessionBean.setHostId(sessionBean.getAltHostId());
			sessionBean.setHostPassword(sessionBean.getAltHostPassword());
		} else if ("2".equalsIgnoreCase(type)) {
			// Refreshing second parallel session
			sessionBean.setHostId(sessionBean.getAltHostId2());
			sessionBean.setHostPassword(sessionBean.getAltHostPassword2());
		} else if ("3".equalsIgnoreCase(type)) {
			// Refreshing third parallel session
			sessionBean.setHostId(sessionBean.getAltHostId3());
			sessionBean.setHostPassword(sessionBean.getAltHostPassword3());
		}

		modelnView.addObject("session", sessionBean);
		modelnView.addObject("type", type);
		modelnView.addObject("SERVER_PATH", SERVER_PATH);
		modelnView.addObject("WEB_EX_LOGIN_API_URL", WEB_EX_LOGIN_API_URL);

		return modelnView;
	}

	@RequestMapping(value = "/startSession", method = { RequestMethod.GET, RequestMethod.POST })
	public ModelAndView startSession(HttpServletRequest request, HttpServletResponse response, Model m)
			throws Exception {

		ModelAndView modelnView = new ModelAndView("startSessionRedirect");
		String id = request.getParameter("id");
		String type = request.getParameter("type");

		TimeTableDAO dao = (TimeTableDAO) act.getBean("timeTableDAO");
		SessionDayTimeAcadsBean session = dao.findScheduledSessionById(id);

		if ("0".equalsIgnoreCase(type)) {// Below 2 lines not needed, still added for consistency
			// Refreshing original session
			session.setMeetingKey(session.getMeetingKey());
		} else if ("1".equalsIgnoreCase(type)) {
			// Refreshing first parallel session
			session.setMeetingKey(session.getAltMeetingKey());
		} else if ("2".equalsIgnoreCase(type)) {
			// Refreshing second parallel session
			session.setMeetingKey(session.getAltMeetingKey2());
		} else if ("3".equalsIgnoreCase(type)) {
			// Refreshing third parallel session
			session.setMeetingKey(session.getAltMeetingKey3());
		}

		String CSRF = (String) request.getParameter("CSRF");
		modelnView.addObject("session", session);
		modelnView.addObject("CSRF", CSRF);
		modelnView.addObject("WEB_EX_API_URL", WEB_EX_API_URL);

		return modelnView;
	}

	@RequestMapping(value = "/refreshSession", method = { RequestMethod.GET, RequestMethod.POST })
	public ModelAndView refreshSession(HttpServletRequest request, HttpServletResponse response, Model m)
			throws Exception {

		String type = request.getParameter("type");
		// deleteAndCreateNewMeeting(request, response, type);
		deleteAndCreateNewWebinar(request, response, type);
		return viewScheduledSession(request, response);
	}

	@RequestMapping(value = "/createParallelSession", method = { RequestMethod.GET, RequestMethod.POST })
	public ModelAndView createParallelSession(HttpServletRequest request, HttpServletResponse response, Model m) throws Exception {

		String successMessage = "";
		String errorMessage = "";

		SessionDayTimeAcadsBean session = conferenceBookingDAO.getSessionForRefresh(request.getParameter("id"));
		String type = request.getParameter("type");
		loggerForSessionScheduling.info("In createParallelSession Id: "+request.getParameter("id")+" Type: "+type);

		if ("1".equalsIgnoreCase(type)) {
			if (!"".equalsIgnoreCase(session.getAltFacultyId()) && session.getAltFacultyId() != null) {
				if ("".equalsIgnoreCase(session.getAltFacultyLocation()) || session.getAltFacultyLocation() == null) {
					setError(request, "Session cannot be created. As Parallel 1 Location is Blank ");
					return viewScheduledSession(request, response);
				}
				/*
				 * session.setHostId(PARALLEL_SESSION1_HOSTID);
				 * session.setHostPassword(PARALLEL_SESSION1_HOSTPASSWORD);
				 */

				session = allocateAvailableRoomForBatchUpload(session);
				if (session.getErrorMessage() != null && !"".equals(session.getErrorMessage())) {
					setError(request, session.getErrorMessage());
					return viewScheduledSession(request, response);
				}
			} else {
				setError(request, "Session cannot be created. As Parallel 1 Faculty Blank ");
				return viewScheduledSession(request, response);
			}
		} else if ("2".equalsIgnoreCase(type)) {
			if (!"".equalsIgnoreCase(session.getAltFacultyId2()) && session.getAltFacultyId2() != null) {
				if ("".equalsIgnoreCase(session.getAltFaculty2Location()) || session.getAltFaculty2Location() == null) {
					setError(request, "Session cannot be created. As Location is occupied ");
					return viewScheduledSession(request, response);
				}
				/*
				 * session.setHostId(PARALLEL_SESSION2_HOSTID);
				 * session.setHostPassword(PARALLEL_SESSION2_HOSTPASSWORD);
				 */

				session = allocateAvailableRoomForBatchUpload(session);

				if (session.getErrorMessage() != null && !"".equals(session.getErrorMessage())) {
					setError(request, session.getErrorMessage());
					return viewScheduledSession(request, response);
				}
			} else {
				setError(request, "Session cannot be created. As Faculty is occupied ");
				return viewScheduledSession(request, response);
			}
		} else if ("3".equalsIgnoreCase(type)) {
			if (!"".equalsIgnoreCase(session.getAltFacultyId3()) && session.getAltFacultyId3() != null) {
				if ("".equalsIgnoreCase(session.getAltFaculty3Location()) || session.getAltFaculty3Location() == null) {
					setError(request, "Session cannot be created. As Location is occupied ");
					return viewScheduledSession(request, response);
				}
				/*
				 * session.setHostId(PARALLEL_SESSION3_HOSTID);
				 * session.setHostPassword(PARALLEL_SESSION3_HOSTPASSWORD);
				 */

				session = allocateAvailableRoomForBatchUpload(session);

				if (session.getErrorMessage() != null && !"".equals(session.getErrorMessage())) {
					setError(request, session.getErrorMessage());
					return viewScheduledSession(request, response);
				}
			} else {
				setError(request, "Session cannot be created. As Faculty is occupied ");
				return viewScheduledSession(request, response);
			}
		} else {
			setError(request, "Session cannot be created. Please set parallel session type");
		}
		
		// Create a new WebEx meeting
		// webExManager.scheduleTrainingSession(session);
		
		// Create new webinar in Zoom
		if ("PROD".equalsIgnoreCase(ENVIRONMENT)) {
			if ("1".equalsIgnoreCase(type)) {
				session.setHostId(session.getAltHostId());
				session.setHostKey(session.getAltHostKey());
				zoomManger.scheduleSessions(session);
			}else if ("2".equalsIgnoreCase(type)) {
				session.setHostId(session.getAltHostId2());
				session.setHostKey(session.getAltHostKey2());
				zoomManger.scheduleSessions(session);
			}else if ("3".equalsIgnoreCase(type)) {
				session.setHostId(session.getAltHostId3());
				session.setHostKey(session.getAltHostKey3());
				zoomManger.scheduleSessions(session);
			}
		}
		
		if (!session.isErrorRecord()) {
			request.setAttribute("success", "true");
			successMessage = successMessage + "Parallel Session Number " + type + " created successfully in Zoom.<br>";

			String dbUpdateError = conferenceBookingDAO.updateZoomWebinarDeatils(session, type);
			if (dbUpdateError == null) {
				successMessage = successMessage + "New Session details saved in database successfully.";
				request.setAttribute("successMessage", successMessage);
			} else {
				errorMessage = errorMessage + dbUpdateError;
				setError(request, errorMessage);
			}
			loggerForSessionScheduling.info("In createParallelSession dbUpdateError: "+dbUpdateError);

		} else {
			errorMessage = errorMessage + "Parallel Session Number " + type + " not created in Zoom: "+ session.getErrorMessage() + " <br>";
			setError(request, errorMessage);
		}

		return viewScheduledSession(request, response);
	}

	private void deleteAndCreateNewMeeting(HttpServletRequest request, HttpServletResponse response, String type) {
		try {
//			String type = request.getParameter("type");
			SessionDayTimeAcadsBean session = conferenceBookingDAO.getSessionForRefresh(request.getParameter("id"));

			String successMessage = "";
			String errorMessage = "";
			String oldMeetingKey = "";

			if ("0".equalsIgnoreCase(type)) {// Below 4 lines not needed, still added for consistency
				// Refreshing original session
				session.setHostId(session.getHostId());
				session.setHostPassword(session.getHostPassword());
				session.setMeetingKey(session.getMeetingKey());
				session.setMeetingPwd(session.getMeetingPwd());

				oldMeetingKey = session.getMeetingKey();

				if ("".equalsIgnoreCase(session.getFacultyLocation()) || session.getFacultyLocation() == null) {
					setError(request, "Session cannot be created. As Location is occupied ");
					return;
				}
			} else if ("1".equalsIgnoreCase(type)) {
				// Refreshing first parallel session
				session.setHostId(session.getAltHostId());
				session.setHostPassword(session.getAltHostPassword());
				session.setMeetingKey(session.getAltMeetingKey());
				session.setMeetingPwd(session.getAltMeetingPwd());

				oldMeetingKey = session.getAltMeetingKey();

				if ("".equalsIgnoreCase(session.getAltFacultyLocation()) || session.getAltFacultyLocation() == null) {
					setError(request, "Session cannot be created. As Location is occupied ");
					return;
				}
			} else if ("2".equalsIgnoreCase(type)) {
				// Refreshing second parallel session
				session.setHostId(session.getAltHostId2());
				session.setHostPassword(session.getAltHostPassword2());
				session.setMeetingKey(session.getAltMeetingKey2());
				session.setMeetingPwd(session.getAltMeetingPwd2());

				oldMeetingKey = session.getAltMeetingKey2();

				if ("".equalsIgnoreCase(session.getAltFaculty2Location()) || session.getAltFaculty2Location() == null) {
					setError(request, "Session cannot be created. As Location is occupied ");
					return;
				}
			} else if ("3".equalsIgnoreCase(type)) {
				// Refreshing third parallel session
				session.setHostId(session.getAltHostId3());
				session.setHostPassword(session.getAltHostPassword3());
				session.setMeetingKey(session.getAltMeetingKey3());
				session.setMeetingPwd(session.getAltMeetingPwd3());

				oldMeetingKey = session.getAltMeetingKey3();

				if ("".equalsIgnoreCase(session.getAltFaculty3Location()) || session.getAltFaculty3Location() == null) {
					setError(request, "Session cannot be created. As Location is occupied ");
					return;
				}
			} else {
				setError(request, "Session cannot be created. Please set parallel session type");
			}

			boolean canUpdate = updateStartTimeIfMeetingPastTime(session);
			if (!canUpdate) {
				setError(request, "Cannot update meeting. Please check if meeting is of past date");
				return;
			}

			// Delete and create new one only if it is already created.
			if (session.getMeetingKey() != null && (!"".equals(session.getMeetingKey().trim()))) {
				webExManager.deleteTrainingSession(session);
				if (!session.isErrorRecord()) {
					request.setAttribute("success", "true");
					successMessage = "Earlier Session deleted successfully<br>";
					request.setAttribute("successMessage", successMessage);
				} else {
					errorMessage = "Old meeting not deleted from WebEx: " + session.getErrorMessage() + " <br>";
					request.setAttribute("error", "true");
					request.setAttribute("errorMessage", errorMessage);
				}

				// Create a new WebEx meeting
				webExManager.scheduleTrainingSession(session);
				if (!session.isErrorRecord()) {
					request.setAttribute("success", "true");
					successMessage = successMessage + "New Session created successfully in WebEx.<br>";

					String dbUpdateError = conferenceBookingDAO.updateWebExDetails(session, type);
					conferenceBookingDAO.updateAttendanceForOldMeeting(session, oldMeetingKey);
					if (dbUpdateError == null) {
						successMessage = successMessage + "New Session details saved in database successfully.";
						request.setAttribute("successMessage", successMessage);
					} else {
						errorMessage = errorMessage + dbUpdateError;
						setError(request, errorMessage);
					}
				} else {
					errorMessage = errorMessage + "New Session not created in Web Ex: " + session.getErrorMessage()
							+ " <br>";
					request.setAttribute("errorMessage", errorMessage);
				}

			}

		} catch (Exception e) {
			  
			request.setAttribute("error", "true");
			request.setAttribute("errorMessage", "Error in refreshing session: " + e.getMessage());
		}
	}

	private boolean updateStartTimeIfMeetingPastTime(SessionDayTimeAcadsBean session) throws ParseException {
		// This method will check if meeting is refreshed after session time, then
		// change start time to current time + 15 minutes

		String sessionDate = session.getDate();
		String sessionTime = session.getStartTime();

		// sessionDate = "2015-02-17";
		// sessionTime = "19:00:00";

		Date sessionDateTime = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(sessionDate + " " + sessionTime);
		Date currentSessionDate = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(sessionDate + " 23:59:00");

		if (currentSessionDate.before(new Date())) {
			return false;
		} else if (sessionDateTime.after(new Date())) {
			return true;
		} else if (sessionDateTime.before(new Date())) {
			// Time is also earlier so no action needed
			
			Calendar cal = Calendar.getInstance();
			cal.setTime(new Date());
			cal.add(Calendar.MINUTE, 15);

			sessionTime = new SimpleDateFormat("HH:mm:ss").format(cal.getTime());
			session.setStartTime(sessionTime);
			return true;
		}

		return true;
	}

	public static long getDateDiff(Date date1, Date date2, TimeUnit timeUnit) {
		long diffInMillies = date2.getTime() - date1.getTime();
		return timeUnit.convert(diffInMillies, TimeUnit.MILLISECONDS);
	}

	private void setOtherSessionsInModel(SessionDayTimeAcadsBean session, ModelAndView modelnView) {
		SessionDayTimeAcadsBean searchBean = new SessionDayTimeAcadsBean();
		searchBean.setYear(session.getYear());
		searchBean.setMonth(session.getMonth());
		searchBean.setSubject(session.getSubject());

		TimeTableDAO dao = (TimeTableDAO)act.getBean("timeTableDAO");
		PageAcads<SessionDayTimeAcadsBean> page = dao.getScheduledSessionPage(1, Integer.MAX_VALUE, searchBean, "distinct");
		List<SessionDayTimeAcadsBean> scheduledSessionList = page.getPageItems();
		modelnView.addObject("scheduledSessionList", scheduledSessionList);

	}

	@RequestMapping(value = "/updateScheduledSession", method = { RequestMethod.GET, RequestMethod.POST })
	public ModelAndView updateScheduledSession(HttpServletRequest request, HttpServletResponse response, @ModelAttribute SessionDayTimeAcadsBean session) {
		try{
			
			if (!checkSession(request, response)) {
					return new ModelAndView("studentPortalRediret");
			}
			String isRecommendationSession = request.getParameter("isRecSession") != null ? "Y" : "N";
			String index = request.getParameter("index") != null ? request.getParameter("index") : "" ;
			loggerForSessionScheduling.info("In updateScheduledSession isRecommendationSession: "+isRecommendationSession+" index: "+index);
			
			if (isRecommendationSession.equalsIgnoreCase("Y")) {
				ArrayList<SessionDayTimeAcadsBean> availableSolts = (ArrayList<SessionDayTimeAcadsBean>) request.getSession().getAttribute("availableSolts");
				SessionDayTimeAcadsBean sessionBean = availableSolts.get(Integer.parseInt(index)-1);
				session.setDate(sessionBean.getDate());
				session.setStartTime(sessionBean.getStartTime());
			}
			
			SessionDayTimeAcadsBean tempSession = new SessionDayTimeAcadsBean();
            tempSession.setDate(session.getDate());
            tempSession.setStartTime(session.getStartTime());
            tempSession.setYear(session.getYear());
            tempSession.setMonth(session.getMonth());
			
			String oldSessionId = session.getId();
			String consumerProgramStructureId = session.getConsumerProgramStructureId();
			SessionDayTimeAcadsBean sessionBeforeEdit = (SessionDayTimeAcadsBean)request.getSession().getAttribute("sessionBeforeEdit");

			boolean dateTimeSubjectChanged = (!sessionBeforeEdit.getDate().equals(session.getDate()))
					|| (!sessionBeforeEdit.getStartTime().equals(session.getStartTime()))
					|| (!sessionBeforeEdit.getSubject().equals(session.getSubject()))
					|| (!sessionBeforeEdit.getSessionName().equals(session.getSessionName()));

			boolean dateTimeChanged = (!sessionBeforeEdit.getDate().equals(session.getDate()))
					|| (!sessionBeforeEdit.getStartTime().equals(session.getStartTime()));

			loggerForSessionScheduling.info("In updateScheduledSession dateTimeSubjectChanged: "+dateTimeSubjectChanged+", dateTimeChanged: "+dateTimeChanged);
			
			TimeTableDAO dao = (TimeTableDAO) act.getBean("timeTableDAO");
			String userId = (String) request.getSession().getAttribute("userId_acads");
			//No recommendation on update
			//session.setIsRecommendationSession("ByPass");
			session.setLastModifiedBy(userId);

			if (!getFacultyList().contains(session.getFacultyId())) {
				return sendToErrorPage(request, session, "Invalid Faculty ID:" + session.getFacultyId(), "true");
			}
//			if (!"Y".equalsIgnoreCase(session.getByPassChecks())) {

			
			boolean isDateTimeValid = dao.isDateTimeValid(session);
			if (!isDateTimeValid) {
				return sendToErrorPage(request, session, "Date " + session.getDate() + " and Time "
						+ session.getStartTime()
						+ " is not valid as per agreed days and time slot OR not within Academic Calendar dates.","true");
			}
				
			if (dateTimeSubjectChanged) {
				if (!"Y".equalsIgnoreCase(session.getByPassFaculty())) {
					if (!StringUtils.isBlank(session.getFacultyId())) {
					boolean isFacultyAvailable = dao.isFacultyAvailable(session);
					if (!isFacultyAvailable) {
						if (!session.getIsRecommendationSession().equalsIgnoreCase("ByPass") || session.getIsRecommendationSession().equalsIgnoreCase("N")){
							getRecommendationSession(session, request);
						}
						return sendToErrorPage(request, session, "Faculty is NOT available on " + session.getDate()+" at "+session.getStartTime(),"true");
					}
					
					boolean isFacultyFree = dao.isFacultyFreeAllChecksV2(session);
					if (!isFacultyFree) {
						ArrayList<SessionDayTimeAcadsBean> facultySession = dao.getFacultyClashDeatils(session);
						String msg = "";
						for (SessionDayTimeAcadsBean bean : facultySession) {
							msg = msg + "Faculty is occupied taking "+bean.getSessionName()+" of Subject : "+bean.getSubject()+
									 	" of "+bean.getTrack()+" on " + bean.getDate() + " at " + bean.getStartTime()+ " /2<br>";
						}
						if (!session.getIsRecommendationSession().equalsIgnoreCase("ByPass") || session.getIsRecommendationSession().equalsIgnoreCase("N")){
							getRecommendationSession(session, request);
						}
						return sendToErrorPage(request, session,msg,"true");
					}
					
					boolean isFacultyTakingLessThan2SubjectsSameDay = dao.isFacultyTakingLessThan2SubjectsSameDay(session);
					if (!isFacultyTakingLessThan2SubjectsSameDay) {
						ArrayList<SessionDayTimeAcadsBean> facultySessions = dao.getSameDaySessionsForFaculty(session);
						String msg = "Faculty " + session.getFacultyId() + " is already taking 2 sessions on "+ session.getDate()+ "<br>";
						int count = 1;
						for (SessionDayTimeAcadsBean bean : facultySessions) {
							msg = msg + count +". Subject : "+bean.getSubject()+ " on "+bean.getDate()
									 +" at "+bean.getStartTime()+ " of Track : "+bean.getTrack()+ "<br>";
							count++;
						}
						if (!session.getIsRecommendationSession().equalsIgnoreCase("ByPass") || session.getIsRecommendationSession().equalsIgnoreCase("N")){
							getRecommendationSession(session, request);
						}
						return sendToErrorPage(request, session, msg, "true");
					}
					
					// If date, time, subject is changed, then do this check. If it not changed,
					// meaning only faculty is changed, then this check is not needed.
					/*
					 boolean isSlotAvailable = dao.isSlotAvailable(session);
					 if(!isSlotAvailable){ 
					 	return sendToErrorPage(request, session, "Already 6 sessions going on "+session.getDate() + " at "+session.getStartTime(), "true");
					 }
					 */
					}
					
					//Check AltFacultyId Start
					if (!StringUtils.isBlank(sessionBeforeEdit.getAltFacultyId())) {
						session.setAltFacultyId(sessionBeforeEdit.getAltFacultyId());
						session.setAltFacultyLocation(sessionBeforeEdit.getAltFacultyLocation());
						
						tempSession.setFacultyId(sessionBeforeEdit.getAltFacultyId());
						boolean isFacultyAvailable = dao.isFacultyAvailable(tempSession);
						if (!isFacultyAvailable) {
							session.setFacultyId(sessionBeforeEdit.getAltFacultyId());
							if (!session.getIsRecommendationSession().equalsIgnoreCase("ByPass") || session.getIsRecommendationSession().equalsIgnoreCase("N")){
								getRecommendationSession(session, request);
							}
							return sendToErrorPage(request, session, "Parallel 1 Faculty is NOT available on " + session.getDate()+" at "+session.getStartTime(),"true");
						}
						
						boolean isFacultyFree = dao.isFacultyFreeAllChecksV2(tempSession);
						if (!isFacultyFree) {
							session.setFacultyId(sessionBeforeEdit.getAltFacultyId());
							ArrayList<SessionDayTimeAcadsBean> facultySession = dao.getFacultyClashDeatils(session);
							String msg = "";
							for (SessionDayTimeAcadsBean bean : facultySession) {
								msg = msg + "Parallel 1 Faculty is occupied taking "+bean.getSessionName()+" of Subject : "+bean.getSubject()+
										 	" of "+bean.getTrack()+" on " + bean.getDate() + " at " + bean.getStartTime()+ " /2<br>";
							}
							if (!session.getIsRecommendationSession().equalsIgnoreCase("ByPass") || session.getIsRecommendationSession().equalsIgnoreCase("N")){
								getRecommendationSession(session, request);
							}
							return sendToErrorPage(request, session,msg,"true");
						}
						
						boolean isFacultyTakingLessThan2SubjectsSameDay = dao.isFacultyTakingLessThan2SubjectsSameDay(tempSession);
						if (!isFacultyTakingLessThan2SubjectsSameDay) {
							session.setFacultyId(sessionBeforeEdit.getAltFacultyId());
							ArrayList<SessionDayTimeAcadsBean> facultySessions = dao.getSameDaySessionsForFaculty(session);
							String msg = "Parallel 1 Faculty is already taking 2 sessions on "+ session.getDate()+ "<br>";
							int count = 1;
							for (SessionDayTimeAcadsBean bean : facultySessions) {
								msg = msg + count +". Subject : "+bean.getSubject()+ " on "+bean.getDate()
										 +" at "+bean.getStartTime()+ " of Track : "+bean.getTrack()+ "<br>";
								count++;
							}
							if (!session.getIsRecommendationSession().equalsIgnoreCase("ByPass") || session.getIsRecommendationSession().equalsIgnoreCase("N")){
								getRecommendationSession(session, request);
							}
							return sendToErrorPage(request, session, msg, "true");
						}
						
						// If date, time, subject is changed, then do this check. If it not changed,
						// meaning only faculty is changed, then this check is not needed.
						/*
						 boolean isSlotAvailable = dao.isSlotAvailable(session);
						 if(!isSlotAvailable){ 
						 	return sendToErrorPage(request, session, "Already 6 sessions going on "+session.getDate() + " at "+session.getStartTime(), "true");
						 }
						 */
						}
					//Check AltFacultyId End
					
					//Check AltFacultyId2 Start
					if (!StringUtils.isBlank(sessionBeforeEdit.getAltFacultyId2())) {
						session.setAltFacultyId2(sessionBeforeEdit.getAltFacultyId2());
						session.setAltFaculty2Location(sessionBeforeEdit.getAltFaculty2Location());
						
						tempSession.setFacultyId(sessionBeforeEdit.getAltFacultyId2());
						boolean isFacultyAvailable = dao.isFacultyAvailable(tempSession);
						if (!isFacultyAvailable) {
							session.setFacultyId(sessionBeforeEdit.getAltFacultyId2());
							if (!session.getIsRecommendationSession().equalsIgnoreCase("ByPass") || session.getIsRecommendationSession().equalsIgnoreCase("N")){
								getRecommendationSession(session, request);
							}
							return sendToErrorPage(request, session, "Parallel 2 is NOT available on " + session.getDate()+" at "+session.getStartTime(),"true");
						}
						
						boolean isFacultyFree = dao.isFacultyFreeAllChecksV2(tempSession);
						if (!isFacultyFree) {
							session.setFacultyId(sessionBeforeEdit.getAltFacultyId2());
							ArrayList<SessionDayTimeAcadsBean> facultySession = dao.getFacultyClashDeatils(session);
							String msg = "";
							for (SessionDayTimeAcadsBean bean : facultySession) {
								msg = msg + "Parallel 2 Faculty is occupied taking "+bean.getSessionName()+" of Subject : "+bean.getSubject()+
										 	" of "+bean.getTrack()+" on " + bean.getDate() + " at " + bean.getStartTime()+ " /2<br>";
							}
							if (!session.getIsRecommendationSession().equalsIgnoreCase("ByPass") || session.getIsRecommendationSession().equalsIgnoreCase("N")){
								getRecommendationSession(session, request);
							}
							return sendToErrorPage(request, session,msg,"true");
						}
						
						boolean isFacultyTakingLessThan2SubjectsSameDay = dao.isFacultyTakingLessThan2SubjectsSameDay(tempSession);
						if (!isFacultyTakingLessThan2SubjectsSameDay) {
							session.setFacultyId(sessionBeforeEdit.getAltFacultyId2());
							ArrayList<SessionDayTimeAcadsBean> facultySessions = dao.getSameDaySessionsForFaculty(session);
							String msg = "Parallel 2 Faculty is already taking 2 sessions on "+ session.getDate()+ "<br>";
							int count = 1;
							for (SessionDayTimeAcadsBean bean : facultySessions) {
								msg = msg + count +". Subject : "+bean.getSubject()+ " on "+bean.getDate()
										 +" at "+bean.getStartTime()+ " of Track : "+bean.getTrack()+ "<br>";
								count++;
							}
							if (!session.getIsRecommendationSession().equalsIgnoreCase("ByPass") || session.getIsRecommendationSession().equalsIgnoreCase("N")){
								getRecommendationSession(session, request);
							}
							return sendToErrorPage(request, session, msg, "true");
						}
						
						// If date, time, subject is changed, then do this check. If it not changed,
						// meaning only faculty is changed, then this check is not needed.
						/*
						 boolean isSlotAvailable = dao.isSlotAvailable(session);
						 if(!isSlotAvailable){ 
						 	return sendToErrorPage(request, session, "Already 6 sessions going on "+session.getDate() + " at "+session.getStartTime(), "true");
						 }
						 */
						}
					//Check AltFacultyId2 End
					
					//Check AltFacultyId3 Start
					if (!StringUtils.isBlank(sessionBeforeEdit.getAltFacultyId3())) {
						session.setAltFacultyId3(sessionBeforeEdit.getAltFacultyId3());
						session.setAltFaculty3Location(sessionBeforeEdit.getAltFaculty3Location());
						
						tempSession.setFacultyId(sessionBeforeEdit.getAltFacultyId3());
						boolean isFacultyAvailable = dao.isFacultyAvailable(tempSession);
						if (!isFacultyAvailable) {
							session.setFacultyId(sessionBeforeEdit.getAltFacultyId3());
							if (!session.getIsRecommendationSession().equalsIgnoreCase("ByPass") || session.getIsRecommendationSession().equalsIgnoreCase("N")){
								getRecommendationSession(session, request);
							}
							return sendToErrorPage(request, session, "Parallel 3 Faculty is NOT available on " + session.getDate()+" at "+session.getStartTime(),"true");
						}
						
						boolean isFacultyFree = dao.isFacultyFreeAllChecksV2(tempSession);
						if (!isFacultyFree) {
							session.setFacultyId(sessionBeforeEdit.getAltFacultyId3());
							ArrayList<SessionDayTimeAcadsBean> facultySession = dao.getFacultyClashDeatils(session);
							String msg = "";
							for (SessionDayTimeAcadsBean bean : facultySession) {
								msg = msg + "Parallel 3 Faculty is occupied taking "+bean.getSessionName()+" of Subject : "+bean.getSubject()+
										 	" of "+bean.getTrack()+" on " + bean.getDate() + " at " + bean.getStartTime()+ " /2<br>";
							}
							if (!session.getIsRecommendationSession().equalsIgnoreCase("ByPass") || session.getIsRecommendationSession().equalsIgnoreCase("N")){
								getRecommendationSession(session, request);
							}
							return sendToErrorPage(request, session,msg,"true");
						}
						
						boolean isFacultyTakingLessThan2SubjectsSameDay = dao.isFacultyTakingLessThan2SubjectsSameDay(tempSession);
						if (!isFacultyTakingLessThan2SubjectsSameDay) {
							session.setFacultyId(sessionBeforeEdit.getAltFacultyId3());
							ArrayList<SessionDayTimeAcadsBean> facultySessions = dao.getSameDaySessionsForFaculty(session);
							String msg = "Parallel 3 Faculty is already taking 2 sessions on "+ session.getDate()+ "<br>";
							int count = 1;
							for (SessionDayTimeAcadsBean bean : facultySessions) {
								msg = msg + count +". Subject : "+bean.getSubject()+ " on "+bean.getDate()
										 +" at "+bean.getStartTime()+ " of Track : "+bean.getTrack()+ "<br>";
								count++;
							}
							if (!session.getIsRecommendationSession().equalsIgnoreCase("ByPass") || session.getIsRecommendationSession().equalsIgnoreCase("N")){
								getRecommendationSession(session, request);
							}
							return sendToErrorPage(request, session, msg, "true");
						}
						
						// If date, time, subject is changed, then do this check. If it not changed,
						// meaning only faculty is changed, then this check is not needed.
						/*
						 boolean isSlotAvailable = dao.isSlotAvailable(session);
						 if(!isSlotAvailable){ 
						 	return sendToErrorPage(request, session, "Already 6 sessions going on "+session.getDate() + " at "+session.getStartTime(), "true");
						 }
						 */
						}
					//Check AltFacultyId3 End
					
					}
				}

				boolean isNotHoliday = dao.isNotHoliday(session);
				if (!isNotHoliday) {
					return sendToErrorPage(request, session, session.getDate() + " is a Holiday ", "true");
				}

				if (dateTimeSubjectChanged) {
				if (!"Y".equalsIgnoreCase(session.getByPassChecks())) {
					if ((getSemSubjectNProgramSubjectBeanMap().containsKey("1-" + session.getSubject())
							|| getSemSubjectNProgramSubjectBeanMap().containsKey("2-" + session.getSubject()))
							&& !StringUtils.isBlank(session.getTrack())) {

						// Check for not more than 3 subjects to be scheduled on Same day check by
						// Program Sem Track

						boolean isNotMoreThanLimitSubjectsSameDayByProgSemTrack = dao.isNotMoreThanLimitSubjectsSameDayByProgSemTrackV3(session);
						if (!isNotMoreThanLimitSubjectsSameDayByProgSemTrack) {
							
							ArrayList<SessionDayTimeAcadsBean>moreThan3SessionClashList = get3SessionsClashingSubjectList(session);
							String table =  errorTableFormat(moreThan3SessionClashList);
							if (!session.getIsRecommendationSession().equalsIgnoreCase("ByPass") || session.getIsRecommendationSession().equalsIgnoreCase("N")){
								getRecommendationSession(session, request);
							}
							return sendToErrorPage(request, session,
									"Already 3 sessions of Program Semester Track going on " + session.getDate()
											+ " of track " + session.getTrack()+ " /1 <br>"+table,"true");
						}

						boolean isNotMoreThan3CommonSubjectsSameDay = dao.isNotMoreThan3CommonSubjectsSameDayByTrack(session);
						if (!isNotMoreThan3CommonSubjectsSameDay) {
							ArrayList<SessionDayTimeAcadsBean>moreThan3SessionClashList = get3SessionsClashingSubjectList(session);
							String table =  errorTableFormat(moreThan3SessionClashList);
							if (!session.getIsRecommendationSession().equalsIgnoreCase("ByPass") || session.getIsRecommendationSession().equalsIgnoreCase("N")){
								getRecommendationSession(session, request);
							}
							return sendToErrorPage(request, session, "Already 3 sessions of common subjects going on "
									+ session.getDate() + " of track " + session.getTrack()+" <br>"+table, "true");
						}

						// boolean isNoSubjectClashing = dao.isNoSubjectClashingByTrack(session);
						boolean isNoSubjectClashing = dao.isNoSubjectClashingV3(session);
						String specializationTypeOfSessionToBeScheduled = dao.getspecializationTypeBySubjectCode(session.getSubjectCode());

						if(!isNoSubjectClashing){
							ArrayList<SessionDayTimeAcadsBean> clashingList = getClashingSubjectList(session);
							String msg = checkClashingOfSubjectCodes(clashingList, specializationTypeOfSessionToBeScheduled, session);
							
							if (!StringUtils.isBlank(msg)) {
								if (!session.getIsRecommendationSession().equalsIgnoreCase("ByPass") || session.getIsRecommendationSession().equalsIgnoreCase("N")){
									getRecommendationSession(session, request);
								}
								return sendToErrorPage(request, session, msg, "true");
							}
						}

					} else {

						boolean isNoSubjectClashing = dao.isNoSubjectClashingV3(session);
						
						String specializationTypeOfSessionToBeScheduled = dao.getspecializationTypeBySubjectCode(session.getSubjectCode());
						
						if(!isNoSubjectClashing){
							ArrayList<SessionDayTimeAcadsBean> clashingList = getClashingSubjectList(session);
							String msg = checkClashingOfSubjectCodes(clashingList, specializationTypeOfSessionToBeScheduled, session);
							
							if (!StringUtils.isBlank(msg)) {
								if (!session.getIsRecommendationSession().equalsIgnoreCase("ByPass") || session.getIsRecommendationSession().equalsIgnoreCase("N")){
									getRecommendationSession(session, request);
								}
								return sendToErrorPage(request, session, msg, "true");
							}
						}

						//Commented by Somesh on 20/07/2021 as we are already Checking More than 3 session on same day by track
						/*
						boolean isNotMoreThan3CommonSubjectsSameDay = dao.isNotMoreThan3CommonSubjectsSameDayByCorporateName(session);
						if (!isNotMoreThan3CommonSubjectsSameDay) {
							ArrayList<SessionDayTimeBean>moreThan3SessionClashList = get3SessionsClashingSubjectList(session);
							String table =  errorTableFormat(moreThan3SessionClashList);
							if (!session.getIsRecommendationSession().equalsIgnoreCase("ByPass") || session.getIsRecommendationSession().equalsIgnoreCase("N")){
								getRecommendationSession(session, request);
							}
							return sendToErrorPage(request, session, "Already 3 sessions of common subjects going on " + session.getDate() +" </br>"+table, "true");
						}
						*/

						// Check for not more than 3 subjects to be scheduled on Same day check by
						// Program Sem Track
						List<String> tracks = dao.getTracks(session);
						String tempTrack=session.getTrack();
						for (String track : tracks) {
							session.setTrack(track);
							boolean isNotMoreThanLimitSubjectsSameDayByProgSemTrack = dao.isNotMoreThanLimitSubjectsSameDayByProgSemTrackV3(session);
							session.setTrack(tempTrack);
							if (!isNotMoreThanLimitSubjectsSameDayByProgSemTrack) {
								
								ArrayList<SessionDayTimeAcadsBean>moreThan3SessionClashList = get3SessionsClashingSubjectList(session);
								String table =  errorTableFormat(moreThan3SessionClashList);

								if (!session.getIsRecommendationSession().equalsIgnoreCase("ByPass") || session.getIsRecommendationSession().equalsIgnoreCase("N")){
									getRecommendationSession(session, request);
								}
								return sendToErrorPage(request, session, "Already 3 sessions of Program Semester Track going on " + session.getDate()
												+ " of track " + session.getTrack()+ " /2 <br>"+table,"true");
							}
						}
					}
				}
				}

				/*
				 * if(getSasSubjectsList().contains(session.getSubject())){ List<String>
				 * StudentSubjectListOfHavingSaSAndOtherProgramActive =
				 * getStudentSubjectListOfHavingSaSAndOtherProgramActive(dao.
				 * getLiveAcadSessionMonth(),dao.getLiveAcadSessionYear()); ArrayList<String>
				 * lstOfClashingSubjects =
				 * dao.getSessionScheduledOnSameDayTime(session.getDate(),session.getStartTime()
				 * ,StudentSubjectListOfHavingSaSAndOtherProgramActive);
				 * if(lstOfClashingSubjects.size() > 0 && lstOfClashingSubjects != null &&
				 * !lstOfClashingSubjects.isEmpty()){ return sendToErrorPage(request,
				 * session,"Session Time "+session.getStartTime() +
				 * " clashing with "+String.join(",", lstOfClashingSubjects)
				 * +" sessions of SAS students PG/Diploma/Certificate program on " +
				 * session.getDate(), "true") ; } }
				 * 
				 * if(getStudentSubjectListOfHavingSaSAndOtherProgramActive(dao.
				 * getLiveAcadSessionMonth(),dao.getLiveAcadSessionYear()).contains(session.
				 * getSubject())){ List<String>
				 * StudentsSASSubjectListOfHavingSaSAndOtherProgramActive =
				 * getStudentsSASSubjectListOfHavingSaSAndOtherProgramActive(dao.
				 * getLiveAcadSessionMonth(),dao.getLiveAcadSessionYear()); ArrayList<String>
				 * lstOfClashingSubjects =
				 * dao.getSessionScheduledOnSameDayTime(session.getDate(),session.getStartTime()
				 * ,StudentsSASSubjectListOfHavingSaSAndOtherProgramActive);
				 * if(lstOfClashingSubjects.size() > 0 && lstOfClashingSubjects != null &&
				 * !lstOfClashingSubjects.isEmpty()){ return sendToErrorPage(request,
				 * session,"Session Time "+session.getStartTime() +
				 * " clashing with "+String.join(",", lstOfClashingSubjects)
				 * +" sessions of SAS students program on " + session.getDate(), "true") ; } }
				 */

//			}

			int capacityOfLocation = dao.getCapacityOfLocation(session.getFacultyLocation());
			int usedCapacity = dao.getNoOfSessionAtSameDateTimeLocation(session.getDate(), session.getStartTime(),
					session.getFacultyLocation(), session.getId());
			int availableCapacity = capacityOfLocation - usedCapacity;

			if(dateTimeChanged){
				//Set up Web Ex ID/Password/Room for new date and time
				//String type = request.getParameter("type") != null ? request.getParameter("type") : "0";
				session = allocateAvailableRoomForBatchUpload(session);
				
				if (session.getErrorMessage() != null && !"".equals(session.getErrorMessage())) {
					return sendToErrorPage(request, session, session.getErrorMessage(), "true");
				}
				
				//Get count of applicable cpsid for Session 
				int sessionCount = dao.getSessionApplicableCount(session.getId());
				if (sessionCount == 0) {
					return sendToErrorPage(request, session, "No Mapping found for this session.", "true");
				}
				
				//If Edit only one session then 
				//Create new entry into session & Update sessionSubjectMapping
				int sizeOfCPSId = session.getConsumerProgramStructureId().split(",").length;
				loggerForSessionScheduling.info("In updateScheduledSession sessionCount: "+sessionCount+ "sizeOfCPSId: "+sizeOfCPSId);
				
				if (sizeOfCPSId >= 1 														
						// if noOfsessions for count not match then create new session
						&& sessionCount > 1 && sessionCount != sizeOfCPSId
						// MBA-Wx program applicable for only one program
						&& !session.getConsumerProgramStructureId().contains("111")	&& !session.getConsumerProgramStructureId().contains("151")
						&& !session.getConsumerProgramStructureId().contains("160")
						// M.Sc. (AI & ML Ops) || M.Sc. (AI) program applicable for only one program
						&& !session.getConsumerProgramStructureId().contains("131") && !session.getConsumerProgramStructureId().contains("158")
						// PC-DS || PD-DS
						&& !session.getConsumerProgramStructureId().contains("154") && !session.getConsumerProgramStructureId().contains("155")) {
					
					boolean isSessionInserted = dao.insertNewSessionAfterSessionUpdate(session, userId, oldSessionId);
					if (!isSessionInserted) {
						return sendToErrorPage(request, session, "Error in saving session to database.", "true");
					}
					
					//Commented by Somesh on 04-06-2021 as Added transactional
					/*
					long sessionId = dao.insertNewSession(session, userId);
					if (sessionId == 0) {
						return sendToErrorPage(request, session, "Error in saving session to database.", "true");
					}else{
						//Set new id
						session.setId(String.valueOf(sessionId));
						dao.updateSessionSubjectMapping(sessionId, userId, oldSessionId, consumerProgramStructureId);
						//Add New Session in Quick Session
						ArrayList<SessionDayTimeBean> sessionWithPSSIds = dao.getAllPSSSessionsMapping(sessionId);
						boolean isInserted = dao.insertQuickSession(sessionWithPSSIds, sessionId);
						if (!isInserted) {
							request.setAttribute("error", "true");
							request.setAttribute("successMessage", "Session Updated successfully. Error while inserting in quick session");
							return searchScheduledSession(request, response, session);
						}
					}
					*/
				}else {
					//Update Host details for the session
					conferenceBookingDAO.updateAllRooms(session);
					
					//Update session date/time after selecting room
					boolean isUpdated = dao.updateScheduledSession(session);
					if (!isUpdated) {
						return sendToErrorPage(request, session, "Error in saving session to database.", "true");
					}
					
					//Delete old Zoom meeting and create a new one
					if ("PROD".equalsIgnoreCase(ENVIRONMENT)) {
						deleteAndCreateNewWebinar(request, response, "0");
						if (!StringUtils.isBlank(sessionBeforeEdit.getAltFacultyId())) {
							deleteAndCreateNewWebinar(request, response, "1");
						}
						if (!StringUtils.isBlank(sessionBeforeEdit.getAltFacultyId2())) {
							deleteAndCreateNewWebinar(request, response, "2");
						}
						if (!StringUtils.isBlank(sessionBeforeEdit.getAltFacultyId3())) {
							deleteAndCreateNewWebinar(request, response, "3");
						}
					}
				}
				
			} else {
				boolean isUpdated = dao.updateScheduledSession(session);
				if (!isUpdated) {
					return sendToErrorPage(request, session, "Error in saving session to database.", "true");
				}
			}
			
			//Commented by Somesh on 04-06-2021 as Added transactional
			/*
			//Delete Sessions from quick session and Add updated session
			long sessionId = Long.parseLong(oldSessionId);
			boolean isDeleted = dao.deleteQuickSessions(sessionId);
			if (!isDeleted) {
				request.setAttribute("error", "true");
				request.setAttribute("successMessage", "Session Updated successfully. Error while updating in quick session");
				return searchScheduledSession(request, response, session);
			}
			
			ArrayList<SessionDayTimeBean> sessionWithPSSIds = dao.getAllPSSSessionsMapping(sessionId);
			boolean isInserted = dao.insertQuickSession(sessionWithPSSIds, sessionId);
			if (!isInserted) {
				request.setAttribute("error", "true");
				request.setAttribute("successMessage", "Session Updated successfully. Error while inserting in quick session");
				return searchScheduledSession(request, response, session);
			}
			*/
			
			if ("Y".equalsIgnoreCase(session.getHasModuleId())) {
				String idForSession = dao.getSessionId(session);
				session.setId(idForSession);
				dao.updateSessionPost(session);
			}
			session.setSessionName("");
			request.setAttribute("success", "true");
			request.setAttribute("successMessage", "Session Updated successfully");
			return searchScheduledSession(request, response, session);

		} catch (Exception e) {
			loggerForSessionScheduling.info("Error in updateScheduledSession "+e.getMessage());
			return sendToErrorPage(request, session, "Error in updating session : "+ e.getMessage(), "true");
		}

	}

	@RequestMapping(value = "/updateSessionName", method = { RequestMethod.GET, RequestMethod.POST })
	public ModelAndView updateSessionName(HttpServletRequest request, HttpServletResponse response,
			@ModelAttribute SessionDayTimeAcadsBean session) {

		try {
			TimeTableDAO dao = (TimeTableDAO) act.getBean("timeTableDAO");
			String userId = (String) request.getSession().getAttribute("userId_acads");
			
			//Get count of applicable cpsid for Session
			int sessionCount = dao.getSessionApplicableCount(session.getId());
			if (sessionCount == 0) {
				return sendToErrorPage(request, session, "No Mapping found for this session.", "true");
			}
			
			int sizeOfCPSId = session.getConsumerProgramStructureId().split(",").length;
			loggerForSessionScheduling.info("In updateSessionName ConsumerProgramStructureId: "+session.getConsumerProgramStructureId()+
					" sessionCount: "+sessionCount+" sizeOfCPSId: "+sizeOfCPSId);
			String oldSessionId = session.getId();
			
			if (sizeOfCPSId >= 1 														
					// if noOfsessions for count not match then create new session
					&& sessionCount > 1 && sessionCount != sizeOfCPSId
					// MBA-Wx program applicable for only one program
					&& !session.getConsumerProgramStructureId().contains("111")	&& !session.getConsumerProgramStructureId().contains("151")
					&& !session.getConsumerProgramStructureId().contains("160")
					// M.Sc. (AI & ML Ops) || M.Sc. (AI) program applicable for only one program
					&& !session.getConsumerProgramStructureId().contains("131") && !session.getConsumerProgramStructureId().contains("158")
					// PC-DS || PD-DS
					&& !session.getConsumerProgramStructureId().contains("154") && !session.getConsumerProgramStructureId().contains("155")) {
				
				//Setting hostId blank so new HostId will get allocate 
				session.setHostId("");
				session = allocateAvailableRoomForBatchUpload(session);
				
				boolean isSessionInserted = dao.insertNewSessionAfterSessionNameUpdate(session, userId, oldSessionId);
				if (!isSessionInserted) {
					return sendToErrorPage(request, session, "Error in saving session to database.", "true");
				}
			} else {
				boolean isUpdated = dao.updateScheduledSession(session);
				if (!isUpdated) {
					return sendToErrorPage(request, session, "Error in saving session to database.", "true");
				}
			}
			
			if (ENVIRONMENT.equalsIgnoreCase("PROD")) {
				zoomManger.updateSession(session);
				if (!StringUtils.isBlank(session.getAltFacultyId())) {
					session.setMeetingKey(session.getAltMeetingKey());
					zoomManger.updateSession(session);
				}
				if (!StringUtils.isBlank(session.getAltFacultyId2())) {
					session.setMeetingKey(session.getAltMeetingKey2());
					zoomManger.updateSession(session);
				}
				if (!StringUtils.isBlank(session.getAltFacultyId3())) {
					session.setMeetingKey(session.getAltMeetingKey3());
					zoomManger.updateSession(session);
				}
			}
			
			if (!session.isErrorRecord()) {
				boolean isUpdated = dao.updateSessionName(session, userId);
				if (isUpdated) {
					session.setSessionName("");
					request.setAttribute("success", "true");
					request.setAttribute("successMessage", "Session Name Updated successfully");
					return searchScheduledSession(request, response, session);
				}else {
					return sendToErrorPage(request, session,"Session Name updated in zoom successfully. Error while updating Session Name in DB", "true");
				}
			}else {
				return sendToErrorPage(request, session,"Error in Zoom while updating Session Name", "true");
			}
		} catch (Exception e) {
			loggerForSessionScheduling.debug("Error in updateSessionName: "+e.getMessage());
			return sendToErrorPage(request, session, e.getMessage(), "true");
		}
	}

	@RequestMapping(value = "/updateTrackDetails", method = { RequestMethod.GET, RequestMethod.POST })
	public ModelAndView updateTrackDetails(HttpServletRequest request, HttpServletResponse response,
			@ModelAttribute SessionDayTimeAcadsBean session) {

		try {
			TimeTableDAO dao = (TimeTableDAO) act.getBean("timeTableDAO");
			String userId = (String) request.getSession().getAttribute("userId_acads");
			SessionDayTimeAcadsBean sessionBeforeEdit = (SessionDayTimeAcadsBean) request.getSession().getAttribute("sessionBeforeEdit");
			String sessionDay = dao.getSessionDay(session);

			boolean dateTimeSubjectChanged = (!sessionBeforeEdit.getDate().equals(session.getDate()))
					|| (!sessionBeforeEdit.getStartTime().equals(session.getStartTime()))
					|| (!sessionBeforeEdit.getSubject().equals(session.getSubject()))
					|| (!sessionBeforeEdit.getSessionName().equals(session.getSessionName()));

			boolean dateTimeChanged = (!sessionBeforeEdit.getDate().equals(session.getDate()))
					|| (!sessionBeforeEdit.getStartTime().equals(session.getStartTime()));

			if (dateTimeSubjectChanged || dateTimeChanged) {
				return sendToErrorPage(request, session,
						"Date, Time or Subject is changed. Please use 'Update Details' button instead of 'Update Track'", "true");
			}
			
			loggerForSessionScheduling.info("In updateTrackDetails dateTimeSubjectChanged: "+dateTimeSubjectChanged+" dateTimeChanged: "+dateTimeChanged);
			loggerForSessionScheduling.info("In updateTrackDetails New Track: "+session.getTrack()+ " Old Track: "+sessionBeforeEdit.getTrack());

			boolean trackChnaged = !sessionBeforeEdit.getTrack().equals(session.getTrack());
			if (!trackChnaged) {
				return sendToErrorPage(request, session, "Track is not changed. Wrong button used!", "true");
			}

			// Check isWeekDays contains sessionDay and Track is Weekday Batch
			// Check isWeekEndDays contains sessionDay and Track is Week End Batch - Fast
			// Track or Week End Batch - Slow Track
			
			String track = session.getTrack();
				//WeekDay Sessions
			if ((weekDays.contains(sessionDay) && ("Weekday Batch".equalsIgnoreCase(track) ||
					"Weekday Slow - Track 2".equalsIgnoreCase(track) || "Weekday Fast - Track 5".equalsIgnoreCase(track)))
				
				//WeekEnd Session
				||  ((weekEndDays.contains(sessionDay)) && (
						"Weekend Batch - Slow Track".equalsIgnoreCase(track) || "Weekend Batch - Fast Track".equalsIgnoreCase(track) ||
						"Weekend Slow - Track 1".equalsIgnoreCase(track) || "Weekend Slow - Track 3".equalsIgnoreCase(track) || "Weekend Fast - Track 4".equalsIgnoreCase(track)
					))
				
				//AllWeek Sessions
				|| ((allWeekDays.contains(sessionDay)) && ("Sem I - All Week - Track 5".equalsIgnoreCase(track) ||
						"Sem II - All Week".equalsIgnoreCase(track)))
				
				//UG Sessions
				|| ("WeekDay Batch - Slow Track".equalsIgnoreCase(track) || "WeekDay Batch - Fast Track".equalsIgnoreCase(track) ||
						"Weekday Batch - Track 1".equalsIgnoreCase(track)  || "Weekday Batch - Track 2".equalsIgnoreCase(track))
				) {
				// Do nothing
			} else {
				return sendToErrorPage(request, session,
						"Unable to changed, " + sessionDay + " is not coming in " + session.getTrack() + "", "false");
			}

			if ((getSemSubjectNProgramSubjectBeanMap().containsKey("1-" + session.getSubject())
					|| getSemSubjectNProgramSubjectBeanMap().containsKey("2-" + session.getSubject()))
					&& !StringUtils.isBlank(session.getTrack())) {

				boolean isNotMoreThan3CommonSubjectsSameDay = dao.isNotMoreThan3CommonSubjectsSameDayByTrack(session);
				
				if (!isNotMoreThan3CommonSubjectsSameDay) {
					return sendToErrorPage(request, session, "Already 3 sessions of common subjects going on "
							+ session.getDate() + " of track " + session.getTrack(), "true");
				}

				boolean isNoSubjectClashing = dao.isNoSubjectClashingByTrack(session);
				String specializationTypeOfSessionToBeScheduled = dao.getspecializationTypeBySubjectCode(session.getSubjectCode());
				
				if(!isNoSubjectClashing){
					ArrayList<SessionDayTimeAcadsBean> clashingList = getClashingSubjectList(session);
					String msg = checkClashingOfSubjectCodes(clashingList, specializationTypeOfSessionToBeScheduled, session);
					
					if (!StringUtils.isBlank(msg)) {
						if (!session.getIsRecommendationSession().equalsIgnoreCase("ByPass") || session.getIsRecommendationSession().equalsIgnoreCase("N")){
							getRecommendationSession(session, request);
						}
						return sendToErrorPage(request, session, msg, "true");
					}
				}

			} else {
				boolean isNoSubjectClashing = dao.isNoSubjectClashingV3(session);
				String specializationTypeOfSessionToBeScheduled = dao.getspecializationTypeBySubjectCode(session.getSubjectCode());

				if(!isNoSubjectClashing){	
					ArrayList<SessionDayTimeAcadsBean> clashingList = getClashingSubjectList(session);
					String msg = checkClashingOfSubjectCodes(clashingList, specializationTypeOfSessionToBeScheduled, session);
					
					if (!StringUtils.isBlank(msg)) {
						if (!session.getIsRecommendationSession().equalsIgnoreCase("ByPass") || session.getIsRecommendationSession().equalsIgnoreCase("N")){
							getRecommendationSession(session, request);
						}
						return sendToErrorPage(request, session, msg, "true");
					}
				}
				
				//Commented by Somesh on 20/07/2021 as we are already Checking More than 3 session on same day by track
				/*
				boolean isNotMoreThan3CommonSubjectsSameDay = dao.isNotMoreThan3CommonSubjectsSameDayByCorporateName(session);
				if (!isNotMoreThan3CommonSubjectsSameDay) {
					return sendToErrorPage(request, session, "Already 3 sessions of common subjects going on " + session.getDate(), "true");
				}
				*/
			}
			
			//Get count of applicable cpsid for Session 
			int sessionCount = dao.getSessionApplicableCount(session.getId());
			if (sessionCount == 0) {
				return sendToErrorPage(request, session, "No Mapping found for this session.", "true");
			}
			
			int sizeOfCPSId = session.getConsumerProgramStructureId().split(",").length;
			String oldSessionId = session.getId();
			
			if (sizeOfCPSId >= 1 														
					// if noOfsessions for count not match then create new session
					&& sessionCount > 1 && sessionCount != sizeOfCPSId
					// MBA-Wx program applicable for only one program
					&& !session.getConsumerProgramStructureId().contains("111")	&& !session.getConsumerProgramStructureId().contains("151")	
					&& !session.getConsumerProgramStructureId().contains("160")	
					// M.Sc. (AI & ML Ops) || M.Sc. (AI) program applicable for only one program
					&& !session.getConsumerProgramStructureId().contains("131") && !session.getConsumerProgramStructureId().contains("158")
					// PC-DS || PD-DS
					&& !session.getConsumerProgramStructureId().contains("154") && !session.getConsumerProgramStructureId().contains("155")) {
				
				//Setting hostId blank so new HostId will get allocate 
				session.setHostId("");
				session = allocateAvailableRoomForBatchUpload(session);
				
				boolean isSessionInserted = dao.insertNewSessionAfterFacultyUpdate(session, userId, oldSessionId);
				if (!isSessionInserted) {
					return sendToErrorPage(request, session, "Error in saving session to database.", "true");
				}
			} else {
				boolean isUpdated = dao.updateScheduledSession(session);
				if (!isUpdated) {
					return sendToErrorPage(request, session, "Error in saving session to database.", "true");
				}
			}

			//Commented by Somesh on 04-06-2021 as Added transactional
			/*
			dao.updateTrack(session, userId, sessionDay);
			
			//Delete Sessions from quick session and Add updated session
			long sessionId = Long.parseLong(session.getId());
			boolean isDeleted = dao.deleteQuickSessions(sessionId);
			if (!isDeleted) {
				request.setAttribute("error", "true");
				request.setAttribute("successMessage", "Faculty ID Updated successfully. Error while updating in quick session");
				return searchScheduledSession(request, response, session);
			}
			
			ArrayList<SessionDayTimeBean> sessionWithPSSIds = dao.getAllPSSSessionsMapping(sessionId);
			boolean isInserted = dao.insertQuickSession(sessionWithPSSIds, sessionId);
			if (!isInserted) {
				request.setAttribute("error", "true");
				request.setAttribute("successMessage", "Session Updated successfully. Error while inserting in quick session");
				return searchScheduledSession(request, response, session);
			}
			*/
			
			// session.setTrack("");
			request.setAttribute("success", "true");
			request.setAttribute("successMessage", "Track Updated successfully");
			return searchScheduledSession(request, response, session);

		} catch (Exception e) {
			loggerForSessionScheduling.debug("Error in updateTrackDetails: "+e.getMessage());
			return sendToErrorPage(request, session, e.getMessage(), "true");
		}

	}

	@RequestMapping(value = "/updateFacultyId", method = { RequestMethod.GET, RequestMethod.POST })
	public ModelAndView updateFacultyId(HttpServletRequest request, HttpServletResponse response, @ModelAttribute SessionDayTimeAcadsBean session) {
		try {
			SessionDayTimeAcadsBean sessionBeforeEdit = (SessionDayTimeAcadsBean) request.getSession().getAttribute("sessionBeforeEdit");

			boolean dateTimeSubjectChanged = (!sessionBeforeEdit.getDate().equals(session.getDate()))
					|| (!sessionBeforeEdit.getStartTime().equals(session.getStartTime()))
					|| (!sessionBeforeEdit.getSubject().equals(session.getSubject()))
					|| (!sessionBeforeEdit.getSessionName().equals(session.getSessionName()));

			boolean dateTimeChanged = (!sessionBeforeEdit.getDate().equals(session.getDate()))
					|| (!sessionBeforeEdit.getStartTime().equals(session.getStartTime()));

			if (dateTimeSubjectChanged || dateTimeChanged) {
				return sendToErrorPage(request, session,
						"Date, Time or Subject is changed. Please use 'Update Details' button instead of 'Update Faculty'", "true");
			}
			
			loggerForSessionScheduling.info("In updateFacultyId dateTimeSubjectChanged: "+dateTimeSubjectChanged+" dateTimeChanged: "+dateTimeChanged);
			loggerForSessionScheduling.info("In updateFacultyId new FacultyID: "+session.getFacultyId()+ " Old FacultyId: "+sessionBeforeEdit.getFacultyId());
			
			boolean facultyChanged = !sessionBeforeEdit.getFacultyId().equals(session.getFacultyId());
			if (!facultyChanged) {
				return sendToErrorPage(request, session, "Faculty ID is not changed. Wrong button used!", "true");
			}

			TimeTableDAO dao = (TimeTableDAO) act.getBean("timeTableDAO");
			String userId = (String) request.getSession().getAttribute("userId_acads");
			session.setLastModifiedBy(userId);

			// Commented By Somesh --> Unused Check for update scheduled session on
			// 23/01/2019
			/*
			 * if(getSasSubjectsList().contains(session.getSubject())){ List<String>
			 * StudentSubjectListOfHavingSaSAndOtherProgramActive =
			 * getStudentSubjectListOfHavingSaSAndOtherProgramActive(dao.
			 * getLiveAcadSessionMonth(),dao.getLiveAcadSessionYear()); ArrayList<String>
			 * lstOfClashingSubjects =
			 * dao.getSessionScheduledOnSameDayTime(session.getDate(),session.getStartTime()
			 * ,StudentSubjectListOfHavingSaSAndOtherProgramActive);
			 * if(lstOfClashingSubjects.size() > 0 ){ return sendToErrorPage(request,
			 * session,"Session Time "+session.getStartTime() +
			 * " clashing with "+String.join(",", lstOfClashingSubjects)
			 * +" sessions of SAS students PG/Diploma/Certificate program on " +
			 * session.getDate(), "true") ; } }
			 * if(getStudentSubjectListOfHavingSaSAndOtherProgramActive(dao.
			 * getLiveAcadSessionMonth(),dao.getLiveAcadSessionYear()).contains(session.
			 * getSubject())){ List<String>
			 * StudentsSASSubjectListOfHavingSaSAndOtherProgramActive =
			 * getStudentsSASSubjectListOfHavingSaSAndOtherProgramActive(dao.
			 * getLiveAcadSessionMonth(),dao.getLiveAcadSessionYear()); ArrayList<String>
			 * lstOfClashingSubjects =
			 * dao.getSessionScheduledOnSameDayTime(session.getDate(),session.getStartTime()
			 * ,StudentsSASSubjectListOfHavingSaSAndOtherProgramActive);
			 * if(lstOfClashingSubjects.size() > 0 && lstOfClashingSubjects != null &&
			 * !lstOfClashingSubjects.isEmpty()){ return sendToErrorPage(request,
			 * session,"Session Time "+session.getStartTime() +
			 * " clashing with "+String.join(",", lstOfClashingSubjects)
			 * +" sessions of SAS students program on " + session.getDate(), "true") ; } }
			 */

			if (!getFacultyList().contains(session.getFacultyId())) {
				return sendToErrorPage(request, session, "Invalid Faculty ID:" + session.getFacultyId(), "true");
			}

			if (!"Y".equalsIgnoreCase(session.getByPassFaculty())) {
			boolean isFacultyAvailable = dao.isFacultyAvailable(session);
			if (!isFacultyAvailable) {
				if (!session.getIsRecommendationSession().equalsIgnoreCase("ByPass") || session.getIsRecommendationSession().equalsIgnoreCase("N")){
					getRecommendationSession(session, request);
				}
				return sendToErrorPage(request, session, "Faculty is NOT available on " + session.getDate()+" at "+session.getStartTime(), "true");
			}

			boolean isFacultyFree = dao.isFacultyFreeAllChecksV2(session);
			if (!isFacultyFree) {
				ArrayList<SessionDayTimeAcadsBean> facultySession = dao.getFacultyClashDeatils(session);
				String msg = "";
				for (SessionDayTimeAcadsBean bean : facultySession) {
					msg = msg + "Faculty is occupied taking "+bean.getSessionName()+" of Subject : "+bean.getSubject()+
							 	" of "+bean.getTrack()+" on " + bean.getDate() + " at " + bean.getStartTime()+ " /3<br>";
				}
				
				if (!session.getIsRecommendationSession().equalsIgnoreCase("ByPass") || session.getIsRecommendationSession().equalsIgnoreCase("N")){
					getRecommendationSession(session, request);
				}
				return sendToErrorPage(request, session,msg,"true");
			}

			boolean isFacultyTakingLessThan2SubjectsSameDay = dao.isFacultyTakingLessThan2SubjectsSameDay(session);
			if (!isFacultyTakingLessThan2SubjectsSameDay) {
				ArrayList<SessionDayTimeAcadsBean> facultySessions = dao.getSameDaySessionsForFaculty(session);
				String msg = "Faculty " + session.getFacultyId() + " is already taking 2 sessions on "+ session.getDate()+ "<br>";
				int count = 1;
				for (SessionDayTimeAcadsBean bean : facultySessions) {
					msg = msg + count +". Subject : "+bean.getSubject()+ " on "+bean.getDate()
							 +" at "+bean.getStartTime()+ " of Track : "+bean.getTrack()+ "<br>";
					count++;
				}				
				return sendToErrorPage(request, session,msg,"true");
			}
			}

			//Get count of applicable cpsid for Session 
			int sessionCount = dao.getSessionApplicableCount(session.getId());
			if (sessionCount == 0) {
				return sendToErrorPage(request, session, "No Mapping found for this session.", "true");
			}
			
			int sizeOfCPSId = session.getConsumerProgramStructureId().split(",").length;
			String oldSessionId = session.getId();
			
			if (sizeOfCPSId >= 1 														
					// if noOfsessions for count not match then create new session
					&& sessionCount > 1 && sessionCount != sizeOfCPSId
					// MBA-Wx program applicable for only one program
					&& !session.getConsumerProgramStructureId().contains("111")	&& !session.getConsumerProgramStructureId().contains("151")	
					&& !session.getConsumerProgramStructureId().contains("160")	
					// M.Sc. (AI & ML Ops) || M.Sc. (AI) program applicable for only one program
					&& !session.getConsumerProgramStructureId().contains("131") && !session.getConsumerProgramStructureId().contains("158")
					// PC-DS || PD-DS
					&& !session.getConsumerProgramStructureId().contains("154") && !session.getConsumerProgramStructureId().contains("155")) {
				
				//Setting hostId blank so new HostId will get allocate 
				session.setHostId("");
				session = allocateAvailableRoomForBatchUpload(session);
				
				boolean isSessionInserted = dao.insertNewSessionAfterFacultyUpdate(session, userId, oldSessionId);
				if (!isSessionInserted) {
					return sendToErrorPage(request, session, "Error in saving session to database.", "true");
				}
			} else {
				boolean isUpdated = dao.updateScheduledSession(session);
				if (!isUpdated) {
					return sendToErrorPage(request, session, "Error in saving session to database.", "true");
				}
			}
			
			//Commented by Somesh on 04-06-2021 as Added transactional
			/*
			//Delete Sessions from quick session and Add updated session
			long sessionId = Long.parseLong(session.getId());
			boolean isDeleted = dao.deleteQuickSessions(sessionId);
			if (!isDeleted) {
				request.setAttribute("error", "true");
				request.setAttribute("successMessage", "Faculty ID Updated successfully. Error while updating in quick session");
				return searchScheduledSession(request, response, session);
			}
			
			ArrayList<SessionDayTimeBean> sessionWithPSSIds = dao.getAllPSSSessionsMapping(sessionId);
			boolean isInserted = dao.insertQuickSession(sessionWithPSSIds, sessionId);
			if (!isInserted) {
				request.setAttribute("error", "true");
				request.setAttribute("successMessage", "Session Updated successfully. Error while inserting in quick session");
				return searchScheduledSession(request, response, session);
			}
			*/
			
			session.setSessionName("");
			request.setAttribute("success", "true");
			request.setAttribute("successMessage", "Faculty ID Updated successfully");
			return searchScheduledSession(request, response, session);

		} catch (Exception e) {
			loggerForSessionScheduling.debug("Error in updateFacultyId: "+e.getMessage());
			return sendToErrorPage(request, session, e.getMessage(), "true");
		}

	}

	@RequestMapping(value = "/addCommonSessionForm", method = { RequestMethod.GET, RequestMethod.POST })
	public ModelAndView addScheduledCommonSessionForm(HttpServletRequest request, HttpServletResponse response, Model m) {
		SessionDayTimeAcadsBean session = new SessionDayTimeAcadsBean();
		TimeTableDAO dao = (TimeTableDAO) act.getBean("timeTableDAO");
		m.addAttribute("programList", dao.getAllPrograms());
		m.addAttribute("session", session);
		m.addAttribute("yearList", ACAD_YEAR_LIST);
		m.addAttribute("semList", semList);
		m.addAttribute("locationList", getLocationList());
		m.addAttribute("sessionTypesMap", getSessionTypesMap());
		m.addAttribute("consumerTypeList", getConsumerTypesList());
		return new ModelAndView("addCommonSession");
	}

	/*
	 * @RequestMapping(value = "/addCommonSession", method = {RequestMethod.GET,
	 * RequestMethod.POST}) public ModelAndView addCommonSession(HttpServletRequest
	 * request, HttpServletResponse response, @ModelAttribute SessionDayTimeBean
	 * session){ try{ TimeTableDAO dao = (TimeTableDAO)act.getBean("timeTableDAO");
	 * String userId = (String)request.getSession().getAttribute("userId_acads");
	 * session.setLastModifiedBy(userId);
	 * 
	 * 
	 * 
	 * if(!getFacultyList().contains(session.getFacultyId())){ return
	 * sendToErrorPageForCommonSession(request, session,
	 * "Invalid Faculty ID:"+session.getFacultyId(), "false"); }
	 * 
	 * boolean isFacultyAvailable = dao.isFacultyAvailable(session);
	 * if(!isFacultyAvailable){ return sendToErrorPageForCommonSession(request,
	 * session, "Faculty is NOT available on "+session.getDate(), "false"); }
	 * 
	 * boolean isFacultyFree = dao.isFacultyFree(session);
	 * if(!isFacultyFree){
	 * return sendToErrorPageForCommonSession(request, session,
	 * "Faculty is occupied on "+session.getDate() + " at "+session.getStartTime(),
	 * "false"); }
	 * 
	 * if(!"Y".equalsIgnoreCase(session.getByPassChecks())){ //Rest of the checks
	 * can be bypassed if user wants it to. boolean isDateTimeValid =
	 * dao.isDateTimeValid(session);
	 * if(!isDateTimeValid){ return sendToErrorPageForCommonSession(request,
	 * session, "Date "+session.getDate() + " and Time " + session.getStartTime() +
	 * " is not valid as per agreed days and time slot OR not within Academic Calendar dates."
	 * , "false"); }
	 * 
	 * boolean isSlotAvailable = dao.isSlotAvailable(session);
	 * if(!isSlotAvailable){ return sendToErrorPageForCommonSession(request,
	 * session, "Already 6 sessions going on "+session.getDate() +
	 * " at "+session.getStartTime(), "false"); }
	 * 
	 * boolean isNotHoliday = dao.isNotHoliday(session);
	 * if(!isNotHoliday){
	 * return sendToErrorPageForCommonSession(request, session, session.getDate() +
	 * " is a Holiday ", "false"); }
	 * 
	 * boolean isNotMoreThan3CommonSubjectsSameDay =
	 * dao.isNotMoreThan3CommonSubjectsSameDay(session);
	 * if(!isNotMoreThan3CommonSubjectsSameDay){ return
	 * sendToErrorPageForCommonSession(request, session,
	 * "Already 3 sessions of common subjects going on "+session.getDate(),
	 * "false"); }
	 * 
	 * boolean isFacultyTakingLessThan2SubjectsSameDay =
	 * dao.isFacultyTakingLessThan2SubjectsSameDay(session);
	 * if(!isFacultyTakingLessThan2SubjectsSameDay){ return
	 * sendToErrorPageForCommonSession(request, session,
	 * "Faculty "+session.getFacultyId()+" is already taking 2 sessions on "+session
	 * .getDate(), "false"); }
	 * 
	 * boolean isNoSubjectClashing = dao.isNoSubjectClashing(session);
	 * if(!isNoSubjectClashing){ return sendToErrorPageForCommonSession(request,
	 * session, "Subject "+session.getSubject() +
	 * " clashing with other subjects of same sem on " + session.getDate()+
	 * " at "+session.getStartTime(), "false"); }
	 * 
	 * }
	 * 
	 * allocateAvailableRoom(session);
	 * 
	 * if(session.getErrorMessage() !=null &&
	 * !"".equals(session.getErrorMessage())){ return
	 * sendToErrorPageForCommonSession(request, session,session.getErrorMessage() ,
	 * "false"); }
	 * 
	 * dao.insertSingleCommonSession(session);
	 * 
	 * session.setSessionName(""); request.setAttribute("success","true");
	 * request.setAttribute("successMessage","Common Session created successfully");
	 * return searchScheduledSession(request, response, session);
	 * 
	 * 
	 * }catch(Exception e){    return
	 * sendToErrorPageForCommonSession(request, session, e.getMessage(), "false"); }
	 * 
	 * }
	 */
	
	// Changes added by steff for location and duplicate session creation
	@RequestMapping(value = "/addCommonSession", method = { RequestMethod.GET, RequestMethod.POST })
	public ModelAndView addCommonSession(HttpServletRequest request, HttpServletResponse response,
			@ModelAttribute SessionDayTimeAcadsBean session) {
		try {

			TimeTableDAO dao = (TimeTableDAO) act.getBean("timeTableDAO");
			String userId = (String) request.getSession().getAttribute("userId_acads");
			session.setCreatedBy(userId);
			session.setLastModifiedBy(userId);
			
			if (StringUtils.isBlank(session.getTrack())) {
				session.setTrack("");
			}
			
			String corporateName = dao.getCorporateName(session.getCorporateName());
			session.setCorporateName(corporateName);
			
			List<String> sem = convertStringtoList(session.getSem());
			Collections.sort(sem);

			session.setSem(sem.get(0));
			session.setIsCommon("Y");
			if (!getFacultyList().contains(session.getFacultyId())) {
				return sendToErrorPageForCommonSession(request, session, "Invalid Faculty ID:" + session.getFacultyId(), "false");
			}
			
			if (StringUtils.isBlank(session.getSubject()) || StringUtils.isBlank(session.getProgramList())) {
				return sendToErrorPageForCommonSession(request, session, "Subject or Program is Blank.", "false");
			}

			//Added Check to byPass All faculty Checks
			if (!"Y".equalsIgnoreCase(session.getByPassFaculty())) {
				boolean isFacultyAvailable = dao.isFacultyAvailable(session);
				if (!isFacultyAvailable) {
					if (!session.getIsRecommendationSession().equalsIgnoreCase("ByPass") || session.getIsRecommendationSession().equalsIgnoreCase("N")){
						getRecommendationSession(session, request);
					}
					return sendToErrorPageForCommonSession(request, session,
							"Faculty is NOT available on " + session.getDate()+" at "+session.getStartTime(), "false");
				}
	
				boolean isFacultyFree = dao.isFacultyFreeAllChecksV2(session);
				if (!isFacultyFree) {
					ArrayList<SessionDayTimeAcadsBean> facultySession = dao.getFacultyClashDeatils(session);
					String msg = "";
					for (SessionDayTimeAcadsBean bean : facultySession) {
						msg = msg + "Faculty is occupied taking "+bean.getSessionName()+" of Subject : "+bean.getSubject()+
								 	" of "+bean.getTrack()+" on " + bean.getDate() + " at " + bean.getStartTime()+ " /4<br>";
					}
					if (!session.getIsRecommendationSession().equalsIgnoreCase("ByPass") || session.getIsRecommendationSession().equalsIgnoreCase("N")){
						getRecommendationSession(session, request);
					}
					return sendToErrorPageForCommonSession(request, session,msg,"false");
				}
				
				boolean isFacultyTakingLessThan2SubjectsSameDay = dao.isFacultyTakingLessThan2SubjectsSameDay(session);
				if (!isFacultyTakingLessThan2SubjectsSameDay) {
					ArrayList<SessionDayTimeAcadsBean> facultySessions = dao.getSameDaySessionsForFaculty(session);
					String msg = "Faculty " + session.getFacultyId() + " is already taking 2 sessions on "+ session.getDate()+ "<br>";
					int count = 1;
					for (SessionDayTimeAcadsBean bean : facultySessions) {
						msg = msg + count +". Subject : "+bean.getSubject()+ " on "+bean.getDate() +" at "+bean.getStartTime()+ " of Track : "+bean.getTrack()+ "<br>";
						count++;
					}
					return sendToErrorPageForCommonSession(request, session, msg, "false");
				}
			}

			if (!"Y".equalsIgnoreCase(session.getByPassChecks())) {
				// Rest of the checks can be bypassed if user wants it to.
				boolean isDateTimeValid = dao.isDateTimeValid(session);
				if (!isDateTimeValid) {
					return sendToErrorPageForCommonSession(request, session, "Date " + session.getDate() + " and Time " + session.getStartTime()
							+ " is not valid as per agreed days and time slot OR not within Academic Calendar dates.", "false");
				}

				//Not in use
				/*
				boolean isSlotAvailable = dao.isSlotAvailable(session);
				if (!isSlotAvailable) {
					return sendToErrorPageForCommonSession(request, session,
							"Already 6 sessions going on " + session.getDate() + " at " + session.getStartTime(), "false");
				}
				*/

				boolean isNotHoliday = dao.isNotHoliday(session);
				if (!isNotHoliday) {
					return sendToErrorPageForCommonSession(request, session, session.getDate() + " is a Holiday ", "false");
				}

				boolean isNoSubjectClashing = dao.isNoSubjectClashingV3(session);
				String specializationTypeOfSessionToBeScheduled = dao.getspecializationTypeBySubjectCode(session.getSubjectCode());

				if(!isNoSubjectClashing){	
					ArrayList<SessionDayTimeAcadsBean> clashingList = getClashingSubjectList(session);
					String msg = checkClashingOfSubjectCodes(clashingList, specializationTypeOfSessionToBeScheduled, session);
					
					if (!StringUtils.isBlank(msg)) {
						if (!session.getIsRecommendationSession().equalsIgnoreCase("ByPass") || session.getIsRecommendationSession().equalsIgnoreCase("N")){
							getRecommendationSession(session, request);
						}
						return sendToErrorPageForCommonSession(request, session, msg, "false");
					}
				}
				
				//Commented by Somesh on 20/07/2021 as we are already Checking More than 3 session on same day by track
				/*
				boolean isNotMoreThan3CommonSubjectsSameDay = dao.isNotMoreThan3CommonSubjectsSameDayByCorporateName(session);
				if (!isNotMoreThan3CommonSubjectsSameDay) {
					return sendToErrorPageForCommonSession(request, session, "Already 3 sessions of common subjects going on " + session.getDate(), "false");
				}
				*/
			}

			session = allocateAvailableRoomForBatchUpload(session);

			if (session.getErrorMessage() != null && !"".equals(session.getErrorMessage())) {
				return sendToErrorPageForCommonSession(request, session, session.getErrorMessage(), "false");
			}

			// Using insertDuplicateSession() as it contains all the necessary fields than
			// normal inssertSsession()

			boolean sessionAdded = dao.insertCommonSession(session);
			if (!sessionAdded) {
				return sendToErrorPageForCommonSession(request, session,
						session.getErrorMessage() + ". Error while saving data to database", "false");
			}

			SessionDayTimeAcadsBean mainSession = dao.scheduledSessionById(session.getDate(), session.getStartTime(), session.getSem());

			// Old Cisco logic to convert DateTime Format which is no longer reqired for
			// zoom
			/*
			 * SimpleDateFormat simpleDateFormat1 = new SimpleDateFormat("MM/dd/yyyy");
			 * SimpleDateFormat simpleDateFormat2 = new SimpleDateFormat("yyyy-MM-dd");
			 * 
			 * mainSession.setDate(simpleDateFormat1.format(date)); String startTime =
			 * mainSession.getStartTime()+":00"; mainSession.setStartTime(startTime);
			 */

			// webExManager.scheduleTrainingSession(mainSession);
			if (ENVIRONMENT.equalsIgnoreCase("PROD")) {
				zoomManger.scheduleSessions(mainSession);
			}

			/*
			 * mainSession.setDate(simpleDateFormat2.format(date));
			 * mainSession.setStartTime(startTime.substring(0, 5));
			 */
			conferenceBookingDAO.updateBookCommonSession(mainSession);

			if (session.isErrorRecord()) {
				return sendToErrorPageForCommonSession(request, session, session.getErrorMessage() + ". Error while creating zoom meeting", "false");
			}

			SessionDayTimeAcadsBean mainUpdatedSession = dao.scheduledSessionById(mainSession.getDate(),mainSession.getStartTime(),mainSession.getSem());

			if (sem.size() > 1) {
				for (int i = 1; i < sem.size(); i++) {
					mainUpdatedSession.setSem(sem.get(i));
					boolean created = dao.insertCommonSession(mainUpdatedSession);
					if (!created) {
						return sendToErrorPageForCommonSession(request, session, "Error in saving common session for sem "+sem.get(i)+" .", "false");
					}
				}
			}

			session.setSessionName("");
			request.setAttribute("success", "true");
			request.setAttribute("successMessage", "Common Session created successfully");
			return searchCommonSession(request, session);

		} catch (Exception e) {
			e.printStackTrace();
			return sendToErrorPageForCommonSession(request, session, e.getMessage(), "false");
		}

	}

	@RequestMapping(value = "/addScheduledSessionForm", method = { RequestMethod.GET, RequestMethod.POST })
	public ModelAndView addScheduledSessionForm(HttpServletRequest request, HttpServletResponse response, Model m) {
		SessionDayTimeAcadsBean session = new SessionDayTimeAcadsBean();
		String moduleId = "";
		String year = "";
		String month = "";
		String corporateName = "";
		String subject = "";
		String facultyId = "";
		String sessionName = "";
		String dateTime = "";
		String sessionType = "";

		try {
			// moduleId = (String)request.getSession().getAttribute("id");
			moduleId = (String) request.getParameter("id");
			year = (String) request.getParameter("year");
			month = (String) request.getParameter("month");
			corporateName = (String) request.getParameter("corporateName");
			subject = (String) request.getParameter("subject");
			facultyId = (String) request.getParameter("facultyId");
			sessionName = (String) request.getParameter("sessionName");
			dateTime = (String) request.getParameter("dateTime");
			sessionType = (String) request.getParameter("sessionType");

		} catch (Exception e) {
		}
		m.addAttribute("session", session);
		m.addAttribute("yearList", ACAD_YEAR_LIST);
		m.addAttribute("subjectList", getSubjectList());
		m.addAttribute("locationList", getLocationList());
		m.addAttribute("trackList", trackList);
		m.addAttribute("moduleId", moduleId);
		m.addAttribute("masterKeys", getMasterKeys());
		//m.addAttribute("subjectCodeList", getSubjectCodeList());
		m.addAttribute("subjectCodeMap", getsubjectCodeMap());
		m.addAttribute("sessionTypesMap", getSessionTypesMap());
		m.addAttribute("masterKeysWithSubjectCodes", getMasterKeysWithSubjectCodes());
		request.getSession().setAttribute("moduleId", moduleId);

		if (year != null) {
			session.setYear(year);
		}
		if (month != null) {
			session.setMonth(month);
		}
		if (corporateName != null) {
			session.setCorporateName(corporateName);
		}
		if (subject != null) {
			session.setSubject(subject);
		}

		if (facultyId != null) {
			session.setFacultyId(facultyId);
			session.setFacultyLocation("Mumbai");
		}

		if (sessionName != null) {
			session.setSessionName(sessionName);
		}

		String[] splitedDateTime;
		String date = "";
		String time = "";
		try {

			splitedDateTime = dateTime.split("\\s+");
			date = splitedDateTime[0];
			time = splitedDateTime[1];

		} catch (Exception e) {
			  
		}

		if (!StringUtils.isBlank(date) && !StringUtils.isBlank(time)) {
			session.setDate(date);
			session.setStartTime(time);
		}
		
		if (sessionType != null) {
			session.setSessionType(sessionType);
		}

		return new ModelAndView("addScheduledSession");
	}

	/*
	 * @RequestMapping(value = "/addScheduledSession", method = {RequestMethod.GET,
	 * RequestMethod.POST}) public ModelAndView
	 * addScheduledSession(HttpServletRequest request, HttpServletResponse
	 * response, @ModelAttribute SessionDayTimeBean session){ try{ TimeTableDAO dao
	 * = (TimeTableDAO)act.getBean("timeTableDAO"); String userId =
	 * (String)request.getSession().getAttribute("userId_acads");
	 * session.setLastModifiedBy(userId);
	 * 
	 * if(!getFacultyList().contains(session.getFacultyId())){ return
	 * sendToErrorPage(request, session,
	 * "Invalid Faculty ID:"+session.getFacultyId(), "false"); }
	 * 
	 * boolean isFacultyAvailable = dao.isFacultyAvailable(session);
	 * if(!isFacultyAvailable){ return sendToErrorPage(request, session,
	 * "Faculty is NOT available on "+session.getDate(), "false"); }
	 * 
	 * boolean isFacultyFree = dao.isFacultyFree(session);
	 * if(!isFacultyFree){
	 * return sendToErrorPage(request, session,
	 * "Faculty is occupied on "+session.getDate() + " at "+session.getStartTime(),
	 * "false"); }
	 * 
	 * if(!"Y".equalsIgnoreCase(session.getByPassChecks())){
	 * 
	 * //Rest of the checks can be bypassed if user wants it to.
	 * 
	 * boolean isDateTimeValid = dao.isDateTimeValid(session);
	 * if(!isDateTimeValid){ return sendToErrorPage(request, session,
	 * "Date "+session.getDate() + " and Time " + session.getStartTime() +
	 * " is not valid as per agreed days and time slot OR not within Academic Calendar dates."
	 * , "false"); }
	 * 
	 * boolean isSlotAvailable = dao.isSlotAvailable(session);
	 * if(!isSlotAvailable){ return sendToErrorPage(request, session,
	 * "Already 6 sessions going on "+session.getDate() +
	 * " at "+session.getStartTime(), "false"); }
	 * 
	 * boolean isNotHoliday = dao.isNotHoliday(session);
	 * if(!isNotHoliday){
	 * return sendToErrorPage(request, session, session.getDate() +
	 * " is a Holiday ", "false"); }
	 * 
	 * boolean isNotMoreThan3CommonSubjectsSameDay =
	 * dao.isNotMoreThan3CommonSubjectsSameDay(session);
	 * if(!isNotMoreThan3CommonSubjectsSameDay){ return sendToErrorPage(request,
	 * session, "Already 3 sessions of common subjects going on "+session.getDate(),
	 * "false"); }
	 * 
	 * boolean isFacultyTakingLessThan2SubjectsSameDay =
	 * dao.isFacultyTakingLessThan2SubjectsSameDay(session);
	 * if(!isFacultyTakingLessThan2SubjectsSameDay){ return sendToErrorPage(request,
	 * session,
	 * "Faculty "+session.getFacultyId()+" is already taking 2 sessions on "+session
	 * .getDate(), "false"); }
	 * 
	 * boolean isNoSubjectClashing = dao.isNoSubjectClashing(session);
	 * if(!isNoSubjectClashing){ return sendToErrorPage(request, session,
	 * "Subject "+session.getSubject() +
	 * " clashing with other subjects of same sem on " + session.getDate()+
	 * " at "+session.getStartTime(), "false"); }
	 * 
	 * if(getSasSubjectsList().contains(session.getSubject())){ List<String>
	 * StudentSubjectListOfHavingSaSAndOtherProgramActive =
	 * getStudentSubjectListOfHavingSaSAndOtherProgramActive(dao.
	 * getLiveAcadSessionMonth(),dao.getLiveAcadSessionYear()); ArrayList<String>
	 * lstOfClashingSubjects =
	 * dao.getSessionScheduledOnSameDayTime(session.getDate(),session.getStartTime()
	 * ,StudentSubjectListOfHavingSaSAndOtherProgramActive);
	 * if(lstOfClashingSubjects !=null && !lstOfClashingSubjects.isEmpty() &&
	 * lstOfClashingSubjects.size() > 0 ){ return sendToErrorPage(request,
	 * session,"Session Time "+session.getStartTime() +
	 * " clashing with "+String.join(",", lstOfClashingSubjects)
	 * +" sessions of SAS students PG/Diploma/Certificate program on " +
	 * session.getDate(), "true") ; } }
	 * if(getStudentSubjectListOfHavingSaSAndOtherProgramActive(dao.
	 * getLiveAcadSessionMonth(),dao.getLiveAcadSessionYear()).contains(session.
	 * getSubject())){ List<String>
	 * StudentsSASSubjectListOfHavingSaSAndOtherProgramActive =
	 * getStudentsSASSubjectListOfHavingSaSAndOtherProgramActive(dao.
	 * getLiveAcadSessionMonth(),dao.getLiveAcadSessionYear()); ArrayList<String>
	 * lstOfClashingSubjects =
	 * dao.getSessionScheduledOnSameDayTime(session.getDate(),session.getStartTime()
	 * ,StudentsSASSubjectListOfHavingSaSAndOtherProgramActive);
	 * if(lstOfClashingSubjects.size() > 0 && lstOfClashingSubjects != null &&
	 * !lstOfClashingSubjects.isEmpty()){ return sendToErrorPage(request,
	 * session,"Session Time "+session.getStartTime() +
	 * " clashing with "+String.join(",", lstOfClashingSubjects)
	 * +" sessions of SAS students program on " + session.getDate(), "true") ; } }
	 * 
	 * }
	 * 
	 * allocateAvailableRoom(session);
	 * 
	 * if(session.getErrorMessage() !=null &&
	 * !"".equals(session.getErrorMessage())){ return sendToErrorPage(request,
	 * session,session.getErrorMessage(), "false"); }
	 * 
	 * dao.insertSingleSession(session);
	 * 
	 * session.setSessionName(""); request.setAttribute("success","true");
	 * request.setAttribute("successMessage","Session created successfully"); return
	 * searchScheduledSession(request, response, session);
	 * 
	 * 
	 * }catch(Exception e){    return sendToErrorPage(request,
	 * session, e.getMessage(), "false"); }
	 * 
	 * }
	 */

	// add scheduled session with event : START

	@RequestMapping(value = "/addScheduledSession", method = { RequestMethod.GET, RequestMethod.POST })
	public ModelAndView addScheduledSession(HttpServletRequest request, HttpServletResponse response, @ModelAttribute SessionDayTimeAcadsBean session) {
		
//		if(checkSession(request, response)) {
//			return new ModelAndView("studentPortalRediret");
//		}
		
		loggerForSessionScheduling.info("addScheduledSession Start");
		
		TimeTableDAO dao = (TimeTableDAO) act.getBean("timeTableDAO");
		String userId = (String) request.getSession().getAttribute("userId_acads");
		String isRecommendationSession = request.getParameter("isRecSession") != null ? "Y" : "N";
		String isBatchRecommendationSession = request.getParameter("isBatchRecSession") != null ? "Y" : "N";
		String index = request.getParameter("index") != null ? request.getParameter("index") : "" ;
		
		//For Session Plan Scheduling
		if (!StringUtils.isBlank(session.getModuleId())) {
			String subjectCode = dao.getSubjectCodeBySessionPlanId(session.getModuleSessionPlanId());
			if (StringUtils.isBlank(subjectCode)) {
				return sendToErrorPage(request, session, "Error in getting Subject Code for :" +session.getSubject()+ " Subject.", "false");
			}
			session.setSubjectCode(subjectCode);
			session.setSubject(session.getSessionplanSubject());
		}
		
		if (isRecommendationSession.equalsIgnoreCase("Y")) {
			ArrayList<SessionDayTimeAcadsBean> availableSolts = (ArrayList<SessionDayTimeAcadsBean>) request.getSession().getAttribute("availableSolts");
			SessionDayTimeAcadsBean sessionBean = availableSolts.get(Integer.parseInt(index)-1);
			session.setDate(sessionBean.getDate());
			session.setStartTime(sessionBean.getStartTime());
		}
		
		if (isBatchRecommendationSession.equalsIgnoreCase("Y")) {
			ArrayList<SessionDayTimeAcadsBean> availableSolts = (ArrayList<SessionDayTimeAcadsBean>) request.getSession().getAttribute("availableSolts");
			SessionDayTimeAcadsBean sessionBean = availableSolts.get(Integer.parseInt(index));
			sessionBean.setBatchUpload("Y");
			session = sessionBean;
		}
		
		//Checks for direct session creation start
		
		if (StringUtils.isBlank(session.getMasterKey()) && StringUtils.isBlank(session.getSubjectCode())) {
			return sendToErrorPage(request, session, "Subject Code and Program is blank. Please select any one of them", "false");
		}
		
		if (!StringUtils.isBlank(session.getMasterKey()) && !StringUtils.isBlank(session.getSubjectCode())) {
			return sendToErrorPage(request, session, "Subject Code and Program both are selected. Please select any one of them", "false");
		}
		
		if(!StringUtils.isBlank(session.getMasterKey()) && StringUtils.isBlank(session.getSubjectCode())){
			String subject = dao.getSubjectNameBySubCodeMappingId(session.getMasterKey());
			if (StringUtils.isBlank(subject)) {
				return sendToErrorPage(request, session, "More than one subject selected, Please varify selected subject", "false");
			}else {
				session.setSubject(dao.getSubjectNameBySubCodeMappingId(session.getMasterKey()));
			}
		} 
		
		if(!StringUtils.isBlank(session.getSubjectCode()) && StringUtils.isBlank(session.getMasterKey())) {
			String subject = dao.getSubjectNameBySubjectCode(session.getSubjectCode());
			if (StringUtils.isBlank(subject)) {
				return sendToErrorPage(request, session, "Error in getting Subject Name for :" +session.getSubjectCode()+ " Subject Code.", "false");
			}else {
				session.setSubject(dao.getSubjectNameBySubjectCode(session.getSubjectCode()));
			}
		}
		
		//Checks for direct session creation end
		
		String[] splitedDateTime;
		int sessionPlanId = session.getModuleSessionPlanId();
		
		if ("Oct".equalsIgnoreCase(session.getMonth())) {
			session.setMonth("Jul");
		}
		
		if ("Apr".equalsIgnoreCase(session.getMonth())) {
			session.setMonth("Jan");
		}

		String date = "";
		String time = "";
		try {
			splitedDateTime = session.getDateTime().split("\\s+");
			date = splitedDateTime[0];
			time = splitedDateTime[1];
		} catch (Exception e) {
//			  
		}

		if (!StringUtils.isBlank(date) && !StringUtils.isBlank(time)) {
			session.setDate(date);
			session.setStartTime(time);
		}

		if (session.getTimebondFacultyId() != null) {
			session.setFacultyId(session.getTimebondFacultyId());
			session.setFacultyLocation("Remote");
		}

		if (session.getSessionplanYear() != null) {
			session.setYear(session.getSessionplanYear());
		}

		if (session.getSessionplanMonth() != null) {
			session.setMonth(session.getSessionplanMonth());
		}

		if (session.getSessionplanCreatedBy() != null) {
			session.setCreatedBy(session.getSessionplanCreatedBy());
		}

		if (session.getSessionplanLastModifiedBy() != null) {
			session.setLastModifiedBy(session.getLastModifiedBy());
		}

		if (session.getSessionplanSubject() != null) {
			session.setSubject(session.getSessionplanSubject());
		}
		try {
			String moduleId = "";
			if (session.getModuleId() != null) {
				moduleId = session.getModuleId();
			} else {
				moduleId = (String) request.getSession().getAttribute("moduleId");
			}
			
			String sessionValidityErrorMessage = "";
			session.setLastModifiedBy(userId);
			session.setCreatedBy(userId);

			if (!StringUtils.isBlank(moduleId)) {
				session.setHasModuleId("Y");
				session.setStudentType("TimeBound");
				session.setSessionModuleNo(moduleId);
				session.setIsRecommendationSession("ByPass");
			} else {
				session.setHasModuleId("N");
				session.setStudentType("");
			}
			
			if (!StringUtils.isBlank(moduleId) && StringUtils.isBlank(session.getCorporateName())) {
				session.setCorporateName("Retail");
			}
			
			String returnMessage = validityCheck_SetWebex_InsertToDb(session, request);
			loggerForSessionScheduling.info("In addScheduledSession returnMessage : "+returnMessage);
			if (returnMessage == null) {
				if (session.getErrorMessage() != null && !"".equalsIgnoreCase(session.getErrorMessage())) {
					sessionValidityErrorMessage = sessionValidityErrorMessage + " " + session.getErrorMessage();
				}
			} else {
				sessionValidityErrorMessage = sessionValidityErrorMessage + " " + returnMessage;
				session.setErrorMessage(returnMessage);
				return sendToErrorPage(request, session, sessionValidityErrorMessage, "false");
			}

			// dao.insertSingleSession(session);
			String idForSession = dao.getSessionId(session);
			session.setId(idForSession);

			if (session.getStartDate() == null || session.getEndDate() == null) {
				dao.insertEventDetails(session);
			}
			request.setAttribute("success", "true");
			request.setAttribute("successMessage", "Session created successfully");
			loggerForSessionScheduling.info("addScheduledSession Ended");
			return searchScheduledSession(request, response, session);

		} catch (Exception e) {
			loggerForSessionScheduling.info("In addScheduledSession Error : "+e.getMessage());
			return sendToErrorPage(request, session, e.getMessage(), "false");
		}

	}

	// add scheduled session with event :END

	// addDuplicateSession Start
	@RequestMapping(value = "/addDuplicateSession", method = RequestMethod.POST)
	public ModelAndView addDuplicateSession(HttpServletRequest request, HttpServletResponse response, @ModelAttribute SessionDayTimeAcadsBean mainSession) {
		try {
			TimeTableDAO dao = (TimeTableDAO) act.getBean("timeTableDAO");
			String userId = (String) request.getSession().getAttribute("userId_acads");
			SessionDayTimeAcadsBean session = new SessionDayTimeAcadsBean();
			session = dao.findScheduledSessionById(mainSession.getId());
			
			//Checks for Subject selection start
			
			if (StringUtils.isBlank(mainSession.getMasterKey()) && StringUtils.isBlank(mainSession.getSubjectCode())) {
				return sendToErrorPage(request, session, "Subject Code and Program is blank. Please select any one of them", "false");
			}
			
			if (!StringUtils.isBlank(mainSession.getMasterKey()) && !StringUtils.isBlank(mainSession.getSubjectCode())) {
				return sendToErrorPage(request, session, "Subject Code and Program both are selected. Please select any one of them", "false");
			}
			
			if(!StringUtils.isBlank(mainSession.getMasterKey()) && StringUtils.isBlank(mainSession.getSubjectCode())){
				String subject = dao.getSubjectNameBySubCodeMappingId(mainSession.getMasterKey());
				if (StringUtils.isBlank(subject)) {
					return sendToErrorPage(request, session, "More than one subject selected, Please varify selected subject", "false");
				}else {
					session.setSubject(dao.getSubjectNameBySubCodeMappingId(mainSession.getMasterKey()));
				}
			} 
			
			if(!StringUtils.isBlank(mainSession.getSubjectCode()) && StringUtils.isBlank(mainSession.getMasterKey())) {
				String subject = dao.getSubjectNameBySubjectCode(mainSession.getSubjectCode());
				if (StringUtils.isBlank(subject)) {
					return sendToErrorPage(request, session, "Error in getting Subject Name for :" +mainSession.getSubjectCode()+ " Subject Code.", "false");
				}else {
					session.setSubject(dao.getSubjectNameBySubjectCode(mainSession.getSubjectCode()));
				}
			}
			
			//Checks for Subject selection end

			// check if webex is already created created start
			if (!"B".equalsIgnoreCase(session.getCiscoStatus()) || StringUtils.isBlank(session.getMeetingKey())) {
				return sendToErrorPage(request, session,
						"Please create zoom of Session before duplicating the session.", "false");
			}
			// check if webex is already created created end

			//session.setSubject(mainSession.getSubject());
			session.setMasterKey(mainSession.getMasterKey());
			session.setSubjectCode(mainSession.getSubjectCode());
			session.setSessionName(mainSession.getSessionName());
			session.setCorporateName(mainSession.getCorporateName());
			session.setTrack(mainSession.getTrack());
			session.setLastModifiedBy(userId);
			session.setMasterKey(mainSession.getMasterKey());

			boolean isSessionPresent = dao.findScheduledSessionByDateTimeSubject(session);
			if (isSessionPresent) {
				return sendToErrorPage(request, session, "Session Already Present With Details <br>  Session Date :" + session.getDate()
								+ " <br>  Session Starttime :" + session.getStartTime() + " <br>  Session Date :"
								+ session.getSubject() + " <br> Track : " + session.getTrack(), "false");
			}

			boolean created = dao.insertDuplicateSession(session);

			if (created) {
				String sessionId = dao.getSessionIdByDateTimeSubjectTrack(session);
				String id = sessionId != null ? sessionId : "";
				session.setId(id);
				session.setSessionName("");
				request.setAttribute("success", "true");
				request.setAttribute("successMessage", "Duplicate Session created successfully");
				return searchScheduledSession(request, response, session);
			} else {
				return sendToErrorPage(request, session, "Error in saving session to database.", "false");

			}

		} catch (Exception e) {
			loggerForSessionScheduling.info("Error in addDuplicateSession : "+e.getMessage());
			return sendToErrorPage(request, mainSession, e.getMessage(), "false");
		}

	}
	// addDuplicateSession End

	// Batch session scheduling start
	@RequestMapping(value = "/batchSessionSchedulingForm", method = RequestMethod.GET)
	public String batchSessionSchedulingForm(HttpServletRequest request, HttpServletResponse response, Model m) {
		if (!checkSession(request, response)) {
			return "studentPortalRediret";
		}
		FileAcadsBean fileBean = new FileAcadsBean();
		SessionDayTimeAcadsBean session = new SessionDayTimeAcadsBean();
		m.addAttribute("fileBean", fileBean);
		m.addAttribute("session", session);
		m.addAttribute("yearList", ACAD_YEAR_LIST);

		return "batchSessionSchedulingForm";
	}
	
	@RequestMapping(value = "/batchSessionScheduling", method = RequestMethod.POST)
	public ModelAndView batchSessionScheduling(FileAcadsBean fileBean, BindingResult result, HttpServletRequest request,
			Model m, HttpServletResponse response) {
		
		if (!checkSession(request, response)) { 
				return new ModelAndView("studentPortalRediret"); 
		} 
		ModelAndView modelnView = new ModelAndView("batchSessionSchedulingForm");
		loggerForSessionScheduling.info("batchSessionScheduling Start");
		try {
			String userId = (String) request.getSession().getAttribute("userId_acads");
			ExcelHelper excelHelper = new ExcelHelper();

			TimeTableDAO dao = (TimeTableDAO) act.getBean("timeTableDAO");
			List<String> locationList = dao.getListOfLocations();
			ArrayList<List> resultList = excelHelper.readBatchSessionScheduleExcel(fileBean, getFacultyList(),
					getSubjectCodeList(), userId, getLocationList(), getSessionTypeList(),getsubjectCodeMap());

			List<SessionDayTimeAcadsBean> corporateSessionList = (ArrayList<SessionDayTimeAcadsBean>) resultList.get(0);
			List<SessionDayTimeAcadsBean> errorBeanList = (ArrayList<SessionDayTimeAcadsBean>) resultList.get(1);

			loggerForSessionScheduling.info("corporateSessionList size : "+corporateSessionList.size());
			loggerForSessionScheduling.info("errorBeanList size : "+errorBeanList.size());
			
			fileBean = new FileAcadsBean();
			m.addAttribute("fileBean", fileBean);
			m.addAttribute("yearList", ACAD_YEAR_LIST); 

			if (errorBeanList.size() > 0) {
				request.setAttribute("errorBeanList", errorBeanList);
				return modelnView;
			}

			// Check session validity start
			String sessionValidityErrorMessage = "";
			List<SessionDayTimeAcadsBean> validSessions = new ArrayList<SessionDayTimeAcadsBean>();
			List<SessionDayTimeAcadsBean> inValidSessions = new ArrayList<SessionDayTimeAcadsBean>();
			String returnMessage = null;
			int row = 2;
			for (SessionDayTimeAcadsBean sessionToCheck : corporateSessionList) {
				sessionToCheck.setIsRecommendationSession("ByPass");
				sessionToCheck.setBatchUpload("Y");
				returnMessage = validityCheck_SetWebex_InsertToDb(sessionToCheck, request);
				loggerForSessionScheduling.info("returnMessage "+returnMessage+ " For row "+row);
				if (returnMessage == null) {
					validSessions.add(sessionToCheck);
					if (sessionToCheck.getErrorMessage() != null && !"".equalsIgnoreCase(sessionToCheck.getErrorMessage())) {
						sessionValidityErrorMessage = sessionValidityErrorMessage + "<br> Row " + row + " : " 
								+ sessionToCheck.getErrorMessage() + " :: "+ showRecommendation(sessionToCheck, row)+ "<br>";
						inValidSessions.add(sessionToCheck);
					}
				} else {
					sessionValidityErrorMessage = sessionValidityErrorMessage + "<br> Row " + row + " : "
							+ returnMessage  + " : "+ showRecommendation(sessionToCheck, row)+ "<br>";
					sessionToCheck.setErrorMessage(returnMessage);
					inValidSessions.add(sessionToCheck);
				}
				row++;
			}
			// Check session validity end

			if (inValidSessions.size() == 0) {
				request.setAttribute("success", "true");
				request.setAttribute("successMessage", corporateSessionList.size() + " rows out of "
						+ corporateSessionList.size() + " inserted successfully.");
				loggerForSessionScheduling.info(corporateSessionList.size() + " rows out of "
						+ corporateSessionList.size() + " inserted successfully.");
			} else {
				request.getSession().setAttribute("inValidSessions", inValidSessions);
				request.setAttribute("error", "true");
				request.setAttribute("errorMessage", inValidSessions.size() + " records were NOT inserted. <br>"
						+ " Please download the excel file to see the invalid session which were not scheduled \t"
						+ " <a href=\"/acads/downloadSessionsWithErrorsWhileBatchUpload\" "
						+ " target=\"_blank\"><b> Download </b> </a> \t <br>"
						+ " (Note: Delete ErrorMessage Column and upload the same file after correction)" + " <br><br> "
						+ " Please Click here to download Batch Upload RecommendationSession \t"
						+ " <a href=\"/acads/admin/getBatchUploadRecommendationSession\" "
						+ " target=\"_blank\"><b> Download </b> </a> \t <br>"
//						+ showRecommendation()
						+ sessionValidityErrorMessage);
				loggerForSessionScheduling.info(inValidSessions.size() + " records were NOT inserted. <br>"+sessionValidityErrorMessage);
			}

		} catch (Exception e) {
			loggerForSessionScheduling.info("Error in batchSessionScheduling : "+e.getMessage());
			request.setAttribute("error", "true");
			request.setAttribute("errorMessage", "Error in batch upload. Error: " + e.getMessage());

		}

		SessionDayTimeAcadsBean session = new SessionDayTimeAcadsBean();
		modelnView.addObject("fileBean", fileBean);
		modelnView.addObject("session", session);
		modelnView.addObject("yearList", ACAD_YEAR_LIST);
		loggerForSessionScheduling.info("batchSessionScheduling End");
		
		return modelnView;
	}

	// Generates an excel sheet with invalid sessions along with error
	@RequestMapping(value = "downloadSessionsWithErrorsWhileBatchUpload", method = RequestMethod.GET)
	public ModelAndView downloadSessionsWithErrorsWhileBatchUpload(HttpServletRequest request, HttpServletResponse response) {
		if (!checkSession(request, response)) {
			return new ModelAndView("studentPortalRediret");
		}
		List<SessionDayTimeAcadsBean> inValidSessions = (List<SessionDayTimeAcadsBean>) request.getSession() .getAttribute("inValidSessions");

		return new ModelAndView("sessionsWithErrorsExcelView", "inValidSessions", inValidSessions);
	}

	public String validityCheck_SetWebex_InsertToDb(SessionDayTimeAcadsBean session, HttpServletRequest request) {
		try {
			
			TimeTableDAO dao = (TimeTableDAO) act.getBean("timeTableDAO");
			loggerForSessionScheduling.info("Inside validityCheck_SetWebex_InsertToDb");
			ArrayList<String> tempFacultyList = getFacultyList();
			SessionDayTimeAcadsBean tempSession = new SessionDayTimeAcadsBean();
			tempSession.setDate(session.getDate());
			tempSession.setStartTime(session.getStartTime());
			tempSession.setYear(session.getYear());
			tempSession.setMonth(session.getMonth());
			
			//Set Subject in Session
			if (StringUtils.isBlank(session.getSubject())) {
				String subject = dao.getSubjectNameBySubjectCode(session.getSubjectCode());
				if (!StringUtils.isBlank(subject)) {
					session.setSubject(subject);
				}else {
					return "Error in getting Subject Name for :" + session.getSubjectCode() + " Subject Code.";
				}
			}

			// Checks for faculty --> Start
			if (!StringUtils.isBlank(session.getFacultyId())) {
				if (!tempFacultyList.contains(session.getFacultyId())) {
					return "Invalid Faculty ID:" + session.getFacultyId();
				}
				
				//Added Check to byPass All faculty Checks
				if (!"Y".equalsIgnoreCase(session.getByPassFaculty())) {
					boolean isFacultyAvailable = dao.isFacultyAvailable(session);
					if (!isFacultyAvailable) {
						if (!session.getIsRecommendationSession().equalsIgnoreCase("ByPass") || session.getIsRecommendationSession().equalsIgnoreCase("N")){
							getRecommendationSession(session, request);
						}
						return "Faculty " + session.getFacultyId() + " is NOT available on " + session.getDate()+" at "+session.getStartTime();
					}
	
					boolean isFacultyFree = dao.isFacultyFreeAllChecksV2(session);
					if (!isFacultyFree) {
						ArrayList<SessionDayTimeAcadsBean> facultySession = dao.getFacultyClashDeatils(session);
						String msg = "";
						for (SessionDayTimeAcadsBean bean : facultySession) {
							msg = msg + "Faculty is occupied taking "+bean.getSessionName()+" of Subject : "+bean.getSubject()+
									 	" of "+bean.getTrack()+" on " + bean.getDate() + " at " + bean.getStartTime()+ " /5<br>";
						}
						if (!session.getIsRecommendationSession().equalsIgnoreCase("ByPass") || session.getIsRecommendationSession().equalsIgnoreCase("N")){
							getRecommendationSession(session, request);
						}
						return msg;
					}
	
					boolean isFacultyTakingLessThan2SubjectsSameDay = dao.isFacultyTakingLessThan2SubjectsSameDay(session);
					if (!isFacultyTakingLessThan2SubjectsSameDay) {
						ArrayList<SessionDayTimeAcadsBean> facultySessions = dao.getSameDaySessionsForFaculty(session);
						String msg = "Faculty " + session.getFacultyId() + " is already taking 2 sessions on "+ session.getDate()+ "<br>";
						int count = 1;
						for (SessionDayTimeAcadsBean bean : facultySessions) {
							msg = msg + count +". Subject : "+bean.getSubject()+ " on "+bean.getDate()
									 +" at "+bean.getStartTime()+ " of Track : "+bean.getTrack()+ "<br>";
							count++;
						}
						return msg;
					}
					//Commented By Somesh as Check is not Required for Now
					/*
					ArrayList<SessionDayTimeBean> earlyOrLastSessionList = dao.isFacultyNotTakingEarlyOrLastSession(session);
					if (earlyOrLastSessionList.size() > 0) {
						String msg = "";
						for (SessionDayTimeBean bean : earlyOrLastSessionList) {
							msg = msg + "Can not schedule session on "+session.getDate()+" at "+session.getStartTime()+
										". As, Faculty " + session.getFacultyId() + " is Taking session on "+bean.getDate()+ " at "+bean.getStartTime()+
										" of "+bean.getSubject()+ " : "+bean.getSessionName();
						}
						if (!session.getIsRecommendationSession().equalsIgnoreCase("ByPass") || session.getIsRecommendationSession().equalsIgnoreCase("N")){
							getRecommendationSession(session, request);
						}
						return msg;
					}*/
				}
			} else {
				return "Invalid Faculty ID:" + session.getFacultyId();
			}

			// Checks for faculty --> End

			// Checks for Alt faculty --> Start

			if (!StringUtils.isBlank(session.getAltFacultyId())) {
				if (!tempFacultyList.contains(session.getAltFacultyId())) {
					return "Invalid Parallel Faculty 1 ID:" + session.getAltFacultyId();
				}

				// Check for duplicate facultyIds
				if (session.getFacultyId().equalsIgnoreCase(session.getAltFacultyId())) {
					return "Duplicate facultyId and Parllel 1 faculty Id : " + session.getFacultyId() + " and " + session.getAltFacultyId();
				}

				tempSession.setFacultyId(session.getAltFacultyId());

				//Added Check to byPass All faculty Checks
				if (!"Y".equalsIgnoreCase(session.getByPassFaculty())) {
					boolean isFacultyAvailable = dao.isFacultyAvailable(tempSession);
					if (!isFacultyAvailable) {
						return "Parallel 1 Faculty is NOT available on " + session.getDate();
					}
	
					boolean isFacultyFree = dao.isFacultyFreeAllChecksV2(tempSession);
					if (!isFacultyFree) {
						if (!session.getIsRecommendationSession().equalsIgnoreCase("ByPass") || session.getIsRecommendationSession().equalsIgnoreCase("N")){
							getRecommendationSession(session, request);
						}
						return "Parallel faculty 1 : " + tempSession.getFacultyId() + " is occupied on " + session.getDate()
								+ " at " + session.getStartTime();
					}
	
					boolean isFacultyTakingLessThan2SubjectsSameDay = dao .isFacultyTakingLessThan2SubjectsSameDay(tempSession);
					if (!isFacultyTakingLessThan2SubjectsSameDay) {
						return "Parallel Faculty 1 " + tempSession.getFacultyId() + " is already taking 2 sessions on "+ session.getDate();
					}
					
					//Commented By Somesh as Check is not Required for Now
					/*
					ArrayList<SessionDayTimeBean> earlyOrLastSessionList = dao.isFacultyNotTakingEarlyOrLastSession(tempSession);
					if (earlyOrLastSessionList.size() > 0) {
						String msg = "";
						for (SessionDayTimeBean bean : earlyOrLastSessionList) {
							msg = msg + "Can not schedule session on "+session.getDate()+" at "+session.getStartTime()+
									". As, Faculty " + tempSession.getFacultyId() + " is Taking session on "+bean.getDate()+ " at "+bean.getStartTime()+
									" of "+bean.getSubject()+ " : "+bean.getSessionName();
						}
						if (!session.getIsRecommendationSession().equalsIgnoreCase("ByPass") || session.getIsRecommendationSession().equalsIgnoreCase("N")){
							getRecommendationSession(session, request);
						}
						return msg;
					}*/
				}
			}

			// Checks for Alt faculty --> End

			// Checks for Alt2 faculty --> Start

			if (!StringUtils.isBlank(session.getAltFacultyId2())) {
				if (!tempFacultyList.contains(session.getAltFacultyId2())) {
					return "Invalid Parallel Faculty 2 ID:" + session.getAltFacultyId2();
				}
				// Check for duplicate facultyIds
				if (session.getFacultyId().equalsIgnoreCase(session.getAltFacultyId2())
						|| session.getAltFacultyId().equalsIgnoreCase(session.getAltFacultyId2())) {
					return "Duplicate facultyIds " + session.getFacultyId() + " - " + session.getAltFacultyId() + " - " + session.getAltFacultyId2();
				}

				tempSession.setFacultyId(session.getAltFacultyId2());

				//Added Check to byPass All faculty Checks
				if (!"Y".equalsIgnoreCase(session.getByPassFaculty())) {
					boolean isFacultyAvailable = dao.isFacultyAvailable(tempSession);
					if (!isFacultyAvailable) {
						return "Parallel 2 Faculty is NOT available on " + session.getDate();
					}
	
					boolean isFacultyFree = dao.isFacultyFreeAllChecksV2(tempSession);
					if (!isFacultyFree) {
						return "Parallel faculty 2 is occupied on " + session.getDate() + " at " + session.getStartTime();
					}
	
					boolean isFacultyTakingLessThan2SubjectsSameDay = dao.isFacultyTakingLessThan2SubjectsSameDay(tempSession);
					if (!isFacultyTakingLessThan2SubjectsSameDay) {
						return "Parallel Faculty 2 " + tempSession.getFacultyId() + " is already taking 2 sessions on "+ session.getDate();
					}
					
					//Commented By Somesh as Check is not Required for Now
					/*
					ArrayList<SessionDayTimeBean> earlyOrLastSessionList = dao.isFacultyNotTakingEarlyOrLastSession(tempSession);
					if (earlyOrLastSessionList.size() > 0) {
						String msg = "";
						for (SessionDayTimeBean bean : earlyOrLastSessionList) {
							msg = msg + "Can not schedule session on "+session.getDate()+" at "+session.getStartTime()+
									". As, Faculty " + tempSession.getFacultyId() + " is Taking session on "+bean.getDate()+ " at "+bean.getStartTime()+
									" of "+bean.getSubject()+ " : "+bean.getSessionName();
						}
						if (!session.getIsRecommendationSession().equalsIgnoreCase("ByPass") || session.getIsRecommendationSession().equalsIgnoreCase("N")){
							getRecommendationSession(session, request);
						}
						return msg;
					}*/
				}
			}

			// Checks for Alt2 faculty --> End

			// Checks for Alt3 faculty --> Start

			if (!StringUtils.isBlank(session.getAltFacultyId3())) {
				if (!tempFacultyList.contains(session.getAltFacultyId3())) {
					return "Invalid Parallel Faculty 3 ID:" + session.getAltFacultyId3();
				}
				// Check for duplicate facultyIds
				if (session.getFacultyId().equalsIgnoreCase(session.getAltFacultyId3())
						|| session.getAltFacultyId().equalsIgnoreCase(session.getAltFacultyId3())
						|| session.getAltFacultyId2().equalsIgnoreCase(session.getAltFacultyId3())) {
					return "Duplicate facultyIds " + session.getFacultyId() + " - " + session.getAltFacultyId() + " - "
							+ session.getAltFacultyId2() + " - " + session.getAltFacultyId3();
				}

				tempSession.setFacultyId(session.getAltFacultyId3());

				//Added Check to byPass All faculty Checks
				if (!"Y".equalsIgnoreCase(session.getByPassFaculty())) {
					boolean isFacultyAvailable = dao.isFacultyAvailable(tempSession);
					if (!isFacultyAvailable) {
						return "Parallel 3 Faculty is NOT available on " + session.getDate();
					}
	
					boolean isFacultyFree = dao.isFacultyFreeAllChecksV2(tempSession);
					if (!isFacultyFree) {
						return "Parallel faculty 3 : " + tempSession.getFacultyId() + " is occupied on " + session.getDate()
								+ " at " + session.getStartTime();
					}
	
					boolean isFacultyTakingLessThan2SubjectsSameDay = dao.isFacultyTakingLessThan2SubjectsSameDay(tempSession);
					if (!isFacultyTakingLessThan2SubjectsSameDay) {
						return "Parallel Faculty 3 " + tempSession.getFacultyId() + " is already taking 2 sessions on "+ session.getDate();
					}
					
					//Commented By Somesh as Check is not Required for Now
					/*
					ArrayList<SessionDayTimeBean> earlyOrLastSessionList = dao.isFacultyNotTakingEarlyOrLastSession(tempSession);
					if (earlyOrLastSessionList.size() > 0) {
						String msg = "";
						for (SessionDayTimeBean bean : earlyOrLastSessionList) {
							msg = msg + "Can not schedule session on "+session.getDate()+" at "+session.getStartTime()+
									". As, Faculty " + tempSession.getFacultyId() + " is Taking session on "+bean.getDate()+ " at "+bean.getStartTime()+
									" of "+bean.getSubject()+ " : "+bean.getSessionName();
						}
						if (!session.getIsRecommendationSession().equalsIgnoreCase("ByPass") || session.getIsRecommendationSession().equalsIgnoreCase("N")){
							getRecommendationSession(session, request);
						}
						return msg;
					}*/
				}
			}

			// Checks for Alt3 faculty --> End

			boolean isDateTimeValid = dao.isDateTimeValid(session);
			if (!isDateTimeValid) {
				return "Date " + session.getDate() + " and Time " + session.getStartTime()
						+ " is not valid as per agreed days and time slot OR not within Academic Calendar dates.";
			}
			/*
			 * boolean isSlotAvailable = dao.isSlotAvailable(session);
			 * if(!isSlotAvailable){ return "Already 6 sessions going on "+session.getDate() + " at "+session.getStartTime(); }
			 */

			boolean isNotHoliday = dao.isNotHoliday(session);
			if (!isNotHoliday) {
				return session.getDate() + " is a Holiday ";
			}


			if (!"Y".equalsIgnoreCase(session.getByPassChecks())) {
			if ((getSemSubjectNProgramSubjectBeanMap().containsKey("1-" + session.getSubject())
					|| getSemSubjectNProgramSubjectBeanMap().containsKey("2-" + session.getSubject()))
					&& !StringUtils.isBlank(session.getTrack()) // added after corporate Diageo on 23Jan19 as corporate
																// doesnot have track
			) {

				// Check for not more than 3 subjects to be scheduled on Same day check by
				// Program Sem Track

				boolean isNotMoreThanLimitSubjectsSameDayByProgSemTrack = dao.isNotMoreThanLimitSubjectsSameDayByProgSemTrackV3(session);

				if (!isNotMoreThanLimitSubjectsSameDayByProgSemTrack && "Y".equalsIgnoreCase(session.getHasModuleId())) {
					return "Already 1 sessions of Program Semester Track Batch going on " + session.getDate() + " of track " + session.getTrack();
				}
				
				if (!isNotMoreThanLimitSubjectsSameDayByProgSemTrack && !"Y".equalsIgnoreCase(session.getHasModuleId())) {
					
					ArrayList<SessionDayTimeAcadsBean>moreThan3SessionClashList = get3SessionsClashingSubjectList(session);
					String table =  errorTableFormat(moreThan3SessionClashList);
					if (!session.getIsRecommendationSession().equalsIgnoreCase("ByPass") || session.getIsRecommendationSession().equalsIgnoreCase("N")){
						getRecommendationSession(session, request);
					}
					return "Already 3 sessions of Program Semester Track going on " + session.getDate() + " of track "
							+ session.getTrack()+ " /3 <br>"+table;
				}

				// To Be check with sanket sir by Pranit on 26 Dec 18
				boolean isNotMoreThan3CommonSubjectsSameDay = dao.isNotMoreThan3CommonSubjectsSameDayByTrack(session);				
				if(!isNotMoreThan3CommonSubjectsSameDay){
					ArrayList<SessionDayTimeAcadsBean>moreThan3SessionClashList = get3SessionsClashingSubjectList(session);
					String table =  errorTableFormat(moreThan3SessionClashList);
					if (!session.getIsRecommendationSession().equalsIgnoreCase("ByPass") || session.getIsRecommendationSession().equalsIgnoreCase("N")){
						getRecommendationSession(session, request);
					}
					return "Already 3 sessions of common subjects going on "+session.getDate()+" of track "+ session.getTrack() + " /7 <br>"+table;
				}	
				
				boolean isNoSubjectClashing = dao.isNoSubjectClashingV3(session);
				String specializationTypeOfSessionToBeScheduled = dao.getspecializationTypeBySubjectCode(session.getSubjectCode());
				
				if(!isNoSubjectClashing){	
					ArrayList<SessionDayTimeAcadsBean> clashingList = getClashingSubjectList(session);
					
					String msg = checkClashingOfSubjectCodes(clashingList, specializationTypeOfSessionToBeScheduled, session);
					
					if (!StringUtils.isBlank(msg)) {
						if (!session.getIsRecommendationSession().equalsIgnoreCase("ByPass") || session.getIsRecommendationSession().equalsIgnoreCase("N")){
							getRecommendationSession(session, request);
						}
						return msg;
					}
				}
				
				/*//Check if subject is matching with same subject of other track
				boolean isSubjectClashingWithSameSubjectOfOtherTrack = dao.isSubjectClashingWithSameSubjectOfOtherTrack(session);
				if(isSubjectClashingWithSameSubjectOfOtherTrack){
					return "Subject "+session.getSubject() + " clashing with same subjects of same track on " + session.getDate()+ " at "+session.getStartTime();
				}*/
				
			}else {
				
				boolean isNoSubjectClashing = dao.isNoSubjectClashingV3(session);
				String specializationTypeOfSessionToBeScheduled = dao.getspecializationTypeBySubjectCode(session.getSubjectCode());
				
				if(!isNoSubjectClashing){
					ArrayList<SessionDayTimeAcadsBean> clashingList = getClashingSubjectList(session);
					
					String msg = checkClashingOfSubjectCodes(clashingList, specializationTypeOfSessionToBeScheduled, session);
					
					if (!StringUtils.isBlank(msg)) {
						if (!session.getIsRecommendationSession().equalsIgnoreCase("ByPass") || session.getIsRecommendationSession().equalsIgnoreCase("N")){
							getRecommendationSession(session, request);
						}
						return msg;
					}
				}

				// Check for not more than 3 subjects to be scheduled on Same day check by
				// Program Sem Track
				List<String> tracks = dao.getTracks(session);
				String tempTrack=session.getTrack();
				for (String track : tracks) {
					session.setTrack(track);
					boolean isNotMoreThanLimitSubjectsSameDayByProgSemTrack = dao.isNotMoreThanLimitSubjectsSameDayByProgSemTrackV3(session);
					session.setTrack(tempTrack);
					
					if (!isNotMoreThanLimitSubjectsSameDayByProgSemTrack && "Y".equalsIgnoreCase(session.getHasModuleId())) {
						return "Already 1 sessions of Program Semester Track Batch going on " + session.getDate();
					}
					
					if (!isNotMoreThanLimitSubjectsSameDayByProgSemTrack && !"Y".equalsIgnoreCase(session.getHasModuleId())) {
						ArrayList<SessionDayTimeAcadsBean>moreThan3SessionClashList = get3SessionsClashingSubjectList(session);
						String table =  errorTableFormat(moreThan3SessionClashList);
						if (!session.getIsRecommendationSession().equalsIgnoreCase("ByPass") || session.getIsRecommendationSession().equalsIgnoreCase("N") || session.getIsRecommendationSession().equalsIgnoreCase("N")){
							getRecommendationSession(session, request);
						}
						session.setTrack(tempTrack);
						return "Already 3 sessions of Program Semester Track going on " + session.getDate()+ " of track " + session.getTrack()+ " /4<br>"+table;
					}
					session.setTrack(tempTrack);
				}
				
				//Commented by Somesh on 20/07/2021 as we are already Checking More than 3 session on same day by track
				/*
				boolean isNotMoreThan3CommonSubjectsSameDay = dao.isNotMoreThan3CommonSubjectsSameDayByCorporateName(session);
				if (!isNotMoreThan3CommonSubjectsSameDay) {
					ArrayList<SessionDayTimeBean>moreThan3SessionClashList = get3SessionsClashingSubjectList(session);
					String table =  errorTableFormat(moreThan3SessionClashList);
					if (!session.getIsRecommendationSession().equalsIgnoreCase("ByPass") || session.getIsRecommendationSession().equalsIgnoreCase("N")){
						getRecommendationSession(session, request);
					}
					return "Already 3 sessions of common subjects going on " + session.getDate()+ "/8 <br>"+table;
				}
				*/
			}
			}

			/*
			 * Uncommented by Pranit on 31 Dec 18 kept for refrence
			 * if(getSasSubjectsList().contains(session.getSubject())){ List<String>
			 * StudentSubjectListOfHavingSaSAndOtherProgramActive =
			 * getStudentSubjectListOfHavingSaSAndOtherProgramActive(dao.
			 * getLiveAcadSessionMonth(),dao.getLiveAcadSessionYear()); ArrayList<String>
			 * lstOfClashingSubjects =
			 * dao.getSessionScheduledOnSameDayTime(session.getDate(),session.getStartTime()
			 * ,StudentSubjectListOfHavingSaSAndOtherProgramActive);
			 * if(lstOfClashingSubjects !=null && !lstOfClashingSubjects.isEmpty() &&
			 * lstOfClashingSubjects.size() > 0 ){ return
			 * "Session Time "+session.getStartTime() + " clashing with "+String.join(",",
			 * lstOfClashingSubjects)
			 * +" sessions of SAS students PG/Diploma/Certificate program on " +
			 * session.getDate() ; } }
			 * 
			 * if(getStudentSubjectListOfHavingSaSAndOtherProgramActive(dao.
			 * getLiveAcadSessionMonth(),dao.getLiveAcadSessionYear()).contains(session.
			 * getSubject())){ List<String>
			 * StudentsSASSubjectListOfHavingSaSAndOtherProgramActive =
			 * getStudentsSASSubjectListOfHavingSaSAndOtherProgramActive(dao.
			 * getLiveAcadSessionMonth(),dao.getLiveAcadSessionYear()); ArrayList<String>
			 * lstOfClashingSubjects =
			 * dao.getSessionScheduledOnSameDayTime(session.getDate(),session.getStartTime()
			 * ,StudentsSASSubjectListOfHavingSaSAndOtherProgramActive);
			 * if(lstOfClashingSubjects.size() > 0 && lstOfClashingSubjects != null &&
			 * !lstOfClashingSubjects.isEmpty()){ return
			 * "Session Time "+session.getStartTime() + " clashing with "+String.join(",",
			 * lstOfClashingSubjects) +" sessions of SAS students program on " +
			 * session.getDate() ; } }
			 */

			session = allocateAvailableRoomForBatchUpload(session);

			if (session.getErrorMessage() != null && !"".equals(session.getErrorMessage())) {
				return session.getErrorMessage();
			}

			// Using insertDuplicateSession() as it contains all the necessary fields than
			// normal inssertSsession()
			
//			if (!StringUtils.isBlank(session.getMasterKey())) {
//				boolean isSubjectApplicable = dao.isSubjectApplicable(session.getMasterKey(), session.getSubject());
//				if (!isSubjectApplicable) {
//					return session.getSubject() +" Subject is not applicable for selected program.";
//				}
//			}

			if (session.getIsRecommendationSession().equalsIgnoreCase("N") || 
					session.getBatchUpload().equalsIgnoreCase("Y") || session.getHasModuleId().equalsIgnoreCase("Y")) {
				Boolean sessionAdded = dao.insertDuplicateSession(session);
				if (!sessionAdded) {
					return session.getErrorMessage() + ". Error while saving data to database";
				}
			}
			
			if ("Y".equalsIgnoreCase(session.getHasModuleId())) {
				String idForSession = dao.getSessionId(session);
				session.setId(idForSession);
				dao.insertSessionPost(session);
				Post post = dao.findSessionPostByReferenceId(session.getId());
//				insertToRedis(post);
				if (post == null) {
					return "Session created successfully... Error while adding post feed.";
				}
				ContentDAO cDao = (ContentDAO) act.getBean("contentDAO");
				loggerForSessionScheduling.info("Calling refreshRedis...");
				cDao.refreshRedis(post);
			}

			/*
			 * if(session.getHasModuleId().equalsIgnoreCase("Y")){ String
			 * idForSession=dao.getSessionId(session); session.setId(idForSession);
			 * dao.insertSessionModuleMapping(session); }
			 */

		} catch (Exception ex) {
			ex.printStackTrace();
			loggerForSessionScheduling.info("Error while validity check "+ex.getMessage());
			return "Error while validity check " + ex.getMessage();
		}

		return null;
	}
	// Batch session scheduling end

	/*
	 * public String insertToRedis(Post posts) { RestTemplate restTemplate = new
	 * RestTemplate(); try { String url =
	 * SERVER_PATH+"timeline/api/post/savePostInRedis";
	 * HttpHeaders headers = new HttpHeaders();
	 * headers.setAccept(Arrays.asList(MediaType.APPLICATION_JSON));
	 * HttpEntity<Post> entity = new HttpEntity<Post>(posts,headers);
	 * 
	 * return restTemplate.exchange( url, HttpMethod.POST, entity,
	 * String.class).getBody(); } catch (RestClientException e) {
	 *    return "Error IN rest call got "+e.getMessage(); } }
	 */
	
	@RequestMapping(value = "/verifySessionFeasibility", method = { RequestMethod.GET, RequestMethod.POST })
	public ModelAndView verifySessionFeasibility(HttpServletRequest request, HttpServletResponse response, @ModelAttribute SessionDayTimeAcadsBean session) {
		
		String edit = request.getParameter("id") != null ? "true" : "false";
		ModelAndView modelnView = new ModelAndView("addScheduledSession");
		try {
			SessionDayTimeAcadsBean sessionBeforeEdit = (SessionDayTimeAcadsBean) request.getSession()
					.getAttribute("sessionBeforeEdit");
			boolean dateTimeSubjectChanged = false;
			if (sessionBeforeEdit != null) {
				dateTimeSubjectChanged = (!sessionBeforeEdit.getDate().equals(session.getDate()))
						|| (!sessionBeforeEdit.getStartTime().equals(session.getStartTime()))
						|| (!sessionBeforeEdit.getSubject().equals(session.getSubject()))
						|| (!sessionBeforeEdit.getSessionName().equals(session.getSessionName()));
			}

			TimeTableDAO dao = (TimeTableDAO) act.getBean("timeTableDAO");

			if (!getFacultyList().contains(session.getFacultyId())) {
				return sendToErrorPage(request, session, "Invalid Faculty ID:" + session.getFacultyId(), edit);
			}

			String successMessage = "";
			String errorMessage = "";

			if (getSasSubjectsList().contains(session.getSubject())) {
				List<String> StudentSubjectListOfHavingSaSAndOtherProgramActive = getStudentSubjectListOfHavingSaSAndOtherProgramActive(
						dao.getLiveAcadSessionMonth(), dao.getLiveAcadSessionYear());
				ArrayList<String> lstOfClashingSubjects = dao.getSessionScheduledOnSameDayTime(session.getDate(),
						session.getStartTime(), StudentSubjectListOfHavingSaSAndOtherProgramActive, session);
				if (lstOfClashingSubjects != null && !lstOfClashingSubjects.isEmpty()
						&& lstOfClashingSubjects.size() > 0) {
					errorMessage = errorMessage + "Session Time " + session.getStartTime() + " clashing with "
							+ String.join(",", lstOfClashingSubjects)
							+ " sessions of SAS students PG/Diploma/Certificate program on " + session.getDate()
							+ "<br>";
				} else {
					successMessage = successMessage + "Session Time " + session.getStartTime()
							+ " is NOT clashing with other sessions of SAS students PG/Diploma/Certificate program "
							+ session.getDate() + " at " + session.getStartTime() + "<br>";
				}
			}
			if (getStudentSubjectListOfHavingSaSAndOtherProgramActive(dao.getLiveAcadSessionMonth(),
					dao.getLiveAcadSessionYear()).contains(session.getSubject())) {
				List<String> StudentsSASSubjectListOfHavingSaSAndOtherProgramActive = getStudentsSASSubjectListOfHavingSaSAndOtherProgramActive(
						dao.getLiveAcadSessionMonth(), dao.getLiveAcadSessionYear());
				ArrayList<String> lstOfClashingSubjects = dao.getSessionScheduledOnSameDayTime(session.getDate(),
						session.getStartTime(), StudentsSASSubjectListOfHavingSaSAndOtherProgramActive, session);
				if (lstOfClashingSubjects.size() > 0 && lstOfClashingSubjects != null
						&& !lstOfClashingSubjects.isEmpty()) {
					errorMessage = errorMessage + "Session Time " + session.getStartTime() + " clashing with "
							+ String.join(",", lstOfClashingSubjects) + " sessions of SAS students program on "
							+ session.getDate() + "<br>";
				} else {
					successMessage = successMessage + "Session Time " + session.getStartTime()
							+ " is NOT clashing with other sessions of SAS students program " + session.getDate()
							+ " at " + session.getStartTime() + "<br>";
				}
			}

			boolean isNotHoliday = dao.isNotHoliday(session);
			if (!isNotHoliday) {
				errorMessage = errorMessage + "Date " + session.getDate() + " is Holiday. <br>";
				return sendToErrorPage(request, session, errorMessage, edit);
			} else {
				successMessage = successMessage + "Date " + session.getDate() + " is not a Holiday.<br>";
			}

			boolean isDateTimeValid = dao.isDateTimeValid(session);
			if (!isDateTimeValid) {
				errorMessage = errorMessage + "Date " + session.getDate() + " and Time " + session.getStartTime()
						+ " is not valid as per agreed days and time slot OR not within Academic Calendar dates. <br>";
				return sendToErrorPage(request, session, errorMessage, edit);
			} else {
				successMessage = successMessage + "Date " + session.getDate() + " and Time " + session.getStartTime()
						+ " is valid as per agreed days and time slot & is within Academic Calendar dates.<br>";
			}

			boolean isFacultyAvailable = dao.isFacultyAvailable(session);
			if (!isFacultyAvailable) {
				if (!session.getIsRecommendationSession().equalsIgnoreCase("ByPass") || session.getIsRecommendationSession().equalsIgnoreCase("N")){
					getRecommendationSession(session, request);
				}
				errorMessage = errorMessage + "Faculty is NOT available on " + session.getDate()+" at "+session.getStartTime() + " <br>";
			} else {
				successMessage = successMessage + "Faculty is available on " + session.getDate()+" at "+session.getStartTime() + " <br>";
			}

			if (isFacultyAvailable) {
				boolean isFacultyFree = dao.isFacultyFreeAllChecksV2(session);
				if (!isFacultyFree) {
					
					ArrayList<SessionDayTimeAcadsBean> facultySession = dao.getFacultyClashDeatils(session);
					String msg = "";
					for (SessionDayTimeAcadsBean bean : facultySession) {
						msg = msg + "Faculty is occupied taking "+bean.getSessionName()+" of Subject : "+bean.getSubject()+
								 	" of "+bean.getTrack()+" on " + bean.getDate() + " at " + bean.getStartTime()+ " /6<br>";
					}
					
					if (!session.getIsRecommendationSession().equalsIgnoreCase("ByPass") || session.getIsRecommendationSession().equalsIgnoreCase("N")){
						getRecommendationSession(session, request);
					}
					errorMessage = errorMessage + msg + " /11<br>";
				} else {
					successMessage = successMessage + "Faculty is free on " + session.getDate() + " at "
							+ session.getStartTime() + " <br>";
				}
			}

			if (dateTimeSubjectChanged) {
				// If date, time, subject is changed, then do this check. If it not changed,
				// meaning only faculty is changed, then this check is not needed.
				boolean isSlotAvailable = dao.isSlotAvailable(session);
				if (!isSlotAvailable) {
					errorMessage = errorMessage + "Already 5 sessions going on " + session.getDate() + " at "
							+ session.getStartTime() + " <br>";
				} else {
					successMessage = successMessage + "Less than 5 sessions going on " + session.getDate() + " at "
							+ session.getStartTime() + " <br>";
				}
			}
			
			//Commented by Somesh on 20/07/2021 as we are already Checking Morethan 3 session on same day by track
			/*
			boolean isNotMoreThan3CommonSubjectsSameDay = dao.isNotMoreThan3CommonSubjectsSameDayByCorporateName(session);
			if (!isNotMoreThan3CommonSubjectsSameDay) {
				errorMessage = errorMessage + "Already 3 sessions of common subjects going on " + session.getDate() + " <br>";
			} else {
				successMessage = successMessage + "Less than 3 sessions of common subjects going on " + session.getDate() + " <br>";
			}
			*/

			boolean isFacultyTakingLessThan2SubjectsSameDay = dao.isFacultyTakingLessThan2SubjectsSameDay(session);
			if (!isFacultyTakingLessThan2SubjectsSameDay) {
				ArrayList<SessionDayTimeAcadsBean> facultySessions = dao.getSameDaySessionsForFaculty(session);
				String msg = "Faculty " + session.getFacultyId() + " is already taking 2 sessions on "+ session.getDate()+ "<br>";
				int count = 1;
				for (SessionDayTimeAcadsBean bean : facultySessions) {
					msg = msg + count +". Subject : "+bean.getSubject()+ " on "+bean.getDate()
							 +" at "+bean.getStartTime()+ " of Track : "+bean.getTrack()+ "<br>";
					count++;
				}
				errorMessage = errorMessage + msg;
			} else {
				successMessage = successMessage + "Faculty " + session.getFacultyId()
						+ " is taking less than 2 sessions on " + session.getDate() + " <br>";
			}

			if (dateTimeSubjectChanged) {
				// If date, time, subject is changed, then do this check. If it not changed,
				// meaning only faculty is changed, then this check is not needed.
				boolean isNoSubjectClashing = dao.isNoSubjectClashingV3(session);
				if(!isNoSubjectClashing){
					ArrayList<SessionDayTimeAcadsBean> clashingList = getClashingSubjectList(session);
					String specializationTypeOfSessionToBeScheduled = dao.getspecializationTypeBySubjectCode(session.getSubjectCode());

					String msg = checkClashingOfSubjectCodes(clashingList, specializationTypeOfSessionToBeScheduled, session);
					errorMessage = errorMessage + msg;
				}else{
					successMessage = successMessage + "Subject "+session.getSubject() + " is NOT clashing with other subjects of same sem on " + session.getDate()+ " at "+session.getStartTime()+"<br>";
				}
			}

			ArrayList<EndPointBean> locationList = dao.getAvailableRoomByLoaction(session.getDate(),
					session.getStartTime(), session.getFacultyLocation());

			if (locationList.size() != 0 || !locationList.isEmpty()) {

				successMessage = successMessage + "No capacity available at " + session.getFacultyLocation()
						+ " Location on " + session.getDate() + " at " + session.getStartTime() + "<br>";
			} else {
				errorMessage = errorMessage + "No capacity available at " + session.getFacultyLocation()
						+ " Location on " + session.getDate() + " at " + session.getStartTime() + "<br>";
			}

			if (!"".equals(successMessage)) {
				request.setAttribute("success", "true");
				request.setAttribute("successMessage", successMessage);
			}

			if (!"".equals(errorMessage)) {
				request.setAttribute("error", "true");
				request.setAttribute("errorMessage", errorMessage);
			}

			modelnView.addObject("session", session);
			request.setAttribute("edit", edit);
			modelnView.addObject("yearList", ACAD_YEAR_LIST);
			modelnView.addObject("subjectList", getSubjectList());
			modelnView.addObject("locationList", getLocationList());
			modelnView.addObject("trackList", trackList);
			setOtherSessionsInModel(session, modelnView);

			return modelnView;

		} catch (Exception e) {
			  
			return sendToErrorPage(request, session, e.getMessage(), edit);
		}
	}

	private ModelAndView sendToErrorPageForCommonSession(HttpServletRequest request, SessionDayTimeAcadsBean session,
			String errorMessage, String edit) {
		ModelAndView errorModelnView = new ModelAndView("addCommonSession");
		TimeTableDAO dao = (TimeTableDAO) act.getBean("timeTableDAO");
		errorModelnView.addObject("programList", dao.getAllPrograms());
		request.setAttribute("error", "true");
		request.setAttribute("errorMessage", errorMessage);
		errorModelnView.addObject("session", session);
		request.setAttribute("edit", edit);
		errorModelnView.addObject("yearList", ACAD_YEAR_LIST);
		errorModelnView.addObject("locationList", getLocationList());
		errorModelnView.addObject("consumerTypeList", getConsumerTypesList());
		errorModelnView.addObject("semList", semList);
		errorModelnView.addObject("sessionTypesMap", getSessionTypesMap());

		setOtherSessionsInModel(session, errorModelnView);

		return errorModelnView;
	}

	private ModelAndView sendToErrorPage(HttpServletRequest request, SessionDayTimeAcadsBean session, String errorMessage, String edit) {
		ModelAndView errorModelnView = new ModelAndView("addScheduledSession");
		request.setAttribute("error", "true");
		request.setAttribute("errorMessage", errorMessage);
		errorModelnView.addObject("session", session);
		request.setAttribute("edit", edit);
		errorModelnView.addObject("yearList", ACAD_YEAR_LIST);
		errorModelnView.addObject("subjectList", getSubjectList());
		errorModelnView.addObject("subjectCodeMap", getsubjectCodeMap());
		errorModelnView.addObject("masterKeysWithSubjectCodes", getMasterKeysWithSubjectCodes());
		errorModelnView.addObject("locationList", getLocationList());
		errorModelnView.addObject("trackList", trackList);
		errorModelnView.addObject("masterKeys", getMasterKeys());
		errorModelnView.addObject("sessionTypesMap", getSessionTypesMap());
		
		setOtherSessionsInModel(session, errorModelnView);

		return errorModelnView;
	}

	public static void main(String[] args) throws ParseException {
		String sessionDate = "2015-08-08";
		String sessionTime = "12:00:00";

		Date sessionDateTime = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(sessionDate + " " + sessionTime);
		Date currentSessionDate = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(sessionDate + " 23:59:00");

		if (currentSessionDate.before(new Date())) {
			// Date earlier No action needed
			// Cannot do changes to past meeting
		} else if (sessionDateTime.after(new Date())) {
			// Date Later than today No action needed
			// Cannot do changes to past meeting
		} else if (sessionDateTime.before(new Date())) {
			// Time is also earlier so no action needed

			Calendar cal = Calendar.getInstance();
			cal.setTime(new Date());
			cal.add(Calendar.MINUTE, 5);

			sessionTime = new SimpleDateFormat("HH:mm:ss").format(cal.getTime());
		}


		sessionDate = "08/13/2015";
		sessionTime = "12:00:00";
		sessionDateTime = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss").parse(sessionDate + " " + sessionTime);
		long minutesToSession = getDateDiff(new Date(), sessionDateTime, TimeUnit.MINUTES);

	}

	@RequestMapping(value = "/addAltMeetingKey", method = { RequestMethod.GET, RequestMethod.POST })
	public @ResponseBody String addAltMeetingKey(HttpServletRequest request, HttpServletResponse respnse,
			@RequestParam String pk, @RequestParam String value) {
		String meetingNumberParameter = (String) request.getParameter("MN");
		try {
			TimeTableDAO dao = (TimeTableDAO) act.getBean("timeTableDAO");
			dao.addAltMeetingKey(pk, value, meetingNumberParameter);
			return "Alt Meeting Key added successfully";
		} catch (Exception e) {
			  
			return "Error in adding Me";
		}
	}

	@RequestMapping(value = "/addAltFacultyId", method = { RequestMethod.GET, RequestMethod.POST })
	public  ResponseEntity<HashMap<String,String>> addAltFacultyId(HttpServletRequest request, @RequestParam String pk, 
																	@RequestParam String value) {
		
		HttpHeaders headers = new HttpHeaders();
		headers.add("Content-Type", "application/json");
		HashMap<String,String> response = new HashMap<>();
		
		try {
			TimeTableDAO dao = (TimeTableDAO) act.getBean("timeTableDAO");
			String facultyParameter = (String) request.getParameter("AF");
			String[] values = value.split("-");
			String facultyId = values[0];
			
			String byPassFaculty = "N";
			if (values.length == 2) {
				byPassFaculty = "Y";
			}
			
			loggerForSessionScheduling.info("In addAltFacultyId pk: "+pk+" value: "+value+" byPassFaculty: "+byPassFaculty);
			
			if (getFacultyList().contains(facultyId) && !StringUtils.isBlank(facultyParameter)) {
				SessionDayTimeAcadsBean session = dao.getSessionById(pk);
				//Set new Faculty ID
				session.setFacultyId(facultyId);
				if ("N".equalsIgnoreCase(byPassFaculty)) {
					
					boolean isFacultyAvailable = dao.isFacultyAvailable(session);
					if (!isFacultyAvailable) {
						response.put("status", "Fail");
						response.put("message", "Faculty "+ session.getFacultyId() + " is NOT available on "+ session.getDate());
						return new ResponseEntity<>(response,headers, HttpStatus.OK);
					}
					
					boolean isFacultyFree = dao.isFacultyFreeAllChecksV2(session);
					if (!isFacultyFree) {
						response.put("status", "Fail");
						response.put("message", "Faculty "+session.getFacultyId()+" is occupied on "+session.getDate()+" at "+session.getStartTime());
						return new ResponseEntity<>(response,headers, HttpStatus.OK);
					}
					
					boolean isFacultyTakingLessThan2SubjectsSameDay = dao.isFacultyTakingLessThan2SubjectsSameDay(session);
					if (!isFacultyTakingLessThan2SubjectsSameDay) {
						response.put("status", "Fail");
						response.put("message", "Faculty " + session.getFacultyId() +" is already taking 2 sessions on "+ session.getDate());
						return new ResponseEntity<>(response,headers, HttpStatus.OK);
					}
				}
				
				dao.addAltFacultyId(pk, facultyId, facultyParameter);
				response.put("status", "Success");
				response.put("message", "Added Faculty for Parallel Session Number "+facultyParameter+" Successfully");
				return new ResponseEntity<>(response,headers, HttpStatus.OK);
				
			}else {
				response.put("status", "Fail");
				response.put("message", "Invalid Faculty Id.");
				return new ResponseEntity<>(response,headers, HttpStatus.OK);
			}
		} catch (Exception e) {
			loggerForSessionScheduling.info("Error in addAltFacultyId: "+e.getMessage()); 
			response.put("status", "Fail");
			response.put("message", "Error in adding Parallel Faculty "+e.getMessage());
			return new ResponseEntity<>(response,headers, HttpStatus.OK);
		}
	}
	
	@RequestMapping(value = "/addAltMeetingPwd", method = { RequestMethod.GET, RequestMethod.POST })
	public @ResponseBody String addAltMeetingPwd(HttpServletRequest request, HttpServletResponse respnse,
			@RequestParam String pk, @RequestParam String value) {
		String meetingPassWordParameter = (String) request.getParameter("MP");
		try {
			TimeTableDAO dao = (TimeTableDAO) act.getBean("timeTableDAO");
			dao.addAltMeetingPwd(pk, value, meetingPassWordParameter);
			return "Alt Meeting Key added successfully";
		} catch (Exception e) {
			  
			return "Error in adding Me";
		}
	}

	@RequestMapping(value = "/sessionCancellationForm", method = { RequestMethod.GET, RequestMethod.POST })
	public ModelAndView sessionCancellationForm(HttpServletRequest request, HttpServletResponse respnse) {
		ModelAndView modelAndView = new ModelAndView("sessionCancellation");
		String id = request.getParameter("id");
		String consumerProgramStructureId = request.getParameter("consumerProgramStructureId");
		TimeTableDAO dao = (TimeTableDAO) act.getBean("timeTableDAO");
		SessionDayTimeAcadsBean session = dao.findScheduledSessionById(id);
		session.setConsumerProgramStructureId(consumerProgramStructureId);
		//Commented by Somesh Added new query for getRegisteredStudent
		//ArrayList<StudentBean> studentList = getRegisteredStudentForSubject(session);
		ArrayList<StudentAcadsBean> studentList = getRegisteredStudentForSession(session);
		int noOfStudentRegisteredForSubject = studentList.size();
		request.getSession().setAttribute("noOfStudentRegisteredForSubject", noOfStudentRegisteredForSubject);
		modelAndView.addObject("session", session);
		modelAndView.addObject("noOfStudentRegisteredForSubject", noOfStudentRegisteredForSubject);
		return modelAndView;
	}

	@RequestMapping(value = "/sessionCancellation", method = { RequestMethod.GET, RequestMethod.POST })
	public ModelAndView sessionCancellation(HttpServletRequest request, HttpServletResponse respnse, @ModelAttribute SessionDayTimeAcadsBean session) {
		
		ModelAndView modelAndView = new ModelAndView("searchScheduledSession");
		String userId = (String)request.getSession().getAttribute("userId_acads");
		TimeTableDAO dao = (TimeTableDAO)act.getBean("timeTableDAO");
		try{
			String oldSessionId = session.getId();
			String consumerProgramStructureId = session.getConsumerProgramStructureId();
			
			modelAndView.addObject("searchBean", new SessionDayTimeAcadsBean());
			modelAndView.addObject("yearList", ACAD_YEAR_LIST);
			modelAndView.addObject("subjectList", getSubjectList());
			modelAndView.addObject("session", session);
			
			//If only one session cancelled then 
			//Create new entry into session & Update sessionSubjectMapping
			
			int sessionCount = dao.getSessionApplicableCount(session.getId());
			if (sessionCount == 0) {
				setError(request, "No Mapping found for this session.");
				return modelAndView;
			}
			
			int sizeOfCPSId = session.getConsumerProgramStructureId().split(",").length;
			
			if (sizeOfCPSId >= 1 														
					// if noOfsessions for count not match then create new session
					&& sessionCount > 1 && sessionCount != sizeOfCPSId
					// MBA-Wx program applicable for only one program
					&& !session.getConsumerProgramStructureId().contains("111")	&& !session.getConsumerProgramStructureId().contains("151")
					&& !session.getConsumerProgramStructureId().contains("160")
					// M.Sc. (AI & ML Ops) || M.Sc. (AI) program applicable for only one program
					&& !session.getConsumerProgramStructureId().contains("131") && !session.getConsumerProgramStructureId().contains("158")
					// PC-DS || PD-DS
					&& !session.getConsumerProgramStructureId().contains("154") && !session.getConsumerProgramStructureId().contains("155")) {	
										
				SessionDayTimeAcadsBean existingSession = dao.getSessionById(oldSessionId);
				boolean isSessionInserted = dao.insertNewSessionAfterCancellation(existingSession, session, userId);
				if (!isSessionInserted) {
					setError(request, "Error in saving session to database.");
					return modelAndView;
				}
			}else{
				boolean isUpdated = dao.updateSessionAfterCancellation(session, userId);
				if (!isUpdated) {
					setError(request, "Error in saving session to database.");
					return modelAndView;
				}
			}
			
			if("Y".equals(session.getIsCancelled())){
				dao.createAnnouncement(session,userId);
				sendSessionCancellationEmailAndSMS(session,userId);

			}
			setSuccess(request, "Session Cancelled for Session Name : " + session.getSessionName());
		} catch (Exception e) {
			setError(request, "Error Occurs while Cancelling Session :" + e.getMessage());
		}
		modelAndView.addObject("searchBean", new SessionDayTimeAcadsBean());
		modelAndView.addObject("yearList", ACAD_YEAR_LIST);
		modelAndView.addObject("subjectList", getSubjectList());
		modelAndView.addObject("session", session);
		return modelAndView;
	}

	// reschedule Cancelled Session
	@RequestMapping(value = "/reScheduleSession", method = { RequestMethod.GET, RequestMethod.POST })
	public String reScheduleSession(HttpServletRequest request, HttpServletResponse respnse,
			@ModelAttribute SessionDayTimeAcadsBean session, Model m) throws Exception {
		ModelAndView modelAndView = new ModelAndView("sessionCancellation");
		modelAndView.addObject("searchBean", new SessionDayTimeAcadsBean());
		modelAndView.addObject("yearList", ACAD_YEAR_LIST);
		modelAndView.addObject("subjectList", getSubjectList());
		long reScheduleSessionId;
		int noOfStudentRegisteredForSubject = (int) request.getSession().getAttribute("noOfStudentRegisteredForSubject");
		modelAndView.addObject("session", session);
		modelAndView.addObject("noOfStudentRegisteredForSubject", noOfStudentRegisteredForSubject);

		String userId = (String) request.getSession().getAttribute("userId_acads");
		TimeTableDAO dao = (TimeTableDAO) act.getBean("timeTableDAO");
		SessionDayTimeAcadsBean reSchedulesession = dao.findScheduledSessionById(session.getId());
		try {
			reSchedulesession.setIsCancelled("N");
			reSchedulesession.setDate(session.getReScheduleDate());
			reSchedulesession.setStartTime(session.getReScheduleStartTime());
			reSchedulesession.setEndTime(session.getReScheduleEndTime());
			reScheduleSessionId = dao.createReScheduleSession(reSchedulesession, userId);// create New entry for
																							// reSchedule Session
			setSuccess(request, "Session Re-Schedule for Session Name : " + session.getSessionName());
		} catch (Exception e) {
			setError(request, "Error Occurs while Re-scheduling Session :" + e.getMessage());
			return "sessionCancellation";
		}
		modelAndView.addObject("session", session);
		return "redirect:/refreshSession?id=" + reScheduleSessionId;
	}

	// get No of Student Registered for Selected Subject
	public ArrayList<StudentAcadsBean> getRegisteredStudentForSubject(SessionDayTimeAcadsBean session) {
		ArrayList<String> allAvailableCorporateCenters = notificationDAO.getAllCorporateCenterNames();
		String subject = session.getSubject();
		String corporateCenterName = session.getCorporateName();
		String hasModuleId = session.getHasModuleId();
		String sessionId = session.getId();
		String consumerProgramStructureId = session.getConsumerProgramStructureId();
		
		ArrayList<StudentAcadsBean> studentList = null;

		studentList = notificationDAO.getRegisteredStudentForSubjectByCPSId(sessionId, hasModuleId, consumerProgramStructureId);
		//studentList = notificationDAO.getRegisteredStudentForSubjectNew(sessionId, hasModuleId);
		
		//Commented as added new query with session configurable 
		/*
		if (allAvailableCorporateCenters.contains(corporateCenterName)) {
			studentList = notificationDAO.getRegisteredStudentForSubject(subject, corporateCenterName, hasModuleId,
					new ArrayList<String>());
		} else {
			studentList = notificationDAO.getRegisteredStudentForSubject(subject, null, hasModuleId, allAvailableCorporateCenters);
		}
		*/
		
		return studentList;
	}
	
	public ArrayList<StudentAcadsBean> getRegisteredStudentForSession(SessionDayTimeAcadsBean session) {
		String hasModuleId = session.getHasModuleId();
		String sessionId = session.getId();
		String consumerProgramStructureId = session.getConsumerProgramStructureId();
		ArrayList<StudentAcadsBean> studentList = notificationDAO.getRegisteredStudentForSessionCancellation(sessionId, hasModuleId, consumerProgramStructureId);
		return studentList;
	}

	// sending SMS and Email to Cancelled Session Student
	public void sendSessionCancellationEmailAndSMS(SessionDayTimeAcadsBean session, String userId) {
		//Commented by Somesh Added new query for getRegisteredStudent
		//ArrayList<StudentBean> studentList = getRegisteredStudentForSubject(session);
		ArrayList<StudentAcadsBean> studentList = getRegisteredStudentForSession(session);
		if (studentList != null && studentList.size() > 0) {
			if (ENVIRONMENT.equalsIgnoreCase("PROD")) {
				sendEmailsToStudent(session, userId, studentList);
				sendSMSsToStudent(session, studentList);
			}
		}

		// create Copy of Mail in Student MyCommunication Table
		/* Commented By Somesh Added in sendEmailsToStudent method
		MailBean mailBean = new MailBean();
		mailBean.setBody(session.getCancellationEmailBody());
		mailBean.setMailIdRecipients(toEmailIds);
		mailBean.setSubject(session.getCancellationSubject());
		mailBean.setFromEmailId("NMIMS Global Access SCE");
		mailBean.setSapIdRecipients(toSapIds);
		createRecordInUserMailTableAndMailTable(mailBean, userId, "NMIMS Global Access SCE");
		*/
	}

	private void sendEmailsToStudent(SessionDayTimeAcadsBean session, String userId, ArrayList<StudentAcadsBean> studentList) {
		MailSender mailSender = (MailSender) act.getBean("mailer");
		try {
			ArrayList<String> toEmailIds = new ArrayList<>();
			List<String> toSapIds = new ArrayList<String>();
			for (StudentAcadsBean studentBean : studentList) {
				toEmailIds.add(studentBean.getEmailId());
				toSapIds.add(studentBean.getSapid());
			}
			
			toEmailIds.add("nelson.soans@nmims.edu");
			toSapIds.add("77777777777");
			toEmailIds.add("Sneha.Utekar@nmims.edu");
			toSapIds.add("77777777777");

			// send 1 email at a time
			for (int i = 0; i < toEmailIds.size(); i = i + 1) {
				int lastIndex = (toEmailIds.size() < i + 1 ? toEmailIds.size() : i + 1);
				int lastIndexForSAPId = (toSapIds.size()<i+1 ? toSapIds.size():i+1);
				
				ArrayList<String> currentRecipientList = new ArrayList<String>(toEmailIds.subList(i, lastIndex));
				mailSender.sendEmail(session.getCancellationSubject(), session.getCancellationEmailBody(),currentRecipientList);
				
				// create Copy of Mail in Student MyCommunication Table
				MailAcadsBean mailBean = new MailAcadsBean();
				List<String> emailIdSubList =  toEmailIds.subList(i, lastIndex);
				List<String> sapIdSubList =  toSapIds.subList(i, lastIndexForSAPId);
				mailBean.setBody(session.getCancellationEmailBody());
				mailBean.setMailIdRecipients(emailIdSubList);
				mailBean.setSubject(session.getCancellationSubject());
				mailBean.setFromEmailId("NMIMS Global Access SCE");
				mailBean.setSapIdRecipients(sapIdSubList);
				createRecordInUserMailTableAndMailTable(mailBean, userId, "NMIMS Global Access SCE");
			}
			// update cancellationEmailSent to 'Y'
			notificationDAO.updateCancellationEmailStatus(session);
		} catch (Exception e) {
			  
		}
	}

	private void sendSMSsToStudent(SessionDayTimeAcadsBean session, ArrayList<StudentAcadsBean> studentList) {
		MailSender mailSender = (MailSender) act.getBean("mailer");
		try {
			String message = session.getCancellationSMSBody();
			// String result = smsSender.sendScheduledSessionSMS(session,
			// studentList,message);
			// String result = smsSender.sendScheduledSessionSMSNetCore(session,
			// studentList,message);
			String result = smsSender.sendScheduledSessionSMSmGage(session, studentList, message, loggerForSessionSMSs);
			if ("OK".equalsIgnoreCase(result)) {
				// update cancellationSMSSent to 'Y'
				notificationDAO.updateCancellationSMSStatus(session);
			} else {
				// sending Error Email if SMS does not sent due to Password change or Username
				// change
				ArrayList<String> recipent = new ArrayList<String>(
						Arrays.asList("sanketpanaskar@gmail.com", "sneha.utekar@nmims.edu"));
				mailSender.sendEmail("SMS NOT SEND", "SMS Not Due to <br><br>" + result, recipent);
			}
		} catch (Exception e) {
			  
		}
	}

	@Async
	public void createRecordInUserMailTableAndMailTable(MailAcadsBean successfullMailList, String userId,
			String fromEmailID) {
		TimeTableDAO dao = (TimeTableDAO) act.getBean("timeTableDAO");
		long insertedMailId = dao.insertMailRecord(successfullMailList, userId);
		dao.insertUserMailRecord(successfullMailList, userId, fromEmailID, insertedMailId);

	}

	@RequestMapping(value = "/addFacultyLocation", method = { RequestMethod.GET, RequestMethod.POST })
	public ResponseEntity<HashMap<String,String>> addFacultyLocation(HttpServletRequest request, @RequestParam String pk, 
																		@RequestParam String value) {
		HttpHeaders headers = new HttpHeaders();
		headers.add("Content-Type", "application/json");
		HashMap<String,String> response = new HashMap<>();
		try {
			if (getLocationList().contains(value)) {
				String facultyParameter = (String) request.getParameter("AF");

				TimeTableDAO dao = (TimeTableDAO) act.getBean("timeTableDAO");
				SessionDayTimeAcadsBean session = dao.getSessionById(pk);
				int capacity = dao.getCapacityOfLocation(value);
				//Set session id blank for checking all location
				int occupied = dao.getNoOfSessionAtSameDateTimeLocation(session.getDate(), session.getStartTime(), value, "");
				int roomsAvailable = capacity - occupied;

				if (roomsAvailable > 0) {
					dao.updateSessionLocation(session.getId(), value, facultyParameter);
					response.put("status", "Success");
					response.put("message", "Faculty Location updated successfully");
					return new ResponseEntity<>(response,headers, HttpStatus.OK);
				} else {
					response.put("status", "Fail");
					response.put("message", "Location not available");
					return new ResponseEntity<>(response,headers, HttpStatus.OK);
				}
			}else {
				response.put("status", "Fail");
				response.put("message", "Invalid Location. Please check location name");
				return new ResponseEntity<>(response,headers, HttpStatus.OK);
			}
			
		} catch (Exception e) {
			  
			response.put("status", "Fail");
			response.put("message", "Error in adding Location");
			return new ResponseEntity<>(response,headers, HttpStatus.OK);
		}
	}

	public boolean checkMeeting(HttpServletRequest request, HttpServletResponse response, String hostId,
			String hostPass, String meetingKey) {

		try {
			SessionDayTimeAcadsBean session = conferenceBookingDAO.getSessionForRefresh(request.getParameter("id"));

			String successMessage = "";
			String errorMessage = "";

			if (meetingKey != null && (!"".equals(meetingKey.trim()))) {
				webExManager.checkValidSession(session, hostId, meetingKey, hostPass);
				if (!session.isErrorRecord()) {
					request.setAttribute("success", "true");
					successMessage = "Session available";
					request.setAttribute("successMessage", successMessage);
					return true;
				} else {
					errorMessage = "Session not available";
					request.setAttribute("error", "true");
					request.setAttribute("errorMessage", errorMessage);
					return false;

				}
			} else {
				return false;
			}
		} catch (Exception e) {
			  
			return false;

		}
	}

	public void deleteAndCreateNewMeetingKey(String id, String meetingKey, String hostId, String hostPass) {

		SessionDayTimeAcadsBean session = conferenceBookingDAO.getSessionForRefresh(id);
		String successMessage = "";
		String errorMessage = "";

		try {
			// Delete and create new one only if it is already created.

			if (hostId != null && (!"".equals(hostId.trim()))) {
				webExManager.deleteTrainingSessionKey(session, meetingKey, hostId, hostPass);
				if (!session.isErrorRecord()) {
					successMessage = "Earlier Session deleted successfully.";
				} else {
					errorMessage = "Old meeting not deleted from WebEx: " + session.getErrorMessage();
				}

				// Create a new WebEx meeting
				webExManager.scheduleTrainingSessionKey(session, hostId, hostPass);
				if (!session.isErrorRecord()) {
					successMessage = successMessage + "New Session created successfully in WebEx.";

					String updateKey = conferenceBookingDAO.updateWebExDetail(session, id, meetingKey, hostId);
					if (updateKey == null) {
						successMessage = successMessage + "New Session details saved in database successfully.";
					} else {
						errorMessage = "New Session not details saved in database";
					}
				} else {
					errorMessage = errorMessage + "New Session not created in Web Ex: " + session.getErrorMessage();
				}
			}
		} catch (Exception e) {
			  
		}
	}

	// Check meeting key for batchjob i.e. validateUpcomingSessions
	public boolean checkValidMeetingKey(String id, String meetingKey, String hostId, String hostPass) {
		SessionDayTimeAcadsBean session = conferenceBookingDAO.getSessionForRefresh(id);

		String successMessage = "";
		String errorMessage = "";

		try {
			if (meetingKey != null && (!"".equals(meetingKey.trim()))) {
				webExManager.checkValidSession(session, hostId, meetingKey, hostPass);
				if (!session.isErrorRecord()) {
					successMessage = "Session available";
					return true;
				} else {
					errorMessage = "Session not available";
					return false;
				}
			}

		} catch (Exception e) {
			  
			return false;
		}

		return false;

	}

	// Added by Somesh on 29/08/2018 for checking Valid / invalid meeting key
	// @Scheduled(fixedDelay = 1000 * 60 * 60 * 24)
	public void validateUpcomingSessions() {

		if (!"tomcat4".equalsIgnoreCase(SERVER) || !"PROD".equalsIgnoreCase(ENVIRONMENT)) {
//			System.out.println("Not running validateUpcomingSessions since this is not tomcat4. This is " + SERVER);
			return;
		}

		MailSender mailSender = (MailSender) act.getBean("mailer");
		ArrayList<String> recipent = new ArrayList<String>(
				Arrays.asList("sneha.utekar@nmims.edu", "bageshree@nmims.edu"));

		ArrayList<SessionDayTimeAcadsBean> nextWeekSession = notificationDAO.getNextWeekSessions();
//		System.out.println("Next Week Sessions : " + nextWeekSession);
		if (nextWeekSession != null && nextWeekSession.size() > 0) {

			for (SessionDayTimeAcadsBean sessionBean : nextWeekSession) {

				String emailBodyForInvalid = "Found Invalid Meetingkey for Session Name : "
						+ sessionBean.getSessionName() + "<br>Subject 	: " + sessionBean.getSubject() + "<br>Date 	: "
						+ sessionBean.getDate() + "<br>Time 	: " + sessionBean.getStartTime()
						+ "<br>New Meetingkey has been updated in database.";

				String emailBodyForBlankMeetingKey = "Found Empty or Blank MeetingKey For Session Name : "
						+ sessionBean.getSessionName() + "<br>Subject 	: " + sessionBean.getSubject() + "<br>Date 	: "
						+ sessionBean.getDate() + "<br>Time 	: " + sessionBean.getStartTime() + "."
						+ "<br>Please Set WebEx MeetingKey for " + sessionBean.getSessionName() + " this session.";

//				System.out.println("Checking first meeting Key");
				if (!StringUtils.isBlank(sessionBean.getHostId())) {

					String sessionId = sessionBean.getId();
					String meetingKey = sessionBean.getMeetingKey();
					String hostId = sessionBean.getHostId();
					String hostPass = sessionBean.getHostPassword();

					if (StringUtils.isBlank(sessionBean.getMeetingPwd())) {
						conferenceBookingDAO.updateMeetingPwd(sessionBean, sessionId, hostId);
//						System.out.println("Meeeting password has been updated");
					}

					if (StringUtils.isBlank(meetingKey)) {
//						System.out.println("Meeting key is Empty or blank or null");
						mailSender.sendEmailForInvalidSession("Found Empty or Blank MeetingKey ",
								emailBodyForBlankMeetingKey, recipent);
					} else {

						if (checkValidMeetingKey(sessionId, meetingKey, hostId, hostPass)) {
//							System.out.println("Meeting key 1 is valid");
						} else {
//							System.out.println("Meeting key 1 not valid");
							deleteAndCreateNewMeetingKey(sessionId, meetingKey, hostId, hostPass);
							mailSender.sendEmailForInvalidSession("Found Invalid MeetingKey ", emailBodyForInvalid,
									recipent);
						}
					}

				} else {
//					System.out.println("Meeting Key not available");
					mailSender.sendEmailForInvalidSession("Meetingkey / HostID is Empty", emailBodyForBlankMeetingKey,
							recipent);
				}

//				System.out.println("Checking Alternate meeting Key");
				if (!StringUtils.isBlank(sessionBean.getAltHostId())) {

					String sessionId = sessionBean.getId();
					String meetingKey = sessionBean.getAltMeetingKey();
					String hostId = sessionBean.getAltHostId();
					String hostPass = sessionBean.getAltHostPassword();

					if (StringUtils.isBlank(sessionBean.getAltMeetingPwd())) {
						conferenceBookingDAO.updateMeetingPwd(sessionBean, sessionId, hostId);
//						System.out.println("Alternate Meeeting password has been updated");
					}

					if (StringUtils.isBlank(meetingKey)) {
//						System.out.println("Meeting key is Empty or blank or null");
						mailSender.sendEmailForInvalidSession("Found Empty or Blank Parallel 1 Meetingkey",
								emailBodyForBlankMeetingKey, recipent);
					} else {

						if (checkValidMeetingKey(sessionId, meetingKey, hostId, hostPass)) {
//							System.out.println("Alternate Meeting key is valid");
						} else {
//							System.out.println("Alternate Meeting key not valid");
							deleteAndCreateNewMeetingKey(sessionId, meetingKey, hostId, hostPass);
							mailSender.sendEmailForInvalidSession("Found Invalid Parallel 1 MeetingKey",
									emailBodyForInvalid, recipent);
						}
					}
 
				} else {
//					System.out.println("Parallel meeting 1 does not exist");
				}

//				System.out.println("Checking Alternate2 meeting Key");
				if (!StringUtils.isBlank(sessionBean.getAltHostId2())) {

					String sessionId = sessionBean.getId();
					String meetingKey = sessionBean.getAltMeetingKey2();
					String hostId = sessionBean.getAltHostId2();
					String hostPass = sessionBean.getAltHostPassword2();

					if (StringUtils.isBlank(sessionBean.getAltMeetingPwd2())) {
						conferenceBookingDAO.updateMeetingPwd(sessionBean, sessionId, hostId);
//						System.out.println("Alternate2 Meeeting password has been updated");
					}

					if (StringUtils.isBlank(meetingKey)) {
//						System.out.println("Meeting key is Empty or blank or null");
						mailSender.sendEmailForInvalidSession("Found Empty or Blank Parallel 2 Meetingkey ",
								emailBodyForBlankMeetingKey, recipent);
					} else {

						if (checkValidMeetingKey(sessionId, meetingKey, hostId, hostPass)) {
//							System.out.println("Alternate2 Meeting key is valid");
						} else {
//							System.out.println("Alternate2 Meeting key not valid");
							deleteAndCreateNewMeetingKey(sessionId, meetingKey, hostId, hostPass);
							mailSender.sendEmailForInvalidSession("Found Invalid Parallel 2 MeetingKey ",
									emailBodyForInvalid, recipent);
						}
					}

				} else {
//					System.out.println("Parallel meeting 2 does not exist");
				}

//				System.out.println("Checking Alternate 3 meeting Key");
				if (!StringUtils.isBlank(sessionBean.getAltHostId3())) {
					String sessionId = sessionBean.getId();
					String meetingKey = sessionBean.getAltMeetingKey3();
					String hostId = sessionBean.getAltHostId3();
					String hostPass = sessionBean.getAltHostPassword3();

					if (StringUtils.isBlank(sessionBean.getAltMeetingPwd3())) {
						conferenceBookingDAO.updateMeetingPwd(sessionBean, sessionId, hostId);
//						System.out.println("Alternate3 Meeeting password has been updated");
					}

					if (StringUtils.isBlank(meetingKey)) {
//						System.out.println("Meeting key is Empty or blank or null");
						mailSender.sendEmailForInvalidSession("Found Empty or Blank Parallel 3 Meetingkey",
								emailBodyForBlankMeetingKey, recipent);
					} else {
						if (checkValidMeetingKey(sessionId, meetingKey, hostId, hostPass)) {
//							System.out.println("Alternate3 Meeting key is valid");
						} else {
//							System.out.println("Alternate3  key not valid");
							deleteAndCreateNewMeetingKey(sessionId, meetingKey, hostId, hostPass);
							mailSender.sendEmailForInvalidSession("Found Invalid Parallel 3 MeetingKey",
									emailBodyForInvalid, recipent);
						}
					}

				} else {
//					System.out.println("Parallel meeting 3 does not exist");
				}
			}
		}
//		System.out.println("End : validateUpcomingSessions");
	}

	/*
	 * Was written for corrections in hostid allocation, not in use now kept for
	 * reference
	 *
	 */
	@RequestMapping(value = "/updateAllWrongHostIdNPass", method = RequestMethod.GET)
	public void updateAllWrongHostIdNPass() {
		TimeTableDAO dao = (TimeTableDAO) act.getBean("timeTableDAO");
		ArrayList<SessionDayTimeAcadsBean> sessions = dao.getSessionAfterDate("SAS");

		for (SessionDayTimeAcadsBean s : sessions) {
			String oldHostId = s.getHostId();
			s.setHostId("");
			s.setAltHostId("");
			s.setAltHostId2("");
			s.setAltHostId3("");
			s = allocateAvailableRoomForBatchUpload(s);
			s.setCiscoStatus("NB");
			dao.updateScheduledSessionForWrongHostid(s);

		}

	}
	/*
	 * @RequestMapping(value="/deleteWebexMeetingsFromADateToNow",
	 * method=RequestMethod.GET) public void deleteWebexMeetingsFromADateToNow() {
	 * TimeTableDAO dao = (TimeTableDAO)act.getBean("timeTableDAO");
	 * ArrayList<SessionDayTimeBean> sessions =
	 * dao.getSessionAfterDate("2018-09-08");
	 * 
	 * for(SessionDayTimeBean s : sessions) { try { String hostId = s.getHostId();
	 * if(hostId != null && (!"".equals(hostId.trim()))){
	 * webExManager.deleteTrainingSessionKey(s, s.getMeetingKey(), hostId,
	 * s.getHostPassword()); if(!s.isErrorRecord()){ String successMessage =
	 * "Earlier Session deleted successfully.";  
	 * } } catch (Exception e) { // TODO Auto-generated catch block
	 *    } }
	 * 
	 */

	/* Zoom Started */

	@RequestMapping(value = "/getZoomWebinars", method = RequestMethod.GET)
	public void getZoomWebinars() {

		try {
			zoomManger.getWebinar();
		} catch (IOException e) {
			  
		}
	}

	@RequestMapping(value = "/scheduleWebinar", method = { RequestMethod.GET, RequestMethod.POST })
	public void scheduleWebinar(HttpServletRequest request, HttpServletResponse respnse) {

		try {
			List<SessionDayTimeAcadsBean> pendingConferenceList = conferenceBookingDAO.getPendingWebinarList();
			ArrayList<SessionDayTimeAcadsBean> errorList = new ArrayList<>();
			ArrayList<SessionDayTimeAcadsBean> zoomCreatedSessionList = new ArrayList<>();
			if (pendingConferenceList != null && (!pendingConferenceList.isEmpty())) {
				for (SessionDayTimeAcadsBean zoomSessions : pendingConferenceList) {
					zoomManger.scheduleWebinarBatchJob(zoomSessions);

					if (zoomSessions.isErrorRecord()) {
						errorList.add(zoomSessions);
					} else {
						zoomCreatedSessionList.add(zoomSessions);
					}
				}

				if (!zoomCreatedSessionList.isEmpty()) {
					conferenceBookingDAO.updateBookedWebinars(zoomCreatedSessionList);
				}

				if (errorList.size() > 0) {
					request.setAttribute("error", "true");
					request.setAttribute("errorMessage",
							errorList.size() + " sessions were not created in Zoom due to error.");
				}

				if (zoomCreatedSessionList.size() > 0) {
					request.setAttribute("success", "true");
					request.setAttribute("successMessage", zoomCreatedSessionList.size() + " sessions out of "
							+ pendingConferenceList.size() + " created in Zoom successfully");
				}
			}

		} catch (Exception e) {
			  
		}

	}

	@RequestMapping(value = "/deleteZoomWebinars", method = RequestMethod.GET)
	public void deleteZoomWebinars() {

		String webinarID = null;
		SessionDayTimeAcadsBean session = null;

		try {
			zoomManger.deleteWebinar(session, webinarID);
		} catch (IOException e) {
			  
		}
	}

	@RequestMapping(value = "/validateUpcomimgWebinars", method = { RequestMethod.GET, RequestMethod.POST })
	public void validateUpcomimgWebinars() {

		ArrayList<SessionDayTimeAcadsBean> nextWeekSession = notificationDAO.getNextWeekWebinars();
		System.out.println("nextWeekSession "+nextWeekSession.size());
		ArrayList<String> errorList = new ArrayList<String>();
		for (SessionDayTimeAcadsBean zoomWebinar : nextWeekSession) {

			if (!StringUtils.isBlank(zoomWebinar.getHostId())) {
				String sessionId = zoomWebinar.getId();
				String webinarID = zoomWebinar.getMeetingKey();
				String hostId = zoomWebinar.getHostId();

				if (checkValidWebinarID(sessionId, webinarID, hostId)) {
				} else {
					errorList.add(sessionId);
					//deleteAndCreateNewWebinarKey(sessionId, webinarID, hostId);
				}
			}
		}
		System.out.println("errorList "+errorList);
	}

	public boolean checkValidWebinarID(String id, String webinarID, String hostId) {

		SessionDayTimeAcadsBean session = conferenceBookingDAO.getSessionForCheck(id);
		String successMessage = "";
		String errorMessage = "";

		try {
			if (webinarID != null && (!"".equals(webinarID.trim()))) {
				zoomManger.checkValidWebinar(session, hostId, webinarID);
				if (!session.isErrorRecord()) {
					successMessage = "Session available";
					return true;
				} else {
					errorMessage = "Session not available";
					return false;
				}
			}
		} catch (Exception e) {
			  
			return false;
		}

		return false;

	}

	public void deleteAndCreateNewWebinarKey(String sessionId, String webinarID, String hostId) {
		SessionDayTimeAcadsBean session = conferenceBookingDAO.getSessionForCheck(sessionId);
		String successMessage = "";
		String errorMessage = "";
		try {
			if (hostId != null && (!"".equals(hostId.trim()))) {
				// Delete old meeting from Zoom
				zoomManger.deleteWebinar(session, webinarID);
				if (!session.isErrorRecord()) {
					successMessage = "Earlier Session deleted successfully.";
				} else {
					errorMessage = "Webinar not deleted from Zoom : " + session.getErrorMessage();
				}
				// Create new webinar in Zoom
				zoomManger.scheduleWebinar(session, hostId);
				if (!session.isErrorRecord()) {
					successMessage = successMessage + "New Session created successfully in WebEx.";

					String updateKey = conferenceBookingDAO.updateWebinarDetails(session, sessionId, hostId);
					if (updateKey == null) {
						successMessage = successMessage + "New Session details saved in database successfully.";
					} else {
						errorMessage = "New Session not details saved in database";
					}
				} else {
					errorMessage = errorMessage + "New Session not created in Web Ex: " + session.getErrorMessage();
				}
			}
		} catch (Exception e) {
			  
		}
	}

	public void deleteAndCreateNewWebinar(HttpServletRequest request, HttpServletResponse response, String type) {
		try {
			SessionDayTimeAcadsBean session = conferenceBookingDAO.getWebinarForRefresh(request.getParameter("id"));

			String successMessage = "";
			String errorMessage = "";
			String oldMeetingKey = "";

			if ("0".equalsIgnoreCase(type)) {// Below 4 lines not needed, still added for consistency
				// Refreshing original session
				session.setHostId(session.getHostId());
				session.setHostPassword(session.getHostPassword());
				session.setMeetingKey(session.getMeetingKey());
				session.setMeetingPwd(session.getMeetingPwd());
				session.setHostKey(session.getHostKey());

				oldMeetingKey = session.getMeetingKey();

				if ("".equalsIgnoreCase(session.getFacultyLocation()) || session.getFacultyLocation() == null) {
					setError(request, "Session cannot be created. As Location is occupied ");
					return;
				}
			} else if ("1".equalsIgnoreCase(type)) {
				// Refreshing first parallel session
				session.setHostId(session.getAltHostId());
				session.setHostPassword(session.getAltHostPassword());
				session.setMeetingKey(session.getAltMeetingKey());
				session.setMeetingPwd(session.getAltMeetingPwd());
				session.setHostKey(session.getAltHostKey());

				oldMeetingKey = session.getAltMeetingKey();

				if ("".equalsIgnoreCase(session.getAltFacultyLocation()) || session.getAltFacultyLocation() == null) {
					setError(request, "Session cannot be created. As Location is occupied ");
					return;
				}
			} else if ("2".equalsIgnoreCase(type)) {
				// Refreshing second parallel session
				session.setHostId(session.getAltHostId2());
				session.setHostPassword(session.getAltHostPassword2());
				session.setMeetingKey(session.getAltMeetingKey2());
				session.setMeetingPwd(session.getAltMeetingPwd2());
				session.setHostKey(session.getAltHostKey2());

				oldMeetingKey = session.getAltMeetingKey2();

				if ("".equalsIgnoreCase(session.getAltFaculty2Location()) || session.getAltFaculty2Location() == null) {
					setError(request, "Session cannot be created. As Location is occupied ");
					return;
				}
			} else if ("3".equalsIgnoreCase(type)) {
				// Refreshing third parallel session
				session.setHostId(session.getAltHostId3());
				session.setHostPassword(session.getAltHostPassword3());
				session.setMeetingKey(session.getAltMeetingKey3());
				session.setMeetingPwd(session.getAltMeetingPwd3());
				session.setHostKey(session.getAltHostKey3());

				oldMeetingKey = session.getAltMeetingKey3();

				if ("".equalsIgnoreCase(session.getAltFaculty3Location()) || session.getAltFaculty3Location() == null) {
					setError(request, "Session cannot be created. As Location is occupied ");
					return;
				}
			} else {
				setError(request, "Session cannot be created.");
			}

			boolean canUpdate = updateStartTimeIfMeetingPastTime(session);
			if (!canUpdate) {
				setError(request, "Cannot update meeting. Please check if meeting is of past date");
				return;
			}

			// Delete and create new one only if it is already created.
			if (session.getMeetingKey() != null && (!"".equals(session.getMeetingKey().trim()))) {
				loggerForSessionScheduling.info("Meeting Key: "+session.getMeetingKey());
				zoomManger.deleteSession(session, session.getMeetingKey());
				if (!session.isErrorRecord()) {
					request.setAttribute("success", "true");
					successMessage = "Earlier Session deleted successfully<br>";
					request.setAttribute("successMessage", successMessage);
				} else {
					errorMessage = "Old meeting not deleted from Zoom: " + session.getErrorMessage() + " <br>";
					request.setAttribute("error", "true");
					request.setAttribute("errorMessage", errorMessage);
				}

				// Create a new Zoom meeting
				zoomManger.scheduleSessions(session);
				if (!session.isErrorRecord()) {
					request.setAttribute("success", "true");
					successMessage = successMessage + "New Session created successfully in Zoom.<br>";

					String dbUpdateError = conferenceBookingDAO.updateZoomDetails(session, type);
					conferenceBookingDAO.updateAttendanceForOldMeeting(session, oldMeetingKey);
					loggerForSessionScheduling.info("IN deleteAndCreateNewWebinar dbUpdateError: "+dbUpdateError);
					if (dbUpdateError == null) {
						successMessage = successMessage + "New Session details saved in database successfully.";
						request.setAttribute("successMessage", successMessage);
					} else {
						errorMessage = errorMessage + dbUpdateError;
						setError(request, errorMessage);
					}
				} else {
					errorMessage = errorMessage + "New Session not created in Zoom: " + session.getErrorMessage() + " <br>";
					request.setAttribute("errorMessage", errorMessage);
				}

			}

		} catch (Exception e) {
			loggerForSessionScheduling.info("Error in deleteAndCreateNewWebinar: "+e.getMessage());
			request.setAttribute("error", "true");
			request.setAttribute("errorMessage", "Error in refreshing session: " + e.getMessage());
		}
	}
	

	@RequestMapping(value = "/loginIntoZoom", method = { RequestMethod.GET, RequestMethod.POST })
	public ModelAndView joinSesssion(HttpServletRequest request) {
		String id = request.getParameter("id");
		String type = request.getParameter("type");
		
		TimeTableDAO dao = (TimeTableDAO) act.getBean("timeTableDAO");
		SessionDayTimeAcadsBean session_bean = dao.findScheduledSessionById(id);
		if (session_bean != null) {
			String start_webinar_url = "error";
			if ("0".equalsIgnoreCase(type)) {
				start_webinar_url = zoomManger.getStartWebinarLink(session_bean.getMeetingKey(), session_bean.getHostId());
			} else if ("1".equalsIgnoreCase(type)) {
				start_webinar_url = zoomManger.getStartWebinarLink(session_bean.getAltMeetingKey(), session_bean.getAltHostId());
			} else if ("2".equalsIgnoreCase(type)) {
				start_webinar_url = zoomManger.getStartWebinarLink(session_bean.getAltMeetingKey2(), session_bean.getAltHostId2());
			} else if ("3".equalsIgnoreCase(type)) {
				start_webinar_url = zoomManger.getStartWebinarLink(session_bean.getAltMeetingKey3(), session_bean.getAltHostId3());
			}
			
			if (!start_webinar_url.equals("error")) {
				return new ModelAndView("redirect:" + start_webinar_url);
			} else {
				ModelAndView model_n_view = new ModelAndView("viewScheduledSession");
				model_n_view.addObject("session", session_bean);
				request.setAttribute("error", "true");
				request.setAttribute("errorMessage", "Invalid Zoom User Token");
				return model_n_view;
			}
		} else {
			ModelAndView model_n_view = new ModelAndView("viewScheduledSession");
			request.setAttribute("error", "true");
			request.setAttribute("errorMessage", "Invalid Session ID");
			return model_n_view;
		}
	}

	@RequestMapping(value = "/mbaWXSessionSchedulingForm", method = { RequestMethod.GET })
	public ModelAndView mbaWXSessionSchedulingForm(HttpServletRequest request, HttpServletResponse response, Model m) {
		String moduleId = (String) request.getParameter("id");
		request.getSession().setAttribute("moduleId", moduleId);
		TimeTableDAO dao = (TimeTableDAO) act.getBean("timeTableDAO");
		String subject = dao.getSessionPlanSubjectFromModuleId(moduleId);
		SessionDayTimeAcadsBean session = new SessionDayTimeAcadsBean();
		m.addAttribute("session", session);
		m.addAttribute("yearList", ACAD_YEAR_LIST);
		m.addAttribute("moduleId", moduleId);
		m.addAttribute("locationList", getLocationList());
		m.addAttribute("sessionTypesMap", getSessionTypesMap());
		if (subject.equalsIgnoreCase("Orientation")) {
			m.addAttribute("programList", dao.getAllPrograms());
			m.addAttribute("semList", semList);
			m.addAttribute("consumerTypeList", getConsumerTypesList());
			return new ModelAndView("addCommonSession");
		} else {
			m.addAttribute("subjectList", getSubjectList());
			m.addAttribute("trackList", trackList);
			return new ModelAndView("addScheduledSession");
		}
	}

	@RequestMapping(value = "/setNewZoomHostId", method = { RequestMethod.GET })
	public ModelAndView setNewZoomHostId(HttpServletRequest request) {

		TimeTableDAO dao = (TimeTableDAO) act.getBean("timeTableDAO");
		String successMessage = "";
		String errorMessage = "";
		ArrayList<SessionDayTimeAcadsBean> sessionList = dao.getSessionFromNow();
		ArrayList<String> sem1SubjectList = dao.getAllSubjectsSemWise("1");
		ArrayList<SessionDayTimeAcadsBean> sem1ErrorList = new ArrayList<SessionDayTimeAcadsBean>();
		ArrayList<SessionDayTimeAcadsBean> sem2_3_4_ErrorList = new ArrayList<SessionDayTimeAcadsBean>();
		ArrayList<SessionDayTimeAcadsBean> zoomDeleteErrorList = new ArrayList<SessionDayTimeAcadsBean>();
		ArrayList<SessionDayTimeAcadsBean> zoomCreationErrorList = new ArrayList<SessionDayTimeAcadsBean>();
		ArrayList<SessionDayTimeAcadsBean> sessioInsertDBErrorList = new ArrayList<SessionDayTimeAcadsBean>();

		/*
		 * ArrayList<String> sem1SubjectList = new
		 * ArrayList<String>(Arrays.asList("Business Economics"
		 * ,"Financial Accounting & Analysis",
		 * "Information Systems for Managers","Management Theory and Practice"
		 * ,"Marketing Management","Organisational Behaviour" ));
		 */

		int sem1Subjects = 0;
		int subjectNotInSem1 = 0;

		for (SessionDayTimeAcadsBean session : sessionList) {

			// Delete Original Meetings For Zoom
			/*
			 * if (ENVIRONMENT.equalsIgnoreCase("PROD")){ try {
			 * zoomManger.deleteWebinar(session,session.getMeetingKey()); if
			 * (session.isErrorRecord()) { zoomDeleteErrorList.add(session); } } catch
			 * (Exception e) { zoomDeleteErrorList.add(session);   
			 * continue; } }
			 */

			if (sem1SubjectList.contains(session.getSubject()) && "N".equalsIgnoreCase(session.getHasModuleId())) {
				sem1Subjects++;
				session = allocateAvailableRoomManual(session, "1");
				if (session.isErrorRecord()) {
					sem1ErrorList.add(session);
					continue;
				}
			} else {
				subjectNotInSem1++;
				session = allocateAvailableRoomManual(session, "234");
				if (session.isErrorRecord()) {
					sem2_3_4_ErrorList.add(session);
					continue;
				}
			}

			// Create Meetings in Zoom
			if (ENVIRONMENT.equalsIgnoreCase("PROD")) {
				try {
					zoomManger.scheduleWebinarBatchJob(session);
				} catch (Exception e) {
					zoomCreationErrorList.add(session);
					  
					continue;
				}
			}

			if (!session.isErrorRecord()) {
				request.setAttribute("success", "true");
				successMessage = successMessage + "Session created successfully in Zoom.<br>";

				String dbUpdateError = conferenceBookingDAO.updateZoomHostKey(session);
				if (dbUpdateError == null) {
					successMessage = successMessage + "New Session details saved in database successfully.";
					request.setAttribute("successMessage", successMessage);
				} else {
					sessioInsertDBErrorList.add(session);
					errorMessage = errorMessage + dbUpdateError;
					setError(request, errorMessage);
				}

			} else {
				errorMessage = errorMessage + "Session not created in Zoom: " + session.getErrorMessage() + " <br>";
				setError(request, errorMessage);
			}

		}

		return null;

	}

	private SessionDayTimeAcadsBean allocateAvailableRoomManual(SessionDayTimeAcadsBean sessionBean, String sem) {

		TimeTableDAO dao = (TimeTableDAO) act.getBean("timeTableDAO");
		ArrayList<EndPointBean> endPointList = dao.getAvailableRoom(sessionBean);
		ArrayList<EndPointBean> endPointsForSem1 = new ArrayList<EndPointBean>();
		ArrayList<EndPointBean> endPointsForSem_2_3_4 = new ArrayList<EndPointBean>();

		if (endPointList.size() > 0 && endPointList != null) {

			if ("1".equalsIgnoreCase(sem)) {
				for (EndPointBean endPoint : endPointList) {
					if ("Zoom1@nmims.edu".equalsIgnoreCase(endPoint.getZoomUID()) || "Zoom2@nmims.edu".equalsIgnoreCase(endPoint.getZoomUID()) ||
							"Zoom3@nmims.edu".equalsIgnoreCase(endPoint.getZoomUID()) || "Zoom4@nmims.edu".equalsIgnoreCase(endPoint.getZoomUID())) {
						endPointsForSem1.add(endPoint);
					}
				}
				endPointList = endPointsForSem1;

				if (endPointList.size() > 0) {
					EndPointBean firstAvailableRoom = endPointList.get(0);
					sessionBean.setRoom(firstAvailableRoom.getName());
					sessionBean.setHostId(firstAvailableRoom.getHostId());
					sessionBean.setHostKey(firstAvailableRoom.getZoomUID());
					sessionBean.setHostPassword(firstAvailableRoom.getHostPassword());

				} else {
					sessionBean.setErrorRecord(true);
					sessionBean.setErrorMessage("<br> Room not Available on Date :" + sessionBean.getDate()
							+ " Start Time : " + sessionBean.getStartTime() + " For Semster 1 Subject "
							+ sessionBean.getSubject());
				}

			} else {
				for (EndPointBean endPoint : endPointList) {
					if ("Zoom1@nmims.edu".equalsIgnoreCase(endPoint.getZoomUID()) || "Zoom2@nmims.edu".equalsIgnoreCase(endPoint.getZoomUID()) ||
							"Zoom3@nmims.edu".equalsIgnoreCase(endPoint.getZoomUID()) || "Zoom4@nmims.edu".equalsIgnoreCase(endPoint.getZoomUID())) {
						endPointsForSem_2_3_4.add(endPoint);
					}
				}
				endPointList.removeAll(endPointsForSem_2_3_4);

				if (endPointList.size() > 0) {
					EndPointBean firstAvailableRoom = endPointList.get(0);
					sessionBean.setRoom(firstAvailableRoom.getName());
					sessionBean.setHostId(firstAvailableRoom.getHostId());
					sessionBean.setHostKey(firstAvailableRoom.getZoomUID());
					sessionBean.setHostPassword(firstAvailableRoom.getHostPassword());
				} else {
					sessionBean.setErrorRecord(true);
					sessionBean.setErrorMessage(
							"<br> Room not Available on Date :" + sessionBean.getDate() + " Start Time : "
									+ sessionBean.getStartTime() + " at " + sessionBean.getFacultyLocation());
				}
			}

		} else {
			sessionBean.setErrorRecord(true);
			sessionBean.setErrorMessage("<br>  Room not Available for Date :" + sessionBean.getDate());
		}

		return sessionBean;

	}

	private void loginFaculty(String userIdFromURL, HttpServletRequest request) {
		request.getSession().setAttribute("userId_acads", userIdFromURL);

		LDAPDao dao = (LDAPDao) act.getBean("ldapdao");
		PersonAcads person = dao.findPerson(userIdFromURL);
		// Person person = new Person();
		person.setUserId(userIdFromURL);
		request.getSession().setAttribute("user_acads", person);
		request.getSession().setAttribute("userId_acads", userIdFromURL);

	}
	
	/*
	 * Commented by Riya as already present in ZoomPollController
	 */
//	@RequestMapping(value = "/viewAddSessionPollsForm", method = { RequestMethod.GET, RequestMethod.POST })
//	public String getSessionPollsForm(HttpServletRequest request, HttpServletResponse response, @RequestParam String id,
//			Model m) {
//
//		String userIdEncrypted = request.getParameter("eid");
//		String userIdFromURL = null;
//		try {
//			if (userIdEncrypted != null) {
//				userIdFromURL = AESencrp.decrypt(userIdEncrypted);
//			}
//		} catch (Exception e) {
//			// TODO: handle exception
//		}
//
//		if (userIdFromURL != null) {
//			loginFaculty(userIdFromURL, request);
//		}
//
//		if (!checkSession(request, response)) {
//			return "login";
//		}
//		String userId = (String) request.getSession().getAttribute("userId_acads");
//
//		Person user = (Person) request.getSession().getAttribute("user_acads");
//
//		TimeTableDAO dao = (TimeTableDAO) act.getBean("timeTableDAO");
//		SessionDayTimeBean session = dao.findScheduledSessionById(id);
//
//		// List<WebinarPollsBean>
//		// webinarPollsBeanList=zoomManger.getWebinarPoll(session.getMeetingKey());
//		WebinarPollsListBean webinarPollsBeanList = zoomManger.getWebinarPolls(session.getMeetingKey());
//
//		m.addAttribute("session", session);
//		m.addAttribute("totalPolls", webinarPollsBeanList.getPolls());
//		m.addAttribute("sessionPoll", new WebinarPollsBean());
//		request.getSession().setAttribute("webinarId", session.getMeetingKey());
//		request.getSession().setAttribute("acadSessionId", id);
//		// request.getSession().setAttribute("webinarId",session.getMeetingKey());
//		m.addAttribute("totalPollsSize", webinarPollsBeanList.getPolls().size());
//
//		return "sessionPolls";
//	}

/*
	@RequestMapping(value = "/viewAddSessionPollsForm", method = { RequestMethod.GET, RequestMethod.POST })
	public String getSessionPollsForm(HttpServletRequest request, HttpServletResponse response, @RequestParam String id,
			Model m) {

		String userIdEncrypted = request.getParameter("eid");
		String userIdFromURL = null;
		try {
			if (userIdEncrypted != null) {
				userIdFromURL = AESencrp.decrypt(userIdEncrypted);
			}
		} catch (Exception e) {
			// TODO: handle exception
		}

		if (userIdFromURL != null) {
			loginFaculty(userIdFromURL, request);
		}

		if (!checkSession(request, response)) {
			return "login";
		}
		String userId = (String) request.getSession().getAttribute("userId_acads");

		PersonAcads user = (PersonAcads) request.getSession().getAttribute("user_acads");

		TimeTableDAO dao = (TimeTableDAO) act.getBean("timeTableDAO");
		SessionDayTimeAcadsBean session = dao.findScheduledSessionById(id);

		// List<WebinarPollsBean>
		// webinarPollsBeanList=zoomManger.getWebinarPoll(session.getMeetingKey());
		WebinarPollsListBean webinarPollsBeanList = zoomManger.getWebinarPolls(session.getMeetingKey());

		m.addAttribute("session", session);
		m.addAttribute("totalPolls", webinarPollsBeanList.getPolls());
		m.addAttribute("sessionPoll", new WebinarPollsBean());
		request.getSession().setAttribute("webinarId", session.getMeetingKey());
		request.getSession().setAttribute("acadSessionId", id);
		// request.getSession().setAttribute("webinarId",session.getMeetingKey());
		m.addAttribute("totalPollsSize", webinarPollsBeanList.getPolls().size());

		return "sessionPolls";
	}
	*/

	/*
	@RequestMapping(value = "/createWebinarPolls", method = { RequestMethod.GET, RequestMethod.POST })
	public String createWebinarPolls(HttpServletRequest request,
			@ModelAttribute("sessionPolls") WebinarPollsBean webinarPollsBean) throws IOException {
		String acadSessionId = null;
		JsonObject jsonObject = null;
		try {
			String webinarId = (String) request.getSession().getAttribute("webinarId");
			acadSessionId = (String) request.getSession().getAttribute("acadSessionId");

			List<WebinarPollsQuestionsBean> webinarPollsQuestionsBeanList = new ArrayList<WebinarPollsQuestionsBean>();

			if (!webinarPollsBean.getTitle().equals("") && !webinarPollsBean.getTitle().isEmpty()
					&& webinarPollsBean.getTitle() != null) {
				for (WebinarPollsQuestionsBean webinarPollsQuestionsBean : webinarPollsBean.getQuestions()) {
					List<String> answers = new ArrayList<>();
					if (!webinarPollsQuestionsBean.getName().equals("")
							&& !webinarPollsQuestionsBean.getName().isEmpty()
							&& webinarPollsQuestionsBean.getName() != null) {
						if (!webinarPollsQuestionsBean.getType().equals("")
								&& !webinarPollsQuestionsBean.getType().isEmpty()
								&& webinarPollsQuestionsBean.getType() != null) {
							int count = 0;
							for (int i = 0; i < webinarPollsQuestionsBean.getAnswers().size(); i++) {
								if (!webinarPollsQuestionsBean.getAnswers().get(i).equals("")
										&& webinarPollsQuestionsBean.getAnswers().get(i) != null
										&& !webinarPollsQuestionsBean.getAnswers().get(i).isEmpty()) {
									count++;
								}
							}
							if (count == 0 || count == 1) {
								continue;
							} else {
								for (String a : webinarPollsQuestionsBean.getAnswers()) {
									if (!a.equals("") && !a.isEmpty() && a != null) {
										answers.add(a);
									}
								}
								webinarPollsQuestionsBean.setAnswers(answers);
								webinarPollsQuestionsBeanList.add(webinarPollsQuestionsBean);
							}
						} else {
							continue;
						}
					} else {
						continue;
					}
				}

				webinarPollsBean.setQuestions(webinarPollsQuestionsBeanList);
				jsonObject = zoomManger.createWebinarPoll(webinarId, webinarPollsBean);

				if (!jsonObject.get("id").getAsString().equals("") && jsonObject.get("id").getAsString() != null
						&& !jsonObject.get("id").getAsString().isEmpty()) {
					request.setAttribute("success", "true");
					request.setAttribute("successMessage", "Successfully Session Poll is created.");
				} else {
					request.setAttribute("error", "true");
					request.setAttribute("errorMessage", "Failed to create session poll!");
				}

			} else {
				request.setAttribute("error", "true");
				request.setAttribute("errorMessage", "Failed to create session poll!");
			}

		} catch (Exception ex) {
			
            request.setAttribute("error", "true");
            request.setAttribute("errorMessage", "Failed to create session poll!");
		}

		return "forward:/viewAddSessionPollsForm?id=" + acadSessionId;
>>>>>>> a49ad524e5ae1c784d649a35b4b77b1879d6abe3
/*
	
//	@RequestMapping(value = "/createWebinarPolls", method = { RequestMethod.GET, RequestMethod.POST })
//	public String createWebinarPolls(HttpServletRequest request,
//			@ModelAttribute("sessionPolls") WebinarPollsBean webinarPollsBean) throws IOException {
//		String acadSessionId = null;
//		JsonObject jsonObject = null;
//		try {
//			String webinarId = (String) request.getSession().getAttribute("webinarId");
//			acadSessionId = (String) request.getSession().getAttribute("acadSessionId");
//
//
//			List<WebinarPollsQuestionsBean> webinarPollsQuestionsBeanList = new ArrayList<WebinarPollsQuestionsBean>();
//
//			if (!webinarPollsBean.getTitle().equals("") && !webinarPollsBean.getTitle().isEmpty()
//					&& webinarPollsBean.getTitle() != null) {
//				for (WebinarPollsQuestionsBean webinarPollsQuestionsBean : webinarPollsBean.getQuestions()) {
//					List<String> answers = new ArrayList<>();
//					if (!webinarPollsQuestionsBean.getName().equals("")
//							&& !webinarPollsQuestionsBean.getName().isEmpty()
//							&& webinarPollsQuestionsBean.getName() != null) {
//						if (!webinarPollsQuestionsBean.getType().equals("")
//								&& !webinarPollsQuestionsBean.getType().isEmpty()
//								&& webinarPollsQuestionsBean.getType() != null) {
//							int count = 0;
//							for (int i = 0; i < webinarPollsQuestionsBean.getAnswers().size(); i++) {
//								if (!webinarPollsQuestionsBean.getAnswers().get(i).equals("")
//										&& webinarPollsQuestionsBean.getAnswers().get(i) != null
//										&& !webinarPollsQuestionsBean.getAnswers().get(i).isEmpty()) {
//									count++;
//								}
//							}
//							if (count == 0 || count == 1) {
//								continue;
//							} else {
//								for (String a : webinarPollsQuestionsBean.getAnswers()) {
//									if (!a.equals("") && !a.isEmpty() && a != null) {
//										answers.add(a);
//									}
//								}
//								webinarPollsQuestionsBean.setAnswers(answers);
//								webinarPollsQuestionsBeanList.add(webinarPollsQuestionsBean);
//							}
//						} else {
//							continue;
//						}
//					} else {
//						continue;
//					}
//				}
//
//				webinarPollsBean.setQuestions(webinarPollsQuestionsBeanList);
//				jsonObject = zoomManger.createWebinarPoll(webinarId, webinarPollsBean);
//
//				if (!jsonObject.get("id").getAsString().equals("") && jsonObject.get("id").getAsString() != null
//						&& !jsonObject.get("id").getAsString().isEmpty()) {
//					request.setAttribute("success", "true");
//					request.setAttribute("successMessage", "Successfully Session Poll is created.");
//				} else {
//					request.setAttribute("error", "true");
//					request.setAttribute("errorMessage", "Failed to create session poll!");
//				}
//
//			} else {
//				request.setAttribute("error", "true");
//				request.setAttribute("errorMessage", "Failed to create session poll!");
//			}
//
//		} catch (Exception ex) {
//			
//            request.setAttribute("error", "true");
//            request.setAttribute("errorMessage", "Failed to create session poll!");
//		}
//
//		return "forward:/viewAddSessionPollsForm?id=" + acadSessionId;
//
//	}
*/
	@RequestMapping(value = "/cancelSessionReScheduling", method = RequestMethod.POST)
	public ModelAndView cancelSessionReScheduling(FileAcadsBean fileBean,  HttpServletRequest request, HttpServletResponse response, Model m) throws ParseException {
		
		if (!checkSession(request, response)) {
			return new ModelAndView("studentPortalRediret");
		}
		
		TimeTableDAO dao = (TimeTableDAO) act.getBean("timeTableDAO");
		SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
		ModelAndView modelnView = new ModelAndView("batchSessionSchedulingForm");
		
		ArrayList<SessionDayTimeAcadsBean> allSessionsForCurrentMonthYear = dao.getAllSession(fileBean);
		
		//Commented as show all session in report
		/*
		ArrayList<SessionDayTimeBean> newSessionList = new ArrayList<SessionDayTimeBean>();
		ArrayList<SessionDayTimeBean> allCancelledSession = dao.getAllCancelledSession(fileBean);
		
		for (SessionDayTimeBean session : allCancelledSession) {
			 Date sessionDate = formatter.parse(session.getDate());
			 GregorianCalendar cal = new GregorianCalendar();
			 cal.setTime(sessionDate);
			 cal.add(Calendar.DATE, 28);
			 String newDate = formatter.format(cal.getTime());
			 session.setDate(newDate);
			 newSessionList.add(session);
		}
		*/
		
		if (allSessionsForCurrentMonthYear.size() > 0) {
			modelnView.addObject("allCancelledSessionSize",allSessionsForCurrentMonthYear.size());
			request.setAttribute("success","true");
			request.setAttribute("successMessage","Report generated successfully. Please click link below to download excel.");
		}else {
			request.setAttribute("error", "true");
			request.setAttribute("errorMessage", "No records found.");
		}
	
		 request.getSession().setAttribute("allCancelledSession", allSessionsForCurrentMonthYear);
		 m.addAttribute("fileBean", fileBean);
		 m.addAttribute("session", new SessionDayTimeAcadsBean());
		 m.addAttribute("yearList", ACAD_YEAR_LIST);
		
		 return modelnView;
		
	}
	
	@RequestMapping(value = "/downloadCancelledSessionReport", method = RequestMethod.GET)
	public ModelAndView downloadCancelledSessionReport(HttpServletRequest request, HttpServletResponse response) {
		
		if(!checkSession(request, response)){
			return new ModelAndView("studentPortalRediret");
		}
		
		ArrayList<SessionDayTimeAcadsBean> allCancelledSession = (ArrayList<SessionDayTimeAcadsBean>) request.getSession().getAttribute("allCancelledSession");
		return new ModelAndView("cancelledSessionsExcelView", "allCancelledSession",allCancelledSession);
	}
	
	@RequestMapping(value = "/getProgramListByConsumerType", method = RequestMethod.POST)
	public ResponseEntity<LinkedHashMap<String, String>> getProgramListByConsumerType(@RequestBody SessionDayTimeAcadsBean sessionBean) {
		HttpHeaders headers = new HttpHeaders();
		headers.add("Content-Type", "application/json");
		try {
			String consumerType = sessionBean.getConsumerType();
			LinkedHashMap<String,String>programNameAndCodeMap = schedulingService.getProgramNameAndCodeMap(consumerType);
			return new ResponseEntity<>(programNameAndCodeMap, headers, HttpStatus.OK);
		}catch(Exception e) {
		    return new ResponseEntity<>(headers, HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}
	
	@RequestMapping(value = "/getCommonSessionProgramsList", method = {RequestMethod.POST})
	public ResponseEntity<ArrayList<SessionDayTimeAcadsBean>> getCommonSessionProgramsList(@RequestBody SessionDayTimeAcadsBean sessionBean) {
		
		TimeTableDAO dao = (TimeTableDAO)act.getBean("timeTableDAO");
		ArrayList<SessionDayTimeAcadsBean> response = new ArrayList<SessionDayTimeAcadsBean>();
		
		try {
			String acadDateFormat = ContentUtil.prepareAcadDateFormat(sessionBean.getMonth(), sessionBean.getYear());
			SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
			Date date = formatter.parse(acadDateFormat);
			Date historyDate=formatter.parse("2020-07-01");
			
			if (date.compareTo(historyDate) >= 0){
				response = dao.getCommonGroupProgramList(sessionBean);
			}else {
				response = dao.getCommonGroupProgramListFromHistory(sessionBean);
			}
		} catch (Exception e) {
			  
		}
	
		return new ResponseEntity<ArrayList<SessionDayTimeAcadsBean>>(response,HttpStatus.OK);
	}
	
	@RequestMapping(value = "/insertSessionSubjectMapping", method = { RequestMethod.GET, RequestMethod.POST })
	public String insertSessionSubjectMapping (HttpServletRequest request) {
		if(!"tomcat6".equalsIgnoreCase(SERVER)){
			System.out.println("Not running synchronizeRecordingUpload: scheduler since this is not tomcat6. This is "+SERVER);
			return null;
		}
		logger.info("-->Migration Starts");	
	
		String userId = (String) request.getSession().getAttribute("userId_acads");
		TimeTableDAO dao = (TimeTableDAO)act.getBean("timeTableDAO");
		int count = 1;
		
		List<String> failureSessionIds = new ArrayList<String>();
		ArrayList<SessionDayTimeAcadsBean> sessionList = dao.getAllScheduledSessions();
		
		logger.info("Total Sessions to be Migrated: "+sessionList.size());
		for (SessionDayTimeAcadsBean session : sessionList) {
			logger.info("Count " + count + "/" + sessionList.size());
			SessionDayTimeAcadsBean newSession = new SessionDayTimeAcadsBean();
			newSession.setSessionId(session.getId());
			newSession.setCreatedBy(userId);
			newSession.setLastModifiedBy(userId);
			
			List<Integer> consumerType = new ArrayList<Integer>();
			try {
				consumerType = dao.getConsumerType(session.getCorporateName());
			} catch (Exception e) {
				// TODO Auto-generated catch block
				  
				logger.info(session.toString());
				failureSessionIds.add(session.getId());
				count++;

				continue;
			}
			List<Integer> consumerProgramStructureIdList = new ArrayList<Integer>();
			try {
				consumerProgramStructureIdList = dao.getconsumerProgramStructureIdList(consumerType);
				
			} catch (Exception e) {
				// TODO Auto-generated catch block
			  
			logger.error(" "+e);
			logger.info(session.toString());
			failureSessionIds.add(session.getId());
			count++;
			continue;
			}
			
			if ("M.sc".equalsIgnoreCase(session.getCorporateName())){
				consumerProgramStructureIdList.removeAll(consumerProgramStructureIdList);
				consumerProgramStructureIdList.add(113);
			}else if ("Y".equalsIgnoreCase(session.getHasModuleId())) {
				consumerProgramStructureIdList.removeAll(consumerProgramStructureIdList);
				consumerProgramStructureIdList.add(111);
				consumerProgramStructureIdList.add(151);
			}else {
				consumerProgramStructureIdList.remove(new Integer(111)); // MBA-WX
				consumerProgramStructureIdList.remove(new Integer(151)); // MBA-WX
				consumerProgramStructureIdList.remove(new Integer(113)); // M.sc
				consumerProgramStructureIdList.remove(new Integer(119)); // MBA-X Jul-19
				consumerProgramStructureIdList.remove(new Integer(126)); // MBA-X Jul-20
				consumerProgramStructureIdList.remove(new Integer(127)); // B.com
				consumerProgramStructureIdList.remove(new Integer(128)); // BBA
				consumerProgramStructureIdList.remove(new Integer(154)); // PC-DS
				consumerProgramStructureIdList.remove(new Integer(155)); // PD-DS
				consumerProgramStructureIdList.remove(new Integer(158)); // M.Sc. (AI)
			}
			
			for (Integer consumerProgramStructureId : consumerProgramStructureIdList) {
				List<Integer> prgmSemSubjectIdList = new ArrayList<Integer>();
				try {
					prgmSemSubjectIdList = dao.getAllPSSIdBySubjectName(session.getSubject(),session.getHasModuleId(), consumerProgramStructureId);
				} catch (Exception e) {
					// TODO Auto-generated catch block
					  
					
					logger.error(" "+e);
					logger.info(session.toString());
					failureSessionIds.add(session.getId());
					
					continue;
				}
				for (Integer pssId : prgmSemSubjectIdList) {
					newSession.setPrgmSemSubId(pssId);
					newSession.setConsumerProgramStructureId(String.valueOf(consumerProgramStructureId));
					try {
						dao.insertSessionSubjectMapping(newSession);
					} catch (Exception e) {
						  
						logger.error(" "+e);
						logger.info(session.toString());
						failureSessionIds.add(session.getId());
						
						continue;
					}
				
				}
			}
			count++;
		}
		logger.info("Failure Session ID's" + failureSessionIds.toString());
		logger.info("Total Count" + count);
		return null  ;
	}
	
	@RequestMapping(value = "/getRecommendationSession", method = {RequestMethod.POST, RequestMethod.GET })
	public ModelAndView getRecommendationSession(@ModelAttribute SessionDayTimeAcadsBean sessionBean,HttpServletRequest request) {
		
		ModelAndView modelnView = new ModelAndView("addScheduledSession");
		TimeTableDAO dao = (TimeTableDAO) act.getBean("timeTableDAO");
		String viewMore = request.getParameter("viewMore") != null ? request.getParameter("viewMore") : "" ;
		String edit = (String) request.getSession().getAttribute("edit") ;
		String isEdit = edit != null ? edit : "false";
		ArrayList<SessionDayTimeAcadsBean> availableSolts = new ArrayList<SessionDayTimeAcadsBean>();
		ArrayList<SessionDayTimeAcadsBean> preAvailableSolts = new ArrayList<SessionDayTimeAcadsBean>();
		String sessionStartDate = sessionBean.getDate();
		String sessionEndDate = "";
		
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		Calendar c = Calendar.getInstance();
		try{
		   c.setTime(sdf.parse(sessionBean.getDate()));
		   c.add(Calendar.DAY_OF_MONTH, 7);
		   sessionEndDate = sdf.format(c.getTime());
		}catch(ParseException e){
			  
		}
		
		//Get previously search slots from session
		/*
		if (viewMore.equalsIgnoreCase("Y")) {
			preAvailableSolts = (ArrayList<SessionDayTimeBean>) request.getSession().getAttribute("availableSolts");
			if (preAvailableSolts.size() > 0) {
				sessionBean = preAvailableSolts.get(preAvailableSolts.size() - 1);
				availableSolts.addAll(preAvailableSolts);
			}
			sessionEndDate = getEndDateForCycle();
		}
		*/
		
		//Added To get Track wise date/day
		String sessionDays = "";
		if (sessionBean.getTrack().equalsIgnoreCase("Weekend Batch")) {
			sessionDays = "'Saturday','Sunday'";
		}else if (sessionBean.getTrack().equalsIgnoreCase("Weekend Batch - Fast Track") || sessionBean.getTrack().equalsIgnoreCase("Weekend Batch - Slow Track") ) {
			sessionDays = "'Saturday','Sunday'";
		}else if (sessionBean.getTrack().equalsIgnoreCase("WeekDay Batch")) {
			sessionDays = "'Monday','Tuesday','Wednesday','Thursday','Friday'";
		}else if(sessionBean.getTrack().equalsIgnoreCase("WeekDay Batch - Slow Track") || sessionBean.getTrack().equalsIgnoreCase("WeekDay Batch - Fast Track") ||
				sessionBean.getTrack().equalsIgnoreCase("Weekday Batch - Track 1") || sessionBean.getTrack().equalsIgnoreCase("Weekday Batch - Track 2")) {
			sessionDays = "'Monday','Tuesday','Wednesday','Thursday','Friday','Saturday','Sunday'";
		}else if(sessionBean.getTrack().equalsIgnoreCase("Weekend Slow - Track 1") || sessionBean.getTrack().equalsIgnoreCase("Weekend Slow - Track 3") || sessionBean.getTrack().equalsIgnoreCase("Weekend Fast - Track 4")) {
			sessionDays = "'Saturday','Sunday'";
		}else if(sessionBean.getTrack().equalsIgnoreCase("Weekday Slow - Track 2") || sessionBean.getTrack().equalsIgnoreCase("Weekday Fast - Track 5")) {
			sessionDays = "'Monday','Tuesday','Wednesday','Thursday','Friday'";
		}else if(sessionBean.getTrack().equalsIgnoreCase("Sem I - All Week - Track 5") || sessionBean.getTrack().equalsIgnoreCase("Sem II - All Week")) {
			sessionDays = "'Monday','Tuesday','Wednesday','Thursday','Friday','Saturday','Sunday'";
		}
		
		ArrayList<SessionDayTimeAcadsBean> activeDayTime = dao.getAllActiveDayTime(sessionBean, sessionDays, "Y");
		
		int count = 0;
		//Added to avoid get Recommendation on local as it takes long time
		if (ENVIRONMENT.equalsIgnoreCase("PROD") || ENVIRONMENT.equalsIgnoreCase("TEST")) {
			for (SessionDayTimeAcadsBean session : activeDayTime) {
				count++;
				session.setSubjectCode(sessionBean.getSubjectCode());
				session.setSubject(sessionBean.getSubject());
				session.setFacultyId(sessionBean.getFacultyId());
				session.setSessionName(sessionBean.getSessionName());
				session.setSessionType(sessionBean.getSessionType());
				session.setCorporateName(sessionBean.getCorporateName());
				session.setFacultyLocation(sessionBean.getFacultyLocation());
				session.setIsRecommendationSession("ByPass");
				session.setTrack(sessionBean.getTrack());
				session.setHasModuleId("N");
				session.setYear(sessionBean.getYear());
				session.setMonth(sessionBean.getMonth());
				
				String returnMessage = validityCheck_SetWebex_InsertToDb(session, request);
				if (returnMessage == null) {
					if (session.getErrorMessage() != null && !"".equalsIgnoreCase(session.getErrorMessage())) {
						continue;
					}else {
						availableSolts.add(session);
					}
				}
				if (availableSolts.size() == 5 && viewMore.equalsIgnoreCase("")) {
					break;
				}
//				Do Not check Limit when view more 
//				if (availableSolts.size() == 2 && viewMore.equalsIgnoreCase("Y")) {
//					break;
//				}
			}
		}
		
		request.getSession().setAttribute("availableSolts", availableSolts);
		modelnView.addObject("session", sessionBean);
		modelnView.addObject("availableSolts", availableSolts);
		modelnView.addObject("noOfAvailableSolts", availableSolts.size());
		modelnView.addObject("yearList", ACAD_YEAR_LIST);
		modelnView.addObject("subjectList", getSubjectList());
		modelnView.addObject("locationList", getLocationList());
		modelnView.addObject("trackList", trackList);
		modelnView.addObject("subjectCodeMap", getsubjectCodeMap());
		modelnView.addObject("sessionTypesMap", getSessionTypesMap());
		modelnView.addObject("masterKeysWithSubjectCodes", getMasterKeysWithSubjectCodes());
		request.getSession().setAttribute("edit", isEdit);
		request.setAttribute("sessionStartDate", sessionStartDate);
		request.setAttribute("sessionEndDate", sessionEndDate);
		
		return modelnView;
		
	}
	
//	@RequestMapping(value = "/getClashingSubjectList", method = RequestMethod.GET)
	public ArrayList<SessionDayTimeAcadsBean> getClashingSubjectList(SessionDayTimeAcadsBean session){
		
		TimeTableDAO dao = (TimeTableDAO)act.getBean("timeTableDAO");
		ArrayList<SessionDayTimeAcadsBean> sessionsClashList = new ArrayList<SessionDayTimeAcadsBean>();
		sessionsClashList = dao.getAllClashingSubjectsList(session);
		/*
		ArrayList<SessionDayTimeBean> allSessionOnSameTime = dao.getAllSessionOnSameDateTime(session);
		
		ArrayList<ProgramSubjectMappingBean> getAllApplicableConsumerIdsForCurrentSubject 
								= dao.getAllApplicableConsumerProgramStructureId(session.getSubject(), session.getHasModuleId(), session.getCorporateName());
		for (SessionDayTimeBean sessionBean : allSessionOnSameTime) {
			String consumerProgramStructureId = "";
			ArrayList<String> clashingList = new ArrayList<String>();
			ArrayList<ProgramSubjectMappingBean> getAllApplicableConsumerIdsForSearchSubject 
								= dao.getAllApplicableConsumerProgramStructureId(sessionBean.getSubject(), sessionBean.getHasModuleId(), session.getCorporateName());
			
			for (ProgramSubjectMappingBean currentSubject : getAllApplicableConsumerIdsForCurrentSubject) {
				for (ProgramSubjectMappingBean searchSubject : getAllApplicableConsumerIdsForSearchSubject) {
					if (currentSubject.getConsumerProgramStructureId() == searchSubject.getConsumerProgramStructureId() && currentSubject.getSem().equalsIgnoreCase(searchSubject.getSem())) {
						clashingList.add(searchSubject.getSubject());
						consumerProgramStructureId = consumerProgramStructureId + ","+ searchSubject.getConsumerProgramStructureId();
					}
				}
			}
			
			if (clashingList.size() > 0) {
				sessionBean.setConsumerProgramStructureId(consumerProgramStructureId);
				sessionsClashList.add(sessionBean);				
			}
		}
		*/

		return sessionsClashList;
	}
	
	public ArrayList<SessionDayTimeAcadsBean> get3SessionsClashingSubjectList(SessionDayTimeAcadsBean session){
		TimeTableDAO dao = (TimeTableDAO)act.getBean("timeTableDAO");
		ArrayList<SessionDayTimeAcadsBean> moreThan3SessionsClashList = new ArrayList<SessionDayTimeAcadsBean>();
		moreThan3SessionsClashList = dao.getMoreThan3ClashSessionsV3(session);
		return moreThan3SessionsClashList;
	}
	
	public String errorTableFormat(ArrayList<SessionDayTimeAcadsBean> moreThan3SessionClashList) {
		String msg= "";
		String table =  " <div class='panel-heading'>"+
				" 	<h4 class='panel-title'>" +
				" 		<a data-toggle='collapse' style='color: blue;' href='#moreThan3Session'>Click here to View Details</a>"+
				" 	</h4>"+
				" </div>" +
		
				" <div id='moreThan3Session' class='panel-collapse collapse'>" +
			    " 	<div class='panel-body'> " +
				"		<div class='table-responsive'>" +
				"			<table class='table table-striped'> " + 
				"  				<tr> " + 
				"    			<th>Subject</th> " + 
				"    			<th>Date</th> " + 
				"    			<th>Start Time</th> " + 
				"    			<th>Session Name</th> " + 
				"    			<th>Faculty Id</th> " + 
				"    			<th>Track</th> " + 
				"    			<th>Sem</th> " + 
				"  				</tr> " ;
				String uniqueSemSubject = "";
				for (SessionDayTimeAcadsBean bean : moreThan3SessionClashList) {
					if (!uniqueSemSubject.equalsIgnoreCase(bean.getUniqueSemSubject())) {
						uniqueSemSubject = bean.getUniqueSemSubject();
						msg+=" <tr> " + 
							 "	 <td colspan='9'>" +
							 " 	 	<div><b>"+bean.getProgram()+" - "+bean.getProgramStructure()+" - "+bean.getConsumerType()+"</b></div> " +	
							 " </tr> ";
					}
					
					msg+=" <tr> " + 
						 "    <td>"+bean.getSubject()+"</td> " + 
						 "    <td>"+bean.getDate()+"</td> " +
						 "    <td>"+bean.getStartTime()+"</td> " +
						 "    <td>"+bean.getSessionName()+"</td> " + 
						 "    <td>"+bean.getFacultyId()+"</td> " + 
						 "    <td>"+bean.getTrack()+"</td> " + 
						 "    <td>"+bean.getSem()+"</td> " +
						 " </tr> ";
				}
				table  = table + msg +
						"</table>" +
						"</div>" +
						"</div>" +
						"</div>";
				return table;
	}
	
	@RequestMapping(value = "/updateSingleScheduledSession", method = { RequestMethod.GET, RequestMethod.POST })
	public ModelAndView updateSingleScheduledSession(HttpServletRequest request, HttpServletResponse response,
			@ModelAttribute SessionDayTimeAcadsBean session) {
		
		TimeTableDAO dao = (TimeTableDAO) act.getBean("timeTableDAO");
		String userId = (String) request.getSession().getAttribute("userId_acads");
		String consumerProgramStructureId = request.getParameter("consumerProgramStructureId");
		SessionDayTimeAcadsBean sessionBeforeEdit = dao.getSessionById(session.getId());
		
		boolean dateTimeSubjectChanged = (!sessionBeforeEdit.getDate().equals(session.getDate()))
				|| (!sessionBeforeEdit.getStartTime().equals(session.getStartTime()))
				|| (!sessionBeforeEdit.getSubject().equals(session.getSubject()))
				|| (!sessionBeforeEdit.getSessionName().equals(session.getSessionName()))
				|| (!sessionBeforeEdit.getFacultyId().equals(session.getFacultyId()));
		
		try {
			if (dateTimeSubjectChanged) {
				
				//No recommendation on update
				session.setIsRecommendationSession("ByPass");
				session.setLastModifiedBy(userId);
				session.setConsumerProgramStructureId(consumerProgramStructureId);
				int prgmSemSubId = dao.getPSSid(session);
				
				if (prgmSemSubId == 0) {
					return sendToErrorPage(request, session, "Error in getting Program Program Sem Subject Id", "true");
				}else {
					session.setPrgmSemSubId(prgmSemSubId);
				}
				
				//Faculty Checks Start
				
				if (!getFacultyList().contains(session.getFacultyId())) {
					return sendToErrorPage(request, session, "Invalid Faculty ID:" + session.getFacultyId(), "true");
				}
				
				boolean isFacultyAvailable = dao.isFacultyAvailable(session);
				if (!isFacultyAvailable) {
					return sendToErrorPage(request, session, "Faculty is NOT available on "+session.getDate()+" at "+session.getStartTime(),"true");
				}
				
				boolean isFacultyFree = dao.isFacultyFreeAllChecksV2(session);
				if (!isFacultyFree) {
					ArrayList<SessionDayTimeAcadsBean> facultySession = dao.getFacultyClashDeatils(session);
					String msg = "";
					for (SessionDayTimeAcadsBean bean : facultySession) {
						msg = msg + "Faculty is occupied taking "+bean.getSessionName()+" of Subject : "+bean.getSubject()+
								 	" of "+bean.getTrack()+" on " + bean.getDate() + " at " + bean.getStartTime()+ " /1<br>";
					}
					return sendToErrorPage(request, session,msg,"true");
				}
				
				boolean isFacultyTakingLessThan2SubjectsSameDay = dao.isFacultyTakingLessThan2SubjectsSameDay(session);
				if (!isFacultyTakingLessThan2SubjectsSameDay) {
					ArrayList<SessionDayTimeAcadsBean> facultySessions = dao.getSameDaySessionsForFaculty(session);
					String msg = "Faculty " + session.getFacultyId() + " is already taking 2 sessions on "+ session.getDate()+ "<br>";
					int count = 1;
					for (SessionDayTimeAcadsBean bean : facultySessions) {
						msg = msg + count +". Subject : "+bean.getSubject()+ " on "+bean.getDate()
								 +" at "+bean.getStartTime()+ " of Track : "+bean.getTrack()+ "<br>";
						count++;
					}
					return sendToErrorPage(request, session, msg, "true");
				}
				
				//Faculty Checks End
				
				boolean isDateTimeValid = dao.isDateTimeValid(session);
				if (!isDateTimeValid) {
					return sendToErrorPage(request, session, "Date " + session.getDate() + " and Time "
							+ session.getStartTime() + " is not valid as per agreed days and time slot OR not within Academic Calendar dates.","true");
				}
				
				boolean isNotHoliday = dao.isNotHoliday(session);
				if (!isNotHoliday) {
					return sendToErrorPage(request, session, session.getDate() + " is a Holiday ", "true");
				}

				if ((getSemSubjectNProgramSubjectBeanMap().containsKey("1-" + session.getSubject())
						|| getSemSubjectNProgramSubjectBeanMap().containsKey("2-" + session.getSubject()))
						&& !StringUtils.isBlank(session.getTrack())) {
					
					boolean isNotMoreThanLimitSubjectsSameDayByProgSemTrackNew = dao.isNotMoreThanLimitSubjectsSameDayByProgSemTrackNew(session, "Y");
					if (isNotMoreThanLimitSubjectsSameDayByProgSemTrackNew) {
						ArrayList<SessionDayTimeAcadsBean>moreThan3SessionClashList = get3SessionsClashingSubjectList(session);
						String table =  errorTableFormat(moreThan3SessionClashList);
						if (!session.getIsRecommendationSession().equalsIgnoreCase("ByPass") || session.getIsRecommendationSession().equalsIgnoreCase("N")){
							getRecommendationSession(session, request);
						}
						return sendToErrorPage(request, session, "Already 3 sessions of Program Semester Track going on " + session.getDate()
										+ " of track " + session.getTrack()+ "<br>"+table,"true");
					}
					
					boolean isNoSubjectClashing = dao.isNoSubjectClashingNew(session);
					if(!isNoSubjectClashing){
						ArrayList<SessionDayTimeAcadsBean> clashingList = getClashingSubjectList(session);
						
						String specializationTypeOfSessionToBeScheduled = dao.getspecializationTypeBySubjectCode(session.getSubjectCode());
						String msg = checkClashingOfSubjectCodes(clashingList, specializationTypeOfSessionToBeScheduled, session);
						
						if (!StringUtils.isBlank(msg)) {
							return sendToErrorPage(request, session, msg, "true");
						}
					}
					
				} else {
					
					boolean isNoSubjectClashing = dao.isNoSubjectClashingNew(session);
					if(!isNoSubjectClashing){
						ArrayList<SessionDayTimeAcadsBean> clashingList = getClashingSubjectList(session);
						
						String specializationTypeOfSessionToBeScheduled = dao.getspecializationTypeBySubjectCode(session.getSubjectCode());
						String msg = checkClashingOfSubjectCodes(clashingList, specializationTypeOfSessionToBeScheduled, session);
						
						if (!StringUtils.isBlank(msg)) {
							return sendToErrorPage(request, session, msg, "true");
						}
					}
				}
				
				//Set up zoom ID/Password/Room for new date and time
				session = allocateAvailableRoomForBatchUpload(session);
				
				if (session.getErrorMessage() != null && !"".equals(session.getErrorMessage())) {
					return sendToErrorPage(request, session, session.getErrorMessage(), "true");
				}
				
				//Create session in zoom
				if (ENVIRONMENT.equalsIgnoreCase("PROD")) {
					zoomManger.scheduleWebinarBatchJob(session);
				}
				
				if (session.isErrorRecord()) {
					return sendToErrorPage(request, session, "Error in creating zoom", "true");
				}
				
				//Insert new session
				long sessionId = dao.insertNewSession(session, userId);
				if (sessionId == 0) {
					return sendToErrorPage(request, session, "Error in saving session to DB.", "true");
				}
				session.setSessionId(Long.toString(sessionId));
				
				int isdeleted = dao.deleteSessionSubjectMapping(session.getId(), consumerProgramStructureId);
				if (isdeleted != 0) {
					return sendToErrorPage(request, session, "Error in deleting old session mapping in DB.", "true");
				}
				int isMappingInserted = dao.insertSessionSubjectMapping(session);
				if (isMappingInserted != 0) {
					return sendToErrorPage(request, session, "Error while inserting session mapping in DB.", "true");
				}
				
				request.setAttribute("success", "true");
				request.setAttribute("successMessage", "Session Updated successfully");
				return searchScheduledSession(request, response, session);
				
				}else {
					return sendToErrorPage(request, session, "No changes found. Old and updated sessions details are same", "true");
				}
		} catch (Exception e) {
			  
			return sendToErrorPage(request, session, "Error in updating session", "true");
		}
	}
	
	@RequestMapping(value = "/testPageWidget", method = {RequestMethod.GET, RequestMethod.POST})
	public String testPageWidget(Model m, @ModelAttribute SessionDayTimeAcadsBean session) {
		
		TimeTableDAO dao = (TimeTableDAO) act.getBean("timeTableDAO");
		m.addAttribute("masterKeys", getMasterKeys());
		m.addAttribute("session", new SessionDayTimeAcadsBean());
		return"multipleMasterKeySelectWidget";
	}
	
	@RequestMapping(value = "/searchCommonSessionForm", method = { RequestMethod.GET, RequestMethod.POST })
	public String searchCommonSessionForm(HttpServletRequest request, HttpServletResponse respnse, Model m) {

		SessionDayTimeAcadsBean searchBean = new SessionDayTimeAcadsBean();
		m.addAttribute("searchBean", searchBean);
		m.addAttribute("yearList", ACAD_YEAR_LIST);
		m.addAttribute("subjectList", getSubjectList());
		m.addAttribute("locationList", getLocationList());
		return "searchCommonSessions";
	}
	
	@RequestMapping(value = "/searchCommonSession", method = { RequestMethod.GET, RequestMethod.POST })
	public ModelAndView searchCommonSession(HttpServletRequest request, @ModelAttribute SessionDayTimeAcadsBean searchBean) {

		ModelAndView modelnView = new ModelAndView("searchCommonSessions");
		request.getSession().setAttribute("commonSessionSearchBean", searchBean);
		
		TimeTableDAO dao = (TimeTableDAO)act.getBean("timeTableDAO");
		List<SessionDayTimeAcadsBean> scheduledSessionList = dao.searchCommonSession(searchBean);
		
		modelnView.addObject("searchBean", searchBean);
		modelnView.addObject("rowCount", scheduledSessionList.size());
		modelnView.addObject("yearList", ACAD_YEAR_LIST);
		modelnView.addObject("locationList", getLocationList());
		modelnView.addObject("subjectList", getSubjectList());
		
		if(scheduledSessionList == null || scheduledSessionList.size() == 0){
			request.setAttribute("error", "true");
			request.setAttribute("errorMessage", "No Records found.");
		}
		
		modelnView.addObject("scheduledSessionList",  scheduledSessionList);
		modelnView.addObject("mapOfFacultyIdAndFacultyRecord", mapOfFacultyIdAndFacultyRecord());
		request.getSession().setAttribute("mapOfFacultyIdAndFacultyRecord", mapOfFacultyIdAndFacultyRecord);
		
		return modelnView;
	}
	
	@RequestMapping(value = "/downloadCommonSessions", method = { RequestMethod.GET, RequestMethod.POST })
	public ModelAndView downloadCommonSessions(HttpServletRequest request, HttpServletResponse response) {
		SessionDayTimeAcadsBean searchBean = (SessionDayTimeAcadsBean) request.getSession().getAttribute("commonSessionSearchBean");

		TimeTableDAO dao = (TimeTableDAO) act.getBean("timeTableDAO");
		List<SessionDayTimeAcadsBean> scheduledSessionList = dao.searchCommonSession(searchBean);
		
		ArrayList<ProgramSubjectMappingAcadsBean> subjectProgramList = getSubjectProgramList();

		if (scheduledSessionList != null) {
			for (SessionDayTimeAcadsBean session : scheduledSessionList) {
				int group = 1;

				if ("Y".equalsIgnoreCase(session.getHasModuleId())) {
					ProgramSubjectMappingAcadsBean subjectProgramMappingForMBAWX = dao
							.getSubjectProgramListForMBAWX(session.getSubject(), session.getModuleId());
					session.setGroup1Sem(subjectProgramMappingForMBAWX.getSem());
					session.setGroup1Program(subjectProgramMappingForMBAWX.getPrgmStructApplicable() + " : "
							+ subjectProgramMappingForMBAWX.getProgram());
				} else {
					for (ProgramSubjectMappingAcadsBean programSubjectMappingBean : subjectProgramList) {
						if (session.getSubject().equals(programSubjectMappingBean.getSubject())) {
							if (group == 1) {
								session.setGroup1Sem(programSubjectMappingBean.getSem());
								session.setGroup1Program(programSubjectMappingBean.getPrgmStructApplicable() + ": "
										+ programSubjectMappingBean.getProgram());
								group++;
							} else if (group == 2) {
								session.setGroup2Sem(programSubjectMappingBean.getSem());
								session.setGroup2Program(programSubjectMappingBean.getPrgmStructApplicable() + ": "
										+ programSubjectMappingBean.getProgram());
								group++;
							} else if (group == 3) {
								session.setGroup3Sem(programSubjectMappingBean.getSem());
								session.setGroup3Program(programSubjectMappingBean.getPrgmStructApplicable() + ": "
										+ programSubjectMappingBean.getProgram());
								group++;
							} else if (group == 4) {
								session.setGroup4Sem(programSubjectMappingBean.getSem());
								session.setGroup4Program(programSubjectMappingBean.getPrgmStructApplicable() + ": "
										+ programSubjectMappingBean.getProgram());
								group++;
								break;
							}
						}
					}
				}
			}
		}
		return new ModelAndView("scheduledSessionExcelView", "scheduledSessionList", scheduledSessionList);
	}
	
	@RequestMapping(value = "/getConsumerProgramStructureDataBySubject", method = RequestMethod.POST)
	public ResponseEntity<List<SessionDayTimeAcadsBean>> getConsumerProgramStructureDataBySubject(@RequestBody SessionDayTimeAcadsBean session) {
		TimeTableDAO dao = (TimeTableDAO)act.getBean("timeTableDAO");
		HttpHeaders headers = new HttpHeaders();
		headers.add("Content-Type", "application/json");
		List<SessionDayTimeAcadsBean> masterKeyList = dao.getConsumerProgramStructureDataBySubject(session.getSubject());
		return new ResponseEntity<>(masterKeyList, headers, HttpStatus.OK);
	}
	
	@RequestMapping(value = "/deleteParallelSession", method = { RequestMethod.GET, RequestMethod.POST })
	public ModelAndView deleteParallelSession(HttpServletRequest request, HttpServletResponse response, Model m) throws Exception{
		
		String successMessage = "";
		String errorMessage = "";
		TimeTableDAO dao = (TimeTableDAO)act.getBean("timeTableDAO");
		
		String type = request.getParameter("type");
		String sessionId = request.getParameter("id");
		SessionDayTimeAcadsBean session = dao.getSessionById(sessionId);
		String meetingKey = "";
		switch (type) {
			case "1": meetingKey = session.getAltMeetingKey();	break;
			case "2": meetingKey = session.getAltMeetingKey2();	break;
			case "3": meetingKey = session.getAltMeetingKey3();	break;
		}
		
		//if Meeting Key is not created then removed faculty.
		if (!StringUtils.isBlank(meetingKey)) {
			if ("PROD".equalsIgnoreCase(ENVIRONMENT)) {
				zoomManger.deleteSession(session, meetingKey);
			}
		}

		if (!session.isErrorRecord()) {
			boolean isDeleted = dao.deleteParallelSession(sessionId, type);
			if (isDeleted) {
				successMessage = successMessage + "Parallel Session "+type+" deleted successfully.";
				request.setAttribute("successMessage", successMessage);
				request.setAttribute("success", "true");
			}else {
				errorMessage = errorMessage + "Error in DB While deleting Parallel Session "+type+".";
				request.setAttribute("errorMessage", errorMessage);
				request.setAttribute("error", "true");
			}
		}else {
			errorMessage = errorMessage + "Error in Zoom While deleting Parallel Session "+type+" , Meeting key : "+meetingKey;
			request.setAttribute("errorMessage", errorMessage);
			request.setAttribute("error", "true");
		}
		
		return viewScheduledSession(request, response);
	}

	@RequestMapping(value = "/getBatchUploadRecommendationSession", method = {RequestMethod.POST, RequestMethod.GET })
	public ModelAndView getBatchUploadRecommendationSession(FileAcadsBean fileBean,HttpServletRequest request, Model m, HttpServletResponse response) {
		
		if (!checkSession(request, response)) {
			return new ModelAndView("studentPortalRediret");
		}
		
		HashMap<Integer, ArrayList<SessionDayTimeAcadsBean>> batchUploadRecommendationList = new HashMap<Integer, ArrayList<SessionDayTimeAcadsBean>>();
		List<SessionDayTimeAcadsBean> inValidSessions = (List<SessionDayTimeAcadsBean>) request.getSession().getAttribute("inValidSessions");
		TimeTableDAO dao = (TimeTableDAO) act.getBean("timeTableDAO");
		int rowCount = 1;
		for (SessionDayTimeAcadsBean sessionBean : inValidSessions) {
			
			ArrayList<SessionDayTimeAcadsBean> availableSolts = new ArrayList<SessionDayTimeAcadsBean>();
			
			//Added To get Track wise date/day
			String sessionDays = "";
			if (sessionBean.getTrack().equalsIgnoreCase("Weekend Batch")) {
				sessionDays = "'Saturday','Sunday'";
			}else if (sessionBean.getTrack().equalsIgnoreCase("Weekend Batch - Fast Track") || sessionBean.getTrack().equalsIgnoreCase("Weekend Batch - Slow Track") ) {
				sessionDays = "'Saturday','Sunday'";
			}else if (sessionBean.getTrack().equalsIgnoreCase("WeekDay Batch")) {
				sessionDays = "'Monday','Tuesday','Wednesday','Thursday','Friday'";
			}else if(sessionBean.getTrack().equalsIgnoreCase("WeekDay Batch - Slow Track") || sessionBean.getTrack().equalsIgnoreCase("WeekDay Batch - Fast Track") ||
					sessionBean.getTrack().equalsIgnoreCase("Weekday Batch - Track 1") || sessionBean.getTrack().equalsIgnoreCase("Weekday Batch - Track 2")) {
				sessionDays = "'Monday','Tuesday','Wednesday','Thursday','Friday','Saturday','Sunday'";
			}else if(sessionBean.getTrack().equalsIgnoreCase("Weekend Slow - Track 1") || sessionBean.getTrack().equalsIgnoreCase("Weekend Slow - Track 3") || sessionBean.getTrack().equalsIgnoreCase("Weekend Fast - Track 4")) {
				sessionDays = "'Saturday','Sunday'";
			}else if(sessionBean.getTrack().equalsIgnoreCase("Weekday Slow - Track 2") || sessionBean.getTrack().equalsIgnoreCase("Weekday Fast - Track 5")) {
				sessionDays = "'Monday','Tuesday','Wednesday','Thursday','Friday'";
			}else if(sessionBean.getTrack().equalsIgnoreCase("Sem I - All Week - Track 5") || sessionBean.getTrack().equalsIgnoreCase("Sem II - All Week")) {
				sessionDays = "'Monday','Tuesday','Wednesday','Thursday','Friday','Saturday','Sunday'";
			}
			
			
			ArrayList<SessionDayTimeAcadsBean> activeDayTime = dao.getAllActiveDayTime(sessionBean, sessionDays, "Y");
			
			int count = 0;
			for (SessionDayTimeAcadsBean session : activeDayTime) {
				count++;
				session.setSubjectCode(sessionBean.getSubjectCode());
				session.setSubject(sessionBean.getSubject());
				session.setFacultyId(sessionBean.getFacultyId());
				session.setSessionName(sessionBean.getSessionName());
				session.setSessionType(sessionBean.getSessionType());
				session.setCorporateName(sessionBean.getCorporateName());
				session.setFacultyLocation(sessionBean.getFacultyLocation());
				session.setIsRecommendationSession("ByPass");
				session.setTrack(sessionBean.getTrack());
				session.setHasModuleId("N");
				session.setYear(sessionBean.getYear());
				session.setMonth(sessionBean.getMonth());
				
				String returnMessage = validityCheck_SetWebex_InsertToDb(session, request);
				if (returnMessage == null) {
					if (session.getErrorMessage() != null && !"".equalsIgnoreCase(session.getErrorMessage())) {
						continue;
					}else {
						availableSolts.add(session);
					}
				}
				//Do not check limit for batch recommendation 
				/*if (availableSolts.size() == 20) {
					break;
				}*/
			}
			
			batchUploadRecommendationList.put(rowCount, availableSolts);
			rowCount++;
		}
		return new ModelAndView("batchSessionRecommendationsExcelView", "batchUploadRecommendationList", batchUploadRecommendationList);
	}
	
	
	@RequestMapping(value = "/getSingleRecommendation", method = RequestMethod.POST)
	public ResponseEntity<ArrayList<SessionDayTimeAcadsBean>> getSingleRecommendation(@RequestBody SessionDayTimeAcadsBean sessionBean, HttpServletRequest request){
		
		TimeTableDAO dao = (TimeTableDAO) act.getBean("timeTableDAO");
		ArrayList<SessionDayTimeAcadsBean> availableSolts = new ArrayList<SessionDayTimeAcadsBean>();
		
		//Added To get Track wise date/day
		
		String sessionDays = "";
		if (sessionBean.getTrack().equalsIgnoreCase("Weekend Batch")) {
			sessionDays = "'Saturday','Sunday'";
		}else if (sessionBean.getTrack().equalsIgnoreCase("Weekend Batch - Fast Track") || sessionBean.getTrack().equalsIgnoreCase("Weekend Batch - Slow Track") ) {
			sessionDays = "'Saturday','Sunday'";
		}else if (sessionBean.getTrack().equalsIgnoreCase("WeekDay Batch")) {
			sessionDays = "'Monday','Tuesday','Wednesday','Thursday','Friday'";
		}else if(sessionBean.getTrack().equalsIgnoreCase("WeekDay Batch - Slow Track") || sessionBean.getTrack().equalsIgnoreCase("WeekDay Batch - Fast Track") ||
				sessionBean.getTrack().equalsIgnoreCase("Weekday Batch - Track 1") || sessionBean.getTrack().equalsIgnoreCase("Weekday Batch - Track 2")) {
			sessionDays = "'Monday','Tuesday','Wednesday','Thursday','Friday','Saturday','Sunday'";
		}else if(sessionBean.getTrack().equalsIgnoreCase("Weekend Slow - Track 1") || sessionBean.getTrack().equalsIgnoreCase("Weekend Slow - Track 3") || sessionBean.getTrack().equalsIgnoreCase("Weekend Fast - Track 4")) {
			sessionDays = "'Saturday','Sunday'";
		}else if(sessionBean.getTrack().equalsIgnoreCase("Weekday Slow - Track 2") || sessionBean.getTrack().equalsIgnoreCase("Weekday Fast - Track 5")) {
			sessionDays = "'Monday','Tuesday','Wednesday','Thursday','Friday'";
		}else if(sessionBean.getTrack().equalsIgnoreCase("Sem I - All Week - Track 5") || sessionBean.getTrack().equalsIgnoreCase("Sem II - All Week")) {
			sessionDays = "'Monday','Tuesday','Wednesday','Thursday','Friday','Saturday','Sunday'";
		}
		
		ArrayList<SessionDayTimeAcadsBean> activeDayTime = dao.getAllActiveDayTime(sessionBean, sessionDays, "Y");
		
		int count = 0;
		for (SessionDayTimeAcadsBean session : activeDayTime) {
			count++;
			session.setSubjectCode(sessionBean.getSubjectCode());
			session.setSubject(sessionBean.getSubject());
			session.setFacultyId(sessionBean.getFacultyId());
			session.setSessionName(sessionBean.getSessionName());
			session.setSessionType(sessionBean.getSessionType());
			session.setCorporateName(sessionBean.getCorporateName());
			session.setFacultyLocation(sessionBean.getFacultyLocation());
			session.setIsRecommendationSession("ByPass");
			session.setTrack(sessionBean.getTrack());
			session.setHasModuleId("N");
			session.setYear(sessionBean.getYear());
			session.setMonth(sessionBean.getMonth());
			session.setAltFacultyId(sessionBean.getAltFacultyId());
			session.setAltFacultyLocation(sessionBean.getAltFacultyLocation());
			session.setAltFacultyId2(sessionBean.getAltFacultyId2());
			session.setAltFaculty2Location(sessionBean.getAltFaculty2Location());
			session.setAltFacultyId3(sessionBean.getAltFacultyId3());
			session.setAltFaculty3Location(sessionBean.getAltFaculty3Location());

			String returnMessage = validityCheck_SetWebex_InsertToDb(session, request);
			if (returnMessage == null) {
				if (session.getErrorMessage() != null && !"".equalsIgnoreCase(session.getErrorMessage())) {
					continue;
				}else {
					availableSolts.add(session);
				}
			}
			if (availableSolts.size() == 50) {
				break;
			}
		}
		request.getSession().setAttribute("availableSolts", availableSolts);
		return new ResponseEntity(availableSolts, HttpStatus.OK);	
	}
	
	public String showRecommendation(SessionDayTimeAcadsBean session, int row) {
		String msg = "";
		msg =   "<button type='button' class='btn btn-warning btn-sm recommendation"+row+"'  data-toggle='collapse' data-target='#batchRecAccordion"+row+"'>Get Recommendation</button>"+
				"	<div id='batchRecAccordion"+row+"' class='collapse'>"+
				"		<table class='table Newtable"+row+"'> " +
				"			<thead>"+
				"				<tr>"+
				"					<th>Date</th>"+
				"					<th>Start Time</th>"+
				"					<th>Session Name</th>"+
				"					<th>Subject</th>"+
				"					<th>Faculty Id</th>"+
				"					<th>Track</th>"+
				"					<th>Subject Code</th>"+
				"				</tr>"+
				"			</thead>"+
				"			<tbody>  </tbody> "+
				"		</table>  " +
				"	</div>" +
				" <script src='https://ajax.googleapis.com/ajax/libs/jquery/1.7.1/jquery.min.js' type='text/javascript'></script>" +

				" <script> " +
				" $(document).ready (function() { " +
				"	$('.recommendation"+row+"').on('click', function(){ " +
				"	var hideCols = ['date','startTime','sessionName','subjectCode','subject','facultyId','track']; "+
				"		var body = { " +
				"			date : `"+session.getDate()+"`," +
				"			startTime : `" +session.getStartTime()+"`," +
				"			endTime : `" +session.getEndTime()+"`," +
				"			subjectCode : `" +session.getSubjectCode()+"`," +
				"			subject : `" +session.getSubject()+"`," +
				"			sessionName : `" +session.getSessionName()+"`," +
				"			sessionType : `" +session.getSessionType()+"`," +
				"			facultyId : `" +session.getFacultyId()+"`," +
				"			facultyLocation : `" +session.getFacultyLocation()+"`," +
				"			altFacultyId : `" +session.getAltFacultyId()+"`," +
				"			altFacultyLocation : `" +session.getAltFacultyLocation()+"`," +
				"			altFacultyId2 : `" +session.getAltFacultyId2()+"`," +
				"			altFaculty2Location : `" +session.getAltFaculty2Location()+"`," +
				"			altFacultyId3 : `" + session.getAltFacultyId3()+"`," +
				"			altFaculty3Location : `"+ session.getAltFaculty3Location()+"`," +
				"			corporateName : `" + session.getCorporateName()+"`," +
				"			track : `" + session.getTrack()+"`," +
				"			year : `" + session.getYear()+"`," +
				"			month : `" + session.getMonth()+"`," +
				"		}; " +
//				" console.debug(body); " +
				"		$.ajax({ " +
				"			type : 'POST', " +
				"			contentType : 'application/json'," +
				"			url : '/acads/admin/getSingleRecommendation'," +   
				"			data : JSON.stringify(body), " +
				"			beforeSend: function(){ " +
				"				$('#loader"+row+"').show(); " +
		   		"			}," +
		   		"			complete:function(data){ " +
				"				$('#loader"+row+"').hide(); " +
				"			}," +
				" 			success : function(data) { " +	
//				"				console.debug(data); " + 
				"				if(data.length){ " +
				"					$.each(data, function(key, value) { " +
				"					var rowNo = key;"+
				"					var tr = $('<tr />'); " +
				"					$.each(value, function(k, v) { " +
				"						if(hideCols.includes(k)){ " +
				"      						tr.append( " +
				"								$('<td />', { " +
			    "      								html: v " +
			    "      							})[0].outerHTML " +
			    "							); " +
			    "						}else{" +
			    "      						tr.append( " +
				"								$('<td />', { " +
			    "      								html: v " +
			    "      							}).hide()[0].outerHTML " +
			    "							); " +
			    "						} "+
			    "					$('.Newtable"+row+" tbody').append(tr) " +
			    " 				}); " +
			    
			    "				$('.Newtable"+row+"> tbody:last').append($(` " +
			    " 					<td><a id='submit' name='submit' class='btn btn-primary btn-sm' href='addScheduledSession?isBatchRecSession=Y&index=${key}' target='_blank'>Add Session</a></td>" +
				"				`)); " +
			    
				" 				}) "+
				" 			}else{ " +
				"				$('.Newtable"+row+" tbody').append(`<tr><td colspan='6' style='text-align: center;'>No Slots Available !!</td></tr>`) "+
				"			} "+
				"		 }, " +
				"		}); " +
				"	}); " +
				" });" +
				"</script> " +
				
				"<div class='text-center' id='loader"+row+"' style='display: none;'>" +
				"  <img src='https://i.gifer.com/ZZ5H.gif' width='32px' height='32px'>" +
				"</div>";
				
		return msg;
	}
		
	@RequestMapping(value = "/scheduledSessionRecommendationSession", method = { RequestMethod.GET, RequestMethod.POST })
	public ModelAndView scheduledSessionRecommendationSession(HttpServletRequest request, HttpServletResponse response) {
		
		ArrayList<SessionDayTimeAcadsBean> availableSolts = (ArrayList<SessionDayTimeAcadsBean>) request.getSession().getAttribute("singleSessionAvailableSlots");
		String rowId= request.getParameter("rowId");
		SessionDayTimeAcadsBean session = availableSolts.get(Integer.parseInt(rowId));
		return addScheduledSession(request, response, session);
	}
	
	@RequestMapping(value = "/deleteCommonSession", method = { RequestMethod.GET, RequestMethod.POST })
	public ModelAndView deleteCommonSession(HttpServletRequest request, HttpServletResponse response) {
		
		if (!checkSession(request, response)) {
			return new ModelAndView("studentPortalRediret");
		}
		SessionDayTimeAcadsBean searchBean =  (SessionDayTimeAcadsBean) request.getSession().getAttribute("commonSessionSearchBean");
		
		try {
			String id = request.getParameter("id");
			TimeTableDAO dao = (TimeTableDAO)act.getBean("timeTableDAO");
			
			if ("PROD".equalsIgnoreCase(ENVIRONMENT)) {
				deleteMeeting(request, response);
			}
			boolean sessionDeleted = dao.deleteCommonSession(id);
			if (!sessionDeleted) {
				request.setAttribute("error", "true");
				request.setAttribute("errorMessage", "Error while deleting Common Session.");
				return searchCommonSession(request, searchBean);
			}else {
				request.setAttribute("success","true");
				request.setAttribute("successMessage","Record deleted successfully from Database");
				return searchCommonSession(request, searchBean);
			}
			
		} catch (Exception e) {
			  
			request.setAttribute("error", "true");
			request.setAttribute("errorMessage", "Error in deleting Record.");
			return searchCommonSession(request, searchBean);
		}
	}
	
	@RequestMapping(value = "/viewScheduleSessionByPSSId", method = { RequestMethod.GET })
	public ModelAndView viewScheduleSessionByPSSId(HttpServletRequest request, HttpServletResponse response) {
		ModelAndView mv = new ModelAndView("viewScheduleSessionByPSSId");
		TimeTableDAO dao = (TimeTableDAO)act.getBean("timeTableDAO");
		try {
			String pssId = request.getParameter("pssId");
			String year = request.getParameter("year");
			String month = request.getParameter("month");
			String track = request.getParameter("track");
			List<SessionDayTimeAcadsBean> sessionDayTimeBeanList = dao.viewScheduleSessionByPSSId(pssId,year,month,track);
			mv.addObject("sessionDayTimeBeanList", sessionDayTimeBeanList);
			mv.addObject("year", year);
			mv.addObject("month", month);
			mv.addObject("subject", sessionDayTimeBeanList.get(0).getSubject());
		}
		catch (Exception e) {
			// TODO: handle exception
			request.setAttribute("error","true");
			request.setAttribute("errorMessage","Something went wrong with these link, please try again after sometime, Error: " + e.getMessage());
		}
		
		return mv;
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
			session.setSubject(session.getSubject());
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
		ProgramSubjectMappingAcadsBean commonProgramList = new ProgramSubjectMappingAcadsBean();
		//For Admin only
		if (student == null) {
			subjectProgramList = dao.getSubjectProgramListBySessionId(id);
			
			//Check only if Common Session
			if (!StringUtils.isBlank(session.getProgramList())) {
				commonProgramList = dao.getcommonProgramList(id);
			}
		}
		modelnView.addObject("commonProgramList", commonProgramList);
		modelnView.addObject("subjectProgramList", subjectProgramList);
		modelnView.addObject("SERVER_PATH", SERVER_PATH);
		//Added as Session Polls tab should be shown to main faculty not alternate faculty
		modelnView.addObject("nosessionPolls",session.getFacultyId().equalsIgnoreCase(userId));
		return modelnView;
	}
	
	@RequestMapping(value = "/autoSessionSchedulingForm", method = { RequestMethod.GET, RequestMethod.POST })
	public String autoSessionSchedulingForm(HttpServletRequest request, HttpServletResponse respnse, Model m) {

		SessionDayTimeAcadsBean searchBean = new SessionDayTimeAcadsBean();
		TimeTableDAO dao = (TimeTableDAO) act.getBean("timeTableDAO");
		FacultyDAO fDao = (FacultyDAO) act.getBean("facultyDAO");
				
		m.addAttribute("searchBean", searchBean);
		m.addAttribute("trackList", trackList);
		m.addAttribute("yearList", ACAD_YEAR_LIST);
		m.addAttribute("locationList", getLocationList());
		m.addAttribute("facultyIdMap", fDao.getFacultyMap());
		m.addAttribute("subjectCodeMap", getsubjectCodeMap());
		m.addAttribute("sessionTypesMap", getSessionTypesMap());
		m.addAttribute("sessionTimeList", dao.getAllSessionTime());
		
		return "autoSessionScheduling";
	}
	
	@RequestMapping(value = "/autoSessionSchedule", method = { RequestMethod.GET, RequestMethod.POST })
	public ModelAndView autoSessionSchedule(HttpServletRequest request, HttpServletResponse response, @ModelAttribute SessionDayTimeAcadsBean session) {
		ModelAndView modelnView = new ModelAndView("autoSessionScheduling");
		TimeTableDAO dao = (TimeTableDAO) act.getBean("timeTableDAO");
		FacultyDAO fDao = (FacultyDAO) act.getBean("facultyDAO");
		ArrayList<SessionDayTimeAcadsBean> availableSessionsList = new ArrayList<SessionDayTimeAcadsBean>();
		ArrayList<SessionDayTimeAcadsBean> sessionList = dao.getDateAndTimeForSesssionToBeSchedule(session);
		
		int count = 1;
		for (SessionDayTimeAcadsBean sessionBean : sessionList) {
			sessionBean.setSubjectCode(session.getSubjectCode());
			sessionBean.setSessionType(session.getSessionType());
			sessionBean.setSessionName("Session "+count);
			sessionBean.setFacultyId(session.getFacultyId());
			sessionBean.setTrack(session.getTrack());
			sessionBean.setFacultyLocation(session.getFacultyLocation());
			sessionBean.setCorporateName(session.getCorporateName());
			sessionBean.setIsRecommendationSession("ByPass");
			sessionBean.setHasModuleId("N");
			
			String returnMessage = validityCheck_SetWebex_InsertToDb(sessionBean, request);
			if (returnMessage == null) {
				if (sessionBean.getErrorMessage() != null && !"".equalsIgnoreCase(session.getErrorMessage())) {
					continue;
				}else {
					availableSessionsList.add(sessionBean);
					count++;
				}
			}
			
			if (availableSessionsList.size() == session.getSlots()) {
				break;
			}
		}
		//Again setting as 1 for next search
		count = 1;

		request.setAttribute("rowCount", availableSessionsList.size());
		modelnView.addObject("searchBean", session);
		modelnView.addObject("trackList", trackList);
		modelnView.addObject("yearList", ACAD_YEAR_LIST);
		modelnView.addObject("locationList", getLocationList());
		modelnView.addObject("facultyIdMap", fDao.getFacultyMap());
		modelnView.addObject("subjectCodeMap", getsubjectCodeMap());
		modelnView.addObject("sessionTypesMap", getSessionTypesMap());
		modelnView.addObject("sessionTimeList", dao.getAllSessionTime());
		modelnView.addObject("availableSessionsList", availableSessionsList);
		
		request.getSession().setAttribute("availableSessionsList", availableSessionsList);
		
		if(availableSessionsList == null || availableSessionsList.size() == 0){
			request.setAttribute("error", "true");
			request.setAttribute("errorMessage", "No records found for the given inputs.");
			return modelnView;
		}else {
			request.setAttribute("success", "true");
			request.setAttribute("successMessage", "Found "+availableSessionsList.size()+" records for the given inputs.");
		}
		
		return modelnView;
	}
	
	public String checkClashingOfSubjectCodes(ArrayList<SessionDayTimeAcadsBean> clashingList, String specializationTypeOfSessionToBeScheduled, SessionDayTimeAcadsBean session) {
		String msg = "";
		for (SessionDayTimeAcadsBean bean : clashingList) {
			String specializationTypeOfAlreadyScheduledSession = bean.getSpecializationType();
			
			/* 
			 * Logic to check elective subjects sessions
			 * 
			 * Specialization1 + Specialization2 = False
			 * Specialization2 + Specialization1 = False
			 * Specialization(Any) + Common = True
			 * Common + Specialization(Any) = True
			 * Specialization1 + Specialization1 = True
			 * Common + Common = True
			 * 
			*/
			
			
			if ( (
					(StringUtils.isBlank(specializationTypeOfSessionToBeScheduled)
					&& !StringUtils.isBlank(specializationTypeOfAlreadyScheduledSession))
				|| 
					(!StringUtils.isBlank(specializationTypeOfSessionToBeScheduled) 
					&& StringUtils.isBlank(specializationTypeOfAlreadyScheduledSession))
				)
				|| specializationTypeOfSessionToBeScheduled.equalsIgnoreCase(specializationTypeOfAlreadyScheduledSession)) {
				
				msg = msg + "Subject "+session.getSubject() + " clashing with <b>"+bean.getSubject()+"</b> subject of same sem by Track, Session Name : "+bean.getSessionName()
				+ ", Faculty : "+bean.getFacultyId()+" Track : "+bean.getTrack()+" on " + bean.getDate()+ " at "+bean.getStartTime()+" <br>";
			}
		}
		return msg;
	}
	
	@RequestMapping(value = "/downloadAutoSessionToBeSchedule", method = { RequestMethod.GET, RequestMethod.POST })
	public ModelAndView downloadAutoSessionToBeSchedule(HttpServletRequest request, HttpServletResponse response) {
		if (!checkSession(request, response)) {
			return new ModelAndView("studentPortalRediret");
		}
		ArrayList<SessionDayTimeAcadsBean> availableSessionsList = (ArrayList<SessionDayTimeAcadsBean>) request.getSession().getAttribute("availableSessionsList");
		return new ModelAndView("availableSessionSlotsExcelView", "availableSessionsList", availableSessionsList);
	}
	
	@RequestMapping(value = "/addAutoSession", method = RequestMethod.POST, consumes="application/json", produces="application/json")
	public ResponseEntity<HashMap<String, String>> addAutoSession(@RequestBody SessionDayTimeAcadsBean session, HttpServletRequest request){
		
		HttpHeaders headers = new HttpHeaders();
		headers.add("Content-Type", "application/json");
		HashMap<String, String> response = new  HashMap<String, String>();
		
		try {
			session.setIsRecommendationSession("N");
			session.setHasModuleId("N");
			
			String returnMessage = validityCheck_SetWebex_InsertToDb(session, request);
			if (returnMessage == null) {
				System.out.println("session.getErrorMessage() "+session.getErrorMessage());
				if (session.getErrorMessage() != null && !"".equalsIgnoreCase(session.getErrorMessage())) {
					response.put("status", "Fail");
					response.put("message", session.getErrorMessage());
					return new ResponseEntity<HashMap<String, String>>(response, headers, HttpStatus.OK);
				}
				response.put("status", "Success");
				response.put("message", "Session created successfully");
				return new ResponseEntity<HashMap<String, String>>(response, headers, HttpStatus.OK);
			}else {
				response.put("status", "Fail");
				response.put("message", returnMessage);
				return new ResponseEntity<HashMap<String, String>>(response, headers, HttpStatus.OK);
			}
		} catch (Exception e) {
			e.printStackTrace();
			response.put("Status", "Fail");
			response.put("message", "Error while Session set up "+e.getMessage());
		}
		return new ResponseEntity<HashMap<String, String>>(response, headers, HttpStatus.OK);
	}
	
	@RequestMapping(value = "/addAllAutoSession", method = { RequestMethod.GET, RequestMethod.POST })
	public ModelAndView addAllAutoSession(HttpServletRequest request, HttpServletResponse response, @ModelAttribute SessionDayTimeAcadsBean session) {
		
		if (!checkSession(request, response)) {
			return new ModelAndView("studentPortalRediret");
		}
		
		ModelAndView modelnView = new ModelAndView("autoSessionScheduling");
		TimeTableDAO dao = (TimeTableDAO) act.getBean("timeTableDAO");
		FacultyDAO fDao = (FacultyDAO) act.getBean("facultyDAO");
		ArrayList<SessionDayTimeAcadsBean> availableSessionsList = (ArrayList<SessionDayTimeAcadsBean>) request.getSession().getAttribute("availableSessionsList");
		List<String> sessionIdsList = session.getSessionList();
		int successCount = 0;
		String errorMessage = "";
		
		//If Sessions is not available in the session
		if (availableSessionsList == null || availableSessionsList.size() == 0) {
			errorMessage = "Unable to find session. Please try again!";
		}else {
			for (String string : sessionIdsList) {
				SessionDayTimeAcadsBean sessionBean = availableSessionsList.get(Integer.parseInt(string)-1);
				sessionBean.setIsRecommendationSession("N");
				sessionBean.setHasModuleId("N");
				String returnMessage = validityCheck_SetWebex_InsertToDb(sessionBean, request);
				if (returnMessage == null) {
					if (sessionBean.getErrorMessage() != null && !"".equalsIgnoreCase(sessionBean.getErrorMessage())) {
						errorMessage = errorMessage + "Error in Session "+sessionBean.getErrorMessage();
					}else {
						successCount++;
					}
				}else {
					errorMessage = errorMessage + "Error in Session :</br>"+returnMessage;
				}
			}
		}
		
		//Setting up empty list, as again same list showing after scheduling
		availableSessionsList = new ArrayList<SessionDayTimeAcadsBean>();
		
		request.setAttribute("rowCount", availableSessionsList.size());
		modelnView.addObject("searchBean", session);
		modelnView.addObject("trackList", trackList);
		modelnView.addObject("yearList", ACAD_YEAR_LIST);
		modelnView.addObject("locationList", getLocationList());
		modelnView.addObject("facultyIdMap", fDao.getFacultyMap());
		modelnView.addObject("subjectCodeMap", getsubjectCodeMap());
		modelnView.addObject("sessionTypesMap", getSessionTypesMap());
		modelnView.addObject("sessionTimeList", dao.getAllSessionTime());
		modelnView.addObject("availableSessionsList", availableSessionsList);
		
		if(!StringUtils.isBlank(errorMessage)){
			request.setAttribute("error", "true");
			request.setAttribute("errorMessage", errorMessage);
		}
				
		if(successCount > 0) {
			request.setAttribute("success", "true");
			request.setAttribute("successMessage", successCount+" records saved successfully.");
		}
		
		return modelnView;
	}
	
	@RequestMapping(value = "/bookTrainingSessionsNew", method = {RequestMethod.POST })
	public ModelAndView bookTrainingSessionsNew(HttpServletRequest request, HttpServletResponse response, Model m, @ModelAttribute SessionDayTimeAcadsBean sessionDayTimeAcadsBean) throws Exception {

		if (!checkSession(request, response)) {
			return new ModelAndView("studentPortalRediret");
		}
		List<String> sessionIdsList = new ArrayList<String>();
		sessionIdsList = sessionDayTimeAcadsBean.getSessionList();
		
		//Commented by Saurabh to created session for selective sessions
		//List<SessionDayTimeAcadsBean> pendingConferenceList = conferenceBookingDAO.getPendingConferenceList();
		List<SessionDayTimeAcadsBean> pendingConferenceList = new ArrayList<SessionDayTimeAcadsBean>();
		if(sessionIdsList != null && (!sessionIdsList.isEmpty())) {
			pendingConferenceList = conferenceBookingDAO.getAllSessionsToScheduleBySessionId(sessionIdsList);
		}else {
			request.setAttribute("error", "true");
			request.setAttribute("errorMessage", "Please select least 1 session to create zoom link.");
		}
		ArrayList<SessionDayTimeAcadsBean> errorList = new ArrayList<>();
		ArrayList<SessionDayTimeAcadsBean> webinarCreatedSessionList = new ArrayList<>();

		if (pendingConferenceList != null && (!pendingConferenceList.isEmpty())) {
			if (ENVIRONMENT.equalsIgnoreCase("PROD")){
				for (int i = 0; i < pendingConferenceList.size(); i++) {
					SessionDayTimeAcadsBean session = pendingConferenceList.get(i);
					
					zoomManger.scheduleSessions(session);
	
					if (session.isErrorRecord()) {
						errorList.add(session);
					} else {
						webinarCreatedSessionList.add(session);
					}
				}
				
				try {
					conferenceBookingDAO.updateBookedConference(webinarCreatedSessionList);
				} catch (Exception e) {
					  
					request.setAttribute("error", "true");
					request.setAttribute("errorMessage", webinarCreatedSessionList.size() + " sessions were created in Zoom. "
							+ "Error while saving into DB");
					return autoScheduleForm(request, response, m);
				}
	
				if (errorList.size() > 0) {
					request.setAttribute("error", "true");
					request.setAttribute("errorMessage", errorList.size() + " sessions were not created in Zoom due to error.");
				}
	
				if (webinarCreatedSessionList.size() > 0) {
					request.setAttribute("success", "true");
					request.setAttribute("successMessage", webinarCreatedSessionList.size() + " sessions out of "
							+ pendingConferenceList.size() + " created in Zoom successfully");
				}
			
			} else {
				int[] result = conferenceBookingDAO.updateBookedConference(pendingConferenceList);
				if(result.length != 0) { 
					request.setAttribute("error","true");
					request.setAttribute("errorMessage"," Sessions not created in Zoom as it not Prod"); 
				} 
			}
		}

		return autoScheduleForm(request, response, m);
	}
	

}