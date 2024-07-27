package com.nmims.beans;

import java.io.Serializable;
import java.util.List;

public class MBAExamBookingSubjects  implements Serializable  {

	private String sapid;
	private List<MBAStudentSubjectMarksDetailsBean> subjects;
	
	public String getSapid() {
		return sapid;
	}
	public void setSapid(String sapid) {
		this.sapid = sapid;
	}
	public List<MBAStudentSubjectMarksDetailsBean> getSubjects() {
		return subjects;
	}
	public void setSubjects(List<MBAStudentSubjectMarksDetailsBean> subjects) {
		this.subjects = subjects;
	}
	@Override
	public String toString() {
		return "ExamBookingRequest [sapid=" + sapid + ", subjects=" + subjects + "]";
	}
}
