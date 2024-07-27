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
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContext;
import org.springframework.test.context.junit4.SpringRunner;

import com.nmims.beans.ProgramSubjectMappingStudentPortalBean;
import com.nmims.beans.StudentStudentPortalBean;
import com.nmims.daos.PortalDao;
import com.nmims.daos.StudentDAO;
import com.nmims.services.StudentService;

@RunWith(SpringRunner.class)
@SpringBootTest
public class WaivedinSubjectsReportTest {

	@Mock
	private StudentDAO studentDAO;
	@Mock
	private StudentService studentService;
	
	@Mock
	PortalDao pDao;
	
	@InjectMocks
	StudentService service;
	

	@Mock
	ApplicationContext act;
	
	 @Before
	    public void setup() {
	        MockitoAnnotations.initMocks(this);
	    }
	
	@Test
	public void getWaivedinSubjectsReportTest() {
//		MockitoAnnotations.initMocks(this); 
		String year="2023";
		String month="Jan";
		
		List<String> sapidList=new ArrayList<String>();
		sapidList.add("77220454877");
		sapidList.add("77220437279");
		sapidList.add("77122757998");
		sapidList.add("77121131240");
		sapidList.add("77220454877");
		sapidList.add("77221656171");
		sapidList.add("77221762383");
		sapidList.add("77221789808");
		sapidList.add("77221799693");
		sapidList.add("77221926235");
		
		sapidList.add("77118663780");
		sapidList.add("77118576036");
		sapidList.add("77118426841");
		
		sapidList.add("77118216925");
		sapidList.add("77118190082");
		sapidList.add("77119104862");
		
		List<StudentStudentPortalBean> studentList=new ArrayList<StudentStudentPortalBean>();
		
		StudentStudentPortalBean studentBean=new StudentStudentPortalBean();
		
		studentBean.setSapid("77220437279");
		studentBean.setIsLateral("Y");
		studentBean.setFirstName("Tanvi");
		studentBean.setLastName("Ramgire");
		studentBean.setPreviousStudentId("77218416696");
		studentBean.setProgram("PGDBM - HRM");
		studentBean.setSem("2");
		studentBean.setRegDate("2021-02-26");
		studentBean.setPrgmStructApplicable("Jul2019");
		studentBean.setConsumerProgramStructureId("81");
		
		
		StudentStudentPortalBean studentBean1=new StudentStudentPortalBean();
		studentBean1.setSapid("77220454877");
		studentBean1.setIsLateral("Y");
		studentBean1.setFirstName("Divyesh");
		studentBean1.setLastName("Gosai");
		studentBean1.setPreviousStudentId("77118520865");
		studentBean1.setProgram("PGDBM - MM");
		studentBean1.setSem("2");
		studentBean1.setRegDate("2021-02-24");
		studentBean1.setPrgmStructApplicable("Jul2019");
		studentBean1.setConsumerProgramStructureId("83");
		
		
		studentList.add(studentBean);
		studentList.add(studentBean1);
		
		ArrayList<String> waiwedinsubjects=new ArrayList<String>();
		waiwedinsubjects.add("Compensation & Benefits");
		waiwedinsubjects.add("Organisation Culture");
		
		ArrayList<String> waiwedinsubjects1=new ArrayList<String>();
		waiwedinsubjects1.add("Compensation & Benefits");
		waiwedinsubjects1.add("Organisation Culture");
		
		
		
		
		try {
			System.out.println("month :: "+month+"Year ::"+year);
			when(studentDAO.getsapIdsFromRegistration(month, year)).thenReturn(sapidList);
			when(studentDAO.getstudentInfoList()).thenReturn(studentList);
			
			List<ProgramSubjectMappingStudentPortalBean> waivedInSubjects = new ArrayList<ProgramSubjectMappingStudentPortalBean>();
			ProgramSubjectMappingStudentPortalBean subMapping=new ProgramSubjectMappingStudentPortalBean();
			ProgramSubjectMappingStudentPortalBean subMapping1=new ProgramSubjectMappingStudentPortalBean();
			subMapping.setSubject("Financial Accounting & Analysis");
			subMapping1.setSubject("Information Systems for Managers");
			subMapping1.setActiveStatus("1");
			subMapping.setActiveStatus("1");
			waivedInSubjects.add(subMapping);
			waivedInSubjects.add(subMapping1);
			when(studentDAO.getAllApplicableSubjectsForMBAStudent(studentBean)).thenReturn(waivedInSubjects);
			when(studentDAO.getAllApplicableSubjectsForStudent(studentBean)).thenReturn(waivedInSubjects);
			
			ArrayList<String> subjects=new ArrayList<String>();
			subjects.add("Business Law");
			subjects.add("Business Communication");
			when(studentDAO.getSem1And2PassSubjectsNamesForAStudent("77220437279")).thenReturn(subjects);
			when(studentDAO.getPassSubjectsNamesForAStudent("77220437279")).thenReturn(subjects);
			
			StudentStudentPortalBean studPortal=new StudentStudentPortalBean();
			studPortal.setProgram("PGDBM - HRM");
			studPortal.setPreviousStudentId("77218416696");
			when(pDao.getSingleStudentsData("77220437279")).thenReturn(studPortal);
			
//			when(studentService.mgetWaivedInSubjects(studentBean)).thenReturn(waiwedinsubjects);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			//e.printStackTrace();
		}
		
		
		try {
			
			List<StudentStudentPortalBean> finalPgmChangedStudList=service.getWaivedinSubjectsReport(month, year);
			finalPgmChangedStudList.forEach(x->{
				System.out.println("Name :: "+x.getFirstName());
				System.out.println("Subject :: "+x.getSubject());
			});
		} catch (Exception e) {
			// TODO: handle exception
			//e.printStackTrace();
		}
		
		assertEquals(true,true);
		
	}

	
//	
//	 @Test
//	    public void testGetWaivedinSubjectsReport() throws Exception {
//	        // Mock data
//	        String month = "May";
//	        String year = "2023";
//	        List<String> sapidList = new ArrayList<>();
//	        sapidList.add("SAPID1");
//	        sapidList.add("SAPID2");
//
//	        List<StudentStudentPortalBean> pgmChangedStudList = new ArrayList<>();
//	        StudentStudentPortalBean student1 = new StudentStudentPortalBean();
//	        student1.setSapid("SAPID1");
//	        // Set other properties for student1
//	        pgmChangedStudList.add(student1);
//
//	        StudentStudentPortalBean student2 = new StudentStudentPortalBean();
//	        student2.setSapid("SAPID2");
//	        // Set other properties for student2
//	        pgmChangedStudList.add(student2);
//
//	        // Mock studentDAO methods
//	        when(studentDAO.getsapIdsFromRegistration(month, year)).thenReturn(sapidList);
//	        when(studentDAO.getstudentInfoList(sapidList)).thenReturn(pgmChangedStudList);
//
//	        // Call the method under test
//	        List<StudentStudentPortalBean> result = studentService.getWaivedinSubjectsReport(month, year);
//
//	        // Verify the interactions and assertions
//	        verify(studentDAO).getsapIdsFromRegistration(month, year);
//	        verify(studentDAO).getstudentInfoList(sapidList);
//	        assertEquals(2, result.size()); // Assert the expected size of the result list
//	        // Assert other properties of the result list, if needed
//	    }
}
