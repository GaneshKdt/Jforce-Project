package com.nmims.beans;

import java.io.Serializable;

public class ServiceRequestType   implements Serializable {
	private Long id;
	private String serviceRequestName;
	private String createdBy;
	private String createdDate;
	private String lastModifiedBy;
	private String lastModifiedDate;
	private Integer charges;
	private String startTime;
	private String endTime;
	private String sapIdList;
	private String active;
	
	
	public String getActive() {
		return active;
	}
	public void setActive(String active) {
		this.active = active;
	}
	public String getSapIdList() {
		return sapIdList;
	}
	public void setSapIdList(String sapIdList) {
		this.sapIdList = sapIdList;
	}
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public String getServiceRequestName() {
		return serviceRequestName;
	}
	public void setServiceRequestName(String serviceRequestName) {
		this.serviceRequestName = serviceRequestName;
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
	public Integer getCharges() {
		return charges;
	}
	public void setCharges(Integer charges) {
		this.charges = charges;
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
	
	
}
