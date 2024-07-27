package com.nmims.coursera.beans;

import java.io.Serializable;

public class CourseraStudentSSOBean implements Serializable{

	private String sapid;
	private String firstName;
    private String lastName;
	private String emailId;
	private String error;
	private String status;
	private String learnerURL;
	private String programName;
	public String getSapid() {
		return sapid;
	}
	public void setSapid(String sapid) {
		this.sapid = sapid;
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
	public String getEmailId() {
		return emailId;
	}
	public void setEmailId(String emailId) {
		this.emailId = emailId;
	}
	public String getError() {
		return error;
	}
	public void setError(String error) {
		this.error = error;
	}
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}
	public String getLearnerURL() {
		return learnerURL;
	}
	public void setLearnerURL(String learnerURL) {
		this.learnerURL = learnerURL;
	}
	public String getProgramName() {
		return programName;
	}
	public void setProgramName(String programName) {
		this.programName = programName;
	}
	
	@Override
	public String toString() {
		return "CourseraStudentSSOBean [sapid=" + sapid + ", firstName=" + firstName + ", lastName=" + lastName
				+ ", emailId=" + emailId + ", error=" + error + ", status=" + status + ", learnerURL=" + learnerURL
				+ ", programName=" + programName + "]";
	}
		
}
