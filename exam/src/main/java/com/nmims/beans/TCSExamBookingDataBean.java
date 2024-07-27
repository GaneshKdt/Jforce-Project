package com.nmims.beans;

import java.io.Serializable;
import java.util.ArrayList;

public class TCSExamBookingDataBean extends BaseExamBean implements Serializable{

	private String id;
	private String sapid;
	private String subject;
	private String centerId;
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
	
	private String enrollmentMonth;
	private String enrollmentYear;
	private String prgmStructApplicable;
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
	private String lc;
	private String ic;

	private String respTranStatus;

	private String errorMessage = "";
	private boolean errorRecord = false;

	private String offlineCenterId;
	private String onlineCenterId;
	private String password;
	private String writenscore;

	private ArrayList<String> releaseSubjects = new ArrayList<>();
	private ArrayList<String> selectedCenters = new ArrayList<>();

	private String uniqueKey;
	private String bookedCount;
	private String filePath;
	private String action;
	private String changeOfCenter;
	

	private String programStructApplicable;
	private String studentType;
	private Long testId;
	private Long testAttempt;
	
	private String validityEndYear;
	private String validityEndMonth;
	private String isLateral;
	
	private String day;
	
	
	private String paymentOption;
	private String bankName;
	
	private String requestAmount;
	private String requestTrackId;
	
	
	private String timeboundId;
	
	private String subjectId;
	
	private String sifySubjectCode;

	private String syncExamCenterProvider;
	
	
	
	public String getSyncExamCenterProvider() {
		return syncExamCenterProvider;
	}
	public void setSyncExamCenterProvider(String syncExamCenterProvider) {
		this.syncExamCenterProvider = syncExamCenterProvider;
	}
	public String getRequestAmount() {
		return requestAmount;
	}
	public void setRequestAmount(String requestAmount) {
		this.requestAmount = requestAmount;
	}
	public String getRequestTrackId() {
		return requestTrackId;
	}
	public void setRequestTrackId(String requestTrackId) {
		this.requestTrackId = requestTrackId;
	}
	public String getPaymentOption() {
		return paymentOption;
	}
	public void setPaymentOption(String paymentOption) {
		this.paymentOption = paymentOption;
	}
	public String getBankName() {
		return bankName;
	}
	public void setBankName(String bankName) {
		this.bankName = bankName;
	}
	public String getTimeboundId() {
		return timeboundId;
	}
	public void setTimeboundId(String timeboundId) {
		this.timeboundId = timeboundId;
	}
	public String getValidityEndYear() {
		return validityEndYear;
	}
	public void setValidityEndYear(String validityEndYear) {
		this.validityEndYear = validityEndYear;
	}
	public String getValidityEndMonth() {
		return validityEndMonth;
	}
	public void setValidityEndMonth(String validityEndMonth) {
		this.validityEndMonth = validityEndMonth;
	}
	

	public Long getTestId() {
		return testId;
	}
	public void setTestId(Long testId) {
		this.testId = testId;
	}
	
	/**
	 * @return the testAttempt
	 */
	public Long getTestAttempt() {
		return testAttempt;
	}
	/**
	 * @param testAttempt the testAttempt to set
	 */
	public void setTestAttempt(Long testAttempt) {
		this.testAttempt = testAttempt;
	}
	public String getProgramStructApplicable() {
		return programStructApplicable;
	}
	public void setProgramStructApplicable(String programStructApplicable) {
		this.programStructApplicable = programStructApplicable;
	}
	public String getLc() {
		return lc;
	}
	public void setLc(String lc) {
		this.lc = lc;
	}
	public String getIc() {
		return ic;
	}
	public void setIc(String ic) {
		this.ic = ic;
	}
	public String getChangeOfCenter() {
		return changeOfCenter;
	}
	public void setChangeOfCenter(String changeOfCenter) {
		this.changeOfCenter = changeOfCenter;
	}



