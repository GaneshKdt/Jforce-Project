package com.nmims.beans;

import java.io.Serializable;

public class ReRegistrationAcadsBean  implements Serializable  {
	
	/**
	 * Change Name from ReRegistrationBean to ReRegistrationAcadsBean for serializable issue
	 */
	
	private String acadYear;
	private String acadMonth;
	private String startTime;
	private String endTime;
	private boolean error=true;
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
				+ ", endTime=" + endTime + ", error=" + error + "]";
	}
	
}
