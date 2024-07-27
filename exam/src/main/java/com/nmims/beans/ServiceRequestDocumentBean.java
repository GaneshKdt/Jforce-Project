package com.nmims.beans;

import java.io.Serializable;
import java.util.List;

public class ServiceRequestDocumentBean implements Serializable {
	
	private List<ServiceRequestBean> serviceRequestBean;
	private boolean error = false;
	private String errorMessage;
	
	
	public List<ServiceRequestBean> getServiceRequestBean() {
		return serviceRequestBean;
	}
	public void setServiceRequestBean(List<ServiceRequestBean> serviceRequestBean) {
		this.serviceRequestBean = serviceRequestBean;
	}
	public boolean isError() {
		return error;
	}
	public void setError(boolean error) {
		this.error = error;
	}
	public String getErrorMessage() {
		return errorMessage;
	}
	public void setErrorMessage(String errorMessage) {
		this.errorMessage = errorMessage;
	}
	
	
	@Override
	public String toString() {
		return "ServiceRequestDocumentBean [serviceRequestBean=" + serviceRequestBean + ", error=" + error
				+ ", errorMessage=" + errorMessage + "]";
	}
}
