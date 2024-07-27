package com.nmims.helpers;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.lang.RandomStringUtils;
import org.apache.http.NameValuePair;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.apache.http.message.BasicNameValuePair;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import com.nmims.beans.AssignmentStudentPortalFileBean;
import com.nmims.beans.MailStudentPortalBean;
import com.nmims.beans.StudentStudentPortalBean;
import com.nmims.daos.LeadDAO;
import com.nmims.daos.PortalDao;
import com.nmims.controllers.BaseController;
@Component("emailHelper")
public class EmailHelper extends BaseController{
	private final String USER_AGENT = "Mozilla/5.0"; 

	@Value( "${ASSIGNMENT_END_DATE}" )
	private String ASSIGNMENT_END_DATE;

	@Value( "${ASSIGNMENT_END_TIME}" )
	private String ASSIGNMENT_END_TIME;

	@Value( "${ENVIRONMENT}" )
	private String ENVIRONMENT;

	@Value( "${EMAILS_SENT_FOLDER}" )
	private String EMAILS_SENT_FOLDER;
	
	@Value( "${SERVER_PATH}" )
	private String SERVER_PATH;

	private static final Logger loggerForEmails = LoggerFactory.getLogger("bulkEmailFromExcel");
	private static final Logger loggerForEmailCount = LoggerFactory.getLogger("emailCount");
	private static final Logger logger = LoggerFactory.getLogger(EmailHelper.class);
	
	@Autowired
	ApplicationContext act;

	@Autowired
	PortalDao portalDao;

	@Autowired
	MailSender mailer;

	@Async
	public void sendMassEmails(List<String> toEmailIds, ArrayList<String> listOfSapIds,String subject, String fromEmailId, String htmlBody, String userId, String criteria) throws InterruptedException{

		if(!"PROD".equalsIgnoreCase(ENVIRONMENT)){
			System.out.println("Not Sending sendMassEmails as it is not Production..");
			return;
		}
		//String fromEmailId = "ngasce@nmims.edu";
		String fromName = "NMIMS Global Access SCE";


		toEmailIds = new ArrayList<String>(new LinkedHashSet<String>(toEmailIds));
		//toEmailIds.add("sanketpanaskar@gmail.com");

		ArrayList<MailStudentPortalBean> mailList = new ArrayList<MailStudentPortalBean>();
		try {
			loggerForEmails.info("EmailHelper:List: OF SAPID size -->"+listOfSapIds.size());
			System.out.println("EmailHelper:List OF SAPID size -->"+listOfSapIds.size());
			for (int i = 0; i < toEmailIds.size(); i = i + 49) {
				int lastIndex = (toEmailIds.size() < i+49 ? toEmailIds.size() : i+49);
				int lastIndexForSAPId = (listOfSapIds.size()<i+49 ? listOfSapIds.size():i+49);
				MailStudentPortalBean mailBean = new MailStudentPortalBean();
				String mailStatus = "";
				System.out.println("EmailHelper:Sending emails from " + i + " to " + lastIndex);
				List<String> emailIdSubList =  toEmailIds.subList(i, lastIndex);
				List<String> sapIdSubList =  listOfSapIds.subList(i, lastIndexForSAPId);
				
				HashMap<String, String> payLoad = new HashMap<>();
				payLoad.put("provider", "AWSSES");
				payLoad.put("htmlBody", htmlBody);
				payLoad.put("subject", subject);
				payLoad.put("from", "donotreply-ngasce@nmims.edu");
				payLoad.put("email", emailIdSubList.stream().collect(Collectors.joining(",")));
				payLoad.put("fromName", fromName);
				
				mailStatus = sendAmazonSESDynamicMail(payLoad);
				
				System.out.println("EmailHelper: Mail STATUS -->"+mailStatus);

				mailBean.setBody(htmlBody);
				mailBean.setMailIdRecipients(emailIdSubList);
				mailBean.setFilterCriteria(criteria);
				mailBean.setSubject(subject);
				mailBean.setFromEmailId(fromEmailId);
				mailBean.setSapIdRecipients(sapIdSubList);
				mailList.add(mailBean);
				
				if(!"Success".equals(mailStatus)){
					//Intimate sender.
					mailer.sendDeliveryFailureEmail(mailBean, mailStatus);
				}
			}
			System.out.println("EmailHelper: MAIL LIST-->"+mailList.size());


			loggerForEmailCount.info("Total "+mailList.size()+" Mails sent via(TNMails) from "+fromEmailId+", Email Subject : "+subject);
			loggerForEmails.info("Saving Audit trail in file");
			writeEmailAuditTrailToFile(toEmailIds, subject, fromEmailId,htmlBody, userId, criteria);
			loggerForEmails.info("Creating entries in Mail table");
			createRecordInUserMailTableAndMailTable(mailList,userId,fromEmailId);

		} catch (Exception e) {
			
		}
	}
	
