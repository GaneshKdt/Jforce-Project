package com.nmims.controllers;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import com.nmims.services.ContentService;
import com.nmims.services.FileMigrationService;

import org.springframework.stereotype.Controller;

@Controller
public class FileMigrationController 
{
	@Autowired
	FileMigrationService fileService;
	
	@RequestMapping(value = "/admin/transferContentFilesFromLocalToS3", method = RequestMethod.GET)
	public void transferFilesFromLocalToS3(HttpServletRequest request, HttpServletResponse response) 
	{
		String recordType = request.getParameter("recordType");
		System.out.println(" Batch Job Of Transferring Content Started");
		fileService.contentFileData(recordType);
		System.out.println(" Batch Job Of Transferring Content Ended ");
		
	}

}
