package com.nmims.beans;

import java.io.Serializable;

public class MettlHookResponseBean  implements Serializable  {

	public static final String TEST_STARTED_ON_PORTAL = "Portal Started";
	public static final String TEST_STARTED_ON_METTL = "Mettl Started";
	public static final String TEST_ENDED_ON_METTL = "Mettl Completed";
	
	public String email;
	public String name;
	public String EVENT_TYPE;
	public String assessment_id;
	public String context_data;
	public String timestamp_GMT;
	public String source_app;
	public String notification_url;
	public String invitation_key;
	public String candidate_instance_id;
	public String finish_node;
	
	
	public String getFinish_node() {
		return finish_node;
	}
	public void setFinish_node(String finish_node) {
		this.finish_node = finish_node;
	}
	public String getEVENT_TYPE() {
		return EVENT_TYPE;
	}
	public void setEVENT_TYPE(String eVENT_TYPE) {
		EVENT_TYPE = eVENT_TYPE;
	}
	public String getAssessment_id() {
		return assessment_id;
	}
	public void setAssessment_id(String assessment_id) {
		this.assessment_id = assessment_id;
	}
	public String getContext_data() {
		return context_data;
	}
	public void setContext_data(String context_data) {
		this.context_data = context_data;
	}
	public String getTimestamp_GMT() {
		return timestamp_GMT;
	}
	public void setTimestamp_GMT(String timestamp_GMT) {
		this.timestamp_GMT = timestamp_GMT;
	}
	public String getSource_app() {
		return source_app;
	}
	public void setSource_app(String source_app) {
		this.source_app = source_app;
	}
	public String getNotification_url() {
		return notification_url;
	}
	public void setNotification_url(String notification_url) {
		this.notification_url = notification_url;
	}
	public String getInvitation_key() {
		return invitation_key;
	}
	public void setInvitation_key(String invitation_key) {
		this.invitation_key = invitation_key;
	}
	public String getCandidate_instance_id() {
		return candidate_instance_id;
	}
	public void setCandidate_instance_id(String candidate_instance_id) {
		this.candidate_instance_id = candidate_instance_id;
	}
	public String getEmail() {
		return email;
	}
	public void setEmail(String email) {
		this.email = email;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	
	@Override
	public String toString() {
		return this.EVENT_TYPE + " | " + this.notification_url + "| " + this.email + " | " + this.name + " | " + this.assessment_id + " | " + this.candidate_instance_id + " | " + this.context_data;
	}
	
}
