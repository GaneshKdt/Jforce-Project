package com.nmims.beans;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;


@SuppressWarnings("serial")
public class TotalVideoDetailsStudentBean implements Serializable {

	private String subject_name;
	private int total_attempt;
	private String total_duration;
	private Map<String, Map<String, String>> subject_details = new HashMap<String, Map<String, String>>();
	
	public TotalVideoDetailsStudentBean() {
		super();
	}

	public String getSubject_name() {
		return subject_name;
	}

	public int getTotal_attempt() {
		return total_attempt;
	}

	public String getTotal_duration() {
		return total_duration;
	}

	public Map<String, Map<String, String>> getSubject_details() {
		return subject_details;
	}

	public void setSubject_name(String subject_name) {
		this.subject_name = subject_name;
	}

	public void setTotal_attempt(int total_attempt) {
		this.total_attempt = total_attempt;
	}

	public void setTotal_duration(String total_duration) {
		this.total_duration = total_duration;
	}

	public void setSubject_details(Map<String, Map<String, String>> subject_details) {
		this.subject_details = subject_details;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("TotalVideoDetailsStudentBean [subject_name=");
		builder.append(subject_name);
		builder.append(", total_attempt=");
		builder.append(total_attempt);
		builder.append(", total_duration=");
		builder.append(total_duration);
		builder.append(", subject_details=");
		builder.append(subject_details);
		builder.append("]");
		return builder.toString();
	}

}