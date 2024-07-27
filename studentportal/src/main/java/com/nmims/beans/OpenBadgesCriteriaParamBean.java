package com.nmims.beans;

import java.io.Serializable;

public class OpenBadgesCriteriaParamBean  extends  OpenBadgesCriteriaBean   implements Serializable  {

	private Integer criteriaParamId;
	private String criteriaName;
	private String criteriaValue;
	
	
	
	
	@Override
	public String toString() {
		return "OpenBadgesCriteriaParamBean [criteriaParamId=" + criteriaParamId + ", criteriaName=" + criteriaName
				+ ", criteriaValue=" + criteriaValue + "]";
	}
	public Integer getCriteriaParamId() {
		return criteriaParamId;
	}
	public void setCriteriaParamId(Integer criteriaParamId) {
		this.criteriaParamId = criteriaParamId;
	}
	public String getCriteriaName() {
		return criteriaName;
	}
	public void setCriteriaName(String criteriaName) {
		this.criteriaName = criteriaName;
	}
	public String getCriteriaValue() {
		return criteriaValue;
	}
	public void setCriteriaValue(String criteriaValue) {
		this.criteriaValue = criteriaValue;
	}
	
	
}
