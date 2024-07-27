package com.nmims.beans;

import java.io.Serializable;

public class CounsellingFeedbackBean  implements Serializable{

	private String preparedness;
	private String communication;
	private String listeningSkills;
	private String bodyLanguage;
	private String clarityOfThought;
	private String connect;
	private String examples;
	private String strength;
	private String improvements;
	private String cvtweaking;
	private String careerchoice;
	private String errorMessage;
	private boolean isError;
	private String userId;
	
	public String getPreparedness() {
		return preparedness;
	}
	public void setPreparedness(String preparedness) {
		this.preparedness = preparedness;
	}
	public String getCommunication() {
		return communication;
	}
	public void setCommunication(String communication) {
		this.communication = communication;
	}
	public String getListeningSkills() {
		return listeningSkills;
	}
	public void setListeningSkills(String listeningSkills) {
		this.listeningSkills = listeningSkills;
	}
	public String getBodyLanguage() {
		return bodyLanguage;
	}
	public void setBodyLanguage(String bodyLanguage) {
		this.bodyLanguage = bodyLanguage;
	}
	public String getClarityOfThought() {
		return clarityOfThought;
	}
	public void setClarityOfThought(String clarityOfThought) {
		this.clarityOfThought = clarityOfThought;
	}
	public String getConnect() {
		return connect;
	}
	public void setConnect(String connect) {
		this.connect = connect;
	}
	public String getExamples() {
		return examples;
	}
	public void setExamples(String examples) {
		this.examples = examples;
	}
	public String getStrength() {
		return strength;
	}
	public void setStrength(String strength) {
		this.strength = strength;
	}
	public String getImprovements() {
		return improvements;
	}
	public void setImprovements(String improvements) {
		this.improvements = improvements;
	}
	public String getCvtweaking() {
		return cvtweaking;
	}
	public void setCvtweaking(String cvtweaking) {
		this.cvtweaking = cvtweaking;
	}
	public String getCareerchoice() {
		return careerchoice;
	}
	public void setCareerchoice(String careerchoice) {
		this.careerchoice = careerchoice;
	}
	public String getErrorMessage() {
		return errorMessage;
	}
	public void setErrorMessage(String errorMessage) {
		this.errorMessage = errorMessage;
	}
	public boolean isError() {
		return isError;
	}
	public void setError(boolean isError) {
		this.isError = isError;
	}
	public String getUserId() {
		return userId;
	}
	public void setUserId(String userId) {
		this.userId = userId;
	}
	@Override
	public String toString() {
		return "InterviewFeedsBean [preparedness=" + preparedness + ", communication=" + communication
				+ ", listeningSkills=" + listeningSkills + ", bodyLanguage=" + bodyLanguage + ", clarityOfThought="
				+ clarityOfThought + ", connect=" + connect + ", examples=" + examples + ", strength=" + strength
				+ ", improvements=" + improvements + ", cvtweaking=" + cvtweaking + ", careerchoice=" + careerchoice
				+ "]";
	}
	
}
