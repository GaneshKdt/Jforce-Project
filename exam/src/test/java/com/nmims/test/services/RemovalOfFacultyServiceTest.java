package com.nmims.test.services;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.when;

import java.util.ArrayList;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.transaction.annotation.Transactional;

import com.nmims.beans.RemovalOfFacultyFromAllStageOfRevaluationBean;
import com.nmims.daos.RemovalOfFacultyFromRevaluationDaoImpl;
import com.nmims.services.impl.RemovalOfFacultyFromRevaluationService;

@SpringBootTest
@RunWith(SpringJUnit4ClassRunner.class)
public class RemovalOfFacultyServiceTest {
	
	
@MockBean
RemovalOfFacultyFromRevaluationDaoImpl removalOfFacultyFromRevaluationDao;

@Autowired
RemovalOfFacultyFromRevaluationService removalOfFacultyRevaluationService;


@Test
@Transactional
@Rollback(true)
public void removeFacultyFromAllStagesOfRevaluationTest() {
	String examYear = "2022" ;
	String  examMonth = "Jun";
	String subject = null;
	String userId = null;
	String facultyId = "NGASCE0445"; 
	RemovalOfFacultyFromAllStageOfRevaluationBean expectedOutPut= new RemovalOfFacultyFromAllStageOfRevaluationBean();
	RemovalOfFacultyFromAllStageOfRevaluationBean actualOutput = new RemovalOfFacultyFromAllStageOfRevaluationBean();
	expectedOutPut.setRowsAffectedFromAssignmentSubmission(1);
	expectedOutPut.setRowsAffectedFromQAssignmentSubmission(1);
	when(removalOfFacultyFromRevaluationDao.removalOfactultyFromAssignmentSubmissionStageTwo(examYear, examMonth, subject, facultyId,userId)).thenReturn(1);
	when(removalOfFacultyFromRevaluationDao.removalOfactultyFromAssignmentSubmissionStageThree(examYear, examMonth, subject, facultyId,userId)).thenReturn(1);
	when(removalOfFacultyFromRevaluationDao.removalOfactultyFromAssignmentSubmissionStageFour(examYear, examMonth, subject, facultyId,userId)).thenReturn(0);
	when(removalOfFacultyFromRevaluationDao.removalOfactultyFromQAssignmentSubmissionStageTwo(examYear, examMonth, subject, facultyId,userId)).thenReturn(0);
	when(removalOfFacultyFromRevaluationDao.removalOfactultyFromQAssignmentSubmissionStageThree(examYear, examMonth, subject, facultyId,userId)).thenReturn(1);
	when(removalOfFacultyFromRevaluationDao.removalOfactultyFromQAssignmentSubmissionStageFour(examYear, examMonth, subject, facultyId,userId)).thenReturn(0);
	
	actualOutput =removalOfFacultyRevaluationService.removeFacultyFromAllStagesOfRevaluation(examYear, examMonth, subject, facultyId,userId);
	System.out.println("Actual Output "+ actualOutput);
	System.out.println("Expected Output "+ expectedOutPut);

	assertEquals(actualOutput,expectedOutPut);
}
	
}
