package com.nmims.test.services;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import org.junit.Test;
import org.junit.runner.RunWith;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit4.SpringRunner;

import com.nmims.beans.StudentRankBean;
import com.nmims.daos.LeaderBoardDAO;
import com.nmims.services.LeaderBoardService;

@RunWith(SpringRunner.class)
@SpringBootTest
public class LeaderBoardServiceTest {

	@Autowired
	private LeaderBoardService leaderBoardService;
	
	@MockBean
	private LeaderBoardDAO leaderBoardDAO;
	
	

	@Test
	public void getSubjectWiseRankConfigList()throws Exception {
		
		//Registraion
		List<StudentRankBean> registrations = new ArrayList<>();
		
		StudentRankBean registration = new StudentRankBean();
		registration.setSem("1");
		registration.setConsumerProgramStructureId("128");
		registration.setYear("2020");
		registration.setMonth("Jul");
		
		StudentRankBean registration1 = new StudentRankBean();
		registration1.setSem("2");
		registration1.setConsumerProgramStructureId("128");
		registration1.setYear("2021");
		registration1.setMonth("Jan");
		
		StudentRankBean registration2 = new StudentRankBean();
		registration2.setSem("3");
		registration2.setConsumerProgramStructureId("128");
		registration2.setYear("2021");
		registration2.setMonth("Jul");
		
		StudentRankBean registration3 = new StudentRankBean();
		registration3.setSem("4");
		registration3.setConsumerProgramStructureId("128");
		registration3.setYear("2022");
		registration3.setMonth("Jan");
		
		StudentRankBean registration4 = new StudentRankBean();
		registration4.setSem("5");
		registration4.setConsumerProgramStructureId("128");
		registration4.setYear("2022");
		registration4.setMonth("Jul");
		
		StudentRankBean registration5 = new StudentRankBean();
		registration5.setSem("6");
		registration5.setConsumerProgramStructureId("128");
		registration5.setYear("2023");
		registration5.setMonth("Jan");
		
		registrations.add(registration);
		registrations.add(registration1);
		registrations.add(registration2);
		registrations.add(registration3);
		registrations.add(registration4);
		registrations.add(registration5);
		
		
		//Examorder
		List<StudentRankBean> examorders = new ArrayList<>();
		
		StudentRankBean examorder = new StudentRankBean();		
		examorder.setMonth("Jun");
		examorder.setAcadMonth("Jan");
		examorder.setYear("2023");
		
		StudentRankBean examorder1 = new StudentRankBean();
		examorder1.setMonth("Dec");
		examorder1.setAcadMonth("Jul");
		examorder1.setYear("2022");
		
		StudentRankBean examorder2 = new StudentRankBean();
		examorder2.setMonth("Jun");
		examorder2.setAcadMonth("Jan");
		examorder2.setYear("2022");
		
		StudentRankBean examorder3 = new StudentRankBean();
		examorder3.setMonth("Dec");
		examorder3.setAcadMonth("Jul");
		examorder3.setYear("2021");
		
		StudentRankBean examorder4 = new StudentRankBean();
		examorder4.setMonth("Jun");
		examorder4.setAcadMonth("Jan");
		examorder4.setYear("2021");
		
		StudentRankBean examorder5 = new StudentRankBean();
		examorder5.setMonth("Dec");
		examorder5.setAcadMonth("Jul");
		examorder5.setYear("2020");
		
		examorders.add(examorder);
		examorders.add(examorder1);
		examorders.add(examorder2);
		examorders.add(examorder3);
		examorders.add(examorder4);
		examorders.add(examorder5);
		
		
		//Subject Mapping
		List<StudentRankBean> subjectMapping = new ArrayList<>();
		StudentRankBean subject = new StudentRankBean();
		subject.setSem("5");
		subject.setConsumerProgramStructureId("128");
		subject.setSubject("Financial Institutions & Markets");
		subject.setSubjectcodeMappingId("1873");
		
		StudentRankBean subject1 = new StudentRankBean();
		subject1.setSem("5");
		subject1.setConsumerProgramStructureId("128");
		subject1.setSubject("Corporate Finance");
		subject1.setSubjectcodeMappingId("1874");
		
		
		StudentRankBean subject2 = new StudentRankBean();
		subject2.setSem("5");
		subject2.setConsumerProgramStructureId("128");
		subject2.setSubject("Strategic Brand Management");
		subject2.setSubjectcodeMappingId("1871");
		
		StudentRankBean subject3 = new StudentRankBean();
		subject3.setSem("5");
		subject3.setConsumerProgramStructureId("128");
		subject3.setSubject("Rural Marketing");
		subject3.setSubjectcodeMappingId("1872");
		
		subjectMapping.add(subject);
		subjectMapping.add(subject1);
		subjectMapping.add(subject2);
		subjectMapping.add(subject3);
		
		List<String> pssIdList = subjectMapping.stream().map(StudentRankBean::getSubjectcodeMappingId).collect(Collectors.toList());
		String sapid = "77120370118";
		
		List<String> bbaElectivePssIdList = Arrays.asList("1873","1874");
		
		final boolean IS_PASSED_EXPECTED =  true;
		boolean IS_PASSED_ACTUAL = false;
		
		when(leaderBoardDAO.getStudentRegistration(sapid)).thenReturn(registrations);
		when(leaderBoardDAO.getLiveExamOrder()).thenReturn(examorders);
		when(leaderBoardDAO.getSubjectDetails()).thenReturn(subjectMapping);
		when(leaderBoardDAO.getBBAElectiveSubjectListByPssidAndSapid(pssIdList, sapid)).thenReturn(bbaElectivePssIdList);
		List<StudentRankBean> subjectWiseRankConfigList = leaderBoardService.getSubjectWiseRankConfigList(sapid);
	
		IS_PASSED_ACTUAL = true;
		
		assertEquals(IS_PASSED_EXPECTED, IS_PASSED_ACTUAL);
	}
	
