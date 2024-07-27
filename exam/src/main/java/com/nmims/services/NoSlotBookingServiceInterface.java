package com.nmims.services;

import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import com.nmims.beans.MBAExamBookingRequest;

public interface NoSlotBookingServiceInterface {
	/**
	 * Get the student timebound details using the timeboundId passed as a parameter.
	 * Using the timebound details check if the project registration or project re-registration is active for the current date time.
	 * Check if the student has already paid for active registration.
	 * @param timeboundId - timeboundId of the student
	 * @param sapid - student No.
	 * @return Map containing the project registration or re-registration details
	 */
	Map<String, Object> studentProjectRegistrationEligibility(final Long timeboundId, final String sapid);
	
	/**
	 * Get the student timebound details using the timeboundId passed as a parameter.
	 * Using the timebound details check if the Project Registration is active for the current date time.
	 * Check if the student has already paid for Project Registration.
	 * @param timeboundId - timeboundId of the student
	 * @param sapid - student No.
	 * @return Map containing Project Registration details
	 */
	Map<String, Object> projectRegistrationDetails(final Long timeboundId, final String sapid);
	
	/**
	 * Get the student timebound details using the timeboundId passed as a parameter.
	 * Using the timebound details check if the Project Re-Registration is active for the current date time.
	 * Check if the student has already paid for Project Re-Registration.
	 * @param timeboundId - timeboundId of the student
	 * @param sapid - student No.
	 * @return Map containing Project Registration details
	 */
	Map<String, Object> projectReRegistrationDetails(final Long timeboundId, final String sapid);
	
	/**
	 * The details passed as parameters are validated and payment is initiated for the booking type.
	 * @param sapid - student No.
	 * @param timeboundId - timeboundId of the student
	 * @param type - noSlot booking type
	 * @param amount - amount charged
	 * @param paymentOption - option selected by the user for payment
	 * @param source - device used for payment
	 * @return tracking ID of the initiated payment request
	 */
	String saveNoSlotBooking(final String sapid, final Long timeboundId, final String type, 
							final String amount, final String paymentOption, final String source);
	
	/**
	 * Gets the initiated payment records using the provided trackId. 
	 * and generate the checksum and provide the callback URL.
	 * @param sapid - student No.
	 * @param timeboundId - timeboundId of the student
	 * @param type - noSlot booking type
	 * @param trackId - tracking ID
	 * @param isWeb - device used for payment
	 * @return booking details containing payment details and generated checksum
	 */
	MBAExamBookingRequest noSlotBookingPaymentDetails(final String sapid, final String timeboundId, final String type, 
														final String trackId, final boolean isWeb);
	
	/**
	 * A payment responseBean is created and error is checked in payment.
	 * If no error is found, payment records for the trackId are marked as Payment Successful.
	 * Their subsequent booking status is marked as true and successful transaction mailer is sent to the student.
	 * @param request - HttpServletRequest
	 * @param paymentRequest - bean containing the booking payment details
	 */
	void noSlotBookingCallbackTransactions(HttpServletRequest request, MBAExamBookingRequest paymentRequest);
	
	/**
	 * Gets the payment records using the trackId passed as a parameter, 
	 * booking records are obtained from the fetched payment records.
	 * @param sapid - student No.
	 * @param timeboundId - timebound of the student
	 * @param trackId - tracking ID
	 * @return List of Map containing noSlot booking records
	 */
	List<Map<String, String>> noSlotBookingStatus(final String sapid, final Long timeboundId, final String trackId);
	
	/**
	 * Checks if the payment records marked with transaction status as Initiated, prior to 10 minutes from the current DateTime are successful,
	 * mark the payment and booking status as successful and send a transaction successful mail.
	 * If the transaction has a conflict, mark the payment as successful and insert the payment record in mba_wx_noslot_booking_conflict_transactions table.
	 */
	void processNoSlotBookingPaymentStatus();
	
	/**
	 * Checks if the payments records with transaction status as Initiated are present for more than 180 minutes.
	 * Mark these transactions as Expired.
	 */
	void processNoSlotBookingExpiredPayments();
	
	/**
	 * Sending Exception stackTrace mail on noSlot booking transaction error.
	 * @param exception - Exception caught
	 * @param trackId - tracking ID
	 */
	void sendNoSlotBookingTransExceptionMail(final Exception exception, final String trackId);
}
