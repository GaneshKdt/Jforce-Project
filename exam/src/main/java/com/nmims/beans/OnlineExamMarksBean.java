package com.nmims.beans;

import java.io.Serializable;

public class OnlineExamMarksBean  implements Serializable {
	private String year;
	private String month;
	private String id;
	private String sapid;
	private String name;
	private String subject;
	private double total;
	private double part1marks;
	private double part2marks;
	private double part3marks;
	private double part4marks;
	private String createdBy;
	private String createdDate;
	private String lastModifiedBy;
	private String lastModifiedDate;
	private String errorMessage = "";
	private boolean errorRecord = false;
	private String remarks ;
	private String studentType;
	
	private String program;
	private String sem;
	private String roundedTotal;
	
	private int prgm_sem_subj_id;
	
	public String getRemarks() {
		return remarks;
	}
	public void setRemarks(String remarks) {
		this.remarks = remarks;
	}
	public String getProgram() {
		return program;
	}
	public void setProgram(String program) {
		this.program = program;
	}
	public String getSem() {
		return sem;
	}
	public void setSem(String sem) {
		this.sem = sem;
	}
	public String getRoundedTotal() {
		return roundedTotal;
	}
	public void setRoundedTotal(String roundedTotal) {
		this.roundedTotal = roundedTotal;
	}
	public String getYear() {
		return year;
	}
	public void setYear(String year) {
		this.year = year;
	}
	public String getMonth() {
		return month;
	}
	public void setMonth(String month) {
		this.month = month;
	}
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getSapid() {
		return sapid;
	}
	public void setSapid(String sapid) {
		this.sapid = sapid;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getSubject() {
		return subject;
	}
	public void setSubject(String subject) {
		this.subject = subject;
	}
	public double getTotal() {
		return total;
	}
	public void setTotal(double total) {
		this.total = total;
	}
	public double getPart1marks() {
		return part1marks;
	}
	public void setPart1marks(double part1marks) {
		this.part1marks = part1marks;
	}
	public double getPart2marks() {
		return part2marks;
	}
	public void setPart2marks(double part2marks) {
		this.part2marks = part2marks;
	}
	public double getPart3marks() {
		return part3marks;
	}
	public void setPart3marks(double part3marks) {
		this.part3marks = part3marks;
	}
	public double getPart4marks() {
		return part4marks;
	}
	public void setPart4marks(double part4marks) {
		this.part4marks = part4marks;
	}
	public String getCreatedBy() {
		return createdBy;
	}
	public void setCreatedBy(String createdBy) {
		this.createdBy = createdBy;
	}
	public String getCreatedDate() {
		return createdDate;
	}
	public void setCreatedDate(String createdDate) {
		this.createdDate = createdDate;
	}
	public String getLastModifiedBy() {
		return lastModifiedBy;
	}
	public void setLastModifiedBy(String lastModifiedBy) {
		this.lastModifiedBy = lastModifiedBy;
	}
	public String getLastModifiedDate() {
		return lastModifiedDate;
	}
	public void setLastModifiedDate(String lastModifiedDate) {
		this.lastModifiedDate = lastModifiedDate;
	}
	public String getErrorMessage() {
		return errorMessage;
	}
	public void setErrorMessage(String errorMessage) {
		this.errorMessage = errorMessage;
	}
	public boolean isErrorRecord() {
		return errorRecord;
	}
	public void setErrorRecord(boolean errorRecord) {
		this.errorRecord = errorRecord;
	}
	public String getStudentType() {
		return studentType;
	}
	public void setStudentType(String studentType) {
		this.studentType = studentType;
	}
	public int getPrgm_sem_subj_id() {
		return prgm_sem_subj_id;
	}
	public void setPrgm_sem_subj_id(int prgm_sem_subj_id) {
		this.prgm_sem_subj_id = prgm_sem_subj_id;
	}
}
