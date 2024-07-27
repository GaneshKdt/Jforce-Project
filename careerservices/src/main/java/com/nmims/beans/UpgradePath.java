package com.nmims.beans;

import java.io.Serializable;
import java.util.List;

public class UpgradePath implements Serializable {

	private String pathId;
	private String pathName;
	private String numberOfFamilies;
	
	private List<PackageFamily> families;
	
	public List<PackageFamily> getFamilies() {
		return families;
	}
	public void setFamilies(List<PackageFamily> families) {
		this.families = families;
	}
	public String getPathId() {
		return pathId;
	}
	public void setPathId(String pathId) {
		this.pathId = pathId;
	}
	public String getPathName() {
		return pathName;
	}
	public void setPathName(String pathName) {
		this.pathName = pathName;
	}
	public String getNumberOfFamilies() {
		return numberOfFamilies;
	}
	public void setNumberOfFamilies(String numberOfFamilies) {
		this.numberOfFamilies = numberOfFamilies;
	}
}
