package com.nmims.beans;

import java.io.Serializable;
import java.util.List;

public class LiveSessionAccessBean  implements Serializable {

	private List<Integer> liveSessionPssIdAccess;
	private List<Integer> currentSemPSSId;
	private Boolean isLiveSessionAccessLogicApply;
	
	private String status;
	private String errorMessage;
	private List<Integer> subjectCodeId;

	public List<Integer> getLiveSessionPssIdAccess() {
		return liveSessionPssIdAccess;
	}
	public void setLiveSessionPssIdAccess(List<Integer> liveSessionPssIdAccess) {
		this.liveSessionPssIdAccess = liveSessionPssIdAccess;
	}
	
	public List<Integer> getSubjectCodeId() {
		return subjectCodeId;
	}
	public void setSubjectCodeId(List<Integer> subjectCodeId) {
		this.subjectCodeId = subjectCodeId;
	}
	public List<Integer> getCurrentSemPSSId() {
		return currentSemPSSId;
	}
	public void setCurrentSemPSSId(List<Integer> currentSemPSSId) {
		this.currentSemPSSId = currentSemPSSId;
	}
	public Boolean getIsLiveSessionAccessLogicApply() {
		return isLiveSessionAccessLogicApply;
	}
	public void setIsLiveSessionAccessLogicApply(Boolean isLiveSessionAccessLogicApply) {
		this.isLiveSessionAccessLogicApply = isLiveSessionAccessLogicApply;
	}
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
}
