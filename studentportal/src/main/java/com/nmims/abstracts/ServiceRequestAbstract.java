package com.nmims.abstracts;

import java.util.Map;
import java.util.Objects;

import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;

import org.apache.commons.lang.RandomStringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.multipart.MultipartFile;

import com.nmims.beans.ServiceRequestDocumentBean;
import com.nmims.beans.ServiceRequestStudentPortal;
import com.nmims.enums.ServiceRequestTypeEnum;
import com.nmims.helpers.AWSHelper;
import com.nmims.repository.ServiceRequestRepository;

/**
 * Abstract Class containing methods required for the Service Request module.
 * @author Raynal Dcunha
 */
public abstract class ServiceRequestAbstract {
	@Value("${AWS_SR_DOCS_BUCKET}")
	private String AWS_SR_DOCS_BUCKET;
	
	@Value("${SR_DOCS_S3_PATH}")
	private String SR_DOCS_S3_PATH;
	
	protected static final String TRAN_STATUS_FREE = "Free";
	
	protected static final String AMOUNT_ZERO = "0";
	
	protected static final String CATEGORY_ADMISSION = "Admission";
	protected static final String CATEGORY_ACADEMICS = "Academics";
	protected static final String CATEGORY_EXAM = "Exam";
	
	protected static final String REQUEST_STATUS_SUBMITTED = "Submitted";
	protected static final String REQUEST_STATUS_CLOSED = "closed";
	
	protected static final String HAS_DOCUMENTS_NO = "N";
	protected static final String HAS_DOCUMENTS_YES = "Y";
	
	protected static final String MODE_OF_DISPATCH_LC = "LC";
	protected static final String MODE_OF_DISPATCH_COURIER = "Courier";
	
	protected static final String DEVICE_WEBAPP = "WebApp";
	protected static final String DEVICE_MOBILEAPP = "MobileApp";
	
	private final ServiceRequestRepository serviceRequestRepository;
	private final AWSHelper awsHelper;
	
	public ServiceRequestAbstract(ServiceRequestRepository serviceRequestRepository, AWSHelper awsHelper) {
		Objects.requireNonNull(serviceRequestRepository);			//Fail-fast approach, field is guaranteed be non-null.
		Objects.requireNonNull(awsHelper);
		this.serviceRequestRepository = serviceRequestRepository;
		this.awsHelper = awsHelper;
	}
	
	/**
	 * The type of ServiceRequestTypeEnum is returned.
	 * @return ServiceRequestTypeEnum element
	 */
	public abstract ServiceRequestTypeEnum getServiceRequestType();
	
	/**
	 * Fetch the student personal details (sapid, firstName, lastName, fatherName, motherName, husbandName, emailId, mobile) 
	 * from the database and return as a Map.
	 * @param sapid - studentNo of the Student
	 * @return Map containing the student' personal details
	 */
	public Map<String, Object> studentDetails(Long sapid) {
		return serviceRequestRepository.studentPersonalDetails(sapid);
	}
	
	/**
	 * Fetch the student enrollment Year and Month from database.
	 * @param sapid - studentNo of the Student
	 * @return Map containing the student' enrollment year and month details
	 */
	public Map<String, Object> studentEnrollmentDetails(Long sapid) {
		return serviceRequestRepository.studentEnrollmentYearMonth(sapid);
	}
	
	/**
	 * Creating a bean containing details of Service Request.
	 * @param sapid - studentNo of the Student
	 * @param srType - type of Service Request
	 * @param description - description of the Service Request
	 * @param device - device from which the Service Request was created
	 * @return bean containing details of Free Service Request
	 */
	public abstract ServiceRequestStudentPortal createServiceRequestBean(Long sapid, ServiceRequestTypeEnum srType, 
																		String description, String device);
	
	/**
	 * Storing the Free Service Request record in database.
	 * @param serviceRequest - bean containing Service Request details
	 * @return serviceRequestId of the record created
	 */
	public Long storeFreeServiceRequestRecord(ServiceRequestStudentPortal serviceRequest) {
		return serviceRequestRepository.insertFreeServiceRequestRecord(serviceRequest);
	}
	
	/**
	 * Remove the static part of the s3 Bucket URL from the passed filePath.
	 * @param filePath - path of the file
	 * @return filePath with the static s3 Bucket URL removed
	 */
	public String removeStaticS3PathFromFilePath(String filePath) {
		return filePath.replace(SR_DOCS_S3_PATH, "");
	}
	
