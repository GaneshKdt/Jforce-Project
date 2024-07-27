package com.nmims.test.services;

import static org.junit.Assert.assertEquals; 
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import com.nmims.services.StudentService;

@RunWith(SpringRunner.class)
@SpringBootTest
public class StudentServiceTest {

	@Autowired
	StudentService studentService;

	@Before
	public void setup(){
		MockitoAnnotations.initMocks(this); //without this you will get NPE
	}
	
	@Test
	public void getapplicableSubjectsForStudent() {
		
		int expected = 1;
		int actual = 0;
		try {

			studentService.getapplicableSubjectsForStudent("77122396045");
 			actual = 1;
			
		}catch(Exception e)
		{
			
		}
		assertEquals(expected, actual);
			
	}
}
