package com.nmims.beans;

import java.io.Serializable;
import java.util.List;

public class PostMyQueryMBAWXBean  implements Serializable  {
	
	public String status;
	public String message;
	public List<SessionQueryAnswer> sessionQueryAnswerList;
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
	public List<SessionQueryAnswer> getSessionQueryAnswerList() {
		return sessionQueryAnswerList;
	}
	public void setSessionQueryAnswerList(List<SessionQueryAnswer> sessionQueryAnswerList) {
		this.sessionQueryAnswerList = sessionQueryAnswerList;
	}
	
	
	
}
