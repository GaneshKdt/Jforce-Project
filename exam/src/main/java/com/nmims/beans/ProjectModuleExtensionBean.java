package com.nmims.beans;

import java.io.Serializable;

public class ProjectModuleExtensionBean  implements Serializable  {

	private String id;
	private String examYear;
	private String examMonth;

	private String sapid;
	private String subject;
	private String programSemSubjId;

	public String getProgramSemSubjId() {
		return programSemSubjId;
	}

	public void setProgramSemSubjId(String programSemSubjId) {
		this.programSemSubjId = programSemSubjId;
	}

	private String firstname;
	private String lastname;
	
	private String moduleType;
	private String endDate;

	private String createdBy;
	private String updatedBy;

	private String error;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getExamYear() {
		return examYear;
	}

	public void setExamYear(String examYear) {
		this.examYear = examYear;
	}

	public String getExamMonth() {
		return examMonth;
	}

	public void setExamMonth(String examMonth) {
		this.examMonth = examMonth;
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

	public String getFirstname() {
		return firstname;
	}

	public void setFirstname(String firstname) {
		this.firstname = firstname;
	}

	public String getLastname() {
		return lastname;
	}

	public void setLastname(String lastname) {
		this.lastname = lastname;
	}

	public String getModuleType() {
		return moduleType;
	}

	public void setModuleType(String moduleType) {
		this.moduleType = moduleType;
	}

	public String getEndDate() {
		return endDate;
	}

	public void setEndDate(String endDate) {
		this.endDate = endDate;
	}

	public String getCreatedBy() {
		return createdBy;
	}

	public void setCreatedBy(String createdBy) {
		this.createdBy = createdBy;
	}

	public String getUpdatedBy() {
		return updatedBy;
	}

	public void setUpdatedBy(String updatedBy) {
		this.updatedBy = updatedBy;
	}

	public String getError() {
		return error;
	}

	public void setError(String error) {
		this.error = error;
	}

	@Override
	public String toString() {
		return "ProjectModuleExtensionBean [id=" + id + ", examYear=" + examYear + ", examMonth=" + examMonth
				+ ", sapid=" + sapid + ", subject=" + subject + ", programSemSubjId=" + programSemSubjId
				+ ", firstname=" + firstname + ", lastname=" + lastname + ", moduleType=" + moduleType + ", endDate="
				+ endDate + ", createdBy=" + createdBy + ", updatedBy=" + updatedBy + ", error=" + error + "]";
	}
}
