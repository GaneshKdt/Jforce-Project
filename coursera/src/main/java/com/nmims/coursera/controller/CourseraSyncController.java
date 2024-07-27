package com.nmims.coursera.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.nmims.coursera.dto.CourseraSyncDto;
import com.nmims.coursera.interfaces.CourseraServiceInterface;


@RestController
public class CourseraSyncController {
	
	private static final Logger coursera_sync_trigger = LoggerFactory.getLogger("coursera_sync_trigger");
	
	@Autowired
	CourseraServiceInterface courseraService;
	
	@PostMapping("/m/courseraPortalSync")
	public CourseraSyncDto createCourseraEntry(@RequestBody CourseraSyncDto dtoObj) {
		 
		CourseraSyncDto returnDto=new CourseraSyncDto();
		
		coursera_sync_trigger.info("dtoObj :"+dtoObj.getSapId());
		
		returnDto=courseraService.create(dtoObj);
		
		return returnDto;
	}

}
