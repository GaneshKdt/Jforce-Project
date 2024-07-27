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
import com.nmims.stratergies.impl.CheckFinalCertificateEligibilityPDDM;
import com.nmims.stratergies.impl.ProceedToGatewaySRStratergy;
import com.nmims.stratergies.impl.SRFeeResponse;
import com.nmims.stratergies.impl.SaveFinalCertificatePaymentStratergy;
import com.nmims.stratergies.impl.SaveFinalCertificateStratergy;
import com.nmims.stratergies.impl.ServiceRequestFeeStratergy;
import com.nmims.stratergies.impl.ShareAsPostLinkedInCertificateStrategyMBAWX;
@Service("certificatePDDM")
public class CertificatePDDM implements CertificateInterface{
	@Autowired
	private AddToProfileCertificateStratergyLinkedIn addToProfileCertificateStratergyLinkedIn;
	
	@Autowired 
	private SaveFinalCertificateStratergy saveFinalCertificateStratergy;
	
	@Autowired 
	private CheckFinalCertificateEligibilityPDDM checkFinalCertificateEligibility;
	
	@Autowired 
	private ServiceRequestFeeStratergy serviceRequestFeeStratergy;
	
	@Autowired
	private SaveFinalCertificatePaymentStratergy saveFinalCertificatePaymentStratergy;
	
	@Autowired
	private ProceedToGatewaySRStratergy proceedToGatewaySRStratergy;
	
	@Autowired
	private SRFeeResponse sRFeeResponse;
	
	@Autowired
	private ShareAsPostLinkedInCertificateStrategyMBAWX shareAsPostLinkedInCertificateStrategyMBAWX;
	
	@Override
	public ServiceRequestStudentPortal checkFinalCertificateEligibility(ServiceRequestStudentPortal sr) throws Exception {
		// TODO Auto-generated method stub
		return checkFinalCertificateEligibility.checkFinalCertificateEligibility(sr);
	}
	@Override
	public Integer checkFinalCertificateCount(ServiceRequestStudentPortal sr) {
		// TODO Auto-generated method stub
		return null;
	}
	@Override
	public ServiceRequestStudentPortal saveFinalCertificateRequest(ServiceRequestStudentPortal sr) throws Exception {
		return saveFinalCertificateStratergy.saveFinalCertificateRequest(sr);
	}
	@Override
	public ServiceRequestStudentPortal saveFinalCertificateAndPayment(ServiceRequestStudentPortal sr) throws Exception {
		// TODO Auto-generated method stub
		return saveFinalCertificatePaymentStratergy.saveFinalCertificateAndPayment(sr);
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
		return addToProfileCertificateStratergyLinkedIn.shareCertificate(sr);
	}
	@Override
	public void saveFinalCertificateRequestPostPayment(ServiceRequestStudentPortal sr) throws Exception {
		// TODO Auto-generated method stub
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
		// TODO Auto-generated method stub
		return shareAsPostLinkedInCertificateStrategyMBAWX.shareAsPostLinkedInCertifiacteStrategy(sr);
	}
	

}
