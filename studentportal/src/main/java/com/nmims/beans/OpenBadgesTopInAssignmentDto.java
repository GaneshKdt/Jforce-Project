package com.nmims.beans;

public class OpenBadgesTopInAssignmentDto {
	private Integer userId;	
	private String sapid;
	private Integer rank;
	private String subjectname;
	private String submissionDate;
	private Integer assignmentscore;
	private String assignmentYear;
	private String assignmentMonth;
	private String acadMonth ;
	private Integer acadYear;
	private String examMonth; 
	private Integer examYear;
	
	
	public Integer getUserId() {
		return userId;
	}
	public void setUserId(Integer userId) {
		this.userId = userId;
	}
	public String getSapid() {
		return sapid;
	}
	public void setSapid(String sapid) {
		this.sapid = sapid;
	}
	public Integer getRank() {
		return rank;
	}
	public void setRank(Integer rank) {
		this.rank = rank;
	}
	public String getSubjectname() {
		return subjectname;
	}
	public void setSubjectname(String subjectname) {
		this.subjectname = subjectname;
	}
	public Integer getAssignmentscore() {
		return assignmentscore;
	}
	public void setAssignmentscore(Integer assignmentscore) {
		this.assignmentscore = assignmentscore;
	}
	public String getAssignmentYear() {
		return assignmentYear;
	}
	public void setAssignmentYear(String assignmentYear) {
		this.assignmentYear = assignmentYear;
	}
	public String getAssignmentMonth() {
		return assignmentMonth;
	}
	public void setAssignmentMonth(String assignmentMonth) {
		this.assignmentMonth = assignmentMonth;
	}
	public String getSubmissionDate() {
		return submissionDate;
	}
	public void setSubmissionDate(String submissionDate) {
		this.submissionDate = submissionDate;
	}
	public String getAcadMonth() {
		return acadMonth;
	}
	public void setAcadMonth(String acadMonth) {
		this.acadMonth = acadMonth;
	}
	public Integer getAcadYear() {
		return acadYear;
	}
	public void setAcadYear(Integer acadYear) {
		this.acadYear = acadYear;
	}
	public String getExamMonth() {
		return examMonth;
	}
	public void setExamMonth(String examMonth) {
		this.examMonth = examMonth;
	}
	public Integer getExamYear() {
		return examYear;
	}
	public void setExamYear(Integer examYear) {
		this.examYear = examYear;
	}
	@Override
	public String toString() {
		return "OpenBadgesTopInAssignmentDto [userId=" + userId + ", sapid=" + sapid + ", rank=" + rank
				+ ", subjectname=" + subjectname + ", submissionDate=" + submissionDate + ", assignmentscore="
				+ assignmentscore + ", assignmentYear=" + assignmentYear + ", assignmentMonth=" + assignmentMonth + "]";
	}
	
	
	
}
