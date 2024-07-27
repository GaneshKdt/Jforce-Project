/**
 * 
 */
package com.nmims.controllers;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.nmims.dto.StudentSessionCoursesDTO;
import com.nmims.interfaces.StudentSessionCoursesClientServiceInterface;

/**
 * @author vil_m
 *
 */
@RestController
public class StudentSessionCoursesController {
	
	@Autowired
	StudentSessionCoursesClientServiceInterface studentSessionCoursesClientService;

	public static final Logger logger = LoggerFactory.getLogger(StudentSessionCoursesController.class);

	@PostMapping(path = "/m/createStudentSessionCourses")
	public StudentSessionCoursesDTO createStudentSessionCourses(@RequestBody StudentSessionCoursesDTO dtoObj) {
		logger.info("Entering StudentSessionCoursesController : createStudentSessionCourses");
		StudentSessionCoursesDTO returnDTO = null;
		String userId = null;
		if (null != dtoObj) {
			System.out.println("Create (SAP Id, Acad Year, Acad Month, Course Id) : (" + dtoObj.getSapId() + ","
					+ dtoObj.getAcadYear() + "," + dtoObj.getAcadMonth() + "," + dtoObj.getCourseIds() + ")");
		}

		//userId = (String) request.getSession().getAttribute("userId");
		userId = "Salesforce";
		returnDTO = studentSessionCoursesClientService.create(dtoObj, userId);
		return returnDTO;
	}

	@PutMapping(path = "/m/updateStudentSessionCourses")
	public StudentSessionCoursesDTO updateStudentSessionCourses(@RequestBody StudentSessionCoursesDTO dtoObj) {
		logger.info("Entering StudentSessionCoursesController : updateStudentSessionCourses");
		StudentSessionCoursesDTO returnDTO = null;
		String userId = null;
		if (null != dtoObj) {
			System.out.println("Update (SAP Id, Acad Year, Acad Month, Course Id) : (" + dtoObj.getSapId() + ","
					+ dtoObj.getAcadYear() + "," + dtoObj.getAcadMonth() + "," + dtoObj.getCourseIds() + ")");
		}
		
		//userId = (String) request.getSession().getAttribute("userId");
		userId = "Salesforce";
		returnDTO = studentSessionCoursesClientService.update(dtoObj, userId);
		return returnDTO;
	}

	@PostMapping(path = "/m/deleteStudentSessionCourses")
	public StudentSessionCoursesDTO deleteStudentSessionCourses(@RequestBody StudentSessionCoursesDTO dtoObj) {
		logger.info("Entering StudentSessionCoursesController : deleteStudentSessionCourses");
		StudentSessionCoursesDTO returnDTO = null;
		System.out.println("Delete (SAP Id, Course Id) : (" + dtoObj.getSapId() + "," + dtoObj.getCourseIds() + ")");
		returnDTO = studentSessionCoursesClientService.delete(dtoObj);
		return returnDTO;
	}

	@GetMapping(path = "/m/readStudentSessionCourses")
	public StudentSessionCoursesDTO readStudentSessionCourses(@RequestBody StudentSessionCoursesDTO dtoObj) {
		logger.info("Entering StudentSessionCoursesController : readStudentSessionCourses");
		StudentSessionCoursesDTO returnDTO = null;
		System.out.println("Read (SAP Id) : (" + dtoObj.getSapId() + ")");
		returnDTO = studentSessionCoursesClientService.read(dtoObj);
		return returnDTO;
	}

}
