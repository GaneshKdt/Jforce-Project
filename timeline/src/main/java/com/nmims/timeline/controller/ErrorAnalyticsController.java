package com.nmims.timeline.controller;

import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.nmims.timeline.model.ErrorAnalytics;
import com.nmims.timeline.service.ErrorAnalyticsService;

@RestController
@RequestMapping("/api/errorAnalytics")
@CrossOrigin(origins = "*", allowedHeaders = "*")
public class ErrorAnalyticsController {
	

    private static final Logger logger = LogManager.getLogger(ErrorAnalyticsController.class);
    
    /*
	@Value( "${SERVER}" )
	private String SERVER;

	@Value( "${ENVIRONMENT}" )
	private String ENVIRONMENT;
	*/
    

    private ErrorAnalyticsService errorAnalyticsService;
    
    public ErrorAnalyticsController(ErrorAnalyticsService errorAnalyticsService) {
    	this.errorAnalyticsService =errorAnalyticsService;
    }
    
    @GetMapping("/findAllByModule")
    public List<ErrorAnalytics> findAllByModule(@RequestParam String module) {
    	//System.out.println("IN StudentTestController findAllByModule() called --->");
    	
    	return errorAnalyticsService.findAllByModule(module);
    	
    }

    

}
