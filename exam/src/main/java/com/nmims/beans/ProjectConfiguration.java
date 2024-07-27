package com.nmims.beans;

import java.io.Serializable;

import org.springframework.web.multipart.commons.CommonsMultipartFile;

public class ProjectConfiguration  implements Serializable  {

	private String id;
	private String examYear;
	private String examMonth;
	private String subject;
	private String consumerProgramStructureId;
	private String programSemSubjId;
	
	private String programId;
	private String programName;
	private String programCode;
	private String consumerTypeId;
	private String consumerType;
	private String programStructureId;
	private String programStructure;

	private String hasTitle;
	private String hasSOP;
	private String hasViva;
	private String hasSynopsis;
	private String hasSubmission;
	
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
	public String getHasTitle() {
		return hasTitle;
	}
	public void setHasTitle(String hasTitle) {
		this.hasTitle = hasTitle;
	}
	public String getHasSOP() {
		return hasSOP;
	}
	public void setHasSOP(String hasSOP) {
		this.hasSOP = hasSOP;
	}
	public String getHasViva() {
		return hasViva;
	}
	public void setHasViva(String hasViva) {
		this.hasViva = hasViva;
	}
	public String getHasSynopsis() {
		return hasSynopsis;
	}
	public void setHasSynopsis(String hasSynopsis) {
		this.hasSynopsis = hasSynopsis;
	}
	public String getHasSubmission() {
		return hasSubmission;
	}
	public void setHasSubmission(String hasSubmission) {
		this.hasSubmission = hasSubmission;
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
		return "ProjectConfiguration [id=" + id + ", examYear=" + examYear + ", examMonth=" + examMonth + ", subject="
				+ subject + ", consumerProgramStructureId=" + consumerProgramStructureId + ", programSemSubjId="
				+ programSemSubjId + ", programId=" + programId + ", programName=" + programName + ", programCode="
				+ programCode + ", consumerTypeId=" + consumerTypeId + ", consumerType=" + consumerType
				+ ", programStructureId=" + programStructureId + ", programStructure=" + programStructure
				+ ", hasTitle=" + hasTitle + ", hasSOP=" + hasSOP + ", hasViva=" + hasViva + ", hasSynopsis="
				+ hasSynopsis + ", hasSubmission=" + hasSubmission + ", createdBy=" + createdBy + ", updatedBy="
				+ updatedBy + ", fileData=" + fileData + ", error=" + error + "]";
	}
}
