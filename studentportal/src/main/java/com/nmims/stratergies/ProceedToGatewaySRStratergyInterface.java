package com.nmims.stratergies;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.web.servlet.ModelAndView;

import com.nmims.beans.ServiceRequestStudentPortal;

public interface ProceedToGatewaySRStratergyInterface {
	
	public abstract ModelAndView proceedToPaymentGatewaySr(ServiceRequestStudentPortal sr, String sapId, String serviceRequestId, String paymentOptionName, HttpServletRequest request, HttpServletResponse response) throws Exception;


}
