/**
 * 
 */
package com.nmims.beans;

import java.io.Serializable;

/**
 * @author vil_m
 *
 */
//spring security related changes rename MettlScheduleBean to MettlScheduleExamBean
public class MettlScheduleExamBean implements Serializable {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 2759682399712702139L;
	
	private String status;
	private String message;
	
	private String name;//Mandatory – Name of the Schedule
	private String scheduleType = "AlwaysOn";//Mandatory – Shown as Access Time in your online account - AlwaysOn OR Fixed
	private String testLinkType;
	private String fixedAccessOptionSW;//"ExactTime"/"SlotWise";
	//private String startsOnDateSW = "Thu, 27 Jun 2021";
	//private String endsOnDateSW = "Thu, 27 Jun 2021";
	//private String startsOnTimeSW = "17:30:00";
	//private String endsOnTimeSW = "18:00:00";
	private String locationtimeZoneSW; //"Asia/Kolkata";
	private String timeZoneSW; // = "UTC+05:30";
	private boolean enabledWP = Boolean.TRUE;
	private int countWP = 99;
	private boolean showRemainingCountsWP = Boolean.FALSE;
	private String modeVP = "VIDEO"; //"PHOTO"; //Before Apr22 was PHOTO
	private boolean candidateScreenCaptureVP = Boolean.TRUE;
	private boolean candidateAuthorizationVP = Boolean.FALSE;
	
	//private boolean isAccessToBeSent = Boolean.FALSE;//option for user to pass Access or not.//true = new Access object
	private String typeA;
	private String[][] candidatesA = null; //2D Array with name, email 
	private boolean sendEmailA = Boolean.FALSE;
	
	private boolean enabledIP = Boolean.FALSE;
	private String typeIP;
	private String ipIP;
	private String rangesIP;
	private boolean isAudioProctoring = Boolean.FALSE;
	
	private boolean enabledTG = Boolean.FALSE; //Before Apr22 exam it was TRUE
	private String[] recipientsTG; //= {"jforcesolutions@gmail.com"};
	private String sourceApp = "NGASCE";//Mandatory - Name of your application
	private String testStartNotificationUrl;
	private String testFinishNotificationUrl;
	private String testGradedNotificationUrl;
	private String testResumeEnabledForExpiredTestURL;
	
	//Query
	private String assessmentId;
	private String startsOnDate;
	private String date;
	private String startTime;
	private String endTime;
	private String endTime2;//(date + assessment duration) + 1 hour
	private String scheduleEndTime;//(date + startTime) + 1 hour

	//since December 2022 METTL waiting room API changes
	private String reportingStartTime;
	private String reportingFinishTime;

	private String customUrlId;
	private String examYear;
	private String examMonth;
	private String programStructure;
	private String subject;
	private String sifySubjectCode;
	
	//ExamsScheduleMettle Table
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
	
	public MettlScheduleExamBean() {
		super();
		// TODO Auto-generated constructor stub
	}

	public String getAssessmentId() {
		return assessmentId;
	}

	public void setAssessmentId(String assessmentId) {
		this.assessmentId = assessmentId;
	}

	public String getStartsOnDate() {
		return startsOnDate;
	}

