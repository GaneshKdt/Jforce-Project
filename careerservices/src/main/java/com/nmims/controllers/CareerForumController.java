
package com.nmims.controllers;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.exception.ExceptionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import com.google.gson.Gson;
import com.nmims.beans.CSResponse;
import com.nmims.beans.CareerForumEventStatusModelBean;
import com.nmims.beans.CareerForumEventsModelBean;
import com.nmims.beans.CareerForumHomeModelBean;
import com.nmims.beans.FeatureTypes;
import com.nmims.beans.SessionDayTimeBean;
import com.nmims.beans.StudentCareerservicesBean;
import com.nmims.beans.StudentEntitlement;
import com.nmims.beans.UserViewedWebinar;
import com.nmims.beans.VideoContentCareerservicesBean;
import com.nmims.beans.VideoContentTypes;
import com.nmims.daos.EntitlementActivationDAO;
import com.nmims.daos.VideoRecordingDao;
import com.nmims.daos.WebinarSchedulerDAO;

@Controller
public class CareerForumController extends CSPortalBaseController {
	
//	@Autowired
//	private StudentDataManagementDAO studentDataManagementDAO;

	@Autowired
	private EntitlementActivationDAO entitlementActivationDAO;

	@Autowired
	private WebinarSchedulerDAO webinarSchedulerDAO;

	@Autowired
	private VideoRecordingDao videoRecordingDao;

	private static final Logger logger = LoggerFactory.getLogger(CareerForumController.class);
 
	@Value( "${SERVER_PATH}" )
	private String SERVER_PATH;
	
	private Gson gson = new Gson();
	
	
	private List<SessionDayTimeBean> allEvents;
	
/*
 * 	------------ Portal ------------
 */
	
	/*
	 * 	career forum home page
	 */
		@RequestMapping(value = "/career_forum", method = RequestMethod.GET)
		public String home(HttpServletRequest request, Model model) {
			//check if a sapid is entered and its not null
			if(!checkLogin(request)) {
				return "redirect:../studentportal/home";
			}
			if(!checkCSAccess(request)) {
				return "redirect:/showAllProducts";
			}
			
			return "portal/career_forum/career_forum_home";
		}

