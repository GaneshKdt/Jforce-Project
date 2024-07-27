package com.nmims.controllers;



import java.io.IOException;

import java.net.HttpURLConnection;


import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;

import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.Enumeration;
import java.io.InputStream;
import java.util.Map;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;

import org.codehaus.jettison.json.JSONArray;
import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;
import org.jsoup.Jsoup; 
import org.jsoup.nodes.Document;

import org.apache.commons.io.FileUtils;
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
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;

import com.froala.editor.File;
import com.froala.editor.Utils;
import com.froala.editor.file.FileOptions;
import com.froala.editor.image.ImageOptions;
import com.google.gson.Gson;
import com.nmims.beans.EmailMessageBean;
import com.nmims.beans.EmailSmsStudentBean;
import com.nmims.beans.LeadStudentPortalBean;
import com.nmims.beans.MailStudentPortalBean;
import com.nmims.beans.ServiceRequestStudentPortal;
import com.nmims.beans.StudentStudentPortalBean;
import com.nmims.daos.EmailMessageDAO;
import com.nmims.daos.LeadDAO;
import com.nmims.daos.PortalDao;
import com.nmims.daos.ServiceRequestDao;
import com.nmims.dto.MailDto;
import com.nmims.helpers.EmailHelper;
import com.nmims.helpers.ExcelHelper;
import com.nmims.helpers.SMSSender;
import com.nmims.services.AnnouncementService;
import com.nmims.services.IEmailSMSService;
import com.nmims.services.PushNotificationOptions;


import net.coobird.thumbnailator.Thumbnails;
import net.coobird.thumbnailator.Thumbnails.Builder;

/**
 * Handles requests for the application home page.
 */
@Controller
@CrossOrigin(origins="*", allowedHeaders="*")
public class EmailSMSController extends BaseController{

	@Autowired
	IEmailSMSService emailSMSService;
	
	@Autowired
	ApplicationContext act;
	
	@Autowired
	EmailHelper emailHelper;
	
	@Autowired
	SMSSender smsSender;
	
	private static final Logger logger = LoggerFactory.getLogger(EmailSMSController.class);
	private static final Logger loggerForEmails = LoggerFactory.getLogger("bulkEmailFromExcel");

	@Autowired
	private ServiceRequestDao serviceRequestDao;

	@Autowired
	LeadDAO leadDAO;
	
	private ArrayList<String> programList = null;
	
	
	@Autowired
	PushNotificationOptions pushNotifyDao;
	
	@Autowired
	AnnouncementService announcementService;
	
	/**
	 * Refresh Cache function to refresh cache
	 * @param 
	 * none
	 * @return 
	 * none
	 * */
	
	@Autowired
	EmailMessageDAO emailMessageDAO;
	
	public String RefreshCache() {
		
		programList = null;
		getProgramList();
		
		
		
		return null;
	}
	
	
/*	private ArrayList<String> yearList = new ArrayList<String>(Arrays.asList( 
			"2008","2009","2010","2011","2012","2013","2014","2015","2016","2017")); */

	public ArrayList<String> getProgramList(){
		if(this.programList == null){
			
			PortalDao dao = (PortalDao)act.getBean("portalDAO");
			this.programList = dao.getAllPrograms();
		}
		return programList;
	}
	
	public ArrayList<String> getProgramListForLeads(){
		if(this.programList == null){
			
			LeadDAO dao = (LeadDAO)act.getBean("leadDAO");
			this.programList = dao.getAllPrograms();
		}
		return programList;
	}
	
	@Value("#{'${PAST_YEAR_LIST}'.split(',')}")
	private List<String> PAST_YEAR_LIST ;
	
	@RequestMapping(value="/myCommunicationWebService",method={RequestMethod.GET,RequestMethod.POST})
	 public void myCommunicationWebService(HttpServletRequest request,HttpServletResponse response){
		String studentNumber = (String)request.getParameter("SN");
		String studentEmailId = (String)request.getParameter("SE");
		String emailBody = (String)request.getParameter("EB");
		String fromEmailId = (String)request.getParameter("FRMID");
		String type  = (String)request.getParameter("T");
		String emailSubject = (String)request.getParameter("ES");
		
		List<String> sapIdRecipients = new ArrayList<String>();
		List<String> mailIdRecipients = new ArrayList<String>();
		ArrayList<MailStudentPortalBean> mailList = new ArrayList<MailStudentPortalBean>();
		
		sapIdRecipients.add(studentNumber);
		mailIdRecipients.add(studentEmailId);
		
		MailStudentPortalBean mailBean = new MailStudentPortalBean();
		mailBean.setSapid(studentNumber);
		mailBean.setBody(emailBody);
		mailBean.setFilterCriteria("Delhivery Dispatch");
		mailBean.setFromEmailId(fromEmailId);
		mailBean.setSapIdRecipients(sapIdRecipients);
		mailBean.setMailIdRecipients(mailIdRecipients);
		mailBean.setSubject(emailSubject);
		mailList.add(mailBean);
		
		emailHelper.createRecordInUserMailTableAndMailTable(mailList,studentNumber,fromEmailId);
	}
	
	
	@RequestMapping(value = "/sendEmailFromExcelForm", method = {RequestMethod.GET, RequestMethod.POST})
	public String sendEmailFromExcelForm(@ModelAttribute ServiceRequestStudentPortal sr, HttpServletRequest request, HttpServletResponse response, Model m) {

		if(!checkSession(request, response)){
			return "jsp/login";
		}
		m.addAttribute("sr", sr);
		return "jsp/sendEmailFromExcel";
	}
	

