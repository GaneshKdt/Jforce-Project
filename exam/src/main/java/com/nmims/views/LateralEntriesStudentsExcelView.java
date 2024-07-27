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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.web.servlet.view.document.AbstractExcelView;
import org.springframework.web.servlet.view.document.AbstractXlsxStreamingView;

import com.nmims.beans.CenterExamBean;
import com.nmims.beans.ExecutiveExamOrderBean;
import com.nmims.beans.StudentExamBean;
import com.nmims.beans.StudentMarksBean;
import com.nmims.beans.StudentsTestDetailsExamBean;
import com.nmims.beans.TestExamBean;
import com.nmims.daos.StudentMarksDAO;

public class LateralEntriesStudentsExcelView  extends AbstractXlsxStreamingView  implements ApplicationContextAware {
	
	@Override
	protected void buildExcelDocument(Map model, Workbook workbook,	HttpServletRequest request, HttpServletResponse response)
		throws Exception {
		
		ArrayList<StudentExamBean> studentsList = (ArrayList<StudentExamBean>) model.get("studentsList");
		
		//create a wordsheet :- START
		Sheet sheet = workbook.createSheet("Report of Pending Subjects For Lateral Entries");
		
		int index = 0;
		Row header = sheet.createRow(0);
		
		header.createCell(index++).setCellValue("Sapid");
		header.createCell(index++).setCellValue("First Name");
		header.createCell(index++).setCellValue("Last Name");
		header.createCell(index++).setCellValue("Subject");
		header.createCell(index++).setCellValue("Sem");
		header.createCell(index++).setCellValue("Previous Student Id,");
		header.createCell(index++).setCellValue("Program");
		header.createCell(index++).setCellValue("Program Structure Applicable");
		header.createCell(index++).setCellValue("Old Program");
		header.createCell(index++).setCellValue("Previous Program Structure Applicable");


		int rowNum = 1;
		for (int i = 0 ; i < studentsList.size(); i++) {
			//create the row data
			index = 0;
			Row row = sheet.createRow(rowNum++);
			StudentExamBean bean = studentsList.get(i);
			
			row.createCell(index++).setCellValue(bean.getSapid());
			row.createCell(index++).setCellValue(bean.getFirstName());
			row.createCell(index++).setCellValue(bean.getLastName());
			row.createCell(index++).setCellValue(bean.getSubject());
			row.createCell(index++).setCellValue(bean.getSem());
			row.createCell(index++).setCellValue(bean.getPreviousStudentId());
			row.createCell(index++).setCellValue(bean.getProgram());
			row.createCell(index++).setCellValue(bean.getPrgmStructApplicable());
			row.createCell(index++).setCellValue(bean.getOldProgram());
			row.createCell(index++).setCellValue(bean.getPreviousPrgmStructApplicable());
	}
		
	
		
	
	}
}

