package com.nmims.beans;

import java.io.Serializable;

//spring security related changes rename LinkedInAddCertToProfileBean to LinkedInAddCertToProfileExamBean
public class LinkedInAddCertToProfileExamBean  implements Serializable   {
	
	private String startTask;
	private String name;
	private String organizationId;
	private String issueYear;
	private String issueMonth;
	private String expirationYear;
	private String expirationMonth;
	private String certUrl;
	private String certId;
	private String consumerProgramStructureId;
	
	public String getConsumerProgramStructureId() {
		return consumerProgramStructureId;
	}

	public void setConsumerProgramStructureId(String consumerProgramStructureId) {
		this.consumerProgramStructureId = consumerProgramStructureId;
	}

	public String getOrganizationId() {
		return organizationId;
	}

	public void setOrganizationId(String organizationId) {
		this.organizationId = organizationId;
	}
	
	public String getName() {
		return name;
	}

	public void setName(String programName) {
		this.name = programName;
	}

	public String getIssueYear() {
		return issueYear;
	}

	public void setIssueYear(String issueYear) {
		this.issueYear = issueYear;
	}

	public String getIssueMonth() {
		return issueMonth;
	}

	public void setIssueMonth(String issueMonth) {
		this.issueMonth = issueMonth;
	}

	public String getExpirationYear() {
		return expirationYear;
	}

	public void setExpirationYear(String expirationYear) {
		this.expirationYear = expirationYear;
	}

	public String getExpirationMonth() {
		return expirationMonth;
	}

	public void setExpirationMonth(String expirationMonth) {
		this.expirationMonth = expirationMonth;
	}

	public String getCertUrl() {
		return certUrl;
	}

	public void setCertUrl(String certUrl) {
		this.certUrl = certUrl;
	}

	public String getCertId() {
		return certId;
	}

	public void setCertId(String certId) {
		this.certId = certId;
	}
	
	public String getStartTask() {
		return startTask;
	}

	public void setStartTask(String startTask) {
		this.startTask = startTask;
	}
}
