package com.nmims.beans;

import java.io.Serializable;
import java.util.Date;

import org.springframework.web.multipart.commons.CommonsMultipartFile;

public class MBALiveSettings  implements Serializable  {
	private Long id;
	private String consumerType;
	private String program;
	private String programStructure;
	private String acadsYear;
	private String acadsMonth;
	private String examYear;
	private String examMonth;
	private String consumerProgramStructureId;
	private String type;

	private String startTimeStr;
	private String endTimeStr;
	private String startDateStr;
	private String endDateStr;
	private Date startTime;
	private Date endTime;
	
	private CommonsMultipartFile fileData;
	
	private String error;

	private String createdBy;
	private String createdOn;
	private String lastModifiedBy;
	private String lastModifiedOn;
	
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public String getConsumerType() {
		return consumerType;
	}
	public void setConsumerType(String consumerType) {
		this.consumerType = consumerType;
	}
	public String getProgram() {
		return program;
	}
	public void setProgram(String program) {
		this.program = program;
	}
	public String getProgramStructure() {
		return programStructure;
	}
	public void setProgramStructure(String programStructure) {
		this.programStructure = programStructure;
	}
	public String getAcadsYear() {
		return acadsYear;
	}
	public void setAcadsYear(String acadsYear) {
		this.acadsYear = acadsYear;
	}
	public String getAcadsMonth() {
		return acadsMonth;
	}
	public void setAcadsMonth(String acadsMonth) {
		this.acadsMonth = acadsMonth;
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
	public String getConsumerProgramStructureId() {
		return consumerProgramStructureId;
	}
	public void setConsumerProgramStructureId(String consumerProgramStructureId) {
		this.consumerProgramStructureId = consumerProgramStructureId;
	}
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	public String getStartTimeStr() {
		return startTimeStr;
	}
	public void setStartTimeStr(String startTimeStr) {
		this.startTimeStr = startTimeStr;
	}
	public String getEndTimeStr() {
		return endTimeStr;
	}
	public void setEndTimeStr(String endTimeStr) {
		this.endTimeStr = endTimeStr;
	}
	public String getStartDateStr() {
		return startDateStr;
	}
	public void setStartDateStr(String startDateStr) {
		this.startDateStr = startDateStr;
	}
	public String getEndDateStr() {
		return endDateStr;
	}
	public void setEndDateStr(String endDateStr) {
		this.endDateStr = endDateStr;
	}
	public Date getStartTime() {
		return startTime;
	}
	public void setStartTime(Date startTime) {
		this.startTime = startTime;
	}
	public Date getEndTime() {
		return endTime;
	}
	public void setEndTime(Date endTime) {
		this.endTime = endTime;
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
	public String getCreatedBy() {
		return createdBy;
	}
	public void setCreatedBy(String createdBy) {
		this.createdBy = createdBy;
	}
	public String getCreatedOn() {
		return createdOn;
	}
	public void setCreatedOn(String createdOn) {
		this.createdOn = createdOn;
	}
	public String getLastModifiedBy() {
		return lastModifiedBy;
	}
	public void setLastModifiedBy(String lastModifiedBy) {
		this.lastModifiedBy = lastModifiedBy;
	}
	public String getLastModifiedOn() {
		return lastModifiedOn;
	}
	public void setLastModifiedOn(String lastModifiedOn) {
		this.lastModifiedOn = lastModifiedOn;
	}
	@Override
	public String toString() {
		return "MBALiveSettings [id=" + id + ", consumerType=" + consumerType + ", program=" + program
				+ ", programStructure=" + programStructure + ", acadsYear=" + acadsYear + ", acadsMonth=" + acadsMonth
				+ ", examYear=" + examYear + ", examMonth=" + examMonth + ", consumerProgramStructureId="
				+ consumerProgramStructureId + ", type=" + type + ", startTimeStr=" + startTimeStr + ", endTimeStr="
				+ endTimeStr + ", startDateStr=" + startDateStr + ", endDateStr=" + endDateStr + ", startTime="
				+ startTime + ", endTime=" + endTime + ", fileData=" + fileData + ", error=" + error + ", createdBy="
				+ createdBy + ", createdOn=" + createdOn + ", lastModifiedBy=" + lastModifiedBy + ", lastModifiedOn="
				+ lastModifiedOn + "]";
	}
}
