package com.nmims.paymentgateways.helper;

import java.security.SignatureException;
import java.text.MessageFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.JsonNode;
import com.nmims.paymentgateways.bean.PayloadWrapper;
import com.nmims.paymentgateways.bean.TransactionsBean;
import com.nmims.paymentgateways.enums.WebhookEnum;
import com.nmims.paymentgateways.interfaces.ProcessTransactionInterface;

/**
 * A helper class used to process payload received from webhooks
 * 
 * @author Swarup Singh Rajpurohit
 * @since Razorpay Integration Oct 2022
 */

@Component("RAZORPAY_IMPL")
public class RazorpayWebhookHelper 
//implements ProcessTransactionInterface 
{
//
//	@Value("${RAZOR_PAY_SECRET_KEY}")
//	private String RAZORPAY_SECRET_KEY;
//
//	@Value("${SERVER_PATH}")
//	private String SERVER_PATH;
//
//	@Value("${PAYMENT_SMS_NUMBERS}")
//	private String SMS_NUMBERS;
//
//	private JsonNode payload;
//
//	@Autowired
//	private RazorpayHelper razorpayHelper;
//
//	private final String PAYMENT_SUCCESS = "Payment Successfull";
//	private final String PAYMENT_FAILED = "Payment Failed";
//
//	@Autowired
//	SMSHelper smsHelper;
//
//	// url of all modules reading from property file
//	@Value("#{${WEBHOOK_MODULE_MAP}}")
//	Map<String, String> WEBHOOK_MODULE_MAP;
//
//	private static final Logger logger = LoggerFactory.getLogger(WebhookEnum.RAZORPAY_WEBHOOK.getValue());
//
//	private void sendSmsToDeveloper(String trackId) {
//		try {
//			String smsBody = MessageFormat.format("Didn't receive 200 status for track id {0}", trackId);
//			smsHelper.sendSMS(smsBody, SMS_NUMBERS);
//		} catch (Exception e) {
//			logger.info("Error while sending sms to developer " + e);
//		}
//	}
//
//	@Override
//	public void setPayload(PayloadWrapper wrapper) {
//		this.payload = wrapper.getJsonData();
//	}
//
//	@Override
//	public String getTrackId() {
//
//		String trackid = null;
//		logger.info("received entity from razorpay : {}", payload);
//
//		if (payload.isEmpty())
//			throw new RuntimeException("Empty payload received!!");
//		try {
//			trackid = payload.get("payload").get("order").get("entity").get("receipt").asText();
//		} catch (Exception e) {
//			throw new RuntimeException("not track id found for payload : " + payload);
//		}
//		return trackid;
//	}
//
//	@Override
//	public String getLogger() {
//		return WebhookEnum.RAZORPAY_WEBHOOK.getValue();
//	}
//
//	@Override
//	public boolean populateBeanForSuccessfulTransaction(TransactionsBean bean) {
//		JsonNode order = payload.get("payload").get("order").get("entity");
//		JsonNode payment = payload.get("payload").get("payment").get("entity");
//		try {
//			Date time = new Date((long) payment.get("created_at").asInt() * 1000);
//			SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
//
//			bean.setTransaction_id(order.get("id").asText());
//			bean.setMerchant_ref_no(order.get("receipt").asText());
//			bean.setResponse_amount(String.valueOf(order.get("amount_paid").asInt() / 100));
//			bean.setAmount(bean.getResponse_amount());
//			bean.setResponse_payment_method(payment.get("method").asText());
//			bean.setResponse_message(order.get("status").asText());
//			bean.setResponse_transaction_date_time(dateFormat.format(time));
//			bean.setPayment_id(payment.get("id").asText());
//			bean.setUpdated_by(bean.getSapid());
//			bean.setCreated_by(bean.getSapid());
//			bean.setEmail_id(payment.get("email").asText());
//			bean.setPayment_option("razorpay");
//			bean.setTransaction_status(PAYMENT_SUCCESS);
//			
//			if (!payment.get("bank").isNull())
//				bean.setBank_name(payment.get("bank").asText());
//
//			logger.info("Bean populated : {}", bean.toString());
//
//			return true;
//
//		} catch (Exception e) {
//			logger.info("Error while populating bean for track id :   error : ", bean.getTrack_id(), e);
//			return false;
//		}
//	}
//
//	@Override
//	public boolean populateBeanForFailedTransaction(TransactionsBean bean) {
//		// TODO Auto-generated method stub
//		return false;
//	}
//
//	@Override
//	public boolean isPaymentSuccess() {
//		return true;
//	}
//
//	@Override
//	public boolean checkErrorInPayload(TransactionsBean transactionsBean, String track_id) {
//
//		try {
//			if (transactionsBean.getTransaction_status() == null) {
//				logger.info("no transaction found for track id : {}", track_id);
//				return true;
//			}
//
//			if (!WEBHOOK_MODULE_MAP.containsKey(transactionsBean.getType())) {
//				logger.info("invalid module type : {} for track id : {}", transactionsBean.getType(), track_id);
//				return true;
//			}
//
//			if (PAYMENT_SUCCESS.equalsIgnoreCase(transactionsBean.getTransaction_status())) {
//				logger.info("payment already marked as sucessful in transaction table for track id : {}", track_id);
//				return true;
//			}
//			JsonNode order = payload.get("payload").get("order").get("entity");
//			JsonNode payment = payload.get("payload").get("payment").get("entity");
//
//			double responseAmount = order.get("amount_paid").asDouble() / 100;
//			double amount = Double.parseDouble(transactionsBean.getAmount());
//
//			int isAmountMatching = Double.compare(responseAmount, amount);
//
//			if (isAmountMatching != 0) {
//				logger.info("Mismatch in amount, in db : {} amount received : {} for track id :{}", amount,
//						responseAmount, track_id);
//				return true;
//			}
//			String secureHash = null;
//			try {
//				secureHash = razorpayHelper.calculateRFC2104HMAC(
//						order.get("id").asText() + "|" + payment.get("id").asText(), RAZORPAY_SECRET_KEY);
//
//			} catch (SignatureException e) {
//				logger.info("Error while generating for track id : " + track_id, e);
//			}
//
//			transactionsBean.setSecure_hash(secureHash);
//
//		} catch (Exception e) {
//			logger.info("Error while checking error in payload : " + e);
//			return true;
//		}
//
//		return false;
//	}
}
