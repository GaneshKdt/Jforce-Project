package com.nmims.beans;

import java.io.Serializable;

public class MettlResultQuestionMcqTypeResponse implements Serializable  {

	private int responseIndex;

	public int getResponseIndex() {
		return responseIndex;
	}
	public void setResponseIndex(int responseIndex) {
		this.responseIndex = responseIndex;
	}
	@Override
	public String toString() {
		return "MettlResultQuestionMcqTypeResponse [responseIndex=" + responseIndex + "]";
	}
}
