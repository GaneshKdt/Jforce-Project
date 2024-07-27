package com.nmims.stratergies.impl;

import java.util.HashMap;

import org.springframework.stereotype.Service;

import com.nmims.beans.ServiceRequestStudentPortal;
import com.nmims.stratergies.CertificateCountStratergyInterface;

@Service("certificateCountStratergy")
public class CertificateCountStratergy implements CertificateCountStratergyInterface{

	@Override
	public Integer checkFinalCertificateCount(ServiceRequestStudentPortal sr) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}
//	HashMap<String, String> response = new HashMap<>();
//	ServiceRequest sr = new ServiceRequest();
//	sr.setSapId((String) request.getParameter("sapId"));
//	sr.setServiceRequestType((String) request.getParameter("requestType"));
//	//System.out.println("sapId : " + sr.getSapId() + " | serviceRequest : " + sr.getServiceRequestType());
//	int diplomaIssuedCount = serviceRequestDao.getDiplomaIssuedCount(sr);
//	int diplomaCertInitiatedCount = serviceRequestDao.getDiplomaCertInintiatedCount(sr);
//	////System.out.println("diplomaIssuedCount = " + diplomaIssuedCount);
//	if (diplomaIssuedCount >= 1) {
//		response.put("result", "Already free service request there");
//    	response.put("status","500");
//    	return response;
//	}
//	if (diplomaCertInitiatedCount >= 1) {
//		response.put("result", "Your Transaction is under Process! Please wait.");
//    	response.put("status","500");
//    	return response;
//	}
//	response.put("result", "Applicable for free final certificate service request");
//	response.put("status","200");
//	return response;
}
