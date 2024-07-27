package com.nmims.beans;

import java.io.Serializable;
import java.util.List;

public class ReadCopyCasesListBean implements Serializable{
	
	private List<ResultDomain> unique1CCList;
	private List<ResultDomain> above90CCList;
	private List<ResultDomain> unique2CCList;
	private List<ResultDomain> detailedThreshold1CClist;
	private List<ResultDomain> detailedThreshold2CClist;
	private String errorMessage;
	
	public List<ResultDomain> getDetailedThreshold1CClist() {
		return detailedThreshold1CClist;
	}
	public void setDetailedThreshold1CClist(List<ResultDomain> detailedThreshold1CClist) {
		this.detailedThreshold1CClist = detailedThreshold1CClist;
	}
	public List<ResultDomain> getDetailedThreshold2CClist() {
		return detailedThreshold2CClist;
	}
	public void setDetailedThreshold2CClist(List<ResultDomain> detailedThreshold2CClist) {
		this.detailedThreshold2CClist = detailedThreshold2CClist;
	}
	public List<ResultDomain> getUnique1CCList() {
		return unique1CCList;
	}
	public void setUnique1CCList(List<ResultDomain> unique1ccList) {
		unique1CCList = unique1ccList;
	}
	public List<ResultDomain> getAbove90CCList() {
		return above90CCList;
	}
	public void setAbove90CCList(List<ResultDomain> above90ccList) {
		above90CCList = above90ccList;
	}
	public List<ResultDomain> getUnique2CCList() {
		return unique2CCList;
	}
	public void setUnique2CCList(List<ResultDomain> unique2ccList) {
		unique2CCList = unique2ccList;
	}
	public String getErrorMessage() {
		return errorMessage;
	}
	public void setErrorMessage(String errorMessage) {
		this.errorMessage = errorMessage;
	}
	
	@Override
	public String toString() {
		return "ReadCopyCasesListBean [unique1CCList=" + unique1CCList + ", above90CCList=" + above90CCList
				+ ", unique2CCList=" + unique2CCList + ", detailedThreshold1CClist=" + detailedThreshold1CClist
				+ ", detailedThreshold2CClist=" + detailedThreshold2CClist + ", errorMessage=" + errorMessage + "]";
	}

}
