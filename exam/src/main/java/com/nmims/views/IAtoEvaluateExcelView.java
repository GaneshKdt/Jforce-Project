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
import com.nmims.beans.ExecutiveExamOrderBean;
import com.nmims.beans.StudentExamBean;
import com.nmims.beans.StudentMarksBean;
import com.nmims.beans.StudentsTestDetailsExamBean;
import com.nmims.beans.TestExamBean;
import com.nmims.daos.StudentMarksDAO;

public class IAtoEvaluateExcelView  extends AbstractXlsxStreamingView  implements ApplicationContextAware {
	
	@Override
	protected void buildExcelDocument(Map model, Workbook workbook,	HttpServletRequest request, HttpServletResponse response)
		throws Exception {
		
		List<TestExamBean> iaList = (List<TestExamBean>) model.get("iaList");
		
		//create a wordsheet :- START
		Sheet sheet = workbook.createSheet("Report Of IA Evaluated List");
		
		int index = 0;
		Row header = sheet.createRow(0);
		//testName, tst.showResult
		header.createCell(index++).setCellValue("Subject");
		header.createCell(index++).setCellValue("TestName");
		header.createCell(index++).setCellValue("IsResultLive");
//		header.createCell(index++).setCellValue("Result Declared On");						//Commented for IA Evaluation Report Results ReRun Card: 12418
		header.createCell(index++).setCellValue("Original Result Live DateTime");			//Added for IA Evaluation Report Results ReRun Card: 12418
		header.createCell(index++).setCellValue("Last Modified Result Live DateTime");		//Added for IA Evaluation Report Results ReRun Card: 12418
		header.createCell(index++).setCellValue("Teaching Faculty Name");
		header.createCell(index++).setCellValue("Teaching Faculty ID");
		header.createCell(index++).setCellValue("Evaluator Faculty Name");
		header.createCell(index++).setCellValue("Evaluator Faculty ID");
		header.createCell(index++).setCellValue("Session No.");
		header.createCell(index++).setCellValue("Question Type");
		header.createCell(index++).setCellValue("Total No. of Questions");
		header.createCell(index++).setCellValue("Test Start Date / Time");
		header.createCell(index++).setCellValue("SAP ID");
		header.createCell(index++).setCellValue("First Name");
		header.createCell(index++).setCellValue("Last Name");
		header.createCell(index++).setCellValue("Question");		
		header.createCell(index++).setCellValue("Answer");
		header.createCell(index++).setCellValue("Remark");		
		header.createCell(index++).setCellValue("Evaluated (Y / N)");
		header.createCell(index++).setCellValue("Marks Awarded");
		header.createCell(index++).setCellValue("Total Score");

		
		
		header.createCell(index++).setCellValue("Attempt Status");
		
		

		header.createCell(index++).setCellValue("Batch");
		header.createCell(index++).setCellValue("Acad Year");
		header.createCell(index++).setCellValue("Acad Month");
		header.createCell(index++).setCellValue("Exam Year");
		header.createCell(index++).setCellValue("Exam Month");
		header.createCell(index++).setCellValue("Attempt Status");

		int rowNum = 1;
		for (int i = 0 ; i < iaList.size(); i++) {
			//create the row data
			index = 0;
			Row row = sheet.createRow(rowNum++);
			TestExamBean bean = iaList.get(i);
			
	
			row.createCell(index++).setCellValue(bean.getSubject());
			row.createCell(index++).setCellValue(bean.getTestName());
			row.createCell(index++).setCellValue(bean.getShowResult());
//			row.createCell(index++).setCellValue(bean.getResultDeclaredOn());					//Commented for IA Evaluation Report Results ReRun Card: 12418
			row.createCell(index++).setCellValue(bean.getInitialResultLiveDateTime());			//Added for IA Evaluation Report Results ReRun Card: 12418
			row.createCell(index++).setCellValue(bean.getLastModifiedResultLiveDate());			//Added for IA Evaluation Report Results ReRun Card: 12418
			row.createCell(index++).setCellValue(bean.getFacultyName());
			row.createCell(index++).setCellValue(bean.getFacultyId());
			row.createCell(index++).setCellValue(bean.getEvalFacultyName());
			row.createCell(index++).setCellValue(bean.getEvalFacultyId());
			row.createCell(index++).setCellValue(bean.getSessionName());
			row.createCell(index++).setCellValue(bean.getQuestionType());
			row.createCell(index++).setCellValue(bean.getNoOfQuestions());
			row.createCell(index++).setCellValue(bean.getStartDate());
			row.createCell(index++).setCellValue(bean.getSapid());
			row.createCell(index++).setCellValue(bean.getFirstName());
			row.createCell(index++).setCellValue(bean.getLastName());
			row.createCell(index++).setCellValue(bean.getQuestion());
			row.createCell(index++).setCellValue(bean.getAnswer());
			row.createCell(index++).setCellValue(bean.getRemark());			
			row.createCell(index++).setCellValue(bean.getEvaluated());
			row.createCell(index++).setCellValue(bean.getScore());
			row.createCell(index++).setCellValue(bean.getQuestionsMarks());
			row.createCell(index++).setCellValue(bean.getBatch());
			row.createCell(index++).setCellValue(bean.getAcadYear());
			row.createCell(index++).setCellValue(bean.getAcadMonth());
			row.createCell(index++).setCellValue(bean.getYear());
			row.createCell(index++).setCellValue(bean.getMonth());	
			row.createCell(index++).setCellValue(bean.getAttemptStatus());
			
			

			row.createCell(index++).setCellValue(bean.getAttemptStatus());
        }
		
	
		
	
	}
}

