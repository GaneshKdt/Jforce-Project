package com.nmims.views;

import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
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


public class QuestionPaperCountReportExcelView  extends AbstractXlsxStreamingView  implements ApplicationContextAware{
	
	@Override
	protected void buildExcelDocument(Map model, Workbook workbook,	HttpServletRequest request, HttpServletResponse response)
		throws Exception {
 
		final int BUFFER = 10;
		List<ExamBookingTransactionBean> examBookingList = (List<ExamBookingTransactionBean>) model.get("examBookingList");
		//create a wordsheet
		Sheet sheet = workbook.createSheet("Paper Count");
		int index = 0;
		Row header = sheet.createRow(0);
		header.createCell(index++).setCellValue("Sr. No.");
		header.createCell(index++).setCellValue("Year");
	    header.createCell(index++).setCellValue("Month");
	    header.createCell(index++).setCellValue("Subject");
		header.createCell(index++).setCellValue("Exam Date");
		header.createCell(index++).setCellValue("Exam Time");
		header.createCell(index++).setCellValue("Center Name");
		header.createCell(index++).setCellValue("City");
		header.createCell(index++).setCellValue("Center Address");
		header.createCell(index++).setCellValue("No. of Bookings");
		header.createCell(index++).setCellValue("Buffer");
		header.createCell(index++).setCellValue("Total Question Papers Needed");
		header.createCell(index++).setCellValue("# of Bundles of 30 question papers");
		header.createCell(index++).setCellValue("# of Bundles of 20 question papers");
	    
 
		int rowNum = 1;
		for (int i = 0 ; i < examBookingList.size(); i++) {
			index = 0;
			//create the row data
			Row row = sheet.createRow(rowNum++);
			ExamBookingTransactionBean bean = examBookingList.get(i);
			row.createCell(index++).setCellValue((i+1));
			row.createCell(index++).setCellValue(new Double(bean.getYear()));
			row.createCell(index++).setCellValue(bean.getMonth());
			row.createCell(index++).setCellValue(bean.getSubject());
			row.createCell(index++).setCellValue(bean.getExamDate());
			row.createCell(index++).setCellValue(bean.getExamTime());
			row.createCell(index++).setCellValue(bean.getExamCenterName());
			row.createCell(index++).setCellValue(bean.getCity());
			row.createCell(index++).setCellValue(bean.getAddress());
			row.createCell(index++).setCellValue(new Double(bean.getSubjectCount()));
			row.createCell(index++).setCellValue(new Double(BUFFER));
			row.createCell(index++).setCellValue(new Double(bean.getSubjectCount()) + new Double(BUFFER));
			
			String formulaForBigBundle = "ROUNDDOWN(L"+rowNum+"/30,0)";
			String formulaForSmallBundle = "ROUNDUP((L"+rowNum+"-(M"+rowNum+"*30))/20,0)";
			
			Cell bigBundleCell = row.createCell(index++);
			Cell smallBundleCell = row.createCell(index++);
			
			bigBundleCell.setCellType(HSSFCell.CELL_TYPE_FORMULA);
			smallBundleCell.setCellType(HSSFCell.CELL_TYPE_FORMULA);
			bigBundleCell.setCellFormula(formulaForBigBundle);
			smallBundleCell.setCellFormula(formulaForSmallBundle);
			
			
        }
		
	}

}
