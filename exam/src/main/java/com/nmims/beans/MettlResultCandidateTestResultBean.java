package com.nmims.beans;

import java.io.Serializable;
import java.util.List;

public class MettlResultCandidateTestResultBean  implements Serializable  {
	private double totalMarks;
	private double maxMarks;
	private double percentile;
	private int attemptTime;
	private String candidateCredibilityIndex;
	private int totalQuestion;
	private int totalCorrectAnswers;
	private int totalUnAnswered;
	private List<MettlResultSection> sectionMarks;
	
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
	public double getPercentile() {
		return percentile;
	}
	public void setPercentile(double percentile) {
		this.percentile = percentile;
	}
	public int getAttemptTime() {
		return attemptTime;
	}
	public void setAttemptTime(int attemptTime) {
		this.attemptTime = attemptTime;
	}
	public String getCandidateCredibilityIndex() {
		return candidateCredibilityIndex;
	}
	public void setCandidateCredibilityIndex(String candidateCredibilityIndex) {
		this.candidateCredibilityIndex = candidateCredibilityIndex;
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
	public List<MettlResultSection> getSectionMarks() {
		return sectionMarks;
	}
	public void setSectionMarks(List<MettlResultSection> sectionMarks) {
		this.sectionMarks = sectionMarks;
	}
	@Override
	public String toString() {
		return "MettlResultCandidateTestResultBean [totalMarks=" + totalMarks + ", maxMarks=" + maxMarks
				+ ", percentile=" + percentile + ", attemptTime=" + attemptTime + ", candidateCredibilityIndex="
				+ candidateCredibilityIndex + ", totalQuestion=" + totalQuestion + ", totalCorrectAnswers="
				+ totalCorrectAnswers + ", totalUnAnswered=" + totalUnAnswered + ", sectionMarks=" + sectionMarks + "]";
	}
}
