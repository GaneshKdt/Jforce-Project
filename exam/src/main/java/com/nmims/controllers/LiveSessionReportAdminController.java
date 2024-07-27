/**
 * 
 */
package com.nmims.controllers;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

import com.nmims.beans.ConsumerProgramStructureExam;
import com.nmims.beans.LiveSessionReportAdminBean;
import com.nmims.beans.LiveSessionReportAdminDTO;
import com.nmims.beans.RemarksGradeBean;
import com.nmims.daos.AssignmentsDAO;
import com.nmims.interfaces.LiveSessionReportAdminClientServiceInterface;
import com.nmims.interfaces.ProductGradingFactoryInterface;

/**
 * @author vil_m   
 *
 */
@Controller
@RequestMapping("/admin")
public class LiveSessionReportAdminController extends BaseController {
	
	private static final String KEY_ERROR = "error";
	private static final String KEY_SUCCESS = "success";
	
	@Autowired(required = false)
	ApplicationContext act;
	
	@Autowired
	AssignmentsDAO asignmentsDAO;
	
	@Value("#{'${ACAD_MONTH_LIST}'.split(',')}")
	private List<String> ACAD_MONTH_LIST;
	
	@Autowired
	private LiveSessionReportAdminClientServiceInterface liveSessionReportAdminClientService;
	
	private static final List<String> yearList;
	
	public static final String CONSUMER_TYPE_RETAIL = "RETAIL";
	
	public static final Logger logger = LoggerFactory.getLogger(LiveSessionReportAdminController.class);
	
	static {
		yearList = new ArrayList<String>(Arrays.asList( 
				"2020","2021","2022","2023","2024","2025","2026"));
	}

	public LiveSessionReportAdminController() {
		super();
		// TODO Auto-generated constructor stub
	}
	
	@RequestMapping(value = "/displayLiveSessionReport", method = { RequestMethod.GET })
	public ModelAndView displayLiveSessionReportAdmin(HttpServletRequest request, HttpServletResponse response) {
		logger.info("Entering LiveSessionReportAdminController : displayLiveSessionReportAdmin");
		int rowCount = 0;
		Map<String,String> consumerTypeMap = null;
		ModelAndView mav = null;
		String consumerTypeId = null;
		if (!checkSession(request, response)) {
			redirectToPortalApp(response);
		} else {
			consumerTypeMap = prepareMapConsumerType();
			consumerTypeId = extractConsumerTypeId(consumerTypeMap, CONSUMER_TYPE_RETAIL);
			
			logger.info("Center codes >> " + getAuthorizedCodes(request));
			request.setAttribute("consumerTypeMap", consumerTypeMap);
			request.setAttribute("yearList", yearList);
			request.setAttribute("monthList", ACAD_MONTH_LIST);
			LiveSessionReportAdminDTO dtoObj = new LiveSessionReportAdminDTO();
			dtoObj.setStudentType(consumerTypeId);
			mav = new ModelAndView("report/liveSessionReportAdmin");
			mav.addObject("liveSessionReportAdminDTO",dtoObj);
			mav.addObject("rowCount", rowCount);
		}
		return mav;
	}
	
