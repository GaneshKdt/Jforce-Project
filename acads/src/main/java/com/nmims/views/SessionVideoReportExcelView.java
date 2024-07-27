package com.nmims.views;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.web.servlet.view.document.AbstractXlsxStreamingView;

import com.nmims.beans.VideoContentAcadsBean;

public class SessionVideoReportExcelView extends AbstractXlsxStreamingView implements ApplicationContextAware{

	private static ApplicationContext act = null;

	@Override
	protected void buildExcelDocument(Map model, Workbook workbook, HttpServletRequest request, HttpServletResponse response) throws Exception {
		
		act = getApplicationContext();
		int index = 0;

		try {
			List<VideoContentAcadsBean> sessionRecordingList = Collections.synchronizedList(new ArrayList());
			sessionRecordingList = (List<VideoContentAcadsBean>) model.get("sessionRecordingList");

			//create a wordsheet
			Sheet sheet = workbook.createSheet("Video Session");
			
			Row header = sheet.createRow(0);
			header.createCell(index++).setCellValue("Sr. No.");
			header.createCell(index++).setCellValue("sessionId");
			header.createCell(index++).setCellValue("sessionDate");
			header.createCell(index++).setCellValue("subject");
			header.createCell(index++).setCellValue("facultyId");
			header.createCell(index++).setCellValue("Duration HH:MM:SS");
			header.createCell(index++).setCellValue("Minutes MM:SS");
			header.createCell(index++).setCellValue("description");
			header.createCell(index++).setCellValue("fileName");
			header.createCell(index++).setCellValue("videoLink");
			header.createCell(index++).setCellValue("mobileUrlHd");
			header.createCell(index++).setCellValue("mobileUrlSd1");
			header.createCell(index++).setCellValue("mobileUrlSd2");

			header.createCell(index++).setCellValue("Session Name");
			header.createCell(index++).setCellValue("Track Name");
			header.createCell(index++).setCellValue("Faculty Name");
			header.createCell(index++).setCellValue("Timebound");
			header.createCell(index++).setCellValue("Timestamp");
			header.createCell(index++).setCellValue("Program Group 1");
			header.createCell(index++).setCellValue("Sem Group 1");
			header.createCell(index++).setCellValue("Program Group 2");
			header.createCell(index++).setCellValue("Sem Group 2");
			header.createCell(index++).setCellValue("Program Group 3");
			header.createCell(index++).setCellValue("Sem Group 3");
			header.createCell(index++).setCellValue("Program Group 4");
			header.createCell(index++).setCellValue("Sem Group 4");
			header.createCell(index++).setCellValue("Program Group 5");
			header.createCell(index++).setCellValue("Sem Group 5");
			header.createCell(index++).setCellValue("Program Group 6");
			header.createCell(index++).setCellValue("Sem Group 6");
			 
			int rowNum = 1;
			for (int i = 0; i < sessionRecordingList.size(); i++) {
				Integer seconds = null; 
				Integer sec  =  null; 
		        Integer hours =  null; 
		        Integer min =  null;
		        Integer minutes = null;
				index = 0;
				//create the row data
				Row row = sheet.createRow(rowNum++);
				VideoContentAcadsBean bean = sessionRecordingList.get(i);
				if(bean.getDuration() != null) {
					seconds = Integer.parseInt( bean.getDuration()); 
			        sec  = seconds % 60;
			        hours = seconds / 60;
			        min = hours % 60;
			        hours = hours / 60;
			        minutes = seconds / 60;
				}
			
				row.createCell(index++).setCellValue(rowNum-1);
				row.createCell(index++).setCellValue(bean.getSessionId());
				row.createCell(index++).setCellValue(bean.getSessionDate());
				row.createCell(index++).setCellValue(bean.getSubject().replace("_SesssionPlan_Video", ""));
				row.createCell(index++).setCellValue(bean.getFacultyId());
				row.createCell(index++).setCellValue(hours + ":" + min + ":" + sec);
				row.createCell(index++).setCellValue(minutes+":"+sec); 
				row.createCell(index++).setCellValue(bean.getDescription());
				row.createCell(index++).setCellValue(bean.getFileName());
				row.createCell(index++).setCellValue(bean.getVideoLink());
				row.createCell(index++).setCellValue(bean.getMobileUrlHd());
				row.createCell(index++).setCellValue(bean.getMobileUrlSd1());
				row.createCell(index++).setCellValue(bean.getMobileUrlSd2());
				row.createCell(index++).setCellValue(bean.getSessionName());
				row.createCell(index++).setCellValue(bean.getTrack());
				row.createCell(index++).setCellValue(bean.getFacultyName());
				row.createCell(index++).setCellValue(bean.getTimeBound());
				row.createCell(index++).setCellValue(bean.getLastModifiedDate().toString());
				row.createCell(index++).setCellValue(bean.getGroup1Program());
				row.createCell(index++).setCellValue(bean.getGroup1Sem());
				row.createCell(index++).setCellValue(bean.getGroup2Program());
				row.createCell(index++).setCellValue(bean.getGroup2Sem());
				row.createCell(index++).setCellValue(bean.getGroup3Program());
				row.createCell(index++).setCellValue(bean.getGroup3Sem());
				row.createCell(index++).setCellValue(bean.getGroup4Program());
				row.createCell(index++).setCellValue(bean.getGroup4Sem());
				row.createCell(index++).setCellValue(bean.getGroup5Program());
				row.createCell(index++).setCellValue(bean.getGroup5Sem());
				row.createCell(index++).setCellValue(bean.getGroup6Program());
				row.createCell(index++).setCellValue(bean.getGroup6Sem());	
			}
		} catch (RuntimeException e) {
			throw new RuntimeException("Error Generating Excel!");
		}					
	}
}
