package com.nmims.listeners;

import java.io.UnsupportedEncodingException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import javax.mail.MessagingException;
import javax.mail.NoSuchProviderException;
import javax.servlet.ServletConfig;
import javax.servlet.http.HttpSession;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.context.ServletConfigAware;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.RequestContextListener;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.filter.RequestContextFilter;

import bookingservice.wsdl.BandwidthOverride;
import bookingservice.wsdl.Conference;
import bookingservice.wsdl.ConferenceType;

import com.nmims.beans.FacultyAcadsBean;
import com.nmims.beans.MailAcadsBean;
import com.nmims.beans.SessionDayTimeAcadsBean;
import com.nmims.beans.StudentAcadsBean;
import com.nmims.beans.TestAcadsBean;
import com.nmims.daos.ConferenceDAO;
import com.nmims.daos.NotificationDAO;
import com.nmims.helpers.ConferenceBookingClient;
import com.nmims.helpers.MailSender;
import com.nmims.helpers.MobileNotificationHelper;
import com.nmims.helpers.SMSSender;
import com.nmims.helpers.SubjectAbbreviationHelper;
import com.nmims.services.StudentService;

@Controller
public class NotificationScheduler implements ApplicationContextAware, ServletConfigAware {

	private static ApplicationContext act = null;
	private static ServletConfig sc = null;

	@Value( "${ENVIRONMENT}" )
	private String ENVIRONMENT;
	
	@Value( "${SERVER}" )
	private String SERVER;
	
	@Value("${TIMEBOUND_PORTAL_LIST}")
	private List<String> TIMEBOUND_PORTAL_LIST;

	@Autowired
	SubjectAbbreviationHelper subjectAbbreviationHelper;
	
	@Autowired
	private NotificationDAO notificationDAO;
	
	@Autowired
	private ConferenceBookingClient conferenceBookingClient;
	
	@Autowired
	private SMSSender smsSender;
	
	@Autowired
	private MobileNotificationHelper mobileNotificationHelper;
	
	@Autowired
	private StudentService studentService;

	private static final Logger loggerForSessionSMSs = LoggerFactory.getLogger("session_SMS");
	private static final Logger loggerForSessionEmails = LoggerFactory.getLogger("session_emails");
	private static final Logger loggerForSessionFirebaseNotifications = LoggerFactory.getLogger("session_firebase_notification");
	private static final Logger loggerForSessionNotificationFaculty = LoggerFactory.getLogger("session_Notification_Faculty");
	
	private ArrayList<String> nonPG_ProgramList = new ArrayList<String>(Arrays.asList("BBA", "B.Com", "PD - WM","PD - DM",
											"M.Sc. (App. Fin.)", "CP-WL", "CP-ME", "BBA-BA"));
	
	@Override
	public void setServletConfig(ServletConfig config) {
		sc = config;
	}
 
	@Override
	public void setApplicationContext(ApplicationContext context) throws BeansException {
		act = context;
	}
	
	//Commented by Somesh added separate scheduler for PG and MBA-WX
	//@Scheduled(fixedDelay = 30 * 60 * 1000)
	public void sendTodaysSessionEmails(){
		try {
			System.out.println("Server = "+SERVER);
			System.out.println("Started Email scheduler for "+ENVIRONMENT); 
			if(!"tomcat4".equalsIgnoreCase(SERVER) || !"PROD".equalsIgnoreCase(ENVIRONMENT)){
				System.out.println("Not running email scheduler since this is not tomcat4. This is "+SERVER);
				return;
			}
			
			ArrayList<String> allAvailableCorporateCenters = notificationDAO.getAllCorporateCenterNames();
			//ArrayList<SessionDayTimeBean> scheduledSessionList = notificationDAO.getScheduledSessionForDay();
			ArrayList<SessionDayTimeAcadsBean> scheduledSessionList = notificationDAO.getScheduledSessionForDayForEmail();
			System.out.println("scheduledSessionList = "+scheduledSessionList);
			if(scheduledSessionList != null && scheduledSessionList.size() > 0 && "PROD".equalsIgnoreCase(ENVIRONMENT)){
				for (int i = 0; i < scheduledSessionList.size(); i++) {
					SessionDayTimeAcadsBean session = scheduledSessionList.get(i);
					String subject = session.getSubject();
					String corporateCenterName = session.getCorporateName();
					String hasModuleId = session.getHasModuleId();
					String sessionId = session.getId();
					ArrayList<StudentAcadsBean> studentList = null;
					String year = session.getYear();
					String month = session.getMonth();
					studentList = notificationDAO.getRegisteredStudentForSubjectNew(sessionId, hasModuleId, year, month);
					
					//Commented as added new query with session configurable 
					/*
					if(allAvailableCorporateCenters.contains(corporateCenterName)){
						studentList = notificationDAO.getRegisteredStudentForSubject(subject,corporateCenterName,hasModuleId,new ArrayList<String>());
						System.out.println("corporateCenterName :: "+corporateCenterName);
					}else{
						studentList = notificationDAO.getRegisteredStudentForSubject(subject,null,hasModuleId,allAvailableCorporateCenters); 
					}
					*/
					
					System.out.println("Sending Emails for "+subject);
					
					if(studentList != null && studentList.size() > 0){
						sendEmailsToStudent(session, studentList);
					}

				}
			}

		} catch (Exception e) {
			  
		}

	}
	
