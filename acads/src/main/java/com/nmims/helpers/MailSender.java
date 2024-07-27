package com.nmims.helpers;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Properties;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

import org.apache.commons.codec.binary.Base64;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import com.nmims.beans.FacultyAcadsBean;
import com.nmims.beans.ForumAcadsBean;
import com.nmims.beans.PCPBookingTransactionBean;
import com.nmims.beans.RecordingStatus;
import com.nmims.beans.SessionDayTimeAcadsBean;
import com.nmims.beans.SessionQueryAnswer;
import com.nmims.beans.StudentAcadsBean;
import com.nmims.beans.TestAcadsBean;
import com.nmims.daos.PCPBookingDAO;
@Component
public class MailSender {

	private String host;
	private String port;
	private String username;
	private String password;
	private String from;
	
	@Autowired
	ApplicationContext act;
	
	@Autowired
	private EmailHelper emailHelper;
	
	@Value( "${SERVER_PATH}" )
	private String SERVER_PATH;
	
	@Value( "${ENVIRONMENT}" )
	private String ENVIRONMENT;
	
	@Value("${MBA_WX_QUERY_CC_EMAIL}")
	private String MBA_WX_QUERY_CC_EMAIL;
	
	@Value("${SESSION_RECORDING_EMAIL}")
	private String SESSION_RECORDING_EMAIL;

	private final String USER_AGENT = "Mozilla/5.0";

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

	private static final Logger loggerForEmailCount = LoggerFactory.getLogger("emailCount");
	private static final Logger loggerForSessionEmails = LoggerFactory.getLogger("session_emails");

