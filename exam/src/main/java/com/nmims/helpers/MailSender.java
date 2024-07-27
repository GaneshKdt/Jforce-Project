package com.nmims.helpers;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.UnsupportedEncodingException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import javax.servlet.http.HttpServletRequest;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.core.io.FileSystemResource;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import com.nmims.beans.AssignmentFileBean;
import com.nmims.beans.AuditTrailExamBean;
import com.nmims.beans.CaseStudyExamBean;
import com.nmims.beans.ExamBookingTransactionBean;
import com.nmims.beans.ExamCenterBean;
import com.nmims.beans.ExamScheduleinfoBean;
import com.nmims.beans.ExecutiveExamOrderBean;
import com.nmims.beans.LostFocusLogExamBean;
import com.nmims.beans.MBAExamBookingRequest;
import com.nmims.beans.MailBean;
import com.nmims.beans.MettlSSOInfoBean;
import com.nmims.beans.ProgramSubjectMappingExamBean;
import com.nmims.beans.ServiceRequestBean;
import com.nmims.beans.Specialisation;
import com.nmims.beans.StudentExamBean;
import com.nmims.beans.StudentsTestDetailsExamBean;
import com.nmims.beans.TcsOnlineExamBean;
import com.nmims.beans.TestExamBean;
import com.nmims.beans.UFMNoticeBean;
import com.nmims.daos.AssignmentsDAO;
import com.nmims.daos.ExamBookingDAO;
import com.nmims.daos.ExamCenterDAO;
import com.nmims.daos.ProjectSubmissionDAO;
import com.nmims.daos.TestDAO;
import com.nmims.dto.ExamBookingTransactionDTO;
import com.nmims.services.ProjectStudentEligibilityService;

@Component
public class MailSender {
	
	@Autowired
	TNSMailSender tnsMailSender;
	
	@Autowired
	ProjectStudentEligibilityService eligibilityService;
	
	@Value("${SERVER_PATH}")
	private String SERVER_PATH;
	
	@Autowired
	EmailHelper emailHelper;

	private static final Logger logger = LoggerFactory.getLogger(MailSender.class);
	private static final Logger loggerForEmailCount = LoggerFactory.getLogger("emailCount");
	private static final Logger projectPaymentsLogger = LoggerFactory.getLogger("project_payments");
	public static final Logger ufm = LoggerFactory.getLogger("ufm");
	private static final Logger demo_exam_mailer_logger = LoggerFactory.getLogger("demo_exam_mailer");
	private static final Logger projectSubmissionLogger = LoggerFactory.getLogger("projectSubmission");
	public static final Logger pullTimeBoundMettlMarksLogger =LoggerFactory.getLogger("pullTimeBoundMettlMarks");

	private static final Logger specializationLogger = LoggerFactory.getLogger("electiveSelection");

	
	@Value( "${ENVIRONMENT}" )
	private String ENVIRONMENT;
	
	@Value("${MBAWX_ATTEMPT_STATUS_MAIL_TO}")
	private String MBAWX_ATTEMPT_STATUS_MAIL_TO;
	
	@Value("${MBAWX_ATTEMPT_STATUS_MAIL_CC}")
	private String MBAWX_ATTEMPT_STATUS_MAIL_CC;
	
	@Value("${MBAWX_ATTEMPT_STATUS_MAIL_BCC}")
	private String MBAWX_ATTEMPT_STATUS_MAIL_BCC;
	
	private String host;
	private String port;
	private String username;
	private String password;
	private String from;

	public static final String EMAILID_NGASCE_EXAMS = "ngasce.exams@nmims.edu";
	public static final String EMAILID_JFORCE_SS = "jforcesolutions@gmail.com";
	public static final String EMAILID_SHIV = "shiv.golani.ext@nmims.edu";
	public static final String EMAILID_VILPESH = "vilpesh.mistry.ext@nmims.edu";
	public static final String EMAILID_SAGAR = "sagar.shinde.ext@nmims.edu";
	public static final String EMAILID_ABHAY = "abhay.sakpal.ext@nmims.edu";
	public static final String EMAILID_SIDDHESWAR = "siddheshwar.khanse.ext@nmims.edu";
	public static final String EMAILID_GANESH = "ganesh.kudtarkar.EXT@nmims.edu";
	public static final String EMAILID_SWARUP = "swarup.rajpurohit.EXT@nmims.edu";
	
	
	public void setHost(String host) {
		this.host = host;
	}
	public void setPort(String port) {
		this.port = port;
	}
	public void setUsername(String username) {
		this.username = username;
	}
	public void setPassword(String password) {
		this.password = password;
	}
	public void setFrom(String from) {
		this.from = from;
	}

	@Autowired
	ApplicationContext act;

