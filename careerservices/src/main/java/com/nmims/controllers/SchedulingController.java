package com.nmims.controllers;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

import com.google.gson.Gson;
import com.nmims.beans.EndPointBean;
import com.nmims.beans.FacultyCareerservicesBean;
import com.nmims.beans.FeatureTypes;
import com.nmims.beans.FileCareerservicesBean;
import com.nmims.beans.MailCareerservicesBean;
import com.nmims.beans.PageCareerservicesBean;
import com.nmims.beans.SessionDayTimeBean;
import com.nmims.beans.StudentCareerservicesBean;
import com.nmims.daos.FacultyDAO;
import com.nmims.daos.NotificationDAO;
import com.nmims.daos.SessionSchedulerDao;
import com.nmims.daos.SessionsDAO;
import com.nmims.helpers.DataValidationHelpers;
import com.nmims.helpers.ExcelHelper;
import com.nmims.helpers.MailSender;
import com.nmims.helpers.SMSSender;
import com.nmims.helpers.ZoomManager;


@Controller
public class SchedulingController {
	
	@Autowired(required=false)
	ApplicationContext act;
	
	@Value( "${SERVER}" )
	private String SERVER;

	@Value( "${ENVIRONMENT}" )
	private String ENVIRONMENT;
	

	@Autowired
	private NotificationDAO notificationDAO;
	
	@Autowired
	private ZoomManager zoomManger;

	@Autowired
	private SMSSender smsSender;
	
	@Autowired
	FacultyDAO facultyDAO;

	@Autowired
	private SessionsDAO sessionsDAO;

	private static final Logger logger = LoggerFactory.getLogger(SchedulingController.class);
 
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
	
	@Value("#{'${ACAD_YEAR_LIST}'.split(',')}")
	private List<String> ACAD_YEAR_LIST;
	
	private HashMap<String,FacultyCareerservicesBean> mapOfFacultyIdAndFacultyRecord = null;
	private ArrayList<String> subjectList = null;
	private ArrayList<String> locationList = null;
	private ArrayList<String> facultyList = null;
	private final int pageSize = 10;
	private List<String> semList = Arrays.asList("1","2","3","4");

	@RequestMapping(value = "/addScheduledSessionForm", method = {RequestMethod.GET, RequestMethod.POST})
	public ModelAndView addScheduledSessionForm(HttpServletRequest request, HttpServletResponse response, Model m){

		/*if(!checkSession(request, response)){
			return new ModelAndView("studentPortalRediret");
		}*/

		m.addAttribute("FacultiesInCS", facultyDAO.getAllSpeakerDetails());
		
		SessionDayTimeBean session = new SessionDayTimeBean();
		m.addAttribute("session", session);
		m.addAttribute("yearList", ACAD_YEAR_LIST);
		m.addAttribute("subjectList", getSubjectList());
		m.addAttribute("locationList", getLocationList());
		return new ModelAndView("admin/session/addScheduledSession");
	}
	
	@RequestMapping(value = "/addScheduledSession", method = {RequestMethod.GET, RequestMethod.POST})
	public ModelAndView addScheduledSession(HttpServletRequest request, HttpServletResponse response, @ModelAttribute SessionDayTimeBean session){
		
		/*if(!checkSession(request, response)){
			return new ModelAndView("studentPortalRediret");
		}*/

		try {
			String userId = (String)request.getSession().getAttribute("userId");
			String sessionValidityErrorMessage="";
			session.setLastModifiedBy(userId);
			session.setCreatedBy(userId);
			
			
			String returnMessage = allChecksForScheduleWebinar(session);
			if(returnMessage==null) {
				
				if(session.getErrorMessage() !=null && !"".equalsIgnoreCase(session.getErrorMessage()) ) {
					sessionValidityErrorMessage= sessionValidityErrorMessage+" "+session.getErrorMessage();
				}
			}else {
				sessionValidityErrorMessage= sessionValidityErrorMessage+" "+returnMessage;
				session.setErrorMessage(returnMessage);
				return sendToErrorPage(request, session, sessionValidityErrorMessage, "false");
			}
			
			request.setAttribute("success","true");
			request.setAttribute("successMessage","Session created successfully");
			return searchScheduledSession(request, response, session);
			
		} catch (Exception e) {
			logger.info("in SchedulingController class got exception : "+e.getMessage());
			return sendToErrorPage(request, session, e.getMessage(), "false");
		}
		
	}
	
	@RequestMapping(value = "/batchSessionSchedulingForm", method = RequestMethod.GET)
	public String batchSessionSchedulingForm(HttpServletRequest request, HttpServletResponse response, Model m) {
		
		/*if(!checkSession(request, response)){
			return "studentPortalRediret";
		}*/
		
		FileCareerservicesBean fileBean = new FileCareerservicesBean();
		SessionDayTimeBean session = new SessionDayTimeBean();
		m.addAttribute("fileBean",fileBean);
		m.addAttribute("session",session);
		m.addAttribute("yearList", ACAD_YEAR_LIST);

		return "admin/session/batchSessionSchedulingForm";
	}
	
	
	
