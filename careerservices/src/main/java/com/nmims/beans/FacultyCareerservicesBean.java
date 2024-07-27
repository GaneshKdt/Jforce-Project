package com.nmims.beans;

import java.io.Serializable;

import org.springframework.web.multipart.commons.CommonsMultipartFile;

public class FacultyCareerservicesBean  implements Serializable{
	private String id;
	private String facultyId;
	private String firstName;
	private String lastName;
	private String middleName;
	private String fullName;
	private String facultyFullName;
	private String email;
	private String active;
	private String mobile;
	private String createdBy;
	private String createdDate;
	private String lastModifiedBy;
	private String lastModifiedDate;
	private String password;
	
	private String roleForAllocation;
	private String facultySelected;
	private String examYear;
	private String examMonth;
	private String acadYear;
	private String acadMonth;
	private String facultyAllocated;
	private String rating;
	
	
	private String altContact;
	private String dob;
	private String address;
	private String education;
	private String teachingExp;
	private String corporateExp;
	private String phd;
	private String setDetail;
	private String net;
	private String ngasceExp;
	private String location;
	private CommonsMultipartFile fileData;
	private CommonsMultipartFile fileData2;
	private String graduationDetails;
	private String yearOfPassingGraduation;
	private String phdDetails;
	private String yearOfPassingPhd;
	private String anyOtherEducationDetails;
	private String cvUrl ;
	//JUST FOR CS. DONT CHANGE IN OTHER APPS
	private String imgUrl = "Faculty/default.png";
		
	private String currentOrganization;
	private String designation;
	private String subjectPref1;
	private String subjectPref2;
	private String subjectPref3;
	private String peerReviewAvg;
	private String studentReviewAvg;
	private String minPeerReviewAvg;
	private String minStudentReviewAvg;
	private String maxPeerReviewAvg;
	private String maxStudentReviewAvg;
	
	private String facultyDescription;
	private String profilePicFilePath;
	private CommonsMultipartFile fileData3;
 	
	//CS SPECEFIC FIELDS
	private String speakerLinkedInProfile;
	private String speakerTwitterProfile;
	private String speakerFacebookProfile;
	
	public String getSpeakerLinkedInProfile() {
		return speakerLinkedInProfile;
	}
	public void setSpeakerLinkedInProfile(String speakerLinkedInProfile) {
		this.speakerLinkedInProfile = speakerLinkedInProfile;
	}
	public String getSpeakerTwitterProfile() {
		return speakerTwitterProfile;
	}
	public void setSpeakerTwitterProfile(String speakerTwitterProfile) {
		this.speakerTwitterProfile = speakerTwitterProfile;
	}
	public String getPeerReviewAvg() {
		return peerReviewAvg;
	}
	public void setPeerReviewAvg(String peerReviewAvg) {
		this.peerReviewAvg = peerReviewAvg;
	}
	
