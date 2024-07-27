package com.nmims.beans;

import java.io.Serializable;

//spring security related changes rename GroupBean to GroupExamBean
public class GroupExamBean  implements Serializable  {
	
	
	private Long id;
	private int timeBoundId;
	private String program_sem_subject_id;
	private String groupName;
	private String groupDescription;
	private String groupProfilePic;
	private String active;
	private String createdBy;
	private String lastModifiedBy;
	
	
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public int getTimeBoundId() {
		return timeBoundId;
	}
	public void setTimeBoundId(int timeBoundId) {
		this.timeBoundId = timeBoundId;
	}
	public String getProgram_sem_subject_id() {
		return program_sem_subject_id;
	}
	public void setProgram_sem_subject_id(String program_sem_subject_id) {
		this.program_sem_subject_id = program_sem_subject_id;
	}
	public String getGroupName() {
		return groupName;
	}
	public void setGroupName(String groupName) {
		this.groupName = groupName;
	}
	public String getGroupDescription() {
		return groupDescription;
	}
	public void setGroupDescription(String groupDescription) {
		this.groupDescription = groupDescription;
	}
	public String getGroupProfilePic() {
		return groupProfilePic;
	}
	public void setGroupProfilePic(String groupProfilePic) {
		this.groupProfilePic = groupProfilePic;
	}
	public String getActive() {
		return active;
	}
	public void setActive(String active) {
		this.active = active;
	}
	public String getCreatedBy() {
		return createdBy;
	}
	public void setCreatedBy(String createdBy) {
		this.createdBy = createdBy;
	}
	public String getLastModifiedBy() {
		return lastModifiedBy;
	}
	public void setLastModifiedBy(String lastModifiedBy) {
		this.lastModifiedBy = lastModifiedBy;
	}
	
	@Override
	public String toString() {
		return "GroupBean [id=" + id + ", timeBoundId=" + timeBoundId + ", program_sem_subject_id="
				+ program_sem_subject_id + ", groupName=" + groupName + ", groupDescription=" + groupDescription
				+ ", groupProfilePic=" + groupProfilePic + ", active=" + active + ", createdBy=" + createdBy
				+ ", lastModifiedBy=" + lastModifiedBy + "]";
	}
	
}