	@SuppressWarnings({ "rawtypes", "unchecked" })
	@RequestMapping(value = "/batchSessionScheduling", method = RequestMethod.POST)
	public ModelAndView batchSessionScheduling(FileCareerservicesBean fileBean, BindingResult result,HttpServletRequest request, Model m, HttpServletResponse response){
		/*if(!checkSession(request, response)){
			return new ModelAndView("studentPortalRediret");
		}*/
		
		ModelAndView modelnView = new ModelAndView("admin/session/batchSessionSchedulingForm");
		
		try {
			String userId = (String)request.getSession().getAttribute("userId");
			ExcelHelper excelHelper = new ExcelHelper();
			
			SessionSchedulerDao sDao = (SessionSchedulerDao)act.getBean("sessionSchedulerDAO");
			List<String> locationList =  sDao.getListOfLocations();
			ArrayList<List> resultList = excelHelper.readBatchSessionScheduleExcel(fileBean, getFacultyList(), getSubjectList(), userId, locationList);
			
			List<SessionDayTimeBean> corporateSessionList = (ArrayList<SessionDayTimeBean>)resultList.get(0);
			List<SessionDayTimeBean> errorBeanList = (ArrayList<SessionDayTimeBean>)resultList.get(1);

			fileBean = new FileCareerservicesBean();
			m.addAttribute("fileBean",fileBean);
			m.addAttribute("yearList", ACAD_YEAR_LIST);

			if(errorBeanList.size() > 0){
				request.setAttribute("errorBeanList", errorBeanList);
				return modelnView;
			}
			
			//Check session validity start
			String sessionValidityErrorMessage="";
			List<SessionDayTimeBean> validSessions = new ArrayList<SessionDayTimeBean>();
			List<SessionDayTimeBean> inValidSessions = new ArrayList<SessionDayTimeBean>();
			String returnMessage=null;
			int row=2;
			for(SessionDayTimeBean sessionToCheck : corporateSessionList) {
				returnMessage=allChecksForScheduleWebinar(sessionToCheck);
				if(returnMessage==null) {
					validSessions.add(sessionToCheck);
					if(sessionToCheck.getErrorMessage() !=null && !"".equalsIgnoreCase(sessionToCheck.getErrorMessage()) ) {
						sessionValidityErrorMessage= sessionValidityErrorMessage+"<br> Row "+row+" : "+sessionToCheck.getErrorMessage();
						inValidSessions.add(sessionToCheck);
					}
				}else {
					sessionValidityErrorMessage= sessionValidityErrorMessage+"<br> Row "+row+" : "+returnMessage;
					sessionToCheck.setErrorMessage(returnMessage);
					inValidSessions.add(sessionToCheck);
				}
				row++;
			}
			//Check session validity end
			
			if(inValidSessions.size() == 0){
				request.setAttribute("success","true");
				request.setAttribute("successMessage",corporateSessionList.size() +" rows out of "+ corporateSessionList.size()+" inserted successfully.");
			}else{
				request.getSession().setAttribute("inValidSessions", inValidSessions);
				request.setAttribute("error", "true");
				request.setAttribute("errorMessage", inValidSessions.size() + " records were NOT inserted. <br>"
													+ " <br>" +sessionValidityErrorMessage);
			}
		} catch (Exception e) {
			logger.info("in SchedulingController class got exception : "+e.getMessage());
			request.setAttribute("error", "true");
			request.setAttribute("errorMessage", "Error in batch upload. Error: "+e.getMessage());
		}
		
		SessionDayTimeBean session = new SessionDayTimeBean();
		modelnView.addObject("fileBean",fileBean);
		modelnView.addObject("session",session);
		modelnView.addObject("yearList", ACAD_YEAR_LIST);
		return modelnView;
	}
	

	@RequestMapping(value = "/sessionCancellationForm", method = {RequestMethod.GET, RequestMethod.POST})
	public ModelAndView sessionCancellationForm(HttpServletRequest request, HttpServletResponse respnse)
	{
		ModelAndView modelAndView = new ModelAndView("admin/session/sessionCancellation");
		String id = request.getParameter("id");
		SessionDayTimeBean session = sessionsDAO.findScheduledSessionById(id);
		List<StudentCareerservicesBean> studentList = getRegisteredStudentForSubject(session);
		int noOfStudentRegisteredForSubject = studentList.size();
		request.getSession().setAttribute("noOfStudentRegisteredForSubject", noOfStudentRegisteredForSubject);
		modelAndView.addObject("session",session);
		modelAndView.addObject("noOfStudentRegisteredForSubject", noOfStudentRegisteredForSubject);
		return modelAndView;
	}
	
