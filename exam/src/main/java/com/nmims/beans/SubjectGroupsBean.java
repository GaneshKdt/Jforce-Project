package com.nmims.beans;

import java.io.Serializable;

public class SubjectGroupsBean implements Serializable {

	private String id;
	private Integer timebound_user_mapping_id;
	private Integer applozic_group_id;
	private String role;
	private String userId;
	private Integer student_subject_config_id;
	private Integer program_sem_subject_id;
	private Integer batchId;
	private String batchName;
	private Integer acadYear;
	private String acadMonth;
	private String groupId;
	private String clientGroupId;
	private String term;
	private String specialisation;
	private String specialisation_initials;
	private String subject;
	private String subject_initials;
	private String chat_group_name;
	private String createdBy;
	private String createdDate;
	private String lastModifiedBy;
	private String lastModifiedDate;
	private String errorMessage = "";
	private boolean errorRecord = false;
	
	
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}

	public Integer getTimebound_user_mapping_id() {
		return timebound_user_mapping_id;
	}

	public void setTimebound_user_mapping_id(Integer timebound_user_mapping_id) {
		this.timebound_user_mapping_id = timebound_user_mapping_id;
	}

	public Integer getApplozic_group_id() {
		return applozic_group_id;
	}

	public void setApplozic_group_id(Integer applozic_group_id) {
		this.applozic_group_id = applozic_group_id;
	}

	public String getRole() {
		return role;
	}

	public void setRole(String role) {
		this.role = role;
	}

	public String getUserId() {
		return userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}

	public Integer getStudent_subject_config_id() {
		return student_subject_config_id;
	}

	public void setStudent_subject_config_id(Integer student_subject_config_id) {
		this.student_subject_config_id = student_subject_config_id;
	}

	public Integer getProgram_sem_subject_id() {
		return program_sem_subject_id;
	}

	public void setProgram_sem_subject_id(Integer program_sem_subject_id) {
		this.program_sem_subject_id = program_sem_subject_id;
	}

	public Integer getBatchId() {
		return batchId;
	}

	public void setBatchId(Integer batchId) {
		this.batchId = batchId;
	}

	public String getBatchName() {
		return batchName;
	}

	public void setBatchName(String batchName) {
		this.batchName = batchName;
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

	public String getGroupId() {
		return groupId;
	}

	public void setGroupId(String groupId) {
		this.groupId = groupId;
	}

	public String getClientGroupId() {
		return clientGroupId;
	}

	public void setClientGroupId(String clientGroupId) {
		this.clientGroupId = clientGroupId;
	}

	public String getTerm() {
		return term;
	}

	public void setTerm(String term) {
		this.term = term;
	}

	public String getSpecialisation() {
		return specialisation;
	}

	public void setSpecialisation(String specialisation) {
		this.specialisation = specialisation;
	}

	public String getSpecialisation_initials() {
		return specialisation_initials;
	}

	public void setSpecialisation_initials(String specialisation_initials) {
		this.specialisation_initials = specialisation_initials;
	}

	public String getSubject() {
		return subject;
	}

	public void setSubject(String subject) {
		this.subject = subject;
	}

	public String getSubject_initials() {
		return subject_initials;
	}

	public void setSubject_initials(String subject_initials) {
		this.subject_initials = subject_initials;
	}

	public String getChat_group_name() {
		return chat_group_name;
	}

	public void setChat_group_name(String chat_group_name) {
		this.chat_group_name = chat_group_name;
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
	public String getErrorMessage() {
		return errorMessage;
	}
	public void setErrorMessage(String errorMessage) {
		this.errorMessage = errorMessage;
	}
	public boolean isErrorRecord() {
		return errorRecord;
	}
	public void setErrorRecord(boolean errorRecord) {
		this.errorRecord = errorRecord;
	}
	
	
	@Override
	public String toString() {
		return "SubjectGroupsBean [id=" + id + ", applozic_group_id=" + applozic_group_id + ", role=" + role + ", timebound_user_mapping_id=" + timebound_user_mapping_id + ", userId=" + userId
				+ ", student_subject_config_id=" + student_subject_config_id + ", program_sem_subject_id=" + program_sem_subject_id
				+ ", batchId=" + batchId + ", batchName=" + batchName + ", acadYear=" + acadYear + ", acadMonth=" + acadMonth + ", groupId=" + groupId
				+ ", clientGroupId=" + clientGroupId + ", term=" + term
				+ ", specialisation=" + specialisation + ", specialisation_initials=" + specialisation_initials
				+ ", subject=" + subject + ", subject_initials=" + subject_initials + ", chat_group_name=" + chat_group_name
				+ ", createdBy=" + createdBy + ", createdDate=" + createdDate + ", lastModifiedBy=" + lastModifiedBy
				+ ", lastModifiedDate=" + lastModifiedDate + ", errorMessage=" + errorMessage + ", errorRecord=" + errorRecord + "]";
	}
}