	public void setStartsOnDate(String startsOnDate) {
		this.startsOnDate = startsOnDate;
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

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public String getTestStartNotificationUrl() {
		return testStartNotificationUrl;
	}

	public void setTestStartNotificationUrl(String testStartNotificationUrl) {
		this.testStartNotificationUrl = testStartNotificationUrl;
	}

	public String getTestFinishNotificationUrl() {
		return testFinishNotificationUrl;
	}

	public void setTestFinishNotificationUrl(String testFinishNotificationUrl) {
		this.testFinishNotificationUrl = testFinishNotificationUrl;
	}

	public String getTestGradedNotificationUrl() {
		return testGradedNotificationUrl;
	}

	public void setTestGradedNotificationUrl(String testGradedNotificationUrl) {
		this.testGradedNotificationUrl = testGradedNotificationUrl;
	}

	public String getTestResumeEnabledForExpiredTestURL() {
		return testResumeEnabledForExpiredTestURL;
	}

	public void setTestResumeEnabledForExpiredTestURL(String testResumeEnabledForExpiredTestURL) {
		this.testResumeEnabledForExpiredTestURL = testResumeEnabledForExpiredTestURL;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getScheduleType() {
		return scheduleType;
	}

	public void setScheduleType(String scheduleType) {
		this.scheduleType = scheduleType;
	}

	public String getFixedAccessOptionSW() {
		return fixedAccessOptionSW;
	}

	public void setFixedAccessOptionSW(String fixedAccessOptionSW) {
		this.fixedAccessOptionSW = fixedAccessOptionSW;
	}

	/*public String getStartsOnDateSW() {
		return startsOnDateSW;
	}

	public void setStartsOnDateSW(String startsOnDateSW) {
		this.startsOnDateSW = startsOnDateSW;
	}

	public String getEndsOnDateSW() {
		return endsOnDateSW;
	}

	public void setEndsOnDateSW(String endsOnDateSW) {
		this.endsOnDateSW = endsOnDateSW;
	}

	public String getStartsOnTimeSW() {
		return startsOnTimeSW;
	}

	public void setStartsOnTimeSW(String startsOnTimeSW) {
		this.startsOnTimeSW = startsOnTimeSW;
	}

	public String getEndsOnTimeSW() {
		return endsOnTimeSW;
	}

	public void setEndsOnTimeSW(String endsOnTimeSW) {
		this.endsOnTimeSW = endsOnTimeSW;
	}*/

	public String getLocationtimeZoneSW() {
		return locationtimeZoneSW;
	}

	public void setLocationtimeZoneSW(String locationtimeZoneSW) {
		this.locationtimeZoneSW = locationtimeZoneSW;
	}

	public String getTimeZoneSW() {
		return timeZoneSW;
	}

	public void setTimeZoneSW(String timeZoneSW) {
		this.timeZoneSW = timeZoneSW;
	}

	public boolean isEnabledWP() {
		return enabledWP;
	}

	public void setEnabledWP(boolean enabledWP) {
		this.enabledWP = enabledWP;
	}

	public int getCountWP() {
		return countWP;
	}

	public void setCountWP(int countWP) {
		this.countWP = countWP;
	}

	public boolean isShowRemainingCountsWP() {
		return showRemainingCountsWP;
	}

	public void setShowRemainingCountsWP(boolean showRemainingCountsWP) {
		this.showRemainingCountsWP = showRemainingCountsWP;
	}

	public String getModeVP() {
		return modeVP;
	}

	public void setModeVP(String modeVP) {
		this.modeVP = modeVP;
	}

	public boolean isCandidateScreenCaptureVP() {
		return candidateScreenCaptureVP;
	}

	public void setCandidateScreenCaptureVP(boolean candidateScreenCaptureVP) {
		this.candidateScreenCaptureVP = candidateScreenCaptureVP;
	}

	public boolean isCandidateAuthorizationVP() {
		return candidateAuthorizationVP;
	}

	public void setCandidateAuthorizationVP(boolean candidateAuthorizationVP) {
		this.candidateAuthorizationVP = candidateAuthorizationVP;
	}

	public String getTypeA() {
		return typeA;
	}

	public void setTypeA(String typeA) {
		this.typeA = typeA;
	}

	public String[][] getCandidatesA() {
		return candidatesA;
	}

	public void setCandidatesA(String[][] candidatesA) {
		this.candidatesA = candidatesA;
	}

	public boolean isSendEmailA() {
		return sendEmailA;
	}

	public void setSendEmailA(boolean sendEmailA) {
		this.sendEmailA = sendEmailA;
	}

	public boolean isEnabledIP() {
		return enabledIP;
	}

	public void setEnabledIP(boolean enabledIP) {
		this.enabledIP = enabledIP;
	}

	public String getTypeIP() {
		return typeIP;
	}

	public void setTypeIP(String typeIP) {
		this.typeIP = typeIP;
	}

	public String getIpIP() {
		return ipIP;
	}

	public void setIpIP(String ipIP) {
		this.ipIP = ipIP;
	}

	public String getRangesIP() {
		return rangesIP;
	}

	public void setRangesIP(String rangesIP) {
		this.rangesIP = rangesIP;
	}

	public boolean isEnabledTG() {
		return enabledTG;
	}

	public void setEnabledTG(boolean enabledTG) {
		this.enabledTG = enabledTG;
	}

	public String[] getRecipientsTG() {
		return recipientsTG;
	}

	public void setRecipientsTG(String[] recipientsTG) {
		this.recipientsTG = recipientsTG;
	}

	public String getSourceApp() {
		return sourceApp;
	}

	public void setSourceApp(String sourceApp) {
		this.sourceApp = sourceApp;
	}

	public String getEndTime2() {
		return endTime2;
	}

	public void setEndTime2(String endTime2) {
		this.endTime2 = endTime2;
	}

	public String getReportingStartTime() {
		return reportingStartTime;
	}

	public void setReportingStartTime(String reportingStartTime) {
		this.reportingStartTime = reportingStartTime;
	}

	public String getReportingFinishTime() {
		return reportingFinishTime;
	}

	public void setReportingFinishTime(String reportingFinishTime) {
		this.reportingFinishTime = reportingFinishTime;
	}

	public String getTestLinkType() {
		return testLinkType;
	}

	public void setTestLinkType(String testLinkType) {
		this.testLinkType = testLinkType;
	}

	public String getScheduleEndTime() {
		return scheduleEndTime;
	}

	public void setScheduleEndTime(String scheduleEndTime) {
		this.scheduleEndTime = scheduleEndTime;
	}

	public boolean isAudioProctoring() {
		return isAudioProctoring;
	}

	public void setAudioProctoring(boolean isAudioProctoring) {
		this.isAudioProctoring = isAudioProctoring;
	}

	@Override
	public String toString() {
		return "MettlScheduleExamBean [status=" + status + ", message=" + message + ", name=" + name + ", scheduleType="
				+ scheduleType + ", testLinkType=" + testLinkType + ", fixedAccessOptionSW=" + fixedAccessOptionSW
				+ ", locationtimeZoneSW=" + locationtimeZoneSW + ", timeZoneSW=" + timeZoneSW + ", enabledWP="
				+ enabledWP + ", countWP=" + countWP + ", showRemainingCountsWP=" + showRemainingCountsWP + ", modeVP="
				+ modeVP + ", candidateScreenCaptureVP=" + candidateScreenCaptureVP + ", candidateAuthorizationVP="
				+ candidateAuthorizationVP + ", typeA=" + typeA + ", candidatesA=" + candidatesA + ", sendEmailA="
				+ sendEmailA + ", enabledIP=" + enabledIP + ", typeIP=" + typeIP + ", ipIP=" + ipIP + ", rangesIP="
				+ rangesIP + ", isAudioProctoring=" + isAudioProctoring + ", enabledTG=" + enabledTG + ", recipientsTG="
				+ recipientsTG + ", sourceApp=" + sourceApp + ", testStartNotificationUrl=" + testStartNotificationUrl
				+ ", testFinishNotificationUrl=" + testFinishNotificationUrl + ", testGradedNotificationUrl="
				+ testGradedNotificationUrl + ", testResumeEnabledForExpiredTestURL="
				+ testResumeEnabledForExpiredTestURL + ", assessmentId=" + assessmentId + ", startsOnDate="
				+ startsOnDate + ", date=" + date + ", startTime=" + startTime + ", endTime=" + endTime + ", endTime2="
				+ endTime2 + ", scheduleEndTime=" + scheduleEndTime + ", reportingStartTime=" + reportingStartTime
				+ ", reportingFinishTime=" + reportingFinishTime + ", customUrlId=" + customUrlId + ", examYear="
				+ examYear + ", examMonth=" + examMonth + ", programStructure=" + programStructure + ", subject="
				+ subject + ", sifySubjectCode=" + sifySubjectCode + ", scheduleId=" + scheduleId + ", scheduleName="
				+ scheduleName + ", scheduleAccessKey=" + scheduleAccessKey + ", scheduleAccessURL=" + scheduleAccessURL
				+ ", scheduleStatus=" + scheduleStatus + ", active=" + active + ", resultLive=" + resultLive
				+ ", maxScore=" + maxScore + ", createdBy=" + createdBy + ", createdAt=" + createdAt
				+ ", lastModifiedBy=" + lastModifiedBy + ", lastModifiedAt=" + lastModifiedAt + "]";
	}
	
	
	
}