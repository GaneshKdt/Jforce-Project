package com.nmims.timeline.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.SecondaryTable;
import javax.persistence.Table;

@Entity
@Table(name="acads.faculty", schema="acads")
public class Faculty {

	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	private Integer id;
	private String facultyId;
	private String firstName;
	private String lastName;
	private String email;
	private String imgUrl ;
	String profilePicFilePath;
	
	
	
	public String getProfilePicFilePath() {
		return profilePicFilePath;
	}
	public void setProfilePicFilePath(String profilePicFilePath) {
		this.profilePicFilePath = profilePicFilePath;
	}
	public Integer getId() {
		return id;
	}
	public void setId(Integer id) {
		this.id = id;
	}
	public String getFacultyId() {
		return facultyId;
	}
	public void setFacultyId(String facultyId) {
		this.facultyId = facultyId;
	}
	public String getFirstName() {
		return firstName;
	}
	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}
	public String getLastName() {
		return lastName;
	}
	public void setLastName(String lastName) {
		this.lastName = lastName;
	}
	public String getEmail() {
		return email;
	}
	public void setEmail(String email) {
		this.email = email;
	}
	public String getImgUrl() {
		return imgUrl;
	}
	public void setImgUrl(String imgUrl) {
		this.imgUrl = imgUrl;
	}
	
	@Override
	public String toString() {
		return "Faculty [id=" + id + ", facultyId=" + facultyId + ", firstName=" + firstName + ", lastName=" + lastName
				+ ", email=" + email + ", imgUrl=" + imgUrl + "]";
	}
	
}
