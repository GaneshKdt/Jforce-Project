package com.nmims.services;

import java.util.ArrayList;
import java.util.HashMap;

import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.nmims.beans.StudentAcadsBean;
import com.nmims.beans.StudentCourseMappingBean;
import com.nmims.daos.ContentDAO;
import com.nmims.daos.StudentCourseMappingDao;

@Service
public class StudentCourseMappingService 
{
	
	@Autowired
	StudentCourseMappingDao studentCourseMapDao;

	private ArrayList<String> subjectList = null;
	
	@Autowired
	ContentService contentService;
	
	@Autowired
	ContentDAO contentdao;
	
	private static final Logger logger = LoggerFactory.getLogger("studentCourses");
	
	public ArrayList<String> getCurrentCycleSujects(String sapid,String month,String year) {
		// TODO Auto-generated method stub
		ArrayList<String> currentList = new ArrayList<String>();
		try {
		currentList = studentCourseMapDao.getCurrentCycleSubjects(sapid,month,year);
		
		}catch(Exception e) {
			logger.error("Service Method getCurrentCycleSubjects For Sapid "+sapid+" and acad-cycle "+year+"/"+"month. Error:- ",e);
		}
		
		return currentList;
	}

	public ArrayList<String> getPSSIds(String sapid,String month,String year) {
		// TODO Auto-generated method stub
		ArrayList<String> currentList = new ArrayList<String>();
		try {
		currentList = studentCourseMapDao.getPSSIds(sapid,month,year);
		
		}catch(Exception e) {
			logger.error("Service Method getPSSIds For Sapid "+sapid+" and acad-cycle "+year+"/"+"month. Error:- ",e);
		}
		
		return currentList;
	}
	
	public ArrayList<Integer> getPSSID(String sapid,String year,String month) {
		ArrayList<Integer> subjects = new ArrayList<Integer>();
		try{
			subjects = studentCourseMapDao.getPSSId(sapid,year,month);
		}catch(Exception e) {
			logger.error("Service Method getPSSIds For Sapid "+sapid+" and acad-cycle "+year+"/"+"month. Error:- ",e);
		}
		return subjects;
	}

	public ArrayList<String> getCurrentCycleSubjectsForSessions(String sapid,String month,String year,String program) {
		// TODO Auto-generated method stub
		ArrayList<String> currentList = new ArrayList<String>();
		try {
		 currentList = getCurrentCycleSujects(sapid,month,year);
		
		//Added temporary for PD - WM project lecture
		if (program.equalsIgnoreCase("PD - WM") && currentList.contains("Module 4 - Project")) {
				currentList.add("Project");
		}
		}catch(Exception e) {
			logger.error("Service Method getCurrentCycleSubjects For Sapid "+sapid+" and acad-cycle "+year+"/"+"month and program "+program+". Error:- ",e);
		}
		
		return currentList;
	}
	
	public ArrayList<String> applicableSubjectsForStudentForWeb(HttpServletRequest request) {
		
		String sapId = (String)request.getSession().getAttribute("userId_acads");
		
		//So admins/faculty would see Videos Page with all videos 
		if(!sapId.startsWith("7")) {
			request.getSession().setAttribute("applicableSubjects", subjectList);
			return subjectList;
		}
		
		StudentAcadsBean student = (StudentAcadsBean)request.getSession().getAttribute("student_acads");
	
		String earlyAccess = (String)request.getSession().getAttribute("earlyAccess");
		// If isEarlyAccess then registration will not be available of this drive.
		StudentCourseMappingBean subjects = applicableSubjectsForVideos(student,earlyAccess,sapId);
		
		if(subjects.getCurrentSemSubjects() != null && subjects.getCurrentSemSubjects().size() > 0){
			request.getSession().setAttribute("currentSemSubjects", subjects.getCurrentSemSubjects());
		}
		
		if(subjects.getListOfApplicableSUbjects().size() == 0){
			request.setAttribute("error", "true");
			request.setAttribute("errorMessage", "No subjects found for you."); 
		}
		
		request.getSession().setAttribute("failSubjectsBeans", subjects.getFailedSubjectList());
		request.getSession().setAttribute("applicableSubjects", subjects.getListOfApplicableSUbjects());
		
		return subjects.getListOfApplicableSUbjects();
				
	}
	
