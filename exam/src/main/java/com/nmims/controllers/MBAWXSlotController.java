package com.nmims.controllers;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import com.nmims.beans.MBACentersBean;
import com.nmims.beans.MBASlotBean;
import com.nmims.beans.MBATimeTableBean;
import com.nmims.daos.MBAWXCentersDAO;
import com.nmims.daos.MBAWXSlotDAO;
import com.nmims.daos.MBAWXTimeTableDAO;

@Controller
@RequestMapping("/admin")
public class MBAWXSlotController extends BaseController {

	@Autowired
	MBAWXSlotDAO slotsDAO;
	
	@Autowired
	MBAWXCentersDAO centersDAO;
	
	@Autowired
	MBAWXTimeTableDAO timeTableDAO;

	@RequestMapping(value="/slotsList_MBAWX",method=RequestMethod.GET)
	public ModelAndView showSlotsList(HttpServletRequest request,HttpServletResponse response) {
		if(!checkSession(request, response)){
			redirectToPortalApp(response);
			return null;
		}
		
		ModelAndView mv = new ModelAndView("MBA_WX_Exam_Booking/slots/slotsList");
		mv.addObject("slotsList", slotsDAO.getSlotsList());
		return mv;
	}
	
	@RequestMapping(value="/autoGenerateSlots_MBAWX",method=RequestMethod.GET)
	public ModelAndView autoGenerateSlots(HttpServletRequest request,HttpServletResponse response) {
		if(!checkSession(request, response)){
			redirectToPortalApp(response);
			return null;
		}

		List<MBASlotBean> successList = new ArrayList<MBASlotBean>();
		List<MBASlotBean> errorList = new ArrayList<MBASlotBean>();

		List<MBACentersBean> listOfActiveCenters = centersDAO.getActiveCentersList();
		List<MBATimeTableBean> listOfTimeTables = timeTableDAO.getUpcomingTimeTableList();

		for (MBACentersBean center : listOfActiveCenters) {
			for (MBATimeTableBean timeTable : listOfTimeTables) {
				MBASlotBean slot = new MBASlotBean();
				slot.setCapacity(center.getCapacity());
				slot.setCenterId(center.getCenterId());
				slot.setTimeTableId(timeTable.getTimeTableId());
				slot.setSubjectName(timeTable.getSubjectName());
				slot.setCenterName(center.getName());
				slot.setExamStartDateTime(timeTable.getExamStartDateTime());
				slot.setExamEndDateTime(timeTable.getExamEndDateTime());
				slot.setExamDate(timeTable.getExamDate());
				slot.setExamStartTime(timeTable.getExamStartTime());
				slot.setExamEndTime(timeTable.getExamEndTime());
				slot.setTerm(timeTable.getTerm());
				slot.setActive("Y");

				String userId = (String) request.getSession().getAttribute("userId");
				slot.setLastModifiedBy(userId);
				slot.setCreatedBy(userId);
				
				boolean slotExists = slotsDAO.checkIfSlotExists(slot.getCenterId(), slot.getTimeTableId());
				if(slotExists) {
					slot.setError("Slot already exists!");
					errorList.add(slot);
				} else {
					try {
						slotsDAO.createSlot(slot);
						successList.add(slot);
					}catch (Exception e) {
						
						slot.setError("Error creating slot : " + e.getMessage());
						errorList.add(slot);
					}
				}
			}
		}
		
		ModelAndView mv = new ModelAndView("MBA_WX_Exam_Booking/slots/slotsList");

		mv.addObject("slotsList", slotsDAO.getSlotsList());
		mv.addObject("errorList", errorList);
		mv.addObject("successList", successList);
		
		return mv;
	}
	

