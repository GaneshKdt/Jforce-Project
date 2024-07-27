package com.nmims.beans;

public class FailedRegistrationResponseBean {

	private String error;
	private String success;
	private String downloadExcel;
	private String count;
	
	public String getCount() {
		return count;
	}
	public void setCount(String count) {
		this.count = count;
	}
	public String getError() {
		return error;
	}
	public void setError(String error) {
		this.error = error;
	}
	public String getSuccess() {
		return success;
	}
	public void setSuccess(String success) {
		this.success = success;
	}
	public String getDownloadExcel() {
		return downloadExcel;
	}
	public void setDownloadExcel(String downloadExcel) {
		this.downloadExcel = downloadExcel;
	}
}
