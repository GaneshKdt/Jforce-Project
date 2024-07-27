package com.nmims.helpers;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Map;
import java.util.Map.Entry;
import java.util.TreeMap;

import javax.servlet.http.HttpServletRequest;

import org.json.simple.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.nmims.beans.MBAExamBookingPaytmResponse;
import com.nmims.beans.MBAPaymentRequest;
import com.nmims.beans.StudentExamBean;
import com.paytm.pg.merchant.CheckSumServiceHelper;

@Component
public class MBAPaytmHelper {

	@Value("${PAYTM_TRANS_URL}")
	private String TRANS_URL;
	
	@Value("${PAYTM_TRANS_STATUS_URL}")
	private String TRANS_STATUS_URL;
	
	@Value("${PAYTM_TRANS_REFUND_URL}")
	private String TRANS_REFUND_URL;

	@Value("${PAYTM_TRANS_REFUND_STATUS_URL}")
	private String TRANS_REFUND_STATUS_URL;

	@Value("${PAYTM_MOBILE_TRANS_URL}")
	private String PAYTM_MOBILE_TRANS_URL;

	//PayTM Creds
	@Value("${PAYTM_MERCHANTMID}")
	private String PAYTM_MERCHANTMID;

	@Value("${PAYTM_MERCHANTKEY}")
	private String PAYTM_MERCHANTKEY;

	@Value("${PAYTM_INDUSTRY_TYPE_ID}")
	private String PAYTM_INDUSTRY_TYPE_ID;

	@Value("${PAYTM_CHANNEL_ID}")
	private String PAYTM_CHANNEL_ID;

	@Value("${PAYTM_WEBSITE}")
	private String PAYTM_WEBSITE;
	

	public void createPaytmResponseBean(MBAExamBookingPaytmResponse bookingPaytmResponse, MBAPaymentRequest bean) {
		Map<String, String> responseMap = bookingPaytmResponse.getApiresponse();

//		Make the booking request bean to insert relevant data back into the database; This is for mobile app payments
		bean.setResponseMessage(responseMap.get("RESPMSG"));
		bean.setTransactionID(responseMap.get("TXNID"));
		bean.setTrackId(bookingPaytmResponse.getTrackId());
		bean.setMerchantRefNo(responseMap.get("ORDERID"));
		bean.setSecureHash(responseMap.get("CHECKSUMHASH"));
		bean.setRespAmount(responseMap.get("TXNAMOUNT"));
		bean.setAmount(responseMap.get("TXNAMOUNT"));
		bean.setRespTranDateTime(responseMap.get("TXNDATE"));
		bean.setResponseCode(responseMap.get("RESPCODE"));
		bean.setRespPaymentMethod(responseMap.get("PAYMENTMODE"));
		bean.setPaymentID(responseMap.get("BANKTXNID"));
		bean.setBankName(responseMap.get("BANKNAME"));
	}

	public void createPaytmResponseBean(HttpServletRequest request, MBAPaymentRequest bean) {
//		Make the booking request bean to insert relevant data back into the database; This is for the web app callback
		bean.setResponseMessage(request.getParameter("RESPMSG"));
		bean.setTransactionID(request.getParameter("TXNID"));
		bean.setMerchantRefNo(request.getParameter("ORDERID"));
		bean.setSecureHash(request.getParameter("CHECKSUMHASH"));
		bean.setRespAmount(request.getParameter("TXNAMOUNT"));
		bean.setRespTranDateTime(request.getParameter("TXNDATE"));
		bean.setResponseCode(request.getParameter("RESPCODE"));
		bean.setRespPaymentMethod(request.getParameter("PAYMENTMODE"));
		bean.setPaymentID(request.getParameter("BANKTXNID"));
		bean.setBankName(request.getParameter("BANKNAME"));
	}
	
	
	public String generateCheckSum(MBAPaymentRequest paymentRequest,StudentExamBean student) {

		// Generate paytm checksum.
		try {
			// Step 1 : create the treemap (treemap keys are sorted ascending. helps with making sure the fields are in correct order.)
			TreeMap<String, String> paytmParams = new TreeMap<String, String>();
			paymentRequest.setTransactionUrl(TRANS_URL);
			paytmParams.put("MID", PAYTM_MERCHANTMID);
			paytmParams.put("ORDER_ID", paymentRequest.getTrackId());
			paytmParams.put("CHANNEL_ID", PAYTM_CHANNEL_ID);
			paytmParams.put("CUST_ID", student.getSapid());
			paytmParams.put("MOBILE_NO",student.getMobile());
			paytmParams.put("EMAIL", student.getEmailId());
			paytmParams.put("TXN_AMOUNT", paymentRequest.getAmount());
			paytmParams.put("WEBSITE", PAYTM_WEBSITE);
			paytmParams.put("INDUSTRY_TYPE_ID", PAYTM_INDUSTRY_TYPE_ID);
			paytmParams.put("CALLBACK_URL", paymentRequest.getCallbackURL());

			// Step 2 generate the checksum, add it to the map and save map in the request bean.
			String checkSum =  CheckSumServiceHelper.getCheckSumServiceHelper().genrateCheckSum(PAYTM_MERCHANTKEY, paytmParams);
			paymentRequest.setSecureHash(checkSum);
			paytmParams.put("CHECKSUMHASH", paymentRequest.getSecureHash());
			paymentRequest.setFormParameters(paytmParams);
			return "true";
		} catch(Exception e) {
			return (String) e.getMessage();
		}
	}

