package com.nmims.controllers;


import java.io.IOException;
import java.math.BigDecimal;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.view.RedirectView;

import com.nmims.beans.AnnouncementAcadsBean;
import com.nmims.beans.ELearnResourcesAcadsBean;
import com.nmims.beans.ExamOrderAcadsBean;
import com.nmims.beans.FacultyAcadsBean;
import com.nmims.beans.PersonAcads;
import com.nmims.beans.SessionDayTimeAcadsBean;
import com.nmims.beans.StudentAcadsBean;
import com.nmims.beans.UserAuthorizationBean;
import com.nmims.beans.VideoContentAcadsBean;
import com.nmims.daos.CareerServicesDAO;
import com.nmims.daos.ContentDAO;
import com.nmims.daos.LDAPDao;
import com.nmims.daos.LeadDAO;
import com.nmims.daos.TimeTableDAO;
import com.nmims.daos.VideoContentDAO;
import com.nmims.dto.LoginSSODto;
import com.nmims.helpers.AESencrp;
import com.nmims.helpers.DateTimeHelper;
import com.nmims.helpers.MobileNotificationHelper;
import com.nmims.helpers.SalesforceHelper;
import com.nmims.services.StudentCourseMappingService;
import com.nmims.services.StudentService;
import com.nmims.services.VideoContentService;
import com.nmims.util.ExamOrderUtil;

/**
 * Handles requests for the application home page.
 */
@Controller
public class HomeController extends BaseController{

	@Autowired
	ApplicationContext act;

	@Autowired
	CareerServicesDAO csDAO; 
	
	@Autowired
	MobileNotificationHelper mobileNotificationHelper;
	
	@Autowired
	SalesforceHelper sfdc;
	
	@Autowired
	LeadDAO leadDAO;
	
	@Autowired
	StudentService studentService;
	
	@Autowired
	VideoContentService videoContentService;
	
	@Value("${CURRENT_ACAD_MONTH}")
	private String CURRENT_ACAD_MONTH;

	@Value("${CURRENT_ACAD_YEAR}")
	private String CURRENT_ACAD_YEAR;
	
	@Value("${TIMEBOUND_PORTAL_LIST}")
	private List<String> TIMEBOUND_PORTAL_LIST;
	
	private static final Logger logger = LoggerFactory.getLogger(HomeController.class);
	private SimpleDateFormat formatter = new SimpleDateFormat("yyyy/MM/dd");

	public static final String LIVE_SESSION_ACCESS_DATE = "01/Jul/2021";//compulsory format dd/MMM/yyyy
	public static final String PGM_CODE_BBA = "BBA";
	public static final String PGM_CODE_BCOM = "B.Com";
	public static final String PGM_CODE_CP_WL = "CP-WL";
	public static final String PGM_CODE_CP_ME = "CP-ME";
	public static final String PGM_CODE_PD_WM = "PD - WM";
	public static final String PGM_CODE_PD_DM = "PD - DM";
	public static final String PGM_CODE_M_Sc_App_Fin = "M.Sc. (App. Fin.)";
	public static final String PGM_CODE_BBA_BA = "BBA-BA";
	public static final List<String> nonPG_ProgramList = new ArrayList<String>(Arrays.asList(PGM_CODE_BBA, PGM_CODE_BCOM, 
														PGM_CODE_PD_WM, PGM_CODE_PD_DM, PGM_CODE_M_Sc_App_Fin, PGM_CODE_CP_WL, PGM_CODE_CP_ME, PGM_CODE_BBA_BA));
	
	@Autowired
	StudentCourseMappingService studentCourseMappingService;
	
	private final static String facultyRole = "Faculty";
	private final static String insofeRole = "Insofe";
	
	public HomeController(){
	}
	/**
	 * Simply selects the home view to render by returning its name.
	 */
	@RequestMapping(value = "/", method = RequestMethod.GET)
	public String homePage(Locale locale, Model model) {
		return "login";
	}
	
