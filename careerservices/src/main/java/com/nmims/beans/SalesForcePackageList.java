package com.nmims.beans;

import java.io.Serializable;
import java.util.List;

public class SalesForcePackageList implements Serializable {

	private List<SalesForcePackage> packageList;

	public List<SalesForcePackage> getPackageList() {
		return packageList;
	}

	public void setPackageList(List<SalesForcePackage> packageList) {
		this.packageList = packageList;
	}
}
