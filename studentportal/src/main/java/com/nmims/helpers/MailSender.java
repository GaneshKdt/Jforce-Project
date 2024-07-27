package com.nmims.helpers;

import java.io.File;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.UnsupportedEncodingException;
import java.util.List;
import java.util.Properties;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.PasswordAuthentication;
import javax.mail.SendFailedException;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.FileSystemResource;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import com.nmims.beans.AdhocPaymentStudentPortalBean;
import com.nmims.beans.ForumStudentPortalBean;
import com.nmims.beans.LeadStudentPortalBean;
import com.nmims.beans.MailStudentPortalBean;
import com.nmims.beans.MassUploadTrackingSRBean;
import com.nmims.beans.ServiceRequestStudentPortal;
import com.nmims.beans.SessionQueryAnswerStudentPortal;
import com.nmims.beans.StudentStudentPortalBean;
import com.nmims.beans.VerifyContactDetailsBean;



@Component("mailer")
public class MailSender {
	
	@Value( "${SERVER_PATH}" )
	private String SERVER_PATH;
	
	@Value( "${ENVIRONMENT}" )
	private String ENVIRONMENT;
	
	private static final Logger loggerForEmails = LoggerFactory.getLogger("bulkEmailFromExcel");
	private static final Logger loggerForEmailCount = LoggerFactory.getLogger("emailCount");
	private static final Logger loggerForCourseQueryNotificationToFaculty = LoggerFactory.getLogger("courseQuery");

	

	private static final Logger adhocPaymentsLogger = LoggerFactory.getLogger("adhoc_payments");
	
	private static final Logger loggerForStudentSubjectCourse = LoggerFactory.getLogger("studentCourses");
	
	public static final String ISSUANCE_OF_MARKSHEET = "Issuance of Marksheet";
	public static final String DUPLICATE_MARKSHEET = "Duplicate Marksheet";
	public static final String ISSUANCE_OF_GRADESHEET = "Issuance of Gradesheet";
	public static final String DUPLICATE_GRADESHEET = "Duplicate Gradesheet";
	public static final String CERTIFICATE_DISPATCHED_DTDC = "Certificate DTDC";
	public static final String CERTIFICATE_DISPATCHED_DELHIVERY = "Certificate Delhivery";
	public static final String DUPLICATE_ID = "Duplicate I-Card";
	public static final String DUPLICATE_FEE_RECEIPT = "Duplicate Fee Receipt";
	public static final String DUPLICATE_STUDY_KIT = "Duplicate Study Kit"; 
	public static final String SINGLE_BOOK = "Single Book";
	public static final String TRANSCRIPT = "Issuance of Transcript";
	public static final String FINAL_CERTIFICATE = "Issuance of Final Certificate";
	
	private static Logger idCardCreationLogger=LoggerFactory.getLogger("id_card_creation");
	
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
	
	//Newly Added by Vikas to send mail for every Response Made//
	@Async
	public void notifyFacultyForResponse(ForumStudentPortalBean forum){
		try {
			Message message = getMessage();
			message.setFrom(new InternetAddress(from, "NMIMS Global Access SCE"));
			message.setRecipients(Message.RecipientType.TO,InternetAddress.parse(forum.getFacultyEmail()));
			message.setSubject("Reply On Forum");
			
			String body = "Dear Faculty, <br><br>"
					+ "A post has been received for <b>" + forum.getSubject() +"<br>"
					+"Forum: "+forum.getTitle()+"<br>"
					+"Please click <a href=\""+ SERVER_PATH + "acads/admin/viewForumResponse?id=" +forum.getId()
					+ "<br>Thanks & Regards"
					+ "<br>"
					+ "NMIMS GLOBAL ACCESS SCHOOL FOR CONTINUING EDUCATION";
			
			
			
			message.setContent(body, "text/html; charset=utf-8");
			Transport.send(message);
 
			//System.out.println("Sent Response Email To Faculty");
 
		} catch(Exception e) {
			throw new RuntimeException(e);
		}
		
		//System.out.println("Mail sent successfully");
	}
	//End//
	@Async
	public void sendRefundEmail(StudentStudentPortalBean student, AdhocPaymentStudentPortalBean bean) {
		try {

			Message message = getMessage();
			message.setFrom(new InternetAddress(from, "NMIMS Global Access SCE"));
			message.setRecipients(Message.RecipientType.TO,InternetAddress.parse(student.getEmailId()));
			message.setRecipients(Message.RecipientType.BCC,InternetAddress.parse("sanketpanaskar@gmail.com,nelson.soans@nmims.edu,jforcesolutions@gmail.com,tushar.jadhav@nmims.edu"));
			message.setSubject("Refund - "+bean.getDescription());

			String body = "";
 
			body = "Dear Student, <br>"
					+"We have processed your refund of Rs "+bean.getRefundAmount()+" against "+bean.getDescription()
					+ " to the same account which was used to make the Payment."
					+" If you have any queries or concern please call us on our TOLL free number 1-800-1025-136 or email us at NGASCE@nmims.edu";

			body = body +"<br>" 
					+"<br>Thanks & Regards"
					+ "<br>"
					+ "NMIMS GLOBAL ACCESS SCHOOL FOR CONTINUING EDUCATION";


			message.setContent(body, "text/html; charset=utf-8");

			Transport.send(message);

			loggerForEmailCount.info("Total 1 Mail sent via(SMTP) from "+from+", Email Subject : Refund "+bean.getDescription()+", for sendRefundEmail");
			//System.out.println("Sent Refund Email");

		} catch(Exception e) {
			
		}


		//System.out.println("Mail sent successfully");

	}
public void sendOtpForContactDetails(VerifyContactDetailsBean bean){
		
		try {
			Message message = getMessage();
			message.setFrom(new InternetAddress(from, "NMIMS Global Access SCE"));
			message.setRecipients(Message.RecipientType.TO,InternetAddress.parse(bean.getEmailId()));
			message.setSubject("Your OTP for NGASCE Student Zone");
			
			String body = "Dear "+ bean.getFirstName()+", \n\n"
					+"Your OTP for NGASCE Student Zone is "+bean.getOtp()
					+"\n\n"
					+"Thanks & Regards"
					+"\n"
					+"NMIMS GLOBAL ACCESS SCHOOL FOR CONTINUING EDUCATION";
			
			
			message.setText(body);
 
			Transport.send(message);
 
			loggerForEmailCount.info("Total 1 Mail sent via(SMTP) from "+from+", Email Subject : Your OTP for NGASCE Student Zone, for sendOtpForLeadLogin");
			//System.out.println("Sent Password Email");
 
		} catch(Exception e) {
			throw new RuntimeException(e);
		}

		//System.out.println("Mail sent successfully");
	}
	@Async
	public void sendPasswordEmail(String name, String email, String password) throws Exception {
		try {
			Message message = getMessage();
			message.setFrom(new InternetAddress(from, "NMIMS Global Access SCE"));
			message.setRecipients(Message.RecipientType.TO,InternetAddress.parse(email));
			message.setSubject("Your Password for NGASCE Student Zone");
			
			String body = "Dear "+ name +", \n\n"
						+ "Your password for NGASCE Student Zone is " + password
						+ "\n\n"
						+ "Thanks & Regards"
						+ "\n"
						+ "NMIMS GLOBAL ACCESS SCHOOL FOR CONTINUING EDUCATION";
				
			message.setText(body);
			Transport.send(message);
	 
			loggerForEmailCount.info("Total 1 forgotPassword Mail sent via(SMTP) from " + from + ", Email Subject : Your Password for NGASCE Student Zone");
		}
		catch(SendFailedException ex) {
			loggerForEmailCount.error("Invalid email address! Failed to send forgot password email to user: " + name + ", with emailId: " + email + 
										", Exception thrown: " + ex.toString());
			throw new RuntimeException("Unable to send Password to your registered Email Address." + 
										"Please try again or contact ngasce@nmims.edu to update your Email Address!");
		}
		catch(MessagingException ex) {
			loggerForEmailCount.error("Authentication Error! Failed to send forgot password email to user: " + name + ", with emailId: " + email + 
										", Exception thrown: " + ex.toString());
			throw new RuntimeException("Unable to send Password to your registered Email Address." + 
										"Please try again or contact ngasce@nmims.edu to Reset your Password!");
		}
	}
	
