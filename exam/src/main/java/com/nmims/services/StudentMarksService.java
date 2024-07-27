package com.nmims.services;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Service;

import com.nmims.beans.AssignmentFileBean;
import com.nmims.beans.StudentMarksBean;
import com.nmims.daos.PassFailDAO;
import com.nmims.daos.StudentMarksDAO;

@Service
public class StudentMarksService {
	@Autowired(required=false)
	ApplicationContext act;
	
	@Autowired
	private PassFailDAO dao;
		
	public List<StudentMarksBean> getStudentAssignmentRemarks(List<StudentMarksBean> marksList){
		
		for(StudentMarksBean b :marksList){
			if("0".equalsIgnoreCase(b.getAssignmentscore())){
				AssignmentFileBean assignmentRemarks = dao.getAssigmentRemarksForSingleStudentYearMonth(b.getSapid(),b.getMonth(),b.getYear(),b.getSubject());
				if(!StringUtils.isBlank(assignmentRemarks.getReason())){
					b.setAssignmentRemarks(assignmentRemarks.getReason());
				}
				if(!StringUtils.isBlank(assignmentRemarks.getFinalReason())){
					b.setAssignmentRemarks(assignmentRemarks.getFinalReason());
				}
			}
		}
		return marksList;
	}

	public List<StudentMarksBean> getStudentMarksList(StudentMarksBean studentMark){
		StudentMarksDAO studentMarksDAO = (StudentMarksDAO)act.getBean("studentMarksDAO");

		List<StudentMarksBean> marksList=studentMarksDAO.getStudentMarks(studentMark);
		List<StudentMarksBean>AllMarkList=marksList;
		List<StudentMarksBean> getSapidnCPSIdFromStudent=studentMarksDAO.getSapidnCPSIdFromStudent();
		List<StudentMarksBean>getCPSidSubjectAndCode=studentMarksDAO.getCPSidSubjectAndCode();
		List<StudentMarksBean>getRegisteredStudents=studentMarksDAO.getSapidnCPSIdFromRegistration();
	
		//Compare Marks table With Student Table And Set ConsumerProgramStructureId To Marks table.
		//Got new Marks Table
		marksList.stream().forEach(l->l.setConsumerProgramStructureId(getSapidnCPSIdFromStudent.stream().
				filter(m->m.getSapid().equals(l.getSapid())).findAny().get().getConsumerProgramStructureId()));
				
		//Compare new Marks table With PSS(program_Sem_Subject table) with CPSId And SUbject and set SifySubjectCode on new Marks Table.
		//Got new Marks Table
		marksList.stream().forEach(l -> {
		    Optional<StudentMarksBean> optional = getCPSidSubjectAndCode.stream()
		        .filter(m -> m.getConsumerProgramStructureId().equals(l.getConsumerProgramStructureId()))
		        .filter(n -> n.getSubject().equals(l.getSubject()))
		        .findAny();
		   
		    if (optional.isPresent()) {
		        l.setSifySubjectCode(optional.get().getSifySubjectCode());
		    } else {
		    	l.setSifySubjectCode("");
		    }
		});
		
		//Compare New  Marks Table With Registration table with (it's Sapid And Sem).
		//Got New Marks Table
		List<StudentMarksBean> newMarksList = marksList.stream()
			    .filter(l -> getRegisteredStudents.stream()
			        .anyMatch(r -> r.getSapid().equals(l.getSapid()) && r.getSem().equals(l.getSem())))
			    .collect(Collectors.toList());
		
		//Filter with noneMatch with (Marks Table and new Marks Table) And After Got WaivedIn SUbject Marks List.
		List<StudentMarksBean>waivedInSubjectList=AllMarkList.stream().filter(l->newMarksList.stream().noneMatch(r->r.getSapid().equals(l.getSapid())&&r.getSem()
				.equals(l.getSem()))).collect(Collectors.toList());
		
		
		return waivedInSubjectList;
	}
	
}
