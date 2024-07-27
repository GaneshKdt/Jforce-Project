package com.nmims.helpers;

import java.io.IOException;

import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.nmims.beans.SessionDayTimeAcadsBean;
import com.nmims.daos.TimeTableDAO;

public class VimeoManager {	
	
	
	
	private HttpHeaders getHeaders() {
		HttpHeaders headers =  new HttpHeaders();
		headers.add("Accept", "application/vnd.vimeo.*+json;version=3.4");
		headers.add("Content-Type", "application/json");
		headers.add("Authorization", "Bearer ad0be361d65db2ba9de7a04c1e99eb44");
		return headers;
	}
	
	public JsonObject checkUploadVideoStatus(String vimeoId) throws IOException {
		//CloseableHttpClient httpClient = null;
		String url = "https://api.vimeo.com/me/videos/" + vimeoId;
		try {
			//httpClient = HttpClientBuilder.create().build();
			HttpHeaders headers =  this.getHeaders();	//get zoom required header
			
			RestTemplate restTemplate = new RestTemplate();
			
			//JsonObject request = new JsonObject();
			
			HttpEntity<String> entity = new HttpEntity<String>("",headers);
			ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.GET, entity, String.class);
			JsonObject jsonResponse = new JsonParser().parse(response.getBody()).getAsJsonObject();
			if ("200".equalsIgnoreCase(response.getStatusCode().toString())) {
				//String status = jsonResponse.get("status").getAsString();
				return jsonResponse;
			}
		}
		catch (Exception e) {
			  
			//sessions.updateStatus(sessionType,"error", e.getMessage(),date,webinarId,null);
		}
	
