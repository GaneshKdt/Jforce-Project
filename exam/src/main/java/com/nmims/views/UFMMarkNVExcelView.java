package com.nmims.views;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.springframework.context.ApplicationContextAware;
import org.springframework.web.servlet.view.document.AbstractXlsxStreamingView;

import com.nmims.beans.StudentMarksBean;

public class UFMMarkNVExcelView extends AbstractXlsxStreamingView  implements ApplicationContextAware{

	@Override
	protected void buildExcelDocument(Map<String, Object> model, Workbook workbook, HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		
		List<StudentMarksBean> list = (ArrayList<StudentMarksBean>) model.get("ufmMarkNVRecords");
		//create a wordsheet :- START
		Sheet sheet = workbook.createSheet("UFM Mark NV Records");
		
		int index = 0;
		Row header = sheet.createRow(0);
		header.createCell(index++).setCellValue("Sapid");
		header.createCell(index++).setCellValue("Subject");
		header.createCell(index++).setCellValue("Year");
		header.createCell(index++).setCellValue("Month");

		int rowNum = 1;
		for (int i = 0 ; i < list.size(); i++) {
			//create the row data
			index = 0;
			Row row = sheet.createRow(rowNum++);
			StudentMarksBean bean = list.get(i);
			
			row.createCell(index++).setCellValue(bean.getSapid());
			row.createCell(index++).setCellValue(bean.getSubject());
			row.createCell(index++).setCellValue(bean.getYear());
			row.createCell(index++).setCellValue(bean.getMonth());
			
        }
		
		sheet.autoSizeColumn(0); sheet.autoSizeColumn(1);
		sheet.autoSizeColumn(2); sheet.autoSizeColumn(3);
	
	}

}
