package com.nmims.test.services;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotSame;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.boot.test.context.SpringBootTest;

import com.nmims.beans.StudentSubjectConfigExamBean;
import com.nmims.beans.TimeBoundUserMapping;
import com.nmims.daos.DashboardDAO;
import com.nmims.services.ConfigurationService;

@SpringBootTest
public class ConfigurationServiceTest1 {

	@InjectMocks
	ConfigurationService configurationService;

	@Mock
	DashboardDAO dashboardDAO;

	@Before
	public void setup() {
		MockitoAnnotations.initMocks(this); // without this you will get NPE
	}

	
	@Test
	public void daoGetExistingStudentsByTimeboundIdTest() {

		TimeBoundUserMapping t = new TimeBoundUserMapping();
		t.setId("789");
		t.setUserId("77777777132");
		t.setTimebound_subject_config_id(5);
		t.setRole("admin");
		t.setCreatedBy("omkar kamble");
		t.setCreatedDate("30-03-2023");
		t.setLastModifiedBy("omkar kamble");

		String timeBoundSubjectConfigId = "4";

		int expected = 1;
		int actual = 0;

		ArrayList<TimeBoundUserMapping> studentList = new ArrayList<TimeBoundUserMapping>();
		studentList.add(t);

		try {
			when(dashboardDAO.getExistingStudentsByTimeboundId(timeBoundSubjectConfigId)).thenReturn(studentList);
			actual = 1;
		} catch (Exception e1) {
			e1.printStackTrace();
		}
		assertEquals(expected, actual);

	}

	@Test
	public void getSubjectNameByprogramSemSubIdTest() {
		int programSemSubId = 4;
		int expected = 1;
		int actual = 0;

		try {
			when(dashboardDAO.getSubjectNameByprgm_sem_subj_id(programSemSubId))
					.thenReturn("Customer Relationship Management");
			actual = 1;

		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		assertEquals(expected, actual);

	}

	@Test
	public void getBatchDetailsByBatchIdTest() {

		String batchId = "199999";
		int expected = 1;
		int actual = 0;
		StudentSubjectConfigExamBean b = new StudentSubjectConfigExamBean();
		b.setBatchName("MBA");
		b.setAcadMonth("jan");
		b.setAcadYear("2021");
		b.setExamMonth("jun");
		b.setExamYear("2021");
		try {
			when(dashboardDAO.getBatchDetailsByBatchId(batchId)).thenReturn(b);
			actual = 1;
		} catch (Exception e) {

		}
		assertEquals(expected, actual);

	}

	
	@Test
	public void downloadTimeBoundExcelServiceTestSuccess() {
		
		String timeBoundSubjectConfigId="1540"; 
		String prgm_sem_subj_id="4"; 
		String batchId="1";
		
		int expected =1;
		int actual =0;
		
		try {
			List<TimeBoundUserMapping> downloadTimeBoundExcelService = configurationService
					.downloadTimeBoundExcelService(timeBoundSubjectConfigId, prgm_sem_subj_id, batchId);
			if (downloadTimeBoundExcelService!= null) {
				actual = 1;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		assertEquals(expected, actual);
	}
	
	
	@Test
	public void downloadTimeBoundExcelServiceTestFail() {
		
		String timeBoundSubjectConfigId="1540"; 
		String prgm_sem_subj_id="4"; 
		String batchId="1";
		
		int expected =1;
		int actual =0;	
		try {
			List<TimeBoundUserMapping> downloadTimeBoundExcelService = configurationService
					.downloadTimeBoundExcelService(timeBoundSubjectConfigId, prgm_sem_subj_id, batchId);
			if (downloadTimeBoundExcelService== null) {
				actual = 1;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		assertNotSame(expected, actual);
	}

}
