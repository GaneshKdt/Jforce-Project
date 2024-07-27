package com.nmims.views;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.util.CellRangeAddress;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.view.document.AbstractExcelView;

import com.nmims.beans.ProgramsBean;

@Component("exitSrProgramMappedExcelView")
public class ExitSrProgramMappedExcelView extends AbstractExcelView implements ApplicationContextAware {

	@Override
	protected void buildExcelDocument(Map<String, Object> model, HSSFWorkbook workbook, HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		
		List<ProgramsBean> exitSrProgramMappedList = (List<ProgramsBean>) model.get("exitSrProgramMappedList");
		HSSFSheet sheet = workbook.createSheet("Program Mapped");
		int index = 0;
		HSSFRow header = sheet.createRow(0);
		header.createCell(index++).setCellValue("Sr No.");
		header.createCell(index++).setCellValue("Consumer Type");
		header.createCell(index++).setCellValue("Program Structure");
		header.createCell(index++).setCellValue("Program");
		
		
		header.createCell(index++).setCellValue("New Program Mapped");
		header.createCell(index++).setCellValue("No.SemToBeClear");
		header.createCell(index++).setCellValue("Created By");
		header.createCell(index++).setCellValue("Created Date");
		header.createCell(index++).setCellValue("LastModified By");
		header.createCell(index++).setCellValue("LastModified Date");
		
		int rowNum = 1;
		for (int i = 0 ; i < exitSrProgramMappedList.size(); i++) {
			index = 0;
			//create the row data

         	
         	try {
			HSSFRow row = sheet.createRow(rowNum++);
			ProgramsBean bean = exitSrProgramMappedList.get(i);
			DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
         	Date d1 = dateFormat.parse(bean.getCreatedDate());
         	Date d2=dateFormat.parse(bean.getLastModifiedDate());
         	Calendar c = Calendar.getInstance();
         	c.setTime(d1);
         	c.setTime(d2);
       
        
			row.createCell(index++).setCellValue((i+1));
			row.createCell(index++).setCellValue((bean.getConsumerType()));
			row.createCell(index++).setCellValue(bean.getProgramStructure());
			row.createCell(index++).setCellValue(bean.getProgramname());
			
			row.createCell(index++).setCellValue(bean.getNewPrgm_structure_map());
			row.createCell(index++).setCellValue(bean.getSem());
			row.createCell(index++).setCellValue(bean.getCreatedDate());
			row.createCell(index++).setCellValue(bean.getCreatedBy());
			row.createCell(index++).setCellValue(bean.getLastModifiedDate());
			row.createCell(index++).setCellValue(bean.getLastModifiedBy());
			
         	} catch (Exception e) {
         		
			}
         	sheet.autoSizeColumn(0); sheet.autoSizeColumn(1);
    		sheet.autoSizeColumn(2); sheet.autoSizeColumn(3);
    		sheet.autoSizeColumn(4); sheet.autoSizeColumn(5);
    		sheet.autoSizeColumn(6); sheet.autoSizeColumn(7);
    		sheet.autoSizeColumn(8); sheet.autoSizeColumn(9);
    		sheet.autoSizeColumn(10);
    		sheet.setAutoFilter(CellRangeAddress.valueOf("A1:R"+exitSrProgramMappedList.size()));
        }
		
	}

}