	public boolean verificationCheckSum(MBAExamBookingPaytmResponse bookingPaytmResponse) throws Exception {

		// Loop the response object

		// Step 1 : create the treemap (treemap keys are sorted ascending. helps with making sure the fields are in correct order.)
		Map<String, String> responseFromPaytm = bookingPaytmResponse.getApiresponse();
		TreeMap<String, String> paytmParams = new TreeMap<String, String>(responseFromPaytm);
		

		// Step 2 : remove the checksum field from the treemap and save it as a String (to pass to paytm verify function)
		String paytmChecksum = paytmParams.remove("CHECKSUMHASH");

		// Step 3 : verify
		return verifyChecksum(paytmParams, paytmChecksum);
	}
	
	private boolean verifyChecksum(TreeMap<String, String> paytmParams, String paytmChecksum) throws Exception {

		// Call the method for verification
		boolean isValidChecksum = CheckSumServiceHelper.getCheckSumServiceHelper().verifycheckSum(PAYTM_MERCHANTKEY, paytmParams, paytmChecksum);
		// If isValidChecksum is false, then checksum is not valid
		if(isValidChecksum){
		    return true;
		}else{
		    return false;
		}
	}
	
	public boolean verificationCheckSum(HttpServletRequest request) throws Exception {
		String paytmChecksum = null;
		// Create a tree map from the form post param
		

		// Step 1 : create the treemap (treemap keys are sorted ascending. helps with making sure the fields are in correct order.)
		TreeMap<String, String> paytmParams = new TreeMap<String, String>();
		for (Entry<String, String[]> requestParamsEntry : request.getParameterMap().entrySet()) {
			// Step 2 : remove the checksum field from the treemap and save it as a String (to pass to paytm verify function)
		    if ("CHECKSUMHASH".equalsIgnoreCase(requestParamsEntry.getKey())){
		        paytmChecksum = requestParamsEntry.getValue()[0];
		    } else {
		        paytmParams.put(requestParamsEntry.getKey(), requestParamsEntry.getValue()[0]);
		    }
		}
		// Step 3 : verify
		return verifyChecksum(paytmParams, paytmChecksum);
	}
	
	/**
	 * payment refund logic
	 * */
	
