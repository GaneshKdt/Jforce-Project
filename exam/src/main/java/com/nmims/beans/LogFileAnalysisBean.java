package com.nmims.beans;

import java.io.Serializable;
import java.util.ArrayList;

public class LogFileAnalysisBean  implements Serializable  {

	private String date;
	private String sapid;
	private String testId;
	private ArrayList<String> logDetailsList;
	boolean present;
	private String questionId;
	
	public String getDate() {
		return date;
	}
	public void setDate(String date) {
		this.date = date;
	}
	public String getSapid() {
		return sapid;
	}
	public void setSapid(String sapid) {
		this.sapid = sapid;
	}
	public String getTestId() {
		return testId;
	}
	public void setTestId(String testId) {
		this.testId = testId;
	}
	public ArrayList<String> getLogDetailsList() {
		return logDetailsList;
	}
	public void setLogDetailsList(ArrayList<String> logDetailsList) {
		this.logDetailsList = logDetailsList;
	}
	public boolean isPresent() {
		return present;
	}
	public void setPresent(boolean present) {
		this.present = present;
	}
	public String getQuestionId() {
		return questionId;
	}
	public void setQuestionId(String questionId) {
		this.questionId = questionId;
	}
}
