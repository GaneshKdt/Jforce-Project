package com.nmims.dto;

public class OpenBadgesTopInSemesterDto {
	private Integer userId;	
	private String sapid;
	private Integer rank;
	private Integer totalMarks;
	private Integer outOfMarks;
	private Integer sem;
	private Integer subjectCount;
	
	
	
	public Integer getUserId() {
		return userId;
	}
	public void setUserId(Integer userId) {
		this.userId = userId;
	}
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
	
	@Override
	public String toString() {
		return "OpenBadgesTopInSemesterDto [userId=" + userId + ", sapid=" + sapid + ", rank=" + rank + ", totalMarks="
				+ totalMarks + ", outOfMarks=" + outOfMarks + ", sem=" + sem + ", subjectCount=" + subjectCount + "]";
	}
	
	
	
	
}