	@RequestMapping(value = "/sessionCancellation", method = {RequestMethod.GET, RequestMethod.POST})
	public ModelAndView sessionCancellation(HttpServletRequest request, HttpServletResponse respnse,@ModelAttribute SessionDayTimeBean session)
	{
		ModelAndView modelAndView = new ModelAndView("admin/session/searchScheduledSession");
		String userId = (String)request.getSession().getAttribute("userId");
		try{
			sessionsDAO.updateCancelledSession(session,userId);
			if("Y".equals(session.getIsCancelled())){
				DataValidationHelpers validationHelpers = new DataValidationHelpers();
				session = validationHelpers.addStartAndEndDate(session);
				sessionsDAO.createAnnouncement(session,userId);
				sendSessionCancellationEmailAndSMS(session,userId);
			}
			setSuccess(request, "Session Cancelled for Session Name : "+session.getSessionName());
		}catch(Exception e){
			setError(request, "Error Occurs while Cancelling Session :"+e.getMessage());
		}
		modelAndView.addObject("searchBean",new SessionDayTimeBean());
		modelAndView.addObject("yearList", ACAD_YEAR_LIST);
		modelAndView.addObject("subjectList", getSubjectList());
		modelAndView.addObject("session",session);
		return modelAndView;
	}
	
	// reschedule Cancelled Session
		@RequestMapping(value = "/reScheduleSession", method = {RequestMethod.GET, RequestMethod.POST})
		public ModelAndView reScheduleSession(HttpServletRequest request, HttpServletResponse response,@ModelAttribute SessionDayTimeBean session,Model m) throws Exception
		{
			ModelAndView modelAndView = new ModelAndView("sessionCancellation");
			modelAndView.addObject("searchBean",new SessionDayTimeBean());
			modelAndView.addObject("yearList", ACAD_YEAR_LIST);
			modelAndView.addObject("subjectList", getSubjectList());
			@SuppressWarnings("unused")
			long reScheduleSessionId ;
			modelAndView.addObject("session",session);
			
			String userId = (String)request.getSession().getAttribute("userId");
			
			SessionDayTimeBean reSchedulesession = sessionsDAO.findScheduledSessionById(session.getId());
			try{
				reSchedulesession.setIsCancelled("N");
				reSchedulesession.setDate(session.getReScheduleDate());
				reSchedulesession.setStartTime(session.getReScheduleStartTime());
				reSchedulesession.setEndTime(session.getReScheduleEndTime());
			    reScheduleSessionId = sessionsDAO.createReScheduleSession(reSchedulesession,userId);
			    setSuccess(request, "Session Re-Schedule for Session Name : "+session.getSessionName()); 
			    modelAndView.setViewName("searchScheduledSession");
				
			}catch(Exception e){
				setError(request, "Error Occurs while Re-scheduling Session :"+e.getMessage());
				modelAndView.setViewName("sessionCancellation");
			}
		    return modelAndView;
		}
	
	// sending SMS and Email to Cancelled Session Student 
		public void sendSessionCancellationEmailAndSMS(SessionDayTimeBean session, String userId)
		{
			List<StudentCareerservicesBean> studentList = getRegisteredStudentForSubject(session);
			List<String> toEmailIds = new ArrayList<String>();
			List<String> toSapIds = new ArrayList<String>();
			if(studentList != null && studentList.size() > 0){
				sendEmailsToStudent(session, studentList);
				sendSMSsToStudent(session, studentList);
				for(StudentCareerservicesBean bean : studentList){
					toEmailIds.add(bean.getEmailId());
					toSapIds.add(bean.getSapid());
				}
			}
			
			// create Copy of Mail in Student MyCommunication Table
			MailCareerservicesBean mailBean = new MailCareerservicesBean();
			mailBean.setBody(session.getCancellationEmailBody());
			mailBean.setMailIdRecipients(toEmailIds);
			mailBean.setSubject(session.getCancellationSubject());
			mailBean.setFromEmailId("NMIMS Global Access SCE");
			mailBean.setSapIdRecipients(toSapIds);
			createRecordInUserMailTableAndMailTable(mailBean,userId,"NMIMS Global Access SCE");
			
		}

		@Async
		public void createRecordInUserMailTableAndMailTable(MailCareerservicesBean successfullMailList,String userId,String fromEmailID){
			long insertedMailId = sessionsDAO.insertMailRecord(successfullMailList,userId);
			sessionsDAO.insertUserMailRecord(successfullMailList,userId,fromEmailID,insertedMailId);

		}

		// get No of Student Registered for Selected Subject
		public List<StudentCareerservicesBean> getRegisteredStudentForSubject(SessionDayTimeBean session)
		{
			List<StudentCareerservicesBean> students = sessionsDAO.getAllStudentsForSessionType(FeatureTypes.CAREER_FORUM);
			return students;
		}
		
		private void sendEmailsToStudent(SessionDayTimeBean session, List<StudentCareerservicesBean> studentList) {
			MailSender mailSender = (MailSender)act.getBean("mailer");
			try {
				ArrayList<String> toEmailIds = new ArrayList<>();
				for(StudentCareerservicesBean studentBean : studentList){
					toEmailIds.add(studentBean.getEmailId());
				}
				mailSender.sendEmail(session.getCancellationSubject(),session.getCancellationEmailBody(),toEmailIds);
				// update cancellationEmailSent to 'Y'
				notificationDAO.updateCancellationEmailStatus(session);
			} catch (Exception e) {
				logger.info("in SchedulingController class got exception : "+e.getMessage());
			}
		}
		
