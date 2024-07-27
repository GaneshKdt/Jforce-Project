package com.nmims.test.services;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import com.nmims.beans.ServiceRequestStudentPortal;
import com.nmims.daos.ServiceRequestDao;
import com.nmims.stratergies.impl.CheckFinalCertificateEligibilityMBAWX;

@RunWith(SpringRunner.class)
@SpringBootTest
public class CheckFinalCertificateEligibilityMBAWXTest {
	
	@InjectMocks
	CheckFinalCertificateEligibilityMBAWX checkFinalCertificateEligibilityMBAWX; 
	
	@Mock
	ServiceRequestDao serviceRequestDao;
	
	@Test
	public void checkFinalCertificateEligibilityFirstTimeRaise()
	{
		ServiceRequestStudentPortal sr = new ServiceRequestStudentPortal();
		sr.setSapId("77421924980");
		
		List<Integer> q7PssIds = Arrays.asList(1990);
		List<Integer> q8PssIds = Arrays.asList(1991);
		
		List<Integer> clearedQ7AndQ8PssIdList = new ArrayList<Integer>();
		clearedQ7AndQ8PssIdList.addAll(q7PssIds);
		clearedQ7AndQ8PssIdList.addAll(q8PssIds);
		
		List<String> subjectsCleared = new ArrayList<>();
		subjectsCleared.add("Foundations of Probability and Statistics for Data Science");
		subjectsCleared.add("Data Structures and Algorithms");
		subjectsCleared.add("Advanced Mathematical Analysis for Data Science");
		subjectsCleared.add("Essential Engineering Skills in Big Data Analytics Using R and Python");
		subjectsCleared.add("Statistics and Probability in Decision Modeling- 1");
		subjectsCleared.add("Statistics and Probability in Decision Modeling -2");
		subjectsCleared.add("Advanced Data Structures and Algorithms");
		subjectsCleared.add("The Art and Science of Storytelling and Visualization & Design Thinking-1");
		subjectsCleared.add("Methods and Algorithms in Machine Learning -1");
		subjectsCleared.add("Methods and Algorithms in Machine Learning -2");
		subjectsCleared.add("Methods and Algorithms in Machine Learning -3");
		subjectsCleared.add("AI and Decision Sciences");
		subjectsCleared.add("Big Data: An overview of Big Data and Hadoop ecosystems");
		subjectsCleared.add("Advanced Python Programming");
		subjectsCleared.add("Text mining and Natural Language Processing Deep learning for NLP");
		subjectsCleared.add("Business Communication and Presentation Skills for Data Analytics");
		subjectsCleared.add("Economics for Analysts");
		subjectsCleared.add("Business Law and Ethics");
		subjectsCleared.add("Behavioural Science and Analytics");
		subjectsCleared.add("Digital and Social Media Analytics");
		subjectsCleared.add("Product Management");
		subjectsCleared.add("Project Management");
		subjectsCleared.add("Architecting Enterprise Applications and Design Thinking-2");
		subjectsCleared.add("Product Deployment Bootcamp");
		subjectsCleared.add("Quantitative Research Methods");
		
		List<String> clearedQ7AndQ8Subjects = new ArrayList<>();
		clearedQ7AndQ8Subjects.add("Product Deployment Bootcamp");
		clearedQ7AndQ8Subjects.add("Quantitative Research Methods");
		
		final boolean expected = true;
		boolean actual = false;
		
		try
		{
			when(serviceRequestDao.getSubjectsClearedCurrentProgramMBAWX(sr.getSapId())).thenReturn(subjectsCleared);
			when(serviceRequestDao.getProgramStatusOfStudent(sr.getSapId())).thenReturn(null);
			when(serviceRequestDao.noOfSubjectsToClear(sr.getSapId())).thenReturn(27);
			when(serviceRequestDao.getClearedPssIdForQ7(sr.getSapId())).thenReturn(q7PssIds);
			when(serviceRequestDao.getClearedPssIdForQ8(sr.getSapId())).thenReturn(q8PssIds);
			when(serviceRequestDao.getSubjectNameByPssIds(clearedQ7AndQ8PssIdList)).thenReturn(clearedQ7AndQ8Subjects);
			//First time raise Issuance of Final Certificate 
			when(serviceRequestDao.getDiplomaIssuedCount(sr)).thenReturn(0);
			
			ServiceRequestStudentPortal response = checkFinalCertificateEligibilityMBAWX.checkFinalCertificateEligibility(sr);
			if(response != null)
			{
				actual = true;
			}
		}
		catch(Exception e)
		{
			actual = false;
		}
		
		assertEquals(expected,actual);
	}
	
