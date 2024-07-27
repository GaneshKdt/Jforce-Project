package com.nmims.controllers;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.nmims.services.AmazonS3Service;

@Controller
public class FileMigrationBatch 
{
	
	@Autowired
	AmazonS3Service amazonS3Service;
	
	
	@RequestMapping(value = "/transferHallticketsForS3", method = RequestMethod.GET)
	public void readHallticketsForS3(HttpServletRequest request, HttpServletResponse response) 
	{
		
		System.out.println(" Batch Job Started ");
		amazonS3Service.batchJobForExistingHallTicket();
		System.out.println(" Batch Job Ended ");
		
	}
	
	//Commented For Now
	
	/*@RequestMapping(value = "/fileDeleteFromLocal", method = RequestMethod.GET)
	public void fileDeleteFromLocal(HttpServletRequest request, HttpServletResponse response) 
	{
		
		System.out.println(" Batch Job Started ");
		amazonS3Service.batchJobFileDeleteFromLocal();
		System.out.println(" Batch Job Ended ");
		
	}
*/
}
