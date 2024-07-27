package com.nmims.helpers;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFCellStyle;
import org.apache.poi.xssf.usermodel.XSSFFont;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import com.nmims.beans.IAReportsBean;

/**
 * Helper class for the creation of Excel files of the IAReports data passed using Apache POI.
 * @author Raynal Dcunha
 */
public class IAReportsExcelViewHelper {
	/**
	 * Method for Creating the styled header row in Workbook sheet
	 * @param headers - Array of the header names to be included
	 * @param workbook - Workbook to be modified
	 * @param sheetName - name of the sheet to be worked upon.
	 */
	private static void createExcelHeaderRowWithStyling(String[] headers, Workbook workbook, String sheetName) {
		Sheet sheet = workbook.getSheet(sheetName);			//Accessing the Sheet to be modified from the Workbook via it's name
		Row headerRow = sheet.createRow(0);					//Creating a Row in the Sheet
		headerRow.setHeightInPoints((2 * sheet.getDefaultRowHeightInPoints()));		//Setting the height of the row

		CellStyle headerCellStyle = workbook.createCellStyle();			//Creating CellStyle to add the styling required
		headerCellStyle.setFillForegroundColor(IndexedColors.AQUA.getIndex());
		headerCellStyle.setFillPattern(CellStyle.SOLID_FOREGROUND);
//		headerCellStyle.setWrapText(true);
		headerCellStyle.setAlignment(CellStyle.ALIGN_CENTER);
		headerCellStyle.setVerticalAlignment(CellStyle.VERTICAL_CENTER);
		
		//Adding borders to the CellStyle
		headerCellStyle.setBorderTop(XSSFCellStyle.BORDER_MEDIUM);
		headerCellStyle.setBorderRight(XSSFCellStyle.BORDER_MEDIUM);
		headerCellStyle.setBorderBottom(XSSFCellStyle.BORDER_MEDIUM);
		headerCellStyle.setBorderLeft(XSSFCellStyle.BORDER_MEDIUM);

		XSSFFont font = ((XSSFWorkbook) workbook).createFont();
		font.setFontName("Arial");
		font.setBold(true);
		headerCellStyle.setFont(font);			//Adding the style of Font to the CellStyle
		
		for(int i = 0; i < headers.length; i++) {
			sheet.setColumnWidth(i, headers[i].length() * 256 + (4 * 256));			//Setting the Column Width of each column as per it's name
			
			//Creating a cell, adding the textValue and styling to be added to the cell
			Cell headerCell = headerRow.createCell(i);
			headerCell.setCellValue(headers[i]);
			headerCell.setCellStyle(headerCellStyle);
		}
	}
	
