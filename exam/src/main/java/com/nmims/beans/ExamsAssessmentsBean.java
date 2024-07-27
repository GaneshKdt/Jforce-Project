package com.nmims.beans;

import java.io.Serializable;

public class ExamsAssessmentsBean  implements Serializable  {
	private String id;
	private String assessments_id;
	private String name;
	private String customAssessmentName;
	private String programType;
	private String timebound_id;
	private String schedule_id;
	private String schedule_name;
	private String schedule_accessKey;
	private String schedule_accessUrl;
	private String schedule_status;
	private String exam_start_date_time;
	private String exam_end_date_time;
	private String active;

	private String type;
	private String testName;
	private String endDate;
	private String startDate;
	private String isResultLive;
	private String extendExamEndTime;
	private String batchName;
		
	public String getEndDate() {
		return endDate;
	}
	public void setEndDate(String endDate) {
		this.endDate = endDate;
	}
	private String subject;
	private String batch_id;
	private String createdBy;
	private String lastModifiedBy;
	private String created_at;
	private String updated_at;
	private String max_score;
	
	private Integer duration;
	
	private String reporting_finish_date_time;
	private String reporting_start_date_time;
	
	private String acadMonth;
	private String acadYear;
	
	public String getAcadMonth() {
		return acadMonth;
	}
	public void setAcadMonth(String acadMonth) {
		this.acadMonth = acadMonth;
	}
	public String getAcadYear() {
		return acadYear;
	}
	public void setAcadYear(String acadYear) {
		this.acadYear = acadYear;
	}
	public String getIsResultLive() {
		return isResultLive;
	}
	public void setIsResultLive(String isResultLive) {
		this.isResultLive = isResultLive;
	}
	public String getCreatedBy() {
		return createdBy;
	}
	public void setCreatedBy(String createdBy) {
		this.createdBy = createdBy;
	}
	public String getLastModifiedBy() {
		return lastModifiedBy;
	}
	public void setLastModifiedBy(String lastModifiedBy) {
		this.lastModifiedBy = lastModifiedBy;
	}
	public String getCreated_at() {
		return created_at;
	}
	public void setCreated_at(String created_at) {
		this.created_at = created_at;
	}
	public String getUpdated_at() {
		return updated_at;
	}
	public void setUpdated_at(String updated_at) {
		this.updated_at = updated_at;
	}
	public String getBatch_id() {
		return batch_id;
	}
	public void setBatch_id(String batch_id) {
		this.batch_id = batch_id;
	}
	public String getSubject() {
		return subject;
	}
	public void setSubject(String subject) {
		this.subject = subject;
	}
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getAssessments_id() {
		return assessments_id;
	}
	public void setAssessments_id(String assessments_id) {
		this.assessments_id = assessments_id;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getCustomAssessmentName() {
		return customAssessmentName;
	}
	public void setCustomAssessmentName(String customAssessmentName) {
		this.customAssessmentName = customAssessmentName;
	}
	public String getTimebound_id() {
		return timebound_id;
	}
	public void setTimebound_id(String timebound_id) {
		this.timebound_id = timebound_id;
	}
	public String getSchedule_id() {
		return schedule_id;
	}
	public void setSchedule_id(String schedule_id) {
		this.schedule_id = schedule_id;
	}
	public String getSchedule_name() {
		return schedule_name;
	}
	public void setSchedule_name(String schedule_name) {
		this.schedule_name = schedule_name;
	}
	public String getSchedule_accessKey() {
		return schedule_accessKey;
	}
	public void setSchedule_accessKey(String schedule_accessKey) {
		this.schedule_accessKey = schedule_accessKey;
	}
	public String getSchedule_accessUrl() {
		return schedule_accessUrl;
	}
	public void setSchedule_accessUrl(String schedule_accessUrl) {
		this.schedule_accessUrl = schedule_accessUrl;
	}
	public String getSchedule_status() {
		return schedule_status;
	}
	public void setSchedule_status(String schedule_status) {
		this.schedule_status = schedule_status;
	}
	public String getExam_start_date_time() {
		return exam_start_date_time;
	}
	public void setExam_start_date_time(String exam_start_date_time) {
		this.exam_start_date_time = exam_start_date_time;
	}
	public String getExam_end_date_time() {
		return exam_end_date_time;
	}
	public void setExam_end_date_time(String exam_end_date_time) {
		this.exam_end_date_time = exam_end_date_time;
	}
	public String getActive() {
		return active;
	}
	public void setActive(String active) {
		this.active = active;
	}
	public String getMax_score() {
		return max_score;
	}
	public void setMax_score(String max_score) {
		this.max_score = max_score;
	}
	
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	
	public String getTestName() {
		return testName;
	}
	public void setTestName(String testName) {
		this.testName = testName;
	}
	
	public String getStartDate() {
		return startDate;
	}
	public void setStartDate(String startDate) {
		this.startDate = startDate;
	}

	public String getProgramType() {
		return programType;
	}
	public void setProgramType(String programType) {
		this.programType = programType;
	}
	public String getExtendExamEndTime() {
		return extendExamEndTime;
	}
	public void setExtendExamEndTime(String extendExamEndTime) {
		this.extendExamEndTime = extendExamEndTime;
	}
	public String getBatchName() {
		return batchName;
	}
	public void setBatchName(String batchName) {
		this.batchName = batchName;
	}
	public Integer getDuration() {
		return duration;
	}
	public void setDuration(Integer duration) {
		this.duration = duration;
	}
	
	public String getReporting_finish_date_time() {
		return reporting_finish_date_time;
	}
	public String getReporting_start_date_time() {
		return reporting_start_date_time;
	}
	public void setReporting_finish_date_time(String reporting_finish_date_time) {
		this.reporting_finish_date_time = reporting_finish_date_time;
	}
	public void setReporting_start_date_time(String reporting_start_date_time) {
		this.reporting_start_date_time = reporting_start_date_time;
	}
	
	@Override
	public boolean equals(Object obj)
	{
		if (obj == this) {
	         return true;
	      }
		
		ExamsAssessmentsBean bean = (ExamsAssessmentsBean)obj;
		return this.timebound_id.equals(bean.getTimebound_id()) && this.schedule_accessKey.equals(bean.getSchedule_accessKey());
	}
	
	@Override
	public String toString() {
		return "ExamsAssessmentsBean [id=" + id + ", assessments_id=" + assessments_id + ", name=" + name
				+ ", customAssessmentName=" + customAssessmentName + ", programType=" + programType + ", timebound_id="
				+ timebound_id + ", schedule_id=" + schedule_id + ", schedule_name=" + schedule_name
				+ ", schedule_accessKey=" + schedule_accessKey + ", schedule_accessUrl=" + schedule_accessUrl
				+ ", schedule_status=" + schedule_status + ", exam_start_date_time=" + exam_start_date_time
				+ ", exam_end_date_time=" + exam_end_date_time + ", active=" + active + ", type=" + type + ", testName="
				+ testName + ", endDate=" + endDate + ", startDate=" + startDate + ", isResultLive=" + isResultLive
				+ ", extendExamEndTime=" + extendExamEndTime + ", batchName=" + batchName
				+ ", reporting_finish_date_time=" + reporting_finish_date_time + ", reporting_start_date_time="
				+ reporting_start_date_time + ", subject=" + subject + ", batch_id=" + batch_id + ", createdBy="
				+ createdBy + ", lastModifiedBy=" + lastModifiedBy + ", created_at=" + created_at + ", updated_at="
				+ updated_at + ", max_score=" + max_score + ", duration=" + duration + "]";
	}
}
