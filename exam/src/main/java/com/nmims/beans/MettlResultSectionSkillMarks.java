package com.nmims.beans;

import java.io.Serializable;
import java.util.List;

public class MettlResultSectionSkillMarks  implements Serializable  {
	
	private String skillName;
	private double totalMarks;
	private double maxMarks;
	private int timeTaken;
	private int totalQuestion;
	private int totalCorrectAnswers;
	private int totalUnAnswered;
	
	private List<MettlResultDifficultyMarks> difficultyMarks;
//	questions;

	public String getSkillName() {
		return skillName;
	}
	public void setSkillName(String skillName) {
		this.skillName = skillName;
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
	public List<MettlResultDifficultyMarks> getDifficultyMarks() {
		return difficultyMarks;
	}
	public void setDifficultyMarks(List<MettlResultDifficultyMarks> difficultyMarks) {
		this.difficultyMarks = difficultyMarks;
	}
	@Override
	public String toString() {
		return "MettlResultSectionSkillMarks [skillName=" + skillName + ", totalMarks=" + totalMarks + ", maxMarks="
				+ maxMarks + ", timeTaken=" + timeTaken + ", totalQuestion=" + totalQuestion + ", totalCorrectAnswers="
				+ totalCorrectAnswers + ", totalUnAnswered=" + totalUnAnswered + ", difficultyMarks=" + difficultyMarks
				+ "]";
	}
}
