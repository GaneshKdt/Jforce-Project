package com.nmims.controllers;

import java.math.BigDecimal;
import java.security.Principal;
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
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.view.RedirectView;

import com.nmims.beans.ExamAnnouncementBean;
import com.nmims.beans.AssignmentLiveSetting;
import com.nmims.beans.ExamOrderExamBean;
import com.nmims.beans.FacultyExamBean;
import com.nmims.beans.Person;
import com.nmims.beans.ProgramSubjectMappingExamBean;
import com.nmims.beans.StudentExamBean;
import com.nmims.beans.TestExamBean;
import com.nmims.beans.UserAuthorizationExamBean;
import com.nmims.daos.AssignmentsDAO;
import com.nmims.daos.BaseDAO;
import com.nmims.daos.CareerServicesDAO;
import com.nmims.daos.CaseStudyDao;
import com.nmims.daos.ExamBookingDAO;
import com.nmims.daos.LDAPDao;
import com.nmims.daos.LeadDAO;
import com.nmims.daos.StudentDAO;
import com.nmims.daos.StudentMarksDAO;
import com.nmims.daos.TestDAO;
import com.nmims.dto.LoginSSODto;
import com.nmims.helpers.AESencrp;
import com.nmims.helpers.SalesforceHelper;
import com.nmims.interfaces.LoginSSOInterface;
import com.nmims.listeners.ConfigurationScheduler;
import com.nmims.services.StudentService;
import com.nmims.listeners.MettlScheduler;

/**
 * Handles requests for the application home page.
 */
@Controller
public class HomeController extends BaseController{

	@Autowired
	CareerServicesDAO csDAO;
	
	@Autowired
	StudentService studentService;
	
	@Value("${SERVER_PATH}")
	private String SERVER_PATH;
	
	@Autowired
	ApplicationContext act;
	
	@Autowired
	SalesforceHelper sfdc;
	
	@Autowired
	LeadDAO leadDAO;
	
	@Autowired
	ConfigurationScheduler configurationScheduler;
	
	@Autowired	
	MettlScheduler mettlScheduler;
	private static final Logger logger = LoggerFactory.getLogger(HomeController.class);
	private SimpleDateFormat formatter = new SimpleDateFormat("yyyy/MM/dd");
	
	public static final Integer PROVISIONAL_ADMISSION_EXAMBOOKING_ALLOWED = 0;
	public static final Integer PROVISIONAL_ADMISSION_EXAMBOOKING_NOT_ALLOWED = 1;
	
	@Autowired
	LoginSSOInterface loginSSO;
	
	@Autowired
	StudentDAO studentDao;
	
	private ArrayList<String> CourseraSubscriptionList = new ArrayList<String>(Arrays.asList("77122898638", "77121164128", "77122952276", "77122125981", "77221674304", "77122181068", "77121718616", "77121180541", "77122107721", "77121280796", "77122328311", "77221676773", "77122211623", "77121999632", "77221271845", "77221808460", "77121914206", "77121593432", "77122223769", "77121622847", "77122998534", "77121533985", "77221179917", "77121791988", "77122592539", "77122775784", "77221412207", "77121166637", "77121878611", "77121353869", "77121596247", "77221623726", "77121169405", "77221197687", "77121360321", "77121279251", "77121393574", "77121918532", "77121288277", "77121943274", "77221498448", "77121138157", "77221725756", "77122846967", "77122917428", "77221382709", "77121724807", "77221184173", "77122127961", "77121798385", "77121594482", "77122677803", "77221821553", "77122710382", "77221745258", "77121440540", "77221886869", "77122410284", "77121707942", "77121650079", "77122219915", "77122955904", "77121614703", "77122589591", "77121501135", "77121282082", "77121817826", "77121277720", "77121964267", "77121379188", "77122582306", "77121907096", "77121482694", "77121591060", "77121967591", "77121654205", "77122355962", "77121657663", "77121835234", "77221638165", "77121640955", "77221315097", "77121124125", "77221376428", "77121932260", "77121339567", "77122795773", "77122481611", "77122334257", "77121986076", "77121559963", "77121120313", "77121162775", "77122720177", "77221636192", "77121752711", "77221201204", "77122620227", "77121654383", "77121550953", "77221844182", "77221156481", "77221197619", "77121755888", "77122440140", "77122768312", "77221925109", "77221629973", "77221437801", "77121299957", "77122142355", "77122290359", "77122182863", "77121431371", "77121200466", "77121470658", "77221815345", "77122269296", "77121582395", "77121553541", "77122705538", "77122791067", "77122258873", "77122664313", "77122549857", "77122682680", "77221401012", "77122779142", "77121190847", "77121724527", "77122551708", "77122274801", "77122115137", "77121437019", "77221809668", "77121109925", "77122405088", "77221800987", "77221681545", "77221856425", "77121166711", "77122795473", "77122971418", "77121121877", "77122372263", "77122227321", "77121647406", "77122303428", "77121131240", "77122233070", "77121496635", "77122526934", "77777777132"));
	

