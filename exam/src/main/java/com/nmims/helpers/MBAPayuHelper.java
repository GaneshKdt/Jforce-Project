package com.nmims.helpers;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStreamReader;
import java.math.BigInteger;
import java.net.URL;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

import javax.net.ssl.HttpsURLConnection;
import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.nmims.beans.MBAPaymentRequest;
import com.nmims.beans.StudentExamBean;

@Component
public class MBAPayuHelper {

	//PAYU Creds
	@Value("${PAYU_SECRET_KEY}")
	private String PAYU_SECRET_KEY;

	@Value("${PAYU_SALT}")
	private String PAYU_SALT;

	@Value("${PAYU_TRANS_URL}")
	private String PAYU_TRANS_URL;

	@Value("${PAYU_TRANS_STATUS_URL}")
	private String PAYU_TRANS_STATUS_URL;

	/**
	 * Generate checksum for payU
	 * */
	
	public String generatePayuCheckSumSHA512(String input) 
    { 
        try { 
            // getInstance() method is called with algorithm SHA-512 
            MessageDigest md = MessageDigest.getInstance("SHA-512"); 
  
            // digest() method is called 
            // to calculate message digest of the input string 
            // returned as array of byte 
            byte[] messageDigest = md.digest(input.getBytes()); 
  
            // Convert byte array into signum representation 
            BigInteger no = new BigInteger(1, messageDigest); 
  
            // Convert message digest into hex value 
            String hashtext = no.toString(16); 
  
            // Add preceding 0s to make it 32 bit 
            while (hashtext.length() < 32) { 
                hashtext = "0" + hashtext; 
            } 
  
            // return the HashText 
            return hashtext; 
        } 
  
        // For specifying wrong message digest algorithms 
        catch (NoSuchAlgorithmException e) { 
            throw new RuntimeException(e); 
        } 
    } 
	
	/**
	 * End of checksum for payU
	 * */
	
	
	public String generatePayuCheckSum(MBAPaymentRequest paymentRequest,StudentExamBean student) {
		try {
			String data = ""
					+ PAYU_SECRET_KEY 
					+ "|" + paymentRequest.getTrackId() 		+ "|" + paymentRequest.getAmount() 
					+ "|" + paymentRequest.getDescription() 	+ "|" + student.getFirstName() 
					+ "|" + student.getEmailId() 			+ "|" + "" 
					+ "|" + ""								+ "|" + "" 
					+ "|" + "" 								+ "|" + "" 
					+ "|" + "" 								+ "|" + "" 
					+ "|" + "" 								+ "|" + "" 
					+ "|" + "" 								+ "|" + PAYU_SALT;
			
			String checksum = generatePayuCheckSumSHA512(data);
			paymentRequest.setSecureHash(checksum);
			setPayuModelData(paymentRequest, student);
			return "true";
		}
		catch (Exception e) {
			return e.getMessage();
		}
	}
	
	
	public void setPayuModelData(MBAPaymentRequest paymentRequest,StudentExamBean student) {
		Map<String, String> paymentParameters = new HashMap<String, String>();
		paymentParameters.put("key", PAYU_SECRET_KEY);
		paymentParameters.put("txnid", paymentRequest.getTrackId());
		paymentParameters.put("amount", paymentRequest.getAmount());
		paymentParameters.put("productinfo", paymentRequest.getDescription());
		paymentParameters.put("firstname", student.getFirstName());
		paymentParameters.put("email", student.getEmailId());
		paymentParameters.put("phone", student.getMobile());
		paymentParameters.put("surl", paymentRequest.getCallbackURL());
		paymentParameters.put("furl", paymentRequest.getCallbackURL());
		paymentParameters.put("hash", paymentRequest.getSecureHash());

		
		paymentRequest.setTransactionUrl(PAYU_TRANS_URL);
		paymentRequest.setFormParameters(paymentParameters);
	}
	

	
	
	public boolean verificationPayuCheckSum(HttpServletRequest request){
		try {
			String data = PAYU_SALT + ""
					+ "|" + request.getParameter("status") + "|" + "|" 
					+ "|" + "|" + "|"
					+ "|" + "|" + "|" 
					+ "|" + "|" + "|" + request.getParameter("email")
					+ "|" + request.getParameter("firstname") + "|" + request.getParameter("productinfo") + "|" + request.getParameter("amount") 
					+ "|" + request.getParameter("txnid") + "|" + request.getParameter("key");
			String checksum = generatePayuCheckSumSHA512(data);
			if(checksum.equalsIgnoreCase(request.getParameter("hash"))) {
				return true;
			}
			return false;
		}
		catch (Exception e) {
			
			return false;
		}
	}
	

