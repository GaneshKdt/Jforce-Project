package com.nmims.coursera.dto;

public class StudentCourseraDto {
	
	private  boolean isApplicable;
	private  boolean isPaid;
	private String learnerURL;
	
	public boolean isApplicable() {
		return isApplicable;
	}
	public void setApplicable(boolean isApplicable) {
		this.isApplicable = isApplicable;
	}
	public boolean isPaid() {
		return isPaid;
	}
	public void setPaid(boolean isPaid) {
		this.isPaid = isPaid;
	}
	public String getLearnerURL() {
		return learnerURL;
	}
	public void setLearnerURL(String learnerURL) {
		this.learnerURL = learnerURL;
	}
	
	@Override
	public String toString() {
		return "StudentCourseraDto [isApplicable=" + isApplicable + ", isPaid=" + isPaid + ", learnerURL=" + learnerURL + "]";
	}
}
