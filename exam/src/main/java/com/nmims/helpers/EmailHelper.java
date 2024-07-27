package com.nmims.helpers;

import java.io.BufferedReader;
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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import com.nmims.beans.AssignmentFileBean;
import com.nmims.beans.FacultyExamBean;

@Component
public class EmailHelper {
	private final String USER_AGENT = "Mozilla/5.0"; 

	@Value( "${ASSIGNMENT_END_DATE}" )
	private String ASSIGNMENT_END_DATE;
	
	@Value( "${ASSIGNMENT_END_TIME}" )
	private String ASSIGNMENT_END_TIME;
	
	@Value( "${ASSIGNMENT_EVALUATION_END_DATE}" )
	private String ASSIGNMENT_EVALUATION_END_DATE;
	
	@Value( "${ENVIRONMENT}" )
	private String ENVIRONMENT;
	
	@Value( "${SERVER_PATH}" )
	private String SERVER_PATH;
	
	@Autowired(required=false)
	ApplicationContext act;
	
	@Async
	public void sendAssignmentReminderEmails(List<AssignmentFileBean> ansList,AssignmentFileBean searchBean) throws InterruptedException{

		//For testing comment line below, and do NOT change property to PROD on Test environment
		if(!"PROD".equalsIgnoreCase(ENVIRONMENT)){
			return;
		}
		
		ArrayList<String> toEmailIds = new ArrayList<>();
		String fromEmailId = "donotreply-ngasce@nmims.edu";
		String fromName = "NMIMS Global Access SCE";
		String emailSubject = "Internal Assignment Submission Reminder";
		
		HashMap<String, ArrayList<AssignmentFileBean>> studentSubjectMap = new HashMap<>();

		for (AssignmentFileBean bean : ansList) {
			String emailId = bean.getEmailId();
			String subject = bean.getSubject();
			if("Project".equalsIgnoreCase(subject)){
				//No assignment reminder for Project 
				continue;
			}
			if("Module 4 - Project".equalsIgnoreCase(subject)){
				//No assignment reminder for Project 
				continue;
			}
			if(emailId != null && !"".equals(emailId.trim()) && (emailId.indexOf("@") != -1)){
				toEmailIds.add(emailId);
			}
			if(!studentSubjectMap.containsKey(bean.getSapId())){
				ArrayList<AssignmentFileBean> subjectList = new ArrayList<>();
				subjectList.add(bean);

				studentSubjectMap.put(bean.getSapId(), subjectList);
			}else{
				ArrayList<AssignmentFileBean> subjectList = studentSubjectMap.get(bean.getSapId());
				subjectList.add(bean);
			}

		}
		

		PoolingHttpClientConnectionManager poolingConnManager = new PoolingHttpClientConnectionManager();
		try {
			poolingConnManager.setDefaultMaxPerRoute(200);
			poolingConnManager.setMaxTotal(500);

			//Setting the HttpClient to Check for Stale Connections
			CloseableHttpClient client = HttpClients.custom().setDefaultRequestConfig(
					RequestConfig.custom().setStaleConnectionCheckEnabled(true).build()).
					setConnectionManager(poolingConnManager).build();

			ExecutorService taskExecutor = Executors.newFixedThreadPool(500);

			for (Map.Entry<String, ArrayList<AssignmentFileBean>> entry : studentSubjectMap.entrySet()) {
				String sapId = entry.getKey();
				ArrayList<AssignmentFileBean> subjectList = entry.getValue();

				String htmlBody = getAssignmentReminderMailHtmlBody(subjectList, searchBean);
				String toEmailId = subjectList.get(0).getEmailId();
				MultiHttpClientConnThread curThread = new MultiHttpClientConnThread(client, toEmailId, fromEmailId, fromName, emailSubject, htmlBody);
				taskExecutor.execute(curThread);
			}

			MultiHttpClientConnThread curThread = new MultiHttpClientConnThread(client, "sanketpanaskar@gmail.com", fromEmailId, fromName, "Reminder Task Complete", "Email Reminder sent successfully to " +studentSubjectMap.size() + " students. <br><br> Thanks" );
			taskExecutor.execute(curThread);
			
			curThread = new MultiHttpClientConnThread(client, "ngasce.exams@nmims.edu", fromEmailId, fromName, "Reminder Task Complete", "Email Reminder sent successfully to " +studentSubjectMap.size() + " students. <br><br> Thanks" );
			taskExecutor.execute(curThread);
			
			taskExecutor.shutdown();

			try {
				taskExecutor.awaitTermination(Long.MAX_VALUE, TimeUnit.NANOSECONDS);
			} catch (InterruptedException e) {
				
			}
			client.close();

		} catch (Exception e) {
			
		}finally{
			poolingConnManager.close();
			poolingConnManager.shutdown();
		}
	}
	
	
	@Async
	public void sendAssignmentEvaluationReminderEmails(List<AssignmentFileBean> ansList,
			AssignmentFileBean searchBean, HashMap<String, FacultyExamBean> facultyMap) throws InterruptedException{

		//For testing comment line below, and do NOT change property to PROD on Test environment
		if(!"PROD".equalsIgnoreCase(ENVIRONMENT)){
			return;
		}
		
		String fromEmailId = "donotreply-ngasce@nmims.edu";
		String fromName = "NMIMS Global Access SCE";
		String emailSubject = "Internal Assignment Evaluation Reminder";
		
		HashMap<String, ArrayList<String>> facultySubjectMap = new HashMap<>();
		String level = searchBean.getLevel();
		for (AssignmentFileBean bean : ansList) {
			String facultyId = bean.getFacultyId();
			if("1".equals(level)){
				facultyId = bean.getFacultyId();
			}else if("2".equals(level)){
				facultyId = bean.getFaculty2();
			}
			
			String subject = bean.getSubject();
			
			if(!facultySubjectMap.containsKey(facultyId)){
				ArrayList<String> subjectList = new ArrayList<>();
				subjectList.add(subject);

				facultySubjectMap.put(facultyId, subjectList);
			}else{
				ArrayList<String> subjectList = facultySubjectMap.get(facultyId);
				if(!subjectList.contains(subject)){
					subjectList.add(subject);
				}
			}
		}
		

		PoolingHttpClientConnectionManager poolingConnManager = new PoolingHttpClientConnectionManager();
		try {
			poolingConnManager.setDefaultMaxPerRoute(200);
			poolingConnManager.setMaxTotal(500);

			//Setting the HttpClient to Check for Stale Connections
			CloseableHttpClient client = HttpClients.custom().setDefaultRequestConfig(
					RequestConfig.custom().setStaleConnectionCheckEnabled(true).build()).
					setConnectionManager(poolingConnManager).build();

			ExecutorService taskExecutor = Executors.newFixedThreadPool(500);

			for (Map.Entry<String, ArrayList<String>> entry : facultySubjectMap.entrySet()) {
				ArrayList<String> subjectList = entry.getValue();

				String htmlBody = getAssignmentEvaluationReminderMailHtmlBody(subjectList, searchBean);
				String toEmailId = facultyMap.get(entry.getKey()).getEmail();
				MultiHttpClientConnThread curThread = new MultiHttpClientConnThread(client, toEmailId, fromEmailId, fromName, emailSubject, htmlBody);
				taskExecutor.execute(curThread);
			}

			MultiHttpClientConnThread curThread = new MultiHttpClientConnThread(client, "sanketpanaskar@gmail.com", fromEmailId, fromName, "Reminder Task Complete", "Email Reminder sent successfully to " +facultySubjectMap.size() + " faculties. <br><br> Thanks" );
			taskExecutor.execute(curThread);
			
			curThread = new MultiHttpClientConnThread(client, "ngasce.exams@nmims.edu", fromEmailId, fromName, "Reminder Task Complete", "Evaluation Email Reminder sent successfully to " +facultySubjectMap.size() + " faculties. <br><br> Thanks" );
			taskExecutor.execute(curThread);
			
			taskExecutor.shutdown();

			try {
				taskExecutor.awaitTermination(Long.MAX_VALUE, TimeUnit.NANOSECONDS);
			} catch (InterruptedException e) {
				
			}
			client.close();

		} catch (Exception e) {
			
		}finally{
			poolingConnManager.close();
			poolingConnManager.shutdown();
		}
	}
	
