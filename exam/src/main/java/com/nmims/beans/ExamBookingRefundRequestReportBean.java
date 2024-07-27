package com.nmims.beans;

import java.io.Serializable;

public class ExamBookingRefundRequestReportBean  implements Serializable {
	private String sapid;
    private String subject;
    private String name;
    private String emailId;
    private String mobile;
    private String trackId;
    private String description;
    private String amount;
    private String options;
    private String year;
    private String month;
    private String submissionDate;

    public String getSapid() {
        return sapid;
    }

    public void setSapid(String sapid) {
        this.sapid = sapid;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmailId() {
        return emailId;
    }

    public void setEmailId(String emailId) {
        this.emailId = emailId;
    }

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    public String getTrackId() {
        return trackId;
    }

    public void setTrackId(String trackId) {
        this.trackId = trackId;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getAmount() {
        return amount;
    }

    public void setAmount(String amount) {
        this.amount = amount;
    }

    public String getOptions() {
        return options;
    }

    public void setOptions(String options) {
        this.options = options;
    }

    public String getYear() {
        return year;
    }

    public void setYear(String year) {
        this.year = year;
    }

    public String getMonth() {
        return month;
    }

    public void setMonth(String month) {
        this.month = month;
    }

    public String getSubmissionDate() {
        return submissionDate;
    }

    public void setSubmissionDate(String submissionDate) {
        this.submissionDate = submissionDate;
    }

    @java.lang.Override
    public java.lang.String toString() {
        return "ExamBookingRefundRequestReportBean{" +
                "sapid='" + sapid + '\'' +
                ", subject='" + subject + '\'' +
                ", name='" + name + '\'' +
                ", emailId='" + emailId + '\'' +
                ", mobile='" + mobile + '\'' +
                ", trackId='" + trackId + '\'' +
                ", description='" + description + '\'' +
                ", amount='" + amount + '\'' +
                ", options='" + options + '\'' +
                ", year='" + year + '\'' +
                ", month='" + month + '\'' +
                ", submissionDate='" + submissionDate + '\'' +
                '}';
    }
}
