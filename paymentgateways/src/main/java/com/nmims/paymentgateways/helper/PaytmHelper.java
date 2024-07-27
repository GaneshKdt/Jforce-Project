package com.nmims.paymentgateways.helper;

import java.util.TreeMap;

import javax.servlet.http.HttpServletRequest;

import org.json.simple.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.ModelAndView;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.nmims.paymentgateways.bean.TransactionsBean;
import com.nmims.paymentgateways.interfaces.PaymentInterface;
import com.paytm.pg.merchant.CheckSumServiceHelper;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;
import java.util.Map.Entry;

@Component
public class PaytmHelper implements PaymentInterface {
	
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
	
	@Value("${PAYTM_TRANS_URL}")
	private String TRANS_URL;
	
	@Value("${PAYTM_TRANS_STATUS_URL}")
	private String TRANS_STATUS_URL;
	
	@Value("${PAYTM_TRANS_REFUND_URL}")
	private String TRANS_REFUND_URL;
	
	@Value("${PAYTM_TRANS_REFUND_STATUS_URL}")
	private String TRANS_REFUND_STATUS_URL;
	
	public static ExceptionHelper exceptionHelper = new ExceptionHelper();
	
	private static final Logger paymentSchedulerLogger = LoggerFactory.getLogger("payment_scheduler");
	
	private static final Logger PAYTM_LOGGER = LoggerFactory.getLogger("paytm_payments");
	
	 private final String paymentOption = "paytm";
	
	@Override
	public String generateCheckSum(TransactionsBean transactionsBean,String returnUrl) {
		try {
			PAYTM_LOGGER.info("generating checksum for track id : {}", transactionsBean.getTrack_id());
			TreeMap<String, String> paytmParams = new TreeMap<String, String>();
			paytmParams.put("MID",PAYTM_MERCHANTMID);
			paytmParams.put("WEBSITE",PAYTM_WEBSITE);
			paytmParams.put("INDUSTRY_TYPE_ID",PAYTM_INDUSTRY_TYPE_ID);
			paytmParams.put("CHANNEL_ID",PAYTM_CHANNEL_ID);
			paytmParams.put("ORDER_ID",transactionsBean.getTrack_id());
			paytmParams.put("CUST_ID",transactionsBean.getSapid());
			paytmParams.put("MOBILE_NO",transactionsBean.getMobile());
			paytmParams.put("EMAIL", transactionsBean.getEmail_id());
			paytmParams.put("TXN_AMOUNT",transactionsBean.getAmount());
			paytmParams.put("MERC_UNQ_REF",transactionsBean.getDescription());
			paytmParams.put("CALLBACK_URL",returnUrl);
			String checkSum = CheckSumServiceHelper.getCheckSumServiceHelper().genrateCheckSum(PAYTM_MERCHANTKEY, paytmParams);
			transactionsBean.setSecure_hash(checkSum);
			return "true";
		}
		catch (Exception e) {
			PAYTM_LOGGER.info("error while generating checksum for track id : {}", transactionsBean.getTrack_id() + e.getMessage());
			return (String) e.getMessage();
		}
	}

	@Override
	public ModelAndView createModelAndViewData(TransactionsBean transactionsBean,String returnUrl) {
		PAYTM_LOGGER.info("creating model and view for track id : {}", transactionsBean.getTrack_id());
		// TODO Auto-generated method stub
		ModelAndView modelAndView = new ModelAndView("paytmPay");
		modelAndView.addObject("TRANS_URL", TRANS_URL);
		modelAndView.addObject("WEBSITE", PAYTM_WEBSITE);
		modelAndView.addObject("ORDER_ID",transactionsBean.getTrack_id());
		modelAndView.addObject("CUST_ID",transactionsBean.getSapid());
		modelAndView.addObject("MOBILE_NO",transactionsBean.getMobile());
		modelAndView.addObject("EMAIL",transactionsBean.getEmail_id());
		modelAndView.addObject("INDUSTRY_TYPE_ID",PAYTM_INDUSTRY_TYPE_ID);
		modelAndView.addObject("CHANNEL_ID", PAYTM_CHANNEL_ID);
		modelAndView.addObject("TXN_AMOUNT", transactionsBean.getAmount());
		modelAndView.addObject("CALLBACK_URL", returnUrl);
		modelAndView.addObject("CHECKSUMHASH", transactionsBean.getSecure_hash());
		modelAndView.addObject("MERCHANTMID",PAYTM_MERCHANTMID);
		modelAndView.addObject("MERC_UNQ_REF",transactionsBean.getDescription());
		PAYTM_LOGGER.info("model and view generated for track id : {}" + modelAndView.getModel (), transactionsBean.getTrack_id());
		return modelAndView;
	}

