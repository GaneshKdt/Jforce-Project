package com.nmims.controllers;

import java.io.IOException;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.view.RedirectView;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.nmims.beans.AnnouncementStudentPortalBean;
import com.nmims.beans.AssignmentStudentPortalFileBean;
import com.nmims.beans.AssignmentLiveSettingStudentPortal;
import com.nmims.beans.ContentStudentPortalBean;
import com.nmims.beans.ExamBookingTransactionStudentPortalBean;
import com.nmims.beans.ExamOrderStudentPortalBean;
import com.nmims.beans.ExecutiveExamOrderStudentPortalBean;
import com.nmims.beans.FlagBean;
import com.nmims.beans.LeadStudentPortalBean;
import com.nmims.beans.Online_EventBean;
import com.nmims.beans.ProgramSubjectMappingStudentPortalBean;
import com.nmims.beans.ServiceRequestStudentPortal;
import com.nmims.beans.SessionDayTimeStudentPortal;
import com.nmims.beans.StudentStudentPortalBean;
import com.nmims.beans.StudentMarksBean;
import com.nmims.beans.VideoContentStudentPortalBean;
import com.nmims.daos.AssignmentsDAO;
import com.nmims.daos.CareerServicesDAO;
import com.nmims.daos.ContentDAO;
import com.nmims.daos.LDAPDao;
import com.nmims.daos.LeadDAO;
import com.nmims.daos.PortalDao;
import com.nmims.daos.ServiceRequestDao;
import com.nmims.factory.LeadFactory;
import com.nmims.helpers.PersonStudentPortalBean;
import com.nmims.helpers.RegistrationHelper;
import com.nmims.interfaces.LeadInterface;
import com.nmims.services.StudentService;
import com.sforce.ws.ConnectionException;

@Controller
public class LeadController extends BaseController{

	@Autowired
	ApplicationContext act;

	@Autowired
	LeadFactory leadFactory;
	
	@Autowired
	LeadDAO leadDAO;

	@Autowired
	ContentDAO contentDAO;
	
	@Autowired
	CareerServicesDAO csDAO;
	
	@Autowired
	StudentService studentService;

	@Autowired
	RegistrationHelper registrationHelper;
	
	@Value( "${SERVER_PATH}" )
	private String SERVER_PATH;

	@Value( "${ENVIRONMENT}" )
	private String ENVIRONMENT;
	
	@Value( "${ASSIGNMENT_SUBMISSIONS_ATTEMPTS}" )
	private String ASSIGNMENT_SUBMISSIONS_ATTEMPTS;

	@Value("${CURRENT_ACAD_MONTH}")
	private String CURRENT_ACAD_MONTH;

	@Value("${CURRENT_ACAD_YEAR}")
	private String CURRENT_ACAD_YEAR;

	private ArrayList<ProgramSubjectMappingStudentPortalBean> programSubjectMappingList = null;
	
	private static final Logger logger = LoggerFactory.getLogger(LeadController.class);
	
	public HashMap<String,String> mapOfSRTypesAndTAT = null;

	public LeadController() {
		
	}
	
	@RequestMapping(value = "/leadHome", method = {RequestMethod.GET, RequestMethod.POST})
	public ModelAndView goToLeadHome(HttpServletRequest request, HttpServletResponse response) {

		if(!checkSession(request, response)){
			return new ModelAndView("jsp/login");
		}
		
		String userId = (String)request.getSession().getAttribute("userId");

		return executePostAuthenticationActivitiesForLeads( request, userId );
	}
	
