package com.nmims.timeline.model;

import java.io.Serializable;

public class TestExtendedSapidsCompositeKey  implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private String sapid;
	private Long testId;
	public String getSapid() {
		return sapid;
	}
	public void setSapid(String sapid) {
		this.sapid = sapid;
	}
	public Long getTestId() {
		return testId;
	}
	public void setTestId(Long testId) {
		this.testId = testId;
	} 
	
	
}
