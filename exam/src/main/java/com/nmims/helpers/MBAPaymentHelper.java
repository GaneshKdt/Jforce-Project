package com.nmims.helpers;

import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.google.gson.JsonObject;
import com.nmims.beans.MBAExamBookingPaytmResponse;
import com.nmims.beans.MBAExamBookingRequest;
import com.nmims.beans.MBAPaymentRequest;
import com.nmims.beans.StudentExamBean;
import com.nmims.daos.MBAStudentDetailsDAO;
import com.nmims.daos.MBAWXExamBookingDAO;
import com.nmims.daos.MBAWXPaymentsDao;
import com.nmims.daos.MBAXExamBookingDAO;
import com.nmims.daos.MBAXPaymentsDao;

@Component
public class MBAPaymentHelper {

	@Autowired
	MBAStudentDetailsDAO studentDetailsDAO;
	
	@Autowired
	MBAWXPaymentsDao mbaWxPaymentsDao;
	
	@Autowired
	MBAXPaymentsDao mbaxPaymentsDao;

	@Autowired
	MBAWXExamBookingDAO mbaWxExamBookingDAO;
	
	@Autowired
	MBAXExamBookingDAO mbaxExamBookingDAO;

	@Autowired
	MailSender mailSender;

	@Autowired
	MBAHdfcHelper hdfcHelper;

	@Autowired
	MBABillDeskHelper billdeskHelper;
	
	@Autowired
	MBAPaytmHelper paytmHelper;

	@Autowired
	MBAPayuHelper payuHelper;

	@Value("${BR_RETURN_URL}")
	private String BR_RETURN_URL;

	@Value("${PAYTM_MOBILE_TRANS_URL}")
	private String PAYTM_MOBILE_TRANS_URL;
	
	@Value("${BR_RETURN_URL_MBA_X}")
	private String BR_RETURN_URL_MBA_X;
	
	@Value("${NOSLOT_BOOKING_RETURN_URL}")
	private String NOSLOT_BOOKING_RETURN_URL;
	
	private static final String LIVE_SETTINGS_TYPE_PROJECT_REGISTRATION = "Project Registration";
	private static final String LIVE_SETTINGS_TYPE_PROJECT_RE_REGISTRATION = "Project Re-Registration";
	
	public void createResponseBean(HttpServletRequest request, MBAPaymentRequest paymentRequest) {
		String paymentOption = paymentRequest.getPaymentOption();

		// Make the booking request bean to insert relevant data back into the database;
		if(paymentOption.equalsIgnoreCase("paytm")) {
			paytmHelper.createPaytmResponseBean(request, paymentRequest);
			return;
		} else if(paymentOption.equalsIgnoreCase("hdfc")) {
			hdfcHelper.createHdfcResponseBean(request, paymentRequest);
			return;
		} else if(paymentOption.equalsIgnoreCase("billdesk")) {
			billdeskHelper.createBillDeskResponseBean(request, paymentRequest);
			return;
		} else if(paymentOption.equalsIgnoreCase("payu")) {
			payuHelper.createPayuResponseBean(request, paymentRequest);
			return;
		}
	}
	
	
	
	
	/**
	 * Common function to Check Any Error During Transaction
	 * @param paymentRequest 
	 * */
	public String checkErrorInPayment(HttpServletRequest request, MBAPaymentRequest paymentRequest) {
		String paymentOption = paymentRequest.getPaymentOption();

		if(paymentOption.equalsIgnoreCase("paytm")) {
			return paytmHelper.checkErrorInPaytmPayment(request, paymentRequest);
		}
		else if(paymentOption.equalsIgnoreCase("hdfc")) {
			return hdfcHelper.checkErrorInHdfcPayment(request, paymentRequest);
		}
		else if(paymentOption.equalsIgnoreCase("billdesk")) {
//			return billdeskHelper.checkErrorInBillDeskPayment(request, paymentRequest);
		}
		else if(paymentOption.equalsIgnoreCase("payu")) {
			return payuHelper.checkErrorInPayuPayment(request, paymentRequest);
		}
		return "Error Invalid Payment Gateway Found";
	}
	
	
	/**
	 * Common generate checksum code 
	 * */
	
