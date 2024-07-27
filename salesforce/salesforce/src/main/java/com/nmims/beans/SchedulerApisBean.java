package com.nmims.beans;

public class SchedulerApisBean {

	private String id;
	private String syncType;
	private String url;
	private String lastSync;
	private String error;
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

}
