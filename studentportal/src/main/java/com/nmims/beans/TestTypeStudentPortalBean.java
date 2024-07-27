package com.nmims.beans;

import java.io.Serializable;

/**
 * old name - TestTypeBean
 * @author
 *
 */
public class TestTypeStudentPortalBean  implements Serializable {

	private Long id;
	private String type;
	private String isEditable;
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	public String getIsEditable() {
		return isEditable;
	}
	public void setIsEditable(String isEditable) {
		this.isEditable = isEditable;
	}
	
	
}
