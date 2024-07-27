package com.nmims.test.services;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.atMost;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
import org.springframework.dao.DataAccessException;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;

import com.nmims.beans.MBAExamBookingRequest;
import com.nmims.beans.MBALiveSettings;
import com.nmims.beans.MBAPaymentRequest;
import com.nmims.beans.NoSlotBookingBean;
import com.nmims.beans.StudentExamBean;
import com.nmims.beans.StudentSubjectConfigExamBean;
import com.nmims.daos.DashboardDAO;
import com.nmims.daos.MBAWXLiveSettingsDAO;
import com.nmims.daos.MBAWXPaymentsDao;
import com.nmims.daos.NoSlotBookingDAOInterface;
import com.nmims.daos.StudentDAO;
import com.nmims.daos.StudentTestDAO;
import com.nmims.helpers.MBAPaymentHelper;
import com.nmims.services.NoSlotBookingServiceInterface;

@RunWith(SpringRunner.class)
@SpringBootTest
//@TestPropertySource("file:C:/NMIMS_PROPERTY_FILE/application.properties")
public class NoSlotBookingServiceTest {
	@Autowired
	private NoSlotBookingServiceInterface noSlotBookingService;
	
	@MockBean
	private DashboardDAO dashboardDAO;
	
	@MockBean
	private MBAWXLiveSettingsDAO mbawxLiveSettingsDAO;
	
	@MockBean
	private MBAWXPaymentsDao mbaWxPaymentsDao;
	
	@MockBean
	private NoSlotBookingDAOInterface noSlotBookingDAO;
	
	@MockBean
	private StudentDAO studentDAO;
	
	@MockBean
	private StudentTestDAO studentTestDAO;
	
	@Rule
	public ExpectedException exceptionRule = ExpectedException.none();
	
	private static final long TIMEBOUND_ID_1491 = 1491L;
	private static final String SAPID_77221111222 = "77221111222";
	
	private static final int CPS_148 = 148;
	private static final String SUBJECT_DMS = "Digital Marketing Strategy";
	
	private static final String ACAD_YEAR_2023 = "2023";
	private static final String ACAD_MONTH_JAN = "Jan";
	private static final String EXAM_YEAR_2023 = "2023";
	private static final String EXAM_MONTH_APR = "Apr";
	private static final String PSS_ID_2268 = "2268";
	
	private static final String TYPE_PROJECT_REGISTRATION = "Project Registration";
	private static final String TYPE_PROJECT_RE_REGISTRATION = "Project Re-Registration";
	
	private static final String TRAN_STATUS_EXPIRED = "Expired";
	private static final String TRAN_STATUS_INITIATED = "Initiated";
	private static final String TRAN_STATUS_MANUALLY_APPROVED = "Online Payment Manually Approved";
	private static final String TRAN_STATUS_PAYMENT_SUCCESSFUL = "Payment Successful";
	
	private static final String PROJECT_REGISTRATION_CHARGES = "600";
	private static final String PROJECT_RE_REGISTRATION_CHARGES = "600";
	
	private static final String PAYMENT_OPTION_PAYTM = "paytm";
	
	private static final String BOOKINGS_STATUS_TRUE = "Y";
	private static final String BOOKINGS_STATUS_FALSE = "N";
	
	private static final String PAYMENT_SOURCE_WEBAPP = "WebApp";
	private static final String PAYMENT_SOURCE_MOBILE = "Mobile";
	
	private static final String MODIFIED_BY_AUTO_BOOKING_SCHEDULER = "Auto Booking Scheduler";
	
	private static final Logger logger = LoggerFactory.getLogger(NoSlotBookingServiceTest.class);
	
	/**
	 * Unit test for studentProjectRegistrationEligibility() method of NoSlotBookingServiceInterface class.
	 */
	@Test
	public void testProjectRegistrationEligibility1() {
		//Scenario 1: Project Registration active
		Map<String, Object> scenario1 = projectRegistrationEligibilityCheck(0, true, false);
		logger.info("Eligible check scenario1 responseMap: {}", scenario1);
		assertTrue((boolean) scenario1.get("isEligible"));
		assertFalse((boolean) scenario1.get("isPaid"));
		assertEquals(scenario1.get("type"), TYPE_PROJECT_REGISTRATION);
	}
	
	/**
	 * Unit test for studentProjectRegistrationEligibility() method of NoSlotBookingServiceInterface class.
	 */
	@Test
	public void testProjectRegistrationEligibility2() {
		//Scenario 2: Project Registration and Re-Registration both active and paid for registration
		Map<String, Object> scenario2 = projectRegistrationEligibilityCheck(1, true, true);
		logger.info("Eligible check scenario2 responseMap: {}", scenario2);
		assertTrue((boolean) scenario2.get("isEligible"));
		assertTrue((boolean) scenario2.get("isPaid"));
		assertEquals(scenario2.get("type"), TYPE_PROJECT_REGISTRATION);
	}
	
	/**
	 * Unit test for studentProjectRegistrationEligibility() method of NoSlotBookingServiceInterface class.
	 */
	@Test
	public void testProjectRegistrationEligibility3() {
		//Scenario 3: Project Re-Registration active
		Map<String, Object> scenario3 = projectRegistrationEligibilityCheck(0, false, true);
		logger.info("Eligible check scenario3 responseMap: {}", scenario3);
		assertTrue((boolean) scenario3.get("isEligible"));
		assertFalse((boolean) scenario3.get("isPaid"));
		assertEquals(scenario3.get("type"), TYPE_PROJECT_RE_REGISTRATION);
	}
	
	/**
	 * Unit test for studentProjectRegistrationEligibility() method of NoSlotBookingServiceInterface class.
	 */
	@Test
	public void testProjectRegistrationEligibility4() {
		//Scenario 4: Project Registration and Re-Registration both inactive 
		Map<String, Object> scenario4 = projectRegistrationEligibilityCheck(0, false, false);
		logger.info("Eligible check scenario4 responseMap: {}", scenario4);
		assertFalse((boolean) scenario4.get("isEligible"));
	}
	
	/**
	 * Unit test for projectRegistrationDetails() method of NoSlotBookingServiceInterface class.
	 */
	@Test
	public void testProjectRegistrationDetails1() {
		//Scenario 1: Project Registration live and not booked by student
		Map<String, Object> scenario1 = projectRegistrationDetails(false, true);
		logger.info("Project reg scenario1 responseMap: {}", scenario1);
		assertTrue((boolean) scenario1.get("live"));
		assertFalse((boolean) scenario1.get("booked"));
	}
	
	/**
	 * Unit test for projectRegistrationDetails() method of NoSlotBookingServiceInterface class.
	 */
	@Test
	public void testProjectRegistrationDetails2() {
		//Scenario 2: Project Registration live and booked by student
		Map<String, Object> scenario2 = projectRegistrationDetails(true, true);
		logger.info("Project reg scenario2 responseMap: {}", scenario2);
		assertTrue((boolean) scenario2.get("live"));
		assertTrue((boolean) scenario2.get("booked"));
	}
	
	/**
	 * Unit test (expected exception) for projectRegistrationDetails() method of NoSlotBookingServiceInterface class.
	 */
	@Test
	public void testProjectRegistrationDetails3() {
		//Scenario 3: Project Registration not live and not booked by the student
		logger.info("Project reg scenario3 Illegal Argument Exception thrown");
		exceptionRule.expect(IllegalArgumentException.class);
	    exceptionRule.expectMessage("Project Registration not live for student " + SAPID_77221111222);
	    projectRegistrationDetails(false, false);
	}
	
	/**
	 * Unit test (expected exception) for projectRegistrationDetails() method of NoSlotBookingServiceInterface class.
	 */
	@Test
	public void testProjectRegistrationDetails4() {
		//Scenario 4: Project Registration not live and booked by student
		logger.info("Project reg scenario4 Illegal Argument Exception");
		exceptionRule.expect(IllegalArgumentException.class);
	    exceptionRule.expectMessage("Project Registration not live for student " + SAPID_77221111222);
		projectRegistrationDetails(true, false);
	}
	
	/**
	 * Unit test for projectReRegistrationDetails() method of NoSlotBookingServiceInterface class.
	 */
	@Test
	public void testProjectReRegistrationDetails1() {
		//Scenario 1: Project Re-Registration live and not booked by student
		Map<String, Object> scenario1 = projectReRegistrationDetails(false, true);
		logger.info("Project reReg scenario1 responseMap: {}", scenario1);
		assertTrue((boolean) scenario1.get("live"));
		assertFalse((boolean) scenario1.get("booked"));
	}
	
