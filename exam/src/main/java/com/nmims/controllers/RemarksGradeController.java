/**
 * 
 */
package com.nmims.controllers;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import com.nmims.beans.CenterExamBean;
import com.nmims.beans.ConsumerProgramStructureExam;
import com.nmims.beans.MDMSubjectCodeBean;
import com.nmims.beans.ProgramStructureBean;
import com.nmims.beans.RemarksGradeBean;
import com.nmims.daos.AssignmentsDAO;
import com.nmims.daos.DashboardDAO;
import com.nmims.daos.MDMSubjectCodeDAO;
import com.nmims.daos.StudentMarksDAO;
import com.nmims.interfaces.GradingTypeServiceInterface;
import com.nmims.interfaces.ProductGradingFactoryInterface;
import com.nmims.views.DownloadRemarksGradeAbsentReport;
import com.nmims.views.DownloadRemarksGradeCopyCaseReport;
import com.nmims.views.DownloadRemarksGradeEligibleStudentsReport;

/**

 * @author vil_m
 *
 */
@Controller
@RequestMapping("/admin")
public class RemarksGradeController extends BaseController {
	
	private static final String KEY_ERROR = "error";
	private static final String KEY_SUCCESS = "success";
	
	@Autowired(required = false)
	ApplicationContext act;
	
	@Autowired
	AssignmentsDAO asignmentsDAO;
	
	@Autowired
	DashboardDAO dashboardDAO;

	@Autowired
	MDMSubjectCodeDAO mdmSubjectCodeDAO;
	
	@Autowired
	StudentMarksDAO studentMarksDAO;
	
	@Autowired
	ProductGradingFactoryInterface productGradingFactory;
	
	@Value("#{'${ACAD_YEAR_LIST}'.split(',')}")
	private List<String> ACAD_YEAR_LIST;
	
	@Value("#{'${ACAD_MONTH_LIST}'.split(',')}")
	private List<String> ACAD_MONTH_LIST;
	
	@Value("#{'${EXAM_MONTH_LIST}'.split(',')}")
	private List<String> EXAM_MONTH_LIST;
	
	@Value("${CURRENT_ACAD_YEAR}")
	private String CURRENT_ACAD_YEAR; 

	@Value("${CURRENT_ACAD_MONTH}")
	private String CURRENT_ACAD_MONTH;
	
	private GradingTypeServiceInterface gradingTypeServiceInterface = null;
	
	//page 1
	private static final List<String> yearList;
	//page 4
	private static final List<String> semList;
	
	public static final Logger logger = LoggerFactory.getLogger("checkListRG");
	
	@Autowired
	@Qualifier("downloadRemarksGradeAbsentReport")
	public DownloadRemarksGradeAbsentReport downloadRemarksGradeAbsentReport;
	
	@Autowired
	@Qualifier("downloadRemarksGradeCopyCaseReport")
	public DownloadRemarksGradeCopyCaseReport downloadRemarksGradeCopyCaseReport;
	
	@Autowired
	@Qualifier("downloadRemarksGradeEligibleStudentsReport")
	public DownloadRemarksGradeEligibleStudentsReport downloadRemarksGradeEligibleStudentsReport;

	public RemarksGradeController() {
		super();
		// TODO Auto-generated constructor stub
	}
	
	static {
		yearList = new ArrayList<String>(Arrays.asList( 
				"2020","2021","2022","2023","2024","2025","2026"));
		semList = new ArrayList<String>(Arrays.asList("1","2","3","4","5","6","7","8","9","10"));
	}
	
	//step0 - Checklist screen
	@RequestMapping(value = "/stepRG0", method = { RequestMethod.GET })
	public ModelAndView stepRG0(HttpServletRequest request, HttpServletResponse response) {
		logger.info("Entering RemarksGradeController : stepRG0");
		ModelAndView mav = null;
		if (!checkSession(request, response)) {
			redirectToPortalApp(response);
		} else {
			mav = new ModelAndView("remarksgrade/rg0");
		}
		return mav;
	}
	
	//step1 - upload marks
	@RequestMapping(value = "/stepRG1", method = { RequestMethod.GET })
	public ModelAndView stepRG1(HttpServletRequest request, HttpServletResponse response) {
		logger.info("Entering RemarksGradeController : stepRG1");
		ModelAndView mav = null;
		if (!checkSession(request, response)) {
			redirectToPortalApp(response);
		} else {
			request.setAttribute("yearList", yearList);
			request.setAttribute("monthList", EXAM_MONTH_LIST);
			RemarksGradeBean fileBean = new RemarksGradeBean();
			mav = new ModelAndView("remarksgrade/rg1");
			mav.addObject("remarksGradeBean",fileBean);
		}
		return mav;
	}
	
	@RequestMapping(value = "/uploadMarksRG1", method = { RequestMethod.POST })
	public ModelAndView uploadMarksRG1(HttpServletRequest request, HttpServletResponse response,
			@ModelAttribute RemarksGradeBean remarksGradeBean) {
		logger.info("Entering RemarksGradeController : uploadMarksRG1");
		ModelAndView mav = null;
		List<RemarksGradeBean> list = null;
		String message = null;
		String userId = null;
		
		if (!checkSession(request, response)) {
			redirectToPortalApp(response);
		} else {
			request.setAttribute("yearList", yearList);
			request.setAttribute("monthList", EXAM_MONTH_LIST);
			//RemarksGradeBean fileBean = new RemarksGradeBean();
			mav = new ModelAndView("remarksgrade/rg1");
			mav.addObject("remarksGradeBean", remarksGradeBean);
			
			//GradingTypeServiceInterface gradingTypeServiceInterface = null;
			gradingTypeServiceInterface = productGradingFactory.getProductGradingType(ProductGradingFactoryInterface.PRODUCT_UG, ProductGradingFactoryInterface.GRADING_REMARK);
			
			userId = (String) request.getSession().getAttribute("userId");
			list = gradingTypeServiceInterface.uploadMarksExcelFile(remarksGradeBean, userId);
			if(null != list) {
				if(list.size() == 1) {
					if(RemarksGradeBean.KEY_ERROR.equalsIgnoreCase(list.get(0).getStatus())) {
						message = list.get(0).getMessage();
						request.setAttribute(KEY_ERROR, message);
					} else {
						message = list.get(0).getMessage();
						request.setAttribute(KEY_SUCCESS, message);
					}
				}
			}
		}
		return mav;
	}
	
	//step 2 - view uploaded marks
	@RequestMapping(value = "/stepRG2", method = { RequestMethod.GET })
	public ModelAndView stepRG2(HttpServletRequest request, HttpServletResponse response) {
		logger.info("Entering RemarksGradeController : stepRG2");
		int rowCount = 0;
		ModelAndView mav = null;
		Map<String,String> consumerTypeMap = null;
		Map<String,String> programStructureMap = null;
		Map<String,String> programMap = null;
		try {
			if (!checkSession(request, response)) {
				redirectToPortalApp(response);
			} else {
				consumerTypeMap = prepareMapConsumerType();
				programStructureMap = prepareMapProgramStructure();
				programMap = prepareMapProgram();
				request.setAttribute("consumerTypeMap", consumerTypeMap);
				request.setAttribute("programStructureMap", programStructureMap);
				request.setAttribute("programMap", programMap);
				request.setAttribute("yearList", yearList);
				request.setAttribute("monthList", EXAM_MONTH_LIST);
				RemarksGradeBean fileBean = new RemarksGradeBean();
				mav = new ModelAndView("remarksgrade/rg2");
				mav.addObject("remarksGradeBean",fileBean);
				mav.addObject("rowCount", rowCount);
			}
		} catch(Exception e) {
			logger.error("RemarksGradeController : stepRG2 : " + e.getMessage());
			
			request.setAttribute(KEY_ERROR, e.getMessage());
		}
		return mav;
	}
	
	@RequestMapping(value = "/displayDataRG2", method = { RequestMethod.POST })
	public ModelAndView displayDataRG2(HttpServletRequest request, HttpServletResponse response,
			@ModelAttribute RemarksGradeBean remarksGradeBean) {
		logger.info("Entering RemarksGradeController : displayDataRG2");
		int rowCount = 0;
		List<RemarksGradeBean> datalist = null;
		Map<String, String> consumerTypeMap = null;
		Map<String,String> programStructureMap = null;
		Map<String,String> programMap = null;
		ModelAndView mav = null;
		try {
			if (!checkSession(request, response)) {
				redirectToPortalApp(response);
			} else {
				consumerTypeMap = prepareMapConsumerType();
				programStructureMap = prepareMapProgramStructure();
				programMap = prepareMapProgram();
				request.setAttribute("consumerTypeMap", consumerTypeMap);
				request.setAttribute("programStructureMap", programStructureMap);
				request.setAttribute("programMap", programMap);
				request.setAttribute("yearList", yearList);
				request.setAttribute("monthList", EXAM_MONTH_LIST);
				//RemarksGradeBean fileBean = new RemarksGradeBean();
				mav = new ModelAndView("remarksgrade/rg2");
				mav.addObject("remarksGradeBean", remarksGradeBean);
				
				gradingTypeServiceInterface = productGradingFactory.getProductGradingType(
						ProductGradingFactoryInterface.PRODUCT_UG, ProductGradingFactoryInterface.GRADING_REMARK);
				datalist = gradingTypeServiceInterface.displayMarksExcelFile(remarksGradeBean);
				if(null != datalist && !datalist.isEmpty()) {
					rowCount = datalist.size();
					request.setAttribute(KEY_SUCCESS, "Total Rows Found : "+rowCount);
				} else {
					request.setAttribute(KEY_SUCCESS, "No Rows Found!");
				}
				//mav.addObject("rowCount", rowCount);
				//mav.addObject("dataList", datalist);
			}
		} catch(Exception e) {
			logger.error("RemarksGradeController : displayDataRG2 : " + e.getMessage());
			
			request.setAttribute(KEY_ERROR, e.getMessage());
		} finally {
			mav.addObject("rowCount", rowCount);
			mav.addObject("dataList", datalist);
		}
		return mav;
	}
	
