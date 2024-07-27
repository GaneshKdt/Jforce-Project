package com.nmims.beans;

import java.io.Serializable;

public class ExamOrderCareerservicesBean  implements Serializable{
	private String acadMonth;
	private String month;
	private String year;
	private String order;
	private String live;
	private String timeTableLive;
	private String acadSessionLive;
	private String assignmentMarksLive;
	private String acadContentLive;
	private String oflineResultslive;
	private String assignmentLive;
	private String resitAssignmentLive;
	private String projectSubmissionLive;
	private String declareDate;
	private String oflineResultsDeclareDate;
	private String forumLive;
	
	
	
	public String getForumLive() {
		return forumLive;
	}
	public void setForumLive(String forumLive) {
		this.forumLive = forumLive;
	}
	public String getDeclareDate() {
		return declareDate;
	}
	public void setDeclareDate(String declareDate) {
		this.declareDate = declareDate;
	}
	public String getOflineResultsDeclareDate() {
		return oflineResultsDeclareDate;
	}
	public void setOflineResultsDeclareDate(String oflineResultsDeclareDate) {
		this.oflineResultsDeclareDate = oflineResultsDeclareDate;
	}
	public String getResitAssignmentLive() {
		return resitAssignmentLive;
	}
	public void setResitAssignmentLive(String resitAssignmentLive) {
		this.resitAssignmentLive = resitAssignmentLive;
	}
	public String getProjectSubmissionLive() {
		return projectSubmissionLive;
	}
	public void setProjectSubmissionLive(String projectSubmissionLive) {
		this.projectSubmissionLive = projectSubmissionLive;
	}
	public String getAssignmentLive() {
		return assignmentLive;
	}
	public void setAssignmentLive(String assignmentLive) {
		this.assignmentLive = assignmentLive;
	}
	public String getOflineResultslive() {
		return oflineResultslive;
	}
	public void setOflineResultslive(String oflineResultslive) {
		this.oflineResultslive = oflineResultslive;
	}
	public String getAssignmentMarksLive() {
		return assignmentMarksLive;
	}
	public void setAssignmentMarksLive(String assignmentMarksLive) {
		this.assignmentMarksLive = assignmentMarksLive;
	}
	public String getAcadContentLive() {
		return acadContentLive;
	}
	public void setAcadContentLive(String acadContentLive) {
		this.acadContentLive = acadContentLive;
	}
	public String getAcadSessionLive() {
		return acadSessionLive;
	}
	public void setAcadSessionLive(String acadSessionLive) {
		this.acadSessionLive = acadSessionLive;
	}
	public String getAcadMonth() {
		return acadMonth;
	}
	public void setAcadMonth(String acadMonth) {
		this.acadMonth = acadMonth;
	}
	public String getTimeTableLive() {
		return timeTableLive;
	}
	public void setTimeTableLive(String timeTableLive) {
		this.timeTableLive = timeTableLive;
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
	public String getOrder() {
		return order;
	}
	public void setOrder(String order) {
		this.order = order;
	}
	public String getLive() {
		return live;
	}
	public void setLive(String live) {
		this.live = live;
	}
	@Override
	public String toString() {
		return "ExamOrderBean [acadMonth=" + acadMonth + ", month=" + month
				+ ", year=" + year + ", order=" + order + ", live=" + live
				+ ", timeTableLive=" + timeTableLive + ", acadSessionLive="
				+ acadSessionLive + ", assignmentMarksLive="
				+ assignmentMarksLive + ", acadContentLive=" + acadContentLive
				+ ", oflineResultslive=" + oflineResultslive
				+ ", assignmentLive=" + assignmentLive
				+ ", resitAssignmentLive=" + resitAssignmentLive
				+ ", projectSubmissionLive=" + projectSubmissionLive
				+ ", declareDate=" + declareDate
				+ ", oflineResultsDeclareDate=" + oflineResultsDeclareDate
				+ ", forumLive=" + forumLive + "]";
	}
	
	
}
