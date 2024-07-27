package com.nmims.beans;

import java.io.Serializable;
import java.util.ArrayList;

public class PortalFeedbackBeanResponse   implements Serializable  {

	ArrayList<SessionAttendanceFeedbackStudentPortal> pendingFeedback;
	ArrayList<AcadCycleFeedback> pendingAcadFeedback ;
	AcadCycleFeedback acadCycleFeedbackBean;
	   


	public AcadCycleFeedback getAcadCycleFeedbackBean() {
		return acadCycleFeedbackBean;
	}

	public void setAcadCycleFeedbackBean(AcadCycleFeedback acadCycleFeedbackBean) {
		this.acadCycleFeedbackBean = acadCycleFeedbackBean;
	}

	private String feedbackType;

	

	public String getFeedbackType() {
		return feedbackType;
	}

	public void setFeedbackType(String feedbackType) {
		this.feedbackType = feedbackType;
	}

	public ArrayList<AcadCycleFeedback> getPendingAcadFeedback() {
		return pendingAcadFeedback;
	}

	public void setPendingAcadFeedback(ArrayList<AcadCycleFeedback> pendingAcadFeedback) {
		this.pendingAcadFeedback = pendingAcadFeedback;
	}

	public ArrayList<SessionAttendanceFeedbackStudentPortal> getPendingFeedback() {
		return pendingFeedback;
	}

	public void setPendingFeedback(ArrayList<SessionAttendanceFeedbackStudentPortal> pendingFeedback) {
		this.pendingFeedback = pendingFeedback;
	}
	
}
