package com.nmims.helpers;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

public class RestCallHelper {

	//helper function
	public ResponseEntity<String> getResponse(String params, String url){
		// set headers
		
		
		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_JSON);
		HttpEntity<String> entity = new HttpEntity<String>(params, headers);

		// send request to salesforce
		RestTemplate restTemplate = new RestTemplate();
		ResponseEntity<String> response = restTemplate
		  .exchange(url, HttpMethod.POST, entity, String.class);

		return response;
	}
}