	@RequestMapping(value = "/clearRG2", method = { RequestMethod.POST })
	public ModelAndView clearRG2(HttpServletRequest request, HttpServletResponse response) {
		logger.info("Entering RemarksGradeController : clearRG2");
		return stepRG2(request, response);
	}
	
	//Copycase
	@RequestMapping(value = "/stepRG25", method = { RequestMethod.GET })
	public ModelAndView stepRG25(HttpServletRequest request, HttpServletResponse response) {
		logger.info("Entering RemarksGradeController : stepRG25");
		int rowCount = 0;
		ModelAndView mav = null;
		try {
			if (!checkSession(request, response)) {
				redirectToPortalApp(response);
			} else {
				request.setAttribute("yearList", yearList);
				request.setAttribute("monthList", EXAM_MONTH_LIST);
				RemarksGradeBean fileBean = new RemarksGradeBean();
				mav = new ModelAndView("remarksgrade/rg25");
				mav.addObject("remarksGradeBean",fileBean);
				mav.addObject("rowCount", rowCount);
			}
		} catch(Exception e) {
			logger.error("RemarksGradeController : stepRG25 : " + e.getMessage());
			
			request.setAttribute(KEY_ERROR, e.getMessage());
		}
		return mav;
	}
	
	@RequestMapping(value = "/searchRG25", method = { RequestMethod.POST })
	public ModelAndView searchRG25(HttpServletRequest request, HttpServletResponse response,
			@ModelAttribute RemarksGradeBean remarksGradeBean) {
		logger.info("Entering searchRG25");
		ModelAndView mav = null;
		int rowCount = 0;
		List<RemarksGradeBean> datalist = null;
		try {
			if (!checkSession(request, response)) {
				redirectToPortalApp(response);
			} else {
				logger.info("RemarksGradeController : searchRG25 : (year,month,request,response) " + "("
						+ remarksGradeBean.getYear() + "," + remarksGradeBean.getMonth() + "," + request + ","
						+ response + ")");
				request.removeAttribute(KEY_SUCCESS);
				request.removeAttribute(KEY_ERROR);
				
				request.setAttribute("yearList", yearList);
				request.setAttribute("monthList", EXAM_MONTH_LIST);
				//RemarksGradeBean fileBean = new RemarksGradeBean();
				mav = new ModelAndView("remarksgrade/rg25");
				mav.addObject("remarksGradeBean", remarksGradeBean);
				mav.addObject("eyear", remarksGradeBean.getYear());
				mav.addObject("emonth", remarksGradeBean.getMonth());
				
				gradingTypeServiceInterface = productGradingFactory.getProductGradingType(
						ProductGradingFactoryInterface.PRODUCT_UG, ProductGradingFactoryInterface.GRADING_REMARK);
				datalist = gradingTypeServiceInterface.searchForCopyCaseStudents(remarksGradeBean);
				if(null != datalist && !datalist.isEmpty()) {
					rowCount = datalist.size();
					request.setAttribute(KEY_SUCCESS, "Total Rows Found : "+rowCount);
				} else {
					request.setAttribute(KEY_SUCCESS, "No Rows Found!");
				}
			}
		} catch (Exception e) {
			logger.error("RemarksGradeController : searchRG25 : " + e.getMessage());
			
			request.setAttribute(KEY_ERROR, e.getMessage());
		} finally {
			mav.addObject("rowCount", rowCount);
			mav.addObject("dataList", datalist);
		}
		return mav;
	}
	
	@RequestMapping(value = "/downloadRG25", method = { RequestMethod.GET })
	public ModelAndView downloadRG25(HttpServletRequest request, HttpServletResponse response,
			@RequestParam String eyear, @RequestParam String emonth) {
		logger.info("Entering downloadRG25");
		ModelAndView mav = null;
		RemarksGradeBean bean = null;
		List<RemarksGradeBean> list1 = null;
		try {
			if (!checkSession(request, response)) {
				redirectToPortalApp(response);
			} else {
				logger.info("RemarksGradeController : downloadRG25 : (year,month,request,response) " + "(" + eyear + ","
						+ emonth + "," + request + "," + response + ")");
				bean = new RemarksGradeBean(null, eyear, emonth, null, null, null, null, null, null);

				list1 = gradingTypeServiceInterface.downloadCopyCaseStudents(bean);
				if (null != list1 && !list1.isEmpty()) {
					logger.info("RemarksGradeController : downloadRG25 : Total to be Exported : " + list1.size());
				}
				mav = new ModelAndView(downloadRemarksGradeCopyCaseReport, "rgCopyCaseList", list1);
			}
		} catch (Exception e) {
			logger.error("RemarksGradeController : downloadRG25 : " + e.getMessage());
			
			request.setAttribute(KEY_ERROR, e.getMessage());
		}
		return mav;
	}
	
	@RequestMapping(value = "/moveRG25", method = { RequestMethod.POST })
	public ModelAndView moveRG25(HttpServletRequest request, HttpServletResponse response,
			@ModelAttribute RemarksGradeBean remarksGradeBean) {
		logger.info("Entering moveRG25");
		ModelAndView mav = null;
		int rowCount = 0;
		String userId = null;
		RemarksGradeBean resultBean = null;
		try {
			if (!checkSession(request, response)) {
				redirectToPortalApp(response);
			} else {
				logger.info("RemarksGradeController : moveRG25 : (year,month,request,response) " + "("
						+ remarksGradeBean.getYear() + "," + remarksGradeBean.getMonth() + "," + request + ","
						+ response + ")");
				request.removeAttribute(KEY_SUCCESS);
				request.removeAttribute(KEY_ERROR);
				
				request.setAttribute("yearList", yearList);
				request.setAttribute("monthList", EXAM_MONTH_LIST);
				//RemarksGradeBean fileBean = new RemarksGradeBean();
				mav = new ModelAndView("remarksgrade/rg25");
				mav.addObject("remarksGradeBean", remarksGradeBean);
				
				userId = (String) request.getSession().getAttribute("userId");
				gradingTypeServiceInterface = productGradingFactory.getProductGradingType(
						ProductGradingFactoryInterface.PRODUCT_UG, ProductGradingFactoryInterface.GRADING_REMARK);
				resultBean = gradingTypeServiceInterface.moveCopyCaseStudents(remarksGradeBean, userId);
				request.setAttribute(resultBean.getStatus(), resultBean.getMessage());
			}
		} catch (Exception e) {
			logger.error("RemarksGradeController : moveRG25 : " + e.getMessage());
			
			request.setAttribute(KEY_ERROR, e.getMessage());
		} finally {
			mav.addObject("rowCount", rowCount);
		}
		return mav;
	}
	
	@RequestMapping(value = "/clearRG25", method = { RequestMethod.POST })
	public ModelAndView clearRG25(HttpServletRequest request, HttpServletResponse response) {
		logger.info("Entering RemarksGradeController : clearRG25");
		return stepRG25(request, response);
	}
	
	//space for /stepRG3
	@RequestMapping(value = "/stepRG3", method = { RequestMethod.GET })
	public ModelAndView stepRG3(HttpServletRequest request, HttpServletResponse response) {
		logger.info("Entering stepRG3");
		ModelAndView mav = null;
		Map<String, String> consumerTypeMap = null;
		Map<String,String> programStructureMap = null;
		Map<String,String> programMap = null;
		try {
			if (!checkSession(request, response)) {
				redirectToPortalApp(response);
			} else {
				
				consumerTypeMap = prepareMapConsumerType();
				programStructureMap = prepareMapProgramStructure();
				programMap = prepareMapProgram();
				request.setAttribute("consumerTypeMap", consumerTypeMap);
				request.setAttribute("programStructureMap", programStructureMap);
				request.setAttribute("programMap", programMap);
				request.setAttribute("semList", semList);
				request.setAttribute("yearList", yearList);
				request.setAttribute("monthList", EXAM_MONTH_LIST);
				request.setAttribute("acadYearList", ACAD_YEAR_LIST);
				request.setAttribute("acadMonthList", ACAD_MONTH_LIST);
				RemarksGradeBean fileBean = new RemarksGradeBean();
				mav = new ModelAndView("remarksgrade/rg3");
				mav.addObject("remarksGradeBean",fileBean);
			}
		} catch(Exception e) {
			logger.error("RemarksGradeController : stepRG3 : " + e.getMessage());
			
			request.setAttribute(KEY_ERROR, e.getMessage());
		}
		return mav;
	}
	
