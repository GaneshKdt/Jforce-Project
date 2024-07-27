package com.nmims.beans;

public class MettlResponseBeanWebHookMbaWx {
	
	private static final String TEST_STARTED_ON_PORTAL_MBAWX = "Portal Started";
	private static final String TEST_STARTED_ON_METTL_MBAWX = "Mettl Started";
	private static final String TEST_ENDED_ON_METTL_MBAWX = "Mettl Completed";
	
	private String email;
	private String name;
	private String EVENT_TYPE;
	private String assessment_id;
	private String context_data;
	private String timestamp_GMT;
	private String source_app;
	private String notification_url;
	private String invitation_key;
	private String candidate_instance_id;
	private String finish_mode;
	private String status;
	private String errorMessage;

	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}
	public String getErrorMessage() {
		return errorMessage;
	}
	public void setErrorMessage(String errorMessage) {
		this.errorMessage = errorMessage;
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
	public String getFinish_mode() {
		return finish_mode;
	}
	public void setFinish_mode(String finish_mode) {
		this.finish_mode = finish_mode;
	}
	public static String getTestStartedOnPortalMbawx() {
		return TEST_STARTED_ON_PORTAL_MBAWX;
	}
	public static String getTestStartedOnMettlMbawx() {
		return TEST_STARTED_ON_METTL_MBAWX;
	}
	public static String getTestEndedOnMettlMbawx() {
		return TEST_ENDED_ON_METTL_MBAWX;
	}
	@Override
	public String toString() {
		return "MettlResponseBeanWebHookMbaWx [email=" + email + ", name=" + name + ", EVENT_TYPE=" + EVENT_TYPE
				+ ", assessment_id=" + assessment_id + ", context_data=" + context_data + ", timestamp_GMT="
				+ timestamp_GMT + ", source_app=" + source_app + ", notification_url=" + notification_url
				+ ", invitation_key=" + invitation_key + ", candidate_instance_id=" + candidate_instance_id
				+ ", finish_mode=" + finish_mode + "]";
	}
	
	
}
