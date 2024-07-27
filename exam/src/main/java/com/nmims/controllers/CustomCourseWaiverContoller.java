package com.nmims.controllers;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import com.nmims.beans.FileBean;
import com.nmims.dto.CustomCourseWaiverDTO;
import com.nmims.helpers.CourseWaiverExcelHelper;
import com.nmims.helpers.ExcelHelper;
import com.nmims.interfaces.CustomCourseWaiverService;
import com.nmims.services.CustomCourseWaiverServiceImpl;

@Controller
@RequestMapping("/admin")
public class CustomCourseWaiverContoller {
	
	@Autowired
	CustomCourseWaiverService customCourseWaiverService;
	
	private static final Logger customCourseWaiverLogs = (Logger) LoggerFactory.getLogger("customCourseWaiver");


	@GetMapping("customCourseWaiverForm")
	public ModelAndView customWaivedSubjectForm(@ModelAttribute CustomCourseWaiverDTO bean,Model m) {
	
		FileBean fileBean = new FileBean();
		m.addAttribute("fileBean",fileBean);
		return new ModelAndView("customCourseWaiver");
	}
	
	@PostMapping("getApplicableSubjectForCustomCourse")
	public ModelAndView getApplicableSubjectForCustomCourse(@ModelAttribute CustomCourseWaiverDTO bean ,FileBean fileBean, BindingResult result,HttpServletRequest request, Model m) {
		ModelAndView model =  new ModelAndView("customCourseWaiver");
		try {
			Map<String,List<?>> applicableSubject = customCourseWaiverService.getApplicableSubject(bean.getSapid());
			List<?> currentSem =  applicableSubject.get("currentRegistrationSem");
			//getting current sem because user should not able to waived off subject from current sem or waived in to less than max sem
			model.addObject("currentRegistrationSem",currentSem.get(0));
			model.addObject("bean", bean);
			model.addObject("applicableSubject", applicableSubject.get("applicableSubject"));
			model.addObject("totalSems", applicableSubject.get("totalSems"));
		} catch (Exception e) {
			
			customCourseWaiverLogs.info("Error Found for "+bean.getSapid()+" "+e.getMessage());
		}

		return model;
	}
	
	@PostMapping("uploadSubject")
	public ModelAndView uploadSubject(FileBean fileBean, BindingResult result,HttpServletRequest request, Model m) {
		ModelAndView model =  new ModelAndView("customCourseWaiver");
		try {
			String userId = (String)request.getSession().getAttribute("userId");
			CourseWaiverExcelHelper excelHelper = new CourseWaiverExcelHelper();
			List<CustomCourseWaiverDTO> courseWaiver = excelHelper.readrCourseWaiverSubject(fileBean, userId);;
			Map<String,Integer> insertCount=customCourseWaiverService.upsertInCourseWaiver(courseWaiver,userId);
			
			request.setAttribute("success", "true");
			request.setAttribute("successMessage",insertCount.get("waivedInCount")+" "+"record inserted for Waived IN"+", "+insertCount.get("waivedOffCount")+" "+"record inserted for Waived OFF");
		} catch (Exception e) {
			
			request.setAttribute("error", "true");
			request.setAttribute("errorMessage","Error Uploading Excel");
			customCourseWaiverLogs.info("Error Found for while Uploading Excel"+fileBean.getFileName()+" "+e.getMessage());
		}

		return model;
	}
	
	
	
	@PostMapping("saveWaivedInSubject")
	public ResponseEntity<String> saveWaivedInSubject(HttpServletRequest request,@RequestBody CustomCourseWaiverDTO customCourseWaiverDTO) {
		HttpHeaders headers = new HttpHeaders();
		headers.add("Content-Type", "application/json");
		String count = "";
		try {
			String loggerUser = (String) request.getSession().getAttribute("userId");
			int insertCount = customCourseWaiverService.saveWaivedInSubject(customCourseWaiverDTO, loggerUser);
			customCourseWaiverService.upsertInStudentCurrentSubject(customCourseWaiverDTO.getSapid());
			customCourseWaiverService.upsertInStudentCourseMapping(customCourseWaiverDTO,loggerUser);
			count = String.valueOf(insertCount);
		} catch (Exception e) {
			
			if(e.getMessage() == "Sem") {
				return new ResponseEntity<String>("Invalid Sem", headers, HttpStatus.INTERNAL_SERVER_ERROR);
			}
			
			customCourseWaiverLogs.info(
					"Error Found for while saveWaivedInSubject" + customCourseWaiverDTO.getSapid() + " " + e.getMessage());
			return new ResponseEntity<String>("Error While Saving", headers, HttpStatus.INTERNAL_SERVER_ERROR);
		}
		return new ResponseEntity<String>(count, headers, HttpStatus.OK);
	}
	
	@PostMapping("saveWaivedOffSubject")
	public ResponseEntity<String> saveWaivedOffSubject(HttpServletRequest request,@RequestBody CustomCourseWaiverDTO customCourseWaiverDTO) {
		HttpHeaders headers = new HttpHeaders();
		headers.add("Content-Type", "application/json");
		String count = "";
		try {
			String loggerUser = (String) request.getSession().getAttribute("userId");
			int insertCount = customCourseWaiverService.saveWaivedOffSubject(customCourseWaiverDTO, loggerUser);
			count = String.valueOf(insertCount);
		} catch (Exception e) {
			
			customCourseWaiverLogs.info(
					"Error Found for while saving" + customCourseWaiverDTO.getSapid() + " " + e.getMessage());
			return new ResponseEntity<String>("Error While Saving", headers, HttpStatus.INTERNAL_SERVER_ERROR);
		}
		return new ResponseEntity<>(count, headers, HttpStatus.OK);
	}
}
