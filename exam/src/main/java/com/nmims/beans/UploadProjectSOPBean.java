package com.nmims.beans;

import java.io.Serializable;

import org.springframework.web.multipart.commons.CommonsMultipartFile;

public class UploadProjectSOPBean implements Serializable {
	private String id;
	private int attempt;
	private CommonsMultipartFile fileData;
	private String sapId;
	private String month;
	private String year;
	private String subject;
	private String filePath;
	private String previewPath;
	private String facultyId;
	private String facultyName;
	private String status;
	private String reason;
	private String startDate;
	private String endDate;
	private String payment_status;
	private String track_id;
	private String updated_at;
	private String created_at;
	private String created_by;
	private String updated_by;
	
	
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

	public String getUpdated_at() {
		return updated_at;
	}

	public void setUpdated_at(String updated_at) {
		this.updated_at = updated_at;
	}

	public String getCreated_at() {
		return created_at;
	}

	public void setCreated_at(String created_at) {
		this.created_at = created_at;
	}

	public String getTrack_id() {
		return track_id;
	}

	public void setTrack_id(String track_id) {
		this.track_id = track_id;
	}

	public String getPayment_status() {
		return payment_status;
	}

	public void setPayment_status(String payment_status) {
		this.payment_status = payment_status;
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

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
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

	public String getFacultyId() {
		return facultyId;
	}

	public void setFacultyId(String facultyId) {
		this.facultyId = facultyId;
	}

	public String getPreviewPath() {
		return previewPath;
	}

	public void setPreviewPath(String previewPath) {
		this.previewPath = previewPath;
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

	public String getSapId() {
		return sapId;
	}

	public void setSapId(String sapId) {
		this.sapId = sapId;
	}

	public CommonsMultipartFile getFileData() {
		return fileData;
	}

	public void setFileData(CommonsMultipartFile fileData) {
		this.fileData = fileData;
	}

	public int getAttempt() {
		return attempt;
	}

	public void setAttempt(int attempt) {
		this.attempt = attempt;
	}

	public String getSubject() {
		return subject;
	}

	public void setSubject(String subject) {
		this.subject = subject;
	}

	public String getFacultyName() {
		return facultyName;
	}

	public void setFacultyName(String facultyName) {
		this.facultyName = facultyName;
	}

	@Override
	public String toString() {
		return "UploadProjectSOPBean [id=" + id + ", attempt=" + attempt + ", fileData=" + fileData + ", sapId=" + sapId
				+ ", month=" + month + ", year=" + year + ", subject=" + subject + ", filePath=" + filePath
				+ ", previewPath=" + previewPath + ", facultyId=" + facultyId + ", facultyName=" + facultyName
				+ ", status=" + status + ", reason=" + reason + ", startDate=" + startDate + ", endDate=" + endDate
				+ ", payment_status=" + payment_status + ", track_id=" + track_id + ", updated_at=" + updated_at
				+ ", created_at=" + created_at + ", created_by=" + created_by + ", updated_by=" + updated_by + "]";
	}
	
	
}
