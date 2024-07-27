package com.nmims.beans;

import java.io.Serializable;
import java.util.ArrayList;

//spring security related changes rename UserAuthorizationBean to UserAuthorizationExamBean
public class UserAuthorizationExamBean extends BaseExamBean implements Serializable {
	public UserAuthorizationExamBean() {
		super();
	}
	private String userId;
	private String roles;
	private String authorizedLC;
	private String authorizedCenters;
	
	private String allCenter;
	private String allLC;
	private ArrayList<String> allRoles;
	private String commaSeparatedAuthorizedCenterCodes;
	private ArrayList<String> authorizedCenterCodes;
	
	
	
	public String getCommaSeparatedAuthorizedCenterCodes() {
		return commaSeparatedAuthorizedCenterCodes;
	}
	public void setCommaSeparatedAuthorizedCenterCodes(
			String commaSeparatedAuthorizedCenterCodes) {
		this.commaSeparatedAuthorizedCenterCodes = commaSeparatedAuthorizedCenterCodes;
	}
	public ArrayList<String> getAuthorizedCenterCodes() {
		return authorizedCenterCodes;
	}
	public void setAuthorizedCenterCodes(ArrayList<String> authorizedCenterCodes) {
		this.authorizedCenterCodes = authorizedCenterCodes;
	}
	public ArrayList<String> getAllRoles() {
		return allRoles;
	}
	public void setAllRoles(ArrayList<String> allRoles) {
		this.allRoles = allRoles;
	}
	public String getAllCenter() {
		return allCenter;
	}
	public void setAllCenter(String allCenter) {
		this.allCenter = allCenter;
	}
	public String getAllLC() {
		return allLC;
	}
	public void setAllLC(String allLC) {
		this.allLC = allLC;
	}
	
	public String getUserId() {
		return userId;
	}
	public void setUserId(String userId) {
		this.userId = userId;
	}
	public String getRoles() {
		return roles;
	}
	public void setRoles(String roles) {
		this.roles = roles;
	}
	public String getAuthorizedLC() {
		return authorizedLC;
	}
	public void setAuthorizedLC(String authorizedLC) {
		this.authorizedLC = authorizedLC;
	}
	public String getAuthorizedCenters() {
		return authorizedCenters;
	}
	public void setAuthorizedCenters(String authorizedCenters) {
		this.authorizedCenters = authorizedCenters;
	}
	@Override
	public String toString() {
		return "UserAuthorizationBean [userId=" + userId + ", roles=" + roles
				+ ", authorizedLC=" + authorizedLC + ", authorizedCenters="
				+ authorizedCenters + ", allCenter=" + allCenter + ", allLC="
				+ allLC + ", allRoles=" + allRoles + "]";
	}
	
	
	
}
