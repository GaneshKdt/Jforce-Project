package com.nmims.controllers;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestAttribute;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import com.google.gson.Gson;
import com.nmims.beans.RevenueReportField;
import com.nmims.helpers.RevenueReportHelper;
import com.nmims.listeners.ReportsSyncScheduler;

@Controller
public class RevenueReportController {

	@Autowired
	RevenueReportHelper helper;
	
	@Autowired
	ReportsSyncScheduler scheduler;

	@Value("${SFDC_API_MAX_RETRY_COUNT}")
	private String SFDC_API_MAX_RETRY_COUNT;
	
	@RequestMapping(value = "/getRevenueForDate", method = RequestMethod.GET, produces = "application/json")
	public ResponseEntity<String> getPaymentStatus(HttpServletRequest request) {
		Gson gson = new Gson();
		String date = request.getParameter("date");
		return ResponseEntity.ok(gson.toJson(helper.getCollectionForDate(date)));
	}
	
	@RequestMapping(value = "/manuallyUpdateRevenueForYesterday", method = RequestMethod.GET, produces = "application/json")
	public ResponseEntity<String> manuallyUpdateRevenueForYesterday(HttpServletRequest request) {
		Gson gson = new Gson();
		
		scheduler.syncRevenueForPreviousDay();

		Date today = new Date();
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(today);
		calendar.add(Calendar.DATE, -1);
		Date yesterday = calendar.getTime();
		SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");

		String dateStr = formatter.format(yesterday);
		return ResponseEntity.ok(gson.toJson(helper.getCollectionForDate(dateStr)));
	}
	
	@RequestMapping(value = "/manuallyUpdateRevenueForDate", method = RequestMethod.POST, produces = "application/json")
	public ResponseEntity<List<RevenueReportField>> manuallyUpdateRevenueForDate(HttpServletRequest request, @RequestBody RevenueReportField input) {
		
		scheduler.syncRevenueForDate(input.getDate(), Integer.parseInt(SFDC_API_MAX_RETRY_COUNT));
		return ResponseEntity.ok(helper.getCollectionForDate(input.getDate()));
	}

	
	@RequestMapping(value = "/syncRevenueForPreviousDates", method = RequestMethod.GET, produces = "application/json")
	public ResponseEntity<String> syncRevenueForPreviousDates(@RequestParam("startDateStr") String startDateStr, @RequestParam("endDateStr") String endDateStr) {
		
		try {
			scheduler.syncRevenueForPreviousDates(startDateStr, endDateStr);
			return ResponseEntity.ok("success");
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return ResponseEntity.ok("err");
		}
	}
}
