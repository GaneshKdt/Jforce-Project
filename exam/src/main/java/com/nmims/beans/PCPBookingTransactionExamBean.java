package com.nmims.beans;

import java.io.Serializable;
import java.util.ArrayList;

//spring security related changes rename PCPBookingTransactionBean to PCPBookingTransactionExamBean
public class PCPBookingTransactionExamBean implements Serializable{
	
	private String sapid;
    private String subject;
    private String centerId;
    private String year;
    private String month;
    private String program;
    private String sem;
    private String trackId;
    private String amount;
    private String tranDateTime;
    private String bookingCompleteTime;
    private String tranStatus;
    private String booked;
    private String ddno;
    private String bank;
    private String ddAmount;
    private String paymentMode;
    private String examDate;
    private String examTime;
    private String examMode;
    private String examEndTime;
    
    private String transactionID;
    private String requestID;
    private String merchantRefNo;
    private String secureHash;
    private String respAmount;
    private String description;
    private String responseCode;
    private String respPaymentMethod;
    private String isFlagged;
    private String paymentID;
    private String responseMessage;
    private String error;
    private String respTranDateTime;
    private String htDownloaded;
    private String filePath;
    
	private String transactionType;
    private String status;
    private String respMode;
    private String errorCode;
    private String examCenterName;
    private String city;
    private String address;
    
    private String lastName;
    private String firstName;
    private String emailId;
    private String mobile;
    private String altPhone;
    private String ddDate;
    private String ddReason;
    private String subjectCount;
    private String centerCode;
	private String centerName;
	
    private String respTranStatus;
    
    private String errorMessage = "";
	private boolean errorRecord = false;
    
	private String offlineCenterId;
	private String onlineCenterId;
	private String password;
	private String writenscore;
	
    private ArrayList<String> releaseSubjects = new ArrayList<>();
    private ArrayList<String> selectedCenters = new ArrayList<>();
    private ArrayList<String> applicableSubjects = new ArrayList<>();
    
