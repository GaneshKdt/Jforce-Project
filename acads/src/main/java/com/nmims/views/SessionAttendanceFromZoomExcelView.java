package com.nmims.views;

import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.util.CellRangeAddress;
import org.springframework.web.servlet.view.document.AbstractExcelView;

import com.nmims.beans.ParticipantReportBean;
import com.nmims.beans.SessionDayTimeAcadsBean;

public class SessionAttendanceFromZoomExcelView extends AbstractExcelView {

	@Override
	protected void buildExcelDocument(Map<String, Object> model, HSSFWorkbook workbook, HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		List<ParticipantReportBean> sessionAttendanceList=(List<ParticipantReportBean>) request.getSession().getAttribute("sessionAttendanceList");
		HSSFSheet sheet = workbook.createSheet("Sessions Attandance");
		
 		int index = 0;
		HSSFRow header = sheet.createRow(0);
		header.createCell(index++).setCellValue("Sr. No.");
		header.createCell(index++).setCellValue("Student Id");
	    header.createCell(index++).setCellValue("Student Name");
	    header.createCell(index++).setCellValue("Subject Name");
		header.createCell(index++).setCellValue("Semester No");
		header.createCell(index++).setCellValue("Session No");
		header.createCell(index++).setCellValue("Start Time");
		header.createCell(index++).setCellValue("End Time");
		header.createCell(index++).setCellValue("Duration in single login");
		header.createCell(index++).setCellValue("Total duration in all logins");
		
 
		int rowNum = 1;
		for (int i = 0 ; i < sessionAttendanceList.size(); i++) {
			index = 0;
			//create the row data
			
			HSSFRow row = sheet.createRow(rowNum++);
			ParticipantReportBean bean = sessionAttendanceList.get(i);
			
			row.createCell(index++).setCellValue((i+1));
			row.createCell(index++).setCellValue(bean.getSapId());
			row.createCell(index++).setCellValue(bean.getName());
			row.createCell(index++).setCellValue(bean.getSubject());
			row.createCell(index++).setCellValue(bean.getSem());
			row.createCell(index++).setCellValue(bean.getSessionName());
			row.createCell(index++).setCellValue(bean.getJoin_time());
			row.createCell(index++).setCellValue(bean.getLeave_time());
			row.createCell(index++).setCellValue(String.format("%.2f", bean.getDuration()));
			row.createCell(index++).setCellValue(String.format("%.2f", bean.getTotalDuration()));
			
        }
		sheet.autoSizeColumn(0); sheet.autoSizeColumn(1);
		sheet.autoSizeColumn(2); sheet.autoSizeColumn(3);
		sheet.autoSizeColumn(4); sheet.autoSizeColumn(5);
		sheet.autoSizeColumn(6); sheet.autoSizeColumn(7);
		sheet.autoSizeColumn(8); sheet.autoSizeColumn(9);
		
		sheet.setAutoFilter(CellRangeAddress.valueOf("A1:AU"+(sessionAttendanceList.size()+1)));
	}

}
