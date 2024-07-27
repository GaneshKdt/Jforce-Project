package com.nmims.services;

import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.nmims.beans.ServiceRequestStudentPortal;
import com.nmims.beans.StudentStudentPortalBean;
import com.nmims.daos.ServiceRequestDao;
import com.nmims.interfaces.SubjectRepeatSR;

@Service
public class SubjectRepeatSrMBAX implements SubjectRepeatSR{

	@Autowired
	private ServiceRequestDao serviceRequestDao;
	
	@Value("${CURRENT_MBAX_ACAD_MONTH}")
	private String CURRENT_MBAX_ACAD_MONTH;
	
	@Value("${CURRENT_MBAX_ACAD_YEAR}")
	private String CURRENT_MBAX_ACAD_YEAR;
	
	@Value("${MBAX_TERM_REPEAT_SR_CHARGES}")
	private String MBAX_TERM_REPEAT_SR_CHARGES;
	
	
	@Override
	public ServiceRequestStudentPortal getSubjectRepeatStatusForStudent(String sapid) {
		// TODO Auto-generated method stub
		ServiceRequestStudentPortal sr = new ServiceRequestStudentPortal();
		try {
			boolean isRegistrationLiveForStudent = serviceRequestDao.getReRegLiveMBAX(sapid, CURRENT_MBAX_ACAD_MONTH, CURRENT_MBAX_ACAD_YEAR, ServiceRequestStudentPortal.SUBJECT_REPEAT);
			
			if(isRegistrationLiveForStudent) {
				List<ServiceRequestStudentPortal> failedSubjectsList =  serviceRequestDao.getFailedSubjectsForStudentMBAX(sapid, MBAX_TERM_REPEAT_SR_CHARGES);
				
				List<ServiceRequestStudentPortal> repeatSubjectsApplied =  serviceRequestDao.getRepeatAppliedSubjectsMBAWX(sapid, CURRENT_MBAX_ACAD_MONTH, CURRENT_MBAX_ACAD_YEAR, ServiceRequestStudentPortal.SUBJECT_REPEAT);
					
				if(failedSubjectsList.size() > 0) {
					sr.setRepeatSubjects(failedSubjectsList);
					sr.setRepeatSubjectsApplied(repeatSubjectsApplied);
					sr.setError("false");
				} else {
					sr.setError("true");
					sr.setErrorMessage("No Subjects available!");
				}
				
			} else {
				sr.setError("true");
				sr.setErrorMessage("Subject Repeat not live at the moment.");
			}
		} catch(Exception e) {
			e.printStackTrace();
			sr.setError("Error checking service request live status.");
			sr.setErrorMessage(e.getMessage());
		}
		return sr;
	}

	@Override
	public ServiceRequestStudentPortal saveSubjectRegistrationSRPayment(ServiceRequestStudentPortal sr) {
		// TODO Auto-generated method stub
		String sapid = sr.getSapId();
		String charges = MBAX_TERM_REPEAT_SR_CHARGES;
		// charges for capstone subject repeat is 4000.
		List<ServiceRequestStudentPortal> subjects = sr.getRepeatSubjects();
		
		String trackIdForMultipleMarksheets = sapid+System.currentTimeMillis(); //Since if we set this value in populateServicebean in the loop,the trackId does not remain unique since the loop runs and some time is lost//	
		String totalAmount ;
        
	    int baseCharge = Integer.parseInt(charges);
	    totalAmount  = Integer.toString(baseCharge * subjects.size());
		sr.setAmount(totalAmount);
		String desc ="";
		for (ServiceRequestStudentPortal subjectInfo : subjects) {
			
			ServiceRequestStudentPortal srToInsert = new ServiceRequestStudentPortal();
			srToInsert.setServiceRequestType(ServiceRequestStudentPortal.SUBJECT_REPEAT);
			srToInsert.setSapId(sapid);
			srToInsert.setSem(subjectInfo.getSem());
			srToInsert.setTrackId(trackIdForMultipleMarksheets);

			srToInsert.setDescription(ServiceRequestStudentPortal.SUBJECT_REPEAT + " for student " + sapid + " for Sem : " + subjectInfo.getSem() + " for Subject : " + subjectInfo.getSubject());
			srToInsert.setTranStatus(ServiceRequestStudentPortal.TRAN_STATUS_INITIATED);
			srToInsert.setRequestStatus(ServiceRequestStudentPortal.REQUEST_STATUS_PAYMENT_PENDING);			

			srToInsert.setYear(CURRENT_MBAX_ACAD_YEAR);
			srToInsert.setMonth(CURRENT_MBAX_ACAD_MONTH);
			
			srToInsert.setAmount(totalAmount);
			srToInsert.setInformationForPostPayment(subjectInfo.getSubject());
			srToInsert.setPaymentOption(sr.getPaymentOption());
			srToInsert.setDevice(sr.getDevice());

			serviceRequestDao.insertServiceRequest(srToInsert);
			serviceRequestDao.insertServiceRequestHistory(srToInsert);
			sr.setId(srToInsert.getId());
		
				desc +="\n>> for Sem : " + subjectInfo.getSem() + ", Subject : " + subjectInfo.getSubject();

		}
		sr.setDescription(ServiceRequestStudentPortal.SUBJECT_REPEAT + " for student " + sapid + " "+desc);
		sr.setServiceRequestType(ServiceRequestStudentPortal.SUBJECT_REPEAT);
	
		sr.setTrackId(trackIdForMultipleMarksheets); 
		sr.setProductType("MBAX"); 
		String paymentUrl = "proceedToPaymentGatewaySr?sapId="+sapid+"&paymentOptionName="+sr.getPaymentOption()+"&serviceRequestId="+sr.getId()+ "&productType=" + sr.getProductType();
		sr.setPaymentUrl(paymentUrl);
		
		return sr;
	}

}
