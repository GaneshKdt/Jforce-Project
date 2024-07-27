package com.nmims.beans;

import java.io.Serializable;
import java.util.List;

public class GetIAResultsBySapidCollectionBean  implements Serializable   {

	private List<GetIAResultsBySapidResponseBean> subjects;

	public List<GetIAResultsBySapidResponseBean> getSubjects() {
		return subjects;
	}

	public void setSubjects(List<GetIAResultsBySapidResponseBean> subjects) {
		this.subjects = subjects;
	}
	
	
	
	
}
