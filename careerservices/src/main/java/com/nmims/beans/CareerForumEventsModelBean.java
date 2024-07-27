package com.nmims.beans;

import java.io.Serializable;

public class CareerForumEventsModelBean  implements Serializable{

	//viewed and not viewed events
	private CareerForumEventStatusModelBean status;

	//Upcoming events and active events 
	private EventScheduleStatusBean schedule;
	
	public CareerForumEventStatusModelBean getStatus() {
		return status;
	}
	public void setStatus(CareerForumEventStatusModelBean status) {
		this.status = status;
	}
	public EventScheduleStatusBean getSchedule() {
		return schedule;
	}
	public void setSchedule(EventScheduleStatusBean schedule) {
		this.schedule = schedule;
	}
}
