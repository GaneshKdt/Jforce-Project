package com.nmims.stratergies.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.nmims.beans.ServiceRequestDocumentBean;
import com.nmims.beans.ServiceRequestStudentPortal;
import com.nmims.beans.StudentStudentPortalBean;
import com.nmims.daos.ServiceRequestDao;
import com.nmims.services.ServiceRequestService;
import com.nmims.stratergies.SaveFinalCertificatePaymentStratergyInterface;

@Service("saveFinalCertificatePaymentStratergy")
public class SaveFinalCertificatePaymentStratergy implements SaveFinalCertificatePaymentStratergyInterface{

	@Autowired
	private ServiceRequestDao serviceRequestDao;

	@Autowired
	private ServiceRequestService servReqServ;
	
	@Override
	public ServiceRequestStudentPortal saveFinalCertificateAndPayment(ServiceRequestStudentPortal sr) throws Exception {
		// TODO Auto-generated method stub
		String sapid = sr.getSapId();
		String studentAddress = sr.getPostalAddress();
		StudentStudentPortalBean student = serviceRequestDao.getSingleStudentsData(sapid);
		student.setAddress(studentAddress);// Setting address since it was failing for case 77114000467
		//boolean isCertificate = isStudentOfCertificate(student.getProgram());
		boolean isCertificate = student.isCertificateStudent();
		if("Yes".equals(sr.getWantAtAddress())){
			sr.setIssued("Y");
			sr.setModeOfDispatch("Courier");
			sr.setCourierAmount(100+"");
		}else{
			sr.setIssued("N");
			sr.setModeOfDispatch("LC");
			sr.setCourierAmount(0+"");
		}
		
		sr.setDescription(sr.getServiceRequestType() + " for student " + sapid);
		sr.setTranStatus(ServiceRequestStudentPortal.TRAN_STATUS_INITIATED);
		sr.setCategory("Exam");
		sr.setCreatedBy(sapid);
		sr.setSrAttribute("");
		sr.setLastModifiedBy(sapid);
		sr.setInformationForPostPayment(sapid);// So that it is used post
		
		// payment for follow up steps
		populateServiceRequestObject(sr);
		if(sr.getAffidavit() != null) {
			sr.setAdditionalInfo1("Spouse");
		}
		serviceRequestDao.insertServiceRequest(sr);
		String courierAmount = isCertificate ? generateAmountBasedOnCriteria(sr.getCourierAmount(),"GST") : sr.getCourierAmount();
		sr.setCourierAmount(courierAmount);
		// be used in post payment
		

		if (sr.getFirCopy()!= null) {
			ServiceRequestDocumentBean document = new ServiceRequestDocumentBean();
			document.setServiceRequestId(sr.getId());
			servReqServ.uploadFile(document, sr.getFirCopy(), sapid + "_FIR");

			if (document.getErrorMessage() == null) {
				//System.out.println("FIR Copy uploaded.");
				document.setDocumentName("FIR for Duplicate Final Certificate");
				serviceRequestDao.insertServiceRequestDocument(document);
				sr.setHasDocuments("Y");
				serviceRequestDao.updateDocumentStatus(sr);
				
				ServiceRequestStudentPortal bean = serviceRequestDao.getServiceRequestBySrId(sr.getId());
				serviceRequestDao.insertServiceRequestStatusHistory(bean, "Update");
			} else {
				sr.setError("Error in uploading document " + document.getErrorMessage());
				//return checkFinalCertificateEligibility(sr);
				return sr;
			}
		}

		if (sr.getIndemnityBond() != null) {
			ServiceRequestDocumentBean document = new ServiceRequestDocumentBean();
			document.setServiceRequestId(sr.getId());
			servReqServ.uploadFile(document, sr.getIndemnityBond(), sapid + "_Indemnity_Bond");

			if (document.getErrorMessage() == null) {
				//System.out.println("Indemnity Bond uploaded.");
				document.setDocumentName("Indemnity Bond for Duplicate Final Certificate");
				serviceRequestDao.insertServiceRequestDocument(document);
				sr.setHasDocuments("Y");
				serviceRequestDao.updateDocumentStatus(sr);
			} else {
				sr.setError("Error in uploading document " + document.getErrorMessage());
				//return checkFinalCertificateEligibility(sr);
				return sr;
			}
		}
		
		
		if (sr.getAffidavit() != null) {
			ServiceRequestDocumentBean document = new ServiceRequestDocumentBean();
			document.setServiceRequestId(sr.getId());
			servReqServ.uploadFile(document, sr.getAffidavit(), sapid + "_Affidavit");
			if (document.getErrorMessage() == null) {
				//System.out.println("FIR Copy uploaded.");
				document.setDocumentName("Affidavit/Marriage Certificate for Duplicate Final Certificate");
				serviceRequestDao.insertServiceRequestDocument(document);
				sr.setHasDocuments("Y");
				serviceRequestDao.updateDocumentStatus(sr);
			} else {
				sr.setError("Error in uploading document " + document.getErrorMessage());
				//return checkFinalCertificateEligibility(sr);
				//return checkFinalCertificateEligibilitynew(sr);
			}
		}
		sr.setFirCopy(null);
		sr.setIndemnityBond(null);
		sr.setAffidavit(null);
		//System.out.println("Student address-->" + student.getAddress());
		
		/*return proceedToPayOptions(model,requestId,ra);*/
		//return new ModelAndView(new RedirectView("pay"), model);
		return sr;
	}


	
private void populateServiceRequestObject(ServiceRequestStudentPortal sr) {
	String trackId = sr.getSapId() + System.currentTimeMillis();
	sr.setTrackId(trackId);
	sr.setRequestStatus(ServiceRequestStudentPortal.REQUEST_STATUS_PAYMENT_PENDING);	
}

public String generateAmountBasedOnCriteria(String amount,String criteria){
	double calculatedAmount = 0.0;
	switch(criteria){
	case "GST":
		calculatedAmount = Double.parseDouble(amount) + (0.18 * Double.parseDouble(amount));
		break;
	}
	//System.out.println("generateAmountBasedOnCriteria"+calculatedAmount);
	return String.valueOf(calculatedAmount);
	
}


}
