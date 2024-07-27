package com.nmims.stratergies.impl;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.nmims.beans.MBAMarksheetBean;
import com.nmims.beans.MBAPassFailBean;
import com.nmims.beans.ProgramSubjectMappingExamBean;
import com.nmims.beans.StudentExamBean;
import com.nmims.daos.MBAWXExamResultsDAO;
import com.nmims.services.MBAResultMonthYearComparatorService;
import com.nmims.stratergies.MBAMarksheetStrategyInterface;

@Service
public class MBAXMarksheetStrategy implements MBAMarksheetStrategyInterface {

	@Autowired
	private MBAWXExamResultsDAO dao;

	@Autowired
	private MBAXMarksStrategy marksStrategy;

	@Autowired
	private MBAGradePointStrategy gradePointStrategy;
	
	@Override
	public MBAMarksheetBean getMarksheetBeanForStudentForTerm(String sapid, StudentExamBean student, int term) throws Exception {
		MBAMarksheetBean studentMarks = new MBAMarksheetBean();
		studentMarks.setSapid(sapid);
		studentMarks.setProgram(student.getProgram());
		studentMarks.setTerm("" + term);

		List<MBAPassFailBean> passFailDataList = marksStrategy.getMarksForStudentForSemester(sapid, term);
		studentMarks.setMarksList(passFailDataList);
		List<ProgramSubjectMappingExamBean> subjectsForTerm = new ArrayList<ProgramSubjectMappingExamBean>();
		subjectsForTerm = dao.getSubjectsForTermForProgramForStructureChangeStudent(sapid, term);
		if(subjectsForTerm.size() == 0) {
			subjectsForTerm = dao.getAllSubjectsForTermForProgram(student.getConsumerProgramStructureId(), term);
		}

		int numberOfSubjectsCleared = 0;
		int numberOfSubjectsAppearedFor = 0;
		for (ProgramSubjectMappingExamBean subject : subjectsForTerm) {
			
			boolean appeared = false;
			boolean cleared = false;
			
			for (MBAPassFailBean marks : passFailDataList) {
				if(marks.getSubject().equals(subject.getSubject())) {
					appeared = true;
					if(marks.getIsPass().equals("Y")) {
						cleared = true;
					}
				}
			}
			
			if(appeared) {
				numberOfSubjectsAppearedFor++;
			}
			if(cleared) {
				numberOfSubjectsCleared++;
			}
		}
		
		if(numberOfSubjectsAppearedFor == subjectsForTerm.size()) {
			studentMarks.setAppearedForTerm(true);
		} else {
			studentMarks.setAppearedForTerm(false);
			studentMarks.setTermCleared(false);
		}
		
		if(studentMarks.isAppearedForTerm()) {
			
			if(numberOfSubjectsCleared == passFailDataList.size()) {
				studentMarks.setTermCleared(true);
			} else {
				studentMarks.setTermCleared(false);
			}
			
			// CGPA requires marks for all previous semesters
			List<MBAPassFailBean> passFailDataListAllSem = marksStrategy.getPassFailForCGPACalculationMBAX(sapid, term);
			// Calculate GPA and CGPA only if student has appeared for all subjects in this term 
			String GPA = gradePointStrategy.getGPA(term, passFailDataListAllSem);
			studentMarks.setGpa(GPA);
			
			String CGPA = gradePointStrategy.getCGPA(term, passFailDataListAllSem);
			studentMarks.setCgpa(CGPA);
		}

		// Term remark is calculated by GPA obtained
		setRemarkForTerm(studentMarks);

		setExamMonthYearForMarksheet(studentMarks);

		return studentMarks;
	}

	@Override
	public void setExamMonthYearForMarksheet(MBAMarksheetBean studentMarks) throws Exception {
		// Clear Month/Year only to be added if the sem is clear
		if(studentMarks.getMarksList() != null && studentMarks.getMarksList().size() > 0) {
			// Loop through all exams for this term.
			MBAPassFailBean marksBeanForLatestExamYearMonth;
			List<MBAPassFailBean> yearMonthList = new ArrayList<MBAPassFailBean>();
			for (MBAPassFailBean subjectResult : studentMarks.getMarksList()) {
				if("Y".equals(subjectResult.getIsPass())) {
					yearMonthList.add(subjectResult);
				}
			}
			MBAResultMonthYearComparatorService comparator = new MBAResultMonthYearComparatorService();
			yearMonthList.sort(comparator);

			Collections.reverse(yearMonthList);
			if(yearMonthList.size() > 0) {
				marksBeanForLatestExamYearMonth = yearMonthList.get(0);
				if(studentMarks.isTermCleared()) {
					studentMarks.setClearExamMonth(marksBeanForLatestExamYearMonth.getExamMonth());
					studentMarks.setClearExamYear(marksBeanForLatestExamYearMonth.getExamYear());
				}
			}
		}
	}

	@Override
	public void setRemarkForTerm(MBAMarksheetBean marksheet) throws Exception {

		String remark = "";
		
		if(marksheet.isTermCleared()) {
			double gpa = Float.parseFloat(marksheet.getGpa());
			if(gpa >= 3.5) {
				remark = "PASS WITH DISTINCTION";
			} else {
				remark = "PASS";
			}
		} else {
			remark = "FAIL";
		}
		
		marksheet.setRemark(remark);
	}
}
