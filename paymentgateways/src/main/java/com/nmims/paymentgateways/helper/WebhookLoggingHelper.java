package com.nmims.paymentgateways.helper;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.JsonNode;

/**
 * To log Incoming Payments from Razorpay (As of Oct 2022)
 * 
 * @author Swarup Singh Rajpurohit
 * @since Razorpay Integration Oct 2022
 */
@Component
public class WebhookLoggingHelper {

	private static final Logger logger = LoggerFactory.getLogger("razorpay_webhook");

	public ResponseEntity<String> logPayments(JsonNode payload) {
		logger.info("logging complete entity : {}", payload.toString());
		return ResponseEntity.ok().build();
	}

}
