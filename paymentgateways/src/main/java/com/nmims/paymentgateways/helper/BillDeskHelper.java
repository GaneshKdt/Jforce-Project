package com.nmims.paymentgateways.helper;

import java.security.MessageDigest;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.servlet.ModelAndView;

import com.google.gson.JsonObject;
import com.nmims.paymentgateways.bean.TransactionsBean;
import com.nmims.paymentgateways.interfaces.PaymentInterface;

/**
 * Billdesk Helper Class to perform Billdesk specific business logic and
 * provide functionalities to service layer
 * 
 * @since Billdesk Integration Oct 2022
 */
@Component
public class BillDeskHelper implements PaymentInterface {

	@Value("${BILLDESK_NMIMSID}")
	private String BILLDESK_NMIMSID;

	@Value("${BILLDESK_SECURITY_ID}")
	private String BILLDESK_SECURITY_ID;

	@Value("${BILLDESK_CHECKSUM_KEY}")
	private String BILLDESK_CHECKSUM_KEY;

	@Value("${BILLDESK_TRANS_STATUS_URL}")
	private String BILLDESK_TRANS_STATUS_URL;

	@Value("${BILLDESK_TRANS_URL}")
	private String BILLDESK_TRANS_URL;

	private static final Logger logger = LoggerFactory.getLogger("billdesk_payments");

	private static final Logger schedulerLogger = LoggerFactory.getLogger("payment_scheduler");

	@Override
	public String generateCheckSum(TransactionsBean transactionsBean, String returnUrl) {
		logger.info("generating checksum");
		try {
			String body = BILLDESK_NMIMSID + "|" + transactionsBean.getTrack_id() + "|NA|"
					+ transactionsBean.getAmount() + "|NA|NA|NA|INR|NA|R|" + BILLDESK_SECURITY_ID + "|NA|NA|F|NA|NA|"
					+ transactionsBean.getDescription() + "|NA|NA|NA|NA|" + returnUrl;
			String checkSum = HmacSHA256(body, BILLDESK_CHECKSUM_KEY);
			transactionsBean.setSecure_hash(checkSum);
			return "true";
		} catch (Exception e) {
			logger.info("Error while generating checksum {}", e);
			return e.getMessage();
		}
	}

	public static String HmacSHA256(String message, String secret) {
		MessageDigest md = null;
		try {
			Mac sha256_HMAC = Mac.getInstance("HmacSHA256");
			SecretKeySpec secret_key = new SecretKeySpec(secret.getBytes(), "HmacSHA256");
			sha256_HMAC.init(secret_key);

			byte raw[] = sha256_HMAC.doFinal(message.getBytes());

			StringBuffer ls_sb = new StringBuffer();
			for (int i = 0; i < raw.length; i++)
				ls_sb.append(char2hex(raw[i]));
			return ls_sb.toString(); // step 6
		} catch (Exception e) {
			logger.info("Error while generating hmac256 {}", e);
			return null;
		}
	}

