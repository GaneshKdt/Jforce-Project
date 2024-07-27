package com.nmims.controllers;

import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;

import com.nmims.beans.MassUploadTrackingSRBean;
import com.nmims.daos.MassUploadTrackingSRDAO;
import com.nmims.services.MassUploadTrackingSRService;

@Controller
public class MassUploadTrackingSRController {
	
	@Value( "${COURIER_NAME}" )
	private List<String> COURIER_NAME;
	
	@Value( "${SR_TRACKING_LIST}" )
	private List<String> SR_TRACKING_LIST;
	
	private static final Logger logger = LoggerFactory.getLogger(MassUploadTrackingSRController.class);
	
	@Autowired
	MassUploadTrackingSRService massUploadTrackingSRService;
	
	@Autowired
	MassUploadTrackingSRDAO massUploadTrackingSRDAO;

	@RequestMapping(value="/admin/massUploadTrackingSRForm", method = { RequestMethod.GET, RequestMethod.POST })
	public ModelAndView massUploadTrackingSR(HttpServletRequest request) {
		
		return new ModelAndView("jsp/serviceRequest/massUploadTrackingSR");
	}
	 
	/**
	 * Upload records from excel file to database
	 * @params Multipart file - file upload by the admin
	 * @return massUploadTrackingSR - return to massUploadTrackingSR page with message
	 */
	@RequestMapping(value = "/admin/massUploadTrackingSR", method = { RequestMethod.POST })
	public ModelAndView massUploadSR(HttpServletRequest request, @RequestParam(value="file") MultipartFile file) {
		MassUploadTrackingSRBean mssgBean;
		
		try {
			mssgBean = massUploadTrackingSRService.saveSrExcelRecord(request,file);
			
			if(mssgBean.getSuccessList() != null && mssgBean.getSuccessList().size() > 0) {
				request.setAttribute("success", "true");
				request.setAttribute("successMessage", mssgBean.getSuccessList().get(0).getSuccessMessage());
			}
			if(mssgBean.getErrorList() != null && mssgBean.getErrorList().size() > 0) {
				request.setAttribute("error", "true");
				request.setAttribute("errorMessage", mssgBean.getErrorList().get(0).getErrorMessage());	
			}
		}
		catch (IllegalArgumentException ie) {
			request.setAttribute("error", "true");
			request.setAttribute("errorMessage","Error : "+ie.getMessage());
			logger.info("{} due to  {} ", ie.getMessage(), ie);
		}
		catch (Exception e) {
			e.printStackTrace();
			request.setAttribute("error", "true");
			request.setAttribute("errorMessage","Error : Fail to upload tracking records");
			logger.info("Error in inserting rows due to : {}", e);
		}
		
		return massUploadTrackingSR(request);
	}
	
	/**
	 * Delete massUploadTrackingSR records by srId
	 * @params Integer srId - srId of the records to be deleted
	 * @return success/error - message to the admin
	 */
	@RequestMapping(value = "/admin/deleteMassUploadTrackingSR", method = RequestMethod.POST)
	public ModelAndView deleteMassUploadSRById(HttpServletRequest request,@RequestParam(value="srId") Integer srId, Model m) {
		
		if(massUploadTrackingSRService.deleteMassUploadTrackingSRBySrId(srId)) {
			request.setAttribute("success", "true");
			request.setAttribute("successMessage", "Record deleted successfully");
		}
		else {
			request.setAttribute("error", "true");
			request.setAttribute("errorMessage", "Failed to delete record for serviceRequestId : "+srId);
		}
		return searchTrackingSR(request, new MassUploadTrackingSRBean(), m);
	}
		
	/**
	 * Edit massUploadTrackingSR records by srId
	 * @params Integer srId - srId of the records to be edit
	 * @return success/error - message to the admin
	 */
	@RequestMapping(value = "/admin/editMassUploadTrackingSR", method = RequestMethod.POST)
	public ModelAndView editmassUploadSRById(HttpServletRequest request,@RequestParam(value="srId") Integer srId) {
		ModelAndView modelAndView = new ModelAndView("jsp/serviceRequest/editMassUploadTrackingSR");
		try {
			MassUploadTrackingSRBean massUploadTrackingSRRecord = massUploadTrackingSRService.getMassUploadTrackingBySRId(srId);
			modelAndView.addObject("sr", massUploadTrackingSRRecord);
			modelAndView.addObject("courierName", COURIER_NAME);
			modelAndView.addObject("rowCount", "1");
			return modelAndView;
	    }
		catch (Exception e) {
			request.setAttribute("error", "true");
			request.setAttribute("errorMessage", "Failed to edit Records");
			logger.info("failed to edit tracking records for seviceRequestId : {} due to {}", srId, e);
			return modelAndView;
		}
	}
		
