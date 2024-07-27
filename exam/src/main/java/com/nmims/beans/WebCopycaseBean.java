package com.nmims.beans;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import org.springframework.web.multipart.commons.CommonsMultipartFile;

public class WebCopycaseBean implements Serializable{
	

	private String id;
	private String subject;
	private String month;
	private String year;
	private String sapid;
	private String name;
	private String createdDate;
	private String aggregatedScore;
	private String totalWords;
	private String identicalWords;
	private String minorChangedWords;
	private String relatedMeaningWords;
	private String webReportPdfPath;
	private String threshold;
	private String responseJson;
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getSubject() {
		return subject;
	}
	public void setSubject(String subject) {
		this.subject = subject;
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
	public String getCreatedDate() {
		return createdDate;
	}
	public void setCreatedDate(String createdDate) {
		this.createdDate = createdDate;
	}
	public String getAggregatedScore() {
		return aggregatedScore;
	}
	public void setAggregatedScore(String aggregatedScore) {
		this.aggregatedScore = aggregatedScore;
	}
	public String getTotalWords() {
		return totalWords;
	}
	public void setTotalWords(String totalWords) {
		this.totalWords = totalWords;
	}
	public String getIdenticalWords() {
		return identicalWords;
	}
	public void setIdenticalWords(String identicalWords) {
		this.identicalWords = identicalWords;
	}
	public String getMinorChangedWords() {
		return minorChangedWords;
	}
	public void setMinorChangedWords(String minorChangedWords) {
		this.minorChangedWords = minorChangedWords;
	}
	public String getRelatedMeaningWords() {
		return relatedMeaningWords;
	}
	public void setRelatedMeaningWords(String relatedMeaningWords) {
		this.relatedMeaningWords = relatedMeaningWords;
	}
	public String getWebReportPdfPath() {
		return webReportPdfPath;
	}
	public void setWebReportPdfPath(String webReportPdfPath) {
		this.webReportPdfPath = webReportPdfPath;
	}
	public String getThreshold() {
		return threshold;
	}
	public void setThreshold(String threshold) {
		this.threshold = threshold;
	}
	public String getResponseJson() {
		return responseJson;
	}
	public void setResponseJson(String responseJson) {
		this.responseJson = responseJson;
	}
	@Override
	public String toString() {
		return "WebCopycaseBean [id=" + id + ", subject=" + subject + ", month=" + month + ", year=" + year + ", sapid="
				+ sapid + ", name=" + name + ", createdDate=" + createdDate + ", aggregatedScore=" + aggregatedScore
				+ ", totalWords=" + totalWords + ", identicalWords=" + identicalWords + ", minorChangedWords="
				+ minorChangedWords + ", relatedMeaningWords=" + relatedMeaningWords + ", webReportPdfPath="
				+ webReportPdfPath + ", threshold=" + threshold + ", responseJson=" + responseJson + "]";
	}
	
	
}