	public String generateCommonCheckSum(MBAPaymentRequest paymentRequest,StudentExamBean student, String requestType) {
		
		String paymentOption = paymentRequest.getPaymentOption();
		
		if(requestType.equals("Exam Registration (MBA - WX)")) {
			String url = BR_RETURN_URL;
			if(!paymentRequest.isWeb() && "paytm".equals(paymentOption)) {
				url = PAYTM_MOBILE_TRANS_URL + paymentRequest.getTrackId();
			}
			paymentRequest.setCallbackURL(url);
		} else if(requestType.equals("Exam Registration (MBA - X)")) {
			String url = BR_RETURN_URL_MBA_X;
			paymentRequest.setCallbackURL(url);
		}
		else if(LIVE_SETTINGS_TYPE_PROJECT_REGISTRATION.equals(requestType)
				|| LIVE_SETTINGS_TYPE_PROJECT_RE_REGISTRATION.equals(requestType)) {
			paymentRequest.setCallbackURL(NOSLOT_BOOKING_RETURN_URL);
		}

		if(StringUtils.isBlank(paymentOption)) {
			return "no paymentOption entered";
		} else  if(paymentOption.equalsIgnoreCase("paytm")) {
			return paytmHelper.generateCheckSum(paymentRequest,student);
		} else if(paymentOption.equalsIgnoreCase("payu")) {
			return payuHelper.generatePayuCheckSum(paymentRequest, student);
		} else if(paymentOption.equalsIgnoreCase("hdfc")) {
			return hdfcHelper.generateChecksum(paymentRequest, student);
		} else if(paymentOption.equalsIgnoreCase("billdesk")) {
			return billdeskHelper.generateChecksum(paymentRequest, student);
		} else {
			return "invalid paymentOption";
		}
	}


	public String checkPaytmChecksum(MBAPaymentRequest paymentRequest, MBAExamBookingPaytmResponse inputRequest) {

		if(paymentRequest == null || StringUtils.isBlank(paymentRequest.getAmount())) {
			return "Payment Request could not be found!";
		}
		try {
			//isSuccess
			boolean verificationCheckSum = paytmHelper.verificationCheckSum(inputRequest);
			
			if(verificationCheckSum) {
				paytmHelper.createPaytmResponseBean(inputRequest, paymentRequest);

				if(Float.parseFloat(inputRequest.getAmount()) != Float.parseFloat(paymentRequest.getAmount())) {
					return "Error in processing payment. Error: Fees " + paymentRequest.getAmount() + " not matching with amount paid ";
				}
				if("Txn Success".equalsIgnoreCase(paymentRequest.getResponseMessage())) {
					return null;	// success response;
				} else {
					return "Error in processing payment. Error:  " + paymentRequest.getResponseMessage() + " Code: " + paymentRequest.getResponseCode();
				}
			} else {
				return "Error in processing payment. Error: Hashvalue not matching. Tampering in response found.";
			}
		} catch (Exception e) {
			
			return "Error in processing payment. Error: " + e.getMessage();
		}
	}




	
	
	public String checkTransactionStatus(MBAPaymentRequest mbawxPaymentRequest) {
		String paymentOption = mbawxPaymentRequest.getPaymentOption();
		String errorMessage = null;

		if(paymentOption.equalsIgnoreCase("paytm")) {

			paytmHelper.getTransactionStatus(mbawxPaymentRequest);
		} else if(paymentOption.equalsIgnoreCase("payu")) {
			
			payuHelper.getTransactionStatus(mbawxPaymentRequest);
		} else if(paymentOption.equalsIgnoreCase("hdfc")) {
			
			return hdfcHelper.getTransactionStatus(mbawxPaymentRequest.getTrackId(), mbawxPaymentRequest);
//		} else if(paymentOption.equalsIgnoreCase("billdesk")) {
			
		}
		return errorMessage;
	}