    private String center;
    
    
    public String getFilePath() {
		return filePath;
	}
	public void setFilePath(String filePath) {
		this.filePath = filePath;
	}
	public String getCenter() {
		return center;
	}
	public void setCenter(String center) {
		this.center = center;
	}
	public ArrayList<String> getApplicableSubjects() {
		return applicableSubjects;
	}
	public void setApplicableSubjects(ArrayList<String> applicableSubjects) {
		this.applicableSubjects = applicableSubjects;
	}
	public String getHtDownloaded() {
		return htDownloaded;
	}
	public void setHtDownloaded(String htDownloaded) {
		this.htDownloaded = htDownloaded;
	}
	public String getWritenscore() {
		return writenscore;
	}
	public void setWritenscore(String writenscore) {
		this.writenscore = writenscore;
	}
	public ArrayList<String> getSelectedCenters() {
		return selectedCenters;
	}
	public void setSelectedCenters(ArrayList<String> selectedCenters) {
		this.selectedCenters = selectedCenters;
	}
	public String getPassword() {
		return password;
	}
	public void setPassword(String password) {
		this.password = password;
	}
	public String getOfflineCenterId() {
		return offlineCenterId;
	}
	public void setOfflineCenterId(String offlineCenterId) {
		this.offlineCenterId = offlineCenterId;
	}
	public String getOnlineCenterId() {
		return onlineCenterId;
	}
	public void setOnlineCenterId(String onlineCenterId) {
		this.onlineCenterId = onlineCenterId;
	}
	public String getAddress() {
		return address;
	}
	public void setAddress(String address) {
		this.address = address;
	}
	public String getBookingCompleteTime() {
		return bookingCompleteTime;
	}
	public void setBookingCompleteTime(String bookingCompleteTime) {
		this.bookingCompleteTime = bookingCompleteTime;
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
	public String getRespTranStatus() {
		return respTranStatus;
	}
	public void setRespTranStatus(String respTranStatus) {
		this.respTranStatus = respTranStatus;
	}
	public ArrayList<String> getReleaseSubjects() {
		return releaseSubjects;
	}
	public void setReleaseSubjects(ArrayList<String> releaseSubjects) {
		this.releaseSubjects = releaseSubjects;
	}
	public String getSubjectCount() {
		return subjectCount;
	}
	public void setSubjectCount(String subjectCount) {
		this.subjectCount = subjectCount;
	}
	public String getDdDate() {
		return ddDate;
	}
	public void setDdDate(String ddDate) {
		this.ddDate = ddDate;
	}
	public String getDdReason() {
		return ddReason;
	}
	public void setDdReason(String ddReason) {
		this.ddReason = ddReason;
	}
	public String getLastName() {
		return lastName;
	}
	public void setLastName(String lastName) {
		this.lastName = lastName;
	}
	public String getFirstName() {
		return firstName;
	}
	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}
	public String getEmailId() {
		return emailId;
	}
	public void setEmailId(String emailId) {
		this.emailId = emailId;
	}
	public String getMobile() {
		return mobile;
	}
	public void setMobile(String mobile) {
		this.mobile = mobile;
	}
	public String getAltPhone() {
		return altPhone;
	}
	public void setAltPhone(String altPhone) {
		this.altPhone = altPhone;
	}
	public String getExamEndTime() {
		return examEndTime;
	}
	public void setExamEndTime(String examEndTime) {
		this.examEndTime = examEndTime;
	}
	public String getExamCenterName() {
		return examCenterName;
	}
	public void setExamCenterName(String examCenterName) {
		this.examCenterName = examCenterName;
	}
	public String getCity() {
		return city;
	}
	public void setCity(String city) {
		this.city = city;
	}
	public String getErrorCode() {
		return errorCode;
	}
	public void setErrorCode(String errorCode) {
		this.errorCode = errorCode;
	}
	public String getRespMode() {
		return respMode;
	}
	public void setRespMode(String respMode) {
		this.respMode = respMode;
	}
	public String getTransactionType() {
		return transactionType;
	}
	public void setTransactionType(String transactionType) {
		this.transactionType = transactionType;
	}
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}
	public String getExamMode() {
		return examMode;
	}
	public void setExamMode(String examMode) {
		this.examMode = examMode;
	}
	public String getRespTranDateTime() {
		return respTranDateTime;
	}
	public void setRespTranDateTime(String respTranDateTime) {
		this.respTranDateTime = respTranDateTime;
	}
	public String getResponseMessage() {
		return responseMessage;
	}
	public void setResponseMessage(String responseMessage) {
		this.responseMessage = responseMessage;
	}
	public String getError() {
		return error;
	}
	public void setError(String error) {
		this.error = error;
	}
	public String getTransactionID() {
		return transactionID;
	}
	public void setTransactionID(String transactionID) {
		this.transactionID = transactionID;
	}
	public String getRequestID() {
		return requestID;
	}
	public void setRequestID(String requestID) {
		this.requestID = requestID;
	}
	public String getMerchantRefNo() {
		return merchantRefNo;
	}
	public void setMerchantRefNo(String merchantRefNo) {
		this.merchantRefNo = merchantRefNo;
	}
	public String getSecureHash() {
		return secureHash;
	}
	public void setSecureHash(String secureHash) {
		this.secureHash = secureHash;
	}
	public String getRespAmount() {
		return respAmount;
	}
	public void setRespAmount(String respAmount) {
		this.respAmount = respAmount;
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	public String getResponseCode() {
		return responseCode;
	}
	public void setResponseCode(String responseCode) {
		this.responseCode = responseCode;
	}
	public String getRespPaymentMethod() {
		return respPaymentMethod;
	}
	public void setRespPaymentMethod(String respPaymentMethod) {
		this.respPaymentMethod = respPaymentMethod;
	}
	public String getIsFlagged() {
		return isFlagged;
	}
	public void setIsFlagged(String isFlagged) {
		this.isFlagged = isFlagged;
	}
	public String getPaymentID() {
		return paymentID;
	}
	public void setPaymentID(String paymentID) {
		this.paymentID = paymentID;
	}
	public String getExamDate() {
		return examDate;
	}
	public void setExamDate(String examDate) {
		this.examDate = examDate;
	}
	public String getExamTime() {
		return examTime;
	}
	public void setExamTime(String examTime) {
		this.examTime = examTime;
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
	public String getCenterId() {
		return centerId;
	}
	public void setCenterId(String centerId) {
		this.centerId = centerId;
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
	public String getProgram() {
		return program;
	}
	public void setProgram(String program) {
		this.program = program;
	}
	public String getSem() {
		return sem;
	}
	public void setSem(String sem) {
		this.sem = sem;
	}
	public String getTrackId() {
		return trackId;
	}
	public void setTrackId(String trackId) {
		this.trackId = trackId;
	}
	public String getAmount() {
		return amount;
	}
	public void setAmount(String amount) {
		this.amount = amount;
	}
	public String getTranDateTime() {
		return tranDateTime;
	}
	public void setTranDateTime(String tranDateTime) {
		this.tranDateTime = tranDateTime;
	}
	public String getTranStatus() {
		return tranStatus;
	}
	public void setTranStatus(String tranStatus) {
		this.tranStatus = tranStatus;
	}
	public String getBooked() {
		return booked;
	}
	public void setBooked(String booked) {
		this.booked = booked;
	}
	public String getDdno() {
		return ddno;
	}
	public void setDdno(String ddno) {
		this.ddno = ddno;
	}
	public String getBank() {
		return bank;
	}
	public void setBank(String bank) {
		this.bank = bank;
	}
	public String getDdAmount() {
		return ddAmount;
	}
	public void setDdAmount(String ddAmount) {
		this.ddAmount = ddAmount;
	}
	public String getPaymentMode() {
		return paymentMode;
	}
	public void setPaymentMode(String paymentMode) {
		this.paymentMode = paymentMode;
	}
	public String getCenterCode() {
		return centerCode;
	}
	public void setCenterCode(String centerCode) {
		this.centerCode = centerCode;
	}
	public String getCenterName() {
		return centerName;
	}
	public void setCenterName(String centerName) {
		this.centerName = centerName;
	}
    
    
    

}
