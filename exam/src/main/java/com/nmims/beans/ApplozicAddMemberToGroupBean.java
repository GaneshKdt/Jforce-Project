package com.nmims.beans;

import java.io.Serializable;

public class ApplozicAddMemberToGroupBean  implements Serializable {

    private String userId;
    private String clientGroupId;
    private int role;

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getClientGroupId() {
        return clientGroupId;
    }

    public void setClientGroupId(String clientGroupId) {
        this.clientGroupId = clientGroupId;
    }

    public int getRole() {
        return role;
    }

    public void setRole(int role) {
        this.role = role;
    }

    @Override
    public String toString() {
        return "ApplozicAddMemberToGroupBean{" +
                "userId='" + userId + '\'' +
                ", clientGroupId='" + clientGroupId + '\'' +
                ", role=" + role +
                '}';
    }
}
