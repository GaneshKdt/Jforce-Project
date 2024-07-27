package com.nmims.timeline.model;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.IdClass;
import javax.persistence.Table;

@Entity
@Table(name="exam.upgrad_test_testextended_sapids", schema="exam")
@IdClass(TestExtendedSapidsCompositeKey.class)
public class MBAXTestExtendedSapids {
	private static final long serialVersionUID = 1L;

	@Id
	private String sapid;

	@Id
	private Long testId; 
	private String extendedStartTime; 
	private String extendedEndTime;
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
	public String getExtendedStartTime() {
		return extendedStartTime;
	}
	public void setExtendedStartTime(String extendedStartTime) {
		this.extendedStartTime = extendedStartTime;
	}
	public String getExtendedEndTime() {
		return extendedEndTime;
	}
	public void setExtendedEndTime(String extendedEndTime) {
		this.extendedEndTime = extendedEndTime;
	}
	
	
	
}
