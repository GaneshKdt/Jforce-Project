package com.nmims.beans;

import java.io.Serializable;
import java.util.List;

public class GetTestsBySapidResponseBean  implements Serializable {

	private List<TestExamBean> tests;

	public List<TestExamBean> getTests() {
		return tests;
	}

	public void setTests(List<TestExamBean> tests) {
		this.tests = tests;
	}
	
	
	
	
}
