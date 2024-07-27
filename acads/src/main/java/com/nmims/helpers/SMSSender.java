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
import org.json.simple.parser.ParseException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.nmims.beans.FacultyAcadsBean;
import com.nmims.beans.SessionDayTimeAcadsBean;
import com.nmims.beans.SessionQueryAnswer;
import com.nmims.beans.StudentAcadsBean;
import com.nmims.beans.TestAcadsBean;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.net.URLEncoder;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

@Component
public class SMSSender {

	private final String USER_AGENT = "Mozilla/5.0";
	/*
	 * 
	 * @Value( "${SMS_PASSWORD}" ) private String SMS_PASSWORD;
	 * 
	 * @Value( "${SMS_USERNAME}" ) private String SMS_USERNAME;
	 * 
	 * 
	 * @Value( "${SMS_SERVICE_URL}" ) private String SMS_SERVICE_URL;
	 * 
	 * 
	 * @Value( "${SENDER_ID}" ) private String SENDER_ID;
	 * 
	 * 
	 * @Value( "${SMS_FEEDID}" ) private String SMS_FEEDID;
	 */
	@Value("${ACAD_SESSION_REMINDER_NUMBERS}")
	private String ACAD_SESSION_REMINDER_NUMBERS;
	
	@Value("${SESSION_RECORDING_SMS}")
	private String SESSION_RECORDING_SMS;

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
	
	@Autowired
	SubjectAbbreviationHelper subjectAbbreviationHelper;

//	private static final Logger loggerForSessionSMSs = LoggerFactory.getLogger("session_SMS");

	
	/*
	 * public String sendScheduledSessionSMS(SessionDayTimeBean session,
	 * ArrayList<StudentBean> studentList,String message) throws Exception {
	 * 
	 * String numbers = ""; for(StudentBean student : studentList){ String mobile =
	 * student.getMobile(); if(mobile != null && !"".equals(mobile)){ numbers =
	 * numbers +","+mobile; } } numbers = numbers
	 * +","+ACAD_SESSION_REMINDER_NUMBERS;
	 * 
	 * try{ URL obj = new URL(SMS_SERVICE_URL); HttpURLConnection con =
	 * (HttpURLConnection) obj.openConnection();
	 * 
	 * //add request header con.setRequestMethod("POST");
	 * con.setRequestProperty("User-Agent", USER_AGENT);
	 * con.setRequestProperty("Accept-Language", "en-US,en;q=0.5");
	 * 
	 * String urlParameters = "user=NGASCE&password="+SMS_PASSWORD +
	 * "&sid="+SENDER_ID + "&msisdn="+numbers + "&msg="+URLEncoder.encode(message,
	 * "UTF-8") + "&&fl=0&gwid=2";
	 * 
	 * // Send post request con.setDoOutput(true); DataOutputStream wr = new
	 * DataOutputStream(con.getOutputStream()); wr.writeBytes(urlParameters);
	 * wr.flush(); wr.close();
	 * 
	 * int responseCode = con.getResponseCode();
	 *  
	 * BufferedReader in = new BufferedReader( new
	 * InputStreamReader(con.getInputStream())); String inputLine; StringBuffer
	 * response = new StringBuffer();
	 * 
	 * while ((inputLine = in.readLine()) != null) { response.append(inputLine); }
	 * in.close();
	 * 
	 * 
	 * return parseSMSResponse(response.toString()); }catch(Exception e){
	 *    throw e; }
	 * 
	 * }
	 */

	/*
	 * NETCORE SMS public String sendScheduledSessionSMSNetCore(SessionDayTimeBean
	 * session, ArrayList<StudentBean> studentList,String message) throws Exception
	 * {
	 * 
	 * String numbers = ""; for(StudentBean student : studentList){ String mobile =
	 * student.getMobile(); if(mobile != null && !"".equals(mobile)){ numbers =
	 * numbers +","+mobile; } } numbers = numbers
	 * +","+ACAD_SESSION_REMINDER_NUMBERS;
	 * 
	 * try{ URL obj = new URL(SMS_BULK_URL); HttpURLConnection con =
	 * (HttpURLConnection) obj.openConnection();
	 * 
	 * //add request header con.setRequestMethod("POST");
	 * con.setRequestProperty("User-Agent", USER_AGENT);
	 * con.setRequestProperty("Accept-Language", "en-US,en;q=0.5");
	 * 
	 * String urlParameters = "feedid="+SMS_FEEDID + "&username="+SMS_USERNAME +
	 * "&password="+SMS_PASSWORD + "&To="+numbers +
	 * "&Text="+URLEncoder.encode(message, "UTF-8");
	 * 
	 * // Send post request con.setDoOutput(true); DataOutputStream wr = new
	 * DataOutputStream(con.getOutputStream()); wr.writeBytes(urlParameters);
	 * wr.flush(); wr.close();
	 * 
	 * int responseCode = con.getResponseCode();
	 * 
	 * BufferedReader in = new BufferedReader( new
	 * InputStreamReader(con.getInputStream())); String inputLine; StringBuffer
	 * response = new StringBuffer();
	 * 
	 * while ((inputLine = in.readLine()) != null) { response.append(inputLine); }
	 * in.close();
	 * 
	 * 
	 * return parseSMSResponse(response.toString()); }catch(Exception e){
	 *    throw e; }
	 * 
	 * }
	 */