	@Test
	public void checkFinalCertificateEligibilitySecondTimeRaise()
	{
		ServiceRequestStudentPortal sr = new ServiceRequestStudentPortal();
		sr.setSapId("77421924980");
		
		List<Integer> q7PssIds = Arrays.asList(1990);
		List<Integer> q8PssIds = Arrays.asList(1991);
		
		List<Integer> clearedQ7AndQ8PssIdList = new ArrayList<Integer>();
		clearedQ7AndQ8PssIdList.addAll(q7PssIds);
		clearedQ7AndQ8PssIdList.addAll(q8PssIds);
		
		List<String> subjectsCleared = new ArrayList<>();
		subjectsCleared.add("Foundations of Probability and Statistics for Data Science");
		subjectsCleared.add("Data Structures and Algorithms");
		subjectsCleared.add("Advanced Mathematical Analysis for Data Science");
		subjectsCleared.add("Essential Engineering Skills in Big Data Analytics Using R and Python");
		subjectsCleared.add("Statistics and Probability in Decision Modeling- 1");
		subjectsCleared.add("Statistics and Probability in Decision Modeling -2");
		subjectsCleared.add("Advanced Data Structures and Algorithms");
		subjectsCleared.add("The Art and Science of Storytelling and Visualization & Design Thinking-1");
		subjectsCleared.add("Methods and Algorithms in Machine Learning -1");
		subjectsCleared.add("Methods and Algorithms in Machine Learning -2");
		subjectsCleared.add("Methods and Algorithms in Machine Learning -3");
		subjectsCleared.add("AI and Decision Sciences");
		subjectsCleared.add("Big Data: An overview of Big Data and Hadoop ecosystems");
		subjectsCleared.add("Advanced Python Programming");
		subjectsCleared.add("Text mining and Natural Language Processing Deep learning for NLP");
		subjectsCleared.add("Business Communication and Presentation Skills for Data Analytics");
		subjectsCleared.add("Economics for Analysts");
		subjectsCleared.add("Business Law and Ethics");
		subjectsCleared.add("Behavioural Science and Analytics");
		subjectsCleared.add("Digital and Social Media Analytics");
		subjectsCleared.add("Product Management");
		subjectsCleared.add("Project Management");
		subjectsCleared.add("Architecting Enterprise Applications and Design Thinking-2");
		subjectsCleared.add("Product Deployment Bootcamp");
		subjectsCleared.add("Quantitative Research Methods");
		
		List<String> clearedQ7AndQ8Subjects = new ArrayList<>();
		clearedQ7AndQ8Subjects.add("Product Deployment Bootcamp");
		clearedQ7AndQ8Subjects.add("Quantitative Research Methods");
		
		final boolean expected = true;
		boolean actual = false;
		
		try
		{
			when(serviceRequestDao.getSubjectsClearedCurrentProgramMBAWX(sr.getSapId())).thenReturn(subjectsCleared);
			when(serviceRequestDao.getProgramStatusOfStudent(sr.getSapId())).thenReturn(null);
			when(serviceRequestDao.noOfSubjectsToClear(sr.getSapId())).thenReturn(27);
			when(serviceRequestDao.getClearedPssIdForQ7(sr.getSapId())).thenReturn(q7PssIds);
			when(serviceRequestDao.getClearedPssIdForQ8(sr.getSapId())).thenReturn(q8PssIds);
			when(serviceRequestDao.getSubjectNameByPssIds(clearedQ7AndQ8PssIdList)).thenReturn(clearedQ7AndQ8Subjects);
			//Already raise Issuance of Final Certificate
			when(serviceRequestDao.getDiplomaIssuedCount(sr)).thenReturn(1);
			
			ServiceRequestStudentPortal response = checkFinalCertificateEligibilityMBAWX.checkFinalCertificateEligibility(sr);
			if(response != null)
			{
				actual = true;
			}
		}
		catch(Exception e)
		{
			actual = false;
		}
		
		assertEquals(expected,actual);
	}
	
