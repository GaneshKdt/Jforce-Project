package com.nmims.timeline.model;


import java.util.Date;

import javax.persistence.Column;
import javax.persistence.MappedSuperclass;
import javax.persistence.PrePersist;
import javax.persistence.PreUpdate;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;

@MappedSuperclass
public abstract class AbstractTimestampEntity {

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "createdDate", updatable=false) 
    private Date createdDate;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "lastModifiedDate")
    private Date lastModifiedDate;

    @PrePersist
    protected void onCreate() {
    	 createdDate = new Date();
    	 lastModifiedDate = new Date();
    }

    @PreUpdate
    protected void onUpdate() {
    	lastModifiedDate = new Date();
    }
    @Transient
	public Date getCreatedDate() {
		return createdDate;
	}
    @Transient
	public Date getLastModifiedDate() {
		return lastModifiedDate;
	}

//	@Transient
//	public void setCreatedDate(Date createdDate) {
//		this.createdDate = createdDate;
//	}
//	@Transient
//	public void setLastModifiedDate(Date lastModifiedDate) {
//		this.lastModifiedDate = lastModifiedDate;
//	}
    
    
}