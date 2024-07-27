package com.nmims.beans;

import java.io.Serializable;
import java.util.List;

public class TimeBoundUserMapping implements Serializable {

	private String id;
	private String userId;
	private int timebound_subject_config_id;
	private String role;
	private String prgm_sem_subj_id;
	private String facultyId_name;
	private String facultyName;
	private String coordinatorId_name;
	private String coordinatorName;
	private String acadYear;
	private String acadMonth;
	private String batchId;
	private String sem;
	private String name;
	private String studentType;
	private List<String> students;
	private List<FacultyExamBean> faculties;
	private List<UserAuthorizationExamBean> coordinators;
	private String createdBy;
	private String createdDate;
	private String lastModifiedBy;
	private String lastModifiedDate;
	private String errorMessage = "";
	private boolean errorRecord = false;

	private String examYear;
	private String examMonth;
	private String subject;

	private String isResit;
	private String studentName;
	private String imageUrl;
	private String emailId;
	private String mobile;

	private String consumerType;
	private String prgmStructApplicable;
	private String program;
	
	private String batchName;
	
	public String getBatchName() {
		return batchName;
	}
	public void setBatchName(String batchName) {
		this.batchName = batchName;
	}
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getUserId() {
		return userId;
	}
	public void setUserId(String userId) {
		this.userId = userId;
	}
	public int getTimebound_subject_config_id() {
		return timebound_subject_config_id;
	}
	public void setTimebound_subject_config_id(int timebound_subject_config_id) {
		this.timebound_subject_config_id = timebound_subject_config_id;
	}
	public String getRole() {
		return role;
	}
	public void setRole(String role) {
		this.role = role;
	}
	public String getPrgm_sem_subj_id() {
		return prgm_sem_subj_id;
	}
	public void setPrgm_sem_subj_id(String prgm_sem_subj_id) {
		this.prgm_sem_subj_id = prgm_sem_subj_id;
	}
	public String getFacultyId_name() {
		return facultyId_name;
	}
	public void setFacultyId_name(String facultyId_name) {
		this.facultyId_name = facultyId_name;
	}
	public String getFacultyName() {
		return facultyName;
	}
	public void setFacultyName(String facultyName) {
		this.facultyName = facultyName;
	}

	public String getCoordinatorId_name() {
		return coordinatorId_name;
	}

	public void setCoordinatorId_name(String coordinatorId_name) {
		this.coordinatorId_name = coordinatorId_name;
	}

	public String getCoordinatorName() {
		return coordinatorName;
	}

	public void setCoordinatorName(String coordinatorName) {
		this.coordinatorName = coordinatorName;
	}

	public String getAcadYear() {
		return acadYear;
	}
	public void setAcadYear(String acadYear) {
		this.acadYear = acadYear;
	}
	public String getAcadMonth() {
		return acadMonth;
	}
	public void setAcadMonth(String acadMonth) {
		this.acadMonth = acadMonth;
	}
	public String getBatchId() {
		return batchId;
	}
	public void setBatchId(String batchId) {
		this.batchId = batchId;
	}
	public String getSem() {
		return sem;
	}
	public void setSem(String sem) {
		this.sem = sem;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getStudentType() {
		return studentType;
	}
	public void setStudentType(String studentType) {
		this.studentType = studentType;
	}
	public List<String> getStudents() {
		return students;
	}
	public void setStudents(List<String> students) {
		this.students = students;
	}
	public List<FacultyExamBean> getFaculties() {
		return faculties;
	}
	public void setFaculties(List<FacultyExamBean> faculties) {
		this.faculties = faculties;
	}

	public List<UserAuthorizationExamBean> getCoordinators() {
		return coordinators;
	}

	public void setCoordinators(List<UserAuthorizationExamBean> coordinators) {
		this.coordinators = coordinators;
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
	public String getSubject() {
		return subject;
	}
	public void setSubject(String subject) {
		this.subject = subject;
	}
	
	public String getIsResit() {
		return isResit;
	}
	public void setIsResit(String isResit) {
		this.isResit = isResit;
	}
	
	public String getStudentName() {
		return studentName;
	}
	public void setStudentName(String studentName) {
		this.studentName = studentName;
	}
	public String getImageUrl() {
		return imageUrl;
	}

	public void setImageUrl(String imageUrl) {
		this.imageUrl = imageUrl;
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

	public String getConsumerType() {
		return consumerType;
	}

	public void setConsumerType(String consumerType) {
		this.consumerType = consumerType;
	}

	public String getPrgmStructApplicable() {
		return prgmStructApplicable;
	}

	public void setPrgmStructApplicable(String prgmStructApplicable) {
		this.prgmStructApplicable = prgmStructApplicable;
	}

	public String getProgram() {
		return program;
	}

	public void setProgram(String program) {
		this.program = program;
	}

	@Override
	public String toString() {
		return "TimeBoundUserMapping{" +
				"id='" + id + '\'' +
				", userId='" + userId + '\'' +
				", timebound_subject_config_id=" + timebound_subject_config_id +
				", role='" + role + '\'' +
				", prgm_sem_subj_id='" + prgm_sem_subj_id + '\'' +
				", facultyId_name='" + facultyId_name + '\'' +
				", facultyName='" + facultyName + '\'' +
				", coordinatorId_name='" + coordinatorId_name + '\'' +
				", coordinatorName='" + coordinatorName + '\'' +
				", acadYear='" + acadYear + '\'' +
				", acadMonth='" + acadMonth + '\'' +
				", batchId='" + batchId + '\'' +
				", sem='" + sem + '\'' +
				", name='" + name + '\'' +
				", studentType='" + studentType + '\'' +
				", students=" + students +
				", faculties=" + faculties +
				", coordinators=" + coordinators +
				", createdBy='" + createdBy + '\'' +
				", createdDate='" + createdDate + '\'' +
				", lastModifiedBy='" + lastModifiedBy + '\'' +
				", lastModifiedDate='" + lastModifiedDate + '\'' +
				", errorMessage='" + errorMessage + '\'' +
				", errorRecord=" + errorRecord +
				", examYear='" + examYear + '\'' +
				", examMonth='" + examMonth + '\'' +
				", subject='" + subject + '\'' +
				", isResit='" + isResit + '\'' +
				", studentName='" + studentName + '\'' +
				", imageUrl='" + imageUrl + '\'' +
				", emailId='" + emailId + '\'' +
				", mobile='" + mobile + '\'' +
				", consumerType='" + consumerType + '\'' +
				", prgmStructApplicable='" + prgmStructApplicable + '\'' +
				", program='" + program + '\'' +
				'}';
	}


}
