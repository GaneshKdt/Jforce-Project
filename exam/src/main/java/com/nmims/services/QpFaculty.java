package com.nmims.services;

import java.util.List;

import org.springframework.stereotype.Service; 
import com.nmims.beans.AssignmentFileBean;
import com.nmims.beans.AssignmentFilesSetbean;

@Service("qpFaculty")
public interface QpFaculty {
	//String uploadAssignmentQpFile(AssignmentFileBean bean, String year, String month); 
	List<AssignmentFilesSetbean> getPendingUploadList(String facultyId);
    AssignmentFilesSetbean countOfQpNotUploaded(String facultyId);
	List<AssignmentFilesSetbean> getQuestionsByQpId(String qpId);
}
