package com.nmims.controllers;

import java.util.ArrayList;
import java.util.HashMap;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.RandomStringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;

import com.nmims.beans.CallRepositoryBean;
import com.nmims.helpers.AmazonS3Helper;
import com.nmims.services.CallRepositoryService;

@Controller
public class CallRepositoryController {

	@Autowired 
	CallRepositoryService callRepositoryService;

	@Autowired 
	AmazonS3Helper amazonS3Helper;

	@Value( "${AWS_CALL_REPOSITORY_BUCKET}" )
	private String AWS_CALL_REPOSITORY_BUCKET;

	
	@RequestMapping(value = "/uploadStudentCounsellorCallForm", method = {RequestMethod.GET})
	public ModelAndView uploadStudentCounsellorCallForm() {
		
		ModelAndView modelAndView = new ModelAndView("uploadStudentCounsellorCalls");
		
		ArrayList<CallRepositoryBean> callList = new ArrayList<>();
		
		try {
			callList = callRepositoryService.getAllUploadedCalls();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		modelAndView.addObject("fileset", new CallRepositoryBean() );
		modelAndView.addObject("callList", callList );
		
		return modelAndView;
		
	}

	@RequestMapping(value = "/uploadStudentCounsellorCall", method = RequestMethod.POST)
	public ModelAndView uploadStudentCounsellorCall(  @ModelAttribute CallRepositoryBean filesSet, HttpServletRequest request ){
		
		Boolean success = Boolean.FALSE;
		
		int successCount = 0;
		int errorCount = 0;
		
		String folderPath =  callRepositoryService.getFolderPathForUpload( filesSet );
			
		for( MultipartFile file : filesSet.getFiles() ) {
				
			String keyName = callRepositoryService.getKeyNameForUpload( filesSet, file );

			try {
				
				success = callRepositoryService.uploadToCallRepository( filesSet, file, folderPath, keyName );
					
				if( success )
					successCount++;
				else {

					request.setAttribute("error", "true");
					request.setAttribute("errorMessage", "ERROR : Could not connect to repository.");
					
					return uploadStudentCounsellorCallForm();
					
				}
				
			}catch (Exception e) {

				e.printStackTrace();
				errorCount++;
				
			}

		}

		request.setAttribute("success", "true");
		request.setAttribute("successMessage", successCount+" files was uploaded successfully.");
		
		if( errorCount > 0 ) {
			request.setAttribute("error", "true");
			request.setAttribute("errorMessage", "Error occured while uploading "+errorCount+" files.");
		}
			
		return uploadStudentCounsellorCallForm();

		
	}
	
}
