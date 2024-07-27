/**
 * 
 */
package com.nmims.helpers;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.commons.CommonsMultipartFile;

import org.apache.commons.lang3.StringUtils;

import com.nmims.beans.RemarksGradeBean;

/**
 * @author vil_m 
 *
 */

@Service("gradingExcelHelper")
public class GradingExcelHelper {
	
	public static final String FILE_EXTENSION_XLS = "xls";
	public static final String FILE_EXTENSION_XLSX = "xlsx";
	
	public static final Integer INDEX_STUDENTNUMBER = 0;
	public static final Integer INDEX_STUDENTNAME = 1;
	public static final Integer INDEX_PROGRAM = 2;
	public static final Integer INDEX_SUBJECT = 3;
	public static final Integer INDEX_ASSIGNMENTSCORE = 4;
	
	public static final Logger logger = LoggerFactory.getLogger("checkListRG");
	
	public static boolean areEqualString(String str1, String str2) {
		return str1.equalsIgnoreCase(str2);
	}
	
	public String validateFileExtension(String fileName) {
		String fileExtension = "";
		if(fileName.endsWith(FILE_EXTENSION_XLS)) {
			fileExtension = FILE_EXTENSION_XLS;
		} else if(fileName.endsWith(FILE_EXTENSION_XLSX)) {
			fileExtension = FILE_EXTENSION_XLSX;
		}
		return fileExtension;
	}
	
	public Iterator<Row> extractIterator(String fileExtension, byte[] byteArr) throws IOException {
		XSSFSheet sheet = null;
		HSSFSheet sheetH = null;
		Workbook workbook = null;
		HSSFWorkbook workbookH = null;
		ByteArrayInputStream bis = null;
		Iterator<Row> rowIterator = null;
		//try {
			if (areEqualString(fileExtension, FILE_EXTENSION_XLS)) {
				bis = new ByteArrayInputStream(byteArr);
				workbookH = new HSSFWorkbook(bis);
				sheetH = workbookH.getSheetAt(0);
				rowIterator = sheetH.iterator();
			} else if (areEqualString(fileExtension, FILE_EXTENSION_XLSX)) {
				bis = new ByteArrayInputStream(byteArr);
				workbook = new XSSFWorkbook(bis);
				sheet = (XSSFSheet) workbook.getSheetAt(0);
				rowIterator = sheet.iterator();
			}
		//} catch(IOException e) {
			//throw e;
		//}
		return rowIterator;
	}
	
	public List<RemarksGradeBean> processRemarkGradingUGMarksFile(CommonsMultipartFile fileData) throws IOException {
		logger.info("Entering GradingExcelHelper : processRemarkGradingUGMarksFile");
		String fileExtension = null;
		Iterator<Row> rowIterator = null;
		Row row = null;
		
		String studentNumber = null;
		String studentName = null;
		String program = null;
		String subject = null;
		String assignmentScore = null;
		
		RemarksGradeBean obj = null;
		List<RemarksGradeBean> list = null;
		//try {
			fileExtension = this.validateFileExtension(fileData.getOriginalFilename());
			rowIterator = extractIterator(fileExtension, fileData.getBytes());

			if (rowIterator.hasNext()) {
				row = rowIterator.next();
				logger.info("GradingExcelHelper : processRemarkGradingUGMarksFile : Phys Cell count : " + row.getPhysicalNumberOfCells());
				logger.info("GradingExcelHelper : processRemarkGradingUGMarksFile : 1 Cell : " + row.getCell(1) + " : " + row.getCell(1).getStringCellValue());
			}
			list = new ArrayList<RemarksGradeBean>();
			while (rowIterator.hasNext()) {
				row = rowIterator.next();
				if (null != row) {
					row.getCell(INDEX_STUDENTNUMBER, Row.CREATE_NULL_AS_BLANK).setCellType(Cell.CELL_TYPE_STRING);
					row.getCell(INDEX_STUDENTNAME, Row.CREATE_NULL_AS_BLANK).setCellType(Cell.CELL_TYPE_STRING);
					row.getCell(INDEX_PROGRAM, Row.CREATE_NULL_AS_BLANK).setCellType(Cell.CELL_TYPE_STRING);
					row.getCell(INDEX_SUBJECT, Row.CREATE_NULL_AS_BLANK).setCellType(Cell.CELL_TYPE_STRING);
					row.getCell(INDEX_ASSIGNMENTSCORE, Row.CREATE_NULL_AS_BLANK).setCellType(Cell.CELL_TYPE_STRING);

					studentNumber = row.getCell(INDEX_STUDENTNUMBER, Row.CREATE_NULL_AS_BLANK).getStringCellValue();
					studentName = row.getCell(INDEX_STUDENTNAME, Row.CREATE_NULL_AS_BLANK).getStringCellValue();
					program = row.getCell(INDEX_PROGRAM, Row.CREATE_NULL_AS_BLANK).getStringCellValue();
					subject = row.getCell(INDEX_SUBJECT, Row.CREATE_NULL_AS_BLANK).getStringCellValue();
					assignmentScore = row.getCell(INDEX_ASSIGNMENTSCORE, Row.CREATE_NULL_AS_BLANK).getStringCellValue();
					
					logger.info("GradingExcelHelper : processRemarkGradingUGMarksFile : " + studentNumber + " : " + studentName + " : " + program + " : " + subject+ " : " + assignmentScore);
					
					if(isBlank(studentNumber) && isBlank(studentName) && isBlank(program) && isBlank(subject) && isBlank(assignmentScore)) {
						logger.info("GradingExcelHelper : processRemarkGradingUGMarksFile : EXITING AS ROW found EMPTY. Current List size : "+list.size());
						break;
					}
					
					obj = new RemarksGradeBean();
					obj.setSapid(studentNumber);
					obj.setProgram(program);
					obj.setSubject(subject);
					//obj.setScoreIA(assignmentScore);
					if(!isBlank(assignmentScore)) {
						obj.setScoreIA(toInteger(assignmentScore));
					}
					
					list.add(obj);
				}
			}
		//} catch (IOException e) {
			//throw e;
		//}
		return list;
	}

	public static boolean isBlank(String arg) {
		return StringUtils.isBlank(arg);
	}
	
	public static Integer toInteger(String arg) {
		return Integer.valueOf(arg);
	}
}