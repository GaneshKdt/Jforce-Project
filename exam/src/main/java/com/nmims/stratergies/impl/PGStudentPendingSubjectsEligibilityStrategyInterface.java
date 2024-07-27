package com.nmims.stratergies.impl;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Service;

import com.nmims.beans.StudentExamBean;
import com.nmims.beans.StudentPendingSubjectBean;
import com.nmims.daos.StudentDAO;
import com.nmims.services.StudentService;
import com.nmims.stratergies.StudentPendingSubjectsEligibilityStrategyInterface;

@Service
public class PGStudentPendingSubjectsEligibilityStrategyInterface
		implements StudentPendingSubjectsEligibilityStrategyInterface {

	@Autowired
	ApplicationContext act;
	
	@Autowired
	StudentService studentService;
	
	@Override
	public List<StudentPendingSubjectBean> getPendingSubjectsForStudent(StudentExamBean student) throws Exception {

		List<StudentPendingSubjectBean> pendingSubjects = new ArrayList<StudentPendingSubjectBean>();;
		
		StudentDAO sDao = (StudentDAO)act.getBean("studentDAO");
		List<StudentPendingSubjectBean> allApplicableSubjects = sDao.getAllCurrentlyApplicableSubjectsForStudent(student);
		List<String> failSubjects = studentService.getAllFailSubjectNamesForSapid(student.getSapid());

		if("Jul2019".equalsIgnoreCase(student.getPrgmStructApplicable()) && failSubjects.contains("Business Statistics")) {
			failSubjects.add("Decision Science");
		}
		for (StudentPendingSubjectBean subject : allApplicableSubjects) {
			if(failSubjects.contains(subject.getSubject())) {
				pendingSubjects.add(subject);
			}
		}
		
		return pendingSubjects;
	}

}
