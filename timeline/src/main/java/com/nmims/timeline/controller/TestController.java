package com.nmims.timeline.controller;

import java.util.List;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.nmims.timeline.model.GetTestQuestionsFromRedisByTestIdResponseBean;
import com.nmims.timeline.model.TestBean;
import com.nmims.timeline.service.TestService;


@RestController
@RequestMapping("/api/test")
public class TestController {


    private TestService testService;

    public TestController(TestService testService) {
        this.testService = testService;
    }
    

    @PostMapping("/setTestQuestionsInRedisByTestId")
    public String setTestQuestionsInRedisByTestId(@RequestBody TestBean test) {
    	//System.out.println("IN TestController setTestQuestionsInRedisByTestId() called --->");
        return testService.setTestQuestionsInRedisByTestId(test.getId());
    }

    @PostMapping("/getTestQuestionsFromRedisByTestId")
    public GetTestQuestionsFromRedisByTestIdResponseBean getTestQuestionsFromRedisByTestId(@RequestBody TestBean test) {
    	//System.out.println("IN TestController getTestQuestionsFromRedisByTestId() called --->");
        return testService.getTestQuestionsFromRedisByTestId(test.getId());
    }
    

    @PostMapping("/setUpcomingTestsPerStudentInRedisByTestId")
    public String setUpcomingTestsPerStudentInRedisByTestId(@RequestBody TestBean test) {
    	//System.out.println("IN TestController setUpcomingTestsPerStudentInRedisByTestId() called --->");
        return testService.setUpcomingTestsPerStudentInRedisByTestId(test.getId());
    }

    @PostMapping("/getUpcomingTestsFromCacheBySapid")
    public List<TestBean> getUpcomingTestsBySapid(@RequestBody TestBean test) {
    	//System.out.println("IN TestController getUpcomingTestsBySapid() called ---> sapid :"+test.getSapid());
        return testService.getUpcomingTestsBySapid(test.getSapid());
    }
    
}
