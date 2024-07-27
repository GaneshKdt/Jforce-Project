package com.nmims.coursera.beans;

import java.io.Serializable;

public class CourseraSSODetails implements Serializable{

	private String destinationURL;
	private String ID;
	private String metadata;
	private String error;
	private String audience;
	private StudentCourseraBean studentInfo;
	
	public String getDestinationURL() {
		return destinationURL;
	}
	public void setDestinationURL(String destinationURL) {
		this.destinationURL = destinationURL;
	}
	public String getID() {
		return ID;
	}
	public void setID(String iD) {
		ID = iD;
	}
	public String getMetadata() {
		return metadata;
	}
	public void setMetadata(String metadata) {
		this.metadata = metadata;
	}
	public String getError() {
		return error;
	}
	public void setError(String error) {
		this.error = error;
	}
	public String getAudience() {
		return audience;
	}
	public void setAudience(String audience) {
		this.audience = audience;
	}
	public StudentCourseraBean getStudentInfo() {
		return studentInfo;
	}
	public void setStudentInfo(StudentCourseraBean studentInfo) {
		this.studentInfo = studentInfo;
	}
	
	
	@Override
	public String toString() {
		return "CourseraSSODetails [destinationURL=" + destinationURL + ", ID=" + ID + ", metadata=" + metadata
				+ ", error=" + error + ", audience=" + audience + ", studentInfo=" + studentInfo + "]";
	}
	

}
