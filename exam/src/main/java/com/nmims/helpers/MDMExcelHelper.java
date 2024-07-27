/**
 * 
 */
package com.nmims.helpers;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

import com.nmims.beans.MDMSubjectCodeBean;
import com.nmims.beans.MDMSubjectCodeMappingBean;

/**
 * @author vil_m
 *
 */
@Component
public class MDMExcelHelper {

	@Autowired(required=false)
	ApplicationContext act;
	
	public static final String FILE_EXTENSION_XLS = "xls";
	public static final String FILE_EXTENSION_XLSX = "xlsx";
	
	public static final Integer INDEX_SRNO = 0;
	public static final Integer INDEX_SUBJECTCODE = 1;
	public static final Integer INDEX_SUBJECTNAME = 2;
	public static final Integer INDEX_COMMONSUBJECT = 3;
	public static final Integer INDEX_ACTIVE = 4;
	public static final Integer INDEX_ISPROJECT = 5;
	public static final Integer INDEX_SPECIALIZATION = 6;
	public static final Integer INDEX_STUDENT_TYPE = 7;
	public static final Integer INDEX_DESCRIPTION = 8;
	public static final Integer INDEX_SESSION_TIME = 9;
	
	//MDMSubjectCodeMapping
	public static final Integer INDEX_CONSUMER_TYPE = 1;
	public static final Integer INDEX_PROGRAM_STRUCTURE = 2;
	public static final Integer INDEX_PROGRAM_NAME = 3;
	public static final Integer INDEX_SEMESTER = 4;
	public static final Integer INDEX_SUBJECT_CODE = 5;
	public static final Integer INDEX_ACTIVE_STATUS = 6;
	public static final Integer INDEX_SIFY_SUBJECTCODE = 7;
	public static final Integer INDEX_PROGRAM_PASS_SCORE = 8;
	public static final Integer INDEX_HASIA = 9;
	public static final Integer INDEX_HASTEST = 10;
	public static final Integer INDEX_HAS_ASSIGNMENT = 11;
	public static final Integer INDEX_ASSIGNMENT_NEEDED_WRITTEN = 12;
	public static final Integer INDEX_ASSIGNMENT_SCORE_MODEL = 13;
	public static final Integer INDEX_WRITTEN_SCORE_MODEL = 14;
	public static final Integer INDEX_CREATE_CASE_FOR_QUERY = 15;
	public static final Integer INDEX_ASSIGN_QUERY_TO_FACULTY = 16;
	public static final Integer INDEX_IS_GRACE_APPLICABLE = 17;
	public static final Integer INDEX_MAX_GRACE_MARKS = 18;
	
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
		try {
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
		} catch(IOException e) {
			throw e;
		}
		return rowIterator;
	}
	
	public List<MDMSubjectCodeBean> processMDMSubjectCodeFile(MDMSubjectCodeBean bean) throws IOException {
		String fileExtension = null;
		Iterator<Row> rowIterator = null;
		Row row = null;
		Double srno = null;
		//String sifySubjectCode = null;
		String subjCode = null;
		String subjName = null;
		String commSubj = null;
		String actStat = null;
		String isProj = null;
		String specializ = null;
		String studType = null;
		String descrip = null;
		String sessionTime = null;
		MDMSubjectCodeBean obj = null;
		List<MDMSubjectCodeBean> list = null;
		try {
			fileExtension = this.validateFileExtension(bean.getFileData().getOriginalFilename());
			rowIterator = extractIterator(fileExtension, bean.getFileData().getBytes());

			if (rowIterator.hasNext()) {
				row = rowIterator.next();
			}
			list = new ArrayList<MDMSubjectCodeBean>();
			while (rowIterator.hasNext()) {
				row = rowIterator.next();
				if (null != row) {
					row.getCell(INDEX_SRNO, Row.CREATE_NULL_AS_BLANK).setCellType(Cell.CELL_TYPE_NUMERIC);
					row.getCell(INDEX_SUBJECTCODE, Row.CREATE_NULL_AS_BLANK).setCellType(Cell.CELL_TYPE_STRING);
					row.getCell(INDEX_SUBJECTNAME, Row.CREATE_NULL_AS_BLANK).setCellType(Cell.CELL_TYPE_STRING);
					row.getCell(INDEX_COMMONSUBJECT, Row.CREATE_NULL_AS_BLANK).setCellType(Cell.CELL_TYPE_STRING);
					row.getCell(INDEX_ACTIVE, Row.CREATE_NULL_AS_BLANK).setCellType(Cell.CELL_TYPE_STRING);
					row.getCell(INDEX_ISPROJECT, Row.CREATE_NULL_AS_BLANK).setCellType(Cell.CELL_TYPE_STRING);
					//row.getCell(INDEX_SIFY_SUBJECTCODE, Row.CREATE_NULL_AS_BLANK).setCellType(Cell.CELL_TYPE_STRING);
					row.getCell(INDEX_SPECIALIZATION, Row.CREATE_NULL_AS_BLANK).setCellType(Cell.CELL_TYPE_STRING);
					row.getCell(INDEX_STUDENT_TYPE, Row.CREATE_NULL_AS_BLANK).setCellType(Cell.CELL_TYPE_STRING);
					row.getCell(INDEX_DESCRIPTION, Row.CREATE_NULL_AS_BLANK).setCellType(Cell.CELL_TYPE_STRING);
					row.getCell(INDEX_SESSION_TIME, Row.CREATE_NULL_AS_BLANK).setCellType(Cell.CELL_TYPE_STRING);

					srno = row.getCell(INDEX_SRNO, Row.CREATE_NULL_AS_BLANK).getNumericCellValue();
					subjCode = row.getCell(INDEX_SUBJECTCODE, Row.CREATE_NULL_AS_BLANK).getStringCellValue();
					subjName = row.getCell(INDEX_SUBJECTNAME, Row.CREATE_NULL_AS_BLANK).getStringCellValue();
					commSubj = row.getCell(INDEX_COMMONSUBJECT, Row.CREATE_NULL_AS_BLANK).getStringCellValue();
					actStat = row.getCell(INDEX_ACTIVE, Row.CREATE_NULL_AS_BLANK).getStringCellValue();
					isProj = row.getCell(INDEX_ISPROJECT, Row.CREATE_NULL_AS_BLANK).getStringCellValue();
					//sifySubjectCode = row.getCell(INDEX_SIFY_SUBJECTCODE, Row.CREATE_NULL_AS_BLANK)
							//.getStringCellValue();
					specializ = row.getCell(INDEX_SPECIALIZATION, Row.CREATE_NULL_AS_BLANK).getStringCellValue();
					studType = row.getCell(INDEX_STUDENT_TYPE, Row.CREATE_NULL_AS_BLANK).getStringCellValue();
					descrip = row.getCell(INDEX_DESCRIPTION, Row.CREATE_NULL_AS_BLANK).getStringCellValue();
					sessionTime = row.getCell(INDEX_SESSION_TIME, Row.CREATE_NULL_AS_BLANK).getStringCellValue();

					obj = new MDMSubjectCodeBean();
					obj.setSubjectcode(subjCode);
					obj.setSubjectname(subjName);
					obj.setCommonSubject(commSubj);
					obj.setActive(actStat);
					obj.setIsProject(isProj);
					/*if(null != sifySubjectCode && !isBlank(sifySubjectCode)) {
						obj.setSifySubjectCode(toInteger(sifySubjectCode));
					}*/
					obj.setSpecializationType(specializ);
					obj.setStudentType(studType);
					obj.setDescription(descrip);
					
					if(null != sessionTime && !isBlank(sessionTime)) {
						obj.setSessionTime(Integer.valueOf(sessionTime));
					}
					list.add(obj);
				}
			}
		} catch (IOException e) {
			throw e;
		}
		return list;
	}
	
	public List<MDMSubjectCodeMappingBean> processMDMSubjectCodeMappingFile(MDMSubjectCodeMappingBean bean) throws IOException {
		String fileExtension = null;
		Iterator<Row> rowIterator = null;
		Row row = null;
		Double srno = null;
		String sem = null;
		String sifySubjectCode = null;
		String progPassScore = null;
		String consType = null;
		String progStruc = null;
		String progCode = null;
		String subjCode = null;
		String actStat = null;
		String hasIA = null;
		String hasTest = null;
		String hasAssign = null;
		String assignNeededWritt = null;
		String assignScore = null;
		String writtScore = null;
		String createCase = null;
		String assignQFacul = null;
		String isGrace = null;
		String maxGrace = null;
		MDMSubjectCodeMappingBean obj = null;
		List<MDMSubjectCodeMappingBean> list = null;
		try {
			fileExtension = this.validateFileExtension(bean.getFileData().getOriginalFilename());
			rowIterator = extractIterator(fileExtension, bean.getFileData().getBytes());

			if (rowIterator.hasNext()) {
				row = rowIterator.next();
			}
			list = new ArrayList<MDMSubjectCodeMappingBean>();
			while (rowIterator.hasNext()) {
				row = rowIterator.next();
				if (null != row) {
					row.getCell(INDEX_SRNO, Row.CREATE_NULL_AS_BLANK).setCellType(Cell.CELL_TYPE_NUMERIC);
					row.getCell(INDEX_CONSUMER_TYPE, Row.CREATE_NULL_AS_BLANK).setCellType(Cell.CELL_TYPE_STRING);
					row.getCell(INDEX_PROGRAM_STRUCTURE, Row.CREATE_NULL_AS_BLANK).setCellType(Cell.CELL_TYPE_STRING);
					row.getCell(INDEX_PROGRAM_NAME, Row.CREATE_NULL_AS_BLANK).setCellType(Cell.CELL_TYPE_STRING);
					row.getCell(INDEX_SEMESTER, Row.CREATE_NULL_AS_BLANK).setCellType(Cell.CELL_TYPE_STRING);
					row.getCell(INDEX_SUBJECT_CODE, Row.CREATE_NULL_AS_BLANK).setCellType(Cell.CELL_TYPE_STRING);
					row.getCell(INDEX_ACTIVE_STATUS, Row.CREATE_NULL_AS_BLANK).setCellType(Cell.CELL_TYPE_STRING);
					row.getCell(INDEX_SIFY_SUBJECTCODE, Row.CREATE_NULL_AS_BLANK).setCellType(Cell.CELL_TYPE_STRING);
					row.getCell(INDEX_PROGRAM_PASS_SCORE, Row.CREATE_NULL_AS_BLANK).setCellType(Cell.CELL_TYPE_STRING);
					row.getCell(INDEX_HASIA, Row.CREATE_NULL_AS_BLANK).setCellType(Cell.CELL_TYPE_STRING);
					row.getCell(INDEX_HASTEST, Row.CREATE_NULL_AS_BLANK).setCellType(Cell.CELL_TYPE_STRING);
					row.getCell(INDEX_HAS_ASSIGNMENT, Row.CREATE_NULL_AS_BLANK).setCellType(Cell.CELL_TYPE_STRING);
					row.getCell(INDEX_ASSIGNMENT_NEEDED_WRITTEN, Row.CREATE_NULL_AS_BLANK).setCellType(Cell.CELL_TYPE_STRING);
					row.getCell(INDEX_ASSIGNMENT_SCORE_MODEL, Row.CREATE_NULL_AS_BLANK).setCellType(Cell.CELL_TYPE_STRING);
					row.getCell(INDEX_WRITTEN_SCORE_MODEL, Row.CREATE_NULL_AS_BLANK).setCellType(Cell.CELL_TYPE_STRING);
					row.getCell(INDEX_CREATE_CASE_FOR_QUERY, Row.CREATE_NULL_AS_BLANK).setCellType(Cell.CELL_TYPE_STRING);
					row.getCell(INDEX_ASSIGN_QUERY_TO_FACULTY, Row.CREATE_NULL_AS_BLANK).setCellType(Cell.CELL_TYPE_STRING);
					row.getCell(INDEX_IS_GRACE_APPLICABLE, Row.CREATE_NULL_AS_BLANK).setCellType(Cell.CELL_TYPE_STRING);
					row.getCell(INDEX_MAX_GRACE_MARKS, Row.CREATE_NULL_AS_BLANK).setCellType(Cell.CELL_TYPE_STRING);

					srno = row.getCell(INDEX_SRNO, Row.CREATE_NULL_AS_BLANK).getNumericCellValue();
					consType = row.getCell(INDEX_CONSUMER_TYPE, Row.CREATE_NULL_AS_BLANK).getStringCellValue();
					progStruc = row.getCell(INDEX_PROGRAM_STRUCTURE, Row.CREATE_NULL_AS_BLANK).getStringCellValue();
					progCode = row.getCell(INDEX_PROGRAM_NAME, Row.CREATE_NULL_AS_BLANK).getStringCellValue();
					sem = row.getCell(INDEX_SEMESTER, Row.CREATE_NULL_AS_BLANK).getStringCellValue();
					subjCode = row.getCell(INDEX_SUBJECT_CODE, Row.CREATE_NULL_AS_BLANK).getStringCellValue();
					actStat = row.getCell(INDEX_ACTIVE_STATUS, Row.CREATE_NULL_AS_BLANK)
							.getStringCellValue();
					sifySubjectCode = row.getCell(INDEX_SIFY_SUBJECTCODE, Row.CREATE_NULL_AS_BLANK)
							.getStringCellValue();
					progPassScore = row.getCell(INDEX_PROGRAM_PASS_SCORE, Row.CREATE_NULL_AS_BLANK).getStringCellValue();
					hasIA = row.getCell(INDEX_HASIA, Row.CREATE_NULL_AS_BLANK).getStringCellValue();
					hasTest = row.getCell(INDEX_HASTEST, Row.CREATE_NULL_AS_BLANK).getStringCellValue();
					hasAssign = row.getCell(INDEX_HAS_ASSIGNMENT, Row.CREATE_NULL_AS_BLANK).getStringCellValue();
					assignNeededWritt = row.getCell(INDEX_ASSIGNMENT_NEEDED_WRITTEN, Row.CREATE_NULL_AS_BLANK).getStringCellValue();
					assignScore = row.getCell(INDEX_ASSIGNMENT_SCORE_MODEL, Row.CREATE_NULL_AS_BLANK).getStringCellValue();
					writtScore = row.getCell(INDEX_WRITTEN_SCORE_MODEL, Row.CREATE_NULL_AS_BLANK).getStringCellValue();
					createCase = row.getCell(INDEX_CREATE_CASE_FOR_QUERY, Row.CREATE_NULL_AS_BLANK).getStringCellValue();
					assignQFacul = row.getCell(INDEX_ASSIGN_QUERY_TO_FACULTY, Row.CREATE_NULL_AS_BLANK)
							.getStringCellValue();
					isGrace = row.getCell(INDEX_IS_GRACE_APPLICABLE, Row.CREATE_NULL_AS_BLANK).getStringCellValue();
					maxGrace = row.getCell(INDEX_MAX_GRACE_MARKS, Row.CREATE_NULL_AS_BLANK).getStringCellValue();


					obj = new MDMSubjectCodeMappingBean();
					obj.setConsumerTypeId(consType);
					obj.setProgramStructureId(progStruc);
					obj.setProgramId(progCode);
					obj.setSubjectCodeId(subjCode);
					obj.setActive(actStat);
					if(null != sem && !isBlank(sem)) {
						obj.setSem(toString(toInteger(sem)));
					}
					
					if(null != sifySubjectCode && !isBlank(sifySubjectCode)) {
						obj.setSifySubjectCode(toInteger(sifySubjectCode));
					}
					if(null != progPassScore && !isBlank(progPassScore)) {
						obj.setPassScore(toInteger(progPassScore));
					}
					if(null != maxGrace && !isBlank(maxGrace)) {
						obj.setMaxGraceMarks(toInteger(maxGrace));
					}
					obj.setHasIA(hasIA);
					obj.setHasTest(hasTest);
					obj.setHasAssignment(hasAssign);
					obj.setAssignmentNeededBeforeWritten(assignNeededWritt);
					obj.setAssignmentScoreModel(assignScore);
					obj.setWrittenScoreModel(writtScore);
					obj.setCreateCaseForQuery(createCase);
					obj.setAssignQueryToFaculty(assignQFacul);
					obj.setIsGraceApplicable(isGrace);
					list.add(obj);
				}
			}
		} catch (IOException e) {
			throw e;
		}
		return list;
	}
	
	/*public static Integer toInteger(String arg) {
		return Integer.valueOf(arg);
	}*/

	public static boolean isBlank(String arg) {
		return StringUtils.isBlank(arg);
	}
	
	public static Integer toInteger(String arg) {
		return Double.valueOf(arg).intValue();
	}
	
	public static String toString(Integer arg) {
		return String.valueOf(arg);
	}
}