package com.nmims.controllers;

import java.util.HashMap;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.nmims.beans.StudentMarksBean;
import com.nmims.beans.UFMNoticeBean;
import com.nmims.beans.UFMResponseBean;
import com.nmims.services.UFMNoticeService;

@RestController
@RequestMapping("m")
public class UFMNoticeRestController {
	
	@Autowired
	UFMNoticeService ufmNoticeService;
	
	
	@RequestMapping(value = "/getStudentUFMList", method = { RequestMethod.POST }, produces = "application/json; charset=UTF-8")
	public ResponseEntity<UFMResponseBean> getStudentUFMList(@RequestBody UFMNoticeBean student) {
		return new ResponseEntity<UFMResponseBean>(ufmNoticeService.getUfmResponseBean(student.getSapid()), HttpStatus.OK);
	}

	@RequestMapping(value = "/submitShowCause", method = {RequestMethod.POST})
	public ResponseEntity<UFMResponseBean> msubmitShowCause(HttpServletRequest request, HttpServletResponse response,@RequestBody UFMNoticeBean bean) {
		UFMResponseBean responseBean = new UFMResponseBean();
		bean.setLastModifiedBy(bean.getSapid());
		
		String error = ufmNoticeService.addStudentResponse(bean);
		
//		ufmNoticeService.getUfmResponseBean(bean.getSapid());
		
		if(StringUtils.isBlank(error)) {
			responseBean.setStatus("success");
		} else {
			responseBean.setStatus("error");
			responseBean.setErrorMessage(error);
		}
		return new ResponseEntity<UFMResponseBean>(responseBean, HttpStatus.OK);
	}
	
	@PostMapping(value = "/getPendingRIARecords",  produces = "application/json; charset=UTF-8")
	public ResponseEntity<List<StudentMarksBean>> getPendingRIARecords(HttpServletRequest request, @RequestBody UFMNoticeBean bean) {
		try {
		List<StudentMarksBean> list = ufmNoticeService.getPendingRIARecords(bean.getYear(), bean.getMonth(), bean.getStatus());
		request.getSession().setAttribute("downloadableRIANVRecords", list);
		return new ResponseEntity<List<StudentMarksBean>>(list, HttpStatus.OK);
		}catch (Exception e) {
			// TODO: handle exception
			return new ResponseEntity<List<StudentMarksBean>>( HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}
	
	@PostMapping(value = "/upateRIANVRecords",  produces = "application/json; charset=UTF-8")
	public ResponseEntity<HashMap<String, Integer>> upateRIANVRecords(HttpServletRequest request, @RequestBody UFMNoticeBean student) {
		try {
			List<StudentMarksBean> list =	(List<StudentMarksBean>) request.getSession().getAttribute("downloadableRIANVRecords");
			String userId = (String)request.getSession().getAttribute("userId");
			HashMap<String, Integer> result= ufmNoticeService.updateRIANVStatus(list, student.getStatus(), userId);
		return new ResponseEntity<HashMap<String, Integer>>(result, HttpStatus.OK);
		}catch (Exception e) {
			// TODO: handle exception
			return new ResponseEntity<HashMap<String, Integer>>( HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}
}
