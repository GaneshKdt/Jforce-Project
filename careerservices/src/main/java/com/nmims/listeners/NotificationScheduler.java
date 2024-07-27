package com.nmims.listeners;


import java.util.ArrayList;
import java.util.Arrays;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.nmims.beans.SessionDayTimeBean;
import com.nmims.beans.StudentCareerservicesBean;
import com.nmims.daos.NotificationDAO;
import com.nmims.helpers.MailSender;
import com.nmims.helpers.SMSSender;


@Component
public class NotificationScheduler {

	private static ApplicationContext act = null;

	@Value( "${ENVIRONMENT}" )
	private String ENVIRONMENT;
	
	@Value( "${SERVER}" )
	private String SERVER;
	
	@Autowired
	private NotificationDAO notificationDAO;

	@Autowired
	private SMSSender smsSender;
	
	@Autowired
	private MailSender mailSender;

	private static final Logger logger = LoggerFactory.getLogger(NotificationScheduler.class);
 
	@Scheduled(fixedDelay = 30 * 60 * 1000)
	public void sendTodaysSessionEmails(){
		try {

			if(!"tomcat4".equalsIgnoreCase(SERVER) || !"PROD".equalsIgnoreCase(ENVIRONMENT)){
				System.out.println("Not running email scheduler since this is not tomcat4. This is "+SERVER);
				return;
			}
			
			ArrayList<SessionDayTimeBean> scheduledSessionList = notificationDAO.getScheduledSessionForDayForEmail();

			if(scheduledSessionList != null && scheduledSessionList.size() > 0 && "PROD".equalsIgnoreCase(ENVIRONMENT)){
				for (int i = 0; i < scheduledSessionList.size(); i++) {
					SessionDayTimeBean session = scheduledSessionList.get(i);
					//here we will take product id 
					String subject = session.getSessionName();
					ArrayList<StudentCareerservicesBean> studentList = null;
					
					studentList = notificationDAO.getRegisteredStudentForSubject(subject); 
					
					if(studentList != null && studentList.size() > 0){
						sendEmailsToStudent(session, studentList);
					}

				}
			}

		} catch (Exception e) {
			logger.info("exception : "+e.getMessage());
		}

	}
	
	private void sendEmailsToStudent(SessionDayTimeBean session, ArrayList<StudentCareerservicesBean> studentList) {
		try {
			mailSender.sendEmails(session, studentList);
			notificationDAO.updateEmailStatus(session,"Y");
		} catch (Exception e) {
			logger.info("exception : "+e.getMessage());
		}
	}
	
	@Scheduled(fixedDelay = 60 * 60 * 1000)
	public void sendTodaysSessionSMSs(){
		try {
			
			if(!"tomcat4".equalsIgnoreCase(SERVER) || !"PROD".equalsIgnoreCase(ENVIRONMENT)){
				System.out.println("Not running SMS scheduler since this is not tomcat4. This is "+SERVER);
				return;
			}
			
			ArrayList<SessionDayTimeBean> scheduledSessionList = notificationDAO.getScheduledSessionForDayForSMS();
			
			if(scheduledSessionList != null && scheduledSessionList.size() > 0 && "PROD".equalsIgnoreCase(ENVIRONMENT)){
				for (int i = 0; i < scheduledSessionList.size(); i++) {
					SessionDayTimeBean session = scheduledSessionList.get(i);
					String subject = session.getSessionName();

					ArrayList<StudentCareerservicesBean> studentList = null;
					
					studentList = notificationDAO.getRegisteredStudentForSubject(subject); 
					
					if(studentList != null && studentList.size() > 0){
						sendSMSsToStudent(session, studentList);
					}
				}
			}
			
		} catch (Exception e) {
			logger.info("exception : "+e.getMessage());
		}
	}
	
	private void sendSMSsToStudent(SessionDayTimeBean session,	ArrayList<StudentCareerservicesBean> studentList) {
		try {
			String message = "Dear Student,\n"
					+ "Online live Session of Career Services -" 
					+ session.getSessionName()+" is scheduled on "
					+ session.getDate() + " at " + session.getStartTime()
					+ "\nPlease login to Student Zone"
					+ "\nNGASCE";
			String result = smsSender.sendScheduledSessionSMSmGage(session, studentList,message);
			if("OK".equalsIgnoreCase(result)){
				notificationDAO.updateSMSStatus(session,"Y");
			}else{
				// sending Error Email if SMS does not sent due to Password change or Username change 
				ArrayList<String> recipent = new ArrayList<String>(Arrays.asList("sanketpanaskar@gmail.com","sneha.utekar@nmims.edu"));
				MailSender mailSender = (MailSender)act.getBean("mailer");
				mailSender.sendEmail("SMS NOT SEND","SMS Not Due to <br><br>"+result,recipent);
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			logger.info("exception : "+e.getMessage());
		}

	}

}
