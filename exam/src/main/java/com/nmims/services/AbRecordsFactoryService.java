package com.nmims.services;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import com.nmims.beans.StudentMarksBean;
import com.nmims.interfaces.ABStudentRecordInterface;




@Service
public class AbRecordsFactoryService {
	
	private static final Logger projectNotBookedLogs = (Logger) LoggerFactory.getLogger("exams"); 

	@Autowired 
	@Qualifier("projectNotBookedStudent")
	ABStudentRecordInterface projectInterface;
	
	public List<StudentMarksBean> getExecuteByProductType(StudentMarksBean bean,String ProductType, ArrayList<String> subjectsUnderProject) {
		int recordInserted = 0;
		String commaSepratedSubject = subjectsUnderProject.stream().collect(Collectors.joining("','", "'", "'"));
		List<StudentMarksBean> finalStudentListForNotBooked = new ArrayList<StudentMarksBean>();
		if ("Project".equalsIgnoreCase(ProductType)) {

			List<StudentMarksBean> studentNotBookedProject = projectInterface.getApplicableStudentForProject(bean,commaSepratedSubject);
			projectNotBookedLogs.info("Total Number of Student For Project Applicable are"+" "+studentNotBookedProject.size());
			
			Set<String> examBookingDetails = projectInterface.getExamBookingDetailsByYearAndMonth(bean);
			projectNotBookedLogs.info("Total Number of Student For Project Booking Found"+" "+examBookingDetails.size());
			
			Set<String> marksBean = projectInterface.getStudentMarksRecordsForCheckExists(bean,commaSepratedSubject);
			projectNotBookedLogs.info("Total Number of Student Details Found In exam.marks Table"+" "+examBookingDetails.size());
			
			 finalStudentListForNotBooked = projectInterface.checkingStudentInExamBookingAndMarks(studentNotBookedProject, examBookingDetails,marksBean);
			 projectNotBookedLogs.info("Total Number of Student To be Inserted in exam.marks"+" "+finalStudentListForNotBooked.size());
			 
			recordInserted = projectInterface.insertOrUpdateAbsentRecordInDataBase(finalStudentListForNotBooked);	
			projectNotBookedLogs.info("Total Records Inserted"+recordInserted);
		} 
		return finalStudentListForNotBooked;
	}

}
