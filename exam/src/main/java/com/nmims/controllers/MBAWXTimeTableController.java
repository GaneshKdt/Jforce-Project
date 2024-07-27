package com.nmims.controllers;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import com.nmims.beans.MBATimeTableBean;
import com.nmims.daos.MBAWXTimeTableDAO;
import com.nmims.helpers.ExcelHelper;

@Controller
@RequestMapping("/admin")
public class MBAWXTimeTableController extends BaseController {

	@Autowired
	private MBAWXTimeTableDAO timeTableDAO;
	
	@RequestMapping(value="/uploadTimeTableForm_MBAWX",method=RequestMethod.GET)
	public ModelAndView addTimeTableForm(HttpServletRequest request,HttpServletResponse response) {
		if(!checkSession(request, response)){
			redirectToPortalApp(response);
			return null;
		}
		MBATimeTableBean timeTableBean = new MBATimeTableBean();
		ModelAndView mv = new ModelAndView("MBA_WX_Exam_Booking/timetable/uploadTimeTable");
		mv.addObject("fileBean", timeTableBean);
		mv.addObject("timeTableList", timeTableDAO.getTimeTableList());
		return mv;
	}
	
	@RequestMapping(value="/uploadTimeTable_MBAWX",method=RequestMethod.POST)
	public ModelAndView addTimeTable(MBATimeTableBean input , HttpServletRequest request,HttpServletResponse response) {
		if(!checkSession(request, response)){
			redirectToPortalApp(response);
			return null;
		}
		
		String userId = (String)request.getSession().getAttribute("userId");
		
		input.setCreatedBy(userId);
		input.setLastModifiedBy(userId);
		
		ModelAndView mv = new ModelAndView("MBA_WX_Exam_Booking/timetable/uploadTimeTable");
		
		ExcelHelper excelHelper = new ExcelHelper();
		ArrayList<MBATimeTableBean> timeTableBeans = null;
		try {
			timeTableBeans = excelHelper.addExcelMBATimeTable(input);
		} catch (IOException e) {
			request.setAttribute("error","true");
			mv.addObject("errorMessage","Unexpected Error while reading file : " + e.getMessage());
			return mv;
		}

		List<MBATimeTableBean> errorList = new ArrayList<MBATimeTableBean>();
		List<MBATimeTableBean> successList = new ArrayList<MBATimeTableBean>();
		
		if(timeTableBeans == null || timeTableBeans.size() == 0) {
			request.setAttribute("error","true");
			mv.addObject("errorMessage","No data or Empty file found");
			
			mv.addObject("timeTableBean", input);
			mv.addObject("timeTableList", timeTableDAO.getTimeTableList());
			return mv;
		}

		// Get and add timebound ids to these subjects
		for (MBATimeTableBean bean : timeTableBeans) {

			Long programSemSubjectId = timeTableDAO.getProgramSemSubjectId(bean);

			bean.setProgramSemSubjectId(programSemSubjectId);
			
			String error = performChecksBeforeInsert(bean);
			
			if(StringUtils.isBlank(error)) {
				successList.add(bean);
			} else {
				bean.setError(error);
				errorList.add(bean);
			}
		}
		request.getSession().setAttribute("timeTableBeansList_MBAWX", successList);
		mv.addObject("successList", successList);
		mv.addObject("errorList", errorList);

		mv.addObject("showApproveButton", successList.size() > 0);
		mv.addObject("isApprove", true);
		
		mv.addObject("fileBean", input);
		mv.addObject("timeTableList", timeTableDAO.getTimeTableList());
		return mv;
	}



	@RequestMapping(value="/approveUploadTimeTable_MBAWX")
	public ModelAndView approveUploadTimeTables(HttpServletRequest request,HttpServletResponse response) {
		if(!checkSession(request, response)){
			redirectToPortalApp(response);
			return null;
		}

		ModelAndView mv = new ModelAndView("MBA_WX_Exam_Booking/timetable/uploadTimeTable");
		
		List<MBATimeTableBean> timeTableBeansList = (List<MBATimeTableBean>) request.getSession().getAttribute("timeTableBeansList_MBAWX");

		if(timeTableBeansList != null && timeTableBeansList.size() == 0) {
			request.setAttribute("error","true");
			request.setAttribute("errorMessage","No data to upload!");
		} else {
			try {
				List<MBATimeTableBean> insertResults = timeTableDAO.batchInsertTimeTables(timeTableBeansList);

				List<MBATimeTableBean> successfulInserts = new ArrayList<MBATimeTableBean>();
				List<MBATimeTableBean> failedInserts = new ArrayList<MBATimeTableBean>();
				for (MBATimeTableBean insertResult : insertResults) {
					if(StringUtils.isBlank(insertResult.getError())) {
						successfulInserts.add(insertResult);
					} else {
						failedInserts.add(insertResult);
					}
				}
				mv.addObject("successList", successfulInserts);
				mv.addObject("errorList", failedInserts);
			} catch (Exception e) {
				
				request.setAttribute("error","true");
				request.setAttribute("errorMessage","Failed to create record, " + e.getMessage());
			}
		}
		mv.addObject("fileBean", new MBATimeTableBean());
		mv.addObject("timeTableList", timeTableDAO.getTimeTableList());
		return mv;
	}
	

