package com.nmims.interfaces;

import com.nmims.beans.ServiceRequestStudentPortal;

public interface SemDeregisterInterface {
	
	public abstract ServiceRequestStudentPortal checkSemDeregisterEligibility(ServiceRequestStudentPortal sr);
	
	public abstract ServiceRequestStudentPortal serviceRequestFee(ServiceRequestStudentPortal sr) throws Exception;


}
