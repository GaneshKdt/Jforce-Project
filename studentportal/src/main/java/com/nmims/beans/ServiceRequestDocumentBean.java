package com.nmims.beans;

import java.io.Serializable;

public class ServiceRequestDocumentBean   implements Serializable  {
	private Long id;
	private Long serviceRequestId;
	private String documentName;
	private String filePath;
	private String errorMessage;
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public Long getServiceRequestId() {
		return serviceRequestId;
	}
	public void setServiceRequestId(Long serviceRequestId) {
		this.serviceRequestId = serviceRequestId;
	}
	public String getDocumentName() {
		return documentName;
	}
	public void setDocumentName(String documentName) {
		this.documentName = documentName;
	}
	public String getFilePath() {
		return filePath;
	}
	public void setFilePath(String filePath) {
		this.filePath = filePath;
	}
	public String getErrorMessage() {
		return errorMessage;
	}
	public void setErrorMessage(String errorMessage) {
		this.errorMessage = errorMessage;
	}
	
	@Override
	public String toString() {
		return "ServiceRequestDocumentBean [id=" + id + ", serviceRequestId=" + serviceRequestId + ", documentName="
				+ documentName + ", filePath=" + filePath + ", errorMessage=" + errorMessage + "]";
	}
}
