package com.nmims.beans;

import java.io.Serializable;

public class MBAStudentDetailsBean  implements Serializable  {

	private String sapid;
	private String consumerProgramStructureId;
	private String currentExamMonth;
	private String currentExamYear;
	private String currentAcadMonth;
	private String currentAcadYear;
	
	public String getSapid() {
		return sapid;
	}
	public void setSapid(String sapid) {
		this.sapid = sapid;
	}
	public String getCurrentExamMonth() {
		return currentExamMonth;
	}
	public void setCurrentExamMonth(String currentExamMonth) {
		this.currentExamMonth = currentExamMonth;
	}
	public String getCurrentExamYear() {
		return currentExamYear;
	}
	public void setCurrentExamYear(String currentExamYear) {
		this.currentExamYear = currentExamYear;
	}
	public String getCurrentAcadMonth() {
		return currentAcadMonth;
	}
	public void setCurrentAcadMonth(String currentAcadMonth) {
		this.currentAcadMonth = currentAcadMonth;
	}
	public String getCurrentAcadYear() {
		return currentAcadYear;
	}
	public void setCurrentAcadYear(String currentAcadYear) {
		this.currentAcadYear = currentAcadYear;
	}
	@Override
	public String toString() {
		return "MBAStudentDetailsBean [sapid=" + sapid + ", currentExamMonth=" + currentExamMonth + ", currentExamYear="
				+ currentExamYear + ", currentAcadMonth=" + currentAcadMonth + ", currentAcadYear=" + currentAcadYear
				+ "]";
	}
	public String getConsumerProgramStructureId() {
		return consumerProgramStructureId;
	}
	public void setConsumerProgramStructureId(String consumerProgramStructureId) {
		this.consumerProgramStructureId = consumerProgramStructureId;
	}
}