	public String getEnrollmentMonth() {
		return enrollmentMonth;
	}
	public void setEnrollmentMonth(String enrollmentMonth) {
		this.enrollmentMonth = enrollmentMonth;
	}
	public String getEnrollmentYear() {
		return enrollmentYear;
	}
	public void setEnrollmentYear(String enrollmentYear) {
		this.enrollmentYear = enrollmentYear;
	}
	public String getPrgmStructApplicable() {
		return prgmStructApplicable;
	}
	public void setPrgmStructApplicable(String prgmStructApplicable) {
		this.prgmStructApplicable = prgmStructApplicable;
	}
	public String getAction() {
		return action;
	}
	public void setAction(String action) {
		this.action = action;
	}
	public String getFilePath() {
		return filePath;
	}
	public void setFilePath(String filePath) {
		this.filePath = filePath;
	}
	public String getUniqueKey() {
		return uniqueKey;
	}
	public void setUniqueKey(String uniqueKey) {
		this.uniqueKey = uniqueKey;
	}
	public String getBookedCount() {
		return bookedCount;
	}
	public void setBookedCount(String bookedCount) {
		this.bookedCount = bookedCount;
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
	
//	@Override
//	public String toString() {
//		return "ExamBookingTransactionBean [sapid=" + sapid + ", subject="
//				+ subject + ", centerId=" + centerId + ", program=" + program + ", sem=" + sem + ", trackId=" + trackId
//				+ ", amount=" + amount + ", tranDateTime=" + tranDateTime + ", bookingCompleteTime="
//				+ bookingCompleteTime + ", tranStatus=" + tranStatus + ", booked=" + booked + ", ddno=" + ddno
//				+ ", bank=" + bank + ", ddAmount=" + ddAmount + ", paymentMode=" + paymentMode + ", examDate="
//				+ examDate + ", examTime=" + examTime + ", examMode=" + examMode + ", examEndTime=" + examEndTime
//				+ ", enrollmentMonth=" + enrollmentMonth + ", enrollmentYear=" + enrollmentYear
//				+ ", prgmStructApplicable=" + prgmStructApplicable + ", transactionID=" + transactionID + ", requestID="
//				+ requestID + ", merchantRefNo=" + merchantRefNo + ", secureHash=" + secureHash + ", respAmount="
//				+ respAmount + ", description=" + description + ", responseCode=" + responseCode
//				+ ", respPaymentMethod=" + respPaymentMethod + ", isFlagged=" + isFlagged + ", paymentID=" + paymentID
//				+ ", responseMessage=" + responseMessage + ", error=" + error + ", respTranDateTime=" + respTranDateTime
//				+ ", htDownloaded=" + htDownloaded + ", transactionType=" + transactionType + ", status=" + status
//				+ ", respMode=" + respMode + ", errorCode=" + errorCode + ", examCenterName=" + examCenterName
//				+ ", city=" + city + ", address=" + address + ", lastName=" + lastName + ", firstName=" + firstName
//				+ ", emailId=" + emailId + ", mobile=" + mobile + ", altPhone=" + altPhone + ", ddDate=" + ddDate
//				+ ", ddReason=" + ddReason + ", subjectCount=" + subjectCount + ", centerCode=" + centerCode
//				+ ", centerName=" + centerName + ", lc=" + lc + ", ic=" + ic + ", respTranStatus=" + respTranStatus
//				+ ", errorMessage=" + errorMessage + ", errorRecord=" + errorRecord + ", offlineCenterId="
//				+ offlineCenterId + ", onlineCenterId=" + onlineCenterId + ", password=" + password + ", writenscore="
//				+ writenscore + ", releaseSubjects=" + releaseSubjects + ", selectedCenters=" + selectedCenters
//				+ ", uniqueKey=" + uniqueKey + ", bookedCount=" + bookedCount + ", filePath=" + filePath + ", action="
//				+ action + ", changeOfCenter=" + changeOfCenter + ", programStructApplicable=" + programStructApplicable
//				+ ", studentType=" + studentType + ", testId=" + testId + ", testAttempt=" + testAttempt
//				+ ", validityEndYear=" + validityEndYear + ", validityEndMonth=" + validityEndMonth + ", isLateral="
//				+ isLateral + ", day=" + day + ", paymentOption=" + paymentOption + ", bankName=" + bankName + "]";
//	}
	public String getStudentType() {
		return studentType;
	}
	public void setStudentType(String studentType) {
		this.studentType = studentType;
	}
	public String getIsLateral() {
		return isLateral;
	}
	public void setIsLateral(String isLateral) {
		this.isLateral = isLateral;
	}
	public String getDay() {
		return day;
	}
	public void setDay(String day) {
		this.day = day;
	}
	
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	
	
	@Override
	public String toString() {
		return "TCSExamBookingDataBean [sapid=" + sapid + ", subject="
				+ subject + ", centerId=" + centerId + ", program=" + program + ", sem=" + sem + ", booked=" + booked + ", ddno=" + ddno
				+ ", examCenterName=" + examCenterName
				+ ", lastName=" + lastName + ", firstName=" + firstName
				+ ", centerCode=" + centerCode
				+ ", password=" + password 
				+ ", subject code=" + subject
				+ ", subject id=" + id
				+ ", Exam Date=" + examDate
				+ ", Exam Time=" + examTime
				
				+ "]";
	}
	public String getSubjectId() {
		return subjectId;
	}
	public void setSubjectId(String subjectId) {
		this.subjectId = subjectId;
	}
	public String getSifySubjectCode() {
		return sifySubjectCode;
	}
	public void setSifySubjectCode(String sifySubjectCode) {
		this.sifySubjectCode = sifySubjectCode;
	}




}
