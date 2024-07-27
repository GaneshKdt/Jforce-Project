package com.nmims.dto;

import java.io.Serializable;

public class PGGraceMarksDTO implements Serializable {

	private static final long serialVersionUID = 5229050644532001303L;

	private String sapid;
	private String consumerType;
	private String prgmStructApplicable;
	private String program;
	private String total;
	private String gracemarks;
	private String remarks;
	private String noOfSubjectsToClear;
	private String noOfSubjectsToClearLateral;
	private String subjects;
	private String noOfSubjectsToClearSem;
	private String islateral;

	public PGGraceMarksDTO() {
		super();
	}

	public String getSapid() {
		return sapid;
	}

	public void setSapid(String sapid) {
		this.sapid = sapid;
	}

	public String getConsumerType() {
		return consumerType;
	}

	public void setConsumerType(String consumerType) {
		this.consumerType = consumerType;
	}

	public String getPrgmStructApplicable() {
		return prgmStructApplicable;
	}

	public void setPrgmStructApplicable(String prgmStructApplicable) {
		this.prgmStructApplicable = prgmStructApplicable;
	}

	public String getProgram() {
		return program;
	}

	public void setProgram(String program) {
		this.program = program;
	}

	public String getTotal() {
		return total;
	}

	public void setTotal(String total) {
		this.total = total;
	}

	public String getGracemarks() {
		return gracemarks;
	}

	public void setGracemarks(String gracemarks) {
		this.gracemarks = gracemarks;
	}

	public String getRemarks() {
		return remarks;
	}

	public void setRemarks(String remarks) {
		this.remarks = remarks;
	}

	public String getNoOfSubjectsToClear() {
		return noOfSubjectsToClear;
	}

	public void setNoOfSubjectsToClear(String noOfSubjectsToClear) {
		this.noOfSubjectsToClear = noOfSubjectsToClear;
	}

	public String getNoOfSubjectsToClearLateral() {
		return noOfSubjectsToClearLateral;
	}

	public void setNoOfSubjectsToClearLateral(String noOfSubjectsToClearLateral) {
		this.noOfSubjectsToClearLateral = noOfSubjectsToClearLateral;
	}

	public String getSubjects() {
		return subjects;
	}

	public void setSubjects(String subjects) {
		this.subjects = subjects;
	}

	public String getNoOfSubjectsToClearSem() {
		return noOfSubjectsToClearSem;
	}

	public void setNoOfSubjectsToClearSem(String noOfSubjectsToClearSem) {
		this.noOfSubjectsToClearSem = noOfSubjectsToClearSem;
	}

	public String getIslateral() {
		return islateral;
	}

	public void setIslateral(String islateral) {
		this.islateral = islateral;
	}

	@Override
	public String toString() {
		return "PGGraceMarksDTO [sapid=" + sapid + ", consumerType=" + consumerType + ", prgmStructApplicable="
				+ prgmStructApplicable + ", program=" + program + ", total=" + total + ", gracemarks=" + gracemarks
				+ ", remarks=" + remarks + ", noOfSubjectsToClear=" + noOfSubjectsToClear
				+ ", noOfSubjectsToClearLateral=" + noOfSubjectsToClearLateral + ", subjects=" + subjects
				+ ", noOfSubjectsToClearSem=" + noOfSubjectsToClearSem + ", islateral=" + islateral + "]";
	}

}