	@Test
	public void checkFinalCertificateEligibilityNoClearedSubject()
	{
		ServiceRequestStudentPortal sr = new ServiceRequestStudentPortal();
		sr.setSapId("77421924980");
		
		List<Integer> q7PssIds = Arrays.asList(1990);
		List<Integer> q8PssIds = Arrays.asList(1991);
		
		List<Integer> clearedQ7AndQ8PssIdList = new ArrayList<Integer>();
		clearedQ7AndQ8PssIdList.addAll(q7PssIds);
		clearedQ7AndQ8PssIdList.addAll(q8PssIds);
		
		List<String> subjectsCleared = new ArrayList<>();
		subjectsCleared.add("Foundations of Probability and Statistics for Data Science");
		subjectsCleared.add("Data Structures and Algorithms");
		subjectsCleared.add("Advanced Mathematical Analysis for Data Science");
		subjectsCleared.add("Essential Engineering Skills in Big Data Analytics Using R and Python");
		subjectsCleared.add("Statistics and Probability in Decision Modeling- 1");
		subjectsCleared.add("Statistics and Probability in Decision Modeling -2");
		subjectsCleared.add("Advanced Data Structures and Algorithms");
		subjectsCleared.add("The Art and Science of Storytelling and Visualization & Design Thinking-1");
		subjectsCleared.add("Methods and Algorithms in Machine Learning -1");
		subjectsCleared.add("Methods and Algorithms in Machine Learning -2");
		subjectsCleared.add("Methods and Algorithms in Machine Learning -3");
		subjectsCleared.add("AI and Decision Sciences");
		subjectsCleared.add("Big Data: An overview of Big Data and Hadoop ecosystems");
		subjectsCleared.add("Advanced Python Programming");
		subjectsCleared.add("Text mining and Natural Language Processing Deep learning for NLP");
		subjectsCleared.add("Business Communication and Presentation Skills for Data Analytics");
		subjectsCleared.add("Economics for Analysts");
		subjectsCleared.add("Business Law and Ethics");
		subjectsCleared.add("Behavioural Science and Analytics");
		subjectsCleared.add("Digital and Social Media Analytics");
		subjectsCleared.add("Product Management");
		subjectsCleared.add("Project Management");
		subjectsCleared.add("Architecting Enterprise Applications and Design Thinking-2");
		subjectsCleared.add("Product Deployment Bootcamp");
		subjectsCleared.add("Quantitative Research Methods");
		
		List<String> clearedQ7AndQ8Subjects = new ArrayList<>();
		clearedQ7AndQ8Subjects.add("Product Deployment Bootcamp");
		clearedQ7AndQ8Subjects.add("Quantitative Research Methods");
		
		final boolean expected = true;
		boolean actual = false;
		
		try
		{
			when(serviceRequestDao.getSubjectsClearedCurrentProgramMBAWX(sr.getSapId())).thenReturn(subjectsCleared);
			when(serviceRequestDao.getProgramStatusOfStudent(sr.getSapId())).thenReturn(null);
			//Number of subject to clear for this sapid is set to zero
			when(serviceRequestDao.noOfSubjectsToClear(sr.getSapId())).thenReturn(0);
			when(serviceRequestDao.getClearedPssIdForQ7(sr.getSapId())).thenReturn(q7PssIds);
			when(serviceRequestDao.getClearedPssIdForQ8(sr.getSapId())).thenReturn(q8PssIds);
			when(serviceRequestDao.getSubjectNameByPssIds(clearedQ7AndQ8PssIdList)).thenReturn(clearedQ7AndQ8Subjects);
			when(serviceRequestDao.getDiplomaIssuedCount(sr)).thenReturn(0);
			
			ServiceRequestStudentPortal response = checkFinalCertificateEligibilityMBAWX.checkFinalCertificateEligibility(sr);
			if(response != null)
			{
				actual = false;
			}
		}
		catch(Exception e)
		{
			actual = true;
		}
		
		assertEquals(expected,actual);
	}
	
