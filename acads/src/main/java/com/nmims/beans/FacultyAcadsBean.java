package com.nmims.beans;

import java.io.Serializable;

import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.commons.CommonsMultipartFile;

public class FacultyAcadsBean implements Serializable{

	/**
	 * Change Name from FacultyBean to FacultyAcadsBean for serializable issue
	 */
	//private static final long serialVersionUID = -5151294025809586673L;
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
	private CommonsMultipartFile cvFileData;
	private CommonsMultipartFile facultyImageFileData;
	private String graduationDetails;
	private String yearOfPassingGraduation;
	private String phdDetails;
	private String yearOfPassingPhd;
	private String anyOtherEducationDetails;
	private String cvUrl ;
	private String imgUrl ;
		
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

	//later added
	private String title;
	private String programGroup;
	private String programName;
	private String officeContact;
	private String homeContact;
	private String secondaryEmail;
	private String natureOfAppointment; // dropdown
	private String areaOfSpecialisation; // dropdown
	private String otherAreaOfSpecialisation;
	private String aadharNumber;
	private String approvedInSlab; // dropdown
	private String dateOfECMeetingApprovalTaken;  // depends on approvedInSlab options of A & B
	private String consentForMarketingCollateralsOrPhotoAndProfileRelease; // dropdown -> Yes/No(if No then reason)
	private String consentForMarketingCollateralsOrPhotoAndProfileReleaseReason; // reason for consentForMarketingCollateralsOrPhotoAndProfileRelease
	private String honorsAndAwards;
	private String memberships;
	private String researchInterest;
	private String articlesPublishedInInternationalJournals;
	private String articlesPublishedInNationalJournals;
	private String summaryOfPapersPublishedInABDCJournals;
	private String paperPresentationsAtInternationalConference;
	private String paperPresentationAtNationalConference;
	private String caseStudiesPublished;
	private String booksPublished;
	private String bookChaptersPublished;
	private String listOfPatents;
	private String consultingProjects;
	private String researchProjects;

	private String linkedInProfileUrl;
	private CommonsMultipartFile facultyConsentFormData;
	private String isConsentForm;
	private String consentFormUrl;
	private String comments;

	private CommonsMultipartFile facultyUpload;
	private String errorMessage = "";
	private boolean errorRecord = false;

	private String countryCode;
	private String salutation;

	private String ecApprovalDate;
	private MultipartFile ecApprovalProof;
	private String ecApprovalProofUrl;
	private String ecApprovalComment;
	private String auditStatus;
	private String facultyStatus;