	private String getAssignmentEvaluationReminderMailHtmlBody(ArrayList<String> subjectList, AssignmentFileBean searchBean) {
		String htmlBody = "";

		SimpleDateFormat ft = new SimpleDateFormat ("dd-MMM-yyyy");

		htmlBody = "Dear Sir / Ma'am, " + " <br><br> "
				+ "For the "+ searchBean.getMonth()+"-"+searchBean.getYear()+" exam cycle, the assignment evaluation assigned for the "
				+ " following subjects  is pending as on date " +ft.format(new Date()) + ".  "
				+ " You are requested to complete the assignment evaluation of the assigned subject on or before "
				+ " the last date of assignment evaluation i.e. " + ASSIGNMENT_EVALUATION_END_DATE +".<br/><br/>"
				+ " In case of any doubt/query, pls. drop an email on ngasce.exams@nmims.edu <br/>"
				+ "You can connect on 022-42355794 between 10.00 A.M. to 5.00 P.M. <br/><br/>" 
				+ "<b>Evaluation Pending Subject/s:" + " </b><br> " ;

		int index = 1;
		for (String subject : subjectList) {
			htmlBody = htmlBody + (index++) + ". " + subject +"<br>";
		}

		htmlBody = htmlBody + " <br> " 
				+ "Regards," + " <br> " 
				+ "NGA-SCE" + " <br> " ;

		return htmlBody;


	}