	@RequestMapping(value = "/home", method = {RequestMethod.GET, RequestMethod.POST})
	public String goToHome(HttpServletRequest request, HttpServletResponse response) {
		
		if(!checkSession(request, response)){
			return "login";
			//Check is user is Admin.
		}
		
		String userId = (String)request.getSession().getAttribute("userId_acads");
		PersonAcads user = (PersonAcads)request.getSession().getAttribute("user_acads");
		
		if(userId.startsWith("77") || userId.startsWith("79")){
			return "studentHome";
		}
		return "home";
	}
	
//	@RequestMapping(value = "/vimeoToPortal", method = {RequestMethod.GET, RequestMethod.POST})
//	public String vimeoToPortal() throws IOException {
//		DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd");
//		LocalDateTime now = LocalDateTime.now();
//		String date = dtf.format(now);
//		date = "2019-01-06";
//		VimeoManager vimeo = new VimeoManager(); 
//		TimeTableDAO sessions = (TimeTableDAO)act.getBean("timeTableDAO");
//		List<SessionDayTimeBean> sessionList = sessions.getSessionsUserIdForDate(date);
//		int i = 0;
//		for (SessionDayTimeBean attendList : sessionList) {
//			JsonObject vimeoResponse = vimeo.checkUploadVideoStatus("primary",attendList.getMeetingKey(), attendList.getVimeoId(), date, sessions);
//			
//			if(attendList.getAltHostId() != null && !attendList.getAltHostId().isEmpty() && attendList.getAltuploadStatus() != "error" && attendList.getAltvimeoId() != null && !attendList.getAltvimeoId().isEmpty() ) {
//				//upload alternate host id data on vimeo
//				vimeo.checkUploadVideoStatus("alt",attendList.getAltMeetingKey(), attendList.getAltvimeoId(), date, sessions);
//			}
//			if(attendList.getAltHostId2() != null && !attendList.getAltHostId2().isEmpty() && attendList.getAltuploadStatus2() != "error" && attendList.getAltvimeoId2() != null && !attendList.getAltvimeoId2().isEmpty()) {
//				//upload alternate host id data on vimeo
//				vimeo.checkUploadVideoStatus("alt2",attendList.getAltMeetingKey2(), attendList.getAltvimeoId2(), date, sessions);
//			}
//			if(attendList.getAltHostId3() != null && !attendList.getAltHostId3().isEmpty() && attendList.getAltuploadStatus3() != "error" && attendList.getAltvimeoId3() != null && !attendList.getAltvimeoId3().isEmpty()) {
//				//upload alternate host id data on vimeo
//				vimeo.checkUploadVideoStatus("alt3",attendList.getAltMeetingKey3(), attendList.getAltvimeoId3(), date, sessions);
//			}
//			i++;
//		}
//		return "TestingPingRequest";
//	}
	/**
	 * function created to get response from vimeo and upload video url to video_content table
	 * */
//	private boolean insertIntoVideoContent(JsonObject vimeoResponse,RecordingStatus recordingStatus) throws IOException {
//			//Checking condition whether video is available on vimeo or not
//			TimeTableDAO recording_status = (TimeTableDAO)act.getBean("timeTableDAO");
//			if(vimeoResponse == null) {
//				recording_status.updateVimeoStatus(recordingStatus.getMeetingId(), "Null response Vimeo");
//				return false;
//			}
//			if(!"available".equalsIgnoreCase(vimeoResponse.get("status").getAsString())) {
//				recording_status.updateVimeoStatus(recordingStatus.getMeetingId(), "Vimeo recording still not available");
//				return false;
//			}
//			VideoContentBean videoContentBean = new VideoContentBean();
//			VideoContentDAO video_content = (VideoContentDAO) act.getBean("videoContentDAO");
//			SessionBean sessionBean = recording_status.getSessionDataById(Integer.parseInt(recordingStatus.getSessionId()));
//			if(sessionBean == null) {
//				return false;
//			}
//			String contentOwner = "System";
//			
//			DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
//			Date date = new Date();
//			String currentDate = dateFormat.format(date);
//			
//			videoContentBean.setSessionId(Integer.parseInt(recordingStatus.getSessionId()));
//			videoContentBean.setFileName(sessionBean.getSubject() + " " + sessionBean.getSessionName());
//			videoContentBean.setFacultyId(sessionBean.getFacultyId());
//			videoContentBean.setKeywords(sessionBean.getSubject() + " " + sessionBean.getSessionName());
//			videoContentBean.setDescription(sessionBean.getSubject() + " " + sessionBean.getSessionName());
//			videoContentBean.setSubject(sessionBean.getSubject());
//			videoContentBean.setSessionDate(sessionBean.getDate());
//			videoContentBean.setAddedOn(currentDate);
//			videoContentBean.setAddedBy(contentOwner);
//			videoContentBean.setYear(sessionBean.getYear());
//			videoContentBean.setMonth(sessionBean.getMonth());
//			videoContentBean.setCreatedBy(contentOwner);
//			videoContentBean.setLastModifiedBy(contentOwner);
//			videoContentBean.setSessionPlanModuleId(sessionBean.getModuleId());
//			//get videos from vimeo response
//			for(JsonElement files : vimeoResponse.get("files").getAsJsonArray() ) {
//				JsonObject file = files.getAsJsonObject();
//				if(file.get("link").getAsString().indexOf("profile_id=174") != -1) {
//					videoContentBean.setMobileUrlHd(file.get("link").getAsString());
//				}
//				else if(file.get("link").getAsString().indexOf("profile_id=165") != -1) {
//					videoContentBean.setMobileUrlSd1(file.get("link").getAsString());
//				}
//				else if(file.get("link").getAsString().indexOf("profile_id=164") != -1) {
//					videoContentBean.setMobileUrlSd2(file.get("link").getAsString());
//				}
//			}
//			
//			//get thumbnail from vimeo response
//			JsonObject pictures = vimeoResponse.get("pictures").getAsJsonObject();
//			for(JsonElement sizes : pictures.get("sizes").getAsJsonArray()) {
//				JsonObject size = sizes.getAsJsonObject();
//				if(size.get("width").getAsInt() == 1920) {
//					videoContentBean.setThumbnailUrl(size.get("link").getAsString());
//					break;
//				}
//				else if(size.get("width").getAsInt() == 1280) {
//					videoContentBean.setThumbnailUrl(size.get("link").getAsString());
//					break;
//				}
//			}
//			videoContentBean.setVideoLink("https://player.vimeo.com" + vimeoResponse.get("uri").getAsString());
//			/*if(videoContentBean.getMobileUrlHd() == null) {
//				//no video found with size 174,
//				recording_status.updateVimeoStatus(recordingStatus.getMeetingId(), "Error in vimeo hdVideoUrl,getting value hdVideoUrl null");
//				return false;
//			}
//			if(videoContentBean.getMobileUrlSd1() == null) {
//				//no video found with size 165,
//				recording_status.updateVimeoStatus(recordingStatus.getMeetingId(), "Error in vimeo SD1VideoUrl,getting value SD1VideoUrl null");
//				return false;
//			}
//			if(videoContentBean.getMobileUrlSd2() == null) {
//				//no video found with size 164,
//				recording_status.updateVimeoStatus(recordingStatus.getMeetingId(), "Error in vimeo SD2VideoUrl,getting value SD2VideoUrl null");
//				return false;
//			}*/
//			if(videoContentBean.getThumbnailUrl() == null) {
//				//no thumbnail found
//				videoContentBean.setThumbnailUrl("https://studentzone-ngasce.nmims.edu/acads/resources_2015/images/thumbnailLogo.png");
//			}
//			ContentDAO dao = (ContentDAO) act.getBean("contentDAO");
//			if("success".equalsIgnoreCase(dao.addIntoVideoContent(videoContentBean))) {
//				recording_status.successRecordingStatus(recordingStatus.getMeetingId());
//				return true;
//			}
//			else {
//				// failed to insert data in video_content
//				recording_status.updateVimeoStatus(recordingStatus.getMeetingId(), "Error while inserting data into video_content table");
//				return false;
//			} 
//	}
	
	@RequestMapping(value = "/admin/sessionRecordingPanel", method = {RequestMethod.GET, RequestMethod.POST})
	public ModelAndView sessionRecordingPanel(HttpServletRequest request) throws IOException {
		ModelAndView mv = new ModelAndView("zoomVimeoIntegration");
		TimeTableDAO recordingStatusDAO = (TimeTableDAO)act.getBean("timeTableDAO");
		List<SessionDayTimeAcadsBean> sessionList = recordingStatusDAO.getSessionRecordingList();
		if(sessionList == null) {
			//set error for panel
			mv.addObject("error","Error: Null response from session recording table.");
		}
		mv.addObject("sessions", sessionList);
		return mv;
	}
	
