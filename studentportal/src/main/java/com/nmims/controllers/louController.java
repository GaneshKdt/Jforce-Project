package com.nmims.controllers;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

import com.nmims.beans.StudentStudentPortalBean;
import com.nmims.beans.louReportBean;
import com.nmims.services.louService;
import com.nmims.views.LouReportView;

@Controller
@RequestMapping("/admin")
@CrossOrigin(origins="*", allowedHeaders="*")
public class louController {
	@Autowired 
	louService service;
	
	@Value("#{'${ACAD_YEAR_LIST}'.split(',')}")
	private List<String> ACAD_YEAR_LIST;
	
	@Value("#{'${ACAD_MONTH_LIST}'.split(',')}") 
	private List<String> ACAD_MONTH_LIST;
	
	@Autowired
	LouReportView louReportView;
	
	private static final Logger logger = LoggerFactory.getLogger(louController.class);

	@GetMapping("/louReportForm")
	public ModelAndView louReportForm(Model m) {
		ArrayList<String> typeList = new ArrayList<String>();
		try {
			logger.info("LOU form for report generation is loading");
			typeList = service.getProgramTypeList();
		}catch(Exception e) {
			logger.error("error while fetching programType from database:"+e.getMessage());
		}
		m.addAttribute("louForm", new louReportBean());
		m.addAttribute("program_Type_List", typeList);
		m.addAttribute("month_list", ACAD_MONTH_LIST);
		m.addAttribute("year_list", ACAD_YEAR_LIST);
		return new ModelAndView("jsp/adminCommon/louReport");
	}
	
	@PostMapping(value="/getProgramNameByProgramType", consumes="application/json", produces="application/json", headers="content-type=application/json")
	public ResponseEntity<ArrayList<String>> getProgramNameByProgramType(HttpServletRequest request, @RequestBody HashMap<String,String> body){
		ArrayList<String> list = new ArrayList<String>();
		HttpHeaders headers = new HttpHeaders();
	    headers.add("Content-Type", "application/json");
		try {
			logger.info("fetching program name using program type");
			list = service.getProgramNameByProgramType(body.get("programType"));
			return new ResponseEntity<ArrayList<String>>(list,headers,HttpStatus.OK);
		}catch(Exception e) {
			logger.error("error while fetching program name using program type:"+e.getMessage());
			return new ResponseEntity<>(list,headers,HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}
	
	@PostMapping(value="/getSemTermByProgram", consumes="application/json", produces="application/json", headers="content-type=application/json")
	public ResponseEntity<ArrayList<String>> getSemTermByProgram(@RequestBody HashMap<String,String> map){
		ArrayList<String> list = new ArrayList<String>();
		HttpHeaders headers = new HttpHeaders();
		headers.add("Content-Type", "application/json");
		try {
			logger.info("fetching sem term using program");
			int semcount=service.getsemByProgramName(map.get("programName"));
			for(int i=0;i<semcount;i++) {
				list.add(String.valueOf(i+1));
			}
			return new ResponseEntity<>(list,headers,HttpStatus.OK);
		}catch(Exception e){
			logger.error("error while fetching sem using programname:"+e.getMessage());
			return new ResponseEntity<>(list,headers,HttpStatus.INTERNAL_SERVER_ERROR);		
		}
	}
	
	@PostMapping("/genratelouReport")
	public ResponseEntity<Integer> generatelouReport(@RequestBody louReportBean bean, HttpServletRequest request) {
		ArrayList<StudentStudentPortalBean> list = new ArrayList<StudentStudentPortalBean>();
		try {
			logger.info("generating lou report");
			list = service.generatereport(bean);
			
			request.getSession().setAttribute("louConfirmedlist", list);
			request.getSession().setAttribute("lclist", service.getlcnamebycode());
			request.getSession().setAttribute("programlist",service.getprogramnamebyprogram());
		}catch(Exception e){
			e.printStackTrace();
			logger.error("error while generating lou report:"+e.getMessage());
		}
		return new ResponseEntity<>(list.size(),HttpStatus.OK);
	}
	
	@RequestMapping(value = "/downloadlouReport",method = {RequestMethod.POST,RequestMethod.GET})
	public ModelAndView downloadLouReport(HttpServletRequest request) {
		logger.info("downloadlouReport called");
		ArrayList<StudentStudentPortalBean> bean=(ArrayList<StudentStudentPortalBean>)request.getSession().getAttribute("louConfirmedlist");
		try {
			return new ModelAndView(louReportView,"louConfirmedlist",bean);
		}catch(Exception e) {
			logger.error("error while downloading report:"+e.getMessage());
			return null;
		}	
	}
}