	/*public void sendPasswordEmail(Person person){


		Properties props = new Properties();
		props.put("mail.smtp.auth", "true");
		props.put("mail.smtp.starttls.enable", "true");
		props.put("mail.smtp.host", host);
		props.put("mail.smtp.port", port);

		Session session = Session.getInstance(props,
		  new javax.mail.Authenticator() {
			protected PasswordAuthentication getPasswordAuthentication() {
				return new PasswordAuthentication(username, password);
			}
		  });

		try {

			Message message = new MimeMessage(session);
			message.setFrom(new InternetAddress(from, "NMIMS Global Access SCE"));
			message.setRecipients(Message.RecipientType.TO,InternetAddress.parse(person.getEmail()));
			message.setSubject("Your Password for NGASCE Student Zone");

			String body = "Dear "+ person.getDisplayName()+", \n\n"
					+"Your password for NGASCE Student Zone is "+person.getPassword()
					+"\n\n"
					+"Thanks & Regards"
					+"\n"
					+"NMIMS GLOBAL ACCESS SCHOOL FOR CONTINUING EDUCATION";


			message.setText(body);

			Transport.send(message);


		} catch(Exception e) {
			throw new RuntimeException(e);
		}


	}
	 */
	@Async
    public void sendEmailForDemoExamReminder(StudentExamBean student) throws Exception {
        
        if(!"PROD".equalsIgnoreCase(ENVIRONMENT)){
            return; 
        }
        
        Properties props = new Properties();
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "true");
        props.put("mail.smtp.host", host);
        props.put("mail.smtp.port", port);
        Session session = Session.getInstance(props, new javax.mail.Authenticator() {
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(username, password);
            }
        });
        
        try {
            Message message = new MimeMessage(session);
            message.setFrom(new InternetAddress(from, "NMIMS Global Access SCE"));
    
            try {
                message.setRecipients(Message.RecipientType.TO,InternetAddress.parse(student.getEmailId()));
                message.setRecipients(Message.RecipientType.BCC,InternetAddress.parse("jforce.solution@gmail.com"));
                message.setSubject("Demo exam - Not attempted");
            }catch(Exception e) {
                message.setRecipients(Message.RecipientType.TO,InternetAddress.parse("jforce.solution@gmail.com, Somesh.Turde.Ext@nmims.edu"));
                message.setSubject("ERROR : Demo exam - Not attempted, sapid : "+student.getSapid());
            }
            
            //If student replies it should go to Exam Email ID
            message.setReplyTo(InternetAddress.parse("ngasce.exams@nmims.edu"));
    
                String body = "";
    
                body =  " Dear Student, <br>"
                       +" Demo exam is live since 8th June and you can access this by visiting <a href='https://studentzone-ngasce.nmims.edu/studentportal/'>Student portal</a>"
                       +" (Login to the portal -> Go to 'Exam' tab -> Click on 'Demo Exam' tab and click on 'Take Test') <br>"
                       +" This will help you to understand step by step process involved during the actual exam, so please attempt this at the earliest. <br>"
                       +" <br>";
    
                body = body + "<br>Thanks & Regards"
                            + "<br>"
                            + "NMIMS GLOBAL ACCESS SCHOOL FOR CONTINUING EDUCATION";
    
                message.setContent(body, "text/html; charset=utf-8");
                Transport.send(message);
                
                loggerForEmailCount.info("Total 1 Mail sent via(SMTP) from "+from+", Email Subject : Demo exam - Not attempted, for sendEmailForDemoExamReminder");
        } catch(Exception e) {
            
        }
    }

	
	
	@Async
    public void sendEmailToExamTomorrow(List<StudentExamBean> studentList) throws Exception {
        
        if(!"PROD".equalsIgnoreCase(ENVIRONMENT)){
            return; 
        }
        
        String emailIds = "";
        String subject = "Term Exam Check list";
		String fromEmailId = "donotreply-ngasce@nmims.edu";
		String fromName = "NMIMS Global Access SCE";
        try {
        	 String body = "";
        	    
             body =  " Dear Student, <br>"
                    +" <ul>"
                    +"  <li>Please ensure you have taken the Demo exam and checked compatibility</li>" 
                    +"  <li>Students must ensure that they use the same laptop/desktop for the actual exam that was used for the System compatibility Check and Demo exam. Note exam cannot be taken on a Tab or mobile phone</li>"
                    +"  <li>Mettl helpline: 8047190917, if the line is busy please wait in the queue</li>" 
                    +"  <li>Student can access the exam link via portal or via email  which will be sent one hour before the exam slot. Student should not forward this email to anyone else as it will be unique to every student</li>" 
                    +"  <li>Exam link will be live as per the slot booked and not before that, so for 10 am slot link will be live at 10 am and not before that, same goes for the 5 pm slot</li>" 
                    +"  <li>The exam link will be only active for 30 minutes from the time of commencement of exam</li>" 
                    +" </ul>"
                    +" <br>"
                    +" All the best for your exam <br>"
                    +" <br>";
 
             body = body + "<br>Regards"
                         + "<br>"
                         + "Team NGASCE";
             
            ArrayList<String> toEmailIds = new ArrayList<>();
            ArrayList<String> toSapIds = new ArrayList<>();
			for (StudentExamBean studentBean : studentList) {
				toEmailIds.add(studentBean.getEmailId());
				toSapIds.add(studentBean.getSapid());//Add SAPID to list
			}
			
			toEmailIds.add("jforce.solution@gmail.com");
			
			tnsMailSender.sendMassEmail(toSapIds, toEmailIds, fromEmailId, fromName, subject, body);
			
		} catch (Exception e) {
			String body = "Error in sending email "+e.getMessage() + " <br>";
			body = body + "Error while sending emails to "+emailIds;
			String encodedBody = new String(Base64.encodeBase64(body.getBytes()));
			
			//tnsMailSender.sendTNSEMail(encodedBody, "[\"Somesh.Turde.Ext@nmims.edu\", \"Shiv.Golani.Ext@nmims.edu\", \"jforce.solution@gmail.com\"]", "Error in sending emails for "+subject);
			HashMap<String, String> payload =  new HashMap<String, String>();
			payload.put("provider", "AWSSES");
			payload.put("htmlBody", encodedBody);
			payload.put("subject", "Error in sending emails for "+subject);
			payload.put("from", "donotreply-ngasce@nmims.edu");
			payload.put("email", "Somesh.Turde.Ext@nmims.edu, Shiv.Golani.Ext@nmims.edu, jforce.solution@gmail.com, mansi.thorat.EXT@nmims.edu");
			payload.put("fromName", "NMIMS Global Access SCE");
			emailHelper.sendAmazonSESDynamicMail(payload);
			loggerForEmailCount.info("Total 1 Mail sent via(TNMails) from "+from+", Email Subject : "+subject+", for sendEmailToExamTomorrow");
			
		}

    }

	@Async
	public void sendDDStatusChangeEmail(String email, String ddno, String action, String reason, String sapid) {
		if(!"PROD".equalsIgnoreCase(ENVIRONMENT)){
			return;
		}
		Properties props = new Properties();
		props.put("mail.smtp.auth", "true");
		props.put("mail.smtp.starttls.enable", "true");
		props.put("mail.smtp.host", host);
		props.put("mail.smtp.port", port);

		Session session = Session.getInstance(props,
				new javax.mail.Authenticator() {
			protected PasswordAuthentication getPasswordAuthentication() {
				return new PasswordAuthentication(username, password);
			}
		});

		try {

			Message message = new MimeMessage(session);
			message.setFrom(new InternetAddress(from, "NMIMS Global Access SCE"));
			message.setRecipients(Message.RecipientType.TO,InternetAddress.parse(email));
			String body = "";
			if("APPROVE".equals(action)){
				message.setSubject("Your DD "+ddno + " is Approved");

				body = "Dear Student, \n\n"
						+"Your DD "+ddno +" has been approved. Please login to Exam portal and complete exam booking process.\n"
						+ "Student ID: "+sapid
						+"\n\n"
						+"Thanks & Regards"
						+"\n"
						+"NMIMS GLOBAL ACCESS SCHOOL FOR CONTINUING EDUCATION";

			}else {
				message.setSubject("Your DD "+ddno + " is Rejected");
				body = "Dear Student, \n\n"
						+ "Your DD "+ddno +" has been rejected.\n "
						+ "Reason: "+ reason +"\n"
						+ "Student ID: "+sapid
						+ "\n\n"
						+ "Thanks & Regards"
						+ "\n"
						+ "NMIMS GLOBAL ACCESS SCHOOL FOR CONTINUING EDUCATION";
			}
			message.setText(body);
			Transport.send(message);

			loggerForEmailCount.info("Total 1 Mail sent via(SMTP) from "+from+", Email Subject : Your DD "+ddno+ " is Approved, for sendDDStatusChangeEmail");

		} catch(Exception e) {
			
		}



	}

	@Async
	public void sendBookingSummaryEmail(HttpServletRequest request, ExamBookingDAO dao, ExamCenterDAO eDao) {
		if(!"PROD".equalsIgnoreCase(ENVIRONMENT)){
			return;
		}
		Properties props = new Properties();
		props.put("mail.smtp.auth", "true");
		props.put("mail.smtp.starttls.enable", "true");
		props.put("mail.smtp.host", host);
		props.put("mail.smtp.port", port);


		StudentExamBean student = (StudentExamBean)request.getSession().getAttribute("studentExam");
		
		HashMap<String,String> corporateCenterUserMapping = (HashMap<String,String>)request.getSession().getAttribute("corporateCenterUserMapping");
	    Map<String, String> examCenterIdNameMap = (HashMap<String, String>)request.getSession().getAttribute("examCenterIdNameMap");
		//List<ExamBookingTransactionBean> examBookings = (List<ExamBookingTransactionBean>)request.getSession().getAttribute("examBookings");
		List<ExamBookingTransactionBean> examBookings = dao.getConfirmedBooking(student.getSapid());
		String year = examBookings.get(0).getYear();
		String month = examBookings.get(0).getMonth();

		Session session = Session.getInstance(props,
				new javax.mail.Authenticator() {
			protected PasswordAuthentication getPasswordAuthentication() {
				return new PasswordAuthentication(username, password);
			}
		});

		try {

			Message message = new MimeMessage(session);
			message.setFrom(new InternetAddress(from, "NMIMS Global Access SCE"));
			message.setRecipients(Message.RecipientType.TO,InternetAddress.parse(student.getEmailId()));
			message.setRecipients(Message.RecipientType.BCC,InternetAddress.parse("jforcesolutions@gmail.com"));
			message.setSubject("Your Exam Registration Summary");

			String body = "";

			body = "Dear " + student.getFirstName() +", <br>"
					+"Here is your Exam Registration Summary for " + month +"-"+ year +" exam. <br>"
					+ " Student ID: "+student.getSapid() +"<br>"
					+ " Program: "+student.getProgram()+"<br>"
					+ "<br>";


			body = body  
					+ "<table border=\"1\">"
					+ "<thead>"
					+ "<tr> "
					+ "<th>Sr. No.</th>"
					+ "<th>Subject</th>"
					+ "<th>Sem</th>"
					+ "<th>Date</th>"
					+ "<th>Start Time</th>"
					+ "<th>End Time</th>"
					+ "<th>Transaction Status</th>"
					+ "<th>Booking Status</th>"
					+ "<th>Exam Center Booked</th>"
					+ "</tr>"
					+ "</thead>"
					+ "<tbody>";


			int count = 1;
			for(int i = 0; i < examBookings.size(); i++){
				ExamBookingTransactionBean bean = (ExamBookingTransactionBean)examBookings.get(i);
				SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
				SimpleDateFormat dateFormatter = new SimpleDateFormat("EEEE, dd-MMM-yyyy");
				Date formattedDate = formatter.parse(bean.getExamDate());
				String examDate = dateFormatter.format(formattedDate);


				String booked = bean.getBooked();
				String bookingStatus = null;
				if("Y".equals(booked)){
					bookingStatus = "Booked";
				}else{
					bookingStatus = "Not Booked";
				}
				String examCenterName = examCenterIdNameMap.get(bean.getCenterId());
				
				String subject = bean.getSubject();

				if (StringUtils.isBlank(examCenterName) && !"Project".equals(subject) && !"Module 4 - Project".equals(subject)) {
					//sendEmailForExamCenterNull(student,bean.getSubject());
					examCenterIdNameMap =eDao.getExamCenterIdNameMap();
					examCenterName = examCenterIdNameMap.get(bean.getCenterId());
					//continue;
				}
				

				body = body 

						+ " <tr> "
						+ "    <td align=\"center\"> " + (count++) +"</td> "
						+ "	<td> " + subject + " </td> "
						+ "	<td align=\"center\">" + bean.getSem()+"</td> ";

				if("Project".equals(subject) || "Module 4 - Project".equals(subject)){ 
					body = body 
							+ "	<td>NA</td>"
							+ "<td>NA</td>"
							+ "<td>NA</td>"
							+ "<td>"+ bean.getTranStatus() +"</td>"
							+ "<td align=\"center\">" +bookingStatus+ "</td>"
							+ "<td>NA</td>";
				}else{ 
					body = body 
							+ "<td>" +examDate+ "</td>"
							+ "<td>" +bean.getExamTime()+ "</td>"
							+ "<td>" +bean.getExamEndTime()+ "</td>"
							+ "<td>" +bean.getTranStatus()+ "</td>"
							+ "<td align=\"center\">" +bookingStatus+ "</td>"
							+ "<td>" +examCenterName+ "</td>";
				}



				body = body + 	"</tr> ";								

			} 

			body = body + 	"</tbody> </table>";	 	

			body = body + "<br><b>Note:</b><br>"
					
					//Commented by Somesh on 18.06.2020 
					/*
					+ "1. This Email is NOT a Hall ticket.<br>"
					+ "2. Hall Ticket will be made available for download on Exam Portal 7 days prior to Exams.<br>"
					+ "3. NMIMS University reserves right to shift students to another available exam center in case of any unavoidable circumstances. <br>";
					*/
					
					+ "1. The NMIMS University reserves right to take decisions in case of any unavoidable circumstances <br>"
					+ "2. The NMIMS University reserves right to shift students to another available exam slot in case of any unavoidable circumstances. <br>"
					+ "3. List of Do's and Don'ts and exam conduct process for the online exam will be sent separately, "
					+ 	 "exam link will be made available on student portal 2 days before the exam goes live. Link will be active 30-15 mins prior to the actual exam date and time <br>"
					+ "4. Compatibility link is very important for you to understand the readiness of your system "
					+ 	 " <a href=\"https://tests.mettl.com/system-check?i=db696a8e#/systemCheck\">https://tests.mettl.com/system-check?i=db696a8e#/systemCheck</a>"
					+ 	 " (if you cannot click, copy paste the link in your browser) <br>";
			

			body = body + "<br>Thanks & Regards"
					+ "<br>"
					+ "NMIMS GLOBAL ACCESS SCHOOL FOR CONTINUING EDUCATION";

			message.setContent(body, "text/html; charset=utf-8");
			Transport.send(message);

			loggerForEmailCount.info("Total 1 Mail sent via(SMTP) from "+from+", Email Subject : Your Exam Registration Summary, for sendBookingSummaryEmail");

		} catch(Exception e) {
			
		}

	}
	@Async
	public void sendBookingSummaryEmailFromWebhook(StudentExamBean student, ExamBookingDAO dao, ExamCenterDAO eDao,
			Map<String, String> examCenterIdNameMap) {
		if(!"PROD".equalsIgnoreCase(ENVIRONMENT)){
			return;
		}
		Properties props = new Properties();
		props.put("mail.smtp.auth", "true");
		props.put("mail.smtp.starttls.enable", "true");
		props.put("mail.smtp.host", host);
		props.put("mail.smtp.port", port);

		//List<ExamBookingTransactionBean> examBookings = (List<ExamBookingTransactionBean>)request.getSession().getAttribute("examBookings");
		List<ExamBookingTransactionBean> examBookings = dao.getConfirmedBooking(student.getSapid());
		String year = examBookings.get(0).getYear();
		String month = examBookings.get(0).getMonth();

		Session session = Session.getInstance(props,
				new javax.mail.Authenticator() {
			protected PasswordAuthentication getPasswordAuthentication() {
				return new PasswordAuthentication(username, password);
			}
		});

		try {

			Message message = new MimeMessage(session);
			message.setFrom(new InternetAddress(from, "NMIMS Global Access SCE"));
			message.setRecipients(Message.RecipientType.TO,InternetAddress.parse(student.getEmailId()));
			message.setRecipients(Message.RecipientType.BCC,InternetAddress.parse("jforcesolutions@gmail.com"));
			message.setSubject("Your Exam Registration Summary");

			String body = "";

			body = "Dear " + student.getFirstName() +", <br>"
					+"Here is your Exam Registration Summary for " + month +"-"+ year +" exam. <br>"
					+ " Student ID: "+student.getSapid() +"<br>"
					+ " Program: "+student.getProgram()+"<br>"
					+ "<br>";


			body = body  
					+ "<table border=\"1\">"
					+ "<thead>"
					+ "<tr> "
					+ "<th>Sr. No.</th>"
					+ "<th>Subject</th>"
					+ "<th>Sem</th>"
					+ "<th>Date</th>"
					+ "<th>Start Time</th>"
					+ "<th>End Time</th>"
					+ "<th>Transaction Status</th>"
					+ "<th>Booking Status</th>"
					+ "<th>Exam Center Booked</th>"
					+ "</tr>"
					+ "</thead>"
					+ "<tbody>";


			int count = 1;
			for(int i = 0; i < examBookings.size(); i++){
				ExamBookingTransactionBean bean = (ExamBookingTransactionBean)examBookings.get(i);
				SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
				SimpleDateFormat dateFormatter = new SimpleDateFormat("EEEE, dd-MMM-yyyy");
				Date formattedDate = formatter.parse(bean.getExamDate());
				String examDate = dateFormatter.format(formattedDate);


				String booked = bean.getBooked();
				String bookingStatus = null;
				if("Y".equals(booked)){
					bookingStatus = "Booked";
				}else{
					bookingStatus = "Not Booked";
				}
				String examCenterName = examCenterIdNameMap.get(bean.getCenterId());
				
				String subject = bean.getSubject();

				if (StringUtils.isBlank(examCenterName) && !"Project".equals(subject) && !"Module 4 - Project".equals(subject)) {
					//sendEmailForExamCenterNull(student,bean.getSubject());
					examCenterIdNameMap =eDao.getExamCenterIdNameMap();
					examCenterName = examCenterIdNameMap.get(bean.getCenterId());
					//continue;
				}
				

				body = body 

						+ " <tr> "
						+ "    <td align=\"center\"> " + (count++) +"</td> "
						+ "	<td> " + subject + " </td> "
						+ "	<td align=\"center\">" + bean.getSem()+"</td> ";

				if("Project".equals(subject) || "Module 4 - Project".equals(subject)){ 
					body = body 
							+ "	<td>NA</td>"
							+ "<td>NA</td>"
							+ "<td>NA</td>"
							+ "<td>"+ bean.getTranStatus() +"</td>"
							+ "<td align=\"center\">" +bookingStatus+ "</td>"
							+ "<td>NA</td>";
				}else{ 
					body = body 
							+ "<td>" +examDate+ "</td>"
							+ "<td>" +bean.getExamTime()+ "</td>"
							+ "<td>" +bean.getExamEndTime()+ "</td>"
							+ "<td>" +bean.getTranStatus()+ "</td>"
							+ "<td align=\"center\">" +bookingStatus+ "</td>"
							+ "<td>" +examCenterName+ "</td>";
				}



				body = body + 	"</tr> ";								

			} 

			body = body + 	"</tbody> </table>";	 	

			body = body + "<br><b>Note:</b><br>"
					
					//Commented by Somesh on 18.06.2020 
					/*
					+ "1. This Email is NOT a Hall ticket.<br>"
					+ "2. Hall Ticket will be made available for download on Exam Portal 7 days prior to Exams.<br>"
					+ "3. NMIMS University reserves right to shift students to another available exam center in case of any unavoidable circumstances. <br>";
					*/
					
					+ "1. The NMIMS University reserves right to take decisions in case of any unavoidable circumstances <br>"
					+ "2. The NMIMS University reserves right to shift students to another available exam slot in case of any unavoidable circumstances. <br>"
					+ "3. List of Do's and Don'ts and exam conduct process for the online exam will be sent separately, "
					+ 	 "exam link will be made available on student portal 2 days before the exam goes live. Link will be active 30-15 mins prior to the actual exam date and time <br>"
					+ "4. Compatibility link is very important for you to understand the readiness of your system "
					+ 	 " <a href=\"https://tests.mettl.com/system-check?i=db696a8e#/systemCheck\">https://tests.mettl.com/system-check?i=db696a8e#/systemCheck</a>"
					+ 	 " (if you cannot click, copy paste the link in your browser) <br>";
			

			body = body + "<br>Thanks & Regards"
					+ "<br>"
					+ "NMIMS GLOBAL ACCESS SCHOOL FOR CONTINUING EDUCATION";

			message.setContent(body, "text/html; charset=utf-8");
			Transport.send(message);

			loggerForEmailCount.info("Total 1 Mail sent via(SMTP) from "+from+", Email Subject : Your Exam Registration Summary, for sendBookingSummaryEmail");

		} catch(Exception e) {
			
		}

	}
	
	
	@Async
	public void sendExecutiveExamBookingSummaryEmail(HttpServletRequest request, List<ExamBookingTransactionBean> examBookings, 
			ExecutiveExamOrderBean examLiveCurrently, ExamCenterDAO edao) {
		/*if(!"PROD".equalsIgnoreCase(ENVIRONMENT)){
			return;
		}*/
		Properties props = new Properties();
		props.put("mail.smtp.auth", "true");
		props.put("mail.smtp.starttls.enable", "true");
		props.put("mail.smtp.host", host);
		props.put("mail.smtp.port", port);


		StudentExamBean student = (StudentExamBean)request.getSession().getAttribute("studentExam");
		
	    Map<String, String> examCenterIdNameMap = (HashMap<String, String>)request.getSession().getAttribute("examCenterIdNameMap");
		//List<ExamBookingTransactionBean> examBookings = (List<ExamBookingTransactionBean>)request.getSession().getAttribute("examBookings");
		String year = examLiveCurrently.getYear();
		String month = examLiveCurrently.getMonth();

		Session session = Session.getInstance(props,
				new javax.mail.Authenticator() {
			protected PasswordAuthentication getPasswordAuthentication() {
				return new PasswordAuthentication(username, password);
			}
		});

		try {

			Message message = new MimeMessage(session);
			message.setFrom(new InternetAddress(from, "NMIMS Global Access SCE"));
			message.setRecipients(Message.RecipientType.TO,InternetAddress.parse(student.getEmailId()));
			message.setSubject("Your Exam Registration Summary");

			String body = "";

			body = "Dear " + student.getFirstName() +", <br>"
					+"Here is your Exam Registration Summary for " + month +"-"+ year +" exam. <br>"
					+ " Student ID: "+student.getSapid() +"<br>"
					+ " Program: "+student.getProgram()+"<br>"
					+ "<br>";


			body = body  
					+ "<table border=\"1\">"
					+ "<thead>"
					+ "<tr> "
					+ "<th>Sr. No.</th>"
					+ "<th>Subject</th>"
					+ "<th>Sem</th>"
					+ "<th>Date</th>"
					+ "<th>Start Time</th>"
					+ "<th>End Time</th>"
					+ "<th>Booking Status</th>"
					+ "<th>Exam Center Booked</th>"
					+ "</tr>"
					+ "</thead>"
					+ "<tbody>";


			int count = 1;
			for(int i = 0; i < examBookings.size(); i++){
				ExamBookingTransactionBean bean = (ExamBookingTransactionBean)examBookings.get(i);
				SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
				SimpleDateFormat dateFormatter = new SimpleDateFormat("EEEE, dd-MMM-yyyy");
				Date formattedDate = formatter.parse(bean.getExamDate());
				String examDate = dateFormatter.format(formattedDate);


				String booked = bean.getBooked();
				String bookingStatus = null;
				if("Y".equals(booked)){
					bookingStatus = "Booked";
				}else{
					bookingStatus = "Not Booked";
				}
				String examCenterName = examCenterIdNameMap.get(bean.getCenterId());
				
				String subject = bean.getSubject();

				if (StringUtils.isBlank(examCenterName) && !"Project".equals(subject) && !"Module 4 - Project".equals(subject)) {
					//sendEmailForExamCenterNull(student,bean.getSubject());
					examCenterIdNameMap =edao.getExamCenterIdNameMap();
					examCenterName = examCenterIdNameMap.get(bean.getCenterId());
					//continue;
				}

				body = body 

						+ " <tr> "
						+ "    <td align=\"center\"> " + (count++) +"</td> "
						+ "	<td> " + subject + " </td> "
						+ "	<td align=\"center\">" + bean.getSem()+"</td> ";

					body = body 
							+ "<td>" +examDate+ "</td>"
							+ "<td>" +bean.getExamTime()+ "</td>"
							+ "<td>" +bean.getExamEndTime()+ "</td>"
							+ "<td align=\"center\">" +bookingStatus+ "</td>"
							+ "<td>" +examCenterName+ "</td>";



				body = body + 	"</tr> ";								

			} 

			body = body + 	"</tbody> </table>";	 	

			body = body + "<br><b>Note:</b><br>"
					+ "1. This Email is NOT a Hall ticket.<br>"
					+ "2. Hall Ticket will be made available for download on Exam Portal 3 days prior to Exams.<br>"
					+ "3. NMIMS University reserves right to shift students to another available exam center in case of any unavoidable circumstances. <br>";
			

			body = body + "<br>Thanks & Regards"
					+ "<br>"
					+ "NMIMS GLOBAL ACCESS SCHOOL FOR CONTINUING EDUCATION";




			message.setContent(body, "text/html; charset=utf-8");

			Transport.send(message);

			loggerForEmailCount.info("Total 1 Mail sent via(SMTP) from "+from+", Email Subject : Your Exam Registration Summary, for sendExecutiveExamBookingSummaryEmail");

		} catch(Exception e) {
			
		}

	}

	//Send Eamil for Executive Exam booking confirm start
	@Async
	public void sendBookingSummaryEmailToExecutiveStudent(HttpServletRequest request, ExamBookingDAO dao) {
		if(!"PROD".equalsIgnoreCase(ENVIRONMENT)){
			return;
		}
		Properties props = new Properties();
		props.put("mail.smtp.auth", "true");
		props.put("mail.smtp.starttls.enable", "true");
		props.put("mail.smtp.host", host);
		props.put("mail.smtp.port", port);


		StudentExamBean student = (StudentExamBean)request.getSession().getAttribute("studentExam");
		
		HashMap<String,String> corporateCenterUserMapping = (HashMap<String,String>)request.getSession().getAttribute("corporateCenterUserMapping");
	    Map<String, String> examCenterIdNameMap = (HashMap<String, String>)request.getSession().getAttribute("examCenterIdNameMap");
		//List<ExamBookingTransactionBean> examBookings = (List<ExamBookingTransactionBean>)request.getSession().getAttribute("examBookings");
		List<ExamBookingTransactionBean> examBookings = dao.getConfirmedBooking(student.getSapid());
		String year = examBookings.get(0).getYear();
		String month = examBookings.get(0).getMonth();

		Session session = Session.getInstance(props,
				new javax.mail.Authenticator() {
			protected PasswordAuthentication getPasswordAuthentication() {
				return new PasswordAuthentication(username, password);
			}
		});

		try {

			Message message = new MimeMessage(session);
			message.setFrom(new InternetAddress(from, "NMIMS Global Access SCE"));
			message.setRecipients(Message.RecipientType.TO,InternetAddress.parse(student.getEmailId()));
			message.setSubject("Your Exam Registration Summary");

			String body = "";

			body = "Dear " + student.getFirstName() +", <br>"
					+"Here is your Exam Registration Summary for " + month +"-"+ year +" exam. <br>"
					+ " Student ID: "+student.getSapid() +"<br>"
					+ " Program: "+student.getProgram()+"<br>"
					+ "<br>";


			body = body  
					+ "<table border=\"1\">"
					+ "<thead>"
					+ "<tr> "
					+ "<th>Sr. No.</th>"
					+ "<th>Subject</th>"
					+ "<th>Sem</th>"
					+ "<th>Date</th>"
					+ "<th>Start Time</th>"
					+ "<th>End Time</th>"
					+ "<th>Transaction Status</th>"
					+ "<th>Booking Status</th>"
					+ "<th>Exam Center Booked</th>"
					+ "</tr>"
					+ "</thead>"
					+ "<tbody>";


			int count = 1;
			for(int i = 0; i < examBookings.size(); i++){
				ExamBookingTransactionBean bean = (ExamBookingTransactionBean)examBookings.get(i);
				SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
				SimpleDateFormat dateFormatter = new SimpleDateFormat("EEEE, dd-MMM-yyyy");
				Date formattedDate = formatter.parse(bean.getExamDate());
				String examDate = dateFormatter.format(formattedDate);


				String booked = bean.getBooked();
				String bookingStatus = null;
				if("Y".equals(booked)){
					bookingStatus = "Booked";
				}else{
					bookingStatus = "Not Booked";
				}
				String examCenterName = examCenterIdNameMap.get(bean.getCenterId());

				
				String subject = bean.getSubject();

				if (StringUtils.isBlank(examCenterName) && !"Project".equals(subject) && !"Module 4 - Project".equals(subject)) {
					//sendEmailForExamCenterNull(student,bean.getSubject());
					continue;
				}

				body = body 

						+ " <tr> "
						+ "    <td align=\"center\"> " + (count++) +"</td> "
						+ "	<td> " + subject + " </td> "
						+ "	<td align=\"center\">" + bean.getSem()+"</td> ";

				if("Project".equals(subject) || "Module 4 - Project".equals(subject)){ 
					body = body 
							+ "	<td>NA</td>"
							+ "<td>NA</td>"
							+ "<td>NA</td>"
							+ "<td>"+ bean.getTranStatus() +"</td>"
							+ "<td align=\"center\">" +bookingStatus+ "</td>"
							+ "<td>NA</td>";
				}else{ 
					body = body 
							+ "<td>" +examDate+ "</td>"
							+ "<td>" +bean.getExamTime()+ "</td>"
							+ "<td>" +bean.getExamEndTime()+ "</td>"
							+ "<td>" +bean.getTranStatus()+ "</td>"
							+ "<td align=\"center\">" +bookingStatus+ "</td>"
							+ "<td>" +examCenterName+ "</td>";
				}



				body = body + 	"</tr> ";								

			} 

			body = body + 	"</tbody> </table>";	 	

			body = body + "<br><b>Note:</b><br>"
					+ "1. This Email is NOT a Hall ticket.<br>"
					+ "2. Hall Ticket will be made available for download on Exam Portal 3 days prior to Exams.<br>"
					+ "3. NMIMS University reserves right to shift students to another available exam center in case of any unavoidable circumstances. <br>";
			

			body = body + "<br>Thanks & Regards"
					+ "<br>"
					+ "NMIMS GLOBAL ACCESS SCHOOL FOR CONTINUING EDUCATION";




			message.setContent(body, "text/html; charset=utf-8");

			Transport.send(message);

			loggerForEmailCount.info("Total 1 Mail sent via(SMTP) from "+from+", Email Subject : Your Exam Registration Summary, for sendBookingSummaryEmailToExecutiveStudent");

		} catch(Exception e) {
			
		}

	}
	//end
	
	@Async
	public void sendBookingExpiredEmailForBooking(List<ExamBookingTransactionBean> examBookings) {
		if(!"PROD".equalsIgnoreCase(ENVIRONMENT)){
			return;
		}
		Properties props = new Properties();
		props.put("mail.smtp.auth", "true");
		props.put("mail.smtp.starttls.enable", "true");
		props.put("mail.smtp.host", host);
		props.put("mail.smtp.port", port);
		
		Session session = Session.getInstance(props,
				new javax.mail.Authenticator() {
			protected PasswordAuthentication getPasswordAuthentication() {
				return new PasswordAuthentication(username, password);
			}
		});
		
		try {
			Message message = new MimeMessage(session);
			message.setFrom(new InternetAddress(from, "NMIMS Global Access SCE"));
			message.setRecipients(Message.RecipientType.TO,InternetAddress.parse("sagar.shinde.ext@nmims.edu, siddheshwar.khanse.ext@nmims.edu, abhay.sakpal.ext@nmims.edu"));
			//message.setRecipients(Message.RecipientType.BCC,InternetAddress.parse("sanketpanaskar@gmail.com, jigna.patel@nmims.edu, Archana.Doifode@nmims.edu, pranit.shirke.ext@nmims.edu, jforce.solution@gmail.com, nelson.soans@nmims.edu, sangeeta.shetty@nmims.edu"));
			//message.setRecipients(Message.RecipientType.BCC,InternetAddress.parse("sanketpanaskar@gmail.com"));
			message.setSubject("Your Exam Registration Expired");
			String body = "<html><head><style>table, th, td { border: 1px solid black; border-collapse: collapse; }th, td { padding: 15px; }th { text-align: left; }</style></head><body>";
			body = body + "Dear Team <br>"
					+"Here is your Exam Registration Expired list. <br>"
					+ "<br><br><table style='width:100%'><tr>"
					+ "<th>TrackId</th>"
					+ "<th>Subject</th>"
					+ "<th>PaymentOption</th>"
					+ "<th>Sapid</th>"
					+ "<th>TransactionDate</th>"
					+ "<th>TransactionStatus</th>"
					+ "</tr>";
			for (ExamBookingTransactionBean examBookingTransactionBean : examBookings) {
				body = body +  "<tr>"
						+ "<td> "+examBookingTransactionBean.getTrackId() +" </td>"
						+ "<td>  "+examBookingTransactionBean.getSubject() +" </td>"
						+ "<td> "+examBookingTransactionBean.getPaymentOption() +" </td>"
						+ "<td> "+examBookingTransactionBean.getSapid() +" </td>"
						+ "<td> "+examBookingTransactionBean.getTranDateTime() +" </td>"
						+ "<td> "+examBookingTransactionBean.getTranStatus() +" </td>"
						+ "</tr>";
			}
			body = body + "</tbody></table>";
			body = body + "<br>Thanks & Regards"
					+ "<br>"
					+ "NMIMS GLOBAL ACCESS SCHOOL FOR CONTINUING EDUCATION";
			body = body + "</body></html>";
			message.setContent(body, "text/html; charset=utf-8");
			Transport.send(message);
			
			loggerForEmailCount.info("Total 1 Mail sent via(SMTP) from "+from+", Email Subject : Your Exam Registration Expired, for sendBookingExpiredEmailForBooking");
		}
		catch (Exception e) {
			// TODO: handle exception
		}
	}
	
	 @Async
	public void sendBookingSummaryEmailForConflictBooking(StudentExamBean student, List<ExamBookingTransactionBean> examBookings,
			Map<String, String> examCenterIdNameMap, ExamCenterDAO edao) {
		
		if(!"PROD".equalsIgnoreCase(ENVIRONMENT)){
			return;
		}
		Properties props = new Properties();
		props.put("mail.smtp.auth", "true");
		props.put("mail.smtp.starttls.enable", "true");
		props.put("mail.smtp.host", host);
		props.put("mail.smtp.port", port);


		Session session = Session.getInstance(props,
				new javax.mail.Authenticator() {
			protected PasswordAuthentication getPasswordAuthentication() {
				return new PasswordAuthentication(username, password);
			}
		});

		try {

			Message message = new MimeMessage(session);
			message.setFrom(new InternetAddress(from, "NMIMS Global Access SCE"));
			message.setRecipients(Message.RecipientType.TO,InternetAddress.parse(student.getEmailId()));
			message.setRecipients(Message.RecipientType.BCC,InternetAddress.parse("sanketpanaskar@gmail.com, jigna.patel@nmims.edu, "
					+ "harshalee.ullal@nmims.edu, pooja.jadhav@nmims.edu, christopher.kevin@nmims.edu, pranit.shirke.ext@nmims.edu, jforce.solution@gmail.com, sangeeta.shetty@nmims.edu"));
			//message.setRecipients(Message.RecipientType.BCC,InternetAddress.parse("sanketpanaskar@gmail.com"));
			message.setSubject("Your Exam Registration Summary");

			String body = "";

			body = "Dear " + student.getFirstName() +", <br>"
					+"Here is your Exam Registration Summary. <br>"
					+ " Student ID: "+student.getSapid()
					+ "<br><br>";
			
			ExamBookingTransactionBean examBookingBean_tmp = (ExamBookingTransactionBean)examBookings.get(0);
			if(examBookingBean_tmp != null && ("Project".equalsIgnoreCase(examBookingBean_tmp.getSubject()) || "Module 4 - Project".equals(examBookingBean_tmp.getSubject()))) {
				String bookingStatus = null;
				if("Y".equals(examBookingBean_tmp.getBooked())){
					bookingStatus = "Booked";
				}else{
					bookingStatus = "Not Booked";
				}
				body = body 
						+ "<table border=\"1\">"
						+ "<thead>"
						+ "<tr> "
						+ "<th>Sr. No.</th>"
						+ "<th>Subject</th>"
						+ "<th>Sem</th>"
						+ "<th>Booking Status</th>"
						+ "</tr>"
						+ "</thead><tbody>"
						+ "<tr>"
						+ "<td>1</td>"
						+ "<td>"+ examBookingBean_tmp.getSubject() +"</td>"
						+ "<td>"+ examBookingBean_tmp.getSem() +"</td>"
						+ "<td>"+ bookingStatus +"</td>"
						+ "</tr>";
			}else {
				body = body  
						+ "<table border=\"1\">"
						+ "<thead>"
						+ "<tr> "
						+ "<th>Sr. No.</th>"
						+ "<th>Subject</th>"
						+ "<th>Sem</th>"
						+ "<th>Date</th>"
						+ "<th>Start Time</th>"
						+ "<th>End Time</th>"
						+ "<th>Transaction Status</th>"
						+ "<th>Booking Status</th>"
						+ "<th>Exam Center Booked</th>"
						+ "</tr>"
						+ "</thead>"
						+ "<tbody>";
	
	
				int count = 1;
				for(int i = 0; i < examBookings.size(); i++){
					ExamBookingTransactionBean bean = (ExamBookingTransactionBean)examBookings.get(i);
					if("Project".equalsIgnoreCase(bean.getSubject()) || "Module 4 - Project".equals(bean.getSubject())) {
						continue;
					}
					SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
					SimpleDateFormat dateFormatter = new SimpleDateFormat("EEEE, dd-MMM-yyyy");
					Date formattedDate = formatter.parse(bean.getExamDate());
					String examDate = dateFormatter.format(formattedDate);
	
	
					String booked = bean.getBooked();
					String bookingStatus = null;
					if("Y".equals(booked)){
						bookingStatus = "Booked";
					}else{
						bookingStatus = "Not Booked";
					}
					String examCenterName = examCenterIdNameMap.get(bean.getCenterId());
					
					String subject = bean.getSubject();
	
					if (StringUtils.isBlank(examCenterName) && !"Project".equals(subject) && !"Module 4 - Project".equals(subject)) {
						//sendEmailForExamCenterNull(student,bean.getSubject());
						examCenterIdNameMap =edao.getExamCenterIdNameMap();
						examCenterName = examCenterIdNameMap.get(bean.getCenterId());
						//continue;
					}
	
					body = body 
	
							+ " <tr> "
							+ "    <td align=\"center\"> " + (count++) +"</td> "
							+ "	<td> " + subject + " </td> "
							+ "	<td align=\"center\">" + bean.getSem()+"</td> ";
	
					/*if("Project".equals(subject)){ 
						body = body 
								+ "	<td>NA</td>"
								+ "<td>NA</td>"
								+ "<td>NA</td>"
								+ "<td>"+ bean.getTranStatus() +"</td>"
								+ "<td align=\"center\">" +bookingStatus+ "</td>"
								+ "<td>NA</td>";
					}else{ */
						body = body 
								+ "<td>" +examDate+ "</td>"
								+ "<td>" +bean.getExamTime()+ "</td>"
								+ "<td>" +bean.getExamEndTime()+ "</td>"
								+ "<td>" +bean.getTranStatus()+ "</td>"
								+ "<td align=\"center\">" +bookingStatus+ "</td>"
								+ "<td>" +examCenterName+ "</td>";
					
					body = body + 	"</tr> ";								
				} 
			
			}
			body = body + 	"</tbody> </table>";
			

			body = body + "<br><b>Note:</b><br>"
					
					//Commented by Somesh on 18.06.2020 
					/*
					+ "1. This Email is NOT a Hall ticket.<br>"
					+ "2. Hall Ticket will be made available for download on Exam Portal 7 days prior to Exams.<br>"
					+ "3. The NMIMS University reserves right to shift students to another available exam center in case of any unavoidable circumstances. <br>";
					*/
					
					+ "1. The NMIMS University reserves right to take decisions in case of any unavoidable circumstances <br>"
					+ "2. The NMIMS University reserves right to shift students to another available exam slot in case of any unavoidable circumstances. <br>"
					+ "3. List of Do's and Don'ts and exam conduct process for the online exam will be sent separately, "
					+ 	 "exam link will be made available on student portal 2 days before the exam goes live. Link will be active 30-15 mins prior to the actual exam date and time <br>"
					+ "4. Compatibility link is very important for you to understand the readiness of your system "
					+ 	 " <a href=\"https://tests.mettl.com/system-check?i=db696a8e#/systemCheck\">https://tests.mettl.com/system-check?i=db696a8e#/systemCheck</a>"
					+ 	 " (if you cannot click, copy paste the link in your browser) <br>";

			body = body + "<br>Thanks & Regards"
					+ "<br>"
					+ "NMIMS GLOBAL ACCESS SCHOOL FOR CONTINUING EDUCATION";




			message.setContent(body, "text/html; charset=utf-8");

			Transport.send(message);

			loggerForEmailCount.info("Total 1 Mail sent via(SMTP) from "+from+", Email Subject : Your Exam Registration Summary, for sendBookingSummaryEmailForConflictBooking");

		} catch(Exception e) {
			
		}



	}
	
	 @Async
		public void sendBookingSummaryEmailForProjectConflictBooking(StudentExamBean student, List<ExamBookingTransactionBean> examBookings,
				Map<String, String> examCenterIdNameMap,String projectLastDate) {
			
			if(!"PROD".equalsIgnoreCase(ENVIRONMENT)){
				return;
			}
			Properties props = new Properties();
			props.put("mail.smtp.auth", "true");
			props.put("mail.smtp.starttls.enable", "true");
			props.put("mail.smtp.host", host);
			props.put("mail.smtp.port", port);


			Session session = Session.getInstance(props,
					new javax.mail.Authenticator() {
				protected PasswordAuthentication getPasswordAuthentication() {
					return new PasswordAuthentication(username, password);
				}
			});

			try {
				Message message = new MimeMessage(session);
				message.setFrom(new InternetAddress(from, "NMIMS Global Access SCE"));
				message.setRecipients(Message.RecipientType.TO,InternetAddress.parse(student.getEmailId()));
				message.setRecipients(Message.RecipientType.BCC,InternetAddress.parse("sanketpanaskar@gmail.com, jigna.patel@nmims.edu, harshalee.ullal@nmims.edu, pooja.jadhav@nmims.edu, christopher.kevin@nmims.edu, pranit.shirke.ext@nmims.edu, jforce.solution@gmail.com, sangeeta.shetty@nmims.edu"));
				//message.setRecipients(Message.RecipientType.BCC,InternetAddress.parse("sanketpanaskar@gmail.com"));
				message.setSubject("Your Exam Registration Summary");

				String body = "";

			
	//************* added new to include project payment auto booking****************************
				int count = 1;
					for(int i = 0; i < examBookings.size(); i++){
						ExamBookingTransactionBean bean = (ExamBookingTransactionBean)examBookings.get(i);
						if(bean.getSubject().equalsIgnoreCase("Project") || bean.getSubject().equalsIgnoreCase("Module 4 - Project")){
							body = "Dear " + student.getFirstName() +", <br>"
								+"Here is your Exam Registration Summary. <br>"
								+ " Student ID: "+student.getSapid()
								+ "<br><br>";
							body = body  
								+ "<table border=\"1\">"
								+ "<thead>"
								+ "<tr> "
								+ "<th>Sr. No.</th>"
								+ "<th>Subject</th>"
								+ "<th>Sem</th>"
								+ "<th>Exam Year</th>"
								+ "<th>Exam Month</th>"
								+ "<th>Transaction Status</th>"
								+ "<th>Booking Status</th>"
								+ "</tr>"
								+ "</thead>"
								+ "<tbody>";

							String booked = bean.getBooked();
							String bookingStatus = null;
								if("Y".equals(booked)){
									bookingStatus = "Booked";
								}else{
									bookingStatus = "Not Booked";
								}
							
							String subject = bean.getSubject();
							body = body 
								+ " <tr> "
								+ "    <td align=\"center\"> " + (count++) +"</td> "
								+ "	<td> " + subject + " </td> "
								+ "	<td align=\"center\">" + bean.getSem()+"</td> ";

							body = body 
								+ "<td>" +bean.getYear()+ "</td>"
								+ "<td>" +bean.getMonth()+ "</td>"
								+ "<td>" +bean.getTranStatus()+ "</td>"
								+ "<td align=\"center\">" +bookingStatus+ "</td>";

							body = body + 	"</tr> ";								

							body = body + 	"</tbody> </table>";	 	

							body = body + "<br><b>Note:</b><br>"
								+ "1. Project submission last date for " + bean.getMonth() + " " + bean.getYear() + " is " + projectLastDate+" on or before 23.59hrs (IST).<br>"
								+ "2. Exam fees once paid will not be refunded nor carry forwarded to next exam cycle.<br>";
				//************* added new to include project payment auto booking end****************************				

							body = body + "<br>Thanks & Regards"
								+ "<br>"
								+ "NMIMS GLOBAL ACCESS SCHOOL FOR CONTINUING EDUCATION";
						}else{
							continue;
						}
					}
				
					if(body.isEmpty()){
						return;
					}else{
						message.setContent(body, "text/html; charset=utf-8");
						Transport.send(message);
					}
					loggerForEmailCount.info("Total 1 Mail sent via(SMTP) from "+from+", Email Subject : Your Exam Registration Summary, for sendBookingSummaryEmailForProjectConflictBooking");
				} catch(Exception e) {
				
				}

		}

	@Async
	public void sendSeatsRealseEmail(String sapid,	ArrayList<String> releaseSubjects, String email, String noCharges) {
		if(!"PROD".equalsIgnoreCase(ENVIRONMENT)){
			return;
		}
		
		Properties props = new Properties();
		props.put("mail.smtp.auth", "true");
		props.put("mail.smtp.starttls.enable", "true");
		props.put("mail.smtp.host", host);
		props.put("mail.smtp.port", port);


		Session session = Session.getInstance(props,
				new javax.mail.Authenticator() {
			protected PasswordAuthentication getPasswordAuthentication() {
				return new PasswordAuthentication(username, password);
			}
		});

		try {

			Message message = new MimeMessage(session);
			message.setFrom(new InternetAddress(from, "NMIMS Global Access SCE"));
			message.setRecipients(Message.RecipientType.TO,InternetAddress.parse(email));
			message.setSubject("Approval to change Exam Center");

			String body = "";

			body = "Dear Student, <br>"
					+"You have approval to change Exam Center for your below mentioned subjects. <br>";
			
			if(!"true".equalsIgnoreCase(noCharges)){
				body += "We wish to inform you that NMIMS University will charge additionally Processing Fee of (Rs.500/-) for Change of Exam Centre/Time slot in TOTAL per student and not per subject <br>"
						+"Exam Center change fees will be accepted only Online. <br>";
			}
					
			body +="Please visit Exam Portal and change exam centers. <br>"
					+ "Student ID: "+sapid
					+ "<br><br>";


			body = body  
					+ "<table border=\"1\">"
					+ "<thead>"
					+ "<tr> "
					+ "<th>Sr. No.</th>"
					+ "<th>Subject</th>"
					+ "</tr>"
					+ "</thead>"
					+ "<tbody>";


			int count = 1;
			for(int i = 0; i < releaseSubjects.size(); i++){



				body = body 

						+ " <tr> "
						+ "    <td align=\"center\"> " + (count++) +"</td> "
						+ "	<td> " + releaseSubjects.get(i) + " </td> ";

				body = body + 	"</tr> ";								

			} 

			body = body + "</tbody> </table>";	 	

			body = body + "<br>Thanks & Regards"
					+ "<br>"
					+ "NMIMS GLOBAL ACCESS SCHOOL FOR CONTINUING EDUCATION";


			message.setContent(body, "text/html; charset=utf-8");

			Transport.send(message);

			loggerForEmailCount.info("Total 1 Mail sent via(SMTP) from "+from+", Email Subject : Approval to change Exam Center, for sendSeatsRealseEmail");

		} catch(Exception e) {
			
		}



	}

	@Async
	public void sendSeatsCancelEmail(String sapid,	ArrayList<ExamBookingTransactionDTO> subjectExamDateTime, String email) {
		if(!"PROD".equalsIgnoreCase(ENVIRONMENT)){
			return;
		}
		
		Properties props = new Properties();
		props.put("mail.smtp.auth", "true");
		props.put("mail.smtp.starttls.enable", "true");
		props.put("mail.smtp.host", host);
		props.put("mail.smtp.port", port);


		Session session = Session.getInstance(props,
				new javax.mail.Authenticator() {
			protected PasswordAuthentication getPasswordAuthentication() {
				return new PasswordAuthentication(username, password);
			}
		});

		try {

			Message message = new MimeMessage(session);
			message.setFrom(new InternetAddress(from, "NMIMS Global Access SCE"));
			message.setRecipients(Message.RecipientType.TO,InternetAddress.parse(email));
			message.setSubject("Exam Booking Cancellation");

			String body = "";

			body = "Dear Student, <br>"
					+"Your exam booking has been cancelled as per the details below: <br>";
					
			body +="<br><br>";


			body = body  
					+ "<table border=\"1\">"
					+ "<thead>"
					+ "<tr> "
					+ "<th>Sr. No.</th>"
					+ "<th>Subject</th>"
					+ "<th>Date</th>"
					+ "<th>Time</th>"
					+ "<th>Status</th>"
					+ "</tr>"
					+ "</thead>"
					+ "<tbody>";


			int count = 1;
			for(int i = 0; i < subjectExamDateTime.size(); i++){



				body = body 

						+ " <tr> "
						+ " <td align=\"center\"> " + (count++) +"</td> "
						+ "	<td> " + subjectExamDateTime.get(i).getSubject() + " </td> "
						+ "	<td> " + subjectExamDateTime.get(i).getExamDate() + " </td> "
						+ "	<td> " + subjectExamDateTime.get(i).getExamTime() + " </td> "
						+ "	<td> Cancelled </td> ";

				body = body + 	"</tr> ";								

			} 

			body = body + "</tbody> </table>";	 	

			body = body + "<br>Thanks & Regards,"
					+ "<br>"
					+ "NMIMS GLOBAL ACCESS SCHOOL FOR CONTINUING EDUCATION";


			message.setContent(body, "text/html; charset=utf-8");
			Transport.send(message);

			loggerForEmailCount.info("Total 1 Mail sent via(SMTP) from "+from+", Email Subject : Exam Booking Cancellation, for sendSeatsCancelEmail");

		} catch(Exception e) {
			loggerForEmailCount.error("Error while sending exam booking cancellation email for sapid: "+sapid+":"+e.getMessage());
		}
	}
	
	@Async
	public void sendTransactionApproveEmail(StudentExamBean student, ExamBookingTransactionBean bean) {
		if(!"PROD".equalsIgnoreCase(ENVIRONMENT)){
			return;
		}
		Properties props = new Properties();
		props.put("mail.smtp.auth", "true");
		props.put("mail.smtp.starttls.enable", "true");
		props.put("mail.smtp.host", host);
		props.put("mail.smtp.port", port);


		Session session = Session.getInstance(props,
				new javax.mail.Authenticator() {
			protected PasswordAuthentication getPasswordAuthentication() {
				return new PasswordAuthentication(username, password);
			}
		});

		try {

			Message message = new MimeMessage(session);
			message.setFrom(new InternetAddress(from, "NMIMS Global Access SCE"));
			message.setRecipients(Message.RecipientType.TO,InternetAddress.parse(student.getEmailId()));
			message.setRecipients(Message.RecipientType.BCC,InternetAddress.parse("sanketpanaskar@gmail.com, jigna.patel@nmims.edu, harshalee.ullal@nmims.edu, pooja.jadhav@nmims.edu, christopher.kevin@nmims.edu, pranit.shirke.ext@nmims.edu, jforce.solution@gmail.com , nelson.soans@nmims.edu"));
			message.setSubject("Exam Registration Transaction Successful");

			String body = "";

			body = "Dear Student, <br>"
					+"Your transaction of amount " + bean.getRespAmount() + " is recorded successfully at our end <br>"
					+"Please visit Exam Portal and select exam centers to complete Exam Registration. <br>"
					+ " Student ID: "+student.getSapid()
					+ "<br>";



			body = body + "<br>Thanks & Regards"
					+ "<br>"
					+ "NMIMS GLOBAL ACCESS SCHOOL FOR CONTINUING EDUCATION";


			message.setContent(body, "text/html; charset=utf-8");

			Transport.send(message);

			loggerForEmailCount.info("Total 1 Mail sent via(SMTP) from "+from+", Email Subject : Exam Registration Transaction Successful, for sendTransactionApproveEmail");

		} catch(Exception e) {
			
		}



	}

	@Async
	public void sendConflictsEmail(ArrayList<ExamBookingTransactionBean> successfulButCenterNotAvailableExamBookings,
			ArrayList<ExamBookingTransactionBean> successfulButAlreadyBookedExamBookings) {
		if(!"PROD".equalsIgnoreCase(ENVIRONMENT)){
			return;
		}
		
		Properties props = new Properties();
		props.put("mail.smtp.auth", "true");
		props.put("mail.smtp.starttls.enable", "true");
		props.put("mail.smtp.host", host);
		props.put("mail.smtp.port", port);


		Session session = Session.getInstance(props,
				new javax.mail.Authenticator() {
			protected PasswordAuthentication getPasswordAuthentication() {
				return new PasswordAuthentication(username, password);
			}
		});

		try {

			Message message = new MimeMessage(session);
			message.setFrom(new InternetAddress(from, "NMIMS Global Access SCE"));
			message.setRecipients(Message.RecipientType.TO,InternetAddress.parse("sanketpanaskar@gmail.com, jigna.patel@nmims.edu, "
					+ "pranit.shirke.ext@nmims.edu, jforce.solution@gmail.com,sangeeta.shetty@nmims.edu, "
					+ "jforcesolutions@gmail.com, nashrah.shaikh@nmims.edu, harshalee.ullal@nmims.edu, pooja.jadhav@nmims.edu, christopher.kevin@nmims.edu, Ankita.Parmar@nmims.edu, "
					+ "khatija.shaikh@nmims.edu, harshalee.ullal@nmims.edu"));
			//message.setRecipients(Message.RecipientType.CC, InternetAddress.parse("rajiv.shah@nmims.edu"));

			//message.setRecipients(Message.RecipientType.TO,InternetAddress.parse("sanketpanaskar@gmail.com"));
			message.setSubject("Transaction Conflicts");

			String body = "";

			body = "Dear Team, <br>"
					+"Below is the list of Transactions Conflicts as of "+new java.util.Date().toString()+" <br> <br>";
			int count = 1;
			DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss"); 
			double totalDifference = 0;

			if(successfulButCenterNotAvailableExamBookings != null && successfulButCenterNotAvailableExamBookings.size() > 0){
				body = body  +"List of Conflict Transactions not auto-booked since Student selected centers are not available. (Approve in system & Inform students to re-select centers.) <br>";
				body = body  
						+ "<table border=\"1\">"
						+ "<thead>"
						+ "<tr> "
						+ "<th>Sr. No.</th>"
						+ "<th>Student ID</th>"
						+ "<th>Amount</th>"
						+ "<th>Amount at HDFC</th>"
						+ "<th>Payment Gateway</th>"
						+ "<th>Name</th>"
						+ "<th>Email</th>"
						+ "<th>Mobile</th>"
						+ "<th>Alt. Phone</th>"
						+ "<th>Transaction Initiation Time</th>"
						+ "<th>Transaction Completion Time</th>"
						+ "<th>Time Difference</th>"
						+ "</tr>"
						+ "</thead>"
						+ "<tbody>";



				for(int i = 0; i < successfulButCenterNotAvailableExamBookings.size(); i++){

					ExamBookingTransactionBean bean = successfulButCenterNotAvailableExamBookings.get(i);
					String sapid = bean.getSapid();
					String amount = bean.getAmount();
					String name = bean.getFirstName() + " "+ bean.getLastName();
					String email = bean.getEmailId();
					String mobile = bean.getMobile();
					String altPhone = bean.getAltPhone();
					String respAmount = bean.getRespAmount();
					
					String paymentOption = bean.getPaymentOption();
					
					String tranTime = bean.getTranDateTime();
					String respTranTime = bean.getRespTranDateTime();
					Date startTime = df.parse(tranTime.substring(0, 19));
					Date endTime = df.parse(respTranTime);

					long diff = endTime.getTime() - startTime.getTime();
					long diffSeconds = diff / 1000;         
					long diffMinutes = diff / (60 * 1000);  
					diffSeconds = diffSeconds - (diffMinutes * 60);
					String timeDiff = diffMinutes +":"+diffSeconds;

					totalDifference += Double.parseDouble(amount);

					body = body 

							+ " <tr align=\"center\"> "
							+ " <td> " + (count++) +"</td> "
							+ "	<td> " + sapid + " </td> "
							+ "	<td> " + amount + "/- </td> "
							+ "	<td> " + respAmount + " </td> "
							
							+ "	<td> " + paymentOption + " </td> "
							
							+ "	<td> " + name + " </td> "
							+ "	<td> " + email + " </td> "
							+ "	<td> " + mobile + " </td> "
							+ "	<td> " + altPhone + " </td> "
							+ "	<td> " + tranTime + " </td> "
							+ "	<td> " + respTranTime + " </td> "
							+ "	<td> " + timeDiff + " </td> "
							+ " </tr>";

				} 


				body = body + "</tbody> </table>";	
			}

			if(successfulButAlreadyBookedExamBookings != null && successfulButAlreadyBookedExamBookings.size() > 0){
				body = body  +"List of Conflict Transactions not auto-booked since Student has already booked subjects. (Process Refunds for these.)<br>";
				body = body  
						+ "<table border=\"1\">"
						+ "<thead>"
						+ "<tr> "
						+ "<th>Sr. No.</th>"
						+ "<th>Student ID</th>"
						+ "<th>Amount</th>"
						+ "<th>Amount at HDFC</th>"
						+ "<th>Name</th>"
						+ "<th>Email</th>"
						+ "<th>Mobile</th>"
						+ "<th>Alt. Phone</th>"
						+ "<th>Transaction Initiation Time</th>"
						+ "<th>Transaction Completion Time</th>"
						+ "<th>Time Difference</th>"
						+ "</tr>"
						+ "</thead>"
						+ "<tbody>";


				count = 1;

				for(int i = 0; i < successfulButAlreadyBookedExamBookings.size(); i++){

					ExamBookingTransactionBean bean = successfulButAlreadyBookedExamBookings.get(i);
					String sapid = bean.getSapid();
					String amount = bean.getAmount();
					String name = bean.getFirstName() + " "+ bean.getLastName();
					String email = bean.getEmailId();
					String mobile = bean.getMobile();
					String altPhone = bean.getAltPhone();
					String respAmount = bean.getRespAmount();

					String tranTime = bean.getTranDateTime();
					String respTranTime = bean.getRespTranDateTime();
					Date startTime = df.parse(tranTime.substring(0, 19));
					Date endTime = df.parse(respTranTime);

					long diff = endTime.getTime() - startTime.getTime();
					long diffSeconds = diff / 1000;         
					long diffMinutes = diff / (60 * 1000);  
					diffSeconds = diffSeconds - (diffMinutes * 60);
					String timeDiff = diffMinutes +":"+diffSeconds;

					totalDifference += Double.parseDouble(amount);

					body = body 

							+ " <tr align=\"center\"> "
							+ " <td> " + (count++) +"</td> "
							+ "	<td> " + sapid + " </td> "
							+ "	<td> " + amount + "/- </td> "
							+ "	<td> " + respAmount + " </td> "
							+ "	<td> " + name + " </td> "
							+ "	<td> " + email + " </td> "
							+ "	<td> " + mobile + " </td> "
							+ "	<td> " + altPhone + " </td> "
							+ "	<td> " + tranTime + " </td> "
							+ "	<td> " + respTranTime + " </td> "
							+ "	<td> " + timeDiff + " </td> "
							+ " </tr>";

				} 

				body = body + "</tbody> </table>";	
			}


			body = body + "<br>Total Amount: "+totalDifference +"<br>";	

			body = body + "<br>Thanks & Regards"
					+ "<br>"
					+ "NMIMS GLOBAL ACCESS SCHOOL FOR CONTINUING EDUCATION";


			message.setContent(body, "text/html; charset=utf-8");
			Transport.send(message);

			loggerForEmailCount.info("Total 1 Mail sent via(SMTP) from "+from+", Email Subject : Transaction Conflicts, for sendConflictsEmail");

		} catch(Exception e) {
			
		}



	}

	@Async
	public void sendBookingsMismatchEmail(ArrayList<ExamCenterBean> mismatchList) {
		
		if(!"PROD".equalsIgnoreCase(ENVIRONMENT)){
			return;
		}
		
		Properties props = new Properties();
		props.put("mail.smtp.auth", "true");
		props.put("mail.smtp.starttls.enable", "true");
		props.put("mail.smtp.host", host);
		props.put("mail.smtp.port", port);


		Session session = Session.getInstance(props,
				new javax.mail.Authenticator() {
			protected PasswordAuthentication getPasswordAuthentication() {
				return new PasswordAuthentication(username, password);
			}
		});

		try {

			Message message = new MimeMessage(session);
			message.setFrom(new InternetAddress(from, "NMIMS Global Access SCE"));
			message.setRecipients(Message.RecipientType.TO,InternetAddress.parse("sanketpanaskar@gmail.com"));

			message.setSubject("Bookings Conflicts");

			String body = "";

			body = "Dear Team, <br>"
					+"Below is the list of Bookings Conflicts as of "+new java.util.Date().toString()+" <br> <br>";

			body = body  
					+ "<table border=\"1\">"
					+ "<thead>"
					+ "<tr> "
					+ "<th>Sr. No.</th>"
					+ "<th>Center ID</th>"
					+ "<th>Center Name</th>"
					+ "<th>Exam Date</th>"
					+ "<th>Exam Time</th>"
					+ "<th>Bookings Done by Students</th>"
					+ "<th>Seats Marked Booked in DB</th>"
					+ "<th>Difference</th>"
					+ "</tr>"
					+ "</thead>"
					+ "<tbody>";


			int count = 1;

			for(int i = 0; i < mismatchList.size(); i++){

				ExamCenterBean bean = mismatchList.get(i);
				String centerId = bean.getCenterId();
				String centerName = bean.getExamCenterName();
				String examDate = bean.getDate();
				String examTime = bean.getStarttime();
				int studentsBooked = bean.getBooked();
				int slotsBooked = bean.getSlotsBooked();


				body = body 

						+ " <tr align=\"center\"> "
						+ " <td> " + (count++) +"</td> "
						+ "	<td> " + centerId + " </td> "
						+ "	<td> " + centerName + "/- </td> "
						+ "	<td> " + examDate + " </td> "
						+ "	<td> " + examTime + " </td> "
						+ "	<td> " + studentsBooked + " </td> "
						+ "	<td> " + slotsBooked + " </td> "
						+ "	<td> " + (slotsBooked - studentsBooked) + " </td> "
						+ " </tr>";


			} 

			body = body + "</tbody> </table>";	


			body = body + "<br>Thanks & Regards"
					+ "<br>"
					+ "NMIMS GLOBAL ACCESS SCHOOL FOR CONTINUING EDUCATION";


			message.setContent(body, "text/html; charset=utf-8");

			Transport.send(message);

			loggerForEmailCount.info("Total 1 Mail sent via(SMTP) from "+from+", Email Subject : Bookings Conflicts, for sendBookingsMismatchEmail");

		} catch(Exception e) {
			
		}



	}

	@Async
	public void mailStackTrace(String subject, Exception e) {
		
		if(!"PROD".equalsIgnoreCase(ENVIRONMENT)){
			return;
		}
		
		Properties props = new Properties();
		props.put("mail.smtp.auth", "true");
		props.put("mail.smtp.starttls.enable", "true");
		props.put("mail.smtp.host", host);
		props.put("mail.smtp.port", port);


		Session session = Session.getInstance(props,
				new javax.mail.Authenticator() {
			protected PasswordAuthentication getPasswordAuthentication() {
				return new PasswordAuthentication(username, password);
			}
		});

		try {

			Message message = new MimeMessage(session);
			message.setFrom(new InternetAddress(from, "NMIMS Global Access SCE"));
			message.setRecipients(Message.RecipientType.TO,InternetAddress.parse("sanketpanaskar@gmail.com,pranit.shirke.ext@nmims.edu,jforce.solution@gmail.com"));

			message.setSubject(subject);

			StringWriter writer = new StringWriter();
			e.printStackTrace(new PrintWriter(writer));
			String stackTrace = writer.toString();

			String body = stackTrace;

			body = body + "<br><br>Thanks & Regards"
					+ "<br>"
					+ "NMIMS GLOBAL ACCESS SCHOOL FOR CONTINUING EDUCATION";


			message.setContent(body, "text/html; charset=utf-8");

			Transport.send(message);

			loggerForEmailCount.info("Total 1 Mail sent via(SMTP) from "+from+", Email Subject : "+subject+", for mailStackTrace");

		} catch(Exception ex) {
			
		}



	}

	@Async
	public void sendRefundEmail(StudentExamBean student, ExamBookingTransactionBean bean) {
		
		if(!"PROD".equalsIgnoreCase(ENVIRONMENT)){
			return;
		}
		
		Properties props = new Properties();
		props.put("mail.smtp.auth", "true");
		props.put("mail.smtp.starttls.enable", "true");
		props.put("mail.smtp.host", host);
		props.put("mail.smtp.port", port);


		Session session = Session.getInstance(props,
				new javax.mail.Authenticator() {
			protected PasswordAuthentication getPasswordAuthentication() {
				return new PasswordAuthentication(username, password);
			}
		});

		try {

			Message message = new MimeMessage(session);
			message.setFrom(new InternetAddress(from, "NMIMS Global Access SCE"));
			message.setRecipients(Message.RecipientType.TO,InternetAddress.parse(student.getEmailId()));
			message.setRecipients(Message.RecipientType.BCC,InternetAddress.parse("sanketpanaskar@gmail.com, rajiv.shah@nmims.edu, jigna.patel@nmims.edu"));
			message.setSubject("Refund of Additional Exam Fees Paid");

			String body = "";

			body = "Dear Student, <br>"
					+ "We observed you had done multiple transactions and paid additional Exam Fees.<br>"
					+ "We have initiated a refund of Rs. " + bean.getRespAmount() + "/- and should be credited to your bank account used to perform transaction within 7 working days. <br>"
					+ "Student ID: "+student.getSapid()
					+ "<br>";



			body = body + "<br>Thanks & Regards"
					+ "<br>"
					+ "NMIMS GLOBAL ACCESS SCHOOL FOR CONTINUING EDUCATION";


			message.setContent(body, "text/html; charset=utf-8");

			Transport.send(message);

			loggerForEmailCount.info("Total 1 Mail sent via(SMTP) from "+from+", Email Subject : Refund of Additional Exam Fees Paid, for sendRefundEmail");

		} catch(Exception e) {
			
		}



	}



	@Async
	public void sendAssignmentReceivedEmail(StudentExamBean student, AssignmentFileBean assignmentFile) {
		
		if(!"PROD".equalsIgnoreCase(ENVIRONMENT)){
			return;
		}
		
		Properties props = new Properties();
		props.put("mail.smtp.auth", "true");
		props.put("mail.smtp.starttls.enable", "true");
		props.put("mail.smtp.host", host);
		props.put("mail.smtp.port", port);

		Session session = Session.getInstance(props,
				new javax.mail.Authenticator() {
			protected PasswordAuthentication getPasswordAuthentication() {
				return new PasswordAuthentication(username, password);
			}
		});

		try {
			SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			Date date = new Date(); 
			Message message = new MimeMessage(session);
			message.setFrom(new InternetAddress(from, "NMIMS Global Access SCE"));
			try {
				message.setRecipients(Message.RecipientType.TO,InternetAddress.parse(student.getEmailId()));
				message.setSubject("Assignment Received for "+assignmentFile.getSubject() + " - "+assignmentFile.getMonth()+"-"+assignmentFile.getYear() + " Examination");
			} catch (Exception e) {
				message.setRecipients(Message.RecipientType.CC,InternetAddress.parse("sanketpanaskar@gmail.com"));
				message.setSubject("Error: Student Email Not Available : Assignment Received for "+assignmentFile.getSubject());
			}
			//If student replies it should go to Exam Email ID
			message.setReplyTo(InternetAddress.parse("ngasce.exams@nmims.edu"));
			
			String body = "";
			
			assignmentFile.setLastModifiedDate(formatter.format(date)); // by-Prahsant - set sysdate due to commented dao call
			
			body = "Dear Student, <br><br>"
					+"Your assignment for the following subject was successfully submitted. <br><br>"
					+"Student Name: "+student.getFirstName().toUpperCase() + " " + student.getLastName().toUpperCase()  + " <br>"
					+"SAP ID: "+student.getSapid() + "<br>"
					+"Program: "+student.getProgram() + "<br>"
					+"Assignment Applicable: "+assignmentFile.getMonth()+"-"+assignmentFile.getYear() + " Examination <br>"
					+"Subject: "+assignmentFile.getSubject()+ "<br>"
					+"Date-Time of submission: "+formatter.format(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(assignmentFile.getLastModifiedDate()))+ "<br>"
					+"Attempt No.: "+assignmentFile.getAttempts()+ "<br><br>";
			
			body = body  

					+"Last Date of Internal Assignment Submission for "+assignmentFile.getMonth()+"-"+assignmentFile.getYear()
					+" Examination is "+formatter.format(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(assignmentFile.getEndDate())) + "<br><br>"
					+"<b>Thank you for the submission.</b><br><br>"
					+"Regards,<br>"
					+"NGA-SCE<br> "


						+ "<hr>"

						+"Please Note:<br>"
						+"<b>The Auto-generated submission mail is only the acknowledgement of file submitted by the student in the system (right/wrong) as the case may be and not confirmation from the NMIMS University certifying it's the right submission. </b><br><br>"
						+"Incase of any doubt or query regarding assignment submission, student can get in touch by email at ngasce.exams@nmims.edu for clarification before last date of assignment submission. No last minute assignment query/request will be accepted.<br>"
						+ "<hr>";


			message.setContent(body, "text/html; charset=utf-8");

			Transport.send(message);
			
			loggerForEmailCount.info("Total 1 Mail sent via(SMTP) from "+from+", Email Subject : Assignment Received ..., for sendAssignmentReceivedEmail");
			loggerForEmailCount.info("Successfully Receiving Assignment Submission mail details  : Sapid = " +student.getSapid() +
					" - Month = " + assignmentFile.getMonth()+
					" - Year = " + assignmentFile.getYear()+
					" - Subject = " + assignmentFile.getSubject()+
					" - Attempt = " + assignmentFile.getAttempts());

		} catch(Exception e) {
			
			loggerForEmailCount.error("Error Receiving mail while assignment submission  : Sapid = " +student.getSapid() +
					" - Month = " + assignmentFile.getMonth()+
					" - Year = " + assignmentFile.getYear()+
					" - Subject = " + assignmentFile.getSubject()+
					" - Attempt = " + assignmentFile.getAttempts() +
					" - Error = " + e );
			
		}


	}
	
	@Async
	public void sendTestEndedEmail(StudentExamBean student, TestExamBean test,StudentsTestDetailsExamBean studentsTestDetails) {
		
		// uncommemt when testing is over
		  if(!"PROD".equalsIgnoreCase(ENVIRONMENT)){
			return;
		}
		  
		String emailSubject = "Internal Assessemnt Test Completed for "+test.getSubject() + " - "+test.getTestName() + " - "+test.getMonth()+"-"+test.getYear() + " Examination";
		String body = "";
		
		try {
			Properties props = new Properties();
			props.put("mail.smtp.auth", "true");
			props.put("mail.smtp.starttls.enable", "true");
			props.put("mail.smtp.host", host);
			props.put("mail.smtp.port", port);

			Session session = Session.getInstance(props,
					new javax.mail.Authenticator() {
				protected PasswordAuthentication getPasswordAuthentication() {
					return new PasswordAuthentication(username, password);
				}
			});

			try {
				SimpleDateFormat formatter = new SimpleDateFormat("dd-MMM-yyyy HH:mm:ss");

				Message message = new MimeMessage(session);
				message.setFrom(new InternetAddress(from, "NMIMS Global Access SCE"));
				try {
					message.setRecipients(Message.RecipientType.TO,InternetAddress.parse(student.getEmailId()));
					message.setSubject(emailSubject);
				} catch (Exception e) {
					message.setRecipients(Message.RecipientType.CC,InternetAddress.parse("pranit.shirke.ext@nmims.edu"));
					message.setSubject("Error: Student Email Not Available : Test Completed for "+test.getSubject());
					logger.info("\n"+"IN sendTestEndedEmail Invalid Student Email got sapid: "+studentsTestDetails.getSapid()+" testId: "+test.getId()+" Error: "+e.toString());
					logger.info(" "+e);
				}
				//If student replies it should go to Exam Email ID
				message.setReplyTo(InternetAddress.parse("ngasce.exams@nmims.edu"));
				
				body = "Dear Student, <br><br>"
						+"Your internal assessment for the following subject was successfully submitted. <br><br>"
						+"Student Name: "+student.getFirstName().toUpperCase() + " " + student.getLastName().toUpperCase()  + " <br>"
						+"SAP ID: "+student.getSapid() + "<br>"
						+"Program: "+student.getProgram() + "<br>"
						+"Test Applicable For: "+test.getMonth()+"-"+test.getYear() + " Examination <br>"
						+"Subject: "+test.getSubject()+ "<br>"
						+"Test Name: "+test.getTestName()+ "<br>"
						+"Date-Time of submission: "+formatter.format(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(studentsTestDetails.getTestEndedOn()))+ "<br>"
						+"Attempt No.: "+studentsTestDetails.getAttempt()+ " / Max Attempts : "+ test.getMaxAttempt()+"<br><br>";

				body = body  

						+"<b>Thank you for taking the test .</b><br><br>"
						+"Regards,<br>"
						+"NGA-SCE<br> "


							+ "<hr>"

							+"Please Note:<br>"
							+"<b>The Auto-generated submission mail is only the acknowledgement of test was ended . </b><br><br>"
							+"Incase of any doubt or query regarding the same, student can get in touch with support for clarification before last date of taking the test. No last minute query/request will be accepted.<br>"
							+ "<hr>";


				message.setContent(body, "text/html; charset=utf-8");

				Transport.send(message);

				loggerForEmailCount.info("Total 1 Mail sent via(SMTP) from "+from+", Email Subject : "+emailSubject+", for sendTestEndedEmail");
			} catch(Exception e) {
				
				logger.info("\n"+"IN sendTestEndedEmail error mail sending got sapid: "+studentsTestDetails.getSapid()+" testId: "+test.getId()+" Error: "+e.toString());
				StringWriter errors = new StringWriter();
				e.printStackTrace(new PrintWriter(errors));
				String apiCalled = "sendTestEndedEmail() : ";
				String stackTrace = "apiCalled="+ apiCalled + ",data= testId: "+ test.getId()+" sapid: "+studentsTestDetails.getSapid()+ 
						",errors=" + errors.toString();
				
				logger.info(stackTrace);
				
			}

			logger.info("\n"+"IN sendTestEndedEmail Sent Test Email to Sapid: "+studentsTestDetails.getSapid()+" testId: "+test.getId()+" ");
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			
			logger.info("\n"+"IN sendTestEndedEmail error got sapid: "+studentsTestDetails.getSapid()+" testId: "+test.getId()+" Error: "+e.toString());
			logger.info(" "+e);
			
		}
		
		MailBean mail = new MailBean();
		mail.setSubject(emailSubject);
		mail.setBody(body);
		mail.setMailId(student.getEmailId());
		mail.setFromEmailId("donotreply-ngasce@nmims.edu");
		mail.setCreatedBy(student.getSapid());
		
		createRecordInUserMailTableAndMailTable(mail);

	}
	
	
	@Async
	public void sendProjectReceivedEmail(StudentExamBean student, AssignmentFileBean assignmentFile) {
		
		 if(!"PROD".equalsIgnoreCase(ENVIRONMENT)){
			return;
		}
		
		Properties props = new Properties();
		props.put("mail.smtp.auth", "true");
		props.put("mail.smtp.starttls.enable", "true");
		props.put("mail.smtp.host", host);
		props.put("mail.smtp.port", port);

		Session session = Session.getInstance(props,
				new javax.mail.Authenticator() {
			protected PasswordAuthentication getPasswordAuthentication() {
				return new PasswordAuthentication(username, password);
			}
		});

		try {
			SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			Date date = new Date();
			Message message = new MimeMessage(session);
			message.setFrom(new InternetAddress(from, "NMIMS Global Access SCE"));
			try {
				message.setRecipients(Message.RecipientType.TO,InternetAddress.parse(student.getEmailId()));
				message.setSubject("Project Submission Received for "+assignmentFile.getMonth()+"-"+assignmentFile.getYear() + " Examination");
			} catch (Exception e) {
				message.setRecipients(Message.RecipientType.CC,InternetAddress.parse("sanketpanaskar@gmail.com"));
				message.setSubject("Error: Student Email Not Available : Project Received for "+assignmentFile.getSubject());
			}
			//If student replies it should go to Exam Email ID
			message.setReplyTo(InternetAddress.parse("ngasce.exams@nmims.edu"));
			
			String body = "";
			assignmentFile.setLastModifiedDate(formatter.format(date)); // by-Prahsant - set sysdate due to commented dao call
			
			body = "Dear Student, <br><br>"
					+"Your Project was successfully submitted. <br><br>"
					+"Student Name: "+student.getFirstName().toUpperCase() + " " + student.getLastName().toUpperCase()  + " <br>"
					+"SAP ID: "+student.getSapid() + "<br>"
					+"Program: "+student.getProgram() + "<br>"
					+"Project Applicable: "+assignmentFile.getMonth()+"-"+assignmentFile.getYear() + " Examination <br>"
					+"Date-Time of submission: "+formatter.format(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(assignmentFile.getLastModifiedDate()))+ "<br>"
					+"Attempt No.: "+assignmentFile.getAttempts()+ "<br><br>";

			body = body  

					+"Last Date of Project Submission for "+assignmentFile.getMonth()+"-"+assignmentFile.getYear()
					+" Examination is "+assignmentFile.getEndDate().replaceAll("T", " ").substring(0,10) + " on or before 23.59hrs (IST). <br><br>"
					+"<b>Thank you for the submission.</b><br><br>"
					+"Regards,<br>"
					+"NGA-SCE<br> "


						+ "<hr>"

						+"Please Note:<br>"
						+"<b>The Auto-generated submission mail is only the acknowledgement of file submitted by the student in the system (right/wrong) as the case may be and not confirmation from the NMIMS University certifying it is the right submission. </b><br><br>"
						+"Incase of any doubt or query regarding Project submission, student can get in touch by email at ngasce.exams@nmims.edu for clarification before last date of Project submission. No last minute Project query/request will be accepted.<br>"
						+ "<hr>";

			message.setContent(body, "text/html; charset=utf-8");

			Transport.send(message);
			loggerForEmailCount.info("Total 1 Mail sent via(SMTP) from "+from+", Email Subject : Project Submission Received , for sendProjectReceivedEmail");
			projectSubmissionLogger.info("Successfully Receiving project submission mail details  : Sapid = " +student.getSapid() +
					" - Month = " + assignmentFile.getMonth()+
					" - Year = " + assignmentFile.getYear()+
					" - Subject = " + assignmentFile.getSubject()+
					" - Attempt = " + assignmentFile.getAttempts()+
					"Success");

			
		} catch(Exception e) {
			projectSubmissionLogger.error("Error Receiving Mail while Project Submission  : Sapid = " +student.getSapid() +
					" - Month = " + assignmentFile.getMonth()+
					" - Year = " + assignmentFile.getYear()+
					" - Subject = " + assignmentFile.getSubject()+
					" - Attempt = " + assignmentFile.getAttempts() +
					" - Error = " + e );
		}


	}
	
	@Async
	public void sendPhotoNotFoundEmail(StudentExamBean student) {
		
		if(!"PROD".equalsIgnoreCase(ENVIRONMENT)){
			return;
		}
		
		Properties props = new Properties();
		props.put("mail.smtp.auth", "true");
		props.put("mail.smtp.starttls.enable", "true");
		props.put("mail.smtp.host", host);
		props.put("mail.smtp.port", port);


		Session session = Session.getInstance(props,
				new javax.mail.Authenticator() {
			protected PasswordAuthentication getPasswordAuthentication() {
				return new PasswordAuthentication(username, password);
			}
		});

		try {

			Message message = new MimeMessage(session);
			message.setFrom(new InternetAddress(from, "NMIMS Global Access SCE"));
			message.addRecipient(Message.RecipientType.TO, new InternetAddress("sanketpanaskar@gmail.com"));
			message.addRecipient(Message.RecipientType.TO, new InternetAddress("Rashmi.Khedkar@nmims.edu"));
			message.addRecipient(Message.RecipientType.TO, new InternetAddress("jforce.solution@gmail.com"));
			message.addRecipient(Message.RecipientType.TO, new InternetAddress("pranit.shirke.ext@nmims.edu"));
			
			message.setSubject("Student Photo not Found");

			String body = "";

			body = "Dear Team, <br>"
					+"Student Photo for " + student.getSapid() + " is not found on server. <br>"
					+"Please include it <br>"
					+ " Student ID: "+student.getSapid()
					+ "<br>";


			body = body + "<br>Thanks & Regards"
					+ "<br>"
					+ "NMIMS GLOBAL ACCESS SCHOOL FOR CONTINUING EDUCATION";

			message.setContent(body, "text/html; charset=utf-8");
			Transport.send(message);
			
			loggerForEmailCount.info("Total 1 Mail sent via(SMTP) from "+from+", Email Subject : Student Photo not Found, for sendPhotoNotFoundEmail");

		} catch(Exception e) {
			
		}



	}
	public void sendAssignmentBookingSummaryEmail(StudentExamBean student, ArrayList<String> subjects, String examPeriod) {
		
		if(!"PROD".equalsIgnoreCase(ENVIRONMENT)){
			return;
		}
		
		Properties props = new Properties();
		props.put("mail.smtp.auth", "true");
		props.put("mail.smtp.starttls.enable", "true");
		props.put("mail.smtp.host", host);
		props.put("mail.smtp.port", port);

		Session session = Session.getInstance(props,
				new javax.mail.Authenticator() {
			protected PasswordAuthentication getPasswordAuthentication() {
				return new PasswordAuthentication(username, password);
			}
		});

		
		try {
			SimpleDateFormat formatter = new SimpleDateFormat("dd-MMM-yyyy HH:mm:ss");

			Message message = new MimeMessage(session);
			message.setFrom(new InternetAddress(from, "NMIMS Global Access SCE"));
			try {
				message.setRecipients(Message.RecipientType.TO,InternetAddress.parse(student.getEmailId()));
				message.setSubject("Assignment Payment Received");
			} catch (Exception e) {
				message.setRecipients(Message.RecipientType.CC,InternetAddress.parse("sanketpanaskar@gmail.com"));
				message.setSubject("Error: Student Email Not Available : Assignment Booking Received for "+student.getSapid());
			}
			//If student replies it should go to Exam Email ID
			message.setReplyTo(InternetAddress.parse("ngasce.exams@nmims.edu"));
			
			String body = "";

			body = "Dear Student, <br><br>"
					+"Your assignment fees for the following subject has been received ";
					if(examPeriod != null){
						body += "towards " +examPeriod+ "  Re-Sit Term End Examination";
					}
			body += " <br><br>"
					+"Student Name: "+student.getFirstName().toUpperCase() + " " + student.getLastName().toUpperCase()  + " <br>"
					+"SAP ID: "+student.getSapid() + "<br>"
					+"Program: "+student.getProgram() + "<br>";
					
					body += "<ul>";
					for (String subject : subjects) {
						body += "<li>" + subject + "</li>";
					}
					body += "</ul>";

			body = body  
					+"<br/><br/>"
					+"Regards,<br>"
					+"NGA-SCE<br> ";


			message.setContent(body, "text/html; charset=utf-8");

			Transport.send(message);

			loggerForEmailCount.info("Total 1 Mail sent via(SMTP) from "+from+", Email Subject : Assignment Payment Received, for sendAssignmentBookingSummaryEmail");
		} catch(Exception e) {
			

		}

		
	}
	@Async
	public void sendProgramCompletedEmail(String subject,String emailBody,List<String> toEmailIds ) {
		
		if(!"PROD".equalsIgnoreCase(ENVIRONMENT)){
			return;
		}
		
		Properties props = new Properties();
		props.put("mail.smtp.auth", "true");
		props.put("mail.smtp.starttls.enable", "true");
		props.put("mail.smtp.host", host);
		props.put("mail.smtp.port", port);
		
		javax.mail.Address address = null;
		String emailsNotSentId = "";
		int successCount = toEmailIds.size();
		int errorCount = 0;

			
		Session session = Session.getInstance(props,
				new javax.mail.Authenticator() {
			protected PasswordAuthentication getPasswordAuthentication() {
				return new PasswordAuthentication(username, password);
			}
		});

		try {

			Message message = new MimeMessage(session);
			message.setFrom(new InternetAddress(from, "NMIMS Global Access SCE"));
			for (int i = 0; i < successCount; i++) 
			{ 
				try {
					address = new javax.mail.internet.InternetAddress(toEmailIds.get(i)); 
					message.addRecipient(Message.RecipientType.TO, address); 
				}catch (Exception e) {
					errorCount++;
					emailsNotSentId = emailsNotSentId +"," + toEmailIds.get(i);
				}
			}
			
			message.addRecipients(Message.RecipientType.CC, 
                    InternetAddress.parse("sanketpanaskar@gmail.com,Sneha.Utekar@nmims.edu ,nelson.soans@nmims.edu"));
			
			String body = "";
			
			message.setSubject(subject);

				body = "Dear All, <br><br>"
						+ emailBody
						+"<br><br>"
						+"Thanks & Regards"
						+"<br>"
						+"NMIMS GLOBAL ACCESS SCHOOL FOR CONTINUING EDUCATION";

				
			message.setContent(body, "text/html; charset=utf-8");

			Transport.send(message);
			loggerForEmailCount.info("Total 1 Mail sent via(SMTP) from "+from+", Email Subject : "+subject+", for sendProgramCompletedEmail");

			// sending  EmailId to which email is not send
			if(!"".equals(emailsNotSentId)){
				 
				Message messages = new javax.mail.internet.MimeMessage(session); 
				try {
					messages.setFrom(new InternetAddress(from, "NMIMS Global Access SCE"));
				} catch (UnsupportedEncodingException e) {
				}
				//message.addRecipients(javax.mail.Message.RecipientType.TO, InternetAddress.parse("sanketpanaskar@gmail.com, sneha.utekar@nmims.edu, bageshree@nmims.edu, Sheetal.Gupta@nmims.edu"));
				messages.addRecipients(javax.mail.Message.RecipientType.TO, InternetAddress.parse("sanketpanaskar@gmail.com, jforcesolutions@gmail.com"));
				messages.setSubject("Emails Errors for "+subject); 
				messages.setText(emailsNotSentId); 
				Transport.send(messages);
				loggerForEmailCount.info("Total 1 Mail sent via(SMTP) from "+from+", Email Subject : Emails Errors for "+subject+", for sendProgramCompletedEmail");
			}
			

		} catch(Exception e) {
			
		}



	}
	
