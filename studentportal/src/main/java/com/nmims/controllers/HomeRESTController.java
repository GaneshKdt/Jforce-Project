package com.nmims.controllers;

import java.io.IOException;
import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;

import java.util.Date;

import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import javax.naming.InvalidNameException;
import javax.naming.directory.InvalidAttributeValueException;
import javax.servlet.http.HttpServletRequest;


import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.exception.ExceptionUtils;

import javax.servlet.http.HttpServletResponse;

import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.jsoup.helper.StringUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.ldap.NameNotFoundException;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import com.nmims.beans.AcadCycleFeedback;
import com.nmims.beans.AssignmentStudentPortalFileBean;
import com.nmims.beans.AssignmentLiveSettingStudentPortal;
import com.nmims.beans.AuthenticateResponseBean;
import com.nmims.beans.ContentStudentPortalBean;
import com.nmims.beans.ExamBookingTransactionStudentPortalBean;
import com.nmims.beans.ExamOrderStudentPortalBean;
import com.nmims.beans.FlagBean;
import com.nmims.beans.ForumStudentPortalBean;
import com.nmims.beans.Online_EventBean;
import com.nmims.beans.PassFailBean;
import com.nmims.beans.PortalFeedbackBeanResponse;
import com.nmims.beans.ProgramSubjectMappingStudentPortalBean;
import com.nmims.beans.ReRegistrationStudentPortalBean;
import com.nmims.beans.SessionAttendanceFeedbackStudentPortal;
import com.nmims.beans.SessionDayTimeStudentPortal;
import com.nmims.beans.SessionQueryAnswerStudentPortal;
import com.nmims.beans.StudentCourseMappingBean;
import com.nmims.beans.StudentStudentPortalBean;

import com.nmims.beans.StudentMarksBean;
import com.nmims.beans.TimetableStudentPortalBean;
import com.nmims.daos.AssignmentsDAO;
import com.nmims.daos.CareerServicesDAO;
import com.nmims.daos.ContentDAO;
import com.nmims.daos.ForumDAO;
import com.nmims.daos.LDAPDao;
import com.nmims.daos.LeadDAO;
import com.nmims.daos.LearningResourcesDAO;
import com.nmims.daos.PortalDao;
import com.nmims.daos.ResultDAO;
import com.nmims.daos.ServiceRequestDao;
import com.nmims.daos.SessionQueryAnswerDAO;
import com.nmims.daos.StudentCourseMappingDao;
import com.nmims.daos.StudentInfoCheckDAO;

import com.nmims.helpers.DateTimeHelper;

import com.nmims.dto.PortalFeedbackBeanResponseDto;
import com.nmims.dto.SessionAttendanceFeedbackDto;
import com.nmims.helpers.MailSender;
import com.nmims.helpers.PaymentHelper;
import com.nmims.helpers.PersonStudentPortalBean;
import com.nmims.helpers.RegistrationHelper;
import com.nmims.helpers.ResultsFromRedisHelper;
import com.nmims.helpers.SalesforceHelper;
import com.nmims.interfaces.ContentServiceInterFace;
import com.nmims.publisher.IdCardEventPublisher;
import com.nmims.services.AssignmentServiceInterface;
import com.nmims.services.FeedbackService;
import com.nmims.services.HomeService;
import com.nmims.services.IdCardService;
import com.nmims.services.LiveSessionAccessService;

import com.nmims.services.ServiceRequestService;
import com.nmims.services.StudentCourseMappingService;
import com.nmims.services.StudentService;
import com.nmims.util.ContentUtil;
import com.nmims.views.LeadReportView;

import com.nmims.services.IStudentReRegistrationService;

@RestController
@RequestMapping("m")
public class HomeRESTController extends BaseController {
	@Autowired
	ApplicationContext act;

	@Autowired
	CareerServicesDAO csDAO;
	
	@Autowired
	SessionQueryAnswerDAO sessionQueryAnswerDAO;
	
	@Autowired
	ContentDAO contentDAO;

	@Autowired
	SalesforceHelper salesforceHelper;
	
	@Autowired
	HomeService homeService;

	@Autowired  
	FeedbackService feedService;

	@Autowired  
	ServiceRequestService srService;

	@Autowired
	MailSender mailSender;

	@Autowired
	PaymentHelper paymentHelper;
	
	@Autowired
	RegistrationHelper registrationHelper;

	@Autowired
	LeadDAO leadDAO;

	@Autowired
	StudentService studentService;
	
	@Autowired
	LeadReportView leadReportView;
	
	@Autowired
	LiveSessionAccessService liveSessionAccessService;
	
	@Autowired
	ContentServiceInterFace contentService;
	
	@Autowired
	StudentCourseMappingService studentCourseService;
	
	@Autowired
	IStudentReRegistrationService studentReRegistrationService;

	IdCardEventPublisher eventPublisher;
	
	@Autowired
	IdCardService idCardService;

	
	@Autowired
	AssignmentServiceInterface assignmentService;
	
	@Value( "${SERVER_PATH}" )
	private String SERVER_PATH;
	
	@Value( "${ASSIGNMENT_SUBMISSIONS_ATTEMPTS}" )
	private String ASSIGNMENT_SUBMISSIONS_ATTEMPTS;

	@Value( "${ACCOUNT_ID}" )
	private String ACCOUNT_ID;
	
	@Value("${SECURE_SECRET}")
	private String SECURE_SECRET; // secret key;

	@Value("${ACAD_YEAR_SAS_LIST}")
	private List<String> ACAD_YEAR_SAS_LIST;

	@Value("${CURRENT_ACAD_MONTH}")
	private String CURRENT_ACAD_MONTH;

	@Value("${CURRENT_ACAD_YEAR}")
	private String CURRENT_ACAD_YEAR;
	
	@Value( "${ENVIRONMENT}" )
	private String ENVIRONMENT;
	
	public static final String default_format = "yyyy-MM-dd";
	
	private static final String PRE_LINK = "https://ngasce.secure.force.com/nmLogin_new?studentNo=";
	private static final String DOB_LINK = "&dob=";
	private static final String POST_LINK = "&type=reregistration";
	private static final String DEFAULT_LINK = "https://studentzone-ngasce.nmims.edu/studentportal/reRegistrationPage";
	
	private static final String MSC_AI = "158";
	private static final String MSC_AI_ML_OPS = "131";
	
	private static final Logger logger = LoggerFactory.getLogger(HomeRESTController.class);
	private static final Logger courses_logger = LoggerFactory.getLogger("studentCourses");
	private static final Logger loggerForCourseQueryNotificationToFaculty = LoggerFactory.getLogger("courseQuery");
	
	public SimpleDateFormat formatter = new SimpleDateFormat("yyyy/MM/dd");
	private ArrayList<ProgramSubjectMappingStudentPortalBean> programSubjectMappingList = null;
	public HashMap<String,String> mapOfSRTypesAndTAT = null;

	private ArrayList<String> industryList = new ArrayList<String>();

	private ArrayList<String> designationList =new ArrayList<String>(
			//Arrays.asList("Executive / Officer","Assistant Manager","Manager","Senior Manager","General Manager","Vice President","CXO","CEO"));
			Arrays.asList("Assistant","Assistant Manager","Associate","Asst. Manager","CEO","CFO","Chairman","CXO","Deputy General Manager","Executive / Officer","General Manager","Jr. Associate","Jr. Officer","Manager","Managing Director","Officer","Others","Sr. Associate","Sr. Manager","Sr. Officer","Sr. Vice president","Vice Chairman","Vice President"));

	private ArrayList<String> paymentTypeList =new ArrayList<String>(
			Arrays.asList("Exam Registration"));

	private ArrayList<String> studentWithBusinessStatisticsSubjectList = null;

	private ArrayList<String> monthList = new ArrayList<String>(
			Arrays.asList("Jan","Feb","Mar","Apr","May","Jun","Jul","Aug","Sep","Oct","Nov","Dec")); 

	private final int pageSize = 20;
	private int downloaded = 0;
	
	@Value("${TIMEBOUND_PORTAL_LIST}")
	private List<String> TIMEBOUND_PORTAL_LIST;
	
	public String RefreshCache() {

		programSubjectMappingList = null;
		getProgramSubjectMappingList();

		mapOfSRTypesAndTAT = null;
		getMapOfSRTypesAndTAT();


		studentWithBusinessStatisticsSubjectList = null;
		getstudentWithBusinessStatisticsSubjectList();

		return null;
	}
	
	public ArrayList<ProgramSubjectMappingStudentPortalBean> getProgramSubjectMappingList(){
		if(this.programSubjectMappingList == null || this.programSubjectMappingList.size() == 0){
			
			PortalDao pDao = (PortalDao)act.getBean("portalDAO");
			this.programSubjectMappingList = pDao.getProgramSubjectMappingList();
		}
		return programSubjectMappingList;
	}
	
	public HashMap<String,String> getMapOfSRTypesAndTAT(){
		if(this.mapOfSRTypesAndTAT == null || this.mapOfSRTypesAndTAT.size() == 0){
			
			ServiceRequestDao serviceRequestDao = (ServiceRequestDao)act.getBean("serviceRequestDao");
			this.mapOfSRTypesAndTAT = serviceRequestDao.getMapOfSRTypesAndTAT();
		}
		return mapOfSRTypesAndTAT;
	}

	public ArrayList<String> getstudentWithBusinessStatisticsSubjectList(){
		if(this.studentWithBusinessStatisticsSubjectList == null ||studentWithBusinessStatisticsSubjectList.size() == 0){
			
			PortalDao pDao = (PortalDao)act.getBean("portalDAO");
			ArrayList<String> studentWithBusinessStatisticsSubjectList = new ArrayList<>();
			this.studentWithBusinessStatisticsSubjectList = pDao.getStudentApplicableForSubject("Business Statistics");

		}
		return studentWithBusinessStatisticsSubjectList;
	}
	/*@PostMapping(path = "/CoursesWithPSSId" , consumes= "application/json", produces = "application/json")
	public ResponseEntity<HashMap<String,String>> mCoursesWithPSSId (@RequestBody StudentStudentPortalBean student){ 
	

		PortalDao pDao = (PortalDao)act.getBean("portalDAO");
		ArrayList<StudentMarksBean> allStudentRegistrations = pDao.getAllRegistrationsFromSAPID(student.getSapid());
		HashMap<String, StudentMarksBean> monthYearAndStudentRegistrationMap = new HashMap<>();
		for (StudentMarksBean bean : allStudentRegistrations) {
			monthYearAndStudentRegistrationMap.put(bean.getMonth() + "-" + bean.getYear(), bean);
		}

		StudentStudentPortalBean studentDetail = pDao.getSingleStudentsData(student.getSapid());

		String liveTypeForCourses = "acadContentLive";
		List<ExamOrderStudentPortalBean> liveFlagList = pDao.getLiveFlagDetails();

		StudentMarksBean studentRegistrationData = getStudentRegistrationForForSpecificLiveSettings(monthYearAndStudentRegistrationMap, liveFlagList, liveTypeForCourses);
		
		//StudentBean studentRegistrationData = pDao.getStudentsMostRecentRegistrationData(student.getSapid());
		
	

		ArrayList<String> allApplicableSubjects = new ArrayList<String>();
		ArrayList<String> currentSemSubjects = null;
		ArrayList<String> notPassedSubjects = null;


		if(studentRegistrationData != null){
			studentDetail.setSem(studentRegistrationData.getSem());
			studentDetail.setProgram(studentRegistrationData.getProgram());
			currentSemSubjects = getSubjectsForStudent(studentDetail);
		}
		

		if(currentSemSubjects == null){
			currentSemSubjects = new ArrayList<>();
		}

		studentService.mgetWaivedInSubjects(studentDetail);		// add waived in subjects
		studentService.mgetWaivedOffSubjects(studentDetail);	// add waived off subjects

		allApplicableSubjects.addAll(currentSemSubjects);
		ArrayList<String> waivedInSubjects = studentDetail.getWaivedInSubjects();
		if(waivedInSubjects != null) {
			for (String subject : waivedInSubjects) {
				if(!allApplicableSubjects.contains(subject)) {
					allApplicableSubjects.add(subject);
				}
			}
		}

		ArrayList<String> failedSubjects = pDao.getFailSubjectsNamesForAStudent(studentDetail.getSapid());
		if(failedSubjects != null){
			allApplicableSubjects.addAll(failedSubjects);
		} else{
			failedSubjects = new ArrayList<String>(); 
		}
	

		notPassedSubjects = pDao.getNotPassedSubjectsBasedOnSapid((studentDetail.getSapid()));
		if(notPassedSubjects != null && notPassedSubjects.size()>0){
			allApplicableSubjects.addAll(notPassedSubjects);
		}
		ArrayList<String> listOfApplicableSubjects = new ArrayList<String>(new LinkedHashSet<String>(allApplicableSubjects));
		//remove Waiveoff subject from applicable subject list
		for(String subjects: allApplicableSubjects)
		{
			if(studentDetail.getWaivedOffSubjects().contains(subjects)){
				listOfApplicableSubjects.remove(subjects);
			}
		}
		// Block to check the remark pass fail subjects which currently is only applicable for BBA and BCOM students.
		if(studentDetail.getProgram().equals("BBA") || studentDetail.getProgram().equals("B.Com")) {
			try {
				List<String> ugPassSubjectsList = getUGPassSubjects(pDao, studentDetail);
				for(String subject:ugPassSubjectsList){
					if(currentSemSubjects.contains(subject)){
						currentSemSubjects.remove(subject);
					}
					if(failedSubjects.contains(subject)){
						failedSubjects.remove(subject);
					}
					/*if(backlogSubjects.contains(subject))
					{
						backlogSubjects.remove(subject);
					}*/
				/*	if(listOfApplicableSubjects.contains(subject)){
						listOfApplicableSubjects.remove(subject);
					}
				}
			} catch (Exception e) {
				// TODO Auto-generated catch block
				//e.printStackTrace();
			/*}
		}
		LinkedList<String> sub = new LinkedList<String>();
		for(int i = 0 ; i< listOfApplicableSubjects.size(); i++){
			sub.add(listOfApplicableSubjects.get(i));
		}*/
		
		//Finding Program sem subject Id of it's respective subject
		/*HashMap<String,String>  programSemSubjectIdWithSubject = pDao.getProgramSemSubjectId(listOfApplicableSubjects, studentDetail.getConsumerProgramStructureId());
		return  new ResponseEntity<HashMap<String,String>>(programSemSubjectIdWithSubject, HttpStatus.OK);
	}*/
	
	private List<String> getUGPassSubjects(PortalDao pDao, StudentStudentPortalBean student) {
		return pDao.getUGPassSubjectsForAStudent(student.getSapid());
	}
	
	@PostMapping(value="/lastCycleContent", consumes="application/json", produces="application/json", headers="content-type=application/json")
	public ResponseEntity<List<ContentStudentPortalBean>> mlastCycleContent(@RequestBody StudentStudentPortalBean input){
		HttpHeaders headers = new HttpHeaders();
	    headers.add("Content-Type", "application/json");
		List<ContentStudentPortalBean> response = new ArrayList<ContentStudentPortalBean>();
		if(input.getProgramSemSubjectId() != null) {
			response =	mgetLastCycleContentNewForLR(input, input.getProgramSemSubjectId());
		}else {
			response = mgetLastCycleContent(input);	
		}
		return new ResponseEntity<>(response, headers,  HttpStatus.OK);
	}
	

	


	
	/*@PostMapping(path="/reRegForMobile", consumes = "application/json", produces = "application/json")
	public ResponseEntity<ReRegistrationStudentPortalBean> reRegForMobile(HttpServletRequest request,
			@RequestBody PersonStudentPortalBean input) throws Exception {

		HttpHeaders headers = new HttpHeaders();
		headers.add("Content-Type", "application/json");

		ReRegistrationStudentPortalBean response = new ReRegistrationStudentPortalBean();

		String userId = input.getSapId();

		boolean makeLive= false;
		StudentInfoCheckDAO studentDao = (StudentInfoCheckDAO)act.getBean("stuentInfoCheckDAO");
		//check student already registered with salesforce's active re-reg month & year
		ReRegistrationStudentPortalBean activeRegistration = srService.getActiveReRegistrationFromSalesForce();
		if( !activeRegistration.isError()) {
			boolean alreadyRegistered = studentDao.ifStudentAlreadyRegisteredForNextSem(userId,activeRegistration);
			if(!alreadyRegistered) {
				//check validity of re-reg 
				SimpleDateFormat sdformat = new SimpleDateFormat("yyyy-MM-dd");

				Date startdate = sdformat.parse(activeRegistration.getStartTime());
				Date endDate = sdformat.parse(activeRegistration.getEndTime());

				Date date = new Date();  
				Date now = sdformat.parse(sdformat.format(date));

				if(now.compareTo(startdate) > 0 && endDate.compareTo(now) > 0 ) {
					makeLive = true; 
					response.setError(false); 
					response.setSuccess(true);
					response.setUrl("https://studentzone-ngasce.nmims.edu/studentportal/reRegistrationPage");
				}
			}
		}
		return new ResponseEntity<ReRegistrationStudentPortalBean>(response,headers, HttpStatus.OK);
	}*/
	
	protected List<StudentMarksBean> matchSubjectInAllStudentMarks(List<StudentMarksBean> tempList, String subject) {
		StudentMarksBean studentMarksBean = null;
		List<StudentMarksBean> studentMarksBeanList = new ArrayList<StudentMarksBean>();
		if (null != tempList && !tempList.isEmpty()) {
			logger.info("HomeRESTController : matchSubjectInAllStudentMarks : size : " + tempList.size());
			for (int y = 0; y < tempList.size(); y++) {
				studentMarksBean = tempList.get(y);
				if (null != studentMarksBean && studentMarksBean.getSubject().equals(subject)) {
					logger.info("HomeRESTController : matchSubjectInAllStudentMarks : subject : " + studentMarksBean.getSubject());
					studentMarksBeanList.add(studentMarksBean);
				}
			}
		} else {
			logger.info("HomeRESTController : matchSubjectInAllStudentMarks : empty : " + subject);
		}
		return studentMarksBeanList;
	}
	
	protected List<StudentMarksBean> fetchAllStudentMarksforMobile(String sapId, String subject) {
		// NOTE: Results fetched from REDIS to display. Added by Vilpesh on 2021-11-27
		Map<String, Object> destinationMap = null;
		List<StudentMarksBean> studentMarksBeanList = null;
		List<StudentMarksBean> tempList = new ArrayList<StudentMarksBean>();
		/*destinationMap = this.fetchRedisHelper().fetchOnlyMarkslist(ResultsFromRedisHelper.EXAM_STAGE_TEE, sapId);
		if (null != destinationMap && null != destinationMap.get(ResultsFromRedisHelper.KEY_STUDENT_MARKSLIST)) {
			tempList.addAll(
					(ArrayList<StudentMarksBean>) destinationMap.get(ResultsFromRedisHelper.KEY_STUDENT_MARKSLIST));
		}*/
		destinationMap = this.fetchRedisHelper().fetchOnlyMarksHistory(ResultsFromRedisHelper.EXAM_STAGE_TEE, sapId);
		if (null != destinationMap && null != destinationMap.get(ResultsFromRedisHelper.KEY_STUDENT_MARKS_HISTORY)) {
			tempList.addAll(
					(ArrayList<StudentMarksBean>) destinationMap.get(ResultsFromRedisHelper.KEY_STUDENT_MARKS_HISTORY));
		}
		studentMarksBeanList = matchSubjectInAllStudentMarks(tempList, subject);
 
		destinationMap = null;
		tempList = null;
		return studentMarksBeanList;
	}
	