	@Async
	public void sendAdhocPaymentEmail(AdhocPaymentStudentPortalBean bean)
	{
		adhocPaymentsLogger.info("MailSender.sendAdhocPaymentEmail() - START");
		try {
			if(!"PROD".equalsIgnoreCase(ENVIRONMENT)){
				return;
			}

			Message message = getMessage();
			message.setFrom(new InternetAddress(from, "NMIMS Global Access SCE"));
			message.setRecipients(Message.RecipientType.TO,InternetAddress.parse(bean.getEmailId()));
			message.setRecipients(Message.RecipientType.BCC,InternetAddress.parse("sanketpanaskar@gmail.com, nelson.soans@nmims.edu "));
			message.setSubject(bean.getPaymentType()+" - Payment Confirmation");

			adhocPaymentsLogger.info("Sending Mail TO:"+bean.getEmailId());
			adhocPaymentsLogger.info("Mail Subject:"+bean.getPaymentType()+" - Payment Confirmation");
			String body = "";

			body = "Dear User, <br>"
					+ "We have received Payment for "+bean.getDescription()+"<br>"
					+ "Amount Rs "+bean.getAmount()+"/-<br>";



			body = body + "<br>Thanks & Regards"
					+ "<br>"
					+ "NMIMS GLOBAL ACCESS SCHOOL FOR CONTINUING EDUCATION";


			message.setContent(body, "text/html; charset=utf-8");

			Transport.send(message);

			loggerForEmailCount.info("Total 1 Mail sent via(SMTP) from "+from+", Email Subject : "+bean.getPaymentType()+" - Payment Confirmation , for sendAdhocPaymentEmail");
			//System.out.println("Sent Adhoc Email");
			adhocPaymentsLogger.info("MailSender.sendAdhocPaymentEmail() - END");

		} catch(Exception e) {
			adhocPaymentsLogger.error("Error while sending mail to:"+bean.getEmailId());
		}


		//System.out.println("Mail sent successfully");
	}
	
