package com.nmims.beans;

import java.io.Serializable;

import org.springframework.web.multipart.commons.CommonsMultipartFile;

public class FileBean extends BaseExamBean  implements Serializable   {

	private CommonsMultipartFile fileData;
	
	private String prgmStructApplicable;
	private String subject;
	private String filePassword;
	private String ic; //Newly added for corporate batch//
	private String centerId; 
	
	//added because of excel upload for non-corporate centers
	private String examMode;
	private String centerName;
	private String capacity;
	private String locality;
	private String address;
	private String city;
	private String state;
	private String googleMapURL;

	private Integer fileId;
	private String fileName;
	private Long id; 
	
	//End
	
	private String enrollmentMonth;
	private Integer enrollmentYear;
	
	private String errorMessage;
	private String filePathToReturn;
	
	
	private Integer sectionId; // added by Abhay for section in IA
	
	
	/**
	 * @return the filePathToReturn
	 */
	public String getFilePathToReturn() {
		return filePathToReturn;
	}
	/**
	 * @param filePathToReturn the filePathToReturn to set
	 */
	public void setFilePathToReturn(String filePathToReturn) {
		this.filePathToReturn = filePathToReturn;
	}
	/**
	 * @return the errorMessage
	 */
	public String getErrorMessage() {
		return errorMessage;
	}
	/**
	 * @param errorMessage the errorMessage to set
	 */
	public void setErrorMessage(String errorMessage) {
		this.errorMessage = errorMessage;
	}
	public String getEnrollmentMonth() {
		return enrollmentMonth;
	}
	public void setEnrollmentMonth(String enrollmentMonth) {
		this.enrollmentMonth = enrollmentMonth;
	}
	public Integer getEnrollmentYear() {
		return enrollmentYear;
	}
	public void setEnrollmentYear(Integer enrollmentYear) {
		this.enrollmentYear = enrollmentYear;
	}
	public String getExamMode() {
		return examMode;
	}
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public Integer getFileId() {
		return fileId;
	}
	public void setFileId(Integer fileId) {
		this.fileId = fileId;
	}
	public String getFileName() {
		return fileName;
	}
	public void setFileName(String fileName) {
		this.fileName = fileName;
	}
	public void setExamMode(String examMode) {
		this.examMode = examMode;
	}
	public String getCenterName() {
		return centerName;
	}
	public void setCenterName(String centerName) {
		this.centerName = centerName;
	}
	public String getCapacity() {
		return capacity;
	}
	public void setCapacity(String capacity) {
		this.capacity = capacity;
	}
	public String getLocality() {
		return locality;
	}
	public void setLocality(String locality) {
		this.locality = locality;
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
	public String getGoogleMapURL() {
		return googleMapURL;
	}
	public void setGoogleMapURL(String googleMapURL) {
		this.googleMapURL = googleMapURL;
	}
	public String getIc() {
		return ic;
	}
	public void setIc(String ic) {
		this.ic = ic;
	}
	
	
	public String getFilePassword() {
		return filePassword;
	}

	public void setFilePassword(String filePassword) {
		this.filePassword = filePassword;
	}

	public String getSubject() {
		return subject;
	}

	public void setSubject(String subject) {
		this.subject = subject;
	}

	public String getPrgmStructApplicable() {
		return prgmStructApplicable;
	}

	public void setPrgmStructApplicable(String prgmStructApplicable) {
		this.prgmStructApplicable = prgmStructApplicable;
	}

	

	public CommonsMultipartFile getFileData()
	{
		return fileData;
	}

	public void setFileData(CommonsMultipartFile fileData)
	{
		this.fileData = fileData;
	}
	public String getCenterId() {
		return centerId;
	}
	public void setCenterId(String centerId) {
		this.centerId = centerId;
	}
	
	public Integer getSectionId() {
		return sectionId;
	}
	public void setSectionId(Integer sectionId) {
		this.sectionId = sectionId;
	}
	
	@Override
	public String toString() {
		return "FileBean [fileData=" + fileData + ", prgmStructApplicable=" + prgmStructApplicable + ", subject="
				+ subject + ", filePassword=" + filePassword + ", ic=" + ic + ", centerId=" + centerId + ", examMode="
				+ examMode + ", centerName=" + centerName + ", capacity=" + capacity + ", locality=" + locality
				+ ", address=" + address + ", city=" + city + ", state=" + state + ", googleMapURL=" + googleMapURL
				+ "]";
	}

}
