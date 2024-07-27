package com.nmims.beans;

import java.io.Serializable;

public class ExamResultChecklistBean implements Serializable {

	private static final long serialVersionUID = 2535100986194744882L;

	private String sapid;
	private String subject;
	private String year;
	private String month;
	private String result_pulled;
	private String assignment_processed;
	private String bod_applied;
	private String moved_to_marks;
	private String project_marks_uploaded;
	private String absent_marks_uploaded;
	private String marked_nv_ria;
	private String moved_to_passfail_staging;
	private String grace_applied;
	private String moved_to_passfail;
	private String make_result_live;
	private String category;
	private String created_by;
	private String created_at;
	private String updated_by;
	private String updated_at;

	public ExamResultChecklistBean() {
		super();
	}

	public ExamResultChecklistBean(String sapid, String subject, String year, String month) {
		this.sapid = sapid;
		this.subject = subject;
		this.year = year;
		this.month = month;
	}
	
	public ExamResultChecklistBean(String sapid, String subject) {
		this.sapid = sapid;
		this.subject = subject;
	}

	public String getSapid() {
		return sapid;
	}

	public String getSubject() {
		return subject;
	}

	public String getYear() {
		return year;
	}

	public String getMonth() {
		return month;
	}

	public String getAssignment_processed() {
		return assignment_processed;
	}

	public String getBod_applied() {
		return bod_applied;
	}

	public String getMoved_to_marks() {
		return moved_to_marks;
	}

	public String getProject_marks_uploaded() {
		return project_marks_uploaded;
	}

	public String getAbsent_marks_uploaded() {
		return absent_marks_uploaded;
	}

	public String getMarked_nv_ria() {
		return marked_nv_ria;
	}

	public String getGrace_applied() {
		return grace_applied;
	}

	public String getCreated_by() {
		return created_by;
	}

	public String getCreated_at() {
		return created_at;
	}

	public String getUpdated_by() {
		return updated_by;
	}

	public String getUpdated_at() {
		return updated_at;
	}

	public void setSapid(String sapid) {
		this.sapid = sapid;
	}

	public void setSubject(String subject) {
		this.subject = subject;
	}

	public void setYear(String year) {
		this.year = year;
	}

	public void setMonth(String month) {
		this.month = month;
	}

	public String getResult_pulled() {
		return result_pulled;
	}

	public void setResult_pulled(String result_pulled) {
		this.result_pulled = result_pulled;
	}

	public void setAssignment_processed(String assignment_processed) {
		this.assignment_processed = assignment_processed;
	}

	public void setBod_applied(String bod_applied) {
		this.bod_applied = bod_applied;
	}

	public void setMoved_to_marks(String moved_to_marks) {
		this.moved_to_marks = moved_to_marks;
	}

	public void setProject_marks_uploaded(String project_marks_uploaded) {
		this.project_marks_uploaded = project_marks_uploaded;
	}

	public void setAbsent_marks_uploaded(String absent_marks_uploaded) {
		this.absent_marks_uploaded = absent_marks_uploaded;
	}

	public void setMarked_nv_ria(String marked_nv_ria) {
		this.marked_nv_ria = marked_nv_ria;
	}

	public void setGrace_applied(String grace_applied) {
		this.grace_applied = grace_applied;
	}

	public void setCreated_by(String created_by) {
		this.created_by = created_by;
	}

	public void setCreated_at(String created_at) {
		this.created_at = created_at;
	}

	public void setUpdated_by(String updated_by) {
		this.updated_by = updated_by;
	}

	public void setUpdated_at(String updated_at) {
		this.updated_at = updated_at;
	}

	public String getMoved_to_passfail_staging() {
		return moved_to_passfail_staging;
	}

	public String getMoved_to_passfail() {
		return moved_to_passfail;
	}

	public String getMake_result_live() {
		return make_result_live;
	}

	public void setMoved_to_passfail_staging(String moved_to_passfail_staging) {
		this.moved_to_passfail_staging = moved_to_passfail_staging;
	}

	public void setMoved_to_passfail(String moved_to_passfail) {
		this.moved_to_passfail = moved_to_passfail;
	}

	public void setMake_result_live(String make_result_live) {
		this.make_result_live = make_result_live;
	}

	public String getCategory() {
		return category;
	}

	public void setCategory(String category) {
		this.category = category;
	}

	public String getKey() {
		return this.sapid.trim() + this.subject.trim();
	}

	@Override
	public String toString() {
		return "ExamResultChecklistBean [sapid=" + sapid + ", subject=" + subject + ", year=" + year + ", month="
				+ month + ", result_pulled=" + result_pulled + ", assignment_processed=" + assignment_processed
				+ ", bod_applied=" + bod_applied + ", moved_to_marks=" + moved_to_marks + ", project_marks_uploaded="
				+ project_marks_uploaded + ", absent_marks_uploaded=" + absent_marks_uploaded + ", marked_nv_ria="
				+ marked_nv_ria + ", moved_to_passfail_staging=" + moved_to_passfail_staging + ", grace_applied="
				+ grace_applied + ", moved_to_passfail=" + moved_to_passfail + ", make_result_live=" + make_result_live
				+ ", category=" + category + ", created_by=" + created_by + ", created_at=" + created_at
				+ ", updated_by=" + updated_by + ", updated_at=" + updated_at + "]";
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((sapid == null) ? 0 : sapid.hashCode());
		result = prime * result + ((subject == null) ? 0 : subject.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (!(obj instanceof ExamResultChecklistBean))
			return false;
		ExamResultChecklistBean other = (ExamResultChecklistBean) obj;
		if (sapid == null) {
			if (other.sapid != null)
				return false;
		} else if (!sapid.equals(other.sapid))
			return false;
		if (subject == null) {
			if (other.subject != null)
				return false;
		} else if (!subject.equals(other.subject))
			return false;
		return true;
	}

}
