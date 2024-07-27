package com.nmims.beans;

import java.util.List;

public class FedExMergerBean {
	
	private String url;
	private int pageNo=1;
	private String successMessage;
	private String errorMessage;
	private Integer Row;
	
	public String getUrl() {
		return url;
	}
	public void setUrl(String url) {
		this.url = url;
	}
	public int getPageNo() {
		return pageNo;
	}
	public void setPageNo(int pageNo) {
		this.pageNo = pageNo;
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
	public Integer getRow() {
		return Row;
	}
	public void setRow(Integer row) {
		Row = row;
	}
	@Override
	public String toString() {
		return "FedExMergerBean [url=" + url + ", pageNo=" + pageNo + ", successMessage=" + successMessage
				+ ", errorMessage=" + errorMessage + ", Row=" + Row + "]";
	}

}
