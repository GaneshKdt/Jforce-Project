package com.nmims.controllers;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import com.google.common.base.Throwables;
import com.nmims.beans.PassFailExamBean;
import com.nmims.beans.StudentExamBean;
import com.nmims.daos.PassFailTransferDao;
import com.nmims.exceptions.NoRecordFoundException;
import com.nmims.services.PGGraceMarksService;
import com.nmims.services.PassFailService;
import com.nmims.views.PassFailResultsExcelView;

@Controller
public class PassFailTransferController extends BaseController {

	@Value("#{'${ACAD_YEAR_LIST}'.split(',')}")
	private List<String> ACAD_YEAR_LIST;

	final protected List<String> MONTH_LIST = Arrays.asList("Jan", "Feb", "Mar", "Apr", "May", "Jun", "Jul", "Aug","Sep", "Oct", "Nov", "Dec");

	@Value("${SERVER_PATH}")
	private String SERVER_PATH;

	@Autowired
	private PassFailService passFailService;

	@Autowired
	@Qualifier("passFailResultsExcelView")
	private PassFailResultsExcelView passFailResultsExcelView;
	
	@Autowired
	private PGGraceMarksService graceMarksService;

	private static final Logger passFailLogger = LoggerFactory.getLogger("pg-passfail-process");

	@RequestMapping(value = "/admin/transferPassFailForm", method = RequestMethod.GET)
	public String transferPassFailForm(HttpServletRequest request, HttpServletResponse response, Model m) {
		passFailLogger.info("PassFailTransferController : PassFail Transfer : START");

		if (!checkSession(request, response)) {
			redirectToPortalApp(response);
			return null;
		}

		StudentExamBean bean = new StudentExamBean();

		m.addAttribute("bean", bean);
		m.addAttribute("yearList", ACAD_YEAR_LIST);
		m.addAttribute("monthList", MONTH_LIST);

		passFailLogger.info("PassFailTransferController : PassFail Transfer : END");
		return "transferPassFailForm";
	}

	@RequestMapping(value = "/admin/searchPassfailStaging", method = RequestMethod.POST)
	public String searchPassfailStaging(HttpServletRequest request, HttpServletResponse response, Model m,
			@ModelAttribute StudentExamBean studentExamBean) {

		if (!checkSession(request, response)) {
			redirectToPortalApp(response);
			return null;
		}
		boolean showReportDownloadTable = false;
		
		passFailLogger.info("PassFailTransferController : PassFail search : START");

		try {
			passFailLogger.info("PassFailTransferController : searching pass fail transfer for year : {} and month : {}",
					studentExamBean.getYear(), studentExamBean.getMonth());
			
			Map<String, Integer> passFailTranferReportCount = passFailService.getPassFailTranferReportCount(studentExamBean.getYear(), studentExamBean.getMonth());

			passFailTranferReportCount.entrySet().forEach(k -> m.addAttribute(k.getKey(), k.getValue()));
			
			int totalNumberOfExpectedData = passFailTranferReportCount.entrySet().stream().mapToInt(Map.Entry::getValue).sum();
			
			if(totalNumberOfExpectedData < 1)
				throw new RuntimeException("Received no data for searched year and month " + studentExamBean.getMonth()
						+ " " + studentExamBean.getYear());
			
			request.getSession().setAttribute("searchBean", studentExamBean);
			request.getSession().setAttribute("totalNumberOfExpectedData", new Integer(totalNumberOfExpectedData));
			
			showReportDownloadTable = true;

		} catch (Exception e) {
			passFailLogger.info("ERROR : while searching for passfail staging data : {}",Throwables.getStackTraceAsString(e));
			request.setAttribute("error", "true");
			request.setAttribute("errorMessage", e.getMessage());
		}

		m.addAttribute("bean", studentExamBean);
		m.addAttribute("yearList", ACAD_YEAR_LIST);
		m.addAttribute("monthList", MONTH_LIST);
		m.addAttribute("showReportDownloadTable", showReportDownloadTable);

		passFailLogger.info("PassFailTransferController : PassFail search : END");
		return "transferPassFailForm";
	}