	@Test
	public void checkFinalCertificateEligibilityClearedSubjectNotMatched()
	{
		ServiceRequestStudentPortal sr = new ServiceRequestStudentPortal();
		sr.setSapId("77421924980");
		
		List<Integer> q7PssIds = Arrays.asList(1990);
		List<Integer> q8PssIds = Arrays.asList(1991);
		
		List<Integer> clearedQ7AndQ8PssIdList = new ArrayList<Integer>();
		clearedQ7AndQ8PssIdList.addAll(q7PssIds);
		clearedQ7AndQ8PssIdList.addAll(q8PssIds);
		
		List<String> subjectsCleared = new ArrayList<>();
		subjectsCleared.add("Foundations of Probability and Statistics for Data Science");
		subjectsCleared.add("Data Structures and Algorithms");
		subjectsCleared.add("Advanced Mathematical Analysis for Data Science");
		subjectsCleared.add("Essential Engineering Skills in Big Data Analytics Using R and Python");
		subjectsCleared.add("Statistics and Probability in Decision Modeling- 1");
		subjectsCleared.add("Statistics and Probability in Decision Modeling -2");
		subjectsCleared.add("Advanced Data Structures and Algorithms");
		subjectsCleared.add("The Art and Science of Storytelling and Visualization & Design Thinking-1");
		subjectsCleared.add("Methods and Algorithms in Machine Learning -1");
		subjectsCleared.add("Methods and Algorithms in Machine Learning -2");
		subjectsCleared.add("Methods and Algorithms in Machine Learning -3");
		subjectsCleared.add("AI and Decision Sciences");
		subjectsCleared.add("Big Data: An overview of Big Data and Hadoop ecosystems");
		subjectsCleared.add("Advanced Python Programming");
		subjectsCleared.add("Text mining and Natural Language Processing Deep learning for NLP");
		subjectsCleared.add("Business Communication and Presentation Skills for Data Analytics");
		subjectsCleared.add("Economics for Analysts");
		subjectsCleared.add("Business Law and Ethics");
		subjectsCleared.add("Behavioural Science and Analytics");
		subjectsCleared.add("Digital and Social Media Analytics");
		subjectsCleared.add("Product Management");
		subjectsCleared.add("Project Management");
		subjectsCleared.add("Architecting Enterprise Applications and Design Thinking-2");
		subjectsCleared.add("Product Deployment Bootcamp");
		//subjectsCleared.add("Quantitative Research Methods");
		
		List<String> clearedQ7AndQ8Subjects = new ArrayList<>();
		clearedQ7AndQ8Subjects.add("Product Deployment Bootcamp");
		clearedQ7AndQ8Subjects.add("Quantitative Research Methods");
		
		final boolean expected = true;
		boolean actual = false;
		
		try
		{
			//Decreased the number of cleared subjects from list `subjectsCleared`
			when(serviceRequestDao.getSubjectsClearedCurrentProgramMBAWX(sr.getSapId())).thenReturn(subjectsCleared);
			when(serviceRequestDao.getProgramStatusOfStudent(sr.getSapId())).thenReturn(null);
			when(serviceRequestDao.noOfSubjectsToClear(sr.getSapId())).thenReturn(27);
			when(serviceRequestDao.getClearedPssIdForQ7(sr.getSapId())).thenReturn(q7PssIds);
			when(serviceRequestDao.getClearedPssIdForQ8(sr.getSapId())).thenReturn(q8PssIds);
			when(serviceRequestDao.getSubjectNameByPssIds(clearedQ7AndQ8PssIdList)).thenReturn(clearedQ7AndQ8Subjects);
			when(serviceRequestDao.getDiplomaIssuedCount(sr)).thenReturn(0);
			
			ServiceRequestStudentPortal response = checkFinalCertificateEligibilityMBAWX.checkFinalCertificateEligibility(sr);
			if(response != null)
			{
				actual = false;
			}
		}
		catch(Exception e)
		{
			actual = true;
		}
		
		assertEquals(expected,actual);
	}
	