		private void sendSMSsToStudent(SessionDayTimeBean session, List<StudentCareerservicesBean> studentList) {
			MailSender mailSender = (MailSender)act.getBean("mailer");
			try {
				String message = session.getCancellationSMSBody();
				//String result = smsSender.sendScheduledSessionSMS(session, studentList,message);
				String result = smsSender.sendScheduledSessionSMSmGage(session, studentList,message);
				if("OK".equalsIgnoreCase(result)){
					// update cancellationSMSSent to 'Y'
					notificationDAO.updateCancellationSMSStatus(session);
				}else{
					// sending Error Email if SMS does not sent due to Password change or Username change 
//					ArrayList<String> recipent = new ArrayList<String>(Arrays.asList("sanketpanaskar@gmail.com","sneha.utekar@nmims.edu"));
					ArrayList<String> recipent = new ArrayList<String>(Arrays.asList("ashutosh.sultania.ext@nmims.edu"));
					mailSender.sendEmail("SMS NOT SEND","SMS Not Due to <br><br>"+result,recipent);
				}
			} catch (Exception e) {
				logger.info("in SchedulingController class got exception : "+e.getMessage());
			}
		}
		
	
	
	public String allChecksForScheduleWebinar (SessionDayTimeBean session){
		
		try {
			SessionSchedulerDao sDao = (SessionSchedulerDao)act.getBean("sessionSchedulerDAO");
//			ArrayList<String> tempFacultyList=getFacultyList();
			SessionDayTimeBean tempSession=new SessionDayTimeBean();
			tempSession.setDate(session.getDate());
			tempSession.setStartTime(session.getStartTime());
			
			//If not bypass all checks
			if(session.getBypassAllChecks() == null || !session.getBypassAllChecks().equals("Y")) {
				boolean isNotHoliday = sDao.isNotHoliday(session);
				if(!isNotHoliday){
					return session.getDate() + " is a Holiday ";
				}
				
				boolean isNotClashingWithPGSessions = sDao.isNotClashingWithPGSessions(session);
				if (!isNotClashingWithPGSessions) {
					return "Clashing with PG Session on "+session.getDate() +" at "+session.getStartTime() ;
				}
				
				boolean isNotClashingWithCSSessions = sDao.isNotClashingWithCSSessions(session);
				if (!isNotClashingWithCSSessions) {
					return "Clashing with Career Services Session on "+session.getDate() +" at "+session.getStartTime() ;
				} 
				
			}
			session = allocateAvailableRoomForBatchUpload(session);
			
			if(session.getErrorMessage() !=null && !"".equals(session.getErrorMessage())){
				return session.getErrorMessage();
			}
			
			//Using insertDuplicateSession() as it contains all the necessary fields than normal inssertSsession()
			Boolean sessionAdded = sDao.insertDuplicateSession(session);
			if(!sessionAdded) {
				return session.getErrorMessage()+". Error while saving data to database";
			}
			
			
		} catch (Exception e) {
			logger.info("in SchedulingController class got exception : "+e.getMessage());
			return "Error while validity check "+e.getMessage();
		}
		
		return null;
		
	}
	
	private SessionDayTimeBean allocateAvailableRoomForBatchUpload(SessionDayTimeBean sessionBean) {
		
		SessionSchedulerDao sDao = (SessionSchedulerDao)act.getBean("sessionSchedulerDAO");
		ArrayList<EndPointBean> endPointList = sDao.getAvailableRoomByLoaction(sessionBean.getDate(), sessionBean.getStartTime(), sessionBean.getEndTime(), sessionBean.getFacultyLocation());
		Gson gson = new Gson();

		int endPointListSize = endPointList !=null ? endPointList.size() : 0; 
		EndPointBean firstAvailableRoom = new EndPointBean();
		
		String date = sessionBean.getDate();
		String time = sessionBean.getStartTime();
		
		Map<String,Integer> locationCapacityMap = new HashMap<>();
		int capacityOFHostLocation = sDao.getCapacityOfLocation(sessionBean.getFacultyLocation());
		int noOfSessionAtSameDateTimeLocation = sDao.getNoOfSessionAtSameDateTimeLocation(date, time, sessionBean.getFacultyLocation());
		int capacityForHost = capacityOFHostLocation - noOfSessionAtSameDateTimeLocation;
		
		if(capacityForHost < 1) {
			sessionBean.setHostId("1");// given garbage value to avoid null pointer in further steps, this value will not be saved in db.
			sessionBean.setErrorRecord(true);
			sessionBean.setErrorMessage(sessionBean.getErrorMessage()+
										"<br>  Room not Available for Host Faculty on Date :"+sessionBean.getDate()+" Start Time : "+sessionBean.getStartTime()+" at "+sessionBean.getFacultyLocation());

		}else {

			if(endPointListSize > 0 && StringUtils.isBlank(sessionBean.getHostId())){
				
				firstAvailableRoom = endPointList.get(0);
				sessionBean.setRoom(firstAvailableRoom.getName());
				sessionBean.setHostId(firstAvailableRoom.getHostId());
				sessionBean.setHostPassword(firstAvailableRoom.getHostPassword());
				sessionBean.setHostKey(firstAvailableRoom.getZoomUID());
				locationCapacityMap.put(sessionBean.getFacultyLocation(), capacityForHost - 1);
				
			}else{
				sessionBean.setHostId("1");// given garbage value to avoid null pointer in further steps, this value will not be saved in db.
				sessionBean.setErrorRecord(true);
				sessionBean.setErrorMessage(sessionBean.getErrorMessage()+
						"<br>  Room not Available for Host Faculty on Date :"+sessionBean.getDate()+" Start Time : "+sessionBean.getStartTime()+" at "+sessionBean.getFacultyLocation());
				
			}
		}
		
		return sessionBean;
		
	}
	
