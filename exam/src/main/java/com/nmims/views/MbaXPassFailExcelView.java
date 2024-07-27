package com.nmims.views;

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
import com.nmims.beans.EmbaPassFailBean;
import com.nmims.beans.ExecutiveExamOrderBean;
import com.nmims.beans.StudentExamBean;
import com.nmims.daos.StudentMarksDAO;

public class MbaXPassFailExcelView  extends AbstractXlsxStreamingView  implements ApplicationContextAware {
	
	private static ApplicationContext act = null;
	
	@Override
	protected void buildExcelDocument(Map model, Workbook workbook,	HttpServletRequest request, HttpServletResponse response)
		throws Exception {
		
		List<EmbaPassFailBean> updatedMbaXPassFailData = (List<EmbaPassFailBean>) model.get("updatedMbaXPassFailData");
		act = getApplicationContext();
		StudentMarksDAO dao = (StudentMarksDAO)act.getBean("studentMarksDAO");
		HashMap<String, StudentExamBean> sapIdStudentsMap = dao.getAllStudents();
		
		//create a wordsheet :- START
		Sheet sheet = workbook.createSheet("Report Of MBA - X PassFailExcelView Reports");
		
		int index = 0;
		Row header = sheet.createRow(0);		
		header.createCell(index++).setCellValue("Sr. No.");
		header.createCell(index++).setCellValue("Batch Id.");
		header.createCell(index++).setCellValue("SAP Id");
		header.createCell(index++).setCellValue("First Name");
		header.createCell(index++).setCellValue("Last Name");
		header.createCell(index++).setCellValue("Program");
	    header.createCell(index++).setCellValue("Subject");
	  
	    header.createCell(index++).setCellValue("IA Score");
	    header.createCell(index++).setCellValue("TEE Score");
	    header.createCell(index++).setCellValue("Grace Marks");
	    header.createCell(index++).setCellValue("Total");
	    header.createCell(index++).setCellValue("Grade");
	    header.createCell(index++).setCellValue("Points");
	    header.createCell(index++).setCellValue("Is Pass");
	    header.createCell(index++).setCellValue("Fail Reason");
	    header.createCell(index++).setCellValue("Is Result Live");
	    header.createCell(index++).setCellValue("Status");
 
		
		
		
 
		int rowNum = 1;
		for (int i = 0 ; i < updatedMbaXPassFailData.size(); i++) {
			//create the row data
			index = 0;
			Row row = sheet.createRow(rowNum++);
			EmbaPassFailBean bean = updatedMbaXPassFailData.get(i);
			StudentExamBean student = sapIdStudentsMap.get(bean.getSapid());
			
			row.createCell(index++).setCellValue((i+1));
			row.createCell(index++).setCellValue(bean.getBatch_id());
			row.createCell(index++).setCellValue(new Double(bean.getSapid()));
			row.createCell(index++).setCellValue(student.getFirstName());
			row.createCell(index++).setCellValue(student.getLastName());
			row.createCell(index++).setCellValue(student.getProgram());
			row.createCell(index++).setCellValue(bean.getSubject());
			row.createCell(index++).setCellValue(bean.getIaScore());
			row.createCell(index++).setCellValue(bean.getTeeScore());
			row.createCell(index++).setCellValue(bean.getGraceMarks());
			row.createCell(index++).setCellValue(bean.getTotal());
			row.createCell(index++).setCellValue(bean.getGrade());
			row.createCell(index++).setCellValue(bean.getPoints());			
			row.createCell(index++).setCellValue(bean.getIsPass());
			row.createCell(index++).setCellValue(bean.getFailReason());
			row.createCell(index++).setCellValue(bean.getIsResultLive());
			row.createCell(index++).setCellValue(bean.getStatus());			
			



		
        }
		
	
	}
}
