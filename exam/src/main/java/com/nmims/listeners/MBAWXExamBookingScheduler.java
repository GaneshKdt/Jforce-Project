package com.nmims.listeners;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.nmims.beans.MBAExamBookingRequest;
import com.nmims.beans.MBAPaymentRequest;
import com.nmims.daos.MBAWXExamBookingDAO;
import com.nmims.daos.MBAWXPaymentsDao;
import com.nmims.helpers.MBAPaymentHelper;

@Component
public class MBAWXExamBookingScheduler
{

	@Autowired
	MBAWXPaymentsDao mbaWxPaymentsDao;

	@Autowired
	MBAPaymentHelper mbaPaymentHelper;

	@Autowired
	MBAWXExamBookingDAO mbaWxExamBookingDao;
	
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
			List<MBAExamBookingRequest> listOfRequests = mbaWxPaymentsDao.getAllInitiatedTransactionsForExamBooking();
			List<MBAExamBookingRequest> listOfConflicts = new ArrayList<MBAExamBookingRequest>();
			for (MBAExamBookingRequest mbawxPaymentRequest : listOfRequests) {
				try {
					mbaPaymentHelper.checkTransactionStatus(mbawxPaymentRequest);
					if(mbawxPaymentRequest.isSuccessFromGateway()) {
						
						boolean isConflict = false;

						List<MBAExamBookingRequest> subjects = mbaWxExamBookingDao.getAllStudentBookingsForTrackId(mbawxPaymentRequest.getTrackId());
						
						for (MBAExamBookingRequest bookedSubject : subjects) {
							boolean hasConflictTransactions = mbaWxExamBookingDao.checkIfStudentHasSuccessfulBookingsForTimeboundId(bookedSubject.getTimeboundId(), bookedSubject.getSapid());
							
							if(hasConflictTransactions) {
								isConflict = true;
							}
						}
						if(!isConflict) {
							mbawxPaymentRequest.setLastModifiedBy("Auto Booking Scheduler");
							mbaPaymentHelper.processSuccessfulTrancation_MBAWX(mbawxPaymentRequest);
							
						} else {
							processConflictTrancation(mbawxPaymentRequest);
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
				mbaWxExamBookingDao.addConflictTransaction(conflictRequest);
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
			mbaWxPaymentsDao.updateTransactionDetails(bookingRequest);
			// Bookings table
			mbaWxPaymentsDao.updateTransactionStatusInExamBooking(bookingRequest);
	}
	
	@Scheduled(fixedDelay=5*60*1000)
	public void clearOldExamBookings(){

		if(!"tomcat4".equalsIgnoreCase(SERVER) || !"PROD".equalsIgnoreCase(ENVIRONMENT)){
			return;
		}
		
		try {
			mbaWxPaymentsDao.clearOldOnlineInitiationTransactionPeriodically();
		}catch (Exception e) {
			
		}
	}
	
}