	/*@RequestMapping(value = "/ZoomVimeoUploadStatus", method = {RequestMethod.GET, RequestMethod.POST})
	public String uploadToUploadStatus() throws IOException {
		VimeoManager vimeo = new VimeoManager(); 
		TimeTableDAO recording_status = (TimeTableDAO)act.getBean("timeTableDAO");
		List<RecordingStatus> recordingStatusList = recording_status.getPendingRecordingData();
		int i=0;
		for (RecordingStatus recordingStatus : recordingStatusList) {
			try {
				if(recordingStatus.getVimeoId() == null) {
					continue;
				}
				JsonObject vimeoResponse = vimeo.checkUploadVideoStatus(recordingStatus.getVimeoId());
				insertIntoVideoContent(vimeoResponse, recordingStatus);
				i++;
			}
			catch (Exception e) {
				// TODO: handle exception
				  
			}
		}
		return "TestingPingRequest";
	}*/
	
	
	/*@RequestMapping(value = "/ZoomVimeoUpload", method = {RequestMethod.GET, RequestMethod.POST})
	public String uploadToUpload() throws IOException {
//		TimeTableDAO sessions = (TimeTableDAO)act.getBean("timeTableDAO");
//		VimeoManager vimeo = new VimeoManager(); 
//		vimeo.uploadVideo("https://ngasce.zoom.us/recording/download/a9adnhc3p21WLxBtkgtBj1lbWU45Vf2aocGvau4ODD-xOcDdwtihbMyEffBJ3laP", "Testing auto upload zoom video", "2019-01-06", "630105554", sessions);
		
		DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd");
		DateTimeFormatter timeFormat = DateTimeFormatter.ofPattern("HH:mm:ss");
		LocalDateTime now = LocalDateTime.now();
		LocalDateTime nowTime = now.minusMinutes(30);
		String date = dtf.format(now);
		String time = timeFormat.format(nowTime);
		//date = "2019-07-10";
		TimeTableDAO recording_status = (TimeTableDAO)act.getBean("timeTableDAO");
		List<SessionDayTimeBean> sessionList = recording_status.getSessionsUserIdForDate(date,time);	//get session data from session table, required for zoom api
		ZoomManager zoom = new ZoomManager();
		int i =0;
		for (SessionDayTimeBean attendList : sessionList) {
			if(!recording_status.createRecordingStatusEntry(attendList.getMeetingKey(),attendList.getId())) {	// initiate record entry
				continue;
			}
			this.getFromZoomAndUploadToVimeo(zoom,recording_status,attendList);
		}
		
		
		return "TestingPingRequest"; 
	}*/
	
//	private boolean getFromZoomAndUploadToVimeo(ZoomManager zoom,TimeTableDAO recording_status,SessionDayTimeBean sessionsData) throws IOException {
//		ResponseEntity<String> zoomResponse;
//		JsonObject jsonObj;
//
//		try {
//			zoomResponse = zoom.getZoomRecordingList(recording_status,sessionsData.getMeetingKey());	//get zoom webinar recording list.
//			if(zoomResponse == null) {
//				return false;	// error from zoom api, error save in recording_status table
//			}
//			jsonObj = new JsonParser().parse(zoomResponse.getBody()).getAsJsonObject();
//			if ("200".equalsIgnoreCase(zoomResponse.getStatusCode().toString())) {
//				if(jsonObj.get("recording_files") == null) {
//					// error no recording found.
//					recording_status.errorRecordingStatus(sessionsData.getMeetingKey(), jsonObj.get("message").getAsString());
//					return false;
//				}
//				
//				if(jsonObj.get("recording_count").getAsInt() <= 0) {
//					recording_status.errorRecordingStatus(sessionsData.getMeetingKey(), "No cloud recording found");	// error no cloud recording found.
//				}else if(jsonObj.get("recording_count").getAsInt() > 3) {
//					recording_status.errorRecordingStatus(sessionsData.getMeetingKey(), "No cloud recording found");	//error multiple cloud recording found.
//				}else {
//					boolean mp4flag = false;
//					for (JsonElement recordings : jsonObj.get("recording_files").getAsJsonArray()) {
//						JsonObject recording = recordings.getAsJsonObject();
//						if(recording.get("file_type").getAsString().equals("MP4")) {
//							mp4flag = true;
//							//pass download url to vimeo to upload video on it.
//							VimeoManager vimeo = new VimeoManager();
//							vimeo.uploadVideo(recording_status, sessionsData.getSubject() + "-" + sessionsData.getSessionName(), recording.get("download_url").getAsString(),sessionsData.getMeetingKey());
//							//vimeo.uploadVideo(videoType,recording.get("download_url").getAsString(), "Testing auto upload zoom video", date, meeting.get("id").getAsString(), sessions);
//							return true;
//							//send to video download link
//						}
//					}
//					if(!mp4flag) {
//						recording_status.errorRecordingStatus(sessionsData.getMeetingKey(), "No Mp4 zoom cloud recording file found");
//					}
//				}
//			
//			}else {
//				recording_status.errorRecordingStatus(sessionsData.getMeetingKey(), "Invalid repsonse from zoom,Status:" + zoomResponse.getStatusCode().toString());	//response is error from zoom
//			}
//			return false;
//		}
//		catch(Exception e) {
//			recording_status.errorRecordingStatus(sessionsData.getMeetingKey(), "Error : " + e.getMessage());
//			return false;
//		}
//	}
	
	
	@RequestMapping(value = "/gotoStudentHome", method = {RequestMethod.GET, RequestMethod.POST})
	public ModelAndView gotoStudentHome(HttpServletRequest request, HttpServletResponse response) {
		
		if(!checkSession(request, response)){
			return new ModelAndView("studentPortalRediret");
		}
		return new ModelAndView("studentHome");
	}

	
	@RequestMapping(value = "/refreshStudentDetails", method = {RequestMethod.GET, RequestMethod.POST})
	public String refreshStudentDetailsInSSO(HttpServletRequest request, HttpServletResponse response) {
		resetStudentInSession(request, response);
		return null;
	}
	
