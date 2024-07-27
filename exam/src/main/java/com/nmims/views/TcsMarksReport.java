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

public class TcsMarksReport  extends AbstractXlsxStreamingView  implements ApplicationContextAware{
	private ApplicationContext act = null;
	@Override
	protected void buildExcelDocument(Map model, Workbook workbook,	HttpServletRequest request, HttpServletResponse response)
		throws Exception {
		act = getApplicationContext();

		List<TCSMarksBean> tcsMarksList = (List<TCSMarksBean>)model.get("tcsMarksListDetails");
		Sheet sheet = workbook.createSheet("TCS Marks Report");
	
		int index = 0;
		
		Row header = sheet.createRow(0);
		header.createCell(index++).setCellValue("Sr. No.");
		header.createCell(index++).setCellValue("Name");
		header.createCell(index++).setCellValue("Year");
		header.createCell(index++).setCellValue("Month");
		header.createCell(index++).setCellValue("ExamCenterCode");
		header.createCell(index++).setCellValue("StudentType");
		header.createCell(index++).setCellValue("Subject");
		header.createCell(index++).setCellValue("Subject ID");
		header.createCell(index++).setCellValue("SectionOneMarks");
		header.createCell(index++).setCellValue("SectionTwoMarks");
		header.createCell(index++).setCellValue("SectionThreeMarks");
		header.createCell(index++).setCellValue("SectionFourMarks");
		header.createCell(index++).setCellValue("TotalScore");
		
		int rowNum = 1;
		
		for(int j =0;j<tcsMarksList.size();j++){
			index = 0;
			
			Row row = sheet.createRow(rowNum++);
		
			
			row.createCell(index++).setCellValue(j+1);
			row.createCell(index++).setCellValue(tcsMarksList.get(j).getName());
			row.createCell(index++).setCellValue(tcsMarksList.get(j).getYear());
			row.createCell(index++).setCellValue(tcsMarksList.get(j).getMonth());
			row.createCell(index++).setCellValue(tcsMarksList.get(j).getCenterCode());
			row.createCell(index++).setCellValue(tcsMarksList.get(j).getStudentType());
			row.createCell(index++).setCellValue(tcsMarksList.get(j).getSubject());
			row.createCell(index++).setCellValue(tcsMarksList.get(j).getSubjectId());
			row.createCell(index++).setCellValue(tcsMarksList.get(j).getSectionOneMarks());
			row.createCell(index++).setCellValue(tcsMarksList.get(j).getSectionTwoMarks());
			row.createCell(index++).setCellValue(tcsMarksList.get(j).getSectionThreeMarks());
			row.createCell(index++).setCellValue(tcsMarksList.get(j).getSectionFourMarks());
			row.createCell(index++).setCellValue(tcsMarksList.get(j).getTotalScore());
			
		}
		
	
	}

}
