package com.nmims.beans;

import java.io.Serializable;

public class ExamLiveSettingMBAWX  implements Serializable  {
	private String acadsYear="0";   
	private String acadsMonth="" ;     
	private String examYear ;
	private String examMonth ;
	private String consumerProgramStructureId ;  
	private String created_at ;
	private String updated_at;  
	private String type;
	private String live;
	 
	public String getLive() {
		return live;
	}
	public void setLive(String live) {
		this.live = live;
	}
	public String getAcadsYear() {
		return acadsYear;
	}
	public String getAcadsMonth() {
		return acadsMonth;
	}
	public String getExamYear() {
		return examYear;
	}
	public String getExamMonth() {
		return examMonth;
	}
	public String getConsumerProgramStructureId() {
		return consumerProgramStructureId;
	}
	public String getCreated_at() {
		return created_at;
	}
	public String getUpdated_at() {
		return updated_at;
	}
	public String getType() {
		return type;
	}
	public void setAcadsYear(String acadsYear) {
		this.acadsYear = acadsYear;
	}
	public void setAcadsMonth(String acadsMonth) {
		this.acadsMonth = acadsMonth;
	}
	public void setExamYear(String examYear) {
		this.examYear = examYear;
	}
	public void setExamMonth(String examMonth) {
		this.examMonth = examMonth;
	}
	public void setConsumerProgramStructureId(String consumerProgramStructureId) {
		this.consumerProgramStructureId = consumerProgramStructureId;
	}
	public void setCreated_at(String created_at) {
		this.created_at = created_at;
	}
	public void setUpdated_at(String updated_at) {
		this.updated_at = updated_at;
	}
	public void setType(String type) {
		this.type = type;
	}
	@Override
	public String toString() {
		return "ExamLiveSettingMBAWX [acadsYear=" + acadsYear + ", acadsMonth=" + acadsMonth + ", examYear=" + examYear
				+ ", examMonth=" + examMonth + ", consumerProgramStructureId=" + consumerProgramStructureId
				+ ", created_at=" + created_at + ", updated_at=" + updated_at + ", type=" + type + ", live=" + live
				+ "]";
	}
	
	
}
