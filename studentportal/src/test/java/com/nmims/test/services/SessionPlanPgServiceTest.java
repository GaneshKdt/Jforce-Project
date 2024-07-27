package com.nmims.test.services;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import com.nmims.beans.FacultyStudentPortalBean;
import com.nmims.beans.SessionPlanModulePg;
import com.nmims.beans.SessionPlanPgBean;
import com.nmims.daos.FacultyDAO;
import com.nmims.daos.SessionPlanPgDao;
import com.nmims.interfaces.SessionPlanPGInterface;
import com.nmims.services.SessionPlanPgService;

public class SessionPlanPgServiceTest {
	
	@InjectMocks
	private SessionPlanPgService sessionPlanPgService;
	
	@Mock
	private SessionPlanPgDao sessionPlanPgDao;
	
	@Mock
	private FacultyDAO facultyDao;
	
	@Before
	public void setup() {
		MockitoAnnotations.initMocks(this);
	}
	
	@Test
	public void testFetchModuleDetails() throws Exception {
		// Mock data
		String programSemSubjectId = "subject123";
		int subjectCodeId = 1;
		String facultyId = "faculty123";
		String facultyFirstName = "Saurabh";
		String facultyLastName = "Pawar";
		String userId = "777777777132";
		List<SessionPlanModulePg> moduleList = new ArrayList<>();
		SessionPlanPgBean sessionPlanBean = new SessionPlanPgBean();
		sessionPlanBean.setId(1);
		sessionPlanBean.setFacultyId(facultyId);
		
		// Stubbing method calls
		when(sessionPlanPgDao.getSubjectCodeIdByPssId(programSemSubjectId)).thenReturn(subjectCodeId);
		when(sessionPlanPgDao.getSessionPlanDetails(subjectCodeId)).thenReturn(sessionPlanBean);
		when(sessionPlanPgDao.getSessionPlanModuleDetails(sessionPlanBean.getId())).thenReturn(moduleList);
		when(facultyDao.getAllFacultyRecords()).thenReturn(createMockFacultyList());
		
		// Call the method
		SessionPlanPgBean result = sessionPlanPgService.fetchModuleDetails(programSemSubjectId,userId);
		
		// Assertions
		assertEquals(moduleList, result.getSessionPlanModuleList());
		assertEquals(facultyFirstName + " " + facultyLastName, result.getFacultyName());
	}
	
	@Test
	public void testFetchModuleDetails_WithValidProgramSemSubjectId_ShouldReturnSessionPlanBean() throws Exception {
		// Mock data
		String programSemSubjectId = "subject123";
		int subjectCodeId = 1;
		String facultyId = "faculty123";
		String facultyFirstName = "Saurabh";
		String facultyLastName = "Pawar";
		String userId = "777777777132";
		List<SessionPlanModulePg> moduleList = new ArrayList<>();
		SessionPlanPgBean sessionPlanBean = new SessionPlanPgBean();
		sessionPlanBean.setId(1);
		sessionPlanBean.setFacultyId(facultyId);
		
		// Stubbing method calls
		when(sessionPlanPgDao.getSubjectCodeIdByPssId(programSemSubjectId)).thenReturn(subjectCodeId);
		when(sessionPlanPgDao.getSessionPlanDetails(subjectCodeId)).thenReturn(sessionPlanBean);
		when(sessionPlanPgDao.getSessionPlanModuleDetails(sessionPlanBean.getId())).thenReturn(moduleList);
		when(facultyDao.getAllFacultyRecords()).thenReturn(createMockFacultyList());
		
		// Call the method
		SessionPlanPgBean result = sessionPlanPgService.fetchModuleDetails(programSemSubjectId, userId);
		
		// Assertions
		assertEquals(moduleList, result.getSessionPlanModuleList());
		assertEquals(facultyFirstName + " " + facultyLastName, result.getFacultyName());
	}
	
	@Test(expected = Exception.class)
	public void testFetchModuleDetails_WithInvalidProgramSemSubjectId_ShouldThrowException() throws Exception {
		// Mock data
		String programSemSubjectId = "invalidSubjectId";
		String userId = "777777777132";
		// Stubbing method call to throw an exception
		when(sessionPlanPgDao.getSubjectCodeIdByPssId(programSemSubjectId)).thenThrow(new Exception());
		
		// Call the method (expecting an exception)
		sessionPlanPgService.fetchModuleDetails(programSemSubjectId,userId);
	}
	
	@Test
	public void testFetchModuleDetails_WithNoSessionPlanDetails_ShouldReturnNull() {
		// Mock data
		String programSemSubjectId = "subject123";
		int subjectCodeId = 1;
		SessionPlanPgBean result = null;
		String userId = "777777777132";
		try {
		// Stubbing method calls
		when(sessionPlanPgDao.getSubjectCodeIdByPssId(programSemSubjectId)).thenReturn(subjectCodeId);
		when(sessionPlanPgDao.getSessionPlanDetails(subjectCodeId)).thenReturn(null);
		
		// Call the method
		result = sessionPlanPgService.fetchModuleDetails(programSemSubjectId,userId);
		}catch (Exception e) {
			
		}
		// Assertion
		assertNull(result);
	}
	
	@Test
	public void testFetchModuleDetails_WithNoSessionPlanModuleDetails_ShouldReturnSessionPlanBeanWithEmptyModuleList() throws Exception {
		// Mock data
		String programSemSubjectId = "subject123";
		int subjectCodeId = 1;
		String facultyId = "faculty123";
		String facultyFirstName = "Saurabh";
		String facultyLastName = "Pawar";
		String userId = "777777777132";
		SessionPlanPgBean sessionPlanBean = new SessionPlanPgBean();
		sessionPlanBean.setId(1);
		sessionPlanBean.setFacultyId(facultyId);
		
		// Stubbing method calls
		when(sessionPlanPgDao.getSubjectCodeIdByPssId(programSemSubjectId)).thenReturn(subjectCodeId);
		when(sessionPlanPgDao.getSessionPlanDetails(subjectCodeId)).thenReturn(sessionPlanBean);
		when(sessionPlanPgDao.getSessionPlanModuleDetails(sessionPlanBean.getId())).thenReturn(null);
		when(facultyDao.getAllFacultyRecords()).thenReturn(createMockFacultyList());
		SessionPlanPgBean result = sessionPlanPgService.fetchModuleDetails(programSemSubjectId,userId);

		
		// Assertions
		assertNull(result.getSessionPlanModuleList());
		assertEquals(facultyFirstName + " " + facultyLastName, result.getFacultyName());
	}
	
	private ArrayList<FacultyStudentPortalBean> createMockFacultyList() {
		ArrayList<FacultyStudentPortalBean> facultyList = new ArrayList<>();
		FacultyStudentPortalBean faculty = new FacultyStudentPortalBean();
		faculty.setFacultyId("faculty123");
		faculty.setFirstName("Saurabh");
		faculty.setLastName("Pawar");
		facultyList.add(faculty);
		return facultyList;
	}
}

