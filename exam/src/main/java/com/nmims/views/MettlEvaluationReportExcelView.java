package com.nmims.views;

import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.springframework.context.ApplicationContextAware;
import org.springframework.web.servlet.view.document.AbstractXlsxStreamingView;

import com.nmims.beans.MettlEvaluatorInfo;
import com.nmims.beans.MettlSectionQuestionResponse;
import com.nmims.beans.MettlStudentSectionInfo;
import com.nmims.beans.MettlStudentTestInfo;



public class MettlEvaluationReportExcelView extends AbstractXlsxStreamingView  implements ApplicationContextAware{

	Map<String, String> questions;
	
	@SuppressWarnings("unchecked")
	@Override
	protected void buildExcelDocument(Map<String, Object> model, Workbook workbook,	HttpServletRequest request, HttpServletResponse response) throws Exception {
		List<MettlStudentTestInfo> testInfoList = (List<MettlStudentTestInfo>) model.get("testInfoList");
		List<MettlStudentSectionInfo> studentTestSectionInfo = (List<MettlStudentSectionInfo>) model.get("studentTestSectionInfo");
		List<MettlSectionQuestionResponse> questionInfo = (List<MettlSectionQuestionResponse>) model.get("questionInfo");
		List<MettlEvaluatorInfo> evaluatorInfo = (List<MettlEvaluatorInfo>) model.get("evaluatorInfo");
		questions = (Map<String, String>) model.get("questions");

		createStudentTestInfoSheet(testInfoList, workbook);
		createStudentSectionTestInfoSheet(studentTestSectionInfo, workbook);
		createStudentQuestionInfoSheet(questionInfo, workbook);
		createStudentEvaluatorInfoSheet(evaluatorInfo, workbook);
	}

	private void createStudentEvaluatorInfoSheet(List<MettlEvaluatorInfo> evaluatorInfo, Workbook workbook) {

		Sheet sheet = workbook.createSheet("Evaluator Info");
		int index = 0;
		Row header = sheet.createRow(0);
		header.createCell(index++).setCellValue("Sr. No.");
		header.createCell(index++).setCellValue("SAP ID");
		header.createCell(index++).setCellValue("Student Name");
		header.createCell(index++).setCellValue("Subject");
		header.createCell(index++).setCellValue("Schedule Access Key");
		header.createCell(index++).setCellValue("Section Name");
		header.createCell(index++).setCellValue("Question Id");
		header.createCell(index++).setCellValue("Question Text");
		header.createCell(index++).setCellValue("Evaluator Email");
		header.createCell(index++).setCellValue("Evaluator Name");
		header.createCell(index++).setCellValue("Marks Awarded");
		header.createCell(index++).setCellValue("Evaluation Comments");
		header.createCell(index++).setCellValue("Evaluation Time");
		header.createCell(index++).setCellValue("Evaluator Role");
		
		int rowNum = 1;
		
		int i = 0;
		for (MettlEvaluatorInfo bean : evaluatorInfo) {
			index = 0;
			i++;
			Row row = sheet.createRow(rowNum++);
			row.createCell(index++).setCellValue((i));
			row.createCell(index++).setCellValue(bean.getSapid());
			row.createCell(index++).setCellValue(bean.getStudent_name());
			row.createCell(index++).setCellValue(bean.getSubject());
			row.createCell(index++).setCellValue(bean.getScheduleAccessKey());
			row.createCell(index++).setCellValue(bean.getSectionName());
			row.createCell(index++).setCellValue(bean.getQuestionId());
			row.createCell(index++).setCellValue(questions.get(bean.getQuestionId()));
			row.createCell(index++).setCellValue(bean.getEvaluatorEmail());
			row.createCell(index++).setCellValue(bean.getEvaluatorName());
			row.createCell(index++).setCellValue(bean.getMarksAwarded());
			row.createCell(index++).setCellValue(bean.getEvaluationComments());
			row.createCell(index++).setCellValue(bean.getEvaluationTime());
			row.createCell(index++).setCellValue(bean.getEvaluatorRole());
		}
	}