	public String getStudentReviewAvg() {
		return studentReviewAvg;
	}
	public void setStudentReviewAvg(String studentReviewAvg) {
		this.studentReviewAvg = studentReviewAvg;
	}
	public String getDob() {
		return dob;
	}
	public void setDob(String dob) {
		this.dob = dob;
	}
	public String getCvUrl() {
		return cvUrl;
	}
	public void setCvUrl(String cvUrl) {
		this.cvUrl = cvUrl;
	}
	public String getImgUrl() {
		return imgUrl;
	}
	public void setImgUrl(String imgUrl) {
		if(imgUrl != null) {
			this.imgUrl = imgUrl;
		}
	}
	public CommonsMultipartFile getFileData2() {
		return fileData2;
	}
	public void setFileData2(CommonsMultipartFile fileData2) {
		this.fileData2 = fileData2;
	}
	public String getGraduationDetails() {
		return graduationDetails;
	}
	public void setGraduationDetails(String graduationDetails) {
		this.graduationDetails = graduationDetails;
	}
	public String getYearOfPassingGraduation() {
		return yearOfPassingGraduation;
	}
	public void setYearOfPassingGraduation(String yearOfPassingGraduation) {
		this.yearOfPassingGraduation = yearOfPassingGraduation;
	}
	public String getPhdDetails() {
		return phdDetails;
	}
	public void setPhdDetails(String phdDetails) {
		this.phdDetails = phdDetails;
	}
	public String getYearOfPassingPhd() {
		return yearOfPassingPhd;
	}
	public void setYearOfPassingPhd(String yearOfPassingPhd) {
		this.yearOfPassingPhd = yearOfPassingPhd;
	}
	public String getAnyOtherEducationDetails() {
		return anyOtherEducationDetails;
	}
	public void setAnyOtherEducationDetails(String anyOtherEducationDetails) {
		this.anyOtherEducationDetails = anyOtherEducationDetails;
	}
	public String getMiddleName() {
		return middleName;
	}
	public void setMiddleName(String middleName) {
		this.middleName = middleName;
	}
	public String getFullName() {
		return firstName + " "+lastName;
	}
	public void setFullName(String fullName) {
		this.fullName = fullName;
	}
	public String getRating() {
		return rating;
	}
	public void setRating(String rating) {
		this.rating = rating;
	}
	public String getRoleForAllocation() {
		return roleForAllocation;
	}
	public void setRoleForAllocation(String roleForAllocation) {
		this.roleForAllocation = roleForAllocation;
	}
	public String getFacultySelected() {
		return facultySelected;
	}
	public void setFacultySelected(String facultySelected) {
		this.facultySelected = facultySelected;
	}
	public String getExamYear() {
		return examYear;
	}
	public void setExamYear(String examYear) {
		this.examYear = examYear;
	}
	public String getExamMonth() {
		return examMonth;
	}
	public void setExamMonth(String examMonth) {
		this.examMonth = examMonth;
	}
	public String getAcadYear() {
		return acadYear;
	}
	public void setAcadYear(String acadYear) {
		this.acadYear = acadYear;
	}
	public String getAcadMonth() {
		return acadMonth;
	}
	public void setAcadMonth(String acadMonth) {
		this.acadMonth = acadMonth;
	}
	public String getFacultyAllocated() {
		return facultyAllocated;
	}
	public void setFacultyAllocated(String facultyAllocated) {
		this.facultyAllocated = facultyAllocated;
	}
	public String getPassword() {
		return password;
	}
	public void setPassword(String password) {
		this.password = password;
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
	public String getId() {
		return id;
	}
	public void setId(String id) {
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
	public String getActive() {
		return active;
	}
	public void setActive(String active) {
		this.active = active;
	}
	public String getMobile() {
		return mobile;
	}
	public void setMobile(String mobile) {
		this.mobile = mobile;
	}
	
	public String getAltContact() {
		return altContact;
	}
	public void setAltContact(String altContact) {
		this.altContact = altContact;
	}
	
	public String getAddress() {
		return address;
	}
	public void setAddress(String address) {
		this.address = address;
	}
	public String getEducation() {
		return education;
	}
	public void setEducation(String education) {
		this.education = education;
	}
	
	public String getPhd() {
		return phd;
	}
	public void setPhd(String phd) {
		this.phd = phd;
	}
	
	public String getNet() {
		return net;
	}
	public void setNet(String net) {
		this.net = net;
	}
	
	public CommonsMultipartFile getFileData() {
		return fileData;
	}
	public void setFileData(CommonsMultipartFile fileData) {
		this.fileData = fileData;
	}
	public String getTeachingExp() {
		return teachingExp;
	}
	public void setTeachingExp(String teachingExp) {
		this.teachingExp = teachingExp;
	}
	public String getCorporateExp() {
		return corporateExp;
	}
	public void setCorporateExp(String corporateExp) {
		this.corporateExp = corporateExp;
	}
	public String getNgasceExp() {
		return ngasceExp;
	}
	public void setNgasceExp(String ngasceExp) {
		this.ngasceExp = ngasceExp;
	}
	public String getLocation() {
		return location;
	}
	public void setLocation(String location) {
		this.location = location;
	}
	
	public String getCurrentOrganization() {
		return currentOrganization;
	}
	public void setCurrentOrganization(String currentOrganization) {
		this.currentOrganization = currentOrganization;
	}
	public String getDesignation() {
		return designation;
	}
	public void setDesignation(String designation) {
		this.designation = designation;
	}
	public String getSubjectPref1() {
		return subjectPref1;
	}
	public void setSubjectPref1(String subjectPref1) {
		this.subjectPref1 = subjectPref1;
	}
	public String getSubjectPref2() {
		return subjectPref2;
	}
	public void setSubjectPref2(String subjectPref2) {
		this.subjectPref2 = subjectPref2;
	}
	public String getSubjectPref3() {
		return subjectPref3;
	}
	public void setSubjectPref3(String subjectPref3) {
		this.subjectPref3 = subjectPref3;
	}
	public String getSetDetail() {
		return setDetail;
	}
	public void setSetDetail(String setDetail) {
		this.setDetail = setDetail;
	}
	
	public String getMinPeerReviewAvg() {
		return minPeerReviewAvg;
	}
	public void setMinPeerReviewAvg(String minPeerReviewAvg) {
		this.minPeerReviewAvg = minPeerReviewAvg;
	}
	public String getMinStudentReviewAvg() {
		return minStudentReviewAvg;
	}
	public void setMinStudentReviewAvg(String minStudentReviewAvg) {
		this.minStudentReviewAvg = minStudentReviewAvg;
	}
	public String getFacultyFullName() {
		return facultyFullName;
	}
	public void setFacultyFullName(String facultyFullName) {
		this.facultyFullName = facultyFullName;
	}
	public String getMaxPeerReviewAvg() {
		return maxPeerReviewAvg;
	}
	public void setMaxPeerReviewAvg(String maxPeerReviewAvg) {
		this.maxPeerReviewAvg = maxPeerReviewAvg;
	}
	public String getMaxStudentReviewAvg() {
		return maxStudentReviewAvg;
	}
	public void setMaxStudentReviewAvg(String maxStudentReviewAvg) {
		this.maxStudentReviewAvg = maxStudentReviewAvg;
	}
	public String getFacultyDescription() {
		return facultyDescription;
	}
	public void setFacultyDescription(String facultyDescription) {
		this.facultyDescription = facultyDescription;
	}
	public String getProfilePicFilePath() {
		return profilePicFilePath;
	}
	public void setProfilePicFilePath(String profilePicFilePath) {
		this.profilePicFilePath = profilePicFilePath;
	}
	public CommonsMultipartFile getFileData3() {
		return fileData3;
	}
	public void setFileData3(CommonsMultipartFile fileData3) {
		this.fileData3 = fileData3;
	}

	@Override
	public String toString() {
		return "FacultyBean [id=" + id + ", facultyId=" + facultyId
				+ ", firstName=" + firstName + ", lastName=" + lastName
				+ ", middleName=" + middleName + ", fullName=" + fullName
				+ ", facultyFullName=" + facultyFullName + ", email=" + email
				+ ", active=" + active + ", mobile=" + mobile + ", createdBy="
				+ createdBy + ", createdDate=" + createdDate
				+ ", lastModifiedBy=" + lastModifiedBy + ", lastModifiedDate="
				+ lastModifiedDate + ", password=" + password
				+ ", roleForAllocation=" + roleForAllocation
				+ ", facultySelected=" + facultySelected + ", examYear="
				+ examYear + ", examMonth=" + examMonth + ", acadYear="
				+ acadYear + ", acadMonth=" + acadMonth + ", facultyAllocated="
				+ facultyAllocated + ", rating=" + rating + ", altContact="
				+ altContact + ", dob=" + dob + ", address=" + address
				+ ", education=" + education + ", teachingExp=" + teachingExp
				+ ", corporateExp=" + corporateExp + ", phd=" + phd
				+ ", setDetail=" + setDetail + ", net=" + net + ", ngasceExp="
				+ ngasceExp + ", location=" + location + ", fileData="
				+ fileData + ", fileData2=" + fileData2
				+ ", graduationDetails=" + graduationDetails
				+ ", yearOfPassingGraduation=" + yearOfPassingGraduation
				+ ", phdDetails=" + phdDetails + ", yearOfPassingPhd="
				+ yearOfPassingPhd + ", anyOtherEducationDetails="
				+ anyOtherEducationDetails + ", cvUrl=" + cvUrl + ", imgUrl="
				+ imgUrl + ", currentOrganization=" + currentOrganization
				+ ", designation=" + designation + ", subjectPref1="
				+ subjectPref1 + ", subjectPref2=" + subjectPref2
				+ ", subjectPref3=" + subjectPref3 + ", minPeerReviewAvg="
				+ minPeerReviewAvg + ", minStudentReviewAvg="
				+ minStudentReviewAvg + ", maxPeerReviewAvg="
				+ maxPeerReviewAvg + ", maxStudentReviewAvg="
				+ maxStudentReviewAvg + "]";
	}
	public String getSpeakerFacebookProfile() {
		return speakerFacebookProfile;
	}
	public void setSpeakerFacebookProfile(String speakerFacebookProfile) {
		this.speakerFacebookProfile = speakerFacebookProfile;
	}
	

	
}