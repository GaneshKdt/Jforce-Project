package com.nmims.beans;

import java.io.Serializable;

@SuppressWarnings("serial")
public class VideoAndSessionAttendanceCountStudentBean implements Serializable {
	
	private String sapid;
	private String subject_name;
	private int subject_count;
	private String track;
	
	public VideoAndSessionAttendanceCountStudentBean() {
		super();
	}
	
	public String getSapid() {
		return sapid;
	}
	
	public String getSubject_name() {
		return subject_name;
	}
	
	public int getSubject_count() {
		return subject_count;
	}
	
	public String getTrack() {
		return track;
	}
	
	public void setSapid(String sapid) {
		this.sapid = sapid;
	}
	
	public void setSubject_name(String subject_name) {
		this.subject_name = subject_name;
	}
	
	public void setSubject_count(int subject_count) {
		this.subject_count = subject_count;
	}
	
	public void setTrack(String track) {
		this.track = track;
	}
	
	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("VideoAndSessionAttendanceCountStudentBean [sapid=");
		builder.append(sapid);
		builder.append(", subject_name=");
		builder.append(subject_name);
		builder.append(", subject_count=");
		builder.append(subject_count);
		builder.append(", track=");
		builder.append(track);
		builder.append("]");
		return builder.toString();
	}
	
}