	@RequestMapping(value = "/loginforSSO", method = {RequestMethod.GET, RequestMethod.POST})
	public @ResponseBody String loginforSSO(HttpServletRequest request, HttpServletResponse respnse) throws Exception {
		try {

			//String userId = (String)request.getSession().getAttribute("userId_acads");

			String emailId = "";
			Boolean logout = false;
			request.getSession().setAttribute("logout", logout);
			
			//request.getSession().setAttribute("userId_acads", principal.getName());

			//String userId = principal.getName();
			
			request.getSession().setAttribute("validityExpired","No");
			request.getSession().setAttribute("earlyAccess", "No");
			ContentDAO cDao = (ContentDAO)act.getBean("contentDAO");
			String userIdEncrypted = request.getParameter("uid");
			//String emailIdEncrypted = request.getParameter("emailId");
			String userId = AESencrp.decrypt(userIdEncrypted);
			
			if(userId.equals(request.getSession().getAttribute("userId_acads")) ){
				//Session already created. Don't fire another query on DB
				return null;
			}
			//if(!StringUtils.isBlank(emailIdEncrypted))
			//emailId = AESencrp.decrypt(emailIdEncrypted);
			//added to fetch the email details form the student portal
			
			if(isEmail(userId)) {
				emailId = userId;
				userId = "77999999999";
		    	request.getSession().setAttribute("isLoginAsLead", "true");
			}
			else
				request.getSession().setAttribute("isLoginAsLead", "fasle");
			
			request.getSession().setAttribute("emailId", emailId);
			request.getSession().setAttribute("userId_acads", userId);
			//Used to get a login over all the page 
			
			TimeTableDAO tdao = (TimeTableDAO)act.getBean("timeTableDAO");
//			HashMap<String,BigDecimal> examOrderMap = tdao.getExamOrderMapper();
			StudentAcadsBean student = cDao.getSingleStudentsData(userId);
			
			if(isEmail(emailId)) {
				student = leadDAO.getLeadsFromSalesForce(emailId, student);
			}
			
		    //boolean makeLive= false;
			//check student already registered with salesforce's active re-reg month & year
			/*
			 * ReRegistrationBean activeRegistration =
			 * sfdc.getActiveReRegistrationFromSalesForce(); if(
			 * !activeRegistration.isError()) { boolean alreadyRegistered =
			 * cDao.ifStudentAlreadyRegisteredForNextSem(userId,activeRegistration);
			 * if(!alreadyRegistered) { //check validity of re-reg SimpleDateFormat sdformat
			 * = new SimpleDateFormat("yyyy-MM-dd");
			 * 
			 * Date startdate = sdformat.parse(activeRegistration.getStartTime()); Date
			 * endDate = sdformat.parse(activeRegistration.getEndTime());
			 * 
			 * Date date = new Date(); Date now = sdformat.parse(sdformat.format(date));
			 * 
			 * if(now.compareTo(startdate) > 0 && endDate.compareTo(now) > 0 ) { makeLive =
			 * true; } } }
			 */
		
			//request.getSession().setAttribute("ifReRegistrationActive", makeLive); 
			/* 
			double examOrderDifference = examOrderMap.get(student.getEnrollmentMonth()+student.getEnrollmentYear()).doubleValue() - examOrderMap.get("Apr"+student.getEnrollmentYear()).doubleValue();
			*/
			request.getSession().setAttribute("student_acads", student);
			
			boolean isValid = isStudentValid(student, userId);
			if(!isValid){
				request.getSession().setAttribute("validityExpired","Yes");
			}
			
			/*List<AnnouncementBean> announcements = cDao.getAllActiveAnnouncements();*/
			//Added for SAS
			List<AnnouncementAcadsBean> announcements = null;
			if(student !=null){
				announcements = cDao.getAllActiveAnnouncements(student.getProgram(),student.getPrgmStructApplicable());
			}else{
				announcements = cDao.getAllActiveAnnouncements();
			}
			
			List<ExamOrderAcadsBean> liveFlagList = cDao.getLiveFlagDetails();
			HashMap<String,BigDecimal> examOrderMap = generateExamOrderMap(liveFlagList);
			
			request.getSession().setAttribute("announcementsAcads", announcements);
			
			PersonAcads person = new PersonAcads();
			person.setUserId(userId);
			if(student != null){
				double examOrderDifference = 0.0;
				double getExamOrderOfProspectiveBatch = examOrderMap.get(student.getEnrollmentMonth()+student.getEnrollmentYear()) !=null ?examOrderMap.get(student.getEnrollmentMonth()+student.getEnrollmentYear()).doubleValue():0.0;
				double getMaxOrderWhereContentLive = cDao.getMaxOrderWhereContentLive();
				examOrderDifference = getExamOrderOfProspectiveBatch - getMaxOrderWhereContentLive;
				
				if(examOrderDifference == 1){
			    	request.getSession().setAttribute("earlyAccess","Yes");
			    }
			    boolean isCertificate = isStudentOfCertificate(student.getProgram());
				request.getSession().setAttribute("isCertificate", isCertificate);
				studentService.mgetWaivedOffSubjects(student);

					
					
				request.getSession().setAttribute("student_acads", student);
				
				
				person.setFirstName(student.getFirstName());
				person.setLastName(student.getLastName());
				person.setProgram(student.getProgram());
				person.setEmail(student.getEmailId());
				person.setContactNo(student.getMobile());
				
				performCSStudentChecks(request, userId, student);
				courseraCheck(request, userId, student);
				
				studentService.mgetWaivedInSubjects(student);
				request.getSession().setAttribute("student_acads", student);
				
			}/*else{
				//Admin user. Fetch information from LDAP
				LDAPDao dao = (LDAPDao)act.getBean("ldapdao");
				person = dao.findPerson(userId);
				person.setUserId(userId);
				request.getSession().setAttribute("user_acads", person);
				
				//Fetch and store User Authorization in session
				UserAuthorizationBean userAuthorization = cDao.getUserAuthorization(userId);
				if(userAuthorization == null){
					userAuthorization = new UserAuthorizationBean();
				}
				
				ArrayList<String> authorizedCenterCodes = cDao.getAuthorizedCenterCodes(userAuthorization);//List of all center codes
				String commaSeparatedAuthorizedCenterCodes = StringUtils.join(authorizedCenterCodes.toArray(), ",");//Comma separated center codes
				
				userAuthorization.setAuthorizedCenterCodes(authorizedCenterCodes);
				userAuthorization.setCommaSeparatedAuthorizedCenterCodes(commaSeparatedAuthorizedCenterCodes);
				
				request.getSession().setAttribute("userAuthorization", userAuthorization);
			}*/
			if(!userId.startsWith("77") && !userId.startsWith("79")){
				
				//Fetch and store User Authorization in session
				UserAuthorizationBean userAuthorization = cDao.getUserAuthorization(userId);
				
				//Admin user. Fetch information from LDAP
				LDAPDao dao = (LDAPDao)act.getBean("ldapdao");
				try {
					person = dao.findPerson(userId);
					
					if(person.getRoles().equalsIgnoreCase("Faculty")) {
					
						FacultyAcadsBean faculty = cDao.isFaculty(userId);
					
						person.setDisplayName(faculty.getFirstName() + faculty.getLastName());
						person.setEmail(faculty.getEmail());
						person.setFirstName(faculty.getFirstName());
						person.setLastName(faculty.getLastName());
						person.setContactNo(faculty.getMobile());
						person.setAltContactNo(faculty.getAltContact());
					}
					
				} catch (Exception e) {
					//Check if faculty
					try {
						FacultyAcadsBean faculty = cDao.isFaculty(userId);

						person.setRoles("Faculty");
						person.setDisplayName(faculty.getFirstName() + faculty.getLastName());
						person.setEmail(faculty.getEmail());
						person.setFirstName(faculty.getFirstName());
						person.setLastName(faculty.getLastName());
						person.setPassword("ngasce@admin20");
						person.setPostalAddress(faculty.getAddress());
						person.setUserId(userId);
					}catch(Exception ex) {
						person.setDisplayName("");
						person.setEmail("");
						person.setFirstName("");
						person.setLastName("");
						person.setPassword("ngasce@admin20");
						person.setPostalAddress("");
						person.setUserId(userId);
						person.setRoles(userAuthorization.getRoles());
					}
				}
				person.setUserId(userId);
				request.getSession().setAttribute("user_acads", person);
				
				
				
				if(userAuthorization == null){
					userAuthorization = new UserAuthorizationBean();
				}
				
				ArrayList<String> authorizedCenterCodes = cDao.getAuthorizedCenterCodes(userAuthorization);//List of all center codes
				String commaSeparatedAuthorizedCenterCodes = StringUtils.join(authorizedCenterCodes.toArray(), ",");//Comma separated center codes
				
				userAuthorization.setAuthorizedCenterCodes(authorizedCenterCodes);
				userAuthorization.setCommaSeparatedAuthorizedCenterCodes(commaSeparatedAuthorizedCenterCodes);

//				following field added for ELearn Resources  validation via roles and provider name 
				ELearnResourcesAcadsBean eLearnResourcesBean = cDao.isStukentApplicable(userId);
				request.getSession().setAttribute("isStukentLtiRoles",eLearnResourcesBean.getRoles() );
				request.getSession().setAttribute("isStukentLtiProviderName",eLearnResourcesBean.getProvider_name());
				request.getSession().setAttribute("isStukentLtiUserIdCount",eLearnResourcesBean.getUserId_count());

				
				ELearnResourcesAcadsBean elearnResourcesBean = cDao.isHarvardApplicable(userId);
				request.getSession().setAttribute("isHarvardLtiRoles",elearnResourcesBean.getRoles() );
				request.getSession().setAttribute("isHarvardLtiProviderName",elearnResourcesBean.getProvider_name());
				request.getSession().setAttribute("isHarvardLtiUserIdCount",elearnResourcesBean.getUserId_count());

				
				//Perform user checks for CS  users.
				csDAO.performCSAffiliateUserChecks(request, userId);
				
				request.getSession().setAttribute("userAuthorization", userAuthorization);
			}
			request.getSession().setAttribute("user_acads", person);
			//start - Added for LiveSessionAccess
			ArrayList<Integer> currentSemPSSId = null; //new ArrayList<String>();
			StudentAcadsBean registration = null; //new StudentBean();
			/*boolean isNonPG_Program = Boolean.FALSE;*/
			boolean isFreeLiveSessionApplicable=Boolean.FALSE;
			try {
				registration = cDao.getStudentMaxSemRegistrationData(userId); //getStudentRegistrationDataForCurrentCycle
				request.getSession().setAttribute("studentRegData", registration);
				//Set up latest semester
				student.setSem(registration.getSem());
				//ArrayList<String> waivedOffSubjects = studentService.mgetWaivedOffSubjects(student);
				//currentSemPSSId = cDao.getPSSIds(registration.getConsumerProgramStructureId(),registration.getSem() ,waivedOffSubjects);
				currentSemPSSId = studentCourseMappingService.getPSSID(student.getSapid(),registration.getMonth(),registration.getYear());
				request.getSession().setAttribute("currentSemPSSId", currentSemPSSId);
			
				String enrollDate = ("01/" + student.getEnrollmentMonth() + "/" + student.getEnrollmentYear());
				/*isNonPG_Program = nonPG_ProgramList.contains(registration.getProgram());*/
					ArrayList<String>listOfMaterKeyHavingFreeLiveSession	=studentService.getListOfLiveSessionAccessMasterKeys(TIMEBOUND_PORTAL_LIST);
					isFreeLiveSessionApplicable=	listOfMaterKeyHavingFreeLiveSession.contains(registration.getConsumerProgramStructureId());
				logger.info("(Enrollment Year/Month, LiveSessionAccessDate, Program, NonPG_Program) : ("
						+ enrollDate + "," + LIVE_SESSION_ACCESS_DATE + "," + registration.getProgram() + "," + isFreeLiveSessionApplicable);
				if (DateTimeHelper.checkDate(DateTimeHelper.FORMAT_ddMMMyyyy, enrollDate, DateTimeHelper.FORMAT_ddMMMyyyy,
						LIVE_SESSION_ACCESS_DATE)  || isFreeLiveSessionApplicable) {
					request.getSession().setAttribute("liveSessionPssIdAccess_acads", currentSemPSSId);
				} else {
					List<Integer> list = null;
					list = studentService.fetchPSSforLiveSessionAccess(userId);
					request.getSession().setAttribute("liveSessionPssIdAccess_acads", list);
				}
			} catch (Exception e) {
				// TODO: handle exception
				  
				logger.info("HomeController : loginforSSO : Error : " + e.getMessage());
			}
			//end - Added for LiveSessionAccess
			
			//Set student registration details to session for Session Video Home
			try {
				String year = CURRENT_ACAD_YEAR;
				String month = CURRENT_ACAD_MONTH;
				
				//Get max order of academic session live
				double acadSessionLiveOrder = ExamOrderUtil.getMaxOrderOfAcadSessionLive(liveFlagList);
				double maxOrderWhereContentLive=ExamOrderUtil.getMaxOrderOfAcadContentLive(liveFlagList);
				//Get student registered year and month order
				double reg_order =  examOrderMap.get(registration.getMonth()+registration.getYear()).doubleValue();
				double current_order =examOrderMap.get(CURRENT_ACAD_MONTH+CURRENT_ACAD_YEAR).doubleValue();
				
				request.getSession().setAttribute("reg_order", reg_order);
				request.getSession().setAttribute("acadContentLiveOrder",maxOrderWhereContentLive);
				request.getSession().setAttribute("current_order",current_order);
				//If the acadSessionLiveOrder and reg_order is equals then set registered year and month as currentSessionCycle
				//otherwise set CURRENT_ACAD_YEAR and CURRENT_ACAD_MONTH as currentSessionCycle
				if(acadSessionLiveOrder == reg_order) {
					year = registration.getYear();
					month = registration.getMonth();
				}

				request.getSession().setAttribute("currentSem", registration.getSem());

				request.getSession().setAttribute("currentSessionCycle", (month+year));
			}catch (Exception e) {
				logger.info("HomeController : loginforSSO : Error : " + e.getMessage());
			}
			
			return null;
		
		} catch (Exception e) {
			  
		}
		
		return null;
	}
	
