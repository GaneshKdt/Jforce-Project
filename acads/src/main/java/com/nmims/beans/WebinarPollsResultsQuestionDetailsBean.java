package com.nmims.beans;

import java.util.*;

public class WebinarPollsResultsQuestionDetailsBean{
    private String id;
    private String session_polls_results_questions_id;
    private String question;
    private String answer;
    private String polling_id;
    private String pollName;
    private String createdBy;
    private String createdDate;
    private String lastModifiedBy;
    private String lastModifiedDate;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getSession_polls_results_questions_id() {
        return session_polls_results_questions_id;
    }

    public void setSession_polls_results_questions_id(String session_polls_results_questions_id) {
        this.session_polls_results_questions_id = session_polls_results_questions_id;
    }

    public String getQuestion() {
        return question;
    }

    public void setQuestion(String question) {
        this.question = question;
    }

    public String getAnswer() {
        return answer;
    }

    public void setAnswer(String answer) {
        this.answer = answer;
    }

    public String getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(String createdBy) {
        this.createdBy = createdBy;
    }

    public String getCreatedDate() {
        return createdDate;
    }

    public void setCreatedDate(String createdDate) {
        this.createdDate = createdDate;
    }

    public String getLastModifiedBy() {
        return lastModifiedBy;
    }

    public void setLastModifiedBy(String lastModifiedBy) {
        this.lastModifiedBy = lastModifiedBy;
    }

    public String getLastModifiedDate() {
        return lastModifiedDate;
    }

    public void setLastModifiedDate(String lastModifiedDate) {
        this.lastModifiedDate = lastModifiedDate;
    }

    @java.lang.Override
    public java.lang.String toString() {
        return "WebinarPollsResultsQuestionDetailsBean{" +
                "id='" + id + '\'' +
                ", session_polls_results_questions_id='" + session_polls_results_questions_id + '\'' +
                ", question='" + question + '\'' +
                ", answer='" + answer + '\'' +
                ", polling_id='" + polling_id + '\'' +
                ", pollName='" + pollName + '\'' +
                ", createdBy='" + createdBy + '\'' +
                ", createdDate='" + createdDate + '\'' +
                ", lastModifiedBy='" + lastModifiedBy + '\'' +
                ", lastModifiedDate='" + lastModifiedDate + '\'' +
                '}';
    }

	public String getPolling_id() {
		return polling_id;
	}

	public void setPolling_id(String polling_id) {
		this.polling_id = polling_id;
	}

	public String getPollName() {
		return pollName;
	}

	public void setPollName(String pollName) {
		this.pollName = pollName;
	}

	
}