	public ArrayList<String> getSubjectList(){
		if(this.subjectList == null){
			SessionSchedulerDao sDao = (SessionSchedulerDao)act.getBean("sessionSchedulerDAO");
			this.subjectList = sDao.getAllSubjects();
		}

		return subjectList;
	}
	
	public ArrayList<String> getLocationList(){
		if(this.locationList == null){
			SessionSchedulerDao sDao = (SessionSchedulerDao)act.getBean("sessionSchedulerDAO");
			this.locationList = sDao.getAllLocations();
		}

		return locationList;
	}
	
	public ArrayList<String> getFacultyList(){
		//if(this.facultyList == null){
		SessionSchedulerDao sDao = (SessionSchedulerDao)act.getBean("sessionSchedulerDAO");
		this.facultyList = sDao.getAllFaculties();
		//}
		return facultyList;
	}
	
	private ModelAndView sendToErrorPage(HttpServletRequest request, SessionDayTimeBean session, String errorMessage, String edit) {
		ModelAndView errorModelnView = new ModelAndView("admin/session/addScheduledSession");
		request.setAttribute("error", "true");
		request.setAttribute("errorMessage", errorMessage);
		errorModelnView.addObject("session", session);
		request.setAttribute("edit", edit);
		errorModelnView.addObject("yearList", ACAD_YEAR_LIST);
		errorModelnView.addObject("subjectList", getSubjectList());
		errorModelnView.addObject("locationList", getLocationList());
		
		setOtherSessionsInModel(session, errorModelnView);

		return errorModelnView;
	}
	
	private void setOtherSessionsInModel(SessionDayTimeBean session, ModelAndView modelnView) {
		SessionDayTimeBean searchBean = new SessionDayTimeBean();
//		searchBean.setYear(session.getYear());
//		searchBean.setMonth(session.getMonth());

		SessionSchedulerDao sDao = (SessionSchedulerDao)act.getBean("sessionSchedulerDAO");
		PageCareerservicesBean<SessionDayTimeBean> page = sDao.getScheduledSessionPage(1, Integer.MAX_VALUE, searchBean);
		List<SessionDayTimeBean> scheduledSessionList = page.getPageItems();
		modelnView.addObject("FacultiesInCS", facultyDAO.getAllSpeakerDetails());
		modelnView.addObject("scheduledSessionList", scheduledSessionList);

	}
	
	@RequestMapping(value = "/searchScheduledSession",  method = {RequestMethod.GET, RequestMethod.POST})
	public ModelAndView searchScheduledSession(HttpServletRequest request, HttpServletResponse response, @ModelAttribute SessionDayTimeBean searchBean){
		ModelAndView modelnView = new ModelAndView("admin/session/searchScheduledSession");
		SessionSchedulerDao sDao = (SessionSchedulerDao)act.getBean("sessionSchedulerDAO");
		request.getSession().setAttribute("searchBean", searchBean);

		PageCareerservicesBean<SessionDayTimeBean> page = sDao.getScheduledSessionPage(1, pageSize, searchBean);
		List<SessionDayTimeBean> scheduledSessionList = page.getPageItems();

		modelnView.addObject("page", page);
		modelnView.addObject("rowCount", page.getRowCount());
		modelnView.addObject("searchBean", searchBean);
		modelnView.addObject("yearList", ACAD_YEAR_LIST);
		modelnView.addObject("locationList", getLocationList());
		modelnView.addObject("semList", semList);
		modelnView.addObject("FacultiesInCS", facultyDAO.getAllSpeakerDetails());
		if(scheduledSessionList == null || scheduledSessionList.size() == 0){
			request.setAttribute("error", "true");
			request.setAttribute("errorMessage", "No Records found.");
		}
		
		modelnView.addObject("mapOfFacultyIdAndFacultyRecord",mapOfFacultyIdAndFacultyRecord());
		request.getSession().setAttribute("mapOfFacultyIdAndFacultyRecord", mapOfFacultyIdAndFacultyRecord);
		modelnView.addObject("scheduledSessionList", scheduledSessionList);
		return modelnView;
	}
	
	@RequestMapping(value = "/editScheduledSession", method = {RequestMethod.GET, RequestMethod.POST})
	public ModelAndView editScheduledSession(HttpServletRequest request, HttpServletResponse response, Model m) {
		ModelAndView modelnView = new ModelAndView("admin/session/addScheduledSession");
		SessionSchedulerDao sDao = (SessionSchedulerDao)act.getBean("sessionSchedulerDAO");
		String id = request.getParameter("id");
		
		SessionDayTimeBean session = sDao.findScheduledSessionById(id);
		m.addAttribute("session", session);
		request.getSession().setAttribute("sessionBeforeEdit", session);
		setOtherSessionsInModel(session, modelnView);

		modelnView.addObject("FacultiesInCS", facultyDAO.getAllSpeakerDetails());
		modelnView.addObject("yearList", ACAD_YEAR_LIST);
		modelnView.addObject("locationList", getLocationList());
		
		request.setAttribute("edit", "true");

		return modelnView;
	}
	
