package com.nmims.beans;

import java.io.Serializable;
import java.util.List;

public class SessionFeedbackQuestionGroup implements Serializable {

	private String feedbackQuestionGroupId;
	private String groupName;
	private List<SessionFeedbackQuestion> questions;
	
	public String getGroupName() {
		return groupName;
	}
	public void setGroupName(String groupName) {
		this.groupName = groupName;
	}
	public List<SessionFeedbackQuestion> getQuestions() {
		return questions;
	}
	public void setQuestions(List<SessionFeedbackQuestion> questions) {
		this.questions = questions;
	}
	public String getFeedbackQuestionGroupId() {
		return feedbackQuestionGroupId;
	}
	public void setFeedbackQuestionGroupId(String feedbackQuestionGroupId) {
		this.feedbackQuestionGroupId = feedbackQuestionGroupId;
	}
}
