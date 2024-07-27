package com.nmims.beans;

import java.io.Serializable;

public class SalesForceCreatePackageResponseObject implements Serializable {

	private SalesForcePackage packages;

	public SalesForcePackage getPackages() {
		return packages;
	}

	public void setPackages(SalesForcePackage packages) {
		this.packages = packages;
	}
}
