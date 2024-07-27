package com.nmims.dto;

import java.util.ArrayList;

import com.nmims.beans.AcadCycleFeedback;
import com.nmims.beans.SessionAttendanceFeedbackStudentPortal;

public class PortalFeedbackBeanResponseDto {
	
	ArrayList<SessionAttendanceFeedbackDto> pendingFeedback;
	ArrayList<AcadCycleFeedback> pendingAcadFeedback ;
	AcadCycleFeedback acadCycleFeedbackBean;
	private String feedbackType;
	
	
	
	
	public ArrayList<SessionAttendanceFeedbackDto> getPendingFeedback() {
		return pendingFeedback;
	}
	public void setPendingFeedback(ArrayList<SessionAttendanceFeedbackDto> pendingFeedback) {
		this.pendingFeedback = pendingFeedback;
	}
	public ArrayList<AcadCycleFeedback> getPendingAcadFeedback() {
		return pendingAcadFeedback;
	}
	public void setPendingAcadFeedback(ArrayList<AcadCycleFeedback> pendingAcadFeedback) {
		this.pendingAcadFeedback = pendingAcadFeedback;
	}
	public AcadCycleFeedback getAcadCycleFeedbackBean() {
		return acadCycleFeedbackBean;
	}
	public void setAcadCycleFeedbackBean(AcadCycleFeedback acadCycleFeedbackBean) {
		this.acadCycleFeedbackBean = acadCycleFeedbackBean;
	}
	public String getFeedbackType() {
		return feedbackType;
	}
	public void setFeedbackType(String feedbackType) {
		this.feedbackType = feedbackType;
	}
	
	

}
