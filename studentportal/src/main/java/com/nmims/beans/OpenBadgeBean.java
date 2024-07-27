package com.nmims.beans;

import java.io.Serializable;
import java.util.List;

public class OpenBadgeBean  implements Serializable{
private Integer badgeId;
private String badgeName;
private String badgeDescription;
private String issuername;
private String issuerurl;
private String issuercontact;
private String expiredate;
private String expireperiod;

private Integer courseid;
private String message;
private String messagesubject;
private String attachment;
private Integer notification;
private Integer status;
private String version;
private String imageauthorname;
private String imageauthoremail;
private String imageauthorurl;
private String imagecaption;


private String createdBy;
private String lastModifiedBy;

private Integer totatlBadges ;
private Integer issuedCount ;
private Integer notIssuedCount ;
private Integer claimedCount ;
private Integer notClaimedCount ;
private Integer revokedCount ;


private List<OpenBadgesIssuedBean> openBadgesIssuedBeanList;

@Override
public String toString() {
	return "OpenBadgeBean [badgeId=" + badgeId + ", badgeName=" + badgeName + ", badgeDescription=" + badgeDescription
			+ ", issuername=" + issuername + ", issuerurl=" + issuerurl + ", issuercontact=" + issuercontact
			+ ", expiredate=" + expiredate + ", expireperiod=" + expireperiod + ", courseid=" + courseid + ", message="
			+ message + ", messagesubject=" + messagesubject + ", attachment=" + attachment + ", notification="
			+ notification + ", status=" + status + ", version=" + version + ", imageauthorname=" + imageauthorname
			+ ", imageauthoremail=" + imageauthoremail + ", imageauthorurl=" + imageauthorurl + ", imagecaption="
			+ imagecaption + ", createdBy=" + createdBy + ", lastModifiedBy=" + lastModifiedBy + ", totatlBadges="
			+ totatlBadges + ", issuedCount=" + issuedCount + ", notIssuedCount=" + notIssuedCount + ", claimedCount="
			+ claimedCount + ", notClaimedCount=" + notClaimedCount + ", revokedCount=" + revokedCount
			+ ", openBadgesIssuedBeanList=" + openBadgesIssuedBeanList + "]";
}

public Integer getBadgeId() {
	return badgeId;
}
public void setBadgeId(Integer badgeId) {
	this.badgeId = badgeId;
}
public String getBadgeName() {
	return badgeName;
}
public void setBadgeName(String badgeName) {
	this.badgeName = badgeName;
}
public String getBadgeDescription() {
	return badgeDescription;
}
public void setBadgeDescription(String badgeDescription) {
	this.badgeDescription = badgeDescription;
}
public String getIssuername() {
	return issuername;
}
public void setIssuername(String issuername) {
	this.issuername = issuername;
}
public String getExpireperiod() {
	return expireperiod;
}
public void setExpireperiod(String expireperiod) {
	this.expireperiod = expireperiod;
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

public List<OpenBadgesIssuedBean> getOpenBadgesIssuedBeanList() {
	return openBadgesIssuedBeanList;
}

public void setOpenBadgesIssuedBeanList(List<OpenBadgesIssuedBean> openBadgesIssuedBeanList) {
	this.openBadgesIssuedBeanList = openBadgesIssuedBeanList;
}

public Integer getTotatlBadges() {
	return totatlBadges;
}

public void setTotatlBadges(Integer totatlBadges) {
	this.totatlBadges = totatlBadges;
}

public Integer getIssuedCount() {
	return issuedCount;
}

public void setIssuedCount(Integer issuedCount) {
	this.issuedCount = issuedCount;
}

public Integer getNotIssuedCount() {
	return notIssuedCount;
}

public void setNotIssuedCount(Integer notIssuedCount) {
	this.notIssuedCount = notIssuedCount;
}

public Integer getClaimedCount() {
	return claimedCount;
}

public void setClaimedCount(Integer claimedCount) {
	this.claimedCount = claimedCount;
}

public Integer getNotClaimedCount() {
	return notClaimedCount;
}

public void setNotClaimedCount(Integer notClaimedCount) {
	this.notClaimedCount = notClaimedCount;
}

public Integer getRevokedCount() {
	return revokedCount;
}

public void setRevokedCount(Integer revokedCount) {
	this.revokedCount = revokedCount;
}

public String getIssuerurl() {
	return issuerurl;
}

public void setIssuerurl(String issuerurl) {
	this.issuerurl = issuerurl;
}

public String getIssuercontact() {
	return issuercontact;
}

public void setIssuercontact(String issuercontact) {
	this.issuercontact = issuercontact;
}

public String getExpiredate() {
	return expiredate;
}

public void setExpiredate(String expiredate) {
	this.expiredate = expiredate;
}

public Integer getCourseid() {
	return courseid;
}

public void setCourseid(Integer courseid) {
	this.courseid = courseid;
}

public String getMessage() {
	return message;
}

public void setMessage(String message) {
	this.message = message;
}

public String getMessagesubject() {
	return messagesubject;
}

public void setMessagesubject(String messagesubject) {
	this.messagesubject = messagesubject;
}

public String getAttachment() {
	return attachment;
}

public void setAttachment(String attachment) {
	this.attachment = attachment;
}

public Integer getNotification() {
	return notification;
}

public void setNotification(Integer notification) {
	this.notification = notification;
}

public Integer getStatus() {
	return status;
}

public void setStatus(Integer status) {
	this.status = status;
}

public String getVersion() {
	return version;
}

public void setVersion(String version) {
	this.version = version;
}

public String getImageauthorname() {
	return imageauthorname;
}

public void setImageauthorname(String imageauthorname) {
	this.imageauthorname = imageauthorname;
}

public String getImageauthoremail() {
	return imageauthoremail;
}

public void setImageauthoremail(String imageauthoremail) {
	this.imageauthoremail = imageauthoremail;
}

public String getImageauthorurl() {
	return imageauthorurl;
}

public void setImageauthorurl(String imageauthorurl) {
	this.imageauthorurl = imageauthorurl;
}

public String getImagecaption() {
	return imagecaption;
}

public void setImagecaption(String imagecaption) {
	this.imagecaption = imagecaption;
}



}