	private String getAssignmentReminderMailHtmlBody(ArrayList<AssignmentFileBean> subjectList, AssignmentFileBean searchBean) {
		String htmlBody = "";

		SimpleDateFormat ft = new SimpleDateFormat ("dd-MMM-yyyy");

		htmlBody = "Dear Student, " + " <br><br> " 

				+ "<b>Assignment applicable for "+ searchBean.getMonth()+"-"+searchBean.getYear()+" Examination for the following subject/s of Current Semester is pending as on date " +ft.format(new Date()) + " </b> <br> "
				+ "Kindly Note: Incase for previous semester/s subject/s: if assignment was not submitted previously, kindly submit the assignment before the last date of assignment submission <b>" + ASSIGNMENT_END_DATE + " before "+ ASSIGNMENT_END_TIME + "</b>. <br> <br>" 
				+ "Student Name: " + subjectList.get(0).getFirstName().toUpperCase() + " " + subjectList.get(0).getLastName().toUpperCase() + " <br> " 
				+ "Student ID: " + subjectList.get(0).getSapId() + " <br> " 
				+ "Program: " + subjectList.get(0).getProgram() + " <br><br> " 

				+ "<b>Current Semester Assignment Non-submitted Subject/s:" + " </b><br> " ;

		int index = 1;
		for (AssignmentFileBean assignmentFileBean : subjectList) {
			htmlBody = htmlBody + (index++) + ". " + assignmentFileBean.getSubject() +"<br>";
		}

		htmlBody = htmlBody + "<br><b>Please submit the assignment well before time and do not wait for last minute submission. Last Date of Internal Assignment Submission for "+ searchBean.getMonth()+"-"+searchBean.getYear()+" Examination is " + ASSIGNMENT_END_DATE + " "+ ASSIGNMENT_END_TIME + "</b> <br> " 
				+ "Login to: Student Zone -> Examination -> Assignment" + " <br> " 
				+ "<br><b>Students need to download and thoroughly read the "+ searchBean.getMonth()+"-"+searchBean.getYear()+" Internal Assignment Preparation and Submission guidelines before submitting the assignment" + "</b> <br> " 
				+ "Incase of any doubt or query regarding assignment submission, student can get in touch by email at ngasce.exams@nmims.edu for clarification before last date of assignment submission. No last minute assignment query/request will be accepted." + " <br><br> " 
				+ "Regards," + " <br> " 
				+ "NGA-SCE" + " <br> " ;

		return htmlBody;


	}