	// billdesk checksum
	public static String char2hex(byte x) {
		char arr[] = { '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'A', 'B', 'C', 'D', 'E', 'F' };
		char c[] = { arr[(x & 0xF0) >> 4], arr[x & 0x0F] };
		return (new String(c));
	}

	@Override
	public ModelAndView createModelAndViewData(TransactionsBean transactionsBean, String returnUrl) {
		logger.info("creating model and view for transaction {}", transactionsBean.toString());
		ModelAndView mv = new ModelAndView("billdeskPay");
		String body = BILLDESK_NMIMSID + "|" + transactionsBean.getTrack_id() + "|NA|" + transactionsBean.getAmount()
				+ "|NA|NA|NA|INR|NA|R|" + BILLDESK_SECURITY_ID + "|NA|NA|F|NA|NA|" + transactionsBean.getDescription()
				+ "|NA|NA|NA|NA|" + returnUrl + "|" + transactionsBean.getSecure_hash();
		mv.addObject("trans_url", BILLDESK_TRANS_URL);
		mv.addObject("msg", body);
		logger.info("created modelandview : {}", mv.getModelMap().toString());
		return mv;
	}

	@Override
	public boolean verifyCheckSum(HttpServletRequest request) {
		logger.info("verifying  checksum");
		try {
			String[] responseList = request.getParameter("msg").split("\\|");
			String message = "";
			for (int i = 0; i < (responseList.length - 1); i++) {
				if (i == (responseList.length - 2)) {
					message = message + responseList[i];
				} else {
					message = message + responseList[i] + "|";
				}

			}
			String checkSum = HmacSHA256(message, BILLDESK_CHECKSUM_KEY);
			logger.info("generated checksum : {}", checkSum);
			if (checkSum.equalsIgnoreCase(responseList[25])) {
				logger.info("checksum matched so continuing with process");
				return true;
			}
			logger.info("checksum verification failed!");
			return false;
		} catch (Exception e) {
			logger.info("Error while verifying checksum : {}", e);
			return false;
		}
	}

	@Override
	public TransactionsBean createResponseBean(HttpServletRequest request) {
		logger.info("Creating response bean");

		TransactionsBean bean = (TransactionsBean) request.getSession().getAttribute("transactionsBean");
		logger.info("response bean received from session : {}", bean.toString());
		String[] responseList = request.getParameter("msg").split("\\|");
		logger.info("extracted individual responses from response : {}", Arrays.toString(responseList));
		String responseDateTime = null;
		DateFormat format = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
		SimpleDateFormat sf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

		try {
			Date date = format.parse(responseList[13]);
			responseDateTime = sf.format(date);
		} catch (ParseException e) {
			logger.info("Error while parsing date : {}", e);
		}

		bean.setTransaction_id(responseList[2]);
		bean.setMerchant_ref_no(responseList[1]);
		bean.setResponse_amount(responseList[4]);
		bean.setResponse_transaction_date_time(responseDateTime);
		bean.setResponse_code(responseList[14]);
		bean.setResponse_payment_method(responseList[5]);
		bean.setPayment_id(responseList[6]);
		bean.setBank_name(responseList[5]);
		if ("0300".equalsIgnoreCase(bean.getResponse_code())) {
			bean.setResponse_message("Success");
		} else {
			bean.setResponse_message(responseList[24]);
		}

		logger.info("Created response bean : {}", bean.toString());
		return bean;
	}

	@Override
	public TransactionsBean getTransactionStatus(TransactionsBean bean, List<String> paymentStatus) {

		schedulerLogger.info("getting transaction status for bean : {}", bean.toString());
		String[] responseList = getResponseList(bean.getTrack_id());

		if ("0002".equalsIgnoreCase(responseList[15])) {
			bean.setError("Transaction in pending");
			bean.setTransaction_status(paymentStatus.get(3));
			schedulerLogger.info("Transaction still pending");
		} else if ("0300".equalsIgnoreCase(responseList[15])) {
			schedulerLogger.info("payment sucessful so creating bean");
			bean.setTransaction_status(paymentStatus.get(1));
			bean.setTransaction_id(responseList[4]);
			bean.setMerchant_ref_no(responseList[2]);
			bean.setResponse_code(responseList[15]);
			if (responseList[19] != null)
				bean.setDescription(responseList[19]);
			bean.setResponse_message("Success");
			bean.setResponse_amount(responseList[5]);
			bean.setResponse_payment_method(responseList[6]);
			bean.setPayment_id(responseList[7]);
			bean.setBank_name(responseList[6]);

			String responseDateTime = null;

			try {
				DateFormat format = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
				SimpleDateFormat sf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
				Date date = format.parse(responseList[14]);
				responseDateTime = sf.format(date);
			} catch (ParseException e) {
				schedulerLogger.info("error while parsing date : {}", e);
			}

			bean.setResponse_transaction_date_time(responseDateTime);

			schedulerLogger.info("created bean : {}", bean.toString());

		} else if ("0399".equalsIgnoreCase(responseList[15])) {
			bean.setError("Payment Failed");
			bean.setTransaction_status(paymentStatus.get(2));
			schedulerLogger.info("payment failed status code : 0399");
		} else if ("NA".equalsIgnoreCase(responseList[15])) {
			bean.setError("Error: [e.g. Txn not found/ Invalid checksum/ Invalid Request IP etc]");
			bean.setTransaction_status(paymentStatus.get(2));
			schedulerLogger.info("Error: [e.g. Txn not found/ Invalid checksum/ Invalid Request IP etc]");
		} else if ("0001".equalsIgnoreCase(responseList[15])) {
			bean.setError("Error at BillDesk");
			bean.setTransaction_status(paymentStatus.get(3));
			schedulerLogger.info("Error at BillDesk");
		} else {
			bean.setError("Invalid Response from billdesk");
			bean.setTransaction_status(paymentStatus.get(3));
			schedulerLogger.info("Invalid Response from billdesk");
		}
		return bean;
	}

	private String[] getResponseList(String trackId) {
		String[] responseList = new String[30];
		schedulerLogger.info("getting transaction details for track id : {}", trackId);

		try {
			SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMddHHmmss");
			Date date = new Date();

			String msg = "0122|" + BILLDESK_NMIMSID + "|" + trackId + "|" + formatter.format(date);
			schedulerLogger.info("created msg : {}", msg);
			String checkSum = HmacSHA256(msg, BILLDESK_CHECKSUM_KEY);
			schedulerLogger.info("generated checksum : {}", checkSum);
			msg = msg + "|" + checkSum.toUpperCase();
			schedulerLogger.info("Final request data to be sent to billdesk API : {}", msg);
			RestTemplate restTemplate = new RestTemplate();
			HttpHeaders headers = new HttpHeaders();
			headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

//			MultiValueMap<String, String> requestMap = new LinkedMultiValueMap<>();
//			requestMap.add("msg", msg);
//			
//			HttpEntity<String> request = new HttpEntity<>(requestMap, headers);
//			
			String requestData = "msg=" + msg;

			HttpEntity<String> request = new HttpEntity<String>(requestData, headers);
			schedulerLogger.info("request data wit header :  {}", requestData);
			ResponseEntity<String> response = restTemplate.postForEntity(BILLDESK_TRANS_STATUS_URL, request,
					String.class);
			schedulerLogger.info("received response from rest template call : {}", response.toString());

			String billdeskResponse = response.getBody();
			schedulerLogger.info("body in string : {}", billdeskResponse);
			responseList = billdeskResponse.split("\\|");
			schedulerLogger.info("after splitting : {}", Arrays.toString(responseList));
		} catch (Exception e) {
			schedulerLogger.info("Errow while getting transaction status : {}", e);
		}
		return responseList;
	}

	@Override
	public String checkErrorInTransaction(HttpServletRequest request) {
		logger.info("checking error in transaction");
		TransactionsBean transactionsBean_session = (TransactionsBean) request.getSession()
				.getAttribute("transactionsBean");
		String amount = transactionsBean_session.getAmount();
		String trackId = transactionsBean_session.getTrack_id();
		try {
			String[] responseList = request.getParameter("msg").split("\\|");
			if (!trackId.equals(responseList[1])) {
				logger.info("Error in processing payment. Error: Mismatch in trackId");
				return "Error in processing payment. Error: Mismatch in trackId";
			}
			if (!"0300".equalsIgnoreCase(responseList[14])) {
				logger.info("Error in processing payment. Error:  {} Code: {}", responseList[24], responseList[14]);
				return "Error in processing payment. Error:  " + responseList[24] + " Code: " + responseList[14];
			}
			if (Float.parseFloat(responseList[4]) != Float.parseFloat(amount)) {
				logger.info("Error in processing payment. Error: Fees {} not matching with amount paid ", amount);
				return "Error in processing payment. Error: Fees " + amount + " not matching with amount paid ";
			}
			boolean verificationCheckSum = verifyCheckSum(request);
			if (!verificationCheckSum) {
				logger.info(
						"Error in processing payment. Error: Hashvalue not matching. Tampering in response found. Track ID: {}",
						trackId);
				return "Error in processing payment. Error: Hashvalue not matching. Tampering in response found. Track ID: "
						+ trackId;
			}
			return "true";
		} catch (Exception e) {
			logger.info("Error in processing payment. Error: ", e.getMessage());
			return "Error in processing payment. Error: " + e.getMessage();
		}

	}

	@Override
	public JsonObject refundInitiate(String tracking_id, String transaction_id, String refund_amount) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public JsonObject refundStatus(String tracking_id, String refId) {
		// TODO Auto-generated method stub
		return null;
	}

}