package com.nmims.controllers;

import java.util.ArrayList;
import java.util.List;
import javax.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;
import com.nmims.beans.SyllabusBean;
import com.nmims.daos.SyllabusDAO;
import com.nmims.helpers.ExcelHelper;
import com.nmims.services.SyllabusService;
import com.nmims.strategies.impl.ReadSyllabusForPSSStrategy;

@Controller
@CrossOrigin(origins = "*", allowedHeaders = "*")
public class SyllabusController {
	
	@Autowired
	SyllabusDAO syllabusDao;

	@Autowired
	SyllabusService syllabusService;
	
	@RequestMapping(value="/uploadSyllabusForm", method=RequestMethod.GET)
	public ModelAndView uploadSyllabusForm() {
		
		ModelAndView modelAndView = new ModelAndView("uploadSyllabus");
		
		SyllabusBean bean = new SyllabusBean();
		ArrayList<SyllabusBean> subjectCodes = new ArrayList<SyllabusBean>();
		
		try {
			subjectCodes = syllabusDao.getSubjectCode();
		} catch (Exception e) {
			  
		}

		modelAndView.addObject("subjectCodes", subjectCodes);
		modelAndView.addObject("bean",bean);
		
		return modelAndView;
		
	}
	
	@RequestMapping(value="/uploadSyllabus", method=RequestMethod.POST)
	public ModelAndView uploadSyllabus(HttpServletRequest request, @ModelAttribute SyllabusBean bean) {
		
		ModelAndView modelAndView = new ModelAndView("uploadSyllabus");
		ExcelHelper excelHelper = new ExcelHelper();
		String userId = (String)request.getSession().getAttribute("userId_acads");
		int successCount = 0;
		int errorCount = 0;

		ArrayList<SyllabusBean> subjectCodes = new ArrayList<SyllabusBean>();
		ArrayList<SyllabusBean> subjectSem = new ArrayList<>();
		try {
			subjectSem = syllabusDao.geSubjectCodeMappingForSubjectCode(bean);
		} catch (Exception e) {
			  
		}
		ArrayList<List<SyllabusBean>> content = excelHelper.readSyllabusExcel(bean, userId);
		ArrayList<SyllabusBean> syllabusList = (ArrayList<SyllabusBean>) content.get(0);
		
		for(SyllabusBean sem : subjectSem) {
			for(SyllabusBean syllabus : syllabusList) {
				
				syllabus.setSubjectCodeMappingId(sem.getSubjectCodeMappingId());
				try {
					
					syllabusDao.insertSyllabus(syllabus);
					successCount++;
					
				} catch (Exception e) {
					
					  
					errorCount++;
					
				}
				
			}
		}
		try {
			subjectCodes = syllabusDao.getSubjectCode();
		} catch (Exception e) {
			  
		}
		
		if(successCount > 0) {
			
			request.setAttribute("success", "true");
			request.setAttribute("successMessage",successCount+" rows of syllabus were saved successfully.");
			
		}else if (successCount > 0 && errorCount > 0){
			
			request.setAttribute("success", "true");
			request.setAttribute("successMessage",successCount+" rows of syllabus were saved successfully and "+errorCount+" rows had issues in saving the syllabus.");
			
		}else {
			
			request.setAttribute("error", "true");
			request.setAttribute("errorMessage",errorCount+" rows of syllabus had issues saving.");
			
		}
		bean = new SyllabusBean();
		modelAndView.addObject("bean",bean);
		modelAndView.addObject("subjectCodes", subjectCodes);
		
		return modelAndView;
		
	}
	
	@RequestMapping(value = "/getSemesterForSubject", method=RequestMethod.POST, consumes ="application/json", produces="application/json")
	public ResponseEntity<ArrayList<SyllabusBean>> getSemesterForSubject(@RequestBody SyllabusBean bean){
		
		HttpHeaders headers = new HttpHeaders();
		headers.add("Content-Type", "application/json");
		ArrayList<SyllabusBean> semesters = new ArrayList<SyllabusBean>();
		
		try {
			semesters = syllabusDao.getSemesterForSubject(bean);
			return new ResponseEntity<>(semesters,headers, HttpStatus.OK);
		} catch (Exception e) {
			  
			return new ResponseEntity<>(semesters,headers, HttpStatus.INTERNAL_SERVER_ERROR);
		}
		
	}

