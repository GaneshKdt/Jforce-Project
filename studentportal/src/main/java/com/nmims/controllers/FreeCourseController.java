package com.nmims.controllers;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import com.nmims.beans.ContentStudentPortalBean;
import com.nmims.beans.FreeCourseResponseBean;
import com.nmims.beans.FreeCourseSubjectResponseBean;
import com.nmims.beans.LeadStudentPortalBean;
import com.nmims.beans.LeadModuleStatusBean;
import com.nmims.beans.ProgramsStudentPortalBean;
import com.nmims.beans.StudentStudentPortalBean;
import com.nmims.beans.TestStudentPortalBean;
import com.nmims.beans.VideoContentStudentPortalBean;
import com.nmims.beans.leadsProgramMapping;
import com.nmims.services.FreeCourseService;


@Controller
public class FreeCourseController extends BaseController{
	
	@Autowired
	FreeCourseService freeCourseService;
	
	@Value( "${SERVER_PATH}" )
	private String SERVER_PATH;
	
	
	@RequestMapping(value = "/student/getFreeCoursesList", method = RequestMethod.GET)
	public ModelAndView getFreeCoursesList(HttpServletRequest request,HttpServletResponse response) {
//		if (!checkSession(request, response)) {
//			return new ModelAndView("jsp/login");
//		}
		ModelAndView mv = new ModelAndView("jsp/freeCourse");
		StudentStudentPortalBean studentBean = (StudentStudentPortalBean) request.getSession().getAttribute("student_studentportal");
		leadsProgramMapping leadsProgramMapping = new leadsProgramMapping();
		leadsProgramMapping.setLeads_id(studentBean.getLeadId());
		List<ProgramsStudentPortalBean> programsList = freeCourseService.getFreeCourseProgram();
		List<String> leadsProgramMappingList = freeCourseService.getEnrolledCourseList(leadsProgramMapping);
		FreeCourseResponseBean responseBean = freeCourseService.getEnrolledAndNotEnrolledList(programsList, leadsProgramMappingList);
		responseBean.setLeads_id(studentBean.getLeadId());
		freeCourseService.setCompletionStatus(responseBean);

		List<ProgramsStudentPortalBean> onGoingPrograms = new ArrayList<ProgramsStudentPortalBean>();
		List<ProgramsStudentPortalBean> completedPrograms = new ArrayList<ProgramsStudentPortalBean>();
		
		for (ProgramsStudentPortalBean program : responseBean.getEnrolledList()) {
			if(program.getCertificate() != null) {
				completedPrograms.add(program);
			} else {
				onGoingPrograms.add(program);
			}
		}
		
		mv.addObject("onGoingPrograms", onGoingPrograms);
		mv.addObject("hasOnGoingPrograms", onGoingPrograms != null && onGoingPrograms.size() > 0);

		mv.addObject("programsBeanNotEnrolledList", responseBean.getNotEnrolledList());
		mv.addObject("hasNotEnrolledPrograms", responseBean.getNotEnrolledList() != null && responseBean.getNotEnrolledList().size() > 0);

		mv.addObject("completedPrograms", completedPrograms);
		mv.addObject("hasCompletedPrograms", completedPrograms != null && completedPrograms.size() > 0);
		return mv;
	}
	
//	to be deleted, api shifted to rest controller
//	@RequestMapping(value="/m/getFreeCoursiesList",method={RequestMethod.POST},consumes ="application/json",produces = "application/json")
//	public @ResponseBody FreeCourseResponseBean mgetFreeCoursiesList(@RequestBody leadsProgramMapping leadsProgramMapping){
//		List<ProgramsBean> programsBeanList = freeCourseService.getFreeCourseProgram();
//		List<String> leadsProgramMappingList = freeCourseService.getEnrolledCourseList(leadsProgramMapping);
//		FreeCourseResponseBean response = freeCourseService.getEnrolledAndNotEnrolledList(programsBeanList, leadsProgramMappingList);
//		response.setLeads_id(leadsProgramMapping.getLeads_id());
//		freeCourseService.setCompletionStatus(response);
//
//		List<ProgramsBean> onGoingPrograms = new ArrayList<ProgramsBean>();
//		List<ProgramsBean> completedPrograms = new ArrayList<ProgramsBean>();
//		
//		for (ProgramsBean program : response.getEnrolledList()) {
//			if(program.getCertificate() != null) {
//				completedPrograms.add(program);
//			} else {
//				onGoingPrograms.add(program);
//			}
//		}
//		
//		response.setOnGoingPrograms(onGoingPrograms);
//		response.setCompletedPrograms(completedPrograms);
//		
//		return response;
//	}
	
