package com.nmims.dto;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.nmims.beans.AnnouncementStudentPortalBean;
import com.nmims.beans.ELearnResourcesStudentPortalBean;
import com.nmims.beans.StudentStudentPortalBean;
import com.nmims.beans.UserAuthorizationStudentPortalBean;
import com.nmims.helpers.PersonStudentPortalBean;

public class LoginSSODto {

	private StudentStudentPortalBean student;
	
	private ArrayList<AnnouncementStudentPortalBean> announcements;
	
	private String userId;
	
	private String isStudent;
	
	private PersonStudentPortalBean personDetails;
	
	private UserAuthorizationStudentPortalBean userBean;
	
	private String validityExpired;
	
	private HashMap<String,String> applicableSubjects;
	
	private StudentStudentPortalBean regData;
	
	private ELearnResourcesStudentPortalBean stukent;
	
	private ELearnResourcesStudentPortalBean harvard;
	
	private double regOrder;
	
	private double maxOrderWhereContentLive;
	
	private double currentOrder;
	
	private List<Integer> currentSemPSSId;
	
	private String isLoginAsLead;
	
	private String earlyAccess;

	private List<Integer> liveSessionPssIdAccess;
	
	Map<String, Boolean> featureViseAccess;
	
	private boolean consumerProgramStructureHasCSAccess;
	
	private double acadSessionLiveOrder;
	
	Map<String, Boolean> csAdmin;
	
	private boolean courseraAccess;
	
	private String validityEndDate;

	private String errorMessage;
	
	private String encryptedSapId;
	
	private List<Integer> subjectCodeId;

	public List<Integer> getSubjectCodeId() {
		return subjectCodeId;
	}

	public void setSubjectCodeId(List<Integer> subjectCodeId) {
		this.subjectCodeId = subjectCodeId;
	}

	public String getEncryptedSapId() {
		return encryptedSapId;
	}

	public void setEncryptedSapId(String encryptedSapId) {
		this.encryptedSapId = encryptedSapId;
	}

	public String getErrorMessage() {
		return errorMessage;
	}

	public void setErrorMessage(String errorMessage) {
		this.errorMessage = errorMessage;
	}

	public Map<String, Boolean> getCsAdmin() {
		return csAdmin;
	}

	public void setCsAdmin(Map<String, Boolean> csAdmin) {
		this.csAdmin = csAdmin;
	}

	public double getAcadSessionLiveOrder() {
		return acadSessionLiveOrder;
	}

	public void setAcadSessionLiveOrder(double acadSessionLiveOrder) {
		this.acadSessionLiveOrder = acadSessionLiveOrder;
	}

	public StudentStudentPortalBean getStudent() {
		return student;
	}

	public void setStudent(StudentStudentPortalBean student) {
		this.student = student;
	}

	public ArrayList<AnnouncementStudentPortalBean> getAnnouncements() {
		return announcements;
	}

	public void setAnnouncements(ArrayList<AnnouncementStudentPortalBean> announcements) {
		this.announcements = announcements;
	}


	public PersonStudentPortalBean getPersonDetails() {
		return personDetails;
	}

	public void setPersonDetails(PersonStudentPortalBean personDetails) {
		this.personDetails = personDetails;
	}

	public UserAuthorizationStudentPortalBean getUserBean() {
		return userBean;
	}

	public void setUserBean(UserAuthorizationStudentPortalBean userBean) {
		this.userBean = userBean;
	}

	public HashMap<String, String> getApplicableSubjects() {
		return applicableSubjects;
	}

	public void setApplicableSubjects(HashMap<String, String> applicableSubjects) {
		this.applicableSubjects = applicableSubjects;
	}


	public StudentStudentPortalBean getRegData() {
		return regData;
	}

	public void setRegData(StudentStudentPortalBean regData) {
		this.regData = regData;
	}

	public String getUserId() {
		return userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}

	public String getIsStudent() {
		return isStudent;
	}

	public void setIsStudent(String isStudent) {
		this.isStudent = isStudent;
	}

	public ELearnResourcesStudentPortalBean getStukent() {
		return stukent;
	}

