package com.nmims.beans;

import java.io.Serializable;
import java.util.Date;

public class UserViewedWebinar implements Serializable {

	private SessionDayTimeBean sessionDetails;
	private String uid;
	private String purchaseId;
	private String activationId;
	private String webinarId;
	private String attendanceFeedbackId;
	private Date consumptionDate;
	private Date lastViewedOnDate;
	private Date date;
	private String duration;
	private String startTime;
	private String endTime;
	private String packageName;
	private String facultyName;
	private String webinarName;
	private String webinarInfoPageURL;
	
	public String getFacultyName() {
		return facultyName;
	}
	public void setFacultyName(String facultyName) {
		this.facultyName = facultyName;
	}
	public String getWebinarName() {
		return webinarName;
	}
	public void setWebinarName(String webinarName) {
		this.webinarName = webinarName;
	}
	public String getWebinarInfoPageURL() {
		return webinarInfoPageURL;
	}
	public void setWebinarInfoPageURL(String webinarInfoPageURL) {
		this.webinarInfoPageURL = webinarInfoPageURL;
	}
	public Date getLastViewedOnDate() {
		return lastViewedOnDate;
	}
	public void setLastViewedOnDate(Date lastViewedOnDate) {
		this.lastViewedOnDate = lastViewedOnDate;
	}
	public Date getDate() {
		return date;
	}
	public void setDate(Date date) {
		this.date = date;
	}
	public String getDuration() {
		return duration;
	}
	public void setDuration(String duration) {
		this.duration = duration;
	}
	public String getStartTime() {
		return startTime;
	}
	public void setStartTime(String startTime) {
		this.startTime = startTime;
	}
	public String getEndTime() {
		return endTime;
	}
	public void setEndTime(String endTime) {
		this.endTime = endTime;
	}
	public String getPackageName() {
		return packageName;
	}
	public void setPackageName(String packageName) {
		this.packageName = packageName;
	}
	public SessionDayTimeBean getSessionDetails() {
		return sessionDetails;
	}
	public void setSessionDetails(SessionDayTimeBean sessionDetails) {
		this.sessionDetails = sessionDetails;
	}
	public String getUid() {
		return uid;
	}
	public void setUid(String uid) {
		this.uid = uid;
	}
	public String getPurchaseId() {
		return purchaseId;
	}
	public void setPurchaseId(String purchaseId) {
		this.purchaseId = purchaseId;
	}
	public String getWebinarId() {
		return webinarId;
	}
	public void setWebinarId(String webinarId) {
		this.webinarId = webinarId;
	}
	public String getAttendanceFeedbackId() {
		return attendanceFeedbackId;
	}
	public void setAttendanceFeedbackId(String attendanceFeedbackId) {
		this.attendanceFeedbackId = attendanceFeedbackId;
	}
	public Date getConsumptionDate() {
		return consumptionDate;
	}
	public void setConsumptionDate(Date consumptionDate) {
		this.consumptionDate = consumptionDate;
	}
	public String getActivationId() {
		return activationId;
	}
	public void setActivationId(String activationId) {
		this.activationId = activationId;
	}
}
