package com.nmims.paymentgateways.helper;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStreamReader;
import java.math.BigInteger;
import java.net.URL;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Calendar;
import java.util.List;

import javax.net.ssl.HttpsURLConnection;
import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.ModelAndView;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.nmims.paymentgateways.bean.TransactionsBean;
import com.nmims.paymentgateways.interfaces.PaymentInterface;

@Component
public class PayuHelper implements PaymentInterface {

	@Value("${PAYU_SECRET_KEY}")
	private String PAYU_SECRET_KEY;
	
	//private final String PAYU_SECRET_KEY = "bWIMX5"; //testCreds
	@Value("${PAYU_SALT}")
	private String PAYU_SALT;
	
	//private final String PAYU_SALT = "2iAFCw0i"; //testCreds
	@Value("${PAYU_TRANS_URL}")
	private String PAYU_TRANS_URL;
	
	//private final String PAYU_TRANS_URL = "https://test.payu.in/_payment"; //testCreds
	@Value("${PAYU_TRANS_STATUS_URL}")
	private String PAYU_TRANS_STATUS_URL;
	
	public static ExceptionHelper exceptionHelper = new ExceptionHelper();
	
	private final String paymentOption = "payu";
	
	
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
            while (hashtext.length() < 128) { 
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
	
	
	@Override
	public String generateCheckSum(TransactionsBean transactionsBean,String returnUrl) {
		try {
			String data = PAYU_SECRET_KEY + "|" + transactionsBean.getTrack_id() + "|" + transactionsBean.getAmount() + "|" + transactionsBean.getDescription() + "|" + transactionsBean.getFirst_name() + "|" + transactionsBean.getEmail_id() + "|||||||||||" + PAYU_SALT;
			String checksum = generatePayuCheckSumSHA512(data);
			transactionsBean.setSecure_hash(checksum);
			return "true";
		}
		catch (Exception e) {
			exceptionHelper.createLog(e);
			return e.getMessage();
		}
	}

	@Override
	public ModelAndView createModelAndViewData(TransactionsBean transactionsBean,String returnUrl) {
		ModelAndView modelAndView = new ModelAndView("payuPay");
		modelAndView.addObject("trans_url", PAYU_TRANS_URL);
		modelAndView.addObject("key", PAYU_SECRET_KEY);
		modelAndView.addObject("productinfo", transactionsBean.getDescription());
		modelAndView.addObject("firstname",transactionsBean.getFirst_name());
		modelAndView.addObject("txnid",transactionsBean.getTrack_id());
		modelAndView.addObject("email",transactionsBean.getEmail_id());
		modelAndView.addObject("phone",transactionsBean.getMobile());
		modelAndView.addObject("amount", transactionsBean.getAmount());
		modelAndView.addObject("productinfo", transactionsBean.getDescription());
		modelAndView.addObject("surl", returnUrl);
		modelAndView.addObject("furl", returnUrl);
		modelAndView.addObject("hash", transactionsBean.getSecure_hash());
		return modelAndView;
	}

	@Override
	public boolean verifyCheckSum(HttpServletRequest request) {
		try {
			String data = PAYU_SALT + "|" + request.getParameter("status") + "|||||||||||" + request.getParameter("email") + "|" + request.getParameter("firstname") + "|" + request.getParameter("productinfo") + "|" + request.getParameter("amount") + "|" + request.getParameter("txnid") + "|" + request.getParameter("key");
			String checksum = generatePayuCheckSumSHA512(data);
			if(checksum.equalsIgnoreCase(request.getParameter("hash"))) {
				return true;
			}
			return false;
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
			//String sapid = (String) request.getSession().getAttribute("userId");
			//String trackId = (String) request.getSession().getAttribute("trackId");
			
			//session_transactionsBean.setSapid(sapid);
			//session_transactionsBean.setTrack_id(trackId);
			session_transactionsBean.setPayment_id(request.getParameter("bank_ref_num"));
			session_transactionsBean.setBank_name(request.getParameter("bankcode"));
			session_transactionsBean.setMerchant_ref_no(request.getParameter("txnid"));
			session_transactionsBean.setSecure_hash(request.getParameter("hash"));
			session_transactionsBean.setResponse_message(request.getParameter("status"));
			session_transactionsBean.setResponse_code(request.getParameter("error"));
			session_transactionsBean.setResponse_transaction_date_time(new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(Calendar.getInstance().getTime()));
			session_transactionsBean.setResponse_amount(request.getParameter("net_amount_debit"));
			session_transactionsBean.setResponse_payment_method(request.getParameter("mode"));
			session_transactionsBean.setTransaction_id(request.getParameter("mihpayid"));
			session_transactionsBean.setError(request.getParameter("field9"));
			//request.getSession().setAttribute("transactionsBean",session_transactionsBean);
			return session_transactionsBean;
		}
		catch (Exception e) {
			exceptionHelper.createLog(e);
			return null;
		}
	}

	
	public JsonObject getTransactionStatus(String track_id) {
		try {
			String msg = PAYU_SECRET_KEY + "|verify_payment|" + track_id + "|" + PAYU_SALT;
			String checkSum = generatePayuCheckSumSHA512(msg);
			String postData = "key="+ PAYU_SECRET_KEY + "&command=verify_payment&hash=" + checkSum + "&var1=" + track_id;
			
			URL obj = new URL(PAYU_TRANS_STATUS_URL);
			HttpsURLConnection con = (HttpsURLConnection) obj.openConnection();
			con.setRequestMethod("POST");
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
			JsonParser parser = new JsonParser();
			JsonObject responseData = parser.parse(response.toString()).getAsJsonObject();
			return responseData;
		}
		catch (Exception e) {
			exceptionHelper.createLog(e);
			return null;
		}
		
	}

	@Override
	public String checkErrorInTransaction(HttpServletRequest request) {
		
		TransactionsBean transactionsBean_session = (TransactionsBean) request.getSession().getAttribute("transactionsBean");
		
		try {
			//trackId check
			if(transactionsBean_session.getTrack_id() == null) {
				return "Error in processing payment. Error: TrackId session expired";
			}
			if(!transactionsBean_session.getTrack_id().equals(request.getParameter("txnid"))) {
				return "Error in processing payment. Error: TrackId not matching with transaction Id";
			}
			//isSuccess
			if(!"success".equalsIgnoreCase(request.getParameter("status"))) {
				return "Error in processing payment. Error:  " + request.getParameter("error");
			}
			//checksum verify
			boolean verificationCheckSum = verifyCheckSum(request);
			if(!verificationCheckSum) {
				exceptionHelper.createInfoLog("Tampering in response found. Track ID: " + transactionsBean_session.getTrack_id());
				return "Error in processing payment. Error: Hashvalue not matching. Tampering in response found. Track ID: " + transactionsBean_session.getTrack_id();
			}
			//amount check
			if(Float.parseFloat(transactionsBean_session.getAmount()) != Float.parseFloat(request.getParameter("net_amount_debit"))) {
				exceptionHelper.createInfoLog("Fees " + transactionsBean_session.getAmount() + " not matching with amount paid ");
				return "Error in processing payment. Error: Fees " + transactionsBean_session.getAmount() + " not matching with amount paid ";
			}
			return "true";
		}
		catch (Exception e) {
			exceptionHelper.createLog(e);
			return "Error in processing payment. Error: " + e.getMessage();
		}
		
	}

	@Override
	public JsonObject refundInitiate(String tracking_id, String transaction_id, String refund_amount) {
		try {
			String formaction = "cancel_refund_transaction";
			String refId = tracking_id + System.currentTimeMillis();	//unique refId for refund payTm
			String msg = PAYU_SECRET_KEY + "|"+ formaction +"|" + transaction_id + "|" + PAYU_SALT;
			String checkSum = generatePayuCheckSumSHA512(msg);
			String postData = "key="+ PAYU_SECRET_KEY + "&command="+ formaction +"&hash=" + checkSum + "&var1=" + transaction_id + "&var2=" + refId + "&var3=" + refund_amount;
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
		    responseData.addProperty("refId", refId);
			return responseData;
		}
		catch (Exception e) {
			exceptionHelper.createLog(e);
			return null;
		}
	}

	@Override
	public JsonObject refundStatus(String tracking_id, String refId) {
		
		try {
			
			String formaction = "check_action_status";
			String msg = PAYU_SECRET_KEY + "|"+ formaction +"|" + refId + "|" + PAYU_SALT;
			String checkSum = generatePayuCheckSumSHA512(msg);
			String postData = "key="+ PAYU_SECRET_KEY + "&command="+ formaction +"&hash=" + checkSum + "&var1=" + refId;
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
			return responseData;
			
		}
		catch (Exception e) {
			exceptionHelper.createLog(e);
			return null;
		}
		
		
	}

	@Override
	public TransactionsBean getTransactionStatus(TransactionsBean transactionsBean, List<String> paymentStatus) {
		// TODO Auto-generated method stub
		transactionsBean.setTransaction_status(paymentStatus.get(3));
		transactionsBean.setError("Payment logic inprocess");
		return transactionsBean;
	}
	
}
