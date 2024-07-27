package com.nmims.beans;

import java.io.Serializable;

public class ReRegistrationReportBean extends StudentExamBean  implements Serializable {
public String keyOfSapidAndSemester;
public String count;
public String gap;
public String informationCenter;
public String learningCenter;
public String sapid;
public String driveMonthYear;
public String commaSeperatedSubjects;


public String getCommaSeperatedSubjects() {
	return commaSeperatedSubjects;
}
public void setCommaSeperatedSubjects(String commaSeperatedSubjects) {
	this.commaSeperatedSubjects = commaSeperatedSubjects;
}
public String getDriveMonthYear() {
	return driveMonthYear;
}
public void setDriveMonthYear(String driveMonthYear) {
	this.driveMonthYear = driveMonthYear;
}
public String getInformationCenter() {
	return informationCenter;
}
public void setInformationCenter(String informationCenter) {
	this.informationCenter = informationCenter;
}
public String getLearningCenter() {
	return learningCenter;
}
public void setLearningCenter(String learningCenter) {
	this.learningCenter = learningCenter;
}
public String getSapid() {
	return sapid;
}
public void setSapid(String sapid) {
	this.sapid = sapid;
}
public String getGap() {
	return gap;
}
public void setGap(String gap) {
	this.gap = gap;
}
public String getKeyOfSapidAndSemester() {
	return keyOfSapidAndSemester;
}
public void setKeyOfSapidAndSemester(String keyOfSapidAndSemester) {
	this.keyOfSapidAndSemester = keyOfSapidAndSemester;
}
public String getCount() {
	return count;
}
public void setCount(String count) {
	this.count = count;
}

}
