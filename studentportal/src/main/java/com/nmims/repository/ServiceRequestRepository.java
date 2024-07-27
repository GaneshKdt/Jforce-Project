package com.nmims.repository;

import java.util.List;
import java.util.Map;

import com.nmims.beans.ServiceRequestDocumentBean;
import com.nmims.beans.ServiceRequestStudentPortal;
import com.nmims.beans.StudentStudentPortalBean;

/**
 * Repository Interface created for communicating with the DAO layers for the Service Request Module
 * @author Raynal Dcunha
 */
public interface ServiceRequestRepository {
	/**
	 * Obtain the stored FatherName, MotherName and HusbandName (Spouse Name) of student.
	 * @param sapid - studentNo of the student
	 * @return Map containing the student' details
	 */
	Map<String, Object> studentFatherMotherHusbandName(Long sapid);
	/**
	 * Obtain the stored email address and mobile number of student.
	 * @param sapid - studentNo of the student
	 * @return Map containing the student' details
	 */
	Map<String, Object> studentEmailIdMobileNo(Long sapid);
	/**
	 * Obtain the student' personal details stored.
	 * @param sapid - studentNo of the student
	 * @return Map containing the student' details
	 */
	Map<String, Object> studentPersonalDetails(Long sapid);
	/**
	 * Obtain the student' enrollment Year and Month details.
	 * @param sapid - studentNo of the student
	 * @return Map containing the student' enrollment year and month details
	 */
	Map<String, Object> studentEnrollmentYearMonth(Long sapid);
	
	/**
	 * A list of description is returned of Service Request' having type as passed in parameter 
	 * and status not Closed or Cancelled.
	 * @param sapid - studentNo of the Student
	 * @param srType - Service Request type
	 * @return List of String containing the Service Request description
	 */
	List<String> srDescriptionList(Long sapid, String srType);
	/**
	 * Returns a List of ServiceRequestBean containing the Service Request type and sapid of provided Service Request IDs.
	 * @param serviceRequestIdList - List containing the Service Request IDs
	 * @return List of Bean containing the Service Request id, type and sapid
	 */
	List<ServiceRequestStudentPortal> serviceRequestTypeSapidList(List<String> serviceRequestIdList);
	
	/**
	 * Insert the Free Service Request record in database and return the id generated.
	 * @param serviceRequest - bean containing the Service Request details
	 * @return serviceRequestId of the created SR record
	 */
	Long insertFreeServiceRequestRecord(ServiceRequestStudentPortal serviceRequest);
	/**
	 * Insert the Service Request document record in database and return the id generated.
	 * @param srDocument - bean containing the SR document details
	 * @return id of the created SR document record
	 */
	Long inserServiceRequestDocumentRecord(ServiceRequestDocumentBean srDocument);
	/**
	 * Update the hasDocument flag of the mentioned Service Request.
	 * @param serviceRequestId - id of the Service Request record
	 * @param hasDocuments - flag denoting if the service request contains a document
	 * @param user - id of the user
	 * @return count of number of rows updated
	 */
	Long updateSrRecordDocumentStatus(Long serviceRequestId, String hasDocuments, String user);
	
	/**
	 * Update the Student Email Id in database and return the number of rows updated.
	 * @param sapid - studentNo of the Student
	 * @param emailId - emailId of the Student to be updated
	 * @param user - id of the Admin user
	 * @return count of number of rows updated
	 */
	Integer studentEmailIdUpdate(Long sapid, String emailId, String user);
	/**
	 * Update the Student MobileNo in database and return the number of rows updated.
	 * @param sapid - studentNo of the Student
	 * @param mobile - mobileNo of the Student to be updated
	 * @param user - id of the Admin user
	 * @return count of number of rows updated
	 */
	Integer studentMobileNoUpdate(Long sapid, String mobile, String user);
	/**
	 * Update the Student FatherName in database and return the number of rows updated.
	 * @param sapid - studentNo of the Student
	 * @param fatherName - fatherName of the Student to be updated
	 * @param user - id of the Admin user
	 * @return count of number of rows updated
	 */
	Integer studentFatherNameUpdate(Long sapid, String fatherName, String user);
	/**
	 * Update the Student MotherName in database and return the number of rows updated.
	 * @param sapid - studentNo of the Student
	 * @param motherName - motherName of the Student to be updated
	 * @param user - id of the Admin user
	 * @return count of number of rows updated
	 */
	Integer studentMotherNameUpdate(Long sapid, String motherName, String user);
	/**
	 * Update the Student SpouseName in database and return the number of rows updated.
	 * @param sapid - studentNo of the Student
	 * @param spouseName - spouseName of the Student to be updated
	 * @param user - id of the Admin user
	 * @return count of number of rows updated
	 */
	Integer studentSpouseNameUpdate(Long sapid, String spouseName, String user);
	
	/**
	 * Get the Email Attribute of Student from LDAP.
	 * @param sapid - studentNo of the Student
	 * @return mail attribute value from LDAP Object of Student
	 */
	String getStudentEmailAttributeLdap(Long sapid);
	/**
	 * Get the Mobile Attribute of Student from LDAP.
	 * @param sapid - studentNo of the Student
	 * @return mobile attribute value from LDAP Object of Student
	 */
	String getStudentMobileAttributeLdap(Long sapid);
	/**
	 * Update the mail attribute value in LDAP Object of Student.
	 * @param sapid - studentNo of the Student
	 * @param attributeValue - value of the attribute
	 */
	void updateStudentMailAttributeLdap(Long sapid, String attributeValue);
	/**
	 * Update the mobile attribute value in LDAP Object of Student.
	 * @param sapid - studentNo of the Student
	 * @param attributeValue - value of the attribute
	 */
	void updateStudentMobileAttributeLdap(Long sapid, String attributeValue);
	
	/**
	 * Updates the Student' programStatus to Program Withdrawal and ProgramCleared flag to true.
	 * @param sapid - studentNo of the Student
	 * @param userId - id of the Admin user
	 * @return count of number of rows updated
	 */
	Integer updateProgramStatusForDeregisteredStudent(Long sapid, String userId,StudentStudentPortalBean newMappedProgram);
	
	/**
	 * Checks if the student' Program Status is already Active (null), 
	 * if not the Student programStatus is marked as Active (null) and programCleared flag to false.
	 * @param sapid - studentNo of the Student
	 * @param userId - id of the Admin user
	 * @return count of number of rows updated
	 */
	Integer updateProgramStatusAsActiveForDeregistrationCancellation(Long sapid, String userId);
	
}