	@Async
	public void sendSREmail(ServiceRequestStudentPortal bean, StudentStudentPortalBean student) {
		try {
			int tat = 7;
			String ngasceRecipient = "ngasce@nmims.edu";
			String serviceRequestType = bean.getServiceRequestType();
			if(ServiceRequestStudentPortal.DUPLICATE_FEE_RECEIPT.equals(serviceRequestType)){
				tat = 3;
				ngasceRecipient += " , Rashmi.Khedkar@nmims.edu";
			}else if(ServiceRequestStudentPortal.ASSIGNMENT_REVALUATION.equals(serviceRequestType) || ServiceRequestStudentPortal.OFFLINE_ASSIGNMENT_REVALUATION.equals(serviceRequestType)){
				tat = 20;
				ngasceRecipient += " , ngasce.exams@nmims.edu";
			}else if(ServiceRequestStudentPortal.DUPLICATE_STUDY_KIT.equals(serviceRequestType)){
				tat = 3;
				ngasceRecipient += " , Rashmi.Khedkar@nmims.edu";
			}else if(ServiceRequestStudentPortal.DUPLICATE_ID.equals(serviceRequestType)){
				tat = 3;
				ngasceRecipient += " , Rashmi.Khedkar@nmims.edu";
			}else if(ServiceRequestStudentPortal.CHANGE_IN_DOB.equals(serviceRequestType)){
				tat = 3;
				ngasceRecipient += " , ngasce.admission@nmims.edu";
			}else if(ServiceRequestStudentPortal.SINGLE_BOOK.equals(serviceRequestType)){
				tat = 3;
				ngasceRecipient += " , Rashmi.Khedkar@nmims.edu";
			}else if(ServiceRequestStudentPortal.TEE_REVALUATION.equals(serviceRequestType)){
				tat = 20;
				ngasceRecipient += " , ngasce.exams@nmims.edu";
			}else if(ServiceRequestStudentPortal.OFFLINE_TEE_REVALUATION.equals(serviceRequestType)){
				tat = 20;
				ngasceRecipient += " , ngasce.exams@nmims.edu";
			}else if(ServiceRequestStudentPortal.PHOTOCOPY_OF_ANSWERBOOK.equals(serviceRequestType)){
				tat = 3;
				ngasceRecipient += " , ngasce.exams@nmims.edu";
			}else if(ServiceRequestStudentPortal.CHANGE_IN_ID.equals(serviceRequestType)){
				tat = 3;
				ngasceRecipient += " , Rashmi.Khedkar@nmims.edu";
			}else if(ServiceRequestStudentPortal.CHANGE_IN_NAME.equals(serviceRequestType)){
				tat = 2;
				ngasceRecipient += " , ngasce.admission@nmims.edu";
				ngasceRecipient += " , manasvi.malve@nmims.edu";
				ngasceRecipient += " , jforce.solution@gmail.com";
			}else if(ServiceRequestStudentPortal.ISSUEANCE_OF_MARKSHEET.equals(serviceRequestType)){
				tat = 12;
				ngasceRecipient += " , ngasce.exams@nmims.edu";
			}else if(ServiceRequestStudentPortal.ISSUEANCE_OF_CERTIFICATE.equals(serviceRequestType)){
				tat = 20;
				ngasceRecipient += " , ngasce.exams@nmims.edu";
			}else if(ServiceRequestStudentPortal.ISSUEANCE_OF_TRANSCRIPT.equals(serviceRequestType)){
				tat = 12;
				ngasceRecipient += " , ngasce.exams@nmims.edu";
			}else if(ServiceRequestStudentPortal.ISSUEANCE_OF_PROVISIONAL_CERTIFICATE.equals(serviceRequestType)){
				tat = 3;
				ngasceRecipient += " , ngasce.exams@nmims.edu";
			}else if(ServiceRequestStudentPortal.ISSUEANCE_OF_BONAFIDE.equals(serviceRequestType)){
				tat = 5;
				ngasceRecipient += " , manasvi.malve@nmims.edu";
				ngasceRecipient += " , ngasce.admission@nmims.edu";
				ngasceRecipient += " , jforce.solution@gmail.com";
			}else if(ServiceRequestStudentPortal.CHANGE_IN_CONTACT_ADDRESS.equals(serviceRequestType)){
				tat = 3;
				ngasceRecipient += " , ngasce.admission@nmims.edu";
			}else if(ServiceRequestStudentPortal.DE_REGISTERED.equals(serviceRequestType)){
				tat = 3;
				ngasceRecipient += " , manasvi.malve@nmims.edu";
			}else if(ServiceRequestStudentPortal.CHANGE_IN_PHOTOGRAPH.equals(serviceRequestType)){
				tat = 5;
				ngasceRecipient += " , ngasce.admission@nmims.edu";
				ngasceRecipient += " , manasvi.malve@nmims.edu";
				ngasceRecipient += " , jforce.solution@gmail.com";
			}else if(ServiceRequestStudentPortal.PROGRAM_DE_REGISTRATION.equals(serviceRequestType)) {
//				tat for this confirmed with Shiv
				tat = 1;
				ngasceRecipient += " , manasvi.malve@nmims.edu";
				ngasceRecipient += " , ngasce.admission@nmims.edu";
				ngasceRecipient += " , jforce.solution@gmail.com";
				ngasceRecipient += " , shiv.golani.ext@nmims.edu";
				ngasceRecipient += " , gayatri.kaley.ext@nmims.edu";
			}else if(ServiceRequestStudentPortal.SUBJECT_REPEAT_MBAWX.equals(serviceRequestType) || ServiceRequestStudentPortal.SUBJECT_REPEAT.equals(serviceRequestType)) {
				tat = 7;
				ngasceRecipient += " , ashutosh.sultania.ext@nmims.edu";
			}
			
 
			Message message = getMessage();
			message.setFrom(new InternetAddress(from, "NMIMS Global Access SCE"));
			message.setRecipients(Message.RecipientType.TO,InternetAddress.parse(student.getEmailId()));
			message.setSubject("Service Request Received");
		
			//System.out.println(student);
			//System.out.println("ngasceRecipient--->"+ngasceRecipient);
			String amount = bean.getAmount() != null ? bean.getAmount() : "0";
			
			String body = "Dear "+ student.getFirstName()+" <br><br>"
						+ "Greetings from NMIMS Global Access School for Continuing Education!<br><br>"
						+ "Thank you for submitting your request for "+bean.getServiceRequestType() + ". <br>";
			
			//Commented temporary due to COVID
			/*
			if(ServiceRequest.ISSUEANCE_OF_MARKSHEET.equals(serviceRequestType)){
				body += "Service Request Number " + bean.getId() + " been created and you will be receiving your Marksheet within " + tat + " working Days.<br>";
			}else if(ServiceRequest.ISSUEANCE_OF_CERTIFICATE.equals(serviceRequestType)){
				body += "Service Request Number " + bean.getId() + " been created and you will be receiving your Final Certificate within " + tat + " working Days.<br>";
			}else{
				body += "Service Request Number " + bean.getId() + " been created and our Counsellor will respond to you in the next " + tat + " Days.<br>";
			}
			*/
			
			if(ServiceRequestStudentPortal.ISSUEANCE_OF_CERTIFICATE.equals(serviceRequestType)){

				/*
				 * body updated by Harsh based on the mailer content from case #00407401
				 * 
				 * */
				body += "Service Request Number " + bean.getId() + " has been created. <br>";
						/*
						 * body updated by Harsh based on the mailer content from case #00436970
						 * 
					     * "Alternatively, we will also provide you with an E-copy of the Certificate with digital signatures , within  7 working days.  <br>";
					     */
			
			}else if (ServiceRequestStudentPortal.ISSUEANCE_OF_MARKSHEET.equals(serviceRequestType)) {
				
				/*
				 * body updated by Harsh based on the mailer content from case #00407401
				 * 
				 * */
				body += "Service Request Number " + bean.getId() + " has been created. <br>";
					/*
					 *  body updated by Harsh based on the mailer content from case #00436970
					 *  
					 *  "Alternatively ,we will email you a copy of your Mark sheet with digital signatures, within 7 working days. <br>";
					 */
			
			}else if (ServiceRequestStudentPortal.ISSUEANCE_OF_GRADESHEET.equals(serviceRequestType)) {

				/*
				 * body updated by Harsh based on the mailer content from case #00407401
				 * 
				 * */
				body += "Service Request Number " + bean.getId() + " has been created. <br>" ;
						
						/*
						 * body updated by Harsh based on the mailer content from case #00436970
						 * 
					 	 * "Alternatively, we will email you a copy of the Grade sheet with digital signatures within 7 working days. <br>";
						 */
						
			}else if (ServiceRequestStudentPortal.ISSUEANCE_OF_BONAFIDE.equals(serviceRequestType)) {
				
				/*
				 * body updated by Harsh based on the mailer content from case #00407401
				 * 
				 * */
				body += "Service Request Number " + bean.getId() + " has been created. <br>";
						/*
						 *  body updated by Harsh based on the mailer content from case #00436970
						 *  
						 * "To assist your further, we will email you a soft copy of the <b>Bonafide letter</b> within 7 working. <br>";
						 */
				
			}
			else if (ServiceRequestStudentPortal.CHANGE_IN_ID.equals(serviceRequestType) ) {

				/*
				 * body updated by Harsh based on the mailer content from case #00407401
				 * 
				 * */
				body += "Service Request Number " + bean.getId() + " has been created. <br>";
				
			}else if (ServiceRequestStudentPortal.CHANGE_IN_DOB.equals(serviceRequestType) 
						|| ServiceRequestStudentPortal.CHANGE_IN_NAME.equals(serviceRequestType)) {
				
				body += "Service Request Number " + bean.getId() + " has been created, "
					 +  "and will be closed in 3 working days subject to compliance on required documentations. <br>";
			
			}else if (ServiceRequestStudentPortal.ISSUEANCE_OF_TRANSCRIPT.equals(serviceRequestType)) {

				/*
				 * body updated by Harsh based on the mailer content from case #00407401
				 * 
				 * */
				body += "Service Request Number " + bean.getId() + " has been created. <br>";
						/*
						 *  body updated by Harsh based on the mailer content from case #00436970
						 *  
					     * "Alternatively, we will provide you with an e-copy of the Transcript with digital signatures, within 7 working days. <br>";
					     */
				
			}else if ( ServiceRequestStudentPortal.DUPLICATE_ID.equals(serviceRequestType) ) {
				
				/*
				 * body updated by Harsh based on the mailer content from case #00407401
				 * 
				 * */
				body += "Service Request Number " + bean.getId() + " has been created. <br>"+
						"As per your request, we are in process of issuance of duplicate fee receipt within 7 working days. <br>";
				
			}else if ( ServiceRequestStudentPortal.DUPLICATE_FEE_RECEIPT.equals(serviceRequestType) ) {
				
				/*
				 * body updated by Harsh based on the mailer content from case #00407401
				 * 
				 * */
				body += "As per your request, we are in process of issuing the duplicate fee receipt to you. You shall receive it within 7 working days. <br>" +
						"Service Request Number " + bean.getId() + " has been created. <br>";
				
			}else if ( ServiceRequestStudentPortal.DUPLICATE_STUDY_KIT.equals(serviceRequestType) ) {
				
				/*
				 * body updated by Harsh based on the mailer content from case #00407401
				 * 
				 * */
				body += "As per your request, we are in process of issuing duplicate study kit to you. <br>";
				
			}
			
			body += "Please quote Service Request Number " + bean.getId() + " for all future communication related to this Service Request.<br><br>"
					
					+"Student Name: "+student.getFirstName() +  " " + student.getLastName() + " <br>"
					+"Student ID: "+student.getSapid()+ " <br>"
					+"Service Request Number: "+bean.getId() + " <br>"
					+"Service Request Name: "+bean.getServiceRequestType() + " <br>"
					+"Service Request Description: "+bean.getDescription() + " <br>"
					+"Amount Paid (INR): "+ amount + "/- " + " <br><br>"
					+"Thanks & Regards"
					+"<br>"
					+"NMIMS GLOBAL ACCESS SCHOOL FOR CONTINUING EDUCATION";
			
			
			//System.out.println("body = "+body);
			
			message.setContent(body, "text/html; charset=utf-8");
			Transport.send(message);
 
			loggerForEmailCount.info("Total 1 Mail sent via(SMTP) from "+from+", Email Subject : Service Request Received , for sendSREmail");
			//System.out.println("Sent Service Request Email");
			
			message = getMessage();
			message.setFrom(new InternetAddress(from, "NMIMS Global Access SCE"));
			message.setRecipients(Message.RecipientType.TO,InternetAddress.parse(ngasceRecipient));
			message.setSubject("Service Request Received");
			
			body = "Dear Team <br><br>,"
					+ "New Service Request Received. <br>"
					+"Service Request Number: "+bean.getId() + " <br>"
					+"Service Request Name: "+bean.getServiceRequestType() + " <br>"
					+"Service Request Description: "+bean.getDescription() + " <br>"
					+"Student ID: "+student.getSapid()+ " <br>";
			
			//Bonafide sr cetificate changed to free - Aneel
//			if(ServiceRequestStudentPortal.ISSUEANCE_OF_BONAFIDE.equals(serviceRequestType)){
//				body += "   Service Request Amount : "+ bean.getAmount() +" <br> "
//						+ " Number Of Copies : "+bean.getNoOfCopies() +" <br> "
//						+ " Program : "+bean.getProgram()+" <br> "
//					    + " Student Address : "+student.getAddress()+" <br> "
//					    + " Payment Status : "+bean.getTranStatus(); 
//			 }		
					
			body += "Student Name: "+student.getFirstName() +  " " + student.getLastName() + " <br>"
					+ "Defined Turn Around Time (TAT): " + tat +" Days<br><br>"
					+ "<b>Kindly respond back to the student in the defined TAT. Please do inform the student in case of delay.</b><br><br>"
					+"Thanks & Regards"
					+"<br>"
					+"NMIMS GLOBAL ACCESS SCHOOL FOR CONTINUING EDUCATION";
			
			//System.out.println("body = "+body);
			
			message.setContent(body, "text/html; charset=utf-8");
			Transport.send(message);
 
			loggerForEmailCount.info("Total 1 Mail sent via(SMTP) from "+from+", Email Subject : Service Request Received(NGASCE) , for sendSREmail");
			//System.out.println("Sent Service Request Email to NGASCE Team");
 
		} catch(Exception e) {
			throw new RuntimeException(e);
		}
		

		//System.out.println("Mail sent successfully");
		
	}
	
