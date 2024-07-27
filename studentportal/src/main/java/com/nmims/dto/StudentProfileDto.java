package com.nmims.dto;

import java.io.Serializable;

import org.springframework.data.annotation.Id;

import com.fasterxml.jackson.annotation.JsonFilter;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * POJO class which contains fields of StudentBean required for the response of /updateProfileFromSFDC API
 * @author Raynal Dcunha
 */
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonFilter("StudentProfileDtoFilter")
public class StudentProfileDto implements Serializable {
	@Id
	private String sapid;
	
	private String firstName;
	private String lastName;
	private String fatherName;
	private String motherName;
	private String spouseName;
	
	private String dob;
	private String studentImage;
	private String emailId;
	private String mobileNo;
	private String altPhone;
	
	private String addressLine1;
	private String addressLine2;
	private String landMark;
	private String addressLine3;
	private String pin;
	private String city;
	private String state;
	private String country;
	
	private String centerName;
	private String centerId;
	private String program;
	private String programStructure;
	
	private String enrollmentMonth;
	private int enrollmentYear;
	private String validityEndMonth;
	private int validityEndYear;
	
	private String previousProgram;
	private String highestQualification;
	
	private String lastModifiedBy;
	private String lastModifiedDate;
	
	
	public String getSapid() {
		return sapid;
	}
	
