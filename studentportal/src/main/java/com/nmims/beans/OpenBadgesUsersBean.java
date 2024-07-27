package com.nmims.beans;

import java.io.Serializable;
import java.math.BigInteger;
import java.util.List;

public class OpenBadgesUsersBean extends OpenBadgesIssuedBean   implements Serializable  {

private Integer userId;	
private String sapid;
private Integer consumerProgramStructureId;
private String firstname;
private String lastname;
private String emailId;
private Integer emailstop;
private String createdBy;
private String lastModifiedBy;

private List<OpenBadgesUsersBean> earnedBadgeList ;
private List<OpenBadgesUsersBean> claimedBadgeList ;
private List<OpenBadgesUsersBean> revokedBadgeList ;
private List<OpenBadgesUsersBean> lockedBadgeList ;


@Override
public String toString() {
	return "OpenBadgesUsersBean [userId=" + userId + ", sapid=" + sapid + ", consumerProgramStructureId="
			+ consumerProgramStructureId + ", firstname=" + firstname + ", lastname=" + lastname + ", emailId="
			+ emailId + ", emailstop=" + emailstop + ", createdBy=" + createdBy + ", lastModifiedBy=" + lastModifiedBy
			+ ", earnedBadgeList=" + earnedBadgeList + ", claimedBadgeList=" + claimedBadgeList + ", revokedBadgeList="
			+ revokedBadgeList + ", lockedBadgeList=" + lockedBadgeList + "]";
}

public Integer getUserId() {
	return userId;
}
public void setUserId(Integer userId) {
	this.userId = userId;
}
public String getSapid() {
	return sapid;
}
public void setSapid(String sapid) {
	this.sapid = sapid;
}
public Integer getConsumerProgramStructureId() {
	return consumerProgramStructureId;
}
public void setConsumerProgramStructureId(Integer consumerProgramStructureId) {
	this.consumerProgramStructureId = consumerProgramStructureId;
}
public String getFirstname() {
	return firstname;
}
public void setFirstname(String firstname) {
	this.firstname = firstname;
}
public String getLastname() {
	return lastname;
}
public void setLastname(String lastname) {
	this.lastname = lastname;
}
public String getEmailId() {
	return emailId;
}
public void setEmailId(String emailId) {
	this.emailId = emailId;
}
public Integer getEmailstop() {
	return emailstop;
}
public void setEmailstop(Integer emailstop) {
	this.emailstop = emailstop;
}
public String getCreatedBy() {
	return createdBy;
}
public void setCreatedBy(String createdBy) {
	this.createdBy = createdBy;
}
public String getLastModifiedBy() {
	return lastModifiedBy;
}
public void setLastModifiedBy(String lastModifiedBy) {
	this.lastModifiedBy = lastModifiedBy;
}

public List<OpenBadgesUsersBean> getEarnedBadgeList() {
	return earnedBadgeList;
}

public void setEarnedBadgeList(List<OpenBadgesUsersBean> earnedBadgeList) {
	this.earnedBadgeList = earnedBadgeList;
}

public List<OpenBadgesUsersBean> getClaimedBadgeList() {
	return claimedBadgeList;
}

public void setClaimedBadgeList(List<OpenBadgesUsersBean> claimedBadgeList) {
	this.claimedBadgeList = claimedBadgeList;
}

public List<OpenBadgesUsersBean> getLockedBadgeList() {
	return lockedBadgeList;
}

public void setLockedBadgeList(List<OpenBadgesUsersBean> lockedBadgeList) {
	this.lockedBadgeList = lockedBadgeList;
}

public List<OpenBadgesUsersBean> getRevokedBadgeList() {
	return revokedBadgeList;
}

public void setRevokedBadgeList(List<OpenBadgesUsersBean> revokedBadgeList) {
	this.revokedBadgeList = revokedBadgeList;
}




}
