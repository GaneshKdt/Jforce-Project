package com.nmims.beans;

import java.io.Serializable;

/**
 * old name - AnalyticsObject
 * @author
 *
 */
public class AnalyticsObjectStudentPortal  implements Serializable {

	private String userAgent;
	private String ipAddress;
	private String sapid;
	private String stackTrace;
	private String module;
	
	public String getStackTrace() {
		return stackTrace;
	}
	public void setStackTrace(String stackTrace) {
		this.stackTrace = stackTrace;
	}
	public String getModule() {
		return module;
	}
	public void setModule(String module) {
		this.module = module;
	}
	public String getUserAgent() {
		return userAgent;
	}
	public void setUserAgent(String userAgent) {
		this.userAgent = userAgent;
	}
	public String getIpAddress() {
		return ipAddress;
	}
	public void setIpAddress(String ipAddress) {
		this.ipAddress = ipAddress;
	}
	public String getSapid() {
		return sapid;
	}
	public void setSapid(String sapid) {
		this.sapid = sapid;
	}
}
