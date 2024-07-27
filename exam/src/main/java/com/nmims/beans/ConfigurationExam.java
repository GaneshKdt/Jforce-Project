package com.nmims.beans;

import java.io.Serializable;

//spring security related changes rename Configuration to ConfigurationExam
public class ConfigurationExam implements Serializable{
	private String id;
	private String configurationType;
	private String startTime;
	private String endTime;
	private String createdBy;
	private String createdDate;
	private String lastModifiedBy;
	private String lastModifiedDate;
	private String extendStartTime;
	private String extendEndTime;
	
	public String getExtendStartTime() {
		return extendStartTime;
	}
	public void setExtendStartTime(String extendStartTime) {
		this.extendStartTime = extendStartTime;
	}
	public String getExtendEndTime() {
		return extendEndTime;
	}
	public void setExtendEndTime(String extendEndTime) {
		this.extendEndTime = extendEndTime;
	}
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getConfigurationType() {
		return configurationType;
	}
	public void setConfigurationType(String configurationType) {
		this.configurationType = configurationType;
	}
	public String getStartTime() {
		return startTime;
	}
	public void setStartTime(String startTime) {
		this.startTime = startTime;
	}
	public String getEndTime() {
		return endTime;
	}
	public void setEndTime(String endTime) {
		this.endTime = endTime;
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
