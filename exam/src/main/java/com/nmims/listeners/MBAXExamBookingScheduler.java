package com.nmims.listeners;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.nmims.beans.MBAExamBookingRequest;
import com.nmims.beans.MBAPaymentRequest;
import com.nmims.daos.MBAXExamBookingDAO;
import com.nmims.daos.MBAXPaymentsDao;
import com.nmims.helpers.MBAPaymentHelper;

@Component
public class MBAXExamBookingScheduler
//implements ApplicationContextAware, ServletConfigAware
{

	@Autowired
	MBAXPaymentsDao paymentsDao;

	@Autowired
	MBAPaymentHelper paymentHelper;

	@Autowired
	MBAXExamBookingDAO examBookingDao;
	
	@Value( "${SERVER}" )
	private String SERVER;

	@Value( "${ENVIRONMENT}" )
	private String ENVIRONMENT;
	
	@Scheduled(fixedDelay=5*60*1000)
	public void updateExamBookingPaymentStatusFromProviders(){

		if(!"tomcat4".equalsIgnoreCase(SERVER) || !"PROD".equalsIgnoreCase(ENVIRONMENT)){
			return;
		}
		
		try {
			List<MBAExamBookingRequest> listOfRequests = paymentsDao.getAllInitiatedTransactionsForExamBooking();
			List<MBAExamBookingRequest> listOfConflicts = new ArrayList<MBAExamBookingRequest>();
			for (MBAExamBookingRequest paymentRequest : listOfRequests) {
				try {
					paymentHelper.checkTransactionStatus(paymentRequest);
					if(paymentRequest.isSuccessFromGateway()) {
						
						boolean isConflict = false;

						List<MBAExamBookingRequest> subjects = examBookingDao.getAllStudentBookingsForTrackId(paymentRequest.getTrackId());
						
						for (MBAExamBookingRequest bookedSubject : subjects) {
							boolean hasConflictTransactions = examBookingDao.checkIfStudentHasSuccessfulBookingsForTimeboundId(bookedSubject.getTimeboundId(), bookedSubject.getSapid());
							
							if(hasConflictTransactions) {
								isConflict = true;
							}
						}
						if(!isConflict) {
							paymentRequest.setLastModifiedBy("Auto Booking Scheduler");
							paymentHelper.processSuccessfulTrancation_MBAX(paymentRequest);
							
						} else {
							processConflictTrancation(paymentRequest);
							for (MBAExamBookingRequest bookedSubject : subjects) {
								listOfConflicts.add(bookedSubject);
							}
						}
					}
					
				} catch (Exception e) {
					
				}
			}
			for (MBAExamBookingRequest conflictRequest : listOfConflicts) {
				conflictRequest.setCreatedBy("System");
				conflictRequest.setLastModifiedBy("System");
				examBookingDao.addConflictTransaction(conflictRequest);
			}
		}catch (Exception e) {
			
		}
	}

//	771197079971571217195236
//	771197079971571217290534
//	771197079971571217392846

	public void processConflictTrancation(MBAPaymentRequest bookingRequest) {
		
		bookingRequest.setTranStatus(MBAPaymentRequest.TRAN_STATUS_MANUALLY_APPROVED);
		bookingRequest.setBookingStatus(MBAExamBookingRequest.BOOKING_STATUS_FAIL);
		bookingRequest.setLastModifiedBy("Auto Booking Scheduler");
		// Update transaction status in db
			// Transactions table
			paymentsDao.updateTransactionDetails(bookingRequest);
			// Bookings table
			paymentsDao.updateTransactionStatusInExamBooking(bookingRequest);
	}
	
	@Scheduled(fixedDelay=5*60*1000)
	public void clearOldExamBookings(){

		if(!"tomcat4".equalsIgnoreCase(SERVER) || !"PROD".equalsIgnoreCase(ENVIRONMENT)){
			return;
		}
		
		try {
			paymentsDao.clearOldOnlineInitiationTransactionPeriodically();
		}catch (Exception e) {
			
		}
	}
	
}
