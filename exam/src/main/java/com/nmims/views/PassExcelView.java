package com.nmims.views;

import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.util.CellRangeAddress;
import org.springframework.context.ApplicationContextAware;
import org.springframework.web.servlet.view.document.AbstractExcelView;
import org.springframework.web.servlet.view.document.AbstractXlsxStreamingView;

import com.nmims.beans.ExamBookingTransactionBean;
import com.nmims.beans.ExecutiveBean;
import com.nmims.beans.PassFailExamBean;
import com.nmims.beans.StudentMarksBean;

public class PassExcelView  extends AbstractXlsxStreamingView  implements ApplicationContextAware{
	
	@Override
	protected void buildExcelDocument(Map model, Workbook workbook,	HttpServletRequest request, HttpServletResponse response)
		throws Exception {
 
		List<ExecutiveBean> listOfExamBookings = (List<ExecutiveBean>) model.get("reportList");
		//create a wordsheet
		Sheet sheet = workbook.createSheet("Report Of Executive Exam Bookings List");
		
		int index = 0;
		Row header = sheet.createRow(0);
		header.createCell(index++).setCellValue("Sr. No.");
		header.createCell(index++).setCellValue("SAP ID");
		header.createCell(index++).setCellValue("Password");
		header.createCell(index++).setCellValue("Sem");
		header.createCell(index++).setCellValue("Subject");
		header.createCell(index++).setCellValue("Year");
		header.createCell(index++).setCellValue("Month");
 
		int rowNum = 1;
		for (int i = 0 ; i < listOfExamBookings.size(); i++) {
			//create the row data
			index = 0;
			Row row = sheet.createRow(rowNum++);
			ExecutiveBean bean = listOfExamBookings.get(i);
			row.createCell(index++).setCellValue(rowNum-1);
			row.createCell(index++).setCellValue(bean.getSapid());
			row.createCell(index++).setCellValue(bean.getPassword());
			row.createCell(index++).setCellValue(bean.getSem());
			row.createCell(index++).setCellValue(bean.getSubject());
			row.createCell(index++).setCellValue(bean.getExamYear());
			row.createCell(index++).setCellValue(bean.getExamMonth());
        }
		
		sheet.autoSizeColumn(0); sheet.autoSizeColumn(1);
		sheet.autoSizeColumn(2); sheet.autoSizeColumn(3);
		sheet.autoSizeColumn(4); sheet.autoSizeColumn(5);
		
	
	}
}
