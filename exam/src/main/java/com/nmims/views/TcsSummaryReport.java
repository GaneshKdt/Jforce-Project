package com.nmims.views;
import java.util.ArrayList;
import java.util.HashMap;
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
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.web.servlet.view.document.AbstractExcelView;
import org.springframework.web.servlet.view.document.AbstractXlsxStreamingView;

import com.nmims.daos.StudentMarksDAO;
import com.nmims.beans.CenterExamBean;
import com.nmims.beans.PassFailExamBean;
import com.nmims.beans.SifyMarksBean;
import com.nmims.beans.StudentExamBean;
import com.nmims.beans.TCSMarksBean;

public class TcsSummaryReport  extends AbstractXlsxStreamingView  implements ApplicationContextAware{
	private ApplicationContext act = null;
	@Override
	protected void buildExcelDocument(Map model, Workbook workbook,	HttpServletRequest request, HttpServletResponse response)
		throws Exception {
		act = getApplicationContext();
		StudentMarksDAO dao = (StudentMarksDAO)act.getBean("studentMarksDAO");

		List<TCSMarksBean> tcsMarksList = (List<TCSMarksBean>)model.get("tcsMarksList");
		Sheet sheet = workbook.createSheet("TCS Summary Report");
	
		int index = 0;
		
		Row header = sheet.createRow(0);
		header.createCell(index++).setCellValue("Sr. No.");
		header.createCell(index++).setCellValue("Year");
		header.createCell(index++).setCellValue("Month");
		header.createCell(index++).setCellValue("StudentType");
		header.createCell(index++).setCellValue("Subject");
		header.createCell(index++).setCellValue("Subject ID");
		header.createCell(index++).setCellValue("Total Count of Records");
		
		
		int rowNum = 1;
		
		for(int j =0;j<tcsMarksList.size();j++){
			index = 0;
			
			Row row = sheet.createRow(rowNum++);
		
			
			row.createCell(index++).setCellValue(j+1);
			row.createCell(index++).setCellValue(tcsMarksList.get(j).getYear());
			row.createCell(index++).setCellValue(tcsMarksList.get(j).getMonth());
			row.createCell(index++).setCellValue(tcsMarksList.get(j).getStudentType());
			row.createCell(index++).setCellValue(tcsMarksList.get(j).getSubject());
			row.createCell(index++).setCellValue(tcsMarksList.get(j).getSubjectId());
			row.createCell(index++).setCellValue(tcsMarksList.get(j).getSubjectCount());
		
			
		}
		
	
	}

}
