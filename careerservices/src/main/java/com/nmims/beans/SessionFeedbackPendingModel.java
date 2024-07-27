package com.nmims.beans;

import java.io.Serializable;
import java.util.List;

public class SessionFeedbackPendingModel implements Serializable{

	private List<SessionFeedbackQuestionsModelBean> pendingFeedback;

	public List<SessionFeedbackQuestionsModelBean> getPendingFeedback() {
		return pendingFeedback;
	}

	public void setPendingFeedback(List<SessionFeedbackQuestionsModelBean> pendingFeedback) {
		this.pendingFeedback = pendingFeedback;
	}
}
