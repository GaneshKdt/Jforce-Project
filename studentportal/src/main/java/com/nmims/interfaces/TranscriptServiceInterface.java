package com.nmims.interfaces;

import com.nmims.beans.ServiceRequestStudentPortal;

public interface TranscriptServiceInterface {
	
	public abstract ServiceRequestStudentPortal checkEligibility(ServiceRequestStudentPortal sr) throws Exception;
	
	public abstract ServiceRequestStudentPortal serviceRequestFee(ServiceRequestStudentPortal sr);
	
	public abstract ServiceRequestStudentPortal createServiceRequest(ServiceRequestStudentPortal sr) throws Exception;

}
