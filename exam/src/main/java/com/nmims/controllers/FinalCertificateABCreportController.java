package com.nmims.controllers;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.ModelAndView;

import com.nmims.beans.FinalCertificateABCreportBean;
import com.nmims.interfaces.FinalCertificateABCreportServiceInterface;
import com.nmims.views.ABCFinalReportExcelView;

@Controller
public class FinalCertificateABCreportController {
	@Value("#{'${CURRENT_YEAR_LIST}'.split(',')}")
	private ArrayList<String> currentYearList;

	@Value("#{'${EXAM_MONTH_LIST}'.split(',')}")
	private List<String> EXAM_MONTH_LIST;

	@Autowired
	private FinalCertificateABCreportServiceInterface finalCertificateABCreportServiceInterface;
	
	
	@Autowired
	ABCFinalReportExcelView abcexcelreport;
	
	@GetMapping(value = "admin/getFinalCertificateABCreportForm")
	public ModelAndView getFinalCertificateABCreportForm(HttpServletRequest request, HttpServletResponse response) {
		FinalCertificateABCreportBean ABCreportbean = new FinalCertificateABCreportBean();
		ModelAndView modelAndView = new ModelAndView("report/ABCFinalCertificateReport");
		modelAndView.addObject("ABCreportbean", ABCreportbean);
		modelAndView.addObject("examYearList", currentYearList);
		modelAndView.addObject("examMonthList", EXAM_MONTH_LIST);
		return modelAndView;
	}

	@PostMapping(value = "admin/getFinalCertificateABC")
	public ModelAndView getFinalCertificateABC(HttpServletRequest request, HttpServletResponse response,
			@ModelAttribute FinalCertificateABCreportBean bean) {
		ModelAndView modelAndView = new ModelAndView("report/ABCFinalCertificateReport");
		FinalCertificateABCreportBean abcReportbean = new FinalCertificateABCreportBean();
		List<FinalCertificateABCreportBean> abcReportDataForFinalCertificate= new ArrayList<FinalCertificateABCreportBean>();
		try {
			abcReportDataForFinalCertificate= finalCertificateABCreportServiceInterface
					.getStudentsDataForABCreport(bean);
		} catch (Exception e) {
			request.setAttribute("error", "true");
			request.setAttribute("errorMessage", "Error in Generating Report : " + e.getMessage());
		}
		request.getSession().setAttribute("ABCreport", abcReportDataForFinalCertificate);
		modelAndView.addObject("ABCreportbean", abcReportbean);
		modelAndView.addObject("examYearList", currentYearList);
		modelAndView.addObject("examMonthList", EXAM_MONTH_LIST);
		modelAndView.addObject("finalCertificateABCreport",abcReportDataForFinalCertificate);
		return modelAndView;
	}
	@GetMapping(value = "admin/getFinalCertificateReportForABC")
	public ModelAndView getFinalCertificateReportForABC(HttpServletRequest request, HttpServletResponse response) {
		return new ModelAndView(abcexcelreport);
	}

}