	@RequestMapping(value = "/executePostAuthenticationActivitiesForLeads", method = {RequestMethod.GET})
	public ModelAndView executePostAuthenticationActivitiesForLeads(HttpServletRequest request, String userId) {
		
		ModelAndView modelAndView = new ModelAndView("jsp/home");
		PortalDao pDao = (PortalDao)act.getBean("portalDAO");
		LDAPDao dao = (LDAPDao)act.getBean("ldapdao");
		ArrayList<String> subjects = new ArrayList<>();
		ArrayList<SessionDayTimeStudentPortal> scheduledSessionList = new ArrayList<>();
		List<AnnouncementStudentPortalBean> announcements = null;
		List<ExecutiveExamOrderStudentPortalBean> ResultliveFlagList = new ArrayList<ExecutiveExamOrderStudentPortalBean>();
		String programStructure = ""; String perspective = ""; String password = "ngasce@admin20";
		String validityEndDate = ""; Boolean logout = false;
		
		StudentStudentPortalBean student = pDao.getSingleStudentsData( userId );
		request.getSession().setAttribute("getSingleStudentsData_studentportal",student);
		
		PersonStudentPortalBean person = new PersonStudentPortalBean();
		
		try {
			person = dao.findPerson(userId);
		} catch (Exception e2) {
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
		}
		
		student = getMappedDataForLeadLogin( request , student );
		programStructure = student.getEnrollmentMonth() + student.getEnrollmentYear();
		ArrayList<VideoContentStudentPortalBean> videoList = leadDAO.getSessionForLead();
		
		if( StringUtils.isBlank( student.getLeadId() ) ) {
			
			@SuppressWarnings("unchecked")
			ArrayList<LeadStudentPortalBean> leadDetails =  (ArrayList<LeadStudentPortalBean>) request.getSession().getAttribute("leadDetails");
			modelAndView = new ModelAndView("jsp/selectLeadToLogin");
			modelAndView.addObject("leadDetails", leadDetails);
			return modelAndView;
			
		}
			
		subjects = leadDAO.getSubjectsForLeads();
		
		try{
			LeadStudentPortalBean lead = leadDAO.getLeadById(student.getLeadId()); 
			perspective=(lead.getPerspective()==null)?"":lead.getPerspective();
			student.setPerspective(perspective);
		}catch (Exception e) {
			//e.printStackTrace();
		}
		
		performCSStudentChecks(request, userId, student);
		getWaivedInSubjects(student, pDao, request);
		announcements = pDao.getAllActiveAnnouncements(student.getProgram(),student.getConsumerProgramStructureId());
		boolean isCertificate = student.isCertificateStudent();
		
		try {
			validityEndDate = getValidityEndDate(student);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			//e.printStackTrace();
		}
		List<ExamOrderStudentPortalBean> liveFlagList = pDao.getLiveFlagDetails();
		StudentStudentPortalBean studentreg = pDao.getStudentsMostRecentRegistrationData(student.getSapid());

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
		double current_order = examOrderMap.get(CURRENT_ACAD_MONTH+CURRENT_ACAD_YEAR).doubleValue();
	
		request.getSession().setAttribute("current_order",current_order);
		request.getSession().setAttribute("acadContentLiveOrder",maxOrderWhereContentLive);
		request.getSession().setAttribute("reg_order",reg_order);
		
		checkIfWaivedOff(student, pDao, request);
		getStudentHomePageDetails(student, request, liveFlagList,ResultliveFlagList);
		modelAndView.addObject("displayName", person.getDisplayName() );
		request.getSession().setAttribute("user_studentportal", person);

		person.setEmail(student.getEmailId());
		person.setContactNo(student.getMobile());
		person.setProgram(student.getProgram());

		ArrayList<StudentMarksBean> allStudentRegistrations = pDao.getAllRegistrationsFromSAPID(student.getSapid());
		HashMap<String, StudentMarksBean> monthYearAndStudentRegistrationMap = new HashMap<>();
		for (StudentMarksBean bean : allStudentRegistrations) {
			monthYearAndStudentRegistrationMap.put(bean.getMonth() + "-" + bean.getYear(), bean);
		}
		String programForHeader =  (String) request.getSession().getAttribute("programForHeaderPortal");
		student.setProgramForHeader(programForHeader);
		request.setAttribute("SERVER_PATH", SERVER_PATH);
		request.getSession().setAttribute("validityExpired","No");
		request.getSession().setAttribute("earlyAccess","No");
		request.getSession().setAttribute("userId", userId);
		request.getSession().setAttribute("password", password);
		request.getSession().setAttribute("logout", logout);
		request.getSession().setAttribute("validityEndDate", validityEndDate);
		request.getSession().setAttribute("student_studentportal", student);
		request.getSession().setAttribute("user_studentportal", person);
		request.getSession().setAttribute("announcementsPortal", announcements);
		request.getSession().setAttribute("emailId", student.getEmailId());
		request.getSession().setAttribute("scheduledSessionList_studentportal", scheduledSessionList); 
		request.getSession().setAttribute("student_studentportal",student);
		request.getSession().setAttribute("assignmentSubjects", subjects); 
		request.getSession().setAttribute("videoList", videoList);
		request.getSession().setAttribute("isCertificate", isCertificate);
		request.getSession().setAttribute("announcementsPortal", announcements);
		request.getSession().setAttribute("studentRecentReg_studentportal",studentreg);
		request.getSession().setAttribute("liveSessionPssIdAccess_studentportal", new ArrayList<Integer>() );
		request.getSession().setAttribute("isLoginAsLead", "true" );

		//Card 10453 disable Portal Experience for Leads and redirect directly to freeCourses page
//		if( perspective.equalsIgnoreCase( "free" ) )
		
		return new ModelAndView(new RedirectView("student/getFreeCoursesList"));
		
//		modelAndView.addObject("isLoginAsLead", true);
//		return modelAndView;
	}
	
	@RequestMapping(value = "/redirectToDashboardForLead", method = {RequestMethod.GET})
	public ModelAndView redirectToDashboard( @RequestParam String userId , HttpServletRequest request) {
		
		ModelAndView modelAndView = new ModelAndView();

		try {
			modelAndView = executePostAuthenticationActivitiesForLeads( request,  userId );
		} catch (Exception e) {
			// TODO Auto-generated catch block
			//e.printStackTrace();
		}

		return modelAndView;
		
	}
	
	@RequestMapping(value = "/loginForLeadForm", method = {RequestMethod.GET})
	public ModelAndView loginForLeadForm(HttpServletRequest request) {
		
		ModelAndView modelAndView = new ModelAndView("jsp/loginForLeads");
		request.getSession().invalidate();
		request.setAttribute("SERVER_PATH", SERVER_PATH);
		return modelAndView;
		
	}
	
	@RequestMapping(value = "/loginForLeads", method = {RequestMethod.POST, RequestMethod.GET})
	public ModelAndView loginForLeads(HttpServletRequest request, HttpServletResponse response, @RequestParam String userId) {

//		ModelAndView modelAndView = new ModelAndView("redirect: /studentportal/loginForLeadForm");
		ModelAndView modelAndView = new ModelAndView("redirect:" + SERVER_PATH + "loginForLeadsForm");

		if( StringUtils.isBlank( userId ) ) {
			request.getSession().invalidate();
			request.setAttribute("SERVER_PATH", SERVER_PATH);
			return modelAndView;
		}
		
		String sapid = "77999999999";

		request.getSession().setAttribute("isLoginAsLead", "true");
		request.getSession().setAttribute("leadUserId", userId);
		
		try {
			return executePostAuthenticationActivitiesForLeads( request, sapid );
		} catch (Exception e) {
			//e.printStackTrace();
			return modelAndView;
		}
		
	}
	
