package com.nmims.test.services;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.not;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.atMost;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.hamcrest.collection.IsIterableContainingInOrder;
import org.hamcrest.collection.IsMapContaining;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.mockito.ArgumentMatchers;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.test.context.junit4.SpringRunner;

import com.nmims.beans.ResponseBean;
import com.nmims.beans.StudentQuestionResponseExamBean;
import com.nmims.beans.StudentsTestDetailsExamBean;
import com.nmims.beans.TEEResultBean;
import com.nmims.beans.TestExamBean;
import com.nmims.beans.TestQuestionExamBean;
import com.nmims.daos.TestDAO;
import com.nmims.services.IATestService;

@RunWith(SpringRunner.class)
@SpringBootTest
//@TestPropertySource("file:C:/NMIMS_PROPERTY_FILE/application.properties")
public class IATestServiceTest {
	@Autowired
	private IATestService iaTestService;
	
	@MockBean
	private TestDAO testDAO;
	
	@Rule
	public ExpectedException exceptionRule = ExpectedException.none();
	
	private static final Long VALID_TEST_ID = 21964903L;
	private static final Long INVALID_TEST_ID = 21969000L;
	
	private static final Long TEST_QUESTION_ID = 41169L;
	private static final String TEST_NAME = "ADS-IA3-MSc (AI) - Jan 2023 - Quarter 4  (New) - Cohort 5";
	private static final String TEST_FACULTY_ID = "TestFaculty001";
	
	private static final String BOD_ACTIVE_TRUE = "Y";
	private static final String BOD_ACTIVE_FALSE = "N";
	
	private static final String TYPE_ASSIGNMENT = "Assignment";
	private static final String TYPE_PROJECT = "Project";
	private static final String TYPE_TEST = "Test";
	
	private static final String USER_NAME = "Bot";
	
	private static final Logger logger = LoggerFactory.getLogger(IATestServiceTest.class);
	
	/**
	 * Unit test for testMaxAttempts() method of IATestService class.
	 */
	@Test
	public void testMaxAttemptsTest1() {
		//Scenario 1: Max attempts of a valid test
		when(testDAO.getMaxAttemptById(VALID_TEST_ID)).thenReturn(1);
		
		int scenario1 = iaTestService.testMaxAttempts(VALID_TEST_ID);
		logger.info("Test max attempts scenario1: {}", scenario1);
		
		verify(testDAO).getMaxAttemptById(VALID_TEST_ID);
		assertEquals(1, scenario1);
	}
	
	/**
	 * Unit test for testMaxAttempts() method of IATestService class.
	 */
	@Test
	public void testMaxAttemptsTest2() {
		//Scenario 2: Max attempts of a invalid test
		when(testDAO.getMaxAttemptById(INVALID_TEST_ID)).thenThrow(EmptyResultDataAccessException.class);
		
		int scenario2 = iaTestService.testMaxAttempts(INVALID_TEST_ID);
		logger.info("Test max attempts scenario2: {}", scenario2);
		
		verify(testDAO).getMaxAttemptById(INVALID_TEST_ID);
		assertEquals(0, scenario2);
	}
	
	/**
	 * Unit test for sectionQuestionsList() method of IATestService class.
	 */
	@Test
	public void testSectionQuestionsList1() {
		//Scenario 1: test questions having multiple sections
		getTestQuestions();
		
		String section1 = "SECTION 1";
		String section2 = "SECTION 2";
		Map<Integer, String> sectionMap = new HashMap<>();
		sectionMap.put(255, section1);
		sectionMap.put(256, section2);
		
		when(testDAO.getSectionIdNameMapByTestId(VALID_TEST_ID))
			.thenReturn(sectionMap);
		
		Map<String, List<TestQuestionExamBean>> scenario1 = iaTestService.sectionQuestionsList(VALID_TEST_ID);
		logger.info("Section questions scenario1: {}", scenario1);
		
		verifySectionQuestions();
		
		assertThat(scenario1.size(), is(2));
		assertThat(scenario1, IsMapContaining.hasKey(section1));
		assertThat(scenario1, IsMapContaining.hasKey(section2));
		assertThat(scenario1, not(IsMapContaining.hasKey("SECTION 3")));
		assertThat(scenario1.get(section1).size(), is(2));
		assertThat(scenario1.get(section2).size(), is(1));
	}
	
	/**
	 * Unit test for sectionQuestionsList() method of IATestService class.
	 */
	@Test
	public void testSectionQuestionsList2() {
		//Scenario 2: test questions having no section
		getTestQuestions();
		
		String section1 = "Section 1";
		when(testDAO.getSectionIdNameMapByTestId(VALID_TEST_ID))
			.thenReturn(new HashMap<Integer, String>());
		
		Map<String, List<TestQuestionExamBean>> scenario2 = iaTestService.sectionQuestionsList(VALID_TEST_ID);
		logger.info("Section questions scenario2: {}", scenario2);
		
		verifySectionQuestions();
		
		assertThat(scenario2.size(), is(1));
		assertThat(scenario2, IsMapContaining.hasKey(section1));
		assertThat(scenario2, not(IsMapContaining.hasKey("Section 2")));
		assertThat(scenario2.get(section1).size(), is(3));
	}
	
	/**
	 * Unit test for sectionQuestionsList() method of IATestService class.
	 */
	@Test
	public void testSectionQuestionsList3() {
		//Scenario 3: test questions of an invalid test
		when(testDAO.getQuestionsByTestId(INVALID_TEST_ID))
			.thenThrow(EmptyResultDataAccessException.class);
		
		Map<String, List<TestQuestionExamBean>> scenario3 = iaTestService.sectionQuestionsList(INVALID_TEST_ID);
		logger.info("Section questions scenario3: {}", scenario3);
		
		verify(testDAO).getQuestionsByTestId(INVALID_TEST_ID);
		assertThat(scenario3, not(IsMapContaining.hasKey("Section 1")));
		assertThat(scenario3.size(), is(0));
	}
	
	/**
	 * Unit test for bodAppliedQuestions() method of IATestService class.
	 */
	@Test
	public void testBodAppliedQuestions1() {
		//Scenario 1: Get BoD applied questions list
		when(testDAO.getBodQuestionsByTestIdActive(VALID_TEST_ID, BOD_ACTIVE_TRUE))
			.thenReturn(Arrays.asList(41181, 41182));
		
		List<Integer> scenario1 = iaTestService.bodAppliedQuestions(VALID_TEST_ID);
		logger.info("BoD applied questions scenario1: {}", scenario1);
		
		verify(testDAO).getBodQuestionsByTestIdActive(VALID_TEST_ID, BOD_ACTIVE_TRUE);
		assertThat(scenario1.size(), is(2));
		assertThat(scenario1, IsIterableContainingInOrder.contains(41181, 41182));
	}
	
	/**
	 * Unit test for bodAppliedQuestions() method of IATestService class.
	 */
	@Test
	public void testBodAppliedQuestions2() {
		//Scenario 2: No questions are marked for BoD
		when(testDAO.getBodQuestionsByTestIdActive(INVALID_TEST_ID, BOD_ACTIVE_TRUE))
			.thenThrow(EmptyResultDataAccessException.class);
		
		List<Integer> scenario2 = iaTestService.bodAppliedQuestions(INVALID_TEST_ID);
		logger.info("BoD applied questions scenario2: {}", scenario2);
		
		verify(testDAO).getBodQuestionsByTestIdActive(INVALID_TEST_ID, BOD_ACTIVE_TRUE);
		assertThat(scenario2.size(), is(0));
	}
	
