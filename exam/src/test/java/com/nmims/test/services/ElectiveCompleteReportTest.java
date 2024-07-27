package com.nmims.test.services;

import static org.junit.Assert.assertEquals;

import java.util.ArrayList;
import java.util.HashMap;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import static org.mockito.Mockito.when;
import org.springframework.test.context.junit4.SpringRunner;

import com.nmims.beans.Specialisation;
import com.nmims.daos.SpecialisationDAO;
import com.nmims.services.SpecialisationService;

@RunWith(SpringRunner.class)
public class ElectiveCompleteReportTest {
	
	@InjectMocks
	SpecialisationService service;
	
	@Mock
	SpecialisationDAO specialisationDAO;
	
	@Test
	public void testElectiveCompleteReportInputWithEmptyValue() {
		System.out.println("testElectiveCompleteReportInputWithEmptyValue Testcase Started");
		int expected = 1;
		int actual = 0;
		try {
			ArrayList<Specialisation> result = new ArrayList<Specialisation>();
			Specialisation bean = new Specialisation();
			bean.setAcadMonth("Jan");
			bean.setAcadYear("2023");
			bean.setTerm("3");
			ArrayList<String> users = new ArrayList<String>();
			when(specialisationDAO.getAllRegistrationDetails(bean.getAcadMonth(), bean.getAcadYear(), bean.getTerm(),bean.getConsumerProgramStructureId())).thenReturn(users);
			result = service.electiveCompleteProdReport(bean.getAcadMonth(), bean.getAcadYear(), bean.getTerm(),bean.getConsumerProgramStructureId());
			if(result.size() == 0)
				actual = 1;
		}catch(Exception e) {
			e.printStackTrace();
			
		}
		assertEquals(expected, actual);
		System.out.println("testElectiveCompleteReportInputWithEmptyValue Testcase Ended");
	}
	
