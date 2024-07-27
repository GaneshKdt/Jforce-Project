package com.nmims.beans;

import java.io.Serializable;
import java.math.BigInteger;
import java.sql.Timestamp;
import java.util.List;

public class OpenBadgesIssuedBean extends OpenBadgesCriteriaBean   implements Serializable  {
private BigInteger issuedId;
private String uniquehash;
private String awardedAt;
private String dateissued;
private String dateexpire;
private Integer isClaimed;
private Integer isRevoked;
private String revocationReason;
private String createdBy;
private String lastModifiedBy;
private String url;

private LinkedInAddCertToProfileBean linkedInCredentials;


public String getUrl() {
	return url;
}

public void setUrl(String url) {
	this.url = url;
}

private Integer isBadgeIssued;

private List<OpenBadgesEvidenceBean> evidenceBeanList; 
private OpenBadgesEvidenceBean evidenceBean; 

private String awardedAtCode;
private String productType;

@Override
public String toString() {
	return "OpenBadgesIssuedBean [issuedId=" + issuedId + ", uniquehash=" + uniquehash + ", awardedAt=" + awardedAt
			+ ", dateissued=" + dateissued + ", dateexpire=" + dateexpire + ", isClaimed=" + isClaimed + ", isRevoked="
			+ isRevoked + ", revocationReason=" + revocationReason + ", createdBy=" + createdBy + ", lastModifiedBy="
			+ lastModifiedBy + ", url=" + url + ", linkedInCredentials=" + linkedInCredentials + ", isBadgeIssued="
			+ isBadgeIssued + ", evidenceBeanList=" + evidenceBeanList + ", evidenceBean=" + evidenceBean
			+ ", awardedAtCode=" + awardedAtCode + ", productType=" + productType + "]";
}

public BigInteger getIssuedId() {
	return issuedId;
}
public void setIssuedId(BigInteger issuedId) {
	this.issuedId = issuedId;
}
public String getUniquehash() {
	return uniquehash;
}
public void setUniquehash(String uniquehash) {
	this.uniquehash = uniquehash;
}
public String getAwardedAt() {
	return awardedAt;
}
public void setAwardedAt(String awardedAt) {
	this.awardedAt = awardedAt;
}

public Integer getIsClaimed() {
	return isClaimed;
}
public void setIsClaimed(Integer isClaimed) {
	this.isClaimed = isClaimed;
}
public Integer getIsRevoked() {
	return isRevoked;
}
public void setIsRevoked(Integer isRevoked) {
	this.isRevoked = isRevoked;
}
public String getRevocationReason() {
	return revocationReason;
}
public void setRevocationReason(String revocationReason) {
	this.revocationReason = revocationReason;
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

public List<OpenBadgesEvidenceBean> getEvidenceBeanList() {
	return evidenceBeanList;
}

public void setEvidenceBeanList(List<OpenBadgesEvidenceBean> evidenceBeanList) {
	this.evidenceBeanList = evidenceBeanList;
}

public Integer getIsBadgeIssued() {
	return isBadgeIssued;
}

public void setIsBadgeIssued(Integer isBadgeIssued) {
	this.isBadgeIssued = isBadgeIssued;
}

public String getDateissued() {
	return dateissued;
}

public void setDateissued(String dateissued) {
	this.dateissued = dateissued;
}

public String getDateexpire() {
	return dateexpire;
}

public void setDateexpire(String dateexpire) {
	this.dateexpire = dateexpire;
}

public String getAwardedAtCode() {
	return awardedAtCode;
}

public void setAwardedAtCode(String awardedAtCode) {
	this.awardedAtCode = awardedAtCode;
}

public OpenBadgesEvidenceBean getEvidenceBean() {
	return evidenceBean;
}

public void setEvidenceBean(OpenBadgesEvidenceBean evidenceBean) {
	this.evidenceBean = evidenceBean;
}

public String getProductType() {
	return productType;
}

public void setProductType(String productType) {
	this.productType = productType;
}

public LinkedInAddCertToProfileBean getLinkedInCredentials() {
	return linkedInCredentials;
}

public void setLinkedInCredentials(LinkedInAddCertToProfileBean linkedInCredentials) {
	this.linkedInCredentials = linkedInCredentials;
}




}
