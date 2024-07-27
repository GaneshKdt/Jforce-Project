package com.nmims.controllers;

import java.io.IOException;
import java.math.BigInteger;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.security.MessageDigest;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.ui.Model;
import org.springframework.ui.ModelMap;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.web.servlet.view.RedirectView;

import com.nmims.beans.AnnouncementStudentPortalBean;
import com.nmims.beans.ELearnResourcesStudentPortalBean;
import com.nmims.beans.FacultyStudentPortalBean;
import com.nmims.beans.SessionDayTimeStudentPortal;
import com.nmims.beans.StudentStudentPortalBean;
import com.nmims.beans.TransactionStudentPortalBean;
import com.nmims.beans.UserAuthorizationStudentPortalBean;
import com.nmims.beans.WalletBean;
import com.nmims.beans.WidgeResponseBean;
import com.nmims.daos.AcadsDAO;
import com.nmims.daos.CareerServicesDAO;
import com.nmims.daos.LDAPDao;
import com.nmims.daos.PortalDao;
import com.nmims.daos.StudentInfoCheckDAO;
import com.nmims.daos.WalletDAO;
import com.nmims.entity.LoginSSO;
import com.nmims.helpers.AESencrp;
import com.nmims.helpers.PersonStudentPortalBean;
import com.nmims.interfaces.LoginSSOInterface;
import com.nmims.services.AnnouncementService;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public  class BaseController{
	
	@Autowired
	ApplicationContext act;
	
	@Autowired
	CareerServicesDAO csDAO;
	
	@Value("${SECURE_SECRET}")
	private String SECURE_SECRET="214cb32eed243b72501f3edc818d9737"; 
	
	@Value("${ACCOUNT_ID}")
	private String ACCOUNT_ID;
	
	@Value("${V3URL}")
	private String V3URL;
	
	@Value("${SERVER_HOST}")
	private String host;
	
	@Value("${SERVER_PORT}")
	private int[] port;
	
	@Value("${CURRENT_ACAD_YEAR}")
	private String year;
	
	@Value("${CURRENT_ACAD_MONTH}")
	private String month;

	@Value("${SERVER_PATH}")
	private String SERVER_PATH;
	
	private final String path = "/studentportal/admin/cacheRefreshToOnlyServer";
	
	private String errorInPort = null;
	
	
	@Autowired
	AnnouncementService announcementService;
	
	@Autowired
	LoginSSOInterface loginSSO;
	
	private final String defaultAcadsUrl = "acads/loginforSSO_new?uid=";
	private  final String defaultExamUrl = "exam/loginforSSO_new?uid=";
	private  final String defaultContentUrl = "content/loginforSSO_new?uid=";
	private  final String defaultAnnouncementUrl = "announcement/loginforSSO_new?uid=";
	private  final String defaultknowyourpolicyUrl = "knowyourpolicy/loginforSSO?uid=";
	private  final String defaultjobsearchUrl = "jobsearch/loginforSSO?uid=";
	private  final String defaultalmashinesyUrl = "almashines/loginforSSO?uid=";
	private  final String defaultinternalassessmentyUrl = "internal-assessment/loginforSSO?uid=";
	private  final String defaultservicerequestUrl = "servicerequest/loginforSSO?uid=";
	private  final String defaultreportingtoolUrl = "reportingtool/loginforSSO?uid=";
	private  final String defaultltidemoUrl = "ltidemo/loginforSSO_new?uid=";
	
	private  final Logger redis_logger = LoggerFactory.getLogger("redis_loginsso");
	
	private static final String programStatus = "Program Withdrawal";
	private static final String yesResult = "Yes";
	
	/*@Value("${WALLET_API}")
	private String WALLET_API;*/
	
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
	
	
	public boolean checkLead(HttpServletRequest request, HttpServletResponse response){
		String isLead = (String)request.getSession().getAttribute("isLoginAsLead");
		
		if("true".equals(isLead)){
			return true;
		}else{
			return false;
		}
	}
	
	/*public boolean isStudentOfCertificate(String program){
		if(program.startsWith("C")){
			return true;
		}else{
			return false;
		}
	}*/
	
	public String generateAmountBasedOnCriteria(String amount,String criteria){
		double calculatedAmount = 0.0;
		switch(criteria){
		case "GST":
			calculatedAmount = Double.parseDouble(amount) + (0.18 * Double.parseDouble(amount));
			break;
		}
		
		return String.valueOf(calculatedAmount);
		
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
	
	public ModelAndView sendToPageBasedOnRole(HttpServletRequest request, String userId) {
		PortalDao pDao = (PortalDao)act.getBean("portalDAO");
		//Admin user. Fetch information from LDAP
		LDAPDao dao = (LDAPDao)act.getBean("ldapdao");
		AcadsDAO acadsDao = (AcadsDAO)act.getBean("AcadsDAO");
		StudentInfoCheckDAO studentDao = (StudentInfoCheckDAO)act.getBean("stuentInfoCheckDAO");
		PersonStudentPortalBean person = new PersonStudentPortalBean();
		UserAuthorizationStudentPortalBean userAuthorization = new UserAuthorizationStudentPortalBean();
		
		try {
			userAuthorization = pDao.getUserAuthorization(userId);
			person = dao.findPerson(userId);
			person.setUserId(userId);
			
			try {
				FacultyStudentPortalBean faculty = pDao.isFaculty(userId);
				
				if( "Insofe Faculty".equals( faculty.getTitle() ) ) 
					person.setRoles("Insofe");
				//get faculty data from db as latest data is not present in ldap
				person.setDisplayName(faculty.getFirstName() + faculty.getLastName());
				person.setEmail(faculty.getEmail());
				person.setFirstName(faculty.getFirstName());
				person.setLastName(faculty.getLastName());
				person.setContactNo(faculty.getMobile());
				person.setAltContactNo(faculty.getAltContact());
			}catch (Exception e) {
				// TODO: handle exception
			}
			
		} catch (Exception e) {
			
			//Check if faculty
			try {
				FacultyStudentPortalBean faculty = pDao.isFaculty(userId);
				
				if( "Insofe Faculty".equals( faculty.getTitle() ) ) 
					person.setRoles("Insofe");
				else
					person.setRoles("Faculty");
				
				person.setDisplayName(faculty.getFirstName() + faculty.getLastName());
				person.setEmail(faculty.getEmail());
				person.setFirstName(faculty.getFirstName());
				person.setLastName(faculty.getLastName());
				person.setPassword("ngasce@admin20");
				person.setPostalAddress(faculty.getAddress());
				person.setUserId(userId);
				
			}catch(Exception ex) {
//				ex.printStackTrace();
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
		
		request.getSession().setAttribute("user_studentportal", person);
		
		String roles = person.getRoles();
		if(userAuthorization == null){
			userAuthorization = new UserAuthorizationStudentPortalBean();
		}
		ArrayList<String> authorizedCenterCodes = pDao.getAuthorizedCenterCodes(userAuthorization);//List of all center codes
		String commaSeparatedAuthorizedCenterCodes = StringUtils.join(authorizedCenterCodes.toArray(), ",");//Comma separated center codes
		
		userAuthorization.setAuthorizedCenterCodes(authorizedCenterCodes);
		userAuthorization.setCommaSeparatedAuthorizedCenterCodes(commaSeparatedAuthorizedCenterCodes);
		
		request.getSession().setAttribute("userAuthorization_studentportal", userAuthorization);
		
		//String roles = person.getRoles();
		
		ModelAndView modelnView = new ModelAndView("jsp/staffHome");
		modelnView.addObject("UsersRole",null);
		
		if(roles.indexOf("Exam Admin") != -1 || roles.indexOf("TEE Admin") != -1) {
			int StudentDataMissingCount = studentDao.getEmptyDataCount();
			
			int MissingSubjectMapping = studentDao.getSubjectMissingMapCount();
			int QpPendingToApprove = studentDao.getQpPendingToApprove();
		
			
			modelnView.addObject("UsersRole",person.getRoles());
			modelnView.addObject("StudentDataMissingCount",StudentDataMissingCount);
			modelnView.addObject("MissingSubjectMapping",MissingSubjectMapping); 
			modelnView.addObject("QpPendingToApprove",QpPendingToApprove);  
		}
		if(roles.indexOf("Faculty") != -1) { 
			
			int CountOfNotAnsQuery = acadsDao.getNotAnsQueryCount(userId, year, month);
			int AssignmentNotRevalutedCount = acadsDao.getAssignmentNotRevalutedCount(userId, year, month);
			int AssignmentNotEvaluatedCount = acadsDao.getAssignmentNotEvaluatedCount(userId);
			int ProjectNotRevalutedCount = acadsDao.getProjectNotRevalutedCount(userId, year, month);
			int ProjectNotEvaluatedCount = acadsDao.getProjectNotEvaluatedCount(userId, year, month);
			int CaseStudyNotEvaluatedCount = acadsDao.getCaseStudyNotEvaluatedCount(userId);
			int SessionQueriesNotAnsweredCount  = acadsDao.getSessionQueriesNotAnsweredCount(userId);
			List<SessionDayTimeStudentPortal> list = acadsDao.getUpComingSessions(userId);
			modelnView.addObject("isfacultyRole",true);
			modelnView.addObject("countOfNotAns",CountOfNotAnsQuery);
			modelnView.addObject("countOfAssignmentNotRevalued",AssignmentNotRevalutedCount);
			modelnView.addObject("countOfAssignmentNotEvaluated",AssignmentNotEvaluatedCount);
			
			modelnView.addObject("ProjectNotRevalutedCount",ProjectNotRevalutedCount);
			modelnView.addObject("ProjectNotEvaluatedCount",ProjectNotEvaluatedCount);
			modelnView.addObject("CaseStudyNotEvaluatedCount",CaseStudyNotEvaluatedCount);
			modelnView.addObject("SessionQueriesNotAnsweredCount",SessionQueriesNotAnsweredCount);
			modelnView.addObject("sessions",list);
		}

		//Perform user checks for CS  users.
		csDAO.performCSAffiliateUserChecks(request, userId);
		
		//If CS Faculty, get count of not answered queries
		if(request.getSession().getAttribute("csSpeaker") != null && (boolean) request.getSession().getAttribute("csSpeaker")) {
			int CountOfNotAnsCSQuery = csDAO.getNotAnsCSQueryCount(userId);
			modelnView.addObject("countOfNotAnsCS",CountOfNotAnsCSQuery);
			modelnView.addObject("isfacultyForCS",true);
		}
		
		/*
		 *  End of missing users information
		 */
		
//		following field added for ELearn Resources  validation via roles and provider name 
		ELearnResourcesStudentPortalBean eLearnResourcesBean = acadsDao.isStukentApplicable(userId);
		request.getSession().setAttribute("isStukentLtiRoles_studentportal",eLearnResourcesBean.getRoles() );
		request.getSession().setAttribute("isStukentLtiProviderName_studentportal",eLearnResourcesBean.getProvider_name());
		request.getSession().setAttribute("isStukentLtiUserIdCount_studentportal",eLearnResourcesBean.getUserId_count());
		request.getSession().setAttribute("stukent_details",eLearnResourcesBean);
		
		ELearnResourcesStudentPortalBean elearnResourcesBean = acadsDao.isHarvardApplicable(userId);
		request.getSession().setAttribute("isHarvardLtiRoles",elearnResourcesBean.getRoles() );
		request.getSession().setAttribute("isHarvardLtiProviderName",elearnResourcesBean.getProvider_name());
		request.getSession().setAttribute("isHarvardLtiUserIdCount",elearnResourcesBean.getUserId_count());
		request.getSession().setAttribute("harvard_details",elearnResourcesBean);
		
		List<AnnouncementStudentPortalBean> announcements = announcementService.getAllActiveAnnouncementByRest();
		request.getSession().setAttribute("announcementsPortal", announcements);
		modelnView.addObject("person",person);
		
		setSessionDataIntoTheDTO(request);
		return modelnView;
	}
	
	
	
	public ModelAndView proceedToPayOptions(ModelMap model,String requestId,RedirectAttributes ra){
		//Redirects to a midway page which posts the request id to walletPayRequest method//
		WalletDAO wDao = (WalletDAO)act.getBean("walletDao");
		TransactionStudentPortalBean transactionBean = new TransactionStudentPortalBean();
		populateTransactionBean(transactionBean,requestId,model);
		wDao.insertApiRequest(transactionBean);
		ra.addAttribute("requestId", requestId);
		return new ModelAndView(new RedirectView("/studentportal/payOptions"));
	}
	
	//Used for initial money load. This method is used in PaymentController and WalletController hence kept in BaseController//
		public void fillPaymentParametersInMapWithWalletParameters(ModelMap model, StudentStudentPortalBean student,WalletBean walletBean,String returnURL,String apiRequestId) {

			String address = student.getAddress();
			

			if (address == null || address.trim().length() == 0) {
				address = "Not Available";
			} else if (address.length() > 200) {
				address = address.substring(0, 200);
			}
			
			String city = student.getCity();
			if (city == null || city.trim().length() == 0) {
				city = "Not Available";
			}

			String pin = student.getPin();
			if (pin == null || pin.trim().length() == 0) {
				pin = "400000";
			}

			String mobile = student.getMobile();
			if (mobile == null || mobile.trim().length() == 0) {
				mobile = "0000000000"; 
			}

			String emailId = student.getEmailId();
			if (emailId == null || emailId.trim().length() == 0) {
				emailId = "notavailable@email.com";
			}
			
			model.addAttribute("channel", "10");
			model.addAttribute("account_id", ACCOUNT_ID);
			model.addAttribute("reference_no",apiRequestId);
			model.addAttribute("amount",walletBean.getAmount());
			model.addAttribute("mode", "LIVE");
			model.addAttribute("currency", "INR");
			model.addAttribute("currency_code", "INR");
			model.addAttribute("description", walletBean.getDescription());
																																			// response
			model.addAttribute("return_url",returnURL);
			model.addAttribute("name", student.getFirstName() + " " + student.getLastName());
			model.addAttribute("address", address);
			model.addAttribute("city", city);
			model.addAttribute("country", "IND");
			model.addAttribute("postal_code", pin);
			model.addAttribute("phone", mobile);
			model.addAttribute("email", emailId);
			model.addAttribute("algo", "MD5");
			model.addAttribute("V3URL", V3URL);
			model.addAttribute("apiRequestId", apiRequestId);
		}
	//This method is referenced within Base Controller itself//
    public void populateTransactionBean(TransactionStudentPortalBean transactionBean,String requestId,ModelMap model){
		
		transactionBean.setRequestId(requestId);
		transactionBean.setChannel(model.get("channel").toString());
		transactionBean.setAccountId(model.get("account_id").toString());
		transactionBean.setReferenceNo(model.get("reference_no").toString());
		transactionBean.setAmount(model.get("amount").toString());
		transactionBean.setMode(model.get("mode").toString());
		transactionBean.setCurrency(model.get("currency").toString());
		transactionBean.setCurrencyCode(model.get("currency_code").toString());
		transactionBean.setDescription(model.get("description").toString());
		transactionBean.setReturnUrl(model.get("return_url").toString());
		transactionBean.setName(model.get("name").toString());
		transactionBean.setAddress(model.get("address").toString());
		transactionBean.setCity(model.get("city").toString());
		transactionBean.setCountry(model.get("country").toString());
		transactionBean.setPostalCode(model.get("postal_code").toString());
		transactionBean.setPhone(model.get("phone").toString());
		transactionBean.setEmail(model.get("email").toString());
		transactionBean.setSapid(model.get("studentNumber").toString());
		transactionBean.setPayApi(model.get("V3URL").toString());
		
	}

			
	//Called in Wallet And Payment Controller hence kept common//
	public void updateWalletBalance(WalletBean walletRecord,String transactionType) {
		
		double newBalance = "CREDIT".equals(transactionType) ? 
				Double.parseDouble(walletRecord.getBalance()) + Double.parseDouble(walletRecord.getAmount()) 
				: Double.parseDouble(walletRecord.getBalance()) - Double.parseDouble(walletRecord.getAmount());
				
		
		walletRecord.setWalletBalance(newBalance+"");
		walletRecord.setBalance(newBalance+"");
		walletRecord.setTransactionType(transactionType);
	}
	//Called in Payment And Wallet Controller//
	public double checkIfAmountIsGreaterThanWalletBalanceAndReturnDifference(WalletBean walletRecord,TransactionStudentPortalBean transactionBean){
		
		double transactionAmount = Double.parseDouble(transactionBean.getAmount());
		double walletBalance = Double.parseDouble(walletRecord.getBalance());
		
		if(walletBalance == 0){
			return walletBalance;
		}else if(walletBalance < transactionAmount){
			return transactionAmount - walletBalance;
		}else{
			return 0;
		}
		
	}
	
	public boolean isTransactionSuccessful(HttpServletRequest request) {
		String error = request.getParameter("Error");
		// Error parameter should be absent to call it successful
		if (error == null) {
			// Response code should be 0 to call it successful
			String responseCode = request.getParameter("ResponseCode");
			if ("0".equals(responseCode)) {
				return true;
			} else {
				return false;
			}
		} else {
			return false;
		}
	}
	public boolean isHashMatching(HttpServletRequest request) {
		try {
			String md5HashData = SECURE_SECRET;
			HashMap testMap = new HashMap();
			Enumeration<String> en = request.getParameterNames();

			while (en.hasMoreElements()) {
				String fieldName = (String) en.nextElement();
				String fieldValue = request.getParameter(fieldName);
				
				if ((fieldValue != null) && (fieldValue.length() > 0)) {
					
					testMap.put(fieldName, fieldValue);
				}
			}

			// Sort the HashMap
			Map requestFields = new TreeMap<>(testMap);

			String V3URL = (String) requestFields.remove("V3URL");
			requestFields.remove("submit");
			requestFields.remove("SecureHash");

			for (Iterator i = requestFields.keySet().iterator(); i.hasNext();) {

				String key = (String) i.next();
				String value = (String) requestFields.get(key);
				md5HashData += "|" + value;

				
			}
			
			String hashedvalue = md5(md5HashData);
			String receivedHashValue = request.getParameter("SecureHash");
		
			if (receivedHashValue != null && receivedHashValue.equals(hashedvalue)) {
				return true;
			} else {
				return false;
			}
		} catch (Exception e) {
//			
		}
		return false;
	}

	public String md5(String str) throws Exception {
		MessageDigest m = MessageDigest.getInstance("MD5");

		byte[] data = str.getBytes();
		m.update(data, 0, data.length);
		BigInteger i = new BigInteger(1, m.digest());
		String hash = String.format("%1$032X", i);

		return hash;
	}

	public boolean isAmountMatching(HttpServletRequest request, String totalFees) {
		try {
			double feesSent = Double.parseDouble(totalFees);
			double amountReceived = Double.parseDouble(request.getParameter("response_amount"));

			
			if (feesSent == amountReceived) {
				return true;
			} else {
				return false;
			}
		} catch (Exception e) {
//			
		}
		return false;
	}

	public boolean isTrackIdMatching(HttpServletRequest request, String trackId) {
		if (trackId != null && trackId.equals(request.getParameter("merchant_ref_no"))) {
			return true;
		} else {
			return false;
		}
	}
	public boolean checkSession(HttpServletRequest request){
		String userId = (String)request.getSession().getAttribute("userId");
		if(userId != null){
			return true;
		}else{
			return false;
		}
		
		/*return true;*/

	}
	
	public void setSuccess(HttpServletRequest request, String successMessage){
		request.setAttribute("success","true");
		request.setAttribute("successMessage",successMessage);
	}
	
	public void setError(HttpServletRequest request, String errorMessage){
		request.setAttribute("error", "true");
		request.setAttribute("errorMessage", errorMessage);
	}
	
	public void setSuccess(Model m, String successMessage){
		m.addAttribute("success","true");
		m.addAttribute("successMessage",successMessage);
	}
	
	public void setError(Model m, String errorMessage){
		m.addAttribute("error", "true");
		m.addAttribute("errorMessage", errorMessage);
	}
	public String convertListStringToCommaSeperated(List<String> listOfStrings){
		String commaSeparatedString = "";
		for (String str : listOfStrings) {
			commaSeparatedString = commaSeparatedString + "'" + str + "',";
		}
		
		
			return commaSeparatedString.substring(0, commaSeparatedString.length()-1);
		
	}
	public String getAuthorizedCodes(HttpServletRequest request) {
		UserAuthorizationStudentPortalBean userAuthorization = (UserAuthorizationStudentPortalBean)request.getSession().getAttribute("userAuthorization_studentportal");
		String commaSeparatedCenterCode = "";
		if(userAuthorization != null){
			ArrayList<String> authorizedCenterCodesList = userAuthorization.getAuthorizedCenterCodes();

			for (String centerCode : authorizedCenterCodesList) {
				commaSeparatedCenterCode = commaSeparatedCenterCode + "'" + centerCode + "',";
			}
			
			
			if(commaSeparatedCenterCode.endsWith(",")){
				return commaSeparatedCenterCode.substring(0, commaSeparatedCenterCode.length()-1);
			}
		}
		return commaSeparatedCenterCode;
	}
	
	
	/*
	 * Refresh Cache from all server
	 * */
	@SuppressWarnings("deprecation")
	public String TryRefreshCacheToAllServer(int portKey) throws IOException {
		if(portKey == 0) {
			this.errorInPort = null;
		}
		int i = portKey;
		try {
			while(i < this.port.length)
			{
				URL obj = new URL("http://" + this.host + ":" + this.port[i] + this.path);
				HttpURLConnection  con = (HttpURLConnection ) obj.openConnection();
				con.setRequestMethod("GET");
				con.setRequestProperty("User-Agent", "Mozilla/5.0");
				int responseCode = con.getResponseCode();
				
				if (responseCode == HttpURLConnection.HTTP_OK) { // success
					// print result
					
				} else {
					if(errorInPort == null) {
						errorInPort = "" + port[i] + "";
					}else {
						errorInPort = errorInPort + " , " +  port[i];
					}
					
				}
				i++;
			}
			return errorInPort;
		}
		catch (Exception e) {
			
			if(i < this.port.length) {
				if(errorInPort == null) {
					errorInPort = "" + port[i] + "";
				}else {
					errorInPort = errorInPort + " , " +  port[i];
				}
				i++;
				TryRefreshCacheToAllServer(i);
			}
			return errorInPort;
		}
	}
	
	public void resetStudentInSession(HttpServletRequest request, HttpServletResponse response) {
		
		String userId = (String)request.getSession().getAttribute("userId");
		PortalDao pDao = (PortalDao)act.getBean("portalDAO");
		StudentStudentPortalBean student = pDao.getSingleStudentsData(userId);
		String programForHeader =  (String) request.getSession().getAttribute("programForHeaderPortal");
		student.setProgramForHeader(programForHeader);
		request.getSession().setAttribute("student_studentportal", student);
		performCSStudentChecks(request, userId, student);
		
		request.getSession().setAttribute("student_studentportal", student);
	}
	
	public void performCSStudentChecks(HttpServletRequest request, String userId, StudentStudentPortalBean student) {
		csDAO.performCSStudentChecks(request, student);
	}
	
	public void redirectMBAWXStudentToTimelineApp(HttpServletResponse httpServletResponse, String sapId) {
		
		try {
			httpServletResponse.sendRedirect(SERVER_PATH+"timeline/login?sapid="+sapId);
		} catch (IOException e) {
//			
		}
	
	}
	
	public boolean isEmail(String email) 
    { 
		Pattern pattern = Pattern.compile("[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,4}");
        Matcher mat = pattern.matcher(email);
        boolean match =  mat.matches();
        return match;
    }
	
	public void setSessionDataIntoTheDTO(HttpServletRequest request) {
		LoginSSO login_details = new LoginSSO();
		try{
			String loginAsLead = (String)request.getSession().getAttribute("isLoginAsLead");
			String encryptedSapId =  "";
			
			if(loginAsLead.equalsIgnoreCase("true"))
				encryptedSapId = URLEncoder.encode(AESencrp.encrypt((String)request.getSession().getAttribute("emailId"))); 
			else 
				encryptedSapId = URLEncoder.encode(AESencrp.encrypt((String)request.getSession().getAttribute("userId"))); 
	
			
			request.setAttribute("defaultAcadsUrl", SERVER_PATH+defaultAcadsUrl+encryptedSapId);
			request.setAttribute("defaultExamUrl", SERVER_PATH+defaultExamUrl+encryptedSapId);
			request.setAttribute("defaultContentUrl", SERVER_PATH+defaultContentUrl+encryptedSapId);
			request.setAttribute("defaultAnnouncementUrl", SERVER_PATH+defaultAnnouncementUrl+encryptedSapId);
			request.setAttribute("defaultknowyourpolicyUrl", SERVER_PATH+defaultknowyourpolicyUrl+encryptedSapId);
			request.setAttribute("defaultjobsearchUrl", SERVER_PATH+defaultjobsearchUrl+encryptedSapId);
			request.setAttribute("defaultalmashinesyUrl", SERVER_PATH+defaultalmashinesyUrl+encryptedSapId);
			request.setAttribute("defaultinternalassessmentyUrl", SERVER_PATH+defaultinternalassessmentyUrl+encryptedSapId);
			request.setAttribute("defaultservicerequestUrl", SERVER_PATH+defaultservicerequestUrl+encryptedSapId);
			request.setAttribute("defaultreportingtoolUrl", SERVER_PATH+defaultreportingtoolUrl+encryptedSapId);
			request.setAttribute("defaultltidemoUrl", SERVER_PATH+defaultltidemoUrl+encryptedSapId);
			
			StudentStudentPortalBean student = (StudentStudentPortalBean)request.getSession().getAttribute("student_studentportal");
			String validityExpired = (String)request.getSession().getAttribute("validityExpired");
			
			login_details.setUserId((String)request.getSession().getAttribute("userId"));
			login_details.setStudent((StudentStudentPortalBean)request.getSession().getAttribute("student_studentportal"));
			login_details.setAnnouncements((ArrayList<AnnouncementStudentPortalBean>)request.getSession().getAttribute("announcementsPortal"));
			login_details.setUserBean((UserAuthorizationStudentPortalBean)request.getSession().getAttribute("userAuthorization_studentportal"));
			login_details.setPersonDetails((PersonStudentPortalBean)request.getSession().getAttribute("user_studentportal"));
			login_details.setApplicableSubjects((HashMap<String, String>)request.getSession().getAttribute("programSemSubjectIdWithSubjects_studentportal"));
			login_details.setRegData((StudentStudentPortalBean)request.getSession().getAttribute("studentRecentReg_studentportal"));
			login_details.setHarvard((ELearnResourcesStudentPortalBean)request.getSession().getAttribute("harvard_details"));
			login_details.setStukent((ELearnResourcesStudentPortalBean)request.getSession().getAttribute("stukent_details"));
			login_details.setValidityExpired((String)request.getSession().getAttribute("validityExpired"));
			login_details.setIsLoginAsLead((String)request.getSession().getAttribute("isLoginAsLead"));
			if(login_details.getStudent() != null){
				
				if(!(student.getProgramStatus()!=null && student.getProgramStatus().equalsIgnoreCase(programStatus)) && !validityExpired.equalsIgnoreCase(yesResult)) {
				
				login_details.setCurrentOrder((double)request.getSession().getAttribute("current_order"));
				login_details.setMaxOrderWhereContentLive((double)request.getSession().getAttribute("acadContentLiveOrder"));
				login_details.setRegOrder((double)request.getSession().getAttribute("reg_order"));
				login_details.setAcadSessionLiveOrder((double)request.getSession().getAttribute("acadSessionLiveOrder"));
				
				}
				
				login_details.setCurrentSemPSSId((List<Integer>)request.getSession().getAttribute("currentSemPSSId_studentportal"));
				login_details.setEarlyAccess((String)request.getSession().getAttribute("earlyAccess"));
				login_details.setLiveSessionPssIdAccess((List<Integer>)request.getSession().getAttribute("liveSessionPssIdAccess_studentportal"));
				login_details.setFeatureViseAccess((Map<String, Boolean>)request.getSession().getAttribute("CSFeatureAccess"));
				login_details.setConsumerProgramStructureHasCSAccess((boolean)request.getSession().getAttribute("consumerProgramStructureHasCSAccess"));
				login_details.setCourseraAccess((boolean)request.getSession().getAttribute("courseraAccess"));
				login_details.setSubjectCodeId((List<Integer>)request.getSession().getAttribute("subjectCodeId_studentportal"));
				}else{
					login_details.setCsAdmin((Map<String, Boolean>)request.getSession().getAttribute("csAdmin"));
		    }
			loginSSO.insertStudentDataIntoRedisFromSession(login_details);
		}catch(Exception e) {
			redis_logger.error("Error in inserting student data in redis. Error Message :- ",e);
		}
	}
}