		return null;
	}
	
	
	public void uploadVideo(TimeTableDAO recordingStatusDAO,SessionDayTimeAcadsBean sessionDayTimeBean,String videoUrl) throws IOException {
		String videoName = sessionDayTimeBean.getSubject() + "-" + sessionDayTimeBean.getSessionName();
		
		//check added as vimeo character limit on video title
		if(videoName.length()>110) {
			videoName = sessionDayTimeBean.getSubject()+sessionDayTimeBean.getSessionName().substring(0, 10);
		}
		
		String meetingId = sessionDayTimeBean.getMeetingKey();
		CloseableHttpClient httpClient = null;
		String url = "https://api.vimeo.com/me/videos";
		try {
			httpClient = HttpClientBuilder.create().build();
			HttpHeaders headers =  this.getHeaders();	//get zoom required header
			
			RestTemplate restTemplate = new RestTemplate();
			
			JsonObject request = new JsonObject();
			JsonObject req = new JsonObject();
			req.addProperty("approach", "pull");
			req.addProperty("link", videoUrl); 
			request.add("upload", req);
			request.addProperty("name", videoName.trim() + "-" +meetingId);
			
			HttpEntity<String> entity = new HttpEntity<String>(request.toString(),headers);
			ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.POST, entity, String.class);
			JsonObject jsonResponse = new JsonParser().parse(response.getBody()).getAsJsonObject();
			if ("201".equalsIgnoreCase(response.getStatusCode().toString())) {
				String uri = jsonResponse.get("uri").getAsString();
				String[] uriArray = uri.split("/"); 
				recordingStatusDAO.pendingRecordingStatus(meetingId,uriArray[2]);
			}else {
				recordingStatusDAO.errorRecordingStatus(meetingId, "Vimeo Reponse Code : " + response.getStatusCode().toString());
			}
		}
		catch (Exception e) {
			  
			recordingStatusDAO.errorRecordingStatus(meetingId, "Error: " + e.getMessage());
		}
		finally {
			httpClient.close();
		}
	}
	
	public void sendErrorNotification(ApplicationContext act,SessionDayTimeAcadsBean sessionDayTimeBean,String errorMessage) {
		try {
			TimeTableDAO recordingStatusDAO = (TimeTableDAO)act.getBean("timeTableDAO");
			MailSender mailSender = (MailSender) act.getBean("mailer");
			SMSSender smsSender = (SMSSender)act.getBean("SMSSender");
			if(sessionDayTimeBean.getMeetingId() == null) {
				sessionDayTimeBean.setMeetingId(sessionDayTimeBean.getMeetingKey());
			}
			mailSender.sendRecordingUploadErrorEmail(sessionDayTimeBean, errorMessage);	
			smsSender.sendRecordingUploadErrorSMS(sessionDayTimeBean);
			recordingStatusDAO.updateIsNotificationSendFlag(sessionDayTimeBean);
		}
		catch (Exception e) {
			  
		}
	}
	


	public String getUploadLinkfromUri(String uri,String vttName) throws IOException {
		CloseableHttpClient httpClient = null;
		String responseString = null;
		try {
			httpClient = HttpClientBuilder.create().build();
			HttpHeaders headers =  this.getHeaders();	
			RestTemplate restTemplate = new RestTemplate();
			
			JsonObject request = new JsonObject();						
			request.addProperty("type", "subtitles");
			request.addProperty("language", "en");
			request.addProperty("name", vttName);					
			
			HttpEntity<String> entity = new HttpEntity<String>(request.toString(), headers);
			
			ResponseEntity<String> response = restTemplate.exchange(uri, HttpMethod.POST, entity, String.class);			
			String statusCode = response.getStatusCode().toString();
			if(!"201".equals(statusCode) && !"200".equals(statusCode)) {
				return null;
			}
			JsonObject jsonResponse = new JsonParser().parse(response.getBody()).getAsJsonObject();
			responseString =  jsonResponse.get("link").getAsString();
			
		} catch (HttpClientErrorException e) {			
			  
		}
		finally { 
			httpClient.close();
		} 
		return responseString;
	}

	public boolean putSubtitleinVideo(String uploadLink,String vttContent) throws IOException {
		CloseableHttpClient httpClient = null;
		try {
			httpClient = HttpClientBuilder.create().build();
			HttpHeaders headers =  new HttpHeaders();	
			headers.add("Accept", "application/vnd.vimeo.*+json;version=3.4");
			RestTemplate restTemplate = new RestTemplate();			
			
			HttpEntity<String> entity = new HttpEntity<String>(vttContent, headers);
		
			ResponseEntity<String> response = restTemplate.exchange(uploadLink, HttpMethod.PUT, entity, String.class);			
			String statusCode = response.getStatusCode().toString();
			if("200".equals(statusCode) || "201".equals(statusCode)) {
				return true;
			}
			
		} catch (HttpClientErrorException e) {			
			  
		}
		finally { 
			httpClient.close();
		} 
		return false;
		
	}
	
	public boolean setTextTrackActive(String uri) throws IOException {
		CloseableHttpClient httpClient = null;
		//String responseString = null;
		try {
			httpClient = HttpClientBuilder.create().build();
			HttpHeaders headers =  this.getHeaders();	
			RestTemplate restTemplate = new RestTemplate();
			
			JsonObject request = new JsonObject();						
			request.addProperty("active", "true");
			//request.addProperty("language", "en");					
			
			HttpEntity<String> entity = new HttpEntity<String>(request.toString(), headers);
			
			ResponseEntity<String> response =restTemplate.exchange(uri, HttpMethod.POST, entity, String.class);
			String statusCode = response.getStatusCode().toString();
			if("200".equals(statusCode) || "201".equals(statusCode)) {
				return true;
			}
			/*JsonObject jsonResponse = new JsonParser().parse(response.getBody()).getAsJsonObject();
			return true;*/
			
		} catch (HttpClientErrorException e) {			
			  
		}
		finally { 
			httpClient.close();
		} 
		return false;
	}

	public String getTranscriptLinkByUploadLinkUrl(String url) {		
		String transcriptLink = null;
		try {					
			HttpHeaders headers =  this.getHeaders();	//get zoom required header					
			RestTemplate restTemplate = new RestTemplate();					
			
			HttpEntity<String> entity = new HttpEntity<String>("",headers);
			ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.GET, entity, String.class);
			JsonObject jsonObj = new JsonParser().parse(response.getBody()).getAsJsonObject();
			if ("200".equalsIgnoreCase(response.getStatusCode().toString())) {
				JsonArray dataArray = jsonObj.get("data").getAsJsonArray();
				if(dataArray.size() > 0) {
					JsonElement dataElement = jsonObj.get("data").getAsJsonArray().get(0);
					JsonObject data = dataElement.getAsJsonObject();
					transcriptLink =  data.get("link").getAsString();
				}
			}
		}catch (Exception e) {
			  
		}				
		return transcriptLink;
	}
}

