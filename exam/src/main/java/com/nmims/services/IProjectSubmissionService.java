package com.nmims.services;

import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.nmims.beans.AssignmentFilesSetbean;
import com.nmims.beans.StudentExamBean;
import com.nmims.beans.UserAuthorizationExamBean;

public interface IProjectSubmissionService {

	public List<StudentExamBean> getProjectPendingReport(UserAuthorizationExamBean userAuthorizationBean , AssignmentFilesSetbean filesSet)throws Exception;
}
