package com.nmims.beans;

import java.io.Serializable;
import java.util.List;

//spring security related changes rename GetAnswersFromRedisByStudentsTestDetailsResponseBean to GetAnswersFromRedisByStudentsTestDetailsResponseExamBean
public class GetAnswersFromRedisByStudentsTestDetailsResponseExamBean  implements Serializable   {
	
	private String status;
	private String message;
	private List<StudentQuestionResponseExamBean> answersFromReds;
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
	public List<StudentQuestionResponseExamBean> getAnswersFromReds() {
		return answersFromReds;
	}
	public void setAnswersFromReds(List<StudentQuestionResponseExamBean> answersFromReds) {
		this.answersFromReds = answersFromReds;
	}
	
	
	
}
