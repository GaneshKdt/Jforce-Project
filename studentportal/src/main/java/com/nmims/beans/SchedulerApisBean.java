package com.nmims.beans;

import java.io.Serializable;

public class SchedulerApisBean   implements Serializable  {
	private String id;
	private String syncType;
	private String url;
	private String lastSync;
	private String error;
	private String statusUrl;
	private String source;
	private String acadYear;
	private String acadMonth;
	
	public String getAcadYear() {
		return acadYear;
	}
	public void setAcadYear(String acadYear) {
		this.acadYear = acadYear;
	}
	public String getAcadMonth() {
		return acadMonth;
	}
	public void setAcadMonth(String acadMonth) {
		this.acadMonth = acadMonth;
	}
	public String getSource() {
		return source;
	}
	public void setSource(String source) {
		this.source = source;
	}
	public String getStatusUrl() {
		return statusUrl;
	}
	public void setStatusUrl(String statusUrl) {
		this.statusUrl = statusUrl;
	}
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getSyncType() {
		return syncType;
	}
	public void setSyncType(String syncType) {
		this.syncType = syncType;
	}
	public String getUrl() {
		return url;
	}
	public void setUrl(String url) {
		this.url = url;
	}
	public String getLastSync() {
		return lastSync;
	}
	public void setLastSync(String lastSync) {
		this.lastSync = lastSync;
	}
	public String getError() {
		return error;
	}
	public void setError(String error) {
		this.error = error;
	}
	@Override
	public String toString() {
		return "SchedulerApisBean [id=" + id + ", syncType=" + syncType + ", url=" + url + ", lastSync=" + lastSync
				+ ", error=" + error + ", statusUrl=" + statusUrl + ", source=" + source + "]";
	} 
	
	
}
