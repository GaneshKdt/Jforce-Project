package com.nmims.beans;

import java.io.Serializable;
import java.util.List;

public class StudentFeedback implements Serializable {

	private List<SessionFeedbackAnswer> feedback;
	private String sapid;
	private String sessionId;
	private String sessionAttendanceId;
	private boolean sessionSuccessfullyViewed;
	private String sessionNotViewedReason;
	
	public boolean isSessionSuccessfullyViewed() {
		return sessionSuccessfullyViewed;
	}
	public void setSessionSuccessfullyViewed(boolean sessionSuccessfullyViewed) {
		this.sessionSuccessfullyViewed = sessionSuccessfullyViewed;
	}
	public String getSessionNotViewedReason() {
		return sessionNotViewedReason;
	}
	public void setSessionNotViewedReason(String sessionNotViewedReason) {
		this.sessionNotViewedReason = sessionNotViewedReason;
	}
	public List<SessionFeedbackAnswer> getFeedback() {
		return feedback;
	}
	public void setFeedback(List<SessionFeedbackAnswer> feedback) {
		this.feedback = feedback;
	}
	public String getSapid() {
		return sapid;
	}
	public void setSapid(String sapid) {
		this.sapid = sapid;
	}
	public String getSessionId() {
		return sessionId;
	}
	public void setSessionId(String sessionId) {
		this.sessionId = sessionId;
	}
	public String getSessionAttendanceId() {
		return sessionAttendanceId;
	}
	public void setSessionAttendanceId(String sessionAttendanceId) {
		this.sessionAttendanceId = sessionAttendanceId;
	}
}
