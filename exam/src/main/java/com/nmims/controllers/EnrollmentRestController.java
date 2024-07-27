package com.nmims.controllers;

import java.util.Objects;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.nmims.dto.StudentProfileDetailsExamDto;
import com.nmims.interfaces.StudentDetailsServiceInterface;

@RestController
@RequestMapping("m")
public class EnrollmentRestController {
	private final StudentDetailsServiceInterface studentDetailsServiceInterface;
	
	private static final Logger logger = LoggerFactory.getLogger(EnrollmentRestController.class);
	
	@Autowired
	public EnrollmentRestController(@Qualifier("studentProfileDetailsService") StudentDetailsServiceInterface studentDetailsServiceInterface) {
		Objects.requireNonNull(studentDetailsServiceInterface);				//Fail-fast approach, field is guaranteed to be non-null.
		this.studentDetailsServiceInterface = studentDetailsServiceInterface;
	}
	
	@GetMapping("/admin/getStudentProfileDetails")
	public ResponseEntity<StudentProfileDetailsExamDto> getStudentProfileDetails(@RequestParam("sapid") Long studentSapid, @RequestParam("sem") Integer studentSem) {
		try {
			logger.info("Fetching Profile details of Student with sapid: {} and sem: {}", studentSapid, studentSem);
			StudentProfileDetailsExamDto studentDetails = studentDetailsServiceInterface.viewStudentProfileDetails(studentSapid, studentSem);
			return new ResponseEntity<>(studentDetails, HttpStatus.OK);
		}
		catch(Exception ex) {
//			ex.printStackTrace();
			logger.error("Failed to fetch Profile details of student {}, Exception thrown: {}", studentSapid, ex.toString());
			return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}
}
