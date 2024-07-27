package com.nmims.paymentgateways.helper;

import java.security.SignatureException;
import java.text.MessageFormat;
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
import javax.xml.bind.DatatypeConverter;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.ModelAndView;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.nmims.paymentgateways.bean.TransactionStatusBean;
import com.nmims.paymentgateways.bean.TransactionsBean;
import com.nmims.paymentgateways.interfaces.PaymentInterface;
import com.razorpay.Order;
import com.razorpay.Payment;
import com.razorpay.RazorpayClient;
import com.razorpay.RazorpayException;
import com.razorpay.Refund;

/**
 * To Help Process razorpay callback transactions and well as helper methods to
 * provide razorpay specific support such as getting transaction status,
 * iniating refund
 * 
 * @author Swarup Singh Rajpurohit
 * @since Razorpay Itegration Oct 2022
 */
@Component
public class RazorpayHelper implements PaymentInterface {
	@Value("${RAZOR_PAY_KEY_ID}")
	private String RAZORPAY_KEY_ID;

	@Value("${RAZOR_PAY_SECRET_KEY}")
	private String RAZORPAY_SECRET_KEY;

	@Value("${RAZOR_PAY_TRAN_URL}")
	private String TRAN_URL;

	@Value("${PAYMENT_GATEWAY_CANCEL_URL}")
	private String CANCEL_URL;

	@Value("${PAYMENT_AWS_MAIL_RECEIVER}")
	private String AWS_MAIL_RECEIVER;

	@Value("${PAYMENT_AWS_MAIL_SENDER}")
	private String AWS_MAIL_SENDER;

	@Value("${PAYMENT_SMS_NUMBERS}")
	private String SMS_NUMBERS;

	@Autowired
	private EmailAWSHelper emailHelper;

	@Autowired
	private SMSHelper smsHelper;

	private static final Logger logger = LoggerFactory.getLogger("razorpay_payments");

	private static final Logger paymentSchedulerLogger = LoggerFactory.getLogger("payment_scheduler");
	
	private static final String PAYMENTS_NOT_FOUND = "payments not found";

	private Gson gson = new Gson();

	public String calculateRFC2104HMAC(String data, String secret) throws java.security.SignatureException {
		String HMAC_SHA256_ALGORITHM = "HmacSHA256";
		String result;
		try {

			// get an hmac_sha256 key from the raw secret bytes
			SecretKeySpec signingKey = new SecretKeySpec(secret.getBytes(), HMAC_SHA256_ALGORITHM);

			// get an hmac_sha256 Mac instance and initialize with the signing key
			Mac mac = Mac.getInstance(HMAC_SHA256_ALGORITHM);
			mac.init(signingKey);

			// compute the hmac on input data bytes
			byte[] rawHmac = mac.doFinal(data.getBytes());

			// base64-encode the hmac
			result = DatatypeConverter.printHexBinary(rawHmac).toLowerCase();

		} catch (Exception e) {
			logger.error("Failed to generate HMAC : {}", e.getMessage());
			throw new SignatureException("Failed to generate HMAC : " + e.getMessage());
		}
		return result;
	}

	public Map<String, String> createOrder(String amount, String receipt) {
		// using track id as receipt
		logger.info("Inside Create Order : for track id  : {}", receipt);

		RazorpayClient client = null;
		JSONObject orderRequest = new JSONObject();
		Order order = null;
		Map<String, String> orderMap = new HashMap<>();

		// converting rupees to paise
		double amountInPaise = (Double.parseDouble(amount) * 100);

		try {

			client = new RazorpayClient(RAZORPAY_KEY_ID, RAZORPAY_SECRET_KEY);
			orderRequest.put("amount", amountInPaise);
			orderRequest.put("currency", "INR");
			orderRequest.put("receipt", receipt);
			order = client.orders.create(orderRequest);
			orderMap.put("orderId", order.get("id"));

		} catch (RazorpayException | JSONException e) {

			logger.error("Razorpay threw exception while creating order : {}", e);
			orderMap.put("error", e.getMessage());
			return orderMap;

		}

		logger.info("Order Created with orderId : {}, amount : {} paise and trackId/receipt : {} ", order.get("id"),
				amount, receipt);
		return orderMap;
	}

