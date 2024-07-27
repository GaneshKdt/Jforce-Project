package com.nmims.views;

import java.util.ArrayList;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContextAware;
import org.springframework.web.servlet.view.document.AbstractXlsxStreamingView;

import com.nmims.beans.ExamsAssessmentsBean;
import com.nmims.beans.MettlRegisterCandidateBean;

public class FailedRegistrationsExcelView extends AbstractXlsxStreamingView implements ApplicationContextAware{
	
	private static final Logger logger = LoggerFactory.getLogger("examRegisterPG");
	
	@Override
	protected void buildExcelDocument(Map<String, Object> model, Workbook workbook, HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		try {
			
		ArrayList<MettlRegisterCandidateBean> userList = (ArrayList<MettlRegisterCandidateBean>) model.get("userList");
		ExamsAssessmentsBean examBean = (ExamsAssessmentsBean)model.get("examBean");
		String subjectName = (String)model.get("subjectName");
		String endTime = (String)model.get("endTime");
		//create a wordsheet :- START
		Sheet sheet = workbook.createSheet("Registration Failure Records");
		
		int index = 0;
		Row header = sheet.createRow(0);
		header.createCell(index++).setCellValue("FirstName");
		header.createCell(index++).setCellValue("LastName");
		header.createCell(index++).setCellValue("Sapid");
		header.createCell(index++).setCellValue("EmailId");
		header.createCell(index++).setCellValue("ImageUrl");
		header.createCell(index++).setCellValue("OpenLinkFlag");
		header.createCell(index++).setCellValue("ScheduleAccessKey");
		header.createCell(index++).setCellValue("TimeBoundId");
		header.createCell(index++).setCellValue("ExamStartDateTime");
		header.createCell(index++).setCellValue("ExamEndDateTime");
		header.createCell(index++).setCellValue("AccessExamStartDateTime");
		header.createCell(index++).setCellValue("AccessExamEndDateTime");
		header.createCell(index++).setCellValue("AssessmentName");
		header.createCell(index++).setCellValue("AssessmentDuration");
		header.createCell(index++).setCellValue("ScheduleId");
		header.createCell(index++).setCellValue("ScheduleAccessUrl");
		header.createCell(index++).setCellValue("ScheduleName");
		header.createCell(index++).setCellValue("SubjectName");
		header.createCell(index++).setCellValue("MaxScore");
		header.createCell(index++).setCellValue("ReportingStartTime");
		header.createCell(index++).setCellValue("ReportingFinishTime");
		
		int rowNum = 1;
		for (int i = 0 ; i < userList.size(); i++) {
			//create the row data
			index = 0;
			Row row = sheet.createRow(rowNum++);
			MettlRegisterCandidateBean candidate  = userList.get(i);
			row.createCell(index++).setCellValue(candidate.getFirstName());
			row.createCell(index++).setCellValue(candidate.getLastName());
			row.createCell(index++).setCellValue(candidate.getSapId());
			row.createCell(index++).setCellValue(candidate.getEmailAddress());
			row.createCell(index++).setCellValue(candidate.getCandidateImage());
			row.createCell(index++).setCellValue(candidate.getOpenLinkFlag());
			row.createCell(index++).setCellValue(examBean.getSchedule_accessKey());
			row.createCell(index++).setCellValue(examBean.getTimebound_id());
			row.createCell(index++).setCellValue(examBean.getExam_start_date_time());
			row.createCell(index++).setCellValue(endTime);
			row.createCell(index++).setCellValue(examBean.getExam_start_date_time());
			row.createCell(index++).setCellValue(examBean.getExam_end_date_time());
			row.createCell(index++).setCellValue(examBean.getName());
			row.createCell(index++).setCellValue(Integer.toString(examBean.getDuration()));
			row.createCell(index++).setCellValue(examBean.getSchedule_id());
			row.createCell(index++).setCellValue(examBean.getSchedule_accessUrl());
			row.createCell(index++).setCellValue(examBean.getSchedule_name());
			row.createCell(index++).setCellValue(subjectName);
			row.createCell(index++).setCellValue(examBean.getMax_score());
			row.createCell(index++).setCellValue(examBean.getReporting_start_date_time());
			row.createCell(index++).setCellValue(examBean.getReporting_finish_date_time());
        }
		
	}
	catch(Exception e)
	{
		logger.error("Error in Excel is:"+e.getMessage());
	}
	}
	
}