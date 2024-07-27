package com.nmims.services;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StopWatch;

import com.nmims.beans.ExamBookingEligibileStudentBean;
import com.nmims.beans.ExamBookingStudentCycleSubjectConfig;
import com.nmims.beans.PassFailExamBean;
import com.nmims.beans.ProgramSubjectMappingExamBean;
import com.nmims.beans.StudentExamBean;
import com.nmims.beans.StudentMarksBean;
import com.nmims.beans.StudentSubjectMappingConfigBean;
import com.nmims.beans.StudentSubjectMappingConfigBean.StudentSubjectMappingPhaseBean;
import com.nmims.daos.ExamBookingDAO;
import com.nmims.daos.ExamBookingEligibilityDAO;
import com.nmims.daos.StudentDAO;

@Service
public class ExamBookingEligibilityService {

	@Autowired
	private ExamBookingEligibilityDAO dao;
	
	@Autowired
	private StudentService studentService;
	
	@Autowired
	private StudentDAO sDao;
	
	private List<String> listOfSubjectsNotAllowedToBookFrom = Arrays.asList(
		"Project", "Module 4 - Project", "Simulation: Mimic Pro", "Simulation: Mimic Social", "Soft Skills for Managers", "Design Thinking", "Employability Skills - II Tally"
	);
	
	private static final Logger logger = LoggerFactory.getLogger("examBookingEligibility");
	