	@RequestMapping(value = "/searchRG3", method = { RequestMethod.POST })
	public ModelAndView searchRG3(HttpServletRequest request, HttpServletResponse response,
			@ModelAttribute RemarksGradeBean remarksGradeBean) {
		logger.info("Entering searchRG3");
		ModelAndView mav = null;
		int rowCount = 0;
		List<RemarksGradeBean> datalist = null;
		Map<String, String> consumerTypeMap = null;
		Map<String, String> programStructureMap = null;
		Map<String, String> programMap = null;
		try {
			if (!checkSession(request, response)) {
				redirectToPortalApp(response);
			} else {
				logger.info(
						"RemarksGradeController : searchRG3 : (year,month,acadYear,acadMonth,sapid,sem,studentType,programStructure,program,subject,request,response) "
								+ "(" + remarksGradeBean.getYear() + "," + remarksGradeBean.getMonth()
								+ remarksGradeBean.getAcadYear() + "," + remarksGradeBean.getAcadMonth() + ","
								+ remarksGradeBean.getSapid() + "," + remarksGradeBean.getSem() + ","
								+ remarksGradeBean.getStudentTypeId() + "," + remarksGradeBean.getProgramStructureId()
								+ "," + remarksGradeBean.getProgramId() + "," + remarksGradeBean.getSubject() + ","
								+ request + "," + response + ")");
				request.removeAttribute(KEY_SUCCESS);
				request.removeAttribute(KEY_ERROR);
				
				consumerTypeMap = prepareMapConsumerType();
				programStructureMap = prepareMapProgramStructure();
				programMap = prepareMapProgram();
				request.setAttribute("consumerTypeMap", consumerTypeMap);
				request.setAttribute("programStructureMap", programStructureMap);
				request.setAttribute("programMap", programMap);
				request.setAttribute("semList", semList);
				request.setAttribute("yearList", yearList);
				request.setAttribute("monthList", EXAM_MONTH_LIST);
				request.setAttribute("acadYearList", ACAD_YEAR_LIST);
				request.setAttribute("acadMonthList", ACAD_MONTH_LIST);
				//RemarksGradeBean fileBean = new RemarksGradeBean();
				mav = new ModelAndView("remarksgrade/rg3");
				mav.addObject("remarksGradeBean", remarksGradeBean);
				mav.addObject("acadyear", remarksGradeBean.getAcadYear());
				mav.addObject("acadmonth", remarksGradeBean.getAcadMonth());
				mav.addObject("eyear", remarksGradeBean.getYear());
				mav.addObject("emonth", remarksGradeBean.getMonth());
				mav.addObject("sem", remarksGradeBean.getSem());
				mav.addObject("sapid", remarksGradeBean.getSapid());
				mav.addObject("studentTypeId", remarksGradeBean.getStudentTypeId());
				mav.addObject("programStructureId", remarksGradeBean.getProgramStructureId());
				mav.addObject("programId", remarksGradeBean.getProgramId());
				
				gradingTypeServiceInterface = productGradingFactory.getProductGradingType(
						ProductGradingFactoryInterface.PRODUCT_UG, ProductGradingFactoryInterface.GRADING_REMARK);
				datalist = gradingTypeServiceInterface.searchForAbsentStudents(remarksGradeBean);
				if(null != datalist && !datalist.isEmpty()) {
					rowCount = datalist.size();
					request.setAttribute(KEY_SUCCESS, "Total Rows Found : "+rowCount);
				} else {
					request.setAttribute(KEY_SUCCESS, "No Rows Found!");
				}
				//mav.addObject("rowCount", rowCount);
				//mav.addObject("dataList", datalist);
			}
		} catch (Exception e) {
			logger.error("RemarksGradeController : searchRG3 : " + e.getMessage());
			
			request.setAttribute(KEY_ERROR, e.getMessage());
		} finally {
			mav.addObject("rowCount", rowCount);
			mav.addObject("dataList", datalist);
		}
		return mav;
	}
	
	@RequestMapping(value = "/downloadRG3", method = { RequestMethod.GET })
	public ModelAndView downloadRG3(HttpServletRequest request, HttpServletResponse response,
			@RequestParam String acadyear, @RequestParam String acadmonth, @RequestParam String eyear,
			@RequestParam String emonth, @RequestParam String sem, @RequestParam String sapid,
			@RequestParam String studentTypeId, @RequestParam String programStructureId,
			@RequestParam String programId) {
		logger.info("Entering downloadRG3");
		ModelAndView mav = null;
		RemarksGradeBean bean = null;
		List<RemarksGradeBean> list1 = null;
		try {
			if (!checkSession(request, response)) {
				redirectToPortalApp(response);
			} else {
				logger.info(
						"RemarksGradeController : downloadRG3 : (year,month,acadYear,acadMonth,sapid,sem,studentType,programStructure,program,request,response) "
								+ "(" + eyear + "," + emonth + "," + acadyear + "," + acadmonth + "," + sapid + ","
								+ sem + "," + studentTypeId + "," + programStructureId + "," + programId + "," + request
								+ "," + response + ")");
				bean = new RemarksGradeBean(null, eyear, emonth, sapid, sem, null, null, null, null);
				bean.setAcadYear(acadyear);
				bean.setAcadMonth(acadmonth);
				bean.setStudentTypeId(studentTypeId);
				bean.setProgramStructureId(programStructureId);
				bean.setProgramId(programId);

				list1 = gradingTypeServiceInterface.downloadAbsentStudents(bean);
				if (null != list1 && !list1.isEmpty()) {
					logger.info("RemarksGradeController : downloadRG3 : Total to be Exported : " + list1.size());
				}
				mav = new ModelAndView(downloadRemarksGradeAbsentReport, "rgAbsentList", list1);
			}
		} catch (Exception e) {
			logger.error("RemarksGradeController : downloadRG3 : " + e.getMessage());
			
			request.setAttribute(KEY_ERROR, e.getMessage());
		}
		return mav;
	}						
	
	@RequestMapping(value = "/moveRG3", method = { RequestMethod.POST })
	public ModelAndView moveRG3(HttpServletRequest request, HttpServletResponse response,
			@ModelAttribute RemarksGradeBean remarksGradeBean) {
		logger.info("Entering moveRG3");
		ModelAndView mav = null;
		int rowCount = 0;
		String userId = null;
		RemarksGradeBean resultBean = null;
		try {
			if (!checkSession(request, response)) {
				redirectToPortalApp(response);
			} else {
				logger.info(
						"RemarksGradeController : moveRG3 : (year,month,acadYear,acadMonth,sapid,sem,studentType,programStructure,program,subject,request,response) "
								+ "(" + remarksGradeBean.getYear() + "," + remarksGradeBean.getMonth() + ","
								+ remarksGradeBean.getAcadYear() + "," + remarksGradeBean.getAcadMonth() + ","
								+ remarksGradeBean.getSapid() + "," + remarksGradeBean.getSem() + ","
								+ remarksGradeBean.getStudentTypeId() + "," + remarksGradeBean.getProgramStructureId()
								+ "," + remarksGradeBean.getProgramId() + "," + remarksGradeBean.getSubject() + ","
								+ request + "," + response + ")");
				request.removeAttribute(KEY_SUCCESS);
				request.removeAttribute(KEY_ERROR);
				
				
				request.setAttribute("yearList", yearList);
				request.setAttribute("monthList", EXAM_MONTH_LIST);
				request.setAttribute("acadYearList", ACAD_YEAR_LIST);
				request.setAttribute("acadMonthList", ACAD_MONTH_LIST);
				//RemarksGradeBean fileBean = new RemarksGradeBean();
				mav = new ModelAndView("remarksgrade/rg3");
				mav.addObject("remarksGradeBean", remarksGradeBean);
				
				userId = (String) request.getSession().getAttribute("userId");
				gradingTypeServiceInterface = productGradingFactory.getProductGradingType(
						ProductGradingFactoryInterface.PRODUCT_UG, ProductGradingFactoryInterface.GRADING_REMARK);
				resultBean = gradingTypeServiceInterface.moveAbsentStudents(remarksGradeBean, userId);
				request.setAttribute(resultBean.getStatus(), resultBean.getMessage());
			}
		} catch (Exception e) {
			logger.error("RemarksGradeController : moveRG3 : " + e.getMessage());
			
			request.setAttribute(KEY_ERROR, e.getMessage());
		} finally {
			mav.addObject("rowCount", rowCount);
		}
		return mav;
	}
	
	@RequestMapping(value = "/clearRG3", method = { RequestMethod.POST })
	public ModelAndView clearRG3(HttpServletRequest request, HttpServletResponse response) {
		logger.info("Entering RemarksGradeController : clearRG3");
		return stepRG3(request, response);
	}
	
	//step 4 - RIA/NV
	@RequestMapping(value = "/stepRG4", method = { RequestMethod.GET })
	public ModelAndView stepRG4(HttpServletRequest request, HttpServletResponse response) {
		logger.info("Entering stepRG4");
		ModelAndView mav = null;
		Map<String, String> consumerTypeMap = null;
		Map<String,String> programStructureMap = null;
		Map<String,String> programMap = null;
		try {
			if (!checkSession(request, response)) {
				redirectToPortalApp(response);
			} else {
				
				consumerTypeMap = prepareMapConsumerType();
				programStructureMap = prepareMapProgramStructure();
				programMap = prepareMapProgram();
				request.setAttribute("consumerTypeMap", consumerTypeMap);
				request.setAttribute("programStructureMap", programStructureMap);
				request.setAttribute("programMap", programMap);
				request.setAttribute("semList", semList);
				request.setAttribute("yearList", yearList);
				request.setAttribute("monthList", EXAM_MONTH_LIST);
				RemarksGradeBean fileBean = new RemarksGradeBean();
				mav = new ModelAndView("remarksgrade/rg4");
				mav.addObject("remarksGradeBean",fileBean);
			}
		} catch(Exception e) {
			logger.error("RemarksGradeController : stepRG4 : " + e.getMessage());
			
			request.setAttribute(KEY_ERROR, e.getMessage());
		}
		return mav;
	}
	
