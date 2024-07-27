package com.nmims.helpers;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;

import org.apache.commons.codec.binary.Base64;
import org.apache.http.NameValuePair;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.apache.http.message.BasicNameValuePair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import com.nmims.beans.StudentBean;
import com.nmims.daos.StudentZoneDao;
import com.nmims.listeners.DelhiveryScheduler;
import com.sforce.soap.partner.sobject.SObject;

@Component
public class EmailHelper {
	private final String USER_AGENT = "Mozilla/5.0"; 
	
	@Value( "${ENVIRONMENT}" )
	private String ENVIRONMENT;

	@Autowired
	StudentZoneDao studentZoneDao;
	
	private static final Logger logger = LoggerFactory.getLogger(EmailHelper.class);
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
			//System.out.println("Result = "+result);

			JSONParser parser = new JSONParser();
			Object obj = parser.parse(result.toString());
			JSONObject jsonObject = (JSONObject) obj;
			//System.out.println(jsonObject);
			logger.info("Response body from tn mails :"+jsonObject);
			status = (Long) jsonObject.get("status");
			//System.out.println("status = "+status);

		} catch (Exception e) {
			e.printStackTrace();
		}
		return status;
	}
	

	
	@Async
	public void sendGenericKitDeliveredEmails(ArrayList<String> toEmailIds,ArrayList<String> studentSapIdList,HashMap<String,StudentBean> studentNumberStudentMap) throws InterruptedException{

		//For testing comment line below, and do NOT change property to PROD on Test environment
		if(!"PROD".equalsIgnoreCase(ENVIRONMENT)){
			//System.out.println("Not sending FedEx Delivered Emails : "+ENVIRONMENT);
			return;
		}
		
		String fromEmailId = "ngasce.exams@nmims.edu";
		String fromName = "NMIMS Global Access SCE";
		String emailSubject = "Your Study Kit is Delivered";
		
		//System.out.println("toEmailId size-->"+toEmailIds.size());
		//System.out.println("studentSapIdList size-->"+studentSapIdList.size());
		
		toEmailIds = new ArrayList<String>(new LinkedHashSet<String>(toEmailIds));
		toEmailIds.add("sanketpanaskar@gmail.com");
		toEmailIds.add("jforce.solution@gmail.com"); 
		toEmailIds.add("erin.prabhu.ext@nmims.edu"); 
		//toEmailIds.add("vikasrmenon@gmail.com");

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
				//System.out.println("Sending emails from " + i + " to " + lastIndex);
				StudentBean student = new StudentBean();
				student = studentNumberStudentMap.get(studentSapIdList.get(i));
				String studentSemester = student.getSem();
				String studentEmailId = student.getEmailId();
				
				String htmlBody = getGenericKitDeliveredMailHtmlBody(studentSapIdList.get(i),studentSemester);
				
				List<String> emailIdSubList =  new ArrayList<String>(Arrays.asList(studentEmailId));
				sendGroupEmailUsingTNSAPI(client, emailIdSubList, fromEmailId, fromName, emailSubject, htmlBody);
			}

			//System.out.println("Closing connections");
			client.close();

		} catch (Exception e) {
			e.printStackTrace();
		}finally{
			poolingConnManager.close();
			poolingConnManager.shutdown();
		}
		//System.out.println("Emails sent for Delivered Dispatches");
	}
	
	@Async
	public void sendGenericKitDeliveredEmailsFromDelhivery(ArrayList<StudentBean> deliveredStudentList) throws Exception
	{

		//For testing comment line below, and do NOT change property to PROD on Test environment
		if(!"PROD".equalsIgnoreCase(ENVIRONMENT)){
			logger.info("Not sending Delivered Emails : "+ENVIRONMENT);
			return;
		}
		
		String fromEmailId = "ngasce@nmims.edu";
		String fromName = "NMIMS Global Access SCE";
		
//		System.out.println("toEmailId size-->"+toEmailIds.size());
//		System.out.println("studentSapIdList size-->"+studentSapIdList.size());
//		
//		toEmailIds = new ArrayList<String>(new LinkedHashSet<String>(toEmailIds));
//		toEmailIds.add("sanketpanaskar@gmail.com");
//		toEmailIds.add("jforce.solution@gmail.com"); 
//		toEmailIds.add("erin.prabhu.ext@nmims.edu"); 
		//toEmailIds.add("vikasrmenon@gmail.com");

		PoolingHttpClientConnectionManager poolingConnManager = null;
		try {
			poolingConnManager = new PoolingHttpClientConnectionManager();
			poolingConnManager.setDefaultMaxPerRoute(200);
			poolingConnManager.setMaxTotal(500);

			//Setting the HttpClient to Check for Stale Connections
			CloseableHttpClient client = HttpClients.custom().setDefaultRequestConfig(
					RequestConfig.custom().setStaleConnectionCheckEnabled(true).build()).
					setConnectionManager(poolingConnManager).build();

			logger.info("Sending Delivered e-mails");
			for (StudentBean student:deliveredStudentList)
			{
				String mailStatus = "";
				String toEmailId=student.getEmailId();
				String subject="Your Study Kit is Delivered";
				String htmlBody = getGenericKitDeliveredMailHtmlBodyForDelhivery(student.getSkuType(),student.getOrderType(),student.getSapid(),student.getSem());
				if(student.getOrderType().equalsIgnoreCase("Single Book"))
					subject="Your Book is Delivered";
				mailStatus=sendGroupEmailUsingTNSAPIForDelhivery(client, toEmailId, fromEmailId, fromName, subject, htmlBody);
				if(!"Success".equals(mailStatus)){
					//Error in sending mail
					//System.out.println("Error in sending mail for Student Number : "+student.getSapid()+" with mail status "+mailStatus);
					logger.info("Error in sending mail for Student Number : "+student.getSapid()+" with mail status "+mailStatus);
				}
				else {
					logger.info("Succes in sending mail for Student Number : "+student.getSapid()+" with mail status "+mailStatus);
				}
			}
			//System.out.println("Closing connections");
			client.close();
			//createRecordInUserMailTableAndMailTable(deliveredStudentList);

		} catch (Exception e) {
			logger.info("Exception in sendGenericKitDeliveredEmailsFromDelhivery: "+e);
			//e.printStackTrace();
			//throw new Exception(e.getMessage());
			
		}finally{
			try
			{
				poolingConnManager.close();
				poolingConnManager.shutdown();
			}
			catch(Exception e)
			{
				logger.info("Exception while closing pooling connection manager:"+e);
			}
		}
	}
	
	@Async
	public String sendGroupEmailUsingTNSAPIForDelhivery(CloseableHttpClient client, String toEmailId,  String fromEmailId, String fromName, String subject, String htmlBody){
		String mailStatus = "";
		try {
			String url = "http://www.tnmails.com/api/sendTransactionalMail";
			String encodedBody = new String(Base64.encodeBase64(htmlBody.getBytes()));

			HttpPost post = new HttpPost(url);
			post.setHeader("User-Agent", USER_AGENT);
			String emailIds="[\""+toEmailId + "\"]";
			
			List<NameValuePair> urlParameters = new ArrayList<NameValuePair>();
			urlParameters.add(new BasicNameValuePair("fromemail", fromEmailId));
			urlParameters.add(new BasicNameValuePair("fromname", fromName));
			urlParameters.add(new BasicNameValuePair("tos", emailIds)); //Needs to be in JSON form only.
			urlParameters.add(new BasicNameValuePair("subject", subject));
			urlParameters.add(new BasicNameValuePair("splitrecipients", "true"));
			urlParameters.add(new BasicNameValuePair("body", encodedBody));
			urlParameters.add(new BasicNameValuePair("apikey", "gjyw9u0mm1ot1j65lr8ke1i3q2tbnlttuift3f6e7jxwttr50oeefm8vdrwq2f0loe5gk1kga93trjnrxru34to70an1gmcur8z1xa7x9yhq8u8ubto5c22c3e49r7s8"));
			
			post.setEntity(new UrlEncodedFormEntity(urlParameters));

			CloseableHttpResponse response = client.execute(post);
//			System.out.println("\nSending 'POST' request to URL : " + url);
//			System.out.println("Post parameters : " + post.getEntity());
//			System.out.println("Response Code : " +  response.getStatusLine().getStatusCode());
			
			Long status = parseJSONResponse(response);
			//System.out.println("STATUS PARAMETER "+status);
			logger.info("STATUS PARAMETER from parseJSONResponse method:"+status);
			if(response.getStatusLine().getStatusCode() ==200 && status==9001){//Check the response code and return success or failure/
				mailStatus ="Success";
			}else{
				mailStatus = response.getStatusLine().getStatusCode() + "-" + status;
			}
			response.close();

		} catch (Exception e) {
			logger.info("Exception from sendGroupEmailUsingTNSAPIForDelhivery: "+e);
		}
		return mailStatus;
	}
	
	public void createRecordInUserMailTableAndMailTable(ArrayList<StudentBean> deliveredStudentList)
	{
		try
		{
			for(StudentBean student : deliveredStudentList)
			{
				//System.out.println("Sapid In createRecordInUserMailTableAndMailTable--"+sapId);
				String subject="Your Study Kit is Delivered";
				String htmlBody = getGenericKitDeliveredMailHtmlBodyForDelhivery(student.getSkuType(),student.getOrderType(),student.getSapid(),student.getSem());
				if(student.getOrderType().equalsIgnoreCase("Single Book"))
					subject="Your Book is Delivered";
				long insertedMailId = studentZoneDao.insertMailRecord(student.getSapid(),"ngasce@nmims.edu",subject,htmlBody);
				studentZoneDao.insertUserMailRecord(student.getSapid(),"ngasce@nmims.edu",student.getSapid(),student.getEmailId(),insertedMailId);
			}
		}
		catch(Exception e)
		{
			logger.info("Exception in db insertion:"+e);
		}
	}
	
	@Async
	public void sendGroupEmailUsingTNSAPI(CloseableHttpClient client, List<String> toEmailIds,  String fromEmailId, String fromName, String subject, String htmlBody){
		try {
			String url = "http://www.tnmails.com/api/sendTransactionalMail";
			String encodedBody = new String(Base64.encodeBase64(htmlBody.getBytes()));

			HttpPost post = new HttpPost(url);
			post.setHeader("User-Agent", USER_AGENT);

			String emailIds = "[";
			for (String toEmailId : toEmailIds) {
				emailIds = emailIds + "\""+toEmailId + "\","; 
			}
			if(emailIds.endsWith(",")){
				emailIds = emailIds.substring(0, emailIds.length()-1);
			}

			emailIds = emailIds + "]";


			List<NameValuePair> urlParameters = new ArrayList<NameValuePair>();
			urlParameters.add(new BasicNameValuePair("fromemail", fromEmailId));
			urlParameters.add(new BasicNameValuePair("fromname", fromName));
			urlParameters.add(new BasicNameValuePair("tos", emailIds)); //Needs to be in JSON form only.
			urlParameters.add(new BasicNameValuePair("subject", subject));
			urlParameters.add(new BasicNameValuePair("splitrecipients", "true"));
			urlParameters.add(new BasicNameValuePair("body", encodedBody));
			urlParameters.add(new BasicNameValuePair("apikey", "gjyw9u0mm1ot1j65lr8ke1i3q2tbnlttuift3f6e7jxwttr50oeefm8vdrwq2f0loe5gk1kga93trjnrxru34to70an1gmcur8z1xa7x9yhq8u8ubto5c22c3e49r7s8"));
			
			post.setEntity(new UrlEncodedFormEntity(urlParameters));

			CloseableHttpResponse response = client.execute(post);
			//System.out.println("\nSending 'POST' request to URL : " + url);
			//System.out.println("Post parameters : " + post.getEntity());
			//System.out.println("Response Code : " +  response.getStatusLine().getStatusCode());
			
			Long status = parseJSONResponse(response);
			
			response.close();

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public String getGenericKitDeliveredMailHtmlBodyForDelhivery(String skuType,String orderType,String studentNumber,String sem)
	{
		String htmlBody="";
//		if(skuType.equalsIgnoreCase("Kit") && sem.equalsIgnoreCase("1"))
//		{
//			htmlBody = "Dear Student, " + " <br><br> " 
//					
//					+" Greetings from NMIMS <br><br> "
//					
//					/*
//					+" This email is in reference to the Study Kit which has been delivered to you. To report any issue (damage or missing items), do write in to ngasce@nmims.edu within 48 hrs."
//					*/
//					+ "This email is reference to the Study Kit comprises of [Course material, Welcome letter, ID card and Student undertaking] which "
//					+ "has been delivered to you. To report any issue (damage or missing items), do write in to ngasce@nmims.edu within 48 hrs. <br><br>"
//					
//					+" Kindly help us with your <a target=\"_blank\" href=\"http://ngasce.force.com/TakeSurvey?id=a0u90000004K9Sz&stnu="+studentNumber+"\">FeedBack </a> on this consignment <br><br>"
//
//					+" Thanks and Regards, <br>"
//					+" <b>Team NGASCE</b>";
//		}
		if(orderType.equalsIgnoreCase("Student Order") && "Kit".equalsIgnoreCase(skuType))
		{
			htmlBody = "Dear Student, " + " <br><br> " 
					
					+" Greetings from NMIMS <br><br> "
					+" This email is in reference to the Study Kit which has been delivered to you. To report any issue (damage or missing items), do write in to ngasce@nmims.edu within 48 hrs."
					
//					+ "This email is reference to the Study Kit comprises of [Course material, Welcome letter, ID card and Student undertaking] which "
//					+ "has been delivered to you. To report any issue (damage or missing items), do write in to ngasce@nmims.edu within 48 hrs. <br><br>"
		
					+" Kindly help us with your <a target=\"_blank\" href=\"http://ngasce.force.com/TakeSurvey?id=a0u90000004K9Sz&stnu="+studentNumber+"\">FeedBack </a> on this consignment. <br><br>"
					+" Thanks and Regards, <br>"
					+" <b>Team NGASCE</b>";
		}
		else if(orderType.equalsIgnoreCase("Single Book") || (orderType.equalsIgnoreCase("Student Order") && skuType.equalsIgnoreCase("Book")))
		{
			htmlBody= "Dear Student, " + " <br><br> "
					+ " Greetings from NMIMS <br><br> "
					+ "This email is in reference to the book which has been delivered to you. To report any issue (damage or missing items), do write in to ngasce@nmims.edu within 48 hrs."
					+" Kindly help us with your <a target=\"_blank\" href=\"http://ngasce.force.com/TakeSurvey?id=a0u90000004K9Sz&stnu="+studentNumber+"\">FeedBack </a> on this consignment. <br><br>"
					+" Thanks and Regards, <br>"
					+" <b>Team NGASCE</b>";
		}
		return htmlBody;
	}
	
	public String getGenericKitDeliveredMailHtmlBody(String studentNumber,String studentSemester) {
		String htmlBody = "";
		/*htmlBody = "Dear Student, " + " <br><br> " 
				
				+"Your Books are Delivered at requested shipping address. (IC or Your Address, as applicable)." + "<br><br>"
				+"Kindly click <a href=\"http://ngasce.force.com/TakeSurvey?id=a0u90000004K9Sz&stnu="+studentNumber+"\">FeedBack </a> to give your feedback <br><br>"
				
				+ "Regards," + " <br> " 
				+ "NGA-SCE" + " <br> " ;*/
		/*if("1".equals(studentSemester)){*/
			if(studentSemester.contains("1")){
			htmlBody = "Dear Student, " + " <br><br> " 
					
					+" Greetings from NMIMS <br><br> "
					
					/*
					+" This email is in reference to the Study Kit which has been delivered to you. To report any issue (damage or missing items), do write in to ngasce@nmims.edu within 48 hrs."
					*/
					+ "This email is reference to the Study Kit comprises of [Course material, Welcome letter, ID card and Student undertaking] which "
					+ "has been delivered to you. To report any issue (damage or missing items), do write in to ngasce@nmims.edu within 48 hrs. <br><br>"
					
					+" Kindly help us with your <a target=\"_blank\" href=\"http://ngasce.force.com/TakeSurvey?id=a0u90000004K9Sz&stnu="+studentNumber+"\">FeedBack </a> on this consignment <br><br>"

					+" Thanks and Regards, <br>"
					+" <b>Team NGASCE</b>";
		}else{
			htmlBody = "Dear Student, " + " <br><br> " 
								
						+" Greetings from NMIMS <br><br> "
						
						/*
						+" This email is in reference to the Study Kit which has been delivered to you. To report any issue (damage or missing items), do write in to ngasce@nmims.edu within 48 hrs."
						*/
						+ "This email is reference to the Study Kit comprises of [Course material, Welcome letter, ID card and Student undertaking] which "
						+ "has been delivered to you. To report any issue (damage or missing items), do write in to ngasce@nmims.edu within 48 hrs. <br><br>"
						
						+" Kindly help us with your <a target=\"_blank\" href=\"http://ngasce.force.com/TakeSurvey?id=a0u90000004K9Sz&stnu="+studentNumber+"\">FeedBack </a> on this consignment <br><br>"

						+" Thanks and Regards, <br>"
						+" <b>Team NGASCE</b>";
		}

		//System.out.println("htmlBody = "+htmlBody);
		return htmlBody;
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

				String url = "http://www.tnmails.com/api/sendTransactionalMail";
				String encodedBody = new String(Base64.encodeBase64(htmlBody.getBytes()));

				HttpPost post = new HttpPost(url);
				post.setHeader("User-Agent", USER_AGENT);

				List<NameValuePair> urlParameters = new ArrayList<NameValuePair>();
				urlParameters.add(new BasicNameValuePair("fromemail", fromEmailId));
				urlParameters.add(new BasicNameValuePair("fromname", fromName));
				urlParameters.add(new BasicNameValuePair("tos", "[\""+toEmailId+"\"]")); //Needs to be in JSON form only.
				urlParameters.add(new BasicNameValuePair("subject", subject));
				
				urlParameters.add(new BasicNameValuePair("body",encodedBody));
				urlParameters.add(new BasicNameValuePair("apikey", "gjyw9u0mm1ot1j65lr8ke1i3q2tbnlttuift3f6e7jxwttr50oeefm8vdrwq2f0loe5gk1kga93trjnrxru34to70an1gmcur8z1xa7x9yhq8u8ubto5c22c3e49r7s8"));
				

				post.setEntity(new UrlEncodedFormEntity(urlParameters));
				
				//System.out.println("Email = "+toEmailId);
				CloseableHttpResponse response = client.execute(post);
				//System.out.println("\nSending 'POST' request to URL : " + url);
				//System.out.println("Post parameters : " + post.getEntity());
				//System.out.println("Response Code : " +  response.getStatusLine().getStatusCode());

				Long status = parseJSONResponse(response);
				response.close();

			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
}