	/**
	 * Unit test for checkIfTestCompleted() method of IATestService class.
	 */
	@Test
	public void testCheckIfTestCompleted1() {
		//Scenario 1: test completed for type Test
		TestExamBean testBean = new TestExamBean();
		testBean.setEndDate("2023-05-04 23:00:00");
		testBean.setDuration(45);
		testBean.setTestType(TYPE_TEST);
		
		when(testDAO.getTestTypeEndDateDurationByTestId(VALID_TEST_ID))
			.thenReturn(testBean);
		
		boolean scenario1 = iaTestService.checkIfTestCompleted(VALID_TEST_ID);
		logger.info("Test completed scenario1: {}", scenario1);
		
		verify(testDAO).getTestTypeEndDateDurationByTestId(VALID_TEST_ID);
		assertTrue(scenario1);
	}
	
	/**
	 * Unit test for checkIfTestCompleted() method of IATestService class.
	 */
	@Test
	public void testCheckIfTestCompleted2() {
		//Scenario 2: test not completed for type Test 
		TestExamBean testBean = new TestExamBean();
		testBean.setEndDate("2030-05-04 23:00:00");
		testBean.setDuration(45);
		testBean.setTestType(TYPE_TEST);
		
		when(testDAO.getTestTypeEndDateDurationByTestId(VALID_TEST_ID))
			.thenReturn(testBean);
		
		boolean scenario2 = iaTestService.checkIfTestCompleted(VALID_TEST_ID);
		logger.info("Test completed scenario2: {}", scenario2);
		
		verify(testDAO).getTestTypeEndDateDurationByTestId(VALID_TEST_ID);
		assertFalse(scenario2);
	}
	
	/**
	 * Unit test for checkIfTestCompleted() method of IATestService class.
	 */
	@Test
	public void testCheckIfTestCompleted3() {
		//Scenario 3: test completed for Type Project
		TestExamBean testBean = new TestExamBean();
		testBean.setEndDate("2023-05-04 11:15:00");
		testBean.setDuration(600);
		testBean.setTestType(TYPE_PROJECT);
		
		when(testDAO.getTestTypeEndDateDurationByTestId(VALID_TEST_ID))
			.thenReturn(testBean);
		
		boolean scenario3 = iaTestService.checkIfTestCompleted(VALID_TEST_ID);
		logger.info("Test completed scenario3: {}", scenario3);
		
		verify(testDAO).getTestTypeEndDateDurationByTestId(VALID_TEST_ID);
		assertTrue(scenario3);
	}
	
	/**
	 * Unit test for checkIfTestCompleted() method of IATestService class.
	 */
	@Test
	public void testCheckIfTestCompleted4() {
		//Scenario 4: test not completed for Type Assignment
		TestExamBean testBean = new TestExamBean();
		testBean.setEndDate("2030-05-04 14:00:00");
		testBean.setDuration(600);
		testBean.setTestType(TYPE_ASSIGNMENT);
		
		when(testDAO.getTestTypeEndDateDurationByTestId(VALID_TEST_ID))
			.thenReturn(testBean);
		
		boolean scenario4 = iaTestService.checkIfTestCompleted(VALID_TEST_ID);
		logger.info("Test completed scenario4: {}", scenario4);
		
		verify(testDAO).getTestTypeEndDateDurationByTestId(VALID_TEST_ID);
		assertFalse(scenario4);
	}
	
	/**
	 * Unit test for checkIfTestCompleted() method of IATestService class.
	 */
	@Test
	public void testCheckIfTestCompleted5() {
		//Scenario 5: test not completed for Type Assignment
		TestExamBean testBean = new TestExamBean();
		testBean.setEndDate("2030-05-04 14:00:00");
		testBean.setDuration(600);
		testBean.setTestType("");
		
		when(testDAO.getTestTypeEndDateDurationByTestId(VALID_TEST_ID))
			.thenReturn(testBean);
		
		boolean scenario5 = iaTestService.checkIfTestCompleted(VALID_TEST_ID);
		logger.info("Test completed scenario5: {}", scenario5);
		
		verify(testDAO).getTestTypeEndDateDurationByTestId(VALID_TEST_ID);
		assertFalse(scenario5);
	}
	
	/**
	 * Unit test for checkIfTestCompleted() method of IATestService class.
	 */
	@Test
	public void testCheckIfTestCompleted6() {
		//Scenario 6: checking test completed for an invalid test
		when(testDAO.getTestTypeEndDateDurationByTestId(INVALID_TEST_ID))
			.thenThrow(EmptyResultDataAccessException.class);
		
		boolean scenario6 = iaTestService.checkIfTestCompleted(INVALID_TEST_ID);
		logger.info("Test completed scenario6: {}", scenario6);
		
		verify(testDAO).getTestTypeEndDateDurationByTestId(INVALID_TEST_ID);
		assertFalse(scenario6);
	}
	
	/**
	 * Unit test for checkIfTestResultsLive() method of IATestService class.
	 */
	@Test
	public void testCheckIfTestResultsLive1() {
		//Scenario 1: results live of a valid test
		when(testDAO.checkTestResultsLive(VALID_TEST_ID))
			.thenReturn(1);
		
		boolean scenario1 = iaTestService.checkIfTestResultsLive(VALID_TEST_ID);
		logger.info("Test results live scenario1: {}", scenario1);
		
		verify(testDAO).checkTestResultsLive(VALID_TEST_ID);
		assertTrue(scenario1);
	}
	
	/**
	 * Unit test for checkIfTestResultsLive() method of IATestService class.
	 */
	@Test
	public void testCheckIfTestResultsLive2() {
		//Scenario 2: results not live for a valid test
		when(testDAO.checkTestResultsLive(VALID_TEST_ID))
			.thenReturn(0);
		
		boolean scenario2 = iaTestService.checkIfTestResultsLive(VALID_TEST_ID);
		logger.info("Test results live scenario2: {}", scenario2);
		
		verify(testDAO).checkTestResultsLive(VALID_TEST_ID);
		assertFalse(scenario2);
	}
	
	/**
	 * Unit test for checkIfTestResultsLive() method of IATestService class.
	 */
	@Test
	public void testCheckIfTestResultsLive3() {
		//Scenario 3: checking results live of an invalid test
		when(testDAO.checkTestResultsLive(INVALID_TEST_ID))
			.thenThrow(EmptyResultDataAccessException.class);
		
		boolean scenario3 = iaTestService.checkIfTestResultsLive(INVALID_TEST_ID);
		logger.info("Test results live scenario3: {}", scenario3);
		
		verify(testDAO).checkTestResultsLive(INVALID_TEST_ID);
		assertFalse(scenario3);
	}
	