	@RequestMapping(value="/singleEmailForm",method={RequestMethod.GET,RequestMethod.POST})
	public ModelAndView singleEmailForm(HttpServletRequest request,HttpServletResponse response){
		if(!checkSession(request)){
			return new ModelAndView("jsp/login");
		}
		ModelAndView modelnView = new ModelAndView("jsp/singleEmail");
		PortalDao pDao = (PortalDao)act.getBean("portalDAO");
		String mailTemplateId = (String)request.getParameter("id");
		MailStudentPortalBean mail = (MailStudentPortalBean)pDao.getSingleMail(mailTemplateId);
		
		modelnView.addObject("mail", mail);
		
		return modelnView;
		
	}
	
    private static String mountResponseRequest(HttpURLConnection con, int httpResponse) throws IOException {
        String jsonResponse;
        if (  httpResponse >= HttpURLConnection.HTTP_OK
                && httpResponse < HttpURLConnection.HTTP_BAD_REQUEST) {
            Scanner scanner = new Scanner(con.getInputStream(), "UTF-8");
            jsonResponse = scanner.useDelimiter("\\A").hasNext() ? scanner.next() : "";
            scanner.close();
        }
        else {
            Scanner scanner = new Scanner(con.getErrorStream(), "UTF-8");
            jsonResponse = scanner.useDelimiter("\\A").hasNext() ? scanner.next() : "";
            scanner.close();
        }
        return jsonResponse;
    }
    
	@RequestMapping(value = "/sendEmailFromExcel", method = {RequestMethod.GET, RequestMethod.POST})
	public String sendEmailFromExcel(HttpServletRequest request, @RequestParam("subject") String subject, @RequestParam("body") String body, @RequestParam("fromEmailId") String fromEmailId, 
			@RequestParam("file") MultipartFile file,   Model m){

		if(!checkSession(request)){
			return "jsp/login";
		}
		
		loggerForEmails.info("Start sendEmailFromExcel ");
		
		String userId = (String)request.getSession().getAttribute("userId");

		try {
			ExcelHelper excelHelper = new ExcelHelper();
			ArrayList<List> resultList = excelHelper.readEmailsExcel(file);
			
			ArrayList<String> sapIdList = (ArrayList<String>)resultList.get(0);
			ArrayList<String> emailList = (ArrayList<String>)resultList.get(1);
			ArrayList<String> errorList = (ArrayList<String>)resultList.get(2);

			
			loggerForEmails.info("sapIdList.size() : "+sapIdList.size());
			loggerForEmails.info("emailList.size() : "+emailList.size());
			loggerForEmails.info("errorList.size() : "+errorList.size());
			
			if(errorList.size() > 0){
				loggerForEmails.info("Inside error.");
				setError(m, "Error:"+errorList);
				m.addAttribute("errorList", errorList);
				return "jsp/sendEmailFromExcel";
			}
			//Check if mail body length exceeds limit.//
		
			
			loggerForEmails.info("Email Body length "+body.length());
			if(body.length() > 9500){
				setError(request,"Body Of Mail Cannot be more than 9500 Characters");
				return "jsp/sendEmailFromExcel";
			}
			// avoid mail sending to Program Terminated Student 
			PortalDao pDao = (PortalDao)act.getBean("portalDAO");
			HashMap<String,StudentStudentPortalBean> studentsMap =pDao.getAllStudents();
			
			for(String sapId :studentsMap.keySet()){
				StudentStudentPortalBean student =studentsMap.get(sapId);
				if("Program Terminated".equalsIgnoreCase(student.getProgramStatus())){

					loggerForEmails.info("student.getProgramStatus()--->"+student.getSapid());
					emailList.remove(student.getEmailId()); // remove Program Terminated Student EmailId
					sapIdList.remove(student.getSapid()); // remove Program Terminated Student SapId
				}
			}
			
			loggerForEmails.info("After sapIdList.size() : "+sapIdList.size());
			loggerForEmails.info("After emailList.size() : "+emailList.size());
			
			try{
				emailHelper.sendMassEmails(emailList,sapIdList,subject, fromEmailId, body.replaceAll("<table", "<table border=\"1\" "), userId, "Excel");
//				Added by Somesh for Change mail provider from TNMails to SMTP 
//				emailHelper.sendMassEmailsToStudentViaSMTP(emailList, sapIdList, subject, fromEmailId, body.replaceAll("<table", "<table border=\"1\" "), userId, "Excel");
				setSuccess(m, "Email initiated successfully");
			}catch(Exception e){
				//e.printStackTrace();
				
				loggerForEmails.info("Error in Inner Catch Block " +e.getMessage());
				setError(request,"Error in sending mails. Reason : "+e.getMessage());
			}
		} catch (Exception e) {
			//e.printStackTrace();
			
			loggerForEmails.info("Error in Outer Catch Block "+e.getMessage());
			setError(m, "Error in sending Emails");
		}
		
		loggerForEmails.info("End sendEmailFromExcel ");
		return "jsp/sendEmailFromExcel";
	}
	
