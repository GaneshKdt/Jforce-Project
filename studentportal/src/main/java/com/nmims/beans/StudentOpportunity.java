package com.nmims.beans;

import java.util.Date;

public class StudentOpportunity {

	private String sapId;
	
	private String termCleared;
	
	private Integer semester;
	
	private String stageName;
	
	private Date createdDate;

	public StudentOpportunity(String sapId, String termCleared, Integer semester, String stageName) {
		this.sapId = sapId;
		this.termCleared = termCleared;
		this.semester = semester;
		this.stageName = stageName;
	}

	public StudentOpportunity() {
		// TODO Auto-generated constructor stub
	}

	public String getSapId() {
		return sapId;
	}

	public Date getCreatedDate() {
		return createdDate;
	}

	public void setCreatedDate(Date createdDate) {
		this.createdDate = createdDate;
	}

	public void setSapId(String sapId) {
		this.sapId = sapId;
	}

	public String getTermCleared() {
		return termCleared;
	}

	public void setTermCleared(String termCleared) {
		this.termCleared = termCleared;
	}

	public Integer getSemester() {
		return semester;
	}

	public void setSemester(Integer semester) {
		this.semester = semester;
	}

	public String getStageName() {
		return stageName;
	}

	public void setStageName(String stageName) {
		this.stageName = stageName;
	}

	@Override
	public String toString() {
		return "StudentOpportunity [sapId=" + sapId + ", termCleared=" + termCleared + ", semester=" + semester
				+ ", stageName=" + stageName + ", createdDate=" + createdDate + "]";
	}	
}
