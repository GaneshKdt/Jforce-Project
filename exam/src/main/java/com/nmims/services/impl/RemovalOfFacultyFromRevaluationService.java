package com.nmims.services.impl;

import java.util.ArrayList;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.nmims.beans.AssignmentFileBean;
import com.nmims.beans.RemovalOfFacultyFromAllStageOfRevaluationBean;
import com.nmims.controllers.RemovalOfFacultyFromRevaluationController;
import com.nmims.daos.FacultyDAO;
import com.nmims.daos.RemovalOfFacultyFromRevaluationDao;
import com.nmims.daos.StudentMarksDAO;
import com.nmims.services.RemovalOfFacultyFromRevaluationServiceInterface;

@Service
public class RemovalOfFacultyFromRevaluationService implements RemovalOfFacultyFromRevaluationServiceInterface {

	@Autowired
	RemovalOfFacultyFromRevaluationDao removalOfFacultyFromRevaluation;
	

	@Autowired
	private StudentMarksDAO studentMarksDao;

	@Autowired
	private FacultyDAO facultyDao;


	private static final Logger logger = LoggerFactory.getLogger(RemovalOfFacultyFromRevaluationController.class);

	public ArrayList<AssignmentFileBean> searchFacultyFromAllStagesOfRevaluation(String examYear, String examMonth,
			String subject, String facultyId)  {
		ArrayList<AssignmentFileBean> searchFacultyList = new ArrayList<AssignmentFileBean>();
		try {
			searchFacultyList = removalOfFacultyFromRevaluation.SearchFacultyFromAllStages(examYear, examMonth, subject,
					facultyId);
			
			logger.info("Search Faculty List Size {} ", searchFacultyList.size());
			
		} catch (Exception e) {
			
			logger.error(facultyId, "{} Error While Getting Faculty List year {} Month {}", examYear, examMonth );
			logger.error("Error Searching Faculty" + e);
			throw new RuntimeException("Error Searching Faculty ");
			
		}
		return searchFacultyList;
	}

	public RemovalOfFacultyFromAllStageOfRevaluationBean removeFacultyFromAllStagesOfRevaluation(String examYear, String examMonth, String subject,
			String facultyId,String userId) {
		RemovalOfFacultyFromAllStageOfRevaluationBean  totalRowsUpdated = new RemovalOfFacultyFromAllStageOfRevaluationBean();
		try {

			int removalOfFacultyFromStageTwoAssignmentSubmission = removalOfFacultyFromRevaluation
					.removalOfactultyFromAssignmentSubmissionStageTwo(examYear, examMonth, subject, facultyId,userId);

			logger.info("Rows Updated Faculty From Stage Two Assignment Submission {} "
					, removalOfFacultyFromStageTwoAssignmentSubmission);

			int removalOfFacultyFromStageThreeAssignmentSubmission = removalOfFacultyFromRevaluation
					.removalOfactultyFromAssignmentSubmissionStageThree(examYear, examMonth, subject, facultyId,userId);
			logger.info("Rows Updated Faculty From Stage Three Assignment Submission {} "
					, removalOfFacultyFromStageThreeAssignmentSubmission);

			int removalOfFacultyFromStageFourAssignmentSubmission = removalOfFacultyFromRevaluation
					.removalOfactultyFromAssignmentSubmissionStageFour(examYear, examMonth, subject, facultyId,userId);
			logger.info("Rows Updated Faculty From Stage Four Assignment Submission {} "
					, removalOfFacultyFromStageFourAssignmentSubmission);

			int removalOfFacultyFromQAssignmentSubmissionStageTwo = removalOfFacultyFromRevaluation
					.removalOfactultyFromQAssignmentSubmissionStageTwo(examYear, examMonth, subject, facultyId,userId);
			logger.info("Rows Updated Faculty From Stage Two  Quick Assignment Submission {} "
					, removalOfFacultyFromQAssignmentSubmissionStageTwo);

			int removalOfFacultyFromQAssignmentSubmissionStageThree = removalOfFacultyFromRevaluation
					.removalOfactultyFromQAssignmentSubmissionStageThree(examYear, examMonth, subject, facultyId,userId);
			logger.info("Rows Updated Faculty From Stage Three  Quick Assignment Submission {} "
					, removalOfFacultyFromQAssignmentSubmissionStageThree);
			
			
			int removalOfFacultyFromQAssignmentSubmissionStageFour = removalOfFacultyFromRevaluation
					.removalOfactultyFromQAssignmentSubmissionStageFour(examYear, examMonth, subject, facultyId,userId);
			logger.info("Rows Updated Faculty From Stage Four Quick Assignment Submission {} "
					, removalOfFacultyFromQAssignmentSubmissionStageFour);
			
			

			totalRowsUpdated.setRowsAffectedFromAssignmentSubmission(removalOfFacultyFromStageTwoAssignmentSubmission
					+ removalOfFacultyFromStageThreeAssignmentSubmission
					+ removalOfFacultyFromStageFourAssignmentSubmission);
			totalRowsUpdated.setRowsAffectedFromQAssignmentSubmission(removalOfFacultyFromQAssignmentSubmissionStageTwo
					+ removalOfFacultyFromQAssignmentSubmissionStageThree
					+ removalOfFacultyFromQAssignmentSubmissionStageFour);
			logger.info("totalRowsUpdated  : {}", totalRowsUpdated);

		} catch (Exception e) {
			logger.error(facultyId, "{}  Error In Removing Faculty From All Stages : Year {}  Month {} Subject {}",
					examYear, examMonth, subject );
			logger.error("Error Updating Faculty" + e);
			throw new RuntimeException("Error Removing Faculty ");
		}
		return totalRowsUpdated;
	}


	public ArrayList<String> getAllFaculties() {
		return facultyDao.getAllFaculties();
	}

	public ArrayList<String> getActiveSubjects() {
		
		return studentMarksDao.getActiveSubjects();
	}

}