	@RequestMapping(value = "/logout", method = {RequestMethod.GET, RequestMethod.POST})
	public ModelAndView logout(HttpServletRequest request, HttpServletResponse response) {
		
		request.getSession().invalidate();
		ModelAndView modelnView = new ModelAndView("login");
		return modelnView;
	}
	

	@RequestMapping(value = "/logoutforSSO", method = {RequestMethod.GET, RequestMethod.POST})
	public @ResponseBody String logoutforSSO(HttpServletRequest request, HttpServletResponse respnse) throws Exception {
		
		request.getSession().invalidate();
		return null;
	}
	
	@RequestMapping(value = "/authenticate", method = RequestMethod.POST)
	public ModelAndView home(HttpServletRequest request, HttpServletResponse respnse) throws Exception {

		LDAPDao dao = (LDAPDao)act.getBean("ldapdao");
		ModelAndView modelnView = new ModelAndView("home");

		String userId = request.getParameter("userId");
		String password = request.getParameter("password");

		boolean authenticated = dao.login(userId, password);
		if(authenticated){
			modelnView = new ModelAndView("home");
			request.getSession().setAttribute("userId_acads", userId);
			request.getSession().setAttribute("password", password);
			PersonAcads person = dao.findPerson(userId);
			person.setUserId(userId);
			modelnView.addObject("displayName", person.getDisplayName() );
			request.getSession().setAttribute("user_acads", person);
			
			String roles = person.getRoles();
			if(userId.startsWith("77") || userId.startsWith("79")){
				modelnView = new ModelAndView("studentHome");
			}
		}else{
			modelnView = new ModelAndView("login");
			request.setAttribute("error", "true");
			request.setAttribute("errorMessage", "Invalid Credentials. Please re-try.");
		}
		return modelnView;
	}
	
