package com.nmims.beans;

import java.io.Serializable;
import java.util.List;

//spring security related changes rename TestAuditTrailsApiResponseBean to TestAuditTrailsApiResponseExamBean
public class TestAuditTrailsApiResponseExamBean implements Serializable{
	
	private List<NetworkLogsExamBean> networkLogs;
	private List<PageVisitExamBean> pageVisits;
	private List<LostFocusLogExamBean> lostFocusLog;
	private List<TestLogResponseExamBean> testLogs;
	private String errorMessage;
	
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

	public String getErrorMessage() {
		return errorMessage;
	}

	public void setErrorMessage(String errorMessage) {
		this.errorMessage = errorMessage;
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
		return "TestAuditTrailsApiResponseBean [networkLogs=" + networkLogs + ", pageVisits=" + pageVisits
				+ ", lostFocusLog=" + lostFocusLog + ", testLogs=" + testLogs + ", errorMessage=" + errorMessage + "]";
	}	
	
	
}
