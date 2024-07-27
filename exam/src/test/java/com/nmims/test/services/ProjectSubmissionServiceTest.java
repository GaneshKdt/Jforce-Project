package com.nmims.test.services;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.RETURNS_SMART_NULLS;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import com.nmims.beans.AssignmentFilesSetbean;
import com.nmims.beans.StudentExamBean;
import com.nmims.beans.UserAuthorizationExamBean;
import com.nmims.daos.AssignmentsDAO;
import com.nmims.daos.ProjectSubmissionDAO;
import com.nmims.services.IProjectSubmissionService;
import com.nmims.services.LevelBasedProjectService;
import com.nmims.services.ProjectSubmissionServiceImpl;

/**
 * Unit test of getProjectPendingReport(request, response, filesSet) of
 * IProjectSubmissionService
 */
@RunWith(SpringRunner.class)
@SpringBootTest
public class ProjectSubmissionServiceTest {
	

	
	@Autowired
	UserAuthorizationExamBean userAuthBean;

	@InjectMocks
	ProjectSubmissionServiceImpl projectSubmissionService;
	
	@Mock
	AssignmentsDAO aDao;
	@Mock
	ProjectSubmissionDAO projectSubmissionDAO;
	@Mock
	LevelBasedProjectService levelBasedProjectService;
	
	
	private static UserAuthorizationExamBean userAuthorization =null;

	/*
	 * setting list of AuthorizedCenterCodes for every method
	 */
	@BeforeClass
	public static void setup() {
		System.out.println("Testt.setup()");
		userAuthorization=new UserAuthorizationExamBean();
		try {
			String data = "a020o00000ufoGV\na020o00000vQVcn\na020o00000xCDja\na020o000010Lu8v\na022j000002ppPk\na022j0000043uIy\na022j000004fAwH\na029000000e70NJ\na029000000EbpNC\na029000000FESbA\na029000000FESbF\na029000000GBA4m\na029000000HCK3R\na029000000HD1X5\na029000000j9vfo\na029000000j9viB\na029000000j9vif\na029000000jKIG4\na029000000jTU11\na029000000njyyu\na029000000NyJ93\na029000000qtONq\na029000000qzCzL\na029000000s8Lpl";
			String[] dataArray = data.split("\\r?\\n");
			List<String> stringList = new ArrayList<>(Arrays.asList(dataArray));

			
			userAuthorization.setAuthorizedCenterCodes((ArrayList<String>) stringList);
			
		} catch (Exception e) {
			
		}
	}
	
	

	
	
		public List<StudentExamBean> instance() {
			UserAuthorizationExamBean userAuthorization=new UserAuthorizationExamBean();
			
			List<StudentExamBean> list=new ArrayList<StudentExamBean>();
			StudentExamBean bean=new StudentExamBean();
			bean.setSem("4");
			bean.setSapid("77119195643");
			bean.setMonth("Jan");
			bean.setYear("2021");
			bean.setFirstName("Riddhi");
			bean.setLastName("Parekh");
			bean.setEmailId("pradeepwaghmode8@gmail.com");
			bean.setMobile("7709064144");
			bean.setProgram("PGDBM - HRM");
			bean.setEnrollmentMonth("Jul");
			bean.setEnrollmentYear("2019");
			bean.setValidityEndMonth("Jun");
			bean.setValidityEndYear("2023");
			bean.setCenterCode("a020o00000xCDja");
			
			list.add(bean);
			
			return list;
			
		}


