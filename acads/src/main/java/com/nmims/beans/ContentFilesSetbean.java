package com.nmims.beans;

import java.io.Serializable;
import java.util.List;

public class ContentFilesSetbean implements Serializable {
	private List<ContentAcadsBean> contentFiles;
	private String subject;
	private String year;
	private String month;
	
	private Integer consumerProgramStructureId;
	private String consumerTypeId;
	private String programStructureId;
	private String programId;
	
	private String consumerTypeIdFormValue;
	private String programStructureIdFormValue;
	private String programIdFormValue;
	private String programStructure;
	private Long id;
	private String productType;
	
	//Added By Riya
	private String subjectCodeId;
	private String masterKey;
	private String selectAllOptions;
	
	
	
	
	public String getProductType() {
		return productType;
	}
	public void setProductType(String productType) {
		this.productType = productType;
	}
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	/**
	 * @return the consumerTypeIdFormValue
	 */
	public String getConsumerTypeIdFormValue() {
		return consumerTypeIdFormValue;
	}
	/**
	 * @param consumerTypeIdFormValue the consumerTypeIdFormValue to set
	 */
	public void setConsumerTypeIdFormValue(String consumerTypeIdFormValue) {
		this.consumerTypeIdFormValue = consumerTypeIdFormValue;
	}
	/**
	 * @return the programStructureIdFormValue
	 */
	public String getProgramStructureIdFormValue() {
		return programStructureIdFormValue;
	}
	/**
	 * @param programStructureIdFormValue the programStructureIdFormValue to set
	 */
	public void setProgramStructureIdFormValue(String programStructureIdFormValue) {
		this.programStructureIdFormValue = programStructureIdFormValue;
	}
	/**
	 * @return the programIdFormValue
	 */
	public String getProgramIdFormValue() {
		return programIdFormValue;
	}
	/**
	 * @param programIdFormValue the programIdFormValue to set
	 */
	public void setProgramIdFormValue(String programIdFormValue) {
		this.programIdFormValue = programIdFormValue;
	}
	/**
	 * @return the consumerProgramStructureId
	 */
	public Integer getConsumerProgramStructureId() {
		return consumerProgramStructureId;
	}
	/**
	 * @param consumerProgramStructureId the consumerProgramStructureId to set
	 */
	public void setConsumerProgramStructureId(Integer consumerProgramStructureId) {
		this.consumerProgramStructureId = consumerProgramStructureId;
	}
	/**
	 * @return the consumerTypeId
	 */
	public String getConsumerTypeId() {
		return consumerTypeId;
	}
	/**
	 * @param consumerTypeId the consumerTypeId to set
	 */
	public void setConsumerTypeId(String consumerTypeId) {
		this.consumerTypeId = consumerTypeId;
	}
	/**
	 * @return the programStructureId
	 */
	public String getProgramStructureId() {
		return programStructureId;
	}
	/**
	 * @param programStructureId the programStructureId to set
	 */
	public void setProgramStructureId(String programStructureId) {
		this.programStructureId = programStructureId;
	}
	/**
	 * @return the programId
	 */
	public String getProgramId() {
		return programId;
	}
	/**
	 * @param programId the programId to set
	 */
	public void setProgramId(String programId) {
		this.programId = programId;
	}
	public String getYear() {
		return year;
	}
	public void setYear(String year) {
		this.year = year;
	}
	public String getMonth() {
		return month;
	}
	public void setMonth(String month) {
		this.month = month;
	}
	public List<ContentAcadsBean> getContentFiles() {
		return contentFiles;
	}
	public void setContentFiles(List<ContentAcadsBean> contentFiles) {
		this.contentFiles = contentFiles;
	}
	public String getSubject() {
		return subject;
	}
	public void setSubject(String subject) {
		this.subject = subject;
	}
	public String getProgramStructure() {
		return programStructure;
	}
	public void setProgramStructure(String programStructure) {
		this.programStructure = programStructure;
	}
	@Override
	public String toString() {
		return "ContentFilesSetbean [productType=" + productType + "contentFiles=" + contentFiles + ", subject=" + subject + ", year=" + year
				+ ", month=" + month + ", consumerProgramStructureId=" + consumerProgramStructureId
				+ ", consumerTypeId=" + consumerTypeId + ", programStructureId=" + programStructureId + ", programId="
				+ programId + ", consumerTypeIdFormValue=" + consumerTypeIdFormValue + ", programStructureIdFormValue="
				+ programStructureIdFormValue + ", programIdFormValue=" + programIdFormValue + ", id=" + id + "]";
	}
	public String getSubjectCodeId() {
		return subjectCodeId;
	}
	public void setSubjectCodeId(String subjectCodeId) {
		this.subjectCodeId = subjectCodeId;
	}
	public String getMasterKey() {
		return masterKey;
	}
	public void setMasterKey(String masterKey) {
		this.masterKey = masterKey;
	}
	public String getSelectAllOptions() {
		return selectAllOptions;
	}
	public void setSelectAllOptions(String selectAllOptions) {
		this.selectAllOptions = selectAllOptions;
	}
	
	private String createdBy;
	private String lastModifiedBy;

	public String getLastModifiedBy() {
		return lastModifiedBy;
	}
	public void setLastModifiedBy(String lastModifiedBy) {
		this.lastModifiedBy = lastModifiedBy;
	}

	public String getCreatedBy() {
		return createdBy;
	}
	public void setCreatedBy(String createdBy) {
		this.createdBy = createdBy;
	}
}
