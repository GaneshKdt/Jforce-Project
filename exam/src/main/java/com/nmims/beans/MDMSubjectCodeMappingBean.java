package com.nmims.beans;

import java.io.Serializable;

import org.springframework.web.multipart.commons.CommonsMultipartFile;

public class MDMSubjectCodeMappingBean implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = -5390069835421416920L;
	
	private String consumerType;
	private String prgmStructApplicable;
	private String program;

	private String sem;// Semester
	private String subjectCodeId;// MDMSubjectCodeBean
	private String consumerProgramStructureId;
	private String active;
	private String createdBy;
	private String createdDate;
	private String lastModifiedBy;
	private String lastModifiedDate;
	
	private String subjectCode;
	private String consumerTypeId;
	private String programStructureId;
	private String programId;
	private Integer id;//Only for EditTable data ordering.
	
	private String status;
	private String message;

	private Integer sifySubjectCode;
	private Integer passScore;
	private String hasIA;
	private String hasTest;
	private String hasAssignment;
	private String hasTEE;
	private String assignmentNeededBeforeWritten;
	private String assignmentScoreModel;
	private String writtenScoreModel;
	private String createCaseForQuery;
	private String assignQueryToFaculty;
	private String isGraceApplicable;
	private Integer maxGraceMarks;
	
	private CommonsMultipartFile fileData;
	
	private String subjectName;//Display Purpose
	
	private Integer sessionTime;
	
	private Double subjectCredits;

	private String programFullName;
	
	public Integer getSessionTime() {
		return sessionTime;
	}

	public void setSessionTime(Integer sessionTime) {
		this.sessionTime = sessionTime;
	}

	public MDMSubjectCodeMappingBean() {
		super();
		// TODO Auto-generated constructor stub
	}

	public String getSem() {
		return sem;
	}

	public void setSem(String sem) {
		this.sem = sem;
	}

	public String getSubjectCodeId() {
		return subjectCodeId;
	}

	public void setSubjectCodeId(String subjectCodeId) {
		this.subjectCodeId = subjectCodeId;
	}

	public String getConsumerProgramStructureId() {
		return consumerProgramStructureId;
	}

	public void setConsumerProgramStructureId(String consumerProgramStructureId) {
		this.consumerProgramStructureId = consumerProgramStructureId;
	}

	public String getConsumerType() {
		return consumerType;
	}

	public void setConsumerType(String consumerType) {
		this.consumerType = consumerType;
	}

	public String getActive() {
		return active;
	}

	public void setActive(String active) {
		this.active = active;
	}

	public String getPrgmStructApplicable() {
		return prgmStructApplicable;
	}

	public void setPrgmStructApplicable(String prgmStructApplicable) {
		this.prgmStructApplicable = prgmStructApplicable;
	}

	public String getProgram() {
		return program;
	}

	public void setProgram(String program) {
		this.program = program;
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

	public String getSubjectCode() {
		return subjectCode;
	}

	public void setSubjectCode(String subjectCode) {
		this.subjectCode = subjectCode;
	}

	public String getConsumerTypeId() {
		return consumerTypeId;
	}

	public void setConsumerTypeId(String consumerTypeId) {
		this.consumerTypeId = consumerTypeId;
	}

	public String getProgramStructureId() {
		return programStructureId;
	}

	public void setProgramStructureId(String programStructureId) {
		this.programStructureId = programStructureId;
	}

	public String getProgramId() {
		return programId;
	}

	public void setProgramId(String programId) {
		this.programId = programId;
	}

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public Integer getPassScore() {
		return passScore;
	}

	public void setPassScore(Integer passScore) {
		this.passScore = passScore;
	}

	public String getHasIA() {
		return hasIA;
	}

	public void setHasIA(String hasIA) {
		this.hasIA = hasIA;
	}

	public String getHasTest() {
		return hasTest;
	}

	public void setHasTest(String hasTest) {
		this.hasTest = hasTest;
	}

	public String getHasAssignment() {
		return hasAssignment;
	}

	public void setHasAssignment(String hasAssignment) {
		this.hasAssignment = hasAssignment;
	}

	public String getAssignmentNeededBeforeWritten() {
		return assignmentNeededBeforeWritten;
	}

	public void setAssignmentNeededBeforeWritten(String assignmentNeededBeforeWritten) {
		this.assignmentNeededBeforeWritten = assignmentNeededBeforeWritten;
	}

	public String getAssignmentScoreModel() {
		return assignmentScoreModel;
	}

	public void setAssignmentScoreModel(String assignmentScoreModel) {
		this.assignmentScoreModel = assignmentScoreModel;
	}

	public String getWrittenScoreModel() {
		return writtenScoreModel;
	}

	public void setWrittenScoreModel(String writtenScoreModel) {
		this.writtenScoreModel = writtenScoreModel;
	}

	public String getCreateCaseForQuery() {
		return createCaseForQuery;
	}

	public void setCreateCaseForQuery(String createCaseForQuery) {
		this.createCaseForQuery = createCaseForQuery;
	}

	public String getAssignQueryToFaculty() {
		return assignQueryToFaculty;
	}

	public void setAssignQueryToFaculty(String assignQueryToFaculty) {
		this.assignQueryToFaculty = assignQueryToFaculty;
	}

	public String getIsGraceApplicable() {
		return isGraceApplicable;
	}

	public void setIsGraceApplicable(String isGraceApplicable) {
		this.isGraceApplicable = isGraceApplicable;
	}

	public Integer getMaxGraceMarks() {
		return maxGraceMarks;
	}

	public void setMaxGraceMarks(Integer maxGraceMarks) {
		this.maxGraceMarks = maxGraceMarks;
	}

	public CommonsMultipartFile getFileData() {
		return fileData;
	}

	public void setFileData(CommonsMultipartFile fileData) {
		this.fileData = fileData;
	}

	public Integer getSifySubjectCode() {
		return sifySubjectCode;
	}

	public void setSifySubjectCode(Integer sifySubjectCode) {
		this.sifySubjectCode = sifySubjectCode;
	}

	public String getSubjectName() {
		return subjectName;
	}

	public void setSubjectName(String subjectName) {
		this.subjectName = subjectName;
	}

	public Double getSubjectCredits() {
		return subjectCredits;
	}

	public void setSubjectCredits(Double subjectCredits) {
		this.subjectCredits = subjectCredits;
	}
	public String getHasTEE() {
		return hasTEE;
	}

	public void setHasTEE(String hasTEE) {
		this.hasTEE = hasTEE;
	}
 
	
	public String getProgramFullName() {
		return programFullName;
	}

	public void setProgramFullName(String programFullName) {
		this.programFullName = programFullName;
	}

	@Override
	public String toString() {
		return "MDMSubjectCodeMappingBean [consumerType=" + consumerType + ", prgmStructApplicable="
				+ prgmStructApplicable + ", program=" + program + ", sem=" + sem + ", subjectCodeId=" + subjectCodeId
				+ ", consumerProgramStructureId=" + consumerProgramStructureId + ", active=" + active + ", createdBy="
				+ createdBy + ", createdDate=" + createdDate + ", lastModifiedBy=" + lastModifiedBy
				+ ", lastModifiedDate=" + lastModifiedDate + ", subjectCode=" + subjectCode + ", consumerTypeId="
				+ consumerTypeId + ", programStructureId=" + programStructureId + ", programId=" + programId + ", id="
				+ id + ", status=" + status + ", message=" + message + ", sifySubjectCode=" + sifySubjectCode
				+ ", passScore=" + passScore + ", hasIA=" + hasIA + ", hasTest=" + hasTest + ", hasAssignment="
				+ hasAssignment + ", assignmentNeededBeforeWritten=" + assignmentNeededBeforeWritten
				+ ", assignmentScoreModel=" + assignmentScoreModel + ", writtenScoreModel=" + writtenScoreModel
				+ ", createCaseForQuery=" + createCaseForQuery + ", assignQueryToFaculty=" + assignQueryToFaculty
				+ ", isGraceApplicable=" + isGraceApplicable + ", maxGraceMarks=" + maxGraceMarks + ", fileData="
				+ fileData + ", subjectName=" + subjectName + ", sessionTime=" + sessionTime + ", subjectCredits="
				+ subjectCredits + "]";
	}
}