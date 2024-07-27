package com.nmims.beans;

import java.io.Serializable;
import java.util.List;

import org.springframework.web.multipart.commons.CommonsMultipartFile;

//spring security related changes rename StudentSubjectConfig to StudentSubjectConfigExamBean
public class StudentSubjectConfigExamBean  implements Serializable  {
	
	private String id;
	private String prgm_sem_subj_id;
	private int batchId;
	private String sem;
	private String userId;
	private String startDate;
	private String endDate;
	private String acadYear;
	private String acadMonth;
	private String examYear;
	private String examMonth;
	private	String subject;
	private String batchName;
	private String consumerTypeId;
	private String programId;
	private String programStructureId;
	private String sequence;
	private CommonsMultipartFile fileData;
	private String createdBy;
	private String createdDate;
	private String lastModifiedBy;
	private String lastModifiedDate;

	private String hasTEE ;
	private String hasIA ;
	
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getPrgm_sem_subj_id() {
		return prgm_sem_subj_id;
	}
	public void setPrgm_sem_subj_id(String prgm_sem_subj_id) {
		this.prgm_sem_subj_id = prgm_sem_subj_id;
	}
	public int getBatchId() {
		return batchId;
	}
	public void setBatchId(int batchId) {
		this.batchId = batchId;
	}
	public String getSem() {
		return sem;
	}
	public void setSem(String sem) {
		this.sem = sem;
	}
	public String getUserId() {
		return userId;
	}
	public void setUserId(String userId) {
		this.userId = userId;
	}
	public String getStartDate() {
		return startDate;
	}
	public void setStartDate(String startDate) {
		this.startDate = startDate;
	}
	public String getEndDate() {
		return endDate;
	}
	public void setEndDate(String endDate) {
		this.endDate = endDate;
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
	public String getSubject() {
		return subject;
	}
	public void setSubject(String subject) {
		this.subject = subject;
	}
	public String getConsumerTypeId() {
		return consumerTypeId;
	}
	public void setConsumerTypeId(String consumerTypeId) {
		this.consumerTypeId = consumerTypeId;
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
	public String getSequence() {
		return sequence;
	}
	public void setSequence(String sequence) {
		this.sequence = sequence;
	}
	public CommonsMultipartFile getFileData() {
		return fileData;
	}
	public void setFileData(CommonsMultipartFile fileData) {
		this.fileData = fileData;
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
	public String getBatchName() {
		return batchName;
	}
	public void setBatchName(String batchName) {
		this.batchName = batchName;
	}public String getHasTEE() {
		return hasTEE;
	}
	public void setHasTEE(String hasTEE) {
		this.hasTEE = hasTEE;
	}public String getHasIA() {
		return hasIA;
	}
	public void setHasIA(String hasIA) {
		this.hasIA = hasIA;
	}
	@Override
	public String toString() {
		return "StudentSubjectConfig [id=" + id + ", prgm_sem_subj_id=" + prgm_sem_subj_id + ", batchId=" + batchId
				+ ", sem=" + sem + ", userId=" + userId + ", startDate=" + startDate + ", endDate=" + endDate
				+ ", acadYear=" + acadYear + ", acadMonth=" + acadMonth + ", examYear=" + examYear + ", examMonth="
				+ examMonth + ", subject=" + subject + ", batchName=" + batchName + ", consumerTypeId=" + consumerTypeId
				+ ", programId=" + programId + ", programStructureId=" + programStructureId + ", sequence=" + sequence
				+ ", fileData=" + fileData + "]";
	}
	
	
}