	/*public void sendMassEmail(ArrayList<String> toEmailIds, String fromEmailId, String fromName, String subject, String htmlBody) {
		PoolingHttpClientConnectionManager poolingConnManager = new PoolingHttpClientConnectionManager();
		try {
			poolingConnManager.setDefaultMaxPerRoute(200);
			poolingConnManager.setMaxTotal(500);

			//Setting the HttpClient to Check for Stale Connections
			CloseableHttpClient client = HttpClients.custom().setDefaultRequestConfig(
					RequestConfig.custom().setStaleConnectionCheckEnabled(true).build()).
					setConnectionManager(poolingConnManager).build();


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
			}

			client.close();

		} catch (Exception e) {
			
		}finally{
			poolingConnManager.close();
			poolingConnManager.shutdown();
		}
	}*/

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
	
	@Async
	public void sendGenericAssignmentReminderEmails(List<AssignmentFileBean> ansList, AssignmentFileBean searchBean) throws InterruptedException{

		//For testing comment line below, and do NOT change property to PROD on Test environment
		if(!"PROD".equalsIgnoreCase(ENVIRONMENT)){
			return;
		}
		
		ArrayList<String> toEmailIds = new ArrayList<>();
		String fromEmailId = "donotreply-ngasce@nmims.edu";
		String fromName = "NMIMS Global Access SCE";
		String emailSubject = "Internal Assignment Submission Pending: Reminder";
		
		HashMap<String, ArrayList<AssignmentFileBean>> studentSubjectMap = new HashMap<>();

		for (AssignmentFileBean bean : ansList) {
			String emailId = bean.getEmailId();
			String subject = bean.getSubject();
			if("Project".equalsIgnoreCase(subject)){
				//No assignment reminder for Project 
				continue;
			}
			if("Module 4 - Project".equalsIgnoreCase(subject)){
				//No assignment reminder for Project 
				continue;
			}
			if(emailId != null && !"".equals(emailId.trim()) && (emailId.indexOf("@") != -1)){
				toEmailIds.add(emailId);
			}
			if(!studentSubjectMap.containsKey(bean.getSapId())){
				ArrayList<AssignmentFileBean> subjectList = new ArrayList<>();
				subjectList.add(bean);

				studentSubjectMap.put(bean.getSapId(), subjectList);
			}else{
				ArrayList<AssignmentFileBean> subjectList = studentSubjectMap.get(bean.getSapId());
				subjectList.add(bean);
			}
		}
		
		toEmailIds = new ArrayList<String>(new LinkedHashSet<String>(toEmailIds));
		toEmailIds.add("sanketpanaskar@gmail.com");
		toEmailIds.add("ngasce.exams@nmims.edu");
		toEmailIds.add("jigna.patel@nmims.edu");


		try {
			String htmlBody = getGenericAssignmentReminderMailHtmlBody(searchBean);
			for (int i = 0; i < toEmailIds.size(); i = i + 100) {
				int lastIndex = (toEmailIds.size() < i+100 ? toEmailIds.size() : i+100);
				List<String> emailIdSubList =  toEmailIds.subList(i, lastIndex);
				//sendGroupEmailUsingTNSAPI(client, emailIdSubList, fromEmailId, fromName, emailSubject, htmlBody);
				HashMap<String, String> payload =  new HashMap<String, String>();
				payload.put("provider", "AWSSES");
				payload.put("htmlBody", htmlBody);
				payload.put("subject", emailSubject);
				payload.put("from", "donotreply-ngasce@nmims.edu");
				payload.put("email", emailIdSubList.stream().collect(Collectors.joining(",")));
				payload.put("fromName", fromName);
				
				sendAmazonSESDynamicMail(payload);
				
			}

		} catch (Exception e) {
			
		}
	}

