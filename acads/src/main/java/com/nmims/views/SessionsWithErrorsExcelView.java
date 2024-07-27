package com.nmims.views;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.util.CellRangeAddress;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.web.servlet.view.document.AbstractXlsxView;

import com.nmims.beans.SessionDayTimeAcadsBean;

/* This class is used to show sessions with errors while scheduling sessions in batch upload */
public class SessionsWithErrorsExcelView extends AbstractXlsxView  implements ApplicationContextAware{
	
private ApplicationContext act = null;

	@Override
	protected void buildExcelDocument(Map model, Workbook  workbook,	HttpServletRequest request, HttpServletResponse response)
			throws Exception {
		act = getApplicationContext();
		List<SessionDayTimeAcadsBean> inValidSessions = (List<SessionDayTimeAcadsBean>) model.get("inValidSessions");
		Sheet sheet = workbook.createSheet("Invalid Sessions");
		
 		int index = 0;
 		Row header = sheet.createRow(0);
		header.createCell(index++).setCellValue("Error(Column to be deleted)");
		header.createCell(index++).setCellValue("Date");
		header.createCell(index++).setCellValue("StartTime");
		header.createCell(index++).setCellValue("EndTime"); 
		header.createCell(index++).setCellValue("Subject");
		header.createCell(index++).setCellValue("Session Name");
		header.createCell(index++).setCellValue("Faculty ID");
		header.createCell(index++).setCellValue("Paralled Faculty 1 ID");
		header.createCell(index++).setCellValue("Paralled Faculty 2 ID");
		header.createCell(index++).setCellValue("Paralled Faculty 3 ID");
		header.createCell(index++).setCellValue("Corporate Name");
		
		 
		int rowNum = 1;
		for (int i = 0 ; i < inValidSessions.size(); i++) {
			SessionDayTimeAcadsBean bean = inValidSessions.get(i);
			index = 0;
			//create the row data
			Row row = sheet.createRow(rowNum++);
			
			 
			row.createCell(index++).setCellValue(bean.getErrorMessage());
			

			SimpleDateFormat sdfDate = new SimpleDateFormat("dd-MM-yyyy");
			SimpleDateFormat sdfDate2 = new SimpleDateFormat("yyyy-MM-dd");
			String date=bean.getDate();
			Date date2=sdfDate2.parse(date);
			date=sdfDate.format(date2);
			row.createCell(index++).setCellValue(date);
			
			//bring date time in proper format i.e. HH:MM:SS start
			String startTime = bean.getStartTime();
			String tempST[] = startTime.split(":",-1);
			if(tempST.length==2) {
				startTime=startTime+":00";
			}

			String endTime = bean.getEndTime();
			String tempET[] = endTime.split(":",-1);
			if(tempET.length==2) {
				endTime=endTime+":00";
			}
			//bring date and time in proper format i.e. HH:MM:SS End
			row.createCell(index++).setCellValue(startTime);
			row.createCell(index++).setCellValue(endTime);
			row.createCell(index++).setCellValue(bean.getSubject());  
			row.createCell(index++).setCellValue(bean.getSessionName());
			row.createCell(index++).setCellValue(bean.getFacultyId());  
			row.createCell(index++).setCellValue(bean.getAltFacultyId());  
			row.createCell(index++).setCellValue(bean.getAltFacultyId2());  
			row.createCell(index++).setCellValue(bean.getAltFacultyId3());
			if("".equalsIgnoreCase(bean.getCorporateName().trim())) {
				row.createCell(index++).setCellValue("Retail");    
			}else {
				row.createCell(index++).setCellValue(bean.getCorporateName());    
			}
		}
	/*	sheet.autoSizeColumn(0); sheet.autoSizeColumn(1);
		sheet.autoSizeColumn(2); sheet.autoSizeColumn(3);
		sheet.autoSizeColumn(4); sheet.autoSizeColumn(5);
		sheet.autoSizeColumn(6); sheet.autoSizeColumn(7);
		sheet.autoSizeColumn(8); sheet.autoSizeColumn(9); 
		sheet.autoSizeColumn(10);
		
		sheet.setAutoFilter(CellRangeAddress.valueOf("A1:N"+(inValidSessions.size()+1)));
	*/
		}
}
