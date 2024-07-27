package com.nmims.timeline.model;

import java.io.Serializable;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.IdClass;
import javax.persistence.Table;

@Entity
@Table(name="lti.student_subject_config", schema="lti")
public class Timebound implements Serializable{

	//id, prgm_sem_subj_id, batchId, startDate, endDate, acadYear, acadMonth, examYear, examMonth, sequence,
	//createdBy, createdDate, lastModifiedBy, lastModifiedDate

	private static final long serialVersionUID = 1L;

	
	@Id  
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer id;
	
	private Integer prgm_sem_subj_id;
	
	private Integer batchId;
	
	private String startDate;
	private String endDate;

	private String acadMonth;
	private String examMonth;
	

	private Integer acadYear;
	private Integer examYear;
	private Integer sequence;
	
	private String createdBy;
	private String createdDate;
	private String lastModifiedBy;
	private String lastModifiedDate;
	public Integer getId() {
		return id;
	}
	public void setId(Integer id) {
		this.id = id;
	}
	public Integer getPrgm_sem_subj_id() {
		return prgm_sem_subj_id;
	}
	public void setPrgm_sem_subj_id(Integer prgm_sem_subj_id) {
		this.prgm_sem_subj_id = prgm_sem_subj_id;
	}
	public Integer getBatchId() {
		return batchId;
	}
	public void setBatchId(Integer batchId) {
		this.batchId = batchId;
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
	public String getAcadMonth() {
		return acadMonth;
	}
	public void setAcadMonth(String acadMonth) {
		this.acadMonth = acadMonth;
	}
	public String getExamMonth() {
		return examMonth;
	}
	public void setExamMonth(String examMonth) {
		this.examMonth = examMonth;
	}
	public Integer getAcadYear() {
		return acadYear;
	}
	public void setAcadYear(Integer acadYear) {
		this.acadYear = acadYear;
	}
	public Integer getExamYear() {
		return examYear;
	}
	public void setExamYear(Integer examYear) {
		this.examYear = examYear;
	}
	public Integer getSequence() {
		return sequence;
	}
	public void setSequence(Integer sequence) {
		this.sequence = sequence;
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
	
	
	
}
