package com.nmims.beans;

import java.io.Serializable;

public class StudentBean implements Serializable, Comparable<StudentBean>{
	
	private String sapid;
    private String sem;
    private String lastName;
    private String firstName;
    private String middleName;
    private String fatherName;
    private String husbandName;
    private String motherName;
    private String gender;
    private String program;
    private String password;
    private String programname;
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
    private String locality;
    private String landMark;
    private String city;
    private String state;
    private String country;
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
    private String year;
    private String month;
    private String sapIdList;
    private String oldSem;
    private String subject;
    private String lc;
    private String previousStudentId;
    private String totalMarks;
    private String id;
    private String accountId;
    private String opportunityId;
    private boolean opportunitySyncedWithStudentZone;
    private boolean accountSyncedWithLDAP;
    private String studentZoneSyncErrorTable;
    private String existingStudentNoForDiscount;
    private String programStatus;
    private String totalExperience;
    private String annualSalary;
    private String companyName;
    private String ugQualification;
    private String age;
    private String highestQualification;
    private String industry;
    private String designation;
    private String status;
    private String examMode;
    private String skuType;
    private String orderType; 
    private String filePath;
    private String trackingNumber;
    private String shippingMode;
    private String bloodGroup;
    private String deliveredDateTime;
    private String specializationType;
    private String specialisation1;
    private String specialisation2;
    private String batchName;
    
    public String getSpecializationType() {
		return specializationType;
	}

	public void setSpecializationType(String specializationType) {
		this.specializationType = specializationType;
	}

	public String getSpecialisation1() {
		return specialisation1;
	}

	public void setSpecialisation1(String specialisation1) {
		this.specialisation1 = specialisation1;
	}

	public String getSpecialisation2() {
		return specialisation2;
	}

	public void setSpecialisation2(String specialisation2) {
		this.specialisation2 = specialisation2;
	}

	public String getFilePath() {
		return filePath;
	}

	public void setFilePath(String filePath) {
		this.filePath = filePath;
	}

	public String getTrackingNumber() {
		return trackingNumber;
	}

	public void setTrackingNumber(String trackingNumber) {
		this.trackingNumber = trackingNumber;
	}

	public String getShippingMode() {
		return shippingMode;
	}

	public void setShippingMode(String shippingMode) {
		this.shippingMode = shippingMode;
	}

	public String getSkuType() {
		return skuType;
	}

	public void setSkuType(String skuType) {
		this.skuType = skuType;
	}

	public String getOrderType() {
		return orderType;
	}

	public void setOrderType(String orderType) {
		this.orderType = orderType;
	}

	public void setExamMode(String examMode) {
		this.examMode = examMode;
	}

	private String closeDate;
    

    private String consumerType;
    private String programId;
    private String programStructureId;
    private String consumerTypeId;
    private String name;
    private String isCorporate;
    private String code;
    private String program_structure;
    private String program_master;
    private String consumerProgramStructureId;
    
    private String purhcasedCSProduct;
    
    
    public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String getProgramId() {
		return programId;
	}

	public void setProgramId(String programId) {
		this.programId = programId;
	}

	public String getProgramStructureId() {
		return programStructureId;
	}

	public void setProgramStructureId(String programStructureId) {
		this.programStructureId = programStructureId;
	}

	public String getConsumerTypeId() {
		return consumerTypeId;
	}