	public ArrayList<String> getProgramStructureList(){
			PortalDao dao = (PortalDao)act.getBean("portalDAO");
			ArrayList<String> programStructureList = dao.getProgramStructure();
			return programStructureList;
		}
	
	
	@RequestMapping(value = "/sendEmailToStudentGroupForm", method = {RequestMethod.GET, RequestMethod.POST})
	public String sendEmailToStudentGroupForm(HttpServletRequest request, HttpServletResponse response, Model m) {

		if(!checkSession(request, response)){
			return "jsp/login";
		}
		try {
		StudentStudentPortalBean student = new StudentStudentPortalBean();
	
		m.addAttribute("yearList", PAST_YEAR_LIST);
		m.addAttribute("programList", getProgramList());
		m.addAttribute("subjectcodes", emailSMSService.getSubjectcodeLists());
		m.addAttribute("batchDetails", emailSMSService.getBatchDetails());
		m.addAttribute("programStructureList", getProgramStructureList());
		
		}catch(Exception e)
		{
			logger.error("Error in Fetching details Method :- (sendEmailToStudentGroupForm) ",e);
		}
		m.addAttribute("student",  new EmailSmsStudentBean());
		return "jsp/sendEmailToStudentGroup";
	}
	
	@RequestMapping(value = "/sendEmailToLeadsForm", method = {RequestMethod.GET, RequestMethod.POST})
	public String sendEmailToLeadsForm(HttpServletRequest request, HttpServletResponse response, Model m) {

		if(!checkSession(request, response)){
			return "jsp/login";
		}
		StudentStudentPortalBean student = new StudentStudentPortalBean();
		m.addAttribute("student", student);
		m.addAttribute("yearList", PAST_YEAR_LIST);
		m.addAttribute("programList", getProgramListForLeads());
		m.addAttribute("programStructure", leadDAO.getProgramDetails());
		return "jsp/sendEmailToLeads";
	}
	
	@RequestMapping(value="/student/myEmailCommunicationsForm",method={RequestMethod.GET,RequestMethod.POST})
	public ModelAndView myEmailCommunicationForm(HttpServletRequest request, HttpServletResponse response){
		
	
		
		if(!checkSession(request, response)){
			return  new ModelAndView("jsp/login");
		}
		

		ModelAndView modelAndView = new ModelAndView("jsp/myEmailCommunications");
		
		ArrayList<MailStudentPortalBean> listOfCommunicationMadeToStudent = (ArrayList<MailStudentPortalBean>)request.getSession().getAttribute("listOfCommunicationMadeToStudent_studentportal");
		
		if(listOfCommunicationMadeToStudent == null){
			//Query from DB since it is not in session
			StudentStudentPortalBean student = (StudentStudentPortalBean)request.getSession().getAttribute("student_studentportal");
			PortalDao pDao = (PortalDao)act.getBean("portalDAO");
			if(!checkLead(request, response)) {
				listOfCommunicationMadeToStudent = pDao.getEmailCommunicationMadeToStudent(student.getSapid());
			}else {
				listOfCommunicationMadeToStudent = leadDAO.getCommunicationForLeads();
			}
		}
		
		if(listOfCommunicationMadeToStudent == null || listOfCommunicationMadeToStudent.size() == 0){
			listOfCommunicationMadeToStudent = new ArrayList<MailStudentPortalBean>();
			request.setAttribute("error", "true");
			request.setAttribute("errorMessage", "No Email Communications Made To You");
			request.getSession().setAttribute("listOfCommunicationMadeToStudent_studentportal",listOfCommunicationMadeToStudent);//So that we don't query again for 0 email scenario as well
			return modelAndView;
		}else{
			modelAndView.addObject("firstMail",listOfCommunicationMadeToStudent.get(0));
		}
		request.getSession().setAttribute("listOfCommunicationMadeToStudent_studentportal",listOfCommunicationMadeToStudent);
		modelAndView.addObject("rowCount",listOfCommunicationMadeToStudent.size());

		modelAndView.addObject("isArchived",false);
		/*ArrayList<MailBean> getAllStudentCommunications = pDao.getAllStudentCommunications();*/
		//Get stream of sapid's sent in order to simplify//
		/*String getAllCommaSeperatedSapIdList = pDao.getCommaSeperatedSapIdListFromMailTable();
		if(!"".equals(getAllCommaSeperatedSapIdList) && getAllCommaSeperatedSapIdList != null){
			List<String> listOfSapid = Arrays.asList(getAllCommaSeperatedSapIdList.split("\\s*,\\s*"));
		
			String sapIdForMailSearch = "";
			for(String sapid : listOfSapid){
				if(sapid.equals(student.getSapid())){
					sapIdForMailSearch = student.getSapid();
				
					break;
				}
			}
			if(!"".equals(sapIdForMailSearch)){
				listOfCommunicationMadeToStudent = pDao.getEmailCommunicationMadeToStudent(sapIdForMailSearch);
			}
			
		
				
				if(listOfCommunicationMadeToStudent == null || listOfCommunicationMadeToStudent.size() == 0){
					request.setAttribute("error", "true");
					request.setAttribute("errorMessage", "No Email Communications Made To You");
					return modelAndView;
				}
				
			
		}else{
			//This loop if there are no communications at all in mail table
			setError(request, "No Emailers");
			return modelAndView;
		}*/
		
		return modelAndView;
	}
	
