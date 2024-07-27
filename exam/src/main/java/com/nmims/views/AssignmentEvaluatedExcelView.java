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
import org.apache.poi.ss.util.CellRangeAddress;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.web.servlet.view.document.AbstractExcelView;
import org.springframework.web.servlet.view.document.AbstractXlsxStreamingView;

import com.nmims.beans.AssignmentFileBean;
import com.nmims.beans.FacultyExamBean;
import com.nmims.beans.StudentExamBean;
import com.nmims.daos.AssignmentsDAO;

public class AssignmentEvaluatedExcelView  extends AbstractXlsxStreamingView  implements ApplicationContextAware{
	
	private static ApplicationContext act = null;
	
	@Override
	protected void buildExcelDocument(Map model, Workbook workbook,	HttpServletRequest request, HttpServletResponse response)
		throws Exception {
 
		act = getApplicationContext();
		List<AssignmentFileBean> studentList =  (List<AssignmentFileBean>)model.get("assignmentEvaluatedList");
		//List<AssignmentFileBean> assignmentEvaluatedList2 = (List<AssignmentFileBean>)request.getSession().getAttribute("assignmentEvaluatedList2");
		AssignmentsDAO dao = (AssignmentsDAO)act.getBean("asignmentsDAO");
		HashMap<String,StudentExamBean> getAllStudents = dao.getAllStudents();
		//create a wordsheet
		Sheet sheet = workbook.createSheet("Assignment Submitted List");
		int index = 0;
		Row header = sheet.createRow(0);
		header.createCell(index++).setCellValue("Sr. No.");
		header.createCell(index++).setCellValue("Year");
		header.createCell(index++).setCellValue("Month");
		header.createCell(index++).setCellValue("Subject");
		header.createCell(index++).setCellValue("Student ID");
		header.createCell(index++).setCellValue("Student Name");
		header.createCell(index++).setCellValue("Email");
		header.createCell(index++).setCellValue("Mobile");
		header.createCell(index++).setCellValue("Final Score");
		header.createCell(index++).setCellValue("Program");
		header.createCell(index++).setCellValue("Faculty 1 ID");
		header.createCell(index++).setCellValue("Faculty 1 Name");
		header.createCell(index++).setCellValue("Faculty 1 Evaluated");
		header.createCell(index++).setCellValue("Faculty 1 Score");
		header.createCell(index++).setCellValue("Faculty 1 Remarks");
		header.createCell(index++).setCellValue("Faculty 1 Reason");
		header.createCell(index++).setCellValue("Faculty 2 ID");
		header.createCell(index++).setCellValue("Faculty 2 Name");
		header.createCell(index++).setCellValue("Faculty 2 Evaluated");
		header.createCell(index++).setCellValue("Faculty 2 Score");
		header.createCell(index++).setCellValue("Faculty 2 Remarks");
		header.createCell(index++).setCellValue("Faculty 2 Reason");
		
		header.createCell(index++).setCellValue("Level 3 Diff");
		
		header.createCell(index++).setCellValue("Faculty 3 ID");
		header.createCell(index++).setCellValue("Faculty 3 Name");
		header.createCell(index++).setCellValue("Faculty 3 Evaluated");
		header.createCell(index++).setCellValue("Faculty 3 Score");
		header.createCell(index++).setCellValue("Faculty 3 Remarks");
		header.createCell(index++).setCellValue("Faculty 3 Reason");
		
		header.createCell(index++).setCellValue("Revalution Faculty");
		header.createCell(index++).setCellValue("Revalution Faculty Name");
		header.createCell(index++).setCellValue("Opted for Revaluation");
		header.createCell(index++).setCellValue("Revaluation Complete");
		header.createCell(index++).setCellValue("Revaluation Score");
		header.createCell(index++).setCellValue("Revaluation Remarks");
		
		header.createCell(index++).setCellValue("Assignment final marks"); 
		header.createCell(index++).setCellValue("End Date"); 
	
		/*header.createCell(index++).setCellValue("Revisit Complete");
		header.createCell(index++).setCellValue("Revisit Score");
		header.createCell(index++).setCellValue("Revisit Remarks");*/
				
		int rowNum = 1;
		for (int i = 0 ; i < studentList.size(); i++) {
			index = 0;
			AssignmentFileBean bean = studentList.get(i);
			 
			StudentExamBean student = getAllStudents.get(bean.getSapId());
			bean.setProgram(student.getProgram());
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
			row.createCell(index++).setCellValue(bean.getSfName() + " " + bean.getSlName());
			row.createCell(index++).setCellValue(student.getEmailId());
			row.createCell(index++).setCellValue(student.getMobile());
			row.createCell(index++).setCellValue(bean.getScore());
			row.createCell(index++).setCellValue(bean.getProgram());
			row.createCell(index++).setCellValue(bean.getFacultyId());
			row.createCell(index++).setCellValue(bean.getFaculty1Name());
			row.createCell(index++).setCellValue(bean.getEvaluated() == null ? "N":bean.getEvaluated());
			row.createCell(index++).setCellValue(bean.getScore());
			row.createCell(index++).setCellValue(bean.getRemarks());
			row.createCell(index++).setCellValue(bean.getReason());
			
			row.createCell(index++).setCellValue(bean.getFaculty2());
			row.createCell(index++).setCellValue(bean.getFaculty2Name());
			row.createCell(index++).setCellValue(bean.getFaculty2Evaluated() == null ? "N":bean.getFaculty2Evaluated());
			row.createCell(index++).setCellValue(bean.getFaculty2Score());
			row.createCell(index++).setCellValue(bean.getFaculty2Remarks());
			row.createCell(index++).setCellValue(bean.getFaculty2Reason());
			
			row.createCell(index++).setCellValue(bean.getPercentDifference());
			
			row.createCell(index++).setCellValue(bean.getFaculty3());
			row.createCell(index++).setCellValue(bean.getFaculty3Name());
			row.createCell(index++).setCellValue(bean.getFaculty3Evaluated() == null ? "N":bean.getFaculty3Evaluated());
			row.createCell(index++).setCellValue(bean.getFaculty3Score());
			row.createCell(index++).setCellValue(bean.getFaculty3Remarks());
			row.createCell(index++).setCellValue(bean.getFaculty3Reason());
			
			row.createCell(index++).setCellValue(bean.getFacultyIdRevaluation());
			row.createCell(index++).setCellValue(bean.getFacultyRevaluationName());
			row.createCell(index++).setCellValue(bean.getMarkedForRevaluation());
			row.createCell(index++).setCellValue(bean.getRevaluated() == null ? "N":bean.getRevaluated());
			row.createCell(index++).setCellValue(bean.getRevaluationScore());
			row.createCell(index++).setCellValue(bean.getRevaluationRemarks());
			
			row.createCell(index++).setCellValue(bean.getAssignmentscore());
			row.createCell(index++).setCellValue(bean.getEndDate());
		
			/*row.createCell(index++).setCellValue(bean.getRevisited());
			row.createCell(index++).setCellValue(bean.getRevisitScore());
			row.createCell(index++).setCellValue(bean.getRevisitRemarks());*/
        }
		
		/*
		//create a wordsheet
				Sheet sheet2 = workbook.createSheet("Assignment QuestionWise Marks List");
				int indexx = 0;
				Row header2 = sheet2.createRow(0);
				header2.createCell(indexx++).setCellValue("Sr. No.");
				header2.createCell(indexx++).setCellValue("Year");
				header2.createCell(indexx++).setCellValue("Month");
				header2.createCell(indexx++).setCellValue("Subject");
				header2.createCell(indexx++).setCellValue("Student ID");
				header2.createCell(indexx++).setCellValue("Student Name");
				
				header2.createCell(indexx++).setCellValue("Evaluated Faculty ID");
				
				header2.createCell(indexx++).setCellValue("Q1 Marks");
				header2.createCell(indexx++).setCellValue("Q1 Remarks");
				header2.createCell(indexx++).setCellValue("Q2 Marks");
				header2.createCell(indexx++).setCellValue("Q2 Remarks");
				header2.createCell(indexx++).setCellValue("Q3 Marks");
				header2.createCell(indexx++).setCellValue("Q3 Remarks");
				header2.createCell(indexx++).setCellValue("Q4 Marks");
				header2.createCell(indexx++).setCellValue("Q4 Remarks");
				header2.createCell(indexx++).setCellValue("Q5 Marks");
				header2.createCell(indexx++).setCellValue("Q5 Remarks");
				header2.createCell(indexx++).setCellValue("Q6 Marks");
				header2.createCell(indexx++).setCellValue("Q6 Remarks");
				header2.createCell(indexx++).setCellValue("Q7 Marks");
				header2.createCell(indexx++).setCellValue("Q7 Remarks");
				header2.createCell(indexx++).setCellValue("Q8 Marks");
				header2.createCell(indexx++).setCellValue("Q8 Remarks");
				header2.createCell(indexx++).setCellValue("Q9 Marks");
				header2.createCell(indexx++).setCellValue("Q9 Remarks");
				header2.createCell(indexx++).setCellValue("Q10 Marks");
				header2.createCell(indexx++).setCellValue("Q10 Remarks");
				
				header.createCell(index++).setCellValue("Revisit Complete");
				header.createCell(index++).setCellValue("Revisit Score");
				header.createCell(index++).setCellValue("Revisit Remarks");
						
				int rowNum2 = 1;
				for (int i = 0 ; i < assignmentEvaluatedList2.size(); i++) {
					indexx = 0;
					AssignmentFileBean bean = assignmentEvaluatedList2.get(i);
					StudentBean student = getAllStudents.get(bean.getSapId());
					bean.setProgram(student.getProgram());
					String faculty1Name = "";
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
					}
					
					//create the row data
					Row row = sheet2.createRow(rowNum2++);
					
					row.createCell(indexx++).setCellValue(rowNum2-1);
					row.createCell(indexx++).setCellValue(new Double(bean.getYear()));
					row.createCell(indexx++).setCellValue(bean.getMonth());
					row.createCell(indexx++).setCellValue(bean.getSubject());
					row.createCell(indexx++).setCellValue(new Double(bean.getSapId()));
					row.createCell(indexx++).setCellValue(bean.getSfName() + " " + bean.getSlName());
			
					row.createCell(indexx++).setCellValue(bean.getEvaluatedFaculty());
					
					row.createCell(indexx++).setCellValue(bean.getQ1Marks());
					row.createCell(indexx++).setCellValue(bean.getQ1Remarks());
					row.createCell(indexx++).setCellValue(bean.getQ2Marks());
					row.createCell(indexx++).setCellValue(bean.getQ2Remarks());
					row.createCell(indexx++).setCellValue(bean.getQ3Marks());
					row.createCell(indexx++).setCellValue(bean.getQ3Remarks());
					row.createCell(indexx++).setCellValue(bean.getQ4Marks());
					row.createCell(indexx++).setCellValue(bean.getQ4Remarks());
					row.createCell(indexx++).setCellValue(bean.getQ5Marks());
					row.createCell(indexx++).setCellValue(bean.getQ5Remarks());
					row.createCell(indexx++).setCellValue(bean.getQ6Marks());
					row.createCell(indexx++).setCellValue(bean.getQ6Remarks());
					row.createCell(indexx++).setCellValue(bean.getQ7Marks());
					row.createCell(indexx++).setCellValue(bean.getQ7Remarks());
					row.createCell(indexx++).setCellValue(bean.getQ8Marks());
					row.createCell(indexx++).setCellValue(bean.getQ8Remarks());
					row.createCell(indexx++).setCellValue(bean.getQ9Marks());
					row.createCell(indexx++).setCellValue(bean.getQ9Remarks());
					row.createCell(indexx++).setCellValue(bean.getQ10Marks());
					row.createCell(indexx++).setCellValue(bean.getQ10Remarks());
					
					
					
					row.createCell(index++).setCellValue(bean.getRevisited());
					row.createCell(index++).setCellValue(bean.getRevisitScore());
					row.createCell(index++).setCellValue(bean.getRevisitRemarks());
		        }
		*/
		
	
	}

}
