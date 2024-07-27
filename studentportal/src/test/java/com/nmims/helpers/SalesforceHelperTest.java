package com.nmims.helpers;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.util.Collection;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import com.nmims.beans.StudentOpportunity;
import com.nmims.exceptions.StudentNotFoundException;


//@SpringBootTest
class SalesforceHelperTest {

	private static final String username = "harsh.kumar.ext@nmims.edu";	
	private static final String password = "harshkumar1208N0ctn2KEH9TllkT604oXMOmv";
	private static final String CORRECT_SAPID = "77220653027";
	private static final String WRONG_SAPID = "ABCD7784";
	private static final String OTHER_PROGRAM_SAPID = "77777777771";

	private static SFConnection sf = new SFConnection(username, password);
	
	@Autowired
	private SalesforceHelper helperService = new SalesforceHelper(sf);
	
	/**
	 * Test for Right conditions
	 * */
//	@Test	
	public void testGetStudentOpportunities() throws StudentNotFoundException, Exception {		
	
		//given	
		Collection<StudentOpportunity> studentOpportunities = helperService.getStudentOpportunities(CORRECT_SAPID);
		
		//when
		//then
		assertThat(studentOpportunities).isNotEmpty();
	}
	
	/**
	 * Test for null
	 * */
//	@Test
	public void testExceptionOnGetStudentOpportunities() throws StudentNotFoundException, Exception {
		
		//given
		
		//when
				
		//then
		assertThatThrownBy(() -> helperService.getStudentOpportunities(null))
										.isInstanceOf(Exception.class)
										.hasMessageContaining("Student opportunities not found on salesforce for:" + "null");
	}
	
	/**
	 * Test for Wrong conditions
	 * */	
//	@Test
	public void testGetStudentOpportunitiesWithFalseSapid() throws StudentNotFoundException, Exception {
		
		//given
		
		//when
		
		//then
		assertThatThrownBy(() -> helperService.getStudentOpportunities(WRONG_SAPID))
		.isInstanceOf(StudentNotFoundException.class)
		.hasMessageStartingWith("Student opportunities not found on salesforce for:" + WRONG_SAPID);
	}
	
	/**
	 * Test for Wrong conditions
	 * */	
//	@Test
	public void testGetStudentOpportunitiesWithOtherProgramSapid() throws StudentNotFoundException, Exception {
		
		//given
		
		//when
		
		//then
		assertThatThrownBy(() -> helperService.getStudentOpportunities(OTHER_PROGRAM_SAPID))
		.isInstanceOf(StudentNotFoundException.class)
		.hasMessageStartingWith("Student opportunities not found on salesforce for:" + OTHER_PROGRAM_SAPID);
	}
}
