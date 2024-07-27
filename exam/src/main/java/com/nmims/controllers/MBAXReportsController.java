package com.nmims.controllers;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;
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
import com.nmims.beans.StudentExamBean;
import com.nmims.daos.MBAXReportsDAO;

@Controller
@RequestMapping("/admin")
public class MBAXReportsController extends BaseController {

	@Autowired
	MBAXReportsDAO reportsDAO;
	
	
	private ArrayList<String> monthList = new ArrayList<String>(Arrays.asList( 
			"Jan", "Feb", "Mar", 
			"Apr", "May", "Jun", 
			"Jul", "Aug", "Sep", 
			"Oct", "Nov", "Dec"
		)
	); 
	
	@Value("#{'${CURRENT_YEAR_LIST}'.split(',')}")
	private List<String> CURRENT_YEAR_LIST; 

	@RequestMapping(value = "/examBookingReportForm_MBAX", method = {RequestMethod.GET, RequestMethod.POST})
	public ModelAndView examBookingReportForm(HttpServletRequest request, HttpServletResponse response) {
		if(!checkSession(request, response)){
			redirectToPortalApp(response);
			return null;
		}

		ModelAndView mv = new ModelAndView("MBA_X_Exam_Booking/reports/bookings");
		mv.addObject("searchBean", new MBAExamBookingReportBean());
		
		mv.addObject("yearList", CURRENT_YEAR_LIST);
		mv.addObject("monthList", monthList);
		return mv;
	}

	@RequestMapping(value = "/examBookingReport_MBAX", method = RequestMethod.POST)
	public ModelAndView examBookingReport(HttpServletRequest request, HttpServletResponse response, @ModelAttribute MBAExamBookingReportBean searchBean){
		
		if(!checkSession(request, response)){
			redirectToPortalApp(response);
			return null;
		}
		ModelAndView mv = new ModelAndView("MBA_X_Exam_Booking/reports/bookings");

		if(StringUtils.isBlank(searchBean.getExamYear()) || StringUtils.isBlank(searchBean.getExamMonth())) {

			mv.addObject("error", "true");
			mv.addObject("errorMessage", "Please Select year/month");
			return mv;
		}
		
		List<MBAExamBookingRequest> bookings = reportsDAO.getBookings(searchBean);
		request.getSession().setAttribute("examBookingList_MBAX", bookings);
		
		mv.addObject("searchBean", searchBean);

		int numRecords = bookings.size();
		mv.addObject("numRecords", numRecords);
		mv.addObject("showNumRecords", true);
		mv.addObject("showDownloadButton", numRecords > 0);
		
		mv.addObject("yearList", CURRENT_YEAR_LIST);
		mv.addObject("monthList", monthList);
		return mv;
	}

	@RequestMapping(value = "/downloadExamBookingReport_MBAX", method = {RequestMethod.GET, RequestMethod.POST})
	public ModelAndView downloadExamBookingReport(HttpServletRequest request, HttpServletResponse response) {
		if(!checkSession(request, response)){
			redirectToPortalApp(response);
			return null;
		}
		
		List<MBAExamBookingRequest> examBookingList = (List<MBAExamBookingRequest>)request.getSession().getAttribute("examBookingList_MBAX");
		
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
	

	@RequestMapping(value = "/examCenterCapacityReportForm_MBAX", method = {RequestMethod.GET, RequestMethod.POST})
	public ModelAndView examCenterCapacityReportForm(HttpServletRequest request, HttpServletResponse response) {
		if(!checkSession(request, response)){
			redirectToPortalApp(response);
			return null;
		}

		ModelAndView mv = new ModelAndView("MBA_X_Exam_Booking/reports/slots");
		
		mv.addObject("searchBean", new MBASlotBean());
		mv.addObject("yearList", CURRENT_YEAR_LIST);
		mv.addObject("monthList", monthList);
		return mv;
	}

	@RequestMapping(value = "/examCenterCapacityReport_MBAX", method = RequestMethod.POST)
	public ModelAndView examCenterCapacityReport(HttpServletRequest request, HttpServletResponse response, @ModelAttribute MBASlotBean searchBean){
		
		if(!checkSession(request, response)){
			redirectToPortalApp(response);
			return null;
		}
		ModelAndView mv = new ModelAndView("MBA_X_Exam_Booking/reports/slots");
		
		if(StringUtils.isBlank(searchBean.getExamYear()) || StringUtils.isBlank(searchBean.getExamMonth())) {

			mv.addObject("error", "true");
			mv.addObject("errorMessage", "Please Select year/month");
			return mv;
		}

		
		List<MBASlotBean> slotDetails = reportsDAO.getSlotBookingDetails(searchBean);
		request.getSession().setAttribute("centerBookingsList_MBA_X", slotDetails);
		

		int numRecords = slotDetails.size();
		mv.addObject("numRecords", numRecords);
		mv.addObject("showNumRecords", true);
		mv.addObject("showDownloadButton", numRecords > 0);

		mv.addObject("searchBean", searchBean);
		mv.addObject("yearList", CURRENT_YEAR_LIST);
		mv.addObject("monthList", monthList);
		return mv;
	}

	@RequestMapping(value = "/downloadExamCenterCapacityReport_MBAX", method = {RequestMethod.GET, RequestMethod.POST})
	public ModelAndView downloadExamCenterCapacityReport(HttpServletRequest request, HttpServletResponse response) {
		if(!checkSession(request, response)){
			redirectToPortalApp(response);
			return null;
		}
		
		List<MBASlotBean> centerBookingsList = (List<MBASlotBean>)request.getSession().getAttribute("centerBookingsList_MBA_X");
		
		return new ModelAndView("mbaExamCenterCapacityReportExcelView","centerBookingsList",centerBookingsList);
	}
	
}
