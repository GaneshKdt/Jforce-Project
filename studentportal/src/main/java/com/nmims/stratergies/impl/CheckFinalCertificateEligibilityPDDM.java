package com.nmims.stratergies.impl;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.nmims.beans.ServiceRequestStudentPortal;
import com.nmims.daos.ServiceRequestDao;
import com.nmims.stratergies.CheckFinalCertificateEligibilityInterface;
@Service("checkFinalCertificateEligibilityPDDM")
public class CheckFinalCertificateEligibilityPDDM implements CheckFinalCertificateEligibilityInterface{
	
	@Autowired
	private ServiceRequestDao serviceRequestDao;
	
	private final String SECOND_DIPLOMA_FEE = "1000";


	@Override
	public ServiceRequestStudentPortal checkFinalCertificateEligibility(ServiceRequestStudentPortal sr) throws Exception {
		// TODO Auto-generated method stub
		String programStatus=serviceRequestDao.getProgramStatusOfStudent(sr.getSapId());
		if(!StringUtils.isBlank(programStatus) && programStatus.equals("Program Withdrawal")) {
			sr = checkFinalCertificateEligibilityForExitProgram(sr);
			return sr;
		}
		
		
		int noOfSubjectsToClear;
		List<String> subjectsCleared;
		int diplomaIssuedCount;
		
		sr.setWantAtAddress("No");		
		noOfSubjectsToClear = serviceRequestDao.noOfSubjectsToClear(sr.getSapId());

		if(noOfSubjectsToClear ==  0 ) {
			throw new Exception("No Applicable subjects found!");
			}
		subjectsCleared = serviceRequestDao.getSubjectsClearedCurrentProgramPDDM(sr.getSapId());

		//no of subjects required to clear program 
	
		if (subjectsCleared.size() != noOfSubjectsToClear) {
			throw new Exception("You have not yet cleared all subjects!");
		} else {
			diplomaIssuedCount = serviceRequestDao.getDiplomaIssuedCount(sr);
			if (diplomaIssuedCount >= 1) {
				sr.setCharges(SECOND_DIPLOMA_FEE);
				sr.setDuplicateDiploma("true");
			} else {
				sr.setCharges("0");
				sr.setDuplicateDiploma("false");
			}

			return sr;
		}	
	}
	
	public ServiceRequestStudentPortal checkFinalCertificateEligibilityForExitProgram(ServiceRequestStudentPortal sr) throws Exception {
		int diplomaIssuedCount;	
		sr.setWantAtAddress("No");		

		boolean closedSr = serviceRequestDao.getStudentsClosedExitSR(sr.getSapId());
		if(!closedSr) {
			throw new Exception( "Please raise Exit Program Service Request first. If you already raised please wait for Approval");	
		}
		//no of subjects required to clear program 
	
			diplomaIssuedCount = serviceRequestDao.getDiplomaIssuedCount(sr);
			if (diplomaIssuedCount >= 1) {
				sr.setCharges(SECOND_DIPLOMA_FEE);
				sr.setDuplicateDiploma("true");
			} else {
				sr.setCharges("0");
				sr.setDuplicateDiploma("false");
			}
			return sr;
		}

}
