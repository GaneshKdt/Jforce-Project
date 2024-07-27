package com.nmims.helpers;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

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
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import com.nmims.beans.MailBean;
import com.nmims.beans.MettlSSOInfoBean;
import com.nmims.daos.ExamBookingDAO;

@Component
public class TNSMailSender {
	
	@Value( "${SERVER_PATH}" )
	private String SERVER_PATH;
	
	@Value( "${ENVIRONMENT}" )
	private String ENVIRONMENT;
	
	@Autowired
	ApplicationContext act;
	
	@Autowired
	EmailHelper emailHelper;

	private final String USER_AGENT = "Mozilla/5.0";
	
	private static final Logger loggerForEmailCount = LoggerFactory.getLogger("emailCount");

	public void sendJoinLinkEmailToStudent(MettlSSOInfoBean input) throws Exception {

		String body = ""
			+ "<div>Dear Student,</div>"
			+ "<div>"
			+ "Exam link for <b style='text-transform: capitalize;'>"+ input.getSubject() + "</b> starting at <b>" + input.getFormattedDateStringForEmail() + "</b>."
			+ "</div>"
			+ "<div>"
				+ "Link:&nbsp;<a href='" + input.getJoinURL() + "'>Click Here To Start The Exam</a></div>"
			+ "<br>"
			+ "<div>"
				+ "<ul>" + 
				"	<li>" + 
				"		Please do not forward this link as it is unique to every individual." + 
				"	</li>" + 
				"	" + 
				"	<li>" + 
				"		Student must ensure that they use the same laptop /desktop for the actual exam that was used for the system compatibility check and demo exam. " + 
				"		<b>" + 
				"			Note:- Exam cannot be taken on a tab or mobile phone." + 
				"		</b>" + 
				"	</li>" + 
				"	" + 
				"	<li>" + 
				"		Mettl Help line nos :- " + 
				"		<b>" + 
				"			08047190917" + 
				"		</b> " + 
				"		or " + 
				"		<b>" + 
				"			+918047190917" + 
				"		</b>" + 
				"	</li>" + 
				"	" + 
				"	<li>" + 
				"		NGASCE helpline details :-  " + 
				"		<b>" + 
				"			18001025136" + 
				"		</b>" + 
				"	</li>" + 
				"	" + 
				"	<li>" + 
				"		Do not access the link before the actual exam time, if the link doesn’t work you can access the same link via " + 
				"		<b>" + 
				"			Student Portal –Dashboard." + 
				"		</b>" + 
				"	</li>" + 
				"	" + 
				"	<li>" + 
				"		Waiting room feature is active now.Students must enter the Mettl examination portal 30 minutes before the exam start time and complete all mandatory screening activities." + 
				"	</li>" + 
				"	" + 
				"	<li>" + 
				"		When your exam starts, you will be automatically  redirected to your assessment." + 
				"	</li>" + 
				"	" + 
				"	<li>" + 
				"		Students must report for the assessment before the reporting end time. If the user attempts to enter the examination after the end time , the platform will not allow  to proceed." + 
				"	</li>" + 
				"	" + 
				"	<li>" + 
				"		<b>" + 
				"			Your examination clock begins and ends at the scheduled time." + 
				"		</b>" + 
				"	</li>" + 
				"</ul>"
				+ "<div>Regards,</div>"
				+ "<div><b>Team NGASCE</b></div>"
			+ "</div>";
		String encodedBody = new String(Base64.encodeBase64(body.getBytes()));
		
		
		String mailSubject = "Exam link for " + input.getSubject() + " starting at " + input.getFormattedDateStringForEmail();
		//sendTNSEMail(encodedBody, "[\"" + input.getEmailId() + "\"]", mailSubject);
		HashMap<String, String> payload =  new HashMap<String, String>();
		payload.put("provider", "AWSSES");
		payload.put("htmlBody", encodedBody);
		payload.put("subject", mailSubject);
		payload.put("from", "donotreply-ngasce@nmims.edu");
		payload.put("email", "no-reply@nmims.edu");
		payload.put("fromName", "NMIMS Global Access SCE");
		payload.put("bcc",input.getEmailId());
		emailHelper.sendAmazonSESDynamicMail(payload);
	}
	
	public void sendMassEmail(ArrayList<String> listOfSapIds,ArrayList<String> toEmailIds, String fromEmailId, String fromName, String subject, String htmlBody) {
		PoolingHttpClientConnectionManager poolingConnManager = new PoolingHttpClientConnectionManager();
		try {
			poolingConnManager.setDefaultMaxPerRoute(200);
			poolingConnManager.setMaxTotal(500);

			//Setting the HttpClient to Check for Stale Connections
			CloseableHttpClient client = HttpClients.custom().setDefaultRequestConfig(
					RequestConfig.custom().setStaleConnectionCheckEnabled(true).build()).
					setConnectionManager(poolingConnManager).build();
			
			int successCount = 0;
			ArrayList<MailBean> mailList = new ArrayList<MailBean>();
			for (int i = 0; i < toEmailIds.size(); i = i + 100) {
				int lastIndex = (toEmailIds.size() < i+100 ? toEmailIds.size() : i+100);
				int lastIndexForSAPId = (listOfSapIds.size()<i+100 ? listOfSapIds.size():i+100);
				MailBean mailBean = new MailBean();
				List<String> emailIdSubList =  toEmailIds.subList(i, lastIndex);
				List<String> sapIdSubList =  listOfSapIds.subList(i, lastIndexForSAPId);
				String mailStatus = "";
				//mailStatus = sendGroupEmailUsingTNSAPIAndReturnStatus(client, emailIdSubList, fromEmailId, fromName, subject, htmlBody);
				HashMap<String, String> payload =  new HashMap<String, String>();
				payload.put("provider", "AWSSES");
				payload.put("htmlBody", htmlBody);
				payload.put("subject", subject);
				payload.put("from", "donotreply-ngasce@nmims.edu");
				payload.put("email", emailIdSubList.stream().collect(Collectors.joining(",")));
				payload.put("fromName", fromName);
				
				mailStatus = emailHelper.sendAmazonSESDynamicMail(payload);
				if("Success".equals(mailStatus)){
					//If Success then added to mailBean and append it to the final List//
					successCount = successCount + (lastIndex - i);
					mailBean.setBody(htmlBody);
					mailBean.setMailIdRecipients(emailIdSubList);
					mailBean.setFilterCriteria("Exam Notification");
					mailBean.setSubject(subject);
					mailBean.setFromEmailId(fromEmailId);
					mailBean.setSapIdRecipients(sapIdSubList);
					mailList.add(mailBean);
				}
			}
			
			loggerForEmailCount.info("Total "+successCount+" Mails sent via(TNMails) from "+fromEmailId+", Email Subject : "+subject+" in sendMassEmail");
			createRecordInUserMailTableAndMailTable(mailList,"Exam",fromEmailId);
		}catch (Exception e) {
			
		}finally{
			poolingConnManager.close();
			poolingConnManager.shutdown();
		}
	}

	@Async
	private void createRecordInUserMailTableAndMailTable(ArrayList<MailBean> successfullMailList,String userId,String fromEmailID){
		ExamBookingDAO bookingDao = (ExamBookingDAO)act.getBean("examBookingDAO");
		long insertedMailId = bookingDao.insertMailRecord(successfullMailList,userId);
		bookingDao.insertUserMailRecord(successfullMailList,userId,fromEmailID,insertedMailId);
		
	}
}