	@Async
	public void sendMassEmailsToLeads(List<String> toEmailIds, String subject, String fromEmailId, String htmlBody, String userId) throws InterruptedException{
		
		if(!"PROD".equalsIgnoreCase(ENVIRONMENT)){
			System.out.println("Not Sending sendMassEmails as it is not Production..");
			return;
		}
		//String fromEmailId = "ngasce@nmims.edu";
		String fromName = "NMIMS Global Access SCE";


		toEmailIds = new ArrayList<String>(new LinkedHashSet<String>(toEmailIds));
		//toEmailIds.add("sanketpanaskar@gmail.com");

		ArrayList<MailStudentPortalBean> mailList = new ArrayList<MailStudentPortalBean>();
		PoolingHttpClientConnectionManager poolingConnManager = new PoolingHttpClientConnectionManager();
		try {
			poolingConnManager.setDefaultMaxPerRoute(200);
			poolingConnManager.setMaxTotal(500);

			//Setting the HttpClient to Check for Stale Connections
			CloseableHttpClient client = HttpClients.custom().setDefaultRequestConfig(
					RequestConfig.custom().setStaleConnectionCheckEnabled(true).build()).
					setConnectionManager(poolingConnManager).build();
			for (int i = 0; i < toEmailIds.size(); i = i + 100) {
				int lastIndex = (toEmailIds.size() < i+100 ? toEmailIds.size() : i+100);
				MailStudentPortalBean mailBean = new MailStudentPortalBean();
				String mailStatus = "";
				System.out.println("EmailHelper:Sending emails from " + i + " to " + lastIndex);
				List<String> emailIdSubList =  toEmailIds.subList(i, lastIndex);
				
				HashMap<String, String> payLoad = new HashMap<>();
				payLoad.put("provider", "AWSSES");
				payLoad.put("htmlBody", htmlBody);
				payLoad.put("subject", subject);
				payLoad.put("from", "donotreply-ngasce@nmims.edu");
				payLoad.put("email", emailIdSubList.stream().collect(Collectors.joining(",")));
				payLoad.put("fromName", fromName);
				
				mailStatus = sendAmazonSESDynamicMail(payLoad);
				
				System.out.println("EmailHelper: Mail STATUS -->"+mailStatus);

				mailBean.setBody(htmlBody);
				mailBean.setMailIdRecipients(emailIdSubList);
				mailBean.setSubject(subject);
				mailBean.setFromEmailId(fromEmailId);
				mailList.add(mailBean);
				
				if(!"Success".equals(mailStatus)){
					//Intimate sender.
					mailer.sendDeliveryFailureEmail(mailBean, mailStatus);
				}
			}
			System.out.println("EmailHelper: MAIL LIST-->"+mailList.size());

			System.out.println("EmailHelper:Closing connections");
			client.close();

//			writeEmailAuditTrailToFile(toEmailIds, subject, fromEmailId,htmlBody, userId, criteria);
//
			createRecordInLeadMail(mailList,userId,fromEmailId);
		} catch (Exception e) {
			
		}finally{
			poolingConnManager.close();
			poolingConnManager.shutdown();
		}
	}
	
	@Async
	public void sendValidityNotificationToStudentList(ArrayList<StudentStudentPortalBean> studentList){
		if(!"PROD".equalsIgnoreCase(ENVIRONMENT)){
//			System.out.println("Not Sending sendValidityNotificationToStudentList as it is not Production..");
			return;
		}
		String fromEmailId = "donotreply-ngasce@nmims.edu";
		String fromName = "NMIMS Global Access SCE";
		String emailSubject = "Reminder :Program Validity Ends";
		PoolingHttpClientConnectionManager poolingConnManager = new PoolingHttpClientConnectionManager();
		try{
			poolingConnManager.setDefaultMaxPerRoute(200);
			poolingConnManager.setMaxTotal(500);

			//Setting the HttpClient to Check for Stale Connections
			CloseableHttpClient client = HttpClients.custom().setDefaultRequestConfig(
					RequestConfig.custom().setStaleConnectionCheckEnabled(true).build()).
					setConnectionManager(poolingConnManager).build();

			ExecutorService taskExecutor = Executors.newFixedThreadPool(500);

			for(StudentStudentPortalBean student : studentList){
				String htmlBody = validityNotificationEmailBody("No",student);
				MultiHttpClientConnThread curThread = new MultiHttpClientConnThread(client, student.getEmailId(), fromEmailId, fromName, emailSubject, htmlBody);
				taskExecutor.execute(curThread);
			}
			
			taskExecutor.shutdown();
//			System.out.println("Waiting for threads to finish");

			try {
				taskExecutor.awaitTermination(Long.MAX_VALUE, TimeUnit.NANOSECONDS);
			} catch (InterruptedException e) {
				logger.error("Task Executor Termination Error : " + e.getMessage());
			}
//			System.out.println("Closing connections");
			client.close();

		}catch(Exception e){
			
		}finally{
			poolingConnManager.close();
			poolingConnManager.shutdown();
		}


	}
	
