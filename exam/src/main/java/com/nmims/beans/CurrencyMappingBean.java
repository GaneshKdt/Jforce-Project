package com.nmims.beans;

import java.io.Serializable;
import java.util.ArrayList;

public class CurrencyMappingBean implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = -2689342405938618643L;
	
	private int id;
	private int feeId;
	private String feeName;
	private int consumerProgramStructureId;
	private int currencyId;
	private String currencyName;
	private Double price;
	private String status;
	private String consumerType;
	private String program;
	private String programStructure;
	private String programId;
	private String programStructureId;
	private String consumerTypeId;
	
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	
	public int getFeeId() {
		return feeId;
	}
	public void setFeeId(int feeId) {
		this.feeId = feeId;
	}
	public int getCurrencyId() {
		return currencyId;
	}
	public void setCurrencyId(int currencyId) {
		this.currencyId = currencyId;
	}
	
	
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}
	
	public String getConsumerType() {
		return consumerType;
	}
	public void setConsumerType(String consumerType) {
		this.consumerType = consumerType;
	}
	public String getProgram() {
		return program;
	}
	public void setProgram(String program) {
		this.program = program;
	}
	
	public String getProgramStructure() {
		return programStructure;
	}
	public void setProgramStructure(String programStructure) {
		this.programStructure = programStructure;
	}
	public String getCurrencyName() {
		return currencyName;
	}
	public void setCurrencyName(String currencyName) {
		this.currencyName = currencyName;
	}
	
	public String getFeeName() {
		return feeName;
	}
	public void setFeeName(String feeName) {
		this.feeName = feeName;
	}
	
	
	public String getProgramId() {
		return programId;
	}
	public void setProgramId(String programId) {
		this.programId = programId;
	}
	public String getProgramStructureId() {
		return programStructureId;
	}
	public void setProgramStructureId(String programStructureId) {
		this.programStructureId = programStructureId;
	}
	
	public String getConsumerTypeId() {
		return consumerTypeId;
	}
	public void setConsumerTypeId(String consumerTypeId) {
		this.consumerTypeId = consumerTypeId;
	}
	public Double getPrice() {
		return price;
	}
	public void setPrice(Double price) {
		this.price = price;
	}
	public int getConsumerProgramStructureId() {
		return consumerProgramStructureId;
	}
	public void setConsumerProgramStructureId(int consumerProgramStructureId) {
		this.consumerProgramStructureId = consumerProgramStructureId;
	}
	@Override
	public String toString() {
		return "CurrencyMappingBean [id=" + id + ", feeId=" + feeId + ", feeName=" + feeName
				+ ", consumerProgramStructureId=" + consumerProgramStructureId + ", currencyId=" + currencyId
				+ ", currencyName=" + currencyName + ", price=" + price + ", status=" + status + ", consumerType="
				+ consumerType + ", program=" + program + ", programStructure=" + programStructure + ", programId="
				+ programId + ", programStructureId=" + programStructureId + ", consumerTypeId=" + consumerTypeId + "]";
	}
	
	
	
	
	
	
	

	
	
	
	
	
}
