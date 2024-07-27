package com.nmims.beans;

import java.io.Serializable;
import java.util.List;

public class ReExamEligibleStudentsResponseBean  implements Serializable  {
	private List<ReExamEligibleStudentBean> listOfFailedOrResitStudentResults;

	private String productType;
	private String error;
	private String authorizedCenterCodes;
	private String examYear;
	private String examMonth;
	
	public List<ReExamEligibleStudentBean> getListOfFailedOrResitStudentResults() {
		return listOfFailedOrResitStudentResults;
	}
	public void setListOfFailedOrResitStudentResults(List<ReExamEligibleStudentBean> listOfFailedOrResitStudentResults) {
		this.listOfFailedOrResitStudentResults = listOfFailedOrResitStudentResults;
	}
	public String getProductType() {
		return productType;
	}
	public void setProductType(String productType) {
		this.productType = productType;
	}
	public String getError() {
		return error;
	}
	public void setError(String error) {
		this.error = error;
	}
	public String getAuthorizedCenterCodes() {
		return authorizedCenterCodes;
	}
	public void setAuthorizedCenterCodes(String authorizedCenterCodes) {
		this.authorizedCenterCodes = authorizedCenterCodes;
	}
	public String getExamYear() {
		return examYear;
	}
	public void setExamYear(String examYear) {
		this.examYear = examYear;
	}
	public String getExamMonth() {
		return examMonth;
	}
	public void setExamMonth(String examMonth) {
		this.examMonth = examMonth;
	}
}
