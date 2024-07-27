package com.nmims.helpers;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
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
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import com.nmims.beans.MailAcadsBean;
import com.nmims.daos.NotificationDAO;


@Component
public class EmailHelper {
	private final String USER_AGENT = "Mozilla/5.0"; 
	
	private static final Logger loggerForEmailCount = LoggerFactory.getLogger("emailCount");
	private static final Logger loggerForSessionEmails = LoggerFactory.getLogger("session_emails");
	
	@Autowired
	ApplicationContext act;

	public void sendMassEmail(ArrayList<String> listOfSapIds,ArrayList<String> toEmailIds, String fromEmailId, String fromName, String subject, String htmlBody) {
		PoolingHttpClientConnectionManager poolingConnManager = new PoolingHttpClientConnectionManager();
		loggerForSessionEmails.info("Inside sendMassEmail");
		try {
			poolingConnManager.setDefaultMaxPerRoute(200);
			poolingConnManager.setMaxTotal(500);

			//Setting the HttpClient to Check for Stale Connections
			CloseableHttpClient client = HttpClients.custom().setDefaultRequestConfig(
					RequestConfig.custom().setStaleConnectionCheckEnabled(true).build()).
					setConnectionManager(poolingConnManager).build();

			/*List<Runnable> threads = new ArrayList<Runnable>();
			if(toEmailIds != null && toEmailIds.size() > 0){
				ExecutorService taskExecutor = Executors.newFixedThreadPool(500);

				for (String toEmailId : toEmailIds) {

					MultiHttpClientConnThread curThread = new MultiHttpClientConnThread(client, toEmailId, fromEmailId, fromName, subject, htmlBody);
					taskExecutor.execute(curThread);

				}
				taskExecutor.shutdown();
				try {
					taskExecutor.awaitTermination(Long.MAX_VALUE, TimeUnit.NANOSECONDS);
				} catch (InterruptedException e) {
					  
				}
			}*/
			int successCount = 0;
			ArrayList<MailAcadsBean> mailList = new ArrayList<MailAcadsBean>();
			for (int i = 0; i < toEmailIds.size(); i = i + 100) {
				int lastIndex = (toEmailIds.size() < i+100 ? toEmailIds.size() : i+100);
				int lastIndexForSAPId = (listOfSapIds.size()<i+100 ? listOfSapIds.size():i+100);
				loggerForSessionEmails.info("Sending emails from " + i + " to " + lastIndex);
				
				MailAcadsBean mailBean = new MailAcadsBean();
				List<String> emailIdSubList =  toEmailIds.subList(i, lastIndex);
				List<String> sapIdSubList =  listOfSapIds.subList(i, lastIndexForSAPId);
				
				loggerForSessionEmails.info("emailIdSubList : "+emailIdSubList);
				loggerForSessionEmails.info("sapIdSubList : "+sapIdSubList);
				
				String mailStatus = "";
				mailStatus = sendGroupEmailUsingTNSAPIAndReturnStatus(client, emailIdSubList, fromEmailId, fromName, subject, htmlBody);
				if("Success".equals(mailStatus)){
					//If Success then added to mailBean and append it to the final List//
					successCount = successCount + (lastIndex - i);
					mailBean.setBody(htmlBody);
					mailBean.setMailIdRecipients(emailIdSubList);
					mailBean.setFilterCriteria("Lecture Notification");
					mailBean.setSubject(subject);
					mailBean.setFromEmailId(fromEmailId);
					mailBean.setSapIdRecipients(sapIdSubList);;
					mailList.add(mailBean);
				}
			}

			loggerForSessionEmails.info("Total "+successCount+" Mails sent via(TNMails) from "+fromEmailId+", Email Subject : "+subject);
			loggerForEmailCount.info("Total "+successCount+" Mails sent via(TNMails) from "+fromEmailId+", Email Subject : "+subject);	
			createRecordInUserMailTableAndMailTable(mailList,"Academics",fromEmailId);
				
			/*for (Thread thread : threads) {
				thread.join();
			}*/
			client.close();

		} catch (Exception e) {
			  
		}finally{
			poolingConnManager.close();
			poolingConnManager.shutdown();
		}
	}
	
	
	@Async
	private void createRecordInUserMailTableAndMailTable(ArrayList<MailAcadsBean> successfullMailList,String userId,String fromEmailID){
		NotificationDAO notificationDAO = (NotificationDAO)act.getBean("notificationDAO");
		long insertedMailId = notificationDAO.insertMailRecord(successfullMailList,userId);
		notificationDAO.insertUserMailRecord(successfullMailList,userId,fromEmailID,insertedMailId);
		
	}

	
	@Async
	public String sendGroupEmailUsingTNSAPIAndReturnStatus(CloseableHttpClient client, List<String> toEmailIds,  String fromEmailId, String fromName, String subject, String htmlBody){
		String mailStatus = "";
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
			urlParameters.add(new BasicNameValuePair("ingoreInvalidEmails", "true"));
			
			post.setEntity(new UrlEncodedFormEntity(urlParameters));

			CloseableHttpResponse response = client.execute(post);
			
			loggerForSessionEmails.info("\nSending 'POST' request to URL : " + url);
			loggerForSessionEmails.info("Post parameters : " + post.getEntity());
			loggerForSessionEmails.info("Response Code : " +  response.getStatusLine().getStatusCode());
			
			Long status = parseJSONResponse(response);
			
			loggerForSessionEmails.info("STATUS PARAMETER "+status);
			
			if(response.getStatusLine().getStatusCode() ==200 && status==9001){//Check the response code and return success or failure/
				mailStatus ="Success";
			}else{
				mailStatus ="Failed";
			}
			
			loggerForSessionEmails.info("mailStatus "+mailStatus);
			response.close();

		} catch (Exception e) {
			  
		}
		return mailStatus;
	}

	@Async
	public void sendEmailUsingTNSAPI(CloseableHttpClient client, String toEmailId,  String fromEmailId, String fromName, String subject, String htmlBody){
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
			//urlParameters.add(new BasicNameValuePair("apikey", "r9x8p38getewcr6e7hdaw17reu2oax6sutqbmohq7mc039dyghz38wjcjarjym2k6ilhutxt5zswyvm52cwzy7387khvw06shgy3ymmuc4h1ptuhvi8kezhbapwwoseu"));

			post.setEntity(new UrlEncodedFormEntity(urlParameters));
			//post.setEntity(urlParameters);

			CloseableHttpResponse response = client.execute(post);
			Long status = parseJSONResponse(response);
			response.close();


		} catch (Exception e) {
			  
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

			JSONParser parser = new JSONParser();
			Object obj = parser.parse(result.toString());
			JSONObject jsonObject = (JSONObject) obj;
			status = (Long) jsonObject.get("status");

		} catch (Exception e) {
			  
		}
		return status;
	}

	/*@Async
	public void sendGroupEmailUsingTNSAPI(CloseableHttpClient client, ArrayList<String> toEmailIds,  String fromEmailId, String fromName, String subject, String htmlBody){
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
			urlParameters.add(new BasicNameValuePair("body", encodedBody));
			//urlParameters.add(new BasicNameValuePair("apikey", "gjyw9u0mm1ot1j65lr8ke1i3q2tbnlttuift3f6e7jxwttr50oeefm8vdrwq2f0loe5gk1kga93trjnrxru34to70an1gmcur8z1xa7x9yhq8u8ubto5c22c3e49r7s8"));
			urlParameters.add(new BasicNameValuePair("apikey", "r9x8p38getewcr6e7hdaw17reu2oax6sutqbmohq7mc039dyghz38wjcjarjym2k6ilhutxt5zswyvm52cwzy7387khvw06shgy3ymmuc4h1ptuhvi8kezhbapwwoseu"));

			post.setEntity(new UrlEncodedFormEntity(urlParameters));

			CloseableHttpResponse response = client.execute(post);
			response.close();

		} catch (Exception e) {
			  
		}
	}*/

	@Async
	public void testMethod() throws InterruptedException{
		Thread.sleep(5000);
	}

	public static void main(String[] args) {
		try {
			EmailHelper helper = new EmailHelper();
			ArrayList<String> emailids = new ArrayList<>(Arrays.asList("jforcesolutions@gmail.com"));
			//helper.sendMassEmail(emailids, "sanketpanaskar@gmail.com", "NMIMS Global Access SCE", "Test Email", "This is a test email");

		} catch (Exception e) {
			  
		}
	}

	/*public class MultiHttpClientConnThread implements Runnable{
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
				//urlParameters.add(new BasicNameValuePair("apikey", "r9x8p38getewcr6e7hdaw17reu2oax6sutqbmohq7mc039dyghz38wjcjarjym2k6ilhutxt5zswyvm52cwzy7387khvw06shgy3ymmuc4h1ptuhvi8kezhbapwwoseu"));

				post.setEntity(new UrlEncodedFormEntity(urlParameters));
				//post.setEntity(urlParameters);

				CloseableHttpResponse response = client.execute(post);
				Long status = parseJSONResponse(response);
				response.close();

			} catch (Exception e) {
				  
			}
		}
	}*/
}
