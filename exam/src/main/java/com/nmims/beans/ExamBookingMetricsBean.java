package com.nmims.beans;

import java.io.Serializable;

public class ExamBookingMetricsBean extends BaseExamBean implements Serializable{

	private String lc;
	private String centerName;
	private String centerCode;
	private int noOfCurrentDrivePendingStudents;
	private int noOfFailedPendingStudents;
	private int noOfCurrentDrivePendingSubjects;
	private int noOfFailedPendingSubejcts;
	public String getLc() {
		return lc;
	}
	public void setLc(String lc) {
		this.lc = lc;
	}
	public String getCenterName() {
		return centerName;
	}
	public void setCenterName(String centerName) {
		this.centerName = centerName;
	}
	public String getCenterCode() {
		return centerCode;
	}
	public void setCenterCode(String centerCode) {
		this.centerCode = centerCode;
	}
	public int getNoOfCurrentDrivePendingStudents() {
		return noOfCurrentDrivePendingStudents;
	}
	public void setNoOfCurrentDrivePendingStudents(
			int noOfCurrentDrivePendingStudents) {
		this.noOfCurrentDrivePendingStudents = noOfCurrentDrivePendingStudents;
	}
	public int getNoOfFailedPendingStudents() {
		return noOfFailedPendingStudents;
	}
	public void setNoOfFailedPendingStudents(int noOfFailedPendingStudents) {
		this.noOfFailedPendingStudents = noOfFailedPendingStudents;
	}
	public int getNoOfCurrentDrivePendingSubjects() {
		return noOfCurrentDrivePendingSubjects;
	}
	public void setNoOfCurrentDrivePendingSubjects(
			int noOfCurrentDrivePendingSubjects) {
		this.noOfCurrentDrivePendingSubjects = noOfCurrentDrivePendingSubjects;
	}
	public int getNoOfFailedPendingSubejcts() {
		return noOfFailedPendingSubejcts;
	}
	public void setNoOfFailedPendingSubejcts(int noOfFailedPendingSubejcts) {
		this.noOfFailedPendingSubejcts = noOfFailedPendingSubejcts;
	}
	
	
	
	
}