		/*
		 * calling service method getProjectPendingReport(userAuthorization, fileset) of
		 * class ProjectSubmissionServiceImpl and checking project payment status in
		 * first exam cycle if payment status is recived for first exam cycle then test
		 * case result will positive and my student data is "student is done project
		 * payment in first cycle which is April 2023 and student is succussefully
		 * submitted project in same exam cycle wich is April 2023"--sapid of that
		 * student is 77119195643
		 */
		@Test
		public void testProjectPendingReportByPassingFristExamCycleForProjectPaymentStatus() {
			MockitoAnnotations.initMocks(this); 
			System.out.println("Testt.servicTest()");
			List<StudentExamBean> eligiblelist = new ArrayList<StudentExamBean>();
			AssignmentFilesSetbean fileset=new AssignmentFilesSetbean();
			fileset.setMonth("Apr");
			fileset.setYear("2023");
			
			List<String> histroryList=new ArrayList<String>();
			List<String> list=new ArrayList<String>();
			list.add("77119195643");
			list.add("77220813968");
			ArrayList<String> masterKey=new ArrayList<String>();
			masterKey.add("80");
			masterKey.add("81");
			String sem="4";
			
			
			List<StudentExamBean> studlist=new ArrayList<StudentExamBean>();
			StudentExamBean bean=new StudentExamBean();
			bean.setSem("4");
			bean.setSapid("77119195643");
			bean.setMonth("Jan");
			bean.setYear("2021");
			bean.setFirstName("Riddhi");
			bean.setLastName("Parekh");
			bean.setEmailId("pradeepwaghmode8@gmail.com");
			bean.setMobile("7709064144");
			bean.setProgram("PGDBM - HRM");
			bean.setEnrollmentMonth("Jul");
			bean.setEnrollmentYear("2019");
			bean.setValidityEndMonth("Jun");
			bean.setValidityEndYear("2023");
			bean.setCenterCode("a020o00000xCDja");
			studlist.add(bean);
			
			
			try {
			
				when(projectSubmissionDAO.getProjectSubmissionlist(fileset.getYear(), fileset.getMonth())).thenReturn(list);
				when(projectSubmissionDAO.getProjectPaymentStatus(fileset.getMonth(), fileset.getYear())).thenReturn(list);
				when( projectSubmissionDAO.getProjectPaymentStatusFromHistory(fileset.getMonth(), fileset.getYear())).thenReturn(histroryList);
				when(aDao.getAllMasterKeysWithProject()).thenReturn(masterKey);
				when(projectSubmissionDAO.getProjectApplicableSem("81")).thenReturn(sem);
				when(levelBasedProjectService.getProjectApplicableStudents("81",sem)).thenReturn(studlist);
				when(levelBasedProjectService.isStudentValid(bean.getSapid(),bean.getValidityEndMonth(),bean.getValidityEndYear())).thenReturn(true);
				when(levelBasedProjectService.checkIfStudentPassProject(bean.getSapid(),"Project","Y")).thenReturn(false);
			List<String> sapids=projectSubmissionDAO.getProjectSubmissionlist(fileset.getYear(),fileset.getMonth());
			}
			
			catch (Exception e) {
				// TODO: handle exception
				e.printStackTrace();
			}
			try {
			
			eligiblelist=projectSubmissionService.getProjectPendingReport(userAuthorization, fileset);
			System.out.println("Total student count ::"+eligiblelist.size());
			
			boolean flag=false;
			
					for(StudentExamBean studbean:eligiblelist) {
						
						if(studbean.getBooked()=="Y" )
							flag=true;
					}
					
			//assertThat(eligiblelist.size()).isGreaterThan(0);
			assertEquals(true,flag);
			}
			catch (Exception e) {
				System.out.println("Execption is :: "+e.getMessage());
				e.printStackTrace();
				// TODO: handle exception
			}
			
		}
		