	private List<StudentMarksBean> mgenerateCourseMarksHistoryMap(StudentStudentPortalBean student) {
		//NOTE: Method copied here from HomeController, changed on 2022-01-24. Vilpesh
		
		ResultDAO resultDAO = (ResultDAO) act.getBean("resultDAO");
		List<StudentMarksBean> studentMarksBeanList = null;
		Boolean readFromCache = Boolean.FALSE;
		try {
			readFromCache = this.fetchRedisHelper().readFromCache();
			if(readFromCache) {
				studentMarksBeanList = this.fetchAllStudentMarksforMobile(student.getSapid(), student.getSubject());
			}
		} catch(Exception e) {
//			e.printStackTrace();
			logger.error("HomeRESTController : mgenerateCourseMarksHistoryMap : exception : " + e.getMessage());
			
			//if REDIS stopped - exception caught - page loading continued -Vilpesh on 2021-11-24
			readFromCache = Boolean.FALSE;
		}
		if(!readFromCache) {
			studentMarksBeanList = resultDAO.getAStudentsMarksForSubject(student.getSapid(), student.getSubject());
		}
		return studentMarksBeanList;
	}
	
	@PostMapping(path="/courseDetailsResults2", consumes = "application/json", produces = "application/json")	
	public ResponseEntity<List<StudentMarksBean>> mcourseDetailsResults2(HttpServletRequest request,@RequestBody StudentStudentPortalBean input) throws Exception {
		HttpHeaders headers = new HttpHeaders();
		headers.add("Content-Type", "application/json"); 
		try {
		//if(checkIfMovingResultsToCache()) { 
			if(this.fetchRedisHelper().sendingResultsToCache()) {	
				List<StudentMarksBean> response = new ArrayList<>();
				return new ResponseEntity<List<StudentMarksBean>>(response, headers,  HttpStatus.OK);
			}
		} catch (Exception ex) {
//			ex.printStackTrace();
			logger.error("HomeRESTController: mcourseDetailsResults2 : error : "+ ex.getMessage());
			//if REDIS stopped - exception caught - page loading continued -Vilpesh on 2022-01-24
		}
		//List<StudentMarksBean> responseResult = generateCourseMarksHistoryMap(request, input.getSubject());//Vilpesh 2021-11-27, wrong method for Mobile
		List<StudentMarksBean> responseResult = mgenerateCourseMarksHistoryMap(input);
		return new ResponseEntity<List<StudentMarksBean>>(responseResult, headers,  HttpStatus.OK);
	}
	
	@PostMapping(path="/feedbackCheck", consumes = "application/json", produces = "application/json")
	public ResponseEntity<PortalFeedbackBeanResponseDto> feedbackCheck(HttpServletRequest request,
			@RequestBody PersonStudentPortalBean input) throws Exception {

		HttpHeaders headers = new HttpHeaders();
		headers.add("Content-Type", "application/json");

		String userId = input.getSapId();
		PortalFeedbackBeanResponse result = new PortalFeedbackBeanResponse();
		PortalFeedbackBeanResponseDto resultDto = new PortalFeedbackBeanResponseDto();
		
		SessionAttendanceFeedbackDto sessionAttendanceFeedbackDto =null;
		
		List<SessionAttendanceFeedbackDto> sessionAttendanceFeedbackDtoList =new ArrayList<SessionAttendanceFeedbackDto>();
		result = feedService.getPendingFeedbacks(userId);
		
		
		if(result.getPendingFeedback()!=null)
		{
			for(SessionAttendanceFeedbackStudentPortal bean : result.getPendingFeedback())
			{
				sessionAttendanceFeedbackDto = new SessionAttendanceFeedbackDto();
				sessionAttendanceFeedbackDto.setSapId(bean.getSapId());
				sessionAttendanceFeedbackDto.setSessionId(bean.getSessionId());
				sessionAttendanceFeedbackDto.setAttended(bean.getAttended());
				sessionAttendanceFeedbackDto.setAttendTime(bean.getAttendTime());
				sessionAttendanceFeedbackDto.setFeedbackGiven(bean.getFeedbackGiven());
				sessionAttendanceFeedbackDto.setDate(bean.getDate());
				sessionAttendanceFeedbackDto.setStartTime(bean.getStartTime());
				sessionAttendanceFeedbackDto.setDay(bean.getDay());
				sessionAttendanceFeedbackDto.setSubject(bean.getSubject());
				sessionAttendanceFeedbackDto.setSessionName(bean.getSessionName());
				sessionAttendanceFeedbackDto.setFirstName(bean.getFirstName());
				sessionAttendanceFeedbackDto.setLastName(bean.getLastName());
				sessionAttendanceFeedbackDto.setFacultyId(bean.getFacultyId());
				sessionAttendanceFeedbackDto.setStudentConfirmationForAttendance(bean.getStudentConfirmationForAttendance());
				sessionAttendanceFeedbackDto.setId(bean.getId());
				sessionAttendanceFeedbackDto.setTrack(bean.getTrack());
				sessionAttendanceFeedbackDto.setCreatedBy(bean.getCreatedBy());
				sessionAttendanceFeedbackDto.setCreatedDate(bean.getCreatedDate());
				sessionAttendanceFeedbackDto.setLastModifiedBy(bean.getLastModifiedBy());
				sessionAttendanceFeedbackDto.setLastModifiedDate(bean.getLastModifiedDate());

				sessionAttendanceFeedbackDtoList.add(sessionAttendanceFeedbackDto);


			}
		}
		
		resultDto.setFeedbackType(result.getFeedbackType());
		resultDto.setPendingAcadFeedback(result.getPendingAcadFeedback());
		resultDto.setAcadCycleFeedbackBean(result.getAcadCycleFeedbackBean());
		resultDto.setPendingFeedback((ArrayList<SessionAttendanceFeedbackDto>) sessionAttendanceFeedbackDtoList);
		
		
		return new ResponseEntity<>(resultDto,headers, HttpStatus.OK);

	}
	
	// Submit session or Acad Feedback
	@PostMapping(path="/FeedbackSave", consumes = "application/json", produces = "application/json")
	public ResponseEntity<HashMap<String, String>> saveMFeedback(HttpServletRequest request,
			@RequestBody SessionAttendanceFeedbackStudentPortal feedback) throws Exception {

		HashMap<String, String> response = new HashMap<>();

		HttpHeaders headers = new HttpHeaders();
		headers.add("Content-Type", "application/json");
		String userId = feedback.getSapId();
		boolean result =feedService.saveSessionFeedback(feedback,userId);
		if (result) {
			response.put("success", "true");
			response.put("successMessage", "feedback saved");
			return new ResponseEntity<>(response, headers, HttpStatus.OK);
		}else
		{
			return new ResponseEntity<>(headers, HttpStatus.UNPROCESSABLE_ENTITY);
		}

		// save session feedback based on studentConfirmationForAttendance=y/n

	}
	
	@PostMapping(path = "/saveAcadCycleFeedback", consumes = "application/json", produces = "application/json")
	public ResponseEntity<HashMap<String, String>> saveMAcadCycleFeedback(HttpServletRequest request,
			@RequestBody AcadCycleFeedback feedback) throws Exception {

		HashMap<String, String> response = new HashMap<>();

		HttpHeaders headers = new HttpHeaders();
		headers.add("Content-Type", "application/json");

		boolean result = feedService.saveAcadFeedback(feedback);
		if (result) {
			response.put("success", "true");
			response.put("successMessage", "feedback saved");
			return new ResponseEntity<>(response, headers, HttpStatus.OK);
		}else {
			return new ResponseEntity<>(headers, HttpStatus.UNPROCESSABLE_ENTITY);
		}

	}
	
	
	@PostMapping(path = "/authenticate", consumes = "application/json", produces = "application/json")
	public ResponseEntity<StudentStudentPortalBean> m_home(@RequestBody PersonStudentPortalBean input) throws Exception {


		HttpHeaders headers = new HttpHeaders();
		headers.add("Content-Type", "application/json"); 
		LDAPDao dao = (LDAPDao)act.getBean("ldapdao");



		String userId = input.getUserId();
		String password = input.getPassword();

		if(password == null || userId == null || "".equals(password.trim()) || "".equals(userId.trim())){
			//			request.setAttribute("error", "true");
			//			request.setAttribute("errorMessage", "Please enter ID and Password.");
			//			return modelnView;
			return new ResponseEntity<>(headers, HttpStatus.UNPROCESSABLE_ENTITY);
		}

		boolean authenticated = false;

		try {
			if("app@ngasce20".equals(password)){
				authenticated = true;
			}else{
				authenticated = dao.login(userId, password);
			}
		} catch (Exception e) {
//			e.printStackTrace();
			//			modelnView = new ModelAndView("login");
			//			request.setAttribute("error", "true");
			//			request.setAttribute("errorMessage", "We are performing temporary maintenance activity, please try after some time.");
			//			return modelnView;
			return new ResponseEntity<>(headers, HttpStatus.SERVICE_UNAVAILABLE);
		}

		//		String session_SERVER_PATH = SERVER_PATH;

		//request.setAttribute("SERVER_PATH", SERVER_PATH);

		if(authenticated){
			PortalDao pDao = (PortalDao)act.getBean("portalDAO");
			String session_validityExpired = "No"; //This parameter is kept false initially.It will get set to true only if the validity is expired.
			String session_earlyAccess = "No";
			//String session_SERVER_PATH = SERVER_PATH;


			if(!(userId.startsWith("77") || userId.startsWith("79"))){
				List error = new ArrayList<String>();
				return new ResponseEntity<>(headers, HttpStatus.UNAUTHORIZED);
			}

			StudentStudentPortalBean student = pDao.getSingleStudentsData(userId);
			boolean isCertificate = student.isCertificateStudent();
			boolean session_isCertificate = isCertificate;
			boolean isValid = isStudentValid(student, userId);
			

			// disable program terminated Student from login 
			if("Program Terminated".equalsIgnoreCase(student.getProgramStatus()))
			{
				return new ResponseEntity<>(headers, HttpStatus.UNAUTHORIZED);
				//			request.setAttribute("error", "true");
				//			request.setAttribute("errorMessage", "Unable to access your Profile for further details call 1800 1025 136 (Mon-Sat) 10am-6pm");
				//			return logout(request,respnse);
			}

			if(!isValid){
				//return new ResponseEntity<>(headers, HttpStatus.UNAUTHORIZED);
				student.setValidityExpired("Y");

				//			request.getSession().setAttribute("validityExpired","Yes");
				//			return new  ModelAndView("support/overview");
			}
			else {
				student.setValidityExpired("N");
			}
			String validityEndDate = getValidityEndDate(student);
			String session_validityEndDate = validityEndDate;

			//if (!"111".equalsIgnoreCase(student.getConsumerProgramStructureId()) 
			if ( !isTimeboundWiseByConsumerProgramStructureId(student.getConsumerProgramStructureId()) 
					&& !"119".equalsIgnoreCase(student.getConsumerProgramStructureId())
					&& !"126".equalsIgnoreCase(student.getConsumerProgramStructureId())	) {
				//HashMap<String,BigDecimal> examOrderMap = pDao.getExamOrderMap();
				List<ExamOrderStudentPortalBean> liveFlagList = pDao.getLiveFlagDetails();
				HashMap<String,BigDecimal> examOrderMap = generateExamOrderMap(liveFlagList);
				double examOrderDifference = 0.0;
				double examOrderOfProspectiveBatch = examOrderMap.get(student.getEnrollmentMonth()+student.getEnrollmentYear()).doubleValue();
				double maxOrderWhereContentLive = getMaxOrderWhereContentLive(liveFlagList);
				examOrderDifference = examOrderOfProspectiveBatch - maxOrderWhereContentLive;

				if(examOrderDifference == 1){
					session_earlyAccess = "Yes";
				}
			}

			if(student != null){
				student = this.replaceNullToEmpty(student); 
				return new ResponseEntity<>(student, headers,  HttpStatus.OK);
			}

		}else{
			return new ResponseEntity<>(headers, HttpStatus.UNAUTHORIZED);
		}
		return new ResponseEntity<>(headers, HttpStatus.UNAUTHORIZED);
	}
	

	@RequestMapping(path = "/resetPassword", method = RequestMethod.POST, produces="application/json", consumes="application/json")
	public ResponseEntity<HashMap<String,String>> mresetPassword(@RequestBody StudentStudentPortalBean student) throws Exception{
		HttpHeaders headers = new HttpHeaders();
		headers.add("Content-Type", "application/json"); 
		HashMap<String,String> response = new HashMap<>();

		String userId = student.getSapid();
		LDAPDao dao = (LDAPDao)act.getBean("ldapdao");
		try{
			String email = "";
			PersonStudentPortalBean person = dao.findPerson(userId);


			if(person != null){
				if(userId.startsWith("77") || userId.startsWith("79")){
					PortalDao pDao = (PortalDao)act.getBean("portalDAO");
					student = pDao.getSingleStudentsData(userId);
					email = student.getEmailId();
				}else{
					email = person.getEmail();
				}

				if(email != null && email.indexOf("@") != -1){
					
					try{

						/*
						 * moved the mail triggering to mailer app
						 * MailSender mailer = (MailSender)act.getBean("mailer");
						 * mailer.sendPasswordEmail(person.getDisplayName(), email, person.getPassword());
						 */
						HashMap<String, String> parameters = new HashMap<>();
						parameters.put("name", person.getDisplayName());
						parameters.put("email", email);
						parameters.put("password", person.getPassword());
						
						RestTemplate restTemplate = new RestTemplate();
						restTemplate.postForObject(SERVER_PATH+"mailer/m/sendPasswordEmail", parameters, HashMap.class);

						response.put("success", "true");
						response.put("successMessage","Your password is emailed to your registered email id: "+email);
						return new ResponseEntity<>(response, headers, HttpStatus.OK);

					}catch(Exception e){
//						e.printStackTrace();
						response.put("error", "true");
						response.put("errorMessage", "Error in resetting password. Please contact ngasce@nmims.edu to reset your password");
						return new ResponseEntity<>(response, headers, HttpStatus.OK);
					}
				}else{
					response.put("error", "true");
					response.put("errorMessage", "No registered mail id exists with us. Please send email to ngasce@nmims.edu to reset your password.");
					return new ResponseEntity<>(response, headers, HttpStatus.OK);
				}
			}else{
				response.put("error", "true");
				response.put("errorMessage", "User ID does not exist. Password cannot be reset.");
				return new ResponseEntity<>(response, headers, HttpStatus.OK);
			}

		}catch(NameNotFoundException e){
//			e.printStackTrace();
			response.put("error", "true");
			response.put("errorMessage", "User ID does not exist. Password cannot be reset.");
			return new ResponseEntity<>(response, headers, HttpStatus.OK);
		}catch(Exception e){
//			e.printStackTrace();
		}
		return new ResponseEntity<>(response, headers, HttpStatus.OK);
	}
	
	@PostMapping(path = "/savePassword", produces="application/json", consumes="application/json")
	public ResponseEntity<HashMap<String,String>> msavePassword(@RequestBody PersonStudentPortalBean input) throws Exception{
		

		HttpHeaders headers = new HttpHeaders();
		headers.add("Content-Type", "application/json"); 
		HashMap<String,String> response = new HashMap<>();

		String password = input.getPassword();
		String oldPassword = input.getOldPassword();
		String userId = input.getUserId();
		System.out.println("This is the pass "+oldPassword);
	
		LDAPDao dao = (LDAPDao)act.getBean("ldapdao");
		PortalDao pdao = (PortalDao)act.getBean("portalDAO");
		//LdapContextSource ctx = (LdapContext) act.getBean("contextSource");
		String userOldPassword = dao.getUserPassword(userId);
	
		if(userOldPassword.equals(oldPassword)) {
			try{
				dao.changePassword(password, userId);
				if(pdao.updatePasswordFlag(userId)) {
					//		modelnView = new ModelAndView("home");
					//				request.setAttribute("success","true");
					//				request.setAttribute("successMessage","Password changed successfully.");
					//				request.getSession().setAttribute("password",password);
					PortalDao pDao = (PortalDao)act.getBean("portalDAO");
					StudentStudentPortalBean student = pDao.getSingleStudentsData(userId);
					PersonStudentPortalBean person = dao.findPerson(userId);
					mailSender.sendPasswordEmailNew(person,student.getEmailId(),password);
					response.put("Status", "success");
					response.put("Message", "Password changed successfully.");
					return new ResponseEntity<>(response, headers, HttpStatus.OK);
				}else {
					dao.changePassword(oldPassword, userId);
					response.put("Status", "error");
					response.put("Message", "Oops! We are Sorry Something went wrong. We're working on it now");
					return new ResponseEntity<>(response, headers, HttpStatus.OK);
				}

			}catch(Exception e){
				response.put("Status", "error");
				response.put("Message", "New password does not meet password policy OR is similar to past/default password. Please set password as per policy.");
				return new ResponseEntity<>(response, headers, HttpStatus.OK);

			}
		}
		response.put("Status", "error");
		response.put("Message", "Incorrect Old Password found");
		//response.put("password", password);
		return new ResponseEntity<>(response, headers, HttpStatus.OK);
		/*try{
			if(!dao.login(userId, oldPassword)) {
				response.put("error", "true");
				response.put("errorMessage", "Incorrect Old Password Found,Please Try again");
				return new ResponseEntity<>(response, headers, HttpStatus.OK);
			}
			dao.changePassword(password, userId);
	//		modelnView = new ModelAndView("home");
//			request.setAttribute("success","true");
//			request.setAttribute("successMessage","Password changed successfully.");
//			request.getSession().setAttribute("password",password);
			response.put("success", "true");
			response.put("successMessage", "Password changed successfully.");
			response.put("password", password);
			return new ResponseEntity<>(response, headers, HttpStatus.OK);

		}catch(Exception e){
			response.put("error", "true");
			response.put("errorMessage", "New password does not meet password policy OR is similar to past/default password. Please set password as per policy.");
			response.put("password", password);
			return new ResponseEntity<>(response, headers, HttpStatus.OK);

		}*/
	}
	
