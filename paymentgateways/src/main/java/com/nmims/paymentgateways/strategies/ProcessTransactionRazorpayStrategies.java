package com.nmims.paymentgateways.strategies;

import java.security.SignatureException;
import java.text.MessageFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.JsonNode;
import com.nmims.paymentgateways.bean.PayloadWrapper;
import com.nmims.paymentgateways.bean.TransactionsBean;
import com.nmims.paymentgateways.dao.TransactionDAO;
import com.nmims.paymentgateways.enums.WebhookEnum;
import com.nmims.paymentgateways.helper.RazorpayHelper;
import com.nmims.paymentgateways.helper.SMSHelper;
import com.nmims.paymentgateways.interfaces.ProcessTransactionInterface;

@Service
public class ProcessTransactionRazorpayStrategies implements ProcessTransactionInterface {

	@Value("${RAZOR_PAY_SECRET_KEY}")
	private String RAZORPAY_SECRET_KEY;

	@Value("${SERVER_PATH}")
	private String SERVER_PATH;

	@Value("${PAYMENT_SMS_NUMBERS}")
	private String SMS_NUMBERS;

	private JsonNode payload;

	@Autowired
	private RazorpayHelper razorpayHelper;

	private final String PAYMENT_SUCCESS = "Payment Successfull";
	private final String PAYMENT_FAILED = "Payment Failed";

	@Autowired
	SMSHelper smsHelper;

	// url of all modules reading from property file
	@Value("#{${WEBHOOK_MODULE_MAP}}")
	Map<String, String> WEBHOOK_MODULE_MAP;

	private static final Logger LOGGER = LoggerFactory.getLogger(WebhookEnum.RAZORPAY_WEBHOOK.getValue());

	@Autowired
	private TransactionDAO dao;

//	@Autowired
//	private Map<String, ProcessTransactionInterface> implementationMap;

	@Override
	public String processWebhookTransaction(PayloadWrapper wrapper, WebhookEnum implementation) {

		try {

			CompletableFuture<Boolean> daoUpdateResponse = null;

			this.payload = wrapper.getJsonData();

			String trackId = null;

			trackId = this.getTrackId();

			TransactionsBean transactionsBean = dao.getPaymentOptionByTrackId(trackId);

			this.checkErrorInPayload(transactionsBean, trackId);

			boolean isTransactionSuccess = false;

			isTransactionSuccess = this.isPaymentSuccess();

			if (isTransactionSuccess) {
				this.populateBeanForSuccessfulTransaction(transactionsBean);

				daoUpdateResponse = CompletableFuture.supplyAsync(() -> dao.markAsSuccessTransaction(transactionsBean));
			} else {
				this.populateBeanForFailedTransaction(transactionsBean);

				daoUpdateResponse = CompletableFuture.supplyAsync(() -> dao.markAsFailedTransaction(transactionsBean));
			}

			String url = SERVER_PATH + WEBHOOK_MODULE_MAP.get(transactionsBean.getType());

//			String url = "http://localhost:8080/" + WEBHOOK_MODULE_MAP.get(transactionsBean.getType());

			LOGGER.info("posting data to url : {} with body : " + transactionsBean.toString(), url);

			String APIResponse = this.postForTransactionUpdate(transactionsBean, url);

			LOGGER.info("API response for track id : {} : {} ", transactionsBean.getTrack_id(), APIResponse);

			if (!daoUpdateResponse.get())
				LOGGER.info("failed to mark transaction : {}", transactionsBean.getTrack_id());

		} catch (Exception e) {
			LOGGER.info("Error for track id : " + e.getMessage());
		}
		return "received";
	}

//	public ResponseEntity<String> logWebhookCalls(JsonNode jsonNode) {
//		return loggingHelper.logPayments(jsonNode);
//	}

//	private ResponseEntity<String> returnOkayResponse() {
//		return new ResponseEntity<>(HttpStatus.OK);
//	}

	private void sendSmsToDeveloper(String trackId) {
		try {
			String smsBody = MessageFormat.format("Didn't receive 200 status for track id {0}", trackId);
			smsHelper.sendSMS(smsBody, SMS_NUMBERS);
		} catch (Exception e) {
			LOGGER.info("Error while sending sms to developer " + e);
		}
	}

	@Override
	public String getTrackId() {

		String trackid = null;
		LOGGER.info("received entity from razorpay : {}", payload);

		if (payload.isEmpty())
			throw new RuntimeException("Empty payload received!!");
		try {
			trackid = payload.get("payload").get("order").get("entity").get("receipt").asText();
		} catch (Exception e) {
			throw new RuntimeException("not track id found for payload : " + payload);
		}
		return trackid;
	}

	@Override
	public boolean populateBeanForSuccessfulTransaction(TransactionsBean bean) {
		JsonNode order = payload.get("payload").get("order").get("entity");
		JsonNode payment = payload.get("payload").get("payment").get("entity");
		try {
			Date time = new Date((long) payment.get("created_at").asInt() * 1000);
			SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

			bean.setTransaction_id(order.get("id").asText());
			bean.setMerchant_ref_no(order.get("receipt").asText());
			bean.setResponse_amount(String.valueOf(order.get("amount_paid").asInt() / 100));
			bean.setAmount(bean.getResponse_amount());
			bean.setResponse_payment_method(payment.get("method").asText());
			bean.setResponse_message(order.get("status").asText());
			bean.setResponse_transaction_date_time(dateFormat.format(time));
			bean.setPayment_id(payment.get("id").asText());
			bean.setUpdated_by("WEBHOOK API");
			bean.setCreated_by(bean.getSapid());
			bean.setEmail_id(payment.get("email").asText());
			bean.setPayment_option("razorpay");
			bean.setTransaction_status(PAYMENT_SUCCESS);

			if (!payment.get("bank").isNull())
				bean.setBank_name(payment.get("bank").asText());

			LOGGER.info("Bean populated : {}", bean.toString());

			return true;

		} catch (Exception e) {
			LOGGER.info("Error while populating bean for track id :   error : ", bean.getTrack_id(), e);
			return false;
		}
	}

	@Override
	public boolean populateBeanForFailedTransaction(TransactionsBean bean) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean isPaymentSuccess() {
		return true;
	}

	@Override
	public void checkErrorInPayload(TransactionsBean transactionsBean, String track_id) {

		if (transactionsBean.getTransaction_status() == null)
			throw new RuntimeException("No transaction found");

		if (!WEBHOOK_MODULE_MAP.containsKey(transactionsBean.getType()))
			throw new RuntimeException("Invalid module type " + transactionsBean.getType());

		if (PAYMENT_SUCCESS.equalsIgnoreCase(transactionsBean.getTransaction_status()))
			throw new RuntimeException("Payment was already marked as successful!");

		JsonNode order = payload.get("payload").get("order").get("entity");
		JsonNode payment = payload.get("payload").get("payment").get("entity");

		double responseAmount = order.get("amount_paid").asDouble() / 100;

		double amount = Double.parseDouble(transactionsBean.getAmount());

		int isAmountMatching = Double.compare(responseAmount, amount);

		if (isAmountMatching != 0)
			throw new RuntimeException(
					"Mismatch amount from db " + amount + " and amount received " + responseAmount + " from payload");

		String secureHash = null;
		try {
			secureHash = razorpayHelper.calculateRFC2104HMAC(
					order.get("id").asText() + "|" + payment.get("id").asText(), RAZORPAY_SECRET_KEY);

		} catch (SignatureException e) {
			throw new RuntimeException("signature error " + e.getMessage());
		}

		transactionsBean.setSecure_hash(secureHash);

	}

}