	@RequestMapping(value = "/searchRG4", method = { RequestMethod.POST })
	public ModelAndView searchRG4(HttpServletRequest request, HttpServletResponse response,
			@ModelAttribute RemarksGradeBean remarksGradeBean) {
		logger.info("Entering searchRG4");
		ModelAndView mav = null;
		int rowCount = 0;
		List<RemarksGradeBean> datalist = null;
		Map<String, String> consumerTypeMap = null;
		Map<String, String> programStructureMap = null;
		Map<String, String> programMap = null;
		try {
			if (!checkSession(request, response)) {
				redirectToPortalApp(response);
			} else {
				logger.info(
						"RemarksGradeController : searchRG4 : (year,month,sapid,sem,studentType,programStructure,program,subject,request,response) "
								+ "(" + remarksGradeBean.getYear() + "," + remarksGradeBean.getMonth() + "," + remarksGradeBean.getSapid() + "," + remarksGradeBean.getSem() + "," + remarksGradeBean.getStudentTypeId()
								+ "," + remarksGradeBean.getProgramStructureId() + "," + remarksGradeBean.getProgramId() + "," + remarksGradeBean.getSubject() + "," + request + ","
								+ response + ")");
				request.removeAttribute(KEY_SUCCESS);
				request.removeAttribute(KEY_ERROR);
				
				consumerTypeMap = prepareMapConsumerType();
				programStructureMap = prepareMapProgramStructure();
				programMap = prepareMapProgram();
				request.setAttribute("consumerTypeMap", consumerTypeMap);
				request.setAttribute("programStructureMap", programStructureMap);
				request.setAttribute("programMap", programMap);
				request.setAttribute("semList", semList);
				request.setAttribute("yearList", yearList);
				request.setAttribute("monthList", EXAM_MONTH_LIST);
				//RemarksGradeBean fileBean = new RemarksGradeBean();
				mav = new ModelAndView("remarksgrade/rg4");
				mav.addObject("remarksGradeBean", remarksGradeBean);
				
				gradingTypeServiceInterface = productGradingFactory.getProductGradingType(
						ProductGradingFactoryInterface.PRODUCT_UG, ProductGradingFactoryInterface.GRADING_REMARK);
				datalist = gradingTypeServiceInterface.searchForStudentMarksStatus(remarksGradeBean);
				if(null != datalist && !datalist.isEmpty()) {
					rowCount = datalist.size();
					request.setAttribute(KEY_SUCCESS, "Total Rows Found : "+rowCount);
				} else {
					request.setAttribute(KEY_SUCCESS, "No Rows Found!");
				}
				//mav.addObject("rowCount", rowCount);
				//mav.addObject("dataList", datalist);
			}
		} catch (Exception e) {
			logger.error("RemarksGradeController : searchRG4 : " + e.getMessage());
			
			request.setAttribute(KEY_ERROR, e.getMessage());
		} finally {
			mav.addObject("rowCount", rowCount);
			mav.addObject("dataList", datalist);
		}
		return mav;
	}
	
	@RequestMapping(value = "/singleUpdateRIANV", method = { RequestMethod.POST })
	public ResponseEntity<HashMap<String, String>> singleUpdateRIANV(@RequestParam String status,
			@RequestParam String year, @RequestParam String month, @RequestParam String sapid, @RequestParam String sem,
			@RequestParam String studentType, @RequestParam String programStructure, @RequestParam String program,
			@RequestParam String subject, HttpServletRequest request, HttpServletResponse response) {
		Boolean isUpdated = Boolean.FALSE;
		String userId = null;
		RemarksGradeBean bean = null;
		HashMap<String, String> responseMap = null;
		try {
			if (!checkSession(request, response)) {
				redirectToPortalApp(response);
			} else {
				logger.info(
						"RemarksGradeController : singleUpdateRIANV : (status,year,month,sapid,sem,studentType,programStructure,program,subject,request,response) "
								+ "(" + status + "," + year + "," + month + "," + sapid + "," + sem + "," + studentType
								+ "," + programStructure + "," + program + "," + subject + "," + request + ","
								+ response + ")");
				bean = new RemarksGradeBean(status, year, month, sapid, sem, studentType, programStructure, program,
						subject);
				
				userId = (String) request.getSession().getAttribute("userId");
				gradingTypeServiceInterface = productGradingFactory.getProductGradingType(
						ProductGradingFactoryInterface.PRODUCT_UG, ProductGradingFactoryInterface.GRADING_REMARK);
				isUpdated = gradingTypeServiceInterface.updateForStudentMarksStatus(bean, userId);
				//responseMap = new HashMap<String, String>();
			}
		} catch (Exception e) {
			logger.error("RemarksGradeController : singleUpdateRIANV : " + e.getMessage());
			
			// request.setAttribute(KEY_ERROR, e.getMessage());
		} finally {
			responseMap = new HashMap<String, String>();
			if (isUpdated) {
				responseMap.put("Status", "Success");
				responseMap.put("marksStatus", status);
				request.removeAttribute(KEY_SUCCESS);
				request.removeAttribute(KEY_ERROR);
			} else {
				responseMap.put("Status", "Fail");
				responseMap.put("marksStatus", "Unable to Update status.");
				request.removeAttribute(KEY_SUCCESS);
				request.removeAttribute(KEY_ERROR);
			}
		}
		return new ResponseEntity(responseMap, HttpStatus.OK);
	}
	
	@RequestMapping(value = "/multipleUpdateRIANV", method = { RequestMethod.POST })
	public ResponseEntity<HashMap<String, String>> multipleUpdateRIANV(@RequestParam String status,
			@RequestParam String year, @RequestParam String month, @RequestParam String sapid, @RequestParam String sem,
			@RequestParam String studentType, @RequestParam String programStructure, @RequestParam String program,
			@RequestParam String subject, HttpServletRequest request, HttpServletResponse response) {
		Boolean isUpdated = Boolean.FALSE;
		String userId = null;
		RemarksGradeBean bean = null;
		HashMap<String, String> responseMap = null;
		try {
			if (!checkSession(request, response)) {
				redirectToPortalApp(response);
			} else {
				logger.info(
						"RemarksGradeController : multipleUpdateRIANV : (status,year,month,sapid,sem,studentType,programStructure,program,subject,request,response) "
								+ "(" + status + "," + year + "," + month + "," + sapid + "," + sem + "," + studentType
								+ "," + programStructure + "," + program + "," + subject + "," + request + ","
								+ response + ")");
				bean = new RemarksGradeBean(status, year, month, sapid, sem, null, null, null,
						subject);
				bean.setStudentTypeId(studentType);
				bean.setProgramStructureId(programStructure);
				bean.setProgramId(program);
				
				userId = (String) request.getSession().getAttribute("userId");
				gradingTypeServiceInterface = productGradingFactory.getProductGradingType(
						ProductGradingFactoryInterface.PRODUCT_UG, ProductGradingFactoryInterface.GRADING_REMARK);
				isUpdated = gradingTypeServiceInterface.updateForStudentMarksStatus(bean, userId);
				//responseMap = new HashMap<String, String>();
			}
		} catch (Exception e) {
			logger.error("RemarksGradeController : multipleUpdateRIANV : " + e.getMessage());
			
			request.setAttribute(KEY_ERROR, e.getMessage());
		} finally {
			responseMap = new HashMap<String, String>();
			if (isUpdated) {
				responseMap.put("Status", "Success");
				responseMap.put("marksStatus", status);
				request.removeAttribute(KEY_SUCCESS);
				request.removeAttribute(KEY_ERROR);
			} else {
				responseMap.put("Status", "Fail");
				responseMap.put("marksStatus", "Unable to Update status.");
				request.removeAttribute(KEY_SUCCESS);
				request.removeAttribute(KEY_ERROR);
			}
		}
		return new ResponseEntity(responseMap, HttpStatus.OK);
	}
	
	//step 5 - PF summary
	@RequestMapping(value = "/stepRG5", method = { RequestMethod.GET })
	public ModelAndView stepRG5(HttpServletRequest request, HttpServletResponse response) {
		logger.info("Entering stepRG5");
		ModelAndView mav = null;
		Map<String, String> consumerTypeMap = null;
		Map<String, String> programStructureMap = null;
		Map<String, String> programMap = null;

		try {
			if (!checkSession(request, response)) {
				redirectToPortalApp(response);
			} else {

				consumerTypeMap = prepareMapConsumerType();
				programStructureMap = prepareMapProgramStructure();
				programMap = prepareMapProgram();
				request.setAttribute("consumerTypeMap", consumerTypeMap);
				request.setAttribute("programStructureMap", programStructureMap);
				request.setAttribute("programMap", programMap);
				request.setAttribute("yearList", yearList);
				request.setAttribute("monthList", EXAM_MONTH_LIST);
				RemarksGradeBean fileBean = new RemarksGradeBean();
				mav = new ModelAndView("remarksgrade/rg5");
				mav.addObject("remarksGradeBean", fileBean);
			}
		} catch (Exception e) {
			logger.error("RemarksGradeController : stepRG5 : " + e.getMessage());
			
			request.setAttribute(KEY_ERROR, e.getMessage());
		}
		return mav;
	}
	
