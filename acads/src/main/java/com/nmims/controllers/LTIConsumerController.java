package com.nmims.controllers;

import java.util.ArrayList;

import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import com.nmims.beans.LTIConsumerRequestBean;
import com.nmims.beans.PersonAcads;
import com.nmims.beans.StudentAcadsBean;
import com.nmims.daos.LtiDao;



@Controller
@CrossOrigin(origins="*", allowedHeaders="*")
public class LTIConsumerController extends BaseController {
	@Autowired(required = false)
	ApplicationContext act; 
	@RequestMapping(value="/viewELearnResources", method= {RequestMethod.GET, RequestMethod.POST})
	public ModelAndView viewELearnResources(HttpServletRequest request, HttpServletResponse response, @RequestParam(value = "roles" , required=false) String roles,
			@RequestParam(value="p_name" ,required=false) String p_name) {
		if(!checkSession(request, response)){
			return new ModelAndView("login");
		}
		ModelAndView modelnView = new ModelAndView("viewELearnResources");
		PersonAcads person = (PersonAcads) request.getSession().getAttribute("user_acads");
		StudentAcadsBean student = (StudentAcadsBean)request.getSession().getAttribute("student_acads");
		List<LTIConsumerRequestBean> resources_list = new ArrayList<LTIConsumerRequestBean>();
		LtiDao dao = (LtiDao) act.getBean("ltiDAO");
		String isStudent = "Y";
		
		try {
			if(!person.getUserId().startsWith("77") && !person.getUserId().startsWith("79")){
				isStudent ="N";
			}
			if(roles == null && p_name == null) {
				
				resources_list = dao.getLtiResources(person.getUserId(), "Stukent",isStudent);
			}else {
				resources_list = dao.getLtiResources(person.getUserId(), roles,p_name ,isStudent);
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			request.setAttribute("error", "true");
			request.setAttribute("errorMessage", e);

			  
		}
		request.getSession().setAttribute("resources_list", resources_list);
		modelnView.addObject("isStudent", isStudent);
		return modelnView;  
	}
	   
//	to be deleted, api shifted to rest controller
//	@RequestMapping(value="/m/viewELearnResources", method= {RequestMethod.GET, RequestMethod.POST}, consumes = "application/json", produces = "application/json")
//	public ResponseEntity<List<LTIConsumerRequestBean>> mviewELearnResources(@RequestBody StudentBean input ) {
////		if(!checkSession(request, response)){
////			return new ModelAndView("login");
////		}
//		HttpHeaders headers = new HttpHeaders();
//		headers.add("Content-Type","application/json");
//		
//		List<LTIConsumerRequestBean> resources_list = new ArrayList<LTIConsumerRequestBean>();
//		LtiDao dao = (LtiDao) act.getBean("ltiDAO");
//		try {
//			if(input.getConsumerProgramStructureId() == null || "".equals(input.getConsumerProgramStructureId()) ) {
//				input.setConsumerProgramStructureId(dao.getMasterKeyOfStudents(input.getSapid()));
//			}
//			if("110".equals(input.getConsumerProgramStructureId())) { // PD - DM
//				resources_list = dao.getLtiResources(input.getSapid(), "Stukent");
//			}else if("111".equals(input.getConsumerProgramStructureId())) { // MBA - WX
//				resources_list = dao.getLtiResources(input.getSapid(), "Harvard");
//			}
//		} catch (Exception e) {
//			// TODO Auto-generated catch block    
//			
//
//			  
//		}
//		return new ResponseEntity<List<LTIConsumerRequestBean>>(resources_list, headers, HttpStatus.OK);	
//}
	
}