	@RequestMapping(value = "/loginForMultipleLeadAccount", method = {RequestMethod.POST, RequestMethod.GET})
	public ModelAndView loginForMultipleLeadAccount(HttpServletRequest request, HttpServletResponse response, @RequestParam String userId) {
		
		ModelAndView modelAndView = new ModelAndView("redirect: /studentportal/loginForLeadForm");
		
		String sapid = "77999999999";
		
		ArrayList<LeadStudentPortalBean> leadDetails = new ArrayList<>();
		LeadStudentPortalBean bean = new LeadStudentPortalBean();
		bean.setUserId( userId );

		request.getSession().setAttribute("isLoginAsLead", "true");
		request.getSession().setAttribute("leadUserId", userId);
		
		LeadInterface lead = leadFactory.getLoginType(LeadFactory.LoginType.valueOf("REGISTATIONID"));

		try {
			leadDetails = lead.getLeadFromSalesForce( bean );
			request.getSession().setAttribute("leadDetails",leadDetails);
		} catch (Exception e) {
			//e.printStackTrace();
		}
		
		try {
			return executePostAuthenticationActivitiesForLeads( request, sapid );
		} catch (Exception e) {
			//e.printStackTrace();
			return modelAndView;
		}
		
	}

	@RequestMapping(value = "/getLoginDetailsForLeads", method = {RequestMethod.POST}, consumes = "application/json", produces = "application/json")
	public ResponseEntity<LeadStudentPortalBean> getLoginDetailsForLeads( HttpServletRequest request , @RequestBody LeadStudentPortalBean bean) {
		
		HttpHeaders headers = new HttpHeaders();
		headers.add("Content-Type", "application/json");
		String loginType = "";
		LeadStudentPortalBean localLeadDetails = new LeadStudentPortalBean();
		ArrayList<LeadStudentPortalBean> sfdcLeadDetails = new ArrayList<>();
		
		switch ( bean.getLoginType() ) {
			case "Email":
				loginType = "EMAIL";
				break;
			case "nm_RegistrationNo__c":
				loginType = "REGISTATIONID";
				break;
			case "Mobile_No__c":
				loginType = "MOBILE";
				break;
			default:
				break;
		}

		LeadInterface lead = leadFactory.getLoginType(LeadFactory.LoginType.valueOf(loginType));

		try {
			
			sfdcLeadDetails = lead.getLeadFromSalesForce( bean );
			request.getSession().setAttribute("leadDetails",sfdcLeadDetails);
			
			try {
				localLeadDetails = lead.getLeadDetailsLocally( bean );
			}catch (Exception e) {
				localLeadDetails = new LeadStudentPortalBean();
			}
			
			if( sfdcLeadDetails.size() < 1 && !StringUtils.isBlank( localLeadDetails.getLeadId() ) ) {
				
				
				bean.setError(true);
				bean.setErrorMessage("No records found");
				return new ResponseEntity<>(bean, headers, HttpStatus.BAD_REQUEST);
				
			}else if ( StringUtils.isBlank( localLeadDetails.getLeadId() ) && sfdcLeadDetails.size() > 0 ) {

			
				for( LeadStudentPortalBean leadBean : sfdcLeadDetails) {
					leadBean.setSapid("77999999999");
					lead.insertLeadDetailsLocally(leadBean);
				}
				bean = sfdcLeadDetails.get(0);
				bean.setError(false);
				return new ResponseEntity<>(bean, headers, HttpStatus.OK);
				
			}else if ( !StringUtils.isBlank( localLeadDetails.getLeadId() ) && sfdcLeadDetails.size() > 0 ){
				
				
				bean = sfdcLeadDetails.get(0);
				bean.setError(false);
				return new ResponseEntity<>(bean, headers, HttpStatus.OK);
				
			}else {

				bean.setError(true);
				bean.setErrorMessage("No records found");
				return new ResponseEntity<>(bean, headers, HttpStatus.BAD_REQUEST);
				
			}

		} catch (Exception e) {
			
			//e.printStackTrace();
			
			if( sfdcLeadDetails.size() == 0 ) {
				bean.setError(true);
				bean.setErrorMessage("No records found");
				return new ResponseEntity<>(bean, headers, HttpStatus.BAD_REQUEST);
			}else {
				bean.setError(true);
				bean.setErrorMessage("Invalid user id");
				return new ResponseEntity<>(bean, headers, HttpStatus.INTERNAL_SERVER_ERROR);
			}
		}
		
	}

	@RequestMapping(value = "/checkIfLeadPresentForEmailAndMobile", method = RequestMethod.POST, consumes = "application/json", produces = "application/json")
	public ResponseEntity<ArrayList<LeadStudentPortalBean>> checkIfLeadPresentForEmailAndMobile( HttpServletRequest request, @RequestBody LeadStudentPortalBean bean) throws IOException {

		HttpHeaders headers = new HttpHeaders();
		headers.add("Content-Type", "application/json"); 
		ArrayList<LeadStudentPortalBean> details = new ArrayList<>();
		
		try {
			if( leadDAO.checkIfLeadExists(bean) ) {

				details = leadDAO.getDetailsForAlreadyRegisteredUser(bean);
				return new ResponseEntity<>( details, headers,  HttpStatus.CONFLICT );
				
			}else {
				
				return new ResponseEntity<>( details, headers,  HttpStatus.OK );
				
			}
		} catch (Exception e) {
			//e.printStackTrace();
			bean.setMessage(e.getMessage());
			details.add( bean );
			return new ResponseEntity<>( details, headers,  HttpStatus.INTERNAL_SERVER_ERROR );
		}
		
	}
	
