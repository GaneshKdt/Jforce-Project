package com.nmims.listeners;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.net.SocketException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.nmims.beans.ExamCenterSlotMappingBean;
import com.nmims.beans.MettlSSOInfoBean;
import com.nmims.beans.StudentExamBean;
import com.nmims.daos.ExamBookingDAO;
import com.nmims.daos.MettlTeeDAO;
import com.nmims.helpers.MailSender;
import com.nmims.helpers.MobileNotification;
import com.nmims.helpers.TeeSSOHelper;
import com.nmims.services.MBAWXTeeService;

@Component
public class TEELinkScheduler {

	private static final Logger logger = LoggerFactory.getLogger(TEELinkScheduler.class);
	private static final Logger demo_exam_mailer_logger = LoggerFactory.getLogger("demo_exam_mailer");
	
	@Autowired
	ApplicationContext act;
	
	@Autowired
	MettlTeeDAO ssoDao;
	
	@Autowired
	TeeSSOHelper teeSSOHelper;
	
	@Autowired
	MailSender mailSender;
	
	@Autowired
	MobileNotification mobileNotification;
	
	@Value("${SERVER}")
	private String SERVER;
	
	@Value("${ENVIRONMENT}")
	private String ENVIRONMENT;
	
	@Autowired
	MBAWXTeeService mbawxMettleService;
	
	//Commented by Somesh as Jun-20 exam is over
	//@Scheduled(cron = "0 0 11,19 * * *")
    public void sendDemoExamPendingEmails() throws Exception{
        if(!"tomcat6".equalsIgnoreCase(SERVER) || !"PROD".equalsIgnoreCase(ENVIRONMENT)){
            return;
        }
        MailSender mailSender = (MailSender)act.getBean("mailer");
        ExamBookingDAO dao = (ExamBookingDAO)act.getBean("examBookingDAO");
        
        List<StudentExamBean> studentsList = dao.getDemoNotCompletedStudent();
        if (studentsList.size() > 0) {
            
            for (StudentExamBean studentBean : studentsList) {
                mailSender.sendEmailForDemoExamReminder(studentBean);
            }
            mobileNotification.sendDemoExamNotification(studentsList);
        }
        
  }
	
	//Commented by Somesh as Jun-20 exam is over
	//@Scheduled(cron = "0 0 17 * * *")
	public void sendOnlineExamTestLinkUrlDayAgo() throws Exception{
        if(!"tomcat6".equalsIgnoreCase(SERVER) || !"PROD".equalsIgnoreCase(ENVIRONMENT)){
            return;
        }
        MailSender mailSender = (MailSender)act.getBean("mailer");
        ExamBookingDAO dao = (ExamBookingDAO)act.getBean("examBookingDAO");
        
        List<StudentExamBean> studentsList = dao.getTomorrowExamStudentList();
        if (studentsList.size() > 0) {
            
        	if(studentsList != null && studentsList.size() > 0){
        		mailSender.sendEmailToExamTomorrow(studentsList);
        		mobileNotification.sendDemoExamNotification(studentsList);
        	}
        }
  }
	

	//At 0 second and 30 minute past hour 7, 11, and 15 in January, April, June, September, and December
	@Scheduled(cron = "0 30 7,11,15 * 1,4,6,9,12 ?")
	public void sendInvitationLinksForUpcomingExams() {
		logger.info("(CRON) Entering sendInvitationLinksForUpcomingExams (Server,Environment) : ("+SERVER+","+ENVIRONMENT+")");
		if(!ENVIRONMENT.equals("PROD") || !SERVER.equals("tomcat6")) {
			logger.info("(CRON) Exiting sendInvitationLinksForUpcomingExams. Required (Server,Environment) : (tomcat6,PROD)");
			return;
		}

		// get all upcoming exams by slot details
		List<ExamCenterSlotMappingBean> slotList = ssoDao.getListOfUpcomingExamsForScheduler();
		logger.info(SERVER+":  sendInvitationLinksForUpcomingExams slotListsize  " +slotList.size());
		if(slotList.size() == 0) {
			return;
		}

		List<MettlSSOInfoBean> successfulEmails = new ArrayList<MettlSSOInfoBean>();
		List<MettlSSOInfoBean> failedEmails = new ArrayList<MettlSSOInfoBean>();
		
		
		for (ExamCenterSlotMappingBean slot : slotList) {
			logger.info(SERVER+":  sendInvitationLinksForUpcomingExams slot  " +slot.getDate()+" "+slot.getStarttime());
			List<MettlSSOInfoBean> bookingsForSlot = ssoDao.getBookingsForSlot(slot);
			String formattedDateTime;
			try {
				formattedDateTime = teeSSOHelper.getFormattedDateTimeForEmail(slot.getDate() + " " + slot.getStarttime());
			}catch (Exception e) {
				
				formattedDateTime = slot.getDate() + " " + slot.getStarttime();
			}
			logger.info(SERVER+": sendInvitationLinksForUpcomingExams " + formattedDateTime + " # bookings found :  " + bookingsForSlot.size());
			
			for (MettlSSOInfoBean booking : bookingsForSlot) {
				
				try {
					String joinLink = teeSSOHelper.generateMettlLink(booking);
					booking.setJoinURL(joinLink);
					booking.setFormattedDateStringForEmail(formattedDateTime);
				} catch (Exception e) {
					
					StringWriter error = new StringWriter();
					e.printStackTrace(new PrintWriter(error));
					logger.error(SERVER+": Error generating Join Link - bean "+booking+", Error : " + error.toString());
					
					booking.setError("Error generating Join Link \n" + e.getMessage());
					failedEmails.add(booking);
					continue;
				}
				try {
					Thread.sleep(50);
					teeSSOHelper.sendJoinMail(booking);
					successfulEmails.add(booking);
				}catch (SocketException s) {
					StringWriter error = new StringWriter();
					s.printStackTrace(new PrintWriter(error));
					logger.error(SERVER+": SocketException Occur while Sending Join Link Retry Start - bean "+booking+", Error : " + error.toString());
					
					try {
						Thread.sleep(5000);
						teeSSOHelper.sendJoinMail(booking);
						successfulEmails.add(booking);
					}catch (Exception ex) {
						StringWriter errors = new StringWriter();
						ex.printStackTrace(new PrintWriter(errors));
						logger.error(SERVER+": Exception Occur while Sending Join Link Retry Fail - bean "+booking+", Error : " + errors.toString());
						booking.setError("(Catch 2) :Error Sending Join Link catch 1 \n" + ex.getMessage());
						failedEmails.add(booking);
						continue;
					}
				}catch (Exception e) {
					StringWriter errors = new StringWriter();
					e.printStackTrace(new PrintWriter(errors));
					logger.error(SERVER+": Exception Occur while Sending Join Link - bean "+booking+", Error : " + errors.toString());
					booking.setError("(Catch 1) :Error Sending Join Link \n" + e.getMessage());
					failedEmails.add(booking);
					continue;
				}
			}
		}
		logger.info(SERVER+": sendInvitationLinksForUpcomingExams FINISH: " + successfulEmails.size() + " # bookings found :  " + failedEmails.size());
		logger.info(SERVER+": sendInvitationLinksForUpcomingExams FINISH: Fail List -" + failedEmails);
		try {
			mailSender.sendExamJoinLinkStatusMail(successfulEmails, failedEmails);
		} catch (Exception e) {
			
		}
	}
	//added to send demo exam mail for student who has not attempted demo exam till exam date 
	
