package com.nmims.beans;

import java.io.Serializable;
import org.apache.commons.lang.StringUtils;
import java.util.ArrayList;
import java.util.HashMap;

//spring security related changes rename StudentBean to StudentExamBean
public class StudentExamBean extends BaseExamBean implements Serializable, Comparable<StudentExamBean> {
	
//	private static final long serialVersionUID = 1L; commented by Abhay
	
	public StudentExamBean() {
		super();
	}
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
    private String enrollmentMonth;
    private String enrollmentYear;
    private String emailId;
    private String mobile;
    private String altPhone;
    private String dob;
    private String age;
    private String regDate;
    private String isLateral;
    private String isReReg;
    private String address; 
    private String houseNoName; 
    private String street; 
    private String landMark; 
    private String locality; 
    private String pin; 
    private String city; 
    private String state; 
    private String country;
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
    private String examMode;
	
	private String subjectsCleared;
   
    private String programChanged;
    private String acadYear;
    private String acadMonth;
    private String sapIdList;
    private String oldSem;
    private String subject;
    private String lc;
    private String totalMarks;
    private boolean validStudent;
    private String previousStudentId;
    private ArrayList<String> waivedOffSubjects = new ArrayList();
    private ArrayList<String> waivedInSubjects = new ArrayList<String>();
    private HashMap<String, String> waivedInSubjectSemMapping = new HashMap<String, String>();
    private String programForHeader;
    //Two fields added for active student report//
    private String gapInMostRecentRegistrationAndCurrentDateInDays;
    private String mostRecentRegistration;
    //End//
    // add field in order to disable student login ,Email for Student 
    private String programStatus;
    private String programRemarks;
    //End
    private String programCleared;
    private String highestQualification;
    private String designation;
    private String industry;
    public String keyOfSapidAndSemester;
    public String count;
    public String gap;
    public String driveMonthYear;
    public String commaSeperatedSubjects;
    private boolean isCorporateExamCenterStudent;
    private String mappedCorporateExamCenterId;
    
    
    //To maintain validity history
    private String oldValidityEndMonth;
    private String oldValidityEndYear;
    private String newValidityEndMonth;
    private String newValidityEndYear;
    private String fullName;
    private String oldValidity;
    private String newValidity;
    private String registered;
    private String consumerType;
    private String serviceRequestIdList;
    private String consumerProgramStructureId;

    private boolean purchasedOtherPackages;

    private String leadId;
    private String registrationNum;
    private String leadImageUrl;
    private boolean error;


//  new fields for CPSI changes
    private String Rsapid;
    private String Rprogram;
    private int RConsumerProgramStructureId;
    private String firebaseToken;
    private String onesignalId;
	private String programType;
    private String previousPrgmStructApplicable;
    private String logoRequired ;
  	//Added by Pranit on 22Apr20, to determine if api hit is fromAdmin or not
  	private String fromAdmin;
  	private String total;
  	
  	private Integer provisionalAdmission;//0 - can book, 1 cannot book exam.
  	
  	//required for Bonafide Certificate
  	private String Specialisation;
  	
  	 private String maxExamDate;
     private String oldProgram;
     
     //required for project submission status  
  	 private String status;
  	//required for project payment status  
  	 private String booked;
  	 
