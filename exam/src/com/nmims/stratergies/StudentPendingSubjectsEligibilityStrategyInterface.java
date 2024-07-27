package com.nmims.stratergies;

import java.util.List;

import com.nmims.beans.ProgramSubjectMappingBean;
import com.nmims.beans.StudentBean;
import com.nmims.beans.StudentPendingSubjectBean;

public interface StudentPendingSubjectsEligibilityStrategyInterface {

	public List<StudentPendingSubjectBean> getPendingSubjectsForStudent(StudentBean student) throws Exception;
}