	@Test
	public void getSubjectWiseRankConfigListThrowEx(){
		
		//Registraion
		List<StudentRankBean> registrations = new ArrayList<>();
		
		StudentRankBean registration = new StudentRankBean();
		registration.setSem("1");
		registration.setConsumerProgramStructureId("128");
		registration.setYear("2020");
		registration.setMonth("Jul");
		
		StudentRankBean registration1 = new StudentRankBean();
		registration1.setSem("2");
		registration1.setConsumerProgramStructureId("128");
		registration1.setYear("2021");
		registration1.setMonth("Jan");
		
		StudentRankBean registration2 = new StudentRankBean();
		registration2.setSem("3");
		registration2.setConsumerProgramStructureId("128");
		registration2.setYear("2021");
		registration2.setMonth("Jul");
		
		StudentRankBean registration3 = new StudentRankBean();
		registration3.setSem("4");
		registration3.setConsumerProgramStructureId("128");
		registration3.setYear("2022");
		registration3.setMonth("Jan");
		
		StudentRankBean registration4 = new StudentRankBean();
		registration4.setSem("5");
		registration4.setConsumerProgramStructureId("128");
		registration4.setYear("2022");
		registration4.setMonth("Jul");
		
		StudentRankBean registration5 = new StudentRankBean();
		registration5.setSem("6");
		registration5.setConsumerProgramStructureId("128");
		registration5.setYear("2023");
		registration5.setMonth("Jan");
		
		registrations.add(registration);
		registrations.add(registration1);
		registrations.add(registration2);
		registrations.add(registration3);
		registrations.add(registration4);
		registrations.add(registration5);
		
		
		//Examorder
		List<StudentRankBean> examorders = new ArrayList<>();
		
		StudentRankBean examorder = new StudentRankBean();		
		examorder.setMonth("Jun");
		examorder.setAcadMonth("Jan");
		examorder.setYear("2023");
		
		StudentRankBean examorder1 = new StudentRankBean();
		examorder1.setMonth("Dec");
		examorder1.setAcadMonth("Jul");
		examorder1.setYear("2022");
		
		StudentRankBean examorder2 = new StudentRankBean();
		examorder2.setMonth("Jun");
		examorder2.setAcadMonth("Jan");
		examorder2.setYear("2022");
		
		StudentRankBean examorder3 = new StudentRankBean();
		examorder3.setMonth("Dec");
		examorder3.setAcadMonth("Jul");
		examorder3.setYear("2021");
		
		StudentRankBean examorder4 = new StudentRankBean();
		examorder4.setMonth("Jun");
		examorder4.setAcadMonth("Jan");
		examorder4.setYear("2021");
		
		StudentRankBean examorder5 = new StudentRankBean();
		examorder5.setMonth("Dec");
		examorder5.setAcadMonth("Jul");
		examorder5.setYear("2020");
		
		examorders.add(examorder);
		examorders.add(examorder1);
		examorders.add(examorder2);
		examorders.add(examorder3);
		examorders.add(examorder4);
		examorders.add(examorder5);
		
		
		//Subject Mapping
		List<StudentRankBean> subjectMapping = new ArrayList<>();
		StudentRankBean subject = new StudentRankBean();
		subject.setSem("5");
		subject.setConsumerProgramStructureId("128");
		subject.setSubject("Financial Institutions & Markets");
		subject.setSubjectcodeMappingId("1873");
		
		StudentRankBean subject1 = new StudentRankBean();
		subject1.setSem("5");
		subject1.setConsumerProgramStructureId("128");
		subject1.setSubject("Corporate Finance");
		subject1.setSubjectcodeMappingId("1874");
		
		
		StudentRankBean subject2 = new StudentRankBean();
		subject2.setSem("5");
		subject2.setConsumerProgramStructureId("128");
		subject2.setSubject("Strategic Brand Management");
		subject2.setSubjectcodeMappingId("1871");
		
		StudentRankBean subject3 = new StudentRankBean();
		subject3.setSem("5");
		subject3.setConsumerProgramStructureId("128");
		subject3.setSubject("Rural Marketing");
		subject3.setSubjectcodeMappingId("1872");
		
		subjectMapping.add(subject);
		subjectMapping.add(subject1);
		subjectMapping.add(subject2);
		subjectMapping.add(subject3);
		
		List<String> pssIdList = subjectMapping.stream().map(StudentRankBean::getSubjectcodeMappingId).collect(Collectors.toList());
		String sapid = "77120370118";
		
		final boolean IS_PASSED_EXPECTED =  true;
		boolean IS_PASSED_ACTUAL = false;
		
		
		try
		{
			when(leaderBoardDAO.getStudentRegistration(sapid)).thenReturn(registrations);
			when(leaderBoardDAO.getLiveExamOrder()).thenReturn(examorders);
			when(leaderBoardDAO.getSubjectDetails()).thenReturn(subjectMapping);
			when(leaderBoardDAO.getBBAElectiveSubjectListByPssidAndSapid(pssIdList, sapid)).thenReturn(null);
			leaderBoardService.getSubjectWiseRankConfigList(sapid);
		}
		catch(Exception ex)
		{
			IS_PASSED_ACTUAL = true;
		}
		
		assertEquals(IS_PASSED_EXPECTED, IS_PASSED_ACTUAL);
	}
}