	@RequestMapping(value="/updateTimeTableForm_MBAWX")
	public ModelAndView editTimeTablesForm(@ModelAttribute MBATimeTableBean searchBean, HttpServletRequest request, HttpServletResponse response) {
		if(!checkSession(request, response)){
			redirectToPortalApp(response);
			return null;
		}

		ModelAndView mv = new ModelAndView("MBA_WX_Exam_Booking/timetable/updateTimeTable");
		MBATimeTableBean timeTableBean = null;
		
		
		if(searchBean != null && searchBean.getTimeTableId() != null) {
			try {
				timeTableBean = timeTableDAO.getTimeTableById(searchBean.getTimeTableId());
			}catch (Exception e) {
				
				timeTableBean = new MBATimeTableBean();
				request.setAttribute("error", "true");
				mv.addObject("errorMessage", "Error retrieving time table details!");
			}
		} else {
			timeTableBean = new MBATimeTableBean();
			request.setAttribute("error", "true");
			mv.addObject("errorMessage", "No time table id!");
		}

		mv.addObject("timeTableBean", timeTableBean);
		
		return mv;
	}
	
	@RequestMapping(value="/updateTimeTable_MBAWX" , method=RequestMethod.POST)
	public ModelAndView editTimeTables(@ModelAttribute MBATimeTableBean timeTableBean, HttpServletRequest request,HttpServletResponse response) {
		if(!checkSession(request, response)){
			redirectToPortalApp(response);
			return null;
		}
		
		ModelAndView mv = new ModelAndView("MBA_WX_Exam_Booking/timetable/updateTimeTable");

		mv.addObject("timeTableBean", timeTableBean);
		
		if(timeTableBean == null) {
			timeTableBean = new MBATimeTableBean();
			request.setAttribute("error", "true");
			mv.addObject("errorMessage", "Error!");
			return mv;
		}
		// check fields
		if(timeTableBean.getTimeTableId() == null) {
			request.setAttribute("error", "true");
			mv.addObject("errorMessage", "Error!");
			return mv;
		}
		String error = checkTimeTableBean(timeTableBean);
		if(error != null) {
			request.setAttribute("error", "true");
			mv.addObject("errorMessage", error);
			return mv;
		}
		
		try {
			String userId = (String)request.getSession().getAttribute("userId");
			timeTableBean.setLastModifiedBy(userId);

			SimpleDateFormat dateTimeFormatter = new SimpleDateFormat("yyyy-MM-dd HH:mm");
			Date examStartDateTime = dateTimeFormatter.parse(timeTableBean.getExamDate() + " " + timeTableBean.getExamStartTime());
			Date examEndDateTime = dateTimeFormatter.parse(timeTableBean.getExamDate() + " " + timeTableBean.getExamEndTime());
			
			timeTableBean.setExamStartDateTime(examStartDateTime);
			timeTableBean.setExamEndDateTime(examEndDateTime);
			
			timeTableDAO.updateTimeTable(timeTableBean);
			mv.addObject("success", "true");
			mv.addObject("successMessage", "Time Table Updated Successfully!");
			mv.addObject("timeTableBean", timeTableDAO.getTimeTableById(timeTableBean.getTimeTableId()));
		}catch (Exception e) {
			
			request.setAttribute("error", "true");
			mv.addObject("errorMessage", "Error updating time table details : " + e.getMessage());
		}
		
		return mv;
	}
	
	@RequestMapping(value="/deleteTimeTable_MBAWX",method=RequestMethod.POST)
	public @ResponseBody HashMap<String, String> deleteTimeTableEntry(HttpServletRequest request,HttpServletResponse response) {
		HashMap<String, String> responseData = new HashMap<String,String>();
		if(!checkSession(request, response)){
//			redirectToPortalApp(response);
			responseData.put("status", "error");
			responseData.put("message", "Session Expired");
			return responseData;
		}
		responseData.put("status", "success");
		String timeTableId = request.getParameter("timeTableId");
		if(timeTableId == null) {
			responseData.put("status", "error");
			responseData.put("message", "Invalid Time Table Id found");
			return responseData;
		}
		String status = timeTableDAO.deleteTimeTableById(timeTableId);
		if(status.indexOf("Successfully record deleted,") != -1) {		
			responseData.put("status", "success");
			responseData.put("message", status);
			return responseData;
		}else {
			responseData.put("status", "error");
			responseData.put("message", status);
			return responseData;
		}
	}

	private String performChecksBeforeInsert(MBATimeTableBean bean) {

		if(bean.getProgramSemSubjectId() == null) {
			return "Couldn't find the course mapping for term/subject/month/year!";
		}

		// check for duplicate timebound/starttime/endtime
		boolean timeTableExists = timeTableDAO.checkIfTimeTableExists(bean);
		
		if(timeTableExists) {
			return "Entry already exists for Subject / Term / Batch / Start Time / End Time";
		}
		if(StringUtils.isEmpty(bean.getSubjectName())) {
			return "Name cannot be empty!";
		} else if(bean.getExamEndDateTime() == null) {
			return "Address cannot be empty!";
		} else if(bean.getExamStartDateTime() == null) {
			return "City cannot be empty!";
		}
		return null;
	}
	
	private String checkTimeTableBean(MBATimeTableBean bean) {
		if(bean.getExamDate() == null) {
			return "Date cannot be empty!";
		} else if(bean.getExamEndTime() == null) {
			return "End Time cannot be empty!";
		} else if(bean.getExamStartTime() == null) {
			return "Start cannot be empty!";
		}
		
		return null;
	}
}
