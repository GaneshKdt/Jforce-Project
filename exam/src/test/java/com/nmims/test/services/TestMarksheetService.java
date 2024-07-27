package com.nmims.test.services;

import java.sql.SQLException;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.boot.test.context.SpringBootTest;

import com.nmims.beans.EmbaPassFailBean;
import com.nmims.daos.ExamsAssessmentsDAO;
import com.nmims.exceptions.NoRecordFoundException;
import com.nmims.services.MarksheetService;

/**
 * 
 * @author Siddheshwar_Khanse
 *
 */

@SpringBootTest
public class TestMarksheetService {

	@InjectMocks
	private MarksheetService marksheetService;
	
	@Mock
	private ExamsAssessmentsDAO examsAssessmentsDAO;
	
	@Rule
	public ExpectedException exceptionRule = ExpectedException.none();
	
	@Before
	public void setup(){
		MockitoAnnotations.initMocks(this);
		
	}
	
	@Test(expected = Exception.class)
	public void testGenerateNonGradedMarksheetWithNull() throws NoRecordFoundException, SQLException, Exception {
		EmbaPassFailBean mbaPassFailBean=null;
		
		marksheetService.generateNonGradedMarksheet(mbaPassFailBean);
		
	}
	
	@Test(expected = Exception.class)
	public void testGenerateNonGradedMarksheetWithEmptySRIds() throws NoRecordFoundException, SQLException, Exception {
		EmbaPassFailBean mbaPassFailBean=null;
		
		mbaPassFailBean = new EmbaPassFailBean();
		mbaPassFailBean.setServiceRequestIdList(" ");
		
		marksheetService.generateNonGradedMarksheet(mbaPassFailBean);
		
	}
	
	@Test()
	public void testGenerateNonGradedMarksheetWithInvalidEmptySRIds() throws NoRecordFoundException, SQLException, Exception {
		EmbaPassFailBean mbaPassFailBean=null;
		
		mbaPassFailBean = new EmbaPassFailBean();
		mbaPassFailBean.setServiceRequestIdList("123456");
		 
		marksheetService.generateNonGradedMarksheet(mbaPassFailBean);
		
	}
}
