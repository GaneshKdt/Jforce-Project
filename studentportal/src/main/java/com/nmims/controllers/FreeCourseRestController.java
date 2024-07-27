package com.nmims.controllers;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.nmims.beans.FreeCourseResponseBean;
import com.nmims.beans.FreeCourseSubjectResponseBean;
import com.nmims.beans.LeadStudentPortalBean;
import com.nmims.beans.LeadModuleStatusBean;
import com.nmims.beans.ProgramsStudentPortalBean;
import com.nmims.beans.leadsProgramMapping;
import com.nmims.services.FreeCourseService;

@RestController
@RequestMapping("m")
public class FreeCourseRestController {

	@Autowired
	FreeCourseService freeCourseService;
	
	@PostMapping(path="/getFreeCoursiesList", consumes ="application/json",produces = "application/json")
	public @ResponseBody FreeCourseResponseBean mgetFreeCoursiesList(@RequestBody leadsProgramMapping leadsProgramMapping){
		List<ProgramsStudentPortalBean> programsBeanList = freeCourseService.getFreeCourseProgram();
		List<String> leadsProgramMappingList = freeCourseService.getEnrolledCourseList(leadsProgramMapping);
		FreeCourseResponseBean response = freeCourseService.getEnrolledAndNotEnrolledList(programsBeanList, leadsProgramMappingList);
		response.setLeads_id(leadsProgramMapping.getLeads_id());
		freeCourseService.setCompletionStatus(response);

		List<ProgramsStudentPortalBean> onGoingPrograms = new ArrayList<ProgramsStudentPortalBean>();
		List<ProgramsStudentPortalBean> completedPrograms = new ArrayList<ProgramsStudentPortalBean>();
		
		for (ProgramsStudentPortalBean program : response.getEnrolledList()) {
			if(program.getCertificate() != null) {
				completedPrograms.add(program);
			} else {
				onGoingPrograms.add(program);
			}
		}
		
		response.setOnGoingPrograms(onGoingPrograms);
		response.setCompletedPrograms(completedPrograms);
		
		return response;
	}
	
	
	@PostMapping(path="/registerFreeCourse",consumes ="application/json",produces = "application/json")
	public @ResponseBody leadsProgramMapping mregisterFreeCourse(@RequestBody leadsProgramMapping leadsProgramMapping) {
		String result = freeCourseService.enrolledForCourse(leadsProgramMapping);
		leadsProgramMapping leadsProgramMapping2 = new leadsProgramMapping();
		if("true".equalsIgnoreCase(result)) {
			leadsProgramMapping2.setStatus("success");
			leadsProgramMapping2.setMessage("Successfully user register ");
			return leadsProgramMapping2;
		}
		leadsProgramMapping2.setStatus("error");
		leadsProgramMapping2.setMessage(result);
		return leadsProgramMapping2;
	}
	
	@PostMapping(path="/getProgramSubjectList",consumes ="application/json",produces = "application/json")
	public @ResponseBody FreeCourseSubjectResponseBean mgetProgramSubjectList(@RequestBody leadsProgramMapping leadsProgramMapping) {
		FreeCourseSubjectResponseBean freeCourseSubjectResponseBean = new FreeCourseSubjectResponseBean();
		if(leadsProgramMapping.getConsumerProgramStructureId() == null) {
			freeCourseSubjectResponseBean.setStatus("error");
			freeCourseSubjectResponseBean.setMessage("Invalid consumerProgramStructure id");
			return freeCourseSubjectResponseBean;
		}
		if(leadsProgramMapping.getLeads_id() == null) {
			freeCourseSubjectResponseBean.setStatus("error");
			freeCourseSubjectResponseBean.setMessage("Invalid Leads id");
			return freeCourseSubjectResponseBean;
		}
		List<LeadModuleStatusBean> subjectList = freeCourseService.getProgramSubjectList(leadsProgramMapping);
		freeCourseSubjectResponseBean.setStatus("success");
		freeCourseSubjectResponseBean.setSubjectList(subjectList);
		return freeCourseSubjectResponseBean;
	}
	
	@PostMapping(path = "/viewModuleDetails",consumes ="application/json",produces = "application/json")
	public @ResponseBody FreeCourseSubjectResponseBean mviewModuleDetails(@RequestBody LeadStudentPortalBean leadBean) {
		
		FreeCourseSubjectResponseBean freeCourseSubjectResponseBean = new FreeCourseSubjectResponseBean();
		String pssId = leadBean.getProgram_sem_subject_id();
		String leadId = leadBean.getLeadId();
		
		if (StringUtils.isBlank(pssId)) {
			freeCourseSubjectResponseBean.setStatus("error");
			freeCourseSubjectResponseBean.setMessage("Invalid Program Subject id");
			return freeCourseSubjectResponseBean;
		}
		String subject = freeCourseService.getSubjectName(pssId).trim();
		if (!StringUtils.isBlank(subject)) {
			freeCourseSubjectResponseBean.setVideoContent(freeCourseService.getVideoContentList(subject));
			freeCourseSubjectResponseBean.setResourceContent(freeCourseService.getResourceContentList(subject));
			freeCourseSubjectResponseBean.setQuizList(freeCourseService.getQuizList(pssId, leadId));
			freeCourseSubjectResponseBean.setStatus("success");
			return freeCourseSubjectResponseBean;
		}else {
			freeCourseSubjectResponseBean.setStatus("error");
			freeCourseSubjectResponseBean.setMessage("Error in getting subject");
			return freeCourseSubjectResponseBean;
		}
	}
}
