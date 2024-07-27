package com.nmims.controllers;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import javax.validation.Valid;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.nmims.beans.ResponseDataBean;
import com.nmims.dto.NoSlotBookingDto;
import com.nmims.services.NoSlotBookingServiceInterface;

@RestController
@RequestMapping("m")
public class NoSlotBookingRestController {
	@Autowired
	private NoSlotBookingServiceInterface noSlotBookingService;
	
	private static final Logger logger = LoggerFactory.getLogger(NoSlotBookingRestController.class);

	/**
	 * Checks if the student is eligible for Project Registration or Project Re-Registration for the passed timeboundId.
	 * @param timeboundId - timeboundId of the student
	 * @param sapid - student No.
	 * @return Map containing project registration or re-registration details
	 */
	@GetMapping(value = "/student/checkProjectRegistrationEligibility", produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<ResponseDataBean> checkProjectRegistrationEligibility(@RequestParam final Long timeboundId, @RequestParam final String sapid) {
		ResponseDataBean response = new ResponseDataBean();
		try {
			Map<String, Object> dataMap = noSlotBookingService.studentProjectRegistrationEligibility(timeboundId, sapid);
			response.setSuccess(true);
			response.setCode(HttpStatus.OK.value());
			response.setMessage("Successfully validated Project Registration eligiblity of student: " + sapid);
			response.setData(dataMap);
			
			logger.info("Project Registration eligiblity validated successfully, response: {}", response);
			return new ResponseEntity<>(response, HttpStatus.OK);
		}
		catch(IllegalArgumentException ex) {
			response.setSuccess(false);
			response.setCode(HttpStatus.BAD_REQUEST.value());
			response.setMessage(ex.getMessage());
			
			logger.error("Unable to validate the Project Registration eligiblity of student: {}, Error message : {}", sapid, ex.getMessage());
			return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
		}
		catch(Exception ex) {
			response.setSuccess(false);
			response.setCode(HttpStatus.INTERNAL_SERVER_ERROR.value());
			response.setMessage("Failed to validate Project Registration eligiblity of student: " + sapid);
			
			logger.error("Error while validating the Project Registration eligiblity of student: {}, Exception thrown:", sapid, ex);
			return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}
	
	/**
	 * Gets the Project Registration details using the passed timeboundId and sapid.
	 * @param timeboundId - timeboundId of the student
	 * @param sapid - student No.
	 * @return Map containing Project Registration details
	 */
	@GetMapping(value = "/student/projectRegistrationDetails", produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<ResponseDataBean> projectRegistrationDetails(@RequestParam final Long timeboundId, @RequestParam final String sapid) {
		ResponseDataBean response = new ResponseDataBean();
		try {
			Map<String, Object> dataMap = noSlotBookingService.projectRegistrationDetails(timeboundId, sapid);
			response.setSuccess(true);
			response.setCode(HttpStatus.OK.value());
			response.setMessage("Successfully obtained Project Registration details for student: " + sapid);
			response.setData(dataMap);
			
			logger.info("Project Registration details obtained successfully, response: {}", response);
			return new ResponseEntity<>(response, HttpStatus.OK);
		}
		catch(IllegalArgumentException ex) {
			response.setSuccess(false);
			response.setCode(HttpStatus.BAD_REQUEST.value());
			response.setMessage(ex.getMessage());
			
			logger.error("Unable to obtain Project Registration details for student: {}, Error message : {}", sapid, ex.getMessage());
			return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
		}
		catch(Exception ex) {
			response.setSuccess(false);
			response.setCode(HttpStatus.INTERNAL_SERVER_ERROR.value());
			response.setMessage("Failed to obatin Project Registration details for student: " + sapid);
			
			logger.error("Error while obtaining Project Registration details for student: {}, Exception thrown:", sapid, ex);
			return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}
	
	/**
	 * Gets the Project Re-Registration details using the passed timeboundId and sapid.
	 * @param timeboundId - timeboundId of the student
	 * @param sapid - student No.
	 * @return Map containing Project Re-Registration details
	 */
	@GetMapping(value = "/student/projectReRegistrationDetails", produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<ResponseDataBean> projectReRegistrationDetails(@RequestParam final Long timeboundId, @RequestParam final String sapid) {
		ResponseDataBean response = new ResponseDataBean();
		try {
			Map<String, Object> dataMap = noSlotBookingService.projectReRegistrationDetails(timeboundId, sapid);
			response.setSuccess(true);
			response.setCode(HttpStatus.OK.value());
			response.setMessage("Successfully obtained Project Re-Registration details for student: " + sapid);
			response.setData(dataMap);
			
			logger.info("Project Re-Registration details obtained successfully, response: {}", response);
			return new ResponseEntity<>(response, HttpStatus.OK);
		}
		catch(IllegalArgumentException ex) {
			response.setSuccess(false);
			response.setCode(HttpStatus.BAD_REQUEST.value());
			response.setMessage(ex.getMessage());
			
			logger.error("Unable to obtain Project Re-Registration details for student: {}, Error message : {}", sapid, ex.getMessage());
			return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
		}
		catch(Exception ex) {
			response.setSuccess(false);
			response.setCode(HttpStatus.INTERNAL_SERVER_ERROR.value());
			response.setMessage("Failed to obatin Project Re-Registration details for student: " + sapid);
			
			logger.error("Error while obtaining Project Re-Registration details for student: {}, Exception thrown:", sapid, ex);
			return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}
	
	/**
	 * Saves the noSlot booking request for the student.
	 * @param noSlotBooking - bean containing the noSlot booking details
	 * @param bindingResult - DataBinder for the Validation errors
	 * @return tracking ID of the payment initiated for the noSlot booking
	 */
	@PostMapping(value = "/student/saveNoSlotBookingRequest", produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<ResponseDataBean> saveNoSlotBookingRequest(@Valid @RequestBody final NoSlotBookingDto noSlotBooking, BindingResult bindingResult) {
		ResponseDataBean response = new ResponseDataBean();
		try {
			if(bindingResult.hasErrors()) {
				String errorMessage = bindingResult.getFieldErrors()			//All field related errors (of RequestBody Object) stored as a List
													.stream()
													.map(FieldError::getDefaultMessage)			//Get message from the FieldError Object of the field
													.collect(Collectors.joining("<br/>"));		//Join the messages with a <br> break tag
				
				throw new IllegalArgumentException(errorMessage);		//throw Exception with detail message retrieved from the BindingResult Object
			}
			
			String trackId = noSlotBookingService.saveNoSlotBooking(noSlotBooking.getSapid(), noSlotBooking.getTimeboundId(), noSlotBooking.getType(), 
																	noSlotBooking.getAmount(), noSlotBooking.getPaymentOption(), noSlotBooking.getSource());
			response.setSuccess(true);
			response.setCode(HttpStatus.OK.value());
			response.setMessage("Successfully added noSlot booking and payment record for student: " + noSlotBooking.getSapid());
			response.setData(Collections.singletonMap("trackId", trackId));
			
			logger.info("NoSlot booking record successfully added for student: {} with trackId: {}", noSlotBooking.getSapid(), trackId);
			return new ResponseEntity<>(response, HttpStatus.OK);
		}
		catch(IllegalArgumentException ex) {
			response.setSuccess(false);
			response.setCode(HttpStatus.BAD_REQUEST.value());
			response.setMessage(ex.getMessage());
			
			logger.error("Unable to add noSlot booking and payment record for student: {}, Error message : {}", noSlotBooking.getSapid(), ex.getMessage());
			return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
		}
		catch(Exception ex) {
			response.setSuccess(false);
			response.setCode(HttpStatus.INTERNAL_SERVER_ERROR.value());
			response.setMessage("Failed to add noSlot booking and payment record for student: " + noSlotBooking.getSapid());
			
			logger.error("Error while adding noSlot booking and payment record for student: {}, Exception thrown:", noSlotBooking.getSapid(), ex);
			return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}
	
	/**
	 * Fetches the booking status of student from the provided trackId.
	 * @param sapid - student No.
	 * @param timeboundId - timeboundId of the student
	 * @param trackId - tracking ID
	 * @return Map containing noSlot booking details 
	 */
	@GetMapping(value = "/student/fetchNoSlotBookingStatus", produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<ResponseDataBean> fetchNoSlotBookingStatus(@RequestParam final String sapid, @RequestParam final Long timeboundId, @RequestParam final String trackId) {
		ResponseDataBean response = new ResponseDataBean();
		try {
			List<Map<String, String>> dataList = noSlotBookingService.noSlotBookingStatus(sapid, timeboundId, trackId);
			response.setSuccess(true);
			response.setCode(HttpStatus.OK.value());
			response.setMessage("Successfully fetched Booking details for student: " + sapid + " with trackId: " + trackId);
			response.setData(dataList);
			
			logger.info("NoSlot Booking details obtained successfully, response: {}", response);
			return new ResponseEntity<>(response, HttpStatus.OK);
		}
		catch(IllegalArgumentException ex) {
			response.setSuccess(false);
			response.setCode(HttpStatus.BAD_REQUEST.value());
			response.setMessage(ex.getMessage());
			
			logger.error("Unable to obtain NoSlot Booking details for student: {} with trackId: {}, Error message : {}", sapid, trackId, ex.getMessage());
			return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
		}
		catch(Exception ex) {
			response.setSuccess(false);
			response.setCode(HttpStatus.INTERNAL_SERVER_ERROR.value());
			response.setMessage("Failed to obatin booking details for student: " + sapid + " with trackId: " + trackId);
			
			logger.error("Error while obtaining NoSlot Booking details for student: {} with trackId: {}, Exception thrown:", sapid, trackId, ex);
			return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}
}
