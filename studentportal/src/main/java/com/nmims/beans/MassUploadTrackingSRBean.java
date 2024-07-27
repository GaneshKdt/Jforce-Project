package com.nmims.beans;

import java.io.Serializable;
import java.util.List;

public class MassUploadTrackingSRBean implements Serializable{
	
	private static final long serialVersionUID = 1L;
	
	private String sapId;
	private String studentName;
	private String emailId;
	private Integer serviceRequestId;
	private String serviceRequestType;
	private String courierName;
	private String trackId;
	private String url;
	private String mailStatus;
	private String createdBy;
	private String lastModifiedBy;
	private String createdDate;
	private String lastModifiedDate;
	private String errorMessage;
	private Integer row;
	private String successMessage;
	private List<MassUploadTrackingSRBean> successList;
	private List<MassUploadTrackingSRBean> errorList;
	private Integer amount;
	private String fromDate;
	private String toDate;
	
	public String getSapId() {
		return sapId;
	}
	public void setSapId(String sapId) {
		this.sapId = sapId;
	}
	public String getStudentName() {
		return studentName;
	}
	public void setStudentName(String studentName) {
		this.studentName = studentName;
	}
	public String getEmailId() {
		return emailId;
	}
	public void setEmailId(String emailId) {
		this.emailId = emailId;
	}
	public Integer getServiceRequestId() {
		return serviceRequestId;
	}
	public void setServiceRequestId(Integer serviceRequestId) {
		this.serviceRequestId = serviceRequestId;
	}
	public String getServiceRequestType() {
		return serviceRequestType;
	}
	public void setServiceRequestType(String serviceRequestType) {
		this.serviceRequestType = serviceRequestType;
	}
	public String getCourierName() {
		return courierName;
	}
	public void setCourierName(String courierName) {
		this.courierName = courierName;
	}
	public String getTrackId() {
		return trackId;
	}
	public void setTrackId(String trackId) {
		this.trackId = trackId;
	}
	public String getUrl() {
		return url;
	}
	public void setUrl(String url) {
		this.url = url;
	}
	public String getMailStatus() {
		return mailStatus;
	}
	public void setMailStatus(String mailStatus) {
		this.mailStatus = mailStatus;
	}
	public String getCreatedBy() {
		return createdBy;
	}
	public void setCreatedBy(String createdBy) {
		this.createdBy = createdBy;
	}
	public String getLastModifiedBy() {
		return lastModifiedBy;
	}
	public void setLastModifiedBy(String lastModifiedBy) {
		this.lastModifiedBy = lastModifiedBy;
	}
	public String getCreatedDate() {
		return createdDate;
	}
	public void setCreatedDate(String createdDate) {
		this.createdDate = createdDate;
	}
	public String getLastModifiedDate() {
		return lastModifiedDate;
	}
	public void setLastModifiedDate(String lastModifiedDate) {
		this.lastModifiedDate = lastModifiedDate;
	}
	public String getErrorMessage() {
		return errorMessage;
	}
	public void setErrorMessage(String errorMessage) {
		this.errorMessage = errorMessage;
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
	public List<MassUploadTrackingSRBean> getSuccessList() {
		return successList;
	}
	public void setSuccessList(List<MassUploadTrackingSRBean> successList) {
		this.successList = successList;
	}
	public List<MassUploadTrackingSRBean> getErrorList() {
		return errorList;
	}
	public void setErrorList(List<MassUploadTrackingSRBean> errorList) {
		this.errorList = errorList;
	}
	public Integer getAmount() {
		return amount;
	}
	public void setAmount(Integer amount) {
		this.amount = amount;
	}
	public String getFromDate() {
		return fromDate;
	}
	public void setFromDate(String fromDate) {
		this.fromDate = fromDate;
	}
	public String getToDate() {
		return toDate;
	}
	public void setToDate(String toDate) {
		this.toDate = toDate;
	}
	public static long getSerialversionuid() {
		return serialVersionUID;
	}
	
	@Override
	public String toString() {
		return "MassUploadTrackingSRBean [sapId=" + sapId + ", studentName=" + studentName + ", emailId=" + emailId
				+ ", serviceRequestId=" + serviceRequestId + ", serviceRequestType=" + serviceRequestType
				+ ", courierName=" + courierName + ", trackId=" + trackId + ", url=" + url + ", mailStatus="
				+ mailStatus + ", createdBy=" + createdBy + ", lastModifiedBy=" + lastModifiedBy + ", createdDate="
				+ createdDate + ", lastModifiedDate=" + lastModifiedDate + ", errorMessage=" + errorMessage + ", row="
				+ row + ", successMessage=" + successMessage + ", successList=" + successList + ", errorList="
				+ errorList + ", amount=" + amount + ", fromDate=" + fromDate + ", toDate=" + toDate + "]";
	}
	
}
