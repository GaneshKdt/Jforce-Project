package com.nmims.beans;

import java.io.Serializable;

public class SessionFeedbackAnswer implements Serializable {

	private String feedbackQuestionId;
	private String value;
	private String comment;
	
	public String getFeedbackQuestionId() {
		return feedbackQuestionId;
	}
	public void setFeedbackQuestionId(String feedbackQuestionId) {
		this.feedbackQuestionId = feedbackQuestionId;
	}
	public String getValue() {
		return value;
	}
	public void setValue(String value) {
		this.value = value;
	}
	public String getComment() {
		return comment;
	}
	public void setComment(String comment) {
		this.comment = comment;
	}
}
