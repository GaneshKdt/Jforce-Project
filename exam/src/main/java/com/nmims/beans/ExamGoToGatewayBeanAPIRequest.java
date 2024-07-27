package com.nmims.beans;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class ExamGoToGatewayBeanAPIRequest   implements Serializable  {
	int totalExamFees;
	String hasReleasedSubjects;
	String onlineSeatBookingComplete;
	List<String> Subjects;
	String sapid;
	List<TimetableBean> timeTableList;
	List<String> selectedCenters;
	
	public List<String> getSelectedCenters() {
		return selectedCenters;
	}

	public void setSelectedCenters(List<String> selectedCenters) {
		this.selectedCenters = selectedCenters;
	}

	public List<TimetableBean> getTimeTableList() {
		return timeTableList;
	}

	public void setTimeTableList(List<TimetableBean> timeTableList) {
		this.timeTableList = timeTableList;
	}

	public String getSapid() {
		return sapid;
	}

	public void setSapid(String sapid) {
		this.sapid = sapid;
	}

	public List<String> getSubjects() {
		return Subjects;
	}

	public void setSubjects(List<String> subjects) {
		Subjects = subjects;
	}

	

	public String getOnlineSeatBookingComplete() {
		return onlineSeatBookingComplete;
	}

	public void setOnlineSeatBookingComplete(String onlineSeatBookingComplete) {
		this.onlineSeatBookingComplete = onlineSeatBookingComplete;
	}

	public String getHasReleasedSubjects() {
		return hasReleasedSubjects;
	}

	public void setHasReleasedSubjects(String hasReleasedSubjects) {
		this.hasReleasedSubjects = hasReleasedSubjects;
	}

	public int getTotalExamFees() {
		return totalExamFees;
	}

	public void setTotalExamFees(int totalExamFees) {
		this.totalExamFees = totalExamFees;
	}
}