	@RequestMapping(value="/student/emailCommunicationsForm",method={RequestMethod.GET,RequestMethod.POST})
	public ModelAndView emailCommunicationsForm(HttpServletRequest request, HttpServletResponse response){
//		if(!checkSession(request, response)){
//			return  new ModelAndView("jsp/login");
//		}
		
		ModelAndView modelAndView = new ModelAndView("jsp/myEmailCommunications");
		
		ArrayList<MailStudentPortalBean> listOfCommunicationMadeToStudent = leadDAO.getCommunicationForLeads();
		
		if(listOfCommunicationMadeToStudent == null || listOfCommunicationMadeToStudent.size() == 0){
			listOfCommunicationMadeToStudent = new ArrayList<MailStudentPortalBean>();
			request.setAttribute("error", "true");
			request.setAttribute("errorMessage", "No Email Communications Made To You");
			request.getSession().setAttribute("listOfCommunicationMadeToStudent_studentportal",listOfCommunicationMadeToStudent);//So that we don't query again for 0 email scenario as well
			return modelAndView;
		}else{
			modelAndView.addObject("firstMail",listOfCommunicationMadeToStudent.get(0));
		}
		request.getSession().setAttribute("listOfCommunicationMadeToStudent_studentportal",listOfCommunicationMadeToStudent);
		modelAndView.addObject("rowCount",listOfCommunicationMadeToStudent.size());
		
		return modelAndView;
	}
	
	
//	to be deleted, api shifted to rest controller
//	@RequestMapping(value="/m/myEmailCommunicationsForm", method = RequestMethod.POST, consumes = "application/json", produces = "application/json")
//	public ResponseEntity<List<MailDto>> mmyEmailCommunicationForm(@RequestBody  StudentBean student){
//		HttpHeaders headers = new HttpHeaders();
//		headers.add("Content-Type","application/json");
////		if(!checkSession(request, response)){
////			return  new ModelAndView("jsp/login"); 
////		}
//		
//		//ModelAndView modelAndView = new ModelAndView("jsp/myEmailCommunications");
//		
//		//ArrayList<MailBean> listOfCommunicationMadeToStudent = (ArrayList<MailBean>)request.getSession().getAttribute("listOfCommunicationMadeToStudent_studentportal");
//		
//		//if(listOfCommunicationMadeToStudent == null){
//			//Query from DB since it is not in session
//		//	StudentBean student = (StudentBean)request.getSession().getAttribute("student_studentportal");
//			PortalDao pDao = (PortalDao)act.getBean("portalDAO");
//			
//			List<MailDto> mailDtoList = new ArrayList<MailDto>();
//			
//			MailDto mailDto = null;
//			
//			List<MailBean> listOfCommunicationMadeToStudent  = (ArrayList<MailBean>)pDao.getEmailCommunicationMadeToStudent(student.getSapid());
//		//}
//			for(MailBean bean : listOfCommunicationMadeToStudent)
//			{
//				mailDto = new MailDto();
//				mailDto.setBody(bean.getBody());
//				mailDto.setSubject(bean.getSubject());
//				mailDto.setCreatedDate(bean.getCreatedDate());
//				mailDto.setId(bean.getId());
//				mailDto.setFilterCriteria(bean.getFilterCriteria());
//				mailDto.setFromEmailId(bean.getFromEmailId());
//				
//				mailDtoList.add(mailDto);
//				
//			}
//		
//		if(listOfCommunicationMadeToStudent == null || listOfCommunicationMadeToStudent.size() == 0){
//			mailDtoList = new ArrayList<MailDto>();
//			//request.setAttribute("error", "true");
//			//request.setAttribute("errorMessage", "No Email Communications Made To You");
//			//request.getSession().setAttribute("listOfCommunicationMadeToStudent_studentportal",listOfCommunicationMadeToStudent);//So that we don't query again for 0 email scenario as well
//			//return modelAndView;
//		}
//		//request.getSession().setAttribute("listOfCommunicationMadeToStudent_studentportal",listOfCommunicationMadeToStudent);
//		//modelAndView.addObject("rowCount",listOfCommunicationMadeToStudent.size());
//		
//		/*ArrayList<MailBean> getAllStudentCommunications = pDao.getAllStudentCommunications();*/
//		//Get stream of sapid's sent in order to simplify//
//		/*String getAllCommaSeperatedSapIdList = pDao.getCommaSeperatedSapIdListFromMailTable();
//		if(!"".equals(getAllCommaSeperatedSapIdList) && getAllCommaSeperatedSapIdList != null){
//			List<String> listOfSapid = Arrays.asList(getAllCommaSeperatedSapIdList.split("\\s*,\\s*"));
//			
//			String sapIdForMailSearch = "";
//			for(String sapid : listOfSapid){
//				if(sapid.equals(student.getSapid())){
//					sapIdForMailSearch = student.getSapid();
//					
//					break;
//				}
//			}
//			if(!"".equals(sapIdForMailSearch)){
//				listOfCommunicationMadeToStudent = pDao.getEmailCommunicationMadeToStudent(sapIdForMailSearch);
//			}
//			
//			
//				
//				if(listOfCommunicationMadeToStudent == null || listOfCommunicationMadeToStudent.size() == 0){
//					request.setAttribute("error", "true");
//					request.setAttribute("errorMessage", "No Email Communications Made To You");
//					return modelAndView;
//				}
//				
//			
//		}else{
//			//This loop if there are no communications at all in mail table
//			setError(request, "No Emailers");
//			return modelAndView;
//		}*/
//		return new ResponseEntity<List<MailDto>>(mailDtoList, headers, HttpStatus.OK);
//
//	
//	}
	@RequestMapping(value="/checkNumberBasedOnStudentGroup",method={RequestMethod.GET,RequestMethod.POST})
	public String checkNumberBasedOnStudentGroup(HttpServletRequest request, HttpServletResponse response, @ModelAttribute EmailSmsStudentBean  student, Model m){
		if(!checkSession(request, response)){
			return "jsp/login";
		}
		String userId = (String)request.getSession().getAttribute("userId");
		try {
		m.addAttribute("student", student);
		m.addAttribute("yearList", PAST_YEAR_LIST);
		m.addAttribute("programList", getProgramList());
		m.addAttribute("subjectcodes", emailSMSService.getSubjectcodeLists());
		m.addAttribute("batchDetails", emailSMSService.getBatchDetails());
		
		PortalDao pDao = (PortalDao)act.getBean("portalDAO");
		ArrayList<StudentStudentPortalBean> studentsList = null;
		//studentsList = pDao.getStudentsListByCriteria(student);
		studentsList = emailSMSService.getStudentsListByCriteria(student);
		setSuccess(request,"Number Of Students-->"+studentsList.size());
		
		}catch(Exception e)
		{
			e.printStackTrace();
			setError(request,"Error in Getting Student list. "+e.getMessage());
		}
		
		return "jsp/sendEmailToStudentGroup";
	}
	