	/**
	 * Send Service Request Confirmation mail to the Student. 
	 * @param serviceRequestId - Service Request ID
	 * @param serviceRequestType - Service Request Type
	 * @param serviceRequestDescription - Service Request Description
	 * @param amount - Service Request Amount paid
	 * @param additionalContent - additional body content to be attached with the mail body
	 * @param studentNo - studentNo of the Student
	 * @param studentFirstName - firstName of the Student
	 * @param studentLastName - lastName of the Student
	 * @param studentEmailAddress - emailId of the Student
	 */
	@Async
	public void sendSRConfirmationEmail(Long serviceRequestId, String serviceRequestType, String serviceRequestDescription, Integer amount, String additionalContent, 
										Long studentNo, String studentFirstName, String studentLastName, String studentEmailAddress) {
		try {
			Message message = getMessage();
			message.setFrom(new InternetAddress(from, "NMIMS Global Access SCE"));
			message.setRecipients(Message.RecipientType.TO,InternetAddress.parse(studentEmailAddress));
			message.setSubject("Service Request Received");
			
			String body = "Dear " + studentFirstName + ", <br><br>"
						+ "Greetings from NMIMS Global Access School for Continuing Education! <br><br>"
						+ "Thank you for submitting your request for " + serviceRequestType + ". <br>";
			
			body += additionalContent;
			
			body += "Please quote Service Request Number " + serviceRequestId + " for all future communication related to this Service Request. <br><br>"
					+ "Student Name: " + studentFirstName + " " + studentLastName + " <br>"
					+ "Student ID: " + studentNo + " <br>"
					+ "Service Request Number: " + serviceRequestId + " <br>"
					+ "Service Request Name: "+ serviceRequestType + " <br>"
					+ "Service Request Description: "+ serviceRequestDescription + " <br>"
					+ "Amount Paid (INR): " + amount + "/-" + " <br><br>"
					+ "Thanks & Regards <br>"
					+ "NMIMS GLOBAL ACCESS SCHOOL FOR CONTINUING EDUCATION";
			
			message.setContent(body, "text/html; charset=utf-8");
			Transport.send(message);
 
			loggerForEmailCount.info("Total 1 SR Confirmation Mail sent via(SMTP) from {}, to {}, Email Subject: Service Request Received", from, studentEmailAddress);
		}
		catch(SendFailedException ex) {
			loggerForEmailCount.error("Invalid email address! Failed to send SR Confirmation email to Student: {}, with emailId: {}, Exception thrown: {}", 
										studentNo, studentEmailAddress, ex.toString());
		}
		catch(MessagingException ex) {
			loggerForEmailCount.error("Authentication Error! Failed to send SR Confirmation email to Student: {}, with emailId: {}, Exception thrown: {}", 
										studentNo, studentEmailAddress, ex.toString());
		}
		catch(Exception ex) {
//			ex.printStackTrace();
			loggerForEmailCount.error("Error occured while sending SR Confirmation email to Student: {}, with emailId: {}, Exception thrown: {}", 
										studentNo, studentEmailAddress, ex.toString());
		}
	}
	
	/**
	 * Send Service Request Created mail to the Concerned team. 
	 * @param serviceRequestId - Service Request ID
	 * @param serviceRequestType - Service Request Type
	 * @param serviceRequestDescription - Service Request Description
	 * @param tat - Service Request TurnAround Time
	 * @param recipients - recipients addressed for the mail
	 * @param additionalContent - additional body content to be attached with the mail body
	 * @param studentNo - studentNo of the Student
	 * @param studentFirstName - firstName of the Student
	 * @param studentLastName - lastName of the Student
	 */
	@Async
	public void sendSrCreatedMail(Long serviceRequestId, String serviceRequestType, String serviceRequestDescription, Integer tat, InternetAddress[] recipients,
									String additionalContent, Long studentNo, String studentFirstName, String studentLastName) {
		try {
			Message message = getMessage();
			message.setFrom(new InternetAddress(from, "NMIMS Global Access SCE"));
			message.setRecipients(Message.RecipientType.TO, recipients);
			message.setSubject("Service Request Received");
			
			String body = "Dear Team, <br><br>"
					+ "New Service Request Received. <br>"
					+ "Service Request Number: " + serviceRequestId + " <br>"
					+ "Service Request Name: " + serviceRequestType + " <br>"
					+ "Service Request Description: " + serviceRequestDescription + " <br>"
					+ "Student ID: " + studentNo + " <br>";
			
			body += additionalContent;
			
			body += "Student Name: " + studentFirstName +  " " + studentLastName + " <br>"
					+ "Defined Turn Around Time (TAT): " + tat +" Days <br><br>"
					+ "<b>Kindly respond back to the student in the defined TAT. Please do inform the student in case of delay.</b><br><br>"
					+ "Thanks & Regards <br>"
					+ "NMIMS GLOBAL ACCESS SCHOOL FOR CONTINUING EDUCATION";
			
			message.setContent(body, "text/html; charset=utf-8");
			Transport.send(message);
 
			loggerForEmailCount.info("Total 1 SR created Mail sent via(SMTP) from {} to {}, Email Subject: Service Request Received(NGASCE)", from, recipients.toString());
		}
		catch(SendFailedException ex) {
			loggerForEmailCount.error("Invalid email address! Failed to send SR created email to {}, Exception thrown: {}", recipients.toString(), ex.toString());
		}
		catch(MessagingException ex) {
			loggerForEmailCount.error("Authentication Error! Failed to send SR created email to {}, Exception thrown: {}", recipients.toString(), ex.toString());
		}
		catch(Exception ex) {
//			ex.printStackTrace();
			loggerForEmailCount.error("Error occured while sending SR created email to {}, Exception thrown: {}", recipients.toString(), ex.toString());
		}
	}
	
	@Async
	public void sendProcessRefundMail(ServiceRequestStudentPortal sr){
		try{
			Message message = getMessage();
			message.setFrom(new InternetAddress(from, "NMIMS Global Access SCE"));
			message.setRecipients(Message.RecipientType.TO,InternetAddress.parse("sanketpanaskar@gmail.com"));
			message.setSubject("Refund SR");
			String serviceRequestType = sr.getServiceRequestType();
			
			//System.out.println(sr.getSapId());
			
			String amount = sr.getAmount() != null ? sr.getAmount() : "0";
			
			String body = "Dear All, <br><br>"
					+ " Please refund for service request id -> "+sr.getId()+" <br><br>"
							+ " For Amount --> "+amount;
			message.setContent(body, "text/html; charset=utf-8");
			//System.out.println("Process refund mail sent");
			Transport.send(message);
			loggerForEmailCount.info("Total 1 Mail sent via(SMTP) from "+from+", Email Subject : Refund SR , for sendProcessRefundMail");
		}catch(Exception e){
			
		}
		
	}
	
	@Async
	public void sendSRClosureEmail(ServiceRequestStudentPortal bean, String firstName, String email) {
		try {
			Message message = getMessage();
			message.setFrom(new InternetAddress(from, "NMIMS Global Access SCE"));
			message.setRecipients(Message.RecipientType.TO,InternetAddress.parse(email));
			message.setSubject("Service Request Closed");
			
			String serviceRequestType = bean.getServiceRequestType();
			
//			String amount = bean.getAmount() != null ? bean.getAmount() : "0";
			
			String body = "Dear "+ firstName +", <br><br>"
					
					+ "Greetings from <b>NMIMS Global Access School for Continuing Education!</b><br><br>"
					+ "This has reference to your Service Request Number " + bean.getId() + " for "+bean.getServiceRequestType() + " dated " + bean.getCreatedDate().substring(0, 10)+ ".<br>"

					+ "<br><br>";
			
			if(ServiceRequestStudentPortal.ISSUEANCE_OF_MARKSHEET.equals(serviceRequestType)){
				
				/*
				 * updated body for case 00442153 
				 * 
				 */

				body += "We have processed your request for the issuance of Mark Sheet and it is dispatched to your address.<br>";
				
			}else if(ServiceRequestStudentPortal.ISSUEANCE_OF_CERTIFICATE.equals(serviceRequestType)){
				
				/*
				 * updated body for case 00442153 
				 * 
				 */
				
				body += "We have processed your request for Final Certificate and it is dispatched to your address.<br>"; 
 	
			}else if(ServiceRequestStudentPortal.EXIT_PROGRAM.equals(serviceRequestType)){
 
				body += "Please note your Exit Program request has been completed and Servie Request is now closed. You can now apply "
						+ "for Certificate via the Service Request console for 'Issuance of Final Certificate'.<br><br>";
				
	        }else if(ServiceRequestStudentPortal.ISSUEANCE_OF_GRADESHEET.equals(serviceRequestType)){
				
				/*
				 * updated body for case 00442153 
				 * 
				 */
				
				body += "We have processed your request for the issuance of Gradesheet and it is dispatched to your address.<br>"; 
 	
			}else if(ServiceRequestStudentPortal.ISSUEANCE_OF_TRANSCRIPT.equals(serviceRequestType)){
				
				/*
				 * updated body for case 00442153 
				 * 
				 */
				
				body += "We have processed your request for the issuance of transcript and it is dispatched to your address.<br>"; 
 	
			}else if(ServiceRequestStudentPortal.TEE_REVALUATION.equals(serviceRequestType)){
				
				/*
				 * updated body for case 00442153 
				 * 
				 */
				
				body += "We have processed your request for Revaluation of Term End Exam Marks. Only students who had applied for " +
						"Term End Exam Revaluation can login to Student Zone -- Exams – Exam Results to view the " +
						"TEE revaluation results. <br>"; 
 	
			}else if(ServiceRequestStudentPortal.CHANGE_IN_DOB.equals(serviceRequestType)) {
				//added for case 00531028
				body += "Your request is Completed. For Identity card with revised DOB, " +
						"please raise a Service Request for Issuance of Duplicate ID card. <br><br>";
			}
			
			body +="For any further assistance do call us on 1-800-1025-136 Mon-Sat (9am � 7pm). We are closed on public holidays. In an effort to enhance our Service <br>"
					+"guidelines kindly help us with your feedback by clicking on the below link"
					+"<br><br>"
					+"Kindly click <a href=\"http://ngasce.force.com/TakeSurvey?id=a0u90000004K9Pv&stnu="+ bean.getSapId() +"\">FeedBack </a> to give your feedback <br><br>"
					+"Thanks & Regards"
					+"<br>"
					+"NMIMS GLOBAL ACCESS SCHOOL FOR CONTINUING EDUCATION";
			
			message.setContent(body, "text/html; charset=utf-8");
			Transport.send(message);
 
			loggerForEmailCount.info("Total 1 Mail sent via(SMTP) from " + from + 
					", Email Subject : Service Request Closed, for sendSRClosureEmail, to student: " + bean.getSapId());
		
		}catch(Exception ex) {
			loggerForEmailCount.info("Failed to send Mail via(SMTP) for Service Request Closure, to student: " + 
					bean.getSapId() + ", due to " + ex.toString());
		}
	}

