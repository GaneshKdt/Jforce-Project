package com.nmims.beans;

import java.io.Serializable;

public class MBAScheduleInfoBean  implements Serializable  {

	private String sapid;
	
	private String scheduleId;
	private String timeboundId;
	
	private String testName;
	private String subject;
	private long startTimestamp;
	private long endTimestamp;
	private int maxMarks;
	
	private boolean canStudentAttempt;
	private String cantAttemptReason;
	
	private Integer duration;
	private long reportingStartTimeStamp;
	
	public long getReportingStartTimeStamp() {
		return reportingStartTimeStamp;
	}
	public void setReportingStartTimeStamp(long reportingStartTimeStamp) {
		this.reportingStartTimeStamp = reportingStartTimeStamp;
	}
	public String getSapid() {
		return sapid;
	}
	public void setSapid(String sapid) {
		this.sapid = sapid;
	}
	public String getScheduleId() {
		return scheduleId;
	}
	public void setScheduleId(String scheduleId) {
		this.scheduleId = scheduleId;
	}
	public String getTimeboundId() {
		return timeboundId;
	}
	public void setTimeboundId(String timeboundId) {
		this.timeboundId = timeboundId;
	}
	public String getTestName() {
		return testName;
	}
	public void setTestName(String testName) {
		this.testName = testName;
	}
	public String getSubject() {
		return subject;
	}
	public void setSubject(String subject) {
		this.subject = subject;
	}
	public long getStartTimestamp() {
		return startTimestamp;
	}
	public void setStartTimestamp(long startTimestamp) {
		this.startTimestamp = startTimestamp;
	}
	public long getEndTimestamp() {
		return endTimestamp;
	}
	public void setEndTimestamp(long endTimestamp) {
		this.endTimestamp = endTimestamp;
	}
	public boolean isCanStudentAttempt() {
		return canStudentAttempt;
	}
	public void setCanStudentAttempt(boolean canStudentAttempt) {
		this.canStudentAttempt = canStudentAttempt;
	}
	@Override
	public String toString() {
		return "MBAScheduleInfoBean [sapid=" + sapid + ", scheduleId=" + scheduleId + ", timeboundId=" + timeboundId
				+ ", testName=" + testName + ", subject=" + subject + ", startTimestamp=" + startTimestamp
				+ ", endTimestamp=" + endTimestamp + ", canStudentAttempt=" + canStudentAttempt + "]";
	}
	public int getMaxMarks() {
		return maxMarks;
	}
	public void setMaxMarks(int maxMarks) {
		this.maxMarks = maxMarks;
	}
	public String getCantAttemptReason() {
		return cantAttemptReason;
	}
	public void setCantAttemptReason(String cantAttemptReason) {
		this.cantAttemptReason = cantAttemptReason;
	}
	public Integer getDuration() {
		return duration;
	}
	public void setDuration(Integer duration) {
		this.duration = duration;
	}
	
	
}