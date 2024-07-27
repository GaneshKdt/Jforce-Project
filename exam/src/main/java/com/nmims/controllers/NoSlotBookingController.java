package com.nmims.controllers;

import java.util.Objects;

import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.nmims.beans.MBAExamBookingRequest;
import com.nmims.services.NoSlotBookingServiceInterface;

@Controller
public class NoSlotBookingController {
	@Autowired
	private NoSlotBookingServiceInterface noSlotBookingService;
	
	private static final String PAYMENT_SOURCE_WEBAPP = "WebApp";
	
	private static final Logger logger = LoggerFactory.getLogger(NoSlotBookingController.class);
	
	/**
	 * Initiates the noSlot booking for the passed trackId by generating the required checkSum and callback URL.
	 * @param request - HttpServletRequest
	 * @param model - Model containing attributes
	 * @param sapid - student No.
	 * @param timeboundId - timeboundId of the student
	 * @param bookingType - noSlot booking type
	 * @param trackId - tracking ID
	 * @param isWeb - device used for transaction
	 * @return redirects to the payment page
	 */
	@GetMapping(value="/student/embaInitiateNoSlotBooking")
	public String initiateNoSlotBooking(HttpServletRequest request, Model model, @RequestParam final String sapid, @RequestParam final String timeboundId, 
										@RequestParam final String bookingType, @RequestParam final String trackId, @RequestParam final boolean isWeb) {
		try {
			MBAExamBookingRequest bookingRequest = noSlotBookingService.noSlotBookingPaymentDetails(sapid, timeboundId, bookingType, trackId, isWeb);
			logger.info("NoSlot Booking initiated successfully for trackId: {}", trackId);
			
			model.addAttribute("bookingRequest", bookingRequest);
			request.getSession().setAttribute("embaBookingRequest", bookingRequest);
			return "embaPayments/pay";
		}
		catch(Exception ex) {
			String errorMessage = (ex instanceof IllegalArgumentException) ? ex.getMessage() : "Error while initiating NoSlot Booking request";
			logger.error("{} for trackId: {}, Exception thrown:", errorMessage, trackId, ex);
			
			model.addAttribute("trackId", trackId);
			return "redirect:../timeline/noSlotBookingStatus";
		}
	}
	
	/**
	 * Payment Callback URL of NoSlot booking request. 
	 * @param request - HttpServletRequest
	 * @param model - Model containing attributes
	 * @return
	 */
	@PostMapping(value = "/embaNoSlotBookingCallback")
	public String noSlotBookingCallback(HttpServletRequest request, Model model) {
		try {
			MBAExamBookingRequest bookingRequest = (MBAExamBookingRequest) request.getSession().getAttribute("embaBookingRequest");		//booking request stored in session
			if(PAYMENT_SOURCE_WEBAPP.equals(bookingRequest.getSource())) {											//adding attributes for payment initiated through WebApp
				if(Objects.nonNull(bookingRequest.getPaymentOption()))
					model.addAttribute("paymentOption", bookingRequest.getPaymentOption());
				
				if(Objects.nonNull(bookingRequest.getSapid()))
					model.addAttribute("sapid", bookingRequest.getSapid());
				
				if(Objects.nonNull(bookingRequest.getTrackId()))
					model.addAttribute("trackId", bookingRequest.getTrackId());
				
				if(Objects.nonNull(bookingRequest.getPaymentType()))
					model.addAttribute("bookingType", bookingRequest.getPaymentType());
			}
			
			noSlotBookingService.noSlotBookingCallbackTransactions(request, bookingRequest);
			logger.info("NoSlot booking callback URL called successfully for sapid: {} with trackId: {} and payment type: {}", 
						bookingRequest.getSapid(), bookingRequest.getTrackId(), bookingRequest.getPaymentType());
		}
		catch (Exception ex) {
			String trackId = request.getParameter("ORDERID");
			logger.error("Error while calling NoSlot booking callback URL for trackId: {}, Exception thrown:", trackId, ex);
			
			noSlotBookingService.sendNoSlotBookingTransExceptionMail(ex, trackId);			//Send transaction callback failure mail to Admin user
			model.addAttribute("trackId", trackId);
		}
		
		return "redirect:../timeline/noSlotBookingStatus";
	}
}
