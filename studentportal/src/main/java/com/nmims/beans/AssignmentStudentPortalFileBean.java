package com.nmims.beans;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import org.springframework.web.multipart.commons.CommonsMultipartFile;

/**
 * old name - AssignmentFileBean
 * @author others and vil_m
 *
 */
public class AssignmentStudentPortalFileBean  implements Serializable {
	
	private String year;
	private String month;
	private CommonsMultipartFile fileData;
	private String subject;
	private String sem;
	private String startDate;
	private String endDate;
	private String instructions;
	private String filePath;
	private String studentFilePath;
	private String createdBy;
	private String createdDate;
	private String lastModifiedBy;
	private String lastModifiedDate;
	private String sapId;
	private String previewPath;
	private String questionFilePreviewPath;
	private String attempts;
	private String status;
	private String attemptsLeft;
	
	private String errorMessage = "";
	private boolean errorRecord = false;
	
	
	
	private String lastName;
    private String firstName;
    private String program;
    private String enrollmentMonth;
    private String enrollmentYear;
    private String emailId;
    private String mobile;
    private String altPhone;
    private String centerCode;
    private String centerName;
    private String validityEndMonth;
    private String validityEndYear;
    private String prgmStructApplicable;
    private String lc;
	private String facultyId;
    private String evaluated;
    private String remarks;
    private String score;
    private String evaluationCount;
    
    private String sfName;
    private String slName;
    private String reason;
    private ArrayList<String> faculties = new ArrayList<>();
    //private List<FacultyBean> faculties = new ArrayList<FacultyBean>();
    private ArrayList<String> numberOfAssignments = new ArrayList<>();
    private ArrayList<String> indexes = new ArrayList<>();
    
    
    private ArrayList<AssignmentStudentPortalFileBean> revalAssignments = new ArrayList<>();
    
    private String level;
    
	private String q1Marks;
	private String q1Remarks;
	private String q2Marks;
	private String q3Marks;
	private String q4Marks;
	private String q5Marks;
	private String q6Marks;
	private String q7Marks;
	private String q8Marks;
	private String q9Marks;
	private String q10Marks;
	private String q2Remarks;
	private String q3Remarks;
	private String q4Remarks;
	private String q5Remarks;
	private String q6Remarks;
	private String q7Remarks;
	private String q8Remarks;
	private String q9Remarks;
	private String q10Remarks;
	
	private String q1RevalMarks;
	private String q1RevalRemarks;
	private String q2RevalMarks;
	private String q3RevalMarks;
	private String q4RevalMarks;
	private String q5RevalMarks;
	private String q6RevalMarks;
	private String q7RevalMarks;
	private String q8RevalMarks;
	private String q9RevalMarks;
	private String q10RevalMarks;
	private String q2RevalRemarks;
	private String q3RevalRemarks;
	private String q4RevalRemarks;
	private String q5RevalRemarks;
	private String q6RevalRemarks;
	private String q7RevalRemarks;
	private String q8RevalRemarks;
	private String q9RevalRemarks;

	private String consumerTypeId;
	private String programStructureId;
	private String programId;
	
	public String getConsumerTypeId() {
		return consumerTypeId;
	}
	public void setConsumerTypeId(String consumerTypeId) {
		this.consumerTypeId = consumerTypeId;
	}
	public String getProgramStructureId() {
		return programStructureId;
	}
	public void setProgramStructureId(String programStructureId) {
		this.programStructureId = programStructureId;
	}
	public String getProgramId() {
		return programId;
	}
	public void setProgramId(String programId) {
		this.programId = programId;
	}


	private String currentSemSubject;
	