	private void sendEmailsToStudent(SessionDayTimeAcadsBean session, ArrayList<StudentAcadsBean> studentList) {
		MailSender mailSender = (MailSender)act.getBean("mailer");
		try {
			mailSender.sendEmails(session, studentList);
			notificationDAO.updateEmailStatus(session,"Y");
		} catch (Exception e) {
			  
			ArrayList<String> recipent = new ArrayList<String>(Arrays.asList("jforce.solution@gmail.com"));
			mailSender.sendEmail("EMAIL NOT SEND","EMAIL Not Due to <br><br>"+e.getMessage(),recipent);
		}
	}
	
	//send email notification for  test start


	private void sendTestNotificationEmailsToStudent(TestAcadsBean test, List<StudentAcadsBean> studentList) {
		MailSender mailSender = (MailSender)act.getBean("mailer");
		try {
			mailSender.sendTestNotificationEmails(test, studentList);
			notificationDAO.updateTestNotificationEmailStatus(test,"Y");
		} catch (Exception e) {
			  
			ArrayList<String> recipent = new ArrayList<String>(Arrays.asList("jforce.solution@gmail.com"));
			mailSender.sendEmail("TEST NOTIFICATION EMAIL NOT SEND","TEST NOTIFICATION EMAIL Not Due to <br><br>"+e.getMessage(),recipent);
		}
	}
	
	
	@Scheduled(fixedDelay = 30 * 60 * 1000)
	public void sendTestsNotificationEmails(){
		try {
			System.out.println("IN sendTestsNotificationEmails Server = "+SERVER);
			System.out.println("IN sendTestsNotificationEmails Started Email scheduler for "+ENVIRONMENT); 
			
			  if(!"tomcat4".equalsIgnoreCase(SERVER) ||
			  !"PROD".equalsIgnoreCase(ENVIRONMENT)){ System.out.
			  println("Not running email scheduler since this is not tomcat4. This is "
			  +SERVER); return; }
			 
			

			List<TestAcadsBean> testsScheduledForTomorrow = notificationDAO.getTestsScheduledForTomorrowNSMSNotSent();
			
			for (TestAcadsBean test : testsScheduledForTomorrow) {
				System.out.println("IN sendEmailForTestsReminders got test : "+test.getTestName());
				
				try {
					String startDate = test.getStartDate();
					String endDate = test.getEndDate();
					//System.out.println("In sendEmailForTestsReminders got startDate : "+startDate);
					//System.out.println("In sendEmailForTestsReminders got endDate : "+endDate);
					SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
					SimpleDateFormat sdf2 = new SimpleDateFormat("E, dd MMM yyyy HH:mm z");
					Date sDate = sdf.parse(startDate.replaceAll("T", " "));
					//Date cDate = new Date();
					//Date eDate = sdf.parse(endDate.replaceAll("T", " "));
					//String newStartDate = sdf2.format(sDate);
					
					//System.out.println("In sendEmailForTestsReminders got formated startDate : "+sDate);
					//System.out.println("In sendEmailForTestsReminders got formated currentDate : "+cDate);
					//System.out.println("In sendEmailForTestsReminders got formated endDate : "+eDate);
					
					String startDateForMail = sdf2.format(sDate);
					//System.out.println("In sendEmailForTestsReminders got startDateForMail : "+startDateForMail);
					
					test.setStartDateForMail(startDateForMail);
					
					List<StudentAcadsBean> studentsApllicableForTest  = notificationDAO.getStudentsEligibleForTestByTestid(test.getId());
					
					if(studentsApllicableForTest != null && studentsApllicableForTest.size() > 0){
						sendTestNotificationEmailsToStudent(test, studentsApllicableForTest);
					}
				} catch (ParseException e) {
					// TODO Auto-generated catch block
					  
				}
				
				
			}

		} catch (Exception e) {
			  
		}

	}
	//send email notification for  test end
	

