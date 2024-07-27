package com.nmims.beans;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import org.springframework.web.multipart.MultipartFile;

/**
 * old name - ServiceRequest
 * @author
 *
 */
public class ServiceRequestStudentPortal extends BaseStudentPortalBean implements Serializable{
	
	public static final String CHANGE_IN_ID = "Change in I-Card";
	public static final String DUPLICATE_ID = "Duplicate I-Card";
	public static final String DUPLICATE_STUDY_KIT = "Duplicate Study Kit"; 
	public static final String SINGLE_BOOK = "Single Book";
	public static final String REDISPATCH_STUDY_KIT="Re-Dispatch Of Study Kit";
	public static final String DUPLICATE_FEE_RECEIPT = "Duplicate Fee Receipt";
	public static final String CHANGE_IN_NAME = "Change in Name";
	public static final String CHANGE_IN_CONTACT_ADDRESS = "Change in Contact Address";
	public static final String CHANGE_IN_DOB = "Change in DOB";
	public static final String CHANGE_FATHER_MOTHER_SPOUSE_NAME = "Change Father/Mother/Spouse Name";
	public static final String CHANGE_IN_CONTACT_DETAILS = "Change in Contact Details";
	public static final String ISSUEANCE_OF_MARKSHEET = "Issuance of Marksheet";
	public static final String ISSUEANCE_OF_BONAFIDE = "Issuance of Bonafide";
	public static final String ISSUEANCE_OF_PROVISIONAL_CERTIFICATE = "Issuance of Provisional Certificate";
	public static final String ISSUEANCE_OF_CERTIFICATE = "Issuance of Final Certificate";
	public static final String ISSUEANCE_OF_TRANSCRIPT = "Issuance of Transcript";
	//public static final String ISSUEANCE_OF_DIPLOMA = "Issuance of Diploma";
	public static final String DUPLICATE_MARKSHEET = "Duplicate Marksheet"; //Newly added by Vikas on 01/09/2016//
	public static final String DUPLICATE_DIPLOMA = "Duplicate Diploma";
	public static final String NAME_CHANGE_IN_MARKSHEET = "Name Change in Marksheet";
	public static final String NAME_CHANGE_IN_DIPLOMA = "Name Change in Diploma";
	public static final String BONAFIDE_CERTIFICATE = "Bonafide certificate";
	public static final String ASSIGNMENT_REVALUATION = "Assignment Revaluation";
	public static final String OFFLINE_ASSIGNMENT_REVALUATION = "Internal Assignment Revaluation";
	public static final String TEE_REVALUATION = "Revaluation of Term End Exam Marks";
	public static final String OFFLINE_TEE_REVALUATION = "Revaluation of Written Exam Answer Books";
	public static final String PHOTOCOPY_OF_ANSWERBOOK = "Photocopy of Written Exam Answer Books";
	public static final String SPECIAL_NEED_SR="Special Needs SR";
	public static final String SCRIBE_FOR_TERM_END_EXAM="Scribe for Term End Exam";

	
	
	public static final String TRAN_STATUS_FREE = "Free";
	public static final String TRAN_STATUS_INITIATED = "Initiated";
	public static final String TRAN_STATUS_SUCCESSFUL = "Payment Successful"; 
	public static final String TRAN_STATUS_EXPIRED = "Expired";
	
	public static final String REQUEST_STATUS_PAYMENT_PENDING = "Payment Pending";
	public static final String REQUEST_STATUS_PAYMENT_FAILED = "Payment Failed";
	public static final String REQUEST_STATUS_SUBMITTED = "Submitted";
	public static final String REQUEST_STATUS_CLOSED = "Closed";
	public static final String REQUEST_STATUS_CANCELLED="Cancelled";
	public static final String REQUEST_STATUS_IN_PROGRESS="In Progress";

	
	public static final String DE_REGISTERED = "De-Registered";
	public static final String CHANGE_IN_PHOTOGRAPH = "Change in Photograph";
	public static final String PROGRAM_DE_REGISTRATION = "Program De-Registration";
	public static final String PROGRAM_WITHDRAWAL = "Program Withdrawal"; 
	public static final String EXIT_PROGRAM = "Exit Program"; 
	
	public static final String SUBJECT_REPEAT_MBAWX = "Subject Repeat MBA - WX";
	public static final String SUBJECT_REPEAT_MSC_AI_ML = "Subject Repeat M.Sc. AI and ML Ops";
	public static final String SUBJECT_REPEAT = "Subject Repeat";
	public static final String ISSUEANCE_OF_GRADESHEET = "Issuance of Gradesheet";
	
