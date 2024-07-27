package com.nmims.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@Controller
public class SystemPanelController {
	
	@RequestMapping(value="/TestingPingRequest",method= {RequestMethod.GET})
	public String TestingPingRequest() {
		return "TestingPingRequest";
	}
	
}
