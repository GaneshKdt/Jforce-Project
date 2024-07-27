package com.nmims.paymentgateways.service;

import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.JsonNode;
import com.nmims.paymentgateways.bean.PayloadWrapper;
import com.nmims.paymentgateways.bean.TransactionsBean;
import com.nmims.paymentgateways.dao.TransactionDAO;
import com.nmims.paymentgateways.enums.WebhookEnum;
import com.nmims.paymentgateways.helper.WebhookLoggingHelper;
import com.nmims.paymentgateways.interfaces.ProcessTransactionInterface;

/**
 * Service Layer that has steps to execute webhook steps based on payload for
 * all kind of webhooks for now it works for paytm and razorpay
 * 
 * @author Swarup Singh Rajpurohit
 * @since Paytm Webhook Integration / Oct 2022
 */
@Component
public class WebhookService {

	@Autowired
	private TransactionDAO dao;

	@Autowired
	private WebhookLoggingHelper loggingHelper;

	@Value("#{${WEBHOOK_MODULE_MAP}}")
	Map<String, String> WEBHOOK_MODULE_MAP;

	@Value("${SERVER_PATH}")
	private String SERVER_PATH;

	@Autowired
	private Map<String, ProcessTransactionInterface> implementationMap;

	private static Logger LOGGER;

//	public ResponseEntity<String> processWebhookTransaction(PayloadWrapper wrapper, WebhookEnum implementation) {
//
//		ProcessTransactionInterface helper = implementationMap.get(implementation.getValue());
//
//		helper.setPayload(wrapper);
//
//		String trackId = null;
//
//		try {
//			trackId = helper.getTrackId();
//		} catch (Exception e) {
//			return returnOkayResponse();
//		}
//
//		LOGGER = LoggerFactory.getLogger(helper.getLogger());
//
//		TransactionsBean transactionsBean = new TransactionsBean();
//
//		try {
//			transactionsBean = dao.getPaymentOptionByTrackId(trackId);
//		} catch (Exception e) {
//			LOGGER.info("transaction not found for track id : {}", trackId);
//			return returnOkayResponse();
//		}
//
//		if (helper.checkErrorInPayload(transactionsBean, trackId))
//			return returnOkayResponse();
//
//		boolean isTransactionSuccess = false;
//
//		try {
//
//			isTransactionSuccess = helper.isPaymentSuccess();
//		} catch (Exception e) {
//			LOGGER.info("Error processing webhook :" + e.getMessage());
//			return returnOkayResponse();
//		}
//
//		if (isTransactionSuccess) {
//			helper.populateBeanForSuccessfulTransaction(transactionsBean);
//			if (!dao.markAsSuccessTransaction(transactionsBean))
//				LOGGER.info("failed to mark transaction as successful : {}", transactionsBean.getTrack_id());
//		} else {
////			return returnOkayResponse();
//			helper.populateBeanForFailedTransaction(transactionsBean);
//			if (!dao.markAsFailedTransaction(transactionsBean))
//				LOGGER.info("failed to mark transaction as failed : {}", transactionsBean.getTrack_id());
//		}
//
//		String url = SERVER_PATH + WEBHOOK_MODULE_MAP.get(transactionsBean.getType());
//
////		String url = "http://localhost:8080/" + WEBHOOK_MODULE_MAP.get(transactionsBean.getType());
//
//		LOGGER.info("posting data to url : {} with body : " + transactionsBean.toString(), url);
//
//		String APIResponse = helper.postForTransactionUpdate(transactionsBean, url);
//
//		LOGGER.info("API response for track id : {} : {} ", transactionsBean.getTrack_id(), APIResponse);
//
//		return returnOkayResponse();
//
//	}

	public ResponseEntity<String> logWebhookCalls(JsonNode jsonNode) {
		return loggingHelper.logPayments(jsonNode);
	}

	private ResponseEntity<String> returnOkayResponse() {
		return new ResponseEntity<>(HttpStatus.OK);
	}
}
