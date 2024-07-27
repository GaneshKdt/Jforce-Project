package com.nmims.controllers;

import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

import com.google.gson.Gson;
import com.nmims.beans.MBAWXConfigurationBean;
import com.nmims.beans.StudentExamBean;
import com.nmims.daos.MBAWXReRegistrationDAO;

@Controller
@CrossOrigin(origins = "*", allowedHeaders = "*")
public class MBAWXReRegistrationController extends BaseController {

	@Autowired
	MBAWXReRegistrationDAO mbawxReRegistrationDAO;

//	to be deleted, api shifted to rest controller
//	@RequestMapping(value = "/m/checkIfReRegApplicableForStudent", method = RequestMethod.POST , produces = "application/json; charset=UTF-8", consumes = "application/json")
//	public ResponseEntity<String> mbaCreateBookingRequest(HttpServletRequest request, Model model, @RequestBody StudentBean student) {
//		
//		Map<String, Object> response = new HashMap<String, Object>();
//		if(student.getSapid() == null) {
//			response.put("status", "fail");
//			response.put("message", "Empty or invalid student!");
//		} else {
//			boolean live = mbawxReRegistrationDAO.checkIfReRegistrationLiveForSapid(student.getSapid());
//			if(live) {
//
//				boolean applicableForStudent = mbawxReRegistrationDAO.checkIfReRegistrationApplicableForSapid(student.getSapid());
//				if(applicableForStudent) {
//					response.put("status", "success");
//					MBAWXConfigurationBean configuration = mbawxReRegistrationDAO.getReRegistrationBeanForSapid(student.getSapid());
//					response.put("configuration", configuration);
//					response.put("url", "https://ngasce.secure.force.com/nmlogin_new?type=reregistration");
//				} else {
//					response.put("status", "fail");
//					response.put("message", "Student cant apply for Re-Registration!");
//				}
//			} else {
//				response.put("status", "fail");
//				response.put("message", "Re-Resigtration not live!");
//			}
//		}
//		return new ResponseEntity<String>(new Gson().toJson(response), HttpStatus.OK);
//	}
	
	@RequestMapping(value="/MBAWXMakeReRegistrationLiveForm",method=RequestMethod.GET)
	public ModelAndView MBAWXMakeReRegistrationLiveForm(HttpServletRequest request,HttpServletResponse response) {
		if(!checkSession(request, response)){
			redirectToPortalApp(response);
			return null;
		}

		MBAWXConfigurationBean configuration = mbawxReRegistrationDAO.getReRegSettings();

		ModelAndView mv = new ModelAndView("MBAWXReRegistrationToggle");
		mv.addObject("configuration", configuration);
		return mv;
	} 
	
	@RequestMapping(value="/MBAWXMakeReRegistrationLive", method=RequestMethod.POST)
	public ModelAndView MBAWXMakeReRegistrationLive(HttpServletRequest request,HttpServletResponse response, MBAWXConfigurationBean configuration) {
		if(!checkSession(request, response)){
			redirectToPortalApp(response);
			return null;
		}
		configuration.setAcadsMonth("Jul");
		configuration.setAcadsYear("2019");
		configuration.setExamMonth("Oct");
		configuration.setExamYear("2019");
		configuration.setConsumerProgramStructureId("111");
		configuration.setType("Exam Re-Registration");

		ModelAndView mv = new ModelAndView("MBAWXReRegistrationToggle");
		
		try {
			mbawxReRegistrationDAO.upsertReRegistrationRecord(configuration);
			mv.addObject("success", "true");
			mv.addObject("successMessage", "Updated Timings successfully!");
		}catch (Exception e) {
			mv.addObject("error", "true");
			mv.addObject("errorMessage", e.getMessage());
		}
		configuration = mbawxReRegistrationDAO.getReRegSettings();
		mv.addObject("configuration", configuration);
		return mv;
	} 
	
}
