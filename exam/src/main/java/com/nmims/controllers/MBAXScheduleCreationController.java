package com.nmims.controllers;

import java.util.ArrayList;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

import com.nmims.beans.ExamsAssessmentsBean;
import com.nmims.beans.FailedRegistrationBean;
import com.nmims.beans.FailedRegistrationResponseBean;
import com.nmims.beans.FailedregistrationExcelBean;
import com.nmims.beans.MettlRegisterCandidateBean;
import com.nmims.beans.MettlResponseBean;
import com.nmims.beans.ScheduleCreationBean;
import com.nmims.factory.MettlAssessmentsFactory;
import com.nmims.helpers.ExcelHelper;
import com.nmims.interfaces.MBAXScheduleCreationService;
import com.nmims.interfaces.MettlAssessments;
import com.nmims.services.EmbaPassFailService;

@Controller
public class MBAXScheduleCreationController {
	
	@Autowired
	MettlAssessmentsFactory mettlAssessmentsFactory;

	@Autowired
	EmbaPassFailService epfService;
	
	
	@Autowired
	MBAXScheduleCreationService mbaxService;
	
	private static final Logger logger = LoggerFactory.getLogger("mbaxScheduleCreation");

	
	private static final String programType = "MBA - X";
	
	
	public ModelAndView mbaxExamScheduleFrom(ModelAndView model,HttpServletRequest request) {
	
		try {
			MettlAssessments mettlAssesments = mettlAssessmentsFactory.getProductType(programType);
			ArrayList<MettlResponseBean> assesmentsResponseList = mettlAssesments.getAllAssessments();
			model.addObject("assesmentsResponseList", assesmentsResponseList);
			model.addObject("batchList", epfService.getBatchesList(programType));
		} catch (Exception e) {
			logger.info("{} : Error While Opening Schedule Creation Model  - {} ",e.getMessage(),e.getStackTrace());
			
		}
		return model;
		

	}
	
	
	@GetMapping("admin/mbaxExamScheduleCreationForm")
	public ModelAndView mbaxExamScheduleCreationForm(HttpServletRequest request) {
		ModelAndView model = new ModelAndView("mbax/mbaxScheduleCreation");
		return mbaxExamScheduleFrom(model,request);
	}
	
	
	@PostMapping("admin/mbaxScheduleCreation")
	public ModelAndView mbaxScheduleCreation(ScheduleCreationBean scheduleBean,HttpServletRequest request) {
		String userId = (String) request.getSession().getAttribute("userId");
		ModelAndView model = new ModelAndView("mbax/mbaxScheduleCreation");

		MettlAssessments mettlAssesments = mettlAssessmentsFactory.getProductType(programType);
		logger.info("{} : mbaxScheduleCreation  - {} ",scheduleBean);
		String  response = mbaxService.createScheduleForMBAX(scheduleBean,mettlAssesments,userId,request);
		
		if("success".equalsIgnoreCase(response)) {
			request.setAttribute("success", "true");
			request.setAttribute("successMessage", "Schedule Created SuccessFully");
		}
		else {
			request.setAttribute("error", "true");
			request.setAttribute("errorMessage", response);
		}
		
		return mbaxExamScheduleFrom(model,request);
	}
	
	@GetMapping("admin/uploadMBAXRegistrationFailedForm")
	public ModelAndView uploadMBAXRegistrationFailedForm(HttpServletRequest request)
	{
		ModelAndView mv = new ModelAndView("mbax/mbaxFailedStudentRegistration");
		String userId = (String)request.getSession().getAttribute("userId");
		mv.addObject("userId", userId);
		return mv;
	}
	
	@RequestMapping(value="/admin/downloadFailedRegistrationsMBAXList",method = RequestMethod.GET)
	public ModelAndView downloadFailedRegistrationsMBAXList(HttpServletRequest request)
	{

		ModelAndView mv = new ModelAndView("mbax/mbaxFailedStudentRegistration");
		ArrayList<MettlRegisterCandidateBean> userList = (ArrayList<MettlRegisterCandidateBean>)request.getSession().getAttribute("failedRegistrations");
		ExamsAssessmentsBean examBean = (ExamsAssessmentsBean)request.getSession().getAttribute("examDetails");
		String subjectName = (String)request.getSession().getAttribute("subjectName");
		String endTime = (String)request.getSession().getAttribute("endTime");

		mv.addObject("userList",userList);
		mv.addObject("examBean",examBean);
		mv.addObject("subjectName",subjectName);
		mv.addObject("endTime",endTime);
		return mv;
	}
	
	@PostMapping(value="m/uploadMBAXRegistrationFailedMettl")
	public ResponseEntity<FailedRegistrationResponseBean> uploadRegistrationFailedMettl(HttpServletRequest request,FailedRegistrationBean bean)
	{
		HttpHeaders headers = new HttpHeaders();
		headers.add("Content-Type", "application/json");
		FailedRegistrationResponseBean response = new FailedRegistrationResponseBean();
		try
		{
			 MettlAssessments assessments = mettlAssessmentsFactory.getProductType(programType);
			 response = mbaxService.registerExcelStudent(bean,assessments,request);
			
			return new ResponseEntity<>(response,headers,HttpStatus.OK);
		}
		catch(Exception e)
		{
			logger.error("MBAX Error is:"+e.getMessage());
			response.setError("Error Found:"+e.getMessage());
			return new ResponseEntity<>(response,headers,HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}


}
