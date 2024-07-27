package com.nmims.beans;
import java.io.Serializable;
import java.util.List;
public class UFMResponseBean implements Serializable {
	private String status;
	private String sapid;
	private boolean markedForCurrentCycle;
	private List<UFMNoticeBean> notices;
	private String errorMessage;
	
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}
	public String getSapid() {
		return sapid;
	}
	public void setSapid(String sapid) {
		this.sapid = sapid;
	}
	public boolean isMarkedForCurrentCycle() {
		return markedForCurrentCycle;
	}
	public void setMarkedForCurrentCycle(boolean markedForCurrentCycle) {
		this.markedForCurrentCycle = markedForCurrentCycle;
	}
	public List<UFMNoticeBean> getNotices() {
		return notices;
	}
	public void setNotices(List<UFMNoticeBean> notices) {
		this.notices = notices;
	}
	
	@Override
	public String toString() {
		return "UFMResponseBean [status=" + status + ", sapid=" + sapid + ", markedForCurrentCycle="
				+ markedForCurrentCycle + ", notices=" + notices + "]";
	}
	public String getErrorMessage() {
		return errorMessage;
	}
	public void setErrorMessage(String errorMessage) {
		this.errorMessage = errorMessage;
	}
}
