package com.nmims.beans;

import java.io.Serializable;
import java.util.ArrayList;

public class SpecialisationResponseBean implements Serializable {
	
	private String status;
	private String message;
	private Specialisation specialisation;
	private Integer maxTerm;
	private ArrayList<Specialisation> specialisationList = new ArrayList<Specialisation>();
	private ArrayList<Specialisation> term3SelectedSubjects = new ArrayList<Specialisation>();
	private ArrayList<Specialisation> term4SelectedSubjects = new ArrayList<Specialisation>();
	private ArrayList<ProgramSubjectMappingExamBean> specialisationSubjectList = new ArrayList<ProgramSubjectMappingExamBean>();
	private ArrayList<ProgramSubjectMappingExamBean> subjectWithPrerequisite = new ArrayList<ProgramSubjectMappingExamBean>();
	private ArrayList<ProgramSubjectMappingExamBean> coreSubject = new ArrayList<ProgramSubjectMappingExamBean>();
	private ArrayList<ProgramSubjectMappingExamBean> specialisationSubjectCount = new ArrayList<ProgramSubjectMappingExamBean>();
	private ArrayList<ProgramSubjectMappingExamBean> autoSelectSubject = new ArrayList<ProgramSubjectMappingExamBean>();
	private ArrayList<ProgramSubjectMappingExamBean> commonSubject = new ArrayList<ProgramSubjectMappingExamBean>();
	
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}
	public String getMessage() {
		return message;
	}
	public void setMessage(String message) {
		this.message = message;
	}
	public Specialisation getSpecialisation() {
		return specialisation;
	}
	public void setSpecialisation(Specialisation specialisation) {
		this.specialisation = specialisation;
	}
	public Integer getMaxTerm() {
		return maxTerm;
	}
	public void setMaxTerm(Integer maxTerm) {
		this.maxTerm = maxTerm;
	}
	public ArrayList<Specialisation> getSpecialisationList() {
		return specialisationList;
	}
	public void setSpecialisationList(ArrayList<Specialisation> specialisationList) {
		this.specialisationList = specialisationList;
	}
	public ArrayList<Specialisation> getTerm3SelectedSubjects() {
		return term3SelectedSubjects;
	}
	public void setTerm3SelectedSubjects(ArrayList<Specialisation> term3SelectedSubjects) {
		this.term3SelectedSubjects = term3SelectedSubjects;
	}
	public ArrayList<ProgramSubjectMappingExamBean> getSpecialisationSubjectList() {
		return specialisationSubjectList;
	}
	public void setSpecialisationSubjectList(ArrayList<ProgramSubjectMappingExamBean> specialisationSubjectList) {
		this.specialisationSubjectList = specialisationSubjectList;
	}
	public ArrayList<ProgramSubjectMappingExamBean> getSubjectWithPrerequisite() {
		return subjectWithPrerequisite;
	}
	public void setSubjectWithPrerequisite(ArrayList<ProgramSubjectMappingExamBean> subjectWithPrerequisite) {
		this.subjectWithPrerequisite = subjectWithPrerequisite;
	}
	public ArrayList<ProgramSubjectMappingExamBean> getCoreSubject() {
		return coreSubject;
	}
	public void setCoreSubject(ArrayList<ProgramSubjectMappingExamBean> coreSubject) {
		this.coreSubject = coreSubject;
	}
	public ArrayList<ProgramSubjectMappingExamBean> getSpecialisationSubjectCount() {
		return specialisationSubjectCount;
	}
	public void setSpecialisationSubjectCount(ArrayList<ProgramSubjectMappingExamBean> specialisationSubjectCount) {
		this.specialisationSubjectCount = specialisationSubjectCount;
	}
	public ArrayList<ProgramSubjectMappingExamBean> getAutoSelectSubject() {
		return autoSelectSubject;
	}
	public void setAutoSelectSubject(ArrayList<ProgramSubjectMappingExamBean> autoSelectSubject) {
		this.autoSelectSubject = autoSelectSubject;
	}
	public ArrayList<ProgramSubjectMappingExamBean> getCommonSubject() {
		return commonSubject;
	}
	public void setCommonSubject(ArrayList<ProgramSubjectMappingExamBean> commonSubject) {
		this.commonSubject = commonSubject;
	}
	public ArrayList<Specialisation> getTerm4SelectedSubjects() {
		return term4SelectedSubjects;
	}
	public void setTerm4SelectedSubjects(ArrayList<Specialisation> term4SelectedSubjects) {
		this.term4SelectedSubjects = term4SelectedSubjects;
	}
	
}