	public HomeController(){
	}
	/**
	 * Simply selects the home view to render by returning its name.
	 */
	
	@RequestMapping(value = "/", method = RequestMethod.GET)
	public String homePage(Locale locale, Model model,HttpServletResponse response) {
		return "login";
	}
	
	
	@RequestMapping(value = "/home", method = {RequestMethod.GET, RequestMethod.POST})
	public String goToHome(HttpServletRequest request, HttpServletResponse response) {
		
		if(!checkSession(request, response)){
			redirectToPortalApp(response);
			return null;
			//Check is user is Admin.
		}
		
		String userId = (String)request.getSession().getAttribute("userId");
		Person user = (Person)request.getSession().getAttribute("user");
		
		if(userId.startsWith("77") || user.getRoles().indexOf("Admin") == -1){
			return "studentHome";
		}
		return "home";
	}
	
	
	@RequestMapping(value = "/logout", method = {RequestMethod.GET, RequestMethod.POST})
	public ModelAndView logout(HttpServletRequest request, HttpServletResponse response) {
		
		request.getSession().invalidate();

		ModelAndView modelnView = new ModelAndView("login");
		return modelnView;
	}
	

	@RequestMapping(value = "/authenticate", method = RequestMethod.POST)
	public ModelAndView home(HttpServletRequest request, HttpServletResponse response) throws Exception {

		LDAPDao dao = (LDAPDao)act.getBean("ldapdao");
		ExamBookingDAO ebDao = (ExamBookingDAO)act.getBean("examBookingDAO");
		ModelAndView modelnView = new ModelAndView("home");

		String userId = request.getParameter("userId");
		String password = request.getParameter("password");

		boolean authenticated = dao.login(userId, password);
		if(authenticated){
			modelnView = new ModelAndView("home");
			request.getSession().setAttribute("userId", userId);
			request.getSession().setAttribute("password", password);
			Person person = dao.findPerson(userId);
			person.setUserId(userId);
			modelnView.addObject("displayName", person.getDisplayName() );
			request.getSession().setAttribute("user", person);
			
			String roles = person.getRoles();
			if(roles.indexOf("Admin") == -1){
				modelnView = new ModelAndView("studentHome");
			}
			
			try {
				UserAuthorizationExamBean userAuthorization = ebDao.getUserAuthorization(userId);
				if(userAuthorization == null){
					userAuthorization = new UserAuthorizationExamBean();
				}
				ArrayList<String> authorizedCenterCodes = ebDao.getAuthorizedCenterCodes(userAuthorization);//List of all center codes
				String commaSeparatedAuthorizedCenterCodes = StringUtils.join(authorizedCenterCodes.toArray(), ",");//Comma separated center codes
				
				userAuthorization.setAuthorizedCenterCodes(authorizedCenterCodes);
				userAuthorization.setCommaSeparatedAuthorizedCenterCodes(commaSeparatedAuthorizedCenterCodes);
				
				request.getSession().setAttribute("userAuthorization", userAuthorization);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				//
			}
			
			
		}else{
			modelnView = new ModelAndView("login");
			request.setAttribute("error", "true");
			request.setAttribute("errorMessage", "Invalid Credentials. Please re-try.");
		}
		return modelnView;
	}
	
	@RequestMapping(value = "/studentHome", method = {RequestMethod.GET, RequestMethod.POST})
	public ModelAndView studentHome(HttpServletRequest request, HttpServletResponse response) throws Exception {

		LDAPDao dao = (LDAPDao)act.getBean("ldapdao");
		ModelAndView modelnView = new ModelAndView("studentHome");

		String sapIdEncrypted = request.getParameter("sapId");
		String userId = "NA";
		try {
			userId = AESencrp.decrypt(sapIdEncrypted);
			request.getSession().setAttribute("userId", userId);
			
			ExamBookingDAO ebDao = (ExamBookingDAO)act.getBean("examBookingDAO");
			StudentExamBean student = ebDao.getSingleStudentsData(userId);
			
			String programForHeader =  (String) request.getSession().getAttribute("programForHeaderExam");
			student.setProgramForHeader(programForHeader);
			request.getSession().setAttribute("studentExam", student);
			
			Person person = dao.findPerson(userId);
			//Person person = new Person();
			person.setUserId(userId);
			modelnView.addObject("displayName", person.getDisplayName() );
			request.getSession().setAttribute("user", person);
			request.getSession().setAttribute("userId", userId);
			
			//Fetch and store User Authorization in session
			UserAuthorizationExamBean userAuthorization = ebDao.getUserAuthorization(userId);
			if(userAuthorization == null){
				userAuthorization = new UserAuthorizationExamBean();
			}
			
			ArrayList<String> authorizedCenterCodes = ebDao.getAuthorizedCenterCodes(userAuthorization);//List of all center codes
			String commaSeparatedAuthorizedCenterCodes = StringUtils.join(authorizedCenterCodes.toArray(), ",");//Comma separated center codes
			
			userAuthorization.setAuthorizedCenterCodes(authorizedCenterCodes);
			userAuthorization.setCommaSeparatedAuthorizedCenterCodes(commaSeparatedAuthorizedCenterCodes);
			
			request.getSession().setAttribute("userAuthorization", userAuthorization);
			
		}catch (Exception e) {
			
		}
		return modelnView;
	}

