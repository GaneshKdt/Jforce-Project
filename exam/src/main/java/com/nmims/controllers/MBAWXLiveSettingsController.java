package com.nmims.controllers;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import com.nmims.beans.MBALiveSettings;
import com.nmims.beans.MBAWXExamRegistrationExtensionBean;
import com.nmims.beans.StudentMarksBean;
import com.nmims.beans.StudentSubjectConfigExamBean;
import com.nmims.daos.ExamBookingDAO;
import com.nmims.daos.MBAWXLiveSettingsDAO;
import com.nmims.helpers.ExcelHelper;

@Controller
@RequestMapping("/admin")
public class MBAWXLiveSettingsController extends BaseController {

	@Autowired
	MBAWXLiveSettingsDAO liveSettingsDAO;

	private List<String> liveSettingTypes = new ArrayList<String>(Arrays.asList(
		"Term De-Registration", "Term Re-Registration", "Exam Re-Registration","Exam Registration", "Hall Ticket", "Subject Repeat MBA - WX", "Subject Repeat", 
		"Specialisation Elective Selection Term-5", "Project Registration", "Project Re-Registration"
    ));
	static private List<String> programStructureList;
	static private List<String> examYearList;
	static private List<String> examMonthList;
	static private List<String> acadYearList;
	static private List<String> acadMonthList;

	private void initialize() {
		List<StudentSubjectConfigExamBean> sscList = liveSettingsDAO.getAllStudentSubjectConfig();
		
		programStructureList = liveSettingsDAO.getAllProgramStructures();

		examYearList = new ArrayList<String>();		
		examMonthList = new ArrayList<String>();
		acadYearList = new ArrayList<String>();
		acadMonthList = new ArrayList<String>();
		
		for (StudentSubjectConfigExamBean ssc : sscList) {
			String examYear = ssc.getExamYear();
			String examMonth = ssc.getExamMonth();
			String acadYear = ssc.getAcadYear();
			String acadMonth = ssc.getAcadMonth();

			if(examYear != null && !examYearList.contains(examYear)) {
				examYearList.add(examYear);
			}
			if(examMonth != null && !examMonthList.contains(examMonth)) {
				examMonthList.add(examMonth);
			}
			if(acadYear != null && !acadYearList.contains(acadYear)) {
				acadYearList.add(acadYear);
			}
			if(acadMonth != null && !acadMonthList.contains(acadMonth)) {
				acadMonthList.add(acadMonth);
			}
		}
	}
	
	
	@RequestMapping(value="/uploadLiveSettingsForm_MBAWX",method=RequestMethod.GET)
	public ModelAndView uploadLiveSettingsForm(HttpServletRequest request,HttpServletResponse response) {
		if(!checkSession(request, response)){
			redirectToPortalApp(response);
			return null;
		}
		ModelAndView mv = new ModelAndView("MBA_WX_Exam_Booking/live_settings/uploadLiveSettings");

		mv.addObject("fileBean", new MBALiveSettings());
		addFieldsToModelViewForUpload(mv);
		
		return mv;
	}
	
	@RequestMapping(value="/uploadLiveSettings_MBAWX",method=RequestMethod.POST)
	public ModelAndView uploadLiveSettings(MBALiveSettings input , HttpServletRequest request,HttpServletResponse response) {
		if(!checkSession(request, response)){
			redirectToPortalApp(response);
			return null;
		}
		String userId = (String) request.getSession().getAttribute("userId");
		ModelAndView mv = new ModelAndView("MBA_WX_Exam_Booking/live_settings/uploadLiveSettings");
		
		ExcelHelper excelHelper = new ExcelHelper();
		List<MBALiveSettings> liveSettingsBeans = excelHelper.addExcelMBALiveSettings(input);

		List<MBALiveSettings> errorList = new ArrayList<MBALiveSettings>();
		List<MBALiveSettings> successList = new ArrayList<MBALiveSettings>();
		
		for (MBALiveSettings liveSettings : liveSettingsBeans) {
			liveSettings.setCreatedBy(userId);
			liveSettings.setLastModifiedBy(userId);
			addLiveSetting(liveSettings, errorList, successList);
		}
		
		mv.addObject("fileBean", input);
		
		mv.addObject("successList", successList);
		mv.addObject("errorList", errorList);		

		addFieldsToModelViewForUpload(mv);
		return mv;
	}
	

