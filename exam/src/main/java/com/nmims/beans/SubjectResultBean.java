package com.nmims.beans;

import java.io.Serializable;

public class SubjectResultBean implements Serializable {
	
	private String subject;
	private String writtenScore;
	private String assignmentScore;
	private String grace;
	private String total;
	private String writtenMonthYear;
	private String assignmentMonthYear;
	boolean graceApplied = false;
	boolean assignmentCarryForward = false;
	boolean totalCarryForward = false;
	boolean writtenCarryForward = false;
	boolean pass = true;
	boolean resultOnHold = false;
	private boolean isWaivedOff;
	private String remarks;
	
	
	
	
	
	public SubjectResultBean(String subject, String writtenScore,
			String assignmentScore, String grace, String total,
			String writtenMonthYear, String assignmentMonthYear) {
		super();
		this.subject = subject;
		this.writtenScore = writtenScore;
		this.assignmentScore = assignmentScore;
		this.grace = grace;
		this.total = total;
		this.writtenMonthYear = writtenMonthYear;
		this.assignmentMonthYear = assignmentMonthYear;
	}
	
	public SubjectResultBean(String subject, String writtenScore,
			String assignmentScore, String grace, String total,
			String writtenMonthYear, String assignmentMonthYear,
			boolean graceApplied, boolean assignmentCarryForward, boolean pass) {
		super();
		this.subject = subject;
		this.writtenScore = writtenScore;
		this.assignmentScore = assignmentScore;
		this.grace = grace;
		this.total = total;
		this.writtenMonthYear = writtenMonthYear;
		this.assignmentMonthYear = assignmentMonthYear;
		this.graceApplied = graceApplied;
		this.assignmentCarryForward = assignmentCarryForward;
		this.pass = pass;
	}
	
	public SubjectResultBean(String subject, String writtenScore,
			String total,String writtenMonthYear,String assignmentMonthYear) {
		super();
		this.subject = subject;
		this.writtenScore = writtenScore;
		this.total = total;
		this.writtenMonthYear = writtenMonthYear;
		this.assignmentMonthYear = assignmentMonthYear;
	}
	
	public boolean isResultOnHold() {
		return resultOnHold;
	}

	public void setResultOnHold(boolean resultOnHold) {
		this.resultOnHold = resultOnHold;
	}

	public boolean isWrittenCarryForward() {
		return writtenCarryForward;
	}

	public void setWrittenCarryForward(boolean writtenCarryForward) {
		this.writtenCarryForward = writtenCarryForward;
	}

	public boolean isTotalCarryForward() {
		return totalCarryForward;
	}

	public void setTotalCarryForward(boolean totalCarryForward) {
		this.totalCarryForward = totalCarryForward;
	}
	public String getSubject() {
		return subject;
	}
	public void setSubject(String subject) {
		this.subject = subject;
	}
	public String getWrittenScore() {
		return writtenScore;
	}
	public void setWrittenScore(String writtenScore) {
		this.writtenScore = writtenScore;
	}
	public String getAssignmentScore() {
		return assignmentScore;
	}
	public void setAssignmentScore(String assignmentScore) {
		this.assignmentScore = assignmentScore;
	}
	public String getGrace() {
		return grace;
	}
	public void setGrace(String grace) {
		this.grace = grace;
	}
	public String getTotal() {
		return total;
	}
	public void setTotal(String total) {
		this.total = total;
	}
	public String getWrittenMonthYear() {
		return writtenMonthYear;
	}
	public void setWrittenMonthYear(String writtenMonthYear) {
		this.writtenMonthYear = writtenMonthYear;
	}
	public String getAssignmentMonthYear() {
		return assignmentMonthYear;
	}
	public void setAssignmentMonthYear(String assignmentMonthYear) {
		this.assignmentMonthYear = assignmentMonthYear;
	}
	public boolean isGraceApplied() {
		return graceApplied;
	}
	public void setGraceApplied(boolean graceApplied) {
		this.graceApplied = graceApplied;
	}
	
	public boolean isAssignmentCarryForward() {
		return assignmentCarryForward;
	}

	public void setAssignmentCarryForward(boolean assignmentCarryForward) {
		this.assignmentCarryForward = assignmentCarryForward;
	}

	public boolean isPass() {
		return pass;
	}
	public void setPass(boolean pass) {
		this.pass = pass;
	}

	public boolean isWaivedOff() {
		return isWaivedOff;
	}

	public void setWaivedOff(boolean isWaivedOff) {
		this.isWaivedOff = isWaivedOff;
	}

	public String getRemarks() {
		return remarks;
	}

	public void setRemarks(String remarks) {
		this.remarks = remarks;
	}
	

}
