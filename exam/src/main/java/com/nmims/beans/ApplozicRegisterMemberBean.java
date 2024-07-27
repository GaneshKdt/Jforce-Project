package com.nmims.beans;

public class ApplozicRegisterMemberBean {

    private String userId;
    private String applicationId;

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getApplicationId() {
        return applicationId;
    }

    public void setApplicationId(String applicationId) {
        this.applicationId = applicationId;
    }

    @Override
    public String toString() {
        return "ApplozicRegisterMemberBean{" +
                "userId='" + userId + '\'' +
                ", applicationId='" + applicationId + '\'' +
                '}';
    }
}
