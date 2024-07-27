package com.nmims.test.services;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.when;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.test.context.junit4.SpringRunner;

import com.nmims.beans.ConsumerProgramStructureExam;
import com.nmims.daos.DashboardDAO;
import com.nmims.services.ConfigurationService;

@RunWith(SpringRunner.class)
public class ConfigurationServiceTest {
	
	@Mock
	DashboardDAO dashboardDao;
	
	@InjectMocks
	ConfigurationService configurationService;
	
	@Test
	public void updateMasterKeyDetails() {
		ConsumerProgramStructureExam bean=new ConsumerProgramStructureExam();
		bean.setId("1");
		bean.setConsumerTypeId("4");
		bean.setProgramId("1");
		bean.setProgramStructureId("1");
		bean.setHasPaidSessionApplicable("0");
		String expected="Success";
		String actual="";
		
		when(dashboardDao.getConsumerProgramStructure(bean)).thenReturn(0);
		when(dashboardDao.updateConsumerProgramStructureMappingList(bean)).thenReturn("true");
		ConsumerProgramStructureExam returnBean=configurationService.updateMasterKeyDetails(bean);
		actual = returnBean.getStatus();
		assertEquals(expected, actual);
	}

}
