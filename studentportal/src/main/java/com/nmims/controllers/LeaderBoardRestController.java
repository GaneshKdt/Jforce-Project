package com.nmims.controllers;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.apache.commons.lang.exception.ExceptionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.nmims.beans.StudentStudentPortalBean;
import com.nmims.beans.StudentRankBean;
import com.nmims.daos.PortalDao;
import com.nmims.services.LeaderBoardService;

@RestController
@RequestMapping("m")
public class LeaderBoardRestController {
	
	@Autowired
	ApplicationContext act;
	
	@Autowired
	HomeController homeController;

	@Autowired
	LeaderBoardService leaderBoardService;

	private static final Logger rankLogger = LoggerFactory.getLogger("rankDenormalization");
	
	@PostMapping(value = "/cycleWiseRank", consumes ="application/json", produces = "application/json")
    public ResponseEntity<StudentRankBean> getCycleWiseRank(@RequestBody StudentStudentPortalBean bean){		
    	
        HttpHeaders header = new HttpHeaders();
        StudentRankBean rank = new StudentRankBean();                      

        try {

        	if(!homeController.checkIfMovingResultsToCache()) {

        		rank = leaderBoardService.getDenormalizedCycleWiseRankBySapid( bean.getConsumerProgramStructureId(), bean.getYear(), 
        				bean.getMonth(), bean.getSem(), bean.getSapid(), bean.getProgram() );
        		
	        }else {
	        	rank = new StudentRankBean();   
	        }
        	
        	return new ResponseEntity<StudentRankBean>(rank, header, HttpStatus.OK);
        	
        } catch( Throwable throwable ){

        	rankLogger.info("in cycleWiseRank got exception: "+ExceptionUtils.getFullStackTrace(throwable) );
            return new ResponseEntity<StudentRankBean>(rank, header, HttpStatus.BAD_REQUEST);
            
        }
        
    }
    
    @PostMapping(value = "/subjectWiseRank", consumes ="application/json", produces = "application/json")
    public ResponseEntity<StudentRankBean> getSubjectWiseRank(@RequestBody StudentStudentPortalBean bean){
    	
        HttpHeaders header = new HttpHeaders();
        StudentRankBean rank = new StudentRankBean();	
        
        try {
        	if(!homeController.checkIfMovingResultsToCache()) {

        		rank = leaderBoardService.getDenormalizedSubjectWiseRankBySapid( bean.getConsumerProgramStructureId(), bean.getProgram(),
        				bean.getYear(), bean.getMonth(), bean.getSem(), bean.getSubject(), bean.getSapid() );
			
        	}
			
			return new ResponseEntity<StudentRankBean>(rank, header, HttpStatus.OK);

			
		} catch( Throwable throwable){
			
        	rankLogger.info("in subjectWiseRank got exception: "+ExceptionUtils.getFullStackTrace(throwable) );
            return new ResponseEntity<StudentRankBean>(rank, header, HttpStatus.BAD_REQUEST);
            
        }
        
    }

	@PostMapping(path = "/getRankConfiguration", consumes ="application/json", produces = "application/json")
    public ResponseEntity<List<List<StudentRankBean>>> getRankConfiguration(@RequestBody StudentStudentPortalBean bean){
		
		HttpHeaders header = new HttpHeaders();
		header.add("Content-Type", "application/json");
		
		List<List<StudentRankBean>> details = new ArrayList<>();
		List<StudentRankBean> rankConfigList = new ArrayList<>();
		List<StudentRankBean> rankSubjectConfigList = new ArrayList<>();

        try {
        	if(!homeController.checkIfMovingResultsToCache()) {
                
        	rankConfigList = leaderBoardService.getCycleWiseRankConfigList(bean.getUserId());
        	Collections.reverse(rankConfigList);
        	
        	rankSubjectConfigList = leaderBoardService.getSubjectWiseRankConfigList(bean.getUserId());
        	Collections.reverse(rankSubjectConfigList);
        	
        	}
        	
        } catch( Throwable throwable){
        	rankLogger.info("in getRankConfiguration got exception: "+ExceptionUtils.getFullStackTrace(throwable) );       
        }       
        
        details.add(rankConfigList);
        details.add(rankSubjectConfigList);
        
        return new ResponseEntity<List<List<StudentRankBean>>>(details, header, HttpStatus.OK);
        
    }
	
	@PostMapping(path = "/getCycleWiseRankConfiguration", consumes ="application/json", produces = "application/json")
    public ResponseEntity< List<StudentRankBean> > getCycleWiseRankConfiguration(@RequestBody StudentStudentPortalBean bean){
		
		HttpHeaders header = new HttpHeaders();
		header.add("Content-Type", "application/json");
		
		List<StudentRankBean> rankConfigList = new ArrayList<>();

        try {
        	if(!homeController.checkIfMovingResultsToCache()) {
        
        		rankConfigList = leaderBoardService.getCycleWiseRankConfigList(bean.getUserId());
        		Collections.reverse(rankConfigList);
        	
        	}
        } catch( Throwable throwable ){
        	rankLogger.info("in getRankConfiguration got exception: "+ExceptionUtils.getFullStackTrace(throwable) );      
        }       
        
        return new ResponseEntity< List<StudentRankBean> >(rankConfigList, header, HttpStatus.OK);
        
    }

	@PostMapping(path = "/getSubejctWiseRankConfiguration", consumes ="application/json", produces = "application/json")
    public ResponseEntity< List<StudentRankBean> > getSubejctWiseRankConfiguration(@RequestBody StudentStudentPortalBean bean){
		
		HttpHeaders header = new HttpHeaders();
		header.add("Content-Type", "application/json");
		
		List<StudentRankBean> rankSubjectConfigList = new ArrayList<>();

        try {
        	
        	if(!homeController.checkIfMovingResultsToCache()) {
                
        	rankSubjectConfigList = leaderBoardService.getSubjectWiseRankConfigList(bean.getUserId());
        	Collections.reverse(rankSubjectConfigList);
        	}
        	
        } catch( Throwable throwable ){
        	rankLogger.info("in getRankConfiguration got exception: "+ExceptionUtils.getFullStackTrace(throwable) );       
        }       
        
        return new ResponseEntity< List<StudentRankBean> >(rankSubjectConfigList, header, HttpStatus.OK);
        
    }
	
}
