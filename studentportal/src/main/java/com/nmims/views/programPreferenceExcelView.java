package com.nmims.views;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.util.CellRangeAddress;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.view.document.AbstractExcelView;

import com.nmims.beans.CenterStudentPortalBean;
import com.nmims.beans.ServiceRequestStudentPortal;
import com.nmims.beans.StudentStudentPortalBean;
import com.nmims.beans.programPreference;
import com.nmims.daos.ServiceRequestDao;

@Component
public class programPreferenceExcelView extends AbstractExcelView implements ApplicationContextAware{

	private static ApplicationContext act = null;
	
	@Override
	protected void buildExcelDocument(Map model, HSSFWorkbook workbook,	HttpServletRequest request, HttpServletResponse response)
		throws Exception {
 
		act = getApplicationContext();
		List<programPreference> srList = (List<programPreference>) model.get("lstOfProcessRecords");
		//create a wordsheet
		HSSFSheet sheet = workbook.createSheet("Service Requests");
		int index = 0;
		HSSFRow header = sheet.createRow(0);
		header.createCell(index++).setCellValue("Sr. No.");
		header.createCell(index++).setCellValue("Registration No");
		header.createCell(index++).setCellValue("Student Name");
		
		header.createCell(index++).setCellValue("Preference 1");
		header.createCell(index++).setCellValue("Preference 2");
		header.createCell(index++).setCellValue("Preference 3");
		header.createCell(index++).setCellValue("Preference 4");
		header.createCell(index++).setCellValue("Preference 5");
		header.createCell(index++).setCellValue("Preference 6");
		header.createCell(index++).setCellValue("Preference 7");
		
		header.createCell(index++).setCellValue("Latest Category");
 
		int rowNum = 1;
		for (int i = 0 ; i < srList.size(); i++) {
			index = 0;
			//create the row data
			HSSFRow row = sheet.createRow(rowNum++);
			String expectedClosedDateOfSR = "";
			
			programPreference bean = srList.get(i);
			
			
			row.createCell(index++).setCellValue((i+1));
			row.createCell(index++).setCellValue(new Double(bean.getRegNo()));
			row.createCell(index++).setCellValue(bean.getStudentName());
			row.createCell(index++).setCellValue(bean.getPreference1());
			row.createCell(index++).setCellValue(bean.getPreference2());
			row.createCell(index++).setCellValue(bean.getPreference3());
			row.createCell(index++).setCellValue(bean.getPreference4());
			row.createCell(index++).setCellValue(bean.getPreference5());
			row.createCell(index++).setCellValue(bean.getPreference6());
			row.createCell(index++).setCellValue(bean.getPreference7());
			
			row.createCell(index++).setCellValue(bean.getLatestProgramCategory());
			
        }
		sheet.autoSizeColumn(0); sheet.autoSizeColumn(1);
		sheet.autoSizeColumn(2); sheet.autoSizeColumn(3);
		sheet.autoSizeColumn(4); sheet.autoSizeColumn(5);
		sheet.autoSizeColumn(6); sheet.autoSizeColumn(7);
		sheet.autoSizeColumn(8); sheet.autoSizeColumn(9);
		sheet.autoSizeColumn(10);
		sheet.setAutoFilter(CellRangeAddress.valueOf("A1:R"+srList.size()));
	}

}