	private StudentStudentPortalBean getMappedDataForLeadLogin( HttpServletRequest request ,StudentStudentPortalBean studentBean ) {
		
		LeadStudentPortalBean leadBean = new LeadStudentPortalBean();
		@SuppressWarnings("unchecked")
		ArrayList<LeadStudentPortalBean> leadDetails =  (ArrayList<LeadStudentPortalBean>) request.getSession().getAttribute("leadDetails");

		if( leadDetails.size() > 1) {
			return new StudentStudentPortalBean();
		}else {
			leadBean = leadDetails.get(0);
			studentBean.setFirstName( leadBean.getFirstName() );
			studentBean.setLastName( leadBean.getLastName() );
			studentBean.setLeadId( leadBean.getLeadId() );
			studentBean.setRegistrationNum( leadBean.getRegistrationId() );
			studentBean.setMobile( leadBean.getMobile() );
			studentBean.setEmailId( leadBean.getEmailId() );
			studentBean.setProgram( leadBean.getProgram() );
			studentBean.setDob( leadBean.getDob() );
			studentBean.setGender( leadBean.getGender() );
			studentBean.setImageUrl( leadBean.getImageUrl() );
			return studentBean;
		}
		
		
	}
	
	@RequestMapping(value = "/registerForLeadsForm", method = {RequestMethod.GET, RequestMethod.POST})
	public ModelAndView registerForLeadsForm(HttpServletRequest request, HttpServletResponse response) {

		ModelAndView modelAndView = new ModelAndView("jsp/registerForLeads");
		
		logger.info("Sending to register lead page");
		LeadStudentPortalBean bean = new LeadStudentPortalBean();
		modelAndView.addObject("bean",bean);
		
		return modelAndView;
	}

	@RequestMapping(value = "/m/registerLeads", method = RequestMethod.POST, consumes = "application/json", produces = "application/json")
	public ResponseEntity<LeadStudentPortalBean> registerLeads( HttpServletRequest request, @RequestBody LeadStudentPortalBean bean) throws IOException {

		HttpHeaders headers = new HttpHeaders();
		headers.add("Content-Type", "application/json"); 
		String email = bean.getEmailId(); 
		String leadId = "";
		String url = "";

		if("PROD".equalsIgnoreCase(ENVIRONMENT)) {
            url = "https://ngasce.secure.force.com/services/apexrest/leadservice";
        }else {
            url = "https://sandbox-ngasce.cs5.force.com/services/apexrest/leadservice";
        }
	
		try {
				
			RestTemplate restTemplate = new RestTemplate();
			JsonObject jsonRequest = new JsonObject();
	
			jsonRequest.addProperty("Name", bean.getFirstName());
			jsonRequest.addProperty("EmailID", email);
			jsonRequest.addProperty("ContactNo", bean.getMobile());
			jsonRequest.addProperty("Agency", "StudentPortal");
			jsonRequest.addProperty("HighestQualification",  "BE");
			jsonRequest.addProperty("AdmissionYear", "2020");
			if(bean.getCurrentLocation().equals("Chennai"))
				jsonRequest.addProperty("CurrentLocation", "Bangalore");
			else
				jsonRequest.addProperty("CurrentLocation", bean.getCurrentLocation());
			jsonRequest.addProperty("CourseIntrestedIn", "Diploma Programs");
			jsonRequest.addProperty("AgencyPassword", "portal@123");
	
			HttpEntity<String> entity = new HttpEntity<String>(jsonRequest.toString(), headers);
	
			ResponseEntity<String> restResponse = restTemplate.exchange(url, HttpMethod.POST, entity, String.class);
			JsonObject jsonObject = new JsonParser().parse(restResponse.getBody()).getAsJsonObject();
			leadId= jsonObject.get("LeadId").getAsString();
			bean.setUserId(leadId);
			
			request.getSession().setAttribute("isLoginWithEmail", false);
			request.getSession().setAttribute("isLoginWithMobile", false);
			request.getSession().setAttribute("isLoginWithRegistrationId", true);
			
		}catch(Exception e) {
			//e.printStackTrace();
			bean.setMessage("Error: "+e.getMessage());
			return new ResponseEntity<>( bean, headers,  HttpStatus.INTERNAL_SERVER_ERROR );
		}
		
		LeadInterface lead = leadFactory.getLoginType(LeadFactory.LoginType.valueOf("REGISTATIONID"));
		ArrayList<LeadStudentPortalBean> leadDetails = new ArrayList<>();
		
		try {
			leadDetails = lead.getLeadFromSalesForce( bean );
			for( LeadStudentPortalBean leadBean : leadDetails) {
				leadBean.setSapid("77999999999");
				lead.insertLeadDetailsLocally(leadBean);
			}
			bean = leadDetails.get(0);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			//e.printStackTrace();
		}
		request.getSession().setAttribute("leadDetails",leadDetails);

		//bean.setRedirectURL( "/studentportal/loginForLeads?userId="+leadId );
		//changes due to issue in response ui
		bean.setRedirectURL( "/authLeadUser");
		bean.setError( false );
		return new ResponseEntity<>( bean, headers,  HttpStatus.OK );

	}
	
	@RequestMapping(value = "/admin/leadReport", method = {RequestMethod.GET})
	public ModelAndView leadReport(HttpServletRequest request, HttpServletResponse response) {

		ModelAndView modelAndView = new ModelAndView("jsp/leadReport");
		ArrayList<LeadStudentPortalBean> leadList = new ArrayList<LeadStudentPortalBean>();

		String userId = (String)request.getSession().getAttribute("userId");
		
		try{
			leadList = leadDAO.getLeadDetailsForReport(userId);
		}catch (Exception e) {
			//e.printStackTrace();
		}
		modelAndView.addObject("leadList",leadList);
		request.getSession().setAttribute("leadList", leadList);
		
		return modelAndView;
	}
	