	@RequestMapping(value = "/reRegForMobile", method = RequestMethod.POST, consumes = "application/json", produces = "application/json")
	public ResponseEntity<ReRegistrationStudentPortalBean> reRegForMobile(
			@RequestBody ReRegistrationStudentPortalBean input) throws Exception {

		logger.info("Entered reRegForMobile() method of HomeRESTController");

		ReRegistrationStudentPortalBean response = new ReRegistrationStudentPortalBean();
		HttpHeaders headers = new HttpHeaders();
		headers.add("Content-Type", "application/json");

		String userId = input.getSapId();
		String dob = input.getDob();
		String paymentLink = "";
		boolean makeLive = false;
		StudentInfoCheckDAO studentDao = (StudentInfoCheckDAO) act.getBean("stuentInfoCheckDAO");

		
		//TODO Switch ()
		ReRegistrationStudentPortalBean activeRegistration = srService.getActiveReRegistrationFromSalesForce();
		// string is passed as an argument
		
		
		SimpleDateFormat sdformat = new SimpleDateFormat(default_format);

		Date startdate = sdformat.parse(activeRegistration.getStartTime());
		Date endDate = sdformat.parse(activeRegistration.getEndTime());

		Date date = new Date();
		Date now = sdformat.parse(sdformat.format(date));

		try {
			if (!activeRegistration.isError()) {
				if (now.compareTo(startdate) > 0 && endDate.compareTo(now) > 0) {

					if (StringUtils.isNotBlank(input.getConsumerProgramStructureId())) {
						switch (input.getConsumerProgramStructureId()) {

						case MSC_AI_ML_OPS:
						case MSC_AI:
							paymentLink = studentReRegistrationService.getReRegistrationPaymentLink(userId, dob);

							if (StringUtils.isNotBlank(paymentLink)) {
								response.setError(false);
								response.setSuccess(true);
								response.setUrl(paymentLink);
							}
							break;

						default:
							if (!activeRegistration.isError()) {
								boolean alreadyRegistered = studentDao.ifStudentAlreadyRegisteredForNextSem(userId,
										activeRegistration);

								if (!alreadyRegistered) {
									// check validity of re-reg

									startdate = sdformat.parse(activeRegistration.getStartTime());
									endDate = sdformat.parse(activeRegistration.getEndTime());

									date = new Date();
									now = sdformat.parse(sdformat.format(date));

									if (now.compareTo(startdate) > 0 && endDate.compareTo(now) > 0) {

										// dob - for updated app
										if (!StringUtils.isBlank(dob)) {
											dob = DateTimeHelper.getDateInFormat(default_format, dob);
											response.setUrl(PRE_LINK + userId + DOB_LINK + dob + POST_LINK);
										} else {
											response.setUrl(DEFAULT_LINK);
										}
										makeLive = true;
										response.setError(false);
										response.setSuccess(true);
									}
								}
							} else {
								response.setUrl(paymentLink);
							}
							break;
						}
					}
				}
			}
		} catch (Exception e) {
			
			response.setError(true);
			response.setSuccess(false);
			response.setUrl("");
			logger.info("Error in getting registration link ", e);
			logger.error(ExceptionUtils.getFullStackTrace(e));
		}
		logger.info("Exiting reRegForMobile() method of HomeRESTController");

		return new ResponseEntity<ReRegistrationStudentPortalBean>(response, headers, HttpStatus.OK);
	}
	
	@PostMapping(path = "/getSession", consumes = "application/json", produces = "application/json")
	private ResponseEntity<ArrayList<SessionDayTimeStudentPortal>> mgetAcademicCalendar(@RequestBody StudentStudentPortalBean student) {


		HttpHeaders headers = new HttpHeaders();
		headers.add("Content-Type","application/json");
		PortalDao pDao = (PortalDao)act.getBean("portalDAO");
		ArrayList<StudentMarksBean> allStudentRegistrations = pDao.getAllRegistrationsFromSAPID(student.getSapid());
		HashMap<String, StudentMarksBean> monthYearAndStudentRegistrationMap = new HashMap<>();
		for (StudentMarksBean bean : allStudentRegistrations) {
			monthYearAndStudentRegistrationMap.put(bean.getMonth() + "-" + bean.getYear(), bean);
		}
		List<ExamOrderStudentPortalBean> liveFlagList = pDao.getLiveFlagDetails();
		StudentMarksBean studentRegistrationData = getStudentRegistrationForForSpecificLiveSettings(monthYearAndStudentRegistrationMap, liveFlagList, "acadSessionLive");
		ArrayList<SessionDayTimeStudentPortal> scheduledSessionList = new ArrayList<>();
		if(studentRegistrationData == null) {
			return new ResponseEntity<ArrayList<SessionDayTimeStudentPortal>>(scheduledSessionList, headers, HttpStatus.UNAUTHORIZED);
		}
		//Check if student has FAA subject.
		Online_EventBean onlineEvent = pDao.getLiveOnlineEvent(studentRegistrationData.getProgram(),studentRegistrationData.getSem(),student.getPrgmStructApplicable());
		boolean registeredForEvent = false;
		if(onlineEvent != null){
			registeredForEvent = pDao.getOnlineEventRegistration(student.getSapid(),onlineEvent.getId());
		}
		student.setProgram(studentRegistrationData.getProgram());
		student.setSem(studentRegistrationData.getSem());
		//ArrayList<String> subjects = getSubjectsForStudent(student);
		ArrayList<String> subjects  = studentCourseService.getCurrentCycleSubjects(student.getSapid(),studentRegistrationData.getYear(),studentRegistrationData.getMonth());
		scheduledSessionList = pDao.getScheduledSessionForStudents(subjects,student);
		return new ResponseEntity<ArrayList<SessionDayTimeStudentPortal>>(scheduledSessionList, headers, HttpStatus.OK);
	}
	
	@PostMapping(path="/Courses" , consumes = "application/json", produces = "application/json")
	public ResponseEntity<LinkedList<String>> mCourses (@RequestBody StudentStudentPortalBean student){

		PortalDao pDao = (PortalDao)act.getBean("portalDAO");
		ArrayList<StudentMarksBean> allStudentRegistrations = pDao.getAllRegistrationsFromSAPID(student.getSapid());
		HashMap<String, StudentMarksBean> monthYearAndStudentRegistrationMap = new HashMap<>();
		for (StudentMarksBean bean : allStudentRegistrations) {
			monthYearAndStudentRegistrationMap.put(bean.getMonth() + "-" + bean.getYear(), bean);
		}

		StudentStudentPortalBean studentDetail = pDao.getSingleStudentsData(student.getSapid());
		
		String liveTypeForCourses = "acadContentLive";
		List<ExamOrderStudentPortalBean> liveFlagList = pDao.getLiveFlagDetails();

		StudentMarksBean studentRegistrationData = getStudentRegistrationForForSpecificLiveSettings(monthYearAndStudentRegistrationMap, liveFlagList, liveTypeForCourses);

		ArrayList<String> allApplicableSubjects = new ArrayList<String>();
		ArrayList<String> currentSemSubjects = null;
		ArrayList<String> notPassedSubjects = null;


		if(studentRegistrationData != null){
			studentDetail.setSem(studentRegistrationData.getSem());
			studentDetail.setProgram(studentRegistrationData.getProgram());
			//currentSemSubjects = getSubjectsForStudent(studentDetail);
			currentSemSubjects = studentCourseService.getCurrentCycleSubjects(studentDetail.getSapid(),studentRegistrationData.getYear(),studentRegistrationData.getMonth());
		}
		

		if(currentSemSubjects == null){
			currentSemSubjects = new ArrayList<>();
		}
		
		studentService.mgetWaivedInSubjects(studentDetail);		// add waived in subjects
		studentService.mgetWaivedOffSubjects(studentDetail);	// add waived off subjects

		allApplicableSubjects.addAll(currentSemSubjects);
		ArrayList<String> waivedInSubjects = studentDetail.getWaivedInSubjects();
		if(waivedInSubjects != null) {
			for (String subject : waivedInSubjects) {
				if(!allApplicableSubjects.contains(subject)) {
					allApplicableSubjects.add(subject);
				}
			}
		}
		
		ArrayList<String> failedSubjects = pDao.getFailSubjectsNamesForAStudent(studentDetail.getSapid());
		if(failedSubjects != null){
			allApplicableSubjects.addAll(failedSubjects);
		} else{
			failedSubjects = new ArrayList<String>(); 
		}

		notPassedSubjects = pDao.getNotPassedSubjectsBasedOnSapid((studentDetail.getSapid()));
		if(notPassedSubjects != null && notPassedSubjects.size()>0){
			allApplicableSubjects.addAll(notPassedSubjects);
		}
		ArrayList<String> listOfApplicableSubjects = new ArrayList<String>(new LinkedHashSet<String>(allApplicableSubjects));
		//remove Waiveoff subject from applicable subject list
		for(String subjects: allApplicableSubjects)
		{
			if(studentDetail.getWaivedOffSubjects().contains(subjects)){
				listOfApplicableSubjects.remove(subjects);
			}
		}
		LinkedList<String> sub = new LinkedList<String>();
		for(int i = 0 ; i< listOfApplicableSubjects.size(); i++){
			sub.add(listOfApplicableSubjects.get(i));
		}
		return  new ResponseEntity<LinkedList<String>>(sub, HttpStatus.OK);
	}
	
	@PostMapping("/viewCourseHomePage")
	public ResponseEntity<HashMap<String, AssignmentStudentPortalFileBean>> mgenerateCourseAsignmentMap(HttpServletRequest request) {
		HashMap<String, StudentMarksBean> monthYearAndStudentRegistrationMap = new HashMap<>();
		PortalDao pDao = (PortalDao)act.getBean("portalDAO");
		List<ExamOrderStudentPortalBean> liveFlagList = pDao.getLiveFlagDetails();
		String liveTypeForCourses = "acadContentLive";
		StudentMarksBean studentRegistrationData = getStudentRegistrationForForSpecificLiveSettings(monthYearAndStudentRegistrationMap, liveFlagList, liveTypeForCourses);
		AssignmentsDAO dao = (AssignmentsDAO)act.getBean("assignmentsDAO");

		StudentStudentPortalBean student = new StudentStudentPortalBean();
		String sapId = student.getSapid();
		Boolean isOnline = isOnline(student);

		ArrayList<String> currentSemSubjects = new ArrayList<>();
		ArrayList<String> failSubjects = new ArrayList<>();
		ArrayList<String> applicableSubjects = new ArrayList<>();

		HashMap<String, String> subjectSemMap = new HashMap<>();

		String currentSem = null;

		int maxAttempts = Integer.parseInt(ASSIGNMENT_SUBMISSIONS_ATTEMPTS);

		currentSem = student.getSem();

		String sapid = request.getParameter("sapid");
		String program = request.getParameter("program");
		String prgmStructApplicable = request.getParameter("prgmStructApplicable");
		student.setSapid(request.getParameter("sapid"));
		student.setSem(request.getParameter("sem"));
		student.setProgram(request.getParameter("program"));
		student.setPrgmStructApplicable(request.getParameter("prgmStructApplicable"));
		if(studentRegistrationData != null){
			student.setSem(request.getParameter("sem"));
			student.setProgram(request.getParameter("program"));
			student.setPrgmStructApplicable(request.getParameter("prgmStructApplicable"));
			//currentSemSubjects = getSubjectsForStudent(student);
			currentSemSubjects = studentCourseService.getCurrentCycleSubjects(sapid,studentRegistrationData.getYear(),studentRegistrationData.getMonth());
			
		}
		
		
		//Get failed Subjects
		failSubjects = new ArrayList<>();


		ArrayList<AssignmentStudentPortalFileBean> failSubjectsBeans = getFailSubjects(student,pDao);

		if(failSubjectsBeans != null && failSubjectsBeans.size() > 0 )
		{
			for(int i = 0 ; i< failSubjectsBeans.size();i++) {
				String subject = failSubjectsBeans.get(i).getSubject();
				String sem = failSubjectsBeans.get(i).getSem();
				failSubjects.add(failSubjectsBeans.get(i).getSubject());
				subjectSemMap.put(subject,sem);
			}

		}



		ArrayList<AssignmentStudentPortalFileBean> failANSSubjectsBeans = getANSNotProcessed(student,pDao);
		if(failANSSubjectsBeans != null && failANSSubjectsBeans.size() > 0 )
		{
			for(int i = 0 ; i < failANSSubjectsBeans.size(); i++) {
				String subject = failANSSubjectsBeans.get(i).getSubject();
				String sem = failANSSubjectsBeans.get(i).getSem();
				failSubjects.add(failSubjectsBeans.get(i).getSubject());
				subjectSemMap.put(subject,sem);
			}
		}

		failSubjects.remove("Project");
		failSubjects.remove("Module 4 - Project");
		
		for(String failedSubject : failSubjects) {
			if(currentSemSubjects.contains(failedSubject)) {
				currentSemSubjects.remove(failedSubject);
			}
		}

		currentSemSubjects.remove("Project");
		currentSemSubjects.remove("Module 4 - Project");
		
		applicableSubjects.addAll(currentSemSubjects);
		applicableSubjects.addAll(failSubjects);
		applicableSubjects.remove("Project");
		applicableSubjects.remove("Module 4 - Project");




		//Get Assignment Files for failed & Current Subjects 

		List<AssignmentStudentPortalFileBean> allAssignmentFilesList = new ArrayList<>();

		if(!isOnline) {
			allAssignmentFilesList = dao.getAssignmentsForSubjects(applicableSubjects, student);

		}else {
			List<AssignmentStudentPortalFileBean> currentSemFiles = dao.getAssignmentsForSubjects(currentSemSubjects, student);
			List<AssignmentStudentPortalFileBean> failSubjectsFiles = dao.getResitAssignmentsForSubjects(failSubjects, student);
			if(currentSemFiles != null) {
				allAssignmentFilesList.addAll(currentSemFiles);
			}

			if(failSubjectsFiles != null) {
				allAssignmentFilesList.addAll(failSubjectsFiles);
			}
		}
		if(allAssignmentFilesList != null) {

			HashMap<String, AssignmentStudentPortalFileBean> subjectSubmissionMap = new HashMap<String, AssignmentStudentPortalFileBean>();
			if(!isOnline) {
				subjectSubmissionMap = dao.getSubmissionStatus(applicableSubjects, student.getSapid());
			}else {

				HashMap<String, AssignmentStudentPortalFileBean> currentSemSubjectSubmissionMap = dao.getSubmissionStatus(currentSemSubjects, sapId);
				HashMap<String, AssignmentStudentPortalFileBean> failSubjectSubbmissionMap = dao.getResitSubmissionStatus(failSubjects, sapId, student);

				if(currentSemSubjectSubmissionMap != null) {
					subjectSubmissionMap.putAll(currentSemSubjectSubmissionMap);
				}
				if(failSubjectSubbmissionMap != null) {
					subjectSubmissionMap.putAll(failSubjectSubbmissionMap);
				}
			}

			for(AssignmentStudentPortalFileBean assignment : allAssignmentFilesList){
				String subject = assignment.getSubject();
				String status = "Not Submitted";
				String attempts = "0";
				String lastModifiedDate = "";
				String previewPath = "";
				AssignmentStudentPortalFileBean studentSubmissionStatus = subjectSubmissionMap.get(subject);
				if(studentSubmissionStatus != null){
					status = studentSubmissionStatus.getStatus();
					attempts = studentSubmissionStatus.getAttempts();
					lastModifiedDate = studentSubmissionStatus.getLastModifiedDate();
					lastModifiedDate = lastModifiedDate.replaceAll("T", " ");
					lastModifiedDate = lastModifiedDate.substring(0,19);
					previewPath = studentSubmissionStatus.getPreviewPath();

				}
				assignment.setStatus(status);
				assignment.setAttempts(attempts);
				assignment.setAttemptsLeft((maxAttempts - Integer.parseInt(attempts))+"");
				assignment.setSem(subjectSemMap.get(subject));
				assignment.setLastModifiedDate(lastModifiedDate);
				assignment.setPreviewPath(previewPath);

			}



		}


		//List<AssignmentFileBean> allAssignmentFilesList = (ArrayList<AssignmentFileBean>)request.getSession().getAttribute("allAssignmentFilesList_studentportal");

		HashMap<String, AssignmentStudentPortalFileBean> courseAssignmentsMap = new HashMap();
		if(allAssignmentFilesList != null) {
			for(AssignmentStudentPortalFileBean assignment: allAssignmentFilesList) {
				courseAssignmentsMap.put(assignment.getSubject(), assignment);
			}
			return new ResponseEntity<HashMap<String, AssignmentStudentPortalFileBean>>(courseAssignmentsMap, HttpStatus.OK);

		}
		return new ResponseEntity<HashMap<String, AssignmentStudentPortalFileBean>>(courseAssignmentsMap, HttpStatus.OK);

	}
	
	@PostMapping("/studentTimeTable")
	public  ResponseEntity<List<TimetableStudentPortalBean>> mstudentTimeTable(HttpServletRequest request) {
		String sapid = request.getParameter("sapid");
		String program = request.getParameter("program");
		String prgmStructApplicable = request.getParameter("prgmStructApplicable");
		StudentStudentPortalBean student = new StudentStudentPortalBean();
		student.setPrgmStructApplicable(prgmStructApplicable);
		student.setProgram(program);		
		Map<String, String> data = new HashMap<String, String>();
		boolean isCorporate = false;
		PortalDao dao = (PortalDao)act.getBean("portalDAO");

		String mostRecentTimetablePeriod; // = dao.getMostRecentTimeTablePeriod();
		List<TimetableStudentPortalBean> timeTableList = dao.getStudentTimetableList(student,false);
		//modelnView.addObject("timeTableList", timeTableList);
		HashMap<String, ArrayList<TimetableStudentPortalBean>> programTimetableMap = new HashMap<>();
		//data.put("String", timeTableList);
		String examYear = "";
		String examMonth = "";

		for (int i = 0; i < timeTableList.size(); i++) {
			TimetableStudentPortalBean bean = timeTableList.get(i);
			examYear = bean.getExamYear();
			examMonth = bean.getExamMonth();
			if(!programTimetableMap.containsKey(bean.getProgram() + " - "+ bean.getPrgmStructApplicable() + " Program Structure")){
				ArrayList<TimetableStudentPortalBean> list = new ArrayList<>();
				list.add(bean);
				programTimetableMap.put(bean.getProgram() + " - "+ bean.getPrgmStructApplicable() + " Program Structure", list);
			}else{
				ArrayList<TimetableStudentPortalBean> list = programTimetableMap.get(bean.getProgram() + " - "+ bean.getPrgmStructApplicable() + " Program Structure");
				list.add(bean);
			}
		}

		mostRecentTimetablePeriod = examMonth + "-" + examYear;
		//SmodelnView.addObject("mostRecentTimetablePeriod", mostRecentTimetablePeriod);
	
		//programTimetableMap = TimeTableController.sortByKeys(programTimetableMap);
		TreeMap<String,  ArrayList<TimetableStudentPortalBean>> treeMap = new TreeMap<String,  ArrayList<TimetableStudentPortalBean>>(programTimetableMap);
		request.setAttribute("programTimetableMap", treeMap);
		return new ResponseEntity<List<TimetableStudentPortalBean>>(timeTableList, HttpStatus.OK);
	}
	
	@PostMapping(path = "/courseDetailsSessions", consumes = "application/json", produces = "application/json")	
	public ResponseEntity<HashMap<String, ArrayList<SessionDayTimeStudentPortal>>> m_courseDetailsSessions(@RequestBody StudentStudentPortalBean input) throws Exception {
		HttpHeaders headers = new HttpHeaders();
		headers.add("Content-Type", "application/json"); 
		HashMap<String, ArrayList<SessionDayTimeStudentPortal>> sessionList = new  HashMap<String, ArrayList<SessionDayTimeStudentPortal>>();
		sessionList = mgenerateCourseSessionsMap(input);
		return  new ResponseEntity<>(sessionList, headers,  HttpStatus.OK);
	}
	
