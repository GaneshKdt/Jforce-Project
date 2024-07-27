package com.nmims.beans;

import java.io.Serializable;

public class ProjectModuleLiveSettings  implements Serializable {

	private String acadYear;
	private String acadMonth;
	private String sapid;
	private String subject;
	private String programSemSubjId;

	private String moduleType;
	
	private boolean isLive;
	
	private String startDate;
	private String endDate;

	private String error;

	public String getAcadYear() {
		return acadYear;
	}
	public void setAcadYear(String acadYear) {
		this.acadYear = acadYear;
	}
	public String getAcadMonth() {
		return acadMonth;
	}
	public void setAcadMonth(String acadMonth) {
		this.acadMonth = acadMonth;
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
	public String getProgramSemSubjId() {
		return programSemSubjId;
	}
	public void setProgramSemSubjId(String programSemSubjId) {
		this.programSemSubjId = programSemSubjId;
	}
	public String getModuleType() {
		return moduleType;
	}
	public void setModuleType(String moduleType) {
		this.moduleType = moduleType;
	}
	public boolean isLive() {
		return isLive;
	}
	public void setLive(boolean isLive) {
		this.isLive = isLive;
	}
	public String getStartDate() {
		return startDate;
	}
	public void setStartDate(String startDate) {
		this.startDate = startDate;
	}
	public String getEndDate() {
		return endDate;
	}
	public void setEndDate(String endDate) {
		this.endDate = endDate;
	}
	public String getError() {
		return error;
	}
	public void setError(String error) {
		this.error = error;
	}
	@Override
	public String toString() {
		return "ProjectModuleLiveSettings [acadYear=" + acadYear + ", acadMonth=" + acadMonth + ", sapid=" + sapid
				+ ", subject=" + subject + ", programSemSubjId=" + programSemSubjId + ", moduleType=" + moduleType
				+ ", isLive=" + isLive + ", startDate=" + startDate + ", endDate=" + endDate + ", error=" + error + "]";
	}
}
