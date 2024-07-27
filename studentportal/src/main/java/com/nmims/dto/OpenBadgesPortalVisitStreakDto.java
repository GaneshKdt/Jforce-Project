package com.nmims.dto;

import java.util.List;

public class OpenBadgesPortalVisitStreakDto {
	
	private String sapId;
	private Long timeSpent;
	private String createdDate;
	private Integer semester;
	private String programNameFull;
	private Integer userId;
	private Integer criteriaValue;
	private List<String> streakDates;
	
	public String getSapId() {
		return sapId;
	}
	public void setSapId(String sapId) {
		this.sapId = sapId;
	}
	public Long getTimeSpent() {
		return timeSpent;
	}
	public void setTimeSpent(Long timeSpent) {
		this.timeSpent = timeSpent;
	}
	public String getCreatedDate() {
		return createdDate;
	}
	public void setCreatedDate(String createdDate) {
		this.createdDate = createdDate;
	}
	
	public Integer getSemester() {
		return semester;
	}
	public void setSemester(Integer semester) {
		this.semester = semester;
	}
	public String getProgramNameFull() {
		return programNameFull;
	}
	public void setProgramNameFull(String programNameFull) {
		this.programNameFull = programNameFull;
	}
	public Integer getUserId() {
		return userId;
	}
	public void setUserId(Integer userId) {
		this.userId = userId;
	}
	public Integer getCriteriaValue() {
		return criteriaValue;
	}
	public void setCriteriaValue(Integer criteriaValue) {
		this.criteriaValue = criteriaValue;
	}
	
	public List<String> getStreakDates() {
		return streakDates;
	}
	public void setStreakDates(List<String> streakDates) {
		this.streakDates = streakDates;
	}
	@Override
	public String toString() {
		return "OpenBadgesPortalVisitStreakDto [sapId=" + sapId + ", timeSpent=" + timeSpent + ", createdDate="
				+ createdDate + ", semester=" + semester + ", programNameFull=" + programNameFull + ", userId=" + userId
				+ ", criteriaValue=" + criteriaValue + ", streakDates=" + streakDates + "]";
	}
	
}
