package com.nmims.beans;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class MBAPassFailResponseBean implements Serializable{
private String id;
private String sapid;
private String sapIds;
private String iaScore;
private String teeScore;
private String graceMarks;
private int total;
private String rank;
private String pssId;
private String timeboundId;
private String sem;
private String subject;
private String outOfMarks;
private String pssIds;
private String pssIdCount;
private String month;
private String year;
private String masterkey;
private List<String> rankIds = new ArrayList<>();
private String isPass;
private String name;
private String imageUrl;
public String getSapid() {
	return sapid;
}
public void setSapid(String sapid) {
	this.sapid = sapid;
}
public String getIaScore() {
	return iaScore;
}
public void setIaScore(String iaScore) {
	this.iaScore = iaScore;
}
public String getTeeScore() {
	return teeScore;
}
public void setTeeScore(String teeScore) {
	this.teeScore = teeScore;
}
public String getGraceMarks() {
	return graceMarks;
}
public void setGraceMarks(String graceMarks) {
	this.graceMarks = graceMarks;
}
public int getTotal() {
	return total;
}
public void setTotal(int total) {
	this.total = total;
}
public String getRank() {
	return rank;
}
public String getPssId() {
	return pssId;
}
public void setPssId(String pssId) {
	this.pssId = pssId;
}
public void setRank(String rank) {
	this.rank = rank;
}
public String getTimeboundId() {
	return timeboundId;
}
public void setTimeboundId(String timeboundId) {
	this.timeboundId = timeboundId;
}
public String getSem() {
	return sem;
}
public void setSem(String sem) {
	this.sem = sem;
}
public String getSubject() {
	return subject;
}
public void setSubject(String subject) {
	this.subject = subject;
}
public String getOutOfMarks() {
	return outOfMarks;
}
public void setOutOfMarks(String outOfMarks) {
	this.outOfMarks = outOfMarks;
}
public List<String> getRankIds() {
	return rankIds;
}
public void setRankIds(List<String> rankIds) {
	this.rankIds = rankIds;
}
public String getId() {
	return id;
}
public void setId(String id) {
	this.id = id;
}
public String getMonth() {
	return month;
}
public void setMonth(String month) {
	this.month = month;
}
public String getYear() {
	return year;
}
public void setYear(String year) {
	this.year = year;
}
public String getPssIds() {
	return pssIds;
}
public void setPssIds(String pssIds) {
	this.pssIds = pssIds;
}
public String getPssIdCount() {
	return pssIdCount;
}
public void setPssIdCount(String pssIdCount) {
	this.pssIdCount = pssIdCount;
}
public String getMasterkey() {
	return masterkey;
}
public void setMasterkey(String masterkey) {
	this.masterkey = masterkey;
}
public String getSapIds() {
	return sapIds;
}
public void setSapIds(String sapIds) {
	this.sapIds = sapIds;
}
public String getIsPass() {
	return isPass;
}
public void setIsPass(String isPass) {
	this.isPass = isPass;
}
public String getName() {
	return name;
}
public void setName(String name) {
	this.name = name;
}
public String getImageUrl() {
	return imageUrl;
}
public void setImageUrl(String imageUrl) {
	this.imageUrl = imageUrl;
}

@Override
public String toString() {
	return "MBAPassFailResponseBean [id=" + id + ", sapid=" + sapid + ", sapIds=" + sapIds + ", iaScore=" + iaScore
			+ ", teeScore=" + teeScore + ", graceMarks=" + graceMarks + ", total=" + total + ", rank=" + rank
			+ ", pssId=" + pssId + ", timeboundId=" + timeboundId + ", sem=" + sem + ", subject=" + subject
			+ ", outOfMarks=" + outOfMarks + ", rankIds=" + rankIds + ", pssIds=" + pssIds + ", pssIdCount="
			+ pssIdCount + ", month=" + month + ", year=" + year + ", masterkey=" + masterkey + ", isPass=" + isPass
			+ ", name=" + name + ", imageUrl=" + imageUrl + "]";
}

}