	private void createStudentQuestionInfoSheet(List<MettlSectionQuestionResponse> questionInfo, Workbook workbook) {

		Sheet sheet = workbook.createSheet("Student Question Info");
		int index = 0;
		Row header = sheet.createRow(0);
		header.createCell(index++).setCellValue("Sr. No.");
		header.createCell(index++).setCellValue("SAP ID");
		header.createCell(index++).setCellValue("Student Name");
		header.createCell(index++).setCellValue("Subject");
		header.createCell(index++).setCellValue("Schedule Access Key");
		header.createCell(index++).setCellValue("Section Name");
		header.createCell(index++).setCellValue("Question Id");
		header.createCell(index++).setCellValue("Question Text");
		header.createCell(index++).setCellValue("API Question Type");
		header.createCell(index++).setCellValue("Version");
		header.createCell(index++).setCellValue("Student Response");
		header.createCell(index++).setCellValue("Min Marks");
		header.createCell(index++).setCellValue("Max Marks");
		header.createCell(index++).setCellValue("Marks Scored");
		header.createCell(index++).setCellValue("Bonus Marks(BOD)");
		header.createCell(index++).setCellValue("Is Attempted");
		header.createCell(index++).setCellValue("Time Spent");
		
		int rowNum = 1;
		
		int i = 0;
		for (MettlSectionQuestionResponse bean : questionInfo) {
			index = 0;
			i++;
			Row row = sheet.createRow(rowNum++);
			row.createCell(index++).setCellValue((i));
			row.createCell(index++).setCellValue(bean.getSapid());
			row.createCell(index++).setCellValue(bean.getStudent_name());
			row.createCell(index++).setCellValue(bean.getSubject());
			row.createCell(index++).setCellValue(bean.getScheduleAccessKey());
			row.createCell(index++).setCellValue(bean.getSectionName());
			row.createCell(index++).setCellValue(bean.getQuestionId());
			row.createCell(index++).setCellValue(questions.get(bean.getQuestionId()));
			row.createCell(index++).setCellValue(bean.getApiQuestionType());
			row.createCell(index++).setCellValue(bean.getVersion());
			row.createCell(index++).setCellValue(bean.getStudentResponse());
			row.createCell(index++).setCellValue(bean.getMinMarks());
			row.createCell(index++).setCellValue(bean.getMaxMarks());
			row.createCell(index++).setCellValue(bean.getMarksScored());
			row.createCell(index++).setCellValue(bean.getBonusMarks());
			row.createCell(index++).setCellValue(bean.isAttempted());
			row.createCell(index++).setCellValue(bean.getTimeSpent());
		}
	}

	private void createStudentSectionTestInfoSheet(List<MettlStudentSectionInfo> studentTestSectionInfo, Workbook workbook) {

		Sheet sheet = workbook.createSheet("Student Test Section Info");
		int index = 0;
		Row header = sheet.createRow(0);
		header.createCell(index++).setCellValue("Sr. No.");
		header.createCell(index++).setCellValue("SAP ID");
		header.createCell(index++).setCellValue("Student Name");
		header.createCell(index++).setCellValue("Subject");
		header.createCell(index++).setCellValue("Schedule Access Key");
		header.createCell(index++).setCellValue("Section Name");
		header.createCell(index++).setCellValue("Section Number");
		header.createCell(index++).setCellValue("Total Marks");
		header.createCell(index++).setCellValue("Max Marks");
		header.createCell(index++).setCellValue("Time Taken");
		header.createCell(index++).setCellValue("Total Question");
		header.createCell(index++).setCellValue("Total CorrectAnswers");
		header.createCell(index++).setCellValue("Total Un-Answered");
		
		int rowNum = 1;
		
		int i = 0;
		for (MettlStudentSectionInfo bean : studentTestSectionInfo) {
			index = 0;
			i++;
			Row row = sheet.createRow(rowNum++);
			row.createCell(index++).setCellValue((i));
			row.createCell(index++).setCellValue(bean.getSapid());
			row.createCell(index++).setCellValue(bean.getStudent_name());
			row.createCell(index++).setCellValue(bean.getSubject());
			row.createCell(index++).setCellValue(bean.getScheduleAccessKey());
			row.createCell(index++).setCellValue(bean.getSectionName());
			row.createCell(index++).setCellValue(bean.getSectionNumber());
			row.createCell(index++).setCellValue(bean.getTotalMarks());
			row.createCell(index++).setCellValue(bean.getMaxMarks());
			row.createCell(index++).setCellValue(bean.getTimeTaken());
			row.createCell(index++).setCellValue(bean.getTotalQuestion());
			row.createCell(index++).setCellValue(bean.getTotalCorrectAnswers());
			row.createCell(index++).setCellValue(bean.getTotalUnAnswered());
		}
	}

