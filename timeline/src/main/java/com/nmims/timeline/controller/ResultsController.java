package com.nmims.timeline.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.nmims.timeline.model.Student;
import com.nmims.timeline.model.StudentMarksBean;
import com.nmims.beans.StudentsDataInRedisBean;
import com.nmims.timeline.service.ResultsService;


@RestController
@RequestMapping("/api/results")
public class ResultsController {

    private ResultsService resultsService;

    public ResultsController(ResultsService resultsService) {
        this.resultsService = resultsService;
    }
    


    /*
	@GetMapping("/getResultsBySapid")
    public List<Post> all() {
    	//System.out.println("IN ResultsController all() called --->");
        return postService.findAll();
    }
    */
    
    @GetMapping("/setAllResultsDataInRedisCache")
    public String setAllResultsDataInRedis() {
    	
        return resultsService.setAllResultsDataInRedisCache();
    }
    

    @PostMapping(path = "/setResultsDataInRedisCacheBySapid", consumes = "application/json", produces = "application/json")
    public StudentMarksBean setResultsDataInRedisCacheBySapid(@RequestBody StudentMarksBean studentMarksBean) {
        
        return resultsService.setResultsDataInRedisCacheBySapid(studentMarksBean);
    }
    
    @PostMapping(path = "/setAllResultsDataInRedisCacheByYearMonth", consumes = "application/json", produces = "application/json")
    public StudentMarksBean setAllResultsDataInRedisCacheByYearMonth(@RequestBody StudentMarksBean studentMarksBean) {
        
    	return resultsService.setAllResultsDataInRedisCacheByYearMonth(studentMarksBean);
    }
    
    @PostMapping(path = "/getResultsDataFromRedisBySapid", consumes = "application/json", produces = "application/json")
    public StudentsDataInRedisBean getResultsDataFromRedisBySapid(@RequestBody Student student) {
        
    	return resultsService.getResultsDataFromRedisBySapid(student.getSapid());
    }
    
}
