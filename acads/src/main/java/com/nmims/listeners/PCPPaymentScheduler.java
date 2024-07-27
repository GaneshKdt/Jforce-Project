package com.nmims.listeners;

import java.util.ArrayList;
import java.util.HashMap;

import javax.servlet.ServletConfig;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.web.context.ServletConfigAware;

import com.nmims.beans.PCPBookingTransactionBean;
import com.nmims.beans.StudentAcadsBean;
import com.nmims.daos.ContentDAO;
import com.nmims.daos.PCPBookingDAO;
import com.nmims.helpers.MailSender;
import com.nmims.helpers.XMLParser;

@Component
public class PCPPaymentScheduler implements ApplicationContextAware, ServletConfigAware{

	@Value( "${SECURE_SECRET}" )
	private String SECURE_SECRET; // secret key;
	@Value( "${ACCOUNT_ID}" )
	private String ACCOUNT_ID;

	@Value( "${SERVER}" )
	private String SERVER;

	@Value( "${ENVIRONMENT}" )
	private String ENVIRONMENT;

	private final String ONLINE_PAYMENT_INITIATED = "Online Payment Initiated"; 
	private final String ONLINE_PAYMENT_MANUALLY_APPROVED = "Online Payment Manually Approved";
	
	private static ApplicationContext act = null;
	private static ServletConfig sc = null;

	@Autowired
	PCPBookingDAO pcpBookingDAO;

	@Autowired
	ContentDAO contentDAO;
	
	@Override
	public void setServletConfig(ServletConfig sc) {
		this.sc = sc;
	}

	@Override
	public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
		this.act = applicationContext;
	}

	public static ApplicationContext getApplicationContext() {
		return act;
	}


	//@Scheduled(fixedDelay=5*60*1000)
	public void deleteIncompleteServiceRequest(){

	}


	@Scheduled(fixedDelay=60*60*1000)
	public void synchronizePendingTransactions(){
		
		System.out.println("Auto Conflict Correction Scheduler started running for PCP/VC Booking");

		System.out.println("Server = "+SERVER);
		System.out.println("synchronizePendingTransactions scheduler for "+ENVIRONMENT); 

		if(!"tomcat4".equalsIgnoreCase(SERVER) || !"PROD".equalsIgnoreCase(ENVIRONMENT)){
			System.out.println("Not running synchronizePendingTransactions scheduler since this is not tomcat4. This is "+SERVER);
			return;
		}
		
		try {
			ArrayList<PCPBookingTransactionBean> paidBookingList =  new ArrayList<>();
			ArrayList<PCPBookingTransactionBean> expiredBookingList =  new ArrayList<>();
			
			HashMap<String, PCPBookingTransactionBean> paidBookingsMap = new HashMap<>();
			HashMap<String, PCPBookingTransactionBean> expiredBookingsMap = new HashMap<>();
			HashMap<String, PCPBookingTransactionBean> initiatedBookingsMap = new HashMap<>();

			ArrayList<PCPBookingTransactionBean> paymentInitiatedBookingsList =  pcpBookingDAO.getPaymentInitiatedSRList();
			System.out.println("PCP: paymentInitiatedBookingsList = "+paymentInitiatedBookingsList);
			
			if(paymentInitiatedBookingsList != null  && paymentInitiatedBookingsList.size() > 0){
				for (PCPBookingTransactionBean pcpBooking : paymentInitiatedBookingsList) {
					initiatedBookingsMap.put(pcpBooking.getTrackId(), pcpBooking);
				}
				//Take only unique track Ids to query
				paymentInitiatedBookingsList = new ArrayList<PCPBookingTransactionBean>(initiatedBookingsMap.values());
			}
			
			
			if(paymentInitiatedBookingsList != null  && paymentInitiatedBookingsList.size() > 0){
				for (PCPBookingTransactionBean pcpBooking : paymentInitiatedBookingsList) {
					String trackId = pcpBooking.getTrackId();
					System.out.println("Checking status for "+trackId);
					XMLParser parser = new XMLParser();
					String xmlResponse = parser.queryTransactionStatus(trackId, ACCOUNT_ID, SECURE_SECRET);
					System.out.println("xmlResponse = "+xmlResponse);
					parser.parseResponse(xmlResponse, pcpBooking);
					String transactionType = pcpBooking.getTransactionType();
					String status = pcpBooking.getStatus();
					
					if(("Authorized".equalsIgnoreCase(transactionType) || "Captured".equalsIgnoreCase(transactionType))&& "Processed".equalsIgnoreCase(status)){
						System.out.println("SAPID: "+pcpBooking.getSapid() + " Amount: "+pcpBooking.getAmount());
						pcpBooking.setTranStatus(ONLINE_PAYMENT_MANUALLY_APPROVED);
						paidBookingsMap.put(pcpBooking.getTrackId(), pcpBooking);
					}else{
						expiredBookingsMap.put(pcpBooking.getTrackId(), pcpBooking);
					}
				}
				
				//Update Failed transactions
				if(expiredBookingsMap.size() > 0){
					expiredBookingList = new ArrayList<PCPBookingTransactionBean>(expiredBookingsMap.values());
					for (PCPBookingTransactionBean pcpBooking : expiredBookingList) {
						pcpBookingDAO.markBookingsExpired(pcpBooking);
					}
				}
				
				//Mark successful transactions
				if(paidBookingsMap.size() > 0){
					paidBookingList = new ArrayList<PCPBookingTransactionBean>(paidBookingsMap.values());
					for (PCPBookingTransactionBean pcpBooking : paidBookingList) {
						pcpBookingDAO.updateSeatsForOnlineTransaction(pcpBooking);
						
						StudentAcadsBean student = contentDAO.getSingleStudentsData(pcpBooking.getSapid());
						MailSender mailSender = (MailSender)act.getBean("mailer");
						mailSender.sendBookingSummaryEmail(student, pcpBookingDAO);
					}
				}
				
				
			}

		} catch (Exception e) {
			  
		}
	}

	

}

