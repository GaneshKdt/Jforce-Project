package com.nmims.stratergies.impl;

import java.util.ArrayList;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Service;

import com.nmims.beans.ServiceRequestStudentPortal;
import com.nmims.beans.StudentStudentPortalBean;
import com.nmims.daos.ServiceRequestDao;
import com.nmims.stratergies.ServiceRequestFeeStratergyInterface;

@Service("serviceRequestFeeStratergy")
public class ServiceRequestFeeStratergy implements ServiceRequestFeeStratergyInterface{
	
	private final int SECOND_DIPLOMA_FEE = 1000;

	
	@Autowired
	private ServiceRequestDao serviceRequestDao;

	@Override
	public ServiceRequestStudentPortal mServiceRequestFee(ServiceRequestStudentPortal sr) throws Exception {
		// TODO Auto-generated method stub
		String sapid = sr.getSapId();
		String serviceRequestType =	sr.getServiceRequestType() ;
		//System.out.println("sapid: "+sapid);
		//System.out.println("serviceRequestType: "+serviceRequestType);
		
		HttpHeaders headers = new HttpHeaders();
		headers.add("Content-Type", "application/json");
		
		StudentStudentPortalBean student = serviceRequestDao.getSingleStudentsData(sapid);
		float totalCost=0;
		
		
		boolean isCertificate = student.isCertificateStudent();
		String program = student.getProgram();
					
		
		if (ServiceRequestStudentPortal.DUPLICATE_FEE_RECEIPT.equalsIgnoreCase(serviceRequestType)) {
			 
			totalCost = 100;
			
		 	
		} else if (ServiceRequestStudentPortal.ASSIGNMENT_REVALUATION.equalsIgnoreCase(serviceRequestType)
				|| ServiceRequestStudentPortal.OFFLINE_ASSIGNMENT_REVALUATION.equals(serviceRequestType)) {
			
		} else if (ServiceRequestStudentPortal.DUPLICATE_STUDY_KIT.equalsIgnoreCase(serviceRequestType)) {
			totalCost  = 3000;
		} else if (ServiceRequestStudentPortal.DUPLICATE_ID.equalsIgnoreCase(serviceRequestType)) {
			totalCost = 200;
			
		} else if (ServiceRequestStudentPortal.CHANGE_IN_DOB.equalsIgnoreCase(serviceRequestType)) {
			 totalCost =0;
		} else if (ServiceRequestStudentPortal.SINGLE_BOOK.equalsIgnoreCase(serviceRequestType)) {
			
		     totalCost = 500;
				     
				ArrayList<String> subjectList = serviceRequestDao.getFailedOrCurrentSubjects(sapid);
				if (subjectList != null) { 
					subjectList.remove("Project");
					subjectList.remove("Module 4 - Project");

					if (student.getWaivedOffSubjects() != null && student.getWaivedOffSubjects().size() > 0) {
						ArrayList<String> waivedOffSubjects = student.getWaivedOffSubjects();
						for (String waivedOffSubject : waivedOffSubjects) {
							if (subjectList.contains(waivedOffSubject)) {
								subjectList.remove(waivedOffSubject);// Student cannot
								// order book
								// that is
								// waived off
							}
						}
					}

				}
				sr.setSubjectList(subjectList);
				sr.setSize((subjectList != null ? subjectList.size() : 0));
				

			
		} else if (ServiceRequestStudentPortal.TEE_REVALUATION.equalsIgnoreCase(serviceRequestType)) {
			
		} else if (ServiceRequestStudentPortal.OFFLINE_TEE_REVALUATION.equalsIgnoreCase(serviceRequestType)) {
			
		} else if (ServiceRequestStudentPortal.PHOTOCOPY_OF_ANSWERBOOK.equalsIgnoreCase(serviceRequestType)) {
			
		} else if (ServiceRequestStudentPortal.CHANGE_IN_ID.equalsIgnoreCase(serviceRequestType)) {
			
		     totalCost = 200;
		  
			
			
		} else if (ServiceRequestStudentPortal.REDISPATCH_STUDY_KIT.equalsIgnoreCase(serviceRequestType)) {
			
		     totalCost = 300;
		 
			
		} else if (ServiceRequestStudentPortal.CHANGE_IN_NAME.equalsIgnoreCase(serviceRequestType)) {
			totalCost =0;
		} else if (ServiceRequestStudentPortal.ISSUEANCE_OF_TRANSCRIPT.equalsIgnoreCase(serviceRequestType)) {
			 totalCost =1000;
			 
			 int additionalCopiesCharges =300;
			 sr.setAdditionalCopiesCharges(additionalCopiesCharges);

		}else if (ServiceRequestStudentPortal.ISSUEANCE_OF_BONAFIDE.equalsIgnoreCase(serviceRequestType)) {
			// response = servReqServ.issueBonafide(sr,request);
			totalCost = 100;
		}
		else if (ServiceRequestStudentPortal.ISSUEANCE_OF_MARKSHEET.equalsIgnoreCase(serviceRequestType)) {
			 totalCost = 0;
		} else if (ServiceRequestStudentPortal.ISSUEANCE_OF_CERTIFICATE.equalsIgnoreCase(serviceRequestType)) {
			
			int diplomaIssuedCount = serviceRequestDao.getDiplomaIssuedCount(sr);
			//System.out.println("diplomaIssuedCount = " + diplomaIssuedCount);
			 totalCost =0;
			if (diplomaIssuedCount >= 1) {
				sr.setDuplicateDiploma("true");
				totalCost = SECOND_DIPLOMA_FEE;		
			} 
			
		}else if (ServiceRequestStudentPortal.DE_REGISTERED.equals(serviceRequestType)) {

	
		} else if (ServiceRequestStudentPortal.CHANGE_IN_PHOTOGRAPH.equalsIgnoreCase(serviceRequestType)) {
			totalCost=0;
		}
		else if(ServiceRequestStudentPortal.PROGRAM_DE_REGISTRATION.equalsIgnoreCase(serviceRequestType) && !program.equals("MBA - X")) {
			//System.out.println("Not MBA X");
		//	response = servReqServ.getProgramsRegisteredByStudent(sr);
		}
		else if(ServiceRequestStudentPortal.PROGRAM_DE_REGISTRATION.equalsIgnoreCase(serviceRequestType) && program.equals("MBA - X")) {
			//System.out.println("MBA X");

		//	response = servReqServ.getProgramsRegisteredByMbaXStudent(sr);				
		}			
		//System.out.println("totalCost::::::::"+totalCost);
		
		
		String amount = Integer.toString(Math.round(totalCost));
		//System.out.println(amount);
		sr.setAmount(amount);
		sr.setSapId(sapid);
		sr.setIsCertificate(isCertificate);
		sr.setServiceRequestType(serviceRequestType);
		return sr;
		
	}

}
