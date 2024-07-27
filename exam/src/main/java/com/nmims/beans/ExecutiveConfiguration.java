package com.nmims.beans;
import java.io.Serializable;
@SuppressWarnings("serial")
public class ExecutiveConfiguration extends BaseExamBean implements Serializable{
 private String id;
 private String configurationType;
 private String startTime;
 private String endTime;
 private String prgrmStructApplicable;
 private String program;
 private String subject;
 
 public String getId() {
  return id;
 }
 public void setId(String id) {
  this.id = id;
 }
 public String getConfigurationType() {
  return configurationType;
 }
 public void setConfigurationType(String configurationType) {
  this.configurationType = configurationType;
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
 public String getPrgrmStructApplicable() {
  return prgrmStructApplicable;
 }
 public void setPrgrmStructApplicable(String prgrmStructApplicable) {
  this.prgrmStructApplicable = prgrmStructApplicable;
 }
 
 public String getSubject() {
  return subject;
 }
 public void setSubject(String subject) {
  this.subject = subject;
 }
 public String getProgram() {
  return program;
 }
 public void setProgram(String programList) {
  this.program = programList;
 }
@Override
public String toString() {
	return "ExecutiveConfiguration [id=" + id + ", configurationType="
			+ configurationType + ", startTime=" + startTime + ", endTime="
			+ endTime + ", prgrmStructApplicable=" + prgrmStructApplicable
			+ ", program=" + program + ", subject=" + subject + "]";
}
 
 
}


