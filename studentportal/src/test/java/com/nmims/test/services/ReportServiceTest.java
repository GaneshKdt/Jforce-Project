package com.nmims.test.services;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.boot.test.context.SpringBootTest;

import com.nmims.beans.ReportBean;
import com.nmims.daos.ReportsDao;
import com.nmims.services.ReportService;

@SpringBootTest
public class ReportServiceTest {

	private ReportService reportService;
	private ReportsDao reportsDao;

	@Before
	public void setUp() throws Exception {
		reportsDao = mock(ReportsDao.class);
		reportService = new ReportService();
		// reportService.rDao = reportsDao;

		Field privateField = ReportService.class.getDeclaredField("rDao");
		privateField.setAccessible(true);
		privateField.set(reportService, reportsDao);
	}
	
	
	/**
	 *  Test Cases to check reports for valid roles
	 *  @author saurabh.pawar
	 *  @return void
	 */
	@Test
	public void testGetAllPowerBIReportDetailsForRoles() {
		// Prepare test data
		String roles = "Acads Admin, Exam Admin";

		List<String> expectedReportCategoryList = Arrays.asList("Common", "Academic", "Exam");
		List<ReportBean> expectedReportList = Arrays.asList(new ReportBean(1, "Report1", "test link", "Academic"),
				new ReportBean(2, "Report2", "test link", "Exam"), new ReportBean(3, "Report3", "test link", "Common"));

		// Mock the behavior of ReportsDao
		when(reportsDao.getPowerbiReportsList(expectedReportCategoryList)).thenReturn(expectedReportList);

		// Perform the test
		List<ReportBean> actualReportList = reportService.getAllPowerBIReportDetails(roles);

		// Verify the result
		assertEquals(expectedReportList, actualReportList);
	}
	
	/**
	 *  Test Cases to check reports for no role but have access to the report
	 *  @author saurabh.pawar
	 *  @return void
	 */
	@Test
	public void testGetAllPowerBIReportDetails_WithNoRolesButHavingAccessToReportsTab() {
		// Prepare test data
		String roles = "";

		List<ReportBean> expectedReportList = Arrays.asList(new ReportBean(1, "Report1", "test link", "Category1"),
				new ReportBean(2, "Report2", "test link", "Category2"));

		// Mock the behavior of ReportsDao
		when(reportsDao.getPowerbiReportsList(Arrays.asList("Common"))).thenReturn(expectedReportList);

		// Perform the test
		List<ReportBean> actualReportList = reportService.getAllPowerBIReportDetails(roles);

		// Verify the result
		assertEquals(expectedReportList, actualReportList);
	}
}