	public JsonObject refundInitiate(MBAPaymentRequest paymentRequest) {
		try {
			String refId = paymentRequest.getTrackId() + System.currentTimeMillis();	//unique refId for refund payTm
			/* initialize an object */
			JSONObject paytmParams = new JSONObject();

			/* body parameters */
			JSONObject body = new JSONObject();

			/* Find your MID in your Paytm Dashboard at https://dashboard.paytm.com/next/apikeys */
			body.put("mid", PAYTM_MERCHANTMID);

			/* This has fixed value for refund transaction */
			body.put("txnType", "REFUND");

			/* Enter your order id for which refund needs to be initiated */
			body.put("orderId", paymentRequest.getTrackId());

			/* Enter transaction id received from Paytm for respective successful order */
			body.put("txnId", paymentRequest.getTransactionID());

			/* Enter numeric or alphanumeric unique refund id */
			body.put("refId", refId);

			/* Enter amount that needs to be refunded, this must be numeric */
			body.put("refundAmount", paymentRequest.getAmount());

			/**
			* Generate checksum by parameters we have in body
			* You can get Checksum JAR from https://developer.paytm.com/docs/checksum/
			* Find your Merchant Key in your Paytm Dashboard at https://dashboard.paytm.com/next/apikeys 
			*/
			String checksum = CheckSumServiceHelper.getCheckSumServiceHelper().genrateCheckSum(PAYTM_MERCHANTKEY, body.toString());

			/* head parameters */
			JSONObject head = new JSONObject();

			/* This is used when you have two different merchant keys. In case you have only one please put - C11 */
			head.put("clientId", "C11");

			/* put generated checksum value here */
			head.put("signature", checksum);

			/* prepare JSON string for request */
			paytmParams.put("body", body);
			paytmParams.put("head", head);
			String post_data = paytmParams.toString();
			URL url = new URL(TRANS_REFUND_URL);

	        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
	        connection.setRequestMethod("POST");
	        connection.setRequestProperty("Content-Type", "application/json");
	        connection.setUseCaches(false);
	        connection.setDoOutput(true);
	        
	        DataOutputStream requestWriter = new DataOutputStream(connection.getOutputStream());
	        requestWriter.writeBytes(post_data);
	        requestWriter.close();
	        String responseData = "";
	        InputStream is = connection.getInputStream();
	        BufferedReader responseReader = new BufferedReader(new InputStreamReader(is));
	        if ((responseData = responseReader.readLine()) != null) {
	        }
	        responseReader.close();
	        JsonParser parser = new JsonParser();

		    
		    JsonObject response = parser.parse(responseData).getAsJsonObject();

		    return response;
        } catch (Exception exception) {
        	return null;
        }
	}
	
	
	/**
	 * refund transaction status check 
	 * */
	
	public JsonObject refundStatus(String tracking_id,String refId) {
		try {
			/* initialize an object */
			JSONObject paytmParams = new JSONObject();
	
			/* body parameters */
			JSONObject body = new JSONObject();
	
			/* Find your MID in your Paytm Dashboard at https://dashboard.paytm.com/next/apikeys */
			body.put("mid", PAYTM_MERCHANTMID);
	
			/* Enter your order id for which refund needs to be initiated */
			body.put("orderId", tracking_id);
	
			/* Enter refund id which was used for initiating refund */
			body.put("refId", refId);
	
			/**
			* Generate checksum by parameters we have in body
			* You can get Checksum JAR from https://developer.paytm.com/docs/checksum/
			* Find your Merchant Key in your Paytm Dashboard at https://dashboard.paytm.com/next/apikeys 
			*/
			String checksum = CheckSumServiceHelper.getCheckSumServiceHelper().genrateCheckSum(PAYTM_MERCHANTKEY, body.toString());
	
			/* head parameters */
			JSONObject head = new JSONObject();
	
			/* This is used when you have two different merchant keys. In case you have only one please put - C11 */
			head.put("clientId", "C11");
	
			/* put generated checksum value here */
			head.put("signature", checksum);
	
			/* prepare JSON string for request */
			paytmParams.put("body", body);
			paytmParams.put("head", head);
			String post_data = paytmParams.toString();
	
			URL url = new URL(TRANS_REFUND_STATUS_URL);
		
			HttpURLConnection connection = (HttpURLConnection) url.openConnection();
			connection.setRequestMethod("POST");
			connection.setRequestProperty("Content-Type", "application/json");
			connection.setDoOutput(true);

			DataOutputStream requestWriter = new DataOutputStream(connection.getOutputStream());
			requestWriter.writeBytes(post_data);
			requestWriter.close();
			String responseData = "";
			InputStream is = connection.getInputStream();
			BufferedReader responseReader = new BufferedReader(new InputStreamReader(is));
			if ((responseData = responseReader.readLine()) != null) {
			}
			responseReader.close();
			JsonParser parser = new JsonParser();
			JsonObject response = parser.parse(responseData).getAsJsonObject();
			return response;
		} catch (Exception exception) {
			return null;
		}
		
	}
	
	/**
	 * End of paytm refund api
	 * */
	