	@RequestMapping(value = "/loginAsForm", method = {RequestMethod.GET, RequestMethod.POST})
	public String loginAsForm(HttpServletRequest request, HttpServletResponse response) throws Exception {
		return "loginAs";
	}
	
	@RequestMapping(value = "/refreshStudentDetails", method = {RequestMethod.GET, RequestMethod.POST})
	public String refreshStudentDetailsInSSO(HttpServletRequest request, HttpServletResponse response) {
		resetStudentInSession(request, response);
		return null;
	}
	
	@RequestMapping(value = "/loginforSSO", method = {RequestMethod.GET, RequestMethod.POST})
	public String loginforSSO(HttpServletRequest request, HttpServletResponse response, Principal principal) throws Exception {

		try {
			Boolean logout = false;
			request.getSession().setAttribute("logout", logout);

//			String userId = (String)request.getSession().getAttribute("userId");
			String emailId = "";

		String userIdEncrypted = request.getParameter("uid");

//		String emailIdEncrypted = request.getParameter("emailId");

		String  userId = AESencrp.decrypt(userIdEncrypted);
		System.out.println("Exam APP User logged in (DAO) - "+userId);
//		userId = AESencrp.decrypt(userIdEncrypted);

		if(userId.equals(request.getSession().getAttribute("userId")) ){
			//Session already created. Don't fire another query on DB
			return null;
		}
		
		if(isEmail(userId)) {
			emailId = userId;
			userId = "77999999999";
	    	request.getSession().setAttribute("isLoginAsLead", "true");
		}
		else
			request.getSession().setAttribute("isLoginAsLead", "fasle");
		
		request.getSession().setAttribute("userId", userId);
		request.getSession().setAttribute("emailId", emailId);
		request.getSession().setAttribute("validityExpired","No");
		request.getSession().setAttribute("earlyAccess", "No");
		BaseDAO studentDao = (BaseDAO)act.getBean("asignmentsDAO");
		
		/*
		 * boolean makeLive= false; //check student already registered with salesforce's
		 * active re-reg month & year ReRegistrationBean activeRegistration =
		 * sfdc.getActiveReRegistrationFromSalesForce(); if(
		 * !activeRegistration.isError()) { boolean alreadyRegistered =
		 * studentDao.ifStudentAlreadyRegisteredForNextSem(userId,activeRegistration);
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
		
		StudentMarksDAO sDao = (StudentMarksDAO)act.getBean("studentMarksDAO");
		ExamBookingDAO eDao = (ExamBookingDAO)act.getBean("examBookingDAO");
		AssignmentsDAO adao = (AssignmentsDAO)act.getBean("asignmentsDAO");
		TestDAO tDao = (TestDAO)act.getBean("testDao");
		//BaseDAO bdao = (BaseDAO)act.getBean("asignmentsDAO");

		StudentExamBean student = eDao.getSingleStudentsData(userId);
		
		if(isEmail(emailId)) {
			student = leadDAO.getLeadsFromSalesForce(emailId, student);
		}
		
		//List<AnnouncementBean> announcements = eDao.getAllActiveAnnouncements();
		//Added for SAS
		List<ExamAnnouncementBean> announcements = null;
		if(student !=null){
			announcements = eDao.getAllActiveAnnouncements(student.getProgram(),student.getPrgmStructApplicable());
		}else{
			announcements = eDao.getAllActiveAnnouncements();
		}
		
		List<ExamOrderExamBean> liveFlagList = sDao.getLiveFlagDetails();
//		HashMap<String,BigDecimal> examOrderMap = sDao.getExamOrderMap();
		HashMap<String,BigDecimal> examOrderMap = generateExamOrderMap(liveFlagList);
		if(student != null) {
			getWaivedInSubjects(student, sDao, request);
		}
		request.getSession().setAttribute("announcementsExam", announcements);
		
		//String programForHeader =  (String) request.getSession().getAttribute("programForHeaderExam");
	//	student.setProgramForHeader(programForHeader);
		request.getSession().setAttribute("studentExam", student);
		boolean isValid = isStudentValid(student, userId);
		if(!isValid){
			request.getSession().setAttribute("validityExpired","Yes");
		}
		
		
		
		
		Person person = new Person();
		if(student != null){
			CaseStudyDao cdao = (CaseStudyDao)act.getBean("caseDAO");
			String programForHeader =  cdao.getExecutiveStudentRegistrationData(userId).getProgram();
			
			student.setProgramForHeader(programForHeader);
			request.getSession().setAttribute("programForHeaderExam", student.getProgramForHeader());
			  
			double examOrderDifference = 0.0;
			double getExamOrderOfProspectiveBatch = examOrderMap.get(student.getEnrollmentMonth()+student.getEnrollmentYear()) !=null ? examOrderMap.get(student.getEnrollmentMonth()+student.getEnrollmentYear()).doubleValue():0.0;
			double getMaxOrderWhereContentLive = eDao.getMaxOrderWhereContentLive();
			examOrderDifference = getExamOrderOfProspectiveBatch - getMaxOrderWhereContentLive;
			if(examOrderDifference == 1){
		    	request.getSession().setAttribute("earlyAccess","Yes");
		    }
			
			//set liveAssignment Flag in BaseDAO
			int hasAssignment = adao.checkHasAssignment(student.getConsumerProgramStructureId());
			if(hasAssignment > 0) {
				AssignmentLiveSetting assignmentLiveSettingRegular = adao.getCurrentLiveAssignment(student.getConsumerProgramStructureId(), "Regular");
				if(assignmentLiveSettingRegular != null) {
					adao.setLiveAssignmentYear(assignmentLiveSettingRegular.getAcadsYear());
					adao.setLiveAssignmentMonth(assignmentLiveSettingRegular.getAcadsMonth());
				}
				AssignmentLiveSetting assignmentLiveSettingResit = adao.getCurrentLiveAssignment(student.getConsumerProgramStructureId(), "Resit");
				if(assignmentLiveSettingResit != null) {
					adao.setLiveResitAssignmentYear(assignmentLiveSettingResit.getAcadsYear());
					adao.setLiveResitAssignmentMonth(assignmentLiveSettingResit.getAcadsMonth());
				}		
			
			}
				//Check for hasTest start
				boolean hasTest = tDao.checkHasTest(student.getConsumerProgramStructureId());
				if(hasTest) {
					TestExamBean testLiveSettingRegular = tDao.getCurrentLiveTestConfigByMasterKeyAndLivetype(student.getConsumerProgramStructureId(), "Regular");
					if(testLiveSettingRegular != null) {
						tDao.setLiveRegularTestYear(testLiveSettingRegular.getAcadsYear());
						tDao.setLiveRegularTestMonth(testLiveSettingRegular.getAcadsMonth());
					}
					
					TestExamBean testLiveSettingResit = tDao.getCurrentLiveTestConfigByMasterKeyAndLivetype(student.getConsumerProgramStructureId(), "Regular");
					if(testLiveSettingResit != null) {
						tDao.setLiveResitTestYear(testLiveSettingResit.getAcadsYear());
						tDao.setLiveResitTestMonth(testLiveSettingResit.getAcadsMonth());
					}		
				}
				//Check for hasTest end
			
			//student.setConsumerProgramStructureId(eDao.getConsumerProId(student.getProgram(), student.getPrgmStructApplicable(), student.getConsumerType()));
			
			boolean isCertificate = isStudentOfCertificate(student.getProgram());
			request.getSession().setAttribute("isCertificate", isCertificate);
			// course Waiver is not applicable for Jul2009 program Structure Students
			// Moved waived off subjects logic from here to the service
			studentService.mgetWaivedOffSubjects(student);
			request.getSession().setAttribute("waivedOffSubjects", student.getWaivedOffSubjects());
			
			student.setProgramForHeader(programForHeader);
			request.getSession().setAttribute("studentExam", student);
			request.getSession().setAttribute("isProvisionalAdmission", student.getProvisionalAdmission());
			
			person.setFirstName(student.getFirstName());
			person.setLastName(student.getLastName());
			person.setProgram(student.getProgram());
			person.setEmail(student.getEmailId());
			person.setContactNo(student.getMobile());
			
			performCSStudentChecks(request, userId, student);
		
			request.getSession().setAttribute("courseraAccess", CourseraSubscriptionList.contains(userId));
		}/*else{
			//Admin user. Fetch information from LDAP
			
				LDAPDao dao = (LDAPDao)act.getBean("ldapdao");
				person = dao.findPerson(userId);
				person.setUserId(userId);
				request.getSession().setAttribute("user", person);
				
				//Fetch and store User Authorization in session
				UserAuthorizationBean userAuthorization = eDao.getUserAuthorization(userId);
				if(userAuthorization == null){
					userAuthorization = new UserAuthorizationBean();
				}
				
				ArrayList<String> authorizedCenterCodes = eDao.getAuthorizedCenterCodes(userAuthorization);//List of all center codes
				String commaSeparatedAuthorizedCenterCodes = StringUtils.join(authorizedCenterCodes.toArray(), ",");//Comma separated center codes
				
				userAuthorization.setAuthorizedCenterCodes(authorizedCenterCodes);
				userAuthorization.setCommaSeparatedAuthorizedCenterCodes(commaSeparatedAuthorizedCenterCodes);
				
				request.getSession().setAttribute("userAuthorization", userAuthorization);
			
			
		}*/
		
		if((!userId.startsWith("77")) && (!userId.startsWith("79"))){
			//Admin user. Fetch information from LDAP
			
			//Fetch and store User Authorization in session
			UserAuthorizationExamBean userAuthorization = eDao.getUserAuthorization(userId);
			if(userAuthorization == null){
				userAuthorization = new UserAuthorizationExamBean();
			}
			
			LDAPDao dao = (LDAPDao)act.getBean("ldapdao");
			try {
				
				person = dao.findPerson(userId);
				
				try {

					FacultyExamBean faculty = sDao.isFaculty(userId);
					
					if( "Insofe Faculty".equals( faculty.getTitle() ) ) 
						person.setRoles("Insofe");
					
				}catch (Exception e) {
					// TODO: handle exception
				}
				
			}catch (Exception e) {
				//Added for LDAP Error
				//Check if faculty
				try {
					FacultyExamBean faculty = sDao.isFaculty(userId);
					
					if( "Insofe Faculty".equals( faculty.getTitle() ) ) 
						person.setRoles("Insofe");
					else
						person.setRoles("Faculty");
					person.setDisplayName(faculty.getFirstName() + faculty.getLastName());
					person.setEmail(faculty.getEmail());
					person.setFirstName(faculty.getFirstName());
					person.setLastName(faculty.getLastName());
					person.setPassword("ngasce@admin20");
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
			
			//added temporarily to set faculty role for Ashutosh  Kar (faculty)
			if(userId.equalsIgnoreCase("NGASCE0332")){
				person.setRoles("Faculty");
			}
			person.setUserId(userId);
			request.getSession().setAttribute("user", person);
			request.getSession().setAttribute("isProvisionalAdmission", PROVISIONAL_ADMISSION_EXAMBOOKING_ALLOWED);
			
			ArrayList<String> authorizedCenterCodes = eDao.getAuthorizedCenterCodes(userAuthorization);//List of all center codes
			String commaSeparatedAuthorizedCenterCodes = StringUtils.join(authorizedCenterCodes.toArray(), ",");//Comma separated center codes

			//Perform user checks for CS  users.
			csDAO.performCSAffiliateUserChecks(request, userId);
			
			userAuthorization.setAuthorizedCenterCodes(authorizedCenterCodes);
			userAuthorization.setCommaSeparatedAuthorizedCenterCodes(commaSeparatedAuthorizedCenterCodes);
			
			request.getSession().setAttribute("userAuthorization", userAuthorization);
			request.getSession().setAttribute("isStukentApplicable", sDao.isStukentApplicable(userId));
			request.getSession().setAttribute("courseraAccess", false);
		}
		
		person.setUserId(userId);
		request.getSession().setAttribute("user", person);
		} catch (Exception e) {
			
		}

		
		if("http://localhost:8080/".equals(SERVER_PATH)) {
			response.setHeader("Set-Cookie", "JSESSIONID=" + request.getSession().getId() + "; Path=/exam; HttpOnly; SameSite=lax;");
			response.setHeader("Set-Cookie", "SESSION=" + request.getSession().getId() + "; Path=/exam/; HttpOnly; SameSite=none;");
		} else {
			response.setHeader("Set-Cookie", "JSESSIONID=" + request.getSession().getId() + "; Path=/exam; HttpOnly; SameSite=none; Secure");
			response.setHeader("Set-Cookie", "SESSION=" + request.getSession().getId() + "; Path=/exam/; HttpOnly; SameSite=none; Secure");
		}
		return null;
		
	}
	
	private ArrayList<String> getWaivedInSubjects(StudentExamBean studentBean,StudentMarksDAO sDao,HttpServletRequest request) {
		// Moved code from here in favor of the common method in studentService
		studentService.mgetWaivedInSubjects(studentBean);
		
		request.getSession().setAttribute("waivedInSubjects", studentBean.getWaivedInSubjects());
		
		String programForHeader =  (String) request.getSession().getAttribute("programForHeaderExam");
		studentBean.setProgramForHeader(programForHeader);
		request.getSession().setAttribute("studentExam", studentBean);
		return studentBean.getWaivedInSubjects();
	}
	
	
	public boolean isEmail(String email) 
    { 
		Pattern pattern = Pattern.compile("[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,4}");
        Matcher mat = pattern.matcher(email);
        boolean match =  mat.matches();
        return match;
    } 
	
	
	@RequestMapping(value = "/logoutforSSO", method = {RequestMethod.GET, RequestMethod.POST})
	public @ResponseBody String logoutforSSO(HttpServletRequest request, HttpServletResponse response) throws Exception {
		
		request.getSession().invalidate();
		return null;
	}
	
	
	@RequestMapping(value = "/chatGroupCreator", method = {RequestMethod.GET, RequestMethod.POST})
	public @ResponseBody String chatGroupCreator(HttpServletRequest request, HttpServletResponse response) throws Exception {
		configurationScheduler.checkSubjectTimeboundDuration();
		//request.getSession().invalidate();
		return "Done";
	}
	
	
	@RequestMapping(value = "/queryForm", method = {RequestMethod.GET, RequestMethod.POST})
	public String queryForm(HttpServletRequest request, HttpServletResponse response) throws Exception {
		return "query";
	}
	
	@RequestMapping(value = "/query", method = {RequestMethod.GET, RequestMethod.POST})
	public ModelAndView query(HttpServletRequest request, HttpServletResponse response){
		ModelAndView modelnView = new ModelAndView("query");

		String sql = request.getParameter("sql");
		StudentMarksDAO dao = (StudentMarksDAO)act.getBean("studentMarksDAO");
		
		try {
			dao.execute(sql);
			request.setAttribute("success","true");
			request.setAttribute("successMessage","Query executed successfully");
		} catch (Exception e) {
			request.setAttribute("error", "true");
			request.setAttribute("errorMessage", "Error in running query.");
		}
		


		return modelnView;
	}
	
	@RequestMapping(value = "/loginAs", method = {RequestMethod.GET, RequestMethod.POST})
	public ModelAndView loginAs(HttpServletRequest request, HttpServletResponse response) throws Exception {

		String loginAs = request.getParameter("userId");
		String sapIdEncrypted = AESencrp.encrypt(loginAs);
		Map model = new HashMap();
		model.put("sapId", sapIdEncrypted);
		return new ModelAndView(new RedirectView("studentHome"), model);
	}
	 
	
	
	@RequestMapping(value = "/changePassword", method = {RequestMethod.GET, RequestMethod.POST})
	public String changePassword(HttpServletRequest request, HttpServletResponse response) {
		if(!checkSession(request, response)){
			redirectToPortalApp(response);
			return null;
		}
		logger.info("Sending to change password page");
		return "changePassword";
	}
	
	

	
	@RequestMapping(value = "/savePassword", method = {RequestMethod.GET, RequestMethod.POST})
	public ModelAndView savePassword(HttpServletRequest request, HttpServletResponse response) {
		if(!checkSession(request, response)){
			redirectToPortalApp(response);
			return null;
		}
		
		String password = request.getParameter("password");
		ModelAndView modelnView = null;

		String userId = (String)request.getSession().getAttribute("userId");
		
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
	private boolean isStudentValid(StudentExamBean student, String userId) throws ParseException {
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
	
	private HashMap<String, BigDecimal> generateExamOrderMap(List<ExamOrderExamBean> liveFlagList) {
		HashMap<String, BigDecimal> orderMap = new HashMap<String, BigDecimal>();
		for (ExamOrderExamBean row : liveFlagList) {
			orderMap.put(row.getMonth()+row.getYear(),BigDecimal.valueOf((long)Double.parseDouble( row.getOrder())));
			orderMap.put(row.getAcadMonth()+row.getYear(),BigDecimal.valueOf((long)Double.parseDouble( row.getOrder())));
		}
		return orderMap;
	}
	

	@RequestMapping(value = "/createNewStructureMapping", method = RequestMethod.GET)
	public String createNewStructureMapping(HttpServletRequest request) {
		
		logger.info("-->createNewStructureMapping Starts");
		try {
			int count = 1;
			StudentMarksDAO sDao = (StudentMarksDAO)act.getBean("studentMarksDAO");
			ArrayList<StudentExamBean> oct2020StudentList = sDao.getOct2020Students();
			HashMap<String, ProgramSubjectMappingExamBean> mapOfSubjectDetailsAndPssId = sDao.getMapOfSubjectDetailsAndPssId111();
			HashMap<String, ProgramSubjectMappingExamBean> mapOfSubjectDetailsAndSubject = sDao.getMapOfSubjectDetailsAndSubject151();
			
			for (StudentExamBean student : oct2020StudentList) {
				logger.info("Count " + count + "/" + oct2020StudentList.size());
				ArrayList<ProgramSubjectMappingExamBean> pssIdList = sDao.getAllPSSIds(student.getSapid());
				for (ProgramSubjectMappingExamBean bean : pssIdList) {
					String sapid = student.getSapid();
					String oldPss = Integer.toString(bean.getId());
					String subject = mapOfSubjectDetailsAndPssId.get(oldPss).getSubject();
					String sem = mapOfSubjectDetailsAndPssId.get(oldPss).getSem();
					int newPss = mapOfSubjectDetailsAndSubject.get(subject).getId();
					String acadYear = bean.getAcadYear();
					String acadsMonth = bean.getAcadMonth();
					String examYear = bean.getExamYear();
					String examMonth = bean.getExamMonth();
					sDao.upsertNewStructureMapping(sapid, oldPss, newPss, sem, acadYear, acadsMonth, examYear, examMonth);
				}
				count++;
			}
		} catch (Exception e) {
			
		}
		logger.info("-->createNewStructureMapping Ends");
		return "";
	}
	

	@RequestMapping(value = "/admin/getadmin", method = RequestMethod.GET)
	public void getadmin() {
	}
	
	@RequestMapping(value = "/student/getstudent", method = RequestMethod.GET)
	public void getstudent() {
	}
	
	@RequestMapping(value = "/m/putDataIntoPGScheduleInfoMettl", method = RequestMethod.GET)	
	public @ResponseBody String putDataIntoPGScheduleInfoMettl(HttpServletRequest request) {	
		mettlScheduler.putDataIntoPGScheduleInfoMettl();	
		return "Done";	
	}
	
	@RequestMapping(value = "/loginforSSO_new", method = {RequestMethod.GET, RequestMethod.POST})
	  public @ResponseBody RedirectView loginforSSO_updated(HttpServletRequest request, HttpServletResponse response) throws Exception {
	    try {
	    Boolean logout = false;
	    request.getSession().setAttribute("logout", logout);
	    String emailId = "";
	    String userIdEncrypted = request.getParameter("uid");
	    String  userId = AESencrp.decrypt(userIdEncrypted);

	    LoginSSODto loginDetails = loginSSO.getStudentData(userId);
	    if(loginDetails == null)
			  return getRedirectUrl("/exam/loginforSSO?uid="+userIdEncrypted);


	  
	    SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
		Date date = new Date();	    
	    System.out.println(formatter.format(date) + "::: EXAM APP: User logged in (Redis) : " + userId);
	    
	    request.getSession().setAttribute("subjectCodeId_exam", loginDetails.getSubjectCodeId());
	    if(userId.equals(request.getSession().getAttribute("userId")) ){
	      //Session already created. Don't fire another query on DB
	      return null;
	    }
	   
	    if(isEmail(userId)) {
	      emailId = userId;
	      userId = "77999999999";
	    } 
	  
	    request.getSession().setAttribute("isLoginAsLead", loginDetails.getIsLoginAsLead()); //Get Login As Lead Details
	    request.getSession().setAttribute("userId", userId);
	    request.getSession().setAttribute("emailId", emailId);
	    request.getSession().setAttribute("validityExpired",loginDetails.getValidityExpired());//Get Validity Expire Details
	    request.getSession().setAttribute("earlyAccess", loginDetails.getEarlyAccess()); //Get Early Access Details
	    StudentExamBean student =  loginDetails.getStudent(); //Get Student Details
	   
		if(isEmail(emailId)) {
			student = leadDAO.getLeadsFromSalesForce(emailId, student);
		}
	    request.getSession().setAttribute("announcementsExam", loginDetails.getAnnouncements()); //Get Announcement Details
	    
	    request.getSession().setAttribute("studentExam", loginDetails.getStudent()); 
	  
	    Person person = loginDetails.getPersonDetails(); //Get Person Details
	    StudentMarksDAO sDao = (StudentMarksDAO)act.getBean("studentMarksDAO");
	    if(student != null){
		    request.getSession().setAttribute("programForHeaderExam", student.getProgramForHeader());
		   AssignmentsDAO adao = (AssignmentsDAO)act.getBean("asignmentsDAO");
		   TestDAO tDao = (TestDAO)act.getBean("testDao");
		   
		   getWaivedInSubjects(student, sDao, request);
		   
	      //set liveAssignment Flag in BaseDAO
	      int hasAssignment = adao.checkHasAssignment(student.getConsumerProgramStructureId());
	      if(hasAssignment > 0) {
	        AssignmentLiveSetting assignmentLiveSettingRegular = adao.getCurrentLiveAssignment(student.getConsumerProgramStructureId(), "Regular");
	        if(assignmentLiveSettingRegular != null) {
	          adao.setLiveAssignmentYear(assignmentLiveSettingRegular.getAcadsYear());
	          adao.setLiveAssignmentMonth(assignmentLiveSettingRegular.getAcadsMonth());
	        }
	        AssignmentLiveSetting assignmentLiveSettingResit = adao.getCurrentLiveAssignment(student.getConsumerProgramStructureId(), "Resit");
	        if(assignmentLiveSettingResit != null) {
	          adao.setLiveResitAssignmentYear(assignmentLiveSettingResit.getAcadsYear());
	          adao.setLiveResitAssignmentMonth(assignmentLiveSettingResit.getAcadsMonth());
	        }   
	      
	      }
	        //Check for hasTest start
	        boolean hasTest = tDao.checkHasTest(student.getConsumerProgramStructureId());
	        if(hasTest) {
	          TestExamBean testLiveSettingRegular = tDao.getCurrentLiveTestConfigByMasterKeyAndLivetype(student.getConsumerProgramStructureId(), "Regular");
	          if(testLiveSettingRegular != null) {
	            tDao.setLiveRegularTestYear(testLiveSettingRegular.getAcadsYear());
	            tDao.setLiveRegularTestMonth(testLiveSettingRegular.getAcadsMonth());
	          }
	          
	          TestExamBean testLiveSettingResit = tDao.getCurrentLiveTestConfigByMasterKeyAndLivetype(student.getConsumerProgramStructureId(), "Regular");
	          if(testLiveSettingResit != null) {
	            tDao.setLiveResitTestYear(testLiveSettingResit.getAcadsYear());
	            tDao.setLiveResitTestMonth(testLiveSettingResit.getAcadsMonth());
	          }   
	        }
	        //Check for hasTest end
	      
	      //student.setConsumerProgramStructureId(eDao.getConsumerProId(student.getProgram(), student.getPrgmStructApplicable(), student.getConsumerType()));
	      
	      boolean isCertificate = isStudentOfCertificate(student.getProgram());
	      request.getSession().setAttribute("isCertificate", isCertificate);
	      // course Waiver is not applicable for Jul2009 program Structure Students
	      
	      // Moved waived off subjects logic from here to the service
	      //studentService.mgetWaivedOffSubjects(student);
	      request.getSession().setAttribute("waivedOffSubjects", student.getWaivedOffSubjects());
	      
	      request.getSession().setAttribute("studentExam", student);
	      request.getSession().setAttribute("isProvisionalAdmission", student.getProvisionalAdmission());
	      request.getSession().setAttribute("consumerProgramStructureHasCSAccess", loginDetails.isConsumerProgramStructureHasCSAccess()); //Get Career Service Details
	      request.getSession().setAttribute("CSFeatureAccess", loginDetails.getFeatureViseAccess());//Get Career Service Details
	      request.getSession().setAttribute("courseraAccess", loginDetails.isCourseraAccess());
	    }
	    
	    
	    if((!userId.startsWith("77")) && (!userId.startsWith("79"))){ 
	      //Fetch and store User Authorization in session
	      UserAuthorizationExamBean userAuthorization = loginDetails.getUserBean(); //Get User Authorization Details
	      
	      if(userAuthorization == null){
	        userAuthorization = new UserAuthorizationExamBean();
	      }
	      
	      if(userAuthorization.getAuthorizedCenterCodes() == null) {
					userAuthorization.setAuthorizedCenterCodes(new ArrayList<String>());
				}
	   
	      //added temporarily to set faculty role for Ashutosh  Kar (faculty)
	      if(userId.equalsIgnoreCase("NGASCE0332")){
	        person.setRoles("Faculty");
	      }
	   
	      request.getSession().setAttribute("isProvisionalAdmission", PROVISIONAL_ADMISSION_EXAMBOOKING_ALLOWED);
	      
	      //Perform user checks for CS  users.
	  
		  Map<String,Boolean> csAdmin = loginDetails.getCsAdmin();
		  request.getSession().setAttribute("isCSSpeaker", csAdmin.get("isCSSpeaker"));
		  request.getSession().setAttribute("isCSAdmin",  csAdmin.get("isCSAdmin"));
		  request.getSession().setAttribute("isCSProductsAdmin", csAdmin.get("isCSProductsAdmin"));
		  request.getSession().setAttribute("isCSSessionsAdmin", csAdmin.get("isCSSessionsAdmin"));
		  request.getSession().setAttribute("isExternallyAffiliatedForProducts",  csAdmin.get("isExternallyAffiliatedForProducts"));
	        
	      request.getSession().setAttribute("userAuthorization", userAuthorization);
	      request.getSession().setAttribute("isStukentApplicable", sDao.isStukentApplicable(userId));
	  	 
	    }
	    request.getSession().setAttribute("user", person);
	    } catch (Exception e) {
	    
	    }
	    if("http://localhost:8080/".equals(SERVER_PATH)) {
	      response.setHeader("Set-Cookie", "JSESSIONID=" + request.getSession().getId() + "; Path=/exam; HttpOnly; SameSite=lax;");
	      response.setHeader("Set-Cookie", "SESSION=" + request.getSession().getId() + "; Path=/exam/; HttpOnly; SameSite=none;");
	    } else {
	      response.setHeader("Set-Cookie", "JSESSIONID=" + request.getSession().getId() + "; Path=/exam; HttpOnly; SameSite=none; Secure");
	      response.setHeader("Set-Cookie", "SESSION=" + request.getSession().getId() + "; Path=/exam/; HttpOnly; SameSite=none; Secure");
	    }
	    return null;
	    
	  }
	public RedirectView getRedirectUrl(String url){
		RedirectView redirectView = new RedirectView();
    	redirectView.setUrl(url);
    	return redirectView;
	}
	
}