	@Override
	public String generateCheckSum(TransactionsBean transactionsBean, String returnUrl) {
		return null;
	}

	@Override
	public ModelAndView createModelAndViewData(TransactionsBean transactionsBean, String returnUrl) {
		ModelAndView modelAndView = new ModelAndView("razorpayPay");
		try {

			logger.info("Creating model and view for Razorpay Portal");

			// Amount is in paise so multiplying it with 100
			String amountInPaise = String.valueOf(Double.parseDouble(transactionsBean.getAmount()) * 100);

			modelAndView.addObject("amount", amountInPaise);
			modelAndView.addObject("TRAN_URL", TRAN_URL);
			modelAndView.addObject("key", RAZORPAY_KEY_ID);
//		modelAndView.addObject("currency", "INR");
			modelAndView.addObject("name", "NGASCE");
			modelAndView.addObject("description", transactionsBean.getDescription());
			modelAndView.addObject("order_id", transactionsBean.getTransaction_id());
			modelAndView.addObject("callback_url", returnUrl);
			modelAndView.addObject("cancel_url", CANCEL_URL);
//		modelAndView.addObject("name", transactionsBean.getFirst_name());
			modelAndView.addObject("contact", transactionsBean.getMobile());
			modelAndView.addObject("email", transactionsBean.getEmail_id());

			logger.info("Returning Razorpay model and view with model and view as : {}",
					modelAndView.getModel().toString());
		} catch (Exception e) {
			logger.info("error while generating model and view : {}", transactionsBean.getTransaction_id());
		}

		return modelAndView;
	}

	@Override
	public boolean verifyCheckSum(HttpServletRequest request) {

		String responseSignature = request.getParameter("razorpay_signature");
		String razorpayPaymentId = request.getParameter("razorpay_payment_id");
		String razorpayOrderId = request.getParameter("razorpay_order_id");
		String generatedSignature = null;

		logger.info("signature received from razorpay : {}, payment id : {} and orderid : {}", responseSignature,
				razorpayPaymentId, razorpayOrderId);

		TransactionsBean transactionsBean = (TransactionsBean) request.getSession().getAttribute("transactionsBean");

		try {
			generatedSignature = calculateRFC2104HMAC(razorpayOrderId + "|" + razorpayPaymentId, RAZORPAY_SECRET_KEY);
		} catch (SignatureException e) {
			logger.error("Error while generating checksum : {}", e.getMessage());
			return false;
		}

		if (generatedSignature.equals(responseSignature))
			return true;

		return false;
	}

	@Override
	public TransactionsBean createResponseBean(HttpServletRequest request) {
		logger.info("Creating respone bean");

		TransactionsBean session_transactionsBean = (TransactionsBean) request.getSession()
				.getAttribute("transactionsBean");

		session_transactionsBean.setSecure_hash((String) request.getSession().getAttribute("signature"));
		String paymentId = (String) request.getSession().getAttribute("paymentId");

		// getting order and payment from callback url payload
		JSONObject order = getOrderByReceipt(session_transactionsBean.getTrack_id());
		JSONObject payment = getCapturedPaymentEntity(paymentId);

		if (payment.has("error")) {
			logger.info(
					"Error while capturing payment using Razorpay SDK : {} \n fetching captured payments manually now",
					payment.get("error"));
			payment = getPaymentFromOrder(order);
		}
		try {
			// time and date format variables to convert unix to time and date
			Date time = new Date((long) payment.getInt("created_at") * 1000);
			SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

			if (payment.get("bank") != null)
				session_transactionsBean.setBank_name(payment.get("bank").toString());
			session_transactionsBean.setPayment_id(payment.getString("id"));
			session_transactionsBean.setMerchant_ref_no(session_transactionsBean.getTrack_id());
			session_transactionsBean.setSecure_hash(session_transactionsBean.getSecure_hash());

			if (("captured".equalsIgnoreCase(payment.getString("status"))
					|| "authorized".equalsIgnoreCase(payment.getString("status")))
					&& ("attempted".equalsIgnoreCase(order.getString("status"))
							|| "created".equalsIgnoreCase(order.getString("status")))) {

				logger.info(
						"deplay from razorpay to update order status so checking order status again for track id {}",
						session_transactionsBean.getTrack_id());
				logger.info("order status : {} and payment status : {}", order.getString("status"),
						payment.getString("status"));
				// deplay from razorpay to update order status so checking order status again
				order = getOrderByReceipt(session_transactionsBean.getTrack_id());

			}
			if ("attempted".equalsIgnoreCase(order.getString("status"))
					|| "created".equalsIgnoreCase(order.getString("status"))) {

				logger.info("order still not in paid status so sending back pending status {}",session_transactionsBean.getTrack_id());
				session_transactionsBean.setTransaction_status("Payment Pending");
				session_transactionsBean.setError("Payment initiated and is in pending state");
				logger.info("created following respones bean : {}", session_transactionsBean.toString());
				logger.info("Created response bean with payment id: {} and order id : {}", payment.get("id"),
						order.get("id"));
				logger.info("returning payment pending response to modules");
				return session_transactionsBean;

			}

			session_transactionsBean.setResponse_message(order.getString("status"));
			session_transactionsBean.setResponse_amount(String.valueOf(order.getDouble("amount_paid") / 100));
			session_transactionsBean.setResponse_transaction_date_time(dateFormat.format(time));
			session_transactionsBean.setResponse_payment_method(payment.getString("method"));
			session_transactionsBean.setTransaction_id(order.getString("id"));
			logger.info("created following respones bean : {}", session_transactionsBean.toString());
			logger.info("Created response bean with payment id: {} and order id : {}", payment.get("id"),
					order.get("id"));

		} catch (Exception e) {
			logger.info("Error while creating response bean : {}", e);
		}
		return session_transactionsBean;
	}

