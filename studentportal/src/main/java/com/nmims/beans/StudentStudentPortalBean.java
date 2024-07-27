package com.nmims.beans;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * old name - StudentBean 
 * @author others and vil_m
 *
 */
public class StudentStudentPortalBean implements Serializable{
	
	public StudentStudentPortalBean() {
		super();
	}
	@Value("#{'${certificatePrograms}'.split(',')}")
	private List<String> certificatePrograms;
	
	private String sapid;
	private String sem;
	private String email;
    private String lastName;
    private String firstName;
    private String middleName;
    private String fatherName;
    private String husbandName;
    private String motherName;
    private String gender;
    private String program;
    private String enrollmentMonth;
    private String enrollmentYear;
    private String emailId;
    private String mobile;
    private String altPhone;
    private String dob;
    private String regDate;
    private String isLateral;
    private String isReReg;
    private String address;
    private String houseNoName;
    private String street;
    private String landMark;
    private String locality;
    private String city;
    private String PostalCode;
    private String state;
    private String country;
    private String shippingAddress;
    private String examMode;
    private String designation;
    private String industry;
    private String userId;
    private String password;
    private String pin;
    private String centerCode;
    private String centerName;
    private String validityEndMonth;
    private String validityEndYear;
    private String createdBy;
    private String createdDate;
    private String lastModifiedBy;
    private String lastModifiedDate;
    private String errorMessage = "";
	private boolean errorRecord = false;
    private String prgmStructApplicable;
    private String updatedByStudent;
    private String imageUrl;
    private String subjectsCleared;
    private String oldProgram;
    private String programChanged;
    private String acadYear;
    private String acadMonth;
    private String subject;
    private String fromEmailId;
    private String spouseName;
    private String body;
    private String previousStudentId;
    private ArrayList<String> waivedOffSubjects = new ArrayList();
    private ArrayList<String> waivedInSubjects = new ArrayList<String>();
    private HashMap<String, String> waivedInSubjectSemMapping = new HashMap<String, String>();
    private String programForHeader;
    // add field in order to disable student login ,Email for Student 
    private String programStatus;
    private String programRemarks;
    
    private String smsContent;
    private String notificationType;
    //End
    private String programCleared;
    private String deRegistered;
    
    
    private String onesignalId;
    private String isPushnotification;
    private String pushContent;

    private String changedPassword;
    private String n_Password;
    private String c_password;
    private String programStructure; 
    private String role;
    private String loginType;
    private String programSemSubjectId;
    private String bloodGroup;
    
    private String programType;
    private String lc;
    private String newConsumerProgramStructureId;
    private boolean louConfirmed;
    private String louConfirmedTimestamp;
    private String batchName;
    private String abcId;
    private String settingType;
    private String isEnable;
    
    public String getRole() {
		return role;
	}
	public void setRole(String role) {
		this.role = role;
	}
    private String perspective;
    private String validityExpired;
    
        
    public String getValidityExpired() {
		return validityExpired;
	}
	public void setValidityExpired(String validityExpired) {
		this.validityExpired = validityExpired;
	}
	public String getPerspective() {
		return perspective;
	}
	public void setPerspective(String perspective) {
		this.perspective = perspective;
	}
	public HashMap<String, String> getWaivedInSubjectSemMapping() {
		return waivedInSubjectSemMapping;
	}
	public void setWaivedInSubjectSemMapping(HashMap<String, String> waivedInSubjectSemMapping) {
		this.waivedInSubjectSemMapping = waivedInSubjectSemMapping;
	}
	public ArrayList<String> getWaivedInSubjects() {
		return waivedInSubjects;
	}
	public void setWaivedInSubjects(ArrayList<String> waivedInSubjects) {
		this.waivedInSubjects = waivedInSubjects ;
	}
	public String getProgramStructure() {
		return programStructure;
	}
	public void setProgramStructure(String programStructure) {
		this.programStructure = programStructure;
	}

