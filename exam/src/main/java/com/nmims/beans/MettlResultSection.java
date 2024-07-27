package com.nmims.beans;

import java.io.Serializable;
import java.util.List;

public class MettlResultSection  implements Serializable  {

	private String sectionName;
	private double totalMarks;
	private double maxMarks;
	private int timeTaken;
	private int totalQuestion;
	private int totalCorrectAnswers;
	private int totalUnAnswered;

	private List<MettlResultSectionSkillMarks> skillMarks;
	private List<MettlResultDifficultyMarks> difficultyMarks;
	private List<MettlResultQuestionWiseResponse> questionWiseResponse;
	
	public String getSectionName() {
		return sectionName;
	}
	public void setSectionName(String sectionName) {
		this.sectionName = sectionName;
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
	public List<MettlResultSectionSkillMarks> getSkillMarks() {
		return skillMarks;
	}
	public void setSkillMarks(List<MettlResultSectionSkillMarks> skillMarks) {
		this.skillMarks = skillMarks;
	}
	public List<MettlResultDifficultyMarks> getDifficultyMarks() {
		return difficultyMarks;
	}
	public void setDifficultyMarks(List<MettlResultDifficultyMarks> difficultyMarks) {
		this.difficultyMarks = difficultyMarks;
	}
	public List<MettlResultQuestionWiseResponse> getQuestionWiseResponse() {
		return questionWiseResponse;
	}
	public void setQuestionWiseResponse(List<MettlResultQuestionWiseResponse> questionWiseResponse) {
		this.questionWiseResponse = questionWiseResponse;
	}
	@Override
	public String toString() {
		return "MettlResultSection [sectionName=" + sectionName + ", totalMarks=" + totalMarks + ", maxMarks="
				+ maxMarks + ", timeTaken=" + timeTaken + ", totalQuestion=" + totalQuestion + ", totalCorrectAnswers="
				+ totalCorrectAnswers + ", totalUnAnswered=" + totalUnAnswered + ", skillMarks=" + skillMarks
				+ ", difficultyMarks=" + difficultyMarks + ", questionWiseResponse=" + questionWiseResponse + "]";
	}
}

