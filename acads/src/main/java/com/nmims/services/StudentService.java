package com.nmims.services;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Service;

import com.nmims.beans.ProgramSubjectMappingAcadsBean;
import com.nmims.beans.StudentAcadsBean;
import com.nmims.daos.StudentDAO;
import com.nmims.daos.ContentDAO;
@Service
public class StudentService {

	@Autowired(required=false)
	ApplicationContext act;
	
	@Autowired
	StudentDAO studentDAO;
	
	private static final Logger logger = LoggerFactory.getLogger(StudentService.class);

	private final static ArrayList<String> waivedInNotApplicable = new ArrayList<String>(Arrays.asList("127","128","159","152"));
	
	public ArrayList<String> mgetWaivedInSubjects(StudentAcadsBean student) {
		ArrayList<String> subjects = new ArrayList<String>();
		HashMap<String, String> studentSemMapping = new HashMap<String, String>();
		
		if(student != null && !StringUtils.isBlank(student.getPreviousStudentId()) && "Y".equalsIgnoreCase(student.getIsLateral()) && !"Jul2009".equals(student.getPrgmStructApplicable()) && !waivedInNotApplicable.contains(student.getConsumerProgramStructureId())) {
			StudentDAO sDao = (StudentDAO)act.getBean("studentDAO");
			List<ProgramSubjectMappingAcadsBean> waivedInSubjects = sDao.getAllApplicableSubjectsForStudent(student);
			List<String> previousSapIdPassSubjectList = getAllPassedSubjectNamesForSapid(student.getPreviousStudentId());
			
			if("Jul2019".equalsIgnoreCase(student.getPrgmStructApplicable()) && previousSapIdPassSubjectList.contains("Business Statistics")) {
				previousSapIdPassSubjectList.add("Decision Science");
			}
			
			for (ProgramSubjectMappingAcadsBean programSubjectBean : waivedInSubjects) {
				if(!previousSapIdPassSubjectList.contains(programSubjectBean.getSubject())) {
					subjects.add(programSubjectBean.getSubject());
					studentSemMapping.put(programSubjectBean.getSubject(), programSubjectBean.getSem());
				}
			}
		}
		
		student.setWaivedInSubjects(subjects);
		student.setWaivedInSubjectSemMapping(studentSemMapping);
		return subjects;
	}
	
	public ArrayList<String> mgetWaivedOffSubjects(StudentAcadsBean student) {
		ArrayList<String> waivedOffSubjects = new ArrayList<String>();
		ContentDAO dao = (ContentDAO)act.getBean("contentDAO");
	
		if(student != null && !StringUtils.isBlank(student.getPreviousStudentId()) && "Y".equalsIgnoreCase(student.getIsLateral()) && !"Jul2009".equals(student.getPrgmStructApplicable()) ) {
			//for MBA(distance) if sem >2 waivedoff subjects not applicable
			/*
			 * String program = student.getProgram(); if(program.startsWith("MBA")){
			 */
				//String currentSem = studentRegistrationData.getSem();
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
					student.setWaivedOffSubjects(waivedOffSubjects);
					return waivedOffSubjects; 
				}
			waivedOffSubjects = getAllPassedSubjectNamesForSapid(student.getPreviousStudentId());
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
		
		student.setWaivedOffSubjects(waivedOffSubjects);
		return waivedOffSubjects;
	}
	
	public ArrayList<String> getAllPassedSubjectNamesForSapid(String sapid) {
		// Check if this sapid is lateral as well.
		StudentDAO sDao = (StudentDAO)act.getBean("studentDAO");
		boolean studentLateral = sDao.checkIfStudentIsLateral(sapid);		
		ArrayList<String> clearedSubjectsList = sDao.getPassSubjectsNamesForAStudent(sapid);
		
		if(clearedSubjectsList.contains("Business Communication and Etiquette")){
			clearedSubjectsList.add("Business Communication");
		}

		if(clearedSubjectsList.contains("Business Communication")){
			clearedSubjectsList.add("Business Communication and Etiquette");
		}
		
		if(studentLateral) {
			// If lateral, get the subjects cleared for this sapid and check if it was lateral; repeat
			String previousStudentId = sDao.getPreviousStudentNumber(sapid);
			List<String> clearedSubjectsForPreviousStudentNumber = getAllPassedSubjectNamesForSapid(previousStudentId);
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
	
	public List<Integer> fetchPSSforLiveSessionAccess(final String sapId) {
		logger.info("StudentService : fetchPSSforLiveSessionAccess");
		List<Integer> list = null;
		list = studentDAO.fetchPSSforLiveSessionAccess(sapId);
		return list;
	}
	
	public ArrayList<String> getSem1and2PassedSubjectNamesForSapid(String sapid) {
		// Check if this sapid is lateral as well.
		StudentDAO sDao = (StudentDAO)act.getBean("studentDAO");
		boolean studentLateral = sDao.checkIfStudentIsLateral(sapid);		
		ArrayList<String> clearedSubjectsList = sDao.getSem1and2PassSubjectsNamesForAStudent(sapid);
		
		if(clearedSubjectsList.contains("Business Communication and Etiquette")){
			clearedSubjectsList.add("Business Communication");
		}

		if(clearedSubjectsList.contains("Business Communication")){
			clearedSubjectsList.add("Business Communication and Etiquette");
		}
		
		if(studentLateral) {
			// If lateral, get the subjects cleared for this sapid and check if it was lateral; repeat
			String previousStudentId = sDao.getPreviousStudentNumber(sapid);
			List<String> clearedSubjectsForPreviousStudentNumber = getAllPassedSubjectNamesForSapid(previousStudentId);
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
	
	public ArrayList<String> getListOfLiveSessionAccessMasterKeys(List<String> timeboundPortalList) {
		logger.info("LiveSessionAccessService : getListOfLiveSessionAccessMasterKeys");
		ArrayList<String> list = null;
		list = studentDAO.getNon_pgProgramList(timeboundPortalList);
		return list;
	}
}