	//At 0 second and 30 minute past hour 23 in January, April, June, September, and December
	  @Scheduled(cron = "0 30 23 * 1,3,4,6,7,8,9,12 ?")
  public void sendDemoExamSecondCommunication() {
//      if(!"tomcat6".equalsIgnoreCase(SERVER) || !"PROD".equalsIgnoreCase(ENVIRONMENT)){
//          return;
//      }
     
      ExamBookingDAO dao = (ExamBookingDAO)act.getBean("examBookingDAO");
     
		try {
			Date now = new Date();
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");  
		//	String Examfirstdate = dao.getFirstExamDate(dao.getLiveExamYear(),dao.getLiveExamMonth());
			//Date firstdateexam = sdf.parse(Examfirstdate);

			//demo_exam_mailer_logger.info(SERVER + " current date "+now.toString());
			//demo_exam_mailer_logger.info(SERVER + " firstdateexam "+firstdateexam.toString());
			
			//if (firstdateexam.after(now)) {
				Date Maxexamdate= null;
				List<StudentExamBean> studentsList = dao.getDemoNotCompletedStudents(dao.getLiveExamYear(),
						dao.getLiveExamMonth());
				demo_exam_mailer_logger.info(SERVER + ":  sendDemoExamPendingEmail studentsList  " + studentsList.size());
				if (studentsList != null && studentsList.size() > 0) {
					// mailSender.sendEmailForDemoExamReminderAfterFirstMail(studentsList);

					for (StudentExamBean studentBean : studentsList) {
						String Exammaxdate = studentBean.getMaxExamDate();
						 Maxexamdate = sdf.parse(Exammaxdate);
						if (Maxexamdate.after(now)) {

						mailSender.sendEmailForDemoExamReminderAfterFirstMail(studentBean, dao.getLiveExamYear(),dao.getLiveExamMonth());

					}

				}
					demo_exam_mailer_logger.info(SERVER + " current date "+now.toString());
					demo_exam_mailer_logger.info(SERVER + " Maxexamdate "+Maxexamdate.toString());
			}
			demo_exam_mailer_logger.info(SERVER + ":  sendDemoExamPendingEmail End ");
		} catch (Exception e) {
			
			demo_exam_mailer_logger.error(SERVER + ":  sendDemoExamPendingEmail Error :  " + e.getMessage());
		}
	 }
	  
	  

	  
	
	 
	  	//At 30 minutes past every hour
	    @Scheduled(cron = "0 30 * * * ?")
		public void sendInvitationLinksForMbaWxUpcomingExams() {
	    	
			logger.info("(CRON) Entering sendInvitationLinksForMbaWxUpcomingExams (Server,Environment) : (" + SERVER
					+ "," + ENVIRONMENT + ")");
			if (!ENVIRONMENT.equals("PROD") || !SERVER.equals("tomcat6")) {
				logger.info(
						"(CRON) Exiting sendInvitationLinksForMbaWxUpcomingExams. Required (Server,Environment) : (tomcat6,PROD)");
				return;
			}else {
				mbawxMettleService.sendExamJoinLinksForMbaWxStudents();
			}

	  		
	  	}
	  
	  
	  
}
	 