  	 private String abcId;
  	 private int textToSpeech;
  	 private int highContrast;

	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}
	public String getBooked() {
		return booked;
	}
	public void setBooked(String booked) {
		this.booked = booked;
	}
	public String getServiceRequestIdList() {
		return serviceRequestIdList;
	}
	public void setServiceRequestIdList(String serviceRequestIdList) {
		this.serviceRequestIdList = serviceRequestIdList;
	}
	public String getTotal() {
		return total;
	}
	public void setTotal(String total) {
		this.total = total;
	}
	public ArrayList<String> getWaivedInSubjects() {
		return waivedInSubjects;
	}
	public void setWaivedInSubjects(ArrayList<String> waivedInSubjects) {
		this.waivedInSubjects = waivedInSubjects;
	}
	public HashMap<String, String> getWaivedInSubjectSemMapping() {
		return waivedInSubjectSemMapping;
	}
	public void setWaivedInSubjectSemMapping(HashMap<String, String> waivedInSubjectSemMapping) {
		this.waivedInSubjectSemMapping = waivedInSubjectSemMapping;
	}
	public String getFromAdmin() {
		return fromAdmin;
	}
	public void setFromAdmin(String fromAdmin) {
		this.fromAdmin = fromAdmin;
	}
  public String getLogoRequired() {
		return logoRequired;
	}
	public void setLogoRequired(String logoRequired) {
		this.logoRequired = logoRequired;
	}
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
	public String getLeadImageUrl() {
		return leadImageUrl;
	}
	public void setLeadImageUrl(String leadImageUrl) {
		this.leadImageUrl = leadImageUrl;
	}
	public boolean isError() {
		return error;
	}
	public void setError(boolean error) {
		this.error = error;
	}

	public String getOnesignalId() {
		return onesignalId;
	}
	public void setOnesignalId(String onesignalId) {
		this.onesignalId = onesignalId;
	}
	public String getFirebaseToken() {
		return firebaseToken;
	  }
	public void setFirebaseToken(String firebaseToken) {
		this.firebaseToken = firebaseToken;
	}
	public int getRConsumerProgramStructureId() {

		return RConsumerProgramStructureId;
	}
	public void setRConsumerProgramStructureId(int rconsumerProgramStructureId) {
		RConsumerProgramStructureId = rconsumerProgramStructureId;
	}
	public String getRprogram() {
		return Rprogram;
	}
	public void setRprogram(String rprogram) {
		Rprogram = rprogram;
	}
	public String getRsapid() {
		return Rsapid;
	}
	public void setRsapid(String rsapid) {
		Rsapid = rsapid;
	}
    /**
	 * @return the consumerType
	 */

	/**
	 * @param consumerType the consumerType to set
	 */

    public String getConsumerProgramStructureId() {
		return consumerProgramStructureId;
	}
	public void setConsumerProgramStructureId(String consumerProgramStructureId) {
		this.consumerProgramStructureId = consumerProgramStructureId;
	}
	public String getConsumerType() {
		return consumerType;
	}

	public void setConsumerType(String consumerType) {
		this.consumerType = consumerType;
	}
	public String getRegistered() {
		return registered;
	}
	public void setRegistered(String registered) {
		this.registered = registered;
	}
	public String getOldValidityEndMonth() {
  		return oldValidityEndMonth;
  	}
  	public void setOldValidityEndMonth(String oldValidityEndMonth) {
  		this.oldValidityEndMonth = oldValidityEndMonth;
  	}
  	public String getOldValidityEndYear() {
  		return oldValidityEndYear;
  	}
  	public void setOldValidityEndYear(String oldValidityEndYear) {
  		this.oldValidityEndYear = oldValidityEndYear;
  	}
  	public String getNewValidityEndMonth() {
  		return newValidityEndMonth;
  	}
  	public void setNewValidityEndMonth(String newValidityEndMonth) {
  		this.newValidityEndMonth = newValidityEndMonth;
  	}
  	public String getNewValidityEndYear() {
  		return newValidityEndYear;
  	}
  	public void setNewValidityEndYear(String newValidityEndYear) {
  		this.newValidityEndYear = newValidityEndYear;
  	}
  	public String getFullName() {
  		return firstName + " "+lastName;
  	}
  	public void setFullName(String fullName) {
  		this.fullName = fullName;
  	}
  	public String getOldValidity() {
		if (StringUtils.isBlank(oldValidityEndMonth)  || StringUtils.isBlank(oldValidityEndYear) ){
			return	validityEndMonth+" - "+validityEndYear;
		}else{
			return oldValidityEndMonth+" - "+oldValidityEndYear;
		}
	}
	public void setOldValidity(String oldValidity) {
		this.oldValidity = oldValidity;
	}
	public String getNewValidity() {
		if (StringUtils.isBlank(newValidityEndMonth) || StringUtils.isBlank(newValidityEndYear) ){
			return	validityEndMonth+" - "+validityEndYear;
		}else{
			return newValidityEndMonth+" - "+newValidityEndYear;
		}
		
	}
	public void setNewValidity(String newValidity) {
		this.newValidity = newValidity;
	}
    //
    
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
	public String getMappedCorporateExamCenterId() {
		return mappedCorporateExamCenterId;
	}
	public void setMappedCorporateExamCenterId(String mappedCorporateExamCenterId) {
		this.mappedCorporateExamCenterId = mappedCorporateExamCenterId;
	}
	public boolean isCorporateExamCenterStudent() {
		return isCorporateExamCenterStudent;
	}
	public void setCorporateExamCenterStudent(boolean isCorporateExamCenterStudent) {
		this.isCorporateExamCenterStudent = isCorporateExamCenterStudent;
	}
	public String getCommaSeperatedSubjects() {
    	return commaSeperatedSubjects;
    }
    public void setCommaSeperatedSubjects(String commaSeperatedSubjects) {
    	this.commaSeperatedSubjects = commaSeperatedSubjects;
    }
    public String getDriveMonthYear() {
    	return driveMonthYear;
    }
    public void setDriveMonthYear(String driveMonthYear) {
    	this.driveMonthYear = driveMonthYear;
    }
   
    public String getGap() {
    	return gap;
    }
    public void setGap(String gap) {
    	this.gap = gap;
    }
    public String getKeyOfSapidAndSemester() {
    	return keyOfSapidAndSemester;
    }
    public void setKeyOfSapidAndSemester(String keyOfSapidAndSemester) {
    	this.keyOfSapidAndSemester = keyOfSapidAndSemester;
    }
    public String getCount() {
    	return count;
    }
    public void setCount(String count) {
    	this.count = count;
    }
    public String getHighestQualification() {
		return highestQualification;
	}
	public void setHighestQualification(String highestQualification) {
		this.highestQualification = highestQualification;
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
    public String getProgramCleared() {
		return programCleared;
	}
	public void setProgramCleared(String programCleared) {
		this.programCleared = programCleared;
	}
    public String getGapInMostRecentRegistrationAndCurrentDateInDays() {
		return gapInMostRecentRegistrationAndCurrentDateInDays;
	}
	public void setGapInMostRecentRegistrationAndCurrentDateInDays(
			String gapInMostRecentRegistrationAndCurrentDateInDays) {
		this.gapInMostRecentRegistrationAndCurrentDateInDays = gapInMostRecentRegistrationAndCurrentDateInDays;
	}
	public String getMostRecentRegistration() {
		return mostRecentRegistration;
	}
	public void setMostRecentRegistration(String mostRecentRegistration) {
		this.mostRecentRegistration = mostRecentRegistration;
	}
	public String getProgramForHeader() {
		return programForHeader;
	}
	public void setProgramForHeader(String programForHeader) {
		this.programForHeader = programForHeader;
	}
	public ArrayList<String> getWaivedOffSubjects() {
		return waivedOffSubjects;
	}
	public void setWaivedOffSubjects(ArrayList<String> waivedOffSubjects) {
		this.waivedOffSubjects = waivedOffSubjects;
	}
	public String getPreviousStudentId() {
		return previousStudentId;
	}
	public void setPreviousStudentId(String previousStudentId) {
		this.previousStudentId = previousStudentId;
	}
	public boolean isValidStudent() {
		return validStudent;
	}
	public void setValidStudent(boolean validStudent) {
		this.validStudent = validStudent;
	}
	public String getTotalMarks() {
		return totalMarks;
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
	public String getAge() {
		return age;
	}
	public void setAge(String age) {
		this.age = age;
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
	public String getLandMark() {
		return landMark;
	}
	public void setLandMark(String landMark) {
		this.landMark = landMark;
	}
	public String getLocality() {
		return locality;
	}
	public void setLocality(String locality) {
		this.locality = locality;
	}
	public String getPin() {
		return pin;
	}
	public void setPin(String pin) {
		this.pin = pin;
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
		
	public String getPreviousPrgmStructApplicable() {
		return previousPrgmStructApplicable;
	}
	public void setPreviousPrgmStructApplicable(String previousPrgmStructApplicable) {
		this.previousPrgmStructApplicable = previousPrgmStructApplicable;
	}
	@Override
	public int compareTo(StudentExamBean bean) {
		return sapid.compareTo(bean.sapid);
	}
	
	public String getSpecialisation() {
		return Specialisation;
	}
	public void setSpecialisation(String specialisation) {
		Specialisation = specialisation;
	}
	public String getMaxExamDate() {
		return maxExamDate;
	}
	public void setMaxExamDate(String maxExamDate) {
		this.maxExamDate = maxExamDate;
	}
	
	@Override
	public String toString() {
		return "StudentExamBean [sapid=" + sapid + ", sem=" + sem + ", lastName=" + lastName + ", firstName="
				+ firstName + ", middleName=" + middleName + ", fatherName=" + fatherName + ", husbandName="
				+ husbandName + ", motherName=" + motherName + ", gender=" + gender + ", program=" + program
				+ ", enrollmentMonth=" + enrollmentMonth + ", enrollmentYear=" + enrollmentYear + ", emailId=" + emailId
				+ ", mobile=" + mobile + ", altPhone=" + altPhone + ", dob=" + dob + ", age=" + age + ", regDate="
				+ regDate + ", isLateral=" + isLateral + ", isReReg=" + isReReg + ", address=" + address
				+ ", houseNoName=" + houseNoName + ", street=" + street + ", landMark=" + landMark + ", locality="
				+ locality + ", pin=" + pin + ", city=" + city + ", state=" + state + ", country=" + country
				+ ", centerCode=" + centerCode + ", centerName=" + centerName + ", validityEndMonth=" + validityEndMonth
				+ ", validityEndYear=" + validityEndYear + ", createdBy=" + createdBy + ", createdDate=" + createdDate
				+ ", lastModifiedBy=" + lastModifiedBy + ", lastModifiedDate=" + lastModifiedDate + ", errorMessage="
				+ errorMessage + ", errorRecord=" + errorRecord + ", prgmStructApplicable=" + prgmStructApplicable
				+ ", updatedByStudent=" + updatedByStudent + ", imageUrl=" + imageUrl + ", examMode=" + examMode
				+ ", subjectsCleared=" + subjectsCleared + ", programChanged=" + programChanged + ", acadYear="
				+ acadYear + ", acadMonth=" + acadMonth + ", sapIdList=" + sapIdList + ", oldSem=" + oldSem
				+ ", subject=" + subject + ", lc=" + lc + ", totalMarks=" + totalMarks + ", validStudent="
				+ validStudent + ", previousStudentId=" + previousStudentId + ", waivedOffSubjects=" + waivedOffSubjects
				+ ", waivedInSubjects=" + waivedInSubjects + ", waivedInSubjectSemMapping=" + waivedInSubjectSemMapping
				+ ", programForHeader=" + programForHeader + ", gapInMostRecentRegistrationAndCurrentDateInDays="
				+ gapInMostRecentRegistrationAndCurrentDateInDays + ", mostRecentRegistration=" + mostRecentRegistration
				+ ", programStatus=" + programStatus + ", programRemarks=" + programRemarks + ", programCleared="
				+ programCleared + ", highestQualification=" + highestQualification + ", designation=" + designation
				+ ", industry=" + industry + ", keyOfSapidAndSemester=" + keyOfSapidAndSemester + ", count=" + count
				+ ", gap=" + gap + ", driveMonthYear=" + driveMonthYear + ", commaSeperatedSubjects="
				+ commaSeperatedSubjects + ", isCorporateExamCenterStudent=" + isCorporateExamCenterStudent
				+ ", mappedCorporateExamCenterId=" + mappedCorporateExamCenterId + ", oldValidityEndMonth="
				+ oldValidityEndMonth + ", oldValidityEndYear=" + oldValidityEndYear + ", newValidityEndMonth="
				+ newValidityEndMonth + ", newValidityEndYear=" + newValidityEndYear + ", fullName=" + fullName
				+ ", oldValidity=" + oldValidity + ", newValidity=" + newValidity + ", registered=" + registered
				+ ", consumerType=" + consumerType + ", serviceRequestIdList=" + serviceRequestIdList
				+ ", ConsumerProgramStructureId=" + consumerProgramStructureId + ", purchasedOtherPackages="
				+ purchasedOtherPackages + ", leadId=" + leadId + ", registrationNum=" + registrationNum
				+ ", leadImageUrl=" + leadImageUrl + ", error=" + error + ", Rsapid=" + Rsapid + ", Rprogram="
				+ Rprogram + ", RConsumerProgramStructureId=" + RConsumerProgramStructureId + ", firebaseToken="
				+ firebaseToken + ", onesignalId=" + onesignalId + ", programType=" + programType
				+ ", previousPrgmStructApplicable=" + previousPrgmStructApplicable + ", logoRequired=" + logoRequired
				+ ", fromAdmin=" + fromAdmin + ", total=" + total + ", provisionalAdmission=" + provisionalAdmission
				+ ", Specialisation=" + Specialisation + ", maxExamDate=" + maxExamDate + ", oldProgram=" + oldProgram
				+ ", status=" + status + ", booked=" + booked + ", getStatus()=" + getStatus() + ", getBooked()="
				+ getBooked() + ", getServiceRequestIdList()=" + getServiceRequestIdList() + ", getTotal()="
				+ getTotal() + ", getWaivedInSubjects()=" + getWaivedInSubjects() + ", getWaivedInSubjectSemMapping()="
				+ getWaivedInSubjectSemMapping() + ", getFromAdmin()=" + getFromAdmin() + ", getLogoRequired()="
				+ getLogoRequired() + ", getLeadId()=" + getLeadId() + ", getRegistrationNum()=" + getRegistrationNum()
				+ ", getLeadImageUrl()=" + getLeadImageUrl() + ", isError()=" + isError() + ", getOnesignalId()="
				+ getOnesignalId() + ", getFirebaseToken()=" + getFirebaseToken()
				+ ", getRConsumerProgramStructureId()=" + getRConsumerProgramStructureId() + ", getRprogram()="
				+ getRprogram() + ", getRsapid()=" + getRsapid() + ", getConsumerProgramStructureId()="
				+ getConsumerProgramStructureId() + ", getConsumerType()=" + getConsumerType() + ", getRegistered()="
				+ getRegistered() + ", getOldValidityEndMonth()=" + getOldValidityEndMonth()
				+ ", getOldValidityEndYear()=" + getOldValidityEndYear() + ", getNewValidityEndMonth()="
				+ getNewValidityEndMonth() + ", getNewValidityEndYear()=" + getNewValidityEndYear() + ", getFullName()="
				+ getFullName() + ", getOldValidity()=" + getOldValidity() + ", getNewValidity()=" + getNewValidity()
				+ ", getAcadYear()=" + getAcadYear() + ", getAcadMonth()=" + getAcadMonth()
				+ ", getMappedCorporateExamCenterId()=" + getMappedCorporateExamCenterId()
				+ ", isCorporateExamCenterStudent()=" + isCorporateExamCenterStudent()
				+ ", getCommaSeperatedSubjects()=" + getCommaSeperatedSubjects() + ", getDriveMonthYear()="
				+ getDriveMonthYear() + ", getGap()=" + getGap() + ", getKeyOfSapidAndSemester()="
				+ getKeyOfSapidAndSemester() + ", getCount()=" + getCount() + ", getHighestQualification()="
				+ getHighestQualification() + ", getDesignation()=" + getDesignation() + ", getIndustry()="
				+ getIndustry() + ", getExamMode()=" + getExamMode() + ", getProgramCleared()=" + getProgramCleared()
				+ ", getGapInMostRecentRegistrationAndCurrentDateInDays()="
				+ getGapInMostRecentRegistrationAndCurrentDateInDays() + ", getMostRecentRegistration()="
				+ getMostRecentRegistration() + ", getProgramForHeader()=" + getProgramForHeader()
				+ ", getWaivedOffSubjects()=" + getWaivedOffSubjects() + ", getPreviousStudentId()="
				+ getPreviousStudentId() + ", isValidStudent()=" + isValidStudent() + ", getTotalMarks()="
				+ getTotalMarks() + ", getLc()=" + getLc() + ", getSubject()=" + getSubject() + ", getOldSem()="
				+ getOldSem() + ", getSapIdList()=" + getSapIdList() + ", getOldProgram()=" + getOldProgram()
				+ ", getProgramChanged()=" + getProgramChanged() + ", getSubjectsCleared()=" + getSubjectsCleared()
				+ ", getImageUrl()=" + getImageUrl() + ", getUpdatedByStudent()=" + getUpdatedByStudent()
				+ ", getPrgmStructApplicable()=" + getPrgmStructApplicable() + ", getErrorMessage()="
				+ getErrorMessage() + ", isErrorRecord()=" + isErrorRecord() + ", getSapid()=" + getSapid()
				+ ", getSem()=" + getSem() + ", getLastName()=" + getLastName() + ", getFirstName()=" + getFirstName()
				+ ", getMiddleName()=" + getMiddleName() + ", getFatherName()=" + getFatherName()
				+ ", getHusbandName()=" + getHusbandName() + ", getMotherName()=" + getMotherName() + ", getGender()="
				+ getGender() + ", getProgram()=" + getProgram() + ", getEnrollmentMonth()=" + getEnrollmentMonth()
				+ ", getEnrollmentYear()=" + getEnrollmentYear() + ", getEmailId()=" + getEmailId() + ", getMobile()="
				+ getMobile() + ", getAltPhone()=" + getAltPhone() + ", getDob()=" + getDob() + ", getAge()=" + getAge()
				+ ", getRegDate()=" + getRegDate() + ", getIsLateral()=" + getIsLateral() + ", getIsReReg()="
				+ getIsReReg() + ", getAddress()=" + getAddress() + ", getHouseNoName()=" + getHouseNoName()
				+ ", getStreet()=" + getStreet() + ", getLandMark()=" + getLandMark() + ", getLocality()="
				+ getLocality() + ", getPin()=" + getPin() + ", getCity()=" + getCity() + ", getState()=" + getState()
				+ ", getCountry()=" + getCountry() + ", getCenterCode()=" + getCenterCode() + ", getCenterName()="
				+ getCenterName() + ", getValidityEndMonth()=" + getValidityEndMonth() + ", getValidityEndYear()="
				+ getValidityEndYear() + ", getCreatedBy()=" + getCreatedBy() + ", getCreatedDate()=" + getCreatedDate()
				+ ", getLastModifiedBy()=" + getLastModifiedBy() + ", getLastModifiedDate()=" + getLastModifiedDate()
				+ ", getProgramStatus()=" + getProgramStatus() + ", getProgramRemarks()=" + getProgramRemarks()
				+ ", getPreviousPrgmStructApplicable()=" + getPreviousPrgmStructApplicable() + ", getSpecialisation()="
				+ getSpecialisation() + ", getMaxExamDate()=" + getMaxExamDate() + ", isPurchasedOtherPackages()="
				+ isPurchasedOtherPackages() + ", getProgramType()=" + getProgramType() + ", getProvisionalAdmission()="
				+ getProvisionalAdmission() + ", getTranDateTime()=" + getTranDateTime() + ", getYear()=" + getYear()
				+ ", getMonth()=" + getMonth() + ", getDate()=" + getDate() + ", toString()=" + super.toString()
				+ ", getClass()=" + getClass() + ", hashCode()=" + hashCode() + "]";
	}
	public boolean isPurchasedOtherPackages() {
		return purchasedOtherPackages;
	}
	public void setPurchasedOtherPackages(boolean purchasedOtherPackages) {
		this.purchasedOtherPackages = purchasedOtherPackages;
	}
	public String getProgramType() {
		return programType;
	}
	public void setProgramType(String programType) {
		this.programType = programType;
	}
	public Integer getProvisionalAdmission() {
		return provisionalAdmission;
	}
	public void setProvisionalAdmission(Integer provisionalAdmission) {
		this.provisionalAdmission = provisionalAdmission;
	}
	public String getAbcId() {
		return abcId;
	}
	public void setAbcId(String abcId) {
		this.abcId = abcId;
	}
	public int getTextToSpeech() {
		return textToSpeech;
	}
	public void setTextToSpeech(int textToSpeech) {
		this.textToSpeech = textToSpeech;
	}
	public int getHighContrast() {
		return highContrast;
	}
	public void setHighContrast(int highContrast) {
		this.highContrast = highContrast;
	}
 
	
}