	/**
	 * Unit test for projectReRegistrationDetails() method of NoSlotBookingServiceInterface class.
	 */
	@Test
	public void testProjectReRegistrationDetails2() {
		//Scenario 2: Project Re-Registration live and booked by student
		Map<String, Object> scenario2 = projectReRegistrationDetails(true, true);
		logger.info("Project reReg scenario2 responseMap: {}", scenario2);
		assertTrue((boolean) scenario2.get("live"));
		assertTrue((boolean) scenario2.get("booked"));
	}
	
	/**
	 * Unit test (expected exception) for projectReRegistrationDetails() method of NoSlotBookingServiceInterface class.
	 */
	@Test
	public void testProjectReRegistrationDetails3() {
		//Scenario 3: Project Re-Registration not live
		logger.info("Project reReg scenario3 Illegal Argument Exception thrown");
		exceptionRule.expect(IllegalArgumentException.class);
	    exceptionRule.expectMessage("Project Re-Registration not live for student " + SAPID_77221111222);
	    projectReRegistrationDetails(false, false);
	}
	
	/**
	 * Unit test (expected exception) for projectReRegistrationDetails() method of NoSlotBookingServiceInterface class.
	 */
	@Test
	public void testProjectReRegistrationDetails4() {
		//Scenario 4: Project Registration not live and booked by student
		logger.info("Project reReg scenario4 Illegal Argument Exception");
		exceptionRule.expect(IllegalArgumentException.class);
	    exceptionRule.expectMessage("Project Re-Registration not live for student " + SAPID_77221111222);
		projectReRegistrationDetails(true, false);
	}
	
	/**
	 * Unit test for saveNoSlotBooking() method of NoSlotBookingServiceInterface class.
	 */
	@Test
	public void testSaveNoSlotBooking1() {
		//Scenario 1: Project Registration payment initiated using Web device
		String scenario1 = insertRegPaymentWeb(PROJECT_REGISTRATION_CHARGES);
		logger.info("Reg booking scenario1 trackId: {}", scenario1);
		assertThat(scenario1).isNotBlank();
	}
	
	/**
	 * Unit test for saveNoSlotBooking() method of NoSlotBookingServiceInterface class.
	 */
	@Test
	public void testSaveNoSlotBooking2() {
		//Scenario 2: Project Registration payment initiated using Mobile device
		String scenario2 = insertRegPaymentMobile(PROJECT_REGISTRATION_CHARGES);
		logger.info("Reg booking scenario2 trackId: {}", scenario2);
		assertThat(scenario2).isNotBlank();
	}
	
	/**
	 * Unit test for saveNoSlotBooking() method of NoSlotBookingServiceInterface class.
	 */
	@Test
	public void testSaveNoSlotBooking3() {
		//Scenario 3: Project Re-Registration payment initiated using Web device
		String scenario3 = insertReRegPaymentWeb(PROJECT_RE_REGISTRATION_CHARGES);
		logger.info("ReReg booking scenario3 trackId: {}", scenario3);
		assertThat(scenario3).isNotBlank();
	}
	
	/**
	 * Unit test for saveNoSlotBooking() method of NoSlotBookingServiceInterface class.
	 */
	@Test
	public void testSaveNoSlotBooking4() {
		//Scenario 4: Project Re-Registration payment initiated using Mobile device
		String scenario4 = insertReRegPaymentMobile(PROJECT_RE_REGISTRATION_CHARGES);
		logger.info("ReReg booking scenario4 trackId: {}", scenario4);
		assertThat(scenario4).isNotBlank();
	}
	
	/**
	 * Unit test (expected exception) for saveNoSlotBooking() method of NoSlotBookingServiceInterface class.
	 */
	@Test
	public void testSaveNoSlotBooking5() {
		//Scenario 5: Project Registration payment initiated using invalid amount
		logger.info("Reg booking scenario5 Illegal Argument Exception");
		exceptionRule.expect(IllegalArgumentException.class);
	    exceptionRule.expectMessage("Provided amount improper!");
	    insertRegPaymentWeb("7500");
	}
	
	/**
	 * Unit test (expected exception) for saveNoSlotBooking() method of NoSlotBookingServiceInterface class.
	 */
	@Test
	public void testSaveNoSlotBooking6() {
		//Scenario 6: Project Re-Registration payment initiated using invalid amount
		logger.info("ReReg booking scenario6 Illegal Argument Exception");
		exceptionRule.expect(IllegalArgumentException.class);
	    exceptionRule.expectMessage("Provided amount improper!");
	    insertReRegPaymentWeb("75");
	}
	
	/**
	 * Unit test for noSlotBookingPaymentDetails() method of NoSlotBookingServiceInterface class.
	 */
	@Test
	public void testNoSlotBookingPaymentDetails1() {
		//Scenario 1: Project Registration payment details for Web device
		MBAExamBookingRequest scenario1 = regBookingPaymentDetails(true, true);
		logger.info("Reg payment initiated scenario1 secureHash: {}", scenario1.getSecureHash());
		assertEquals(scenario1.getFormParameters().get("CUST_ID"), SAPID_77221111222);
		assertEquals(scenario1.getFormParameters().get("TXN_AMOUNT"), PROJECT_REGISTRATION_CHARGES);
	}
	
	/**
	 * Unit test for noSlotBookingPaymentDetails() method of NoSlotBookingServiceInterface class.
	 */
	@Test
	public void testNoSlotBookingPaymentDetails2() {
		//Scenario 2: Project Registration payment details for Mobile device
		MBAExamBookingRequest scenario2 = regBookingPaymentDetails(false, true);
		logger.info("Reg payment initiated scenario2 secureHash: {}", scenario2.getSecureHash());
		assertEquals(scenario2.getFormParameters().get("CUST_ID"), SAPID_77221111222);
		assertEquals(scenario2.getFormParameters().get("TXN_AMOUNT"), PROJECT_REGISTRATION_CHARGES);
	}
	
	/**
	 * Unit test for noSlotBookingPaymentDetails() method of NoSlotBookingServiceInterface class.
	 */
	@Test
	public void testNoSlotBookingPaymentDetails3() {
		//Scenario 3: Project Re-Registration payment details for Web device
		MBAExamBookingRequest scenario3 = reRegBookingPaymentDetails(true, true);
		logger.info("ReReg payment initiated scenario3 secureHash: {}", scenario3.getSecureHash());
		assertEquals(scenario3.getFormParameters().get("CUST_ID"), SAPID_77221111222);
		assertEquals(scenario3.getFormParameters().get("TXN_AMOUNT"), PROJECT_RE_REGISTRATION_CHARGES);
	}
	
	/**
	 * Unit test for noSlotBookingPaymentDetails() method of NoSlotBookingServiceInterface class.
	 */
	@Test
	public void testNoSlotBookingPaymentDetails4() {
		//Scenario 4: Project Re-Registration payment details for Mobile device
		MBAExamBookingRequest scenario4 = reRegBookingPaymentDetails(false, true);
		logger.info("ReReg payment initiated scenario4 secureHash: {}", scenario4.getSecureHash());
		assertEquals(scenario4.getFormParameters().get("CUST_ID"), SAPID_77221111222);
		assertEquals(scenario4.getFormParameters().get("TXN_AMOUNT"), PROJECT_RE_REGISTRATION_CHARGES);
	}
	
	/**
	 * Unit test (expected exception) for noSlotBookingPaymentDetails() method of NoSlotBookingServiceInterface class.
	 */
	@Test
	public void testNoSlotBookingPaymentDetails5() {
		//Scenario 5: Project Registration payment status not Initiated
		logger.info("Reg payment initiated scenario6 Illegal Argument Exception");
		exceptionRule.expect(IllegalArgumentException.class);
	    exceptionRule.expectMessage("Payment attempt already made");
	    regBookingPaymentDetails(true, false);
	}
	
	/**
	 * Unit test (expected exception) for noSlotBookingPaymentDetails() method of NoSlotBookingServiceInterface class.
	 */
	@Test
	public void testNoSlotBookingPaymentDetails6() {
		//Scenario 6: Project Re-Registration payment status not Initiated
		logger.info("ReReg payment initiated scenario6 Illegal Argument Exception");
		exceptionRule.expect(IllegalArgumentException.class);
	    exceptionRule.expectMessage("Payment attempt already made");
	    reRegBookingPaymentDetails(true, false);
	}
	