	public void sendScheduleSessionEmails(SessionDayTimeAcadsBean scheduledession, ArrayList<StudentAcadsBean> studentList) throws MessagingException, UnsupportedEncodingException{
		java.util.Properties properties = new java.util.Properties(); 
		properties.setProperty("mail.transport.protocol", "smtp"); 
		//properties.setProperty("mail.host", "mail1.domain.com,mail2.domain.com"); 
		properties.setProperty("mail.transport.pool-size", "500");
		properties.setProperty("mail.transport.connect-retry-period", "60");
		properties.setProperty("mail.transport.sender-strategy", "net.sf.hajavamail.SimpleSenderStrategy"); 

		properties.setProperty("mail.transport.protocol", "smtp");
		properties.put("mail.smtp.auth", "true");
		properties.put("mail.smtp.starttls.enable", "true");
		properties.put("mail.smtp.host", host);
		properties.put("mail.smtp.ssl.trust", host);
		properties.put("mail.smtp.port", port);

		String body = "";
		/*Commented by steffi to add new body template
		 * body = "Dear Student, <br><br>"
				+ "<b>Your session Schedule:</b>.<br><br>"
				+ "Online live Session of <b>" + scheduledession.getSubject() + " - " + scheduledession.getSessionName()+"</b> is scheduled on <b>"+scheduledession.getDate() + " at " + scheduledession.getStartTime()+ "</b>. <br>"
				+ "Login to student Zone -> Go To Academics Portal -> Academic Calendar. <br>"
				+ "Kindly login 5 minutes before the session starts to avoid any inconvenience.<br>"
				+ "<br>Thanks & Regards"
				+ "<br>"
				+ "NMIMS GLOBAL ACCESS SCHOOL FOR CONTINUING EDUCATION";*/
		body = "Dear Student, <br><br>"
				+ "<b>Please find below session schedule for tomorrow�s lecture</b>.<br><br>"
				+ "Online live Session of <b>" + scheduledession.getSubject() + " - " + scheduledession.getSessionName()+"</b> is scheduled on <b>"+scheduledession.getDate() + " at " + scheduledession.getStartTime()+ "</b>. <br>"
				+ "<b>Important checks:</b><br>"
				+ "Good internet connectivity is highly recommended<br>"
				+ "All sessions are mapped under Student portal�Academic Calendar<br>"
				+ "When you click on �Attend session� you will be re-directed to join the live session and prompted to click on �<b>Join audio conference</b>� this is a very important step which will enable your audio settings for the lecture <br>"
				+ "Lecture recordings will be uploaded within 72 hrs post commencement of live lectures"
				+ "If you are joining the session/lectures using your mobile phone, kindly download the �Cisco WebEx meetings� app from the play store <br>"
				+ "Kindly login 5 minutes before the session starts to avoid any inconvenience for any issues call us on 18001025136.<br>"
				+ "<br>Thanks & Regards"
				+ "<br>"
				+ "NMIMS GLOBAL ACCESS SCHOOL FOR CONTINUING EDUCATION";
		

		Session session = Session.getInstance(properties,
				new javax.mail.Authenticator() {
			protected PasswordAuthentication getPasswordAuthentication() {
				return new PasswordAuthentication(username, password);
			}
		});
		Transport transport = session.getTransport(); 

		long startTime = System.currentTimeMillis(); 

		transport.connect(); 

		javax.mail.Address address = null;
		String emailsNotSentId = "";
		int successCount = studentList.size();
		int errorCount = 0;

		for (int j = 0; j < studentList.size(); j = j + 50) {
			int lastIndex = (studentList.size() < j+50 ? studentList.size() : j+50);
			List<StudentAcadsBean> studentsSubList =  studentList.subList(j, lastIndex);

			String emailId = "";

			Message message = new javax.mail.internet.MimeMessage(session); 
			message.setContent(body, "text/html; charset=utf-8");
			message.setFrom(new InternetAddress(from, "NMIMS Global Access SCE"));

			for (int i = 0; i < studentsSubList.size(); i++) 
			{ 
				try {
					address = new javax.mail.internet.InternetAddress(studentList.get(i).getEmailId()); 
					message.addRecipient(Message.RecipientType.BCC, address); 
				}catch (Exception e) {
					errorCount++;
					//  
					emailsNotSentId = emailsNotSentId +"," + emailId;
				}
			}
			message.setSubject("Your Session Schedule for "+ scheduledession.getSubject()); 
			transport.sendMessage(message, message.getAllRecipients()); 


		} 

		if(!"".equals(emailsNotSentId)){

			//address = new javax.mail.internet.InternetAddress("sanketpanaskar@gmail.com"); 
			Message message = new javax.mail.internet.MimeMessage(session); 
			try {
				message.setFrom(new InternetAddress(from, "NMIMS Global Access SCE"));
			} catch (UnsupportedEncodingException e) {
			}
			//message.addRecipients(javax.mail.Message.RecipientType.TO, InternetAddress.parse("sanketpanaskar@gmail.com, sneha.utekar@nmims.edu, bageshree@nmims.edu, Sheetal.Gupta@nmims.edu"));
			message.addRecipients(javax.mail.Message.RecipientType.TO, InternetAddress.parse("sanketpanaskar@gmail.com, jforcesolutions@gmail.com"));
			message.setSubject("Emails Errors for "+scheduledession.getSubject()); 
			message.setText(emailsNotSentId); 
			transport.sendMessage(message, message.getAllRecipients()); 
		}
		
		Message message = new javax.mail.internet.MimeMessage(session); 
		try {
			message.setFrom(new InternetAddress(from, "NMIMS Global Access SCE"));
		} catch (UnsupportedEncodingException e) {
		}
		//message.addRecipients(javax.mail.Message.RecipientType.TO, InternetAddress.parse("sanketpanaskar@gmail.com, sneha.utekar@nmims.edu, bageshree@nmims.edu, Sheetal.Gupta@nmims.edu"));
		message.addRecipients(javax.mail.Message.RecipientType.TO, InternetAddress.parse("sanketpanaskar@gmail.com, jforcesolutions@gmail.com"));
		
		message.setSubject("Emails Summary for Subject "+scheduledession.getSubject()); 
		message.setText("Emails sent for "+scheduledession.getSubject()+ " to "+(successCount - errorCount)+ " students out of " 
				+ successCount+" successfully at "+new Date()); 
		transport.sendMessage(message, message.getAllRecipients()); 


		transport.close(); 

		long endTime = System.currentTimeMillis(); 

	}
	
