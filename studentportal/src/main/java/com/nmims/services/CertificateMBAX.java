package com.nmims.services;

import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.ui.Model;
import org.springframework.ui.ModelMap;
import org.springframework.web.servlet.ModelAndView;

import com.nmims.beans.ServiceRequestStudentPortal;
import com.nmims.beans.StudentStudentPortalBean;
import com.nmims.daos.ServiceRequestDao;
import com.nmims.interfaces.CertificateInterface;
import com.nmims.stratergies.impl.AddToProfileCertificateStratergyLinkedIn;
import com.nmims.stratergies.impl.ProceedToGatewaySRStratergy;
import com.nmims.stratergies.impl.SRFeeResponse;
import com.nmims.stratergies.impl.SaveFinalCertificatePaymentStratergy;
import com.nmims.stratergies.impl.SaveFinalCertificateStratergy;
import com.nmims.stratergies.impl.ServiceRequestFeeStratergy;
import com.nmims.stratergies.impl.ShareAsPostLinkedInCertificateStrategyPG ;

@Service("certificateMBAX")
public class CertificateMBAX implements CertificateInterface  {
	@Autowired
	private ServiceRequestDao serviceRequestDao;
	
	@Autowired
	private AddToProfileCertificateStratergyLinkedIn addToProfileCertificateStratergyLinkedIn;

	private final String SECOND_DIPLOMA_FEE = "1000";
	
	@Autowired 
	private SaveFinalCertificateStratergy saveFinalCertificateStratergy;

	@Autowired
	private SaveFinalCertificatePaymentStratergy saveFinalCertificatePaymentStratergy;
	
	@Autowired
	private ShareAsPostLinkedInCertificateStrategyPG  shareAsPostLinkedInCertificateStrategyPG ;
	
	@Autowired
	private ServiceRequestFeeStratergy serviceRequestFeeStratergy;
	
	
	@Autowired
	private ProceedToGatewaySRStratergy proceedToGatewaySRStratergy;
	
	@Autowired
	private SRFeeResponse sRFeeResponse;
	
	@Override
	public ServiceRequestStudentPortal checkFinalCertificateEligibility(ServiceRequestStudentPortal sr) throws Exception {
		// TODO Auto-generated method stub
				int noOfSubjectsToClear;
				List<String> subjectsCleared;
				int diplomaIssuedCount;
				
				sr.setWantAtAddress("No");		
				noOfSubjectsToClear = serviceRequestDao.noOfSubjectsToClear(sr.getSapId());
				
				//System.out.println("Applicable subjects = "+applicableSubjects.size());
				if(noOfSubjectsToClear ==  0 ) {
					throw new Exception("No Applicable subjects found!");
					}
				
				subjectsCleared = serviceRequestDao.getClearedSubjectsForStructureChangeStudentMBAX(sr.getSapId());
				if(subjectsCleared.size() == 0) {
					subjectsCleared = serviceRequestDao.getSubjectsClearedCurrentProgramMBAX(sr.getSapId());
				}
				
				//need to added Waivedoff logic later here		
				
				
				//no of subjects required to clear program 
				
				if (subjectsCleared.size() != noOfSubjectsToClear) {
					throw new Exception("You have not yet cleared all subjects!");
				} else {
					diplomaIssuedCount = serviceRequestDao.getDiplomaIssuedCount(sr);
					int PaymentInitiatedCount = serviceRequestDao.getDiplomaCertInintiatedCount(sr);
					if (PaymentInitiatedCount >= 1) {
						throw new Exception("Your previous transaction status is in initiate stage. Please try again after 1 hour.");
					}
					//System.out.println("diplomaIssuedCount = " + diplomaIssuedCount);
					if (diplomaIssuedCount >= 1) {
						sr.setCharges(SECOND_DIPLOMA_FEE);
						sr.setDuplicateDiploma("true");
					} else {
						sr.setCharges("0");
						sr.setDuplicateDiploma("false");
					}

					return sr;
				}
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
		return null;
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
		// TODO Auto-generated method stub
		return shareAsPostLinkedInCertificateStrategyPG.shareAsPostLinkedInCertifiacteStrategy(sr);
	}

}
