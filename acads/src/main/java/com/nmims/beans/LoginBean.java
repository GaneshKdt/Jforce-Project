package com.nmims.beans;

import java.io.Serializable;

public class LoginBean  implements Serializable {
public String sapid;
public String logintime;
public String enrollmentYear;
public String enrollmentMonth;
public String getEnrollmentYear() {
	return enrollmentYear;
}
public void setEnrollmentYear(String enrollmentYear) {
	this.enrollmentYear = enrollmentYear;
}
public String getEnrollmentMonth() {
	return enrollmentMonth;
}
public void setEnrollmentMonth(String enrollmentMonth) {
	this.enrollmentMonth = enrollmentMonth;
}
public String getSapid() {
	return sapid;
}
public void setSapid(String sapid) {
	this.sapid = sapid;
}
public String getLogintime() {
	return logintime;
}
public void setLogintime(String logintime) {
	this.logintime = logintime;
}

}
