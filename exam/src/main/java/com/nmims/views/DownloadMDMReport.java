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
import com.nmims.beans.ProgramSubjectMappingExamBean;
import com.nmims.beans.SifyMarksBean;
import com.nmims.beans.StudentExamBean;

public class DownloadMDMReport  extends AbstractXlsxStreamingView  implements ApplicationContextAware{
	private ApplicationContext act = null;
	@Override
	protected void buildExcelDocument(Map model, Workbook workbook,	HttpServletRequest request, HttpServletResponse response)
		throws Exception {
		act = getApplicationContext();
		StudentMarksDAO dao = (StudentMarksDAO)act.getBean("studentMarksDAO");

		ArrayList<ProgramSubjectMappingExamBean>  programSubjectList= (ArrayList<ProgramSubjectMappingExamBean>)model.get("programSubjectList");
		Sheet sheet = workbook.createSheet("MDM Report");
	
		int index = 0;
		
		Row header = sheet.createRow(0);
		header.createCell(index++).setCellValue("Sr. No.");
		header.createCell(index++).setCellValue("Program");
		header.createCell(index++).setCellValue("Program Structure");
		header.createCell(index++).setCellValue("Subject");
		header.createCell(index++).setCellValue("Sem");
		header.createCell(index++).setCellValue("Active");
		header.createCell(index++).setCellValue("Pass Score");
		header.createCell(index++).setCellValue("Has Assignment");
		header.createCell(index++).setCellValue("Assignment Needed Before Written");
		header.createCell(index++).setCellValue("Assignment Score Model");
		header.createCell(index++).setCellValue("Written Score Model");
		header.createCell(index++).setCellValue("Subject Code");
		header.createCell(index++).setCellValue("Create Case for Query");
		header.createCell(index++).setCellValue("Assign Query to Faculty");
		header.createCell(index++).setCellValue("Is Grace Appilcable");
		header.createCell(index++).setCellValue("Max Grace");
		
		int rowNum = 1;
		
		for(int j =0;j<programSubjectList.size();j++){
			index = 0;
			
			Row row = sheet.createRow(rowNum++);
		
			
			row.createCell(index++).setCellValue(j+1);
			row.createCell(index++).setCellValue(programSubjectList.get(j).getProgram());
			row.createCell(index++).setCellValue(programSubjectList.get(j).getPrgmStructApplicable());
			row.createCell(index++).setCellValue(programSubjectList.get(j).getSubject());
			row.createCell(index++).setCellValue(programSubjectList.get(j).getSem());
			row.createCell(index++).setCellValue(programSubjectList.get(j).getActive());
			row.createCell(index++).setCellValue(programSubjectList.get(j).getPassScore());
			row.createCell(index++).setCellValue(programSubjectList.get(j).getHasAssignment());
			row.createCell(index++).setCellValue(programSubjectList.get(j).getAssignmentNeededBeforeWritten());
			row.createCell(index++).setCellValue(programSubjectList.get(j).getAssignmentScoreModel());
			row.createCell(index++).setCellValue(programSubjectList.get(j).getWrittenScoreModel());
			row.createCell(index++).setCellValue(programSubjectList.get(j).getSifySubjectCode());
			row.createCell(index++).setCellValue(programSubjectList.get(j).getCreateCaseForQuery());
			row.createCell(index++).setCellValue(programSubjectList.get(j).getAssignQueryToFaculty());
			row.createCell(index++).setCellValue(programSubjectList.get(j).getIsGraceApplicable());
			row.createCell(index++).setCellValue(programSubjectList.get(j).getMaxGraceMarks());
			
		
			
		}
		
	
	}

}