		/*
		 * calling service method getProjectPendingReport(userAuthorization, fileset) of
		 * class ProjectSubmissionServiceImpl and checking project submission status in
		 * first exam cycle, if project submission status is submitted for first exam
		 * cycle then test case result will positive and my student data is "student is
		 * done project payment in first cycle which is April 2023 and student is
		 * succussefully submitted project in same exam cycle wich is April 2023"--sapid
		 * of that student is 77119195643
		 */
		@Test
		public void testProjectPendingReportByPassingFristExamCycleForProjectProjectSubmissionStatus() {
			MockitoAnnotations.initMocks(this); 
			System.out.println("Testt.servicTest()");
			List<StudentExamBean> eligiblelist = new ArrayList<StudentExamBean>();
			AssignmentFilesSetbean fileset=new AssignmentFilesSetbean();
			fileset.setMonth("Apr");
			fileset.setYear("2023");
			
			List<String> histroryList=new ArrayList<String>();
			List<String> list=new ArrayList<String>();
			list.add("77119195643");
			list.add("77220813968");
			ArrayList<String> masterKey=new ArrayList<String>();
			masterKey.add("80");
			masterKey.add("81");
			String sem="4";
			
			
			List<StudentExamBean> studlist=new ArrayList<StudentExamBean>();
			StudentExamBean bean=new StudentExamBean();
			bean.setSem("4");
			bean.setSapid("77119195643");
			bean.setMonth("Jan");
			bean.setYear("2021");
			bean.setFirstName("Riddhi");
			bean.setLastName("Parekh");
			bean.setEmailId("pradeepwaghmode8@gmail.com");
			bean.setMobile("7709064144");
			bean.setProgram("PGDBM - HRM");
			bean.setEnrollmentMonth("Jul");
			bean.setEnrollmentYear("2019");
			bean.setValidityEndMonth("Jun");
			bean.setValidityEndYear("2023");
			bean.setCenterCode("a020o00000xCDja");
			studlist.add(bean);
			
			
			try {
			
				when(projectSubmissionDAO.getProjectSubmissionlist(fileset.getYear(), fileset.getMonth())).thenReturn(list);
				when(projectSubmissionDAO.getProjectPaymentStatus(fileset.getMonth(), fileset.getYear())).thenReturn(list);
				when( projectSubmissionDAO.getProjectPaymentStatusFromHistory(fileset.getMonth(), fileset.getYear())).thenReturn(histroryList);
				when(aDao.getAllMasterKeysWithProject()).thenReturn(masterKey);
				when(projectSubmissionDAO.getProjectApplicableSem("81")).thenReturn(sem);
				when(levelBasedProjectService.getProjectApplicableStudents("81",sem)).thenReturn(studlist);
				when(levelBasedProjectService.isStudentValid(bean.getSapid(),bean.getValidityEndMonth(),bean.getValidityEndYear())).thenReturn(true);
				when(levelBasedProjectService.checkIfStudentPassProject(bean.getSapid(),"Project","Y")).thenReturn(false);
			List<String> sapids=projectSubmissionDAO.getProjectSubmissionlist(fileset.getYear(),fileset.getMonth());
			}
			
			catch (Exception e) {
				// TODO: handle exception
				e.printStackTrace();
			}
			try {
			
			eligiblelist=projectSubmissionService.getProjectPendingReport(userAuthorization, fileset);
			System.out.println("Total student count ::"+eligiblelist.size());
			
			boolean flag=false;
			
			for(StudentExamBean studbean:eligiblelist) {
				
				if(studbean.getStatus()=="Submitted" )
					flag=true;
			}
			
			assertEquals(true,flag);
			}
			catch (Exception e) {
				System.out.println("Execption is :: "+e.getMessage());
				e.printStackTrace();
				// TODO: handle exception
			}
			
		}
	
