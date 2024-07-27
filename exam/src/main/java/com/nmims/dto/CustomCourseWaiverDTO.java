package com.nmims.dto;

public class CustomCourseWaiverDTO {

	private int id;
	private int subjectCodeId;
	private int pssId;
	private String subjectName;
	private int currentSem;
	private int sem;
	private long sapid ;
	private String courseWaiver;
	private String month;
	private String year;
	
	
	
	
	
	public int getCurrentSem() {
		return currentSem;
	}
	public void setCurrentSem(int currentSem) {
		this.currentSem = currentSem;
	}
	public String getCourseWaiver() {
		return courseWaiver;
	}
	public void setCourseWaiver(String courseWaiver) {
		this.courseWaiver = courseWaiver;
	}
	public long getSapid() {
		return sapid;
	}
	public void setSapid(long sapid) {
		this.sapid = sapid;
	}
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public int getSubjectCodeId() {
		return subjectCodeId;
	}
	public void setSubjectCodeId(int subjectCodeId) {
		this.subjectCodeId = subjectCodeId;
	}
	public int getPssId() {
		return pssId;
	}
	public void setPssId(int pssId) {
		this.pssId = pssId;
	}
	public String getSubjectName() {
		return subjectName;
	}
	public void setSubjectName(String subjectName) {
		this.subjectName = subjectName;
	}
	public int getSem() {
		return sem;
	}
	public void setSem(int sem) {
		this.sem = sem;
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
	@Override
	public String toString() {
		return "CustomCourseWaiverDTO [id=" + id + ", subjectCodeId=" + subjectCodeId + ", pssId=" + pssId
				+ ", subjectName=" + subjectName + ", currentSem=" + currentSem + ", sem=" + sem + ", sapid=" + sapid
				+ ", courseWaiver=" + courseWaiver + ", month=" + month + ", year=" + year + "]";
	}
	
	
	
	
	
	
	

}