	public List<ExamBookingStudentCycleSubjectConfig> generateStudentSubjectMappingsForUnmappedStudents(String year, String month) throws Exception {
		/* 
		 * Flow :
		 * 1) Fetch eligible students according to their validity along with their current registration month/year
		 * 2) For each student who is eligible to apply in the given cycle :
		 * 		2.1) Determine applicable cycle and if student is eligible to book for it under the program constraints
		 * 		2.2) Fetch all applicable subjects in the students current program registration.
		 * 		2.3) Get students passed subject names - This includes subjects passed in previous cycles as well.
		 * 		2.4) Get subjects which have been marked as 'waived in' for the current cycle (exam.student_course_mapping)
		 * 		2.5) Get subjects previously marked 'waived in' for student in previous cycles (This list will most likely be obsolete. logic still kept for being sure). 
		 * 		2.6) Get failed subjects for student
		 * 		2.7) For each subject applicable under the students registration
		 * 			2.7.1) Check if subject is in the list of subjects for which exam booking is not applicable. IE listOfSubjectsNotAllowedToBookFrom
		 * 			2.7.2) Apply various checks from the lists obtained above to determine if the student has failed in the subject, passed or if the subject is from the current cycle.
		 * 			2.7.3) Check if subject is in the list of subjects for which exam booking is not applicable. IE listOfSubjectsNotAllowedToBookFrom
		 * 			2.7.4) Add students data to the list of data to be inserted depending upon which cycle type is applicable to him and if submission is allowed for that subject according to the checks performed.
		 * 3) Insert into the temporary table
		 * 4) Return entries that werent inserted
		 */
		

		long startTime = Calendar.getInstance().getTimeInMillis();
		logger.info(" START generateStudentMappingsForCycle "+year+" / "+month);
		// #1
		List<ExamBookingEligibileStudentBean> currentlyEligibleStudents = dao.getCurrentlyEligibleStudentsList(year, month);
		logger.info(" currentlyEligibleStudents Size "+currentlyEligibleStudents.size());
		startTime = Calendar.getInstance().getTimeInMillis();
		List<ExamBookingStudentCycleSubjectConfig> eligibleList = new ArrayList<ExamBookingStudentCycleSubjectConfig>();
		// #2
		for (ExamBookingEligibileStudentBean studentInfo : currentlyEligibleStudents) {
			if("Diageo".equalsIgnoreCase(studentInfo.getConsumerType()) && month.equals("Sep") && month.equals("2019") ){
				// Dont run for this specific cycle in case of diageo students
				continue;
			}
			if("ACBM".equals(studentInfo.getProgram()) && (month.contains("Apr") || month.contains("Sep"))){
				continue;
			}
			
			// #2.1
			boolean isCurrentCycle;
			if(month.equals("Jun") || month.equals("Dec")) {
				if("Diageo".equalsIgnoreCase(studentInfo.getConsumerType())) {
					//for diageo jun/dec is resit attempt.
					isCurrentCycle = false;
				} else {
					isCurrentCycle = true;
				}
			} else if(month.equals("Apr") || month.equals("Sep")) {
				if("Diageo".equalsIgnoreCase(studentInfo.getConsumerType())) {
					//for diageo jun/dec is resit attempt.
					isCurrentCycle = false;
				} else if("ACBM".equals(studentInfo.getProgram())) {
					// ACBM students cant apply in Resit Cycles.
					continue;
				} else {
					isCurrentCycle = true;
				}
			} else {
				throw new Exception("Invalid cycle found!");
			}
			

			// #2.2
			logger.info(" sapid "+studentInfo.getSapid()+" masterKey "+studentInfo.getConsumerProgramStructureId()+" sem "+studentInfo.getSem() );
			List<ProgramSubjectMappingExamBean> subjectsEligibleForStudent = new ArrayList<ProgramSubjectMappingExamBean>();
			if("128".equals(studentInfo.getConsumerProgramStructureId()) && ("5".equals(studentInfo.getSem()) || "6".equals(studentInfo.getSem()) ) ) {
				subjectsEligibleForStudent = dao.getAllSubjectBeansForSem(studentInfo.getConsumerProgramStructureId(), "4");
				subjectsEligibleForStudent.addAll(dao.getBBATerm5AndTerm6SubjectForSapid(studentInfo.getSapid(), studentInfo.getSem()));
			}else {
				subjectsEligibleForStudent = dao.getAllSubjectBeansForSem(studentInfo.getConsumerProgramStructureId(), studentInfo.getSem());
			}
//			List<ProgramSubjectMappingBean> subjectsEligibleInCurrentRegistration = new ArrayList<ProgramSubjectMappingBean>();
			logger.info(" subjectsEligibleForStudent Size"+subjectsEligibleForStudent.size());
			// #2.3
			List<String> passedSubjects = getAllPassSubjectsForStudent(studentInfo);
			logger.info(" passedSubjects Size"+passedSubjects.size());
			
			// #2.4
			/* Commented by Siddheshwar_K as we are preparing waived in subjects list dynamically.
			List<String> currentlyWaivedInSubjects = dao.getWaivedInSubjectsForCycle(studentInfo.getSapid(), year, month);*/
			
			//Get Waived-In subjects list of a student.
			List<String> currentlyWaivedInSubjects = studentService.mgetWaivedInSubjects(studentInfo.getSapid(),
					studentInfo.getIsLateral(), studentInfo.getPrgmStructApplicable(), studentInfo.getProgramChanged(),
					studentInfo.getPreviousStudentId(),studentInfo.getConsumerProgramStructureId(),studentInfo.getProgram());
			
			logger.info(" currentlyWaivedInSubjects Size"+currentlyWaivedInSubjects.size());
			// #2.5
			/*List<String> pastWaivedInSubjects = dao.getPastWaviedInSubjectsForCycle(studentInfo.getSapid(), year, month);
			logger.info(" pastWaivedInSubjects Size"+pastWaivedInSubjects.size());*/
			// #2.6
			List<String> failedSubjects = dao.getFailedSubjectsForStudent(studentInfo.getSapid());
			logger.info(" failedSubjects Size"+failedSubjects.size());

			// #2.7
			for (ProgramSubjectMappingExamBean subjectBean : subjectsEligibleForStudent) {
				// Loop and do checks to see if this subject is to be allowed to book for the active/live cycle at all.
				logger.info(" Sapid "+studentInfo.getSapid()+" Subject "+subjectBean.getSubject());
				if(!dao.checkIfEntryAlreadyExists(studentInfo.getSapid(),subjectBean.getSubject(), year, month )) {		
				// #2.7.1
				if(listOfSubjectsNotAllowedToBookFrom.contains(subjectBean.getSubject())){
					// Dont perform action for the subjects in the list
					continue;
				}

				// #2.7.2
				boolean isPassedInSubject = passedSubjects.contains(subjectBean.getSubject());
				logger.info(" isPassedInSubject "+isPassedInSubject);
				boolean isFailedInSubject = failedSubjects.contains(subjectBean.getSubject()); // Subject is currently marked as failed
				logger.info(" isFailedInSubject "+isFailedInSubject);
				boolean isCurrentCycleWaivedInSubject= currentlyWaivedInSubjects.contains(subjectBean.getSubject()); // Subject is currently marked as waived in
				logger.info(" isCurrentCycleWaivedInSubject "+isCurrentCycleWaivedInSubject);
				/*boolean isSubjectWaivedInNotAttempted = pastWaivedInSubjects.contains(subjectBean.getSubject()); // Subject was marked in waived in but wasnt attempted by student and record isnt present in pass/fail either
				logger.info(" isSubjectWaivedInNotAttempted "+isSubjectWaivedInNotAttempted);*/
				boolean isCurrentSubject = subjectBean.getSem().equals(studentInfo.getSem()) && !isFailedInSubject; // Subject is in students current registration. If student has failed in this subject, the entry should be inserted as failed
				logger.info(" isCurrentSubject "+isCurrentSubject);
				//boolean isResitCycleSubject = isFailedInSubject || isSubjectWaivedInNotAttempted;
				boolean isResitCycleSubject = isFailedInSubject;
				logger.info(" isResitCycleSubject "+isResitCycleSubject);
				boolean isCurrentCycleSubject = isCurrentSubject || isCurrentCycleWaivedInSubject;
				logger.info(" isCurrentCycleSubject "+isCurrentCycleSubject);
				
				if(isPassedInSubject) {
					// Do Nothing
				} else if(isResitCycleSubject) {
					// Insert subject to list as a resit subject.
					// Resit subjects are applicable to book in both cycles
					eligibleList.add(getSubjectConfigBean(studentInfo, subjectBean, year, month, "Resit"));
					logger.info(" added subject to list as a RESIT subject for sapid "+studentInfo.getSapid()+" subject "+subjectBean.getSubject()+" program "+ studentInfo.getProgram());
				} else if(isCurrentCycleSubject && isCurrentCycle && ("Jun".equals(month) || "Dec".equals(month) ) ) {
					// Insert subject to list as a current sem subject
					eligibleList.add(getSubjectConfigBean(studentInfo, subjectBean, year, month, "Current"));
					logger.info(" added subject to list as a CURRENT subject for sapid "+studentInfo.getSapid()+" subject "+subjectBean.getSubject()+" program "+ studentInfo.getProgram());

				} else {
					// This is a current cycle subject and booking is for resit students 
				}
				
			  }
			}
		}
		
		
		startTime = Calendar.getInstance().getTimeInMillis();
		logger.info(" eligibleList size "+eligibleList.size());

		// #3
		List<ExamBookingStudentCycleSubjectConfig> errorInserts = new ArrayList<ExamBookingStudentCycleSubjectConfig>();
		for (ExamBookingStudentCycleSubjectConfig bookingEligibleSubject : eligibleList) {
			try {
				dao.insertBookingConfig(bookingEligibleSubject);
			}catch (Exception e) {
				errorInserts.add(bookingEligibleSubject);
				StringWriter errors = new StringWriter();
				e.printStackTrace(new PrintWriter(errors));
				logger.error("insertBookingConfig Exception Message : "+errors.toString());
			}
		}
		
		// #4
		return errorInserts;
	}
	