	@RequestMapping(value = "/updateScheduledSession", method = {RequestMethod.GET, RequestMethod.POST})
	public ModelAndView updateScheduledSession(HttpServletRequest request, HttpServletResponse response, @ModelAttribute SessionDayTimeBean session){
		try {
			
			SessionSchedulerDao sDao = (SessionSchedulerDao)act.getBean("sessionSchedulerDAO");
			SessionDayTimeBean sessionBeforeEdit = (SessionDayTimeBean)request.getSession().getAttribute("sessionBeforeEdit");
			String userId = (String)request.getSession().getAttribute("userId");
			session.setLastModifiedBy(userId);
			
			boolean dateTimeChanged = (!sessionBeforeEdit.getDate().equals(session.getDate())) 
					|| (!sessionBeforeEdit.getStartTime().equals(session.getStartTime()));
			
			if(!getFacultyList().contains(session.getFacultyId())){
				return sendToErrorPage(request, session, "Invalid Faculty ID:"+session.getFacultyId(), "true");
			}
			

			boolean isNotHoliday = sDao.isNotHoliday(session);

			if(!isNotHoliday){
				return sendToErrorPage(request, session, session.getDate() + " is a Holiday " , "true");
			}
			
			int capacityOfLocation = sDao.getCapacityOfLocation(session.getFacultyLocation());
			int usedCapacity = sDao.getNoOfSessionAtSameDateTimeLocation(session.getDate(), session.getStartTime(), session.getFacultyLocation());
			int availableCapacity = capacityOfLocation - usedCapacity;
		
			if(availableCapacity == 0){
				return sendToErrorPage(request, session,"Location "+session.getFacultyLocation() + " is  clashing with same location on " + session.getDate()+ " at "+session.getStartTime() + "<br>", "true") ;
			}
			
			if(dateTimeChanged){
				//Set up Web Ex ID/Password/Room for new date and time
				session = allocateAvailableRoomForBatchUpload(session);
				sDao.updateRooms(session);
				//Update session date/time after selecting room
				sDao.updateScheduledSession(session);
				//Delete old web ex meeting and create a new one
				//deleteAndCreateNewMeeting(request, response,type);
				if ("PROD".equalsIgnoreCase(ENVIRONMENT)) {
					deleteAndCreateNewWebinar(request, response);
				} else {
				}
				
			}else{
				sDao.updateScheduledSession(session);
			}
			
			session.setSessionName("");
			request.setAttribute("success","true");
			request.setAttribute("successMessage","Session Updated successfully");
			return searchScheduledSession(request, response, session);
			
		} catch (Exception e) {
			logger.info("in SchedulingController class got exception : "+e.getMessage());
			return sendToErrorPage(request, session, e.getMessage(), "true");
		}
	}
		
	public void deleteAndCreateNewWebinar(HttpServletRequest request, HttpServletResponse response){
		try {
			SessionSchedulerDao sDao = (SessionSchedulerDao)act.getBean("sessionSchedulerDAO");
			SessionDayTimeBean session = sDao.getWebinarForRefresh(request.getParameter("id"));
			
			String successMessage = "";
			String errorMessage = "";
			String oldMeetingKey = "";

			//Below 4 lines not needed, still added for consistency
			session.setHostId(session.getHostId());
			session.setHostPassword(session.getHostPassword());
			session.setMeetingKey(session.getMeetingKey());
				
			oldMeetingKey = session.getMeetingKey();
			
			if("".equalsIgnoreCase(session.getFacultyLocation()) || session.getFacultyLocation() ==null){
				setError(request, "Session cannot be created. As Location is occupied ");
				return;
			}
			
			boolean canUpdate = updateStartTimeIfMeetingPastTime(session);
			if(!canUpdate){
				setError(request, "Cannot update meeting. Please check if meeting is of past date");
				return;
			}
			
			//Delete and create new one only if it is already created.
			if(session.getMeetingKey() != null && (!"".equals(session.getMeetingKey().trim()))){
				zoomManger.deleteWebinar(session, session.getMeetingKey());
				if (!session.isErrorRecord()) {
					request.setAttribute("success","true");
					successMessage = "Earlier Session deleted successfully<br>";
					request.setAttribute("successMessage",successMessage);
				}else {
					errorMessage = "Old meeting not deleted from Zoom: "+ session.getErrorMessage() +" <br>";
					request.setAttribute("error", "true");
					request.setAttribute("errorMessage", errorMessage);
				}
				
				//Create a new Zoom meeting
				zoomManger.scheduleWebinarBatchJob(session);
				if(!session.isErrorRecord()){
					request.setAttribute("success","true");
					successMessage = successMessage + "New Session created successfully in Zoom.<br>";
					
					String dbUpdateError = sDao.updateZoomDetails(session);
					sDao.updateAttendanceForOldMeeting(session, oldMeetingKey);
					if(dbUpdateError == null){
						successMessage = successMessage + "New Session details saved in database successfully.";
						request.setAttribute("successMessage",successMessage);
					}else{
						errorMessage = errorMessage + dbUpdateError;
						setError(request, errorMessage);
					}
				}else{
					errorMessage = errorMessage + "New Session not created in Zoom: "+session.getErrorMessage() + " <br>";
					request.setAttribute("errorMessage", errorMessage);
				}
				
			}
			
		} catch (Exception e) {
			logger.info("in SchedulingController class got exception : "+e.getMessage());
		}
	}
	
