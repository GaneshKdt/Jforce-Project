package com.nmims.beans;

public class CustomCourseWaiverDTO {
	
	private int id;
	private int subjectCodeId;
	private int pssId;
	private String subjectName;
	private int sem;
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
	
	

}
