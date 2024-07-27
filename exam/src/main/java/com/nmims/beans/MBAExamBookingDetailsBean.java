package com.nmims.beans;

import java.io.Serializable;
import java.util.List;

public class MBAExamBookingDetailsBean  implements Serializable   {

	private String url;
	private String sapid;
	private String batchId;
	private boolean canBook;
	private String canNotBookReason;
	private List<String> subjectsAppliedFor;
	private List<MBAStudentSubjectMarksDetailsBean> failedSubjectsList;
	
	public String getUrl() {
		return url;
	}
	public void setUrl(String url) {
		this.url = url;
	}
	public String getSapid() {
		return sapid;
	}
	public void setSapid(String sapid) {
		this.sapid = sapid;
	}
	public String getBatchId() {
		return batchId;
	}
	public void setBatchId(String batchId) {
		this.batchId = batchId;
	}
	public boolean isCanBook() {
		return canBook;
	}
	public void setCanBook(boolean canBook) {
		this.canBook = canBook;
	}
	public String getCanNotBookReason() {
		return canNotBookReason;
	}
	public void setCanNotBookReason(String canNotBookReason) {
		this.canNotBookReason = canNotBookReason;
	}
	public List<String> getSubjectsAppliedFor() {
		return subjectsAppliedFor;
	}
	public void setSubjectsAppliedFor(List<String> subjectsAppliedFor) {
		this.subjectsAppliedFor = subjectsAppliedFor;
	}
	public List<MBAStudentSubjectMarksDetailsBean> getFailedSubjectsList() {
		return failedSubjectsList;
	}
	public void setFailedSubjectsList(List<MBAStudentSubjectMarksDetailsBean> failedSubjectsList) {
		this.failedSubjectsList = failedSubjectsList;
	}
	@Override
	public String toString() {
		return "MBAWXExamBookingDetailsBean [url=" + url + ", sapid=" + sapid + ", batchId=" + batchId + ", canBook="
				+ canBook + ", canNotBookReason=" + canNotBookReason + ", subjectsAppliedFor=" + subjectsAppliedFor
				+ ", failedSubjectsList=" + failedSubjectsList + "]";
	}
	
}