	@Async
	public void sendSRClosureEmailWithAttachment(ServiceRequestStudentPortal bean, String firstName, String email,String localPath,String awsPath ) {
		try {
			
			String serviceRequestType = bean.getServiceRequestType();
			MimeMessage mimeMessage = (MimeMessage) getMessage();
			MimeMessageHelper mimeMessageHelper = new MimeMessageHelper(mimeMessage,true);
			mimeMessageHelper.setSubject("Service Request Closed");
			mimeMessageHelper.setFrom(new InternetAddress(from));
			mimeMessageHelper.setTo(email);
		    
			FileSystemResource file = new FileSystemResource(new File(localPath));
			mimeMessageHelper.addAttachment(file.getFilename(), file);
			String body = "Dear "+ firstName +", <br><br>"
					
					+ "Greetings from <b>NMIMS Global Access School for Continuing Education!</b><br><br>"
					+ "This has reference to your Service Request Number " + bean.getId() + " for "+bean.getServiceRequestType() + " dated " + bean.getCreatedDate().substring(0, 10)+ ".<br>"

					+ "<br><br>";
			
//			if(ServiceRequestStudentPortal.ISSUEANCE_OF_BONAFIDE.equals(serviceRequestType)) {
//				body+= "Please download your Bonafide certificate from the below link : <br>"
//						+ awsPath+"<br><br>";
//			}
			
			body +="For any further assistance do call us on 1-800-1025-136 Mon-Sat (9am - 7pm). We are closed on public holidays. In an effort to enhance our Service <br>"
					+"guidelines kindly help us with your feedback by clicking on the below link"
					+"<br><br>"
					+"Kindly click <a href=\"http://ngasce.force.com/TakeSurvey?id=a0u90000004K9Pv&stnu="+ bean.getSapId() +"\">FeedBack </a> to give your feedback <br><br>"
					+"Thanks & Regards"
					+"<br>"
					+"NMIMS GLOBAL ACCESS SCHOOL FOR CONTINUING EDUCATION";
			
			
			mimeMessageHelper.setText(body,true);
			Transport.send(mimeMessage);
			loggerForEmailCount.info("Total 1 Mail sent via(SMTP) from " + from + 
					", Email Subject : Service Request Closed, for sendSRClosureEmail, to student: " + bean.getSapId());
		
		}catch(Exception ex) {
			loggerForEmailCount.info("Failed to send Mail via(SMTP) for Service Request Closure, to student: " + 
					bean.getSapId() + ", due to " + ex.toString());
		}
		String fileDeletionMessage = deleteLocalAttachment(localPath);
		loggerForEmails.info("E-Bonafide localFile deletion message: {}", fileDeletionMessage);
	}
	
	@Async
	public void sendSRCancellationEmail(ServiceRequestStudentPortal sr, String firstName, String email) {
		try {
			Message message = getMessage();
			message.setFrom(new InternetAddress(from, "NMIMS Global Access SCE"));
			message.setRecipients(Message.RecipientType.TO,InternetAddress.parse(email));
			message.setSubject("Service Request Cancelled");
			
//			String amount = sr.getAmount() != null ? sr.getAmount() : "0";
			
			String body = "Dear "+ firstName +", <br><br>"
						+ "Greetings from NMIMS Global Access School for Continuing Education!<br><br>"
						+ "This has reference to your Service Request Number " + sr.getId() + " for " + sr.getServiceRequestType() + " dated " + sr.getCreatedDate().substring(0, 10) + ".<br>"
						+ "<br><br>"
						+ "Please note our counsellor has addressed your Service Request and Service Request has been cancelled. <br/>"
						+ "Reason for Cancellation of Service Request: " + sr.getCancellationReason() + " <br/>"
						+ "For any further assistance do call us on 1-800-1025-136 Mon-Sat (9am-7pm). We are closed on public holidays. In an effort to enhance our Service <br>"
						+ "guidelines kindly help us with your feedback by clicking on the below link"
						+ "<br><br>"
						+ "Kindly click <a href=\"http://ngasce.force.com/TakeSurvey?id=a0u90000004K9Pv&stnu=" + sr.getSapId() + "\">FeedBack </a> to give your feedback <br><br>"
						+ "Thanks & Regards"
						+ "<br>"
						+ "NMIMS GLOBAL ACCESS SCHOOL FOR CONTINUING EDUCATION";
			
			//System.out.println("body = "+ body);
			
			message.setContent(body, "text/html; charset=utf-8");
			Transport.send(message);
 
			loggerForEmailCount.info("Total 1 Mail sent via(SMTP) from " + from + ", Email Subject : Service Request Cancelled, for sendSRCancellationEmail, to student: " + sr.getSapId());
		} 
		catch(Exception ex) {
			loggerForEmailCount.info("Failed to send Mail via(SMTP) for Service Request Cancellation, to student: " + sr.getSapId() + ", due to " + ex.toString());
		}
	}
	
	@Async
	public void sendForumAbuseEmail(ForumStudentPortalBean bean, String reportee, String reportedOn) {
		try {
 
			Message message = getMessage();
			message.setFrom(new InternetAddress(from, "NMIMS Global Access SCE"));

			
			message.addRecipients(Message.RecipientType.TO, 
                    InternetAddress.parse("sanketpanaskar@gmail.com"));
			
			message.addRecipients(Message.RecipientType.CC, 
                    InternetAddress.parse("sanketpanaskar@gmail.com"));
			
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
			
			
			//System.out.println("body = "+body);
			
			message.setContent(body, "text/html; charset=utf-8");
			Transport.send(message);
 
			loggerForEmailCount.info("Total 1 Mail sent via(SMTP) from "+from+", Email Subject : Abuse Reported, for sendForumAbuseEmail");
			//System.out.println("Sent Abuse Report Email");
 
		} catch(Exception e) {
			
		}

		
	}
	
	public static void main(String[] args) throws UnsupportedEncodingException {
		
		PersonStudentPortalBean p = new PersonStudentPortalBean();
		p.setEmail("sanketpanaskar@gmail.com");
		p.setPassword("pass@1234");
		
		
		
		
		final String username = "ngasce@nmims.edu";
		final String password = "May@2014#";
 
		Properties props = new Properties();
		props.put("mail.smtp.auth", "true");
		props.put("mail.smtp.starttls.enable", "true");
		props.put("mail.smtp.host", "192.168.2.99");
		props.put("mail.smtp.port", "25");
 
		Session session = Session.getInstance(props,
		  new javax.mail.Authenticator() {
			protected PasswordAuthentication getPasswordAuthentication() {
				return new PasswordAuthentication(username, password);
			}
		  });
 
		try {
 
			Message message = new MimeMessage(session);
			message.setFrom(new InternetAddress("ngasce@nmims.edu", "NMIMS Global Access SCE"));
			message.setRecipients(Message.RecipientType.TO,
				InternetAddress.parse("sanketpanaskar@gmail.com"));
			message.setSubject("Testing Subject");
			message.setText("Dear Mail Crawler,"
				+ "\n\n No spam to my email, please!");
 
			Transport.send(message);
 
			//System.out.println("Done");
 
		} catch (MessagingException e) {
			throw new RuntimeException(e);
		}
	}
	
