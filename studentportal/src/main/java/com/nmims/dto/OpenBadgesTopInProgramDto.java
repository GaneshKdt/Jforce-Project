package com.nmims.dto;

public class OpenBadgesTopInProgramDto {
	
	private String sapid;
	private String program;
	private Integer rank;
	private Integer totalMarks;
	private Integer outOfMarks;
	private Integer sem;
	private Integer subjectCount;
	private String examMonth;
	private String acadMonth;
	private Integer year;
	
	public String getSapid() {
		return sapid;
	}
	public void setSapid(String sapid) {
		this.sapid = sapid;
	}
	public Integer getRank() {
		return rank;
	}
	public void setRank(Integer rank) {
		this.rank = rank;
	}
	public Integer getTotalMarks() {
		return totalMarks;
	}
	public void setTotalMarks(Integer totalMarks) {
		this.totalMarks = totalMarks;
	}
	public Integer getOutOfMarks() {
		return outOfMarks;
	}
	public void setOutOfMarks(Integer outOfMarks) {
		this.outOfMarks = outOfMarks;
	}
	public Integer getSem() {
		return sem;
	}
	public void setSem(Integer sem) {
		this.sem = sem;
	}
	public Integer getSubjectCount() {
		return subjectCount;
	}
	public void setSubjectCount(Integer subjectCount) {
		this.subjectCount = subjectCount;
	}
	public String getExamMonth() {
		return examMonth;
	}
	public void setExamMonth(String examMonth) {
		this.examMonth = examMonth;
	}
	public Integer getYear() {
		return year;
	}
	public void setYear(Integer year) {
		this.year = year;
	}
	public String getAcadMonth() {
		return acadMonth;
	}
	public void setAcadMonth(String acadMonth) {
		this.acadMonth = acadMonth;
	}
	public String getProgram() {
		return program;
	}
	public void setProgram(String program) {
		this.program = program;
	}
	
	@Override
	public String toString() {
		return "OpenBadgesTopInProgramDto [sapid=" + sapid + ", program=" + program + ", rank=" + rank + ", totalMarks="
				+ totalMarks + ", outOfMarks=" + outOfMarks + ", sem=" + sem + ", subjectCount=" + subjectCount
				+ ", examMonth=" + examMonth + ", acadMonth=" + acadMonth + ", year=" + year + "]";
	}
	
	
	
}
