package com.nmims.test.services;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import com.nmims.beans.MDMSubjectCodeMappingBean;
import com.nmims.daos.SubjectCreditDAOImpl;
import com.nmims.services.SubjectCreditServiceImpl;

@RunWith(SpringRunner.class)
@SpringBootTest
public class SubjectCreditServiceTest {

	@InjectMocks
	private SubjectCreditServiceImpl creditService;	
	@Mock
	private SubjectCreditDAOImpl creditDAO;
	
	
	@Test
	public void getMappedSubjectCredit(){
		List<MDMSubjectCodeMappingBean> subjectCreditList = new ArrayList<>();
		MDMSubjectCodeMappingBean bean = new MDMSubjectCodeMappingBean();
		bean.setId(1259);
		bean.setSubjectCredits(0.0);
		subjectCreditList.add(bean);
		
		final boolean expected = true;
		boolean actual=false;
		
		try
		{
			when(creditDAO.getSubjectCreditList()).thenReturn(subjectCreditList);
			Map<Integer, MDMSubjectCodeMappingBean> mappedSubjectCredit = creditService.getMappedSubjectCredit();
			actual=true;
		}
		catch(Exception e)
		{
			actual=false;
		}
		
		assertEquals(expected, actual);
	}
	
	@Test
	public void getMappedSubjectCreditNoRecordFound(){
		List<MDMSubjectCodeMappingBean> subjectCreditList = new ArrayList<>();
		MDMSubjectCodeMappingBean bean = new MDMSubjectCodeMappingBean();
		bean.setId(1259);
		bean.setSubjectCredits(0.0);
		//subjectCreditList.add(bean);
		
		final boolean expected = true;
		boolean actual=false;
		
		try
		{
			when(creditDAO.getSubjectCreditList()).thenReturn(subjectCreditList);
			Map<Integer, MDMSubjectCodeMappingBean> mappedSubjectCredit = creditService.getMappedSubjectCredit();
			actual=false;
		}
		catch(Exception e)
		{
			actual=true;
		}
		
		assertEquals(expected, actual);
	}
	
	@Test
	public void getMappedSubjectCreditException(){
		
		final boolean expected = true;
		boolean actual=false;
		
		try
		{
			when(creditDAO.getSubjectCreditList()).thenReturn(null);
			Map<Integer, MDMSubjectCodeMappingBean> mappedSubjectCredit = creditService.getMappedSubjectCredit();
			actual=false;
		}
		catch(Exception e)
		{
			actual=true;
		}
		
		assertEquals(expected, actual);
	}
	
	@Test
	public void getMappedPssDetail(){
		List<MDMSubjectCodeMappingBean> pssDetailList = new ArrayList<>();
		MDMSubjectCodeMappingBean bean1 = new MDMSubjectCodeMappingBean();
		bean1.setConsumerProgramStructureId("84");
		bean1.setSubjectName("Decision Science");
		bean1.setSem("2");
		bean1.setId(1259);
		pssDetailList.add(bean1);
		
		final boolean expected = true;
		boolean actual=false;
		
		try
		{
			when(creditDAO.getPssDetailList()).thenReturn(pssDetailList);
			Map<String, MDMSubjectCodeMappingBean> mappedPssDetail = creditService.getMappedPssDetail();
			actual=true;
		}
		catch(Exception e)
		{
			actual=false;
		}
		
		assertEquals(expected, actual);
	}
	
	@Test
	public void getMappedPssDetailNotRecordFound(){
		List<MDMSubjectCodeMappingBean> pssDetailList = new ArrayList<>();
		MDMSubjectCodeMappingBean bean1 = new MDMSubjectCodeMappingBean();
		bean1.setConsumerProgramStructureId("84");
		bean1.setSubjectName("Decision Science");
		bean1.setSem("2");
		bean1.setId(1259);
		//pssDetailList.add(bean1);
		
		final boolean expected = true;
		boolean actual=false;
		
		try
		{
			when(creditDAO.getPssDetailList()).thenReturn(pssDetailList);
			Map<String, MDMSubjectCodeMappingBean> mappedPssDetail = creditService.getMappedPssDetail();
			actual=false;
		}
		catch(Exception e)
		{
			actual=true;
		}
		
		assertEquals(expected, actual);
	}
	
	@Test
	public void getMappedPssDetailException(){
		
		final boolean expected = true;
		boolean actual=false;
		
		try
		{
			when(creditDAO.getPssDetailList()).thenReturn(null);
			Map<String, MDMSubjectCodeMappingBean> mappedPssDetail = creditService.getMappedPssDetail();
			actual=false;
		}
		catch(Exception e)
		{
			actual=true;
		}
		
		assertEquals(expected, actual);
	}
}
