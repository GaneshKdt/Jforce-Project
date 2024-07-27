package com.nmims.controllers;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.nmims.beans.LTIConsumerRequestBean;
import com.nmims.beans.StudentAcadsBean;
import com.nmims.daos.LtiDao;

@RestController
@CrossOrigin(origins="*", allowedHeaders="*")
@RequestMapping("m")
public class LTIConsumerRestController {
	
	@Autowired(required = false)
	ApplicationContext act; 
	
	public static final String[] harvard_masterkeys =  {"111","151","160"}; 
	
	@RequestMapping(value="/viewELearnResources", method= {RequestMethod.GET, RequestMethod.POST}, consumes = "application/json", produces = "application/json")
	public ResponseEntity<List<LTIConsumerRequestBean>> mviewELearnResources(@RequestBody StudentAcadsBean input ) {
//		if(!checkSession(request, response)){
//			return new ModelAndView("login");
//		}
		HttpHeaders headers = new HttpHeaders();
		headers.add("Content-Type","application/json");
		
		List<LTIConsumerRequestBean> resources_list = new ArrayList<LTIConsumerRequestBean>();
		LtiDao dao = (LtiDao) act.getBean("ltiDAO");
		try {
			
			
			if(input.getConsumerProgramStructureId() == null || "".equals(input.getConsumerProgramStructureId()) ) {
				input.setConsumerProgramStructureId(dao.getMasterKeyOfStudents(input.getSapid()));
			}
			if("110".equals(input.getConsumerProgramStructureId())) { // PD - DM
				//passing the parameter as Y to avoid endDate check in sql query
				resources_list = dao.getLtiResources(input.getSapid(), "Stukent","Y");
			}else if(Arrays.asList(harvard_masterkeys).contains(input.getConsumerProgramStructureId())) { // MBA - WX
				//passing the parameter as N to allow endDate check in sql query
				resources_list = dao.getLtiResources(input.getSapid(), "Harvard","N");
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block    
			

			  
		}
		return new ResponseEntity<List<LTIConsumerRequestBean>>(resources_list, headers, HttpStatus.OK);	
}

}
