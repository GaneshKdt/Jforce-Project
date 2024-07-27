package com.nmims.stratergies.impl;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.nmims.beans.AssignmentLiveSettingStudentPortal;
import com.nmims.beans.AssignmentStudentPortalFileBean;
import com.nmims.daos.AssignmentsDAO;
import com.nmims.daos.PortalDao;
import com.nmims.services.AssignmentServiceInterface;

@Service("AssignmentServiceImpl")
public class AssignmentServiceImpl implements AssignmentServiceInterface{

	@Autowired
	AssignmentsDAO asignmentsDAO;
	
	@Autowired
	PortalDao portalDAO;
	
	private static final Logger assg_logger_mobile = LoggerFactory.getLogger("assignmentSubmissionMobile");
	
	@Override
	public List<AssignmentStudentPortalFileBean> mgetAssignmentsForStudents(final String sapid, final String consumerProgramStructureId) {
		
		assg_logger_mobile.info("Pg visit /m/courseDetailsAssignments Sapid - {}",sapid);
		
		List<AssignmentStudentPortalFileBean> allAssignmentFilesList = new ArrayList<AssignmentStudentPortalFileBean>();
		List<AssignmentStudentPortalFileBean> quickAssignments = asignmentsDAO.getQuickAssignmentsForSingleStudent(sapid);
		ArrayList<AssignmentStudentPortalFileBean> currentSemAssignmentFilesList = new ArrayList<AssignmentStudentPortalFileBean>();
		ArrayList<AssignmentStudentPortalFileBean> failSubjectsAssignmentFilesList = new ArrayList<AssignmentStudentPortalFileBean>();
		
		ArrayList<String> failSubjects = new ArrayList<String>();
		AssignmentLiveSettingStudentPortal resitLive = asignmentsDAO.getCurrentLiveAssignment(consumerProgramStructureId, "Resit");
		
		//get most recent marks live from cache
		String asgMarksLiveMonth = asignmentsDAO.getLiveAssignmentMarksMonth();
		String asgMarksLiveYear = asignmentsDAO.getLiveAssignmentMarksYear();  
		String marksLiveYearMonth = asgMarksLiveMonth+"-"+asgMarksLiveYear;
		
		for (AssignmentStudentPortalFileBean q : quickAssignments) {
			if (!q.getCurrentSemSubject().equalsIgnoreCase("Y") 
					&& resitLive.getExamYear().equalsIgnoreCase(q.getYear()) && resitLive.getExamMonth().equalsIgnoreCase(q.getMonth()) ) {
				failSubjectsAssignmentFilesList.add(q);
				failSubjects.add(q.getSubject());
			}
		}
		// For ANS cases, where result is not declared, failed subject will also be
		// present in Current sem subject.
		// Give preference to it as Failed, so that assignment can be submitted and
		// remove from Current list
		// If result is live, hide assignments
		for (AssignmentStudentPortalFileBean q : quickAssignments) {
			if (q.getCurrentSemSubject().equalsIgnoreCase("Y") 
					&& !(q.getMonth()+"-"+q.getYear()).equalsIgnoreCase(marksLiveYearMonth)
					&& !(failSubjects.contains(q.getSubject()))) {
				currentSemAssignmentFilesList.add(q);
			}
		}
//		String currentSemEndDateTime = "";
		if (currentSemAssignmentFilesList.size() > 0) {
			allAssignmentFilesList.addAll(currentSemAssignmentFilesList);
//			currentSemEndDateTime = currentSemAssignmentFilesList.get(0).getEndDate().substring(0, 19);
//			response.setCurrentSemEndDateTime(currentSemEndDateTime);
		}
//		String failSubjectsEndDateTime = "";
		if (failSubjectsAssignmentFilesList.size() > 0) {
			allAssignmentFilesList.addAll(failSubjectsAssignmentFilesList);
//			failSubjectsEndDateTime = failSubjectsAssignmentFilesList.get(0).getEndDate().substring(0, 19);
			//response.setFailedSemSubjectsCount(failSubjectsAssignmentFilesList.size());
		}
		
		
		allAssignmentFilesList.stream()
				.forEach(assignment ->{
					if(assignment.getStatus().equals("Results Awaited")) {
						assignment.setSubmissionAllowed(false);
					}else {
						assignment.setSubmissionAllowed(true);
					}
					if(assignment.getPreviewPath()== null) {
						assignment.setPreviewPath(""); // for mobile app logic used in course-details/assignments set previewPath as empty
					}
				}
		);
		return allAssignmentFilesList;
	}

}