	private Long id;
	private String serviceRequestType;
	private String serviceRequestName;
	private String sapId;
	private String trackId;
	private String amount;
	private String amountToBeDisplayedForMarksheetSummary;
	
	private String tranDateTime;
	private String service_requestcol;
	private String tranStatus;
	private String requestStatus;
	
	private String requestClosedDate;
	private String transactionID;
	private String requestID;
	private String merchantRefNo;
	private String secureHash;
	private String respAmount;
	private String description;
	private String descriptionToBeShownInMarksheetSummary;
	
	private String responseCode;
	private String respPaymentMethod;
	private String isFlagged;
	private String paymentID;
	private String responseMessage;
	private String error;
	private String respTranDateTime;
	private String category;

	private List<ServiceRequestDocumentBean> documents;
	private List<String> revaluationSubjects = new ArrayList<>();
	private List<ServiceRequestStudentPortal> repeatSubjects;
	private List<ServiceRequestStudentPortal> repeatSubjectsApplied;
	private boolean duplicateMarksheet;
	private String serviceRequestIdList;
	MultipartFile firCopy;
	private String certificationType;
	private int successCount;
	private String productType; 
	private String receiptNo; 
	List<String> failedList ;
	List<String> errorList ;
	private String userId; 
	private String center;
	private String consumerProgramStructureId;
	private String expectedClosedDate;
	public String getCenter() {
		return center;
	}
	public void setCenter(String center) {
		this.center = center;
	}
	public String getReceiptNo() {
		return receiptNo;
	}
	public void setReceiptNo(String receiptNo) {
		this.receiptNo = receiptNo;
	}
	public String getUserId() {
		return userId;
	}
	public void setUserId(String userId) {
		this.userId = userId;
	}
	public List<String> getErrorList() {
		return errorList;
	}
	public void setErrorList(List<String> errorList) {
		this.errorList = errorList;
	}
	public int getSuccessCount() {
		return successCount;
	}
	public void setSuccessCount(int successCount) {
		this.successCount = successCount;
	}
	public List<String> getFailedList() {
		return failedList;
	}
	public void setFailedList(List<String> failedList) {
		this.failedList = failedList; 
	}
	public String getServiceRequestIdList() {
		return serviceRequestIdList;
	}
	public void setServiceRequestIdList(String serviceRequestIdList) {
		this.serviceRequestIdList = serviceRequestIdList;
	}
	public String getCertificationType() {
		return certificationType;
	}
	public void setCertificationType(String certificationType) {
		this.certificationType = certificationType;
	}
	public MultipartFile getFirCopy() {
		return firCopy;
	}
	public void setFirCopy(MultipartFile firCopy) {
		this.firCopy = firCopy;
	}
	public MultipartFile getIndemnityBond() {
		return indemnityBond;
	}
	public void setIndemnityBond(MultipartFile indemnityBond) {
		this.indemnityBond = indemnityBond;
	}

	MultipartFile indemnityBond;
	
	public boolean isDuplicateMarksheet() {
		return duplicateMarksheet;
	}
	public boolean getDuplicateMarksheet() {
		return duplicateMarksheet;
	}
	
	private String firstName;
	public List<ServiceRequestDocumentBean> getDocuments() {
		return documents;
	}
	public void setDocuments(List<ServiceRequestDocumentBean> documents) {
		this.documents = documents;
	}
	public void setDuplicateMarksheet(boolean duplicateMarksheet) {
		this.duplicateMarksheet = duplicateMarksheet;
	}

	private String lastName;

	private String middleName;
	public String getMiddleName() {
		return middleName;
	}
	public void setMiddleName(String middleName) {
		this.middleName = middleName;
	}

	private String fatherName;
	private String MotherName;
	
	
	

	private String motherName;
	private String husbandName;
	
	private String emailId;
	private String mobile;
	private String hasDocuments = "N";
	
	private String transactionType;
    private String status;
    private String respMode;
    private String errorCode;
    private String paymentType;
    
   
	private String informationForPostPayment;
    
    //For Multiple Marksheet module//
    public String marksheetDetailRecord1;
    public String marksheetDetailRecord2;
    public String marksheetDetailRecord3;
    public String marksheetDetailRecord4;
    public String marksheetDetailRecord5;
    public String marksheetDetailRecord6;
    public String marksheetDetailRecord7;
    public String marksheetDetailRecord8;

