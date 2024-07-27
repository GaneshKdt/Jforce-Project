package com.nmims.beans;

import java.io.Serializable;


public class MBAWXConfigurationBean implements Serializable{
	private String id;
	private String acadsYear;
	private String acadsMonth;
	private String examYear;
	private String examMonth;
	private String consumerProgramStructureId;
	private String type;
	private String startTime;
	private String endTime;
	private String created_at;
	private String updated_at;
	
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
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
	public String getConsumerProgramStructureId() {
		return consumerProgramStructureId;
	}
	public void setConsumerProgramStructureId(String consumerProgramStructureId) {
		this.consumerProgramStructureId = consumerProgramStructureId;
	}
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	public String getStartTime() {
		return startTime;
	}
	public void setStartTime(String startTime) {
		this.startTime = startTime;
	}
	public String getEndTime() {
		return endTime;
	}
	public void setEndTime(String endTime) {
		this.endTime = endTime;
	}
	public String getCreated_at() {
		return created_at;
	}
	public void setCreated_at(String created_at) {
		this.created_at = created_at;
	}
	public String getUpdated_at() {
		return updated_at;
	}
	public void setUpdated_at(String updated_at) {
		this.updated_at = updated_at;
	}
}
