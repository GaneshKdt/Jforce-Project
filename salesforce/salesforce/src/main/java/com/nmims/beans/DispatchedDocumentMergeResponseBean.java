package com.nmims.beans;

import java.util.List;

public class DispatchedDocumentMergeResponseBean {

	private String url;
	private String successMessage;
	private String errorMessage;
	private List<FedExMergerBean> successList;
	private List<FedExMergerBean> errorList;
	
	public String getUrl() {
		return url;
	}
	public void setUrl(String url) {
		this.url = url;
	}
	public String getSuccessMessage() {
		return successMessage;
	}
	public void setSuccessMessage(String successMessage) {
		this.successMessage = successMessage;
	}
	public String getErrorMessage() {
		return errorMessage;
	}
	public void setErrorMessage(String errorMessage) {
		this.errorMessage = errorMessage;
	}
	public List<FedExMergerBean> getSuccessList() {
		return successList;
	}
	public void setSuccessList(List<FedExMergerBean> successList) {
		this.successList = successList;
	}
	public List<FedExMergerBean> getErrorList() {
		return errorList;
	}
	public void setErrorList(List<FedExMergerBean> errorList) {
		this.errorList = errorList;
	}
	
	@Override
	public String toString() {
		return "ExcelResponseBean [url=" + url + ", successMessage=" + successMessage + ", errorMessage=" + errorMessage
				+ ", successList=" + successList + ", errorList=" + errorList + "]";
	}

}
