package com.nmims.helpers;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.TimeZone;

import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.JsonSyntaxException;
import com.nmims.beans.CounsellingBean;
import com.nmims.beans.InterviewBean;
import com.nmims.beans.SessionDayTimeBean;
import com.nmims.beans.StudentCareerservicesBean;
import com.nmims.beans.ZoomGetJoinURLModelBean;

@Service("zoomManger")
public class ZoomManager {
	
	private final String zoomAPIBaseURL = "https://ngasce.zoom.us/v2/";   
	private final String zoomAPIStartMeetingBaseURL = "https://ngasce.zoom.us/s/";      

	private static final Logger logger = LoggerFactory.getLogger(ZoomManager.class);
 
	public HttpHeaders getWebinarHeaders() {
		HttpHeaders headers =  new HttpHeaders();
		headers.add("Accept", "application/json, application/xml");
		headers.add("Content-Type", "application/json");
		headers.add("Authorization", "Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6Imp3dCJ9.eyJpc3MiOiJrczFPX1BCUFJmTzdxN1duSGE5UWl3IiwiZXhwIjoiMTQ5NjA5MTk2NDAwMCJ9.CvrMLpU36wK1m_M3R7yqtG9I7hXQZ6EC3FQ1eZUSwcU");
		return headers;
	}
	
	public String getStartWebinarLink(String meetingKey , String hostId) {
		RestTemplate restTemplate = new RestTemplate();  
		HttpEntity<String> entity = new HttpEntity<String>(getWebinarHeaders());
		
		JsonObject jsonObj;
		try {
			ResponseEntity<String> apiResponse = restTemplate.exchange(zoomAPIBaseURL + "users/" + hostId + "/token?type=zak", HttpMethod.GET, entity, String.class);
			jsonObj = new JsonParser().parse(apiResponse.getBody()).getAsJsonObject();
			String url = zoomAPIStartMeetingBaseURL +  meetingKey + "?zak=" + jsonObj.get("token").getAsString();
			return url;
		} catch (RestClientException e) {
			// TODO Auto-generated catch block
			logger.info("in getStartWebinarLink got exception : "+e.getMessage());
			return "error";
		} catch (JsonSyntaxException e) {
			// TODO Auto-generated catch block
			logger.info("in getStartWebinarLink got exception : "+e.getMessage());
			return "error";
		}

	}

