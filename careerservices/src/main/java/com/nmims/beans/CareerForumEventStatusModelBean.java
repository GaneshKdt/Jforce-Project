package com.nmims.beans;

import java.io.Serializable;
import java.util.List;

public class CareerForumEventStatusModelBean  implements Serializable{
	private List<SessionDayTimeBean> viewedEvents;
	private List<SessionDayTimeBean> notViewedEvents;
	
	public List<SessionDayTimeBean> getViewedEvents() {
		return viewedEvents;
	}
	public void setViewedEvents(List<SessionDayTimeBean> viewedEvents) {
		this.viewedEvents = viewedEvents;
	}
	public List<SessionDayTimeBean> getNotViewedEvents() {
		return notViewedEvents;
	}
	public void setNotViewedEvents(List<SessionDayTimeBean> notViewedEvents) {
		this.notViewedEvents = notViewedEvents;
	}
}