	@RequestMapping(value = "/studentHome", method = {RequestMethod.GET, RequestMethod.POST})
	public ModelAndView studentHome(HttpServletRequest request, HttpServletResponse respnse) throws Exception {

		LDAPDao dao = (LDAPDao)act.getBean("ldapdao");
		ModelAndView modelnView = new ModelAndView("studentHome");

		String sapIdEncrypted = request.getParameter("sapId");
		String userId = "NA";
		try {
			userId = AESencrp.decrypt(sapIdEncrypted);
			request.getSession().setAttribute("userId_acads", userId);
			
			ContentDAO cDao = (ContentDAO)act.getBean("contentDAO");
			StudentAcadsBean student = cDao.getSingleStudentsData(userId);
			request.getSession().setAttribute("student_acads", student);
			
			
			PersonAcads person = dao.findPerson(userId);
			//Person person = new Person();
			person.setUserId(userId);
			modelnView.addObject("displayName", person.getDisplayName() );
			request.getSession().setAttribute("user_acads", person);
			request.getSession().setAttribute("userId_acads", userId);
			
			//Fetch and store User Authorization in session
			UserAuthorizationBean userAuthorization = cDao.getUserAuthorization(userId);
			if(userAuthorization == null){
				userAuthorization = new UserAuthorizationBean();
			}
			
			ArrayList<String> authorizedCenterCodes = cDao.getAuthorizedCenterCodes(userAuthorization);//List of all center codes
			String commaSeparatedAuthorizedCenterCodes = StringUtils.join(authorizedCenterCodes.toArray(), ",");//Comma separated center codes
			
			userAuthorization.setAuthorizedCenterCodes(authorizedCenterCodes);
			userAuthorization.setCommaSeparatedAuthorizedCenterCodes(commaSeparatedAuthorizedCenterCodes);
			
			request.getSession().setAttribute("userAuthorization", userAuthorization);
			
			String roles = person.getRoles();
			if(roles != null && (roles.indexOf("Faculty") != -1 || roles.indexOf("Acads Admin") != -1 )){
				return new ModelAndView("home");
			}
			
			
		}catch (Exception e) {
			  
		}
		return modelnView;
	}

	@RequestMapping(value = "/loginAsForm", method = {RequestMethod.GET, RequestMethod.POST})
	public String loginAsForm(HttpServletRequest request, HttpServletResponse respnse) throws Exception {
		return "loginAs";
	}
	
	@RequestMapping(value = "/queryForm", method = {RequestMethod.GET, RequestMethod.POST})
	public String queryForm(HttpServletRequest request, HttpServletResponse respnse) throws Exception {
		return "query";
	}
	
	@RequestMapping(value = "/query", method = {RequestMethod.GET, RequestMethod.POST})
	public ModelAndView query(HttpServletRequest request, HttpServletResponse response){
		ModelAndView modelnView = new ModelAndView("query");

		String sql = request.getParameter("sql");
		//StudentMarksDAO dao = (StudentMarksDAO)act.getBean("studentMarksDAO");
		
		try {
			//dao.execute(sql);
			request.setAttribute("success","true");
			request.setAttribute("successMessage","Query executed successfully");
		} catch (Exception e) {
			request.setAttribute("error", "true");
			request.setAttribute("errorMessage", "Error in running query.");
		}
		


		return modelnView;
	}
	
	
	public boolean isEmail(String email) 
    { 
		Pattern pattern = Pattern.compile("[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,4}");
        Matcher mat = pattern.matcher(email);
        boolean match =  mat.matches();
        return match;
    } 
	
	
	@RequestMapping(value = "/loginAs", method = {RequestMethod.GET, RequestMethod.POST})
	public ModelAndView loginAs(HttpServletRequest request, HttpServletResponse respnse) throws Exception {
		String loginAs = request.getParameter("userId");
		if(isEmail(loginAs))
	    	 request.getSession().setAttribute("isLoginAsLead", "true");
		else
			request.getSession().setAttribute("isLoginAsLead", "fasle");
		String sapIdEncrypted = AESencrp.encrypt(loginAs);
		Map model = new HashMap();
		model.put("sapId", sapIdEncrypted);
		
		return new ModelAndView(new RedirectView("studentHome"), model);
	}
	
	
	
