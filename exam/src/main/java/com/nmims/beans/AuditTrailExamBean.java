package com.nmims.beans;

import java.io.Serializable;

//spring security related changes rename AuditTrailBean to AuditTrailExamBean
public class AuditTrailExamBean  implements Serializable {

	private long testId;
	private String sapid;
	private String startDate;
	private int duration;
	private String testEndedStatus;
	private String testName;

	public long getTestId() {
		return testId;
	}

	public void setTestId(long testId) {
		this.testId = testId;
	}

	public String getSapid() {
		return sapid;
	}

	public void setSapid(String sapid) {
		this.sapid = sapid;
	}

	public String getStartDate() {
		return startDate;
	}

	public void setStartDate(String startDate) {
		this.startDate = startDate;
	}

	public int getDuration() {
		return duration;
	}

	public void setDuration(int duration) {
		this.duration = duration;
	}

	public String getTestEndedStatus() {
		return testEndedStatus;
	}

	public void setTestEndedStatus(String testEndedStatus) {
		this.testEndedStatus = testEndedStatus;
	}

	public String getTestName() {
		return testName;
	}

	public void setTestName(String testName) {
		this.testName = testName;
	}

}