	public void createPayuResponseBean(HttpServletRequest request, MBAPaymentRequest bean) {
		// Make the booking request bean to insert relevant data back into the database;
		bean.setResponseMessage(request.getParameter("status"));
		bean.setTransactionID(request.getParameter("mihpayid"));
		bean.setMerchantRefNo(request.getParameter("txnid"));
		bean.setSecureHash(request.getParameter("hash"));
		bean.setRespAmount(request.getParameter("amount"));
		bean.setRespTranDateTime(new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(Calendar.getInstance().getTime()));
		bean.setResponseCode(request.getParameter("status"));
		bean.setRespPaymentMethod(request.getParameter("mode"));
		bean.setPaymentID(request.getParameter("bank_ref_num"));
		bean.setBankName(request.getParameter("bankcode"));
		bean.setError(request.getParameter("error"));
	}
	
	public String checkErrorInPayuPayment(HttpServletRequest request, MBAPaymentRequest paymentRequest) {
		//String errorMessage = null;
		String amount = paymentRequest.getAmount();
		
		try {
			//isSuccess
			if(!request.getParameter("status").equalsIgnoreCase("success")) {
				return "Error in processing payment. Error:  " + request.getParameter("error");
			}
			boolean verificationCheckSum = verificationPayuCheckSum(request);
			
			if(verificationCheckSum) {
				if(Float.parseFloat(request.getParameter("amount")) != Float.parseFloat(amount)) {
					return "Error in processing payment. Error: Fees " + amount + " not matching with amount paid ";
				}
				if("success".equalsIgnoreCase(request.getParameter("status"))) {
					return null;	// success response;
				}
				return "Error in payment response.";
			}
			else {
				return "Error in processing payment. Error: Hashvalue not matching. Tampering in response found.";
			}
		} catch (Exception e) {
			
			return "Error in processing payment. Error: " + e.getMessage();
		}
	}
	
	

	public void checkIfPaymentSuccess(JsonObject jsonObj, MBAPaymentRequest mbawxPaymentRequest) {
		String STATUS = jsonObj.get("status").getAsString();
		try {
			JsonObject transaction_detail = jsonObj.get("transaction_details").getAsJsonObject();
			transaction_detail = transaction_detail.get(mbawxPaymentRequest.getTrackId()).getAsJsonObject();
			String MSG = transaction_detail.get("status").getAsString();
			
			if("1".equalsIgnoreCase(STATUS) && "success".equalsIgnoreCase(MSG)) {
				mbawxPaymentRequest.setRequestStatus(MBAPaymentRequest.REQUEST_STATUS_SUBMITTED);
				mbawxPaymentRequest.setTransactionID(transaction_detail.get("mihpayid").getAsString());
				mbawxPaymentRequest.setMerchantRefNo(transaction_detail.get("txnid").getAsString());
				mbawxPaymentRequest.setResponseCode(transaction_detail.get("status").getAsString());
				mbawxPaymentRequest.setResponseMessage(transaction_detail.get("status").getAsString());
				mbawxPaymentRequest.setRespAmount(transaction_detail.get("amt").getAsString());
				mbawxPaymentRequest.setRespPaymentMethod(transaction_detail.get("mode").getAsString());
				if(transaction_detail.get("bank_ref_num") != null) {
					mbawxPaymentRequest.setPaymentID(transaction_detail.get("bank_ref_num").getAsString());
				}
				if(transaction_detail.get("bankcode") != null) {
					mbawxPaymentRequest.setBankName(transaction_detail.get("bankcode").getAsString());
				}
				mbawxPaymentRequest.setSuccessFromGateway(true);
			}else if("0".equalsIgnoreCase(STATUS) || ("1".equalsIgnoreCase(STATUS) && "failure".equalsIgnoreCase(MSG))) {
				mbawxPaymentRequest.setRequestStatus(MBAPaymentRequest.REQUEST_STATUS_PAYMENT_FAILED);
				//serviceRequest.setError(transaction_detail.get("error_Message").getAsString());
				mbawxPaymentRequest.setError(MSG);
			}
		}catch(Exception e) {
			if("0".equalsIgnoreCase(STATUS)) {
				mbawxPaymentRequest.setRequestStatus(MBAPaymentRequest.REQUEST_STATUS_PAYMENT_FAILED);
				//serviceRequest.setError(transaction_detail.get("error_Message").getAsString());
				if(jsonObj.get("msg") != null) {
					mbawxPaymentRequest.setError(jsonObj.get("msg").getAsString());
				} else {
					mbawxPaymentRequest.setError("Invalid Response from payu");
				}
			}
		}
	}
	
