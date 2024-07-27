package com.nmims.services;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Service;

import com.nmims.beans.ProgramSubjectMappingExamBean;
import com.nmims.beans.StudentExamBean;
import com.nmims.beans.WebCopycaseBean;
import com.nmims.daos.AssignmentsDAO;
import com.nmims.daos.PassFailDAO;
import com.nmims.daos.StudentDAO;
import com.nmims.interfaces.CustomCourseWaiverService;

@Service
public class StudentService {

	@Autowired(required=false)
	ApplicationContext act;
	
	@Autowired
	PassFailDAO pdao;
	
	@Autowired
	AssignmentsDAO asignmentsDAO;

	@Autowired
	CustomCourseWaiverService customCourseWaiverService;

	private final static ArrayList<String> waivedInNotApplicable = new ArrayList<String>(Arrays.asList("127","128","159","152"));
	
	public ArrayList<String> mgetWaivedInSubjects(StudentExamBean student) {
		ArrayList<String> subjects = new ArrayList<String>();
		HashMap<String, String> studentSemMapping = new HashMap<String, String>();

		if(student != null && 
				((!StringUtils.isBlank(student.getPreviousStudentId()) && "Y".equalsIgnoreCase(student.getIsLateral())) 
				|| "Y".equalsIgnoreCase(student.getProgramChanged()))
				&& !"Jul2009".equals(student.getPrgmStructApplicable())  && !waivedInNotApplicable.contains(student.getConsumerProgramStructureId())) {
			StudentDAO sDao = (StudentDAO)act.getBean("studentDAO");
			//List<ProgramSubjectMappingExamBean> waivedInSubjects = sDao.getAllApplicableSubjectsForStudent(student);
			List<ProgramSubjectMappingExamBean> waivedInSubjects = new ArrayList<ProgramSubjectMappingExamBean>();
			if(student.getProgram().startsWith("MBA") && !"Y".equalsIgnoreCase(student.getProgramChanged())) {
				waivedInSubjects = sDao.getAllApplicableSubjectsForMBAStudent(student);
			}else
				waivedInSubjects = sDao.getAllApplicableSubjectsForStudent(student);
			
			List<String> previousSapIdPassSubjectList = getAllPassedSubjectNamesForSapid(student.getSapid(),student.getProgram(),student.getProgramChanged());
		
			if("Jul2019".equalsIgnoreCase(student.getPrgmStructApplicable()) && previousSapIdPassSubjectList.contains("Business Statistics")) {
				previousSapIdPassSubjectList.add("Decision Science");
			}
			
			for (ProgramSubjectMappingExamBean programSubjectBean : waivedInSubjects) {
				if(!previousSapIdPassSubjectList.contains(programSubjectBean.getSubject())) {
					subjects.add(programSubjectBean.getSubject());
					studentSemMapping.put(programSubjectBean.getSubject(), programSubjectBean.getSem());
				}
			}
		}
		customCourseWaiverService.getWaivedInSubject(student, subjects, studentSemMapping);
		student.setWaivedInSubjects(subjects);
		student.setWaivedInSubjectSemMapping(studentSemMapping);
		return subjects;
	}
	
	public ArrayList<String> mgetWaivedOffSubjects(StudentExamBean student) {
		ArrayList<String> waivedOffSubjects = new ArrayList<String>();
		if(student != null && !StringUtils.isBlank(student.getPreviousStudentId()) && "Y".equalsIgnoreCase(student.getIsLateral()) && !"Jul2009".equals(student.getPrgmStructApplicable()) || "Y".equalsIgnoreCase(student.getProgramChanged()) ) {
			AssignmentsDAO dao = (AssignmentsDAO)act.getBean("asignmentsDAO");
			

			StudentExamBean studentData = dao.getSingleStudentsData(student.getSapid());
			/*String program = studentData.getProgram();
			System.out.println("program>>"+program);
			if(program.startsWith("MBA")){ 
				waivedOffSubjects = getSem1And2PassedSubjectNamesForSapid(student.getPreviousStudentId());
				System.out.println("waivedOffSubjects>>"+waivedOffSubjects);
				student.setWaivedOffSubjects(waivedOffSubjects);
				return waivedOffSubjects;
			}
			//for MBA(distance) if sem >2 waivedoff subjects not applicable
			/*if(student.getProgram().startsWith("MBA")){
				//get registration data
				StudentExamBean studentRegistrationData = new StudentExamBean();
				
				String enrolledSem = student.getSem();
				
				if("Diageo".equalsIgnoreCase(student.getConsumerType())) { // temp fix for diageo students
					studentRegistrationData = dao.getDiageoStudentRegistrationData(student.getSapid());
					enrolledSem = studentRegistrationData.getSem();
				}
				// Sem 2 lateral wavedoff logic
				//if(Integer.parseInt(enrolledSem)  == 2) {
					try {
						ArrayList<String> sem1and2Subjects = dao.getSubjectsForSem1and2(student.getConsumerProgramStructureId());
						 ArrayList<String> passSubjects = getAllPassedSubjectNamesForSapid(student.getPreviousStudentId());
						 for(String semSubject :sem1and2Subjects) {
							 if(passSubjects.contains(semSubject)) {
								 waivedOffSubjects.add(semSubject);
							 }
						 }
					 }
					//student.setWaivedOffSubjects(waivedOffSubjects);
					// return waivedOffSubjects;
				}
				catch (Exception e) {
				}
				student.setWaivedOffSubjects(waivedOffSubjects);
				return waivedOffSubjects; 
			}*/
			waivedOffSubjects = getAllPassedSubjectNamesForSapid(student.getPreviousStudentId(),student.getProgram(),studentData.getProgramChanged());
			
			if(waivedOffSubjects.contains("Business Statistics")){
				if("EPBM".equalsIgnoreCase(student.getProgram())){
					waivedOffSubjects.remove("Business Statistics");
					waivedOffSubjects.add("Business Statistics- EP");
				}
				
				if("MPDV".equalsIgnoreCase(student.getProgram())){
					waivedOffSubjects.remove("Business Statistics");
					waivedOffSubjects.add("Business Statistics- MP");
				}
			}
		}
		customCourseWaiverService.getWaivedOffSubject(student, waivedOffSubjects);
		student.setWaivedOffSubjects(waivedOffSubjects);
		return waivedOffSubjects;
	}
	