	public synchronized List<ExamBookingStudentCycleSubjectConfig> generateStudentSubjectMappingsForUnmappedStudents(
			StudentSubjectMappingConfigBean configBean) throws Exception {
		/* 
		 * Flow :
		 * 1) Fetch eligible students according to their validity along with their current registration month/year
		 * 2) For each student who is eligible to apply in the given cycle :
		 * 		2.1) Determine applicable cycle and if student is eligible to book for it under the program constraints
		 * 		2.2) Fetch all applicable subjects in the students current program registration.
		 * 		2.3) Get students passed subject names - This includes subjects passed in previous cycles as well.
		 * 		2.4) Get subjects which have been marked as 'waived in' for the current cycle (exam.student_course_mapping)
		 * 		2.5) Get subjects previously marked 'waived in' for student in previous cycles (This list will most likely be obsolete. logic still kept for being sure). 
		 * 		2.6) Get failed subjects for student
		 * 		2.7) For each subject applicable under the students registration
		 * 			2.7.1) Check if subject is in the list of subjects for which exam booking is not applicable. IE listOfSubjectsNotAllowedToBookFrom
		 * 			2.7.2) Apply various checks from the lists obtained above to determine if the student has failed in the subject, passed or if the subject is from the current cycle.
		 * 			2.7.3) Check if subject is in the list of subjects for which exam booking is not applicable. IE listOfSubjectsNotAllowedToBookFrom
		 * 			2.7.4) Add students data to the list of data to be inserted depending upon which cycle type is applicable to him and if submission is allowed for that subject according to the checks performed.
		 * 3) Insert into the temporary table
		 * 4) Return entries that werent inserted
		 */
		
		String examYear = configBean.getExamYear();
		String examMonth = configBean.getExamMonth();
		Map<String, StudentSubjectMappingPhaseBean> configMap = configBean.getPhaseList().stream()
																.collect(Collectors.toMap(k -> k.getAcadCycle(), k -> k));
		
		StopWatch stopWatch = new StopWatch();
		
		logger.info(" START generateStudentMappingsForCycle for cycle {} / {}, "
				+ "config bean : {} ", examMonth, examYear, configBean);
		// #1
		
		List<ExamBookingEligibileStudentBean> currentlyEligibleStudents =  new ArrayList<>();
		
		configMap.entrySet().stream().forEach(k -> {

			stopWatch.start("fetchEligibleStudentsFromDatabase" + k.getValue().getAcadCycle());
			logger.info(" looping through enrollment cycle for : {} ", k.getValue().getAcadCycle());
			
			List<ExamBookingEligibileStudentBean> currentlyEligibleStudentsForEnrollment = 
					dao.getCurrentlyEligibleStudentsList(examYear, examMonth,"'" + k.getValue().getAcadCycle() + "'", true);
			
			logger.info(" found {} eligible students for cycle : {}", currentlyEligibleStudentsForEnrollment.size(),
					k.getValue().getAcadCycle());
			
			currentlyEligibleStudentsForEnrollment.forEach(v -> {
				v.setBookingStartDateTime(k.getValue().getBookingStartTime());
				v.setBookingEndDateTime(k.getValue().getBookingEndTime());
			});
			
			stopWatch.stop();
			
			currentlyEligibleStudents.addAll(currentlyEligibleStudentsForEnrollment);
			
		});
		
		logger.info(" total eligible students with enrollment cycle : {} ", currentlyEligibleStudents.size() );

		stopWatch.start("fetchEligibleStudentsFromDatabaseForAll");
		
		String cycleToAvoid = getCommaSeparatedCycle(configMap.keySet());
		
		logger.info(" fetching cycles to avoid : {} ", cycleToAvoid);
		
		List<ExamBookingEligibileStudentBean> currentlyEligibleStudentsWithoutEnrollment = 
				dao.getCurrentlyEligibleStudentsList(examYear, examMonth,cycleToAvoid,false);
		
		logger.info(" total currentlyEligibleStudentsWithoutEnrollment : {} ", currentlyEligibleStudentsWithoutEnrollment.size());
		
		currentlyEligibleStudentsWithoutEnrollment.forEach(k -> {
			k.setBookingStartDateTime(configBean.getDefaultBookingStartTime());
			k.setBookingEndDateTime(configBean.getDefaultBookingEndTime());
		});
		
		stopWatch.stop();
		
		currentlyEligibleStudents.addAll(currentlyEligibleStudentsWithoutEnrollment);
		
		logger.info(" currentlyEligibleStudents Size {} ", currentlyEligibleStudents.size());
		
		List<ExamBookingStudentCycleSubjectConfig> eligibleList = new ArrayList<ExamBookingStudentCycleSubjectConfig>();
		// #2
		stopWatch.start("fetchSubjectsForEligibleStudents");
		for (ExamBookingEligibileStudentBean studentInfo : currentlyEligibleStudents) {
			
			if("Diageo".equalsIgnoreCase(studentInfo.getConsumerType()) && examMonth.equals("Sep") && examMonth.equals("2019") ){
				// Dont run for this specific cycle in case of diageo students
				continue;
			}
			if("ACBM".equals(studentInfo.getProgram()) && (examMonth.contains("Apr") || examMonth.contains("Sep"))){
				continue;
			}
			
			// #2.1
			boolean isCurrentCycle;
			if(examMonth.equals("Jun") || examMonth.equals("Dec")) {
				if("Diageo".equalsIgnoreCase(studentInfo.getConsumerType())) {
					//for diageo jun/dec is resit attempt.
					isCurrentCycle = false;
				} else {
					isCurrentCycle = true;
				}
			} else if(examMonth.equals("Apr") || examMonth.equals("Sep")) {
				if("Diageo".equalsIgnoreCase(studentInfo.getConsumerType())) {
					//for diageo jun/dec is resit attempt.
					isCurrentCycle = false;
				} else if("ACBM".equals(studentInfo.getProgram())) {
					// ACBM students cant apply in Resit Cycles.
					continue;
				} else {
					isCurrentCycle = true;
				}
			} else {
				throw new Exception("Invalid cycle found!");
			}
			
			
			// #2.2
			logger.info(" sapid {} masterkey : {} sem : {} ", studentInfo.getSapid(), studentInfo.getConsumerProgramStructureId(),
					studentInfo.getSem());
			List<ProgramSubjectMappingExamBean> subjectsEligibleForStudent = new ArrayList<ProgramSubjectMappingExamBean>();
			if("128".equals(studentInfo.getConsumerProgramStructureId()) && ("5".equals(studentInfo.getSem()) || "6".equals(studentInfo.getSem()) ) ) {
				subjectsEligibleForStudent = dao.getAllSubjectBeansForSem(studentInfo.getConsumerProgramStructureId(), "4");
				subjectsEligibleForStudent.addAll(dao.getBBATerm5AndTerm6SubjectForSapid(studentInfo.getSapid(), studentInfo.getSem()));
			}else {
				subjectsEligibleForStudent = dao.getAllSubjectBeansForSem(studentInfo.getConsumerProgramStructureId(), studentInfo.getSem());
			}
//			List<ProgramSubjectMappingBean> subjectsEligibleInCurrentRegistration = new ArrayList<ProgramSubjectMappingBean>();
			logger.info(" subjectsEligibleForStudent Size {} ", subjectsEligibleForStudent.size());
			// #2.3
			List<String> passedSubjects = getAllPassSubjectsForStudent(studentInfo);
			logger.info(" passedSubjects Size : {}", passedSubjects.size());
			
			// #2.4
			/* Commented by Siddheshwar_K as we are preparing waived in subjects list dynamically.
			List<String> currentlyWaivedInSubjects = dao.getWaivedInSubjectsForCycle(studentInfo.getSapid(), year, month);*/
			
			//Get Waived-In subjects list of a student.
			List<String> currentlyWaivedInSubjects = studentService.mgetWaivedInSubjects(studentInfo.getSapid(),
					studentInfo.getIsLateral(), studentInfo.getPrgmStructApplicable(), studentInfo.getProgramChanged(),
					studentInfo.getPreviousStudentId(),studentInfo.getConsumerProgramStructureId(),studentInfo.getProgram());
			
			logger.info(" currentlyWaivedInSubjects Size : {}", currentlyWaivedInSubjects.size());
			// #2.5
			/*List<String> pastWaivedInSubjects = dao.getPastWaviedInSubjectsForCycle(studentInfo.getSapid(), year, month);
			logger.info(" pastWaivedInSubjects Size"+pastWaivedInSubjects.size());*/
			// #2.6
			List<String> failedSubjects = dao.getFailedSubjectsForStudent(studentInfo.getSapid());
			logger.info(" failedSubjects Size {} ", failedSubjects.size());
			
			// #2.7
			for (ProgramSubjectMappingExamBean subjectBean : subjectsEligibleForStudent) {
				// Loop and do checks to see if this subject is to be allowed to book for the active/live cycle at all.
				logger.info(" Sapid : {} Subject : {} ", studentInfo.getSapid(), subjectBean.getSubject());
				if(!dao.checkIfEntryAlreadyExists(studentInfo.getSapid(),subjectBean.getSubject(), examYear, examMonth )) {		
					// #2.7.1
					if(listOfSubjectsNotAllowedToBookFrom.contains(subjectBean.getSubject())){
						// Dont perform action for the subjects in the list
						continue;
					}
					
					// #2.7.2
					boolean isPassedInSubject = passedSubjects.contains(subjectBean.getSubject());
					logger.info(" isPassedInSubject : {} ", isPassedInSubject);
					boolean isFailedInSubject = failedSubjects.contains(subjectBean.getSubject()); // Subject is currently marked as failed
					logger.info(" isFailedInSubject : {} ", isFailedInSubject);
					boolean isCurrentCycleWaivedInSubject= currentlyWaivedInSubjects.contains(subjectBean.getSubject()); // Subject is currently marked as waived in
					logger.info(" isCurrentCycleWaivedInSubject : {} ", isCurrentCycleWaivedInSubject);
					/*boolean isSubjectWaivedInNotAttempted = pastWaivedInSubjects.contains(subjectBean.getSubject()); // Subject was marked in waived in but wasnt attempted by student and record isnt present in pass/fail either
				logger.info(" isSubjectWaivedInNotAttempted "+isSubjectWaivedInNotAttempted);*/
					boolean isCurrentSubject = subjectBean.getSem().equals(studentInfo.getSem()) && !isFailedInSubject; // Subject is in students current registration. If student has failed in this subject, the entry should be inserted as failed
					logger.info(" isCurrentSubject : {} ", isCurrentSubject);
					//boolean isResitCycleSubject = isFailedInSubject || isSubjectWaivedInNotAttempted;
					boolean isResitCycleSubject = isFailedInSubject;
					logger.info(" isResitCycleSubject : {} ", isResitCycleSubject);
					boolean isCurrentCycleSubject = isCurrentSubject || isCurrentCycleWaivedInSubject;
					logger.info(" isCurrentCycleSubject : {} ", isCurrentCycleSubject);
					
					if(isPassedInSubject) {
						// Do Nothing
					} else if(isResitCycleSubject) {
						// Insert subject to list as a resit subject.
						// Resit subjects are applicable to book in both cycles
						eligibleList.add(getSubjectConfigBean(studentInfo, subjectBean,examYear,examMonth, "Resit"));
						logger.info(" added subject to list as a RESIT subject for Sapid : {} Subject : {} Program : {}", studentInfo.getSapid(), 
								subjectBean.getSubject(), studentInfo.getProgram());
					} else if(isCurrentCycleSubject && isCurrentCycle && ("Jun".equals(examMonth) || "Dec".equals(examMonth) ) ) {
						// Insert subject to list as a current sem subject
						eligibleList.add(getSubjectConfigBean(studentInfo, subjectBean,examYear,examMonth,  "Current"));
						logger.info(" added subject to list as a CURRENT subject for Subject : {} Program : {}", studentInfo.getSapid(), 
								subjectBean.getSubject(), studentInfo.getProgram());
						
					} else {
						// This is a current cycle subject and booking is for resit students 
					}
					
				}
			}
		}
		stopWatch.stop();
		
		logger.info(" eligibleList size to insert : {} ", eligibleList.size());
		
		// #3
		stopWatch.start("insertDataInTempTable");
		List<ExamBookingStudentCycleSubjectConfig> errorInserts = new ArrayList<ExamBookingStudentCycleSubjectConfig>();
		for (ExamBookingStudentCycleSubjectConfig bookingEligibleSubject : eligibleList) {
			try {
				dao.insertBookingConfig(bookingEligibleSubject);
			}catch (Exception e) {
				errorInserts.add(bookingEligibleSubject);
				StringWriter errors = new StringWriter();
				e.printStackTrace(new PrintWriter(errors));
				logger.error(" {} {} insertBookingConfig Exception Message : {} ", bookingEligibleSubject.getSapid(), 
						bookingEligibleSubject.getSubject(),  errors.toString());
			}
		}
		stopWatch.stop();
		
		logger.info(" TOTAL TIME TAKEN : {} ", stopWatch.prettyPrint());
		// #4
		return errorInserts;
	}
	
