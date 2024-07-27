package com.nmims.controllers;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.servlet.ModelAndView;

import com.nmims.beans.StudentStudentPortalBean;
import com.nmims.services.IdCardService;

@Controller
public class IdCardController extends BaseController {
	
	@Autowired
	IdCardService idcardService;
	
	@GetMapping(value = "/public/getIdCardDetails/{uniqueHashKey}")
	public ModelAndView getIdCardDetailsWeb(@PathVariable String uniqueHashKey) {
		ModelAndView modelAndView = new ModelAndView("jsp/idCard/idCardDetails");
		StudentStudentPortalBean studentBean=idcardService.getSingleStudentByUniqueHash(uniqueHashKey);
		modelAndView.addObject("studentBean", studentBean);
		return modelAndView;
	}
	
	@GetMapping("/digitalidcard")
	public ModelAndView previewIdcard(HttpServletRequest request, HttpServletResponse respnse, Model m) {
		if(!checkSession(request, respnse)){
			return new ModelAndView("jsp/studentPortalRediret");
		}
		ModelAndView modelAndView=new ModelAndView("jsp/idCard/previewIdCard");
		StudentStudentPortalBean student=(StudentStudentPortalBean) request.getSession().getAttribute("getSingleStudentsData_studentportal");
		String fileName=idcardService.getIdCardFileNameBySapid(student.getSapid());
		modelAndView.addObject("fileName", fileName);
		return modelAndView;
	}
}
