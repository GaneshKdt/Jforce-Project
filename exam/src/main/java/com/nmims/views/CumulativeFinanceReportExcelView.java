package com.nmims.views;

import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.hssf.usermodel.HSSFDataFormat;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.util.CellRangeAddress;
import org.springframework.context.ApplicationContextAware;
import org.springframework.web.servlet.view.document.AbstractExcelView;
import org.springframework.web.servlet.view.document.AbstractXlsxStreamingView;

import com.nmims.beans.ExamBookingTransactionBean;
import com.nmims.beans.ExamCenterBean;
import com.nmims.beans.ExamCenterSlotMappingBean;


public class CumulativeFinanceReportExcelView  extends AbstractXlsxStreamingView  implements ApplicationContextAware{
	
	@Override
	protected void buildExcelDocument(Map model, Workbook workbook,	HttpServletRequest request, HttpServletResponse response)
		throws Exception {
 
		Map<String, Double> sortedDateAmountMap = (Map<String, Double>) model.get("sortedDateAmountMap");
		//create a wordsheet
		Sheet sheet = workbook.createSheet("Finance Report");
		int index = 0;
		Row header = sheet.createRow(0);
		header.createCell(index++).setCellValue("Sr. No.");
		header.createCell(index++).setCellValue("Date");
		header.createCell(index++).setCellValue("Amount");
	    
 
		int rowNum = 1;
		CellStyle styleCurrencyFormat = null;
		styleCurrencyFormat = workbook.createCellStyle();
	    styleCurrencyFormat.setDataFormat(HSSFDataFormat.getBuiltinFormat("$#,##0.00"));
		for (Iterator i = sortedDateAmountMap.keySet().iterator(); i.hasNext(); ) {

			String date = (String)i.next();
			double amount = (Double)sortedDateAmountMap.get(date);
			
			index = 0;
			//create the row data
			Row row = sheet.createRow(rowNum++);
			
			row.createCell(index++).setCellValue((rowNum-1));
			row.createCell(index++).setCellValue(date);
			Cell amountCell = row.createCell(index++);
			amountCell.setCellValue(new Double(amount));
			amountCell.setCellStyle(styleCurrencyFormat);

		}
		
		
	}

}
