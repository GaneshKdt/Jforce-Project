package com.nmims.timeline.model;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import org.springframework.data.annotation.ReadOnlyProperty;

@Entity
@Table(name="portal.error_analytics", schema="portal")
public class ErrorAnalytics {
	
	//id, sapid, module, fixed, ipAddress, userAgent, stackTrace, createdBy, createdOn, updatedBy, updatedOn

	@Id  
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	
	private String sapid; 
	private String module;
	private Integer fixed;
	private String ipAddress;
	private String userAgent;
	private String stackTrace;
	private String createdBy; 
	private String updatedBy;
	
	@ReadOnlyProperty
	private String createdOn;
	
	
	
	public String getCreatedOn() {
		return createdOn;
	}
	public void setCreatedOn(String createdOn) {
		this.createdOn = createdOn;
	}
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public String getSapid() {
		return sapid;
	}
	public void setSapid(String sapid) {
		this.sapid = sapid;
	}
	public String getModule() {
		return module;
	}
	public void setModule(String module) {
		this.module = module;
	}
	public Integer getFixed() {
		return fixed;
	}
	public void setFixed(Integer fixed) {
		this.fixed = fixed;
	}
	public String getIpAddress() {
		return ipAddress;
	}
	public void setIpAddress(String ipAddress) {
		this.ipAddress = ipAddress;
	}
	public String getUserAgent() {
		return userAgent;
	}
	public void setUserAgent(String userAgent) {
		this.userAgent = userAgent;
	}
	public String getStackTrace() {
		return stackTrace;
	}
	public void setStackTrace(String stackTrace) {
		this.stackTrace = stackTrace;
	}
	public String getCreatedBy() {
		return createdBy;
	}
	public void setCreatedBy(String createdBy) {
		this.createdBy = createdBy;
	}
	public String getUpdatedBy() {
		return updatedBy;
	}
	public void setUpdatedBy(String updatedBy) {
		this.updatedBy = updatedBy;
	}
	
	
	
	
}