	@Test
	public void checkFinalCertificateEligibilityProgramStatusAndNotClosed()
	{
		ServiceRequestStudentPortal sr = new ServiceRequestStudentPortal();
		sr.setSapId("77421924980");
		
		List<Integer> q7PssIds = Arrays.asList(1990);
		List<Integer> q8PssIds = Arrays.asList(1991);
		
		List<Integer> clearedQ7AndQ8PssIdList = new ArrayList<Integer>();
		clearedQ7AndQ8PssIdList.addAll(q7PssIds);
		clearedQ7AndQ8PssIdList.addAll(q8PssIds);
		
		List<String> subjectsCleared = new ArrayList<>();
		subjectsCleared.add("Foundations of Probability and Statistics for Data Science");
		subjectsCleared.add("Data Structures and Algorithms");
		subjectsCleared.add("Advanced Mathematical Analysis for Data Science");
		subjectsCleared.add("Essential Engineering Skills in Big Data Analytics Using R and Python");
		subjectsCleared.add("Statistics and Probability in Decision Modeling- 1");
		subjectsCleared.add("Statistics and Probability in Decision Modeling -2");
		subjectsCleared.add("Advanced Data Structures and Algorithms");
		subjectsCleared.add("The Art and Science of Storytelling and Visualization & Design Thinking-1");
		subjectsCleared.add("Methods and Algorithms in Machine Learning -1");
		subjectsCleared.add("Methods and Algorithms in Machine Learning -2");
		subjectsCleared.add("Methods and Algorithms in Machine Learning -3");
		subjectsCleared.add("AI and Decision Sciences");
		subjectsCleared.add("Big Data: An overview of Big Data and Hadoop ecosystems");
		subjectsCleared.add("Advanced Python Programming");
		subjectsCleared.add("Text mining and Natural Language Processing Deep learning for NLP");
		subjectsCleared.add("Business Communication and Presentation Skills for Data Analytics");
		subjectsCleared.add("Economics for Analysts");
		subjectsCleared.add("Business Law and Ethics");
		subjectsCleared.add("Behavioural Science and Analytics");
		subjectsCleared.add("Digital and Social Media Analytics");
		subjectsCleared.add("Product Management");
		subjectsCleared.add("Project Management");
		subjectsCleared.add("Architecting Enterprise Applications and Design Thinking-2");
		subjectsCleared.add("Product Deployment Bootcamp");
		subjectsCleared.add("Quantitative Research Methods");
		
		List<String> clearedQ7AndQ8Subjects = new ArrayList<>();
		clearedQ7AndQ8Subjects.add("Product Deployment Bootcamp");
		clearedQ7AndQ8Subjects.add("Quantitative Research Methods");
		
		final boolean expected = true;
		boolean actual = false;
		
		try
		{
			when(serviceRequestDao.getSubjectsClearedCurrentProgramMBAWX(sr.getSapId())).thenReturn(subjectsCleared);
			when(serviceRequestDao.getProgramStatusOfStudent(sr.getSapId())).thenReturn("Program Withdrawal");
			when(serviceRequestDao.noOfSubjectsToClear(sr.getSapId())).thenReturn(27);
			when(serviceRequestDao.getClearedPssIdForQ7(sr.getSapId())).thenReturn(q7PssIds);
			when(serviceRequestDao.getClearedPssIdForQ8(sr.getSapId())).thenReturn(q8PssIds);
			when(serviceRequestDao.getSubjectNameByPssIds(clearedQ7AndQ8PssIdList)).thenReturn(clearedQ7AndQ8Subjects);	
			//SR not closed
			when(serviceRequestDao.getStudentsClosedExitSR(sr.getSapId())).thenReturn(false);
			//First time raising SR
			when(serviceRequestDao.getDiplomaIssuedCount(sr)).thenReturn(0);
			
			ServiceRequestStudentPortal response = checkFinalCertificateEligibilityMBAWX.checkFinalCertificateEligibility(sr);
			if(response != null)
			{
				actual = false;
			}
		}
		catch(Exception e)
		{
			actual = true;
		}
		
		assertEquals(expected,actual);
	}
	