	private boolean updateStartTimeIfMeetingPastTime(SessionDayTimeBean session) throws ParseException {
		//This method will check if meeting is refreshed after session time, then change start time to current time + 15 minutes

		String date = session.getDate();
        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");  
        String sessionDate = dateFormat.format(date);  
        
		String sessionTime = session.getStartTime();

		//sessionDate = "2015-02-17";
		//sessionTime = "19:00:00";

		Date sessionDateTime = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(sessionDate + " " + sessionTime); 
		Date currentSessionDate = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(sessionDate + " 23:59:00");

		if(currentSessionDate.before(new Date())){
			return false;
		}else if(sessionDateTime.after(new Date())){
			return true;
		}else if(sessionDateTime.before(new Date())){
			//Time is also earlier so no action needed

			Calendar cal = Calendar.getInstance();
			cal.setTime(new Date());
			cal.add(Calendar.MINUTE, 15);

			sessionTime = new SimpleDateFormat("HH:mm:ss").format(cal.getTime());
			session.setStartTime(sessionTime);
			return true;
		}

		return true;
	}

		
	public HashMap<String,FacultyCareerservicesBean> mapOfFacultyIdAndFacultyRecord(){
		SessionSchedulerDao sDao = (SessionSchedulerDao)act.getBean("sessionSchedulerDAO");
		ArrayList<FacultyCareerservicesBean> listOfAllFaculties = sDao.getAllFacultyRecords();
		if(this.mapOfFacultyIdAndFacultyRecord == null){
			this.mapOfFacultyIdAndFacultyRecord = new HashMap<String,FacultyCareerservicesBean>();
			for(FacultyCareerservicesBean faculty : listOfAllFaculties){
				this.mapOfFacultyIdAndFacultyRecord.put(faculty.getFacultyId(),faculty);
			}
		}
		return mapOfFacultyIdAndFacultyRecord;
	}
	
	@RequestMapping(value = "/autoScheduleForm", method = {RequestMethod.GET, RequestMethod.POST})
	public ModelAndView autoScheduleForm(HttpServletRequest request, HttpServletResponse respnse, Model m) {

		try {
			SessionSchedulerDao sDao = (SessionSchedulerDao)act.getBean("sessionSchedulerDAO");

			List<SessionDayTimeBean> pendingConferenceList = sDao.getPendingConferenceList();
			m.addAttribute("pendingConferenceCount", pendingConferenceList.size());
			m.addAttribute("pendingConferenceList", pendingConferenceList);

		}catch (Exception e) {
			request.setAttribute("error", "true");
			request.setAttribute("errorMessage", e.getMessage());
		}

		return new ModelAndView("admin/session/autoScheduleSessions");
	}
	
	public void setSuccess(HttpServletRequest request, String successMessage){
		request.setAttribute("success","true");
		request.setAttribute("successMessage",successMessage);
	}
	
	public void setError(HttpServletRequest request, String errorMessage){
		request.setAttribute("error", "true");
		request.setAttribute("errorMessage", errorMessage);
	}
	
	@RequestMapping(value = "/bookTrainingSessions", method = {RequestMethod.GET, RequestMethod.POST})
	public ModelAndView bookTrainingSessions(HttpServletRequest request, HttpServletResponse respnse, Model m) throws Exception{
		
//		ModelAndView modelnView = new ModelAndView("admin/session/autoScheduleSessions");
		SessionSchedulerDao sDao = (SessionSchedulerDao)act.getBean("sessionSchedulerDAO");
		List<SessionDayTimeBean> pendingConferenceList = sDao.getPendingConferenceList();
		ArrayList<SessionDayTimeBean> errorList = new ArrayList<>();
		ArrayList<SessionDayTimeBean> zoomCreatedSessionList = new ArrayList<>();
	
		if (pendingConferenceList != null && (!pendingConferenceList.isEmpty())) {

		if (ENVIRONMENT.equalsIgnoreCase("PROD")){
			for (int i = 0; i < pendingConferenceList.size(); i++) {
				
				SessionDayTimeBean session = pendingConferenceList.get(i);

				zoomManger.scheduleWebinarBatchJob(session);

				if(session.isErrorRecord()){
					errorList.add(session);
				}else{
					zoomCreatedSessionList.add(session);
				}

			}
			@SuppressWarnings("unused")
			int[] result = sDao.updateBookedConference(zoomCreatedSessionList);
			
			if(errorList.size() > 0){
				request.setAttribute("error", "true");
				request.setAttribute("errorMessage", errorList.size() + " sessions were not created in Zoom due to error.");
			}

			if(zoomCreatedSessionList.size() > 0){
				request.setAttribute("success","true");
				request.setAttribute("successMessage",zoomCreatedSessionList.size() +" sessions out of " + pendingConferenceList.size() + " created in Zoom successfully");
			}
			
			
		}else
			{
				int[] result = sDao.updateBookedConference(pendingConferenceList);
				if(result.length != 0){
					request.setAttribute("success","true");
					request.setAttribute("successMessage"," Sessions Not created in Zoom as it not PROD");
				}
			}
		}
		
		return autoScheduleForm(request,respnse,m);
	}
	

