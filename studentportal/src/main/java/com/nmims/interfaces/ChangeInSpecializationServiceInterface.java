package com.nmims.interfaces;

import com.nmims.beans.ServiceRequestStudentPortal;

public interface ChangeInSpecializationServiceInterface {
	
	public abstract ServiceRequestStudentPortal checkEligibility(ServiceRequestStudentPortal sr);
	
	public abstract ServiceRequestStudentPortal serviceRequestFee(ServiceRequestStudentPortal sr);
	
	public abstract ServiceRequestStudentPortal createServiceRequest(ServiceRequestStudentPortal sr);

}
