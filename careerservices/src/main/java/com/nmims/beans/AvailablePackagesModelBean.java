package com.nmims.beans;

import java.io.Serializable;
import java.util.List;

public class AvailablePackagesModelBean  implements Serializable{
	
	//upgrade path details
	private List<UpgradePathDetails> upgradePathsAndPackageDetails;
	
	//other configurable details
	private TermsAndConditions termsAndConditions;
	private AboutCS aboutCS;
	
	
	public TermsAndConditions getTermsAndConditions() {
		return termsAndConditions;
	}
	public List<UpgradePathDetails> getUpgradePathsAndPackageDetails() {
		return upgradePathsAndPackageDetails;
	}
	public void setUpgradePathsAndPackageDetails(List<UpgradePathDetails> upgradePathsAndPackageDetails) {
		this.upgradePathsAndPackageDetails = upgradePathsAndPackageDetails;
	}
	public void setTermsAndConditions(TermsAndConditions termsAndConditions) {
		this.termsAndConditions = termsAndConditions;
	}
	public AboutCS getAboutCS() {
		return aboutCS;
	}
	public void setAboutCS(AboutCS aboutCS) {
		this.aboutCS = aboutCS;
	}
}