	public JsonObject getTransactionStatus(MBAPaymentRequest mbawxPaymentRequest) {
		try {  
			String trackId = mbawxPaymentRequest.getTrackId();
			String msg = ""
					+ PAYU_SECRET_KEY + "|" + "verify_payment"
					+ "|" + trackId + "|" + PAYU_SALT;
			String checkSum = generatePayuCheckSumSHA512(msg);
			String postData =  ""
					+ "key=" + PAYU_SECRET_KEY + "&command=" + "verify_payment"
					+ "&hash=" + checkSum + "&var1=" + trackId;
		
			URL obj = new URL(PAYU_TRANS_STATUS_URL);
			HttpsURLConnection con = (HttpsURLConnection) obj.openConnection();

			//add reuqest header
			con.setRequestMethod("POST");

			
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
			JsonParser parser = new JsonParser();
		    JsonObject responseData = parser.parse(response.toString()).getAsJsonObject();
		    
		    checkIfPaymentSuccess(responseData, mbawxPaymentRequest);
			return responseData;
		}
		catch (Exception e) {
			
			return null;
		}
	}

	/**
	 * start of payu refund api
	 * */
	
	public JsonObject refundInitiate(MBAPaymentRequest paymentRequest) {
		try {  
			String formaction = "cancel_refund_transaction";
			String refId = paymentRequest.getTrackId() + System.currentTimeMillis();	//unique refId for refund payTm
			String msg = PAYU_SECRET_KEY + "|"+ formaction +"|" + paymentRequest.getTransactionID() + "|" + PAYU_SALT;
			String checkSum = generatePayuCheckSumSHA512(msg);
			String postData = ""
					+ "key="+ PAYU_SECRET_KEY + "&command="+ formaction
					+ "&hash=" + checkSum + "&var1=" + paymentRequest.getTransactionID()
					+ "&var2=" + refId + "&var3=" + paymentRequest.getAmount();

			URL obj = new URL(PAYU_TRANS_STATUS_URL);
			HttpsURLConnection con = (HttpsURLConnection) obj.openConnection();

			//add reuqest header
			con.setRequestMethod("POST");

			
			// Send post request
			con.setDoOutput(true);
			DataOutputStream wr = new DataOutputStream(con.getOutputStream());
			wr.writeBytes(postData);
			wr.flush();
			wr.close();

//			int responseCode = con.getResponseCode();

			BufferedReader in = new BufferedReader(
			        new InputStreamReader(con.getInputStream()));
			String inputLine;
			StringBuffer response = new StringBuffer();

			while ((inputLine = in.readLine()) != null) {
				response.append(inputLine);
			}
			in.close();
			
			//print result

			JsonParser parser = new JsonParser();
		    JsonObject responseData = parser.parse(response.toString()).getAsJsonObject();
		    responseData.addProperty("refId", refId);

			return responseData;
		}
		catch (Exception e) {
			
			return null;
		}
	}
	
	public JsonObject payuRefundStatus(String refundId) {
		try {  
			String formaction = "check_action_status";
			String msg = ""
					+ PAYU_SECRET_KEY + "|"+ formaction 
					+ "|" + refundId + "|" + PAYU_SALT;
			
			String checkSum = generatePayuCheckSumSHA512(msg);
			String postData = ""
					+ "key="+ PAYU_SECRET_KEY + "&command="+ formaction
					+ "&hash=" + checkSum + "&var1=" + refundId;

			URL obj = new URL(PAYU_TRANS_STATUS_URL);
			HttpsURLConnection con = (HttpsURLConnection) obj.openConnection();

			//add reuqest header
			con.setRequestMethod("POST");

			// Send post request
			con.setDoOutput(true);
			DataOutputStream wr = new DataOutputStream(con.getOutputStream());
			wr.writeBytes(postData);
			wr.flush();
			wr.close();

//			int responseCode = con.getResponseCode();

			BufferedReader in = new BufferedReader(
			        new InputStreamReader(con.getInputStream()));
			String inputLine;
			StringBuffer response = new StringBuffer();

			while ((inputLine = in.readLine()) != null) {
				response.append(inputLine);
			}
			in.close();
			
			//print result
			JsonParser parser = new JsonParser();
		    JsonObject responseData = parser.parse(response.toString()).getAsJsonObject();
			return responseData;
		}
		catch (Exception e) {
			
			return null;
		}
	}

	
	/**
	 * End of payu refund api
	 * */
}
