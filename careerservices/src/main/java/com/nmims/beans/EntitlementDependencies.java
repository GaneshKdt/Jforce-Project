package com.nmims.beans;

import java.io.Serializable;
import java.util.List;

public class EntitlementDependencies  implements Serializable{
	private List<EntitlementDependency> dependencies;

	public List<EntitlementDependency> getDependencies() {
		return dependencies;
	}

	public void setDependencies(List<EntitlementDependency> dependencies) {
		this.dependencies = dependencies;
	}
	
}
