package com.nmims.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class ThymeleafController {

	@GetMapping("/thymeleafhome")
	public String thymeleafhome() {
		return "templates/thymeleafhome";
	}
	
	@GetMapping("/adminThymleafHome")
	public String adminThymleafHome() {
		return "templates/staffHome";
	}
}
