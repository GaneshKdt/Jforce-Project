package com.nmims.beans;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class SessionPlanAPIResponseBean  implements Serializable  {
	
	private SessionPlanBean sessionPlan;
	private List<SessionPlanModuleBean> modules;
	private List<Post> posts;
	private String errorMessage="";
	private String successMessage="";
	public SessionPlanBean getSessionPlan() {
		return sessionPlan;
	}
	public void setSessionPlan(SessionPlanBean sessionPlan) {
		this.sessionPlan = sessionPlan;
	}
	public List<SessionPlanModuleBean> getModules() {
		
		if(modules == null) {
			return new ArrayList<>();
		}
		
		return modules;
	}
	public void setModules(List<SessionPlanModuleBean> modules) {
		this.modules = modules;
	}
	public List<Post> getPosts() {
		return posts;
	}
	public void setPosts(List<Post> posts) {
		this.posts = posts;
	}
	public String getErrorMessage() {
		if( errorMessage==null ) {
			return "";
		}
		return errorMessage;
	}
	public void setErrorMessage(String errorMessage) {
		this.errorMessage = errorMessage;
	}
	public String getSuccessMessage() {
		if( successMessage==null ) {
			return "";
		}
		return successMessage;
	}
	public void setSuccessMessage(String successMessage) {
		this.successMessage = successMessage;
	}
	
	
	
	
}
