package com.nmims.beans;

import java.util.List;

import org.springframework.web.multipart.commons.CommonsMultipartFile;

public class AssignmentFilesSetbean {


	private List<AssignmentFileBean> assignmentFiles;
	private String year;
	private String month;
	private String startDate;
	private String endDate;
	private String consumerTypeId;
	private String programId;
	private String programStructureId;
	private String consumerType;
	private String program;
	private String programStructure;
	private String liveType;
	private String examYear;
	private String examMonth;
	private String acadsYear;
	private String acadsMonth;
	private String consumerProgramStructureId;
	private String pss_id;
	private String id;
	private String facultyId;
	private String lastModifiedDate;
	private String createdDate;
	private String status;
	private CommonsMultipartFile fileData;
	private boolean errorRecord;
	private String subject;
	private String reviewer;
	private String countOfUpload;
	private String countOfReview;
	private String countOfResolution;
	private String filePath;
	private String faculty;
	private String  dueDate;
	private String feedback;
	private String approve;
	private String message;
	private String adminApprove;
	private String uploadStatus;
	private List<AssignmentFilesSetbean> assignmentFilesSet;
	private String questionFilePreviewPath;
	private String studentStartDate;
	private String studentEndDate;
	private Integer totalQpNotUploadedCount;
	private Integer totalQpNotReviewedCount;
	private String tabindex;
	private List<AssignmentFilesSetbean> uploadList;
	private List<AssignmentFilesSetbean> reviewList;
	private List<AssignmentFilesSetbean> completedList;
	private List<AssignmentFilesSetbean> ResolutionList;
	private String reviewStatus;
	private String resolveStatus; 
	private String remark;
	private List<String> questions;
	private List<String> marks;
	private String qpId;
	private String question;
	private String mark;
	private String  countOfApprove;
	private List<String> qnNos;
	private String  qnNo;
	
	
	public String getPss_id() {
		return pss_id;
	}

