package com.nmims.controllers;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

import com.nmims.beans.MBAExamBookingReportBean;
import com.nmims.beans.MBAExamBookingRequest;
import com.nmims.beans.MBAPaymentRequest;
import com.nmims.beans.MBASlotBean;
import com.nmims.beans.ReExamEligibleStudentsResponseBean;
import com.nmims.beans.StudentExamBean;
import com.nmims.daos.MBAWXReportsDAO;
import com.nmims.factory.ReExamEligibleStudentsReportFactory;
import com.nmims.interfaces.ReExamEligibleStudentsReportServiceInterface;

@Controller
@RequestMapping("/admin")
public class MBAWXReportsController extends BaseController {

	@Autowired
	MBAWXReportsDAO reportsDAO;
	
	@Autowired
	ReExamEligibleStudentsReportFactory reExamEligibleStudentsFactory; 
	
	private static final Logger logger = LoggerFactory.getLogger(MBAWXReportsController.class);
	
	private ArrayList<String> monthList = new ArrayList<String>(Arrays.asList( 
			"Jan", "Feb", "Mar", 
			"Apr", "May", "Jun", 
			"Jul", "Aug", "Sep", 
			"Oct", "Nov", "Dec"
		)
	); 
	
	@Value("#{'${CURRENT_YEAR_LIST}'.split(',')}")
	private ArrayList<String> yearList; 

	@RequestMapping(value = "/examBookingReportForm_MBAWX", method = {RequestMethod.GET, RequestMethod.POST})
	public ModelAndView examBookingReportForm(HttpServletRequest request, HttpServletResponse response) {
		if(!checkSession(request, response)){
			redirectToPortalApp(response);
			return null;
		}

		ModelAndView mv = new ModelAndView("MBA_WX_Exam_Booking/reports/bookings");
		mv.addObject("searchBean", new MBAExamBookingReportBean());
		
		mv.addObject("yearList", yearList);
		mv.addObject("monthList", monthList);
		return mv;
	}

	@RequestMapping(value = "/examBookingReport_MBAWX", method = RequestMethod.POST)
	public ModelAndView examBookingReport(HttpServletRequest request, HttpServletResponse response, @ModelAttribute MBAExamBookingReportBean searchBean){
		
		if(!checkSession(request, response)){
			redirectToPortalApp(response);
			return null;
		}
		ModelAndView mv = new ModelAndView("MBA_WX_Exam_Booking/reports/bookings");

		if(StringUtils.isBlank(searchBean.getExamYear()) || StringUtils.isBlank(searchBean.getExamMonth())) {

			mv.addObject("error", "true");
			mv.addObject("errorMessage", "Please Select year/month");
			return mv;
		}
		
		List<MBAExamBookingRequest> bookings = reportsDAO.getBookings(searchBean);
		request.getSession().setAttribute("examBookingList_MBAWX", bookings);
		
		mv.addObject("searchBean", searchBean);

		int numRecords = bookings.size();
		mv.addObject("numRecords", numRecords);
		mv.addObject("showNumRecords", true);
		mv.addObject("showDownloadButton", numRecords > 0);
		
		mv.addObject("yearList", yearList);
		mv.addObject("monthList", monthList);
		return mv;
	}

	@RequestMapping(value = "/downloadExamBookingReport_MBAWX", method = {RequestMethod.GET, RequestMethod.POST})
	public ModelAndView downloadExamBookingReport(HttpServletRequest request, HttpServletResponse response) {
		if(!checkSession(request, response)){
			redirectToPortalApp(response);
			return null;
		}
		
		List<MBAExamBookingRequest> examBookingList = (List<MBAExamBookingRequest>)request.getSession().getAttribute("examBookingList_MBAWX");
		
		List<MBAExamBookingReportBean> examBookingReportBeans = new ArrayList<MBAExamBookingReportBean>();
		
		for (MBAExamBookingRequest examBooking : examBookingList) {
			MBAExamBookingReportBean reportBean = new MBAExamBookingReportBean();

			StudentExamBean studentDetails = reportsDAO.getStudentDetails(examBooking.getSapid());
			MBAPaymentRequest paymentDetails = reportsDAO.getPaymentDetails(examBooking.getPaymentRecordId());
			
			reportBean.setBooking(examBooking);
			reportBean.setStudent(studentDetails);
			reportBean.setPaymentDetails(paymentDetails);
			
			examBookingReportBeans.add(reportBean);
		}
		
		return new ModelAndView("mbaExamBookingReportExcelView","examBookingList",examBookingReportBeans);
	}
	

	@RequestMapping(value = "/examCenterCapacityReportForm_MBAWX", method = {RequestMethod.GET, RequestMethod.POST})
	public ModelAndView examCenterCapacityReportForm(HttpServletRequest request, HttpServletResponse response) {
		if(!checkSession(request, response)){
			redirectToPortalApp(response);
			return null;
		}

		ModelAndView mv = new ModelAndView("MBA_WX_Exam_Booking/reports/slots");
		
		mv.addObject("searchBean", new MBASlotBean());
		mv.addObject("yearList", yearList);
		mv.addObject("monthList", monthList);
		return mv;
	}