	public JsonObject getTransactionStatus(MBAPaymentRequest mbawxPaymentRequest) {
		String trackId = mbawxPaymentRequest.getTrackId();
		TreeMap<String, String> paytmParams = new TreeMap<String, String>();
		paytmParams.put("MID", PAYTM_MERCHANTMID);
		paytmParams.put("ORDERID", trackId);
		
		try {
			String checkSum = CheckSumServiceHelper.getCheckSumServiceHelper().genrateCheckSum(PAYTM_MERCHANTKEY, paytmParams);
		    paytmParams.put("CHECKSUMHASH", checkSum);
		    JSONObject obj = new JSONObject();
		    obj.putAll(paytmParams);
		    String postData = "JsonData=" + obj.toString();
		    URL url =  new URL(TRANS_STATUS_URL);
		    HttpURLConnection connection = (HttpURLConnection) url.openConnection();
		    connection.setRequestMethod("POST");
		    connection.setRequestProperty("contentType", "application/json");
		    connection.setUseCaches(false);
		    connection.setDoOutput(true);

		    DataOutputStream requestWriter = new DataOutputStream(connection.getOutputStream());
		    requestWriter.writeBytes( postData);
		    requestWriter.close();
		    String responseData = "";
		    InputStream is = (InputStream) connection.getInputStream();
		    BufferedReader responseReader = new BufferedReader(new InputStreamReader(is));
		    if((responseData = responseReader.readLine()) != null) {
		    }
		    JsonParser parser = new JsonParser();
		    JsonObject response = parser.parse(responseData).getAsJsonObject();

		    checkIfPaymentSuccess(response, mbawxPaymentRequest);
		    return response;
		} catch (Exception exception) {
		    return null;
		}
		
	}
	
	public void checkIfPaymentSuccess(JsonObject response, MBAPaymentRequest paymentRequest) {
		
		String transactionStatus = response.get("STATUS").getAsString();
		
		if("TXN_SUCCESS".equalsIgnoreCase(transactionStatus)) {
			try {
				paymentRequest.setRequestStatus(MBAPaymentRequest.TRAN_STATUS_SUCCESSFUL);
				paymentRequest.setTransactionID(response.get("TXNID").getAsString());
				paymentRequest.setMerchantRefNo(response.get("ORDERID").getAsString());
				paymentRequest.setResponseCode(response.get("RESPCODE").getAsString());
				paymentRequest.setResponseMessage(response.get("RESPMSG").getAsString());
				paymentRequest.setRespAmount(response.get("TXNAMOUNT").getAsString());
				paymentRequest.setRespPaymentMethod(response.get("PAYMENTMODE").getAsString());
				if(response.get("BANKTXNID") != null) {
					paymentRequest.setPaymentID(response.get("BANKTXNID").getAsString());
				}
				if(response.get("BANKNAME") != null) {
					paymentRequest.setBankName(response.get("BANKNAME").getAsString());
				}
				paymentRequest.setSuccessFromGateway(true);
			} catch(Exception e) {
			}
		
		}else if("TXN_FAILURE".equalsIgnoreCase(transactionStatus)) {
			
			paymentRequest.setRequestStatus(MBAPaymentRequest.REQUEST_STATUS_PAYMENT_FAILED);
			paymentRequest.setError(response.get("RESPMSG").getAsString());
		}
	}

	public String checkErrorInPaytmPayment(HttpServletRequest request, MBAPaymentRequest paymentRequest) {

		String amount = paymentRequest.getAmount();

		try {
			//isSuccess
			if(!request.getParameter("RESPMSG").equalsIgnoreCase("Txn Success")) {
				return "Error in processing payment. Error:  " + request.getParameter("RESPMSG") + " Code: " + request.getParameter("RESPCODE");
			}
			boolean verificationCheckSum = verificationCheckSum(request);
			
			if(verificationCheckSum) {
				if(Float.parseFloat(request.getParameter("TXNAMOUNT")) != Float.parseFloat(amount)) {
					return "Error in processing payment. Error: Fees " + amount + " not matching with amount paid ";
				}
				if("Txn Success".equalsIgnoreCase(request.getParameter("RESPMSG"))) {
					return null;	// success response;
				}
				return "Error in processing payment.";
			}
			else {
				return "Error in processing payment. Error: Hashvalue not matching. Tampering in response found.";
			}
		} catch (Exception e) {
			
			return "Error in processing payment. Error: " + e.getMessage();
		}
	}
	
}
