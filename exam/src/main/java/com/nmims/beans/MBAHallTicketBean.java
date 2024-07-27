package com.nmims.beans;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

public class MBAHallTicketBean  implements Serializable  {

	private String sapid;
	private StudentExamBean student;
	
	private boolean htDownloadStatus;
	private boolean corporateCenterUserMapping;
	
	private String subjectDoubleBookingMap;
	
	private String passwordPresent;
	private String passwordAbsent;
	private String password;
	
	private String title;
	private String location;
	private String examination;
	private String programFullName;

	private String year;
	private String month;
	
	private List<TimetableBean> timeTableList;
    private Map<String, MBAExamBookingRequest> subjectBookingMap;
    private List<MBAExamBookingRequest> examBookings;
    
	private String errorMessage;
	private String status;

	private String downloadURL;
	private String fileName;
	
	public String getSapid() {
		return sapid;
	}
	public void setSapid(String sapid) {
		this.sapid = sapid;
	}
	public StudentExamBean getStudent() {
		return student;
	}
	public void setStudent(StudentExamBean student) {
		this.student = student;
	}
	public boolean isHtDownloadStatus() {
		return htDownloadStatus;
	}
	public void setHtDownloadStatus(boolean htDownloadStatus) {
		this.htDownloadStatus = htDownloadStatus;
	}
	public String getSubjectDoubleBookingMap() {
		return subjectDoubleBookingMap;
	}
	public void setSubjectDoubleBookingMap(String subjectDoubleBookingMap) {
		this.subjectDoubleBookingMap = subjectDoubleBookingMap;
	}
	public String getPasswordPresent() {
		return passwordPresent;
	}
	public void setPasswordPresent(String passwordPresent) {
		this.passwordPresent = passwordPresent;
	}
	public String getPasswordAbsent() {
		return passwordAbsent;
	}
	public void setPasswordAbsent(String passwordAbsent) {
		this.passwordAbsent = passwordAbsent;
	}
	public String getPassword() {
		return password;
	}
	public void setPassword(String password) {
		this.password = password;
	}
	public boolean isCorporateCenterUserMapping() {
		return corporateCenterUserMapping;
	}
	public void setCorporateCenterUserMapping(boolean corporateCenterUserMapping) {
		this.corporateCenterUserMapping = corporateCenterUserMapping;
	}
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	public String getLocation() {
		return location;
	}
	public void setLocation(String location) {
		this.location = location;
	}
	public String getExamination() {
		return examination;
	}
	public void setExamination(String examination) {
		this.examination = examination;
	}
	public String getProgramFullName() {
		return programFullName;
	}
	public void setProgramFullName(String programFullName) {
		this.programFullName = programFullName;
	}
	public String getYear() {
		return year;
	}
	public void setYear(String year) {
		this.year = year;
	}
	public String getMonth() {
		return month;
	}
	public void setMonth(String month) {
		this.month = month;
	}
	public List<TimetableBean> getTimeTableList() {
		return timeTableList;
	}
	public void setTimeTableList(List<TimetableBean> timeTableList) {
		this.timeTableList = timeTableList;
	}
	public Map<String, MBAExamBookingRequest> getSubjectBookingMap() {
		return subjectBookingMap;
	}
	public void setSubjectBookingMap(Map<String, MBAExamBookingRequest> subjectBookingMap) {
		this.subjectBookingMap = subjectBookingMap;
	}
	public List<MBAExamBookingRequest> getExamBookings() {
		return examBookings;
	}
	public void setExamBookings(List<MBAExamBookingRequest> examBookings) {
		this.examBookings = examBookings;
	}
	public String getErrorMessage() {
		return errorMessage;
	}
	public void setErrorMessage(String errorMessage) {
		this.errorMessage = errorMessage;
	}
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}
	@Override
	public String toString() {
		return "MBAXHallTicketBean [sapid=" + sapid + ", student=" + student + ", htDownloadStatus=" + htDownloadStatus
				+ ", subjectDoubleBookingMap=" + subjectDoubleBookingMap + ", passwordPresent=" + passwordPresent
				+ ", passwordAbsent=" + passwordAbsent + ", password=" + password + ", corporateCenterUserMapping="
				+ corporateCenterUserMapping + ", title=" + title + ", location=" + location + ", examination="
				+ examination + ", programFullName=" + programFullName + ", year=" + year + ", month=" + month
				+ ", timeTableList=" + timeTableList + ", subjectBookingMap=" + subjectBookingMap + ", examBookings="
				+ examBookings + ", errorMessage=" + errorMessage + ", status=" + status + "]";
	}
	public String getDownloadURL() {
		return downloadURL;
	}
	public void setDownloadURL(String downloadURL) {
		this.downloadURL = downloadURL;
	}
	public String getFileName() {
		return fileName;
	}
	public void setFileName(String fileName) {
		this.fileName = fileName;
	}
}
