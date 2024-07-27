package com.nmims.beans;

import java.io.Serializable;

public class FailedSubjectCountCriteriaBean implements Serializable{
	
	private String createdBy;
	private String lastModifiedBy;
	private String failedSubjectCount;
	private String programType;
	private String message;
	private String flag;
	private String consumerProgramStructureId;
	
	
	public String getConsumerProgramStructureId() {
		return consumerProgramStructureId;
	}
	public void setConsumerProgramStructureId(String consumerProgramStructureId) {
		this.consumerProgramStructureId = consumerProgramStructureId;
	}
	public String getCreatedBy() {
		return createdBy;
	}
	public void setCreatedBy(String createdBy) {
		this.createdBy = createdBy;
	}
	
	public String getLastModifiedBy() {
		return lastModifiedBy;
	}
	public void setLastModifiedBy(String lastModifiedBy) {
		this.lastModifiedBy = lastModifiedBy;
	}
	public String getFailedSubjectCount() {
		return failedSubjectCount;
	}
	public void setFailedSubjectCount(String failedSubjectCount) {
		this.failedSubjectCount = failedSubjectCount;
	}
	public String getProgramType() {
		return programType;
	}
	public void setProgramType(String programType) {
		this.programType = programType;
	}
	public String getMessage() {
		return message;
	}
	public void setMessage(String message) {
		this.message = message;
	}
	public String getFlag() {
		return flag;
	}
	public void setFlag(String flag) {
		this.flag = flag;
	}
	
	@Override
	public String toString() {
		return "FailedSubjectCountCriteriaBean [createdBy=" + createdBy + ", lastModifiedBy=" + lastModifiedBy
				+ ", failedSubjectCount=" + failedSubjectCount + ", programType=" + programType + ", message=" + message
				+ ", flag=" + flag + "]";
	}
	
}
