package com.nmims.beans;

import java.io.Serializable;
import java.util.List;

import org.springframework.web.multipart.commons.CommonsMultipartFile;

public class MettlResultsSyncBean extends ExamsAssessmentsBean  implements Serializable {

	private CommonsMultipartFile fileData;
	private List<MettlPGResponseBean> customFetchInput;
	
	private List<MettlPGResponseBean> studentResults;
	
	private String fromDate;
	private String toDate;
	private String examYear;
	private String examMonth;
	private String applicationStatus;
	private String programId;
	private String programStructureId;
	private String consumerTypeId;
	private String sifySubjectCode;
	private String error;
	public CommonsMultipartFile getFileData() {
		return fileData;
	}
	public void setFileData(CommonsMultipartFile fileData) {
		this.fileData = fileData;
	}
	public List<MettlPGResponseBean> getCustomFetchInput() {
		return customFetchInput;
	}
	public void setCustomFetchInput(List<MettlPGResponseBean> customFetchInput) {
		this.customFetchInput = customFetchInput;
	}
	public List<MettlPGResponseBean> getStudentResults() {
		return studentResults;
	}
	public void setStudentResults(List<MettlPGResponseBean> studentResults) {
		this.studentResults = studentResults;
	}
	public String getFromDate() {
		return fromDate;
	}
	public void setFromDate(String fromDate) {
		this.fromDate = fromDate;
	}
	public String getToDate() {
		return toDate;
	}
	public void setToDate(String toDate) {
		this.toDate = toDate;
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
	public String getApplicationStatus() {
		return applicationStatus;
	}
	public void setApplicationStatus(String applicationStatus) {
		this.applicationStatus = applicationStatus;
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
	public String getConsumerTypeId() {
		return consumerTypeId;
	}
	public void setConsumerTypeId(String consumerTypeId) {
		this.consumerTypeId = consumerTypeId;
	}
	public String getSifySubjectCode() {
		return sifySubjectCode;
	}
	public void setSifySubjectCode(String sifySubjectCode) {
		this.sifySubjectCode = sifySubjectCode;
	}
	public String getError() {
		return error;
	}
	public void setError(String error) {
		this.error = error;
	}
	@Override
	public String toString() {
		return "MettlResultsSyncBean [fileData=" + fileData + ", customFetchInput=" + customFetchInput
				+ ", studentResults=" + studentResults + ", fromDate=" + fromDate + ", toDate=" + toDate + ", examYear="
				+ examYear + ", examMonth=" + examMonth + ", applicationStatus=" + applicationStatus + ", programId="
				+ programId + ", programStructureId=" + programStructureId + ", consumerTypeId=" + consumerTypeId
				+ ", sifySubjectCode=" + sifySubjectCode + ", error=" + error + "]";
	}
}
