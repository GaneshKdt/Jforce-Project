package com.nmims.views;

import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.view.document.AbstractExcelView;

import com.nmims.beans.StudentSubjectConfigExamBean;
import com.nmims.beans.TimeBoundUserMapping;


@Component
public class StudentTimeboundExcelView extends AbstractExcelView    {

	private static final Logger logger = LoggerFactory.getLogger(StudentTimeboundExcelView.class);
	private static final String String = null;

	@Override
	protected void buildExcelDocument(Map<String, Object> model, HSSFWorkbook workbook, HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		try {
			
			HSSFSheet sheet = workbook.createSheet("TimeBoundExcelReport");		
		
			List<TimeBoundUserMapping> bean=(List<TimeBoundUserMapping>)model.get("existingStudentList");					
			int index= 0;		
			HSSFRow header = sheet.createRow(0);			
			header.createCell(index++).setCellValue("Sr.No.");
			header.createCell(index++).setCellValue("Cycle");
			header.createCell(index++).setCellValue("Subject");
			header.createCell(index++).setCellValue("Batch");
			header.createCell(index++).setCellValue("sapid");
			header.createCell(index++).setCellValue("Student Type");
			int rowNum=1;			
			for (int i = 0; i <bean.size() ; i++) {
				index=0;				
				//create the row data
				HSSFRow row = sheet.createRow(rowNum++);
				//columns				
				TimeBoundUserMapping data=bean.get(i);	
				row.createCell(index++).setCellValue((i+1));			
				row.createCell(index++).setCellValue(data.getAcadMonth()+" "+data.getAcadYear());
				row.createCell(index++).setCellValue(data.getSubject());
				row.createCell(index++).setCellValue(data.getBatchName());
				row.createCell(index++).setCellValue(data.getUserId());
				row.createCell(index++).setCellValue(data.getRole().equals("Student")?"Regular":data.getRole());
			}		
			} catch (Exception e) {
				logger.error(String.valueOf(e));
			}
		
	}
	
	
}
