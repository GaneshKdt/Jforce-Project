package com.nmims.services;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.nmims.beans.ServiceRequestStudentPortal;
import com.nmims.daos.ServiceRequestDao;
import com.nmims.interfaces.SubjectRepeatSR;

@Service
public class SubjectRepeatSrMSCAIOPS implements SubjectRepeatSR{
	
	@Value("${CURRENT_MBAWX_ACAD_MONTH}")
	private String CURRENT_MBAWX_ACAD_MONTH;
	
	@Value("${CURRENT_MBAWX_ACAD_YEAR}")
	private String CURRENT_MBAWX_ACAD_YEAR;
	
	@Value("${MSCAIOPS_TERM_REPEAT_SR_CHARGES_PER_CREDIT}")
	private String MSCAIOPS_TERM_REPEAT_SR_CHARGES_PER_CREDIT;
	
	@Autowired
	private ServiceRequestDao serviceRequestDao;
	
	@Override
	public ServiceRequestStudentPortal getSubjectRepeatStatusForStudent(String sapid) {

		ServiceRequestStudentPortal sr = new ServiceRequestStudentPortal();
		try {
			
			boolean isRegistrationLiveForStudent = serviceRequestDao.getReRegLiveMBAWX(sapid, CURRENT_MBAWX_ACAD_MONTH, CURRENT_MBAWX_ACAD_YEAR, ServiceRequestStudentPortal.SUBJECT_REPEAT_MSC_AI_ML);

			List<ServiceRequestStudentPortal> failedSubjectsList =  serviceRequestDao.getFailedSubjectsForStudentMBAWX(sapid, MSCAIOPS_TERM_REPEAT_SR_CHARGES_PER_CREDIT);

			for (ServiceRequestStudentPortal serviceRequest : failedSubjectsList) {
					double costForSubject = getsubjectcredit(serviceRequest.getSubject()) *  Integer.parseInt(MSCAIOPS_TERM_REPEAT_SR_CHARGES_PER_CREDIT) ;
					serviceRequest.setAmount(Double.toString(costForSubject));
			}
			
			List<ServiceRequestStudentPortal> repeatSubjectsApplied =  serviceRequestDao.getRepeatAppliedSubjectsMBAWX(sapid, CURRENT_MBAWX_ACAD_MONTH, CURRENT_MBAWX_ACAD_YEAR, ServiceRequestStudentPortal.SUBJECT_REPEAT_MSC_AI_ML);
			
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
		
		List<ServiceRequestStudentPortal> subjects = sr.getRepeatSubjects();
		double mscCharges = 0;
		for (ServiceRequestStudentPortal subjectInfo : subjects) {
			mscCharges = mscCharges +  ( getsubjectcredit(subjectInfo.getSubject()) * Integer.parseInt(MSCAIOPS_TERM_REPEAT_SR_CHARGES_PER_CREDIT) );
		}
		
		
		String trackIdForMultipleMarksheets = sapid+System.currentTimeMillis(); //Since if we set this value in populateServicebean in the loop,the trackId does not remain unique since the loop runs and some time is lost//
		String totalAmount  = Double.toString(mscCharges);
		sr.setAmount(totalAmount);
		String desc ="";
		for (ServiceRequestStudentPortal subjectInfo : subjects) {
			
			ServiceRequestStudentPortal srToInsert = new ServiceRequestStudentPortal();
			srToInsert.setServiceRequestType(ServiceRequestStudentPortal.SUBJECT_REPEAT_MSC_AI_ML );
			srToInsert.setSapId(sapid);
			srToInsert.setSem(subjectInfo.getSem());
			srToInsert.setTrackId(trackIdForMultipleMarksheets);

			srToInsert.setDescription(ServiceRequestStudentPortal.SUBJECT_REPEAT_MSC_AI_ML  + " for student " + sapid + " for Sem : " + subjectInfo.getSem() + " for Subject : " + subjectInfo.getSubject());
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
		sr.setDescription(ServiceRequestStudentPortal.SUBJECT_REPEAT_MSC_AI_ML  + " for student " + sapid + " "+desc);
		sr.setServiceRequestType(ServiceRequestStudentPortal.SUBJECT_REPEAT_MSC_AI_ML );

		sr.setTrackId(trackIdForMultipleMarksheets); 
		sr.setProductType("MBAWX"); 
		String paymentUrl = "proceedToPaymentGatewaySr?sapId="+sapid+"&paymentOptionName="+sr.getPaymentOption()+"&serviceRequestId="+sr.getId()+ "&productType=" + sr.getProductType();
		sr.setPaymentUrl(paymentUrl);
		return sr;
	}
	
	
	private double getsubjectcredit(String subject) {
		switch (subject) {
		case "Foundations of Probability and Statistics for Data Science":
			return 5;
		case "Data Structures and Algorithms":
			return 4;
		case "Advanced Mathematical Analysis for Data Science":
			return 3;
		case "Essential Engineering Skills in Big Data Analytics Using R and Python":
			return 3;
		case "Statistics and Probability in Decision Modeling -1":
			return 3;
		case "Statistics and Probability in Decision Modeling -2":
			return 3;
		case "Advanced Data Structures and Algorithms":
			return 2;
		case "The Art and Science of Storytelling and Visualization & Design Thinking-1":
			return 3;
		case "Methods and Algorithms in Machine Learning -1":
			return 4;
		case "Methods and Algorithms in Machine Learning -2":
			return 4;
		case "Methods and Algorithms in Machine Learning -3":
			return 3;
		case "AI and Decision Sciences":
			return 4;
		case "Computer vision fundamentals and deep learning applications":
			return 5;
		case "Text mining and Natural Language Processing Deep learning for NLP":
			return 5;
		case "Big Data: An overview of Big Data and Hadoop ecosystems":
			return 5;
		case "Quantum Computing":
			return 5;		
		case "Advanced Python Programming":
			return 3;
		case "Business Communication and Presentation Skills for Data Analytics":
			return 1;
		case "Economics for Analysts":
			return 2.5;
		case "Business Law and Ethics":
			return 2.5;
		case "Behaviour Science and Analytics":
			return 2.5;
		case "Digital and Social Media Analytics":
			return 2.5;
		case "Product Management":
			return 2.5;
		case "Project Management":
			return 2.5;
		case "Architecting Enterprise Applications and Design Thinking-2":
			return 3;
		case "Product Deployment Bootcamp":
			return 5;
		case "Quantitative Research Methods":
			return 2;
		case "Masters Dissertation":
			return 5;
		default :
			return 0;
		}
	}

}