	/*
	 * 	view a scheduled session
	 */
		@RequestMapping(value = "/viewScheduledSession", method = {RequestMethod.GET, RequestMethod.POST})
		public ModelAndView viewScheduledSession(HttpServletRequest request, HttpServletResponse response) throws Exception{
			
			String id = request.getParameter("id");
			if(id == null) {
				return new ModelAndView("redirect:career_forum");
			}
			if(!checkLoginFacultyAndStudent(request)) {
				return new ModelAndView("redirect:../studentportal/home");
			}
			String sapid = (String)request.getSession().getAttribute("userId");
			if((sapid.startsWith("77") || sapid.startsWith("79"))) {
				
				if(!checkCSAccess(request)) {
					return new ModelAndView("redirect:/showAllProducts");
				}
			}
			
			ModelAndView modelnView;

			
			//--------CS Specefic check--------
			if((sapid.startsWith("77") || sapid.startsWith("79"))/* && !"77215000851".equals(userId) */){

				 modelnView = new ModelAndView("portal/career_forum/viewScheduledSession_student");
				//get the latest entitlement 
				StudentEntitlement entitlement = entitlementActivationDAO.getApplicableEntitlementForPurchase(sapid, FeatureTypes.CAREER_FORUM);
			
				int activationsLeft = entitlement.getActivationsLeft();
				
				modelnView.addObject("activationsLeft", activationsLeft);
				
				
				//check if student has attended this session
				if(entitlementActivationDAO.checkIfStudentActivatedSessionWithThisId(sapid, id)) {
					modelnView.addObject("sessionViewed", true);
					modelnView.addObject("canActivate", true);
				}else {
					modelnView.addObject("sessionViewed", false);
					
					// If yes, let him attend. No deductions will be made if he does
					if(activationsLeft > 0) {
						modelnView.addObject("canActivate", true);
					}else {
						modelnView.addObject("canActivate", false);
					}
				}
				
				//check if sesssion has any video recording available
				
				VideoContentCareerservicesBean videoContentBean = new VideoContentCareerservicesBean();
				boolean videoContentAvailable = false;
				if(videoRecordingDao.checkIfRecordingExistsForSessionId(id)) {
					videoContentAvailable = true;
					videoContentBean = videoRecordingDao.getVideoContentBySessionId(id);
				}
				modelnView.addObject("videoContentBean", videoContentBean);
				modelnView.addObject("videoContentAvailable", videoContentAvailable);
				
			}else {
				modelnView = new ModelAndView("portal/career_forum/viewScheduledSession_faculty");
			}
			//---------------END---------------
			
			/*String classFullMessage = request.getParameter("classFullMessage");*/
			SessionDayTimeBean session = webinarSchedulerDAO.findScheduledSessionById(id);
			HashMap<String,Integer> facultyIdAndRemSeatsMap = webinarSchedulerDAO.getMapOfFacultyIdAndRemainingSeats(id,session);
			modelnView.addObject("facultyIdAndRemSeatsMap", facultyIdAndRemSeatsMap);
			modelnView.addObject("session", session);

			String sessionStartDate = session.getDate();
	       /* DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");  
	        String sessionDate = dateFormat.format(date);  */
			String sessionStartTime = session.getStartTime();

			String sessionEndDate = session.getDate();
			String sessionEndTime = session.getEndTime();

			Date sessionStartDateTime = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(sessionStartDate + " " + sessionStartTime); 
			Date sessionEndDateTime = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(sessionEndDate + " " + sessionEndTime); 
			long minutesToSession = getDateDiff(new Date(), sessionStartDateTime, TimeUnit.MINUTES);
			long minutesAfterSession = getDateDiff(sessionEndDateTime, new Date(), TimeUnit.MINUTES);

			modelnView.addObject("enableAttendButton", "false");
			modelnView.addObject("showQueryButton", "false");
			modelnView.addObject("sessionOver", "false");
			modelnView.addObject("mapOfFacultyIdAndFacultyRecord",webinarSchedulerDAO.mapOfFacultyIdAndFacultyRecord());
			
			
			
			if(minutesToSession < 60 && minutesAfterSession < 0){
				modelnView.addObject("enableAttendButton", "true");
			}

			
			if(minutesAfterSession > 0){
				modelnView.addObject("sessionOver", "true");
			}
			

			//For Students join URL
			String studentJoinUrlForHost= session.getJoinUrl();
			
			modelnView.addObject("joinUrl", "attendScheduledSession?id=" + id);
			
			//For Hosts join URL
			String hostUrl = session.getHostUrl();
			
			modelnView.addObject("hostUrl", hostUrl);
			
			modelnView.addObject("SERVER_PATH", SERVER_PATH);

			return modelnView;
		}

/*
 * 	---------APIS---------
 */
		
	/*
	 * 	Returns the list of webinars for student calendar.
	 */
		@CrossOrigin(origins = "*") 
		@RequestMapping(value = "/getAllWebinars", method = RequestMethod.POST, produces = "application/json", consumes = "application/json")
		public ResponseEntity<String> getAllWebinars(Locale locale, Model model, @RequestBody Map<String, String> requestParams) {
			CSResponse csResponse = new CSResponse();
			
			if(!requestParams.containsKey("sapid")) {
				csResponse.setNotValidRequest();
				return ResponseEntity.ok(gson.toJson(csResponse));
			}
			String sapid = requestParams.get("sapid");
			StudentCareerservicesBean student = getStudent(sapid);
			if(student == null) {
				csResponse.setInvalidSapid();
				return ResponseEntity.ok(gson.toJson(csResponse));
			}
			if(!checkCSAccess(student)) {
				csResponse.setNoCSAccess();
				return ResponseEntity.ok(gson.toJson(csResponse));
			}
			
			List<SessionDayTimeBean> sessions = new ArrayList<>();
			
			try {
				sessions = webinarSchedulerDAO.getAllWebinarSchedule();
			} catch ( Throwable throwable ) {
				logger.info("in getAllWebinars got exception : "+ExceptionUtils.getFullStackTrace(throwable));
			}
			
			return ResponseEntity.ok(gson.toJson(sessions));
		}

