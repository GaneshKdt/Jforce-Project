package com.nmims.beans;

import java.util.*;

public class WebinarPollsResultsBean{
    private String id;
    private String uuid;
    private String start_time;
    private String title;
    private List<WebinarPollsResultsQuestionsBean> questions;
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

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public String getStart_time() {
        return start_time;
    }

    public void setStart_time(String start_time) {
        this.start_time = start_time;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public List<WebinarPollsResultsQuestionsBean> getQuestions() {
        return questions;
    }

    public void setQuestions(List<WebinarPollsResultsQuestionsBean> questions) {
        this.questions = questions;
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
        return "WebinarPollsResultsBean{" +
                "id='" + id + '\'' +
                ", uuid='" + uuid + '\'' +
                ", start_time='" + start_time + '\'' +
                ", title='" + title + '\'' +
                ", questions=" + questions +
                ", createdBy='" + createdBy + '\'' +
                ", createdDate='" + createdDate + '\'' +
                ", lastModifiedBy='" + lastModifiedBy + '\'' +
                ", lastModifiedDate='" + lastModifiedDate + '\'' +
                '}';
    }

}