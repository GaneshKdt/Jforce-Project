package com.nmims.beans;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import org.springframework.web.multipart.commons.CommonsMultipartFile;

//spring security related changes rename CaseStudyBean to CaseStudyExamBean
public class CaseStudyExamBean implements Serializable {
	private int id;
	private String topic;
	private String status;
	private String lastModifiedDate;
	private String lastModifiedBy;
	private String month;
	private String year;
	private List<CaseStudyExamBean> caseStudyFiles;
	private CommonsMultipartFile fileData;
	private String filePath;
	private String studentFilePath;
	private String questionFilePreviewPath;
	private String subject;
	private String sapid;
	private String previewPath;
	private String program;
	private String startTime;
	private String endTime;
	private String startDate;
	private String endDate;
	private String batchYear;
	private String batchMonth;
	private String evaluated;
	private String evaluationDate;
	private String score;
	private String remarks;
	private String facultyId;
	private String reason;
	private String q1Marks;
	private String q2Marks;
	private String q3Marks;
	private String q4Marks;
	private String q5Marks;
	private String q6Marks;
	private String q7Marks;
	private String q8Marks;
	private String q9Marks;
	private String q10Marks;
	private String q1Remarks;
	private String q2Remarks;
	private String q3Remarks;
	private String q4Remarks;
	private String q5Remarks;
	private String q6Remarks;

	private String grade;
	private String evaluationCount;
	private String firstName;
	private String lastName;
	private String PrgmStructApplicable;
	 public String getEvaluationCount() {
		return evaluationCount;
	}
	public void setEvaluationCount(String evaluationCount) {
		this.evaluationCount = evaluationCount;
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
	
	public String getEvaluationDate() {
		return evaluationDate;
	}
	public void setEvaluationDate(String evaluationDate) {
		this.evaluationDate = evaluationDate;
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
		return evaluated;
	}
	public void setEvaluated(String evaluated) {
		this.evaluated = evaluated;
	}
	private ArrayList<String> faculties = new ArrayList<>();
	    //private List<FacultyBean> faculties = new ArrayList<FacultyBean>();
	    private ArrayList<String> numberOfCaseStudies = new ArrayList<>();
	    private ArrayList<String> indexes = new ArrayList<>();
	    
	    
	    
	public ArrayList<String> getFaculties() {
			return faculties;
		}
		public void setFaculties(ArrayList<String> faculties) {
			this.faculties = faculties;
		}
		public ArrayList<String> getNumberOfCaseStudies() {
			return numberOfCaseStudies;
		}
		public void setNumberOfCaseStudies(ArrayList<String> numberOfCaseStudies) {
			this.numberOfCaseStudies = numberOfCaseStudies;
		}
		public ArrayList<String> getIndexes() {
			return indexes;
		}
		public void setIndexes(ArrayList<String> indexes) {
			this.indexes = indexes;
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
	
	public List<CaseStudyExamBean> getCaseStudyFiles() {
		return caseStudyFiles;
	}

	public void setCaseStudyFiles(List<CaseStudyExamBean> caseStudyFiles) {
		this.caseStudyFiles = caseStudyFiles;
	}
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public String getTopic() {
		return topic;
	}
	public void setTopic(String topic) {
		this.topic = topic;
	}
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}
	public String getLastModifiedDate() {
		return lastModifiedDate;
	}
	public void setLastModifiedDate(String lastModifiedDate) {
		this.lastModifiedDate = lastModifiedDate;
	}
	public String getLastModifiedBy() {
		return lastModifiedBy;
	}
	public void setLastModifiedBy(String lastModifiedBy) {
		this.lastModifiedBy = lastModifiedBy;
	}

	public String getMonth() {
		return month;
	}
	public void setMonth(String month) {
		this.month = month;
	}
	public String getYear() {
		return year;
	}
	public void setYear(String year) {
		this.year = year;
	}

	public String getStudentFilePath() {
		return studentFilePath;
	}
	public void setStudentFilePath(String studentFilePath) {
		this.studentFilePath = studentFilePath;
	}
	public String getQuestionFilePreviewPath() {
		return questionFilePreviewPath;
	}
	public void setQuestionFilePreviewPath(String questionFilePreviewPath) {
		this.questionFilePreviewPath = questionFilePreviewPath;
	}
	public String getSubject() {
		return subject;
	}
	public void setSubject(String subject) {
		this.subject = subject;
	}
	public String getSapid() {
		return sapid;
	}
	public void setSapid(String sapid) {
		this.sapid = sapid;
	}
	public String getPreviewPath() {
		return previewPath;
	}
	public void setPreviewPath(String previewPath) {
		this.previewPath = previewPath;
	}
	public String getProgram() {
		return program;
	}
	public void setProgram(String program) {
		this.program = program;
	}

	public String getStartTime() {
		return startTime;
	}
	public void setStartTime(String startTime) {
		this.startTime = startTime;
	}
	public String getEndTime() {
		return endTime;
	}
	public void setEndTime(String endTime) {
		this.endTime = endTime;
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

	public String getBatchMonth() {
		return batchMonth;
	}
	public void setBatchMonth(String batchMonth) {
		this.batchMonth = batchMonth;
	}
	public String getBatchYear() {
		return batchYear;
	}
	public void setBatchYear(String batchYear) {
		this.batchYear = batchYear;
	}
	@Override
	public String toString() {
		return "CaseStudyBean [id=" + id + ", topic=" + topic + ", status=" + status + ", lastModifiedDate="
				+ lastModifiedDate + ", lastModifiedBy=" + lastModifiedBy + ", month=" + month + ", year=" + year
				+ ", caseStudyFiles=" + caseStudyFiles + ", fileData=" + fileData + ", filePath=" + filePath
				+ ", studentFilePath=" + studentFilePath + ", questionFilePreviewPath=" + questionFilePreviewPath
				+ ", subject=" + subject + ", sapid=" + sapid + ", previewPath=" + previewPath + ", program=" + program
				+ ", startTime=" + startTime + ", endTime=" + endTime + ", startDate=" + startDate + ", endDate="
				+ endDate + ", batchYear=" + batchYear + ", batchMonth=" + batchMonth + ", faculties=" + faculties
				+ ", numberOfCaseStudies=" + numberOfCaseStudies + ", indexes=" + indexes + "]";
	}
	public String getFacultyId() {
		return facultyId;
	}
	public void setFacultyId(String facultyId) {
		this.facultyId = facultyId;
	}
	public String getReason() {
		return reason;
	}
	public void setReason(String reason) {
		this.reason = reason;
	}
	public String getGrade() {
		return grade;
	}
	public void setGrade(String grade) {
		this.grade = grade;
	}
	public String getFirstName() {
		return firstName;
	}
	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}
	public String getLastName() {
		return lastName;
	}
	public void setLastName(String lastName) {
		this.lastName = lastName;
	}
	public String getPrgmStructApplicable() {
		return PrgmStructApplicable;
	}
	public void setPrgmStructApplicable(String prgmStructApplicable) {
		PrgmStructApplicable = prgmStructApplicable;
	}
	
	
	
}
