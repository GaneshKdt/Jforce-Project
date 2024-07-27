package com.nmims.stratergies;

import java.util.List;

import com.nmims.beans.ProgramSubjectMappingExamBean;
import com.nmims.beans.StudentExamBean;
import com.nmims.beans.StudentPendingSubjectBean;

public interface StudentPendingSubjectsEligibilityStrategyInterface {

	public List<StudentPendingSubjectBean> getPendingSubjectsForStudent(StudentExamBean student) throws Exception;
}
