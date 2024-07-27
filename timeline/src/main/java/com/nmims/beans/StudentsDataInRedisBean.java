package com.nmims.beans;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

public class StudentsDataInRedisBean implements Serializable{

	private static final long serialVersionUID = 1L;

	private Map<String,List> resultsData;
	
	private String sapid;
	
	
	
	public String getSapid() {
		return sapid;
	}

	public void setSapid(String sapid) {
		this.sapid = sapid;
	}

	public Map<String, List> getResultsData() {
		return resultsData;
	}

	public void setResultsData(Map<String, List> resultsData) {
		this.resultsData = resultsData;
	}

	@Override
	public String toString() {
		return "StudentsDataInRedisBean [resultsData=" + resultsData + ", sapid=" + sapid + "]";
	}
	
	
}
