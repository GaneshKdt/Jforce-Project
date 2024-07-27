package com.nmims.beans;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class ViewTestDetailsForStudentsAPIResponse implements Serializable {
	/*
	 * 
	 * 	m.addAttribute("assignmentPaymentPending","true");
		m.addAttribute("test", test);
		m.addAttribute("studentsTestDetails", studentsTestDetails);
		m.addAttribute("messageDetails", message);
		m.addAttribute("attemptsDetails", attemptsDetails);
		m.addAttribute("attemptNoNQuestionsMap", attemptNoNQuestionsMap);
		m.addAttribute("continueAttempt", continueAttempt);
		m.addAttribute("subject",test.getSubject());
		m.addAttribute("paymentPendingForSecondOrHigherAttempt", paymentPendingForSecondOrHigherAttempt);
		
	 * */
	private String assignmentPaymentPending;
	private TestExamBean test;
	private StudentsTestDetailsExamBean studentsTestDetails;
	private String messageDetails;
	private List<StudentsTestDetailsExamBean> attemptsDetails;
	private Map<Integer, List<TestQuestionExamBean>> attemptNoNQuestionsMap ;
	private String continueAttempt;
	private String subject;
	private String paymentPendingForSecondOrHigherAttempt;
	
	private boolean showStartTestButton;
	
	private ArrayList<TestQuestionExamBean> attemptDetail1;
	private ArrayList<TestQuestionExamBean> attemptDetail2;
	
	private List<Integer> bodQuestions;
	
	public ArrayList<TestQuestionExamBean> getAttemptDetail1() {
		return attemptDetail1;
	}



	public void setAttemptDetail1(ArrayList<TestQuestionExamBean> attemptDetail1) {
		this.attemptDetail1 = attemptDetail1;
	}



	public ArrayList<TestQuestionExamBean> getAttemptDetail2() {
		return attemptDetail2;
	}



	public void setAttemptDetail2(ArrayList<TestQuestionExamBean> attemptDetail2) {
		this.attemptDetail2 = attemptDetail2;
	}



	public ArrayList<TestQuestionExamBean> getAttemptDetail3() {
		return attemptDetail3;
	}



	public void setAttemptDetail3(ArrayList<TestQuestionExamBean> attemptDetail3) {
		this.attemptDetail3 = attemptDetail3;
	}

	private ArrayList<TestQuestionExamBean> attemptDetail3;
	
	
	
	public boolean isShowStartTestButton() {
		return showStartTestButton;
	}



	public void setShowStartTestButton(boolean showStartTestButton) {
		this.showStartTestButton = showStartTestButton;
	}



	/**
	 * @return the assignmentPaymentPending
	 */
	public String getAssignmentPaymentPending() {
		return assignmentPaymentPending;
	}
	
	
	
	/**
	 * @param assignmentPaymentPending the assignmentPaymentPending to set
	 */
	public void setAssignmentPaymentPending(String assignmentPaymentPending) {
		this.assignmentPaymentPending = assignmentPaymentPending;
	}
	/**
	 * @return the test
	 */
	public TestExamBean getTest() {
		return test;
	}
	/**
	 * @param test the test to set
	 */
	public void setTest(TestExamBean test) {
		this.test = test;
	}
	/**
	 * @return the studentsTestDetails
	 */
	public StudentsTestDetailsExamBean getStudentsTestDetails() {
		return studentsTestDetails;
	}
	/**
	 * @param studentsTestDetails the studentsTestDetails to set
	 */
	public void setStudentsTestDetails(StudentsTestDetailsExamBean studentsTestDetails) {
		this.studentsTestDetails = studentsTestDetails;
	}
	/**
	 * @return the messageDetails
	 */
	public String getMessageDetails() {
		return messageDetails;
	}
	/**
	 * @param messageDetails the messageDetails to set
	 */
	public void setMessageDetails(String messageDetails) {
		this.messageDetails = messageDetails;
	}
	/**
	 * @return the attemptsDetails
	 */
	public List<StudentsTestDetailsExamBean> getAttemptsDetails() {
		return attemptsDetails;
	}
	/**
	 * @param attemptsDetails the attemptsDetails to set
	 */
	public void setAttemptsDetails(List<StudentsTestDetailsExamBean> attemptsDetails) {
		this.attemptsDetails = attemptsDetails;
	}
	/**
	 * @return the attemptNoNQuestionsMap
	 */
	public Map<Integer, List<TestQuestionExamBean>> getAttemptNoNQuestionsMap() {
		return attemptNoNQuestionsMap;
	}
	/**
	 * @param attemptNoNQuestionsMap the attemptNoNQuestionsMap to set
	 */
	public void setAttemptNoNQuestionsMap(Map<Integer, List<TestQuestionExamBean>> attemptNoNQuestionsMap) {
		this.attemptNoNQuestionsMap = attemptNoNQuestionsMap;
	}
	/**
	 * @return the continueAttempt
	 */
	public String getContinueAttempt() {
		return continueAttempt;
	}
	/**
	 * @param continueAttempt the continueAttempt to set
	 */
	public void setContinueAttempt(String continueAttempt) {
		this.continueAttempt = continueAttempt;
	}
	/**
	 * @return the subject
	 */
	public String getSubject() {
		return subject;
	}
	/**
	 * @param subject the subject to set
	 */
	public void setSubject(String subject) {
		this.subject = subject;
	}
	/**
	 * @return the paymentPendingForSecondOrHigherAttempt
	 */
	public String getPaymentPendingForSecondOrHigherAttempt() {
		return paymentPendingForSecondOrHigherAttempt;
	}
	/**
	 * @param paymentPendingForSecondOrHigherAttempt the paymentPendingForSecondOrHigherAttempt to set
	 */
	public void setPaymentPendingForSecondOrHigherAttempt(String paymentPendingForSecondOrHigherAttempt) {
		this.paymentPendingForSecondOrHigherAttempt = paymentPendingForSecondOrHigherAttempt;
	}

	public List<Integer> getBodQuestions() {
		return bodQuestions;
	}

	public void setBodQuestions(List<Integer> bodQuestions) {
		this.bodQuestions = bodQuestions;
	}


	@Override
	public String toString() {
		return "ViewTestDetailsForStudentsAPIResponse [assignmentPaymentPending=" + assignmentPaymentPending + ", test="
				+ test + ", studentsTestDetails=" + studentsTestDetails + ", messageDetails=" + messageDetails
				+ ", attemptsDetails=" + attemptsDetails + ", attemptNoNQuestionsMap=" + attemptNoNQuestionsMap
				+ ", continueAttempt=" + continueAttempt + ", subject=" + subject
				+ ", paymentPendingForSecondOrHigherAttempt=" + paymentPendingForSecondOrHigherAttempt
				+ ", showStartTestButton=" + showStartTestButton + ", attemptDetail1=" + attemptDetail1
				+ ", attemptDetail2=" + attemptDetail2 + ", attemptDetail3=" + attemptDetail3 + ", getAttemptDetail1()="
				+ getAttemptDetail1() + ", getAttemptDetail2()=" + getAttemptDetail2() + ", getAttemptDetail3()="
				+ getAttemptDetail3() + ", bodQuestions=" + bodQuestions + "]";
	}
	
	
	 
//	@Override
//	public String toString() {
//		return "[ "
//				+ "assignmentPaymentPending : " +assignmentPaymentPending+
//				"	,\n test: " +test.toString()+ 
//				"	,\n  studentsTestDetails : " + studentsTestDetails.toString()+
//				"	,\n  messageDetails: " + messageDetails +
//				"	,\n  attemptsDetails : " + attemptsDetails +
//				"	,\n attemptNoNQuestionsMap : " + attemptNoNQuestionsMap + 
//				"	,\n continueAttempt : " + continueAttempt + 
//				"	, \n  subject : " + subject+ 
//				"	, paymentPendingForSecondOrHigherAttempt : " + paymentPendingForSecondOrHigherAttempt +
//				"	,\n attemptDetail1 :" + attemptDetail1.toString() +
//				"	,\n attemptDetail2 :" + attemptDetail2 +
//				"	,\n attemptDetail2 :" + attemptDetail3 +
//				"  "
//				+ ""
//				+ " ]";
//	}
}