    public String getMarksheetDetailRecord5() {
		return marksheetDetailRecord5;
	}
	public void setMarksheetDetailRecord5(String marksheetDetailRecord5) {
		this.marksheetDetailRecord5 = marksheetDetailRecord5;
	}

	public String getMarksheetDetailRecord6() {
		return marksheetDetailRecord6;
	}
	public void setMarksheetDetailRecord6(String marksheetDetailRecord6) {
		this.marksheetDetailRecord6 = marksheetDetailRecord6;
	}
	
	//End//
	private String dob;
	private String year;
	private String month;
    private String sem;
    private String postalAddress;
    private String wantAtAddress;
    private String additionalInfo1;
    private String cancellationReason;
    private String courierAmount;
    private String issued;
    private String multipleMarksheet;
	
    private String modeOfDispatch;
	
	private String program;
    
	private String subject;
    private String multipleSem;
    private String refundStatus;
    private String refundAmount;
    //For Transcript//
    private String noOfCopies;
    private String tat;
	private String srAttribute;
    private String srIdList;
    private String descriptionList;

    private String reason;

   
    private String charges;

	private String finalName;
	private String channel;
	private String account_id;
	private String mode;
	private String currency;
	private String currency_code;
	private String return_url;
	private String algo;
	private String V3URL;
	private String studentNumber;
	private String reference_no;
	public String getReference_no() {
		return reference_no;
	}
	public void setReference_no(String reference_no) {
		this.reference_no = reference_no;
	}
	public String getReturn_url() {
		return return_url;
	}
	public void setReturn_url(String return_url) {
		this.return_url = return_url;
	}
	public String getAlgo() {
		return algo;
	}
	public void setAlgo(String algo) {
		this.algo = algo;
	}
	public String getV3URL() {
		return V3URL;
	}
	public void setV3URL(String v3url) {
		V3URL = v3url;
	}
	public String getStudentNumber() {
		return studentNumber;
	}
	public void setStudentNumber(String studentNumber) {
		this.studentNumber = studentNumber;
	}
	public String getChannel() {
		return channel;
	}
	public String getMode() {
		return mode;
	}
	public void setMode(String mode) {
		this.mode = mode;
	}
	public String getCurrency() {
		return currency;
	}
	public void setCurrency(String currency) {
		this.currency = currency;
	}
	public String getCurrency_code() {
		return currency_code;
	}
	public void setCurrency_code(String currency_code) {
		this.currency_code = currency_code;
	}
	public String getAccount_id() {
		return account_id;
	}
	public void setAccount_id(String account_id) {
		this.account_id = account_id;
	}
	public void setChannel(String channel) {
		this.channel = channel;
	}
    private String paymentOption;
    private String orderId;
    private String bankName;
    private String bankTxtId;
    
    private String landMark;
    private String Old_LandMark;
    private String houseNoName;
    private String street;
    private String locality;
    private String city;
    private String state;
    private String country;
    private String pin;
    
    //prgm withdrawal    
    public String getReason() {
		return reason;
	} 
	public void setReason(String reason) {
		this.reason = reason;
	}


	//    deregistration
    private List<StudentStudentPortalBean> studentRegistrationList;
    
    private String isLateral;
    private String programChanged;
    private String programStatus;
    
    
    private Boolean isMobile = false;
    
	private String paymentUrl;
	
	private String device;
	
	private int additionalCopiesCharges;
    
    public int getAdditionalCopiesCharges() {
		return additionalCopiesCharges;
	}
	public void setAdditionalCopiesCharges(int additionalCopiesCharges) {
		this.additionalCopiesCharges = additionalCopiesCharges;
	}
	public String getDevice() {
		return device;
	}
	public void setDevice(String device) {
		this.device = device;
	}
	public List<StudentStudentPortalBean> getStudentRegistrationList() {
		return studentRegistrationList;
	}
	public void setStudentRegistrationList(List<StudentStudentPortalBean> studentRegistrationList) {
		this.studentRegistrationList = studentRegistrationList;
	}
	
//end deregistration
	

	public String getIsLateral() {
		return isLateral;
	}
	public void setIsLateral(String isLateral) {
		this.isLateral = isLateral;
	}
	public String getProgramChanged() {
		return programChanged;
	}
	public void setProgramChanged(String programChanged) {
		this.programChanged = programChanged;
	}
	public String getProgramStatus() {
		return programStatus;
	}
	public void setProgramStatus(String programStatus) {
		this.programStatus = programStatus;
	}