	@Override
	public boolean verifyCheckSum(HttpServletRequest request) {
		try {
			String paytmChecksum = null;
			TreeMap<String, String> paytmParams = new TreeMap<String, String>();
			for (Entry<String, String[]> requestParamsEntry : request.getParameterMap().entrySet()) {
			    if ("CHECKSUMHASH".equalsIgnoreCase(requestParamsEntry.getKey())){
			        paytmChecksum = requestParamsEntry.getValue()[0];
			    } else {
			        paytmParams.put(requestParamsEntry.getKey(), requestParamsEntry.getValue()[0]);
			    }
			}
			
			boolean isValidChecksum = CheckSumServiceHelper.getCheckSumServiceHelper().verifycheckSum(PAYTM_MERCHANTKEY, paytmParams, paytmChecksum);
			
			if(isValidChecksum){
			    return true;
			}else{
			    return false;
			}
		}
		catch (Exception e) {
			exceptionHelper.createLog(e);
			return false;
		}
	}

	@Override
	public TransactionsBean createResponseBean(HttpServletRequest request) {
		try {
			TransactionsBean session_transactionsBean = (TransactionsBean) request.getSession().getAttribute("transactionsBean");
			
			session_transactionsBean.setPayment_id(request.getParameter("BANKTXNID"));
			session_transactionsBean.setBank_name(request.getParameter("BANKNAME"));
			session_transactionsBean.setMerchant_ref_no(request.getParameter("ORDERID"));
			session_transactionsBean.setSecure_hash(request.getParameter("CHECKSUMHASH"));
			session_transactionsBean.setResponse_message(request.getParameter("RESPMSG"));
			session_transactionsBean.setResponse_code(request.getParameter("RESPCODE"));
			session_transactionsBean.setResponse_amount(request.getParameter("TXNAMOUNT"));
			session_transactionsBean.setResponse_transaction_date_time(request.getParameter("TXNDATE"));
			session_transactionsBean.setResponse_payment_method(request.getParameter("PAYMENTMODE"));
			session_transactionsBean.setTransaction_id(request.getParameter("TXNID"));
			session_transactionsBean.setError(request.getParameter("RESPMSG"));
			//request.getSession().setAttribute("transactionsBean",session_transactionsBean);
			return session_transactionsBean;
		}
		catch (Exception e) {
			exceptionHelper.createLog(e);
			return null;
		}
	} 	

