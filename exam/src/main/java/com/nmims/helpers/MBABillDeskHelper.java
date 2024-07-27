package com.nmims.helpers;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import javax.net.ssl.HttpsURLConnection;
import javax.servlet.http.HttpServletRequest;

import org.springframework.stereotype.Component;

import com.nmims.beans.MBAPaymentRequest;
import com.nmims.beans.StudentExamBean;

@Component
public class MBABillDeskHelper {

	private final String BILLDESK_TRANS_URL = "https://pgi.billdesk.com/pgidsk/PGIMerchantPayment";
	private final String BILLDESK_TRANS_STATUS_URL = "https://www.billdesk.com/pgidsk/PGIQueryController";
	private final String BILLDESK_NMIMSID = "NMIMS";
	private final String BILLDESK_SECURITY_ID = "eSTTsLAjCzks";

	/**
	 * CheckSum generation function by bill desk
	 * */
	public static String HmacSHA256(String message,String secret)  {
		try {
			Mac sha256_HMAC = Mac.getInstance("HmacSHA256");
			SecretKeySpec secret_key = new SecretKeySpec(secret.getBytes(), "HmacSHA256");
			sha256_HMAC.init(secret_key);


			byte raw[] = sha256_HMAC.doFinal(message.getBytes());

			StringBuffer ls_sb=new StringBuffer();
			for(int i=0;i<raw.length;i++)
				ls_sb.append(char2hex(raw[i]));
			return ls_sb.toString(); //step 6
		}catch(Exception e){
			
			return null;
		}
	}

	public static String char2hex(byte x){
		 char arr[]={
				 '0','1','2','3',
		         '4','5','6','7',
		         '8','9','A','B',
		         'C','D','E','F'
		 };

		 char c[] = {arr[(x & 0xF0)>>4],arr[x & 0x0F]};
		 return (new String(c));
	}
	/**
	 * End of CheckSum generation function by bill desk
	 * */
	
	public String generateChecksum(MBAPaymentRequest paymentRequest, StudentExamBean student) {
		try {
			
			String message = getMessage(paymentRequest);
			String checkSum = HmacSHA256(message,BILLDESK_SECURITY_ID);
			paymentRequest.setSecureHash(checkSum);
			setBillDeskModelData(paymentRequest, student);;
			return "true";
		}
		catch (Exception e) {
			return (String) e.getMessage();
		}
	}

	public void setBillDeskModelData(MBAPaymentRequest paymentRequest, StudentExamBean student) {

		Map<String, String> paymentParameters = new HashMap<String, String>();
		
		String message = getMessage(paymentRequest)
			+ "|" + paymentRequest.getSecureHash();
		paymentParameters.put("msg", message);

		paymentRequest.setTransactionUrl(BILLDESK_TRANS_URL);
		paymentRequest.setFormParameters(paymentParameters);
	}
	
	private String getMessage(MBAPaymentRequest paymentRequest) {
		String message = BILLDESK_NMIMSID 
				+ "|" + paymentRequest.getTrackId() + "|NA" + "|" + paymentRequest.getAmount() 
				+ "|NA|NA|NA"
				+ "|INR|NA|R"
				+ "|" + BILLDESK_SECURITY_ID + "|NA|NA"
				+ "|F|NA|NA"
				+ "|NA|NA|NA"
				+ "|NA|NA|" + paymentRequest.getCallbackURL();
		
		return message;
	}
	