	/**
	 * Unit test for applyingBenefitOfBoubt() method of IATestService class.
	 */
	@Test
	public void testApplyingBenefitOfBoubt1() {
		//Scenario 1: applying BoD to a question and re-run results and unmark TEE processed flag
		TestExamBean testBean = new TestExamBean();
		testBean.setEndDate("2023-05-04 23:00:00");
		testBean.setDuration(45);
		testBean.setTestType(TYPE_TEST);
		
		when(testDAO.getTestTypeEndDateDurationByTestId(VALID_TEST_ID))
			.thenReturn(testBean);
		when(testDAO.checkBodByTestIdQuestionId(VALID_TEST_ID, TEST_QUESTION_ID))
			.thenReturn(0);
		when(testDAO.insertBenefitOfDoubt(VALID_TEST_ID, TEST_QUESTION_ID, BOD_ACTIVE_TRUE, USER_NAME))
			.thenReturn(1);
		when(testDAO.checkTestResultsLive(VALID_TEST_ID))
			.thenReturn(1);
		testAttemptedStudentScore();
		
		teeMarksProcessed();
		when(testDAO.checkIfTEEMarksPresent(ArgumentMatchers.any(TEEResultBean.class)))
			.thenReturn(true);
		when(testDAO.updateProcessedFlagInTEEMarks(ArgumentMatchers.any(TEEResultBean.class), ArgumentMatchers.eq(USER_NAME)))
			.thenReturn(true);
		
		Map<String, Boolean> scenario1 = iaTestService.applyingBenefitOfBoubt(VALID_TEST_ID, TEST_QUESTION_ID, USER_NAME);
		logger.info("Apply BoD scenario1: {}", scenario1);
		
		verify(testDAO).getTestTypeEndDateDurationByTestId(VALID_TEST_ID);
		verify(testDAO).checkBodByTestIdQuestionId(VALID_TEST_ID, TEST_QUESTION_ID);
		verify(testDAO).insertBenefitOfDoubt(VALID_TEST_ID, TEST_QUESTION_ID, BOD_ACTIVE_TRUE, USER_NAME);
		verify(testDAO).checkTestResultsLive(VALID_TEST_ID);
		verifyStudentsAttempted();
		
		verifyTEEMarksProcessed();
		verify(testDAO, atMost(2)).checkIfTEEMarksPresent(ArgumentMatchers.any(TEEResultBean.class));
		verify(testDAO, atMost(2)).updateProcessedFlagInTEEMarks(ArgumentMatchers.any(TEEResultBean.class), ArgumentMatchers.eq(USER_NAME));
		
		assertThat(scenario1.size(), is(2));
		assertThat(scenario1, IsMapContaining.hasKey("benefitOfDoubt"));
		assertThat(scenario1, IsMapContaining.hasKey("resultsLive"));
		assertThat(scenario1, not(IsMapContaining.hasKey("bodApplied")));
		assertThat(scenario1.get("benefitOfDoubt"), is(true));
		assertThat(scenario1.get("resultsLive"), is(true));
	}
	
	/**
	 * Unit test for applyingBenefitOfBoubt() method of IATestService class.
	 */
	@Test
	public void testApplyingBenefitOfBoubt2() {
		//Scenario 2: applying BoD to a question wherein inactive BoD record is already present for the question and re-run results
		TestExamBean testBean = new TestExamBean();
		testBean.setEndDate("2023-05-04 11:15:00");
		testBean.setDuration(600);
		testBean.setTestType(TYPE_PROJECT);
		
		when(testDAO.getTestTypeEndDateDurationByTestId(VALID_TEST_ID))
			.thenReturn(testBean);
		when(testDAO.checkBodByTestIdQuestionId(VALID_TEST_ID, TEST_QUESTION_ID))
			.thenReturn(1);
		when(testDAO.updateBenefitOfDoubt(VALID_TEST_ID, TEST_QUESTION_ID, BOD_ACTIVE_TRUE, USER_NAME))
			.thenReturn(1);
		when(testDAO.checkTestResultsLive(VALID_TEST_ID))
			.thenReturn(1);
		testAttemptedStudentScore();
		
		teeMarksProcessed();
		when(testDAO.checkIfTEEMarksPresent(ArgumentMatchers.any(TEEResultBean.class)))
			.thenReturn(false);
		
		Map<String, Boolean> scenario2 = iaTestService.applyingBenefitOfBoubt(VALID_TEST_ID, TEST_QUESTION_ID, USER_NAME);
		logger.info("Apply BoD scenario2: {}", scenario2);
		
		verify(testDAO).getTestTypeEndDateDurationByTestId(VALID_TEST_ID);
		verify(testDAO).checkTestResultsLive(VALID_TEST_ID);
		verify(testDAO).checkBodByTestIdQuestionId(VALID_TEST_ID, TEST_QUESTION_ID);
		verify(testDAO).updateBenefitOfDoubt(VALID_TEST_ID, TEST_QUESTION_ID, BOD_ACTIVE_TRUE, USER_NAME);
		verifyStudentsAttempted();
		
		verifyTEEMarksProcessed();
		verify(testDAO, atMost(2)).checkIfTEEMarksPresent(ArgumentMatchers.any(TEEResultBean.class));
		
		assertThat(scenario2.size(), is(2));
		assertThat(scenario2, IsMapContaining.hasKey("benefitOfDoubt"));
		assertThat(scenario2, IsMapContaining.hasKey("resultsLive"));
		assertThat(scenario2, not(IsMapContaining.hasKey("bodApplied")));
		assertThat(scenario2.get("benefitOfDoubt"), is(true));
		assertThat(scenario2.get("resultsLive"), is(true));
	}
	
	/**
	 * Unit test for applyingBenefitOfBoubt() method of IATestService class.
	 */
	@Test
	public void testApplyingBenefitOfBoubt3() {
		//Scenario 3: applying BoD to a question with test results not live and unmark TEE processed flag
		TestExamBean testBean = new TestExamBean();
		testBean.setEndDate("2023-05-04 23:00:00");
		testBean.setDuration(45);
		testBean.setTestType(TYPE_TEST);
		
		when(testDAO.getTestTypeEndDateDurationByTestId(VALID_TEST_ID))
			.thenReturn(testBean);
		when(testDAO.checkBodByTestIdQuestionId(VALID_TEST_ID, TEST_QUESTION_ID))
			.thenReturn(0);
		when(testDAO.insertBenefitOfDoubt(VALID_TEST_ID, TEST_QUESTION_ID, BOD_ACTIVE_TRUE, USER_NAME))
			.thenReturn(1);
		when(testDAO.checkTestResultsLive(VALID_TEST_ID))
			.thenReturn(0);
		
		testAttemptedStudents();
		teeMarksProcessed();
		when(testDAO.checkIfTEEMarksPresent(ArgumentMatchers.any(TEEResultBean.class)))
			.thenReturn(true);
		when(testDAO.updateProcessedFlagInTEEMarks(ArgumentMatchers.any(TEEResultBean.class), ArgumentMatchers.eq(USER_NAME)))
			.thenReturn(true);
		
		Map<String, Boolean> scenario3 = iaTestService.applyingBenefitOfBoubt(VALID_TEST_ID, TEST_QUESTION_ID, USER_NAME);
		logger.info("Apply BoD scenario3: {}", scenario3);
		
		verify(testDAO).getTestTypeEndDateDurationByTestId(VALID_TEST_ID);
		verify(testDAO).checkBodByTestIdQuestionId(VALID_TEST_ID, TEST_QUESTION_ID);
		verify(testDAO).insertBenefitOfDoubt(VALID_TEST_ID, TEST_QUESTION_ID, BOD_ACTIVE_TRUE, USER_NAME);
		verify(testDAO).checkTestResultsLive(VALID_TEST_ID);
		
		verify(testDAO).getStudentTestAttemptQuestions(VALID_TEST_ID);
		verifyTEEMarksProcessed();
		verify(testDAO, atMost(2)).checkIfTEEMarksPresent(ArgumentMatchers.any(TEEResultBean.class));
		verify(testDAO, atMost(2)).updateProcessedFlagInTEEMarks(ArgumentMatchers.any(TEEResultBean.class), ArgumentMatchers.eq(USER_NAME));
		
		assertThat(scenario3.size(), is(2));
		assertThat(scenario3, IsMapContaining.hasKey("benefitOfDoubt"));
		assertThat(scenario3, IsMapContaining.hasKey("resultsLive"));
		assertThat(scenario3, not(IsMapContaining.hasKey("bodApplied")));
		assertThat(scenario3.get("benefitOfDoubt"), is(true));
		assertThat(scenario3.get("resultsLive"), is(false));
	}
	
