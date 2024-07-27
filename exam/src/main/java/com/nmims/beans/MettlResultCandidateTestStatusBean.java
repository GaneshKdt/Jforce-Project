package com.nmims.beans;

import java.io.Serializable;

public class MettlResultCandidateTestStatusBean  implements Serializable  {
	private String status;

    private String startTime;
    private String endTime;
    private String completionMode;
    
	private MettlResultCandidateTestResultBean result;
	private String pdfReport;
	private String htmlReport;
	private String performanceCategory;
	private String performanceCategoryVersion;
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
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
	public String getCompletionMode() {
		return completionMode;
	}
	public void setCompletionMode(String completionMode) {
		this.completionMode = completionMode;
	}
	public MettlResultCandidateTestResultBean getResult() {
		return result;
	}
	public void setResult(MettlResultCandidateTestResultBean result) {
		this.result = result;
	}
	public String getPdfReport() {
		return pdfReport;
	}
	public void setPdfReport(String pdfReport) {
		this.pdfReport = pdfReport;
	}
	public String getHtmlReport() {
		return htmlReport;
	}
	public void setHtmlReport(String htmlReport) {
		this.htmlReport = htmlReport;
	}
	public String getPerformanceCategory() {
		return performanceCategory;
	}
	public void setPerformanceCategory(String performanceCategory) {
		this.performanceCategory = performanceCategory;
	}
	public String getPerformanceCategoryVersion() {
		return performanceCategoryVersion;
	}
	public void setPerformanceCategoryVersion(String performanceCategoryVersion) {
		this.performanceCategoryVersion = performanceCategoryVersion;
	}
	@Override
	public String toString() {
		return "MettlResultCandidateTestStatusBean [status=" + status + ", startTime=" + startTime + ", endTime="
				+ endTime + ", completionMode=" + completionMode + ", result=" + result + ", pdfReport=" + pdfReport
				+ ", htmlReport=" + htmlReport + ", performanceCategory=" + performanceCategory
				+ ", performanceCategoryVersion=" + performanceCategoryVersion + "]";
	}
}
