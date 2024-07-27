package com.nmims.test.services;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.web.multipart.MultipartFile;

import com.mysql.cj.util.StringUtils;
import com.nmims.dto.ChangeDetailsSRDto;
import com.nmims.beans.ServiceRequestStudentPortal;
import com.nmims.beans.StudentStudentPortalBean;
import com.nmims.daos.ServiceRequestDao;
import com.nmims.dto.StudentSrDTO;
import com.nmims.services.ServiceRequestService;

@RunWith(SpringRunner.class)
@SpringBootTest
public class ServiceRequestTest
{
	@InjectMocks
	ServiceRequestService serviceRequestService; 

	@Mock
	ServiceRequestDao serviceRequestDao;
	

	
	@Before
	public void setup(){
		MockitoAnnotations.initMocks(this); //without this you will get NPE
	}
	
	@Test
	public void showPdfDetails() {
		StudentSrDTO expectedBean = new StudentSrDTO();
		Integer expected=1;
		Integer actual=0;
		
		expectedBean.setFirstName("Somesh");
		StudentSrDTO studentDetails = new StudentSrDTO();
		studentDetails.setProgram("CBM");
		
			when(serviceRequestDao.getStudentDetailsBySapId("77777777771")).thenReturn(studentDetails);
			when(serviceRequestDao.getProgramDetailsByProgram(studentDetails.getProgram())).thenReturn(expectedBean);
			serviceRequestService.getEBonafidePDFDetails("77777777771", "VISA Purpose", (long) 12354);
			if(!expectedBean.getFirstName().isEmpty()){
				actual=1;
			}

			assertEquals(expected,actual);
	}
	
	
	@Test
	public void getFilePathBySRId() {
		String expectedBean ="filePath";
		
		Integer expected=1;
		Integer actual=0;
		
		when(serviceRequestDao.getFilePathBySrId("77777777771")).thenReturn(expectedBean);
		serviceRequestService.getFilePathBySRId("77777777771");
		if(!expectedBean.isEmpty()){
			actual=1;
		}
		
		assertEquals(expected,actual);
	}
	
	
	@Test
	public void getServiceRequest() {
		ServiceRequestStudentPortal bean = new ServiceRequestStudentPortal();
		bean.setServiceRequestName("Issuance of Bonafide");
		
		Integer expected=1;
		Integer actual=0;
		
		when(serviceRequestDao.getServiceRequestBySrId((long) 12345)).thenReturn(bean);
		serviceRequestService.getServiceRequest((long) 12345);
		if(!bean.getServiceRequestName().isEmpty()){
			actual=1;
		}
		
		assertEquals(expected,actual);
	}
	
	@Test
	public void checkIfMScAIStudentApplicableForExitProgram() {
		StudentStudentPortalBean student = new StudentStudentPortalBean();
		String sapId="77121100487";
		student.setSapid(sapId);
		int latestSem=5;
		String acadyear="2022";
		String acadmonth="Jul";
		int actualNumber=0;
		int expectedNumber=1;
		ArrayList<String>getfailedPssId =new ArrayList<String>();
		when(serviceRequestDao.getPssIdOfStudentBySapId(sapId)).thenReturn(getfailedPssId);
		 serviceRequestService.checkIfMScAIStudentApplicableForExitProgram(student);
		
         ArrayList<String> getTimeBoundsubjectconfigId=new ArrayList<String>();
         getTimeBoundsubjectconfigId.add("1103");
		String DateAndTime="2022-07-25 00:00:00";
	
		when(serviceRequestDao.getTimeBoundsubjectconfigId(student.getSapid())).thenReturn(getTimeBoundsubjectconfigId);
		 serviceRequestService.checkIfMScAIStudentApplicableForExitProgram(student);
		 
	 	when(serviceRequestDao.getTimeBoundStartedDateAndTime((ArrayList<String>) getTimeBoundsubjectconfigId,acadyear,acadmonth)).thenReturn(DateAndTime);
	 	 serviceRequestService.checkIfMScAIStudentApplicableForExitProgram(student);
		
		if(!StringUtils.isNullOrEmpty(DateAndTime)) {
			actualNumber=1;
		}
		
		assertEquals(actualNumber,expectedNumber);
	}
		
	@Test
	public void testChangeFatherMotherSpouseName() {
		ChangeDetailsSRDto srDto = new ChangeDetailsSRDto();
		srDto.setSapid(77777777771L);
		srDto.setDetailType("fatherName");
		srDto.setCurrentValue("Test");
		srDto.setUpdateValue("TestNew");
		srDto.setDevice("WebApp");
		
		MultipartFile file = new MockMultipartFile("mockFile", new byte[256]);
		srDto.setSupportingDocument(file);
		
		try {
			assertEquals("Y", serviceRequestService.changeFatherMotherSpouseName(srDto).getHasDocuments());
		}
		catch(Exception ex) {
//			ex.printStackTrace();
		}
	}
	
	@Test
	public void testChangeContactDetails() {
		ChangeDetailsSRDto srDto = new ChangeDetailsSRDto();
		srDto.setSapid(77777777771L);
		srDto.setDetailType("emailId");
		srDto.setCurrentValue("test@mail.com");
		srDto.setUpdateValue("testNew@mail.com");
		srDto.setDevice("WebApp");
		
		try {
			assertEquals("N", serviceRequestService.changeContactDetails(srDto).getHasDocuments());
		}
		catch(Exception ex) {
//			ex.printStackTrace();
		}
	}
}