	/**
	 * Unit test for applyingBenefitOfBoubt() method of IATestService class.
	 */
	@Test
	public void testApplyingBenefitOfBoubt4() {
		//Scenario 4: applying BoD to a question with test results not live
		TestExamBean testBean = new TestExamBean();
		testBean.setEndDate("2023-05-04 12:00:00");
		testBean.setDuration(600);
		testBean.setTestType(TYPE_PROJECT);
		
		when(testDAO.getTestTypeEndDateDurationByTestId(VALID_TEST_ID))
			.thenReturn(testBean);
		when(testDAO.checkBodByTestIdQuestionId(VALID_TEST_ID, TEST_QUESTION_ID))
			.thenReturn(0);
		when(testDAO.insertBenefitOfDoubt(VALID_TEST_ID, TEST_QUESTION_ID, BOD_ACTIVE_TRUE, USER_NAME))
			.thenReturn(1);
		when(testDAO.checkTestResultsLive(VALID_TEST_ID))
			.thenReturn(0);
		
		testAttemptedStudents();
		teeMarksProcessed();
		when(testDAO.checkIfTEEMarksPresent(ArgumentMatchers.any(TEEResultBean.class)))
			.thenReturn(false);
		
		Map<String, Boolean> scenario4 = iaTestService.applyingBenefitOfBoubt(VALID_TEST_ID, TEST_QUESTION_ID, USER_NAME);
		logger.info("Apply BoD scenario4: {}", scenario4);
		
		verify(testDAO).getTestTypeEndDateDurationByTestId(VALID_TEST_ID);
		verify(testDAO).checkBodByTestIdQuestionId(VALID_TEST_ID, TEST_QUESTION_ID);
		verify(testDAO).insertBenefitOfDoubt(VALID_TEST_ID, TEST_QUESTION_ID, BOD_ACTIVE_TRUE, USER_NAME);
		verify(testDAO).checkTestResultsLive(VALID_TEST_ID);
		
		verify(testDAO).getStudentTestAttemptQuestions(VALID_TEST_ID);
		verifyTEEMarksProcessed();
		verify(testDAO, atMost(2)).checkIfTEEMarksPresent(ArgumentMatchers.any(TEEResultBean.class));
		
		assertThat(scenario4.size(), is(2));
		assertThat(scenario4, IsMapContaining.hasKey("benefitOfDoubt"));
		assertThat(scenario4, IsMapContaining.hasKey("resultsLive"));
		assertThat(scenario4, not(IsMapContaining.hasKey("bodApplied")));
		assertThat(scenario4.get("benefitOfDoubt"), is(true));
		assertThat(scenario4.get("resultsLive"), is(false));
	}
	
	/**
	 * Unit test for applyingBenefitOfBoubt() method of IATestService class.
	 */
	@Test
	public void testApplyingBenefitOfBoubt5() {
		//Scenario 5: applying BoD to a test which is not yet completed and re-run results
		TestExamBean testBean = new TestExamBean();
		testBean.setEndDate("2030-05-04 11:00:00");
		testBean.setDuration(45);
		testBean.setTestType(TYPE_TEST);
		
		when(testDAO.getTestTypeEndDateDurationByTestId(VALID_TEST_ID))
			.thenReturn(testBean);
		
		logger.info("Apply BoD scenario5 Illegal Argument Exception");
		exceptionRule.expect(IllegalArgumentException.class);
	    exceptionRule.expectMessage("Cannot apply BoD as test is not completed for testId: " + VALID_TEST_ID);
		iaTestService.applyingBenefitOfBoubt(VALID_TEST_ID, TEST_QUESTION_ID, USER_NAME);
	}
	
	/**
	 * Unit test for applyingBenefitOfBoubt() method of IATestService class.
	 */
	@Test
	public void testApplyingBenefitOfBoubt6() {
		//Scenario 6: applying BoD to a question and re-run results for student whose TEE processed cannot be updated
		TestExamBean testBean = new TestExamBean();
		testBean.setEndDate("2023-05-04 11:15:00");
		testBean.setDuration(600);
		testBean.setTestType(TYPE_ASSIGNMENT);
		
		when(testDAO.getTestTypeEndDateDurationByTestId(VALID_TEST_ID))
			.thenReturn(testBean);
		when(testDAO.checkBodByTestIdQuestionId(VALID_TEST_ID, TEST_QUESTION_ID))
			.thenReturn(0);
		when(testDAO.insertBenefitOfDoubt(VALID_TEST_ID, TEST_QUESTION_ID, BOD_ACTIVE_TRUE, USER_NAME))
			.thenReturn(1);
		when(testDAO.checkTestResultsLive(VALID_TEST_ID))
			.thenReturn(1);
		testAttemptedStudentScore();
		
		teeMarksProcessed();
		when(testDAO.checkIfTEEMarksPresent(ArgumentMatchers.any(TEEResultBean.class)))
			.thenReturn(true);
		when(testDAO.updateProcessedFlagInTEEMarks(ArgumentMatchers.any(TEEResultBean.class), ArgumentMatchers.eq(USER_NAME)))
			.thenReturn(false);
		
		logger.info("Apply BoD scenario6 Illegal Argument Exception");
		exceptionRule.expect(IllegalArgumentException.class);
	    exceptionRule.expectMessage("Error in updating processed Flag.");
		iaTestService.applyingBenefitOfBoubt(VALID_TEST_ID, TEST_QUESTION_ID, USER_NAME);
	}
	
	/**
	 * Unit test for removingBenefitOfBoubt() method of IATestService class.
	 */
	@Test
	public void testRemovingBenefitOfBoubt1() {
		//Scenario 1: removing BoD of a question and re-run results and unmark TEE processed flag
		TestExamBean testBean = new TestExamBean();
		testBean.setEndDate("2023-05-04 23:00:00");
		testBean.setDuration(45);
		testBean.setTestType(TYPE_TEST);
		
		when(testDAO.getTestTypeEndDateDurationByTestId(VALID_TEST_ID))
			.thenReturn(testBean);
		when(testDAO.updateBenefitOfDoubt(VALID_TEST_ID, TEST_QUESTION_ID, BOD_ACTIVE_FALSE, USER_NAME))
			.thenReturn(1);
		when(testDAO.checkTestResultsLive(VALID_TEST_ID))
			.thenReturn(1);
		testAttemptedStudentScore();
		
		teeMarksProcessed();
		when(testDAO.checkIfTEEMarksPresent(ArgumentMatchers.any(TEEResultBean.class)))
			.thenReturn(true);
		when(testDAO.updateProcessedFlagInTEEMarks(ArgumentMatchers.any(TEEResultBean.class), ArgumentMatchers.eq(USER_NAME)))
			.thenReturn(true);
		
		Map<String, Boolean> scenario1 = iaTestService.removingBenefitOfBoubt(VALID_TEST_ID, TEST_QUESTION_ID, USER_NAME);
		logger.info("Remove BoD scenario1: {}", scenario1);
		
		verify(testDAO).getTestTypeEndDateDurationByTestId(VALID_TEST_ID);
		verify(testDAO).updateBenefitOfDoubt(VALID_TEST_ID, TEST_QUESTION_ID, BOD_ACTIVE_FALSE, USER_NAME);
		verify(testDAO).checkTestResultsLive(VALID_TEST_ID);
		verifyStudentsAttempted();
		
		verifyTEEMarksProcessed();
		verify(testDAO, atMost(2)).checkIfTEEMarksPresent(ArgumentMatchers.any(TEEResultBean.class));
		verify(testDAO, atMost(2)).updateProcessedFlagInTEEMarks(ArgumentMatchers.any(TEEResultBean.class), ArgumentMatchers.eq(USER_NAME));
		
		assertThat(scenario1.size(), is(2));
		assertThat(scenario1, IsMapContaining.hasKey("benefitOfDoubt"));
		assertThat(scenario1, IsMapContaining.hasKey("resultsLive"));
		assertThat(scenario1, not(IsMapContaining.hasKey("bodRemoved")));
		assertThat(scenario1.get("benefitOfDoubt"), is(true));
		assertThat(scenario1.get("resultsLive"), is(true));
	}
	
