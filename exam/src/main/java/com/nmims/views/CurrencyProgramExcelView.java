package com.nmims.views;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.util.CellRangeAddress;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.view.document.AbstractExcelView;
import org.springframework.web.servlet.view.document.AbstractXlsxStreamingView;

import com.nmims.beans.CurrencyMappingBean;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.web.servlet.view.document.AbstractXlsxStreamingView;

public class CurrencyProgramExcelView extends AbstractXlsxStreamingView  implements ApplicationContextAware{

	private static ApplicationContext act=null;

	@Override
	protected void buildExcelDocument(Map<String, Object> model, Workbook workbook, HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		act=getApplicationContext();
		ArrayList<CurrencyMappingBean> currencyProgramList= (ArrayList<CurrencyMappingBean>) model.get("currencyDetailsList");
		Sheet sheet=workbook.createSheet("currencyProgramExcel");
		int index=0;
		Row header=sheet.createRow(0);
		header.createCell(index++).setCellValue("Sr. No.");
		header.createCell(index++).setCellValue("Fee Type");
		header.createCell(index++).setCellValue("Consumer Type");
		header.createCell(index++).setCellValue("Program");
		header.createCell(index++).setCellValue("ProgramStructure");
		header.createCell(index++).setCellValue("Currency");
		header.createCell(index++).setCellValue("Price");
		
		int rowNum=1;
		for(int i=0;i<currencyProgramList.size();i++) {
			index=0;
			Row row=sheet.createRow(rowNum++);
			CurrencyMappingBean currencyMap=currencyProgramList.get(i);
			row.createCell(index++).setCellValue(i+1);
			row.createCell(index++).setCellValue(currencyMap.getFeeName());
			row.createCell(index++).setCellValue(currencyMap.getConsumerType());
			row.createCell(index++).setCellValue(currencyMap.getProgram());
			row.createCell(index++).setCellValue(currencyMap.getProgramStructure());
			row.createCell(index++).setCellValue(currencyMap.getCurrencyName());
			row.createCell(index++).setCellValue(currencyMap.getPrice());
			
			sheet.autoSizeColumn(0); sheet.autoSizeColumn(1);
			sheet.autoSizeColumn(2); sheet.autoSizeColumn(3);
			sheet.autoSizeColumn(4); sheet.autoSizeColumn(5);
			sheet.autoSizeColumn(6); sheet.autoSizeColumn(7);
		
		
	}
	}
	

	

}