	@RequestMapping(value = "/Lead_Report", method = {RequestMethod.GET, RequestMethod.POST})
	public ModelAndView downloadExamLostFocusReport(HttpServletRequest request, HttpServletResponse response) {
		
		ArrayList<LeadStudentPortalBean> leadList = new ArrayList<LeadStudentPortalBean>();
		String userId = (String)request.getSession().getAttribute("userId");
		
		try{
			leadList = leadDAO.getLeadDetailsForReport(userId);
		}catch (Exception e) {
			//e.printStackTrace();
		}
		request.getSession().setAttribute("leadList", leadList);
		
		return new ModelAndView("jsp/leadReportView", "leadList",leadList);
	}

	
//	to be deleted, api shifted to rest controller
//	@RequestMapping(value = "/m/getLeadDetailsForMobile", method = RequestMethod.POST, consumes = "application/json", 
//	produces = "application/json")
//	public ResponseEntity<ArrayList<StudentBean>> getLeadDetailsForMobile(@RequestBody LeadBean input) throws Exception {
//
//		HttpHeaders headers = new HttpHeaders();
//		headers.add("Content-Type", "application/json"); 
//
//		String userId = input.getUserId();
//		String loginType = "";
//		
//		ArrayList<StudentBean> details = new ArrayList<>();
//
//		userId="77999999999";
//
//		PortalDao pDao = (PortalDao)act.getBean("portalDAO");
//
//		switch ( input.getLoginType() ) {
//			case "Email":
//				loginType = "EMAIL";
//				break;
//			case "nm_RegistrationNo__c":
//				loginType = "REGISTATIONID";
//				break;
//			case "Mobile_No__c":
//				loginType = "MOBILE";
//				break;
//			default:
//				break;
//		}
//		
//		LeadInterface lead = leadFactory.getLoginType(LeadFactory.LoginType.valueOf(loginType));
//		
//		try {
//			ArrayList<LeadBean> leadDetails = lead.getLeadFromSalesForce( input );
//			for( LeadBean bean: leadDetails ) {
//
//				StudentBean student = pDao.getSingleStudentsData(userId);
//				
//				student.setLeadId( bean.getLeadId() );
//				student.setMobile( bean.getMobile() );
//				student.setEmail( bean.getEmailId() );
//				student.setRegistrationNum( bean.getRegistrationId() );
//				student.setFirstName( bean.getFirstName());
//				student.setMiddleName( bean.getFirstName() );
//				student.setLastName( bean.getLastName() );
//				student.setDob( bean.getDob() );
//				student.setFatherName( bean.getFatherName() );
//				student.setMotherName( bean.getMotherName());
//				student.setGender( bean.getGender() );
//				student.setSpouseName( bean.getSpouseName() );
//				
//				details.add( student );
//				
//			}
//		}catch (Exception e) {
//			return new ResponseEntity<>(headers, HttpStatus.UNAUTHORIZED);
//		}
//
//		return new ResponseEntity<>(details, headers,  HttpStatus.OK);
//
//		
//	}

    @RequestMapping(value = "/student/setPerspectiveForLeads", method = {RequestMethod.GET})
     public ModelAndView setPerspectiveForLeads(HttpServletRequest request, HttpServletResponse response,
    		 @RequestParam String perspective) { 
    	
    	StudentStudentPortalBean student = (StudentStudentPortalBean)request.getSession().getAttribute("student_studentportal");
		String userId = (String)request.getSession().getAttribute("userId");
    	
        if(perspective.equalsIgnoreCase("toggle")) {
        	
     			if( "experienced".equals( student.getPerspective() ) ) {  
     				perspective="free";
     			}else {
     				perspective="experienced";
     			}
     		}
    		try {
    			leadDAO.setPerspectiveForLead(student.getLeadId(), perspective);
    			student.setPerspective(perspective);
    			String programForHeader =  (String) request.getSession().getAttribute("programForHeaderPortal");
    			student.setProgramForHeader(programForHeader);
    			request.getSession().setAttribute("student_studentportal", student);
    			
    			
    			if(perspective.equalsIgnoreCase("experienced")) { 
    				return executePostAuthenticationActivitiesForLeads( request, userId );
    			}
    			
    		} catch (Exception e) {
    			//e.printStackTrace();
    		}
    		return new ModelAndView( "redirect:/student/getFreeCoursesList" );
    	} 
    
    
//    to be deleted, api shifted to rest controller
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
    
	public void performCSStudentChecks(HttpServletRequest request, String userId, StudentStudentPortalBean student) {
		csDAO.performCSStudentChecks(request, student);
	}
	
