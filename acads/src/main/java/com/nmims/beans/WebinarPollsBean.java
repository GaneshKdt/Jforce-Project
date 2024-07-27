package com.nmims.beans;

import java.io.Serializable;
import java.util.*;
import com.nmims.beans.WebinarPollsQuestionsBean;

public class WebinarPollsBean  implements Serializable  {
    private String id;
    private String title;
    private List<WebinarPollsQuestionsBean> questions;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public List<WebinarPollsQuestionsBean> getQuestions() {
        return questions;
    }

    public void setQuestions(List<WebinarPollsQuestionsBean> questions) {
        this.questions = questions;
    }

    private String webinarId;
    private String createdBy;
    private String createdDate;
    private String lastModifiedBy;
    private String lastModifiedDate;

	public String getWebinarId() {
		return webinarId;
	}

	public void setWebinarId(String webinarId) {
		this.webinarId = webinarId;
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

	@Override
	public String toString() {
		return "WebinarPollsBean [id=" + id + ", title=" + title + ", questions=" + questions + ", webinarId="
				+ webinarId + ", createdBy=" + createdBy + ", createdDate=" + createdDate + ", lastModifiedBy="
				+ lastModifiedBy + ", lastModifiedDate=" + lastModifiedDate + "]";
	}

}