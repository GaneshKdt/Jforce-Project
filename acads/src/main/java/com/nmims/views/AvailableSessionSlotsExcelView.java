package com.nmims.views;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.CreationHelper;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.web.servlet.view.document.AbstractXlsxView;

import com.nmims.beans.SessionDayTimeAcadsBean;

public class AvailableSessionSlotsExcelView extends AbstractXlsxView  implements ApplicationContextAware{

	private ApplicationContext act = null;
	
	@Override
	protected void buildExcelDocument(Map model, Workbook workbook, HttpServletRequest request, HttpServletResponse response) throws Exception {
		
		act = getApplicationContext();
		ArrayList<SessionDayTimeAcadsBean> availableSessionsList = (ArrayList<SessionDayTimeAcadsBean>) model.get("availableSessionsList");
		Sheet sheet = workbook.createSheet("Available Sessions Slots");
		CreationHelper creationHelper = workbook.getCreationHelper();
		
		int index = 0;
 		Row header = sheet.createRow(0);
 		
 		header.createCell(index++).setCellValue("Date");
		header.createCell(index++).setCellValue("StartTime");
		header.createCell(index++).setCellValue("EndTime");
		header.createCell(index++).setCellValue("Subject");
		header.createCell(index++).setCellValue("Subject Code");
		header.createCell(index++).setCellValue("Session Name");
		header.createCell(index++).setCellValue("Faculty ID");
		header.createCell(index++).setCellValue("Faculty Location");
		header.createCell(index++).setCellValue("Parallel 1 Faculty ID");
		header.createCell(index++).setCellValue("Parallel 1 Location");
		header.createCell(index++).setCellValue("Parallel 2 Faculty ID");
		header.createCell(index++).setCellValue("Parallel 2 Location");
		header.createCell(index++).setCellValue("Parallel 3 Faculty ID");
		header.createCell(index++).setCellValue("Parallel 3 Location");
		header.createCell(index++).setCellValue("Corporate Name");
		header.createCell(index++).setCellValue("Track");
		header.createCell(index++).setCellValue("Session Type");
		
		int rowNum = 1;
		for (SessionDayTimeAcadsBean bean : availableSessionsList) {
			index = 0;
			//create the row data
			Row row = sheet.createRow(rowNum++);
			
			SimpleDateFormat sdfDate = new SimpleDateFormat("dd-MM-yyyy");
			SimpleDateFormat sdfDate2 = new SimpleDateFormat("yyyy-MM-dd");
			String date = bean.getDate();
			Date date2 = sdfDate2.parse(date);
			date=sdfDate.format(date2);
			
			Cell cell = row.createCell(index++);
			cell.setAsActiveCell();
			cell.setCellValue(sdfDate.format(date2));
			
			CellStyle style1 = workbook.createCellStyle();
			style1.setDataFormat(creationHelper.createDataFormat().getFormat("dd-MM-yyyy"));
			cell.setCellStyle(style1);				
			
			//bring date time in proper format i.e. HH:MM:SS start
			String startTime = bean.getStartTime();
			String tempST[] = startTime.split(":",-1);
			if(tempST.length==2) {
				startTime=startTime+":00";
			}
			
			Cell cell2 = row.createCell(index++);
			cell2.setCellValue(startTime);
			CellStyle style2 = workbook.createCellStyle();
			style2.setDataFormat(creationHelper.createDataFormat().getFormat("hh:mm:ss"));
			cell2.setCellStyle(style2);
			cell2.setAsActiveCell();

			String endTime = bean.getEndTime();
			String tempET[] = endTime.split(":",-1);
			if(tempET.length==2) {
				endTime=endTime+":00";
			}
			
			Cell cell3 = row.createCell(index++);
			cell3.setCellValue(endTime);
			CellStyle style3 = workbook.createCellStyle();
			style3.setDataFormat(creationHelper.createDataFormat().getFormat("hh:mm:ss")); 
			cell3.setCellStyle(style3);
			cell3.setAsActiveCell();
			
			//bring date and time in proper format i.e. HH:MM:SS End
			
			row.createCell(index++).setCellValue(bean.getSubject());
			row.createCell(index++).setCellValue(bean.getSubjectCode());
			row.createCell(index++).setCellValue(bean.getSessionName());
			row.createCell(index++).setCellValue(bean.getFacultyId());
			row.createCell(index++).setCellValue(bean.getFacultyLocation());
			row.createCell(index++).setCellValue(bean.getAltFacultyId());
			row.createCell(index++).setCellValue(bean.getAltFacultyLocation());
			row.createCell(index++).setCellValue(bean.getAltFacultyId2());
			row.createCell(index++).setCellValue(bean.getAltFaculty2Location());
			row.createCell(index++).setCellValue(bean.getAltFacultyId3());
			row.createCell(index++).setCellValue(bean.getAltFaculty3Location());
			if("".equalsIgnoreCase(bean.getCorporateName().trim())) {
				row.createCell(index++).setCellValue("Retail");    
			}else {
				row.createCell(index++).setCellValue(bean.getCorporateName());    
			}
			row.createCell(index++).setCellValue(bean.getTrack());
			
			//Replace Session Type to Plain text
			bean.setSessionType(bean.getSessionType().replace("1", "Webinar"));
			bean.setSessionType(bean.getSessionType().replace("2", "Meeting"));
			row.createCell(index++).setCellValue(bean.getSessionType());
		}

			sheet.autoSizeColumn(0); sheet.autoSizeColumn(1);
			sheet.autoSizeColumn(2); sheet.autoSizeColumn(3);
			sheet.autoSizeColumn(4); sheet.autoSizeColumn(5);
			sheet.autoSizeColumn(6); sheet.autoSizeColumn(7);
			sheet.autoSizeColumn(8); sheet.autoSizeColumn(9);
		
	}
}