//Added for checking double booking cases for subject as well as slots
  	/*@Async
	public void sendTwiceBookedEmail(ArrayList<ExamBookingTransactionBean> successfulButSameSlotTwiceExamBookings,
			ArrayList<ExamBookingTransactionBean> successfulButSameSubjectTwiceExamBookings) {
		
  		if(!"PROD".equalsIgnoreCase(ENVIRONMENT)){
			return;
		}
  		
  		Properties props = new Properties();
		props.put("mail.smtp.auth", "true");
		props.put("mail.smtp.starttls.enable", "true");
		props.put("mail.smtp.host", host);
		props.put("mail.smtp.port", port);


		Session session = Session.getInstance(props,
				new javax.mail.Authenticator() {
			protected PasswordAuthentication getPasswordAuthentication() {
				return new PasswordAuthentication(username, password);
			}
		});

		try {

			Message message = new MimeMessage(session);
			message.setFrom(new InternetAddress(from, "NMIMS Global Access SCE"));
			message.setRecipients(Message.RecipientType.TO,InternetAddress.parse("sanketpanaskar@gmail.com, jigna.patel@nmims.edu, Archana.Doifode@nmims.edu, pranit.shirke.ext@nmims.edu, jforce.solution@gmail.com,sangeeta.shetty@nmims.edu, jforcesolutions@gmail.com, nashrah.shaikh@nmims.edu, laxmi.raaj@nmims.edu"));
			message.setSubject("Double Booking");

			String body = "";

			body = "Dear Team, <br>"
					+"Below is the list of Double Bookings as of "+new java.util.Date().toString()+" <br> <br>";
			int count = 1;
			DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss"); 
			double totalDifference = 0;

			if(successfulButSameSlotTwiceExamBookings != null && successfulButSameSlotTwiceExamBookings.size() > 0){
				body = body  +"List of Twice Booked Slots by Student. <br>";
				body = body  
						+ "<table border=\"1\">"
						+ "<thead>"
						+ "<tr> "
						+ "<th>Sr. No.</th>"
						+ "<th>Student ID</th>"
						+ "<th>Exam Time</th>"
						+ "<th>Exam Date</th>"
						+ "<th>Amount</th>"
						+ "<th>Amount at HDFC</th>"
						+ "<th>Name</th>"
						+ "<th>Email</th>"
						+ "<th>Mobile</th>"
						+ "<th>Alt. Phone</th>"
						+ "<th>Transaction Initiation Time</th>"
						+ "<th>Transaction Completion Time</th>"
						+ "<th>Time Difference</th>"
						+ "</tr>"
						+ "</thead>"
						+ "<tbody>";



				for(int i = 0; i < successfulButSameSlotTwiceExamBookings.size(); i++){

					ExamBookingTransactionBean bean = successfulButSameSlotTwiceExamBookings.get(i);
					String sapid = bean.getSapid();
					String examTime = bean.getExamTime();
					String examDate = bean.getExamDate();
					String amount = bean.getAmount();
					String name = bean.getFirstName() + " "+ bean.getLastName();
					String email = bean.getEmailId();
					String mobile = bean.getMobile();
					String altPhone = bean.getAltPhone();
					String respAmount = bean.getRespAmount();

					String tranTime = bean.getTranDateTime();
					String respTranTime = bean.getRespTranDateTime();
					Date startTime = df.parse(tranTime.substring(0, 19));
					Date endTime = df.parse(respTranTime);

					long diff = endTime.getTime() - startTime.getTime();
					long diffSeconds = diff / 1000;         
					long diffMinutes = diff / (60 * 1000);  
					diffSeconds = diffSeconds - (diffMinutes * 60);
					String timeDiff = diffMinutes +":"+diffSeconds;

					totalDifference += Double.parseDouble(amount);

					body = body 

							+ " <tr align=\"center\"> "
							+ " <td> " + (count++) +"</td> "
							+ "	<td> " + sapid + " </td> "
							+ "	<td> " + examTime + " </td> "
							+ "	<td> " + examDate + " </td> "
							+ "	<td> " + amount + "/- </td> "
							+ "	<td> " + respAmount + " </td> "
							+ "	<td> " + name + " </td> "
							+ "	<td> " + email + " </td> "
							+ "	<td> " + mobile + " </td> "
							+ "	<td> " + altPhone + " </td> "
							+ "	<td> " + tranTime + " </td> "
							+ "	<td> " + respTranTime + " </td> "
							+ "	<td> " + timeDiff + " </td> "
							+ " </tr>";

				} 


				body = body + "</tbody> </table>";	
			}

			if(successfulButSameSubjectTwiceExamBookings != null && successfulButSameSubjectTwiceExamBookings.size() > 0){
				body = body  +"List of Subjects Booked Twice by Student.<br>";
				body = body  
						+ "<table border=\"1\">"
						+ "<thead>"
						+ "<tr> "
						+ "<th>Sr. No.</th>"
						+ "<th>Student ID</th>"
						+ "<th>Subject</th>"
						+ "<th>Amount</th>"
						+ "<th>Amount at HDFC</th>"
						+ "<th>Name</th>"
						+ "<th>Email</th>"
						+ "<th>Mobile</th>"
						+ "<th>Alt. Phone</th>"
						+ "<th>Transaction Initiation Time</th>"
						+ "<th>Transaction Completion Time</th>"
						+ "<th>Time Difference</th>"
						+ "</tr>"
						+ "</thead>"
						+ "<tbody>";


				count = 1;

				for(int i = 0; i < successfulButSameSubjectTwiceExamBookings.size(); i++){

					ExamBookingTransactionBean bean = successfulButSameSubjectTwiceExamBookings.get(i);
					String sapid = bean.getSapid();
					String subject = bean.getSubject();
					String amount = bean.getAmount();
					String name = bean.getFirstName() + " "+ bean.getLastName();
					String email = bean.getEmailId();
					String mobile = bean.getMobile();
					String altPhone = bean.getAltPhone();
					String respAmount = bean.getRespAmount();

					String tranTime = bean.getTranDateTime();
					String respTranTime = bean.getRespTranDateTime();
					Date startTime = df.parse(tranTime.substring(0, 19));
					Date endTime = df.parse(respTranTime);

					long diff = endTime.getTime() - startTime.getTime();
					long diffSeconds = diff / 1000;         
					long diffMinutes = diff / (60 * 1000);  
					diffSeconds = diffSeconds - (diffMinutes * 60);
					String timeDiff = diffMinutes +":"+diffSeconds;

					totalDifference += Double.parseDouble(amount);

					body = body 

							+ " <tr align=\"center\"> "
							+ " <td> " + (count++) +"</td> "
							+ "	<td> " + sapid + " </td> "
							+ "	<td> " + subject + " </td> "
							+ "	<td> " + amount + "/- </td> "
							+ "	<td> " + respAmount + " </td> "
							+ "	<td> " + name + " </td> "
							+ "	<td> " + email + " </td> "
							+ "	<td> " + mobile + " </td> "
							+ "	<td> " + altPhone + " </td> "
							+ "	<td> " + tranTime + " </td> "
							+ "	<td> " + respTranTime + " </td> "
							+ "	<td> " + timeDiff + " </td> "
							+ " </tr>";

				} 

				body = body + "</tbody> </table>";	
			}


			//body = body + "<br>Total Amount: "+totalDifference +"<br>";	

			body = body + "<br>Thanks & Regards"
					+ "<br>"
					+ "NMIMS GLOBAL ACCESS SCHOOL FOR CONTINUING EDUCATION";


			message.setContent(body, "text/html; charset=utf-8");
			Transport.send(message);


		} catch(Exception e) {
			
		}



	}*/
	
	@Async
	public void sendTwiceBookedEmail(ArrayList<ExamBookingTransactionBean> doubleBookingList) {
		
  		if(!"PROD".equalsIgnoreCase(ENVIRONMENT)){
			return;
		}
  		
  		Properties props = new Properties();
		props.put("mail.smtp.auth", "true");
		props.put("mail.smtp.starttls.enable", "true");
		props.put("mail.smtp.host", host);
		props.put("mail.smtp.port", port);


		Session session = Session.getInstance(props,
				new javax.mail.Authenticator() {
			protected PasswordAuthentication getPasswordAuthentication() {
				return new PasswordAuthentication(username, password);
			}
		});

		try {

			Message message = new MimeMessage(session);
			message.setFrom(new InternetAddress(from, "NMIMS Global Access SCE"));
			message.setRecipients(Message.RecipientType.TO,InternetAddress.parse("sanketpanaskar@gmail.com, jigna.patel@nmims.edu, harshalee.ullal@nmims.edu, pooja.jadhav@nmims.edu, christopher.kevin@nmims.edu, jforce.solution@gmail.com,sangeeta.shetty@nmims.edu, jforcesolutions@gmail.com, nashrah.shaikh@nmims.edu, laxmi.raaj@nmims.edu"));
			//message.setRecipients(Message.RecipientType.TO,InternetAddress.parse("jforce.solution@gmail.com"));

			message.setSubject("Double Booking");

			String body = "";

			body = "Dear Team, <br>"
					+"Below is the list of Double Bookings as of "+new java.util.Date().toString()+" <br> <br>";
			int count = 1;
			DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss"); 
			double totalDifference = 0;

			if(doubleBookingList != null && doubleBookingList.size() > 0){
				body = body  +"List of Twice Booked Slots by Student. <br>";
				body = body  
						+ "<table border=\"1\">"
						+ "<thead>"
						+ "<tr> "
						+ "<th>Sr. No.</th>"
						+ "<th>Student ID</th>"
						+ "<th>Subject</th>"
						+ "<th>Exam Time</th>"
						+ "<th>Exam Date</th>"
						+ "<th>Amount</th>"
						+ "<th>Amount at HDFC</th>"
						+ "<th>Name</th>"
						+ "<th>Email</th>"
						+ "<th>Mobile</th>"
						+ "<th>Alt. Phone</th>"
						+ "<th>Transaction Initiation Time</th>"
						+ "<th>Transaction Completion Time</th>"
						+ "<th>Time Difference</th>"
						+ "</tr>"
						+ "</thead>"
						+ "<tbody>";



				for(int i = 0; i < doubleBookingList.size(); i++){

					ExamBookingTransactionBean bean = doubleBookingList.get(i);
					String sapid = bean.getSapid();
					String subject = bean.getSubject();
					String examTime = bean.getExamTime();
					String examDate = bean.getExamDate();
					String amount = bean.getAmount();
					String name = bean.getFirstName() + " "+ bean.getLastName();
					String email = bean.getEmailId();
					String mobile = bean.getMobile();
					String altPhone = bean.getAltPhone();
					String respAmount = bean.getRespAmount();

					String tranTime = bean.getTranDateTime();
					String respTranTime = bean.getRespTranDateTime();
					Date startTime = df.parse(tranTime.substring(0, 19));
					Date endTime = df.parse(respTranTime);

					long diff = endTime.getTime() - startTime.getTime();
					long diffSeconds = diff / 1000;         
					long diffMinutes = diff / (60 * 1000);  
					diffSeconds = diffSeconds - (diffMinutes * 60);
					String timeDiff = diffMinutes +":"+diffSeconds;

					totalDifference += Double.parseDouble(amount);

					body = body 

							+ " <tr align=\"center\"> "
							+ " <td> " + (count++) +"</td> "
							+ "	<td> " + sapid + " </td> "
							+ "	<td> " + subject + " </td> "
							+ "	<td> " + examTime + " </td> "
							+ "	<td> " + examDate + " </td> "
							+ "	<td> " + amount + "/- </td> "
							+ "	<td> " + respAmount + " </td> "
							+ "	<td> " + name + " </td> "
							+ "	<td> " + email + " </td> "
							+ "	<td> " + mobile + " </td> "
							+ "	<td> " + altPhone + " </td> "
							+ "	<td> " + tranTime + " </td> "
							+ "	<td> " + respTranTime + " </td> "
							+ "	<td> " + timeDiff + " </td> "
							+ " </tr>";

				} 


				body = body + "</tbody> </table>";	
			}


			body = body + "<br>Thanks & Regards"
					+ "<br>"
					+ "NMIMS GLOBAL ACCESS SCHOOL FOR CONTINUING EDUCATION";


			message.setContent(body, "text/html; charset=utf-8");
			Transport.send(message);

			loggerForEmailCount.info("Total 1 Mail sent via(SMTP) from "+from+", Email Subject : Double Booking, for sendTwiceBookedEmail");

		} catch(Exception e) {
			
		}



	}
	//end
  	
  	
  	@Async
	public void sendProjectBookingSummaryEmail(HttpServletRequest request, ExamBookingDAO dao , ProjectSubmissionDAO projectSubmissionDAO, AssignmentFileBean assignmentFile) {
  		projectPaymentsLogger.info("MailSender.sendProjectBookingSummaryEmail() - START");
  		if(!"PROD".equalsIgnoreCase(ENVIRONMENT)){
			return;
		}
  		Properties props = new Properties();
		props.put("mail.smtp.auth", "true");
		props.put("mail.smtp.starttls.enable", "true");
		props.put("mail.smtp.host", host);
		props.put("mail.smtp.port", port);

		StudentExamBean student = (StudentExamBean)request.getSession().getAttribute("studentExam");
		
		List<ExamBookingTransactionBean> examBookings = new ArrayList<ExamBookingTransactionBean>();
//		examBookings = dao.getConfirmedProjectBooking(student.getSapid()); // Commented to make two cycle live
		String method = "sendProjectBookingSummaryEmail()";
		AssignmentFileBean examMonthYearBean = eligibilityService.getStudentApplicableExamMonthYear(student.getSapid(), assignmentFile.getSubject(), method);
		examBookings = eligibilityService.getConfirmedProjectBookingApplicableCycle(student.getSapid(), examMonthYearBean.getMonth(), examMonthYearBean.getYear());
		AssignmentFileBean projectFile = new AssignmentFileBean();
//		projectFile = projectSubmissionDAO.findById(assignmentFile); // Commented to make two cycle live
		projectFile.setConsumerProgramStructureId(student.getConsumerProgramStructureId());
		projectFile.setMonth(examMonthYearBean.getMonth());
		projectFile.setYear(examMonthYearBean.getYear());
		projectFile = projectSubmissionDAO.findProjectGuidelinesForApplicableCycle(projectFile);
		
		String year = examBookings.get(0).getYear();
		String month = examBookings.get(0).getMonth();
		String endDate = projectFile.getEndDate();
		endDate = endDate.replaceAll("T", " ");
		projectFile.setEndDate(endDate.substring(0,10));
		
		Session session = Session.getInstance(props,
				new javax.mail.Authenticator() {
			protected PasswordAuthentication getPasswordAuthentication() {
				return new PasswordAuthentication(username, password);
			}
		});

		try {

			Message message = new MimeMessage(session);
			message.setFrom(new InternetAddress(from, "NMIMS Global Access SCE"));
			message.setRecipients(Message.RecipientType.TO,InternetAddress.parse(student.getEmailId()));
			message.setSubject("Your Project Registration Summary");

			String body = "";

			body = "Dear " + student.getFirstName() +", <br>"
					+"Here is your Project Registration Summary for " + month +"-"+ year +" exam. <br>"
					+ " Student ID: "+student.getSapid() +"<br>"
					+ " Program: "+student.getProgram()+"<br>"
					+ "<br>";

			body = body  
					+ "<table border=\"1\">"
					+ "<thead>"
					+ "<tr> "
					+ "<th>Sr. No.</th>"
					+ "<th>Subject</th>"
					+ "<th>Sem</th>"
					+ "<th>Exam Year</th>"
					+ "<th>Exam Month</th>"
					+ "<th>Transaction Status</th>"
					+ "<th>Booking Status</th>"
					+ "</tr>"
					+ "</thead>"
					+ "<tbody>";

			int count = 1;
			for(int i = 0; i < examBookings.size(); i++){
				ExamBookingTransactionBean bean = (ExamBookingTransactionBean)examBookings.get(i);

				String booked = bean.getBooked();
				String bookingStatus = null;
				if("Y".equals(booked)){
					bookingStatus = "Booked";
				}else{
					bookingStatus = "Not Booked";
				}

				String subject = bean.getSubject();

				body = body 

						+ " <tr> "
						+ "    <td align=\"center\"> " + (count++) +"</td> "
						+ "	<td> " + subject + " </td> "
						+ "	<td align=\"center\">" + bean.getSem()+"</td> ";

				if("Project".equals(subject) || "Module 4 - Project".equals(subject)){ 
					body = body 
							+ "<td>"+bean.getYear()+"</td>"
							+ "<td>"+bean.getMonth()+"</td>"
							+ "<td>"+ bean.getTranStatus() +"</td>"
							+ "<td align=\"center\">" +bookingStatus+ "</td>";
					
				}
				body = body + 	"</tr> ";								
			} 

			body = body + 	"</tbody> </table>";	 	

			body = body + "<br><b>Note:</b><br>"
					+ "1. Project submission last date for "+month+" "+year+" is "+projectFile.getEndDate()+" on or before 23.59hrs (IST).<br>"
					+ "2. Exam fees once paid will not be refunded nor carry forwarded to next exam cycle. <br>";
			
			body = body + "<br>Thanks & Regards"
					+ "<br>"
					+ "NMIMS GLOBAL ACCESS SCHOOL FOR CONTINUING EDUCATION";

			message.setContent(body, "text/html; charset=utf-8");
			Transport.send(message);
			
			loggerForEmailCount.info("Total 1 Mail sent via(SMTP) from "+from+", Email Subject : Your Project Registration Summary, for sendProjectBookingSummaryEmail");
		} catch(Exception e) {
			projectPaymentsLogger.error("Error while sending mail to:"+student.getEmailId()+" and Error:"+e);
		}
		projectPaymentsLogger.info("MailSender.sendProjectBookingSummaryEmail() - END");
	}
  	
  	@Async
	public void sendProjectBookingSummaryEmailFromGateway(StudentExamBean student, ExamBookingDAO dao , ProjectSubmissionDAO projectSubmissionDAO, AssignmentFileBean assignmentFile) {
  		projectPaymentsLogger.info("MailSender.sendProjectBookingSummaryEmail() - START");
  		if(!"PROD".equalsIgnoreCase(ENVIRONMENT)){
			return;
		}
  		Properties props = new Properties();
		props.put("mail.smtp.auth", "true");
		props.put("mail.smtp.starttls.enable", "true");
		props.put("mail.smtp.host", host);
		props.put("mail.smtp.port", port);

		List<ExamBookingTransactionBean> examBookings = new ArrayList<ExamBookingTransactionBean>();
//		examBookings = dao.getConfirmedProjectBooking(student.getSapid()); // Commented to make two cycle live
		String method = "sendProjectBookingSummaryEmailFromGateway()";
		AssignmentFileBean examMonthYearBean = eligibilityService.getStudentApplicableExamMonthYear(student.getSapid(), assignmentFile.getSubject(), method);
		examBookings = eligibilityService.getConfirmedProjectBookingApplicableCycle(student.getSapid(), examMonthYearBean.getMonth(), examMonthYearBean.getYear());
		
		//added because assignemnt file doesnt have this data
		assignmentFile.setConsumerProgramStructureId(student.getConsumerProgramStructureId());
		
		AssignmentFileBean projectFile = new AssignmentFileBean();
//		projectFile = projectSubmissionDAO.findById(assignmentFile); // Commented to make two cycle live
		projectFile.setConsumerProgramStructureId(student.getConsumerProgramStructureId());
		projectFile.setMonth(examMonthYearBean.getMonth());
		projectFile.setYear(examMonthYearBean.getYear());
		projectFile = projectSubmissionDAO.findProjectGuidelinesForApplicableCycle(projectFile);
		String year = examBookings.get(0).getYear();
		String month = examBookings.get(0).getMonth();
		String endDate = projectFile.getEndDate();
		endDate = endDate.replaceAll("T", " ");
		projectFile.setEndDate(endDate.substring(0,10));
		
		Session session = Session.getInstance(props,
				new javax.mail.Authenticator() {
			protected PasswordAuthentication getPasswordAuthentication() {
				return new PasswordAuthentication(username, password);
			}
		});

		try {

			Message message = new MimeMessage(session);
			message.setFrom(new InternetAddress(from, "NMIMS Global Access SCE"));
			message.setRecipients(Message.RecipientType.TO,InternetAddress.parse(student.getEmailId()));
			message.setSubject("Your Project Registration Summary");

			String body = "";

			body = "Dear " + student.getFirstName() +", <br>"
					+"Here is your Project Registration Summary for " + month +"-"+ year +" exam. <br>"
					+ " Student ID: "+student.getSapid() +"<br>"
					+ " Program: "+student.getProgram()+"<br>"
					+ "<br>";

			body = body  
					+ "<table border=\"1\">"
					+ "<thead>"
					+ "<tr> "
					+ "<th>Sr. No.</th>"
					+ "<th>Subject</th>"
					+ "<th>Sem</th>"
					+ "<th>Exam Year</th>"
					+ "<th>Exam Month</th>"
					+ "<th>Transaction Status</th>"
					+ "<th>Booking Status</th>"
					+ "</tr>"
					+ "</thead>"
					+ "<tbody>";

			int count = 1;
			for(int i = 0; i < examBookings.size(); i++){
				ExamBookingTransactionBean bean = (ExamBookingTransactionBean)examBookings.get(i);

				String booked = bean.getBooked();
				String bookingStatus = null;
				if("Y".equals(booked)){
					bookingStatus = "Booked";
				}else{
					bookingStatus = "Not Booked";
				}

				String subject = bean.getSubject();

				body = body 

						+ " <tr> "
						+ "    <td align=\"center\"> " + (count++) +"</td> "
						+ "	<td> " + subject + " </td> "
						+ "	<td align=\"center\">" + bean.getSem()+"</td> ";

				if("Project".equals(subject) || "Module 4 - Project".equals(subject)){ 
					body = body 
							+ "<td>"+bean.getYear()+"</td>"
							+ "<td>"+bean.getMonth()+"</td>"
							+ "<td>"+ bean.getTranStatus() +"</td>"
							+ "<td align=\"center\">" +bookingStatus+ "</td>";
					
				}
				body = body + 	"</tr> ";								
			} 

			body = body + 	"</tbody> </table>";	 	

			body = body + "<br><b>Note:</b><br>"
					+ "1. Project submission last date for "+month+" "+year+" is "+projectFile.getEndDate()+" on or before 23.59hrs (IST).<br>"
					+ "2. Exam fees once paid will not be refunded nor carry forwarded to next exam cycle. <br>";
			
			body = body + "<br>Thanks & Regards"
					+ "<br>"
					+ "NMIMS GLOBAL ACCESS SCHOOL FOR CONTINUING EDUCATION";

			message.setContent(body, "text/html; charset=utf-8");
			Transport.send(message);
			
			loggerForEmailCount.info("Total 1 Mail sent via(SMTP) from "+from+", Email Subject : Your Project Registration Summary, for sendProjectBookingSummaryEmail");
		} catch(Exception e) {
			projectPaymentsLogger.error("Error while sending mail to:"+student.getEmailId()+" and Error:"+e);
		}
		projectPaymentsLogger.info("MailSender.sendProjectBookingSummaryEmail() - END");
	}
  	
  	/*Commented by Stef
  	 * @Async
	public void sendEmailForExamCenterNull (StudentBean student, String subject){
		
		if(!"PROD".equalsIgnoreCase(ENVIRONMENT)){
		return;
		}
		
		Properties props = new Properties();
		props.put("mail.smtp.auth", "true");
		props.put("mail.smtp.starttls.enable", "true");
		props.put("mail.smtp.host", host);
		props.put("mail.smtp.port", port);
	
		Session session = Session.getInstance(props,new javax.mail.Authenticator() {
		protected PasswordAuthentication getPasswordAuthentication() {
			return new PasswordAuthentication(username, password);
		}
		});
	
		try {
			Message message = new MimeMessage(session);
			message.setFrom(new InternetAddress(from, "NMIMS Global Access SCE"));
			message.setRecipients(Message.RecipientType.TO,InternetAddress.parse("jigna.patel@nmims.edu, Archana.Doifode@nmims.edu, jforcesolutions@gmail.com, nashrah.shaikh@nmims.edu, laxmi.raaj@nmims.edu"));
			message.setRecipients(Message.RecipientType.CC, InternetAddress.parse("sanketpanaskar@gmail.com, pranit.shirke.ext@nmims.edu, somesh.turde.ext@nmims.edu"));
			message.setSubject("ExamCenter Blank/Null in Exam Summary");
			
			String body = "";
			
			body = 	"Dear Team, <br> "
					+ "Name : "+student.getFirstName()+" "+student.getLastName()+"<br>"
					+ "SAP ID : "+student.getSapid()+"<br>"
					+ "Subject : "+subject+"<br>"
					+ "Student getting ExamCenter Blank/Null in Exam Summary.<br> "
					+ "Please check and send Exam Summary to student."
					+"<br><br><br>"
					+"Thanks & Regards"
					+"<br>"
					+"NMIMS GLOBAL ACCESS SCHOOL FOR CONTINUING EDUCATION";
			
			message.setContent(body, "text/html; charset=utf-8");
			
			Transport.send(message);
			
		} catch (Exception e) {
			
		}
	}*/
  	
	public void sendEmailForTwicePaymentReceived(StudentExamBean student, ExamBookingTransactionBean examBean, AssignmentsDAO dao){
		
		if(!"PROD".equalsIgnoreCase(ENVIRONMENT)){
			return;
		}
		
		Properties props = new Properties();
		props.put("mail.smtp.auth", "true");
		props.put("mail.smtp.starttls.enable", "true");
		props.put("mail.smtp.host", host);
		props.put("mail.smtp.port", port);
	
		Session session = Session.getInstance(props,
				new javax.mail.Authenticator() {
			protected PasswordAuthentication getPasswordAuthentication() {
				return new PasswordAuthentication(username, password);
			}
		});
		
		try {
			Message message = new MimeMessage(session);
			message.setFrom(new InternetAddress(from, "NMIMS Global Access SCE"));
			message.setRecipients(Message.RecipientType.TO,InternetAddress.parse("ngasce.exams@nmims.edu, jigna.patel@nmims.edu, harshalee.ullal@nmims.edu, pooja.jadhav@nmims.edu, christopher.kevin@nmims.edu, jforcesolutions@gmail.com, nashrah.shaikh@nmims.edu, laxmi.raaj@nmims.edu"));
			message.setRecipients(Message.RecipientType.BCC,InternetAddress.parse("sanketpanaskar@gmail.com, pranit.shirke.ext@nmims.edu, somesh.turde.ext@nmims.edu"));
			message.setSubject("Twice Payment Received for Assignment");
			
			String body = "Dear Team <br><br>"
						+ "Twice payment received from "+examBean.getSapid()+"<br>"
						+ "Student Name : "+student.getFirstName().toUpperCase()+ " " +student.getLastName().toUpperCase()+"<br>"
						+ "Subject : "+examBean.getSubject()+"<br>"
						+ "Booked On : "+examBean.getTranDateTime()+"<br>"
						+ "Please check."
						+ "<br>"
						+ "<br>"
						+ "<br>"
						+ "Thanks & Regards"
						+ "<br>"
						+ "NMIMS GLOBAL ACCESS SCHOOL FOR CONTINUING EDUCATION";
			
			
			message.setContent(body, "text/html; charset=utf-8");
			Transport.send(message);		
			
			loggerForEmailCount.info("Total 1 Mail sent via(SMTP) from "+from+", Email Subject : Twice Payment Received for Assignment, for sendEmailForTwicePaymentReceived");
//			Make entry in database after sending SMS  
			dao.updateSentEmailForTwiceAssignmentFess(examBean);
			
			
			} catch (Exception e) {
				
			}
		}
	
	
	@Async
	public void sendCaseStudyReceivedEmail(StudentExamBean student, CaseStudyExamBean assignmentFile) {
		
		if(!"PROD".equalsIgnoreCase(ENVIRONMENT)){
			return;
		}
		
		Properties props = new Properties();
		props.put("mail.smtp.auth", "true");
		props.put("mail.smtp.starttls.enable", "true");
		props.put("mail.smtp.host", host);
		props.put("mail.smtp.port", port);

		Session session = Session.getInstance(props,
				new javax.mail.Authenticator() {
			protected PasswordAuthentication getPasswordAuthentication() {
				return new PasswordAuthentication(username, password);
			}
		});

		try {
			SimpleDateFormat formatter = new SimpleDateFormat("dd-MMM-yyyy HH:mm:ss");

			Message message = new MimeMessage(session);
			message.setFrom(new InternetAddress(from, "NMIMS Global Access SCE"));
			try {
				message.setRecipients(Message.RecipientType.TO,InternetAddress.parse(student.getEmailId()));
				message.setSubject("Case Study Received for "+assignmentFile.getTopic() + " - "+assignmentFile.getBatchMonth()+"-"+assignmentFile.getBatchYear());
			} catch (Exception e) {
				message.setRecipients(Message.RecipientType.CC,InternetAddress.parse(/*"sanketpanaskar@gmail.com"*/"jforce.solution@gmail.com"));
				message.setSubject("Error: Student Email Not Available : Case Study Received for "+assignmentFile.getTopic());
			}
			//If student replies it should go to Exam Email ID
			message.setReplyTo(InternetAddress.parse("ngasce.exams@nmims.edu"));
			
			String body = "";

			body = "Dear Student, <br><br>"
					+"Your Case Study for the following topic was successfully submitted. <br><br>"
					+"Student Name: "+student.getFirstName().toUpperCase() + " " + student.getLastName().toUpperCase()  + " <br>"
					+"SAP ID: "+student.getSapid() + "<br>"
					+"Program: "+student.getProgram() + "<br>"
					+"Case Study Applicable: "+assignmentFile.getBatchMonth()+"-"+assignmentFile.getBatchYear() + " Examination <br>"
					+"Topic: "+assignmentFile.getTopic()+ "<br>"
					+"Date-Time of submission: "+formatter.format(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(assignmentFile.getLastModifiedDate()))+ "<br>";

			body = body  

					+"Last Date of Case Study Submission for "+assignmentFile.getBatchMonth()+"-"+assignmentFile.getBatchYear()
					+" is "+formatter.format(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(assignmentFile.getEndDate())) + "<br><br>"
					+"<b>Thank you for the submission.</b><br><br>"
					+"Regards,<br>"
					+"NGA-SCE<br> "


						+ "<hr>"

						+"Please Note:<br>"
						+"<b>The Auto-generated submission mail is only the acknowledgement of file submitted by the student in the system (right/wrong) as the case may be and not confirmation from the NMIMS University certifying it is the right submission. </b><br><br>"
						+"Incase of any doubt or query regarding case study submission, student can get in touch by email at ngasce.exams@nmims.edu for clarification before last date of case study submission. No last minute case study query/request will be accepted.<br>"
						+ "<hr>";


			message.setContent(body, "text/html; charset=utf-8");

			Transport.send(message);

			loggerForEmailCount.info("Total 1 Mail sent via(SMTP) from "+from+", Email Subject : Case Study Received, for sendCaseStudyReceivedEmail");
		} catch(Exception e) {
			
		}


	}
  	
	
	@Async
	public void sendProjectConflictsEmail(ArrayList<ExamBookingTransactionBean> successfulButAlreadyBookedExamBookings) {
		if(!"PROD".equalsIgnoreCase(ENVIRONMENT)){
			return;
		}
		
		Properties props = new Properties();
		props.put("mail.smtp.auth", "true");
		props.put("mail.smtp.starttls.enable", "true");
		props.put("mail.smtp.host", host);
		props.put("mail.smtp.port", port);


		Session session = Session.getInstance(props,
				new javax.mail.Authenticator() {
			protected PasswordAuthentication getPasswordAuthentication() {
				return new PasswordAuthentication(username, password);
			}
		});

		try {

			Message message = new MimeMessage(session);
			message.setFrom(new InternetAddress(from, "NMIMS Global Access SCE"));
			message.setRecipients(Message.RecipientType.TO,InternetAddress.parse("sanketpanaskar@gmail.com, jigna.patel@nmims.edu, pranit.shirke.ext@nmims.edu, jforce.solution@gmail.com,sangeeta.shetty@nmims.edu, jforcesolutions@gmail.com, nashrah.shaikh@nmims.edu, harshalee.ullal@nmims.edu, pooja.jadhav@nmims.edu, christopher.kevin@nmims.edu, Ankita.Parmar@nmims.edu, khatija.shaikh@nmims.edu"));
			//message.setRecipients(Message.RecipientType.CC, InternetAddress.parse("rajiv.shah@nmims.edu"));

			//message.setRecipients(Message.RecipientType.TO,InternetAddress.parse("sanketpanaskar@gmail.com"));
			message.setSubject("Transaction Conflicts");

			String body = "";

			body = "Dear Team, <br>"
					+"Below is the list of Transactions Conflicts as of "+new java.util.Date().toString()+" <br> <br>";
			int count = 1;
			DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss"); 
			double totalDifference = 0;


			if(successfulButAlreadyBookedExamBookings != null && successfulButAlreadyBookedExamBookings.size() > 0){
				body = body  +"List of Conflict Transactions not auto-booked since Student has already booked subjects. (Process Refunds for these.)<br>";
				body = body  
						+ "<table border=\"1\">"
						+ "<thead>"
						+ "<tr> "
						+ "<th>Sr. No.</th>"
						+ "<th>Student ID</th>"
						+ "<th>Amount</th>"
						+ "<th>Amount at HDFC</th>"
						+ "<th>Name</th>"
						+ "<th>Email</th>"
						+ "<th>Mobile</th>"
						+ "<th>Alt. Phone</th>"
						+ "<th>Transaction Initiation Time</th>"
						+ "<th>Transaction Completion Time</th>"
						+ "<th>Time Difference</th>"
						+ "</tr>"
						+ "</thead>"
						+ "<tbody>";


				count = 1;

				for(int i = 0; i < successfulButAlreadyBookedExamBookings.size(); i++){

					ExamBookingTransactionBean bean = successfulButAlreadyBookedExamBookings.get(i);
					String sapid = bean.getSapid();
					String amount = bean.getAmount();
					String name = bean.getFirstName() + " "+ bean.getLastName();
					String email = bean.getEmailId();
					String mobile = bean.getMobile();
					String altPhone = bean.getAltPhone();
					String respAmount = bean.getRespAmount();

					String tranTime = bean.getTranDateTime();
					String respTranTime = bean.getRespTranDateTime();
					Date startTime = df.parse(tranTime.substring(0, 19));
					Date endTime = df.parse(respTranTime);

					long diff = endTime.getTime() - startTime.getTime();
					long diffSeconds = diff / 1000;         
					long diffMinutes = diff / (60 * 1000);  
					diffSeconds = diffSeconds - (diffMinutes * 60);
					String timeDiff = diffMinutes +":"+diffSeconds;

					totalDifference += Double.parseDouble(amount);

					body = body 

							+ " <tr align=\"center\"> "
							+ " <td> " + (count++) +"</td> "
							+ "	<td> " + sapid + " </td> "
							+ "	<td> " + amount + "/- </td> "
							+ "	<td> " + respAmount + " </td> "
							+ "	<td> " + name + " </td> "
							+ "	<td> " + email + " </td> "
							+ "	<td> " + mobile + " </td> "
							+ "	<td> " + altPhone + " </td> "
							+ "	<td> " + tranTime + " </td> "
							+ "	<td> " + respTranTime + " </td> "
							+ "	<td> " + timeDiff + " </td> "
							+ " </tr>";

				} 

				body = body + "</tbody> </table>";	
			}


			body = body + "<br>Total Amount: "+totalDifference +"<br>";	

			body = body + "<br>Thanks & Regards"
					+ "<br>"
					+ "NMIMS GLOBAL ACCESS SCHOOL FOR CONTINUING EDUCATION";


			message.setContent(body, "text/html; charset=utf-8");
			Transport.send(message);
			
			loggerForEmailCount.info("Total 1 Mail sent via(SMTP) from "+from+", Email Subject : Transaction Conflicts, for sendProjectConflictsEmail");

		} catch(Exception e) {
			
		}


		

	}
	
	
	@Async
	public void sendCaseStudyReport(String title,String fileName,String folderName,String mailId) {
		
		if(!"PROD".equalsIgnoreCase(ENVIRONMENT)){
			return;
		}
		
		Properties props = new Properties();
		props.put("mail.smtp.auth", "true");
		props.put("mail.smtp.starttls.enable", "true");
		props.put("mail.smtp.host", host);
		props.put("mail.smtp.port", port);


		Session session = Session.getInstance(props,
				new javax.mail.Authenticator() {
			protected PasswordAuthentication getPasswordAuthentication() {
				return new PasswordAuthentication(username, password);
			}
		});

		try {

			MimeMessage message = new MimeMessage(session);
			
			MimeMessageHelper helper = new MimeMessageHelper(message, true);
			
			String body = "";
			body = "Dear Team, <br>"
					+"Please find attachment of "+title+". <br> <br>";
			body = body + "<br>Thanks & Regards"
					+ "<br>"
					+ "NMIMS GLOBAL ACCESS SCHOOL FOR CONTINUING EDUCATION";

			helper.setFrom(new InternetAddress(from, "NMIMS Global Access SCE"));
			helper.setTo(mailId);
			helper.setSubject(title);
			helper.setText(body);
			
			FileSystemResource file = new FileSystemResource(folderName+fileName);
			helper.addAttachment(file.getFilename(), file);

			Transport.send(message);

			loggerForEmailCount.info("Total 1 Mail sent via(SMTP) from "+from+", Email Subject : "+title+", for sendCaseStudyReport");
		} catch(Exception e) {
			
		}



	}
	@Async
	public void sendProjectTransactionApproveEmail(StudentExamBean student, ExamBookingTransactionBean bean) {
		if(!"PROD".equalsIgnoreCase(ENVIRONMENT)){
			return;
		}
		Properties props = new Properties();
		props.put("mail.smtp.auth", "true");
		props.put("mail.smtp.starttls.enable", "true");
		props.put("mail.smtp.host", host);
		props.put("mail.smtp.port", port);


		Session session = Session.getInstance(props,
				new javax.mail.Authenticator() {
			protected PasswordAuthentication getPasswordAuthentication() {
				return new PasswordAuthentication(username, password);
			}
		});

		try {

			Message message = new MimeMessage(session);
			message.setFrom(new InternetAddress(from, "NMIMS Global Access SCE"));
			message.setRecipients(Message.RecipientType.TO,InternetAddress.parse(student.getEmailId()));
			message.setRecipients(Message.RecipientType.BCC,InternetAddress.parse("sanketpanaskar@gmail.com, jigna.patel@nmims.edu, harshalee.ullal@nmims.edu, pooja.jadhav@nmims.edu, christopher.kevin@nmims.edu, pranit.shirke.ext@nmims.edu, jforce.solution@gmail.com , nelson.soans@nmims.edu"));
			message.setSubject("Exam Registration Transaction Successful");

			String body = "";

			body = "Dear Student, <br>"
					+"Your transaction of amount " + bean.getRespAmount() + " is recorded successfully at our end <br>"
					+"Please visit Exam Portal and proceed with project submission. <br>"
					+ " Student ID: "+student.getSapid()
					+ "<br>";



			body = body + "<br>Thanks & Regards"
					+ "<br>"
					+ "NMIMS GLOBAL ACCESS SCHOOL FOR CONTINUING EDUCATION";


			message.setContent(body, "text/html; charset=utf-8");

			Transport.send(message);

			loggerForEmailCount.info("Total 1 Mail sent via(SMTP) from "+from+", Email Subject : Exam Registration Transaction Successful, for sendProjectTransactionApproveEmail");

		} catch(Exception e) {
			
		}



	}
	public void sendEmailForTestsReminder(StudentExamBean student, TestExamBean test, TestDAO dao) {
		/*
		 * if(!"PROD".equalsIgnoreCase(ENVIRONMENT)){
		 * return; }
		 */
		Properties props = new Properties();
		props.put("mail.smtp.auth", "true");
		props.put("mail.smtp.starttls.enable", "true");
		props.put("mail.smtp.host", host);
		props.put("mail.smtp.port", port);


		Session session = Session.getInstance(props,
				new javax.mail.Authenticator() {
			protected PasswordAuthentication getPasswordAuthentication() {
				return new PasswordAuthentication(username, password);
			}
		});

		try {

			Message message = new MimeMessage(session);
			message.setFrom(new InternetAddress(from, "NMIMS Global Access SCE"));
			message.setRecipients(Message.RecipientType.TO,InternetAddress.parse(student.getEmailId()));
			message.setRecipients(Message.RecipientType.BCC,InternetAddress.parse("jforce.solution@gmail.com"));
			message.setSubject("Internal Assessments Test Reminder Scheduled For "+test.getStartDate());

			String body = "";

			body = "Dear Student, <br>"
					+"Your Internal Assessments Test : " + test.getTestName() + " will be live on "+test.getStartDate()+" till "+test.getEndDate()+" <br>"
					+"Please visit  Portal and proceed with test within the time range . <br>"
					+ "<br>";

			body = body + "<br>Thanks & Regards"
					+ "<br>"
					+ "NMIMS GLOBAL ACCESS SCHOOL FOR CONTINUING EDUCATION";


			message.setContent(body, "text/html; charset=utf-8");

			Transport.send(message);

			loggerForEmailCount.info("Total 1 Mail sent via(SMTP) from "+from+", Email Subject : Internal Assessments Test Reminder , for sendEmailForTestsReminder");

		} catch(Exception e) {
			
		}



	}


	@Async
	public void sendMBAWXExamBookingSummaryEmail(StudentExamBean student, List<MBAExamBookingRequest> bookingRequests) {
		
		if(!"PROD".equalsIgnoreCase(ENVIRONMENT)){
			return;
		}
		Properties props = new Properties();
		props.put("mail.smtp.auth", "true");
		props.put("mail.smtp.starttls.enable", "true");
		props.put("mail.smtp.host", host);
		props.put("mail.smtp.port", port);
		Session session = Session.getInstance(props,
				new javax.mail.Authenticator() {
			protected PasswordAuthentication getPasswordAuthentication() {
				return new PasswordAuthentication(username, password);
			}
		});
		try {
			Message message = new MimeMessage(session);
			message.setFrom(new InternetAddress(from, "NMIMS Global Access SCE"));

			message.setRecipients(Message.RecipientType.TO,InternetAddress.parse(student.getEmailId()));

//			message.setRecipients(Message.RecipientType.TO,InternetAddress.parse("ashutosh.sultania.ext@nmims.edu"));
			message.setSubject("Your Exam Registration Summary");

			String body = "";

			String month = bookingRequests.get(0).getMonth();
			String year = bookingRequests.get(0).getYear();
			body = "Dear " + student.getFirstName() +", <br>"
					+"Here is your Exam Registration Summary for " + month +"-"+ year +" exam. <br>"
					+ " Student ID: "+student.getSapid() +"<br>"
					+ " Program: "+student.getProgram()+"<br>"
					+ "<br>";

			body = body + ""
					+ "<table border=\"1\">"
						+ "<thead>"
							+ "<tr>"
								+ "<th>Sr. No.</th>"
								+ "<th>Subject</th>"
								+ "<th>Term</th>"
								+ "<th>Date</th>"
								+ "<th>Start Time</th>"
								+ "<th>End Time</th>"
								+ "<th>Transaction Status</th>"
								+ "<th>Booking Status</th>"
								+ "<th>Exam Center Booked</th>"
							+ "</tr>"
						+ "</thead>"
					+ "<tbody>";

			int count = 1;
			
			SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			SimpleDateFormat dateFormatter = new SimpleDateFormat("EEEE, dd-MMM-yyyy");
			SimpleDateFormat timeFormatter = new SimpleDateFormat("hh:mm a");

			for (MBAExamBookingRequest bookingRequest : bookingRequests) {
				
				Date formattedStartDateTime = formatter.parse(bookingRequest.getExamStartDateTime());
				Date formattedEndDateTime = formatter.parse(bookingRequest.getExamEndDateTime());
				String examDate = dateFormatter.format(formattedStartDateTime);
				String startTime = timeFormatter.format(formattedStartDateTime);
				String endTime = timeFormatter.format(formattedEndDateTime);
				
				String booked = bookingRequest.getBookingStatus();
				String bookingStatus = null;
				if("Y".equals(booked)){
					bookingStatus = "Booked";
				}else{
					bookingStatus = "Not Booked";
				}
				
				String examCenterName = bookingRequest.getCenterName();
				String subject = bookingRequest.getSubjectName();
				String term = bookingRequest.getTerm();
				String transStatus = bookingRequest.getTranStatus();
				body =	body + ""
						+ "<tr> "
							+ "<td align=\"center\"> " + (count++) +"</td> "
							+ "<td> " + subject + " </td> "
							+ "<td align=\"center\">" + term +"</td>"
							+ "<td>" + examDate + "</td>"
							+ "<td>" + startTime + "(IST)</td>"
							+ "<td>" + endTime + "(IST)</td>"
							+ "<td>" + transStatus + "</td>"
							+ "<td align=\"center\">" + bookingStatus + "</td>"
							+ "<td>" + examCenterName + "</td>"
						+ "</tr> ";
			}

			body = body + ""
					+ "</tbody>"
				+ "</table>";	 	

			body = body + "<br>"
					+ "<b>Note:</b><br>"
					+ "1. This Email is NOT a Hall ticket.<br>"
					// TODO : HALL TICKET # DAYS 
					+ "2. Hall Ticket will be made available for download on Exam Portal 1 day prior to Exams.<br>"
					+ "3. NMIMS University reserves right to shift students to another available exam center in case of any unavoidable circumstances. <br>";

			body = body + ""
					+ "<br>Thanks & Regards"
					+ "<br>"
					+ "NMIMS GLOBAL ACCESS SCHOOL FOR CONTINUING EDUCATION";

			message.setContent(body, "text/html; charset=utf-8");
			Transport.send(message);
			
			loggerForEmailCount.info("Total 1 Mail sent via(SMTP) from "+from+", Email Subject : Your Exam Registration Summary, for sendMBAWXExamBookingSummaryEmail");
		} catch(Exception e) {
			
		}

	}
	
	@Async
	public void sendTransactionApproveEmail_MBA_WX(StudentExamBean student, MBAExamBookingRequest bean) {
		if(!"PROD".equalsIgnoreCase(ENVIRONMENT)){
			return;
		}
		Properties props = new Properties();
		props.put("mail.smtp.auth", "true");
		props.put("mail.smtp.starttls.enable", "true");
		props.put("mail.smtp.host", host);
		props.put("mail.smtp.port", port);


		Session session = Session.getInstance(props,
				new javax.mail.Authenticator() {
			protected PasswordAuthentication getPasswordAuthentication() {
				return new PasswordAuthentication(username, password);
			}
		});

		try {

			Message message = new MimeMessage(session);
			message.setFrom(new InternetAddress(from, "NMIMS Global Access SCE"));
			message.setRecipients(Message.RecipientType.TO,InternetAddress.parse(student.getEmailId()));
			message.setRecipients(Message.RecipientType.BCC,InternetAddress.parse("sanketpanaskar@gmail.com, jigna.patel@nmims.edu, harshalee.ullal@nmims.edu, pooja.jadhav@nmims.edu, christopher.kevin@nmims.edu, pranit.shirke.ext@nmims.edu, jforce.solution@gmail.com , nelson.soans@nmims.edu"));
			message.setSubject("Exam Registration Transaction Successful");

			String body = "";

			body = "Dear Student, <br>"
					+"Your exam slot has been booked successfully, with <br>"
					+"transaction amount of " + bean.getRespAmount() + ".<br>"
					+ " Student ID: "+student.getSapid()
					+ "<br>";



			body = body + "<br>Thanks & Regards"
					+ "<br>"
					+ "NMIMS GLOBAL ACCESS SCHOOL FOR CONTINUING EDUCATION";


			message.setContent(body, "text/html; charset=utf-8");

			Transport.send(message);

			loggerForEmailCount.info("Total 1 Mail sent via(SMTP) from "+from+", Email Subject : Exam Registration Transaction Successful, for sendTransactionApproveEmail_MBA_WX");

		} catch(Exception e) {
			
		}



	}

	@Async
	public void sendMBAXExamBookingSummaryEmail(StudentExamBean student, List<MBAExamBookingRequest> bookingRequests) {
		
		if(!"PROD".equalsIgnoreCase(ENVIRONMENT)){
			return;
		}
		Properties props = new Properties();
		props.put("mail.smtp.auth", "true");
		props.put("mail.smtp.starttls.enable", "true");
		props.put("mail.smtp.host", host);
		props.put("mail.smtp.port", port);
		Session session = Session.getInstance(props,
				new javax.mail.Authenticator() {
			protected PasswordAuthentication getPasswordAuthentication() {
				return new PasswordAuthentication(username, password);
			}
		});
		try {
			Message message = new MimeMessage(session);
			message.setFrom(new InternetAddress(from, "NMIMS Global Access SCE"));

			message.setRecipients(Message.RecipientType.TO,InternetAddress.parse(student.getEmailId()));

//			message.setRecipients(Message.RecipientType.TO,InternetAddress.parse("ashutosh.sultania.ext@nmims.edu"));
			message.setSubject("Your Exam Registration Summary");

			String body = "";

			String month = bookingRequests.get(0).getMonth();
			String year = bookingRequests.get(0).getYear();
			body = "Dear " + student.getFirstName() +", <br>"
					+"Here is your Exam Registration Summary for " + month +"-"+ year +" exam. <br>"
					+ " Student ID: "+student.getSapid() +"<br>"
					+ " Program: "+student.getProgram()+"<br>"
					+ "<br>";

			body = body + ""
					+ "<table border=\"1\">"
						+ "<thead>"
							+ "<tr>"
								+ "<th>Sr. No.</th>"
								+ "<th>Subject</th>"
								+ "<th>Term</th>"
								+ "<th>Date</th>"
								+ "<th>Start Time</th>"
								+ "<th>End Time</th>"
								+ "<th>Transaction Status</th>"
								+ "<th>Booking Status</th>"
								+ "<th>Exam Center Booked</th>"
							+ "</tr>"
						+ "</thead>"
					+ "<tbody>";

			int count = 1;
			
			SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			SimpleDateFormat dateFormatter = new SimpleDateFormat("EEEE, dd-MMM-yyyy");
			SimpleDateFormat timeFormatter = new SimpleDateFormat("hh:mm a");

			for (MBAExamBookingRequest bookingRequest : bookingRequests) {
				
				Date formattedStartDateTime = formatter.parse(bookingRequest.getExamStartDateTime());
				Date formattedEndDateTime = formatter.parse(bookingRequest.getExamEndDateTime());
				String examDate = dateFormatter.format(formattedStartDateTime);
				String startTime = timeFormatter.format(formattedStartDateTime);
				String endTime = timeFormatter.format(formattedEndDateTime);
				
				String booked = bookingRequest.getBookingStatus();
				String bookingStatus = null;
				if("Y".equals(booked)){
					bookingStatus = "Booked";
				}else{
					bookingStatus = "Not Booked";
				}
				
				String examCenterName = bookingRequest.getCenterName();
				String subject = bookingRequest.getSubjectName();
				String term = bookingRequest.getTerm();
				String transStatus = bookingRequest.getTranStatus();
				body =	body + ""
						+ "<tr> "
							+ "<td align=\"center\"> " + (count++) +"</td> "
							+ "<td> " + subject + " </td> "
							+ "<td align=\"center\">" + term +"</td>"
							+ "<td>" + examDate + "</td>"
							+ "<td>" + startTime + "(IST)</td>"
							+ "<td>" + endTime + "(IST)</td>"
							+ "<td>" + transStatus + "</td>"
							+ "<td align=\"center\">" + bookingStatus + "</td>"
							+ "<td>" + examCenterName + "</td>"
						+ "</tr> ";
			}

			body = body + ""
					+ "</tbody>"
				+ "</table>";	 	

			body = body + "<br>"
					+ "<b>Note:</b><br>"
					+ "1. This Email is NOT a Hall ticket.<br>"
					// TODO : HALL TICKET # DAYS 
					+ "2. Hall Ticket will be made available for download on Exam Portal 1 day prior to Exams.<br>"
					+ "3. NMIMS University reserves right to shift students to another available exam center in case of any unavoidable circumstances. <br>";

			body = body + ""
					+ "<br>Thanks & Regards"
					+ "<br>"
					+ "NMIMS GLOBAL ACCESS SCHOOL FOR CONTINUING EDUCATION";

			message.setContent(body, "text/html; charset=utf-8");
			Transport.send(message);
			
			loggerForEmailCount.info("Total 1 Mail sent via(SMTP) from "+from+", Email Subject : Your Exam Registration Summary, for sendMBAXExamBookingSummaryEmail");
		} catch(Exception e) {
			
		}

	}
	
	@Async
	public void sendTransactionApproveEmail_MBA_X(StudentExamBean student, MBAExamBookingRequest bean) {
		if(!"PROD".equalsIgnoreCase(ENVIRONMENT)){
			return;
		}
		Properties props = new Properties();
		props.put("mail.smtp.auth", "true");
		props.put("mail.smtp.starttls.enable", "true");
		props.put("mail.smtp.host", host);
		props.put("mail.smtp.port", port);


		Session session = Session.getInstance(props,
				new javax.mail.Authenticator() {
			protected PasswordAuthentication getPasswordAuthentication() {
				return new PasswordAuthentication(username, password);
			}
		});

		try {

			Message message = new MimeMessage(session);
			message.setFrom(new InternetAddress(from, "NMIMS Global Access SCE"));
			message.setRecipients(Message.RecipientType.TO,InternetAddress.parse(student.getEmailId()));
			message.setRecipients(Message.RecipientType.BCC,InternetAddress.parse("sanketpanaskar@gmail.com, jigna.patel@nmims.edu, harshalee.ullal@nmims.edu, pooja.jadhav@nmims.edu, christopher.kevin@nmims.edu, pranit.shirke.ext@nmims.edu, jforce.solution@gmail.com , nelson.soans@nmims.edu"));
			message.setSubject("Exam Registration Transaction Successful");

			String body = "";

			body = "Dear Student, <br>"
					+"Your exam slot has been booked successfully, with <br>"
					+"transaction amount of " + bean.getRespAmount() + ".<br>"
					+ " Student ID: "+student.getSapid()
					+ "<br>";



			body = body + "<br>Thanks & Regards"
					+ "<br>"
					+ "NMIMS GLOBAL ACCESS SCHOOL FOR CONTINUING EDUCATION";


			message.setContent(body, "text/html; charset=utf-8");

			Transport.send(message);

			loggerForEmailCount.info("Total 1 Mail sent via(SMTP) from "+from+", Email Subject : Exam Registration Transaction Successful, for sendTransactionApproveEmail_MBA_X");

		} catch(Exception e) {
			
		}


	}
	/*
	 * 
//		if(!"PROD".equalsIgnoreCase(ENVIRONMENT)){
//			return;
//		}
	 * */

	@Async
	public void sendIATestCopyCaseReport(String title,String fileName,String folderName,String[] mailId) {
		
		
		Properties props = new Properties();
		props.put("mail.smtp.auth", "true");
		props.put("mail.smtp.starttls.enable", "true");
		props.put("mail.smtp.host", host);
		props.put("mail.smtp.port", port);


		Session session = Session.getInstance(props,
				new javax.mail.Authenticator() {
			protected PasswordAuthentication getPasswordAuthentication() {
				return new PasswordAuthentication(username, password);
			}
		});

		try {

			MimeMessage message = new MimeMessage(session);
			
			MimeMessageHelper helper = new MimeMessageHelper(message, true);
			
			String body = "";
			body = "Dear Team, <br>"
					+"Please find attachment of "+title+". <br> <br>";
			body = body + "<br>Thanks & Regards"
					+ "<br>"
					+ "NMIMS GLOBAL ACCESS SCHOOL FOR CONTINUING EDUCATION";

			helper.setFrom(new InternetAddress(from, "NMIMS Global Access SCE"));
			helper.setTo(mailId);
			helper.setSubject(ENVIRONMENT+" : "+ title);
			helper.setText(body);
			helper.setBcc( new String[]{ "pranit.shirke.ext@nmims.edu", "shiv.golani.ext@nmims.edu", "harsh.kumar.EXT@nmims.edu", 
					"abhay.sakpal.ext@nmims.edu", "jforcesolutions@gmail.com" } );
			
			FileSystemResource file = new FileSystemResource(folderName+fileName);
			helper.addAttachment(file.getFilename(), file);

			Transport.send(message);

			loggerForEmailCount.info("Total 1 Mail sent via(SMTP) from "+from+", Email Subject : "+title+", for sendIATestCopyCaseReport");
		} catch(Exception e) {
			
		}



	}

	@Async
	public void sendIALostfocusCopyCaseReport(String title, String fileName, String folderName, 
			String[] mailId, TestExamBean test ) {
		
		Properties props = new Properties();
		props.put("mail.smtp.auth", "true");
		props.put("mail.smtp.starttls.enable", "true");
		props.put("mail.smtp.host", host);
		props.put("mail.smtp.port", port);

		Session session = Session.getInstance(props,
				new javax.mail.Authenticator() {
			protected PasswordAuthentication getPasswordAuthentication() {
				return new PasswordAuthentication(username, password);
			}
		});

		try {

			MimeMessage message = new MimeMessage(session);
			
			MimeMessageHelper helper = new MimeMessageHelper(message, true);

			String body = "Dear Team, <br>"
					+"The following IA was considered for marking lost focus copy case.<br><br>";
			body += test.getTestName()+"<br>";
			body += "<br>Please find attachment of "+title+"<br><br>"; 
			body = body + "<br>Thanks & Regards"
					+ "<br>"
					+ "NMIMS GLOBAL ACCESS SCHOOL FOR CONTINUING EDUCATION";

			helper.setFrom(new InternetAddress(from, "NMIMS Global Access SCE"));
			helper.setTo(mailId);
			helper.setSubject(ENVIRONMENT+" : "+ title);
			helper.setText(body);
			helper.setBcc( new String[]{ "pranit.shirke.ext@nmims.edu", "shiv.golani.ext@nmims.edu", "harsh.kumar.EXT@nmims.edu", 
					"abhay.sakpal.ext@nmims.edu", "jforcesolutions@gmail.com" } );
			
			FileSystemResource file = new FileSystemResource(folderName+fileName);
			helper.addAttachment(file.getFilename(), file);

			Transport.send(message);

			loggerForEmailCount.info("Total 1 Mail sent via(SMTP) from "+from+", Email Subject : "+title+", for sendIATestCopyCaseReport");
		} catch(Exception e) {
			
		}

	}

	@Async
	public void mailStackTraceForIATestCopyCaseError(String subject, String copyCaseError ) {
		
		/*
		 * if(!"PROD".equalsIgnoreCase(ENVIRONMENT)){
		 * return; }
		 */
		
		Properties props = new Properties();
		props.put("mail.smtp.auth", "true");
		props.put("mail.smtp.starttls.enable", "true");
		props.put("mail.smtp.host", host);
		props.put("mail.smtp.port", port);


		Session session = Session.getInstance(props,
				new javax.mail.Authenticator() {
			protected PasswordAuthentication getPasswordAuthentication() {
				return new PasswordAuthentication(username, password);
			}
		});

		try {

			Message message = new MimeMessage(session);
			message.setFrom(new InternetAddress(from, "NMIMS Global Access SCE"));
			message.setRecipients(Message.RecipientType.TO,InternetAddress.parse("jforce.solution@gmail.com"));

			message.setSubject(subject);

			String body = copyCaseError;

			body = body + "<br><br>Thanks & Regards"
					+ "<br>"
					+ "NMIMS GLOBAL ACCESS SCHOOL FOR CONTINUING EDUCATION";


			message.setContent(body, "text/html; charset=utf-8");

			Transport.send(message);

			loggerForEmailCount.info("Total 1 Mail sent via(SMTP) from "+from+", Email Subject : "+subject+", for mailStackTraceForIATestCopyCaseError");
		} catch(Exception ex) {
			
		}


	}
	
	
	@Async
	public void sendSpecialisationSubjectSummary (StudentExamBean student, LinkedList<ProgramSubjectMappingExamBean> SpecialisationSubjectList, Integer subjectSelectedForTerm) {
		
		if(!"PROD".equalsIgnoreCase(ENVIRONMENT)){
			return;
		}
		
		Properties props = new Properties();
		props.put("mail.smtp.auth", "true");
		props.put("mail.smtp.starttls.enable", "true");
		props.put("mail.smtp.host", host);
		props.put("mail.smtp.port", port);
		
		Session session = Session.getInstance(props, new javax.mail.Authenticator() {
			protected PasswordAuthentication getPasswordAuthentication() {
				return new PasswordAuthentication(username, password);
			}
		});
		
		try {
			Message message = new MimeMessage(session);
			message.setFrom(new InternetAddress(from, "NMIMS Global Access SCE"));
			message.setRecipients(Message.RecipientType.TO,InternetAddress.parse(student.getEmailId()));
//			message.setRecipients(Message.RecipientType.BCC,InternetAddress.parse("jforcesolutions@gmail.com, Somesh.Turde.Ext@nmims.edu"));
			message.setSubject("Your Electives subject Summary");
			
			String body = "";

			body =    "Dear " + student.getFirstName() +", <br>"
					+ "Here is your Term "+ subjectSelectedForTerm +" Electives Specialisation Summary. <br>"
					+ "Student ID: "+student.getSapid() +"<br>"
					+ "Program: "+student.getProgram()+"<br>"
					+ "<br>";

			body = body
					+ "<table border=\"1\">"
					+ "<thead>"
					+ "<tr> "
					+ "<th>Sr. No.</th>"
					+ "<th>Subject</th>"
					+ "<th>Sem</th>"
					+ "<th>Specialisation</th>"
					+ "</tr>"
					+ "</thead>"
					+ "<tbody>";
			
			int count = 1;

			for (ProgramSubjectMappingExamBean subject : SpecialisationSubjectList) {
					
				body =  body 
						+ " <tr> "
						+ " <td align=\"center\"> " + (count++) +"</td> "
						+ "	<td> " + subject.getSubject() + " </td> "
						+ "	<td align=\"center\"> " + subjectSelectedForTerm +"</td> "
						+ "	<td> " + subject.getSpecializationTypeName()+"</td> "
						+ " </tr> ";
			}
			
			body = body + " </tbody> "
						+ " </table>"
						+ " <br>Thanks & Regards"
						+ " <br>"
						+ " NMIMS GLOBAL ACCESS SCHOOL FOR CONTINUING EDUCATION";
		
		message.setContent(body, "text/html; charset=utf-8");
		Transport.send(message);
		
		loggerForEmailCount.info("Total 1 Mail sent via(SMTP) from "+from+", Email Subject : Your Electives subject Summary, for sendSpecialisationSubjectSummary");
			
		} catch (Exception e) {
			
		}
	}
	
	public void sendSREmail (StudentExamBean student, ServiceRequestBean srBean) {
		
		if(!"PROD".equalsIgnoreCase(ENVIRONMENT)){
			return;
		}
		
		Properties props = new Properties();
		props.put("mail.smtp.auth", "true");
		props.put("mail.smtp.starttls.enable", "true");
		props.put("mail.smtp.host", host);
		props.put("mail.smtp.port", port);
		
		Session session = Session.getInstance(props, new javax.mail.Authenticator() {
			protected PasswordAuthentication getPasswordAuthentication() {
				return new PasswordAuthentication(username, password);
			}
		});
		
		try {
			Message message = new MimeMessage(session);
			message.setFrom(new InternetAddress(from, "NMIMS Global Access SCE"));
			message.setRecipients(Message.RecipientType.TO,InternetAddress.parse(student.getEmailId()));
			message.setRecipients(Message.RecipientType.BCC,InternetAddress.parse("jforcesolutions@gmail.com, Somesh.Turde.Ext@nmims.edu"));
			message.setSubject("Service Request for Change in Specialisation ");
			
			String body = "Dear "+ student.getFirstName()+" <br><br>"
						+ "Greetings from NMIMS Global Access School for Continuing Education!<br><br>"
						+ "Thank you for submitting your request for Change in Specialisation. <br>" 
						+ "Service Request Number " + srBean.getId() + " been created. <br>";

			body += "Please quote Service Request Number " + srBean.getId() + " for all future communication related to this Service Request.<br><br>"
					
					+"Student Name: "+student.getFirstName() +  " " + student.getLastName() + " <br>"
					+"Student ID: "+student.getSapid()+ " <br>"
					+"Service Request Number: "+srBean.getId() + " <br>"
					+"Service Request Name: Change in Specialisation <br>"
					+"Thanks & Regards"
					+"<br>"
					+"NMIMS GLOBAL ACCESS SCHOOL FOR CONTINUING EDUCATION";
			
			message.setContent(body, "text/html; charset=utf-8");
			Transport.send(message);
			
			loggerForEmailCount.info("Total 1 Mail sent via(SMTP) from "+from+", Email Subject : Service Request for Change in Specialisation, for sendSpecialisationSubjectSummary");
			
		} catch (Exception e) {
			
		}

	}
	
	public void sendExamBookingRequestEmail (StudentExamBean student,String status) {
		
		if(!"PROD".equalsIgnoreCase(ENVIRONMENT)){
			return;
		}
		
		Properties props = new Properties();
		props.put("mail.smtp.auth", "true");
		props.put("mail.smtp.starttls.enable", "true");
		props.put("mail.smtp.host", host);
		props.put("mail.smtp.port", port);
		
		Session session = Session.getInstance(props, new javax.mail.Authenticator() {
			protected PasswordAuthentication getPasswordAuthentication() {
				return new PasswordAuthentication(username, password);
			}
		});
		
		try {
			Message message = new MimeMessage(session);
			message.setFrom(new InternetAddress(from, "NMIMS Global Access SCE"));
			message.setRecipients(Message.RecipientType.TO,InternetAddress.parse(student.getEmailId()));
			message.setRecipients(Message.RecipientType.BCC,InternetAddress.parse("jforcesolutions@gmail.com"));
			if("refund".equalsIgnoreCase(status)){
				message.setSubject("Refund request for examBooking Apr-2020");
			}else {
				message.setSubject("Carry forward request for examBooking Apr-2020 amount to Jun-2020");
			}
			
			
			String body = "";
			if("refund".equalsIgnoreCase(status)){
				body = "Dear "+ student.getFirstName()+" <br><br>"
						+ "Greetings from NMIMS Global Access School for Continuing Education!<br><br>"
						+ "We appreciate and thank you for your selection, exam fees will be processed for refund once we resume work post lock down situation ends. Refund process thereafter will take 15-20 days<br/>" ;
			}else {
				body = "Dear "+ student.getFirstName()+" <br><br>"
						+ "Greetings from NMIMS Global Access School for Continuing Education!<br><br>"
						+ "We appreciate and thank you for your selection, this amount will be adjusted against the bookings you make for June 2020 exam cycle. Any additional bookings made during June exam cycle will incur the applicable charges <br>";
			}

			body += "Thanks & Regards"
					+"<br>"
					+"NMIMS GLOBAL ACCESS SCHOOL FOR CONTINUING EDUCATION";
			
			message.setContent(body, "text/html; charset=utf-8");
			Transport.send(message);
			
			loggerForEmailCount.info("Total 1 Mail sent via(SMTP) from "+from+", Email Subject : Refund request for examBooking Apr-2020, for sendExamBookingRequestEmail");
			
		} catch (Exception e) {
			
		}

	}
	
	@Async
	public void sendMailsForUnfairMeans(LostFocusLogExamBean bean) throws Exception {
		
		// uncommemt when testing is over
//		if(!"PROD".equalsIgnoreCase(ENVIRONMENT)){
//			return;
//		}

		Properties props = new Properties();
		props.put("mail.smtp.auth", "true");
		props.put("mail.smtp.starttls.enable", "true");
		props.put("mail.smtp.host", host);
		props.put("mail.smtp.port", port);

		Session session = Session.getInstance(props,
				new javax.mail.Authenticator() {
			protected PasswordAuthentication getPasswordAuthentication() {
				return new PasswordAuthentication(username, password);
			}
		});

		try {
			Message message = new MimeMessage(session);
			message.setFrom(new InternetAddress(from, "NMIMS Global Access SCE"));
			try {
				message.setRecipients(Message.RecipientType.TO,InternetAddress.parse(bean.getEmailId()));
				message.setSubject("IA Proctoring- System alert");
			} catch (Exception e) {
				message.setRecipients(Message.RecipientType.CC,InternetAddress.parse("harsh.kumar.ext@nmims.edu"));
				message.setSubject("Error: Student Email Not Available : IA Proctoring- System alert for: "+bean.getTestId());
			}
			//If student replies it should go to Exam Email ID
			message.setReplyTo(InternetAddress.parse("ngasce.exams@nmims.edu"));

			String body = "";

			body = "Dear Student, " + " <br><br> " 
					+ "Post implementing Proctoring mechanism last week, we have identified that you have moved away from the IA window on multiple occasions. The triggers generated have been validated for that day. <br><br>" 
					+ "<b>"
					+ "Time in minutes: " + bean.getTimeAway()
					+ "Number of instances: " + bean.getCount()
					+ "</b><br><br>"
					+ "We expect this to change with immediate effect, failing to which your IA will be tagged for Plagiarism, in turn this will impact your future IA scores. <br> "
					+ "Regards," + " <br> " 
					+ "NGA-SCE" + " <br> " ;

			message.setContent(body, "text/html; charset=utf-8");

			Transport.send(message);
			
			loggerForEmailCount.info("Total 1 Mail sent via(SMTP) from "+from+", Email Subject : IA Proctoring- System alert, for sendMailsForUnfairMeans");

		} catch(Exception e) {
			
		}


	}
	
	@Async
	private void createRecordInUserMailTableAndMailTable(MailBean mail) {
		TestDAO dao = (TestDAO)act.getBean("testDao");
		long mailId  = dao.insertMailRecord(mail);
		if (mailId > 0) {
			dao.insertUserMailRecord(mail, mailId);
		}
	}

	@Async
	public void sendTestEndedEmailForLeads(StudentExamBean student, TestExamBean test,StudentsTestDetailsExamBean studentsTestDetails) {
		
		// uncommemt when testing is over
		  if(!"PROD".equalsIgnoreCase(ENVIRONMENT)){
			return;
		}
		  
		String emailSubject = "Internal Assessment Test Completed for "+test.getSubject() + " - "+test.getTestName() + ".";
		String body = "";
		
		try {
			Properties props = new Properties();
			props.put("mail.smtp.auth", "true");
			props.put("mail.smtp.starttls.enable", "true");
			props.put("mail.smtp.host", host);
			props.put("mail.smtp.port", port);

			Session session = Session.getInstance(props,
					new javax.mail.Authenticator() {
				protected PasswordAuthentication getPasswordAuthentication() {
					return new PasswordAuthentication(username, password);
				}
			});

			try {
				SimpleDateFormat formatter = new SimpleDateFormat("dd-MMM-yyyy HH:mm:ss");

				Message message = new MimeMessage(session);
				message.setFrom(new InternetAddress(from, "NMIMS Global Access SCE"));
				try {
					//message.setRecipients(Message.RecipientType.TO,InternetAddress.parse("jforcesolutions@gmail.com"));
					
					message.setSubject(emailSubject);
					message.setRecipients(Message.RecipientType.BCC,InternetAddress.parse("jforcesolutions@gmail.com"));
					message.setRecipients(Message.RecipientType.TO,InternetAddress.parse(student.getEmailId()));
					
				} catch (Exception e) {
					message.setRecipients(Message.RecipientType.CC,InternetAddress.parse("pranit.shirke.ext@nmims.edu"));
					message.setSubject("Error: Student Email Not Available : Test Completed for "+test.getSubject());
					logger.info("\n"+"IN sendTestEndedEmail Invalid Student Email got sapid: "+studentsTestDetails.getSapid()+" testId: "+test.getId()+" Error: "+e.toString());
					logger.info(" "+e);
				}
				//If student replies it should go to Exam Email ID
				message.setReplyTo(InternetAddress.parse("ngasce.exams@nmims.edu"));
				
				body = "Dear Student, <br><br>"
						+"Your internal assessment for the following subject was successfully submitted. <br><br>"
						+"Student Name: "+student.getFirstName().toUpperCase() + " " + student.getLastName().toUpperCase()  + " <br>"
						+"LEAD ID: "+student.getLeadId() + "<br>"
						+"Program: "+student.getProgram() + "<br>"
						+"Subject: "+test.getSubject()+ "<br>"
						+"Test Name: "+test.getTestName()+ "<br>"
						+"Date-Time of submission: "+formatter.format(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(studentsTestDetails.getTestEndedOn()))+ "<br>"
						+"Attempt No.: "+studentsTestDetails.getAttempt()+ " / Max Attempts : "+ test.getMaxAttempt()+"<br><br>";

				body = body  

						+"<b>Thank you for taking the test .</b><br><br>"
						+"Regards,<br>"
						+"NGA-SCE<br> "


							+ "<hr>"

							+"Please Note:<br>"
							+"<b>The Auto-generated submission mail is only the acknowledgement of test was ended . </b><br><br>"
							+"Incase of any doubt or query regarding the same, student can get in touch with support for clarification.<br>"
							+ "<hr>";


				message.setContent(body, "text/html; charset=utf-8");
				Transport.send(message);
				
				loggerForEmailCount.info("Total 1 Mail sent via(SMTP) from "+from+", Email Subject : "+emailSubject+", for sendTestEndedEmailForLeads");

			} catch(Exception e) {
				
				logger.info("\n"+"IN sendTestEndedEmail error mail sending got sapid: "+studentsTestDetails.getSapid()+" testId: "+test.getId()+" Error: "+e.toString());
				StringWriter errors = new StringWriter();
				e.printStackTrace(new PrintWriter(errors));
				String apiCalled = "sendTestEndedEmail() : ";
				String stackTrace = "apiCalled="+ apiCalled + ",data= testId: "+ test.getId()+" sapid: "+studentsTestDetails.getSapid()+ 
						",errors=" + errors.toString();
				
				logger.info(stackTrace);
				
			}

			logger.info("\n"+"IN sendTestEndedEmailForLeads Sent Test Email to Sapid: "+studentsTestDetails.getSapid()+" testId: "+test.getId()+" ");
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			
			logger.info("\n"+"IN sendTestEndedEmailForleads error got sapid: "+studentsTestDetails.getSapid()+" testId: "+test.getId()+" Error: "+e.toString());
			logger.info(" "+e);
			
		}
		
		MailBean mail = new MailBean();
		mail.setSubject(emailSubject);
		mail.setBody(body);
		mail.setMailId(student.getEmailId());
		mail.setFromEmailId("donotreply-ngasce@nmims.edu");
		mail.setCreatedBy(student.getSapid());
		
		//createRecordInUserMailTableAndMailTable(mail);

	}

	public void sendExamJoinMail(MettlSSOInfoBean booking) throws Exception {
		Properties props = new Properties();
		props.put("mail.smtp.auth", "true");
		props.put("mail.smtp.starttls.enable", "true");
		props.put("mail.smtp.host", host);
		props.put("mail.smtp.port", port);

		Session session = Session.getInstance(props,
				new javax.mail.Authenticator() {
			protected PasswordAuthentication getPasswordAuthentication() {
				return new PasswordAuthentication(username, password);
			}
		});
		Message message = new MimeMessage(session);
		message.setFrom(new InternetAddress(from, "NMIMS Global Access SCE"));
		message.setRecipients(Message.RecipientType.TO,InternetAddress.parse(booking.getEmailId()));
		message.setSubject("Your Exam Link");
		String body = "";

		body = "Dear " + booking.getFirstname() +", <br>"
				+ " Here is your Exam Join Link for the " + booking.getSubject() 
				+ " exam at " + booking.getExamStartDateTime() + ". <br> "
				+ " Please click this <a href='" + booking.getJoinURL() + "'>Link</a> To join the exam."
				+ "<br>"
				+ "<br>";
		body = body + "<br>Thanks & Regards"
				+ "<br>"
				+ "NMIMS GLOBAL ACCESS SCHOOL FOR CONTINUING EDUCATION";
		message.setContent(body, "text/html; charset=utf-8");
		Transport.send(message);
		
		loggerForEmailCount.info("Total 1 Mail sent via(SMTP) from "+from+", Email Subject : Your Exam Link, for sendExamJoinMail");
	}

	public void sendRescheduleMailToStudent(TcsOnlineExamBean tcsOnlineExamBean)  {
		try {
		Properties props = new Properties();
		props.put("mail.smtp.auth", "true");
		props.put("mail.smtp.starttls.enable", "true");
		props.put("mail.smtp.host", host);
		props.put("mail.smtp.port", port);

		Session session = Session.getInstance(props,
				new javax.mail.Authenticator() {
			protected PasswordAuthentication getPasswordAuthentication() {
				return new PasswordAuthentication(username, password);
			}
		});
		Message message = new MimeMessage(session);
		message.setFrom(new InternetAddress(from, "NMIMS Global Access SCE"));
		message.setRecipients(Message.RecipientType.TO,InternetAddress.parse(tcsOnlineExamBean.getRegisteredEmailId()));
		message.setRecipients(Message.RecipientType.BCC,InternetAddress.parse("jforcesolutions@gmail.com,abhay.sakpal.ext@nmims.edu"));
		message.setSubject("Exam re-scheduled");
		String body = "";

		body = "Dear " + tcsOnlineExamBean.getFirstName()+", <br>"
				+ " Basis your request, your exam has been re-scheduled and updated details will reflect on the student portal." 
				+ " Subject name :  " + tcsOnlineExamBean.getSubject() + ". <br> "
				+ " New exam date :  " + tcsOnlineExamBean.getExamDate() + " <br> "
				+ " New exam time :  " + tcsOnlineExamBean.getExamTime() + " <br> "
				+ "<br>"
				+ "<br>";
		body = body + "<br>Thanks & Regards"
				+ "<br>"
				+ "NMIMS GLOBAL ACCESS SCHOOL FOR CONTINUING EDUCATION";
		message.setContent(body, "text/html; charset=utf-8");
		Transport.send(message);
		
		loggerForEmailCount.info("Total 1 Mail sent via(SMTP) from "+from+", Email Subject : Exam re-scheduled, for sendRescheduleMailToStudent");
		}catch(Exception e){
			
		}
	}

	public void sendExamJoinLinkStatusMail(List<MettlSSOInfoBean> successfulBookings, List<MettlSSOInfoBean> failedBookings) throws Exception {
		Properties props = new Properties();
		props.put("mail.smtp.auth", "true");
		props.put("mail.smtp.starttls.enable", "true");
		props.put("mail.smtp.host", host);
		props.put("mail.smtp.port", port);

		Session session = Session.getInstance(props,
				new javax.mail.Authenticator() {
			protected PasswordAuthentication getPasswordAuthentication() {
				return new PasswordAuthentication(username, password);
			}
		});
		Message message = new MimeMessage(session);
		message.setFrom(new InternetAddress(from, "NMIMS Global Access SCE"));
		message.setRecipients(Message.RecipientType.TO,InternetAddress.parse("abhay.sakpal.ext@nmims.edu, siddheshwar.khanse.ext@nmims.edu, shiv.golani.ext@nmims.edu, ganesh.kudtarkar.EXT@nmims.edu, swarup.rajpurohit.EXT@nmims.edu, saurabh.pawar.EXT@nmims.edu"));
		message.setSubject("Exam Join Links Scheduler Status Mailer.");
		
		String body = "Hello Team,"
				+ "<br>";;
		body += "<br> Sent mail successfully to " + successfulBookings.size() + " Students";
		if(failedBookings.size() > 0) {

			body += ""
					+ "<br>"
					+ "Below is the list of error emails list."
					+ "<br>"
					+ "<table>"
						+ "<thead>"
							+ "<tr>"
								+ "<th>#</th>"
								+ "<th>Sapid</th>"
								+ "<th>Email Id</th>"
								+ "<th>Mobile Number</th>"
								+ "<th>Subject</th>"
								+ "<th>Exam Start Time</th>"
								+ "<th>Exam End Time</th>"
								+ "<th>Exam Join Link</th>"
								+ "<th>Error Messagee</th>"
							+ "</tr>"
						+ "</thead>"
						+ "<tbody>";
			int count = 0;
			
			
			for (MettlSSOInfoBean booking : failedBookings) {
				count++;
				body += ""
					+ "<tr>"
						+ "<td>" + count + "</td>"
						+ "<td>" + booking.getSapid() + "</td>"
						+ "<td>" + booking.getEmailId() + "</td>"
						+ "<td>" + booking.getMobile() + "</td>"
						+ "<td>" + booking.getSubject() + "</td>"
						+ "<td>" + booking.getExamStartDateTime() + "</td>"
						+ "<td>" + booking.getExamEndDateTime() + "</td>"
						+ "<td>" + booking.getJoinURL() + "</td>"
						+ "<td>" + booking.getError() + "</td>"
					+ "</tr>";
			}
			body += ""
					+ "</tbody>"
				+ "</table>";
		} else {
			body += "<br> No Errors were found for this slot!";
		}
		
//		if(successfulBookings.size() > 0) {
//
//			body += ""
//					+ "<br>"
//					+ "Below is the list of success emails list."
//					+ "<br>"
//					+ "<table>"
//						+ "<thead>"
//							+ "<tr>"
//								+ "<th>#</th>"
//								+ "<th>Sapid</th>"
//								+ "<th>Email Id</th>"
//								+ "<th>Mobile Number</th>"
//								+ "<th>Subject</th>"
//								+ "<th>Exam Start Time</th>"
//								+ "<th>Exam End Time</th>"
//								+ "<th>Exam Join Link</th>"
//							+ "</tr>"
//						+ "</thead>"
//						+ "<tbody>";
//			int count = 0;
//			for (MettlSSOInfoBean booking : successfulBookings) {
//				count++;
//				body += ""
//					+ "<tr>"
//						+ "<td>" + count + "</td>"
//						+ "<td>" + booking.getSapid() + "</td>"
//						+ "<td>" + booking.getEmailId() + "</td>"
//						+ "<td>" + booking.getMobile() + "</td>"
//						+ "<td>" + booking.getSubject() + "</td>"
//						+ "<td>" + booking.getExamStartDateTime() + "</td>"
//						+ "<td>" + booking.getExamEndDateTime() + "</td>"
//						+ "<td>" + booking.getJoinURL() + "</td>"
//					+ "</tr>";
//			}
//			body += ""
//					+ "</tbody>"
//				+ "</table>";
//			
//		} else {
//			body += "<br> No Successful bookings were found for this slot!";
//		}
		

		message.setContent(body, "text/html; charset=utf-8");
		Transport.send(message);
		
		loggerForEmailCount.info("Total 1 Mail sent via(SMTP) from "+from+", Email Subject : Exam Join Links Scheduler Status Mailer, for sendExamJoinLinkStatusMail");
	}
	
	@Async
	public void sendTeeRescheduleExamBookingReport(String title,String fileNameAllReschedule, String fileNameNotToBeEvaluate ,String folderName,String[] toEmailId, String[] ccEmailId) {
		
		
		Properties props = new Properties();
		props.put("mail.smtp.auth", "true");
		props.put("mail.smtp.starttls.enable", "true");
		props.put("mail.smtp.host", host);
		props.put("mail.smtp.port", port);


		Session session = Session.getInstance(props,
				new javax.mail.Authenticator() {
			protected PasswordAuthentication getPasswordAuthentication() {
				return new PasswordAuthentication(username, password);
			}
		});

		try {

			MimeMessage message = new MimeMessage(session);
			
			MimeMessageHelper helper = new MimeMessageHelper(message, true);
			
			String body = "";
			body = "Dear Team, <br>"
					+"Please find attachment of "+title+". <br> <br>";
			body = body + "<br>Thanks & Regards"
					+ "<br>"
					+ "NMIMS GLOBAL ACCESS SCHOOL FOR CONTINUING EDUCATION";

			helper.setFrom(new InternetAddress(from, "NMIMS Global Access SCE"));
			helper.setTo(toEmailId);
			helper.setSubject(ENVIRONMENT+" : "+ title);
			helper.setText(body, true);
			helper.setBcc("abhay.sakpal.ext@nmims.edu");
			helper.setCc(ccEmailId);
			
			FileSystemResource fileNotToBeEvaluate = new FileSystemResource(folderName+fileNameNotToBeEvaluate);
			helper.addAttachment(fileNotToBeEvaluate.getFilename(), fileNotToBeEvaluate);
			
			FileSystemResource fileAllReschedule = new FileSystemResource(folderName+fileNameAllReschedule);
			helper.addAttachment(fileAllReschedule.getFilename(), fileAllReschedule);

			Transport.send(message);
			
			loggerForEmailCount.info("Total 1 Mail sent via(SMTP) from "+from+", Email Subject : "+title+", for sendTeeRescheduleExamBookingReport");
		} catch(Exception e) {
			logger.error("\n"+"IN sendTeeRescheduleExamBookingReport folderName :"+folderName+" fileName "+fileNameNotToBeEvaluate+" toEmailId / ccEmailId : "+Arrays.toString(toEmailId) +" / "+Arrays.toString(ccEmailId)+" Error Message : "+e.getMessage());
			
			
		}

	}
	
	public Properties fetchSMTPProperties() {
		Properties props = new Properties();
        
		props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "true");
        props.put("mail.smtp.host", host);
        props.put("mail.smtp.port", port);
        
        return props;
	}
	
	@Async
    public void emailReportRegisterCandidate(String emailBody, String examDate) throws Exception {
		Properties props = null;
		Session session = null;
		Message message = null;
		String subject = null;
		
        if(!"PROD".equalsIgnoreCase(ENVIRONMENT)) {
            return; 
        }
        
        try {
        	subject = "Register Candidate Report: ("+examDate+")";
        	props = fetchSMTPProperties();
        	
        	session = Session.getInstance(props, new javax.mail.Authenticator() {
                protected PasswordAuthentication getPasswordAuthentication() {
                    return new PasswordAuthentication(username, password);
                }
            });
        	
            message = new MimeMessage(session);
            message.setContent(emailBody, "text/html; charset=utf-8");
            message.setFrom(new InternetAddress(from, EMAILID_NGASCE_EXAMS));
            message.setReplyTo(InternetAddress.parse(EMAILID_NGASCE_EXAMS));

            message.setRecipients(Message.RecipientType.TO,InternetAddress.parse("vaibhav.dhariwal@mettl.com, aditi.singh@mettl.com, akash.singh@mettl.com, mihir.bhatia@mettl.com, nipun.agarwal@mettl.com, nachiketa.chandra@mettl.com"));
            message.setRecipients(Message.RecipientType.CC,InternetAddress.parse("harshad.kasliwal@nmims.edu, madhavi.upadrasta@nmims.edu, arif.sayed@nmims.edu, "+ EMAILID_SHIV + "," + EMAILID_VILPESH+ "," + EMAILID_GANESH+ "," + EMAILID_SWARUP));
            message.setRecipients(Message.RecipientType.BCC,InternetAddress.parse(EMAILID_JFORCE_SS + "," + EMAILID_SAGAR+ "," + EMAILID_ABHAY+ "," + EMAILID_SIDDHESWAR));
            message.setSubject(subject);

            Transport.send(message);
        } catch(Exception ex) {
        	logger.info("Sending Exception Email..."+ ex.getMessage());
        	message.setRecipients(Message.RecipientType.TO,InternetAddress.parse(EMAILID_SHIV + "," + EMAILID_VILPESH+ "," + EMAILID_GANESH+ "," + EMAILID_SWARUP));
            message.setRecipients(Message.RecipientType.BCC,InternetAddress.parse(EMAILID_JFORCE_SS + "," + EMAILID_SAGAR+ "," + EMAILID_ABHAY+ "," + EMAILID_SIDDHESWAR));
        	message.setSubject("ERROR! "+subject);
        	message.setContent(ex.getMessage(), "text/html; charset=utf-8");
            Transport.send(message);
            
            loggerForEmailCount.info("Total 1 Mail sent via(SMTP) from "+from+", Email Subject : Register Candidate Report, for emailReportRegisterCandidate");
        } finally {
        	message = null;
        	session = null;
        	props = null;
        }
    }
	
	@Async
    public void emailAttemptStatusForSchedule(String emailBody,String todayDate) throws Exception {
		Properties props = null;
		Session session = null;
		Message message = null;
		String subject = null;
		
        if(!"PROD".equalsIgnoreCase(ENVIRONMENT)) {
            return; 
        }
        
        try {
        	subject = "Canidate Attempt Status: ("+todayDate+")";
        	props = fetchSMTPProperties();
        	
        	session = Session.getInstance(props, new javax.mail.Authenticator() {
                protected PasswordAuthentication getPasswordAuthentication() {
                    return new PasswordAuthentication(username, password);
                }
            });
        	
            message = new MimeMessage(session);
            message.setContent(emailBody, "text/html; charset=utf-8");
            message.setFrom(new InternetAddress(from, EMAILID_NGASCE_EXAMS));
            message.setReplyTo(InternetAddress.parse(EMAILID_NGASCE_EXAMS));

            //message.setRecipients(Message.RecipientType.TO,InternetAddress.parse("vaibhav.dhariwal@mettl.com, aditi.singh@mettl.com, akash.singh@mettl.com, mihir.bhatia@mettl.com, nipun.agarwal@mettl.com, nachiketa.chandra@mettl.com"));
            //message.setRecipients(Message.RecipientType.CC,InternetAddress.parse("harshad.kasliwal@nmims.edu, madhavi.upadrasta@nmims.edu, arif.sayed@nmims.edu, "+ EMAILID_SHIV + "," + EMAILID_VILPESH+ "," + EMAILID_GANESH+ "," + EMAILID_SWARUP));
            message.setRecipients(Message.RecipientType.TO,InternetAddress.parse(MBAWX_ATTEMPT_STATUS_MAIL_TO));
            message.setRecipients(Message.RecipientType.CC,InternetAddress.parse(MBAWX_ATTEMPT_STATUS_MAIL_CC));
            message.setRecipients(Message.RecipientType.BCC,InternetAddress.parse(MBAWX_ATTEMPT_STATUS_MAIL_BCC));
            message.setSubject(subject);

            Transport.send(message);
            pullTimeBoundMettlMarksLogger.info("Candidate Attempt Status mail sent");
        } catch(Exception ex) {
        	pullTimeBoundMettlMarksLogger.error("Error is:"+ex.getMessage());
        	message.setRecipients(Message.RecipientType.TO,InternetAddress.parse(MBAWX_ATTEMPT_STATUS_MAIL_TO));
            message.setRecipients(Message.RecipientType.BCC,InternetAddress.parse(MBAWX_ATTEMPT_STATUS_MAIL_BCC));
        	message.setSubject("ERROR! "+subject);
        	message.setContent(ex.getMessage(), "text/html; charset=utf-8");
            Transport.send(message);
            
        } finally {
        	message = null;
        	session = null;
        	props = null;
        }
    }
	

	@Async
    public void sendUFMShowCauseMailer(UFMNoticeBean bean) throws Exception {
        
		if(!"PROD".equalsIgnoreCase(ENVIRONMENT)){
            return; 
        }
        
        Properties props = new Properties();
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "true");
        props.put("mail.smtp.host", host);
        props.put("mail.smtp.port", port);
        Session session = Session.getInstance(props, new javax.mail.Authenticator() {
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(username, password);
            }
        });
        
        try {
            Message message = new MimeMessage(session);
            message.setFrom(new InternetAddress(from, "NMIMS Global Access SCE"));
            String subject = "Show Cause Notice Issued";
            try {
                message.setRecipients(Message.RecipientType.TO,InternetAddress.parse(bean.getEmailId()));
                message.setSubject(subject);
            }catch(Exception e) {
            	ufm.info("ERROR : " + subject + " , sapid : "+bean.getSapid());
                message.setRecipients(Message.RecipientType.TO,InternetAddress.parse("jforce.solution@gmail.com, abhay.sakpal.ext@nmims.edu"));
                message.setSubject("ERROR : " + subject + " , sapid : "+bean.getSapid());
            }
            
            SimpleDateFormat sdfIn = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
    		String showCauseDeadline = "" + bean.getShowCauseDeadline();
    		Date date = sdfIn.parse(showCauseDeadline);
    		SimpleDateFormat sdfOut = new SimpleDateFormat("dd MMMM, HH:mm");
    		showCauseDeadline = sdfOut.format(date);
    		
    		SimpleDateFormat sdf = new SimpleDateFormat("MMM yyyy");
            String examMonthYear = bean.getMonth()+" "+bean.getYear() ;
            Date d = sdf.parse(examMonthYear);
            SimpleDateFormat sdfformate = new SimpleDateFormat("MMMM yyyy");
            examMonthYear = sdfformate.format(d);
    		
            //If student replies it should go to Exam Email ID
            message.setReplyTo(InternetAddress.parse("ngasce.exams@nmims.edu"));
            
            //Body of this email updated by shivam.pandey.EXT
            String body = ""
            		+ "Dear Student,"
            		+ "<br><br>"
                    + "During "+examMonthYear+" exam cycle it was found that you were involved in unfair means / Breach code of conduct, owing to this we have issued you a show cause notice.</br>" 
            		+ "To access this, kindly login to Student portal, click on the message you will see on the Dashboard, you will be navigated to download the show cause notice.<br><br>"
                    + "Kindly provide explanation in the space provided on or before "+showCauseDeadline+" hrs, failure to which Unfair means committee will implement the decision which will be communicated to you.<br><br>"
                    + "Post your submission on explanation provided, final decision will be communicated within two weeks, until then results for the subject/’s mentioned will be display as RIA (Result in abeyance) "
                    + "<br><br>"
                    + "Thanks & Regards<br>"
                    + "NMIMS GLOBAL ACCESS SCHOOL FOR CONTINUING EDUCATION";
                message.setContent(body, "text/html; charset=utf-8");
                Transport.send(message);
                
                loggerForEmailCount.info("to emailId "+bean.getEmailId()+" sapid "+bean.getSapid()+" body " + body);
                loggerForEmailCount.info("Total 1 Mail sent via(SMTP) from " + from + ", Email Subject :" + subject);
        } catch(Exception e) {
        	ufm.info("ERROR for sapid : "+bean.getSapid()+" is "+e.getMessage());
        }
    }
	
	@Async
    public void sendUFMDecisionMailer(UFMNoticeBean bean) throws Exception {
        
        if(!"PROD".equalsIgnoreCase(ENVIRONMENT)){
            return; 
        }
        
        Properties props = new Properties();
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "true");
        props.put("mail.smtp.host", host);
        props.put("mail.smtp.port", port);
        Session session = Session.getInstance(props, new javax.mail.Authenticator() {
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(username, password);
            }
        });
        
        try {
            Message message = new MimeMessage(session);
            message.setFrom(new InternetAddress(from, "NMIMS Global Access SCE"));

            String subject = "UFM Decision Issued";
            try {
                message.setRecipients(Message.RecipientType.TO,InternetAddress.parse(bean.getEmailId()));
                message.setSubject(subject);
            }catch(Exception e) {   	
            	ufm.info("ERROR : " + subject + " , sapid : "+bean.getSapid());
	            message.setRecipients(Message.RecipientType.TO,InternetAddress.parse("jforce.solution@gmail.com, ashutosh.sultania.ext@nmims.edu"));
	            message.setSubject("ERROR : " + subject + " , sapid : "+bean.getSapid());
            }
            
        	SimpleDateFormat sdfIn = new SimpleDateFormat("yyyy-MM-dd");
        	String showCauseGenerationDate = bean.getShowCauseGenerationDate() ;
    		Date date = sdfIn.parse(bean.getShowCauseGenerationDate());
    		SimpleDateFormat sdfOut = new SimpleDateFormat("dd MMMM,");
    		showCauseGenerationDate = sdfOut.format(date);
            
            //If student replies it should go to Exam Email ID
            message.setReplyTo(InternetAddress.parse("ngasce.exams@nmims.edu"));
    
          //Body of this email updated by shivam.pandey.EXT
            String body = ""
        		+ "Dear Student, <br>"
        		+ "Basis your explanation on show cause issued on "+showCauseGenerationDate+" decision is uploaded on student portal, "
        		+ "to access the same navigate via link provided on Student portal. The decision of the UFM committee is "
        		+ "final and binding on the student. <br><br>"
        		+ "Thanks & Regards<br>"
                + "NMIMS GLOBAL ACCESS SCHOOL FOR CONTINUING EDUCATION";

            message.setContent(body, "text/html; charset=utf-8");
            Transport.send(message);

            loggerForEmailCount.info("Total 1 Mail sent via(SMTP) from " + from + ", Email Subject :" + subject);
        } catch(Exception e) {
        	ufm.info("ERROR for sapid "+bean.getSapid()+" is:"+e.getMessage());
        }
    }
	/**
	 * shivam.pandey.EXT COC - START
	 */
	//To Generate COC Show Cause File Uploaded Mail
	@Async
    public void sendCOCShowCauseMailer(UFMNoticeBean bean) throws Exception {
        
		if(!"PROD".equalsIgnoreCase(ENVIRONMENT)){
            return; 
        }
        
        Properties props = new Properties();
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "true");
        props.put("mail.smtp.host", host);
        props.put("mail.smtp.port", port);
        Session session = Session.getInstance(props, new javax.mail.Authenticator() {
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(username, password);
            }
        });
        
        try {
            Message message = new MimeMessage(session);
            message.setFrom(new InternetAddress(from, "NMIMS Global Access SCE"));
            String subject = "Show Cause Notice Issued";
            try {
                message.setRecipients(Message.RecipientType.TO,InternetAddress.parse(bean.getEmailId()));
                message.setSubject(subject);
            }catch(Exception e) {
            	ufm.info("ERROR : " + subject + " , sapid : "+bean.getSapid());
                message.setRecipients(Message.RecipientType.TO,InternetAddress.parse("jforce.solution@gmail.com, abhay.sakpal.ext@nmims.edu"));
                message.setSubject("ERROR : " + subject + " , sapid : "+bean.getSapid());
            }
            
            SimpleDateFormat sdfIn = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
    		String showCauseDeadline = "" + bean.getShowCauseDeadline();
    		Date date = sdfIn.parse(showCauseDeadline);
    		SimpleDateFormat sdfOut = new SimpleDateFormat("dd MMMM, HH:mm");
    		showCauseDeadline = sdfOut.format(date);
    		
    		SimpleDateFormat sdf = new SimpleDateFormat("MMM yyyy");
            String examMonthYear = bean.getMonth()+" "+bean.getYear() ;
            Date d = sdf.parse(examMonthYear);
            SimpleDateFormat sdfformate = new SimpleDateFormat("MMMM yyyy");
            examMonthYear = sdfformate.format(d);
    		
            //If student replies it should go to Exam Email ID
            message.setReplyTo(InternetAddress.parse("ngasce.exams@nmims.edu"));

            String body = ""
            		+ "Dear Student,"
            		+ "<br><br>"
                    + "During "+examMonthYear+" exam cycle it was found that you were involved in unfair means / Breach code of conduct, owing to this we have issued you a show cause notice.</br>" 
            		+ "To access this, kindly login to Student portal, click on the message you will see on the Dashboard, you will be navigated to download the show cause notice.<br><br>"
                    + "Kindly provide explanation in the space provided on or before "+showCauseDeadline+" hrs, failure to which Unfair means committee will implement the decision which will be communicated to you.<br><br>"
                    + "Post your submission on explanation provided, final decision will be communicated within two weeks, until then results for the subject/’s mentioned will be display as RIA (Result in abeyance) "
                    + "<br><br>"
                    + "Thanks & Regards<br>"
                    + "NMIMS GLOBAL ACCESS SCHOOL FOR CONTINUING EDUCATION";
                message.setContent(body, "text/html; charset=utf-8");
                Transport.send(message);
                
                loggerForEmailCount.info("to emailId "+bean.getEmailId()+" sapid "+bean.getSapid()+" body " + body);
                loggerForEmailCount.info("Total 1 Mail sent via(SMTP) from " + from + ", Email Subject :" + subject);
        } catch(Exception e) {
        	ufm.info("ERROR for sapid : "+bean.getSapid()+" is "+e.getMessage());
        }
    }
	
	//To Generate COC Action File Uploaded Mail
	@Async
    public void sendCOCDecisionMailer(UFMNoticeBean bean) throws Exception {
        
        if(!"PROD".equalsIgnoreCase(ENVIRONMENT)){
            return; 
        }
        
        Properties props = new Properties();
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "true");
        props.put("mail.smtp.host", host);
        props.put("mail.smtp.port", port);
        Session session = Session.getInstance(props, new javax.mail.Authenticator() {
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(username, password);
            }
        });
        
        try {
            Message message = new MimeMessage(session);
            message.setFrom(new InternetAddress(from, "NMIMS Global Access SCE"));

            String subject = "UFM Decision Issued";
            try {
                message.setRecipients(Message.RecipientType.TO,InternetAddress.parse(bean.getEmailId()));
                message.setSubject(subject);
            }catch(Exception e) {   	
            	ufm.info("ERROR : " + subject + " , sapid : "+bean.getSapid());
	            message.setRecipients(Message.RecipientType.TO,InternetAddress.parse("jforce.solution@gmail.com, ashutosh.sultania.ext@nmims.edu"));
	            message.setSubject("ERROR : " + subject + " , sapid : "+bean.getSapid());
            }
            
        	SimpleDateFormat sdfIn = new SimpleDateFormat("yyyy-MM-dd");
        	String showCauseGenerationDate = bean.getShowCauseGenerationDate() ;
    		Date date = sdfIn.parse(bean.getShowCauseGenerationDate());
    		SimpleDateFormat sdfOut = new SimpleDateFormat("dd MMMM,");
    		showCauseGenerationDate = sdfOut.format(date);
            
            //If student replies it should go to Exam Email ID
            message.setReplyTo(InternetAddress.parse("ngasce.exams@nmims.edu"));
    
            String body = ""
            		+ "Dear Student, <br>"
            		+ "Basis your explanation on show cause issued on "+showCauseGenerationDate+" decision is uploaded on student portal, "
            		+ "to access the same navigate via link provided on Student portal. The decision of the UFM committee is "
            		+ "final and binding on the student. <br><br>"
            		+ "Thanks & Regards<br>"
                    + "NMIMS GLOBAL ACCESS SCHOOL FOR CONTINUING EDUCATION";

            message.setContent(body, "text/html; charset=utf-8");
            Transport.send(message);

            loggerForEmailCount.info("Total 1 Mail sent via(SMTP) from " + from + ", Email Subject :" + subject);
        } catch(Exception e) {
        	ufm.info("ERROR for sapid "+bean.getSapid()+" is:"+e.getMessage());
        }
    }
	/**
	 * shivam.pandey.EXT COC - END
	 */
	
	
	
	/**
	 * shivam.pandey.EXT Disconnect Above - START
	 */
	//To Generate Disconnect Above Show Cause File Uploaded Mail
	@Async
    public void sendDisconnectAboveShowCauseMailer(UFMNoticeBean bean) throws Exception {
        
		if(!"PROD".equalsIgnoreCase(ENVIRONMENT)){
            return; 
        }
        
        Properties props = new Properties();
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "true");
        props.put("mail.smtp.host", host);
        props.put("mail.smtp.port", port);
        Session session = Session.getInstance(props, new javax.mail.Authenticator() {
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(username, password);
            }
        });
        
        try {
            Message message = new MimeMessage(session);
            message.setFrom(new InternetAddress(from, "NMIMS Global Access SCE"));
            String subject = "Show Cause Notice Issued";
            try {
                message.setRecipients(Message.RecipientType.TO,InternetAddress.parse(bean.getEmailId()));
                message.setSubject(subject);
            }catch(Exception e) {
            	ufm.info("ERROR : " + subject + " , sapid : "+bean.getSapid());
                message.setRecipients(Message.RecipientType.TO,InternetAddress.parse("jforce.solution@gmail.com, abhay.sakpal.ext@nmims.edu"));
                message.setSubject("ERROR : " + subject + " , sapid : "+bean.getSapid());
            }
            
            SimpleDateFormat sdfIn = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
    		String showCauseDeadline = "" + bean.getShowCauseDeadline();
    		Date date = sdfIn.parse(showCauseDeadline);
    		SimpleDateFormat sdfOut = new SimpleDateFormat("dd MMMM, HH:mm");
    		showCauseDeadline = sdfOut.format(date);
    		
    		SimpleDateFormat sdf = new SimpleDateFormat("MMM yyyy");
            String examMonthYear = bean.getMonth()+" "+bean.getYear() ;
            Date d = sdf.parse(examMonthYear);
            SimpleDateFormat sdfformate = new SimpleDateFormat("MMMM yyyy");
            examMonthYear = sdfformate.format(d);
    		
            //If student replies it should go to Exam Email ID
            message.setReplyTo(InternetAddress.parse("ngasce.exams@nmims.edu"));

            String body = ""
            		+ "Dear Students,"
            		+ "<br><br>"
                    + "During "+examMonthYear+" exam cycle it was brought to our attention that there were multiple disconnections during the term end exam, owing to this we have issued you a show cause notice.</br>" 
            		+ "To access this, kindly login to Student portal, click on the message you will see on the Dashboard, you will be navigated to download the show cause notice.<br><br>"
                    + "Kindly provide explanation in the space provided on or before "+showCauseDeadline+" hrs, failure to which University will implement the decision which will be communicated to you.<br><br>"
                    + "Post your submission on explanation provided, final decision will be communicated within two weeks, until then results for the subject/’s mentioned will be display as RIA (Result in abeyance) "
                    + "<br><br>"
                    + "Regards,<br>"
                    + "Team NGASCE";
                message.setContent(body, "text/html; charset=utf-8");
                Transport.send(message);
                
                loggerForEmailCount.info("to emailId "+bean.getEmailId()+" sapid "+bean.getSapid()+" body " + body);
                loggerForEmailCount.info("Total 1 Mail sent via(SMTP) from " + from + ", Email Subject :" + subject);
        } catch(Exception e) {
        	ufm.info("ERROR for sapid : "+bean.getSapid()+" is "+e.getMessage());
        }
    }
	
	//To Generate Disconnect Above Action File Uploaded Mail
	@Async
    public void sendDisconnectAboveDecisionMailer(UFMNoticeBean bean) throws Exception {
        
        if(!"PROD".equalsIgnoreCase(ENVIRONMENT)){
            return; 
        }
        
        Properties props = new Properties();
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "true");
        props.put("mail.smtp.host", host);
        props.put("mail.smtp.port", port);
        Session session = Session.getInstance(props, new javax.mail.Authenticator() {
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(username, password);
            }
        });
        
        try {
            Message message = new MimeMessage(session);
            message.setFrom(new InternetAddress(from, "NMIMS Global Access SCE"));

            String subject = "UFM Decision Issued";
            try {
                message.setRecipients(Message.RecipientType.TO,InternetAddress.parse(bean.getEmailId()));
                message.setSubject(subject);
            }catch(Exception e) {   	
            	ufm.info("ERROR : " + subject + " , sapid : "+bean.getSapid());
	            message.setRecipients(Message.RecipientType.TO,InternetAddress.parse("jforce.solution@gmail.com, ashutosh.sultania.ext@nmims.edu"));
	            message.setSubject("ERROR : " + subject + " , sapid : "+bean.getSapid());
            }
            
        	SimpleDateFormat sdfIn = new SimpleDateFormat("yyyy-MM-dd");
        	String showCauseGenerationDate = bean.getShowCauseGenerationDate() ;
    		Date date = sdfIn.parse(bean.getShowCauseGenerationDate());
    		SimpleDateFormat sdfOut = new SimpleDateFormat("dd MMMM,");
    		showCauseGenerationDate = sdfOut.format(date);
            
            //If student replies it should go to Exam Email ID
            message.setReplyTo(InternetAddress.parse("ngasce.exams@nmims.edu"));
    
            String body = ""
            		+ "Dear Student, <br>"
            		+ "Basis your explanation on show cause issued on "+showCauseGenerationDate+" decision is uploaded on student portal, "
            		+ "to access the same navigate via link provided on Student portal. The decision of the UFM committee is "
            		+ "final and binding on the student. <br><br>"
            		+ "Regards,<br>"
                    + "Team NGASCE";

            message.setContent(body, "text/html; charset=utf-8");
            Transport.send(message);

            loggerForEmailCount.info("Total 1 Mail sent via(SMTP) from " + from + ", Email Subject :" + subject);
        } catch(Exception e) {
        	ufm.info("ERROR for sapid "+bean.getSapid()+" is:"+e.getMessage());
        }
    }
	/**
	 * shivam.pandey.EXT Disconnect Above - END
	 */
	
	
	
	
	/**
	 * shivam.pandey.EXT Disconnect Below - START
	 */
	//To Generate Disconnect Below Show Cause File Uploaded Mail
	@Async
    public void sendDisconnectBelowShowCauseMailer(UFMNoticeBean bean) throws Exception {
        
		if(!"PROD".equalsIgnoreCase(ENVIRONMENT)){
            return; 
        }
        
        Properties props = new Properties();
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "true");
        props.put("mail.smtp.host", host);
        props.put("mail.smtp.port", port);
        Session session = Session.getInstance(props, new javax.mail.Authenticator() {
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(username, password);
            }
        });
        
        try {
            Message message = new MimeMessage(session);
            message.setFrom(new InternetAddress(from, "NMIMS Global Access SCE"));
            String subject = "Show Cause Notice Issued";
            try {
                message.setRecipients(Message.RecipientType.TO,InternetAddress.parse(bean.getEmailId()));
                message.setRecipients(Message.RecipientType.BCC,InternetAddress.parse("jforce.solutions@gmail.com"));
                message.setSubject(subject);
            }catch(Exception e) {
            	ufm.info("ERROR : " + subject + " , sapid : "+bean.getSapid());
                message.setRecipients(Message.RecipientType.TO,InternetAddress.parse("jforce.solution@gmail.com, abhay.sakpal.ext@nmims.edu"));
                message.setSubject("ERROR : " + subject + " , sapid : "+bean.getSapid());
            }
            
            SimpleDateFormat sdfIn = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
    		String showCauseDeadline = "" + bean.getShowCauseDeadline();
    		Date date = sdfIn.parse(showCauseDeadline);
    		SimpleDateFormat sdfOut = new SimpleDateFormat("dd MMMM, HH:mm");
    		showCauseDeadline = sdfOut.format(date);
    		
    		SimpleDateFormat sdf = new SimpleDateFormat("MMM yyyy");
            String examMonthYear = bean.getMonth()+" "+bean.getYear() ;
            Date d = sdf.parse(examMonthYear);
            SimpleDateFormat sdfformate = new SimpleDateFormat("MMMM yyyy");
            examMonthYear = sdfformate.format(d);
    		
            //If student replies it should go to Exam Email ID
            message.setReplyTo(InternetAddress.parse("ngasce.exams@nmims.edu"));

            String body = ""
            		+ "Dear Students,"
            		+ "<br><br>"
                    + "During "+examMonthYear+" exam cycle it was brought to our attention that there were multiple disconnections during the term end exam, owing to this we have issued you a show cause notice.</br>" 
            		+ "To access this, kindly login to Student portal, click on the message you will see on the Dashboard, you will be navigated to download the show cause notice.<br><br>"
                    + "Kindly provide explanation in the space provided on or before "+showCauseDeadline+" hrs, failure to which University will implement the decision which will be communicated to you.<br><br>"
                    + "Post your submission on explanation provided, final decision will be communicated within two weeks, until then results for the subject/’s mentioned will be display as RIA (Result in abeyance) "
                    + "<br><br>"
                    + "Regards,<br>"
                    + "Team NGASCE";
                message.setContent(body, "text/html; charset=utf-8");
                Transport.send(message);
                
                loggerForEmailCount.info("to emailId "+bean.getEmailId()+" sapid "+bean.getSapid()+" body " + body);
                loggerForEmailCount.info("Total 1 Mail sent via(SMTP) from " + from + ", Email Subject :" + subject);
        } catch(Exception e) {
        	ufm.info("ERROR for sapid : "+bean.getSapid()+" is "+e.getMessage());
        }
    }
	
	//To Generate Disconnect Below Action File Uploaded Mail
	@Async
    public void sendDisconnectBelowDecisionMailer(UFMNoticeBean bean) throws Exception {
        
		if(!"PROD".equalsIgnoreCase(ENVIRONMENT)){
            return; 
        }
        
        Properties props = new Properties();
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "true");
        props.put("mail.smtp.host", host);
        props.put("mail.smtp.port", port);
        Session session = Session.getInstance(props, new javax.mail.Authenticator() {
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(username, password);
            }
        });
        
        try {
            Message message = new MimeMessage(session);
            message.setFrom(new InternetAddress(from, "NMIMS Global Access SCE"));

            String subject = "UFM Decision Issued";
            try {
                message.setRecipients(Message.RecipientType.TO,InternetAddress.parse(bean.getEmailId()));
                message.setRecipients(Message.RecipientType.BCC,InternetAddress.parse("jforce.solutions@gmail.com"));
                message.setSubject(subject);
            }catch(Exception e) {   	
            	ufm.info("ERROR : " + subject + " , sapid : "+bean.getSapid());
	            message.setRecipients(Message.RecipientType.TO,InternetAddress.parse("jforce.solution@gmail.com, ashutosh.sultania.ext@nmims.edu"));
	            message.setSubject("ERROR : " + subject + " , sapid : "+bean.getSapid());
            }
            
        	SimpleDateFormat sdfIn = new SimpleDateFormat("yyyy-MM-dd");
        	String showCauseGenerationDate = bean.getShowCauseGenerationDate() ;
    		Date date = sdfIn.parse(bean.getShowCauseGenerationDate());
    		SimpleDateFormat sdfOut = new SimpleDateFormat("dd MMMM,");
    		showCauseGenerationDate = sdfOut.format(date);
            
            //If student replies it should go to Exam Email ID
            message.setReplyTo(InternetAddress.parse("ngasce.exams@nmims.edu"));
    
            String body = ""
            		+ "Dear Students,"
            		+ "<br><br>"
            		+ "During "+bean.getMonth()+" "+bean.getYear()+" exam cycle it was brought to our attention that there were multiple disconnections during the term end exam, "
            		+ "owing to this we have issued you a Warning notice. To access this, kindly login to Student portal, click on the message you "
            		+ "will see on the Dashboard, you will be navigated to download the Warning notice."
            		+ "<br><br>"
            		+ "This notice serves as a reminder that you are responsible for ensuring a stable internet connection and appropriate technology setup before starting an exam. "
            		+ "<br><br>"
            		+ "We urge you to take the necessary steps to prevent disconnections during future exams. If you have any issues or concerns, "
            		+ "you have an option of appearing for your exams from the University campuses. "
            		+ "<br>"
            		+ "We hope that you will take this notice as a formal �Warning� and take necessary steps to ensure that such incidents do not occur in the future. "
            		+ "<br><br>"
            		+ "Regards,<br>"
                    + "Team NGASCE";

            message.setContent(body, "text/html; charset=utf-8");
            Transport.send(message);

            loggerForEmailCount.info("Total 1 Mail sent via(SMTP) from " + from + ", Email Subject :" + subject);
        } catch(Exception e) {
        	ufm.info("ERROR for sapid "+bean.getSapid()+" is:"+e.getMessage());
        }
    }
	/**
	 * shivam.pandey.EXT Disconnect Below - END
	 */
	
	
	
	//added to send demo exam mail after registration
	@Async
    public void sendEmailForDemoExamReminderAfterRegistration(StudentExamBean student) throws Exception {
		
		  if(!"PROD".equalsIgnoreCase(ENVIRONMENT)){ 
			  return; 
			  }
		 
        
        Properties props = new Properties();
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "true");
        props.put("mail.smtp.host", host);
        props.put("mail.smtp.port", port);
        Session session = Session.getInstance(props, new javax.mail.Authenticator() {
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(username, password);
            }
        });
        
        try {
            Message message = new MimeMessage(session);
            message.setFrom(new InternetAddress(from, "NMIMS Global Access SCE"));
    
            message.setRecipients(Message.RecipientType.TO,InternetAddress.parse(student.getEmailId()));
            message.setRecipients(Message.RecipientType.BCC,InternetAddress.parse("jforce.solution@gmail.com"));
            message.setSubject("IMPORATANT NOTIFICATION! Compatibility Test and Demo Exams Are Mandatory! ");
          
            
            //If student replies it should go to Exam Email ID
            message.setReplyTo(InternetAddress.parse("ngasce.exams@nmims.edu"));
    
                String body = "";
    
                body =  " Dear Student, <br>"
                		+ "<br>"
                       +" Greetings from NMIMS Global Access School for Continuing Education (NGASCE)! <br><br>"
                       +" Thank you for registering for your exams! <b>System Compatibility check and Demo exams</b> are MANDATORY and must be completed prior to the start of your exams.  "
                       + "Compatibility tests on the new computer are likewise critical and imperative, in order to avoid any system issues during the exams.<br><br>"
                       +" <b> Please take Note: -</b>   <br><br>"
                       + "The University keeps track of and monitors all the Demo Exams for reference. " + 
                       "Failure to complete the Demo test may result in technical difficulties, for which the University will not grant any additional time to finish your exams. <br><br>"
                       +"Please  take a note  of the  test link <a href='https://tests.mettl.com/system-check?i=db696a8e#/systemCheck'>Demo Exam test link</a> <br> <br>"
					   + "Looking forward for your cooperation.  " 
                       +" <br>";
    
                body = body + "<br>All The Best!"
                            + "<br>"
                            + "NGASCE Examination Team!";
    
                message.setContent(body, "text/html; charset=utf-8");
                Transport.send(message);
                
                demo_exam_mailer_logger.info(" Mail sent via(SMTP) from "+from+", To emaild :" +student.getEmailId()+" sapid "+student.getSapid()+",Email Subject : Demo exam - Not attempted, for sendEmailForDemoExamReminderAfterRegistration");
        } catch(Exception e) {
        	demo_exam_mailer_logger.error("Error for sapid :" +student.getSapid()+ "and  emaild :" +student.getEmailId()+ ",Message:"  + e.getMessage());
        }
    }
	//added to send reminder mail for demo exam by manasi
	@Async
    public void sendEmailForDemoExamReminderAfterFirstMail (StudentExamBean student,String year,String Month) throws Exception {
        
		
		  if(!"PROD".equalsIgnoreCase(ENVIRONMENT))
		  { 
			  return; 
		  }
		 
        
        Properties props = new Properties();
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "true");
        props.put("mail.smtp.host", host);
        props.put("mail.smtp.port", port);
        Session session = Session.getInstance(props, new javax.mail.Authenticator() {
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(username, password);
            }
        });
        
        try {
            Message message = new MimeMessage(session);
            message.setFrom(new InternetAddress(from, "NMIMS Global Access SCE"));
    
            message.setRecipients(Message.RecipientType.TO,InternetAddress.parse(student.getEmailId()));
            message.setRecipients(Message.RecipientType.BCC,InternetAddress.parse("jforce.solution@gmail.com"));
            message.setSubject("REMINDER NOTIFICATION! Complete your Compatibility Test and Demo Exams! ");
           
            
            //If student replies it should go to Exam Email ID
            message.setReplyTo(InternetAddress.parse("ngasce.exams@nmims.edu"));
    
                String body = "";
    
                body =  " Dear Student, <br><br>"
                	    +" Greetings from NMIMS Global Access School for Continuing Education (NGASCE)! <br><br>"     
                	    +" It is noticed that you have NOT yet completed the <b>System Compatibility check and Demo exams</b> as mandated by the University. <br> <br>"
                	    +" Kindly note that System Compatibility check and Demo Exams are required prior to beginning your exams and it is MANDATORY.  "
                	    + "Compatibility tests on the <b>new computer</b> are likewise critical and imperative, in order to avoid any system issues during the exams.  <br> <br>"
                	    +"Please be aware that the University records and monitors all the Demo Exams for future reference. <br><br>"
                	    +"Failure to complete the Demo test may result in technical difficulties, for which the University will not grant any additional time to finish your exams. <br><br> "
                        +"Please  take a note  of the  test link  <a href='https://tests.mettl.com/system-check?i=db696a8e#/systemCheck'>Demo Exam</a>"
                        +" <br>";
    
                body = body + "<br>All The Best! "
                            + "<br>"
                            + "NGASCE Examination Team! ";
    
                message.setContent(body, "text/html; charset=utf-8");
                Transport.send(message);
                
                demo_exam_mailer_logger.info("Mail sent via(SMTP) from "+from+", To "+student.getSapid()+"and email id "+student.getEmailId()+ ",year and month " + year,Month + ",Email Subject : Demo exam - Not attempted, for sendEmailForDemoExamReminderAndSystemCompatibilitycheck");
        } catch(Exception e) {
           
        	demo_exam_mailer_logger.error("Error for sapid :" +student.getSapid()+ "and  emaild :" +student.getEmailId()+ ",Message:"  + e.getMessage());
        }
    }

	
	public void sendEmailForDemoExamReminderAfterFirstMail(List<StudentExamBean> studentList) throws Exception {
        
        if(!"PROD".equalsIgnoreCase(ENVIRONMENT)){
            return; 
        }
        
        String emailIds = "";
        String subject = "REMINDER NOTIFICATION! Complete your Compatibility Test and Demo Exams! ";
		String fromEmailId = "donotreply-ngasce@nmims.edu";
		String fromName = "NMIMS Global Access SCE";
        try {
        	String body = "";
            
            body =  " Dear Student, <br><br>"
            	    +" Greetings from NMIMS Global Access School for Continuing Education (NGASCE)! <br><br>"     
            	    +" It is noticed that you have NOT yet completed the <b>System Compatibility check and Demo exams</b> as mandated by the University. <br> <br>"
            	    +" Kindly note that System Compatibility check and Demo Exams are required prior to beginning your exams and it is MANDATORY.  "
            	    + "Compatibility tests on the <b>new computer</b> are likewise critical and imperative, in order to avoid any system issues during the exams.  <br> <br>"
            	    +"Please be aware that the University records and monitors all the Demo Exams for future reference. <br><br>"
            	    +"Failure to complete the Demo test may result in technical difficulties, for which the University will not grant any additional time to finish your exams. <br><br> "
                    +"Please  take a note  of the  test link  <a href='https://tests.mettl.com/system-check?i=db696a8e#/systemCheck'>Demo Exam</a>"
                    +" <br>";

            body = body + "<br>All The Best! "
                        + "<br>"
                        + "NGASCE Examination Team! ";
             
            ArrayList<String> toEmailIds = new ArrayList<>();
            ArrayList<String> toSapIds = new ArrayList<>();
			for (StudentExamBean studentBean : studentList) {
				toEmailIds.add(studentBean.getEmailId());
				toSapIds.add(studentBean.getSapid());//Add SAPID to list
			}
			
			toEmailIds.add("jforce.solution@gmail.com");
			
			tnsMailSender.sendMassEmail(toSapIds, toEmailIds, fromEmailId, fromName, subject, body);
			
		} catch (Exception e) {
			String body = "Error in sending email "+e.getMessage() + " <br>";
			body = body + "Error while sending emails to "+emailIds;
			String encodedBody = new String(Base64.encodeBase64(body.getBytes()));
			
			//tnsMailSender.sendTNSEMail(encodedBody, "[\"Somesh.Turde.Ext@nmims.edu\", \"Shiv.Golani.Ext@nmims.edu\", \"jforce.solution@gmail.com\", \"mansi.thorat.EXT@nmims.edu\"]", "Error in sending emails for "+subject);
			HashMap<String, String> payload =  new HashMap<String, String>();
			payload.put("provider", "AWSSES");
			payload.put("htmlBody", encodedBody);
			payload.put("subject", "Error in sending emails for "+subject);
			payload.put("from", "donotreply-ngasce@nmims.edu");
			payload.put("email", "Somesh.Turde.Ext@nmims.edu, Shiv.Golani.Ext@nmims.edu, jforce.solution@gmail.com, mansi.thorat.EXT@nmims.edu");
			payload.put("fromName", "NMIMS Global Access SCE");
			emailHelper.sendAmazonSESDynamicMail(payload);
			demo_exam_mailer_logger.error("Mail sent via(TNMails) from "+from+", Email Subject : "+subject+", for sendEmailToExamTomorrow");
			
		}

    }

	@Async
	public void sendSpecialisationSelectionSummary (StudentExamBean student, List<Specialisation> specialization) {

//		if(!"PROD".equalsIgnoreCase(ENVIRONMENT)){
//			return;
//		}

		Properties props = new Properties();
		props.put("mail.smtp.auth", "true");
		props.put("mail.smtp.starttls.enable", "true");
		props.put("mail.smtp.host", host);
		props.put("mail.smtp.port", port);

		Session session = Session.getInstance(props, new javax.mail.Authenticator() {
			protected PasswordAuthentication getPasswordAuthentication() {
				return new PasswordAuthentication(username, password);
			}
		});

		try {
			Message message = new MimeMessage(session);
			message.setFrom(new InternetAddress(from, "NMIMS Global Access SCE"));
			message.setRecipients(Message.RecipientType.TO,InternetAddress.parse(student.getEmailId()));
			message.setRecipients(Message.RecipientType.BCC, InternetAddress.parse("jforcesolutions@gmail.com"));

			message.setSubject("Your Electives subject Summary");

			String body = "Dear " + student.getFirstName() +", <br>"
					+ "Here is your Electives Specialisation Summary. <br>"
					+ "Student ID: "+student.getSapid() +"<br>"
					+ "Program: "+student.getProgram()+"<br>"
					+ "<br>";

			body = body
					+ "<table border=\"1\">"
					+ "<thead>"
					+ "<tr> "
					+ "<th>Sr. No.</th>"
					+ "<th>Subject</th>"
					+ "<th>Sem</th>"
					+ "<th>Specialisation</th>"
					+ "</tr>"
					+ "</thead>"
					+ "<tbody>";

			int count = 1;

			for (Specialisation subject : specialization) {
				
				specializationLogger.info(subject.getSubject()+"~"+subject.getSem()+"~"+subject.getSpecialization());
				
				body =  body 
						+ " <tr> "
						+ " <td align=\"center\"> " + (count++) +"</td> "
						+ "	<td> " + subject.getSubject() + " </td> "
						+ "	<td align=\"center\"> " + subject.getSem() +"</td> "
						+ "	<td> " + subject.getSpecialization()+"</td> "
						+ " </tr> ";
			}

			body = body + " </tbody> "
					+ " </table>"
					+ " <br>Thanks & Regards"
					+ " <br>"
					+ " NMIMS GLOBAL ACCESS SCHOOL FOR CONTINUING EDUCATION";

			message.setContent(body, "text/html; charset=utf-8");
//			Transport.send(message);

			loggerForEmailCount.info("Total 1 Mail sent via(SMTP) from "+from+", Email Subject : Your Electives subject Summary, for sendSpecialisationSubjectSummary");

		} catch (Exception e) {

		}
	}
	
	@Async
	public void sendFinalAssessmentTestEndedEmailForLeads(StudentExamBean student, TestExamBean test,StudentsTestDetailsExamBean studentsTestDetails) {
		
		// uncommemt when testing is over
		  if(!"PROD".equalsIgnoreCase(ENVIRONMENT)){
			return;
		}
		  
		String emailSubject = "Final Assessment Test Completed for "+test.getSubject() + " - "+test.getTestName() + ".";
		String body = "";
		
		try {
			Properties props = new Properties();
			props.put("mail.smtp.auth", "true");
			props.put("mail.smtp.starttls.enable", "true");
			props.put("mail.smtp.host", host);
			props.put("mail.smtp.port", port);

			Session session = Session.getInstance(props,
					new javax.mail.Authenticator() {
				protected PasswordAuthentication getPasswordAuthentication() {
					return new PasswordAuthentication(username, password);
				}
			});

			try {
				SimpleDateFormat formatter = new SimpleDateFormat("dd-MMM-yyyy HH:mm:ss");

				Message message = new MimeMessage(session);
				message.setFrom(new InternetAddress(from, "NMIMS Global Access SCE"));
				try {
					//message.setRecipients(Message.RecipientType.TO,InternetAddress.parse("jforcesolutions@gmail.com"));
					
					message.setSubject(emailSubject);
					message.setRecipients(Message.RecipientType.BCC,InternetAddress.parse("jforcesolutions@gmail.com"));
					message.setRecipients(Message.RecipientType.TO,InternetAddress.parse(student.getEmailId()));
					
				} catch (Exception e) {
					message.setRecipients(Message.RecipientType.CC,InternetAddress.parse("pranit.shirke.ext@nmims.edu"));
					message.setSubject("Error: Student Email Not Available : Test Completed for "+test.getSubject());
					logger.info("\n"+"IN sendTestEndedEmail Invalid Student Email got sapid: "+studentsTestDetails.getSapid()+" testId: "+test.getId()+" Error: "+e.toString());
					logger.info(" "+e);
				}
				//If student replies it should go to Exam Email ID
				message.setReplyTo(InternetAddress.parse("ngasce.exams@nmims.edu"));
				
				body = "Dear Student, <br><br>"
						+"Your final assessment for the following subject was successfully submitted. <br><br>"
						+"Student Name: "+student.getFirstName().toUpperCase() + " " + student.getLastName().toUpperCase()  + " <br>"
						+"LEAD ID: "+student.getLeadId() + "<br>"
						+"Program: "+student.getProgram() + "<br>"
						+"Subject: "+test.getSubject()+ "<br>"
						+"Test Name: "+test.getTestName()+ "<br>"
						+"Date-Time of submission: "+formatter.format(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(studentsTestDetails.getTestEndedOn()))+ "<br>"
						+"Attempt No.: "+studentsTestDetails.getAttempt()+ " / Max Attempts : "+ test.getMaxAttempt()+"<br><br>";

				body = body  

						+"<b>Thank you for taking the test .</b><br><br>"
						+"Regards,<br>"
						+"NGA-SCE<br> "


							+ "<hr>"

							+"Please Note:<br>"
							+"<b>The Auto-generated submission mail is only the acknowledgement of test was ended . </b><br><br>"
							+"Incase of any doubt or query regarding the same, student can get in touch with support for clarification.<br>"
							+ "<hr>";


				message.setContent(body, "text/html; charset=utf-8");
				Transport.send(message);
				
				loggerForEmailCount.info("Total 1 Mail sent via(SMTP) from "+from+", Email Subject : "+emailSubject+", for sendTestEndedEmailForLeads");

			} catch(Exception e) {
				
				logger.info("\n"+"IN sendTestEndedEmail error mail sending got sapid: "+studentsTestDetails.getSapid()+" testId: "+test.getId()+" Error: "+e.toString());
				StringWriter errors = new StringWriter();
				e.printStackTrace(new PrintWriter(errors));
				String apiCalled = "sendTestEndedEmail() : ";
				String stackTrace = "apiCalled="+ apiCalled + ",data= testId: "+ test.getId()+" sapid: "+studentsTestDetails.getSapid()+ 
						",errors=" + errors.toString();
				
				logger.info(stackTrace);
				
			}

			logger.info("\n"+"IN sendTestEndedEmailForLeads Sent Test Email to Sapid: "+studentsTestDetails.getSapid()+" testId: "+test.getId()+" ");
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			
			logger.info("\n"+"IN sendTestEndedEmailForleads error got sapid: "+studentsTestDetails.getSapid()+" testId: "+test.getId()+" Error: "+e.toString());
			logger.info(" "+e);
			
		}
		
		MailBean mail = new MailBean();
		mail.setSubject(emailSubject);
		mail.setBody(body);
		mail.setMailId(student.getEmailId());
		mail.setFromEmailId("donotreply-ngasce@nmims.edu");
		mail.setCreatedBy(student.getSapid());
		
		//createRecordInUserMailTableAndMailTable(mail);

	}
	
	/**
	 * Mail triggered when an Exception is thrown while updating noSlot booking transaction. 
	 * @param subject - mail subject
	 * @param exceptionStackTrace - exception stack trace which is attached as body
	 * @throws MessagingException 
	 * @throws UnsupportedEncodingException 
	 */
	@Async
	public void transExceptionMail(String subject, String exceptionStackTrace) throws UnsupportedEncodingException, MessagingException {
		if(!"PROD".equals(ENVIRONMENT))
			return;
		
		Message message = getMessage();
		message.setFrom(new InternetAddress(from, "NMIMS Global Access SCE"));
		message.setRecipients(Message.RecipientType.TO, InternetAddress.parse("jforce.solution@gmail.com,pranit.shirke.ext@nmims.edu,raynal.dcunha.ext@nmims.edu"));
		message.setSubject(subject);
		
		String body = exceptionStackTrace + "<br><br>Thanks & Regards" + "<br>" + "NMIMS GLOBAL ACCESS SCHOOL FOR CONTINUING EDUCATION";
		message.setContent(body, "text/html; charset=utf-8");
		
		Transport.send(message);
		loggerForEmailCount.info("Total 1 Mail sent via(SMTP) from {}, Email Subject: {}, for Transaction Exception stackTrace", from, subject);
	}
	
	/**
	 * Mail triggered on successful noSlot booking transaction with the booking details.
	 * @param sapid - student No.
	 * @param studentName - full name of the student
	 * @param studentEmail - email address of the student
	 * @param bookingType - type of booking
	 * @param trackId - tracking ID
	 * @param amount - booking amount
	 * @param tranStatus - transaction status
	 */
	@Async
	public void sendNoSlotBookingTransactionMail(String sapid, String studentName, String studentEmail, String bookingType, 
												String trackId, String amount, String tranStatus) {
		if(!"PROD".equals(ENVIRONMENT))
			return;
		
		try {
			Message message = getMessage();
			message.setFrom(new InternetAddress(from, "NMIMS Global Access SCE"));
			message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(studentEmail));
			
			String subject = bookingType + " Booking Successful";
			message.setSubject(subject);
			
			String body = "Dear " + studentName + ", <br><br>" + 
						"Your booking is successful for " + bookingType + "." +
						"<br>Student No: " + sapid + 
						"<br>Track ID: " + trackId + 
						"<br>Amount: " + amount + 
						"<br>Transaction Status: " + tranStatus +
						"<br><br>Thanks & Regards <br>NMIMS GLOBAL ACCESS SCHOOL FOR CONTINUING EDUCATION";
			message.setContent(body, "text/html; charset=utf-8");
			
			Transport.send(message);
			loggerForEmailCount.info("Total 1 Mail sent via(SMTP) from {}, Email Subject: {}, for trackId: {}", from, subject, trackId);
		}
		catch(UnsupportedEncodingException ex) {
			loggerForEmailCount.error("UnsupportedEncoding Exception due to invalid email address: {} of student: {} for bookingType: {} with trackID: {}", 
										studentEmail, sapid, bookingType, trackId);
			throw new RuntimeException("Unable to send NoSlot booking successful transaction mail due to invalid email address.");
		}
		catch(MessagingException ex) {
			loggerForEmailCount.error("Messaging Exception due to Authentication Error! Failed to send NoSlot Booking transaction success mail to student: {}, " +
										"for bookingType: {} with trackId: {}", sapid, bookingType, trackId);
			throw new RuntimeException("Unable to send NoSlot booking successful transaction mail due to Authentication error.");
		}
	}
	
	/**
	 * Adding mail properties to the Message object as attributes.
	 * @return Message object
	 */
	private Message getMessage() {
		Properties props = new Properties();
		props.put("mail.smtp.auth", "true");
		props.put("mail.smtp.starttls.enable", "true");
		props.put("mail.smtp.host", host);
		props.put("mail.smtp.port", port);
	
		Session session = Session.getInstance(props, new javax.mail.Authenticator() {
			protected PasswordAuthentication getPasswordAuthentication() {
				return new PasswordAuthentication(username, password);
			}
		});
	
		return new MimeMessage(session);
	}
	
	public void sendMbaWxExamJoinLinkStatusMail(List<ExamScheduleinfoBean> successfulBookings, List<ExamScheduleinfoBean> failedBookings) throws Exception {
		Properties props = new Properties();
		props.put("mail.smtp.auth", "true");
		props.put("mail.smtp.starttls.enable", "true");
		props.put("mail.smtp.host", host);
		props.put("mail.smtp.port", port);

		Session session = Session.getInstance(props,
				new javax.mail.Authenticator() {
			protected PasswordAuthentication getPasswordAuthentication() {
				return new PasswordAuthentication(username, password);
			}
		});
		Message message = new MimeMessage(session);
		message.setFrom(new InternetAddress(from, "NMIMS Global Access SCE"));
		message.setRecipients(Message.RecipientType.TO,InternetAddress.parse(MBAWX_ATTEMPT_STATUS_MAIL_TO));
		message.setRecipients(Message.RecipientType.CC,InternetAddress.parse(MBAWX_ATTEMPT_STATUS_MAIL_CC));
		message.setRecipients(Message.RecipientType.BCC,InternetAddress.parse(MBAWX_ATTEMPT_STATUS_MAIL_BCC));
		message.setSubject("MbaWx Exam Join Links Scheduler Status Mailer.");
		
		String body = "Hello Team,"
				+ "<br>";;
		body += "<br> Sent mail successfully to " + successfulBookings.size() + " Students";
		if(failedBookings.size() > 0) {

			body += ""
					+ "<br>"
					+ "The following is a list of error emails. "
					+ "<br>"
					+ "<table border='1' style='border-collapse: collapse;'>"
						+ "<thead>"
							+ "<tr>"
								+ "<th>#</th>"
								+ "<th>Sapid</th>"
								+ "<th>Email Id</th>"
								+ "<th>Subject</th>"
								+ "<th>Exam Start Time</th>"
								+ "<th>Exam Join Link</th>"
								+ "<th>AssessmentName</th>"								
								+ "<th>Error Messagee</th>"
							+ "</tr>"
						+ "</thead>"
						+ "<tbody>";
			int count = 0;
			
			
			for (ExamScheduleinfoBean booking : failedBookings) {
				count++;
				body += ""
					+ "<tr>"
						+ "<td>" + count + "</td>"
						+ "<td>" + booking.getSapid() + "</td>"
						+ "<td>" + booking.getEmailId() + "</td>"
						+ "<td>" + booking.getSubject() + "</td>"
						+ "<td>" + booking.getExamStartDateTime() + "</td>"
						+ "<td>" + booking.getJoinURL() + "</td>"
						+ "<td>" + booking.getAssessmentName() + "</td>"
						+ "<td>" + booking.getError() + "</td>"
					+ "</tr>";
			}
			body += ""
					+ "</tbody>"
				+ "</table>";
		} else {
			body += "<br> No Errors were found!";
		}
			

		message.setContent(body, "text/html; charset=utf-8");
	
		try {
			Transport.send(message);
		} catch (Exception e) {
			logger.error("Error While sending MbaWx Exam Join Links Scheduler Status Mailer, for sendExamJoinLinkStatusMail :: "+e.getMessage());
		}
		
		
		logger.info("Total 1 Mail sent via(SMTP) from "+from+", Email Subject : MbaWx Exam Join Links Scheduler Status Mailer, for sendExamJoinLinkStatusMail");
	}
	
}
