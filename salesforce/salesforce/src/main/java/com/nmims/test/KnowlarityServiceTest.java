package com.nmims.test;

import static org.junit.Assert.assertEquals;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import com.nmims.beans.KnowlarityData;
import com.nmims.services.KnowlarityService;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest
public class KnowlarityServiceTest {

	@Autowired
	KnowlarityService knowlarityService;
	
	@Test
	public void insertDataTest1()   //inserts correct data 
	{
		int expected=1;
		int actual;
		try 
		{
			KnowlarityData kd=new KnowlarityData();
			kd.setCall__c("Inbound Call");
			kd.setCall_date__c("11-05-2022");
			kd.setCall_time__c("17:50:39.000Z");
			kd.setCall_uuid__c("bea00035-cb5c-4cea-a2b7-afe792fb2d3e");
			kd.setCalled_number__c("+919833345766");
			kd.setCustomer_call_duration__c("0:00:09");
			kd.setCustomer_status__c("Connected");
			kd.setId("00T2j00000YqYTeEAN");
			kd.setMenu__c("None");
			kd.setOwnerid("0052j000000YMNp");
			kd.setPriority("Normal");
			kd.setPurpose__c("Enquiry");
			kd.setStatus("Not Started");
			kd.setSubject("Knowlarity Call");
			kd.setWhoid("00Q2j000009BVjQ");
			actual=knowlarityService.insertData(kd);
		}
		catch(Exception e)
		{
			actual=0;
		}
		assertEquals(expected, actual);
	}
	
	@Test
	public void insertDataTest2()  //inserts duplicate primary key
	{
		int expected=1;
		int actual;
		try 
		{
			KnowlarityData kd=new KnowlarityData();
			kd.setCall__c("Inbound Call");
			kd.setCall_date__c("11-05-2022");
			kd.setCall_time__c("17:50:39.000Z");
			kd.setCall_uuid__c("bea00035-cb5c-4cea-a2b7-afe792fb2d3e");
			kd.setCalled_number__c("+919833345766");
			kd.setCustomer_call_duration__c("0:00:09");
			kd.setCustomer_status__c("Connected");
			kd.setId("00T2j00000YqYTeEAN");
			kd.setMenu__c("None");
			kd.setOwnerid("0052j000000YMNp");
			kd.setPriority("Normal");
			kd.setPurpose__c("Enquiry");
			kd.setStatus("Not Started");
			kd.setSubject("Knowlarity Call");
			kd.setWhoid("00Q2j000009BVjQ");
			actual=knowlarityService.insertData(kd);
		}
		catch(Exception e)
		{
			actual=0;
		}
		assertEquals(expected, actual);
	}
	
}