	/*
	 * 	Returns the info for career forum main page
	 */
		@CrossOrigin(origins = "*") 
		@RequestMapping(value = "/m/getCareerForumInfo", method = RequestMethod.POST, produces = "application/json", consumes = "application/json")
		public ResponseEntity<String> getCareerForumInfo(Locale locale, Model model, @RequestBody Map<String, String> requestParams) {
			//check if a sapid is entered and its not null
			CSResponse csResponse = new CSResponse();
			
			if(!requestParams.containsKey("sapid")) {
				csResponse.setNotValidRequest();
				return ResponseEntity.ok(gson.toJson(csResponse));
			}
			String sapid = requestParams.get("sapid");
			StudentCareerservicesBean student = getStudent(sapid);
			if(student == null) {
				csResponse.setInvalidSapid();
				return ResponseEntity.ok(gson.toJson(csResponse));
			}
			if(!checkCSAccess(student)) {
				csResponse.setNoCSAccess();
				return ResponseEntity.ok(gson.toJson(csResponse));
			}
			allEvents = getAllEvents(sapid);
			csResponse.setStatusSuccess();
			csResponse.setResponse(getCareerForumData(sapid));
			
			return ResponseEntity.ok(gson.toJson(csResponse));
		}
		/*
		 * 	Returns the info for career forum main page
		 */
			@CrossOrigin(origins = "*") 
			@RequestMapping(value = "/m/getCareerForumActivationInfo", method = RequestMethod.POST, produces = "application/json", consumes = "application/json")
			public ResponseEntity<String> getCareerForumActivationInfo(Locale locale, Model model, @RequestBody Map<String, String> requestParams) {
				//check if a sapid is entered and its not null
				CSResponse csResponse = new CSResponse();
				
				if(!requestParams.containsKey("sapid")) {
					csResponse.setNotValidRequest();
					return ResponseEntity.ok(gson.toJson(csResponse));
				}
				String sapid = requestParams.get("sapid");
				StudentCareerservicesBean student = getStudent(sapid);
				if(student == null) {
					csResponse.setInvalidSapid();
					return ResponseEntity.ok(gson.toJson(csResponse));
				}
				if(!checkCSAccess(student)) {
					csResponse.setNoCSAccess();
					return ResponseEntity.ok(gson.toJson(csResponse));
				}
				
				csResponse.setStatusSuccess();
				csResponse.setResponse(getActivationInfo(sapid, FeatureTypes.CAREER_FORUM));
				
				return ResponseEntity.ok(gson.toJson(csResponse));
			}
			
	/*
	 * 	Returns the list of sessions which student hasn't viewed
	 */
		@CrossOrigin(origins = "*") 
		@RequestMapping(value = "/getCareerForumNotViewedEvents", method = RequestMethod.POST, produces = "application/json", consumes = "application/json")
		public ResponseEntity<String> getCareerForumNotViewedEvents(Locale locale, Model model, @RequestBody Map<String, String> requestParams) {
			//check if a sapid is entered and its not null
			CSResponse csResponse = new CSResponse();
			
			if(!requestParams.containsKey("sapid")) {
				csResponse.setNotValidRequest();
				return ResponseEntity.ok(gson.toJson(csResponse));
			}
			String sapid = requestParams.get("sapid");
			StudentCareerservicesBean student = getStudent(sapid);
			if(student == null) {
				csResponse.setInvalidSapid();
				return ResponseEntity.ok(gson.toJson(csResponse));
			}
			if(!checkCSAccess(student)) {
				csResponse.setNoCSAccess();
				return ResponseEntity.ok(gson.toJson(csResponse));
			}
			allEvents = getAllEvents(sapid);
			
			csResponse.setStatusSuccess();
			csResponse.setResponse(getCareerForumData(sapid).getEvents().getStatus().getNotViewedEvents());
			
			return ResponseEntity.ok(gson.toJson(csResponse));
		}