	public void setSapid(String sapid) {
		this.sapid = sapid;
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
	
	public String getFatherName() {
		return fatherName;
	}
	
	public void setFatherName(String fatherName) {
		this.fatherName = fatherName;
	}
	
	public String getMotherName() {
		return motherName;
	}
	
	public void setMotherName(String motherName) {
		this.motherName = motherName;
	}
	
	@JsonProperty("spouseName")
	public String getSpouseName() {
		return spouseName;
	}
	
	@JsonProperty("husbandName")
	public String getHusbandName() {
		return spouseName;
	}
	
	@JsonProperty("husbandName")
	public void setHusbandName(String spouseName) {
		this.spouseName = spouseName;
	}
	
	public String getDob() {
		return dob;
	}
	
	public void setDob(String dob) {
		this.dob = dob;
	}
	
	@JsonProperty("studentImage")
	public String getStudentImage() {
		return studentImage;
	}
	
	@JsonProperty("imageUrl")
	public String getImageUrl() {
		return studentImage;
	}
	
	@JsonProperty("imageUrl")
	public void setImageUrl(String studentImage) {
		this.studentImage = studentImage;
	}
	
	public String getEmailId() {
		return emailId;
	}
	
	public void setEmailId(String emailId) {
		this.emailId = emailId;
	}
	
	@JsonProperty("mobileNo")
	public String getMobileNo() {
		return mobileNo;
	}
	
	@JsonProperty("mobile")
	public String getMobile() {
		return mobileNo;
	}
	
	@JsonProperty("mobile")
	public void setMobile(String mobileNo) {
		this.mobileNo = mobileNo;
	}
	
	public String getAltPhone() {
		return altPhone;
	}
	
	public void setAltPhone(String altPhone) {
		this.altPhone = altPhone;
	}
	
	@JsonProperty("addressLine1")
	public String getAddressLine1() {
		return addressLine1;
	}
	
	@JsonProperty("houseNoName")
	public String getHouseNoName() {
		return addressLine1;
	}

	@JsonProperty("houseNoName")
	public void setHouseNoName(String addressLine1) {
		this.addressLine1 = addressLine1;
	}
	
	@JsonProperty("addressLine2")
	public String getAddressLine2() {
		return addressLine2;
	}
	
	@JsonProperty("street")
	public String getStreet() {
		return addressLine2;
	}
	
	@JsonProperty("street")
	public void setStreet(String addressLine2) {
		this.addressLine2 = addressLine2;
	}
	
	public String getLandMark() {
		return landMark;
	}
	
	public void setLandMark(String landMark) {
		this.landMark = landMark;
	}
	
	@JsonProperty("addressLine3")
	public String getAddressLine3() {
		return addressLine3;
	}
	
	@JsonProperty("locality")
	public String getLocality() {
		return addressLine3;
	}
	
	@JsonProperty("locality")
	public void setLocality(String addressLine3) {
		this.addressLine3 = addressLine3;
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
	
	public String getCenterName() {
		return centerName;
	}
	
	public void setCenterName(String centerName) {
		this.centerName = centerName;
	}
	
	@JsonProperty("centerId")
	public String getCenterId() {
		return centerId;
	}
	
	@JsonProperty("centerCode")
	public String getCenterCode() {
		return centerId;
	}
	
	@JsonProperty("centerCode")
	public void setCenterCode(String centerId) {
		this.centerId = centerId;
	}
	
	public String getProgram() {
		return program;
	}
	
	public void setProgram(String program) {
		this.program = program;
	}
	
	@JsonProperty("programStructure")
	public String getProgramStructure() {
		return programStructure;
	}
	
	@JsonProperty("prgmStructApplicable")
	public String getPrgmStructApplicable() {
		return programStructure;
	}
	
	@JsonProperty("prgmStructApplicable")
	public void setPrgmStructApplicable(String prgmStructApplicable) {
		this.programStructure = prgmStructApplicable;
	}
	
	public String getEnrollmentMonth() {
		return enrollmentMonth;
	}

	public void setEnrollmentMonth(String enrollmentMonth) {
		this.enrollmentMonth = enrollmentMonth;
	}

	public int getEnrollmentYear() {
		return enrollmentYear;
	}

	public void setEnrollmentYear(int enrollmentYear) {
		this.enrollmentYear = enrollmentYear;
	}

	public String getValidityEndMonth() {
		return validityEndMonth;
	}
	
	public void setValidityEndMonth(String validityEndMonth) {
		this.validityEndMonth = validityEndMonth;
	}
	
	public int getValidityEndYear() {
		return validityEndYear;
	}
	
	public void setValidityEndYear(int validityEndYear) {
		this.validityEndYear = validityEndYear;
	}
	
	@JsonProperty("previousProgram")
	public String getPreviousProgram() {
		return previousProgram;
	}
	
	@JsonProperty("oldProgram")
	public String getOldProgram() {
		return previousProgram;
	}
	
	@JsonProperty("oldProgram")
	public void setOldProgram(String previousProgram) {
		this.previousProgram = previousProgram;
	}
	
	public String getHighestQualification() {
		return highestQualification;
	}

	public void setHighestQualification(String highestQualification) {
		this.highestQualification = highestQualification;
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
	public String toString() {
		return "StudentProfileDto [sapid=" + sapid + ", firstName=" + firstName + ", lastName=" + lastName
				+ ", fatherName=" + fatherName + ", motherName=" + motherName + ", spouseName=" + spouseName + ", dob="
				+ dob + ", studentImage=" + studentImage + ", emailId=" + emailId + ", mobileNo=" + mobileNo
				+ ", altPhone=" + altPhone + ", addressLine1=" + addressLine1 + ", addressLine2=" + addressLine2
				+ ", landMark=" + landMark + ", addressLine3=" + addressLine3 + ", pin=" + pin + ", city=" + city
				+ ", state=" + state + ", country=" + country + ", centerName=" + centerName + ", centerId=" + centerId
				+ ", program=" + program + ", programStructure=" + programStructure + ", enrollmentMonth="
				+ enrollmentMonth + ", enrollmentYear=" + enrollmentYear + ", validityEndMonth=" + validityEndMonth
				+ ", validityEndYear=" + validityEndYear + ", previousProgram=" + previousProgram
				+ ", highestQualification=" + highestQualification + ", lastModifiedBy=" + lastModifiedBy
				+ ", lastModifiedDate=" + lastModifiedDate + "]";
	}
}