		/*
		 * calling service method getProjectPendingReport(userAuthorization, fileset) of
		 * class ProjectSubmissionServiceImpl and checking project payment status in
		 * first exam cycle if payment status is recived for first exam cycle then test
		 * case result will positive and my student data is "student is done project
		 * payment in first cycle which is April 2023 and student is succussefully
		 * submitted project in same exam cycle wich is April 2023"--sapid of that
		 * student is 77119195643
		 */
		@Test
		public void testProjectPendingReportByPassingSecondExamCycleForProjectPaymentStatus() {
			MockitoAnnotations.initMocks(this); 
			System.out.println("Testt.servicTest()");
			List<StudentExamBean> eligiblelist = new ArrayList<StudentExamBean>();
			AssignmentFilesSetbean fileset=new AssignmentFilesSetbean();
			fileset.setMonth("Jun");
			fileset.setYear("2023");
			
			List<String> list=new ArrayList<String>();
//			list.add("77119195643");
//			list.add("77220813968");
			ArrayList<String> masterKey=new ArrayList<String>();
			masterKey.add("80");
			masterKey.add("81");
			String sem="4";
			
			
			List<StudentExamBean> studlist=new ArrayList<StudentExamBean>();
			StudentExamBean bean=new StudentExamBean();
			bean.setSem("4");
			bean.setSapid("77119195643");
			bean.setMonth("Jan");
			bean.setYear("2021");
			bean.setFirstName("Riddhi");
			bean.setLastName("Parekh");
			bean.setEmailId("pradeepwaghmode8@gmail.com");
			bean.setMobile("7709064144");
			bean.setProgram("PGDBM - HRM");
			bean.setEnrollmentMonth("Jul");
			bean.setEnrollmentYear("2019");
			bean.setValidityEndMonth("Jun");
			bean.setValidityEndYear("2023");
			bean.setCenterCode("a020o00000xCDja");
			studlist.add(bean);
			
			
			try {
			
				when(projectSubmissionDAO.getProjectSubmissionlist(fileset.getYear(), fileset.getMonth())).thenReturn(list);
				when(projectSubmissionDAO.getProjectPaymentStatus(fileset.getMonth(), fileset.getYear())).thenReturn(list);
				when( projectSubmissionDAO.getProjectPaymentStatusFromHistory(fileset.getMonth(), fileset.getYear())).thenReturn(list);
				when(aDao.getAllMasterKeysWithProject()).thenReturn(masterKey);
				when(projectSubmissionDAO.getProjectApplicableSem("81")).thenReturn(sem);
				when(levelBasedProjectService.getProjectApplicableStudents("81",sem)).thenReturn(studlist);
				when(levelBasedProjectService.isStudentValid(bean.getSapid(),bean.getValidityEndMonth(),bean.getValidityEndYear())).thenReturn(true);
				when(levelBasedProjectService.checkIfStudentPassProject(bean.getSapid(),"Project","Y")).thenReturn(false);
			List<String> sapids=projectSubmissionDAO.getProjectSubmissionlist(fileset.getYear(),fileset.getMonth());
			}
			
			catch (Exception e) {
				// TODO: handle exception
				e.printStackTrace();
			}
			try {
			
			eligiblelist=projectSubmissionService.getProjectPendingReport(userAuthorization, fileset);
			System.out.println("Total student count ::"+eligiblelist.size());
			
			boolean flag=false;
			
					for(StudentExamBean studbean:eligiblelist) {
						
						if(!(studbean.getBooked()=="Y") )
							flag=true;
					}
					
			//assertThat(eligiblelist.size()).isGreaterThan(0);
			assertEquals(true,flag);
			}
			catch (Exception e) {
				System.out.println("Execption is :: "+e.getMessage());
				e.printStackTrace();
				// TODO: handle exception
			}
			
		}
		
		/*
		 * calling service method getProjectPendingReport(userAuthorization, fileset) of
		 * class ProjectSubmissionServiceImpl and checking project submission status in
		 * first exam cycle, if project submission status is submitted for first exam
		 * cycle then test case result will positive and my student data is "student is
		 * done project payment in first cycle which is April 2023 and student is
		 * succussefully submitted project in same exam cycle wich is April 2023"--sapid
		 * of that student is 77119195643
		 */
		@Test
		public void testProjectPendingReportByPassingSecondExamCycleForProjectProjectSubmissionStatus() {
			MockitoAnnotations.initMocks(this); 
			System.out.println("Testt.servicTest()");
			List<StudentExamBean> eligiblelist = new ArrayList<StudentExamBean>();
			AssignmentFilesSetbean fileset=new AssignmentFilesSetbean();
			fileset.setMonth("Jun");
			fileset.setYear("2023");
			
			List<String> list=new ArrayList<String>();
//			list.add("77119195643");
//			list.add("77220813968");
			ArrayList<String> masterKey=new ArrayList<String>();
			masterKey.add("80");
			masterKey.add("81");
			String sem="4";
			
			
			List<StudentExamBean> studlist=new ArrayList<StudentExamBean>();
			StudentExamBean bean=new StudentExamBean();
			bean.setSem("4");
			bean.setSapid("77119195643");
			bean.setMonth("Jan");
			bean.setYear("2021");
			bean.setFirstName("Riddhi");
			bean.setLastName("Parekh");
			bean.setEmailId("pradeepwaghmode8@gmail.com");
			bean.setMobile("7709064144");
			bean.setProgram("PGDBM - HRM");
			bean.setEnrollmentMonth("Jul");
			bean.setEnrollmentYear("2019");
			bean.setValidityEndMonth("Jun");
			bean.setValidityEndYear("2023");
			bean.setCenterCode("a020o00000xCDja");
			studlist.add(bean);
			
			
			try {
			
				when(projectSubmissionDAO.getProjectSubmissionlist(fileset.getYear(), fileset.getMonth())).thenReturn(list);
				when(projectSubmissionDAO.getProjectPaymentStatus(fileset.getMonth(), fileset.getYear())).thenReturn(list);
				when( projectSubmissionDAO.getProjectPaymentStatusFromHistory(fileset.getMonth(), fileset.getYear())).thenReturn(list);
				when(aDao.getAllMasterKeysWithProject()).thenReturn(masterKey);
				when(projectSubmissionDAO.getProjectApplicableSem("81")).thenReturn(sem);
				when(levelBasedProjectService.getProjectApplicableStudents("81",sem)).thenReturn(studlist);
				when(levelBasedProjectService.isStudentValid(bean.getSapid(),bean.getValidityEndMonth(),bean.getValidityEndYear())).thenReturn(true);
				when(levelBasedProjectService.checkIfStudentPassProject(bean.getSapid(),"Project","Y")).thenReturn(false);
			List<String> sapids=projectSubmissionDAO.getProjectSubmissionlist(fileset.getYear(),fileset.getMonth());
			}
			
			catch (Exception e) {
				// TODO: handle exception
				e.printStackTrace();
			}
			try {
			
			eligiblelist=projectSubmissionService.getProjectPendingReport(userAuthorization, fileset);
			System.out.println("Total student count ::"+eligiblelist.size());
			
			boolean flag=false;
			
			for(StudentExamBean studbean:eligiblelist) {
				System.out.println(studbean.getStatus());
				if(!(studbean.getStatus()=="Submitted" )) {
					flag=true;
					}
			}
			
			assertEquals(true,flag);
			}
			catch (Exception e) {
				System.out.println("Execption is :: "+e.getMessage());
				e.printStackTrace();
				// TODO: handle exception
			}
			
		}
		
