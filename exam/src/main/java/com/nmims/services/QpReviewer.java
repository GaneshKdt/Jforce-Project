package com.nmims.services;

import java.util.List;

import org.springframework.stereotype.Service;

import com.nmims.beans.AssignmentFilesSetbean;
@Service("qpReviewer")
public interface QpReviewer { 
	public AssignmentFilesSetbean sendFeedback(AssignmentFilesSetbean filesSet);
	AssignmentFilesSetbean approveOrRejectFile(AssignmentFilesSetbean filesSet); 
	List<AssignmentFilesSetbean> getPendingReviewList(String facultyId);
	
}
