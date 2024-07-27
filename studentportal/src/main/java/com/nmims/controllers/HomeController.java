

package com.nmims.controllers;

import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.net.URLEncoder;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.Principal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.Month;
import java.time.Period;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Objects;
import java.util.TreeMap;
import java.util.stream.Collectors;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.lang.StringUtils;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.poi.ss.usermodel.DateUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.ldap.NameNotFoundException;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.web.servlet.view.RedirectView;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.nmims.beans.AcadCycleFeedback;
import com.nmims.beans.AnnouncementStudentPortalBean;
import com.nmims.beans.AssignmentLiveSettingStudentPortal;
import com.nmims.beans.AssignmentStudentPortalFileBean;
import com.nmims.beans.CaptchaResponse;
import com.nmims.beans.ContentStudentPortalBean;
import com.nmims.beans.Event;
import com.nmims.beans.ExamBookingTransactionStudentPortalBean;
import com.nmims.beans.ExamOrderStudentPortalBean;
import com.nmims.beans.ExecutiveExamOrderStudentPortalBean;
import com.nmims.beans.FacultyStudentPortalBean;
import com.nmims.beans.FileStudentPortalBean;
import com.nmims.beans.FlagBean;
import com.nmims.beans.ForumResponseBean;
import com.nmims.beans.ForumStudentPortalBean;
import com.nmims.beans.MentionedDataBean;
import com.nmims.beans.MettlExamUpcomingBean;
import com.nmims.beans.ModuleContentStudentPortalBean;
import com.nmims.beans.Online_EventBean;
import com.nmims.beans.OpenBadgesUsersBean;
import com.nmims.beans.PageStudentPortal;
import com.nmims.beans.PassFailBean;
import com.nmims.beans.ProgramSubjectMappingStudentPortalBean;
import com.nmims.beans.ResponseStudentPortalBean;
import com.nmims.beans.ServiceRequestStudentPortal;
import com.nmims.beans.SessionAttendanceFeedbackStudentPortal;
import com.nmims.beans.SessionDayTimeStudentPortal;
import com.nmims.beans.SessionPlanPgBean;
import com.nmims.beans.SessionQueryAnswerStudentPortal;
import com.nmims.beans.StudentCourseMappingBean;
import com.nmims.beans.StudentMarksBean;
import com.nmims.beans.StudentRankBean;
import com.nmims.beans.StudentStudentPortalBean;
import com.nmims.beans.StudentsDataInRedisBean;
import com.nmims.beans.TestStudentPortalBean;
import com.nmims.beans.VideoContentStudentPortalBean;
import com.nmims.beans.programPreference;
import com.nmims.daos.AssignmentsDAO;
import com.nmims.daos.CareerServicesDAO;
import com.nmims.daos.ContentDAO;
import com.nmims.daos.FacultyDAO;
import com.nmims.daos.ForumDAO;
import com.nmims.daos.LDAPDao;
import com.nmims.daos.LearningResourcesDAO;
import com.nmims.daos.PortalDao;
import com.nmims.daos.ResultDAO;
import com.nmims.daos.ServiceRequestDao;
import com.nmims.daos.SessionQueryAnswerDAO;
import com.nmims.daos.StudentInfoCheckDAO;
import com.nmims.helpers.AESencrp;
import com.nmims.helpers.ChatBotEncryptionHelper;
import com.nmims.helpers.DateTimeHelper;
import com.nmims.helpers.ExcelHelper;
import com.nmims.helpers.MailSender;
import com.nmims.helpers.PaymentHelper;
import com.nmims.helpers.PersonStudentPortalBean;
import com.nmims.helpers.RegistrationHelper;
import com.nmims.helpers.ResultsFromRedisHelper;
import com.nmims.helpers.SalesforceHelper;
import com.nmims.helpers.XMLParser;
import com.nmims.interfaces.ContentServiceInterFace;
import com.nmims.interfaces.QueryAnswerInterface;
import com.nmims.interfaces.SessionPlanPGInterface;
import com.nmims.listeners.ServiceRequestPaymentScheduler;
import com.nmims.publisher.IdCardEventPublisher;
import com.nmims.repository.FlagsRepositoryForRedis;
import com.nmims.repository.ResultsRepositoryForRedis;
import com.nmims.services.AnnouncementService;
import com.nmims.services.FeedbackService;
import com.nmims.services.ForumService;
import com.nmims.services.HomeService;
import com.nmims.services.IdCardService;
import com.nmims.services.LeaderBoardService;
import com.nmims.services.LiveSessionAccessService;
import com.nmims.services.LoginLogService;
import com.nmims.services.OpenBadgesService;
import com.nmims.services.ServiceRequestService;
import com.nmims.services.StudentCourseMappingService;
import com.nmims.services.StudentService;
import com.nmims.services.StudentSettingService;
import com.nmims.services.SupportService;
import com.nmims.util.ContentUtil;
import com.nmims.util.ExamOrderUtil;
import com.nmims.views.StudentWaivedinExcelReportView;

/**
 * Handles requests for the application home page. 
 */
@Controller
@CrossOrigin(origins="*", allowedHeaders="*")
@EnableAsync
public class HomeController extends BaseController{

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

//	@Autowired  
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
	StudentService studentService;
	
	@Autowired
	LiveSessionAccessService liveSessionAccessService;
	
	@Autowired
	ServiceRequestPaymentScheduler serviceRequestPaymentScheduler;
	
	@Autowired
	private HomeService homeService;
	
	@Autowired
	FlagsRepositoryForRedis flagsRepository;
	
	@Autowired
	ResultsRepositoryForRedis resultsRepository;
	
	@Autowired
	LoginLogService loginLogService;
	
	@Autowired
	LeaderBoardService leaderBoardService;
	
	@Autowired
	QueryAnswerInterface queryAnswerService;
	
	@Autowired
	IdCardEventPublisher eventPublisher;
	
	@Autowired
	IdCardService idCardService;

	@Autowired
	ForumService forumService;
	
	@Autowired
	SessionPlanPGInterface sessionPlanPGService;
	
	@Autowired
	StudentSettingService studentSettingService;
	
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
	
	@Value("${WEB_RECAPTCHA_SECRET_KEY}")
	private String WEB_RECAPTCHA_SECRET_KEY;
	
	@Value("${TIMEBOUND_PORTAL_LIST}")
	private List<String> TIMEBOUND_PORTAL_LIST;
	
	
	


	private static final Logger logger = LoggerFactory.getLogger(HomeController.class);
	
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

	private ArrayList<String> CourseraSubscriptionList = new ArrayList<String>(Arrays.asList("77122898638", "77121164128", "77122952276", "77122125981", "77221674304", "77122181068", "77121718616", "77121180541", "77122107721", "77121280796", "77122328311", "77221676773", "77122211623", "77121999632", "77221271845", "77221808460", "77121914206", "77121593432", "77122223769", "77121622847", "77122998534", "77121533985", "77221179917", "77121791988", "77122592539", "77122775784", "77221412207", "77121166637", "77121878611", "77121353869", "77121596247", "77221623726", "77121169405", "77221197687", "77121360321", "77121279251", "77121393574", "77121918532", "77121288277", "77121943274", "77221498448", "77121138157", "77221725756", "77122846967", "77122917428", "77221382709", "77121724807", "77221184173", "77122127961", "77121798385", "77121594482", "77122677803", "77221821553", "77122710382", "77221745258", "77121440540", "77221886869", "77122410284", "77121707942", "77121650079", "77122219915", "77122955904", "77121614703", "77122589591", "77121501135", "77121282082", "77121817826", "77121277720", "77121964267", "77121379188", "77122582306", "77121907096", "77121482694", "77121591060", "77121967591", "77121654205", "77122355962", "77121657663", "77121835234", "77221638165", "77121640955", "77221315097", "77121124125", "77221376428", "77121932260", "77121339567", "77122795773", "77122481611", "77122334257", "77121986076", "77121559963", "77121120313", "77121162775", "77122720177", "77221636192", "77121752711", "77221201204", "77122620227", "77121654383", "77121550953", "77221844182", "77221156481", "77221197619", "77121755888", "77122440140", "77122768312", "77221925109", "77221629973", "77221437801", "77121299957", "77122142355", "77122290359", "77122182863", "77121431371", "77121200466", "77121470658", "77221815345", "77122269296", "77121582395", "77121553541", "77122705538", "77122791067", "77122258873", "77122664313", "77122549857", "77122682680", "77221401012", "77122779142", "77121190847", "77121724527", "77122551708", "77122274801", "77122115137", "77121437019", "77221809668", "77121109925", "77122405088", "77221800987", "77221681545", "77221856425", "77121166711", "77122795473", "77122971418", "77121121877", "77122372263", "77122227321", "77121647406", "77122303428", "77121131240", "77122233070", "77121496635", "77122526934", "77777777132"));
			
	private final int pageSize = 20;
	
	private int downloaded = 0;

	@Value( "${ENVIRONMENT}" )
	private String ENVIRONMENT;
	
	//Attribute names added in HTTP Session for Results display - Added by Vilpesh on 2021-11-23
	public static final String S_MOST_RECENT_RESULT_PERIOD = "mostRecentResultPeriod_studentportal";
	public static final String S_DECLAREDATE_RR = "declareDate_RR_studentportal";
	public static final String S_MARKS_LIST = "studentMarksList_studentportal";
	public static final String S_MARKS_LIST_SIZE = "studentMarksListSize_studentportal";
	public static final String S_MARKS_HISTORY_RR = "studentMarksHistory_RR_studentportal";
	public static final String S_PASSFAIL_RR = "passFailStatus_RR_studentportal";
	
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
	public static final String programNotIncluded = "Bachelor Programs";//For SRB in support view
	
	
	@Autowired
	ContentServiceInterFace contentService;
	
	@Autowired
	StudentCourseMappingService studentCourseService;
	
	@Autowired
	AnnouncementService announcementService;
	
	@Autowired
	SupportService supportService;
	
	private static final Logger courses_logger = LoggerFactory.getLogger("studentCourses");
	
	private static final Logger sessionPlanPG_logger = LoggerFactory.getLogger("sessionPlanPG");
	
	public HomeController(){
	}
	
	/**
	 * Refresh Cache function to refresh cache
	 * @param 
	 * none
	 * @return 
	 * none
	 * */
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
	
	public String calculateAge(StudentStudentPortalBean student){

		SimpleDateFormat[] dateList = {new SimpleDateFormat("yyyy-MM-dd"), new SimpleDateFormat("dd.MM.yyyy"), new SimpleDateFormat("yyyyMMdd")};
		Calendar now = Calendar.getInstance();
		Calendar dob = Calendar.getInstance();
		Date date = null;
		int nowYear = 0, dobYear=0, age = 0, nowMonth = 0, dobMonth = 0, nowDay = 0, dobDay = 0; 
		
		for(SimpleDateFormat dateFormat: dateList) {
			try {
				try {
					date = dateFormat.parse(student.getDob());
				}catch (Exception e) {
					date = DateUtil.getJavaDate(Double.parseDouble( student.getDob()));
				}
				
				dob.setTime(date);
				if (dob.after(now)) {
					student.setErrorRecord(true);
					student.setErrorMessage("Inavalid age");
				}
				nowYear = now.get(Calendar.YEAR);
				dobYear = dob.get(Calendar.YEAR);
				age = nowYear - dobYear;
				nowMonth = now.get(Calendar.MONTH);
				dobMonth = dob.get(Calendar.MONTH);
				if (dobMonth > nowMonth) {
					age--;
				} else if (nowMonth == dobMonth) {
					nowDay = now.get(Calendar.DAY_OF_MONTH);
					dobDay = dob.get(Calendar.DAY_OF_MONTH);
					if (dobDay > nowDay) {
						age--;
					}
				}student.setDob(new SimpleDateFormat("yyyy-MM-dd").format(date));
				break;
			}catch (Exception e) {
				student.setErrorRecord(true);
				student.setErrorMessage("Invalid Date Format"); 
			}
		}

		return Integer.toString(age);
	}

	


	/**
	 * Simply selects the home view to render by returning its name.
	 */
	@RequestMapping(value = "/", method = RequestMethod.GET)
	public String homePage(HttpServletRequest request, HttpServletResponse response, Model m) {
		request.getSession().invalidate();
		request.setAttribute("SERVER_PATH", SERVER_PATH);

		PortalDao pDao = (PortalDao)act.getBean("portalDAO");
		if("PROD".equalsIgnoreCase(ENVIRONMENT)) {
			industryList = pDao.getIndustryList();
		}
		String sessionExpired = request.getParameter("sessionExpired");
		String feedbackSaved = request.getParameter("feedbackSaved");
		if("true".equals(sessionExpired)){
			setError(request, "Session Expired, Please login again.");
		}
		
		if("true".equals(feedbackSaved)){
			setSuccess(request, "Feedback Saved successfuly. Please login to continue!");
		}

//		return "login";
		return "redirect:" + SERVER_PATH + "logout";
	}

	
//Dummy Page Commented Not Required By Shiv.G

/*	
	@RequestMapping(value = "/studentHome", method = RequestMethod.GET)
	public String studentHome(HttpServletRequest request, HttpServletResponse response, Model m) {
		return "studentHome";
	} 
*/	private void chatbotEncryptedPayload(String username,HttpServletRequest request)  {
	try {
		ChatBotEncryptionHelper chatBotEncryptionHelper = new ChatBotEncryptionHelper();
		SimpleDateFormat sdf = new SimpleDateFormat("YYYY-mm-dd hh:mm:ss");
		String currentTimeStamp =  sdf.format(new Date());
		HashMap<String, String> toJsonMap = new HashMap<>();
		toJsonMap.put("sap_id", username);
		toJsonMap.put("timestamp", currentTimeStamp);
		String jsonSapIdWithStamp = new Gson().toJson(toJsonMap);
		request.getSession().setAttribute("payload",jsonSapIdWithStamp); 
		request.getSession().setAttribute("encJson",URLEncoder.encode(chatBotEncryptionHelper.encrypt(jsonSapIdWithStamp), "UTF-8"));
	} catch (Exception e) {
		e.printStackTrace();	
	}
}
	
	@RequestMapping(value = "/authenticate", method = {RequestMethod.GET, RequestMethod.POST})
	public ModelAndView home(HttpServletRequest request, HttpServletResponse respnse,RedirectAttributes ra,Principal principal, @CookieValue("username") String username) throws Exception {
		
		PortalDao pDao = (PortalDao)act.getBean("portalDAO");
		ModelAndView modelnView = null;
	//Object principal2 = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
//		String username;
//		if (principal2 instanceof UserDetails) {
//username= ((UserDetails)principal2).getUsername();
//		} else {
//username = principal2.toString();
//		}
		
			
		request.getSession().setAttribute("userId", username);
		
				

		String userId =username;
		chatbotEncryptedPayload(username,request);

		Date date = new Date() ;
		SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm") ;
		dateFormat.format(date);

		//3 Intermidiate Checks Order
		//1. Tee Quick Join
		//2. Session Quick Join
		//3. Results View
		
		
		//1. check if student has exam in the live slot.
		
		MettlExamUpcomingBean mettlExamUpcomingBean = null;  
		try {
			mettlExamUpcomingBean = pDao.getMettlUpcomingQuickJoinDB(userId);
		}catch(Exception e) {
			//e.printStackTrace();
		}
		
		//end

		LDAPDao dao = (LDAPDao)act.getBean("ldapdao");
		String password = dao.getUserPassword(userId);

		request.getSession().setAttribute("password", password);


		//2. Check If Results Live
		
	/*	boolean showResultsFromCache = checkCanShowResultsFromCache();
		StudentsDataInRedisBean response = new StudentsDataInRedisBean();
		try {
			response = getStudentDataFromRedisCache(userId);
		}catch(Exception e) {
			
		} */
				
					
		

	//	request.getSession().setAttribute("password",password);
		if(password == null || userId == null || "".equals(password.trim()) || "".equals(userId.trim())){
			modelnView = new ModelAndView("jsp/login");
			request.setAttribute("error", "true");
			request.setAttribute("errorMessage", "Please enter ID and Password.");
			return modelnView;
		}
		
		
		//Validating the Google-ReCaptcha
		/*if(isValidCaptcha(request.getParameter("g-recaptcha-response"))) {
			request.getSession().setAttribute("captchaMessage", "Captcha Saved Successfully!");
		}
		else {
			modelnView = new ModelAndView("jsp/login");
			request.setAttribute("error", "true");
			request.setAttribute("errorMessage", "Captcha Failed. Please try again!");
			return modelnView;
		}
		*/
		boolean authenticated = false;
		request.getSession().setAttribute("isLoginAsLead","false");

		try {
			
			authenticated = dao.login(userId, password);
			
		} catch (Exception e) {
			authenticated = true;
			
			
			//e.printStackTrace();
			//modelnView = new ModelAndView("jsp/login");
			//request.setAttribute("error", "true");
			//request.setAttribute("errorMessage", "Invalid Login");
			//return modelnView;
		}
		//authenticated = true;
		request.setAttribute("SERVER_PATH", SERVER_PATH);
		//added as a check for demo lead on 01-02-2020
		if(authenticated){
			
			// uncommented this section to re-enable quickjoin for TEE exams
						try {
							//MettlExamUpcomingBean mettlExamUpcomingBean = pDao.getMettlUpcomingQuickJoin(userId);
							if(mettlExamUpcomingBean.getAcessKey() != null) {
								request.getSession().setAttribute("userId",userId);

								modelnView = new ModelAndView("jsp/mettl/mettlQuickJoin");
								modelnView.addObject("mettlExamUpcomingBean", mettlExamUpcomingBean);
								modelnView.addObject("joinLink", generateMettlLink(mettlExamUpcomingBean));
								return modelnView;
							}
						}catch(Exception e) {
							//e.printStackTrace();
						}
			
			//Added for sessions start
			try {
				
				
				StudentStudentPortalBean student = pDao.getSingleStudentsData(userId);
				request.getSession().setAttribute("getSingleStudentsData_studentportal",student);
				//Redirect MBA-WX students to timeline App Start 

	            if(isTimeboundWiseByConsumerProgramStructureId(student.getConsumerProgramStructureId())) {
	                redirectMBAWXStudentToTimelineApp(respnse, student.getSapid());
	    			return null;
	            }
	            //Redirect MBA-WX students to timeline App End 
	             
	            
				//Added by Shiv G to avoid getStudentData DB Hits 
				ArrayList<Integer> currentSemPSSId = new ArrayList<Integer>();
				List<Integer> liveSessionPssIdList = new ArrayList<Integer>();
				StudentStudentPortalBean studentRegistrationForAcademicSession = new StudentStudentPortalBean();
				//boolean isNonPG_Program = Boolean.FALSE;
				boolean isFreeLiveSessionApplicable = Boolean.FALSE;
			
	            
				try {
					studentRegistrationForAcademicSession = registrationHelper.checkStudentRegistration(userId, student);
					request.getSession().setAttribute("programForHeaderPortal",studentRegistrationForAcademicSession.getProgram());
					request.getSession().setAttribute("studentRegistrationForAcademicSession_studentportal", studentRegistrationForAcademicSession);
					
					//Set up latest semester
					student.setSem(studentRegistrationForAcademicSession.getSem());
					//ArrayList<String> waivedOffSubjects = studentService.mgetWaivedOffSubjects(student);
				//	currentSemPSSId = pDao.getPSSIds(studentRegistrationForAcademicSession.getConsumerProgramStructureId(), studentRegistrationForAcademicSession.getSem() ,waivedOffSubjects);
					
					currentSemPSSId = studentCourseService.getPSSID(userId,studentRegistrationForAcademicSession.getYear(),studentRegistrationForAcademicSession.getMonth());
					request.getSession().setAttribute("currentSemPSSId_studentportal", currentSemPSSId);
					String enrollDate = ("01/" + student.getEnrollmentMonth() + "/" + student.getEnrollmentYear());
					//isNonPG_Program = nonPG_ProgramList.contains(studentRegistrationForAcademicSession.getProgram());
					ArrayList<String> listOfMaterKeyHavingFreeLiveSession = liveSessionAccessService.getListOfFreeLiveSessionAccessMasterKeys(TIMEBOUND_PORTAL_LIST); 
					isFreeLiveSessionApplicable = listOfMaterKeyHavingFreeLiveSession.contains(studentRegistrationForAcademicSession.getConsumerProgramStructureId());
					
					logger.info("(Enrollment Year/Month, LiveSessionAccessDate, Program, NonPG_Program) : ("
							+ enrollDate + "," + LIVE_SESSION_ACCESS_DATE + ","
							+ studentRegistrationForAcademicSession.getProgram() + "," + isFreeLiveSessionApplicable);
					if (DateTimeHelper.checkDate(DateTimeHelper.FORMAT_ddMMMyyyy, enrollDate,
							DateTimeHelper.FORMAT_ddMMMyyyy, LIVE_SESSION_ACCESS_DATE) || isFreeLiveSessionApplicable) {
						request.getSession().setAttribute("liveSessionPssIdAccess_studentportal", currentSemPSSId);
					} else {
						liveSessionPssIdList = liveSessionAccessService.fetchPSSforLiveSessionAccess(userId, studentRegistrationForAcademicSession.getYear(), studentRegistrationForAcademicSession.getMonth());
						request.getSession().setAttribute("liveSessionPssIdAccess_studentportal", liveSessionPssIdList);
					}
				} catch (Exception e) {
//					e.printStackTrace();
					request.getSession().setAttribute("programForHeaderPortal",student.getProgram());
					request.getSession().setAttribute("currentSemPSSId_studentportal", currentSemPSSId);
					request.getSession().setAttribute("liveSessionPssIdAccess_studentportal", liveSessionPssIdList);
				}
				
				//Added For UG Consent Form
				//Commented by Riya as it is inactive for temporary
				/*if(studentRegistrationForAcademicSession != null) {
					StudentStudentPortalBean studentreg = studentRegistrationForAcademicSession;
					System.out.println("studentreg "+studentreg);
					if(supportService.checkStudentUgOrNot(studentreg.getProgram(),studentreg.getSem(),studentreg.getMonth(),studentreg.getYear(),studentreg.getSapid())) {
						ModelAndView model = new ModelAndView("jsp/ugConsentForm");
						model.addObject("program",student.getProgram());
						model.addObject("course",supportService.getCourseName(student.getProgram()));
						return model;
					}
				}*/
				
				if((dateFormat.parse(dateFormat.format(date)).after(dateFormat.parse("06:00:00")) && 
					dateFormat.parse(dateFormat.format(date)).before(dateFormat.parse("22:00:00")) &&
					studentRegistrationForAcademicSession != null)){
					
					ArrayList<SessionDayTimeStudentPortal> sessionList = new ArrayList<SessionDayTimeStudentPortal>();
					
					//Get Common Sessions
					ArrayList<SessionDayTimeStudentPortal> commonSessionsList = pDao.getTodaysCommonSessionsByCPSId(studentRegistrationForAcademicSession);
					sessionList.addAll(commonSessionsList);
					
					ArrayList<Integer> liveSessionPssIdsList = (ArrayList<Integer>) request.getSession().getAttribute("liveSessionPssIdAccess_studentportal");
					if (liveSessionPssIdsList != null && liveSessionPssIdsList.size() > 0) {
						//Get Scheduled Sessions
						ArrayList<SessionDayTimeStudentPortal> scheduledSessionsList = pDao.getTodaysSessionsByPSSId(currentSemPSSId, studentRegistrationForAcademicSession);
						sessionList.addAll(scheduledSessionsList);
					}
					
					if (sessionList.size() > 0) {
						request.getSession().setAttribute("userId",userId);
						modelnView = new ModelAndView("jsp/sessionQuickJoin");
						modelnView.addObject("sessionList", sessionList);
						return modelnView;
					}
				}
			}catch(Exception e) {
//				e.printStackTrace();
			}
			//Added for sessions end

			//Temporarily Commented on 8-Sep-2017 for Performance Improvements
			//Changes for leads to login 
			
			//pDao.insertLoginDetails(userId,getClientIp(request),getClientOs(request),getClientBrowserDetails(request));
			
			
			
		/*	try {
				if(response.getResultsData() != null) { 
					request.getSession().setAttribute("userId",userId);
					modelnView = new ModelAndView("jsp/resutlsFormCachePage");
					modelnView.addObject("studentsDataInRedisBean", response);
					return modelnView;
				}
			}catch(Exception e) {
//				if((dateFormat.parse(dateFormat.format(date)).after(dateFormat.parse("09:55:00")) && dateFormat.parse(dateFormat.format(date)).before(dateFormat.parse("10:05:00"))) || (dateFormat.parse(dateFormat.format(date)).after(dateFormat.parse("16:55:00")) && dateFormat.parse(dateFormat.format(date)).before(dateFormat.parse("17:08:00")))) {
//						//e.printStackTrace();
//						//logout student
//						//return to login page with error of maintenance
//						modelnView = new ModelAndView("jsp/login");
//						request.setAttribute("error", "true");
//						request.setAttribute("errorMessage", "Server is currently Under Maintenance, Please login after 10 mins.");
//						return modelnView;
//				}
			} */
			
			//pDao.insertLoginDetails(userId,getClientIp(request),getClientOs(request),getClientBrowserDetails(request));
			return executePostAuthenticationActivities(request,respnse, userId, password,false);
		}else{
			modelnView = new ModelAndView("jsp/login");
			request.setAttribute("error", "true");
			request.setAttribute("errorMessage", "Invalid Credentials. Please re-try.");
		}
		return modelnView;
	}
	
	
	

	/*Added to skip feedback for registration period May 19 by Ps
	 * To be removed later.
	 * */

	@RequestMapping(value = "/skipFeedback", method = {RequestMethod.GET, RequestMethod.POST})
	public ModelAndView skipFeedback(HttpServletRequest request, HttpServletResponse response) {
//		try {
//			String userId = (String)request.getSession().getAttribute("userId");
//
//			return executePostAuthenticationActivities(request,response, userId,request.getParameter("password"),false);
//		} catch (Exception e) {
//			e.printStackTrace();
//			return new ModelAndView("jsp/login");
//		}
		
		try {
			if(!checkSession(request, response)){
				return new ModelAndView("jsp/login");
			}
			request.getSession().setAttribute("skipFeedback", "true");
			return goToHome(request, response);
		} catch (Exception e) {
//			e.printStackTrace();
			return new ModelAndView("jsp/login");
		}
		
	}
	
	// Used By Intermediate Lite Pages
	@RequestMapping(value = "/skipToHome", method = {RequestMethod.GET, RequestMethod.POST})
	public ModelAndView skipToHomeFeedback(HttpServletRequest request, HttpServletResponse response) {
		try {
			request.getSession().setAttribute("isLoginAsLead","false");
						String userId = (String)request.getSession().getAttribute("userId");
						String password =(String)request.getSession().getAttribute("password");						
						return executePostAuthenticationActivities(request,response, userId,password,false);						
		}
					catch (Exception e) {
//						e.printStackTrace();
			 			return new ModelAndView("jsp/login");
			 		}
		
	
	}
	
	@RequestMapping(value = "/home", method = {RequestMethod.GET, RequestMethod.POST})
	public ModelAndView goToHome(HttpServletRequest request, HttpServletResponse response) {
		if(!checkSession(request, response)){
			return new ModelAndView("jsp/login");
		}
		
		//If the students validity is expired, redirect them to the correct page.
		String validityExpired = (String)request.getSession().getAttribute("validityExpired");
		if(validityExpired != null && "Yes".equals(validityExpired)) {
			return new  ModelAndView("jsp/support/overview");
		}

		String userId = (String)request.getSession().getAttribute("userId");
		chatbotEncryptedPayload(userId,request);
		PortalDao pDao = (PortalDao)act.getBean("portalDAO");
		
		//List<AnnouncementBean> announcements = pDao.getAllActiveAnnouncements();
		/*List<AnnouncementBean> jobAnnouncements = pDao.getAllNewJobAnnouncements();
		if(jobAnnouncements != null && jobAnnouncements.size() > 0){
			announcements.addAll(jobAnnouncements);
		}*/
		//request.setAttribute("announcements", announcements);
		request.setAttribute("SERVER_PATH", SERVER_PATH);

		if(!(userId.startsWith("77") || userId.startsWith("79") || isEmail(userId))){
			return sendToPageBasedOnRole(request, userId);
		}

		StudentStudentPortalBean student = pDao.getSingleStudentsData(userId);
		String programStructure = student.getEnrollmentMonth() + student.getEnrollmentYear();
		//refresh the data from Exam App and 

		refreshSessionDataFromOtherApps(userId,request);
		ModelAndView modelAndView = new ModelAndView("jsp/home");
		ArrayList<String> currentSemSubjects = new ArrayList<>();
		HashMap<String, String> subjectSemMap = new HashMap<>();
		//currentSemSubjects = getSubjectsForStudent(student, subjectSemMap);
		currentSemSubjects = (ArrayList<String>) request.getSession().getAttribute("currentSemSubjects_studentportal");
		try {
			return executePostAuthenticationActivities(request,response, userId,request.getParameter("password"),false);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			
//			e.printStackTrace();
			return new ModelAndView("jsp/login");
		}

	}



	/*@RequestMapping(value = "/updatePrograms", method = {RequestMethod.GET, RequestMethod.POST})
	public String updatePrograms(HttpServletRequest request, HttpServletResponse response) {

		ExcelHelper helper = new ExcelHelper();
		LDAPDao dao = (LDAPDao)act.getBean("ldapdao");
		helper.readMarksExcel(dao);
		request.setAttribute("success","true");
		request.setAttribute("successMessage","Programs updated successfully.");
		return "home";
	}*/

	@RequestMapping(value = "/logout", method = {RequestMethod.GET, RequestMethod.POST})
	public @ResponseBody String logout(HttpServletRequest request, HttpServletResponse response) throws Exception{
		logger.info("Logging out");

		request.getSession().invalidate();
		request.setAttribute("SERVER_PATH", SERVER_PATH);

		return null;
	}
	
	@RequestMapping(value="/loginMobile",method={RequestMethod.POST},consumes ="application/json",produces = "application/json")
	public @ResponseBody String userDetails(@RequestBody StudentStudentPortalBean student,HttpServletRequest request,HttpServletResponse response){
		PortalDao pDao = (PortalDao)act.getBean("portalDAO");
		StudentStudentPortalBean students = (StudentStudentPortalBean)pDao.getSingleStudentsData(student.getUserId());
		StudentStudentPortalBean studentDetails = new StudentStudentPortalBean();

		studentDetails.setFirstName(students.getFirstName());
		studentDetails.setLastName(students.getLastName());
		studentDetails.setEmailId(students.getEmailId());
		studentDetails.setMobile(students.getMobile());
		studentDetails.setImageUrl(students.getImageUrl());

		Gson gson = new Gson();
		String json = gson.toJson(studentDetails);
		return json;
	}

	/*@RequestMapping(value = "/createUser", method = {RequestMethod.GET, RequestMethod.POST})
	public ModelAndView createUser(HttpServletRequest request, HttpServletResponse response) {
		logger.info("Logging out");
		request.getSession().invalidate();
		String userId = request.getParameter("userId");
		String value = request.getParameter("expires");
		LDAPDao dao = (LDAPDao)act.getBean("ldapdao");
		dao.updateAttributes("77114000100");
		ModelAndView modelnView = new ModelAndView("jsp/login");
		return modelnView;
	}*/

	/**
	 * Simply selects the home view to render by returning its name.
	 * @throws Exception 
	 */


	@Autowired
	private OpenBadgesService openBadgesService;

	private ModelAndView executePostAuthenticationActivities(HttpServletRequest request, HttpServletResponse respnse, String userId, String password,boolean isLoginAsForm ) throws Exception {
		PortalDao pDao = (PortalDao)act.getBean("portalDAO");
		LDAPDao dao = (LDAPDao)act.getBean("ldapdao");
		ModelAndView modelnView = new ModelAndView("jsp/home");		
		
		// This header stops session from being created in non https context if secure flag is set  
		if("http://localhost:8080/".equals(SERVER_PATH)) {
			respnse.setHeader("Set-Cookie", "JSESSIONID=" + request.getSession().getId() + "; Path=/studentportal; HttpOnly; SameSite=none;");
			respnse.setHeader("Set-Cookie", "SESSION=" + request.getSession().getId() + "; Path=/studentportal/; HttpOnly; SameSite=none; Secure");
		} else {
			respnse.setHeader("Set-Cookie", "JSESSIONID=" + request.getSession().getId() + "; Path=/studentportal; HttpOnly; SameSite=none; Secure");
			respnse.setHeader("Set-Cookie", "SESSION=" + request.getSession().getId() + "; Path=/studentportal/; HttpOnly; SameSite=none; Secure");
		}		
		request.getSession().setAttribute("courseraAccess", CourseraSubscriptionList.contains(userId));
		request.getSession().setAttribute("validityExpired","No");//This parameter is kept false initially.It will get set to true only if the validity is expired.
		request.getSession().setAttribute("earlyAccess","No");
		StudentInfoCheckDAO studentDao = (StudentInfoCheckDAO)act.getBean("stuentInfoCheckDAO");
		boolean makeLive= false;
		ArrayList<SessionDayTimeStudentPortal> scheduledSessionList = new ArrayList<>();
	
//end
		request.getSession().setAttribute("userId", userId);
		request.getSession().setAttribute("password", password);
		request.setAttribute("SERVER_PATH", SERVER_PATH);
		Boolean logout = false;
		request.getSession().setAttribute("logout", logout);


		if(!(userId.startsWith("77") || userId.startsWith("79"))){
			return sendToPageBasedOnRole(request, userId);
		}


		//ServiceRequestDao sDao = (ServiceRequestDao)act.getBean("serviceRequestDao");
		
		
		
		//Added by Shiv G to remove redundant getSingleStudentsData DAO calls.
		StudentStudentPortalBean student = (StudentStudentPortalBean) request.getSession().getAttribute("getSingleStudentsData_studentportal");
						
		if(null == student) {
			student = pDao.getSingleStudentsData(userId);
			request.getSession().setAttribute("getSingleStudentsData_studentportal",student);
		}
		
		//StudentBean 
		PersonStudentPortalBean person = new PersonStudentPortalBean();
		try {
			person = dao.findPerson(userId);
			person.setUserId(userId);
		} catch (Exception e2) {
			// TODO Auto-generated catch block
			//e2.printStackTrace();
			//Added to bypass LDAP
			person.setAltContactNo(student.getAltPhone());
			person.setContactNo(student.getMobile());
			person.setDisplayName(student.getFirstName());
			person.setEmail(student.getEmailId());
			person.setFirstName(student.getFirstName());
			person.setLastName(student.getLastName());
			person.setPassword(password);
			person.setPostalAddress(student.getAddress());
			person.setSapId(student.getSapid());
			person.setUserId(userId);
			/**person.setErrorMessage(errorMessage);
			person.setErrorRecord(errorRecord);
			person.setRegistrationType(registrationType);
			person.setRoles(student.); 
			person.setOldPassword(oldPassword);
			person.setIdentityConfirmed(identityConfirmed);
			person.setLastLogon(lastLogon);*/
			
		}
		ArrayList<String> subjects = new ArrayList<>();
		String programStructure = student.getEnrollmentMonth() + student.getEnrollmentYear();
		
		//Code for redirect to ResutlsFormCachePage start
//		boolean showResultsFromCache = checkCanShowResultsFromCache();
//		
//		if(showResultsFromCache) {
//			request.getSession().setAttribute("student_studentportal", student);
//			ModelAndView mvResutlsFormCachePage = new ModelAndView("jsp/resutlsFormCachePage");
//			return mvResutlsFormCachePage;
//		 
//		}
		
		//Code for redirect to ResutlsFormCachePage end
		
		try {
			String semCheck = pDao.getStudentsMostRecentRegistrationData(student.getSapid()).getSem();
			request.getSession().setAttribute("currentSem", semCheck);
		} catch (Exception e1) { 
//			e1.printStackTrace();
		}

		//String programStructure = student.getEnrollmentMonth() + student.getEnrollmentYear();
		//ServiceRequestDao sDao = (ServiceRequestDao)act.getBean("serviceRequestDao");

		
		performCSStudentChecks(request, userId, student);
				
		getWaivedInSubjects(student, pDao, request);

		//student.setConsumerProgramStructureId(pDao.getConsumerProId(student.getProgram(), student.getPrgmStructApplicable(), student.getConsumerType()));

		//get student consumerId and store in session.
		/*request.getSession().setAttribute("consumerTypeId", );

		//List<AnnouncementBean> announcements = pDao.getAllActiveAnnouncements();
		/*			List<AnnouncementBean> jobAnnouncements = pDao.getAllNewJobAnnouncements();
		if(jobAnnouncements != null && jobAnnouncements.size() > 0){
			announcements.addAll(jobAnnouncements);
		}*/
		//Added for Sas--> 
		
		
		/*List<AnnouncementStudentPortalBean> announcements = new ArrayList<AnnouncementStudentPortalBean>();
		
		 announcements = pDao.getAllActiveAnnouncements(student.getProgram(),student.getConsumerProgramStructureId());
		 

		 //announcements = new ArrayList();*/
		//request.getSession().setAttribute("announcementsPortal", announcements);

		
		
		
		//ArrayList<String> currentSemSubjects = new ArrayList<>();
		//HashMap<String, String> subjectSemMap = new HashMap<>();
		//currentSemSubjects = getSubjectsForStudent(student, subjectSemMap);
		

		ArrayList<VideoContentStudentPortalBean> videoList = null;
		
		request.getSession().setAttribute("videoList", videoList);

		//boolean isCertificate = isStudentOfCertificate(student.getProgram());
		boolean isCertificate = student.isCertificateStudent();
		request.getSession().setAttribute("isCertificate", isCertificate);
		boolean isValid = isStudentValid(student, userId);
		
		//validity end date
		String validityEndDate = getValidityEndDate(student);
		request.getSession().setAttribute("validityEndDate", validityEndDate);
		
		//parent name variables created to redirect student to updateProfile page if found null/empty
		String motherName = (student.getMotherName() != null) ? student.getMotherName().trim() : "";
		String fatherName = (student.getFatherName() != null) ? student.getFatherName().trim() : "";

		// disable program terminated Student from login 
		if("Program Terminated".equalsIgnoreCase(student.getProgramStatus()))
		{
			request.setAttribute("error", "true");
			request.setAttribute("errorMessage", "Unable to access your Profile for further details call 1800 1025 136 (Mon-Sat) 9am-7pm");
			return new ModelAndView("jsp/login");
		}
        if(student.getProgramStatus()!=null && student.getProgramStatus().equalsIgnoreCase("Program Withdrawal")) {
        	String programForHeader =  (String) request.getSession().getAttribute("programForHeaderPortal");
			student.setProgramForHeader(programForHeader);
        	request.getSession().setAttribute("student_studentportal", student);
			request.getSession().setAttribute("user_studentportal", person);
			
			//check for student to update their profile information
			if(	("N".equalsIgnoreCase(student.getDetailsConfirmedByStudent()) ||
				 motherName.isEmpty() || motherName.equalsIgnoreCase("null") ||
				 fatherName.isEmpty() || fatherName.equalsIgnoreCase("null")
				) && isLoginAsForm == false) {
					
				try {
					ModelAndView upDateInfoModelView = new ModelAndView("jsp/confirmStudentDetails");
					upDateInfoModelView = updateInformation(request, student, upDateInfoModelView, false);
					return upDateInfoModelView;
				}
				catch (Exception ex) {
					logger.info("Error while redirecting student: " + student.getSapid() + " with programStatus: Program Withdrawal, to confirmStudentDetails page, due to " + ex.toString());
				}
			}
			
			modelnView.addObject("userId",request.getSession().getAttribute("userId"));
			setSessionDataIntoTheDTO(request);
			return new ModelAndView("jsp/support/connectWithUs");
        }
		if(!isValid){
			request.getSession().setAttribute("validityExpired","Yes");
			String programForHeader =  (String) request.getSession().getAttribute("programForHeaderPortal");
			student.setProgramForHeader(programForHeader);
			request.getSession().setAttribute("student_studentportal", student);
			request.getSession().setAttribute("user_studentportal", person);
			
			
			if(student != null) {
				checkIfWaivedOff(student, pDao, request);
			}
			
			//check for student to update their profile information
			if(	("N".equalsIgnoreCase(student.getDetailsConfirmedByStudent()) ||
				 motherName.isEmpty() || motherName.equalsIgnoreCase("null") ||
				 fatherName.isEmpty() || fatherName.equalsIgnoreCase("null")
				) && isLoginAsForm == false) {
				
				try {
					ModelAndView upDateInfoModelView = new ModelAndView("jsp/confirmStudentDetails");
					upDateInfoModelView = updateInformation(request, student, upDateInfoModelView, false);
					return upDateInfoModelView;
				}
				catch (Exception ex) {
					logger.info("Error while redirecting validityEnd student: " + student.getSapid() + " to confirmStudentDetails page, due to " + ex.toString());
				}
			}
			
			modelnView.addObject("userId",request.getSession().getAttribute("userId"));
			setSessionDataIntoTheDTO(request);
			return new ModelAndView("jsp/support/connectWithUs");
		}
		
		request.getSession().setAttribute("validityEndDate", validityEndDate);
		String programForHeader =  (String) request.getSession().getAttribute("programForHeaderPortal");
		student.setProgramForHeader(programForHeader);
		request.getSession().setAttribute("student_studentportal", student);
		request.getSession().setAttribute("user_studentportal", person);

		
		//HashMap<String,BigDecimal> examOrderMap = pDao.getExamOrderMap();
		List<ExamOrderStudentPortalBean> liveFlagList = pDao.getLiveFlagDetails();
		StudentStudentPortalBean studentreg = pDao.getStudentsMostRecentRegistrationData(student.getSapid());

		//Set Student's Recent Registration in Session
		request.getSession().setAttribute("studentRecentReg_studentportal",studentreg);

		
		if (!isTimeboundWiseByConsumerProgramStructureId(student.getConsumerProgramStructureId())) {
			
			HashMap<String,BigDecimal> examOrderMap = generateExamOrderMap(liveFlagList);
			double examOrderDifference = 0.0;
			double examOrderOfProspectiveBatch = examOrderMap.get(student.getEnrollmentMonth()+student.getEnrollmentYear()).doubleValue();
			double maxOrderWhereContentLive = getMaxOrderWhereContentLive(liveFlagList);
			examOrderDifference = examOrderOfProspectiveBatch - maxOrderWhereContentLive;
			
			if(examOrderDifference == 1){
				request.getSession().setAttribute("earlyAccess","Yes");
			}
			
			/* For 2 Acad content live */
			double reg_order = examOrderMap.get(studentreg.getMonth()+studentreg.getYear()).doubleValue();
			double current_order =examOrderMap.get(CURRENT_ACAD_MONTH+CURRENT_ACAD_YEAR).doubleValue();// added by sachin
		
			request.getSession().setAttribute("current_order",current_order);
			request.getSession().setAttribute("acadContentLiveOrder",maxOrderWhereContentLive);
			request.getSession().setAttribute("reg_order",reg_order);
			double getMaxOrderOfAcadSessionLive = ExamOrderUtil.getMaxOrderOfAcadSessionLive(liveFlagList);
			request.getSession().setAttribute("acadSessionLiveOrder",getMaxOrderOfAcadSessionLive);

			
			//Set student registration details to session for Session Video Home
			try {
				String year = CURRENT_ACAD_YEAR;
				String month = CURRENT_ACAD_MONTH;

				//Get max order of academic session live
				double acadSessionLiveOrder = ExamOrderUtil.getMaxOrderOfAcadSessionLive(liveFlagList);

				//If the acadSessionLiveOrder and reg_order is equals then set registered year and month as currentSessionCycle
				//otherwise set CURRENT_ACAD_YEAR and CURRENT_ACAD_MONTH as currentSessionCycle
				if(acadSessionLiveOrder == reg_order) {
					year = studentreg.getYear();
					month = studentreg.getMonth();
				}

				request.getSession().setAttribute("currentSem", studentreg.getSem());

				request.getSession().setAttribute("currentSessionCycle", (month+year));
			}catch (Exception e) {
				logger.info("HomeController : authenticate : Error : " + e.getMessage());
			}
			


		}
		
		if(student != null){
			/*
			 * Commented by Sanket on 20-Aug-2017 for performance improvement. Uncomment when needed.
			 * String pendingAmount = sDao.getPendingAmountFromSapId(student.getSapid());
			if(!"".equals(pendingAmount) && pendingAmount !=null){ 
				request.getSession().setAttribute("SECURE_SECRET",SECURE_SECRET);
				ModelAndView modelAndView = new ModelAndView("jsp/pendingPaymentForm");
				request.getSession().setAttribute("pendingAmount",pendingAmount);//Placing it in session since if we cancel payment page should get attributes//
				AdhocPaymentBean adhocPaymentBean = new AdhocPaymentBean();
				adhocPaymentBean.setAmount(pendingAmount);
				adhocPaymentBean.setDescription("Exam Registration Pending Fee");
				modelAndView.addObject("adhocPaymentBean", adhocPaymentBean);
				modelAndView.addObject("paymentType",paymentTypeList);
				return modelAndView;
			}*/

			/*Redirect MBA-WX students to timeline App Start */
			if(isTimeboundWiseByConsumerProgramStructureId(student.getConsumerProgramStructureId())) {
				redirectMBAWXStudentToTimelineApp(respnse, student.getSapid());
			}
			/*Redirect MBA-WX students to timeline App End */

			checkIfWaivedOff(student, pDao, request);

			List<ExecutiveExamOrderStudentPortalBean> ResultliveFlagList = new ArrayList<ExecutiveExamOrderStudentPortalBean>();

			if(student.getProgram().equalsIgnoreCase("EPBM") || student.getProgram().equalsIgnoreCase("MPDV")){
				ResultliveFlagList = pDao.getResultliveFlagList();
				HashMap<String,BigDecimal> executiveExamOrderMap = generateExecutiveExamOrderMap(ResultliveFlagList);

			}



			getStudentHomePageDetails(student, request, liveFlagList,ResultliveFlagList);
			
			//String IdentityConfirmed = person.getIdentityConfirmed();
			modelnView.addObject("displayName", person.getDisplayName() );
			request.getSession().setAttribute("user_studentportal", person);

			person.setEmail(student.getEmailId());
			person.setContactNo(student.getMobile());
			person.setProgram(student.getProgram());

			//Add Announcement by Riya (to get pssId)
			List<AnnouncementStudentPortalBean> announcements = new ArrayList<AnnouncementStudentPortalBean>();
			// announcements = pDao.getAllActiveAnnouncements(student.getProgram(),student.getConsumerProgramStructureId());
			 
			 HashMap<String,String> applicablePSSId =(HashMap<String,String>) request.getSession().getAttribute("programSemSubjectIdWithSubjects_studentportal");
			 
			 try {
				 // Commented as announcements coming dynamically
				 // announcements.addAll(announcementService.getActiveAnnouncementByRest(student.getConsumerProgramStructureId(),applicablePSSId));

			 }catch(Exception e)
			 {
				 logger.error("Error in Getting Active Announcements For parameters [program "+student.getProgram()+", masterKey "+student.getConsumerProgramStructureId()+", pssIds "+applicablePSSId.toString()+"] - ",e);
			 }
			 request.getSession().setAttribute("announcementsPortal", announcements);
			//String programStructure = student.getPrgmStructApplicable();

		

			//			if(isEmail(userId)) {
			//				
			//			}

			ArrayList<StudentMarksBean> allStudentRegistrations = pDao.getAllRegistrationsFromSAPID(student.getSapid());
			HashMap<String, StudentMarksBean> monthYearAndStudentRegistrationMap = new HashMap<>();
			for (StudentMarksBean bean : allStudentRegistrations) {
				monthYearAndStudentRegistrationMap.put(bean.getMonth() + "-" + bean.getYear(), bean);
			}
			StudentMarksBean studentRegistrationData = getStudentRegistrationForForSpecificLiveSettings(monthYearAndStudentRegistrationMap, liveFlagList, "acadSessionLive");

		/*	if(studentRegistrationData !=null){
				Online_EventBean onlineEvent = pDao.getLiveOnlineEvent(studentRegistrationData.getProgram(),studentRegistrationData.getSem(),student.getPrgmStructApplicable());
				boolean registeredForEvent = false;
				if(onlineEvent.getId() != null){
					registeredForEvent = pDao.getOnlineEventRegistration(student.getSapid(),onlineEvent.getId());
					if(registeredForEvent == false){
						return onlineEventRegistration(request, respnse,onlineEvent);
					}
				}
			}*/
		}
		
		

		
		
		//Commented	By Shiv 15/07/2020 for PG Online Exam For Performance
		//check if student updated the profile password start
		
		if(!"Y".equals(student.getChangedPassword()) && isLoginAsForm == false){
			modelnView = new ModelAndView("jsp/changeProfilePassword");
			modelnView.addObject("student", student );
			modelnView.addObject("passwordUpdate", "false");
			modelnView.addObject("hideProfileLink", "true");
			modelnView.addObject("hideProfileLink", "true");
			return modelnView;
		} 

		//Commented	By Shiv 15/07/2020 for PG Online Exam For Performance
		
		//check for student to update their profile information
		if(	("N".equalsIgnoreCase(student.getDetailsConfirmedByStudent()) ||
			 motherName.isEmpty() || motherName.equalsIgnoreCase("null") ||
			 fatherName.isEmpty() || fatherName.equalsIgnoreCase("null")
			) && isLoginAsForm == false) {
			
			try {
				ModelAndView upDateInfoModelView = new ModelAndView("jsp/confirmStudentDetails");
				upDateInfoModelView = updateInformation(request, student, upDateInfoModelView, false);
				return upDateInfoModelView;
			}
			catch (Exception ex) {
				logger.info("Error while redirecting student: " + student.getSapid() + " to confirmStudentDetails page, due to " + ex.toString());
			}
		}


//		request.getSession().setAttribute("ifReRegistrationActive", makeLive);
//		if(!"Y".equals(student.getUpdatedByStudent())){
//			modelnView = new ModelAndView("jsp/confirmParentName");
//			modelnView.addObject("student", student );
//			modelnView.addObject("hideProfileLink", "true");
//			return modelnView;
//		}
		//Temporarily Commented on 8-Sep-2017 for Performance Improvements

		loginLogService.insertLoginDetails( userId, request );
		
		/*Commented by Sanket: 20-Aug-2017
		 * if(!("Confirmed".equalsIgnoreCase(IdentityConfirmed))){//For first time login of new students
			modelnView = new ModelAndView("jsp/confirmIdentity");
			modelnView.addObject("displayName", person.getDisplayName() );
			modelnView.addObject("hideProfileLink", "true");
			return modelnView;
		}*/

		//Check if student has FAA subject.
		/*ArrayList<String> studentWithFAASubjectList = new ArrayList<>();
		studentWithFAASubjectList = pDao.getStudentApplicableForSubject("Financial Accounting & Analysis");
		if(studentWithFAASubjectList.contains(userId)){
			//Check if user has never responded to Event Registration. If not then show event registgration pagee
			boolean registeredForEvent = pDao.checkIfRegisteredForEvent(userId);
			if(!registeredForEvent){
				return onlineEventRegistration(request, respnse);
			}
		}*/
		//Check if student has Business Statistics subject.


		/*getstudentWithBusinessStatisticsSubjectList();
		if(getstudentWithBusinessStatisticsSubjectList().contains(userId)){
			//Check if user has never responded to Event Registration. If not then show event registgration pagee
			boolean registeredForEvent = pDao.checkIfRegisteredForEvent(userId);
			if(!registeredForEvent){
				return onlineEventRegistration(request, respnse);
			}
		}*/

//		try {
//			MettlExamUpcomingBean mettlExamUpcomingBean = pDao.getMettlUpcomingQuickJoinDB(student.getSapid());
//			if(mettlExamUpcomingBean.getAcessKey() != null) {
//				modelnView = new ModelAndView("jsp/mettl/mettlQuickJoin");
//				modelnView.addObject("student", student );
//				modelnView.addObject("mettlExamUpcomingBean", mettlExamUpcomingBean);
//				modelnView.addObject("joinLink", generateMettlLink(mettlExamUpcomingBean));
//				return modelnView;
//			}
//		}catch(Exception e) {
//			e.printStackTrace();
//		}

//		if(checkLead(request, respnse)) {
//		
//			String perspective = student.getPerspective();
//			if(perspective.equalsIgnoreCase("free")) { 
//				return new ModelAndView("redirect:/getFreeCoursesList"); 
//			}
//			return new ModelAndView("jsp/home");
//		}else {
		
			//return new ModelAndView("jsp/home");
		//Added by Riya Check ugc student for consent form
//			if(supportService.checkStudentUgOrNot(studentreg.getProgram(),studentreg.getSem(),studentreg.getMonth(),studentreg.getYear(),studentreg.getSapid())) {
//				ModelAndView model = new ModelAndView("jsp/ugConsentForm");
//				model.addObject("program",student.getProgram());
//				model.addObject("course",supportService.getCourseName(student.getProgram()));
//				return model;
//			}
			//Added By Somesh to skip feedback
		
			//Set the data from session into the DTO Object
			setSessionDataIntoTheDTO(request);
		
			String skipFeedback = (String) request.getSession().getAttribute("skipFeedback");
			if (!StringUtils.isBlank(skipFeedback) && skipFeedback.equalsIgnoreCase("true")) {
				return new ModelAndView("jsp/home");
			}else {
				return checkIfPendingfeedback(request, respnse);
			}
//		}
	}

	// returns true if master key of MBAWx or Msc Ai & Ml
	public boolean isTimeboundWiseByConsumerProgramStructureId(String consumerProgramStructureId) {
		if(TIMEBOUND_PORTAL_LIST.contains(consumerProgramStructureId)) {
			return true;
		}else {
			return false;
		}
	}

	public StudentsDataInRedisBean getStudentDataFromRedisCache(String sapid) {
		if(checkCanShowResultsFromCache()) {
			try {
				return resultsRepository.findBySapid(sapid);
			}catch(Exception e) {
//				e.printStackTrace();
				return new StudentsDataInRedisBean();
			}
		}
		return new StudentsDataInRedisBean();
	}

	public boolean checkCanShowResultsFromCache() {
		/* REDIS stopped- gives error here - commented by Vilpesh on 2021-11-18
		FlagBean flagBean = flagsRepository.getByKey("showResultsFromCache");
		if(flagBean != null) {
			if("Y".equalsIgnoreCase(flagBean.getValue())) {
				return true;
			}
		}
		return false;*/
		
		Boolean canShow;
		ResultsFromRedisHelper resultsFromRedisHelper = null;
		try {
			//if REDIS stopped - exception catched - page loading continued -Vilpesh on 2021-11-19
			resultsFromRedisHelper = (ResultsFromRedisHelper) act.getBean("resultsFromRedisHelper");
			canShow = resultsFromRedisHelper.displayResultsFromCache();
		} catch (Exception e) {
//			e.printStackTrace();
			logger.error("HomeController : checkCanShowResultsFromCache : "+ e.getMessage());
			canShow = Boolean.FALSE;;
		}
		logger.info("HomeController : checkCanShowResultsFromCache : canShow : "+ canShow);
		return canShow;
	}


	public boolean checkIfMovingResultsToCache() {
		/* REDIS stopped- gives error here - commented by Vilpesh on 2021-12-08
		FlagExamBean flagBean = flagsRepository.getByKey("movingResultsToCache");
		
		if(flagBean != null) {
			if("Y".equalsIgnoreCase(flagBean.getValue())) {
				return true;
			}
		}
		
		return false;*/
		
		Boolean isMoving;
		ResultsFromRedisHelper resultsFromRedisHelper = null;
		try {
			//if REDIS stopped - exception catched - page loading continued -Vilpesh on 2021-12-08
			resultsFromRedisHelper = (ResultsFromRedisHelper) act.getBean("resultsFromRedisHelper");
			isMoving = resultsFromRedisHelper.sendingResultsToCache();
		} catch (Exception e) {
//			e.printStackTrace();
			logger.error("HomeController : checkIfMovingResultsToCache : " + e.getMessage());
			isMoving = Boolean.FALSE;;
		}
		logger.info("HomeController : checkIfMovingResultsToCache : isMoving : " + isMoving);
		return isMoving;
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
					// TODO Auto-generated catch block
//					e.printStackTrace();
				}
			 }
			return flagBean;
	}
	
	private ArrayList<String> getWaivedInSubjects(StudentStudentPortalBean studentBean,PortalDao pDao,HttpServletRequest request) {
		ArrayList<String> subjects = studentService.mgetWaivedInSubjects(studentBean);
		request.getSession().setAttribute("waivedInSubjects", subjects);
		String programForHeader =  (String) request.getSession().getAttribute("programForHeaderPortal");
		studentBean.setProgramForHeader(programForHeader);
		request.getSession().setAttribute("student_studentportal", studentBean);
		return subjects;
	}
	
	private void checkIfWaivedOff(StudentStudentPortalBean student, PortalDao pDao, HttpServletRequest request) {
		ArrayList<String> waivedOffSubjects = studentService.mgetWaivedOffSubjects(student);
		request.getSession().setAttribute("student_studentportal", student);
		request.getSession().setAttribute("waivedOffSubjects", waivedOffSubjects);
	}


	/*  @Cacheable("studentWithBusinessStatisticsSubjectList")
	  public ArrayList<String> data() {
			PortalDao pDao = (PortalDao)act.getBean("portalDAO");
	    ArrayList<String> studentWithBusinessStatisticsSubjectList = new ArrayList<>();
		studentWithBusinessStatisticsSubjectList = pDao.getStudentApplicableForSubject("Business Statistics");

	    return studentWithBusinessStatisticsSubjectList;
	  }*/


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

	private HashMap<String, BigDecimal> generateExamOrderMap(List<ExamOrderStudentPortalBean> liveFlagList) {
		HashMap<String, BigDecimal> orderMap = new HashMap<String, BigDecimal>();
		for (ExamOrderStudentPortalBean row : liveFlagList) {
			orderMap.put(row.getMonth()+row.getYear(),BigDecimal.valueOf((long)Double.parseDouble( row.getOrder())));
			orderMap.put(row.getAcadMonth()+row.getYear(),BigDecimal.valueOf((long)Double.parseDouble( row.getOrder())));
		}
		return orderMap;
	}

	private HashMap<String, BigDecimal> generateExecutiveExamOrderMap(List<ExecutiveExamOrderStudentPortalBean> liveFlagList) {
		HashMap<String, BigDecimal> orderMap = new HashMap<String, BigDecimal>();
		for (ExecutiveExamOrderStudentPortalBean row : liveFlagList) {
			orderMap.put(row.getMonth()+row.getYear(),BigDecimal.valueOf((long)Double.parseDouble( row.getOrder())));
			orderMap.put(row.getAcadMonth()+row.getYear(),BigDecimal.valueOf((long)Double.parseDouble( row.getOrder())));
		}
		return orderMap;
	} 
	
	@RequestMapping(value = "/refreshStudentDetails", method = {RequestMethod.GET, RequestMethod.POST})
	public String refreshStudentDetailsInSSO(HttpServletRequest request, HttpServletResponse response) {
		resetStudentInSession(request, response);
		return null;
	} 

	@RequestMapping(value = "/loginAsForm", method = {RequestMethod.GET, RequestMethod.POST})
	public ModelAndView loginAsForm(HttpServletRequest request, HttpServletResponse respnse) throws Exception {
		request.getSession().invalidate();
		request.setAttribute("SERVER_PATH", SERVER_PATH);
		ModelAndView mv = new ModelAndView("jsp/loginAsForm");
		return mv;
	}

	@RequestMapping(value = "/loginAsNew", method = {RequestMethod.GET, RequestMethod.POST})
	public ModelAndView loginAsNew(HttpServletRequest request, HttpServletResponse respnse) throws Exception {
		
		String masterPassword = "ngasce@admin20";
		String userEnteredPassword = request.getParameter("password");
		String password = null;
		if(masterPassword.equals(userEnteredPassword)) {
			LDAPDao dao = (LDAPDao)act.getBean("ldapdao");
			password = dao.getUserPassword(request.getParameter("username"));
		}
		if(password == null) {
			ModelAndView mv = new ModelAndView("jsp/loginAsForm");
			mv.addObject("error", "true");
			mv.addObject("errorMessage", "Invalid username or password");
			return mv;
		}
		ModelAndView mv = new ModelAndView("jsp/loginAsFormAutoSubmit");
		mv.addObject("username", request.getParameter("username"));
		mv.addObject("password", password);
		return mv;
	}
	
	@RequestMapping(value = "/loginAs", method = {RequestMethod.GET, RequestMethod.POST})
	public ModelAndView loginAs(HttpServletRequest request, HttpServletResponse respnse, @CookieValue("username") String username) throws Exception {
		logger.info("HomeController:home", "");
		BaseController baseCon = new BaseController();
		ModelAndView modelnView = null;

		//  Principal principal = request.getUserPrincipal();
	      //   principal.getName();
	         
	         
	         
	         
	         
		String userId = username.trim();
		String password = "ngasce@admin20";
		request.getSession().setAttribute("userId", username);
		request.getSession().setAttribute("password", password);


		boolean isLoginAsForm = false;
		boolean authenticated = false;
		if("ngasce@admin20".equals(password)){
			authenticated = true;
			isLoginAsForm = true;
			request.getSession().setAttribute("userId", userId);
			request.getSession().setAttribute("password", password);
		}else{
			authenticated = false;
			modelnView = new ModelAndView("jsp/loginAs");
			request.setAttribute("error", "true");
			request.setAttribute("errorMessage", "Invalid Credentials. Please re-try.");
			return modelnView;
		}
		Date date = new Date() ;
		SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm") ;
		PortalDao pDao = (PortalDao)act.getBean("portalDAO");

		//Check If Results Live
		boolean showResultsFromCache = checkCanShowResultsFromCache();
		StudentsDataInRedisBean response = new StudentsDataInRedisBean();
		
		if(showResultsFromCache) {
					//	request.getSession().setAttribute("student_studentportal", student);
			try {
				response = getStudentDataFromRedisCache(userId);
			
				if(response.getResultsData() != null)
				{ 
				request.getSession().setAttribute("userId",userId);
				ModelAndView mvResutlsFormCachePage = new ModelAndView("jsp/resutlsFormCachePage");
				mvResutlsFormCachePage.addObject("studentsDataInRedisBean", response);
				return mvResutlsFormCachePage;
				}
			}catch(Exception e) {
//				e.printStackTrace();
//				if((dateFormat.parse(dateFormat.format(date)).after(dateFormat.parse("09:55:00")) && dateFormat.parse(dateFormat.format(date)).before(dateFormat.parse("10:05:00"))) || (dateFormat.parse(dateFormat.format(date)).after(dateFormat.parse("16:55:00")) && dateFormat.parse(dateFormat.format(date)).before(dateFormat.parse("17:08:00"))))
//								{
//									//e.printStackTrace();
//									//logout student
//									//return to login page with error of maintenance
//									modelnView = new ModelAndView("jsp/login");
//									request.setAttribute("error", "true");
//									request.setAttribute("errorMessage", "Server is currently Under Maintenance, Please login after 10 mins.");
//									return modelnView;
//							}
			}
						
					}
		request.setAttribute("SERVER_PATH", SERVER_PATH);
		request.getSession().setAttribute("isLoginAsLead", "false");
		
		if(authenticated){
			
			try {

				MettlExamUpcomingBean mettlExamUpcomingBean = pDao.getMettlUpcomingQuickJoinDB(userId);
				if(mettlExamUpcomingBean.getAcessKey() != null) {
					modelnView = new ModelAndView("jsp/mettl/mettlQuickJoin");
					modelnView.addObject("mettlExamUpcomingBean", mettlExamUpcomingBean);
					modelnView.addObject("joinLink", generateMettlLink(mettlExamUpcomingBean));
					return modelnView;
				}
			}catch(Exception e) {
//				e.printStackTrace();
			}
			
			//Added for set PSS in session Start
			StudentStudentPortalBean student = pDao.getSingleStudentsData(userId);
			
			//Added by Shiv G to avoid getStudentData DB Hits 
			request.getSession().setAttribute("getSingleStudentsData_studentportal",student);
			
			ArrayList<Integer> currentSemPSSId = new ArrayList<Integer>();
			List<Integer> liveSessionPssIdList = new ArrayList<Integer>();
			StudentStudentPortalBean studentRegistrationForAcademicSession = new StudentStudentPortalBean();
			/*boolean isNonPG_Program = Boolean.FALSE;*/
			boolean isFreeLiveSessionApplicable = Boolean.FALSE;
			
			try {
				studentRegistrationForAcademicSession = registrationHelper.checkStudentRegistration(userId, student);

				request.getSession().setAttribute("studentRegistrationForAcademicSession_studentportal", studentRegistrationForAcademicSession);
				
				//Set up latest semester
				student.setSem(studentRegistrationForAcademicSession.getSem());
				//ArrayList<String> waivedOffSubjects = studentService.mgetWaivedOffSubjects(student);
				//currentSemPSSId = pDao.getPSSIds(studentRegistrationForAcademicSession.getConsumerProgramStructureId(), studentRegistrationForAcademicSession.getSem() ,waivedOffSubjects);
				currentSemPSSId = studentCourseService.getPSSID(student.getSapid(),studentRegistrationForAcademicSession.getYear(),studentRegistrationForAcademicSession.getMonth());
				request.getSession().setAttribute("currentSemPSSId_studentportal", currentSemPSSId);
				
				String enrollDate = ("01/" + student.getEnrollmentMonth() + "/" + student.getEnrollmentYear());
				/*isNonPG_Program = nonPG_ProgramList.contains(studentRegistrationForAcademicSession.getProgram());*/
				ArrayList<String> listOfMaterKeyHavingFreeLiveSession = liveSessionAccessService.getListOfFreeLiveSessionAccessMasterKeys(TIMEBOUND_PORTAL_LIST); 
				isFreeLiveSessionApplicable = listOfMaterKeyHavingFreeLiveSession.contains(studentRegistrationForAcademicSession.getConsumerProgramStructureId());
				
				logger.info("(Enrollment Year/Month, LiveSessionAccessDate, Program, NonPG_Program) : ("
						+ enrollDate + "," + LIVE_SESSION_ACCESS_DATE + ","
						+ studentRegistrationForAcademicSession.getProgram() + "," + isFreeLiveSessionApplicable);
				if (DateTimeHelper.checkDate(DateTimeHelper.FORMAT_ddMMMyyyy, enrollDate,
						DateTimeHelper.FORMAT_ddMMMyyyy, LIVE_SESSION_ACCESS_DATE) || isFreeLiveSessionApplicable) {
					request.getSession().setAttribute("liveSessionPssIdAccess_studentportal", currentSemPSSId);
				} else {
					liveSessionPssIdList = liveSessionAccessService.fetchPSSforLiveSessionAccess(userId, studentRegistrationForAcademicSession.getYear(), studentRegistrationForAcademicSession.getMonth());
					request.getSession().setAttribute("liveSessionPssIdAccess_studentportal", liveSessionPssIdList);
				}
			} catch (Exception e) {
//				e.printStackTrace();
				request.getSession().setAttribute("currentSemPSSId_studentportal", currentSemPSSId);
				request.getSession().setAttribute("liveSessionPssIdAccess_studentportal", liveSessionPssIdList);
			}
			//Added for set PSS in session End
			
			
			return executePostAuthenticationActivities(request,respnse, userId, password, isLoginAsForm);
		}else{
			modelnView = new ModelAndView("jsp/loginAs");
//			request.getSession().removeAttribute("userId");
			request.setAttribute("error", "true");
			request.setAttribute("errorMessage", "Invalid Credentials. Please re-try.");
		}

		return modelnView;
	}


	private void getStudentHomePageDetails(StudentStudentPortalBean student, HttpServletRequest request, List<ExamOrderStudentPortalBean> liveFlagList, List<ExecutiveExamOrderStudentPortalBean> ResultliveFlagList) {
		
		PortalDao pDao = (PortalDao)act.getBean("portalDAO");
		AssignmentsDAO adao = (AssignmentsDAO)act.getBean("asignmentsDAO");
		ArrayList<StudentMarksBean> allStudentRegistrations = pDao.getAllRegistrationsFromSAPID(student.getSapid());
		HashMap<String, StudentMarksBean> monthYearAndStudentRegistrationMap = new HashMap<>();
		double reg_order=(double) request.getSession().getAttribute("reg_order");
        double acadContentLiveOrder=(double) request.getSession().getAttribute("acadContentLiveOrder");
		double current_order=(double) request.getSession().getAttribute("current_order"); 
		
		List<StudentRankBean> homepageRank = new ArrayList<>();

		for (StudentMarksBean bean : allStudentRegistrations) {
			monthYearAndStudentRegistrationMap.put(bean.getMonth() + "-" + bean.getYear(), bean);
		}
		//StudentMarksBean studentRegistrationForAcademicSession = getStudentRegistrationForForSpecificLiveSettings(monthYearAndStudentRegistrationMap, liveFlagList, "acadSessionLive");

		
		int hasAssignment = adao.checkHasAssignment(student.getConsumerProgramStructureId());
		// Temporary added static value
		//hasAssignment = -1;
		StudentMarksBean studentRegistrationForAssignment = null;
		if(hasAssignment > 0) {
			studentRegistrationForAssignment = getAssignmentRegistrationForSpecificLiveSettings(monthYearAndStudentRegistrationMap, student.getConsumerProgramStructureId(), "Regular");
		}
		
		String liveTypeForCourses = "acadContentLive";
		if("Yes".equalsIgnoreCase((String)request.getSession().getAttribute("earlyAccess"))){
			liveTypeForCourses = "acadContentLiveNextBatch";
		}
		
		// Added for getCourses
		// added by sachin for registration object
		// StudentMarksBean studentRegistrationForCourses = getStudentRegistrationForForSpecificLiveSettings(monthYearAndStudentRegistrationMap, liveFlagList, liveTypeForCourses);
		
		StudentMarksBean studentRegistrationForCourses = registrationHelper.CheckStudentRegistrationForCourses(monthYearAndStudentRegistrationMap,acadContentLiveOrder,current_order,reg_order,liveFlagList);
		List<ExamBookingTransactionStudentPortalBean> upcomingExams = pDao.getUpcomingExams(student.getSapid());
		request.getSession().setAttribute("upcomingExams", upcomingExams);

		// getAcademicCalendar Checks
		
		StudentStudentPortalBean studentRegistrationForAcademicSession = registrationHelper.checkStudentRegistration(student.getSapid(), student);
		request.getSession().setAttribute("studentRegistrationForAcademicSession_studentportal", studentRegistrationForAcademicSession);
		request.getSession().setAttribute("studentBeanForSession_studentportal", student);
		
		ExamOrderStudentPortalBean examOrderForSession = null;
		if (studentRegistrationForAcademicSession != null) {
			try {
				examOrderForSession = pDao.getExamOrderByYearMonth(studentRegistrationForAcademicSession.getYear(), studentRegistrationForAcademicSession.getMonth());
			} catch (Exception e) {
//				e.printStackTrace();
			}
		}
		request.getSession().setAttribute("examOrderForSession_studentportal", examOrderForSession);
		request.getSession().setAttribute("trackDetails", pDao.getAllTracksDetails());		


		//IMPORTANT: Do not change order of function calls
		// 1. Academic Calendar
		boolean registeredForEvent = false;
		ArrayList<SessionDayTimeStudentPortal> allScheduledSessionList = new ArrayList<SessionDayTimeStudentPortal>();
		if (examOrderForSession != null) {
			getAcademicCalendar(student, request, pDao, studentRegistrationForAcademicSession);
		}else {
			request.getSession().setAttribute("scheduledSessionList_studentportal", allScheduledSessionList);
			request.getSession().setAttribute("registeredForEvent", registeredForEvent);
		}
		
		// 2. Assignment List
		if(hasAssignment > 0) {
			getAssignments(student, request);
		}
		
		//getTests(student.getSapid(),request);

		getResults(student, request, pDao, liveFlagList, ResultliveFlagList );

//		getServiceRequests(student, request);  -- commented by shailesh 
		try {
			getNewSrList(student.getSapid(),request);
		}catch(Exception e) {}
		//getCourses(student, request, pDao, studentRegistrationForCourses);//this must be last function called. Do not change order
		
		getCourses_new(student, request, studentRegistrationForCourses,current_order,acadContentLiveOrder,reg_order);

		//Getting Videos On Home Page
		List<VideoContentStudentPortalBean> videoList = new ArrayList<VideoContentStudentPortalBean>();
		String year = CURRENT_ACAD_YEAR, month = CURRENT_ACAD_MONTH;

		//Read recent registration of a student from the session
		StudentStudentPortalBean stdRegData = (StudentStudentPortalBean) request.getSession().getAttribute("studentRecentReg_studentportal");
		
		//Preparing program structure
		String programStructure = student.getEnrollmentMonth() + student.getEnrollmentYear();
		
		student.setProgram(stdRegData.getProgram());
		student.setSem(stdRegData.getSem());

				
		//Override the current academic year and month with registration year and month only if max academic content live order 
		// and student registration year and month order is same else continue with current academic year and month.
		if(acadContentLiveOrder == reg_order) {
			year = stdRegData.getYear();
			month = stdRegData.getMonth();
		}		

		//Read pssIdsWith subject from the session
		Map<String,String> pssWithSubject = (HashMap<String,String>) request.getSession().getAttribute("programSemSubjectIdWithSubjects_studentportal");
		
		//Get PSS id's into List of string
		List<String> currentSemPSSId = new ArrayList<String>(pssWithSubject.keySet());
		
		//Fetching session recordings for home page from temp table 
		videoList = homeService.getVideos(currentSemPSSId,month,year);

		//getVideos(student, request, stdRegData);

		request.getSession().setAttribute("videoList", videoList);
		checkUFM(student, request, pDao);
		
		/*
		 * added to fetch rank details for home page 
		 * 
		 * */
		try {
			homepageRank = leaderBoardService.getHomepageRankDetails( student );
		} catch (Exception e) {
			// TODO Auto-generated catch block
//			e.printStackTrace();
		}
		
		request.getSession().setAttribute("homepageRank", homepageRank);
		
		//Get latest Earned Badge
		getLatestBadge(student, request);
		
	}

	private void checkUFM(StudentStudentPortalBean student, HttpServletRequest request, PortalDao pDao) {
		boolean markedForUFM = pDao.checkIfStudentMarkedForUFM(student.getSapid());
		
		if(markedForUFM) {
			request.getSession().setAttribute("markedForUFM", "true");
		}
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

	private void getServiceRequests(StudentStudentPortalBean student,HttpServletRequest request) {
		ServiceRequestDao serviceRequestDao = (ServiceRequestDao)act.getBean("serviceRequestDao");
		ArrayList<ServiceRequestStudentPortal> srList =  new ArrayList<ServiceRequestStudentPortal>();
		srList = serviceRequestDao.getStudentsSR(student.getSapid());
		HashMap<String,String> mapOfSRTypesAndTAT = getMapOfSRTypesAndTAT();
		request.getSession().setAttribute("srList", srList);
		request.getSession().setAttribute("mapOfSRTypesAndTAT", mapOfSRTypesAndTAT);
	}

	protected ResultsFromRedisHelper fetchRedisHelper() {
		ResultsFromRedisHelper resultsFromRedisHelper = null;
		resultsFromRedisHelper = (ResultsFromRedisHelper) act.getBean("resultsFromRedisHelper");
		return resultsFromRedisHelper;
	}
	
	protected void nullifyResultsData(HttpServletRequest request) {
		
		if(null != request.getSession()) {
			logger.info("HomeController : nullifyResultsData : Nullify Session data of results.");
			
			//Section 1: Dashboard Results require only following 3
			// request.getSession().setAttribute("declareDate_studentportal", null);//Unused on JSP-Vilpesh on 2021-10-29
			request.getSession().setAttribute(S_MOST_RECENT_RESULT_PERIOD, null);
			request.getSession().setAttribute(S_MARKS_LIST, null);
			request.getSession().setAttribute(S_MARKS_LIST_SIZE, null);
	
			//Section 2: required to show or prepare results on /viewCourseDetails- Vilpesh on 2021-11-23
			request.getSession().setAttribute(S_DECLAREDATE_RR, null);
			request.getSession().setAttribute(S_PASSFAIL_RR, null);
			request.getSession().setAttribute(S_MARKS_HISTORY_RR, null);
		}
	}

	private void getResults(StudentStudentPortalBean student, HttpServletRequest request, PortalDao dao, List<ExamOrderStudentPortalBean> liveFlagList,List<ExecutiveExamOrderStudentPortalBean>  ResultliveFlagList) {

		/*if will read results from REDIS since ShowResultsFromCache flag is not checked. Hence commented Vilpesh 2021-12-07
		if(checkIfMovingResultsToCache()) { 
			request.getSession().setAttribute("mostRecentResultPeriod_studentportal", "");
			request.getSession().setAttribute("declareDate_studentportal", "");
			request.getSession().setAttribute("studentMarksList_studentportal", new ArrayList<StudentMarksBean>());

		  return; 
		 }*/
		
//		List<StudentMarksBean> studentMarksList = new ArrayList<StudentMarksBean>();
//		String mostRecentResultPeriod = "";
//		String declareDate = "";
//		SimpleDateFormat sdfr = new SimpleDateFormat("dd-MMM-yyyy");

//		double onlineLiveOrder = 0.0;
//		double offlineLiveOrder = 0.0;
//		String offlineResultDeclareDateString = null;
//		String onlineResultDeclareDateString = null; 
//		String mostRecentOnlineResultPeriod = "";
//		String mostRecentOfflineResultPeriod = "";

//		if(student.getProgram().equalsIgnoreCase("EPBM") || student.getProgram().equalsIgnoreCase("MPDV") ){
//
//			for (ExecutiveExamOrderBean bean : ResultliveFlagList) {
//				double currentOrder = Double.parseDouble(bean.getOrder());
//				if("Y".equalsIgnoreCase(bean.getResultLive()) && currentOrder > onlineLiveOrder){
//					onlineLiveOrder = currentOrder;
//					mostRecentOnlineResultPeriod = bean.getMonth() + "-" + bean.getYear();
//					onlineResultDeclareDateString = bean.getDeclareDate();
//				}
//			}
//
//			mostRecentResultPeriod = mostRecentOnlineResultPeriod;
//			try {
//				declareDate = sdfr.format(onlineResultDeclareDateString);
//			} catch (Exception e) {
//				declareDate = "";
//			} 
//			studentMarksList =  dao.getAExecutiveStudentsMostRecentMarks(student.getSapid());
//
//			if(studentMarksList != null){
//				for (StudentMarksBean studentMarksBean : studentMarksList) {
//
//					int writtenScore = 0;
//
//					try {
//						writtenScore = Integer.parseInt(studentMarksBean.getWritenscore());
//					} catch (Exception e) {}
//
//					int total = writtenScore;
//					studentMarksBean.setTotal(total+"");
//				}
//			}
//
//
//		}else{
//
//			for (ExamOrderBean bean : liveFlagList) {
//				double currentOrder = Double.parseDouble(bean.getOrder());
//				if("Y".equalsIgnoreCase(bean.getLive()) && currentOrder > onlineLiveOrder){
//					onlineLiveOrder = currentOrder;
//					mostRecentOnlineResultPeriod = bean.getMonth() + "-" + bean.getYear();
//					onlineResultDeclareDateString = bean.getDeclareDate();
//				}
//
//				if("Y".equalsIgnoreCase(bean.getOflineResultslive()) && currentOrder > offlineLiveOrder){
//					offlineLiveOrder = currentOrder;
//					mostRecentOfflineResultPeriod = bean.getMonth() + "-" + bean.getYear();
//					offlineResultDeclareDateString = bean.getOflineResultsDeclareDate();
//				}
//			}
//
//
//			if("Online".equals(student.getExamMode())){
//				//mostRecentResultPeriod = dao.getMostRecentResultPeriod();
//				//declareDate = dao.getRecentExamDeclarationDate();
//				mostRecentResultPeriod = mostRecentOnlineResultPeriod;
//				try {
//					declareDate = sdfr.format(onlineResultDeclareDateString);
//				} catch (Exception e) {
//					declareDate = "";
//				} 
//				studentMarksList =  dao.getAStudentsMostRecentMarks(student.getSapid());
//			}else{
//				//mostRecentResultPeriod = dao.getMostRecentOfflineResultPeriod();
//				//declareDate = dao.getRecentOfflineExamDeclarationDate();
//				mostRecentResultPeriod = mostRecentOfflineResultPeriod;
//				try {
//					declareDate = sdfr.format(offlineResultDeclareDateString);
//				} catch (Exception e) {
//					declareDate = "";
//				} 
//				studentMarksList =  dao.getAStudentsMostRecentOfflineMarks(student.getSapid());
//			}
//
//			if(studentMarksList != null){
//				for (StudentMarksBean studentMarksBean : studentMarksList) {
//					int assignmentScore = 0;
//					int writtenScore = 0;
//
//					try {
//						assignmentScore = Integer.parseInt(studentMarksBean.getAssignmentscore());
//					} catch (Exception e) {}
//
//					try {
//						writtenScore = Integer.parseInt(studentMarksBean.getWritenscore());
//					} catch (Exception e) {}
//
//					int total = assignmentScore + writtenScore;
//					studentMarksBean.setTotal(total+"");
//				}
//			}
//		}

		
	try {
		/*NOTE: Earlier decision was based on checkIfMovingResultsToCache(), which only read movingResultsToCache=Y.
		Now, another flag showResultsFromCache=N, must lead to results read from REDIS. Vilpesh 2021-12-07*/
		if(this.fetchRedisHelper().readFromCache()) {
		
				//NOTE: Results fetched from REDIS to display. Added by Vilpesh on 2021-10-28
				String declareDate = null;
				List<PassFailBean> listPassFailBean = null;
				List<StudentMarksBean> listStudentMarksBean2 = null;
				List<StudentMarksBean> studentMarksList = null;
				String mostRecentResultPeriod = null;
				Integer size = 0;
				List<StudentMarksBean> markslist = null;
				Map<String, Object> destinationMap = null;
		
				destinationMap = this.fetchRedisHelper().fetchResultsFromRedis(ResultsFromRedisHelper.EXAM_STAGE_TEE,
						student.getSapid());
				
				if(null != destinationMap) {
					//declareDate = ((List<String>) destinationMap.get(ResultsFromRedisHelper.KEY_DECLARE_DATE)).get(0);
					if(null != destinationMap.get(ResultsFromRedisHelper.KEY_MOST_RECENT_RESULT_PERIOD)) {
						mostRecentResultPeriod = ((String) destinationMap.get(ResultsFromRedisHelper.KEY_MOST_RECENT_RESULT_PERIOD));
					}
					
					if(null != destinationMap.get(ResultsFromRedisHelper.KEY_SIZE)) {
						size = ((Integer) destinationMap.get(ResultsFromRedisHelper.KEY_SIZE));
					}
					
					if(null != destinationMap.get(ResultsFromRedisHelper.KEY_STUDENT_MARKSLIST)) {
						markslist = (List<StudentMarksBean>) destinationMap.get(ResultsFromRedisHelper.KEY_STUDENT_MARKSLIST);
						studentMarksList = markslist;
					}
					//request.getSession().setAttribute("declareDate_studentportal", declareDate);//Unused on JSP- Vilpesh on 2021-10-29
					request.getSession().setAttribute(S_MOST_RECENT_RESULT_PERIOD, mostRecentResultPeriod);
					request.getSession().setAttribute(S_MARKS_LIST, studentMarksList);
					request.getSession().setAttribute(S_MARKS_LIST_SIZE, size);
					
					//Section 2: required to show or prepare results on /viewCourseDetails- Vilpesh on 2021-11-23
					if(null != destinationMap.get(ResultsFromRedisHelper.KEY_DECLARE_DATE)) {
						declareDate = ((List<String>) destinationMap.get(ResultsFromRedisHelper.KEY_DECLARE_DATE)).get(0);
					}
					if(null != destinationMap.get(ResultsFromRedisHelper.KEY_STUDENT_MARKS_HISTORY)) {
						listStudentMarksBean2 = (List<StudentMarksBean>) destinationMap.get(ResultsFromRedisHelper.KEY_STUDENT_MARKS_HISTORY);
					}
					if(null != destinationMap.get(ResultsFromRedisHelper.KEY_PASSFAIL_STATUS)) {
						listPassFailBean = (List<PassFailBean>) destinationMap.get(ResultsFromRedisHelper.KEY_PASSFAIL_STATUS);
					}
					
					request.getSession().setAttribute(S_DECLAREDATE_RR, declareDate);
					request.getSession().setAttribute(S_MARKS_HISTORY_RR, listStudentMarksBean2);
					request.getSession().setAttribute(S_PASSFAIL_RR, listPassFailBean);
					
					logger.info("HomeController : getResults : Pulled results from REDIS(can be empty) to be used in Dashboard or /viewCourseDetails");
				} else {
					this.nullifyResultsData(request);
					logger.info("HomeController : getResults : No results from REDIS, used in Dashboard or /viewCourseDetails");
				}
			} else {
				this.nullifyResultsData(request);
				logger.info("HomeController : getResults : No results from REDIS, used in Dashboard or /viewCourseDetails");
			}
		} catch (Exception e) {
//			e.printStackTrace();
			logger.error("HomeController : getResults : exception : "+ e.getMessage());
			
			//if REDIS stopped - exception catched - page loading continued -Vilpesh on 2021-11-19
			this.nullifyResultsData(request);
			logger.info("HomeController : getResults : No results from REDIS, used in Dashboard or /viewCourseDetails");
		}
	}


	/*	private void getAssignments(StudentBean student,HttpServletRequest request, PortalDao pDao, StudentMarksBean studentRegistrationData) {

		AssignmentsDAO dao = (AssignmentsDAO)act.getBean("asignmentsDAO");
		String sapId = student.getSapid();
		boolean isOnline = isOnline(student);

		ArrayList<String> currentSemSubjects = new ArrayList<>();
		ArrayList<String> failSubjects = new ArrayList<>();
		ArrayList<String> applicableSubjects = new ArrayList<>();

		//List<AssignmentFileBean> failSubjectsAssignmentFilesList = new ArrayList<>();
		//List<AssignmentFileBean> currentSemAssignmentFilesList = new ArrayList<>();

		HashMap<String, String> subjectSemMap = new HashMap<>();
		//int currentSemSubmissionCount = 0;
		//int failSubjectSubmissionCount = 0;


		//StudentBean studentRegistrationData = dao.getStudentRegistrationDataForAssignment(sapId);
		String currentSem = null;

		int maxAttempts = Integer.parseInt(ASSIGNMENT_SUBMISSIONS_ATTEMPTS);


		currentSem = student.getSem();
		if(studentRegistrationData != null){
			student.setSem(studentRegistrationData.getSem());
			student.setProgram(studentRegistrationData.getProgram());
			currentSemSubjects = getSubjectsForStudent(student, subjectSemMap);
			currentSemSubjects.remove("Project"); //Project not applicable for Assignments submission
		}

		failSubjects = new ArrayList<>();
		//if((currentSem != null && (!"1".equals(currentSem))) || studentRegistrationData == null){
		//If current semester is 1, then there cannot be any failed subjects

		ArrayList<AssignmentFileBean> failSubjectsBeans = getFailSubjects(student, pDao);
		if(failSubjectsBeans != null && failSubjectsBeans.size() > 0){

			for (int i = 0; i < failSubjectsBeans.size(); i++) {
				String subject = failSubjectsBeans.get(i).getSubject();
				String sem = failSubjectsBeans.get(i).getSem();
				failSubjects.add(failSubjectsBeans.get(i).getSubject());
				subjectSemMap.put(subject, sem);
			}
		}

		//}

		ArrayList<AssignmentFileBean> failANSSubjectsBeans = getANSNotProcessed(student,pDao);
		if(failANSSubjectsBeans != null && failANSSubjectsBeans.size() > 0){

			for (int i = 0; i < failANSSubjectsBeans.size(); i++) {
				String subject = failANSSubjectsBeans.get(i).getSubject();
				String sem = failANSSubjectsBeans.get(i).getSem();
				failSubjects.add(failANSSubjectsBeans.get(i).getSubject());
				subjectSemMap.put(subject, sem);
			}
		}
		failSubjects.remove("Project"); //Project not applicable for Assignments submission


		for (String failedSubject : failSubjects) {
			//For ANS cases, where result is not declared, failed subject will also be present in Current sem subject.
			//Give preference to it as Failed, so that assignment can be submitted and remove  from Current list
			if(currentSemSubjects.contains(failedSubject)){
				currentSemSubjects.remove(failedSubject);
			}
		}


		currentSemSubjects.remove("Project");
		applicableSubjects.addAll(currentSemSubjects);
		applicableSubjects.addAll(failSubjects);
		applicableSubjects.remove("Project");

		request.getSession().setAttribute("subjectsForStudent", applicableSubjects);
		List<AssignmentFileBean> allAssignmentFilesList = new ArrayList<>();
		Commented By Steffi----to allow offine students fro APR/SEP Submissions  
	 * if(!isOnline){
			allAssignmentFilesList = dao.getAssignmentsForSubjects(applicableSubjects, student);
		}else{
			List<AssignmentFileBean> currentSemFiles = dao.getAssignmentsForSubjects(currentSemSubjects, student);
			List<AssignmentFileBean> failSubjectFiles = dao.getResitAssignmentsForSubjects(failSubjects, student);

			if(currentSemFiles != null){
				allAssignmentFilesList.addAll(currentSemFiles);
			}

			if(failSubjectFiles != null){
				allAssignmentFilesList.addAll(failSubjectFiles);
			}
		}

		if(allAssignmentFilesList != null ){

			HashMap<String,AssignmentFileBean> subjectSubmissionMap = new HashMap<>();
			 Commented by Steffi to allow offline students to submit assignments in APR/SEP 
	 * if(!isOnline){
				subjectSubmissionMap = dao.getSubmissionStatus(applicableSubjects, student.getSapid());//Assignments from Jun, Dec cycle
			}else{
				//For online, resit i.e. fail subjects paper change after resit date is over.//Applicable cycle is all 4 i.e. Apr, Jun, Sep, Dec
				HashMap<String,AssignmentFileBean>  currentSemSubjectSubmissionMap = dao.getSubmissionStatus(currentSemSubjects, sapId);
				HashMap<String,AssignmentFileBean>  failSubjectSubmissionMap = dao.getResitSubmissionStatus(failSubjects, sapId);

				if(currentSemSubjectSubmissionMap != null){
					subjectSubmissionMap.putAll(currentSemSubjectSubmissionMap);
				}

				if(failSubjectSubmissionMap != null){
					subjectSubmissionMap.putAll(failSubjectSubmissionMap);
				}
			//}

			for(AssignmentFileBean assignment : allAssignmentFilesList){
				String subject = assignment.getSubject();
				String status = "Not Submitted";
				String attempts = "0";
				String lastModifiedDate = "";
				String previewPath = "";

				AssignmentFileBean studentSubmissionStatus = subjectSubmissionMap.get(subject);
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
			}
		}

		request.getSession().setAttribute("allAssignmentFilesList_studentportal", allAssignmentFilesList);


	}
	 */
	private void getAssignmentsOldToRemove(StudentStudentPortalBean student,HttpServletRequest request, PortalDao pDao, StudentMarksBean studentRegistrationData) {

		if(checkIfMovingResultsToCache()) {
			return;
		}
		
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


		String currentSem = null;

		int maxAttempts = Integer.parseInt(ASSIGNMENT_SUBMISSIONS_ATTEMPTS);


		if(studentRegistrationData != null){
			//Take program from Registration data and not Student data. 
			student.setProgram(studentRegistrationData.getProgram());
			student.setSem(studentRegistrationData.getSem());
			currentSem = studentRegistrationData.getSem();
			currentSemSubjects = getSubjectsForStudent(student, subjectSemMap);
			//currentSemSubjects = studentCourseService.getCurrentCycleSubjects(student.getSapid(),studentRegistrationData.getYear(),studentRegistrationData.getMonth());
		}
	
		//currentSemSubjects.addAll(student.getWaivedInSubjects());		//add waived in subjects
		subjectSemMap.putAll(student.getWaivedInSubjectSemMapping());	//add waived in subject sem mapping
		
		ArrayList<String> passSubjectsList = getPassSubjects(student,pDao);
		if(!passSubjectsList.isEmpty() && passSubjectsList != null){
			for(String subject:passSubjectsList){
				if(currentSemSubjects.contains(subject)){
					currentSemSubjects.remove(subject);
				}
			}
		}

		failSubjects = new ArrayList<>();
		//if((currentSem != null && (!"1".equals(currentSem))) || studentRegistrationData == null){
		//If current semester is 1, then there cannot be any failed subjects

		ArrayList<AssignmentStudentPortalFileBean> failSubjectsBeans = getFailSubjects(student,pDao);
		if(failSubjectsBeans != null && failSubjectsBeans.size() > 0){

			for (AssignmentStudentPortalFileBean bean : failSubjectsBeans) {
				String subject = bean.getSubject();
				String sem = bean.getSem();
				failSubjects.add(bean.getSubject());
				subjectSemMap.put(subject, sem);

				if("ANS".equalsIgnoreCase(bean.getAssignmentscore())){
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

		ArrayList<AssignmentStudentPortalFileBean> currentSemResultAwaitedSubjectsList = new ArrayList<AssignmentStudentPortalFileBean>();

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
		failSubjects.remove("Project");
		
		currentSemSubjects.remove("Module 4 - Project");
		failSubjects.remove("Module 4 - Project");

		applicableSubjects.addAll(currentSemSubjects);
		applicableSubjects.addAll(failSubjects);
		applicableSubjects.remove("Project");

		applicableSubjects.remove("Module 4 - Project");

		//remove subjects applicable for test start
		try {
			currentSemSubjects = (ArrayList<String>) removeSubjectsApplicableForTestFromList(student, currentSemSubjects);
			failSubjects = (ArrayList<String>) removeSubjectsApplicableForTestFromList(student, failSubjects);
			applicableSubjects = (ArrayList<String>) removeSubjectsApplicableForTestFromList(student, applicableSubjects);
		} catch (Exception e) {
			// TODO Auto-generated catch block
//			e.printStackTrace();
		}
		//remove subjects applicable for test end

		request.getSession().setAttribute("subjectsForStudent", applicableSubjects);
		List<AssignmentStudentPortalFileBean> allAssignmentFilesList = new ArrayList<>();
		/*Commented by Steffi to allow offline students to submit assignments in APR/SEP 
		 * 	if(!isOnline){
			allAssignmentFilesList = dao.getAssignmentsForSubjects(applicableSubjects, student);
		}else{*/
		List<AssignmentStudentPortalFileBean> currentSemFiles = null;
		List<AssignmentStudentPortalFileBean> failSubjectFiles = null;
		if(currentSemSubjects != null && currentSemSubjects.size()>0){
			currentSemFiles = dao.getAssignmentsForSubjects(currentSemSubjects, student);
		}
		if(failSubjects != null && failSubjects.size()>0){
			failSubjectFiles = dao.getResitAssignmentsForSubjects(failSubjects, student);
		}

		if(currentSemFiles != null){
			allAssignmentFilesList.addAll(currentSemFiles);
		}

		if(failSubjectFiles != null){

			allAssignmentFilesList.addAll(failSubjectFiles);
		}
		//}

		if(allAssignmentFilesList != null ){

			HashMap<String,AssignmentStudentPortalFileBean> subjectSubmissionMap = new HashMap<>();
			/*Commented by Steffi to allow offline students to submit assignments in APR/SEP 
			 * if(!isOnline){
				subjectSubmissionMap = dao.getSubmissionStatus(applicableSubjects, sapId);//Assignments from Jun, Dec cycle
			}else{*/
			//For online, resit i.e. fail subjects paper change after resit date is over.//Applicable cycle is all 4 i.e. Apr, Jun, Sep, Dec
			request.getSession().setAttribute("currentSemSubjects_studentportal", currentSemSubjects);
			request.getSession().setAttribute("failSubjects_studentportal", failSubjects);
			request.getSession().setAttribute("subjectSemMap_studentportal", subjectSemMap);

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
					previewPath = subjectSubmissionMap.get(subject).getPreviewPath();
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
			}
		}
		
		if(allAssignmentFilesList != null && allAssignmentFilesList.size() > 0) {

			for (AssignmentStudentPortalFileBean assignmentFileBean : failSubjectsAssignmentFilesList) {
				if(ANSSubjects.contains(assignmentFileBean.getSubject())){
					//ANS cases will always be allowed to Submit
					assignmentFileBean.setSubmissionAllowed(true);
					subjectsNotAllowedToSubmit.remove(assignmentFileBean.getSubject());
				}else if(subjectsNotAllowedToSubmit.contains(assignmentFileBean.getSubject())){
					assignmentFileBean.setSubmissionAllowed(false);
				}else{
					assignmentFileBean.setSubmissionAllowed(true);
				}


				int pastCycleAssignmentAttempts = dao.getPastCycleAssignmentAttempts(assignmentFileBean.getSubject(), sapId);
				if(pastCycleAssignmentAttempts >=2 && !"Submitted".equals(assignmentFileBean.getStatus())){
					assignmentFileBean.setPaymentApplicable("Yes");
					boolean hasPaidForAssignment = dao.checkIfAssignmentFeesPaid(assignmentFileBean.getSubject(), sapId); ////check if Assignment Fee Paid for Current drive 
					if(!hasPaidForAssignment){
						assignmentFileBean.setPaymentDone("No");
					}else{
						assignmentFileBean.setPaymentDone("Yes");
					}
				}
			}
		}
		
		request.getSession().setAttribute("subjectsNotAllowedToSubmitDashboard", subjectsNotAllowedToSubmit);
		/*
		 * Added by Pranit on 24 Dec 18 to hide assignments on dashboard for offline students 
		 * */
		if(!"Online".equalsIgnoreCase(student.getExamMode())){
			allAssignmentFilesList = new ArrayList<>();
		}
		request.getSession().setAttribute("allAssignmentFilesList_studentportal", allAssignmentFilesList);


	}

	private List<String> removeSubjectsApplicableForTestFromList(StudentStudentPortalBean student, List<String> listOfSubjects) {
		ArrayList<ProgramSubjectMappingStudentPortalBean> programSubjectMappingList = getProgramSubjectMappingList();
		List<String> subjects = new ArrayList<>();
		if(listOfSubjects !=null && listOfSubjects.size() > 0) {
			for(String s : listOfSubjects) {
				for (int i = 0; i < programSubjectMappingList.size(); i++) {
					ProgramSubjectMappingStudentPortalBean bean = programSubjectMappingList.get(i);

					if(
							bean.getPrgmStructApplicable().equals(student.getPrgmStructApplicable()) 
							&& bean.getProgram().equals(student.getProgram())
							&& !student.getWaivedOffSubjects().contains(bean.getSubject())//Subjects has not already cleared it
							&& "Y".equals(bean.getHasTest())
							&& s.equalsIgnoreCase(bean.getSubject())
							){
						subjects.add(bean.getSubject());
						break;
					}
				}
			}

			listOfSubjects.removeAll(subjects);
		}
		return listOfSubjects;
	}

	private void getVideos(StudentStudentPortalBean student, HttpServletRequest request, StudentStudentPortalBean studentRegistrationForAcademicSession) {
		ArrayList<VideoContentStudentPortalBean> videoList = new ArrayList<VideoContentStudentPortalBean>();
		HttpServletResponse response = null;
		String programStructure = student.getEnrollmentMonth() + student.getEnrollmentYear();
		String acadDateFormat = null;
		
		student.setProgram(studentRegistrationForAcademicSession.getProgram());
		student.setSem(studentRegistrationForAcademicSession.getSem());
		
		//Commented By Somesh as getting PSS from session
		/*
		ArrayList<String> subjects = getSubjectsForStudent(student);
		//Remove WaiveOff Subject from applicable Subject list
		subjects.removeAll(student.getWaivedOffSubjects());
		ArrayList<String> currentSemPSSId = contentDAO.getProgramSemSubjectId(subjects, student.getConsumerProgramStructureId());
		*/
		
		ArrayList<String> currentSemPSSId = (ArrayList<String>)request.getSession().getAttribute("currentSemPSSId_studentportal");
		
		
//		if(checkLead(request,response)) {
//			videoList = contentDAO.getSessionForLead(getSubjectsForStudent(student), programStructure);
//		}else {

		//Temp Commented By Somesh
		//videoList = contentDAO.getSessionOnHome(currentSemPSSId, studentRegistrationForAcademicSession);
		
		//Convert student registration month and year to YYYY-MM-DD format for acadDateFormat 
		acadDateFormat = ContentUtil.pepareAcadDateFormat(studentRegistrationForAcademicSession.getMonth(),
		studentRegistrationForAcademicSession.getYear());

		//Fetching recent session recordings for home page from temp table 
		//videoList = (ArrayList<VideoContentBean>) contentDAO.getSessionRecordingOnHome(currentSemPSSId,acadDateFormat);

			//Adding sessions by course mapping

			//videoList.addAll(contentDAO.getSessionsByCourseMapping(student.getSapid()));
		//}
		
		//Added for sorting
		if (videoList.size() > 0) {
			Collections.sort(videoList, new Comparator<VideoContentStudentPortalBean>() {
				@Override
				public int compare(VideoContentStudentPortalBean vBean1, VideoContentStudentPortalBean vBean2) {
					return vBean2.getSessionDate().compareTo(vBean1.getSessionDate());
				}
			});
		}
		
		request.getSession().setAttribute("videoList", videoList);
	}

	private void getTests(String sapId, HttpServletRequest request) {
		List<TestStudentPortalBean> testsForStudent = new ArrayList<>();
		try {
			testsForStudent = getTestsForStudentFromExamApp(sapId);
		} catch (Exception e) {
			// TODO Auto-generated catch block	
//			e.printStackTrace();
		}
		request.getSession().setAttribute("testsForStudent", testsForStudent);	
	}

	private boolean isOnline(StudentStudentPortalBean student) {
		if("Online".equalsIgnoreCase(student.getExamMode())){
			return true;
		}else{
			return false;
		}
	}

	private void getCourses(StudentStudentPortalBean student, HttpServletRequest request,PortalDao pDao, StudentMarksBean studentRegistrationData) {

	
		//All Subjects list going add in this list
		ArrayList<String> allApplicableSubjects = new ArrayList<String>();
		
		//Current Cycle Subjects(If registration in current cycle)
		ArrayList<String> currentSemSubjects = new ArrayList<String>();
		
		//Subjects never appeared hence no entry in pass fail//
		ArrayList<String> notPassedSubjects = new ArrayList<String>();
		
		//Backlog subjects if any
		//ArrayList<String> backlogSubjects=new ArrayList<String>();
		
		//Failed subjects list
		ArrayList<String> failedSubjects = new ArrayList<String>();
		
		//Waived-off subjects if any (Previous(Old) program passed subjects)
		ArrayList<String> waivedOffSubjects = new ArrayList<String>();
		
		//Waived-off subjects if any (Applicable for lateral student)
		ArrayList<String> waivedInSubjects = new ArrayList<String>();
		
		
		/*StudentBean studentRegistrationData = new StudentBean();
		studentRegistrationData = pDao.getStudentRegistrationDataForContent(student.getSapid());
		if("Yes".equalsIgnoreCase((String)request.getSession().getAttribute("earlyAccess"))){
			studentRegistrationData = pDao.getStudentRegistrationDataForNextBatch(student.getSapid());
		}*/
			
		if(registrationHelper.twoAcadCycleCourses(request)){
			student.setSem(studentRegistrationData.getSem());
			student.setProgram(studentRegistrationData.getProgram());
			student.setConsumerProgramStructureId(studentRegistrationData.getConsumerProgramStructureId());
//			currentSemSubjects = getSubjectsForStudent(student);
			waivedOffSubjects = studentService.mgetWaivedOffSubjects(student);
			currentSemSubjects = pDao.getCurrentCycleSubjects(studentRegistrationData.getConsumerProgramStructureId() , studentRegistrationData.getSem(), waivedOffSubjects);
		}
		
		failedSubjects = pDao.getFailSubjectsNamesForAStudent(student.getSapid());
		waivedInSubjects = student.getWaivedInSubjects();

		if(currentSemSubjects != null) {
			for(String subject : waivedInSubjects) {
				if(!currentSemSubjects.contains(subject) && !failedSubjects.contains(subject)) {
					//Add waived in subject if not available in current cycle list
					currentSemSubjects.add(subject);
				}
			}
			
			if(failedSubjects.size() > 0)
				currentSemSubjects.removeAll(failedSubjects);
		}

		/*else {
			currentSemSubjects = waivedInSubjects;
		}*/

		if(currentSemSubjects == null && (student.getProgram().equalsIgnoreCase("EPBM") || student.getProgram().equalsIgnoreCase("MPDV"))){
			currentSemSubjects = getSubjectsForStudent(student);
		}else if(currentSemSubjects == null){
			currentSemSubjects = new ArrayList<>();
		}
		
		
		/*
		ArrayList<String> currentsemsub=new ArrayList<String>(currentSemSubjects);
		for(String subjects :currentsemsub)
		{
			if(student.getWaivedOffSubjects().contains(subjects))
			{
				currentSemSubjects.remove(subjects);
			}
		}
		*/

		//Add current sem subject in all applicable subject
		allApplicableSubjects.addAll(currentSemSubjects);

		//Add failed subject in all applicable subject
		if(failedSubjects != null){
			allApplicableSubjects.addAll(failedSubjects);
		}else{
			failedSubjects = new ArrayList<String>();
		}
		
		
		notPassedSubjects = pDao.getNotPassedSubjectsBasedOnSapid(student.getSapid()); 
		if(currentSemSubjects == null){
			notPassedSubjects.addAll(waivedInSubjects);
		}
		
		ArrayList<String> notPassed= new ArrayList<String>(notPassedSubjects);
		
		for(String subjects :notPassed){
			if(student.getWaivedOffSubjects().contains(subjects)){
				notPassedSubjects.remove(subjects);
			}
		}

		
		if(notPassedSubjects != null && notPassedSubjects.size() > 0){
			//added by sachin
			//in case if student is in current acad cycle then he/she will get all subjects of current sem and previous sem also thats why i am removing here
			notPassedSubjects.removeAll(currentSemSubjects);
			
			failedSubjects.addAll(notPassedSubjects);
			
			//backlogSubjects.addAll(notPassedSubjects);//adding in backlog list
			//Add failed subject in all applicable subject
			allApplicableSubjects.addAll(notPassedSubjects);
		}
		
		ArrayList<String> lstOfApplicableSubjects = new ArrayList<String>(new LinkedHashSet<String>(allApplicableSubjects));

		//Remove WaiveOff Subject from applicable Subject list
		for(String subjects :allApplicableSubjects){
			if(student.getWaivedOffSubjects().contains(subjects)){
				lstOfApplicableSubjects.remove(subjects);
			}
		}

		// Block to check the remark pass fail subjects which currently is only applicable for BBA and BCOM students.
		if(student.getProgram().equals("BBA") || student.getProgram().equals("B.Com")) {
			try {
				List<String> ugPassSubjectsList = getUGPassSubjects(pDao, student);
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
					if(lstOfApplicableSubjects.contains(subject)){
						lstOfApplicableSubjects.remove(subject);
					}
				}
			} catch (Exception e) {
				// TODO Auto-generated catch block
				//e.printStackTrace();
			}
		}
		    
		//backlogSubjects.addAll(failedSubjects);// added by sachin

		HashMap<String,String> programSemSubjectIdWithSubject = new HashMap<String,String>();
		HashMap<String,String> programSemSubjectIdWithSubjectForBacklog = new HashMap<String,String>();
		HashMap<String,String> programSemSubjectIdWithSubjectForCurrentsem = new HashMap<String,String>();
		
		//Get map of subject and PSSId for all subjects
		if(lstOfApplicableSubjects.size() > 0) {
			try{
				programSemSubjectIdWithSubject = pDao.getProgramSemSubjectId(lstOfApplicableSubjects, student.getConsumerProgramStructureId());
			}catch(Exception e){
//				e.printStackTrace();
			}
		}
		
		//Get map of subject and PSSId for Current Cycle Subjects
		if(currentSemSubjects.size() > 0) {
			try{
				programSemSubjectIdWithSubjectForCurrentsem = pDao.getProgramSemSubjectId(currentSemSubjects, student.getConsumerProgramStructureId());
			}catch(Exception e){
//				e.printStackTrace();
			}
		}
		
		//Get map of subject and PSSId for Backlog
		if(failedSubjects.size() > 0) {
			try{
				programSemSubjectIdWithSubjectForBacklog = pDao.getProgramSemSubjectId(failedSubjects, student.getConsumerProgramStructureId());
			}catch(Exception e){
//				e.printStackTrace();
			}
		}
		
		if (programSemSubjectIdWithSubjectForCurrentsem.size() != 0 ) {
			request.getSession().setAttribute("type", "Ongoing ");
		} else if (programSemSubjectIdWithSubjectForBacklog.size() != 0 ){
			request.getSession().setAttribute("type", "Backlog ");
		}else {
			request.getSession().setAttribute("type", "");
		}
		
		
		
		request.getSession().setAttribute("failedSubjects", failedSubjects);
		request.getSession().setAttribute("currentSemSubjects_studentportal", currentSemSubjects);
		request.getSession().setAttribute("studentCourses_studentportal", lstOfApplicableSubjects);
		request.getSession().setAttribute("programSemSubjectIdWithSubjects_studentportal", programSemSubjectIdWithSubject);
		request.getSession().setAttribute("programSemSubjectIdWithSubjectForBacklog", programSemSubjectIdWithSubjectForBacklog);// added by sachin
		request.getSession().setAttribute("programSemSubjectIdWithSubjectForCurrentsem", programSemSubjectIdWithSubjectForCurrentsem);//added by sachin
	}

	private List<String> getUGPassSubjects(PortalDao pDao, StudentStudentPortalBean student) {
		return pDao.getUGPassSubjectsForAStudent(student.getSapid());
	}
	
	private void getAcademicCalendar(StudentStudentPortalBean student, HttpServletRequest request, PortalDao pDao, StudentStudentPortalBean studentRegistrationData) {
		//StudentBean studentRegistrationData = pDao.getStudentRegistrationDataForAcademicSession(student.getSapid());

		ArrayList<SessionDayTimeStudentPortal> scheduledSessionList = new ArrayList<>();
		request.getSession().setAttribute("studentSessionList", scheduledSessionList);
		
		ArrayList<SessionDayTimeStudentPortal> commonscheduledSessionList = new ArrayList<SessionDayTimeStudentPortal>();
		ArrayList<SessionDayTimeStudentPortal> allScheduledSessionList = new ArrayList<SessionDayTimeStudentPortal>();

		boolean isCourseMappingAvailable = pDao.isCourseMappingApplicableForCurrentAcadCycle(student.getSapid());
		boolean registeredForEvent = false;
		
		if(studentRegistrationData == null){
			
			if (isCourseMappingAvailable) {
				allScheduledSessionList = pDao.getAllSessionsByCourseMapping(student.getSapid());			
				request.getSession().setAttribute("scheduledSessionList_studentportal", allScheduledSessionList);
				request.getSession().setAttribute("registeredForEvent", registeredForEvent);
				return; //Course Mapping session are available even student is not registered
			}
			
			return; //No sessions if student has not registered for current academic cycle
		}

		Online_EventBean onlineEvent = pDao.getLiveOnlineEvent(studentRegistrationData.getProgram(),studentRegistrationData.getSem(),student.getPrgmStructApplicable());
		
		if(onlineEvent.getId() != null){
			registeredForEvent = pDao.getOnlineEventRegistration(student.getSapid(),onlineEvent.getId());
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
		
		ArrayList<Integer> currentSemPSSId = (ArrayList<Integer>)request.getSession().getAttribute("currentSemPSSId_studentportal");
		
		scheduledSessionList = pDao.getScheduledSessionForStudentsByCPSIdV3(studentRegistrationData.getYear(), studentRegistrationData.getMonth(), currentSemPSSId);

		//Adding sessions by course mapping
		//scheduledSessionList.addAll(pDao.getAllSessionsByCourseMapping(student.getSapid()));
		
		//Commented by Somesh as Added new table for Common Sessions
		/*
		if(!"BBA".equalsIgnoreCase(student.getProgram()) && !"B.Com".equalsIgnoreCase(student.getProgram())){
			//Commented by Somesh as now session will coming on CPS id
			//scheduledSessionList = pDao.getScheduledSessionForStudents(subjects,student);
			
			//Get common sessions
			commonscheduledSessionList = (ArrayList<SessionDayTimeBean>)pDao.getCommonSessionsSemesterBased(student.getSem(),student.getProgram(),student.getConsumerProgramStructureId());
		}else {
			//Get Common Sessions for UG
			commonscheduledSessionList = (ArrayList<SessionDayTimeBean>)pDao.getCommonSessionsSemesterBasedForUG(student.getSem(),student.getProgram(),student.getConsumerProgramStructureId());
		}
		*/
		commonscheduledSessionList = pDao.getUpcomingCommonSessionsFromCommonQuickSessions(student.getConsumerProgramStructureId(), studentRegistrationData.getYear(), 
																							studentRegistrationData.getMonth(), student.getSem());
		
		if (commonscheduledSessionList.size() > 0) {
			allScheduledSessionList.addAll(commonscheduledSessionList);
		}
		
		allScheduledSessionList.addAll(scheduledSessionList);
		
		//Added for sorting
		if(allScheduledSessionList.size() > 0) {
			Collections.sort(allScheduledSessionList, new Comparator<SessionDayTimeStudentPortal>() {
				@Override
				public int compare(SessionDayTimeStudentPortal sBean1, SessionDayTimeStudentPortal sBean2) {
					return sBean1.getDate().compareTo(sBean2.getDate());
				}
			});
		}
		
		request.getSession().setAttribute("scheduledSessionList_studentportal", allScheduledSessionList);
		request.getSession().setAttribute("registeredForEvent", registeredForEvent);
	}


	private ModelAndView checkIfPendingfeedback(HttpServletRequest request,	HttpServletResponse respnse) {
		if(!checkSession(request, respnse)){
			return new ModelAndView("jsp/login");
		} 
		PortalDao pDao = (PortalDao)act.getBean("portalDAO");
		String userId = (String)request.getSession().getAttribute("userId");
		StudentStudentPortalBean student = (StudentStudentPortalBean)request.getSession().getAttribute("student_studentportal");
		/*boolean checkIfAllSessionsHaveEnded = false;
		StudentBean studentRegistrationData = new StudentBean();
		studentRegistrationData = (StudentBean)pDao.getStudentRegistrationDataForContent(userId);

		student.setSem(studentRegistrationData.getSem());
		student.setProgram(studentRegistrationData.getProgram());
		ArrayList<String> subjectList = getSubjectsForStudent(student);
		String commaSeperatedSubjects = convertListStringToCommaSeperated(subjectList);


		//If All the sessions have ended for the particular student then only ask him for course feedback//
	    ArrayList<FacultyCourseFeedBackBean> listOfSessionsAndTheirLastDates = (ArrayList<FacultyCourseFeedBackBean>)pDao.getListOfSessionsAndTheirLastDates(commaSeperatedSubjects);	
		if(listOfSessionsAndTheirLastDates == null || listOfSessionsAndTheirLastDates.size() ==0){
			checkIfAllSessionsHaveEnded = true;
		}
		if(checkIfAllSessionsHaveEnded){
			ModelAndView modelAndView = new ModelAndView("jsp/facultyCourseFeedBack");
			FacultyCourseFeedBackBean facultyCourseFeedBack = new FacultyCourseFeedBackBean();
			facultyCourseFeedBack.setProgram(studentRegistrationData.getProgram());
			facultyCourseFeedBack.setSem(studentRegistrationData.getSem());
			modelAndView.addObject("facultyCourseFeedBack", facultyCourseFeedBack);
			return modelAndView;
		}
		 */
		
		StudentStudentPortalBean studentRegistrationData = (StudentStudentPortalBean) request.getSession().getAttribute("studentRegistrationForAcademicSession_studentportal");
		ArrayList<SessionAttendanceFeedbackStudentPortal> pendingFeedback = new ArrayList<SessionAttendanceFeedbackStudentPortal>();
		if(student.getProgram().equalsIgnoreCase("EPBM")|| student.getProgram().equalsIgnoreCase("MPDV")){
			pendingFeedback  = pDao.getPendingFeedbacksSAS(userId,student.getEnrollmentYear(),student.getEnrollmentMonth());
		}else if(studentRegistrationData != null){
			pendingFeedback = pDao.getPendingFeedbacks(userId, studentRegistrationData);
		}

		if(pendingFeedback != null && pendingFeedback.size() > 0 ){
			ModelAndView modelnView = new ModelAndView("jsp/feedback");
			modelnView.addObject("feedback", pendingFeedback.get(0));
			modelnView.addObject("hideProfileLink", "true");
			return modelnView;
		}else{
			return checkIfPendingAcadCyclefeedback(request, respnse);
			//return new ModelAndView("jsp/home");
		}
	}

	@RequestMapping(value = "/saveFeedback", method = RequestMethod.POST)
	public ModelAndView saveFeedback(HttpServletRequest request, HttpServletResponse response, @ModelAttribute SessionAttendanceFeedbackStudentPortal feedback){
		if(!checkSession(request, response)){
			return new ModelAndView("jsp/login");
		}

		String userId = (String)request.getSession().getAttribute("userId");
		feedback.setSapId(userId);
		feedback.setFeedbackGiven("Y");
		feedback.setCreatedBy(userId);
		feedback.setLastModifiedBy(userId);
		PortalDao pDao = (PortalDao)act.getBean("portalDAO");
		pDao.saveFeedback(feedback);

		setSuccess(request, "Feedback saved successfully for "+feedback.getSubject() + " - "+feedback.getSessionName());
		return checkIfPendingfeedback(request, response);
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

				//Commented by Somesh on 23-08-2021, Removed additional days portal access after validity end
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



	/**
	 * Simply selects the home view to render by returning its name.
	 */
	@RequestMapping(value = "/gotoBB", method = RequestMethod.GET)
	public ModelAndView gotoBB(HttpServletRequest request, HttpServletResponse respnse) {
		logger.info("Redirecting to BB", "");
		if(!checkSession(request, respnse)){
			return new ModelAndView("jsp/login");
		}

		ModelAndView modelnView = new ModelAndView("jsp/blackboardRedirect");

		String userId = (String)request.getSession().getAttribute("userId");
		String password = (String)request.getSession().getAttribute("password");


		modelnView.addObject("password", password );
		modelnView.addObject("userId", userId );

		return modelnView;
	}

	@RequestMapping(value = "/gotoEZProxy", method = RequestMethod.GET)
	public ModelAndView gotoEZProxy(HttpServletRequest request, HttpServletResponse respnse) {
		logger.info("Redirecting to EZProxy", "");

		if(!checkSession(request, respnse)){
			return new ModelAndView("jsp/login");
		}

		ModelAndView modelnView = new ModelAndView("jsp/ezproxyRedirect");

		String userId = (String)request.getSession().getAttribute("userId");
		String password = (String)request.getSession().getAttribute("password");
		
		if(StringUtils.isBlank(password)) {
			LDAPDao dao = (LDAPDao)act.getBean("ldapdao");
			password = dao.getUserPassword(userId);
		}
		
		modelnView.addObject("password", password );
		modelnView.addObject("userId", userId );

		return modelnView;
	}


	@RequestMapping(value = "/changePassword", method = {RequestMethod.GET, RequestMethod.POST})
	public String changePassword(HttpServletRequest request, HttpServletResponse response) {
		if(!checkSession(request, response)){
			return "jsp/login";
		}
		logger.info("Sending to change password page");
		return "jsp/changePassword";
	}


	@RequestMapping(value = "/changeUserPassword", method = {RequestMethod.GET, RequestMethod.POST})
	public String changeUserPassword(HttpServletRequest request, HttpServletResponse response) {
		if(!checkSession(request, response)){
			return "jsp/login";
		}
		logger.info("Sending to change password page");
		return "jsp/changeUserPassword";
	}
	
	@Deprecated
	// update Student Details from SFDC to Portal 
//	@RequestMapping(value = "/updateProfileFromSFDC", method = {RequestMethod.GET, RequestMethod.POST})
	public void updateProfileFromSFDC(HttpServletRequest request, HttpServletResponse response)
	{
		String userId =(String)request.getParameter("sapid");
		String emailId =(String)request.getParameter("emailId");
		String mobilePhone =(String)request.getParameter("mobileNo");
		String fatherName =(String)request.getParameter("fatherName");
		String motherName =(String)request.getParameter("motherName");
		String studentImageUrl = (String)request.getParameter("studentImage");
		String validityEndMonth = (String)request.getParameter("validityEndMonth");
		String validityEndYear = (String)request.getParameter("validityEndYear");
		String centerID = (String)request.getParameter("centerId");
		String centerName = (String)request.getParameter("centerName");
		String dob = (String)request.getParameter("dob");
		PortalDao pDao = (PortalDao)act.getBean("portalDAO");
		StudentStudentPortalBean student =pDao.getSingleStudentsData(userId);
		LDAPDao dao = (LDAPDao)act.getBean("ldapdao");
		try{
			dao.updateProfile(userId, emailId, mobilePhone, student.getAltPhone());//Update Details in LDAP//
			pDao.updateStudentContactFromSFDC(userId, emailId, dob, mobilePhone, "",student.getAltPhone(),fatherName, motherName,studentImageUrl,validityEndYear, validityEndMonth,centerID,centerName );
		}catch(Exception e)
		{
//			e.printStackTrace();
		}
	}
	//Program Suspension status update call out from SFDC//.
	@RequestMapping(value="/updateStudentPortalProgramStatus",method={RequestMethod.GET,RequestMethod.POST})
	public void updateStudentPortalProgramStatus(HttpServletRequest request, HttpServletResponse response){

		String sapid = (String)request.getParameter("sapid");
		PortalDao pDao = (PortalDao)act.getBean("portalDAO");
		if(!"".equals(sapid) || sapid != null){
			pDao.updateProgramStatus(sapid);
		}
	}

	@RequestMapping(value = "/student/updateProfile", method = {RequestMethod.GET})
	/*old code
	 * public String updateProfile(HttpServletRequest request, HttpServletResponse response,Model m) {
		if(!checkSession(request, response)){
			return "login";
		}


		  logger.info("Sending to update profile page");
		String userId = (String)request.getSession().getAttribute("userId");
		PortalDao pDao = (PortalDao)act.getBean("portalDAO");
		StudentBean student = pDao.getSingleStudentsData(userId);
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
				m.addAttribute("showShippingAddress", "Yes");
				//HashMap<String,String> mapOfShippingAddress = salesforceHelper.getShippingAddressOfStudent((String)request.getSession().getAttribute("userId")); 
				//m.addAttribute("mapOfShippingAddress",mapOfShippingAddress);
			}else{
				m.addAttribute("showShippingAddress", "No");
			}
		}catch(Exception e){
			e.printStackTrace();
		}
		m.addAttribute("student",student);

		//Done in this manner since the page does not send values using form bind of spring//
		if(student.getIndustry()!=null && !"".equals(student.getIndustry())){
			m.addAttribute("industryList", industryList);
		}else{
			m.addAttribute("industryList", industryList);
		}

		if(student.getDesignation()!=null && !"".equals(student.getDesignation())){
			m.addAttribute("designationList",designationList);
		}else{
			m.addAttribute("designationList", designationList);
		}


		return "confirmStudentDetails";*/

	public ModelAndView updateProfile(HttpServletRequest request, HttpServletResponse response) {
		if(!checkSession(request, response)){
			return new ModelAndView("jsp/login");
		}
		ModelAndView modelnView = new ModelAndView("jsp/confirmStudentDetails");
		try{
			PortalDao pDao = (PortalDao)act.getBean("portalDAO");
			
			String userId = (String)request.getSession().getAttribute("userId");
			StudentStudentPortalBean student = pDao.getSingleStudentsData(userId);
			student.setAge(calculateAge(student));
			
			String showShippingAddress = "";
			request.getSession().setAttribute("showShippingAddress",showShippingAddress);
			
			modelnView = updateInformation(request, student, modelnView, true);
			
			//code moved to updateInformation() method  ~Raynal
//			Boolean userInSalesforce =false;
//			String enrollmentYearAndMonth = student.getEnrollmentMonth()+","+student.getEnrollmentYear();
//			SimpleDateFormat month_yearFormat = new SimpleDateFormat("MMM,yyyy");
//			SimpleDateFormat fulldateFormat = new SimpleDateFormat("dd-MM-yyyy");
			//set age based on the year and month 
			
			// format year and Month into date 
//			Date dateR = month_yearFormat.parse(enrollmentYearAndMonth);
//			String fullFormatedDate = fulldateFormat.format(dateR);
//
//			Date enrollmentDate = fulldateFormat.parse(fullFormatedDate);
//			Date salesforceUseStartDate = fulldateFormat.parse("01-06-2014"); // byPass Date for Student
			
//			if(enrollmentDate.after(salesforceUseStartDate))
//			{
//				userInSalesforce=true;
//				modelnView.addObject("showShippingAddress", "Yes");
//			}else{
//				modelnView.addObject("showShippingAddress", "No");
//			}
//			boolean fromProfileIcon=true;
//			request.getSession().setAttribute("userInSalesforce",userInSalesforce);
//			request.getSession().setAttribute("fromProfileIcon",fromProfileIcon);
//			modelnView.addObject("student", student );
//			modelnView.addObject("industryList", industryList);
//			modelnView.addObject("designationList", designationList);
//			modelnView.addObject("fromProfileIcon", fromProfileIcon);

		}catch(Exception e){
//			e.printStackTrace();
		}
		return modelnView;
	}
	
	/**
	 * Adds the required attributes to the ModelView
	 * @param request - Session Request
	 * @param student - StudentBean containing the student data
	 * @param modelnView - a View Object 
	 * @param redirectedFromProfileIcon	- boolean value which indicates if the student has been redirected from the updateProfileIcon or not
	 * @return	a ModelAndView object with the specified attributes
	 */
	private ModelAndView updateInformation(HttpServletRequest request, StudentStudentPortalBean student, ModelAndView modelnView, boolean redirectedFromProfileIcon) {
		try{
			Boolean userInSalesforce = false;
			String enrollmentYearAndMonth = student.getEnrollmentMonth()+","+student.getEnrollmentYear();
			SimpleDateFormat month_yearFormat = new SimpleDateFormat("MMM,yyyy");
			SimpleDateFormat fulldateFormat = new SimpleDateFormat("dd-MM-yyyy");

			// format year and Month into date 
			Date dateR = month_yearFormat.parse(enrollmentYearAndMonth);
			String fullFormatedDate = fulldateFormat.format(dateR);

			Date enrollmentDate = fulldateFormat.parse(fullFormatedDate);
			Date salesforceUseStartDate = fulldateFormat.parse("01-06-2014"); // byPass Date for Student
			if(enrollmentDate.after(salesforceUseStartDate))
			{
				userInSalesforce=true;
				modelnView.addObject("showShippingAddress", "Yes");
			}else{
				modelnView.addObject("showShippingAddress", "No");
			}
			request.getSession().setAttribute("userInSalesforce",userInSalesforce);
		}catch(Exception e){
//			e.printStackTrace();
		} 
		request.getSession().setAttribute("fromProfileIcon",redirectedFromProfileIcon);
		modelnView.addObject("student", student );
		modelnView.addObject("industryList", industryList);
		modelnView.addObject("designationList", designationList);
		modelnView.addObject("hideProfileLink", "true");
		modelnView.addObject("fromProfileIcon", redirectedFromProfileIcon);
		return modelnView;
	}
	

	@RequestMapping(value = "/updateFirstTimeProfile", method = {RequestMethod.GET, RequestMethod.POST})
	public String updateFirstTimeProfile(HttpServletRequest request, HttpServletResponse response) {
		if(!checkSession(request, response)){
			return "jsp/login";
		}
		logger.info("Sending to update profile page first time");
		request.setAttribute("hideProfileLink", "true");
		return "jsp/updateFirstTimeProfile";
	}


	@RequestMapping(value = "/saveFirstTimeProfile", method = {RequestMethod.GET, RequestMethod.POST})
	public ModelAndView saveFirstTimeProfile(HttpServletRequest request, HttpServletResponse respnse) {
		logger.info("", "");

		if(!checkSession(request, respnse)){
			return new ModelAndView("jsp/login");
		}

		String password = request.getParameter("password");
		String email = request.getParameter("email");
		String mobile = request.getParameter("mobile");
		String altMobile = request.getParameter("altMobile");
		String postalAddress = request.getParameter("address");
		ModelAndView modelnView = null;

		PersonStudentPortalBean person = (PersonStudentPortalBean)request.getSession().getAttribute("user_studentportal");
		person.setEmail(email);
		person.setPostalAddress(postalAddress);
		person.setContactNo(mobile);
		person.setAltContactNo(altMobile);

		request.getSession().setAttribute("user_studentportal", person);

		String userId = (String)request.getSession().getAttribute("userId");

		LDAPDao dao = (LDAPDao)act.getBean("ldapdao");
		try{
			dao.updateFirstTimeProfile(password, userId, email, mobile, altMobile, postalAddress);

			person.setIdentityConfirmed("Confirmed");
			request.getSession().setAttribute("user_studentportal", person);

			modelnView = new ModelAndView("jsp/home");
			request.setAttribute("success","true");
			request.setAttribute("successMessage","Password changed and profile updated successfully.");
			request.getSession().setAttribute("password",password);

		}catch(Exception e){
			modelnView = new ModelAndView("jsp/updateFirstTimeProfile");
			request.setAttribute("error", "true");
			request.setAttribute("errorMessage", "New password does not meet password policy OR is similar to past/default password. Please set password as per policy.");

		}

		return modelnView;
	}

	/*
	 * Commented by @Raynal as some students were randomly hitting this mapping which would lead to their parentNames being updated to null, 
	 * since this method does not require any parameters, there were no errors thrown, 
	 * and the method would run successfully (setting parentNames to null) and redirect the student to the home page (/home method). 
	 * Unable to find the source from which students were redirected to this mapping.
	 */
	@Deprecated
//	@RequestMapping(value = "/updateParentName", method = {RequestMethod.GET, RequestMethod.POST})
	public ModelAndView updateParentName(HttpServletRequest request, HttpServletResponse respnse) {
		logger.info("", "");

		if(!checkSession(request, respnse)){
			return new ModelAndView("jsp/login");
		}

		String fatherName = request.getParameter("fatherName");
		String motherName = request.getParameter("motherName");

		ModelAndView modelnView = null;

		String userId = (String)request.getSession().getAttribute("userId");

		PortalDao pDao = (PortalDao)act.getBean("portalDAO");
		try{
//			pDao.updateStudentInfo(userId, fatherName, motherName);				//Commenting the DAO call to prevent db updation, as this method is deprecated

			modelnView = new ModelAndView("jsp/home");
			request.setAttribute("success","true");
			request.setAttribute("successMessage","Information updated successfully.");


		}catch(Exception e){
			modelnView = new ModelAndView("jsp/home");
			request.setAttribute("error", "true");
			request.setAttribute("errorMessage", "Error in updating information.");

		}

		return modelnView;
	}
	//For updating address only in portal since the student does not exist in sfdc
	/*commented by steffi
	 * @RequestMapping(value="/saveProfileForPortal",method={RequestMethod.POST})
	public ModelAndView saveProfileForPortal(HttpServletRequest request, HttpServletResponse respnse) {
		logger.info("", "");
		if(!checkSession(request, respnse)){
			return new ModelAndView("jsp/login");
		}
		ModelAndView modelnView = null;
		LDAPDao dao = (LDAPDao)act.getBean("ldapdao");
		PortalDao pDao = (PortalDao)act.getBean("portalDAO");
		MailSender mailer = (MailSender)act.getBean("mailer");
		String email = request.getParameter("email");
		String mobile = request.getParameter("mobile");
		String altMobile = request.getParameter("altMobile");
		String postalAddress = request.getParameter("studentAddress");
		String fatherName = request.getParameter("fatherName");
		String motherName = request.getParameter("motherName");
		String industry = request.getParameter("industry");
		String designation = request.getParameter("designation");


		String userId=(String)request.getSession().getAttribute("userId");
		StudentBean student = pDao.getSingleStudentsData(userId);

		try{
			dao.updateProfile(userId, email, mobile, altMobile);//Update Details in LDAP//
			pDao.updateStudentContact(userId, email,mobile,postalAddress,altMobile,fatherName, motherName,industry,designation,mailer);// Update Details in exam.student Table //

			request.getSession().setAttribute("student_studentportal", student);
			modelnView = new ModelAndView("jsp/home");
			request.setAttribute("success","true");
			request.setAttribute("successMessage","Profile updated successfully.");
			return modelnView;
		}catch(Exception e){
			request.getSession().setAttribute("student_studentportal", student);
			modelnView = new ModelAndView("jsp/home");
			request.setAttribute("error","true");
			request.setAttribute("errorMessage","Problem Updating profile. Please try again");
			modelnView = new ModelAndView("jsp/home");
			mailer.mailStackTrace("Problem Updating profile.", e);
			return modelnView;
		}


	}*/
	/*commented by steffi
	 * 
	 * //For updating profile in sfdc and portal//
	@RequestMapping(value = "/saveProfileForSFDCAndPortal", method = {RequestMethod.POST})
	public ModelAndView saveProfileForSFDCAndPortal(HttpServletRequest request, HttpServletResponse respnse) {
		logger.info("", "");
		if(!checkSession(request, respnse)){
			return new ModelAndView("jsp/login");
		}

		String email = request.getParameter("email");
		String mobile = request.getParameter("mobile");
		String altMobile = request.getParameter("altMobile");
		//String postalAddress = request.getParameter("studentAddress");
		//Get Fathers Name and Mothers Name//
		String fatherName = request.getParameter("fatherName");
		String motherName = request.getParameter("motherName");
		String industry = request.getParameter("industry");
		String designation = request.getParameter("designation");
		//Shipping Address Fields from update profile page//
		String shippingStreet = request.getParameter("shippingStreet");
		String shippingCity = request.getParameter("shippingCity");
		String shippingState = request.getParameter("shippingState");
		String shippingPostalCode = request.getParameter("shippingPostalCode");
		String shippingCountry = request.getParameter("shippingCountry");
		String shippingLocalityName = request.getParameter("shippingLocalityName");
		String shippingNearestLandmark = request.getParameter("shippingNearestLandmark");
		String shippingHouseName = request.getParameter("shippingHouseName");


		String postalAddress =  shippingHouseName + ", " + shippingLocalityName+","+shippingStreet+",near "+shippingNearestLandmark+","
				+shippingPostalCode+","+shippingCity;
		//end//
		ModelAndView modelnView = null;

		StudentBean student = (StudentBean)request.getSession().getAttribute("student_studentportal");
		student.setEmailId(email);
		student.setMobile(mobile);
		request.getSession().setAttribute("student_studentportal", student);

		Person person = (Person)request.getSession().getAttribute("user_studentportal");
		person.setEmail(email);
		person.setPostalAddress(postalAddress);
		person.setContactNo(mobile);
		person.setAltContactNo(altMobile);
		request.getSession().setAttribute("user_studentportal", person);

		String userId = (String)request.getSession().getAttribute("userId");

		LDAPDao dao = (LDAPDao)act.getBean("ldapdao");
		PortalDao pDao = (PortalDao)act.getBean("portalDAO");
		StudentBean student = pDao.getSingleStudentsData(userId);

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
			if(enrollmentDate.after(salesforceUseStartDate))
			{
				if("PROD".equalsIgnoreCase(ENVIRONMENT)){

				errorMessage = salesforceHelper.updateSalesforceProfile(userId,email,mobile,fatherName,motherName,shippingStreet,shippingCity,shippingState,
						shippingPostalCode,shippingCountry,shippingLocalityName,
						shippingNearestLandmark,shippingHouseName,altMobile);//This is to update Students Shipping Address in SFDC//
				}
			}

			if(errorMessage == null || "".equals(errorMessage)) 
			{
				dao.updateProfile(userId, email, mobile, altMobile);//Update Details in LDAP//
				//Used overloaded updateStudentContact to have more fields of address
				pDao.updateStudentContact(userId, email, mobile, postalAddress,altMobile,
										  fatherName, motherName,industry,designation,mailer,
										  shippingHouseName,shippingStreet,shippingLocalityName,
										  shippingNearestLandmark,shippingCity,shippingState,
										  shippingCountry,shippingPostalCode
										);// Update Details in exam.student Table //
				request.getSession().setAttribute("student_studentportal", student);
				modelnView = new ModelAndView("jsp/home");
				request.setAttribute("success","true");
				request.setAttribute("successMessage","Profile updated successfully.");
			}else
			{
				modelnView = new ModelAndView("jsp/home");
				request.setAttribute("error","true");
				request.setAttribute("errorMessage",errorMessage);
				pDao.updateErrorFlag(userId,errorMessage,mailer);
			}
		}catch(Exception e){
			modelnView = new ModelAndView("jsp/updateProfile");
			request.setAttribute("error", "true");
			request.setAttribute("errorMessage", "Error in updating profile.");
			pDao.updateErrorFlag(userId,"Error in updating profile.",mailer);
		}

		return modelnView;
	}*/

	@RequestMapping(value = "/savePassword", method = {RequestMethod.GET, RequestMethod.POST})
	public ModelAndView savePassword(HttpServletRequest request, HttpServletResponse respnse) {
		logger.info("", "");

		if(!checkSession(request, respnse)){
			return new ModelAndView("jsp/login");
		}

		String password = request.getParameter("password");
		String confirmPassword = request.getParameter("confirm_password");
		
		ModelAndView modelnView = null;

		String userId = (String)request.getSession().getAttribute("userId");

		LDAPDao dao = (LDAPDao)act.getBean("ldapdao");
		try{
			homeService.validateStudentPassword(userId, password, confirmPassword);			//validate the password entered by the student
			dao.changePassword(password, userId);
			modelnView = new ModelAndView("jsp/home");
			request.setAttribute("success","true");
			request.setAttribute("successMessage","Password changed successfully.");
			request.getSession().setAttribute("password",password);
		}
		catch(IllegalArgumentException ex) {
//			ex.printStackTrace();
			modelnView = new ModelAndView("jsp/changePassword");
			request.setAttribute("error", "true");
			request.setAttribute("errorMessage", ex.getMessage());
		}
		catch(Exception e){
			modelnView = new ModelAndView("jsp/changePassword");
			request.setAttribute("error", "true");
			request.setAttribute("errorMessage", "New password does not meet password policy OR is similar to past/default password. Please set password as per policy.");
		}

		return modelnView;
	}


	@RequestMapping(value = "/saveUserPassword", method = {RequestMethod.GET, RequestMethod.POST})
	public ModelAndView saveUserPassword(HttpServletRequest request, HttpServletResponse respnse) {
		logger.info("", "");

		if(!checkSession(request, respnse)){
			return new ModelAndView("jsp/login");
		}

		String password = request.getParameter("password");
		String confirmPassword = request.getParameter("confirm_password");
		String userId = request.getParameter("userId");
		ModelAndView modelnView = null;


		LDAPDao dao = (LDAPDao)act.getBean("ldapdao");
		try{
			homeService.validateStudentPassword(userId, password, confirmPassword);			//validate the password entered by the student
			dao.changePassword(password, userId);
			modelnView = new ModelAndView("jsp/changeUserPassword");
			request.setAttribute("success","true");
			request.setAttribute("successMessage","Password changed successfully.");
			request.getSession().setAttribute("password",password);

		}catch(NameNotFoundException e){
			modelnView = new ModelAndView("jsp/changeUserPassword");
			request.setAttribute("error", "true");
			request.setAttribute("errorMessage", "User does not exist.");

		}
		catch(IllegalArgumentException ex) {
//			ex.printStackTrace();
			modelnView = new ModelAndView("jsp/changeUserPassword");
			request.setAttribute("error", "true");
			request.setAttribute("errorMessage", ex.getMessage());
		}
		catch(Exception e){
			modelnView = new ModelAndView("jsp/changeUserPassword");
			request.setAttribute("error", "true");
			request.setAttribute("errorMessage", "New password does not meet password policy OR is similar to past/default password. Please set password as per policy.");
		}

		return modelnView;
	}

	@RequestMapping(value = "/saveUserRoles", method = {RequestMethod.GET, RequestMethod.POST})
	public ModelAndView saveUserRoles(HttpServletRequest request, HttpServletResponse respnse) {
		logger.info("", "");

		if(!checkSession(request, respnse)){
			return new ModelAndView("jsp/login");
		}

		String roles = request.getParameter("roles");
		String userId = request.getParameter("userId");
		ModelAndView modelnView = null;


		LDAPDao dao = (LDAPDao)act.getBean("ldapdao");
		try{
			dao.changeRoles(roles, userId);
			modelnView = new ModelAndView("jsp/changeUserPassword");
			request.setAttribute("success","true");
			request.setAttribute("successMessage","Roles changed successfully.");


		}catch(Exception e){
			modelnView = new ModelAndView("jsp/changeUserPassword");
			request.setAttribute("error", "true");
			request.setAttribute("errorMessage", "Error in changing roles.");

		}

		return modelnView;
	}

	@RequestMapping(value = "/admin/createUserForm", method = {RequestMethod.GET, RequestMethod.POST})
	public String createUserForm(HttpServletRequest request, HttpServletResponse respnse, Model m) {
		logger.info("Upload Writtn Marks page");

		FileStudentPortalBean fileBean = new FileStudentPortalBean();
		m.addAttribute("fileBean",fileBean);

		return "jsp/createUsers";
	}

	@RequestMapping(value = "/admin/createUsers", method = {RequestMethod.GET, RequestMethod.POST})
	public ModelAndView createUsers(FileStudentPortalBean fileBean, BindingResult result,HttpServletRequest request, Model m){
		if(!checkSession(request, null)){
			return new ModelAndView("jsp/login");
		}

		ModelAndView modelnView = new ModelAndView("jsp/createUsers");
		try{
			//String userId = (String)request.getSession().getAttribute("userId");
			ExcelHelper excelHelper = new ExcelHelper();
			ArrayList<List> resultList = excelHelper.readUsersExcel(fileBean);
			//List<StudentMarksBean> marksBeanList = excelHelper.readMarksExcel(fileBean, programList, subjectList);

			List<PersonStudentPortalBean> studentsList = (ArrayList<PersonStudentPortalBean>)resultList.get(0);
			List<PersonStudentPortalBean> errorBeanList = (ArrayList<PersonStudentPortalBean>)resultList.get(1);

			fileBean = new FileStudentPortalBean();
			m.addAttribute("fileBean",fileBean);

			if(errorBeanList.size() > 0){
				request.setAttribute("errorBeanList", errorBeanList);
				return modelnView;
			}

			LDAPDao dao = (LDAPDao)act.getBean("ldapdao");

			errorBeanList = new ArrayList<PersonStudentPortalBean>();
			for (int i = 0; i < studentsList.size(); i++) {
				PersonStudentPortalBean p = studentsList.get(i);
				try {
					dao.createUser(p);
					if(p.isErrorRecord()){
						errorBeanList.add(p);
					}
				} catch (Exception e) {
					// TODO: handle exception
				}
			}

			if(errorBeanList.size() == 0){
				request.setAttribute("success","true");
				request.setAttribute("successMessage",studentsList.size() +" students out of "+ studentsList.size()+" created successfully.");
			}else{
				request.setAttribute("errorBeanList", errorBeanList);
				request.setAttribute("error", "true");
				request.setAttribute("errorMessage", errorBeanList.size() + " records were NOT inserted. ");
				//request.setAttribute("errorMessage", "Error in inserting marks records at row "+(lastRowUpdated+1)+" for SAPID:"+bean.getSapid()+" "+bean.getSubject()+". All rows before are inserted successfully.");
			}

		}catch(Exception e){
//			e.printStackTrace();
			request.setAttribute("error", "true");
			request.setAttribute("errorMessage", "Error in Creating users.");

		}

		return modelnView;
	}

	@RequestMapping(value = "/updateUserForm", method = {RequestMethod.GET, RequestMethod.POST})
	public String updateUserForm(HttpServletRequest request, HttpServletResponse respnse, Model m) {
		logger.info("Upload Writtn Marks page");

		FileStudentPortalBean fileBean = new FileStudentPortalBean();
		m.addAttribute("fileBean",fileBean);

		return "jsp/updateUsers";
	}

	@RequestMapping(value = "/updateUsers", method = {RequestMethod.GET, RequestMethod.POST})
	public ModelAndView updateUsers(FileStudentPortalBean fileBean, BindingResult result,HttpServletRequest request, Model m){
		if(!checkSession(request, null)){
			return new ModelAndView("jsp/login");
		}

		ModelAndView modelnView = new ModelAndView("jsp/updateUsers");
		try{
			//String userId = (String)request.getSession().getAttribute("userId");
			ExcelHelper excelHelper = new ExcelHelper();
			ArrayList<List> resultList = excelHelper.readUsersExcel(fileBean);
			//List<StudentMarksBean> marksBeanList = excelHelper.readMarksExcel(fileBean, programList, subjectList);

			List<PersonStudentPortalBean> studentsList = (ArrayList<PersonStudentPortalBean>)resultList.get(0);
			List<PersonStudentPortalBean> errorBeanList = (ArrayList<PersonStudentPortalBean>)resultList.get(1);

			fileBean = new FileStudentPortalBean();
			m.addAttribute("fileBean",fileBean);

			if(errorBeanList.size() > 0){
				request.setAttribute("errorBeanList", errorBeanList);
				return modelnView;
			}

			LDAPDao dao = (LDAPDao)act.getBean("ldapdao");

			errorBeanList = new ArrayList<PersonStudentPortalBean>();
			for (int i = 0; i < studentsList.size(); i++) {
				PersonStudentPortalBean p = studentsList.get(i);
				try {
					dao.updateAttributes(p.getUserId());
					if(p.isErrorRecord()){
						errorBeanList.add(p);
					}
				} catch (Exception e) {
					// TODO: handle exception
				}
			}

			if(errorBeanList.size() == 0){
				request.setAttribute("success","true");
				request.setAttribute("successMessage",studentsList.size() +" students out of "+ studentsList.size()+" updated successfully.");
			}else{
				request.setAttribute("errorBeanList", errorBeanList);
				request.setAttribute("error", "true");
				request.setAttribute("errorMessage", errorBeanList.size() + " records were NOT inserted. ");
				//request.setAttribute("errorMessage", "Error in inserting marks records at row "+(lastRowUpdated+1)+" for SAPID:"+bean.getSapid()+" "+bean.getSubject()+". All rows before are inserted successfully.");
			}

		}catch(Exception e){
//			e.printStackTrace();
			request.setAttribute("error", "true");
			request.setAttribute("errorMessage", "Error in Creating users.");

		}

		return modelnView;
	}

	@RequestMapping(value = "/admin/createSingleUserForm", method = {RequestMethod.GET, RequestMethod.POST})
	public String createSingleUserForm(HttpServletRequest request, HttpServletResponse respnse, Model m) {
		if(!checkSession(request, respnse)){
			return "jsp/login";
		}

		PersonStudentPortalBean p = new PersonStudentPortalBean();
		m.addAttribute("person",p);
		request.getSession().setAttribute("person", p);

		return "jsp/createSingleUser";
	}

	@RequestMapping(value = "/admin/createSingleUser", method = RequestMethod.POST)
	public ModelAndView createSingleUser(HttpServletRequest request, HttpServletResponse response, @ModelAttribute PersonStudentPortalBean p){
		if(!checkSession(request, response)){
			return new ModelAndView("jsp/login");
		}
		ModelAndView modelnView = new ModelAndView("jsp/createSingleUser");
		/*if(p.getEmail() == null || "".equals(p.getEmail().trim())){
			p.setEmail("Not Available");
		}
		if(p.getProgram() == null || "".equals(p.getProgram().trim())){
			p.setProgram("Not Available");
		}
		if(p.getContactNo() == null || "".equals(p.getContactNo().trim())){
			p.setContactNo("Not Available");
		}
		if(p.getAltContactNo() == null || "".equals(p.getAltContactNo().trim())){
			p.setAltContactNo("Not Available");
		}
		if(p.getPostalAddress() == null || "".equals(p.getPostalAddress().trim())){
			p.setPostalAddress("Not Available");
		}*/


		LDAPDao dao = (LDAPDao)act.getBean("ldapdao");
		dao.createUser(p);
		request.getSession().setAttribute("person", p);
		modelnView.addObject("person", p);
		if(p.isErrorRecord()){
			request.setAttribute("error", "true");
			request.setAttribute("errorMessage", p.getErrorMessage());
		}else{
			request.setAttribute("success","true");
			request.setAttribute("successMessage","User created successfully.");
		}
		return modelnView;
	}

	@Deprecated				//Mapping not in use
	@RequestMapping(value = "/sendEmail", method = {RequestMethod.GET, RequestMethod.POST})
	public String sendEmail(HttpServletRequest request, HttpServletResponse response) {
		PersonStudentPortalBean p = new PersonStudentPortalBean();
		p.setEmail("sanketpanaskar@gmail.com");
		p.setPassword("pass@1234");
		MailSender mailer = (MailSender)act.getBean("mailer");
//		mailer.sendPasswordEmail(p.getDisplayName(), p.getEmail(), p.getPassword());
		request.setAttribute("success","true");
		request.setAttribute("successMessage","Your password is emailed to your registered email id: "+p.getEmail());
		return "jsp/resetPassword";
	}

	@Deprecated
	@RequestMapping(value = "/resetPasswordForm", method = {RequestMethod.GET, RequestMethod.POST})
	public String resetPasswordForm(HttpServletRequest request, HttpServletResponse response) {

		logger.info("Sending to reset password page");
		return "jsp/resetPassword";
	}

	@RequestMapping(value = "/down", method = {RequestMethod.GET, RequestMethod.POST})
	public String down(HttpServletRequest request, HttpServletResponse response) {

		logger.info("Sending to reset password page");
		return "jsp/down";
	}


	@Deprecated
	@RequestMapping(value = "/resetPassword", method = {RequestMethod.GET, RequestMethod.POST})
	public String resetPassword(HttpServletRequest request, HttpServletResponse response) {


		String userId = request.getParameter("userId");
		LDAPDao dao = (LDAPDao)act.getBean("ldapdao");
		try{
			String email = "";
			PersonStudentPortalBean person = dao.findPerson(userId);


			if(person != null){
				if(userId.startsWith("77") || userId.startsWith("79")){
					PortalDao pDao = (PortalDao)act.getBean("portalDAO");
					StudentStudentPortalBean student = pDao.getSingleStudentsData(userId);
					email = student.getEmailId();
				}else{
					email = person.getEmail();
				}

				if(email != null && email.indexOf("@") != -1){
					MailSender mailer = (MailSender)act.getBean("mailer");
					try{
						mailer.sendPasswordEmail(person.getDisplayName(), email, person.getPassword());
						request.setAttribute("success","true");
						request.setAttribute("successMessage","Your password is emailed to your registered email id: "+email);
					}catch(Exception e){
//						e.printStackTrace();
						request.setAttribute("error", "true");
						request.setAttribute("errorMessage", "Error in resetting password. Please contact ngasce@nmims.edu to reset your password");
					}
				}else{
					request.setAttribute("error", "true");
					request.setAttribute("errorMessage", "No registered mail id exists with us. Please send email to ngasce@nmims.edu to reset your password.");
				}
			}else{
				request.setAttribute("error", "true");
				request.setAttribute("errorMessage", "User ID does not exist. Password cannot be reset.");
			}

		}catch(NameNotFoundException e){
//			e.printStackTrace();
			request.setAttribute("error", "true");
			request.setAttribute("errorMessage", "User ID does not exist. Password cannot be reset.");
		}catch(Exception e){
//			e.printStackTrace();
		}
		
		return "redirect:" + SERVER_PATH + "forgotPasswordForm";
//		return "resetPassword";
	}


	@RequestMapping(value = "/student/getAllAnnouncementDetails", method = {RequestMethod.GET, RequestMethod.POST})
	public ModelAndView getAllAnnouncementDetails(HttpServletRequest request, HttpServletResponse response) {

		if(!checkSession(request, response)){
			return new ModelAndView("jsp/login");
		}
		ModelAndView modelnView = new ModelAndView("jsp/announcementsDetails");
		/*PortalDao pDao = (PortalDao)act.getBean("portalDAO");
		List<AnnouncementBean> announcements = pDao.getAllActiveAnnouncements();
		List<AnnouncementBean> jobAnnouncements = pDao.getAllNewJobAnnouncements();
		if(jobAnnouncements != null && jobAnnouncements.size() > 0){
			announcements.addAll(jobAnnouncements);
		}
		request.getSession().setAttribute("announcements", announcements);*/
		List<AnnouncementStudentPortalBean> announcements = (ArrayList<AnnouncementStudentPortalBean>)request.getAttribute("announcementsPortal");
		int announcementSize = announcements != null ? announcements.size() : 0;
		modelnView.addObject("announcements", announcements);
		modelnView.addObject("announcementSize", announcementSize);
		return modelnView;
	}

	@RequestMapping(value = "/student/getAllAnnouncementDetailsForLogin", method = {RequestMethod.GET, RequestMethod.POST})
	public ModelAndView getAllAnnouncementDetailsForLogin(HttpServletRequest request, HttpServletResponse response) {


		ModelAndView modelnView = new ModelAndView("jsp/announcementsDetailsInLogin");
		PortalDao pDao = (PortalDao)act.getBean("portalDAO");

		/*List<AnnouncementBean> announcements = pDao.getAllActiveAnnouncements();
		List<AnnouncementBean> jobAnnouncements = pDao.getAllNewJobAnnouncements();
		if(jobAnnouncements != null && jobAnnouncements.size() > 0){
			announcements.addAll(jobAnnouncements);
		}
		request.getSession().setAttribute("announcements", announcements);*/
		//List<AnnouncementBean> announcements = pDao.getAllActiveAnnouncements();
		//Added for Sas-->
		StudentStudentPortalBean student = (StudentStudentPortalBean)request.getSession().getAttribute("student_studentportal");
		List<AnnouncementStudentPortalBean> announcements = null;
		if(student != null){
			announcements = pDao.getAllActiveAnnouncements(student.getProgram(),student.getPrgmStructApplicable());
		}else{
			announcements = pDao.getAllActiveAnnouncements();
		}

		int announcementSize = announcements != null ? announcements.size() : 0;
		modelnView.addObject("announcements", announcements);
		modelnView.addObject("announcementSize", announcementSize);
		return modelnView;
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
	private ArrayList<String> getPassSubjects(StudentStudentPortalBean student, PortalDao dao) {
		ArrayList<String> passSubjectList = dao.getPassSubjectsNamesForAStudent(student.getSapid());
		return passSubjectList;
	}	
	private ArrayList<AssignmentStudentPortalFileBean> getFailSubjects(StudentStudentPortalBean student, PortalDao dao) {
		ArrayList<AssignmentStudentPortalFileBean> failSubjectList = dao.getFailSubjectsForAStudent(student.getSapid());
		return failSubjectList;
	}

	private ArrayList<AssignmentStudentPortalFileBean> getANSNotProcessed(StudentStudentPortalBean student, PortalDao dao) {
		ArrayList<AssignmentStudentPortalFileBean> failSubjectList = dao.getANSNotProcessed(student.getSapid());
		return failSubjectList;
	}

	/*@RequestMapping(value = "/viewCourseHomePage", method = {RequestMethod.GET, RequestMethod.POST})
	public ModelAndView viewCourseHomePage(HttpServletRequest request, HttpServletResponse response,Model m) {

		if(!checkSession(request, response)){
			return new ModelAndView("jsp/login");
		}
		ModelAndView modelnView = new ModelAndView("jsp/courseHome/home");
		String subject = request.getParameter("subject");
		if(subject == null){
			ArrayList<String> studentCourses = (ArrayList<String>)request.getSession().getAttribute("studentCourses_studentportal");

			if(studentCourses != null && studentCourses.size() > 0){
				subject = studentCourses.get(0).trim();
			}else{
				modelnView.addObject("subject", "");
				setError(request, "No Active Course Available for you");
				return modelnView;
			}

		}else{
			subject = subject.trim();
		}


		modelnView.addObject("subject", subject);

		generateCourseSessionsMap(request);

		generateCourseAssignmentsMap(request);

		generateCourseLearningResourcesMap(request, subject, modelnView);

		generateCourseResultsMap(request, subject);

		getCourseQueriesMap(request, subject);

		getForumBasedOnSubjects(request,subject);

		//Attended/Pending/Scheduled sessions 23rd Nov
		  getAllPendingSessions(request,subject);
		getAllScheduledSessions(request, subject);
		getAllAttendedSessions(request,subject);
		getAllConductedSessions(request,subject);

		getSingleStudentAttendanceforSubject(request,subject);

		//added 6mar by PS
		getVideoContentForSubject(request,subject);

		return modelnView;
	}*/

	@RequestMapping(value = "/student/viewCourseHomePage", method = {RequestMethod.GET, RequestMethod.POST})
	public ModelAndView viewCourseHomePage(HttpServletRequest request, HttpServletResponse response,Model m) {


		if(!checkSession(request, response)){
			return new ModelAndView("jsp/login");
		}
		HashMap<String, String> programSemSubjectIdWithSubject = new HashMap<String,String>();

		/*
		 * Old view commented out by Harsh on 2020/09/03
		 * Added new view for my courses
		 * ModelAndView modelnView = new ModelAndView("jsp/courseHome/subjectList");  
		 */
		String cycledata=request.getParameter("cycle");
		ModelAndView modelnView = new ModelAndView("jsp/courseHome/MyCoursesDemo");  
		try {
	
		//String subject = request.getParameter("subject");
		if (StringUtils.isBlank(cycledata)) {
			
			programSemSubjectIdWithSubject = (HashMap<String, String>) request.getSession()
			.getAttribute("programSemSubjectIdWithSubjects_studentportal");
			
		}else if (cycledata.equals("ongoing")) { 
			
			programSemSubjectIdWithSubject = (HashMap<String, String>) request.getSession()
					.getAttribute("programSemSubjectIdWithSubjectForCurrentsem");
			
		} else if (cycledata.equals("backlog")) {
			
			programSemSubjectIdWithSubject = (HashMap<String, String>) request.getSession()
			.getAttribute("programSemSubjectIdWithSubjectForBacklog");

		} else {
			
			programSemSubjectIdWithSubject = (HashMap<String, String>) request.getSession()
			.getAttribute("programSemSubjectIdWithSubjects_studentportal");
		
		}
		

		
		}catch(Exception e)
		{
			//e.printStackTrace();
			programSemSubjectIdWithSubject = (HashMap<String, String>) request.getSession()
					.getAttribute("programSemSubjectIdWithSubjects_studentportal");
			
			
		}
		
		if(programSemSubjectIdWithSubject.size() == 0)
		{
			setError(request, "No Active Course Available for you");	
		}
		
		
		
		/*if(subject == null){
			ArrayList<String> studentCourses = (ArrayList<String>)request.getSession().getAttribute("studentCourses_studentportal");
			
			if(studentCourses != null && studentCourses.size() > 0){
				subject = studentCourses.get(0).trim();
			}else{
				modelnView.addObject("subject", "");
				setError(request, "No Active Course Available for you");
				return modelnView;
			}
		}*/
		//subject = subject.trim();
		//modelnView.addObject("subject", subject);
		modelnView.addObject("cycledata",cycledata);
		request.getSession().setAttribute("programSemSubjectIdWithSubject", programSemSubjectIdWithSubject);
		return modelnView;
	}

	//added for new learning module

	@RequestMapping(value = "/student/viewCourseDetails", method = {RequestMethod.GET, RequestMethod.POST})
	public ModelAndView viewCourseHomePageX(HttpServletRequest request, HttpServletResponse response,Model m) {

	    if(!checkSession(request, response)){
	        return new ModelAndView("jsp/login");
	    }
	    
	    /*if(checkIfMovingResultsToCache()) {
	        return new ModelAndView("jsp/noDataAvailable");
	    }*/
	    try {
	    	//if(checkIfMovingResultsToCache()) {
	    	if(this.fetchRedisHelper().sendingResultsToCache()) {
	    		return new ModelAndView("jsp/noDataAvailable");
	    	}
	    } catch (Exception ex) {
//	    	ex.printStackTrace();
	    	logger.error("HomeController: viewCourseHomePageX : error : " + ex.getMessage());

	    	//if REDIS stopped - exception catched - page loading continued -Vilpesh on 2021-11-23
	    }
	    
	    String userId = (String)request.getSession().getAttribute("userId");
	    StudentStudentPortalBean student = (StudentStudentPortalBean) request.getSession().getAttribute("student_studentportal");
	    
	    Integer percentageOverall=0;
	    ModuleContentStudentPortalBean moduleContentBean = new ModuleContentStudentPortalBean();
	    List<ModuleContentStudentPortalBean> moduleDocumentList = new ArrayList<ModuleContentStudentPortalBean>();
	    List<ModuleContentStudentPortalBean> downloadCenterLink = new ArrayList<ModuleContentStudentPortalBean>();
	    List<ContentStudentPortalBean> downloadCenter = new ArrayList<ContentStudentPortalBean>();
	    
	    /*
	    List<String> listOfPercentage=new ArrayList<>();
        List<Integer> listOfModuleDocumentsCount=new ArrayList<>();
        List<Integer> listOfModuleVideosCount=new ArrayList<>();
        */
	    LearningResourcesDAO dao = (LearningResourcesDAO) act.getBean("learningResourcesDAO");
	    ModelAndView modelnView = new ModelAndView("jsp/courseHome/courseDetailsDemo"); 
	    //ModelAndView modelnView = new ModelAndView("jsp/courseHome/subjectList");   
	    

	    String programSemSubjectId = request.getParameter("programSemSubjectId").trim();

	//OLD Retrieve Content By Subject Name Commented by Riya 
	    ///String subject = request.getParameter("subject");
	  
	  //OLD Retrieve Content By Subject Name Commented by Riya 
	    // if(subject == null){
	    //     ArrayList<String> studentCourses = (ArrayList<String>)request.getSession().getAttribute("studentCourses_studentportal");
	    //     if(studentCourses != null && studentCourses.size() > 0){
	    //         subject = studentCourses.get(0).trim();
	    //     }else{
	    //         modelnView.addObject("subject", "");
	    //         setError(request, "No Active Course Available for you");
	    //         return modelnView;
	    //     }
	    // }

	//Commented By Riya getting subject pssid map from db

	    		/*String  subject = dao.getSubjectByProgramSemSubjectId(programSemSubjectId);
			
			if(programSemSubjectId == null){
				ArrayList<ConsumerProgramStructure> programSemSubjectIdWithSubject = (ArrayList<ConsumerProgramStructure>)request.getSession().getAttribute("programSemSubjectIdWithSubjects_studentportal");
				if(programSemSubjectIdWithSubject != null && programSemSubjectIdWithSubject.size() > 0){
					programSemSubjectId = programSemSubjectIdWithSubject.get(0).getProgramSemSubjectId().trim();
				}else{
					modelnView.addObject("programSemSubjectId", "");
					setError(request, "No Active Course Available for you");
					return modelnView;
				}
			}*/

	    HashMap<String,String> programSemSubjectIdWithSubject = (HashMap<String,String>)request.getSession().getAttribute("programSemSubjectIdWithSubjects_studentportal");
	    String subject = programSemSubjectIdWithSubject.get(programSemSubjectId);
	    if(StringUtils.isBlank(subject)) {
	        request.setAttribute("subjectNotAvail", true);
	        setError(request, "No Active Content Available for you");
	        return modelnView;
	    }

	    
	    try {
	        //subject
	        subject = subject.trim();
	        
	        /*
	         * Commented by Somesh as LR Percentage module not in use
	        moduleDocumentList= dao.getContentListSubjectWise(subject);

	        //String earlyAccess = (String)request.getSession().getAttribute("earlyAccess");
	        downloadCenter=dao.getDownloadCenterContents(subject);

	        downloadCenterLink=dao.getDownloadCenterLinks(subject, userId);
	        for(ModuleContentBean mcb:downloadCenterLink) {
	        }
	        for(ModuleContentBean bean:moduleDocumentList) {
	            Integer percentageForDoc=dao.getModuleDocumentPercentage(userId, bean.getId());
	            List<VideoContentBean> videoTopicsList=dao.getVideoSubTopicsListByModuleId(bean.getId());

	            Integer noOFSeenVideos=dao.getModuleVideoPercentage(userId, bean.getId());
	            Integer videoPercentage=0;
	            if(videoTopicsList!=null) {
	                if(videoTopicsList.size()>0) {
	                    videoPercentage=(noOFSeenVideos*100)/videoTopicsList.size();
	                }
	            }

	            moduleContentBean.setVideoPercentage(videoPercentage);

	            percentageOverall=(percentageForDoc+videoPercentage)/2;
	            bean.setPercentageCombined(percentageOverall);
	        }
	        */
	    }

	    catch(Exception e) {
//	        e.printStackTrace();
	    }
	    
	    ExamOrderStudentPortalBean examOrderForSession = (ExamOrderStudentPortalBean) request.getSession().getAttribute("examOrderForSession_studentportal");
	    StudentStudentPortalBean studentRegistrationForAcademicSession = (StudentStudentPortalBean) request.getSession().getAttribute("studentRegistrationForAcademicSession_studentportal");

	    modelnView.addObject("subject", subject);
	    generateCourseSessionsMap(request);
	    getAssignmentSubmissionHistoryBySubject(request,userId,subject);
	    generateCourseAssignmentsMap(request);

	    //generateCourseLearningResourcesMap(request, subject, modelnView); Comment By Riya Subject Name to PSSID Switch
	 
	    generateCourseLearningResourcesMap(request, programSemSubjectId, modelnView,subject);
	
	    
	    generateCourseResultsMap(request, subject);
	    generateCourseMarksHistoryMap(request,subject);
	    //Passed pssId by Sauarbh
//	    getCourseQueriesMap(request, subject, programSemSubjectId);
	    StudentStudentPortalBean registration=(StudentStudentPortalBean) request.getSession().getAttribute("studentRecentReg_studentportal");
	    
	    HashMap<String, List<SessionQueryAnswerStudentPortal>> mapOfStudentQueries=queryAnswerService.getCourseQueriesMap(userId, registration.getYear(), registration.getMonth(), subject, programSemSubjectId);
	    
	    request.getSession().setAttribute("myQueries", mapOfStudentQueries.get("myQueries"));
		request.getSession().setAttribute("publicQueries", mapOfStudentQueries.get("publicQueries"));
		
	    getCourseQueriesFacultyList(request, subject);
	  
	    //Forum Details Start
	    
	    //OLD Method
	    //getForumBasedOnSubjects(request,subject);
	    
	    //New Method
	    getForumBasedOnSubjectsNew(request);
	    
	    //Forum Details Start
	   
	    if (examOrderForSession != null) {
	        //Attended/Pending/Scheduled sessions 23rd Nov
	        getAllPendingSessions(request,subject, studentRegistrationForAcademicSession);
	        getAllScheduledSessions(request, subject, studentRegistrationForAcademicSession);
	        getAllAttendedSessions(request,subject, studentRegistrationForAcademicSession);
	        getAllConductedSessions(request,subject, studentRegistrationForAcademicSession);
	        
	        //Student Session with Attendance Status
	        getSingleStudentAttendanceforSubject(request,subject);

	    }else {
	        request.getSession().setAttribute("totalSessions", 0);
	        request.getSession().setAttribute("totalPendingSessions", 0);
	        request.getSession().setAttribute("totalAttendedSessions", 0);
	        request.getSession().setAttribute("totalConductedSessions", 0);
	        request.getSession().setAttribute("SessionsAttendanceforSubjectList", new ArrayList<SessionAttendanceFeedbackStudentPortal>());
	    }
	    SessionPlanPgBean sessionPlanPgBean = new SessionPlanPgBean();
	    try {
	    	sessionPlanPgBean = sessionPlanPGService.fetchModuleDetails(programSemSubjectId, userId);
	    }catch (Exception e) {
	    	sessionPlanPG_logger.error("METHOD : fetchModuleDetails(). Error occured while fetching module details for progarmSemSubjectId : "+programSemSubjectId + " Error : "+e.getMessage());
		}
	    request.getSession().setAttribute("sessionPlanPgBean", sessionPlanPgBean);
	    //Get all session videos of current cycle for the subject
	    getVideoContentForSubject(request, programSemSubjectId);
	    
	    //Get last cycle content
	    getLastCycleContent(request, programSemSubjectId);

	    request.getSession().setAttribute("moduleDocumentList", moduleDocumentList);
	    request.getSession().setAttribute("downloadCenterLink",downloadCenterLink);
	    request.getSession().setAttribute("downloadCenter",downloadCenter);
	    modelnView.addObject("moduleContentBean",moduleContentBean);
	    modelnView.addObject("sessionQuery", new SessionQueryAnswerStudentPortal());
	    modelnView.addObject("programSemSubjectId", programSemSubjectId);
	    modelnView.addObject("consumerProgramStructureId",registration.getConsumerProgramStructureId());
	    
	    modelnView.addObject("userId",userId);
	    request.setAttribute("subjectNotAvail", false);
		
		modelnView.addObject("isLoginAsLead", Boolean.parseBoolean( request.getSession().getAttribute("isLoginAsLead").toString() ) );
	    
	    return modelnView;
	}
	
//	@RequestMapping(value = "/m/courseDetailsResults2", method = RequestMethod.POST, consumes = "application/json", produces = "application/json")	
//	public ResponseEntity<List<StudentMarksBean>> mcourseDetailsResults2(HttpServletRequest request,@RequestBody StudentBean input) throws Exception {
//		HttpHeaders headers = new HttpHeaders();
//		headers.add("Content-Type", "application/json"); 
//		
//		if(checkIfMovingResultsToCache()) {
//			List<StudentMarksBean> response = new ArrayList<>();
//			return new ResponseEntity<List<StudentMarksBean>>(response, headers,  HttpStatus.OK);
//		}
//		List<StudentMarksBean> responseResult = generateCourseMarksHistoryMap(request, input.getSubject());
//		return new ResponseEntity<List<StudentMarksBean>>(responseResult, headers,  HttpStatus.OK);
//	}

//	private void getLastCycleContent(HttpServletRequest request, String subject) {
//		StudentBean student = (StudentBean) request.getSession().getAttribute("student_studentportal");
//		PortalDao pDao = (PortalDao) act.getBean("portalDAO");
//
//		List<ContentBean> contentLastCycleList = new ArrayList<ContentBean>();
//		List<ContentBean> finalContentLastCycleList = new ArrayList<ContentBean>();
//		List<ContentBean> allLastCycleContentListForSubject = new ArrayList<ContentBean>();
//		String programStructureForStudent = student.getPrgmStructApplicable();
//
//		allLastCycleContentListForSubject = pDao.getContentsForSubjectsForLastCycles(subject,student.getConsumerProgramStructureId());
//		
//		for (ContentBean contentBean : allLastCycleContentListForSubject) {
//			String programStructureForContent = contentBean.getProgramStructure();
//
//			if ("113".equals(student.getConsumerProgramStructureId()) && "Business Economics".equalsIgnoreCase(subject) && "M.sc".equals(programStructureForContent)) {
//				contentLastCycleList.add(contentBean);
//			} else if("127".equalsIgnoreCase(student.getConsumerProgramStructureId()) || "128".equalsIgnoreCase(student.getConsumerProgramStructureId())){
//				if (programStructureForContent.equals(programStructureForStudent)) {
//					contentLastCycleList.add(contentBean);
//				}
//			} else {
//				if (programStructureForContent == null || "".equals(programStructureForContent.trim())|| "All".equals(programStructureForContent)) {
//					contentLastCycleList.add(contentBean);
//				} else if (programStructureForContent.equals(programStructureForStudent)) {
//					contentLastCycleList.add(contentBean);
//				}
//			}
//		}
//
//		for(ContentBean contentBean : contentLastCycleList){
//			if(pDao.checkIfBookmarked(student.getSapid(),contentBean.getId())){
//				contentBean.setBookmarked("Y");
//			}
//			finalContentLastCycleList.add(contentBean);
//		}
//		request.getSession().setAttribute("contentLastCycleList", finalContentLastCycleList);
//	}
	
	private void getLastCycleContent(HttpServletRequest request, String programSemSubjectId) {
	    //StudentBean student = (StudentBean) request.getSession().getAttribute("student_studentportal");
	    PortalDao pDao = (PortalDao) act.getBean("portalDAO");

	   // List<ContentBean> contentLastCycleList = new ArrayList<ContentBean>();
	    List<ContentStudentPortalBean> allLastCycleContentListForSubject = new ArrayList<ContentStudentPortalBean>();
	    
	    
	    double acadContentLiveOrder = (double)request.getSession().getAttribute("acadContentLiveOrder");
	    double reg_order = (double)request.getSession().getAttribute("reg_order");
	    
	    //Student's latest registration details
	    StudentStudentPortalBean semCheck = (StudentStudentPortalBean)request.getSession().getAttribute("studentRecentReg_studentportal");
	    
	   
	    
	    //allLastCycleContentListForSubject = pDao.getContentsForSubjectsForLastCyclesNew(programSemSubjectId,acadDateFormat);
	   
	    allLastCycleContentListForSubject = contentService.getContentByPssId(reg_order,acadContentLiveOrder,semCheck.getMonth(),semCheck.getYear(),programSemSubjectId,semCheck.getSapid(),false);
		
	    //Logic shifted in service layer
	   /* if(allLastCycleContentListForSubject.size() > 0) {
	    allLastCycleContentListForSubject = fetchAndInsertBookmarksInContent(allLastCycleContentListForSubject,semCheck.getSapid());
	    }*/
	    
	  //Commented as for bookmark above method is being created
//	    for(ContentBean contentBean : allLastCycleContentListForSubject){
//	        if(pDao.checkIfBookmarked(student.getSapid(),contentBean.getId())){
//	            contentBean.setBookmarked("Y");
//	        }
//	        contentLastCycleList.add(contentBean);
//	    }
	    request.getSession().setAttribute("contentLastCycleList", allLastCycleContentListForSubject);
	}
	
	//commented this method as moved to service layer by Saurabh
	/*//Passed PssId by Saurabh
		private void getCourseQueriesMap(HttpServletRequest request, String subject, String programSemSubjectId) {
			StudentStudentPortalBean student = (StudentStudentPortalBean)request.getSession().getAttribute("student_studentportal");
			PortalDao pDao = (PortalDao)act.getBean("portalDAO");
			
			//Commented by Saurabh and fetching myQueries in single DAO
//			List<SessionQueryAnswer> myQueries = pDao.getQueriesForSessionByStudentV2(subject, student.getSapid());
//			List<SessionQueryAnswer> myCourseQueries =  new ArrayList<SessionQueryAnswer>();
			
			List<SessionQueryAnswerStudentPortal>  myQueries= pDao.getQueriesForSessionByStudentV2(student.getSapid(), programSemSubjectId);
			List<SessionQueryAnswerStudentPortal> publicQueries = new ArrayList<SessionQueryAnswerStudentPortal>();
			
			//Remove check for BBA and B.com Students by Abhay
			//myCourseQueries = pDao.getQueriesForCourseByStudent(subject, student.getSapid());
			publicQueries = getPublicCourseQueries( subject,student.getSapid(), programSemSubjectId, request);
			
			//myQueries.addAll(myCourseQueries);
			request.getSession().setAttribute("myQueries", myQueries);
			request.getSession().setAttribute("publicQueries", publicQueries);	
		}*/
	
	private void getCourseQueriesFacultyList(HttpServletRequest request, String subject) {
		StudentStudentPortalBean student = (StudentStudentPortalBean)request.getSession().getAttribute("student_studentportal");
		PortalDao pDao = (PortalDao)act.getBean("portalDAO");
		List<FacultyStudentPortalBean> facultyForQuery = new ArrayList<>();
		
		//Commented by Somesh as Already fetching same list in getCourseQueriesMap()
		/*
		List<SessionQueryAnswer> publicQueries = new ArrayList<SessionQueryAnswer>();	
		List<SessionQueryAnswer> myQueries = pDao.getQueriesForSessionByStudent(subject, student.getSapid());
		List<SessionQueryAnswer> myCourseQueries = pDao.getQueriesForCourseByStudent(subject, student.getSapid());
		myQueries.addAll(myCourseQueries);
		request.getSession().setAttribute("myQueries", myQueries);
		publicQueries = getPublicCourseQueries(subject,student.getSapid(), student.getConsumerProgramStructureId());
		*/
		
		/*
		 * acadContentLiveOrder : Max  Content Live Order from exam.examorder table
		 * reg_order : Student's Recent Registration order
		 * */
		StudentStudentPortalBean studentRecentReg = (StudentStudentPortalBean)request.getSession().getAttribute("studentRecentReg_studentportal");
		double acadContentLiveOrder = (double)	request.getSession().getAttribute("acadContentLiveOrder");
		double reg_order = (double)request.getSession().getAttribute("reg_order");
		if(acadContentLiveOrder == reg_order)
			facultyForQuery = sessionQueryAnswerDAO.getFacultyForASubject(subject,student.getConsumerProgramStructureId(),studentRecentReg.getYear(), studentRecentReg.getMonth());
		else
			facultyForQuery = sessionQueryAnswerDAO.getFacultyForASubject(subject,student.getConsumerProgramStructureId(),CURRENT_ACAD_YEAR, CURRENT_ACAD_MONTH);

//		request.getSession().setAttribute("publicQueries", publicQueries);
		request.getSession().setAttribute("facultyForQuery", facultyForQuery);	
	}
	
	//commented this method as moved to service layer by Saurabh
//	//fetching records with PssId added by Saurabh
//	private List<SessionQueryAnswerStudentPortal> getPublicCourseQueries( String subject,String sapId, String programSemSubjectId, HttpServletRequest request) {
//		PortalDao pDao = (PortalDao)act.getBean("portalDAO");
//		List<String> pssIdList = new ArrayList<>();
//		
//		//commented by Saurabh
//		//List<SessionQueryAnswer> publicQueries = pDao.getPublicQueriesForCourse(sapId, subject, consumerProgramStructureId);
//		StudentStudentPortalBean studentRecentReg = (StudentStudentPortalBean)request.getSession().getAttribute("studentRecentReg_studentportal");
//		pssIdList=pDao.getPssIdBySubjectCodeId(programSemSubjectId);
//
//		List<SessionQueryAnswerStudentPortal> publicQueries = pDao.getPublicQueriesForCourseV2(sapId, pssIdList, studentRecentReg.getYear(), studentRecentReg.getMonth());
//		if (publicQueries != null) {
//			for (SessionQueryAnswerStudentPortal sessionQueryAnswer : publicQueries) {
//				if ("Y".equals(sessionQueryAnswer.getIsAnswered())) {
//					sessionQueryAnswer.setIsAnswered("Y");
//				} else {
//					sessionQueryAnswer.setAnswer("Not Answered Yet");
//					sessionQueryAnswer.setIsAnswered("N");
//				}
//			}
//		}
//		return publicQueries;
//	}

	private void getForumBasedOnSubjects(HttpServletRequest request,String subject){
		ForumDAO fDao = (ForumDAO)act.getBean("forumDAO");
		StudentStudentPortalBean student = (StudentStudentPortalBean)request.getSession().getAttribute("student_studentportal");
		List<ForumStudentPortalBean> listOfForumsRelatedToSubject = new ArrayList<ForumStudentPortalBean>();
		HashMap<Long,String> mapOfMainThreadIdAndReplyCount = new HashMap<Long,String>();
		
		if(!"BBA".equalsIgnoreCase(student.getProgram()) && !"B.Com".equalsIgnoreCase(student.getProgram())){
			listOfForumsRelatedToSubject = fDao.getForumThreadsForSubject(subject);
		}
		
		if (listOfForumsRelatedToSubject.size() > 0) {
			for(ForumStudentPortalBean bean:listOfForumsRelatedToSubject){
				bean.setFacultyFullName(fDao.getFacultyName(bean.getCreatedBy()));
				ArrayList<ForumStudentPortalBean> repliesOfMainThread = fDao.getThreadRepliesOfMainThread(bean.getId()+"");
				mapOfMainThreadIdAndReplyCount.put(bean.getId(),String.valueOf(repliesOfMainThread.size()));
			}
		}
		
		request.getSession().setAttribute("mapOfForumThreadAndReplyCount",mapOfMainThreadIdAndReplyCount);
		request.getSession().setAttribute("listOfForumsRelatedToSubjectInSession",listOfForumsRelatedToSubject);
	}
	
	protected PassFailBean matchSubjectInPassFail(List<PassFailBean> listPassFailBean, String subject) {
		PassFailBean passFailBean = null;
		if (null != listPassFailBean && !listPassFailBean.isEmpty()) {
			logger.info("HomeController : matchSubjectInPassFail : size : " + listPassFailBean.size());
			for (int x = 0; x < listPassFailBean.size(); x++) {
				passFailBean = listPassFailBean.get(x);
				if (null != passFailBean && passFailBean.getSubject().equals(subject)) {
					logger.info("HomeController : matchSubjectInPassFail : subject : " + passFailBean.getSubject());
					break;
				}
			}
		}
		return passFailBean;
	}
	protected PassFailBean fetchPassFailfromSession(HttpServletRequest request, String subject) {
		PassFailBean passFailBean = null;
		List<PassFailBean> listPassFailBean = (ArrayList<PassFailBean>) request.getSession()
				.getAttribute(S_PASSFAIL_RR);
		passFailBean = matchSubjectInPassFail(listPassFailBean, subject);
		
		listPassFailBean = null;
		return passFailBean;
	}

	private void generateCourseResultsMap(HttpServletRequest request, String subject) {
		List<StudentMarksBean> studentMarksList = (ArrayList<StudentMarksBean>)request.getSession().getAttribute(S_MARKS_LIST);//("studentMarksList_studentportal");
		HashMap<String, StudentMarksBean> courseResultsMap = new HashMap();
		StudentStudentPortalBean student = (StudentStudentPortalBean)request.getSession().getAttribute("student_studentportal");
		boolean isSem1Student = student.getEnrollmentYear().equalsIgnoreCase("2022") && 
							   student.getEnrollmentMonth().equalsIgnoreCase("Jan") && 
							   student.getSem().equalsIgnoreCase("1");
		if(studentMarksList != null){
			for (StudentMarksBean marksBean : studentMarksList) {
				courseResultsMap.put(marksBean.getSubject(),marksBean );
			}

			request.getSession().setAttribute("courseResultsMap_studentportal", courseResultsMap);
		}

		PortalDao pDao = (PortalDao)act.getBean("portalDAO");
		PassFailBean passFailBean = new PassFailBean();
		if (!isSem1Student) {
			if (null != request.getSession().getAttribute(S_PASSFAIL_RR)
					&& !((ArrayList<PassFailBean>) request.getSession().getAttribute(S_PASSFAIL_RR)).isEmpty()) {
				passFailBean = this.fetchPassFailfromSession(request, subject);
			} else {
				passFailBean = pDao.getPassFailStatus(student.getSapid(), subject);
			}
		}
		request.getSession().setAttribute("passFailBean_studentportal", passFailBean);
	}
	
	private List<StudentMarksBean> generateCourseMarksHistoryMap(HttpServletRequest request,String subject) {
		ResultDAO resultDAO = (ResultDAO) act.getBean("resultDAO");
		StudentStudentPortalBean student = (StudentStudentPortalBean)request.getSession().getAttribute("student_studentportal");
		boolean isSem1Student = student.getEnrollmentYear().equalsIgnoreCase("2022") && 
							   student.getEnrollmentMonth().equalsIgnoreCase("Jan") && 
							   student.getSem().equalsIgnoreCase("1");
		List<StudentMarksBean> studentMarksBeanList = new ArrayList<StudentMarksBean>();
		
		if (!isSem1Student) {
			if (null != request.getSession().getAttribute(S_MARKS_HISTORY_RR)) {
				studentMarksBeanList = this.fetchAllStudentMarksfromSession(request, subject);
			} else {
				studentMarksBeanList = resultDAO.getAStudentsMarksForSubject(student.getSapid(), subject);
			}
		}
		request.getSession().setAttribute("studentMarksBeanList_studentportal", studentMarksBeanList);
		return studentMarksBeanList;
	}
	
	protected List<StudentMarksBean> fetchAllStudentMarksfromSession(HttpServletRequest request, String subject) {
		List<StudentMarksBean> studentMarksBeanList = null;
		List<StudentMarksBean> tempList = new ArrayList<StudentMarksBean>();

		/*if (null != request.getSession().getAttribute(S_MARKS_LIST)
				&& request.getSession().getAttribute(S_MARKS_LIST) instanceof List) {
			tempList.addAll((ArrayList<StudentMarksBean>) request.getSession().getAttribute(S_MARKS_LIST));
		}*/
		if (null != request.getSession().getAttribute(S_MARKS_HISTORY_RR)
				&& request.getSession().getAttribute(S_MARKS_HISTORY_RR) instanceof List) {
			tempList.addAll((ArrayList<StudentMarksBean>) request.getSession().getAttribute(S_MARKS_HISTORY_RR));
		}
		studentMarksBeanList = matchSubjectInAllStudentMarks(tempList, subject);
		
		tempList = null;
		return studentMarksBeanList;
	}

	protected List<StudentMarksBean> matchSubjectInAllStudentMarks(List<StudentMarksBean> tempList, String subject) {
		StudentMarksBean studentMarksBean = null;
		List<StudentMarksBean> studentMarksBeanList = new ArrayList<StudentMarksBean>();

		if (null != tempList && !tempList.isEmpty()) {
			logger.info("HomeController : matchSubjectInAllStudentMarks : size : " + tempList.size());
			for (int y = 0; y < tempList.size(); y++) {
				studentMarksBean = tempList.get(y);
				if (null != studentMarksBean && studentMarksBean.getSubject().equals(subject)) {
					logger.info("HomeController : matchSubjectInAllStudentMarks : subject : " + studentMarksBean.getSubject());
					studentMarksBeanList.add(studentMarksBean);
				}
			}
		} else {
			logger.info("HomeController : matchSubjectInAllStudentMarks : empty : " + subject);
		}
		return studentMarksBeanList;
	}
	
	private List<StudentMarksBean> mgenerateCourseMarksHistoryMap(StudentStudentPortalBean student) {
		ResultDAO resultDAO = (ResultDAO) act.getBean("resultDAO");
		List<StudentMarksBean> studentMarksBeanList = resultDAO.getAStudentsMarksForSubject(student.getSapid(), student.getSubject());
		return studentMarksBeanList;
	}

	/*private void generateCourseLearningResourcesMap(HttpServletRequest request, String subject, ModelAndView modelnView) {
		PortalDao pDao = (PortalDao)act.getBean("portalDAO");
		StudentBean student = (StudentBean)request.getSession().getAttribute("student_studentportal");
		List<ContentBean> contentList = new ArrayList<ContentBean>();
		List<ContentBean> finalListOfAllContentListForSubject = new ArrayList<ContentBean>();
		try{
			String earlyAccess = (String)request.getSession().getAttribute("earlyAccess");
			//for sem check remove later start
			StudentBean semCheck = pDao.getStudentsMostRecentRegistrationData(student.getSapid());
			request.getSession().setAttribute("semCheck", semCheck);
			//end
			//List<ContentBean> allContentListForSubject = pDao.getContentsForSubjectsForCurrentSession(subject);
			List<ContentBean> allContentListForSubject = new ArrayList<ContentBean>();			
			HttpServletResponse response = null;
			if(student.getProgram().equalsIgnoreCase("EPBM")|| student.getProgram().equalsIgnoreCase("MPDV")){

				allContentListForSubject = pDao.getContentsForSubjectsForCurrentSessionNew(subject,earlyAccess,student,semCheck.getMonth(),semCheck.getYear());

			}
				
			// _________________________________________ check for leads and content list
			// based on it __________________________________________
			else if (checkLead(request, response)) {
				StudentBean bean = new StudentBean();
				bean.setSubject(subject);
				allContentListForSubject = pDao.getContentsForLeads(bean);
			} else {
				allContentListForSubject = pDao.getContentsForSubjectsForCurrentSession(subject, earlyAccess, student);
			}


			String programStructureForStudent = student.getPrgmStructApplicable();
			for (ContentBean contentBean : allContentListForSubject) {
				String programStructureForContent = contentBean.getProgramStructure();

				if ("113".equals(student.getConsumerProgramStructureId())
						&& "Business Economics".equalsIgnoreCase(subject)) {
					contentList.add(contentBean);
				} else if ("127".equalsIgnoreCase(student.getConsumerProgramStructureId())
						|| "128".equalsIgnoreCase(student.getConsumerProgramStructureId())) {
					if (programStructureForContent.equals(programStructureForStudent)) {
						contentList.add(contentBean);
					}
				}

				else {
					if (programStructureForContent == null || "".equals(programStructureForContent.trim())
							|| "All".equals(programStructureForContent)) {

						contentList.add(contentBean);
					} else if (programStructureForContent.equals(programStructureForStudent)) {
						contentList.add(contentBean);
					}
				}
			}

			for(ContentBean contentBean : contentList){
				if(pDao.checkIfBookmarked(student.getSapid(),contentBean.getId())){
					contentBean.setBookmarked("Y");
				}
				finalListOfAllContentListForSubject.add(contentBean);
			}

		}catch(Exception e){
			e.printStackTrace();
		}*/

		//List<ContentBean> lastCycleContentList = pDao.getRecordingForLastCycle(subject);


		//Show only Course presentation and Course Material for next batch stuents
		/*if(earlyAccess != null && "Yes".equals(earlyAccess)){
			List<ContentBean> prospectStudentContentList = new ArrayList<ContentBean>();
			for (ContentBean contentBean : contentList) {
				String contentType = contentBean.getContentType();
				if("Course Presentation".equalsIgnoreCase(contentType) || "Course Material".equalsIgnoreCase(contentType)){
					prospectStudentContentList.add(contentBean);
				}
			}

			contentList = prospectStudentContentList;
			//For next batch students, current recordings will be considered as last cycle recordings
			lastCycleContentList = pDao.getRecordingForCurrentCycle(subject);
		}

		request.getSession().setAttribute("lastCycleContentList", lastCycleContentList);
		modelnView.addObject("lastCycleContentList", lastCycleContentList);*/


		/*request.getSession().setAttribute("contentList", finalListOfAllContentListForSubject);
		modelnView.addObject("contentList", finalListOfAllContentListForSubject);


	}*/
	 
	private void getAssignmentSubmissionHistoryBySubject(HttpServletRequest request,String sapid,String subject) {
		AssignmentsDAO dao = (AssignmentsDAO)act.getBean("asignmentsDAO");
		StudentStudentPortalBean student = (StudentStudentPortalBean) request.getSession().getAttribute("student_studentportal");
		boolean isSem1Student = student.getEnrollmentYear().equalsIgnoreCase("2022") && 
							   student.getEnrollmentMonth().equalsIgnoreCase("Jan") && 
							   student.getSem().equalsIgnoreCase("1");
		List<AssignmentStudentPortalFileBean> studentAssignmentMarksBeanList = new ArrayList<AssignmentStudentPortalFileBean>();
		if(!isSem1Student) {
			studentAssignmentMarksBeanList = dao.getAllSubmittedAsignmentsBySubject(sapid,subject);
		}
		request.getSession().setAttribute("studentAssignmentMarksBeanList", studentAssignmentMarksBeanList);
	}
	
	//studentAssignmentMarksBeanList

	private void generateCourseAssignmentsMap(HttpServletRequest request) {

		List<AssignmentStudentPortalFileBean> allAssignmentFilesList =  (ArrayList<AssignmentStudentPortalFileBean>)request.getSession().getAttribute("quickAssignments_studentportal");
 		HashMap<String, AssignmentStudentPortalFileBean> courseAssignmentsMap = new HashMap();

		if(allAssignmentFilesList != null){
			for (AssignmentStudentPortalFileBean assignment : allAssignmentFilesList) {
				courseAssignmentsMap.put(assignment.getSubject(),assignment );
			}

			request.getSession().setAttribute("courseAssignmentsMap", courseAssignmentsMap);
		}


	}

	private void generateCourseSessionsMap(HttpServletRequest request) {

		//ArrayList<SessionDayTimeBean> scheduledSessionList = (ArrayList<SessionDayTimeBean>)request.getSession().getAttribute("scheduledSessionList_studentportal");
		ArrayList<SessionDayTimeStudentPortal> scheduledSessionList = getAllScheduledSessions(request);
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
		request.getSession().setAttribute("courseSessionsMap", courseSessionsMap);


	}
	
	private ArrayList<SessionDayTimeStudentPortal> getAllScheduledSessions(HttpServletRequest request) {
		
		PortalDao pDao = (PortalDao)act.getBean("portalDAO");
		ArrayList<SessionDayTimeStudentPortal> scheduledSessionList = new ArrayList<SessionDayTimeStudentPortal>();
		StudentStudentPortalBean student = (StudentStudentPortalBean) request.getSession().getAttribute("studentBeanForSession_studentportal");
		ArrayList<Integer> currentSemPSSId = (ArrayList<Integer>)request.getSession().getAttribute("currentSemPSSId_studentportal");
		ExamOrderStudentPortalBean examOrderForSession = (ExamOrderStudentPortalBean) request.getSession().getAttribute("examOrderForSession_studentportal");
		StudentStudentPortalBean studentRegistrationData = (StudentStudentPortalBean) request.getSession().getAttribute("studentRegistrationForAcademicSession_studentportal");
		
		boolean isCourseMappingAvailable = pDao.isCourseMappingApplicableForCurrentAcadCycle(student.getSapid());
		
		//If Registration data OR examOrderForSession is not available then session is not applicable for the student
		if(studentRegistrationData == null || examOrderForSession == null){
			//If any waived in subjet's session available then fetch session
			if (isCourseMappingAvailable) {
				scheduledSessionList = pDao.getAllSessionsByCourseMapping(student.getSapid());
				return scheduledSessionList;
			}
			return scheduledSessionList;
		}	
		
		if(studentRegistrationData != null){
			student.setProgram(studentRegistrationData.getProgram());
			student.setSem(studentRegistrationData.getSem());
		}
		//Commented by Somesh updated Dao from Quick_session table
		//scheduledSessionList = pDao.getScheduledSessionForStudentsByCPSIdV2(student, studentRegistrationData.getYear(), studentRegistrationData.getMonth(), currentSemPSSId);
		scheduledSessionList = pDao.getAllScheduledSessionsFromQuickSessions(studentRegistrationData.getYear(), studentRegistrationData.getMonth(), currentSemPSSId);
		
		//Adding sessions by course mapping
		scheduledSessionList.addAll(pDao.getAllSessionsByCourseMapping(student.getSapid()));
		
		//Added for sorting
		if(scheduledSessionList.size() > 0) {
			Collections.sort(scheduledSessionList, new Comparator<SessionDayTimeStudentPortal>() {
				@Override
				public int compare(SessionDayTimeStudentPortal sBean1, SessionDayTimeStudentPortal sBean2) {
					return sBean1.getDate().compareTo(sBean2.getDate());
				}
			});
		}
		
		return scheduledSessionList;
		
	}

	//Attended/Pending/Scheduled sessions 23rd Nov
	private void getAllScheduledSessions(HttpServletRequest request,String subject, StudentStudentPortalBean studentRegistrationForAcademicSession) {
		StudentStudentPortalBean student = (StudentStudentPortalBean)request.getSession().getAttribute("student_studentportal");
		PortalDao pdao = (PortalDao)act.getBean("portalDAO");
		int totalSessions = 0;
		totalSessions = pdao.getAllSessionsforSubjectNew(subject, student.getConsumerProgramStructureId(), studentRegistrationForAcademicSession);
//		Commented By Somesh as Added session configurable
//		if(!"BBA".equalsIgnoreCase(student.getProgram()) && !"B.Com".equalsIgnoreCase(student.getProgram())){
//			totalSessions = pdao.getAllSessionsforSubject(subject,student);
//		}
		request.getSession().setAttribute("totalSessions", totalSessions);
	}

	private void getAllPendingSessions(HttpServletRequest request,String subject, StudentStudentPortalBean studentRegistrationForAcademicSession) {
		StudentStudentPortalBean student = (StudentStudentPortalBean)request.getSession().getAttribute("student_studentportal");
		PortalDao pdao = (PortalDao)act.getBean("portalDAO"); 
		int totalPendingSessions = 0;
		
		totalPendingSessions = pdao.getAllPendingSessionsNew(subject,student.getConsumerProgramStructureId(), studentRegistrationForAcademicSession);
		
//		Commented By Somesh as Added session configurable
//		if(!"BBA".equalsIgnoreCase(student.getProgram()) && !"B.Com".equalsIgnoreCase(student.getProgram())){
//			totalPendingSessions = pdao.getAllPendingSessions(subject,student);
//		}
		request.getSession().setAttribute("totalPendingSessions", totalPendingSessions);
	} 

	private void getAllAttendedSessions(HttpServletRequest request, String subject, StudentStudentPortalBean studentRegistrationForAcademicSession) {
		StudentStudentPortalBean student = (StudentStudentPortalBean)request.getSession().getAttribute("student_studentportal");
		PortalDao pdao = (PortalDao)act.getBean("portalDAO");
		int totalAttendedSessions = 0;
		totalAttendedSessions = pdao.getAllAttendedSessionsNew(subject, student.getConsumerProgramStructureId(), studentRegistrationForAcademicSession);
//		Commented By Somesh as Added session configurable
//		if(!"BBA".equalsIgnoreCase(student.getProgram()) && !"B.Com".equalsIgnoreCase(student.getProgram())){
//			totalAttendedSessions = pdao.getAllAttendedSessions(subject,student.getSapid(),student);
//		}
		request.getSession().setAttribute("totalAttendedSessions", totalAttendedSessions);
	}
	private void getAllConductedSessions(HttpServletRequest request, String subject, StudentStudentPortalBean studentRegistrationForAcademicSession) {
		StudentStudentPortalBean student = (StudentStudentPortalBean)request.getSession().getAttribute("student_studentportal");
		PortalDao pdao = (PortalDao)act.getBean("portalDAO");
		int totalConductedSessions = 0;
		totalConductedSessions = pdao.getAllConductedSessionsforSubjectNew(subject, student.getConsumerProgramStructureId(), studentRegistrationForAcademicSession);
//		Commented By Somesh as Added session configurable
//		if(!"BBA".equalsIgnoreCase(student.getProgram()) && !"B.Com".equalsIgnoreCase(student.getProgram())){
//			totalConductedSessions = pdao.getAllConductedSessionsforSubject(subject,student);
//		}
		request.getSession().setAttribute("totalConductedSessions", totalConductedSessions);
	}
	
	private void getSingleStudentAttendanceforSubject(HttpServletRequest request,String subject) {
		StudentStudentPortalBean student = (StudentStudentPortalBean)request.getSession().getAttribute("student_studentportal");
		PortalDao pdao = (PortalDao)act.getBean("portalDAO");
		StudentStudentPortalBean studentRegistrationForAcademicSession = (StudentStudentPortalBean) request.getSession().getAttribute("studentRegistrationForAcademicSession_studentportal");
		List<SessionAttendanceFeedbackStudentPortal> SessionsAttendanceforSubjectList = new ArrayList<SessionAttendanceFeedbackStudentPortal>();
		
		SessionsAttendanceforSubjectList = pdao.getSingleStudentAttendanceforSubjectNew(subject,student.getConsumerProgramStructureId(), studentRegistrationForAcademicSession);
//		Commented By Somesh as Added session configurable
//		if(!"BBA".equalsIgnoreCase(student.getProgram()) && !"B.Com".equalsIgnoreCase(student.getProgram())){
//			SessionsAttendanceforSubjectList = pdao.getSingleStudentAttendanceforSubject(student.getSapid(),subject,student);
//		}
		request.getSession().setAttribute("SessionsAttendanceforSubjectList", SessionsAttendanceforSubjectList);
	}
	
//	private void getVideoContentForSubject(HttpServletRequest request,String subject) {
//		PortalDao pdao = (PortalDao)act.getBean("portalDAO");
//		List<VideoContentBean> videoContentList=null;
//		List<VideoContentBean> finalVideoContentList=new ArrayList<>();
//		try{
//			StudentBean student = (StudentBean)request.getSession().getAttribute("student_studentportal");
//			String earlyAccess = (String)request.getSession().getAttribute("earlyAccess");
//			int programSemSubjectId = pdao.getPssIdBySubject(subject, student.getConsumerProgramStructureId());
//			HttpServletResponse response = null;
//				if(checkLead(request, response))
//					videoContentList = pdao.getVideoContentForLeads(subject,student);
//				else
//					//videoContentList = pdao.getVideoContentForSubject(subject, student, earlyAccess);
//					videoContentList = pdao.getVideoContentForSubjectNew(programSemSubjectId,earlyAccess);
//			
//			for(VideoContentBean videoContentBean : videoContentList){
//				if(pdao.checkIfBookmarked(student.getSapid(),videoContentBean.getId().toString())){
//					videoContentBean.setBookmarked("Y");
//				}
//				finalVideoContentList.add(videoContentBean);
//			}
//			request.getSession().setAttribute("videoContentList", finalVideoContentList);
//		}catch(Exception e){
//			e.printStackTrace();
//		}
//
//	}

	@RequestMapping(value = "/onlineEventRegistration", method = {RequestMethod.GET, RequestMethod.POST})
	public ModelAndView onlineEventRegistration(HttpServletRequest request, HttpServletResponse response,@ModelAttribute Online_EventBean onlineEvent) {
		if(!checkSession(request, response)){
			return new ModelAndView("jsp/login");
		}
		logger.info("Sending to online Event Registration");
		ModelAndView modelnview = new ModelAndView("jsp/onlineEventRegistration");
		modelnview.addObject("onlineEvent",onlineEvent);
		modelnview.addObject("onlineEventID",onlineEvent.getId());
		modelnview.addObject("onlineEventName",onlineEvent.getEventName());
		return modelnview ;
	}

	@RequestMapping(value = "/saveEventRegistration", method = {RequestMethod.POST,RequestMethod.GET})
	public String saveEventRegistration(@RequestParam("response")String formResponse,@RequestParam("online_EventId")String eventId,@RequestParam("eventName")String eventName, HttpServletRequest request, HttpServletResponse response, @ModelAttribute Event feedback){
		if(!checkSession(request, response)){
			return "jsp/login";
		}
		feedback.setResponse(formResponse);
		feedback.setOnline_EventId(eventId);;
		feedback.setEventName(eventName);
		String userId = (String)request.getSession().getAttribute("userId");
		feedback.setSapId(userId);
		feedback.setCreatedBy(userId);
		feedback.setLastModifiedBy(userId);
		PortalDao pDao = (PortalDao)act.getBean("portalDAO");
		pDao.saveEventRegisteration(feedback);


		return "jsp/home";
	}



	// MOBILE APP APIs
	// /m/authenticate
	// /m/resetPassword
	// /m/savePassword

//	@RequestMapping(value = "/m/feedbackCheck", method = RequestMethod.POST, consumes = "application/json", produces = "application/json")
//	public ResponseEntity<PortalFeedbackBeanResponseDto> feedbackCheck(HttpServletRequest request,
//			@RequestBody Person input) throws Exception {
//
//		HttpHeaders headers = new HttpHeaders();
//		headers.add("Content-Type", "application/json");
//
//		String userId = input.getSapId();
//		PortalFeedbackBeanResponse result = new PortalFeedbackBeanResponse();
//		PortalFeedbackBeanResponseDto resultDto = new PortalFeedbackBeanResponseDto();
//		
//		SessionAttendanceFeedbackDto sessionAttendanceFeedbackDto =null;
//		
//		List<SessionAttendanceFeedbackDto> sessionAttendanceFeedbackDtoList =new ArrayList<SessionAttendanceFeedbackDto>();
//		result = feedService.getPendingFeedbacks(userId);
//		
//		
//		if(result.getPendingFeedback()!=null)
//		{
//			for(SessionAttendanceFeedback bean : result.getPendingFeedback())
//			{
//				sessionAttendanceFeedbackDto = new SessionAttendanceFeedbackDto();
//				sessionAttendanceFeedbackDto.setSapId(bean.getSapId());
//				sessionAttendanceFeedbackDto.setSessionId(bean.getSessionId());
//				sessionAttendanceFeedbackDto.setAttended(bean.getAttended());
//				sessionAttendanceFeedbackDto.setAttendTime(bean.getAttendTime());
//				sessionAttendanceFeedbackDto.setFeedbackGiven(bean.getFeedbackGiven());
//				sessionAttendanceFeedbackDto.setDate(bean.getDate());
//				sessionAttendanceFeedbackDto.setStartTime(bean.getStartTime());
//				sessionAttendanceFeedbackDto.setDay(bean.getDay());
//				sessionAttendanceFeedbackDto.setSubject(bean.getSubject());
//				sessionAttendanceFeedbackDto.setSessionName(bean.getSessionName());
//				sessionAttendanceFeedbackDto.setFirstName(bean.getFirstName());
//				sessionAttendanceFeedbackDto.setLastName(bean.getLastName());
//				sessionAttendanceFeedbackDto.setFacultyId(bean.getFacultyId());
//				sessionAttendanceFeedbackDto.setStudentConfirmationForAttendance(bean.getStudentConfirmationForAttendance());
//				sessionAttendanceFeedbackDto.setId(bean.getId());
//				sessionAttendanceFeedbackDto.setTrack(bean.getTrack());
//				sessionAttendanceFeedbackDto.setCreatedBy(bean.getCreatedBy());
//				sessionAttendanceFeedbackDto.setCreatedDate(bean.getCreatedDate());
//				sessionAttendanceFeedbackDto.setLastModifiedBy(bean.getLastModifiedBy());
//				sessionAttendanceFeedbackDto.setLastModifiedDate(bean.getLastModifiedDate());
//
//				sessionAttendanceFeedbackDtoList.add(sessionAttendanceFeedbackDto);
//
//
//			}
//		}
//		
//		resultDto.setFeedbackType(result.getFeedbackType());
//		resultDto.setPendingAcadFeedback(result.getPendingAcadFeedback());
//		resultDto.setAcadCycleFeedbackBean(result.getAcadCycleFeedbackBean());
//		resultDto.setPendingFeedback((ArrayList<SessionAttendanceFeedbackDto>) sessionAttendanceFeedbackDtoList);
//		
//		
//		return new ResponseEntity<>(resultDto,headers, HttpStatus.OK);
//
//	}

	// Submit session or Acad Feedback
//	@RequestMapping(value = "/m/FeedbackSave", method = RequestMethod.POST, consumes = "application/json", produces = "application/json")
//	public ResponseEntity<HashMap<String, String>> saveMFeedback(HttpServletRequest request,
//			@RequestBody SessionAttendanceFeedback feedback) throws Exception {
//
//		HashMap<String, String> response = new HashMap<>();
//
//		HttpHeaders headers = new HttpHeaders();
//		headers.add("Content-Type", "application/json");
//		String userId = feedback.getSapId();
//		boolean result =feedService.saveSessionFeedback(feedback,userId);
//		if (result) {
//			response.put("success", "true");
//			response.put("successMessage", "feedback saved");
//			return new ResponseEntity<>(response, headers, HttpStatus.OK);
//		}else
//		{
//			return new ResponseEntity<>(headers, HttpStatus.UNPROCESSABLE_ENTITY);
//		}
//
//		// save session feedback based on studentConfirmationForAttendance=y/n
//
//	}

//	@RequestMapping(value = "/m/saveAcadCycleFeedback", method = RequestMethod.POST, consumes = "application/json", produces = "application/json")
//	public ResponseEntity<HashMap<String, String>> saveMAcadCycleFeedback(HttpServletRequest request,
//			@RequestBody AcadCycleFeedback feedback) throws Exception {
//
//		HashMap<String, String> response = new HashMap<>();
//
//		HttpHeaders headers = new HttpHeaders();
//		headers.add("Content-Type", "application/json");
//
//		boolean result = feedService.saveAcadFeedback(feedback);
//		if (result) {
//			response.put("success", "true");
//			response.put("successMessage", "feedback saved");
//			return new ResponseEntity<>(response, headers, HttpStatus.OK);
//		}else {
//			return new ResponseEntity<>(headers, HttpStatus.UNPROCESSABLE_ENTITY);
//		}
//
//	}
	
//	@RequestMapping(value = "/m/authenticate", method = RequestMethod.POST, consumes = "application/json", produces = "application/json")
//	public ResponseEntity<StudentBean> m_home(@RequestBody Person input) throws Exception {
//
//
//		HttpHeaders headers = new HttpHeaders();
//		headers.add("Content-Type", "application/json"); 
//		LDAPDao dao = (LDAPDao)act.getBean("ldapdao");
//
//
//
//		String userId = input.getUserId();
//		String password = input.getPassword();
//
//		if(password == null || userId == null || "".equals(password.trim()) || "".equals(userId.trim())){
//			//			request.setAttribute("error", "true");
//			//			request.setAttribute("errorMessage", "Please enter ID and Password.");
//			//			return modelnView;
//			return new ResponseEntity<>(headers, HttpStatus.UNPROCESSABLE_ENTITY);
//		}
//
//		boolean authenticated = false;
//
//		try {
//			if("app@ngasce20".equals(password)){
//				authenticated = true;
//			}else{
//				authenticated = dao.login(userId, password);
//			}
//		} catch (Exception e) {
//			e.printStackTrace();
//			//			modelnView = new ModelAndView("jsp/login");
//			//			request.setAttribute("error", "true");
//			//			request.setAttribute("errorMessage", "We are performing temporary maintenance activity, please try after some time.");
//			//			return modelnView;
//			return new ResponseEntity<>(headers, HttpStatus.SERVICE_UNAVAILABLE);
//		}
//
//		//		String session_SERVER_PATH = SERVER_PATH;
//
//		//request.setAttribute("SERVER_PATH", SERVER_PATH);
//
//		if(authenticated){
//			PortalDao pDao = (PortalDao)act.getBean("portalDAO");
//			String session_validityExpired = "No"; //This parameter is kept false initially.It will get set to true only if the validity is expired.
//			String session_earlyAccess = "No";
//			//String session_SERVER_PATH = SERVER_PATH;
//
//
//			if(!(userId.startsWith("77") || userId.startsWith("79"))){
//				List error = new ArrayList<String>();
//				return new ResponseEntity<>(headers, HttpStatus.UNAUTHORIZED);
//			}
//
//			StudentBean student = pDao.getSingleStudentsData(userId);
//			boolean isCertificate = student.isCertificateStudent();
//			boolean session_isCertificate = isCertificate;
//			boolean isValid = isStudentValid(student, userId);
//			
//
//			// disable program terminated Student from login 
//			if("Program Terminated".equalsIgnoreCase(student.getProgramStatus()))
//			{
//				return new ResponseEntity<>(headers, HttpStatus.UNAUTHORIZED);
//				//			request.setAttribute("error", "true");
//				//			request.setAttribute("errorMessage", "Unable to access your Profile for further details call 1800 1025 136 (Mon-Sat) 10am-6pm");
//				//			return logout(request,respnse);
//			}
//
//			if(!isValid){
//				//return new ResponseEntity<>(headers, HttpStatus.UNAUTHORIZED);
//				student.setValidityExpired("Y");
//
//				//			request.getSession().setAttribute("validityExpired","Yes");
//				//			return new  ModelAndView("support/overview");
//			}
//			else {
//				student.setValidityExpired("N");
//			}
//			String validityEndDate = getValidityEndDate(student);
//			String session_validityEndDate = validityEndDate;
//
//			if ( !isTimeboundWiseByConsumerProgramStructureId(student.getConsumerProgramStructureId()) 
//					&& !"119".equalsIgnoreCase(student.getConsumerProgramStructureId())
//					&& !"126".equalsIgnoreCase(student.getConsumerProgramStructureId())	) {
//				//HashMap<String,BigDecimal> examOrderMap = pDao.getExamOrderMap();
//				List<ExamOrderBean> liveFlagList = pDao.getLiveFlagDetails();
//				HashMap<String,BigDecimal> examOrderMap = generateExamOrderMap(liveFlagList);
//				double examOrderDifference = 0.0;
//				double examOrderOfProspectiveBatch = examOrderMap.get(student.getEnrollmentMonth()+student.getEnrollmentYear()).doubleValue();
//				double maxOrderWhereContentLive = getMaxOrderWhereContentLive(liveFlagList);
//				examOrderDifference = examOrderOfProspectiveBatch - maxOrderWhereContentLive;
//
//				if(examOrderDifference == 1){
//					session_earlyAccess = "Yes";
//				}
//			}
//
//			if(student != null){
//				student = this.replaceNullToEmpty(student); 
//				return new ResponseEntity<>(student, headers,  HttpStatus.OK);
//			}
//
//		}else{
//			return new ResponseEntity<>(headers, HttpStatus.UNAUTHORIZED);
//		}
//		return new ResponseEntity<>(headers, HttpStatus.UNAUTHORIZED);
//	}

//	@RequestMapping(value = "/m/resetPassword", method = RequestMethod.POST, produces="application/json", consumes="application/json")
//	public ResponseEntity<HashMap<String,String>> mresetPassword(@RequestBody StudentBean student) throws Exception{
//		HttpHeaders headers = new HttpHeaders();
//		headers.add("Content-Type", "application/json"); 
//		HashMap<String,String> response = new HashMap<>();
//
//		String userId = student.getSapid();
//		LDAPDao dao = (LDAPDao)act.getBean("ldapdao");
//		try{
//			String email = "";
//			Person person = dao.findPerson(userId);
//
//
//			if(person != null){
//				if(userId.startsWith("77") || userId.startsWith("79")){
//					PortalDao pDao = (PortalDao)act.getBean("portalDAO");
//					student = pDao.getSingleStudentsData(userId);
//					email = student.getEmailId();
//				}else{
//					email = person.getEmail();
//				}
//
//				if(email != null && email.indexOf("@") != -1){
//					MailSender mailer = (MailSender)act.getBean("mailer");
//					try{
//						mailer.sendPasswordEmail(person, email);
//						response.put("success", "true");
//						response.put("successMessage","Your password is emailed to your registered email id: "+email);
//						return new ResponseEntity<>(response, headers, HttpStatus.OK);
//
//
//					}catch(Exception e){
//						e.printStackTrace();
//						response.put("error", "true");
//						response.put("errorMessage", "Error in resetting password. Please contact ngasce@nmims.edu to reset your password");
//						return new ResponseEntity<>(response, headers, HttpStatus.OK);
//					}
//				}else{
//					response.put("error", "true");
//					response.put("errorMessage", "No registered mail id exists with us. Please send email to ngasce@nmims.edu to reset your password.");
//					return new ResponseEntity<>(response, headers, HttpStatus.OK);
//				}
//			}else{
//				response.put("error", "true");
//				response.put("errorMessage", "User ID does not exist. Password cannot be reset.");
//				return new ResponseEntity<>(response, headers, HttpStatus.OK);
//			}
//
//		}catch(NameNotFoundException e){
//			e.printStackTrace();
//			response.put("error", "true");
//			response.put("errorMessage", "User ID does not exist. Password cannot be reset.");
//			return new ResponseEntity<>(response, headers, HttpStatus.OK);
//		}catch(Exception e){
//			e.printStackTrace();
//		}
//		return new ResponseEntity<>(response, headers, HttpStatus.OK);
//	}


//	@RequestMapping(value = "/m/savePassword", method = RequestMethod.POST, produces="application/json", consumes="application/json")
//	public ResponseEntity<HashMap<String,String>> msavePassword(@RequestBody Person input) throws Exception{
//
//		HttpHeaders headers = new HttpHeaders();
//		headers.add("Content-Type", "application/json"); 
//		HashMap<String,String> response = new HashMap<>();
//
//		String password = input.getPassword();
//		String oldPassword = input.getOldPassword();
//		String userId = input.getUserId();
//		LDAPDao dao = (LDAPDao)act.getBean("ldapdao");
//		PortalDao pdao = (PortalDao)act.getBean("portalDAO");
//		//LdapContextSource ctx = (LdapContext) act.getBean("contextSource");
//		String userOldPassword = dao.getUserPassword(userId);
//		if(userOldPassword.equals(oldPassword)) {
//			try{
//				dao.changePassword(password, userId);
//				if(pdao.updatePasswordFlag(userId)) {
//					//		modelnView = new ModelAndView("jsp/home");
//					//				request.setAttribute("success","true");
//					//				request.setAttribute("successMessage","Password changed successfully.");
//					//				request.getSession().setAttribute("password",password);
//					PortalDao pDao = (PortalDao)act.getBean("portalDAO");
//					StudentBean student = pDao.getSingleStudentsData(userId);
//					Person person = dao.findPerson(userId);
//					mailSender.sendPasswordEmailNew(person,student.getEmailId(),password);
//					response.put("Status", "success");
//					response.put("Message", "Password changed successfully.");
//					return new ResponseEntity<>(response, headers, HttpStatus.OK);
//				}else {
//					dao.changePassword(oldPassword, userId);
//					response.put("Status", "error");
//					response.put("Message", "Oops! We are Sorry Something went wrong. We're working on it now");
//					return new ResponseEntity<>(response, headers, HttpStatus.OK);
//				}
//
//			}catch(Exception e){
//				response.put("Status", "error");
//				response.put("Message", "New password does not meet password policy OR is similar to past/default password. Please set password as per policy.");
//				return new ResponseEntity<>(response, headers, HttpStatus.OK);
//
//			}
//		}
//		response.put("Status", "error");
//		response.put("Message", "Incorrect Old Password found");
//		//response.put("password", password);
//		return new ResponseEntity<>(response, headers, HttpStatus.OK);
//		/*try{
//			if(!dao.login(userId, oldPassword)) {
//				response.put("error", "true");
//				response.put("errorMessage", "Incorrect Old Password Found,Please Try again");
//				return new ResponseEntity<>(response, headers, HttpStatus.OK);
//			}
//			dao.changePassword(password, userId);
//	//		modelnView = new ModelAndView("jsp/home");
////			request.setAttribute("success","true");
////			request.setAttribute("successMessage","Password changed successfully.");
////			request.getSession().setAttribute("password",password);
//			response.put("success", "true");
//			response.put("successMessage", "Password changed successfully.");
//			response.put("password", password);
//			return new ResponseEntity<>(response, headers, HttpStatus.OK);
//
//		}catch(Exception e){
//			response.put("error", "true");
//			response.put("errorMessage", "New password does not meet password policy OR is similar to past/default password. Please set password as per policy.");
//			response.put("password", password);
//			return new ResponseEntity<>(response, headers, HttpStatus.OK);
//
//		}*/
//	}



//	@RequestMapping(value = "/m/getSession", method = RequestMethod.POST, consumes = "application/json", produces = "application/json")
//	private ResponseEntity<ArrayList<SessionDayTimeBean>> mgetAcademicCalendar(@RequestBody StudentBean student) {
//
//		HttpHeaders headers = new HttpHeaders();
//		headers.add("Content-Type","application/json");
//		PortalDao pDao = (PortalDao)act.getBean("portalDAO");
//		ArrayList<StudentMarksBean> allStudentRegistrations = pDao.getAllRegistrationsFromSAPID(student.getSapid());
//		HashMap<String, StudentMarksBean> monthYearAndStudentRegistrationMap = new HashMap<>();
//		for (StudentMarksBean bean : allStudentRegistrations) {
//			monthYearAndStudentRegistrationMap.put(bean.getMonth() + "-" + bean.getYear(), bean);
//		}
//		List<ExamOrderBean> liveFlagList = pDao.getLiveFlagDetails();
//		StudentMarksBean studentRegistrationData = getStudentRegistrationForForSpecificLiveSettings(monthYearAndStudentRegistrationMap, liveFlagList, "acadSessionLive");
//		ArrayList<SessionDayTimeBean> scheduledSessionList = new ArrayList<>();
//		if(studentRegistrationData == null) {
//			return new ResponseEntity<ArrayList<SessionDayTimeBean>>(scheduledSessionList, headers, HttpStatus.UNAUTHORIZED);
//		}
//		//Check if student has FAA subject.
//		Online_EventBean onlineEvent = pDao.getLiveOnlineEvent(studentRegistrationData.getProgram(),studentRegistrationData.getSem(),student.getPrgmStructApplicable());
//		boolean registeredForEvent = false;
//		if(onlineEvent != null){
//			registeredForEvent = pDao.getOnlineEventRegistration(student.getSapid(),onlineEvent.getId());
//		}
//		student.setProgram(studentRegistrationData.getProgram());
//		student.setSem(studentRegistrationData.getSem());
//		ArrayList<String> subjects = getSubjectsForStudent(student);
//		scheduledSessionList = pDao.getScheduledSessionForStudents(subjects,student);
//		return new ResponseEntity<ArrayList<SessionDayTimeBean>>(scheduledSessionList, headers, HttpStatus.OK);
//	}

//	@RequestMapping(value = "/m/Courses" , method = RequestMethod.POST, consumes= "application/json", produces = "application/json")
//	public ResponseEntity<LinkedList<String>> mCourses (@RequestBody StudentBean student){
//
//		PortalDao pDao = (PortalDao)act.getBean("portalDAO");
//		ArrayList<StudentMarksBean> allStudentRegistrations = pDao.getAllRegistrationsFromSAPID(student.getSapid());
//		HashMap<String, StudentMarksBean> monthYearAndStudentRegistrationMap = new HashMap<>();
//		for (StudentMarksBean bean : allStudentRegistrations) {
//			monthYearAndStudentRegistrationMap.put(bean.getMonth() + "-" + bean.getYear(), bean);
//		}
//
//		StudentBean studentDetail = pDao.getSingleStudentsData(student.getSapid());
//		
//		String liveTypeForCourses = "acadContentLive";
//		List<ExamOrderBean> liveFlagList = pDao.getLiveFlagDetails();
//
//		StudentMarksBean studentRegistrationData = getStudentRegistrationForForSpecificLiveSettings(monthYearAndStudentRegistrationMap, liveFlagList, liveTypeForCourses);
//
//		ArrayList<String> allApplicableSubjects = new ArrayList<String>();
//		ArrayList<String> currentSemSubjects = null;
//		ArrayList<String> notPassedSubjects = null;
//
//
//		if(studentRegistrationData != null){
//			studentDetail.setSem(studentRegistrationData.getSem());
//			studentDetail.setProgram(studentRegistrationData.getProgram());
//			currentSemSubjects = getSubjectsForStudent(studentDetail);
//		}
//
//		if(currentSemSubjects == null){
//			currentSemSubjects = new ArrayList<>();
//		}
//		
//		studentService.mgetWaivedInSubjects(studentDetail);		// add waived in subjects
//		studentService.mgetWaivedOffSubjects(studentDetail);	// add waived off subjects
//
//		allApplicableSubjects.addAll(currentSemSubjects);
//		ArrayList<String> waivedInSubjects = studentDetail.getWaivedInSubjects();
//		if(waivedInSubjects != null) {
//			for (String subject : waivedInSubjects) {
//				if(!allApplicableSubjects.contains(subject)) {
//					allApplicableSubjects.add(subject);
//				}
//			}
//		}
//		
//		ArrayList<String> failedSubjects = pDao.getFailSubjectsNamesForAStudent(studentDetail.getSapid());
//		if(failedSubjects != null){
//			allApplicableSubjects.addAll(failedSubjects);
//		} else{
//			failedSubjects = new ArrayList<String>(); 
//		}
//
//		notPassedSubjects = pDao.getNotPassedSubjectsBasedOnSapid((studentDetail.getSapid()));
//		if(notPassedSubjects != null && notPassedSubjects.size()>0){
//			allApplicableSubjects.addAll(notPassedSubjects);
//		}
//		ArrayList<String> listOfApplicableSubjects = new ArrayList<String>(new LinkedHashSet<String>(allApplicableSubjects));
//		//remove Waiveoff subject from applicable subject list
//		for(String subjects: allApplicableSubjects)
//		{
//			if(studentDetail.getWaivedOffSubjects().contains(subjects)){
//				listOfApplicableSubjects.remove(subjects);
//			}
//		}
//		LinkedList<String> sub = new LinkedList<String>();
//		for(int i = 0 ; i< listOfApplicableSubjects.size(); i++){
//			sub.add(listOfApplicableSubjects.get(i));
//		}
//		return  new ResponseEntity<LinkedList<String>>(sub, HttpStatus.OK);
//	}


//	@RequestMapping(value ="/m/viewCourseHomePage", method = {RequestMethod.POST})
//	public ResponseEntity<HashMap<String, AssignmentFileBean>> mgenerateCourseAsignmentMap(HttpServletRequest request) {
//		HashMap<String, StudentMarksBean> monthYearAndStudentRegistrationMap = new HashMap<>();
//		PortalDao pDao = (PortalDao)act.getBean("portalDAO");
//		List<ExamOrderBean> liveFlagList = pDao.getLiveFlagDetails();
//		String liveTypeForCourses = "acadContentLive";
//		StudentMarksBean studentRegistrationData = getStudentRegistrationForForSpecificLiveSettings(monthYearAndStudentRegistrationMap, liveFlagList, liveTypeForCourses);
//		AssignmentsDAO dao = (AssignmentsDAO)act.getBean("assignmentsDAO");
//
//		StudentBean student = new StudentBean();
//		String sapId = student.getSapid();
//		Boolean isOnline = isOnline(student);
//
//		ArrayList<String> currentSemSubjects = new ArrayList<>();
//		ArrayList<String> failSubjects = new ArrayList<>();
//		ArrayList<String> applicableSubjects = new ArrayList<>();
//
//		HashMap<String, String> subjectSemMap = new HashMap<>();
//
//		String currentSem = null;
//
//		int maxAttempts = Integer.parseInt(ASSIGNMENT_SUBMISSIONS_ATTEMPTS);
//
//		currentSem = student.getSem();
//
//		String sapid = request.getParameter("sapid");
//		String program = request.getParameter("program");
//		String prgmStructApplicable = request.getParameter("prgmStructApplicable");
//		student.setSapid(request.getParameter("sapid"));
//		student.setSem(request.getParameter("sem"));
//		student.setProgram(request.getParameter("program"));
//		student.setPrgmStructApplicable(request.getParameter("prgmStructApplicable"));
//		if(studentRegistrationData != null){
//			student.setSem(request.getParameter("sem"));
//			student.setProgram(request.getParameter("program"));
//			student.setPrgmStructApplicable(request.getParameter("prgmStructApplicable"));
//			currentSemSubjects = getSubjectsForStudent(student);
//		}
//		
//		
//		//Get failed Subjects
//		failSubjects = new ArrayList<>();
//
//
//		ArrayList<AssignmentFileBean> failSubjectsBeans = getFailSubjects(student,pDao);
//
//		if(failSubjectsBeans != null && failSubjectsBeans.size() > 0 )
//		{
//			for(int i = 0 ; i< failSubjectsBeans.size();i++) {
//				String subject = failSubjectsBeans.get(i).getSubject();
//				String sem = failSubjectsBeans.get(i).getSem();
//				failSubjects.add(failSubjectsBeans.get(i).getSubject());
//				subjectSemMap.put(subject,sem);
//			}
//
//		}
//
//
//
//		ArrayList<AssignmentFileBean> failANSSubjectsBeans = getANSNotProcessed(student,pDao);
//		if(failANSSubjectsBeans != null && failANSSubjectsBeans.size() > 0 )
//		{
//			for(int i = 0 ; i < failANSSubjectsBeans.size(); i++) {
//				String subject = failANSSubjectsBeans.get(i).getSubject();
//				String sem = failANSSubjectsBeans.get(i).getSem();
//				failSubjects.add(failSubjectsBeans.get(i).getSubject());
//				subjectSemMap.put(subject,sem);
//			}
//		}
//
//		failSubjects.remove("Project");
//		failSubjects.remove("Module 4 - Project");
//		
//		for(String failedSubject : failSubjects) {
//			if(currentSemSubjects.contains(failedSubject)) {
//				currentSemSubjects.remove(failedSubject);
//			}
//		}
//
//		currentSemSubjects.remove("Project");
//		currentSemSubjects.remove("Module 4 - Project");
//		
//		applicableSubjects.addAll(currentSemSubjects);
//		applicableSubjects.addAll(failSubjects);
//		applicableSubjects.remove("Project");
//		applicableSubjects.remove("Module 4 - Project");
//
//
//
//
//		//Get Assignment Files for failed & Current Subjects 
//
//		List<AssignmentFileBean> allAssignmentFilesList = new ArrayList<>();
//
//		if(!isOnline) {
//			allAssignmentFilesList = dao.getAssignmentsForSubjects(applicableSubjects, student);
//
//		}else {
//			List<AssignmentFileBean> currentSemFiles = dao.getAssignmentsForSubjects(currentSemSubjects, student);
//			List<AssignmentFileBean> failSubjectsFiles = dao.getResitAssignmentsForSubjects(failSubjects, student);
//			if(currentSemFiles != null) {
//				allAssignmentFilesList.addAll(currentSemFiles);
//			}
//
//			if(failSubjectsFiles != null) {
//				allAssignmentFilesList.addAll(failSubjectsFiles);
//			}
//		}
//		if(allAssignmentFilesList != null) {
//
//			HashMap<String, AssignmentFileBean> subjectSubmissionMap = new HashMap<String, AssignmentFileBean>();
//			if(!isOnline) {
//				subjectSubmissionMap = dao.getSubmissionStatus(applicableSubjects, student.getSapid());
//			}else {
//
//				HashMap<String, AssignmentFileBean> currentSemSubjectSubmissionMap = dao.getSubmissionStatus(currentSemSubjects, sapId);
//				HashMap<String, AssignmentFileBean> failSubjectSubbmissionMap = dao.getResitSubmissionStatus(failSubjects, sapId, student);
//
//				if(currentSemSubjectSubmissionMap != null) {
//					subjectSubmissionMap.putAll(currentSemSubjectSubmissionMap);
//				}
//				if(failSubjectSubbmissionMap != null) {
//					subjectSubmissionMap.putAll(failSubjectSubbmissionMap);
//				}
//			}
//
//			for(AssignmentFileBean assignment : allAssignmentFilesList){
//				String subject = assignment.getSubject();
//				String status = "Not Submitted";
//				String attempts = "0";
//				String lastModifiedDate = "";
//				String previewPath = "";
//				AssignmentFileBean studentSubmissionStatus = subjectSubmissionMap.get(subject);
//				if(studentSubmissionStatus != null){
//					status = studentSubmissionStatus.getStatus();
//					attempts = studentSubmissionStatus.getAttempts();
//					lastModifiedDate = studentSubmissionStatus.getLastModifiedDate();
//					lastModifiedDate = lastModifiedDate.replaceAll("T", " ");
//					lastModifiedDate = lastModifiedDate.substring(0,19);
//					previewPath = studentSubmissionStatus.getPreviewPath();
//
//				}
//				assignment.setStatus(status);
//				assignment.setAttempts(attempts);
//				assignment.setAttemptsLeft((maxAttempts - Integer.parseInt(attempts))+"");
//				assignment.setSem(subjectSemMap.get(subject));
//				assignment.setLastModifiedDate(lastModifiedDate);
//				assignment.setPreviewPath(previewPath);
//
//			}
//
//
//
//		}
//
//
//		//List<AssignmentFileBean> allAssignmentFilesList = (ArrayList<AssignmentFileBean>)request.getSession().getAttribute("allAssignmentFilesList_studentportal");
//
//		HashMap<String, AssignmentFileBean> courseAssignmentsMap = new HashMap();
//		if(allAssignmentFilesList != null) {
//			for(AssignmentFileBean assignment: allAssignmentFilesList) {
//				courseAssignmentsMap.put(assignment.getSubject(), assignment);
//			}
//			return new ResponseEntity<HashMap<String, AssignmentFileBean>>(courseAssignmentsMap, HttpStatus.OK);
//
//		}
//		return new ResponseEntity<HashMap<String, AssignmentFileBean>>(courseAssignmentsMap, HttpStatus.OK);
//
//	}


//	@RequestMapping(value = "/m/studentTimeTable", method = RequestMethod.POST)
//	public  ResponseEntity<List<TimetableBean>> mstudentTimeTable(HttpServletRequest request) {
//		String sapid = request.getParameter("sapid");
//		String program = request.getParameter("program");
//		String prgmStructApplicable = request.getParameter("prgmStructApplicable");
//		StudentBean student = new StudentBean();
//		student.setPrgmStructApplicable(prgmStructApplicable);
//		student.setProgram(program);		
//		Map<String, String> data = new HashMap<String, String>();
//		boolean isCorporate = false;
//		PortalDao dao = (PortalDao)act.getBean("portalDAO");
//
//		String mostRecentTimetablePeriod; // = dao.getMostRecentTimeTablePeriod();
//		List<TimetableBean> timeTableList = dao.getStudentTimetableList(student,false);
//		//modelnView.addObject("timeTableList", timeTableList);
//		HashMap<String, ArrayList<TimetableBean>> programTimetableMap = new HashMap<>();
//		//data.put("String", timeTableList);
//		String examYear = "";
//		String examMonth = "";
//
//		for (int i = 0; i < timeTableList.size(); i++) {
//			TimetableBean bean = timeTableList.get(i);
//			examYear = bean.getExamYear();
//			examMonth = bean.getExamMonth();
//			if(!programTimetableMap.containsKey(bean.getProgram() + " - "+ bean.getPrgmStructApplicable() + " Program Structure")){
//				ArrayList<TimetableBean> list = new ArrayList<>();
//				list.add(bean);
//				programTimetableMap.put(bean.getProgram() + " - "+ bean.getPrgmStructApplicable() + " Program Structure", list);
//			}else{
//				ArrayList<TimetableBean> list = programTimetableMap.get(bean.getProgram() + " - "+ bean.getPrgmStructApplicable() + " Program Structure");
//				list.add(bean);
//			}
//		}
//
//		mostRecentTimetablePeriod = examMonth + "-" + examYear;
//		//SmodelnView.addObject("mostRecentTimetablePeriod", mostRecentTimetablePeriod);
//		//programTimetableMap = TimeTableController.sortByKeys(programTimetableMap);
//		TreeMap<String,  ArrayList<TimetableBean>> treeMap = new TreeMap<String,  ArrayList<TimetableBean>>(programTimetableMap);
//		request.setAttribute("programTimetableMap", treeMap);
//		return new ResponseEntity<List<TimetableBean>>(timeTableList, HttpStatus.OK);
//	}

//	@RequestMapping(value = "/m/courseDetailsSessions", method = RequestMethod.POST, consumes = "application/json", produces = "application/json")	
//	public ResponseEntity<HashMap<String, ArrayList<SessionDayTimeBean>>> m_courseDetailsSessions(@RequestBody StudentBean input) throws Exception {
//		HttpHeaders headers = new HttpHeaders();
//		headers.add("Content-Type", "application/json"); 
//		HashMap<String, ArrayList<SessionDayTimeBean>> sessionList = new  HashMap<String, ArrayList<SessionDayTimeBean>>();
//		sessionList = mgenerateCourseSessionsMap(input);
//		return  new ResponseEntity<>(sessionList, headers,  HttpStatus.OK);
//	}
	
//	@RequestMapping(value = "/m/courseDetailsDash", method = RequestMethod.POST, consumes = "application/json", produces = "application/json")	
//	public ResponseEntity<List<Integer>> m_courseDetailsDash(@RequestBody StudentBean student) throws Exception {
//		HttpHeaders headers = new HttpHeaders();
//		headers.add("Content-Type", "application/json");
//		PortalDao pDao = (PortalDao)act.getBean("portalDAO");
//		List<Integer> sessionList = new  ArrayList<Integer>();
//		String subject = student.getSubject();
//		student = pDao.getSingleStudentsData(student.getSapid());
//		
//		StudentBean studentRegistrationForAcademicSession = registrationHelper.checkStudentRegistration(student.getSapid(), student);
//		ExamOrderBean examOrderForSession = pDao.getExamOrderByYearMonth(studentRegistrationForAcademicSession.getYear(), studentRegistrationForAcademicSession.getMonth());
//		
//		if (examOrderForSession != null) {
//			int session_pending = mgetAllPendingSessions(subject, student.getConsumerProgramStructureId(), studentRegistrationForAcademicSession);
//			int session_scheduled = mgetAllScheduledSessions(subject, student.getConsumerProgramStructureId(), studentRegistrationForAcademicSession);
//			int session_attented = mgetAllAttendedSessions(subject, student.getConsumerProgramStructureId(), studentRegistrationForAcademicSession);
//			int session_conducted = mgetAllConductedSessions(subject, student.getConsumerProgramStructureId(), studentRegistrationForAcademicSession);
//			sessionList.add(session_pending);
//			sessionList.add(session_scheduled);
//			sessionList.add(session_attented);
//			sessionList.add(session_conducted);
//		}else {
//			sessionList.add(0);
//			sessionList.add(0);
//			sessionList.add(0);
//			sessionList.add(0);
//		}
//		
//		return  new ResponseEntity<>(sessionList, headers,  HttpStatus.OK);
//	}

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
	
//	@RequestMapping(value = "/m/courseDetailsSessionsSingleStudentAttendanceforSubject", method = RequestMethod.POST, consumes = "application/json", produces = "application/json")	
//	public ResponseEntity<List<SessionAttendanceFeedbackDto>> m_courseDetailsSessionsSingleStudentAttendanceforSubject(@RequestBody StudentBean input) throws Exception {
//		HttpHeaders headers = new HttpHeaders();
//		headers.add("Content-Type", "application/json"); 
//		PortalDao pDao = (PortalDao)act.getBean("portalDAO");		
//		List<SessionAttendanceFeedback> sessionListSingleStudentAttendanceforSubject = new ArrayList<SessionAttendanceFeedback>();
//		List<SessionAttendanceFeedbackDto> sessionListSingleStudentAttendanceforSubjectList = new ArrayList<SessionAttendanceFeedbackDto>();
//		SessionAttendanceFeedbackDto sessionAttendanceFeedbackDto = null;
//		
//		StudentBean student = pDao.getSingleStudentsData(input.getSapid());
//		String subject = input.getSubject();
//		String earlyAccess = checkEarlyAccess(input.getSapid());
//		if (earlyAccess.equalsIgnoreCase("No")) {
//			
//			sessionListSingleStudentAttendanceforSubject = mgetSingleStudentAttendanceforSubject(student, subject);
//			
//			for(SessionAttendanceFeedback bean: sessionListSingleStudentAttendanceforSubject)
//			{
//				sessionAttendanceFeedbackDto = new SessionAttendanceFeedbackDto();
//				sessionAttendanceFeedbackDto.setDate(bean.getDate());
//				sessionAttendanceFeedbackDto.setStartTime(bean.getStartTime());
//				sessionAttendanceFeedbackDto.setSubject(bean.getSubject());
//				sessionAttendanceFeedbackDto.setSessionName(bean.getSessionName());
//				sessionAttendanceFeedbackDto.setFacultyFirstName(bean.getFacultyFirstName());
//				sessionAttendanceFeedbackDto.setFacultyLastName(bean.getFacultyLastName());
//				sessionAttendanceFeedbackDto.setId(bean.getId());
//				sessionAttendanceFeedbackDto.setConducted(bean.getConducted());
//				sessionAttendanceFeedbackDto.setTrack(bean.getTrack());
//				
//				sessionListSingleStudentAttendanceforSubjectList.add(sessionAttendanceFeedbackDto);
//				
//				
//			}
//		}
//		
//		return  new ResponseEntity<List<SessionAttendanceFeedbackDto>>(sessionListSingleStudentAttendanceforSubjectList, headers,  HttpStatus.OK);
//	}


//		@RequestMapping(value = "/m/courseDetailsResults", method = RequestMethod.POST, consumes = "application/json", produces = "application/json")	
//		public ResponseEntity<List<PassFailBean>> m_courseDetailsResults(@RequestBody StudentBean input) throws Exception {
//			HttpHeaders headers = new HttpHeaders();
//	        headers.add("Content-Type", "application/json"); 
//
//			if(checkIfMovingResultsToCache()) {
//				List<PassFailBean> response = new ArrayList<>();
//				return new ResponseEntity<List<PassFailBean>>(response, headers,  HttpStatus.OK);
//			}
//	        List<PassFailBean> resultList = new ArrayList<PassFailBean>();
//	        resultList =  mgenerateCourseResultsMap(input);
////	        List<StudentMarksBean> resultList = mgenerateCourseMarksHistoryMap(input);
//	        return new ResponseEntity<>(resultList, headers,  HttpStatus.OK);
//	}


//	@RequestMapping(value = "/m/courseDetailsResources", method = RequestMethod.POST, consumes = "application/json", produces = "application/json")	
//	public ResponseEntity<List<ContentBean>> m_courseDetailsResources(@RequestBody StudentBean input) throws Exception {
//		HttpHeaders headers = new HttpHeaders();
//		headers.add("Content-Type", "application/json"); 
//		List<ContentBean> resourceList = new ArrayList<ContentBean>();
//		if(input.getProgramSemSubjectId() != null) {
//			resourceList =	mgenerateCourseLearningResourcesMapNewForLR(input, input.getProgramSemSubjectId());
//		}else {
//			resourceList = mgenerateCourseLearningResourcesMap(input);	
//		}
//		return new ResponseEntity<>(resourceList, headers,  HttpStatus.OK);
//	}
//	@RequestMapping(value = "/m/courseDetailsResourcesLastCycle", method = RequestMethod.POST, consumes = "application/json", produces = "application/json")	
//	public ResponseEntity<List<ContentBean>> m_courseDetailsResourcesLastCycle(@RequestBody StudentBean input) throws Exception {
//		HttpHeaders headers = new HttpHeaders();
//		headers.add("Content-Type", "application/json"); 
//		List<ContentBean> resourceList = new ArrayList<ContentBean>();
//		resourceList = mgenerateCourseLearningResourcesMapLastCycle(input);	
//		return new ResponseEntity<>(resourceList, headers,  HttpStatus.OK);
//	}


//	@RequestMapping(value = "/m/courseDetailsQueries", method = RequestMethod.POST, consumes = "application/json", produces = "application/json")	
//	public ResponseEntity<List<SessionQueryAnswer>> m_courseDetailsQueries(@RequestBody StudentBean input) throws Exception {
//		HttpHeaders headers = new HttpHeaders();
//		headers.add("Content-Type", "application/json"); 
//		List<SessionQueryAnswer> queryList = new ArrayList<SessionQueryAnswer>();
//		queryList = mgetCourseQueriesMap(input);
//		return new ResponseEntity<>(queryList, headers,  HttpStatus.OK);
//	}

//	@RequestMapping(value = "/m/courseDetailsForum", method = RequestMethod.POST, consumes = "application/json", produces = "application/json")	
//	public ResponseEntity<List<ForumBean>> m_courseDetailsForum(@RequestBody StudentBean input) throws Exception {
//		HttpHeaders headers = new HttpHeaders();
//		headers.add("Content-Type", "application/json"); 
//		List<ForumBean> forumList = new ArrayList<ForumBean>();
//		forumList = mgetForumBasedOnSubjects(input);
//		return new ResponseEntity<>(forumList, headers,  HttpStatus.OK);
//	}

//	private HashMap<String, ArrayList<SessionDayTimeBean>> mgenerateCourseSessionsMap(StudentBean student) {
//
//		ArrayList<SessionDayTimeBean> scheduledSessionList = mgetScheduledSessionList(student);
//		HashMap<String, ArrayList<SessionDayTimeBean>> courseSessionsMap = new HashMap();
//
//		if(scheduledSessionList != null){
//			for (SessionDayTimeBean sessionDayTimeBean : scheduledSessionList) {
//				if(!courseSessionsMap.containsKey(sessionDayTimeBean.getSubject())){
//					ArrayList<SessionDayTimeBean> sessionList = new ArrayList<SessionDayTimeBean>();
//					sessionList.add(sessionDayTimeBean);
//
//					courseSessionsMap.put(sessionDayTimeBean.getSubject(),sessionList );
//				}else{
//					ArrayList<SessionDayTimeBean> sessionList = courseSessionsMap.get(sessionDayTimeBean.getSubject());
//					sessionList.add(sessionDayTimeBean);
//				}
//			}
//
//
//		}
//		return courseSessionsMap;
//
//
//	}

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
	//	ArrayList<String> subjects = getSubjectsForStudent(student);
		ArrayList<String> subjects = studentCourseService.getCurrentCycleSubjects(student.getSapid(),studentRegistrationForCourses.getYear(),studentRegistrationForCourses.getMonth());
		scheduledSessionList = pDao.getScheduledSessionForStudents(subjects,student);
		return scheduledSessionList;

	}

	private List<SessionAttendanceFeedbackStudentPortal> mgetSingleStudentAttendanceforSubject(StudentStudentPortalBean student, String subject) {
		PortalDao pdao = (PortalDao)act.getBean("portalDAO");
		StudentStudentPortalBean studentRegistrationForAcademicSession = registrationHelper.checkStudentRegistration(student.getSapid(), student);
		//Commented by Somesh as now session will coming on CPS id
		//List<SessionAttendanceFeedback> SessionsAttendanceforSubjectList = pdao.getSingleStudentAttendanceforSubject(student.getSapid(),student.getSubject(),student);
		List<SessionAttendanceFeedbackStudentPortal> SessionsAttendanceforSubjectList = pdao.getSingleStudentAttendanceforSubjectNew(subject,student.getConsumerProgramStructureId(), studentRegistrationForAcademicSession);
		return SessionsAttendanceforSubjectList;
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


	private List<PassFailBean> mgenerateCourseResultsMap(StudentStudentPortalBean student) {
		List<StudentMarksBean> studentMarksList = mgetResults(student);
		HashMap<String, StudentMarksBean> courseResultsMap = new HashMap();
		if(studentMarksList != null){
			for (StudentMarksBean marksBean : studentMarksList) {
				courseResultsMap.put(marksBean.getSubject(),marksBean );
			}

			//request.getSession().setAttribute("courseResultsMap", courseResultsMap);
		}
		PortalDao pDao = (PortalDao)act.getBean("portalDAO");
		PassFailBean passFailBean = pDao.getPassFailStatus(student.getSapid(),student.getSubject() );
		List<PassFailBean> resultList = new  ArrayList<PassFailBean>();
		resultList.add(passFailBean);
		return resultList;
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

	private List<SessionQueryAnswerStudentPortal> mgetCourseQueriesMap(StudentStudentPortalBean student) {
		PortalDao pDao = (PortalDao)act.getBean("portalDAO");
		List<SessionQueryAnswerStudentPortal> myQueries = pDao.getQueriesForSessionByStudent(student.getSubject(), student.getSapid());	
		List<SessionQueryAnswerStudentPortal> myCourseQueries = pDao.getQueriesForCourseByStudent(student.getSubject(), student.getSapid());
		myQueries.addAll(myCourseQueries);
		return myQueries;
	}

	private  List<AssignmentStudentPortalFileBean> mgenerateCourseAssignmentsMap(StudentStudentPortalBean student) {
		PortalDao pDao = (PortalDao)act.getBean("portalDAO");
		StudentStudentPortalBean studentTemp  = pDao.getSingleStudentsData(student.getSapid());
		student.setIsLateral(studentTemp.getIsLateral());
		student.setPreviousStudentId(studentTemp.getPreviousStudentId());
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


		List<AssignmentStudentPortalFileBean> allAssignmentFilesList =  mgetAssignments(student, pDao, studentRegistrationForAssignment); 
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

	private List<AssignmentStudentPortalFileBean> mgetAssignmentsOld(StudentStudentPortalBean student, PortalDao pDao, StudentMarksBean studentRegistrationData) {

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
			currentSemSubjects.addAll(studentService.mgetWaivedInSubjects(student));//added by tushar to get waived in subject if applicable
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
	public ArrayList<String> mgetCourses (StudentStudentPortalBean input){
		PortalDao pDao = (PortalDao)act.getBean("portalDAO");
		HashMap<String, StudentMarksBean> monthYearAndStudentRegistrationMap = new HashMap<>();
		List<ExamOrderStudentPortalBean> liveFlagList = pDao.getLiveFlagDetails();
		String liveTypeForCourses = "acadContentLive";
		ArrayList<String> allApplicableSubjects = new ArrayList<String>();
		ArrayList<String> currentSemSubjects = null;
		ArrayList<String> notPassedSubjects = null;
		StudentMarksBean studentRegistrationData = getStudentRegistrationForForSpecificLiveSettings(monthYearAndStudentRegistrationMap, liveFlagList, liveTypeForCourses);
		String sapid = input.getSapid();
		String program = input.getProgram();
		String prgmStructApplicable = input.getPrgmStructApplicable();
		String sem = input.getSem();
		StudentStudentPortalBean student = new StudentStudentPortalBean();

		if(studentRegistrationData != null){
			student.setSem(sem);
			student.setProgram(program);
			student.setPrgmStructApplicable(prgmStructApplicable);
		//	currentSemSubjects = getSubjectsForStudent(student);
			currentSemSubjects = studentCourseService.getCurrentCycleSubjects(sapid,studentRegistrationData.getYear(),studentRegistrationData.getMonth());
		}

		if(currentSemSubjects == null){
			currentSemSubjects = new ArrayList<>();
		}

		allApplicableSubjects.addAll(currentSemSubjects);


		ArrayList<String> failedSubjects = pDao.getFailSubjectsNamesForAStudent(sapid);

		if(failedSubjects != null){
			allApplicableSubjects.addAll(failedSubjects);
		} else{
			failedSubjects = new ArrayList<String>(); 
		}

		notPassedSubjects = pDao.getNotPassedSubjectsBasedOnSapid((sapid));
		if(notPassedSubjects != null && notPassedSubjects.size()>0){
			allApplicableSubjects.addAll(notPassedSubjects);
		} 
		ArrayList<String> lstOfApplicableSubjects = new ArrayList<String>(new LinkedHashSet<String>(allApplicableSubjects));
		//remove Waiveoff subject from applicable subject list
		for(String subjects: allApplicableSubjects)
		{
			if(student.getWaivedOffSubjects().contains(subjects)){
				lstOfApplicableSubjects.remove(subjects);
			}
		}
		ArrayList<String> sub = new ArrayList<String>();
		for(int i = 0 ; i< lstOfApplicableSubjects.size(); i++){
			sub.add(lstOfApplicableSubjects.get(i));
		}
		return  sub;
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
	
//	@RequestMapping(value = "/m/courseDetailsAssignments", method = RequestMethod.POST, consumes = "application/json", produces = "application/json")	
//	public ResponseEntity<List<AssignmentFileBean>> m_courseDetailsAssignments(@RequestBody StudentBean input) throws Exception {
//		HttpHeaders headers = new HttpHeaders();
//        headers.add("Content-Type", "application/json"); 
//        List<AssignmentFileBean> assignmentList = new ArrayList<AssignmentFileBean>();
//        
//		if(checkIfMovingResultsToCache()) {
//			return new ResponseEntity<List<AssignmentFileBean>>(assignmentList, headers,  HttpStatus.OK);
//		}
//        assignmentList = 	mgenerateCourseAssignmentsMap(input);
//        return new ResponseEntity<>(assignmentList, headers,  HttpStatus.OK);
//}
	
//	@RequestMapping(value = "/m/courseDetailsAssignmentResults", method = RequestMethod.POST, consumes = "application/json", produces = "application/json")	
//	public ResponseEntity<List<AssignmentFileBean>> mcourseDetailsAssignmentResults(@RequestBody StudentBean input) throws Exception {
//		HttpHeaders headers = new HttpHeaders();
//        headers.add("Content-Type", "application/json"); 
//
//		if(checkIfMovingResultsToCache()) {
//			List<AssignmentFileBean> response = new ArrayList<>();
//			return new ResponseEntity<List<AssignmentFileBean>>(response, headers,  HttpStatus.OK);
//		}
////        List<PassFailBean> resultList = new ArrayList<PassFailBean>();
////        resultList =  mgenerateCourseResultsMap(input);
//        List<AssignmentFileBean> resultList = mgetAssignmentSubmissionHistoryBySubject(input.getSapid(),input.getSubject());
//        return new ResponseEntity<>(resultList, headers,  HttpStatus.OK);
//	}
	
	private List<AssignmentStudentPortalFileBean> mgetAssignmentSubmissionHistoryBySubject(String sapid,String subject) {
		AssignmentsDAO dao = (AssignmentsDAO)act.getBean("asignmentsDAO");
		return dao.getAllSubmittedAsignmentsBySubject(sapid,subject);
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





	public String getMonthNumber(String MonthName)
	{
		HashMap<String,String> mapOfMonthNameAndValue =new HashMap<String,String>();
		for(int i=0;i<monthList.size();i++)
		{
			mapOfMonthNameAndValue.put(monthList.get(i),String.valueOf(i+1));
		}
		return mapOfMonthNameAndValue.get(MonthName);
	}

	/*
	 * Shifted in AnnouncementStudentRESTController by Riya
	 * 
	@RequestMapping(value = "/m/getAllStudentAnnouncements", method =RequestMethod.POST, produces="application/json", consumes="application/json")
	public ResponseEntity<List<AnnouncementBean>> getAllStudentAnnouncements(HttpServletRequest request, HttpServletResponse response , @RequestBody Person input){
		
		HttpHeaders headers = new HttpHeaders();
		headers.add("Content-Type", "application/json"); 
		String userId = input.getUserId();


		PortalDao dao = (PortalDao)act.getBean("portalDAO");
		StudentBean student =dao.getSingleStudentsData(userId);


		Page<AnnouncementBean> announcementspage = new Page<AnnouncementBean>();
		String consumerProgramStructureId = student.getConsumerProgramStructureId();


		List<AnnouncementBean> announcements = new ArrayList<AnnouncementBean>();

		try {
			
			announcements = dao.getAllActiveAnnouncements(student.getProgram(),consumerProgramStructureId);	
			

		}catch(Exception e) {
			e.printStackTrace();
		}
		//Added temp for hiding Announcements for new batch
		if(student.getEnrollmentMonth().equalsIgnoreCase("Oct")) {
			announcements = new ArrayList<AnnouncementBean>();
		}
		//			int announcementSize = announcements != null ? announcements.size() : 0;
		

		return new ResponseEntity<List<AnnouncementBean>>(announcements, headers, HttpStatus.OK);


	}*/

	
	@RequestMapping(value = "/m/saveUserPassword",  method =RequestMethod.POST, produces="application/json", consumes="application/json")
	public ModelAndView msaveUserPassword(HttpServletRequest request, HttpServletResponse respnse) {
		logger.info("", "");

		if(!checkSession(request, respnse)){
			return new ModelAndView("jsp/login");
		}

		String password = request.getParameter("password");
		String userId = request.getParameter("userId");
		ModelAndView modelnView = null;
		PortalDao pDao = (PortalDao)act.getBean("portalDAO");
		StudentStudentPortalBean student = pDao.getSingleStudentsData(userId);
		LDAPDao dao = (LDAPDao)act.getBean("ldapdao");
		try{
			dao.changePassword(password, userId);
			modelnView = new ModelAndView("jsp/changeUserPassword");
			PersonStudentPortalBean person = dao.findPerson(userId);
			mailSender.sendPasswordEmailNew(person,student.getEmailId(),password);
			request.setAttribute("success","true");
			request.setAttribute("successMessage","Password changed successfully.");
			request.getSession().setAttribute("password",password);
			return new ModelAndView("jsp/forward:/studentportal/logout");
		}catch(NameNotFoundException e){
			modelnView = new ModelAndView("jsp/changeUserPassword");
			request.setAttribute("error", "true");
			request.setAttribute("errorMessage", "User does not exist.");

		}catch(Exception e){
			modelnView = new ModelAndView("jsp/changeUserPassword");
			request.setAttribute("error", "true");
			request.setAttribute("errorMessage", "New password does not meet password policy OR is similar to past/default password. Please set password as per policy.");
		}

		return modelnView;
	}




	//For updating profile in sfdc and portal from mobile//
//	@RequestMapping(value = "/m/saveProfileForSFDCAndPortal", method = RequestMethod.POST, consumes="application/json", produces="application/json")
//	 public ResponseEntity<StudentBean> saveProfileForSFDCAndPortalFromMobile(@RequestBody StudentBean input){
//			 /*,
//			 @RequestParam("shippingStreet") String shippingStreet,
//			 @RequestParam("shippingCity") String shippingCity,
//			 @RequestParam("shippingState") String shippingState,
//			 @RequestParam("shippingPostalCode") String shippingPostalCode,
//			 @RequestParam("shippingCountry") String shippingCountry,
//			 @RequestParam("shippingLocalityName") String shippingLocalityName,
//			 @RequestParam("shippingNearestLandmark") String shippingNearestLandmark,
//			 @RequestParam("shippingHouseName") String shippingHouseName){*/
//		
////		Map<String,String> response = new HashMap<String,String>();
//		StudentBean response = new StudentBean();
//		
//		String email = input.getEmailId();
//		String mobile = input.getMobile();
//		String altMobile = input.getAltPhone();
//		
//		//Get Fathers Name and Mothers Name
//		String fatherName = input.getFatherName();
//		String motherName = input.getMotherName();
//		
//		//Get industry and designation
//		String industry = input.getIndustry();
//		String designation = input.getDesignation();
//		
//		//Shipping Address Fields from update profile page//
//		String shippingHouseName = input.getHouseNoName();
//		String shippingStreet = input.getStreet();
//		String shippingLocalityName = input.getLocality();
////		String shippingNearestLandmark = input.getLandMark();
//		String shippingPostalCode = input.getPin();
//		String shippingCity = input.getCity();
//		String shippingState = input.getState();
//		String shippingCountry = input.getCountry();
//		
//		String postalAddress = shippingHouseName + ", " + shippingLocalityName+","+shippingStreet+","
//							  +shippingPostalCode+", "+shippingCity;
//		//end//
//
//		/*StudentBean student = (StudentBean)request.getSession().getAttribute("student_studentportal");
//		student.setEmailId(email);
//		student.setMobile(mobile);
//		request.getSession().setAttribute("student_studentportal", student);*/
//
//		Person person = new Person();
//		person.setEmail(email);
//		person.setPostalAddress(postalAddress);
//		person.setContactNo(mobile);
//		person.setAltContactNo(altMobile);
//
//		String userId = input.getSapid();
//
//		LDAPDao dao = (LDAPDao)act.getBean("ldapdao");
//		PortalDao pDao = (PortalDao)act.getBean("portalDAO");
//		StudentBean student = pDao.getSingleStudentsData(userId);
//		
//		//Set up new values in studentBean
//		student.setFatherName(fatherName);
//		student.setMotherName(motherName);
//		student.setDesignation(designation);
//		student.setIndustry(industry);
//		student.setHouseNoName(shippingHouseName);
//		student.setStreet(shippingStreet);
//		student.setLocality(shippingLocalityName);
////		student.setLandMark(shippingNearestLandmark);
//		student.setPostalCode(shippingPostalCode);
//		student.setCity(shippingCity);
//		student.setState(shippingState);
//		student.setCountry(shippingCountry);
//		
//		MailSender mailer = (MailSender)act.getBean("mailer");
//		String errorMessage = "";
//		try{
//
//			String enrollmentYearAndMonth = student.getEnrollmentMonth()+","+student.getEnrollmentYear();
//
//			SimpleDateFormat month_yearFormat = new SimpleDateFormat("MMM,yyyy");
//			SimpleDateFormat fulldateFormat = new SimpleDateFormat("dd-MM-yyyy");
//
//			// format year and Month into date 
//			Date dateR = month_yearFormat.parse(enrollmentYearAndMonth);
//			String fullFormatedDate = fulldateFormat.format(dateR);
//
//			Date enrollmentDate = fulldateFormat.parse(fullFormatedDate);
//			Date salesforceUseStartDate = fulldateFormat.parse("01-06-2014"); // byPass Date for Student 
//
//			// bypass student before Jul2014 as Student record not present in Salesforce  //
//			if(enrollmentDate.after(salesforceUseStartDate) && "PROD".equalsIgnoreCase(ENVIRONMENT))
//			{
//				errorMessage = salesforceHelper.updateSalesforceProfile(userId,email,mobile,fatherName,motherName,shippingStreet,shippingCity,shippingState,
//						shippingPostalCode,shippingCountry,shippingLocalityName,
//						shippingHouseName,altMobile);//This is to update Students Shipping Address in SFDC//
//			}
//
//
//			if(errorMessage == null || "".equals(errorMessage)) 
//			{
//				dao.updateProfile(userId, email, mobile, altMobile);//Update Details in LDAP//
//				pDao.updateStudentContact(student, postalAddress, mailer);// Update Details in exam.student Table
//				response = pDao.getSingleStudentsData(student.getSapid());
//				response.setStatus("success");
////				response.put("success","true");
////				response.put("successMessage","Profile updated successfully.");
//			}else
//			{
//				response.setStatus("error");
//				response.setErrorMessage(errorMessage);
////				response.put("error","true");
////				response.put("errorMessage",errorMessage);
//				pDao.updateErrorFlag(userId,errorMessage,mailer);
//			}
//		}catch(Exception e){
//			response.setStatus("error");
//			response.setErrorMessage(errorMessage);
////			response.put("error","true");
////			response.put("errorMessage", "Error in updating profile.");
//			pDao.updateErrorFlag(userId,errorMessage,mailer);
//		}
//		HttpHeaders headers = new HttpHeaders();
//		headers.add("Content-Type", "application/json"); 
//		
//		return new ResponseEntity<StudentBean>(response,headers,HttpStatus.OK);
//	}

	//For updating onesignal id from mobile//
//	@RequestMapping(value = "/m/updateOneSignalId", method = RequestMethod.POST, consumes="application/json", produces="application/json")
//	public ResponseEntity<Map<String,String>> updateOneSignalId(@RequestBody StudentBean input,
//			@RequestParam("onsignalId") String onsignalId){
//		Map<String,String> response = new HashMap<String,String>(); 
//		String userId = input.getSapid();
//		String firebaseToken = input.getFirebaseToken();
//
//		PortalDao pDao = (PortalDao)act.getBean("portalDAO");
//		if(onsignalId !="undefined") {
//			try{
//				pDao.updateOneSignalId(userId, onsignalId);// Update Details in exam.student Table //request.getSession().setAttribute("student_studentportal", student);
//				pDao.updateFirebaseToken(userId, firebaseToken);
//				response.put("success","true");
//				response.put("successMessage","Profile updated successfully.");
//			}catch(Exception e){
//				response.put("error","true");
//				response.put("errorMessage", "Error in updating profile.");
//			}
//		}
//		HttpHeaders headers = new HttpHeaders();
//		headers.add("Content-Type", "application/json"); 
//
//		return new ResponseEntity<Map<String, String>>(response,headers,HttpStatus.OK);
//	}


	@RequestMapping(value = "/processProgramPreferences", method = {RequestMethod.GET, RequestMethod.POST})
	public ModelAndView processProgramPreferences(HttpServletRequest request, HttpServletResponse respnse,@ModelAttribute FileStudentPortalBean fileBean) throws IOException{
		ModelAndView model = new ModelAndView("jsp/processProgramPreferences");
		model.addObject("fileBean", fileBean);

		MultipartFile file = fileBean.getFileData();
		if(file.isEmpty()){//Check if File was attached
			setError(request, "Please Select File To Upload ...");
			return model;
		}
		try{
			// read Excel File 
			ExcelHelper excelHelper = new ExcelHelper();
			ArrayList<List> lst = excelHelper.readProgramPreferenceExcel(fileBean);
			ArrayList<programPreference> lstProgramPreferences = (ArrayList<programPreference>) lst.get(0);
			ArrayList<programPreference> lstOfProcessRecords = new ArrayList<programPreference>();
			for(programPreference bean : lstProgramPreferences){
				programPreference processBean = new programPreference();

				populateMBAPreference(bean,1,1,processBean);
				populatePGDMPreference(bean,1,1,processBean);

				processBean.setLatestProgramCategory(bean.getLatestProgramCategory());
				processBean.setRegNo(bean.getRegNo());
				processBean.setMobileNo(bean.getMobileNo());
				processBean.setEmail(bean.getEmail());

				lstOfProcessRecords.add(processBean);
			}

			request.getSession().setAttribute("lstOfProcessRecords_studentportal", lstOfProcessRecords);
			model.addObject("rowCount", lstOfProcessRecords.size());
			setSuccess(request, "processProgramPreferences Referesh Successfully "+lstOfProcessRecords.size());
		}catch(Exception e){
//			e.printStackTrace();
			setError(request, "Error Occurs while refreshing processProgramPreferences .."+e.getMessage());
		}
		return model;
	}

	public void populateMBAPreference(programPreference bean,  int startFrom, int preferenceNumberToPopulate ,programPreference processBean){
		ArrayList<String> mbaSet = new ArrayList(Arrays.asList("MBA","MBA HR","MBA Pharmaceutical"));

		if(preferenceNumberToPopulate > 3){
			//Completed
			return;
		}

		if(startFrom == 1){
			if(mbaSet.contains(bean.getPreference1())){
				if(preferenceNumberToPopulate == 1){
					processBean.setPreference1( bean.getPreference1());
				}else if(preferenceNumberToPopulate == 2){
					processBean.setPreference2( bean.getPreference1());
				}else if(preferenceNumberToPopulate == 3){
					processBean.setPreference3( bean.getPreference1());
				}
				populateMBAPreference(bean, startFrom+1, preferenceNumberToPopulate + 1,processBean);
			}else{
				populateMBAPreference(bean, startFrom+1, preferenceNumberToPopulate,processBean);
			}
		}

		if(startFrom == 2){
			if(mbaSet.contains(bean.getPreference2())){
				if(preferenceNumberToPopulate == 1){
					processBean.setPreference1( bean.getPreference2());
				}else if(preferenceNumberToPopulate == 2){
					processBean.setPreference2( bean.getPreference2());
				}else if(preferenceNumberToPopulate == 3){
					processBean.setPreference3( bean.getPreference2());
				}
				populateMBAPreference(bean, startFrom+1, preferenceNumberToPopulate + 1,processBean);
			}else{
				populateMBAPreference(bean, startFrom+1, preferenceNumberToPopulate,processBean);
			}
		}

		if(startFrom == 3){
			if(mbaSet.contains(bean.getPreference3())){
				if(preferenceNumberToPopulate == 1){
					processBean.setPreference1( bean.getPreference3());
				}else if(preferenceNumberToPopulate == 2){
					processBean.setPreference2( bean.getPreference3());
				}else if(preferenceNumberToPopulate == 3){
					processBean.setPreference3( bean.getPreference3());
				}
				populateMBAPreference(bean, startFrom+1, preferenceNumberToPopulate + 1,processBean);
			}else{
				populateMBAPreference(bean, startFrom+1, preferenceNumberToPopulate,processBean);
			}
		}

		if(startFrom == 4){
			if(mbaSet.contains(bean.getPreference4())){
				if(preferenceNumberToPopulate == 1){
					processBean.setPreference1( bean.getPreference4());
				}else if(preferenceNumberToPopulate == 2){
					processBean.setPreference2( bean.getPreference4());
				}else if(preferenceNumberToPopulate == 3){
					processBean.setPreference3( bean.getPreference4());
				}
				populateMBAPreference(bean, startFrom+1, preferenceNumberToPopulate + 1,processBean);
			}else{
				populateMBAPreference(bean, startFrom+1, preferenceNumberToPopulate,processBean);
			}
		}

		if(startFrom == 5){
			if(mbaSet.contains(bean.getPreference5())){
				if(preferenceNumberToPopulate == 1){
					processBean.setPreference1( bean.getPreference5());
				}else if(preferenceNumberToPopulate == 2){
					processBean.setPreference2( bean.getPreference5());
				}else if(preferenceNumberToPopulate == 3){
					processBean.setPreference3( bean.getPreference5());
				}
				populateMBAPreference(bean, startFrom+1, preferenceNumberToPopulate + 1,processBean);
			}else{
				populateMBAPreference(bean, startFrom+1, preferenceNumberToPopulate,processBean);
			}
		}

		if(startFrom == 6){
			if(mbaSet.contains(bean.getPreference6())){
				if(preferenceNumberToPopulate == 1){
					processBean.setPreference1( bean.getPreference6());
				}else if(preferenceNumberToPopulate == 2){
					processBean.setPreference2( bean.getPreference6());
				}else if(preferenceNumberToPopulate == 3){
					processBean.setPreference3( bean.getPreference6());
				}
				populateMBAPreference(bean, startFrom+1, preferenceNumberToPopulate + 1,processBean);
			}else{
				populateMBAPreference(bean, startFrom+1, preferenceNumberToPopulate,processBean);
			}
		}

		if(startFrom == 7){
			if(mbaSet.contains(bean.getPreference7())){
				if(preferenceNumberToPopulate == 1){
					processBean.setPreference1( bean.getPreference7());
				}else if(preferenceNumberToPopulate == 2){
					processBean.setPreference2( bean.getPreference7());
				}else if(preferenceNumberToPopulate == 3){
					processBean.setPreference3( bean.getPreference7());
				}
				populateMBAPreference(bean, startFrom+1, preferenceNumberToPopulate + 1,processBean);
			}else{
				populateMBAPreference(bean, startFrom+1, preferenceNumberToPopulate,processBean);
			}
		}
	}

	public void populatePGDMPreference(programPreference bean,  int startFrom, int preferenceNumberToPopulate ,programPreference processBean){
		ArrayList<String> mbaSet = new ArrayList(Arrays.asList("PGDM Bangalore","PGDM Hyderabad","PGDM Navi Mumbai","PGDM Indore"));

		if(preferenceNumberToPopulate > 4){
			//Completed
			return;
		}

		if(startFrom == 1){
			if(mbaSet.contains(bean.getPreference1())){
				if(preferenceNumberToPopulate == 1){
					processBean.setPreference4(bean.getPreference1());
				}else if(preferenceNumberToPopulate == 2){
					processBean.setPreference5(bean.getPreference1());
				}else if(preferenceNumberToPopulate == 3){
					processBean.setPreference6(bean.getPreference1());
				}else if(preferenceNumberToPopulate == 4){
					processBean.setPreference7(bean.getPreference1());
				}
				populatePGDMPreference(bean, startFrom+1, preferenceNumberToPopulate + 1,processBean);
			}else{
				populatePGDMPreference(bean, startFrom+1, preferenceNumberToPopulate,processBean);
			}
		}

		if(startFrom == 2){
			if(mbaSet.contains(bean.getPreference2())){
				if(preferenceNumberToPopulate == 1){
					processBean.setPreference4(bean.getPreference2());
				}else if(preferenceNumberToPopulate == 2){
					processBean.setPreference5(bean.getPreference2());
				}else if(preferenceNumberToPopulate == 3){
					processBean.setPreference6(bean.getPreference2());
				}else if(preferenceNumberToPopulate == 4){
					processBean.setPreference7(bean.getPreference2());
				}
				populatePGDMPreference(bean, startFrom+1, preferenceNumberToPopulate + 1,processBean);
			}else{
				populatePGDMPreference(bean, startFrom+1, preferenceNumberToPopulate,processBean);
			}
		}

		if(startFrom == 3){
			if(mbaSet.contains(bean.getPreference3())){
				if(preferenceNumberToPopulate == 1){
					processBean.setPreference4(bean.getPreference3());
				}else if(preferenceNumberToPopulate == 2){
					processBean.setPreference5(bean.getPreference3());
				}else if(preferenceNumberToPopulate == 3){
					processBean.setPreference6(bean.getPreference3());
				}else if(preferenceNumberToPopulate == 4){
					processBean.setPreference7(bean.getPreference3());
				}
				populatePGDMPreference(bean, startFrom+1, preferenceNumberToPopulate + 1,processBean);
			}else{
				populatePGDMPreference(bean, startFrom+1, preferenceNumberToPopulate,processBean);
			}
		}

		if(startFrom == 4){
			if(mbaSet.contains(bean.getPreference4())){
				if(preferenceNumberToPopulate == 1){
					processBean.setPreference4(bean.getPreference4());
				}else if(preferenceNumberToPopulate == 2){
					processBean.setPreference5(bean.getPreference4());
				}else if(preferenceNumberToPopulate == 3){
					processBean.setPreference6(bean.getPreference4());
				}else if(preferenceNumberToPopulate == 4){
					processBean.setPreference7(bean.getPreference4());
				}
				populatePGDMPreference(bean, startFrom+1, preferenceNumberToPopulate + 1,processBean);
			}else{
				populatePGDMPreference(bean, startFrom+1, preferenceNumberToPopulate,processBean);
			}
		}

		if(startFrom == 5){
			if(mbaSet.contains(bean.getPreference5())){
				if(preferenceNumberToPopulate == 1){
					processBean.setPreference4(bean.getPreference5());
				}else if(preferenceNumberToPopulate == 2){
					processBean.setPreference5(bean.getPreference5());
				}else if(preferenceNumberToPopulate == 3){
					processBean.setPreference6(bean.getPreference5());
				}else if(preferenceNumberToPopulate == 4){
					processBean.setPreference7(bean.getPreference5());
				}
				populatePGDMPreference(bean, startFrom+1, preferenceNumberToPopulate + 1,processBean);
			}else{
				populatePGDMPreference(bean, startFrom+1, preferenceNumberToPopulate,processBean);
			}
		}

		if(startFrom == 6){
			if(mbaSet.contains(bean.getPreference6())){
				if(preferenceNumberToPopulate == 1){
					processBean.setPreference4(bean.getPreference6());
				}else if(preferenceNumberToPopulate == 2){
					processBean.setPreference5(bean.getPreference6());
				}else if(preferenceNumberToPopulate == 3){
					processBean.setPreference6(bean.getPreference6());
				}else if(preferenceNumberToPopulate == 4){
					processBean.setPreference7(bean.getPreference6());
				}
				populatePGDMPreference(bean, startFrom+1, preferenceNumberToPopulate + 1,processBean);
			}else{
				populatePGDMPreference(bean, startFrom+1, preferenceNumberToPopulate,processBean);
			}
		}

		if(startFrom == 7){
			if(mbaSet.contains(bean.getPreference7())){
				if(preferenceNumberToPopulate == 1){
					processBean.setPreference4(bean.getPreference7());
				}else if(preferenceNumberToPopulate == 2){
					processBean.setPreference5(bean.getPreference7());
				}else if(preferenceNumberToPopulate == 3){
					processBean.setPreference6(bean.getPreference7());
				}else if(preferenceNumberToPopulate == 4){
					processBean.setPreference7(bean.getPreference7());
				}
				populatePGDMPreference(bean, startFrom+1, preferenceNumberToPopulate + 1,processBean);
			}else{
				populatePGDMPreference(bean, startFrom+1, preferenceNumberToPopulate,processBean);
			}
		}
	}

	@RequestMapping(value="/downloadProgramPreferenceReport",method={RequestMethod.GET,RequestMethod.POST})
	public ModelAndView downloadProgramPreferenceReport(HttpServletRequest request, HttpServletResponse response){
		ArrayList<programPreference> lstOfProcessRecords = (ArrayList<programPreference>)request.getSession().getAttribute("lstOfProcessRecords_studentportal");
		return new ModelAndView("jsp/programPreferenceExcelView","lstOfProcessRecords",lstOfProcessRecords);
	}

//	@RequestMapping(value = "/m/updateProfile", method = RequestMethod.POST, consumes = "application/json", produces = "application/json")
//	public ResponseEntity<Map<String, Map>> mupdateProfile(@RequestBody StudentBean input) {
//		//			if(!checkSession(request, response)){
//		//				return "login";
//		//			}
//		HttpHeaders headers = new HttpHeaders();
//		headers.add("Content-Type", "application/json"); 
//		logger.info("Sending to update profile page");
//		Map<String,Map> response = new HashMap<String,Map>();
//		String userId = input.getSapid();
//		PortalDao pDao = (PortalDao)act.getBean("portalDAO");
//		StudentBean student = pDao.getSingleStudentsData(userId);
//		String enrollmentYearAndMonth = student.getEnrollmentMonth()+","+student.getEnrollmentYear();
//		try{
//			SimpleDateFormat month_yearFormat = new SimpleDateFormat("MMM,yyyy");
//			SimpleDateFormat fulldateFormat = new SimpleDateFormat("dd-MM-yyyy");
//
//			// format year and Month into date 
//			Date dateR = month_yearFormat.parse(enrollmentYearAndMonth);
//			String fullFormatedDate = fulldateFormat.format(dateR);
//
//			Date enrollmentDate = fulldateFormat.parse(fullFormatedDate);
//			Date salesforceUseStartDate = fulldateFormat.parse("01-06-2014"); // byPass Date for Student
//			if(enrollmentDate.after(salesforceUseStartDate))
//			{
//				HashMap<String,String> mapOfShippingAddress = salesforceHelper.getShippingAddressOfStudent((String)input.getSapid()); 
//				response.put("mapOfShippingAddress",mapOfShippingAddress);
//				//					response.put("showShippingAddress","Yes");
//
//				//					m.addAttribute("showShippingAddress", "Yes");
//				//					m.addAttribute("student",student);
//			}else{
//				//					m.addAttribute("showShippingAddress", "No");
//				//					m.addAttribute("student",student);
//			}
//		}catch(Exception e){
//			e.printStackTrace();
//		}
//		//
//		//			//Done in this manner since the page does not send values using form bind of spring//
//		if(student.getIndustry()!=null && !"".equals(student.getIndustry())){
//			//				m.addAttribute("industryList", industryList);
//		}else{
//			//				m.addAttribute("industryList", industryList);
//		}
//		//
//		if(student.getDesignation()!=null && !"".equals(student.getDesignation())){
//			//				m.addAttribute("designationList",designationList);
//		}else{
//			//				m.addAttribute("designationList", designationList);
//		}
//		//
//		//			
//		return new ResponseEntity<Map<String, Map>>(response, headers,  HttpStatus.OK);
//
//	}





	//added for healthdashboard - START
	@RequestMapping(value = "/dashboard", method = {RequestMethod.GET, RequestMethod.POST})
	public ModelAndView dashboard(HttpServletRequest request, HttpServletResponse respnse) throws Exception {
		ModelAndView mav=new ModelAndView("jsp/dashboard");
		try {
			logger.info("dashboard called");

		}
		catch(Exception e) {
			logger.isErrorEnabled();
		}

		return mav;
	}
	//END

	private ModelAndView checkIfPendingAcadCyclefeedback(HttpServletRequest request,HttpServletResponse respnse) {
		if(!checkSession(request, respnse)){
			return new ModelAndView("jsp/login");
		}
		PortalDao pDao = (PortalDao)act.getBean("portalDAO");
		String userId = (String)request.getSession().getAttribute("userId");
		StudentStudentPortalBean student = (StudentStudentPortalBean)request.getSession().getAttribute("student_studentportal");
		AcadCycleFeedback surveyList = pDao.getLiveSurveyDetails();

		AcadCycleFeedback bean = pDao.getSingleStudentsRegistrationData(userId,surveyList);
		ModelAndView modelAndView = new ModelAndView("jsp/home");
		
		boolean isDemoExamPending = false;
		//Commented by Somesh as Jun-20 exam is over
		//boolean isDemoExamPending = pDao.isDemoExamPending(student.getSapid());
		modelAndView.addObject("isDemoExamPending", isDemoExamPending);

		if( bean != null  ){
			ArrayList<AcadCycleFeedback> pendingFeedback = pDao.getPendingAcadCycleFeedbacks(userId,bean.getSem(),student.getProgram());
			if(pendingFeedback.size() == 0){
				ModelAndView modelnView = new ModelAndView("jsp/acadCycleFeedbackFormPage");
				modelnView.addObject("feedback", bean);
				modelnView.addObject("hideProfileLink", "true");
				return new ModelAndView("jsp/home");
			}
			//				return new ModelAndView("jsp/home");
			return modelAndView;
		}else{
			//					return new ModelAndView("jsp/home");
			return modelAndView;
		}
	}

	@RequestMapping(value = "/acadCycleFeedbackForm", method = RequestMethod.POST)
	private ModelAndView acadCycleFeedbackForm(HttpServletRequest request,HttpServletResponse respnse,@ModelAttribute AcadCycleFeedback feedback) {
		if(!checkSession(request, respnse)){
			return new ModelAndView("jsp/login");
		}
		StudentStudentPortalBean student = (StudentStudentPortalBean)request.getSession().getAttribute("student_studentportal");
		ModelAndView modelnView = new ModelAndView("jsp/acadCycleFeedback");
		modelnView.addObject("feedback",feedback);
		modelnView.addObject("hideProfileLink", "true");
		return modelnView;

	}

	@RequestMapping(value = "/saveAcadCycleFeedback", method = RequestMethod.POST)
	public ModelAndView saveAcadCycleFeedback(HttpServletRequest request, HttpServletResponse response, @ModelAttribute AcadCycleFeedback feedback){
		if(!checkSession(request, response)){
			return new ModelAndView("jsp/login");
		}
		String userId = (String)request.getSession().getAttribute("userId");
		StudentStudentPortalBean student = (StudentStudentPortalBean)request.getSession().getAttribute("student_studentportal");
		String feedbackVal = request.getParameter("val");
		feedback.setSapid(userId);
		//feedback.setSem(student.getSem());
		feedback.setProgram(student.getProgram());
		if(feedbackVal.equalsIgnoreCase("No")){
			feedback.setFeedbackGiven("N");
		}else{
			feedback.setFeedbackGiven("Y");
		}
		feedback.setCreatedBy(userId);
		feedback.setLastModifiedBy(userId);
		PortalDao pDao = (PortalDao)act.getBean("portalDAO");
		pDao.saveAcadCycleFeedback(feedback);

		setSuccess(request, "Feedback saved successfully for "+feedback.getProgram() + " - "+feedback.getSem());
		return checkIfPendingfeedback(request, response);
	}

	@RequestMapping(value = "/downloadAcadFeedbackReportForm", method = { RequestMethod.GET, RequestMethod.POST })
	public ModelAndView downloadAcadFeedbackReportForm(HttpServletRequest request, HttpServletResponse response,@ModelAttribute AcadCycleFeedback feedback) {
		ModelAndView mv = new ModelAndView("jsp/AcadFeedbackReport");
		mv.addObject("monthList", monthList);
		mv.addObject("yearList",ACAD_YEAR_SAS_LIST );
		mv.addObject("feedback", feedback);
		return mv;
	}

	@RequestMapping(value = "/generateAcadFeedbackReport", method = {RequestMethod.POST })
	public ModelAndView generateAcadFeedbackReport(HttpServletRequest request, HttpServletResponse response,@ModelAttribute AcadCycleFeedback feedback) {
		PortalDao pDao = (PortalDao)act.getBean("portalDAO");
		String month = request.getParameter("month");
		String year = request.getParameter("year");
		ModelAndView mv = new ModelAndView("jsp/AcadFeedbackReport");
		PageStudentPortal<AcadCycleFeedback> page = pDao.getAcadFeedbackReport(month,year,1, Integer.MAX_VALUE);
		List<AcadCycleFeedback> feedbackList = page.getPageItems();
		request.getSession().setAttribute("feedbackList_studentportal", feedbackList);
		mv.addObject("monthList", monthList);
		mv.addObject("yearList",ACAD_YEAR_SAS_LIST );
		mv.addObject("feedback", feedback);
		mv.addObject("rowCount", page.getRowCount());
		return mv;
	}

	@RequestMapping(value = "/downloadAcadFeedbackReport", method = { RequestMethod.GET, RequestMethod.POST })
	public ModelAndView downloadAcadFeedbackReport(HttpServletRequest request, HttpServletResponse response) {

		if (!checkSession(request, response)) {
			return new ModelAndView("jsp/studentPortalRediret");
		}

		@SuppressWarnings("unchecked")
		ArrayList<AcadCycleFeedback> feedbackList = (ArrayList<AcadCycleFeedback>) request.getSession().getAttribute("feedbackList_studentportal");

		return new ModelAndView("jsp/acadCycleFeedbackExcelView", "feedbackList", feedbackList);
	}

	//getTestsForStudentFromExamApp start
	//Go to Exam > StudentTestController > getTestsForStudentFromExamApp() to find the method form which data is coming here
	public List<TestStudentPortalBean> getTestsForStudentFromExamApp(String sapId) {

		CloseableHttpClient client = HttpClientBuilder.create().build();
		String url = SERVER_PATH+"exam/getTestsForStudentFromExamApp?sapId="+sapId;
		List<TestStudentPortalBean> testsForStudent = new ArrayList<>();

		try {
			HttpHeaders headers =  new HttpHeaders();
			headers.add("Content-Type", "application/json");

			RestTemplate restTemplate = new RestTemplate();
			HttpEntity<String> entity = new HttpEntity<>("parameters", headers);

			ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.GET, entity, String.class);
			JsonObject jsonObject = new JsonParser().parse(response.getBody()).getAsJsonObject();
			JsonArray jsonArray = jsonObject.get("testsForStudent").getAsJsonArray();
			Gson gson= new Gson();
			if (jsonArray != null) { 
				for (int i=0;i<jsonArray.size();i++){ 
					try {
						JsonObject tempJObj = jsonArray.get(i).getAsJsonObject();
						testsForStudent.add(gson.fromJson(tempJObj, TestStudentPortalBean.class));
					} catch (Exception e) {
						// TODO Auto-generated catch block
//						e.printStackTrace();
					}
				} 
			} 

		}catch(Exception e) {
//			e.printStackTrace();
		}
		finally{
			//Important: Close the connect
			try {
				client.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
//				e.printStackTrace();
			}
		}

		return testsForStudent;
	}
	//getTestsForStudentFromExamApp end


	public void refreshSessionDataFromOtherApps(String sapId ,HttpServletRequest request) {

		getTests(sapId,request);
	}



	/*
	 * Get Subject,program,month,year,sem list of missing sifySubjectCode
	 * */
	@RequestMapping(value="/MissingSubjectMap",method=RequestMethod.GET)
	public ModelAndView getMissingSubjectMappingList() {
		StudentInfoCheckDAO studentDao = (StudentInfoCheckDAO)act.getBean("stuentInfoCheckDAO");
		ModelAndView mv = new ModelAndView("jsp/MissingSubjectMapping");
		mv.addObject("MissingMapsubject",studentDao.getSubjectMissingMap());
		return mv;
	}

	@RequestMapping(value="/refreshAssignmentFilesStatus",method=RequestMethod.GET)
	public ResponseEntity<ResponseStudentPortalBean> refreshAssignmentFileStatus(HttpServletRequest request) {

		List<AssignmentStudentPortalFileBean> allAssignmentFilesList = (List<AssignmentStudentPortalFileBean>)request.getSession().getAttribute("allAssignmentFilesList_studentportal");
		ArrayList<String> currentSemSubjects = (ArrayList<String>)request.getSession().getAttribute("currentSemSubjects_studentportal");
		ArrayList<String> failSubjects = (ArrayList<String>)request.getSession().getAttribute("failSubjects_studentportal");
		HashMap<String, String> subjectSemMap = (HashMap<String, String>)request.getSession().getAttribute("subjectSemMap_studentportal");
		AssignmentsDAO dao = (AssignmentsDAO)act.getBean("asignmentsDAO");
		StudentStudentPortalBean student = (StudentStudentPortalBean) request.getSession().getAttribute("student_studentportal");
		String sapId = student.getSapid();
		ResponseStudentPortalBean response = new ResponseStudentPortalBean();
		try {
			int maxAttempts = Integer.parseInt(ASSIGNMENT_SUBMISSIONS_ATTEMPTS);
			if(allAssignmentFilesList != null ){

				HashMap<String,AssignmentStudentPortalFileBean> subjectSubmissionMap = new HashMap<>();
				/*Commented by Steffi to allow offline students to submit assignments in APR/SEP 
				 * if(!isOnline){
						subjectSubmissionMap = dao.getSubmissionStatus(applicableSubjects, sapId);//Assignments from Jun, Dec cycle
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
						previewPath = subjectSubmissionMap.get(subject).getPreviewPath();
					}

					assignment.setStatus(status);
					assignment.setAttempts(attempts);
					assignment.setAttemptsLeft((maxAttempts - Integer.parseInt(attempts))+"");
					assignment.setSem(subjectSemMap.get(subject));
					assignment.setLastModifiedDate(lastModifiedDate);
					assignment.setPreviewPath(previewPath);
				}
			}

			response.setResult("Successfully session refresh");
			response.setStatus("Success");
			return new ResponseEntity<ResponseStudentPortalBean>(response,HttpStatus.OK);
		}
		catch (Exception e) {
			// TODO: handle exception
			response.setResult("failed session refresh");
			response.setStatus("failed");
			return new ResponseEntity<ResponseStudentPortalBean>(response,HttpStatus.OK);
		}
	}


	//getAddressFromPinCode start
	public List<TestStudentPortalBean> getAddressFromPinCode2(String sapId) {

		CloseableHttpClient client = HttpClientBuilder.create().build();
		String url = SERVER_PATH+"exam/getTestsForStudentFromExamApp?sapId="+sapId;
		List<TestStudentPortalBean> testsForStudent = new ArrayList<>();

		try {
			HttpHeaders headers =  new HttpHeaders();
			headers.add("Content-Type", "application/json");

			RestTemplate restTemplate = new RestTemplate();
			HttpEntity<String> entity = new HttpEntity<>("parameters", headers);

			ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.GET, entity, String.class);
			JsonObject jsonObject = new JsonParser().parse(response.getBody()).getAsJsonObject();
			JsonArray jsonArray = jsonObject.get("testsForStudent").getAsJsonArray();
			Gson gson= new Gson();
			if (jsonArray != null) { 
				for (int i=0;i<jsonArray.size();i++){ 
					try {
						JsonObject tempJObj = jsonArray.get(i).getAsJsonObject();
						testsForStudent.add(gson.fromJson(tempJObj, TestStudentPortalBean.class));
					} catch (Exception e) {
						// TODO Auto-generated catch block
//						e.printStackTrace();
					}
				} 
			} 


		}catch(Exception e) {
//			e.printStackTrace();
		}
		finally{
			//Important: Close the connect
			try {
				client.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
//				e.printStackTrace();
			}
		}

		return testsForStudent;
	}
	@RequestMapping(value = "/getAddressDetailsFromPinCode", method = RequestMethod.POST, produces="application/json", consumes="application/json")
	public ResponseEntity<HashMap<String,String>> getAddressDetailsFromPinCode(@RequestBody StudentStudentPortalBean bean) throws Exception{

		HttpHeaders headers = new HttpHeaders();
		headers.add("Content-Type", "application/json"); 
		HashMap<String,String> response = new HashMap<>();
		String pinCode = bean.getPin();


		CloseableHttpClient client = HttpClientBuilder.create().build();
		String url = "http://postalpincode.in/api/pincode/"+pinCode;
		List<TestStudentPortalBean> testsForStudent = new ArrayList<>();

		try {

			RestTemplate restTemplate = new RestTemplate();
			HttpEntity<String> entity = new HttpEntity<>("parameters", headers);

			ResponseEntity<String> responseData = restTemplate.exchange(url, HttpMethod.GET, entity, String.class);
			JsonObject jsonObject = new JsonParser().parse(responseData.getBody()).getAsJsonObject();

			String statusJsonObject = jsonObject.get("Status").getAsString();

			if(!"Success".equalsIgnoreCase(statusJsonObject)) {
				response.put("error","true");
				response.put("errorMessage", "Invalid pincode.");
				return new ResponseEntity<>(response, headers, HttpStatus.INTERNAL_SERVER_ERROR);
			}
			JsonArray postOfficeJsonArray = jsonObject.get("PostOffice").getAsJsonArray();

			if (postOfficeJsonArray != null) { 
				if (postOfficeJsonArray.size() > 0) { 
					JsonObject tempJObj = postOfficeJsonArray.get(0).getAsJsonObject();
					bean.setCity(tempJObj.get("District").getAsString());
					bean.setState(tempJObj.get("State").getAsString());
					bean.setCountry(tempJObj.get("Country").getAsString());
				}
			} 
			response.put("city", bean.getCity());
			response.put("state", bean.getState());
			response.put("country", bean.getCountry());
			response.put("success", "true");
			response.put("successMessage", " successfully.");
			response.put("responseData", responseData.getBody());

			return new ResponseEntity<>(response, headers, HttpStatus.OK);


		}catch(Exception e) {
//			e.printStackTrace();
			response.put("error","true");
			response.put("errorMessage", "Error "+e.getMessage());
			return new ResponseEntity<>(response, headers, HttpStatus.INTERNAL_SERVER_ERROR);
		}
		finally{
			//Important: Close the connect
			try {
				client.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
//				e.printStackTrace();
			}
		}


	}

	//getAddressFromPinCode end




	/*private ModelAndView updateProfilePassword(HttpServletRequest request,	HttpServletResponse respnse) {
			if(!checkSession(request, respnse)){
				return new ModelAndView("jsp/login");
			}
			StudentBean student = (StudentBean)request.getSession().getAttribute("student_studentportal");
				ModelAndView modelnView = new ModelAndView("jsp/changeProfilePassword");

				modelnView.addObject("student", student);
				modelnView.addObject("hideProfileLink", "true");
				modelnView.addObject("passwordUpdate", "false");
				return modelnView;
		}*/

	@RequestMapping(value="/updateStudentProfilePassword",method={RequestMethod.POST})
	public ModelAndView updateStudentProfilePassword(HttpServletRequest request, HttpServletResponse response,@ModelAttribute StudentStudentPortalBean student) {
		if(!checkSession(request, response)){
			return new ModelAndView("jsp/login");
		}
		ModelAndView modelnView = new ModelAndView("jsp/changeProfilePassword");
		String password = student.getN_Password();
		String userId = (String)request.getSession().getAttribute("userId");
		StudentStudentPortalBean studentFromSession = (StudentStudentPortalBean)request.getSession().getAttribute("student_studentportal");
		LDAPDao dao = (LDAPDao)act.getBean("ldapdao");
		StudentInfoCheckDAO studentDao = (StudentInfoCheckDAO)act.getBean("stuentInfoCheckDAO");
		try{
			homeService.validateStudentPassword(userId, password, student.getC_password());			//validate the password entered by the student
			dao.changePassword(password, userId);
			request.getSession().setAttribute("password",password);
			studentDao.updateStudentPasswordStatus(userId);
			PersonStudentPortalBean person = dao.findPerson(userId);
			mailSender.sendPasswordEmailNew(person,studentFromSession.getEmailId(),password);
			request.setAttribute("success", "true");
			request.setAttribute("successMessage", "Password changed successfully continue to login. Your new password is sent to your registered emailId");
			modelnView.addObject("hideProfileLink", "true");
			modelnView.addObject("passwordUpdate", "true");
			return modelnView;
		}
		catch(IllegalArgumentException ex) {
//			ex.printStackTrace();
			request.setAttribute("error", "true");
			request.setAttribute("errorMessage", ex.getMessage());
			modelnView.addObject("passwordUpdate", "false");
			return new ModelAndView("jsp/login");
		}
		catch(Exception e){
			request.setAttribute("error", "true");
			request.setAttribute("errorMessage", "New password does not meet password policy OR is similar to past/default password. Please set password as per policy.");
			modelnView.addObject("passwordUpdate", "false");
			return new ModelAndView("jsp/login");
		}
	}

	/**
	 * Get data if modified 
	 * */
//	@CrossOrigin(origins = "*", allowedHeaders = "*")
//	@RequestMapping(value="/m/getStudentDataForUpdate",method=RequestMethod.POST)
//	public ResponseEntity<AuthenticateResponseBean> mgetStudentDataForUpdate(@RequestBody StudentBean request) {
//		HttpHeaders headers = new HttpHeaders();
//		headers.add("Content-Type", "application/json");
//		AuthenticateResponseBean response = new AuthenticateResponseBean();
//		StudentInfoCheckDAO studentDao = (StudentInfoCheckDAO)act.getBean("stuentInfoCheckDAO");
//		StudentBean data = studentDao.getstudentData(request.getSapid()); 	//add sapId where
//		if(data.getLastModifiedDate().equalsIgnoreCase(request.getLastModifiedDate())) {
//			response.setStatus("No update found");
//			return new ResponseEntity<AuthenticateResponseBean>(response,headers,HttpStatus.OK);
//		}
//		data = this.replaceNullToEmpty(data);
//		response.setData(data);
//		response.setStatus("update found");
//		return new ResponseEntity<AuthenticateResponseBean>(response,headers,HttpStatus.OK);
//	}
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
//		if(bean.getLandMark() == null) {
//			bean.setLandMark("");
//		}
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





	@RequestMapping(value = "/student/saveConfirmedProfileForSFDCAndPortal", method = {RequestMethod.POST})
	public ModelAndView saveConfirmedProfileForSFDCAndPortal(HttpServletRequest request, HttpServletResponse response,@ModelAttribute StudentStudentPortalBean student) throws Exception {
		if(!checkSession(request, response)){
			return new ModelAndView("jsp/login");
		}
		ModelAndView modelnView  = new ModelAndView("jsp/confirmStudentDetails");
		//get student sapid 
		String userId = (String)request.getSession().getAttribute("userId"); 
		// check if student is present in salesforce
		Boolean userInSalesforce = (Boolean)request.getSession().getAttribute("userInSalesforce");
		//generate postal address from shipping address if address field is blank
		String postalAddress =  student.getHouseNoName() + ", " + student.getLocality()+","+student.getStreet()+","
				+student.getPin()+","+student.getCity();
		/*if(userInSalesforce)
			{*/
		student.setAddress(postalAddress) ;
		/*}*/

		//get existing person details from ldap and update email/address/mobile/altphone
		PersonStudentPortalBean person = (PersonStudentPortalBean)request.getSession().getAttribute("user_studentportal");
		person.setEmail(student.getEmailId());
		person.setPostalAddress(postalAddress);
		person.setContactNo(student.getMobile());
		person.setAltContactNo(student.getAltPhone());
		request.getSession().setAttribute("user_studentportal", person);
		Boolean fromProfileIcon = (Boolean)request.getSession().getAttribute("fromProfileIcon");
		String showShippingAddress =(String)request.getSession().getAttribute("showShippingAddress");
		LDAPDao dao = (LDAPDao)act.getBean("ldapdao");
		PortalDao pDao = (PortalDao)act.getBean("portalDAO");

		student.setSapid(userId);
		MailSender mailer = (MailSender)act.getBean("mailer");
		String errorMessage = "";
		//below condition is to store timestamp for fresh students for lou
		if(student.isLouConfirmed()) {
			Date dt=new Date();
			SimpleDateFormat sdf =  new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			student.setLouConfirmedTimestamp(sdf.format(dt));
		//below condition is to store timestamp for existing students for lou
		}else {
			StudentStudentPortalBean currentstudent=(StudentStudentPortalBean)request.getSession().getAttribute("getSingleStudentsData_studentportal");
			student.setLouConfirmedTimestamp(currentstudent.getLouConfirmedTimestamp());
		}
		try{
			if(userInSalesforce)
			{
				if("PROD".equalsIgnoreCase(ENVIRONMENT)){
					errorMessage = salesforceHelper.updateSalesforceProfile(student);//This is to update Students Shipping Address in SFDC//
				}
			}

			if(errorMessage == null || "".equals(errorMessage)) 
			{
				//dao.updateProfile(userId, student.getEmailId(), student.getMobile(), student.getAltPhone());	//Commented as student will update Email, Mobile from Change Contact Details SR (cardNo: 2009)
				dao.updateUserAltPhoneLdap(userId, student.getAltPhone());
				pDao.updateStudentDetails(student, mailer);// Update Details in exam.student Table //
				student = pDao.getSingleStudentsData(userId);
				String programForHeader =  (String) request.getSession().getAttribute("programForHeaderPortal");
				student.setProgramForHeader(programForHeader);
				request.getSession().setAttribute("student_studentportal", student);
				if("PROD".equalsIgnoreCase(ENVIRONMENT)){
				//update IdCard after the student update
				idCardService.updateIdCard(student);
				}

			}else
			{
				pDao.updateErrorFlag(userId,errorMessage,mailer);
				request.setAttribute("error","true");
				request.setAttribute("errorMessage",errorMessage);
				return modelnView;
			}
		}catch(Exception e){
			pDao.updateErrorFlag(userId,"Confirm Details:Error in updating profile.",mailer);
			request.setAttribute("error","true");
			request.setAttribute("errorMessage",errorMessage);
			return modelnView;
		}

		if(fromProfileIcon){
			request.setAttribute("success","true");
			request.setAttribute("successMessage","Profile updated successfully!");
			modelnView.addObject("student", student );
			modelnView.addObject("industryList", industryList);
			modelnView.addObject("designationList", designationList);
			modelnView.addObject("fromProfileIcon", fromProfileIcon);
			modelnView.addObject("showShippingAddress", showShippingAddress);
			return modelnView;
		}else{
			request.getSession().setAttribute("getSingleStudentsData_studentportal",student);
			
			return executePostAuthenticationActivities(request,response, 
					userId, (String)request.getSession().getAttribute("password"),false);
		}
	}

	//for testing purpose added for direct url hit instead of backjob.

	/*@RequestMapping(value = "/refundTransaction", method = {RequestMethod.GET})
		public String initiateRefund(HttpServletRequest request) {
			String tracking_id = request.getParameter("tracking_id");
			String transaction_id = request.getParameter("transaction_id");
			String refund_amount = request.getParameter("refund_amount");
			PaymentHelper paymentHelper = new PaymentHelper();
			paymentHelper.refundInitiate(tracking_id, transaction_id, refund_amount);
			return "RequestTesting";
		}


		@RequestMapping(value = "/refundStatus", method = {RequestMethod.GET})
		public String refundStatus(HttpServletRequest request) {
			String tracking_id = request.getParameter("tracking_id");
			String refId = request.getParameter("refId");
			PaymentHelper paymentHelper = new PaymentHelper();
			paymentHelper.refundStatus(tracking_id, refId);
			return "RequestTesting";
		}*/

	@RequestMapping(value = "/admin/payuTransactionStatus", method = {RequestMethod.GET})
	public @ResponseBody String payuTransactionStatus(HttpServletRequest request) {
		String tracking_id = request.getParameter("tracking_id");
		JsonObject responseData = paymentHelper.getPayuTransactionStatus(tracking_id);
		return responseData.toString();
	}

	@RequestMapping(value = "/admin/paytmTransactionStatus", method = {RequestMethod.GET})
	public @ResponseBody String paytmTransactionStatus(HttpServletRequest request) {
		String tracking_id = request.getParameter("tracking_id");
		JsonObject responseData = paymentHelper.getPaytmTransactionStatus(tracking_id);
		return responseData.toString();
	}
	
	@RequestMapping(value = "/admin/hdfcTransactionStatus", method = {RequestMethod.GET})
	public ResponseEntity<String> hdfcTransactionStatus(HttpServletRequest request) {
		String tracking_id = request.getParameter("tracking_id");
		HttpHeaders headers = new HttpHeaders();
		try {
			String response = new XMLParser().queryTransactionStatus(tracking_id, ACCOUNT_ID, SECURE_SECRET);
			headers.add("Content-Type", "application/xml");
			return new ResponseEntity<String>(response, headers, HttpStatus.OK);
		}catch (Exception e) {
//			e.printStackTrace();
			return new ResponseEntity<String>("Exception fetching data : " + e.getMessage(), headers, HttpStatus.OK);
		}
	}
	/*@RequestMapping(value = "/payuRefundStatus1", method = {RequestMethod.GET})
		public String payuRefundStatus1(HttpServletRequest request) {
			String tracking_id = request.getParameter("tracking_id");
			PaymentHelper paymentHelper = new PaymentHelper();
			paymentHelper.payuRefundStatus(tracking_id);
			return "RequestTesting";
		}*/	

		
public List<AnnouncementStudentPortalBean> getMentionedDataBySapid(String sapid) {				
			
			List<AnnouncementStudentPortalBean> aBeanList = new ArrayList<AnnouncementStudentPortalBean>();
			List<MentionedDataBean> mdBeanList = new ArrayList<MentionedDataBean>();
			PortalDao pDao = (PortalDao)act.getBean("portalDAO");
			try {
				mdBeanList = pDao.getMentionedDataBySapid(sapid);
			
			}catch(Exception e) {
//				e.printStackTrace();
			}	

			for (MentionedDataBean mdBean : mdBeanList) {
				AnnouncementStudentPortalBean  aBean = new AnnouncementStudentPortalBean();
				aBean.setSapid(mdBean.getSapid());
				if(mdBean.getVisibility().equals("0")) {
					aBean.setActive("Y");	
				}else {
					aBean.setActive("N");
				}
				aBean.setCategory("Activity");
				if(Integer.parseInt(mdBean.getMaster_comment_id()) == 0 ) {
					aBean.setComment_id(mdBean.getComment_id());
				}else {
					aBean.setComment_id(Integer.parseInt(mdBean.getMaster_comment_id()));
				}
				aBean.setCreatedDate(mdBean.getCreatedDate());
				aBean.setStartDate(mdBean.getCreatedDate());
				aBean.setLastModifiedDate(mdBean.getLastModifiedDate());
				aBean.setMentionBy(mdBean.getMentionBy());	
				aBean.setPost_id(mdBean.getPost_id());
				aBean.setComment(mdBean.getComment());
				
				aBeanList.add(aBean);
			}
			return aBeanList;
		}


@RequestMapping(value = "/student/postCourseQuery", method = { RequestMethod.POST })
public ModelAndView postCourseQuery(HttpServletRequest request, HttpServletResponse response,
							  @ModelAttribute SessionQueryAnswerStudentPortal sessionQuery) throws UnsupportedEncodingException {
	
	if(!checkSession(request, response)){
		return new ModelAndView("jsp/login");
	}
	
//	String updatedSubject= URLEncoder.encode( sessionQuery.getSubject(), "UTF-8" ); commented by Abhay for change subject name to programSemSubjectId in redirect URL
	String sapId = (String) request.getSession().getAttribute("userId");
	String redirectURL = "/student/viewCourseDetails?programSemSubjectId="+sessionQuery.getProgramSemSubjectId()+"&activeMenu=qna";
	ModelAndView modelAndView = new ModelAndView("jsp/common/redirectPage");
	modelAndView.addObject("redirectURL", redirectURL);
	// Get Latest Acads Year, month via ExamOrderBean having max order
	ExamOrderStudentPortalBean examorderBean = sessionQueryAnswerDAO.getExamOrderBeanWhereContentLive();

	if (examorderBean == null) {			
		modelAndView.addObject("redirectURL", redirectURL+"&queryError=y&queryMsg=Error in posting query try again later.");
		modelAndView.setViewName(modelAndView.getViewName());
		return modelAndView;
	}

	// Get facultyId of faculty that query will be assigned to


	String facultyId = sessionQuery.getFacultyId();

	sessionQuery.setFacultyId(facultyId);

	if (StringUtils.isBlank(facultyId)) {
		facultyId = sessionQueryAnswerDAO.getFaultyIdToAnswerCourseQuery(sessionQuery.getSubject());				
		modelAndView.addObject("redirectURL", redirectURL+"&queryError=y&queryMsg=Faculty Not Yet Assgined To This Course Try Again Later...");
		modelAndView.setViewName(modelAndView.getViewName());
		return modelAndView;
	}


	if (facultyId == null) {			
		modelAndView.addObject("redirectURL", redirectURL+"&queryError=y&queryMsg=Error in posting query try again later.");
		modelAndView.setViewName(modelAndView.getViewName());
		return modelAndView;
	}
	sessionQuery.setFacultyId(facultyId);
	sessionQuery.setAssignedToFacultyId(facultyId);
	sessionQuery.setSapId(sapId);
	sessionQuery.setQueryType("Course Query");
	
	if(registrationHelper.twoAcadCycleCourses(request)) {
		StudentStudentPortalBean studentReg=(StudentStudentPortalBean) request.getSession().getAttribute("studentRegistrationForAcademicSession_studentportal");
		sessionQuery.setYear(studentReg.getYear());
		sessionQuery.setMonth(studentReg.getMonth());
	}else {
		sessionQuery.setYear(String.valueOf(examorderBean.getYear()));
		sessionQuery.setMonth(examorderBean.getAcadMonth());
	}


	long studentZone_QueryId;
	try {
		Integer count = sessionQueryAnswerDAO.checkForSameAskQuery(sessionQuery);
		if(count > 0) {
			modelAndView.addObject("redirectURL", redirectURL+"&queryError=y&queryMsg=This Query Already asked by you please check in My Queries Tab.");
			modelAndView.setViewName(modelAndView.getViewName());
			return modelAndView;
		}

		studentZone_QueryId = sessionQueryAnswerDAO.saveQuery(sessionQuery);
		setSuccess(request, "Query submitted successfully");
		loggerForCourseQueryNotificationToFaculty.info("Course Query saved to database session_query_answer_id : "+studentZone_QueryId+" sapid : "+sessionQuery.getSapId()+" Student Query: "+sessionQuery.getQuery());
	} catch (Exception e1) {
		// TODO Auto-generated catch block
		loggerForCourseQueryNotificationToFaculty.error("Course Query not saved to database sapid : "+sessionQuery.getSapId()+", Student Query: "+sessionQuery.getQuery()+"  Error message : "+e1.getMessage());
//		e1.printStackTrace();			
		modelAndView.addObject("redirectURL", redirectURL+"&queryError=y&queryMsg=Unable to submit your query. Please try again...");
		modelAndView.setViewName(modelAndView.getViewName());
		return modelAndView;
	}

	sessionQuery.setId(String.valueOf(studentZone_QueryId));

	FacultyDAO fDao = (FacultyDAO) act.getBean("facultyDAO");

	MailSender mailSender = (MailSender) act.getBean("mailer");

	StudentStudentPortalBean student = (StudentStudentPortalBean) request.getSession().getAttribute("student_studentportal");

	try {
		FacultyStudentPortalBean faculty = fDao.findfacultyByFacultyId(facultyId);
		HashMap<String,ProgramSubjectMappingStudentPortalBean> programSubjectPassingConfigurationMap = sessionQueryAnswerDAO.getProgramSubjectPassingConfigurationMap();
		String programSubjectProgramStructureKey = student.getProgram()+"-"+sessionQuery.getSubject()+"-"+student.getPrgmStructApplicable();
		ProgramSubjectMappingStudentPortalBean passingConfiguration = programSubjectPassingConfigurationMap.get(programSubjectProgramStructureKey);
		//Added by steffi to create case in salesforce instead of sending email for VA/EMiner/EGuide/ABC.
		if("Y".equalsIgnoreCase(passingConfiguration.getCreateCaseForQuery())){
			// Create Case In Salesforce
			loggerForCourseQueryNotificationToFaculty.info("creating case on salesforce sapid : "+sessionQuery.getSapId()+" session_query_answer_id : "+studentZone_QueryId+" Student Query: "+sessionQuery.getQuery());
			if("PROD".equalsIgnoreCase(ENVIRONMENT)){
				sessionQuery = salesforceHelper.createCaseInSalesforce(student, sessionQuery);
				sessionQueryAnswerDAO.updateSalesforceErrorMessage(sessionQuery);// update CaseId and ErrorMessage in Table
			}
		}else{
			mailSender.sendCourseQueryPostedEmail(sessionQuery, faculty.getEmail());
		}

	} catch (Exception e) {
		loggerForCourseQueryNotificationToFaculty.error("sapid : "+ sessionQuery.getSapId()+
				" session_query_answer_id : "+sessionQuery.getId()+", Student Query: "+sessionQuery.getQuery()+" Error message : "+e.getMessage());
		// TODO Auto-generated catch block
//		e.printStackTrace();
	}
	modelAndView.addObject("redirectURL", redirectURL+"&queryError=n&queryMsg=Successfully posted the query.");
	modelAndView.setViewName(modelAndView.getViewName());
	return modelAndView;
}

	
//	@RequestMapping(value = "/m/getIndustryDesignation", method = RequestMethod.GET)
//	private ResponseEntity<StudentBean> getIndustryDesignation(){
//		
//		HttpHeaders headers = new HttpHeaders();
//		headers.add("Content-Type", "application/json");
//		PortalDao pDao = (PortalDao)act.getBean("portalDAO");
//		
//		StudentBean response = new StudentBean();
//		ArrayList<String> industryList = pDao.getIndustryList();
//		
//		response.setDesignationList(designationList);
//		response.setIndustryList(industryList);
//		
//		return new ResponseEntity<StudentBean>(response, headers, HttpStatus.OK);
//	}
	
	public String generateMettlLink(MettlExamUpcomingBean booking) throws Exception {
		TreeMap<String, String> params = new TreeMap<String, String>();
		
		// fields for booking update
		params.put("sapid", booking.getSapid());
		params.put("emailId", booking.getEmailId());
		params.put("imageUrl", booking.getImageURL());
		
		params.put("subject", booking.getSubject());
		params.put("year", booking.getYear());
		params.put("month", booking.getMonth());
		params.put("trackId", booking.getTrackId());

		if(!StringUtils.isBlank(booking.getJoinURL()) && booking.getJoinURL().contains("ecc=")) {
			params.put("ecc", booking.getJoinURL().split("ecc=")[1]);
		} else {
			params.put("accessUrl", booking.getJoinURL());
		}
		
		params.put("scheduleId", booking.getScheduleId());
		
		params.put("startTime", booking.getExamStartDateTime());
		params.put("reportingStartTime", booking.getReporting_start_date_time());
		params.put("endTime", booking.getExamEndDateTime());
		params.put("accessEndTime", booking.getAccessEndDateTime());
		
		params.put("firstname", booking.getFirstname());
		params.put("lastname", booking.getLastname());
		
		return SERVER_PATH + "ltidemo/mettl_sso_student?joinKey=" + URLEncoder.encode(encryptParameters(params), "UTF-8");
	}
	
	private String encryptParameters(TreeMap<String, String> params) throws Exception {
		try {
			List<String> valuesToEncrypt = new ArrayList<String>();
			for (Entry<String, String> entry : params.entrySet()) {
				String key = entry.getKey();
				String value = entry.getValue();
				if(!StringUtils.isBlank(key) && !StringUtils.isBlank(value)) {
					valuesToEncrypt.add(key + "=" + value);	
				}
			}
			String data = StringUtils.join(valuesToEncrypt, "\n");
			
			String encryptedData = AESencrp.encrypt(data);
			String encryptedDataBase64 = getStringBase64Encoded(encryptedData);
			return encryptedDataBase64;
		}catch (Exception e) {
//			e.printStackTrace();
			throw new Exception("Error Encrypting Parameters");
		}
	}
	private String getStringBase64Encoded(String input) {
		return new String(Base64.encodeBase64(input.getBytes()));
	}

	//Commented by Somesh as not required for now
	//Created API for download content 
	//@RequestMapping(value ="/api/downloadResourceContent/{contextId}",  method = RequestMethod.GET)
    public ResponseEntity<ByteArrayResource> downloadResourceContent(@PathVariable("contextId") String contextId) throws IOException 
    {
		
		String filename = contextId.split("_")[0];
		String extension= contextId.split("_")[1];
		if (extension.equalsIgnoreCase("pdf")) {
			contextId = filename + ".pdf";
		}else if (extension.equalsIgnoreCase("zip")) {
			contextId = filename + ".zip";
		}if (extension.equalsIgnoreCase("rar")) {
			contextId = filename + ".rar";
		}
		        
		try {
			if (downloaded < 10 ) {
				File file = new File("E:/ExtDownloadContent/"+contextId);
		        HttpHeaders header = new HttpHeaders();
		        header.add("Access-Control-Allow-Origin", "*");
		        header.add("Access-Control-Allow-Methods", "GET, POST, PUT");
		        header.add(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename="+contextId );
		        header.add("Cache-Control", "no-cache, no-store, must-revalidate");
		        header.add("Pragma", "no-cache");
		        header.add("Expires", "0");

		        Path path = Paths.get(file.getAbsolutePath());
		        ByteArrayResource resource = new ByteArrayResource(Files.readAllBytes(path));

		        downloaded++;
		        return ResponseEntity.ok().headers(header)
		                        		  .contentLength(file.length())
		                        		  .contentType(MediaType.parseMediaType("application/octet-stream"))
		                        		  .body(resource);
			}else {
				return null;
			}
		} catch (Exception e) {
//			e.printStackTrace();
			return null;
		}
    } 
 
//    @RequestMapping(value = "/m/CheckDemoExamStatus", method = RequestMethod.POST, consumes = "application/json", produces = "application/json")
//	public ResponseEntity<HashMap<String, String>> mCheckDemoExamStatus(HttpServletRequest request, @RequestBody StudentBean student) throws Exception {
//
//		HashMap<String, String> response = new HashMap<>();
//		PortalDao pDao = (PortalDao)act.getBean("portalDAO");
//		HttpHeaders headers = new HttpHeaders();
//		headers.add("Content-Type", "application/json");
//		response.put("isDemoExamPending", "false");
//		return new ResponseEntity<>(response, headers, HttpStatus.OK);
//		/*boolean result = pDao.isDemoExamPending(student.getSapid());
//		if (result) {
//			response.put("isDemoExamPending", "true");
//			return new ResponseEntity<>(response, headers, HttpStatus.OK);
//		}else{
//			response.put("isDemoExamPending", "false");
//			return new ResponseEntity<>(response, headers, HttpStatus.OK);
//		}*/
//
//	} 
    
//    @RequestMapping(value = "/m/getLocation", method = RequestMethod.POST, consumes = "application/json", produces = "application/json")
//	public ResponseEntity<StudentBean> getLocation(@RequestBody StudentBean student){
//
//    	StudentBean response = new StudentBean();
//		HttpHeaders headers = new HttpHeaders();
//		headers.add("Content-Type", "application/json");
//		
//		try {
//			
//			leadDAO.updateLeadLocation();
//			response.setError(false);
//			response.setErrorMessage("Completed");		
//			return new ResponseEntity<>(response, headers, HttpStatus.OK);
//			
//		} catch (ConnectionException e) {
//			
//			response.setError(true);
//			response.setErrorMessage("Error");
//			e.printStackTrace();
//			return new ResponseEntity<>(response, headers, HttpStatus.INTERNAL_SERVER_ERROR);
//		}
//
//
//	}

  //faculty profile view
    @RequestMapping(value = "/facultyProfile", method = {RequestMethod.GET})
	public ModelAndView facultyProfile(HttpServletRequest request, HttpServletResponse response) {
		if(!checkSession(request, response)){
			return new ModelAndView("jsp/login");
		}	
		
		ModelAndView modelnView = null;
		try{
			
			StudentStudentPortalBean student = (StudentStudentPortalBean)request.getSession().getAttribute("student_studentportal");
			if(student != null) {
				modelnView = new ModelAndView("jsp/facultyView");
			}else {
				modelnView = new ModelAndView("jsp/facultyViewAdmin");
			}
			PortalDao pDao = (PortalDao)act.getBean("portalDAO");
			String facultyId = (String)request.getParameter("facultyId");
			FacultyStudentPortalBean faculty = pDao.getFacultyData(facultyId);

			modelnView.addObject("faculty", faculty );
			request.getSession().setAttribute("faculty",faculty);			
		}catch(Exception e){
//			e.printStackTrace();
			new ModelAndView("jsp/login");
		}		
		
		return modelnView;
	}
    
//    @RequestMapping(value = "/m/getTodaysSessions", method = RequestMethod.POST, consumes = "application/json", produces = "application/json")
//	public ResponseEntity<ArrayList<SessionDayTimeBean>> getTodaysSessions(HttpServletRequest request,@RequestBody Person input) throws Exception {
//
//    	String userId = input.getSapId();
//		HttpHeaders headers = new HttpHeaders();
//		headers.add("Content-Type", "application/json");
//		PortalDao pDao = (PortalDao)act.getBean("portalDAO");
//		ArrayList<SessionDayTimeBean> sessionList = new ArrayList<SessionDayTimeBean>();
//		ArrayList<String> currentSemPSSId = new ArrayList<String>();
//		
//		StudentBean student = pDao.getSingleStudentsData(userId);
//		StudentBean studentRegistrationForAcademicSession = new StudentBean();
//		
//		//added by Tushar for new PSS id list logic
//		studentRegistrationForAcademicSession = registrationHelper.checkStudentRegistration(userId, student);
//		
//		//added by tushar to fetch common session
//		
//		if (studentRegistrationForAcademicSession != null) {
//			ArrayList<SessionDayTimeBean> commonSessionsList = pDao.getTodaysCommonSessionsByCPSId(studentRegistrationForAcademicSession);
//			sessionList.addAll(commonSessionsList);
//		}
//		
//		if(input.getApplicablePSSId()==null) {
//			try {
//				if (studentRegistrationForAcademicSession != null) {
//					//Set up latest semester
//					student.setSem(studentRegistrationForAcademicSession.getSem());
//					ArrayList<String> waivedOffSubjects = studentService.mgetWaivedOffSubjects(student);
//					currentSemPSSId = pDao.getPSSIds(studentRegistrationForAcademicSession.getConsumerProgramStructureId(), studentRegistrationForAcademicSession.getSem() ,waivedOffSubjects);
//					sessionList.addAll(pDao.getTodaysSessionsByPSSId(currentSemPSSId,studentRegistrationForAcademicSession));
//					if (sessionList.size() > 0) {
//						return new ResponseEntity<>(sessionList,headers, HttpStatus.OK);
//					}
//				}
//			} catch (Exception e) {
//				return new ResponseEntity<>(sessionList,headers, HttpStatus.OK);
//			}
//		}else {
//			sessionList.addAll(pDao.getTodaysSessionsByPSSId(input.getApplicablePSSId(),studentRegistrationForAcademicSession));
//			if (sessionList.size() > 0) {
//				return new ResponseEntity<>(sessionList,headers, HttpStatus.OK);
//			}
//		}
//		
//		return new ResponseEntity<>(sessionList,headers, HttpStatus.OK);
//    }
    


    public StudentStudentPortalBean checkStudentRegistration(String sapId, StudentStudentPortalBean student) {
		
		StudentStudentPortalBean studentRegistrationData = new StudentStudentPortalBean();
		PortalDao pDao = (PortalDao)act.getBean("portalDAO");
		
		studentRegistrationData = pDao.getStudentRegistrationDetails(sapId);

		if ((!"111".equalsIgnoreCase(studentRegistrationData.getConsumerProgramStructureId()))
				&& (!"151".equalsIgnoreCase(studentRegistrationData.getConsumerProgramStructureId()))
				&& (!"131".equalsIgnoreCase(studentRegistrationData.getConsumerProgramStructureId()))
				&& (!"17".equalsIgnoreCase(studentRegistrationData.getConsumerProgramStructureId()))
				&& (!"153".equalsIgnoreCase(studentRegistrationData.getConsumerProgramStructureId()))
				&& (!"150".equalsIgnoreCase(studentRegistrationData.getConsumerProgramStructureId()))
				&& (!"154".equalsIgnoreCase(studentRegistrationData.getConsumerProgramStructureId()))
				&& (!"155".equalsIgnoreCase(studentRegistrationData.getConsumerProgramStructureId()))
				&& (!"156".equalsIgnoreCase(studentRegistrationData.getConsumerProgramStructureId()))
				&& (!"157".equalsIgnoreCase(studentRegistrationData.getConsumerProgramStructureId()))
				&& (!"158".equalsIgnoreCase(studentRegistrationData.getConsumerProgramStructureId()))) {
			String month_curr = "";
			String month_reg = "";
			if(studentRegistrationData.getMonth().equals("Jan"))
				month_reg = "JANUARY";
			else
				month_reg = "JULY";
			
			if(CURRENT_ACAD_MONTH.equals("Jan")) {
				month_curr = "JANUARY";}
			else 
				month_curr = "JULY";
			
			Month reg_m = Month.valueOf(month_reg);	
			Month curr_m = Month.valueOf(month_curr);
			
			YearMonth reg_date = YearMonth.of(Integer.parseInt(studentRegistrationData.getYear()) ,reg_m);
			YearMonth curr_date = YearMonth.of(Integer.parseInt(CURRENT_ACAD_YEAR) ,curr_m);
			
			
			if(reg_date.compareTo(curr_date) < 0) {
				studentRegistrationData = null;
			}
		}
		return studentRegistrationData;
	}

    
    private void generateCourseLearningResourcesMap(HttpServletRequest request, String programSemSubjectId, ModelAndView modelnView,String subject) {

		PortalDao pDao = (PortalDao)act.getBean("portalDAO");
		/*StudentBean student = (StudentBean)request.getSession().getAttribute("student_studentportal");
		LearningResourcesDAO dao = (LearningResourcesDAO) act.getBean("learningResourcesDAO");
		List<ContentBean> contentList = new ArrayList<ContentBean>();*/
	   // List<ContentBean> finalListOfAllContentListForSubject = new ArrayList<ContentBean>();
		List<ContentStudentPortalBean> allContentListForSubject = new ArrayList<ContentStudentPortalBean>();			
		

		try{
			String earlyAccess = (String)request.getSession().getAttribute("earlyAccess");
			//Dao  call Redundancy
			/*StudentBean semCheck = pDao.getStudentsMostRecentRegistrationData(student.getSapid());
			request.getSession().setAttribute("semCheck", semCheck);*/
			
			StudentStudentPortalBean semCheck = (StudentStudentPortalBean)request.getSession().getAttribute("studentRecentReg_studentportal");

			HttpServletResponse response = null;
			//Commented by Riya as EPBM & MPDV inactive
	/*		   if(student.getProgram().equalsIgnoreCase("EPBM")|| student.getProgram().equalsIgnoreCase("MPDV")){

		            allContentListForSubject = pDao.getContentsForSubjectsForCurrentSessionNew(subject,earlyAccess,student,semCheck.getMonth(),semCheck.getYear());

		        
		        }*/
			double acadContentLiveOrder = (double)request.getSession().getAttribute("acadContentLiveOrder");
			double reg_order = (double)request.getSession().getAttribute("reg_order");
			
			
			
			//allContentListForSubject =pDao.getContentsForSubjectsForCurrentSessionNewLR(programSemSubjectId,earlyAccess,acadDateFormat);
			
			allContentListForSubject = contentService.getContentByPssId(reg_order,acadContentLiveOrder,semCheck.getMonth(),semCheck.getYear(),programSemSubjectId,semCheck.getSapid(),true);
			
			
			//Commented by Riya Since course pssid Table created
			/*  String programStructureForStudent = student.getPrgmStructApplicable();
		        for (ContentBean contentBean : allContentListForSubject) {
		            String programStructureForContent = contentBean.getProgramStructure();

		            if ("113".equals(student.getConsumerProgramStructureId())
		                    && "Business Economics".equalsIgnoreCase(subject)) {
		                contentList.add(contentBean);
		            } else if ("127".equalsIgnoreCase(student.getConsumerProgramStructureId())
		                    || "128".equalsIgnoreCase(student.getConsumerProgramStructureId())) {
		                if (programStructureForContent.equals(programStructureForStudent)) {
		                    contentList.add(contentBean);
		                }
		            }

		            else {
		                if (programStructureForContent == null || "".equals(programStructureForContent.trim())
		                        || "All".equals(programStructureForContent)) {

		                    contentList.add(contentBean);
		                } else if (programStructureForContent.equals(programStructureForStudent)) {
		                    contentList.add(contentBean);
		                }
		            }
		        }*/
			
			//Logic shifted in service layer
			/*if(allContentListForSubject.size() > 0) {
			allContentListForSubject = fetchAndInsertBookmarksInContent(allContentListForSubject,semCheck.getSapid());
			}*/
			
			//Commented as for bookmark above method is being created
//			}
//			for(ContentBean contentBean : allContentListForSubject){
//				if(pDao.checkIfBookmarked(student.getSapid(),contentBean.getId())){
//					contentBean.setBookmarked("Y");
//				}
//				contentList.add(contentBean);
//			}

		}catch(Exception e){
//			e.printStackTrace();
		}
		
	    //List<ContentBean> lastCycleContentList = pDao.getRecordingForLastCycle(subject);


	    //Show only Course presentation and Course Material for next batch stuents
	    /*if(earlyAccess != null && "Yes".equals(earlyAccess)){
	        List<ContentBean> prospectStudentContentList = new ArrayList<ContentBean>();
	        for (ContentBean contentBean : contentList) {
	            String contentType = contentBean.getContentType();
	            if("Course Presentation".equalsIgnoreCase(contentType) || "Course Material".equalsIgnoreCase(contentType)){
	                prospectStudentContentList.add(contentBean);
	            }
	        }

	        contentList = prospectStudentContentList;
	        //For next batch students, current recordings will be considered as last cycle recordings
	        lastCycleContentList = pDao.getRecordingForCurrentCycle(subject);
	    }

	    request.getSession().setAttribute("lastCycleContentList", lastCycleContentList);
	    modelnView.addObject("lastCycleContentList", lastCycleContentList);*/
		
		
		/*request.getSession().setAttribute("contentList", finalListOfAllContentListForSubject);
		modelnView.addObject("contentList", finalListOfAllContentListForSubject);*/
		request.getSession().setAttribute("contentList", allContentListForSubject);
		modelnView.addObject("contentList", allContentListForSubject);
	}
    
    private void getVideoContentForSubject(HttpServletRequest request,String programSemSubjectId) {
        PortalDao pdao = (PortalDao)act.getBean("portalDAO");
        List<VideoContentStudentPortalBean> videoContentList=null;
        //List<VideoContentBean> finalVideoContentList=new ArrayList<>();
        String acadDateFormat = "";
        try {
            StudentStudentPortalBean student = (StudentStudentPortalBean)request.getSession().getAttribute("student_studentportal");
            String earlyAccess = (String)request.getSession().getAttribute("earlyAccess");
            StudentStudentPortalBean studentRecentData = (StudentStudentPortalBean) request.getSession().getAttribute("studentRecentReg_studentportal");

			//For 2 Acad Content Live
			double acadContentLiveOrder = (double)request.getSession().getAttribute("acadContentLiveOrder");
			double reg_order = (double)request.getSession().getAttribute("reg_order");
			//StudentBean studentRecentData = pdao.getStudentsMostRecentRegistrationData(student.getSapid());
			
           
            

       
            	acadDateFormat = ContentUtil.getCorrectOrderAccordTo2AcadContentLive(acadContentLiveOrder, reg_order, 
            			studentRecentData.getMonth(),studentRecentData.getYear(),CURRENT_ACAD_MONTH,CURRENT_ACAD_YEAR, true);
            	
            	
                //videoContentList = pdao.getVideoContentForSubjectNewForLR(programSemSubjectId, earlyAccess,acadContentLiveOrder,reg_order,studentRecentData.getMonth(),studentRecentData.getYear());
                videoContentList = homeService.getVideos(programSemSubjectId, acadDateFormat,earlyAccess);
            
			/*for(VideoContentBean videoContentBean : videoContentList){
			    if(pdao.checkIfBookmarked(student.getSapid(),videoContentBean.getId().toString())){
			        videoContentBean.setBookmarked("Y");
			    }
			    finalVideoContentList.add(videoContentBean);
			}*/
            
            if (videoContentList != null && videoContentList.size() > 0) {
            	videoContentList = prepareVideoContentBookmark(videoContentList, studentRecentData.getSapid());
			}
            
            request.getSession().setAttribute("videoContentList", videoContentList);
        } catch (Exception e) {
//            e.printStackTrace();
        }
    }
    

    
    //Commented Fot now , it is LR Config for mobile Api
    /*private ArrayList<ContentBean> mgenerateCourseLearningResourcesMapNewForLR(String sapid) {
		
		PortalDao pDao = (PortalDao)act.getBean("portalDAO");
		StudentInfoCheckDAO studentDao = (StudentInfoCheckDAO)act.getBean("stuentInfoCheckDAO");
		//StudentBean studentDeatil = studentDao.getstudentData(student.getSapid());
		//String programSemSubjectId = pDao.getProgramSemSubjectId(studentDeatil);
		StudentBean studentDetail  = pDao.getStudentsMostRecentRegistrationData(sapid);
		String earlyAccess = checkEarlyAccess(sapid);
		ArrayList<ContentBean> allContentListForSubject = null;//(ArrayList<ContentBean>) pDao.getContentsForSubjectsForCurrentSessionNewLR(programSemSubjectId,earlyAccess, studentDetail.getMonth(),studentDetail.getYear());
		 
		return allContentListForSubject;
	}*/

    
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
    		
    		
    		contentList = contentService.getContentByPssId(reg_order,acadContentLiveOrder,studentreg.getMonth(),studentreg.getYear(),programSemSubjectId,studentreg.getSapid(),true);
    			
    		//Logic shifted to service layer
    		//contentList = pDao.getContentsForSubjectsForCurrentSessionNewLR(programSemSubjectId,earlyAccess,acadDateFormat);

    	}catch(Exception e){
//    		e.printStackTrace();
    	}
    	return (ArrayList<ContentStudentPortalBean>) contentList;

    }
    
   
    
    
	
    
    public List<VideoContentStudentPortalBean> prepareVideoContentBookmark(List<VideoContentStudentPortalBean> videoContentList, String sapid){
		String commaSaperatedContentIds = null;
		
		commaSaperatedContentIds =	videoContentList.stream()
				.map(videoContent -> String.valueOf(videoContent.getId()))
				.collect(Collectors.joining(","));
		
		//get bookmark's status of all Content
		List<VideoContentStudentPortalBean> contentBookmarkIds = contentDAO.getBookmarksForVideo(commaSaperatedContentIds,sapid);
		
		//set bookmark's status in original ContentList
		videoContentList.forEach(myObject1 -> contentBookmarkIds.stream()
	            .filter(myObject2 -> myObject1.getId().equals(myObject2.getId()))
	            .findAny().ifPresent(myObject2 -> myObject1.setBookmarked(myObject2.getBookmarked())));
		
		return videoContentList;
	}

    
    /**
     * validates if the captcha response is valid
     * @author Raynal Dcunha
     * @param captcha string via the jsp
     * @return true/false depending if the captcha is valid
     */
    public boolean isValidCaptcha(String captcha) {
		String url= "https://www.google.com/recaptcha/api/siteverify";
		String params = "?secret=" + WEB_RECAPTCHA_SECRET_KEY + "&response=" + captcha;
		String completeUrl = url + params;
		

		RestTemplate restTemplate = new RestTemplate();
		CaptchaResponse response = restTemplate.postForObject(completeUrl, null, CaptchaResponse.class);
	
		return response.isSuccess();
	}
    

    //Added by Saurabh to update pssId in session_query_answer table
    @RequestMapping(value = "/updatePssIdInSessionQueryAnswer", method = {RequestMethod.GET, RequestMethod.POST})
    public void updatePssIdInSessionQuerAnwer() {
    	PortalDao pDao = (PortalDao)act.getBean("portalDAO");
    	
    	///get All masterKeys for students in session_query_answer table
    	ArrayList<Integer> masterKeyList=pDao.getConsumerPgmStrIds();
    	ArrayList<SessionQueryAnswerStudentPortal> psssIdWithSubject=new ArrayList<>();
    	ArrayList<SessionQueryAnswerStudentPortal> sessionQnAList=new ArrayList<>();
    	
    	for(int consumerProgramStructureId: masterKeyList){
    		psssIdWithSubject=pDao.getPssIdandSubject(consumerProgramStructureId);
    		sessionQnAList=pDao.getQnAIdAndSubject(consumerProgramStructureId);
    		
    		for(SessionQueryAnswerStudentPortal pssObj: psssIdWithSubject) {
    			ArrayList<String> sessionQueryAnswerIdList=new ArrayList<>();
    			String subject1=pssObj.getSubject();
    			
    			for(SessionQueryAnswerStudentPortal sessionIdObj: sessionQnAList) {
    				String subject2=sessionIdObj.getSubject();
    				if(subject1.equals(subject2)) {
    			
    					sessionQueryAnswerIdList.add(sessionIdObj.getId());
    				}
    			}
				pDao.updatePssIdForQnA(sessionQueryAnswerIdList, pssObj.getId());
				sessionQueryAnswerIdList.clear();
    			
    		}
    	}
    }

    @RequestMapping(value = "/admin/synchronizePendingTransactions", method = RequestMethod.GET)
	public void synchronizePendingTransactions() {
    	System.out.println("------>>>> synchronizePendingTransactions STARTED <<<<------");
    	serviceRequestPaymentScheduler.synchronizePendingTransactions();
    	System.out.println("------>>>> synchronizePendingTransactions END <<<<------");
	}
    
    @RequestMapping(value = "/admin/getadmin", method = RequestMethod.GET)
	public void getadmin() {
		System.out.println("admin");
	}
	
	@RequestMapping(value = "/student/getstudent", method = RequestMethod.GET)
	public void getstudent() {
		System.out.println("student");
	}  
	
	private void getAssignments(StudentStudentPortalBean student,HttpServletRequest request) {

		if(checkIfMovingResultsToCache()) {
			return;
		}
		
		AssignmentsDAO dao = (AssignmentsDAO)act.getBean("asignmentsDAO");
		List<AssignmentStudentPortalFileBean> quickAssignments = dao.getQuickAssignmentsForSingleStudent(student.getSapid());
		if(!"Online".equalsIgnoreCase(student.getExamMode())){
			quickAssignments = new ArrayList<>();
		}
		

    	// For ANS cases, where result is not declared, failed subject will also be
		// present in Current sem subject.
		// Give preference to it as Failed, so that assignment can be submitted and
		// remove from Current list
		ArrayList<AssignmentStudentPortalFileBean> quickAssignmentFilesList=new ArrayList<AssignmentStudentPortalFileBean>();
		AssignmentLiveSettingStudentPortal resitLive = dao.getCurrentLiveAssignment(student.getConsumerProgramStructureId(),"Resit");
		ArrayList<String> failSubjects = new ArrayList<String> ();
		//get all fail subjects
		for(AssignmentStudentPortalFileBean q: quickAssignments){
		if(!q.getCurrentSemSubject().equalsIgnoreCase("Y") && resitLive.getExamYear().equalsIgnoreCase(q.getYear()) && resitLive.getExamMonth().equalsIgnoreCase(q.getMonth())){
			failSubjects.add(q.getSubject()); 
					quickAssignmentFilesList.add(q); 
				}
		}
		//get all current sem subjects removing failsubjects
		//get most recent marks live from cache
        String asgMarksLiveMonth = dao.getLiveAssignmentMarksMonth();
        String asgMarksLiveYear = dao.getLiveAssignmentMarksYear();
        String marksLiveYearMonth = asgMarksLiveMonth+"-"+asgMarksLiveYear;
        for(AssignmentStudentPortalFileBean q: quickAssignments){
            if(q.getCurrentSemSubject().equalsIgnoreCase("Y") && !(failSubjects.contains(q.getSubject())) 
                    // If result is live, hide assignments
                    && !(q.getMonth()+"-"+q.getYear()).equalsIgnoreCase(marksLiveYearMonth)){
                 quickAssignmentFilesList.add(q); 
            }
        }
		request.getSession().setAttribute("quickAssignments_studentportal", quickAssignmentFilesList); 
		 
	}
	private List<AssignmentStudentPortalFileBean> mgetAssignments(StudentStudentPortalBean student, PortalDao pDao, StudentMarksBean studentRegistrationData) {

		String sapId = student.getSapid();
		AssignmentsDAO adao = (AssignmentsDAO) act.getBean("asignmentsDAO");
		List<AssignmentStudentPortalFileBean> quickAssignments = adao.getQuickAssignmentsForSingleStudent(student.getSapid());
		
		// For ANS cases, where result is not declared, failed subject will also be
		// present in Current sem subject.
		// Give preference to it as Failed, so that assignment can be submitted and
		// remove from Current list
		ArrayList<AssignmentStudentPortalFileBean> quickAssignmentFilesList=new ArrayList<AssignmentStudentPortalFileBean>();
		AssignmentLiveSettingStudentPortal resitLive = adao.getCurrentLiveAssignment(student.getConsumerProgramStructureId(),"Resit");
		ArrayList<String> failSubjects = new ArrayList<String> ();
		//get all fail subjects
		for(AssignmentStudentPortalFileBean q: quickAssignments){
			if(!q.getCurrentSemSubject().equalsIgnoreCase("Y") && resitLive.getExamYear().equalsIgnoreCase(q.getYear()) && resitLive.getExamMonth().equalsIgnoreCase(q.getMonth())){
				failSubjects.add(q.getSubject()); 
				quickAssignmentFilesList.add(q); 
			}
		}
		//get all current sem subjects removing failsubjects
		for(AssignmentStudentPortalFileBean q: quickAssignments){
	    	if(q.getCurrentSemSubject().equalsIgnoreCase("Y") && !(failSubjects.contains(q.getSubject())) ){
	    		quickAssignmentFilesList.add(q); 
	    	}
	    } 
		/*
		 * Added by Pranit on 24 Dec 18 to hide assignments on dashboard for offline students 
		 * */
		if(!"Online".equalsIgnoreCase(student.getExamMode())){
			quickAssignments = new ArrayList<>();
		}


		return quickAssignments;


	}
	
	@RequestMapping(value = "/student/doPost", method = RequestMethod.POST)
	public ModelAndView doPost(@RequestParam Map<String,String> allRequestParams) {
		String destination = "/exam/examFeesReponse";
		ModelAndView mv = new ModelAndView("jsp/doPost");
		mv.addObject("allRequestParam", allRequestParams);
		mv.addObject("destination", destination);
		return mv;
	}
	
	
	
	private void getCourses_new(StudentStudentPortalBean student, HttpServletRequest request, StudentMarksBean studentRegistrationData,double current_order,double acadContentLiveOrder,double reg_order) {

		StudentCourseMappingBean  studentCourse = new StudentCourseMappingBean();
		
		
		try{
		studentCourse = studentCourseService.getcourses(student, studentRegistrationData, current_order, acadContentLiveOrder, reg_order);
		
		}catch(Exception e){
			courses_logger.info("Error in getting courses For Sapid "+student.getSapid()+" in method getCourses_new :- ",e);
		}
		if (studentCourse.getCurrentSemSubjectsmap().size() != 0 ) {
			request.getSession().setAttribute("type", "Ongoing ");
		} else if (studentCourse.getFailedSubjectListsmap().size() != 0 ){
			request.getSession().setAttribute("type", "Backlog ");
		}else {
			request.getSession().setAttribute("type", "");
		}
		
		request.getSession().setAttribute("isRedis", studentCourse.getIsRedis());
		request.getSession().setAttribute("failedSubjects", new ArrayList<>(studentCourse.getFailedSubjectListsmap().values()));
		request.getSession().setAttribute("currentSemSubjects_studentportal", new ArrayList<>(studentCourse.getCurrentSemSubjectsmap().values()));
		request.getSession().setAttribute("studentCourses_studentportal", new ArrayList<>(studentCourse.getListOfApplicableSUbjectssmap().values()));
		request.getSession().setAttribute("programSemSubjectIdWithSubjects_studentportal", studentCourse.getListOfApplicableSUbjectssmap());
		request.getSession().setAttribute("programSemSubjectIdWithSubjectForBacklog", studentCourse.getFailedSubjectListsmap());// added by sachin
		request.getSession().setAttribute("programSemSubjectIdWithSubjectForCurrentsem", studentCourse.getCurrentSemSubjectsmap());//added by sachin
		request.getSession().setAttribute("subjectCodeId_studentportal", studentCourse.getSubjectCodeId());
	}
	
	@RequestMapping(value = "/ugConsent", method = RequestMethod.POST)
	public ModelAndView ugConsent(HttpServletRequest request, HttpServletResponse response,@RequestParam("optionId") String optionId,@RequestParam("sapid") String sapid) throws Exception {
		if (!checkSession(request, response)) {
			return new ModelAndView("jsp/login");
		}
		try {
			request.getSession().setAttribute("isLoginAsLead","false");
			String userId = (String)request.getSession().getAttribute("userId");
			String password =(String)request.getSession().getAttribute("password");
			supportService.insertUgConsentForm(optionId, sapid);
			setSuccess(request, "Form Submitted successfully. ");
			return executePostAuthenticationActivities(request,response, userId,password,false);
		}catch(Exception e) {
			setError(request, "Error in Submitting Form. Error:- "+e.getMessage()+" . Please try again login.");
			return new ModelAndView("redirect:/");
		}
	}

	/* Purpose :- To get Sr List. 
	 * Condition :- i)After 15 Days Of Closed Date, Sr should not Show on Home Page. 
	 * @Param   sapid - Sapid Of Students.              
	 * */
	public void getNewSrList(String sapid,HttpServletRequest request){
		ServiceRequestDao serviceRequestDao = (ServiceRequestDao)act.getBean("serviceRequestDao");
		List<ServiceRequestStudentPortal>srList = serviceRequestDao.getStudentsSR(sapid);
		
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
		LocalDateTime currentDate = LocalDateTime.now();
		HashMap<String,String> mapOfSRTypesAndTAT = getMapOfSRTypesAndTAT();
		List<ServiceRequestStudentPortal>newSrList = new ArrayList<ServiceRequestStudentPortal>();
	
		for(ServiceRequestStudentPortal sr : srList) {	
			//get Expected Closed Date By "tat"
			if(!StringUtils.isBlank(mapOfSRTypesAndTAT.get(sr.getServiceRequestType()))) {
				int days = Integer.parseInt(mapOfSRTypesAndTAT.get(sr.getServiceRequestType()));
				LocalDateTime expectedClosedDate = LocalDateTime.parse(sr.getCreatedDate(), formatter).plus(Period.ofDays(days));
				sr.setExpectedClosedDate(expectedClosedDate.toString().replace("T"," "));
			}else {
				sr.setExpectedClosedDate("");
			}
				//Get Service Request to show for sapid
			 if(!StringUtils.isBlank(sr.getRequestClosedDate())){
				 LocalDateTime newDate = LocalDateTime.parse(sr.getRequestClosedDate(), formatter).plus(Period.ofDays(15));
				 if(currentDate.compareTo(newDate) < 0){				 
					 newSrList.add(sr);
				 }
            }else {
            	 sr.setRequestClosedDate("");
            	 newSrList.add(sr);
            }
		}
		request.getSession().setAttribute("newSrList", newSrList);	
	}
	
	private void getForumBasedOnSubjectsNew(HttpServletRequest request) {
		ForumResponseBean currentCycleForum = forumService.getForumList(request, request.getParameter("programSemSubjectId"), "current");
		request.getSession().setAttribute("mapOfForumThreadAndReplyCount", currentCycleForum.getReplyCount());
		request.getSession().setAttribute("listOfForumsRelatedToSubjectInSession", currentCycleForum.getForumlist());

		//For Backlogs Subjects
		ForumResponseBean lastCycleForum = new ForumResponseBean();
		ArrayList<String> failedSubjects = (ArrayList<String>) request.getSession().getAttribute("failedSubjects");
		int noOffailedSubjects = failedSubjects != null ? failedSubjects.size() : 0;
		if (noOffailedSubjects > 0) {
			lastCycleForum = forumService.getForumList(request, request.getParameter("programSemSubjectId"), "previous");
		}
		request.getSession().setAttribute("mapOfForumThreadAndReplyCountBacklog", lastCycleForum.getReplyCount());
		request.getSession().setAttribute("listOfForumsRelatedToSubjectInSessionBacklog", lastCycleForum.getForumlist());
	}
	
	private void getLatestBadge(StudentStudentPortalBean student, HttpServletRequest request) {
		OpenBadgesUsersBean badgeBean = new OpenBadgesUsersBean ();
		int size = 0;
		
		try {
			badgeBean = openBadgesService.getDashboardBadgeList(student.getSapid(), Integer.valueOf(student.getConsumerProgramStructureId()));
			
			if(Objects.isNull(badgeBean.getEarnedBadgeList())) {
				size = 0;
			} else {
				size = badgeBean.getEarnedBadgeList().size();
			}
		} catch (Exception e) {
//			e.printStackTrace();
		}
		
		request.getSession().setAttribute("badgeSize", size);
		request.getSession().setAttribute("mybadges", badgeBean);
	}

	@PostMapping("/student/updateSetting")
	 public ResponseEntity<String> updateSetting(HttpServletRequest request,@RequestBody StudentStudentPortalBean bean) {
		try {
			 int value = bean.getIsEnable().equals("true") ? 1:0;
			 studentSettingService.updateStudentSettings(bean.getSettingType(),bean.getSapid(),value);
			 return new ResponseEntity<String>("error",HttpStatus.OK);
		}catch(Exception e) {
			return new ResponseEntity<String>("error",HttpStatus.INTERNAL_SERVER_ERROR);
		}	
	 }
	
	@GetMapping("/student/studentSettings")
	public ModelAndView studentSettings(HttpServletRequest request) {
		ModelAndView model = new ModelAndView("jsp/settingsPage");
		StudentStudentPortalBean studentBean =  studentSettingService.getSingleStudentsData(request);
		model.addObject("studentBean", studentBean);
		return model;
	}
}