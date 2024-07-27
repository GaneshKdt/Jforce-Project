package com.nmims.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.view.RedirectView;

import com.nmims.beans.ServiceRequestStudentPortal;
import com.nmims.beans.StudentStudentPortalBean;
import com.nmims.daos.ServiceRequestDao;
import com.nmims.interfaces.TranscriptServiceInterface;

@Service("transcriptServicePG")
public class TranscriptServicePG implements TranscriptServiceInterface
{
	@Autowired
	ServiceRequestDao serviceRequestDao;
	
	@Override
	public ServiceRequestStudentPortal checkEligibility(ServiceRequestStudentPortal sr) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public ServiceRequestStudentPortal serviceRequestFee(ServiceRequestStudentPortal sr) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public ServiceRequestStudentPortal createServiceRequest(ServiceRequestStudentPortal sr) {
		// TODO Auto-generated method stub
		System.out.println("PG Riyaaa");
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
		
		int numberOfMarkEntries = serviceRequestDao.getNumberOfMarkEntries(student.getSapid());
		System.out.println("numberOfMarkEntries" + numberOfMarkEntries);
		//If student does not have any marks against his sapid throw error//	
		if (numberOfMarkEntries == 0) {
			sr.setError("You donot have any mark entries.");
			
			return sr;
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
