package com.nmims.beans;

import java.io.Serializable;

public class Event extends BaseStudentPortalBean implements Serializable{
	private String sapId;
	private String eventName;
	private String response;
	private String registered;
	private String online_EventId;
	public String getSapId() {
		return sapId;
	}
	public void setSapId(String sapId) {
		this.sapId = sapId;
	}
	public String getEventName() {
		return eventName;
	}
	public void setEventName(String eventName) {
		this.eventName = eventName;
	}
	public String getResponse() {
		return response;
	}
	public void setResponse(String response) {
		this.response = response;
	}
	
	
	public String getRegistered() {
		return registered;
	}
	public void setRegistered(String registered) {
		this.registered = registered;
	}

	public String getOnline_EventId() {
		return online_EventId;
	}
	public void setOnline_EventId(String online_EventId) {
		this.online_EventId = online_EventId;
	}
	@Override
	public String toString() {
		return "Event [sapId=" + sapId + ", eventName=" + eventName
				+ ", response=" + response + ", registered=" + registered
				+ ", online_EventId=" + online_EventId + "]";
	}
	
	
	
}