	private JSONObject getCapturedPaymentEntity(String paymentId) {
		logger.info("Trying to capture payment with paymentId : {}", paymentId);
		JSONObject paymentEntity = new JSONObject();
		try {

			RazorpayClient razorpayClient = new RazorpayClient(RAZORPAY_KEY_ID, RAZORPAY_SECRET_KEY);
			Payment payment = razorpayClient.payments.fetch(paymentId);
			paymentEntity = payment.toJson();

		} catch (RazorpayException e) {

			logger.error("Error while trying to capture payment by id : {}, {}", paymentId, e.getMessage());
			return paymentEntity.accumulate("error", e.getMessage());

		}
		logger.info("Inside Fetch payment by paymentId with payment entity : {}", paymentEntity.toString());
		return paymentEntity;
	}

	public JsonObject getTransactionStatus(String track_id) {

		logger.info("fetching transaction status for track id : {}", track_id);
		JSONObject payment = new JSONObject();

		// no exception handling since it's done in functions used below
		JSONObject order = getOrderByReceipt(track_id);

		payment = getPaymentFromOrder(order);
		payment.put("orderStatus", order.getString("status"));

		return gson.fromJson(payment.toString(), JsonObject.class);

	}

	// An order can have multiple payments which includes failed and captured ones
	// so separating out a method to get payment from order
	private JSONObject getPaymentFromOrder(JSONObject order) {

		JSONObject returnEntity = new JSONObject();
		JSONObject payment = new JSONObject();
		ArrayList<Integer> capturedTransaction = new ArrayList<>();

		if (order.has("error"))
			return returnEntity.accumulate("error", order.getString("error"));

		JSONArray jsonPaymentsArray = order.getJSONObject("payments").getJSONArray("items");
		int paymentLength = jsonPaymentsArray.length();

		// checking if there are any payments in the payment array
		if (paymentLength > 0) {
			logger.info("Number of payments inside order entity  : {}", paymentLength);

			for (int i = 0; i < paymentLength; i++) {
				JSONObject paymentEntity = jsonPaymentsArray.getJSONObject(i);
				if ("captured".equalsIgnoreCase(paymentEntity.getString("status"))) {
					capturedTransaction.add(i);
				}
			}

			// if more than one captured payments appeared it'll send a mail to the
			// developer
			if (capturedTransaction.size() > 0) {

				payment = (JSONObject) order.getJSONObject("payments").getJSONArray("items")
						.get(capturedTransaction.get(0));
				logger.info("Captured payment found {}", payment.toString());

				if (capturedTransaction.size() > 1) {
					logger.info(
							"Payment was done twice for this order id : {} \n sending mail and sms to concerning individuals : {}",
							order.getString("id"), AWS_MAIL_RECEIVER);
//					sendMailAndSMS(order, capturedTransaction.size());
				}

			} else {

				payment = (JSONObject) order.getJSONObject("payments").getJSONArray("items").get(paymentLength - 1);
				logger.info("No captured payments found so sending back failed payments {}", payment.toString());

			}

			return payment;
		} else {
			logger.info("no payments were made for this trackid");
			return returnEntity.accumulate("error", "payments not found");
		}

	}