	@Async
	public void sendForumAbuseEmail(ForumAcadsBean bean, String reportee, String reportedOn) {
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

			
			message.addRecipients(Message.RecipientType.TO, 
                    InternetAddress.parse("sanketpanaskar@gmail.com,Sneha.Utekar@nmims.edu ,nelson.soans@nmims.edu"));
			
			message.addRecipients(Message.RecipientType.CC, 
                    InternetAddress.parse("sanketpanaskar@gmail.com,Sneha.Utekar@nmims.edu ,nelson.soans@nmims.edu"));
			
			message.setSubject("Abuse Reported");
			
			
			
			String body = "Dear Coordinator, <br><br>"
					+ "Below are the details of Abuse report<br>"
					+ "Reported by: "+reportee +"<br>"
							+ "Subject: "+bean.getSubject() + "<br>"
							+ "Original Post: "+bean.getTitle()+ "<br>"
							+ "Abusive Reply By: " + reportedOn+ "<br>"
							+ "Abusive Description of reply: "+bean.getDescription() + "<br>";
			
			
			
			body +="Thanks & Regards"
					+"<br>"
					+"NMIMS GLOBAL ACCESS SCHOOL FOR CONTINUING EDUCATION";
			
			
			message.setContent(body, "text/html; charset=utf-8");
			Transport.send(message);
			
			loggerForEmailCount.info("Total 1 Mail sent via(SMTP) from "+from+", Email Subject : Abuse Reported ");
		} catch(Exception e) {
			  
		}

		
	}
	public void sendEmails(SessionDayTimeAcadsBean scheduledession, ArrayList<StudentAcadsBean> studentList){
		
		String emailIds = "";
		try {
			
			String htmlBody = "";
			htmlBody = "Dear Student, <br><br>"
					+ "<b>Your session Schedule:</b><br><br>"
					+ "Interactive live Session of <b>" + scheduledession.getSubject() + " - " + scheduledession.getSessionName()+"</b> is scheduled on <b>"+scheduledession.getDate() + " at " + scheduledession.getStartTime()+ "</b>. <br>"
					+ "Login to student Zone -> Go To Sessions Calendar  <br>"
					+ "Kindly login 5 minutes before the session starts to avoid any inconvenience.<br>"
					+ "<br>Thanks & Regards"
					+ "<br>"
					+ "NMIMS GLOBAL ACCESS SCHOOL FOR CONTINUING EDUCATION";
			
			ArrayList<String> toEmailIds = new ArrayList<>();
			ArrayList<String> toSapIds = new ArrayList<>();
			String subject = "Live Interactive Session Alert: "+scheduledession.getSubject() + " - " + scheduledession.getSessionName();
			//String fromEmailId = "ngasce.academics@nmims.edu";
			String fromEmailId = "donotreply-ngasce@nmims.edu";
			String fromName = "NMIMS Global Access SCE";
			
			for (StudentAcadsBean studentBean : studentList) {
				toEmailIds.add(studentBean.getEmailId());
				toSapIds.add(studentBean.getSapid());//Add SAPID to list
			}
			toEmailIds.add("sanketpanaskar@gmail.com");
			toEmailIds.add("sheetal.gupta@nmims.edu");
			toEmailIds.add("sneha.utekar@nmims.edu");
			toEmailIds.add("bageshree@nmims.edu");
			toEmailIds.add("salil.nayak@nmims.edu");
			toEmailIds.add("parul.dhamija@nmims.edu");
			toEmailIds.add("deepak.asarsa@nmims.edu");
			toEmailIds.add("chaitali.patel@nmims.edu");
			toEmailIds.add("nikhil.bhosale@nmims.edu");
			toEmailIds.add("priya.nimal@nmims.edu");
			toEmailIds.add("binoy.nair@nmims.edu");
			toEmailIds.add("nandana.narayanan@nmims.edu");
			toEmailIds.add("Michael.Dcruz@nmims.edu");
			toEmailIds.add("Knaresh.Kumar@nmims.edu ");
			toEmailIds.add("Dinesh.Kalyani@nmims.edu");
			toEmailIds.add("sujatha.tadepalli@nmims.edu");
			toEmailIds.add("Poornima.KP@nmims.edu");
			toEmailIds.add("meghavi.panchal@nmims.edu");
			toEmailIds.add("meghana.patange@nmims.edu");
			toEmailIds.add("Sirshendu.Sen@nmims.edu");
			toEmailIds.add("Ketaki.amin@nmims.edu");
			toEmailIds.add("jasmeet.kaur@nmims.edu");
			toEmailIds.add("Shiv.Golani.Ext@nmims.edu");
			toEmailIds.add("Somesh.Turde.Ext@nmims.edu");
			
			loggerForSessionEmails.info("Inside Mailsender -> sendEmails Total Email id's : "+toEmailIds.size()+ " Total sapid's : "+toSapIds.size());
			
			emailHelper.sendMassEmail(toSapIds,toEmailIds, fromEmailId, fromName, subject, htmlBody);
	 
		  } catch (Exception e) {
			String body = "Error in sending email "+e.getMessage() + " <br>";
			body = body + "Error while sending emails to "+emailIds;
			String encodedBody = new String(Base64.encodeBase64(body.getBytes()));
			
			sendTNSEMail(encodedBody, "[\"sanketpanaskar@gmail.com\",\"sheetal.gupta@nmims.edu\", \"sneha.utekar@nmims.edu\", \"bageshree@nmims.edu\"]", "Error in sending emails for "+scheduledession.getSubject());
			loggerForEmailCount.info("Total 1 Mail sent via(TNMails) from "+from+", Email Subject : "+scheduledession.getSubject()+", for sendEmails");
			  
	 
		  }
	}

	public void sendTestNotificationEmails(TestAcadsBean test, List<StudentAcadsBean> studentList){
		
		String emailIds = "";
		try {
			
			String htmlBody = "";
			htmlBody = "Dear Student, <br><br>"
					+ "<b>Your Internal Assessment Test : " + test.getSubject() + " - " + test.getTestName()+"</b> is scheduled on <b>"+test.getStartDateForMail() + " </b>. <br>"
					+ "Login to student Zone  <br>"
					+ "Kindly follow all guidelines to avoid any inconvenience.<br>"
					+ "<br>Thanks & Regards"
					+ "<br>"
					+ "NMIMS GLOBAL ACCESS SCHOOL FOR CONTINUING EDUCATION";
			
			ArrayList<String> toEmailIds = new ArrayList<>();
			ArrayList<String> toSapIds = new ArrayList<>();
			String subject = "Internal Assessment Test Live Alert: "+test.getSubject() + " - " + test.getTestName();
			//String fromEmailId = "ngasce.academics@nmims.edu";
			String fromEmailId = "donotreply-ngasce@nmims.edu";
			String fromName = "NMIMS Global Access SCE";
			
			for (StudentAcadsBean studentBean : studentList) {
				toEmailIds.add(studentBean.getEmailId());
				toSapIds.add(studentBean.getSapid());//Add SAPID to list
			}
			//toEmailIds.add("sanketpanaskar@gmail.com");
			//toEmailIds.add("sneha.utekar@nmims.edu");
			//toEmailIds.add("bageshree@nmims.edu");
			toEmailIds.add("jforce.solution@gmail.com");
			emailHelper.sendMassEmail(toSapIds,toEmailIds, fromEmailId, fromName, subject, htmlBody);
	 
		  } catch (Exception e) {
			String body = "Error in sending test notifictions email "+e.getMessage() + " <br>";
			body = body + "Error while sending emails to "+emailIds;
			String encodedBody = new String(Base64.encodeBase64(body.getBytes()));
			
			sendTNSEMail(encodedBody, "[\"sanketpanaskar@gmail.com\",\"sheetal.gupta@nmims.edu\", \"sneha.utekar@nmims.edu\", \"bageshree@nmims.edu\"]", "Error in sending emails for "+test.getSubject());
			loggerForEmailCount.info("Total 1 Mail sent via(TNMails) from "+from+", Email Subject : "+test.getSubject()+", for sendTestNotificationEmails");
			  
	 
		  }
	}
	
	public void sendTNSEMail(String base64EncodedBody, String mailerList, String subject){
		try {
			String url = "http://www.tnmails.com/api/sendTransactionalMail";
			URL obj = new URL(url);
			HttpURLConnection con = (HttpURLConnection) obj.openConnection();
	 
			//add reuqest header
			con.setRequestMethod("POST");
			con.setRequestProperty("User-Agent", USER_AGENT);
			con.setRequestProperty("Accept-Language", "en-US,en;q=0.5");
	
			String urlParameters = "fromemail=donotreply-ngasce@nmims.edu&fromname=NMIMS Global Access SCE&tos=[\"no-reply@nmims.edu\"]"
					+ "&bccs="+mailerList+"&subject="+subject+"&body="+URLEncoder.encode(base64EncodedBody,"UTF-8")+"&apikey=gjyw9u0mm1ot1j65lr8ke1i3q2tbnlttuift3f6e7jxwttr50oeefm8vdrwq2f0loe5gk1kga93trjnrxru34to70an1gmcur8z1xa7x9yhq8u8ubto5c22c3e49r7s8";

			// Send post request
			con.setDoOutput(true);
			DataOutputStream wr = new DataOutputStream(con.getOutputStream());
			wr.writeBytes(urlParameters);
			wr.flush();
	
			wr.close();
	 
			int responseCode = con.getResponseCode();
			BufferedReader in = new BufferedReader( new InputStreamReader(con.getInputStream()));
			String inputLine;
			StringBuffer response = new StringBuffer();
	 
			while ((inputLine = in.readLine()) != null) {
				response.append(inputLine);
			}
			in.close();
	 
		} catch (Exception e) {
			  
		}
	}
	
