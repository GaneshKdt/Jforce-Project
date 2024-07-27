package com.nmims.interfaces;

import java.util.List;
import java.util.Map;

import com.nmims.beans.ServiceRequestStudentPortal;
import com.nmims.daos.ServiceRequestDao;

public interface StudentSRForSFDCInterface {
	List<ServiceRequestStudentPortal> getStudentSR(List<ServiceRequestStudentPortal> list, Map<String, String> mapOfServiceRequestTypeAndTAT);
	
	List<ServiceRequestStudentPortal> getAllServiceRequestFromSapIDForStudentDetailDashBoard(String sapid, ServiceRequestDao sDao);

	Map<String, String> mapOfActiveSRTypesAndTAT(ServiceRequestDao sDao);
}
