package com.nmims.helpers;

import java.io.IOException;
import java.io.StringReader;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.TreeMap;

import javax.servlet.http.HttpServletRequest;

import org.jdom.Document;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.jdom.input.SAXBuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.nmims.beans.MBAPaymentRequest;
import com.nmims.beans.StudentExamBean;

@Component
public class MBAHdfcHelper {


	@Value( "${SECURE_SECRET}" )
	private String SECURE_SECRET; // secret key;
	
	@Value( "${ACCOUNT_ID}" )
	private String ACCOUNT_ID;

	@Value("${V3URL}")
	private String V3URL;
	
	public String generateChecksum(MBAPaymentRequest paymentRequest, StudentExamBean student) {
		try {
		    // retrieve all the parameters into a hash map
			fillPaymentParametersInMap(student, paymentRequest);
			Map<String, String> requestFields = new TreeMap<String, String>(paymentRequest.getFormParameters());
			String hashedvalue = getGeneratedHash(paymentRequest.getFormParameters());
		    requestFields.put("secure_hash", hashedvalue);
		    paymentRequest.setFormParameters(requestFields);
		    return "true";
		}catch (Exception e) {
			
		}
		return "Error generating checksum";
	}

	private void fillPaymentParametersInMap(StudentExamBean student, MBAPaymentRequest paymentRequest) {

		String address = student.getAddress();
		
		Map<String, String> paymentParameters = new HashMap<String, String>();
		
		if (address == null || address.trim().length() == 0) {
			address = "Not Available";
		} else if (address.length() > 200) {
			address = address.substring(0, 200);
		}
		String city = student.getCity();
		if (city == null || city.trim().length() == 0) {
			city = "Not Available";
		}

		String pin = student.getPin();
		if (pin == null || pin.trim().length() == 0) {
			pin = "400000";
		}

		String mobile = student.getMobile();
		if (mobile == null || mobile.trim().length() == 0) {
			mobile = "0000000000";
		}

		String emailId = student.getEmailId();
		if (emailId == null || emailId.trim().length() == 0) {
			emailId = "notavailable@email.com";
		}
		paymentParameters.put("udf1", paymentRequest.getDescription());
		paymentParameters.put("account_id", ACCOUNT_ID);
		paymentParameters.put("address", address);
		paymentParameters.put("algo", "MD5");
		paymentParameters.put("amount", paymentRequest.getAmount());
		paymentParameters.put("channel", "10");
		paymentParameters.put("city", city);
		paymentParameters.put("country", "IND");
		paymentParameters.put("currency", "INR");
		paymentParameters.put("currency_code", "INR");
		paymentParameters.put("description", paymentRequest.getDescription());
		paymentParameters.put("email", emailId);
		paymentParameters.put("mode", "LIVE");
		paymentParameters.put("name", student.getFirstName() + " " + student.getLastName());
		paymentParameters.put("orderId", paymentRequest.getTrackId());
		paymentParameters.put("phone", mobile);
		paymentParameters.put("postal_code", pin);
		paymentParameters.put("reference_no", paymentRequest.getTrackId());
		paymentParameters.put("return_url", paymentRequest.getCallbackURL());
		paymentParameters.put("studentNumber", student.getSapid());

		paymentRequest.setTransactionUrl(V3URL);
		paymentRequest.setFormParameters(paymentParameters);
	}


	public String checkErrorInHdfcPayment(HttpServletRequest request, MBAPaymentRequest paymentRequest) {
		
		String errorMessage = null;
//		String trackId = (String) request.getSession().getAttribute("trackId");
		String amount = paymentRequest.getAmount();
		
		boolean isHashMatching = isHashMatching(request);

		boolean isAmountMatching = isAmountMatching(request, amount);
		boolean isSuccessful = isTransactionSuccessful(request);
		
		/*boolean isHashMatching = true;
		boolean isTrackIdMatching = true;
		boolean isAmountMatching = false;
		boolean isSuccessful = true;*/
		
	/*	if(!"Wallet".equals(typeOfPayment)){
			 isHashMatching = isHashMatching(request);
			 isAmountMatching = isAmountMatching(request, amount);
			 isTrackIdMatching = isTrackIdMatching(request, trackId);
		}*/
		
		
		if (!isSuccessful) {
			errorMessage = "Error in processing payment."
					+ " Error: " + request.getParameter("Error") 
					+ " Code: " + request.getParameter("ResponseCode");
		}

		if (!isHashMatching) {
			errorMessage = "Error in processing payment. Error: Hashvalue not matching. Tampering in response found. ";
		}

		if (!isAmountMatching) {
			errorMessage = "Error in processing payment. Error: Fees " + amount + " not matching with amount paid " + request.getParameter("Amount");
		}

//		if (!isTrackIdMatching) {
//			errorMessage = "Error in processing payment. "
//					+ "Error: Track ID: " + trackId + " not matching with Merchant Ref No. " + request.getParameter("MerchantRefNo");
//		}
		return errorMessage;
	}
	
	public String getTransactionStatus(String trackId, MBAPaymentRequest mbawxPaymentRequest) {
		try {
			XMLParser parser = new XMLParser();
			String xmlResponse = parser.queryTransactionStatus(trackId, ACCOUNT_ID, SECURE_SECRET);
			parseResponse(xmlResponse, mbawxPaymentRequest);
			SAXBuilder saxBuilder = new SAXBuilder();
			Document doc = saxBuilder.build(new StringReader(xmlResponse));
			Element root = doc.getRootElement();
			return root.getAttributeValue("error");
		}
		catch (Exception e) {
			
			return null;
		}
	}
	
