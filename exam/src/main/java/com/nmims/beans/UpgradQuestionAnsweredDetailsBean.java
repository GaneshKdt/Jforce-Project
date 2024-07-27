package com.nmims.beans;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

public class UpgradQuestionAnsweredDetailsBean implements Serializable {
	@Override
	public String toString() {
		return " questionNo=" + questionNo + ", question="
				+ question + ", studentAnswer=" + studentAnswer + ", correctAnswer=" + correctAnswer + ", isCorrect="
				+ isCorrect + ", marksObtained=" + marksObtained + ", maxMarks=" + maxMarks;
	}
	
	private Integer questionNo;
	private String question;
	private String studentAnswer;
	private String correctAnswer;
	private String isCorrect;
	private String marksObtained;
	private String maxMarks;
	private String peerPenalty;
	private String onlinePenalty;
	private String remark;
	
	
	
	
	
	public String getRemark() {
		return remark;
	}
	public void setRemark(String remark) {
		this.remark = remark;
	}
	public Integer getQuestionNo() {
		return questionNo;
	}
	public void setQuestionNo(Integer questionNo) {
		this.questionNo = questionNo;
	}
	public String getQuestion() {
		return question;
	}
	public void setQuestion(String question) {
		this.question = question;
	}
	public String getStudentAnswer() {
		return studentAnswer;
	}
	public void setStudentAnswer(String studentAnswer) {
		this.studentAnswer = studentAnswer;
	}
	public String getCorrectAnswer() {
		return correctAnswer;
	}
	public void setCorrectAnswer(String correctAnswer) {
		this.correctAnswer = correctAnswer;
	}
	public String getIsCorrect() {
		return isCorrect;
	}
	public void setIsCorrect(String isCorrect) {
		this.isCorrect = isCorrect;
	}
	public String getMarksObtained() {
		return marksObtained;
	}
	public void setMarksObtained(String marksObtained) {
		this.marksObtained = marksObtained;
	}
	public String getMaxMarks() {
		return maxMarks;
	}
	public void setMaxMarks(String maxMarks) {
		this.maxMarks = maxMarks;
	}
	public String getPeerPenalty() {
		return peerPenalty;
	}
	public void setPeerPenalty(String peerPenalty) {
		this.peerPenalty = peerPenalty;
	}
	public String getOnlinePenalty() {
		return onlinePenalty;
	}
	public void setOnlinePenalty(String onlinePenalty) {
		this.onlinePenalty = onlinePenalty;
	}
	
	
}
