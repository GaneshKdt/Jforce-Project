package com.nmims.services.impl;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.google.common.base.Throwables;
import com.nmims.beans.ExamBookingTransactionBean;
import com.nmims.beans.ExamOrderExamBean;
import com.nmims.beans.ExamResultChecklistBean;
import com.nmims.beans.StudentExamBean;
import com.nmims.daos.ExamResultChecklistDao;
import com.nmims.daos.StudentMarksDAO;
import com.nmims.services.ExamResultChecklistService;
import com.nmims.services.StudentService;

@Service
public class ExamResultChecklistServiceImpl implements ExamResultChecklistService {

	@Autowired
	private ExamResultChecklistDao examResultsCheckListDao;

	@Autowired
	private StudentService studentService;

	@Autowired
	private StudentMarksDAO studentMarksDao;

	public static final Logger logger = LoggerFactory.getLogger("examResultsChecklist");

	private final String CONFIRMED_BOOKINGS_COUNT = "confirmBookingsCount";
	private final String CONFIRMED_PROJECT_BOOKINGS_COUNT = "confirmProjectBookingsCount";
	private final String PROJECT_NOT_BOOKED_COUNT = "projectNotBookedCount";
	private final String ASSIGNMENT_NOT_SUBMITTED_COUNT = "assignmentNotSubmittedCount";
	private final String ASSIGNMENT_SUBMITTED_BUT_EXAM_NOT_BOOKED_COUNT = "assignmentSubmittedButTEENotBookedCount";
	private final String TOTAL_COUNT = "totalCount";
	private final String PRESENT_RECORDS = "presentRecords";

	@Override
	public Map<String, Integer> getDashboardCountForExamResults(String examMonth, String examYear) {
		logger.info("Getting dashboard count : START for exam year and month : {} {}", examMonth, examYear);

		Integer bookedProjectCount = new Integer(examResultsCheckListDao.getProjectBookedCount(examYear, examMonth));
		logger.info("bookedProjectCount : {}", bookedProjectCount);

		Integer bookedSubjectCount = new Integer(examResultsCheckListDao.getExamBookedCount(examYear, examMonth));
		logger.info("bookedSubjectCount : {}", bookedSubjectCount);

		Integer assignmentNotSubmittedCount = new Integer(getAssignmentNotSubmittedRecords(examYear, examMonth, Arrays.asList()).size());
		logger.info("assignmentNotSubmittedCount : {}", assignmentNotSubmittedCount);

		Integer assignmentSubmittedCount = new Integer(examResultsCheckListDao.getOnlyAssignmentSubmittedCount(examYear, examMonth));
		logger.info("assignmentSubmittedCount : {}", assignmentSubmittedCount);

		Integer projectNotBookedCount = new Integer(examResultsCheckListDao.getProjectNotBookedCount(examYear, examMonth));
		logger.info("projectNotBookedCount : {}", projectNotBookedCount);

		// Project Exempted = students whose project exempted but they did not book
		// project, included because this data later gets taken into consider while
		// fetching ANS records
		Integer projectExemptedButNotBookedCount = getProjectExemptedButNotBookedCount(examYear, examMonth);
		logger.info("projectExemptedButNotBookedCount : {}", projectExemptedButNotBookedCount);

		// Adding project exempted in project not booked count for now, since the number
		// are usually very low
		projectNotBookedCount = projectNotBookedCount + projectExemptedButNotBookedCount;
		logger.info("projectNotBookedCount (after adding projectExemptedButNotBookedCount) : {}",
				projectNotBookedCount);

		Integer totalCount = bookedSubjectCount + bookedProjectCount + projectNotBookedCount
				+ assignmentNotSubmittedCount + assignmentSubmittedCount;

		if (totalCount.intValue() < 1)
			throw new RuntimeException("No records found");

		logger.info("totalCount : {}", totalCount.intValue());
		
		Integer alreadyPresentRecords = new Integer(examResultsCheckListDao.getExamResultChecklistCount(examYear,examMonth));
		logger.info("Already present records in checklist table : {} for cycle {} {} ",alreadyPresentRecords, examMonth,examYear);

		Map<String, Integer> allDashBoardCountMap = new LinkedHashMap<>(7);

		allDashBoardCountMap.put(CONFIRMED_BOOKINGS_COUNT, bookedSubjectCount);
		allDashBoardCountMap.put(CONFIRMED_PROJECT_BOOKINGS_COUNT, bookedProjectCount);
		allDashBoardCountMap.put(PROJECT_NOT_BOOKED_COUNT, projectNotBookedCount);
		allDashBoardCountMap.put(ASSIGNMENT_NOT_SUBMITTED_COUNT, assignmentNotSubmittedCount);
		allDashBoardCountMap.put(ASSIGNMENT_SUBMITTED_BUT_EXAM_NOT_BOOKED_COUNT, assignmentSubmittedCount);
		allDashBoardCountMap.put(TOTAL_COUNT, totalCount);
		allDashBoardCountMap.put(PRESENT_RECORDS, alreadyPresentRecords);

		logger.info("Getting dashboard count : END for exam year and month : {} {}", examMonth, examYear);
		return allDashBoardCountMap;
	}