	@RequestMapping(value = "/loginIntoZoom", method = {RequestMethod.GET, RequestMethod.POST})
	public ModelAndView joinSesssion(HttpServletRequest request){

		String id = request.getParameter("id");

		SessionDayTimeBean session_bean = sessionsDAO.findScheduledSessionById(id);
		if(session_bean != null) {
			String start_webinar_url = zoomManger.getStartWebinarLink(session_bean.getMeetingKey(), session_bean.getHostId());
			if(!start_webinar_url.equals("error")){	
			    return new ModelAndView("redirect:" + start_webinar_url);
			}else {
				ModelAndView model_n_view = new ModelAndView("viewScheduledSession");
				model_n_view.addObject("session", session_bean);
				request.setAttribute("error", "true");
				request.setAttribute("errorMessage", "Invalid Zoom User Token");
				return model_n_view;
			}
		}
		else {
			ModelAndView model_n_view = new ModelAndView("viewScheduledSession");
			request.setAttribute("error", "true");
			request.setAttribute("errorMessage", "Invalid Session ID");
			return model_n_view;
		}
	
	}
	
	@RequestMapping(value = "/searchScheduledSessionForm", method = {RequestMethod.GET, RequestMethod.POST})
	public String searchScheduledSessionForm(HttpServletRequest request, HttpServletResponse respnse, Model m) {

		SessionDayTimeBean searchBean = new SessionDayTimeBean();
		m.addAttribute("searchBean",searchBean);
		m.addAttribute("yearList", ACAD_YEAR_LIST);
		m.addAttribute("locationList", getLocationList());
		return "admin/session/searchScheduledSession";
	}
	
	@RequestMapping(value = "/deleteScheduledSession", method = {RequestMethod.GET, RequestMethod.POST})
	public ModelAndView deleteScheduledSession(HttpServletRequest request, HttpServletResponse response){
		try{
			String id = request.getParameter("id");
			SessionSchedulerDao sDao = (SessionSchedulerDao)act.getBean("sessionSchedulerDAO");
			deleteMeeting(request, response);
			sDao.deleteScheduledSession(id);
			request.setAttribute("success","true");
			request.setAttribute("successMessage","Record deleted successfully from Database");

			
		}catch(Exception e){
			logger.info("in SchedulingController class got exception : "+e.getMessage());
			request.setAttribute("error", "true");
			request.setAttribute("errorMessage", "Error in deleting Record.");
		}
		SessionDayTimeBean searchBean = (SessionDayTimeBean)request.getSession().getAttribute("searchBean");
		if(searchBean == null){
			searchBean = new SessionDayTimeBean();
		}
		return searchScheduledSession(request,response, searchBean);
	}

	private void deleteMeeting(HttpServletRequest request, HttpServletResponse response){
		
		try {
			SessionSchedulerDao sDao = (SessionSchedulerDao)act.getBean("sessionSchedulerDAO");
			SessionDayTimeBean session = sDao.getSessionForCheck(request.getParameter("id"));
			
			String successMessage = "";
			String errorMessage = "";

			if(session.getMeetingKey() != null && (!"".equals(session.getMeetingKey().trim()))){
				//webExManager.deleteTrainingSession(session);
				zoomManger.deleteWebinar(session, session.getMeetingKey());
				if(!session.isErrorRecord()){
					request.setAttribute("success","true");
					successMessage = "Earlier Session deleted successfully<br>";
					request.setAttribute("successMessage",successMessage);
				}else{
					errorMessage = "Old meeting not deleted from Zoom: "+ session.getErrorMessage() +" <br>";
					request.setAttribute("error", "true");
					request.setAttribute("errorMessage", errorMessage);
				}
			}

		} catch (Exception e) {
			logger.info("in SchedulingController class got exception : "+e.getMessage());
			request.setAttribute("error", "true");
			request.setAttribute("errorMessage", "Error in refreshing session: " + e.getMessage());
		}
	}
	
	@RequestMapping(value = "/getEntitlements", method = {RequestMethod.GET, RequestMethod.POST})
	public String getEntitlements(){
		
		return "entitlements";
	}
	
	@RequestMapping(value = "/getEntitlementDependency", method = {RequestMethod.GET, RequestMethod.POST})
	public String getEntitlementDependency(){
		
		return "entitlementDependency";
	}
	
	@RequestMapping(value = "/getNewFile", method = {RequestMethod.GET, RequestMethod.POST})
	public String getNewFile(){
		
		return "NewFile";
	}
	
	@RequestMapping(value = "/getFeatures", method = {RequestMethod.GET, RequestMethod.POST})
	public String getFeatures(){
		
		return "features";
	}
}
