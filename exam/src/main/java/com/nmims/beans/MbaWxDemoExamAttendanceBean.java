package com.nmims.beans;

public class MbaWxDemoExamAttendanceBean {
private String sapid;
private String demoExamId;
private String accessKey;
private String startedTime;
private String endTime;
private String markAttend;
private String status;
private String message;
private String id;


public String getMessage() {
	return message;
}
public void setMessage(String message) {
	this.message = message;
}
public String getId() {
	return id;
}
public void setId(String id) {
	this.id = id;
}
public String getSapid() {
	return sapid;
}
public void setSapid(String sapid) {
	this.sapid = sapid;
}
public String getDemoExamId() {
	return demoExamId;
}
public void setDemoExamId(String demoExamId) {
	this.demoExamId = demoExamId;
}
public String getAccessKey() {
	return accessKey;
}
public void setAccessKey(String accessKey) {
	this.accessKey = accessKey;
}
public String getStartedTime() {
	return startedTime;
}
public void setStartedTime(String startedTime) {
	this.startedTime = startedTime;
}
public String getEndTime() {
	return endTime;
}
public void setEndTime(String endTime) {
	this.endTime = endTime;
}
public String getMarkAttend() {
	return markAttend;
}
public void setMarkAttend(String markAttend) {
	this.markAttend = markAttend;
}
public String getStatus() {
	return status;
}
public void setStatus(String status) {
	this.status = status;
}

@Override
public String toString() {
	return "MbaWxDemoExamAttendanceBean [sapid=" + sapid + ", demoExamId=" + demoExamId + ", accessKey=" + accessKey
			+ ", startedTime=" + startedTime + ", endTime=" + endTime + ", markAttend=" + markAttend + ", status="
			+ status + "]";
}
}
