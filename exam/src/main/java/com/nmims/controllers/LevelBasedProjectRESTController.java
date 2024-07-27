package com.nmims.controllers;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.RandomStringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.commons.CommonsMultipartFile;
import org.springframework.web.servlet.ModelAndView;

import com.nmims.beans.AssignmentFileBean;
import com.nmims.beans.ExamSelectSubjectBeanAPIResponse;
import com.nmims.beans.LevelBasedProjectBean;
import com.nmims.beans.LevelBasedSOPConfigBean;
import com.nmims.beans.LevelBasedSynopsisConfigBean;
import com.nmims.beans.ProjectConfiguration;
import com.nmims.beans.ProjectModuleExtensionBean;
import com.nmims.beans.ProjectModuleStatusBean;
import com.nmims.beans.ProjectStudentStatus;
import com.nmims.beans.StudentExamBean;
import com.nmims.beans.UploadProjectSOPBean;
import com.nmims.beans.UploadProjectSynopsisBean;
import com.nmims.beans.VivaSlotBean;
import com.nmims.beans.VivaSlotBookingConfigBean;
import com.nmims.daos.AssignmentsDAO;
import com.nmims.daos.DashboardDAO;
import com.nmims.daos.ExamBookingDAO;
import com.nmims.daos.LevelBasedProjectDAO;
import com.nmims.daos.PassFailDAO;
import com.nmims.daos.ProjectSubmissionDAO;
import com.nmims.daos.ProjectTitleDAO;
import com.nmims.dto.GuidedProjectDto;
import com.nmims.helpers.LevelBasedProjectHelper;
import com.nmims.services.LevelBasedProjectService;
import com.nmims.services.ProjectStudentEligibilityService;

@RestController
@RequestMapping("m")
public class LevelBasedProjectRESTController extends BaseController {
	
	@Autowired
	ApplicationContext act;
	
	@Autowired
	LevelBasedProjectDAO levelBasedProjectDAO ;
	
	@Autowired
	LevelBasedProjectHelper levelBasedProjectHelper;
	
	@Autowired
	LevelBasedProjectService levelBasedProjectService;
	
	@Autowired
	ProjectTitleDAO projectTitleDAO;
	
	@Autowired
	ProjectStudentEligibilityService eligibilityService;
	
	@Autowired
	ProjectSubmissionDAO projectSubmissionDAO;
	
	private final String subjectName = "Module 4 - Project";
	
