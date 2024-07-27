package com.nmims.views;

import java.util.ArrayList;
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
import com.nmims.beans.QuestionFileBean;

public class QuestionReviewExcelView  extends AbstractXlsxStreamingView  implements ApplicationContextAware{
	
	@Override
	protected void buildExcelDocument(Map model, Workbook workbook,	HttpServletRequest request, HttpServletResponse response)
		throws Exception {
 

		ArrayList<List> questionBeanList = (ArrayList<List>)model.get("questionBeanList");
		
		ArrayList<QuestionFileBean> mcqQuestionBeanList = (ArrayList<QuestionFileBean>)questionBeanList.get(0);
		ArrayList<QuestionFileBean> descriptiveQuestionBeanList = (ArrayList<QuestionFileBean>)questionBeanList.get(1);
		
		//create a wordsheet
		Sheet sheet = workbook.createSheet("MCQ Questions");
		int index = 0;
		Row header = sheet.createRow(0);
		//header.createCell(index++).setCellValue("question_id");
		header.createCell(index++).setCellValue("exam_code");
		header.createCell(index++).setCellValue("subject_code");
		header.createCell(index++).setCellValue("section_code");
		header.createCell(index++).setCellValue("question_text");
		header.createCell(index++).setCellValue("option_1");
		header.createCell(index++).setCellValue("option_2");
		header.createCell(index++).setCellValue("option_3");
		header.createCell(index++).setCellValue("option_4");
		header.createCell(index++).setCellValue("option_5");
		header.createCell(index++).setCellValue("correct_answer");
		header.createCell(index++).setCellValue("marks");
		header.createCell(index++).setCellValue("negative_marks");
		header.createCell(index++).setCellValue("question_type");
		header.createCell(index++).setCellValue("case_id");
		header.createCell(index++).setCellValue("difficulty");
		header.createCell(index++).setCellValue("sub_section_code");
		header.createCell(index++).setCellValue("Review Remarks");
		
		int rowNum = 1;
		for (int i = 0 ; i < mcqQuestionBeanList.size(); i++) {
			index = 0;
			//create the row data
			Row row = sheet.createRow(rowNum++);
			QuestionFileBean bean = mcqQuestionBeanList.get(i);
			//row.createCell(index++).setCellValue(bean.getQuestionId());
			row.createCell(index++).setCellValue(bean.getExamCode());
			row.createCell(index++).setCellValue(bean.getSubjectCode());
			
			row.createCell(index++).setCellValue(new Double(bean.getSectionCode()));
			row.createCell(index++).setCellValue(bean.getQuestionText());
			row.createCell(index++).setCellValue(bean.getOption1());
			row.createCell(index++).setCellValue(bean.getOption2());
			row.createCell(index++).setCellValue(bean.getOption3());
			row.createCell(index++).setCellValue(bean.getOption4());
			row.createCell(index++).setCellValue(bean.getOption5());
			row.createCell(index++).setCellValue(new Double(bean.getCorrectAnswer()));
			row.createCell(index++).setCellValue(new Double(bean.getMarks()));
			row.createCell(index++).setCellValue(new Double(bean.getNegativeMarks()));
			row.createCell(index++).setCellValue(bean.getQuestionType());
			row.createCell(index++).setCellValue(bean.getCaseId());
			row.createCell(index++).setCellValue(new Double(bean.getDifficulty()));
			row.createCell(index++).setCellValue(new Double(bean.getSubsectionCode()));
			
			if(bean.getReviewRemarks().length() > 32765){
			}
			row.createCell(index++).setCellValue(bean.getReviewRemarks());
			
        }
		
		
		
		//Create Descriptive excel
		//create a wordsheet
		sheet = workbook.createSheet("Descriptive Questions");
		index = 0;
		header = sheet.createRow(0);
		//header.createCell(index++).setCellValue("question_id");
		header.createCell(index++).setCellValue("exam_code");
		header.createCell(index++).setCellValue("subject_code");
		header.createCell(index++).setCellValue("section_code");
		header.createCell(index++).setCellValue("question_text");
		
		header.createCell(index++).setCellValue("marks");
		header.createCell(index++).setCellValue("question_type");
		header.createCell(index++).setCellValue("difficulty");
		header.createCell(index++).setCellValue("sub_section_code");
		header.createCell(index++).setCellValue("Review Remarks");
		
		rowNum = 1;
		for (int i = 0 ; i < descriptiveQuestionBeanList.size(); i++) {
			index = 0;
			//create the row data
			Row row = sheet.createRow(rowNum++);
			QuestionFileBean bean = descriptiveQuestionBeanList.get(i);
			//row.createCell(index++).setCellValue(bean.getQuestionId());
			row.createCell(index++).setCellValue(bean.getExamCode());
			row.createCell(index++).setCellValue(bean.getSubjectCode());
			row.createCell(index++).setCellValue(new Double(bean.getSectionCode()));
			row.createCell(index++).setCellValue(bean.getQuestionText());
			row.createCell(index++).setCellValue(new Double(bean.getMarks()));
			row.createCell(index++).setCellValue(bean.getQuestionType());
			row.createCell(index++).setCellValue(new Double(bean.getDifficulty()));
			row.createCell(index++).setCellValue(new Double(bean.getSubsectionCode()));
			row.createCell(index++).setCellValue(bean.getReviewRemarks());
			
        }
		
		sheet.setAutoFilter(CellRangeAddress.valueOf("A1:I"+questionBeanList.size()));
	}

}
