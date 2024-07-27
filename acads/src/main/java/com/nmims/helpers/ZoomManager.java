package com.nmims.helpers;

import java.io.IOException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Random;
import java.util.TimeZone;

import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.JsonSyntaxException;
import com.nmims.beans.ParticipantReportBean;
import com.nmims.beans.ParticipantsListBean;
import com.nmims.beans.SessionBean;
import com.nmims.beans.SessionDayTimeAcadsBean;
import com.nmims.beans.StudentAcadsBean;
import com.nmims.beans.WebinarPollsBean;
import com.nmims.beans.WebinarPollsListBean;
import com.nmims.beans.WebinarPollsQuestionsBean;
import com.nmims.beans.WebinarPollsResultsBean;
import com.nmims.daos.TimeTableDAO;

public class ZoomManager {

	private String site;       
	private String ZoomHostID;
	private String ZoomUserID;
	private String password;
	private String accessTokan;
	private String webinarPassword;
	private final String zoomAPIBaseURL = "https://ngasce.zoom.us/v2/";      
	private final String zoomAPIStartMeetingBaseURL = "https://ngasce.zoom.us/s/";      

	
	public String getSite() {
		return site;
	}
	public void setSite(String site) {
		this.site = site;
	}
	public String getZoomHostID() {
		return ZoomHostID;
	}
	public void setZoomHostID(String zoomHostID) {
		ZoomHostID = zoomHostID;
	}
	public String getZoomUserID() {
		return ZoomUserID;
	}
	public void setZoomUserID(String zoomUserID) {
		ZoomUserID = zoomUserID;
	}
	public String getPassword() {
		return password;
	}
	public void setPassword(String password) {
		this.password = password;
	}
	public String getAccessTokan() {
		return accessTokan;
	}
	public void setAccessTokan(String accessTokan) {
		this.accessTokan = accessTokan;
	}
	public String getWebinarPassword() {
		return webinarPassword;
	}
	public void setWebinarPassword(String webinarPassword) {
		this.webinarPassword = webinarPassword;
	}
	