	//Commented by Somesh added separate scheduler for PG and MBA-WX
	//@Scheduled(fixedDelay = 60 * 60 * 1000)
	public void sendTodaysSessionSMSs(){
		System.out.println("Started SMS scheduler for "+ENVIRONMENT);
		if(!"tomcat4".equalsIgnoreCase(SERVER) || !"PROD".equalsIgnoreCase(ENVIRONMENT)){
			System.out.println("Not running SMS scheduler since this is not tomcat4. This is "+SERVER);
			return;
		}
		
		ArrayList<String> allAvailableCorporateCenters = notificationDAO.getAllCorporateCenterNames();
		ArrayList<SessionDayTimeAcadsBean> scheduledSessionList = notificationDAO.getScheduledSessionForDayForSMS();
		System.out.println("scheduledSessionList = "+scheduledSessionList);
		if(scheduledSessionList != null && scheduledSessionList.size() > 0 && "PROD".equalsIgnoreCase(ENVIRONMENT)){
			for (int i = 0; i < scheduledSessionList.size(); i++) {
				SessionDayTimeAcadsBean session = scheduledSessionList.get(i);
				String subject = session.getSubject();
				System.out.println("Sending SMS for "+subject);
				String corporateCenterName = session.getCorporateName();
				String hasModuleId = session.getHasModuleId();
				String sessionId = session.getId();
				ArrayList<StudentAcadsBean> studentList = null;
				
				String year = session.getYear();
				String month = session.getMonth();
				studentList = notificationDAO.getRegisteredStudentForSubjectNew(sessionId, hasModuleId, year, month);
				
				//Commented as added new query with session configurable 
				/*
				if(allAvailableCorporateCenters.contains(corporateCenterName)){
					studentList = notificationDAO.getRegisteredStudentForSubject(subject,corporateCenterName,hasModuleId,new ArrayList<String>()); 
				}else{
					studentList = notificationDAO.getRegisteredStudentForSubject(subject,null,hasModuleId,allAvailableCorporateCenters); 
				}
				*/
				if(studentList != null && studentList.size() > 0){
					sendSMSsToStudent(session, studentList);
				}
			}
		}

	}
	
	//Commented by Somesh added separate scheduler for PG and MBA-WX
	//@Scheduled(fixedDelay = 30 * 60 * 1000)
	public void sendTodaysSessionMobileFirebaseNotification(){		//before 30 min session start
		System.out.println("Started Firebase scheduler for "+ENVIRONMENT);
		if(!"tomcat4".equalsIgnoreCase(SERVER) || !"PROD".equalsIgnoreCase(ENVIRONMENT)){
			System.out.println("Not running SMS scheduler since this is not tomcat4. This is "+SERVER);
			return;
		} 
		 
		ArrayList<String> allAvailableCorporateCenters = notificationDAO.getAllCorporateCenterNames();
		ArrayList<SessionDayTimeAcadsBean> scheduledSessionList = notificationDAO.getScheduledSessionForFirebase();		//temp only for MBAWx
		System.out.println("scheduledSessionList = "+scheduledSessionList);
		if(scheduledSessionList != null && scheduledSessionList.size() > 0){
			for (int i = 0; i < scheduledSessionList.size(); i++) {
				SessionDayTimeAcadsBean session = scheduledSessionList.get(i);
				String subject = session.getSubject();
				System.out.println("Sending SMS for "+subject);
				String corporateCenterName = session.getCorporateName();
				String hasModuleId = session.getHasModuleId();
				String sessionId = session.getId();
				ArrayList<StudentAcadsBean> studentList = null;
				
				String year = session.getYear();
				String month = session.getMonth();
				studentList = notificationDAO.getRegisteredStudentForSubjectNew(sessionId, hasModuleId, year, month);
				
				//Commented as added new query with session configurable 
				/*
				if(allAvailableCorporateCenters.contains(corporateCenterName)){
					studentList = notificationDAO.getRegisteredStudentForSubject(subject,corporateCenterName,hasModuleId,new ArrayList<String>()); 
				}else{
					studentList = notificationDAO.getRegisteredStudentForSubject(subject,null,hasModuleId,allAvailableCorporateCenters); 
				}
				*/
				
				if(studentList != null && studentList.size() > 0){
					mobileNotificationHelper.sendSessionNotification(session, studentList);
				}
			}
		}

	}
	
	

