package com.nmims.helpers;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedHashSet;
import java.util.List;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.lang.RandomStringUtils;
import org.apache.http.NameValuePair;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.apache.http.message.BasicNameValuePair;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.jsoup.Jsoup;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

import com.nmims.beans.LeadStudentPortalBean;
import com.nmims.beans.MailStudentPortalBean;
import com.nmims.beans.SessionDayTimeStudentPortal;
import com.nmims.beans.StudentStudentPortalBean;
import com.nmims.beans.VerifyContactDetailsBean;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
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
	 * @Value( "${SMS_PASSWORD}" ) private String SMS_PASSWORD;
	 * 
	 * @Value( "${SMS_USERNAME}" ) private String SMS_USERNAME;
	 * 
	 * @Value( "${SMS_SERVICE_URL}" ) private String SMS_SERVICE_URL;
	 * 
	 * @Value( "${SENDER_ID}" ) private String SENDER_ID;
	 * 
	 * @Value("${SMS_FEEDID}") private String SMS_FEEDID;
	 * 
	 * 
	 */

	@Value("${EMAILS_SENT_FOLDER}")
	private String EMAILS_SENT_FOLDER;

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

	@Autowired
	MailSender mailer;
	@Async
	public void sendRequestOTP(VerifyContactDetailsBean verifyBean) {
		String numbers = verifyBean.getMobile();
		String message = verifyBean.getOtp() + " is your OTP to verify your mobile number at SVKM's NMIMS Global Access. "
				+ "OTP is confidential and valid for 10 minutes";
//		if("android".equalsIgnoreCase(lead.getDeviceType())) {
//			message = "<#> " + lead.getOtp() + " is your ngasce verification code. No3/in9GPUA";
//		}

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

			// print result
			System.out.println(response.toString());
		} catch (Exception e) {
			e.printStackTrace();
		}

	}
	@Async
	public void sendMassSMS(ArrayList<StudentStudentPortalBean> studentList, String message, String userId, String criteria)
			throws InterruptedException {
		ArrayList<MailStudentPortalBean> mailList = new ArrayList<MailStudentPortalBean>();
		try {

			System.out.println("SMS Sender: List OF SAPID size -->" + studentList.size());

			for (int i = 0; i < studentList.size(); i = i + 2000) {
				int lastIndex = (studentList.size() < i + 2000 ? studentList.size() : i + 2000);
				MailStudentPortalBean mailBean = new MailStudentPortalBean();
				String smsStatus = "";
				System.out.println("Sending SMS from " + i + " to " + lastIndex);
				List<StudentStudentPortalBean> studentSubList = (List<StudentStudentPortalBean>) studentList.subList(i, lastIndex);
				smsStatus = sendSMS(message, studentSubList);
				System.out.println("smsStatus STATUS -->" + smsStatus);

				mailBean.setBody(message);
				mailBean.setFilterCriteria(criteria);
				mailBean.setStudents(studentSubList);
				mailList.add(mailBean);

				if (!"Success".equals(smsStatus)) {
					// Intimate sender.
					mailer.sendSMSDeliveryFailureEmail(mailBean, smsStatus);
				}
			}
			System.out.println("SMS Sender:MAIL LIST-->" + mailList.size());

			writeSMSAuditTrailToFile(studentList, message, userId, criteria);

		} catch (Exception e) {
			//e.printStackTrace();
		}
	}

	@Async
	private void writeSMSAuditTrailToFile(ArrayList<StudentStudentPortalBean> studentList, String message, String userId,
			String criteria) {
		try {

			String numbers = "";
			String sapIds = "";
			for (StudentStudentPortalBean student : studentList) {
				numbers = numbers + "," + student.getMobile();
				sapIds = sapIds + student.getSapid();
			}

			String todayAsString = new SimpleDateFormat("ddMMyyyy_HHmmss").format(new Date());
			String fileName = "SMS_" + todayAsString + "_" + userId + "_" + RandomStringUtils.randomAlphanumeric(10)
					+ ".txt";
			File file = new File(EMAILS_SENT_FOLDER + "/" + fileName);

			// if file doesnt exists, then create it
			if (!file.exists()) {
				file.createNewFile();
			}

			FileWriter fw = new FileWriter(file.getAbsoluteFile());
			BufferedWriter bw = new BufferedWriter(fw);

			bw.write("Time: " + todayAsString);
			bw.write(System.lineSeparator());

			bw.write("Sender User Id: " + userId);
			bw.write(System.lineSeparator());
			bw.write(System.lineSeparator());

			bw.write("SMS Group Criteria: ");
			bw.write(System.lineSeparator());
			bw.write(criteria);
			bw.write(System.lineSeparator());
			bw.write(System.lineSeparator());

			bw.write("SMS Content: ");
			bw.write(System.lineSeparator());
			bw.write(message);
			bw.write(System.lineSeparator());
			bw.write(System.lineSeparator());

			bw.write("Recipients Students: ");
			bw.write(System.lineSeparator());
			bw.write(sapIds.toString());
			bw.write(System.lineSeparator());

			bw.write("Recipients Numbers: ");
			bw.write(System.lineSeparator());
			bw.write(numbers.toString());
			bw.write(System.lineSeparator());

			bw.close();

			System.out.println("Done");

		} catch (IOException e) {
			//e.printStackTrace();
		}

	}

	/* request mapping not used
	 * @Async public String sendGroupSMS(CloseableHttpClient client,
	 * List<StudentBean> studentList, String message) {
	 */
	/*
	 * String SMSStatus = ""; try { HttpPost post = new HttpPost(SMS_SERVICE_URL);
	 * post.setHeader("User-Agent", USER_AGENT);
	 * 
	 * String numbers = ""; for(StudentBean student : studentList){ String mobile =
	 * student.getMobile(); if(mobile != null && !"".equals(mobile)){ numbers =
	 * numbers +","+mobile; } } numbers = numbers + ","+SMS_COPY_NUMBERS;
	 * 
	 * 
	 * List<NameValuePair> urlParameters = new ArrayList<NameValuePair>();
	 * urlParameters.add(new BasicNameValuePair("user", "NGASCE"));
	 * urlParameters.add(new BasicNameValuePair("password", SMS_PASSWORD));
	 * urlParameters.add(new BasicNameValuePair("sid", SENDER_ID)); //Needs to be in
	 * JSON form only. urlParameters.add(new BasicNameValuePair("msisdn", numbers));
	 * urlParameters.add(new BasicNameValuePair("msg", message));
	 * urlParameters.add(new BasicNameValuePair("fl", "0")); urlParameters.add(new
	 * BasicNameValuePair("gwid", "2"));
	 * 
	 * post.setEntity(new UrlEncodedFormEntity(urlParameters));
	 * CloseableHttpResponse response = client.execute(post);
	 * 
	 * System.out.println("\nSending 'POST' request to URL : " + SMS_SERVICE_URL);
	 * System.out.println("Post parameters : " + post.getEntity());
	 * System.out.println("Response Code : " +
	 * response.getStatusLine().getStatusCode());
	 * 
	 * SMSStatus = parseSMSResponse(response.toString());
	 * System.out.println("STATUS PARAMETER "+SMSStatus);
	 * 
	 * response.close();
	 * 
	 * } catch (Exception e) { e.printStackTrace(); } return SMSStatus;
	 */
		
		
	/* latest code
	 * String SMSStatus = ""; try { HttpPost post = new HttpPost(SMS_BULK_URL);
	 * post.setHeader("User-Agent", USER_AGENT);
	 * 
	 * String numbers = ""; for (StudentBean student : studentList) { String mobile
	 * = student.getMobile(); if (mobile != null && !"".equals(mobile)) { numbers =
	 * numbers + "," + mobile; } } numbers = numbers + "," + SMS_COPY_NUMBERS;
	 * 
	 * List<NameValuePair> urlParameters = new ArrayList<NameValuePair>();
	 * urlParameters.add(new BasicNameValuePair("feedid", SMS_FEEDID));
	 * urlParameters.add(new BasicNameValuePair("username", SMS_USERNAME));
	 * urlParameters.add(new BasicNameValuePair("password", SMS_PASSWORD)); //
	 * urlParameters.add(new BasicNameValuePair("sid", SENDER_ID)); //Needs to be in
	 * // JSON form only. urlParameters.add(new BasicNameValuePair("To", numbers));
	 * urlParameters.add(new BasicNameValuePair("Text", message));
	 * 
	 * urlParameters.add(new BasicNameValuePair("fl", "0")); urlParameters.add(new
	 * BasicNameValuePair("gwid", "2"));
	 * 
	 * System.out.println("Parameters = " + urlParameters);
	 * 
	 * post.setEntity(new UrlEncodedFormEntity(urlParameters));
	 * CloseableHttpResponse response = client.execute(post);
	 * 
	 * System.out.println("\nSending 'POST' request to URL : " + SMS_BULK_URL);
	 * System.out.println("Post parameters : " + post.getEntity());
	 * System.out.println("Response Code : " +
	 * response.getStatusLine().getStatusCode());
	 * 
	 * SMSStatus = parseSMSResponse(response.toString());
	 * System.out.println("STATUS PARAMETER " + SMSStatus);
	 * 
	 * response.close();
	 * 
	 * } catch (Exception e) { e.printStackTrace(); } return SMSStatus; }
	 */

	public String sendSMS(String message, List<StudentStudentPortalBean> studentList) throws Exception {

		/*
		 * String numbers = "9920726538"; for(StudentBean student : studentList){ String
		 * mobile = student.getMobile(); if(mobile != null && !"".equals(mobile)){
		 * numbers = numbers +","+mobile; } } numbers = numbers + ","+SMS_COPY_NUMBERS;
		 * 
		 * try{ URL obj = new URL(SMS_SERVICE_URL); HttpURLConnection con =
		 * (HttpURLConnection) obj.openConnection();
		 * 
		 * //add request header con.setRequestMethod("POST");
		 * con.setRequestProperty("User-Agent", USER_AGENT);
		 * con.setRequestProperty("Accept-Language", "en-US,en;q=0.5");
		 * 
		 * 
		 * String urlParameters = "user=NGASCE&password="+SMS_PASSWORD +
		 * "&sid="+SENDER_ID + "&msisdn="+numbers + "&msg="+URLEncoder.encode(message,
		 * "UTF-8") + "&&fl=0&gwid=2";
		 * 
		 * 
		 * // Send post request con.setDoOutput(true); DataOutputStream wr = new
		 * DataOutputStream(con.getOutputStream()); wr.writeBytes(urlParameters);
		 * wr.flush(); wr.close();
		 * 
		 * int responseCode = con.getResponseCode();
		 * System.out.println("\nSending 'POST' request to URL : " + SMS_SERVICE_URL);
		 * System.out.println("Post parameters : " + urlParameters);
		 * System.out.println("Response Code : " + responseCode);
		 * 
		 * BufferedReader in = new BufferedReader( new
		 * InputStreamReader(con.getInputStream())); String inputLine; StringBuffer
		 * response = new StringBuffer();
		 * 
		 * while ((inputLine = in.readLine()) != null) { response.append(inputLine); }
		 * in.close();
		 * 
		 * //print result System.out.println(response.toString());
		 * 
		 * return parseSMSResponse(response.toString()); }catch(Exception e){
		 * e.printStackTrace(); throw e; }
		 */
		/*
		 * String numbers = "9920726538"; for (StudentBean student : studentList) {
		 * String mobile = student.getMobile(); if (mobile != null &&
		 * !"".equals(mobile)) { numbers = numbers + "," + mobile; } } numbers = numbers
		 * + "," + SMS_COPY_NUMBERS;
		 * 
		 * try { URL obj = new URL(SMS_BULK_URL); HttpURLConnection con =
		 * (HttpURLConnection) obj.openConnection();
		 * 
		 * // add request header con.setRequestMethod("POST");
		 * con.setRequestProperty("User-Agent", USER_AGENT);
		 * con.setRequestProperty("Accept-Language", "en-US,en;q=0.5");
		 * 
		 * String urlParameters = "feedid=" + SMS_FEEDID + "&username=" + SMS_USERNAME +
		 * "&password=" + SMS_PASSWORD + "&To=" + numbers + "&Text=" +
		 * URLEncoder.encode(message, "UTF-8"); System.out.println("Parameters = " +
		 * urlParameters);
		 * 
		 * // Send post request con.setDoOutput(true); DataOutputStream wr = new
		 * DataOutputStream(con.getOutputStream()); wr.writeBytes(urlParameters);
		 * wr.flush(); wr.close();
		 * 
		 * int responseCode = con.getResponseCode();
		 * System.out.println("\nSending 'POST' request to URL : " + SMS_BULK_URL);
		 * System.out.println("Post parameters : " + urlParameters);
		 * System.out.println("Response Code : " + responseCode);
		 * 
		 * BufferedReader in = new BufferedReader(new
		 * InputStreamReader(con.getInputStream())); String inputLine; StringBuffer
		 * response = new StringBuffer();
		 * 
		 * while ((inputLine = in.readLine()) != null) { response.append(inputLine); }
		 * in.close();
		 * 
		 * // print result System.out.println(response.toString());
		 * 
		 * return parseSMSResponse(response.toString());
		 */

		String numbers = "9920726538";
		for (StudentStudentPortalBean student : studentList) {
			String mobile = student.getMobile();
			if (mobile != null && !"".equals(mobile)) {
				numbers = numbers + "," + mobile;
			}
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

			// print result
			System.out.println(response.toString());

			return parseSMSResponse(response.toString());
		} catch (Exception e) {
			//e.printStackTrace();
			throw e;
		}

	}
	
	public void sendRequestOTP(LeadStudentPortalBean lead) {
		String numbers = lead.getMobile();
		String message = lead.getOtp() + " is your OTP to verify your mobile number at SVKM's NMIMS Global Access. "
				+ "OTP is confidential and valid for 10 minutes";
//		if("android".equalsIgnoreCase(lead.getDeviceType())) {
//			message = "<#> " + lead.getOtp() + " is your ngasce verification code. No3/in9GPUA";
//		}

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

			// print result
			System.out.println(response.toString());
		} catch (Exception e) {
			//e.printStackTrace();
		}

	}

	public static String parseSMSResponse(String respJSON) throws Exception {
		System.out.println("respJSON = " + respJSON);
		JSONObject obj = (JSONObject) new JSONParser().parse(respJSON);
		String output = (String) obj.get("ErrorMessage");
		System.out.println("output = " + output);
		return output; // Will be "Success" or some other value

	}

	public static void main(String[] args) throws ParseException {
		try {
			String url = "http://sms.domainadda.com/vendorsms/pushsms.aspx";
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
			String s = "Test SMS";
			String urlParameters = "user=NGASCE&password=Nga_SCEsms@2020" + "&sid=NGASCE" + "&msisdn=9920726538"
					+ "&msg=" + URLEncoder.encode(s.replaceAll("\\<.*?\\>", ""), "UTF-8") + "&&fl=0&gwid=2";
			System.out.println("Parameters = " + urlParameters);

			// Send post request
			con.setDoOutput(true);
			DataOutputStream wr = new DataOutputStream(con.getOutputStream());
			wr.writeBytes(urlParameters);
			wr.flush();
			wr.close();

			int responseCode = con.getResponseCode();
			System.out.println("\nSending 'POST' request to URL : " + url);
			System.out.println("Post parameters : " + urlParameters);
			System.out.println("Response Code : " + responseCode);

			BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
			String inputLine;
			StringBuffer response = new StringBuffer();

			while ((inputLine = in.readLine()) != null) {
				response.append(inputLine);
			}
			in.close();

			// print result
			String l = parseSMSResponse(response.toString());
			System.out.println(l);

		} catch (Exception e) {
			//e.printStackTrace();

		}

	}

}
