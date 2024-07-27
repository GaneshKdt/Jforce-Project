package com.nmims.views;

import java.util.ArrayList;
import java.util.Map;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.springframework.context.ApplicationContextAware;
import org.springframework.web.servlet.view.document.AbstractXlsxStreamingView;
import com.nmims.beans.LostFocusLogExamBean;

public class LostFocusReportView extends AbstractXlsxStreamingView  implements ApplicationContextAware{

	@SuppressWarnings("rawtypes")
	@Override
	protected void buildExcelDocument(Map model, Workbook workbook,	HttpServletRequest request, HttpServletResponse response)
		throws Exception {
		
		@SuppressWarnings("unchecked")
		ArrayList<LostFocusLogExamBean> studentListForUnfairMeans = (ArrayList<LostFocusLogExamBean>) model.get("studentListForUnfairMeans");
		
		//create a worksheet
		Sheet sheet = workbook.createSheet("Lost Focus Report");
		int index = 0;
		Row header = sheet.createRow(0);
		
		header.createCell(index++).setCellValue("Sr. No.");
		header.createCell(index++).setCellValue("Test Name");
		header.createCell(index++).setCellValue("Date");
		header.createCell(index++).setCellValue("Batch");
		header.createCell(index++).setCellValue("Subject");
		header.createCell(index++).setCellValue("Instances");
		header.createCell(index++).setCellValue("Sapid");
		header.createCell(index++).setCellValue("First Name");
		header.createCell(index++).setCellValue("Last Name");
		header.createCell(index++).setCellValue("Email Id");
		header.createCell(index++).setCellValue("Mobile Number");
		header.createCell(index++).setCellValue("Duration");
		header.createCell(index++).setCellValue("IP Address");
 
		int rowNum = 1;
		
		int i = 0;
		for (LostFocusLogExamBean bean : studentListForUnfairMeans) {
			index = 0;
			i++;
			Row row = sheet.createRow(rowNum++);
			
			row.createCell(index++).setCellValue((i));
			row.createCell(index++).setCellValue(bean.getTestName());
			row.createCell(index++).setCellValue(bean.getTestStartDate());
			row.createCell(index++).setCellValue(bean.getBatchName());
			row.createCell(index++).setCellValue(bean.getSubject());
			row.createCell(index++).setCellValue(bean.getCount());
			row.createCell(index++).setCellValue(bean.getSapid());
			row.createCell(index++).setCellValue(bean.getFirstName());
			row.createCell(index++).setCellValue(bean.getLastName());
			row.createCell(index++).setCellValue(bean.getEmailId());
			row.createCell(index++).setCellValue(bean.getMobile());
			row.createCell(index++).setCellValue(bean.getTimeAway());
			row.createCell(index++).setCellValue(bean.getIpAddress());
			
		}
	}
}