	@Async
	public void sendDeliveryFailureEmail(MailStudentPortalBean mailBean, String mailResponseStatus) {
		try {
 
			Message message = getMessage();
			message.setFrom(new InternetAddress(from, "NMIMS Global Access SCE"));

			
			message.addRecipients(Message.RecipientType.TO, 
                    InternetAddress.parse("nelson.soans@nmims.edu"));
			
			message.addRecipients(Message.RecipientType.CC, 
                    InternetAddress.parse("sanketpanaskar@gmail.com"));
			
			message.setSubject("Email Delivery Exception:"+mailBean.getSubject());
			
			
			
			String body = " Dear Sir,<br><br>"
						+ "Email delivery failed to below receipients with response code = "+ mailResponseStatus +"<br><br>"
					    +" Please Find EMail Filter Criteria : <br>"+mailBean.getFilterCriteria()+"<br><br>"
					    +" Email Body : <br>"+mailBean.getBody()+"<br><br>"
					    +" Student Number List :<br>"+mailBean.getSapIdRecipients()
					    +" Receipent Email Id List : <br>"+mailBean.getMailIdRecipients()+"<br><br>";
			
			body +="Thanks & Regards"
					+"<br>"
					+"NMIMS GLOBAL ACCESS SCHOOL FOR CONTINUING EDUCATION";
			
			
			//System.out.println("body = "+body);
			
			message.setContent(body, "text/html; charset=utf-8");
			Transport.send(message);
 
			loggerForEmailCount.info("Total 1 Mail sent via(SMTP) from "+from+", Email Subject : Email Delivery Exception: "+mailBean.getSubject()+", for sendDeliveryFailureEmail");
			//System.out.println("Email Delivery Exception Email");
 
		} catch(Exception e) {
			
		}
		
	}
	
	
	@Async
	public void sendSMSDeliveryFailureEmail(MailStudentPortalBean mailBean, String smsResponseStatus) {
		
		try {
			Message message = getMessage();
			message.setFrom(new InternetAddress(from, "NMIMS Global Access SCE"));

			
			message.addRecipients(Message.RecipientType.TO, 
                    InternetAddress.parse("nelson.soans@nmims.edu"));
			
			message.addRecipients(Message.RecipientType.CC, 
                    InternetAddress.parse("sanketpanaskar@gmail.com"));
			
			message.setSubject("SMS Delivery Exception:");
			
			
			String body = " Dear Sir,<br><br>"
						+ "SMS delivery failed to below receipients with response code = "+ smsResponseStatus +"<br><br>"
					    +" Please Find SMS Filter Criteria : <br>"+mailBean.getFilterCriteria()+"<br><br>"
					    +" SMS Message : <br>"+mailBean.getBody()+"<br><br>"
					    +" Student Number List :<br>"+mailBean.getSapiIdsFromStudentList()
					    +" Receipent Mobile List : <br>"+mailBean.getMobileNosFromStudentList()+"<br><br>";
			
			body +="Thanks & Regards"
					+"<br>"
					+"NMIMS GLOBAL ACCESS SCHOOL FOR CONTINUING EDUCATION";
			
			
			//System.out.println("body = "+body);
			
			message.setContent(body, "text/html; charset=utf-8");
			Transport.send(message);
 
			loggerForEmailCount.info("Total 1 Mail sent via(SMTP) from "+from+", Email Subject : SMS Delivery Exception, for sendSMSDeliveryFailureEmail");
			//System.out.println("Email Delivery Exception Email");
 
		} catch(Exception e) {
			
		}
		
	}
	private Message getMessage() {
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
		return message;
	}
	
	
	
