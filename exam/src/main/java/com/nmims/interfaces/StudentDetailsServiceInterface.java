package com.nmims.interfaces;

import com.nmims.dto.StudentProfileDetailsExamDto;

/**
 * Service Interface created for performing CRUD operations on Student Details
 * @author Raynal Dcunha
 */
public interface StudentDetailsServiceInterface {
	/**
	 * Validate Student Profile details and update profile details in MySQL database,
	 * Also update the LDAP Object of student,
	 * And update Student' Account details in Salesforce.
	 * @param studentProfileDetailsDto - DTO containing the Student' Profile details
	 * @param userId - id of the Admin user performing the updation
	 */
	void updateStudentProfileDetails(StudentProfileDetailsExamDto studentProfileDetailsDto, String userId);
	
	/**
	 * Fetch and return student profile details.
	 * @param sapid - Student No. of the student
	 * @param sem - Enrolled semester of the student
	 * @return DTO Object containing the student profile details
	 */
	StudentProfileDetailsExamDto viewStudentProfileDetails(Long sapid, Integer sem);
}
