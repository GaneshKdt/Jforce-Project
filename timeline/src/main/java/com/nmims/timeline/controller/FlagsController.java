package com.nmims.timeline.controller;

import java.util.List;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.nmims.beans.FlagBean;
import com.nmims.timeline.service.FlagsService;

@RestController
@RequestMapping("/api/flag")
public class FlagsController {


    private FlagsService flagsService;

    public FlagsController(FlagsService flagsService) {
        this.flagsService = flagsService;
    }
    /*
     
	FlagBean getByKey(String key);
	List<FlagBean> getByKeysCommaSeperated(String keys);
	List<FlagBean> getAll();
	String save(FlagBean flagBean);
	String deleteByKey(String key);
	

     * */

    @PostMapping("/getByKey")
    public FlagBean getByKey(@RequestBody FlagBean flag) {
    	
        return flagsService.getByKey(flag.getKey());
    }
    
    @PostMapping("/getByKeysCommaSeperated")
    public List<FlagBean> getByKeysCommaSeperated(@RequestBody FlagBean flag) {
    	
        return flagsService.getByKeysCommaSeperated(flag.getKey());
    }
    
    @PostMapping("/getAll")
    public List<FlagBean> getAll() {
    	
        return flagsService.getAll();
    }

    @PostMapping("/save")
    public String save(@RequestBody FlagBean flag) {
    	
        return flagsService.save(flag);
    }

    @PostMapping("/deleteByKey")
    public String deleteByKey(@RequestBody FlagBean flag) {
    	
        return flagsService.deleteByKey(flag.getKey());
    }
    
}
