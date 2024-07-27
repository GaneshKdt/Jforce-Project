package com.nmims.beans;

import java.io.Serializable;

/**
 * 
 * @author shivam.pandey.EXT
 *
 */
public class RescheduledCancelledSlotChangeReportBean implements Serializable{

	/*Variables*/
	private String sapid;
	private String examDate;
	private String examTime;
	private String program;
	private String subject;
	private String tranStatus;
	private String booked;
	private String createdBy;
	private String createdDate;
	private String cancelStatus;
	private String ic;
	private String lc;
	private String centerCode;
	

	/*Getters and Setters*/
	public String getSapid() {
		return sapid;
	}
	public void setSapid(String sapid) {
		this.sapid = sapid;
	}
	public String getExamDate() {
		return examDate;
	}
	public void setExamDate(String examDate) {
		this.examDate = examDate;
	}
	public String getExamTime() {
		return examTime;
	}
	public void setExamTime(String examTime) {
		this.examTime = examTime;
	}
	public String getSubject() {
		return subject;
	}
	public void setSubject(String subject) {
		this.subject = subject;
	}
	public String getIc() {
		return ic;
	}
	public void setIc(String ic) {
		this.ic = ic;
	}
	public String getLc() {
		return lc;
	}
	public void setLc(String lc) {
		this.lc = lc;
	}
	public String getTranStatus() {
		return tranStatus;
	}
	public void setTranStatus(String tranStatus) {
		this.tranStatus = tranStatus;
	}
	public String getBooked() {
		return booked;
	}
	public void setBooked(String booked) {
		this.booked = booked;
	}
	public String getCancelStatus() {
		return cancelStatus;
	}
	public void setCancelStatus(String cancelStatus) {
		this.cancelStatus = cancelStatus;
	}
	public String getCenterCode() {
		return centerCode;
	}
	public void setCenterCode(String centerCode) {
		this.centerCode = centerCode;
	}
	public String getCreatedBy() {
		return createdBy;
	}
	public void setCreatedBy(String createdBy) {
		this.createdBy = createdBy;
	}
	public String getCreatedDate() {
		return createdDate;
	}
	public void setCreatedDate(String createdDate) {
		this.createdDate = createdDate;
	}
	public String getProgram() {
		return program;
	}
	public void setProgram(String program) {
		this.program = program;
	}
	
	
	/*To String*/
	@Override
	public String toString() {
		return "RescheduledCancelledSlotChangeReportBean [sapid=" + sapid + ", examDate=" + examDate + ", examTime="
				+ examTime + ", subject=" + subject + ", tranStatus=" + tranStatus + ", booked=" + booked
				+ ", createdDate=" + createdDate + ", cancelStatus=" + cancelStatus + ", ic=" + ic + ", lc="
				+ lc + ", createdDate=" + createdDate + ", program=" + program +"]";
	}
	
}
