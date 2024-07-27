package com.nmims.beans;

public class programStudentPortalBean {
String id;
String code;
String name;
String createdBy;
String createdData;
String lastModifiedBy;
String lastModefiedDate;
public String getCode() {
	return code;
}
public void setCode(String code) {
	this.code = code;
}
public String getName() {
	return name;
}
public void setName(String name) {
	this.name = name;
}
public String getCreatedBy() {
	return createdBy;
}
public void setCreatedBy(String createdBy) {
	this.createdBy = createdBy;
}
public String getCreatedData() {
	return createdData;
}
public void setCreatedData(String createdData) {
	this.createdData = createdData;
}
public String getLastModifiedBy() {
	return lastModifiedBy;
}
public void setLastModifiedBy(String lastModifiedBy) {
	this.lastModifiedBy = lastModifiedBy;
}
public String getLastModefiedDate() {
	return lastModefiedDate;
}
public void setLastModefiedDate(String lastModefiedDate) {
	this.lastModefiedDate = lastModefiedDate;
}
@Override
public String toString() {
	return "programStudentPortalBean [id=" + id + ", code=" + code + ", name=" + name + ", createdBy=" + createdBy
			+ ", createdData=" + createdData + ", lastModifiedBy=" + lastModifiedBy + ", lastModefiedDate="
			+ lastModefiedDate + "]";
}

}
