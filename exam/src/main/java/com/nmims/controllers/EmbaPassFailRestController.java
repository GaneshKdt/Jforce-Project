package com.nmims.controllers;

import java.util.ArrayList;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.nmims.beans.EmbaPassFailBean;
import com.nmims.services.EmbaPassFailService;
import com.nmims.services.MarksheetMBAX;

@RestController
@RequestMapping("m")
public class EmbaPassFailRestController {
	
	@Autowired
	EmbaPassFailService epfService;
	
	@Autowired
	MarksheetMBAX mxs;
	
//	api to get cleared sems of a student
	@PostMapping(path = "/getClearedSemForStudent", produces="application/json")
	public ResponseEntity<ArrayList<EmbaPassFailBean>> getClearedSemForStudent(@RequestBody EmbaPassFailBean bean
			) throws Exception {
		String sapid = bean.getSapid();
		
		HttpHeaders headers = new HttpHeaders();
		headers.add("Content-Type", "application/json");
		ArrayList<EmbaPassFailBean> response =  epfService.getClearedSemForStudent(sapid);
		return new ResponseEntity<>(response,headers, HttpStatus.OK);

	}
	
	@PostMapping(path = "/getClearedSemForStudentMBAX", produces="application/json")
	public ResponseEntity<ArrayList<EmbaPassFailBean>> getClearedSemForStudentMBAX(@RequestBody EmbaPassFailBean bean
			) throws Exception {
		String sapid = bean.getSapid();
		
		HttpHeaders headers = new HttpHeaders();
		headers.add("Content-Type", "application/json");
		ArrayList<EmbaPassFailBean> response =  mxs.getClearedSemForStudent(sapid);
		return new ResponseEntity<>(response,headers, HttpStatus.OK);

	}

	@PostMapping(path = "/getClearedSemForAStudent", produces="application/json")
	public ResponseEntity<ArrayList<EmbaPassFailBean>> getClearedSemForAStudent(@RequestBody EmbaPassFailBean bean
			) throws Exception {
		String sapid = bean.getSapid();
		
		HttpHeaders headers = new HttpHeaders();
		headers.add("Content-Type", "application/json");
		ArrayList<EmbaPassFailBean> response =  epfService.fetchClearedSemForAStudent(sapid);
		return new ResponseEntity<>(response,headers, HttpStatus.OK);
	}
	
}
