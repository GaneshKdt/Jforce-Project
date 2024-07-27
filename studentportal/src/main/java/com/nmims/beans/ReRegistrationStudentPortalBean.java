package com.nmims.beans;

import java.io.Serializable;

/**
 * old name - ReRegistrationBean
 * @author
 *
 */

public class ReRegistrationStudentPortalBean   implements Serializable  {
	
	private boolean error=true;
	private boolean success=false;	
	private boolean isTermCleared = false;
	private boolean isPaymentApplicable = false;
	
	private String acadYear;
	private String acadMonth;
	private String startTime;
	private String endTime;
	private String sapId;
	private String dob;
	private String consumerProgramStructureId;
	private String url="";
				
	
	public ReRegistrationStudentPortalBean() {
		
	}
		
	public ReRegistrationStudentPortalBean(boolean error, boolean success, boolean isTermCleared,
			boolean isPaymentApplicable, String acadYear, String acadMonth, String startTime, String endTime,
			String sapId, String dob, String consumerProgramStructureId, String url) {
		this.error = error;
		this.success = success;
		this.isTermCleared = isTermCleared;
		this.isPaymentApplicable = isPaymentApplicable;
		this.acadYear = acadYear;
		this.acadMonth = acadMonth;
		this.startTime = startTime;
		this.endTime = endTime;
		this.sapId = sapId;
		this.dob = dob;
		this.consumerProgramStructureId = consumerProgramStructureId;
		this.url = url;
	}


	public boolean isTermCleared() {
		return isTermCleared;
	}
	public void setTermCleared(boolean isTermCleared) {
		this.isTermCleared = isTermCleared;
	}	
	public boolean isPaymentApplicable() {
		return isPaymentApplicable;
	}
	public void setPaymentApplicable(boolean isPaymentApplicable) {
		this.isPaymentApplicable = isPaymentApplicable;
	}
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
	public String getConsumerProgramStructureId() {
		return consumerProgramStructureId;
	}
	public void setConsumerProgramStructureId(String consumerProgramStructureId) {
		this.consumerProgramStructureId = consumerProgramStructureId;
	}
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
	@Override
	public String toString() {
		return "ReRegistrationStudentPortalBean [acadYear=" + acadYear + ", acadMonth=" + acadMonth + ", startTime="
				+ startTime + ", endTime=" + endTime + ", error=" + error + ", success=" + success + ", url=" + url
				+ ", isTermCleared=" + isTermCleared + ", isPaymentApplicable=" + isPaymentApplicable + ", sapId="
				+ sapId + ", dob=" + dob + ", consumerProgramStructureId=" + consumerProgramStructureId + "]";
	}
}
