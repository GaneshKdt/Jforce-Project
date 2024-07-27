package com.nmims.beans;

import java.io.Serializable;
import java.util.List;

public class OpenBadgesCriteriaBean extends OpenBadgeBean  implements Serializable {
private Integer criteriaId;
private Integer criteriatype;
private String criteriaDescription;

private List<OpenBadgesCriteriaParamBean> openBadgesCriteriaParamBeanList;



@Override
public String toString() {
	return "OpenBadgesCriteriaBean [criteriaId=" + criteriaId + ", criteriatype=" + criteriatype
			+ ", criteriaDescription=" + criteriaDescription + ", openBadgesCriteriaParamBeanList="
			+ openBadgesCriteriaParamBeanList + "]";
}
public Integer getCriteriaId() {
	return criteriaId;
}
public void setCriteriaId(Integer criteriaId) {
	this.criteriaId = criteriaId;
}
public Integer getCriteriatype() {
	return criteriatype;
}
public void setCriteriatype(Integer criteriatype) {
	this.criteriatype = criteriatype;
}
public String getCriteriaDescription() {
	return criteriaDescription;
}
public void setCriteriaDescription(String criteriaDescription) {
	this.criteriaDescription = criteriaDescription;
}
public List<OpenBadgesCriteriaParamBean> getOpenBadgesCriteriaParamBeanList() {
	return openBadgesCriteriaParamBeanList;
}
public void setOpenBadgesCriteriaParamBeanList(List<OpenBadgesCriteriaParamBean> openBadgesCriteriaParamBeanList) {
	this.openBadgesCriteriaParamBeanList = openBadgesCriteriaParamBeanList;
}


}