	public ArrayList<String> getAllPassedSubjectNamesForSapid(String sapid) {
		// Check if this sapid is lateral as well.
		StudentDAO sDao = (StudentDAO)act.getBean("studentDAO");
		AssignmentsDAO dao = (AssignmentsDAO)act.getBean("asignmentsDAO");

		StudentExamBean studentData = dao.getSingleStudentsData(sapid);
		ArrayList<String> clearedSubjectsList = sDao.getPassSubjectsNamesForAStudent(sapid);
		
		if(clearedSubjectsList.contains("Business Communication and Etiquette")){
			clearedSubjectsList.add("Business Communication");
		}

		if(clearedSubjectsList.contains("Business Communication")){
			clearedSubjectsList.add("Business Communication and Etiquette");
		}
	
		if ("Y".equals(studentData.getIsLateral())) {
			
			List<String> clearedSubjectsForPreviousStudentNumber = new ArrayList<>();
			
			if (StringUtils.isBlank(studentData.getPreviousStudentId())) {
				// If lateral, get the subjects cleared for this sapid and check if it was
				// lateral; repeat
				boolean sapidExist = false;
				sapidExist = customCourseWaiverService.checkSapidExist(sapid);
				if (!sapidExist) {
					clearedSubjectsForPreviousStudentNumber = getPassFailSubjectsForSapid(studentData);
				}
			} else{
				clearedSubjectsForPreviousStudentNumber = getPassFailSubjectsForSapid(studentData);
			}
			clearedSubjectsList.addAll(clearedSubjectsForPreviousStudentNumber);
		}
			
		
		// Remove duplicates from list
		ArrayList<String> listToReturn = new ArrayList<String>();
		for (String subject : clearedSubjectsList) {
			if(!listToReturn.contains(subject)) {
				listToReturn.add(subject);
			}
		}
		
		return listToReturn;
	}
	public ArrayList<String> getAllFailSubjectNamesForSapid(String sapid) {
		// Check if this sapid is lateral as well.
		StudentDAO sDao = (StudentDAO)act.getBean("studentDAO"); 
		boolean studentLateral = sDao.checkIfStudentIsLateral(sapid);		
		ArrayList<String> failedSubjecysList = pdao.getFailSubjectsNamesForAStudent(sapid);
		
		if(failedSubjecysList.contains("Business Communication and Etiquette")){
			failedSubjecysList.add("Business Communication");
		}

		if(failedSubjecysList.contains("Business Communication")){
			failedSubjecysList.add("Business Communication and Etiquette");
		}
		 
		
		// Remove duplicates from list
		ArrayList<String> listToReturn = new ArrayList<String>();
		for (String subject : failedSubjecysList) {
			if(!listToReturn.contains(subject)) {
				listToReturn.add(subject);
			}
		}
		return listToReturn;
	}

	public ArrayList<String> getSem1And2PassedSubjectNamesForSapid(String sapid) {
		// Check if this sapid is lateral as well.
		StudentDAO sDao = (StudentDAO)act.getBean("studentDAO"); 		
		ArrayList<String> clearedSubjectsList = sDao.getSem1And2PassSubjectsNamesForAStudent(sapid);
		System.out.println("clearedSubjectsList>>"+clearedSubjectsList);

		if(clearedSubjectsList.contains("Business Communication and Etiquette")){
			clearedSubjectsList.add("Business Communication");
		}

		if(clearedSubjectsList.contains("Business Communication")){
			clearedSubjectsList.add("Business Communication and Etiquette");
		}

		// Remove duplicates from list
		ArrayList<String> listToReturn = new ArrayList<String>();
		for (String subject : clearedSubjectsList) {
			if(!listToReturn.contains(subject)) {
				listToReturn.add(subject);
			}
		}
		return listToReturn;
	}

