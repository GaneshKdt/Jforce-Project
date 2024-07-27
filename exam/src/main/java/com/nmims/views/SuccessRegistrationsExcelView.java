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
import com.nmims.beans.MettlRegisterCandidateBean;

public class SuccessRegistrationsExcelView extends AbstractXlsxStreamingView implements ApplicationContextAware{
		
		private static final Logger logger = LoggerFactory.getLogger("examRegisterPG");
		
		@Override
		protected void buildExcelDocument(Map<String, Object> model, Workbook workbook, HttpServletRequest request,
				HttpServletResponse response) throws Exception {
			try {
				
			ArrayList<MettlRegisterCandidateBean> userList = (ArrayList<MettlRegisterCandidateBean>) model.get("userList");
			
			//create a wordsheet :- START
			Sheet sheet = workbook.createSheet("Registration Success Records");
			
			int index = 0;
			Row header = sheet.createRow(0);
			header.createCell(index++).setCellValue("Sapid");
			header.createCell(index++).setCellValue("FirstName");
			header.createCell(index++).setCellValue("LastName");
			header.createCell(index++).setCellValue("EmailId");
			header.createCell(index++).setCellValue("BatchName");
			header.createCell(index++).setCellValue("SubjectName");
			header.createCell(index++).setCellValue("AssessmentName");
			header.createCell(index++).setCellValue("ScheduleName");
			header.createCell(index++).setCellValue("ScheduleAccessKey");
			header.createCell(index++).setCellValue("ExamStartDateTime");
		
			
			
			int rowNum = 1;
			for (int i = 0 ; i < userList.size(); i++) {
				//create the row data
				index = 0;
				Row row = sheet.createRow(rowNum++);
				MettlRegisterCandidateBean candidate  = userList.get(i);
				row.createCell(index++).setCellValue(candidate.getSapId());
				row.createCell(index++).setCellValue(candidate.getFirstName());
				row.createCell(index++).setCellValue(candidate.getLastName());
				row.createCell(index++).setCellValue(candidate.getEmailAddress());
				row.createCell(index++).setCellValue(candidate.getBatchName());
				row.createCell(index++).setCellValue(candidate.getSubject());
				row.createCell(index++).setCellValue(candidate.getAssessmentName());
				row.createCell(index++).setCellValue(candidate.getScheduleName());
				row.createCell(index++).setCellValue(candidate.getScheduleAccessKey());
				row.createCell(index++).setCellValue(candidate.getExamStartDateTime());
	        }
			
		}
		catch(Exception e)
		{
			logger.error("Error in Excel is:"+e.getMessage());
		}
		}
		
	}