	@RequestMapping(value="/updateSlotForm_MBAWX")
	public ModelAndView editTimeTablesForm(@ModelAttribute MBASlotBean searchBean, HttpServletRequest request, HttpServletResponse response) {
		if(!checkSession(request, response)){
			redirectToPortalApp(response);
			return null;
		}

		ModelAndView mv = new ModelAndView("MBA_WX_Exam_Booking/slots/updateSlot");
		MBASlotBean slotBean = null;
		
		if(searchBean != null && searchBean.getSlotId() != null) {
			try {
				slotBean = slotsDAO.getSlotById(searchBean.getSlotId());
			}catch (Exception e) {
				
				slotBean = new MBASlotBean();
				request.setAttribute("error", "true");
				mv.addObject("errorMessage", "Error retrieving time table details!");
			}
		} else {
			slotBean = new MBASlotBean();
			request.setAttribute("error", "true");
			mv.addObject("errorMessage", "No Slot id!");
		}
		
		mv.addObject("slotBean", slotBean);
		return mv;
	}
	
	@RequestMapping(value="/updateSlot_MBAWX" , method=RequestMethod.POST)
	public ModelAndView editTimeTables(@ModelAttribute MBASlotBean slotBean, HttpServletRequest request,HttpServletResponse response) {
		if(!checkSession(request, response)){
			redirectToPortalApp(response);
			return null;
		}
		
		ModelAndView mv = new ModelAndView("MBA_WX_Exam_Booking/slots/updateSlot");
		mv.addObject("slotBean", slotBean);
		
		if(slotBean == null || slotBean.getSlotId() == null) {
			slotBean = new MBASlotBean();
			request.setAttribute("error", "true");
			mv.addObject("errorMessage", "Error!");
			return mv;
		}

		if(slotBean.getCapacity() == 0) {
			request.setAttribute("error", "true");
			mv.addObject("errorMessage", "Capacity cannot be 0!");
			return mv;
		}
		
		try {
			String userId = (String) request.getSession().getAttribute("userId");
			
			slotBean.setLastModifiedBy(userId);
			slotsDAO.updateSlot(slotBean);
			
			mv.addObject("success", "true");
			mv.addObject("successMessage", "Slot Updated Successfully!");
			mv.addObject("slotBean", slotsDAO.getSlotById(slotBean.getSlotId()));
		}catch (Exception e) {
			
			request.setAttribute("error", "true");
			mv.addObject("errorMessage", "Error updating slot details : " + e.getMessage());
		}
		
		return mv;
	}
	
	@RequestMapping(value="/deleteSlot_MBAWX",method=RequestMethod.POST)
	public @ResponseBody HashMap<String, String> deleteTimeTableEntry(HttpServletRequest request,HttpServletResponse response) {
		HashMap<String, String> responseData = new HashMap<String,String>();
		if(!checkSession(request, response)){
//			redirectToPortalApp(response);
			responseData.put("status", "error");
			responseData.put("message", "Session Expired");
			return responseData;
		}
		responseData.put("status", "success");
		
		String slotId = request.getParameter("slotId");
		if(slotId == null) {
			responseData.put("status", "error");
			responseData.put("message", "Invalid Slot Id found");
			return responseData;
		}
	
		String status = slotsDAO.deleteSlotById(slotId);
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

	@RequestMapping(value="/toggleSlotActive_MBAWX",method=RequestMethod.POST)
	public @ResponseBody HashMap<String, String> toggleSlotActive(HttpServletRequest request,HttpServletResponse response) {
		HashMap<String, String> responseData = new HashMap<String,String>();
		if(!checkSession(request, response)){
			redirectToPortalApp(response);
			responseData.put("status", "error");
			responseData.put("message", "Session Expired");
			return responseData;
		}
		responseData.put("status", "success");
		
		String slotId = request.getParameter("slotId");
		if(slotId == null) {
			responseData.put("status", "error");
			responseData.put("message", "Invalid Slot Id");
			return responseData;
		}
		
		String active = request.getParameter("active");

		if(active == null || !(active.equals("Y") || active.equals("N"))) {
			responseData.put("status", "error");
			responseData.put("message", "Invalid Value for Active");
			return responseData;
		}

		String userId = (String)request.getSession().getAttribute("userId");
		
		try {
			slotsDAO.toggleSlotActive(slotId, active, userId);	
			responseData.put("status", "success");
			responseData.put("message", "Record updated successfully!");
			return responseData;
		} catch (Exception e) {
			
			responseData.put("status", "error");
			responseData.put("message", "Error : " + e.getMessage());
			return responseData;
		}
	}
}
