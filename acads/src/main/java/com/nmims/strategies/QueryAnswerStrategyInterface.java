package com.nmims.strategies;

import java.util.ArrayList;
import java.util.List;

import com.nmims.beans.ExamOrderAcadsBean;
import com.nmims.beans.QueryAnswerListBean;
import com.nmims.beans.SessionQueryAnswer;

public interface QueryAnswerStrategyInterface {
	
	public abstract void postQueryAsForum(SessionQueryAnswer sessionQuery, String year, String month) throws Exception;
	public abstract void updateForumStatus(String assignetoFacultyId, String sessionQueryId) throws Exception;
	public abstract ExamOrderAcadsBean getForumCurrentlyLive() throws Exception;
	public abstract QueryAnswerListBean getAssignedqueriesForFaculty(ArrayList<String> nonPG_ProgramList, String facultyId) throws Exception;
	public abstract List<SessionQueryAnswer> getPublicCourseQueriesForMobile(String sapId, String programSemSubjectId, String year, String month);

}
