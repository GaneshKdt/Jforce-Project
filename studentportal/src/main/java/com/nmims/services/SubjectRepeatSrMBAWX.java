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
public class SubjectRepeatSrMBAWX implements SubjectRepeatSR{
	
	@Value("${CURRENT_MBAWX_ACAD_MONTH}")
	private String CURRENT_MBAWX_ACAD_MONTH;
	
	@Value("${CURRENT_MBAWX_ACAD_YEAR}")
	private String CURRENT_MBAWX_ACAD_YEAR;
	
	@Value("${MBAWX_TERM_REPEAT_SR_CHARGES}")
	private String MBAWX_TERM_REPEAT_SR_CHARGES;
	
	@Autowired
	private ServiceRequestDao serviceRequestDao;
	
	@Override
	public ServiceRequestStudentPortal getSubjectRepeatStatusForStudent(String sapid) {

		ServiceRequestStudentPortal sr = new ServiceRequestStudentPortal();
		try {

			StudentStudentPortalBean student = serviceRequestDao.getSingleStudentsData(sapid);
			
			boolean isRegistrationLiveForStudent = serviceRequestDao.getReRegLiveMBAWX(sapid, CURRENT_MBAWX_ACAD_MONTH, CURRENT_MBAWX_ACAD_YEAR, ServiceRequestStudentPortal.SUBJECT_REPEAT_MBAWX);
			

			String charges = MBAWX_TERM_REPEAT_SR_CHARGES;
			
			if(!StringUtils.isBlank(student.getEnrollmentMonth()) && !StringUtils.isBlank(student.getEnrollmentYear())) {
				String monthYear = student.getEnrollmentMonth() + student.getEnrollmentYear();
				if("Jul2019".equals(monthYear) || "Oct2019".equals(monthYear)) {
					charges = "6000";
				}
			}

			List<ServiceRequestStudentPortal> failedSubjectsList =  serviceRequestDao.getFailedSubjectsForStudentMBAWX(sapid, charges);

			for (ServiceRequestStudentPortal serviceRequest : failedSubjectsList) {
				if ("5".equals(serviceRequest.getSem())) {
					charges = "10000";
					serviceRequest.setAmount("10000");
				}
			}
			
			List<ServiceRequestStudentPortal> repeatSubjectsApplied =  serviceRequestDao.getRepeatAppliedSubjectsMBAWX(sapid, CURRENT_MBAWX_ACAD_MONTH, CURRENT_MBAWX_ACAD_YEAR, ServiceRequestStudentPortal.SUBJECT_REPEAT_MBAWX);
			
			if(isRegistrationLiveForStudent) {
								
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
			sr.setError("Error checking service request live status.");
			sr.setErrorMessage(e.getMessage());
		}
		return sr;
	}

	
	
	@Override
	public ServiceRequestStudentPortal saveSubjectRegistrationSRPayment(ServiceRequestStudentPortal sr) {

		String sapid = sr.getSapId();
		
		String charges = MBAWX_TERM_REPEAT_SR_CHARGES;
		if(sapid != null) {

			StudentStudentPortalBean student = serviceRequestDao.getSingleStudentsData(sapid);

			if(!StringUtils.isBlank(student.getEnrollmentMonth()) && !StringUtils.isBlank(student.getEnrollmentYear())) {
				String monthYear = student.getEnrollmentMonth() + student.getEnrollmentYear();
				if("Jul2019".equals(monthYear) || "Oct2019".equals(monthYear)) {
					charges = "6000";
				}
			}
		}

		// charges for capstone subject repeat is 4000.
		List<ServiceRequestStudentPortal> subjects = sr.getRepeatSubjects();
		for (ServiceRequestStudentPortal subjectInfo : subjects) {
			if(subjectInfo.getSem().equals("5")) {
				charges = "10000";
			}
		}
		
		
		String trackIdForMultipleMarksheets = sapid+System.currentTimeMillis(); //Since if we set this value in populateServicebean in the loop,the trackId does not remain unique since the loop runs and some time is lost//
		String totalAmount ;
		
		int baseCharge = Integer.parseInt(charges);
		totalAmount  = Integer.toString(baseCharge * subjects.size());
		sr.setAmount(totalAmount);
		String desc ="";
		for (ServiceRequestStudentPortal subjectInfo : subjects) {
			
			ServiceRequestStudentPortal srToInsert = new ServiceRequestStudentPortal();
			srToInsert.setServiceRequestType(ServiceRequestStudentPortal.SUBJECT_REPEAT_MBAWX);
			srToInsert.setSapId(sapid);
			srToInsert.setSem(subjectInfo.getSem());
			srToInsert.setTrackId(trackIdForMultipleMarksheets);

			srToInsert.setDescription(ServiceRequestStudentPortal.SUBJECT_REPEAT_MBAWX + " for student " + sapid + " for Sem : " + subjectInfo.getSem() + " for Subject : " + subjectInfo.getSubject());
			srToInsert.setTranStatus(ServiceRequestStudentPortal.TRAN_STATUS_INITIATED);
			srToInsert.setRequestStatus(ServiceRequestStudentPortal.REQUEST_STATUS_PAYMENT_PENDING);			

			srToInsert.setYear(CURRENT_MBAWX_ACAD_YEAR);
			srToInsert.setMonth(CURRENT_MBAWX_ACAD_MONTH);
			
			srToInsert.setAmount(totalAmount);
			srToInsert.setInformationForPostPayment(subjectInfo.getSubject());
			srToInsert.setPaymentOption(sr.getPaymentOption());
			srToInsert.setDevice(sr.getDevice());

			serviceRequestDao.insertServiceRequest(srToInsert);
			serviceRequestDao.insertServiceRequestHistory(srToInsert);
			sr.setId(srToInsert.getId());
		
				desc +="\n>> for Sem : " + subjectInfo.getSem() + ", Subject : " + subjectInfo.getSubject();

		}
		sr.setDescription(ServiceRequestStudentPortal.SUBJECT_REPEAT_MBAWX + " for student " + sapid + " "+desc);
		sr.setServiceRequestType(ServiceRequestStudentPortal.SUBJECT_REPEAT_MBAWX);
		String totalFeesForMarksheetPayment = sr.getTotalAmountToBePayed();

//		sr.setAmount(totalFeesForMarksheetPayment);
		sr.setTrackId(trackIdForMultipleMarksheets); 
		sr.setProductType("MBAWX"); 
		String paymentUrl = "proceedToPaymentGatewaySr?sapId="+sapid+"&paymentOptionName="+sr.getPaymentOption()+"&serviceRequestId="+sr.getId()+ "&productType=" + sr.getProductType();
		sr.setPaymentUrl(paymentUrl);
		return sr;
	}

}
