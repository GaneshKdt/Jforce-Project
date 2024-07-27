package com.nmims.dto;

public class OpenBadgesTopInTEEDto {
	
	private String sapid;
	private Integer writtenscore;
	private Integer studentrank;
	private String writtenYear;
	private String writtenMonth;
	private String subject;
	private Integer userId;
	private Integer sem;
	
	public String getSapid() {
		return sapid;
	}
	public void setSapid(String sapid) {
		this.sapid = sapid;
	}
	public Integer getWrittenscore() {
		return writtenscore;
	}
	public void setWrittenscore(Integer writtenscore) {
		this.writtenscore = writtenscore;
	}
	public Integer getStudentrank() {
		return studentrank;
	}
	public void setStudentrank(Integer studentrank) {
		this.studentrank = studentrank;
	}
	public String getWrittenYear() {
		return writtenYear;
	}
	public void setWrittenYear(String writtenYear) {
		this.writtenYear = writtenYear;
	}
	public String getWrittenMonth() {
		return writtenMonth;
	}
	public void setWrittenMonth(String writtenMonth) {
		this.writtenMonth = writtenMonth;
	}
	public String getSubject() {
		return subject;
	}
	public void setSubject(String subject) {
		this.subject = subject;
	}
	public Integer getUserId() {
		return userId;
	}
	public void setUserId(Integer userId) {
		this.userId = userId;
	}
	
	public Integer getSem() {
		return sem;
	}
	public void setSem(Integer sem) {
		this.sem = sem;
	}
	@Override
	public String toString() {
		return "OpenBadgesTopInTEEDto [sapid=" + sapid + ", writtenscore=" + writtenscore + ", studentrank="
				+ studentrank + ", writtenYear=" + writtenYear + ", writtenMonth=" + writtenMonth + ", subject="
				+ subject + ", userId=" + userId + ", sem=" + sem + "]";
	}
	

}
