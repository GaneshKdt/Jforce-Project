package com.nmims.paymentgateways.strategies;

import java.util.Iterator;
import java.util.Map;
import java.util.TreeMap;
import java.util.concurrent.CompletableFuture;
import java.util.Map.Entry;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.nmims.paymentgateways.bean.PayloadWrapper;
import com.nmims.paymentgateways.bean.TransactionsBean;
import com.nmims.paymentgateways.dao.TransactionDAO;
import com.nmims.paymentgateways.enums.WebhookEnum;
import com.nmims.paymentgateways.interfaces.ProcessTransactionInterface;
import com.paytm.pg.merchant.CheckSumServiceHelper;

@Service
public class ProcessTransactionPaytmStrategies implements ProcessTransactionInterface {

	@Value("${SERVER_PATH}")
	private String SERVER_PATH;

	@Value("#{${WEBHOOK_MODULE_MAP}}")
	Map<String, String> WEBHOOK_MODULE_MAP;

	@Value("${PAYTM_MERCHANTMID}")
	private String PAYTM_MERCHANTMID;

	@Value("${PAYTM_MERCHANTKEY}")
	private String PAYTM_MERCHANTKEY;

	private Map<String, String> payload;

	private final static String PAYMENT_SUCCESSFUL = "Payment Successfull";
	private final static String PAYMENT_FAILED = "Payment Failed";
	private final static String STATUS_FROM_PAYLOAD = "STATUS";
	private final static String ORDERID = "ORDERID";

//	@Autowired private PaytmWebhookHelper helper;

	@Autowired
	private TransactionDAO dao;

	private final static Logger paytmWebhookLogger = LoggerFactory.getLogger("paytm_webhook");

	@Override
	public String processWebhookTransaction(PayloadWrapper wrapper, WebhookEnum implementation) {
		try {
			CompletableFuture<Boolean> daoUpdateResponse = null;
			this.payload = wrapper.getFormdata().toSingleValueMap();

			String trackId = null;

			trackId = this.getTrackId();

//			TransactionsBean transactionsBean = new TransactionsBean();

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

			paytmWebhookLogger.info("posting data to url : {} with body : " + transactionsBean.toString(), url);

			String APIResponse = this.postForTransactionUpdate(transactionsBean, url);

			paytmWebhookLogger.info("API response for track id : {} : {} ", transactionsBean.getTrack_id(),
					APIResponse);

			if (!daoUpdateResponse.get())

				paytmWebhookLogger.info("failed to mark transaction : {}", transactionsBean.getTrack_id());

		} catch (Exception e) {
			paytmWebhookLogger.info("Error processing webhook" + e.getMessage());
		}
		return "received";
	}

	@Override
	public String getTrackId() {
		paytmWebhookLogger.info("received entity from paytm : {}", payload);

		if (payload.isEmpty())
			throw new RuntimeException("Empty payload received!!");

		if (!(payload.containsKey(ORDERID) && payload.containsKey(STATUS_FROM_PAYLOAD))) {
			paytmWebhookLogger.info("NOT A PAYTM PAYLOAD : " + payload);
			throw new RuntimeException("track id not found in payload : " + payload);
		}
		return payload.get(ORDERID);
	}

	@Override
	public boolean populateBeanForSuccessfulTransaction(TransactionsBean transactionBean) {
		try {
			transactionBean.setTransaction_id(payload.get("TXNID"));
			transactionBean.setMerchant_ref_no(payload.get("ORDERID"));
			transactionBean.setResponse_code(payload.get("RESPCODE"));
			transactionBean.setResponse_message(payload.get("RESPMSG"));
			transactionBean.setResponse_amount(payload.get("TXNAMOUNT"));
			transactionBean.setResponse_payment_method(payload.get("PAYMENTMODE"));

			if (!payload.get("MERC_UNQ_REF").isEmpty())
				transactionBean.setDescription(payload.get("MERC_UNQ_REF"));

			if (!payload.get("BANKTXNID").isEmpty()) {
				transactionBean.setPayment_id(payload.get("BANKTXNID"));
			}
			if (!payload.get("BANKNAME").isEmpty()) {
				transactionBean.setBank_name(payload.get("BANKNAME"));
			}
			transactionBean.setResponse_transaction_date_time(payload.get("TXNDATE"));
			transactionBean.setUpdated_by("WEBHOOK API");
			transactionBean.setCreated_by(transactionBean.getSapid());
			transactionBean.setTransaction_status(PAYMENT_SUCCESSFUL);
			transactionBean.setPayment_option("paytm");

//			pending for discussion			
//			transactionBean.setEmail_id(EMAIL);

			paytmWebhookLogger.info("Prepared bean : " + transactionBean.toString());
		} catch (Exception e) {
			paytmWebhookLogger.info("Error while poplating response bean for track id {} : " + e,
					transactionBean.getTrack_id());
			return true;
		}
		return false;
	}