	@RequestMapping(value = "/searchRG5", method = { RequestMethod.POST })
	public ModelAndView searchRG5(HttpServletRequest request, HttpServletResponse response,
			@ModelAttribute RemarksGradeBean remarksGradeBean) {
		logger.info("Entering searchRG5");
		ModelAndView mav = null;
		int rowCount = 0;
		Map<String, Integer> summaryMap = null;
		Map<String, String> consumerTypeMap = null;
		Map<String, String> programStructureMap = null;
		Map<String, String> programMap = null;
		try {
			if (!checkSession(request, response)) {
				redirectToPortalApp(response);
			} else {
				logger.info(
						"RemarksGradeController : searchRG5 : (year,month,studentType,programStructure,program,request,response) "
								+ "(" + remarksGradeBean.getYear() + "," + remarksGradeBean.getMonth() + ","
								+ remarksGradeBean.getStudentTypeId() + "," + remarksGradeBean.getProgramStructureId()
								+ "," + remarksGradeBean.getProgramId() + "," + request + "," + response + ")");
				
				request.removeAttribute(KEY_SUCCESS);
				request.removeAttribute(KEY_ERROR);
				consumerTypeMap = prepareMapConsumerType();
				programStructureMap = prepareMapProgramStructure();
				programMap = prepareMapProgram();
				request.setAttribute("consumerTypeMap", consumerTypeMap);
				request.setAttribute("programStructureMap", programStructureMap);
				request.setAttribute("programMap", programMap);
				request.setAttribute("yearList", yearList);
				request.setAttribute("monthList", EXAM_MONTH_LIST);
				mav = new ModelAndView("remarksgrade/rg5");
				mav.addObject("remarksGradeBean", remarksGradeBean);
				
				gradingTypeServiceInterface = productGradingFactory.getProductGradingType(
						ProductGradingFactoryInterface.PRODUCT_UG, ProductGradingFactoryInterface.GRADING_REMARK);
				summaryMap = gradingTypeServiceInterface.fetchStudentSummary(remarksGradeBean);
				if(null != summaryMap && !summaryMap.isEmpty()) {
					rowCount = summaryMap.size();
				} else {
					request.setAttribute(KEY_SUCCESS, "No Summary Found!");
				}
				//mav.addObject("rowCount", rowCount);
				//mav.addObject("summaryMap", summaryMap);
			}
		} catch (Exception e) {
			logger.error("RemarksGradeController : searchRG5 : " + e.getMessage());
			
			request.setAttribute(KEY_ERROR, e.getMessage());
		} finally {
			mav.addObject("rowCount", rowCount);
			mav.addObject("summaryMap", summaryMap);
		}
		return mav;
	}
	
	@RequestMapping(value = "/processRG5", method = { RequestMethod.POST })
	public ModelAndView processRG5(HttpServletRequest request, HttpServletResponse response,
			@ModelAttribute RemarksGradeBean remarksGradeBean) {
		ModelAndView mav = null;
		String userId = null;
		RemarksGradeBean bean = null;
		int rowCount = 0;
		Map<String, Integer> summaryMap = null;
		Map<String, String> consumerTypeMap = null;
		Map<String, String> programStructureMap = null;
		Map<String, String> programMap = null;
		try {
			if (!checkSession(request, response)) {
				redirectToPortalApp(response);
			} else {
				
				logger.info(
						"RemarksGradeController : processRG5 : (year,month,studentType,programStructure,program,request,response) "
								+ "(" + remarksGradeBean.getYear() + "," + remarksGradeBean.getMonth() + "," + remarksGradeBean.getStudentTypeId() + "," + remarksGradeBean.getProgramStructureId() + ","
								+ remarksGradeBean.getProgramId() + "," + request + "," + response + ")");
				
				request.removeAttribute(KEY_SUCCESS);
				request.removeAttribute(KEY_ERROR);
				consumerTypeMap = prepareMapConsumerType();
				programStructureMap = prepareMapProgramStructure();
				programMap = prepareMapProgram();
				request.setAttribute("consumerTypeMap", consumerTypeMap);
				request.setAttribute("programStructureMap", programStructureMap);
				request.setAttribute("programMap", programMap);
				request.setAttribute("yearList", yearList);
				request.setAttribute("monthList", EXAM_MONTH_LIST);
				mav = new ModelAndView("remarksgrade/rg5");
				mav.addObject("remarksGradeBean", remarksGradeBean);
				mav.addObject("rowCount", rowCount);
				
				bean = new RemarksGradeBean(null, remarksGradeBean.getYear(), remarksGradeBean.getMonth(), null, null, null, null, null,
						null);
				bean.setStudentTypeId(remarksGradeBean.getStudentTypeId());
				bean.setProgramStructureId(remarksGradeBean.getProgramStructureId());
				bean.setProgramId(remarksGradeBean.getProgramId());
				userId = (String) request.getSession().getAttribute("userId");
				gradingTypeServiceInterface = productGradingFactory.getProductGradingType(
						ProductGradingFactoryInterface.PRODUCT_UG, ProductGradingFactoryInterface.GRADING_REMARK);
				summaryMap = gradingTypeServiceInterface.processStudentSummary(bean, userId);
			}
		} catch (Exception e) {
			logger.error("RemarksGradeController : processRG5 : " + e.getMessage());
			
			request.setAttribute(KEY_ERROR, e.getMessage());
		} finally {
			if(null != summaryMap && !summaryMap.isEmpty()) {
				String msg = "Successfully Records Processed ";
				Entry<String, Integer> etObj = null;
				Set<Entry<String, Integer>> stObj = summaryMap.entrySet();
				Iterator<Entry<String, Integer>> itObj = stObj.iterator();
				while(itObj.hasNext()) {
					etObj = itObj.next();
					msg += "(" + etObj.getKey() +  "," + etObj.getValue() + ") ";
				}
				request.setAttribute(KEY_SUCCESS, msg);
			}
		}
		return mav;
	}
	
	//step 66 - absolute grading
	@RequestMapping(value = "/stepRG66", method = { RequestMethod.GET })
	public ModelAndView stepRG66(HttpServletRequest request, HttpServletResponse response) {
		logger.info("Entering stepRG66");
		ModelAndView mav = null;
		Map<String,String> consumerTypeMap = null;
		Map<String,String> programStructureMap = null;
		Map<String,String> programMap = null;
		Map<Integer,String> subjectCodeIdMap = null;
		try {
			if (!checkSession(request, response)) {
				redirectToPortalApp(response);
			} else {
				request.removeAttribute(KEY_SUCCESS);
				request.removeAttribute(KEY_ERROR);
				
				consumerTypeMap = prepareMapConsumerType();
				programStructureMap = prepareMapProgramStructure();
				programMap = prepareMapProgram();
				subjectCodeIdMap = fetchSubjectCodeIdMap();
				request.setAttribute("consumerTypeMap", consumerTypeMap);
				request.setAttribute("programStructureMap", programStructureMap);
				request.setAttribute("programMap", programMap);
				request.setAttribute("subjectCodeIdMap", subjectCodeIdMap);
				request.setAttribute("yearList", yearList);
				request.setAttribute("monthList", EXAM_MONTH_LIST);
				RemarksGradeBean fileBean = new RemarksGradeBean();
				mav = new ModelAndView("remarksgrade/rg66");
				mav.addObject("remarksGradeBean", fileBean);
			}
		} catch (Exception e) {
			logger.error("RemarksGradeController : stepRG66 : " + e.getMessage());
			
			request.setAttribute(KEY_ERROR, e.getMessage());
		}
		return mav;
	}
	
	Map<Integer, String> fetchSubjectCodeIdMap() {
		String tempSubjName = null;
		Map<Integer, String> mapSubjectCodeId = null;
		List<MDMSubjectCodeBean> listSC = mdmSubjectCodeDAO.fetchMDMSubjectCodeList(1);
		mapSubjectCodeId = new LinkedHashMap<Integer, String>();
		for(int j = 0; j < listSC.size(); j++) {
			tempSubjName = listSC.get(j).getSubjectname()+ " ("+listSC.get(j).getSubjectcode() + ")";
			mapSubjectCodeId.put(listSC.get(j).getId(), tempSubjName);
		}
		return mapSubjectCodeId;
	}
	