	/**
	 * Unit test for noSlotBookingStatus() method of NoSlotBookingServiceInterface class.
	 */
	@Test
	public void testNoSlotBookingStatus1() {
		//Scenario 1: Project Registration booking status of successful payment
		List<Map<String, String>> scenario1 = regBookingStatus(true, true);
		logger.info("Reg booking status scenario1 response: {}", scenario1);
		assertEquals(scenario1.get(0).get("type"), TYPE_PROJECT_REGISTRATION);
		assertEquals(scenario1.get(0).get("paymentStatus"), TRAN_STATUS_PAYMENT_SUCCESSFUL);
		assertEquals(scenario1.get(0).get("bookingStatus"), BOOKINGS_STATUS_TRUE);
	}
	
	/**
	 * Unit test for noSlotBookingStatus() method of NoSlotBookingServiceInterface class.
	 */
	@Test
	public void testNoSlotBookingStatus2() {
		//Scenario 2: Project Registration booking status of unsuccessful payment
		List<Map<String, String>> scenario2 = regBookingStatus(false, true);
		logger.info("Reg booking status scenario2 response: {}", scenario2);
		assertEquals(scenario2.get(0).get("type"), TYPE_PROJECT_REGISTRATION);
		assertEquals(scenario2.get(0).get("paymentStatus"), TRAN_STATUS_INITIATED);
		assertEquals(scenario2.get(0).get("bookingStatus"), BOOKINGS_STATUS_FALSE);
	}
	
	/**
	 * Unit test for noSlotBookingStatus() method of NoSlotBookingServiceInterface class.
	 */
	@Test
	public void testNoSlotBookingStatus3() {
		//Scenario 3: Project Re-Registration booking status of successful payment
		List<Map<String, String>> scenario3 = reRegBookingStatus(true, true);
		logger.info("ReReg booking status scenario3 response: {}", scenario3);
		assertEquals(scenario3.get(0).get("type"), TYPE_PROJECT_RE_REGISTRATION);
		assertEquals(scenario3.get(0).get("paymentStatus"), TRAN_STATUS_PAYMENT_SUCCESSFUL);
		assertEquals(scenario3.get(0).get("bookingStatus"), BOOKINGS_STATUS_TRUE);
	}
	
	/**
	 * Unit test for noSlotBookingStatus() method of NoSlotBookingServiceInterface class.
	 */
	@Test
	public void testNoSlotBookingStatus4() {
		//Scenario 4: Project Re-Registration booking status of unsuccessful payment
		List<Map<String, String>> scenario4 = reRegBookingStatus(false, true);
		logger.info("ReReg booking status scenario4 response: {}", scenario4);
		assertEquals(scenario4.get(0).get("type"), TYPE_PROJECT_RE_REGISTRATION);
		assertEquals(scenario4.get(0).get("paymentStatus"), TRAN_STATUS_INITIATED);
		assertEquals(scenario4.get(0).get("bookingStatus"), BOOKINGS_STATUS_FALSE);
	}
	
	/**
	 * Unit test (expected exception) for noSlotBookingStatus() method of NoSlotBookingServiceInterface class.
	 */
	@Test
	public void testNoSlotBookingStatus5() {
		//Scenario 5: Project Registration booking status of invalid trackId
		logger.info("Reg booking status scenario5 Illegal Argument Exception");
		exceptionRule.expect(IllegalArgumentException.class);
	    exceptionRule.expectMessage(ArgumentMatchers.startsWith("No payment records found for sapid: " + SAPID_77221111222 + " and trackId: ") + ArgumentMatchers.anyString());
	    regBookingStatus(true, false);
	}
	
	/**
	 * Unit test (expected exception) for noSlotBookingStatus() method of NoSlotBookingServiceInterface class.
	 */
	@Test
	public void testNoSlotBookingStatus6() {
		//Scenario 6: Project Re-Registration booking status of invalid trackId
		logger.info("ReReg booking status scenario6 Illegal Argument Exception");
		exceptionRule.expect(IllegalArgumentException.class);
	    exceptionRule.expectMessage(ArgumentMatchers.startsWith("No payment records found for sapid: " + SAPID_77221111222 + " and trackId: ") + ArgumentMatchers.anyString());
	    reRegBookingStatus(true, false);
	}
	
	/**
	 * Unit test for processNoSlotBookingPaymentStatus() method of NoSlotBookingServiceInterface class.
	 */
	@Test
	public void testProcessNoSlotBookingPaymentStatus1() {
		//Scenario 1: Process Registration and Re-Registration payments and gateway success response as true
		processBookingPayment(false, false, true);
		logger.info("Process reg and reReg payments scenario1");
	}
	
	/**
	 * Unit test for processNoSlotBookingPaymentStatus() method of NoSlotBookingServiceInterface class.
	 */
	@Test
	public void testProcessNoSlotBookingPaymentStatus2() {
		//Scenario 2: Process Registration and Re-Registration payments wherein both bookings are already booked and gateway success response as true
		processBookingPayment(true, true, true);
		logger.info("Process reg and reReg payments scenario2");
	}
	
	/**
	 * Unit test for processNoSlotBookingPaymentStatus() method of NoSlotBookingServiceInterface class.
	 */
	@Test
	public void testProcessNoSlotBookingPaymentStatus3() {
		//Scenario 3: Process Registration payment and already booked Re-Registration payment and gateway success response as true
		processBookingPayment(false, true, true);
		logger.info("Process reg and reReg payments scenario3");
	}
	
	/**
	 * Unit test for processNoSlotBookingPaymentStatus() method of NoSlotBookingServiceInterface class.
	 */
	@Test
	public void testProcessNoSlotBookingPaymentStatus4() {
		//Scenario 4: Process already booked Registration payment and Re-Registration payment and gateway success response as true
		processBookingPayment(true, false, true);
		logger.info("Process reg and reReg payments scenario4");
	}
	
	/**
	 * Unit test for processNoSlotBookingPaymentStatus() method of NoSlotBookingServiceInterface class.
	 */
	@Test
	public void testProcessNoSlotBookingPaymentStatus5() {
		//Scenario 1: Process Registration and Re-Registration payments and gateway success response as false
		processBookingPayment(false, false, false);
		logger.info("Process reg and reReg payments scenario5");
	}
	
	/**
	 * Unit test for processNoSlotBookingPaymentStatus() method of NoSlotBookingServiceInterface class.
	 */
	@Test
	public void testProcessNoSlotBookingPaymentStatus6() {
		//Scenario 2: Process Registration and Re-Registration payments wherein both bookings are already booked and gateway success response as false
		processBookingPayment(true, true, false);
		logger.info("Process reg and reReg payments scenario6");
	}
	
	/**
	 * Unit test for processNoSlotBookingPaymentStatus() method of NoSlotBookingServiceInterface class.
	 */
	@Test
	public void testProcessNoSlotBookingPaymentStatus7() {
		//Scenario 3: Process Registration payment and already booked Re-Registration payment and gateway success response as false
		processBookingPayment(false, true, false);
		logger.info("Process reg and reReg payments scenario7");
	}
	
	/**
	 * Unit test for processNoSlotBookingPaymentStatus() method of NoSlotBookingServiceInterface class.
	 */
	@Test
	public void testProcessNoSlotBookingPaymentStatus8() {
		//Scenario 4: Process already booked Registration payment and Re-Registration payment and gateway success response as false
		processBookingPayment(true, false, false);
		logger.info("Process reg and reReg payments scenario8");
	}
	
	/**
	 * Unit tests for processNoSlotBookingExpiredPayments() method of NoSlotBookingServiceInterface class.
	 */
	@Test
	public void testProcessNoSlotBookingExpiredPayments() {
		//Process Registration and Re-Registration expired payments
		processExpiredPayments();
		logger.info("Process reg and reReg expired payments");
	}
	
