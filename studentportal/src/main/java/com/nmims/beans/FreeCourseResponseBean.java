package com.nmims.beans;

import java.io.Serializable;
import java.util.List;

public class FreeCourseResponseBean  implements Serializable {

	private String status;
	private String leads_id;
	private String error;
	
	private List<ProgramsStudentPortalBean> enrolledList;
	private List<ProgramsStudentPortalBean> notEnrolledList;
	private List<LeadModuleStatusBean> certificateList;

	private List<ProgramsStudentPortalBean> onGoingPrograms;
	private List<ProgramsStudentPortalBean> completedPrograms;
	
	public List<ProgramsStudentPortalBean> getOnGoingPrograms() {
		return onGoingPrograms;
	}

	public void setOnGoingPrograms(List<ProgramsStudentPortalBean> onGoingPrograms) {
		this.onGoingPrograms = onGoingPrograms;
	}

	public List<ProgramsStudentPortalBean> getCompletedPrograms() {
		return completedPrograms;
	}

	public void setCompletedPrograms(List<ProgramsStudentPortalBean> completedPrograms) {
		this.completedPrograms = completedPrograms;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String getLeads_id() {
		return leads_id;
	}

	public void setLeads_id(String leads_id) {
		this.leads_id = leads_id;
	}

	public String getError() {
		return error;
	}

	public void setError(String error) {
		this.error = error;
	}

	public List<ProgramsStudentPortalBean> getEnrolledList() {
		return enrolledList;
	}

	public void setEnrolledList(List<ProgramsStudentPortalBean> enrolledList) {
		this.enrolledList = enrolledList;
	}

	public List<ProgramsStudentPortalBean> getNotEnrolledList() {
		return notEnrolledList;
	}

	public void setNotEnrolledList(List<ProgramsStudentPortalBean> notEnrolledList) {
		this.notEnrolledList = notEnrolledList;
	}

	public List<LeadModuleStatusBean> getCertificateList() {
		return certificateList;
	}

	public void setCertificateList(List<LeadModuleStatusBean> certificateList) {
		this.certificateList = certificateList;
	}

	@Override
	public String toString() {
		return "FreeCourseResponseBean [status=" + status + ", leads_id=" + leads_id + ", error=" + error
				+ ", enrolledList=" + enrolledList + ", notEnrolledList=" + notEnrolledList + ", certificateList="
				+ certificateList + "]";
	}
}
