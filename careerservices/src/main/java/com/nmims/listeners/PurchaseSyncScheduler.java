package com.nmims.listeners;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.google.gson.Gson;
import com.nmims.beans.PaymentDetails;
import com.nmims.beans.PaymentGatewayConstants;
import com.nmims.daos.PaymentManagerDAO;
import com.nmims.helpers.MailSender;
import com.sforce.soap.partner.Connector;
import com.sforce.soap.partner.PartnerConnection;
import com.sforce.ws.ConnectionException;
import com.sforce.ws.ConnectorConfig;
import com.sun.mail.util.logging.MailHandler;

@Component
public class PurchaseSyncScheduler {

	@Value( "${SERVER_PATH}" )
	private String SERVER_PATH;

	@Value( "${ENVIRONMENT}" )
	private String ENVIRONMENT;
	
	@Value( "${SERVER}" )
	private String SERVER;
	
	@Value( "${SFDC_USERID}" )
	private String SFDC_USERID;
	
	@Value( "${SFDC_PASSWORD_TOKEN}" )
	private String SFDC_PASSWORD_TOKEN;
	
	@Autowired
	PaymentManagerDAO paymentManagerDAO;

	private PartnerConnection connection;	

	private static final Logger logger = LoggerFactory.getLogger(PurchaseSyncScheduler.class);
 
	@Scheduled(fixedDelay=30*60*1000)
	public void addPendingPackages() {

		//Commented for test server
		if(!"tomcat4".equalsIgnoreCase(SERVER) || !"PROD".equalsIgnoreCase(ENVIRONMENT)){
			System.out.println("Not Running CS Purchase Sync since it is not PROD ");
			return;
		}

		//Get a list of all unsynced payment details
		List<PaymentDetails> listOfPurchaseToSync = paymentManagerDAO.getListOfPendingAdditions();

		if(listOfPurchaseToSync != null) {
			for (PaymentDetails purchaseToSync : listOfPurchaseToSync) {
				PaymentDetails thisPurchase = purchaseToSync;
				try{
					paymentManagerDAO.addPayment(thisPurchase);
					if(thisPurchase.isPaymentStatus()) {
						thisPurchase.setSource(PaymentGatewayConstants.PAYMENT_SOURCE_SCHEDULER);
						paymentManagerDAO.setSuccessfulPurchaseStatusForStudent(thisPurchase);
					}else {
						paymentManagerDAO.addFailedProductIntimation(thisPurchase);
					}
				}catch (Exception e) {
					if(e.getMessage().equals("Transaction details already present")) {
						thisPurchase.setPaymentStatus(true);
						paymentManagerDAO.setSuccessfulPurchaseStatusForStudent(thisPurchase);
					} else {
						paymentManagerDAO.addFailedProductIntimation(thisPurchase);
					}
				}
			}
		}
	}
	
	// Once a day
	@Scheduled(fixedDelay=24*60*60*1000)
	public void syncStudentPaymentDetails() {

//      Commented for test server
		if(!"tomcat4".equalsIgnoreCase(SERVER) || !"PROD".equalsIgnoreCase(ENVIRONMENT)){
			System.out.println("Not Running CS Purchase Sync since it is not PROD ");
			return;
		}
		
		int count = 0;
		ConnectorConfig config = new ConnectorConfig();
		config.setUsername(SFDC_USERID);  
		config.setPassword(SFDC_PASSWORD_TOKEN);  
		List<PaymentDetails> payment = new ArrayList<PaymentDetails>();
		try {
			connection = Connector.newConnection(config);
			payment = paymentManagerDAO.getPendingPaymentToSynch( connection );
			for(PaymentDetails bean : payment) {
				if(!paymentManagerDAO.checkIfRecordExist(bean)) {
					count++;
					paymentManagerDAO.addSyncDetials(bean);
				}
			}
		} catch (Exception e) {
			logger.info("exception : "+e.getMessage());
		}
		
//		MailSender mail = new MailSender();
//		ArrayList<String> recipient = new ArrayList<String>();
//		recipient.add("harsh.kumar.EXT@nmims.edu");
//		recipient.add("ashutosh.sultania.ext@nmims.edu");
//		recipient.add("Shiv.Golani.EXT@nmims.edu");
//		
//		mail.sendEmail("Sync Student Payment Details", count+" records have been synced", recipient);
	}
}
