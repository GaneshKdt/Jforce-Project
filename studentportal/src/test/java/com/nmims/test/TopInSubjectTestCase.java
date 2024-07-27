package com.nmims.test;

import static org.mockito.Mockito.when;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import org.junit.runners.Parameterized.Parameters;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.ApplicationContext;
import org.springframework.test.context.junit4.SpringRunner;

import com.nmims.beans.OpenBadgeBean;
import com.nmims.beans.OpenBadgesCriteriaBean;
import com.nmims.beans.OpenBadgesCriteriaParamBean;
import com.nmims.beans.OpenBadgesEvidenceBean;
import com.nmims.beans.OpenBadgesIssuedBean;
import com.nmims.beans.OpenBadgesUsersBean;
import com.nmims.daos.OpenBadgesDAO;
import com.nmims.dto.OpenBadgesTopInsubjectDto;
import com.nmims.services.OpenBadgesService;

//@RunWith(SpringRunner.class)
//@SpringBootTest
public class TopInSubjectTestCase {
	
	//@InjectMocks
	// OpenBadgesService  openBadgeservice;
	
	//@Mock
	// OpenBadgesDAO  openBadgesDAO;
	
	//	@Test
	/*void callTopnInSubjectBadgeForAllStudent_test() {
		 String expected = "true";
		 String actual = "false";
		 
		 
		 List<OpenBadgesCriteriaParamBean>  Bean	= new ArrayList<OpenBadgesCriteriaParamBean>();
		 OpenBadgesCriteriaParamBean bean1 = new OpenBadgesCriteriaParamBean();
		 bean1.setBadgeId(53);
		 bean1.setCriteriaId(53);
		 bean1.setCriteriaName("topInSubject");
		 bean1.setCriteriaValue("4");
	     
		 Bean.add(bean1);
	     
	     
	  
	/*	
		 HashMap<String,String> response; response = new HashMap<String,String>();
		 int totalStudentsEntryPerBadge = 0;  
		 
		
	 
		when(openBadgesDAO.getCriteriaDetails("topInSubject")).thenReturn(Bean);
	
		try {
			
			int result_count =  processTopInSubjectBadgeForAllStudent(Bean).getBadgeId(), Integer.parseInt(Bean).getCriteriaValue()), totalStudentsEntryPerBadge)));
			response.put(" Bean.getBadgeId()" ,String.valueOf("result_count"));
			 totalStudentsEntryPerBadge = 0;
			 
			actual="True";
		 }catch(Exception e) {
			 response.put("Issued Student ",String.valueOf(totalStudentsEntryPerBadge));		
		 
		assertEquals(expected,actual);
		 
		 }
	}
	
	@Test
	void processTopInSubjectBadgeForAllStudent(Integer badgeId,Integer criteriaValue,int totalStudentsEntryPerBadge) {
		
		
		String masterkeys = "1,2,3,4,5,6,7,8,9,10,11,12,13,14,15";
		List<String> masterKeyIds = new ArrayList<String>();
		
		when(openBadgesDAO.getMasterKeyListByBadgeId(1)).thenReturn(masterKeyIds);
		
		List<OpenBadgesUsersBean>  Bean = new  ArrayList<OpenBadgesUsersBean>();
		OpenBadgesUsersBean bean1= new OpenBadgesUsersBean	();
		bean1.setUserId(3025);
		bean1.setSapid("77217943364");
		bean1.setConsumerProgramStructureId(44);
		Bean.add(bean1);
		
		
		
		List<OpenBadgesTopInsubjectDto>  open = new ArrayList<OpenBadgesTopInsubjectDto>();
		
		OpenBadgesTopInsubjectDto open1 =new OpenBadgesTopInsubjectDto();
		open1.setSubject(" Financial Accounting");
		open1.setTotal("51");
		open1.setOutOfMarks("100");
		open1.setRank("4");
		
		open.add(open1);
		 
		
		
	
		
		
		
		
		totalStudentsEntryPerBadge += studentsData1.size();
		if(.size() > 0) {
			
			popuateTopInSubjectIssuedAndEvidence(studentsData1,badgeId, masterKeyIds.getConsumerProgramStructureId());
		
	}
		
		assertEquals(true, studentsData1.size()>0);
	}	
		
		@Test
	void popuateTopInSubjectIssuedAndEvidencetest() {
		List<String> error_List = new ArrayList<String>();
		 String expected = "true";
		 String actual = "false";
		 
		 
		 try {
			 List<OpenBadgesTopInsubjectDto> studentData = new ArrayList<OpenBadgesTopInsubjectDto>();
		 	OpenBadgesEvidenceBean bean = new OpenBadgesEvidenceBean();
		 	actual="True";
		 
		 	
		 	bean.setAwardedAt("Financial Accounting");
		 	bean.setUserId(3025);
		 	bean.setBadgeId(53);
		 	bean.setTotal(51);
		 	bean.setOutOfMarks(100);
		 	bean.setrank("4");
			
			
			String inputMd5 = studentData.getBadgeId()+studentData.getAwardedAt()+studentData.getUserId();
	 		String uniquehash = getMd5("inputMd5") ;
	 		//studentData.setUniquehash(uniquehash);
	 		studentData.setCreatedBy("createBadgeIssuedEntry");
	 		studentData.setLastModifiedBy("createBadgeIssuedEntry");
	 		
			
		 	
	 		when(openBadgesDAO.insertBadgeIssued(studentData)).thenReturn(studentData);
			
			bean.setIssuedId(BigInteger.valueOf(issuedId));
			bean.setCreatedBy("TopInSubject");
			bean.setLastModifiedBy("TopINSubject");
			bean.setEvidenceValue("<p> Subject Name: "+studentData.getSubject() +" Total Marks: "+ studentData.getTotal() +" out of "+ studentData.getOutOfMarks()+ " Subject Topper - Rank : " +studentData.getRank()+" </p> ");
			bean.setEvidenceType("htmlbody");
			
			
			
			error_List.add("Error in Badge Issued Or Evidence Insertion For Student "+studentData.getUserId() + " For badgeId "+53);
			
			
			
			
		 }catch(Exception e) {
			
		 assertEquals(expected,actual);
	}
	
	
	}	
		
		*/	
		
}
		
		
		
	
		
		
		

		
		
		
		
		
		
		

	

	 


	 