	private ArrayList<String> getWaivedInSubjects(StudentStudentPortalBean studentBean,PortalDao pDao,HttpServletRequest request) {
		ArrayList<String> subjects = studentService.mgetWaivedInSubjects(studentBean);
		request.getSession().setAttribute("waivedInSubjects", subjects);
		String programForHeader =  (String) request.getSession().getAttribute("programForHeaderPortal");
		studentBean.setProgramForHeader(programForHeader);
		request.getSession().setAttribute("student_studentportal", studentBean);
		return subjects;
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
	
	public ArrayList<ProgramSubjectMappingStudentPortalBean> getProgramSubjectMappingList(){
		
		if(this.programSubjectMappingList == null || this.programSubjectMappingList.size() == 0){

			PortalDao pDao = (PortalDao)act.getBean("portalDAO");
			this.programSubjectMappingList = pDao.getProgramSubjectMappingList();
			
		}
		
		return programSubjectMappingList;
	}
	
	public String getValidityEndDate(StudentStudentPortalBean student) throws Exception{
		String validityEndMonthStr = student.getValidityEndMonth();
		String date = "";
		int validityEndYear = Integer.parseInt(student.getValidityEndYear());
		
	
		int validityEndMonth = 0;
		if("Jun".equals(validityEndMonthStr)){
			validityEndMonth = 6;
			date = validityEndYear + "/" + validityEndMonth + "/" + "30";
		}else if("Dec".equals(validityEndMonthStr)){
			validityEndMonth = 12;
		    date = validityEndYear + "/" + validityEndMonth + "/" + "31";
		}else if("Sep".equals(validityEndMonthStr)){
			validityEndMonth = 9;
			date = validityEndYear + "/" + validityEndMonth + "/" + "30";
		}else if("Apr".equals(validityEndMonthStr)){
			validityEndMonth = 4;
			date = validityEndYear + "/" + validityEndMonth + "/" + "30";
		}else if("Aug".equals(validityEndMonthStr)){
			validityEndMonth = 8;
			date = validityEndYear + "/" + validityEndMonth + "/" + "31";
		}else if("Oct".equals(validityEndMonthStr)){
			validityEndMonth = 10;
			date = validityEndYear + "/" + validityEndMonth + "/" + "31";
		}else if("Feb".equals(validityEndMonthStr)){
			validityEndMonth = 2;
			date = validityEndYear + "/" + validityEndMonth + "/" + "28";
		}else if("Mar".equals(validityEndMonthStr)){
			validityEndMonth = 3;
			date = validityEndYear + "/" + validityEndMonth + "/" + "31";
		}else if("Jan".equals(validityEndMonthStr)){
			validityEndMonth = 1;
			date = validityEndYear + "/" + validityEndMonth + "/" + "31";
		}else if("May".equals(validityEndMonthStr)){
			validityEndMonth = 5;
			date = validityEndYear + "/" + validityEndMonth + "/" + "31";
		}else if("Jul".equals(validityEndMonthStr)){
			validityEndMonth = 7;
			date = validityEndYear + "/" + validityEndMonth + "/" + "31";
		}
		
		return String.valueOf(date);
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
	
	private void checkIfWaivedOff(StudentStudentPortalBean student, PortalDao pDao, HttpServletRequest request) {
		ArrayList<String> waivedOffSubjects = studentService.mgetWaivedOffSubjects(student);
		String programForHeader =  (String) request.getSession().getAttribute("programForHeaderPortal");
		student.setProgramForHeader(programForHeader);
		request.getSession().setAttribute("student_studentportal", student);
		request.getSession().setAttribute("waivedOffSubjects", waivedOffSubjects);
	}
	
	private void getStudentHomePageDetails(StudentStudentPortalBean student, HttpServletRequest request, List<ExamOrderStudentPortalBean> liveFlagList, 
			List<ExecutiveExamOrderStudentPortalBean> ResultliveFlagList) {
		
		PortalDao pDao = (PortalDao)act.getBean("portalDAO");
		AssignmentsDAO adao = (AssignmentsDAO)act.getBean("asignmentsDAO");

		ArrayList<StudentMarksBean> allStudentRegistrations = pDao.getAllRegistrationsFromSAPID(student.getSapid());
		HashMap<String, StudentMarksBean> monthYearAndStudentRegistrationMap = new HashMap<>();

		for (StudentMarksBean bean : allStudentRegistrations) {
			
			monthYearAndStudentRegistrationMap.put(bean.getMonth() + "-" + bean.getYear(), bean);
			
		}
		
		int hasAssignment = adao.checkHasAssignment(student.getConsumerProgramStructureId());
		hasAssignment = -1;

		StudentMarksBean studentRegistrationForAssignment = null;
		
		if(hasAssignment > 0) {
			studentRegistrationForAssignment = getAssignmentRegistrationForSpecificLiveSettings(monthYearAndStudentRegistrationMap, student.getConsumerProgramStructureId(), "Regular");
		}
		
		String liveTypeForCourses = "acadContentLive";
		
		if("Yes".equalsIgnoreCase((String)request.getSession().getAttribute("earlyAccess"))){
			liveTypeForCourses = "acadContentLiveNextBatch";
		}
		
		//Added on July2023 to replicate studentRegistrationForCourses of student for lead user
		double current_order = (double) request.getSession().getAttribute("current_order");
	    double reg_order = (double) request.getSession().getAttribute("reg_order");
	    double acadContentLiveOrder = (double) request.getSession().getAttribute("acadContentLiveOrder");
	    
//		StudentMarksBean studentRegistrationForCourses = getStudentRegistrationForForSpecificLiveSettings(monthYearAndStudentRegistrationMap, liveFlagList, liveTypeForCourses);
		StudentMarksBean studentRegistrationForCourses = registrationHelper.CheckStudentRegistrationForCourses(monthYearAndStudentRegistrationMap, acadContentLiveOrder, current_order, reg_order, liveFlagList);
		
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
				//e.printStackTrace();
			}
		}
		request.getSession().setAttribute("examOrderForSession_studentportal", examOrderForSession);

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
			getAssignments(student, request, pDao, studentRegistrationForAssignment);
		}
		
		String year = CURRENT_ACAD_YEAR, month = CURRENT_ACAD_MONTH;
		
		//Preparing program structure
		String programStructure = student.getEnrollmentMonth() + student.getEnrollmentYear();

		List<VideoContentStudentPortalBean> videoList = leadDAO.getVideosForLead( getSubjectsForStudent(student), programStructure );
		request.getSession().setAttribute("videoList", videoList);

		//getTests(student.getSapid(),request);

		getResults(student, request, pDao, liveFlagList, ResultliveFlagList );

		getServiceRequests(student, request);

		getCourses(student, request, pDao, studentRegistrationForCourses);//this must be last function called. Do not change order

		//checkUFM(student, request, pDao);
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
	