	/**
	 * Calls the studentProjectRegistrationEligibility() method of NoSlotBookingServiceInterface 
	 * and mocks the return values of its DAO calls.
	 * @return response Map returned containing eligibility details
	 */
	private Map<String, Object> projectRegistrationEligibilityCheck(final int bookingCount, final boolean isRegLive, final boolean isReRegLive) {
		timeboundDetails();																//Mocking timebound details DAO calls
		
		projectRegDetails(isRegLive);													//Mocking Project Registration details DAO calls
		
		checkEligibleForReExam();														//Mocking eligibility check for Re-Exam DAO calls
		
		projectReRegDetails(isReRegLive);												//Mocking Project Re-Registration details DAO calls
		
		//Mocking booking status check DAO calls
		when(noSlotBookingDAO.checkNoSlotBookingStatus(SAPID_77221111222, TIMEBOUND_ID_1491, TYPE_PROJECT_REGISTRATION, BOOKINGS_STATUS_TRUE))
			.thenReturn(bookingCount);
		when(noSlotBookingDAO.checkNoSlotBookingStatus(SAPID_77221111222, TIMEBOUND_ID_1491, TYPE_PROJECT_RE_REGISTRATION, BOOKINGS_STATUS_TRUE))
			.thenReturn(bookingCount);
		
		Map<String, Object> responseMap = noSlotBookingService.studentProjectRegistrationEligibility(TIMEBOUND_ID_1491, SAPID_77221111222);			//calling the service method
		
		//verifying DAO methods called only once
		verifyTimeboundDaoCalls();
		
		//verifying DAO methods called at most once
		verify(mbawxLiveSettingsDAO, atMost(1))
			.liveSettingsTypeStartEndTime(ACAD_YEAR_2023, ACAD_MONTH_JAN, EXAM_YEAR_2023, EXAM_MONTH_APR, CPS_148, TYPE_PROJECT_REGISTRATION);
		verify(mbawxLiveSettingsDAO, atMost(1))
			.liveSettingsTypeStartEndTime(ACAD_YEAR_2023, ACAD_MONTH_JAN, EXAM_YEAR_2023, EXAM_MONTH_APR, CPS_148, TYPE_PROJECT_RE_REGISTRATION);
		verify(noSlotBookingDAO, atMost(1))
			.checkNoSlotBookingStatus(SAPID_77221111222, TIMEBOUND_ID_1491, TYPE_PROJECT_REGISTRATION, BOOKINGS_STATUS_TRUE);
		verify(noSlotBookingDAO, atMost(1))
			.checkNoSlotBookingStatus(SAPID_77221111222, TIMEBOUND_ID_1491, TYPE_PROJECT_RE_REGISTRATION, BOOKINGS_STATUS_TRUE);
		
		return responseMap;
	}
	
	/**
	 * Calls the projectRegistrationDetails() method of NoSlotBookingServiceInterface 
	 * and mocks the return values of its DAO calls.
	 * @return response Map returned containing Project Registration details
	 */
	private Map<String, Object> projectRegistrationDetails(final boolean isBooked, final boolean isRegLive) {
		timeboundDetails();										//Mocking timebound details DAO calls
		
		projectRegDetails(isRegLive);							//Mocking Project Registration details DAO calls
		
		bookingStatusCheck(isBooked);							//Mocking Booked status check DAO calls
		
		Map<String, Object> responseMap = noSlotBookingService.projectRegistrationDetails(TIMEBOUND_ID_1491, SAPID_77221111222);			//calling the service method
		
		//verifying DAO methods called only once
		verifyTimeboundDaoCalls();
		
		//verifying DAO methods of liveSettings and bookings
		verify(mbawxLiveSettingsDAO)
			.liveSettingsTypeStartEndTime(ACAD_YEAR_2023, ACAD_MONTH_JAN, EXAM_YEAR_2023, EXAM_MONTH_APR, CPS_148, TYPE_PROJECT_REGISTRATION);
		verify(noSlotBookingDAO, atMost(1))
			.getNoSlotBookingBySapidTimeboundType(TIMEBOUND_ID_1491, SAPID_77221111222, TYPE_PROJECT_REGISTRATION);
				
		return responseMap;
	}
	
	/**
	 * Calls the projectReRegistrationDetails() method of NoSlotBookingServiceInterface 
	 * and mocks the return values of its DAO calls.
	 * @return response Map returned containing Project Re-Registration details
	 */
	private Map<String, Object> projectReRegistrationDetails(final boolean isBooked, final boolean isReRegLive) {
		timeboundDetails();										//Mocking timebound details DAO calls
		
		checkEligibleForReExam();								//Mocking eligibility check for Re-Exam DAO calls
		
		projectReRegDetails(isReRegLive);						//Mocking Project Registration details DAO calls
		
		bookingStatusCheck(isBooked);							//Mocking Booked status check DAO calls
		
		Map<String, Object> responseMap = noSlotBookingService.projectReRegistrationDetails(TIMEBOUND_ID_1491, SAPID_77221111222);			//calling the service method
		
		//verifying DAO methods called only once
		verifyTimeboundDaoCalls();
		
		//verifying DAO methods of liveSettings and bookings
		verify(mbawxLiveSettingsDAO)
			.liveSettingsTypeStartEndTime(ACAD_YEAR_2023, ACAD_MONTH_JAN, EXAM_YEAR_2023, EXAM_MONTH_APR, CPS_148, TYPE_PROJECT_RE_REGISTRATION);
		verify(noSlotBookingDAO, atMost(1))
			.getNoSlotBookingBySapidTimeboundType(TIMEBOUND_ID_1491, SAPID_77221111222, TYPE_PROJECT_RE_REGISTRATION);
				
		return responseMap;
	}
	
	/**
	 * Contains mock and verify DAO calls to insert Project Registration booking details for Web device.
	 * @return trackId of the payment
	 */
	private String insertRegPaymentWeb(final String amount) {
		String paymentDesc = TYPE_PROJECT_REGISTRATION + " for student " + SAPID_77221111222;
		
		when(mbaWxPaymentsDao.insertPaymentRecord(ArgumentMatchers.eq(TYPE_PROJECT_REGISTRATION), ArgumentMatchers.eq(SAPID_77221111222), 
												ArgumentMatchers.eq(PAYMENT_OPTION_PAYTM), ArgumentMatchers.anyString(), ArgumentMatchers.eq(amount), 
												ArgumentMatchers.eq(TRAN_STATUS_INITIATED), ArgumentMatchers.eq(paymentDesc), ArgumentMatchers.eq(PAYMENT_SOURCE_WEBAPP)))
			.thenReturn(3195L);
		
		when(noSlotBookingDAO.insertNoSlotBooking(SAPID_77221111222, TIMEBOUND_ID_1491, TYPE_PROJECT_REGISTRATION, 3195L, BOOKINGS_STATUS_FALSE))
			.thenReturn(1);
		
		String trackId = noSlotBookingService.saveNoSlotBooking(SAPID_77221111222, TIMEBOUND_ID_1491, TYPE_PROJECT_REGISTRATION, 
																amount, PAYMENT_OPTION_PAYTM, PAYMENT_SOURCE_WEBAPP);
		
		//verifying DAO methods run only once
		verify(mbaWxPaymentsDao)
			.insertPaymentRecord(TYPE_PROJECT_REGISTRATION, SAPID_77221111222, PAYMENT_OPTION_PAYTM, trackId, 
								amount, TRAN_STATUS_INITIATED, paymentDesc, PAYMENT_SOURCE_WEBAPP);
		
		verify(noSlotBookingDAO)
			.insertNoSlotBooking(SAPID_77221111222, TIMEBOUND_ID_1491, TYPE_PROJECT_REGISTRATION, 3195L, BOOKINGS_STATUS_FALSE);
						
		return trackId;
	}
	
	/**
	 * Contains mock and verify DAO calls to insert Project Registration booking details for Mobile device.
	 * @return trackId of the payment
	 */
	private String insertRegPaymentMobile(final String amount) {
		String paymentDesc = TYPE_PROJECT_REGISTRATION + " for student " + SAPID_77221111222;
		
		when(mbaWxPaymentsDao.insertPaymentRecord(ArgumentMatchers.eq(TYPE_PROJECT_REGISTRATION), ArgumentMatchers.eq(SAPID_77221111222), 
												ArgumentMatchers.eq(PAYMENT_OPTION_PAYTM), ArgumentMatchers.anyString(), ArgumentMatchers.eq(amount), 
												ArgumentMatchers.eq(TRAN_STATUS_INITIATED), ArgumentMatchers.eq(paymentDesc), ArgumentMatchers.eq(PAYMENT_SOURCE_MOBILE)))
			.thenReturn(3196L);
		
		when(noSlotBookingDAO.insertNoSlotBooking(SAPID_77221111222, TIMEBOUND_ID_1491, TYPE_PROJECT_REGISTRATION, 3196L, BOOKINGS_STATUS_FALSE))
			.thenReturn(1);
		
		String trackId = noSlotBookingService.saveNoSlotBooking(SAPID_77221111222, TIMEBOUND_ID_1491, TYPE_PROJECT_REGISTRATION, 
																amount, PAYMENT_OPTION_PAYTM, PAYMENT_SOURCE_MOBILE);
		
		//verifying DAO methods run only once
		verify(mbaWxPaymentsDao)
			.insertPaymentRecord(TYPE_PROJECT_REGISTRATION, SAPID_77221111222, PAYMENT_OPTION_PAYTM, trackId, 
								amount, TRAN_STATUS_INITIATED, paymentDesc, PAYMENT_SOURCE_MOBILE);
	
		verify(noSlotBookingDAO)
			.insertNoSlotBooking(SAPID_77221111222, TIMEBOUND_ID_1491, TYPE_PROJECT_REGISTRATION, 3196L, BOOKINGS_STATUS_FALSE);
						
		return trackId;
	}
	
