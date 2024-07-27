package com.nmims.beans;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class ExamSelectSubjectAPIRequest  implements Serializable  {
	private String sapid;
	private List<String> subjects;
	private List<String> selectedCentersList;
	private String hasReleasedSubjects;
	private String onlineSeatBookingComplete;
	private String totalExamFees;
	
	public String getHasReleasedSubjects() {
		return hasReleasedSubjects;
	}
	public void setHasReleasedSubjects(String hasReleasedSubjects) {
		this.hasReleasedSubjects = hasReleasedSubjects;
	}
	public String getOnlineSeatBookingComplete() {
		return onlineSeatBookingComplete;
	}
	public void setOnlineSeatBookingComplete(String onlineSeatBookingComplete) {
		this.onlineSeatBookingComplete = onlineSeatBookingComplete;
	}
	public String getTotalExamFees() {
		return totalExamFees;
	}
	public void setTotalExamFees(String totalExamFees) {
		this.totalExamFees = totalExamFees;
	}
	public List<String> getSubjects() {
		return subjects;
	}
	public void setSubjects(List<String> subjects) {
		this.subjects = subjects;
	}
	public String getSapid() {
		return sapid;
	}
	public void setSapid(String sapid) {
		this.sapid = sapid;
	}
	public List<String> getSelectedCentersList() {
		return selectedCentersList;
	}
	public void setSelectedCentersList(List<String> selectedCentersList) {
		this.selectedCentersList = selectedCentersList;
	}
	
	
	
	
}
