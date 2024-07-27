package com.nmims.interfaces;

import java.util.ArrayList;

import javax.servlet.http.HttpServletRequest;

import com.nmims.beans.ExamsAssessmentsBean;
import com.nmims.beans.MettlRegisterCandidateBean;
import com.nmims.beans.MettlResponseBean;
import com.nmims.beans.MettlScheduleExamBean;
import com.nmims.beans.ScheduleCreationBean;

public interface MettlAssessments {
	
	ArrayList<MettlResponseBean> getAllAssessments();
	String createSchedule(HttpServletRequest request,ScheduleCreationBean scheduleBean,String userId) throws Exception;
	ExamsAssessmentsBean createDbBean(MettlScheduleExamBean dbBean,ScheduleCreationBean scheduleBean,String userId);
	String registerCandidates(HttpServletRequest request,ExamsAssessmentsBean examBean,String subjectName,String endTime,boolean isReExam,boolean isExcelUpload,ArrayList<MettlRegisterCandidateBean> excelUserList);
}