	public void setConsumerTypeId(String consumerTypeId) {
		this.consumerTypeId = consumerTypeId;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getIsCorporate() {
		return isCorporate;
	}

	public void setIsCorporate(String isCorporate) {
		this.isCorporate = isCorporate;
	}

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public String getProgram_structure() {
		return program_structure;
	}

	public void setProgram_structure(String program_structure) {
		this.program_structure = program_structure;
	}

	public String getExamMode() {

    	    	if("PGDRM".equals(program) && "Jul2013".equals(prgmStructApplicable)){
    	    		return "Offline";
    	    	}else if("PGDRM".equals(program) && "Jul2014".equals(prgmStructApplicable)){
    	    		return "Online";
    	    	}else if("Jul2014".equals(prgmStructApplicable) || "Jul2013".equals(prgmStructApplicable) || "Jul2017".equals(prgmStructApplicable) || "Jul2018".equals(prgmStructApplicable) || "Jan2019".equals(prgmStructApplicable ) || "Jul2019".equals(prgmStructApplicable)){
    	    		return "Online";
    	    	}else if("EPBM".equals(program) || "MPDV".equals(program)){ //For Executive Programs to be ONLINE
    	    		return "Online";
    	    	}else{
    	    		return "Offline";
    	    	}
    			
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

	public String getIndustry() {
		return industry;
	}

	public void setIndustry(String industry) {
		this.industry = industry;
	}

	public String getDesignation() {
		return designation;
	}

	public void setDesignation(String designation) {
		this.designation = designation;
	}

	public void setOpportunitySyncedWithStudentZone(boolean opportunitySyncedWithStudentZone) {
		this.opportunitySyncedWithStudentZone = opportunitySyncedWithStudentZone;
	}

	public void setAccountSyncedWithLDAP(boolean accountSyncedWithLDAP) {
		this.accountSyncedWithLDAP = accountSyncedWithLDAP;
	}

	/**
	 * @return the landMark
	 */
	public String getLandMark() {
		return landMark;
	}

	/**
	 * @param landMark the landMark to set
	 */
	public void setLandMark(String landMark) {
		this.landMark = landMark;
	}

	/**
	 * @return the houseNoName
	 */
	public String getHouseNoName() {
		return houseNoName;
	}

	/**
	 * @param houseNoName the houseNoName to set
	 */
	public void setHouseNoName(String houseNoName) {
		this.houseNoName = houseNoName;
	}

	/**
	 * @return the street
	 */
	public String getStreet() {
		return street;
	}

	/**
	 * @param street the street to set
	 */
	public void setStreet(String street) {
		this.street = street;
	}

	/**
	 * @return the locality
	 */
	public String getLocality() {
		return locality;
	}

	/**
	 * @param locality the locality to set
	 */
	public void setLocality(String locality) {
		this.locality = locality;
	}

	/**
	 * @return the programStatus
	 */
	public String getProgramStatus() {
		return programStatus;
	}

	/**
	 * @param programStatus the programStatus to set
	 */
	public void setProgramStatus(String programStatus) {
		this.programStatus = programStatus;
	}

	public String getStudentZoneSyncErrorTable() {
		return studentZoneSyncErrorTable;
	}

	public void setStudentZoneSyncErrorTable(String studentZoneSyncErrorTable) {
		this.studentZoneSyncErrorTable = studentZoneSyncErrorTable;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public boolean isAccountSyncedWithLDAP() {
		return accountSyncedWithLDAP;
	}

	public void setAccountSyncedWithLDAP(String accountSyncedWithLDAP) {
		if(accountSyncedWithLDAP.equals("true")){
		this.accountSyncedWithLDAP = true;
		}else{
			this.accountSyncedWithLDAP = false;
		}
	}

	public boolean isOpportunitySyncedWithStudentZone() {
		return opportunitySyncedWithStudentZone;
	}

	public void setOpportunitySyncedWithStudentZone(String opportunitySyncedWithStudentZone) {
		if(opportunitySyncedWithStudentZone.equals("true")){
			this.opportunitySyncedWithStudentZone = true;
		}else{
			this.opportunitySyncedWithStudentZone = false;
		}
	}

	public String getAccountId() {
		return accountId;
	}

	public void setAccountId(String accountId) {
		this.accountId = accountId;
	}

	public String getOpportunityId() {
		return opportunityId;
	}

	public void setOpportunityId(String opportunityId) {
		this.opportunityId = opportunityId;
	}

	public String getPreviousStudentId() {
		return previousStudentId;
	}

	public void setPreviousStudentId(String previousStudentId) {
		this.previousStudentId = previousStudentId;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getTotalMarks() {
		return totalMarks;
	}
	
	public String getProgramname() {
		return programname;
	}

	public void setProgramname(String programname) {
		this.programname = programname;
	}

	public void setTotalMarks(String totalMarks) {
		this.totalMarks = totalMarks;
	}
	public String getLc() {
		return lc;
	}
	public void setLc(String lc) {
		this.lc = lc;
	}
	public String getSubject() {
		return subject;
	}
	public void setSubject(String subject) {
		this.subject = subject;
	}
	public String getOldSem() {
		return oldSem;
	}
	public void setOldSem(String oldSem) {
		this.oldSem = oldSem;
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
	public String getSapIdList() {
		return sapIdList;
	}
	public void setSapIdList(String sapIdList) {
		this.sapIdList = sapIdList;
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
    
	@Override
	public int compareTo(StudentBean bean) {
		return sapid.compareTo(bean.sapid);
	}

	@Override
	public String toString() {
	
		try {
			return "StudentBean [sapid=" + sapid + ", sem=" + sem + ", lastName=" + lastName + ", firstName=" + firstName
					+ ", middleName=" + middleName + ", fatherName=" + fatherName + ", husbandName=" + husbandName
					+ ", motherName=" + motherName + ", gender=" + gender + ", program=" + program + ", password="
					+ password + ", programname=" + programname + ", enrollmentMonth=" + enrollmentMonth
					+ ", enrollmentYear=" + enrollmentYear + ", emailId=" + emailId + ", mobile=" + mobile + ", altPhone="
					+ altPhone + ", dob=" + dob + ", regDate=" + regDate + ", isLateral=" + isLateral + ", isReReg="
					+ isReReg + ", address=" + address + ", houseNoName=" + houseNoName + ", street=" + street
					+ ", locality=" + locality + ", landMark=" + landMark + ", city=" + city + ", state=" + state
					+ ", country=" + country + ", pin=" + pin + ", centerCode=" + centerCode + ", centerName=" + centerName
					+ ", validityEndMonth=" + validityEndMonth + ", validityEndYear=" + validityEndYear + ", createdBy="
					+ createdBy + ", createdDate=" + createdDate + ", lastModifiedBy=" + lastModifiedBy
					+ ", lastModifiedDate=" + lastModifiedDate + ", errorMessage=" + errorMessage + ", errorRecord="
					+ errorRecord + ", prgmStructApplicable=" + prgmStructApplicable + ", updatedByStudent="
					+ updatedByStudent + ", imageUrl=" + imageUrl + ", subjectsCleared=" + subjectsCleared + ", oldProgram="
					+ oldProgram + ", programChanged=" + programChanged + ", year=" + year + ", month=" + month
					+ ", sapIdList=" + sapIdList + ", oldSem=" + oldSem + ", subject=" + subject + ", lc=" + lc
					+ ", previousStudentId=" + previousStudentId + ", totalMarks=" + totalMarks + ", id=" + id
					+ ", accountId=" + accountId + ", opportunityId=" + opportunityId
					+ ", opportunitySyncedWithStudentZone=" + opportunitySyncedWithStudentZone + ", accountSyncedWithLDAP="
					+ accountSyncedWithLDAP + ", studentZoneSyncErrorTable=" + studentZoneSyncErrorTable
					+ ", existingStudentNoForDiscount=" + existingStudentNoForDiscount + ", programStatus=" + programStatus
					+ ", totalExperience=" + totalExperience + ", annualSalary=" + annualSalary + ", companyName="
					+ companyName + ", ugQualification=" + ugQualification + ", age=" + age + ", highestQualification="
					+ highestQualification + ", industry=" + industry + ", designation=" + designation + ", status="
					+ status + ", examMode=" + examMode + ", skuType=" + skuType + ", orderType=" + orderType
					+ ", filePath=" + filePath + ", trackingNumber=" + trackingNumber + ", shippingMode=" + shippingMode
					+ ", bloodGroup=" + bloodGroup + ", deliveredDateTime=" + deliveredDateTime + ", specializationType="
					+ specializationType + ", specialisation1=" + specialisation1 + ", specialisation2=" + specialisation2
					+ ", closeDate=" + closeDate + ", consumerType=" + consumerType + ", programId=" + programId
					+ ", programStructureId=" + programStructureId + ", consumerTypeId=" + consumerTypeId + ", name=" + name
					+ ", isCorporate=" + isCorporate + ", code=" + code + ", program_structure=" + program_structure
					+ ", program_master=" + program_master + ", consumerProgramStructureId=" + consumerProgramStructureId
					+ ", purhcasedCSProduct=" + purhcasedCSProduct + "]";
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return "error in generation student bean";
	}

	public String getExistingStudentNoForDiscount() {
		return existingStudentNoForDiscount;
	}

	public void setExistingStudentNoForDiscount(
			String existingStudentNoForDiscount) {
		this.existingStudentNoForDiscount = existingStudentNoForDiscount;
	}

	public String getCloseDate() {
		return closeDate;
	}

	public void setCloseDate(String closeDate) {
		this.closeDate = closeDate;
	}


	public String getConsumerType() {
		return consumerType;
	}

	public void setConsumerType(String consumerType) {
		this.consumerType = consumerType;
	}

	public String getProgram_master() {
		return program_master;
	}

	public void setProgram_master(String program_master) {
		this.program_master = program_master;
	}

	public String getConsumerProgramStructureId() {
		return consumerProgramStructureId;
	}

	public void setConsumerProgramStructureId(String consumerProgramStructureId) {
		this.consumerProgramStructureId = consumerProgramStructureId;

	}

	public String getPurhcasedCSProduct() {
		return purhcasedCSProduct;
	}

	public void setPurhcasedCSProduct(String purhcasedCSProduct) {
		this.purhcasedCSProduct = purhcasedCSProduct;
	}

	public String getBloodGroup() {
		return bloodGroup;
	}

	public void setBloodGroup(String bloodGroup) {
		this.bloodGroup = bloodGroup;
	}
	
	public String getDeliveredDateTime() {
		return deliveredDateTime;
	}

	public void setDeliveredDateTime(String deliveredDateTime) {
		this.deliveredDateTime = deliveredDateTime;
	}

	public String getBatchName() {
		return batchName;
	}

	public void setBatchName(String batchName) {
		this.batchName = batchName;
	}
	
}
