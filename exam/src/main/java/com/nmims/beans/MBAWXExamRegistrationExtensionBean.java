package com.nmims.beans;

import java.util.Arrays;

public class MBAWXExamRegistrationExtensionBean {

	private String sapid;
	private String program;
	private String extendStartDateTime;
	private String extendEndDateTime;
	private String createdDate;
	private String createdBy;
	private String examMonth;
	private String examYear;
	private String lastModifiedDate;
	private String lastModifiedBy;
	private String consumerProgramStructureId;
	private String sapIds[];
	private String active;

	public String getSapid() {
		return sapid;
	}

	public String getProgram() {
		return program;
	}

	public String getExtendStartDateTime() {
		return extendStartDateTime;
	}

	public String getExtendEndDateTime() {
		return extendEndDateTime;
	}

	public void setSapid(String sapid) {
		this.sapid = sapid;
	}

	public void setProgram(String program) {
		this.program = program;
	}

	public void setExtendStartDateTime(String extendStartDateTime) {
		this.extendStartDateTime = extendStartDateTime;
	}

	public void setExtendEndDateTime(String extendEndDateTime) {
		this.extendEndDateTime = extendEndDateTime;
	}

	public String getExamMonth() {
		return examMonth;
	}

	public String getExamYear() {
		return examYear;
	}

	public void setExamMonth(String examMonth) {
		this.examMonth = examMonth;
	}

	public void setExamYear(String examYear) {
		this.examYear = examYear;
	}

	public String getCreatedDate() {
		return createdDate;
	}

	public String getCreatedBy() {
		return createdBy;
	}

	public String getConsumerProgramStructureId() {
		return consumerProgramStructureId;
	}

	public void setCreatedDate(String createdDate) {
		this.createdDate = createdDate;
	}

	public void setCreatedBy(String createdBy) {
		this.createdBy = createdBy;
	}

	public void setConsumerProgramStructureId(String consumerProgramStructureId) {
		this.consumerProgramStructureId = consumerProgramStructureId;
	}

	public String[] getSapIds() {
		return sapIds;
	}

	public void setSapIds(String[] sapIds) {
		this.sapIds = sapIds;
	}

	public String getLastModifiedDate() {
		return lastModifiedDate;
	}

	public String getLastModifiedBy() {
		return lastModifiedBy;
	}

	public void setLastModifiedDate(String lastModifiedDate) {
		this.lastModifiedDate = lastModifiedDate;
	}

	public void setLastModifiedBy(String lastModifiedBy) {
		this.lastModifiedBy = lastModifiedBy;
	}

	public String getActive() {
		return active;
	}

	public void setActive(String active) {
		this.active = active;
	}

	@Override
	public String toString() {
		return "MBAWXExamRegistrationExtensionBean [sapid=" + sapid + ", program=" + program + ", extendStartDateTime="
				+ extendStartDateTime + ", extendEndDateTime=" + extendEndDateTime + ", createdDate=" + createdDate
				+ ", createdBy=" + createdBy + ", examMonth=" + examMonth + ", examYear=" + examYear
				+ ", lastModifiedDate=" + lastModifiedDate + ", lastModifiedBy=" + lastModifiedBy
				+ ", consumerProgramStructureId=" + consumerProgramStructureId + ", sapIds=" + Arrays.toString(sapIds)
				+ ", active=" + active + "]";
	}
}