	@RequestMapping(value = "/searchProcessRG66", method = { RequestMethod.POST })
	public ModelAndView searchProcessRG66(HttpServletRequest request, HttpServletResponse response,
			@ModelAttribute RemarksGradeBean remarksGradeBean) {
		logger.info("Entering searchProcessRG66");
		ModelAndView mav = null;
		Integer rowsProcessed = 0;
		String userId = null;
		Map<String, String> consumerTypeMap = null;
		Map<String, String> programStructureMap = null;
		Map<String, String> programMap = null;
		Map<Integer,String> subjectCodeIdMap = null;
		try {
			if (!checkSession(request, response)) {
				redirectToPortalApp(response);
			} else {
				logger.info(
						"RemarksGradeController : searchProcessRG66 : (year,month,sapid,studentType,programStructure,program,subject,request,response) "
								+ "(" + remarksGradeBean.getYear() + "," + remarksGradeBean.getMonth() + "," + remarksGradeBean.getSapid() + "," + remarksGradeBean.getStudentTypeId()
								+ "," + remarksGradeBean.getProgramStructureId() + "," + remarksGradeBean.getProgramId() + "," + remarksGradeBean.getSubject() + "," + request + ","
								+ response + ")");
				request.removeAttribute(KEY_SUCCESS);
				request.removeAttribute(KEY_ERROR);
				
				consumerTypeMap = prepareMapConsumerType();
				programStructureMap = prepareMapProgramStructure();
				programMap = prepareMapProgram();
				subjectCodeIdMap = fetchSubjectCodeIdMap();
				request.setAttribute("consumerTypeMap", consumerTypeMap);
				request.setAttribute("programStructureMap", programStructureMap);
				request.setAttribute("programMap", programMap);
				request.setAttribute("subjectCodeIdMap", subjectCodeIdMap);
				request.setAttribute("yearList", yearList);
				request.setAttribute("monthList", EXAM_MONTH_LIST);
				//RemarksGradeBean fileBean = new RemarksGradeBean();
				mav = new ModelAndView("remarksgrade/rg66");
				mav.addObject("remarksGradeBean", remarksGradeBean);
				
				userId = (String) request.getSession().getAttribute("userId");
				gradingTypeServiceInterface = productGradingFactory.getProductGradingType(
						ProductGradingFactoryInterface.PRODUCT_UG, ProductGradingFactoryInterface.GRADING_REMARK);
				rowsProcessed = gradingTypeServiceInterface.searchProcessStudentForGrade(remarksGradeBean, userId);
				//request.setAttribute(KEY_SUCCESS, "Total Rows Processed : "+rowsProcessed);
			}
		} catch (Exception e) {
			logger.error("RemarksGradeController : searchProcessRG66 : " + e.getMessage());
			
			request.setAttribute(KEY_ERROR, e.getMessage());
		} finally {
			request.setAttribute(KEY_SUCCESS, "Total Rows Processed : "+rowsProcessed);
		}
		return mav;
	}
	
	//step 7 - transfer from staging table to main table
	@RequestMapping(value = "/stepRG7", method = { RequestMethod.GET })
	public ModelAndView stepRG7(HttpServletRequest request, HttpServletResponse response) {
		logger.info("Entering stepRG7");
		ModelAndView mav = null;
		try {
			if (!checkSession(request, response)) {
				redirectToPortalApp(response);
			} else {
				request.setAttribute("yearList", yearList);
				request.setAttribute("monthList", EXAM_MONTH_LIST);
				RemarksGradeBean fileBean = new RemarksGradeBean();
				mav = new ModelAndView("remarksgrade/rg7");
				mav.addObject("remarksGradeBean", fileBean);
			}
		} catch (Exception e) {
			logger.error("RemarksGradeController : stepRG7 : " + e.getMessage());
			
			request.setAttribute(KEY_ERROR, e.getMessage());
		}
		return mav;
	}
	
	@RequestMapping(value = "/searchRG7", method = { RequestMethod.POST })
	public ModelAndView searchRG7(HttpServletRequest request, HttpServletResponse response,
			@ModelAttribute RemarksGradeBean remarksGradeBean) {
		logger.info("Entering searchRG7");
		ModelAndView mav = null;
		int rowCount = 0;
		List<RemarksGradeBean> list1 = null;
		try {
			if (!checkSession(request, response)) {
				redirectToPortalApp(response);
			} else {
				logger.info("RemarksGradeController : searchRG7 : (year,month,request,response) " + "("
						+ remarksGradeBean.getYear() + "," + remarksGradeBean.getMonth() + "," + request + ","
						+ response + ")");
				request.removeAttribute(KEY_SUCCESS);
				request.removeAttribute(KEY_ERROR);
				request.setAttribute("yearList", yearList);
				request.setAttribute("monthList", EXAM_MONTH_LIST);
				//RemarksGradeBean fileBean = new RemarksGradeBean();
				mav = new ModelAndView("remarksgrade/rg7");
				mav.addObject("remarksGradeBean", remarksGradeBean);
				mav.addObject("syear", remarksGradeBean.getYear());
				mav.addObject("smonth", remarksGradeBean.getMonth());
				
				gradingTypeServiceInterface = productGradingFactory.getProductGradingType(
						ProductGradingFactoryInterface.PRODUCT_UG, ProductGradingFactoryInterface.GRADING_REMARK);
				list1 = gradingTypeServiceInterface.searchStudentsForTransfer(remarksGradeBean);
				if (null != list1 && !list1.isEmpty()) {
					rowCount = list1.size();
				}
				//mav.addObject("rowCount", rowCount);
				//mav.addObject("dataList", list1);
			}
		} catch (Exception e) {
			logger.error("RemarksGradeController : searchRG7 : " + e.getMessage());
			
			request.setAttribute(KEY_ERROR, e.getMessage());
		} finally {
			request.setAttribute(KEY_SUCCESS, "Total Rows found : "+rowCount);
			mav.addObject("rowCount", rowCount);
			mav.addObject("dataList", list1);
		}
		return mav;
	}
	
	@RequestMapping(value = "/downloadRG7", method = { RequestMethod.GET })
	public ModelAndView downloadRG7(HttpServletRequest request, HttpServletResponse response,
			@RequestParam String syear, @RequestParam String smonth) {
		logger.info("Entering downloadRG7");
		ModelAndView mav = null;
		RemarksGradeBean bean = null;
		List<RemarksGradeBean> list1 = null;
		try {
			if (!checkSession(request, response)) {
				redirectToPortalApp(response);
			} else {
				logger.info("RemarksGradeController : downloadRG7 : (year,month,request,response) " + "(" + syear + ","
						+ smonth + "," + request + "," + response + ")");
				bean = new RemarksGradeBean(null, syear, smonth, null, null, null, null, null, null);

				list1 = gradingTypeServiceInterface.downloadStudentsForTransfer(bean);
				if (null != list1 && !list1.isEmpty()) {
					logger.info("RemarksGradeController :  downloadRG7 : Total to be Exported : " + list1.size());
				}
				mav = new ModelAndView("downloadRemarksGradedPFReport", "studentGradedForTransferList", list1);
			}
		} catch (Exception e) {
			logger.error("RemarksGradeController : downloadRG7 : " + e.getMessage());
			
			request.setAttribute(KEY_ERROR, e.getMessage());
		}
		return mav;
	}
	
	@RequestMapping(value = "/transferRG7", method = { RequestMethod.POST })
	public ModelAndView transferRG7(HttpServletRequest request, HttpServletResponse response,
			@ModelAttribute RemarksGradeBean remarksGradeBean) {
		logger.info("Entering transferRG7");
		ModelAndView mav = null;
		int rowCount = 0;
		String userId = null;
		try {
			if (!checkSession(request, response)) {
				redirectToPortalApp(response);
			} else {
				logger.info("RemarksGradeController : transferRG7 : (year,month,request,response) " + "("
						+ remarksGradeBean.getYear() + "," + remarksGradeBean.getMonth() + "," + request + ","
						+ response + ")");
				request.removeAttribute(KEY_SUCCESS);
				request.removeAttribute(KEY_ERROR);
				request.setAttribute("yearList", yearList);
				request.setAttribute("monthList", EXAM_MONTH_LIST);
				//RemarksGradeBean fileBean = new RemarksGradeBean();
				mav = new ModelAndView("remarksgrade/rg7");
				mav.addObject("remarksGradeBean", remarksGradeBean);
				mav.addObject("rowCount", 0);
				
				userId = (String) request.getSession().getAttribute("userId");
				gradingTypeServiceInterface = productGradingFactory.getProductGradingType(
						ProductGradingFactoryInterface.PRODUCT_UG, ProductGradingFactoryInterface.GRADING_REMARK);
				rowCount = gradingTypeServiceInterface.searchTransferStudents(remarksGradeBean, userId);
				//request.setAttribute(KEY_SUCCESS, "Total Rows transferred : "+rowCount);
			}
		} catch (Exception e) {
			logger.error("RemarksGradeController : transferRG7 : " + e.getMessage());
			
			request.setAttribute(KEY_ERROR, e.getMessage());
		} finally {
			request.setAttribute(KEY_SUCCESS, "Total Rows transferred : "+rowCount);
		}
		return mav;
	}
	
	@RequestMapping(value = "/stepRG8", method = { RequestMethod.GET })
	public ModelAndView stepRG8(HttpServletRequest request, HttpServletResponse response) {
		logger.info("Entering stepRG8");
		int rowCount = 0;
		ModelAndView mav = null;
		try {
			if (!checkSession(request, response)) {
				redirectToPortalApp(response);
			} else {
				request.setAttribute("yearList", yearList);
				request.setAttribute("monthList", EXAM_MONTH_LIST);
				RemarksGradeBean fileBean = new RemarksGradeBean();
				mav = new ModelAndView("remarksgrade/rg8");
				mav.addObject("remarksGradeBean", fileBean);
				mav.addObject("rowCount", rowCount);
			}
		} catch (Exception e) {
			logger.error("RemarksGradeController : stepRG8 : " + e.getMessage());
			
			request.setAttribute(KEY_ERROR, e.getMessage());
		}
		return mav;
	}
	
