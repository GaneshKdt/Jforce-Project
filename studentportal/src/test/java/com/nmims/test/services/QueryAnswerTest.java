package com.nmims.test.services;

import static org.junit.Assert.*;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.test.context.junit4.SpringRunner;

import com.nmims.beans.SessionQueryAnswerStudentPortal;
import com.nmims.beans.StudentStudentPortalBean;
import com.nmims.daos.PortalDao;
import com.nmims.services.QueryAnswerService;

@RunWith(SpringRunner.class)
@SpringBootTest
public class QueryAnswerTest {
	
	@InjectMocks
	QueryAnswerService qnaservice;
	
	@Mock
	PortalDao portalDAO;
	
	@Before
	public void setup(){
	    MockitoAnnotations.initMocks(this);
	}

	@Test
	public void getCourseQueriesMap() {
		SessionQueryAnswerStudentPortal query1=new SessionQueryAnswerStudentPortal();
		query1.setId("1");
		query1.setSapId("");
		query1.setQuery("Test Query");
		query1.setIsPublic("Y");
		
		SessionQueryAnswerStudentPortal query2=new SessionQueryAnswerStudentPortal();
		query2.setId("1");
		query2.setSapId("");
		query2.setQuery("Test Query");
		query2.setIsPublic("Y");
		
		List<String> pssIdList = Arrays.asList("1234","1564");

		String sapId="";
		String subject="Business Communication";
		String programSemSubjectId="";
		StudentStudentPortalBean reg=new StudentStudentPortalBean();
		
		MockHttpServletRequest request = new MockHttpServletRequest();
				
		int expectedPublicQueryCount=2;
		int actualPublicQueryCount;
		
		List<SessionQueryAnswerStudentPortal>  myQueries=new ArrayList<SessionQueryAnswerStudentPortal>();
		List<SessionQueryAnswerStudentPortal> publicQueries = new ArrayList<SessionQueryAnswerStudentPortal>();
		List<SessionQueryAnswerStudentPortal> testList=new ArrayList<SessionQueryAnswerStudentPortal>();
		testList.add(query1);
		testList.add(query2);
		System.out.println("testList"+testList.size());
		
		when(portalDAO.getQueriesForSessionByStudentV2(sapId,"1235")).thenReturn(testList);
		when(portalDAO.getPssIdBySubjectCodeId("119")).thenReturn(pssIdList);
		//when(portalDAO.getPublicQueriesForCourseV2(sapId, pssIdList, "2022", "Jan")).thenReturn(testList);
		when(qnaservice.getPublicCourseQueries(subject, sapId, "1234", reg.getAcadYear(), reg.getMonth())).thenReturn(testList);
		HashMap<String, List<SessionQueryAnswerStudentPortal>> courseQueriesMap = qnaservice.getCourseQueriesMap(sapId,reg.getYear(),reg.getMonth(),subject,"1235");
		myQueries=courseQueriesMap.get("myQueries");
		publicQueries=courseQueriesMap.get("publicQueries");

		actualPublicQueryCount=publicQueries.size();
		assertEquals(expectedPublicQueryCount, actualPublicQueryCount);
		
	}

}
