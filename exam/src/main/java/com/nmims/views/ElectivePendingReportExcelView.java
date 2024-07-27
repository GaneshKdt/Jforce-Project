package com.nmims.views;

import java.util.ArrayList;
import java.util.HashMap;
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

import com.nmims.beans.Specialisation;

public class ElectivePendingReportExcelView extends AbstractXlsxStreamingView  implements ApplicationContextAware{

	private static ApplicationContext act = null;

	@Override
	protected void buildExcelDocument(Map model, Workbook workbook, HttpServletRequest request, HttpServletResponse response) throws Exception {

		act = getApplicationContext();
		
		int index = 0;

		List<Specialisation> electivePendingReport = new ArrayList<>();
		electivePendingReport = (List<Specialisation>)model.get("electivePendingReport");
		HashMap<String, String> specializationTypesMap = (HashMap<String, String>) request.getSession().getAttribute("getSpecializationTypesMap");
		
		//create a wordsheet
		
		Sheet sheet = workbook.createSheet("Students");
		 
		Row header = sheet.createRow(0);
		header.createCell(index++).setCellValue("Sr. No.");
		header.createCell(index++).setCellValue("Sapid");
		header.createCell(index++).setCellValue("Name");
		header.createCell(index++).setCellValue("Email");
		header.createCell(index++).setCellValue("Mobile No");
		header.createCell(index++).setCellValue("Student's Specialisation");
		header.createCell(index++).setCellValue("Specialisation Track-1");
		header.createCell(index++).setCellValue("Specialisation Track-2");
		
		int rowNum = 1;
		for (int i = 0; i < electivePendingReport.size(); i++) {
			index = 0;
			//create the row data
			Row row = sheet.createRow(rowNum++);
			Specialisation bean = electivePendingReport.get(i);
			
			row.createCell(index++).setCellValue(rowNum-1);
			row.createCell(index++).setCellValue(bean.getSapid());
			row.createCell(index++).setCellValue(bean.getName());
			row.createCell(index++).setCellValue(bean.getEmailId());
			row.createCell(index++).setCellValue(bean.getMobile());
			row.createCell(index++).setCellValue(bean.getSpecializationType());
			row.createCell(index++).setCellValue(specializationTypesMap.get(bean.getSpecialisation1()));
			row.createCell(index++).setCellValue(specializationTypesMap.get(bean.getSpecialisation2()));
			
			sheet.autoSizeColumn(0); sheet.autoSizeColumn(1);
			sheet.autoSizeColumn(2); sheet.autoSizeColumn(3);
			sheet.autoSizeColumn(4); sheet.autoSizeColumn(5);
			sheet.autoSizeColumn(6);
			
		}
	}

}
