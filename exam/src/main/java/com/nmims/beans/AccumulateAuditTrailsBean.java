package com.nmims.beans;

import java.io.Serializable;
import java.util.List;

public class AccumulateAuditTrailsBean  implements Serializable {
	
	private StudentsTestDetailsExamBean studentTestDetails;
	
	private List<StudentQuestionResponseExamBean> answers;

	private List<ErrorAnalyticsBean> errorAnalytics;
	
	private TestExamBean test;
	
	private List<PageVisitsBean> pageVisitsWeb;
	
	private List<NetworkLogsExamBean> networkLogs;
	
	private List<PageVisitExamBean> pageVisits;

	private List<LostFocusLogExamBean> lostFocusLog;
	
	private List<TestLogResponseExamBean> testLogs;
	
	
	public List<PageVisitsBean> getPageVisitsWeb() {
		return pageVisitsWeb;
	}

	public void setPageVisitsWeb(List<PageVisitsBean> pageVisitsWeb) {
		this.pageVisitsWeb = pageVisitsWeb;
	}

	public List<ErrorAnalyticsBean> getErrorAnalytics() {
		return errorAnalytics;
	}

	public void setErrorAnalytics(List<ErrorAnalyticsBean> errorAnalytics) {
		this.errorAnalytics = errorAnalytics;
	}

	public StudentsTestDetailsExamBean getStudentTestDetails() {
		return studentTestDetails;
	}

	public void setStudentTestDetails(StudentsTestDetailsExamBean studentTestDetails) {
		this.studentTestDetails = studentTestDetails;
	}

	public List<StudentQuestionResponseExamBean> getAnswers() {
		return answers;
	}

	public void setAnswers(List<StudentQuestionResponseExamBean> answers) {
		this.answers = answers;
	}

	public TestExamBean getTest() {
		return test;
	}

	public void setTest(TestExamBean test) {
		this.test = test;
	}

	public List<NetworkLogsExamBean> getNetworkLogs() {
		return networkLogs;
	}

	public void setNetworkLogs(List<NetworkLogsExamBean> networkLogs) {
		this.networkLogs = networkLogs;
	}

	public List<PageVisitExamBean> getPageVisits() {
		return pageVisits;
	}

	public void setPageVisits(List<PageVisitExamBean> pageVisits) {
		this.pageVisits = pageVisits;
	}

	public List<LostFocusLogExamBean> getLostFocusLog() {
		return lostFocusLog;
	}

	public void setLostFocusLog(List<LostFocusLogExamBean> lostFocusLog) {
		this.lostFocusLog = lostFocusLog;
	}

	public List<TestLogResponseExamBean> getTestLogs() {
		return testLogs;
	}

	public void setTestLogs(List<TestLogResponseExamBean> testLogs) {
		this.testLogs = testLogs;
	}

	@Override
	public String toString() {
		return "GetStudentTestAuditTrailDataBySapidTestIdBean [studentTestDetails=" + studentTestDetails + ", answers="
				+ answers + ", errorAnalytics=" + errorAnalytics + ", test=" + test + ", pageVisitsWeb=" + pageVisitsWeb
				+ ", networkLogs=" + networkLogs + ", pageVisits=" + pageVisits + ", lostFocusLog=" + lostFocusLog
				+ ", testLogs=" + testLogs + "]";
	}
}
