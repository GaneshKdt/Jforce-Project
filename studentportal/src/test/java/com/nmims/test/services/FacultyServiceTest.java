package com.nmims.test.services;

import static org.junit.Assert.assertEquals;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import com.nmims.beans.FacultyStudentPortalBean;
import com.nmims.services.FacultyService;


@RunWith(SpringRunner.class)
@SpringBootTest
public class FacultyServiceTest 
{

	@Autowired
	FacultyService facultyService;

	@Before
	public void setup(){
		MockitoAnnotations.initMocks(this); //without this you will get NPE
	}
	
	@Test
	public void facultyProfile()
	{
		int expected = 1;
		int actual = 0;
		try {
			FacultyStudentPortalBean faculty = facultyService.facultyProfile("NGASCE00021");
		
			actual = 1;
		}catch(Exception e)
		{
			
		}
		assertEquals(expected, actual);
			

	}
	
	@Test
	public void facultyProfileA()
	{
		int expected = 1;
		int actual = 0;
		try {
			FacultyStudentPortalBean faculty = facultyService.facultyProfile(null);
		
			actual = 1;
		}catch(Exception e)
		{
			
		}
		assertEquals(expected, actual);
			

	}
	
}