	/**
	 * Contains mock and verify DAO calls to insert Project Re-Registration booking details for Web device.
	 * @return trackId of the payment
	 */
	private String insertReRegPaymentWeb(final String amount) {
		String paymentDesc = TYPE_PROJECT_RE_REGISTRATION + " for student " + SAPID_77221111222;
		
		when(mbaWxPaymentsDao.insertPaymentRecord(ArgumentMatchers.eq(TYPE_PROJECT_RE_REGISTRATION), ArgumentMatchers.eq(SAPID_77221111222), 
												ArgumentMatchers.eq(PAYMENT_OPTION_PAYTM), ArgumentMatchers.anyString(), ArgumentMatchers.eq(amount), 
												ArgumentMatchers.eq(TRAN_STATUS_INITIATED), ArgumentMatchers.eq(paymentDesc), ArgumentMatchers.eq(PAYMENT_SOURCE_WEBAPP)))
			.thenReturn(3195L);
		
		when(noSlotBookingDAO.insertNoSlotBooking(SAPID_77221111222, TIMEBOUND_ID_1491, TYPE_PROJECT_RE_REGISTRATION, 3195L, BOOKINGS_STATUS_FALSE))
			.thenReturn(1);
		
		String trackId = noSlotBookingService.saveNoSlotBooking(SAPID_77221111222, TIMEBOUND_ID_1491, TYPE_PROJECT_RE_REGISTRATION, 
																amount, PAYMENT_OPTION_PAYTM, PAYMENT_SOURCE_WEBAPP);

		//verifying DAO methods run only once
		verify(mbaWxPaymentsDao)
			.insertPaymentRecord(TYPE_PROJECT_RE_REGISTRATION, SAPID_77221111222, PAYMENT_OPTION_PAYTM, trackId, 
								amount, TRAN_STATUS_INITIATED, paymentDesc, PAYMENT_SOURCE_WEBAPP);
		
		verify(noSlotBookingDAO)
			.insertNoSlotBooking(SAPID_77221111222, TIMEBOUND_ID_1491, TYPE_PROJECT_RE_REGISTRATION, 3195L, BOOKINGS_STATUS_FALSE);
		
		return trackId;
	}
	
	/**
	 * Contains mock and verify DAO calls to insert Project Re-Registration booking details for Mobile device.
	 * @return trackId of the payment
	 */
	private String insertReRegPaymentMobile(final String amount) {
		String paymentDesc = TYPE_PROJECT_RE_REGISTRATION + " for student " + SAPID_77221111222;
		
		when(mbaWxPaymentsDao.insertPaymentRecord(ArgumentMatchers.eq(TYPE_PROJECT_RE_REGISTRATION), ArgumentMatchers.eq(SAPID_77221111222), 
												ArgumentMatchers.eq(PAYMENT_OPTION_PAYTM), ArgumentMatchers.anyString(), ArgumentMatchers.eq(amount), 
												ArgumentMatchers.eq(TRAN_STATUS_INITIATED), ArgumentMatchers.eq(paymentDesc), ArgumentMatchers.eq(PAYMENT_SOURCE_MOBILE)))
			.thenReturn(3196L);
		
		when(noSlotBookingDAO.insertNoSlotBooking(SAPID_77221111222, TIMEBOUND_ID_1491, TYPE_PROJECT_RE_REGISTRATION, 3196L, BOOKINGS_STATUS_FALSE))
			.thenReturn(1);
		
		String trackId = noSlotBookingService.saveNoSlotBooking(SAPID_77221111222, TIMEBOUND_ID_1491, TYPE_PROJECT_RE_REGISTRATION, 
																amount, PAYMENT_OPTION_PAYTM, PAYMENT_SOURCE_MOBILE);

		//verifying DAO methods run only once
		verify(mbaWxPaymentsDao)
			.insertPaymentRecord(TYPE_PROJECT_RE_REGISTRATION, SAPID_77221111222, PAYMENT_OPTION_PAYTM, trackId, 
								amount, TRAN_STATUS_INITIATED, paymentDesc, PAYMENT_SOURCE_MOBILE);
	
		verify(noSlotBookingDAO)
			.insertNoSlotBooking(SAPID_77221111222, TIMEBOUND_ID_1491, TYPE_PROJECT_RE_REGISTRATION, 3196L, BOOKINGS_STATUS_FALSE);
		
		return trackId;
	}
	
	/**
	 * Contains mock and verify DAO calls of Project Registration payment initiated details.
	 * @return bean containing payment details
	 */
	@SuppressWarnings("serial")
	private MBAExamBookingRequest regBookingPaymentDetails(final boolean webDevice, final boolean initiatedPaymentPresent) {
		final String trackId = "772211112221681994861689";
		final String description = TYPE_PROJECT_REGISTRATION + " for student " + SAPID_77221111222;
		
		if(initiatedPaymentPresent) {
			MBAPaymentRequest paymentRequest = new MBAPaymentRequest();
			paymentRequest.setPaymentOption(PAYMENT_OPTION_PAYTM);
			paymentRequest.setTrackId(trackId);
			paymentRequest.setAmount(PROJECT_REGISTRATION_CHARGES);
			paymentRequest.setDescription(description);
			paymentRequest.setSource(webDevice ? PAYMENT_SOURCE_WEBAPP : PAYMENT_SOURCE_MOBILE);
			
			when(mbaWxPaymentsDao.getPaymentRecordsBySapidTrackIdTranStatus(SAPID_77221111222, trackId, TRAN_STATUS_INITIATED))
				.thenReturn(Arrays.asList(paymentRequest));
		}
		else {
			when(mbaWxPaymentsDao.getPaymentRecordsBySapidTrackIdTranStatus(SAPID_77221111222, trackId, TRAN_STATUS_INITIATED))
				.thenThrow(new DataAccessException("No records found") {});
		}
		
		StudentExamBean studentBean = new StudentExamBean();
		studentBean.setSapid(SAPID_77221111222);
		studentBean.setEmailId("test@mail.com");
		studentBean.setMobile("7777788888");
		when(studentDAO.getStudentInfo(SAPID_77221111222)).thenReturn(studentBean);
		
		MBAExamBookingRequest bookingRequest = noSlotBookingService.noSlotBookingPaymentDetails(SAPID_77221111222, String.valueOf(TIMEBOUND_ID_1491), TYPE_PROJECT_REGISTRATION, 
																								trackId, webDevice);
		
		//verifying DAO methods run only once
		verify(mbaWxPaymentsDao)
			.getPaymentRecordsBySapidTrackIdTranStatus(SAPID_77221111222, trackId, TRAN_STATUS_INITIATED);
	
		verify(studentDAO)
			.getStudentInfo(SAPID_77221111222);
		
		return bookingRequest;
	}
	
