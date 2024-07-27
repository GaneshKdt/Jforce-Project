package com.nmims.beans;

import java.io.Serializable;

public class CareerForumHomeModelBean  implements Serializable{

	private String description;
	
	private CareerForumEventsModelBean events;


	private ActivationInfo activationInfo;


	public ActivationInfo getActivationInfo() {
		return activationInfo;
	}


	public void setActivationInfo(ActivationInfo activationInfo) {
		this.activationInfo = activationInfo;
	}


	public String getDescription() {
		return description;
	}


	public void setDescription(String description) {
		this.description = description;
	}


	public CareerForumEventsModelBean getEvents() {
		return events;
	}


	public void setEvents(CareerForumEventsModelBean events) {
		this.events = events;
	}
	
}
