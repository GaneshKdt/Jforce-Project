package com.nmims.services.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ConcurrentMap;
import java.util.function.Function;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.nmims.beans.ExamBookingExamBean;
import com.nmims.beans.ExamOrderExamBean;
import com.nmims.beans.StudentExamBean;
import com.nmims.beans.StudentMarksBean;
import com.nmims.daos.StudentMarksDAO;
import com.nmims.interfaces.ABStudentRecordInterface;

@Service("projectNotBookedStudent")
public class ABStudentRecordServiceImpl implements ABStudentRecordInterface {

	@Autowired
	StudentMarksDAO studentDao;
	
	@Override
	public List<StudentMarksBean> getApplicableStudentForProject(StudentMarksBean bean,String subjectsUnderProject) {
		
		ExamOrderExamBean examOrderBean = getExamOrderBean(bean);
		List<StudentMarksBean> registrationStudentDetails = getStudentRegistrationDetailsByExamMonth(examOrderBean.getAcadMonth(),examOrderBean.getYear());
		List<StudentMarksBean> programDetails = getPSSDetails(subjectsUnderProject);			
		HashMap<String, StudentExamBean> studentsDetails =  getStudentDetails();
		return getFilteredListOfProjectApplicableStudent(examOrderBean,registrationStudentDetails,programDetails,studentsDetails);			 
	}
	
	@Override
	public Set<String> getExamBookingDetailsByYearAndMonth(StudentMarksBean bean) {
		Set<String> examBookingSapidForProject =new HashSet<>();
		try {
			List<ExamBookingExamBean> examBookingBean = studentDao.getExamBookingDetails( bean);
			examBookingSapidForProject= examBookingBean.stream().map(k -> k.getSapid()).collect(Collectors.toSet());
		}catch(Exception e) {
			return examBookingSapidForProject;
		}
		return examBookingSapidForProject;
	}

	@Override
	public List<StudentMarksBean> checkingStudentInExamBookingAndMarks(List<StudentMarksBean> studentNotBookedProject, Set<String> examBookingDetails,Set<String> marksStudent) {
		List<StudentMarksBean> studentNotBookedFinalList =  new ArrayList<StudentMarksBean>();
		try {
		studentNotBookedFinalList = studentNotBookedProject.stream()
				.filter(x -> !examBookingDetails.contains(x.getSapid()) && !marksStudent.contains(x.getSapid())).collect(Collectors.toList());
		}catch(Exception e) {
			return studentNotBookedFinalList;
		}
		return studentNotBookedFinalList;
	}

	@Override
	public int insertOrUpdateAbsentRecordInDataBase(List<StudentMarksBean> finalStudnetListForNotBooked) {
		int countHasBeenInserted=0;
		try {
		 countHasBeenInserted = studentDao.insertProjectNotBookedRecords(finalStudnetListForNotBooked);
		}catch(Exception e) {
			e.printStackTrace();
		}
		return countHasBeenInserted;
	}

	@Override
	public Set<String> getStudentMarksRecordsForCheckExists(StudentMarksBean bean,String commaSepratedSubject) {
		Set<String> marksBean =  new HashSet<>();
		try {
			List<StudentMarksBean> marksList = studentDao.getMarksDetailsFroProjectStudent(bean,commaSepratedSubject);
			marksBean = marksList.stream().map(x -> x.getSapid()).collect(Collectors.toSet());
		}catch(Exception e) {
			
		}
		return marksBean;
	}

	public ExamOrderExamBean getExamOrderBean(StudentMarksBean bean) {
		return studentDao.getExamOrderDetailsByExamMonthAndYear(bean);
	}
	
	public List<StudentMarksBean> getStudentRegistrationDetailsByExamMonth(String acadMonth, String year) {
		return  studentDao.getStudentRegistrationDetailsByExamMonthAndYear(acadMonth,year);
	}

	public List<StudentMarksBean> getPSSDetails(String subjectsUnderProject){
		return studentDao.getProgramSemSubjectDetailsByConsumerStructureId(subjectsUnderProject);
	}
	
	public HashMap<String,StudentExamBean> getStudentDetails(){
		return  studentDao.getAllStudents();
	}
	public List<StudentMarksBean> getFilteredListOfProjectApplicableStudent(ExamOrderExamBean examOrderBean, List<StudentMarksBean> registrationStudentDetails, List<StudentMarksBean> programDetails, HashMap<String, StudentExamBean> studentsDetails){
		List<StudentMarksBean> finalStudentListForProjectApplicable = new ArrayList<StudentMarksBean>();
		
		ConcurrentMap<String, StudentMarksBean> programSemSubjectMap = programDetails.parallelStream()
				.collect(Collectors.toConcurrentMap(programData -> programData.getConsumerProgramStructureId() + programData.getSem(), Function.identity()));
		
		List<StudentMarksBean> studentFilterDataFromPssAndStudents = registrationStudentDetails.stream()
				.filter(x -> programSemSubjectMap.containsKey(x.getConsumerProgramStructureId() + x.getSem()))
				.filter(y-> studentsDetails.containsKey(y.getSapid())).collect(Collectors.toList());

		finalStudentListForProjectApplicable = studentFilterDataFromPssAndStudents.stream().map(x -> {
			StudentExamBean studentMapDetails = studentsDetails.get(x.getSapid());
			StudentMarksBean programMapDetails = programSemSubjectMap
					.get(x.getConsumerProgramStructureId() + x.getSem());
			x.setMonth(examOrderBean.getMonth());
			x.setYear(examOrderBean.getYear());
			x.setExamorder(examOrderBean.getOrder());
			x.setStudentname(studentMapDetails.getFirstName() + " " + studentMapDetails.getLastName());
			x.setSubject(programMapDetails.getSubject());
			return x;
		}).collect(Collectors.toList());
		
		 return finalStudentListForProjectApplicable;
	}
}
