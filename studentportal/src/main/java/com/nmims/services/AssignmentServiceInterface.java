package com.nmims.services;

import java.util.List;

import org.springframework.stereotype.Service;

import com.nmims.beans.AssignmentStudentPortalFileBean;

@Service("assignmentService")
public interface AssignmentServiceInterface {
	
	public List<AssignmentStudentPortalFileBean> mgetAssignmentsForStudents(final String sapid, final String consumerProgramStructureId);
	

}
