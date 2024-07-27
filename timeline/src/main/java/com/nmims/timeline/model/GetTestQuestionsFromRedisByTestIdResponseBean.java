package com.nmims.timeline.model;

import java.io.Serializable;
import java.util.List;

public class GetTestQuestionsFromRedisByTestIdResponseBean  implements Serializable{

	private static final long serialVersionUID = 1L;
	private List<TestQuestionBean> testQuestions;
	private TestBean test;
	
	
	
	public TestBean getTest() {
		return test;
	}

	public void setTest(TestBean test) {
		this.test = test;
	}

	public List<TestQuestionBean> getTestQuestions() {
		return testQuestions;
	}

	public void setTestQuestions(List<TestQuestionBean> testQuestions) {
		this.testQuestions = testQuestions;
	}
	
	
	
}