	@PostMapping(path = "/courseDetailsDash", consumes = "application/json", produces = "application/json")	
	public ResponseEntity<List<Integer>> m_courseDetailsDash(@RequestBody StudentStudentPortalBean student) throws Exception {
		HttpHeaders headers = new HttpHeaders();
		headers.add("Content-Type", "application/json");
		PortalDao pDao = (PortalDao)act.getBean("portalDAO");
		List<Integer> sessionList = new  ArrayList<Integer>();
		String subject = student.getSubject();
		student = pDao.getSingleStudentsData(student.getSapid());
		
		StudentStudentPortalBean studentRegistrationForAcademicSession = registrationHelper.checkStudentRegistration(student.getSapid(), student);
		ExamOrderStudentPortalBean examOrderForSession = pDao.getExamOrderByYearMonth(studentRegistrationForAcademicSession.getYear(), studentRegistrationForAcademicSession.getMonth());
		
		if (examOrderForSession != null) {
			int session_pending = mgetAllPendingSessions(subject, student.getConsumerProgramStructureId(), studentRegistrationForAcademicSession);
			int session_scheduled = mgetAllScheduledSessions(subject, student.getConsumerProgramStructureId(), studentRegistrationForAcademicSession);
			int session_attented = mgetAllAttendedSessions(subject, student.getConsumerProgramStructureId(), studentRegistrationForAcademicSession);
			int session_conducted = mgetAllConductedSessions(subject, student.getConsumerProgramStructureId(), studentRegistrationForAcademicSession);
			sessionList.add(session_pending);
			sessionList.add(session_scheduled);
			sessionList.add(session_attented);
			sessionList.add(session_conducted);
		}else {
			sessionList.add(0);
			sessionList.add(0);
			sessionList.add(0);
			sessionList.add(0);
		}
		
		return  new ResponseEntity<>(sessionList, headers,  HttpStatus.OK);
	}
	
	@PostMapping(path = "/courseDetailsSessionsSingleStudentAttendanceforSubject", consumes = "application/json", produces = "application/json")	
	public ResponseEntity<List<SessionAttendanceFeedbackDto>> m_courseDetailsSessionsSingleStudentAttendanceforSubject(@RequestBody StudentStudentPortalBean input) throws Exception {
		HttpHeaders headers = new HttpHeaders();
		headers.add("Content-Type", "application/json"); 
		PortalDao pDao = (PortalDao)act.getBean("portalDAO");		
		List<SessionAttendanceFeedbackStudentPortal> sessionListSingleStudentAttendanceforSubject = new ArrayList<SessionAttendanceFeedbackStudentPortal>();
		List<SessionAttendanceFeedbackDto> sessionListSingleStudentAttendanceforSubjectList = new ArrayList<SessionAttendanceFeedbackDto>();
		SessionAttendanceFeedbackDto sessionAttendanceFeedbackDto = null;
		
		StudentStudentPortalBean student = pDao.getSingleStudentsData(input.getSapid());
		String subject = input.getSubject();
		String earlyAccess = checkEarlyAccess(input.getSapid());
		if (earlyAccess.equalsIgnoreCase("No")) {
			
			sessionListSingleStudentAttendanceforSubject = mgetSingleStudentAttendanceforSubject(student, subject);
			
			for(SessionAttendanceFeedbackStudentPortal bean: sessionListSingleStudentAttendanceforSubject)
			{
				sessionAttendanceFeedbackDto = new SessionAttendanceFeedbackDto();
				sessionAttendanceFeedbackDto.setDate(bean.getDate());
				sessionAttendanceFeedbackDto.setStartTime(bean.getStartTime());
				sessionAttendanceFeedbackDto.setSubject(bean.getSubject());
				sessionAttendanceFeedbackDto.setSessionName(bean.getSessionName());
				sessionAttendanceFeedbackDto.setFacultyFirstName(bean.getFacultyFirstName());
				sessionAttendanceFeedbackDto.setFacultyLastName(bean.getFacultyLastName());
				sessionAttendanceFeedbackDto.setId(bean.getId());
				sessionAttendanceFeedbackDto.setConducted(bean.getConducted());
				sessionAttendanceFeedbackDto.setTrack(bean.getTrack());
				
				sessionListSingleStudentAttendanceforSubjectList.add(sessionAttendanceFeedbackDto);
				
				
			}
		}
		
		return  new ResponseEntity<List<SessionAttendanceFeedbackDto>>(sessionListSingleStudentAttendanceforSubjectList, headers,  HttpStatus.OK);
	}
	
	protected ResultsFromRedisHelper fetchRedisHelper() {
 		ResultsFromRedisHelper resultsFromRedisHelper = null;
 		resultsFromRedisHelper = (ResultsFromRedisHelper) act.getBean("resultsFromRedisHelper");
 		return resultsFromRedisHelper;
 	}
	
	@PostMapping(path = "/courseDetailsResults", consumes = "application/json", produces = "application/json")	
	public ResponseEntity<List<PassFailBean>> m_courseDetailsResults(@RequestBody StudentStudentPortalBean input) throws Exception {
	HttpHeaders headers = new HttpHeaders();
    headers.add("Content-Type", "application/json"); 
    try {
	//if(checkIfMovingResultsToCache()) {
    	if(this.fetchRedisHelper().sendingResultsToCache()) {
    		List<PassFailBean> response = new ArrayList<>();
    		return new ResponseEntity<List<PassFailBean>>(response, headers,  HttpStatus.OK);
    	}
	} catch (Exception ex) {
//		ex.printStackTrace();
		logger.error("HomeRESTController: m_courseDetailsResults : error : "+ ex.getMessage());
		//if REDIS stopped - exception caught - page loading continued -Vilpesh on 2021-11-24
	}
    List<PassFailBean> resultList = new ArrayList<PassFailBean>();
    
    
   
    resultList =  mgenerateCourseResultsMap(input);
//    List<StudentMarksBean> resultList = mgenerateCourseMarksHistoryMap(input); old method
    return new ResponseEntity<>(resultList, headers,  HttpStatus.OK);
}
	
	@PostMapping(path = "/courseDetailsResources", consumes = "application/json", produces = "application/json")	
	public ResponseEntity<List<ContentStudentPortalBean>> m_courseDetailsResources(@RequestBody StudentStudentPortalBean input) throws Exception {
		HttpHeaders headers = new HttpHeaders();
		headers.add("Content-Type", "application/json"); 
		List<ContentStudentPortalBean> resourceList = new ArrayList<ContentStudentPortalBean>();
		if(input.getProgramSemSubjectId() != null) {
			resourceList =	mgenerateCourseLearningResourcesMapNewForLR(input, input.getProgramSemSubjectId());
		}else {
			resourceList = mgenerateCourseLearningResourcesMap(input);	
		}
		return new ResponseEntity<>(resourceList, headers,  HttpStatus.OK);
	}
	@PostMapping(path = "/courseDetailsResourcesLastCycle", consumes = "application/json", produces = "application/json")	
	public ResponseEntity<List<ContentStudentPortalBean>> m_courseDetailsResourcesLastCycle(@RequestBody StudentStudentPortalBean input) throws Exception {
		HttpHeaders headers = new HttpHeaders();
		headers.add("Content-Type", "application/json"); 
		List<ContentStudentPortalBean> resourceList = new ArrayList<ContentStudentPortalBean>();
		resourceList = mgenerateCourseLearningResourcesMapLastCycle(input);	
		return new ResponseEntity<>(resourceList, headers,  HttpStatus.OK);
	}


	@PostMapping(path = "/courseDetailsQueries", consumes = "application/json", produces = "application/json")	
	public ResponseEntity<List<SessionQueryAnswerStudentPortal>> m_courseDetailsQueries(@RequestBody StudentStudentPortalBean input) throws Exception {
		HttpHeaders headers = new HttpHeaders();
		headers.add("Content-Type", "application/json"); 
		List<SessionQueryAnswerStudentPortal> queryList = new ArrayList<SessionQueryAnswerStudentPortal>();
		queryList = mgetCourseQueriesMap(input);
		return new ResponseEntity<>(queryList, headers,  HttpStatus.OK);
	}

	@PostMapping(path = "/courseDetailsForum", consumes = "application/json", produces = "application/json")	
	public ResponseEntity<List<ForumStudentPortalBean>> m_courseDetailsForum(@RequestBody StudentStudentPortalBean input) throws Exception {
		HttpHeaders headers = new HttpHeaders();
		headers.add("Content-Type", "application/json"); 
		List<ForumStudentPortalBean> forumList = new ArrayList<ForumStudentPortalBean>();
		forumList = mgetForumBasedOnSubjects(input);
		return new ResponseEntity<>(forumList, headers,  HttpStatus.OK);
	}
	
	@PostMapping(path = "/courseDetailsAssignments", consumes = "application/json", produces = "application/json")	
	public ResponseEntity<List<AssignmentStudentPortalFileBean>> m_courseDetailsAssignments(@RequestBody StudentStudentPortalBean input) throws Exception {
		HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Type", "application/json"); 
        List<AssignmentStudentPortalFileBean> assignmentList = new ArrayList<AssignmentStudentPortalFileBean>();
        
		if(checkIfMovingResultsToCache()) {
			return new ResponseEntity<List<AssignmentStudentPortalFileBean>>(assignmentList, headers,  HttpStatus.OK);
		}
        assignmentList = 	mgenerateCourseAssignmentsMap(input);
        return new ResponseEntity<>(assignmentList, headers,  HttpStatus.OK);
		}
	
	@PostMapping(path = "/courseDetailsAssignmentResults", consumes = "application/json", produces = "application/json")	
	public ResponseEntity<List<AssignmentStudentPortalFileBean>> mcourseDetailsAssignmentResults(@RequestBody StudentStudentPortalBean input) throws Exception {
		HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Type", "application/json"); 

		if(checkIfMovingResultsToCache()) {
			List<AssignmentStudentPortalFileBean> response = new ArrayList<>();
			return new ResponseEntity<List<AssignmentStudentPortalFileBean>>(response, headers,  HttpStatus.OK);
		}
//        List<PassFailBean> resultList = new ArrayList<PassFailBean>();
//        resultList =  mgenerateCourseResultsMap(input);
        List<AssignmentStudentPortalFileBean> resultList = mgetAssignmentSubmissionHistoryBySubject(input.getSapid(),input.getSubject());
        return new ResponseEntity<>(resultList, headers,  HttpStatus.OK);
	}
	
	
//**************	Shifted in AnnouncementStudentRESTController********************//
	
//	@PostMapping(path = "/getAllStudentAnnouncements",consumes = "application/json", produces = "application/json")
//	public ResponseEntity<List<AnnouncementBean>> getAllStudentAnnouncements(HttpServletRequest request, HttpServletResponse response , @RequestBody Person input){
//		HttpHeaders headers = new HttpHeaders();
//		headers.add("Content-Type", "application/json"); 
//		String userId = input.getUserId();
//
//
//		PortalDao dao = (PortalDao)act.getBean("portalDAO");
//		StudentBean student =dao.getSingleStudentsData(userId);
//
//
//		Page<AnnouncementBean> announcementspage = new Page<AnnouncementBean>();
//		String consumerProgramStructureId = student.getConsumerProgramStructureId();
//
//
//		List<AnnouncementBean> announcements = new ArrayList<AnnouncementBean>();
//
//		try {
//			announcements = dao.getAllActiveAnnouncements(student.getProgram(),consumerProgramStructureId);							  
//		}catch(Exception e) {
//			e.printStackTrace();
//		}
//		//Added temp for hiding Announcements for new batch
//		if(student.getEnrollmentMonth().equalsIgnoreCase("Oct")) {
//			announcements = new ArrayList<AnnouncementBean>();
//		}
//		//			int announcementSize = announcements != null ? announcements.size() : 0;
//		return new ResponseEntity<List<AnnouncementBean>>(announcements, headers, HttpStatus.OK);
//
//
//	}

	
	//For updating profile in sfdc and portal from mobile//
	@PostMapping(path = "/saveProfileForSFDCAndPortal", consumes="application/json", produces="application/json")
	 public ResponseEntity<StudentStudentPortalBean> saveProfileForSFDCAndPortalFromMobile(@RequestBody StudentStudentPortalBean input){
		 /*,
		 @RequestParam("shippingStreet") String shippingStreet,
		 @RequestParam("shippingCity") String shippingCity,
		 @RequestParam("shippingState") String shippingState,
		 @RequestParam("shippingPostalCode") String shippingPostalCode,
		 @RequestParam("shippingCountry") String shippingCountry,
		 @RequestParam("shippingLocalityName") String shippingLocalityName,
		 @RequestParam("shippingNearestLandmark") String shippingNearestLandmark,
		 @RequestParam("shippingHouseName") String shippingHouseName){*/
	
	//	Map<String,String> response = new HashMap<String,String>();
		StudentStudentPortalBean response = new StudentStudentPortalBean();
		
		String email = input.getEmailId();
		String mobile = input.getMobile();
		String altMobile = input.getAltPhone();
		
		//Get Fathers Name and Mothers Name
		String fatherName = input.getFatherName();
		String motherName = input.getMotherName();
		
		//Get industry and designation
		String industry = input.getIndustry();
		String designation = input.getDesignation();
		
		//Shipping Address Fields from update profile page//
		String shippingHouseName = input.getHouseNoName();
		String shippingStreet = input.getStreet();
		String shippingLocalityName = input.getLocality();
	//	String shippingNearestLandmark = input.getLandMark();
		String shippingPostalCode = input.getPin();
		String shippingCity = input.getCity();
		String shippingState = input.getState();
		String shippingCountry = input.getCountry();
		
		String abcId = input.getAbcId();
		
		String postalAddress = shippingHouseName + ", " + shippingLocalityName+","+shippingStreet+","
							  +shippingPostalCode+", "+shippingCity;
		//end//
	
		/*StudentBean student = (StudentBean)request.getSession().getAttribute("student_studentportal");
		student.setEmailId(email);
		student.setMobile(mobile);
		request.getSession().setAttribute("student_studentportal", student);*/
	
		PersonStudentPortalBean person = new PersonStudentPortalBean();
		person.setEmail(email);
		person.setPostalAddress(postalAddress);
		person.setContactNo(mobile);
		person.setAltContactNo(altMobile);
	
		String userId = input.getSapid();
	
		LDAPDao dao = (LDAPDao)act.getBean("ldapdao");
		PortalDao pDao = (PortalDao)act.getBean("portalDAO");
		StudentStudentPortalBean student = pDao.getSingleStudentsData(userId);
		
		//Set up new values in studentBean
		student.setFatherName(fatherName);
		student.setMotherName(motherName);
		student.setDesignation(designation);
		student.setIndustry(industry);
		student.setHouseNoName(shippingHouseName);
		student.setStreet(shippingStreet);
		student.setLocality(shippingLocalityName);
	//	student.setLandMark(shippingNearestLandmark);
		student.setPostalCode(shippingPostalCode);
		student.setCity(shippingCity);
		student.setState(shippingState);
		student.setCountry(shippingCountry);
		student.setAbcId(abcId);
		
		MailSender mailer = (MailSender)act.getBean("mailer");
		String errorMessage = "";
		try{
	
			String enrollmentYearAndMonth = student.getEnrollmentMonth()+","+student.getEnrollmentYear();
	
			SimpleDateFormat month_yearFormat = new SimpleDateFormat("MMM,yyyy");
			SimpleDateFormat fulldateFormat = new SimpleDateFormat("dd-MM-yyyy");
	
			// format year and Month into date 
			Date dateR = month_yearFormat.parse(enrollmentYearAndMonth);
			String fullFormatedDate = fulldateFormat.format(dateR);
	
			Date enrollmentDate = fulldateFormat.parse(fullFormatedDate);
			Date salesforceUseStartDate = fulldateFormat.parse("01-06-2014"); // byPass Date for Student 
	
			// bypass student before Jul2014 as Student record not present in Salesforce  //
			if(enrollmentDate.after(salesforceUseStartDate) && "PROD".equalsIgnoreCase(ENVIRONMENT))
			{
				errorMessage = salesforceHelper.updateSalesforceProfile(userId,email,mobile,fatherName,motherName,shippingStreet,shippingCity,shippingState,
						shippingPostalCode,shippingCountry,shippingLocalityName,
						shippingHouseName,altMobile);//This is to update Students Shipping Address in SFDC//
			}
	
	
			if(errorMessage == null || "".equals(errorMessage)) 
			{
				dao.updateProfile(userId, email, mobile, altMobile);//Update Details in LDAP//
				pDao.updateStudentContact(student, postalAddress, mailer);// Update Details in exam.student Table
				response = pDao.getSingleStudentsData(student.getSapid());
				response.setStatus("success");
				if("PROD".equalsIgnoreCase(ENVIRONMENT)){
				//update IdCard after the student update
				idCardService.updateIdCard(student);
				}
	//			response.put("success","true");
	//			response.put("successMessage","Profile updated successfully.");
			}else
			{
				response.setStatus("error");
				response.setErrorMessage(errorMessage);
	//			response.put("error","true");
	//			response.put("errorMessage",errorMessage);
				pDao.updateErrorFlag(userId,errorMessage,mailer);
			}
		}catch(Exception e){
			response.setStatus("error");
			response.setErrorMessage(errorMessage);
	//		response.put("error","true");
	//		response.put("errorMessage", "Error in updating profile.");
			pDao.updateErrorFlag(userId,errorMessage,mailer);
		}
		HttpHeaders headers = new HttpHeaders();
		headers.add("Content-Type", "application/json"); 
		
		return new ResponseEntity<StudentStudentPortalBean>(response,headers,HttpStatus.OK);
	}
	
	//For updating onesignal id from mobile//
	@PostMapping(path = "/updateOneSignalId", consumes="application/json", produces="application/json")
	public ResponseEntity<Map<String,String>> updateOneSignalId(@RequestBody StudentStudentPortalBean input,
			@RequestParam("onsignalId") String onsignalId){
		Map<String,String> response = new HashMap<String,String>(); 
		String userId = input.getSapid();
		String firebaseToken = input.getFirebaseToken();

		PortalDao pDao = (PortalDao)act.getBean("portalDAO");
		if(onsignalId !="undefined") {
			try{
				pDao.updateOneSignalId(userId, onsignalId);// Update Details in exam.student Table //request.getSession().setAttribute("student_studentportal", student);
				pDao.updateFirebaseToken(userId, firebaseToken);
				response.put("success","true");
				response.put("successMessage","Profile updated successfully.");
			}catch(Exception e){
				response.put("error","true");
				response.put("errorMessage", "Error in updating profile.");
			}
		}
		HttpHeaders headers = new HttpHeaders();
		headers.add("Content-Type", "application/json"); 

		return new ResponseEntity<Map<String, String>>(response,headers,HttpStatus.OK);
	}
	