	public ArrayList<String> applicableSubjectsForStudentMobile(StudentAcadsBean student) {

		String earlyAccess = contentService.checkEarlyAccess(student.getSapid());

		StudentCourseMappingBean subjects = applicableSubjectsForVideos(student,earlyAccess,student.getSapid());

		return subjects.getListOfApplicableSUbjects();	
	}

	private StudentCourseMappingBean applicableSubjectsForVideos(StudentAcadsBean student,String earlyAccess,String sapId)
	{
		ArrayList<String> failSubjectsBeans = new ArrayList<>();
		ArrayList<String> allsubjects = new ArrayList<>();
		ArrayList<String>  currentSemSubjects = new ArrayList<>();
		ArrayList<String> unAttemptedSubjectsBeans = new ArrayList<>();
		
		StudentCourseMappingBean subjectbean = new StudentCourseMappingBean();
		StudentAcadsBean studentRegistrationData;
		try {	
		
		if("Yes".equalsIgnoreCase(earlyAccess)) {
			studentRegistrationData= student;
		}else{
			studentRegistrationData = contentdao.getStudentMaxSemRegistrationData(sapId);
		}
		
		if(studentRegistrationData != null){
		
		
			//Take program from Registration data and not Student data. 
			student.setProgram(studentRegistrationData.getProgram());
			student.setSem(studentRegistrationData.getSem());
			
			currentSemSubjects = getCurrentCycleSujects(student.getSapid(),studentRegistrationData.getMonth(),studentRegistrationData.getYear());
			allsubjects.addAll(currentSemSubjects);
		
			unAttemptedSubjectsBeans = studentCourseMapDao.getNotPassedSubjectsBasedOnSapid(sapId,studentRegistrationData.getSem(),studentRegistrationData.getConsumerProgramStructureId());
			if(unAttemptedSubjectsBeans != null && unAttemptedSubjectsBeans.size() > 0){
			
				unAttemptedSubjectsBeans.removeAll(currentSemSubjects);	
				failSubjectsBeans.addAll(unAttemptedSubjectsBeans);
			
				allsubjects.addAll(unAttemptedSubjectsBeans);
			}
		
		}//StudentRegistrationData if complete
		
		//If current sem is 1, then there will be no failed subjects. Get failed subjects only when he is in higher semesters
		/*if(!"1".equals(studentRegistrationData.getSem())){*/
			failSubjectsBeans = studentCourseMapDao.getFailSubjectsNamesForAStudent(student.getSapid());
			if(failSubjectsBeans != null && failSubjectsBeans.size() > 0){
				allsubjects.addAll(failSubjectsBeans);
			}
		/*}*/
	
	
		allsubjects.add("Guest Session: GST by CA. Bimal Jain");	
		
		try{
		if("1".equals(student.getSem())){
			allsubjects.add("Orientation");
		}
		
		allsubjects.add("Assignment");

		}catch(Exception e){
		}
		
		//Remove orientation,Project n assignment subject for sas student
		try {

			if("EPBM".equalsIgnoreCase(student.getProgram()) || "MPDV".equalsIgnoreCase(student.getProgram()) ){
				allsubjects.remove("Assignment");
				
				//We are not adding 'Project Preparation Session' in applicableSubjects list so no need of remove
				/*if(applicableSubjects.contains("Project Preparation Session")) {
					applicableSubjects.remove("Project Preparation Session");
				}*/
				if(allsubjects.contains("Orientation")) {
					allsubjects.remove("Orientation");
				
					if("1".equals(student.getSem())){
						allsubjects.add("Executive Program Orientation");
					}
				}
			}
		} catch (Exception e) {
			  
		}
		//end
		if(student.getIsLateral().equalsIgnoreCase("Y")) {
			
			for(String subject:student.getWaivedOffSubjects()){
				if(allsubjects.contains(subject)) {
					allsubjects.remove(subject);
				}
			}
			
			for (String subject : student.getWaivedInSubjects()) {
				if(!allsubjects.contains(subject)) {
					allsubjects.add(subject);
				}
			}
		}
		
		}catch(Exception e) {
			logger.error("Service Method applicableSubjectsForVideos For Sapid "+sapId+". Error:- ",e);
		}
		subjectbean.setCurrentSemSubjects(currentSemSubjects);
		subjectbean.setFailedSubjectList(failSubjectsBeans);
		subjectbean.setListOfApplicableSUbjects(allsubjects);
		return subjectbean;
	}
}
