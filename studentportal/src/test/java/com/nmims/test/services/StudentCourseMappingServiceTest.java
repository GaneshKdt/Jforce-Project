package com.nmims.test.services;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import com.nmims.beans.ProgramSubjectMappingStudentPortalBean;
import com.nmims.beans.StudentCourseMappingBean;
import com.nmims.beans.StudentStudentPortalBean;
import com.nmims.daos.PortalDao;
import com.nmims.daos.StudentCourseMappingDao;
import com.nmims.daos.StudentDAO;
import com.nmims.interfaces.CustomCourseWaiverService;
import com.nmims.services.StudentCourseMappingService;
import com.nmims.services.StudentService;

@RunWith(SpringRunner.class)
@SpringBootTest
public class StudentCourseMappingServiceTest 
{
	
	@Mock
	StudentCourseMappingDao studentCourse;
	
	@Mock
	StudentService studentService;
	
	@Mock
	PortalDao portalDAO;
	
	@Mock
	StudentDAO studentDao;
	
	@InjectMocks
	StudentCourseMappingService studentCourseService;
	
	@Mock
	CustomCourseWaiverService courseWaiverService;
	
	@Test
	public void populateStudentCourseMapping()
	{
		//Get The Sapids of Current Month And Year
		int expected = 1;
		int actual = 0;
		try {
		StudentStudentPortalBean bean = new StudentStudentPortalBean();
		bean.setSapid("77121101890");
		bean.setIsLateral("Y");
		bean.setPreviousStudentId("77220535649");
		bean.setConsumerProgramStructureId("79");
		bean.setProgramStructure("Jul2019");
		bean.setProgram("MBA (MM)");
		bean.setSem("2");
		
		when(portalDAO.getSingleStudentsData("77121101890")).thenReturn(bean);
		
		
		//Check waived Off
		
		
		String waivedOffSubjects = "Business Economics; Financial Accounting & Analysis; Information Systems for Managers; Management Theory and Practice; Marketing Management; Organisational Behaviour";
		ArrayList<String> waivedOffSubjectsList = new ArrayList<String>(Arrays.asList(waivedOffSubjects.split(";")));

		//Prepare current subjects
		String current = "Brand Management; Consumer Behaviour; Customer Relationship Management; International Marketing; Marketing Strategy; Sales Management";

		ArrayList<String> currentSubjects = new ArrayList<String>(Arrays.asList(current.split(";")));
		
		//Prepared passed subjects
		String passedSubject = "Business Communication; Business Law; Decision Science; Essentials of HRM; Operations Management; Strategic Management";
		ArrayList<String> passedSubjects = new ArrayList<String>(Arrays.asList(waivedOffSubjects.split(";")));
		
		
		//Waived Off logic
		when(studentDao.checkIfStudentIsLateral("77121101890")).thenReturn(true);
		
		when(studentDao.getPassSubjectsNamesForAStudent("77121101890")).thenReturn(passedSubjects);
		
		when(studentDao.getPreviousStudentNumber("77121101890")).thenReturn("77220535649");
		
		when(studentDao.checkIfStudentIsLateral("77220535649")).thenReturn(false);
		
		when(studentDao.getPassSubjectsNamesForAStudent("77220535649")).thenReturn(passedSubjects);
		
	
		
		//latest Student Registration data preparation
		StudentStudentPortalBean studentReg = new StudentStudentPortalBean();
		studentReg.setSapid("77121101890");
		studentReg.setProgram("MBA (MM)");
		studentReg.setMonth("Jan");
		studentReg.setYear("2022");
		studentReg.setSem("3");
		studentReg.setConsumerProgramStructureId("138");
		//current subject
		when(portalDAO.getStudentsMostRecentRegistrationData("77121101890")).thenReturn(studentReg);
		
		when(portalDAO.getCurrentCycleSubjects(studentReg.getConsumerProgramStructureId() , studentReg.getSem(), waivedOffSubjectsList)).thenReturn(currentSubjects);
		
		//waived in logic
		when(studentDao.getAllApplicableSubjectsForStudent(bean)).thenReturn(new ArrayList<ProgramSubjectMappingStudentPortalBean>());
		
		when(studentDao.checkIfStudentIsLateral("77121101890")).thenReturn(true);
		
		when(studentDao.getPassSubjectsNamesForAStudent("77121101890")).thenReturn(passedSubjects);
		
		when(studentDao.getPreviousStudentNumber("77121101890")).thenReturn("77220535649");
		
		when(studentDao.checkIfStudentIsLateral("77220535649")).thenReturn(false);
		
		when(studentDao.getPassSubjectsNamesForAStudent("77220535649")).thenReturn(passedSubjects);
		
		
		
		//Passsed Subjects
		when( portalDAO.getPassSubjectsNamesForAStudent("77121101890")).thenReturn(passedSubjects);
		
		//Failed Subjects
		when(studentCourseService.getFailSubjectsNamesForAStudent("77121101890")).thenReturn(new ArrayList<String>());
		
		
		 HashMap<String,String> currentListwithpssid = new HashMap<String,String>();
		
		 currentListwithpssid.put("2215","Consumer Behaviour");
		 currentListwithpssid.put("2216","Customer Relationship Management");
		 currentListwithpssid.put("2217","International Marketing");
		 currentListwithpssid.put("2218","Marketing Strategy");
		 currentListwithpssid.put("2219","Sales Management");
		 currentListwithpssid.put("2214","Brand Management");
		 
		//check current Subjects
		when(portalDAO.getProgramSemSubjectId(currentSubjects, studentReg.getConsumerProgramStructureId())).thenReturn(currentListwithpssid);
		
		when(studentCourse.batchInsertStudentPssIdsMappings(currentListwithpssid, "77121101890","Jul","2022")).thenReturn(6);
		when(courseWaiverService.checkSapidExist("77121101890")).thenReturn(false);

		//Add All the List And perform Insertion
		studentCourseService.insertInStudentCourseTable("77121101890",false);
		actual = 1;
		}catch(Exception e)
		{
			e.printStackTrace();
		}
		assertEquals(expected, actual);
	}
	

}
