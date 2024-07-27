package com.nmims.dto;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.nmims.beans.UserAuthorizationBean;
import com.nmims.beans.AnnouncementAcadsBean;
import com.nmims.beans.StudentAcadsBean;
import com.nmims.beans.PersonAcads;
import com.nmims.beans.ELearnResourcesAcadsBean;
public class LoginSSODto 
{
	private StudentAcadsBean student;
	
	private ArrayList<AnnouncementAcadsBean> announcements;
	
	private String userId;
	
	private String isStudent;
	
	private PersonAcads personDetails;
	
//	private UserAuthorizationBean userBean;
	
	private String validityExpired;
	
	private HashMap<String,String> applicableSubjects;
	
	private StudentAcadsBean regData;
	
	private ELearnResourcesAcadsBean stukent;
	
	private ELearnResourcesAcadsBean harvard;
	
	private double regOrder;
	
	private double maxOrderWhereContentLive;
	
	private double currentOrder;
	
	private List<Integer> currentSemPSSId;
	
	private String isLoginAsLead;
	
	private String earlyAccess;
	
	Map<String, Boolean> featureViseAccess;

	private List<Integer> liveSessionPssIdAccess;
	
	private boolean consumerProgramStructureHasCSAccess;
	
	private double acadSessionLiveOrder;
	
	Map<String, Boolean> csAdmin;

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


	public StudentAcadsBean getStudent() {
		return student;
	}


	public void setStudent(StudentAcadsBean student) {
		this.student = student;
	}


	public ArrayList<AnnouncementAcadsBean> getAnnouncements() {
		return announcements;
	}


	public void setAnnouncements(ArrayList<AnnouncementAcadsBean> announcements) {
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


	public PersonAcads getPersonDetails() {
		return personDetails;
	}


	public void setPersonDetails(PersonAcads personDetails) {
		this.personDetails = personDetails;
	}


	/*public UserAuthorizationBean getUserBean() {
		return userBean;
	}


	public void setUserBean(UserAuthorizationBean userBean) {
		this.userBean = userBean;
	}
*/

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

	public StudentAcadsBean getRegData() {
		return regData;
	}

	public void setRegData(StudentAcadsBean regData) {
		this.regData = regData;
	}

	public ELearnResourcesAcadsBean getStukent() {
		return stukent;
	}

	public void setStukent(ELearnResourcesAcadsBean stukent) {
		this.stukent = stukent;
	}

	public ELearnResourcesAcadsBean getHarvard() {
		return harvard;
	}

	public void setHarvard(ELearnResourcesAcadsBean harvard) {
		this.harvard = harvard;
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

	@Override
	public String toString() {
		return "LoginSSODto [student=" + student + ", announcements=" + announcements + ", userId=" + userId
				+ ", isStudent=" + isStudent + ", personDetails=" + personDetails + ", validityExpired="
				+ validityExpired + ", applicableSubjects=" + applicableSubjects + ", regData=" + regData + ", stukent="
				+ stukent + ", harvard=" + harvard + ", regOrder=" + regOrder + ", maxOrderWhereContentLive="
				+ maxOrderWhereContentLive + ", currentOrder=" + currentOrder + ", currentSemPSSId=" + currentSemPSSId
				+ ", isLoginAsLead=" + isLoginAsLead + ", earlyAccess=" + earlyAccess + ", featureViseAccess="
				+ featureViseAccess + ", liveSessionPssIdAccess=" + liveSessionPssIdAccess
				+ ", consumerProgramStructureHasCSAccess=" + consumerProgramStructureHasCSAccess
				+ ", acadSessionLiveOrder=" + acadSessionLiveOrder + ", csAdmin=" + csAdmin + "]";
	}




}
