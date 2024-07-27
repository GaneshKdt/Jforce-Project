package com.nmims.beans;

import java.io.Serializable;

public class AssignmentLiveSetting  implements Serializable {
	
	private String acadsYear;
	private String acadsMonth;
	private String examYear;
	private String examMonth;
	private String liveType;
	private String consumerProgramStructureId;
	public String getAcadsYear() {
		return acadsYear;
	}
	public void setAcadsYear(String acadsYear) {
		this.acadsYear = acadsYear;
	}
	public String getAcadsMonth() {
		return acadsMonth;
	}
	public void setAcadsMonth(String acadsMonth) {
		this.acadsMonth = acadsMonth;
	}
	public String getExamYear() {
		return examYear;
	}
	public void setExamYear(String examYear) {
		this.examYear = examYear;
	}
	public String getExamMonth() {
		return examMonth;
	}
	public void setExamMonth(String examMonth) {
		this.examMonth = examMonth;
	}
	public String getLiveType() {
		return liveType;
	}
	public void setLiveType(String liveType) {
		this.liveType = liveType;
	}
	public String getConsumerProgramStructureId() {
		return consumerProgramStructureId;
	}
	public void setConsumerProgramStructureId(String consumerProgramStructureId) {
		this.consumerProgramStructureId = consumerProgramStructureId;
	}
	
	
	
}
