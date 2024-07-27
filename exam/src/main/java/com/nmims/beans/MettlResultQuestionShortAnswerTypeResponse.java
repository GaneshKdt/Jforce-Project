package com.nmims.beans;

import java.io.Serializable;
import java.util.List;

public class MettlResultQuestionShortAnswerTypeResponse  implements Serializable  {
	private String candidateResponse;
	private List<String> uploadedFiles;
	
	public String getCandidateResponse() {
		return candidateResponse;
	}
	public void setCandidateResponse(String candidateResponse) {
		this.candidateResponse = candidateResponse;
	}
	public List<String> getUploadedFiles() {
		return uploadedFiles;
	}
	public void setUploadedFiles(List<String> uploadedFiles) {
		this.uploadedFiles = uploadedFiles;
	}
	@Override
	public String toString() {
		return "MettlResultLongAnswerTypeResponse [candidateResponse=" + candidateResponse + ", uploadedFiles="
				+ uploadedFiles + "]";
	}
}