//	public void notifyFacultyForForums(){
//		EmailHelper helper = new EmailHelper();
//		ForumDAO dao = (ForumDAO)act.getBean("forumDAO");
//		ArrayList<String> emailIds = dao.getFacultyEmailForAnActiveForum();
//		String htmlBody = "Dear Faculty,"
//						+"This a forum Response mail which is scheduled at 2:00 PM"
//						+"To go to only those faculties for which the replies are greater than 0";
//		helper.sendMassEmail(null,emailIds, "NMIMS Global Access SCE", "NMIMS Global Access SCE","Forum Response", htmlBody);
//	}
	

	@Async
	public void sendQueryPostedEmail(SessionDayTimeAcadsBean scheduledSession, SessionQueryAnswer sessionQuery, FacultyAcadsBean faculty,String toEmailAddress) {
		
		if(!"PROD".equalsIgnoreCase(ENVIRONMENT)){
			System.out.println("Not sending query email since this is not Prod.");
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
			message.setRecipients(Message.RecipientType.TO,InternetAddress.parse(toEmailAddress));
			String body = "";
			
			message.setSubject("Query Received");

				body = "Dear Faculty, <br><br>"
						+"A query has been received for " + scheduledSession.getSubject() + "-" + scheduledSession.getSessionName() +". <br>"
						+ "Query: "+sessionQuery.getQuery()
						+"<br><br>"
						+"Please click <a href=\""+ SERVER_PATH + "acads/admin/viewQueryForm?id=" +scheduledSession.getId() +"&eid="
						+URLEncoder.encode(AESencrp.encrypt(faculty.getFacultyId()))+"\">here</a> to respond to query."
						+"<br><br>"
						+"Thanks & Regards"
						+"<br>"
						+"NMIMS GLOBAL ACCESS SCHOOL FOR CONTINUING EDUCATION";



			message.setContent(body, "text/html; charset=utf-8");

			Transport.send(message);

			loggerForEmailCount.info("Total 1 Mail sent via(SMTP) from "+from+", Email Subject : Query Received, for sendQueryPostedEmail");

		} catch(Exception e) {
			  
		}
	}
	
	@Async
	public void sendWXQueryPostedEmail(SessionQueryAnswer sessionQuery, FacultyAcadsBean faculty,String toEmailAddress) {
		
		if(!"PROD".equalsIgnoreCase(ENVIRONMENT)){
			System.out.println("Not sending query email since this is not Prod.");
			return;
		}
		/*
		String programName="";
		if(("111".equals(sessionQuery.getConsumerProgramStructureId())) || ("151".equals(sessionQuery.getConsumerProgramStructureId()))) {
			programName="MBA - WX";
		}else if ("131".equals(sessionQuery.getConsumerProgramStructureId())) {
			programName="M.Sc. (AI & ML Ops)";
		}
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
			message.addRecipients(Message.RecipientType.CC, 
                    InternetAddress.parse(MBA_WX_QUERY_CC_EMAIL));
//			added bcc
			message.addRecipients(Message.RecipientType.BCC, 
                    InternetAddress.parse("jforce.solution@gmail.com"));
			message.setRecipients(Message.RecipientType.TO,InternetAddress.parse(toEmailAddress));
			String body = "";
			
			message.setSubject("Query Received");

				body = "Dear Faculty, <br><br>"
						+"A query has been received for " + sessionQuery.getSubject() +" - "+ sessionQuery.getProgramName() +". <br>"
						+ "Query: "+sessionQuery.getQuery()
						+"<br><br>"
						+"Please click <a href=\""+ SERVER_PATH + "studentportal/\">here</a> to respond to query."
						+"<br><br>"
						+"Thanks & Regards"
						+"<br>"
						+"NMIMS GLOBAL ACCESS SCHOOL FOR CONTINUING EDUCATION";



			message.setContent(body, "text/html; charset=utf-8");

			Transport.send(message);

			loggerForEmailCount.info("Total 1 Mail sent via(SMTP) from "+from+", Email Subject : Query Received, for sendWXQueryPostedEmail");

		} catch(Exception e) {
			  
		}
	}
	
	@Async
	public void sendRecordingUploadErrorEmail(SessionDayTimeAcadsBean sessionDayTimeBean,String error) {
		
		if(!"PROD".equalsIgnoreCase(ENVIRONMENT)){
			System.out.println("Not sending query email since this is not Prod.");
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
			message.setRecipients(Message.RecipientType.TO,InternetAddress.parse(SESSION_RECORDING_EMAIL));
			message.setRecipients(Message.RecipientType.CC,InternetAddress.parse("NGASCE.IT.Support@nmims.edu"));
			String body = "";
			
			message.setSubject("Session Recording Auto Upload Error");
				if("Multiple recording found".equalsIgnoreCase(error)) {
					body = "Hello Team, <br><br>"
					+ "Subject: " + sessionDayTimeBean.getSubject()
					+ "<br>"
					+ "Warning: " + error
					+"<br><br>"
					+"Please click <a href=\""+ SERVER_PATH + "studentportal/\">here</a> to login."
					+"<br><br>"
					+"Thanks & Regards"
					+"<br>"
					+"NMIMS GLOBAL ACCESS SCHOOL FOR CONTINUING EDUCATION";
				}else {
					body = "Dear Team, <br><br>"
						+"Session recording failed to upload for subject: "+ sessionDayTimeBean.getSubject() +" and meetingId: "+ sessionDayTimeBean.getMeetingId() +" <br>"
						+ "Error: " +error 
						+"<br><br>"
						+"Please click <a href=\""+ SERVER_PATH + "studentportal/\">here</a> to login."
						+"<br><br>"
						+"Thanks & Regards"
						+"<br>"
						+"NMIMS GLOBAL ACCESS SCHOOL FOR CONTINUING EDUCATION";
				}



			message.setContent(body, "text/html; charset=utf-8");

			Transport.send(message);

			loggerForEmailCount.info("Total 1 Mail sent via(SMTP) from "+from+", Email Subject : Session Recording Auto Upload Error, for sendRecordingUploadErrorEmail");
		} catch(Exception e) {
			  
		}
	}
	
	
	@Async 
	public void sendWXQueryAnswerPostedEmailToStudent(SessionQueryAnswer sessionQuery,StudentAcadsBean student) {
		if(!"PROD".equalsIgnoreCase(ENVIRONMENT)){
			System.out.println("Not sending query email since this is not Prod.");
			return;
		}
		
		String userIdFromURL = null;
		try {
				userIdFromURL = AESencrp.encrypt(student.getSapid());
		} catch (Exception e) {
			// TODO: handle exception
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
			message.addRecipients(Message.RecipientType.BCC, 
                    InternetAddress.parse(MBA_WX_QUERY_CC_EMAIL));
			String body = "";
			
			message.setSubject("Query Answer");

				body = "Dear Student, <br><br>"
						+" Your query has been responded for "+ sessionQuery.getSubject() + ". <br>"
						+ "Query: "+sessionQuery.getQuery()
						+"<br><br>"
						+" Please click <a href=\""+ SERVER_PATH + "studentportal/\">here</a> "
						+" <br><br>"
						+" for any queries do call us on 1-800-1025-136 (Mon-Sat) 9am � 7pm "
						+" <br><br>"
						+" Thanks & Regards"
						+" <br>"
						+" NMIMS GLOBAL ACCESS SCHOOL FOR CONTINUING EDUCATION";



			message.setContent(body, "text/html; charset=utf-8");

			Transport.send(message);

			loggerForEmailCount.info("Total 1 Mail sent via(SMTP) from "+from+", Email Subject : Query Answer, for sendWXQueryAnswerPostedEmailToStudent");
		} catch(Exception e) {
			  
		}
	}
	
	
	@Async
	public void sendQueryAnswerPostedEmailToStudent(SessionQueryAnswer sessionQuery,StudentAcadsBean student,SessionDayTimeAcadsBean scheduledSession) {
		if(!"PROD".equalsIgnoreCase(ENVIRONMENT)){
			System.out.println("Not sending query email since this is not Prod.");
			return;
		}
		
		String userIdFromURL = null;
		try {
				userIdFromURL = AESencrp.encrypt(student.getSapid());
		} catch (Exception e) {
			// TODO: handle exception
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
			String body = "";
			
			message.setSubject("Query Answer");

				body = "Dear Student, <br><br>"
						+" Your query has been responded for "+ scheduledSession.getSubject() + "-" + scheduledSession.getSessionName() +". <br>"
						+" Session Date & Time : "+scheduledSession.getDate()+"-"+scheduledSession.getStartTime()+"<br>"
						+" and the resolution is available under " 
						+" Please click <a href=\""+ SERVER_PATH + "acads/student/postQueryForm?id=" +sessionQuery.getSessionId() +"&action=viewQueries&eid="+userIdFromURL+"\">here</a> "
						+" <br><br>"
						+" for any queries do call us on 1-800-1025-136 (Mon-Sat) 9am � 7pm "
						+" <br><br>"
						+" Thanks & Regards"
						+" <br>"
						+" NMIMS GLOBAL ACCESS SCHOOL FOR CONTINUING EDUCATION";



			message.setContent(body, "text/html; charset=utf-8");

			Transport.send(message);

			loggerForEmailCount.info("Total 1 Mail sent via(SMTP) from "+from+", Email Subject : Query Answer, for sendQueryAnswerPostedEmailToStudent");
		} catch(Exception e) {
			  
		}
	}

	//Send Course Query Posted Mail to faculty Start
	@Async
	public void sendCourseQueryPostedEmail(SessionQueryAnswer sessionQuery,String toEmailAddress) {
		
		if(!"PROD".equalsIgnoreCase(ENVIRONMENT)){
			System.out.println("Not sending query email since this is not Prod.");
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
			message.setRecipients(Message.RecipientType.TO,InternetAddress.parse(toEmailAddress));
			String body = "";
			
			message.setSubject("Course Query Received");

				body = "Dear Faculty, <br><br>"
						+"A query has been received for " + sessionQuery.getSubject() + ". <br>"
						+ "Query: "+sessionQuery.getQuery()
						+"<br><br>"
						+"Please click <a href=\""+ SERVER_PATH + "studentportal/login"
						+""+"\">here</a> to login page and go to <b>Queries For Me</b> section  to respond to query."
						+"<br><br>"
						+"Thanks & Regards"
						+"<br>"
						+"NMIMS GLOBAL ACCESS SCHOOL FOR CONTINUING EDUCATION";



			message.setContent(body, "text/html; charset=utf-8");

			Transport.send(message);

			loggerForEmailCount.info("Total 1 Mail sent via(SMTP) from "+from+", Email Subject : Course Query Received, for sendCourseQueryPostedEmail");
		} catch(Exception e) {
			  
		}
	}
	//Send Course Query Mail to faculty End
	
	@Async
	public void sendCourseQueryAnswerPostedEmailToStudent(SessionQueryAnswer sessionQuery,StudentAcadsBean student) {
		
		if(!"PROD".equalsIgnoreCase(ENVIRONMENT)){
			System.out.println("Not sending query email since this is not Prod.");
			return;
		}
		
		String userIdFromURL = null;
		try {
				userIdFromURL = AESencrp.encrypt(student.getSapid());
		} catch (Exception e) {
			// TODO: handle exception
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
			String body = "";
			
			message.setSubject("Query Answer");

				body = "Dear Student, <br><br>"
						+" Your query has been responded for "+ sessionQuery.getSubject() + ". <br>"
						+" and the resolution is available under " 
						+" Please click <a href=\""+ SERVER_PATH + "acads/student/courseQueryForm?subject=" +sessionQuery.getSubject() +"\">here</a> "
						+" <br><br>"
						+" for any queries do call us on 1-800-1025-136 (Mon-Sat) 9am � 7pm "
						+" <br><br>"
						+" Thanks & Regards"
						+" <br>"
						+" NMIMS GLOBAL ACCESS SCHOOL FOR CONTINUING EDUCATION";



			message.setContent(body, "text/html; charset=utf-8");

			Transport.send(message);

			loggerForEmailCount.info("Total 1 Mail sent via(SMTP) from "+from+", Email Subject : Query Answer, for sendCourseQueryAnswerPostedEmailToStudent");
		} catch(Exception e) {
			  
		}
	}
	

	
	@Async
	public void sendBookingSummaryEmail(StudentAcadsBean student, PCPBookingDAO dao) {
		Properties props = new Properties();
		props.put("mail.smtp.auth", "true");
		props.put("mail.smtp.starttls.enable", "true");
		props.put("mail.smtp.host", host);
		props.put("mail.smtp.port", port);


		List<PCPBookingTransactionBean> pcpBooking = dao.getConfirmedBooking(student.getSapid());
		String year = pcpBooking.get(0).getYear();
		String month = pcpBooking.get(0).getMonth();

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
			message.setSubject("Your PCP/VC Registration Summary");

			String body = "";

			body = "Dear " + student.getFirstName() +", <br>"
					+"Here is your PCP/VC Registration Summary. <br>"
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
					+ "<th>Transaction Status</th>"
					+ "<th>Booking Status</th>"
					+ "</tr>"
					+ "</thead>"
					+ "<tbody>";


			int count = 1;
			for(int i = 0; i < pcpBooking.size(); i++){
				PCPBookingTransactionBean bean = (PCPBookingTransactionBean)pcpBooking.get(i);

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
						+ " <td align=\"center\"> " + (count++) +"</td> "
						+ "	<td> " + subject + " </td> "
						+ "	<td align=\"center\">" + bean.getSem()+"</td> "
						+ " <td>" +bean.getTranStatus()+ "</td>"
						+ " <td align=\"center\">" +bookingStatus+ "</td>"
						+ " </tr> ";								

			} 

			body = body + 	"</tbody> </table>";	 	

/*			body = body + "<br><b>Note:</b><br>"
					+ "1. This Email is NOT a Hall ticket.<br>"
					+ "2. Hall Ticket will be made available for download on Exam Portal 15 days prior to Exams.<br>"
					+ "3. The NMIMS University reserves right to shift students to another available exam center in case of any unavoidable circumstances. <br>";
			*/

			body = body + "<br>Thanks & Regards"
					+ "<br>"
					+ "NMIMS GLOBAL ACCESS SCHOOL FOR CONTINUING EDUCATION";




			message.setContent(body, "text/html; charset=utf-8");

			Transport.send(message);

			loggerForEmailCount.info("Total 1 Mail sent via(SMTP) from "+from+", Email Subject : Your PCP/VC Registration Summary, for sendBookingSummaryEmail");
		} catch(Exception e) {
			  
		}
	}
	
	@Async
	public void sendEmail(String subject,String emailBody,ArrayList<String> recipient ) {
		
		if(!"PROD".equalsIgnoreCase(ENVIRONMENT)){
			System.out.println("Not sending query email since this is not Prod.");
			return;
		}
		
		Properties props = new Properties();
		props.put("mail.smtp.auth", "true");
		props.put("mail.smtp.starttls.enable", "true");
		props.put("mail.smtp.host", host);
		props.put("mail.smtp.port", port);
		
		javax.mail.Address address = null;
		String emailsNotSentId = "";
		int successCount = recipient.size();
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
					address = new javax.mail.internet.InternetAddress(recipient.get(i)); 
					message.addRecipient(Message.RecipientType.TO, address); 
				}catch (Exception e) {
					errorCount++;
					emailsNotSentId = emailsNotSentId +"," + recipient.get(i);
				}
			}
			
			message.addRecipients(Message.RecipientType.BCC, InternetAddress.parse("jforce.solution@gmail.com"));
			
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

			loggerForEmailCount.info("Total 1 Mail sent via(SMTP) from "+from+", Email Subject : "+subject+", for sendEmail");
			
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
				loggerForEmailCount.info("Total 1 Mail sent via(SMTP) from "+from+", Email Subject : Emails Errors for "+subject+", for sendEmail");
			}
			
		} catch(Exception e) {
			  
		}

	}
	
	@Async
	public void sendEmailForInvalidSession(String subject,String emailBody,ArrayList<String> recipient ){
		
		Properties props = new Properties();
		props.put("mail.smtp.auth", "true");
		props.put("mail.smtp.starttls.enable", "true");
		props.put("mail.smtp.host", host);
		props.put("mail.smtp.port", port);
		
		javax.mail.Address address = null;
		String emailsNotSentId = "";
		int successCount = recipient.size();
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
					address = new javax.mail.internet.InternetAddress(recipient.get(i)); 
					message.addRecipient(Message.RecipientType.TO, address); 
				}catch (Exception e) {
					errorCount++;
					emailsNotSentId = emailsNotSentId +"," + recipient.get(i);
				}
			}
			
			message.addRecipients(Message.RecipientType.CC, 
                    InternetAddress.parse("sanketpanaskar@gmail.com, Pranit.Shirke.EXT@nmims.edu , Somesh.Turde.EXT@nmims.edu "));
			
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

			// sending  EmailId to which email is not send
			if(!"".equals(emailsNotSentId)){
				 
				Message messages = new javax.mail.internet.MimeMessage(session); 
				try {
					messages.setFrom(new InternetAddress(from, "NMIMS Global Access SCE"));
				} catch (UnsupportedEncodingException e) {
				}

				messages.addRecipients(javax.mail.Message.RecipientType.TO, InternetAddress.parse("sanketpanaskar@gmail.com, Pranit.Shirke.EXT@nmims.edu,  Somesh.Turde.EXT@nmims.edu"));
				messages.setSubject("Emails Errors for "+subject); 
				messages.setText(emailsNotSentId); 
				Transport.send(messages);
			}
			
			loggerForEmailCount.info("Total 1 Mail sent via(SMTP) from "+from+", Email Subject : "+subject+", for sendEmailForInvalidSession ");
		} catch(Exception e) {
			  
		}
	}
	
}
