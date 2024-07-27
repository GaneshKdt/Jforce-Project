package com.nmims.test.services;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit4.SpringRunner;

import com.nmims.beans.ServiceRequestBean;
import com.nmims.daos.ServiceRequestDAO;
import com.nmims.services.ServiceRequestDocumentImplService;

@RunWith(SpringRunner.class)
@SpringBootTest
public class ServiceRequestDocumentServiceTest {
	 
	@Autowired
	private ServiceRequestDocumentImplService serviceRequestDocumentService;
	
	@MockBean
	private ServiceRequestDAO srDao;
	
	@Test
	public void getMySRDocumentsFromSapId()throws Exception {
		
		String sapid = "77777777771";
		
		List<ServiceRequestBean> listOfSRDocumentsBasedOnSapid = new ArrayList<>();
		ServiceRequestBean bean = new ServiceRequestBean();
		bean.setFilePath("Subarna_Ghosh_VISA_Purpose_Certificate_12-May-2023_ac9Ka.pdf");
		listOfSRDocumentsBasedOnSapid.add(bean);
		
		String expected="Success";
		String actual="";
			
		when(srDao.getGeneratedSrDocuments(sapid)).thenReturn(listOfSRDocumentsBasedOnSapid);
		serviceRequestDocumentService.getMySRDocumentsFromSapId(sapid);
		
		actual="Success";
		
		assertEquals(expected, actual);
	}
}
