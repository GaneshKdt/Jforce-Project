package com.nmims.beans;

import java.io.Serializable;

public class metadata implements Serializable {
	
	//private String REMOVE_MEMBER_MESSAGE = "adminName removed userName";
	//private String ADD_MEMBER_MESSAGE = "userName joined group";
	//private String JOIN_MEMBER_MESSAGE = "userName joined group";
	//private String GROUP_NAME_CHANGE_MESSAGE = "userName changed group name";
	//private String GROUP_ICON_CHANGE_MESSAGE = "userName changed group icon";
	//private String GROUP_LEFT_MESSAGE = "userName left group";
	//private String DELETED_GROUP_MESSAGE = "adminName deleted group";
	//private String GROUP_USER_ROLE_UPDATED_MESSAGE = "";
	//private String CREATE_GROUP_MESSAGE = "adminName created group";
	//private String ALERT = "false";
	//private String HIDE = "false";
	
	private String REMOVE_MEMBER_MESSAGE = "";
	private String ADD_MEMBER_MESSAGE = "";
	private String JOIN_MEMBER_MESSAGE = "";
	private String GROUP_NAME_CHANGE_MESSAGE = "userName changed group name";
	private String GROUP_ICON_CHANGE_MESSAGE = "userName changed group icon";
	private String GROUP_LEFT_MESSAGE = "";
	private String DELETED_GROUP_MESSAGE = "";
	private String GROUP_USER_ROLE_UPDATED_MESSAGE = "";
	private String CREATE_GROUP_MESSAGE = "adminName created group";
	private String ALERT = "false";
	private String HIDE = "false";	
	public String getCREATE_GROUP_MESSAGE() {
		return CREATE_GROUP_MESSAGE;
	}
	public void setCREATE_GROUP_MESSAGE(String cREATE_GROUP_MESSAGE) {
		CREATE_GROUP_MESSAGE = cREATE_GROUP_MESSAGE;
	}
	public String getREMOVE_MEMBER_MESSAGE() {
		return REMOVE_MEMBER_MESSAGE;
	}
	public void setREMOVE_MEMBER_MESSAGE(String rEMOVE_MEMBER_MESSAGE) {
		REMOVE_MEMBER_MESSAGE = rEMOVE_MEMBER_MESSAGE;
	}
	public String getADD_MEMBER_MESSAGE() {
		return ADD_MEMBER_MESSAGE;
	}
	public void setADD_MEMBER_MESSAGE(String aDD_MEMBER_MESSAGE) {
		ADD_MEMBER_MESSAGE = aDD_MEMBER_MESSAGE;
	}
	public String getJOIN_MEMBER_MESSAGE() {
		return JOIN_MEMBER_MESSAGE;
	}
	public void setJOIN_MEMBER_MESSAGE(String jOIN_MEMBER_MESSAGE) {
		JOIN_MEMBER_MESSAGE = jOIN_MEMBER_MESSAGE;
	}
	public String getGROUP_NAME_CHANGE_MESSAGE() {
		return GROUP_NAME_CHANGE_MESSAGE;
	}
	public void setGROUP_NAME_CHANGE_MESSAGE(String gROUP_NAME_CHANGE_MESSAGE) {
		GROUP_NAME_CHANGE_MESSAGE = gROUP_NAME_CHANGE_MESSAGE;
	}
	public String getGROUP_ICON_CHANGE_MESSAGE() {
		return GROUP_ICON_CHANGE_MESSAGE;
	}
	public void setGROUP_ICON_CHANGE_MESSAGE(String gROUP_ICON_CHANGE_MESSAGE) {
		GROUP_ICON_CHANGE_MESSAGE = gROUP_ICON_CHANGE_MESSAGE;
	}
	public String getGROUP_LEFT_MESSAGE() {
		return GROUP_LEFT_MESSAGE;
	}
	public void setGROUP_LEFT_MESSAGE(String gROUP_LEFT_MESSAGE) {
		GROUP_LEFT_MESSAGE = gROUP_LEFT_MESSAGE;
	}
	public String getDELETED_GROUP_MESSAGE() {
		return DELETED_GROUP_MESSAGE;
	}
	public void setDELETED_GROUP_MESSAGE(String dELETED_GROUP_MESSAGE) {
		DELETED_GROUP_MESSAGE = dELETED_GROUP_MESSAGE;
	}
	public String getGROUP_USER_ROLE_UPDATED_MESSAGE() {
		return GROUP_USER_ROLE_UPDATED_MESSAGE;
	}
	public void setGROUP_USER_ROLE_UPDATED_MESSAGE(String gROUP_USER_ROLE_UPDATED_MESSAGE) {
		GROUP_USER_ROLE_UPDATED_MESSAGE = gROUP_USER_ROLE_UPDATED_MESSAGE;
	}
	public String getALERT() {
		return ALERT;
	}
	public void setALERT(String aLERT) {
		ALERT = aLERT;
	}
	public String getHIDE() {
		return HIDE;
	}
	public void setHIDE(String hIDE) {
		HIDE = hIDE;
	}


}
