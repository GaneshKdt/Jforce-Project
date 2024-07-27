package com.nmims.beans;

import java.io.Serializable;

@SuppressWarnings("serial")
public class LoginLogBean implements Serializable{

	private String sapid;
	private String ipAddress;
	private String operatingSystem;
	private String browserDetails;
	private String firstLogin;
	private String lastLogin;
	private String mailerTriggredOn;
	
	public String getSapid() {
		return sapid;
	}
	public void setSapid(String sapid) {
		this.sapid = sapid;
	}
	public String getIpAddress() {
		return ipAddress;
	}
	public void setIpAddress(String ipAddress) {
		this.ipAddress = ipAddress;
	}
	public String getOperatingSystem() {
		return operatingSystem;
	}
	public void setOperatingSystem(String operatingSystem) {
		this.operatingSystem = operatingSystem;
	}
	public String getBrowserDetails() {
		return browserDetails;
	}
	public void setBrowserDetails(String browserDetails) {
		this.browserDetails = browserDetails;
	}
	
	public String getFirstLogin() {
		return firstLogin;
	}
	public void setFirstLogin(String firstLogin) {
		this.firstLogin = firstLogin;
	}
	public String getLastLogin() {
		return lastLogin;
	}
	public void setLastLogin(String lastLogin) {
		this.lastLogin = lastLogin;
	}
	public String getMailerTriggredOn() {
		return mailerTriggredOn;
	}
	public void setMailerTriggredOn(String mailerTriggredOn) {
		this.mailerTriggredOn = mailerTriggredOn;
	}
	@Override
	public String toString() {
		return "LoginDetailsBean [sapid=" + sapid + ", ipAddress=" + ipAddress + ", operatingSystem=" + operatingSystem 
				+ ", browserDetails=" + browserDetails + "]";
	}
	
}
