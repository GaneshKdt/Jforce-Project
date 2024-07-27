package com.nmims.services;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.nmims.beans.FacultyStudentPortalBean;
import com.nmims.beans.SessionPlanModulePg;
import com.nmims.beans.SessionPlanPgBean;
import com.nmims.daos.FacultyDAO;
import com.nmims.daos.SessionPlanPgDao;
import com.nmims.interfaces.SessionPlanPGInterface;

/**
 * This class implements the SessionPlanPGInterface and provides session plan module details
 * for a given programSemSubjectId.
 * 
 * It is a service class annotated with the Spring Framework's @Service annotation.
 * 
 * @see SessionPlanPGInterface
 */
@Service
public class SessionPlanPgService implements SessionPlanPGInterface{
	
	@Autowired
	SessionPlanPgDao sessionPlanPgDao;
	
	@Autowired
	FacultyDAO facultyDao;
	
	
	/**
	 * Retrieves the mapping of faculty IDs to faculty details.
	 * 
	 * @return A HashMap representing the mapping of facultyId as Key to FacultyStudentPortalBean objects.
	 */
	private HashMap<String, FacultyStudentPortalBean> mapOfFacultyIdAndFacultyDetails() {
		ArrayList<FacultyStudentPortalBean> listOfAllFaculties = facultyDao.getAllFacultyRecords();
		HashMap<String, FacultyStudentPortalBean> mapOfFacultyIdAndFacultyRecord = new HashMap<String, FacultyStudentPortalBean>();
			for (FacultyStudentPortalBean faculty : listOfAllFaculties) {
				mapOfFacultyIdAndFacultyRecord.put(faculty.getFacultyId(), faculty);
			}
		return mapOfFacultyIdAndFacultyRecord;

	}
	
	
	/**
	 * Fetches the session plan module details for the given programSemSubjectId.
	 * 
	 * @param programSemSubjectId The programSemSubjectId for which to fetch the session plan module details.
	 * @param sapId The unique id of student
	 * @return The SessionPlanPgBean object containing the session plan module details.
	 * @throws Exception If an error occurs during the fetch operation.
	 */
	@Override
	public SessionPlanPgBean fetchModuleDetails(String programSemSubjectId, String sapId) throws Exception {
		int subjectCodeId = sessionPlanPgDao.getSubjectCodeIdByPssId(programSemSubjectId);

		SessionPlanPgBean sessionPlanBean = sessionPlanPgDao.getSessionPlanDetails(subjectCodeId);

		List<SessionPlanModulePg> moduleList = sessionPlanPgDao.getSessionPlanModuleDetails(sessionPlanBean.getId());

		if (moduleList != null) {
			List<SessionPlanModulePg> listOfModleDetailsUsingQuizScores = fetchStudentAttemptedQuizScore(moduleList,
					sapId);
			sessionPlanBean.setSessionPlanModuleList(listOfModleDetailsUsingQuizScores);
		}

		if (!StringUtils.isBlank(sessionPlanBean.getFacultyId())) {
			sessionPlanBean.setFacultyName(
					mapOfFacultyIdAndFacultyDetails().get(sessionPlanBean.getFacultyId()).getFirstName() + " "
							+ mapOfFacultyIdAndFacultyDetails().get(sessionPlanBean.getFacultyId()).getLastName());
		}

		return sessionPlanBean;
	}

	/**
	 * Fetches the attempted quiz scores for a student and updates the corresponding module details.
	 * 
	 * @param moduleList The list of session plan module objects.
	 * @param sapId The SAP ID of the student.
	 * @return The updated list of session plan module objects with attempted quiz scores.
	 * @throws Exception if an error occurs during the process.
	 */
	private List<SessionPlanModulePg> fetchStudentAttemptedQuizScore(List<SessionPlanModulePg> moduleList, String sapId)
			throws Exception {
		try {
			List<Integer> idList = moduleList.stream().map(SessionPlanModulePg::getId).collect(Collectors.toList());

			List<SessionPlanModulePg> listofAttemptedQuizForStudent = sessionPlanPgDao
					.getAttemptedQuizForStudent(sapId);
			List<SessionPlanModulePg> listOfTestId = sessionPlanPgDao.getListOfQuizesForModules(idList);

			listofAttemptedQuizForStudent.parallelStream().forEach(bean1 -> {
				listOfTestId.parallelStream().filter(bean2 -> bean1.getTestId() == bean2.getTestId()).findFirst()
						.ifPresent(bean2 -> bean1.setTestReferenceId(bean2.getTestReferenceId()));
			});

			moduleList.parallelStream().forEach(moduleDetailsBean -> {
				listofAttemptedQuizForStudent.parallelStream()
						.filter(quizDetailsBean -> moduleDetailsBean.getId() == quizDetailsBean.getTestReferenceId())
						.findFirst().ifPresent(quizDetailsBean -> {
							moduleDetailsBean.setAttemptStatus(quizDetailsBean.getAttemptStatus());
							moduleDetailsBean.setQuizScore(quizDetailsBean.getQuizScore());
						});
			});
		} catch (Exception e) {
			e.printStackTrace();
		}
		return moduleList;
	}
}
