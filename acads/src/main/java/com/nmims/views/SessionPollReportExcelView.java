package com.nmims.views;

import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.view.document.AbstractExcelView;

import com.nmims.beans.SessionPollReportBean;

@Component("sessionPollReportExcelView")
public class SessionPollReportExcelView extends AbstractExcelView  implements ApplicationContextAware
{

	
	@Override
	protected void buildExcelDocument(Map model, HSSFWorkbook workbook,	HttpServletRequest request, HttpServletResponse response)
		throws Exception {
		
		List<SessionPollReportBean> sessionPollsList = (List<SessionPollReportBean>) model.get("sessionPollList");
		 
		//create worksheet
 		int index = 0;

			
			String[] headerNames = {"Sr. No.","Acad Year","Acad Month","Date","Start Time","Subject","Subject Code","Poll Name","Faculty ID","Faculty Name","Meeting Key","Zoom ID","Track","isLaunched","Total No.of Quest"};
			
			HSSFSheet sheet = workbook.createSheet("Session Poll Report ");
			HSSFRow header = sheet.createRow(0);
			for(String headerName: headerNames) {
				header.createCell(index++).setCellValue(headerName);
				}
			
			int i = 1;
			int rowNum = 1;
			for (SessionPollReportBean bean:sessionPollsList) {
				index = 0;
				
				HSSFRow row = sheet.createRow(rowNum++);
					row.createCell(index++).setCellValue((i++));
					row.createCell(index++).setCellValue(bean.getYear());
					row.createCell(index++).setCellValue(bean.getMonth());
					row.createCell(index++).setCellValue(bean.getDate());
					row.createCell(index++).setCellValue(bean.getStartTime());
					row.createCell(index++).setCellValue(bean.getSubject());
					row.createCell(index++).setCellValue(bean.getSubjectcode());
					row.createCell(index++).setCellValue(bean.getTitle());
					row.createCell(index++).setCellValue(bean.getFacultyIdPoll());
					row.createCell(index++).setCellValue(bean.getFacultyName());
					row.createCell(index++).setCellValue(bean.getWebinarId());
					row.createCell(index++).setCellValue(bean.getHostKey());
					row.createCell(index++).setCellValue(bean.getTrack());
					row.createCell(index++).setCellValue(bean.getIsLaunched());
					row.createCell(index++).setCellValue(bean.getNoofQuest());
		}
			
		
		
	}//end of method
}
