package com.nmims.test.services;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.multipart.MultipartFile;

import com.nmims.beans.ServiceRequestDocumentBean;
import com.nmims.beans.ServiceRequestStudentPortal;
import com.nmims.controllers.ServiceRequestController;
import com.nmims.daos.ServiceRequestDao;
import com.nmims.services.ServiceRequestService;

import javafx.beans.binding.When;

@RunWith(SpringRunner.class)
@SpringBootTest
public class SpecialNeedsSRTest {

	@Mock
	ServiceRequestDao Dao;
	
	@InjectMocks
	ServiceRequestService service;
	
	private MockMvc mockMvc;
	
	@Before
    public void setup() {
        this.mockMvc = MockMvcBuilders.standaloneSetup(new ServiceRequestController()).build();
    }
	
	@Test
	public void testAlreadySubmittedORClosedSR() throws Exception
	{
		Integer expected=1;
		Integer actual=0;
		List<ServiceRequestStudentPortal> expectBean1=new ArrayList<ServiceRequestStudentPortal>();
		ServiceRequestStudentPortal sr=new ServiceRequestStudentPortal();
		sr.setRequestStatus("Submitted");
		sr.setSapId("77777777771");
		sr.setServiceRequestType("Special Needs SR");
		expectBean1.add(sr);
		when(Dao.getSRBySapIdandtype("77777777771","Special Needs SR")).thenReturn(expectBean1);
		if(expectBean1.size()>0)
		{
			actual=1;
		}
		assertEquals(expected,actual);
		
	}
	@Test
	public void checkSRDocumetBysrId()
	{
		Integer expected=1;
		Integer actual=0;
		 List<ServiceRequestDocumentBean> expectBean=new ArrayList<ServiceRequestDocumentBean>();
		 ServiceRequestDocumentBean sr=new ServiceRequestDocumentBean();
		 sr.setDocumentName("Student medical proof for special need");
		expectBean.add(sr);
		when(Dao.getDocuments(Long.valueOf(165901))).thenReturn(expectBean);
		if(expectBean.size()>0)
		{
			actual=1;
		}
		assertEquals(expected,actual);
	}
	
	
	
}
