package com.nmims.test;

import static org.junit.Assert.assertEquals;

import org.junit.Before;
import org.junit.Test;

import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import com.nmims.beans.ServiceRequestStudentPortal;

import com.nmims.daos.PortalDao;

import com.nmims.services.StudentService;
import com.nmims.stratergies.CheckFinalCertificateEligibilityInterface;


//@RunWith(SpringJUnit4ClassRunner.class)
//@WebAppConfiguration

//@ContextConfiguration(classes = AppConfig.class)
//@ContextConfiguration(locations = {"file:**/WebContent/WEB-INF/spring/appServlet/servlet-context.xml"})
//@ContextConfiguration(locations = {"file:**/WebContent/WEB-INF/web.xml"})

public class CheckFinalCertificateTestCase extends StudentPortalTests {

	
//	@Mock
//	private ServiceRequestDao serviceRequestDao;
//	
	@Mock 
	private PortalDao portalDAO;
	
	@Mock
	private StudentService studentService;
	
	@Autowired @Qualifier("checkFinalCertificateEligibility")
	CheckFinalCertificateEligibilityInterface  checkFinalCertificateEligibility;

	@Before
	public void setup(){
	    MockitoAnnotations.initMocks(this); //without this you will get NPE
	}

	@Test
	public void test() throws Exception {
			
		//fail("Not yet implemented");
		  String expected = "True";
		  String actual = "";
		
		try {
			System.out.println("Test");
			ServiceRequestStudentPortal sr1 = new ServiceRequestStudentPortal();
			sr1.setSapId("77119250172");
		//	sr1.setIsLateral("Y");
		//	sr1.setCpsi("85");
			//sr1.setPreviousStudentId("77117746598");
		//	StudentBean student = serviceRequestDao.getSingleStudentsData("77119250172");

			
			ServiceRequestStudentPortal sr = checkFinalCertificateEligibility.checkFinalCertificateEligibility(sr1);
			actual = "True";
		} catch (Exception e) {
			
		 actual = "False";
			
		}
		assertEquals(expected,actual);
	 
	    
	}

	
}