	@Override
	public String checkErrorInTransaction(HttpServletRequest request) {
		TransactionsBean transactionsBean = (TransactionsBean) request.getSession().getAttribute("transactionsBean");
		// checking for callback url error parameter in request object
		if (request.getParameterMap().containsKey("error[description]")) {
			logger.info("Transaction failed for track id : {}, error : {} inside checkError in transaction method",
					transactionsBean.getTrack_id(), request.getParameter("error[description]"));
			return request.getParameter("error[description]");
		}

		String razorpayOrderId = request.getParameter("razorpay_order_id");

		// checking if trackid is null
		if (null == transactionsBean.getTrack_id() ) {
			logger.info("Error in processing payment. Error: orderId session expired");
			return "Error in processing payment. Error: orderId session expired";
		}

		// checking if orderid sent and received are one and the same
		if (!transactionsBean.getTransaction_id().equals(razorpayOrderId)) {
			logger.info("Tampering in response found. Track ID: {}", transactionsBean.getTrack_id());
			return "Tampering in response found. Track ID: " + transactionsBean.getTrack_id();
		}

		boolean signatureCheck = verifyCheckSum(request);
		if (!signatureCheck) {
			logger.info("Tampering in response found. Track ID: {}", transactionsBean.getTrack_id());
			return "Error in processing payment. Error: Hashvalue not matching. Tampering in response found. Track ID: "
					+ transactionsBean.getTrack_id();
		}

		request.getSession().setAttribute("signature", request.getParameter("razorpay_signature"));
		request.getSession().setAttribute("paymentId", request.getParameter("razorpay_payment_id"));

		return "true";
	}

	public JSONObject getOrderByReceipt(String receipt) {
		JSONObject returnObject = new JSONObject();
		logger.info("trying to fetch order by receipt : {}", receipt);
		if (receipt == null || receipt == "") {
			logger.info("Returning error JSONObject as receipt is null");
			return returnObject.accumulate("error", "No trackId provided.");
		}

		RazorpayClient razorpayClient = null;
		List<Order> orders = null;

		try {
			razorpayClient = new RazorpayClient(RAZORPAY_KEY_ID, RAZORPAY_SECRET_KEY);
			JSONObject jsonObject = new JSONObject();
			jsonObject.put("receipt", receipt);
			jsonObject.put("expand[]", "payments");
			orders = razorpayClient.orders.fetchAll(jsonObject);
		} catch (RazorpayException e) {
			logger.error("Error while fetching razorpay order : {}", e.getMessage());
			returnObject.put("error", e.getMessage());
			return returnObject;
		}

		logger.info("order object in get order by receipt : {} ", orders.toString());
		if (!orders.isEmpty())
			return (JSONObject) orders.get(0).toJson();

		logger.info("Error No order found for this track id");
		return returnObject.accumulate("error", "No order found for this track id");
	}

	@Override
	public JsonObject refundInitiate(String tracking_id, String transaction_id, String refund_amount) {
		logger.info("Trying to initiate refund for track id : {}", tracking_id);
		Refund refund = null;

		// two different json because interface forces us to use gson json
		// while razorpay internally uses JSONObject
		JsonObject returnObject = new JsonObject();
		JSONObject refundObject = new JSONObject();

		// null checking of all parameters to avoid exception0
		if (tracking_id == null || tracking_id == "" || transaction_id == null || transaction_id == ""
				|| refund_amount == null || refund_amount == "") {

			logger.info("Not sufficient data to initiate refund, track id = {}, paymentId = {}, and amount = {} ",
					tracking_id, transaction_id, refund_amount);
			returnObject.addProperty("error", "Not sufficient data to initiate refund, track id = " + tracking_id
					+ ", paymentId = " + transaction_id + " and amount = " + refund_amount);
			return returnObject;

		}
		try {

			RazorpayClient client = new RazorpayClient(RAZORPAY_KEY_ID, RAZORPAY_SECRET_KEY);
			refundObject.put("amount", (Double.parseDouble(refund_amount) * 100));
			refundObject.put("speed", "normal");
			refundObject.put("receipt", tracking_id);
			refund = client.payments.refund(transaction_id, refundObject);
			returnObject = gson.fromJson(refund.toString(), JsonObject.class);

		} catch (Exception e) {

			logger.error("Error while trying to initate refund : {}", e);
			returnObject.addProperty("error", e.getMessage());
			return returnObject;

		}
		if (returnObject.isJsonNull()) {
			returnObject.addProperty("error", "Error while initiating refund");
		}
		return returnObject;
	}