	@RequestMapping(value="/addLiveSettings_MBAWX",method=RequestMethod.POST)
	public ModelAndView addLiveSettings(MBALiveSettings input , HttpServletRequest request,HttpServletResponse response) {
		if(!checkSession(request, response)){
			redirectToPortalApp(response);
			return null;
		}
		String userId = (String) request.getSession().getAttribute("userId");
		ModelAndView mv = new ModelAndView("MBA_WX_Exam_Booking/live_settings/uploadLiveSettings");
		
		List<MBALiveSettings> errorList = new ArrayList<MBALiveSettings>();
		List<MBALiveSettings> successList = new ArrayList<MBALiveSettings>();
		
		input.setCreatedBy(userId);
		input.setLastModifiedBy(userId);
		
		addLiveSetting(input, errorList, successList);

		if(successList.size() > 0) {
			mv.addObject("success", "true");
			mv.addObject("successMessage", "Successfully inserted record.");
		} else if (errorList.size() > 0) {
			mv.addObject("error", "true");
			mv.addObject("errorMessage", errorList.get(0).getError());
		}
		
		mv.addObject("fileBean", input);
		
		addFieldsToModelViewForUpload(mv);
		return mv;
	}

	private void addLiveSetting(MBALiveSettings liveSettings, List<MBALiveSettings> errorList,
			List<MBALiveSettings> successList) {
		String programStructure = liveSettings.getProgramStructure();
				String program = liveSettings.getProgram();
				String consumerProgramStructureId = liveSettingsDAO.getConsumerProgramStructure( programStructure, program );
		
		try {
			if(StringUtils.isBlank(consumerProgramStructureId)) {
				liveSettings.setError("No Mapping found for " + programStructure );
				errorList.add(liveSettings);
			} else {
				liveSettings.setConsumerProgramStructureId(consumerProgramStructureId);
				
				setStartEndTime(liveSettings);
				
				try {
					liveSettingsDAO.upsertLiveSetting(liveSettings);
					liveSettingsDAO.upsertLiveSettingHistory(liveSettings);
					successList.add(liveSettings);
				}catch (Exception e) {
					
					liveSettings.setError(e.getMessage());
					errorList.add(liveSettings);
				}
			}
		}catch (Exception e) {
			
			liveSettings.setError(e.getMessage());
			errorList.add(liveSettings);
		}
	}


	@RequestMapping(value="/updateLiveSettingForm_MBAWX")
	public ModelAndView editTimeTablesForm(@ModelAttribute MBALiveSettings searchBean, HttpServletRequest request, HttpServletResponse response) {
		if(!checkSession(request, response)){
			redirectToPortalApp(response);
			return null;
		}

		ModelAndView mv = new ModelAndView("MBA_WX_Exam_Booking/live_settings/updateLiveSettings");
		addFieldsToModelViewForUpdate(mv);
		
		MBALiveSettings liveSettings = new MBALiveSettings();
		if(searchBean != null && searchBean.getId() != null) {
			try {
				liveSettings = liveSettingsDAO.getLiveSettingsById(searchBean.getId());
			}catch (Exception e) {
				
				request.setAttribute("error", "true");
				mv.addObject("errorMessage", "Error retrieving live setting details!");
			}
		} else {
			request.setAttribute("error", "true");
			mv.addObject("errorMessage", "No id!");
		}

		mv.addObject("liveSettings", liveSettings);
		
		return mv;
	}
	
	@RequestMapping(value="/updateLiveSetting_MBAWX" , method=RequestMethod.POST)
	public ModelAndView editTimeTables(@ModelAttribute MBALiveSettings liveSettings, HttpServletRequest request,HttpServletResponse response) {
		if(!checkSession(request, response)){
			redirectToPortalApp(response);
			return null;
		}
		
		ModelAndView mv = new ModelAndView("MBA_WX_Exam_Booking/live_settings/updateLiveSettings");
		addFieldsToModelViewForUpdate(mv);
		mv.addObject("liveSettings", liveSettings);
		
		if(liveSettings == null) {
			liveSettings = new MBALiveSettings();
			request.setAttribute("error", "true");
			mv.addObject("errorMessage", "Error!");
			return mv;
		}
		// check fields
		if(liveSettings.getId() == null) {
			request.setAttribute("error", "true");
			mv.addObject("errorMessage", "Error!");
			return mv;
		}
		String error = checkLiveSettingsBean(liveSettings);
		if(error != null) {
			request.setAttribute("error", "true");
			mv.addObject("errorMessage", error);
			return mv;
		}
		
		try {
			String userId = (String)request.getSession().getAttribute("userId");
			
			liveSettings.setLastModifiedBy(userId);
			liveSettings.setCreatedBy(userId);
			
			List<MBALiveSettings> errorList = new ArrayList<MBALiveSettings>();
			List<MBALiveSettings> successList = new ArrayList<MBALiveSettings>();
			
			addLiveSetting(liveSettings, errorList, successList);

			if(successList.size() > 0) {
				mv.addObject("success", "true");
				mv.addObject("successMessage", "Successfully inserted record.");
			} else if (errorList.size() > 0) {
				mv.addObject("error", "true");
				mv.addObject("errorMessage", errorList.get(0).getError());
			}
			
			mv.addObject("liveSettings", liveSettingsDAO.getLiveSettingsById(liveSettings.getId()));
		}catch (Exception e) {
			
			request.setAttribute("error", "true");
			mv.addObject("errorMessage", "Error updating time table details : " + e.getMessage());
		}
		
		return mv;
	}