	@RequestMapping(value="/checkNumberForLeads",method={RequestMethod.GET,RequestMethod.POST})
	public String checkNumberForLeads(HttpServletRequest request, HttpServletResponse response, @ModelAttribute StudentStudentPortalBean student, Model m){
		if(!checkSession(request, response)){
			return "jsp/login";
		}

		m.addAttribute("student", student);
		m.addAttribute("yearList", PAST_YEAR_LIST);
		m.addAttribute("programList", getProgramList());
		
		LeadDAO lDao = (LeadDAO)act.getBean("leadDAO");
		int numberOfStudents = lDao.leadCount();
		setSuccess(request,"Number Of Students: "+numberOfStudents);
		
		return "jsp/sendEmailToLeads";
	}
	
	@RequestMapping(value = "/sendEmailToStudentGroup", method = {RequestMethod.GET, RequestMethod.POST})
	public String sendEmailToStudentGroup(HttpServletRequest request, HttpServletResponse response, @ModelAttribute EmailSmsStudentBean  student, Model m){
		if(!checkSession(request, response)){
			return "jsp/login";
		}
		String userId = (String)request.getSession().getAttribute("userId");
		m.addAttribute("student", student);
		m.addAttribute("yearList", PAST_YEAR_LIST);
		m.addAttribute("programList", getProgramList());
		
		ArrayList<String> emailList = new ArrayList<>();
		ArrayList<String> sapIdList = new ArrayList<>();
		ArrayList<StudentStudentPortalBean> studentsList = new ArrayList<>();
		ArrayList<StudentStudentPortalBean> studentsListForSMS = new ArrayList<StudentStudentPortalBean>();
		List<String> onesignalIdList  = new ArrayList<>();
		String successMessage = "";
		String errorMessage = "";
		String isPushnotification = student.getIsPushnotification();
		String pushContent = student.getPushContent();
		String pushSubject=student.getSubject();
		
		try {
			m.addAttribute("subjectcodes", emailSMSService.getSubjectcodeLists());
			m.addAttribute("batchDetails", emailSMSService.getBatchDetails());
			
			PortalDao pDao = (PortalDao)act.getBean("portalDAO");
			//studentsList = pDao.getStudentsListByCriteria(student);//Exclude program terminated students
			//String criteria = pDao.getStudentsCriteria(student);
			studentsList = emailSMSService.getStudentsListByCriteria(student);//Exclude program terminated students
			
			String criteria = pDao.getStudentsCriteria(student.getEnrollmentMonth(),student.getEnrollmentYear(),student.getAcadMonth(),student.getAcadYear(),student.getSem(),student.getPrgmStructApplicable(),student.getProgram());
			
			if(studentsList == null || studentsList.size() == 0){
				setError(request, "No students found matching with criteria");
				return "sendEmailToStudentGroup";
			}else{
				ArrayList<String> validStudentsList = pDao.getAllValidStudents();//Exclude program terminated students
				
				for (StudentStudentPortalBean studentBean : studentsList) {
					if(validStudentsList.contains(studentBean.getSapid())){
						emailList.add(studentBean.getEmailId());
						sapIdList.add(studentBean.getSapid());
						onesignalIdList.add( studentBean.getOnesignalId()) ;
						studentsListForSMS.add(studentBean);
					}
				}
			}
			if("Email".equalsIgnoreCase(student.getNotificationType()) || "Email-SMS".equalsIgnoreCase(student.getNotificationType())){
				try{
					emailHelper.sendMassEmails(emailList,sapIdList,student.getSubject(), student.getFromEmailId(), 
							student.getBody().replaceAll("<table", "<table border=\"1\" "), userId, criteria);
					successMessage = successMessage + "Email initiated successfully for " + emailList.size() + " students <br/>";
					
					Document doc = Jsoup.parse(student.getBody());
			       
					setSuccess(request, successMessage);
				}catch(Exception e){
					//e.printStackTrace();
					errorMessage = errorMessage + "Error in sending Emails: Reason : "+e.getMessage() + "<br/>";
					setError(request,errorMessage);
				}
			}
			
			if("SMS".equalsIgnoreCase(student.getNotificationType()) || "Email-SMS".equalsIgnoreCase(student.getNotificationType())){
				try{
					smsSender.sendMassSMS(studentsListForSMS,  student.getSmsContent(), userId, criteria);
					successMessage = successMessage + "SMS initiated successfully for " + emailList.size() + " students <br/>";
					 
					setSuccess(request, successMessage);
				}catch(Exception e){
					//e.printStackTrace();
					errorMessage = errorMessage + "Error in sending SMS: Reason : "+e.getMessage() + "<br/>";
					setError(request,errorMessage);
				}
			}
			if("Y".equalsIgnoreCase(isPushnotification) ){
				try{
					
					try {
						
					    pushNotifyDao.sendMessageToPlayerIds(pushSubject,pushContent,onesignalIdList );
							
						} catch (Exception e) {
							//e.printStackTrace();
							setError(m, "Error in sending Push notification");
						} 
				}catch(Exception e){
					//e.printStackTrace();
					errorMessage = errorMessage + "Error in sending Push notification : "+e.getMessage() + "<br/>";
					setError(request,errorMessage);
				}
			}
			
		} catch (Exception e) {
			//e.printStackTrace();
			errorMessage = errorMessage + "Error in sending Emails/SMS" + e.getMessage();
			setError(request, errorMessage);
		}
		
		return "jsp/sendEmailToStudentGroup";
	}
	
