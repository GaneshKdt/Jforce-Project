package com.nmims.controllers;

import static org.junit.Assert.assertEquals;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import com.nmims.daos.PassFailDAO;

@RunWith(SpringRunner.class)
@SpringBootTest
public class HallTicketStudentControllerTest {
	
@Autowired
PassFailDAO pfDAO;

@Before
public void setup(){
	MockitoAnnotations.initMocks(this); //without this you will get NPE
}

@Test
public void getSRTypeTest()
{
	String expected = "Issuance Of Bonafide";
	int actual = 0;
	try {
//		String srtype = pfDAO.getSRType("5651");
//		System.out.println(srtype);
//		 assertEquals(expected, srtype);
	}
	catch(Exception e)
	{
		e.printStackTrace();
	}
}

}
