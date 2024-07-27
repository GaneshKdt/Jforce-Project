package com.nmims.beans;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class BlockStudentExamCenterBean implements Serializable
{
	//Variables
	private String sapid;
	private String year;
	private String month;
	private ArrayList<String> centerNameList;
	private ArrayList<String> centerIdList;
	private String centerName;
	private String centerNameId;
	private String centerId;
	private String programStructureName;
	private String programStructureId;
	private String consumerTypeName;
	private String consumerTypeId;
	private String programName;
	private String programId;
	private String allowed;
	private String writenscore;
	private String examorder;
	private String masterKeyId;
	private List<BlockStudentExamCenterBean> studentList;
	private List<BlockStudentExamCenterBean> errorList;
	private String errorMessage = "";
	private boolean errorRecord = false;
	private Integer row;
	private String successMessage;
	
	//Getters And Setters
	public String getSapid() {
		return sapid;
	}
	public void setSapid(String sapid) {
		this.sapid = sapid;
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
	public ArrayList<String> getCenterNameList() {
		return centerNameList;
	}
	public void setCenterNameList(ArrayList<String> centerNameList) {
		this.centerNameList = centerNameList;
	}
	public String getCenterName() {
		return centerName;
	}
	public void setCenterName(String centerName) {
		this.centerName = centerName;
	}
	public String getProgramStructureName() {
		return programStructureName;
	}
	public void setProgramStructureName(String programStructureName) {
		this.programStructureName = programStructureName;
	}
	public String getProgramName() {
		return programName;
	}
	public void setProgramName(String programName) {
		this.programName = programName;
	}
	public String getProgramId() {
		return programId;
	}
	public void setProgramId(String programId) {
		this.programId = programId;
	}
	public String getConsumerTypeName() {
		return consumerTypeName;
	}
	public void setConsumerTypeName(String consumerTypeName) {
		this.consumerTypeName = consumerTypeName;
	}
	public String getAllowed() {
		return allowed;
	}
	public void setAllowed(String allowed) {
		this.allowed = allowed;
	}
	public String getCenterId() {
		return centerId;
	}
	public void setCenterId(String centerId) {
		this.centerId = centerId;
	}
	
	public ArrayList<String> getCenterIdList() {
		return centerIdList;
	}
	public void setCenterIdList(ArrayList<String> centerIdList) {
		this.centerIdList = centerIdList;
	}
	public String getProgramStructureId() {
		return programStructureId;
	}
	public void setProgramStructureId(String programStructureId) {
		this.programStructureId = programStructureId;
	}
	public String getConsumerTypeId() {
		return consumerTypeId;
	}
	public void setConsumerTypeId(String consumerTypeId) {
		this.consumerTypeId = consumerTypeId;
	}
	public String getCenterNameId() {
		return centerNameId;
	}
	public void setCenterNameId(String centerNameId) {
		this.centerNameId = centerNameId;
	}
	public String getWritenscore() {
		return writenscore;
	}
	public void setWritenscore(String writenscore) {
		this.writenscore = writenscore;
	}
	public String getExamorder() {
		return examorder;
	}
	public void setExamorder(String examorder) {
		this.examorder = examorder;
	}
	public String getMasterKeyId() {
		return masterKeyId;
	}
	public void setMasterKeyId(String masterKeyId) {
		this.masterKeyId = masterKeyId;
	}
	public List<BlockStudentExamCenterBean> getStudentList() {
		return studentList;
	}
	public void setStudentList(List<BlockStudentExamCenterBean> studentList) {
		this.studentList = studentList;
	}
	public List<BlockStudentExamCenterBean> getErrorList() {
		return errorList;
	}
	public void setErrorList(List<BlockStudentExamCenterBean> errorList) {
		this.errorList = errorList;
	}
	public String getErrorMessage() {
		return errorMessage;
	}
	public void setErrorMessage(String errorMessage) {
		this.errorMessage = errorMessage;
	}
	public boolean isErrorRecord() {
		return errorRecord;
	}
	public void setErrorRecord(boolean errorRecord) {
		this.errorRecord = errorRecord;
	}
	public Integer getRow() {
		return row;
	}
	public void setRow(Integer row) {
		this.row = row;
	}
	public String getSuccessMessage() {
		return successMessage;
	}
	public void setSuccessMessage(String successMessage) {
		this.successMessage = successMessage;
	}
	
	@Override
	public String toString() {
		return "ExamBookingBlockedCenterNameBean [sapid=" + sapid + ", year=" + year + ", month=" + month
				+ ", blockedCenterName=" + centerNameList + "]";
	}
}
