package com.nmims.beans;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonProperty;

@SuppressWarnings("serial")
public class MyActivityResponseStudentBean implements Serializable{

	private String status;
	private String message;
	
	private boolean currentCycleStudent;
	
	@JsonProperty("This_Week")
	private List<TimeSpentStudentBean> this_week = new ArrayList<TimeSpentStudentBean>();
	
	@JsonProperty("Current_Month")
	private List<TimeSpentStudentBean> current_month = new ArrayList<TimeSpentStudentBean>();
	
	@JsonProperty("Last_Month")
	private List<TimeSpentStudentBean> last_month = new ArrayList<TimeSpentStudentBean>();
	
	@JsonProperty("session_attendance_count")
	private List<VideoAndSessionAttendanceCountStudentBean> sessionAttendanceCountList = new ArrayList<VideoAndSessionAttendanceCountStudentBean>();
	
	private List<HashMap<String, String>> trackDetailsList = new ArrayList<HashMap<String , String>>();
	private List<TotalSessionDetailsStudentBean> totalSessionDetails = new ArrayList<TotalSessionDetailsStudentBean>();
	private List<TotalVideoDetailsStudentBean> totalVideoDetails = new ArrayList<TotalVideoDetailsStudentBean>();
	private List<Map<String, Object>> pdfReadDetailsList = new ArrayList<Map<String,Object>>();
	
	public MyActivityResponseStudentBean() {
		super();
	}

	public String getStatus() {
		return status;
	}

	public String getMessage() {
		return message;
	}
	
	public boolean isCurrentCycleStudent() {
		return currentCycleStudent;
	}

	public List<TimeSpentStudentBean> getThis_week() {
		return this_week;
	}

	public List<TimeSpentStudentBean> getCurrent_month() {
		return current_month;
	}

	public List<TimeSpentStudentBean> getLast_month() {
		return last_month;
	}

	public List<VideoAndSessionAttendanceCountStudentBean> getSessionAttendanceCountList() {
		return sessionAttendanceCountList;
	}

	public List<HashMap<String, String>> getTrackDetailsList() {
		return trackDetailsList;
	}

	public List<TotalSessionDetailsStudentBean> getTotalSessionDetails() {
		return totalSessionDetails;
	}

	public List<TotalVideoDetailsStudentBean> getTotalVideoDetails() {
		return totalVideoDetails;
	}

	public List<Map<String, Object>> getPdfReadDetailsList() {
		return pdfReadDetailsList;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public void setMessage(String message) {
		this.message = message;
	}
	
	public void setCurrentCycleStudent(boolean currentCycleStudent) {
		this.currentCycleStudent = currentCycleStudent;
	}

	public void setThis_week(List<TimeSpentStudentBean> this_week) {
		this.this_week = this_week;
	}

	public void setCurrent_month(List<TimeSpentStudentBean> current_month) {
		this.current_month = current_month;
	}

	public void setLast_month(List<TimeSpentStudentBean> last_month) {
		this.last_month = last_month;
	}

	public void setSessionAttendanceCountList(List<VideoAndSessionAttendanceCountStudentBean> sessionAttendanceCountList) {
		this.sessionAttendanceCountList = sessionAttendanceCountList;
	}

	public void setTrackDetailsList(List<HashMap<String, String>> trackDetailsList) {
		this.trackDetailsList = trackDetailsList;
	}

	public void setTotalSessionDetails(List<TotalSessionDetailsStudentBean> totalSessionDetails) {
		this.totalSessionDetails = totalSessionDetails;
	}

	public void setTotalVideoDetails(List<TotalVideoDetailsStudentBean> totalVideoDetails) {
		this.totalVideoDetails = totalVideoDetails;
	}

	public void setPdfReadDetailsList(List<Map<String, Object>> pdfReadDetailsList) {
		this.pdfReadDetailsList = pdfReadDetailsList;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("MyActivityResponseStudentBean [status=");
		builder.append(status);
		builder.append(", message=");
		builder.append(message);
		builder.append(", currentCycleStudent=");
		builder.append(currentCycleStudent);
		builder.append(", this_week=");
		builder.append(this_week);
		builder.append(", current_month=");
		builder.append(current_month);
		builder.append(", last_month=");
		builder.append(last_month);
		builder.append(", sessionAttendanceCountList=");
		builder.append(sessionAttendanceCountList);
		builder.append(", trackDetailsList=");
		builder.append(trackDetailsList);
		builder.append(", totalSessionDetails=");
		builder.append(totalSessionDetails);
		builder.append(", totalVideoDetails=");
		builder.append(totalVideoDetails);
		builder.append(", pdfReadDetailsList=");
		builder.append(pdfReadDetailsList);
		builder.append("]");
		return builder.toString();
	}

}