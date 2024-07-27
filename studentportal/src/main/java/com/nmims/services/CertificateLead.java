package com.nmims.services;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.ui.Model;
import org.springframework.ui.ModelMap;
import org.springframework.web.servlet.ModelAndView;

import com.nmims.beans.ServiceRequestStudentPortal;
import com.nmims.beans.StudentStudentPortalBean;
import com.nmims.interfaces.CertificateInterface;
import com.nmims.stratergies.impl.AddToProfileCertificateStratergyLinkedIn;
import com.nmims.stratergies.impl.ShareAsPostLinkedInCertificateStrategyPG;

@Service("certificateLead")
public class CertificateLead implements CertificateInterface {
	
	@Autowired
	private AddToProfileCertificateStratergyLinkedIn addToProfileCertificateStratergyLinkedIn;
	
	@Autowired
	private ShareAsPostLinkedInCertificateStrategyPG shareAsPostLinkedInCertificateStrategyPG;

	@Override
	public ServiceRequestStudentPortal checkFinalCertificateEligibility(ServiceRequestStudentPortal sr) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Integer checkFinalCertificateCount(ServiceRequestStudentPortal sr) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public ServiceRequestStudentPortal saveFinalCertificateAndPayment(ServiceRequestStudentPortal sr) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void saveFinalCertificateRequestPostPayment(ServiceRequestStudentPortal sr) {
		// TODO Auto-generated method stub
	//	return null;
	}	

	@Override
	public ServiceRequestStudentPortal saveFinalCertificateRequest(ServiceRequestStudentPortal sr)
			throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public ServiceRequestStudentPortal serviceRequestFee(ServiceRequestStudentPortal sr) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public ModelAndView proceedToPaymentGatewaySr(ServiceRequestStudentPortal sr, String sapId, String serviceRequestId, String paymentOptionName, HttpServletRequest request, HttpServletResponse response) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public ServiceRequestStudentPortal addCredentialLinkedIn(ServiceRequestStudentPortal sr) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public ServiceRequestStudentPortal srFeeResponse(ServiceRequestStudentPortal sr, StudentStudentPortalBean student,HttpServletRequest request, HttpServletResponse respnse, ModelMap model,
			Model m) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public ServiceRequestStudentPortal previewCertificate(ServiceRequestStudentPortal sr) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public ServiceRequestStudentPortal shareCertificate(ServiceRequestStudentPortal sr) throws Exception {
		// TODO Auto-generated method stub
		return shareAsPostLinkedInCertificateStrategyPG.shareAsPostLinkedInCertifiacteStrategy(sr);
	}
}
