package com.nmims.beans;

import java.io.Serializable;
import java.util.List;

public class MettlResultQuestionMcaTypeResponse implements Serializable  {
	private List<String> responses;

	public List<String> getResponses() {
		return responses;
	}
	public void setResponses(List<String> responses) {
		this.responses = responses;
	}
	@Override
	public String toString() {
		return "MettlResultQuestionMcaTypeResponse [responses=" + responses + "]";
	}
}
