package com.nmims.beans;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import org.springframework.web.multipart.commons.CommonsMultipartFile;

public class UFMIncidentBean implements Serializable {
private int id;
private int ufm_student_id;
private String year;
private String month ;
private String category;
private String sapid;
private String subject ;
private String incident;
private String time_Stamp;
private String video_Number;
private String error;
private String incidentKey;
private List<List<Integer>> errorColumns;
private CommonsMultipartFile fileData;
private String examDate;

public String getExamDate() {
	return examDate;
}

public void setExamDate(String examDate) {
	this.examDate = examDate;
}

public int getId() {
	return id;
}

public void setId(int id) {
	this.id = id;
}

public UFMIncidentBean() {
	errorColumns= new ArrayList<List<Integer>>();
}

public List<List<Integer>> getErrorColumns() {
	return errorColumns;
}
public void setErrorColumns(List<List<Integer>> errorColumns) {
	this.errorColumns = errorColumns;
}
public String getIncidentKey() {
	return incidentKey;
}
public void setIncidentKey(String incidentKey) {
	this.incidentKey = incidentKey;
}
public CommonsMultipartFile getFileData() {
	return fileData;
}
public void setFileData(CommonsMultipartFile fileData) {
	this.fileData = fileData;
}
public int getUfm_student_id() {
	return ufm_student_id;
}
public void setUfm_student_id(int ufm_student_id) {
	this.ufm_student_id = ufm_student_id;
}
public String getYear() {
	return year;
}
public void setYear(String year) {
	this.year = year;
}
public String getMonth() {
	return month;
}
public void setMonth(String month) {
	this.month = month;
}
public String getCategory() {
	return category;
}
public void setCategory(String category) {
	this.category = category;
}
public String getSapid() {
	return sapid;
}
public void setSapid(String sapid) {
	this.sapid = sapid;
}
public String getSubject() {
	return subject;
}
public void setSubject(String subject) {
	this.subject = subject;
}
public String getIncident() {
	return incident;
}
public void setIncident(String incident) {
	this.incident = incident;
}
public String getTime_Stamp() {
	return time_Stamp;
}
public void setTime_Stamp(String time_Stamp) {
	this.time_Stamp = time_Stamp;
}
public String getVideo_Number() {
	return video_Number;
}
public void setVideo_Number(String video_Number) {
	this.video_Number = video_Number;
}
public String getError() {
	return error;
}
public void setError(String error) {
	this.error = error;
}
@Override
public String toString() {
	return "UFMIncidentBean [ufm_student_id=" + ufm_student_id + ", year=" + year + ", month=" + month + ", category="
			+ category + ", sapid=" + sapid + ", subject=" + subject + ", incident=" + incident + ", time_Stamp="
			+ time_Stamp + ", video_Number=" + video_Number + ", error=" + error + ", incidentKey=" + incidentKey
			+ ", fileData=" + fileData + "]";
}






}
