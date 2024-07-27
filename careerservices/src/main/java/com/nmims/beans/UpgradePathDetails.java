package com.nmims.beans;

import java.io.Serializable;
import java.util.List;

public class UpgradePathDetails implements Serializable {

	private UpgradePath upgradePath;
	
	//list of all package families
//	private List<PackageFamily> packageFamilies;
	
	//list of all the features available for this family
	private List<Feature> featuresAvailableForThisFamily;

	private List<PacakageAvailabilityBean> packages;

	public UpgradePath getUpgradePath() {
		return upgradePath;
	}

	public void setUpgradePath(UpgradePath upgradePath) {
		this.upgradePath = upgradePath;
	}

	public List<Feature> getFeaturesAvailableForThisFamily() {
		return featuresAvailableForThisFamily;
	}

	public void setFeaturesAvailableForThisFamily(List<Feature> featuresAvailableForThisFamily) {
		this.featuresAvailableForThisFamily = featuresAvailableForThisFamily;
	}

	public List<PacakageAvailabilityBean> getPackages() {
		return packages;
	}

	public void setPackages(List<PacakageAvailabilityBean> packages) {
		this.packages = packages;
	}
	
}
