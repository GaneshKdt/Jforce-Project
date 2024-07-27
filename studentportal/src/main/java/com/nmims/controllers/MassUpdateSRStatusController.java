package com.nmims.controllers;

import java.util.Arrays;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.nmims.services.MassUpdateSRStatusService;

@Controller
public class MassUpdateSRStatusController {
	private static final Logger logger = LoggerFactory.getLogger(MassUpdateSRStatusController.class);
	
	
	@Autowired
	MassUpdateSRStatusService massUpdateSrStatusService ;
	/**
	 * Displays Mass update Service Request Status Form
	 * @return massUpdateSR view
	 */
	@RequestMapping(value = "/admin/massUpdateSRStatusForm" ,method = {RequestMethod.GET ,RequestMethod.POST })
	public String massUpdateSRStatusForm() {
		return "jsp/serviceRequest/massUpdateSR";
	}
	
	/**
	 * Mass update Service Request status of Service Request IDs provided by the Admin user. 
	 * @param request - HttpServletRequest
	 * @param redirectAttributes - RedirectAttributes to display Model Attributes on redirect
	 * @param serviceRequestIds - String containing the serviceRequestIds provided by the user
	 * @param requestStatus - status to be applied to the provided Service Request records
	 * @param cancellationReason - cancellation reason to be applied to the provided Service Request records
	 * @return redirects to massUpdateSR view displaying a response message
	 */
	@PostMapping(value = "/admin/massUpdateSRStatus")
	public ModelAndView massUpdateSRStatus(HttpServletRequest request, final RedirectAttributes redirectAttributes, @RequestParam String serviceRequestIds, 
									@RequestParam String requestStatus, @RequestParam(required = false) String cancellationReason) {
		String userId = (String) request.getSession().getAttribute("userId");
		ModelAndView model = new ModelAndView("jsp/serviceRequest/massUpdateSR");
		try {
			String[] serviceRequestIdsArray = serviceRequestIds.split("\\R");												//split the string on line breaks (\R)
			
			Map<String, String> response = massUpdateSrStatusService.massUpdateSR(Arrays.asList(serviceRequestIdsArray), requestStatus, cancellationReason, userId);
			logger.info("Mass updated Service Request status: {}, initiated by user: {}. Response status: {}, {} records updated successfully. Error message: {}", 
						requestStatus, userId, response.get("status"), response.get("successCount"), response.get("errorMessage"));
			
			//Adding Redirect Attributes to display Messages to the user
			model.addObject("status", response.get("status"));
			model.addObject("successCount", response.get("successCount"));
			model.addObject("statusMessage", response.get("errorMessage"));
		}
		catch(Exception ex) {
//			ex.printStackTrace();
			logger.error("Error encountered while Mass updating Service Request status: {}, initiated by user: {}, Exception thrown: ", requestStatus, userId, ex);
			
			//Adding Redirect Attributes to display Error Message to the user. IllegalArgumentException thrown for invalid fields.
			String errorMessage = (ex instanceof IllegalArgumentException) ? ex.getMessage() : "Error while Mass updating Service Request Status. Please try again!";
			model.addObject("status", "error");
			model.addObject("statusMessage", errorMessage);
		}
		
		return model;
	}
	
	
}
