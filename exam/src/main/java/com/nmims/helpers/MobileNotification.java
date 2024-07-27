package com.nmims.helpers;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.nmims.beans.StudentExamBean;

@Component
public class MobileNotification {
	
	private final String USER_AGENT = "Mozilla/5.0";
	
	@Value("${FIREBASE_SERVER_KEY}")
	private String Serverkey;
	
	
	@Value("${FIREBASE_SENDER_ID}")
	private String SenderId;
	
	@Value("${SMS_VERSION}")
	private String SMS_VERSION;

	@Value("${SMS_KEY}")
	private String SMS_KEY;

	@Value("${SMS_ENCRYPT}")
	private String SMS_ENCRYPT;

	@Value("${SMS_SENDERID}")
	private String SMS_SENDERID;

	@Value("${SMS_BULK_URL}")
	private String SMS_BULK_URL;
	
	
	public void sendEmailToExamTomorrow(List<StudentExamBean> studentList) {
		String body = "Please ensure you have taken the Demo exam and checked compatibility. Note exam cannot be taken on a Tab or mobile phone";
		String title = "Exam Tomorrow!!";
		String smsMessage = "Please ensure you have taken the Demo exam and checked compatibility. Note exam cannot be taken on a Tab or mobile phone";
		this.sendPushNotification(title,body,studentList);
		this.sendSMSSender(smsMessage, studentList);
	}
	
	public void sendDemoExamNotification(List<StudentExamBean> studentList) {
		String body = "It is Mandatory to Attend the Demo Exam before giving your Final Exam. Your Demo Exam is pending";
		String title = "Demo Exam Pending";
		String smsMessage = "It is Mandatory to Attend the Demo Exam before giving your Final Exam. Your Demo Exam is pending ";
		this.sendPushNotification(title,body,studentList);
		this.sendSMSSender(smsMessage, studentList);
	}
	
	private void sendSMSSender(String message,List<StudentExamBean> studentList) {
		String numbers = "";
		for (StudentExamBean student : studentList) {
			String mobile = student.getMobile();
			if (mobile != null && !"".equals(mobile)) {
				numbers = numbers + "," + mobile;
			}
		}
		
		try {
			URL obj = new URL(SMS_BULK_URL);
			HttpURLConnection con = (HttpURLConnection) obj.openConnection();

			// add request header
			con.setRequestMethod("GET");
			con.setRequestProperty("User-Agent", USER_AGENT);
			con.setRequestProperty("Accept-Language", "en-US,en;q=0.5");

			String urlParameters = "ver=" + SMS_VERSION + "&key=" + SMS_KEY + "&encrpt=" + SMS_ENCRYPT + "&dest="
					+ numbers + "&send=" + SMS_SENDERID + "&text=" + URLEncoder.encode(message, "UTF-8");

			// Send post request
			con.setDoOutput(true);
			DataOutputStream wr = new DataOutputStream(con.getOutputStream());
			wr.writeBytes(urlParameters);
			wr.flush();
			wr.close();

			int responseCode = con.getResponseCode();

			BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
			String inputLine;
			StringBuffer response = new StringBuffer();

			while ((inputLine = in.readLine()) != null) {
				response.append(inputLine);
			}
			in.close();

			// print result


//			if(responseCode == 200) {
//				/return "OK";
//			}else {
//				return response.toString();
//			}
//			response fully depends on string and isnt xml
//			return parseSMSResponse(response.toString());
		} catch (Exception e) {
			
		}
	}
	
	private void sendPushNotification(String title,String body,List<StudentExamBean> studentList) {
		
		String url = "https://fcm.googleapis.com/fcm/send";
		ArrayList<ArrayList<String>> tokenIds = new ArrayList<ArrayList<String>>();
		ArrayList<String> tmp_list = new ArrayList<String>();
		
		if(studentList == null) {
			return;
		}
		
		for(int i=1;i <= studentList.size();i++) {
			StudentExamBean student = studentList.get(i - 1);
			if(i%1000 != 0) {
				if(student.getFirebaseToken() != null) {	// to avoid null firebaseToken 
					tmp_list.add(student.getFirebaseToken());
				}
			}else {
				tokenIds.add(tmp_list);
				tmp_list = new ArrayList<String>();
			}
		}
		if(tmp_list.size() > 0) {
			tokenIds.add(tmp_list);
			tmp_list = new ArrayList<String>();
		}
		
		for(int i=0;i < tokenIds.size();i++) {
			try {
				Gson gson = new GsonBuilder().create();
				JsonArray students = gson.toJsonTree(tokenIds.get(i)).getAsJsonArray();
				
				HttpHeaders headers =  this.getFireBaseHeaders();
				RestTemplate restTemplate = new RestTemplate();
				
				JsonObject notification = new JsonObject();
				notification.addProperty("body", body);
				notification.addProperty("title", title);
				notification.addProperty("content_available", true);
				notification.addProperty("priority", "high");
				notification.addProperty("showWhenInForeground", true);
				
				JsonObject request = new JsonObject();
				request.add("registration_ids", students);
				request.addProperty("time_to_live", 3600);
				request.add("notification", notification);
				request.add("data", notification);
				HttpEntity<String> entity = new HttpEntity<String>(request.toString(),headers);
				ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.POST, entity, String.class);
				if (!"200".equalsIgnoreCase(response.getStatusCode().toString())) {
				}
			}
			catch (Exception e) {
				// TODO: handle exception
				
			}
		}
	}
	
	private HttpHeaders getFireBaseHeaders() {
		HttpHeaders headers =  new HttpHeaders();
		headers.add("Accept", "application/json");
		headers.add("Content-Type", "application/json");
		headers.add("Authorization", "key=" + Serverkey);
		headers.add("project_id", SenderId);
		return headers;
	}
}
