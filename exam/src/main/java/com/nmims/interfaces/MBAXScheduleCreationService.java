package com.nmims.interfaces;

import java.util.ArrayList;

import javax.servlet.http.HttpServletRequest;

import com.nmims.beans.ExamsAssessmentsBean;
import com.nmims.beans.FailedRegistrationBean;
import com.nmims.beans.FailedRegistrationResponseBean;
import com.nmims.beans.MettlRegisterCandidateBean;
import com.nmims.beans.MettlScheduleExamBean;
import com.nmims.beans.ScheduleCreationBean;

public interface MBAXScheduleCreationService {

	MettlScheduleExamBean createApiParametersWithWaitingRoom(ScheduleCreationBean scheduleBean) throws Exception;

	MettlScheduleExamBean createApiParametersWithoutWaitingRoom(ScheduleCreationBean scheduleBean) throws Exception;

	ExamsAssessmentsBean createDbBean(MettlScheduleExamBean dbBean, ScheduleCreationBean scheduleBean, String userId);

	String extendExamEndDateTime(String slotDate_id, String endTime, String assessmentDuration) throws Exception;

	String createScheduleForMBAX(ScheduleCreationBean scheduleBean, MettlAssessments mettlAssesments, String userId, HttpServletRequest request);

	FailedRegistrationResponseBean registerExcelStudent(FailedRegistrationBean bean, MettlAssessments assessments,HttpServletRequest request)throws Exception;

	ArrayList<MettlRegisterCandidateBean> getNotRegisterStudentOfExcel(String timebound_id, String schedule_accessKey,
			ArrayList<MettlRegisterCandidateBean> userList);


}
