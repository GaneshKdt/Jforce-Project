package com.nmims.beans;

import java.io.Serializable;
import java.util.List;

public class SessionPlanPgBean implements Serializable {
	private static final long serialVersionUID = 1L;

	private int id;
	private int subjectCodeId;
	private String subject;
	private String facultyId;
	private String facultyName;
	private String month;
	private String year;
	private String objectives;
	private String learningOutcomes;
	private String prerequisites;
	private String links;
	private String createdBy;
	private String createdDate;
	private String lastModifiedBy;
	private String lastModifiedDate;
	List<SessionPlanModulePg> sessionPlanModuleList;

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public int getSubjectCodeId() {
		return subjectCodeId;
	}

	public void setSubjectCodeId(int subjectCodeId) {
		this.subjectCodeId = subjectCodeId;
	}

	public String getSubject() {
		return subject;
	}

	public void setSubject(String subject) {
		this.subject = subject;
	}

	public String getFacultyId() {
		return facultyId;
	}

	public void setFacultyId(String facultyId) {
		this.facultyId = facultyId;
	}

	public String getMonth() {
		return month;
	}

	public void setMonth(String month) {
		this.month = month;
	}

	public String getYear() {
		return year;
	}

	public void setYear(String year) {
		this.year = year;
	}

	public String getObjectives() {
		return objectives;
	}

	public void setObjectives(String objectives) {
		this.objectives = objectives;
	}

	public String getLearningOutcomes() {
		return learningOutcomes;
	}

	public void setLearningOutcomes(String learningOutcomes) {
		this.learningOutcomes = learningOutcomes;
	}

	public String getPrerequisites() {
		return prerequisites;
	}

	public void setPrerequisites(String prerequisites) {
		this.prerequisites = prerequisites;
	}

	public String getLinks() {
		return links;
	}

	public void setLinks(String links) {
		this.links = links;
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

	public List<SessionPlanModulePg> getSessionPlanModuleList() {
		return sessionPlanModuleList;
	}

	public void setSessionPlanModuleList(List<SessionPlanModulePg> sessionPlanModuleList) {
		this.sessionPlanModuleList = sessionPlanModuleList;
	}
	public String getFacultyName() {
		return facultyName;
	}

	public void setFacultyName(String facultyName) {
		this.facultyName = facultyName;
	}

	@Override
	public String toString() {
		return "SessionPlanPgBean [id=" + id + ", subjectCodeId=" + subjectCodeId + ", subject=" + subject
				+ ", facultyId=" + facultyId + ", facultyName=" + facultyName + ", month=" + month + ", year=" + year
				+ ", objectives=" + objectives + ", learningOutcomes=" + learningOutcomes + ", prerequisites="
				+ prerequisites + ", links=" + links + ", createdBy=" + createdBy + ", createdDate=" + createdDate
				+ ", lastModifiedBy=" + lastModifiedBy + ", lastModifiedDate=" + lastModifiedDate
				+ ", sessionPlanModuleList=" + sessionPlanModuleList + "]";
	}
}
