package com.nmims.beans;

import java.io.Serializable;

/**
 * old name - CacheRefreshBean
 * @author
 *
 */
public class CacheRefreshStudentPortalBean  implements Serializable {

	
	/**
	 * For response purpose
	 * */
	private String message;
	private String status;
	
	
	public String getMessage() {
		return message;
	}
	public void setMessage(String message) {
		this.message = message;
	}
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}
	
}
