package com.nmims.paymentgateways.helper;

import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

@Component
public class EmailAWSHelper {
	@Value("${PAYMENT_MAIL_URL}")
	private String MAIL_URL;

	private static final Logger logger = LoggerFactory.getLogger(EmailAWSHelper.class);
	private RestTemplate template = new RestTemplate();

	public void sendMail(Map mailBody) {
		try {
			ResponseEntity mailResponse = template.postForEntity(MAIL_URL, mailBody, String.class);
			logger.info("Response from email : {}", mailResponse.toString());
		} catch (Exception e) {
			logger.info("Error while sending mail : {}", e.getMessage());
		}
	}
}