	public TransactionStatusBean getTransactionStatusByTrackId(String parameter) {

		logger.info("trying to get transaction status by track id : {}", parameter);
		JSONObject orderEntity = new JSONObject();
		JSONObject paymentEntity = new JSONObject();
		TransactionStatusBean tranasctionStatus = new TransactionStatusBean();

		orderEntity = getOrderByReceipt(parameter);
		paymentEntity = getPaymentFromOrder(orderEntity);

		if (paymentEntity.has("error")) {
			tranasctionStatus.setHasError(true);
			tranasctionStatus.setError(orderEntity.getString("error"));
			return tranasctionStatus;
		}

		String status = orderEntity.getString("status");
		logger.info("Status of order in question : {}", status);

		Date time = new Date((long) (paymentEntity.getInt("created_at")) * 1000);
		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

		if ("paid".equalsIgnoreCase(status) && "captured".equalsIgnoreCase(paymentEntity.getString("status"))) {

			tranasctionStatus.setTrackId(orderEntity.getString("receipt"));
			tranasctionStatus.setPaymentId(paymentEntity.getString("id"));
			tranasctionStatus.setAmount(String.valueOf(orderEntity.getDouble("amount") / 100));
			tranasctionStatus.setCreatedAt(dateFormat.format(time));
			tranasctionStatus.setMerchantRefNo(orderEntity.getString("receipt"));
			tranasctionStatus.setTransactionMethod(paymentEntity.getString("method"));
			tranasctionStatus.setOrderStatus(status);
			tranasctionStatus.setPaymentStatus(paymentEntity.getString("status"));

			// bank is null for UPI transactions
			if (paymentEntity.get("bank") != null)
				tranasctionStatus.setBank(paymentEntity.get("bank").toString());

			logger.info("Order was paid so bean was made for paid order : {}", tranasctionStatus.toString());

		} else if ("attempted".equalsIgnoreCase(status)
				&& "failed".equalsIgnoreCase(paymentEntity.getString("status"))) {

			tranasctionStatus.setOrderStatus(status);
			tranasctionStatus.setPaymentStatus(paymentEntity.getString("status"));
			tranasctionStatus.setError(paymentEntity.getString("error_description"));
			logger.info("Order was attempted /  failed so reason : {}", paymentEntity.getString("error_description"));

		}
		return tranasctionStatus;
	}

	@Override
	public JsonObject refundStatus(String tracking_id, String refId) {
		logger.info("trying to get refund status for refund id : {}", refId);
		JsonObject refundStatusEntity = new JsonObject();
		Refund refund = null;

		try {
			// another refund API that
			// Refund refund = client.payments.fetchRefund(tracking_id, refId);

			RazorpayClient client = new RazorpayClient(RAZORPAY_KEY_ID, RAZORPAY_SECRET_KEY);
			refund = client.refunds.fetch(refId);
			refundStatusEntity = gson.fromJson(refund.toString(), JsonObject.class);

		} catch (RazorpayException e) {

			logger.info("Error while getting razorpay refund : {}", e.getMessage());
			refundStatusEntity.addProperty("error", e.getMessage());
			return refundStatusEntity;

		}
		logger.info("Got refund entity : {}", refundStatusEntity.toString());
		return refundStatusEntity;
	}

