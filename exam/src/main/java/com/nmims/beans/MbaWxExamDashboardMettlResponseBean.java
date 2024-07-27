package com.nmims.beans;

public class MbaWxExamDashboardMettlResponseBean {
	private String mettl_started;
	private String mettl_completed;
	private String no_action;
	private String portal_started;
	private String programType;
	private String examType;
	private String examDate;
	private String examTime;
	
	
	
	public String getExamTime() {
		return examTime;
	}
	public void setExamTime(String examTime) {
		this.examTime = examTime;
	}
	public String getMettl_started() {
		return mettl_started;
	}
	public void setMettl_started(String mettl_started) {
		this.mettl_started = mettl_started;
	}
	public String getMettl_completed() {
		return mettl_completed;
	}
	public void setMettl_completed(String mettl_completed) {
		this.mettl_completed = mettl_completed;
	}
	public String getNo_action() {
		return no_action;
	}
	public void setNo_action(String no_action) {
		this.no_action = no_action;
	}
	public String getPortal_started() {
		return portal_started;
	}
	public void setPortal_started(String portal_started) {
		this.portal_started = portal_started;
	}
	public String getProgramType() {
		return programType;
	}
	public void setProgramType(String programType) {
		this.programType = programType;
	}
	public String getExamType() {
		return examType;
	}
	public void setExamType(String examType) {
		this.examType = examType;
	}
	public String getExamDate() {
		return examDate;
	}
	public void setExamDate(String examDate) {
		this.examDate = examDate;
	}
	@Override
	public String toString() {
		return "MbaWxExamDashboardMettlResponseBean [mettl_started=" + mettl_started + ", mettl_completed="
				+ mettl_completed + ", no_action=" + no_action + ", portal_started=" + portal_started + ", programType="
				+ programType + ", examType=" + examType + ", examDate=" + examDate + "]";
	}
}
