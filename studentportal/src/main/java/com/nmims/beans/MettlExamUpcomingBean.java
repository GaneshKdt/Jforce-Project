package com.nmims.beans;

import java.io.Serializable;

public class MettlExamUpcomingBean implements Serializable {
	
	private String subject;

	private String month;
	private String trackId; 
	private String sapid; 
	private String testTaken; 
	private String firstname;
	private String lastname; 
	private String emailId; 
	private String examStartDateTime;  
	private String examEndDateTime;  
	private String reporting_start_date_time;
	private String reporting_finish_date_time;
	
	public MettlExamUpcomingBean() {
		super();
	}
	public String getAccessStartDateTime() {
		return accessStartDateTime;
	}
	public void setAccessStartDateTime(String accessStartDateTime) {
		this.accessStartDateTime = accessStartDateTime;
	}
	public String getAccessEndDateTime() {
		return accessEndDateTime;
	}
	public void setAccessEndDateTime(String accessEndDateTime) {
		this.accessEndDateTime = accessEndDateTime;
	}
	private String accessStartDateTime;  
	private String accessEndDateTime;  
	private String sifySubjectCode;  
	private String scheduleName;  
	private String acessKey; 
	private String joinURL;
	private String imageURL;
	private String scheduleId;
	private String year;
	
	public String getSubject() {
		return subject;
	}
	public void setSubject(String subject) {
		this.subject = subject;
	}
	public String getMonth() {
		return month;
	}
	public void setMonth(String month) {
		this.month = month;
	}
	public String getTrackId() {
		return trackId;
	}
	public void setTrackId(String trackId) {
		this.trackId = trackId;
	}
	public String getSapid() {
		return sapid;
	}
	public void setSapid(String sapid) {
		this.sapid = sapid;
	}
	public String getTestTaken() {
		return testTaken;
	}
	public void setTestTaken(String testTaken) {
		this.testTaken = testTaken;
	}
	public String getFirstname() {
		return firstname;
	}
	public void setFirstname(String firstname) {
		this.firstname = firstname;
	}
	public String getLastname() {
		return lastname;
	}
	public void setLastname(String lastname) {
		this.lastname = lastname;
	}
	public String getEmailId() {
		return emailId;
	}
	public void setEmailId(String emailId) {
		this.emailId = emailId;
	}
	public String getExamStartDateTime() {
		return examStartDateTime;
	}
	public void setExamStartDateTime(String examStartDateTime) {
		this.examStartDateTime = examStartDateTime;
	}
	public String getExamEndDateTime() {
		return examEndDateTime;
	}
	public void setExamEndDateTime(String examEndDateTime) {
		this.examEndDateTime = examEndDateTime;
	}

	public String getSifySubjectCode() {
		return sifySubjectCode;
	}
	public void setSifySubjectCode(String sifySubjectCode) {
		this.sifySubjectCode = sifySubjectCode;
	}
	public String getScheduleName() {
		return scheduleName;
	}
	public void setScheduleName(String scheduleName) {
		this.scheduleName = scheduleName;
	}
	public String getAcessKey() {
		return acessKey;
	}
	public void setAcessKey(String acessKey) {
		this.acessKey = acessKey;
	}
	public String getJoinURL() {
		return joinURL;
	}
	public void setJoinURL(String joinURL) {
		this.joinURL = joinURL;
	}
	public String getImageURL() {
		return imageURL;
	}
	public void setImageURL(String imageURL) {
		this.imageURL = imageURL;
	}
	public String getScheduleId() {
		return scheduleId;
	}
	public void setScheduleId(String scheduleId) {
		this.scheduleId = scheduleId;
	}
	public String getYear() {
		return year;
	}
	public void setYear(String year) {
		this.year = year;
	}
	public String getReporting_start_date_time() {
		return reporting_start_date_time;
	}
	public void setReporting_start_date_time(String reporting_start_date_time) {
		this.reporting_start_date_time = reporting_start_date_time;
	}
	public String getReporting_finish_date_time() {
		return reporting_finish_date_time;
	}
	public void setReporting_finish_date_time(String reporting_finish_date_time) {
		this.reporting_finish_date_time = reporting_finish_date_time;
	}
	
}