    private Integer almashinesId;
    
    private ArrayList<String> industryList = new ArrayList<String>();
    private ArrayList<String> designationList = new ArrayList<String>();
    
    private String year;
    private String month;
 
    private String emailSentProgramCleared;
    private String existingStudentNoForDiscount;

    private String errorFlag;
    private String totalExperience;
    private String annualSalary;
    private String companyName;
    private String ugQualification;
    private String age;
    private String highestQualification;
    private String consumerType;
    private String consumerProgramStructureId;
    private String firebaseToken;
    private String leadId;
    private String registrationNum;
    private boolean error;
    
    private String detailsConfirmedByStudent;
    private String status;
    
    private boolean purchasedOtherPackages;
    private int highContrast;
    private int textToSpeech;
    //for rank
    private String subjectsCount;
    
    public String getLeadId() {
		return leadId;
	}
	public void setLeadId(String leadId) {
		this.leadId = leadId;
	}
	public String getRegistrationNum() {
		return registrationNum;
	}
	public void setRegistrationNum(String registrationNum) {
		this.registrationNum = registrationNum;
	}
	public boolean getError() {
		return error;
	}
	public void setError(boolean error) {
		this.error = error;
	}
	public String getFirebaseToken() {
		return firebaseToken;
	}
	public void setFirebaseToken(String firebaseToken) {
		this.firebaseToken = firebaseToken;
	}
	public String getConsumerType() {
		return consumerType;
	}
	public void setConsumerType(String consumerType) {
		this.consumerType = consumerType;
	}
	public String getConsumerProgramStructureId() {
		return consumerProgramStructureId;
	}
	public void setConsumerProgramStructureId(String consumerProgramStructureId) {
		this.consumerProgramStructureId = consumerProgramStructureId;
	}
	public List<String> getCertificatePrograms() {
		return certificatePrograms;
	}
	public void setCertificatePrograms(List<String> certificatePrograms) {
		this.certificatePrograms = certificatePrograms;
	}
	public String getEmailSentProgramCleared() {
		return emailSentProgramCleared;
	}
	public void setEmailSentProgramCleared(String emailSentProgramCleared) {
		this.emailSentProgramCleared = emailSentProgramCleared;
	}
	public String getExistingStudentNoForDiscount() {
		return existingStudentNoForDiscount;
	}
	public void setExistingStudentNoForDiscount(String existingStudentNoForDiscount) {
		this.existingStudentNoForDiscount = existingStudentNoForDiscount;
	}
	public String getOnesignalId() {
		return onesignalId;
	}
	public void setOnesignalId(String onesignalId) {
		this.onesignalId = onesignalId;
	}
	public String getHouseNoName() {
		return houseNoName;
	}
	public void setHouseNoName(String houseNoName) {
		this.houseNoName = houseNoName;
	}
	public String getStreet() {
		return street;
	}
	public void setStreet(String street) {
		this.street = street;
	}
	public String getLocality() {
		return locality;
	}
	public void setLocality(String locality) {
		this.locality = locality;
	}
	public String getLandMark() {
		return landMark;
	}
	public void setLandMark(String landMark) {
		this.landMark = landMark;
	}
	public String getErrorFlag() {
		return errorFlag;
	}
	public void setErrorFlag(String errorFlag) {
		this.errorFlag = errorFlag;
	}
	public String getTotalExperience() {
		return totalExperience;
	}
	public void setTotalExperience(String totalExperience) {
		this.totalExperience = totalExperience;
	}
	public String getAnnualSalary() {
		return annualSalary;
	}
	public void setAnnualSalary(String annualSalary) {
		this.annualSalary = annualSalary;
	}
	public String getCompanyName() {
		return companyName;
	}
	public void setCompanyName(String companyName) {
		this.companyName = companyName;
	}
	public String getUgQualification() {
		return ugQualification;
	}
	public void setUgQualification(String ugQualification) {
		this.ugQualification = ugQualification;
	}
	public String getAge() {
		return age;
	}
	public void setAge(String age) {
		this.age = age;
	}
	public String getHighestQualification() {
		return highestQualification;
	}
	public void setHighestQualification(String highestQualification) {
		this.highestQualification = highestQualification;
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
    public boolean isCertificateStudent(){
		ArrayList<String> certificatePrograms = new ArrayList<String>(
				Arrays.asList("CITM - DB","CITM - ET","CITM - ES","ACDM","CDM","CPM","ACOM","COM","ACWM","CWM","ACBM","CCC","CBM","ADSCM","ADITM","ADBFM"));
		if(certificatePrograms.contains(program)){
			return true;
		}else{
			return false;
		}
	}

    
	public String getDeRegistered() {
		return deRegistered;
	}


	public void setDeRegistered(String deRegistered) {
		this.deRegistered = deRegistered;
	}


	public String getNotificationType() {
		return notificationType;
	}
	public void setNotificationType(String notificationType) {
		this.notificationType = notificationType;
	}
	public String getSmsContent() {
		return smsContent;
	}
	public void setSmsContent(String smsContent) {
		this.smsContent = smsContent;
	}
	public String getPassword() {
		return password;
	}
	public void setPassword(String password) {
		this.password = password;
	}
	public String getUserId() {
		return userId;
	}
	public void setUserId(String userId) {
		this.userId = userId;
	}
	public String getExamMode() {
		/*
		 * if("PGDRM".equals(program) && "Jul2013".equals(prgmStructApplicable)){ return
		 * "Offline"; }else if("PGDRM".equals(program) &&
		 * "Jul2014".equals(prgmStructApplicable)){ return "Online"; }else
		 * if("Jul2014".equals(prgmStructApplicable) ||
		 * "Jul2013".equals(prgmStructApplicable) ||
		 * "Jul2017".equals(prgmStructApplicable) ||
		 * "Jul2018".equals(prgmStructApplicable) ||
		 * "Jul2019".equals(prgmStructApplicable)){ return "Online"; }else
		 * if("EPBM".equals(program) || "MPDV".equals(program)){ //For Executive
		 * Programs to be ONLINE return "Online"; }else if("CPBM".equals(program) &&
		 * "Jan2019".equals(prgmStructApplicable)){ return "Online"; }else{ return
		 * "Offline"; }
		 */
		
		if(("PGDRM".equals(program) && "Jul2013".equals(prgmStructApplicable) ) || "Jul2009".equals(prgmStructApplicable)  || "Jul2008".equals(prgmStructApplicable)){
    		return "Offline";
    	}else{
    		return "Online";
    	}
		
	}
	public void setExamMode(String examMode) {
		this.examMode = examMode;
	}
	
	
    
    
    public String getDesignation() {
		return designation;
	}
	public void setDesignation(String designation) {
		this.designation = designation;
	}
	public String getIndustry() {
		return industry;
	}
	public void setIndustry(String industry) {
		this.industry = industry;
	}
    public String getProgramStatus() {
		return programStatus;
	}
	public void setProgramStatus(String programStatus) {
		this.programStatus = programStatus;
	}
	public String getProgramRemarks() {
		return programRemarks;
	}
	public void setProgramRemarks(String programRemarks) {
		this.programRemarks = programRemarks;
	}
	public String getProgramCleared() {
		return programCleared;
	}
	public void setProgramCleared(String programCleared) {
		this.programCleared = programCleared;
	}
    public String getShippingAddress() {
		return shippingAddress;
	}
	public void setShippingAddress(String shippingAddress) {
		this.shippingAddress = shippingAddress;
	}
    
   	public String getProgramForHeader() {
   		return programForHeader;
   	}
   	public void setProgramForHeader(String programForHeader) {
   		this.programForHeader = programForHeader;
   	}
    
	public String getPreviousStudentId() {
		return previousStudentId;
	}
	public void setPreviousStudentId(String previousStudentId) {
		this.previousStudentId = previousStudentId;
	}
	public ArrayList<String> getWaivedOffSubjects() {
		return waivedOffSubjects;
	}
	public void setWaivedOffSubjects(ArrayList<String> waivedOffSubjects) {
		this.waivedOffSubjects = waivedOffSubjects;
	}
    
	public String getBody() {
		return body;
	}
	public void setBody(String body) {
		this.body = body;
	}
	public String getSubject() {
		return subject;
	}
	public void setSubject(String subject) {
		this.subject = subject;
	}
	public String getFromEmailId() {
		return fromEmailId;
	}
	public void setFromEmailId(String fromEmailId) {
		this.fromEmailId = fromEmailId;
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
	public String getOldProgram() {
		return oldProgram;
	}
	public void setOldProgram(String oldProgram) {
		this.oldProgram = oldProgram;
	}
	public String getProgramChanged() {
		return programChanged;
	}
	public void setProgramChanged(String programChanged) {
		this.programChanged = programChanged;
	}
	public String getSubjectsCleared() {
		return subjectsCleared;
	}
	public void setSubjectsCleared(String subjectsCleared) {
		this.subjectsCleared = subjectsCleared;
	}
	public String getImageUrl() {
		return imageUrl;
	}
	public void setImageUrl(String imageUrl) {
		this.imageUrl = imageUrl;
	}
	public String getUpdatedByStudent() {
		return updatedByStudent;
	}
	public void setUpdatedByStudent(String updatedByStudent) {
		this.updatedByStudent = updatedByStudent;
	}
	public String getPrgmStructApplicable() {
		return prgmStructApplicable;
	}
	public void setPrgmStructApplicable(String prgmStructApplicable) {
		this.prgmStructApplicable = prgmStructApplicable;
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
	public String getSapid() {
		return sapid;
	}
	public void setSapid(String sapid) {
		this.sapid = sapid;
	}
	public String getSem() {
		return sem;
	}
	public void setSem(String sem) {
		this.sem = sem;
	}
	public String getLastName() {
		return lastName;
	}
	public void setLastName(String lastName) {
		this.lastName = lastName;
	}
	public String getFirstName() {
		return firstName;
	}
	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}
	public String getMiddleName() {
		return middleName;
	}
	public void setMiddleName(String middleName) {
		this.middleName = middleName;
	}
	public String getFatherName() {
		return fatherName;
	}
	public void setFatherName(String fatherName) {
		this.fatherName = fatherName;
	}
	public String getHusbandName() {
		return husbandName;
	}
	public void setHusbandName(String husbandName) {
		this.husbandName = husbandName;
	}
	public String getMotherName() {
		return motherName;
	}
	public void setMotherName(String motherName) {
		this.motherName = motherName;
	}
	public String getGender() {
		return gender;
	}
	public void setGender(String gender) {
		this.gender = gender;
	}
	public String getProgram() {
		return program;
	}
	public void setProgram(String program) {
		this.program = program;
	}
	public String getEnrollmentMonth() {
		return enrollmentMonth;
	}
	public void setEnrollmentMonth(String enrollmentMonth) {
		this.enrollmentMonth = enrollmentMonth;
	}
	public String getEnrollmentYear() {
		return enrollmentYear;
	}
	public void setEnrollmentYear(String enrollmentYear) {
		this.enrollmentYear = enrollmentYear;
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
	public String getAltPhone() {
		return altPhone;
	}
	public void setAltPhone(String altPhone) {
		this.altPhone = altPhone;
	}
	public String getDob() {
		return dob;
	}
	public void setDob(String dob) {
		this.dob = dob;
	}
	public String getSpouseName() {
		return spouseName;
	}
	public void setSpouseName(String spouseName) {
		this.spouseName = spouseName;
	}
	public String getRegDate() {
		return regDate;
	}
	public void setRegDate(String regDate) {
		this.regDate = regDate;
	}
	public String getIsLateral() {
		return isLateral;
	}
	public void setIsLateral(String isLateral) {
		this.isLateral = isLateral;
	}
	public String getIsReReg() {
		return isReReg;
	}
	public void setIsReReg(String isReReg) {
		this.isReReg = isReReg;
	}
	public String getAddress() {
		return address;
	}
	public void setAddress(String address) {
		this.address = address;
	}
	public String getCity() {
		return city;
	}
	public void setCity(String city) {
		this.city = city;
	}
	public String getPostalCode() {
		return PostalCode;
	}
	public void setPostalCode(String postalCode) {
		PostalCode = postalCode;
	}
	public String getState() {
		return state;
	}
	public void setState(String state) {
		this.state = state;
	}
	public String getCountry() {
		return country;
	}
	public void setCountry(String country) {
		this.country = country;
	}
	public String getPin() {
		return pin;
	}
	public void setPin(String pin) {
		this.pin = pin;
	}
	public String getCenterCode() {
		return centerCode;
	}
	public void setCenterCode(String centerCode) {
		this.centerCode = centerCode;
	}
	public String getCenterName() {
		return centerName;
	}
	public void setCenterName(String centerName) {
		this.centerName = centerName;
	}
	public String getValidityEndMonth() {
		return validityEndMonth;
	}
	public void setValidityEndMonth(String validityEndMonth) {
		this.validityEndMonth = validityEndMonth;
	}
	public String getValidityEndYear() {
		return validityEndYear;
	}
	public void setValidityEndYear(String validityEndYear) {
		this.validityEndYear = validityEndYear;
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
	public String getChangedPassword() {
		return changedPassword;
	}
	public void setChangedPassword(String changedPassword) {
		this.changedPassword = changedPassword;
	}
	public String getN_Password() {
		return n_Password;
	}
	public void setN_Password(String n_Password) {
		this.n_Password = n_Password;
	}
	public String getC_password() {
		return c_password;
	}
	public void setC_password(String c_password) {
		this.c_password = c_password;
	}
	public String getDetailsConfirmedByStudent() {
		return detailsConfirmedByStudent;
	}
	public void setDetailsConfirmedByStudent(String detailsConfirmedByStudent) {
		this.detailsConfirmedByStudent = detailsConfirmedByStudent;
	}

	public String getEmail() {
		return email;
	}
	public void setEmail(String email) {
		this.email = email;
	}
	public Integer getAlmashinesId() {
		return almashinesId;
	}
	public void setAlmashinesId(Integer almashinesId) {
		this.almashinesId = almashinesId;
	}
	public String getSubjectsCount() {
		return subjectsCount;
	}
	public void setSubjectsCount(String subjectsCount) {
		this.subjectsCount = subjectsCount;
	}
	
	public String getProgramSemSubjectId() {
		return programSemSubjectId;
	}
	public void setProgramSemSubjectId(String programSemSubjectId) {
		this.programSemSubjectId = programSemSubjectId;
	}
	
	public boolean isPurchasedOtherPackages() {
		return purchasedOtherPackages;
	}
	public void setPurchasedOtherPackages(boolean purchasedOtherPackages) {
		this.purchasedOtherPackages = purchasedOtherPackages;
	}
	public String getPushContent() {
		return pushContent;
	}
	public void setPushContent(String pushContent) {
		this.pushContent = pushContent;
	}
	public String getIsPushnotification() {
		return isPushnotification;
	}
	public void setIsPushnotification(String isPushnotification) {
		this.isPushnotification = isPushnotification;
	}
	public ArrayList<String> getIndustryList() {
		return industryList;
	}
	public void setIndustryList(ArrayList<String> industryList) {
		this.industryList = industryList;
	}
	public ArrayList<String> getDesignationList() {
		return designationList;
	}
	public void setDesignationList(ArrayList<String> designationList) {
		this.designationList = designationList;
	}
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}
	public String getLoginType() {
		return loginType;
	}
	public void setLoginType(String loginType) {
		this.loginType = loginType;
	}
	public String getProgramType() {
		return programType;
	}
	public void setProgramType(String programType) {
		this.programType = programType;
	}
	public String getBloodGroup() {
		return bloodGroup;
	}
	public void setBloodGroup(String bloodGroup) {
		this.bloodGroup = bloodGroup;
	}
	public String getLc() {
		return lc;
	}
	public void setLc(String lc) {
		this.lc = lc;
	}
	
	public String getNewConsumerProgramStructureId() {
		return newConsumerProgramStructureId;
	}
	public void setNewConsumerProgramStructureId(String newConsumerProgramStructureId) {
		this.newConsumerProgramStructureId = newConsumerProgramStructureId;
	}
	
	public boolean isLouConfirmed() {
		return louConfirmed;
	}
	public void setLouConfirmed(boolean louConfirmed) {
		this.louConfirmed = louConfirmed;
	}
	public String getLouConfirmedTimestamp() {
		return louConfirmedTimestamp;
	}
	public void setLouConfirmedTimestamp(String louConfirmedTimestamp) {
		this.louConfirmedTimestamp = louConfirmedTimestamp;
	}
	
	@Override
	public String toString() {
		return "StudentStudentPortalBean [certificatePrograms=" + certificatePrograms + ", sapid=" + sapid + ", sem="
				+ sem + ", email=" + email + ", lastName=" + lastName + ", firstName=" + firstName + ", middleName="
				+ middleName + ", fatherName=" + fatherName + ", husbandName=" + husbandName + ", motherName="
				+ motherName + ", gender=" + gender + ", program=" + program + ", enrollmentMonth=" + enrollmentMonth
				+ ", enrollmentYear=" + enrollmentYear + ", emailId=" + emailId + ", mobile=" + mobile + ", altPhone="
				+ altPhone + ", dob=" + dob + ", regDate=" + regDate + ", isLateral=" + isLateral + ", isReReg="
				+ isReReg + ", address=" + address + ", houseNoName=" + houseNoName + ", street=" + street
				+ ", landMark=" + landMark + ", locality=" + locality + ", city=" + city + ", PostalCode=" + PostalCode
				+ ", state=" + state + ", country=" + country + ", shippingAddress=" + shippingAddress + ", examMode="
				+ examMode + ", designation=" + designation + ", industry=" + industry + ", userId=" + userId
				+ ", password=" + password + ", pin=" + pin + ", centerCode=" + centerCode + ", centerName="
				+ centerName + ", validityEndMonth=" + validityEndMonth + ", validityEndYear=" + validityEndYear
				+ ", createdBy=" + createdBy + ", createdDate=" + createdDate + ", lastModifiedBy=" + lastModifiedBy
				+ ", lastModifiedDate=" + lastModifiedDate + ", errorMessage=" + errorMessage + ", errorRecord="
				+ errorRecord + ", prgmStructApplicable=" + prgmStructApplicable + ", updatedByStudent="
				+ updatedByStudent + ", imageUrl=" + imageUrl + ", subjectsCleared=" + subjectsCleared + ", oldProgram="
				+ oldProgram + ", programChanged=" + programChanged + ", acadYear=" + acadYear + ", acadMonth="
				+ acadMonth + ", subject=" + subject + ", fromEmailId=" + fromEmailId + ", spouseName=" + spouseName
				+ ", body=" + body + ", previousStudentId=" + previousStudentId + ", waivedOffSubjects="
				+ waivedOffSubjects + ", waivedInSubjects=" + waivedInSubjects + ", waivedInSubjectSemMapping="
				+ waivedInSubjectSemMapping + ", programForHeader=" + programForHeader + ", programStatus="
				+ programStatus + ", programRemarks=" + programRemarks + ", smsContent=" + smsContent
				+ ", notificationType=" + notificationType + ", programCleared=" + programCleared + ", deRegistered="
				+ deRegistered + ", onesignalId=" + onesignalId + ", isPushnotification=" + isPushnotification
				+ ", pushContent=" + pushContent + ", changedPassword=" + changedPassword + ", n_Password=" + n_Password
				+ ", c_password=" + c_password + ", programStructure=" + programStructure + ", role=" + role
				+ ", loginType=" + loginType + ", programSemSubjectId=" + programSemSubjectId + ", bloodGroup="
				+ bloodGroup + ", programType=" + programType + ", lc=" + lc + ", perspective=" + perspective
				+ ", validityExpired=" + validityExpired + ", almashinesId=" + almashinesId + ", industryList="
				+ industryList + ", designationList=" + designationList + ", year=" + year + ", month=" + month
				+ ", emailSentProgramCleared=" + emailSentProgramCleared + ", existingStudentNoForDiscount="
				+ existingStudentNoForDiscount + ", errorFlag=" + errorFlag + ", totalExperience=" + totalExperience
				+ ", annualSalary=" + annualSalary + ", companyName=" + companyName + ", ugQualification="
				+ ugQualification + ", age=" + age + ", highestQualification=" + highestQualification
				+ ", consumerType=" + consumerType + ", consumerProgramStructureId=" + consumerProgramStructureId
				+ ", firebaseToken=" + firebaseToken + ", leadId=" + leadId + ", registrationNum=" + registrationNum
				+ ", error=" + error + ", detailsConfirmedByStudent=" + detailsConfirmedByStudent + ", status=" + status
				+ ", purchasedOtherPackages=" + purchasedOtherPackages + ", subjectsCount=" + subjectsCount + ",louConfirmed="+louConfirmed+",louConfirmedTimestamp="+louConfirmedTimestamp+"]";
	}
	
	public boolean isCorporateExamCenterStudent() {
		return isCorporateExamCenterStudent;
	}
	public void setCorporateExamCenterStudent(boolean isCorporateExamCenterStudent) {
		this.isCorporateExamCenterStudent = isCorporateExamCenterStudent;
	}
	public boolean isValidStudent() {
		return validStudent;
	}
	public void setValidStudent(boolean validStudent) {
		this.validStudent = validStudent;
	}
	public int getRConsumerProgramStructureId() {
		return RConsumerProgramStructureId;
	}
	public void setRConsumerProgramStructureId(int rConsumerProgramStructureId) {
		RConsumerProgramStructureId = rConsumerProgramStructureId;
	}
	private Integer provisionalAdmission;//0 - can book, 1 cannot book exam.

	public Integer getProvisionalAdmission() {
		return provisionalAdmission;
	}
	public void setProvisionalAdmission(Integer provisionalAdmission) {
		this.provisionalAdmission = provisionalAdmission;
	}

	
	  private boolean isCorporateExamCenterStudent;
	  private boolean validStudent;
	  private int RConsumerProgramStructureId;

	public String getBatchName() {
		return batchName;
	}
	public void setBatchName(String batchName) {
		this.batchName = batchName;
	}
	public String getAbcId() {
		return abcId;
	}
	public void setAbcId(String abcId) {
		this.abcId = abcId;
	}

	public String getSettingType() {
		return settingType;
	}
	public void setSettingType(String settingType) {
		this.settingType = settingType;
	}
	public String getIsEnable() {
		return isEnable;
	}
	public void setIsEnable(String isEnable) {
		this.isEnable = isEnable;
	}
	public int getHighContrast() {
		return highContrast;
	}
	public void setHighContrast(int highContrast) {
		this.highContrast = highContrast;
	}
	public int getTextToSpeech() {
		return textToSpeech;
	}
	public void setTextToSpeech(int textToSpeech) {
		this.textToSpeech = textToSpeech;
	}
	

}