		/*
		 * calling service method getProjectPendingReport(userAuthorization, fileset) of
		 * class ProjectSubmissionServiceImpl and checking project payment status in
		 * first exam cycle if payment status is recived for first exam cycle then test
		 * case result will positive and my student data is "student is done project
		 * payment in first cycle which is April 2023 and student is succussefully
		 * submitted project in same exam cycle wich is April 2023"--sapid of that
		 * student is 77119195643
		 */
	@Test
	public void testProjectPendingReportByPassingThirdExamCycleForProjectPaymentStatus() {
		MockitoAnnotations.initMocks(this); 
		System.out.println("Testt.servicTest()");
		List<StudentExamBean> eligiblelist = new ArrayList<StudentExamBean>();
		AssignmentFilesSetbean fileset=new AssignmentFilesSetbean();
		fileset.setMonth("Sep");
		fileset.setYear("2023");
		
		List<String> list=new ArrayList<String>();
//		list.add("77119195643");
//		list.add("77220813968");
		ArrayList<String> masterKey=new ArrayList<String>();
		masterKey.add("80");
		masterKey.add("81");
		String sem="4";
		
		
		List<StudentExamBean> studlist=new ArrayList<StudentExamBean>();
		StudentExamBean bean=new StudentExamBean();
		bean.setSem("4");
		bean.setSapid("77119195643");
		bean.setMonth("Jan");
		bean.setYear("2021");
		bean.setFirstName("Riddhi");
		bean.setLastName("Parekh");
		bean.setEmailId("pradeepwaghmode8@gmail.com");
		bean.setMobile("7709064144");
		bean.setProgram("PGDBM - HRM");
		bean.setEnrollmentMonth("Jul");
		bean.setEnrollmentYear("2019");
		bean.setValidityEndMonth("Jun");
		bean.setValidityEndYear("2023");
		bean.setCenterCode("a020o00000xCDja");
		studlist.add(bean);
		
		
		try {
		
			when(projectSubmissionDAO.getProjectSubmissionlist(fileset.getYear(), fileset.getMonth())).thenReturn(list);
			when(projectSubmissionDAO.getProjectPaymentStatus(fileset.getMonth(), fileset.getYear())).thenReturn(list);
			when( projectSubmissionDAO.getProjectPaymentStatusFromHistory(fileset.getMonth(), fileset.getYear())).thenReturn(list);
			when(aDao.getAllMasterKeysWithProject()).thenReturn(masterKey);
			when(projectSubmissionDAO.getProjectApplicableSem("81")).thenReturn(sem);
			when(levelBasedProjectService.getProjectApplicableStudents("81",sem)).thenReturn(studlist);
			when(levelBasedProjectService.isStudentValid(bean.getSapid(),bean.getValidityEndMonth(),bean.getValidityEndYear())).thenReturn(true);
			when(levelBasedProjectService.checkIfStudentPassProject(bean.getSapid(),"Project","Y")).thenReturn(false);
		List<String> sapids=projectSubmissionDAO.getProjectSubmissionlist(fileset.getYear(),fileset.getMonth());
		}
		
		catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
		try {
		
		eligiblelist=projectSubmissionService.getProjectPendingReport(userAuthorization, fileset);
		System.out.println("Total student count ::"+eligiblelist.size());
		
		boolean flag=false;
		
				for(StudentExamBean studbean:eligiblelist) {
					
					if(!(studbean.getBooked()=="Y" ))
						flag=true;
				}
				
		//assertThat(eligiblelist.size()).isGreaterThan(0);
		assertEquals(true,flag);
		}
		catch (Exception e) {
			System.out.println("Execption is :: "+e.getMessage());
			e.printStackTrace();
			// TODO: handle exception
		}
		
	}
	
