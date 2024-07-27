package com.nmims.interfaces;

import com.nmims.beans.ReExamEligibleStudentsResponseBean;

public interface ReExamEligibleStudentsReportServiceInterface {
	public ReExamEligibleStudentsResponseBean getReExamEligibleStudents(ReExamEligibleStudentsResponseBean searchBean) throws Exception;
}