	@Override
	public boolean populateBeanForFailedTransaction(TransactionsBean transactionBean) {

		try {
			transactionBean.setError(payload.get("RESPMSG"));
			transactionBean.setUpdated_by("WEBHOOK API");
			transactionBean.setCreated_by(transactionBean.getSapid());
			transactionBean.setResponse_code(payload.get("RESPCODE"));
			transactionBean.setTransaction_status(PAYMENT_FAILED);
			transactionBean.setPayment_option("paytm");

//			pending for discussion			
//			transactionBean.setEmail_id(EMAIL);

			paytmWebhookLogger.info("Prepared bean : " + transactionBean.toString());
		} catch (Exception e) {
			paytmWebhookLogger.info("Error while poplating response bean for track id {} : " + e,
					transactionBean.getTrack_id());
			return true;
		}
		return false;
	}

	@Override
	public boolean isPaymentSuccess() {
		if ("TXN_FAILURE".equalsIgnoreCase(payload.get(STATUS_FROM_PAYLOAD)))
			return false;
		else if ("TXN_SUCCESS".equalsIgnoreCase(payload.get(STATUS_FROM_PAYLOAD)))
			return true;
		paytmWebhookLogger.info("unknown transaction status for payload : {}", payload);
		throw new RuntimeException("unknown transaction status for payload : " + payload);
	}

	@Override
	public void checkErrorInPayload(TransactionsBean transactionsBean, String track_id) {
		String paytmChecksum = null;

		TreeMap<String, String> paytmParams = new TreeMap<String, String>();
		if (transactionsBean.getTransaction_status() == null)
			throw new RuntimeException("No transaction found");

		if (!WEBHOOK_MODULE_MAP.containsKey(transactionsBean.getType()))
			throw new RuntimeException("Invalid module type " + transactionsBean.getType());

		if (PAYMENT_SUCCESSFUL.equalsIgnoreCase(transactionsBean.getTransaction_status()))
			throw new RuntimeException("Payment was already marked as successful!");

		if (PAYMENT_FAILED.equalsIgnoreCase(this.payload.get(transactionsBean.getTransaction_status()))
				&& "TXN_FAILURE".equalsIgnoreCase(this.payload.get(STATUS_FROM_PAYLOAD)))
			throw new RuntimeException("Payment was already marked as failed");

		double amountFromDB = Double.valueOf(transactionsBean.getAmount());

		double amountFromWebhook = Double.valueOf(payload.get("TXNAMOUNT"));

		// to be expected 0
		int isAmountMatching = Double.compare(amountFromDB, amountFromWebhook);

		if (isAmountMatching != 0)
			throw new RuntimeException("Mismatch amount from db " + amountFromDB + " and amount received "
					+ amountFromWebhook + " from payload");

		if (payload.containsKey("CHECKSUMHASH")) {

			Iterator<Entry<String, String>> fields = payload.entrySet().iterator();

			while (fields.hasNext()) {
				Entry<String, String> next = fields.next();
				if ("CHECKSUMHASH".equalsIgnoreCase(next.getKey()))
					paytmChecksum = next.getValue();
				else
					paytmParams.put(next.getKey(), next.getValue());
			}
			boolean isCheckSumValid = false;

			try {
				isCheckSumValid = CheckSumServiceHelper.getCheckSumServiceHelper().verifycheckSum(PAYTM_MERCHANTKEY,
						paytmParams, paytmChecksum);

			} catch (Exception e) {
				throw new RuntimeException("Error while checking checksum : " + e.getMessage());
			}

			if (!isCheckSumValid)
				throw new RuntimeException("Checksum not valid");
		}
	}
}
