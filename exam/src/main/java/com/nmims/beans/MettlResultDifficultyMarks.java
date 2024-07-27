package com.nmims.beans;

import java.io.Serializable;

public class MettlResultDifficultyMarks  implements Serializable  {
	
	private String level;
	private double totalMarks;
	private double maxMarks;
	private int timeTaken;
	private int totalQuestion;
	private int totalCorrectAnswers;
	private int totalUnAnswered;
	// questions
	
	public String getLevel() {
		return level;
	}
	public void setLevel(String level) {
		this.level = level;
	}
	public double getTotalMarks() {
		return totalMarks;
	}
	public void setTotalMarks(double totalMarks) {
		this.totalMarks = totalMarks;
	}
	public double getMaxMarks() {
		return maxMarks;
	}
	public void setMaxMarks(double maxMarks) {
		this.maxMarks = maxMarks;
	}
	public int getTimeTaken() {
		return timeTaken;
	}
	public void setTimeTaken(int timeTaken) {
		this.timeTaken = timeTaken;
	}
	public int getTotalQuestion() {
		return totalQuestion;
	}
	public void setTotalQuestion(int totalQuestion) {
		this.totalQuestion = totalQuestion;
	}
	public int getTotalCorrectAnswers() {
		return totalCorrectAnswers;
	}
	public void setTotalCorrectAnswers(int totalCorrectAnswers) {
		this.totalCorrectAnswers = totalCorrectAnswers;
	}
	public int getTotalUnAnswered() {
		return totalUnAnswered;
	}
	public void setTotalUnAnswered(int totalUnAnswered) {
		this.totalUnAnswered = totalUnAnswered;
	}
	
	@Override
	public String toString() {
		return "MettlResultDifficultyMarks [level=" + level + ", totalMarks=" + totalMarks + ", maxMarks=" + maxMarks
				+ ", timeTaken=" + timeTaken + ", totalQuestion=" + totalQuestion + ", totalCorrectAnswers="
				+ totalCorrectAnswers + ", totalUnAnswered=" + totalUnAnswered + "]";
	}
}
