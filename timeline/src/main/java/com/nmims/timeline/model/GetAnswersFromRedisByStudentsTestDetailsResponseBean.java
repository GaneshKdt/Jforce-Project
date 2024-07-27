package com.nmims.timeline.model;

import java.util.List;

public class GetAnswersFromRedisByStudentsTestDetailsResponseBean {
	
	private String status;
	private String message;
	private List<StudentQuestionResponseBean> answersFromReds;
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
	public List<StudentQuestionResponseBean> getAnswersFromReds() {
		return answersFromReds;
	}
	public void setAnswersFromReds(List<StudentQuestionResponseBean> answersFromReds) {
		this.answersFromReds = answersFromReds;
	}
	
	
	
}
