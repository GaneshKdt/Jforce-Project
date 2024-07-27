package com.nmims.helpers;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.nmims.beans.SessionDayTimeBean;
import com.nmims.beans.StudentCareerservicesBean;


@Component
public class SMSSender {

	private static final Logger logger = LoggerFactory.getLogger(SMSSender.class);
 
//
//	private final String USER_AGENT = "Mozilla/5.0";
//	
//	@Value( "${SMS_PASSWORD}" )
//	private String SMS_PASSWORD;
//	
//	@Value( "${SMS_USERNAME}" )
//	private String SMS_USERNAME; 
//	
//	@Value( "${SMS_BULK_URL}" )
//	private String SMS_BULK_URL;
//	
//	@Value( "${SMS_FEEDID}" )
//	private String SMS_FEEDID;
//	
//	@Value( "${ACAD_SESSION_REMINDER_NUMBERS}" )
//	private String ACAD_SESSION_REMINDER_NUMBERS;
//
//	public String sendScheduledSessionSMSNetCore(SessionDayTimeBean session, List<StudentBean> studentList,String message) throws Exception {
//
//		String numbers = "";
//		for(StudentBean student : studentList){
//			String mobile = student.getMobile();
//			if(mobile != null && !"".equals(mobile)){
//				numbers = numbers +","+mobile;
//			}
//		}
////		numbers = numbers +","+ACAD_SESSION_REMINDER_NUMBERS;
//
//		try{
//			URL obj = new URL(SMS_BULK_URL);
//			HttpURLConnection con = (HttpURLConnection) obj.openConnection();
//
//			//add request header
//			con.setRequestMethod("POST");
//			con.setRequestProperty("User-Agent", USER_AGENT);
//			con.setRequestProperty("Accept-Language", "en-US,en;q=0.5");
//
//			String urlParameters = "feedid="+SMS_FEEDID
//					+ "&username="+SMS_USERNAME
//					+ "&password="+SMS_PASSWORD
//					+ "&To="+numbers
//					+ "&Text="+URLEncoder.encode(message, "UTF-8");
//			System.out.println("Parameters = "+urlParameters);
//
//			// Send post request
//			con.setDoOutput(true);
//			DataOutputStream wr = new DataOutputStream(con.getOutputStream());
//			wr.writeBytes(urlParameters);
//			wr.flush();
//			wr.close();
//
//			int responseCode = con.getResponseCode();
//			System.out.println("\nSending 'POST' request to URL : " + SMS_BULK_URL);
//			System.out.println("Post parameters : " + urlParameters);
//			System.out.println("Response Code : " + responseCode);
//
//			BufferedReader in = new BufferedReader(
//					new InputStreamReader(con.getInputStream()));
//			String inputLine;
//			StringBuffer response = new StringBuffer();
//
//			while ((inputLine = in.readLine()) != null) {
//				response.append(inputLine);
//			}
//			in.close();
//
//			//print result
//			System.out.println(response.toString());
//
//			return parseSMSResponse(response.toString());
//		}catch(Exception e){
//			logger.info("exception : "+e.getMessage());
//			throw e;
//		}
//
//	}
//	
//	public static String parseSMSResponse(String respJSON) throws Exception{
//		JSONObject  obj = (JSONObject)new JSONParser().parse(respJSON);
//		String output = (String) obj.get("ErrorMessage");
//		System.out.println("output = "+output);
//		if("Success".equalsIgnoreCase(output)){
//			return "OK";
//		}else{
//			return "ERROR :"+output;
//		}
//	}

	private final String USER_AGENT = "Mozilla/5.0";
	
	@Value("${ACAD_SESSION_REMINDER_NUMBERS}")
	private String ACAD_SESSION_REMINDER_NUMBERS;

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


	public String sendScheduledSessionSMSmGage(SessionDayTimeBean session, List<StudentCareerservicesBean> studentList,
			String message) throws Exception {

		String numbers = "";
		for (StudentCareerservicesBean student : studentList) {
			String mobile = student.getMobile();
			if (mobile != null && !"".equals(mobile)) {
				numbers = numbers + "," + mobile;
			}
		}
		numbers = numbers + "," + ACAD_SESSION_REMINDER_NUMBERS;
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

			if(responseCode == 200) {
				return "OK";
			}else {
				return response.toString();
			}
		} catch (Exception e) {
			logger.info("exception : "+e.getMessage());
			throw e;
		}

	}
//
//	public static String parseSMSResponse(String respJSON) throws Exception {
//		JSONObject obj = (JSONObject) new JSONParser().parse(respJSON);
//		String output = (String) obj.get("ErrorMessage");
//		System.out.println("output = " + output);
//		if ("Success".equalsIgnoreCase(output)) {
//			return "OK";
//		} else {
//			return "ERROR :" + output;
//		}
//
//	}

}