	@PostMapping(path = "/getProjectsSummary", consumes = "application/json", produces = "application/json")
	public ResponseEntity<GuidedProjectDto> getProjectsSummary(@RequestBody StudentExamBean person) {
	
		//ModelAndView mv = new ModelAndView("project/student/guidedProjectSubmissionSummary");
		
		GuidedProjectDto response = new  GuidedProjectDto();
		
		ExamBookingDAO eDao = (ExamBookingDAO)act.getBean("examBookingDAO");
		StudentExamBean student  = eDao.getSingleStudentsData(person.getSapid());

		LevelBasedProjectBean recentMapping = new  LevelBasedProjectBean();
//		try {
//		 recentMapping = levelBasedProjectService.getRecentStudentGuideMapping(student.getSapid());
//		}catch (Exception e) {
//			// TODO: handle exception
//			response.setError(true);
//			response.setErrorMessage("Project Configuration not found for given subject");
//			return new ResponseEntity<GuidedProjectDto>(response, HttpStatus.OK);
//		}
//		
//		
//		String examMonth = recentMapping.getMonth();
//		String examYear = recentMapping.getYear();
		String examMonth = null;
		String examYear = null;
		if(recentMapping != null) {
			examMonth = recentMapping.getMonth();
			examYear = recentMapping.getYear();
		}else {
			String method = "getProjectsSummary()";
			// Set Student Applicable Exam Mont Year
			AssignmentFileBean examMonthYearBean = eligibilityService.getStudentApplicableExamMonthYear(student.getSapid(), subjectName , method);
			examMonth = examMonthYearBean.getMonth();
			examYear = examMonthYearBean.getYear();
		}
		
		if(student.getExamMode().equalsIgnoreCase("Offline")){
			response.setError(true);
			response.setErrorMessage( "You are not eligible for Project Submission!");
			return new ResponseEntity<GuidedProjectDto>(response, HttpStatus.OK);
		}
		
		PassFailDAO passFailDAO = (PassFailDAO)act.getBean("passFailDAO");
		ArrayList<String> passedSubjects = passFailDAO.getPassSubjectsNamesForAStudent(student.getSapid());
		if(passedSubjects.contains(this.subjectName)) {
			response.setError(true);
			response.setErrorMessage("Subject already passed!,You are not eligible for Project Submission!");
			return new ResponseEntity<GuidedProjectDto>(response, HttpStatus.OK);
		}
		
		String pssId = levelBasedProjectService.getProgramSemSubjectIdForStudent(student.getSapid(), this.subjectName);
		
		ProjectConfiguration projectConfiguration = null;
		try {
			projectConfiguration = projectTitleDAO.getSingleLevelBasedProjectConfiguration(examYear, examMonth, pssId);
		}catch (Exception e) {
			// TODO: handle exception
			response.setError(true);
			response.setErrorMessage("Project Configuration not found for given subject");
			return new ResponseEntity<GuidedProjectDto>(response, HttpStatus.OK);
		}
		
		
		
		ProjectStudentStatus status = new ProjectStudentStatus();
		status.setSapid(student.getSapid());
		status.setSubject(this.subjectName);
		status.setConsumerProgramStructureId(student.getConsumerProgramStructureId());
		status.setProgramSemSubjId(pssId);
		status.setExamMonth(examMonth);
		status.setExamYear(examYear);
		
		//eligibilityService.getPaymentInfoForStudent(student, status);
		
		/*if(status.getCanSubmit().equals("N")) {
			setError(request, status.getCantSubmitError());
			return mv;
		}*/
		try {
			//ProjectConfiguration config = levelBasedProjectService.getProjectConfigurationForSubjectId(examMonth, examYear, pssId);
			status.setHasSOP(projectConfiguration.getHasSOP());
			status.setHasSynopsis(projectConfiguration.getHasSynopsis());
			status.setHasSubmission(projectConfiguration.getHasSubmission());
			status.setHasTitle(projectConfiguration.getHasTitle());
			status.setHasViva(projectConfiguration.getHasViva());
			
			List<ProjectModuleExtensionBean> extensions = levelBasedProjectService.getProjectExtensionsListForStudent(examMonth, examYear, student.getSapid(), pssId);
			if("Y".equals(projectConfiguration.getHasSOP())) {
				ProjectModuleStatusBean sopStatus = eligibilityService.getSOPInfoForStudent(student, pssId, this.subjectName, examYear, examMonth, extensions);	
				status.setSopStatus(sopStatus);	
			} else {
				ProjectModuleStatusBean sopStatus = new ProjectModuleStatusBean();
				sopStatus.setError("Not Eligible!");
				status.setSopStatus(sopStatus);	
			}
			
			if("Y".equals(projectConfiguration.getHasSynopsis())) {
				ProjectModuleStatusBean synopsisStatus = eligibilityService.getSynopsisInfoForStudent(student, pssId, examYear, examMonth, extensions);	
				status.setSynopsisStatus(synopsisStatus);
			} else {
				ProjectModuleStatusBean synopsisStatus = new ProjectModuleStatusBean();
				synopsisStatus.setError("Not Eligible!");
				status.setSynopsisStatus(synopsisStatus);
			}
			
			if("Y".equalsIgnoreCase(projectConfiguration.getHasSubmission())) {
				ProjectModuleStatusBean projectSubmissionStatus = new ProjectModuleStatusBean();
//				ArrayList<String> applicableStudentIdSubjectList =  projectSubmissionDAO.getProjectApplicableStudentList(examYear,examMonth); // to check for students for whom applicable
				projectSubmissionStatus.setAllowSubmission(false);
//				if(applicableStudentIdSubjectList.contains(student.getSapid() + "" + this.subjectName)) {
					AssignmentFileBean assignmentFileBean = new AssignmentFileBean();
					assignmentFileBean.setSubject(this.subjectName);
					assignmentFileBean.setConsumerProgramStructureId(student.getConsumerProgramStructureId());
					AssignmentFileBean assignmentFileBean2 = new AssignmentFileBean();
//					assignmentFileBean2 = projectSubmissionDAO.findById(assignmentFileBean); // Commented to make two cycle live
					String method = "getProjectsSummary()";
					AssignmentFileBean examMonthYearBean = eligibilityService.getStudentApplicableExamMonthYear(student.getSapid(), assignmentFileBean.getSubject(), method);
					assignmentFileBean.setMonth(examMonthYearBean.getMonth());
					assignmentFileBean.setYear(examMonthYearBean.getYear());
					assignmentFileBean2 = projectSubmissionDAO.findProjectGuidelinesForApplicableCycle(assignmentFileBean);
					if(assignmentFileBean2 != null) {
						assignmentFileBean2.setEndDate(assignmentFileBean2.getEndDate().replaceFirst("T", " "));
						projectSubmissionStatus.setEndDate(assignmentFileBean2.getEndDate());
						projectSubmissionStatus.setAllowSubmission(true);	
					}	
//				}
				status.setSubmissionStatus(projectSubmissionStatus);
			}else {
				ProjectModuleStatusBean projectSubmissionStatus = new ProjectModuleStatusBean();
				projectSubmissionStatus.setAllowSubmission(false);
				status.setSubmissionStatus(projectSubmissionStatus);
			}
			
			// Viva and title submission are currently kept inactive.
			if("Y".equals(projectConfiguration.getHasViva())) {
				ProjectModuleStatusBean vivaStatus = eligibilityService.getVivaInfoForStudent(student, pssId, examYear, examMonth, extensions);	
				status.setVivaStatus(vivaStatus);
				/*
				 * ProjectModuleStatusBean vivaStatus = new ProjectModuleStatusBean();
				 * vivaStatus.
				 * setError("Invalid Configuration detected! Kindly contact Administrator!");
				 * status.setVivaStatus(vivaStatus);
				 */
			} else {
				ProjectModuleStatusBean vivaStatus = new ProjectModuleStatusBean();
				vivaStatus.setError("Not Eligible!");
				status.setVivaStatus(vivaStatus);
			}

			if("Y".equals(projectConfiguration.getHasTitle())) {
				ProjectModuleStatusBean titleStatus = new ProjectModuleStatusBean();
				titleStatus.setError("Invalid Configuration detected! Kindly contact Administrator!");
				status.setTitleStatus(titleStatus);
			} else {
				ProjectModuleStatusBean titleStatus = new ProjectModuleStatusBean();
				titleStatus.setError("Not Eligible!");
				status.setTitleStatus(titleStatus);
			}
		}catch (Exception e) {
			response.setStatusBean(status);
			response.setError(true);
			response.setErrorMessage("Error getting Project configuration");
			return new ResponseEntity<GuidedProjectDto>(response, HttpStatus.OK);
		}
		response.setStatusBean(status);
		response.setError(false);
		return new ResponseEntity<GuidedProjectDto>(response, HttpStatus.OK);
	}

}