	@PostMapping(path = "/updateProfile", consumes = "application/json", produces = "application/json")
	public ResponseEntity<Map<String, Map>> mupdateProfile(@RequestBody StudentStudentPortalBean input) {
		//			if(!checkSession(request, response)){
		//				return "login";
		//			}
		HttpHeaders headers = new HttpHeaders();
		headers.add("Content-Type", "application/json"); 
		logger.info("Sending to update profile page");
		Map<String,Map> response = new HashMap<String,Map>();
		String userId = input.getSapid();
		PortalDao pDao = (PortalDao)act.getBean("portalDAO");
		StudentStudentPortalBean student = pDao.getSingleStudentsData(userId);
		String enrollmentYearAndMonth = student.getEnrollmentMonth()+","+student.getEnrollmentYear();
		try{
			SimpleDateFormat month_yearFormat = new SimpleDateFormat("MMM,yyyy");
			SimpleDateFormat fulldateFormat = new SimpleDateFormat("dd-MM-yyyy");

			// format year and Month into date 
			Date dateR = month_yearFormat.parse(enrollmentYearAndMonth);
			String fullFormatedDate = fulldateFormat.format(dateR);

			Date enrollmentDate = fulldateFormat.parse(fullFormatedDate);
			Date salesforceUseStartDate = fulldateFormat.parse("01-06-2014"); // byPass Date for Student
			if(enrollmentDate.after(salesforceUseStartDate))
			{
				HashMap<String,String> mapOfShippingAddress = salesforceHelper.getShippingAddressOfStudent((String)input.getSapid()); 
				response.put("mapOfShippingAddress",mapOfShippingAddress);
				//					response.put("showShippingAddress","Yes");

				//					m.addAttribute("showShippingAddress", "Yes");
				//					m.addAttribute("student",student);
			}else{
				//					m.addAttribute("showShippingAddress", "No");
				//					m.addAttribute("student",student);
			}
		}catch(Exception e){
//			e.printStackTrace();
		}
		//
		//			//Done in this manner since the page does not send values using form bind of spring//
		if(student.getIndustry()!=null && !"".equals(student.getIndustry())){
			//				m.addAttribute("industryList", industryList);
		}else{
			//				m.addAttribute("industryList", industryList);
		}
		//
		if(student.getDesignation()!=null && !"".equals(student.getDesignation())){
			//				m.addAttribute("designationList",designationList);
		}else{
			//				m.addAttribute("designationList", designationList);
		}
		//
		//			
		return new ResponseEntity<Map<String, Map>>(response, headers,  HttpStatus.OK);

	}
	
	//Get Upcoming Exam List 	
	@PostMapping(path = "/getUpcomingExamList", consumes="application/json", produces="application/json")
	public ResponseEntity<ArrayList<ExamBookingTransactionStudentPortalBean>> mgetUpcomingExamList(@RequestBody StudentStudentPortalBean postInput) {
		HttpHeaders headers = new HttpHeaders();
		headers.add("Content-Type", "application/json"); 
		PortalDao pDao = (PortalDao)act.getBean("portalDAO");
		List<ExamBookingTransactionStudentPortalBean> upcomingExams = null;
		try {
			upcomingExams = pDao.getUpcomingExams(postInput.getSapid());
		} catch(Exception e) {
//			e.printStackTrace();
		}
		return new ResponseEntity(upcomingExams, headers, HttpStatus.OK);
	}
	
	@CrossOrigin(origins = "*", allowedHeaders = "*")
	@PostMapping(path="/getStudentDataForUpdate")
	public ResponseEntity<AuthenticateResponseBean> mgetStudentDataForUpdate(@RequestBody StudentStudentPortalBean request) {
		HttpHeaders headers = new HttpHeaders();
		headers.add("Content-Type", "application/json");
		AuthenticateResponseBean response = new AuthenticateResponseBean();
		StudentInfoCheckDAO studentDao = (StudentInfoCheckDAO)act.getBean("stuentInfoCheckDAO");
		StudentStudentPortalBean data = studentDao.getstudentData(request.getSapid()); 	//add sapId where
		if(data.getLastModifiedDate().equalsIgnoreCase(request.getLastModifiedDate())) {
			response.setStatus("No update found");
			return new ResponseEntity<AuthenticateResponseBean>(response,headers,HttpStatus.OK);
		}
		data = this.replaceNullToEmpty(data);
		response.setData(data);
		response.setStatus("update found");
		return new ResponseEntity<AuthenticateResponseBean>(response,headers,HttpStatus.OK);
	}
	
	@PostMapping(path = "/getIndustryDesignation")
	private ResponseEntity<StudentStudentPortalBean> getIndustryDesignation(){
	
		HttpHeaders headers = new HttpHeaders();
		headers.add("Content-Type", "application/json");
		PortalDao pDao = (PortalDao)act.getBean("portalDAO");
		
		StudentStudentPortalBean response = new StudentStudentPortalBean();
		ArrayList<String> industryList = pDao.getIndustryList();
		
		response.setDesignationList(designationList);
		response.setIndustryList(industryList);
		
		return new ResponseEntity<StudentStudentPortalBean>(response, headers, HttpStatus.OK);
	}
	
	
    @PostMapping(path = "/CheckDemoExamStatus", consumes = "application/json", produces = "application/json")
	public ResponseEntity<HashMap<String, String>> mCheckDemoExamStatus(HttpServletRequest request, @RequestBody StudentStudentPortalBean student) throws Exception {

		HashMap<String, String> response = new HashMap<>();
		PortalDao pDao = (PortalDao)act.getBean("portalDAO");
		HttpHeaders headers = new HttpHeaders();
		headers.add("Content-Type", "application/json");
		response.put("isDemoExamPending", "false");
		return new ResponseEntity<>(response, headers, HttpStatus.OK);
		/*boolean result = pDao.isDemoExamPending(student.getSapid());
		if (result) {
			response.put("isDemoExamPending", "true");
			return new ResponseEntity<>(response, headers, HttpStatus.OK);
		}else{
			response.put("isDemoExamPending", "false");
			return new ResponseEntity<>(response, headers, HttpStatus.OK);
		}*/

	} 
    

    
    @PostMapping(path = "/getTodaysSessions", consumes = "application/json", produces = "application/json")
    public ResponseEntity<ArrayList<SessionDayTimeStudentPortal>> getTodaysSessions(HttpServletRequest request,@RequestBody PersonStudentPortalBean input) throws Exception {

    	String userId = input.getSapId();
		HttpHeaders headers = new HttpHeaders();
		headers.add("Content-Type", "application/json");
		PortalDao pDao = (PortalDao)act.getBean("portalDAO");
		ArrayList<SessionDayTimeStudentPortal> sessionList = new ArrayList<SessionDayTimeStudentPortal>();
		ArrayList<Integer> currentSemPSSId = new ArrayList<Integer>();
		
		StudentStudentPortalBean student = pDao.getSingleStudentsData(userId);
		StudentStudentPortalBean studentRegistrationForAcademicSession = new StudentStudentPortalBean();
		
		//added by Tushar for new PSS id list logic
		studentRegistrationForAcademicSession = registrationHelper.checkStudentRegistration(userId, student);
		
		//added by tushar to fetch common session
		
		if (studentRegistrationForAcademicSession != null) {
			ArrayList<SessionDayTimeStudentPortal> commonSessionsList = pDao.getTodaysCommonSessionsByCPSId(studentRegistrationForAcademicSession);
			sessionList.addAll(commonSessionsList);
		}
		
		if(input.getApplicablePSSId()==null) {
			try {
				if (studentRegistrationForAcademicSession != null) {
					//Set up latest semester
					student.setSem(studentRegistrationForAcademicSession.getSem());
				//	ArrayList<String> waivedOffSubjects = studentService.mgetWaivedOffSubjects(student);
					currentSemPSSId = studentCourseService.getPSSID(studentRegistrationForAcademicSession.getSapid(),studentRegistrationForAcademicSession.getYear(),studentRegistrationForAcademicSession.getMonth());
					sessionList.addAll(pDao.getTodaysSessionsByPSSId(currentSemPSSId,studentRegistrationForAcademicSession));
					if (sessionList.size() > 0) {
						return new ResponseEntity<>(sessionList,headers, HttpStatus.OK);
					}
				}
			} catch (Exception e) {
				return new ResponseEntity<>(sessionList,headers, HttpStatus.OK);
			}
		}else {
			sessionList.addAll(pDao.getTodaysSessionsByPSSId(input.getApplicablePSSId(),studentRegistrationForAcademicSession));
			if (sessionList.size() > 0) {
				return new ResponseEntity<>(sessionList,headers, HttpStatus.OK);
			}
		}
		
		return new ResponseEntity<>(sessionList,headers, HttpStatus.OK);
    }
    
    
    
    
    
    ////////////////////////////////functions////////////////////////////////
    
    
	public boolean checkIfMovingResultsToCache() {
		
		FlagBean flagBean = apiCallToGetFlagValueByKey("movingResultsToCache");
		
		if(flagBean != null) {
			if("Y".equalsIgnoreCase(flagBean.getValue())) {
				return true;
			}
		}
		
		return false;
	}
	
	private List<StudentMarksBean> generateCourseMarksHistoryMap(HttpServletRequest request,String subject) {
		ResultDAO resultDAO = (ResultDAO) act.getBean("resultDAO");
		StudentStudentPortalBean student = (StudentStudentPortalBean)request.getSession().getAttribute("student_studentportal");
		List<StudentMarksBean> studentMarksBeanList = resultDAO.getAStudentsMarksForSubject(student.getSapid(), subject);
		
		request.getSession().setAttribute("studentMarksBeanList", studentMarksBeanList);
		return studentMarksBeanList;
	}
	
	protected boolean isStudentValid(StudentStudentPortalBean student, String userId) throws ParseException {
		String date = "";
		if(userId.startsWith("77")){
			String validityEndMonthStr = student.getValidityEndMonth();
			int validityEndYear = Integer.parseInt(student.getValidityEndYear());

			
			Date lastAllowedAcccessDate = null;
			int validityEndMonth = 0;
			if("Jun".equals(validityEndMonthStr)){
				validityEndMonth = 6;
				date = validityEndYear + "/" + validityEndMonth + "/" + "30";
				lastAllowedAcccessDate = formatter.parse(date);
			}else if("Dec".equals(validityEndMonthStr)){
				validityEndMonth = 12;
				date = validityEndYear + "/" + validityEndMonth + "/" + "31";
				lastAllowedAcccessDate = formatter.parse(date);
			}else if("Sep".equals(validityEndMonthStr)){
				validityEndMonth = 9;
				date = validityEndYear + "/" + validityEndMonth + "/" + "30";
				lastAllowedAcccessDate = formatter.parse(date);
			}else if("Apr".equals(validityEndMonthStr)){
				validityEndMonth = 4;
				date = validityEndYear + "/" + validityEndMonth + "/" + "30";
				lastAllowedAcccessDate = formatter.parse(date);
			}else if("Oct".equals(validityEndMonthStr)){
				validityEndMonth = 10;
				date = validityEndYear + "/" + validityEndMonth + "/" + "31";
				lastAllowedAcccessDate = formatter.parse(date);
			}else if("Aug".equals(validityEndMonthStr)){
				validityEndMonth = 8;
				date = validityEndYear + "/" + validityEndMonth + "/" + "31";
				lastAllowedAcccessDate = formatter.parse(date);
			}else if("Feb".equals(validityEndMonthStr)){
				validityEndMonth = 2;
				date = validityEndYear + "/" + validityEndMonth + "/" + "28";
				lastAllowedAcccessDate = formatter.parse(date);
			}else if("Mar".equals(validityEndMonthStr)){
				validityEndMonth = 3;
				date = validityEndYear + "/" + validityEndMonth + "/" + "31";
				lastAllowedAcccessDate = formatter.parse(date);
			}else if("Jan".equals(validityEndMonthStr)){
				validityEndMonth = 1;
				date = validityEndYear + "/" + validityEndMonth + "/" + "31";
				lastAllowedAcccessDate = formatter.parse(date);
			}else if("May".equals(validityEndMonthStr)){
				validityEndMonth = 5;
				date = validityEndYear + "/" + validityEndMonth + "/" + "31";
				lastAllowedAcccessDate = formatter.parse(date);
			}else if("Jul".equals(validityEndMonthStr)){
				validityEndMonth = 7;
				date = validityEndYear + "/" + validityEndMonth + "/" + "31";
				lastAllowedAcccessDate = formatter.parse(date);
			}else if("Nov".equals(validityEndMonthStr)){
				validityEndMonth = 11;
				date = validityEndYear + "/" + validityEndMonth + "/" + "30";
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
			}


		}else{
			//Admin Staff login
			return true;
		}

	}
	
	// returns true if master key of MBAWx or Msc Ai & Ml
	/*private boolean isTimeboundWiseByConsumerProgramStructureId(String consumerProgramStructureId) {
		if("111".equalsIgnoreCase(consumerProgramStructureId)
		   || "131".equalsIgnoreCase(consumerProgramStructureId)		
			) {
			return true;
		}else {
			return false;
		}
	
	}*/
	
	// returns true if master key of MBAWx or Msc Ai & Ml
		private boolean isTimeboundWiseByConsumerProgramStructureId(String consumerProgramStructureId) {
			if(TIMEBOUND_PORTAL_LIST.contains(consumerProgramStructureId)) {
				return true;
			}else {
				return false;
			}
		}
	
	private HashMap<String, BigDecimal> generateExamOrderMap(List<ExamOrderStudentPortalBean> liveFlagList) {
		HashMap<String, BigDecimal> orderMap = new HashMap<String, BigDecimal>();
		for (ExamOrderStudentPortalBean row : liveFlagList) {
			orderMap.put(row.getMonth()+row.getYear(),BigDecimal.valueOf((long)Double.parseDouble( row.getOrder())));
			orderMap.put(row.getAcadMonth()+row.getYear(),BigDecimal.valueOf((long)Double.parseDouble( row.getOrder())));
		}
		return orderMap;
	}
	private double getMaxOrderWhereContentLive(List<ExamOrderStudentPortalBean> liveFlagList){
		double contentLiveOrder = 0.0;
		for (ExamOrderStudentPortalBean bean : liveFlagList) {
			double currentOrder = Double.parseDouble(bean.getOrder());
			if("Y".equalsIgnoreCase(bean.getAcadContentLive()) && currentOrder > contentLiveOrder){
				contentLiveOrder = currentOrder;
			}
		}
		return contentLiveOrder;
	}
	
	ArrayList<ContentStudentPortalBean> mgetLastCycleContentNewForLR(StudentStudentPortalBean student, String programSemSubjectId) {

    	PortalDao pDao = (PortalDao) act.getBean("portalDAO");

    	List<ContentStudentPortalBean> contentLastCycleList = new ArrayList<ContentStudentPortalBean>();

    	StudentStudentPortalBean studentreg = pDao.getStudentsMostRecentRegistrationData(student.getSapid());

    	List<ExamOrderStudentPortalBean> liveFlagList = pDao.getLiveFlagDetails();
    	HashMap<String,BigDecimal> examOrderMap = generateExamOrderMap(liveFlagList);
    	double acadContentLiveOrder = getMaxOrderWhereContentLive(liveFlagList);
    	double reg_order =  examOrderMap.get(studentreg.getMonth()+studentreg.getYear()).doubleValue();
    	//double current_order =  examOrderMap.get(CURRENT_ACAD_MONTH+CURRENT_ACAD_YEAR).doubleValue();

    	
    	/* Logic shifted in service layer
    	StringBuffer acadDateFormat = new StringBuffer();
    	if(reg_order == acadContentLiveOrder)
			acadDateFormat.append(ContentUtil.findLastAcadDate(studentreg.getYear(),studentreg.getMonth()));
		else
			acadDateFormat.append(ContentUtil.findLastAcadDate(CURRENT_ACAD_YEAR,CURRENT_ACAD_MONTH)); 
		*/
    	
    	contentLastCycleList = contentService.getContentByPssId(reg_order, acadContentLiveOrder, studentreg.getMonth(), studentreg.getYear()	, programSemSubjectId, studentreg.getSapid(), false);
    	//contentLastCycleList = pDao.getContentsForSubjectsForLastCyclesNew(programSemSubjectId,acadDateFormat.toString());
    	
    	
    	return (ArrayList<ContentStudentPortalBean>) contentLastCycleList;
    }
	
