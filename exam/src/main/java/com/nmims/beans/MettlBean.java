/**
 * 
 */
package com.nmims.beans;

import java.io.Serializable;

/**
 * @author vil_m
 *
 */
@Deprecated
public class MettlBean  implements Serializable  {
	
	private String mettlAssessmentId;
	private String startsOnDate;

	private String customUrlId;
	private String examYear;
	private String examMonth;
	private String date;//
	private String startTime;//
	private String endTime;//
	private String programStructure;
	private String subject;
	private String sifySubjectCode;
	
	private String mettlStatus;
	
	//Schedule
	private String assessmentId;
	private String scheduleId;
	private String scheduleName;
	private String scheduleAccessKey;
	private String scheduleAccessURL;
	private String scheduleStatus;
	private String active;
	private String resultLive;
	private String maxScore;
	private String createdBy;
	private String createdAt;
	private String lastModifiedBy;
	private String lastModifiedAt;
	
	public MettlBean() {
		super();
		// TODO Auto-generated constructor stub
	}

	public String getCustomUrlId() {
		return customUrlId;
	}

	public void setCustomUrlId(String customUrlId) {
		this.customUrlId = customUrlId;
	}

	public String getExamYear() {
		return examYear;
	}

	public void setExamYear(String examYear) {
		this.examYear = examYear;
	}

	public String getExamMonth() {
		return examMonth;
	}

	public void setExamMonth(String examMonth) {
		this.examMonth = examMonth;
	}

	public String getDate() {
		return date;
	}

	public void setDate(String date) {
		this.date = date;
	}

	public String getStartTime() {
		return startTime;
	}

	public void setStartTime(String startTime) {
		this.startTime = startTime;
	}

	public String getEndTime() {
		return endTime;
	}

	public void setEndTime(String endTime) {
		this.endTime = endTime;
	}

	public String getProgramStructure() {
		return programStructure;
	}

	public void setProgramStructure(String programStructure) {
		this.programStructure = programStructure;
	}

	public String getSubject() {
		return subject;
	}

	public void setSubject(String subject) {
		this.subject = subject;
	}

	public String getSifySubjectCode() {
		return sifySubjectCode;
	}

	public void setSifySubjectCode(String sifySubjectCode) {
		this.sifySubjectCode = sifySubjectCode;
	}

	public String getMettlAssessmentId() {
		return mettlAssessmentId;
	}

	public void setMettlAssessmentId(String mettlAssessmentId) {
		this.mettlAssessmentId = mettlAssessmentId;
	}

	public String getMettlStatus() {
		return mettlStatus;
	}

	public void setMettlStatus(String mettlStatus) {
		this.mettlStatus = mettlStatus;
	}

	public String getAssessmentId() {
		return assessmentId;
	}

	public void setAssessmentId(String assessmentId) {
		this.assessmentId = assessmentId;
	}

	public String getScheduleId() {
		return scheduleId;
	}

	public void setScheduleId(String scheduleId) {
		this.scheduleId = scheduleId;
	}

	public String getScheduleName() {
		return scheduleName;
	}

	public void setScheduleName(String scheduleName) {
		this.scheduleName = scheduleName;
	}

	public String getScheduleAccessKey() {
		return scheduleAccessKey;
	}

	public void setScheduleAccessKey(String scheduleAccessKey) {
		this.scheduleAccessKey = scheduleAccessKey;
	}

	public String getScheduleAccessURL() {
		return scheduleAccessURL;
	}

	public void setScheduleAccessURL(String scheduleAccessURL) {
		this.scheduleAccessURL = scheduleAccessURL;
	}

	public String getScheduleStatus() {
		return scheduleStatus;
	}

	public void setScheduleStatus(String scheduleStatus) {
		this.scheduleStatus = scheduleStatus;
	}

	public String getActive() {
		return active;
	}

	public void setActive(String active) {
		this.active = active;
	}

	public String getResultLive() {
		return resultLive;
	}

	public void setResultLive(String resultLive) {
		this.resultLive = resultLive;
	}

	public String getMaxScore() {
		return maxScore;
	}

	public void setMaxScore(String maxScore) {
		this.maxScore = maxScore;
	}

	public String getCreatedBy() {
		return createdBy;
	}

	public void setCreatedBy(String createdBy) {
		this.createdBy = createdBy;
	}

	public String getCreatedAt() {
		return createdAt;
	}

	public void setCreatedAt(String createdAt) {
		this.createdAt = createdAt;
	}

	public String getLastModifiedBy() {
		return lastModifiedBy;
	}

	public void setLastModifiedBy(String lastModifiedBy) {
		this.lastModifiedBy = lastModifiedBy;
	}

	public String getLastModifiedAt() {
		return lastModifiedAt;
	}

	public void setLastModifiedAt(String lastModifiedAt) {
		this.lastModifiedAt = lastModifiedAt;
	}

	public String getStartsOnDate() {
		return startsOnDate;
	}

	public void setStartsOnDate(String startsOnDate) {
		this.startsOnDate = startsOnDate;
	}
	
}