	public void setPss_id(String pss_id) {
		this.pss_id = pss_id;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getFacultyId() {
		return facultyId;
	}

	public void setFacultyId(String facultyId) {
		this.facultyId = facultyId;
	}

	public String getLastModifiedDate() {
		return lastModifiedDate;
	}

	public void setLastModifiedDate(String lastModifiedDate) {
		this.lastModifiedDate = lastModifiedDate;
	}

	public String getCreatedDate() {
		return createdDate;
	}

	public void setCreatedDate(String createdDate) {
		this.createdDate = createdDate;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public CommonsMultipartFile getFileData() {
		return fileData;
	}

	public void setFileData(CommonsMultipartFile fileData) {
		this.fileData = fileData;
	}

	public boolean isErrorRecord() {
		return errorRecord;
	}

	public void setErrorRecord(boolean errorRecord) {
		this.errorRecord = errorRecord;
	}

	public String getSubject() {
		return subject;
	}

	public void setSubject(String subject) {
		this.subject = subject;
	}

	public String getReviewer() {
		return reviewer;
	}

	public void setReviewer(String reviewer) {
		this.reviewer = reviewer;
	}

	public String getCountOfUpload() {
		return countOfUpload;
	}

	public void setCountOfUpload(String countOfUpload) {
		this.countOfUpload = countOfUpload;
	}

	public String getCountOfReview() {
		return countOfReview;
	}

	public void setCountOfReview(String countOfReview) {
		this.countOfReview = countOfReview;
	}

	public String getCountOfResolution() {
		return countOfResolution;
	}

	public void setCountOfResolution(String countOfResolution) {
		this.countOfResolution = countOfResolution;
	}

	public String getFilePath() {
		return filePath;
	}

	public void setFilePath(String filePath) {
		this.filePath = filePath;
	}

	public String getFaculty() {
		return faculty;
	}

	public void setFaculty(String faculty) {
		this.faculty = faculty;
	}

	public String getDueDate() {
		return dueDate;
	}

	public void setDueDate(String dueDate) {
		this.dueDate = dueDate;
	}

	public String getFeedback() {
		return feedback;
	}

	public void setFeedback(String feedback) {
		this.feedback = feedback;
	}

	public String getApprove() {
		return approve;
	}

	public void setApprove(String approve) {
		this.approve = approve;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public String getAdminApprove() {
		return adminApprove;
	}

	public void setAdminApprove(String adminApprove) {
		this.adminApprove = adminApprove;
	}

	public String getUploadStatus() {
		return uploadStatus;
	}

	public void setUploadStatus(String uploadStatus) {
		this.uploadStatus = uploadStatus;
	}

	public List<AssignmentFilesSetbean> getAssignmentFilesSet() {
		return assignmentFilesSet;
	}

	public void setAssignmentFilesSet(List<AssignmentFilesSetbean> assignmentFilesSet) {
		this.assignmentFilesSet = assignmentFilesSet;
	}

	public String getQuestionFilePreviewPath() {
		return questionFilePreviewPath;
	}

	public void setQuestionFilePreviewPath(String questionFilePreviewPath) {
		this.questionFilePreviewPath = questionFilePreviewPath;
	}

	public String getStudentStartDate() {
		return studentStartDate;
	}

	public void setStudentStartDate(String studentStartDate) {
		this.studentStartDate = studentStartDate;
	}

	public String getStudentEndDate() {
		return studentEndDate;
	}

	public void setStudentEndDate(String studentEndDate) {
		this.studentEndDate = studentEndDate;
	}

	public Integer getTotalQpNotUploadedCount() {
		return totalQpNotUploadedCount;
	}

	public void setTotalQpNotUploadedCount(Integer totalQpNotUploadedCount) {
		this.totalQpNotUploadedCount = totalQpNotUploadedCount;
	}

	public Integer getTotalQpNotReviewedCount() {
		return totalQpNotReviewedCount;
	}

	public void setTotalQpNotReviewedCount(Integer totalQpNotReviewedCount) {
		this.totalQpNotReviewedCount = totalQpNotReviewedCount;
	}

	public String getTabindex() {
		return tabindex;
	}

	public void setTabindex(String tabindex) {
		this.tabindex = tabindex;
	}

	public List<AssignmentFilesSetbean> getUploadList() {
		return uploadList;
	}

	public void setUploadList(List<AssignmentFilesSetbean> uploadList) {
		this.uploadList = uploadList;
	}

	public List<AssignmentFilesSetbean> getReviewList() {
		return reviewList;
	}

	public void setReviewList(List<AssignmentFilesSetbean> reviewList) {
		this.reviewList = reviewList;
	}

	public List<AssignmentFilesSetbean> getCompletedList() {
		return completedList;
	}

	public void setCompletedList(List<AssignmentFilesSetbean> completedList) {
		this.completedList = completedList;
	}

	public List<AssignmentFilesSetbean> getResolutionList() {
		return ResolutionList;
	}

	public void setResolutionList(List<AssignmentFilesSetbean> resolutionList) {
		ResolutionList = resolutionList;
	}

	public String getReviewStatus() {
		return reviewStatus;
	}

	public void setReviewStatus(String reviewStatus) {
		this.reviewStatus = reviewStatus;
	}

	public String getResolveStatus() {
		return resolveStatus;
	}

	public void setResolveStatus(String resolveStatus) {
		this.resolveStatus = resolveStatus;
	}

	public String getRemark() {
		return remark;
	}

	public void setRemark(String remark) {
		this.remark = remark;
	}

	public List<String> getQuestions() {
		return questions;
	}

	public void setQuestions(List<String> questions) {
		this.questions = questions;
	}

	public List<String> getMarks() {
		return marks;
	}

	public void setMarks(List<String> marks) {
		this.marks = marks;
	}

	public String getQpId() {
		return qpId;
	}

	public void setQpId(String qpId) {
		this.qpId = qpId;
	}

	public String getQuestion() {
		return question;
	}

	public void setQuestion(String question) {
		this.question = question;
	}

	public String getMark() {
		return mark;
	}

	public void setMark(String mark) {
		this.mark = mark;
	}

	public String getCountOfApprove() {
		return countOfApprove;
	}

	public void setCountOfApprove(String countOfApprove) {
		this.countOfApprove = countOfApprove;
	}

	public List<String> getQnNos() {
		return qnNos;
	}

	public void setQnNos(List<String> qnNos) {
		this.qnNos = qnNos;
	}

	public String getQnNo() {
		return qnNo;
	}

	public void setQnNo(String qnNo) {
		this.qnNo = qnNo;
	}

	public String getAcadsYear() {
		return acadsYear;
	}

	public void setAcadsYear(String acadsYear) {
		this.acadsYear = acadsYear;
	}

	public String getAcadsMonth() {
		return acadsMonth;
	}

	public void setAcadsMonth(String acadsMonth) {
		this.acadsMonth = acadsMonth;
	}

	public String getExamYear() {
		return examYear;
	}

	public void setExamYear(String examYear) {
		this.examYear = examYear;
	}

	public String getExamMonth() {
		return examMonth;
	}

	public void setExamMonth(String examMonth) {
		this.examMonth = examMonth;
	}

	public String getConsumerType() {
		return consumerType;
	}

	public void setConsumerType(String consumerType) {
		this.consumerType = consumerType;
	}

	public String getProgram() {
		return program;
	}

	public void setProgram(String program) {
		this.program = program;
	}

	public String getProgramStructure() {
		return programStructure;
	}

	public void setProgramStructure(String programStructure) {
		this.programStructure = programStructure;
	}

	public String getLiveType() {
		return liveType;
	}

	public void setLiveType(String liveType) {
		this.liveType = liveType;
	}

	public String getConsumerProgramStructureId() {
		return consumerProgramStructureId;
	}

	public void setConsumerProgramStructureId(String consumerProgramStructureId) {
		this.consumerProgramStructureId = consumerProgramStructureId;
	}

	public List<AssignmentFileBean> getAssignmentFiles() {
		return assignmentFiles;
	}

	public void setAssignmentFiles(List<AssignmentFileBean> assignmentFiles) {
		this.assignmentFiles = assignmentFiles;
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
	
	public String getConsumerTypeId() {
		return consumerTypeId;
	}

	public void setConsumerTypeId(String consumerTypeId) {
		this.consumerTypeId = consumerTypeId;
	}

	public String getProgramId() {
		return programId;
	}

	public void setProgramId(String programId) {
		this.programId = programId;
	}

	public String getProgramStructureId() {
		return programStructureId;
	}

	public void setProgramStructureId(String programStructureId) {
		this.programStructureId = programStructureId;
	}

	@Override
	public String toString() {
		return "AssignmentFilesSetbean [assignmentFiles=" + assignmentFiles + ", year=" + year + ", month=" + month
				+ ", startDate=" + startDate + ", endDate=" + endDate + ", consumerTypeId=" + consumerTypeId
				+ ", programId=" + programId + ", programStructureId=" + programStructureId + ", consumerType="
				+ consumerType + ", program=" + program + ", programStructure=" + programStructure + ", liveType="
				+ liveType + ", examYear=" + examYear + ", examMonth=" + examMonth + ", acadsYear=" + acadsYear
				+ ", acadsMonth=" + acadsMonth + ", consumerProgramStructureId=" + consumerProgramStructureId
				+ ", pss_id=" + pss_id + ", id=" + id + ", facultyId=" + facultyId + ", lastModifiedDate="
				+ lastModifiedDate + ", createdDate=" + createdDate + ", status=" + status + ", fileData=" + fileData
				+ ", errorRecord=" + errorRecord + ", subject=" + subject + ", reviewer=" + reviewer
				+ ", countOfUpload=" + countOfUpload + ", countOfReview=" + countOfReview + ", countOfResolution="
				+ countOfResolution + ", filePath=" + filePath + ", faculty=" + faculty + ", dueDate=" + dueDate
				+ ", feedback=" + feedback + ", approve=" + approve + ", message=" + message + ", adminApprove="
				+ adminApprove + ", uploadStatus=" + uploadStatus + ", assignmentFilesSet=" + assignmentFilesSet
				+ ", questionFilePreviewPath=" + questionFilePreviewPath + ", studentStartDate=" + studentStartDate
				+ ", studentEndDate=" + studentEndDate + ", totalQpNotUploadedCount=" + totalQpNotUploadedCount
				+ ", totalQpNotReviewedCount=" + totalQpNotReviewedCount + ", tabindex=" + tabindex + ", uploadList="
				+ uploadList + ", reviewList=" + reviewList + ", completedList=" + completedList + ", ResolutionList="
				+ ResolutionList + ", reviewStatus=" + reviewStatus + ", resolveStatus=" + resolveStatus + ", remark="
				+ remark + ", questions=" + questions + ", marks=" + marks + ", qpId=" + qpId + ", question=" + question
				+ ", mark=" + mark + ", countOfApprove=" + countOfApprove + ", qnNos=" + qnNos + ", qnNo=" + qnNo + "]";
	}
	
	
}
