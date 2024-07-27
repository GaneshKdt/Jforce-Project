package com.nmims.dto;

public class StudentTimeTableDto {


	private String sapid;
	private String id; 
	private Integer prgmSemSubId; 
	private String date; 
	private String startTime; 
	private String day; 
	private String subject; 
	private String sessionName; 
	private String month; 
	private String year; 
	private String facultyId; 
	private String endTime; 
	private String track; 
	private String firstName; 
	private String lastName; 
	private String isCancelled; 
	private String reasonForCancellation; 
	private String facultyName;
	private String attended;

	public String getSapid() {
		return sapid;
	}
	public void setSapid(String sapid) {
		this.sapid = sapid;
	}
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public Integer getPrgmSemSubId() {
		return prgmSemSubId;
	}
	public void setPrgmSemSubId(Integer prgmSemSubId) {
		this.prgmSemSubId = prgmSemSubId;
	}
	public String getDate() {
		return date;
	}
	public void setDate(String date) {
		this.date = date;
	}
	public String getStartTime() {
		return startTime;
	}
	public void setStartTime(String startTime) {
		this.startTime = startTime;
	}
	public String getDay() {
		return day;
	}
	public void setDay(String day) {
		this.day = day;
	}
	public String getSubject() {
		return subject;
	}
	public void setSubject(String subject) {
		this.subject = subject;
	}
	public String getSessionName() {
		return sessionName;
	}
	public void setSessionName(String sessionName) {
		this.sessionName = sessionName;
	}
	public String getMonth() {
		return month;
	}
	public void setMonth(String month) {
		this.month = month;
	}
	public String getYear() {
		return year;
	}
	public void setYear(String year) {
		this.year = year;
	}
	public String getFacultyId() {
		return facultyId;
	}
	public void setFacultyId(String facultyId) {
		this.facultyId = facultyId;
	}
	public String getEndTime() {
		return endTime;
	}
	public void setEndTime(String endTime) {
		this.endTime = endTime;
	}
	public String getTrack() {
		return track;
	}
	public void setTrack(String track) {
		this.track = track;
	}
	public String getFirstName() {
		return firstName;
	}
	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}
	public String getLastName() {
		return lastName;
	}
	public void setLastName(String lastName) {
		this.lastName = lastName;
	}
	public String getIsCancelled() {
		return isCancelled;
	}
	public void setIsCancelled(String isCancelled) {
		this.isCancelled = isCancelled;
	}
	public String getReasonForCancellation() {
		return reasonForCancellation;
	}
	public void setReasonForCancellation(String reasonForCancellation) {
		this.reasonForCancellation = reasonForCancellation;
	}

	public String getFacultyName() {
		return facultyName;
	}
	public void setFacultyName(String facultyName) {
		this.facultyName = facultyName;
	}
	
	
	public String getAttended() {
		return attended;
	}
	public void setAttended(String attended) {
		this.attended = attended;
	}
	@Override
	public String toString() {
		return "StudentTimeTableDto [sapid=" + sapid + ", id=" + id + ", prgmSemSubId=" + prgmSemSubId + ", date="
				+ date + ", startTime=" + startTime + ", day=" + day + ", subject=" + subject + ", sessionName="
				+ sessionName + ", month=" + month + ", year=" + year + ", facultyId=" + facultyId + ", endTime="
				+ endTime + ", track=" + track + ", firstName=" + firstName + ", lastName=" + lastName
				+ ", isCancelled=" + isCancelled + ", reasonForCancellation=" + reasonForCancellation + ", facultyName="
				+ facultyName + "]";
	}



}
