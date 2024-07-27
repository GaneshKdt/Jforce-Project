package com.nmims.beans;

import java.io.Serializable;

public class SalesForceCreatePackageRequest implements Serializable {
	
	private String packageName;
	private String type;
	
	public String getPackageName() {
		return packageName;
	}
	public void setPackageName(String packageName) {
		this.packageName = packageName;
	}
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
}
