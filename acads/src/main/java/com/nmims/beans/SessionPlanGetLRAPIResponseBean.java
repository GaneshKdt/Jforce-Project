package com.nmims.beans;

import java.io.Serializable;
import java.util.List;

public class SessionPlanGetLRAPIResponseBean  implements Serializable  {

	private List<ContentAcadsBean> learningResources;

	public List<ContentAcadsBean> getLearningResources() {
		return learningResources;
	}

	public void setLearningResources(List<ContentAcadsBean> learningResources) {
		this.learningResources = learningResources;
	}
	
	
}
