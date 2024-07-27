package com.nmims.beans;

import java.io.Serializable;
import java.util.ArrayList;

public class SectionBean  implements Serializable  { 
	private String sectionName;
	private String sectionDay;
	private String sectionHours;
	private String sectionMinutes;
	private String instructions;
	private String allQnsMandatory;
	private String sectionRandQns;
	private String sectionQnCount;
	private String sectionQnType;
	private String  sectionDur;
	private String id;
	private String templateId;
	private String questionMarks;
	private ArrayList<SectionBean> sectionQnTypeConfigbean = new ArrayList<SectionBean>();
	private String type;  
	
	
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	public String getQuestionMarks() {
		return questionMarks;
	}
	public void setQuestionMarks(String questionMarks) {
		this.questionMarks = questionMarks;
	}
	public String getSectionDur() {
		return sectionDur;
	}
	public void setSectionDur(String sectionDur) {
		this.sectionDur = sectionDur;
	}
	public String getSectionName() {
		return sectionName;
	}
	public void setSectionName(String sectionName) {
		this.sectionName = sectionName;
	}
	public String getSectionDay() {
		return sectionDay;
	}
	public void setSectionDay(String sectionDay) {
		this.sectionDay = sectionDay;
	}
	public String getSectionHours() {
		return sectionHours;
	}
	public void setSectionHours(String sectionHours) {
		this.sectionHours = sectionHours;
	}
	public String getSectionMinutes() {
		return sectionMinutes;
	}
	public void setSectionMinutes(String sectionMinutes) {
		this.sectionMinutes = sectionMinutes;
	}
	public String getInstructions() {
		return instructions;
	}
	public void setInstructions(String instructions) {
		this.instructions = instructions;
	}
	public String getAllQnsMandatory() {
		return allQnsMandatory;
	}
	public void setAllQnsMandatory(String allQnsMandatory) {
		this.allQnsMandatory = allQnsMandatory;
	}
	public String getSectionRandQns() {
		return sectionRandQns;
	}
	public void setSectionRandQns(String sectionRandQns) {
		this.sectionRandQns = sectionRandQns;
	}
	public String getSectionQnCount() {
		return sectionQnCount;
	}
	public void setSectionQnCount(String sectionQnCount) {
		this.sectionQnCount = sectionQnCount;
	}
	public String getSectionQnType() {
		return sectionQnType;
	}
	public void setSectionQnType(String sectionQnType) {
		this.sectionQnType = sectionQnType;
	}
	public ArrayList<SectionBean> getSectionQnTypeConfigbean() {
		return sectionQnTypeConfigbean;
	}
	public void setSectionQnTypeConfigbean(ArrayList<SectionBean> sectionQnTypeConfigbean) {
		this.sectionQnTypeConfigbean = sectionQnTypeConfigbean;
	}
	
	public String getTemplateId() {
		return templateId;
	}
	public void setTemplateId(String templateId) {
		this.templateId = templateId;
	}
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	@Override
	public String toString() {
		return "SectionBean [sectionName=" + sectionName + ", sectionDay=" + sectionDay + ", sectionHours="
				+ sectionHours + ", sectionMinutes=" + sectionMinutes + ", instructions=" + instructions
				+ ", allQnsMandatory=" + allQnsMandatory + ", sectionRandQns=" + sectionRandQns + ", sectionQnCount="
				+ sectionQnCount + ", sectionQnType=" + sectionQnType + ", sectionDur=" + sectionDur + ", id=" + id
				+ ", templateId=" + templateId + ", questionMarks=" + questionMarks + ", sectionQnTypeConfigbean="
				+ sectionQnTypeConfigbean + "]";
	} 
	 
}
