package com.nmims.services;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Service;

import com.nmims.beans.AssignmentFilesSetbean;
import com.nmims.beans.StudentExamBean;
import com.nmims.beans.UserAuthorizationExamBean;
import com.nmims.daos.AssignmentsDAO;
import com.nmims.daos.ProjectSubmissionDAO;

@Service
public class ProjectSubmissionServiceImpl implements IProjectSubmissionService {

	@Autowired
	ApplicationContext act;
	@Autowired
	ProjectSubmissionDAO projectSubmissionDAO;
	@Autowired
	LevelBasedProjectService levelBasedProjectService;
	@Value("${CURRENT_ACAD_MONTH}")
	private String CURRENT_ACAD_MONTH;

	@Value("${CURRENT_ACAD_YEAR}")
	private String CURRENT_ACAD_YEAR;
	private static final Logger logger = LoggerFactory.getLogger(ProjectSubmissionServiceImpl.class);
	@Autowired
	@Qualifier("asignmentsDAO")
	AssignmentsDAO aDao;

	@Override
	public List<StudentExamBean> getProjectPendingReport(UserAuthorizationExamBean userAuthorizationBean,
			AssignmentFilesSetbean filesSet) throws Exception {
		String subject = "Project";
		String isPass = "Y";
		List<StudentExamBean> eligiblelist = new ArrayList<StudentExamBean>();
		List<String> resitCycleMonth = new ArrayList(Arrays.asList("Apr", "Sep"));
		String year = filesSet.getYear();
		String month = filesSet.getMonth();
		List<String> projectSubmittedStudentslist = projectSubmissionDAO.getProjectSubmissionlist(year, month);
		List<String> projectPaymentSubmittedStudentslist = projectSubmissionDAO.getProjectPaymentStatus(month, year);
		List<String> projectPaymentSubmittedNotInCurrentYearStudentslist = projectSubmissionDAO.getProjectPaymentStatusFromHistory(month, year);

		// Get userAuthorization by session
//			UserAuthorizationExamBean userAuthorization = userAuthorizationBean;
		// Fetch all authorized from userAuthorization if any
		ArrayList<String> authorizedCenterCodesList = userAuthorizationBean.getAuthorizedCenterCodes();

		/* find masterkey of input program */
		ArrayList<String> consumerProgramStructureIds = aDao.getAllMasterKeysWithProject();

		for (String cpsId : consumerProgramStructureIds) {

			/* fetch sem in which project applicable */
			String sem = projectSubmissionDAO.getProjectApplicableSem(cpsId);

			/*
			 * find all eligible students. Copied logic of pdwm eligible students list
			 * report
			 */
			List<StudentExamBean> studentDetailList = levelBasedProjectService.getProjectApplicableStudents(cpsId, sem);

			/**
			 * Added By - shivam.pandey.EXT [START]
			 */
			/*
			 * To extract authorized center codes from all student list except logged in
			 * user authorized center codes
			 */
			// If authorizedCenterCodesList have not null or not empty
			if (!authorizedCenterCodesList.isEmpty()) {
				// Created temp list of bean bean
				List<StudentExamBean> studentLCWiseDetailList = new ArrayList<>();
				// Iterating temp bean
				studentDetailList.forEach(bean -> {
					// If center code matched with student
					if (authorizedCenterCodesList.contains(bean.getCenterCode())) {
						// Add bean in temp list of bean
						studentLCWiseDetailList.add(bean);
					}
				});
				// Swap temp list to studentDetails List, and this list only include those
				// students whose centerCode have matched with authorized center code
				studentDetailList = studentLCWiseDetailList;
			}
			/**
			 * Added By - shivam.pandey.EXT [END]
			 */


			if (studentDetailList.size() > 0) {
				for (StudentExamBean StudentBean : studentDetailList) {

					/* removing all validity expired students */
					boolean isValid = levelBasedProjectService.isStudentValid(StudentBean.getSapid(),
							StudentBean.getValidityEndMonth(), StudentBean.getValidityEndYear());
					if (isValid) {

						/* removing all students passed Project */
						boolean studentPassProject = levelBasedProjectService
								.checkIfStudentPassProject(StudentBean.getSapid(), "Project", isPass);
						if (!studentPassProject) {

							/* removing all students submitted Project in current Project live cycle */
//								AssignmentFileBean studentSubmissionStatus = projectSubmissionDAO.getProjectSubmissionStatus(subject, StudentBean.getSapid());
//				    			if(studentSubmissionStatus == null){
//			    					eligiblelist.add(list);
//				    			}

							/**
							 * Added By - Pradeep.Waghmode.EXT [START]
							 */

							/*
							 * below if block is checking payment is done or not and setting status
							 * accordingly
							 */
							StudentBean.setBooked("N");
							if (projectPaymentSubmittedStudentslist.contains(StudentBean.getSapid())) {
								StudentBean.setBooked("Y");
							} else if (projectPaymentSubmittedNotInCurrentYearStudentslist
									.contains(StudentBean.getSapid())) {
								StudentBean.setBooked("Y");
							}
							/*
							 * below if block is checking project is submitted or not and setting status
							 * accordingly
							 */
							if (projectSubmittedStudentslist.contains(StudentBean.getSapid())) {
								StudentBean.setStatus("Submitted");
								eligiblelist.add(StudentBean);
							} else {
								StudentBean.setStatus("Not Submitted");
								eligiblelist.add(StudentBean);
							}

							/**
							 * Added By - Pradeep.waghmode.EXT [END]
							 */

							// Remove fresh student if exam cycle is resit
							if (resitCycleMonth.contains(filesSet.getMonth())) {
								if (StudentBean.getMonth().equalsIgnoreCase(CURRENT_ACAD_MONTH)
										&& StudentBean.getYear().equalsIgnoreCase(CURRENT_ACAD_YEAR)) {
									eligiblelist.remove(StudentBean);
								}
							}
						}
					}

				}
			}
		}

		return eligiblelist;
	}

}
