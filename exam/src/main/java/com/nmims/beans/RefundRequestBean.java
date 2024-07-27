package com.nmims.beans;

import java.io.Serializable;

public class RefundRequestBean  implements Serializable  {
	private String sapid;
    private String trackId;
    private String amount;
    private String description;
    private String created_at;
    private String updated_at;
    private String name;
    private String emailId;
    private String mobile;
    
    
    
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

	public String getSapid() {
        return sapid;
    }

    public void setSapid(String sapid) {
        this.sapid = sapid;
    }

    public String getTrackId() {
        return trackId;
    }

    public void setTrackId(String trackId) {
        this.trackId = trackId;
    }

    public String getAmount() {
        return amount;
    }

    public void setAmount(String amount) {
        this.amount = amount;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getCreated_at() {
        return created_at;
    }

    public void setCreated_at(String created_at) {
        this.created_at = created_at;
    }

    public String getUpdated_at() {
        return updated_at;
    }

    public void setUpdated_at(String updated_at) {
        this.updated_at = updated_at;
    }

    @java.lang.Override
    public java.lang.String toString() {
        return "RefundRequestBean{" +
                "sapid='" + sapid + '\'' +
                ", trackId='" + trackId + '\'' +
                ", amount='" + amount + '\'' +
                ", description='" + description + '\'' +
                ", created_at='" + created_at + '\'' +
                ", updated_at='" + updated_at + '\'' +
                '}';
    }
}
