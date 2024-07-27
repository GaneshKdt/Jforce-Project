package com.nmims.beans;

import java.io.Serializable;
import java.util.List;

public class CSHomeModelBean  implements Serializable{
	private TermsAndConditions termsAndConditions;
	private List<CSHomePackageInfo> packages;
	private List<VideoContentCareerservicesBean> orientationVideos;
	
	public TermsAndConditions getTermsAndConditions() {
		return termsAndConditions;
	}
	public void setTermsAndConditions(TermsAndConditions termsAndConditions) {
		this.termsAndConditions = termsAndConditions;
	}
	public List<CSHomePackageInfo> getPackages() {
		return packages;
	}
	public void setPackages(List<CSHomePackageInfo> packages) {
		this.packages = packages;
	}
	public List<VideoContentCareerservicesBean> getOrientationVideos() {
		return orientationVideos;
	}
	public void setOrientationVideos(List<VideoContentCareerservicesBean> orientationVideos) {
		this.orientationVideos = orientationVideos;
	}
}