	// works same as above but instead of return count it returns records
	public List<ExamResultChecklistBean> getAssignmentNotSubmittedRecords(String examYear, String examMonth, List<ExamResultChecklistBean> examBookingRecords) {
	
		List<ExamResultChecklistBean> tempExamBookingRecords = null;
		List<ExamResultChecklistBean> assignmentApplicableRecords = null;
		try {
			assignmentApplicableRecords = examResultsCheckListDao
					.getAssignmentApplicableRecords(examYear, examMonth);
			
			logger.info("total assignment applicable records found {} ", assignmentApplicableRecords.size());
			
			if(examBookingRecords.isEmpty())
				tempExamBookingRecords = examResultsCheckListDao.getExamBookingRecords(examYear, examMonth);
			else
				tempExamBookingRecords = examBookingRecords;
			
			logger.info("total exam bookings records : {}", tempExamBookingRecords.size());
			
			Set<ExamResultChecklistBean> setOfExamBookings = new HashSet<>(tempExamBookingRecords);
			
			Set<ExamResultChecklistBean> setOfPassedRecords = examResultsCheckListDao.getAllPassedRecords();
			
			logger.info("all passed records : {}", setOfPassedRecords.size());
			
			Set<ExamResultChecklistBean> setOfAssignmentSubmittedRecords = examResultsCheckListDao.getAssignmentSubmittedRecords(examYear, examMonth);
			
			logger.info("assignment submitted records : {}", setOfAssignmentSubmittedRecords.size());
			
			assignmentApplicableRecords.removeIf(k -> setOfAssignmentSubmittedRecords.contains(k) || setOfExamBookings.contains(k) || 
					setOfPassedRecords.contains(k) || removeIfWaivedOffSubjectIsFound(k));
			
			logger.info("total assignment not submitted records : {} ", assignmentApplicableRecords.size());
			
		} finally {
			tempExamBookingRecords = null;
		}
		
		return assignmentApplicableRecords;
	}

	private boolean removeIfWaivedOffSubjectIsFound(ExamResultChecklistBean k) {
		try {
			StudentExamBean studentExamBean = examResultsCheckListDao.getStudentdata(k.getSapid());
			// Internally uses student service to get waived of subjects
			studentService.mgetWaivedOffSubjects(studentExamBean);
			return studentExamBean.getWaivedOffSubjects().contains(k.getSubject());
		} catch (Exception e) {
			logger.info("ERROR getting student from students table for sapid  {} : {}", k.getSapid(),
					Throwables.getStackTraceAsString(e));
			return false;
		}
	}

	public Integer getProjectExemptedButNotBookedCount(String examYear, String examMonth) {
//		return new Integer(examResultsCheckListDao.getProjectExemptedButNotBookedRecords(examYear, examMonth));
		ExamBookingTransactionBean searchBean = new ExamBookingTransactionBean();
		searchBean.setYear(examYear);
		searchBean.setMonth(examMonth);
		// Uses the same method as ANS marking method to avoid having to change logic on
		// both ends
		List<ExamBookingTransactionBean> projectFeeExemptAndNotSubmitted = studentMarksDao.projectFeeExemptAndNotSubmitted(searchBean);
		
		// Above method returns duplicate values, checks for exempted students twice, so
		// to avoid duplicate values sorting removing them
		List<ExamBookingTransactionBean> uniqueEntries = projectFeeExemptAndNotSubmitted.stream()
				.collect(Collectors
						.collectingAndThen(Collectors
								.toCollection(() -> 
								new TreeSet<>(Comparator
										.comparing(ExamBookingTransactionBean::getSapid)
												.thenComparing(ExamBookingTransactionBean::getSubject))),
								ArrayList::new));
		
		return new Integer(uniqueEntries.size());
	}

	@Override
	public Integer populateResultChecklist(String examYear, String examMonth,String userId) {

		Integer numberOfInsertedRecords = null;

		logger.info("fetching exam order for {} {}", examMonth, examYear);

		// Getting acad  month from exam order 
		ExamOrderExamBean examOrder = examResultsCheckListDao.getExamOrder(examYear, examMonth);
		logger.info("Exam order found for {} {} : acad month : {}", examMonth, examYear, examOrder.getAcadMonth());

		List<ExamResultChecklistBean> finalChecklistRecords = getFinalCheckListRecords(examYear, examMonth,
				examOrder.getAcadMonth());

		logger.info("list size for insert : {}", finalChecklistRecords.size());

		
		Set<ExamResultChecklistBean> existingBaseRecords = examResultsCheckListDao.getExistingBaseRecords(examYear, examMonth);
		
		logger.info("Number of existing records found : {}", existingBaseRecords.size());

		if (!existingBaseRecords.isEmpty()) {
			logger.info("There are existing records in the base data so removing existing ones to insert!");

			finalChecklistRecords.removeAll(existingBaseRecords);

			logger.info("final list size after removing existing records : {}", finalChecklistRecords.size());
		}

		numberOfInsertedRecords = insertExamChecklistRecords(finalChecklistRecords, examYear, examMonth, userId);

		return numberOfInsertedRecords;
	}

