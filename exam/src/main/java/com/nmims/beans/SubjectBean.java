package com.nmims.beans;

import java.io.Serializable;

public class SubjectBean  implements Serializable  {
	private int id;
	private String subjectname;
	private String subjectbbcode;
	private String commonSubject;
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public String getSubjectname() {
		return subjectname;
	}
	public void setSubjectname(String subjectname) {
		this.subjectname = subjectname;
	}
	public String getSubjectbbcode() {
		return subjectbbcode;
	}
	public void setSubjectbbcode(String subjectbbcode) {
		this.subjectbbcode = subjectbbcode;
	}
	public String getCommonSubject() {
		return commonSubject;
	}
	public void setCommonSubject(String commonSubject) {
		this.commonSubject = commonSubject;
	}
	
}
