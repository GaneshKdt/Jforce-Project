package com.nmims.beans;

import java.util.*;

public class WebinarPollsResultsQuestionsBean{
    private String id;
    private String webinarId;
    private String name;
    private String email;
    private List<WebinarPollsResultsQuestionDetailsBean> question_details;
    private String createdBy;
    private String createdDate;
    private String lastModifiedBy;
    private String lastModifiedDate;
    private String sapid;
    private String isLateral;
   

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getWebinarId() {
        return webinarId;
    }

    public void setWebinarId(String webinarId) {
        this.webinarId = webinarId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public List<WebinarPollsResultsQuestionDetailsBean> getQuestion_details() {
        return question_details;
    }

    public void setQuestion_details(List<WebinarPollsResultsQuestionDetailsBean> question_details) {
        this.question_details = question_details;
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
        return "WebinarPollsResultsQuestionsBean{" +
                "id='" + id + '\'' +
                ", webinarId='" + webinarId + '\'' +
                ", name='" + name + '\'' +
                ", email='" + email + '\'' +
                ", question_details=" + question_details +
                ", createdBy='" + createdBy + '\'' +
                ", createdDate='" + createdDate + '\'' +
                ", lastModifiedBy='" + lastModifiedBy + '\'' +
                ", lastModifiedDate='" + lastModifiedDate + '\'' +
                '}';
    }

	public String getSapid() {
		return sapid;
	}

	public void setSapid(String sapid) {
		this.sapid = sapid;
	}

	public String getIsLateral() {
		return isLateral;
	}

	public void setIsLateral(String isLateral) {
		this.isLateral = isLateral;
	}

	
}