	/*
	 * calling service method getProjectPendingReport(userAuthorization, fileset) of
	 * class ProjectSubmissionServiceImpl and checking project submission status in
	 * first exam cycle, if project submission status is submitted for first exam
	 * cycle then test case result will positive and my student data is "student is
	 * done project payment in first cycle which is April 2023 and student is
	 * succussefully submitted project in same exam cycle wich is April 2023"--sapid
	 * of that student is 77119195643
	 */
	@Test
	public void testProjectPendingReportByPassingThirdExamCycleForProjectProjectSubmissionStatus() {
		MockitoAnnotations.initMocks(this); 
		System.out.println("Testt.servicTest()");
		List<StudentExamBean> eligiblelist = new ArrayList<StudentExamBean>();
		AssignmentFilesSetbean fileset=new AssignmentFilesSetbean();
		fileset.setMonth("Sep");
		fileset.setYear("2023");
		
		List<String> list=new ArrayList<String>();
//		list.add("77119195643");
//		list.add("77220813968");
		ArrayList<String> masterKey=new ArrayList<String>();
		masterKey.add("80");
		masterKey.add("81");
		String sem="4";
		
		
		List<StudentExamBean> studlist=new ArrayList<StudentExamBean>();
		StudentExamBean bean=new StudentExamBean();
		bean.setSem("4");
		bean.setSapid("77119195643");
		bean.setMonth("Jan");
		bean.setYear("2021");
		bean.setFirstName("Riddhi");
		bean.setLastName("Parekh");
		bean.setEmailId("pradeepwaghmode8@gmail.com");
		bean.setMobile("7709064144");
		bean.setProgram("PGDBM - HRM");
		bean.setEnrollmentMonth("Jul");
		bean.setEnrollmentYear("2019");
		bean.setValidityEndMonth("Jun");
		bean.setValidityEndYear("2023");
		bean.setCenterCode("a020o00000xCDja");
		studlist.add(bean);
		
		
		try {
		
			when(projectSubmissionDAO.getProjectSubmissionlist(fileset.getYear(), fileset.getMonth())).thenReturn(list);
			when(projectSubmissionDAO.getProjectPaymentStatus(fileset.getMonth(), fileset.getYear())).thenReturn(list);
			when( projectSubmissionDAO.getProjectPaymentStatusFromHistory(fileset.getMonth(), fileset.getYear())).thenReturn(list);
			when(aDao.getAllMasterKeysWithProject()).thenReturn(masterKey);
			when(projectSubmissionDAO.getProjectApplicableSem("81")).thenReturn(sem);
			when(levelBasedProjectService.getProjectApplicableStudents("81",sem)).thenReturn(studlist);
			when(levelBasedProjectService.isStudentValid(bean.getSapid(),bean.getValidityEndMonth(),bean.getValidityEndYear())).thenReturn(true);
			when(levelBasedProjectService.checkIfStudentPassProject(bean.getSapid(),"Project","Y")).thenReturn(false);
		List<String> sapids=projectSubmissionDAO.getProjectSubmissionlist(fileset.getYear(),fileset.getMonth());
		}
		
		catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
		try {
		
		eligiblelist=projectSubmissionService.getProjectPendingReport(userAuthorization, fileset);
		System.out.println("Total student count ::"+eligiblelist.size());
		
		boolean flag=false;
		
		for(StudentExamBean studbean:eligiblelist) {
			System.out.println(studbean.getStatus());
			if(!(studbean.getStatus()=="Submitted" )){
				flag=true;
				}
		}
		
		assertEquals(true,flag);
		}
		catch (Exception e) {
			System.out.println("Execption is :: "+e.getMessage());
			e.printStackTrace();
			// TODO: handle exception
		}
		
	}
	