	/**
	 * Unit test for removingBenefitOfBoubt() method of IATestService class.
	 */
	@Test
	public void testRemovingBenefitOfBoubt2() {
		//Scenario 2: removing BoD of a question with test results not live and unmark TEE processed flag
		TestExamBean testBean = new TestExamBean();
		testBean.setEndDate("2023-05-04 12:00:00");
		testBean.setDuration(600);
		testBean.setTestType(TYPE_PROJECT);
		
		when(testDAO.getTestTypeEndDateDurationByTestId(VALID_TEST_ID))
			.thenReturn(testBean);
		when(testDAO.updateBenefitOfDoubt(VALID_TEST_ID, TEST_QUESTION_ID, BOD_ACTIVE_FALSE, USER_NAME))
			.thenReturn(1);
		when(testDAO.checkTestResultsLive(VALID_TEST_ID))
			.thenReturn(0);
		
		testAttemptedStudents();
		teeMarksProcessed();
		when(testDAO.checkIfTEEMarksPresent(ArgumentMatchers.any(TEEResultBean.class)))
			.thenReturn(true);
		when(testDAO.updateProcessedFlagInTEEMarks(ArgumentMatchers.any(TEEResultBean.class), ArgumentMatchers.eq(USER_NAME)))
			.thenReturn(true);
		
		Map<String, Boolean> scenario2 = iaTestService.removingBenefitOfBoubt(VALID_TEST_ID, TEST_QUESTION_ID, USER_NAME);
		logger.info("Remove BoD scenario2: {}", scenario2);
		
		verify(testDAO).getTestTypeEndDateDurationByTestId(VALID_TEST_ID);
		verify(testDAO).updateBenefitOfDoubt(VALID_TEST_ID, TEST_QUESTION_ID, BOD_ACTIVE_FALSE, USER_NAME);
		verify(testDAO).checkTestResultsLive(VALID_TEST_ID);
		
		verify(testDAO).getStudentTestAttemptQuestions(VALID_TEST_ID);
		verifyTEEMarksProcessed();
		verify(testDAO, atMost(2)).checkIfTEEMarksPresent(ArgumentMatchers.any(TEEResultBean.class));
		verify(testDAO, atMost(2)).updateProcessedFlagInTEEMarks(ArgumentMatchers.any(TEEResultBean.class), ArgumentMatchers.eq(USER_NAME));
		
		assertThat(scenario2.size(), is(2));
		assertThat(scenario2, IsMapContaining.hasKey("benefitOfDoubt"));
		assertThat(scenario2, IsMapContaining.hasKey("resultsLive"));
		assertThat(scenario2, not(IsMapContaining.hasKey("bodRemoved")));
		assertThat(scenario2.get("benefitOfDoubt"), is(true));
		assertThat(scenario2.get("resultsLive"), is(false));
	}
	
	/**
	 * Unit test for removingBenefitOfBoubt() method of IATestService class.
	 */
	@Test
	public void testRemovingBenefitOfBoubt3() {
		//Scenario 3: removing BoD of a question with test results not live
		TestExamBean testBean = new TestExamBean();
		testBean.setEndDate("2023-05-04 09:00:00");
		testBean.setDuration(600);
		testBean.setTestType(TYPE_ASSIGNMENT);
		
		when(testDAO.getTestTypeEndDateDurationByTestId(VALID_TEST_ID))
			.thenReturn(testBean);
		when(testDAO.updateBenefitOfDoubt(VALID_TEST_ID, TEST_QUESTION_ID, BOD_ACTIVE_FALSE, USER_NAME))
			.thenReturn(1);
		when(testDAO.checkTestResultsLive(VALID_TEST_ID))
			.thenReturn(0);
		
		testAttemptedStudents();
		teeMarksProcessed();
		when(testDAO.checkIfTEEMarksPresent(ArgumentMatchers.any(TEEResultBean.class)))
			.thenReturn(false);
		
		Map<String, Boolean> scenario3 = iaTestService.removingBenefitOfBoubt(VALID_TEST_ID, TEST_QUESTION_ID, USER_NAME);
		logger.info("Remove BoD scenario3: {}", scenario3);
		
		verify(testDAO).getTestTypeEndDateDurationByTestId(VALID_TEST_ID);
		verify(testDAO).updateBenefitOfDoubt(VALID_TEST_ID, TEST_QUESTION_ID, BOD_ACTIVE_FALSE, USER_NAME);
		verify(testDAO).checkTestResultsLive(VALID_TEST_ID);
		
		verify(testDAO).getStudentTestAttemptQuestions(VALID_TEST_ID);
		verifyTEEMarksProcessed();
		verify(testDAO, atMost(2)).checkIfTEEMarksPresent(ArgumentMatchers.any(TEEResultBean.class));
		
		assertThat(scenario3.size(), is(2));
		assertThat(scenario3, IsMapContaining.hasKey("benefitOfDoubt"));
		assertThat(scenario3, IsMapContaining.hasKey("resultsLive"));
		assertThat(scenario3, not(IsMapContaining.hasKey("bodRemoved")));
		assertThat(scenario3.get("benefitOfDoubt"), is(true));
		assertThat(scenario3.get("resultsLive"), is(false));
	}
	
	/**
	 * Unit test for removingBenefitOfBoubt() method of IATestService class.
	 */
	@Test
	public void testRemovingBenefitOfBoubt4() {
		//Scenario 4: removing BoD of a test which is not yet completed and re-run results
		TestExamBean testBean = new TestExamBean();
		testBean.setEndDate("2030-05-04 11:00:00");
		testBean.setDuration(600);
		testBean.setTestType(TYPE_PROJECT);
		
		when(testDAO.getTestTypeEndDateDurationByTestId(VALID_TEST_ID))
			.thenReturn(testBean);
		
		logger.info("Remove BoD scenario4 Illegal Argument Exception");
		exceptionRule.expect(IllegalArgumentException.class);
	    exceptionRule.expectMessage("Cannot remove BoD as test is not completed for testId: " + VALID_TEST_ID);
		iaTestService.removingBenefitOfBoubt(VALID_TEST_ID, TEST_QUESTION_ID, USER_NAME);
	}
	
