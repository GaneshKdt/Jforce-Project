package com.nmims.listeners;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.nmims.services.NoSlotBookingServiceInterface;

@Component
public class NoSlotBookingScheduler {
	@Autowired
	NoSlotBookingServiceInterface noSlotBookingService;
	
	@Value("${SERVER}")
	private String SERVER;

	@Value("${ENVIRONMENT}")
	private String ENVIRONMENT;
	
	private static final String ENVIRONMENT_PRODUCTION = "PROD";
	private static final String SERVER_TOMCAT4 = "tomcat4";
	
	private static final Logger logger = LoggerFactory.getLogger(NoSlotBookingScheduler.class);
	
	/**
	 * Scheduler to check the transaction status of Payment records with transaction status as Initiated
	 * and transaction dateTime prior to 10 minutes from the current dateTime.
	 * If the payment is successful mark the payment and booking records as successful and send a successful transaction mail.
	 * If the payment is successful and there is conflict in transaction, solve the payment conflict.
	 */
	@Scheduled(fixedDelay = 5*60*1000)
	public void noSlotBookingPaymentStatus() {
		try {
			if(!ENVIRONMENT_PRODUCTION.equals(ENVIRONMENT) && !SERVER_TOMCAT4.equals(SERVER))
				return;
			
			noSlotBookingService.processNoSlotBookingPaymentStatus();
			logger.info("NoSlot Booking Payment Status scheduler ran successfully.");
		}
		catch(Exception ex) {
			logger.error("Error while running NoSlot Booking Payment Status scheduler. Exception thrown:", ex);
		}
	}
	
	/**
	 * Scheduler to check transaction records marked as Initiated exceeding transaction dateTime of 180 minutes from the current dateTime.
	 * Mark these transactions as Expired.
	 */
	@Scheduled(fixedDelay = 5*60*1000)
	public void noSlotBookingExpiredPaymentStatus() {
		try {
			if(!ENVIRONMENT_PRODUCTION.equals(ENVIRONMENT) && !SERVER_TOMCAT4.equals(SERVER))
				return;
			
			noSlotBookingService.processNoSlotBookingExpiredPayments();
			logger.info("NoSlot Booking Expired Payment Status scheduler ran successfully.");
		}
		catch(Exception ex) {
			logger.error("Error while running NoSlot Booking Expired Payment Status scheduler. Exception thrown:", ex);
		}
	}
}
