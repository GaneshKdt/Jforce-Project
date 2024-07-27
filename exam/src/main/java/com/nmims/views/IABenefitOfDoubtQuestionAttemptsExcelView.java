package com.nmims.views;

import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFFont;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.view.document.AbstractXlsxStreamingView;

import com.nmims.beans.StudentQuestionResponseExamBean;

/**
 * View class for creation of Excel Workbook from IA Benefit of Doubt question attempts data using Apache POI.
 * @author Raynal Dcunha
 */
@Component("iaBenefitOfDoubtQuestionAttemptsExcelView")
public class IABenefitOfDoubtQuestionAttemptsExcelView extends AbstractXlsxStreamingView {
	private static final String SHEET_NAME_QUESTIONS_ATTEMPT = "Student Question Attempts";
	private static final String SHEET_NAME_OPTIONS_ATTEMPT = "Option Attempts Count";
	
	private static final String[] questionAttemptsHeaderArray = {	"Sr. No.", "Student ID", "Test Name", "Attempt", "Answer", 
																	"Marks", "Faculty ID", "Is Checked? (Y/N)"	};
	private static final String[] questionSelectionAttemptsHeaderArray = {	"Sr. No.", "Student ID", "Test Name", "Attempt", "Answer"	};
	private static final String[] questionAttemptCountHeaderArray = {	"IA Benefit of Doubt - student question attempts data", "Count"	};
	
	private static final Logger bod_excelView_logger = LoggerFactory.getLogger(IABenefitOfDoubtQuestionAttemptsExcelView.class);
	
	/**
	 * Method for Creating the styled header row in Workbook sheet
	 * @param headers - Array of the header names to be included
	 * @param workbook - Workbook to be modified
	 * @param sheetName - name of the sheet to be worked upon.
	 */
	private static void createExcelHeaderRowWithStyling(String[] headers, Workbook workbook, String sheetName) {
		Sheet sheet = workbook.getSheet(sheetName);										//Accessing the Sheet to be modified from the Workbook via it's name
		Row headerRow = sheet.createRow(0);												//Creating a Row in the Sheet
		headerRow.setHeightInPoints((2 * sheet.getDefaultRowHeightInPoints()));			//Setting the height of the row
		CellStyle headerCellStyle = workbook.createCellStyle();							//Creating CellStyle to add the styling required
		headerCellStyle.setFillForegroundColor(IndexedColors.AQUA.getIndex());
		headerCellStyle.setFillPattern(CellStyle.SOLID_FOREGROUND);
		headerCellStyle.setAlignment(CellStyle.ALIGN_CENTER);
		headerCellStyle.setVerticalAlignment(CellStyle.VERTICAL_CENTER);
		
		//Adding borders to the CellStyle
		headerCellStyle.setBorderTop(CellStyle.BORDER_MEDIUM);
		headerCellStyle.setBorderRight(CellStyle.BORDER_MEDIUM);
		headerCellStyle.setBorderBottom(CellStyle.BORDER_MEDIUM);
		headerCellStyle.setBorderLeft(CellStyle.BORDER_MEDIUM);
		XSSFFont font = (XSSFFont) workbook.createFont();
		font.setFontName("Arial");
		font.setBold(true);
		headerCellStyle.setFont(font);													//Adding the style of Font to the CellStyle
		
		for(int i = 0; i < headers.length; i++) {
			sheet.setColumnWidth(i, headers[i].length() * 256 + (4 * 256));				//Setting the Column Width of each column as per it's name
			
			Cell headerCell = headerRow.createCell(i);									//Creating a cell, adding the textValue and styling to be added to the cell
			headerCell.setCellValue(headers[i]);
			headerCell.setCellStyle(headerCellStyle);
		}
	}
	
