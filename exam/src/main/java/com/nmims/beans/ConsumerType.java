package com.nmims.beans;

import java.io.Serializable;

public class ConsumerType  implements Serializable {
	private String id;
	private String name;
	private String isCorporate;
	
	private String createdBy;
	private String lastModifiedBy;
	
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getIsCorporate() {
		return isCorporate;
	}
	public void setIsCorporate(String isCorporate) {
		this.isCorporate = isCorporate;
	}
	public String getCreatedBy() {
		return createdBy;
	}
	public void setCreatedBy(String createdBy) {
		this.createdBy = createdBy;
	}
	public String getLastModifiedBy() {
		return lastModifiedBy;
	}
	public void setLastModifiedBy(String lastModifiedBy) {
		this.lastModifiedBy = lastModifiedBy;
	}
	
	
	

}
