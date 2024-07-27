package com.nmims.services;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.Arrays;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Service;

import com.nmims.beans.ProgramSubjectMappingStudentPortalBean;
import com.nmims.beans.StudentStudentPortalBean;
import com.nmims.daos.PortalDao;
import com.nmims.daos.StudentDAO;

import com.nmims.interfaces.CustomCourseWaiverService;

import org.springframework.beans.BeanUtils;


@Service
public class StudentService {

	@Autowired(required=false)
	ApplicationContext act;
	
	@Autowired
	private StudentDAO studentDAO;
	
	@Autowired
	private PortalDao portalDao;
	
	@Autowired
	CustomCourseWaiverService customCourseWaiverService;
	
	private final static ArrayList<String> waivedInNotApplicable = new ArrayList<String>(Arrays.asList("127","128","159","152"));
	
	public ArrayList<String> mgetWaivedInSubjects(StudentStudentPortalBean student) {
		ArrayList<String> subjects = new ArrayList<String>();
		HashMap<String, String> studentSemMapping = new HashMap<String, String>();
		
		if(student != null && !StringUtils.isBlank(student.getPreviousStudentId()) && "Y".equalsIgnoreCase(student.getIsLateral()) && !"Jul2009".equals(student.getPrgmStructApplicable()) && !waivedInNotApplicable.contains(student.getConsumerProgramStructureId())) {
			StudentDAO sDao = (StudentDAO)act.getBean("studentDAO");
			
			// Get all subjects for which the student is/was applicable for in the previous semesters.
			//List<ProgramSubjectMappingStudentPortalBean> waivedInSubjects = sDao.getAllApplicableSubjectsForStudent(student);
			List<ProgramSubjectMappingStudentPortalBean> waivedInSubjects = new ArrayList<ProgramSubjectMappingStudentPortalBean>();
			if(student.getProgram().startsWith("MBA")) {
				waivedInSubjects = sDao.getAllApplicableSubjectsForMBAStudent(student);
			}else
				waivedInSubjects = sDao.getAllApplicableSubjectsForStudent(student);
		
			// Get all passed subjects for this student.
			List<String> previousSapIdPassSubjectList = getAllPassedSubjectNamesForSapid(student.getSapid(),student.getProgram());
			
			if("Jul2019".equalsIgnoreCase(student.getPrgmStructApplicable()) && previousSapIdPassSubjectList.contains("Business Statistics")) {
				previousSapIdPassSubjectList.add("Decision Science");
			}
			
			for (ProgramSubjectMappingStudentPortalBean programSubjectBean : waivedInSubjects) {
				if(!previousSapIdPassSubjectList.contains(programSubjectBean.getSubject())) {
					subjects.add(programSubjectBean.getSubject());
					studentSemMapping.put(programSubjectBean.getSubject(), programSubjectBean.getSem());
				}
			}
		}
		
		//to get subject for student from other university
		customCourseWaiverService.getWaivedInSubject(student,subjects,studentSemMapping);
		
		
		
		student.setWaivedInSubjects(subjects);
		student.setWaivedInSubjectSemMapping(studentSemMapping);
		return subjects;
	}
	