	private void createStudentTestInfoSheet(List<MettlStudentTestInfo> testInfoList, Workbook workbook) {

		Sheet sheet = workbook.createSheet("Student Test Info");
		int index = 0;
		Row header = sheet.createRow(0);
		
		header.createCell(index++).setCellValue("Sr. No.");
		header.createCell(index++).setCellValue("SAP ID");
		header.createCell(index++).setCellValue("Student Name");
		header.createCell(index++).setCellValue("Subject");
		header.createCell(index++).setCellValue("Schedule Access Key");
		header.createCell(index++).setCellValue("Email Id");
		header.createCell(index++).setCellValue("Start Time");
		header.createCell(index++).setCellValue("End Time");
		header.createCell(index++).setCellValue("Completion Mode");
		header.createCell(index++).setCellValue("Total Marks");
	    header.createCell(index++).setCellValue("Max Marks");
		header.createCell(index++).setCellValue("Percentile");
	    header.createCell(index++).setCellValue("Attempt Time");
	    header.createCell(index++).setCellValue("Candidate Credibility Index");
		header.createCell(index++).setCellValue("Total Question");
		header.createCell(index++).setCellValue("Total Correct Answers");
		header.createCell(index++).setCellValue("Total Un-Answered");
		header.createCell(index++).setCellValue("PDF Report Link");
		header.createCell(index++).setCellValue("HTML Report");
		
		int rowNum = 1;
		
		int i = 0;
		for (MettlStudentTestInfo bean : testInfoList) {
			index = 0;
			i++;
			Row row = sheet.createRow(rowNum++);
			row.createCell(index++).setCellValue((i));
			row.createCell(index++).setCellValue(bean.getSapid());
			row.createCell(index++).setCellValue(bean.getStudent_name());
			row.createCell(index++).setCellValue(bean.getSubject());
			row.createCell(index++).setCellValue(bean.getScheduleAccessKey());
			row.createCell(index++).setCellValue(bean.getEmailId());
			row.createCell(index++).setCellValue(bean.getStartTime());
			row.createCell(index++).setCellValue(bean.getEndTime());
			row.createCell(index++).setCellValue(bean.getCompletionMode());
			row.createCell(index++).setCellValue(bean.getTotalMarks());
			row.createCell(index++).setCellValue(bean.getMaxMarks());
			row.createCell(index++).setCellValue(bean.getPercentile());
			row.createCell(index++).setCellValue(bean.getAttemptTime());
			row.createCell(index++).setCellValue(bean.getCandidateCredibilityIndex());
			row.createCell(index++).setCellValue(bean.getTotalQuestion());
			row.createCell(index++).setCellValue(bean.getTotalCorrectAnswers());
			row.createCell(index++).setCellValue(bean.getTotalUnAnswered());
			row.createCell(index++).setCellValue(bean.getPdfReport());
			row.createCell(index++).setCellValue(bean.getHtmlReport());

		}
	}
}