	public void setStukent(ELearnResourcesStudentPortalBean stukent) {
		this.stukent = stukent;
	}

	public ELearnResourcesStudentPortalBean getHarvard() {
		return harvard;
	}

	public void setHarvard(ELearnResourcesStudentPortalBean harvard) {
		this.harvard = harvard;
	}

	public String isValidityExpired() {
		return validityExpired;
	}

	public void setValidityExpired(String validityExpired) {
		this.validityExpired = validityExpired;
	}

	public double getRegOrder() {
		return regOrder;
	}

	public void setRegOrder(double regOrder) {
		this.regOrder = regOrder;
	}

	public double getMaxOrderWhereContentLive() {
		return maxOrderWhereContentLive;
	}

	public void setMaxOrderWhereContentLive(double maxOrderWhereContentLive) {
		this.maxOrderWhereContentLive = maxOrderWhereContentLive;
	}

	public double getCurrentOrder() {
		return currentOrder;
	}

	public void setCurrentOrder(double currentOrder) {
		this.currentOrder = currentOrder;
	}

	public List<Integer> getCurrentSemPSSId() {
		return currentSemPSSId;
	}

	public void setCurrentSemPSSId(List<Integer> currentSemPSSId) {
		this.currentSemPSSId = currentSemPSSId;
	}

	public String getIsLoginAsLead() {
		return isLoginAsLead;
	}

	public void setIsLoginAsLead(String isLoginAsLead) {
		this.isLoginAsLead = isLoginAsLead;
	}

	public String getValidityExpired() {
		return validityExpired;
	}

	public String getEarlyAccess() {
		return earlyAccess;
	}

	public void setEarlyAccess(String earlyAccess) {
		this.earlyAccess = earlyAccess;
	}


	public List<Integer> getLiveSessionPssIdAccess() {
		return liveSessionPssIdAccess;
	}

	public void setLiveSessionPssIdAccess(List<Integer> liveSessionPssIdAccess) {
		this.liveSessionPssIdAccess = liveSessionPssIdAccess;
	}

	public Map<String, Boolean> getFeatureViseAccess() {
		return featureViseAccess;
	}

	public void setFeatureViseAccess(Map<String, Boolean> featureViseAccess) {
		this.featureViseAccess = featureViseAccess;
	}

	public boolean isConsumerProgramStructureHasCSAccess() {
		return consumerProgramStructureHasCSAccess;
	}

	public void setConsumerProgramStructureHasCSAccess(boolean consumerProgramStructureHasCSAccess) {
		this.consumerProgramStructureHasCSAccess = consumerProgramStructureHasCSAccess;

	}

	public boolean isCourseraAccess() {
		return courseraAccess;
	}

	public void setCourseraAccess(boolean courseraAccess) {
		this.courseraAccess = courseraAccess;
	}

	public String getValidityEndDate() {
		return validityEndDate;
	}

	public void setValidityEndDate(String validityEndDate) {
		this.validityEndDate = validityEndDate;
	}

	@Override
	public String toString() {
		return "LoginSSODto [student=" + student + ", announcements=" + announcements + ", userId=" + userId
				+ ", isStudent=" + isStudent + ", personDetails=" + personDetails + ", userBean=" + userBean
				+ ", validityExpired=" + validityExpired + ", applicableSubjects=" + applicableSubjects + ", regData="
				+ regData + ", stukent=" + stukent + ", harvard=" + harvard + ", regOrder=" + regOrder
				+ ", maxOrderWhereContentLive=" + maxOrderWhereContentLive + ", currentOrder=" + currentOrder
				+ ", currentSemPSSId=" + currentSemPSSId + ", isLoginAsLead=" + isLoginAsLead + ", earlyAccess="
				+ earlyAccess + ", liveSessionPssIdAccess=" + liveSessionPssIdAccess + ", featureViseAccess="
				+ featureViseAccess + ", consumerProgramStructureHasCSAccess=" + consumerProgramStructureHasCSAccess
				+ ", acadSessionLiveOrder=" + acadSessionLiveOrder + ", csAdmin=" + csAdmin + ", validityEndDate=" + validityEndDate + "]";
	}



}
