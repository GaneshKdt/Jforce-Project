package com.nmims.beans;

import java.io.Serializable;
import java.util.List;

public class GetIAResultsBySapidResponseBean  implements Serializable   {
	
	private TestExamBean subjectDetails;
	
	private List<StudentsTestDetailsExamBean> attemptedTestsList;

	private MBAWXPassFailStatus attemptedTestDetails;
	
	public TestExamBean getSubjectDetails() {
		return subjectDetails;
	}

	public void setSubjectDetails(TestExamBean subjectDetails) {
		this.subjectDetails = subjectDetails;
	}

	public List<StudentsTestDetailsExamBean> getAttemptedTestsList() {
		return attemptedTestsList;
	}

	public void setAttemptedTestsList(List<StudentsTestDetailsExamBean> attemptedTestsList) {
		this.attemptedTestsList = attemptedTestsList;
	}

	public MBAWXPassFailStatus getAttemptedTestDetails() {
		return attemptedTestDetails;
	}

	public void setAttemptedTestDetails(MBAWXPassFailStatus attemptedTestDetails) {
		this.attemptedTestDetails = attemptedTestDetails;
	}
	
	
}
