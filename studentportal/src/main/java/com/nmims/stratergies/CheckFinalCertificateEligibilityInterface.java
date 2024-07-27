package com.nmims.stratergies;

import org.springframework.stereotype.Component;

import com.nmims.beans.ServiceRequestStudentPortal;

public interface CheckFinalCertificateEligibilityInterface {

	public abstract ServiceRequestStudentPortal checkFinalCertificateEligibility(ServiceRequestStudentPortal sr) throws Exception;

}