	private void sendSMSsToStudent(SessionDayTimeAcadsBean session,	ArrayList<StudentAcadsBean> studentList) {
		
		String subject = "";
		String sessionName = "";

		if( session.getSubject().length() > 30 ) {
			subject = subjectAbbreviationHelper.createAbbreviation(session.getSubject());
		}else {
			subject = session.getSubject();
		}
		if( session.getSessionName().length() > 30 ) {
			sessionName = session.getSessionName().substring(0, 27)+"...";
		}else {
			sessionName = session.getSessionName();
		}
		
		try {
			String message = "Dear Student, interactive live Session of " + subject + "-" 
					+ sessionName+" is scheduled on " + session.getDate() + " at " + session.getStartTime()+". "
					+ "Please login to Student Zone of SVKM's NGASCE";
			
			/*
			String result = smsSender.sendScheduledSessionSMS(session, studentList,message);
			String result = smsSender.sendScheduledSessionSMSNetCore(session, studentList,message);
			*/
			
			String result = smsSender.sendScheduledSessionSMSmGage(session, studentList,message, loggerForSessionSMSs);
			if("OK".equalsIgnoreCase(result)){
				notificationDAO.updateSMSStatus(session,"Y");
			}else{
				// sending Error Email if SMS does not sent due to Password change or Username change 
				ArrayList<String> recipent = new ArrayList<String>(Arrays.asList("sanketpanaskar@gmail.com","sneha.utekar@nmims.edu","jforce.solution@gmail.com"));
				MailSender mailSender = (MailSender)act.getBean("mailer");
				mailSender.sendEmail("SMS NOT SEND","SMS Not Due to <br><br>"+result,recipent);
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			  
		}

	}

	
	//Send SMS For Tomorrows test start

	private void sendTestReminderSMSsToStudent(TestAcadsBean test,	List<StudentAcadsBean> studentsApllicableForTest) {
		
		String subject = "";
		String testName = "";

		if( test.getSubject().length() > 30 ) {
			subject = subjectAbbreviationHelper.createAbbreviation(test.getSubject());
		}else {
			subject = test.getSubject();
		}
		if( test.getTestName().length() > 30 ) {
			testName = test.getTestName().substring(0, 27)+"...";
		}else {
			testName = test.getTestName();
		}

		String message = "Dear Student, Internal Assessment Test of " + subject + "-" 
				+ testName+" is scheduled on " + test.getStartDateForMail()+". Please login to Student Zone of SVKM's NGASCE";
		System.out.println("message : "+message);

		try {

			//System.out.println("message : \n"+message);
			String result = smsSender.sendScheduledTestSMSmGage(test, studentsApllicableForTest,message);
			if("OK".equalsIgnoreCase(result)){
				notificationDAO.updateTestSMSStatus(test,"Y");
			}else{
				// sending Error Email if SMS does not sent due to Password change or Username change 
				ArrayList<String> recipent = new ArrayList<String>(Arrays.asList("sanketpanaskar@gmail.com","sneha.utekar@nmims.edu","jforce.solution@gmail.com"));
				MailSender mailSender = (MailSender)act.getBean("mailer");
				mailSender.sendEmail("Test SMS NOT SEND","Test SMS Not Due to <br><br>"+result,recipent);
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			  
		}

	}


	@Scheduled(fixedDelay = 60 * 60 * 1000)
	public void sendTestNotificationSMSs(){
		System.out.println("Started sendTestNotificationSMSs scheduler for "+ENVIRONMENT);
		
		  if(!"tomcat4".equalsIgnoreCase(SERVER) ||
		  !"PROD".equalsIgnoreCase(ENVIRONMENT)){ System.out.
		  println("Not running SMS scheduler since this is not tomcat4. This is "
		  +SERVER); return; }
		 
		

		List<TestAcadsBean> testsScheduledForTomorrow = notificationDAO.getTestsScheduledForTomorrowNSMSNotSent();
		
		for (TestAcadsBean test : testsScheduledForTomorrow) {
			System.out.println("IN sendEmailForTestsReminders got test : "+test.getTestName());
			
			try {
				String startDate = test.getStartDate();
				String endDate = test.getEndDate();
				//System.out.println("In sendEmailForTestsReminders got startDate : "+startDate);
				//System.out.println("In sendEmailForTestsReminders got endDate : "+endDate);
				SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
				SimpleDateFormat sdf2 = new SimpleDateFormat("E, dd MMM yyyy HH:mm z");
				Date sDate = sdf.parse(startDate.replaceAll("T", " "));
				//Date cDate = new Date();
				//Date eDate = sdf.parse(endDate.replaceAll("T", " "));
				//String newStartDate = sdf2.format(sDate);
				
				//System.out.println("In sendEmailForTestsReminders got formated startDate : "+sDate);
				//System.out.println("In sendEmailForTestsReminders got formated currentDate : "+cDate);
				//System.out.println("In sendEmailForTestsReminders got formated endDate : "+eDate);
				
				String startDateForMail = sdf2.format(sDate);
				//System.out.println("In sendEmailForTestsReminders got startDateForMail : "+startDateForMail);
				
				test.setStartDateForMail(startDateForMail);
				
				List<StudentAcadsBean> studentsApllicableForTest  = notificationDAO.getStudentsEligibleForTestByTestid(test.getId());
				
				if(studentsApllicableForTest != null && studentsApllicableForTest.size() > 0){
					sendTestReminderSMSsToStudent(test, studentsApllicableForTest);
				}
			} catch (ParseException e) {
				// TODO Auto-generated catch block
				  
			}
			
			
		}
		
		System.out.println("Exited sendTestNotificationSMSs scheduler for "+ENVIRONMENT);
		
	}
	//Send SMS For Tomorrows test end

	@Scheduled(fixedDelay = 30 * 60 * 1000)
	public void sendPGTodaysSessionEmails(){
		try {
			System.out.println("Server = "+SERVER);
			System.out.println("Started Email scheduler for "+ENVIRONMENT);
			loggerForSessionEmails.info("Started sendPGTodaysSessionEmails Email scheduler for "+ENVIRONMENT);
			
			if(!"tomcat4".equalsIgnoreCase(SERVER) || !"PROD".equalsIgnoreCase(ENVIRONMENT)){
				System.out.println("Not running email scheduler since this is not tomcat4. This is "+SERVER);
				return;
			}
			
			ArrayList<SessionDayTimeAcadsBean> scheduledSessionList = notificationDAO.getPGScheduledSessionForDayForEmail();
//			System.out.println("scheduledSessionList = "+scheduledSessionList);
			loggerForSessionEmails.info("scheduledSessionList in sendPGTodaysSessionEmails = "+scheduledSessionList);
			
			if(scheduledSessionList != null && scheduledSessionList.size() > 0 && "PROD".equalsIgnoreCase(ENVIRONMENT)){
				for (int i = 0; i < scheduledSessionList.size(); i++) {
					SessionDayTimeAcadsBean session = scheduledSessionList.get(i);
					String subject = session.getSubject();
					String sessionId = session.getId();
					ArrayList<StudentAcadsBean> studentList = new ArrayList<StudentAcadsBean>();
					ArrayList<StudentAcadsBean> studentListAfterJul = new ArrayList<StudentAcadsBean>();
					ArrayList<StudentAcadsBean> studentListBeforeJul = new ArrayList<StudentAcadsBean>();
					ArrayList<StudentAcadsBean> studentListUG = new ArrayList<StudentAcadsBean>();
					
					studentListAfterJul = notificationDAO.getRegisteredPGStudentForSessionAfterJul21(sessionId);
					studentListBeforeJul = notificationDAO.getRegisteredPGStudentForSessionBeforeJul21(sessionId);
					studentListUG = notificationDAO.getRegisteredNonPGStudentForSession(sessionId, studentService.getListOfLiveSessionAccessMasterKeys(TIMEBOUND_PORTAL_LIST));
					
					studentList.addAll(studentListAfterJul);
					studentList.addAll(studentListBeforeJul);
					studentList.addAll(studentListUG);
					
					System.out.println("Sending Email to "+studentList.size()+" students for subject "+session.getSubject());
					loggerForSessionEmails.info("Sending Email to "+studentList.size()+" students for subject "+session.getSubject());
					System.out.println("Sending Emails for "+subject);
					loggerForSessionEmails.info("Sending Emails for "+subject);
					
					if(studentList != null && studentList.size() > 0){
						sendEmailsToStudent(session, studentList);
					}
				}
			}

		} catch (Exception e) {
			  
			loggerForSessionEmails.info("Error in sendPGTodaysSessionEmails : "+e.getMessage());
		}
		
		loggerForSessionEmails.info("Ended sendPGTodaysSessionEmails.");
	}
	
	@Scheduled(fixedDelay = 30 * 60 * 1000)
	public void sendMBATodaysSessionEmails(){
		try {
			System.out.println("Server = "+SERVER);
			System.out.println("Started Email scheduler for "+ENVIRONMENT);
			loggerForSessionEmails.info("Started sendMBATodaysSessionEmails Email scheduler for "+ENVIRONMENT);
			
			if(!"tomcat4".equalsIgnoreCase(SERVER) || !"PROD".equalsIgnoreCase(ENVIRONMENT)){
				System.out.println("Not running email scheduler since this is not tomcat4. This is "+SERVER);
				return;
			}
			
			ArrayList<SessionDayTimeAcadsBean> scheduledSessionList = notificationDAO.getMBAScheduledSessionForDayForEmail();
//			System.out.println("scheduledSessionList = "+scheduledSessionList);
			loggerForSessionEmails.info("scheduledSessionList in sendMBATodaysSessionEmails = "+scheduledSessionList);
			
			if(scheduledSessionList != null && scheduledSessionList.size() > 0 && "PROD".equalsIgnoreCase(ENVIRONMENT)){
				for (int i = 0; i < scheduledSessionList.size(); i++) {
					SessionDayTimeAcadsBean session = scheduledSessionList.get(i);
					String subject = session.getSubject();
					String sessionId = session.getId();
					ArrayList<StudentAcadsBean> studentList = notificationDAO.getRegisteredTimeBoundStudentForSession(sessionId);
					
					System.out.println("Sending Email to "+studentList.size()+" students for subject "+session.getSubject());
					loggerForSessionEmails.info("Sending Email to "+studentList.size()+" students for subject "+session.getSubject());
					System.out.println("Sending Emails for "+subject);
					loggerForSessionEmails.info("Sending Emails for "+subject);
					
					if(studentList != null && studentList.size() > 0){
						sendEmailsToStudent(session, studentList);
					}
				}
			}

		} catch (Exception e) {
			  
			loggerForSessionEmails.info("Error in sendMBATodaysSessionEmails : "+e.getMessage());
		}
		loggerForSessionEmails.info("Ended sendMBATodaysSessionEmails.");
	}
	
	@Scheduled(fixedDelay = 30 * 60 * 1000)
	public void sendPGTodaysSessionSMSs(){
		try {
			System.out.println("Started SMS scheduler for "+ENVIRONMENT);
			loggerForSessionSMSs.info("Started sendPGTodaysSessionSMSs scheduler for "+ENVIRONMENT);
			
			if(!"tomcat4".equalsIgnoreCase(SERVER) || !"PROD".equalsIgnoreCase(ENVIRONMENT)){
				System.out.println("Not running SMS scheduler since this is not tomcat4. This is "+SERVER);
				return;
			}
			
			ArrayList<SessionDayTimeAcadsBean> scheduledSessionList = notificationDAO.getPGScheduledSessionForDayForSMS();
//			System.out.println("scheduledSessionList = "+scheduledSessionList);
			loggerForSessionSMSs.info("scheduledSessionList in sendPGTodaysSessionSMSs = "+scheduledSessionList);
			
			if(scheduledSessionList != null && scheduledSessionList.size() > 0 && "PROD".equalsIgnoreCase(ENVIRONMENT)){
				for (int i = 0; i < scheduledSessionList.size(); i++) {
					SessionDayTimeAcadsBean session = scheduledSessionList.get(i);
					String subject = session.getSubject();
					String sessionId = session.getId();
					System.out.println("Sending SMS for "+subject);
				
					ArrayList<StudentAcadsBean> studentList = new ArrayList<StudentAcadsBean>();
					ArrayList<StudentAcadsBean> studentListAfterJul = new ArrayList<StudentAcadsBean>();
					ArrayList<StudentAcadsBean> studentListBeforeJul = new ArrayList<StudentAcadsBean>();
					ArrayList<StudentAcadsBean> studentListUG = new ArrayList<StudentAcadsBean>();
					
					studentListAfterJul = notificationDAO.getRegisteredPGStudentForSessionAfterJul21(sessionId);
					studentListBeforeJul = notificationDAO.getRegisteredPGStudentForSessionBeforeJul21(sessionId);
					studentListUG = notificationDAO.getRegisteredNonPGStudentForSession(sessionId, studentService.getListOfLiveSessionAccessMasterKeys(TIMEBOUND_PORTAL_LIST));
					
					studentList.addAll(studentListAfterJul);
					studentList.addAll(studentListBeforeJul);
					studentList.addAll(studentListUG);
					
					System.out.println("Sending SMS to "+studentList.size()+" students for subject "+session.getSubject());
					loggerForSessionSMSs.info("Sending SMS to "+studentList.size()+" students for subject "+session.getSubject());
					System.out.println("Sending SMS for "+subject);
					loggerForSessionSMSs.info("Sending SMS for "+subject);
					
					if(studentList != null && studentList.size() > 0){
						sendSMSsToStudent(session, studentList);
					}
				}
			}
		} catch (Exception e) {
			  
			loggerForSessionSMSs.info("Error in sendPGTodaysSessionSMSs : "+e.getMessage());
		}
		
		loggerForSessionSMSs.info("Ended sendPGTodaysSessionSMSs.");
	}
	
	@Scheduled(fixedDelay = 30 * 60 * 1000)
	public void sendMBATodaysSessionSMSs(){
		try {
			System.out.println("Started SMS scheduler for "+ENVIRONMENT);
			loggerForSessionSMSs.info("Started sendMBATodaysSessionSMSs scheduler for "+ENVIRONMENT);
			
			if(!"tomcat4".equalsIgnoreCase(SERVER) || !"PROD".equalsIgnoreCase(ENVIRONMENT)){
				System.out.println("Not running SMS scheduler since this is not tomcat4. This is "+SERVER);
				return;
			}
			
			ArrayList<SessionDayTimeAcadsBean> scheduledSessionList = notificationDAO.getMBAScheduledSessionForDayForSMS();
//			System.out.println("scheduledSessionList = "+scheduledSessionList);
			loggerForSessionSMSs.info("scheduledSessionList in sendMBATodaysSessionSMSs = "+scheduledSessionList);
			
			if(scheduledSessionList != null && scheduledSessionList.size() > 0 && "PROD".equalsIgnoreCase(ENVIRONMENT)){
				for (int i = 0; i < scheduledSessionList.size(); i++) {
					SessionDayTimeAcadsBean session = scheduledSessionList.get(i);
					String subject = session.getSubject();
					String sessionId = session.getId();
					System.out.println("Sending SMS for "+subject);
				
					ArrayList<StudentAcadsBean> studentList = notificationDAO.getRegisteredTimeBoundStudentForSession(sessionId);
					
					System.out.println("Sending SMS to "+studentList.size()+" students for subject "+session.getSubject());
					loggerForSessionSMSs.info("Sending SMS to "+studentList.size()+" students for subject "+session.getSubject());
					System.out.println("Sending SMS for "+subject);
					loggerForSessionSMSs.info("Sending SMS for "+subject);
					
					if(studentList != null && studentList.size() > 0){
						sendSMSsToStudent(session, studentList);
					}
				}
			}
		} catch (Exception e) {
			  
			loggerForSessionSMSs.info("Error in sendMBATodaysSessionSMSs : "+e.getMessage());
		}
		
		loggerForSessionSMSs.info("Ended sendMBATodaysSessionSMSs.");
	}
	
	@Scheduled(fixedDelay = 30 * 60 * 1000)
	public void sendPGTodaysSessionMobileFirebaseNotification(){		//before 30 min session start
		try {
			System.out.println("Started Firebase scheduler for "+ENVIRONMENT);
			loggerForSessionFirebaseNotifications.info("Started sendPGTodaysSessionMobileFirebaseNotification scheduler for "+ENVIRONMENT);
			
			if(!"tomcat4".equalsIgnoreCase(SERVER) || !"PROD".equalsIgnoreCase(ENVIRONMENT)){
				System.out.println("Not running SMS scheduler since this is not tomcat4. This is "+SERVER);
				return;
			} 
			 
			ArrayList<SessionDayTimeAcadsBean> scheduledSessionList = notificationDAO.getPGScheduledSessionForFirebase();
//			System.out.println("scheduledSessionList = "+scheduledSessionList);
			loggerForSessionFirebaseNotifications.info("scheduledSessionList in sendPGTodaysSessionMobileFirebaseNotification = "+scheduledSessionList);
			
			if(scheduledSessionList != null && scheduledSessionList.size() > 0 && "PROD".equalsIgnoreCase(ENVIRONMENT)){
				for (int i = 0; i < scheduledSessionList.size(); i++) {
					SessionDayTimeAcadsBean session = scheduledSessionList.get(i);
					String subject = session.getSubject();
					String sessionId = session.getId();
					System.out.println("Sending SMS for "+subject);

					ArrayList<StudentAcadsBean> studentList = new ArrayList<StudentAcadsBean>();
					ArrayList<StudentAcadsBean> studentListAfterJul = new ArrayList<StudentAcadsBean>();
					ArrayList<StudentAcadsBean> studentListBeforeJul = new ArrayList<StudentAcadsBean>();
					ArrayList<StudentAcadsBean> studentListUG = new ArrayList<StudentAcadsBean>();
					
					studentListAfterJul = notificationDAO.getRegisteredPGStudentForSessionAfterJul21(sessionId);
					studentListBeforeJul = notificationDAO.getRegisteredPGStudentForSessionBeforeJul21(sessionId);
					studentListUG = notificationDAO.getRegisteredNonPGStudentForSession(sessionId, studentService.getListOfLiveSessionAccessMasterKeys(TIMEBOUND_PORTAL_LIST));
					
					studentList.addAll(studentListAfterJul);
					studentList.addAll(studentListBeforeJul);
					studentList.addAll(studentListUG);
					
					System.out.println("Sending Firebase Notification to "+studentList.size()+" students for subject "+session.getSubject());
					loggerForSessionFirebaseNotifications.info("Sending Firebase Notification to "+studentList.size()+" students for subject "+session.getSubject());
					System.out.println("Sending Firebase Notification for "+subject);
					loggerForSessionFirebaseNotifications.info("Sending Firebase Notification for "+subject);
					
					if(studentList != null && studentList.size() > 0){
						mobileNotificationHelper.sendSessionNotification(session, studentList);
					}
				}
			}
		} catch (Exception e) {
			  
			loggerForSessionFirebaseNotifications.info("Error in sendPGTodaysSessionMobileFirebaseNotification");
		}
		loggerForSessionFirebaseNotifications.info("Ended sendPGTodaysSessionMobileFirebaseNotification.");
	}
	
	@Scheduled(fixedDelay = 30 * 60 * 1000)
	public void sendMBATodaysSessionMobileFirebaseNotification(){		//before 30 min session start
		try {
			System.out.println("Started Firebase scheduler for "+ENVIRONMENT);
			loggerForSessionFirebaseNotifications.info("Started sendMBATodaysSessionMobileFirebaseNotification scheduler for "+ENVIRONMENT);
			
			if(!"tomcat4".equalsIgnoreCase(SERVER) || !"PROD".equalsIgnoreCase(ENVIRONMENT)){
				System.out.println("Not running SMS scheduler since this is not tomcat4. This is "+SERVER);
				return;
			} 
			 
			ArrayList<SessionDayTimeAcadsBean> scheduledSessionList = notificationDAO.getMBAScheduledSessionForFirebase();		//temp only for MBAWx
//			System.out.println("scheduledSessionList = "+scheduledSessionList);
			loggerForSessionFirebaseNotifications.info("scheduledSessionList in sendMBATodaysSessionMobileFirebaseNotification = "+scheduledSessionList);
			
			if(scheduledSessionList != null && scheduledSessionList.size() > 0 && "PROD".equalsIgnoreCase(ENVIRONMENT)){
				for (int i = 0; i < scheduledSessionList.size(); i++) {
					SessionDayTimeAcadsBean session = scheduledSessionList.get(i);
					String subject = session.getSubject();
					String sessionId = session.getId();
					System.out.println("Sending SMS for "+subject);

					ArrayList<StudentAcadsBean> studentList = notificationDAO.getRegisteredTimeBoundStudentForSession(sessionId);
					
					System.out.println("Sending Firebase Notification to "+studentList.size()+" students for subject "+session.getSubject());
					loggerForSessionFirebaseNotifications.info("Sending Firebase Notification to "+studentList.size()+" students for subject "+session.getSubject());
					System.out.println("Sending Firebase Notification for "+subject);
					loggerForSessionFirebaseNotifications.info("Sending Firebase Notification for "+subject);
					
					if(studentList != null && studentList.size() > 0){
						mobileNotificationHelper.sendSessionNotification(session, studentList);
					}
				}
			}
		} catch (Exception e) {
			  
			loggerForSessionFirebaseNotifications.info("Error in sendMBATodaysSessionMobileFirebaseNotification");
		}
		
		loggerForSessionFirebaseNotifications.info("Ended sendMBATodaysSessionMobileFirebaseNotification.");
	}
	
	@Scheduled(fixedDelay = 30 * 60 * 1000)
	public void sendTodaysSessionNotificationToFaculty(){		//before 30 min session start
		try {
			System.out.println("Started Faculty Notification scheduler for "+ENVIRONMENT);
			loggerForSessionNotificationFaculty.info("Started sendTodaysSessionNotificationToFaculty scheduler for "+ENVIRONMENT);
			
			if(!"tomcat4".equalsIgnoreCase(SERVER) || !"PROD".equalsIgnoreCase(ENVIRONMENT)){
				System.out.println("Not running SMS scheduler since this is not tomcat4. This is "+SERVER);
				return;
			} 
			 
			ArrayList<SessionDayTimeAcadsBean> scheduledSessionList = notificationDAO.getScheduledSessionForFaculty();
//			System.out.println("scheduledSessionList = "+scheduledSessionList);
			loggerForSessionNotificationFaculty.info("scheduledSessionList in sendTodaysSessionNotificationToFaculty = "+scheduledSessionList);
			
			if(scheduledSessionList != null && scheduledSessionList.size() > 0 && "PROD".equalsIgnoreCase(ENVIRONMENT)){
				for (int i = 0; i < scheduledSessionList.size(); i++) {
					SessionDayTimeAcadsBean session = scheduledSessionList.get(i);
					ArrayList<String> FacultyIdsList = new ArrayList<String>();
					String subject = session.getSubject();
					FacultyIdsList.add(session.getFacultyId());
					
					if (!StringUtils.isBlank(session.getAltFacultyId())) {
						FacultyIdsList.add(session.getAltFacultyId());
					}
					
					if (!StringUtils.isBlank(session.getAltFacultyId2())) {
						FacultyIdsList.add(session.getAltFacultyId2());
					}
					if (!StringUtils.isBlank(session.getAltFacultyId3())) {
						FacultyIdsList.add(session.getAltFacultyId3());
					}
					
					System.out.println("Sending SMS for :"+subject);
				
					ArrayList<FacultyAcadsBean> facultyList = notificationDAO.getFacultyDetails(FacultyIdsList);
					
//					System.out.println("Sending SMS to "+facultyList.size()+" Faculty's for subject "+session.getSubject());
					loggerForSessionNotificationFaculty.info("Sending SMS to "+facultyList.size()+" Faculty's for subject "+session.getSubject());
//					System.out.println("Sending SMS for "+subject);
					loggerForSessionNotificationFaculty.info("Sending SMS for "+subject);
					
					if(facultyList != null && facultyList.size() > 0){
						sendSMSsToFaculty(session, facultyList);
					}
				}
			}
		} catch (Exception e) {
			  
			loggerForSessionNotificationFaculty.info("Error in sendTodaysSessionNotificationToFaculty");
		}
		
		loggerForSessionNotificationFaculty.info("Ended sendTodaysSessionNotificationToFaculty.");
	}
	
	private void sendSMSsToFaculty(SessionDayTimeAcadsBean session,	ArrayList<FacultyAcadsBean> facultyList) {
		
		String subject = "";
		String sessionName = "";

		if( session.getSubject().length() > 30 ) {
			subject = subjectAbbreviationHelper.createAbbreviation(session.getSubject());
		}else {
			subject = session.getSubject();
		}
		if( session.getSessionName().length() > 30 ) {
			sessionName = session.getSessionName().substring(0, 27)+"...";
		}else {
			sessionName = session.getSessionName();
		}
		
		try {
			String message = "Dear Faculty, Your interactive live session of " + subject + " " 
					+ sessionName+" is scheduled on " + session.getDate() + " at " + session.getStartTime()+". "
					+ "Please login to the Student Zone to start the session. Regards-SVKM's NMIMS Global";
			
			/*
			String result = smsSender.sendScheduledSessionSMS(session, studentList,message);
			String result = smsSender.sendScheduledSessionSMSNetCore(session, studentList,message);
			*/
			
			loggerForSessionNotificationFaculty.info("Subject of SMS : "+subject);
			loggerForSessionNotificationFaculty.info("Body of SMS : "+message);
			
//			System.out.println("Subject of SMS "+subject);
//			System.out.println("Body of SMS "+message);
			
			ArrayList<StudentAcadsBean> studentList = new ArrayList<StudentAcadsBean>();
			for (FacultyAcadsBean faculty : facultyList) {
				StudentAcadsBean student = new StudentAcadsBean();
				student.setFirstName(faculty.getFirstName());
				student.setLastName(faculty.getLastName());
				student.setMobile(faculty.getMobile());
				studentList.add(student);
			}
			
			String result = smsSender.sendScheduledSessionSMSmGage(session, studentList,message, loggerForSessionNotificationFaculty);
			loggerForSessionNotificationFaculty.info("result "+result);
			
			if("OK".equalsIgnoreCase(result)){
				notificationDAO.updateFacultySMSStatus(session,"Y");
			}else{
				// sending Error Email if SMS does not sent due to Password change or Username change 
				ArrayList<String> recipent = new ArrayList<String>(Arrays.asList("Somesh.Turde.Ext@nmims.edu","jforce.solution@gmail.com"));
				MailSender mailSender = (MailSender)act.getBean("mailer");
				mailSender.sendEmail("SMS NOT SEND","SMS Not Due to <br><br>"+result,recipent);
			}
		} catch (Exception e) {
			  
		}

	}
}
