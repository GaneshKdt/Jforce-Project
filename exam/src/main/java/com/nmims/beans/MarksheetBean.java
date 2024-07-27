package com.nmims.beans;

import java.util.ArrayList;

public class MarksheetBean implements Comparable<MarksheetBean>{
	private String consumerType;
	private String sapid;
	private String firstName;
	private String lastName;
	private String middleName;
	private String fatherName;
	private String motherName;
	private String husbandName;
	private String program;
	private String sem;
	private String enrollmentYear;
	private String enrollmentMonth;
	private String regDate;
	private String centerCode;
	private String centerName;
	private String validityEndMonth;
	private String validityEndYear;
	private String writtenMonth;
	private String writtenYear;
	private String assignmentMonth;
	private String assignmentYear;
	private String examMonth;
	private String examYear;
	private String attemptsRemaining;
	private String prgmStructApplicable;
	private String additionalInfo1;
	private String gender;
	private ArrayList<SubjectResultBean> subjects = new ArrayList<>();
	private String month;
	private String year;
	private String resultDeclarationDate;
	private String serviceRequestId;
	private String examMode;
	private String imageUrl;
	private boolean isPassForExceutive;
	private String specialisation;
//	following attributes added for download marksheet api
	private boolean error;
	private boolean success;
	private String message;
	private String fileName;
	private String consumerProgramStructureId;
	
	/*Properties added by Siddheshwar_K for getting Waived-Off subjects to add on mark sheet.*/
	private String previousStudentId;
	private String isLateral;
	private String logoRequired = "N";
	
	private String oldProgram;
	
	public String getOldProgram() {
		return oldProgram;
	}
	public void setOldProgram(String oldProgram) {
		this.oldProgram = oldProgram;
	}
	public String getLogoRequired() {
		return logoRequired;
	}
	public void setLogoRequired(String logoRequired) {
		this.logoRequired = logoRequired;
	}
	public String getConsumerProgramStructureId() {
		return consumerProgramStructureId;
	}
	public void setConsumerProgramStructureId(String consumerProgramStructureId) {
		this.consumerProgramStructureId = consumerProgramStructureId;
	}
	public String getSpecialisation() {
		return specialisation;
	}
	public void setSpecialisation(String specialisation) {
		this.specialisation = specialisation;
	}
	public String getConsumerType() {
		return consumerType;
	}
	public void setConsumerType(String consumerType) {
		this.consumerType = consumerType;
	}
	public boolean isError() {
		return error;
	}
	public void setError(boolean error) {
		this.error = error;
	}
	public boolean isSuccess() {
		return success;
	}
	public void setSuccess(boolean success) {
		this.success = success;
	}
	
	public String getMessage() {
		return message;
	}
	
	public void setMessage(String message) {
		this.message = message;
	}
	public String getFileName() {
		return fileName;
	}
	
