package com.nmims.helpers;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;

import org.apache.commons.lang3.StringUtils;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

@Component
public class SMSSender {

	private final String USER_AGENT = "Mozilla/5.0";

//	@Value("${EMAILS_SENT_FOLDER}")
//	private String EMAILS_SENT_FOLDER;

	@Value("${SMS_COPY_NUMBERS}")
	private String SMS_COPY_NUMBERS;

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
	
	private static final Logger logger = LoggerFactory.getLogger(SMSSender.class);
	
	@Autowired
	MailSender mailer;
	
	@Async
	public void sendDispatchInitiatedSMS(String mobileNumber, String message)
			throws InterruptedException {
		try {
			String smsStatus = new String();
			
			smsStatus = sendSMS(mobileNumber, message);

			if (!"OK".equals(smsStatus)) 
				mailer.sendSMSDispatchFailureEmail(mobileNumber, message,  smsStatus);
			else
				logger.info("Dispatch SMS successfully send for mobileNumber: {}, messageBody {}, smsBody: {}, smsStatus: {}", 
						mobileNumber, message,  smsStatus);
				
		} catch (Exception e) {
			//e.printStackTrace();
			logger.error("Error in sending Dispatch SMS for mobileNumber: {}, messageBody {}, smsBody: {}, due to : {}", 
					mobileNumber, message,  e);
		}
	}
	
	@Async
	public void sendDeliveredInitiatedSMS(String mobileNumber, String message)
			throws InterruptedException {
		try {
			String smsStatus = new String();
			
			smsStatus = sendSMS(mobileNumber, message);
			
			if (!"OK".equals(smsStatus)) 
				mailer.sendSMSDispatchFailureEmail(mobileNumber, message,  smsStatus);
			else
				logger.info("Delivered SMS successfully send for mobileNumber: {}, messageBody {}, smsStatus: {}", 
						mobileNumber, message,  smsStatus);
			
		} catch (Exception e) {
			e.printStackTrace();
			logger.error("Error in sending Delivered SMS for mobileNumber: {}, messageBody {}, due to : {}", 
					mobileNumber, message,  e);
		}
	}

	public String sendSMS(String mobileNumber, String message) throws Exception {

		String numbers = "";
			
		if (StringUtils.isNotBlank(mobileNumber)) {
			numbers = numbers + "," + mobileNumber;
		}
		numbers = numbers + "," + SMS_COPY_NUMBERS;

		try {
			URL obj = new URL(SMS_BULK_URL);
			HttpURLConnection con = (HttpURLConnection) obj.openConnection();

			// add request header
			con.setRequestMethod("GET");
			con.setRequestProperty("User-Agent", USER_AGENT);
			con.setRequestProperty("Accept-Language", "en-US,en;q=0.5");

			String urlParameters = "ver=" + SMS_VERSION + "&key=" + SMS_KEY + "&encrpt=" + SMS_ENCRYPT + "&dest="
					+ numbers + "&send=" + SMS_SENDERID + "&text=" + URLEncoder.encode(message, "UTF-8");
			System.out.println("Parameters = " + urlParameters);

			// Send post request
			con.setDoOutput(true);
			DataOutputStream wr = new DataOutputStream(con.getOutputStream());
			wr.writeBytes(urlParameters);
			wr.flush();
			wr.close();

			int responseCode = con.getResponseCode();
			System.out.println("\nSending 'GET' request to URL : " + SMS_BULK_URL);
			System.out.println("Post parameters : " + urlParameters);
			System.out.println("Response Code : " + responseCode);

			BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
			String inputLine;
			StringBuffer response = new StringBuffer();

			while ((inputLine = in.readLine()) != null) {
				response.append(inputLine);
			}
			in.close();
			
			System.out.println("sms response is : "+response.toString());
			if(responseCode == 200) {
				logger.info("Successfully send SMS for mobileNmuber : {} and messageBody : {}", mobileNumber, message);
				return "OK";
			}else {
				return parseSMSResponse(response.toString());
			}

		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		}

	}
	
	public static String parseSMSResponse(String respJSON) throws Exception {
		JSONObject obj = (JSONObject) new JSONParser().parse(respJSON);
		String errorMessage = (String) obj.get("ErrorMessage");
		System.out.println("output = " + errorMessage);
		logger.error("error in sending SMS for due to {}", errorMessage);
		return errorMessage; 
	}
}