	public List<String> mgetWaivedInSubjects(String sapId, String isLateral, String prgmStructApplicable,
			String programChanged, String previousStudentId, String consumerProgramStructureId, String program) {
		//Prepare StudentExamBean and set required values.
		StudentExamBean student = new StudentExamBean();
		student.setSapid(sapId);
		student.setIsLateral(isLateral);
		student.setPrgmStructApplicable(prgmStructApplicable);
		student.setProgramChanged(programChanged);
		student.setPreviousStudentId(previousStudentId);
		student.setConsumerProgramStructureId(consumerProgramStructureId);
		student.setProgram(program);
	
		//Get Waived-In subjects list.
		List<String> waivedInSubjectsList = this.mgetWaivedInSubjects(student);
	
		//Return Waived-In subjects list.
		return waivedInSubjectsList;
	}
	
	//created overloaded method to avoid conflicts
	
	public ArrayList<String> getAllPassedSubjectNamesForSapid(String sapid,String program,String programChanged) {
		// Check if this sapid is lateral as well.
		StudentDAO sDao = (StudentDAO)act.getBean("studentDAO");
		AssignmentsDAO dao = (AssignmentsDAO)act.getBean("asignmentsDAO");
		boolean studentLateral = sDao.checkIfStudentIsLateral(sapid);		
		//ArrayList<String> clearedSubjectsList = sDao.getPassSubjectsNamesForAStudent(sapid);
		
		ArrayList<String> clearedSubjectsList = new ArrayList<String>();
		if(program.startsWith("MBA") && !"Y".equalsIgnoreCase(programChanged))
			clearedSubjectsList = sDao.getSem1And2PassSubjectsNamesForAStudent(sapid);
		else
			clearedSubjectsList = sDao.getPassSubjectsNamesForAStudent(sapid);	
		
		if(clearedSubjectsList.contains("Business Communication and Etiquette")){
			clearedSubjectsList.add("Business Communication");
		}

		if(clearedSubjectsList.contains("Business Communication")){
			clearedSubjectsList.add("Business Communication and Etiquette");
		}
		
		if(studentLateral) {
			// If lateral, get the subjects cleared for this sapid and check if it was lateral; repeat
			//String previousStudentId = sDao.getPreviousStudentNumber(sapid);
			StudentExamBean studentData = dao.getSingleStudentsData(sapid);
			List<String> clearedSubjectsForPreviousStudentNumber = getAllPassedSubjectNamesForSapid(studentData.getPreviousStudentId(),studentData.getProgram(),studentData.getProgramChanged());
			clearedSubjectsList.addAll(clearedSubjectsForPreviousStudentNumber);
		}
		
		// Remove duplicates from list
		ArrayList<String> listToReturn = new ArrayList<String>();
		for (String subject : clearedSubjectsList) {
			if(!listToReturn.contains(subject)) {
				listToReturn.add(subject);
			}
		}
		return listToReturn;
	}
	
	public List<String> mWaivedOffSubjectsForProgramComplete(StudentExamBean student,List<String> waivedOffSubjects,List<String> clearedSubjects) {
		StudentDAO sDao = (StudentDAO)act.getBean("studentDAO");
		
		List<String> finalWaivedOffSubjects = new ArrayList<String>();
		List<String> applicableSubjects = sDao.getApplicableSubjectNew(student.getConsumerProgramStructureId());
		
		if(waivedOffSubjects.contains("Business Communication and Etiquette") && waivedOffSubjects.contains("Business Communication") )
			waivedOffSubjects.remove("Business Communication and Etiquette");
		
		if("Jul2019".equalsIgnoreCase(student.getPrgmStructApplicable()) && waivedOffSubjects.contains("Business Statistics")) 
			finalWaivedOffSubjects.add("Decision Science");
		
		finalWaivedOffSubjects.addAll(waivedOffSubjects.stream().filter(applicableSubjects::contains).filter(str -> !clearedSubjects.contains(str)).collect(Collectors.toList()));
		
		return finalWaivedOffSubjects;
		
	}

	public List<WebCopycaseBean> getCcStudentList(String month, String year, String subject) throws Exception {
		// TODO Auto-generated method stub
		
		return asignmentsDAO.getwebPlagiarismDetectedStudetns(month, year, subject);
	}
	
	
	
	
	public List<String> getPassFailSubjectsForSapid(StudentExamBean studentData) {
	List<String> clearedSubjectsForPreviousStudentNumber = new ArrayList<>();
	if (studentData.getProgram().startsWith("MBA")) {
		clearedSubjectsForPreviousStudentNumber = getAllPassedSubjectNamesForSapid(studentData.getPreviousStudentId(), studentData.getProgram(),studentData.getProgramChanged());
	} else {
		clearedSubjectsForPreviousStudentNumber = getAllPassedSubjectNamesForSapid(studentData.getPreviousStudentId());
	}
	return clearedSubjectsForPreviousStudentNumber;
}
}
