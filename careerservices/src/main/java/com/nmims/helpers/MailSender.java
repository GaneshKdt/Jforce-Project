package com.nmims.helpers;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Properties;

import javax.mail.Message;
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
import org.springframework.scheduling.annotation.Async;

import com.nmims.beans.FacultyCareerservicesBean;
import com.nmims.beans.SessionDayTimeBean;
import com.nmims.beans.SessionQueryAnswerCareerservicesBean;
import com.nmims.beans.StudentCareerservicesBean;


public class MailSender {
	
	@Autowired
	private EmailHelper emailHelper;
	
	@Value( "${SERVER_PATH}" )
	private String SERVER_PATH;
	
	@Value( "${ENVIRONMENT}" )
	private String ENVIRONMENT;
	
	private final String USER_AGENT = "Mozilla/5.0";
	
	private String host;
	private String port;
	private String username;
	private String password;
	private String from;
	
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

	private static final Logger logger = LoggerFactory.getLogger(MailSender.class);
 
	public void sendEmails(SessionDayTimeBean scheduledession, ArrayList<StudentCareerservicesBean> studentList){
		
		String emailIds = "";
		try {
			
			String htmlBody = "";
			htmlBody = "Dear Student, <br><br>"
					+ "<b>Your session Schedule:</b><br><br>"
					+ "Online live Session of <b>" + scheduledession.getSessionName()+"</b> is scheduled on <b>"+scheduledession.getDate() + " at " + scheduledession.getStartTime()+ "</b>. <br>"
					+ "Login to student Zone -> Go To Sessions Calendar  <br>"
					+ "Kindly login 5 minutes before the session starts to avoid any inconvenience.<br>"
					+ "<br>Thanks & Regards"
					+ "<br>"
					+ "NMIMS GLOBAL ACCESS SCHOOL FOR CONTINUING EDUCATION";
			
			ArrayList<String> toEmailIds = new ArrayList<>();
			ArrayList<String> toSapIds = new ArrayList<>();
			String subject = "Live Online Session Alert: "+ scheduledession.getSessionName();
			//String fromEmailId = "ngasce.academics@nmims.edu";
			String fromEmailId = "ngasce@nmims.edu";
			String fromName = "NMIMS Global Access SCE";
			
			for (StudentCareerservicesBean studentBean : studentList) {
				toEmailIds.add(studentBean.getEmailId());
				toSapIds.add(studentBean.getSapid());//Add SAPID to list
			}

			toEmailIds.add("ashutosh.sultania.ext@nmims.edu");
//			toEmailIds.add("Harshad.Kasliwal@nmims.edu");
//			toEmailIds.add("Rahul.Banik@nmims.edu");
			toEmailIds.add("Shiv.Golani.EXT@nmims.edu");
			
			/*toEmailIds.add("sanketpanaskar@gmail.com");
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
			toEmailIds.add("jasmeet.kaur@nmims.edu");*/
			
			emailHelper.sendMassEmail(toSapIds,toEmailIds, fromEmailId, fromName, subject, htmlBody);
	 
		  } catch (Exception e) {
			String body = "Error in sending email "+e.getMessage() + " <br>";
			body = body + "Error while sending emails to "+emailIds;
			String encodedBody = new String(Base64.encodeBase64(body.getBytes()));
			
			sendTNSEMail(encodedBody, "[\"sanketpanaskar@gmail.com\",\"sheetal.gupta@nmims.edu\", \"sneha.utekar@nmims.edu\", \"bageshree@nmims.edu\"]", "Error in sending emails for "+scheduledession.getSessionName());
			logger.info("exception : "+e.getMessage());
	 
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
	
			String urlParameters = "fromemail=ngasce@nmims.edu&fromname=NMIMS Global Access SCE&tos=[\"no-reply@nmims.edu\"]"
					+ "&bccs="+mailerList+"&subject="+subject+"&body="+URLEncoder.encode(base64EncodedBody,"UTF-8")+"&apikey=gjyw9u0mm1ot1j65lr8ke1i3q2tbnlttuift3f6e7jxwttr50oeefm8vdrwq2f0loe5gk1kga93trjnrxru34to70an1gmcur8z1xa7x9yhq8u8ubto5c22c3e49r7s8";

			// Send post request
			con.setDoOutput(true);
			DataOutputStream wr = new DataOutputStream(con.getOutputStream());
			wr.writeBytes(urlParameters);
			wr.flush();
	
			wr.close();
	 
			int responseCode = con.getResponseCode();

			BufferedReader in = new BufferedReader(
			        new InputStreamReader(con.getInputStream()));
			String inputLine;
			StringBuffer response = new StringBuffer();
	 
			while ((inputLine = in.readLine()) != null) {
				response.append(inputLine);
			}
			in.close();
	 
		} catch (Exception e) {
			logger.info("exception : "+e.getMessage());
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
					logger.info(" Mail NOT sent  Error = "+e.getMessage());
					emailsNotSentId = emailsNotSentId +"," + recipient.get(i);
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

			// sending  EmailId to which email is not send
			if(!"".equals(emailsNotSentId)){
				 
				Message messages = new javax.mail.internet.MimeMessage(session); 
				try {
					messages.setFrom(new InternetAddress(from, "NMIMS Global Access SCE"));
				} catch (UnsupportedEncodingException e) {
				}
				//message.addRecipients(javax.mail.Message.RecipientType.TO, InternetAddress.parse("sanketpanaskar@gmail.com, sneha.utekar@nmims.edu, bageshree@nmims.edu, Sheetal.Gupta@nmims.edu"));
				messages.addRecipients(javax.mail.Message.RecipientType.TO, InternetAddress.parse("sanketpanaskar@gmail.com, sanket.panaskar@lntinfotech.com"));
				messages.setSubject("Emails Errors for "+subject); 
				messages.setText(emailsNotSentId); 
				Transport.send(messages);
			}
			
		} catch(Exception e) {
			logger.info("exception : "+e.getMessage());
		}


		logger.info("Mail sent successfully");

	}