	/*
	 * calling service method getProjectPendingReport(userAuthorization, fileset) of
	 * class ProjectSubmissionServiceImpl and checking project payment status in
	 * first exam cycle if payment status is recived for first exam cycle then test
	 * case result will positive and my student data is "student is done project
	 * payment in first cycle which is April 2023 and student is succussefully
	 * submitted project in same exam cycle wich is April 2023"--sapid of that
	 * student is 77119195643
	 */
	@Test
	public void testProjectPendingReportByPassingFourthExamCycleForProjectPaymentStatus() {
		MockitoAnnotations.initMocks(this); 
		System.out.println("Testt.servicTest()");
		List<StudentExamBean> eligiblelist = new ArrayList<StudentExamBean>();
		AssignmentFilesSetbean fileset=new AssignmentFilesSetbean();
		fileset.setMonth("Dec");
		fileset.setYear("2023");
		
		List<String> list=new ArrayList<String>();
//		list.add("77119195643");
//		list.add("77220813968");
		ArrayList<String> masterKey=new ArrayList<String>();
		masterKey.add("80");
		masterKey.add("81");
		String sem="4";
		
		
		List<StudentExamBean> studlist=new ArrayList<StudentExamBean>();
		StudentExamBean bean=new StudentExamBean();
		bean.setSem("4");
		bean.setSapid("77119195643");
		bean.setMonth("Jan");
		bean.setYear("2021");
		bean.setFirstName("Riddhi");
		bean.setLastName("Parekh");
		bean.setEmailId("pradeepwaghmode8@gmail.com");
		bean.setMobile("7709064144");
		bean.setProgram("PGDBM - HRM");
		bean.setEnrollmentMonth("Jul");
		bean.setEnrollmentYear("2019");
		bean.setValidityEndMonth("Jun");
		bean.setValidityEndYear("2023");
		bean.setCenterCode("a020o00000xCDja");
		studlist.add(bean);
		
		
		try {
		
			when(projectSubmissionDAO.getProjectSubmissionlist(fileset.getYear(), fileset.getMonth())).thenReturn(list);
			when(projectSubmissionDAO.getProjectPaymentStatus(fileset.getMonth(), fileset.getYear())).thenReturn(list);
			when( projectSubmissionDAO.getProjectPaymentStatusFromHistory(fileset.getMonth(), fileset.getYear())).thenReturn(list);
			when(aDao.getAllMasterKeysWithProject()).thenReturn(masterKey);
			when(projectSubmissionDAO.getProjectApplicableSem("81")).thenReturn(sem);
			when(levelBasedProjectService.getProjectApplicableStudents("81",sem)).thenReturn(studlist);
			when(levelBasedProjectService.isStudentValid(bean.getSapid(),bean.getValidityEndMonth(),bean.getValidityEndYear())).thenReturn(true);
			when(levelBasedProjectService.checkIfStudentPassProject(bean.getSapid(),"Project","Y")).thenReturn(false);
		List<String> sapids=projectSubmissionDAO.getProjectSubmissionlist(fileset.getYear(),fileset.getMonth());
		}
		
		catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
		try {
		
		eligiblelist=projectSubmissionService.getProjectPendingReport(userAuthorization, fileset);
		System.out.println("Total student count ::"+eligiblelist.size());
		
		boolean flag=false;
		
				for(StudentExamBean studbean:eligiblelist) {
					
					if(!(studbean.getBooked()=="Y" ))
						flag=true;
				}
				
		//assertThat(eligiblelist.size()).isGreaterThan(0);
		assertEquals(true,flag);
		}
		catch (Exception e) {
			System.out.println("Execption is :: "+e.getMessage());
			e.printStackTrace();
			// TODO: handle exception
		}
		
	}
	
