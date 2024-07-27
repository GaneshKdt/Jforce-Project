package com.nmims.controllers;

import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.NoHandlerFoundException;

import com.google.gson.Gson;
import com.nmims.beans.AnnouncementCareerservicesBean;
import com.nmims.beans.AppStartupChecksModelBean;
import com.nmims.beans.CSResponse;
import com.nmims.beans.ExamOrderCareerservicesBean;
import com.nmims.beans.InterviewBean;
import com.nmims.beans.PersonCareerservicesBean;
import com.nmims.beans.ProgressDetailsBean;
import com.nmims.beans.FacultyCareerservicesBean;
import com.nmims.beans.StudentCareerservicesBean;
import com.nmims.beans.UserAuthorizationBean;
import com.nmims.daos.InterviewDAO;
import com.nmims.daos.LDAPDao;
import com.nmims.daos.LoginDAO;
import com.nmims.daos.ProgressDetailsDAO;
import com.nmims.helpers.AESencrp;
import com.nmims.helpers.ExcelHelper;

/**
 * Handles requests for the application home page.
 */
@Controller
public class HomeController extends BaseController{

	@Autowired
	ApplicationContext act;
	
	@Autowired
	ProgressDetailsDAO progressDAO;
	
	@Autowired
	InterviewDAO interviewDAO;

	private static final Logger logger = LoggerFactory.getLogger(HomeController.class);
 
	private ArrayList<String> facultyList = null;
	private SimpleDateFormat formatter = new SimpleDateFormat("yyyy/MM/dd");

    @ExceptionHandler(NoHandlerFoundException.class)
    public ModelAndView handleError404(HttpServletRequest request, Exception e)   {
            ModelAndView mav = new ModelAndView("/404");
            mav.addObject("exception", e);  
            //mav.addObject("errorcode", "404");
            return mav;
    }
	
	@RequestMapping(value = "/refreshStudentDetails", method = {RequestMethod.GET, RequestMethod.POST})
	public String refreshStudentDetailsInSSO(HttpServletRequest request, HttpServletResponse response) {
		resetStudentInSession(request, response);
		return null;
	}
    
