package com.nmims.beans;

import java.io.Serializable;

public class SessionFeedbackQuestion implements Serializable {

	private String feedbackQuestionGroupId;
	private String feedbackQuestionGroupName;
	private String feedbackQuestionId;
	private String questionString;
	private String questionType;
	private boolean giveAdditionalComment;
	
	public String getFeedbackQuestionGroupId() {
		return feedbackQuestionGroupId;
	}
	public void setFeedbackQuestionGroupId(String feedbackQuestionGroupId) {
		this.feedbackQuestionGroupId = feedbackQuestionGroupId;
	}
	public String getFeedbackQuestionId() {
		return feedbackQuestionId;
	}
	public void setFeedbackQuestionId(String feedbackQuestionId) {
		this.feedbackQuestionId = feedbackQuestionId;
	}
	public String getQuestionString() {
		return questionString;
	}
	public void setQuestionString(String questionString) {
		this.questionString = questionString;
	}
	public String getFeedbackQuestionGroupName() {
		return feedbackQuestionGroupName;
	}
	public void setFeedbackQuestionGroupName(String feedbackQuestionGroupName) {
		this.feedbackQuestionGroupName = feedbackQuestionGroupName;
	}
	public String getQuestionType() {
		return questionType;
	}
	public void setQuestionType(String questionType) {
		this.questionType = questionType;
	}
	public boolean isGiveAdditionalComment() {
		return giveAdditionalComment;
	}
	public void setGiveAdditionalComment(boolean giveAdditionalComment) {
		this.giveAdditionalComment = giveAdditionalComment;
	}
}
