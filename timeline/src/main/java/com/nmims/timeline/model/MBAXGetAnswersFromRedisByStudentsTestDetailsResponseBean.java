package com.nmims.timeline.model;

import java.util.List;

public class MBAXGetAnswersFromRedisByStudentsTestDetailsResponseBean {
	
	private String status;
	private String message;
	private List<MBAXStudentQuestionResponseBean> answersFromReds;
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}
	public String getMessage() {
		return message;
	}
	public void setMessage(String message) {
		this.message = message;
	}
	public List<MBAXStudentQuestionResponseBean> getAnswersFromReds() {
		return answersFromReds;
	}
	public void setAnswersFromReds(List<MBAXStudentQuestionResponseBean> answersFromReds) {
		this.answersFromReds = answersFromReds;
	}
	
	
	
}
