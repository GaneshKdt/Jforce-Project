package com.nmims.helpers;

import java.io.File;
import java.io.FileOutputStream;
import java.io.PrintStream;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Properties;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

import org.apache.commons.lang.RandomStringUtils;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.FileSystemResource;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import com.nmims.beans.ReceiverDetails;
import com.nmims.beans.RevenueReportField;
import com.nmims.beans.StudentBean;
import com.nmims.beans.StudentDataMismatchBean;
import com.nmims.daos.StudentZoneDao;
import com.nmims.listeners.SalesforceSyncScheduler;
import com.nmims.webservice.fedex.ship.Address;
import com.nmims.webservice.fedex.ship.Contact;

@Component
public class MailSender {
	
	@Value( "${ENVIRONMENT}" )
	private String ENVIRONMENT;
	
	@Value("${MY_COMMUNICATION_WS}")
	private String MY_COMMUNICATION_WS;
	
	@Autowired
	StudentZoneDao studentZoneDao;
	
	private  String REPORTS="d:/Reports";
	
	private String host;
	private String portalHostId="192.168.2.99";
	private String portalPortId="25";
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
	
	private static final Logger loggerForEmailCount = LoggerFactory.getLogger("emailCount");

	private static final Logger studentcourses_logger = LoggerFactory.getLogger("studentCourses");
	
	@Async
	public void sendPasswordEmail(Person person, String email){
		
 
		Properties props = new Properties();
		props.put("mail.smtp.auth", "true");
		props.put("mail.smtp.starttls.enable", "true");
		props.put("mail.smtp.host", portalHostId);
		props.put("mail.smtp.port", portalPortId);
 
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
			message.setSubject("Your Password for NGASCE Student Zone");
			
			String body = "Dear "+ person.getDisplayName()+", \n\n"
					+"Your password for NGASCE Student Zone is "+person.getPassword()
					+"\n\n"
					+"Thanks & Regards"
					+"\n"
					+"NMIMS GLOBAL ACCESS SCHOOL FOR CONTINUING EDUCATION";
			
			
			message.setText(body);
 
			Transport.send(message);
 
			//System.out.println("Sent Password Email");
 
		} catch(Exception e) {
			throw new RuntimeException(e);
		}
		

