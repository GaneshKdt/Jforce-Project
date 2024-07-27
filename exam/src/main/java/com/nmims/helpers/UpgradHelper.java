package com.nmims.helpers;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpStatus;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.json.simple.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.nmims.beans.ResponseBean;
import com.nmims.beans.UpgradAssessmentExamBean;

@Component
public class UpgradHelper {
	@Value( "${AUTH_TOKEN_FOR_UPGRAD}" )
	private String AUTH_TOKEN_FOR_UPGRAD;
	

	public JsonObject postDataToUpgrad(String parametersFromApi,String apiPath, String courseId, String sessionId) {
		
		CloseableHttpClient client = HttpClientBuilder.create().build();
		
		String response = new String();
		JsonObject responseJsonObject = new JsonObject();
		String url = apiPath;
		try {
			
			HttpHeaders headers = new HttpHeaders();
			headers.set("Content-Type", "application/json");
			headers.set("auth-token", AUTH_TOKEN_FOR_UPGRAD);
			headers.set("courseId", courseId);
			headers.set("sessionId", sessionId);
			
			RestTemplate restTemplate = new RestTemplate();
			HttpEntity<String> request = new HttpEntity<>(parametersFromApi, headers);
			response = restTemplate.postForObject(url, request, String.class);
			responseJsonObject = new JsonParser().parse(response).getAsJsonObject();
			

			
			
			
		}catch(Exception e) {
				
				throw e;
			}
		finally{
			     //Important: Close the connect
				 try {
					client.close();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					
				}
			 }
		return responseJsonObject;
			
	}
}
