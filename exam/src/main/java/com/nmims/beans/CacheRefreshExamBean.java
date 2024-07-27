package com.nmims.beans;

import java.io.Serializable;

//spring security related changes rename CacheRefreshBean to CacheRefreshExamBean
public class CacheRefreshExamBean  implements Serializable {

	/**
	 * For response Purpose only 
	 * */
	private String message;
	private String status;
	private int[] failSeverPorts;
	private int[] successServerPorts;
	
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
	public int[] getFailSeverPorts() {
		return failSeverPorts;
	}
	public void setFailSeverPorts(int[] failSeverPorts) {
		this.failSeverPorts = failSeverPorts;
	}
	public int[] getSuccessServerPorts() {
		return successServerPorts;
	}
	public void setSuccessServerPorts(int[] successServerPorts) {
		this.successServerPorts = successServerPorts;
	}
	
	
	
}
