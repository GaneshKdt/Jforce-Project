package com.nmims.test;

import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.test.context.junit4.SpringRunner;

import com.nmims.beans.ConsumerProgramStructureAcads;
import com.nmims.beans.SearchTimeBoundContent;
import com.nmims.daos.ContentDAO;
import com.nmims.services.AdhocFileUploadService;
import com.nmims.services.ContentMBAWXService;

@RunWith(SpringRunner.class)
public class ContentSchedulingReport
{
	@InjectMocks
	ContentMBAWXService contentMBAWXservice;
	
	@Mock
	ContentDAO contentDAO;
	
	
	//send the subject Name
	//subject code id
	
	@Before
	public void setup(){
	    MockitoAnnotations.initMocks(this);
	}
	
	@Test
	public void searchScheduleContent()
	{
		System.out.println(" search Scheduled Content Started ");
		int expected  = 1;
		int actual = 0;
		
		try {
			SearchTimeBoundContent bean = new SearchTimeBoundContent();
			bean.setMonth("Jul");
			bean.setYear("2020");
			bean.setProgramSemSubjectId("1571,1737,2290,2323");

			List<SearchTimeBoundContent> contentlist = contentMBAWXservice.scheduledContentList(bean.getYear(),bean.getMonth(),bean.getBatchId(),bean.getProgramSemSubjectId(),bean.getFacultyId(),bean.getDate());
			
			
			
			System.out.println("Content List "+contentlist);
		
			
				
			
			
		}catch(Exception e)
		{
			  
		}
		
		
		
		System.out.println(" search Scheduled Content Ended ");
	}
	
	
	@Test
	public void getBatchByPssId()
	{
		System.out.println("Get Batch By PssId Started ");
		int expected  = 1;
		int actual = 0;
		
		try {
			ConsumerProgramStructureAcads  bean = new ConsumerProgramStructureAcads();
			bean.setMonth("Jul");
			bean.setYear("2020");
			bean.setProgramSemSubjectId("1571,1737,2290,2323");

			List<SearchTimeBoundContent> contentlist = contentMBAWXservice.getbatchDetails(bean.getYear(),bean.getMonth(),bean.getProgramSemSubjectId());
			
			
			actual = 1;	
			
			
		}catch(Exception e)
		{
			  
		}
		
		
		
		System.out.println(" Get Batch By PssId Ended ");
	}

	
	@Test
	public void getFacultyByPssId()
	{
		System.out.println("Get Faculty By PssId Started ");
		int expected  = 1;
		int actual = 0;
		
		try {
			ConsumerProgramStructureAcads  bean = new ConsumerProgramStructureAcads();
			bean.setMonth("Jul");
			bean.setYear("2020");
			bean.setProgramSemSubjectId("1571,1737,2290,2323");

			List<String> Facultylist = contentMBAWXservice.getFacultyIdsByPssIds(bean.getYear(),bean.getMonth(),bean.getConsumerProgramStructureId());
			
			
			actual = 1;	
			
			
		}catch(Exception e)
		{
			  
		}
		
		
		
		System.out.println(" Get Batch By PssId Ended ");
	}
}
