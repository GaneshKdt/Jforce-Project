package com.nmims.stratergies.impl;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import com.nmims.beans.ServiceRequestStudentPortal;
import com.nmims.daos.ServiceRequestDao;
import com.nmims.stratergies.CheckFinalCertificateEligibilityInterface;

@Service("checkFinalCertificateEligibilityMBAWX")
public class CheckFinalCertificateEligibilityMBAWX implements CheckFinalCertificateEligibilityInterface {
	
	@Autowired
	private ServiceRequestDao serviceRequestDao;
	
	private final String SECOND_DIPLOMA_FEE = "1000";


	@Override
	public ServiceRequestStudentPortal checkFinalCertificateEligibility(ServiceRequestStudentPortal sr) throws Exception {
		
		String programStatus = serviceRequestDao.getProgramStatusOfStudent(sr.getSapId());
		if(!StringUtils.isBlank(programStatus) && programStatus.equals("Program Withdrawal")) {
			sr = checkFinalCertificateEligibilityForExitProgram(sr);
			return sr;
		}
		 
		List<String> subjectsCleared;
		int diplomaIssuedCount;
		List<Integer> q7ClearedSubject = new ArrayList<>();
		List<Integer> q8ClearedSubject = new ArrayList<>();
		List<Integer>clearedQ7AndQ8PssIdList = new ArrayList<>();
		List<String> clearedQ7AndQ8Subjects = new ArrayList<>();
		
		sr.setWantAtAddress("No");		
		int noOfSubjectsToClear = serviceRequestDao.noOfSubjectsToClear(sr.getSapId());

		if(noOfSubjectsToClear ==  0 ) {
			throw new Exception("No Applicable subjects found!");
			}
		subjectsCleared = serviceRequestDao.getSubjectsClearedCurrentProgramMBAWX(sr.getSapId());
		
		//Added to get Q7 and Q8 cleared subjects count
		q7ClearedSubject = serviceRequestDao.getClearedPssIdForQ7(sr.getSapId());
		q8ClearedSubject = serviceRequestDao.getClearedPssIdForQ8(sr.getSapId());
		clearedQ7AndQ8PssIdList.addAll(q7ClearedSubject);
		clearedQ7AndQ8PssIdList.addAll(q8ClearedSubject);
		//To get Q7 and Q8 subject names
		if(!CollectionUtils.isEmpty(clearedQ7AndQ8PssIdList))
		{
			clearedQ7AndQ8Subjects = serviceRequestDao.getSubjectNameByPssIds(clearedQ7AndQ8PssIdList);
			subjectsCleared.addAll(clearedQ7AndQ8Subjects);
		}
		//need to added Waivedoff logic later here	 
		
		
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
		if(!closedSr ) {
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
	

