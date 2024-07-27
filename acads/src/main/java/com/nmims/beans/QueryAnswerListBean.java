package com.nmims.beans;

import java.io.Serializable;
import java.util.List;

public class QueryAnswerListBean implements Serializable{
	private List<SessionQueryAnswer> allQueries;
	private List<SessionQueryAnswer> unansweredQueries;
	private List<SessionQueryAnswer> answeredQueries;
	private List<SessionQueryAnswer> unansweredWXQueries;
	private List<SessionQueryAnswer> answeredWXQueries;
	public List<SessionQueryAnswer> getAllQueries() {
		return allQueries;
	}
	public void setAllQueries(List<SessionQueryAnswer> allQueries) {
		this.allQueries = allQueries;
	}
	public List<SessionQueryAnswer> getUnansweredQueries() {
		return unansweredQueries;
	}
	public void setUnansweredQueries(List<SessionQueryAnswer> unansweredQueries) {
		this.unansweredQueries = unansweredQueries;
	}
	public List<SessionQueryAnswer> getAnsweredQueries() {
		return answeredQueries;
	}
	public void setAnsweredQueries(List<SessionQueryAnswer> answeredQueries) {
		this.answeredQueries = answeredQueries;
	}
	public List<SessionQueryAnswer> getUnansweredWXQueries() {
		return unansweredWXQueries;
	}
	public void setUnansweredWXQueries(List<SessionQueryAnswer> unansweredWXQueries) {
		this.unansweredWXQueries = unansweredWXQueries;
	}
	public List<SessionQueryAnswer> getAnsweredWXQueries() {
		return answeredWXQueries;
	}
	public void setAnsweredWXQueries(List<SessionQueryAnswer> answeredWXQueries) {
		this.answeredWXQueries = answeredWXQueries;
	}
	
	@Override
	public String toString() {
		return "QueryAnswerListBean [allQueries=" + allQueries + ", unansweredQueries=" + unansweredQueries
				+ ", answeredQueries=" + answeredQueries + ", unansweredWXQueries=" + unansweredWXQueries
				+ ", answeredWXQueries=" + answeredWXQueries + "]";
	}

}