    ArrayList<ContentStudentPortalBean> mgetLastCycleContent(StudentStudentPortalBean input) {
		List<ContentStudentPortalBean> response = new ArrayList<ContentStudentPortalBean>();

		PortalDao pDao = (PortalDao)act.getBean("portalDAO");
		List<ContentStudentPortalBean> allLastCycleContentListForSubject = new ArrayList<ContentStudentPortalBean>();
		try {
			StudentInfoCheckDAO studentDao = (StudentInfoCheckDAO)act.getBean("stuentInfoCheckDAO");
			StudentStudentPortalBean student = studentDao.getstudentData(input.getSapid());

			allLastCycleContentListForSubject = pDao.getContentsForSubjectsForLastCycles(input.getSubject(),student.getConsumerProgramStructureId());
			
		
			String programStructureForStudent = input.getPrgmStructApplicable();
			for (ContentStudentPortalBean contentBean : allLastCycleContentListForSubject) {
				String programStructureForContent = contentBean.getProgramStructure();

				if ("113".equals(student.getConsumerProgramStructureId()) && "Business Economics".equalsIgnoreCase(input.getSubject()) && "M.sc".equals(programStructureForContent)) {
					response.add(contentBean);
				} else if("127".equalsIgnoreCase(student.getConsumerProgramStructureId()) || "128".equalsIgnoreCase(student.getConsumerProgramStructureId())){
					if (programStructureForContent.equals(programStructureForStudent)) {
						response.add(contentBean);
					}
				} else {
					if(programStructureForContent == null || "".equals(programStructureForContent.trim()) || "All".equals(programStructureForContent)){
						response.add(contentBean);
					}else if(programStructureForContent.equals(programStructureForStudent)){
						response.add(contentBean);
					}
				}
			}
			return (ArrayList<ContentStudentPortalBean>) response;

		} catch (Exception e) {
//			e.printStackTrace();
			return (ArrayList<ContentStudentPortalBean>) response;
		}
    }
    
	
	/**
	 * add new condition if you create new field in student database.
	 * */
	private StudentStudentPortalBean replaceNullToEmpty(StudentStudentPortalBean bean) {
		if(bean.getLastName() == null) {
			bean.setLastName("");
		}
		if(bean.getFirstName() == null) {
			bean.setFirstName("");
		}
		if(bean.getMiddleName() == null) {
			bean.setMiddleName("");
		}
		if(bean.getFatherName() == null) {
			bean.setFatherName("");
		}
		if(bean.getHusbandName() == null) {
			bean.setHusbandName("");
		}
		if(bean.getMotherName() == null) {
			bean.setMotherName("");
		}
		if(bean.getGender() == null) {
			bean.setGender("");
		}
		if(bean.getProgram() == null) {
			bean.setProgram("");
		}
		if(bean.getEnrollmentMonth() == null) {
			bean.setEnrollmentMonth("");
		}
		if(bean.getEnrollmentYear() == null) {
			bean.setEnrollmentYear("");
		}
		if(bean.getEmailId() == null) {
			bean.setEmailId("");
		}
		if(bean.getMobile() == null) {
			bean.setMobile("");
		}
		if(bean.getAltPhone() == null) {
			bean.setAltPhone("");
		}
		if(bean.getDob() == null) {
			bean.setDob("");
		}
		if(bean.getRegDate() == null) {
			bean.setRegDate("");
		}
		if(bean.getIsLateral() == null) {
			bean.setIsLateral("");
		}
		if(bean.getIsReReg() == null) {
			bean.setIsReReg("");
		}
		if(bean.getAddress() == null) {
			bean.setAddress("");
		}
		if(bean.getCity() == null) {
			bean.setCity("");
		}
		if(bean.getState() == null) {
			bean.setCity("");
		}
		if(bean.getCountry() == null) {
			bean.setCountry("");
		}
		if(bean.getPin() == null) {
			bean.setPin("");
		}
		if(bean.getCenterCode() == null) {
			bean.setCenterCode("");
		}
		if(bean.getCenterName() == null) {
			bean.setCenterName("");
		}
		if(bean.getValidityEndMonth() == null) {
			bean.setValidityEndMonth("");
		}
		if(bean.getValidityEndYear() == null) {
			bean.setValidityEndYear("");
		}
		if(bean.getCreatedDate() == null) {
			bean.setCreatedDate("");
		}
		if(bean.getCreatedBy() == null) {
			bean.setCreatedBy("");
		}
		if(bean.getLastModifiedBy() == null) {
			bean.setLastModifiedBy("");
		}
		if(bean.getLastModifiedDate() == null) {
			bean.setLastModifiedDate("");
		}
		if(bean.getPrgmStructApplicable() == null) {
			bean.setPrgmStructApplicable("");
		}
		if(bean.getUpdatedByStudent() == null) {
			bean.setUpdatedByStudent("");
		}
		if(bean.getProgramChanged() == null) {
			bean.setProgramChanged("");
		}
		if(bean.getImageUrl() == null) {
			bean.setImageUrl("");
		}
		if(bean.getOldProgram() == null) {
			bean.setOldProgram("");
		}
		if(bean.getPreviousStudentId() == null) {
			bean.setPreviousStudentId("");
		}
		if(bean.getProgramCleared() == null) {
			bean.setProgramCleared("");
		}
		if(bean.getProgramStatus() == null) {
			bean.setProgramStatus("");
		}
		if(bean.getProgramRemarks() == null) {
			bean.setProgramRemarks("");
		}
		if(bean.getIndustry() == null) {
			bean.setIndustry("");
		}
		if(bean.getDesignation() == null) {
			bean.setDesignation("");
		}
		if(bean.getEmailSentProgramCleared() == null) {
			bean.setEmailSentProgramCleared("");
		}
		if(bean.getExistingStudentNoForDiscount() == null) {
			bean.setExistingStudentNoForDiscount("");
		}
		if(bean.getOnesignalId() == null) {
			bean.setOnesignalId("");
		}
		if(bean.getDeRegistered() == null) {
			bean.setDeRegistered("");
		}
		if(bean.getHouseNoName() == null) {
			bean.setHouseNoName("");
		}
		if(bean.getStreet() == null) {
			bean.setStreet("");
		}
		if(bean.getLocality() == null) {
			bean.setLocality("");
		}
		if(bean.getLandMark() == null) {
			bean.setLandMark("");
		}
		if(bean.getErrorFlag() == null) {
			bean.setErrorFlag("");
		}
		if(bean.getErrorMessage() == null) {
			bean.setErrorMessage("");
		}
		if(bean.getTotalExperience() == null) {
			bean.setTotalExperience("");
		}
		if(bean.getAnnualSalary() == null) {
			bean.setAnnualSalary("");
		}
		if(bean.getCompanyName() == null) {
			bean.setCompanyName("");
		}
		if(bean.getUgQualification() == null) {
			bean.setUgQualification("");
		}
		if(bean.getAge() == null) {
			bean.setAge("");
		}
		if(bean.getHighestQualification() == null) {
			bean.setHighestQualification("");
		}
		if(bean.getConsumerType() == null) {
			bean.setConsumerType("");
		}
		if(bean.getConsumerProgramStructureId() == null) {
			bean.setConsumerProgramStructureId("");
		}
		return bean;
	}
	
	private StudentMarksBean getStudentRegistrationForForSpecificLiveSettings(
			HashMap<String, StudentMarksBean> monthYearAndStudentRegistrationMap,
			List<ExamOrderStudentPortalBean> liveFlagList, 
			String liveType) {

		double liveOrder = 0.0;
		String key = null;
		for (ExamOrderStudentPortalBean bean : liveFlagList) {
			double currentOrder = Double.parseDouble(bean.getOrder());

			if("acadSessionLive".equalsIgnoreCase(liveType)){
				if("Y".equalsIgnoreCase(bean.getAcadSessionLive()) && currentOrder > liveOrder){
					liveOrder = currentOrder;
					key = bean.getAcadMonth() + "-" + bean.getYear();
				}
			}else if("acadContentLive".equalsIgnoreCase(liveType)){
				if("Y".equalsIgnoreCase(bean.getAcadContentLive()) && currentOrder > liveOrder){
					liveOrder = currentOrder;
					key = bean.getAcadMonth() + "-" + bean.getYear();
				}
			}else if("assignmentLive".equalsIgnoreCase(liveType)){
				if("Y".equalsIgnoreCase(bean.getAssignmentLive()) && currentOrder > liveOrder){
					liveOrder = currentOrder;
					key = bean.getAcadMonth() + "-" + bean.getYear();
				}
			}else if("acadContentLiveNextBatch".equalsIgnoreCase(liveType)){
				if("Y".equalsIgnoreCase(bean.getAcadContentLive()) && currentOrder > liveOrder){
					liveOrder = currentOrder;
					key = bean.getAcadMonth() + "-" + bean.getYear();
				}
			}
		}

		if("acadContentLiveNextBatch".equalsIgnoreCase(liveType)){
			for (ExamOrderStudentPortalBean bean : liveFlagList) {
				double currentOrder = Double.parseDouble(bean.getOrder());
				if(currentOrder == (liveOrder + 1) ){
					key = bean.getAcadMonth() + "-" + bean.getYear();
				}
			}
		}
		return monthYearAndStudentRegistrationMap.get(key);
	}

	
	private ArrayList<String> getSubjectsForStudent(StudentStudentPortalBean student, HashMap<String, String> subjectSemMap) {
		ArrayList<ProgramSubjectMappingStudentPortalBean> programSubjectMappingList = getProgramSubjectMappingList();
		ArrayList<String> subjects = new ArrayList<>();

		for (int i = 0; i < programSubjectMappingList.size(); i++) {
			ProgramSubjectMappingStudentPortalBean bean = programSubjectMappingList.get(i);

			if(
					bean.getPrgmStructApplicable().equals(student.getPrgmStructApplicable()) 
					&& bean.getProgram().equals(student.getProgram())
					&& bean.getSem().equals(student.getSem())
					&& !student.getWaivedOffSubjects().contains(bean.getSubject())//Subjects has not already cleared it
					){

				subjects.add(bean.getSubject());
				subjectSemMap.put(bean.getSubject(), bean.getSem());
			}
		}

		return subjects;
	}

	private ArrayList<String> getSubjectsForStudent(StudentStudentPortalBean student) {

		ArrayList<ProgramSubjectMappingStudentPortalBean> programSubjectMappingList = getProgramSubjectMappingList();
		ArrayList<String> subjects = new ArrayList<>();
		for (int i = 0; i < programSubjectMappingList.size(); i++) {
			ProgramSubjectMappingStudentPortalBean bean = programSubjectMappingList.get(i);

			if(
					bean.getPrgmStructApplicable().equals(student.getPrgmStructApplicable()) 
					&& bean.getProgram().equals(student.getProgram())
					&& bean.getSem().equals(student.getSem())
					&& !student.getWaivedOffSubjects().contains(bean.getSubject())//Subjects has not already cleared it
					){
				subjects.add(bean.getSubject());
			}
		}
		
		return subjects;
	}
	
	private boolean isOnline(StudentStudentPortalBean student) {
		if("Online".equalsIgnoreCase(student.getExamMode())){
			return true;
		}else{
			return false;
		}
	}
	
	private ArrayList<AssignmentStudentPortalFileBean> getFailSubjects(StudentStudentPortalBean student, PortalDao dao) {
		ArrayList<AssignmentStudentPortalFileBean> failSubjectList = dao.getFailSubjectsForAStudent(student.getSapid());
		return failSubjectList;
	}
	
	private ArrayList<AssignmentStudentPortalFileBean> getANSNotProcessed(StudentStudentPortalBean student, PortalDao dao) {
		ArrayList<AssignmentStudentPortalFileBean> failSubjectList = dao.getANSNotProcessed(student.getSapid());
		return failSubjectList;
	}
	
	private HashMap<String, ArrayList<SessionDayTimeStudentPortal>> mgenerateCourseSessionsMap(StudentStudentPortalBean student) {

		ArrayList<SessionDayTimeStudentPortal> scheduledSessionList = mgetScheduledSessionList(student);
		HashMap<String, ArrayList<SessionDayTimeStudentPortal>> courseSessionsMap = new HashMap();

		if(scheduledSessionList != null){
			for (SessionDayTimeStudentPortal sessionDayTimeBean : scheduledSessionList) {
				if(!courseSessionsMap.containsKey(sessionDayTimeBean.getSubject())){
					ArrayList<SessionDayTimeStudentPortal> sessionList = new ArrayList<SessionDayTimeStudentPortal>();
					sessionList.add(sessionDayTimeBean);

					courseSessionsMap.put(sessionDayTimeBean.getSubject(),sessionList );
				}else{
					ArrayList<SessionDayTimeStudentPortal> sessionList = courseSessionsMap.get(sessionDayTimeBean.getSubject());
					sessionList.add(sessionDayTimeBean);
				}
			}


		}
		return courseSessionsMap;


	}
	
	private int mgetAllPendingSessions(String subject, String cpsId, StudentStudentPortalBean studentRegistrationForAcademicSession) {
		PortalDao pdao = (PortalDao)act.getBean("portalDAO"); 
		//int totalPendingSessions = pdao.getAllPendingSessions(student.getSubject(),student);
		int totalPendingSessions = pdao.getAllPendingSessionsNew(subject, cpsId, studentRegistrationForAcademicSession);
		return totalPendingSessions;
	} 

	private int mgetAllScheduledSessions(String subject, String cpsId, StudentStudentPortalBean studentRegistrationForAcademicSession) {
		PortalDao pdao = (PortalDao)act.getBean("portalDAO"); 
		//int totalSessions = pdao.getAllSessionsforSubject(student.getSubject(),student);
		int totalSessions = pdao.getAllSessionsforSubjectNew(subject, cpsId, studentRegistrationForAcademicSession);
		return totalSessions;
	}

	private int mgetAllAttendedSessions(String subject, String cpsId, StudentStudentPortalBean studentRegistrationForAcademicSession) {
		PortalDao pdao = (PortalDao)act.getBean("portalDAO"); 
		//int totalAttendedSessions = pdao.getAllAttendedSessions(student.getSubject(),student.getSapid(),student);
		int totalAttendedSessions = pdao.getAllAttendedSessionsNew(subject, cpsId, studentRegistrationForAcademicSession);
		return totalAttendedSessions;
	}
	private int mgetAllConductedSessions(String subject, String cpsId, StudentStudentPortalBean studentRegistrationForAcademicSession) {
		PortalDao pdao = (PortalDao)act.getBean("portalDAO"); 
		//int totalConductedSessions = pdao.getAllConductedSessionsforSubject(student.getSubject(),student);
		int totalConductedSessions = pdao.getAllConductedSessionsforSubjectNew(subject, cpsId, studentRegistrationForAcademicSession);
		return totalConductedSessions;
	}
	
	
	public String checkEarlyAccess(String userId) {
		String earlyAccess = "No";
		PortalDao pDao = (PortalDao)act.getBean("portalDAO");
		List<ExamOrderStudentPortalBean> liveFlagList = pDao.getLiveFlagDetails();
		HashMap<String,BigDecimal> examOrderMap = generateExamOrderMap(liveFlagList);
		StudentStudentPortalBean student = pDao.getSingleStudentsData(userId);
		double examOrderDifference = 0.0;
		double examOrderOfProspectiveBatch = examOrderMap.get(student.getEnrollmentMonth()+student.getEnrollmentYear()).doubleValue();
		double maxOrderWhereContentLive = getMaxOrderWhereContentLive(liveFlagList);
		examOrderDifference = examOrderOfProspectiveBatch - maxOrderWhereContentLive;

		if(examOrderDifference == 1){
			earlyAccess= "Yes";
		}
		return earlyAccess;
	}
	
	private List<SessionAttendanceFeedbackStudentPortal> mgetSingleStudentAttendanceforSubject(StudentStudentPortalBean student, String subject) {
		PortalDao pdao = (PortalDao)act.getBean("portalDAO");
		StudentStudentPortalBean studentRegistrationForAcademicSession = registrationHelper.checkStudentRegistration(student.getSapid(), student);
		//Commented by Somesh as now session will coming on CPS id
		//List<SessionAttendanceFeedback> SessionsAttendanceforSubjectList = pdao.getSingleStudentAttendanceforSubject(student.getSapid(),student.getSubject(),student);
		List<SessionAttendanceFeedbackStudentPortal> SessionsAttendanceforSubjectList = pdao.getSingleStudentAttendanceforSubjectNew(subject,student.getConsumerProgramStructureId(), studentRegistrationForAcademicSession);
		return SessionsAttendanceforSubjectList;
	}
	
	private List<PassFailBean> mgenerateCourseResultsMap(StudentStudentPortalBean student) {		
		PortalDao pDao = (PortalDao)act.getBean("portalDAO");
		PassFailBean passFailBean = null;
		Boolean readFromCache = Boolean.FALSE;
		try {
			readFromCache = this.fetchRedisHelper().readFromCache();
			if(readFromCache) {
				passFailBean = this.fetchPassFailforMobile(student.getSapid(),student.getSubject());
			}
		} catch(Exception e) {
//			e.printStackTrace();
			logger.error("HomeRESTController : mgenerateCourseResultsMap : exception : " + e.getMessage());
			
			//if REDIS stopped - exception catched - page loading continued -Vilpesh on 2021-11-24
			readFromCache = Boolean.FALSE;
		}
		if(!readFromCache) {
			passFailBean = pDao.getPassFailStatus(student.getSapid(),student.getSubject() );
		}
		List<PassFailBean> resultList = new  ArrayList<PassFailBean>();
		if(null != passFailBean) {
			resultList.add(passFailBean);
		}
		return resultList;
	}
	
	protected PassFailBean fetchPassFailforMobile(String sapId, String subject) {
		// NOTE: Results fetched from REDIS to display. Added by Vilpesh on 2021-11-24
		PassFailBean passFailBean = null;
		List<PassFailBean> listPassFailBean = null;
		Map<String, Object> destinationMap = null;
		
		destinationMap = this.fetchRedisHelper().fetchOnlyPassfail(ResultsFromRedisHelper.EXAM_STAGE_TEE, sapId);
		
		
		
		
		if(null != destinationMap) {
			if(null != destinationMap.get(ResultsFromRedisHelper.KEY_PASSFAIL_STATUS)) {
				listPassFailBean = (List<PassFailBean>) destinationMap.get(ResultsFromRedisHelper.KEY_PASSFAIL_STATUS);
			}
		}
		
		//listPassFailBean
		passFailBean = matchSubjectInPassFail(listPassFailBean, subject);
		
		destinationMap = null;
		listPassFailBean = null;
		return passFailBean;
	}
	
	protected PassFailBean matchSubjectInPassFail(List<PassFailBean> listPassFailBean, String subject) {
		PassFailBean passFailBean = null;
		if (null != listPassFailBean && !listPassFailBean.isEmpty()) {
			logger.info("HomeRESTController : matchSubjectInPassFail : size : " + listPassFailBean.size());
			
			for (int x = 0; x < listPassFailBean.size(); x++) {
				PassFailBean iteratorPassFailBean = listPassFailBean.get(x);
				if (null != iteratorPassFailBean && subject.equals(iteratorPassFailBean.getSubject())) {
					passFailBean = iteratorPassFailBean;
					logger.info("HomeRESTController : matchSubjectInPassFail : subject : " + passFailBean.getSubject());		
					break;
				}
			}
		}
		return passFailBean;
	}
	
	private ArrayList<ContentStudentPortalBean>  mgenerateCourseLearningResourcesMap(StudentStudentPortalBean student) {
		PortalDao pDao = (PortalDao)act.getBean("portalDAO");
		StudentInfoCheckDAO studentDao = (StudentInfoCheckDAO)act.getBean("stuentInfoCheckDAO");
		StudentStudentPortalBean studentDeatil = studentDao.getstudentData(student.getSapid());
		String earlyAccess = checkEarlyAccess(student.getSapid());
		List<ContentStudentPortalBean> allContentListForSubject = new ArrayList<ContentStudentPortalBean>();
		List<ContentStudentPortalBean> lastCycleContentList = new ArrayList<ContentStudentPortalBean>();
		
		allContentListForSubject = pDao.getContentsForSubjectsForCurrentSession(student.getSubject(),studentDeatil.getConsumerProgramStructureId(),earlyAccess);
		lastCycleContentList = pDao.getRecordingForLastCycle(student.getSubject());
		
//		String earlyAccess = (String)request.getSession().getAttribute("earlyAccess");
		
		ArrayList<ContentStudentPortalBean> contentList = new ArrayList<ContentStudentPortalBean>();
		String programStructureForStudent = student.getPrgmStructApplicable();
		
		if (allContentListForSubject.size() > 0) {
			for (ContentStudentPortalBean contentBean : allContentListForSubject) {
				String programStructureForContent = contentBean.getProgramStructure();
				
				if ("113".equals(studentDeatil.getConsumerProgramStructureId()) && "Business Economics".equalsIgnoreCase(student.getSubject())) {
					contentList.add(contentBean);
				}else if ("127".equalsIgnoreCase(studentDeatil.getConsumerProgramStructureId())
						|| "128".equalsIgnoreCase(studentDeatil.getConsumerProgramStructureId())) {
					if (programStructureForContent.equals(programStructureForStudent)) {
						contentList.add(contentBean);
					}
				}else {
					if(programStructureForContent == null || "".equals(programStructureForContent.trim()) || "All".equals(programStructureForContent)){
						contentList.add(contentBean);
					}else if(programStructureForContent.equals(programStructureForStudent)){
						contentList.add(contentBean);
					}
				}
			}
		}

//		contentList.addAll(lastCycleContentList);
		//Show only Course presentation and Course Material for next batch stuents
		
		if(earlyAccess != null && "Yes".equals(earlyAccess)){
		//Commented as now all content will be applicable for early access student
		/*
			ArrayList<ContentBean> prospectStudentContentList = new ArrayList<ContentBean>();
			for (ContentBean contentBean : contentList) {
				String contentType = contentBean.getContentType();
				if("Course Presentation".equalsIgnoreCase(contentType) || "Course Material".equalsIgnoreCase(contentType)){
					prospectStudentContentList.add(contentBean);
				}
			}

			contentList = prospectStudentContentList;
		*/
			//For next batch students, current recordings will be considered as last cycle recordings
			lastCycleContentList = pDao.getRecordingForCurrentCycle(student.getSubject());
		}
//		request.getSession().setAttribute("lastCycleContentList", lastCycleContentList);
		//modelnView.addObject("lastCycleContentList", lastCycleContentList);
//		request.getSession().setAttribute("contentList", contentList);
		//modelnView.addObject("contentList", contentList);
		return contentList;
	}
	