	@RequestMapping(value = "/sendEmailToLeads", method = {RequestMethod.GET, RequestMethod.POST})
	public String sendEmailToLeads(HttpServletRequest request, HttpServletResponse response, @ModelAttribute StudentStudentPortalBean student, Model m){
		if(!checkSession(request, response)){
			return "jsp/login";
		}
		String userId = (String)request.getSession().getAttribute("userId");
		m.addAttribute("student", student);
		m.addAttribute("yearList", PAST_YEAR_LIST);
		m.addAttribute("programList", getProgramList());
		
		List<String> emailList = new ArrayList<>();
		String successMessage = "";
		String errorMessage = "";
		
		try {
			LeadDAO lDao = (LeadDAO)act.getBean("leadDAO");
			emailList = lDao.getLeadList();
			
			if(emailList == null || emailList.size() == 0){
				setError(request, "No students found matching with criteria");
				return "jsp/sendEmailToLeads";
			}else{
				try{
					emailHelper.sendMassEmailsToLeads(emailList,student.getSubject(), student.getFromEmailId(), 
							student.getBody().replaceAll("<table", "<table border=\"1\" "), userId);
					successMessage = successMessage + "Email initiated successfully for " + emailList.size() + " students <br/>";
					
					Document doc = Jsoup.parse(student.getBody());
			       
					setSuccess(request, successMessage);
				}catch(Exception e){
					//e.printStackTrace();
					errorMessage = errorMessage + "Error in sending Emails: Reason : "+e.getMessage() + "<br/>";
					setError(request,errorMessage);
				}
			}
			
		} catch (Exception e) {
			//e.printStackTrace();
			errorMessage = errorMessage + "Error in sending Emails/SMS" + e.getMessage();
			setError(request, errorMessage);
		}
		
		return "jsp/sendEmailToLeads";
	}
	
	@RequestMapping(value="/admin/uploadFileForMail",method={RequestMethod.POST})
	public void uploadTemporaryFileForMail(HttpServletRequest request, HttpServletResponse response) throws Exception{

		 
		 InputStream in = request.getInputStream();
		 javax.mail.internet.InternetHeaders headers = new javax.mail.internet.InternetHeaders(in);		 
		 Enumeration headerEnumeration = headers.getAllHeaders();
		 String fileName = null;

		 	while (headerEnumeration.hasMoreElements()) {		    	
			     javax.mail.Header header = (javax.mail.Header) headerEnumeration.nextElement();			    
			      if(header.getName().equals("Content-Disposition")) { 
			    	  fileName = header.getValue().replaceFirst("(?i)^.*filename=\"?([^\"]+)\"?.*$", "$1");			  
			      }		      
			}
		 	FileOptions options = new FileOptions();
		 	options.setValidation(null);
		 	Map<Object, Object> responseData;
	        try {
	            responseData = uploadByInputStream(request, options,in,fileName);
	        } catch (Exception e) {
	            //e.printStackTrace();
	            responseData = new HashMap<Object, Object>();
	            responseData.put("error", e.toString());
	        }
	        String jsonResponseData = new Gson().toJson(responseData);
	        response.setContentType("application/json");
	        response.setCharacterEncoding("UTF-8");
	        response.getWriter().write(jsonResponseData);
	}
	
