package com.nmims.beans;

import java.io.Serializable;
import java.util.List;

public class MettlResultQuestionWiseResponse  implements Serializable  {
	
	private String questionId;
	private String apiQuestionType;
	private String version;
	private MettlResultQuestionResponse questionResponse;
	private double minMarks;
	private double maxMarks;
	private double marksScored;
	private boolean isAttempted;
	private int timeSpent;
	private String skillName;
	private List<MettlResultEvaluatorData> evaluatorData;
	
	public String getQuestionId() {
		return questionId;
	}
	public void setQuestionId(String questionId) {
		this.questionId = questionId;
	}
	public String getApiQuestionType() {
		return apiQuestionType;
	}
	public void setApiQuestionType(String apiQuestionType) {
		this.apiQuestionType = apiQuestionType;
	}
	public String getVersion() {
		return version;
	}
	public void setVersion(String version) {
		this.version = version;
	}
	public MettlResultQuestionResponse getQuestionResponse() {
		return questionResponse;
	}
	public void setQuestionResponse(MettlResultQuestionResponse questionResponse) {
		this.questionResponse = questionResponse;
	}
	public double getMinMarks() {
		return minMarks;
	}
	public void setMinMarks(double minMarks) {
		this.minMarks = minMarks;
	}
	public double getMaxMarks() {
		return maxMarks;
	}
	public void setMaxMarks(double maxMarks) {
		this.maxMarks = maxMarks;
	}
	public double getMarksScored() {
		return marksScored;
	}
	public void setMarksScored(double marksScored) {
		this.marksScored = marksScored;
	}
	public boolean isAttempted() {
		return isAttempted;
	}
	public void setAttempted(boolean isAttempted) {
		this.isAttempted = isAttempted;
	}
	public int getTimeSpent() {
		return timeSpent;
	}
	public void setTimeSpent(int timeSpent) {
		this.timeSpent = timeSpent;
	}
	public String getSkillName() {
		return skillName;
	}
	public void setSkillName(String skillName) {
		this.skillName = skillName;
	}
	public List<MettlResultEvaluatorData> getEvaluatorData() {
		return evaluatorData;
	}
	public void setEvaluatorData(List<MettlResultEvaluatorData> evaluatorData) {
		this.evaluatorData = evaluatorData;
	}
	@Override
	public String toString() {
		return "MettlResultQuestionWiseResponse [questionId=" + questionId + ", apiQuestionType=" + apiQuestionType
				+ ", version=" + version + ", questionResponse=" + questionResponse + ", minMarks=" + minMarks
				+ ", maxMarks=" + maxMarks + ", marksScored=" + marksScored + ", isAttempted=" + isAttempted
				+ ", timeSpent=" + timeSpent + ", skillName=" + skillName + ", evaluatorData=" + evaluatorData + "]";
	}
}
