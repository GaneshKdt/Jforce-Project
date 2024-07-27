package com.nmims.beans;

import java.io.Serializable;
import java.math.BigInteger;

public class OpenBadgesEvidenceBean extends OpenBadgesIssuedBean   implements Serializable  {
private BigInteger evidenceId;	
private String evidenceType;
private String evidenceValue;
private String createdBy;
private String lastModifiedBy;



@Override
public String toString() {
	return "OpenBadgesEvidenceBean [evidenceId=" + evidenceId + ", evidenceType=" + evidenceType + ", evidenceValue="
			+ evidenceValue + ", createdBy=" + createdBy + ", lastModifiedBy=" + lastModifiedBy + "]";
}

public BigInteger getEvidenceId() {
	return evidenceId;
}
public void setEvidenceId(BigInteger evidenceId) {
	this.evidenceId = evidenceId;
}
public String getEvidenceValue() {
	return evidenceValue;
}
public void setEvidenceValue(String evidenceValue) {
	this.evidenceValue = evidenceValue;
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

public String getEvidenceType() {
	return evidenceType;
}

public void setEvidenceType(String evidenceType) {
	this.evidenceType = evidenceType;
}




}