	public String getTransactionStatus(String trackId) {
		try {
//			SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMddHHmmss");
//			Date date = new Date();
//			String msg = "0122|NMIMS|" + trackId + "|" + formatter.format(date);
//			String checkSum = HmacSHA256(msg,BILLDESK_SECURITY_ID);
//			msg = msg + "|" + checkSum.toUpperCase();
//			String postData = "msg="+msg;
//			
//			RestTemplate restTemplate = new RestTemplate();
//			HttpHeaders headers =  new HttpHeaders();
//			headers.add("Content-Type", "application/x-www-form-urlencoded");
//			HttpEntity<String> entity = new HttpEntity<String>(postData,headers);
//			ResponseEntity<String> response = restTemplate.exchange(BILLDESK_TRANS_STATUS_URL, HttpMethod.POST, entity, String.class);
//			return response.getBody();
			
			
			SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMddHHmmss");
			Date date = new Date();
			String msg = "0122"
					+ "|NMIMS"
					+ "|" + trackId 
					+ "|" + formatter.format(date);
			String checkSum = HmacSHA256(msg,BILLDESK_SECURITY_ID);
			msg = msg + "|" + checkSum.toUpperCase();
			String postData = "msg="+msg;
		
			URL obj = new URL(BILLDESK_TRANS_STATUS_URL);
			HttpsURLConnection con = (HttpsURLConnection) obj.openConnection();

			//add reuqest header
			con.setRequestMethod("POST");
			con.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");

			
			// Send post request
			con.setDoOutput(true);
			DataOutputStream wr = new DataOutputStream(con.getOutputStream());
			wr.writeBytes(postData);
			wr.flush();
			wr.close();

			int responseCode = con.getResponseCode();
			BufferedReader in = new BufferedReader(
			        new InputStreamReader(con.getInputStream()));
			String inputLine;
			StringBuffer response = new StringBuffer();

			while ((inputLine = in.readLine()) != null) {
				response.append(inputLine);
			}
			in.close();
			
			//print result
			return response.toString();
		}
		catch (Exception e) {
			
			return null;
		}
	}
	
//	public boolean verificationBillDeskCheckSum(String[] responseList){
//		try {
//			String message = BILLDESK_NMIMSID + "|" + responseList[2] + "|NA|" + responseList[4] + "|NA|NA|NA|INR|NA|R|" + BILLDESK_SECURITY_ID + "|NA|NA|F|NA|NA|NA|NA|NA|NA|NA|" + BR_RETURN_URL;
//			String checkSum = HmacSHA256(message,BILLDESK_SECURITY_ID);
//			if(checkSum.equalsIgnoreCase(responseList[25])) {
//				return true;
//			}
//			return false;
//		}
//		catch (Exception e) {
//			
//			return false;
//		}
//	}
//	
//	public String checkErrorInBillDeskPayment(HttpServletRequest request, MBAWXPaymentRequest paymentRequest) {
//
//		String errorMessage = null;
//		String amount = (String) request.getSession().getAttribute("amount");
//		String trackId = (String) request.getSession().getAttribute("trackId");
//		try {
//			String[] responseList = request.getParameter("msg").split("\\|");
//
//			if(!responseList[14].equalsIgnoreCase("0300")) {
//				return "Error in processing payment. Error:  " + responseList[24] + " Code: " + responseList[14];
//			}
//			boolean verificationCheckSum = verificationBillDeskCheckSum(responseList);
//			
//			if(verificationCheckSum) {
//				if(Float.parseFloat(responseList[4]) != Float.parseFloat(amount)) {
//					errorMessage = "Error in processing payment. Error: Fees " + amount + " not matching with amount paid ";
//				}
//				return errorMessage;
//			}
//			else {
//				return "Error in processing payment. Error: Hashvalue not matching. Tampering in response found. Track ID: " + trackId;
//			}
//		} catch (Exception e) {
//			
//			return "Error in processing payment. Error: " + e.getMessage();
//		}
//	}
	
	public void createBillDeskResponseBean(HttpServletRequest request, MBAPaymentRequest bean) {
		// Make the booking request bean to insert relevant data back into the database; This is for mobile app payments
		
		String[] responseList = request.getParameter("msg").split("\\|");
		bean.setTransactionID(responseList[2]);
		bean.setMerchantRefNo(responseList[3]);
		bean.setSecureHash(responseList[25]);
		bean.setRespAmount(responseList[4]);
		bean.setRespTranDateTime(responseList[13]);
		bean.setResponseCode(responseList[14]);
		if(bean.getResponseCode().equalsIgnoreCase("0300")) {
			bean.setResponseMessage("Success");
		}else {
			bean.setResponseMessage(responseList[24]);
		}
		bean.setRespPaymentMethod(responseList[7]);
		bean.setBankName(responseList[5]);
	}
}