	@RequestMapping(value = "/changeResultsLiveRG8", method = { RequestMethod.POST })
	public ModelAndView changeResultsLiveRG8(HttpServletRequest request, HttpServletResponse response,
			@ModelAttribute RemarksGradeBean remarksGradeBean) {
		logger.info("Entering changeResultsLiveRG8");
		ModelAndView mav = null;
		String userId = null;
		int rowCount = 1;
		Boolean isSuccess = Boolean.FALSE;
		try {
			if (!checkSession(request, response)) {
				redirectToPortalApp(response);
			} else {
				logger.info(
						"RemarksGradeController : changeResultsLiveRG8 : (year, month, resultLive, request, response) "
								+ "(" + remarksGradeBean.getYear() + "," + remarksGradeBean.getMonth() + ","
								+ remarksGradeBean.getAssignmentMarksLive() + "," + request + "," + response + ")");
				request.removeAttribute(KEY_SUCCESS);
				request.removeAttribute(KEY_ERROR);

				request.setAttribute("yearList", yearList);
				request.setAttribute("monthList", EXAM_MONTH_LIST);
				// RemarksGradeBean fileBean = new RemarksGradeBean();

				userId = (String) request.getSession().getAttribute("userId");
				gradingTypeServiceInterface = productGradingFactory.getProductGradingType(
						ProductGradingFactoryInterface.PRODUCT_UG, ProductGradingFactoryInterface.GRADING_REMARK);
				isSuccess = gradingTypeServiceInterface.changeResultsLiveState(remarksGradeBean, userId);
			}
		} catch (Exception e) {
			logger.error("RemarksGradeController : changeResultsLiveRG8 : " + e.getMessage());
			
			request.setAttribute(KEY_ERROR, e.getMessage());
		} finally {
			mav = new ModelAndView("remarksgrade/rg8");
			mav.addObject("remarksGradeBean", remarksGradeBean);
			mav.addObject("rowCount", rowCount);
			mav.addObject("isSuccess", isSuccess);
			if(KEY_SUCCESS.equals(remarksGradeBean.getStatus())) {
				request.setAttribute(KEY_SUCCESS, remarksGradeBean.getMessage());
			} else {
				request.setAttribute(KEY_ERROR, remarksGradeBean.getMessage());
			}
		}
		return mav;
	}
	
	//step 9 - passfail report
	@RequestMapping(value = "/stepRG9", method = { RequestMethod.GET })
	public ModelAndView stepRG9(HttpServletRequest request, HttpServletResponse response) {
		logger.info("Entering stepRG9");
		ModelAndView mav = null;
		Map<String, String> consumerTypeMap = null;
		Map<String, String> programStructureMap = null;
		Map<String, String> programMap = null;
		Map<String, String> centerCodeNameMap = null;
		try {
			if (!checkSession(request, response)) {
				redirectToPortalApp(response);
			} else {
				consumerTypeMap = prepareMapConsumerType();
				programStructureMap = prepareMapProgramStructure();
				programMap = prepareMapProgram();
				centerCodeNameMap = fetchCenterCodeNameMap();
				request.setAttribute("consumerTypeMap", consumerTypeMap);
				request.setAttribute("programStructureMap", programStructureMap);
				request.setAttribute("programMap", programMap);
				request.setAttribute("centerCodeNameMap", centerCodeNameMap);
				request.setAttribute("semList", semList);
				request.setAttribute("yearList", yearList);
				request.setAttribute("monthList", EXAM_MONTH_LIST);
				RemarksGradeBean fileBean = new RemarksGradeBean();
				mav = new ModelAndView("remarksgrade/rg9");
				mav.addObject("remarksGradeBean", fileBean);
			}
		} catch (Exception e) {
			logger.error("RemarksGradeController : stepRG9 : " + e.getMessage());
			
			request.setAttribute(KEY_ERROR, e.getMessage());
		}
		return mav;
	}
	
	@RequestMapping(value = "/searchRG9", method = { RequestMethod.POST })
	public ModelAndView searchRG9(HttpServletRequest request, HttpServletResponse response,
			@ModelAttribute RemarksGradeBean remarksGradeBean) {
		logger.info("Entering searchRG9");
		String message = null;
		ModelAndView mav = null;
		Map<String, String> consumerTypeMap = null;
		Map<String, String> programStructureMap = null;
		Map<String, String> programMap = null;
		Map<String, String> centerCodeNameMap = null;
		List<RemarksGradeBean> list1 = null;
		RemarksGradeBean remarksGradeBeanObj = null;
		
		try {
			if (!checkSession(request, response)) {
				redirectToPortalApp(response);
			} else {
				logger.info("RemarksGradeController : searchRG9 : (year,month,request,response) " + "("
						+ remarksGradeBean.getYear() + "," + remarksGradeBean.getMonth() + "," + request + ","
						+ response + ")");
				request.removeAttribute(KEY_SUCCESS);
				request.removeAttribute(KEY_ERROR);
				consumerTypeMap = prepareMapConsumerType();
				programStructureMap = prepareMapProgramStructure();
				programMap = prepareMapProgram();
				centerCodeNameMap = fetchCenterCodeNameMap();
				request.setAttribute("consumerTypeMap", consumerTypeMap);
				request.setAttribute("programStructureMap", programStructureMap);
				request.setAttribute("programMap", programMap);
				request.setAttribute("centerCodeNameMap", centerCodeNameMap);
				request.setAttribute("semList", semList);
				request.setAttribute("yearList", yearList);
				request.setAttribute("monthList", EXAM_MONTH_LIST);
				//RemarksGradeBean fileBean = new RemarksGradeBean();
				mav = new ModelAndView("remarksgrade/rg9");
				mav.addObject("remarksGradeBean", remarksGradeBean);
				
				gradingTypeServiceInterface = productGradingFactory.getProductGradingType(
						ProductGradingFactoryInterface.PRODUCT_UG, ProductGradingFactoryInterface.GRADING_REMARK);
				list1 = gradingTypeServiceInterface.searchResultsAsPassFailReport(remarksGradeBean, Boolean.TRUE);
				if (null != list1 && !list1.isEmpty()) {
					remarksGradeBeanObj = list1.get(0);
					message = remarksGradeBeanObj.getMessage();
				}
			}
		} catch (Exception e) {
			logger.error("RemarksGradeController : searchRG9 : " + e.getMessage());
			
			request.setAttribute(KEY_ERROR, e.getMessage());
		} finally {
			request.setAttribute(KEY_SUCCESS, message);
		}
		return mav;
	}
	
	@RequestMapping(value = "/downloadRG9", method = { RequestMethod.POST })
	public ModelAndView downloadRG9(HttpServletRequest request, HttpServletResponse response,
			@ModelAttribute RemarksGradeBean remarksGradeBean) {
		logger.info("Entering downloadRG9");
		ModelAndView mav = null;
		//RemarksGradeBean bean = null;
		List<RemarksGradeBean> list1 = null;
		try {
			if (!checkSession(request, response)) {
				redirectToPortalApp(response);
			} else {

				list1 = gradingTypeServiceInterface.downloadResultsAsPassFailReport(remarksGradeBean);
				if (null != list1 && !list1.isEmpty()) {
					logger.info("RemarksGradeController :  downloadRG9 : Total to be Exported : " + list1.size());
				}
				mav = new ModelAndView("downloadRemarksGradedPFResultsReport", "pfResultsList", list1);
			}
		} catch (Exception e) {
			logger.error("RemarksGradeController : downloadRG9 : " + e.getMessage());
			
			request.setAttribute(KEY_ERROR, e.getMessage());
		}
		return mav;
	}
	
	@RequestMapping(value = "/stepRG15", method = { RequestMethod.GET })
	public ModelAndView stepRG15(HttpServletRequest request, HttpServletResponse response) {
		logger.info("Entering stepRG15");
		ModelAndView mav = null;
		try {
			if (!checkSession(request, response)) {
				redirectToPortalApp(response);
			} else {
				
				request.setAttribute("semList", semList);
				request.setAttribute("yearList", yearList);
				request.setAttribute("monthList", EXAM_MONTH_LIST);
				request.setAttribute("acadYearList", ACAD_YEAR_LIST);
				request.setAttribute("acadMonthList", ACAD_MONTH_LIST);
				RemarksGradeBean fileBean = new RemarksGradeBean();
				fileBean.setAcadYear(CURRENT_ACAD_YEAR);
				fileBean.setAcadMonth(CURRENT_ACAD_MONTH);
				mav = new ModelAndView("remarksgrade/rg15");
				mav.addObject("remarksGradeBean",fileBean);
			}
		} catch(Exception e) {
			logger.error("RemarksGradeController : stepRG15 : " + e.getMessage());
			
			request.setAttribute(KEY_ERROR, e.getMessage());
		}
		return mav;
	}
	