	/*
	 * calling service method getProjectPendingReport(userAuthorization, fileset) of
	 * class ProjectSubmissionServiceImpl and checking project submission status in
	 * first exam cycle, if project submission status is submitted for first exam
	 * cycle then test case result will positive and my student data is "student is
	 * done project payment in first cycle which is April 2023 and student is
	 * succussefully submitted project in same exam cycle wich is April 2023"--sapid
	 * of that student is 77119195643
	 */
	@Test
	public void testProjectPendingReportByPassingFourthExamCycleForProjectProjectSubmissionStatus() {
		MockitoAnnotations.initMocks(this); 
		System.out.println("Testt.servicTest()");
		List<StudentExamBean> eligiblelist = new ArrayList<StudentExamBean>();
		AssignmentFilesSetbean fileset=new AssignmentFilesSetbean();
		fileset.setMonth("Dec");
		fileset.setYear("2023");
		
		List<String> list=new ArrayList<String>();
//		list.add("77119195643");
//		list.add("77220813968");
		ArrayList<String> masterKey=new ArrayList<String>();
		masterKey.add("80");
		masterKey.add("81");
		String sem="4";
		
		
		List<StudentExamBean> studlist=new ArrayList<StudentExamBean>();
		StudentExamBean bean=new StudentExamBean();
		bean.setSem("4");
		bean.setSapid("77119195643");
		bean.setMonth("Jan");
		bean.setYear("2021");
		bean.setFirstName("Riddhi");
		bean.setLastName("Parekh");
		bean.setEmailId("pradeepwaghmode8@gmail.com");
		bean.setMobile("7709064144");
		bean.setProgram("PGDBM - HRM");
		bean.setEnrollmentMonth("Jul");
		bean.setEnrollmentYear("2019");
		bean.setValidityEndMonth("Jun");
		bean.setValidityEndYear("2023");
		bean.setCenterCode("a020o00000xCDja");
		studlist.add(bean);
		
		
		try {
		
			when(projectSubmissionDAO.getProjectSubmissionlist(fileset.getYear(), fileset.getMonth())).thenReturn(list);
			when(projectSubmissionDAO.getProjectPaymentStatus(fileset.getMonth(), fileset.getYear())).thenReturn(list);
			when( projectSubmissionDAO.getProjectPaymentStatusFromHistory(fileset.getMonth(), fileset.getYear())).thenReturn(list);
			when(aDao.getAllMasterKeysWithProject()).thenReturn(masterKey);
			when(projectSubmissionDAO.getProjectApplicableSem("81")).thenReturn(sem);
			when(levelBasedProjectService.getProjectApplicableStudents("81",sem)).thenReturn(studlist);
			when(levelBasedProjectService.isStudentValid(bean.getSapid(),bean.getValidityEndMonth(),bean.getValidityEndYear())).thenReturn(true);
			when(levelBasedProjectService.checkIfStudentPassProject(bean.getSapid(),"Project","Y")).thenReturn(false);
		List<String> sapids=projectSubmissionDAO.getProjectSubmissionlist(fileset.getYear(),fileset.getMonth());
		}
		
		catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
		try {
		
		eligiblelist=projectSubmissionService.getProjectPendingReport(userAuthorization, fileset);
		System.out.println("Total student count ::"+eligiblelist.size());
		
		boolean flag=false;
		
		for(StudentExamBean studbean:eligiblelist) {
			System.out.println(studbean.getStatus());
			if(!(studbean.getStatus()=="Submitted" ) ){
				flag=true;
				}
		}
		
		assertEquals(true,flag);
		}
		catch (Exception e) {
			System.out.println("Execption is :: "+e.getMessage());
			e.printStackTrace();
			// TODO: handle exception
		}
		
	}
	
	
	
}
