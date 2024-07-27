package com.nmims.timeline.controller;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.nmims.timeline.model.CounterBean;
import com.nmims.timeline.service.CounterService;
 
@RestController
@RequestMapping("/api/counter")
public class CounterController {

    private CounterService counterService;

    public CounterController(CounterService counterService) {
        this.counterService = counterService;
    }

    @PostMapping(path = "/save", consumes = "application/json", produces = "application/json")
    public String save(@RequestBody CounterBean counterBean) {
        
    	//System.out.println("IN CounterController save() called --->");
    	//System.out.println("IN CounterController save() got data : "+counterBean.toString());
        return counterService.save(counterBean.getTableName(),counterBean.getKeyName(),counterBean.getCounterData());
    }
    
    @PostMapping(path = "/findByTableNameKeyName", consumes = "application/json", produces = "application/json")
    public CounterBean findByTableNameKeyName(@RequestBody CounterBean counterBean) {
        
    	//System.out.println("IN CounterController findByTableNameKeyName() called --->");
    	//System.out.println("IN CounterController findByTableNameKeyName() got data : "+counterBean.toString());
    	counterBean.setCounterData(counterService.findByTableNameKeyName(counterBean.getTableName(),counterBean.getKeyName()));
        //System.out.println("IN CounterController findByTableNameKeyName() response : "+counterBean.toString());
        return counterBean;
    }
    
}