	/**
	 * Save massUploadTrackingSR records to database
	 * @params MassUploadTrackingSRBean massUploadTrackingSRBean - get from the admin side
	 * @return success/error - message to the admin
	 */
	@RequestMapping(value = "/admin/updateMassUploadTrackingSR", method={RequestMethod.POST})
	public ModelAndView saveUploadSR(HttpServletRequest request, MassUploadTrackingSRBean massUploadTrackingSRBean, Model m) {
		
		if(massUploadTrackingSRService.updateMassUploadTrackingSR(request,massUploadTrackingSRBean)) {
			request.setAttribute("success", "true");
			request.setAttribute("successMessage", "Successfully updated SR record");
			return searchTrackingSR(request, new MassUploadTrackingSRBean(), m);
		}
		else {
			request.setAttribute("error", "true");
			request.setAttribute("errorMessage", "Failed to update records for serviceRequestId : "+massUploadTrackingSRBean.getServiceRequestId());
			return searchTrackingSR(request, new MassUploadTrackingSRBean(), m);
		}
	}
	
	/**
	 * Get Tracking shipment details
	 * @params Integer srId - sericeRequestId of the SR
	 * @params String srType - serviceRequestType of the SR
	 * @return trackShipment - forwards to trackShipment page
	 */
	@RequestMapping(value="/student/trackShipment",method={RequestMethod.POST})
	public ModelAndView trackShipment(HttpServletRequest request,@RequestParam(value="srId") Integer srId,@RequestParam(value="srType") String srType) {
		
		ModelAndView modelAndView = new ModelAndView("jsp/serviceRequest/trackShipment");
		try {
			MassUploadTrackingSRBean massUploadTrackingSRBean = massUploadTrackingSRService.getMassUploadTrackingBySRId(srId);
			modelAndView.addObject("massUploadTrackingSRBean",massUploadTrackingSRBean);
			modelAndView.addObject("srType",srType);
		} catch (Exception e) {
			request.setAttribute("error", "true");
			request.setAttribute("errorMessage", "Failed to get shipment details");
			logger.info("Failed to get shipment details for : "+srId+" "+ e);
		}
		return  modelAndView;
	}
	
	/**
	 * Get the data from the searchTrackingSR form
	 * @param request
	 * @param response
	 * @param m
	 * @return
	 */
	@RequestMapping(value="/admin/searchTrackingSRForm", method = {RequestMethod.GET, RequestMethod.POST})
	public ModelAndView searchTrackingSRForm(HttpServletRequest request, Model m) {
		MassUploadTrackingSRBean searchBean = new MassUploadTrackingSRBean();
		
		m.addAttribute("rowCount", 0); 
		m.addAttribute("courierList", COURIER_NAME); 
		m.addAttribute("searchBean", searchBean); 
		m.addAttribute("srTypeList", SR_TRACKING_LIST);
		return new ModelAndView("jsp/serviceRequest/searchTrackingSR");
	}
	
	/**
	 * Get the filtered records from trackingRecords based on search
	 * @param request
	 * @param response
	 * @param searchBean
	 * @param m
	 * @return
	 */
	@RequestMapping(value="/admin/searchTrackingSR",method={RequestMethod.POST})
	public ModelAndView searchTrackingSR(HttpServletRequest request, @ModelAttribute MassUploadTrackingSRBean searchBean, Model m) {
		ModelAndView modelAndView = new ModelAndView("jsp/serviceRequest/searchTrackingSR");
		try {
			List<MassUploadTrackingSRBean> searchTrackingList  = massUploadTrackingSRService.getSearchTrackingRecords(searchBean);
			
			if(searchTrackingList  == null || searchTrackingList.size() == 0) {
				request.setAttribute("error", "true");
				request.setAttribute("errorMessage", "No records found.");
				
				return searchTrackingSRForm(request, m);
			}
			request.getSession().setAttribute("searchTrackingList", searchTrackingList);
			
			modelAndView.addObject("searchTrackingList", searchTrackingList);
			modelAndView.addObject("rowCount", searchTrackingList.size()); 
			modelAndView.addObject("searchBean", searchBean); 
			modelAndView.addObject("courierList", COURIER_NAME); 
			modelAndView.addObject("srTypeList", SR_TRACKING_LIST); 
		} 
		catch (Exception e) {
			request.setAttribute("error", "true");
			request.setAttribute("errorMessage", "No records found.");
			logger.info("Error in getting Search SR Records from servicerequest_trackingRecords table due to {}", e);
			
			return modelAndView;
		}
		return modelAndView;
	}
	
	/**
	 * Downloading the tracking sr report in excel file
	 * @param request
	 * @param response
	 * @return
	 */
	@SuppressWarnings("unchecked")
	@RequestMapping(value = "/admin/downloadTrackingReport", method = {RequestMethod.GET, RequestMethod.POST})
	public ModelAndView downloadTrackingReport(HttpServletRequest request, HttpServletResponse response) {
		List<MassUploadTrackingSRBean> searchTrackingList = (List<MassUploadTrackingSRBean>) request.getSession().getAttribute("searchTrackingList");
		
		return new ModelAndView("jsp/trackingSRExcelView", "searchTrackingList", searchTrackingList);
	}
	
	/**
	 * sends tracking notification to the students mail
	 * @return
	 */
	@ResponseBody
	@RequestMapping(value = "/notifyStudentForTrackingDetails", method = { RequestMethod.GET, RequestMethod.POST })
	public String notifyStudentForTrackingDetailsCall() {
		try {
			massUploadTrackingSRService.notifyStudentForTrackingDetails();
		} 
		catch (Exception e) {
			logger.info("Error during notifyStudentForTrackingDetails due to  : "+e);
		}
		return "success";
	}
	
}
