package com.nmims.beans;

import java.io.Serializable;

public class PassFailBean implements Serializable{
	
	private String sapid;
	private String subject;
	private String grno;
	private String writtenYear;
	private String writtenMonth;
	private String assignmentYear;
	private String assignmentMonth;
	private String name;
	private String program;
	private String sem;
	private String writtenscore;
	private String assignmentscore;
	private String total;
	private String failReason;
    private String remarks;
    private String isPass;
	private String gracemarks;
	private double assignmentAttemptOrder;
	private double writtenAttemptOrder;
    private String graceGiven;
    private String assignmentSubmitted;
	private String examMode;
    private String completionYear;
    private String completionMonth;
	private String serviceRequestIdList;
    private String centerCode;
    private int count;
    private String resultProcessedYear;
        
    public int getCount() {
		return count;
	}
	public void setCount(int count) {
		this.count = count;
	}
	public String getCenterCode() {
		return centerCode;
	}
	public void setCenterCode(String centerCode) {
		this.centerCode = centerCode;
	}
	public String getServiceRequestIdList() {
		return serviceRequestIdList;
	}
	public void setServiceRequestIdList(String serviceRequestIdList) {
		this.serviceRequestIdList = serviceRequestIdList;
	}
	public String getCompletionYear() {
		return completionYear;
	}
	public void setCompletionYear(String completionYear) {
		this.completionYear = completionYear;
	}
	public String getCompletionMonth() {
		return completionMonth;
	}
	public void setCompletionMonth(String completionMonth) {
		this.completionMonth = completionMonth;
	}
	public String getExamMode() {
		return examMode;
	}
	public void setExamMode(String examMode) {
		this.examMode = examMode;
	}
	public String getAssignmentSubmitted() {
		return assignmentSubmitted;
	}
	public void setAssignmentSubmitted(String assignmentSubmitted) {
		this.assignmentSubmitted = assignmentSubmitted;
	}
	public String getGraceGiven() {
		return graceGiven;
	}
	public void setGraceGiven(String graceGiven) {
		this.graceGiven = graceGiven;
	}
	public double getWrittenAttemptOrder() {
		return writtenAttemptOrder;
	}
	public void setWrittenAttemptOrder(double writtenAttemptOrder) {
		this.writtenAttemptOrder = writtenAttemptOrder;
	}
	public double getAssignmentAttemptOrder() {
		return assignmentAttemptOrder;
	}
	public void setAssignmentAttemptOrder(double assignmentAttemptOrder) {
		this.assignmentAttemptOrder = assignmentAttemptOrder;
	}
	public String getGracemarks() {
		return gracemarks;
	}
	public void setGracemarks(String gracemarks) {
		this.gracemarks = gracemarks;
	}
	public String getIsPass() {
		return isPass;
	}
	public void setIsPass(String isPass) {
		this.isPass = isPass;
	}
	public String getSapid() {
		return sapid;
	}
	public void setSapid(String sapid) {
		this.sapid = sapid;
	}
	public String getSubject() {
		return subject;
	}
	public void setSubject(String subject) {
		this.subject = subject;
	}
	public String getGrno() {
		return grno;
	}
	public void setGrno(String grno) {
		this.grno = grno;
	}

	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getProgram() {
		return program;
	}
	public void setProgram(String program) {
		this.program = program;
	}
	public String getSem() {
		return sem;
	}
	public void setSem(String sem) {
		this.sem = sem;
	}
	public String getWrittenscore() {
		return writtenscore;
	}
	public void setWrittenscore(String writtenscore) {
		this.writtenscore = writtenscore;
	}
	public String getAssignmentscore() {
		return assignmentscore;
	}
	public void setAssignmentscore(String assignmentscore) {
		this.assignmentscore = assignmentscore;
	}
	public String getTotal() {
		return total;
	}
	public void setTotal(String total) {
		this.total = total;
	}
	public String getFailReason() {
		return failReason;
	}
	public void setFailReason(String failReason) {
		this.failReason = failReason;
	}
	public String getRemarks() {
		return remarks;
	}
	public void setRemarks(String remarks) {
		this.remarks = remarks;
	}
	public String getWrittenYear() {
		return writtenYear;
	}
	public void setWrittenYear(String writtenYear) {
		this.writtenYear = writtenYear;
	}
	public String getWrittenMonth() {
		return writtenMonth;
	}
	public void setWrittenMonth(String writtenMonth) {
		this.writtenMonth = writtenMonth;
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
	public String getResultProcessedYear() {
		return resultProcessedYear;
	}
	public void setResultProcessedYear(String resultProcessedYear) {
		this.resultProcessedYear = resultProcessedYear;
	}
	@Override
	public String toString() {
		return "PassFailBean [sapid=" + sapid + ", subject=" + subject + ", grno=" + grno + ", writtenYear="
				+ writtenYear + ", writtenMonth=" + writtenMonth + ", assignmentYear=" + assignmentYear
				+ ", assignmentMonth=" + assignmentMonth + ", name=" + name + ", program=" + program + ", sem=" + sem
				+ ", writtenscore=" + writtenscore + ", assignmentscore=" + assignmentscore + ", total=" + total
				+ ", failReason=" + failReason + ", remarks=" + remarks + ", isPass=" + isPass + ", gracemarks="
				+ gracemarks + ", assignmentAttemptOrder=" + assignmentAttemptOrder + ", writtenAttemptOrder="
				+ writtenAttemptOrder + ", graceGiven=" + graceGiven + ", assignmentSubmitted=" + assignmentSubmitted
				+ ", examMode=" + examMode + ", completionYear=" + completionYear + ", completionMonth="
				+ completionMonth + ", serviceRequestIdList=" + serviceRequestIdList + ", centerCode=" + centerCode
				+ ", count=" + count + ", resultProcessedYear=" + resultProcessedYear + "]";
	}
	
    
	
}