	public HttpHeaders getZoomAuthenticateHeaders() {
		HttpHeaders headers =  new HttpHeaders();
		headers.add("Accept", "application/json, application/xml");
		headers.add("Content-Type", "application/json");
		headers.add("Authorization", "Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6Imp3dCJ9.eyJpc3MiOiJrczFPX1BCUFJmTzdxN1duSGE5UWl3IiwiZXhwIjoiMTQ5NjA5MTk2NDAwMCJ9.CvrMLpU36wK1m_M3R7yqtG9I7hXQZ6EC3FQ1eZUSwcU");
		return headers;
	}
	public void getWebinar() throws IOException{
		
		CloseableHttpClient httpClient = HttpClientBuilder.create().build();
		String url = "https://ngasce.zoom.us/v2/webinars/711329461";
		
		try {
			HttpHeaders headers =  new HttpHeaders();
			headers.add("Accept", "application/json, application/xml");
			headers.add("Content-Type", "application/json");
			headers.add("Authorization", "Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6Imp3dCJ9.eyJpc3MiOiJrczFPX1BCUFJmTzdxN1duSGE5UWl3IiwiZXhwIjoiMTQ5NjA5MTk2NDAwMCJ9.CvrMLpU36wK1m_M3R7yqtG9I7hXQZ6EC3FQ1eZUSwcU");
			
			RestTemplate restTemplate = new RestTemplate();
			HttpEntity<String> entity = new HttpEntity<String>("parameters", headers);
			
			ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.GET, entity, String.class);
			JsonObject jsonObj = new JsonParser().parse(response.getBody()).getAsJsonObject();
		} catch (HttpClientErrorException e) {
			e.printStackTrace();
		}
		finally {
			httpClient.close();
		}
		
		
	}
	
	public void scheduleWebinarBatchJob(SessionDayTimeAcadsBean session) throws IOException{
		CloseableHttpClient httpClient = HttpClientBuilder.create().build();
		String url = "https://ngasce.zoom.us/v2/users/"+session.getHostId()+"/webinars";
		
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
		sdf.setTimeZone(TimeZone.getTimeZone("GMT"));
		
		//Comment
		try {
			
			RestTemplate restTemplate = new RestTemplate();
			JsonObject request = new JsonObject();
			
			session.setMeetingPwd(generatePassword());
			request.addProperty("topic", session.getSessionName());
			request.addProperty("type", 5);
//			request.addProperty("start_time", sdf.format(session.getDate()));
			request.addProperty("start_time",session.getDate()+"T"+session.getStartTime());
			request.addProperty("duration", 120);
			request.addProperty("timezone", "Asia/Kolkata");
			request.addProperty("password", session.getMeetingPwd());
			request.addProperty("agenda", session.getSubject());
			/* Settings  */
			
			JsonObject req = new JsonObject();
			req.addProperty("host_video", false);
			req.addProperty("panelists_video", false);
			req.addProperty("practice_session", true);
			req.addProperty("hd_video", false);
			req.addProperty("approval_type", 0);
			req.addProperty("registration_type", 3);
			req.addProperty("audio", "both");
			req.addProperty("auto_recording", "cloud");
			req.addProperty("enforce_login", false);
			req.addProperty("enforce_login_domains", "");
			req.addProperty("alternative_hosts", "");
			req.addProperty("close_registration", false);
			req.addProperty("show_share_button", false);
			req.addProperty("allow_multiple_devices", true);
			req.addProperty("registrants_confirmation_email", false);
			req.addProperty("notify_registrants", false);
			req.addProperty("registrants_email_notification", false);
			req.addProperty("post_webinar_survey", true);
			req.addProperty("survey_url", "https://studentzone-ngasce.nmims.edu/acads/student/getPostSessionFeedback");
				
			request.add("settings", req);

			HttpEntity<String> entity = new HttpEntity<String>(request.toString(), getZoomAuthenticateHeaders());
			ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.POST, entity, String.class);
			JsonObject jsonObj = new JsonParser().parse(response.getBody()).getAsJsonObject();
			
			if ("201".equalsIgnoreCase(response.getStatusCode().toString())) {
				session.setErrorRecord(false);
				String hostJoinUrl = jsonObj.get("start_url").getAsString();
				String studentJoinUrl = jsonObj.get("join_url").getAsString();
				String webinarID = jsonObj.get("id").getAsString();
				session.setMeetingKey(webinarID);
				session.setJoinUrl(studentJoinUrl);
				session.setHostUrl(hostJoinUrl);
				
			}
			
		} catch (HttpClientErrorException e) {
			e.printStackTrace();
			session.setErrorRecord(true);
		    
		    /*if ("404".equalsIgnoreCase(e.getStatusCode().toString())){
				session.setErrorRecord(true);
				session.setErrorMessage(""+e.getStatusCode());
			} else if ("1001".equalsIgnoreCase(e.getStatusCode().toString())){
				session.setErrorRecord(true);
				session.setErrorMessage(""+e.getStatusCode());
			} else if ("1010".equalsIgnoreCase(e.getStatusCode().toString())){
				session.setErrorRecord(true);
				session.setErrorMessage(""+e.getStatusCode());
			}*/
		    
		}
		
		finally {
			httpClient.close();
		}
	}
	
	public void scheduleMeetingBatchJob(SessionDayTimeAcadsBean session) throws IOException{
		CloseableHttpClient httpClient = HttpClientBuilder.create().build();
		String url = "https://ngasce.zoom.us/v2/users/"+session.getHostId()+"/meetings";
		
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
		sdf.setTimeZone(TimeZone.getTimeZone("GMT"));
		
		//Comment
		try {
			
			RestTemplate restTemplate = new RestTemplate();
			JsonObject request = new JsonObject();
			
			session.setMeetingPwd(generatePassword());
			request.addProperty("topic", session.getSessionName());
			request.addProperty("type", 2);
//			request.addProperty("start_time", sdf.format(session.getDate()));
			request.addProperty("start_time",session.getDate()+"T"+session.getStartTime());
			request.addProperty("duration", 120);
			request.addProperty("timezone", "Asia/Kolkata");
			request.addProperty("password", session.getMeetingPwd());
			request.addProperty("agenda", session.getSubject());
			/* Settings  */
			
			JsonObject req = new JsonObject();
			req.addProperty("host_video", false);
			req.addProperty("participant_video", false);
			req.addProperty("join_before_host", false);
			req.addProperty("waiting_room", true);
			req.addProperty("participant_video", false);
			req.addProperty("mute_upon_entry", false);
			req.addProperty("approval_type", 0);
			req.addProperty("registration_type", 3);
			req.addProperty("audio", "both");
			req.addProperty("auto_recording", "cloud");
			req.addProperty("enforce_login", false);
			req.addProperty("enforce_login_domains", "");
			req.addProperty("registrants_confirmation_email", false);
			req.addProperty("registrants_email_notification", false);
			request.add("settings", req);

			HttpEntity<String> entity = new HttpEntity<String>(request.toString(), getZoomAuthenticateHeaders());
			ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.POST, entity, String.class);
			JsonObject jsonObj = new JsonParser().parse(response.getBody()).getAsJsonObject();
			
			if ("201".equalsIgnoreCase(response.getStatusCode().toString())) {
				session.setErrorRecord(false);
				String hostJoinUrl = jsonObj.get("start_url").getAsString();
				String studentJoinUrl = jsonObj.get("join_url").getAsString();
				String webinarID = jsonObj.get("id").getAsString();
				session.setMeetingKey(webinarID);
				session.setJoinUrl(studentJoinUrl);
				session.setHostUrl(hostJoinUrl);
				
			}
			
		} catch (HttpClientErrorException e) {
			e.printStackTrace();
			session.setErrorRecord(true);
		    
		    /*if ("404".equalsIgnoreCase(e.getStatusCode().toString())){
				session.setErrorRecord(true);
				session.setErrorMessage(""+e.getStatusCode());
			} else if ("1001".equalsIgnoreCase(e.getStatusCode().toString())){
				session.setErrorRecord(true);
				session.setErrorMessage(""+e.getStatusCode());
			} else if ("1010".equalsIgnoreCase(e.getStatusCode().toString())){
				session.setErrorRecord(true);
				session.setErrorMessage(""+e.getStatusCode());
			}*/
		    
		}
		
		finally {
			httpClient.close();
		}
	}
	
	public void deleteWebinar(SessionDayTimeAcadsBean session,String webinarID) throws IOException{
		
		CloseableHttpClient httpClient = HttpClientBuilder.create().build();
		String url = "https://ngasce.zoom.us/v2/webinars/"+webinarID;
		
		try {
			
			RestTemplate restTemplate = new RestTemplate();
			HttpEntity<String> entity = new HttpEntity<String>("parameters", getZoomAuthenticateHeaders());
			ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.DELETE, entity, String.class);
			
			String statusCode = response.getStatusCode().toString();
			if (statusCode.equalsIgnoreCase(Integer.toString(204))) {
				session.setErrorRecord(false);
			}
			
		} catch (HttpClientErrorException e) {
			e.printStackTrace();
			session.setErrorRecord(true);
			session.setErrorMessage(e.getStatusText());
		}
		finally {
			httpClient.close();
		}
	}
	
	public void deleteMeeting(SessionDayTimeAcadsBean session,String meetingKey) throws IOException{
		
		CloseableHttpClient httpClient = HttpClientBuilder.create().build();
		String url = "https://ngasce.zoom.us/v2/meetings/"+meetingKey;
		
		try {
			
			RestTemplate restTemplate = new RestTemplate();
			HttpEntity<String> entity = new HttpEntity<String>("parameters", getZoomAuthenticateHeaders());
			ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.DELETE, entity, String.class);
			
			String statusCode = response.getStatusCode().toString();
			if (statusCode.equalsIgnoreCase(Integer.toString(204))) {
				session.setErrorRecord(false);
			}
			
		} catch (HttpClientErrorException e) {
			e.printStackTrace();
			session.setErrorRecord(true);
			session.setErrorMessage(e.getStatusText());
		}
		finally {
			httpClient.close();
		}
	}
	
	public String registrantsForWebinar(SessionDayTimeAcadsBean session,StudentAcadsBean student,String webinarID) throws IOException{
		
		CloseableHttpClient httpClient = HttpClientBuilder.create().build();
		String url = "https://ngasce.zoom.us/v2/webinars/"+webinarID+"/registrants";
		String regJoinUrl = null;
		
		try {
			
			RestTemplate restTemplate = new RestTemplate();
			JsonObject request = new JsonObject();
			
			request.addProperty("email", student.getEmailId());
			request.addProperty("first_name", student.getFirstName());
			request.addProperty("last_name", student.getLastName());
			
			HttpEntity<String> entity = new HttpEntity<String>(request.toString(), getZoomAuthenticateHeaders());	
			ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.POST, entity, String.class);
			JsonObject jsonObj = new JsonParser().parse(response.getBody()).getAsJsonObject();
			
			if ("201".equalsIgnoreCase(response.getStatusCode().toString())) {
				regJoinUrl = jsonObj.get("join_url").getAsString();
			}
			
			
		} catch (HttpClientErrorException e) {
//			e.printStackTrace();
			session.setErrorRecord(true);
			session.setErrorMessage(e.getMessage());
		}
		finally {
			httpClient.close();
		}
		return regJoinUrl;
	}
	
	public String registrantsForMeeting(SessionDayTimeAcadsBean session,StudentAcadsBean student,String meetingKey) throws IOException{
		
		CloseableHttpClient httpClient = HttpClientBuilder.create().build();
		String url = "https://ngasce.zoom.us/v2/meetings/"+meetingKey+"/registrants";
		String regJoinUrl = null;
		
		try {
			
			RestTemplate restTemplate = new RestTemplate();
			JsonObject request = new JsonObject();
			
			request.addProperty("email", student.getEmailId());
			request.addProperty("first_name", student.getFirstName());
			request.addProperty("last_name", student.getLastName());
			
			HttpEntity<String> entity = new HttpEntity<String>(request.toString(), getZoomAuthenticateHeaders());	
			ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.POST, entity, String.class);
			JsonObject jsonObj = new JsonParser().parse(response.getBody()).getAsJsonObject();
			
			if ("201".equalsIgnoreCase(response.getStatusCode().toString())) {
				regJoinUrl = jsonObj.get("join_url").getAsString();
			}
			
			
		} catch (HttpClientErrorException e) {
			e.printStackTrace();
			session.setErrorRecord(true);
			session.setErrorMessage(e.getMessage());
		}
		finally {
			httpClient.close();
		}
		return regJoinUrl;
	}
	
	public HashMap<String, String> registrantsForWebinarByMobile(SessionDayTimeAcadsBean session,StudentAcadsBean student,String webinarID) throws IOException{
		CloseableHttpClient httpClient = HttpClientBuilder.create().build();
		String url = "https://ngasce.zoom.us/v2/webinars/"+webinarID+"/registrants";
		
		String registrant_id = null;
		String id = null;
		String topic = null;
		String start_time = null;
		String regJoinUrl = null;
		
		ResponseEntity<String> response = null;
		JsonObject jsonObj = null;
		HashMap<String, String> responseObj = new HashMap<>();
		
		try {
			
			RestTemplate restTemplate = new RestTemplate();
			JsonObject request = new JsonObject();
			
			request.addProperty("email", student.getEmailId());
			request.addProperty("first_name", student.getFirstName());
			request.addProperty("last_name", student.getLastName());
			
			HttpEntity<String> entity = new HttpEntity<String>(request.toString(), getZoomAuthenticateHeaders());	
			response = restTemplate.exchange(url, HttpMethod.POST, entity, String.class);
			jsonObj = new JsonParser().parse(response.getBody()).getAsJsonObject();
		
			if ("201".equalsIgnoreCase(response.getStatusCode().toString())) {
				registrant_id = jsonObj.get("registrant_id").getAsString();
				id = jsonObj.get("id").getAsString();
				topic = jsonObj.get("topic").getAsString();
				start_time = jsonObj.get("start_time").getAsString();
				regJoinUrl = jsonObj.get("join_url").getAsString();
				
				responseObj.put("registrant_id", registrant_id);
				responseObj.put("id", id);
				responseObj.put("topic", topic);
				responseObj.put("start_time", start_time);
				responseObj.put("join_url", regJoinUrl);
				responseObj.put("status", "success");
				
			}
			
		} catch (HttpClientErrorException e) {
			e.printStackTrace();
			session.setErrorRecord(true);
			session.setErrorMessage(e.getStatusText());
		}
		finally {
			httpClient.close();
		}
		return responseObj;	
	}
	
	public HashMap<String, String> registrantsForMeetingByMobile(SessionDayTimeAcadsBean session,StudentAcadsBean student,String webinarID) throws IOException{
		CloseableHttpClient httpClient = HttpClientBuilder.create().build();
		String url = "https://ngasce.zoom.us/v2/meetings/"+webinarID+"/registrants";
		
		String registrant_id = null;
		String id = null;
		String topic = null;
		String start_time = null;
		String regJoinUrl = null;
		
		ResponseEntity<String> response = null;
		JsonObject jsonObj = null;
		HashMap<String, String> responseObj = new HashMap<>();
		
		try {
			
			RestTemplate restTemplate = new RestTemplate();
			JsonObject request = new JsonObject();
			
			request.addProperty("email", student.getEmailId());
			request.addProperty("first_name", student.getFirstName());
			request.addProperty("last_name", student.getLastName());
			
			HttpEntity<String> entity = new HttpEntity<String>(request.toString(), getZoomAuthenticateHeaders());	
			response = restTemplate.exchange(url, HttpMethod.POST, entity, String.class);
			jsonObj = new JsonParser().parse(response.getBody()).getAsJsonObject();
		
			if ("201".equalsIgnoreCase(response.getStatusCode().toString())) {
				registrant_id = jsonObj.get("registrant_id").getAsString();
				id = jsonObj.get("id").getAsString();
				topic = jsonObj.get("topic").getAsString();
				start_time = jsonObj.get("start_time").getAsString();
				regJoinUrl = jsonObj.get("join_url").getAsString();
				
				responseObj.put("registrant_id", registrant_id);
				responseObj.put("id", id);
				responseObj.put("topic", topic);
				responseObj.put("start_time", start_time);
				responseObj.put("join_url", regJoinUrl+"&uuid="+session.getHostId());
				responseObj.put("status", "success");
				
			}
			
		} catch (HttpClientErrorException e) {
			e.printStackTrace();
			session.setErrorRecord(true);
			session.setErrorMessage(e.getStatusText());
		}
		finally {
			httpClient.close();
		}
		return responseObj;
		
	}
	
	public void checkValidWebinar(SessionDayTimeAcadsBean session, String hostId, String webinarID) throws IOException{
		
		CloseableHttpClient httpClient = HttpClientBuilder.create().build();
		String url = "https://ngasce.zoom.us/v2/webinars/"+webinarID;
		
		try {
			HttpHeaders headers =  new HttpHeaders();
			headers.add("Accept", "application/json, application/xml");
			headers.add("Content-Type", "application/json");
			headers.add("Authorization", "Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6Imp3dCJ9.eyJpc3MiOiJrczFPX1BCUFJmTzdxN1duSGE5UWl3IiwiZXhwIjoiMTQ5NjA5MTk2NDAwMCJ9.CvrMLpU36wK1m_M3R7yqtG9I7hXQZ6EC3FQ1eZUSwcU");
			
			RestTemplate restTemplate = new RestTemplate();
			HttpEntity<String> entity = new HttpEntity<String>("parameters", headers);
			
			ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.GET, entity, String.class);
			JsonObject jsonObj = new JsonParser().parse(response.getBody()).getAsJsonObject();
			
			String resWebinarID = jsonObj.get("id").getAsString();
			String resHostId = jsonObj.get("host_id").getAsString();
			String start_time = jsonObj.get("start_time").getAsString();
			String sessionTime = session.getDate()+"T"+session.getStartTime()+"Z";
			
			String DATE_FORMAT_PATTERN = "yyyy-MM-dd'T'HH:mm:ss'Z'";
			
			
			SimpleDateFormat newFormat = new SimpleDateFormat(DATE_FORMAT_PATTERN);
			DateFormat utcFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");
			utcFormat.setTimeZone(TimeZone.getTimeZone("UTC"));

			Date date = null;
			try {
				date = utcFormat.parse(start_time);
			} catch (ParseException e) {
				e.printStackTrace();
			}
			String finalDate = newFormat.format(date);
			
			if (sessionTime.equalsIgnoreCase(finalDate)) {
				session.setErrorRecord(false);
				System.out.println("Webinar Time match");
				
				if (resWebinarID.equalsIgnoreCase(webinarID)) {
					System.out.println("WebinarID Match");
					session.setErrorRecord(false);
					
					if (resHostId.equalsIgnoreCase(hostId)) {
						System.out.println("HostId match");
						session.setErrorRecord(false);
					}else {
						System.out.println("HostId Not match");
						session.setErrorRecord(true);
					}
				}else {
					System.out.println("WebinarID Not Match");
					session.setErrorRecord(true);
				}
			} else {
				System.out.println("Webinar Time not match");
				session.setErrorRecord(true);
			}
			
		} catch (HttpClientErrorException e) {
			e.printStackTrace();
			System.out.println("inside catch..");
			 session.setErrorRecord(true);
		}
		finally {
			httpClient.close();
		}
	}
	
	
	public void scheduleWebinar(SessionDayTimeAcadsBean session, String hostId) throws IOException{
		CloseableHttpClient httpClient = HttpClientBuilder.create().build();
		String url = "https://ngasce.zoom.us/v2/users/"+hostId+"/webinars";
		
		try {
			HttpHeaders headers =  new HttpHeaders();
			headers.add("Accept", "application/json, application/xml");
			headers.add("Content-Type", "application/json");
			headers.add("Authorization", "Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6Imp3dCJ9.eyJpc3MiOiJrczFPX1BCUFJmTzdxN1duSGE5UWl3IiwiZXhwIjoiMTQ5NjA5MTk2NDAwMCJ9.CvrMLpU36wK1m_M3R7yqtG9I7hXQZ6EC3FQ1eZUSwcU");
			
			RestTemplate restTemplate = new RestTemplate();
			session.setMeetingPwd(generatePassword());
			JsonObject request = new JsonObject();
			request.addProperty("topic", session.getSessionName());
			request.addProperty("type", 5);
			request.addProperty("start_time",session.getDate()+"T"+session.getStartTime());
			request.addProperty("duration", 120);
			request.addProperty("timezone", "Asia/Kolkata");
			request.addProperty("password", session.getMeetingPwd());
			request.addProperty("agenda", session.getSubject());
			/* Settings */
			
			JsonObject req = new JsonObject();
			req.addProperty("host_video", false);
			req.addProperty("panelists_video", false);
			req.addProperty("practice_session", true);
			req.addProperty("hd_video", false);
			req.addProperty("approval_type", 0);
			req.addProperty("registration_type", 3);
			req.addProperty("audio", "both");
			req.addProperty("auto_recording", "cloud");
			req.addProperty("enforce_login", false);
			req.addProperty("enforce_login_domains", "");
			req.addProperty("alternative_hosts", "");
			req.addProperty("close_registration", false);
			req.addProperty("show_share_button", true);
			req.addProperty("allow_multiple_devices", true);
			req.addProperty("registrants_confirmation_email", false);
			req.addProperty("notify_registrants", false);
			req.addProperty("registrants_email_notification", false);
			req.addProperty("post_webinar_survey", true);
			req.addProperty("survey_url", "https://studentzone-ngasce.nmims.edu/acads/student/getPostSessionFeedback");
				   
			request.add("settings", req);
			
			HttpEntity<String> entity = new HttpEntity<String>(request.toString(),headers);
			ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.POST, entity, String.class);
			JsonObject jsonObj = new JsonParser().parse(response.getBody()).getAsJsonObject();
			
			if ("201".equalsIgnoreCase(response.getStatusCode().toString())) {
				session.setErrorRecord(false);
				String hostJoinUrl = jsonObj.get("start_url").getAsString();
				String studentJoinUrl = jsonObj.get("join_url").getAsString();
				String webinarID = jsonObj.get("id").getAsString();

				session.setMeetingKey(webinarID);
				session.setJoinUrl(studentJoinUrl);
				session.setHostUrl(hostJoinUrl);
				
			}
			
		} catch (HttpClientErrorException e) {
			e.printStackTrace();
			session.setErrorRecord(true);
		}
		
		finally {
			httpClient.close();
		}
	}
	
	
	public void updateWebinar(SessionDayTimeAcadsBean session) throws IOException{
		CloseableHttpClient httpClient = HttpClientBuilder.create().build();
		String url = "https://api.zoom.us/v2/webinars/" + session.getMeetingKey();
		try {
			HttpHeaders headers =  new HttpHeaders();
			headers.add("Accept", "application/json, application/xml");
			headers.add("Content-Type", "application/json");
			headers.add("Authorization", "Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6Imp3dCJ9.eyJpc3MiOiJrczFPX1BCUFJmTzdxN1duSGE5UWl3IiwiZXhwIjoiMTQ5NjA5MTk2NDAwMCJ9.CvrMLpU36wK1m_M3R7yqtG9I7hXQZ6EC3FQ1eZUSwcU");
			
			RestTemplate restTemplate = new RestTemplate(new HttpComponentsClientHttpRequestFactory());
			
			session.setMeetingPwd(generatePassword());
			
			JsonObject request = new JsonObject();
			request.addProperty("topic", session.getSessionName());
			request.addProperty("type", 5);
			request.addProperty("start_time",session.getDate()+"T"+session.getStartTime());
			request.addProperty("duration", 120);
			request.addProperty("timezone", "Asia/Kolkata");
			request.addProperty("password", session.getMeetingPwd());
			request.addProperty("agenda", session.getSubject());
			/* Settings */
			
			JsonObject req = new JsonObject();
			req.addProperty("host_video", false);
			req.addProperty("panelists_video", false);
			req.addProperty("practice_session", true);
			req.addProperty("hd_video", false);
			req.addProperty("approval_type", 0);
			req.addProperty("registration_type", 3);
			req.addProperty("audio", "both");
			req.addProperty("auto_recording", "cloud");
			req.addProperty("enforce_login", false);
			req.addProperty("enforce_login_domains", "");
			req.addProperty("alternative_hosts", "");
			req.addProperty("close_registration", false);
			req.addProperty("show_share_button", true);
			req.addProperty("allow_multiple_devices", true);
			req.addProperty("registrants_confirmation_email", false);
			req.addProperty("notify_registrants", false);
			req.addProperty("registrants_email_notification", false);
			req.addProperty("post_webinar_survey", true);
			req.addProperty("survey_url", "https://studentzone-ngasce.nmims.edu/acads/student/getPostSessionFeedback");
				   
			request.add("settings", req);
			
			HttpEntity<String> entity = new HttpEntity<String>(request.toString(),headers);
			//ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.PATCH, entity, String.class);
			restTemplate.patchForObject(url, entity, ResponseEntity.class);
			session.setErrorRecord(false);
			
		} catch (HttpClientErrorException e) {
			e.printStackTrace();
			session.setErrorRecord(true);
		}
		
		finally {
			httpClient.close();
		}
	}
	
	private String generatePassword() {
		int length= 10;
		String capitalCaseLetters = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
	      String lowerCaseLetters = "abcdefghijklmnopqrstuvwxyz";
	      String specialCharacters = "!@#$";
	      String numbers = "1234567890";
	      String combinedChars = capitalCaseLetters + lowerCaseLetters + specialCharacters + numbers;
	      Random random = new Random();
	      char[] password = new char[length];

	      password[0] = lowerCaseLetters.charAt(random.nextInt(lowerCaseLetters.length()));
	      password[1] = capitalCaseLetters.charAt(random.nextInt(capitalCaseLetters.length()));
	      password[2] = specialCharacters.charAt(random.nextInt(specialCharacters.length()));
	      password[3] = numbers.charAt(random.nextInt(numbers.length()));

	      for(int i = 4; i< length ; i++) {
	         password[i] = combinedChars.charAt(random.nextInt(combinedChars.length()));
	      }
	      return new String(password);
	}
	
	public String getStartWebinarLink(String meetingKey , String hostId) {
		RestTemplate restTemplate = new RestTemplate();  
		HttpEntity<String> entity = new HttpEntity<String>(getZoomAuthenticateHeaders());
		
		JsonObject jsonObj;
		try {
			ResponseEntity<String> apiResponse = restTemplate.exchange(zoomAPIBaseURL + "users/" + hostId + "/token?type=zak", HttpMethod.GET, entity, String.class);
			jsonObj = new JsonParser().parse(apiResponse.getBody()).getAsJsonObject();
			String url = zoomAPIStartMeetingBaseURL +  meetingKey + "?zak=" + jsonObj.get("token").getAsString();
			return url;
		} catch (RestClientException e) {
			e.printStackTrace();
			return "error";
		} catch (JsonSyntaxException e) {
			e.printStackTrace();
			return "error";
		}

	}
	
	public boolean deleteRecordingFile(String webinarId,String fileId) {
			
		                String url = "https://api.zoom.us/v2/meetings/"+ webinarId +"/recordings/" + fileId;
			
		                try {
			
		                        RestTemplate restTemplate = new RestTemplate(new HttpComponentsClientHttpRequestFactory());
			
		                        JsonObject request = new JsonObject();
			
		                        request.addProperty("action", "delete");
			
		                        HttpEntity<String> entity = new HttpEntity<String>(request.toString(),getZoomAuthenticateHeaders());
			
		                        restTemplate.exchange(url, HttpMethod.DELETE, entity, String.class);
			
		                        //restTemplate.patchForObject(url, entity, ResponseEntity.class);
			
		                        //restTemplate.delete(url, entity);
			
		                        return true;
			
		                }
			
		                catch (Exception e) {
		                        e.printStackTrace();
		                        return false;
		                }
		        }
	
	
	//change return type to json
	public ResponseEntity<String> getZoomRecordingList(TimeTableDAO recording_status,String meetingId,boolean markError) throws IOException {
		//TimeTableDAO sessions = (TimeTableDAO)act.getBean("timeTableDAO");
		//String url = "https://api.zoom.us/v2/users/"+ userID +"/recordings?from="+ from +"&to=" + to;
		String url = "https://api.zoom.us/v2/meetings/"+ meetingId +"/recordings";
		try {
			 //get zoom required header
			RestTemplate restTemplate = new RestTemplate();
			HttpEntity<String> entity = new HttpEntity<String>("",getZoomAuthenticateHeaders());
			ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.GET, entity, String.class);
			return response;
		}
		catch (Exception e) {
			e.printStackTrace();
			if(markError) {
				recording_status.errorRecordingStatus(meetingId,"Error while getting video from zoom");
			}
			return null;
		}
	}
	
	public ResponseEntity<String> getZoomRecordingList2(TimeTableDAO recording_status,String meetingId,String sessionId,String sessionDate) throws IOException {
		//TimeTableDAO sessions = (TimeTableDAO)act.getBean("timeTableDAO");
		//String url = "https://api.zoom.us/v2/users/"+ userID +"/recordings?from="+ from +"&to=" + to;
		String url = "https://api.zoom.us/v2/meetings/"+ meetingId +"/recordings";
		try {
			 //get zoom required header
			RestTemplate restTemplate = new RestTemplate();
			HttpEntity<String> entity = new HttpEntity<String>("",getZoomAuthenticateHeaders());
			ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.GET, entity, String.class);
			return response;
		}
		catch (Exception e) {
			e.printStackTrace();
			recording_status.markAsDelete(sessionId,meetingId, "Error: " + e.getMessage(),sessionDate);
			return null;
		}
	}

	public JsonObject  getQAReportFromWebinar(String id) throws IOException {
		JsonObject jsonObj = null;
		String url = zoomAPIBaseURL +"report/webinars/"+id+"/qa";
		
		try { 

			RestTemplate restTemplate = new RestTemplate();  
			HttpEntity<String> entity = new HttpEntity<String>(getZoomAuthenticateHeaders());
			ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.GET, entity, String.class);
			 jsonObj = new JsonParser().parse(response.getBody()).getAsJsonObject();
			
		} catch (HttpClientErrorException e) {
			e.printStackTrace();
		}
		
		return jsonObj;

	}

	public JsonObject createWebinarPoll(String webinarId, WebinarPollsBean webinarPollsBean) throws IOException{

		JsonObject jsonObject = null;
		String url = zoomAPIBaseURL+"webinars/"+webinarId+"/polls";

		try {
			HttpHeaders headers =  getZoomAuthenticateHeaders();

			JsonObject request = new JsonObject();

			List<WebinarPollsQuestionsBean> webinarPollsQuestionsBeanList=webinarPollsBean.getQuestions();

			request.addProperty("title", webinarPollsBean.getTitle());
			request.add("questions", new Gson().toJsonTree(webinarPollsQuestionsBeanList));
			
			RestTemplate restTemplate = new RestTemplate();
			HttpEntity<String> entity = new HttpEntity<String>(request.toString(), headers);

			ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.POST, entity, String.class);
			jsonObject = new JsonParser().parse(response.getBody()).getAsJsonObject();
			
		} catch (HttpClientErrorException e) {
			e.printStackTrace();
		}
		return jsonObject;
	}

	public WebinarPollsListBean getWebinarPolls(String webinarId){
		WebinarPollsListBean webinarPollsListBean = null;
		String url = zoomAPIBaseURL+"webinars/"+webinarId+"/polls";

		try {

			RestTemplate restTemplate = new RestTemplate();
			HttpEntity<String> entity = new HttpEntity<String>(getZoomAuthenticateHeaders());						
			ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.GET, entity, String.class);
			JsonObject jsonObject = new JsonParser().parse(response.getBody()).getAsJsonObject();
			String json_string = new Gson().toJson(jsonObject);
			webinarPollsListBean = new Gson().fromJson(json_string, WebinarPollsListBean.class);

		} catch (HttpClientErrorException e) {
			e.printStackTrace();
		}
		return webinarPollsListBean;

	}
	
	public void updateWebinarName(SessionDayTimeAcadsBean session) throws IOException{
		CloseableHttpClient httpClient = HttpClientBuilder.create().build();
		String url = zoomAPIBaseURL+"webinars/" + session.getMeetingKey();
		try {
			
			RestTemplate restTemplate = new RestTemplate(new HttpComponentsClientHttpRequestFactory());
			JsonObject request = new JsonObject();
			request.addProperty("topic", session.getSessionName());
			HttpEntity<String> entity = new HttpEntity<String>(request.toString(), getZoomAuthenticateHeaders());
			restTemplate.patchForObject(url, entity, ResponseEntity.class);
			session.setErrorRecord(false);
			
		}catch (Exception e) {
			e.printStackTrace();
			session.setErrorRecord(true);
		}finally {
			httpClient.close();
		}
	}
	
	public void updateMeetingName(SessionDayTimeAcadsBean session) throws IOException{
		CloseableHttpClient httpClient = HttpClientBuilder.create().build();
		String url = zoomAPIBaseURL+"meetings/" + session.getMeetingKey();
		try {
			
			RestTemplate restTemplate = new RestTemplate(new HttpComponentsClientHttpRequestFactory());
			JsonObject request = new JsonObject();
			request.addProperty("topic", session.getSessionName());
			HttpEntity<String> entity = new HttpEntity<String>(request.toString(), getZoomAuthenticateHeaders());
			restTemplate.patchForObject(url, entity, ResponseEntity.class);
			session.setErrorRecord(false);
			
		}catch (Exception e) {
			e.printStackTrace();
			session.setErrorRecord(true);
		}finally {
			httpClient.close();
		}
	}
	
	//For Update Webinar Polls
	public HttpStatus updateWebinarPoll(String webinarId, WebinarPollsBean webinarPollsBean) throws IOException{
		  
  		HttpStatus httpStatus = null;
  		String url = zoomAPIBaseURL+"webinars/"+webinarId+"/polls/"+webinarPollsBean.getId();
  
  		try {
  			HttpHeaders headers =  getZoomAuthenticateHeaders();
  
  			JsonObject request = new JsonObject();
  
  			List<WebinarPollsQuestionsBean> webinarPollsQuestionsBeanList=webinarPollsBean.getQuestions();
  			
  
  			request.addProperty("title", webinarPollsBean.getTitle());
  			request.add("questions", new Gson().toJsonTree(webinarPollsQuestionsBeanList));
  
//  			System.out.println("request json for updating polls : "+request);
  
  			RestTemplate restTemplate = new RestTemplate();
  			HttpEntity<String> entity = new HttpEntity<String>(request.toString(), headers);
  
  			ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.PUT, entity, String.class);
  			
  			httpStatus=response.getStatusCode();	
  			
  
  		} catch (HttpClientErrorException e) {
  		
  		}
  		return httpStatus;
  	}
	
	//Api for Deleting Webinar Polls
	public HttpStatus deleteWebinarPoll(String webinarId, String pollId){
		String url = zoomAPIBaseURL+"webinars/"+webinarId+"/polls/"+pollId;
  		HttpStatus httpStatus=null;
  		try {
  
  			RestTemplate restTemplate = new RestTemplate();
  			HttpEntity<String> entity = new HttpEntity<String>(getZoomAuthenticateHeaders());
  			ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.DELETE, entity, String.class);						
//  			System.out.println("responseEntity response of deleting webinar polls : "  +response);
  			
  			httpStatus=response.getStatusCode();	
//  			System.out.println("response status code of deleting webinar polls  ::: "  +httpStatus);
  
  		} catch (HttpClientErrorException e) {
  			httpStatus=e.getStatusCode();
//  			System.out.println(e.getStatusCode());
//  			System.out.println(e.getResponseBodyAsString());
  		}
  		return httpStatus;
  	}
	
	//Api For getting the poll results
	public WebinarPollsResultsBean getWebinarPollsResults(String webinarId){
  		WebinarPollsResultsBean webinarPollsResultsBean = null;
  		String url = zoomAPIBaseURL+"past_webinars/"+webinarId+"/polls";
  
  		try {
  
  			RestTemplate restTemplate = new RestTemplate();
  			HttpEntity<String> entity = new HttpEntity<String>(getZoomAuthenticateHeaders());
  			ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.GET, entity, String.class);
  			JsonObject jsonObject = new JsonParser().parse(response.getBody()).getAsJsonObject();
  			String json_string = new Gson().toJson(jsonObject);
//  			System.out.println("json_string in getWebinarPollsResults(): "+json_string);
  			webinarPollsResultsBean = new Gson().fromJson(json_string, WebinarPollsResultsBean.class);
//  			System.out.println("response json of gettting webinar polls results ::: "+webinarPollsResultsBean);
  
  		} catch (HttpClientErrorException e) {
  			System.out.println(e.getStatusCode());
  			System.out.println(e.getResponseBodyAsString());
  		}
  		return webinarPollsResultsBean;
  
  	}
	
	public String registerForSession(SessionDayTimeAcadsBean session,StudentAcadsBean student,String meetingKey) throws IOException {
		
		switch (session.getSessionType()) {
		case "1":
			return registrantsForWebinar(session, student, meetingKey);
		
		case "2":
			return registrantsForMeeting(session, student, meetingKey);
		}
		return null;
	}
	
	public HashMap<String, String> registerForSessiongByMobile(SessionDayTimeAcadsBean session,StudentAcadsBean student,String webinarID) throws IOException{
		
		switch (session.getSessionType()) {
		case "1":
			return registrantsForWebinarByMobile(session, student, session.getMeetingKey());
		
		case "2":
			return registrantsForMeetingByMobile(session, student, session.getMeetingKey());
		}
		return null;
	}
	
	public void scheduleSessions(SessionDayTimeAcadsBean session) throws IOException{

		switch (session.getSessionType()) {
		case "1":
			scheduleWebinarBatchJob(session);
			break;
		
		case "2":
			scheduleMeetingBatchJob(session);
			break;
		}
	}
	
	public void deleteSession(SessionDayTimeAcadsBean session, String meetingKey) throws IOException{

		switch (session.getSessionType()) {
		case "1":
			deleteWebinar(session, meetingKey);
			break;
		
		case "2":
			deleteMeeting(session, meetingKey);
			break;
		}
	}
	
	public void updateSession(SessionDayTimeAcadsBean session) throws IOException{

		switch (session.getSessionType()) {
		case "1":
			updateWebinarName(session);
			break;
		
		case "2":
			updateMeetingName(session);
			break;
		}
	}
	
	public List<ParticipantReportBean> getWebinarParticipantsDetails(String webinarId) {
		ParticipantsListBean participantListBean=null;
		int i=1;
		String url=null;
		List<ParticipantReportBean> participantsReportBeanList=new ArrayList<ParticipantReportBean>();
		while(i<=15) {
			if(i==1) {
			url=zoomAPIBaseURL+"report/webinars/"+webinarId+"/participants?page_size=300";
			}else {					
					String next_page_token=participantListBean.getNext_page_token();
					if(next_page_token==null || "".equalsIgnoreCase(next_page_token)) {
						break;
					}
					
					url=zoomAPIBaseURL+"report/webinars/"+webinarId+"/participants?page_size=300&next_page_token="+next_page_token;
					
			}
		
			RestTemplate restTemplate = new RestTemplate();
			HttpEntity<String> entity = new HttpEntity<String>(getZoomAuthenticateHeaders());
			ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.GET, entity, String.class);
			JsonObject jsonObject = new JsonParser().parse(response.getBody()).getAsJsonObject();
			Gson gson=new Gson();
			String json_string = gson.toJson(jsonObject);
			participantListBean = new Gson().fromJson(json_string, ParticipantsListBean.class);
			i++;
			participantsReportBeanList.addAll(participantListBean.getParticipants());
		}
		return participantsReportBeanList;
	}
	
	public List<ParticipantReportBean> getMeetingParticipantsDetails(String meetingKey) {
		ParticipantsListBean participantListBean=null;
		int i=1;
		String url=null;
		List<ParticipantReportBean> participantsReportBeanList=new ArrayList<ParticipantReportBean>();
		while(i<=15) {
			if(i==1) {
			url=zoomAPIBaseURL+"report/meetings/"+meetingKey+"/participants?page_size=300";
			}else {					
					String next_page_token=participantListBean.getNext_page_token();
					if(next_page_token==null || "".equalsIgnoreCase(next_page_token)) {
						break;
					}
					
					url=zoomAPIBaseURL+"report/meetings/"+meetingKey+"/participants?page_size=300&next_page_token="+next_page_token;
					
			}
		
			RestTemplate restTemplate = new RestTemplate();
			HttpEntity<String> entity = new HttpEntity<String>(getZoomAuthenticateHeaders());
			ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.GET, entity, String.class);
			JsonObject jsonObject = new JsonParser().parse(response.getBody()).getAsJsonObject();
			Gson gson=new Gson();
			String json_string = gson.toJson(jsonObject);
			participantListBean = new Gson().fromJson(json_string, ParticipantsListBean.class);
			i++;
			participantsReportBeanList.addAll(participantListBean.getParticipants());
		}
		return participantsReportBeanList;
	}
	
}
