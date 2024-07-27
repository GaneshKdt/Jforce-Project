package com.nmims.beans;

import java.io.Serializable;

import org.springframework.web.multipart.commons.CommonsMultipartFile;

public class ProjectTitle  implements Serializable  {

	private String id;
	private String sapid;
	private String titleId;
	private String examYear;
	private String examMonth;
	private String title;
	private String subject;
	private String consumerProgramStructureId;
	private String prgm_sem_subj_id;
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

	private CommonsMultipartFile fileData;
	
	private String error;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getSapid() {
		return sapid;
	}

	public void setSapid(String sapid) {
		this.sapid = sapid;
	}

	public String getTitleId() {
		return titleId;
	}

	public void setTitleId(String titleId) {
		this.titleId = titleId;
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

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getConsumerProgramStructureId() {
		return consumerProgramStructureId;
	}

	public void setConsumerProgramStructureId(String consumerProgramStructureId) {
		this.consumerProgramStructureId = consumerProgramStructureId;
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

	public CommonsMultipartFile getFileData() {
		return fileData;
	}

	public void setFileData(CommonsMultipartFile fileData) {
		this.fileData = fileData;
	}

	public String getError() {
		return error;
	}

	public void setError(String error) {
		this.error = error;
	}

	@Override
	public String toString() {
		return "ProjectTitle [id=" + id + ", sapid=" + sapid + ", titleId=" + titleId + ", examYear=" + examYear
				+ ", examMonth=" + examMonth + ", title=" + title + ", subject=" + subject
				+ ", consumerProgramStructureId=" + consumerProgramStructureId + ", prgm_sem_subj_id="
				+ prgm_sem_subj_id + ", active=" + active + ", programId=" + programId + ", programName=" + programName
				+ ", programCode=" + programCode + ", consumerTypeId=" + consumerTypeId + ", consumerType="
				+ consumerType + ", programStructureId=" + programStructureId + ", programStructure=" + programStructure
				+ ", createdBy=" + createdBy + ", updatedBy=" + updatedBy + ", fileData=" + fileData + ", error="
				+ error + "]";
	}

	public String getSubject() {
		return subject;
	}

	public void setSubject(String subject) {
		this.subject = subject;
	}

	public String getPrgm_sem_subj_id() {
		return prgm_sem_subj_id;
	}

	public void setPrgm_sem_subj_id(String prgm_sem_subj_id) {
		this.prgm_sem_subj_id = prgm_sem_subj_id;
	}

	public String getActive() {
		return active;
	}

	public void setActive(String active) {
		this.active = active;
	}
}