	@RequestMapping(value = "/changePassword", method = {RequestMethod.GET, RequestMethod.POST})
	public String changePassword(HttpServletRequest request, HttpServletResponse response) {
		if(!checkSession(request, response)){
			return "login";
		}
		logger.info("Sending to change password page");
		return "changePassword";
	}
	

	
	@RequestMapping(value = "/savePassword", method = {RequestMethod.GET, RequestMethod.POST})
	public ModelAndView savePassword(HttpServletRequest request, HttpServletResponse respnse) {
		if(!checkSession(request, respnse)){
			return new ModelAndView("login");
		}
		
		String password = request.getParameter("password");
		ModelAndView modelnView = null;

		String userId = (String)request.getSession().getAttribute("userId_acads");
		
		LDAPDao dao = (LDAPDao)act.getBean("ldapdao");
		try{
			dao.changePassword(password, userId);
			modelnView = new ModelAndView("home");
			request.setAttribute("success","true");
			request.setAttribute("successMessage","Password changed successfully.");
			request.getSession().setAttribute("password",password);
			
		}catch(Exception e){
			modelnView = new ModelAndView("changePassword");
			request.setAttribute("error", "true");
			request.setAttribute("errorMessage", "New password does not meet password policy OR is similar to past/default password. Please set password as per policy.");

		}
		
		return modelnView;
	}
	private boolean isStudentValid(StudentAcadsBean student, String userId) throws ParseException {
		if(userId.startsWith("77")){
			String validityEndMonthStr = student.getValidityEndMonth();
			int validityEndYear = Integer.parseInt(student.getValidityEndYear());
			Date lastAllowedAcccessDate = null;
			int validityEndMonth = 0;
			if("Jun".equals(validityEndMonthStr)){
				validityEndMonth = 6;
				 String date = validityEndYear + "/" + validityEndMonth + "/" + "30";
				 lastAllowedAcccessDate = formatter.parse(date);
			}else if("Dec".equals(validityEndMonthStr)){
				validityEndMonth = 12;
				 String date = validityEndYear + "/" + validityEndMonth + "/" + "31";
				 lastAllowedAcccessDate = formatter.parse(date);
			}else if("Sep".equals(validityEndMonthStr)){
				validityEndMonth = 9;
				 String date = validityEndYear + "/" + validityEndMonth + "/" + "30";
				 lastAllowedAcccessDate = formatter.parse(date);
			}else if("Apr".equals(validityEndMonthStr)){
				validityEndMonth = 4;
				 String date = validityEndYear + "/" + validityEndMonth + "/" + "30";
				 lastAllowedAcccessDate = formatter.parse(date);
			}else if("Aug".equals(validityEndMonthStr)){
				validityEndMonth = 8;
				 String date = validityEndYear + "/" + validityEndMonth + "/" + "31";
				 lastAllowedAcccessDate = formatter.parse(date);
			}else if("Oct".equals(validityEndMonthStr)){
				validityEndMonth = 10;
				 String date = validityEndYear + "/" + validityEndMonth + "/" + "31";
				 lastAllowedAcccessDate = formatter.parse(date);
			}else if("Feb".equals(validityEndMonthStr)){
				validityEndMonth = 2;
				 String date = validityEndYear + "/" + validityEndMonth + "/" + "28";
				 lastAllowedAcccessDate = formatter.parse(date);
			}else if("Mar".equals(validityEndMonthStr)){
				validityEndMonth = 3;
				 String date = validityEndYear + "/" + validityEndMonth + "/" + "31";
				 lastAllowedAcccessDate = formatter.parse(date);
			}else if("Jan".equals(validityEndMonthStr)){
				validityEndMonth = 1;
				String date = validityEndYear + "/" + validityEndMonth + "/" + "31";
				lastAllowedAcccessDate = formatter.parse(date);
			}else if("May".equals(validityEndMonthStr)){
				validityEndMonth = 5;
				String date = validityEndYear + "/" + validityEndMonth + "/" + "31";
				lastAllowedAcccessDate = formatter.parse(date);
			}else if("Jul".equals(validityEndMonthStr)){
				validityEndMonth = 7;
				String date = validityEndYear + "/" + validityEndMonth + "/" + "31";
				lastAllowedAcccessDate = formatter.parse(date);
			}
			
			Calendar now = Calendar.getInstance();
		    int currentExamYear = now.get(Calendar.YEAR);
		    int currentExamMonth = (now.get(Calendar.MONTH) + 1);
		    
		    if(currentExamYear < validityEndYear  ){
		    	return true;
		    }else if(currentExamYear == validityEndYear && currentExamMonth <= validityEndMonth){
		    	return true;
		    }else{
				Date currentDate = new Date();
				GregorianCalendar cal = new GregorianCalendar();
				cal.setTime(lastAllowedAcccessDate);
				
				if(currentDate.before(cal.getTime())){
					return true;
				}else {
					return false;
				}
				
				//Commented by Somesh on 25-08-2021, Removed additional days portal access after validity end
				/*
				if (student.getProgram().equals("EPBM") && student.getEnrollmentYear().equals("2018") && student.getEnrollmentMonth().equals("Jan") ) {
					cal.add(Calendar.DATE, 242);//Allow access till 1 July 2019 For SAS-Jan/2018 batch --> Requirement of Nelson sir
					if(currentDate.before(cal.getTime())){
							return true;//Allow 242 additional days access from Validity End Date
						}
						
						
				}else if (student.getProgram().equals("MPDV") && student.getEnrollmentYear().equals("2018") && student.getEnrollmentMonth().equals("Jan") ) {
					cal.add(Calendar.DATE, 303);//Allow access till 1 July 2019 For SAS-Jan/2018 batch --> Requirement of Nelson sir
					if(currentDate.before(cal.getTime())){
						return true;//Allow 303 additional days access from Validity End Date
					}
					
					
				}else if (student.getProgram().equals("EPBM") && student.getEnrollmentYear().equals("2018") && student.getEnrollmentMonth().equals("Jul") ){
					cal.add(Calendar.DATE, 93);//Allow access  till 1 July 2019 For SAS-Jul/2018 batch --> Requirement of Nelson sir
					if(currentDate.before(cal.getTime())){
						return true;//Allow 63 additional days access from Validity End Date
					}
				
				}else if (student.getProgram().equals("MPDV") && student.getEnrollmentYear().equals("2018") && student.getEnrollmentMonth().equals("Jul") ){
					cal.add(Calendar.DATE, 182);//Allow access  till 1 July 2019 For SAS-Jul/2018 batch --> Requirement of Nelson sir
					if(currentDate.before(cal.getTime())){
						return true;//Allow 182 additional days access from Validity End Date
					}
				
				}else{
					cal.add(Calendar.DATE, 45);//Allow access 45 days after validity end date
						if(currentDate.before(cal.getTime())){
							return true;//Allow 45 additional days access from Validity End Date
						}
					}
				return false;
				*/
			}
			
			
		}else{
			//Admin Staff login
			return true;
		}
		
	}
	
	private HashMap<String, BigDecimal> generateExamOrderMap(List<ExamOrderAcadsBean> liveFlagList) {
		HashMap<String, BigDecimal> orderMap = new HashMap<String, BigDecimal>();
		for (ExamOrderAcadsBean row : liveFlagList) {
			orderMap.put(row.getMonth()+row.getYear(),BigDecimal.valueOf((long)Double.parseDouble( row.getOrder())));
			orderMap.put(row.getAcadMonth()+row.getYear(),BigDecimal.valueOf((long)Double.parseDouble( row.getOrder())));
		}
		return orderMap;
	}
	
	@RequestMapping(value = "/m/viewSessionVideo", method = RequestMethod.GET)
	public ModelAndView viewSessionVideo(HttpServletRequest request) {
		ModelAndView m = new ModelAndView("viewSessionVideo");
		m.addObject("videoUrl", request.getParameter("videoUrl")+"#t="+request.getParameter("startTime"));
		return m;
	}
	
	@RequestMapping(value = "/m/testingMobileNotification", method = RequestMethod.GET)
	public ModelAndView testingMobileNotification(HttpServletRequest request) {
		ModelAndView m = new ModelAndView("RequestTesting");
		if(request.getParameter("token") == null) {
			m.addObject("status", "Invalid token found");
			return m;
		}
		List<StudentAcadsBean> studentList = new ArrayList<StudentAcadsBean>();
		StudentAcadsBean student = new StudentAcadsBean();
		student.setFirebaseToken(request.getParameter("token"));
		studentList.add(student);
		SessionDayTimeAcadsBean session = new SessionDayTimeAcadsBean();
		session.setSubject("NGASCE");
		session.setSessionName("");

		mobileNotificationHelper.sendSessionNotification(session, studentList);
		m.addObject("status", "Successfully send push notification");
		return m;
	}
	
	@RequestMapping(value = "/uploadSessionRecorindAudio", method = RequestMethod.GET)
	public ModelAndView uploadSessionRecordingAudio(HttpServletRequest request) {
		ModelAndView m = new ModelAndView("RequestTesting");
		VideoContentDAO video_content = (VideoContentDAO) act.getBean("videoContentDAO");
		VideoContentAcadsBean videoContentBean =  video_content.getVideoContentWithZoomAudioLinkById(Integer.parseInt(request.getParameter("id")));
		if(videoContentBean != null) {
			try {
				videoContentService.uploadSessionRecordingAudio(videoContentBean);
			} catch (Exception e) {
				  
			}
		}
		return m;
	}
	
	@RequestMapping(value = "/admin/getadmin", method = RequestMethod.GET)
	public void getadmin() {
//		System.out.println("admin");
	}
	
	@RequestMapping(value = "/student/getstudent", method = RequestMethod.GET)
	public void getstudent() {
//		System.out.println("student");
	}
	                      