	public ZoomGetJoinURLModelBean registrantsForWebinar(SessionDayTimeBean session,StudentCareerservicesBean student,String webinarID) throws IOException{
		ZoomGetJoinURLModelBean zoomGetJoinURLModelBean = new ZoomGetJoinURLModelBean();
		CloseableHttpClient httpClient = HttpClientBuilder.create().build();
		String url = zoomAPIBaseURL+"/webinars/"+webinarID+"/registrants";
		String regJoinUrl = null;
		
		try {
			
			RestTemplate restTemplate = new RestTemplate();
			JsonObject request = new JsonObject();
			
			request.addProperty("email", student.getEmailId());
			request.addProperty("first_name", student.getFirstName());
			request.addProperty("last_name", student.getLastName());
			
			HttpEntity<String> entity = new HttpEntity<String>(request.toString(), getWebinarHeaders());	
			ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.POST, entity, String.class);
			JsonObject jsonObj = new JsonParser().parse(response.getBody()).getAsJsonObject();
			
			if ("201".equalsIgnoreCase(response.getStatusCode().toString())) {
				regJoinUrl = jsonObj.get("join_url").getAsString();
				zoomGetJoinURLModelBean.setStatus("success");
				zoomGetJoinURLModelBean.setJoinURL(regJoinUrl);
				zoomGetJoinURLModelBean.setMessage("Join URL generated successfully");
			}else {
				zoomGetJoinURLModelBean.setStatus("fail");
				zoomGetJoinURLModelBean.setMessage("Meeting not found or expired");
			}
			
			
		} catch (HttpClientErrorException e) {
			zoomGetJoinURLModelBean.setStatus("fail");
			zoomGetJoinURLModelBean.setMessage("Meeting url generation failed with status code : " + e.getStatusCode());
			session.setErrorRecord(true);
			session.setErrorMessage(e.getMessage());
		}
		finally {
			httpClient.close();
		}
		return zoomGetJoinURLModelBean;
	}
	
	public void deleteWebinar(SessionDayTimeBean session,String webinarID) throws IOException{
		
		CloseableHttpClient httpClient = HttpClientBuilder.create().build();
		String url = zoomAPIBaseURL+"webinars/"+webinarID;
		
		try {
			
			RestTemplate restTemplate = new RestTemplate();
			HttpEntity<String> entity = new HttpEntity<String>("parameters", getWebinarHeaders());
			
			ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.DELETE, entity, String.class);
			
			String statusCode = response.getStatusCode().toString();

			if (statusCode.equalsIgnoreCase(Integer.toString(204))) {
				session.setErrorRecord(false);
			}
			
			
		} catch (HttpClientErrorException e) {
			session.setErrorRecord(true);
			session.setErrorMessage(e.getStatusText());
		}
		finally {
			httpClient.close();
		}
	}
	
	public void scheduleWebinarBatchJob(SessionDayTimeBean session) throws IOException{
		CloseableHttpClient httpClient = HttpClientBuilder.create().build();
		String url = "https://ngasce.zoom.us/v2/users/"+session.getHostId()+"/webinars";
		
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
		sdf.setTimeZone(TimeZone.getTimeZone("GMT"));
		
		try {
			
			RestTemplate restTemplate = new RestTemplate();
			JsonObject request = new JsonObject();
			
			request.addProperty("topic", session.getSessionName());
			request.addProperty("type", 5);
//			request.addProperty("start_time", sdf.format(session.getDate()));
			request.addProperty("start_time",session.getDate()+"T"+session.getStartTime());
			request.addProperty("duration", 120);
			request.addProperty("timezone", "Asia/Kolkata");
			request.addProperty("password", "");
			request.addProperty("agenda", session.getSessionName());
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
				
			request.add("settings", req);

			HttpEntity<String> entity = new HttpEntity<String>(request.toString(),getWebinarHeaders());
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
			session.setErrorRecord(true);
		   
		}
		
		finally {
			httpClient.close();
		}
	}
	
	public void scheduleInterview(InterviewBean bean) throws IOException{
		CloseableHttpClient httpClient = HttpClientBuilder.create().build();
		String url = "https://ngasce.zoom.us/v2/users/"+bean.getHostId()+"/meetings";
		
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
		sdf.setTimeZone(TimeZone.getTimeZone("GMT"));
		
		try {
			
			RestTemplate restTemplate = new RestTemplate();
			JsonObject request = new JsonObject();
			
			request.addProperty("topic", "Interview");
			request.addProperty("type", 2);
			request.addProperty("start_time",bean.getDate()+"T"+bean.getStartTime());
			request.addProperty("duration", 60);
			request.addProperty("timezone", "Asia/Kolkata");
			request.addProperty("password", "");
			request.addProperty("agenda", "Interview Meeting");
			/* Settings  */
			
			JsonObject req = new JsonObject();
			req.addProperty("host_video", true);
			req.addProperty("participant_video", true);
		    req.addProperty("in_meeting", true);
		    req.addProperty("join_before_host", true);
			req.addProperty("approval_type", 0);
			req.addProperty("registration_type", 2);
			req.addProperty("audio", "viop");
			req.addProperty("auto_recording", "cloud");
			req.addProperty("enforce_login", false);
			req.addProperty("enforce_login_domains", "");
				
			request.add("settings", req);

			HttpEntity<String> entity = new HttpEntity<String>(request.toString(),getWebinarHeaders());
			ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.POST, entity, String.class);
			JsonObject jsonObj = new JsonParser().parse(response.getBody()).getAsJsonObject();
			
			if ("201".equalsIgnoreCase(response.getStatusCode().toString())) {
				bean.setErrorRecord(false);
				String hostJoinUrl = jsonObj.get("start_url").getAsString();
				String studentJoinUrl = jsonObj.get("join_url").getAsString();
				String webinarID = jsonObj.get("id").getAsString();
				bean.setMeetingKey(webinarID);
				bean.setJoinUrl(studentJoinUrl);
				bean.setHostUrl(hostJoinUrl);
				
			}
			
		} catch (HttpClientErrorException e) {
			bean.setErrorRecord(true);
			logger.info("in getStartWebinarLink got exception : "+e.getMessage());
		   
		}
		
		finally {
			httpClient.close();
		}
	}
	
	public void cancelInterview(InterviewBean bean) throws IOException{

		CloseableHttpClient httpClient = HttpClientBuilder.create().build();
		String url = zoomAPIBaseURL+"meetings/"+bean.getMeetingKey();

		try {

			RestTemplate restTemplate = new RestTemplate();
			HttpEntity<String> entity = new HttpEntity<String>("parameters", getWebinarHeaders());

			ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.DELETE, entity, String.class);

			String statusCode = response.getStatusCode().toString();
			
			if (statusCode.equalsIgnoreCase(Integer.toString(204))) {
				logger.info("Zoom Webinar Deleted Successfully... ");
				bean.setErrorRecord(false);
			}


		} catch (HttpClientErrorException e) {
			bean.setErrorRecord(true);
			bean.setErrorMessage(e.getStatusText());
			logger.info("in cancelInterview got exception : "+e.getMessage());
		}
		finally {
			httpClient.close();
		}
	}
	
	public void rescheduleInterview(InterviewBean bean,String meetingId) throws IOException{

		CloseableHttpClient httpClient = HttpClientBuilder.create().build();
		String url = zoomAPIBaseURL+"meetings/"+meetingId;

		try {

			RestTemplate restTemplate = new RestTemplate();
			HttpEntity<String> entity = new HttpEntity<String>("parameters", getWebinarHeaders());

			ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.DELETE, entity, String.class);

			String statusCode = response.getStatusCode().toString();

			if (statusCode.equalsIgnoreCase(Integer.toString(204))) {
				bean.setErrorRecord(false);
			}


		} catch (HttpClientErrorException e) {
			bean.setErrorRecord(true);
			bean.setErrorMessage(e.getStatusText());
			logger.info("in rescheduleInterview got exception : "+e.getMessage());
		}
		finally {
			httpClient.close();
		}
	}
	
	public void scheduleCounselling(CounsellingBean bean) throws IOException{
		CloseableHttpClient httpClient = HttpClientBuilder.create().build();
		String url = "https://ngasce.zoom.us/v2/users/"+bean.getHostId()+"/meetings";
		
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
		sdf.setTimeZone(TimeZone.getTimeZone("GMT"));
		
		try {
			
			RestTemplate restTemplate = new RestTemplate();
			JsonObject request = new JsonObject();
			
			request.addProperty("topic", "Counselling");
			request.addProperty("type", 2);
			request.addProperty("start_time",bean.getDate()+"T"+bean.getStartTime());
			request.addProperty("duration", 60);
			request.addProperty("timezone", "Asia/Kolkata");
			request.addProperty("password", "");
			request.addProperty("agenda", "Counselling Meeting");
			/* Settings  */
			
			JsonObject req = new JsonObject();
			req.addProperty("host_video", true);
			req.addProperty("participant_video", true);
		    req.addProperty("in_meeting", true);
		    req.addProperty("join_before_host", true);
			req.addProperty("approval_type", 0);
			req.addProperty("registration_type", 2);
			req.addProperty("audio", "viop");
			req.addProperty("auto_recording", "cloud");
			req.addProperty("enforce_login", false);
			req.addProperty("enforce_login_domains", "");
				
			request.add("settings", req);

			HttpEntity<String> entity = new HttpEntity<String>(request.toString(),getWebinarHeaders());
			ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.POST, entity, String.class);
			JsonObject jsonObj = new JsonParser().parse(response.getBody()).getAsJsonObject();
			
			if ("201".equalsIgnoreCase(response.getStatusCode().toString())) {
				bean.setErrorRecord(false);
				String hostJoinUrl = jsonObj.get("start_url").getAsString();
				String studentJoinUrl = jsonObj.get("join_url").getAsString();
				String webinarID = jsonObj.get("id").getAsString();
				bean.setMeetingKey(webinarID);
				bean.setJoinUrl(studentJoinUrl);
				bean.setHostUrl(hostJoinUrl);
				
			}
			
		} catch (HttpClientErrorException e) {
			bean.setErrorRecord(true);
			logger.info("in scheduleCounselling got exception : "+e.getMessage());
		   
		}
		
		finally {
			httpClient.close();
		}
	}
	
	public void cancelCounselling(CounsellingBean bean) throws IOException{

		CloseableHttpClient httpClient = HttpClientBuilder.create().build();
		String url = zoomAPIBaseURL+"meetings/"+bean.getMeetingKey();

		try {

			RestTemplate restTemplate = new RestTemplate();
			HttpEntity<String> entity = new HttpEntity<String>("parameters", getWebinarHeaders());

			ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.DELETE, entity, String.class);

			String statusCode = response.getStatusCode().toString();

			if (statusCode.equalsIgnoreCase(Integer.toString(204))) {
				bean.setErrorRecord(false);
			}


		} catch (HttpClientErrorException e) {
			bean.setErrorRecord(true);
			bean.setErrorMessage(e.getStatusText());
			logger.info("in cancelCounselling got exception : "+e.getMessage());
		}
		finally {
			httpClient.close();
		}
	}
	
	public void rescheduleCounselling(CounsellingBean bean,String meetingId) throws IOException{

		CloseableHttpClient httpClient = HttpClientBuilder.create().build();
		String url = zoomAPIBaseURL+"meetings/"+meetingId;

		try {

			RestTemplate restTemplate = new RestTemplate();
			HttpEntity<String> entity = new HttpEntity<String>("parameters", getWebinarHeaders());

			ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.DELETE, entity, String.class);

			String statusCode = response.getStatusCode().toString();

			if (statusCode.equalsIgnoreCase(Integer.toString(204))) {
				bean.setErrorRecord(false);
			}


		} catch (HttpClientErrorException e) {
			bean.setErrorRecord(true);
			bean.setErrorMessage(e.getStatusText());
			logger.info("in rescheduleCounselling got exception : "+e.getMessage());
		}
		finally {
			httpClient.close();
		}
	}
}