	public String getCountryCode() {
		return countryCode;
	}
	public void setCountryCode(String countryCode) {
		this.countryCode = countryCode;
	}
	public String getSalutation() {
		return salutation;
	}
	public void setSalutation(String salutation) {
		this.salutation = salutation;
	}
	public String getEcApprovalComment() {
		return ecApprovalComment;
	}
	public void setEcApprovalComment(String ecApprovalComment) {
		this.ecApprovalComment = ecApprovalComment;
	}
	public String getFacultyStatus() {
		return facultyStatus;
	}
	public void setFacultyStatus(String facultyStatus) {
		this.facultyStatus = facultyStatus;
	}
	public String getEcApprovalProofUrl() {
		return ecApprovalProofUrl;
	}
	public void setEcApprovalProofUrl(String ecApprovalProofUrl) {
		this.ecApprovalProofUrl = ecApprovalProofUrl;
	}
	public String getEcApprovalDate() {
		return ecApprovalDate;
	}
	public void setEcApprovalDate(String ecApprovalDate) {
		this.ecApprovalDate = ecApprovalDate;
	}
	public String getAuditStatus() {
		return auditStatus;
	}
	public void setAuditStatus(String auditStatus) {
		this.auditStatus = auditStatus;
	}
	public MultipartFile getEcApprovalProof() {
		return ecApprovalProof;
	}
	public void setEcApprovalProof(MultipartFile ecApprovalProof) {
		this.ecApprovalProof = ecApprovalProof;
	}
	public CommonsMultipartFile getCvFileData() {
		return cvFileData;
	}
	public void setCvFileData(CommonsMultipartFile cvFileData) {
		this.cvFileData = cvFileData;
	}
	public CommonsMultipartFile getFacultyImageFileData() {
		return facultyImageFileData;
	}
	public void setFacultyImageFileData(CommonsMultipartFile facultyImageFileData) {
		this.facultyImageFileData = facultyImageFileData;
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
		this.imgUrl = imgUrl;
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


	//later added


	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getProgramGroup() {
		return programGroup;
	}

	public void setProgramGroup(String programGroup) {
		this.programGroup = programGroup;
	}

	public String getProgramName() {
		return programName;
	}

	public void setProgramName(String programName) {
		this.programName = programName;
	}

	public String getOfficeContact() {
		return officeContact;
	}

	public void setOfficeContact(String officeContact) {
		this.officeContact = officeContact;
	}

	public String getHomeContact() {
		return homeContact;
	}

	public void setHomeContact(String homeContact) {
		this.homeContact = homeContact;
	}

	public String getSecondaryEmail() {
		return secondaryEmail;
	}

	public void setSecondaryEmail(String secondaryEmail) {
		this.secondaryEmail = secondaryEmail;
	}

	public String getNatureOfAppointment() {
		return natureOfAppointment;
	}

	public void setNatureOfAppointment(String natureOfAppointment) {
		this.natureOfAppointment = natureOfAppointment;
	}

	public String getAreaOfSpecialisation() {
		return areaOfSpecialisation;
	}

	public void setAreaOfSpecialisation(String areaOfSpecialisation) {
		this.areaOfSpecialisation = areaOfSpecialisation;
	}

	public String getOtherAreaOfSpecialisation() {
		return otherAreaOfSpecialisation;
	}

	public void setOtherAreaOfSpecialisation(String otherAreaOfSpecialisation) {
		this.otherAreaOfSpecialisation = otherAreaOfSpecialisation;
	}

	public String getAadharNumber() {
		return aadharNumber;
	}

	public void setAadharNumber(String aadharNumber) {
		this.aadharNumber = aadharNumber;
	}

	public String getApprovedInSlab() {
		return approvedInSlab;
	}

	public void setApprovedInSlab(String approvedInSlab) {
		this.approvedInSlab = approvedInSlab;
	}

	public String getDateOfECMeetingApprovalTaken() {
		return dateOfECMeetingApprovalTaken;
	}

	public void setDateOfECMeetingApprovalTaken(String dateOfECMeetingApprovalTaken) {
		this.dateOfECMeetingApprovalTaken = dateOfECMeetingApprovalTaken;
	}

	public String getConsentForMarketingCollateralsOrPhotoAndProfileRelease() {
		return consentForMarketingCollateralsOrPhotoAndProfileRelease;
	}

	public void setConsentForMarketingCollateralsOrPhotoAndProfileRelease(String consentForMarketingCollateralsOrPhotoAndProfileRelease) {
		this.consentForMarketingCollateralsOrPhotoAndProfileRelease = consentForMarketingCollateralsOrPhotoAndProfileRelease;
	}

	public String getConsentForMarketingCollateralsOrPhotoAndProfileReleaseReason() {
		return consentForMarketingCollateralsOrPhotoAndProfileReleaseReason;
	}

	public void setConsentForMarketingCollateralsOrPhotoAndProfileReleaseReason(String consentForMarketingCollateralsOrPhotoAndProfileReleaseReason) {
		this.consentForMarketingCollateralsOrPhotoAndProfileReleaseReason = consentForMarketingCollateralsOrPhotoAndProfileReleaseReason;
	}

	public String getHonorsAndAwards() {
		return honorsAndAwards;
	}

	public void setHonorsAndAwards(String honorsAndAwards) {
		this.honorsAndAwards = honorsAndAwards;
	}

	public String getMemberships() {
		return memberships;
	}

	public void setMemberships(String memberships) {
		this.memberships = memberships;
	}

	public String getResearchInterest() {
		return researchInterest;
	}

	public void setResearchInterest(String researchInterest) {
		this.researchInterest = researchInterest;
	}

	public String getArticlesPublishedInInternationalJournals() {
		return articlesPublishedInInternationalJournals;
	}

	public void setArticlesPublishedInInternationalJournals(String articlesPublishedInInternationalJournals) {
		this.articlesPublishedInInternationalJournals = articlesPublishedInInternationalJournals;
	}

	public String getArticlesPublishedInNationalJournals() {
		return articlesPublishedInNationalJournals;
	}

	public void setArticlesPublishedInNationalJournals(String articlesPublishedInNationalJournals) {
		this.articlesPublishedInNationalJournals = articlesPublishedInNationalJournals;
	}

	public String getSummaryOfPapersPublishedInABDCJournals() {
		return summaryOfPapersPublishedInABDCJournals;
	}

	public void setSummaryOfPapersPublishedInABDCJournals(String summaryOfPapersPublishedInABDCJournals) {
		this.summaryOfPapersPublishedInABDCJournals = summaryOfPapersPublishedInABDCJournals;
	}

	public String getPaperPresentationsAtInternationalConference() {
		return paperPresentationsAtInternationalConference;
	}

	public void setPaperPresentationsAtInternationalConference(String paperPresentationsAtInternationalConference) {
		this.paperPresentationsAtInternationalConference = paperPresentationsAtInternationalConference;
	}

	public String getPaperPresentationAtNationalConference() {
		return paperPresentationAtNationalConference;
	}

	public void setPaperPresentationAtNationalConference(String paperPresentationAtNationalConference) {
		this.paperPresentationAtNationalConference = paperPresentationAtNationalConference;
	}

	public String getCaseStudiesPublished() {
		return caseStudiesPublished;
	}

	public void setCaseStudiesPublished(String caseStudiesPublished) {
		this.caseStudiesPublished = caseStudiesPublished;
	}

	public String getBooksPublished() {
		return booksPublished;
	}

	public void setBooksPublished(String booksPublished) {
		this.booksPublished = booksPublished;
	}

	public String getBookChaptersPublished() {
		return bookChaptersPublished;
	}

	public void setBookChaptersPublished(String bookChaptersPublished) {
		this.bookChaptersPublished = bookChaptersPublished;
	}

	public String getListOfPatents() {
		return listOfPatents;
	}

	public void setListOfPatents(String listOfPatents) {
		this.listOfPatents = listOfPatents;
	}

	public String getConsultingProjects() {
		return consultingProjects;
	}

	public void setConsultingProjects(String consultingProjects) {
		this.consultingProjects = consultingProjects;
	}

	public String getResearchProjects() {
		return researchProjects;
	}

	public void setResearchProjects(String researchProjects) {
		this.researchProjects = researchProjects;
	}

	public String getComments() {
		return comments;
	}
	public void setComments(String comments) {
		this.comments = comments;
	}

	public String getLinkedInProfileUrl() {
		return linkedInProfileUrl;
	}

	public void setLinkedInProfileUrl(String linkedInProfileUrl) {
		this.linkedInProfileUrl = linkedInProfileUrl;
	}

	public CommonsMultipartFile getFacultyConsentFormData() {
		return facultyConsentFormData;
	}

	public void setFacultyConsentFormData(CommonsMultipartFile facultyConsentFormData) {
		this.facultyConsentFormData = facultyConsentFormData;
	}

	public String getIsConsentForm() {
		return isConsentForm;
	}

	public void setIsConsentForm(String isConsentForm) {
		this.isConsentForm = isConsentForm;
	}

	public String getConsentFormUrl() {
		return consentFormUrl;
	}

	public void setConsentFormUrl(String consentFormUrl) {
		this.consentFormUrl = consentFormUrl;
	}

	public CommonsMultipartFile getFacultyUpload() {
		return facultyUpload;
	}

	public void setFacultyUpload(CommonsMultipartFile facultyUpload) {
		this.facultyUpload = facultyUpload;
	}

	public String getErrorMessage() {
		return errorMessage;
	}

	public void setErrorMessage(String errorMessage) {
		this.errorMessage = errorMessage;
	}

	public boolean isErrorRecord() {
		return errorRecord;
	}

	public void setErrorRecord(boolean errorRecord) {
		this.errorRecord = errorRecord;
	}

	@Override
	public String toString() {
		return "FacultyAcadsBean [id=" + id + ", facultyId=" + facultyId + ", firstName=" + firstName + ", lastName="
				+ lastName + ", middleName=" + middleName + ", fullName=" + fullName + ", facultyFullName="
				+ facultyFullName + ", email=" + email + ", active=" + active + ", mobile=" + mobile + ", createdBy="
				+ createdBy + ", createdDate=" + createdDate + ", lastModifiedBy=" + lastModifiedBy
				+ ", lastModifiedDate=" + lastModifiedDate + ", password=" + password + ", roleForAllocation="
				+ roleForAllocation + ", facultySelected=" + facultySelected + ", examYear=" + examYear + ", examMonth="
				+ examMonth + ", acadYear=" + acadYear + ", acadMonth=" + acadMonth + ", facultyAllocated="
				+ facultyAllocated + ", rating=" + rating + ", altContact=" + altContact + ", dob=" + dob + ", address="
				+ address + ", education=" + education + ", teachingExp=" + teachingExp + ", corporateExp="
				+ corporateExp + ", phd=" + phd + ", setDetail=" + setDetail + ", net=" + net + ", ngasceExp="
				+ ngasceExp + ", location=" + location + ", cvFileData=" + cvFileData + ", facultyImageFileData="
				+ facultyImageFileData + ", graduationDetails=" + graduationDetails + ", yearOfPassingGraduation="
				+ yearOfPassingGraduation + ", phdDetails=" + phdDetails + ", yearOfPassingPhd=" + yearOfPassingPhd
				+ ", anyOtherEducationDetails=" + anyOtherEducationDetails + ", cvUrl=" + cvUrl + ", imgUrl=" + imgUrl
				+ ", currentOrganization=" + currentOrganization + ", designation=" + designation + ", subjectPref1="
				+ subjectPref1 + ", subjectPref2=" + subjectPref2 + ", subjectPref3=" + subjectPref3
				+ ", peerReviewAvg=" + peerReviewAvg + ", studentReviewAvg=" + studentReviewAvg + ", minPeerReviewAvg="
				+ minPeerReviewAvg + ", minStudentReviewAvg=" + minStudentReviewAvg + ", maxPeerReviewAvg="
				+ maxPeerReviewAvg + ", maxStudentReviewAvg=" + maxStudentReviewAvg + ", facultyDescription="
				+ facultyDescription + ", title=" + title + ", programGroup=" + programGroup + ", programName="
				+ programName + ", officeContact=" + officeContact + ", homeContact=" + homeContact
				+ ", secondaryEmail=" + secondaryEmail + ", natureOfAppointment=" + natureOfAppointment
				+ ", areaOfSpecialisation=" + areaOfSpecialisation + ", otherAreaOfSpecialisation="
				+ otherAreaOfSpecialisation + ", aadharNumber=" + aadharNumber + ", approvedInSlab=" + approvedInSlab
				+ ", dateOfECMeetingApprovalTaken=" + dateOfECMeetingApprovalTaken
				+ ", consentForMarketingCollateralsOrPhotoAndProfileRelease="
				+ consentForMarketingCollateralsOrPhotoAndProfileRelease
				+ ", consentForMarketingCollateralsOrPhotoAndProfileReleaseReason="
				+ consentForMarketingCollateralsOrPhotoAndProfileReleaseReason + ", honorsAndAwards=" + honorsAndAwards
				+ ", memberships=" + memberships + ", researchInterest=" + researchInterest
				+ ", articlesPublishedInInternationalJournals=" + articlesPublishedInInternationalJournals
				+ ", articlesPublishedInNationalJournals=" + articlesPublishedInNationalJournals
				+ ", summaryOfPapersPublishedInABDCJournals=" + summaryOfPapersPublishedInABDCJournals
				+ ", paperPresentationsAtInternationalConference=" + paperPresentationsAtInternationalConference
				+ ", paperPresentationAtNationalConference=" + paperPresentationAtNationalConference
				+ ", caseStudiesPublished=" + caseStudiesPublished + ", booksPublished=" + booksPublished
				+ ", bookChaptersPublished=" + bookChaptersPublished + ", listOfPatents=" + listOfPatents
				+ ", consultingProjects=" + consultingProjects + ", researchProjects=" + researchProjects
				+ ", linkedInProfileUrl=" + linkedInProfileUrl + ", facultyConsentFormData=" + facultyConsentFormData
				+ ", isConsentForm=" + isConsentForm + ", consentFormUrl=" + consentFormUrl + ", comments=" + comments
				+ ", facultyUpload=" + facultyUpload + ", errorMessage=" + errorMessage + ", errorRecord=" + errorRecord
				+ ", countryCode=" + countryCode + ", salutation=" + salutation
				+ ", ecApprovalDate=" + ecApprovalDate + ", ecApprovalProof=" + ecApprovalProof
				+ ", ecApprovalProofUrl=" + ecApprovalProofUrl + ", ecApprovalComment=" + ecApprovalComment
				+ ", auditStatus=" + auditStatus + ", facultyStatus=" + facultyStatus + "]";
	}


}