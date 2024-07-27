package com.nmims.controllers;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import com.nmims.beans.MBACentersBean;
import com.nmims.daos.MBAWXCentersDAO;
import com.nmims.helpers.ExcelHelper;

@Controller
@RequestMapping("/admin")
public class MBAWXCentersController extends BaseController {
	
	@Autowired
	MBAWXCentersDAO centersDAO;
	
	@RequestMapping(value="/uploadCentersForm_MBAWX",method=RequestMethod.GET)
	public ModelAndView uploadCentersForm(HttpServletRequest request,HttpServletResponse response) {
		if(!checkSession(request, response)){
			redirectToPortalApp(response);
			return null;
		}
		
		MBACentersBean centersBean = new MBACentersBean();
		ModelAndView mv = new ModelAndView("MBA_WX_Exam_Booking/centers/uploadCenters");
		mv.addObject("fileBean", centersBean);
		mv.addObject("centersList", centersDAO.getCentersList());
		return mv;
	} 
	
	@RequestMapping(value="/uploadCenters_MBAWX",method=RequestMethod.POST)
	public ModelAndView uploadCenters(MBACentersBean centersBean , HttpServletRequest request,HttpServletResponse response) {
		if(!checkSession(request, response)){
			redirectToPortalApp(response);
			return null;
		}
		String userId = (String)request.getSession().getAttribute("userId");
		centersBean.setLastModifiedBy(userId);
		ModelAndView mv = new ModelAndView("MBA_WX_Exam_Booking/centers/uploadCenters");
		ExcelHelper excelHelper = new ExcelHelper();
		List<MBACentersBean> centersBeansList = excelHelper.addExcelMBACenters(centersBean);

		List<MBACentersBean> successCentersBeansList = new ArrayList<MBACentersBean>();
		List<MBACentersBean> failCentersBeansList = new ArrayList<MBACentersBean>();

		List<MBACentersBean> allCenters = centersDAO.getCentersList();

		List<String> centerNames = new ArrayList<String>();

		for (MBACentersBean center : allCenters) {
			centerNames.add(center.getName());
		}

		for (MBACentersBean center : centersBeansList) {
			String error = null;
			
			// Check for errors while mapping excel record
			if(!StringUtils.isBlank(center.getError())) {
				error = center.getError();
			} else {
				error = checkCentersBean(center);

				if(centerNames.contains(center.getName())) {
					error = "Duplicate Center Name";
				}
			}
			
			if(error == null) {
				centerNames.add(center.getName());
				successCentersBeansList.add(center);
			} else {
				center.setError(error);
				failCentersBeansList.add(center);
			}
		}
		
		request.getSession().setAttribute("centersBeansList_MBAWX", successCentersBeansList);
		
		mv.addObject("fileBean", centersBean);
		mv.addObject("showApproveButton", successCentersBeansList.size() > 0);
		mv.addObject("isApprove", true);
		mv.addObject("successCentersBeansList", successCentersBeansList);
		mv.addObject("failCentersBeansList", failCentersBeansList);
		mv.addObject("centersList", centersBeansList);
		return mv;
	}

	@RequestMapping(value="/approveUploadCenters_MBAWX")
	public ModelAndView approveUploadCenters(HttpServletRequest request,HttpServletResponse response) {
		if(!checkSession(request, response)){
			redirectToPortalApp(response);
			return null;
		}

		ModelAndView mv = new ModelAndView("MBA_WX_Exam_Booking/centers/uploadCenters");
		
		List<MBACentersBean> centersBeansList = (List<MBACentersBean>) request.getSession().getAttribute("centersBeansList_MBAWX");

		if(centersBeansList != null && centersBeansList.size() == 0) {
			request.setAttribute("error","true");
			request.setAttribute("errorMessage","No data to upload!");
		} else {
			try {
				List<MBACentersBean> insertResults = centersDAO.batchInsertCenters(centersBeansList);

				List<MBACentersBean> successfulInserts = new ArrayList<MBACentersBean>();
				List<MBACentersBean> failedInserts = new ArrayList<MBACentersBean>();
				for (MBACentersBean insertResult : insertResults) {
					if(StringUtils.isBlank(insertResult.getError())) {
						successfulInserts.add(insertResult);
					} else {
						failedInserts.add(insertResult);
					}
				}
				mv.addObject("successCentersBeansList", successfulInserts);
				mv.addObject("failCentersBeansList", failedInserts);
			} catch (Exception e) {
				
				request.setAttribute("error","true");
				request.setAttribute("errorMessage","Failed to create record, " + e.getMessage());
			}
		}
		mv.addObject("fileBean", new MBACentersBean());
		mv.addObject("centersList", centersDAO.getCentersList());
		return mv;
	}
	
