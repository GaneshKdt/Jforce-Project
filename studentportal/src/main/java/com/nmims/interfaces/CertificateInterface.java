package com.nmims.interfaces;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.ui.Model;
import org.springframework.ui.ModelMap;
import org.springframework.web.servlet.ModelAndView;


import com.nmims.beans.ServiceRequestStudentPortal;
import com.nmims.beans.StudentStudentPortalBean;

public interface CertificateInterface {
	
	public abstract ServiceRequestStudentPortal checkFinalCertificateEligibility(ServiceRequestStudentPortal sr) throws Exception;
	
	public abstract Integer checkFinalCertificateCount(ServiceRequestStudentPortal sr)  throws Exception;
	
	public abstract ServiceRequestStudentPortal saveFinalCertificateRequest(ServiceRequestStudentPortal srX)  throws Exception;

	public abstract ServiceRequestStudentPortal serviceRequestFee(ServiceRequestStudentPortal sr) throws Exception;
	
	public abstract ServiceRequestStudentPortal saveFinalCertificateAndPayment(ServiceRequestStudentPortal sr)  throws Exception;
	
	public abstract ModelAndView proceedToPaymentGatewaySr(ServiceRequestStudentPortal sr, String sapId, String serviceRequestId, String paymentOptionName, HttpServletRequest request, HttpServletResponse response) throws Exception;

	public abstract void saveFinalCertificateRequestPostPayment(ServiceRequestStudentPortal sr)  throws Exception;
	
	public abstract ServiceRequestStudentPortal addCredentialLinkedIn(ServiceRequestStudentPortal sr)  throws Exception;
	
	public abstract ServiceRequestStudentPortal srFeeResponse(ServiceRequestStudentPortal sr, StudentStudentPortalBean student,HttpServletRequest request, HttpServletResponse respnse, ModelMap model,
			Model m) throws Exception;

	public abstract ServiceRequestStudentPortal previewCertificate(ServiceRequestStudentPortal sr)  throws Exception;
	
	public abstract ServiceRequestStudentPortal shareCertificate(ServiceRequestStudentPortal sr)  throws Exception;
	
}