	/**
	 * Unit test for removingBenefitOfBoubt() method of IATestService class.
	 */
	@Test
	public void testRemovingBenefitOfBoubt5() {
		//Scenario 5: removing BoD of a question and re-run results for student whose TEE processed cannot be updated
		TestExamBean testBean = new TestExamBean();
		testBean.setEndDate("2023-05-04 18:00:00");
		testBean.setDuration(45);
		testBean.setTestType(TYPE_TEST);
		
		when(testDAO.getTestTypeEndDateDurationByTestId(VALID_TEST_ID))
			.thenReturn(testBean);
		when(testDAO.updateBenefitOfDoubt(VALID_TEST_ID, TEST_QUESTION_ID, BOD_ACTIVE_TRUE, USER_NAME))
			.thenReturn(1);
		when(testDAO.checkTestResultsLive(VALID_TEST_ID))
			.thenReturn(1);
		testAttemptedStudentScore();
		
		teeMarksProcessed();
		when(testDAO.checkIfTEEMarksPresent(ArgumentMatchers.any(TEEResultBean.class)))
			.thenReturn(true);
		when(testDAO.updateProcessedFlagInTEEMarks(ArgumentMatchers.any(TEEResultBean.class), ArgumentMatchers.eq(USER_NAME)))
			.thenReturn(false);
		
		logger.info("Remove BoD scenario5 Illegal Argument Exception");
		exceptionRule.expect(IllegalArgumentException.class);
	    exceptionRule.expectMessage("Error in updating processed Flag.");
		iaTestService.removingBenefitOfBoubt(VALID_TEST_ID, TEST_QUESTION_ID, USER_NAME);
	}
	
	/**
	 * Unit test for questionAttemptDataMap() method of IATestService class.
	 */
	@Test
	public void testQuestionAttemptDataMap1() {
		//Scenario 1: question attempts data for question type single-select
		when(testDAO.getStudentAttemptCountByTestId(VALID_TEST_ID))
			.thenReturn(3);
		
		testAttemptedStudents();
		studentAnswers();
		
		when(testDAO.getCorrectOptionsByQuestionId(TEST_QUESTION_ID))
			.thenReturn(Arrays.asList("96779"));
		
		Map<String, Integer> scenario1 = iaTestService.questionAttemptDataMap(VALID_TEST_ID, TEST_QUESTION_ID, 1, 1);
		logger.info("Question attempts scenario1: {}", scenario1);
		
		verify(testDAO).getStudentAttemptCountByTestId(VALID_TEST_ID);
		verify(testDAO).getStudentTestAttemptQuestions(VALID_TEST_ID);
		verify(testDAO).getAnswerAttemptsByTestIdQuestionId(VALID_TEST_ID, TEST_QUESTION_ID);
		verify(testDAO).getCorrectOptionsByQuestionId(TEST_QUESTION_ID);
		
		assertThat(scenario1.size(), is(5));
		assertThat(scenario1, IsMapContaining.hasKey("right-selection"));
		assertThat(scenario1, IsMapContaining.hasKey("test-attempted"));
		assertThat(scenario1, not(IsMapContaining.hasKey("question-attempted")));
		assertThat(scenario1.get("right-selection"), is(2));
		assertThat(scenario1.get("wrong-selection"), is(0));
		assertThat(scenario1.get("test-attempted"), is(3));
	}
	
	/**
	 * Unit test for questionAttemptDataMap() method of IATestService class.
	 */
	@Test
	public void testQuestionAttemptDataMap2() {
		//Scenario 2: question attempts data for question type descriptive and max attempts 2
		when(testDAO.getStudentAttemptCountByTestId(VALID_TEST_ID))
			.thenReturn(3);
		
		testAttemptedStudents();
		studentAnswers();
		
		Map<String, Integer> scenario2 = iaTestService.questionAttemptDataMap(VALID_TEST_ID, TEST_QUESTION_ID, 4, 2);
		logger.info("Question attempts scenario1: {}", scenario2);
		
		verify(testDAO).getStudentAttemptCountByTestId(VALID_TEST_ID);
		verify(testDAO).getStudentTestAttemptQuestions(VALID_TEST_ID);
		verify(testDAO).getAnswerAttemptsByTestIdQuestionId(VALID_TEST_ID, TEST_QUESTION_ID);
		
		assertThat(scenario2.size(), is(4));
		assertThat(scenario2, IsMapContaining.hasKey("question-attempted"));
		assertThat(scenario2, IsMapContaining.hasKey("applicable-students"));
		assertThat(scenario2, not(IsMapContaining.hasKey("right-selection")));
		assertThat(scenario2.get("not-attempted"), is(0));
		assertThat(scenario2.get("applicable-students"), is(2));
	}
	
	/**
	 * Unit test for questionDetails() method of IATestService class.
	 */
	@Test
	public void testQuestionDetails1() {
		//Scenario 1: question details of multi-select question type
		when(testDAO.getTestNameById(VALID_TEST_ID))
			.thenReturn(TEST_NAME);
		
		questionOptionsData();
		studentAnswers();
		
		List<StudentQuestionResponseExamBean> scenario1 = iaTestService.questionDetails(VALID_TEST_ID, TEST_QUESTION_ID, 2);
		logger.info("Question details scenario1: {}", scenario1);
		
		verify(testDAO).getTestNameById(VALID_TEST_ID);
		verify(testDAO).getOptionDataByQuestionId(TEST_QUESTION_ID);
		verify(testDAO).getAnswerAttemptsByTestIdQuestionId(VALID_TEST_ID, TEST_QUESTION_ID);
		
		assertThat(scenario1.size(), is(2));
		assertEquals(TEST_NAME, scenario1.get(0).getTestName());
		assertEquals("Option 1", scenario1.get(1).getAnswer());
	}
	
	/**
	 * Unit test for questionDetails() method of IATestService class.
	 */
	@Test
	public void testQuestionDetails2() {
		//Scenario 2: question details of link question type 
		when(testDAO.getTestNameById(VALID_TEST_ID))
			.thenReturn(TEST_NAME);
		
		studentAnswers();
		
		List<StudentQuestionResponseExamBean> scenario2 = iaTestService.questionDetails(VALID_TEST_ID, TEST_QUESTION_ID, 7);
		logger.info("Question details scenario2: {}", scenario2);
		
		verify(testDAO).getTestNameById(VALID_TEST_ID);
		verify(testDAO).getAnswerAttemptDataByTestIdQuestionId(VALID_TEST_ID, TEST_QUESTION_ID);
		
		assertThat(scenario2.size(), is(2));
		assertEquals(TEST_NAME, scenario2.get(0).getTestName());
		assertEquals(TEST_FACULTY_ID, scenario2.get(1).getFacultyId());
	}
	
	/**
	 * Unit test for optionSelectedDataMap() method of IATestService class.
	 */
	@Test
	public void testOptionSelectedDataMap1() {
		//Scenario 1: option details of true-false question
		questionOptionsData();
		
		when(testDAO.getOptionSelectedCount(VALID_TEST_ID, TEST_QUESTION_ID, "96779"))
			.thenReturn(2);
		when(testDAO.getOptionSelectedCount(VALID_TEST_ID, TEST_QUESTION_ID, "96780"))
			.thenReturn(4);
		when(testDAO.getOptionSelectedCount(VALID_TEST_ID, TEST_QUESTION_ID, "96781"))
			.thenReturn(1);
		when(testDAO.getOptionSelectedCount(VALID_TEST_ID, TEST_QUESTION_ID, "96782"))
			.thenReturn(0);
		
		Map<String, Integer> scenario1 = iaTestService.optionSelectedDataMap(VALID_TEST_ID, TEST_QUESTION_ID, 5);
		logger.info("Options details scenario1: {}", scenario1);
		
		verify(testDAO).getOptionDataByQuestionId(TEST_QUESTION_ID);
		verify(testDAO).getOptionSelectedCount(VALID_TEST_ID, TEST_QUESTION_ID, "96779");
		verify(testDAO).getOptionSelectedCount(VALID_TEST_ID, TEST_QUESTION_ID, "96780");
		verify(testDAO).getOptionSelectedCount(VALID_TEST_ID, TEST_QUESTION_ID, "96781");
		verify(testDAO).getOptionSelectedCount(VALID_TEST_ID, TEST_QUESTION_ID, "96782");
		
		assertThat(scenario1.size(), is(4));
		assertThat(scenario1, IsMapContaining.hasKey("No. of students who selected Option: Option 1"));
		assertThat(scenario1, IsMapContaining.hasKey("No. of students who selected Option: Option 4"));
		assertThat(scenario1, not(IsMapContaining.hasKey("No. of students who selected Option: Option 5")));
		assertThat(scenario1.get("No. of students who selected Option: Option 1"), is(2));
		assertThat(scenario1.get("No. of students who selected Option: Option 3"), is(1));
	}
	