	/**
	 * Uploads the Service Request document for the provided Service Request and returns the filePath of the uploaded file.
	 * The Folder Name of the Bucket in which the file is to uploaded is passed as a parameter.
	 * @param sapid - studentNo of the Student
	 * @param serviceRequestId - id of the Service Request
	 * @param urlDocumentName - document name to be appended in the URL
	 * @param srDocumentFile - MultiPart file to be uploaded
	 * @param srDocsBucketFolderName - bucket folder name in which the file is to be uploaded
	 * @return AWS filePath of the uploaded file
	 */
	public ServiceRequestDocumentBean uploadServiceRequestDocument(Long sapid, Long serviceRequestId, String urlDocumentName,
																	MultipartFile srDocumentFile, String srDocsBucketFolderName) {
		ServiceRequestDocumentBean documentBean = new ServiceRequestDocumentBean();
		documentBean.setServiceRequestId(serviceRequestId);
		documentBean.setDocumentName("Document for " + getServiceRequestType().getValue());
		
		String fileName = sapid + "_" + urlDocumentName + "_" + RandomStringUtils.randomAlphanumeric(10) + 
							srDocumentFile.getOriginalFilename().substring(srDocumentFile.getOriginalFilename().lastIndexOf("."));
		
		String fileUploadPath = awsHelper.uploadFile(srDocumentFile, srDocsBucketFolderName, AWS_SR_DOCS_BUCKET, srDocsBucketFolderName + fileName);
		
		if(fileUploadPath.isEmpty())
			throw new RuntimeException("Error while uploading student document. Please try again!");
		
		documentBean.setFilePath(fileUploadPath);
		return documentBean;
	}
	
	/**
	 * Insert the document record in the Service Request documents table.
	 * @param srDocument - bean containing the service request documents detail
	 * @return id of the record inserted
	 */
	public Long insertServiceRequestDocumentRecord(ServiceRequestDocumentBean srDocument) {
		return serviceRequestRepository.inserServiceRequestDocumentRecord(srDocument);
	}
	
	/**
	 * Update the hasDocuments flag of the Service Request and return the noOfRowsUpdated.
	 * @param serviceRequestId - id of the Service Request
	 * @param hasDocument - flag denoting if document exists for the service request
	 * @param user - id of the user
	 * @return count of noOfRowsUpdated
	 */
	public Long updateSrRecordDocumentStatus(Long serviceRequestId, boolean hasDocument, String user) {
		String srDocumentStatus = hasDocument ? "Y" : "N";
		return serviceRequestRepository.updateSrRecordDocumentStatus(serviceRequestId, srDocumentStatus, user);
	}
	
	/**
	 * Send mail to the student on successful Service Request creation.
	 * @param serviceRequest - bean containing the Service Request details
	 * @param studentSapid - studentNo of the Student
	 * @param studentFirstName - firstName of the Student
	 * @param studentLastName - lastName of the Student
	 * @param studentEmailId - emailId of the Student
	 */
	public abstract void sendSuccessfulSrRequestMail(ServiceRequestStudentPortal serviceRequest, String studentSapid,
													String studentFirstName, String studentLastName, String studentEmailId);
	
	/**
	 * Send mail to the concerned team on successful Service Request creation.
	 * @param serviceRequest - bean containing the Service Request details
	 * @param studentSapid - studentNo of the Student
	 * @param studentFirstName - firstName of the Student
	 * @param studentLastName - lastName of the Student
	 * @param studentEmailId - emailId of the Student
	 */
	public abstract void sendSuccessfulSrCreatedMail(ServiceRequestStudentPortal serviceRequest, String studentSapid,
													String studentFirstName, String studentLastName);
	
	/**
	 * Convert the Email addresses passed in a String array to an Internet Address array.
	 * The String array is converted to a comma separated string which is then parsed as an Internet Address array.
	 * @param emailAddresses - array containing email address as a String
	 * @return Internet Address array containing the Internet Address of each email address.
	 * @throws AddressException
	 */
	public InternetAddress[] convertEmailAddressToInternetAddressArray(String... emailAddresses) throws AddressException {
		return InternetAddress.parse(String.join(",", emailAddresses));
	}
}
