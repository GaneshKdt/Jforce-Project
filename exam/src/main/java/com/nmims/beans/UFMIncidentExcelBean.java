package com.nmims.beans;

import java.io.Serializable;
import java.util.List;

public class UFMIncidentExcelBean implements Serializable{
private List<UFMIncidentBean> successList;
private List<UFMIncidentBean> errorList;
private String errorMessage ;
public List<UFMIncidentBean> getSuccessList() {
	return successList;
}
public void setSuccessList(List<UFMIncidentBean> successList) {
	this.successList = successList;
}
public List<UFMIncidentBean> getErrorList() {
	return errorList;
}
public void setErrorList(List<UFMIncidentBean> errorList) {
	this.errorList = errorList;
}
public String getErrorMessage() {
	return errorMessage;
}
public void setErrorMessage(String errorMessage) {
	this.errorMessage = errorMessage;
}


}