	@RequestMapping(value = "/loginforSSO", method = {RequestMethod.GET, RequestMethod.POST})
	public String loginforSSO(HttpServletRequest request, HttpServletResponse response) throws Exception {
		
		try {
		
			Boolean logout = false;
			String emailId = "";
			
			request.getSession().setAttribute("logout", logout);
			request.getSession().setAttribute("validityExpired","No");
			request.getSession().setAttribute("earlyAccess", "No");
	
			String userIdEncrypted = request.getParameter("uid");
			String userId = AESencrp.decrypt(userIdEncrypted);
			
			if(userId.equals(request.getSession().getAttribute("userId")) ){
				//Session already created. Don't fire another query on DB
				return null;
			}
			
			request.getSession().setAttribute("userId", userId);
			
			String emailIdEncrypted = request.getParameter("emailId");
	
			if(!StringUtils.isBlank(emailIdEncrypted))
				emailId = AESencrp.decrypt(emailIdEncrypted);
	
			LoginDAO loginDAO = (LoginDAO)act.getBean("loginDAO");

			StudentCareerservicesBean student = loginDAO.getSingleStudentsData(userId);
			
			//List<AnnouncementBean> announcements = eDao.getAllActiveAnnouncements();
			//Added for SAS
			List<AnnouncementCareerservicesBean> announcements = null;
			if(student !=null){
				announcements = loginDAO.getAllActiveAnnouncements(student.getProgram(),student.getPrgmStructApplicable());
			}else{
				announcements = loginDAO.getAllActiveAnnouncements();
			}
			
			List<ExamOrderCareerservicesBean> liveFlagList = loginDAO.getLiveFlagDetails();
	//		HashMap<String,BigDecimal> examOrderMap = sDao.getExamOrderMap();
			HashMap<String,BigDecimal> examOrderMap = generateExamOrderMap(liveFlagList);

			request.getSession().setAttribute("announcements", announcements);
			request.getSession().setAttribute("student_careerservices", student);
			boolean isValid = isStudentValid(student, userId);
			if(!isValid){
				request.getSession().setAttribute("validityExpired","Yes");
			}
			
			
			PersonCareerservicesBean person = new PersonCareerservicesBean();
			if(student != null){
				double examOrderDifference = 0.0;
				double getExamOrderOfProspectiveBatch = examOrderMap.get(student.getEnrollmentMonth()+student.getEnrollmentYear()) !=null ? examOrderMap.get(student.getEnrollmentMonth()+student.getEnrollmentYear()).doubleValue():0.0;
				double getMaxOrderWhereContentLive = loginDAO.getMaxOrderWhereContentLive();
				examOrderDifference = getExamOrderOfProspectiveBatch - getMaxOrderWhereContentLive;

				if(examOrderDifference == 1){
			    	request.getSession().setAttribute("earlyAccess","Yes");
			    }
				
				boolean isCertificate = isStudentOfCertificate(student.getProgram());

				request.getSession().setAttribute("isCertificate", isCertificate);
				// course Waiver is not applicable for Jul2009 program Structure Students
				if(student.getPreviousStudentId() != null && !"".equals(student.getPreviousStudentId()) && !"Jul2009".equals(student.getPrgmStructApplicable())  && student.getIsLateral().equalsIgnoreCase("Y")){
					
					ArrayList<String> waivedOffSubjects = new ArrayList<String>();
					waivedOffSubjects = loginDAO.getPassSubjectsNamesForAStudent(student.getPreviousStudentId());
					if(waivedOffSubjects.contains("Business Statistics")){
						if("EPBM".equalsIgnoreCase(student.getProgram())){
							waivedOffSubjects.remove("Business Statistics");
							waivedOffSubjects.add("Business Statistics- EP");
						}
						if("MPDV".equalsIgnoreCase(student.getProgram())){
							waivedOffSubjects.remove("Business Statistics");
							waivedOffSubjects.add("Business Statistics- MP");
						}
					}
					
					student.setWaivedOffSubjects(waivedOffSubjects);
					
					request.getSession().setAttribute("waivedOffSubjects", student.getWaivedOffSubjects());
					request.getSession().setAttribute("student_careerservices", student);
				}
				
				person.setFirstName(student.getFirstName());
				person.setLastName(student.getLastName());
				person.setProgram(student.getProgram());
				person.setEmail(student.getEmailId());
				person.setContactNo(student.getMobile());
				
				performCSStudentChecks(request, userId, student);
			}/*else{
				//Admin user. Fetch information from LDAP
				
					LDAPDao dao = (LDAPDao)act.getBean("ldapdao");
					person = dao.findPerson(userId);

					person.setUserId(userId);
					request.getSession().setAttribute("user_careerservices", person);
					
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
				
				//Fetch and store User Authorization in session
				UserAuthorizationBean userAuthorization = loginDAO.getUserAuthorization(userId);
				
				//Admin user. Fetch information from LDAP
				LDAPDao dao = (LDAPDao)act.getBean("ldapdao");
				try {
					person = dao.findPerson(userId);
				}catch(Exception e){
					//Added for LDAP Error
					//Check if faculty
					try {
						FacultyCareerservicesBean faculty = loginDAO.isFaculty(userId);
	
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
						person.setPassword("");
						person.setPostalAddress("");
						person.setUserId(userId);
						person.setRoles(userAuthorization.getRoles());
					}
				}
				
				person.setUserId(userId);
				person.setDisplayName("");
				person.setEmail("");
				person.setFirstName("");
				person.setLastName("");
				person.setPassword("");
				person.setPostalAddress("");
	
				request.getSession().setAttribute("user_careerservices", person);
				
				if(userAuthorization == null){
					userAuthorization = new UserAuthorizationBean();
				}
				
				ArrayList<String> authorizedCenterCodes = loginDAO.getAuthorizedCenterCodes(userAuthorization);//List of all center codes
				String commaSeparatedAuthorizedCenterCodes = StringUtils.join(authorizedCenterCodes.toArray(), ",");//Comma separated center codes
				
				//Check if faculty
				try {
					FacultyCareerservicesBean faculty = loginDAO.isFaculty(userId);
					
					person.setRoles("Faculty");
					person.setDisplayName(faculty.getFirstName() + faculty.getLastName());
					person.setEmail(faculty.getEmail());
					person.setFirstName(faculty.getFirstName());
					person.setLastName(faculty.getLastName());
					person.setPassword("ngasce@admin20");
					person.setPostalAddress(faculty.getAddress());
					person.setUserId(userId);
					
				}catch(Exception e) {
					
				}
				
				//Perform user checks for CS  users.
				csDAO.performCSAffiliateUserChecks(request, userId);
				
				userAuthorization.setAuthorizedCenterCodes(authorizedCenterCodes);
				userAuthorization.setCommaSeparatedAuthorizedCenterCodes(commaSeparatedAuthorizedCenterCodes);
				
				request.getSession().setAttribute("userAuthorization", userAuthorization);
			}
			
			person.setUserId(userId);
			request.getSession().setAttribute("user_careerservices", person);
			
		} catch (Exception e) {
			logger.info("in HomeController class got exception : "+e.getMessage());
		}
		return null;
	}

	public boolean isStudentOfCertificate(String program){
		if(program.startsWith("C")){
			return true;
		}else{
			return false;
		}
	}
	
	private boolean isStudentValid(StudentCareerservicesBean student, String userId) throws ParseException {
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
					cal.add(Calendar.DATE, 115);//Allow access till 22 Feb 2019 For SAS-Jan/2018 batch --> Requirement of Nelson sir
					if(currentDate.before(cal.getTime())){
							return true;//Allow 115 additional days access from Validity End Date
						}
						
						
				}else if (student.getProgram().equals("MPDV") && student.getEnrollmentYear().equals("2018") && student.getEnrollmentMonth().equals("Jan") ) {
					cal.add(Calendar.DATE, 175);//Allow access till 22 Feb 2019 For SAS-Jan/2018 batch --> Requirement of Nelson sir
					if(currentDate.before(cal.getTime())){
						return true;//Allow 175 additional days access from Validity End Date
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
	private HashMap<String, BigDecimal> generateExamOrderMap(List<ExamOrderCareerservicesBean> liveFlagList) {
		HashMap<String, BigDecimal> orderMap = new HashMap<String, BigDecimal>();
		for (ExamOrderCareerservicesBean row : liveFlagList) {
			orderMap.put(row.getMonth()+row.getYear(),BigDecimal.valueOf((long)Double.parseDouble( row.getOrder())));
			orderMap.put(row.getAcadMonth()+row.getYear(),BigDecimal.valueOf((long)Double.parseDouble( row.getOrder())));
		}
		return orderMap;
	}
	
	@RequestMapping(value = "/logoutforSSO", method = {RequestMethod.GET, RequestMethod.POST})
	public @ResponseBody String logoutforSSO(HttpServletRequest request, HttpServletResponse response) throws Exception {
		request.getSession().invalidate();
		return null;
	}

	@RequestMapping(value = "/csStartupChecks", method = RequestMethod.POST, produces = "application/json", consumes = "application/json")
	public ResponseEntity<String> csStartupChecks(Locale locale, Model model, @RequestBody Map<String, String> requestParams) {
		Gson gson = new Gson();
		CSResponse csResponse = new CSResponse();

		if(requestParams.get("sapid") == null) {
			csResponse.setNoSapid();
			return ResponseEntity.ok(gson.toJson(csResponse));
		}
		String sapid = (String) requestParams.get("sapid");
		
		AppStartupChecksModelBean appStartupChecksModelBean = new AppStartupChecksModelBean();

		LoginDAO loginDAO = (LoginDAO)act.getBean("LoginDAO");

		StudentCareerservicesBean student = loginDAO.getSingleStudentsData(sapid);
		
		checkIfCSAccessAvailableForMasterKey(student, appStartupChecksModelBean);

		csResponse.setStatusSuccess();
		csResponse.setResponse(appStartupChecksModelBean);
		
		return ResponseEntity.ok(gson.toJson(csResponse));
	}
	
	@RequestMapping(value = "/m/getDuration",  method = RequestMethod.POST , consumes="application/json", produces="application/json")
	public ResponseEntity<ProgressDetailsBean> getDuration(@RequestBody ProgressDetailsBean progressDetailsBean){
	
		ProgressDetailsBean response = (ProgressDetailsBean) new ProgressDetailsBean();
		
		HttpHeaders headers = new HttpHeaders();
		headers.add("Content-Type", "application/json");
		try {
			response.setStatus("success");
			response.setDuration(progressDAO.getDuration(progressDetailsBean.getPackageName()));
		} catch (Exception e) {
			logger.info("in HomeController class got exception : "+e.getMessage());
			response.setErrorMessage("fail");
		}
		
		return new ResponseEntity<ProgressDetailsBean>(response, HttpStatus.OK);	
	}
	
	@RequestMapping(value = "/m/getFeature",  method = RequestMethod.POST , consumes="application/json", produces="application/json")
	public ResponseEntity<ProgressDetailsBean> getFeature(@RequestBody ProgressDetailsBean progressDetailsBean){
	
		ProgressDetailsBean response = (ProgressDetailsBean) new ProgressDetailsBean();
		
		HttpHeaders headers = new HttpHeaders();
		headers.add("Content-Type", "application/json");
		try {
			response.setStatus("success");
			response.setFeatures(progressDAO.getFeatures(progressDetailsBean.getPackageName(),progressDetailsBean.getDurationMax()));
		} catch (Exception e) {
			logger.info("in HomeController class got exception : "+e.getMessage());
			response.setErrorMessage("fail");
		}
		
		return new ResponseEntity<ProgressDetailsBean>(response, HttpStatus.OK);	
	}

}