	/**
	 * Contains mock and verify DAO calls of Project Re-Registration payment initiated details.
	 * @return bean containing payment details
	 */
	@SuppressWarnings("serial")
	private MBAExamBookingRequest reRegBookingPaymentDetails(final boolean webDevice, final boolean initiatedPaymentPresent) {
		final String trackId = "772211112221681994760179";
		final String description = TYPE_PROJECT_RE_REGISTRATION + " for student " + SAPID_77221111222;
		
		if(initiatedPaymentPresent) {
			MBAPaymentRequest paymentRequest = new MBAPaymentRequest();
			paymentRequest.setPaymentOption(PAYMENT_OPTION_PAYTM);
			paymentRequest.setTrackId(trackId);
			paymentRequest.setAmount(PROJECT_RE_REGISTRATION_CHARGES);
			paymentRequest.setDescription(description);
			paymentRequest.setSource(webDevice ? PAYMENT_SOURCE_WEBAPP : PAYMENT_SOURCE_MOBILE);
			
			when(mbaWxPaymentsDao.getPaymentRecordsBySapidTrackIdTranStatus(SAPID_77221111222, trackId, TRAN_STATUS_INITIATED))
				.thenReturn(Arrays.asList(paymentRequest));
		}
		else {
			when(mbaWxPaymentsDao.getPaymentRecordsBySapidTrackIdTranStatus(SAPID_77221111222, trackId, TRAN_STATUS_INITIATED))
				.thenThrow(new DataAccessException("No records found") {});
		}
		
		StudentExamBean studentBean = new StudentExamBean();
		studentBean.setSapid(SAPID_77221111222);
		studentBean.setEmailId("test@mail.com");
		studentBean.setMobile("7777788888");
		when(studentDAO.getStudentInfo(SAPID_77221111222)).thenReturn(studentBean);
		
		MBAExamBookingRequest bookingRequest = noSlotBookingService.noSlotBookingPaymentDetails(SAPID_77221111222, String.valueOf(TIMEBOUND_ID_1491), TYPE_PROJECT_RE_REGISTRATION, 
																								trackId, webDevice);
		
		//verifying DAO methods run only once
		verify(mbaWxPaymentsDao)
			.getPaymentRecordsBySapidTrackIdTranStatus(SAPID_77221111222, trackId, TRAN_STATUS_INITIATED);
	
		verify(studentDAO)
			.getStudentInfo(SAPID_77221111222);
		
		return bookingRequest;
	}
	
	/**
	 * Contains mock and verify DAO calls to get Project Registration booking status.
	 * @return list of map containing booking details
	 */
	private List<Map<String, String>> regBookingStatus(final boolean paymentSuccessful, final boolean validTrackId) {
		final String trackId = "772211112221681994861689";
		final Long paymentRecordId = 3196L;
		
		if(validTrackId) {
			MBAPaymentRequest paymentRequest = new MBAPaymentRequest();
			paymentRequest.setId(paymentRecordId);
			paymentRequest.setPaymentType(TYPE_PROJECT_REGISTRATION);
			paymentRequest.setPaymentOption(PAYMENT_OPTION_PAYTM);
			paymentRequest.setAmount(PROJECT_REGISTRATION_CHARGES);
			paymentRequest.setTranStatus(paymentSuccessful ? TRAN_STATUS_PAYMENT_SUCCESSFUL: TRAN_STATUS_INITIATED);
			
			when(mbaWxPaymentsDao.getPaymentDetailsBySapidTrackId(SAPID_77221111222, trackId))
				.thenReturn(Arrays.asList(paymentRequest));
		}
		else {
			when(mbaWxPaymentsDao.getPaymentDetailsBySapidTrackId(SAPID_77221111222, trackId))
			.thenReturn(new ArrayList<>());
		}
		
		final String bookingSuccessful = paymentSuccessful ? BOOKINGS_STATUS_TRUE : BOOKINGS_STATUS_FALSE;
		when(noSlotBookingDAO.getNoSlotBookingStatus(SAPID_77221111222, TIMEBOUND_ID_1491, TYPE_PROJECT_REGISTRATION, paymentRecordId))
			.thenReturn(bookingSuccessful);
		
		List<Map<String, String>> bookingStatusList = noSlotBookingService.noSlotBookingStatus(SAPID_77221111222, TIMEBOUND_ID_1491, trackId);
		
		//verifying DAO methods run only once
		verify(mbaWxPaymentsDao)
			.getPaymentDetailsBySapidTrackId(SAPID_77221111222, trackId);
	
		verify(noSlotBookingDAO)
			.getNoSlotBookingStatus(SAPID_77221111222, TIMEBOUND_ID_1491, TYPE_PROJECT_REGISTRATION, paymentRecordId);
		
		return bookingStatusList;
	}
	
	/**
	 * Contains mock and verify DAO calls to get Project Re-Registration booking status
	 * @return list of map containing booking details
	 */
	private List<Map<String, String>> reRegBookingStatus(final boolean paymentSuccessful, final boolean validTrackId) {
		final String trackId = "772211112221681994760179";
		final Long paymentRecordId = 3195L;
		
		if(validTrackId) {
			MBAPaymentRequest paymentRequest = new MBAPaymentRequest();
			paymentRequest.setId(paymentRecordId);
			paymentRequest.setPaymentType(TYPE_PROJECT_RE_REGISTRATION);
			paymentRequest.setPaymentOption(PAYMENT_OPTION_PAYTM);
			paymentRequest.setAmount(PROJECT_RE_REGISTRATION_CHARGES);
			paymentRequest.setTranStatus(paymentSuccessful ? TRAN_STATUS_PAYMENT_SUCCESSFUL: TRAN_STATUS_INITIATED);
			
			when(mbaWxPaymentsDao.getPaymentDetailsBySapidTrackId(SAPID_77221111222, trackId))
				.thenReturn(Arrays.asList(paymentRequest));
		}
		else {
			when(mbaWxPaymentsDao.getPaymentDetailsBySapidTrackId(SAPID_77221111222, trackId))
				.thenReturn(new ArrayList<>());
		}
		
		final String bookingSuccessful = paymentSuccessful ? BOOKINGS_STATUS_TRUE : BOOKINGS_STATUS_FALSE;
		when(noSlotBookingDAO.getNoSlotBookingStatus(SAPID_77221111222, TIMEBOUND_ID_1491, TYPE_PROJECT_RE_REGISTRATION, paymentRecordId))
			.thenReturn(bookingSuccessful);
		
		List<Map<String, String>> bookingStatusList = noSlotBookingService.noSlotBookingStatus(SAPID_77221111222, TIMEBOUND_ID_1491, trackId);
		
		//verifying DAO methods run only once
		verify(mbaWxPaymentsDao)
			.getPaymentDetailsBySapidTrackId(SAPID_77221111222, trackId);
	
		verify(noSlotBookingDAO)
			.getNoSlotBookingStatus(SAPID_77221111222, TIMEBOUND_ID_1491, TYPE_PROJECT_RE_REGISTRATION, paymentRecordId);
		
		return bookingStatusList;
	}
	
