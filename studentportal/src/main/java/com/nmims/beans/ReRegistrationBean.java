package com.nmims.beans;

public class ReRegistrationBean {
	private String acadYear;
	private String acadMonth;
	private String startTime;
	private String endTime;
	private boolean error=true;
	private boolean success=false;
	private String url="";
	
	public boolean isSuccess() {
		return success;
	}
	public void setSuccess(boolean success) {
		this.success = success;
	}
	public String getUrl() {
		return url;
	}
	public void setUrl(String url) {
		this.url = url;
	}
	public String getAcadYear() {
		return acadYear;
	}
	public String getAcadMonth() {
		return acadMonth;
	}
	public String getStartTime() {
		return startTime;
	}
	public String getEndTime() {
		return endTime;
	}
	public boolean isError() {
		return error;
	}
	public void setAcadYear(String acadYear) {
		this.acadYear = acadYear;
	}
	public void setAcadMonth(String acadMonth) {
		this.acadMonth = acadMonth;
	}
	public void setStartTime(String startTime) {
		this.startTime = startTime;
	}
	public void setEndTime(String endTime) {
		this.endTime = endTime;
	}
	public void setError(boolean error) {
		this.error = error;
	}
	@Override
	public String toString() {
		return "ReRegistrationBean [acadYear=" + acadYear + ", acadMonth=" + acadMonth + ", startTime=" + startTime
				+ ", endTime=" + endTime + ", error=" + error + ", url=" + url + "]";
	}

	/**
	 * added by Riya for Re-Registration link
	 */
	private String sapId;
	private String dob;

	public String getSapId() {
		return sapId;
	}
	public void setSapId(String sapId) {
		this.sapId = sapId;
	}
	public String getDob() {
		return dob;
	}
	public void setDob(String dob) {
		this.dob = dob;
	}
}