	public Map<Object, Object> uploadByInputStream(HttpServletRequest req, FileOptions options,InputStream fileContent,String fileName)
			throws Exception {
	
		String fileRoute = "E:\\MassEmailAttachments\\";
		if (options == null) {
			options = new FileOptions();;
		}
		if (req.getContentType() == null || req.getContentType().toLowerCase().indexOf("multipart/form-data") == -1) {
			throw new Exception("Invalid contentType. It must be " + "multipart/form-data");
		}
		String uniqueFileName = Utils.generateUniqueString() + fileName;
		String linkName = "https://studentzone-ngasce.nmims.edu/MassEmailAttachments/" +  uniqueFileName;	//for public url

		String filePath = fileRoute + uniqueFileName;
		java.io.File targetFile = new java.io.File(filePath);

		// Resize image.
		if (options instanceof ImageOptions && ((ImageOptions) options).getResizeOptions() != null) {

			ImageOptions.ResizeOptions imageOptions = ((ImageOptions) options).getResizeOptions();

			Builder<? extends InputStream> thumbnailsBuilder = Thumbnails.of(fileContent);

			// Check aspect ratio.
			int newWidth = imageOptions.getNewWidth();
			int newHeight = imageOptions.getNewHeight();
			if (imageOptions.getKeepAspectRatio()) {
				thumbnailsBuilder = thumbnailsBuilder.size(newWidth, newHeight);
			} else {
				thumbnailsBuilder = thumbnailsBuilder.forceSize(newWidth, newHeight);
			}
			
			thumbnailsBuilder.toFile(targetFile);
		} else {
			FileUtils.copyInputStreamToFile(fileContent, targetFile);			
		}

		if (options.getValidation() != null
				&& !options.getValidation().check(filePath, "multipart/form-data")) {
			
			File.delete(req, linkName);
			throw new Exception("File does not meet the validation.");
		}

		Map<Object, Object> linkObject = new HashMap<Object, Object>();
		linkObject.put("link", linkName);
		return linkObject;
	}
	

//	to be deleted, api shifted to rest controller
//	@RequestMapping(value="/m/emailCommunicationsForLeads", method = RequestMethod.POST, consumes = "application/json", produces = "application/json")
//	public ResponseEntity<ArrayList<MailBean>> emailCommunicationsForLeads(@RequestBody  StudentBean student){
//		HttpHeaders headers = new HttpHeaders();
//		headers.add("Content-Type","application/json");
//
//		ArrayList<MailBean> listOfCommunicationMadeToStudent = leadDAO.getCommunicationForLeads();
//		
//		return new ResponseEntity<ArrayList<MailBean>>(listOfCommunicationMadeToStudent, headers, HttpStatus.OK);
//	}

	@RequestMapping(value="/uploadCommunicationModuleForm",method={RequestMethod.GET})
	public ModelAndView uploadCommunicationModuleForm(HttpServletRequest request, HttpServletResponse response) throws Exception{
		
		ModelAndView modelAndView = new ModelAndView("jsp/uploadCommunicationModule");
		return modelAndView;
		
	}
	
	@RequestMapping(value="/uploadCommunicationModule",method={RequestMethod.GET})
	public ModelAndView uploadCommunicationModule(HttpServletRequest request, HttpServletResponse response) throws Exception{
		
		ModelAndView modelAndView = new ModelAndView("jsp/uploadCommunicationModule");
		return modelAndView;
		
	}