	private List<ContentStudentPortalBean>  mgenerateCourseLearningResourcesMapLastCycle(StudentStudentPortalBean student) {
		PortalDao pDao = (PortalDao)act.getBean("portalDAO");
		StudentInfoCheckDAO studentDao = (StudentInfoCheckDAO)act.getBean("stuentInfoCheckDAO");
		StudentStudentPortalBean studentDeatil = studentDao.getstudentData(student.getSapid());
		List<ContentStudentPortalBean> allContentListForSubject =  new ArrayList<ContentStudentPortalBean>();
		List<ContentStudentPortalBean> lastCycleContentList =  new ArrayList<ContentStudentPortalBean>();
		String earlyAccess = checkEarlyAccess(student.getSapid());
		
		allContentListForSubject = pDao.getContentsForSubjectsForCurrentSession(student.getSubject(),studentDeatil.getConsumerProgramStructureId(), earlyAccess);	
		lastCycleContentList = pDao.getRecordingForLastCycle(student.getSubject());

		//				String earlyAccess = (String)request.getSession().getAttribute("earlyAccess");
		//String earlyAccess = "No";

		ArrayList<ContentStudentPortalBean> contentList = new ArrayList<ContentStudentPortalBean>();
		String programStructureForStudent = student.getPrgmStructApplicable();
		if (allContentListForSubject.size() > 0) {
			for (ContentStudentPortalBean contentBean : allContentListForSubject) {
				String programStructureForContent = contentBean.getProgramStructure();

				if ("113".equals(studentDeatil.getConsumerProgramStructureId()) && "Business Economics".equalsIgnoreCase(student.getSubject())) {
					contentList.add(contentBean);
				}else if ("127".equalsIgnoreCase(studentDeatil.getConsumerProgramStructureId())
						|| "128".equalsIgnoreCase(studentDeatil.getConsumerProgramStructureId())) {
					if (programStructureForContent.equals(programStructureForStudent)) {
						contentList.add(contentBean);
					}
				}else {
					if(programStructureForContent == null || "".equals(programStructureForContent.trim()) || "All".equals(programStructureForContent)){
						contentList.add(contentBean);
					}else if(programStructureForContent.equals(programStructureForStudent)){
						contentList.add(contentBean);
					}
				}
			}
		}

		//				contentList.addAll(lastCycleContentList);
		//Show only Course presentation and Course Material for next batch stuents
		if(earlyAccess != null && "Yes".equals(earlyAccess)){
			ArrayList<ContentStudentPortalBean> prospectStudentContentList = new ArrayList<ContentStudentPortalBean>();
			for (ContentStudentPortalBean contentBean : contentList) {
				String contentType = contentBean.getContentType();
				if("Course Presentation".equalsIgnoreCase(contentType) || "Course Material".equalsIgnoreCase(contentType)){
					prospectStudentContentList.add(contentBean);
				}
			}

			contentList = prospectStudentContentList;
			//For next batch students, current recordings will be considered as last cycle recordings
			lastCycleContentList = pDao.getRecordingForCurrentCycle(student.getSubject());
		}
		//				request.getSession().setAttribute("lastCycleContentList", lastCycleContentList);
		//modelnView.addObject("lastCycleContentList", lastCycleContentList);
		//				request.getSession().setAttribute("contentList", contentList);
		//modelnView.addObject("contentList", contentList);
		return lastCycleContentList;
	}
	
	private List<SessionQueryAnswerStudentPortal> mgetCourseQueriesMap(StudentStudentPortalBean student) {
		PortalDao pDao = (PortalDao)act.getBean("portalDAO");
		System.out.print(student.getSubject() +  student.getSapid());
		List<SessionQueryAnswerStudentPortal> myQueries = pDao.getQueriesForSessionByStudent(student.getSubject(), student.getSapid());	
		List<SessionQueryAnswerStudentPortal> myCourseQueries = pDao.getQueriesForCourseByStudent(student.getSubject(), student.getSapid());
		myQueries.addAll(myCourseQueries);
		return myQueries;
	}
	
	private List<ForumStudentPortalBean> mgetForumBasedOnSubjects(StudentStudentPortalBean student){
		ForumDAO fDao = (ForumDAO)act.getBean("forumDAO");
		List<ForumStudentPortalBean> listOfForumsRelatedToSubject = fDao.getForumThreadsForSubject(student.getSubject());
		HashMap<Long,String> mapOfMainThreadIdAndReplyCount = new HashMap<Long,String>();
		for(ForumStudentPortalBean bean:listOfForumsRelatedToSubject){

			ArrayList<ForumStudentPortalBean> repliesOfMainThread = fDao.getThreadRepliesOfMainThread(bean.getId()+"");
		
			mapOfMainThreadIdAndReplyCount.put(bean.getId(),String.valueOf(repliesOfMainThread.size()));
		}
		//			request.getSession().setAttribute("mapOfForumThreadAndReplyCount",mapOfMainThreadIdAndReplyCount);
		return listOfForumsRelatedToSubject;

	}
	
	private  List<AssignmentStudentPortalFileBean> mgenerateCourseAssignmentsMap(StudentStudentPortalBean student) {
		PortalDao pDao = (PortalDao)act.getBean("portalDAO");
		ArrayList<StudentMarksBean> allStudentRegistrations = pDao.getAllRegistrationsFromSAPID(student.getSapid());
		HashMap<String, StudentMarksBean> monthYearAndStudentRegistrationMap = new HashMap<>();
		List<ExamOrderStudentPortalBean> liveFlagList = pDao.getLiveFlagDetails();
		for (StudentMarksBean bean : allStudentRegistrations) {
			monthYearAndStudentRegistrationMap.put(bean.getMonth() + "-" + bean.getYear(), bean);
		}
		StudentMarksBean studentRegistrationForAcademicSession = getStudentRegistrationForForSpecificLiveSettings(monthYearAndStudentRegistrationMap, liveFlagList, "acadSessionLive");
		StudentMarksBean studentRegistrationForAssignment = getAssignmentRegistrationForSpecificLiveSettings(monthYearAndStudentRegistrationMap, student.getConsumerProgramStructureId(), "Regular");
		//StudentMarksBean studentRegistrationForAssignment = getStudentRegistrationForForSpecificLiveSettings(monthYearAndStudentRegistrationMap, liveFlagList, "assignmentLive");
		String liveTypeForCourses = "acadContentLive";
		//			if("Yes".equalsIgnoreCase((String)request.getSession().getAttribute("earlyAccess"))){
		liveTypeForCourses = "acadContentLiveNextBatch";
		//			}
		StudentMarksBean studentRegistrationForCourses = getStudentRegistrationForForSpecificLiveSettings(monthYearAndStudentRegistrationMap, liveFlagList, liveTypeForCourses);

		// Commented old logic code which fetch students assignments using exam.assignments table and not from temp table
//		List<AssignmentStudentPortalFileBean> allAssignmentFilesLists =  mgetAssignments(student, pDao, studentRegistrationForAssignment); 
		List<AssignmentStudentPortalFileBean> allAssignmentFilesList =  assignmentService.mgetAssignmentsForStudents(student.getSapid(),student.getConsumerProgramStructureId()); 
		List<AssignmentStudentPortalFileBean> courseAssignmentsMap = new ArrayList<AssignmentStudentPortalFileBean>();

		if(allAssignmentFilesList != null){

			for (AssignmentStudentPortalFileBean assignment : allAssignmentFilesList) {
				if(student.getSubject().equals(assignment.getSubject())){
					courseAssignmentsMap.add(assignment);
				}
			}

			return courseAssignmentsMap;
		}

		return courseAssignmentsMap;

	}
	
	private List<AssignmentStudentPortalFileBean> mgetAssignmentSubmissionHistoryBySubject(String sapid,String subject) {
		AssignmentsDAO dao = (AssignmentsDAO)act.getBean("asignmentsDAO");
		return dao.getAllSubmittedAsignmentsBySubject(sapid,subject);
	}

	public boolean checkSession(HttpServletRequest request, HttpServletResponse respnse){
		String userId = (String)request.getSession().getAttribute("userId");
		if(userId != null){
			return true;
		}else{
			setError(request,"Session Expired, Please login again.");
			return false;
		}
		
		/*return true;*/

	}
	
	private FlagBean apiCallToGetFlagValueByKey(String key) {

		 
		CloseableHttpClient client = HttpClientBuilder.create().build();
		String url = SERVER_PATH +   "timeline/api/flag/getByKey";
		//url = "https://uat-studentzone-ngasce.nmims.edu/timeline/api/flag/getByKey";

		
		//List<TestBean> testsForStudent = new ArrayList<>();
		FlagBean flagBean = new FlagBean();
		flagBean.setKey(key);
		try {
			RestTemplate restTemplate = new RestTemplate();
			
			FlagBean response = restTemplate.postForObject(url,flagBean, FlagBean.class);
			return response;
		}catch(Exception e) {
//			e.printStackTrace();
		}
		finally{
			     //Important: Close the connect
				 try {
					client.close();
				} catch (IOException e) {
//					e.printStackTrace();
				}
			 }
			return flagBean;
	}
	

	
	private ArrayList<SessionDayTimeStudentPortal> mgetScheduledSessionList(StudentStudentPortalBean student) {
		HttpHeaders headers = new HttpHeaders();
		headers.add("Content-Type","application/json");
		PortalDao pDao = (PortalDao)act.getBean("portalDAO");
		List<ExamOrderStudentPortalBean> liveFlagList = pDao.getLiveFlagDetails();

		ArrayList<StudentMarksBean> allStudentRegistrations = pDao.getAllRegistrationsFromSAPID(student.getSapid());
		HashMap<String, StudentMarksBean> monthYearAndStudentRegistrationMap = new HashMap<>();
		for (StudentMarksBean bean : allStudentRegistrations) {
			monthYearAndStudentRegistrationMap.put(bean.getMonth() + "-" + bean.getYear(), bean);
		}
		StudentMarksBean studentRegistrationForAcademicSession = getStudentRegistrationForForSpecificLiveSettings(monthYearAndStudentRegistrationMap, liveFlagList, "acadSessionLive");
		StudentMarksBean studentRegistrationForAssignment = getStudentRegistrationForForSpecificLiveSettings(monthYearAndStudentRegistrationMap, liveFlagList, "assignmentLive");
		String liveTypeForCourses = "acadContentLive";
		//			if("Yes".equalsIgnoreCase((String)request.getSession().getAttribute("earlyAccess"))){
		liveTypeForCourses = "acadContentLiveNextBatch";
		//				}
		StudentMarksBean studentRegistrationForCourses = getStudentRegistrationForForSpecificLiveSettings(monthYearAndStudentRegistrationMap, liveFlagList, liveTypeForCourses);
		StudentMarksBean studentRegistrationData = getStudentRegistrationForForSpecificLiveSettings(monthYearAndStudentRegistrationMap, liveFlagList, liveTypeForCourses);
		ArrayList<SessionDayTimeStudentPortal> scheduledSessionList = new ArrayList<>();
	

		
		if(studentRegistrationData == null) {
			return scheduledSessionList;
		}
		//ArrayList<String> subjects = getSubjectsForStudent(student);
		ArrayList<String> subjects  = studentCourseService.getCurrentCycleSubjects(student.getSapid(),studentRegistrationForCourses.getYear(),studentRegistrationForCourses.getMonth());
		scheduledSessionList = pDao.getScheduledSessionForStudents(subjects,student);
	
		return scheduledSessionList;

	}
	
	private List<StudentMarksBean> mgetResults(StudentStudentPortalBean student) {
		List<StudentMarksBean> studentMarksList = new ArrayList<StudentMarksBean>();
		String mostRecentResultPeriod = "";
		String declareDate = "";
		SimpleDateFormat sdfr = new SimpleDateFormat("dd-MMM-yyyy");
		List<StudentMarksBean> studentMarksBeanList = new ArrayList<>();

		double onlineLiveOrder = 0.0;
		double offlineLiveOrder = 0.0;
		String offlineResultDeclareDateString = null;
		String onlineResultDeclareDateString = null; 
		String mostRecentOnlineResultPeriod = "";
		String mostRecentOfflineResultPeriod = "";
		PortalDao dao = (PortalDao)act.getBean("portalDAO");
		List<ExamOrderStudentPortalBean> liveFlagList = dao.getLiveFlagDetails();
		for (ExamOrderStudentPortalBean bean : liveFlagList) {
			double currentOrder = Double.parseDouble(bean.getOrder());
			if("Y".equalsIgnoreCase(bean.getLive()) && currentOrder > onlineLiveOrder){
				onlineLiveOrder = currentOrder;
				mostRecentOnlineResultPeriod = bean.getMonth() + "-" + bean.getYear();
				onlineResultDeclareDateString = bean.getDeclareDate();
			}

			if("Y".equalsIgnoreCase(bean.getOflineResultslive()) && currentOrder > offlineLiveOrder){
				offlineLiveOrder = currentOrder;
				mostRecentOfflineResultPeriod = bean.getMonth() + "-" + bean.getYear();
				offlineResultDeclareDateString = bean.getOflineResultsDeclareDate();
			}
		}


		if("Online".equals(student.getExamMode())){
			//mostRecentResultPeriod = dao.getMostRecentResultPeriod();
			//declareDate = dao.getRecentExamDeclarationDate();
			mostRecentResultPeriod = mostRecentOnlineResultPeriod;
			try {
				declareDate = sdfr.format(onlineResultDeclareDateString);
			} catch (Exception e) {
				declareDate = "";
			} 
			studentMarksList =  dao.getAStudentsMostRecentMarks(student.getSapid());
		}else{
			//mostRecentResultPeriod = dao.getMostRecentOfflineResultPeriod();
			//declareDate = dao.getRecentOfflineExamDeclarationDate();
			mostRecentResultPeriod = mostRecentOfflineResultPeriod;
			try {
				declareDate = sdfr.format(offlineResultDeclareDateString);
			} catch (Exception e) {
				declareDate = "";
			} 
			studentMarksList =  dao.getAStudentsMostRecentOfflineMarks(student.getSapid());
		}

		if(studentMarksList != null){
			for (StudentMarksBean studentMarksBean : studentMarksList) {
				int assignmentScore = 0;
				int writtenScore = 0;

				try {
					assignmentScore = Integer.parseInt(studentMarksBean.getAssignmentscore());
				} catch (Exception e) {}

				try {
					writtenScore = Integer.parseInt(studentMarksBean.getWritenscore());
				} catch (Exception e) {}

				int total = assignmentScore + writtenScore;
				studentMarksBean.setTotal(total+"");
			}
		}

		//request.getSession().setAttribute("mostRecentResultPeriod_studentportal", mostRecentResultPeriod);
		//request.getSession().setAttribute("declareDate_studentportal", declareDate);
		//request.getSession().setAttribute("studentMarksList_studentportal", studentMarksList);
		return studentMarksBeanList;

	}
	
	private StudentMarksBean getAssignmentRegistrationForSpecificLiveSettings(HashMap<String, StudentMarksBean> monthYearAndStudentRegistrationMap,String consumerProgramStructureId,String liveType) {
		AssignmentsDAO dao = (AssignmentsDAO)act.getBean("asignmentsDAO");
		AssignmentLiveSettingStudentPortal assignmentLiveSetting = dao.getCurrentLiveAssignment(consumerProgramStructureId, liveType);
		if(assignmentLiveSetting != null) {
			String key = assignmentLiveSetting.getAcadsMonth() + "-" + assignmentLiveSetting.getAcadsYear();
			
			return monthYearAndStudentRegistrationMap.get(key);
		}
		return null;
	}
	