	@RequestMapping(value = "/loginforSSO_new", method = {RequestMethod.GET, RequestMethod.POST})
	public @ResponseBody String loginforSSO_new(HttpServletRequest request, HttpServletResponse respnse,@RequestBody LoginSSODto loginDetails) throws Exception {
		try {
			String emailId = "";
			Boolean logout = false;
			request.getSession().setAttribute("logout", logout);
			
			request.getSession().setAttribute("validityExpired",loginDetails.getValidityExpired());
			request.getSession().setAttribute("earlyAccess", loginDetails.getEarlyAccess());
			ContentDAO cDao = (ContentDAO)act.getBean("contentDAO");
			String userIdEncrypted = request.getParameter("uid");
			
			String userId = AESencrp.decrypt(userIdEncrypted);
			System.out.println("ACADS APP: User logged in "+userId);
			if(userId.equals(request.getSession().getAttribute("userId_acads")) ){
				//Session already created. Don't fire another query on DB
				return null;
			}
			
			if(isEmail(userId)) {
				emailId = userId;
				userId = "77999999999";
			}
		
			request.getSession().setAttribute("isLoginAsLead", loginDetails.getIsLoginAsLead());
			request.getSession().setAttribute("emailId", emailId);
			request.getSession().setAttribute("userId_acads", userId);
			//Used to get a login over all the page 
			
			StudentAcadsBean student = loginDetails.getStudent();

			if(isEmail(emailId)) {
				student = leadDAO.getLeadsFromSalesForce(emailId, student);
			}
			
			request.getSession().setAttribute("student_acads", student);
			
			//Added for SAS
			List<AnnouncementAcadsBean> announcements = loginDetails.getAnnouncements();
			request.getSession().setAttribute("announcementsAcads", announcements);
			PersonAcads person = loginDetails.getPersonDetails();
			
			
			if(student != null){
				
			    boolean isCertificate = isStudentOfCertificate(student.getProgram());
				request.getSession().setAttribute("isCertificate", isCertificate);
				
				courseraCheck(request, userId, student); //coursera check
				
				//CS session set attriburtes
				request.getSession().setAttribute("consumerProgramStructureHasCSAccess", loginDetails.isConsumerProgramStructureHasCSAccess());
				request.getSession().setAttribute("CSFeatureAccess", loginDetails.getFeatureViseAccess());
				
			}
			
			if(!userId.startsWith("77") && !userId.startsWith("79")){
				
				if(person.getRoles().equalsIgnoreCase(insofeRole))
					person.setRoles(facultyRole);
				
				//Fetch and store User Authorization in session
				UserAuthorizationBean userAuthorization = cDao.getUserAuthorization(userId);
				
				if(userAuthorization == null){
					userAuthorization = new UserAuthorizationBean();
				}
				
				ArrayList<String> authorizedCenterCodes = cDao.getAuthorizedCenterCodes(userAuthorization);//List of all center codes
				String commaSeparatedAuthorizedCenterCodes = StringUtils.join(authorizedCenterCodes.toArray(), ",");//Comma separated center codes
				
				userAuthorization.setAuthorizedCenterCodes(authorizedCenterCodes);
				userAuthorization.setCommaSeparatedAuthorizedCenterCodes(commaSeparatedAuthorizedCenterCodes);

//				following field added for ELearn Resources  validation via roles and provider name 
				ELearnResourcesAcadsBean eLearnResourcesBean = loginDetails.getStukent();
				request.getSession().setAttribute("isStukentLtiRoles",eLearnResourcesBean.getRoles() );
				request.getSession().setAttribute("isStukentLtiProviderName",eLearnResourcesBean.getProvider_name());
				request.getSession().setAttribute("isStukentLtiUserIdCount",eLearnResourcesBean.getUserId_count());

				
				ELearnResourcesAcadsBean elearnResourcesBean =  loginDetails.getHarvard();
				request.getSession().setAttribute("isHarvardLtiRoles",elearnResourcesBean.getRoles() );
				request.getSession().setAttribute("isHarvardLtiProviderName",elearnResourcesBean.getProvider_name());
				request.getSession().setAttribute("isHarvardLtiUserIdCount",elearnResourcesBean.getUserId_count());

				
				//Perform user checks for CS  users.
				Map<String,Boolean> csAdmin = loginDetails.getCsAdmin();
				request.getSession().setAttribute("isCSSpeaker", csAdmin.get("isCSSpeaker"));
				request.getSession().setAttribute("isCSAdmin",  csAdmin.get("isCSAdmin"));
				request.getSession().setAttribute("isCSProductsAdmin", csAdmin.get("isCSProductsAdmin"));
				request.getSession().setAttribute("isCSSessionsAdmin", csAdmin.get("isCSSessionsAdmin"));
				request.getSession().setAttribute("isExternallyAffiliatedForProducts",  csAdmin.get("isExternallyAffiliatedForProducts"));
				
				request.getSession().setAttribute("userAuthorization", userAuthorization);
			}
			
			request.getSession().setAttribute("user_acads", person);
			//start - Added for LiveSessionAccess
		
			StudentAcadsBean registration = null; //new StudentBean();
			/*boolean isNonPG_Program = Boolean.FALSE;*/
			
			try {
				registration = loginDetails.getRegData(); //getStudentRegistrationDataForCurrentCycle
				request.getSession().setAttribute("studentRegData", registration);//Set up latest semester
				
				student.setSem(registration.getSem());
				request.getSession().setAttribute("currentSemPSSId",  loginDetails.getCurrentSemPSSId());//Set Current Sem   PssId
				request.getSession().setAttribute("liveSessionPssIdAccess_acads", loginDetails.getLiveSessionPssIdAccess());//Set Live Session PssId
			} catch (Exception e) {
				// TODO: handle exception
				logger.info("HomeController : loginforSSO For Sapid "+userId+" : Error : " + e.getMessage());
			}
			//end - Added for LiveSessionAccess
			
			//Set student registration details to session for Session Video Home
			try {
				String year = CURRENT_ACAD_YEAR;
				String month = CURRENT_ACAD_MONTH;
				
				//Get max order of academic session live
				double acadSessionLiveOrder = loginDetails.getAcadSessionLiveOrder();
				double maxOrderWhereContentLive= loginDetails.getMaxOrderWhereContentLive();
		
				//Get student registered year and month order
				double reg_order =  loginDetails.getRegOrder();
				double current_order =loginDetails.getCurrentOrder();
				
				request.getSession().setAttribute("reg_order", reg_order);
				request.getSession().setAttribute("acadContentLiveOrder",maxOrderWhereContentLive);
				request.getSession().setAttribute("current_order",current_order);
				//If the acadSessionLiveOrder and reg_order is equals then set registered year and month as currentSessionCycle
				//otherwise set CURRENT_ACAD_YEAR and CURRENT_ACAD_MONTH as currentSessionCycle
				if(acadSessionLiveOrder == reg_order) {
					year = registration.getYear();
					month = registration.getMonth();
				}

				request.getSession().setAttribute("currentSem", registration.getSem());

				request.getSession().setAttribute("currentSessionCycle", (month+year));
			}catch (Exception e) {
				logger.info("HomeController : loginforSSO "+userId+": Error : " + e.getMessage());
			}
			
			return null;
		
		} catch (Exception e) {
			
		}
		return null;
	}
}