	private void parseResponse(final String xmlResponse, MBAPaymentRequest bean){
		SAXBuilder saxBuilder = new SAXBuilder();
		try {
			org.jdom.Document doc = saxBuilder.build(new StringReader(xmlResponse));
			Element root = doc.getRootElement();
			String transactionId = root.getAttributeValue("transactionId");
			String paymentId = root.getAttributeValue("paymentId");
			String amount = root.getAttributeValue("amount");
			String dateTime = root.getAttributeValue("dateTime");
			String mode = root.getAttributeValue("mode");
			String referenceNo = root.getAttributeValue("referenceNo");
			String transactionType = root.getAttributeValue("transactionType");
			String status = root.getAttributeValue("status");
			String isFlagged = root.getAttributeValue("isFlagged");


			String errorCode = root.getAttributeValue("errorCode");
			String error = root.getAttributeValue("error");
			
			if(error == null){
				bean.setTransactionID(transactionId);
				bean.setPaymentID(paymentId);
				bean.setRespAmount(amount);
				bean.setRespTranDateTime(dateTime);
				bean.setMerchantRefNo(referenceNo);
				bean.setIsFlagged(isFlagged);
			}else{
				bean.setError(error);
			}

			if(("Authorized".equalsIgnoreCase(transactionType) || "Captured".equalsIgnoreCase(transactionType))&& "Processed".equalsIgnoreCase(status)){
				bean.setSuccessFromGateway(true);
//			}else if("AuthFailed".equalsIgnoreCase(transactionType)&& "Processed".equalsIgnoreCase(status)){
//				
//				transactionFailedExamBookings.add(bean);
//			}else if("3".equals(errorCode) && ("Invalid Refrence No".equals(error) || "Invalid Reference No".equals(error))){
//				transactionFailedExamBookings.add(bean);
			}
		} catch (JDOMException e) {
			
		} catch (IOException e) {
			
		}
	}

	private boolean isTransactionSuccessful(HttpServletRequest request) {
		String error = request.getParameter("Error");
		// Error parameter should be absent to call it successful
		if (error == null) {
			// Response code should be 0 to call it successful
			String responseCode = request.getParameter("ResponseCode");
			if ("0".equals(responseCode)) {
				return true;
			} else {
				return false;
			}
		} else {
			return false;
		}
	}

	private boolean isAmountMatching(HttpServletRequest request, String totalFees) {
		try {
			double feesSent = Double.parseDouble(totalFees);
			double amountReceived = Double.parseDouble(request.getParameter("Amount"));
			
			if (feesSent == amountReceived) {
				return true;
			} else {
				return false;
			}
		} catch (Exception e) {
			
		}
		return false;
	}
	
	private boolean isHashMatching(HttpServletRequest request) {
		try {
			HashMap<String, String> testMap = new HashMap<String, String>();
			Enumeration<String> en = request.getParameterNames();

			while (en.hasMoreElements()) {
				String fieldName = (String) en.nextElement();
				String fieldValue = request.getParameter(fieldName);

				if ((fieldValue != null) && (fieldValue.length() > 0)) {
					testMap.put(fieldName, fieldValue);
				}
			}

			// Sort the HashMap
			Map<String, String> requestFields = new TreeMap<>(testMap);

			String hashedvalue = getGeneratedHash(requestFields);
			String receivedHashValue = request.getParameter("SecureHash");
			
			if (receivedHashValue != null && receivedHashValue.equals(hashedvalue)) {
				return true;
			} else {
				return false;
			}
		} catch (Exception e) {
			
		}
		return false;
	}
	
	private String getGeneratedHash(Map<String, String> requestFields) throws Exception {
		String md5HashData = SECURE_SECRET;
		
//		String V3URL = (String) requestFields.remove("V3URL");
		requestFields.remove("submit");
		requestFields.remove("SecureHash");

		Map<String, String> sortedMap = new TreeMap<String, String>(requestFields);
		for (Iterator<String> i = sortedMap.keySet().iterator(); i.hasNext();) {

			String key = i.next();
			String value = sortedMap.get(key);
			md5HashData += "|" + value;

		}
		String hashedValue = md5(md5HashData);
		
		return hashedValue;
	}
	
	public void createHdfcResponseBean(HttpServletRequest request, MBAPaymentRequest bean) {
		// Make the booking request bean to insert relevant data back into the database;
		bean.setResponseMessage(request.getParameter("ResponseMessage"));
		bean.setTransactionID(request.getParameter("TransactionID"));
		bean.setRequestID(request.getParameter("RequestID"));
		bean.setMerchantRefNo(request.getParameter("MerchantRefNo"));
		bean.setSecureHash(request.getParameter("SecureHash"));
		bean.setRespAmount(request.getParameter("Amount"));
		bean.setRespTranDateTime(request.getParameter("DateCreated"));
		bean.setResponseCode(request.getParameter("ResponseCode"));
		bean.setRespPaymentMethod(request.getParameter("PaymentMethod"));
		bean.setPaymentID(request.getParameter("PaymentID"));
	}
	
	public String md5(String str) throws Exception {
		MessageDigest m = MessageDigest.getInstance("MD5");

		byte[] data = str.getBytes();
		m.update(data, 0, data.length);
		BigInteger i = new BigInteger(1, m.digest());
		String hash = String.format("%1$032X", i);

		return hash;
	}
}
