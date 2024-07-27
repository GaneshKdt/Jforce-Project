package com.nmims.services;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.stereotype.Component;

import com.nmims.beans.ContentStudentPortalBean;
import com.nmims.beans.FreeCourseResponseBean;
import com.nmims.beans.LeadStudentPortalBean;
import com.nmims.beans.LeadModuleStatusBean;
import com.nmims.beans.ProgramSubjectMappingStudentPortalBean;
import com.nmims.beans.ProgramsStudentPortalBean;
import com.nmims.beans.TestStudentPortalBean;
import com.nmims.beans.VideoContentStudentPortalBean;
import com.nmims.beans.leadsProgramMapping;
import com.nmims.daos.FreeCourseDAO;

@Component
public class FreeCourseService {

	@Autowired
	FreeCourseDAO FreeCourseDAO;

	@Value("${LEAD_CURRENT_ACAD_YEAR}")
	private String LEAD_CURRENT_ACAD_YEAR;

	@Value("${LEAD_CURRENT_ACAD_MONTH}")
	private String LEAD_CURRENT_ACAD_MONTH;

	@Value("${LEAD_CURRENT_EXAM_YEAR}")
	private String LEAD_CURRENT_EXAM_YEAR;

	@Value("${LEAD_CURRENT_EXAM_MONTH}")
	private String LEAD_CURRENT_EXAM_MONTH;
	
	public List<ProgramsStudentPortalBean> getFreeCourseProgram(){
		return FreeCourseDAO.getFreeCourseList();
	}
	
	public List<String> getEnrolledCourseList(leadsProgramMapping leadsProgramMapping){
		return FreeCourseDAO.getEnrolledCourseList(leadsProgramMapping);
	}
	
	public FreeCourseResponseBean getEnrolledAndNotEnrolledList(List<ProgramsStudentPortalBean> programsList,List<String> leadsProgramMappingList){
		FreeCourseResponseBean response = new FreeCourseResponseBean();
		
		List<ProgramsStudentPortalBean> programsBeanEnrolledList = new ArrayList<ProgramsStudentPortalBean>();
		List<ProgramsStudentPortalBean> programsBeanNotEnrolledList = new ArrayList<ProgramsStudentPortalBean>();
		for (ProgramsStudentPortalBean programsBean : programsList) {
			if(leadsProgramMappingList.contains(programsBean.getConsumerProgramStructureId())) {
				programsBeanEnrolledList.add(programsBean);
			}else {
				programsBeanNotEnrolledList.add(programsBean);
			}
		}
		response.setEnrolledList(programsBeanEnrolledList);
		response.setNotEnrolledList(programsBeanNotEnrolledList);
		
		return response;
	}
	
	public String enrolledForCourse(leadsProgramMapping leadsProgramMapping) {
		LeadStudentPortalBean leadBean = FreeCourseDAO.getLeadBean(leadsProgramMapping);
		if(leadBean == null) {
			return "Invalid lead Id found";
		}
		String status = FreeCourseDAO.insertIntoLeadsMasterKeyMapping(leadsProgramMapping);
		if("true".equalsIgnoreCase(status)) {
			return "true";
		}
		return "Failed to register user";
	}
	
	public List<LeadModuleStatusBean> getProgramSubjectList(leadsProgramMapping leadsProgramMapping) {
//		List<String> semList = FreeCourseDAO.getListOfTestsForProgram(leadsProgramMapping);
//		String currentSem = semList.get(semList.size() - 1);	//get current sem
//		semList.remove(semList.size() - 1);
//		List<ProgramSubjectMappingBean> ProgramSubjectMappingBeanSubjectList = FreeCourseDAO.getSubjectList(leadsProgramMapping);
//		for (ProgramSubjectMappingBean programSubjectMappingBean : ProgramSubjectMappingBeanSubjectList) {
//			if(semList.contains(programSubjectMappingBean.getSem())) {
//				programSubjectMappingBean.setCompleted("Y");
//				programSubjectMappingBean.setOngoing("N");
//			}else {
//				programSubjectMappingBean.setCompleted("N");
//				programSubjectMappingBean.setOngoing("N");
//			}
//			if(currentSem.equalsIgnoreCase(programSubjectMappingBean.getSem())) {
//				programSubjectMappingBean.setCompleted("N");
//				programSubjectMappingBean.setOngoing("Y");
//			}
//		}
//		return ProgramSubjectMappingBeanSubjectList;
		
		return getListOfPassedSubjectsForStudent(leadsProgramMapping);
//		return null;
	}
	
	public ArrayList<ContentStudentPortalBean> getResourceContentList (String subject){
		return FreeCourseDAO.getResourceContentList(subject);
	}
	
	public ArrayList<VideoContentStudentPortalBean> getVideoContentList (String subject){
		return FreeCourseDAO.getVideoContentList(subject);
	}
	
	public ArrayList<TestStudentPortalBean> getQuizList (String pssId, String leadId){
		return FreeCourseDAO.getQuizList(pssId, leadId);
	}
	
	public String getSubjectName(String pssId) {
		return FreeCourseDAO.getSubjectName(pssId);
	}
	
	public String getConsumerProgramStructureId(String pssId) {
		return FreeCourseDAO.getConsumerProgramStructureId(pssId);
	}
	