	@RequestMapping(value = "/syllabus",  method=RequestMethod.GET)
	public ModelAndView getAllSubjectForSyllabus() {
		
		ModelAndView modelAndView = new ModelAndView("syllabus");
		ArrayList<SyllabusBean> syllabus = new ArrayList<SyllabusBean>();
		try {
			syllabus = syllabusService.getSubject();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			  
		}
		modelAndView.addObject("syllabus",syllabus);
		return modelAndView;
		
	}
	
	@RequestMapping(value = "/syllabusDetails",  method=RequestMethod.GET)
	public ModelAndView getAllSyllabusForSubjectCode(@RequestParam Long subjectCodeMappingId) {
		
		ModelAndView modelAndView = new ModelAndView("syllabusDetails");
		ArrayList<SyllabusBean> syllabus = new ArrayList<SyllabusBean>();
		SyllabusBean bean = new SyllabusBean();
		bean.setSubjectCodeMappingId(subjectCodeMappingId);
		try {
			syllabus = syllabusService.readSyllabusForPSS(bean);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			  
		}
		modelAndView.addObject("subject",syllabus.get(0).getSubjectname());
		modelAndView.addObject("syllabus",syllabus);
		return modelAndView;
		
	}
	
	@RequestMapping(value = "/deleteSyllabus", method=RequestMethod.POST, consumes ="application/json", produces="application/json")
	public ResponseEntity<SyllabusBean> deleteSyllabus(@RequestBody SyllabusBean bean){
		
		HttpHeaders headers = new HttpHeaders();
		headers.add("Content-Type", "application/json");
		
		try {
			int result = syllabusService.deleteSyllabus(bean);
			if( result > 0 ) {
				bean.setErrorRecord(false);
				bean.setMessage("Syllabus successfully deleted.");
			}else {
				bean.setErrorRecord(false);
				bean.setMessage("Something went wrong.");
			}
			return new ResponseEntity<>(bean, headers, HttpStatus.OK);
		} catch (Exception e) {
			  
			bean.setErrorRecord(true);
			bean.setMessage(e.getMessage());
			return new ResponseEntity<>(bean,headers, HttpStatus.INTERNAL_SERVER_ERROR);
		}
		
	}

	@RequestMapping(value = "/editSyllabus",  method=RequestMethod.GET)
	public ModelAndView editSyllabus(@RequestParam Long id) {
		
		ModelAndView modelAndView = new ModelAndView("editSyllabus");
		SyllabusBean syllabus = new SyllabusBean();
		SyllabusBean bean = new SyllabusBean();
		bean.setId(id);
		try {
			syllabus = syllabusService.readSyllabusForSyllabusId(bean);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			  
		}
		modelAndView.addObject("subject",syllabus.getSubjectname());
		modelAndView.addObject("syllabus",syllabus);
		return modelAndView;
		
	}
	
	@RequestMapping(value = "/updateSyllabus", method=RequestMethod.POST, consumes ="application/json", produces="application/json")
	public ResponseEntity<SyllabusBean> updateSyllabus(@RequestBody SyllabusBean bean){
		
		HttpHeaders headers = new HttpHeaders();
		headers.add("Content-Type", "application/json");
		
		try {
			int result = syllabusService.updateSyllabus(bean);
			if( result > 0 ) {
				bean.setErrorRecord(false);
				bean.setMessage("Syllabus successfully updated.");
			}else {
				bean.setErrorRecord(false);
				bean.setMessage("Something went wrong.");
			}
			return new ResponseEntity<>(bean, headers, HttpStatus.OK);
		} catch (Exception e) {
			  
			bean.setErrorRecord(true);
			bean.setMessage(e.getMessage());
			return new ResponseEntity<>(bean,headers, HttpStatus.INTERNAL_SERVER_ERROR);
		}
		
	}

	@RequestMapping(value = "deleteSyllabusDetails", method=RequestMethod.POST, consumes ="application/json", produces="application/json")
	public ResponseEntity<SyllabusBean> deleteSyllabusDetails(@RequestBody SyllabusBean bean){
		
		HttpHeaders headers = new HttpHeaders();
		headers.add("Content-Type", "application/json");
		
		try {
			int result = syllabusService.deleteSyllabusDetails(bean);
			if( result > 0 ) {
				bean.setErrorRecord(false);
				bean.setMessage("Syllabus details was successfully deleted.");
			}else {
				bean.setErrorRecord(false);
				bean.setMessage("Something went wrong.");
			}
			return new ResponseEntity<>(bean, headers, HttpStatus.OK);
		} catch (Exception e) {
			  
			bean.setErrorRecord(true);
			bean.setMessage(e.getMessage());
			return new ResponseEntity<>(bean,headers, HttpStatus.INTERNAL_SERVER_ERROR);
		}
		
	}

}
