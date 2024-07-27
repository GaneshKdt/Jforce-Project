package com.nmims.beans;

import java.io.Serializable;
import java.util.List;

public class StudentPendingSubjectsResponseBean  implements Serializable  {

	private String status;
	private String message;
	private List<StudentPendingSubjectBean> pendingSubjects;
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}
	public String getMessage() {
		return message;
	}
	public void setMessage(String message) {
		this.message = message;
	}
	public List<StudentPendingSubjectBean> getPendingSubjects() {
		return pendingSubjects;
	}
	public void setPendingSubjects(List<StudentPendingSubjectBean> pendingSubjects) {
		this.pendingSubjects = pendingSubjects;
	}
}
