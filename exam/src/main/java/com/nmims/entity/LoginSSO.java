package com.nmims.entity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;
import org.springframework.data.redis.core.index.Indexed;

import com.nmims.beans.UserAuthorizationExamBean;
import com.nmims.beans.ExamAnnouncementBean;
import com.nmims.beans.StudentExamBean;
import com.nmims.beans.Person;

@RedisHash("LoginSSO")
public class LoginSSO {
	
	@Id private String userId;

	private StudentExamBean student;
	
	private ArrayList<ExamAnnouncementBean> announcements;
	
	private String isStudent;
	
	private Person personDetails;
	
	private UserAuthorizationExamBean userBean;
	
	private String validityExpired;

	private HashMap<String,String> applicableSubjects;
	
	private StudentExamBean regData;
	
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
	
	private List<Integer> subjectCodeId;
	
	public List<Integer> getSubjectCodeId() {
		return subjectCodeId;
	}

	public void setSubjectCodeId(List<Integer> subjectCodeId) {
		this.subjectCodeId = subjectCodeId;
	}

	public StudentExamBean getStudent() {
		return student;
	}

	public void setStudent(StudentExamBean student) {
		this.student = student;
	}

	public ArrayList<ExamAnnouncementBean> getAnnouncements() {
		return announcements;
	}

	public void setAnnouncements(ArrayList<ExamAnnouncementBean> announcements) {
		this.announcements = announcements;
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

	public Person getPersonDetails() {
		return personDetails;
	}

	public void setPersonDetails(Person personDetails) {
		this.personDetails = personDetails;
	}

	public UserAuthorizationExamBean getUserBean() {
		return userBean;
	}

	public void setUserBean(UserAuthorizationExamBean userBean) {
		this.userBean = userBean;
	}

	public String getValidityExpired() {
		return validityExpired;
	}

	public void setValidityExpired(String validityExpired) {
		this.validityExpired = validityExpired;
	}

	public HashMap<String, String> getApplicableSubjects() {
		return applicableSubjects;
	}

	public void setApplicableSubjects(HashMap<String, String> applicableSubjects) {
		this.applicableSubjects = applicableSubjects;
	}

	public StudentExamBean getRegData() {
		return regData;
	}

	public void setRegData(StudentExamBean regData) {
		this.regData = regData;
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

	public double getAcadSessionLiveOrder() {
		return acadSessionLiveOrder;
	}

	public void setAcadSessionLiveOrder(double acadSessionLiveOrder) {
		this.acadSessionLiveOrder = acadSessionLiveOrder;
	}

	public Map<String, Boolean> getCsAdmin() {
		return csAdmin;
	}

	public void setCsAdmin(Map<String, Boolean> csAdmin) {
		this.csAdmin = csAdmin;
	}

	public boolean isCourseraAccess() {
		return courseraAccess;
	}

	public void setCourseraAccess(boolean courseraAccess) {
		this.courseraAccess = courseraAccess;
	}

	@Override
	public String toString() {
		return "LoginSSO [userId=" + userId + ", student=" + student + ", announcements=" + announcements
				+ ", isStudent=" + isStudent + ", personDetails=" + personDetails + ", userBean=" + userBean
				+ ", validityExpired=" + validityExpired + ", applicableSubjects=" + applicableSubjects + ", regData="
				+ regData + ", regOrder=" + regOrder + ", maxOrderWhereContentLive=" + maxOrderWhereContentLive
				+ ", currentOrder=" + currentOrder + ", currentSemPSSId=" + currentSemPSSId + ", isLoginAsLead="
				+ isLoginAsLead + ", earlyAccess=" + earlyAccess + ", liveSessionPssIdAccess=" + liveSessionPssIdAccess
				+ ", featureViseAccess=" + featureViseAccess + ", consumerProgramStructureHasCSAccess="
				+ consumerProgramStructureHasCSAccess + ", acadSessionLiveOrder=" + acadSessionLiveOrder + ", csAdmin="
				+ csAdmin + ", courseraAccess=" + courseraAccess + "]";
	}
}
