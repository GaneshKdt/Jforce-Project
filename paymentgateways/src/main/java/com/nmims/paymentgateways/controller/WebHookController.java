package com.nmims.paymentgateways.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.databind.JsonNode;
import com.nmims.paymentgateways.bean.PayloadWrapper;
import com.nmims.paymentgateways.enums.WebhookEnum;
import com.nmims.paymentgateways.factory.WebhookFactoryInterface;
import com.nmims.paymentgateways.interfaces.ProcessWebhookInterface;
import com.nmims.paymentgateways.service.WebhookService;

/**
 * Controller used to process and log webhook requests related to payment
 * gateways from various payment gateways
 * 
 * @since Razorpay Integration Oct 2022
 * @author Swarup Singh Rajpurohit
 */
@RestController
public class WebHookController {
	
	private ProcessWebhookInterface processWebhookInterface;
	
	@Autowired
	private WebhookFactoryInterface webhookFactoryInterface;

	private static final Logger payment_logs = LoggerFactory.getLogger("payment_logs");
	
	@Autowired
	private WebhookService service;
	
	private final static Logger paytmWebhookLogger = LoggerFactory.getLogger("paytm_webhook");

	/**
	 * Used to process payload received from razorpay webhook to update transaction
	 * status
	 * 
	 * @param Application/json as JsonNode payload
	 * @return response entity as ok
	 */
	@PostMapping(value = "/student/getWebHooksPayLoad", consumes = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<String> getWebHooksPayLoad(@RequestBody JsonNode webHookPayLoad) {
		processWebhookInterface = webhookFactoryInterface.getProductType(WebhookEnum.RAZORPAY);
		return new ResponseEntity<String>(processWebhookInterface.processWebhooktransaction(new PayloadWrapper(webHookPayLoad), null), HttpStatus.OK) ;
//		return service.processWebhookTransaction(new PayloadWrapper(webHookPayLoad), WebhookEnum.RAZORPAY);
	}

	/**
	 * Used to log payload received from payment webhook
	 * 
	 * @param Json node payload
	 * @return response entity as ok
	 */
	@PostMapping(value = "/student/webhookLoggingAPI", consumes = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<String> webhookLoggingAPI(@RequestBody JsonNode payload) {
		return service.logWebhookCalls(payload);
	}

	/**
	 * Used to process payload received from paytm webhook
	 * 
	 * @param Map of multipart/form-data
	 * @return ok status with comment sometimes
	 */
	@PostMapping(value = "/m/paytmWebhookAPI", consumes = { MediaType.APPLICATION_FORM_URLENCODED_VALUE,
			MediaType.MULTIPART_FORM_DATA_VALUE, MediaType.APPLICATION_JSON_VALUE })
	public ResponseEntity<String> paytmWebhookAPI(@RequestParam MultiValueMap<String, String> param) {
		processWebhookInterface = webhookFactoryInterface.getProductType(WebhookEnum.PAYTM);
		return new ResponseEntity<String>(processWebhookInterface.processWebhooktransaction(new PayloadWrapper(param), null), HttpStatus.OK) ;
	}
	

//	/**
//	 * Used to process payload received from paytm webhook
//	 * 
//	 * @param Map of multipart/form-data
//	 * @return ok status with comment sometimes
//	 */
//	@PostMapping(value = "/m/paytmWebhookAPI", consumes = { MediaType.APPLICATION_FORM_URLENCODED_VALUE,
//			MediaType.MULTIPART_FORM_DATA_VALUE, MediaType.APPLICATION_JSON_VALUE })
//	public ResponseEntity<String> paytmWebhookAPI(@RequestParam MultiValueMap<String, String> param) {
//		payment_logs.info("webhook received : " + param.toString());
//		return new ResponseEntity<String>("received", HttpStatus.OK);
//	}

}