	/**
	 * Contains mock and verify DAO calls to process Project Registration & Re-Registration booking payment.
	 */
	private void processBookingPayment(final boolean regBookingPresent, final boolean reRegBookingPresent, final boolean paymentSuccessful) {
		final String regTrackId = "772211112221681899603510";
		final String reRegTrackId = "772211112221681900577711";
		
		MBAPaymentRequest regPaymentRequest = new MBAPaymentRequest();
		regPaymentRequest.setId(3190L);
		regPaymentRequest.setSapid(SAPID_77221111222);
		regPaymentRequest.setPaymentOption(PAYMENT_OPTION_PAYTM);
		regPaymentRequest.setTrackId(regTrackId);
		regPaymentRequest.setSuccessFromGateway(paymentSuccessful);
		
		NoSlotBookingBean regBookingBean = new NoSlotBookingBean();
		regBookingBean.setId(50L);
		regBookingBean.setSapid(SAPID_77221111222);
		regBookingBean.setTimeboundId(TIMEBOUND_ID_1491);
		regBookingBean.setType(TYPE_PROJECT_REGISTRATION);
		
		MBAPaymentRequest reRegPaymentRequest = new MBAPaymentRequest();
		reRegPaymentRequest.setId(3191L);
		reRegPaymentRequest.setSapid(SAPID_77221111222);
		reRegPaymentRequest.setPaymentOption(PAYMENT_OPTION_PAYTM);
		reRegPaymentRequest.setTrackId(reRegTrackId);
		reRegPaymentRequest.setSuccessFromGateway(paymentSuccessful);
		
		NoSlotBookingBean reRegBookingBean = new NoSlotBookingBean();
		reRegBookingBean.setId(51L);
		reRegBookingBean.setSapid(SAPID_77221111222);
		reRegBookingBean.setTimeboundId(TIMEBOUND_ID_1491);
		reRegBookingBean.setType(TYPE_PROJECT_RE_REGISTRATION);
		
		when(mbaWxPaymentsDao.getAllTransactionsByTypeStatus(ArgumentMatchers.anyList(), ArgumentMatchers.eq(TRAN_STATUS_INITIATED)))
			.thenReturn(Arrays.asList(regPaymentRequest, reRegPaymentRequest));
		
		MBAPaymentHelper paymentHelper = mock(MBAPaymentHelper.class);
		when(paymentHelper.checkTransactionStatus(regPaymentRequest)).thenReturn("");
		when(paymentHelper.checkTransactionStatus(reRegPaymentRequest)).thenReturn("");
		
		if(paymentSuccessful) {
			when(noSlotBookingDAO.noSlotBookingsByTrackId(regTrackId)).thenReturn(Arrays.asList(regBookingBean));
			when(noSlotBookingDAO.noSlotBookingsByTrackId(reRegTrackId)).thenReturn(Arrays.asList(reRegBookingBean));
			
			when(noSlotBookingDAO.checkNoSlotBookingStatus(SAPID_77221111222, TIMEBOUND_ID_1491, TYPE_PROJECT_REGISTRATION, BOOKINGS_STATUS_TRUE))
				.thenReturn(regBookingPresent ? 1 : 0);
			when(noSlotBookingDAO.checkNoSlotBookingStatus(SAPID_77221111222, TIMEBOUND_ID_1491, TYPE_PROJECT_RE_REGISTRATION, BOOKINGS_STATUS_TRUE))
				.thenReturn(reRegBookingPresent ? 1 : 0);
			
			when(noSlotBookingDAO.insertNoSlotBookingConflictTransaction(regTrackId, regBookingBean.getId(), MODIFIED_BY_AUTO_BOOKING_SCHEDULER))
				.thenReturn(1);
			when(noSlotBookingDAO.insertNoSlotBookingConflictTransaction(reRegTrackId, reRegBookingBean.getId(), MODIFIED_BY_AUTO_BOOKING_SCHEDULER))
				.thenReturn(1);
			
			when(mbaWxPaymentsDao.updatePaymentRecords(ArgumentMatchers.any(MBAPaymentRequest.class), ArgumentMatchers.eq(TRAN_STATUS_MANUALLY_APPROVED), 
														ArgumentMatchers.eq(SAPID_77221111222), ArgumentMatchers.eq(MODIFIED_BY_AUTO_BOOKING_SCHEDULER)))
				.thenReturn(1);
			when(mbaWxPaymentsDao.updatePaymentRecords(ArgumentMatchers.any(MBAPaymentRequest.class), ArgumentMatchers.eq(TRAN_STATUS_PAYMENT_SUCCESSFUL), 
														ArgumentMatchers.eq(SAPID_77221111222), ArgumentMatchers.eq(MODIFIED_BY_AUTO_BOOKING_SCHEDULER)))
				.thenReturn(1);
			
			when(noSlotBookingDAO.updateNoSlotBookingStatusBySapidPaymentId(BOOKINGS_STATUS_FALSE, MODIFIED_BY_AUTO_BOOKING_SCHEDULER, SAPID_77221111222, regPaymentRequest.getId()))
				.thenReturn(1);
			when(noSlotBookingDAO.updateNoSlotBookingStatusBySapidPaymentId(BOOKINGS_STATUS_TRUE, MODIFIED_BY_AUTO_BOOKING_SCHEDULER, SAPID_77221111222, regPaymentRequest.getId()))
				.thenReturn(1);
			when(noSlotBookingDAO.updateNoSlotBookingStatusBySapidPaymentId(BOOKINGS_STATUS_FALSE, MODIFIED_BY_AUTO_BOOKING_SCHEDULER, SAPID_77221111222, reRegPaymentRequest.getId()))
				.thenReturn(1);
			when(noSlotBookingDAO.updateNoSlotBookingStatusBySapidPaymentId(BOOKINGS_STATUS_TRUE, MODIFIED_BY_AUTO_BOOKING_SCHEDULER, SAPID_77221111222, reRegPaymentRequest.getId()))
				.thenReturn(1);
		
			Map<String, Object> studentDetailsMap = new HashMap<>();
			studentDetailsMap.put("name", "Hello Test");
			studentDetailsMap.put("emailId", "test@mail.com");
			when(studentDAO.getStudentNameEmailId(SAPID_77221111222)).thenReturn(studentDetailsMap);
		}
		
		noSlotBookingService.processNoSlotBookingPaymentStatus();
		
		
		//verify DAO calls run only once or at most once
		verify(mbaWxPaymentsDao).getAllTransactionsByTypeStatus(ArgumentMatchers.anyList(), ArgumentMatchers.eq(TRAN_STATUS_INITIATED));
		
		if(paymentSuccessful) {
			verify(noSlotBookingDAO).noSlotBookingsByTrackId(regTrackId);
			verify(noSlotBookingDAO).noSlotBookingsByTrackId(reRegTrackId);
			
			verify(noSlotBookingDAO, atMost(1)).checkNoSlotBookingStatus(SAPID_77221111222, TIMEBOUND_ID_1491, TYPE_PROJECT_REGISTRATION, BOOKINGS_STATUS_TRUE);
			verify(noSlotBookingDAO, atMost(1)).checkNoSlotBookingStatus(SAPID_77221111222, TIMEBOUND_ID_1491, TYPE_PROJECT_RE_REGISTRATION, BOOKINGS_STATUS_TRUE);
			
			verify(noSlotBookingDAO, atMost(1)).insertNoSlotBookingConflictTransaction(regTrackId, regBookingBean.getId(), MODIFIED_BY_AUTO_BOOKING_SCHEDULER);
			verify(noSlotBookingDAO, atMost(1)).insertNoSlotBookingConflictTransaction(reRegTrackId, reRegBookingBean.getId(), MODIFIED_BY_AUTO_BOOKING_SCHEDULER);
			
			verify(mbaWxPaymentsDao, times(2)).updatePaymentRecords(ArgumentMatchers.any(MBAPaymentRequest.class), ArgumentMatchers.anyString(), 
														ArgumentMatchers.eq(SAPID_77221111222), ArgumentMatchers.eq(MODIFIED_BY_AUTO_BOOKING_SCHEDULER));
			
			verify(noSlotBookingDAO, atMost(1)).updateNoSlotBookingStatusBySapidPaymentId(BOOKINGS_STATUS_FALSE, MODIFIED_BY_AUTO_BOOKING_SCHEDULER, SAPID_77221111222, regPaymentRequest.getId());
			verify(noSlotBookingDAO, atMost(1)).updateNoSlotBookingStatusBySapidPaymentId(BOOKINGS_STATUS_TRUE, MODIFIED_BY_AUTO_BOOKING_SCHEDULER, SAPID_77221111222, regPaymentRequest.getId());
			verify(noSlotBookingDAO, atMost(1)).updateNoSlotBookingStatusBySapidPaymentId(BOOKINGS_STATUS_FALSE, MODIFIED_BY_AUTO_BOOKING_SCHEDULER, SAPID_77221111222, reRegPaymentRequest.getId());
			verify(noSlotBookingDAO, atMost(1)).updateNoSlotBookingStatusBySapidPaymentId(BOOKINGS_STATUS_TRUE, MODIFIED_BY_AUTO_BOOKING_SCHEDULER, SAPID_77221111222, reRegPaymentRequest.getId());
			
			verify(studentDAO, atMost(2)).getStudentNameEmailId(SAPID_77221111222);
		}
	}
	
