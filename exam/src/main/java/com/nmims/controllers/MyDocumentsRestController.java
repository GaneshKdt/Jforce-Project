package com.nmims.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.nmims.beans.DocumentResponseBean;
import com.nmims.beans.StudentExamBean;
import com.nmims.services.MyDocumentsService;

@RestController
@RequestMapping("m")
public class MyDocumentsRestController {

	@Autowired
	MyDocumentsService myDocumentsService;

	@PostMapping(value = "/myDocuments")
	public DocumentResponseBean mmyDocuments(@RequestBody StudentExamBean studentBean) {

		DocumentResponseBean response = new DocumentResponseBean();
		try {
			response = myDocumentsService.getStudentsDocuments(studentBean.getSapid());
			response.setError("false");
			response.setStatus("success");
			return response;
		} catch (Exception e) {
			// TODO Auto-generated catch block
		//	e.printStackTrace();
			response.setError("true");
			response.setStatus("error");
			response.setErrorMessage(e.getMessage());
			return response;
		}
	}
}