	public TransactionStatusBean initiateRefund(String track_id, String transaction_id, String amount) {
		TransactionStatusBean bean = new TransactionStatusBean();
		JsonObject jsonObject = new JsonObject();

		jsonObject = refundInitiate(track_id, transaction_id, amount);

		if (jsonObject.has("error")) {
			bean.setHasError(true);
			bean.setError(jsonObject.get("error").getAsString());
			return bean;
		}

		String refundStatus = jsonObject.get("status").getAsString();
		logger.info("refund status for track id {}  : {}", track_id, refundStatus);

		if ("processed".equalsIgnoreCase(refundStatus)) {

			bean.setHasError(false);
			bean.setRefundId(jsonObject.get("id").getAsString());
			bean.setTrackId(jsonObject.get("receipt").getAsString());
			bean.setRefundStatus(refundStatus);

		} else if ("failed".equalsIgnoreCase(refundStatus)) {
			bean.setHasError(false);
			bean.setError("Transaction Failed");
			bean.setRefundStatus(refundStatus);

		} else if ("pending".equalsIgnoreCase(refundStatus)) {

			bean.setHasError(false);
			bean.setRefundId(jsonObject.get("id").getAsString());
			bean.setTrackId(jsonObject.get("receipt").getAsString());
			bean.setRefundStatus(refundStatus);

		} else {

			bean.setHasError(true);
			bean.setError("No response from razorpay");
			logger.info("No response from razorpay");

		}
		logger.info("returning {}", bean.toString());
		return bean;
	}

	public void sendMailAndSMS(JSONObject order, int paymentLenght) {

		Map<String, Object> requestBody = new HashMap<>();

		String track_id = order.getString("receipt");

		String mailAndSMSBody = MessageFormat.format(
				"More than one Payment for track id : {0}, with amount : {1} has been successful {2} times with Razorpay orderid = {3}",
				track_id, (order.getDouble("amount") / 100), paymentLenght, order.getString("id"));

		requestBody.put("sender", AWS_MAIL_SENDER);
		requestBody.put("subject", "More than one payment success for this track id : " + track_id);
		requestBody.put("htmlbody", mailAndSMSBody);
		requestBody.put("recipient", Arrays.asList(AWS_MAIL_RECEIVER));
//		requestBody.put("bcc", "placeholder@gmail.com");
//		requestBody.put("cc", "placeholder@gmail.com");

		try {
			logger.info("sending mail  to {} from {}, content : {} ", AWS_MAIL_RECEIVER, AWS_MAIL_SENDER,
					"More than one payment success for this track id : " + track_id);
			emailHelper.sendMail(requestBody);
		} catch (Exception e) {
			logger.info("error while sending mail {}", e);
		}

		try {
			smsHelper.sendSMS(mailAndSMSBody, SMS_NUMBERS);
		} catch (Exception e) {
			logger.info("Error while sending message : {}", e.getMessage());
		}

	}

	public TransactionStatusBean getRefundStatus(String merchantRefNo, String refundId) {

		TransactionStatusBean refundBean = new TransactionStatusBean();

		if (refundId == null || refundId == "") {
			refundBean.setHasError(true);
			refundBean.setError("Refund id is null");
			logger.info("Refund id is null.");
			return refundBean;
		}

		JsonObject refundEntity = refundStatus(merchantRefNo, refundId);

		if (refundEntity.has("error")) {

			refundBean.setHasError(true);
			refundBean.setError(refundEntity.get("error").getAsString());
			logger.info("Error : {}", refundEntity.get("error").getAsString());
			return refundBean;

		}

		String STATUS = refundEntity.get("status").getAsString();
		refundBean.setRefundStatus(STATUS);
		logger.info("Refund with refund id : {} has status : {}", refundEntity.get("id").getAsString(), STATUS);
		return refundBean;

	}

	// we can manually capture payments that enter authorized state
	// this isn't in use for now but might need it in future
	public int capturePayment(String paymentId, double amount, String currency) {

		logger.info("Attempting to capture payment with payment id : {}, amount : {} and currency : {}", paymentId,
				amount, currency);

		JSONObject paymentRequest = new JSONObject();

		try {

			RazorpayClient client = new RazorpayClient(RAZORPAY_KEY_ID, RAZORPAY_SECRET_KEY);
			paymentRequest.put("amount", amount);
			paymentRequest.put("currency", currency);
			JSONObject payment = client.payments.capture(paymentId, paymentRequest).toJson();
			logger.info("captured payment its status is {}", payment.getString("status"));

		} catch (RazorpayException e) {
			logger.error("error while trying to capture payment : {}", e.getMessage());
		}
		return 1;
	}

