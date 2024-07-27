package com.nmims.helpers;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
import com.nmims.beans.SessionDayTimeAcadsBean;
import com.nmims.beans.StudentAcadsBean;
@Component
public class MobileNotificationHelper {
	@Value("${FIREBASE_SERVER_KEY}")
	private String Serverkey;
	
	
	@Value("${FIREBASE_SENDER_ID}")
	private String SenderId;
	
	private static final Logger loggerForSessionFirebaseNotifications = LoggerFactory.getLogger("session_firebase_notification");
	
	public HttpHeaders getFireBaseHeaders() {
		HttpHeaders headers =  new HttpHeaders();
		headers.add("Accept", "application/json");
		headers.add("Content-Type", "application/json");
		headers.add("Authorization", "key=" + Serverkey);
		headers.add("project_id", SenderId);
		return headers;
	}
	
	public void sendSessionNotification(SessionDayTimeAcadsBean session,List<StudentAcadsBean> studentList) {
		
		try {
			
			loggerForSessionFirebaseNotifications.info("Inside sendSessionNotification...");
			
			String body = session.getSubject() + " " + session.getSessionName();
			String title = "Session Reminder";
			
			JsonObject notification = new JsonObject();
			notification.addProperty("body", body);
			notification.addProperty("title", title);
			notification.addProperty("content_available", true);
			notification.addProperty("priority", "high");
			notification.addProperty("showWhenInForeground", true);
			
			//Calling send notification method with three param
			this.sendSessionNotification(notification,studentList);
		}
		catch (Exception e) {
			loggerForSessionFirebaseNotifications.info("Error in sendSessionNotification Catch "+e.getMessage());
			  
		}
	}
	
	public void sendSessionNotification(JsonObject notification,List<StudentAcadsBean> studentList) {
		loggerForSessionFirebaseNotifications.info("MobileNotificationHelper.sendSessionNotification(-,-,-) - START");
		String url = "https://fcm.googleapis.com/fcm/send";
		
		ArrayList<ArrayList<String>> tokenIds = new ArrayList<ArrayList<String>>();
		ArrayList<String> tmp_list = new ArrayList<String>();
		
		if(studentList == null) {
			return;
		}
	
		for(int i=1;i <= studentList.size();i++) {
			StudentAcadsBean student = studentList.get(i - 1);
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
				
				JsonObject request = new JsonObject();
				request.add("registration_ids", students);
				request.addProperty("time_to_live", 3600);
				request.add("notification", notification);
				request.add("data", notification);
				HttpEntity<String> entity = new HttpEntity<String>(request.toString(),headers);
				ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.POST, entity, String.class);
				loggerForSessionFirebaseNotifications.info("response code "+response.getStatusCode().toString());
				if (!"200".equalsIgnoreCase(response.getStatusCode().toString())) {
					loggerForSessionFirebaseNotifications.info("error while sending push notification");
				}
			}
			catch (Exception e) {
				loggerForSessionFirebaseNotifications.info("Error in sendSessionNotification Catch "+e.getMessage());
				  
			}
		}
		loggerForSessionFirebaseNotifications.info("MobileNotificationHelper.sendSessionNotification(-,-,-) - END");
	}//sendSessionNotification(-,-,-)
	
}
