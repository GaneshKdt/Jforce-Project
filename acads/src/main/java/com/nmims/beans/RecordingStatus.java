package com.nmims.beans;

import java.io.Serializable;

public class RecordingStatus  implements Serializable  {
	private String meetingId;
	private String sessionId;
	private String status;
	private String error;
	private String vimeoId;
	private String vimeoStatus;
	
	public String getMeetingId() {
		return meetingId;
	}
	public void setMeetingId(String meetingId) {
		this.meetingId = meetingId;
	}
	public String getSessionId() {
		return sessionId;
	}
	public void setSessionId(String sessionId) {
		this.sessionId = sessionId;
	}
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}
	public String getError() {
		return error;
	}
	public void setError(String error) {
		this.error = error;
	}
	public String getVimeoId() {
		return vimeoId;
	}
	public void setVimeoId(String vimeoId) {
		this.vimeoId = vimeoId;
	}
	public String getVimeoStatus() {
		return vimeoStatus;
	}
	public void setVimeoStatus(String vimeoStatus) {
		this.vimeoStatus = vimeoStatus;
	}
	
}