	@Override
	public TransactionsBean getTransactionStatus(TransactionsBean transactionsBean, List<String> paymentStatus) {

		JSONObject orderEntity = new JSONObject();
		JSONObject paymentEntity = new JSONObject();

		paymentSchedulerLogger.info("Fetching transaction for track : {} with razorpay option",
				transactionsBean.getTrack_id());

		orderEntity = getOrderByReceipt(transactionsBean.getTrack_id());

		paymentSchedulerLogger.info("Recieved Order : {}", orderEntity.toString());

		paymentEntity = getPaymentFromOrder(orderEntity);

		paymentSchedulerLogger.info("received payment from order : {}", paymentEntity.toString());

		if (paymentEntity.has("error")) {
			if(PAYMENTS_NOT_FOUND.equalsIgnoreCase(paymentEntity.getString("error"))) {
				transactionsBean.setError(paymentEntity.getString("error"));
				transactionsBean.setTransaction_status(paymentStatus.get(2));
				return transactionsBean;
			}
			paymentSchedulerLogger.info("Error while extracting payment or getting order : {}",
					paymentEntity.getString("error"));
			transactionsBean.setError(paymentEntity.getString("error"));
			transactionsBean.setTransaction_status(paymentStatus.get(3));
			return transactionsBean;
		}

		String status = orderEntity.getString("status");
		paymentSchedulerLogger.info("Status of order in question : {}", status);

		try {
			Date time = new Date((long) (paymentEntity.getInt("created_at")) * 1000);
			SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

			if ("paid".equalsIgnoreCase(status) && "captured".equalsIgnoreCase(paymentEntity.getString("status"))) {
				String orderId = orderEntity.getString("id");
				String paymentId = paymentEntity.getString("id");

				String secureHash = calculateRFC2104HMAC(orderId + "|" + paymentId, RAZORPAY_SECRET_KEY);

				transactionsBean.setSecure_hash(secureHash);

				transactionsBean.setTransaction_id(orderId);
				transactionsBean.setPayment_id(paymentId);
				transactionsBean.setResponse_amount(String.valueOf(orderEntity.getDouble("amount") / 100));
				transactionsBean.setCreated_at(dateFormat.format(time));
				transactionsBean.setMerchant_ref_no(orderEntity.getString("receipt"));
				transactionsBean.setResponse_payment_method(paymentEntity.getString("method"));
				transactionsBean.setTransaction_status(paymentStatus.get(1));
				transactionsBean.setResponse_message(status);
				transactionsBean.setResponse_transaction_date_time(dateFormat.format(time));

				if (paymentEntity.get("bank") != null)
					transactionsBean.setBank_name(paymentEntity.get("bank").toString());

				paymentSchedulerLogger.info("Order was paid so bean was made for paid order");
			} else if ("attempted".equalsIgnoreCase(status)
					&& "failed".equalsIgnoreCase(paymentEntity.getString("status"))) {

				transactionsBean.setTransaction_status(paymentStatus.get(2));
				transactionsBean.setError(paymentEntity.getString("error_description"));
				paymentSchedulerLogger.info("Order was attempted /  failed so reason : {}",
						paymentEntity.getString("error_description"));

			} else if ("attempted".equalsIgnoreCase(status)
					&& ("created".equalsIgnoreCase(paymentEntity.getString("status"))
							|| "authorized".equalsIgnoreCase(paymentEntity.getString("status")))) {

				transactionsBean.setError(paymentEntity.getString("status"));
				transactionsBean.setTransaction_status(paymentStatus.get(3));

			} else {
				paymentSchedulerLogger.info("Invalid conditional status : {}", status);
				transactionsBean.setError("Invalid conditional status : " + status);
				transactionsBean.setTransaction_status(paymentStatus.get(3));
			}
			return transactionsBean;
		} catch (Exception e) {
			paymentSchedulerLogger.info("Error occurred while getting transaction status : {}", e.getMessage());
			transactionsBean.setError(e.getMessage());
			transactionsBean.setTransaction_status(paymentStatus.get(3));
			return transactionsBean;
		}
	}
}