	/**
	 * Unit test for optionSelectedDataMap() method of IATestService class.
	 */
	@Test
	public void testOptionSelectedDataMap2() {
		//Scenario 2: option details of descriptive question
		Map<String, Integer> scenario2 = iaTestService.optionSelectedDataMap(VALID_TEST_ID, TEST_QUESTION_ID, 4);
		logger.info("Options details scenario2: {}", scenario2);
		
		assertThat(scenario2.size(), is(0));
		assertThat(scenario2, not(IsMapContaining.hasKey("No. of students who selected Option: Option 1")));
	}
	
	/**
	 * Unit test for unmarkTEEProcessed() method of IATestService class.
	 */
	@Test
	public void testUnmarkTEEProcessed1() {
		//Scenario 1: TEE marks exists for student and mark TEE processed flag as N
		TEEResultBean resultBean = new TEEResultBean();
		resultBean.setTimebound_id("1572");
		resultBean.setSapid("77422961632");
		
		when(testDAO.getStudentsTimeboundIdFromTest(VALID_TEST_ID, "77422961632"))
			.thenReturn(resultBean);
		when(testDAO.checkIfTEEMarksPresent(ArgumentMatchers.any(TEEResultBean.class)))
			.thenReturn(true);
		when(testDAO.updateProcessedFlagInTEEMarks(ArgumentMatchers.any(TEEResultBean.class), ArgumentMatchers.eq(USER_NAME)))
			.thenReturn(true);
		
		ResponseBean scenario1 = iaTestService.unmarkTEEProcessed(VALID_TEST_ID, "77422961632", USER_NAME);
		logger.info("Unmark TEE processed scenario1: {}", scenario1);
		verify(testDAO).getStudentsTimeboundIdFromTest(VALID_TEST_ID, "77422961632");
		verify(testDAO).checkIfTEEMarksPresent(ArgumentMatchers.any(TEEResultBean.class));
		verify(testDAO).updateProcessedFlagInTEEMarks(ArgumentMatchers.any(TEEResultBean.class), ArgumentMatchers.eq(USER_NAME));
		
		assertEquals("success", scenario1.getStatus());
	}
	
	/**
	 * Unit test for unmarkTEEProcessed() method of IATestService class.
	 */
	@Test
	public void testUnmarkTEEProcessed2() {
		//Scenario 2: TEE marks does not exist for student
		TEEResultBean resultBean = new TEEResultBean();
		resultBean.setTimebound_id("1572");
		resultBean.setSapid("77422656750");
		
		when(testDAO.getStudentsTimeboundIdFromTest(VALID_TEST_ID, "77422656750"))
			.thenReturn(resultBean);
		when(testDAO.checkIfTEEMarksPresent(ArgumentMatchers.any(TEEResultBean.class)))
			.thenReturn(false);
		
		ResponseBean scenario2 = iaTestService.unmarkTEEProcessed(VALID_TEST_ID, "77422656750", USER_NAME);
		logger.info("Unmark TEE processed scenario1: {}", scenario2);
		verify(testDAO).getStudentsTimeboundIdFromTest(VALID_TEST_ID, "77422656750");
		verify(testDAO).checkIfTEEMarksPresent(ArgumentMatchers.any(TEEResultBean.class));
		
		assertEquals("success", scenario2.getStatus());
	}
	
	/**
	 * Unit test for unmarkTEEProcessed() method of IATestService class.
	 */
	@Test
	public void testUnmarkTEEProcessed3() {
		//Scenario 3: student timeboundId returned as empty String
		TEEResultBean resultBean = new TEEResultBean();
		resultBean.setTimebound_id("");
		resultBean.setSapid("77422656750");
		
		when(testDAO.getStudentsTimeboundIdFromTest(VALID_TEST_ID, "77422656750"))
			.thenReturn(resultBean);
		
		ResponseBean scenario3 = iaTestService.unmarkTEEProcessed(VALID_TEST_ID, "77422656750", USER_NAME);
		logger.info("Unmark TEE processed scenario3: {}", scenario3);
		verify(testDAO).getStudentsTimeboundIdFromTest(VALID_TEST_ID, "77422656750");
		
		assertEquals("error", scenario3.getStatus());
		assertEquals("Error in getting timeboundID.", scenario3.getMessage());
	}
	
	/**
	 * Unit test for unmarkTEEProcessed() method of IATestService class.
	 */
	@Test
	public void testUnmarkTEEProcessed4() {
		//Scenario 4: student sapid returned as empty String
		TEEResultBean resultBean = new TEEResultBean();
		resultBean.setTimebound_id("1572");
		resultBean.setSapid("");
		
		when(testDAO.getStudentsTimeboundIdFromTest(VALID_TEST_ID, "77422656750"))
			.thenReturn(resultBean);
		
		ResponseBean scenario4 = iaTestService.unmarkTEEProcessed(VALID_TEST_ID, "77422656750", USER_NAME);
		logger.info("Unmark TEE processed scenario3: {}", scenario4);
		verify(testDAO).getStudentsTimeboundIdFromTest(VALID_TEST_ID, "77422656750");
		
		assertEquals("error", scenario4.getStatus());
		assertEquals("Error in getting timeboundID.", scenario4.getMessage());
	}
	
	/**
	 * Unit test for unmarkTEEProcessed() method of IATestService class.
	 */
	@Test
	public void testUnmarkTEEProcessed5() {
		//Scenario 5: TEE marks exists for student and unable to mark TEE processed flag as N
		TEEResultBean resultBean = new TEEResultBean();
		resultBean.setTimebound_id("1572");
		resultBean.setSapid("77422961632");
		
		when(testDAO.getStudentsTimeboundIdFromTest(VALID_TEST_ID, "77422961632"))
			.thenReturn(resultBean);
		when(testDAO.checkIfTEEMarksPresent(ArgumentMatchers.any(TEEResultBean.class)))
			.thenReturn(true);
		when(testDAO.updateProcessedFlagInTEEMarks(ArgumentMatchers.any(TEEResultBean.class), ArgumentMatchers.eq(USER_NAME)))
			.thenReturn(false);
		
		ResponseBean scenario5 = iaTestService.unmarkTEEProcessed(VALID_TEST_ID, "77422961632", USER_NAME);
		logger.info("Unmark TEE processed scenario4: {}", scenario5);
		verify(testDAO).getStudentsTimeboundIdFromTest(VALID_TEST_ID, "77422961632");
		verify(testDAO).checkIfTEEMarksPresent(ArgumentMatchers.any(TEEResultBean.class));
		verify(testDAO).updateProcessedFlagInTEEMarks(ArgumentMatchers.any(TEEResultBean.class), ArgumentMatchers.eq(USER_NAME));
		
		assertEquals("error", scenario5.getStatus());
		assertEquals("Error in updating processed Flag.", scenario5.getMessage());
	}
	