	@Test
	public void checkFinalCertificateEligibilityProgramStatusAndClosedAndFirstRaise()
	{
		ServiceRequestStudentPortal sr = new ServiceRequestStudentPortal();
		sr.setSapId("77421924980");
		
		List<Integer> q7PssIds = Arrays.asList(1990);
		List<Integer> q8PssIds = Arrays.asList(1991);
		
		List<Integer> clearedQ7AndQ8PssIdList = new ArrayList<Integer>();
		clearedQ7AndQ8PssIdList.addAll(q7PssIds);
		clearedQ7AndQ8PssIdList.addAll(q8PssIds);
		
		List<String> subjectsCleared = new ArrayList<>();
		subjectsCleared.add("Foundations of Probability and Statistics for Data Science");
		subjectsCleared.add("Data Structures and Algorithms");
		subjectsCleared.add("Advanced Mathematical Analysis for Data Science");
		subjectsCleared.add("Essential Engineering Skills in Big Data Analytics Using R and Python");
		subjectsCleared.add("Statistics and Probability in Decision Modeling- 1");
		subjectsCleared.add("Statistics and Probability in Decision Modeling -2");
		subjectsCleared.add("Advanced Data Structures and Algorithms");
		subjectsCleared.add("The Art and Science of Storytelling and Visualization & Design Thinking-1");
		subjectsCleared.add("Methods and Algorithms in Machine Learning -1");
		subjectsCleared.add("Methods and Algorithms in Machine Learning -2");
		subjectsCleared.add("Methods and Algorithms in Machine Learning -3");
		subjectsCleared.add("AI and Decision Sciences");
		subjectsCleared.add("Big Data: An overview of Big Data and Hadoop ecosystems");
		subjectsCleared.add("Advanced Python Programming");
		subjectsCleared.add("Text mining and Natural Language Processing Deep learning for NLP");
		subjectsCleared.add("Business Communication and Presentation Skills for Data Analytics");
		subjectsCleared.add("Economics for Analysts");
		subjectsCleared.add("Business Law and Ethics");
		subjectsCleared.add("Behavioural Science and Analytics");
		subjectsCleared.add("Digital and Social Media Analytics");
		subjectsCleared.add("Product Management");
		subjectsCleared.add("Project Management");
		subjectsCleared.add("Architecting Enterprise Applications and Design Thinking-2");
		subjectsCleared.add("Product Deployment Bootcamp");
		subjectsCleared.add("Quantitative Research Methods");
		
		List<String> clearedQ7AndQ8Subjects = new ArrayList<>();
		clearedQ7AndQ8Subjects.add("Product Deployment Bootcamp");
		clearedQ7AndQ8Subjects.add("Quantitative Research Methods");
		
		final boolean expected = true;
		boolean actual = false;
		
		try
		{
			when(serviceRequestDao.getSubjectsClearedCurrentProgramMBAWX(sr.getSapId())).thenReturn(subjectsCleared);
			when(serviceRequestDao.getProgramStatusOfStudent(sr.getSapId())).thenReturn("Program Withdrawal");
			when(serviceRequestDao.noOfSubjectsToClear(sr.getSapId())).thenReturn(27);
			when(serviceRequestDao.getClearedPssIdForQ7(sr.getSapId())).thenReturn(q7PssIds);
			when(serviceRequestDao.getClearedPssIdForQ8(sr.getSapId())).thenReturn(q8PssIds);
			when(serviceRequestDao.getSubjectNameByPssIds(clearedQ7AndQ8PssIdList)).thenReturn(clearedQ7AndQ8Subjects);
			//Closed SR
			when(serviceRequestDao.getStudentsClosedExitSR(sr.getSapId())).thenReturn(true);
			//First time raising SR
			when(serviceRequestDao.getDiplomaIssuedCount(sr)).thenReturn(0);
			
			ServiceRequestStudentPortal response = checkFinalCertificateEligibilityMBAWX.checkFinalCertificateEligibility(sr);
			if(response != null)
			{
				actual = true;
			}
		}
		catch(Exception e)
		{
			actual = false;
		}
		
		assertEquals(expected,actual);
	}
	
