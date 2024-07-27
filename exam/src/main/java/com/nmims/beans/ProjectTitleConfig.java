package com.nmims.beans;

import java.io.Serializable;

public class ProjectTitleConfig  implements Serializable  {
	private String id;
	private String examYear;
	private String examMonth;
	private String subject;
	private String consumerProgramStructureId;
	private String programSemSubjId;
	private String active;
	
	private String programId;
	private String programName;
	private String programCode;
	private String consumerTypeId;
	private String consumerType;
	private String programStructureId;
	private String programStructure;
	private String createdBy;
	private String updatedBy;
	private String start_date;
	private String end_date;
	
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
	public String getSubject() {
		return subject;
	}
	public void setSubject(String subject) {
		this.subject = subject;
	}
	public String getConsumerProgramStructureId() {
		return consumerProgramStructureId;
	}
	public void setConsumerProgramStructureId(String consumerProgramStructureId) {
		this.consumerProgramStructureId = consumerProgramStructureId;
	}
	public String getProgramSemSubjId() {
		return programSemSubjId;
	}
	public void setProgramSemSubjId(String programSemSubjId) {
		this.programSemSubjId = programSemSubjId;
	}
	public String getActive() {
		return active;
	}
	public void setActive(String active) {
		this.active = active;
	}
	public String getProgramId() {
		return programId;
	}
	public void setProgramId(String programId) {
		this.programId = programId;
	}
	public String getProgramName() {
		return programName;
	}
	public void setProgramName(String programName) {
		this.programName = programName;
	}
	public String getProgramCode() {
		return programCode;
	}
	public void setProgramCode(String programCode) {
		this.programCode = programCode;
	}
	public String getConsumerTypeId() {
		return consumerTypeId;
	}
	public void setConsumerTypeId(String consumerTypeId) {
		this.consumerTypeId = consumerTypeId;
	}
	public String getConsumerType() {
		return consumerType;
	}
	public void setConsumerType(String consumerType) {
		this.consumerType = consumerType;
	}
	public String getProgramStructureId() {
		return programStructureId;
	}
	public void setProgramStructureId(String programStructureId) {
		this.programStructureId = programStructureId;
	}
	public String getProgramStructure() {
		return programStructure;
	}
	public void setProgramStructure(String programStructure) {
		this.programStructure = programStructure;
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
	public String getStart_date() {
		return start_date;
	}
	public void setStart_date(String start_date) {
		this.start_date = start_date;
	}
	public String getEnd_date() {
		return end_date;
	}
	public void setEnd_date(String end_date) {
		this.end_date = end_date;
	}
	public String getError() {
		return error;
	}
	public void setError(String error) {
		this.error = error;
	}
	@Override
	public String toString() {
		return "ProjectTitleConfig [id=" + id + ", examYear=" + examYear + ", examMonth=" + examMonth + ", subject="
				+ subject + ", consumerProgramStructureId=" + consumerProgramStructureId + ", programSemSubjId="
				+ programSemSubjId + ", active=" + active + ", programId=" + programId + ", programName=" + programName
				+ ", programCode=" + programCode + ", consumerTypeId=" + consumerTypeId + ", consumerType="
				+ consumerType + ", programStructureId=" + programStructureId + ", programStructure=" + programStructure
				+ ", createdBy=" + createdBy + ", updatedBy=" + updatedBy + ", start_date=" + start_date + ", end_date="
				+ end_date + ", error=" + error + "]";
	}
}