	/*
	 * 	Returns the list of sessions which student has viewed
	 */
		@CrossOrigin(origins = "*") 
		@RequestMapping(value = "/m/getCareerForumViewedEvents", method = RequestMethod.POST, produces = "application/json", consumes = "application/json")
		public ResponseEntity<String> getCareerForumViewedEvents(Locale locale, Model model, @RequestBody Map<String, String> requestParams) {
			//check if a sapid is entered and its not null
			CSResponse csResponse = new CSResponse();
			
			if(!requestParams.containsKey("sapid")) {
				csResponse.setNotValidRequest();
				return ResponseEntity.ok(gson.toJson(csResponse));
			}
			String sapid = requestParams.get("sapid");
			StudentCareerservicesBean student = getStudent(sapid);
			if(student == null) {
				csResponse.setInvalidSapid();
				return ResponseEntity.ok(gson.toJson(csResponse));
			}
			if(!checkCSAccess(student)) {
				csResponse.setNoCSAccess();
				return ResponseEntity.ok(gson.toJson(csResponse));
			}
			allEvents = getAllEvents(sapid);
			csResponse.setStatusSuccess();
			csResponse.setResponse(getCareerForumData(sapid).getEvents().getStatus().getViewedEvents());
			
			return ResponseEntity.ok(gson.toJson(csResponse));
		}
	
	/*
	 *	Upcoming events bean for CF
	 */
		@CrossOrigin(origins = "*") 
		@RequestMapping(value = "/getUpcomingCareerForumSchedule", method = RequestMethod.POST, produces = "application/json", consumes = "application/json")
		public ResponseEntity<String> getUpcomingCareerForumSchedule(Locale locale, Model model, @RequestBody Map<String, String> requestParams) {
			//check if a sapid is entered and its not null
			CSResponse csResponse = new CSResponse();
			
			if(!requestParams.containsKey("sapid")) {
				csResponse.setNotValidRequest();
				return ResponseEntity.ok(gson.toJson(csResponse));
			}
			String sapid = requestParams.get("sapid");
			StudentCareerservicesBean student = getStudent(sapid);
			if(student == null) {
				csResponse.setInvalidSapid();
				return ResponseEntity.ok(gson.toJson(csResponse));
			}
			if(!checkCSAccess(student)) {
				csResponse.setNoCSAccess();
				return ResponseEntity.ok(gson.toJson(csResponse));
			}
			allEvents = getAllEvents(sapid);
			CareerForumHomeModelBean reponseBean = getCareerForumData(sapid);
			CareerForumEventsModelBean events = reponseBean.getEvents();
			events.setStatus(null);
			reponseBean.setEvents(events);

			csResponse.setStatusSuccess();
			csResponse.setResponse(reponseBean);
			
			return ResponseEntity.ok(gson.toJson(csResponse));
			
		}
		
		
/*
 * 	--------- END -------------		
 */
			
		
/*
 *	----------- Object Methods----------
 */

		
	/*
	 * 	get all career forum data	
	 */
		private CareerForumHomeModelBean getCareerForumData(String sapid) {
			CareerForumHomeModelBean modelBean = new CareerForumHomeModelBean();
			modelBean.setActivationInfo(getActivationInfo(sapid, FeatureTypes.CAREER_FORUM));
			modelBean.setDescription(""
					+ "Webinar sessions with career coaches to provide you with comprehensive knowledge on building career paths."
					+ "Students can take a maximum of twelve webinars during the course of program.");
	
			modelBean.setEvents(getEventsModelBeanCareerForum(sapid));
	
			return modelBean;
		}

