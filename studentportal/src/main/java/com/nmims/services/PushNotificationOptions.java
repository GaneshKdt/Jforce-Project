
package com.nmims.services;



import java.util.List;

import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.codehaus.jettison.json.JSONArray;
import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

@Component("pushNotifyDao")
public class PushNotificationOptions {
	
	@Value( "${PUSHNOTIFICATION_API_KEY}" )
	private String PUSHNOTIFICATION_API_KEY ;
	
	@Value( "${PUSHNOTIFICATIONAPP_ID}" )
	private String PUSHNOTIFICATIONAPP_ID ;

    public void sendMessageToPlayerIds(String pushSubject,String pushBody, List<String> player_idList) {
        		 CloseableHttpClient httpClient = HttpClientBuilder.create().build();
			String url = "https://onesignal.com/api/v1/notifications";
			
			try {
				
				HttpHeaders headers =  new HttpHeaders();
				System.out.println("PUSHNOTIFICATION_API_KEY"+PUSHNOTIFICATION_API_KEY);
				headers.add("Content-Type", "application/json; charset=UTF-8");
				headers.add("Authorization", "Basic " +PUSHNOTIFICATION_API_KEY);
				System.out.println("headers::"+headers);
				RestTemplate restTemplate = new RestTemplate();
				
				JSONObject  request = new JSONObject ();
				
				try {
					JSONObject data = new JSONObject();
					data.put("pushSubject", pushSubject);
					
					
					JSONObject contents = new JSONObject();
					contents.put("en", pushBody);
					request.put("key", data);
					request.put("contents", contents);
					//add extra field for sending image
					//request.put("big_picture", "http://academy.dsij.in/portals/8/Images/nimis.jpg"); 
					request.put("app_id", PUSHNOTIFICATIONAPP_ID);
					
					JSONArray array = new JSONArray();
					for (int i = 0; i < player_idList.size(); i++) {
					        array.put(player_idList.get(i));
					}
					request.put("include_player_ids", array);
					System.out.println("request:"+request);
					
				} catch (JSONException e) {
					
					
				}
				
				HttpEntity<String> entity = new HttpEntity<String>(request.toString(), headers);
				
				System.out.println("calling onesignal api...");
				ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.POST, entity, String.class);
				System.out.println("response"+response);
				String statusCode = response.getStatusCode().toString();
				System.out.println("Status Code ::"+statusCode);
				
			}catch (HttpClientErrorException e) {
				
				System.out.println(e.getStatusCode());
			    System.out.println(e.getResponseBodyAsString());
			}
    }
}
