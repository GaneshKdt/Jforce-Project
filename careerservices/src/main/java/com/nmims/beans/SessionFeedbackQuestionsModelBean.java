package com.nmims.beans;

import java.io.Serializable;
import java.util.List;

public class SessionFeedbackQuestionsModelBean implements Serializable{

	private String sessionId;
	private String sapid;
	private SessionDayTimeBean sessionDetails;
	private boolean sessionAttended;
	private List<SessionFeedbackQuestionGroup> feedbackQuestions;
	
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
	public SessionDayTimeBean getSessionDetails() {
		return sessionDetails;
	}
	public void setSessionDetails(SessionDayTimeBean sessionDetails) {
		this.sessionDetails = sessionDetails;
	}
	public boolean getSessionAttended() {
		return sessionAttended;
	}
	public void setSessionAttended(boolean sessionAttended) {
		this.sessionAttended = sessionAttended;
	}
	public List<SessionFeedbackQuestionGroup> getFeedbackQuestions() {
		return feedbackQuestions;
	}
	public void setFeedbackQuestions(List<SessionFeedbackQuestionGroup> feedbackQuestions) {
		this.feedbackQuestions = feedbackQuestions;
	}
}
