package com.nmims.services;

import java.util.List;

import com.nmims.beans.TestExamBean;

public interface StudentTestServiceInterface {
	
	List<TestExamBean> getTestDataForTODO(Long timeboundid, String sapid) throws Exception;
	
	List<TestExamBean> getFinishedTestDataForTODO(Long timeboundid, String sapid) throws Exception;
	
	List<TestExamBean> getTestDataForCalendar(String sapid) throws Exception;
	
	/**
	 * Checks if the test is created for Digital Marketing Strategy subject and test is of type Project
	 * which are the attributes of PDDM Project IA.
	 * @param subject - subject name
	 * @param testType - type of test
	 * @return boolean value indicating if the test subject and type are valid
	 */
	boolean checkIfTestPddmProject(final String subject, final String testType);
	
	/**
	 * Depending on the test maxScore, the booking type (Project Registration/Project Re-Registration) is set.
	 * And checks if the student has booked (paid) for the above set booking type.
	 * @param sapid - student No.
	 * @param timeboundId - timeboundId of the student
	 * @param iaMaxScore - test max score
	 * @return boolean value indicating successful project registration/re-registration booking
	 */
	boolean checkProjectRegBookingStatus(final String sapid, final long timeboundId, final int iaMaxScore);
	
	/**
	 * The timeboundId is fetched using the referenceId (moduleId) passed as the parameter.
	 * checkProjectRegBookingStatus() method is called using the fetched timeboundId.
	 * @param sapid - student No.
	 * @param iaMaxScore - test max score
	 * @param referenceId - test referenceId
	 * @return boolean value indicating successful project registration/re-registration booking
	 */
	boolean getStudentTimeboundAndCheckProjectRegStatus(final String sapid, final int iaMaxScore, final int referenceId);
	
	/**
	 * Fetches reExam tests for student with role as Resit in timebound using the sapid passed as parameter.
	 * @param sapid - studentNo
	 * @return list of reExam tests
	 */
	List<TestExamBean> projectReExamListForResitStudent(final String sapid);
	
	/**
	 * Fetches reExam tests for student with role as Resit in timebound using the sapid and referenceId passed as parameters.
	 * @param sapid - studentNo
	 * @param referenceId - sessionPlan module ID
	 * @return list of reExam tests
	 */
	List<TestExamBean> projectReExamListForModuleId(final String sapid, final Integer referenceId);
}