	@RequestMapping(value="/emailTemplate",method={RequestMethod.GET})
	public ModelAndView emailTemplate(HttpServletRequest request, HttpServletResponse response){
		
		ModelAndView modelAndView = new ModelAndView("jsp/emailTemplate");
		
		ArrayList<EmailMessageBean> modules = new ArrayList<>();
		try {
			modules = emailMessageDAO.getModulesForMail();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		modelAndView.addObject("modules", modules);
		
		return modelAndView;
		
	}
	
	@RequestMapping(value="/getMessageForModule", method = RequestMethod.POST, consumes = "application/json", produces = "application/json")
	public ResponseEntity<EmailMessageBean> getMessageForModule( @RequestBody EmailMessageBean bean){

		HttpHeaders headers = new HttpHeaders();
		headers.add("Content-Type", "application/json");

		EmailMessageBean message = new EmailMessageBean();
		try {
			message = emailMessageDAO.getMessageForModule(bean);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return new ResponseEntity<>(message, headers, HttpStatus.OK);
		
	}	
	
	@RequestMapping(value="/updateMessage", method = RequestMethod.POST, consumes = "application/json", produces = "application/json")
	public ResponseEntity<EmailMessageBean> updateMessage( HttpServletRequest request,  @RequestBody EmailMessageBean bean){

		HttpHeaders headers = new HttpHeaders();
		headers.add("Content-Type", "application/json");
		
		bean.setLastModifiedBy((String)request.getSession().getAttribute("userId"));
		/* 
		 * to find the variables that are marked by #
		Matcher matcher = Pattern.compile("#\\s*(\\w+)").matcher(bean.getBody());
		while (matcher.find()) {
		  System.out.println(matcher.group(1));
		}
		*

		String body = bean.getBody();
		String subject = bean.getSubject();
		
		Matcher matcher = Pattern.compile("#\\s*(\\w+)").matcher(body);
		while (matcher.find()) {
			String instance = matcher.group(1);

			switch (instance) {
			case "name":
				body = body.replace("#name","Harsh");
				break;
			case "sapid":
				body = body.replace("#sapid","77777788888");
				break;
			case "program":
				body = body.replace("#program","PGDM");
				break;
			case "yearMonth":
				body = body.replace("#yearMonth","2020-09-20 02:51:00");
				break;
			case "subject":
				body = body.replace("#subject","Baiscs of Python");
				break;
			case "testName":
				body = body.replace("#testName","DemoTest");
			case "testEndedOn":
				body = body.replace("#testEndedOn","2020-09-20 02:51:00");
				break;
			case "attempt":
				body = body.replace("#attempt","1");
				break;
			case "maxAttempt":
				body = body.replace("#maxAttempt","1");
				break;
			default:
				break;
			}
		}
		
		matcher = Pattern.compile("#\\s*(\\w+)").matcher(subject);
		while (matcher.find()) {
			String instance = matcher.group(1);

			switch (instance) {
			case "subject":
				subject = subject.replace("#subject","Basics of Python");
				break;
			case "testName":
				subject = subject.replace("#testName","DemoTest");
				break;
			case "yearMonth":
				subject = subject.replace("#yearMonth","2020-09-20 02:51:00");
				break;
			default:
				break;
			}
		}
		bean.setBody(body);
		bean.setSubject(subject);
		
		
		
		*/	
		try {
			
			emailMessageDAO.updateMessage(bean);
			emailMessageDAO.updateProviderForModule(bean);
			bean.setSuccess(true);
			bean.setMessage("Email template was updated successfully");
			return new ResponseEntity<>(bean, headers, HttpStatus.OK);
			
		}catch (Exception e) {

			e.printStackTrace();
			bean.setSuccess(false);
			bean.setMessage("An error occured while updating the template");
			return new ResponseEntity<>(bean, headers, HttpStatus.INTERNAL_SERVER_ERROR);
			
		}
		
	}
	
	/*
	 *This call is used to fetch all archived email communication of a current user 
	 *@return	String	:returning logical view name 
	 *@date 	Feb 13, 2021 */
	@GetMapping("/student/viewArchivedCommunication")
	public ModelAndView getAllEmailCommunication(Map<String,Object> map, 
											HttpServletRequest request, 
											HttpServletResponse response) {
		List<MailStudentPortalBean> mailCommunicationsList=null;
		HttpSession session=null;
		StudentStudentPortalBean student=null;
		
		//Verify session is expired 

		if(!checkSession(request, response)){
			return  new ModelAndView("jsp/login");
		}
		
		ModelAndView modelAndView = new ModelAndView("jsp/myEmailCommunications");
		
		//Creating session object
		session=request.getSession();
		
		//getting student record from session
		student=(StudentStudentPortalBean) session.getAttribute("student_studentportal");
		
		try {
			//check is current user is lead or not 
			if(!checkLead(request, response)) {
				//Get data by applying union on both current and history table
				mailCommunicationsList= emailSMSService.fetchArchivedEmailCommunication(student.getSapid());
				
			}
			else {
				//get leads mails
				mailCommunicationsList = leadDAO.getCommunicationForLeads();
			}
		}//try
		catch (Exception e) {
			//e.printStackTrace();
			//adding error message if any exceptions caught
			request.setAttribute("error", "true");
			request.setAttribute("errorMessage", "Some thing went wrong. Please try again or some time.");
			session.setAttribute("listOfCommunicationMadeToStudent_studentportal",mailCommunicationsList);
			return modelAndView;	
		}//catch
		
		//add first mail info to map to show in details to user if and only if mailCommunicatonsList is not null or size not zero 
		if(mailCommunicationsList !=null && mailCommunicationsList.size() !=0) {
			modelAndView.addObject("firstMail",mailCommunicationsList.get(0));	
		}//if
		else {
			//adding error message if mailCommunication is null or size is zero
			request.setAttribute("error", "true");
			request.setAttribute("errorMessage", "No Email Communications Made To You");
			session.setAttribute("listOfCommunicationMadeToStudent_studentportal",mailCommunicationsList);
			//return logical view name to show page with error message
			return modelAndView;
		}//else
		
		request.getSession().setAttribute("listOfCommunicationMadeToStudent_studentportal",mailCommunicationsList);
		modelAndView.addObject("rowCount", mailCommunicationsList.size());
		modelAndView.addObject("isArchived",true);
		
		/*
		//adding row count to map
		map.put("rowCount", mailCommunicationsList.size());
		
		//add mailCommunicationsList as model data into map
		map.put("listOfCommunicationMadeToStudent",mailCommunicationsList);
		
		//Sending isArchved as true to disable viewArchivedEmail link 
		map.put("isArchived", true);
		
		//return logical view name
		map.put("isArchived", true);
		*/
		return modelAndView;
		
	}//getAllEmailCommunication()
		
	
	/*
	 *@return	String	: return logical view name as string value
	 *@date		Feb 15, 2021 */
	@GetMapping("singleArchiveEmailForm")
	public String retrieveSingleArchieveEmail(Map<String,Object> map,
												HttpServletRequest request,
												HttpServletResponse response){
		MailStudentPortalBean mailBean=null;
		if(!checkSession(request)){
			return "jsp/login";
		}
		
		//Read query string parameter value
		String mailTemplateId = request.getParameter("id");
		
		try {
			//getting mail bean by passing mailTemplateId
			mailBean = emailSMSService.fetchSingleArchiveEmail(mailTemplateId);
		}//try
		catch (Exception e) {
			//e.printStackTrace();
			//adding error message if any exceptions caught
			request.setAttribute("error", "true");
			request.setAttribute("errorMessage", "Some thing went wrong. Please try again or some time.");
		}
		//put model data in map
		map.put("mail", mailBean);
				
		//return logical view name
		return "singleEmail";
	}//retrieveSingleArchieveEmail
	
}