	public String getLandMark() {
		return landMark;
	}
	public void setLandMark(String landMark) {
		this.landMark = landMark;
	}
	public String getOld_LandMark() {
		return Old_LandMark;
	}
	public void setOld_LandMark(String old_LandMark) {
		this.Old_LandMark = old_LandMark;
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
public String getPaymentOption() {
		return paymentOption;
	}
	public void setPaymentOption(String paymentOption) {
		this.paymentOption = paymentOption;
	}
public String getOrderId() {
		return orderId;
	}
	public void setOrderId(String orderId) {
		this.orderId = orderId;
	}
	public String getBankName() {
		return bankName;
	}
	public void setBankName(String bankName) {
		this.bankName = bankName;
	}
	public String getBankTxtId() {
		return bankTxtId;
	}
	public void setBankTxtId(String bankTxtId) {
		this.bankTxtId = bankTxtId;
	}


    public String getDob() {
		return dob;
	}
	public void setDob(String dob) {
		this.dob = dob;
	}	



    private ArrayList<String> yearList;
	private List<ServiceRequestStudentPortal> marksheetDetailAndAmountToBePaidList;
	private String marksheetDetailAndAmountToBePaidListAsString;
	private String totalAmountToBePayed;


    private boolean isCertificate;
	private String Charges;
	private String errorMessage;
	private String duplicateDiploma;
	private List<String> subjectList;
	private int size;
	private String successMessage;
	private String requestExpectedClosedDate;
	
	private String abcId;
	
	public String getTotalAmountToBePayed() {
		return totalAmountToBePayed;
	}

	public void setTotalAmountToBePayed(String totalAmountToBePayed) {
		this.totalAmountToBePayed = totalAmountToBePayed;
	}

	public List<ServiceRequestStudentPortal> getMarksheetDetailAndAmountToBePaidList() {
		return marksheetDetailAndAmountToBePaidList;
	}

	public void setMarksheetDetailAndAmountToBePaidList(
			List<ServiceRequestStudentPortal> marksheetDetailAndAmountToBePaidList) {
		this.marksheetDetailAndAmountToBePaidList = marksheetDetailAndAmountToBePaidList;
	}

	public String getCharges() {
		return charges;
	}
	public void setCharges(String charges) {
		this.charges = charges;
	}
public ArrayList<String> getYearList() {
		return yearList;
	}
	public void setYearList(ArrayList<String> yearList) {
		this.yearList = yearList;
	}

	public int getSize() {
		return size;
	}

	public void setSize(int size) {
		this.size = size;
	}

	public List<String> getSubjectList() {
		return subjectList;
	}

	public void setSubjectList(List<String> subjectList) {
		this.subjectList = subjectList;
	}
	public String getDuplicateDiploma() {
		return duplicateDiploma;
	}
	public void setDuplicateDiploma(String duplicateDiploma) {
		this.duplicateDiploma = duplicateDiploma;
	}
	public String getErrorMessage() {
		return errorMessage;
	}
	public void setErrorMessage(String errorMessage) {
		this.errorMessage = errorMessage;
	}
	public String getFinalName() {
		return finalName;
	}
	public void setFinalName(String finalName) {
		this.finalName = finalName;
	}
 
public boolean getIsCertificate() {
		return isCertificate;
	}
	public void setIsCertificate(boolean isCertificate) {
		this.isCertificate = isCertificate;
	}


	public String getSrIdList() {
		return srIdList;
	}
	public void setSrIdList(String srIdList) {
		this.srIdList = srIdList;
	}
	public String getDescriptionList() {
		return descriptionList;
	}
	public void setDescriptionList(String descriptionList) {
		this.descriptionList = descriptionList;
	}
	public String getPaymentType() {
		return paymentType;
	}
	public void setPaymentType(String paymentType) {
		this.paymentType = paymentType;
	}
    public String getSrAttribute() {
		return srAttribute;
	}
	public void setSrAttribute(String srAttribute) {
		this.srAttribute = srAttribute;
	}
	public String getModeOfDispatch() {
		return modeOfDispatch;
	}
	public void setModeOfDispatch(String modeOfDispatch) {
		this.modeOfDispatch = modeOfDispatch;
	}
    public String getAmountToBeDisplayedForMarksheetSummary() {
		return amountToBeDisplayedForMarksheetSummary;
	}
	public void setAmountToBeDisplayedForMarksheetSummary(
			String amountToBeDisplayedForMarksheetSummary) {
		this.amountToBeDisplayedForMarksheetSummary = amountToBeDisplayedForMarksheetSummary;
	}
    public String getDescriptionToBeShownInMarksheetSummary() {
		return descriptionToBeShownInMarksheetSummary;
	}
	public void setDescriptionToBeShownInMarksheetSummary(
			String descriptionToBeShownInMarksheetSummary) {
		this.descriptionToBeShownInMarksheetSummary = descriptionToBeShownInMarksheetSummary;
	}
    public String getMultipleMarksheet() {
		return multipleMarksheet;
	}
	public void setMultipleMarksheet(String multipleMarksheet) {
		this.multipleMarksheet = multipleMarksheet;
	}
    public String getIssued() {
		return issued;
	}
	public void setIssued(String issued) {
		this.issued = issued;
	}
    public String getCourierAmount() {
		return courierAmount;
	}
	public void setCourierAmount(String courierAmount) {
		this.courierAmount = courierAmount;
	}
    public String getMarksheetDetailRecord1() {
		return marksheetDetailRecord1;
	}
	public void setMarksheetDetailRecord1(String marksheetDetailRecord1) {
		this.marksheetDetailRecord1 = marksheetDetailRecord1;
	}
	public String getMarksheetDetailRecord2() {
		return marksheetDetailRecord2;
	}
	public void setMarksheetDetailRecord2(String marksheetDetailRecord2) {
		this.marksheetDetailRecord2 = marksheetDetailRecord2;
	}
	public String getMarksheetDetailRecord3() {
		return marksheetDetailRecord3;
	}
	public void setMarksheetDetailRecord3(String marksheetDetailRecord3) {
		this.marksheetDetailRecord3 = marksheetDetailRecord3;
	}
	public String getMarksheetDetailRecord4() {
		return marksheetDetailRecord4;
	}
	public void setMarksheetDetailRecord4(String marksheetDetailRecord4) {
		this.marksheetDetailRecord4 = marksheetDetailRecord4;
	}
    public String getServiceRequestName() {
		return serviceRequestName;
	}
	public void setServiceRequestName(String serviceRequestName) {
		this.serviceRequestName = serviceRequestName;
	}
	
	public String getTat() {
		return tat;
	}
	public void setTat(String tat) {
		this.tat = tat;
	}
	public String getNoOfCopies() {
		return noOfCopies;
	}
	public void setNoOfCopies(String noOfCopies) {
		this.noOfCopies = noOfCopies;
	}
	public String getRefundStatus() {
		return refundStatus;
	}
	public void setRefundStatus(String refundStatus) {
		this.refundStatus = refundStatus;
	}
	public String getRefundAmount() {
		return refundAmount;
	}
	public void setRefundAmount(String refundAmount) {
		this.refundAmount = refundAmount;
	}
	public String getMultipleSem() {
		return multipleSem;
	}
	public void setMultipleSem(String multipleSem) {
		this.multipleSem = multipleSem;
	}
	public String getSubject() {
		return subject;
	}
	public void setSubject(String subject) {
		this.subject = subject;
	}
	public String getProgram() {
		return program;
	}
	public void setProgram(String program) {
		this.program = program;
	}
	public String getAdditionalInfo1() {
		return additionalInfo1;
	}
	public void setAdditionalInfo1(String additionalInfo1) {
		this.additionalInfo1 = additionalInfo1;
	}
	public String getCancellationReason() {
		return cancellationReason;
	}
	public void setCancellationReason(String cancellationReason) {
		this.cancellationReason = cancellationReason;
	}
	public String getWantAtAddress() {
		return wantAtAddress;
	}
	public void setWantAtAddress(String wantAtAddress) {
		this.wantAtAddress = wantAtAddress;
	}
	public String getPostalAddress() {
		return postalAddress;
	}
	public void setPostalAddress(String postalAddress) {
		this.postalAddress = postalAddress;
	}
	public String getSem() {
		return sem;
	}
	public void setSem(String sem) {
		this.sem = sem;
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
	public String getInformationForPostPayment() {
		return informationForPostPayment;
	}
	public void setInformationForPostPayment(String informationForPostPayment) {
		this.informationForPostPayment = informationForPostPayment;
	}
	public String getErrorCode() {
		return errorCode;
	}
	public void setErrorCode(String errorCode) {
		this.errorCode = errorCode;
	}
	public String getTransactionType() {
		return transactionType;
	}
	public void setTransactionType(String transactionType) {
		this.transactionType = transactionType;
	}
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}
	public String getRespMode() {
		return respMode;
	}
	public void setRespMode(String respMode) {
		this.respMode = respMode;
	}
	public String getHasDocuments() {
		return hasDocuments;
	}
	public void setHasDocuments(String hasDocuments) {
		this.hasDocuments = hasDocuments;
	}
	public String getFirstName() {
		return firstName;
	}
	
	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}
	public void FinalName(String firstName) {
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

	public String getHusbandName() {
		return husbandName;
	}
	public void setHusbandName(String husbandName) {
		this.husbandName = husbandName;
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
	public String getCategory() {
		return category;
	}
	public void setCategory(String category) {
		this.category = category;
	}
	public List<String> getRevaluationSubjects() {
		return revaluationSubjects;
	}
	public void setRevaluationSubjects(List<String> revaluationSubjects) {
		this.revaluationSubjects = revaluationSubjects;
	}
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public String getServiceRequestType() {
		return serviceRequestType;
	}
	public void setServiceRequestType(String serviceRequestType) {
		this.serviceRequestType = serviceRequestType;
	}
	public String getSapId() {
		return sapId;
	}
	public void setSapId(String sapId) {
		this.sapId = sapId;
	}
	public String getTrackId() {
		return trackId;
	}
	public void setTrackId(String trackId) {
		this.trackId = trackId;
	}
	public String getAmount() {
		return amount;
	}
	public void setAmount(String amount) {
		this.amount = amount;
	}
	public String getTranDateTime() {
		return tranDateTime;
	}
	public void setTranDateTime(String tranDateTime) {
		this.tranDateTime = tranDateTime;
	}
	public String getService_requestcol() {
		return service_requestcol;
	}
	public void setService_requestcol(String service_requestcol) {
		this.service_requestcol = service_requestcol;
	}
	public String getTranStatus() {
		return tranStatus;
	}
	public void setTranStatus(String tranStatus) {
		this.tranStatus = tranStatus;
	}
	public String getRequestStatus() {
		return requestStatus;
	}
	public void setRequestStatus(String requestStatus) {
		this.requestStatus = requestStatus;
	}
	public String getTransactionID() {
		return transactionID;
	}
	public void setTransactionID(String transactionID) {
		this.transactionID = transactionID;
	}
	public String getRequestID() {
		return requestID;
	}
	public void setRequestID(String requestID) {
		this.requestID = requestID;
	}
	public String getMerchantRefNo() {
		return merchantRefNo;
	}
	public void setMerchantRefNo(String merchantRefNo) {
		this.merchantRefNo = merchantRefNo;
	}
	public String getSecureHash() {
		return secureHash;
	}
	public void setSecureHash(String secureHash) {
		this.secureHash = secureHash;
	}
	public String getRespAmount() {
		return respAmount;
	}
	public void setRespAmount(String respAmount) {
		this.respAmount = respAmount;
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	public String getResponseCode() {
		return responseCode;
	}
	public void setResponseCode(String responseCode) {
		this.responseCode = responseCode;
	}
	public String getRespPaymentMethod() {
		return respPaymentMethod;
	}
	public void setRespPaymentMethod(String respPaymentMethod) {
		this.respPaymentMethod = respPaymentMethod;
	}
	public String getIsFlagged() {
		return isFlagged;
	}
	public void setIsFlagged(String isFlagged) {
		this.isFlagged = isFlagged;
	}
	public String getPaymentID() {
		return paymentID;
	}
	public void setPaymentID(String paymentID) {
		this.paymentID = paymentID;
	}
	public String getResponseMessage() {
		return responseMessage;
	}
	public void setResponseMessage(String responseMessage) {
		this.responseMessage = responseMessage;
	}
	public String getError() {
		return error;
	}
	public void setError(String error) {
		this.error = error;
	}
	public String getRespTranDateTime() {
		return respTranDateTime;
	}
	public void setRespTranDateTime(String respTranDateTime) {
		this.respTranDateTime = respTranDateTime;
	}
	public String getRequestClosedDate() {
		return requestClosedDate;
	}
	public void setRequestClosedDate(String requestClosedDate) {
		this.requestClosedDate = requestClosedDate;
	}
  
	
	public String getPin() {
		return pin;
	}
	public void setPin(String pin) {
		this.pin = pin;
	}
	public Boolean getIsMobile() {
		return isMobile;
	}
	public void setIsMobile(Boolean isMobile) {
		this.isMobile = isMobile;
	}
	public String getPaymentUrl() {
		return paymentUrl;
	}
	public void setPaymentUrl(String paymentUrl) {
		this.paymentUrl = paymentUrl;
	}
	public String getMarksheetDetailAndAmountToBePaidListAsString() {
		return marksheetDetailAndAmountToBePaidListAsString;
	}
	public void setMarksheetDetailAndAmountToBePaidListAsString(String marksheetDetailAndAmountToBePaidListAsString) {
		this.marksheetDetailAndAmountToBePaidListAsString = marksheetDetailAndAmountToBePaidListAsString;
	}
	public List<ServiceRequestStudentPortal> getRepeatSubjects() {
		return repeatSubjects;
	}
	public void setRepeatSubjects(List<ServiceRequestStudentPortal> repeatSubjects) {
		this.repeatSubjects = repeatSubjects;
	}
	public List<ServiceRequestStudentPortal> getRepeatSubjectsApplied() {
		return repeatSubjectsApplied;
	}
	public void setRepeatSubjectsApplied(List<ServiceRequestStudentPortal> repeatSubjectsApplied) {
		this.repeatSubjectsApplied = repeatSubjectsApplied;
	}
	public String getProductType() {
		return productType;
	}
	public void setProductType(String productType) {
		this.productType = productType;
	}
	public String getSuccessMessage() {
		return successMessage;
	}
	public void setSuccessMessage(String successMessage) {
		this.successMessage = successMessage;
	}
	
	public String getRequestExpectedClosedDate() {
		return requestExpectedClosedDate;
	}
	public void setRequestExpectedClosedDate(String requestExpectedClosedDate) {
		this.requestExpectedClosedDate = requestExpectedClosedDate;
	}
	@Override
	public String toString() {
		return "ServiceRequestStudentPortal [id=" + id + ", serviceRequestType=" + serviceRequestType + ", serviceRequestName="
				+ serviceRequestName + ", sapId=" + sapId + ", trackId=" + trackId + ", amount=" + amount
				+ ", amountToBeDisplayedForMarksheetSummary=" + amountToBeDisplayedForMarksheetSummary
				+ ", tranDateTime=" + tranDateTime + ", service_requestcol=" + service_requestcol + ", tranStatus="
				+ tranStatus + ", requestStatus=" + requestStatus + ", requestClosedDate=" + requestClosedDate
				+ ", transactionID=" + transactionID + ", requestID=" + requestID + ", merchantRefNo=" + merchantRefNo
				+ ", secureHash=" + secureHash + ", respAmount=" + respAmount + ", description=" + description
				+ ", descriptionToBeShownInMarksheetSummary=" + descriptionToBeShownInMarksheetSummary
				+ ", responseCode=" + responseCode + ", respPaymentMethod=" + respPaymentMethod + ", isFlagged="
				+ isFlagged + ", paymentID=" + paymentID + ", responseMessage=" + responseMessage + ", error=" + error
				+ ", respTranDateTime=" + respTranDateTime + ", category=" + category + ", documents=" + documents
				+ ", revaluationSubjects=" + revaluationSubjects + ", repeatSubjects=" + repeatSubjects
				+ ", repeatSubjectsApplied=" + repeatSubjectsApplied + ", duplicateMarksheet=" + duplicateMarksheet
				+ ", serviceRequestIdList=" + serviceRequestIdList + ", firCopy=" + firCopy + ", certificationType="
				+ certificationType + ", successCount=" + successCount + ", productType=" + productType
				+ ", failedList=" + failedList + ", indemnityBond=" + indemnityBond + ", firstName=" + firstName
				+ ", lastName=" + lastName + ", middleName=" + middleName + ", fatherName=" + fatherName
				+ ", MotherName=" + MotherName + ", motherName=" + motherName + ", husbandName=" + husbandName
				+ ", emailId=" + emailId + ", mobile=" + mobile + ", hasDocuments=" + hasDocuments
				+ ", transactionType=" + transactionType + ", status=" + status + ", respMode=" + respMode
				+ ", errorCode=" + errorCode + ", paymentType=" + paymentType + ", informationForPostPayment="
				+ informationForPostPayment + ", marksheetDetailRecord1=" + marksheetDetailRecord1
				+ ", marksheetDetailRecord2=" + marksheetDetailRecord2 + ", marksheetDetailRecord3="
				+ marksheetDetailRecord3 + ", marksheetDetailRecord4=" + marksheetDetailRecord4
				+ ", marksheetDetailRecord5=" + marksheetDetailRecord5 + ", dob=" + dob + ", year=" + year + ", month="
				+ month + ", sem=" + sem + ", postalAddress=" + postalAddress + ", wantAtAddress=" + wantAtAddress
				+ ", additionalInfo1=" + additionalInfo1 + ", courierAmount=" + courierAmount + ", issued=" + issued
				+ ", multipleMarksheet=" + multipleMarksheet + ", modeOfDispatch=" + modeOfDispatch + ", program="
				+ program + ", subject=" + subject + ", multipleSem=" + multipleSem + ", refundStatus=" + refundStatus
				+ ", refundAmount=" + refundAmount + ", noOfCopies=" + noOfCopies + ", tat=" + tat + ", srAttribute="
				+ srAttribute + ", srIdList=" + srIdList + ", descriptionList=" + descriptionList + ", reason=" + reason
				+ ", charges=" + charges + ", finalName=" + finalName + ", channel=" + channel + ", account_id="
				+ account_id + ", mode=" + mode + ", currency=" + currency + ", currency_code=" + currency_code
				+ ", return_url=" + return_url + ", algo=" + algo + ", V3URL=" + V3URL + ", studentNumber="
				+ studentNumber + ", reference_no=" + reference_no + ", paymentOption=" + paymentOption + ", orderId="
				+ orderId + ", bankName=" + bankName + ", bankTxtId=" + bankTxtId + ", landMark=" + landMark +" ,Old_LandMark=" + Old_LandMark
				+ ", houseNoName=" + houseNoName + ", street=" + street + ", locality=" + locality + ", city=" + city
				+ ", state=" + state + ", country=" + country + ", pin=" + pin + ", studentRegistrationList="
				+ studentRegistrationList + ", isLateral=" + isLateral + ", programChanged=" + programChanged
				+ ", programStatus=" + programStatus + ", isMobile=" + isMobile + ", paymentUrl=" + paymentUrl
				+ ", device=" + device + ", additionalCopiesCharges=" + additionalCopiesCharges + ", yearList="
				+ yearList + ", marksheetDetailAndAmountToBePaidList=" + marksheetDetailAndAmountToBePaidList
				+ ", marksheetDetailAndAmountToBePaidListAsString=" + marksheetDetailAndAmountToBePaidListAsString
				+ ", totalAmountToBePayed=" + totalAmountToBePayed + ", isCertificate=" + isCertificate + ", Charges="
				+ Charges + ", errorMessage=" + errorMessage + ", duplicateDiploma=" + duplicateDiploma
				+ ", subjectList=" + subjectList + ", size=" + size +", requestExpectedClosedDate="+requestExpectedClosedDate+ "]";
	}
	
	private String purpose;

	public String getPurpose() {
		return purpose;
	}
	public void setPurpose(String purpose) {
		this.purpose = purpose;
	}
	
	private MultipartFile affidavit;
	
	public MultipartFile getAffidavit() {
		return affidavit;
	}
	public void setAffidavit(MultipartFile affidavit) {
		this.affidavit = affidavit;
	}
	public String getConsumerProgramStructureId() {
		return consumerProgramStructureId;
	}
	public void setConsumerProgramStructureId(String consumerProgramStructureId) {
		this.consumerProgramStructureId = consumerProgramStructureId;
	}
	public String getExpectedClosedDate() {
		return expectedClosedDate;
	}
	public void setExpectedClosedDate(String expectedClosedDate) {
		this.expectedClosedDate = expectedClosedDate;
	}
	public String getMarksheetDetailRecord7() {
		return marksheetDetailRecord7;
	}
	public void setMarksheetDetailRecord7(String marksheetDetailRecord7) {
		this.marksheetDetailRecord7 = marksheetDetailRecord7;
	}
	public String getMarksheetDetailRecord8() {
		return marksheetDetailRecord8;
	}
	public void setMarksheetDetailRecord8(String marksheetDetailRecord8) {
		this.marksheetDetailRecord8 = marksheetDetailRecord8;
	}
	public String getAbcId() {
		return abcId;
	}
	public void setAbcId(String abcId) {
		this.abcId = abcId;
	}
	
	
	
	
}