	@Override
	public TransactionsBean getTransactionStatus(TransactionsBean transactionBean,List<String> paymentStatus) {
		paymentSchedulerLogger.info("PaytmHelper.getTransactionStatus() - START");
		TreeMap<String, String> paytmParams = new TreeMap<String, String>();
		paytmParams.put("MID", PAYTM_MERCHANTMID);
		paytmParams.put("ORDERID", transactionBean.getTrack_id());
		try {
			String checkSum = CheckSumServiceHelper.getCheckSumServiceHelper().genrateCheckSum(PAYTM_MERCHANTKEY, paytmParams);
			paymentSchedulerLogger.info("CheckSum generated for track_id:"+transactionBean.getTrack_id()+" is:"+checkSum);
			paytmParams.put("CHECKSUMHASH", checkSum);
			JSONObject obj = new JSONObject(paytmParams);
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
		    paymentSchedulerLogger.info("PayTm response for track_id:"+transactionBean.getTrack_id()+" is:"+response);
		    if(response != null) {
		    	String STATUS = response.get("STATUS").getAsString();
		    	paymentSchedulerLogger.info("STATUS for track_id:"+transactionBean.getTrack_id());
		    	if("TXN_SUCCESS".equalsIgnoreCase(STATUS)) {
		    		transactionBean.setTransaction_status(paymentStatus.get(1));
		    		transactionBean.setTransaction_id(response.get("TXNID").getAsString());
		    		transactionBean.setMerchant_ref_no(response.get("ORDERID").getAsString());
		    		transactionBean.setResponse_code(response.get("RESPCODE").getAsString());
		    		transactionBean.setResponse_message(response.get("RESPMSG").getAsString());
		    		transactionBean.setResponse_amount(response.get("TXNAMOUNT").getAsString());
		    		transactionBean.setResponse_payment_method(response.get("PAYMENTMODE").getAsString());
		    		
		    		if(response.get("MERC_UNQ_REF") != null)
		    			transactionBean.setDescription(response.get("MERC_UNQ_REF").getAsString());
		    		
		    		if(response.get("BANKTXNID") != null) {
		    			transactionBean.setPayment_id(response.get("BANKTXNID").getAsString());
		    		}
		    		if(response.get("BANKNAME") != null) {
		    			transactionBean.setBank_name(response.get("BANKNAME").getAsString());
		    		}
		    		transactionBean.setResponse_transaction_date_time(response.get("TXNDATE").getAsString());
		    	} else if("TXN_FAILURE".equalsIgnoreCase(STATUS)) {
		    		transactionBean.setTransaction_status(paymentStatus.get(2));
		    		transactionBean.setError(response.get("RESPMSG").getAsString());
		    	} else if("PENDING".equalsIgnoreCase(STATUS)) {
		    		transactionBean.setError(response.get("RESPMSG").getAsString());
		    		transactionBean.setTransaction_status(paymentStatus.get(3));
		    	}else {
		    		paymentSchedulerLogger.info("Invalid conditional status: " + STATUS);
		    		transactionBean.setError("Invalid conditional status: " + STATUS);
		    		transactionBean.setTransaction_status(paymentStatus.get(3));
		    	}
		    }else {
		    	paymentSchedulerLogger.info("Invalid Null response from paytm api.");
		    	transactionBean.setError("Invalid Null response from paytm api");
	    		transactionBean.setTransaction_status(paymentStatus.get(3));
		    }
		    paymentSchedulerLogger.info("PaytmHelper.getTransactionStatus() - END");
		    return transactionBean;
		}
		catch (Exception e) {
			paymentSchedulerLogger.error("Error occured while processing PayTm transaction. Error Message:"+e.getMessage());
			transactionBean.setError("Invalid exception: " + e.getMessage());
    		transactionBean.setTransaction_status(paymentStatus.get(3));
    		return transactionBean;
		}
	}

	@Override
	public String checkErrorInTransaction(HttpServletRequest request) {
		try {
			TransactionsBean transactionsBean_session = (TransactionsBean) request.getSession().getAttribute("transactionsBean");
			String track_id = (String) request.getSession().getAttribute("track_id");
			PAYTM_LOGGER.info("checking error in transaction for track id : {}", track_id);
			//check trackId
			if(!track_id.equals(request.getParameter("ORDERID"))) {
				PAYTM_LOGGER.info("Error in processing payment. Error: TrackId not matching with transaction Id {}", track_id);
				return "Error in processing payment. Error: TrackId not matching with transaction Id";
			}
			if(transactionsBean_session.getTrack_id() == null) {
				PAYTM_LOGGER.info("Error in processing payment. Error: TrackId session expired : {}", track_id);
				return "Error in processing payment. Error: TrackId session expired";
			}
			if(!transactionsBean_session.getTrack_id().equals(request.getParameter("ORDERID"))) {
				PAYTM_LOGGER.info("Error in processing payment. Error: TrackId not matching with transaction Id : {}", track_id);
				return "Error in processing payment. Error: TrackId not matching with transaction Id";
			}
			//isSuccess
			if(!"Txn Success".equalsIgnoreCase(request.getParameter("RESPMSG"))) {
				PAYTM_LOGGER.info("Error in processing payment. Error:  " + request.getParameter("RESPMSG") + " Code: "
						+ request.getParameter("RESPCODE") + " fpr track id : " + track_id);
				return "Error in processing payment. Error:  " + request.getParameter("RESPMSG") + " Code: " + request.getParameter("RESPCODE");
			}
			//checksum verify
			boolean verificationCheckSum = verifyCheckSum(request);
			if(!verificationCheckSum) {
				exceptionHelper.createInfoLog("Tampering in response found. Track ID: " + transactionsBean_session.getTrack_id());
				PAYTM_LOGGER.info(
						"Error in processing payment. Error: Hashvalue not matching. Tampering in response found. Track ID: "
								+ transactionsBean_session.getTrack_id());
				
				return "Error in processing payment. Error: Hashvalue not matching. Tampering in response found. Track ID: " + transactionsBean_session.getTrack_id();
			}
			//amount check
			if(Float.parseFloat(transactionsBean_session.getAmount()) != Float.parseFloat(request.getParameter("TXNAMOUNT"))) {
				PAYTM_LOGGER.info("Fees " + transactionsBean_session.getAmount() + " not matching with amount paid " + " for track id " + track_id);
				exceptionHelper.createInfoLog("Fees " + transactionsBean_session.getAmount() + " not matching with amount paid ");
				return "Error in processing payment. Error: Fees " + transactionsBean_session.getAmount() + " not matching with amount paid ";
			}
			return "true";
			
		}
		catch (Exception e) {
			exceptionHelper.createLog(e);
			PAYTM_LOGGER.info("Error in processing payment. Error: " + e.getMessage());
			return "Error in processing payment. Error: " + e.getMessage();
		}
	}

