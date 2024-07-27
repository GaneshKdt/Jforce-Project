package com.nmims.controllers;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import com.nmims.beans.VerifyContactDetailsBean;
import com.nmims.interfaces.VerifyContactDetailsInterface;
import com.nmims.services.VerifyOtpService;

@Controller
@RequestMapping("/m")
public class VerifyContactDetailsController {
	
	@Autowired
	VerifyContactDetailsInterface verifyContactDetailsInterface;
	
	Logger logger  = LoggerFactory.getLogger(VerifyContactDetailsController.class);
	
	
	@PostMapping(value="/requestForOTP",consumes ="application/json",produces = "application/json")
	public ResponseEntity<VerifyContactDetailsBean>  requestOTP(@RequestBody VerifyContactDetailsBean verifyBean,HttpServletRequest request,HttpServletResponse response) {
		VerifyContactDetailsBean sendOtp = new VerifyContactDetailsBean();
		try {
			sendOtp =verifyContactDetailsInterface.sendOtpAndInsertIntoTable(verifyBean);
			logger.info("");
		if(sendOtp.isStatus()) {
			return new ResponseEntity<>(sendOtp,HttpStatus.OK);
		}
		sendOtp.setStatus(false);
		return new ResponseEntity<>(sendOtp, HttpStatus.INTERNAL_SERVER_ERROR);
		}
	 catch (Exception e) {
			sendOtp.setMessage(e.getMessage());
			return new ResponseEntity<>(sendOtp, HttpStatus.INTERNAL_SERVER_ERROR);
}
		
		}
	@PostMapping(value="/verifyForOTP",consumes ="application/json",produces = "application/json")
	public ResponseEntity<VerifyContactDetailsBean>  verifyOTP(@RequestBody VerifyContactDetailsBean verifyBean,HttpServletRequest request,HttpServletResponse response) {
		VerifyContactDetailsBean verified= new VerifyContactDetailsBean();
		try {
			verified=verifyContactDetailsInterface.verifyDetails(verifyBean);
			if(verified.isStatus()) {
				return new ResponseEntity<>(  verified, HttpStatus.OK);
			}
			
		} catch (Exception e) {
			// TODO: handle exception
			verified.setMessage(e.getMessage());
			
		}
		return new ResponseEntity<>(verified, HttpStatus.INTERNAL_SERVER_ERROR);
}

}
