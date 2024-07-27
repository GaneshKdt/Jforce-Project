package com.nmims.beans;

import java.io.Serializable;
import java.util.List;

public class SessionFeedbackAnswers implements Serializable {

	private List<SessionFeedbackAnswer> answers;
	private String sessionId;
	private String sapid;
	private String sessionAttendanceId;
	private boolean successfullyAttended;
	private String notAttendedReason;
	
	public boolean getSuccessfullyAttended() {
		return successfullyAttended;
	}
	public void setSuccessfullyAttended(boolean successfullyAttended) {
		this.successfullyAttended = successfullyAttended;
	}
	public String getNotAttendedReason() {
		return notAttendedReason;
	}
	public void setNotAttendedReason(String notAttendedReason) {
		this.notAttendedReason = notAttendedReason;
	}
	public List<SessionFeedbackAnswer> getAnswers() {
		return answers;
	}
	public void setAnswers(List<SessionFeedbackAnswer> answers) {
		this.answers = answers;
	}
	public String getSessionId() {
		return sessionId;
	}
	public void setSessionId(String sessionId) {
		this.sessionId = sessionId;
	}
	public String getSapid() {
		return sapid;
	}
	public void setSapid(String sapid) {
		this.sapid = sapid;
	}
	public String getSessionAttendanceId() {
		return sessionAttendanceId;
	}
	public void setSessionAttendanceId(String sessionFeedbackId) {
		this.sessionAttendanceId = sessionFeedbackId;
	}
}
