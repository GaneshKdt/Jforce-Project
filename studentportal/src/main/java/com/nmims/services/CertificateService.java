package com.nmims.services;


import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.ui.Model;
import org.springframework.ui.ModelMap;
import org.springframework.web.servlet.ModelAndView;

import com.nmims.beans.ServiceRequestStudentPortal;
import com.nmims.beans.StudentStudentPortalBean;
import com.nmims.interfaces.CertificateInterface;
import com.nmims.stratergies.impl.AddToProfileCertificateStratergyLinkedInPG;
import com.nmims.stratergies.impl.CheckFinalCertificateEligibility;
import com.nmims.stratergies.impl.CheckFinalCertificateEligibilityMBAWX;

import com.nmims.stratergies.impl.ProceedToGatewaySRStratergy;
import com.nmims.stratergies.impl.SRFeeResponse;
import com.nmims.stratergies.impl.SaveFinalCertificatePaymentStratergy;
import com.nmims.stratergies.impl.SaveFinalCertificateStratergy;
import com.nmims.stratergies.impl.ServiceRequestFeeStratergy;
import com.nmims.stratergies.impl.ShareAsPostLinkedInCertificateStrategyPG;

@Service("certificateService")
public class CertificateService implements CertificateInterface {
	
	@Autowired
	private AddToProfileCertificateStratergyLinkedInPG addToProfileCertificateStratergyLinkedInPG;
	
	@Autowired 
	private SaveFinalCertificateStratergy saveFinalCertificateStratergy;
	
	@Autowired 
	private ServiceRequestFeeStratergy serviceRequestFeeStratergy;
	
	@Autowired
	private SaveFinalCertificatePaymentStratergy saveFinalCertificatePaymentStratergy;
	
	@Autowired
	private ShareAsPostLinkedInCertificateStrategyPG shareAsPostLinkedInCertificateStrategyPG;
	
	@Autowired
	private ProceedToGatewaySRStratergy proceedToGatewaySRStratergy;
	
	@Autowired
	private CheckFinalCertificateEligibility checkFinalCertificateEligibility;
	
	@Autowired
	private SRFeeResponse sRFeeResponse;
		
	@Value("${SERVICE_REQUEST_FILES_PATH}")
	private String SERVICE_REQUEST_FILES_PATH;

	
	@Override
	public ServiceRequestStudentPortal checkFinalCertificateEligibility(ServiceRequestStudentPortal sr) throws Exception {
		// TODO Auto-generated method stub

//		return saveFinalCertificateStratergy.saveFinalCertificateRequest(sr);
        return checkFinalCertificateEligibility.checkFinalCertificateEligibility(sr);
	}
	
	@Override
	public Integer checkFinalCertificateCount(ServiceRequestStudentPortal sr) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public ServiceRequestStudentPortal saveFinalCertificateAndPayment(ServiceRequestStudentPortal sr) throws Exception {
		// TODO Auto-generated method stub
		return saveFinalCertificatePaymentStratergy.saveFinalCertificateAndPayment(sr);
	}

	@Override
	public ServiceRequestStudentPortal saveFinalCertificateRequest(ServiceRequestStudentPortal sr)
			throws Exception {
		// TODO Auto-generated method stub
		return saveFinalCertificateStratergy.saveFinalCertificateRequest(sr);
	}

	@Override
	public ServiceRequestStudentPortal serviceRequestFee(ServiceRequestStudentPortal sr) throws Exception {
		// TODO Auto-generated method stub
		return serviceRequestFeeStratergy.mServiceRequestFee(sr);
	}

	@Override
	public ModelAndView proceedToPaymentGatewaySr(ServiceRequestStudentPortal sr, String sapId, String serviceRequestId, String paymentOptionName, HttpServletRequest request, HttpServletResponse response) throws Exception {
		// TODO Auto-generated method stub
		return proceedToGatewaySRStratergy.proceedToPaymentGatewaySr(sr, sapId, serviceRequestId, paymentOptionName, request, response);
	}

	@Override
	public ServiceRequestStudentPortal addCredentialLinkedIn(ServiceRequestStudentPortal sr) throws Exception {
		// TODO Auto-generated method stub
		return addToProfileCertificateStratergyLinkedInPG.shareCertificate(sr);
	}

	@Override
	public void saveFinalCertificateRequestPostPayment(ServiceRequestStudentPortal sr) throws Exception {
		// TODO Auto-generated method stub
		//return null;
		
	}

	@Override
	public ServiceRequestStudentPortal srFeeResponse(ServiceRequestStudentPortal sr, StudentStudentPortalBean student, HttpServletRequest request, HttpServletResponse respnse, ModelMap model,
			Model m) throws Exception {
		// TODO Auto-generated method stub
		return sRFeeResponse.srFeeResponse(sr, student, request, respnse, model, m);

	}

	@Override
	public ServiceRequestStudentPortal previewCertificate(ServiceRequestStudentPortal sr) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public ServiceRequestStudentPortal shareCertificate(ServiceRequestStudentPortal sr) throws Exception {
		return shareAsPostLinkedInCertificateStrategyPG.shareAsPostLinkedInCertifiacteStrategy(sr);
	}
	
}
