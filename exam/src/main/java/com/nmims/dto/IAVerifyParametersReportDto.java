package com.nmims.dto;

import java.io.Serializable;

import com.fasterxml.jackson.annotation.JsonProperty;

public class IAVerifyParametersReportDto implements Serializable {
	private Integer examYear;
	private String examMonth;
	private Integer acadYear;
	private String acadMonth;
	
	private String startDate;
	
	private String testName;
	private String subject;
	private String batch;
	
	private String testType;
	private String facultyNameId;
	
	private Integer consumerTypeId;
	private Integer programId;
	private Integer programStructureId;
	

	@JsonProperty("examYear")
	public Integer getExamYear() {
		return examYear;
	}
	
	@JsonProperty("year")
	public Integer getYear() {
		return examYear;
	}
	
	@JsonProperty("examYear")
	public void setExamYear(Integer examYear) {
		this.examYear = examYear;
	}

	@JsonProperty("year")
	public void setYear(Integer examYear) {
		this.examYear = examYear;
	}

	@JsonProperty("examMonth")
	public String getExamMonth() {
		return examMonth;
	}
	
	@JsonProperty("month")
	public String getMonth() {
		return examMonth;
	}
	
	@JsonProperty("examMonth")
	public void setExamMonth(String examMonth) {
		this.examMonth = examMonth;
	}

	@JsonProperty("month")
	public void setMonth(String examMonth) {
		this.examMonth = examMonth;
	}

	public Integer getAcadYear() {
		return acadYear;
	}

	public void setAcadYear(Integer acadYear) {
		this.acadYear = acadYear;
	}

	public String getAcadMonth() {
		return acadMonth;
	}

	public void setAcadMonth(String acadMonth) {
		this.acadMonth = acadMonth;
	}
	
	public String getStartDate() {
		return startDate;
	}
	
	public void setStartDate(String startDate) {
		this.startDate = startDate;
	}

	public String getBatch() {
		return batch;
	}

	public void setBatch(String batch) {
		this.batch = batch;
	}
	
	public String getTestName() {
		return testName;
	}

	public void setTestName(String testName) {
		this.testName = testName;
	}
	
	public String getSubject() {
		return subject;
	}

	public void setSubject(String subject) {
		this.subject = subject;
	}

	public String getTestType() {
		return testType;
	}

	public void setTestType(String testType) {
		this.testType = testType;
	}

	@JsonProperty("facultyNameId")
	public String getFacultyNameId() {
		return facultyNameId;
	}
	
	@JsonProperty("facultyId")
	public String getFacultyId() {
		return facultyNameId;
	}
	
	@JsonProperty("facultyNameId")
	public void setFacultyNameId(String facultyNameId) {
		this.facultyNameId = facultyNameId;
	}

	@JsonProperty("facultyId")
	public void setFacultyId(String facultyNameId) {
		this.facultyNameId = facultyNameId;
	}
	
	@JsonProperty("consumerTypeId")
	public Integer getConsumerTypeId() {
		return consumerTypeId;
	}
	
	@JsonProperty("consumerTypeIdFormValue")
	public Integer getConsumerTypeIdFormValue() {
		return consumerTypeId;
	}

	@JsonProperty("consumerTypeId")
	public void setConsumerTypeId(Integer consumerTypeId) {
		this.consumerTypeId = consumerTypeId;
	}
	
	@JsonProperty("consumerTypeIdFormValue")
	public void setConsumerTypeIdFormValue(Integer consumerTypeId) {
		this.consumerTypeId = consumerTypeId;
	}
	
	@JsonProperty("programId")
	public Integer getProgramId() {
		return programId;
	}
	
	@JsonProperty("programIdFormValue")
	public Integer getProgramIdFormValue() {
		return programId;
	}
	
	@JsonProperty("programId")
	public void setProgramId(Integer programId) {
		this.programId = programId;
	}
	
	@JsonProperty("programIdFormValue")
	public void setProgramIdFormValue(Integer programId) {
		this.programId = programId;
	}

	@JsonProperty("programStructureId")
	public Integer getProgramStructureId() {
		return programStructureId;
	}
	
	@JsonProperty("programStructureIdFormValue")
	public Integer getProgramStructureIdFormValue() {
		return programStructureId;
	}
	
	@JsonProperty("programStructureId")
	public void setProgramStructureId(Integer programStructureId) {
		this.programStructureId = programStructureId;
	}
	
	@JsonProperty("programStructureIdFormValue")
	public void setProgramStructureIdFormValue(Integer programStructureId) {
		this.programStructureId = programStructureId;
	}

	@Override
	public String toString() {
		return "IAVerifyParametersReportDto [examYear=" + examYear + ", examMonth=" + examMonth + ", acadYear="
				+ acadYear + ", acadMonth=" + acadMonth + ", startDate=" + startDate + ", testName=" + testName
				+ ", subject=" + subject + ", batch=" + batch + ", testType=" + testType + ", facultyNameId="
				+ facultyNameId + ", consumerTypeId=" + consumerTypeId + ", programId=" + programId
				+ ", programStructureId=" + programStructureId + "]";
	}
}