	@RequestMapping(value = "/examCenterCapacityReport_MBAWX", method = RequestMethod.POST)
	public ModelAndView examCenterCapacityReport(HttpServletRequest request, HttpServletResponse response, @ModelAttribute MBASlotBean searchBean){
		
		if(!checkSession(request, response)){
			redirectToPortalApp(response);
			return null;
		}
		ModelAndView mv = new ModelAndView("MBA_WX_Exam_Booking/reports/slots");
		
		if(StringUtils.isBlank(searchBean.getExamYear()) || StringUtils.isBlank(searchBean.getExamMonth())) {

			mv.addObject("error", "true");
			mv.addObject("errorMessage", "Please Select year/month");
			return mv;
		}

		
		List<MBASlotBean> slotDetails = reportsDAO.getSlotBookingDetails(searchBean);
		request.getSession().setAttribute("centerBookingsList_MBA_WX", slotDetails);
		

		int numRecords = slotDetails.size();
		mv.addObject("numRecords", numRecords);
		mv.addObject("showNumRecords", true);
		mv.addObject("showDownloadButton", numRecords > 0);

		mv.addObject("searchBean", searchBean);
		mv.addObject("yearList", yearList);
		mv.addObject("monthList", monthList);
		return mv;
	}

	@RequestMapping(value = "/downloadExamCenterCapacityReport_MBAWX", method = {RequestMethod.GET, RequestMethod.POST})
	public ModelAndView downloadExamCenterCapacityReport(HttpServletRequest request, HttpServletResponse response) {
		if(!checkSession(request, response)){
			redirectToPortalApp(response);
			return null;
		}
		
		List<MBASlotBean> centerBookingsList = (List<MBASlotBean>)request.getSession().getAttribute("centerBookingsList_MBA_WX");
		
		return new ModelAndView("mbaExamCenterCapacityReportExcelView","centerBookingsList",centerBookingsList);
	}
	
	@RequestMapping(value = "/reExamEligibilityReportForm", method = {RequestMethod.GET})
	public ModelAndView reExamEligibilityReportFormMBAWX(HttpServletRequest request, HttpServletResponse response) {
		if(!checkSession(request, response)){
			redirectToPortalApp(response);
			return null;
		}

		ModelAndView mv = new ModelAndView("reExamEligibilityReport");
		mv.addObject("searchBean", new ReExamEligibleStudentsResponseBean());
		
		ArrayList<String> productTypes = new ArrayList<String>();
		productTypes.add("MBAWX");
		productTypes.add("MBAX");
		productTypes.add("PDDM");
		mv.addObject("productTypeList", productTypes);
		return mv;
	}

	@RequestMapping(value = "/reExamEligibilityReport", method = {RequestMethod.POST})
	public ModelAndView getReExamEligibleStudents(HttpServletRequest request, HttpServletResponse response, @ModelAttribute ReExamEligibleStudentsResponseBean searchBean) {
		if(!checkSession(request, response)){
			redirectToPortalApp(response);
			return null;
		}

		ModelAndView mv = new ModelAndView("reExamEligibilityReport");

		ReExamEligibleStudentsReportServiceInterface reExamEligibleStudentsStratergy = reExamEligibleStudentsFactory.getProductType(ReExamEligibleStudentsReportFactory.ProductType.valueOf(searchBean.getProductType()));
		// If IC/LC download the reprt, only show information about the students registered under them.
		searchBean.setAuthorizedCenterCodes(getAuthorizedCodes(request));
		
		try {
			reExamEligibleStudentsStratergy.getReExamEligibleStudents(searchBean);
			request.getSession().setAttribute("reExamEligibilityReport", searchBean);
			mv.addObject("showDownloadButton", true);
			setSuccess(request, ""
				+ "Report generated successfully for "
				+ "Exam Month : " + searchBean.getExamMonth() + " "
				+ "Exam Year : " + searchBean.getExamYear() + "!\n"
				+ "Please click download to download the report."
			);
			logger.info(searchBean.getListOfFailedOrResitStudentResults()+" re-exam eligible students found for "+searchBean.getProductType());
		} catch (Exception e) {
			setError(request, e.getMessage());
			logger.error("Exception occured while processing re-exam eligible students of "+searchBean.getProductType()+". Error:"+e.getStackTrace());
		}

		mv.addObject("searchBean", searchBean);

		ArrayList<String> productTypes = new ArrayList<String>();
		productTypes.add("MBAWX");
		productTypes.add("MBAX");
		productTypes.add("PDDM");
		mv.addObject("productTypeList", productTypes);
		return mv;
	}

	@RequestMapping(value = "/downloadReExamEligibilityReport", method = {RequestMethod.GET, RequestMethod.POST})
	public ModelAndView downloadReExamEligibilityReportMBAWX(HttpServletRequest request, HttpServletResponse response) {
		if(!checkSession(request, response)){
			redirectToPortalApp(response);
			return null;
		}
		
		ReExamEligibleStudentsResponseBean reportBean = (ReExamEligibleStudentsResponseBean)request.getSession().getAttribute("reExamEligibilityReport");
		
		if(reportBean == null) {
			ModelAndView mv = new ModelAndView("reExamEligibilityReport");
			setError(request, "File Not Found. Please try again!");
			mv.addObject("searchBean", new ReExamEligibleStudentsResponseBean());
			mv.addObject("yearList", yearList);
			mv.addObject("monthList", monthList);

			ArrayList<String> productTypes = new ArrayList<String>();
			productTypes.add("MBAWX");
			productTypes.add("MBAX");
			productTypes.add("PDDM");
			mv.addObject("productTypeList", productTypes);
			return mv;
		}
		
		return new ModelAndView("mbaReExamEligibilityReportExcelView", "reportBean", reportBean);
	}
}