	/*
	 * 	events bean for CF
	 */
		private CareerForumEventsModelBean getEventsModelBeanCareerForum(String sapid) {
			
			CareerForumEventsModelBean events = new CareerForumEventsModelBean();
			
			//add the events to schedule
			events.setSchedule(getActiveAndUpcoming(getAllEvents(sapid)));
			
			events.setStatus(getEventStatus(sapid));
			return events;
		}
	

	/*
	 * 	get events status model bean
	 */
		private CareerForumEventStatusModelBean getEventStatus(String sapid) {

			List<SessionDayTimeBean> viewedEvents = new ArrayList<SessionDayTimeBean>();
			List<SessionDayTimeBean> notViewedEvents = new ArrayList<SessionDayTimeBean>();
			List<UserViewedWebinar> viewedWebinars = webinarSchedulerDAO.getStudentViewedWebinarList(sapid);
			
			//check for active events
			for(SessionDayTimeBean event: allEvents) {
				boolean viewed = false;
				//check if student viewed this webinar
				for (UserViewedWebinar userViewedWebinar : viewedWebinars) {
					if(userViewedWebinar.getWebinarId().equals(event.getId())) {
						viewed = true;
						event.setAttendTime(userViewedWebinar.getConsumptionDate());
						event.setPackageName(userViewedWebinar.getPackageName());
						viewedEvents.add(event);
					}
				}
				if(!viewed) {
					SessionDayTimeBean sessionWithStartAndEndTime = dataHelpers.addStartAndEndDate(event);
					Date date = dataHelpers.convertStringToDateAndTime(sessionWithStartAndEndTime.getEndDate());
					date = dataHelpers.addMinutesToDate(date, 60);
					if(dataHelpers.checkIfDateBeforeCurrent(date) 
//							&& !dataHelpers.checkIfeventActive(event)
							) {
						notViewedEvents.add(event);
					}
				}
			}

			//add the events to status
			CareerForumEventStatusModelBean status = new CareerForumEventStatusModelBean();
			
			status.setNotViewedEvents(notViewedEvents);
			status.setViewedEvents(viewedEvents);

			return status;
		}
		
	@RequestMapping(value = "/career_forum_video_content", method = RequestMethod.GET)
	public String watchVideo(HttpServletRequest request, HttpServletResponse response, Model model, @RequestParam("id") String sessionId) {
		//check if a sapid is entered and its not null
		if(!checkLogin(request)) {
			return "redirect:../studentportal/home";
		}
		String sapid = (String) request.getSession().getAttribute("userId");
		if(!checkCSAccess(request)) {
			return "redirect:/showAllProducts";
		}
		
		VideoContentCareerservicesBean videoContentBean = new VideoContentCareerservicesBean();

		if(!entitlementActivationDAO.checkIfStudentActivatedSessionWithThisId(sapid, sessionId)) {
			model.addAttribute("errorMessage", "You haven't Activated this session yet!");
			return "redirect:career_forum";
		}
		
		SessionDayTimeBean session = webinarSchedulerDAO.findScheduledSessionById(sessionId);
		model.addAttribute("session", session);

		if(videoRecordingDao.checkIfRecordingExistsForSessionId(sessionId)) {
			videoContentBean = videoRecordingDao.getVideoContentBySessionId(sessionId);
		}

		List<VideoContentCareerservicesBean> allVideoContent = videoRecordingDao.getAllVideoContentByTypeId(VideoContentTypes.SESSION_VIDEO);
		model.addAttribute("videoContentBean", videoContentBean);
		model.addAttribute("allVideoContent", allVideoContent);
		return "portal/career_forum/videoContent";
	}
/*
 * 	----------- END --------------
 */
	public static long getDateDiff(Date date1, Date date2, TimeUnit timeUnit) {
		long diffInMillies = date2.getTime() - date1.getTime();
		return timeUnit.convert(diffInMillies,TimeUnit.MILLISECONDS);
	}
}