	@RequestMapping(value="/student/registerFreeCourse",method={RequestMethod.POST})
	public ModelAndView registerFreeCourse(leadsProgramMapping leadsProgramMapping,HttpServletRequest request,HttpServletResponse response) {
		if (!checkSession(request, response)) {
			return new ModelAndView("jsp/jsp/login");
		}
		StudentStudentPortalBean studentBean = (StudentStudentPortalBean) request.getSession().getAttribute("student_studentportal");
		leadsProgramMapping.setLeads_id(studentBean.getLeadId());
		String result = freeCourseService.enrolledForCourse(leadsProgramMapping);
		if("true".equalsIgnoreCase(result)) {
			request.setAttribute("successMessage", "Successfully register for course: " + leadsProgramMapping.getProgramName());
			request.setAttribute("success", "true");
		}else {
			request.setAttribute("errorMessage", result);
			request.setAttribute("error", "true");
		}
		return getFreeCoursesList(request,response);
	}
	
	/**
	 * @Request body
	 * consumerProgramStructureId: INT
	 * leads_id: INT
	 * */
	
//	to be deleted, api shifted to rest controller
//	@RequestMapping(value="/m/registerFreeCourse",method={RequestMethod.POST},consumes ="application/json",produces = "application/json")
//	public @ResponseBody leadsProgramMapping mregisterFreeCourse(@RequestBody leadsProgramMapping leadsProgramMapping) {
//		String result = freeCourseService.enrolledForCourse(leadsProgramMapping);
//		leadsProgramMapping leadsProgramMapping2 = new leadsProgramMapping();
//		if("true".equalsIgnoreCase(result)) {
//			leadsProgramMapping2.setStatus("success");
//			leadsProgramMapping2.setMessage("Successfully user register ");
//			return leadsProgramMapping2;
//		}
//		leadsProgramMapping2.setStatus("error");
//		leadsProgramMapping2.setMessage(result);
//		return leadsProgramMapping2;
//	}
	
	@RequestMapping(value="/student/getProgramSubjectList",method={RequestMethod.GET})
	public ModelAndView getProgramSubjectList(leadsProgramMapping leadsProgramMapping,HttpServletRequest request,HttpServletResponse response) {
		if (!checkSession(request, response)) {
			return new ModelAndView("jsp/login");
		}
		String consumerProgramStructureId = request.getParameter("id");
		if(consumerProgramStructureId == null) {
			return getFreeCoursesList(request,response);
		}
		StudentStudentPortalBean studentBean = (StudentStudentPortalBean) request.getSession().getAttribute("student_studentportal");
		leadsProgramMapping.setLeads_id(studentBean.getLeadId());
		leadsProgramMapping.setConsumerProgramStructureId(request.getParameter("id"));
		ModelAndView mv = new ModelAndView("jsp/subjectCourseList");
		List<LeadModuleStatusBean> subjectList = freeCourseService.getProgramSubjectList(leadsProgramMapping);
		//ProgramsBean programsBean = freeCourseService.getCourseDetails(leadsProgramMapping.getConsumerProgramStructureId());
		mv.addObject("subjectList", subjectList);
		mv.addObject("cpsId", consumerProgramStructureId);
		return mv;
	}
	
	
//	to be deleted, api shifted to restcontroller
//	@RequestMapping(value="/m/getProgramSubjectList",method={RequestMethod.POST},consumes ="application/json",produces = "application/json")
//	public @ResponseBody FreeCourseSubjectResponseBean mgetProgramSubjectList(@RequestBody leadsProgramMapping leadsProgramMapping) {
//		FreeCourseSubjectResponseBean freeCourseSubjectResponseBean = new FreeCourseSubjectResponseBean();
//		if(leadsProgramMapping.getConsumerProgramStructureId() == null) {
//			freeCourseSubjectResponseBean.setStatus("error");
//			freeCourseSubjectResponseBean.setMessage("Invalid consumerProgramStructure id");
//			return freeCourseSubjectResponseBean;
//		}
//		if(leadsProgramMapping.getLeads_id() == null) {
//			freeCourseSubjectResponseBean.setStatus("error");
//			freeCourseSubjectResponseBean.setMessage("Invalid Leads id");
//			return freeCourseSubjectResponseBean;
//		}
//		List<LeadModuleStatusBean> subjectList = freeCourseService.getProgramSubjectList(leadsProgramMapping);
//		freeCourseSubjectResponseBean.setStatus("success");
//		freeCourseSubjectResponseBean.setSubjectList(subjectList);
//		return freeCourseSubjectResponseBean;
//	}
	