	@RequestMapping(value="/updateCenterForm_MBAWX")
	public ModelAndView editCentersForm(@ModelAttribute MBACentersBean searchBean, HttpServletRequest request, HttpServletResponse response) {
		if(!checkSession(request, response)){
			redirectToPortalApp(response);
			return null;
		}

		ModelAndView mv = new ModelAndView("MBA_WX_Exam_Booking/centers/updateCenter");
		MBACentersBean centerBean = null;
		
		
		if(searchBean != null && searchBean.getCenterId() != null) {
			try {
				centerBean = centersDAO.getCenterById(searchBean.getCenterId());
			}catch (Exception e) {
				
				centerBean = new MBACentersBean();
				mv.addObject("error", "true");
				mv.addObject("errorMessage", "Error retrieving center!");
			}
		} else {
			centerBean = new MBACentersBean();
			mv.addObject("error", "true");
			mv.addObject("errorMessage", "No center id!");
		}

		mv.addObject("centerBean", centerBean);
		
		return mv;
	}
	
	@RequestMapping(value="/updateCenter_MBAWX" , method=RequestMethod.POST)
	public ModelAndView editCenters(@ModelAttribute MBACentersBean centerBean, HttpServletRequest request,HttpServletResponse response) {
		if(!checkSession(request, response)){
			redirectToPortalApp(response);
			return null;
		}
		
		ModelAndView mv = new ModelAndView("MBA_WX_Exam_Booking/centers/updateCenter");
		
		if(centerBean != null) {
			// check fields
			if(centerBean.getCenterId() == null) {
				mv.addObject("error", "true");
				mv.addObject("errorMessage", "Error!");
			} else {
				String error = checkCentersBean(centerBean);
				if(error != null) {
					mv.addObject("error", "true");
					mv.addObject("errorMessage", error);
				} else {
					try {
						String userId = (String)request.getSession().getAttribute("userId");
						centerBean.setLastModifiedBy(userId);
						centersDAO.updateCenterDetails(centerBean);

						mv.addObject("success", "true");
						mv.addObject("successMessage", "Center Updated Successfully!");
					}catch (Exception e) {
						
						mv.addObject("error", "true");
						mv.addObject("errorMessage", "Error updating center details!");
					}
				}
			}
		} else {
			centerBean = new MBACentersBean();
			mv.addObject("error", "true");
			mv.addObject("errorMessage", "Error!");
		}
		
		mv.addObject("centerBean", centerBean);
		return mv;
	}
	
	@RequestMapping(value="/deleteCenter_MBAWX",method=RequestMethod.POST)
	public @ResponseBody HashMap<String, String> deleteCenter(HttpServletRequest request,HttpServletResponse response) {
		HashMap<String, String> responseData = new HashMap<String,String>();
		if(!checkSession(request, response)){
			redirectToPortalApp(response);
			responseData.put("status", "error");
			responseData.put("message", "Session Expired");
			return responseData;
		}
		responseData.put("status", "success");
		String centerId = request.getParameter("centerId");
		if(centerId == null) {
			responseData.put("status", "error");
			responseData.put("message", "Invalid CenterID found");
			return responseData;
		}
		
		String status = centersDAO.deleteCenterById(centerId);
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
	

	@RequestMapping(value="/toggleCenterActive_MBAWX",method=RequestMethod.POST)
	public @ResponseBody HashMap<String, String> toggleCenterActive(HttpServletRequest request,HttpServletResponse response) {
		HashMap<String, String> responseData = new HashMap<String,String>();
		if(!checkSession(request, response)){
			redirectToPortalApp(response);
			responseData.put("status", "error");
			responseData.put("message", "Session Expired");
			return responseData;
		}
		responseData.put("status", "success");
		
		String centerId = request.getParameter("centerId");
		if(centerId == null) {
			responseData.put("status", "error");
			responseData.put("message", "Invalid CenterID");
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
			centersDAO.toggleCenterActive(centerId, active, userId);	
			responseData.put("status", "success");
			responseData.put("message", "Record updated successfully!");
			return responseData;
		} catch (Exception e) {
			
			responseData.put("status", "error");
			responseData.put("message", "Error : " + e.getMessage());
			return responseData;
		}
	}
	
	private String checkCentersBean(MBACentersBean center) {
		if(StringUtils.isEmpty(center.getName())) {
			return "Name cannot be empty!";
		} else if(StringUtils.isEmpty(center.getAddress())) {
			return "Address cannot be empty!";
		} else if(StringUtils.isEmpty(center.getCity())) {
			return "City cannot be empty!";
		} else if(StringUtils.isEmpty(center.getState())) {
			return "State cannot be empty!";
		}
		return null;
	}
}
