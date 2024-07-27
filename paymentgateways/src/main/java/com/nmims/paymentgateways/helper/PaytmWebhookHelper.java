package com.nmims.paymentgateways.helper;

import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.TreeMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.nmims.paymentgateways.bean.PayloadWrapper;
import com.nmims.paymentgateways.bean.TransactionsBean;
import com.nmims.paymentgateways.dao.TransactionDAO;
import com.nmims.paymentgateways.enums.WebhookEnum;
import com.nmims.paymentgateways.interfaces.ProcessTransactionInterface;
import com.paytm.pg.merchant.CheckSumServiceHelper;

/**
 * Helper to process webhook received from paytm
 * @since Paytm Webhook Integration Oct 2022
 * @author Swarup Singh Rajpurohit
 */

@Component("PAYTM_IMPL")
public class PaytmWebhookHelper 
//implements ProcessTransactionInterface 
{
//
//	@Autowired
//	private TransactionDAO transactionDAO;
//
//	@Value("${SERVER_PATH}")
//	private String SERVER_PATH;
//
//	@Value("#{${WEBHOOK_MODULE_MAP}}")
//	Map<String, String> WEBHOOK_MODULE_MAP;
//
//	@Value("${PAYTM_MERCHANTMID}")
//	private String PAYTM_MERCHANTMID;
//
//	@Value("${PAYTM_MERCHANTKEY}")
//	private String PAYTM_MERCHANTKEY;
//
//	private Map<String, String> payload;
//
//	private final static Logger paytmWebhookLogger = LoggerFactory.getLogger("paytm_webhook");
//
//	private final static String PAYMENT_SUCCESSFUL = "Payment Successfull";
//	private final static String PAYMENT_FAILED = "Payment Failed";
//	private final static String STATUS_FROM_PAYLOAD = "STATUS";
//	private final static String ORDERID = "ORDERID";
//
//	@Override
//	public void setPayload(PayloadWrapper wrapper) {
//		this.payload = wrapper.getFormdata().toSingleValueMap();
//
//	}
//
//	@Override
//	public String getTrackId() {
//		paytmWebhookLogger.info("received entity from paytm : {}", payload);
//
//		if (payload.isEmpty())
//			throw new RuntimeException("Empty payload received!!");
//
//		if (!(payload.containsKey(ORDERID) && payload.containsKey(STATUS_FROM_PAYLOAD))) {
//			paytmWebhookLogger.info("NOT A PAYTM PAYLOAD : " + payload);
//			throw new RuntimeException("track id not found in payload : " + payload);
//		}
//		return payload.get(ORDERID);
//	}
//
//	@Override
//	public boolean checkErrorInPayload(TransactionsBean transactionsBean, String track_id) {
//		String paytmChecksum = null;
//
//		TreeMap<String, String> paytmParams = new TreeMap<String, String>();
//		try {
//			if (transactionsBean.getTransaction_status() == null) {
//				paytmWebhookLogger.info("no transaction found for track id : {}", track_id);
//				return true;
//			}
//
//			if (!WEBHOOK_MODULE_MAP.containsKey(transactionsBean.getType())) {
//				paytmWebhookLogger.info("invalid module type : {} for track id : {}", transactionsBean.getType(),
//						track_id);
//				return true;
//			}
//
//			if (PAYMENT_SUCCESSFUL.equalsIgnoreCase(transactionsBean.getTransaction_status())) {
//				paytmWebhookLogger.info("payment already marked as sucessful in transaction table for track id : {}",
//						track_id);
//				return true;
//			}
//
//			double amountFromDB = Double.valueOf(transactionsBean.getAmount());
//
//			double amountFromWebhook = Double.valueOf(payload.get("TXNAMOUNT"));
//
//			// to be expected 0
//			int isAmountMatching = Double.compare(amountFromDB, amountFromWebhook);
//
//			if (isAmountMatching != 0) {
//				paytmWebhookLogger.info("Mismatch in amount, in db : {} amount received : {} for track id :{}",
//						amountFromDB, amountFromWebhook, track_id);
//				return true;
//			}
//			if(payload.containsKey("CHECKSUMHASH")) {
//			Iterator<Entry<String, String>> fields = payload.entrySet().iterator();
//
//			while (fields.hasNext()) {
//				Entry<String, String> next = fields.next();
//				if ("CHECKSUMHASH".equalsIgnoreCase(next.getKey()))
//					paytmChecksum = next.getValue();
//				else
//					paytmParams.put(next.getKey(), next.getValue());
//			}
//
//			boolean isCheckSumValid = CheckSumServiceHelper.getCheckSumServiceHelper().verifycheckSum(PAYTM_MERCHANTKEY,
//					paytmParams, paytmChecksum);
//			paytmWebhookLogger.info("response received from checksum check : " + isCheckSumValid);
//
//			if (!isCheckSumValid)
//				return true;
//			}
//
//		} catch (Exception e) {
//			paytmWebhookLogger.info("Error while checking error in payload : " + e);
//			return true;
//		}
//
//		return false;
//	}
//
//	@Override
//	public boolean populateBeanForSuccessfulTransaction(TransactionsBean transactionBean) {
//		try {
//			transactionBean.setTransaction_id(payload.get("TXNID"));
//			transactionBean.setMerchant_ref_no(payload.get("ORDERID"));
//			transactionBean.setResponse_code(payload.get("RESPCODE"));
//			transactionBean.setResponse_message(payload.get("RESPMSG"));
//			transactionBean.setResponse_amount(payload.get("TXNAMOUNT"));
//			transactionBean.setResponse_payment_method(payload.get("PAYMENTMODE"));
//
//			if (!payload.get("MERC_UNQ_REF").isEmpty())
//				transactionBean.setDescription(payload.get("MERC_UNQ_REF"));
//
//			if (!payload.get("BANKTXNID").isEmpty()) {
//				transactionBean.setPayment_id(payload.get("BANKTXNID"));
//			}
//			if (!payload.get("BANKNAME").isEmpty()) {
//				transactionBean.setBank_name(payload.get("BANKNAME"));
//			}
//			transactionBean.setResponse_transaction_date_time(payload.get("TXNDATE"));
//			transactionBean.setUpdated_at(transactionBean.getSapid());
//			transactionBean.setCreated_by(transactionBean.getSapid());
//			transactionBean.setTransaction_status(PAYMENT_SUCCESSFUL);
//			transactionBean.setPayment_option("paytm");
//
////			pending for discussion			
////			transactionBean.setEmail_id(EMAIL);
//
//			paytmWebhookLogger.info("Prepared bean : " + transactionBean.toString());
//		} catch (Exception e) {
//			paytmWebhookLogger.info("Error while poplating response bean for track id {} : " + e,
//					transactionBean.getTrack_id());
//			return true;
//		}
//		return false;
//	}
//
//	@Override
//	public boolean populateBeanForFailedTransaction(TransactionsBean transactionBean) {
//
//		try {
//			transactionBean.setError(payload.get("RESPMSG"));
//			transactionBean.setUpdated_at(transactionBean.getSapid());
//			transactionBean.setCreated_by(transactionBean.getSapid());
//			transactionBean.setResponse_code(payload.get("RESPCODE"));
//			transactionBean.setTransaction_status(PAYMENT_FAILED);
//			transactionBean.setPayment_option("paytm");
//
////			pending for discussion			
////			transactionBean.setEmail_id(EMAIL);
//
//			paytmWebhookLogger.info("Prepared bean : " + transactionBean.toString());
//		} catch (Exception e) {
//			paytmWebhookLogger.info("Error while poplating response bean for track id {} : " + e,
//					transactionBean.getTrack_id());
//			return true;
//		}
//		return false;
//	}
//
//	@Override
//	public boolean isPaymentSuccess() {
//		if ("TXN_FAILURE".equalsIgnoreCase(payload.get(STATUS_FROM_PAYLOAD)))
//			return false;
//		else if ("TXN_SUCCESS".equalsIgnoreCase(payload.get(STATUS_FROM_PAYLOAD)))
//			return true;
//		paytmWebhookLogger.info("unknown transaction status for payload : {}", payload);
//		throw new RuntimeException("unknown transaction status for payload : " + payload);
//	}
//
//	@Override
//	public String getLogger() {
//		return WebhookEnum.PAYTM_WEBHOOK.getValue();
//	}

}
