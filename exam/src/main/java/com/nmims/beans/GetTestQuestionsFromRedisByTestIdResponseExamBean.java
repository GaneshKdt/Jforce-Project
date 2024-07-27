package com.nmims.beans;

import java.io.Serializable;
import java.util.List;

//spring security related changes rename GetTestQuestionsFromRedisByTestIdResponseBean to GetTestQuestionsFromRedisByTestIdResponseExamBean
public class GetTestQuestionsFromRedisByTestIdResponseExamBean  implements Serializable{

	private static final long serialVersionUID = 1L;
	private List<TestQuestionExamBean>testQuestions;
	private TestExamBean test;
	
	
	
	public TestExamBean getTest() {
		return test;
	}

	public void setTest(TestExamBean test) {
		this.test = test;
	}

	public List<TestQuestionExamBean> getTestQuestions() {
		return testQuestions;
	}

	public void setTestQuestions(List<TestQuestionExamBean> testQuestions) {
		this.testQuestions = testQuestions;
	}
	
	
	
}
