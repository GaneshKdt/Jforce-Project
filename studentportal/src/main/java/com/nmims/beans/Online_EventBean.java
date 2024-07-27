package com.nmims.beans;

import java.io.Serializable;
import java.util.Date;

public class Online_EventBean extends BaseStudentPortalBean implements Serializable {
	
	public String id;
	public String eventName;
	public Date startDate;
	public Date endDate;
	public String program;
	public String sem;
	public String PrgmStructApplicable;
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getEventName() {
		return eventName;
	}
	public void setEventName(String eventName) {
		this.eventName = eventName;
	}
	public Date getStartDate() {
		return startDate;
	}
	public void setStartDate(Date startDate) {
		this.startDate = startDate;
	}
	public Date getEndDate() {
		return endDate;
	}
	public void setEndDate(Date endDate) {
		this.endDate = endDate;
	}
	public String getProgram() {
		return program;
	}
	public void setProgram(String program) {
		this.program = program;
	}
	public String getSem() {
		return sem;
	}
	public void setSem(String sem) {
		this.sem = sem;
	}
	public String getPrgmStructApplicable() {
		return PrgmStructApplicable;
	}
	public void setPrgmStructApplicable(String prgmStructApplicable) {
		PrgmStructApplicable = prgmStructApplicable;
	}
	@Override
	public String toString() {
		return "Online_Event [id=" + id + ", eventName=" + eventName
				+ ", startDate=" + startDate + ", endDate=" + endDate
				+ ", program=" + program + ", sem=" + sem
				+ ", PrgmStructApplicable=" + PrgmStructApplicable + "]";
	}

}