	/**
	 * Contains mock and verify DAO calls to process Project Registration & Re-Registration expired payments.
	 */
	private void processExpiredPayments() {
		final String regTrackId = "772211112221681899603510";
		final String reRegTrackId = "772211112221681900577711";
		
		MBAPaymentRequest regPaymentRequest = new MBAPaymentRequest();
		regPaymentRequest.setId(3190L);
		regPaymentRequest.setSapid(SAPID_77221111222);
		regPaymentRequest.setPaymentOption(PAYMENT_OPTION_PAYTM);
		regPaymentRequest.setTrackId(regTrackId);
		
		MBAPaymentRequest reRegPaymentRequest = new MBAPaymentRequest();
		reRegPaymentRequest.setId(3191L);
		reRegPaymentRequest.setSapid(SAPID_77221111222);
		regPaymentRequest.setPaymentOption(PAYMENT_OPTION_PAYTM);
		reRegPaymentRequest.setTrackId(reRegTrackId);
		
		when(mbaWxPaymentsDao.getExpiredTransactionsByTypeStatus(ArgumentMatchers.anyList(), ArgumentMatchers.eq(TRAN_STATUS_INITIATED)))
			.thenReturn(Arrays.asList(regPaymentRequest, reRegPaymentRequest));
		
		when(mbaWxPaymentsDao.updatePaymentRecord(regPaymentRequest.getId(), TRAN_STATUS_EXPIRED, MODIFIED_BY_AUTO_BOOKING_SCHEDULER)).thenReturn(1);
		when(mbaWxPaymentsDao.updatePaymentRecord(reRegPaymentRequest.getId(), TRAN_STATUS_EXPIRED, MODIFIED_BY_AUTO_BOOKING_SCHEDULER)).thenReturn(1);
		
		noSlotBookingService.processNoSlotBookingExpiredPayments();
		
		//verify DAO calls run only once
		verify(mbaWxPaymentsDao).getExpiredTransactionsByTypeStatus(ArgumentMatchers.anyList(), ArgumentMatchers.eq(TRAN_STATUS_INITIATED));
		verify(mbaWxPaymentsDao).updatePaymentRecord(regPaymentRequest.getId(), TRAN_STATUS_EXPIRED, MODIFIED_BY_AUTO_BOOKING_SCHEDULER);
		verify(mbaWxPaymentsDao).updatePaymentRecord(reRegPaymentRequest.getId(), TRAN_STATUS_EXPIRED, MODIFIED_BY_AUTO_BOOKING_SCHEDULER);
	}
	
	/**
	 * Contains mock DAO calls required to fetch timebound details.
	 */
	private void timeboundDetails() {
		StudentSubjectConfigExamBean timeboundBean = new StudentSubjectConfigExamBean();
		timeboundBean.setAcadYear(ACAD_YEAR_2023);
		timeboundBean.setAcadMonth(ACAD_MONTH_JAN);
		timeboundBean.setExamYear(EXAM_YEAR_2023);
		timeboundBean.setExamMonth(EXAM_MONTH_APR);
		timeboundBean.setPrgm_sem_subj_id(PSS_ID_2268);					//Digital Marketing Strategy pssId
		
		//Mocking return values of DAO calls
		when(noSlotBookingDAO.getTimeboundDetails(TIMEBOUND_ID_1491))
			.thenReturn(timeboundBean);
		when(dashboardDAO.getSubjectNameByPSSId(timeboundBean.getPrgm_sem_subj_id()))
			.thenReturn(SUBJECT_DMS);
		when(noSlotBookingDAO.getCPSIdBySapidYearMonth(SAPID_77221111222, timeboundBean.getAcadYear(), timeboundBean.getAcadMonth()))
			.thenReturn(CPS_148);
	}
	
	/**
	 * Contains timebound DAO calls verify methods.
	 */
	private void verifyTimeboundDaoCalls() {
		verify(noSlotBookingDAO).getTimeboundDetails(TIMEBOUND_ID_1491);
		verify(dashboardDAO).getSubjectNameByPSSId(PSS_ID_2268);
		verify(noSlotBookingDAO).getCPSIdBySapidYearMonth(SAPID_77221111222, ACAD_YEAR_2023, ACAD_MONTH_JAN);
	}
	
	/**
	 * Contains mock DAO calls required to fetch Project Registration details.
	 */
	@SuppressWarnings("serial")
	private void projectRegDetails(final boolean isRegLive) {
		if(isRegLive) {																			//Checks if Project Registration is active
			MBALiveSettings registrationLiveSettings = new MBALiveSettings();
			registrationLiveSettings.setType(TYPE_PROJECT_REGISTRATION);
			registrationLiveSettings.setStartTime(new Date());
			registrationLiveSettings.setEndTime(new Date());
			
			when(mbawxLiveSettingsDAO.liveSettingsTypeStartEndTime(ACAD_YEAR_2023, ACAD_MONTH_JAN, EXAM_YEAR_2023, EXAM_MONTH_APR, CPS_148, 
																	TYPE_PROJECT_REGISTRATION))
				.thenReturn(registrationLiveSettings);
		}
		else {
			when(mbawxLiveSettingsDAO.liveSettingsTypeStartEndTime(ACAD_YEAR_2023, ACAD_MONTH_JAN, EXAM_YEAR_2023, EXAM_MONTH_APR, CPS_148, 
																	TYPE_PROJECT_REGISTRATION))
				.thenThrow(new DataAccessException("No records found!") {});					//DatAccess Exception thrown on no results found
		}
	}
	
	/**
	 * Contains mock DAO calls to check booked status of Project Registration & Re-Registration.
	 */
	private void bookingStatusCheck(final boolean isBooked) {
		NoSlotBookingBean bookingBean = new NoSlotBookingBean();
		bookingBean.setPaymentRecordId(3196L);
		
		if(isBooked) {
			bookingBean.setStatus(BOOKINGS_STATUS_TRUE);
			
			when(noSlotBookingDAO.getNoSlotBookingBySapidTimeboundType(TIMEBOUND_ID_1491, SAPID_77221111222, TYPE_PROJECT_REGISTRATION))
				.thenReturn(new ArrayList<>(Arrays.asList(bookingBean)));
			when(noSlotBookingDAO.getNoSlotBookingBySapidTimeboundType(TIMEBOUND_ID_1491, SAPID_77221111222, TYPE_PROJECT_RE_REGISTRATION))
				.thenReturn(new ArrayList<>(Arrays.asList(bookingBean)));
		}
		else {
			bookingBean.setStatus(BOOKINGS_STATUS_FALSE);
			
			when(noSlotBookingDAO.getNoSlotBookingBySapidTimeboundType(TIMEBOUND_ID_1491, SAPID_77221111222, TYPE_PROJECT_REGISTRATION))
				.thenReturn(new ArrayList<>(Arrays.asList(bookingBean)));
			when(noSlotBookingDAO.getNoSlotBookingBySapidTimeboundType(TIMEBOUND_ID_1491, SAPID_77221111222, TYPE_PROJECT_RE_REGISTRATION))
				.thenReturn(new ArrayList<>(Arrays.asList(bookingBean)));
		}
	}
	
	/**
	 * Contains mock DAO calls required to fetch Project Re-Registration details.
	 */
	@SuppressWarnings("serial")
	private void projectReRegDetails(final boolean isReRegLive) {
		if(isReRegLive) {																			//Checks if Project Re-Registration is active
			MBALiveSettings reRegistrationLiveSettings = new MBALiveSettings();
			reRegistrationLiveSettings.setType(TYPE_PROJECT_RE_REGISTRATION);
			reRegistrationLiveSettings.setStartTime(new Date());
			reRegistrationLiveSettings.setEndTime(new Date());
											
			when(mbawxLiveSettingsDAO.liveSettingsTypeStartEndTime(ACAD_YEAR_2023, ACAD_MONTH_JAN, EXAM_YEAR_2023, EXAM_MONTH_APR, CPS_148, 
																	TYPE_PROJECT_RE_REGISTRATION))
				.thenReturn(reRegistrationLiveSettings);
		}
		else {
			when(mbawxLiveSettingsDAO.liveSettingsTypeStartEndTime(ACAD_YEAR_2023, ACAD_MONTH_JAN, EXAM_YEAR_2023, EXAM_MONTH_APR, CPS_148, 
																	TYPE_PROJECT_RE_REGISTRATION))
				.thenThrow(new DataAccessException("No records found!") {});						//DatAccess Exception thrown on no results found
		}
	}
	
	/**
	 * Contains mock DAO calls to check student eligibility for Project Re-exam.
	 */
	private void checkEligibleForReExam() {
		final List<Integer> sessionPlanIdList = Arrays.asList(991);
		final double testScore = 31.00;
		final Map<Integer, Integer> applicableTestScoreMap  = new HashMap<>();
		applicableTestScoreMap.put(21964970, 70);
		applicableTestScoreMap.put(21964816, 15);
		applicableTestScoreMap.put(21965085, 15);
		
		when(noSlotBookingDAO.getSessionPlanByTimebound(TIMEBOUND_ID_1491)).thenReturn(sessionPlanIdList);
		
		when(studentTestDAO.getApplicableTestsMaxScore(ArgumentMatchers.anyList())).thenReturn(applicableTestScoreMap);
		
		when(studentTestDAO.getStudentTotalScoreByTestIds(ArgumentMatchers.anySet(), ArgumentMatchers.eq(SAPID_77221111222))).thenReturn(testScore);
	}
}
