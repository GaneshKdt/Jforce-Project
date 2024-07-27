package com.nmims.beans;

import java.io.Serializable;
import java.util.List;

public class ProjectLiveConfigurationBean  implements Serializable  {

	private String sapid;
	private String acadYear;
	private String acadMonth;
	private String sem;
	private String subject;
	private String consumerProgramStructureId;
	private String programSemSubjId;
	
	private List<ProjectModuleLiveSettings> modules;
	
	private AssignmentFileBean projectFileBean;

	private String error;

	public String getSapid() {
		return sapid;
	}
	public void setSapid(String sapid) {
		this.sapid = sapid;
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
	public String getSem() {
		return sem;
	}
	public void setSem(String sem) {
		this.sem = sem;
	}
	public String getSubject() {
		return subject;
	}
	public void setSubject(String subject) {
		this.subject = subject;
	}
	public String getConsumerProgramStructureId() {
		return consumerProgramStructureId;
	}
	public void setConsumerProgramStructureId(String consumerProgramStructureId) {
		this.consumerProgramStructureId = consumerProgramStructureId;
	}
	public String getProgramSemSubjId() {
		return programSemSubjId;
	}
	public void setProgramSemSubjId(String programSemSubjId) {
		this.programSemSubjId = programSemSubjId;
	}
	public List<ProjectModuleLiveSettings> getModules() {
		return modules;
	}
	public void setModules(List<ProjectModuleLiveSettings> modules) {
		this.modules = modules;
	}
	public AssignmentFileBean getProjectFileBean() {
		return projectFileBean;
	}
	public void setProjectFileBean(AssignmentFileBean projectFileBean) {
		this.projectFileBean = projectFileBean;
	}
	public String getError() {
		return error;
	}
	public void setError(String error) {
		this.error = error;
	}
	@Override
	public String toString() {
		return "ProjectLiveConfigurationBean [sapid=" + sapid + ", acadYear=" + acadYear + ", acadMonth=" + acadMonth
				+ ", sem=" + sem + ", subject=" + subject + ", consumerProgramStructureId=" + consumerProgramStructureId
				+ ", programSemSubjId=" + programSemSubjId + ", modules=" + modules + ", projectFileBean="
				+ projectFileBean + ", error=" + error + "]";
	}
}
