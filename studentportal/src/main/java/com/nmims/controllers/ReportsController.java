package com.nmims.controllers;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import com.nmims.beans.ReportBean;
import com.nmims.helpers.PersonStudentPortalBean;
import com.nmims.stratergies.ReportsInterface;

@Controller
@RequestMapping("/admin")
public class ReportsController {

	@Autowired
	ReportsInterface reportService;

	@GetMapping("/powerbireports")
	public ModelAndView showPowerBIReportsList(HttpServletRequest request) {
		ModelAndView m = new ModelAndView("templates/powerbireports");
		PersonStudentPortalBean person = (PersonStudentPortalBean) request.getSession()
				.getAttribute("user_studentportal");
		List<ReportBean> reportList = new ArrayList<ReportBean>();
		try {
			reportList = reportService.getAllPowerBIReportDetails(person.getRoles());
			m.addObject("reportList", reportList);
		} catch (Exception e) {
			m.addObject("reportList", reportList);
			e.printStackTrace();
		}
		return m;
	}
}
