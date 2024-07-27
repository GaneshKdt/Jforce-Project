package com.nmims.coursera.beans;

import java.io.Serializable;
import java.time.LocalDateTime;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;

@Entity
@Table(name="coursera.coursera_student_mapping",schema="coursera")
public class StudentCourseraMappingBean implements Serializable{
	
	@Id
	@Column(name="sapId")
	private String sapId;
	private String coursera_program_id;
	@Column(name="expiryDate")
	private LocalDateTime expiryDate;
	private String createdBy;
	private String lastUpdatedBy;
	@Transient
	private String consumer_program_structure_id;
	@Transient
	private String learnerURL;
	
	public String getSapId() {
		return sapId;
	}
	public void setSapId(String sapId) {
		this.sapId = sapId;
	}
	public String getCoursera_program_id() {
		return coursera_program_id;
	}
	public void setCoursera_program_id(String coursera_program_id) {
		this.coursera_program_id = coursera_program_id;
	}
	public LocalDateTime getExpiryDate() {
		return expiryDate;
	}
	public void setExpiryDate(LocalDateTime expiryDate) {
		this.expiryDate = expiryDate;
	}
	public String getCreatedBy() {
		return createdBy;
	}
	public void setCreatedBy(String createdBy) {
		this.createdBy = createdBy;
	}
	public String getLastUpdatedBy() {
		return lastUpdatedBy;
	}
	public void setLastUpdatedBy(String lastUpdatedBy) {
		this.lastUpdatedBy = lastUpdatedBy;
	}
	
	public String getConsumer_program_structure_id() {
		return consumer_program_structure_id;
	}
	public void setConsumer_program_structure_id(String consumer_program_structure_id) {
		this.consumer_program_structure_id = consumer_program_structure_id;
	}
	public String getLearnerURL() {
		return learnerURL;
	}
	public void setLearnerURL(String learnerURL) {
		this.learnerURL = learnerURL;
	}	
	
	@Override
	public String toString() {
		return "StudentCourseraMappingBean [sapId=" + sapId + ", coursera_program_id=" + coursera_program_id
				+ ", expiryDate=" + expiryDate + ", createdBy=" + createdBy + ", lastUpdatedBy=" + lastUpdatedBy
				+ ", consumer_program_structure_id=" + consumer_program_structure_id + ", learnerURL=" + learnerURL
				+ "]";
	}
}