	@Override
	public JsonObject refundInitiate(String tracking_id, String transaction_id, String refund_amount) {
		PAYTM_LOGGER.info("Initiating refund for track id : {} with amount : {}", tracking_id, refund_amount);
		try {
			String refId = tracking_id + System.currentTimeMillis();	//unique refId for refund payTm
			/* initialize an object */
			JSONObject paytmParams = new JSONObject();

			/* body parameters */
			JSONObject body = new JSONObject();

			/* Find your MID in your Paytm Dashboard at https://dashboard.paytm.com/next/apikeys */
			body.put("mid", PAYTM_MERCHANTMID);

			/* This has fixed value for refund transaction */
			body.put("txnType", "REFUND");

			/* Enter your order id for which refund needs to be initiated */
			body.put("orderId",tracking_id);

			/* Enter transaction id received from Paytm for respective successful order */
			body.put("txnId", transaction_id);

			/* Enter numeric or alphanumeric unique refund id */
			body.put("refId", refId);

			/* Enter amount that needs to be refunded, this must be numeric */
			body.put("refundAmount", refund_amount);
			
			
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
			
			PAYTM_LOGGER.info("sending data : " + paytmParams.toString() + " to url " + TRANS_REFUND_URL);
			
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
	        	System.out.append("Response: " + responseData);
	        }
	        responseReader.close();
	        JsonParser parser = new JsonParser();
		    JsonObject response = parser.parse(responseData).getAsJsonObject();
		    PAYTM_LOGGER.info("received data : " + response + " for track id : " + tracking_id);
		    return response;
		}
		catch (Exception e) {
			exceptionHelper.createLog(e);
			PAYTM_LOGGER.info("Error initiating refund for track id " + tracking_id);
			return null;
		}
	}

	@Override
	public JsonObject refundStatus(String tracking_id, String refId) {
		
		try {
			PAYTM_LOGGER.info("fetching refund status for track id : " + tracking_id + " and refund id " + refId);
			
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
		
			PAYTM_LOGGER.info("Sending data : " + paytmParams + " to url : {}", TRANS_REFUND_STATUS_URL);
			
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
				System.out.append("Response: " + responseData);
			}
			// System.out.append("Request: " + post_data);
			responseReader.close();
			JsonParser parser = new JsonParser();
			JsonObject response = parser.parse(responseData).getAsJsonObject();
			PAYTM_LOGGER.info("response received for track id : {} " + response, tracking_id);
			return response;
			
		}
		catch (Exception e) {
			exceptionHelper.createLog(e);
			PAYTM_LOGGER.info("Error fetching refund  status for track id " + tracking_id);
			return null;
		}
		
	}
	
	
	
}
