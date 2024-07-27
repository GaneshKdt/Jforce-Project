package com.nmims.helpers;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;

import org.codehaus.jackson.map.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.nmims.beans.AssignmentStudentPortalFileBean;
import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.WebResource;

import org.apache.commons.codec.binary.Base64;

@Component
public class ExamAppHelper {
	
	/*@Value( "${SERVER_PATH}" )
	private String SERVER_PATH;*/
	
	private final String USER_AGENT = "Mozilla/5.0";
	
	public ArrayList<AssignmentStudentPortalFileBean> getStudentAssignments(String SERVER_PATH, String encryptedId){
		ArrayList<AssignmentStudentPortalFileBean> list = new ArrayList<>();
		try {
			 
			Client client = Client.create();
			//encryptedId = "VXEDr50TxU36E1nZWn65VQ==";
			String url = SERVER_PATH+"exam/getAssignments?uid="+encryptedId;
			//String url = "http://localhost:8080/exam/getAssignments?uid="+encryptedId;
			
			System.out.println("url = "+url);
			WebResource webResource = client.resource(url);
	 
			ClientResponse response = webResource.accept("application/json")
	                   .get(ClientResponse.class);
	 
			if (response.getStatus() != 200) {
			   throw new RuntimeException("Failed : HTTP error code : "
				+ response.getStatus());
			}
	 
			String output = response.getEntity(String.class);
	 
			System.out.println("Output from Server .... \n");
			System.out.println(output);
			
			ObjectMapper mapper = new ObjectMapper();
			list = mapper.readValue(output, mapper.getTypeFactory().constructCollectionType(
					ArrayList.class, AssignmentStudentPortalFileBean.class));
	 
		  } catch (Exception e) {
	 
			
	 
		  }
		
		return list;
	}
	
	
	
	
	public void sendPost(String index) throws Exception {

 
		String body = "<b>This is a test email for auto-email generation</b> <br>. Regard <br> NGA SCE";
		
		
//		String urlParameters = "fromemail=ngasce@nmims.edu&fromname=Sanket Panaskar&tos=[\"wongemailsanketpanskar1234@gmail.com\"]&bccs=[\"sanketpanaskar@gmail.com\","
//				+ "\"jforcesolutions@gmail.com\", "
//				+ "\"sneha.utekar@nmims.edu\", "
//				+ "\"bageshree@nmims.edu\", "
//				+ "\"nandana.narayanan@nmims.edu\", "
//				+ "\"smita.nadkarni@nmims.edu\", "
//				+ "\"abhijit.jadhav@nmims.edu\", "
//				+ "\"rajshree.pawar@nmims.edu\""
//				+ "]"
//				+ "&subject=Test"+index+"&body="+URLEncoder.encode(encodedBody,"UTF-8")+"&apikey=gjyw9u0mm1ot1j65lr8ke1i3q2tbnlttuift3f6e7jxwttr50oeefm8vdrwq2f0loe5gk1kga93trjnrxru34to70an1gmcur8z1xa7x9yhq8u8ubto5c22c3e49r7s8";
		
		EmailHelper emailHelper = new EmailHelper();
		
		HashMap<String, String> payLoad = new HashMap<>();
		payLoad.put("provider", "AWSSES");
		payLoad.put("htmlBody", body);
		payLoad.put("subject", "Test");
		payLoad.put("from", "donotreply-ngasce@nmims.edu");
		payLoad.put("fromName", "NMIMS Global Access SCE");
		emailHelper.sendAmazonSESDynamicMail(payLoad);
		// Send post request
	}
	
	public static void main(String[] args) throws Exception {
		ExamAppHelper examAppHelper = new ExamAppHelper();
		for (int i = 0; i < 5; i++) {
			examAppHelper.sendPost((i+1)+"");
		}
		
	}
	

}
