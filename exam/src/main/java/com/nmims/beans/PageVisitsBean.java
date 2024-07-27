package com.nmims.beans;

import java.io.Serializable;

public class PageVisitsBean  implements Serializable  {

	//id, sapid, pageId, initialTimeStamp, timeSpent, deviceType, deviceName, deviceOS, deviceSystemVersion, applicationType, applicationVersion, browserName, browserVersion, ipAddress, latitude, longitude
	private Integer id; 
	private String  sapid; 
	private Integer pageId; 
	private String  initialTimeStamp; 
	private String  timeSpent; 
	private String  deviceType; 
	private String  deviceName; 
	private String  deviceOS; 
	private String  deviceSystemVersion; 
	private String  applicationType; 
	private String  applicationVersion; 
	private String  browserName; 
	private String  browserVersion; 
	private String  ipAddress; 
	private String  latitude; 
	private String  longitude;
	private String visiteddate;
	private String totalTimeSpent;
	private String path;
	
	
	
	public String getPath() {
		return path;
	}
	public void setPath(String path) {
		this.path = path;
	}
	public String getVisiteddate() {
		return visiteddate;
	}
	public void setVisiteddate(String visiteddate) {
		this.visiteddate = visiteddate;
	}
	public String getTotalTimeSpent() {
		return totalTimeSpent;
	}
	public void setTotalTimeSpent(String totalTimeSpent) {
		this.totalTimeSpent = totalTimeSpent;
	}
	public Integer getId() {
		return id;
	}
	public void setId(Integer id) {
		this.id = id;
	}
	public String getSapid() {
		return sapid;
	}
	public void setSapid(String sapid) {
		this.sapid = sapid;
	}
	public Integer getPageId() {
		return pageId;
	}
	public void setPageId(Integer pageId) {
		this.pageId = pageId;
	}
	public String getInitialTimeStamp() {
		return initialTimeStamp;
	}
	public void setInitialTimeStamp(String initialTimeStamp) {
		this.initialTimeStamp = initialTimeStamp;
	}
	public String getTimeSpent() {
		return timeSpent;
	}
	public void setTimeSpent(String timeSpent) {
		this.timeSpent = timeSpent;
	}
	public String getDeviceType() {
		return deviceType;
	}
	public void setDeviceType(String deviceType) {
		this.deviceType = deviceType;
	}
	public String getDeviceName() {
		return deviceName;
	}
	public void setDeviceName(String deviceName) {
		this.deviceName = deviceName;
	}
	public String getDeviceOS() {
		return deviceOS;
	}
	public void setDeviceOS(String deviceOS) {
		this.deviceOS = deviceOS;
	}
	public String getDeviceSystemVersion() {
		return deviceSystemVersion;
	}
	public void setDeviceSystemVersion(String deviceSystemVersion) {
		this.deviceSystemVersion = deviceSystemVersion;
	}
	public String getApplicationType() {
		return applicationType;
	}
	public void setApplicationType(String applicationType) {
		this.applicationType = applicationType;
	}
	public String getApplicationVersion() {
		return applicationVersion;
	}
	public void setApplicationVersion(String applicationVersion) {
		this.applicationVersion = applicationVersion;
	}
	public String getBrowserName() {
		return browserName;
	}
	public void setBrowserName(String browserName) {
		this.browserName = browserName;
	}
	public String getBrowserVersion() {
		return browserVersion;
	}
	public void setBrowserVersion(String browserVersion) {
		this.browserVersion = browserVersion;
	}
	public String getIpAddress() {
		return ipAddress;
	}
	public void setIpAddress(String ipAddress) {
		this.ipAddress = ipAddress;
	}
	public String getLatitude() {
		return latitude;
	}
	public void setLatitude(String latitude) {
		this.latitude = latitude;
	}
	public String getLongitude() {
		return longitude;
	}
	public void setLongitude(String longitude) {
		this.longitude = longitude;
	}
	
	
	
	
}
