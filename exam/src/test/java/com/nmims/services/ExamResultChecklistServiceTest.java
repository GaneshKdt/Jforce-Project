package com.nmims.services;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit4.SpringRunner;

import com.nmims.beans.ExamBookingTransactionBean;
import com.nmims.beans.ExamResultChecklistBean;
import com.nmims.beans.StudentExamBean;
import com.nmims.daos.ExamResultChecklistDao;
import com.nmims.daos.StudentMarksDAO;

@SpringBootTest
@RunWith(SpringRunner.class)
public class ExamResultChecklistServiceTest {

	@Autowired
	ExamResultChecklistService examResultChecklistService;

	@MockBean
	ExamResultChecklistDao examResultChecklistDao;

	@MockBean
	StudentMarksDAO studentMarksDao;
	
	@MockBean
	StudentService studentService;

	@Rule
	public ExpectedException expectedException = ExpectedException.none();
	
	private final String DUMMY_YEAR = "DummyYear";
	private final String DUMMY_MONTH = "DummyMonth";
	private final int TEN_INT = 10;
	private final int FIVE_INT = 5;
	private final int ZERO_INT = 0;
	private final int SIX_INT = 6;
	private final int SEVEN_INT = 7;
	private final int FIFTY_INT = 50;
	private final String TOTAL_COUNT_KEY_STR = "totalCount";

	@Test
	public void loadApplicationContext() {
		assertNotNull(examResultChecklistService);
	}

	@Test
	public void getNoRecordFoundException() {
		String year = DUMMY_YEAR;
		String month = DUMMY_MONTH;
		ExamBookingTransactionBean searchBean = new ExamBookingTransactionBean();
		searchBean.setYear(year);
		searchBean.setMonth(month);

		when(examResultChecklistDao.getProjectBookedCount(year, month)).thenReturn(ZERO_INT);
		when(examResultChecklistDao.getExamBookedCount(year, month)).thenReturn(ZERO_INT);
		when(examResultChecklistDao.getOnlyAssignmentSubmittedCount(year, month)).thenReturn(ZERO_INT);
		when(examResultChecklistDao.getAssignmentApplicableRecords(year, month)).thenReturn(Arrays.asList());
		when(examResultChecklistDao.getProjectNotBookedCount(year, month)).thenReturn(ZERO_INT);
		when(studentMarksDao.projectFeeExemptAndNotSubmitted(searchBean)).thenReturn(Arrays.asList());

		expectedException.expect(RuntimeException.class);
		expectedException.expectMessage("No records found");
		
		examResultChecklistService.getDashboardCountForExamResults(month, year);

	}
	
	@Test
	public void getTenRecordsEachCategory() {
		String year = DUMMY_YEAR;
		String month = DUMMY_MONTH;
		ExamBookingTransactionBean searchBean = new ExamBookingTransactionBean();
		searchBean.setYear(year);
		searchBean.setMonth(month);
		
		when(examResultChecklistDao.getProjectBookedCount(year, month)).thenReturn(TEN_INT);
		when(examResultChecklistDao.getExamBookedCount(year, month)).thenReturn(TEN_INT);
		when(examResultChecklistDao.getOnlyAssignmentSubmittedCount(year, month)).thenReturn(TEN_INT);
		
		when(examResultChecklistDao.getAssignmentApplicableRecords(year, month)).thenReturn(listOfExamBookingBeans(TEN_INT));
		when(examResultChecklistDao.getStudentdata(Mockito.anyString())).thenReturn(new StudentExamBean());
		when(studentService.mgetWaivedOffSubjects(new StudentExamBean())).thenReturn(new ArrayList<>(Arrays.asList()));
		
		when(examResultChecklistDao.getProjectNotBookedCount(year, month)).thenReturn(TEN_INT);
		when(studentMarksDao.projectFeeExemptAndNotSubmitted(searchBean)).thenReturn(listOfExamTransactionBeans((TEN_INT)));
		
		Map<String, Integer> dashboardCountForExamResults = examResultChecklistService.getDashboardCountForExamResults(month, year);
		
		// should contain all categories and total
		assertEquals(SEVEN_INT, dashboardCountForExamResults.size());

		int totalCount = 0;
		
		// asserting all values int the map should be as we had mocked
		for (Entry<String, Integer> entries : dashboardCountForExamResults.entrySet())
			if (!TOTAL_COUNT_KEY_STR.equalsIgnoreCase(entries.getKey()))
				assertEquals(TEN_INT, entries.getValue().intValue());
			else
				totalCount = entries.getValue().intValue();
		
		assertEquals(FIFTY_INT, totalCount);
		
	}

	private List<ExamBookingTransactionBean> listOfExamTransactionBeans(int i) {
		return Stream.generate(ExamBookingTransactionBean::new).limit(i).collect(Collectors.toList());
	}

	private List<ExamResultChecklistBean> listOfExamBookingBeans(int i) {
		return Stream.generate(ExamResultChecklistBean::new).limit(i).collect(Collectors.toList());
	}

}