	private String getGenericAssignmentReminderMailHtmlBody(AssignmentFileBean searchBean) {
		String htmlBody = "";
		SimpleDateFormat ft = new SimpleDateFormat ("dd-MMM-yyyy");
		
		htmlBody = "Dear Student, " + " <br><br> " 
				
				+"<font color=\"red\"><b>Your Internal Assignment submission is pending for one or more subjects as on date " +ft.format(new Date()) + ".</b></font><br><br>"
				+ "<b>Internal Assignment submission is a pre-requisite for Exam Registration and appearing for the Term End Examinations of NGA-SCE.</b>" + "<br>"
				+ "If a student initially does not submit assignment of a course/subject he/she will not be eligible for Exam Registration and will not be allowed to appear for the Term End Examination in the said course/subject." + "<br>"
				
				
				
				+ "<b>Kindly Note:</b> Incase for previous semester/s subject/s: if assignment was not submitted previously, kindly submit the assignment before the last date of assignment submission <b>" + ASSIGNMENT_END_DATE + " before "+ ASSIGNMENT_END_TIME + "</b>. <br> <br>" 

				+ "Internal Assignment applicable for "+ searchBean.getMonth()+"-"+searchBean.getYear()+" Examination is made available in Student Zone. Login to: Student Zone -> Examination -> Assignment" + "<br>"
				+ "<br><b>Students need to download and thoroughly read the "+ searchBean.getMonth()+"-"+searchBean.getYear()+" Internal Assignment Preparation and Submission guidelines before submitting the assignment" + "</b> <br> "
				
				+ "<br><b>Last Date of Internal Assignment Submission for "+ searchBean.getMonth()+"-"+searchBean.getYear()+" Examination is " + ASSIGNMENT_END_DATE + " "+ ASSIGNMENT_END_TIME + "</b> <br> " 
				+ "<br><b>Please submit the assignment well before time and do not wait for last minute submission. </b>" + "<br><br>"
				 
				+ "Incase of any doubt or query regarding assignment submission, student can get in touch by email at ngasce.exams@nmims.edu for clarification before last date of assignment submission. <br>"
				+ "No last minute assignment query/request will be accepted. Pls. mention student number in all communication with the institute." + " <br><br> "
				+ "Student can ignore if Internal Assignment has already been submitted for all subjects applicable for "+ searchBean.getMonth()+"-"+searchBean.getYear()+" Examination." + "<br><br>"
				+ "Regards," + " <br> " 
				+ "NGA-SCE" + " <br> " ;

		return htmlBody;
	}
	
	@Async
	public void testMethod() throws InterruptedException{
		Thread.sleep(5000);
	}

	public static void main(String[] args) {
		try {
			EmailHelper helper = new EmailHelper();
			ArrayList<AssignmentFileBean> ansList = new ArrayList<>();
			
			AssignmentFileBean searchbean = new AssignmentFileBean();
			searchbean.setEmailId("sanketpanaskar@gmail.com");
			ansList.add(searchbean);
			
			searchbean = new AssignmentFileBean();
			searchbean.setEmailId("jigna.patel@nmims.edu");
			ansList.add(searchbean);
			
			searchbean.setYear("2015");
			searchbean.setMonth("Jun");
			
			helper.sendGenericAssignmentReminderEmails(ansList, searchbean);

		} catch (Exception e) {
			
		}
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

				HashMap<String, String> payload =  new HashMap<String, String>();
				payload.put("provider", "AWSSES");
				payload.put("htmlBody", htmlBody);
				payload.put("subject", subject);
				payload.put("from", "donotreply-ngasce@nmims.edu");
				payload.put("email", toEmailId);
				payload.put("fromName", fromName);
				sendAmazonSESDynamicMail(payload);
			} catch (Exception e) {
				
			}
		}
	}
	
	/*@Async
	public void sendMassEmails(List<String> toEmailIds,String subject, String fromEmailId, String htmlBody, String userId) throws InterruptedException{


		//String fromEmailId = "ngasce@nmims.edu";
		String fromName = "NMIMS Global Access SCE";


		toEmailIds = new ArrayList<String>(new LinkedHashSet<String>(toEmailIds));
		
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
				List<String> emailIdSubList =  toEmailIds.subList(i, lastIndex);
				sendGroupEmailUsingTNSAPI(client, emailIdSubList, fromEmailId, fromName, subject, htmlBody);
			}

			client.close();


		} catch (Exception e) {
			
			
		}finally{
			poolingConnManager.close();
			poolingConnManager.shutdown();
		}
	}*/
	
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
		}
		return "Failed";
	}
	
	public String sendAmazonSESMail(HashMap<String, String> payload) {
		try {
			RestTemplate restTemplate = new RestTemplate();
			HashMap<String, String> response = restTemplate.postForObject(SERVER_PATH+"mailer/m/sendMail",payload, HashMap.class);

			if ("true".equals(response.get("success"))) {
				return "Success";
			}
		} catch (Exception e) {
			
		}
		return "Failed";
	}
}
