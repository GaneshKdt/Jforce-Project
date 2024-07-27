package com.nmims.beans;

public class OpenBadgesAppearedForTEEBean {

	private double examOrder;
	private Integer marksYear;
	private String marksMonth;
	private String sapId;
	private String subject;
	private String examDate;
	private String examTime;
	private String acadMonth;
	
	public String getAcadMonth() {
		return acadMonth;
	}

	public void setAcadMonth(String acadMonth) {
		this.acadMonth = acadMonth;
	}

	private Integer userId;
	private Integer consumerProgramStructureId;

	public OpenBadgesAppearedForTEEBean() {
		super();
	}

	public double getExamOrder() {
		return examOrder;
	}

	public void setExamOrder(double examOrder) {
		this.examOrder = examOrder;
	}

	public Integer getMarksYear() {
		return marksYear;
	}

	public void setMarksYear(Integer marksYear) {
		this.marksYear = marksYear;
	}

	public String getMarksMonth() {
		return marksMonth;
	}

	public void setMarksMonth(String marksMonth) {
		this.marksMonth = marksMonth;
	}

	public String getSapId() {
		return sapId;
	}

	public void setSapId(String sapId) {
		this.sapId = sapId;
	}

	public String getSubject() {
		return subject;
	}

	public void setSubject(String subject) {
		this.subject = subject;
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

	public Integer getUserId() {
		return userId;
	}

	public void setUserId(Integer userId) {
		this.userId = userId;
	}

	public Integer getConsumerProgramStructureId() {
		return consumerProgramStructureId;
	}

	public void setConsumerProgramStructureId(Integer consumerProgramStructureId) {
		this.consumerProgramStructureId = consumerProgramStructureId;
	}

	@Override
	public String toString() {
		return "OpenBadgesAppearedForTEEBean [examOrder=" + examOrder + ", marksYear=" + marksYear + ", marksMonth="
				+ marksMonth + ", sapId=" + sapId + ", subject=" + subject + ", examDate=" + examDate + ", examTime="
				+ examTime + ", acadMonth=" + acadMonth + ", userId=" + userId + ", consumerProgramStructureId="
				+ consumerProgramStructureId + "]";
	}

}