	private List<ExamResultChecklistBean> getFinalCheckListRecords(String examYear, String examMonth,
			String acadMonth) {

		logger.info("----------------------Fetching checklist data START ----------------------");
		List<ExamResultChecklistBean> finalChecklistList = new ArrayList<>();

		List<ExamResultChecklistBean> examBookingRecords = examResultsCheckListDao.getExamBookingRecords(examYear,
				examMonth);
		
		logAndAddAllToFinalList(finalChecklistList, examBookingRecords,"examBooked");

		List<ExamResultChecklistBean> projectBookingRecords = examResultsCheckListDao
				.getProjectBookingRecords(examYear, examMonth);
		
		logAndAddAllToFinalList(finalChecklistList, projectBookingRecords,"projectBooked");

		List<ExamResultChecklistBean> projectNotBookedRecords = getProjectNotBookedRecords(examYear, examMonth,
				acadMonth, projectBookingRecords);
		
		logAndAddAllToFinalList(finalChecklistList, projectNotBookedRecords,"projectNotBooked");
		
		List<ExamResultChecklistBean> assignmentNotSubmittedRecords = getAssignmentNotSubmittedRecords(examYear,examMonth,examBookingRecords);
		
		logAndAddAllToFinalList(finalChecklistList, assignmentNotSubmittedRecords,"assignmentNotSubmmited");
		
		List<ExamResultChecklistBean> assignmentSubmittedButNotBookedRecords = getAssignmentSubmittedButNotBookedRecords(examYear, examMonth);
		
		logAndAddAllToFinalList(finalChecklistList, assignmentSubmittedButNotBookedRecords,"onlyAssignmentSubmitted");
		
		logger.info("----------------------Fetching checklist data END ----------------------");
		
		return finalChecklistList;
	}

	private void logAndAddAllToFinalList(List<ExamResultChecklistBean> finalChecklistList,
			List<ExamResultChecklistBean> records, String type) {
		logger.info(" {} {} records found ",records.size(), type);
		
		records.forEach(k -> k.setCategory(type));
		
		finalChecklistList.addAll(records);
	}

	private List<ExamResultChecklistBean> getProjectNotBookedRecords(String examYear, String examMonth,
			String acadMonth, List<ExamResultChecklistBean> projectBookingRecords) {

		Set<ExamResultChecklistBean> applicableStudentsForProject = examResultsCheckListDao.getApplicableStudentsForProject(examYear, acadMonth);

		logger.info("total applicable students for project : {}", applicableStudentsForProject.size());

		Set<ExamResultChecklistBean> projectBookedSet = new HashSet<>(projectBookingRecords);

		applicableStudentsForProject.removeAll(projectBookedSet);

		logger.info("after removing booked students and project not booked students, applicable student size : {}",
				applicableStudentsForProject.size());

		Set<ExamResultChecklistBean> projectExtemptedButNotSubmitted = getProjectExemptedButNotSubmittedList(examYear, examMonth);
		
		applicableStudentsForProject.addAll(projectExtemptedButNotSubmitted);

		logger.info("Final size of project applicable but not booked : {}", applicableStudentsForProject.size());

		return applicableStudentsForProject.stream().collect(Collectors.toList());
	}

	public Set<ExamResultChecklistBean> getProjectExemptedButNotSubmittedList(String examYear, String examMonth) {

		ExamBookingTransactionBean searchBean = new ExamBookingTransactionBean();
		searchBean.setYear(examYear);
		searchBean.setMonth(examMonth);

		List<ExamBookingTransactionBean> projectFeeExemptAndNotSubmitted = studentMarksDao
				.projectFeeExemptAndNotSubmitted(searchBean);

		logger.info("Received project exempted but submitted list : {}", projectFeeExemptAndNotSubmitted.size());
		
		return projectFeeExemptAndNotSubmitted.stream()
				.map(k ->  new ExamResultChecklistBean(k.getSapid(), k.getSubject()))
				.collect(Collectors.toSet());
	}

	@Override
	public Integer insertExamChecklistRecords(List<ExamResultChecklistBean> checklistRecords, String examYear, String examMonth,String userId) {
		return examResultsCheckListDao.insertExamChecklistRecords(checklistRecords,examYear,examMonth,userId);
	}

	
	public List<ExamResultChecklistBean> getAssignmentSubmittedButNotBookedRecords(String examYear, String examMonth){
		return examResultsCheckListDao.getOnlyAssignmentSubmittedRecords(examYear, examMonth);
	}

}