	private StudentMarksBean getAssignmentRegistrationForSpecificLiveSettings(HashMap<String, StudentMarksBean> monthYearAndStudentRegistrationMap,String consumerProgramStructureId,String liveType) {
		AssignmentsDAO dao = (AssignmentsDAO)act.getBean("asignmentsDAO");
		AssignmentLiveSettingStudentPortal assignmentLiveSetting = dao.getCurrentLiveAssignment(consumerProgramStructureId, liveType);
		if(assignmentLiveSetting != null) {
			String key = assignmentLiveSetting.getAcadsMonth() + "-" + assignmentLiveSetting.getAcadsYear();
			
			return monthYearAndStudentRegistrationMap.get(key);
		}
		return null;
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
		
		//Added for sorting
		if(scheduledSessionList.size() > 0) {
			Collections.sort(scheduledSessionList, new Comparator<SessionDayTimeStudentPortal>() {
				@Override
				public int compare(SessionDayTimeStudentPortal sBean1, SessionDayTimeStudentPortal sBean2) {
					return sBean1.getDate().compareTo(sBean2.getDate());
				}
			});
		}
		
		if(!"BBA".equalsIgnoreCase(student.getProgram()) && !"B.Com".equalsIgnoreCase(student.getProgram())){
//			Commented by Somesh as now session will coming on CPS id
			//scheduledSessionList = pDao.getScheduledSessionForStudents(subjects,student);
			
			//Get common sessions
			//Temporary commented by Somesh for orientation
			//commonscheduledSessionList = (ArrayList<SessionDayTimeBean>)pDao.getCommonSessionsSemesterBased(student.getSem(),student.getProgram(),student.getConsumerProgramStructureId());
		}else {
			//Get Common Sessions for UG
			commonscheduledSessionList = (ArrayList<SessionDayTimeStudentPortal>)pDao.getCommonSessionsSemesterBasedForUG(student.getSem(),student.getProgram(),student.getConsumerProgramStructureId());
		}
		
		if (commonscheduledSessionList.size() > 0) {
			allScheduledSessionList.addAll(commonscheduledSessionList);
		}
		
		allScheduledSessionList.addAll(scheduledSessionList);
		
		request.getSession().setAttribute("scheduledSessionList_studentportal", allScheduledSessionList);
		request.getSession().setAttribute("registeredForEvent", registeredForEvent);
	}

	
	private void getAssignments(StudentStudentPortalBean student,HttpServletRequest request, PortalDao pDao, StudentMarksBean studentRegistrationData) {

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
			student.setProgram(studentRegistrationData.getProgram());
			student.setSem(studentRegistrationData.getSem());
			currentSem = studentRegistrationData.getSem();
			currentSemSubjects = getSubjectsForStudent(student, subjectSemMap);
		}
	
		currentSemSubjects.addAll(student.getWaivedInSubjects());		//add waived in subjects
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

		try {
			currentSemSubjects = (ArrayList<String>) removeSubjectsApplicableForTestFromList(student, currentSemSubjects);
			failSubjects = (ArrayList<String>) removeSubjectsApplicableForTestFromList(student, failSubjects);
			applicableSubjects = (ArrayList<String>) removeSubjectsApplicableForTestFromList(student, applicableSubjects);
		} catch (Exception e) {
			//e.printStackTrace();
		}

		request.getSession().setAttribute("subjectsForStudent", applicableSubjects);
		List<AssignmentStudentPortalFileBean> allAssignmentFilesList = new ArrayList<>();

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

