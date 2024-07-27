package com.nmims.services;


import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;


//@SpringBootTest
public class StudentReRegistrationServiceTest {

	@Autowired
	private IStudentReRegistrationService underTestStudentReRegistrationService;
	
	private static final String CORRECT_SAPID = "77220653027";
	private static final String DOB = "1990-06-01";
//	private static final String WRONG_SAPID = "ABCD7784";
//	private static final String OTHER_PROGRAM_SAPID = "77777777771";
		
	@Before
	public void setUp() throws Exception {
		underTestStudentReRegistrationService = new StudentReRegistrationService();
	}

	@After
	public void tearDown() throws Exception {
	}

//	@Test
	public void testGetReRegistrationPaymentLink() throws Exception {

		String paymentLink = underTestStudentReRegistrationService.getReRegistrationPaymentLink(CORRECT_SAPID, DOB);
		System.err.println("PAYMENT LINK: " + paymentLink);
		
//		when(underTestStudentReRegistrationService.getReRegistrationPaymentLink(Mockito.anyString(), Mockito.anyString())).thenReturn("SOME VALUE");
	}
}