	@RequestMapping(value = "/deleteLiveSetting_MBAWX", method = { RequestMethod.POST, RequestMethod.GET }, produces = "application/json; charset=UTF-8")
	public ResponseEntity<Map<String, String>> toggleStudentBooking(HttpServletResponse response, HttpServletRequest request) {
		
		Map<String, String> res = new HashMap<String, String>();
		
		if(!checkSession(request, response)){
			res.put("status", "fail");
			res.put("message", "Log in to continue!!");
			return new ResponseEntity<Map<String, String>>(res, HttpStatus.OK);
		}
		String id = request.getParameter("id");
		String message = liveSettingsDAO.deleteLiveSetting(id);
		res.put("message", message);
		res.put("status", StringUtils.isBlank(message) ? "fail" : "success");
		
		return new ResponseEntity<Map<String, String>>(res, HttpStatus.OK);
	}

	private void setStartEndTime(MBALiveSettings liveSettings) throws ParseException {

		SimpleDateFormat dateTimeFormatter = new SimpleDateFormat("yyyy-MM-dd HH:mm");
		Date startDateTime = dateTimeFormatter.parse(liveSettings.getStartDateStr() + " " + liveSettings.getStartTimeStr());
		Date endDateTime = dateTimeFormatter.parse(liveSettings.getEndDateStr() + " " + liveSettings.getEndTimeStr());

		liveSettings.setStartTime(startDateTime);
		liveSettings.setEndTime(endDateTime);
		
	}
	
	private String checkLiveSettingsBean(MBALiveSettings bean) {
		if(bean.getStartDateStr() == null) {
			return "Start Date cannot be empty!";
		}if(bean.getEndDateStr()  == null) {
			return "End Date cannot be empty!";
		} else if(bean.getEndTimeStr() == null) {
			return "End Time cannot be empty!";
		} else if(bean.getStartTimeStr() == null) {
			return "Start Time cannot be empty!";
		}
		
		return null;
	}

	private void addFieldsToModelViewForUpload(ModelAndView mv) {

		mv.addObject("liveSettingsList", liveSettingsDAO.getMBALiveSettingsList());
		mv.addObject("liveSettingTypes", liveSettingTypes);
		mv.addObject("acadMonthList", getAcadMonthList());
		mv.addObject("acadYearList", getAcadYearList());
		mv.addObject("examMonthList", getExamMonthList());
		mv.addObject("examYearList", getExamYearList());
		mv.addObject("programStructureList", getProgramStructureList());
	}

	private void addFieldsToModelViewForUpdate(ModelAndView mv) {

		mv.addObject("liveSettingTypes", liveSettingTypes);
		mv.addObject("acadMonthList", getAcadMonthList());
		mv.addObject("acadYearList", getAcadYearList());
		mv.addObject("examMonthList", getExamMonthList());
		mv.addObject("examYearList", getExamYearList());
		mv.addObject("programStructureList", getProgramStructureList());
	}
	
	private List<String> getAcadMonthList() {
		if(acadMonthList == null) {
			initialize();
		}
		return acadMonthList;
	}
	
	private List<String> getAcadYearList() {
		if(acadYearList == null) {
			initialize();
		}
		return acadYearList;
	}
	
	private List<String> getExamMonthList() {
		if(examMonthList == null) {
			initialize();
		}
		return examMonthList;
	}
	
	private List<String> getExamYearList() {
		if(examYearList == null) {
			initialize();
		}
		return examYearList;
	}
	
	private List<String> getProgramStructureList() {
		if(programStructureList == null) {
			initialize();
		}
		return programStructureList;
	}

	@RequestMapping(value = "/getExtendExamForTimeboundStudentForm", method = RequestMethod.GET)
	public ModelAndView getExtendExamForTimeboundStudentForm(HttpServletRequest request, HttpServletResponse response) {

		if (!checkSession(request, response)) {
			redirectToPortalApp(response);
			return null;
		}

		ModelAndView mav = new ModelAndView("extendExamForTimeboundStudentForm");

		mav.addObject("userId", request.getSession().getAttribute("userId"));
		mav.addObject("examMonthList", getExamMonthList());
		mav.addObject("examYearList", getExamYearList());

		return mav;
	}

}