	private List<AssignmentStudentPortalFileBean> mgetAssignments(StudentStudentPortalBean student, PortalDao pDao, StudentMarksBean studentRegistrationData) {

		AssignmentsDAO dao = (AssignmentsDAO)act.getBean("asignmentsDAO");
		String sapId = student.getSapid();
		boolean isOnline = isOnline(student);

		ArrayList<String> currentSemSubjects = new ArrayList<>();
		ArrayList<String> failSubjects = new ArrayList<>();
		ArrayList<String> applicableSubjects = new ArrayList<>();
		ArrayList<String> ANSSubjects = new ArrayList<>();
		List<AssignmentStudentPortalFileBean> failSubjectsAssignmentFilesList = new ArrayList<>();
		List<AssignmentStudentPortalFileBean> currentSemAssignmentFilesList = new ArrayList<>();

		HashMap<String, String> subjectSemMap = new HashMap<>();
		int currentSemSubmissionCount = 0;
		int failSubjectSubmissionCount = 0;


		//StudentBean studentRegistrationData = dao.getStudentRegistrationDataForAssignment(sapId);
		String currentSem = null;

		int maxAttempts = Integer.parseInt(ASSIGNMENT_SUBMISSIONS_ATTEMPTS);


		currentSem = student.getSem();
		if(studentRegistrationData != null){
			student.setSem(studentRegistrationData.getSem());
			student.setProgram(studentRegistrationData.getProgram());
			currentSemSubjects = getSubjectsForStudent(student, subjectSemMap);
			//currentSemSubjects = studentCourseService.getCurrentCycleSubjects(student.getSapid(),studentRegistrationData.getYear(),studentRegistrationData.getMonth());
			currentSemSubjects.remove("Project"); //Project not applicable for Assignments submission
			currentSemSubjects.remove("Module 4 - Project"); //Project not applicable for Assignments submission
			
		}

		failSubjects = new ArrayList<>();
		//if((currentSem != null && (!"1".equals(currentSem))) || studentRegistrationData == null){
		//If current semester is 1, then there cannot be any failed subjects

		ArrayList<AssignmentStudentPortalFileBean> failSubjectsBeans = getFailSubjects(student, pDao);
		if(failSubjectsBeans != null && failSubjectsBeans.size() > 0){

			for (int i = 0; i < failSubjectsBeans.size(); i++) {
				String subject = failSubjectsBeans.get(i).getSubject();
				String sem = failSubjectsBeans.get(i).getSem();
				failSubjects.add(failSubjectsBeans.get(i).getSubject());
				subjectSemMap.put(subject, sem);
				if("ANS".equalsIgnoreCase(failSubjectsBeans.get(i).getAssignmentscore())){
					ANSSubjects.add(subject);
				}
			}
		}

		//}

		ArrayList<AssignmentStudentPortalFileBean> failANSSubjectsBeans = getANSNotProcessed(student,pDao);
		if(failANSSubjectsBeans != null && failANSSubjectsBeans.size() > 0){

			for (int i = 0; i < failANSSubjectsBeans.size(); i++) {
				String subject = failANSSubjectsBeans.get(i).getSubject();
				String sem = failANSSubjectsBeans.get(i).getSem();
				failSubjects.add(failANSSubjectsBeans.get(i).getSubject());
				subjectSemMap.put(subject, sem);
				ANSSubjects.add(subject);
			}
		}
		failSubjects.remove("Project");
		failSubjects.remove("Module 4 - Project"); //Project not applicable for Assignments submission
		
		//Check if result is live for last submission cycle
		boolean isResultLiveForLastSubmissionCycle = dao.isResultLiveForLastAssignmentSubmissionCycle();
		
		ArrayList<String> subjectsNotAllowedToSubmit = new ArrayList<String>();
		if(!isResultLiveForLastSubmissionCycle){
			/*Commented so that current sem subjects should not show in ANS/Results Awaited/Failed Subjects Table and also should not display as Result awaited in dashboard
			 * currentSemResultAwaitedSubjectsList = dao.getResultAwaitedAssignmentSubmittedSubjectsList(student.getSapid());
				for (AssignmentFileBean assignmentFileBean : currentSemResultAwaitedSubjectsList){
					String subject = assignmentFileBean.getSubject();
					if(!failSubjects.contains(subject) && !currentSemSubjects.contains(subject)){
						failSubjects.add(subject);
					}
				}*/

			ArrayList<String> subjectsSubmittedInLastCycle = dao.getFailedSubjectsSubmittedInLastCycle(sapId, failSubjects);
			ArrayList<String> subjectsExamBookedInLastCycle = dao.getFailedSubjectsExamBookedInLastCycle(sapId, failSubjects);


			// Uncommented so that current sem subjects should not show in ANS/Results Awaited/Failed Subjects Table and also should not display as Result awaited in dashboard 
			for(String subject: currentSemSubjects){
				if(subjectsSubmittedInLastCycle.contains(subject)){
					subjectsSubmittedInLastCycle.remove(subject);
				}
			}

			for(String subject: currentSemSubjects){
				if(subjectsExamBookedInLastCycle.contains(subject)){
					subjectsExamBookedInLastCycle.remove(subject);
				}
			}

			for (String subject : subjectsSubmittedInLastCycle) {
				ANSSubjects.remove(subject);
			}

		
			ArrayList<String> subjectsExamBookedInLastCycleANS = new ArrayList<String>();
			for(String subject:subjectsExamBookedInLastCycle ){
				if(ANSSubjects.contains(subject)){
					subjectsExamBookedInLastCycleANS.add(subject);
				}
			}


		
			if(subjectsSubmittedInLastCycle.size() > 0 || subjectsExamBookedInLastCycle.size() > 0){
				//There are failed subjects submitted in last submission cycle 

				//If result is not live then subjects submitted in last cycle cannot be submitted till results are live
				subjectsNotAllowedToSubmit.addAll(subjectsSubmittedInLastCycle);
				subjectsNotAllowedToSubmit.addAll(subjectsExamBookedInLastCycle);
			}

			for(String subject : subjectsExamBookedInLastCycleANS){
				if(subjectsNotAllowedToSubmit.contains(subject)){
					subjectsNotAllowedToSubmit.remove(subject);
				}
			}

		

		}

		for (String failedSubject : failSubjects) {
			//For ANS cases, where result is not declared, failed subject will also be present in Current sem subject.
			//Give preference to it as Failed, so that assignment can be submitted and remove  from Current list
			if(currentSemSubjects.contains(failedSubject)){
				currentSemSubjects.remove(failedSubject);
			}
		}


		currentSemSubjects.remove("Project");
		currentSemSubjects.remove("Module 4 - Project");
		applicableSubjects.addAll(currentSemSubjects);
		applicableSubjects.addAll(failSubjects);
		applicableSubjects.remove("Project");
		applicableSubjects.remove("Module 4 - Project");
		




		//			request.getSession().setAttribute("subjectsForStudent", applicableSubjects);
		List<AssignmentStudentPortalFileBean> allAssignmentFilesList = new ArrayList<>();
		if(!isOnline){
			allAssignmentFilesList = dao.getAssignmentsForSubjects(applicableSubjects, student);
		}else{
			List<AssignmentStudentPortalFileBean> currentSemFiles = dao.getAssignmentsForSubjects(currentSemSubjects, student);
			List<AssignmentStudentPortalFileBean> failSubjectFiles = dao.getResitAssignmentsForSubjects(failSubjects, student);

			if(currentSemFiles != null){
				allAssignmentFilesList.addAll(currentSemFiles);
			}

			if(failSubjectFiles != null){
				allAssignmentFilesList.addAll(failSubjectFiles);
			}
		}

		if(allAssignmentFilesList != null ){

			HashMap<String,AssignmentStudentPortalFileBean> subjectSubmissionMap = new HashMap<>();
			/*Commented by Steffi to allow offline students to submit assignments in APR/SEP
			 * if(!isOnline){
					subjectSubmissionMap = dao.getSubmissionStatus(applicableSubjects, student.getSapid());//Assignments from Jun, Dec cycle
				}else{*/
			//For online, resit i.e. fail subjects paper change after resit date is over.//Applicable cycle is all 4 i.e. Apr, Jun, Sep, Dec
			HashMap<String,AssignmentStudentPortalFileBean>  currentSemSubjectSubmissionMap = dao.getSubmissionStatus(currentSemSubjects, sapId);
			HashMap<String,AssignmentStudentPortalFileBean>  failSubjectSubmissionMap = dao.getResitSubmissionStatus(failSubjects, sapId, student);

			if(currentSemSubjectSubmissionMap != null){
				subjectSubmissionMap.putAll(currentSemSubjectSubmissionMap);
			}

			if(failSubjectSubmissionMap != null){
				subjectSubmissionMap.putAll(failSubjectSubmissionMap);
			}
			//}

			for(AssignmentStudentPortalFileBean assignment : allAssignmentFilesList){
				String subject = assignment.getSubject();
				String status = "Not Submitted";
				String attempts = "0";
				String lastModifiedDate = "";
				String previewPath = "";
				String pastCycleAssignmentDetails = "";

				AssignmentStudentPortalFileBean studentSubmissionStatus = subjectSubmissionMap.get(subject);
				if(studentSubmissionStatus != null){
					status = studentSubmissionStatus.getStatus();
					attempts = studentSubmissionStatus.getAttempts();
					lastModifiedDate = studentSubmissionStatus.getLastModifiedDate();
					lastModifiedDate = lastModifiedDate.replaceAll("T", " ");
					lastModifiedDate = lastModifiedDate.substring(0,19);
					previewPath = studentSubmissionStatus.getPreviewPath();

				}

				assignment.setStatus(status);
				assignment.setAttempts(attempts);
				assignment.setAttemptsLeft((maxAttempts - Integer.parseInt(attempts))+"");
				assignment.setSem(subjectSemMap.get(subject));
				assignment.setLastModifiedDate(lastModifiedDate);
				assignment.setPreviewPath(previewPath);

				if(failSubjects.contains(subject)){
				
					failSubjectsAssignmentFilesList.add(assignment);
					if("Submitted".equals(status)){
						failSubjectSubmissionCount++;
					}
				}else{
					currentSemAssignmentFilesList.add(assignment);
					if("Submitted".equals(status)){
						currentSemSubmissionCount++;
					}
				}
				if (ANSSubjects.contains(subject) ){
					// ANS cases will always be allowed to Submit
					assignment.setSubmissionAllowed(true);
					subjectsNotAllowedToSubmit.remove(assignment.getSubject());
				} else if (subjectsNotAllowedToSubmit.contains(subject)) {
				
					assignment.setSubmissionAllowed(false);
				} else {
					
					assignment.setSubmissionAllowed(true);
				}
			}

		}


		return allAssignmentFilesList;


	}
	
    ArrayList<ContentStudentPortalBean> mgenerateCourseLearningResourcesMapNewForLR(StudentStudentPortalBean student, String programSemSubjectId) {

    	PortalDao pDao = (PortalDao)act.getBean("portalDAO");

    	LearningResourcesDAO dao = (LearningResourcesDAO) act.getBean("learningResourcesDAO");
    	StudentInfoCheckDAO studentDao = (StudentInfoCheckDAO)act.getBean("stuentInfoCheckDAO");
    	List<ContentStudentPortalBean> contentList = new ArrayList<ContentStudentPortalBean>();
    	try{
    		StudentStudentPortalBean studentreg = pDao.getStudentsMostRecentRegistrationData(student.getSapid());
    		String earlyAccess = checkEarlyAccess(student.getSapid());

    		List<ExamOrderStudentPortalBean> liveFlagList = pDao.getLiveFlagDetails();
    		HashMap<String,BigDecimal> examOrderMap = generateExamOrderMap(liveFlagList);
    		double examOrderDifference = 0.0;

    		
    		double acadContentLiveOrder = getMaxOrderWhereContentLive(liveFlagList);
    		double reg_order =  examOrderMap.get(studentreg.getMonth()+studentreg.getYear()).doubleValue();
    		
    		
    		String acadDateFormat = getCorrectOrderAccordTo2AcadContentLive(acadContentLiveOrder,reg_order,studentreg.getMonth(),studentreg.getYear(),true);
    		
    		
    		contentList = pDao.getContentsForSubjectsForCurrentSessionNewLR(programSemSubjectId,earlyAccess,acadDateFormat);

    	}catch(Exception e){
//    		e.printStackTrace();
    	}
    	return (ArrayList<ContentStudentPortalBean>) contentList;

    }
    
    public String getCorrectOrderAccordTo2AcadContentLive(double acadContentLiveOrder, double reg_order,String reg_month,String reg_year,boolean isCurrent)
    {
    	
    	
		StringBuffer acadDateFormat = new StringBuffer();
			
		if(isCurrent) {
				/*========================  FOR CURRENT CONTENT MONTH AND YEAR  ========================  */
		if(reg_order == acadContentLiveOrder)
			acadDateFormat.append(ContentUtil.pepareAcadDateFormat(reg_month,reg_year));
		else
			acadDateFormat.append(ContentUtil.pepareAcadDateFormat(CURRENT_ACAD_MONTH,CURRENT_ACAD_YEAR));
		
				/*========================  END ========================  */
		}else {
			
			/*========================  FOR LAST CONTENT MONTH AND YEAR  ========================  */
			
			if(reg_order == acadContentLiveOrder)
				acadDateFormat.append(ContentUtil.findLastAcadDate(reg_year,reg_month));
			else
				acadDateFormat.append(ContentUtil.findLastAcadDate(CURRENT_ACAD_YEAR,CURRENT_ACAD_MONTH)); 
			
			
			
			/*========================  END ========================  */
		}
		return acadDateFormat.toString();
    }
	
	public void setError(HttpServletRequest request, String errorMessage){
		request.setAttribute("error", "true");
		request.setAttribute("errorMessage", errorMessage);
	}

	
	@RequestMapping(value = "/setBookmark", method = {RequestMethod.POST}, consumes="application/json")
	@ResponseBody
	public void setBookmark(HttpServletRequest request,@RequestBody ContentStudentPortalBean contentBean) {
		PortalDao dao = (PortalDao)act.getBean("portalDAO");
		
		String userId = (String) request.getSession().getAttribute("userId");
		
		if(StringUtil.isBlank(userId))
			userId = contentBean.getSapId();
		
		dao.setBookmark(contentBean,userId);
	}
	
	/**
	 * modified done in courseWithPssId mapping and commented the old one
	 * @param student
	 * @return
	 */
	@PostMapping(path = "/CoursesWithPSSId" , consumes= "application/json", produces = "application/json")
	public ResponseEntity<HashMap<String,String>> mCoursesWithPSSId_updated(@RequestBody StudentStudentPortalBean student){ 
	
		StudentCourseMappingBean studentCourse = new StudentCourseMappingBean();
		try {
			
		PortalDao pDao = (PortalDao)act.getBean("portalDAO");
		ArrayList<StudentMarksBean> allStudentRegistrations = pDao.getAllRegistrationsFromSAPID(student.getSapid());
		HashMap<String, StudentMarksBean> monthYearAndStudentRegistrationMap = new HashMap<>();
		for (StudentMarksBean bean : allStudentRegistrations) {
			monthYearAndStudentRegistrationMap.put(bean.getMonth() + "-" + bean.getYear(), bean);
		}

		StudentStudentPortalBean studentDetail = pDao.getSingleStudentsData(student.getSapid());

		List<ExamOrderStudentPortalBean> liveFlagList = pDao.getLiveFlagDetails();

		
		StudentStudentPortalBean studentRereg = pDao.getStudentsMostRecentRegistrationData(student.getSapid());
		
		studentService.mgetWaivedInSubjects(studentDetail);		// add waived in subjects
		studentService.mgetWaivedOffSubjects(studentDetail);	// add waived off subjects
		
		HashMap<String,BigDecimal> examOrderMap = generateExamOrderMap(liveFlagList);
    	double acadContentLiveOrder = getMaxOrderWhereContentLive(liveFlagList);
    	double reg_order =  examOrderMap.get(studentRereg.getMonth()+studentRereg.getYear()).doubleValue();
    	double current_order =  examOrderMap.get(CURRENT_ACAD_MONTH+CURRENT_ACAD_YEAR).doubleValue();
		StudentMarksBean studentRegistrationData = registrationHelper.CheckStudentRegistrationForCourses(monthYearAndStudentRegistrationMap,acadContentLiveOrder,current_order,reg_order,liveFlagList);
	
		studentCourse = studentCourseService.getcourses(studentDetail, studentRegistrationData, current_order, acadContentLiveOrder, reg_order);
		
		}catch(Exception e) {
			courses_logger.info("Error in getting courses service For Sapid "+student.getSapid()+" :- ",e);
		}
		return  new ResponseEntity<HashMap<String,String>>(studentCourse.getListOfApplicableSUbjectssmap(), HttpStatus.OK);
	}
	
	/**
	 * The entered userId is verified and an email consisting of the user's password is sent to the user on their registered email address
	 * @param userId - userId/studentNo entered by the user
	 * @return a success/error message to display on the front end along with the response status 
	 */
	@GetMapping(value = "/forgotPassword")
	public ResponseEntity<String> forgotPassword(@RequestParam String userId) {
		try{
			String userEmail = homeService.forgotPasswordVerifyDetails(userId);
			
			logger.info("Password sent to user: " + userId + ", on their registered Email Address: " + userEmail);
			return new ResponseEntity<>("Password is emailed to your registered Email Address: " + userEmail, HttpStatus.OK);
		}
		catch(RuntimeException ex) {
			//Runtime Exception thrown for Exceptions caught in the Service and DAO layer of forgotPassword Mapping
			logger.error("Custom Runtime Exception caught for user: " + userId + ", Response sent: " + ex.getMessage());
			return new ResponseEntity<>(ex.getMessage(), HttpStatus.BAD_REQUEST);
		}
		catch(Exception ex) {
			logger.error("Unable to send forgot password email to user: " + userId + ", due to " + ex.toString());
			return new ResponseEntity<>("Please try again or contact ngasce@nmims.edu to Reset your Password!", HttpStatus.BAD_REQUEST);
		}
	}

	
	@PostMapping(value="/checklouConfirmed")
	public ResponseEntity<HashMap<String,String>> checklouConfirmed(@RequestBody HashMap<String,String> body){
		HashMap<String,String> map=new HashMap<String,String>();
		try {
			logger.info("checking louConfirmed status in database for sapid:"+body.get("sapid"));
			boolean result=homeService.checkLOUConfirmed(body.get("sapid"));
			map.put("louConfirmed", String.valueOf(result));
			return new ResponseEntity<>(map,HttpStatus.OK);
		}catch(Exception e) {
			//e.printStackTrace();
			logger.error("error while checking louConfirmed status in database for sapid:"+body.get("sapid"));
			return new ResponseEntity<>(map,HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}
	
	@PostMapping("/savelouConfirmed")
	public ResponseEntity<HashMap<String,String>> savelouConfirmed(@RequestBody HashMap<String,String> body ) {
		HashMap<String,String> map=new HashMap<String,String>();
		try {
			logger.info("saving louConfirmed status in database for sapid:"+body.get("sapid"));
			homeService.savelouConfirmed(body.get("sapid"));
			map.put("louConfirmedStatus", "Updated");
			return new ResponseEntity<>(map,HttpStatus.OK);
		}catch(Exception e) {
			logger.info("error while saving louConfirmed status in database for sapid:"+body.get("sapid"));
			return new ResponseEntity<>(map,HttpStatus.INTERNAL_SERVER_ERROR);
		}
		
	}
	
	/**
	 * overloaded method of courseWithPssId mapping, differentiate current and passed subjects
	 * @param student sapid
	 * @return  StudentCourseMappingBean
	 */
	@PostMapping(path = "/CoursesWithPSSIdV2" , consumes= "application/json", produces = "application/json")
	public ResponseEntity<StudentCourseMappingBean> mCoursesWithPSSIdV2(@RequestBody StudentStudentPortalBean student){ 
	
		StudentCourseMappingBean studentCourse = new StudentCourseMappingBean();
		try {
			
		PortalDao pDao = (PortalDao)act.getBean("portalDAO");
		ArrayList<StudentMarksBean> allStudentRegistrations = pDao.getAllRegistrationsFromSAPID(student.getSapid());
		HashMap<String, StudentMarksBean> monthYearAndStudentRegistrationMap = new HashMap<>();
		for (StudentMarksBean bean : allStudentRegistrations) {
			monthYearAndStudentRegistrationMap.put(bean.getMonth() + "-" + bean.getYear(), bean);
		}

		StudentStudentPortalBean studentDetail = pDao.getSingleStudentsData(student.getSapid());

		List<ExamOrderStudentPortalBean> liveFlagList = pDao.getLiveFlagDetails();

		
		StudentStudentPortalBean studentRereg = pDao.getStudentsMostRecentRegistrationData(student.getSapid());
		
		studentService.mgetWaivedInSubjects(studentDetail);		// add waived in subjects
		studentService.mgetWaivedOffSubjects(studentDetail);	// add waived off subjects
		
		HashMap<String,BigDecimal> examOrderMap = generateExamOrderMap(liveFlagList);
    	double acadContentLiveOrder = getMaxOrderWhereContentLive(liveFlagList);
    	double reg_order =  examOrderMap.get(studentRereg.getMonth()+studentRereg.getYear()).doubleValue();
    	double current_order =  examOrderMap.get(CURRENT_ACAD_MONTH+CURRENT_ACAD_YEAR).doubleValue();
		StudentMarksBean studentRegistrationData = registrationHelper.CheckStudentRegistrationForCourses(monthYearAndStudentRegistrationMap,acadContentLiveOrder,current_order,reg_order,liveFlagList);
	
		studentCourse = studentCourseService.getcourses(studentDetail, studentRegistrationData, current_order, acadContentLiveOrder, reg_order);
		
		}catch(Exception e) {
			
			courses_logger.info("Error in getting courses service For Sapid "+student.getSapid()+" :- ",e);
		}
		return  new ResponseEntity<StudentCourseMappingBean>(studentCourse, HttpStatus.OK);
	}

}