	public JsonObject initiateRefund(MBAPaymentRequest paymentRequest) {
		String paymentOption = paymentRequest.getPaymentOption();
		if(StringUtils.isBlank(paymentOption)) {
			return null;
			
		} else  if(paymentOption.equalsIgnoreCase("paytm")) {
			JsonObject paytmResponse = paytmHelper.refundInitiate(paymentRequest);
			return paytmResponse;
			
		} else if(paymentOption.equalsIgnoreCase("payu")) {
			JsonObject payuResponse = payuHelper.refundInitiate(paymentRequest);
			return  payuResponse;
//		} else if(paymentOption.equalsIgnoreCase("hdfc")) {
//			return hdfcHelper.generateChecksum(paymentRequest, student);

//		} else if(paymentOption.equalsIgnoreCase("billdesk")) {
//			return billdeskHelper.generateChecksum(paymentRequest, student);

		} else {
			return null;
		}
	}

	public void processSuccessfulTrancation_MBAWX(MBAPaymentRequest bookingRequest) {
		bookingRequest.setTranStatus(MBAPaymentRequest.TRAN_STATUS_SUCCESSFUL);
		bookingRequest.setBookingStatus(MBAExamBookingRequest.BOOKING_STATUS_BOOKED);
		
		// Update transaction status in db
			// Transactions table
			mbaWxPaymentsDao.updateTransactionDetails(bookingRequest);
			// Bookings table
			mbaWxPaymentsDao.updateTransactionStatusInExamBooking(bookingRequest);


		// Send a mail to the student.
		sendSuccessfulTransactionMail_MBAWX(bookingRequest);
	}
	

	private void sendSuccessfulTransactionMail_MBAWX(MBAPaymentRequest bookingRequest) {
		String sapid = bookingRequest.getSapid();
		String trackId = bookingRequest.getTrackId();

		// Get the booking details for the mail.
		List<MBAExamBookingRequest> bookingRequests = mbaWxExamBookingDAO.getAllStudentBookingsForTrackId(sapid, trackId);
		
		// Get student details for mail.
		StudentExamBean student = studentDetailsDAO.getSingleStudentsData(sapid);
		
		// Send the mail.
		mailSender.sendMBAWXExamBookingSummaryEmail(student, bookingRequests);
	}
	

	public void processSuccessfulTrancation_MBAX(MBAPaymentRequest bookingRequest) {
		bookingRequest.setTranStatus(MBAPaymentRequest.TRAN_STATUS_SUCCESSFUL);
		bookingRequest.setBookingStatus(MBAExamBookingRequest.BOOKING_STATUS_BOOKED);
		
		// Update transaction status in db
			// Transactions table
			mbaxPaymentsDao.updateTransactionDetails(bookingRequest);
			// Bookings table
			mbaxPaymentsDao.updateTransactionStatusInExamBooking(bookingRequest);


		// Send a mail to the student.
			sendSuccessfulTransactionMail_MBAX(bookingRequest);
	}
	

	private void sendSuccessfulTransactionMail_MBAX(MBAPaymentRequest bookingRequest) {
		String sapid = bookingRequest.getSapid();
		String trackId = bookingRequest.getTrackId();

		// Get the booking details for the mail.
		List<MBAExamBookingRequest> bookingRequests = mbaxExamBookingDAO.getAllStudentBookingsForTrackId(sapid, trackId);
		
		// Get student details for mail.
		StudentExamBean student = studentDetailsDAO.getSingleStudentsData(sapid);
		
		// Send the mail.
		mailSender.sendMBAXExamBookingSummaryEmail(student, bookingRequests);
	}
	
	
}