	@Override
	protected void buildExcelDocument(Map<String, Object> model, Workbook workbook, HttpServletRequest request, HttpServletResponse response) {
		try {
			//Type safety: Unchecked cast warning, Java cannot guarantee object returned from model is an instance of List or Map, throws ClassCast Exception
			final int questionType = (int) model.get("questionType");
			final List<StudentQuestionResponseExamBean> questionAttempts = (List<StudentQuestionResponseExamBean>) model.get("attemptList");
			final Map<String, Integer> questionAttemptCount = (Map<String, Integer>) model.get("questionAttemptCount");
			final Map<String, Integer> optionSelectCount = (Map<String, Integer>) model.get("selectedOptionCount");
			
			Sheet sheet = workbook.createSheet(SHEET_NAME_QUESTIONS_ATTEMPT);															//Create a new sheet with sheetName
			if(questionType == 1 || questionType == 2 || questionType == 5)																//Creating the header row with styling on the sheet
				createExcelHeaderRowWithStyling(questionSelectionAttemptsHeaderArray, workbook, SHEET_NAME_QUESTIONS_ATTEMPT);			
			else
				createExcelHeaderRowWithStyling(questionAttemptsHeaderArray, workbook, SHEET_NAME_QUESTIONS_ATTEMPT);
			
			int rowNum = 1;
			for(StudentQuestionResponseExamBean studentAttempt: questionAttempts) {
				//create the row data
				Row row = sheet.createRow(rowNum);
				
				row.createCell(0).setCellValue(rowNum++);
				row.createCell(1).setCellValue(studentAttempt.getSapid());
				row.createCell(2).setCellValue(studentAttempt.getTestName());
				row.createCell(3).setCellValue(studentAttempt.getAttempt());
				row.createCell(4).setCellValue(studentAttempt.getAnswer());
				
				if(questionType != 1 && questionType != 2 && questionType != 5) {
					row.createCell(5).setCellValue(studentAttempt.getMarks());
					row.createCell(6).setCellValue(studentAttempt.getFacultyId());
					row.createCell(7).setCellValue(studentAttempt.getIsChecked() == 1 ? "Y" : "N");
				}
			}
			
			Sheet attemptCountSheet = workbook.createSheet(SHEET_NAME_OPTIONS_ATTEMPT);													//Create a new sheet with sheetName
			createExcelHeaderRowWithStyling(questionAttemptCountHeaderArray, workbook, SHEET_NAME_OPTIONS_ATTEMPT);
			
			int countSheetRow = 1;
			for(Map.Entry<String, Integer> entrySet: questionAttemptCount.entrySet()) {
				Row row = attemptCountSheet.createRow(countSheetRow++);
				row.createCell(0).setCellValue(questionAttemptsMapKey(entrySet.getKey()));
				row.createCell(1).setCellValue(entrySet.getValue());
			}
			
			attemptCountSheet.createRow(countSheetRow++);																				//Creating an empty row for data segregation
			for(Map.Entry<String, Integer> entrySet: optionSelectCount.entrySet()) {
				Row row = attemptCountSheet.createRow(countSheetRow++);
				row.createCell(0).setCellValue(entrySet.getKey());
				row.createCell(1).setCellValue(entrySet.getValue());
			}
			
			response.setHeader(HttpHeaders.CONTENT_DISPOSITION, "attachment;filename=BoD-student-question-attempts-data.xlsx");			//Content-Disposition - attachment content with specified name
		}
		catch(ClassCastException ex) {
			bod_excelView_logger.error("ClassCast Exception while retrieving the data from model for IA Benefit of Doubt question attempts data excel view.");
		}
		catch(Exception ex) {
			bod_excelView_logger.error("Error while creating the XSSFWorkbook with the question attempts data, Exception thrown: ", ex);
		}
	}
	
	private String questionAttemptsMapKey(String key) {
		switch(key) {
			case "test-attempted":
				return "No. of students who attempted the test";
			case "applicable-students":
				return "No. of students who got this particular question";
			case "right-selection":
				return "No. of students who selected the correct option(s)";
			case "wrong-selection":
				return "No. of students who selected the wrong option(s)";
			case "question-attempted":
				return "No. of students who attempted this question";
			case "not-attempted":
				return "No. of students who did not attempt this question";
			default:
				return key;
		}
	}
}
