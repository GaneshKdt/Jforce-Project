package com.nmims.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.nmims.beans.ResponseBean;
import com.nmims.beans.UpgradAssessmentExamBean;
import com.nmims.services.UpgradAssessmentService;

@RestController
@CrossOrigin(origins = "*", allowedHeaders = "*")
public class UpgradAssessmentsRESTController {
	
	@Autowired
	UpgradAssessmentService assessmentService;
	
	@RequestMapping(value = "/enterAssessmentScores", consumes = "application/json", produces = "application/json", method = {RequestMethod.POST})
	public  ResponseEntity<ResponseBean> createAssessment(@RequestBody UpgradAssessmentExamBean bean) {
		ResponseBean responseBean = new ResponseBean();
		if("UPSERT".equals(bean.getAction())) {
			responseBean = assessmentService.saveUpdateAssessmentScores(bean.getTestDetails());
			return new ResponseEntity<ResponseBean>(responseBean, HttpStatus.OK);
		}else if("DELETE".equals(bean.getAction())) {
			responseBean = assessmentService.deleteAssessmentScores(bean.getTestDetails());
			return new ResponseEntity<ResponseBean>(responseBean, HttpStatus.OK);
		}else {
			responseBean.setStatus("fail");
			responseBean.setMessage("Action filed is wrong, Please check and Try Again !!!");
			responseBean.setCode(422);
			return new ResponseEntity<ResponseBean>(responseBean, HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}
	
	@RequestMapping(value = "/m/enterAssessmentScores", consumes = "application/json", produces = "application/json", method = {RequestMethod.POST})
	public  ResponseEntity<ResponseBean> mcreateAssessment(@RequestBody UpgradAssessmentExamBean bean) {
		ResponseBean responseBean = new ResponseBean();
		if("UPSERT".equals(bean.getAction())) {
			responseBean = assessmentService.saveUpdateAssessmentScores(bean.getTestDetails());
			return new ResponseEntity<ResponseBean>(responseBean, HttpStatus.OK);
		}else if("DELETE".equals(bean.getAction())) {
			responseBean = assessmentService.deleteAssessmentScores(bean.getTestDetails());
			return new ResponseEntity<ResponseBean>(responseBean, HttpStatus.OK);
		}else {
			responseBean.setStatus("fail");
			responseBean.setMessage("Action filed is wrong, Please check and Try Again !!!");
			responseBean.setCode(422);
			return new ResponseEntity<ResponseBean>(responseBean, HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}
	
	@PostMapping(value = "/m/viewTestDetailsForStudentsMBAX")
	public ResponseEntity<ResponseBean> getStudentIATestDetails(
			@RequestParam("sapId") String sapid,
			@RequestParam("testId") Long testId){
		
		HttpHeaders headers = new HttpHeaders();
		headers.add("Content-Type", "application/json");
		ResponseBean responseBean = assessmentService.getStudentIATestDetailsService(sapid, testId);
		return  new ResponseEntity<ResponseBean>(responseBean,headers, HttpStatus.OK);
	}
	
}
