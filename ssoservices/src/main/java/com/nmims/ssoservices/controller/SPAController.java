package com.nmims.ssoservices.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller

public class SPAController {
	@RequestMapping(value = "/mbaxExamBooking/login")
    public String index(@RequestParam String sapid ) {
		return "/index.html";
	}
	@RequestMapping(value = "/mbaxExamBooking/home")
    public String home() {
		return "/index.html";
	}
	
	@RequestMapping(value = "/**/{[path:[^\\.]*}")
	public String all() {
		System.out.println("------------>Check");
	    return "/index.html";
	}
}
