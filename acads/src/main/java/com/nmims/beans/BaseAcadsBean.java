package com.nmims.beans;

import java.io.Serializable;
import java.util.Date;

public abstract class BaseAcadsBean implements Serializable{
	/**
	 * Change Name from BaseBean to BaseAcadsBean for serializable issue
	 */
	private Long id;
	
	private Date createdDate;
	
	private Date lastModifiedDate;
	
	private String createdBy;
	
	private String lastModifiedBy;
	
	
	private String consumerProgramStructureId;

	private String consumerTypeId;
	private String programStructureId;
	private String programId;
	

	private String consumerType;
	private String program;
	private String programStructure;
	private Integer countOfProgramsApplicableTo;
	
	private String allowedToUpdate;
	
	
	
	/**
	 * @return the allowedToUpdate
	 */
	public String getAllowedToUpdate() {
		return allowedToUpdate;
	}

	/**
	 * @param allowedToUpdate the allowedToUpdate to set
	 */
	public void setAllowedToUpdate(String allowedToUpdate) {
		this.allowedToUpdate = allowedToUpdate;
	}

	/**
	 * @return the programStructure
	 */
	public String getProgramStructure() {
		return programStructure;
	}

	/**
	 * @param programStructure the programStructure to set
	 */
	public void setProgramStructure(String programStructure) {
		this.programStructure = programStructure;
	}

	/**
	 * @return the consumerProgramStructureId
	 */
	public String getConsumerProgramStructureId() {
		return consumerProgramStructureId;
	}

	/**
	 * @param consumerProgramStructureId the consumerProgramStructureId to set
	 */
	public void setConsumerProgramStructureId(String consumerProgramStructureId) {
		this.consumerProgramStructureId = consumerProgramStructureId;
	}

	/**
	 * @return the consumerTypeId
	 */
	public String getConsumerTypeId() {
		return consumerTypeId;
	}

	/**
	 * @param consumerTypeId the consumerTypeId to set
	 */
	public void setConsumerTypeId(String consumerTypeId) {
		this.consumerTypeId = consumerTypeId;
	}

	/**
	 * @return the programStructureId
	 */
	public String getProgramStructureId() {
		return programStructureId;
	}

	/**
	 * @param programStructureId the programStructureId to set
	 */
	public void setProgramStructureId(String programStructureId) {
		this.programStructureId = programStructureId;
	}

	/**
	 * @return the programId
	 */
	public String getProgramId() {
		return programId;
	}

	/**
	 * @param programId the programId to set
	 */
	public void setProgramId(String programId) {
		this.programId = programId;
	}

	/**
	 * @return the consumerType
	 */
	public String getConsumerType() {
		return consumerType;
	}

	/**
	 * @param consumerType the consumerType to set
	 */
	public void setConsumerType(String consumerType) {
		this.consumerType = consumerType;
	}

	/**
	 * @return the program
	 */
	public String getProgram() {
		return program;
	}

	/**
	 * @param program the program to set
	 */
	public void setProgram(String program) {
		this.program = program;
	}

	/**
	 * @return the countOfProgramsApplicableTo
	 */
	public Integer getCountOfProgramsApplicableTo() {
		return countOfProgramsApplicableTo;
	}

	/**
	 * @param countOfProgramsApplicableTo the countOfProgramsApplicableTo to set
	 */
	public void setCountOfProgramsApplicableTo(Integer countOfProgramsApplicableTo) {
		this.countOfProgramsApplicableTo = countOfProgramsApplicableTo;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Date getCreatedDate() {
		return createdDate;
	}

	public void setCreatedDate(Date createdDate) {
		this.createdDate = createdDate;
	}

	public Date getLastModifiedDate() {
		return lastModifiedDate;
	}

	public void setLastModifiedDate(Date lastModifiedDate) {
		this.lastModifiedDate = lastModifiedDate;
	}

	public String getCreatedBy() {
		return createdBy;
	}

	public void setCreatedBy( String createdBy) {
		this.createdBy = createdBy;
	}

	public  String getLastModifiedBy() {
		return lastModifiedBy;
	}

	public void setLastModifiedBy( String lastModifiedBy) {
		this.lastModifiedBy = lastModifiedBy;
	}

	

	//Added for test module Start
	protected String compareStringAndSet(String value, String compare, String defaultValue) {
		return compare.equalsIgnoreCase(value) ? compare : defaultValue;
	}
	
	protected String checkYElseSetN(String value) {
		return compareStringAndSet(value, "Y", "N");
	}
	protected String formatDate(String date) {
		if(null == date) return date;
		if(date.length() > 19) {
			return date.substring(0, 19).replace(' ', 'T');
		} else {
			return date.replace(' ', 'T');
		}
	}
	//Added for test module End
}
