package com.nmims.repository;

import java.time.LocalDateTime;

import com.nmims.beans.Person;
import com.nmims.beans.StudentExamBean;

/**
 * Repository Interface created for communicating with the DAO layers for performing CRUD operations on Student Details
 * @author Raynal Dcunha
 */
public interface StudentDetailsRepository {
	/**
	 * update the student details in exam.students table of database
	 * @param student - studentBean containing the student details
	 * @param userId - id of the Admin user
	 * @return no. of rows updated in database
	 */
	int updateStudentProfile(StudentExamBean student, String userId);
	
	/**
	 * Get student details from the students table in database
	 * @param sapid - Student No. of the student
	 * @param sem - Enrolled semester of the student
	 * @return studentBean containing the student' details
	 */
	StudentExamBean getStudentProfile(Long sapid, int sem);
	
	/**
	 * Get the Attributes stored in LDAP Object of student
	 * @param sapid - Studen No. of the student
	 * @return Person bean containing the student attributes
	 */
	Person getStudentLdapObjectAttributes(String sapid);
	
	/**
	 * Modify the LDAP Attributes of the student and get the lastModified DateTime of the LDAP Object
	 * @param sapid - Student No. of the student
	 * @param firstName - first name of the student
	 * @param lastName - last name of the student
	 * @param displayName - display name of the student
	 * @param email - email address of the student
	 * @param mobile - mobile number of the student
	 * @param altMobile - alternate phone number of the student
	 * @param program - student program
	 * @return lastModified DateTime of the LDAP Object
	 */
	LocalDateTime updateStudentLdapObjectAttributes(String sapid, String firstName, String lastName, String displayName, 
													String email, String mobile, String altMobile, String program);
}
