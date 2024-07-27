package com.nmims.beans;

import java.io.Serializable;
import java.util.Date;

public class CSHomeEntitlementInfo  implements Serializable{

	private String entitlementType;
	private String entitlementName;
	private int totalSessions;
	private int sessionsLeft;
	private int sessionsActivationsAvailable;
	private Date nextActivationAvailableDate;
	
	public String getEntitlementType() {
		return entitlementType;
	}
	public void setEntitlementType(String entitlementType) {
		this.entitlementType = entitlementType;
	}
	public String getEntitlementName() {
		return entitlementName;
	}
	public void setEntitlementName(String entitlementName) {
		this.entitlementName = entitlementName;
	}
	public int getTotalSessions() {
		return totalSessions;
	}
	public void setTotalSessions(int totalSessions) {
		this.totalSessions = totalSessions;
	}
	public int getSessionsLeft() {
		return sessionsLeft;
	}
	public void setSessionsLeft(int sessionsLeft) {
		this.sessionsLeft = sessionsLeft;
	}
	public int getSessionsActivationsAvailable() {
		return sessionsActivationsAvailable;
	}
	public void setSessionsActivationsAvailable(int sessionsActivationsAvailable) {
		this.sessionsActivationsAvailable = sessionsActivationsAvailable;
	}
	public Date getNextActivationAvailableDate() {
		return nextActivationAvailableDate;
	}
	public void setNextActivationAvailableDate(Date nextActivationAvailableDate) {
		this.nextActivationAvailableDate = nextActivationAvailableDate;
	}
}