	/**
	 * Test questions list and question types mock DAO calls.
	 */
	private void getTestQuestions() {
		TestQuestionExamBean question1 = new TestQuestionExamBean();
		question1.setId(41169L);
		question1.setTestId(VALID_TEST_ID);
		question1.setMarks(1.00);
		question1.setType(1);
		question1.setQuestion("Question 1");
		question1.setSectionId(255);
		
		TestQuestionExamBean question2 = new TestQuestionExamBean();
		question2.setId(41181L);
		question2.setTestId(VALID_TEST_ID);
		question2.setMarks(4.00);
		question2.setType(2);
		question2.setQuestion("Question 2");
		question2.setSectionId(255);
		
		TestQuestionExamBean question3 = new TestQuestionExamBean();
		question3.setId(41187L);
		question3.setTestId(VALID_TEST_ID);
		question3.setMarks(6.00);
		question3.setType(1);
		question3.setQuestion("Question 3");
		question3.setSectionId(256);
		
		HashMap<Integer, String> questionTypeMap = new HashMap<>();
		questionTypeMap.put(1, "SINGLESELECT");
		questionTypeMap.put(2, "MULTISELECT");
		questionTypeMap.put(4, "DESCRIPTIVE");
		questionTypeMap.put(5, "TRUEFALSE");
		questionTypeMap.put(8, "Link");
		
		when(testDAO.getQuestionsByTestId(VALID_TEST_ID))
			.thenReturn(Arrays.asList(question1, question2, question3));
		when(testDAO.getTestQuestionTypeMap())
			.thenReturn(questionTypeMap);
	}
	
	/**
	 * Verify section questions DAO calls.
	 */
	private void verifySectionQuestions() {
		verify(testDAO).getQuestionsByTestId(VALID_TEST_ID);
		verify(testDAO).getTestQuestionTypeMap();
		verify(testDAO).getSectionIdNameMapByTestId(VALID_TEST_ID);
	}
	
	/**
	 * Test attempted student details DAO calls.
	 */
	private void testAttemptedStudents() {
		StudentsTestDetailsExamBean testDetailsBean1 = new StudentsTestDetailsExamBean();
		testDetailsBean1.setId(474783L);
		testDetailsBean1.setSapid("77422961632");
		testDetailsBean1.setAttempt(1);
		testDetailsBean1.setTestQuestions("41169, 41177, 41183");
		
		StudentsTestDetailsExamBean testDetailsBean2 = new StudentsTestDetailsExamBean();
		testDetailsBean2.setId(474965L);
		testDetailsBean2.setSapid("77221871302");
		testDetailsBean2.setAttempt(1);
		testDetailsBean2.setTestQuestions("41172, 41178, 41183");
		
		StudentsTestDetailsExamBean testDetailsBean3 = new StudentsTestDetailsExamBean();
		testDetailsBean3.setId(474971L);
		testDetailsBean3.setSapid("77422656750");
		testDetailsBean3.setAttempt(1);
		testDetailsBean3.setTestQuestions("41169, 41178, 41187");
		
		when(testDAO.getStudentTestAttemptQuestions(VALID_TEST_ID))
			.thenReturn(Arrays.asList(testDetailsBean1, testDetailsBean2, testDetailsBean3));
	}
	
	/**
	 * Getting test attempted students, calculating and updating test scores DAO calls.
	 */
	private void testAttemptedStudentScore() {
		testAttemptedStudents();
		
		when(testDAO.caluclateTestScore("77422961632", VALID_TEST_ID))
			.thenReturn(8.00);
		when(testDAO.caluclateTestScore("77422656750", VALID_TEST_ID))
			.thenReturn(18.00);
		
		when(testDAO.updateStudentTestScore(474783L, 8.00, USER_NAME))
			.thenReturn(1);
		when(testDAO.updateStudentTestScore(474971L, 18.00, USER_NAME))
			.thenReturn(1);
	}
	
	private void teeMarksProcessed() {
		TEEResultBean resultBean1 = new TEEResultBean();
		resultBean1.setTimebound_id("1572");
		resultBean1.setSapid("77422961632");
		
		TEEResultBean resultBean2 = new TEEResultBean();
		resultBean2.setTimebound_id("1572");
		resultBean2.setSapid("77422656750");
		
		when(testDAO.getStudentsTimeboundIdFromTest(VALID_TEST_ID, "77422961632"))
			.thenReturn(resultBean1);
		when(testDAO.getStudentsTimeboundIdFromTest(VALID_TEST_ID, "77422656750"))
			.thenReturn(resultBean2);
	}
	
	private void verifyTEEMarksProcessed() {
		verify(testDAO).getStudentsTimeboundIdFromTest(VALID_TEST_ID, "77422961632");
		verify(testDAO).getStudentsTimeboundIdFromTest(VALID_TEST_ID, "77422656750");
	}
	
	/**
	 * Verifying test attempted students, calculating and updating test scores DAO calls.
	 */
	private void verifyStudentsAttempted() {
		verify(testDAO).getStudentTestAttemptQuestions(VALID_TEST_ID);
		verify(testDAO).caluclateTestScore("77422961632", VALID_TEST_ID);
		verify(testDAO).caluclateTestScore("77422656750", VALID_TEST_ID);
		verify(testDAO).updateStudentTestScore(474783L, 8.00, USER_NAME);
		verify(testDAO).updateStudentTestScore(474971L, 18.00, USER_NAME);
	}
	
	/**
	 * Student test answers DAO calls.
	 */
	private void studentAnswers() {
		StudentQuestionResponseExamBean studentAnswer1 = new StudentQuestionResponseExamBean();
		studentAnswer1.setSapid("77422961632");
		studentAnswer1.setAttempt(1);
		studentAnswer1.setAnswer("96779");
		studentAnswer1.setMarks(4.00);
		studentAnswer1.setFacultyId(TEST_FACULTY_ID);
		studentAnswer1.setIsChecked(1);
		
		StudentQuestionResponseExamBean studentAnswer2 = new StudentQuestionResponseExamBean();
		studentAnswer2.setSapid("77422656750");
		studentAnswer2.setAttempt(1);
		studentAnswer2.setAnswer("96779");
		studentAnswer2.setMarks(2.00);
		studentAnswer2.setFacultyId(TEST_FACULTY_ID);
		studentAnswer2.setIsChecked(1);
		
		when(testDAO.getAnswerAttemptsByTestIdQuestionId(VALID_TEST_ID, TEST_QUESTION_ID))
			.thenReturn(Arrays.asList(studentAnswer1, studentAnswer2));
		
		when(testDAO.getAnswerAttemptDataByTestIdQuestionId(VALID_TEST_ID, TEST_QUESTION_ID))
			.thenReturn(Arrays.asList(studentAnswer1, studentAnswer2));
	}
	
	/**
	 * question options data DAO calls.
	 */
	private void questionOptionsData() {
		Map<Integer, String> optionsMap = new HashMap<>();
		optionsMap.put(96779, "Option 1");
		optionsMap.put(96780, "Option 2");
		optionsMap.put(96781, "Option 3");
		optionsMap.put(96782, "Option 4");
		
		when(testDAO.getOptionDataByQuestionId(TEST_QUESTION_ID))
			.thenReturn(optionsMap);
	}
}
