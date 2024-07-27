package com.nmims.beans;

import java.io.Serializable;
import java.util.ArrayList;

@SuppressWarnings("serial")
public class TimeSpentStudentBean implements Serializable {

	private String sapid;
	private String day_name;
	private String week_name;
	private int hours;
	private int minutes;
	private int seconds;
	
	ArrayList<Integer> pss_id = new ArrayList<Integer>();
	
	public TimeSpentStudentBean() {
		super();
	}

	public String getSapid() {
		return sapid;
	}

	public String getDay_name() {
		return day_name;
	}

	public String getWeek_name() {
		return week_name;
	}

	public int getHours() {
		return hours;
	}

	public int getMinutes() {
		return minutes;
	}

	public int getSeconds() {
		return seconds;
	}

	public void setSapid(String sapid) {
		this.sapid = sapid;
	}

	public void setDay_name(String day_name) {
		this.day_name = day_name;
	}

	public void setWeek_name(String week_name) {
		this.week_name = week_name;
	}

	public void setHours(int hours) {
		this.hours = hours;
	}

	public void setMinutes(int minutes) {
		this.minutes = minutes;
	}

	public void setSeconds(int seconds) {
		this.seconds = seconds;
	}
	
	public ArrayList<Integer> getPss_id() {
		return pss_id;
	}

	public void setPss_id(ArrayList<Integer> pss_id) {
		this.pss_id = pss_id;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("TimeSpentStudentBean [sapid=");
		builder.append(sapid);
		builder.append(", day_name=");
		builder.append(day_name);
		builder.append(", week_name=");
		builder.append(week_name);
		builder.append(", hours=");
		builder.append(hours);
		builder.append(", minutes=");
		builder.append(minutes);
		builder.append(", seconds=");
		builder.append(seconds);
		builder.append(", pss_id=");
		builder.append(pss_id);
		builder.append("]");
		return builder.toString();
	}
	
}