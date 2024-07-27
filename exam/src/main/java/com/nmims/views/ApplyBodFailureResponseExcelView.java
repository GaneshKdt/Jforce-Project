package com.nmims.views;

import java.util.ArrayList;
import java.util.List;
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

import com.nmims.beans.MettlPGResponseBean;
import com.nmims.beans.MettlStudentTestInfo;

public class ApplyBodFailureResponseExcelView extends AbstractXlsxStreamingView  implements ApplicationContextAware{

	public static final Logger applybodPG = LoggerFactory.getLogger("applybod-PG");
	
	@Override
	protected void buildExcelDocument(Map<String, Object> model, Workbook workbook, HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		try {
			
		ArrayList<String> list = (ArrayList<String>) model.get("applyBodErrorList");
		//create a wordsheet :- START
		Sheet sheet = workbook.createSheet("Apply Bod Process Failure Records");
		
		int index = 0;
		Row header = sheet.createRow(0);
		header.createCell(index++).setCellValue("Question Id");

		int rowNum = 1;
		for (int i = 0 ; i < list.size(); i++) {
			//create the row data
			index = 0;
			Row row = sheet.createRow(rowNum++);
			String questionId = list.get(i);
	
			row.createCell(index++).setCellValue(questionId);
        }
		
	}
	catch(Exception e)
	{
		applybodPG.info("Exception is:"+e.getMessage());
		//e.printStackTrace();
	}
}
}