	@RequestMapping(value = "/admin/transferPassFail", method = RequestMethod.POST)
	public ModelAndView transferPassFail(HttpServletRequest request, HttpServletResponse response,
			@ModelAttribute StudentExamBean studentExamBean) {

		if (!checkSession(request, response)) {
			redirectToPortalApp(response);
			return null;
		}

		passFailLogger.info("------------------------- PASS FAIL TRANSFER PROCESS START -------------------------");
		ModelAndView modelAndView = new ModelAndView("transferPassFailForm");

		modelAndView.addObject("bean", studentExamBean);
		modelAndView.addObject("yearList", ACAD_YEAR_LIST);
		modelAndView.addObject("monthList", MONTH_LIST);
		try {
			StringBuilder builder = new StringBuilder();
			
			//to be used for transactional purpose
			Integer totalNumberOfExpectedData = (Integer) request.getSession().getAttribute("totalNumberOfExpectedData");
			
			passFailLogger.info("Processing for year {} and month {}", studentExamBean.getYear(),studentExamBean.getMonth());

			
			Integer transferRecordsFromStagingToPassFail = passFailService.transferRecordsFromStagingToPassFail(studentExamBean.getYear(), studentExamBean.getMonth());
			
			builder.append("Processed Records : " + transferRecordsFromStagingToPassFail + ". ");
			
			List<PassFailExamBean> validityEndGraceRecords = graceMarksService.fetchAndApplyValidityEndGrace(studentExamBean.getYear(), studentExamBean.getMonth());
			
			if(validityEndGraceRecords.size() > 0) {
				request.getSession().setAttribute("validityEndGraceRecords", validityEndGraceRecords);
				
				builder.append("Validity end grace applied to ");
				builder.append(validityEndGraceRecords.size());
				builder.append(" records.");
				builder.append("<a href = '/exam/admin/downloadGraceValidityEndRecords' > Click Here </a>");
				builder.append(" To download Excel.");
			}
			
			request.setAttribute("successMessage", builder.toString());
			request.setAttribute("success", "true");

		} catch (NoRecordFoundException e) {
			passFailLogger.info("Error while trying to process pass fail staging : NoRecordFoundException : {}", e.getMessage());
			request.setAttribute("error", "true");
			request.setAttribute("errorMessage", "Error Transferring records to passfail : " + e.getMessage());
		} catch (Exception e) {
			passFailLogger.info("Error while trying to process pass fail staging : {}",Throwables.getStackTraceAsString(e));
			request.setAttribute("error", "true");
			request.setAttribute("errorMessage", "Error Transferring records to passfail");
		}
		passFailLogger.info("------------------------- PASS FAIL TRANSFER PROCESS END -------------------------");
		return modelAndView;
	}

	@RequestMapping(value = "/admin/downloadPassFailTransferReport", method = { RequestMethod.GET, RequestMethod.POST })
	public ModelAndView downloadPassFailTransferReport(HttpServletRequest request, HttpServletResponse response) {

		if (!checkSession(request, response)) {
			redirectToPortalApp(response);
			return null;
		}
		passFailLogger.info("-------------------------- Pass Fail Report download start --------------------------");
		StudentExamBean searchBean = null;
		String type = null;
		List<PassFailExamBean> passFailStagingReport = null;
		try {
			searchBean = (StudentExamBean) request.getSession().getAttribute("searchBean");
			
			type = request.getParameter("type");
			
			passFailLogger.info("downloading report for type : {} and year : {} and month : {}", type,searchBean.getYear(), searchBean.getMonth());
			
			passFailStagingReport = passFailService.getPassFailReportForType(type, searchBean.getYear(),searchBean.getMonth());
			
			response.setHeader("Content-Disposition", "attachment; filename="+type+".xlsx");

			passFailLogger.info("received data with data : {}", passFailStagingReport.size());
			
			return new ModelAndView(this.passFailResultsExcelView, "studentMarksList", passFailStagingReport);

		} catch (Exception e) {
			passFailLogger.info("ERROR : while downloading report {}", Throwables.getStackTraceAsString(e));

			ModelAndView modelAndView = new ModelAndView("transferPassFailForm");

			modelAndView.addObject("bean", new StudentExamBean());
			modelAndView.addObject("yearList", ACAD_YEAR_LIST);
			modelAndView.addObject("monthList", MONTH_LIST);
			request.setAttribute("error", "true");
			request.setAttribute("errorMessage", "Error downloading report : " + e.getMessage());
			
			return modelAndView;
		} finally {
			searchBean = null;
			type = null;
			passFailLogger.info("-------------------------- Pass Fail Report download END --------------------------");
		}
	}
}