	public String getCurrentSemSubject() {
			return currentSemSubject;
	}
	public void setCurrentSemSubject(String currentSemSubject) {
			this.currentSemSubject = currentSemSubject;
	}
	public ArrayList<AssignmentStudentPortalFileBean> getRevalAssignments() {

		return revalAssignments;
	}
	public void setRevalAssignments(ArrayList<AssignmentStudentPortalFileBean> revalAssignments) {
		this.revalAssignments = revalAssignments;
	}
	public String getLevel() {
		return level;
	}
	public void setLevel(String level) {
		this.level = level;
	}
	public String getQ1Marks() {
		return q1Marks;
	}
	public void setQ1Marks(String q1Marks) {
		this.q1Marks = q1Marks;
	}
	public String getQ1Remarks() {
		return q1Remarks;
	}
	public void setQ1Remarks(String q1Remarks) {
		this.q1Remarks = q1Remarks;
	}
	public String getQ2Marks() {
		return q2Marks;
	}
	public void setQ2Marks(String q2Marks) {
		this.q2Marks = q2Marks;
	}
	public String getQ3Marks() {
		return q3Marks;
	}
	public void setQ3Marks(String q3Marks) {
		this.q3Marks = q3Marks;
	}
	public String getQ4Marks() {
		return q4Marks;
	}
	public void setQ4Marks(String q4Marks) {
		this.q4Marks = q4Marks;
	}
	public String getQ5Marks() {
		return q5Marks;
	}
	public void setQ5Marks(String q5Marks) {
		this.q5Marks = q5Marks;
	}
	public String getQ6Marks() {
		return q6Marks;
	}
	public void setQ6Marks(String q6Marks) {
		this.q6Marks = q6Marks;
	}
	public String getQ7Marks() {
		return q7Marks;
	}
	public void setQ7Marks(String q7Marks) {
		this.q7Marks = q7Marks;
	}
	public String getQ8Marks() {
		return q8Marks;
	}
	public void setQ8Marks(String q8Marks) {
		this.q8Marks = q8Marks;
	}
	public String getQ9Marks() {
		return q9Marks;
	}
	public void setQ9Marks(String q9Marks) {
		this.q9Marks = q9Marks;
	}
	public String getQ10Marks() {
		return q10Marks;
	}
	public void setQ10Marks(String q10Marks) {
		this.q10Marks = q10Marks;
	}
	public String getQ2Remarks() {
		return q2Remarks;
	}
	public void setQ2Remarks(String q2Remarks) {
		this.q2Remarks = q2Remarks;
	}
	public String getQ3Remarks() {
		return q3Remarks;
	}
	public void setQ3Remarks(String q3Remarks) {
		this.q3Remarks = q3Remarks;
	}
	public String getQ4Remarks() {
		return q4Remarks;
	}
	public void setQ4Remarks(String q4Remarks) {
		this.q4Remarks = q4Remarks;
	}
	public String getQ5Remarks() {
		return q5Remarks;
	}
	public void setQ5Remarks(String q5Remarks) {
		this.q5Remarks = q5Remarks;
	}
	public String getQ6Remarks() {
		return q6Remarks;
	}
	public void setQ6Remarks(String q6Remarks) {
		this.q6Remarks = q6Remarks;
	}
	public String getQ7Remarks() {
		return q7Remarks;
	}
	public void setQ7Remarks(String q7Remarks) {
		this.q7Remarks = q7Remarks;
	}
	public String getQ8Remarks() {
		return q8Remarks;
	}
	public void setQ8Remarks(String q8Remarks) {
		this.q8Remarks = q8Remarks;
	}
	public String getQ9Remarks() {
		return q9Remarks;
	}
	public void setQ9Remarks(String q9Remarks) {
		this.q9Remarks = q9Remarks;
	}
	public String getQ10Remarks() {
		return q10Remarks;
	}
	public void setQ10Remarks(String q10Remarks) {
		this.q10Remarks = q10Remarks;
	}
	public String getQ1RevalMarks() {
		return q1RevalMarks;
	}
	public void setQ1RevalMarks(String q1RevalMarks) {
		this.q1RevalMarks = q1RevalMarks;
	}
	public String getQ1RevalRemarks() {
		return q1RevalRemarks;
	}
	public void setQ1RevalRemarks(String q1RevalRemarks) {
		this.q1RevalRemarks = q1RevalRemarks;
	}
	public String getQ2RevalMarks() {
		return q2RevalMarks;
	}
	public void setQ2RevalMarks(String q2RevalMarks) {
		this.q2RevalMarks = q2RevalMarks;
	}
	public String getQ3RevalMarks() {
		return q3RevalMarks;
	}
	public void setQ3RevalMarks(String q3RevalMarks) {
		this.q3RevalMarks = q3RevalMarks;
	}
	public String getQ4RevalMarks() {
		return q4RevalMarks;
	}
	public void setQ4RevalMarks(String q4RevalMarks) {
		this.q4RevalMarks = q4RevalMarks;
	}
	public String getQ5RevalMarks() {
		return q5RevalMarks;
	}
	public void setQ5RevalMarks(String q5RevalMarks) {
		this.q5RevalMarks = q5RevalMarks;
	}
	public String getQ6RevalMarks() {
		return q6RevalMarks;
	}
	public void setQ6RevalMarks(String q6RevalMarks) {
		this.q6RevalMarks = q6RevalMarks;
	}
	public String getQ7RevalMarks() {
		return q7RevalMarks;
	}
	public void setQ7RevalMarks(String q7RevalMarks) {
		this.q7RevalMarks = q7RevalMarks;
	}
	public String getQ8RevalMarks() {
		return q8RevalMarks;
	}
	public void setQ8RevalMarks(String q8RevalMarks) {
		this.q8RevalMarks = q8RevalMarks;
	}
	public String getQ9RevalMarks() {
		return q9RevalMarks;
	}
	public void setQ9RevalMarks(String q9RevalMarks) {
		this.q9RevalMarks = q9RevalMarks;
	}
	public String getQ10RevalMarks() {
		return q10RevalMarks;
	}
	public void setQ10RevalMarks(String q10RevalMarks) {
		this.q10RevalMarks = q10RevalMarks;
	}
	public String getQ2RevalRemarks() {
		return q2RevalRemarks;
	}
	public void setQ2RevalRemarks(String q2RevalRemarks) {
		this.q2RevalRemarks = q2RevalRemarks;
	}
	public String getQ3RevalRemarks() {
		return q3RevalRemarks;
	}
	public void setQ3RevalRemarks(String q3RevalRemarks) {
		this.q3RevalRemarks = q3RevalRemarks;
	}
	public String getQ4RevalRemarks() {
		return q4RevalRemarks;
	}
	public void setQ4RevalRemarks(String q4RevalRemarks) {
		this.q4RevalRemarks = q4RevalRemarks;
	}
	public String getQ5RevalRemarks() {
		return q5RevalRemarks;
	}
	public void setQ5RevalRemarks(String q5RevalRemarks) {
		this.q5RevalRemarks = q5RevalRemarks;
	}
	public String getQ6RevalRemarks() {
		return q6RevalRemarks;
	}
	public void setQ6RevalRemarks(String q6RevalRemarks) {
		this.q6RevalRemarks = q6RevalRemarks;
	}
	public String getQ7RevalRemarks() {
		return q7RevalRemarks;
	}
	public void setQ7RevalRemarks(String q7RevalRemarks) {
		this.q7RevalRemarks = q7RevalRemarks;
	}
	public String getQ8RevalRemarks() {
		return q8RevalRemarks;
	}
	public void setQ8RevalRemarks(String q8RevalRemarks) {
		this.q8RevalRemarks = q8RevalRemarks;
	}
	public String getQ9RevalRemarks() {
		return q9RevalRemarks;
	}
	public void setQ9RevalRemarks(String q9RevalRemarks) {
		this.q9RevalRemarks = q9RevalRemarks;
	}
	public String getQ10RevalRemarks() {
		return q10RevalRemarks;
	}
	public void setQ10RevalRemarks(String q10RevalRemarks) {
		this.q10RevalRemarks = q10RevalRemarks;
	}
	public String getEvaluationDate() {
		return evaluationDate;
	}
	public void setEvaluationDate(String evaluationDate) {
		this.evaluationDate = evaluationDate;
	}
	public String getFaculty2Score() {
		return faculty2Score;
	}
	public void setFaculty2Score(String faculty2Score) {
		this.faculty2Score = faculty2Score;
	}
	public String getFaculty2Remarks() {
		return faculty2Remarks;
	}
	public void setFaculty2Remarks(String faculty2Remarks) {
		this.faculty2Remarks = faculty2Remarks;
	}
	public String getFaculty2Reason() {
		return faculty2Reason;
	}
	public void setFaculty2Reason(String faculty2Reason) {
		this.faculty2Reason = faculty2Reason;
	}
	public String getFaculty2EvaluationCount() {
		return faculty2EvaluationCount;
	}
	public void setFaculty2EvaluationCount(String faculty2EvaluationCount) {
		this.faculty2EvaluationCount = faculty2EvaluationCount;
	}
	public String getFaculty2EvaluationDate() {
		return faculty2EvaluationDate;
	}
	public void setFaculty2EvaluationDate(String faculty2EvaluationDate) {
		this.faculty2EvaluationDate = faculty2EvaluationDate;
	}
	public String getFaculty2Evaluated() {
		return faculty2Evaluated;
	}
	public void setFaculty2Evaluated(String faculty2Evaluated) {
		this.faculty2Evaluated = faculty2Evaluated;
	}
	public String getFaculty3Score() {
		return faculty3Score;
	}
	public void setFaculty3Score(String faculty3Score) {
		this.faculty3Score = faculty3Score;
	}
	public String getFaculty3Remarks() {
		return faculty3Remarks;
	}
	public void setFaculty3Remarks(String faculty3Remarks) {
		this.faculty3Remarks = faculty3Remarks;
	}
	public String getFaculty3Reason() {
		return faculty3Reason;
	}
	public void setFaculty3Reason(String faculty3Reason) {
		this.faculty3Reason = faculty3Reason;
	}
	public String getFaculty3EvaluationCount() {
		return faculty3EvaluationCount;
	}
	public void setFaculty3EvaluationCount(String faculty3EvaluationCount) {
		this.faculty3EvaluationCount = faculty3EvaluationCount;
	}
	public String getFaculty3EvaluationDate() {
		return faculty3EvaluationDate;
	}
	public void setFaculty3EvaluationDate(String faculty3EvaluationDate) {
		this.faculty3EvaluationDate = faculty3EvaluationDate;
	}
	public String getFaculty3Evaluated() {
		return faculty3Evaluated;
	}
	public void setFaculty3Evaluated(String faculty3Evaluated) {
		this.faculty3Evaluated = faculty3Evaluated;
	}
	public int getMinMatchPercent() {
		return minMatchPercent;
	}
	public void setMinMatchPercent(int minMatchPercent) {
		this.minMatchPercent = minMatchPercent;
	}
	public String getFinalScore() {
		return finalScore;
	}
	public void setFinalScore(String finalScore) {
		this.finalScore = finalScore;
	}
	public String getFinalReason() {
		return finalReason;
	}
	public void setFinalReason(String finalReason) {
		this.finalReason = finalReason;
	}
	public String getFinalRemarks() {
		return finalRemarks;
	}
	public void setFinalRemarks(String finalRemarks) {
		this.finalRemarks = finalRemarks;
	}
	public String getToBeEvaluated() {
		return toBeEvaluated;
	}
	public void setToBeEvaluated(String toBeEvaluated) {
		this.toBeEvaluated = toBeEvaluated;
	}
	public String getSapIdList() {
		return sapIdList;
	}
	public void setSapIdList(String sapIdList) {
		this.sapIdList = sapIdList;
	}
	public String getPercentDifference() {
		return percentDifference;
	}
	public void setPercentDifference(String percentDifference) {
		this.percentDifference = percentDifference;
	}
	public String getMean() {
		return mean;
	}
	public void setMean(String mean) {
		this.mean = mean;
	}
	public String getStddev() {
		return stddev;
	}
	public void setStddev(String stddev) {
		this.stddev = stddev;
	}
	public String getNormalizedScore() {
		return normalizedScore;
	}
	public void setNormalizedScore(String normalizedScore) {
		this.normalizedScore = normalizedScore;
	}
	public String getRoundedNormalizedScore() {
		return roundedNormalizedScore;
	}
	public void setRoundedNormalizedScore(String roundedNormalizedScore) {
		this.roundedNormalizedScore = roundedNormalizedScore;
	}
	public String getPopulationCount() {
		return populationCount;
	}
	public void setPopulationCount(String populationCount) {
		this.populationCount = populationCount;
	}
	public String getWeightedMean() {
		return weightedMean;
	}
	public void setWeightedMean(String weightedMean) {
		this.weightedMean = weightedMean;
	}
	public String getWeightedstddev() {
		return weightedstddev;
	}
	public void setWeightedstddev(String weightedstddev) {
		this.weightedstddev = weightedstddev;
	}
	public String getOfflineAssignmentLive() {
		return offlineAssignmentLive;
	}
	public void setOfflineAssignmentLive(String offlineAssignmentLive) {
		this.offlineAssignmentLive = offlineAssignmentLive;
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
	public String getBookingCompleteTime() {
		return bookingCompleteTime;
	}
	public void setBookingCompleteTime(String bookingCompleteTime) {
		this.bookingCompleteTime = bookingCompleteTime;
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
	public String getRespTranDateTime() {
		return respTranDateTime;
	}
	public void setRespTranDateTime(String respTranDateTime) {
		this.respTranDateTime = respTranDateTime;
	}
	public String getPaymentApplicable() {
		return paymentApplicable;
	}
	public void setPaymentApplicable(String paymentApplicable) {
		this.paymentApplicable = paymentApplicable;
	}
	public String getPaymentDone() {
		return paymentDone;
	}
	public void setPaymentDone(String paymentDone) {
		this.paymentDone = paymentDone;
	}
	public String getAssignmentscore() {
		return assignmentscore;
	}
	public void setAssignmentscore(String assignmentscore) {
		this.assignmentscore = assignmentscore;
	}
	public boolean isSubmissionAllowed() {
		return submissionAllowed;
	}
	public void setSubmissionAllowed(boolean submissionAllowed) {
		this.submissionAllowed = submissionAllowed;
	}
	public String getEvaluatedFaculty() {
		return evaluatedFaculty;
	}
	public void setEvaluatedFaculty(String evaluatedFaculty) {
		this.evaluatedFaculty = evaluatedFaculty;
	}
	private String q10RevalRemarks;
	
	private String evaluationDate;
	
	private String faculty2Score;
	private String faculty2Remarks;
	private String faculty2Reason;
	private String faculty2EvaluationCount;
	private String faculty2EvaluationDate;
	private String faculty2Evaluated;
	
	private String faculty3Score;
	private String faculty3Remarks;
	private String faculty3Reason;
	private String faculty3EvaluationCount;
	private String faculty3EvaluationDate;
	private String faculty3Evaluated;
	
    private int minMatchPercent;
    private String finalScore;
	private String finalReason;
	private String finalRemarks;
    private String toBeEvaluated;
	private String sapIdList;
    private String percentDifference;
	
	private String mean;
	private String stddev;
    private String normalizedScore;
    private String roundedNormalizedScore;
	private String populationCount;
	private String weightedMean;
	private String weightedstddev;
    
	private String offlineAssignmentLive;
	
	
	private String trackId;
    private String amount;
    private String tranDateTime;
    private String bookingCompleteTime;
    private String tranStatus;
    private String booked;
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
	
    private String paymentApplicable;
    private String paymentDone;
    private String assignmentscore;
    private boolean submissionAllowed = true; 
    
    private String evaluatedFaculty;
	
	public String getReason() {
		return reason;
	}
	public void setReason(String reason) {
		this.reason = reason;
	}
	public String getSfName() {
		return sfName;
	}
	public void setSfName(String sfName) {
		this.sfName = sfName;
	}
	public String getSlName() {
		return slName;
	}
	public void setSlName(String slName) {
		this.slName = slName;
	}
	public String getEvaluationCount() {
		return evaluationCount;
	}
	public void setEvaluationCount(String evaluationCount) {
		this.evaluationCount = evaluationCount;
	}
	public ArrayList<String> getIndexes() {
		return indexes;
	}
	public void setIndexes(ArrayList<String> indexes) {
		this.indexes = indexes;
	}
	public ArrayList<String> getNumberOfAssignments() {
		return numberOfAssignments;
	}
	public void setNumberOfAssignments(ArrayList<String> numberOfAssignments) {
		this.numberOfAssignments = numberOfAssignments;
	}
	public ArrayList<String> getFaculties() {
		return faculties;
	}
	public void setFaculties(ArrayList<String> faculties) {
		this.faculties = faculties;
	}
	public String getScore() {
		return score;
	}
	public void setScore(String score) {
		this.score = score;
	}
	public String getRemarks() {
		return remarks;
	}
	public void setRemarks(String remarks) {
		this.remarks = remarks;
	}
	public String getEvaluated() {
		/*if(evaluated == null || "".equals(evaluated)){
			return "N";
		}*/
		return evaluated;
	}
	public void setEvaluated(String evaluated) {
		this.evaluated = evaluated;
	}
	public String getFacultyId() {
		return facultyId;
	}
	public void setFacultyId(String facultyId) {
		this.facultyId = facultyId;
	}
	
	public String getLc() {
		return lc;
	}
	public void setLc(String lc) {
		this.lc = lc;
	}
	public String getPrgmStructApplicable() {
		return prgmStructApplicable;
	}
	public void setPrgmStructApplicable(String prgmStructApplicable) {
		this.prgmStructApplicable = prgmStructApplicable;
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
	public String getProgram() {
		return program;
	}
	public void setProgram(String program) {
		this.program = program;
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
	public String getValidityEndMonth() {
		return validityEndMonth;
	}
	public void setValidityEndMonth(String validityEndMonth) {
		this.validityEndMonth = validityEndMonth;
	}
	public String getValidityEndYear() {
		return validityEndYear;
	}
	public void setValidityEndYear(String validityEndYear) {
		this.validityEndYear = validityEndYear;
	}
	public String getQuestionFilePreviewPath() {
		return questionFilePreviewPath;
	}
	public void setQuestionFilePreviewPath(String questionFilePreviewPath) {
		this.questionFilePreviewPath = questionFilePreviewPath;
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
	public String getSem() {
		return sem;
	}
	public void setSem(String sem) {
		this.sem = sem;
	}
	public String getAttemptsLeft() {
		return attemptsLeft;
	}
	public void setAttemptsLeft(String attemptsLeft) {
		this.attemptsLeft = attemptsLeft;
	}
	public String getAttempts() {
		return attempts;
	}
	public void setAttempts(String attempts) {
		this.attempts = attempts;
	}
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}
	public String getPreviewPath() {
		return previewPath;
	}
	public void setPreviewPath(String previewPath) {
		this.previewPath = previewPath;
	}
	public String getStudentFilePath() {
		return studentFilePath;
	}
	public void setStudentFilePath(String studentFilePath) {
		this.studentFilePath = studentFilePath;
	}
	public String getSapId() {
		return sapId;
	}
	public void setSapId(String sapId) {
		this.sapId = sapId;
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
	public String getCreatedBy() {
		return createdBy;
	}
	public void setCreatedBy(String createdBy) {
		this.createdBy = createdBy;
	}
	public String getCreatedDate() {
		return createdDate;
	}
	public void setCreatedDate(String createdDate) {
		this.createdDate = createdDate;
	}
	public String getLastModifiedBy() {
		return lastModifiedBy;
	}
	public void setLastModifiedBy(String lastModifiedBy) {
		this.lastModifiedBy = lastModifiedBy;
	}
	public String getLastModifiedDate() {
		return lastModifiedDate;
	}
	public void setLastModifiedDate(String lastModifiedDate) {
		this.lastModifiedDate = lastModifiedDate;
	}
	public String getFilePath() {
		return filePath;
	}
	public void setFilePath(String filePath) {
		this.filePath = filePath;
	}
	public CommonsMultipartFile getFileData() {
		return fileData;
	}
	public void setFileData(CommonsMultipartFile fileData) {
		this.fileData = fileData;
	}
	public String getSubject() {
		return subject;
	}
	public void setSubject(String subject) {
		this.subject = subject;
	}
	public String getStartDate() {
		return startDate;
	}
	public void setStartDate(String startDate) {
		this.startDate = startDate;
	}
	public String getEndDate() {
		return endDate;
	}
	public void setEndDate(String endDate) {
		this.endDate = endDate;
	}
	public String getInstructions() {
		return instructions;
	}
	public void setInstructions(String instructions) {
		this.instructions = instructions;
	}
	@Override
	public String toString() {
		return "AssignmentFileBean [year=" + year + ", month=" + month + ", fileData=" + fileData + ", subject="
				+ subject + ", sem=" + sem + ", startDate=" + startDate + ", endDate=" + endDate + ", instructions="
				+ instructions + ", filePath=" + filePath + ", studentFilePath=" + studentFilePath + ", createdBy="
				+ createdBy + ", createdDate=" + createdDate + ", lastModifiedBy=" + lastModifiedBy
				+ ", lastModifiedDate=" + lastModifiedDate + ", sapId=" + sapId + ", previewPath=" + previewPath
				+ ", questionFilePreviewPath=" + questionFilePreviewPath + ", attempts=" + attempts + ", status="
				+ status + ", attemptsLeft=" + attemptsLeft + ", errorMessage=" + errorMessage + ", errorRecord="
				+ errorRecord + ", lastName=" + lastName + ", firstName=" + firstName + ", program=" + program
				+ ", enrollmentMonth=" + enrollmentMonth + ", enrollmentYear=" + enrollmentYear + ", emailId=" + emailId
				+ ", mobile=" + mobile + ", altPhone=" + altPhone + ", centerCode=" + centerCode + ", centerName="
				+ centerName + ", validityEndMonth=" + validityEndMonth + ", validityEndYear=" + validityEndYear
				+ ", prgmStructApplicable=" + prgmStructApplicable + ", lc=" + lc + ", facultyId=" + facultyId
				+ ", evaluated=" + evaluated + ", remarks=" + remarks + ", score=" + score + ", evaluationCount="
				+ evaluationCount + ", sfName=" + sfName + ", slName=" + slName + ", reason=" + reason + ", faculties="
				+ faculties + ", numberOfAssignments=" + numberOfAssignments + ", indexes=" + indexes
				+ ", revalAssignments=" + revalAssignments + ", level=" + level + ", q1Marks=" + q1Marks
				+ ", q1Remarks=" + q1Remarks + ", q2Marks=" + q2Marks + ", q3Marks=" + q3Marks + ", q4Marks=" + q4Marks
				+ ", q5Marks=" + q5Marks + ", q6Marks=" + q6Marks + ", q7Marks=" + q7Marks + ", q8Marks=" + q8Marks
				+ ", q9Marks=" + q9Marks + ", q10Marks=" + q10Marks + ", q2Remarks=" + q2Remarks + ", q3Remarks="
				+ q3Remarks + ", q4Remarks=" + q4Remarks + ", q5Remarks=" + q5Remarks + ", q6Remarks=" + q6Remarks
				+ ", q7Remarks=" + q7Remarks + ", q8Remarks=" + q8Remarks + ", q9Remarks=" + q9Remarks + ", q10Remarks="
				+ q10Remarks + ", q1RevalMarks=" + q1RevalMarks + ", q1RevalRemarks=" + q1RevalRemarks
				+ ", q2RevalMarks=" + q2RevalMarks + ", q3RevalMarks=" + q3RevalMarks + ", q4RevalMarks=" + q4RevalMarks
				+ ", q5RevalMarks=" + q5RevalMarks + ", q6RevalMarks=" + q6RevalMarks + ", q7RevalMarks=" + q7RevalMarks
				+ ", q8RevalMarks=" + q8RevalMarks + ", q9RevalMarks=" + q9RevalMarks + ", q10RevalMarks="
				+ q10RevalMarks + ", q2RevalRemarks=" + q2RevalRemarks + ", q3RevalRemarks=" + q3RevalRemarks
				+ ", q4RevalRemarks=" + q4RevalRemarks + ", q5RevalRemarks=" + q5RevalRemarks + ", q6RevalRemarks="
				+ q6RevalRemarks + ", q7RevalRemarks=" + q7RevalRemarks + ", q8RevalRemarks=" + q8RevalRemarks
				+ ", q9RevalRemarks=" + q9RevalRemarks + ", q10RevalRemarks=" + q10RevalRemarks + ", evaluationDate="
				+ evaluationDate + ", faculty2Score=" + faculty2Score + ", faculty2Remarks=" + faculty2Remarks
				+ ", faculty2Reason=" + faculty2Reason + ", faculty2EvaluationCount=" + faculty2EvaluationCount
				+ ", faculty2EvaluationDate=" + faculty2EvaluationDate + ", faculty2Evaluated=" + faculty2Evaluated
				+ ", faculty3Score=" + faculty3Score + ", faculty3Remarks=" + faculty3Remarks + ", faculty3Reason="
				+ faculty3Reason + ", faculty3EvaluationCount=" + faculty3EvaluationCount + ", faculty3EvaluationDate="
				+ faculty3EvaluationDate + ", faculty3Evaluated=" + faculty3Evaluated + ", minMatchPercent="
				+ minMatchPercent + ", finalScore=" + finalScore + ", finalReason=" + finalReason + ", finalRemarks="
				+ finalRemarks + ", toBeEvaluated=" + toBeEvaluated + ", sapIdList=" + sapIdList
				+ ", percentDifference=" + percentDifference + ", mean=" + mean + ", stddev=" + stddev
				+ ", normalizedScore=" + normalizedScore + ", roundedNormalizedScore=" + roundedNormalizedScore
				+ ", populationCount=" + populationCount + ", weightedMean=" + weightedMean + ", weightedstddev="
				+ weightedstddev + ", offlineAssignmentLive=" + offlineAssignmentLive + ", trackId=" + trackId
				+ ", amount=" + amount + ", tranDateTime=" + tranDateTime + ", bookingCompleteTime="
				+ bookingCompleteTime + ", tranStatus=" + tranStatus + ", booked=" + booked + ", transactionID="
				+ transactionID + ", requestID=" + requestID + ", merchantRefNo=" + merchantRefNo + ", secureHash="
				+ secureHash + ", respAmount=" + respAmount + ", description=" + description + ", responseCode="
				+ responseCode + ", respPaymentMethod=" + respPaymentMethod + ", isFlagged=" + isFlagged
				+ ", paymentID=" + paymentID + ", responseMessage=" + responseMessage + ", error=" + error
				+ ", respTranDateTime=" + respTranDateTime + ", paymentApplicable=" + paymentApplicable
				+ ", paymentDone=" + paymentDone + ", assignmentscore=" + assignmentscore + ", submissionAllowed="
				+ submissionAllowed + ", evaluatedFaculty=" + evaluatedFaculty + "]";
	}
	
	
	
}