	@RequestMapping(value = "/student/viewModuleDetails", method = {RequestMethod.GET, RequestMethod.POST})
	public ModelAndView viewModuleDetails(HttpServletRequest request, HttpServletResponse response,Model m) {
		
		if(!checkSession(request, response)){
			return new ModelAndView("jsp/login");
		}
		
		StudentStudentPortalBean student = (StudentStudentPortalBean) request.getSession().getAttribute("student_studentportal");
		ModelAndView modelnView = new ModelAndView("jsp/moduleDetails");
		String pssId = request.getParameter("pssId");
		String leadId = student.getLeadId();
		
		//Get All Content Details
		
		//Get subject name
		String subject = freeCourseService.getSubjectName(pssId).trim();
		String consumerProgramStructureId = freeCourseService.getConsumerProgramStructureId(pssId);
		if (!StringUtils.isBlank(subject)) {
			//Course Material 
			ArrayList<ContentStudentPortalBean> resourceContent = freeCourseService.getResourceContentList(subject);
			request.getSession().setAttribute("resourceContent", resourceContent);
			
			//Video Content
			ArrayList<VideoContentStudentPortalBean> videoContent = freeCourseService.getVideoContentList(subject);
			request.getSession().setAttribute("videoContent", videoContent);
			
			//IA & Results
			ArrayList<TestStudentPortalBean> quizList = freeCourseService.getQuizList(pssId, null);
			request.getSession().setAttribute("quizList", quizList);
		}
		
		request.getSession().setAttribute("SERVER_PATH", SERVER_PATH);
		modelnView.addObject("pssId", pssId); 
		modelnView.addObject("leadId", leadId);
		modelnView.addObject("subject", subject);
		modelnView.addObject("cpsId", consumerProgramStructureId);
		return modelnView;
	}
	
	
//	to be deletd, api shifted to rest controller
//	@RequestMapping(value = "/m/viewModuleDetails", method = {RequestMethod.POST},consumes ="application/json",produces = "application/json")
//	public @ResponseBody FreeCourseSubjectResponseBean mviewModuleDetails(@RequestBody LeadBean leadBean) {
//		
//		FreeCourseSubjectResponseBean freeCourseSubjectResponseBean = new FreeCourseSubjectResponseBean();
//		String pssId = leadBean.getProgram_sem_subject_id();
//		String leadId = leadBean.getLeadId();
//	
//		if (StringUtils.isBlank(pssId)) {
//			freeCourseSubjectResponseBean.setStatus("error");
//			freeCourseSubjectResponseBean.setMessage("Invalid Program Subject id");
//			return freeCourseSubjectResponseBean;
//		}
//		String subject = freeCourseService.getSubjectName(pssId).trim();
//		if (!StringUtils.isBlank(subject)) {
//			freeCourseSubjectResponseBean.setVideoContent(freeCourseService.getVideoContentList(subject));
//			freeCourseSubjectResponseBean.setResourceContent(freeCourseService.getResourceContentList(subject));
//			freeCourseSubjectResponseBean.setQuizList(freeCourseService.getQuizList(pssId, leadId));
//			freeCourseSubjectResponseBean.setStatus("success");
//			return freeCourseSubjectResponseBean;
//		}else {
//			freeCourseSubjectResponseBean.setStatus("error");
//			freeCourseSubjectResponseBean.setMessage("Error in getting subject");
//			return freeCourseSubjectResponseBean;
//		}
//	}
	}
