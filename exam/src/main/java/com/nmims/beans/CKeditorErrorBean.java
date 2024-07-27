package com.nmims.beans;

import java.io.Serializable;

public class CKeditorErrorBean  implements Serializable {
	
	private String message;

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	@Override
	public String toString() {
		return "CKeditorErrorBean [message=" + message + "]";
	}
	
	
	
}