	@Test
	public void testElectiveReportForNonEmptyOutput() {
		System.out.println("testElectiveReportForNonEmptyOutput Testcase Started");
		int expected = 1;
		int actual = 0;
		try {
			ArrayList<Specialisation> result = new ArrayList<Specialisation>();
			HashMap<String,Specialisation> studentResult = new HashMap<String,Specialisation>();
			
			result = createBeanElectiveReportWithOutput();
			studentResult = createStudentDetailsDataForNonEmptyOutput();
			
			Specialisation bean = new Specialisation();
			bean.setAcadMonth("Jan");
			bean.setAcadYear("2023");
			bean.setTerm("3");
			bean.setConsumerProgramStructureId("111");
			
			ArrayList<String> users = new ArrayList<String>();
			users.add("77121293957");
			users.add("77121612969");
			users.add("77121862746");
			
			when(specialisationDAO.getAllRegistrationDetails(bean.getAcadMonth(), bean.getAcadYear(), bean.getTerm(),bean.getConsumerProgramStructureId())).thenReturn(users);
			when(specialisationDAO.getAllStudentSpecializationDetails(users,bean.getAcadMonth(), bean.getAcadYear())).thenReturn(result);
			when(specialisationDAO.getStudentDetails(users)).thenReturn(studentResult);
			
			result = service.electiveCompleteProdReport(bean.getAcadMonth(), bean.getAcadYear(), bean.getTerm(),bean.getConsumerProgramStructureId());
			
		
			if(result.size() > 0)
				actual = 1;
		}catch(Exception e) {
			e.printStackTrace();
			
		}
		assertEquals(expected, actual);
		System.out.println("testElectiveReportForNonEmptyOutput Testcase Ended");
		
		
	}

	
	@Test
	public void testElectiveReportForEmptyOutput() {
		System.out.println("testElectiveReportForEmptyOutput Testcase Started");
		int expected = 1;
		int actual = 0;
		try {
			ArrayList<Specialisation> result = new ArrayList<Specialisation>();
			HashMap<String,Specialisation> studentResult = new HashMap<String,Specialisation>();
			
			studentResult = createStudentDetailsDataForEmptyOutput();
			
			Specialisation bean = new Specialisation();
			bean.setAcadMonth("Jan");
			bean.setAcadYear("2022");
			bean.setTerm("3");
			bean.setConsumerProgramStructureId("111");
			
			ArrayList<String> users = new ArrayList<String>();
			users.add("77121448739");
			users.add("77121741327");
			users.add("77219837235");
			
			when(specialisationDAO.getAllRegistrationDetails(bean.getAcadMonth(), bean.getAcadYear(), bean.getTerm(),bean.getConsumerProgramStructureId())).thenReturn(users);
			when(specialisationDAO.getAllStudentSpecializationDetails(users,bean.getAcadMonth(), bean.getAcadYear())).thenReturn(result);
			when(specialisationDAO.getStudentDetails(users)).thenReturn(studentResult);
			
			result = service.electiveCompleteProdReport(bean.getAcadMonth(), bean.getAcadYear(), bean.getTerm(),bean.getConsumerProgramStructureId());
			
		
			if(result.size() == 0)
				actual = 1;
		}catch(Exception e) {
			e.printStackTrace();
			
		}
		assertEquals(expected, actual);
		System.out.println("testElectiveReportForEmptyOutput Testcase Ended");
		
		
	}
	
	
	public ArrayList<Specialisation> createBeanElectiveReportWithOutput(){
		ArrayList<Specialisation> result = new ArrayList<Specialisation>();
		Specialisation bean = new Specialisation();
		bean.setTimeBoundId("1494");
		bean.setSapid("77121862746");
		bean.setSpecializationType("10");
		bean.setProgram_sem_subject_id("1554");
		result.add(bean);
		
		Specialisation bean1 = new Specialisation();
		bean1.setTimeBoundId("1497");
		bean1.setSapid("77121862746");
		bean1.setSpecializationType("10");
		bean1.setProgram_sem_subject_id("1732");
		result.add(bean1);
		
		Specialisation bean2 = new Specialisation();
		bean2.setTimeBoundId("1506");
		bean2.setSapid("77121862746");
		bean2.setSpecializationType("9");
		bean2.setProgram_sem_subject_id("2241");
		result.add(bean2);
		
		Specialisation bean3 = new Specialisation();
		bean3.setTimeBoundId("1508");
		bean3.setSapid("77121862746");
		bean3.setSpecializationType("9");
		bean3.setProgram_sem_subject_id("1549");
		result.add(bean3);
		
		Specialisation bean4 = new Specialisation();
		bean4.setTimeBoundId("1512");
		bean4.setSapid("77121862746");
		bean4.setSpecializationType("9");
		bean4.setProgram_sem_subject_id("2239");
		result.add(bean4);
		
		return result;
	}

	public HashMap<String,Specialisation> createStudentDetailsDataForNonEmptyOutput(){
		HashMap<String,Specialisation> result = new HashMap<String,Specialisation>();
		Specialisation bean = new Specialisation();
		bean.setName("Abhishek Malpani");
		bean.setEmailId("test@gmail.com");
		bean.setMobile("0");
		result.put("77121293957",bean);
		
		Specialisation bean1 = new Specialisation();
		bean1.setName("Priyanka Kumar");
		bean1.setEmailId("test@gmail.com");
		bean1.setMobile("0");
		result.put("77121612969",bean1);
		
		Specialisation bean2 = new Specialisation();
		bean2.setName("Himani Sharma");
		bean2.setEmailId("test@gmail.com");
		bean2.setMobile("0");
		result.put("77121862746",bean2);
		
		return result;
	}
	
	public HashMap<String,Specialisation> createStudentDetailsDataForEmptyOutput(){
		HashMap<String,Specialisation> result = new HashMap<String,Specialisation>();
		Specialisation bean = new Specialisation();
		bean.setName("Pritha Sanyal");
		bean.setEmailId("test@gmail.com");
		bean.setMobile("0");
		result.put("77121448739",bean);
		
		Specialisation bean1 = new Specialisation();
		bean1.setName("Akshay Bele");
		bean1.setEmailId("test@gmail.com");
		bean1.setMobile("0");
		result.put("77121741327",bean1);
		
		Specialisation bean2 = new Specialisation();
		bean2.setName("Sakshi Verma");
		bean2.setEmailId("test@gmail.com");
		bean2.setMobile("0");
		result.put("77219837235",bean2);
		
		return result;
	}
	
}
