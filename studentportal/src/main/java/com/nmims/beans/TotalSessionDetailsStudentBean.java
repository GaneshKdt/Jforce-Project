package com.nmims.beans;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

@SuppressWarnings("serial")
public class TotalSessionDetailsStudentBean implements Serializable {

	private int total_session;
	private int attended_session;
	private String total_duration;
	private Map<String, Map<String, String>> subject_details = new HashMap<String, Map<String, String>>();
	
	public TotalSessionDetailsStudentBean() {
		super();
	}

	public int getTotal_session() {
		return total_session;
	}

	public int getAttended_session() {
		return attended_session;
	}

	public String getTotal_duration() {
		return total_duration;
	}

	public Map<String, Map<String, String>> getSubject_Details() {
		return subject_details;
	}

	public void setTotal_session(int total_session) {
		this.total_session = total_session;
	}

	public void setAttended_session(int attended_session) {
		this.attended_session = attended_session;
	}

	public void setTotal_duration(String total_duration) {
		this.total_duration = total_duration;
	}

	public void setSubject_Details(Map<String, Map<String, String>> subject_details) {
		this.subject_details = subject_details;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("TotalSessionDetailsStudentBean [total_session=");
		builder.append(total_session);
		builder.append(", attended_session=");
		builder.append(attended_session);
		builder.append(", total_duration=");
		builder.append(total_duration);
		builder.append(", subject_details=");
		builder.append(subject_details);
		builder.append("]");
		return builder.toString();
	}
	
}