	public void setFileName(String fileName) {
		this.fileName = fileName;
	}
//	
	/**
		 * @return the isPassForExceutive
		 */
		public boolean isPassForExceutive() {
			return isPassForExceutive;
		}
		/**
		 * @param isPassForExceutive the isPassForExceutive to set
		 */
		public void setPassForExceutive(boolean isPassForExceutive) {
			this.isPassForExceutive = isPassForExceutive;
		}
	/**
	 * @return the imageUrl
	 */
		public String getImageUrl() {
			return imageUrl;
		}
	/**
	 * @param imageUrl the imageUrl to set
	 */
		public void setImageUrl(String imageUrl) {
		this.imageUrl = imageUrl;
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
	
	public String getServiceRequestId() {
		return serviceRequestId;
	}

	public void setServiceRequestId(String serviceRequestId) {
		this.serviceRequestId = serviceRequestId;
	}

	public String getGender() {
		return gender;
	}

	public void setGender(String gender) {
		this.gender = gender;
	}
	public String getAdditionalInfo1() {
		return additionalInfo1;
	}

	public void setAdditionalInfo1(String additionalInfo1) {
		this.additionalInfo1 = additionalInfo1;
	}
	public String getLc() {
		return lc;
	}

	public void setLc(String lc) {
		this.lc = lc;
	}

	private String lc;
	
	
	
	public String getResultDeclarationDate() {
		return resultDeclarationDate;
	}

	public void setResultDeclarationDate(String resultDeclarationDate) {
		this.resultDeclarationDate = resultDeclarationDate;
	}

	public String getAttemptsRemaining() {
		return attemptsRemaining;
	}

	public void setAttemptsRemaining(String attemptsRemaining) {
		this.attemptsRemaining = attemptsRemaining;
	}

	public String getMonth() {
		return month;
	}

	public void setMonth(String month) {
		this.month = month;
	}

	public String getYear() {
		return year;
	}

	public void setYear(String year) {
		this.year = year;
	}

	public String getPrgmStructApplicable() {
		return prgmStructApplicable;
	}

	public void setPrgmStructApplicable(String prgmStructApplicable) {
		this.prgmStructApplicable = prgmStructApplicable;
	}

	/*public String getAttemptsRemaining() {
		return attemptsRemaining;
	}

	public void setAttemptsRemaining(String attemptsRemaining) {
		this.attemptsRemaining = attemptsRemaining;
	}*/

	public String getExamMonth() {
		return examMonth;
	}

	public void setExamMonth(String examMonth) {
		this.examMonth = examMonth;
	}

	public String getExamYear() {
		return examYear;
	}

	public void setExamYear(String examYear) {
		this.examYear = examYear;
	}

	public String getAssignmentMonth() {
		return assignmentMonth;
	}

	public void setAssignmentMonth(String assignmentMonth) {
		this.assignmentMonth = assignmentMonth;
	}

	public String getAssignmentYear() {
		return assignmentYear;
	}

	public void setAssignmentYear(String assignmentYear) {
		this.assignmentYear = assignmentYear;
	}

	public String getWrittenMonth() {
		return writtenMonth;
	}

	public void setWrittenMonth(String writtenMonth) {
		this.writtenMonth = writtenMonth;
	}

	public String getWrittenYear() {
		return writtenYear;
	}

	public void setWrittenYear(String writtenYear) {
		this.writtenYear = writtenYear;
	}

	public String getProgram() {
		return program;
	}

	public void setProgram(String program) {
		this.program = program;
	}

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

	public String getMiddleName() {
		if(middleName == null || "".equals(middleName.trim())){
			if(fatherName == null || "".equals(fatherName.trim())){
				return "";
			}else{
				return fatherName;
			}
		}else{
			return middleName;
		}
		
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

	public String getMotherName() {
		if(motherName == null || "".equals(motherName.trim())){
			return "";
		}
		return motherName;
	}

	public void setMotherName(String motherName) {
		this.motherName = motherName;
	}

	public String getHusbandName() {
		return husbandName;
	}

	public void setHusbandName(String husbandName) {
		this.husbandName = husbandName;
	}

	public String getSem() {
		return sem;
	}

	public void setSem(String sem) {
		this.sem = sem;
	}

	public String getEnrollmentYear() {
		return enrollmentYear;
	}

	public void setEnrollmentYear(String enrollmentYear) {
		this.enrollmentYear = enrollmentYear;
	}

	public String getEnrollmentMonth() {
		return enrollmentMonth;
	}

	public void setEnrollmentMonth(String enrollmentMonth) {
		this.enrollmentMonth = enrollmentMonth;
	}

	public String getRegDate() {
		return regDate;
	}

	public void setRegDate(String regDate) {
		this.regDate = regDate;
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

	public ArrayList<SubjectResultBean> getSubjects() {
		return subjects;
	}

	public void setSubjects(ArrayList<SubjectResultBean> subjects) {
		this.subjects = subjects;
	}

	@Override
	public int compareTo(MarksheetBean bean) {
		return lc.compareTo(bean.lc);
	}

	public String getPreviousStudentId() {
		return previousStudentId;
	}
	public void setPreviousStudentId(String previousStudentId) {
		this.previousStudentId = previousStudentId;
	}
	public String getIsLateral() {
		return isLateral;
	}
	public void setIsLateral(String isLateral) {
		this.isLateral = isLateral;
	}
	@Override
	public String toString() {
		return "MarksheetBean [consumerType=" + consumerType + ", sapid=" + sapid + ", firstName=" + firstName
				+ ", lastName=" + lastName + ", middleName=" + middleName + ", fatherName=" + fatherName
				+ ", motherName=" + motherName + ", husbandName=" + husbandName + ", program=" + program + ", sem="
				+ sem + ", enrollmentYear=" + enrollmentYear + ", enrollmentMonth=" + enrollmentMonth + ", regDate="
				+ regDate + ", centerCode=" + centerCode + ", centerName=" + centerName + ", validityEndMonth="
				+ validityEndMonth + ", validityEndYear=" + validityEndYear + ", writtenMonth=" + writtenMonth
				+ ", writtenYear=" + writtenYear + ", assignmentMonth=" + assignmentMonth + ", assignmentYear="
				+ assignmentYear + ", examMonth=" + examMonth + ", examYear=" + examYear + ", attemptsRemaining="
				+ attemptsRemaining + ", prgmStructApplicable=" + prgmStructApplicable + ", additionalInfo1="
				+ additionalInfo1 + ", gender=" + gender + ", subjects=" + subjects + ", month=" + month + ", year="
				+ year + ", resultDeclarationDate=" + resultDeclarationDate + ", serviceRequestId=" + serviceRequestId
				+ ", examMode=" + examMode + ", imageUrl=" + imageUrl + ", isPassForExceutive=" + isPassForExceutive
				+ ", specialisation=" + specialisation + ", error=" + error + ", success=" + success + ", message="
				+ message + ", fileName=" + fileName + ", consumerProgramStructureId=" + consumerProgramStructureId
				+ ", previousStudentId=" + previousStudentId + ", isLateral=" + isLateral + ", logoRequired="
				+ logoRequired + ", oldProgram=" + oldProgram + ", lc=" + lc + "]";
	}
}
