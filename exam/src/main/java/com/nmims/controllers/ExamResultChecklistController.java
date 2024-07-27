package com.nmims.controllers;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

import com.google.common.base.Throwables;
import com.nmims.beans.StudentExamBean;
import com.nmims.services.ExamResultChecklistService;

@Controller
public class ExamResultChecklistController extends BaseController {

	@Autowired
	private ExamResultChecklistService examResultsChecklistService;

	@Value("#{'${ACAD_YEAR_LIST}'.split(',')}")
	private List<String> ACAD_YEAR_LIST;

	protected List<String> MONTH_LIST = Arrays.asList("Jan", "Feb", "Mar", "Apr", "May", "Jun", "Jul", "Aug", "Sep",
			"Oct", "Nov", "Dec");

	public static final Logger logger = LoggerFactory.getLogger("examResultsChecklist");

	private final String ERROR_MESSAGE_STR = "errorMessage";
	private final String ERROR_STR = "error";
	private final String TEE_RESULTS_DASHBOARD_JSP = "teeResultsDashboard";
	private final String YEAR_MONTH_STR = "yearMonth";
	private final String TRUE_STR = "true";
	private final String SUCCESS_STR = "success";
	private final String SUCCESS_MESSAGE_STR = "successMessage";

	@RequestMapping(value = "/admin/teeResultsDashboard", method = RequestMethod.GET)
	public ModelAndView getTEEResultsDashboard(HttpServletRequest request, HttpServletResponse response) {

		if (!checkSession(request, response)) {
			redirectToPortalApp(response);
			return null;
		}

		boolean showCount = false;

		logger.info("------------------ GET TEE RESULTS DASHBOARD START ------------------");

		ModelAndView modelAndView = new ModelAndView(TEE_RESULTS_DASHBOARD_JSP);
		try {
			StudentExamBean bean = new StudentExamBean();
			
			setModelAndViewForChecklist(showCount, modelAndView, bean);

		} catch (Exception e) {
			logger.info("ERROR while getting dashboard count : {}", Throwables.getStackTraceAsString(e));
		}

		logger.info("------------------ GET TEE RESULTS DASHBOARD END ------------------");

		return modelAndView;
	}


	@RequestMapping(value = "/admin/getExamResultsChecklistCount", method = RequestMethod.POST)
	public ModelAndView getExamResultsChecklistCount(HttpServletRequest request, HttpServletResponse response,
			@ModelAttribute StudentExamBean studentExamBean) {

		if (!checkSession(request, response)) {
			redirectToPortalApp(response);
			return null;
		}

		logger.info("------------------ GET TEE RESULTS DASHBOARD START ------------------");
		Map<String, Integer> dashboardCountMap = null;
		ModelAndView modelAndView = new ModelAndView(TEE_RESULTS_DASHBOARD_JSP);

		boolean showCount = false;
		try {

			String yearMonth = studentExamBean.getMonth() + " " + studentExamBean.getYear();

			dashboardCountMap = examResultsChecklistService.getDashboardCountForExamResults(studentExamBean.getMonth(),
					studentExamBean.getYear());

			dashboardCountMap.entrySet().forEach(k -> modelAndView.addObject(k.getKey(), k.getValue()));

			showCount = true;

			modelAndView.addObject(YEAR_MONTH_STR, yearMonth);

			logger.info("Returning Dashboard for year and month : {}", yearMonth);

		} catch (Exception e) {
			setErrorAttributeAndLog(request, e);
		}

		setModelAndViewForChecklist(showCount, modelAndView, studentExamBean);

		logger.info("------------------ GET TEE RESULTS DASHBOARD END ------------------");

		return modelAndView;
	}

	@RequestMapping(value = "/admin/populateResultChecklist", method = RequestMethod.POST)
	public ModelAndView populateResultChecklist(HttpServletRequest request, HttpServletResponse response,
			@ModelAttribute StudentExamBean studentExamBean) {

		if (!checkSession(request, response)) {
			redirectToPortalApp(response);
			return null;
		}

		logger.info("------------------ POPULATE RESULT CHECKLIST START ------------------");
		ModelAndView modelAndView = new ModelAndView(TEE_RESULTS_DASHBOARD_JSP);

		boolean showCount = false;
		try {
			String userId = (String) request.getSession().getAttribute("userId");
			Integer populateResultChecklist = examResultsChecklistService
					.populateResultChecklist(studentExamBean.getYear(), studentExamBean.getMonth(), userId);
			
			logger.info("Populated number of rows : {} for cycle : {} {} ", populateResultChecklist.intValue(),
					studentExamBean.getYear(), studentExamBean.getMonth());
			
			modelAndView.addObject(SUCCESS_STR, TRUE_STR);
			modelAndView.addObject(SUCCESS_MESSAGE_STR,
					"created Base data for : " + populateResultChecklist.intValue() + " number of records");

		} catch (RuntimeException e) {
			setErrorAttributeAndLog(request, e);
		} catch (Exception e) {
			setErrorAttributeAndLog(request, e);
		}

		setModelAndViewForChecklist(showCount, modelAndView, studentExamBean);

		logger.info("------------------ POPULATE RESULT CHECKLIST END ------------------");

		return modelAndView;
	}

	private void setModelAndViewForChecklist(boolean showCount, ModelAndView modelAndView, StudentExamBean bean) {
		
		modelAndView.addObject("bean", bean);
		modelAndView.addObject("yearList", ACAD_YEAR_LIST);
		modelAndView.addObject("monthList", MONTH_LIST);
		modelAndView.addObject("showCount", showCount);
	}
	
	private void setErrorAttributeAndLog(HttpServletRequest request, Exception e) {
		request.setAttribute(ERROR_STR, TRUE_STR);
		
		request.setAttribute(ERROR_MESSAGE_STR, "ERROR populating checklist records : "
				+ e.getMessage());
		
		logger.info("ERROR populating checklist table : {}", Throwables.getStackTraceAsString(e));
	}

}