		//System.out.println("Mail sent successfully");
	}

	

	public static void main(String[] args) throws UnsupportedEncodingException {
		
		Person p = new Person();
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
	
	/*OLD CODE
	 * @Async
	public void sendSyncStudentZoneMissingRegistrationDataEmail(HashMap<String,StudentBean> studentsMap,String mismatchMonth,String mismatchYear){
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
			message.setRecipients(Message.RecipientType.CC,InternetAddress.parse("sanketpanaskar@gmail.com,jforce.solution@gmail.com"));
			message.setRecipients(Message.RecipientType.TO,InternetAddress.parse("sneha.utekar@nmims.edu,nelson.soans@nmims.edu,manasvi.malve@nmims.edu"));
			message.setSubject(ENVIRONMENT +" :  Salesforce-StudentZone Registration MisMatch Error ");
			
			
			String body = "Dear Sir, Madam, <br><br>";
			
			body += "Please find below records they were synced with Salesforce but record not Present In Student Zone for "+mismatchMonth+"-"+mismatchYear+" <br><br>";
			
			body += "<table>"
					+ "<tr>"
						+ "<td>Sr.No</td>"
						+ "<td>Student No</td>"
						+ "<td>First Name</td>"
						+ "<td>Last Name</td>"
						+ "<td>Program</td>"
					+ "</tr>";
			
			int count = 1;
			for (String sapId  : studentsMap.keySet()) {
				StudentBean student = studentsMap.get(sapId);
				body += "<tr>"
							+ "<td>" + count++ + "</td>"
							+ "<td>" + student.getSapid() + "</td>"
							+ "<td>" + student.getFirstName() + "</td>"
							+ "<td>" + student.getLastName() + "</td>"
							+ "<td>" + student.getProgram() + "</td>"
						+ "</tr>";
						
			}
					
			body += "</table><br><br>";	
			
			body += "Thanks & Regards"
					+"<br>"
					+"NMIMS GLOBAL ACCESS SCHOOL FOR CONTINUING EDUCATION";
			
			
			System.out.println("body = "+body);
			
			message.setContent(body, "text/html; charset=utf-8");
			Transport.send(message);
 
			System.out.println("Sent MisMacth Sync Cases");
			
		}catch(Exception e){
			throw new RuntimeException(e);
		}
		
		System.out.println("Sent Email Successfully ");
	}*/
	
	@Async
	public void sendStudentZoneUpsertErrorEmail(ArrayList<StudentBean> invalidSalesforceRecordsListForEmail,ArrayList<Person> ldapErrorListForEmail,
			ArrayList<String> salesforceUpdateErrorListForEmail,ArrayList<StudentBean> studentZoneUpdateErrorListForEmail , ArrayList<StudentBean> studentZoneSuccessfulListForEmail, ArrayList<StudentBean> idCardErrorList){
		
		Properties props = new Properties();
		props.put("mail.smtp.auth", "true");
		props.put("mail.smtp.starttls.enable", "true");
		props.put("mail.smtp.host", portalHostId);
		props.put("mail.smtp.port", portalPortId);
 
		Session session = Session.getInstance(props,
		  new javax.mail.Authenticator() {
			protected PasswordAuthentication getPasswordAuthentication() {
				return new PasswordAuthentication(username, password);
			}
		  });
  
		try {
			
 
			Message message = new MimeMessage(session);
			message.setFrom(new InternetAddress(from, "NMIMS Global Access SCE"));
			message.setRecipients(Message.RecipientType.CC,InternetAddress.parse("sanketpanaskar@gmail.com,jforce.solution@gmail.com,shiv.golani.ext@nmims.edu,balakrishnan.ramalingam.ext@nmims.edu,phalguni.bhadarka.ext@nmims.edu"));
			message.setRecipients(Message.RecipientType.TO,InternetAddress.parse("sneha.utekar@nmims.edu,nelson.soans@nmims.edu,manasvi.malve@nmims.edu"));
			//message.setRecipients(Message.RecipientType.TO,InternetAddress.parse("jforce.solution@gmail.com"));
			message.setSubject(ENVIRONMENT +" :  Salesforce Admission/Registration Success and Error ");
			
			
			String body = "Dear Sir, Madam, <br><br>";
			
			if( studentZoneSuccessfulListForEmail !=null && !studentZoneSuccessfulListForEmail.isEmpty()){
				body += "Please find below records they were synced with Student Zone. <br><br>";
				
				body += "<table>"
						+ "<tr>"
							+ "<td>Sr.No</td>"
							+ "<td>Student No</td>"
							+ "<td>First Name</td>"
							+ "<td>Last Name</td>"
							+ "<td>Program</td>"
							+ "<td>Semester</td>"
						+ "</tr>";
				
				int count = 1;
				for (StudentBean student  : studentZoneSuccessfulListForEmail) {
					body += "<tr>"
								+ "<td>" + count++ + "</td>"
								+ "<td>" + student.getSapid() + "</td>"
								+ "<td>" + student.getFirstName() + "</td>"
								+ "<td>" + student.getLastName() + "</td>"
								+ "<td>" + student.getProgram() + "</td>"
								+ "<td>" + student.getSem() + "</td>"
							+ "</tr>";
							
				}
						
				body += "</table><br><br>";	
			}
			
			if( invalidSalesforceRecordsListForEmail !=null && !invalidSalesforceRecordsListForEmail.isEmpty()){
				body += "Please find below records with Invalid Information In salesforce. These were not synced. Please correct Information in salesforce. <br><br>";
				
				body += "<table>"
						+ "<tr>"
							+ "<td>Sr.No</td>"
							+ "<td>Student No</td>"
							+ "<td>First Name</td>"
							+ "<td>Last Name</td>"
							+ "<td>Program</td>"
							+ "<td>Error Message</td>"
						+ "</tr>";
				
				int count = 1;
				for (StudentBean student  : invalidSalesforceRecordsListForEmail) {
					body += "<tr>"
								+ "<td>" + count++ + "</td>"
								+ "<td>" + student.getSapid() + "</td>"
								+ "<td>" + student.getFirstName() + "</td>"
								+ "<td>" + student.getLastName() + "</td>"
								+ "<td>" + student.getProgram() + "</td>"
								+ "<td>" + student.getErrorMessage() + "</td>"
							+ "</tr>";
							
				}
						
				body += "</table><br><br>";	
			}
			
			if( ldapErrorListForEmail !=null && !ldapErrorListForEmail.isEmpty()){
				body += "Please find below records that were not inserted in LDAP. Please analyze and take necessary actions. If already created in LDAP, then update flag in Salesforce<br><br>";
				
				body += "<table>"
						+ "<tr>"
							+ "<td>Sr.No</td>"
							+ "<td>Student No</td>"
							+ "<td>First Name</td>"
							+ "<td>Last Name</td>"
							+ "<td>Error Message</td>"
						+ "</tr>";
				
				int count = 1;
				for (Person person : ldapErrorListForEmail) {
					body += "<tr>"
								+ "<td>" + count++ + "</td>"
								+ "<td>" + person.getUserId() + "</td>"
								+ "<td>" + person.getFirstName() + "</td>"
								+ "<td>" + person.getLastName() + "</td>"
								+ "<td>" + person.getErrorMessage() + "</td>"
							+ "</tr>";
							
				}
						
				body += "</table><br><br>";	
			}
			
			
			if( salesforceUpdateErrorListForEmail !=null && !salesforceUpdateErrorListForEmail.isEmpty())
			{
				body += " <br><br>"
						+ "Please find records that were not updated back in Salesforce during Synchronization. <br><br>";
				
				body += "<table>"
						+ "<tr>"
							+ "<td>Sr.No</td>"
							+ "<td>Salesforce Object</td>"
							+ "<td>Salesforce Record Id</td>"
							+ "<td>Salesforce Error Message</td>"
						+ "</tr>";
				
				 int count = 1;
				for (String sapidAccountIdErrorMessage : salesforceUpdateErrorListForEmail) {
					String salesforceSObject = sapidAccountIdErrorMessage.split(":")[0];
					String salesforceSObjectId = sapidAccountIdErrorMessage.split(":")[1];
					String salesforceSObjectErrorMessage = sapidAccountIdErrorMessage.split(":")[2];
					
					body += "<tr>"
								+ "<td>" + count++ + "</td>"
								+ "<td>" + salesforceSObject + "</td>"
								+ "<td>" + salesforceSObjectId + "</td>"
								+ "<td>" + salesforceSObjectErrorMessage + "</td>"
							+ "</tr>";
							
				}
						
				body += "</table><br><br>";	
			}
			
			if( studentZoneUpdateErrorListForEmail !=null && !studentZoneUpdateErrorListForEmail.isEmpty())
			{
				body += " <br><br>"
						+ "Below records were not inserted in Student Zone table. Please analyze errors with IT team.<br><br>";
				
				body += "<table>"
						+ "<tr>"
							+ "<td>Sr.No</td>"
							+ "<td>Student No</td>"
							+ "<td>First Name</td>"
							+ "<td>Last Name</td>"
							+ "<td>Semester</td>"
							+ "<td>Program</td>"
							+ "<td>Error Message</td>"
						+ "</tr>";
				
				 int count = 1;
				for (StudentBean student : studentZoneUpdateErrorListForEmail) {
					body += "<tr>"
								+ "<td>" + count++ + "</td>"
								+ "<td>" + student.getSapid() + "</td>"
								+ "<td>" + student.getFirstName() + "</td>"
								+ "<td>" + student.getLastName() + "</td>"
								+ "<td>" + student.getSem() + "</td>"
								+ "<td>" + student.getProgram() + "</td>"
								+ "<td>" + student.getErrorMessage() + "</td>"
							+ "</tr>";
							
				}
						
				body += "</table><br><br>";	
			}
			
			if( idCardErrorList !=null && !idCardErrorList.isEmpty())
			{
				body += " <br><br>"
						+ "Id card not created for below records. Please analyze errors with IT team.<br><br>";
				
				body += "<table>"
						+ "<tr>"
							+ "<td>Sr.No</td>"
							+ "<td>Student No</td>"
							+ "<td>First Name</td>"
							+ "<td>Last Name</td>"
							+ "<td>Semester</td>"
							+ "<td>Program</td>"
							+ "<td>Error Message</td>"
						+ "</tr>";
				
				 int count = 1;
				for (StudentBean student : idCardErrorList) {
					body += "<tr>"
								+ "<td>" + count++ + "</td>"
								+ "<td>" + student.getSapid() + "</td>"
								+ "<td>" + student.getFirstName() + "</td>"
								+ "<td>" + student.getLastName() + "</td>"
								+ "<td>" + student.getSem() + "</td>"
								+ "<td>" + student.getProgram() + "</td>"
								+ "<td>" + student.getErrorMessage() + "</td>"
							+ "</tr>";
							
				}
						
				body += "</table><br><br>";	
			}
			
			
			body += "Thanks & Regards"
					+"<br>"
					+"NMIMS GLOBAL ACCESS SCHOOL FOR CONTINUING EDUCATION";
			
			
			//System.out.println("body = "+body);
			
			message.setContent(body, "text/html; charset=utf-8");
			Transport.send(message);
 
			//System.out.println("Sent Upsert Failure Email");
			
		}catch(Exception e){
			logger.info("Error:  " + e.getMessage());
			throw new RuntimeException(e);
		}
		
		//System.out.println("Sent Email Successfully ");
	}
	
	
	@Async
	public void sendMismatchEmail(ArrayList<StudentDataMismatchBean> mismatchList,HashMap<String,StudentBean>getAllStudentsNotPresentInRegistrationDataTableForCurrentAcadMonthAndYear,
			HashMap<String, StudentBean> getAllStudentsProgramDifferentfromRegistrationDataTableForCurrentAcadMonthAndYear) {
		 
		Properties props = new Properties(); 
		props.put("mail.smtp.auth", "true");
		props.put("mail.smtp.starttls.enable", "true");
		props.put("mail.smtp.host", portalHostId);
		props.put("mail.smtp.port", portalPortId);
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
					+"Please find attachment of Salesforce & Student Zone Mismatch Report."
					+ " <ol> "
					+ "		<li>Sheet 1 contains list of mismatch data of  Salesforce & Student Zone for a student</li>"
					+ "		<li>Sheet 2 contains list of Students who's Program Different from Registration DataTable for enrollment month/year </li>"
					+ "		<li>Sheet 3 contains list of Students who's entry Not Present In Registration Data Table For Current Acad Month And Year </li>"
					+ "</ol>" + 
					". <br> <br>";
			body = body + "<br>Thanks & Regards"
					+ "<br>"
					+ "NMIMS GLOBAL ACCESS SCHOOL FOR CONTINUING EDUCATION";

			helper.setFrom(new InternetAddress(from, "NMIMS Global Access SCE"));
			helper.setCc(InternetAddress.parse("sanketpanaskar@gmail.com,jforce.solution@gmail.com,pranit.shirke.ext@nmims.edu,shiv.golani.ext@nmims.edu"));
			helper.setTo(InternetAddress.parse("sneha.utekar@nmims.edu,nelson.soans@nmims.edu,manasvi.malve@nmims.edu,harshad.kasliwal@nmims.edu,jigna.patel@nmims.edu"));
			
			//helper.setTo("jforce.solution@gmail.com");
			helper.setSubject(ENVIRONMENT+" : Salesforce & Student Zone Mismatch");
			
			String folderName = REPORTS+"/SFDC_StudentZone_Mismatch" ;
			String fileName = "/SFDC_StudentZone_MismatchReport_"+RandomStringUtils.randomAlphanumeric(12)+".xlsx";
			File folder = new File(folderName);
			
			if(!folder.exists()){
				folder.mkdirs();
			}
			File f = new File(folderName+fileName);
			if(!f.exists()){
				f.createNewFile();
			}
			
			
			SXSSFWorkbook workbook = new SXSSFWorkbook();
			//create a word sheet
			Sheet sheet = workbook.createSheet("StudentDetails_MismatchReport");
			
			try {
				int index = 0;
				Row header = sheet.createRow(0);
				
				header.createCell(index++).setCellValue("Sr. No.");
				header.createCell(index++).setCellValue("SAP ID");
				header.createCell(index++).setCellValue("First Name");
				header.createCell(index++).setCellValue("Last Name");
				header.createCell(index++).setCellValue("Mismatch Type");
				header.createCell(index++).setCellValue("Salesforce Value");
				header.createCell(index++).setCellValue("Student Zone Value");

				int rowNum = 1;
				for (int i = 0 ; i < mismatchList.size(); i++) {
					//create the row data
					index = 0;
					Row row = sheet.createRow(rowNum++);
					StudentDataMismatchBean studentDataMismatchBean = mismatchList.get(i);
					
					row.createCell(index++).setCellValue(i+1);
					row.createCell(index++).setCellValue(studentDataMismatchBean.getSapid());
					row.createCell(index++).setCellValue(studentDataMismatchBean.getFirstName());
					row.createCell(index++).setCellValue(studentDataMismatchBean.getLastName());
					row.createCell(index++).setCellValue(studentDataMismatchBean.getMismatchType());
					row.createCell(index++).setCellValue(studentDataMismatchBean.getSalesforceValue() );
					row.createCell(index++).setCellValue(studentDataMismatchBean.getStudentZoneValue());
					
				}
			} catch (Exception e) {
				logger.info("Error in generating excel sheet");
				logger.info(e.getMessage());
				e.printStackTrace();
				body=body+"<h1><b>Error in generating excel sheet, Please contact software developer team ASAP</b></h1>";
			}
			
			try {
					Sheet sheet2 = workbook.createSheet("ProgramDifferentfromRegistrationDataTableForCurrentAcadMonthAndYear");
					
					int index2 = 0;
					Row header2 = sheet2.createRow(0);
					
					index2 = 0;
					header2.createCell(index2++).setCellValue("Sr. No.");
					header2.createCell(index2++).setCellValue("SAP ID");
					header2.createCell(index2++).setCellValue("First Name");
					header2.createCell(index2++).setCellValue("Last Name");
					header2.createCell(index2++).setCellValue("Mobile");
					header2.createCell(index2++).setCellValue("Email Id");
					header2.createCell(index2++).setCellValue("Program");
					
					int rowNum2 = 1;
					int i2 =0;
					if(!getAllStudentsProgramDifferentfromRegistrationDataTableForCurrentAcadMonthAndYear.isEmpty())
					{
					for (String key : getAllStudentsProgramDifferentfromRegistrationDataTableForCurrentAcadMonthAndYear.keySet()) {
						StudentBean student = getAllStudentsProgramDifferentfromRegistrationDataTableForCurrentAcadMonthAndYear.get(key);
							//create the row data
						index2 = 0;
						Row row2 = sheet2.createRow(rowNum2++);
						
						row2.createCell(index2++).setCellValue(i2+1);
						row2.createCell(index2++).setCellValue(student.getSapid());
						row2.createCell(index2++).setCellValue(student.getFirstName());
						row2.createCell(index2++).setCellValue(student.getLastName());
						row2.createCell(index2++).setCellValue(student.getMobile());
						row2.createCell(index2++).setCellValue(student.getEmailId() );
						row2.createCell(index2++).setCellValue(student.getProgram());
						i2++;
					}

				}else {
					index2 = 0;
					Row row2 = sheet2.createRow(rowNum2++);
					
					row2.createCell(index2++).setCellValue(1);
					row2.createCell(index2++).setCellValue("NO RECORDS FOUND");
					
				}
			} catch (Exception e) {
				logger.info("Error in generating excel sheet");
				logger.info(e.getMessage());
				e.printStackTrace();
				body=body+"<h1><b>Error in generating excel sheet, Please contact software developer team ASAP</b></h1>";
			}
			
			try {
					Sheet sheet3 = workbook.createSheet("StudentsNotPresentInRegistrationDataTable");
					
					int index3 = 0;
					Row header3 = sheet3.createRow(0);
					
					index3 = 0;
					header3.createCell(index3++).setCellValue("Sr. No.");
					header3.createCell(index3++).setCellValue("SAP ID");
					header3.createCell(index3++).setCellValue("First Name");
					header3.createCell(index3++).setCellValue("Last Name");
					header3.createCell(index3++).setCellValue("Mobile");
					header3.createCell(index3++).setCellValue("Email Id");
					header3.createCell(index3++).setCellValue("Program");
					
					int rowNum3 = 1;
					int i3 =0;
					if(!getAllStudentsNotPresentInRegistrationDataTableForCurrentAcadMonthAndYear.isEmpty())
					{
					for (String key : getAllStudentsNotPresentInRegistrationDataTableForCurrentAcadMonthAndYear.keySet()) {
						StudentBean student = getAllStudentsNotPresentInRegistrationDataTableForCurrentAcadMonthAndYear.get(key);
							//create the row data
						index3 = 0;
						Row row3 = sheet3.createRow(rowNum3++);
						
						row3.createCell(index3++).setCellValue(i3+1);
						row3.createCell(index3++).setCellValue(student.getSapid());
						row3.createCell(index3++).setCellValue(student.getFirstName());
						row3.createCell(index3++).setCellValue(student.getLastName());
						row3.createCell(index3++).setCellValue(student.getMobile());
						row3.createCell(index3++).setCellValue(student.getEmailId() );
						row3.createCell(index3++).setCellValue(student.getProgram());
						i3++;
					}

				}else {
					index3 = 0;
					Row row3 = sheet3.createRow(rowNum3++);
					
					row3.createCell(index3++).setCellValue(1);
					row3.createCell(index3++).setCellValue("No Records Found");
					
				}
			} catch (Exception e) {
				logger.info("Error in generating excel sheet");
				logger.info(e.getMessage());
				e.printStackTrace();
				body=body+"<h1><b>Error in generating excel sheet, Please contact software developer team ASAP</b></h1>";
				
			}
			FileOutputStream fileOut = new FileOutputStream(f);
			//System.out.println(fileOut);
			workbook.write(fileOut);
			fileOut.close();
			
			
			FileSystemResource file = new FileSystemResource(folderName+fileName);
			helper.addAttachment(file.getFilename(), file);
			helper.setText(body,true);
			
			Transport.send(message);

		} catch(Exception e) {
			e.printStackTrace();
			logger.info(e.getMessage());
		}

		logger.info("Salesforce & Student Zone Mismatch Report : Mail sent successfully");
		//System.out.println("Salesforce & Student Zone Mismatch Report : Mail sent successfully");

	
		
		
/*		try {
			
 
			Message message = new MimeMessage(session);
			message.setFrom(new InternetAddress(from, "NMIMS Global Access SCE"));
			//message.setRecipients(Message.RecipientType.CC,InternetAddress.parse("sanketpanaskar@gmail.com,jforce.solution@gmail.com,pranit.shirke.ext@nmims.edu"));
			//message.setRecipients(Message.RecipientType.TO,InternetAddress.parse("sneha.utekar@nmims.edu,nelson.soans@nmims.edu,manasvi.malve@nmims.edu"));
			message.setRecipients(Message.RecipientType.TO,InternetAddress.parse("jforce.solution@gmail.com"));
			message.setSubject(ENVIRONMENT +" : Salesforce & Student Zone Mismatch");
			
			
			String body = "Dear Sir, Madam, <br><br>"
					+ "Please find mismatch below for Salesforce & Student Zone data. <br><br>";
			
			
			body += "<table>"
					+ "<tr>"
						+ "<td>Sr.No</td>"
						+ "<td>Student No</td>"
						+ "<td>First Name</td>"
						+ "<td>Last Name</td>"
						+ "<td>Mismatch Type</td>"
						+ "<td>Salesforce Value</td>"
						+ "<td>Student Zone Value</td>"
					+ "</tr>";
			
			int count = 1;
			for (StudentDataMismatchBean studentDataMismatchBean : mismatchList) {
				body += "<tr>"
							+ "<td>" + count++ + "</td>"
							+ "<td>" + studentDataMismatchBean.getSapid() + "</td>"
							+ "<td>" + studentDataMismatchBean.getFirstName() + "</td>"
							+ "<td>" + studentDataMismatchBean.getLastName() + "</td>"
							+ "<td>" + studentDataMismatchBean.getMismatchType() + "</td>"
							+ "<td>" + studentDataMismatchBean.getSalesforceValue() + "</td>"
							+ "<td>" + studentDataMismatchBean.getStudentZoneValue() + "</td>"
						+ "</tr>";
						
			}
					
			body += "</table><br><br>";	
			
			if(!getAllStudentsNotPresentInRegistrationDataTableForCurrentAcadMonthAndYear.isEmpty())
			{
				body += " <br><br>"
						+ "Please find Missing Registrations. <br><br>";
				
				body += "<table>"
						+ "<tr>"
							+ "<td>Sr.No</td>"
							+ "<td>Student No</td>"
							+ "<td>First Name</td>"
							+ "<td>Last Name</td>"
							+ "<td>Mobile </td>"
							+ "<td>Email Id</td>"
						+ "</tr>";
				
				 count = 1;
				for (String key : getAllStudentsNotPresentInRegistrationDataTableForCurrentAcadMonthAndYear.keySet()) {
					StudentBean student = getAllStudentsNotPresentInRegistrationDataTableForCurrentAcadMonthAndYear.get(key);
					body += "<tr>"
								+ "<td>" + count++ + "</td>"
								+ "<td>" + student.getSapid() + "</td>"
								+ "<td>" + student.getFirstName() + "</td>"
								+ "<td>" + student.getLastName() + "</td>"
								+ "<td>" + student.getMobile() + "</td>"
								+ "<td>" + student.getEmailId() + "</td>"
							+ "</tr>";
							
				}
						
				body += "</table><br><br>";	
			}
			
			if(!getAllStudentsProgramDifferentfromRegistrationDataTableForCurrentAcadMonthAndYear.isEmpty())
			{
				body += " <br><br>"
						+ "Please find Miss Match Program with Registration and Student Master. <br><br>";
				
				body += "<table>"
						+ "<tr>"
							+ "<td>Sr.No</td>"
							+ "<td>Student No</td>"
							+ "<td>First Name</td>"
							+ "<td>Last Name</td>"
							+ "<td>Mobile </td>"
							+ "<td>Email Id</td>"
							+ "<td>Program</td>"
						+ "</tr>";
				
				 count = 1;
				for (String key : getAllStudentsProgramDifferentfromRegistrationDataTableForCurrentAcadMonthAndYear.keySet()) {
					StudentBean student = getAllStudentsProgramDifferentfromRegistrationDataTableForCurrentAcadMonthAndYear.get(key);
					body += "<tr>"
								+ "<td>" + count++ + "</td>"
								+ "<td>" + student.getSapid() + "</td>"
								+ "<td>" + student.getFirstName() + "</td>"
								+ "<td>" + student.getLastName() + "</td>"
								+ "<td>" + student.getMobile() + "</td>"
								+ "<td>" + student.getEmailId() + "</td>"
								+ "<td>" + student.getProgram() + "</td>"
							+ "</tr>";
							
				}
						
				body += "</table><br><br>";	
			}
			
			body += "Thanks & Regards"
					+"<br>"
					+"NMIMS GLOBAL ACCESS SCHOOL FOR CONTINUING EDUCATION";
			
			
			System.out.println("body = "+body);
			
			message.setContent(body, "text/html; charset=utf-8");
			Transport.send(message);
 
			System.out.println("Sent Service Request Email");
			System.out.println("Mail sent successfully");
		} catch(Exception e) {
			throw new RuntimeException(e);
		}*/
		
	}
	
	@Async
	public void sendDispatchInitiatedEmailForDelhivery(String studentEmailId,String studentNumber, String semester, String skuType,
			ReceiverDetails receiver, String trackingNumber,String program,String placeToShip,String informationCenterAddress,String orderType) {
		Properties props = new Properties();
		props.put("mail.smtp.auth", "true");
		props.put("mail.smtp.starttls.enable", "true");
		props.put("mail.smtp.host", portalHostId);
		props.put("mail.smtp.port", portalPortId);
 
		Session session = Session.getInstance(props,
		  new javax.mail.Authenticator() {
			protected PasswordAuthentication getPasswordAuthentication() {
				return new PasswordAuthentication(username, password);
			}
		  });
 
		String body = null;
		String subject=null;
		try {
			
			String shippingAddress = "";
			shippingAddress +=receiver.getCustomerPersonName()+", ";
			shippingAddress +=receiver.getCustomerCompanyName()+", ";
			shippingAddress +=receiver.getCustomerAddress()+", ";
			shippingAddress +=receiver.getCustomerCity()+", ";
			shippingAddress +=receiver.getCustomerState()+", ";
			shippingAddress +=receiver.getCustomerPin()+", ";
			shippingAddress +=receiver.getCustomerCountry();

			if(orderType.equalsIgnoreCase("Student Order") && "Kit".equalsIgnoreCase(skuType) && semester.equalsIgnoreCase("1"))
			{
				subject="Your Study Kit is dispatched";
				body = "Dear Student, <br>"
						+ "Greetings from NMIMS Global Access School for Continuing Education! <br><br>"
						//+ "Your Semester 1 study kit has been dispatched along with Welcome kit at below mentioned address. Welcome kit will include your ID card, welcome letter and student undertaking form. <br><br>";
						+ "Your Study Kit for Semester 1 has been dispatched to the registered address, as noted below:- <br><br>";
						if(placeToShip.contains("Send to my shipping address")){
							body += "Shipping Address: " + shippingAddress +"<br>";
						}else if(placeToShip.contains("Send to my Information Centre")){
							body += "Information Center Address: " + informationCenterAddress +"<br>";
						}
						body += "Courier: Delhivery<br>" 
						+ "Tracking Number: " + trackingNumber  +"<br>"
						/*
						+ "We regret to inform you that the dispatch of your Welcome Kit will be delayed due to some technical issues.<br>"
						+ "The Kit includes your Student Identity Card, Undertaking Form and Welcome letter. We will intimate you when we dispatch the Welcome Kit to your shipping address.<br>"
						+ "We apologize for the inconvenience caused to you.<br>"
						*/
						+ "The link to track your books is: https://www.delhivery.com/track/package/"+trackingNumber
						+ "For further details or queries, you can write to ngasce@nmims.edu.<br>";
						//+ "The link to track your study kit is: https://www.delhivery.com/track/package/"+trackingNumber;
			}
			else if(orderType.equalsIgnoreCase("Student Order") && "Kit".equalsIgnoreCase(skuType) && !(semester.equalsIgnoreCase("1")))
			{
				subject="Your Study Kit is dispatched";
				body = "Dear Student, <br>"
						+ "Greetings from NMIMS Global Access School for Continuing Education! <br><br>"
						+ "Your Semester " +semester+ " study kit has been dispatched at below mentioned address. <br><br>";
						if(placeToShip.contains("Send to my shipping address")){
							body += "Shipping Address: " + shippingAddress +"<br>";
						}else if(placeToShip.contains("Send to my Information Centre")){
							body += "Information Center Address: " + informationCenterAddress +"<br>";
						}
						body += "Courier: Delhivery<br>" 
						+ "Tracking Number: " + trackingNumber  +"<br>"
						+ "The link to track your study kit is: https://www.delhivery.com/track/package/"+trackingNumber;
			}
			else if(orderType.equalsIgnoreCase("Single Book") || (orderType.equalsIgnoreCase("Student Order") && skuType.equalsIgnoreCase("Book")))
			{
				subject="Your Book is dispatched";
				body = "Dear Student, <br><br>"
						+ "Greetings from NMIMS Global Access School for Continuing Education! <br>"
						+ "Your Book has been dispatched, details of the same are as follows: <br><br>";
						if(placeToShip.contains("Send to my shipping address")){
							body += "Shipping Address: " + shippingAddress +"<br>";
						}else if(placeToShip.contains("Send to my Information Centre")){
							body += "Information Center Address: " + informationCenterAddress +"<br>";
						}
						body += "Courier: Delhivery<br>" 
						+ "Tracking Number: " + trackingNumber  +"<br>"
						+ "The link to track your books is: https://www.delhivery.com/track/package/"+trackingNumber;
						
			}
				
			body += "<br><br>";		
			body += "Thanks & Regards"
					+"<br>"
					+"NMIMS GLOBAL ACCESS SCHOOL FOR CONTINUING EDUCATION (NGA-SCE)";

			Message message = new MimeMessage(session);
			message.setFrom(new InternetAddress(from, "NMIMS Global Access SCE"));
			
			message.setRecipients(Message.RecipientType.TO,InternetAddress.parse(studentEmailId));
			
			message.setRecipients(Message.RecipientType.BCC,InternetAddress.parse("sanketpanaskar@gmail.com,jforce.solution@gmail.com"));
			//message.setRecipients(Message.RecipientType.BCC,InternetAddress.parse("vikasrmenon@gmail.com"));
			
			message.setSubject(subject);
			//System.out.println("body = "+body);
			
			message.setContent(body, "text/html; charset=utf-8");
			Transport.send(message);
 
			logger.info("Sent Dispatch Initiated Email for Student Number : "+studentNumber);
		} 
		catch(Exception e)
		{
			logger.info("Exception in sendDispatchInitiated method :"+e);
			//e.printStackTrace();
			//throw new RuntimeException(e.getMessage());
		}
		finally
		{
			try
			{
				if(orderType.equalsIgnoreCase("Student Order") && "Kit".equalsIgnoreCase(skuType))
					createRecordInUserMailTableAndMailTable("Your Study Kit is dispatched",studentEmailId,studentNumber,body,"ngasce@nmims.edu");
				else if(orderType.equalsIgnoreCase("Single Book") || (orderType.equalsIgnoreCase("Student Order") && skuType.equalsIgnoreCase("Book")))
					createRecordInUserMailTableAndMailTable("Your Book is dispatched",studentEmailId,studentNumber,body,"ngasce@nmims.edu");
			}
			catch(Exception e)
			{
				logger.info(""+e);
			}
		}
	}
	
	public void createRecordInUserMailTableAndMailTable(String subject,String studentEmail,String studentNumber,String body,String fromEmail)
	{
		String filter="Delhivery Dispatch";
		long insertedMailId = studentZoneDao.insertMailRecordFilterCriteria(filter,studentNumber,fromEmail,subject,body);
		studentZoneDao.insertUserMailRecord(studentNumber,fromEmail,studentNumber,studentEmail,insertedMailId);
	}
	
	
	@Async
	public void sendDispatchInitiatedEmail(String studentEmailId,String studentNumber, String semester, String skuType,
			Address recipientAddress, Contact recipientContact, String trackingNumber,String program,String placeToShip,String informationCenterAddress) {
		Properties props = new Properties();
		props.put("mail.smtp.auth", "true");
		props.put("mail.smtp.starttls.enable", "true");
		props.put("mail.smtp.host", portalHostId);
		props.put("mail.smtp.port", portalPortId);
 
		Session session = Session.getInstance(props,
		  new javax.mail.Authenticator() {
			protected PasswordAuthentication getPasswordAuthentication() {
				return new PasswordAuthentication(username, password);
			}
		  });
 
		try {
			
			String shippingAddress = "";
			//System.out.println(recipientAddress.getStreetLines());
			shippingAddress += recipientContact.getPersonName() + ", ";
			shippingAddress += recipientContact.getCompanyName() + ", ";
			for (String steetLine : recipientAddress.getStreetLines()) {
				shippingAddress += steetLine;
			}
			shippingAddress += ", " + recipientAddress.getCity() + ", ";
			shippingAddress += recipientAddress.getStateOrProvinceCode()+ ", ";
			shippingAddress += recipientAddress.getPostalCode()+ ", ";
			shippingAddress += recipientAddress.getCountryName();
 
			Message message = new MimeMessage(session);
			message.setFrom(new InternetAddress(from, "NMIMS Global Access SCE"));
			
			message.setRecipients(Message.RecipientType.TO,InternetAddress.parse(studentEmailId));
			
			message.setRecipients(Message.RecipientType.BCC,InternetAddress.parse("sanketpanaskar@gmail.com,jforce.solution@gmail.com"));
			//message.setRecipients(Message.RecipientType.BCC,InternetAddress.parse("vikasrmenon@gmail.com"));
			
			
			
			
			String body = null;
			
			if("Kit".equalsIgnoreCase(skuType) && "1".equalsIgnoreCase(semester)){
				
				message.setSubject("Your Study Kit is dispatched");
				body = "Dear Student, <br>"
						
						+ "Greetings from NMIMS Global Access School for Continuing Education! <br><br> "
						+ "Kindly note that your Welcome Kit for Semester 1 has been dispatched along with Study Kit. <br> "
						+ "Welcome kit will include your ID card, welcome letter and student undertaking form. <br><br> "
						+ "For any questions do write into ngasce@nmims.edu.";

						/*
						+ "Kindly note that your Welcome Kit of Semester 1 has been dispatched along with Study Kit. "
						+ "Welcome Kit will include your ID card, welcome letter and student undertaking form. <br><br>"
						+ "Kindly note that your Study Kit for Semester 1 has been dispatched. Owing to current lockdown situation in Maharashtra, "
						+ "employees are required to work from home, hence Welcome Kit which includes your welcome letter, Student undertaking and "
						+ "ID card will be dispatched when we resume work, this in no way will impact your Academic delivery or Examinations."
						*/
						
						/*
						+ "Stay safe and follow the lock down protocol, for any questions do write in to ngasce@nmims.edu<br><br>";
						 */
						
						if(placeToShip.contains("Send to my shipping address")){
							body += "Shipping Address: " + shippingAddress +"<br>";
						}else if(placeToShip.contains("Send to my Information Centre")){
							body += "Information Center Address: " + informationCenterAddress +"<br>";
						}
				
						body += "Courier: FedEx<br>" 
						+ "Tracking Number: " + trackingNumber  +"<br>"
						+ "The link to track your books is: https://www.fedex.com/apps/fedextrack/?action=track&trackingnumber=" + trackingNumber + " <br>"
						+ "Alternatively you can also call the FedEx Toll Free Number at :- 1800 419 4343 to track your shipment";
						
			}else if("Kit".equalsIgnoreCase(skuType) && ("2".equalsIgnoreCase(semester) || "3".equalsIgnoreCase(semester)||"4".equalsIgnoreCase(semester))){
				message.setSubject("Your Study Kit is dispatched");
				body = "Dear Student, <br>"
						+ "Greetings from NMIMS Global Access School for Continuing Education! <br><br>"
						+ "Your Study kit has been dispatched to your shipping address for program "+ program +" for Semester " + semester + " , details of the same are as follows: <br><br>";
						if(placeToShip.contains("Send to my shipping address")){
							body += "Shipping Address: " + shippingAddress +"<br>";
						}else if(placeToShip.contains("Send to my Information Centre")){
							body += "Information Center Address: " + informationCenterAddress +"<br>";
						}
					body += "Courier: FedEx<br>" 
						+ "Tracking Number: " + trackingNumber  +"<br>"
						+ "The link to track your books is: https://www.fedex.com/apps/fedextrack/?action=track&trackingnumber=" + trackingNumber + " <br>"
						+ "Alternatively you can also call the FedEx Toll Free Number at :- 1800 419 4343 to track your shipment";
			}
			else if("Book".equalsIgnoreCase(skuType)){
				message.setSubject("Your Books are dispatched");
				body = "Dear Student, <br><br>"
						+ "Greetings from NMIMS Global Access School for Continuing Education! <br>"
						+ "Your Books have been dispatched, details of the same are as follows: <br><br>";
						if(placeToShip.contains("Send to my shipping address")){
							body += "Shipping Address: " + shippingAddress +"<br>";
						}else if(placeToShip.contains("Send to my Information Centre")){
							body += "Information Center Address: " + informationCenterAddress +"<br>";
						}
						body += "Courier: FedEx<br>" 
						+ "Tracking Number: " + trackingNumber 
						+ "The link to track your books is: https://www.fedex.com/apps/fedextrack/?action=track&trackingnumber=" + trackingNumber + " <br>"
						+ "Alternatively you can also call the FedEx Toll Free Number at :- 1800 419 4343 to track your shipment";
			}
			
								
			body += "<br><br>";		
			body += "Thanks & Regards"
					+"<br>"
					+"NMIMS GLOBAL ACCESS SCHOOL FOR CONTINUING EDUCATION";
			
			
			//System.out.println("body = "+body);
			
			message.setContent(body, "text/html; charset=utf-8");
			Transport.send(message);
 
			//System.out.println("Sent Disptach Initiated Email");
			createRecordUnderMyEmailCommuncationInStudentPortal("Your Study Kit is dispatched",studentEmailId,studentNumber,body,"ngasce@nmims.edu");	
		} catch(Exception e) {
			e.printStackTrace();
			throw new RuntimeException(e);
		}
		

		//System.out.println("Fed Ex Dispatch Mail sent successfully");
		
	}
	
	public void createRecordUnderMyEmailCommuncationInStudentPortal(String emailSubject,String studentEmailId,String studentNumber,String emailBody,String fromEmailId){
		try{
			URL url = new URL(MY_COMMUNICATION_WS);
			HttpURLConnection hConnection = (HttpURLConnection)
					url.openConnection();
			HttpURLConnection.setFollowRedirects( true );

			hConnection.setDoOutput( true );
			hConnection.setRequestMethod("POST");	

			StringBuilder parameters = new StringBuilder();
			parameters.append("SN=" +studentNumber);
			parameters.append("&");
			parameters.append("SE=" +studentEmailId);
			parameters.append("&");
			parameters.append("EB=" +emailBody);
			parameters.append("&");
			parameters.append("FRMID=" +fromEmailId);
			parameters.append("&");
			parameters.append("T=EMAIL");
			parameters.append("&");
			parameters.append("ES="+emailSubject);
			//System.out.println("Parameters :"+parameters);
			PrintStream ps = new PrintStream( hConnection.getOutputStream() );
			ps.print(parameters);
			ps.close();

			hConnection.connect();
			
			if( HttpURLConnection.HTTP_OK == hConnection.getResponseCode() )
			{
				logger.info("Success response while creating record in student portal");
				//System.out.println("Success");
				hConnection.disconnect();
			}else{
				logger.info("Error response while creating records in student portal");
				//System.out.println("ERROR");
			}
			
		}catch(Exception e){
			logger.info("Exception in createRecordUnderMyEmailCommuncationInStudentPortal: "+e);
			e.printStackTrace();
		}
	}
	
	@Async
	public void sendFedExOrderCreationErrorEmailFromDelhivery(String error) {
		Properties props = new Properties();
		props.put("mail.smtp.auth", "true");
		props.put("mail.smtp.starttls.enable", "true");
		props.put("mail.smtp.host", portalHostId);
		props.put("mail.smtp.port", portalPortId);
 
		Session session = Session.getInstance(props,
		  new javax.mail.Authenticator() {
			protected PasswordAuthentication getPasswordAuthentication() {
				return new PasswordAuthentication(username, password);
			}
		  });
 
		try {
			
			
			Message message = new MimeMessage(session);
			message.setFrom(new InternetAddress(from, "NMIMS Global Access SCE"));
			
			message.setRecipients(Message.RecipientType.TO,InternetAddress.parse("sanketpanaskar@gmail.com,jforce.solution@gmail.com"));
			
			//message.setRecipients(Message.RecipientType.CC,InternetAddress.parse("nelson.soans@nmims.edu"));
			//message.setRecipients(Message.RecipientType.BCC,InternetAddress.parse("vikasrmenon@gmail.com"));
			
			
			message.setSubject("Error in Delhivery Order Creation");
			
			String body = "Below Error Received during FedEx Order Creation<br>";
			body += "Error:<br>" + error + "<br><br>";					
			body += "<br><br>";		
			body += "Thanks & Regards"
					+"<br>"
					+"NMIMS GLOBAL ACCESS SCHOOL FOR CONTINUING EDUCATION";
			
			
			//System.out.println("body = "+body);
			
			message.setContent(body, "text/html; charset=utf-8");
			Transport.send(message);
			logger.info("Error mail sent: "+body);
 
		} catch(Exception e) {
			logger.info("Exception: "+e.getMessage());
			//e.printStackTrace();
			//throw new RuntimeException(e.getMessage());
		}
		
	}
	
	
	@Async
	public void sendFedExOrderCreationErrorEmail(String request, String response, String error) {
		Properties props = new Properties();
		props.put("mail.smtp.auth", "true");
		props.put("mail.smtp.starttls.enable", "true");
		props.put("mail.smtp.host", portalHostId);
		props.put("mail.smtp.port", portalPortId);
 
		Session session = Session.getInstance(props,
		  new javax.mail.Authenticator() {
			protected PasswordAuthentication getPasswordAuthentication() {
				return new PasswordAuthentication(username, password);
			}
		  });
 
		try {
			
			
			Message message = new MimeMessage(session);
			message.setFrom(new InternetAddress(from, "NMIMS Global Access SCE"));
			
			message.setRecipients(Message.RecipientType.TO,InternetAddress.parse("sanketpanaskar@gmail.com,jforce.solution@gmail.com"));
			
			message.setRecipients(Message.RecipientType.CC,InternetAddress.parse("nelson.soans@nmims.edu"));
			//message.setRecipients(Message.RecipientType.BCC,InternetAddress.parse("vikasrmenon@gmail.com"));
			
			
			message.setSubject("Error in FedEx Order Creation");
			
			String body = "Below Error Received during FedEx Order Creation<br>";
			
			 body += "Request:<br>" + request + "<br><br>";
			 body += "Response:<br>" + response + "<br><br>";
			 body += "Error:<br>" + error + "<br><br>";
			
								
			body += "<br><br>";		
			body += "Thanks & Regards"
					+"<br>"
					+"NMIMS GLOBAL ACCESS SCHOOL FOR CONTINUING EDUCATION";
			
			
			//System.out.println("body = "+body);
			
			message.setContent(body, "text/html; charset=utf-8");
			Transport.send(message);
 
		} catch(Exception e) {
			throw new RuntimeException(e);
		}
		

		//System.out.println("Fed Ex Error Mail sent successfully");
		
	}
	
	
	@Async
	public void mailStackTrace(String subject, Exception e) {
		
		//Commented for testing on test server
		if(!"PROD".equalsIgnoreCase(ENVIRONMENT)){
			//System.out.println("Not sending query email since this is not Prod.");
			return;
		}
		
		Properties props = new Properties();
		props.put("mail.smtp.auth", "true");
		props.put("mail.smtp.starttls.enable", "true");
		props.put("mail.smtp.host", portalHostId);
		props.put("mail.smtp.port", portalPortId);


		Session session = Session.getInstance(props,
				new javax.mail.Authenticator() {
			protected PasswordAuthentication getPasswordAuthentication() {
				return new PasswordAuthentication(username, password);
			}
		});

		try {

			Message message = new MimeMessage(session);
			message.setFrom(new InternetAddress(from, "NMIMS Global Access SCE"));
			message.setRecipients(Message.RecipientType.TO,InternetAddress.parse("sanketpanaskar@gmail.com,pranit.shirke.ext@nmims.edu,jforce.solution@gmail.com,shiv.golani.ext@nmims.edu"));
		//	message.setRecipients(Message.RecipientType.TO,InternetAddress.parse("jforce.solution@gmail.com"));

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
			ex.printStackTrace();
		}

	}
	
	
	@Async
	public void sendSyncStudentZoneMissingRegistrationDataEmail(ArrayList<StudentBean> studentNotInRegTableList){
		  if(!"PROD".equalsIgnoreCase(ENVIRONMENT)){
				//System.out.println("Not sending query email since this is not Prod.");
				return;
			}
			
		
		Properties props = new Properties();
		props.put("mail.smtp.auth", "true");
		props.put("mail.smtp.starttls.enable", "true");
		props.put("mail.smtp.host", portalHostId);
		props.put("mail.smtp.port", portalPortId);
 
		Session session = Session.getInstance(props,
		  new javax.mail.Authenticator() {
		protected PasswordAuthentication getPasswordAuthentication() {
				return new PasswordAuthentication(username, password);
			}
		  });
 
		try {
			
 
			Message message = new MimeMessage(session);
			message.setFrom(new InternetAddress(from, "NMIMS Global Access SCE"));
			message.setRecipients(Message.RecipientType.CC,InternetAddress.parse("sanketpanaskar@gmail.com,jforce.solution@gmail.com,pranit.shirke.ext@nmims.edu,shiv.golani.ext@nmims.edu"));
			message.setRecipients(Message.RecipientType.TO,InternetAddress.parse("sneha.utekar@nmims.edu,nelson.soans@nmims.edu,manasvi.malve@nmims.edu"));
			//message.setRecipients(Message.RecipientType.TO,InternetAddress.parse("jforce.solution@gmail.com"));

			message.setSubject(ENVIRONMENT +" :  Salesforce-StudentZone Registration MisMatch Error "+" Count : "+studentNotInRegTableList.size());
			
			
		String body = "Dear Sir, Madam, <br><br>";
			
			if(studentNotInRegTableList.size()>0){
				body += "Please find below records not Present In Student Zone for Registration <br><br>";
				
				body += "<table>"
						+ "<tr>"
							+ "<td>Sr.No</td>"
							+ "<td>Student No</td>"
							+ "<td>First Name</td>"
							+ "<td>Last Name</td>"
							+ "<td>Program</td>"
							+ "<td>Sem</td>"
						+ "<td>Year</td>"
							+ "<td>Month</td>"
							+ "<td>CloseDate</td>"
						+ "</tr>";
				
			int count = 1;
				for (StudentBean student  : studentNotInRegTableList) {
					body += "<tr>"
								+ "<td>" + count++ + "</td>"
								+ "<td>" + student.getSapid() + "</td>"
								+ "<td>" + student.getFirstName() + "</td>"
								+ "<td>" + student.getLastName() + "</td>"
								+ "<td>" + student.getProgram() + "</td>"
								+ "<td>" + student.getSem() + "</td>"
								+ "<td>" + student.getYear() + "</td>"
								+ "<td>" + student.getMonth() + "</td>"
								+ "<td>" + student.getCloseDate() + "</td>"
							+ "</tr>";
							
				}
						
				body += "</table><br><br>";	
			}else{
				body += "No MisMatch Records found. <br><br>";
			}
			
			body += "Thanks & Regards"
					+"<br>"
					+"NMIMS GLOBAL ACCESS SCHOOL FOR CONTINUING EDUCATION";
			
			
			//System.out.println("body = "+body);
			
			message.setContent(body, "text/html; charset=utf-8");
			Transport.send(message);
 
			//System.out.println("Sent mismatch opportunity Sync Cases");
			
		}catch(Exception e){
			//System.out.println("Not Sent mismatch opportunity Sync Cases");
			e.printStackTrace();
			//throw new RuntimeException(e);
			
		}
		
	//	System.out.println("Sent Email Successfully ");
	}
	public void sendRevenueReportMail(List<RevenueReportField> insertSuccessList, List<RevenueReportField> errorRevenueRecords) {
		if(!"PROD".equalsIgnoreCase(ENVIRONMENT)){
			//System.out.println("Not sending query email since this is not Prod.");
			return;
		}
			
		
		Properties props = new Properties();
		props.put("mail.smtp.auth", "true");
		props.put("mail.smtp.starttls.enable", "true");
		props.put("mail.smtp.host", portalHostId);
		props.put("mail.smtp.port", portalPortId);
	
		Session session = Session.getInstance(props, new javax.mail.Authenticator() {
			protected PasswordAuthentication getPasswordAuthentication() {
				return new PasswordAuthentication(username, password);
			}
		});
	
		try {
			
	
			Message message = new MimeMessage(session);
			message.setFrom(new InternetAddress(from, "NMIMS Global Access SCE"));
			message.setRecipients(Message.RecipientType.CC,InternetAddress.parse("jforce.solution@gmail.com,pranit.shirke.ext@nmims.edu,shiv.golani.ext@nmims.edu,erin.prabhu.ext@nmims.edu "));
			message.setRecipients(Message.RecipientType.TO,InternetAddress.parse("ashutosh.sultania.ext@nmims.edu"));
			//message.setRecipients(Message.RecipientType.TO,InternetAddress.parse("jforce.solution@gmail.com"));
	
			message.setSubject(ENVIRONMENT +" : Salesforce-StudentZone Revenue Report Sync Status");
			
			
			String body = "Dear Sir, Madam, <br><br>";
			
			if(insertSuccessList.size()>0){
				body += "These records were inserted successfully. <br><br>";
				
				body += "<table>"
						+ "<tr>"
							+ "<td>Sr.No</td>"
							+ "<td>ID on SFDC</td>"
							+ "<td>Type</td>"
							+ "<td>Payment Status</td>"
							+ "<td>Amount</td>"
							+ "<td>Actual Amount</td>"
							+ "<td>Refund Amount</td>"
						+ "</tr>";
				
				int count = 1;
				for (RevenueReportField report : insertSuccessList) {
					body += ""
						+ "<tr>"
							+ "<td>" + count++ + "</td>"
							+ "<td>" + report.getId() + "</td>"
							+ "<td>" + report.getType() + "</td>"
							+ "<td>" + report.getPaymentStatus() + "</td>"
							+ "<td>" + report.getAmount() + "</td>"
							+ "<td>" + report.getActualPaymentAmount() + "</td>"
							+ "<td>" + report.getRefundedAmount() + "</td>"
						+ "</tr>";
							
				}
						
				body += "</table><br><br>";	
			}

			if(errorRevenueRecords.size()>0){
				body += "These records were not inserted. <br><br>";
				
				body += "<table>"
						+ "<tr>"
							+ "<td>Sr.No</td>"
							+ "<td>Type</td>"
							+ "<td>Payment Status</td>"
							+ "<td>Amount</td>"
							+ "<td>Actual Amount</td>"
							+ "<td>Refund Amount</td>"
							+ "<td>Reason</td>"
						+ "</tr>";
				
				int count = 1;
				for (RevenueReportField report : errorRevenueRecords) {
					body += ""
						+ "<tr>"
							+ "<td>" + count++ + "</td>"
							+ "<td>" + report.getType() + "</td>"
							+ "<td>" + report.getPaymentStatus() + "</td>"
							+ "<td>" + report.getAmount() + "</td>"
							+ "<td>" + report.getActualPaymentAmount() + "</td>"
							+ "<td>" + report.getRefundedAmount() + "</td>"
							+ "<td>" + report.getErrorMessage() + "</td>"
						+ "</tr>";
							
				}
						
				body += "</table><br><br>";	
			}
			body += "Thanks & Regards"
					+"<br>"
					+"NMIMS GLOBAL ACCESS SCHOOL FOR CONTINUING EDUCATION";
			
			
			//System.out.println("body = "+body);
			
			message.setContent(body, "text/html; charset=utf-8");
			Transport.send(message);
	
			//System.out.println("Sent mismatch opportunity Sync Cases");
			
		}catch(Exception e){
			//System.out.println("Not Sent mismatch opportunity Sync Cases");
			e.printStackTrace();
			//throw new RuntimeException(e);
			
		}
	}
	
	
	@Async
	public void studentCourseMailTrace(String subject, String error) {
		if(!"PROD".equalsIgnoreCase(ENVIRONMENT)){
			//System.out.println("Not sending query email since this is not Prod.");
			return;
		}
		Properties props = new Properties();
		props.put("mail.smtp.auth", "true");
		props.put("mail.smtp.starttls.enable", "true");
		props.put("mail.smtp.host", portalHostId);
		props.put("mail.smtp.port", portalPortId);
		Session session = Session.getInstance(props,
				new javax.mail.Authenticator() {
			protected PasswordAuthentication getPasswordAuthentication() {
				return new PasswordAuthentication(username, password);
			}
		});
		try {

			Message message = new MimeMessage(session);
			message.setFrom(new InternetAddress(from, "NMIMS Global Access SCE"));
			message.setRecipients(Message.RecipientType.CC,InternetAddress.parse("jforce.solution@gmail.com"));
			message.setRecipients(Message.RecipientType.TO,InternetAddress.parse("riya.jakhariya.ext@nmims.edu,somesh.turde.ext@nmims.edu"));

			message.setSubject(subject);

		    String body = error + "<br><br>Thanks & Regards <br> NMIMS GLOBAL ACCESS SCHOOL FOR CONTINUING EDUCATION";
			message.setContent(body, "text/html; charset=utf-8");

			Transport.send(message);

		} catch(Exception ex) {
			studentcourses_logger.info(" Error in sending mails ",ex);
		}

	}
	
	@Async
	public void sendSMSDispatchFailureEmail(String mobileNumber, String smsMessage, String smsResponseStatus) {
		try {
			Message mailMessage = getMessage();
			mailMessage.setFrom(new InternetAddress(from, "NMIMS Global Access SCE"));

			
			mailMessage.addRecipients(Message.RecipientType.TO, 
                    InternetAddress.parse("nelson.soans@nmims.edu"));
			
			mailMessage.addRecipients(Message.RecipientType.CC, 
                    InternetAddress.parse("sanketpanaskar@gmail.com"));
			
			mailMessage.setSubject("SMS Delivery Exception:");
			
			
			String body = " Dear Sir,<br><br>"
						+ "SMS delivery failed to below receipients with response code = "+ smsResponseStatus +"<br><br>"
					    +" SMS Message : <br>"+smsMessage+"<br><br>"
					    +" Receipent Mobile : <br>"+mobileNumber+"<br><br>";
			
			body +="Thanks & Regards"
					+"<br>"
					+"NMIMS GLOBAL ACCESS SCHOOL FOR CONTINUING EDUCATION";
			
			
			//System.out.println("body = "+body);
			
			mailMessage.setContent(body, "text/html; charset=utf-8");
			Transport.send(mailMessage);
 
			loggerForEmailCount.info("Total 1 Mail sent via(SMTP) from "+from+", Email Subject : SMS Delivery Exception, for sendSMSDeliveryFailureEmail");
			//System.out.println("Email Delivery Exception Email");
 
		} catch(Exception e) {
			logger.error("Error in sending SMS Dispatch Failure Email due to {}"	, e);
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
	
	
}