	@Async
	public void mailStackTrace(String subject, Exception e) {
		
		
		/*if(!"PROD".equalsIgnoreCase(ENVIRONMENT)){
			//System.out.println("Not sending query email since this is not Prod.");
			return;
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
			//message.setRecipients(Message.RecipientType.TO,InternetAddress.parse("sanketpanaskar@gmail.com,pranit.shirke.ext@nmims.edu,jforce.solution@gmail.com"));
			message.setRecipients(Message.RecipientType.TO,InternetAddress.parse("pranit.shirke.ext@nmims.edu,raynal.dcunha.ext@nmims.edu,jforce.solution@gmail.com"));
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

			//System.out.println("Error Mail sent successfully");

		} catch(Exception ex) {
			//ex.printStackTrace();
		}

	}
	@Async
	public void sendPasswordEmailNew(PersonStudentPortalBean person,String email,String password){
		
		try {
 
			Message message = getMessage();
			message.setFrom(new InternetAddress(from, "NMIMS Global Access SCE"));
			message.setRecipients(Message.RecipientType.TO,InternetAddress.parse(email));
			message.setSubject("Your Password for NGASCE Student Zone");
			
			String body = "Dear "+ person.getDisplayName()+", \n\n"
					+"Your password for NGASCE Student Zone is "+password
					+"\n\n"
					+"Thanks & Regards"
					+"\n"
					+"NMIMS GLOBAL ACCESS SCHOOL FOR CONTINUING EDUCATION";
			
			
			message.setText(body);
 
			Transport.send(message);
 
			loggerForEmailCount.info("Total 1 Mail sent via(SMTP) from "+from+", Email Subject : Your Password for NGASCE Student Zone, for sendPasswordEmailNew");
			//System.out.println("Sent Password Changed Email");
 
		} catch(Exception e) {
			
			throw new RuntimeException(e);
		}

		//System.out.println("Sent Password Changed successfully");
	}

	//Send Course Query Posted Mail to faculty Start
	@Async
	public void sendCourseQueryPostedEmail(SessionQueryAnswerStudentPortal sessionQuery,String toEmailAddress) {

		if(!"PROD".equalsIgnoreCase(ENVIRONMENT)){
			//System.out.println("Not sending query email since this is not Prod.");
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
			loggerForCourseQueryNotificationToFaculty.info("Course Query Email Sent to emailId : "+toEmailAddress+"/ sapid : "+sessionQuery.getSapId()+" session_query_answer_id : "+sessionQuery.getId()+"  Student Query: "+sessionQuery.getQuery());
			loggerForEmailCount.info("Total 1 Mail sent via(SMTP) from "+from+", Email Subject : Course Query Received, for sendCourseQueryPostedEmail");
			//System.out.println("Sent Query Email");

		} catch(Exception e) {
			loggerForCourseQueryNotificationToFaculty.error("Course Query Email Not Sent  to emailId : "+toEmailAddress+"/ sapid : "+sessionQuery.getSapId()+" session_query_answer_id : "+sessionQuery.getId()+",  Student Query: "+sessionQuery.getQuery()+" Error message : "+e.getMessage() );
			
		}


		//System.out.println("Mail sent successfully");

	}
	//Send Course Query Mail to faculty End

	public void sendOtpForLeadLogin(LeadStudentPortalBean bean){
		
		try {
 
			Message message = getMessage();
			message.setFrom(new InternetAddress(from, "NMIMS Global Access SCE"));
			message.setRecipients(Message.RecipientType.TO,InternetAddress.parse(bean.getEmailId()));
			message.setSubject("Your OTP for NGASCE Student Zone");
			
			String body = "Dear "+ bean.getFirstName()+", \n\n"
					+"Your OTP for NGASCE Student Zone is "+bean.getOtp()
					+"\n\n"
					+"Thanks & Regards"
					+"\n"
					+"NMIMS GLOBAL ACCESS SCHOOL FOR CONTINUING EDUCATION";
			
			
			message.setText(body);
 
			Transport.send(message);
 
			loggerForEmailCount.info("Total 1 Mail sent via(SMTP) from "+from+", Email Subject : Your OTP for NGASCE Student Zone, for sendOtpForLeadLogin");
			//System.out.println("Sent Password Email");
 
		} catch(Exception e) {
			throw new RuntimeException(e);
		}

		//System.out.println("Mail sent successfully");
	}
	
	@Async
	public void sendSMTPEmail(String subject, String emailBody, String fromEmailId, List<String> recipient ) {
		
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
		System.out.println("Number of emails to send = "+successCount);

			
		Session session = Session.getInstance(props, new javax.mail.Authenticator() {
			protected PasswordAuthentication getPasswordAuthentication() {
				return new PasswordAuthentication(username, password);
			}
		});

		try {

			Message message = new MimeMessage(session);
			message.setFrom(new InternetAddress(from, "NMIMS Global Access SCE"));
			for (int i = 0; i < successCount; i++){ 
				try {
					address = new javax.mail.internet.InternetAddress(recipient.get(i)); 
					message.addRecipient(Message.RecipientType.TO, address); 
				}catch (Exception e) {
					errorCount++;
					System.out.println(" Mail NOT sent  Error = "+e.getMessage());
					emailsNotSentId = emailsNotSentId +"," + recipient.get(i);
				}
			}
			
			message.addRecipients(Message.RecipientType.BCC,InternetAddress.parse("jforce.solution@gmail.com"));
			
			String body = "";
			message.setSubject(subject);

				body =  emailBody
						+"<br><br>"
						+"Thanks & Regards"
						+"<br>"
						+"NMIMS GLOBAL ACCESS SCHOOL FOR CONTINUING EDUCATION";
				
			message.setContent(body, "text/html; charset=utf-8");

			loggerForEmails.info("recipient "+recipient);
//			System.out.println("recipient "+recipient);
			Transport.send(message);

			// sending  EmailId to which email is not send
			if(!"".equals(emailsNotSentId)){
				System.out.println("Sending Error emails");
				loggerForEmails.info("Sending Error emails");
				 
				Message messages = new javax.mail.internet.MimeMessage(session); 
				try {
					messages.setFrom(new InternetAddress(from, "NMIMS Global Access SCE"));
				} catch (UnsupportedEncodingException e) {
//					
				}
				messages.addRecipients(javax.mail.Message.RecipientType.TO, InternetAddress.parse("jforce.solution@gmail.com, Somesh.Turde.Ext@nmims.edu"));
				messages.setSubject("Emails Errors for "+subject); 
				messages.setText(emailsNotSentId); 
				Transport.send(messages);
				loggerForEmails.info("Sent Error Email");
				System.out.println("Sent Error Email");
			}
		} catch(Exception e) {
			
		}

		loggerForEmails.info("Mail sent successfully");
		System.out.println("Mail sent successfully");
	}
	
	@Async
	public void studentCourseMailTrace(String subject, String error) {
		try {
			if(!"PROD".equalsIgnoreCase(ENVIRONMENT)){
				System.out.println("Not sending query email since this is not Prod.");
				return;
			}
		
			Message message = getMessage();
			message.setFrom(new InternetAddress(from, "NMIMS Global Access SCE"));
			message.setRecipients(Message.RecipientType.CC,InternetAddress.parse("jforce.solution@gmail.com"));
			message.setRecipients(Message.RecipientType.TO,InternetAddress.parse("riya.jakhariya.ext@nmims.edu,somesh.turde.ext@nmims.edu"));

			message.setSubject(subject);

			String body = error + "<br><br>Thanks & Regards <br> NMIMS GLOBAL ACCESS SCHOOL FOR CONTINUING EDUCATION";
			message.setContent(body, "text/html; charset=utf-8");

			Transport.send(message);
		} catch(Exception ex) {
			loggerForStudentSubjectCourse.info("Error: Student Course Subject Mail Sending error ",ex);
		}

	}

	private String deleteLocalAttachment(String localPath) {
		try {
			File f = new File(localPath); 	// file to be delete
			if (f.delete()) // returns Boolean value
				return "File deleted succesfully of " + f.getName();
		} catch (Exception e) {
//			e.printStackTrace();
		}
		return "Failed to delete, file not found: "+localPath;
	}
	
	public void sendTrackingNotificationToStudents(List<MassUploadTrackingSRBean> listSendEmailNotification) {
		
	    for(MassUploadTrackingSRBean bean:listSendEmailNotification)	{
	    	String subject="Service Request Tracking";
	    	String emailId=bean.getEmailId();
	    	try {
	    		
				Message message = composeForSRTrackingMailer(emailId,subject);
				String body = getTrackingSRBody(bean);
				message.setContent(body, "text/html; charset=utf-8");
				Transport.send(message);
				
	    	}catch(Exception ex) {
				loggerForEmailCount.info("Failed to send Mail via(SMTP) for sendTrackingNotificationToStudents, for serviceRequest : " + 
						bean.getServiceRequestType() +" and serviceRequestId "+bean.getServiceRequestId()+ ", due to " + ex);
			}
	    }
	}
	
	private String getTrackingSRBody(MassUploadTrackingSRBean bean) {
		String serviceRequestType = bean.getServiceRequestType();
		String courierName = bean.getCourierName().substring(0, 1).toUpperCase() + bean.getCourierName().substring(1);
		String trackId = bean.getTrackId();
		String url = bean.getUrl();
		String courierLink = "<a href=\""+url+"\">"+trackId+"</a>";
		
		if("indiapost".equalsIgnoreCase(bean.getCourierName())){
			courierName = "India Post";
		}
		
		String body = "Dear "+bean.getStudentName()+", <br><br>"
				+ "Greetings from NMIMS Global Access School for Continuing Education!<br><br>";
		
		switch (serviceRequestType) {
		case ISSUANCE_OF_MARKSHEET:
			body+=getMarksheetBody(courierName,trackId,url,courierLink);
			break;
			
		case DUPLICATE_MARKSHEET:
			body+=getDuplicateMarksheetBody(courierName,trackId,url,courierLink);
			break;
			
		case ISSUANCE_OF_GRADESHEET:
			body+=getGradeSheetBody(courierName,trackId,url,courierLink);
			break;
			
		case DUPLICATE_GRADESHEET:
			body+=getDuplicateGradeSheetBody(courierName,trackId,url,courierLink);
			break;
			
		case CERTIFICATE_DISPATCHED_DTDC:
			body+=getCertificateDTDCBody(courierName,trackId,url,courierLink);
			break;
			
		case CERTIFICATE_DISPATCHED_DELHIVERY:
			body+=getCertificateDelhiveryBody(courierName,trackId,url,courierLink);
			break;
			
		case DUPLICATE_ID:
			body+=getDuplicateIDBody(courierName,trackId,url,courierLink);
			break;
			
		case DUPLICATE_FEE_RECEIPT:
			body+=getDuplicateFeeRecieptBody(courierName,trackId,url,courierLink);
			break;
			
		case DUPLICATE_STUDY_KIT:
			body+=getDuplicateStudyKitBody(courierName,trackId,url,courierLink);
			break;
			
		case SINGLE_BOOK:
			body+=getSingleBookBody(courierName,trackId,url,courierLink);
			break;
			
		case TRANSCRIPT:
			body+=getTranscriptBody(courierName,trackId,url,courierLink);
			break;
			
		case FINAL_CERTIFICATE:
			body+=getFinalCertificateBody(courierName,trackId,url,courierLink);
			break;

		default:
			break;
		}
		
		body +="<br>Thanks<br>"
			 + "NMIMS GLOBAL ACCESS SCHOOL FOR CONTINUING EDUCATION<br><br>";
		
		return body;
	}
	
	private String getMarksheetBody(String courierName, String trackId, String url, String courierLink) {
		String body="Please take note that your Mark sheet has been dispatched to your shipping address. The<br>details are as follows:<br><br>"
				  + "Courier Name: "+courierName+"<br><br>"
				  + "Tracking number: "+courierLink+"<br><br>"
				  + "For any questions, do write in to ngasce@nmims.edu<br><br>";
			
			  if("delhivery".equalsIgnoreCase(courierName)) {
				  body+="Your Mark sheet's delivery route can be tracked by using the link below:-<br>"
					+  url+"<br><br>";
			  }
		return body;
	}
	
	private String getDuplicateMarksheetBody(String courierName, String trackId, String url, String courierLink) {
		String body="Please take note that your Duplicate Marksheet has been dispatched to your shipping address. The<br>details are as follows:<br><br>"
				  + "Courier Name: "+courierName+"<br><br>"
				  + "Tracking number: "+courierLink+"<br><br>"
				  + "For any questions, do write in to ngasce@nmims.edu<br><br>";
			
				if("delhivery".equalsIgnoreCase(courierName)) {
					body+="Your Certificate delivery route can be tracked by using the link below:-<br>"
						+  url+"<br><br>";
				}
		return body;
	}
	
	private String getGradeSheetBody(String courierName, String trackId, String url, String courierLink) {
		String body="Please take note that your Grade sheet has been dispatched to your shipping address. The<br>details are as follows:<br><br>"
			   	  + "Courier Name: "+courierName+"<br><br>"
			   	  + "Tracking number: "+courierLink+"<br><br>"
			   	  + "For any questions, do write in to ngasce@nmims.edu<br><br>";
					
				if("delhivery".equalsIgnoreCase(courierName)) {
					body+="Your Grade sheet's delivery route can be tracked by using the link below:-<br>"
						+  url+"<br><br>";
				}
		return body;
	}
	
	private String getDuplicateGradeSheetBody(String courierName, String trackId, String url, String courierLink) {
		String body="Please take note that your Duplicate Gradesheet has been dispatched to your shipping address. The<br>details are as follows:<br><br>"
				  + "Courier Name: "+courierName+"<br><br>"
				  + "Tracking number: "+courierLink+"<br><br>"
				  + "For any questions, do write in to ngasce@nmims.edu<br><br>";
					
				if("delhivery".equalsIgnoreCase(courierName)) {
					body+="Your Certificate delivery route can be tracked by using the link below:-<br>"
						+  url+"<br><br>";
				}
		return body;
	}
	
	private String getCertificateDTDCBody(String courierName, String trackId, String url, String courierLink) {
		String body="Please take note that your Certificate has been dispatched to your shipping address. The<br>details are as follows:<br><br>"
				  + "Courier Name: "+courierName+"<br><br>"
				  + "Tracking number: "+courierLink+"<br><br>"
				  + "For any questions, do write in to ngasce@nmims.edu<br><br>";
		
				if("delhivery".equalsIgnoreCase(courierName)) {
					body+="Your Certificate delivery route can be tracked by using the link below:-<br>"
							+  url+"<br><br>";
				}
		return body;
	}
	
	private String getCertificateDelhiveryBody(String courierName, String trackId, String url, String courierLink) {
		String body="Please take note that your Certificate has been dispatched to your shipping address. The<br>details are as follows:<br><br>"
			      + "Courier Name: "+courierName+"<br><br>"
			      + "Tracking number: "+courierLink+"<br><br>"
			      + "For any questions, do write in to ngasce@nmims.edu<br><br>";
				
				if("delhivery".equalsIgnoreCase(courierName)) {
					body+="Your Certificate delivery route can be tracked by using the link below:-<br>"
							+  url+"<br><br>";
				}
		return body;
	}
	
	private String getDuplicateIDBody(String courierName, String trackId, String url, String courierLink) {
		String body="Please take note that your Duplicate ID card has been dispatched to your shipping address. The<br>details are as follows:<br><br>"
				  + "Courier Name: "+courierName+"<br><br>"
				  + "Tracking number: "+courierLink+"<br><br>"
				  + "For any questions, do write in to ngasce@nmims.edu<br><br>";
				
				if("delhivery".equalsIgnoreCase(courierName)) {
					body+="Your Duplicate ID card's delivery route can be tracked by using the link below:-<br>"
							+  url+"<br><br>";
				}
		return body;
	}
	
	private String getDuplicateFeeRecieptBody(String courierName, String trackId, String url, String courierLink) {
		String body="Please take note that your Duplicate Fee Receipt has been dispatched to your shipping address. The<br>details are as follows:<br><br>"
				  + "Courier Name: "+courierName+"<br><br>"
				  + "Tracking number: "+courierLink+"<br><br>"
				  + "For any questions, do write in to ngasce@nmims.edu<br><br>";
				
				if("delhivery".equalsIgnoreCase(courierName)) {
					body+="Your Duplicate Fee Receipt's delivery route can be tracked by using the link below:-<br>"
							+  url+"<br><br>";
				}
		return body;
	}
	
	private String getDuplicateStudyKitBody(String courierName, String trackId, String url, String courierLink) {
		String body="Please take note that your Duplicate Study kit has been dispatched to your shipping address. The<br>details are as follows:<br><br>"
				  + "Courier Name: "+courierName+"<br><br>"
				  + "Tracking number: "+courierLink+"<br><br>"
				  + "For any questions, do write in to ngasce@nmims.edu<br><br>";
				
				if("delhivery".equalsIgnoreCase(courierName)) {
					body+="The delivery route can be tracked by using the link below:-<br>"
							+  url+"<br><br>";
				}
		return body;
	}
	
	private String getSingleBookBody(String courierName, String trackId, String url, String courierLink) {
		String body = "";		
		if("delhivery".equalsIgnoreCase(courierName)) {
			body="Please take note that the single book requested by you was dispatched to your shipping <br>address. The details are as follows:<br><br>"
				      + "Courier Name: "+courierName+"<br><br>"
				      + "Tracking number: "+courierLink+"<br><br>"
				      + "For any questions, do write in to ngasce@nmims.edu<br><br>";
			
			body+="The delivery route can be tracked by using the link below:-<br>"
					+  url+"<br><br>";
		}
		else if("indiapost".equalsIgnoreCase(courierName)){
			body="Kindly note that your Single Book has been dispatched to your shipping address, details of<br>the same as follows:<br><br>"
				      + "Courier Name: "+courierName+"<br><br>"
				      + "Tracking number: "+courierLink+"<br><br>"
				      + "For any questions, do write in to ngasce@nmims.edu<br><br>";
		}
		return body;
	}
		
		private String getTranscriptBody(String courierName, String trackId, String url, String courierLink) {
			String body="Please take note that your Transcript has been dispatched to your shipping address. The details are as follows:<br><br>"
				      + "Courier Name: "+courierName+"<br><br>"
				      + "Tracking number: "+courierLink+"<br><br>"
			          + "For any questions, do write in to ngasce@nmims.edu<br><br>";
				
				if("delhivery".equalsIgnoreCase(courierName)) {
					body+="Your Transcript's delivery route can be tracked by using the link below:-<br>"
							+  url+"<br><br>";
				}
		return body;
	}
		private String getFinalCertificateBody(String courierName, String trackId, String url, String courierLink) {
			String body="Please take note that your Final Certificate has been dispatched to your shipping address. The details are as follows:<br><br>"
					+ "Courier Name: "+courierName+"<br><br>"
					+ "Tracking number: "+courierLink+"<br><br>"
					+ "For any questions, do write in to ngasce@nmims.edu<br><br>";
			
			if("delhivery".equalsIgnoreCase(courierName)) {
				body+="Your Certificate delivery route can be tracked by using the link below:-<br>"
						+  url+"<br><br>";
			}
			return body;
		}
	
	/**
	 * @param bean
	 * @return message 
	 * @throws MessagingException
	 * @throws UnsupportedEncodingException
	 * @throws AddressException
	 */
	private Message composeForSRTrackingMailer(String emailId,String subject)
			throws MessagingException, UnsupportedEncodingException, AddressException {
		Message message = getMessage();
		message.setFrom(new InternetAddress(from, "NMIMS Global Access SCE"));
		message.setRecipients(Message.RecipientType.BCC,InternetAddress.parse("jforcesolutions@gmail.com"));
		message.setRecipients(Message.RecipientType.TO,InternetAddress.parse(emailId));
		message.setSubject(subject);
		return message;
	}
	
	@Async
	public void idCardUpdationFailedEmailAlert(String sapid, String errorMessage) {
		try {
			if(!"PROD".equalsIgnoreCase(ENVIRONMENT)){
				System.out.println("Not sending idCardUpdationFailedEmailAlert email since this is not Prod.");
				return;
			}
		
			Message message = getMessage();
			message.setFrom(new InternetAddress(from, "NMIMS Global Access SCE"));
			message.setRecipients(Message.RecipientType.CC,InternetAddress.parse("jforce.solution@gmail.com"));
			message.setRecipients(Message.RecipientType.TO,InternetAddress.parse("saurabh.pawar.ext@nmims.edu,somesh.turde.ext@nmims.edu"));

			message.setSubject("Id Card updation failed for sapid : "+ sapid);

			String body = " Error while updating id card for sapid :"+sapid +" with error message "+errorMessage+"."
					+ "<br><br>Thanks & Regards <br> NMIMS GLOBAL ACCESS SCHOOL FOR CONTINUING EDUCATION";
			message.setContent(body, "text/html; charset=utf-8");

			Transport.send(message);
		} catch(Exception ex) {
			idCardCreationLogger.info("Error: Student update Id card Mail Sending error ",ex);
		}

	}
}