	public String sendScheduledSessionSMSmGage(SessionDayTimeAcadsBean session, ArrayList<StudentAcadsBean> studentList,
			String message, Logger loggerForSessionSMSs) throws Exception {

		loggerForSessionSMSs.info("Inside sendScheduledSessionSMSmGage");
		String numbers = "";
		for (StudentAcadsBean student : studentList) {
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
			
			loggerForSessionSMSs.info("\nSending 'GET' request to URL : " + SMS_BULK_URL);
			loggerForSessionSMSs.info("Post parameters : " + urlParameters);
			loggerForSessionSMSs.info("Response Code : " + responseCode);

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
//			response fully depends on string and isnt xml
//			return parseSMSResponse(response.toString());
		} catch (Exception e) {
			  
			loggerForSessionSMSs.info("Error in sendScheduledSessionSMSmGage "+e.getMessage());
			throw e;
		}

	}
	
	
	
	public String sendRecordingUploadErrorSMS(SessionDayTimeAcadsBean sessionDayTimeBean) throws Exception { 
		
		String numbers = SESSION_RECORDING_SMS;
		String subject = "";
		
		if( sessionDayTimeBean.getSubject().length() > 30 ) {
			subject = subjectAbbreviationHelper.createAbbreviation(sessionDayTimeBean.getSubject());
		}else {
			subject = sessionDayTimeBean.getSubject();
		}

		String message = "Session recording failed to upload for subject: "+ subject +" and meetingId: "+ 
				sessionDayTimeBean.getMeetingId()+" - SVKM's NGASCE";
		
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
//			response fully depends on string and isnt xml
//			return parseSMSResponse(response.toString());
		} catch (Exception e) {
			  
			throw e;
		}

	}

	public String sendScheduledTestSMSmGage(TestAcadsBean test, List<StudentAcadsBean> studentsApllicableForTest,
			String message) throws Exception {

		String numbers = "";
		for (StudentAcadsBean student : studentsApllicableForTest) {
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
//			response fully depends on string and isnt xml
//			return parseSMSResponse(response.toString());
		} catch (Exception e) {
			  
			throw e;
		}

	}

	
	public static String parseSMSResponse(String respJSON) throws Exception {
		JSONObject obj = (JSONObject) new JSONParser().parse(respJSON);
		String output = (String) obj.get("ErrorMessage");
		if ("Success".equalsIgnoreCase(output)) {
			return "OK";
		} else {
			return "ERROR :" + output;
		}

	}

	public static void main(String[] args) throws ParseException {
		try {
			// String url = "http://sms.domainadda.com/vendorsms/pushsms.aspx";
			String url = "http://bulkpush.mytoday.com/BulkSms/SingleMsgApi";
			URL obj = new URL(url);
			HttpURLConnection con = (HttpURLConnection) obj.openConnection();

			// add request header
			con.setRequestMethod("POST");
			con.setRequestProperty("User-Agent", "Mozilla/5.0");
			con.setRequestProperty("Accept-Language", "en-US,en;q=0.5");

			// Solutions Infini
			/*
			 * String urlParameters = "method=sms&" + "api_key="+API_KEY + "&to="+numbers +
			 * "&sender="+SENDER_ID + "&message="+URLEncoder.encode(message, "UTF-8") +
			 * "&format=XML";
			 */
			// String s = "<html><head><title>${title}</head><body><h1>Hello</h1><p>Hello
			// Vikas </p></body></html>";
			String s = "Testing session SMS";
			/*
			 * String urlParameters = "user=&password=Nga_SCEsms@2020" + "&sid=NGASCE" +
			 * "&msisdn=9820834921" + "&msg="+URLEncoder.encode(s.replaceAll("\\<.*?\\>",
			 * ""), "UTF-8") + "&&fl=0&gwid=2";
			 */
			String urlParameters = "feedid=372884" + "&username=8082222940" + "&password=Groupm@2018"
					+ "&To=9819753702,9284211039" + "&Text=" + s;
			// + "&senderid=test sms";

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
			String l = parseSMSResponse(response.toString());

		} catch (Exception e) {
			  

		}

	}
	
	public String sendPostedQuerySMSToFaculty(FacultyAcadsBean faculty, String message) throws Exception {

		String mobile = faculty.getMobile();
		try {
			URL obj = new URL(SMS_BULK_URL);
			HttpURLConnection con = (HttpURLConnection) obj.openConnection();

			// add request header
			con.setRequestMethod("GET");
			con.setRequestProperty("User-Agent", USER_AGENT);
			con.setRequestProperty("Accept-Language", "en-US,en;q=0.5");

			String urlParameters = "ver=" + SMS_VERSION + "&key=" + SMS_KEY + "&encrpt=" + SMS_ENCRYPT + "&dest="
					+ mobile + "&send=" + SMS_SENDERID + "&text=" + URLEncoder.encode(message, "UTF-8");

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
//			response fully depends on string and isnt xml
//			return parseSMSResponse(response.toString());
		} catch (Exception e) {
			  
			throw e;
		}

	}

}
