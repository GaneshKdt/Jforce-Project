package com.nmims.timeline.model;

import java.io.Serializable;

public class StudentCompositeKey implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private String sapid;
	private String sem;
	
	public String getSapid() {
		return sapid;
	}
	public void setSapid(String sapid) {
		this.sapid = sapid;
	}
	public String getSem() {
		return sem;
	}
	public void setSem(String sem) {
		this.sem = sem;
	}
	
	
}
