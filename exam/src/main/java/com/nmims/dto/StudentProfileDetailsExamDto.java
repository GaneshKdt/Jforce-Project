package com.nmims.dto;

import java.io.Serializable;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;

import org.hibernate.validator.constraints.NotBlank;
import org.springframework.data.annotation.Id;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * POJO class containing fields similar to StudentExamBean related to the student profile
 * @author Raynal Dcunha
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class StudentProfileDetailsExamDto implements Serializable {
	@Id
	@NotNull
	private Long sapid;
	@NotNull
	private int sem;
	
	@NotBlank(message="Student First Name cannot be empty!")
	private String firstName;
	private String middleName;
	private String lastName;				//student lastName can be stored as empty, only dot (period) character is not allowed as lastName, card: 14965
	@NotBlank(message="Father First Name cannot be empty!")
	private String fatherName;
	@NotBlank(message="Mother First Name cannot be empty!")
	private String motherName;
	private String spouseName;
	
	@NotBlank(message="Please Select a Gender.")
	private String gender;
	@NotBlank(message="Please Select a Date of Birth.")
	@JsonFormat(pattern = "yyyy-MM-dd")
	private String dob;
	private int age;
	private String imageUrl;
	//OWASP Email Validation regex
	@Pattern(regexp = "^[a-zA-Z0-9_\\+&\\*-]+(?:\\.[a-zA-Z0-9_\\+&\\*-]+)*@(?:[a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,7}$", 
			 message = "Invalid Email Address! Allowed characters: A(a)-Z(z), digits: 0-9, special characters: hyphen, underscore, plus(+), ampersand(&), asterisk(*)")
	private String emailId;
	/* Generic validation for worldwide Mobile Phone numbers
	1. Every mobile number uses only 0-9 and sometimes space/dash/comma/period/round brackets.
	2. Minimum 7 characters and a Maximum of 34 characters
	3. Allow one space/dash/comma/period/round brackets character at a time, no repetition
	4. Can begin with a plus sign or two leading zeros. */
	@Pattern(regexp = "^(\\+|00)?([0-9][-,\\(\\)\\.\\s]?){6,31}[0-9\\)]$", 
			 message = "Invalid Mobile Number! Allowed digits: 0-9, special characters: hyphen, space key, round brackets & period")
	private String mobile;
	private String altPhone;
	
	@NotBlank(message = "Address Line 1 cannot be empty!")
	private String addressLine1;
	@NotBlank(message = "Address Line 2 cannot be empty!")
	private String addressLine2;
	private String landMark;
	@NotBlank(message = "Address Line 3 cannot be empty!")
	private String addressLine3;
	/* Generic validation for worldwide pincode's
	1. Every postal code system uses only A-Z and/or 0-9 and sometimes space/dash.
	2. Minimum 2 characters and a Maximum of 12 characters.
	3. Allow one space or dash character at a time, no repetition.
	4. Should not begin or end with space or dash. */
	@Pattern(regexp = "^[A-Za-z0-9]([A-Za-z0-9][-\\s]?){0,10}[A-Za-z0-9]$", 
			 message = "Invalid Pin Code! Allowed characters: A(a)-Z(z), digits: 0-9, special characters: hyphen & space key")
	private String pin;
	@NotBlank(message = "City field cannot be empty!")
	private String city;
	@NotBlank(message = "State field cannot be empty!")
	private String state;
	@NotBlank(message = "Country field cannot be empty!")
	private String country;
	
	private String centerCode;
	private String centerName;
	private String program;
	private String programStructure;
	
	private String enrollmentMonth;
	private int enrollmentYear;
	private String validityEndMonth;
	private int validityEndYear;
	
	private String programChanged;
	private String oldProgram;
	@NotBlank(message = "Please Select Program Cleared.")
	private String programCleared;
	private String programStatus;
	private String programRemarks;
	
	private String highestQualification;
	private String industry;
	private String designation;
	
	public Long getSapid() {
		return sapid;
	}
	
	public void setSapid(Long sapid) {
		this.sapid = sapid;
	}
	
	public int getSem() {
		return sem;
	}
	
	public void setSem(int sem) {
		this.sem = sem;
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
	
	@JsonProperty("husbandName")
	public String getHusbandName() {
		return spouseName;
	}
	
	@JsonProperty("spouseName")
	public String getSpouseName() {
		return spouseName;
	}
	
	@JsonProperty("husbandName")
	public void setHusbandName(String spouseName) {
		this.spouseName = spouseName;
	}
	
	@JsonProperty("spouseName")
	public void setSpouseName(String spouseName) {
		this.spouseName = spouseName;
	}
	
	public String getGender() {
		return gender;
	}
	
	public void setGender(String gender) {
		this.gender = gender;
	}
	
	public String getDob() {
		return dob;
	}
	public void setDob(String dob) {
		this.dob = dob;
	}
	
	public int getAge() {
		return age;
	}
	
	public void setAge(int age) {
		this.age = age;
	}
	
	public String getImageUrl() {
		return imageUrl;
	}

	public void setImageUrl(String imageUrl) {
		this.imageUrl = imageUrl;
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
	
	@JsonProperty("addressLine1")
	public void setAddressLine1(String addressLine1) {
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
	
	@JsonProperty("addressLine2")
	public void setAddressLine2(String addressLine2) {
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
	
	@JsonProperty("addressLine3")
	public void setAddressLine3(String addressLine3) {
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
	
	public String getCenterCode() {
		return centerCode;
	}
	
	public void setCenterCode(String centerCode) {
		this.centerCode = centerCode;
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
	public void setPrgmStructApplicable(String programStructure) {
		this.programStructure = programStructure;
	}
	
	@JsonProperty("programStructure")
	public void setProgramStructure(String programStructure) {
		this.programStructure = programStructure;
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
	
	public String getProgramChanged() {
		return programChanged;
	}

	public void setProgramChanged(String programChanged) {
		this.programChanged = programChanged;
	}

	public String getOldProgram() {
		return oldProgram;
	}
	
	public void setOldProgram(String oldProgram) {
		this.oldProgram = oldProgram;
	}
	
	public String getProgramCleared() {
		return programCleared;
	}

	public void setProgramCleared(String programCleared) {
		this.programCleared = programCleared;
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

	@Override
	public String toString() {
		return "StudentProfileDetailsExamDto [sapid=" + sapid + ", sem=" + sem + ", firstName=" + firstName
				+ ", middleName=" + middleName + ", lastName=" + lastName + ", fatherName=" + fatherName
				+ ", motherName=" + motherName + ", spouseName=" + spouseName + ", gender=" + gender + ", dob=" + dob
				+ ", age=" + age + ", imageUrl=" + imageUrl + ", emailId=" + emailId + ", mobile=" + mobile
				+ ", altPhone=" + altPhone + ", addressLine1=" + addressLine1 + ", addressLine2=" + addressLine2
				+ ", landMark=" + landMark + ", addressLine3=" + addressLine3 + ", pin=" + pin + ", city=" + city
				+ ", state=" + state + ", country=" + country + ", centerCode=" + centerCode + ", centerName="
				+ centerName + ", program=" + program + ", programStructure=" + programStructure + ", enrollmentMonth="
				+ enrollmentMonth + ", enrollmentYear=" + enrollmentYear + ", validityEndMonth=" + validityEndMonth
				+ ", validityEndYear=" + validityEndYear + ", programChanged=" + programChanged + ", oldProgram="
				+ oldProgram + ", programCleared=" + programCleared + ", programStatus=" + programStatus
				+ ", programRemarks=" + programRemarks + ", highestQualification=" + highestQualification
				+ ", industry=" + industry + ", designation=" + designation + "]";
	}
}