		if(allAssignmentFilesList != null ){

			HashMap<String,AssignmentStudentPortalFileBean> subjectSubmissionMap = new HashMap<>();

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
	
	@SuppressWarnings("unchecked")
	private void getVideos(StudentStudentPortalBean student, HttpServletRequest request, StudentStudentPortalBean studentRegistrationForAcademicSession) {
		
		ArrayList<VideoContentStudentPortalBean> videoList = new ArrayList<VideoContentStudentPortalBean>();
		
		student.setProgram(studentRegistrationForAcademicSession.getProgram());
		student.setSem(studentRegistrationForAcademicSession.getSem());
		
		ArrayList<Integer> currentSemPSSId = (ArrayList<Integer>)request.getSession().getAttribute("currentSemPSSId_studentportal");
		
		videoList = contentDAO.getSessionOnHome(currentSemPSSId, studentRegistrationForAcademicSession);
			
		if (videoList.size() > 0) {
			Collections.sort(videoList, new Comparator<VideoContentStudentPortalBean>() {
				@Override
				public int compare(VideoContentStudentPortalBean vBean1, VideoContentStudentPortalBean vBean2) {
					return vBean1.getSessionDate().compareTo(vBean2.getSessionDate());
				}
			});
		}
		
		request.getSession().setAttribute("videoList", videoList);
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
	private void getCourses(StudentStudentPortalBean student, HttpServletRequest request,PortalDao pDao, StudentMarksBean studentRegistrationData) {

		
		//All Subjects list going add in this list
		ArrayList<String> allApplicableSubjects = new ArrayList<String>();
		
		//Current Cycle Subjects(If registration in current cycle)
		ArrayList<String> currentSemSubjects = new ArrayList<String>();
		
		//Subjects never appeared hence no entry in pass fail//
		ArrayList<String> notPassedSubjects = new ArrayList<String>();
		
		//Failed subjects list
		ArrayList<String> failedSubjects = new ArrayList<String>();
		
		//Waived-off subjects if any (Previous(Old) program passed subjects)
		ArrayList<String> waivedOffSubjects = new ArrayList<String>();
		
		//Waived-off subjects if any (Applicable for lateral student)
		ArrayList<String> waivedInSubjects = new ArrayList<String>();

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

		HashMap<String,String> programSemSubjectIdWithSubject = new HashMap<String,String>();
		HashMap<String,String> programSemSubjectIdWithSubjectForBacklog = new HashMap<String,String>();
		HashMap<String,String> programSemSubjectIdWithSubjectForCurrentsem = new HashMap<String,String>();
		
		//Get map of subject and PSSId for all subjects
		if(lstOfApplicableSubjects.size() > 0) {
			try{
				programSemSubjectIdWithSubject = pDao.getProgramSemSubjectId(lstOfApplicableSubjects, student.getConsumerProgramStructureId());
			}catch(Exception e){
				//e.printStackTrace();
			}
		}
		
		//Get map of subject and PSSId for Current Cycle Subjects
		if(currentSemSubjects.size() > 0) {
			try{
				programSemSubjectIdWithSubjectForCurrentsem = pDao.getProgramSemSubjectId(currentSemSubjects, student.getConsumerProgramStructureId());
			}catch(Exception e){
				//e.printStackTrace();
			}
		}
		
		//Get map of subject and PSSId for Backlog
		if(failedSubjects.size() > 0) {
			try{
				programSemSubjectIdWithSubjectForBacklog = pDao.getProgramSemSubjectId(failedSubjects, student.getConsumerProgramStructureId());
			}catch(Exception e){
				//e.printStackTrace();
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

	public boolean checkIfMovingResultsToCache() {
		
		FlagBean flagBean = apiCallToGetFlagValueByKey("movingResultsToCache");
		
		if(flagBean != null) {
			if("Y".equalsIgnoreCase(flagBean.getValue())) {
				return true;
			}
		}
		
		return false;
	}
	private List<String> getUGPassSubjects(PortalDao pDao, StudentStudentPortalBean student) {
		return pDao.getUGPassSubjectsForAStudent(student.getSapid());
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
			//e.printStackTrace();
		}
		finally{
			     //Important: Close the connect
				 try {
					client.close();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					//e.printStackTrace();
				}
			 }
			return flagBean;
	}
	
	private boolean isOnline(StudentStudentPortalBean student) {
		if("Online".equalsIgnoreCase(student.getExamMode())){
			return true;
		}else{
			return false;
		}
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
	
	private void getResults(StudentStudentPortalBean student, HttpServletRequest request, PortalDao dao, List<ExamOrderStudentPortalBean> liveFlagList,List<ExecutiveExamOrderStudentPortalBean>  ResultliveFlagList) {
		
		
		  if(checkIfMovingResultsToCache()) { 
				request.getSession().setAttribute("mostRecentResultPeriod_studentportal", "");
				request.getSession().setAttribute("declareDate_studentportal", "");
				request.getSession().setAttribute("studentMarksList_studentportal", new ArrayList<StudentMarksBean>());
				request.getSession().setAttribute("studentMarksListSize_studentportal", 0);

			  return; 
			 }
		 
		
		List<StudentMarksBean> studentMarksList = new ArrayList<StudentMarksBean>();
		String mostRecentResultPeriod = "";
		String declareDate = "";
		SimpleDateFormat sdfr = new SimpleDateFormat("dd-MMM-yyyy");

		request.getSession().setAttribute("mostRecentResultPeriod_studentportal", mostRecentResultPeriod);
		request.getSession().setAttribute("declareDate_studentportal", declareDate);
		request.getSession().setAttribute("studentMarksList_studentportal", studentMarksList);
		request.getSession().setAttribute("studentMarksListSize_studentportal", studentMarksList.size());

	}

	private void getServiceRequests(StudentStudentPortalBean student,HttpServletRequest request) {
		ServiceRequestDao serviceRequestDao = (ServiceRequestDao)act.getBean("serviceRequestDao");
		ArrayList<ServiceRequestStudentPortal> srList =  new ArrayList<ServiceRequestStudentPortal>();
		srList = serviceRequestDao.getStudentsSR(student.getSapid());
		HashMap<String,String> mapOfSRTypesAndTAT = getMapOfSRTypesAndTAT();
		request.getSession().setAttribute("srList", srList);
		request.getSession().setAttribute("mapOfSRTypesAndTAT", mapOfSRTypesAndTAT);
	

	}

	private void checkUFM(StudentStudentPortalBean student, HttpServletRequest request, PortalDao pDao) {
		boolean markedForUFM = pDao.checkIfStudentMarkedForUFM(student.getSapid());
		
		if(markedForUFM) {
			request.getSession().setAttribute("markedForUFM", "true");
		}
	}

	public HashMap<String,String> getMapOfSRTypesAndTAT(){
		if(this.mapOfSRTypesAndTAT == null || this.mapOfSRTypesAndTAT.size() == 0){
			
			ServiceRequestDao serviceRequestDao = (ServiceRequestDao)act.getBean("serviceRequestDao");
			this.mapOfSRTypesAndTAT = serviceRequestDao.getMapOfSRTypesAndTAT();
		}
		return mapOfSRTypesAndTAT;
	}
	
	
//	to be deleted, api shifted to rest controller
//	@RequestMapping(value = "/m/getContent", method = RequestMethod.POST, consumes = "application/json", produces = "application/json")
//	public ResponseEntity<List<ContentBean>> getContent(HttpServletRequest request,
//			@RequestBody StudentBean bean) throws Exception {
//
//		HttpHeaders headers = new HttpHeaders();
//		headers.add("Content-Type", "application/json");
//		PortalDao pDao = (PortalDao)act.getBean("portalDAO");
//		List<ContentBean> allContentListForSubject = new ArrayList<ContentBean>();
//		
//		if(!StringUtils.isBlank(bean.getSubject())) {
//			allContentListForSubject = pDao.getContentsForLeads(bean);
//		}else {
//			allContentListForSubject = pDao.getAllContentsForLeads();
//		}
//
//		return new ResponseEntity<>(allContentListForSubject,headers, HttpStatus.OK);
//
//	}
	

}