	public List<LeadModuleStatusBean> getListOfPassedSubjectsForStudent(leadsProgramMapping leadsProgramMapping) {

		List<ProgramSubjectMappingStudentPortalBean> ProgramSubjectMappingBeanSubjectList = FreeCourseDAO.getSubjectList(leadsProgramMapping);
		
		List<LeadModuleStatusBean> listOfSubjects = new ArrayList<LeadModuleStatusBean>();
		/*
		 *  Sem-wise Quiz pass/fail info stored in this Map.
		 *  Used to query completion status
		 */
		Map<Integer, Boolean> semQuizCompletionStatus = new HashMap<Integer, Boolean>();
		
		for (ProgramSubjectMappingStudentPortalBean subjectMapping : ProgramSubjectMappingBeanSubjectList) {
			LeadModuleStatusBean subject = new LeadModuleStatusBean();
			subject.setConsumerProgramStructureId(leadsProgramMapping.getConsumerProgramStructureId());
			subject.setProgram_sem_subject_id(subjectMapping.getId());
			subject.setSem(Integer.parseInt(subjectMapping.getSem()));
			subject.setSubjectName(subjectMapping.getSubject());
			subject.setLeads_id(leadsProgramMapping.getLeads_id());
			subject.setSubjectDescription(subjectMapping.getDescription());

			getQuizAndAttemptDetails(subject);

			// Add test pass/fail status to a map for later checking access
			if(subject.getQuizAttemptsTaken() > 0) {
				semQuizCompletionStatus.put(subject.getSem(), subject.isQuizPassed());
			} else {
				semQuizCompletionStatus.put(subject.getSem(), false);
			}
			
			listOfSubjects.add(subject);
		}

		
		for (LeadModuleStatusBean subject : listOfSubjects) {

			/* 
			 * Check if student has access; 
			 * Student always has access in sem 1
			 * Quiz needs to be completed to gain access
			 */
			if(subject.getSem() == 1 || semQuizCompletionStatus.get(subject.getSem() - 1) == true) {
				subject.setUnlocked(true);

				if(semQuizCompletionStatus.get(subject.getSem()) == true) {
					// If student has passed the quiz in this sem, mark as complete
					subject.setCompleted(true);
				} else {
					// If student has passed the quiz in this sem, mark as not complete
					subject.setCompleted(false);
				}
			} else {
				/*
				 * Completion Status is always false when previous sem quiz is not passed; 
				 * Status is always locked as well
				 */
				subject.setUnlocked(false);
				subject.setCompleted(false);
			}
			
		} 
		return listOfSubjects;
	}

	public void setCompletionStatus(FreeCourseResponseBean response) {
		try {
			// Loop the list of enrolled programs
			List<ProgramsStudentPortalBean> programsEnrolled = response.getEnrolledList();
			List<LeadModuleStatusBean> certificateList = new ArrayList<LeadModuleStatusBean>();
			for (ProgramsStudentPortalBean program : programsEnrolled) {
				LeadModuleStatusBean certificateBean = getCompletionStatusForProgram(program, response);
				if(certificateBean != null) {
					certificateList.add(certificateBean);
					program.setCertificate(certificateBean);
				}
			}
			response.setEnrolledList(programsEnrolled);
			response.setCertificateList(certificateList);
		}catch (Exception e) {
			
		}
	}

	
	private LeadModuleStatusBean getCompletionStatusForProgram(ProgramsStudentPortalBean program, FreeCourseResponseBean response) {
		/*
		 * If the student is eligible for either a participation or completion certificate for this program, add it to the list.
		 * 
		 * Criteria for certificate--
		 * Participation certificate -> Student has given quiz for all semesters
		 * Completion certificate -> Student has passed in the quiz for the final semester 
		 */
		ProgramSubjectMappingStudentPortalBean pss = FreeCourseDAO.getLastSemSubjectForProgram(program.getConsumerProgramStructureId());

		LeadModuleStatusBean certificateBean = new LeadModuleStatusBean();
		certificateBean.setConsumerProgramStructureId(program.getConsumerProgramStructureId());
		certificateBean.setProgramName(program.getProgramname());
		certificateBean.setLeads_id(response.getLeads_id());
		certificateBean.setProgram_sem_subject_id(pss.getId());;
		getQuizAndAttemptDetails(certificateBean);
		
		if(certificateBean.getQuizAttemptsTaken() > 0) {
			if(certificateBean.isQuizPassed()) {
				certificateBean.setCertificateType("completion");
			} else {
				certificateBean.setCertificateType("participation");
			}
			return certificateBean;
		}
		return null;
	}

	private void getQuizAndAttemptDetails(LeadModuleStatusBean subject) {

		TestStudentPortalBean quiz = getTestStatusForStudent(subject.getProgram_sem_subject_id(), subject.getLeads_id());
		
		if(quiz != null) {
			subject.setQuizId( "" + quiz.getId());

			// Number of attempts taken by student and number of attempts left
			subject.setQuizAttemptsTaken(quiz.getAttempt());
			subject.setQuizAttemptsLeft(quiz.getMaxAttempt() - quiz.getAttempt());

			// Score, pass score and max score
			subject.setQuizScore(quiz.getScore());
			subject.setQuizPassScore(quiz.getPassScore());
			subject.setQuizMaxScore(quiz.getMaxScore());

			// Check if the student has passed in Quiz
			boolean testPassed = quiz.getScore() >= quiz.getPassScore();
			subject.setQuizPassed(testPassed);
			if(testPassed) {
				subject.setProgramComplete(true);
			} else {
				subject.setProgramComplete(false);
			}
		}
		System.out.println(subject);
	}
	
	private TestStudentPortalBean getTestStatusForStudent(int pssId, String leadId) {
		try {
			return FreeCourseDAO.getTestForProgramAndStudent(pssId, leadId, LEAD_CURRENT_EXAM_MONTH, LEAD_CURRENT_EXAM_YEAR, LEAD_CURRENT_ACAD_MONTH, LEAD_CURRENT_ACAD_YEAR);
		}catch (EmptyResultDataAccessException e) {
			System.out.println("===> No Test Id Found for --" + pssId);
		}catch (Exception e) {
			System.out.println(pssId + " " + leadId);
			
		}
		return null;
	}
}
