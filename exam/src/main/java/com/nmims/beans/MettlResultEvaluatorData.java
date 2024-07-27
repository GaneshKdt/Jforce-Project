package com.nmims.beans;

import java.io.Serializable;

public class MettlResultEvaluatorData  implements Serializable {
	private String name;
	private String email;
	private double marksAwarded;
	private String evaluationComments;
	private String evaluationTime;
	private String evaluatorRole;
	
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getEmail() {
		return email;
	}
	public void setEmail(String email) {
		this.email = email;
	}
	public double getMarksAwarded() {
		return marksAwarded;
	}
	public void setMarksAwarded(double marksAwarded) {
		this.marksAwarded = marksAwarded;
	}
	public String getEvaluationComments() {
		return evaluationComments;
	}
	public void setEvaluationComments(String evaluationComments) {
		this.evaluationComments = evaluationComments;
	}
	public String getEvaluationTime() {
		return evaluationTime;
	}
	public void setEvaluationTime(String evaluationTime) {
		this.evaluationTime = evaluationTime;
	}
	public String getEvaluatorRole() {
		return evaluatorRole;
	}
	public void setEvaluatorRole(String evaluatorRole) {
		this.evaluatorRole = evaluatorRole;
	}
	
	@Override
	public String toString() {
		return "MettlResultEvaluatorData [name=" + name + ", email=" + email + ", marksAwarded=" + marksAwarded
				+ ", evaluationComments=" + evaluationComments + ", evaluationTime=" + evaluationTime
				+ ", evaluatorRole=" + evaluatorRole + "]";
	}
}