	/**
	 * Creating a Sheet of XSSFWorkbook from the list of IAReportsBean data passed.
	 * @param reportDataList - list of IAReportsBean data which contains the required information for each included test.
	 * @return ByteArrayInputStream containing the data of the created XSSFWorkbook as an Array of Bytes
	 * @throws IOException
	 */
	public static ByteArrayInputStream testDataWorksheet(ArrayList<IAReportsBean> reportDataList) throws IOException {
		Workbook workbook = new XSSFWorkbook();
		Sheet sheet = workbook.createSheet("IA Verify Parameters Report");		//Name of the sheet
		
		//Array of Column headers
		String[] headerStringArray = {	"Sr. No.", "Test Name", "Test Initials", "Test Description", "Exam Year",  "Exam Month", "Acad Year", "Acad Month", "Faculty Id", "Faculty Name", 
										"Start Date Time", "End Date Time", "Window Time (in Minutes)", "Duration (in Minutes)", "Consumer Type", "Program", "Program Structure", "Subject", 
										"Applicable Type", "Batch Name", "Module Name", "Type", "Max Questions To Show to Students", "Sectionwise Questions Configured", "Sectionwise Questions Uploaded", 
										"Score out of", "Allow After End Date (Y / N)", "Send Email Alert (Y / N)", "Send SMS Alert (Y / N)", 
										"Proctoring Enabled (Y / N)", "Show Calculator (Y / N)", "Test Live (Y / N)", "Results Live (Y / N)"	};
		
		createExcelHeaderRowWithStyling(headerStringArray, workbook, "IA Verify Parameters Report");		//Creating the header row with styling on the sheet

		CellStyle cellStyle = workbook.createCellStyle();				//Creating a CellStyle with WrapText set to true for MultiLine cells
//		CreationHelper createHelper = workbook.getCreationHelper();
		cellStyle.setWrapText(true);
//		cellStyle.setDataFormat(createHelper.createDataFormat().getFormat("yyyy-MM-dd HH:mm:ss"));

		int index = 0, rowNum = 1;
		for (int i = 0 ; i < reportDataList.size(); i++) {
			//create the row data
			Row row = sheet.createRow(rowNum);
			
			IAReportsBean iaReportsBean = reportDataList.get(i);
			
			row.createCell(index++).setCellValue(rowNum++);
			row.createCell(index++).setCellValue(iaReportsBean.getTestName());
			row.createCell(index++).setCellValue(iaReportsBean.getTestInitials());
			row.createCell(index++).setCellValue(iaReportsBean.getTestDescription());
			row.createCell(index++).setCellValue(iaReportsBean.getYear());
			row.createCell(index++).setCellValue(iaReportsBean.getMonth());
			row.createCell(index++).setCellValue(iaReportsBean.getAcadYear());
			row.createCell(index++).setCellValue(iaReportsBean.getAcadMonth());
			row.createCell(index++).setCellValue(iaReportsBean.getFacultyId());
			row.createCell(index++).setCellValue(iaReportsBean.getFacultyName());
			row.createCell(index++).setCellValue(iaReportsBean.getStartDate());
			row.createCell(index++).setCellValue(iaReportsBean.getEndDate());
			row.createCell(index++).setCellValue(iaReportsBean.getWindowTime());
			row.createCell(index++).setCellValue(iaReportsBean.getDuration());
			row.createCell(index++).setCellValue(iaReportsBean.getConsumerType());
			row.createCell(index++).setCellValue(iaReportsBean.getProgram());
			row.createCell(index++).setCellValue(iaReportsBean.getProgramStructure());
			row.createCell(index++).setCellValue(iaReportsBean.getSubject());
			row.createCell(index++).setCellValue(iaReportsBean.getApplicableType());
			row.createCell(index++).setCellValue(iaReportsBean.getBatchName());
			row.createCell(index++).setCellValue(iaReportsBean.getModuleName());
			row.createCell(index++).setCellValue(iaReportsBean.getTestType());
			row.createCell(index++).setCellValue(iaReportsBean.getMaxQuestnToShow());
			
			setValueInCellStyle(row, index++, iaReportsBean.getQuestionsConfigured(), cellStyle);
			setValueInCellStyle(row, index++, iaReportsBean.getQuestionsUploaded(), cellStyle);
			
			row.createCell(index++).setCellValue(iaReportsBean.getMaxScore());
			row.createCell(index++).setCellValue(iaReportsBean.getAllowAfterEndDate());
			row.createCell(index++).setCellValue(iaReportsBean.getSendEmailAlert());
			row.createCell(index++).setCellValue(iaReportsBean.getSendSmsAlert());
			row.createCell(index++).setCellValue(iaReportsBean.getProctoringEnabled());
			row.createCell(index++).setCellValue(iaReportsBean.getShowCalculator());
			row.createCell(index++).setCellValue(iaReportsBean.getTestLive());
			row.createCell(index++).setCellValue(iaReportsBean.getShowResultsToStudents());
			
			index = 0;			//Setting index back to 0, to start the next Row from the first Column
        }
		
		//Creating a OutputStream to hold the Workbook data
		ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        workbook.write(outputStream);
        outputStream.flush();
        
        //Returning the OutputStream as an array of bytes
        return new ByteArrayInputStream(outputStream.toByteArray());
	}
	
	/**
	 * Utility Method created to create a Cell with the passed CellStyle and cellValue
	 * @param row - Row object to create the required Cell
	 * @param cellColumn - position of the Cell
	 * @param cellValue - textValue of the Cell
	 * @param cellStyle - style to be added to created Cell
	 */
	private static void  setValueInCellStyle(Row row, int cellColumn, String cellValue, CellStyle cellStyle) {
		Cell cell = row.createCell(cellColumn++);
		cell.setCellStyle(cellStyle);
		cell.setCellValue(cellValue);
	}
}
