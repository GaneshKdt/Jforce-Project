package com.nmims.views;

import java.io.ByteArrayOutputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.view.document.AbstractXlsxStreamingView;
/*import org.springframework.web.servlet.view.document.AbstractXlsxView;*/ 

import com.nmims.beans.CenterExamBean;
import com.nmims.beans.PassFailExamBean;
import com.nmims.beans.StudentExamBean;
import com.nmims.daos.StudentMarksDAO;

@Component("passFailResultsExcelView")
public class PassFailResultsExcelView extends AbstractXlsxStreamingView  implements ApplicationContextAware{
	
	private static ApplicationContext act = null;

	
	@Override
	protected void buildExcelDocument(Map model, Workbook workbook,	HttpServletRequest request, HttpServletResponse response)
		throws Exception {
 
		act = getApplicationContext();
		StudentMarksDAO dao = (StudentMarksDAO)act.getBean("studentMarksDAO");
		HashMap<String, StudentExamBean> sapIdStudentsMap = dao.getAllStudents();
		HashMap<String, CenterExamBean> icLcMap = dao.getICLCMap();
		
		List<PassFailExamBean> studentMarksList = (List<PassFailExamBean>) model.get("studentMarksList");
		
		//create a wordsheet
		//Sheet  sheet = workbook.createSheet("ANS List");

		Sheet sheet = workbook.createSheet("Pass Fail Results");
		int index = 0;
		Row header = sheet.createRow(0);
		header.createCell(index++).setCellValue("Sr. No.");
		header.createCell(index++).setCellValue("Result Processing Year");
		header.createCell(index++).setCellValue("Result Processing Month");
		header.createCell(index++).setCellValue("Written Year");
		header.createCell(index++).setCellValue("Written Month");
		header.createCell(index++).setCellValue("Assignment Year");
		header.createCell(index++).setCellValue("Assignment Month");
		header.createCell(index++).setCellValue("SAP ID");

		//header.createCell(index++).setCellValue("Email ID"); student contact details not be shown in any report
		//header.createCell(index++).setCellValue("Mobile No."); student contact details not be shown in any report
		header.createCell(index++).setCellValue("Validity End Year");
		header.createCell(index++).setCellValue("Validity End Month");
		
		header.createCell(index++).setCellValue("Student Name");
		header.createCell(index++).setCellValue("Gender");
		header.createCell(index++).setCellValue("Customer Type");
		header.createCell(index++).setCellValue("Program");
		header.createCell(index++).setCellValue("Sem");
		header.createCell(index++).setCellValue("Subject");
		header.createCell(index++).setCellValue("Sify Subject Code");
		header.createCell(index++).setCellValue("TEE");
		header.createCell(index++).setCellValue("Assignment");
		header.createCell(index++).setCellValue("Grace");
		header.createCell(index++).setCellValue("Total");
		header.createCell(index++).setCellValue("Pass");
		header.createCell(index++).setCellValue("Fail Reason");
		header.createCell(index++).setCellValue("Assignment Fail Reason");
		header.createCell(index++).setCellValue("IC");
		header.createCell(index++).setCellValue("LC");
		header.createCell(index++).setCellValue("Exam Mode");
		header.createCell(index++).setCellValue("Enrollment Year");
		header.createCell(index++).setCellValue("Enrollment Month");
		header.createCell(index++).setCellValue("Program Structure");
		header.createCell(index++).setCellValue("Result Declared Date");
 
		int rowNum = 1;
		for (int i = 0 ; i < studentMarksList.size(); i++) {
			PassFailExamBean bean = studentMarksList.get(i);
			StudentExamBean student = sapIdStudentsMap.get(bean.getSapid());
			
			String ic = "";
			String lc = "";
			String examMode = "Offline";
			if(student != null){
				ic = student.getCenterName();
				CenterExamBean center = icLcMap.get(student.getCenterCode()); 
				if(center != null){
					lc = center.getLc();
				}
				
				if("Online".equals(student.getExamMode())){
					examMode = "Online";
				}
			}
			
			//create the row data
			Row row = sheet.createRow(rowNum++);
			
			row.createCell(0).setCellValue(rowNum);
			row.createCell(1).setCellValue(bean.getResultProcessedYear());
			row.createCell(2).setCellValue(bean.getResultProcessedMonth());
			row.createCell(3).setCellValue(bean.getWrittenYear());
			row.createCell(4).setCellValue(bean.getWrittenMonth());
			row.createCell(5).setCellValue(bean.getAssignmentYear());
			row.createCell(6).setCellValue(bean.getAssignmentMonth());
			row.createCell(7).setCellValue(bean.getSapid());
			
		//	row.createCell(8).setCellValue(student.getEmailId());
		//	row.createCell(9).setCellValue(student.getMobile());

			row.createCell(8).setCellValue(student.getValidityEndMonth());
			row.createCell(9).setCellValue(student.getValidityEndYear());
			
			

			row.createCell(10).setCellValue(bean.getName());
			row.createCell(11).setCellValue(bean.getGender());
			row.createCell(12).setCellValue(bean.getConsumerType());
			row.createCell(13).setCellValue(bean.getProgram());
			row.createCell(14).setCellValue(bean.getSem());
			row.createCell(15).setCellValue(bean.getSubject());
			row.createCell(16).setCellValue(bean.getSifySubjectCode());
			row.createCell(17).setCellValue(bean.getWrittenscore());
			row.createCell(18).setCellValue(bean.getAssignmentscore());
			row.createCell(19).setCellValue(bean.getGracemarks());
			row.createCell(20).setCellValue(bean.getTotal());
			row.createCell(21).setCellValue(bean.getIsPass());
			row.createCell(22).setCellValue(bean.getFailReason());
			row.createCell(23).setCellValue(bean.getAssignmentRemarks());
			row.createCell(24).setCellValue(ic);
			row.createCell(25).setCellValue(lc);
			row.createCell(26).setCellValue(examMode);
			row.createCell(27).setCellValue(student.getEnrollmentMonth());
			row.createCell(28).setCellValue(student.getEnrollmentYear());
			row.createCell(29).setCellValue(student.getPrgmStructApplicable());
			row.createCell(30).setCellValue(bean.getResultDeclaredDate());
	 
        }
		
		//Written to write excel file to outputStream and flush it out of controller itself//
		/*ByteArrayOutputStream outByteStream = new ByteArrayOutputStream();
		workbook.write(outByteStream);
		byte [] outArray = outByteStream.toByteArray();
		response.setContentType("application/ms-excel");
		response.setContentLength(outArray.length);
		response.setHeader("Expires:", "0"); // eliminates browser caching
		response.setHeader("Content-Disposition", "attachment; filename=PassFailRecords.xls");
		ServletOutputStream outStream = response.getOutputStream();
		outStream.write(outArray);
		outStream.flush();*/ //<- Commented by Vilpesh, as causing error on 06Oct2021 -/downloadPassFailResults.
		//End//
	}

}
