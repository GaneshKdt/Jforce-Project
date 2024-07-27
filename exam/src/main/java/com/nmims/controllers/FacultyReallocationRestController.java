package com.nmims.controllers;

import java.util.ArrayList;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.nmims.beans.FacultyReallocationBean;
import com.nmims.services.FacultyReallocationService;

@RestController
@RequestMapping("m")
public class FacultyReallocationRestController {

	@Autowired
	private FacultyReallocationService facultyReallocationService;
	private static final Logger logger = LoggerFactory.getLogger("projectReallocation");
	
	
	@PostMapping(path = "/getStudentsDetailByFacultyId", consumes = "application/json", produces = "application/json")
	public ResponseEntity<List<FacultyReallocationBean>> getStudentTestDetails(@RequestBody FacultyReallocationBean bean){
		
		HttpHeaders headers = new HttpHeaders();
		headers.add("Content-Type", "application/json");
		
		List<FacultyReallocationBean> response = new ArrayList<FacultyReallocationBean>();
		
		String facultyId = bean.getFacultyId();
		String year = bean.getYear();
		String month = bean.getMonth();
		
		try {
			response = facultyReallocationService.getStudentsByFacultyIdAndYearAndMonth(facultyId, year, month);
		} catch (Exception e) {
			//e.printStackTrace();
			logger.error("Error in getting project students by facultyId: "+e);
		}

		return new ResponseEntity<>(response, headers, HttpStatus.OK);

	}
}