	@RequestMapping(value = "/fetchLiveSessionReport", method = { RequestMethod.POST })
	public ModelAndView fetchLiveSessionReportAdmin(HttpServletRequest request, HttpServletResponse response,
			@ModelAttribute LiveSessionReportAdminDTO dtoObj) {
		logger.info("Entering LiveSessionReportAdminController : fetchLiveSessionReportAdmin");
		int rowCount = 0;
		Map<String, String> consumerTypeMap = null;
		ModelAndView mav = null;
		String consumerTypeId = null;
		List<LiveSessionReportAdminDTO> datalist = null;
		String centerCodes = null;
		List<String> list = null;
		try {
			if (!checkSession(request, response)) {
				redirectToPortalApp(response);
			} else {
				consumerTypeMap = prepareMapConsumerType();
				consumerTypeId = extractConsumerTypeId(consumerTypeMap, CONSUMER_TYPE_RETAIL);
				
				centerCodes = getAuthorizedCodes(request);
				logger.info("Center codes >> " + centerCodes + " isBlank >> "+isBlank(centerCodes));
				request.setAttribute("consumerTypeMap", consumerTypeMap);
				request.setAttribute("yearList", yearList);
				request.setAttribute("monthList", ACAD_MONTH_LIST);
				//LiveSessionReportAdminDTO dtoObj = new LiveSessionReportAdminDTO();
				dtoObj.setStudentType(consumerTypeId);
				mav = new ModelAndView("report/liveSessionReportAdmin");
				mav.addObject("liveSessionReportAdminDTO",dtoObj);
				
				if(!isBlank(centerCodes)) {
					list = stringToList(centerCodes);
				}
				
				datalist = liveSessionReportAdminClientService.fetchLiveSessionReport(dtoObj, list, CONSUMER_TYPE_RETAIL);
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
			logger.error("LiveSessionReportAdminController : fetchLiveSessionReportAdmin : " + e.getMessage());
			
			request.setAttribute(KEY_ERROR, e.getMessage());
		} finally {
			mav.addObject("rowCount", rowCount);
			mav.addObject("dataList", datalist);
		}
		return mav;
	}
	
	@RequestMapping(value = "/downloadLiveSessionReport", method = { RequestMethod.POST })
	public ModelAndView downloadLiveSessionReportAdmin(HttpServletRequest request, HttpServletResponse response,
			@ModelAttribute LiveSessionReportAdminDTO dtoObj) {
		logger.info("Entering LiveSessionReportAdminController : downloadLiveSessionReportAdmin");
		ModelAndView mav = null;
		List<LiveSessionReportAdminDTO> datalist = null;
		String centerCodes = null;
		List<String> list = null;
		try {
			if (!checkSession(request, response)) {
				redirectToPortalApp(response);
			} else {
				centerCodes = getAuthorizedCodes(request);
				logger.info("Center codes >> " + centerCodes + " isBlank >> "+isBlank(centerCodes));
				
				if(!isBlank(centerCodes)) {
					list = stringToList(centerCodes);
				}
				
				datalist = liveSessionReportAdminClientService.fetchLiveSessionReport(dtoObj, list, CONSUMER_TYPE_RETAIL);
				if (null != datalist && !datalist.isEmpty()) {
					logger.info("LiveSessionReportAdminController :  downloadLiveSessionReportAdmin : Total to be Exported : " + datalist.size());
				}
				mav = new ModelAndView("downloadLiveSessionReportAdmin", "datalist", datalist);
			}
		} catch (Exception e) {
			logger.error("LiveSessionReportAdminController : downloadLiveSessionReportAdmin : " + e.getMessage());
			
			request.setAttribute(KEY_ERROR, e.getMessage());
		}
		return mav;
	}
	
	@RequestMapping(value = "/clearScreen", method = { RequestMethod.POST })
	public ModelAndView clearScreen(HttpServletRequest request, HttpServletResponse response) {
		logger.info("Entering LiveSessionReportAdminController : clearScreen");
		return displayLiveSessionReportAdmin(request, response);
	}
	
	/*@Deprecated 
	public static List<String> stringToList(String str) {
		String[] strArr = str.split(",");
		List<String> list1 = Arrays.asList(strArr);
		return list1;
	}*/
	
	protected static List<String> stringToList(String str) {
		String[] strArr = null;
		String[] strArrNew = null;
		String temp = null;
		
		strArr = str.split(",");
		strArrNew = new String[strArr.length];
		for(int i = 0; i < strArr.length; i++) {
			temp = strArr[i];
			temp = temp.substring(1, temp.length() - 1);//removes Quote at first and last position.
			//logger.info("temp_str : "+ temp);
			strArrNew[i] = temp;
		}
		List<String> list1 = Arrays.asList(strArrNew);
		return list1;
	}
	
	public String extractConsumerTypeId(Map<String,String> consumerTypeMap, String consumerTypeName) {
		Set<Entry<String, String>> setCT = null;
		Iterator<Entry<String, String>> iteCT = null;
		Entry<String, String> entry = null;
		String consumerTypeId = null;
		
		setCT = consumerTypeMap.entrySet();
		iteCT = setCT.iterator();
		while(iteCT.hasNext()) {
			entry = iteCT.next();
			if(entry.getValue().equalsIgnoreCase(consumerTypeName)) {
				consumerTypeId = entry.getKey();
				break;
			}
		}
		return consumerTypeId;
	}
	
	public Map<String,String> prepareMapConsumerType() {
		Map<String,String> consumerTypeMap = null;
		List<ConsumerProgramStructureExam> listConsumerType = null;
		
		listConsumerType = fetchConsumerTypeList();
		consumerTypeMap = new LinkedHashMap<String, String>();
		for(ConsumerProgramStructureExam bean : listConsumerType) {
			consumerTypeMap.put(bean.getId(), bean.getName());
		}
		if(null != listConsumerType) {
			listConsumerType.clear();
		}
		return consumerTypeMap;
	}
	
	protected List<ConsumerProgramStructureExam> fetchConsumerTypeList() {
		logger.info("Entering LiveSessionReportAdminController : fetchConsumerTypeList");
		List<ConsumerProgramStructureExam> listCPS = null;
		listCPS = asignmentsDAO.getConsumerTypeList();
		return listCPS;
	}

	public static boolean isBlank(String arg) {
		return StringUtils.isBlank(arg);
	}
}
