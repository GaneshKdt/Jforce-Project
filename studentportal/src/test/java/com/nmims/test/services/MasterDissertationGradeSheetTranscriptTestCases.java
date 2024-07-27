package com.nmims.test.services;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.when;

import java.util.ArrayList;

import javax.servlet.http.HttpServletRequest;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import com.nmims.beans.ServiceRequestStudentPortal;
import com.nmims.beans.StudentStudentPortalBean;
import com.nmims.daos.ServiceRequestDao;
import com.nmims.services.ServiceRequestService;

@RunWith(SpringRunner.class)
@SpringBootTest
public class MasterDissertationGradeSheetTranscriptTestCases {

	private static final HttpServletRequest HttpServletRequest = null;

	@InjectMocks
	ServiceRequestService srService;
	
	@Mock
	ServiceRequestDao serviceRequestDao;
	
	
	
	@Test
	public void ServiceRequestStudentPortal() {
		ServiceRequestStudentPortal sr = new ServiceRequestStudentPortal();
		
		sr.setSapId("777777777771");
		sr.setMonth("Jan");
		sr.setYear("2023");
		sr.setSem("8");
		
		StudentStudentPortalBean bean = new StudentStudentPortalBean();
		bean.setSapid("777777777771");
		bean.setSem("8");
		bean.setConsumerProgramStructureId("131");
		
		when(serviceRequestDao.getSingleStudentsData("777777777771")).thenReturn(bean);
		when(serviceRequestDao.checkSapidExistForQ8("777777777771")).thenReturn(1);
		String message ="";
		ArrayList<String> list = new ArrayList<>();
		list.add(message);
		when(serviceRequestDao.getSubjectsAppearedForSemesterMessageListForMBAWX(sr)).thenReturn(list);
		ArrayList<String> resultDeclaredMessage = new ArrayList<String>();
		when(serviceRequestDao.resultDeclaredMessageForMBAWX(sr)).thenReturn(resultDeclaredMessage);
		when(serviceRequestDao.getMarksheetPrintedCount(sr)).thenReturn(resultDeclaredMessage);
		
		ServiceRequestStudentPortal srReturn = srService.checkMarksheetHistoryForMBAWX(sr, null);
		boolean expected = true;
		boolean result = false;
		if(srReturn.getSapId().equalsIgnoreCase("777777777771")) {
			if(srReturn.getSem().equals("8")) {
			result = true;	
			}
		}
		
		assertEquals(expected, result);
		
		
	}
	
	@Test
	public void ServiceRequestStudentPortalWithNull() {
		ServiceRequestStudentPortal sr = new ServiceRequestStudentPortal();;
		sr.setSapId("777777777771");
		sr.setMonth("Jan");
		sr.setYear("2023");
		sr.setSem("8");
		StudentStudentPortalBean bean = new StudentStudentPortalBean();
		bean.setSapid("777777777771");
		bean.setSem("8");
		bean.setConsumerProgramStructureId("131");
		
		when(serviceRequestDao.getSingleStudentsData("777777777771")).thenReturn(bean);
		when(serviceRequestDao.checkSapidExistForQ8(null)).thenReturn(0);
		String message ="";
		ArrayList<String> list = new ArrayList<>();
		list.add(message);
		when(serviceRequestDao.getSubjectsAppearedForSemesterMessageListForMBAWX(sr)).thenReturn(list);
		ArrayList<String> resultDeclaredMessage = new ArrayList<String>();
		when(serviceRequestDao.resultDeclaredMessageForMBAWX(sr)).thenReturn(resultDeclaredMessage);
		when(serviceRequestDao.getMarksheetPrintedCount(sr)).thenReturn(resultDeclaredMessage);
		
		ServiceRequestStudentPortal srReturn = srService.checkMarksheetHistoryForMBAWX(sr, null);
		boolean expected = true;
		boolean result = false;
		System.out.println("srReturn"+srReturn);
		if(srReturn.getSapId().equalsIgnoreCase("777777777771")) {
			result = true;	
		
		}
		
		assertEquals(expected, result);
		
		
	}
	

}
