package com.nmims.beans;

import java.io.Serializable;

import org.springframework.web.multipart.commons.CommonsMultipartFile;

public class UploadProjectSynopsisBean implements Serializable {
	private String id;
	private int attempt;
	private CommonsMultipartFile fileData;
	private String sapid;
	private String month;
	private String year;
	private String subject;
	private String filePath;
	private String previewPath;
	private String facultyId;
	private String facultyName;
	private String status;
	private String reason;
	private String score;
	private String evaluationDate;
	private String evaluated;
	@Override
	public String toString() {
		return "UploadProjectSynopsisBean [id=" + id + ", attempt=" + attempt + ", fileData=" + fileData + ", sapid="
				+ sapid + ", month=" + month + ", year=" + year + ", subject=" + subject + ", filePath=" + filePath
				+ ", previewPath=" + previewPath + ", facultyId=" + facultyId + ", facultyName=" + facultyName
				+ ", status=" + status + ", reason=" + reason + ", score=" + score + ", evaluationDate="
				+ evaluationDate + ", evaluated=" + evaluated + ", evaluationCount=" + evaluationCount
				+ ", payment_status=" + payment_status + ", track_id=" + track_id + ", created_by=" + created_by
				+ ", updated_by=" + updated_by + ", created_at=" + created_at + ", updated_at=" + updated_at + "]";
	}
	private String evaluationCount;
	private String payment_status;
	private String track_id;
	private String created_by;
	private String updated_by;
	private String created_at;
	private String updated_at;
	
	
	public String getScore() {
		return score;
	}
	public void setScore(String score) {
		this.score = score;
	}
	public String getEvaluationDate() {
		return evaluationDate;
	}
	public void setEvaluationDate(String evaluationDate) {
		this.evaluationDate = evaluationDate;
	}
	public String getEvaluated() {
		return evaluated;
	}
	public void setEvaluated(String evaluated) {
		this.evaluated = evaluated;
	}
	public String getEvaluationCount() {
		return evaluationCount;
	}
	public void setEvaluationCount(String evaluationCount) {
		this.evaluationCount = evaluationCount;
	}
	public String getCreated_by() {
		return created_by;
	}
	public void setCreated_by(String created_by) {
		this.created_by = created_by;
	}
	public String getUpdated_by() {
		return updated_by;
	}
	public void setUpdated_by(String updated_by) {
		this.updated_by = updated_by;
	}
	public String getCreated_at() {
		return created_at;
	}
	public void setCreated_at(String created_at) {
		this.created_at = created_at;
	}
	public String getUpdated_at() {
		return updated_at;
	}
	public void setUpdated_at(String updated_at) {
		this.updated_at = updated_at;
	}
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public int getAttempt() {
		return attempt;
	}
	public void setAttempt(int attempt) {
		this.attempt = attempt;
	}
	public CommonsMultipartFile getFileData() {
		return fileData;
	}
	public void setFileData(CommonsMultipartFile fileData) {
		this.fileData = fileData;
	}
	public String getSapid() {
		return sapid;
	}
	public void setSapid(String sapid) {
		this.sapid = sapid;
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
	public String getFilePath() {
		return filePath;
	}
	public void setFilePath(String filePath) {
		this.filePath = filePath;
	}
	public String getPreviewPath() {
		return previewPath;
	}
	public void setPreviewPath(String previewPath) {
		this.previewPath = previewPath;
	}
	public String getFacultyId() {
		return facultyId;
	}
	public void setFacultyId(String facultyId) {
		this.facultyId = facultyId;
	}
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}
	public String getReason() {
		return reason;
	}
	public void setReason(String reason) {
		this.reason = reason;
	}
	public String getPayment_status() {
		return payment_status;
	}
	public void setPayment_status(String payment_status) {
		this.payment_status = payment_status;
	}
	public String getTrack_id() {
		return track_id;
	}
	public void setTrack_id(String track_id) {
		this.track_id = track_id;
	}
	public String getFacultyName() {
		return facultyName;
	}
	public void setFacultyName(String facultyName) {
		this.facultyName = facultyName;
	}
	public String getSubject() {
		return subject;
	}
	public void setSubject(String subject) {
		this.subject = subject;
	}
	
	
}
