package com.nmims.beans;

import java.io.Serializable;
import java.util.List;

public class EventsModelBean  implements Serializable{

	//Upcoming events and active events 
	private List<SessionDayTimeBean> upcomingEvents;
	private List<SessionDayTimeBean> activeEvents;
	
	public List<SessionDayTimeBean> getUpcomingEvents() {
		return upcomingEvents;
	}
	public void setUpcomingEvents(List<SessionDayTimeBean> upcomingEvents) {
		this.upcomingEvents = upcomingEvents;
	}
	public List<SessionDayTimeBean> getActiveEvents() {
		return activeEvents;
	}
	public void setActiveEvents(List<SessionDayTimeBean> activeEvents) {
		this.activeEvents = activeEvents;
	}
}