	public ArrayList<String> mgetWaivedOffSubjects(StudentStudentPortalBean student) {
		ArrayList<String> waivedOffSubjects = new ArrayList<String>();
		PortalDao dao = (PortalDao)act.getBean("portalDAO"); 	
		
		if(student != null && !StringUtils.isBlank(student.getPreviousStudentId()) && "Y".equalsIgnoreCase(student.getIsLateral()) && !"Jul2009".equals(student.getPrgmStructApplicable()) ) {
			
			StudentStudentPortalBean studentRegistrationData = dao.getStudentsMostRecentRegistrationData(student.getSapid());
			String program = studentRegistrationData.getProgram();
			
			/*if(program.startsWith("MBA")){ 
				waivedOffSubjects = getSem1And2PassedSubjectNamesForSapid(student.getPreviousStudentId());
				student.setWaivedOffSubjects(waivedOffSubjects);
				return waivedOffSubjects;
			}
			//StudentStudentPortalBean studentRegistrationData = new StudentStudentPortalBean();
			//studentRegistrationData = dao.getStudentsMostRecentRegistrationData(student.getSapid());
			//String program = studentRegistrationData.getProgram();
			//for MBA(distance) if sem >2 waivedoff subjects not applicable

			/*String program = student.getProgram();  
			if(program.startsWith("MBA")){
				String enrolledSem = dao.getSemFromStudentDetail(student.getSapid());
				//String currentSem = studentRegistrationData.getSem();
				// Sem 2 lateral wavedoff logic
				//if(Integer.parseInt(enrolledSem)  == 2) {
				//For MBA, Only sem-1 and sem-2 subjects from previous programs are eligible for waiver from any of the semesters from the new program
				if(student.getProgram().startsWith("MBA")){ 
						try {
							ArrayList<String> passSubjects = getSem1and2PassedSubjectNamesForSapid(student.getPreviousStudentId());
							if(passSubjects!=null) {
								waivedOffSubjects.addAll(passSubjects);
							} 
							//student.setWaivedOffSubjects(waivedOffSubjects);
							// return waivedOffSubjects;
						}
						catch (Exception e) {
						} 
					//student.setWaivedOffSubjects(waivedOffSubjects);
					return waivedOffSubjects; 
				}
				student.setWaivedOffSubjects(waivedOffSubjects);
				return waivedOffSubjects; 
			}*/

			waivedOffSubjects = getAllPassedSubjectNamesForSapid(student.getPreviousStudentId(),program);
			
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
		
		customCourseWaiverService.getWaivedOffSubject(student,waivedOffSubjects);
	
		student.setWaivedOffSubjects(waivedOffSubjects);
		return waivedOffSubjects;
	}
	
	public ArrayList<String> getAllPassedSubjectNamesForSapid(String sapid,String program) {
		// Check if this sapid is lateral as well.
		StudentDAO sDao = (StudentDAO)act.getBean("studentDAO");
		PortalDao pDao = (PortalDao)act.getBean("portalDAO"); 	
		boolean studentLateral = sDao.checkIfStudentIsLateral(sapid);		
		ArrayList<String> clearedSubjectsList = new ArrayList<String>();
		
		if(program.startsWith("MBA"))
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
			StudentStudentPortalBean studentData = pDao.getSingleStudentsData(sapid);
			List<String> clearedSubjectsForPreviousStudentNumber = getAllPassedSubjectNamesForSapid(studentData.getPreviousStudentId(),studentData.getProgram());
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
	
	public ArrayList<String> getSem1And2PassedSubjectNamesForSapid(String sapid) {
		// Check if this sapid is lateral as well.
		StudentDAO sDao = (StudentDAO)act.getBean("studentDAO"); 		
		ArrayList<String> clearedSubjectsList = sDao.getSem1And2PassSubjectsNamesForAStudent(sapid);
		
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
	
	
	public List<StudentStudentPortalBean> getWaivedinSubjectsReport(String month, String year) throws Exception {
		// getting all sapid from registration table based on year and month
		List<String> sapidList = studentDAO.getsapIdsFromRegistration(month, year);
		
		// featching all sapid information from students table based on is lateral Y
		List<StudentStudentPortalBean> isLateralYList = studentDAO.getstudentInfoList();

		// filtering the isLateralYList based on sapid which i present in the sapidList
		List<StudentStudentPortalBean> pgmChangedStudList = isLateralYList.stream()
				.filter(student -> sapidList.contains(student.getSapid())).collect(Collectors.toList());
		
		Set<String> masterKeyList =new HashSet<String>();
		
		pgmChangedStudList.stream().forEach(x->masterKeyList.add(x.getConsumerProgramStructureId()));
		List<StudentStudentPortalBean> finalPgmChangedStudList = new ArrayList<StudentStudentPortalBean>();
		
		if(!(masterKeyList.isEmpty())){
		//get all subjects and sem from program_sem_subject table 
		List<StudentStudentPortalBean> subjectSemList=studentDAO.getsubjectSemByMasterKeyList(masterKeyList);
		
		Map<String,String> subjectsemMap=new HashMap<String, String>();
		subjectSemList.forEach(x->subjectsemMap.put(x.getConsumerProgramStructureId()+"-"+x.getSubject(), x.getSem()));
		// for each student of sapid retriving waivedin subjects by using
		// mgetWaivedInSubjects() method
		
		pgmChangedStudList.stream().forEach(x -> {
			ArrayList<String> subjects = mgetWaivedInSubjects(x);
			ArrayList<String> failedSubjects = portalDao.getFailSubjectsNamesForAStudent(x.getSapid());

			subjects.stream().forEach(subject -> {
				if(!(failedSubjects.contains(subject))) {

				StudentStudentPortalBean studBean = new StudentStudentPortalBean();
				BeanUtils.copyProperties(x, studBean);
				studBean.setSubject(subject);
				studBean.setSem(subjectsemMap.get(x.getConsumerProgramStructureId()+"-"+subject));
				finalPgmChangedStudList.add(studBean);
				}
			});

		});
		}
		return finalPgmChangedStudList;
	}
	
	public HashMap<String, Integer> getapplicableSubjectsForStudent(String sapid) {
		
		return studentDAO.getapplicableSubjectsForStudent(sapid);
	}

}
