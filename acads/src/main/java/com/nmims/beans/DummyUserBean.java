package com.nmims.beans;

import java.io.Serializable;

public class DummyUserBean  implements Serializable {

	private String  userId;
	private String batchId;
	private String acadYear; 
	private String acadMonth;
	private String examYear;
	private String examMonth;
	
	private String batchName;
	
	private String consumerType;
	private String programType; 
	private String programStructure;
	private String program;	
	
	public String getUserId() {
		return userId;
	}
	public void setUserId(String userId) {
		this.userId = userId;
	}
	public String getBatchId() {
		return batchId;
	}
	public void setBatchId(String batchId) {
		this.batchId = batchId;
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

	public String getBatchName() {
		return batchName;
	}
	public void setBatchName(String batchName) {
		this.batchName = batchName;
	}
	public String getConsumerType() {
		return consumerType;
	}
	public void setConsumerType(String consumerType) {
		this.consumerType = consumerType;
	}
	public String getProgramType() {
		return programType;
	}
	public void setProgramType(String programType) {
		this.programType = programType;
	}
	public String getProgramStructure() {
		return programStructure;
	}
	public void setProgramStructure(String programStructure) {
		this.programStructure = programStructure;
	}
	public String getProgram() {
		return program;
	}
	public void setProgram(String program) {
		this.program = program;
	}
	
	@Override
	public String toString() {
		return "DummyUserBean [userId=" + userId + ", batchId=" + batchId + ", acadYear=" + acadYear + ", acadMonth="
				+ acadMonth + ", examYear=" + examYear + ", examMonth=" + examMonth + ", batchName=" + batchName
				+ ", consumerType=" + consumerType + ", programType=" + programType + ", programStructure="
				+ programStructure + ", program=" + program + "]";
	}
}
