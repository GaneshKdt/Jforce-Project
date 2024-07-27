package com.nmims.services;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.stereotype.Component;

import com.nmims.beans.ProgramSubjectMappingExamBean;
import com.nmims.beans.StudentExamBean;
import com.nmims.daos.FreeCertificateCourseDAO;
import com.nmims.daos.LeadDAO;
import com.nmims.helpers.FreeCourseCertificatePDFCreator;

@Component
public class FreeCourseCertificateServiceLayer {

	@Autowired
	FreeCourseCertificatePDFCreator pdfCreator;
	
	@Autowired
	LeadDAO leadDao;
	
	@Autowired
	FreeCertificateCourseDAO certificateDAO;

	@Value("${SERVER_PATH}")
	private String SERVER_PATH;
	
	@Value("${LEAD_CURRENT_EXAM_YEAR}")
	private String LEAD_CURRENT_EXAM_YEAR;

	@Value("${LEAD_CURRENT_EXAM_MONTH}")
	private String LEAD_CURRENT_EXAM_MONTH;

	@Value("${LEAD_CURRENT_ACAD_YEAR}")
	private String LEAD_CURRENT_ACAD_YEAR;

	@Value("${LEAD_CURRENT_ACAD_MONTH}")
	private String LEAD_CURRENT_ACAD_MONTH;

	
	public Map<String, String> generateCertificateForLead(String leadId, String consumerProgramStructureId, String certificateType ) {

		Map<String, String> resp = new HashMap<String, String>();
		StudentExamBean student;
		String complectionDate=null;
		try {
			student = leadDao.getLeadById(leadId);
			student.setSapid(leadId);
		}catch (Exception e) {
			
			resp.put("error", "true");
			resp.put("errorMessage", "Error getting student details.");
			return resp;
		}
		// Check certificate type
		if(!("participation".equals(certificateType) || "completion".equals(certificateType))) {
			resp.put("error", "true");
			resp.put("errorMessage", "Invalid certificate type.");
			return resp;
		}
		if(!certificateDAO.checkIfStudentEnrolledForProgram(leadId, consumerProgramStructureId)) {
			resp.put("error", "true");
			resp.put("errorMessage", "Student not enrolled for program!");
			return resp;
		}
		
		if(!checkCertificateEligibilityForStudent(leadId, consumerProgramStructureId, certificateType)) {
			resp.put("error", "true");
			resp.put("errorMessage", "Student is not eligible for this certificate!");
			return resp;
		}
		
		//Calling the helper method to get complection date
		complectionDate=getComplectionDate(leadId, consumerProgramStructureId, certificateType);
		
		try {
			// Get the program name and generate the certificate
			String programName = certificateDAO.getProgramName(consumerProgramStructureId);
			String fileName = pdfCreator.createTrascript(student, programName, LEAD_CURRENT_EXAM_MONTH+LEAD_CURRENT_EXAM_YEAR, certificateType,complectionDate);

			resp.put("error", "false");
			resp.put("programName", programName);
			resp.put("download_url", SERVER_PATH + "Certificates/" + fileName);
			return resp;
		} catch (Exception e) {
			
			resp.put("error", "true");
			resp.put("errorMessage", "Error in generating Certificates.");
			return resp;
		}
	}


	private boolean checkCertificateEligibilityForStudent(String leadId, String consumerProgramStructureId, String certificateType) {
		/*
		 * If the student is eligible for either a participation or completion certificate for this program, add it to the list.
		 * 
		 * Criteria for certificate--
		 * Participation certificate -> Student has given quiz for all semesters
		 * Completion certificate -> Student has passed in the quiz for the final semester 
		 */
		ProgramSubjectMappingExamBean pss = certificateDAO.getLastSemSubjectForProgram(consumerProgramStructureId);
		
		String testId = null;
		try {
			testId = certificateDAO.getTestIdForProgram(pss.getId(), LEAD_CURRENT_EXAM_MONTH, LEAD_CURRENT_EXAM_YEAR, LEAD_CURRENT_ACAD_MONTH, LEAD_CURRENT_ACAD_YEAR);
		}catch (EmptyResultDataAccessException e) {
		}catch (Exception e) {
			
		}
		if(!StringUtils.isBlank(testId)) {
			
			// Check if the student has taken quiz/number of attempts taken
			int numberOfAttempts = certificateDAO.getNumberOfTestsTakenByStudents(testId, leadId);
			if(numberOfAttempts > 0) {
				// Check if the student has passed in Quiz
				if(certificateType.equals("participation")) {
					return true;
				}
				
				boolean testPassed = certificateDAO.getTestStatusForStudent(testId, leadId);
				if(testPassed) {
					return true;
				}
			}
		}
		return false;
	}
	
	/**
	 * This is returning completed date of test
	 * @param leadId
	 * @param consumerProgramStructureId
	 * @param certificateType
	 * @return String
	 * @author 
	 */
	private String getComplectionDate(String leadId, String consumerProgramStructureId, String certificateType) {
		/*
		 * If the student is eligible for either a participation or completion certificate for this program, add it to the list.
		 * 
		 * Criteria for certificate--
		 * Participation certificate -> Student has given quiz for all semesters
		 * Completion certificate -> Student has passed in the quiz for the final semester 
		 */
		ProgramSubjectMappingExamBean pss = certificateDAO.getLastSemSubjectForProgram(consumerProgramStructureId);
		
		String testId = null;
		String completionDate=null;
		try {
			testId = certificateDAO.getTestIdForProgram(pss.getId(), LEAD_CURRENT_EXAM_MONTH, LEAD_CURRENT_EXAM_YEAR, LEAD_CURRENT_ACAD_MONTH, LEAD_CURRENT_ACAD_YEAR);
		}catch (EmptyResultDataAccessException e) {
		}catch (Exception e) {
			
		}
		if(!StringUtils.isBlank(testId)) {
			//Calling DAO class method 
			completionDate=certificateDAO.getCompletionDate(testId,leadId);
		}
		return completionDate;
	}
	
}
