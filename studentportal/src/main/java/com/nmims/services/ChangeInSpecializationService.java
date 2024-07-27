package com.nmims.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.nmims.beans.ServiceRequestStudentPortal;
import com.nmims.beans.StudentStudentPortalBean;
import com.nmims.daos.ServiceRequestDao;
import com.nmims.interfaces.ChangeInSpecializationServiceInterface;

@Service("changeInSpecializationService")
public class ChangeInSpecializationService implements ChangeInSpecializationServiceInterface {

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

		String sapid = sr.getSapId();
		StudentStudentPortalBean student = serviceRequestDao.getSingleStudentsData(sapid);
	
		System.out.println("totalCostOfChangeInSpecialization---->" + sr.getAmount());

		sr.setDescription(sr.getServiceRequestType() + " for student " + sapid);
		sr.setTranStatus(ServiceRequestStudentPortal.TRAN_STATUS_INITIATED);
		sr.setCategory("Exam");
		sr.setCreatedBy(sapid);
		sr.setLastModifiedBy(sapid);
		sr.setSrAttribute("");
		sr.setInformationForPostPayment(sapid);// So that it is used post
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
