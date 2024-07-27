package com.nmims.beans;

import java.io.Serializable;

public class BaseCareerservicesBean  implements Serializable{
	private String sapid;
	private String year;
	private String month;
	private String createdBy;
	private String createdDate;
	private String lastModifiedBy;
	private String lastModifiedDate;
    private String tranDateTime;
    private String date;

	

	public String getTranDateTime() {
		return tranDateTime;
	}

	public void setTranDateTime(String tranDateTime) {
		this.tranDateTime = tranDateTime;
	}

	public String getYear() {
		return year;
	}

	public void setYear(String year) {
		this.year = year;
	}

	public String getMonth() {
		return month;
	}

	public void setMonth(String month) {
		this.month = month;
	}

	public String getCreatedBy() {
		return createdBy;
	}

	public void setCreatedBy(String createdBy) {
		this.createdBy = createdBy;
	}

	public String getCreatedDate() {
		return createdDate;
	}

	public void setCreatedDate(String createdDate) {
		this.createdDate = createdDate;
	}

	public String getLastModifiedBy() {
		return lastModifiedBy;
	}

	public void setLastModifiedBy(String lastModifiedBy) {
		this.lastModifiedBy = lastModifiedBy;
	}

	public String getLastModifiedDate() {
		return lastModifiedDate;
	}

	public void setLastModifiedDate(String lastModifiedDate) {
		this.lastModifiedDate = lastModifiedDate;
	}

	public String getSapid() {
		return sapid;
	}

	public void setSapid(String sapid) {
		this.sapid = sapid;
	}

	public String getDate() {
		return date;
	}

	public void setDate(String date) {
		this.date = date;
	}

	@Override
	public String toString() {
		return "BaseBean [sapid=" + sapid + ", year=" + year + ", month="
				+ month + ", createdBy=" + createdBy + ", createdDate="
				+ createdDate + ", lastModifiedBy=" + lastModifiedBy
				+ ", lastModifiedDate=" + lastModifiedDate + ", tranDateTime="
				+ tranDateTime + ", date=" + date + "]";
	}
	
	//Added for test module Start
	protected String compareStringAndSet(String value, String compare, String defaultValue) {
		return compare.equalsIgnoreCase(value) ? compare : defaultValue;
	}
	
	protected String checkYElseSetN(String value) {
		return compareStringAndSet(value, "Y", "N");
	}
	protected String formatDate(String date) {
		if(null == date) return date;
		if(date.length() > 19) {
			return date.substring(0, 19).replace(' ', 'T');
		} else {
			return date.replace(' ', 'T');
		}
	}
	//Added for test module End
	
}
