package com.nmims.beans;

import java.io.Serializable;

//spring security related changes rename ConsumerProgramStructure to ConsumerProgramStructureExam
public class ConsumerProgramStructureExam  implements Serializable {

	private String id;
	private String programId;
	private String programStructureId;
	private String name;
	private String code;
	private String program_structure;
	private String consumerTypeId;
	private String subject;
	private String groupid;
	private String batchId;
	private String sem;
	private String acadYear;
	private String acadMonth;
	
	private String createdBy;
	private String createdDate;
	private String lastModifiedBy;
	private String lastModifiedDate;
	private String status;
	private String message;
//	private String hasLiveSessionApplicable;
	private String hasPaidSessionApplicable;
	private String examYear;
	private String examMonth;
	
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getProgramId() {
		return programId;
	}
	public void setProgramId(String programId) {
		this.programId = programId;
	}
	public String getProgramStructureId() {
		return programStructureId;
	}
	public void setProgramStructureId(String programStructureId) {
		this.programStructureId = programStructureId;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getCode() {
		return code;
	}
	public void setCode(String code) {
		this.code = code;
	}
	public String getProgram_structure() {
		return program_structure;
	}
	public void setProgram_structure(String program_structure) {
		this.program_structure = program_structure;
	}
	public String getConsumerTypeId() {
		return consumerTypeId;
	}
	public void setConsumerTypeId(String consumerTypeId) {
		this.consumerTypeId = consumerTypeId;
	}
	public String getSubject() {
		return subject;
	}
	public void setSubject(String subject) {
		this.subject = subject;
	}
	public String getGroupid() {
		return groupid;
	}
	public void setGroupid(String groupid) {
		this.groupid = groupid;
	}
	public String getSem() {
		return sem;
	}
	public void setSem(String sem) {
		this.sem = sem;
	}
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
	public String getBatchId() {
		return batchId;
	}
	public void setBatchId(String batchId) {
		this.batchId = batchId;
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
	public String getLastModifiedBy() {
		return lastModifiedBy;
	}
	public void setLastModifiedBy(String lastModifiedBy) {
		this.lastModifiedBy = lastModifiedBy;
	}
	public String getLastModifiedDate() {
		return lastModifiedDate;
	}
	public void setLastModifiedDate(String lastModifiedDate) {
		this.lastModifiedDate = lastModifiedDate;
	}	

	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}
	public String getMessage() {
		return message;
	}
	public void setMessage(String message) {
		this.message = message;
	}
	
	
	public String getHasPaidSessionApplicable() {
		return hasPaidSessionApplicable;
	}
	public void setHasPaidSessionApplicable(String hasPaidSessionApplicable) {
		this.hasPaidSessionApplicable = hasPaidSessionApplicable;
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
	@Override
	public String toString() {
		return "ConsumerProgramStructureExam [id=" + id + ", programId=" + programId + ", programStructureId="
				+ programStructureId + ", name=" + name + ", code=" + code + ", program_structure=" + program_structure
				+ ", consumerTypeId=" + consumerTypeId + ", subject=" + subject + ", groupid=" + groupid + ", batchId="
				+ batchId + ", sem=" + sem + ", acadYear=" + acadYear + ", acadMonth=" + acadMonth + ", createdBy="
				+ createdBy + ", createdDate=" + createdDate + ", lastModifiedBy=" + lastModifiedBy
				+ ", lastModifiedDate=" + lastModifiedDate + ", status=" + status + ", message=" + message
				+ ", hasPaidSessionApplicable=" + hasPaidSessionApplicable + ", examYear=" + examYear + ", examMonth="
				+ examMonth + "]";
	}
	
	
	
}