	private String getCommaSeparatedCycle(Set<String> keySet) {
		
		return keySet.stream().collect(Collectors.joining("','","'","'"));
	}

	private List<String> getAllPassSubjectsForStudent(ExamBookingEligibileStudentBean studentInfo) {
	
		List<String> list = studentService.getAllPassedSubjectNamesForSapid(studentInfo.getSapid());
//		try {
//			//for MBA(distance) if sem >2 waivedoff logic not applicable
//			if("Y".equals(studentInfo.getIsLateral()) && studentInfo.getProgram().startsWith("MBA")){
//				//get waivedoff subjects not applicable
//				List<String> waivedOffNotApplicableSubjectsList = dao.gettWaivedOffSubjectsForLateralMbaDistance( studentInfo.getConsumerProgramStructureId());
//				
//				List<String> currentProgramPassSubjects = sDao.getPassSubjectsNamesForAStudent(studentInfo.getSapid());
//				List<String> finalWaivedOffSubjectsList = waivedOffNotApplicableSubjectsList.stream()
//				.filter(bean -> !currentProgramPassSubjects.contains(bean) )
//				.collect(Collectors.toList());
//				
//				list.removeIf(p -> {
//			        return finalWaivedOffSubjectsList.stream().anyMatch(x -> (p.equals(x) ));
//			    });
//			}  
//		}catch (Exception e) {
//			// TODO: handle exception
//			StringWriter errors = new StringWriter();
//			e.printStackTrace(new PrintWriter(errors));
//			logger.error("getAllPassSubjectsForStudent Exception Message : "+errors.toString());
//			
//		}	
		return list;
	}

	private ExamBookingStudentCycleSubjectConfig getSubjectConfigBean(ExamBookingEligibileStudentBean studentInfo,
			ProgramSubjectMappingExamBean subjectBean, String year, String month, String cycleType) {
		
		ExamBookingStudentCycleSubjectConfig bean = new ExamBookingStudentCycleSubjectConfig();
		bean.setSapid(studentInfo.getSapid());
		bean.setSubject(subjectBean.getSubject());
		bean.setProgram(studentInfo.getProgram());
		bean.setProgramSemSubjId(Integer.toString(subjectBean.getId()));
		bean.setMonth(month);
		bean.setYear(year);
		bean.setSem(subjectBean.getSem());
		bean.setRole(cycleType);
		bean.setBookingStartDateTime(studentInfo.getBookingStartDateTime());
		bean.setBookingEndDateTime(studentInfo.getBookingEndDateTime());
		
		return bean;
		
	}
	
}
