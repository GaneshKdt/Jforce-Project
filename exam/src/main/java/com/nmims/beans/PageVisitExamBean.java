package com.nmims.beans;

import java.io.Serializable;
import java.sql.Timestamp;

//spring security related changes rename PageVisitBean to PageVisitExamBean
public class PageVisitExamBean implements Serializable{
	
	private String path;
	
	private String visiteddate;
	
	private String timespent;
	
	private String deviceName;
	
	private String deviceOS;
	
	private String deviceSystemVersion;
	
	private String ipAddress;
	
	private String applicationType;
	
	private String description;
	
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
	public String getTimespent() {
		return timespent;
	}
	public void setTimespent(String timespent) {
		this.timespent = timespent;
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
	public String getIpAddress() {
		return ipAddress;
	}
	public void setIpAddress(String ipAddress) {
		this.ipAddress = ipAddress;
	}
	public String getApplicationType() {
		return applicationType;
	}
	public void setApplicationType(String applicationType) {
		this.applicationType = applicationType;
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	@Override
	public String toString() {
		return "PageVisitBean [path=" + path + ", visiteddate=" + visiteddate + ", timespent=" + timespent + "]";
	}
	
}
