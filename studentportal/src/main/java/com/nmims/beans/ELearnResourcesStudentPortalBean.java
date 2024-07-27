package com.nmims.beans;

import java.io.Serializable;

/**
 * old name - ELearnResourcesBean
 * @author
 *
 */
public class ELearnResourcesStudentPortalBean implements Serializable {

	public ELearnResourcesStudentPortalBean() {
		super();
	}
	int userId_count;
	String roles;
	String provider_name;
	String userId;
	
	public String getUserId() {
		return userId;
	}
	public void setUserId(String userId) {
		this.userId = userId;
	}
	public int getUserId_count() {
		return userId_count;
	}
	public void setUserId_count(int userId_count) {
		this.userId_count = userId_count;
	}
	public String getRoles() {
		return roles;
	}
	public void setRoles(String roles) {
		this.roles = roles;
	}
	public String getProvider_name() {
		return provider_name;
	}
	public void setProvider_name(String provider_name) {
		this.provider_name = provider_name;
	}
	
}