	@SuppressWarnings("deprecation")
	@Async
	public void sendQueryPostedEmail(SessionDayTimeBean scheduledSession, SessionQueryAnswerCareerservicesBean sessionQuery, FacultyCareerservicesBean faculty,String toEmailAddress) {
		
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
						+"A query has been received for " +  scheduledSession.getSessionName() +". <br>"
						+ "Query: "+sessionQuery.getQuery()
						+"<br><br>"
						+"Please click <a href=\""+ SERVER_PATH + "acads/viewQueryForm?id=" +scheduledSession.getId() +"&eid="
						+URLEncoder.encode(AESencrp.encrypt(faculty.getFacultyId()))+"\">here</a> to respond to query."
						+"<br><br>"
						+"Thanks & Regards"
						+"<br>"
						+"NMIMS GLOBAL ACCESS SCHOOL FOR CONTINUING EDUCATION";



			message.setContent(body, "text/html; charset=utf-8");

			Transport.send(message);

		} catch(Exception e) {
			logger.info("exception : "+e.getMessage());
		}


		logger.info("Mail sent successfully");

	}
	
	@Async
	public void sendQueryAnswerPostedEmailToStudent(SessionQueryAnswerCareerservicesBean sessionQuery,StudentCareerservicesBean student,SessionDayTimeBean scheduledSession) {
		
		if(!"PROD".equalsIgnoreCase(ENVIRONMENT)){
			System.out.println("Not sending query email since this is not Prod.");
			return;
		}
		
		String userIdFromURL = null;
		try {
				userIdFromURL = AESencrp.encrypt(student.getSapid());
		} catch (Exception e) {
			logger.info("exception : "+e.getMessage());
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
						+" Your query has been responded for "+  scheduledSession.getSessionName() +". <br>"
						+" Session Date & Time : "+scheduledSession.getDate()+"-"+scheduledSession.getStartTime()+"<br>"
						+" and the resolution is available under " 
						+" Please click <a href=\""+ SERVER_PATH + "acads/postQueryForm?id=" +sessionQuery.getSessionId() +"&action=viewQueries&eid="+userIdFromURL+"\">here</a> "
						+" <br><br>"
						+" for any queries do call us on 1-800-1025-136 (Mon-Sat) 9am � 7pm "
						+" <br><br>"
						+" Thanks & Regards"
						+" <br>"
						+" NMIMS GLOBAL ACCESS SCHOOL FOR CONTINUING EDUCATION";



			message.setContent(body, "text/html; charset=utf-8");

			Transport.send(message);

		} catch(Exception e) {
			logger.info("exception : "+e.getMessage());
		}


		logger.info("Mail sent successfully");

	}
}
