package com.nmims.controllers;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

import com.nmims.beans.MassUploadTrackingSRBean;
import com.nmims.beans.PageStudentPortal;
import com.nmims.beans.SRTeeRevaluationReportBean;
import com.nmims.beans.ServiceRequestStudentPortal;
import com.nmims.beans.UserAuthorizationStudentPortalBean;
import com.nmims.daos.MassUploadTrackingSRDAO;
import com.nmims.daos.ServiceRequestDao;
import com.nmims.services.TeeRevaluationReportServiceInterface;
import com.nmims.views.TeeRevaluationExcelView;

import jdk.nashorn.internal.ir.RuntimeNode.Request;

/**
 * Create Report For SrTeeRevaluation
 * 
 * @author shivam sangale
 *
 */
@Controller
public class TeeRevaluationSRReportController extends BaseController {
	private final int pageSize = 10;

	@Autowired
	ServiceRequestDao serviceRequestDao;
	@Autowired
	MassUploadTrackingSRDAO massUploadTrackingSRDAO;
	@Autowired
	TeeRevaluationExcelView teeExcelView;
	@Autowired
	TeeRevaluationReportServiceInterface teeRevaluationServiceInterface;

	// rename mapping:- teerevaluationsrReportForm
	@RequestMapping(value = "/admin/teeRevaluationReportForm", method = { RequestMethod.GET, RequestMethod.POST })
	public String getTeeRevaluationSRReportForm(HttpServletRequest request, HttpServletResponse response, Model model) {
		try {
			if (!checkSession(request, response)) {
				return "studentPortalRediret";
			}
			ServiceRequestStudentPortal sr = new ServiceRequestStudentPortal();
			model.addAttribute("sr", sr);
			model.addAttribute("serviceRequest", "Revaluation of Term End Exam Marks");

		} catch (Exception e) {
			e.printStackTrace();
			// add error message
			// add loggers
		}
		System.out.println("SRTeeRevaluationForm Returning");
		return "jsp/serviceRequest/sRTeeRevaluationForm";
	}

	// rename mapping teerevaluationsrReport
	@PostMapping(value = "/admin/teeRevaluationReport")
	public ModelAndView getTeeRevaluationSRReport(@ModelAttribute ServiceRequestStudentPortal sr,
			HttpServletRequest request, HttpServletResponse response) {
		ModelAndView modelAndView = new ModelAndView("jsp/serviceRequest/sRTeeRevaluationForm");
		try {
			UserAuthorizationStudentPortalBean userAuthorization = (UserAuthorizationStudentPortalBean) request
					.getSession().getAttribute("userAuthorization_studentportal");
			String roles = "";
			if (userAuthorization != null) {
				roles = (userAuthorization.getRoles() != null && !"".equals(userAuthorization.getRoles()))
						? userAuthorization.getRoles()
						: roles;
			}
			ArrayList<SRTeeRevaluationReportBean> reportData = teeRevaluationServiceInterface
					.getSRTeeReport(sr.getSapId());
			request.getSession().setAttribute("reportData", reportData);
			request.getSession().setAttribute("roles", roles);
			PageStudentPortal<ServiceRequestStudentPortal> page = serviceRequestDao.getServiceRequestPage(1, pageSize,
					sr, getAuthorizedCodes(request));
			List<ServiceRequestStudentPortal> srList = page.getPageItems();
			modelAndView.addObject("page", page);
			modelAndView.addObject("rowCount", page.getRowCount());
			if (srList == null || srList.size() == 0) {
				request.setAttribute("error", "true");
				request.setAttribute("errorMessage", "No Records Found ");
			}
			List<Long> srIds = new ArrayList<Long>();
			Map<Long, MassUploadTrackingSRBean> trackingMailStatus = new HashMap<Long, MassUploadTrackingSRBean>();
			srIds = srList.stream().map(list -> list.getId()).collect(Collectors.toList());

			if (srIds.size() > 0)
				trackingMailStatus = massUploadTrackingSRDAO.getTrackingMailStatus(srIds);

			modelAndView.addObject("srList", srList);
			modelAndView.addObject("sr", sr);
			modelAndView.addObject("trackingMailStatus", trackingMailStatus);

		} catch (Exception e) {
			e.printStackTrace();
		}
		if (!checkSession(request, response)) {
			return new ModelAndView("jsp/login");
		}

		return modelAndView;
	}

	@RequestMapping(value = "/admin/downloadSRTeeReport", method = { RequestMethod.GET, RequestMethod.POST })
	public ModelAndView downloadSRReport(HttpServletRequest request, HttpServletResponse response) {

		if (!checkSession(request, response)) {
			return new ModelAndView("jsp/studentPortalRediret");

		}

		ServiceRequestStudentPortal sr = (ServiceRequestStudentPortal) request.getSession().getAttribute("searchBean");

		List<SRTeeRevaluationReportBean> srList = new ArrayList<SRTeeRevaluationReportBean>();
		
		return new ModelAndView(teeExcelView, "srList", srList);
	}
}
