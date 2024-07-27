package com.nmims.coursera.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.nmims.coursera.beans.StudentCourseraMappingBean;
import com.nmims.coursera.dto.StudentCourseraDto;
import com.nmims.coursera.interfaces.CourseraServiceInterface;


@RestController
@RequestMapping("m")
public class CourseraRestController {
		
	@Autowired
	CourseraServiceInterface courseraService;
	
	@PostMapping("/courseraMobileCheck")
	public StudentCourseraDto createCourseraEntry(@RequestBody StudentCourseraMappingBean dtoObj) {
		StudentCourseraDto returnDto=new StudentCourseraDto();
		
		returnDto=courseraService.checkForStudentOptedForCoursera(dtoObj);
		
		return returnDto;
	}
}