	@Async
	public void createRecordInUserMailTableAndMailTable(ArrayList<MailStudentPortalBean> successfullMailList,String userId,String fromEmailID){
		PortalDao pDao = (PortalDao)act.getBean("portalDAO");
		long insertedMailId = pDao.insertMailRecord(successfullMailList,userId);
		pDao.insertUserMailRecord(successfullMailList,userId,fromEmailID,insertedMailId);

	}

	@Async
	public void createRecordInLeadMail(ArrayList<MailStudentPortalBean> successfullMailList,String userId,String fromEmailID){
		LeadDAO lDao = (LeadDAO)act.getBean("leadDAO");
		lDao.insertMailRecord(successfullMailList, fromEmailID);
	}
	
	@Async
	private void writeEmailAuditTrailToFile(List<String> toEmailIds, String subject, String fromEmailId, String htmlBody, String userId, String criteria) {
		try {

			String content = "";
			String todayAsString = new SimpleDateFormat("ddMMyyyy_HHmmss").format(new Date());
			String fileName = todayAsString + "_" + userId + "_" + RandomStringUtils.randomAlphanumeric(10) + ".txt";
			File file = new File(EMAILS_SENT_FOLDER +  "/" + fileName);

			// if file doesnt exists, then create it
			if (!file.exists()) {
				file.createNewFile();
			}

			FileWriter fw = new FileWriter(file.getAbsoluteFile());
			BufferedWriter bw = new BufferedWriter(fw);
			bw.write("Subject: "+subject);
			bw.write(System.lineSeparator());

			bw.write("Time: "+todayAsString);
			bw.write(System.lineSeparator());

			bw.write("Sender User Id: "+userId);
			bw.write(System.lineSeparator());
			bw.write(System.lineSeparator());

			bw.write("From EmailId: "+fromEmailId);
			bw.write(System.lineSeparator());
			bw.write(System.lineSeparator());

			bw.write("Email Group Criteria: ");
			bw.write(System.lineSeparator());
			bw.write(criteria);
			bw.write(System.lineSeparator());
			bw.write(System.lineSeparator());

			bw.write("Email Content: ");
			bw.write(System.lineSeparator());
			bw.write(htmlBody);
			bw.write(System.lineSeparator());
			bw.write(System.lineSeparator());

			bw.write("Recipients: ");
			bw.write(System.lineSeparator());
			bw.write(toEmailIds.toString());
			bw.write(System.lineSeparator());

			bw.close();

			System.out.println("Done");

		} catch (IOException e) {
			
		}

	}


	private Long parseJSONResponse(CloseableHttpResponse response) {
		Long status = 0L;
		try {
			BufferedReader rd = new BufferedReader(
					new InputStreamReader(response.getEntity().getContent()));

			StringBuffer result = new StringBuffer();
			String line = "";
			while ((line = rd.readLine()) != null) {
				result.append(line);
			}
			System.out.println("Result = "+result);

			JSONParser parser = new JSONParser();
			Object obj = parser.parse(result.toString());
			JSONObject jsonObject = (JSONObject) obj;
			status = (Long) jsonObject.get("status");
			System.out.println("status = "+status);

		} catch (Exception e) {
			
		}
		return status;
	}

	public class MultiHttpClientConnThread implements Runnable{
		private CloseableHttpClient client;
		private String toEmailId;
		private String fromEmailId;
		private String fromName;
		private String subject;
		private String htmlBody;

		public MultiHttpClientConnThread(CloseableHttpClient client, String toEmailId,  String fromEmailId, String fromName, String subject, String htmlBody) {
			this.client = client;
			this.toEmailId = toEmailId;
			this.fromEmailId = fromEmailId;
			this.fromName = fromName;
			this.subject = subject;
			this.htmlBody = htmlBody;
		}

