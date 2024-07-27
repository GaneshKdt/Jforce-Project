package com.nmims.services;

import org.springframework.beans.factory.annotation.Autowired;

import com.nmims.beans.MBAStudentDetailsBean;
import com.nmims.daos.MBAStudentDetailsDAO;

public class MBAWXExamBookingEligibilityService {

	@Autowired
	private MBAStudentDetailsDAO studentDetailsDAO;

	public MBAStudentDetailsBean getCurrentRegistrationData(String sapid, String acadYear, String acadMonth) {	
		// Get latest timebound details for current acad cycle 
		MBAStudentDetailsBean currentRegData = studentDetailsDAO.getTimeboundDetailsForStudentForMonthYear(sapid, acadMonth, acadYear);
		return currentRegData;
	}
}