	@Test
	public void checkFinalCertificateEligibilityProgramStatusAndClosedAndSecondRaise()
	{
		ServiceRequestStudentPortal sr = new ServiceRequestStudentPortal();
		sr.setSapId("77421924980");
		
		List<Integer> q7PssIds = Arrays.asList(1990);
		List<Integer> q8PssIds = Arrays.asList(1991);
		
		List<Integer> clearedQ7AndQ8PssIdList = new ArrayList<Integer>();
		clearedQ7AndQ8PssIdList.addAll(q7PssIds);
		clearedQ7AndQ8PssIdList.addAll(q8PssIds);
		
		List<String> subjectsCleared = new ArrayList<>();
		subjectsCleared.add("Foundations of Probability and Statistics for Data Science");
		subjectsCleared.add("Data Structures and Algorithms");
		subjectsCleared.add("Advanced Mathematical Analysis for Data Science");
		subjectsCleared.add("Essential Engineering Skills in Big Data Analytics Using R and Python");
		subjectsCleared.add("Statistics and Probability in Decision Modeling- 1");
		subjectsCleared.add("Statistics and Probability in Decision Modeling -2");
		subjectsCleared.add("Advanced Data Structures and Algorithms");
		subjectsCleared.add("The Art and Science of Storytelling and Visualization & Design Thinking-1");
		subjectsCleared.add("Methods and Algorithms in Machine Learning -1");
		subjectsCleared.add("Methods and Algorithms in Machine Learning -2");
		subjectsCleared.add("Methods and Algorithms in Machine Learning -3");
		subjectsCleared.add("AI and Decision Sciences");
		subjectsCleared.add("Big Data: An overview of Big Data and Hadoop ecosystems");
		subjectsCleared.add("Advanced Python Programming");
		subjectsCleared.add("Text mining and Natural Language Processing Deep learning for NLP");
		subjectsCleared.add("Business Communication and Presentation Skills for Data Analytics");
		subjectsCleared.add("Economics for Analysts");
		subjectsCleared.add("Business Law and Ethics");
		subjectsCleared.add("Behavioural Science and Analytics");
		subjectsCleared.add("Digital and Social Media Analytics");
		subjectsCleared.add("Product Management");
		subjectsCleared.add("Project Management");
		subjectsCleared.add("Architecting Enterprise Applications and Design Thinking-2");
		subjectsCleared.add("Product Deployment Bootcamp");
		subjectsCleared.add("Quantitative Research Methods");
		
		List<String> clearedQ7AndQ8Subjects = new ArrayList<>();
		clearedQ7AndQ8Subjects.add("Product Deployment Bootcamp");
		clearedQ7AndQ8Subjects.add("Quantitative Research Methods");
		
		final boolean expected = true;
		boolean actual = false;
		
		try
		{
			when(serviceRequestDao.getSubjectsClearedCurrentProgramMBAWX(sr.getSapId())).thenReturn(subjectsCleared);
			when(serviceRequestDao.getProgramStatusOfStudent(sr.getSapId())).thenReturn("Program Withdrawal");
			when(serviceRequestDao.noOfSubjectsToClear(sr.getSapId())).thenReturn(27);
			when(serviceRequestDao.getClearedPssIdForQ7(sr.getSapId())).thenReturn(q7PssIds);
			when(serviceRequestDao.getClearedPssIdForQ8(sr.getSapId())).thenReturn(q8PssIds);
			when(serviceRequestDao.getSubjectNameByPssIds(clearedQ7AndQ8PssIdList)).thenReturn(clearedQ7AndQ8Subjects);
			//Closed SR
			when(serviceRequestDao.getStudentsClosedExitSR(sr.getSapId())).thenReturn(true);
			//Already Raised
			when(serviceRequestDao.getDiplomaIssuedCount(sr)).thenReturn(1);
			
			ServiceRequestStudentPortal response = checkFinalCertificateEligibilityMBAWX.checkFinalCertificateEligibility(sr);
			if(response != null)
			{
				actual = true;
			}
		}
		catch(Exception e)
		{
			actual = false;
		}
		
		assertEquals(expected,actual);
	}
}