		public void run(){
			try {				
				HashMap<String, String> payLoad = new HashMap<>();
				payLoad.put("provider", "AWSSES");
				payLoad.put("htmlBody", htmlBody);
				payLoad.put("subject", subject);
				payLoad.put("from", "donotreply-ngasce@nmims.edu");
				payLoad.put("email", toEmailId);
				payLoad.put("fromName", fromName);
				sendAmazonSESDynamicMail(payLoad);
			} catch (Exception e) {
				
			}
		}
	}
	
	public String validityNotificationEmailBody(String isExpired,StudentStudentPortalBean student){
		String body = "",validtyEndDate="";
		try{
			validtyEndDate = getValidityEndDate(student);
		}catch(Exception e){
			
			validtyEndDate = "";
		}
		if("No".equals(isExpired)){
			body =  "  Dear "+student.getFirstName()+" "+student.getLastName()+", <br /> "
					+" <br />"
					+ " Greetings from NGA-SCE. <br /> "
					+" <br />"
					+" This is to inform that the validity of the program you registered for will expire on "+ validtyEndDate + "."
					+" Kindly ensure you appear for all pending subjects before the validity ends. <br/>"
					+" <br />"
					+" For any further queries please call our toll free number 1800 1025 136 (Monday to Saturday from 10 am to 6 pm).<br/>"
					+" <br />"
					+"Thanks & Regards"
					+"<br/>"
					+"NMIMS GLOBAL ACCESS SCHOOL FOR CONTINUING EDUCATION";
		}
		return body;
	}

	public void sendMassEmailsToStudentViaSMTP(List<String> toEmailIds, ArrayList<String> listOfSapIds,String subject, String fromEmailId, String htmlBody, String userId, String criteria) {
		
		if(!"PROD".equalsIgnoreCase(ENVIRONMENT)){
			System.out.println("Not Sending sendMassEmails as it is not Production..");
			return;
		}
		
		loggerForEmails.info("toEmailIds size in sendMassEmailsToStudentViaSMTP : "+toEmailIds.size());
		
		ArrayList<MailStudentPortalBean> mailList = new ArrayList<MailStudentPortalBean>();
		for (int i = 0; i < toEmailIds.size(); i = i + 1) {
			int lastIndex = (toEmailIds.size() < i+1 ? toEmailIds.size() : i+1);
			int lastIndexForSAPId = (listOfSapIds.size()<i+1 ? listOfSapIds.size():i+1);
			MailStudentPortalBean mailBean = new MailStudentPortalBean();
			loggerForEmails.info("EmailHelper:Sending emails from " + i + " to " + lastIndex);
			System.out.println("EmailHelper:Sending emails from " + i + " to " + lastIndex);
			List<String> emailIdSubList = toEmailIds.subList(i, lastIndex);
			List<String> sapIdSubList =  listOfSapIds.subList(i, lastIndexForSAPId);
			mailer.sendSMTPEmail(subject, htmlBody, fromEmailId, emailIdSubList);

			mailBean.setBody(htmlBody);
			mailBean.setMailIdRecipients(emailIdSubList);
			mailBean.setFilterCriteria(criteria);
			mailBean.setSubject(subject);
			mailBean.setFromEmailId(fromEmailId);
			mailBean.setSapIdRecipients(sapIdSubList);
			mailList.add(mailBean);
		}
		
		System.out.println("EmailHelper: MAIL LIST-->"+mailList.size());
		loggerForEmailCount.info("Total "+mailList.size()+" Mails sent via(SMTP) from "+fromEmailId+", Email Subject : "+subject);
		loggerForEmails.info("Saving Audit trail in file");
		writeEmailAuditTrailToFile(toEmailIds, subject, fromEmailId,htmlBody, userId, criteria);
		loggerForEmails.info("Creating entries in Mail table");
		createRecordInUserMailTableAndMailTable(mailList,userId,fromEmailId);
	}
	
	@SuppressWarnings("unchecked")
	@Async
	public String sendAmazonSESDynamicMail(HashMap<String, String> payload) {
		try {
			RestTemplate restTemplate = new RestTemplate();
			HashMap<String, String> response = restTemplate.postForObject(SERVER_PATH+"mailer/m/sendDynamicMail",payload, HashMap.class);
			//loggerForSessionEmails.info("Response in sendAmazonSESMail Status : " + response.get("message"));

			if ("true".equals(response.get("success"))) {
				return "Success";
			}
		} catch (Exception e) {
			e.printStackTrace();
			//loggerForSessionEmails.info("Error in sendAmazonSESMail Status : " + e.getStackTrace());
		}
		return "Failed";
	}
}
