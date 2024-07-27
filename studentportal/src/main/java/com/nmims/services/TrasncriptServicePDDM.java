package com.nmims.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.nmims.beans.ServiceRequestStudentPortal;
import com.nmims.beans.StudentStudentPortalBean;
import com.nmims.daos.ServiceRequestDao;
import com.nmims.interfaces.TranscriptServiceInterface;

@Service("transcriptServicePDDM")
public class TrasncriptServicePDDM implements TranscriptServiceInterface {
	
	@Autowired
	ServiceRequestDao serviceRequestDao;

	@Override
	public ServiceRequestStudentPortal checkEligibility(ServiceRequestStudentPortal sr) throws Exception {
		String sapid = sr.getSapId();
		int numberOfMarkEntries = serviceRequestDao.getNumberOfMarkEntriesMBAWX(sapid);
		//If student does not have any marks against his sapid throw error//	
		if (numberOfMarkEntries == 0) {
			throw new Exception("You do not have any mark entries.");
			
		}
		// TODO Auto-generated method stub
		return sr;
	}

	@Override
	public ServiceRequestStudentPortal serviceRequestFee(ServiceRequestStudentPortal sr) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public ServiceRequestStudentPortal createServiceRequest(ServiceRequestStudentPortal sr) throws Exception {
		// TODO Auto-generated method stub
		
		String sapid = sr.getSapId();
		StudentStudentPortalBean student = serviceRequestDao.getSingleStudentsData(sapid);
	
		System.out.println("totalCostOfTranscripts---->" + sr.getAmount());
		if("Yes".equals(sr.getWantAtAddress())){
			sr.setIssued("Y");
			sr.setModeOfDispatch("Courier");
		}else{
			sr.setIssued("N");
			sr.setModeOfDispatch("LC");
		} 
		

		sr.setDescription(sr.getServiceRequestType() + " for student " + sapid);
		sr.setTranStatus(ServiceRequestStudentPortal.TRAN_STATUS_INITIATED);
		sr.setCategory("Exam");
		sr.setCreatedBy(sapid);
		sr.setLastModifiedBy(sapid);
		sr.setSrAttribute("");
		sr.setInformationForPostPayment(sapid);// So that it is used post
		// checking if student applied for more than one transcript or not
		if("".equals(sr.getNoOfCopies()))
		{
			sr.setNoOfCopies("0");
		}
		// payment for follow up steps
		populateServiceRequestObject(sr);
		serviceRequestDao.insertServiceRequest(sr);
		return sr;
	}
	
	private void populateServiceRequestObject(ServiceRequestStudentPortal sr) {
		String trackId = sr.getSapId() + System.currentTimeMillis();
		sr.setTrackId(trackId);
		sr.setRequestStatus(ServiceRequestStudentPortal.REQUEST_STATUS_PAYMENT_PENDING);	
	}

}
