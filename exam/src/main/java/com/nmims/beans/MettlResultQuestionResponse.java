package com.nmims.beans;

import java.io.Serializable;

public class MettlResultQuestionResponse implements Serializable  {
	
	private MettlResultQuestionMcqTypeResponse mcqTypeResponse;
	private MettlResultQuestionMcaTypeResponse mcaTypeResponse; 
	private MettlResultQuestionLongAnswerTypeResponse longAnswerTypeResponse;
	private MettlResultQuestionShortAnswerTypeResponse shortAnswerTypeResponse;
//	diagramTypeResponse": null
	
	public MettlResultQuestionMcqTypeResponse getMcqTypeResponse() {
		return mcqTypeResponse;
	}
	public void setMcqTypeResponse(MettlResultQuestionMcqTypeResponse mcqTypeResponse) {
		this.mcqTypeResponse = mcqTypeResponse;
	}
	public MettlResultQuestionMcaTypeResponse getMcaTypeResponse() {
		return mcaTypeResponse;
	}
	public void setMcaTypeResponse(MettlResultQuestionMcaTypeResponse mcaTypeResponse) {
		this.mcaTypeResponse = mcaTypeResponse;
	}
	public MettlResultQuestionLongAnswerTypeResponse getLongAnswerTypeResponse() {
		return longAnswerTypeResponse;
	}
	public void setLongAnswerTypeResponse(MettlResultQuestionLongAnswerTypeResponse longAnswerTypeResponse) {
		this.longAnswerTypeResponse = longAnswerTypeResponse;
	}
	public MettlResultQuestionShortAnswerTypeResponse getShortAnswerTypeResponse() {
		return shortAnswerTypeResponse;
	}
	public void setShortAnswerTypeResponse(MettlResultQuestionShortAnswerTypeResponse shortAnswerTypeResponse) {
		this.shortAnswerTypeResponse = shortAnswerTypeResponse;
	}
	@Override
	public String toString() {
		return "MettlResultQuestionResponse [mcqTypeResponse=" + mcqTypeResponse + ", mcaTypeResponse="
				+ mcaTypeResponse + ", longAnswerTypeResponse=" + longAnswerTypeResponse + ", shortAnswerTypeResponse="
				+ shortAnswerTypeResponse + "]";
	}
}
