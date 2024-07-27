package com.nmims.helpers;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.ArrayList;
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

import com.nmims.beans.MailCareerservicesBean;
import com.nmims.daos.NotificationDAO;

@Component
public class EmailHelper {

	@Autowired
	ApplicationContext act;

	private static final Logger logger = LoggerFactory.getLogger(EmailHelper.class);
 
	private final String USER_AGENT = "Mozilla/5.0"; 
	
	public void sendMassEmail(ArrayList<String> listOfSapIds,ArrayList<String> toEmailIds, String fromEmailId, String fromName, String subject, String htmlBody) {
		PoolingHttpClientConnectionManager poolingConnManager = new PoolingHttpClientConnectionManager();
		try {
			poolingConnManager.setDefaultMaxPerRoute(200);
			poolingConnManager.setMaxTotal(500);

			//Setting the HttpClient to Check for Stale Connections
			CloseableHttpClient client = HttpClients.custom().setDefaultRequestConfig(
					RequestConfig.custom().setStaleConnectionCheckEnabled(true).build()).
					setConnectionManager(poolingConnManager).build();

			ArrayList<MailCareerservicesBean> mailList = new ArrayList<MailCareerservicesBean>();
			for (int i = 0; i < toEmailIds.size(); i = i + 100) {
				int lastIndex = (toEmailIds.size() < i+100 ? toEmailIds.size() : i+100);
				int lastIndexForSAPId = (listOfSapIds.size()<i+100 ? listOfSapIds.size():i+100);

				MailCareerservicesBean mailBean = new MailCareerservicesBean();
				List<String> emailIdSubList =  toEmailIds.subList(i, lastIndex);
				List<String> sapIdSubList =  listOfSapIds.subList(i, lastIndexForSAPId);
				String mailStatus = "";
				mailStatus = sendGroupEmailUsingTNSAPIAndReturnStatus(client, emailIdSubList, fromEmailId, fromName, subject, htmlBody);
				if("Success".equals(mailStatus)){
					//If Success then added to mailBean and append it to the final List//
					mailBean.setBody(htmlBody);
					mailBean.setMailIdRecipients(emailIdSubList);
					mailBean.setFilterCriteria("Lecture Notification");
					mailBean.setSubject(subject);
					mailBean.setFromEmailId(fromEmailId);
					mailBean.setSapIdRecipients(sapIdSubList);;
					mailList.add(mailBean);
				}
			}

				
			createRecordInUserMailTableAndMailTable(mailList,"Academics",fromEmailId);
				
			/*for (Thread thread : threads) {
				thread.join();
			}*/

			client.close();

		} catch (Exception e) {
			logger.info("exception : "+e.getMessage());
		}finally{
			poolingConnManager.close();
			poolingConnManager.shutdown();
		}
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
			
			Long status = parseJSONResponse(response);
			
			if(response.getStatusLine().getStatusCode() ==200 && status==9001){//Check the response code and return success or failure/
				mailStatus ="Success";
			}else{
				mailStatus ="Failed";
			}
			
			response.close();

		} catch (Exception e) {
			logger.info("exception : "+e.getMessage());
		}
		return mailStatus;
	}
	
	@Async
	private void createRecordInUserMailTableAndMailTable(ArrayList<MailCareerservicesBean> successfullMailList,String userId,String fromEmailID){

		NotificationDAO notificationDAO = (NotificationDAO)act.getBean("notificationDAO");
		long insertedMailId = notificationDAO.insertMailRecord(successfullMailList,userId);
		notificationDAO.insertUserMailRecord(successfullMailList,userId,fromEmailID,insertedMailId);
		
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
			logger.info("exception : "+e.getMessage());
		}
		return status;
	}

}