	@RequestMapping(value = "/searchRG15AS", method = { RequestMethod.POST })
	public ModelAndView searchRG15AS(HttpServletRequest request, HttpServletResponse response,
			@ModelAttribute RemarksGradeBean remarksGradeBean) {
		logger.info("Entering searchRG15AS");
		ModelAndView mav = null;
		int rowCount = 0;
		List<RemarksGradeBean> datalist = null;
		try {
			if (!checkSession(request, response)) {
				redirectToPortalApp(response);
			} else {
				remarksGradeBean.setAssignmentType(RemarksGradeBean.ASSIGNMENT_SUBMITTED);
				remarksGradeBean.setAcadYear(CURRENT_ACAD_YEAR);
				remarksGradeBean.setAcadMonth(CURRENT_ACAD_MONTH);
				
				logger.info(
						"RemarksGradeController : searchRG15AS : (year,month,acadYear,acadMonth,assgType,request,response) "
								+ "(" + remarksGradeBean.getYear() + "," + remarksGradeBean.getMonth() + ","
								+ remarksGradeBean.getAcadYear() + "," + remarksGradeBean.getAcadMonth() + ","
								+ remarksGradeBean.getAssignmentType() + "," + request + "," + response + ")");
				request.removeAttribute(KEY_SUCCESS);
				request.removeAttribute(KEY_ERROR);
				
				request.setAttribute("semList", semList);
				request.setAttribute("yearList", yearList);
				request.setAttribute("monthList", EXAM_MONTH_LIST);
				request.setAttribute("acadYearList", ACAD_YEAR_LIST);
				request.setAttribute("acadMonthList", ACAD_MONTH_LIST);
				//RemarksGradeBean fileBean = new RemarksGradeBean();
				mav = new ModelAndView("remarksgrade/rg15");
				mav.addObject("remarksGradeBean", remarksGradeBean);
				mav.addObject("acadyear", remarksGradeBean.getAcadYear());
				mav.addObject("acadmonth", remarksGradeBean.getAcadMonth());
				mav.addObject("eyear", remarksGradeBean.getYear());
				mav.addObject("emonth", remarksGradeBean.getMonth());
				mav.addObject("assgtype", remarksGradeBean.getAssignmentType());
				
				gradingTypeServiceInterface = productGradingFactory.getProductGradingType(
						ProductGradingFactoryInterface.PRODUCT_UG, ProductGradingFactoryInterface.GRADING_REMARK);
				datalist = gradingTypeServiceInterface.searchStudentsForAssignments(remarksGradeBean);
				if(null != datalist && !datalist.isEmpty()) {
					rowCount = datalist.size();
					request.setAttribute(KEY_SUCCESS, "Total Rows Found : "+rowCount);
				} else {
					request.setAttribute(KEY_SUCCESS, "No Rows Found!");
				}
			}
		} catch (Exception e) {
			logger.error("RemarksGradeController : searchRG15AS : " + e.getMessage());
			
			request.setAttribute(KEY_ERROR, e.getMessage());
		} finally {
			mav.addObject("rowCount", rowCount);
			mav.addObject("dataList", datalist);
		}
		return mav;
	}
	
	@RequestMapping(value = "/searchRG15ANS", method = { RequestMethod.POST })
	public ModelAndView searchRG15ANS(HttpServletRequest request, HttpServletResponse response,
			@ModelAttribute RemarksGradeBean remarksGradeBean) {
		logger.info("Entering searchRG15ANS");
		ModelAndView mav = null;
		int rowCount = 0;
		List<RemarksGradeBean> datalist = null;
		try {
			if (!checkSession(request, response)) {
				redirectToPortalApp(response);
			} else {
				remarksGradeBean.setAssignmentType(RemarksGradeBean.ASSIGNMENT_NOT_SUBMITTED);
				remarksGradeBean.setAcadYear(CURRENT_ACAD_YEAR);
				remarksGradeBean.setAcadMonth(CURRENT_ACAD_MONTH);
				
				logger.info(
						"RemarksGradeController : searchRG15ANS : (year,month,acadYear,acadMonth,assgType,request,response) "
								+ "(" + remarksGradeBean.getYear() + "," + remarksGradeBean.getMonth() + ","
								+ remarksGradeBean.getAcadYear() + "," + remarksGradeBean.getAcadMonth() + ","
								+ remarksGradeBean.getAssignmentType() + "," + request + "," + response + ")");
				request.removeAttribute(KEY_SUCCESS);
				request.removeAttribute(KEY_ERROR);
				
				request.setAttribute("semList", semList);
				request.setAttribute("yearList", yearList);
				request.setAttribute("monthList", EXAM_MONTH_LIST);
				request.setAttribute("acadYearList", ACAD_YEAR_LIST);
				request.setAttribute("acadMonthList", ACAD_MONTH_LIST);
				//RemarksGradeBean fileBean = new RemarksGradeBean();
				mav = new ModelAndView("remarksgrade/rg15");
				mav.addObject("remarksGradeBean", remarksGradeBean);
				mav.addObject("acadyear", remarksGradeBean.getAcadYear());
				mav.addObject("acadmonth", remarksGradeBean.getAcadMonth());
				mav.addObject("eyear", remarksGradeBean.getYear());
				mav.addObject("emonth", remarksGradeBean.getMonth());
				mav.addObject("assgtype", remarksGradeBean.getAssignmentType());
				
				gradingTypeServiceInterface = productGradingFactory.getProductGradingType(
						ProductGradingFactoryInterface.PRODUCT_UG, ProductGradingFactoryInterface.GRADING_REMARK);
				datalist = gradingTypeServiceInterface.searchStudentsForAssignments(remarksGradeBean);
				if(null != datalist && !datalist.isEmpty()) {
					rowCount = datalist.size();
					request.setAttribute(KEY_SUCCESS, "Total Rows Found : "+rowCount);
				} else {
					request.setAttribute(KEY_SUCCESS, "No Rows Found!");
				}
			}
		} catch (Exception e) {
			logger.error("RemarksGradeController : searchRG15ANS : " + e.getMessage());
			
			request.setAttribute(KEY_ERROR, e.getMessage());
		} finally {
			mav.addObject("rowCount", rowCount);
			mav.addObject("dataList", datalist);
		}
		return mav;
	}
	
	@RequestMapping(value = "/downloadRG15", method = { RequestMethod.GET })
	public ModelAndView downloadRG15(HttpServletRequest request, HttpServletResponse response,
			@RequestParam String acadyear, @RequestParam String acadmonth, @RequestParam String eyear,
			@RequestParam String emonth, @RequestParam String assgtype) {
		logger.info("Entering downloadRG15");
		ModelAndView mav = null;
		RemarksGradeBean bean = null;
		List<RemarksGradeBean> list1 = null;
		try {
			if (!checkSession(request, response)) {
				redirectToPortalApp(response);
			} else {
				logger.info(
						"RemarksGradeController : downloadRG15 : (year,month,acadYear,acadMonth,assgtype,request,response) "
								+ "(" + eyear + "," + emonth + "," + acadyear + "," + acadmonth + "," + assgtype + ","
								+ request + "," + response + ")");
				bean = new RemarksGradeBean();
				bean.setYear(eyear);
				bean.setMonth(emonth);
				bean.setAcadYear(acadyear);
				bean.setAcadMonth(acadmonth);
				bean.setAssignmentType(assgtype);

				list1 = gradingTypeServiceInterface.downloadStudentsForAssignments(bean);
				if (null != list1 && !list1.isEmpty()) {
					logger.info("RemarksGradeController : downloadRG15 : Total to be Exported : " + list1.size());
				}
				mav = new ModelAndView(downloadRemarksGradeEligibleStudentsReport, "rgEligibleStudentsList", list1);
			}
		} catch (Exception e) {
			logger.error("RemarksGradeController : downloadRG15 : " + e.getMessage());

			request.setAttribute(KEY_ERROR, e.getMessage());
		}
		return mav;
	}
	
	@RequestMapping(value = "/clearRG15", method = { RequestMethod.POST })
	public ModelAndView clearRG15(HttpServletRequest request, HttpServletResponse response) {
		logger.info("Entering RemarksGradeController : clearRG15");
		return stepRG15(request, response);
	}
	
	public Map<String,String> fetchCenterCodeNameMap() {
		CenterExamBean cBean = null;
		List<CenterExamBean> centers = null;
		HashMap<String, String> centerCodeNameMap = null;
		
		centers = studentMarksDAO.getAllCenters();
		centerCodeNameMap = new HashMap<String, String>();
		for (int i = 0; i < centers.size(); i++) {
			cBean = centers.get(i);
			centerCodeNameMap.put(cBean.getCenterCode(), cBean.getCenterName());
		}
		
		return centerCodeNameMap;
	}
	
	public Map<String,String> prepareMapConsumerType() {
		Map<String,String> consumerTypeMap = null;
		List<ConsumerProgramStructureExam> listConsumerType = null;
		
		listConsumerType = fetchConsumerTypeList();
		consumerTypeMap = new LinkedHashMap<String, String>();
		for(ConsumerProgramStructureExam bean : listConsumerType) {
			consumerTypeMap.put(bean.getId(), bean.getName());
		}
		return consumerTypeMap;
	}
	
	protected List<ConsumerProgramStructureExam> fetchConsumerTypeList() {
		logger.info("Entering RemarksGradeController : fetchConsumerTypeList");
		List<ConsumerProgramStructureExam> listCPS = null;
		listCPS = asignmentsDAO.getConsumerTypeList();
		return listCPS;
	}
	
	public Map<String,String> prepareMapProgramStructure() {
		Map<String,String> programStructureMap = null;
		List<ProgramStructureBean> listConsumerType = null;
		
		listConsumerType = fetchProgramStructureList();
		programStructureMap = new LinkedHashMap<String, String>();
		for(ProgramStructureBean bean : listConsumerType) {
			programStructureMap.put(bean.getId(), bean.getProgram_structure());
		}
		return programStructureMap;
	}
	
	protected List<ProgramStructureBean> fetchProgramStructureList() {
		logger.info("Entering RemarksGradeController : fetchProgramStructureList");
		List<ProgramStructureBean> listPS = null;
		listPS = dashboardDAO.getProgramStructureList();
		return listPS;
	}
	
	public Map<String,String> prepareMapProgram() {
		Map<String,String> programMap = null;
		List<ConsumerProgramStructureExam> listProgram = null;
		
		listProgram = fetchProgramList();
		programMap = new LinkedHashMap<String, String>();
		for(ConsumerProgramStructureExam bean : listProgram) {
			programMap.put(bean.getId(), bean.getCode());
		}
		return programMap;
	}
	
	protected List<ConsumerProgramStructureExam> fetchProgramList() {
		logger.info("Entering RemarksGradeController : fetchProgramList");
		List<ConsumerProgramStructureExam> listCPS = null;
		listCPS = dashboardDAO.getProgramList();
		return listCPS;
	}
}
