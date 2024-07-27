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

import com.nmims.beans.AssignmentFileBean;

public class AssignmentEvaluationLevel3EligibleExcelView  extends AbstractXlsxStreamingView  implements ApplicationContextAware{
	
	@Override
	protected void buildExcelDocument(Map model, Workbook workbook,	HttpServletRequest request, HttpServletResponse response)
		throws Exception {
 

		List<AssignmentFileBean> studentList =  (List<AssignmentFileBean>)model.get("assignmentEvaluatedList");
		
		//create a wordsheet
		Sheet sheet = workbook.createSheet("Assignment Level 3 Evaluation");
		int index = 0;
		Row header = sheet.createRow(0);
		header.createCell(index++).setCellValue("Sr. No.");
		header.createCell(index++).setCellValue("Year");
		header.createCell(index++).setCellValue("Month");
		header.createCell(index++).setCellValue("Subject");
		header.createCell(index++).setCellValue("Student ID");
		header.createCell(index++).setCellValue("Faculty 1 ID");
		header.createCell(index++).setCellValue("Faculty 1 Evaluated");
		header.createCell(index++).setCellValue("Faculty 1 Score");
		header.createCell(index++).setCellValue("Faculty 1 Remarks");
		header.createCell(index++).setCellValue("Faculty 1 Reason");
		header.createCell(index++).setCellValue("Faculty 2 ID");
		header.createCell(index++).setCellValue("Faculty 2 Evaluated");
		header.createCell(index++).setCellValue("Faculty 2 Score");
		header.createCell(index++).setCellValue("Faculty 2 Remarks");
		header.createCell(index++).setCellValue("Faculty 2 Reason");
		
		header.createCell(index++).setCellValue("Level 3 Diff");
		
		int rowNum = 1;
		for (int i = 0 ; i < studentList.size(); i++) {
			index = 0;
			AssignmentFileBean bean = studentList.get(i);
			
			/*String faculty1Name = "";
			String faculty2Name = "";
			String faculty3Name = "";
			String facultyRevalName = "";
			
			if(facultyMap.get(bean.getFacultyId()) != null){
				faculty1Name = facultyMap.get(bean.getFacultyId()).getFirstName() + facultyMap.get(bean.getFacultyId()).getLastName();
			}
			if(facultyMap.get(bean.getFaculty2()) != null){
				faculty2Name = facultyMap.get(bean.getFaculty2()).getFirstName() + facultyMap.get(bean.getFaculty2()).getLastName();
			}
			if(facultyMap.get(bean.getFaculty3()) != null){
				faculty3Name = facultyMap.get(bean.getFaculty3()).getFirstName() + facultyMap.get(bean.getFaculty3()).getLastName();
			}
			if(facultyMap.get(bean.getFacultyIdRevaluation()) != null){
				facultyRevalName = facultyMap.get(bean.getFacultyIdRevaluation()).getFirstName() + facultyMap.get(bean.getFacultyIdRevaluation()).getLastName();
			}*/
			
			//create the row data
			Row row = sheet.createRow(rowNum++);
			
			row.createCell(index++).setCellValue(rowNum-1);
			row.createCell(index++).setCellValue(new Double(bean.getYear()));
			row.createCell(index++).setCellValue(bean.getMonth());
			row.createCell(index++).setCellValue(bean.getSubject());
			row.createCell(index++).setCellValue(new Double(bean.getSapId()));
			row.createCell(index++).setCellValue(bean.getFacultyId());
			row.createCell(index++).setCellValue(bean.getEvaluated() == null ? "N":bean.getEvaluated());
			row.createCell(index++).setCellValue(bean.getScore());
			row.createCell(index++).setCellValue(bean.getRemarks());
			row.createCell(index++).setCellValue(bean.getReason());
			
			row.createCell(index++).setCellValue(bean.getFaculty2());
			row.createCell(index++).setCellValue(bean.getFaculty2Evaluated() == null ? "N":bean.getFaculty2Evaluated());
			row.createCell(index++).setCellValue(bean.getFaculty2Score());
			row.createCell(index++).setCellValue(bean.getFaculty2Remarks());
			row.createCell(index++).setCellValue(bean.getFaculty2Reason());
			
			row.createCell(index++).setCellValue(bean.getPercentDifference());
		}
		
		
	}

}
