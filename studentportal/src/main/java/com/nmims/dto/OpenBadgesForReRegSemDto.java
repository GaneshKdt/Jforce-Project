package com.nmims.dto;

public class OpenBadgesForReRegSemDto 
{
	private String createdDate;
	private String sapid;
	private String sem;
	private String userId;
	private String month;
	private String year;
	public String getCreatedDate() {
		return createdDate;
	}
	public void setCreatedDate(String createdDate) {
		this.createdDate = createdDate;
	}
	public String getSapid() {
		return sapid;
	}
	public void setSapid(String sapid) {
		this.sapid = sapid;
	}
	public String getSem() {
		return sem;
	}
	public void setSem(String sem) {
		this.sem = sem;
	}
	public String getUserId() {
		return userId;
	}
	public void setUserId(String userId) {
		this.userId = userId;
	}
	public String getMonth() {
		return month;
	}
	public void setMonth(String month) {
		this.month = month;
	}
	public String getYear() {
		return year;
	}
	public void setYear(String year) {
		this.year = year;
	}
	@Override
	public String toString() {
		return "OpenBadgesForReRegSemDto [createdDate=" + createdDate + ", sapid=" + sapid + ", sem=" + sem
				+ ", userId=" + userId + ", month=" + month + ", year=" + year + "]";
	}

	
	
}
