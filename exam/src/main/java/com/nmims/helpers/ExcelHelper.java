package com.nmims.helpers;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.security.GeneralSecurityException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.servlet.ServletContext;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringEscapeUtils;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.hssf.usermodel.HSSFDateUtil;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.hssf.util.CellRangeAddress;
import org.apache.poi.poifs.crypt.Decryptor;
import org.apache.poi.poifs.crypt.EncryptionInfo;
import org.apache.poi.poifs.filesystem.NPOIFSFileSystem;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.DateUtil;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.streaming.SXSSFSheet;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.jsoup.helper.StringUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import com.google.gson.Gson;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.commons.CommonsMultipartFile;

import com.nmims.beans.AssignmentFileBean;
import com.nmims.beans.AssignmentFilesSetbean;
import com.nmims.beans.AssignmentStatusBean;
import com.nmims.beans.BodBean;
import com.nmims.beans.CenterExamBean;
import com.nmims.beans.BlockStudentExamCenterBean;
import com.nmims.beans.ExamBookingTransactionBean;
import com.nmims.beans.ExamCenterBean;
import com.nmims.beans.ExamsAssessmentsBean;
import com.nmims.beans.ExecutiveExamCenter;
import com.nmims.beans.FailedRegistrationBean;
import com.nmims.beans.FailedregistrationExcelBean;
import com.nmims.beans.FileBean;
import com.nmims.beans.LostFocusLogExamBean;
import com.nmims.beans.MBACentersBean;
import com.nmims.beans.MBALiveSettings;
import com.nmims.beans.MBATimeTableBean;
import com.nmims.beans.MettlPGResponseBean;
import com.nmims.beans.MettlRegisterCandidateBean;
import com.nmims.beans.MettlResultsSyncBean;
import com.nmims.beans.OnlineExamMarksBean;
import com.nmims.beans.ProjectConfiguration;
import com.nmims.beans.ProjectTitle;
import com.nmims.beans.OperationsRevenueBean;
import com.nmims.beans.ProgramExamBean;
import com.nmims.beans.ProgramSubjectMappingExamBean;
import com.nmims.beans.ProjectConfiguration;
import com.nmims.beans.ProjectTitle;
import com.nmims.beans.QuestionFileBean;
import com.nmims.beans.StudentExamBean;
import com.nmims.beans.StudentMarksBean;
import com.nmims.beans.StudentSubjectConfigExamBean;
import com.nmims.beans.TEEResultBean;
import com.nmims.beans.TcsOnlineExamBean;
import com.nmims.beans.TestQuestionExamBean;
import com.nmims.beans.TestQuestionOptionExamBean;
import com.nmims.beans.TestTypeBean;
import com.nmims.beans.TestWeightageBean;
import com.nmims.beans.TimeBoundUserMapping;
import com.nmims.beans.TimetableBean;
import com.nmims.beans.UFMNoticeBean;
import com.nmims.daos.AssignmentsDAO;
import com.nmims.daos.DashboardDAO;
import com.nmims.daos.ExamsAssessmentsDAO;
import com.nmims.daos.ServiceRequestDAO;
import com.nmims.daos.StudentMarksDAO;
import com.nmims.daos.TCSApiDAO;
import com.nmims.util.StringUtility;


@Service("excelHelper")
public class ExcelHelper {
	
	@Autowired
	TCSApiDAO tcsDAO;
	
	@Autowired 
	private ExamsAssessmentsDAO examsAssessmentsDAO;
	
	public static final Logger timeboundProjectMarksUploadLogger = LoggerFactory.getLogger("timeboundProjectMarksUpload");
	
	public static final Logger ufm = LoggerFactory.getLogger("ufm");
	
	private static final Logger assignmentCopyCaseLogger = LoggerFactory.getLogger("assignmentCopyCase");
	
	String fileToSave = "D:/";

	private ArrayList<String> yearList = new ArrayList<String>(Arrays.asList( 
			"2000","2001","2002","2003","2004","2005","2006","2007",
			"2008","2009","2010","2011","2012","2013","2014","2015","2016","2017", "2018", "2019", "2020", "2021", "2022", "2023", "2024")); 


	//by Vilpesh 2022-02-25
	public static final Logger logger = LoggerFactory.getLogger(ExcelHelper.class); 
	
	public static final Logger applybodPG = LoggerFactory.getLogger("applybod-PG");
	
	private static final Logger examRegisterPG = LoggerFactory.getLogger("examRegisterPG");
	
	private static final int SAP_ID_LENGTH = 11;
	private static final String STATUS_NV = "NV";
	private static final String STATUS_HASH_CC = "#CC";
	private static final String STATUS_AB = "AB";
	private static final String STATUS_RIA = "RIA";
	
	public ArrayList<List> readMarksExcel(FileBean fileBean, ArrayList<String> programList, ArrayList<String> subjectList, String userId){

		int  YEAR_INDEX = 0;
		int  MONTH_INDEX = 1;
		int  SYLLABUS_YEAR_INDEX = 2;
		int  GRNO_INDEX = 3;
		int  SAPID_INDEX = 4;
		int  NAME_INDEX = 5;
		int  PROGRAM_INDEX = 6;
		int  SEM_INDEX = 7;
		int  SUBJECT_INDEX = 8;
		int  WRITTEN_SCORE_INDEX = 9;
		int  ASSIGNMENT_INDEX = 10;
		int  GRACE_INDEX = 11;
		int  TOTAL_INDEX = 12;
		int  ATTEMPT_INDEX = 13;
		int  SOURCE_INDEX = 14;
		int  LOCATION_INDEX = 15;
		int  CENTER_CODE_INDEX = 16;
		int  REMARKS_INDEX = 17;

		ByteArrayInputStream bis = new ByteArrayInputStream(fileBean.getFileData().getBytes());
		Workbook workbook;
		List<StudentMarksBean> marksBeanList = new ArrayList<StudentMarksBean>();
		List<StudentMarksBean> errorBeanList = new ArrayList<StudentMarksBean>();
		ArrayList<List> resultList = new ArrayList<>();

		try {
			if (fileBean.getFileData().getOriginalFilename().endsWith("xls")) {
				workbook = new HSSFWorkbook(bis);
			} else if (fileBean.getFileData().getOriginalFilename().endsWith("xlsx")) {
				workbook = new XSSFWorkbook(bis);
			} else {
				throw new IllegalArgumentException("Received file does not have a standard excel extension.");
			}

			//Get first/desired sheet from the workbook
			XSSFSheet sheet = (XSSFSheet) workbook.getSheetAt(0);

			Iterator<Row> rowIterator = sheet.iterator();

			int i = 0;
			//Skip first row since it contains column names, not data.
			if(rowIterator.hasNext()){
				Row row = rowIterator.next();
			}
			while(rowIterator.hasNext()) {
				i++;
				Row row = rowIterator.next();


				if(row!=null){
					row.getCell(YEAR_INDEX, Row.CREATE_NULL_AS_BLANK).setCellType(Cell.CELL_TYPE_STRING);
					row.getCell(MONTH_INDEX, Row.CREATE_NULL_AS_BLANK).setCellType(Cell.CELL_TYPE_STRING);
					row.getCell(SYLLABUS_YEAR_INDEX, Row.CREATE_NULL_AS_BLANK).setCellType(Cell.CELL_TYPE_STRING);
					row.getCell(GRNO_INDEX, Row.CREATE_NULL_AS_BLANK).setCellType(Cell.CELL_TYPE_STRING);
					row.getCell(SAPID_INDEX, Row.CREATE_NULL_AS_BLANK).setCellType(Cell.CELL_TYPE_STRING);
					row.getCell(NAME_INDEX, Row.CREATE_NULL_AS_BLANK).setCellType(Cell.CELL_TYPE_STRING);
					row.getCell(PROGRAM_INDEX, Row.CREATE_NULL_AS_BLANK).setCellType(Cell.CELL_TYPE_STRING);
					row.getCell(SEM_INDEX, Row.CREATE_NULL_AS_BLANK).setCellType(Cell.CELL_TYPE_STRING);
					row.getCell(SUBJECT_INDEX, Row.CREATE_NULL_AS_BLANK).setCellType(Cell.CELL_TYPE_STRING);
					row.getCell(WRITTEN_SCORE_INDEX, Row.CREATE_NULL_AS_BLANK).setCellType(Cell.CELL_TYPE_STRING);
					row.getCell(ASSIGNMENT_INDEX, Row.CREATE_NULL_AS_BLANK).setCellType(Cell.CELL_TYPE_STRING);
					row.getCell(GRACE_INDEX, Row.CREATE_NULL_AS_BLANK).setCellType(Cell.CELL_TYPE_STRING);
					row.getCell(TOTAL_INDEX, Row.CREATE_NULL_AS_BLANK).setCellType(Cell.CELL_TYPE_STRING);
					row.getCell(ATTEMPT_INDEX, Row.CREATE_NULL_AS_BLANK).setCellType(Cell.CELL_TYPE_STRING);
					row.getCell(SOURCE_INDEX, Row.CREATE_NULL_AS_BLANK).setCellType(Cell.CELL_TYPE_STRING);
					row.getCell(LOCATION_INDEX, Row.CREATE_NULL_AS_BLANK).setCellType(Cell.CELL_TYPE_STRING);
					row.getCell(CENTER_CODE_INDEX, Row.CREATE_NULL_AS_BLANK).setCellType(Cell.CELL_TYPE_STRING);
					row.getCell(REMARKS_INDEX, Row.CREATE_NULL_AS_BLANK).setCellType(Cell.CELL_TYPE_STRING);

					StudentMarksBean bean = new StudentMarksBean();

					String year = row.getCell(YEAR_INDEX, Row.CREATE_NULL_AS_BLANK).getStringCellValue();
					String month = row.getCell(MONTH_INDEX, Row.CREATE_NULL_AS_BLANK).getStringCellValue();
					String syllabusYear = row.getCell(SYLLABUS_YEAR_INDEX, Row.CREATE_NULL_AS_BLANK).getStringCellValue();
					String grno = row.getCell(GRNO_INDEX, Row.CREATE_NULL_AS_BLANK).getStringCellValue();
					String sapid = row.getCell(SAPID_INDEX, Row.CREATE_NULL_AS_BLANK).getStringCellValue();
					String studentname = row.getCell(NAME_INDEX, Row.CREATE_NULL_AS_BLANK).getStringCellValue();
					String program = row.getCell(PROGRAM_INDEX, Row.CREATE_NULL_AS_BLANK).getStringCellValue();
					String sem = row.getCell(SEM_INDEX, Row.CREATE_NULL_AS_BLANK).getStringCellValue();
					String subject = row.getCell(SUBJECT_INDEX, Row.CREATE_NULL_AS_BLANK).getStringCellValue();
					String writenscore = row.getCell(WRITTEN_SCORE_INDEX, Row.CREATE_NULL_AS_BLANK).getStringCellValue();
					String assignmentscore = row.getCell(ASSIGNMENT_INDEX, Row.CREATE_NULL_AS_BLANK).getStringCellValue();
					String gracemarks = row.getCell(GRACE_INDEX, Row.CREATE_NULL_AS_BLANK).getStringCellValue();
					String total = row.getCell(TOTAL_INDEX, Row.CREATE_NULL_AS_BLANK).getStringCellValue();
					String attempt = row.getCell(ATTEMPT_INDEX, Row.CREATE_NULL_AS_BLANK).getStringCellValue();
					String source = row.getCell(SOURCE_INDEX, Row.CREATE_NULL_AS_BLANK).getStringCellValue();
					String location = row.getCell(LOCATION_INDEX, Row.CREATE_NULL_AS_BLANK).getStringCellValue();
					String centercode = row.getCell(CENTER_CODE_INDEX, Row.CREATE_NULL_AS_BLANK).getStringCellValue();
					String remarks = row.getCell(REMARKS_INDEX, Row.CREATE_NULL_AS_BLANK).getStringCellValue();

					if("".equals(year.trim()) && "".equals(month.trim()) && "".equals(sapid.trim()) && "".equals(subject.trim()) && "".equals(program)){
						break;
					}

					bean.setYear(year.trim());
					bean.setMonth(month.trim());
					bean.setSyllabusYear(syllabusYear);
					//bean.setExamorder((int)row.get("examorder")+"");
					bean.setGrno(grno.trim());
					bean.setSapid(sapid.trim());
					bean.setStudentname(studentname.trim());
					bean.setProgram(program.trim());
					bean.setSem(sem.trim());
					bean.setSubject(subject.trim());
					bean.setWritenscore(writenscore);
					bean.setAssignmentscore(assignmentscore);
					bean.setGracemarks(gracemarks);
					bean.setTotal(total);
					bean.setAttempt(attempt);
					bean.setSource(source);
					bean.setLocation(location);
					bean.setCentercode(centercode);
					bean.setRemarks(remarks);
					bean.setCreatedBy(userId);
					bean.setLastModifiedBy(userId);

					validateSAPIDInMarksBean(bean, i);
					if(!subjectList.contains(bean.getSubject())){
						bean.setErrorMessage(bean.getErrorMessage()+" Row : "+ (i+1) +" Invalid Subject for record with SAPID:"+bean.getSapid()+ " & SUBJECT:"+bean.getSubject());
						bean.setErrorRecord(true);
					}
					if(!programList.contains(bean.getProgram())){
						bean.setErrorMessage(bean.getErrorMessage()+" Row : "+ (i+1) +" Invalid Program for record with SAPID:"+bean.getSapid()+ " & PROGRAM:"+bean.getProgram());
						bean.setErrorRecord(true);
					}
					if(!(yearList.contains(bean.getYear()) && ("Jun".equals(bean.getMonth()) || "Dec".equals(bean.getMonth())))){
						bean.setErrorMessage(bean.getErrorMessage()+" Row : "+ (i+1) +" Invalid Year/Month for record with SAPID:"+bean.getSapid());
						bean.setErrorRecord(true);
					}

					if(bean.isErrorRecord()){
						errorBeanList.add(bean);
					}else{
						marksBeanList.add(bean);
					}


					if(i % 1000 == 0){
					}

				}
			}
		} catch (IOException e) {
			
		}
		resultList.add(marksBeanList);
		resultList.add(errorBeanList);
		return resultList;
	}

	public ArrayList<List> readWrittenMarksExcel(FileBean fileBean, ArrayList<String> programList, ArrayList<String> subjectList, String userId){

		//int  YEAR_INDEX = 0;
		//int  MONTH_INDEX = 1;
		//int  SYLLABUS_YEAR_INDEX = 2;
		//int  GRNO_INDEX = 3;
		int  SAPID_INDEX = 2;
		int  NAME_INDEX = 1;
		int  PROGRAM_INDEX = 3;
		int  SEM_INDEX = 4;
		int  SUBJECT_INDEX = 5;
		int  WRITTEN_SCORE_INDEX = 6;
		int  REMARKS_INDEX = 7;
	//	int  STUDENT_TYPE_INDEX = 7;
		//int  ASSIGNMENT_INDEX = 10;
		//int  GRACE_INDEX = 11;
		//int  TOTAL_INDEX = 12;
		//int  ATTEMPT_INDEX = 13;
		//int  SOURCE_INDEX = 14;
		//int  LOCATION_INDEX = 15;
		//int  CENTER_CODE_INDEX = 16;
		//int  REMARKS_INDEX = 17;

		ByteArrayInputStream bis = new ByteArrayInputStream(fileBean.getFileData().getBytes());
		Workbook workbook;
		List<StudentMarksBean> marksBeanList = new ArrayList<StudentMarksBean>();
		List<StudentMarksBean> errorBeanList = new ArrayList<StudentMarksBean>();
		ArrayList<List> resultList = new ArrayList<>();
		HashMap<String, String> sapIdSujectKeys = new HashMap<>();

		try {
			if (fileBean.getFileData().getOriginalFilename().endsWith("xls")) {
				workbook = new HSSFWorkbook(bis);
			} else if (fileBean.getFileData().getOriginalFilename().endsWith("xlsx")) {
				workbook = new XSSFWorkbook(bis);
			} else {
				throw new IllegalArgumentException("Received file does not have a standard excel extension.");
			}

			//Get first/desired sheet from the workbook
			XSSFSheet sheet = (XSSFSheet) workbook.getSheetAt(0);

			Iterator<Row> rowIterator = sheet.iterator();

			StudentMarksBean bean = null;//shifted Vilpesh 2022-02-25
			int i = 0;
			//Skip first row since it contains column names, not data.
			if(rowIterator.hasNext()){
				Row row = rowIterator.next();
			}
			while(rowIterator.hasNext()) {
				i++;
				Row row = rowIterator.next();


				if(row!=null){
					//row.getCell(YEAR_INDEX, Row.CREATE_NULL_AS_BLANK).setCellType(Cell.CELL_TYPE_STRING);
					//row.getCell(MONTH_INDEX, Row.CREATE_NULL_AS_BLANK).setCellType(Cell.CELL_TYPE_STRING);
					//row.getCell(SYLLABUS_YEAR_INDEX, Row.CREATE_NULL_AS_BLANK).setCellType(Cell.CELL_TYPE_STRING);
					//row.getCell(GRNO_INDEX, Row.CREATE_NULL_AS_BLANK).setCellType(Cell.CELL_TYPE_STRING);
					row.getCell(SAPID_INDEX, Row.CREATE_NULL_AS_BLANK).setCellType(Cell.CELL_TYPE_STRING);
					row.getCell(NAME_INDEX, Row.CREATE_NULL_AS_BLANK).setCellType(Cell.CELL_TYPE_STRING);
					row.getCell(PROGRAM_INDEX, Row.CREATE_NULL_AS_BLANK).setCellType(Cell.CELL_TYPE_STRING);
					row.getCell(SEM_INDEX, Row.CREATE_NULL_AS_BLANK).setCellType(Cell.CELL_TYPE_STRING);
					row.getCell(SUBJECT_INDEX, Row.CREATE_NULL_AS_BLANK).setCellType(Cell.CELL_TYPE_STRING);
					row.getCell(WRITTEN_SCORE_INDEX, Row.CREATE_NULL_AS_BLANK).setCellType(Cell.CELL_TYPE_STRING);
					row.getCell(REMARKS_INDEX, Row.CREATE_NULL_AS_BLANK).setCellType(Cell.CELL_TYPE_STRING);
			//		row.getCell(STUDENT_TYPE_INDEX, Row.CREATE_NULL_AS_BLANK).setCellType(Cell.CELL_TYPE_STRING);
					//row.getCell(ASSIGNMENT_INDEX, Row.CREATE_NULL_AS_BLANK).setCellType(Cell.CELL_TYPE_STRING);
					//row.getCell(GRACE_INDEX, Row.CREATE_NULL_AS_BLANK).setCellType(Cell.CELL_TYPE_STRING);
					//row.getCell(TOTAL_INDEX, Row.CREATE_NULL_AS_BLANK).setCellType(Cell.CELL_TYPE_STRING);
					//row.getCell(ATTEMPT_INDEX, Row.CREATE_NULL_AS_BLANK).setCellType(Cell.CELL_TYPE_STRING);
					//row.getCell(SOURCE_INDEX, Row.CREATE_NULL_AS_BLANK).setCellType(Cell.CELL_TYPE_STRING);
					//row.getCell(LOCATION_INDEX, Row.CREATE_NULL_AS_BLANK).setCellType(Cell.CELL_TYPE_STRING);
					//row.getCell(CENTER_CODE_INDEX, Row.CREATE_NULL_AS_BLANK).setCellType(Cell.CELL_TYPE_STRING);
					//row.getCell(REMARKS_INDEX, Row.CREATE_NULL_AS_BLANK).setCellType(Cell.CELL_TYPE_STRING);

					bean = new StudentMarksBean();

					//String year = row.getCell(YEAR_INDEX, Row.CREATE_NULL_AS_BLANK).getStringCellValue();
					//String month = row.getCell(MONTH_INDEX, Row.CREATE_NULL_AS_BLANK).getStringCellValue();
					//String syllabusYear = row.getCell(SYLLABUS_YEAR_INDEX, Row.CREATE_NULL_AS_BLANK).getStringCellValue();
					//String grno = row.getCell(GRNO_INDEX, Row.CREATE_NULL_AS_BLANK).getStringCellValue();
					String sapid = row.getCell(SAPID_INDEX, Row.CREATE_NULL_AS_BLANK).getStringCellValue();
					String studentname = row.getCell(NAME_INDEX, Row.CREATE_NULL_AS_BLANK).getStringCellValue();
					String program = row.getCell(PROGRAM_INDEX, Row.CREATE_NULL_AS_BLANK).getStringCellValue();
					String sem = row.getCell(SEM_INDEX, Row.CREATE_NULL_AS_BLANK).getStringCellValue();
					String subject = row.getCell(SUBJECT_INDEX, Row.CREATE_NULL_AS_BLANK).getStringCellValue();
					String writenscore = row.getCell(WRITTEN_SCORE_INDEX, Row.CREATE_NULL_AS_BLANK).getStringCellValue();
					String remarks = row.getCell(REMARKS_INDEX, Row.CREATE_NULL_AS_BLANK).getStringCellValue();
			//		String studentType = row.getCell(STUDENT_TYPE_INDEX, Row.CREATE_NULL_AS_BLANK).getStringCellValue();
					//String assignmentscore = row.getCell(ASSIGNMENT_INDEX, Row.CREATE_NULL_AS_BLANK).getStringCellValue();
					//String gracemarks = row.getCell(GRACE_INDEX, Row.CREATE_NULL_AS_BLANK).getStringCellValue();
					//String total = row.getCell(TOTAL_INDEX, Row.CREATE_NULL_AS_BLANK).getStringCellValue();
					//String attempt = row.getCell(ATTEMPT_INDEX, Row.CREATE_NULL_AS_BLANK).getStringCellValue();
					//String source = row.getCell(SOURCE_INDEX, Row.CREATE_NULL_AS_BLANK).getStringCellValue();
					//String location = row.getCell(LOCATION_INDEX, Row.CREATE_NULL_AS_BLANK).getStringCellValue();
					//String centercode = row.getCell(CENTER_CODE_INDEX, Row.CREATE_NULL_AS_BLANK).getStringCellValue();
					//String remarks = row.getCell(REMARKS_INDEX, Row.CREATE_NULL_AS_BLANK).getStringCellValue();

					if("".equals(sapid.trim()) && "".equals(subject.trim()) && "".equals(program)){
						break;
					}

					bean.setYear(fileBean.getYear());
					bean.setMonth(fileBean.getMonth());
					//bean.setSyllabusYear(syllabusYear);
					//bean.setExamorder((int)row.get("examorder")+"");
					bean.setGrno("Not Available");
					bean.setSapid(sapid.trim());
					bean.setStudentname(studentname.trim());
					bean.setProgram(program.trim());
					bean.setSem(sem.trim());
					bean.setSubject(subject.trim());
					bean.setWritenscore(writenscore);

					bean.setAssignmentscore("");
				    bean.setRemarks(remarks);
					bean.setGracemarks("");
					bean.setTotal("");
					bean.setAttempt("");
					bean.setSource("");
					bean.setLocation("");
					bean.setCentercode("");
					//bean.setStudentType("");
					
					
					bean.setCreatedBy(userId);
					bean.setLastModifiedBy(userId);

					validateSAPIDInMarksBean(bean, i);
					validateWrittenMarks(bean, i);
					if("".equals(bean.getWritenscore().trim())){
						bean.setErrorMessage(bean.getErrorMessage()+" Row : "+ (i+1) +" Blank written marks for record with SAPID:"+bean.getSapid()+ " & SUBJECT:"+bean.getSubject());
						bean.setErrorRecord(true);
					}
					//validateAssignmentMarks(bean, i); Not needed
					if(!subjectList.contains(bean.getSubject())){
						bean.setErrorMessage(bean.getErrorMessage()+" Row : "+ (i+1) +" Invalid Subject for record with SAPID:"+bean.getSapid()+ " & SUBJECT:"+bean.getSubject());
						bean.setErrorRecord(true);
					}
					if(!programList.contains(bean.getProgram())){
						bean.setErrorMessage(bean.getErrorMessage()+" Row : "+ (i+1) +" Invalid Program for record with SAPID:"+bean.getSapid()+ " & PROGRAM:"+bean.getProgram());
						bean.setErrorRecord(true);
					}
					/*if(!(yearList.contains(bean.getYear()) && ("Jun".equals(bean.getMonth()) || "Dec".equals(bean.getMonth()) ||  "Sep".equals(bean.getMonth()) ||  "Apr".equals(bean.getMonth())))){
						bean.setErrorMessage(bean.getErrorMessage()+" Row : "+ (i+1) +" Invalid Year/Month for record with SAPID:"+bean.getSapid());
						bean.setErrorRecord(true);
					}*/
//					if("".equals(bean.getStudentType().trim())){
//						bean.setErrorMessage(bean.getErrorMessage()+" Row : "+ (i+1) +" Blank Student Type for record with SAPID:"+bean.getSapid()+ " & SUBJECT:"+bean.getSubject());
//						bean.setErrorRecord(true);
//					}
					if(!sapIdSujectKeys.containsKey(sapid.trim()+subject.trim())){
						sapIdSujectKeys.put(sapid.trim()+subject.trim(), null);
					}else{
						bean.setErrorMessage(bean.getErrorMessage()+" Row : "+ (i+1) +" Duplicate Entry for record with SAPID:"+bean.getSapid()+ " & SUBJECT:"+bean.getSubject());
						bean.setErrorRecord(true);
					}

					if(bean.isErrorRecord()){
						errorBeanList.add(bean);
					}else{
						marksBeanList.add(bean);
					}

					//by Vilpesh 2022-02-25
					//if(i % 1000 == 0){
					//}
				}
			}
		} catch (IOException e) {
			logger.error("readWrittenMarksExcel : "+e.getMessage());//by Vilpesh 2022-02-25
		}
		resultList.add(marksBeanList);
		resultList.add(errorBeanList);
		return resultList;
	}
	
	public ArrayList<TcsOnlineExamBean> readTcsBulkUpdateExamBooking(FileBean fileBean){
		Integer UniqueRequestId = 0;
		Integer ExamYearIndex = 1;
		Integer ExamMonthIndex = 2;
		Integer ProgramIndex = 3;
		Integer SapIdIndex = 4;
		Integer SubjectIndex = 7;
		Integer FirstNameIndex = 10;
		Integer LastNameIndex = 11;
		Integer ExamDateIndex = 13 ;
		Integer ExamTimeIndex = 14;
		Integer ExamCenterIdIndex = 15;
		Integer BulkActionIndex = 17 ;
		ArrayList<TcsOnlineExamBean> tcsOnlineExamBeanList = new ArrayList<TcsOnlineExamBean>();
		try{

			ByteArrayInputStream bis = new ByteArrayInputStream(fileBean.getFileData().getBytes());
			Workbook workbook;
			
			
			if (fileBean.getFileData().getOriginalFilename().endsWith("xls")) {
				workbook = new HSSFWorkbook(bis);
			} else if (fileBean.getFileData().getOriginalFilename().endsWith("xlsx")) {
				workbook = new XSSFWorkbook(bis);
			} else {
				throw new IllegalArgumentException("Received file does not have a standard excel extension.");
			}
			
			
			XSSFSheet sheet = (XSSFSheet) workbook.getSheetAt(0);

			Iterator<Row> rowIterator = sheet.iterator();

			int i = 0;
			//Skip first row since it contains column names, not data.
			if(rowIterator.hasNext()){
				Row row = rowIterator.next();
			}
			while(rowIterator.hasNext()){
				i++;
				Row row = rowIterator.next();
				TcsOnlineExamBean tcsOnlineExamBean = new TcsOnlineExamBean();
				
				if(row!=null){
					
					row.getCell(UniqueRequestId, Row.CREATE_NULL_AS_BLANK).setCellType(Cell.CELL_TYPE_STRING);
					row.getCell(ExamYearIndex, Row.CREATE_NULL_AS_BLANK).setCellType(Cell.CELL_TYPE_STRING);
					row.getCell(ExamMonthIndex, Row.CREATE_NULL_AS_BLANK).setCellType(Cell.CELL_TYPE_STRING);
					row.getCell(SapIdIndex, Row.CREATE_NULL_AS_BLANK).setCellType(Cell.CELL_TYPE_STRING);
					row.getCell(SubjectIndex, Row.CREATE_NULL_AS_BLANK).setCellType(Cell.CELL_TYPE_STRING);
					
					row.getCell(ProgramIndex, Row.CREATE_NULL_AS_BLANK).setCellType(Cell.CELL_TYPE_STRING);
					row.getCell(FirstNameIndex, Row.CREATE_NULL_AS_BLANK).setCellType(Cell.CELL_TYPE_STRING);
					row.getCell(LastNameIndex, Row.CREATE_NULL_AS_BLANK).setCellType(Cell.CELL_TYPE_STRING);
					
					
					row.getCell(ExamCenterIdIndex, Row.CREATE_NULL_AS_BLANK).setCellType(Cell.CELL_TYPE_STRING);
					row.getCell(BulkActionIndex, Row.CREATE_NULL_AS_BLANK).setCellType(Cell.CELL_TYPE_STRING);
					
					
					String uniqueRequestId = row.getCell(UniqueRequestId, Row.CREATE_NULL_AS_BLANK).getStringCellValue();
					String examYear = row.getCell(ExamYearIndex, Row.CREATE_NULL_AS_BLANK).getStringCellValue();
					String examMonth = row.getCell(ExamMonthIndex, Row.CREATE_NULL_AS_BLANK).getStringCellValue();
					String sapid = row.getCell(SapIdIndex, Row.CREATE_NULL_AS_BLANK).getStringCellValue();
					String subject = row.getCell(SubjectIndex, Row.CREATE_NULL_AS_BLANK).getStringCellValue();
					
					String program = row.getCell(ProgramIndex, Row.CREATE_NULL_AS_BLANK).getStringCellValue();
					String firstName = row.getCell(FirstNameIndex, Row.CREATE_NULL_AS_BLANK).getStringCellValue();
					String lastName = row.getCell(LastNameIndex, Row.CREATE_NULL_AS_BLANK).getStringCellValue();
					
//					Date date = row.getCell(ExamDateIndex, Row.CREATE_NULL_AS_BLANK).getDateCellValue();
//					String examTime = row.getCell(ExamTimeIndex, Row.CREATE_NULL_AS_BLANK).getStringCellValue();
					String centerId = row.getCell(ExamCenterIdIndex, Row.CREATE_NULL_AS_BLANK).getStringCellValue();
					String bulkAction = row.getCell(BulkActionIndex, Row.CREATE_NULL_AS_BLANK).getStringCellValue();
					
//					if("".equals(sapid) || sapid==null || "".equals(centerId) || centerId ==null){
//						errorBeanList.add(examCenterBean);
//					}
					Date date = null, startTime = null;
					String examDate, examTime;
					Cell dateCell = row.getCell(ExamDateIndex);
					Cell startTimeCell = row.getCell(ExamTimeIndex);
					

					if(dateCell.getCellType() == Cell.CELL_TYPE_NUMERIC){
						if (DateUtil.isCellDateFormatted(dateCell)) {
							date = dateCell.getDateCellValue();
						} else {
							date = HSSFDateUtil.getJavaDate(dateCell.getNumericCellValue());
						}
						SimpleDateFormat sdfDate = new SimpleDateFormat("yyyy-MM-dd");
						 examDate = sdfDate.format(date);

					}else {
						row.getCell(ExamDateIndex, Row.CREATE_NULL_AS_BLANK).setCellType(Cell.CELL_TYPE_STRING);
						examDate = row.getCell(ExamDateIndex, Row.CREATE_NULL_AS_BLANK).getStringCellValue();
					}

					if(startTimeCell.getCellType() == Cell.CELL_TYPE_NUMERIC){
						if (DateUtil.isCellDateFormatted(startTimeCell)) {
							startTime = startTimeCell.getDateCellValue();
						} else {
							startTime = HSSFDateUtil.getJavaDate(startTimeCell.getNumericCellValue());
						}
						SimpleDateFormat sdfTime = new java.text.SimpleDateFormat("HH:mm:ss");
						 examTime = sdfTime.format(startTime);

					}else {
						row.getCell(ExamTimeIndex, Row.CREATE_NULL_AS_BLANK).setCellType(Cell.CELL_TYPE_STRING);
						examTime = row.getCell(ExamTimeIndex, Row.CREATE_NULL_AS_BLANK).getStringCellValue();
					}
					if( "UPDATE".equalsIgnoreCase(bulkAction)  ) {
					
					String examCenterName = tcsDAO.getCenterNameForPreivew(centerId);
					tcsOnlineExamBean.setUniqueRequestId(uniqueRequestId);
					tcsOnlineExamBean.setExamYear(examYear);
					tcsOnlineExamBean.setExamMonth(examMonth);
					tcsOnlineExamBean.setUserId(sapid);
					tcsOnlineExamBean.setSubject(subject);
					tcsOnlineExamBean.setProgram(program);
					tcsOnlineExamBean.setFirstName(firstName);
					tcsOnlineExamBean.setLastName(lastName);
					tcsOnlineExamBean.setExamCenterName(examCenterName);
					tcsOnlineExamBean.setExamDate(examDate);
					tcsOnlineExamBean.setExamTime(examTime);
					tcsOnlineExamBean.setCenterId(centerId);
					tcsOnlineExamBean.setBulkAction(bulkAction);
					tcsOnlineExamBeanList.add(tcsOnlineExamBean);
					}
					

					if(i % 1000 == 0){
					}
				}
				
			}
			
			
		}catch(Exception e){
			
		}
		return tcsOnlineExamBeanList;
	}
	
	public ArrayList<List> readCenterUserExcel(FileBean fileBean){
		int SAPID_INDEX = 0;
		int CENTER_ID = 1;
		ByteArrayInputStream bis = new ByteArrayInputStream(fileBean.getFileData().getBytes());
		Workbook workbook;
		
		List<ExamCenterBean> centerUserMappingList = new ArrayList<ExamCenterBean>();
		List<ExamCenterBean> errorBeanList = new ArrayList<ExamCenterBean>();
		
		ArrayList<List> resultList = new ArrayList<>();
		try{
			if (fileBean.getFileData().getOriginalFilename().endsWith("xls")) {
				workbook = new HSSFWorkbook(bis);
			} else if (fileBean.getFileData().getOriginalFilename().endsWith("xlsx")) {
				workbook = new XSSFWorkbook(bis);
			} else {
				throw new IllegalArgumentException("Received file does not have a standard excel extension.");
			}
			
			
			XSSFSheet sheet = (XSSFSheet) workbook.getSheetAt(0);

			Iterator<Row> rowIterator = sheet.iterator();

			int i = 0;
			//Skip first row since it contains column names, not data.
			if(rowIterator.hasNext()){
				Row row = rowIterator.next();
			}
			while(rowIterator.hasNext()){
				i++;
				Row row = rowIterator.next();
				ExamCenterBean examCenterBean = new ExamCenterBean();
				
				if(row!=null){


					row.getCell(SAPID_INDEX, Row.CREATE_NULL_AS_BLANK).setCellType(Cell.CELL_TYPE_STRING);
					row.getCell(CENTER_ID, Row.CREATE_NULL_AS_BLANK).setCellType(Cell.CELL_TYPE_STRING);
					
		
					String sapid = row.getCell(SAPID_INDEX, Row.CREATE_NULL_AS_BLANK).getStringCellValue();
					String centerId = row.getCell(CENTER_ID, Row.CREATE_NULL_AS_BLANK).getStringCellValue();
					
					if("".equals(sapid) || sapid==null || "".equals(centerId) || centerId ==null){
						errorBeanList.add(examCenterBean);
					}
					
					
					examCenterBean.setYear(fileBean.getYear());
					examCenterBean.setMonth(fileBean.getMonth());
					examCenterBean.setSapid(sapid);
					examCenterBean.setCenterId(centerId);
					
					

					if(i % 1000 == 0){
					}
				}
				centerUserMappingList.add(examCenterBean);
			}
			
			
		}catch(Exception e){
			
		}
		resultList.add(centerUserMappingList);
		resultList.add(errorBeanList);
		return resultList;
	}

	public ArrayList<List> readTimeTableExcel(FileBean fileBean, ArrayList<String> programList, ArrayList<String> subjectList, String userId){

		int  PROGRAM_INDEX = 0;
		int  SEM_INDEX = 1;
		int  SUBJECT_INDEX = 2;
		int  DATE_INDEX = 3;
		int  START_TIME_INDEX = 4;
		int  END_TIME_INDEX = 5;
		int  MODE_INDEX = 6;


		ByteArrayInputStream bis = new ByteArrayInputStream(fileBean.getFileData().getBytes());
		Workbook workbook;
		List<TimetableBean> marksBeanList = new ArrayList<TimetableBean>();
		List<TimetableBean> errorBeanList = new ArrayList<TimetableBean>();
		ArrayList<List> resultList = new ArrayList<>();
		HashMap<String, String> programSubjectKeys = new HashMap<>();
		HashMap<String, String> subjectDateMap = new HashMap<>();

		try {
			if (fileBean.getFileData().getOriginalFilename().endsWith("xls")) {
				workbook = new HSSFWorkbook(bis);
			} else if (fileBean.getFileData().getOriginalFilename().endsWith("xlsx")) {
				workbook = new XSSFWorkbook(bis);
			} else {
				throw new IllegalArgumentException("Received file does not have a standard excel extension.");
			}

			//Get first/desired sheet from the workbook
			XSSFSheet sheet = (XSSFSheet) workbook.getSheetAt(0);

			Iterator<Row> rowIterator = sheet.iterator();

			int i = 0;
			//Skip first row since it contains column names, not data.
			if(rowIterator.hasNext()){
				Row row = rowIterator.next();
			}
			while(rowIterator.hasNext()) {
				i++;
				Row row = rowIterator.next();


				if(row!=null){


					row.getCell(PROGRAM_INDEX, Row.CREATE_NULL_AS_BLANK).setCellType(Cell.CELL_TYPE_STRING);
					row.getCell(SEM_INDEX, Row.CREATE_NULL_AS_BLANK).setCellType(Cell.CELL_TYPE_STRING);
					row.getCell(SUBJECT_INDEX, Row.CREATE_NULL_AS_BLANK).setCellType(Cell.CELL_TYPE_STRING);
					row.getCell(MODE_INDEX, Row.CREATE_NULL_AS_BLANK).setCellType(Cell.CELL_TYPE_STRING);
					
					

					TimetableBean bean = new TimetableBean();


					String program = row.getCell(PROGRAM_INDEX, Row.CREATE_NULL_AS_BLANK).getStringCellValue();
					String sem = row.getCell(SEM_INDEX, Row.CREATE_NULL_AS_BLANK).getStringCellValue();
					String subject = row.getCell(SUBJECT_INDEX, Row.CREATE_NULL_AS_BLANK).getStringCellValue();
					String mode = row.getCell(MODE_INDEX, Row.CREATE_NULL_AS_BLANK).getStringCellValue();

					if("".equals(program.trim()) && "".equals(sem.trim()) && "".equals(subject)){
						break;
					}

					Date date = null, startTime = null, endTime = null;
					Cell dateCell = row.getCell(DATE_INDEX);
					Cell startTimeCell = row.getCell(START_TIME_INDEX);
					Cell endTimeCell = row.getCell(END_TIME_INDEX);

					if(dateCell.getCellType() == Cell.CELL_TYPE_NUMERIC){
						if (DateUtil.isCellDateFormatted(dateCell)) {
							date = dateCell.getDateCellValue();
						} else {
							date = HSSFDateUtil.getJavaDate(dateCell.getNumericCellValue());
						}
					}else{
						bean.setErrorMessage(bean.getErrorMessage()+" Row : "+ (i+1) +" Invalid Format of cell : Not a date ");
						bean.setErrorRecord(true);
					}

					if(startTimeCell.getCellType() == Cell.CELL_TYPE_NUMERIC){
						if (DateUtil.isCellDateFormatted(startTimeCell)) {
							startTime = startTimeCell.getDateCellValue();
						} else {
							startTime = HSSFDateUtil.getJavaDate(startTimeCell.getNumericCellValue());
						}
					}else{
						bean.setErrorMessage(bean.getErrorMessage()+" Row : "+ (i+1) +" Invalid Format Start time cell : Not a date/time ");
						bean.setErrorRecord(true);
					}


					if(endTimeCell.getCellType() == Cell.CELL_TYPE_NUMERIC){
						if (DateUtil.isCellDateFormatted(endTimeCell)) {
							endTime = endTimeCell.getDateCellValue();
						} else {
							endTime = HSSFDateUtil.getJavaDate(endTimeCell.getNumericCellValue());
						}
					}else{
						bean.setErrorMessage(bean.getErrorMessage()+" Row : "+ (i+1) +" Invalid Format End time cell : Not a date/time ");
						bean.setErrorRecord(true);
					}

					if(bean.isErrorRecord()){
						errorBeanList.add(bean);
						continue;
					}

					SimpleDateFormat sdfDate = new SimpleDateFormat("yyyy-MM-dd");
					String examDate = sdfDate.format(date);

					SimpleDateFormat sdfTime = new java.text.SimpleDateFormat("HH:mm");

					bean.setExamYear(fileBean.getYear());
					bean.setExamMonth(fileBean.getMonth());
					bean.setPrgmStructApplicable(fileBean.getPrgmStructApplicable());
					bean.setProgram(program.trim());
					bean.setSem(sem.trim());
					bean.setSubject(subject.trim());
					bean.setDate(examDate);
					bean.setStartTime(sdfTime.format(startTime));
					bean.setEndTime(sdfTime.format(endTime));
					bean.setMode(mode);	
					bean.setCreatedBy(userId);
					bean.setLastModifiedBy(userId);
					
					if(!"".equals(fileBean.getIc()) && fileBean.getIc()!=null){
						bean.setIc(fileBean.getIc());
					}

					if((!"Online".equals(mode)) && (!"Offline".equals(mode))){
						bean.setErrorMessage(bean.getErrorMessage()+" Row : "+ (i+1) +" Invalid Exam Mode. Mode can be Online/Offline ");
						bean.setErrorRecord(true);
					}
					
					
					if(!(yearList.contains(bean.getExamYear()) 
							&& ("Jun".equals(bean.getExamMonth()) || "Dec".equals(bean.getExamMonth()) || "Sep".equals(bean.getExamMonth()) || "Apr".equals(bean.getExamMonth())))){
						bean.setErrorMessage(bean.getErrorMessage()+" Row : "+ (i+1) +" Invalid Year/Month for record with SAPID:");
						bean.setErrorRecord(true);
					}

					if(!"NA".equals(program.trim())){//NA mean timetable for Re-sit exam where program and subject is not applicable
						
						if(!subjectList.contains(bean.getSubject())){
							bean.setErrorMessage(bean.getErrorMessage()+" Row : "+ (i+1) +" Invalid Subject ");
							bean.setErrorRecord(true);
						}
						
						if(!programList.contains(bean.getProgram())){
							bean.setErrorMessage(bean.getErrorMessage()+" Row : "+ (i+1) +" Invalid Program ");
							bean.setErrorRecord(true);
						}
						
						
						
						
						if(!subjectDateMap.containsKey(subject.trim())){
							subjectDateMap.put(subject.trim(), examDate);
						}else{
							String examDateForSubject = subjectDateMap.get(subject.trim());
							if(!examDateForSubject.equals(examDate) && (!"PGDMM-MLI".equals(program))){
								bean.setErrorMessage(bean.getErrorMessage()+" Row : "+ (i+1) +" Different exam dates for same SUBJECT:"+bean.getSubject());
								bean.setErrorRecord(true);
							}

						}
						
					}

					

					if(bean.isErrorRecord()){
						errorBeanList.add(bean);
					}else{
						marksBeanList.add(bean);
					}


					if(i % 1000 == 0){
					}
				}
			}
		} catch (IOException e) {
			
		}
		resultList.add(marksBeanList);
		resultList.add(errorBeanList);
		return resultList;
	}

	public ArrayList<List> readAssignmentStatusExcel(FileBean fileBean, ArrayList<String> programList, ArrayList<String> subjectList, String userId){

		int  SAPID_INDEX = 0;
		int  SUBJECT_INDEX = 1;
		int  SUBMITTED_INDEX = 2;


		ByteArrayInputStream bis = new ByteArrayInputStream(fileBean.getFileData().getBytes());
		Workbook workbook;
		List<AssignmentStatusBean> assignmentStatusList = new ArrayList<AssignmentStatusBean>();
		List<AssignmentStatusBean> errorBeanList = new ArrayList<AssignmentStatusBean>();
		ArrayList<List> resultList = new ArrayList<>();
		HashMap<String, String> sapidSubjectKeys = new HashMap<>();
		HashMap<String, String> subjectDateMap = new HashMap<>();

		try {
			if (fileBean.getFileData().getOriginalFilename().endsWith("xls")) {
				workbook = new HSSFWorkbook(bis);
			} else if (fileBean.getFileData().getOriginalFilename().endsWith("xlsx")) {
				workbook = new XSSFWorkbook(bis);
			} else {
				throw new IllegalArgumentException("Received file does not have a standard excel extension.");
			}

			//Get first/desired sheet from the workbook
			XSSFSheet sheet = (XSSFSheet) workbook.getSheetAt(0);

			Iterator<Row> rowIterator = sheet.iterator();

			int i = 0;
			//Skip first row since it contains column names, not data.
			if(rowIterator.hasNext()){
				Row row = rowIterator.next();
			}
			while(rowIterator.hasNext()) {
				i++;
				Row row = rowIterator.next();


				if(row!=null){


					row.getCell(SAPID_INDEX, Row.CREATE_NULL_AS_BLANK).setCellType(Cell.CELL_TYPE_STRING);
					row.getCell(SUBMITTED_INDEX, Row.CREATE_NULL_AS_BLANK).setCellType(Cell.CELL_TYPE_STRING);
					row.getCell(SUBJECT_INDEX, Row.CREATE_NULL_AS_BLANK).setCellType(Cell.CELL_TYPE_STRING);

					AssignmentStatusBean bean = new AssignmentStatusBean();


					String sapid = row.getCell(SAPID_INDEX, Row.CREATE_NULL_AS_BLANK).getStringCellValue();
					String status = row.getCell(SUBMITTED_INDEX, Row.CREATE_NULL_AS_BLANK).getStringCellValue();
					String subject = row.getCell(SUBJECT_INDEX, Row.CREATE_NULL_AS_BLANK).getStringCellValue();

					if("".equals(sapid.trim()) && "".equals(status.trim()) && "".equals(subject)){
						break;
					}



					bean.setExamYear(fileBean.getYear());
					bean.setExamMonth(fileBean.getMonth());
					bean.setSapid(sapid.trim());
					bean.setSubject(subject.trim());
					bean.setSubmitted(status);					
					bean.setCreatedBy(userId);



					if(sapid.length() != 11){
						bean.setErrorMessage(bean.getErrorMessage()+" Row : "+ (i+1) +" Invalid SAPID '"+sapid+"'");
						bean.setErrorRecord(true);
					}
					if(!subjectList.contains(bean.getSubject())){
						bean.setErrorMessage(bean.getErrorMessage()+" Row : "+ (i+1) +" Invalid Subject ");
						bean.setErrorRecord(true);
					}
					if(!("Y".equals(status) || "N".equals(status))){
						bean.setErrorMessage(bean.getErrorMessage()+" Row : "+ (i+1) +" Invalid Assignment Submitted Status.");
						bean.setErrorRecord(true);
					}


					if(!sapidSubjectKeys.containsKey(sapid.trim()+subject.trim())){
						sapidSubjectKeys.put(sapid.trim()+subject.trim(), null);
					}else{
						bean.setErrorMessage(bean.getErrorMessage()+" Row : "+ (i+1) +" Duplicate Entry SAPID:"+bean.getSapid()+ " & SUBJECT:"+bean.getSubject());
						bean.setErrorRecord(true);
					}

					if(bean.isErrorRecord()){
						errorBeanList.add(bean);
					}else{
						assignmentStatusList.add(bean);
					}


					if(i % 1000 == 0){
					}
				}
			}
		} catch (IOException e) {
			
		}
		resultList.add(assignmentStatusList);
		resultList.add(errorBeanList);
		return resultList;
	}


	public ArrayList<List> readExamFeeExemptExcel(FileBean fileBean, String userId){

		int  SAPID_INDEX = 0;
		int  SEM_INDEX = 1;



		ByteArrayInputStream bis = new ByteArrayInputStream(fileBean.getFileData().getBytes());
		Workbook workbook;
		List<StudentMarksBean> exemptFeesList = new ArrayList<StudentMarksBean>();
		List<StudentMarksBean> errorBeanList = new ArrayList<StudentMarksBean>();
		ArrayList<List> resultList = new ArrayList<>();
		HashMap<String, String> sapidSubjectKeys = new HashMap<>();
		HashMap<String, String> subjectDateMap = new HashMap<>();

		try {
			if (fileBean.getFileData().getOriginalFilename().endsWith("xls")) {
				workbook = new HSSFWorkbook(bis);
			} else if (fileBean.getFileData().getOriginalFilename().endsWith("xlsx")) {
				workbook = new XSSFWorkbook(bis);
			} else {
				throw new IllegalArgumentException("Received file does not have a standard excel extension.");
			}

			//Get first/desired sheet from the workbook
			XSSFSheet sheet = (XSSFSheet) workbook.getSheetAt(0);

			Iterator<Row> rowIterator = sheet.iterator();

			int i = 0;
			//Skip first row since it contains column names, not data.
			if(rowIterator.hasNext()){
				Row row = rowIterator.next();
			}
			while(rowIterator.hasNext()) {
				i++;
				Row row = rowIterator.next();


				if(row!=null){


					row.getCell(SAPID_INDEX, Row.CREATE_NULL_AS_BLANK).setCellType(Cell.CELL_TYPE_STRING);
					row.getCell(SEM_INDEX, Row.CREATE_NULL_AS_BLANK).setCellType(Cell.CELL_TYPE_STRING);


					StudentMarksBean bean = new StudentMarksBean();


					String sapid = row.getCell(SAPID_INDEX, Row.CREATE_NULL_AS_BLANK).getStringCellValue();
					String sem = row.getCell(SEM_INDEX, Row.CREATE_NULL_AS_BLANK).getStringCellValue();


					if("".equals(sapid.trim()) && "".equals(sem.trim()) ){
						break;
					}

					bean.setYear(fileBean.getYear());
					bean.setMonth(fileBean.getMonth());
					bean.setSapid(sapid.trim());
					bean.setSem(sem.trim());

					bean.setCreatedBy(userId);
					bean.setLastModifiedBy(userId);



					if(sapid.length() != 11){
						bean.setErrorMessage(bean.getErrorMessage()+" Row : "+ (i+1) +" Invalid SAPID '"+sapid+"'");
						bean.setErrorRecord(true);

					}
					if((!"1".equals(sem)) &&  (!"2".equals(sem))  &&   (!"3".equals(sem))  &&  (!"4".equals(sem))){
						bean.setErrorMessage(bean.getErrorMessage()+" Row : "+ (i+1) +" Invalid Semester for record with SAPID:"+bean.getSapid()+ " & Sem:"+sem);
						bean.setErrorRecord(true);
					}


					if(bean.isErrorRecord()){
						errorBeanList.add(bean);
					}else{
						exemptFeesList.add(bean);
					}


					if(i % 1000 == 0){
					}
				}
			}
		} catch (IOException e) {
			
		}
		resultList.add(exemptFeesList);
		resultList.add(errorBeanList);
		return resultList;
	}
	
	public ArrayList<List> readExamFeeExemptSubjectsExcel(FileBean fileBean, String userId, ArrayList<String> subjectList){

		int  SAPID_INDEX = 0;
		int  SUBJECT_INDEX = 1;



		ByteArrayInputStream bis = new ByteArrayInputStream(fileBean.getFileData().getBytes());
		Workbook workbook;
		List<StudentMarksBean> exemptFeesList = new ArrayList<StudentMarksBean>();
		List<StudentMarksBean> errorBeanList = new ArrayList<StudentMarksBean>();
		ArrayList<List> resultList = new ArrayList<>();

		try {
			if (fileBean.getFileData().getOriginalFilename().endsWith("xls")) {
				workbook = new HSSFWorkbook(bis);
			} else if (fileBean.getFileData().getOriginalFilename().endsWith("xlsx")) {
				workbook = new XSSFWorkbook(bis);
			} else {
				throw new IllegalArgumentException("Received file does not have a standard excel extension.");
			}

			//Get first/desired sheet from the workbook
			XSSFSheet sheet = (XSSFSheet) workbook.getSheetAt(0);

			Iterator<Row> rowIterator = sheet.iterator();

			int i = 0;
			//Skip first row since it contains column names, not data.
			if(rowIterator.hasNext()){
				Row row = rowIterator.next();
			}
			while(rowIterator.hasNext()) {
				i++;
				Row row = rowIterator.next();


				if(row!=null){


					row.getCell(SAPID_INDEX, Row.CREATE_NULL_AS_BLANK).setCellType(Cell.CELL_TYPE_STRING);
					row.getCell(SUBJECT_INDEX, Row.CREATE_NULL_AS_BLANK).setCellType(Cell.CELL_TYPE_STRING);


					StudentMarksBean bean = new StudentMarksBean();


					String sapid = row.getCell(SAPID_INDEX, Row.CREATE_NULL_AS_BLANK).getStringCellValue();
					String subject = row.getCell(SUBJECT_INDEX, Row.CREATE_NULL_AS_BLANK).getStringCellValue();


					if("".equals(sapid.trim()) && "".equals(subject.trim()) ){
						break;
					}

					bean.setYear(fileBean.getYear());
					bean.setMonth(fileBean.getMonth());
					bean.setSapid(sapid.trim());
					bean.setSubject(subject.trim());

					bean.setCreatedBy(userId);
					bean.setLastModifiedBy(userId);



					if(sapid.length() != 11){
						bean.setErrorMessage(bean.getErrorMessage()+" Row : "+ (i+1) +" Invalid SAPID '"+sapid+"'");
						bean.setErrorRecord(true);

					}
					
					if(!subjectList.contains(bean.getSubject())){
						bean.setErrorMessage(bean.getErrorMessage()+" Row : "+ (i+1) +" Invalid Subject for record with SAPID:"+bean.getSapid()+ " & SUBJECT:"+bean.getSubject());
						bean.setErrorRecord(true);
					}


					if(bean.isErrorRecord()){
						errorBeanList.add(bean);
					}else{
						exemptFeesList.add(bean);
					}


					if(i % 1000 == 0){
					}
				}
			}
		} catch (IOException e) {
			
		}
		resultList.add(exemptFeesList);
		resultList.add(errorBeanList);
		return resultList;
	}

	public ArrayList<List> readRegistrationExcel(FileBean fileBean, ArrayList<String> programList, ArrayList<String> subjectList, String userId){

		int  SAPID_INDEX = 0;
		int  PROGRAM_INDEX = 1;
		int  SEM_INDEX = 2;
		int  MONTH_INDEX = 3;
		int  YEAR_INDEX = 4;
		int  CENTER_CODE_INDEX = 5;
		int  CENTER_NAME_INDEX = 6;

		ByteArrayInputStream bis = new ByteArrayInputStream(fileBean.getFileData().getBytes());
		Workbook workbook;
		List<StudentMarksBean> marksBeanList = new ArrayList<StudentMarksBean>();
		List<StudentMarksBean> errorBeanList = new ArrayList<StudentMarksBean>();
		ArrayList<List> resultList = new ArrayList<>();
		HashMap<String,String> mapOfSapIdAndYearMonthSemKey = new HashMap<String,String>();
		try {
			if (fileBean.getFileData().getOriginalFilename().endsWith("xls")) {
				workbook = new HSSFWorkbook(bis);
			} else if (fileBean.getFileData().getOriginalFilename().endsWith("xlsx")) {
				workbook = new XSSFWorkbook(bis);
			} else {
				throw new IllegalArgumentException("Received file does not have a standard excel extension.");
			}

			//Get first/desired sheet from the workbook
			XSSFSheet sheet = (XSSFSheet) workbook.getSheetAt(0);
			Iterator<Row> rowIterator = sheet.iterator();

			int i = 0;
			//Skip first row since it contains column names, not data.
			if(rowIterator.hasNext()){
				Row row = rowIterator.next();
			}
			while(rowIterator.hasNext()) {
				i++;
				Row row = rowIterator.next();


				if(row!=null){
					row.getCell(YEAR_INDEX, Row.CREATE_NULL_AS_BLANK).setCellType(Cell.CELL_TYPE_STRING);
					row.getCell(MONTH_INDEX, Row.CREATE_NULL_AS_BLANK).setCellType(Cell.CELL_TYPE_STRING);
					row.getCell(SAPID_INDEX, Row.CREATE_NULL_AS_BLANK).setCellType(Cell.CELL_TYPE_STRING);
					row.getCell(PROGRAM_INDEX, Row.CREATE_NULL_AS_BLANK).setCellType(Cell.CELL_TYPE_STRING);
					row.getCell(SEM_INDEX, Row.CREATE_NULL_AS_BLANK).setCellType(Cell.CELL_TYPE_STRING);
					row.getCell(CENTER_CODE_INDEX, Row.CREATE_NULL_AS_BLANK).setCellType(Cell.CELL_TYPE_STRING);
					row.getCell(CENTER_NAME_INDEX, Row.CREATE_NULL_AS_BLANK).setCellType(Cell.CELL_TYPE_STRING);

					StudentMarksBean bean = new StudentMarksBean();

					String year = row.getCell(YEAR_INDEX, Row.CREATE_NULL_AS_BLANK).getStringCellValue();
					String month = row.getCell(MONTH_INDEX, Row.CREATE_NULL_AS_BLANK).getStringCellValue();
					String sapid = row.getCell(SAPID_INDEX, Row.CREATE_NULL_AS_BLANK).getStringCellValue();
					String program = row.getCell(PROGRAM_INDEX, Row.CREATE_NULL_AS_BLANK).getStringCellValue();
					String sem = row.getCell(SEM_INDEX, Row.CREATE_NULL_AS_BLANK).getStringCellValue();
					String centerCode = row.getCell(CENTER_CODE_INDEX, Row.CREATE_NULL_AS_BLANK).getStringCellValue();
					String centerName = row.getCell(CENTER_NAME_INDEX, Row.CREATE_NULL_AS_BLANK).getStringCellValue();

					if("Jan".equalsIgnoreCase(month)){
						month = "Jan";
					}else if("Jul".equalsIgnoreCase(month)){
						month = "Jul";
					}

					if("".equals(year.trim()) && "".equals(month.trim()) && "".equals(sapid.trim()) && "".equals(sem.trim()) && "".equals(program)){
						break;
					}

					bean.setYear(year.trim());
					bean.setMonth(month.trim());
					bean.setSapid(sapid.trim());
					bean.setProgram(program.trim());
					bean.setSem(sem.trim());
					bean.setCentercode(centerCode);
					bean.setCenterName(centerName);

					bean.setCreatedBy(userId);
					bean.setLastModifiedBy(userId);

					if(sapid.length() != 11){
						bean.setErrorMessage(bean.getErrorMessage()+" Row : "+ (i+1) +" Invalid SAPID '"+sapid+"'");
						bean.setErrorRecord(true);
					}
					if(!programList.contains(bean.getProgram())){
						bean.setErrorMessage(bean.getErrorMessage()+" Row : "+ (i+1) +" Invalid Program for record with SAPID:"+bean.getSapid()+ " & PROGRAM:"+bean.getProgram());
						bean.setErrorRecord(true);
					}
					if(!(yearList.contains(bean.getYear()) && ("Jul".equals(bean.getMonth()) || "Jan".equals(bean.getMonth())))){
						bean.setErrorMessage(bean.getErrorMessage()+" Row : "+ (i+1) +" Invalid Year/Month for record with SAPID:"+bean.getSapid());
						bean.setErrorRecord(true);
					}
					/*if(mapOfSapIdAndYearMonthSemKey.containsKey(bean.getSapid())){
						String keyForCheck = bean.getYear()+"-"+bean.getMonth()+"-"+bean.getSem();
						String uniqueRegistrationKey = mapOfSapIdAndYearMonthSemKey.get(bean.getSapid());
						if(uniqueRegistrationKey.equals(keyForCheck)){
							bean.setErrorMessage(bean.getErrorMessage()+" Row : "+ (i+1) +" Student Number found for same year,month registration for the same semster. SAPID:"+bean.getSapid());
							bean.setErrorRecord(true);
						}
					}*/

					if(bean.isErrorRecord()){
						errorBeanList.add(bean);
					}else{
						marksBeanList.add(bean);
						/*mapOfSapIdAndYearMonthSemKey.put(bean.getSapid(),bean.getYear()+"-"+bean.getMonth()+"-"+bean.getSem());//Add successfull records to a map and keep the value as a unique key of year-month-sem//
*/					}


					if(i % 1000 == 0){
					}

				}
			}
		} catch (IOException e) {
			
		}
		resultList.add(marksBeanList);
		resultList.add(errorBeanList);
		return resultList;
	}

	public ArrayList<List> readOnlineExamMarksExcelWithPassword(FileBean fileBean, String userId, StudentMarksDAO dao) throws Exception{

		ByteArrayInputStream bis = new ByteArrayInputStream(fileBean.getFileData().getBytes());
		Workbook workbook;

		NPOIFSFileSystem fs = new NPOIFSFileSystem(bis);
		EncryptionInfo info = new EncryptionInfo(fs);
		Decryptor d = Decryptor.getInstance(info);
		InputStream dataStream = null;
		try {
			if (!d.verifyPassword(fileBean.getFilePassword())) {
				throw new RuntimeException("Invalid Password. Please re-enter");
			}
			dataStream = d.getDataStream(fs);

		} catch (GeneralSecurityException ex) {
			throw new RuntimeException("Unable to process encrypted document", ex);
		}

		try {

			if (fileBean.getFileData().getOriginalFilename().endsWith("xls")) {
				workbook = new HSSFWorkbook(dataStream);
			} else if (fileBean.getFileData().getOriginalFilename().endsWith("xlsx")) {
				workbook = new XSSFWorkbook(dataStream);
			} else {
				throw new IllegalArgumentException("Received file does not have a standard excel extension.");
			}
			return readOnlineMarksExcel(workbook, fileBean, userId, dao);

		} catch (Exception e) {
			
			throw e;
		}

	}

	public ArrayList<List> readOnlineExamMarksExcelWithoutPassword(FileBean fileBean, String userId, StudentMarksDAO dao) throws Exception{

		ByteArrayInputStream bis = new ByteArrayInputStream(fileBean.getFileData().getBytes());
		Workbook workbook;

		try {

			if (fileBean.getFileData().getOriginalFilename().endsWith("xls")) {
				workbook = new HSSFWorkbook(bis);
			} else if (fileBean.getFileData().getOriginalFilename().endsWith("xlsx")) {
				workbook = new XSSFWorkbook(bis);
			} else {
				throw new IllegalArgumentException("Received file does not have a standard excel extension.");
			}
			return readOnlineMarksExcel(workbook, fileBean, userId, dao);

		} catch (Exception e) {
			
			throw e;
		}

	}
	
	public ArrayList<List> readOnlineRevalWrittenMarks(FileBean fileBean, String userId, StudentMarksDAO dao) throws Exception{

		ByteArrayInputStream bis = new ByteArrayInputStream(fileBean.getFileData().getBytes());
		Workbook workbook;

		try {

			if (fileBean.getFileData().getOriginalFilename().endsWith("xls")) {
				workbook = new HSSFWorkbook(bis);
			} else if (fileBean.getFileData().getOriginalFilename().endsWith("xlsx")) {
				workbook = new XSSFWorkbook(bis);
			} else {
				throw new IllegalArgumentException("Received file does not have a standard excel extension.");
			}
			
			int  SAPID_INDEX = 0;
			int  SECTION4_MARKS_INDEX = 1;
			int REMARKS_INDEX = 2;
			
			List<OnlineExamMarksBean> marksBeanList = new ArrayList<OnlineExamMarksBean>();
			List<OnlineExamMarksBean> errorBeanList = new ArrayList<OnlineExamMarksBean>();
			ArrayList<List> resultList = new ArrayList<>();

			//Get first/desired sheet from the workbook
			XSSFSheet sheet = (XSSFSheet) workbook.getSheetAt(0);
			//org.apache.poi.ss.usermodel.Sheet sheet = workbook.getSheetAt(0);
			Iterator<Row> rowIterator = sheet.iterator();

			int i = 0;
			//Skip first row since it contains column names, not data.
			if(rowIterator.hasNext()){
				Row row = rowIterator.next();
			}
			Pattern p = Pattern.compile("\\d+");
			while(rowIterator.hasNext()) {
				i++;
				Row row = rowIterator.next();


				if(row!=null){
					row.getCell(SAPID_INDEX, Row.CREATE_NULL_AS_BLANK).setCellType(Cell.CELL_TYPE_STRING);
					row.getCell(SECTION4_MARKS_INDEX, Row.CREATE_NULL_AS_BLANK).setCellType(Cell.CELL_TYPE_NUMERIC);
					row.getCell(REMARKS_INDEX, Row.CREATE_NULL_AS_BLANK).setCellType(Cell.CELL_TYPE_STRING);
					
					OnlineExamMarksBean bean = new OnlineExamMarksBean();

					String sapid = row.getCell(SAPID_INDEX, Row.CREATE_NULL_AS_BLANK).getStringCellValue().trim();
					double part4marks = row.getCell(SECTION4_MARKS_INDEX).getNumericCellValue();
					String remarks = row.getCell(REMARKS_INDEX, Row.CREATE_NULL_AS_BLANK).getStringCellValue();
					
					if("".equals(sapid.trim())){
						break;
					}

					bean.setYear(fileBean.getYear());
					bean.setMonth(fileBean.getMonth());
					bean.setSubject(fileBean.getSubject());
					bean.setSapid(sapid.trim());
					bean.setPart4marks(part4marks);
					bean.setRemarks(remarks);
					bean.setCreatedBy(userId);
					bean.setLastModifiedBy(userId);

					sapid = sapid.trim();


					Matcher m = p.matcher(sapid);

					while(m.find()) {
						sapid = m.group();
						break;
					}


					if(sapid.trim().length() != 11){
						bean.setErrorMessage(bean.getErrorMessage()+" Row : "+ (i+1) +" Invalid SAPID '"+sapid+"'");
						bean.setErrorRecord(true);
					}

					//Check if student had registered for exam
					ArrayList<ExamBookingTransactionBean> bookingList = (ArrayList<ExamBookingTransactionBean>)dao.getConfirmedBookingForGivenYearMonth(sapid, fileBean.getSubject(), fileBean.getYear(), fileBean.getMonth());
					if(bookingList == null || bookingList.size() == 0 || bookingList.size() > 1){
						bean.setErrorMessage(bean.getErrorMessage()+" Row : "+ (i+1) +" Exam Registration not found for record with SAPID:"+bean.getSapid()+ " & SUBJECT:"+bean.getSubject());
						bean.setErrorRecord(true);
					}


					//Check if total of 4 sections matches with total given
					double totalForComparision =  part4marks;
					totalForComparision = Math.round(totalForComparision * 100.0) / 100.0;
					
//					//Check if written marks are not more than Max marks 70
//					if(totalForComparision > 20){
//						bean.setErrorMessage(bean.getErrorMessage()+" Row : "+ (i+1) +" Marks cannot be above 20 marks.");
//						bean.setErrorRecord(true);
//					}


					if(bean.isErrorRecord()){
						errorBeanList.add(bean);
					}else{
						marksBeanList.add(bean);
					}


					if(i % 1000 == 0){
					}
				}
			}
			resultList.add(marksBeanList);
			resultList.add(errorBeanList);
			return resultList;

		} catch (Exception e) {
			
			throw e;
		}
 
	}

	private ArrayList<List> readOnlineMarksExcel(Workbook workbook, FileBean fileBean, String userId, StudentMarksDAO dao) {

		int  SAPID_INDEX = 0;
		int  NAME_INDEX = 1;
		int  TOTAL_INDEX = 2;
		int  SECTION1_MARKS_INDEX = 3;
		int  SECTION2_MARKS_INDEX = 4;
		int  SECTION3_MARKS_INDEX = 5;
		int  SECTION4_MARKS_INDEX = 6;
		int  STUDENT_TYPE_INDEX = 7;

		List<OnlineExamMarksBean> marksBeanList = new ArrayList<OnlineExamMarksBean>();
		List<OnlineExamMarksBean> errorBeanList = new ArrayList<OnlineExamMarksBean>();
		ArrayList<List> resultList = new ArrayList<>();

		//Get first/desired sheet from the workbook
		XSSFSheet sheet = (XSSFSheet) workbook.getSheetAt(0);
		//org.apache.poi.ss.usermodel.Sheet sheet = workbook.getSheetAt(0);
		Iterator<Row> rowIterator = sheet.iterator();

		int i = 0;
		//Skip first row since it contains column names, not data.
		if(rowIterator.hasNext()){
			Row row = rowIterator.next();
		}
		Pattern p = Pattern.compile("\\d+");
		while(rowIterator.hasNext()) {
			i++;
			Row row = rowIterator.next();


			if(row!=null){
				row.getCell(SAPID_INDEX, Row.CREATE_NULL_AS_BLANK).setCellType(Cell.CELL_TYPE_STRING);
				row.getCell(NAME_INDEX, Row.CREATE_NULL_AS_BLANK).setCellType(Cell.CELL_TYPE_STRING);
				row.getCell(TOTAL_INDEX, Row.CREATE_NULL_AS_BLANK).setCellType(Cell.CELL_TYPE_NUMERIC);
				row.getCell(SECTION1_MARKS_INDEX, Row.CREATE_NULL_AS_BLANK).setCellType(Cell.CELL_TYPE_NUMERIC);
				row.getCell(SECTION2_MARKS_INDEX, Row.CREATE_NULL_AS_BLANK).setCellType(Cell.CELL_TYPE_NUMERIC);
				row.getCell(SECTION3_MARKS_INDEX, Row.CREATE_NULL_AS_BLANK).setCellType(Cell.CELL_TYPE_NUMERIC);
				row.getCell(SECTION4_MARKS_INDEX, Row.CREATE_NULL_AS_BLANK).setCellType(Cell.CELL_TYPE_NUMERIC);
				row.getCell(STUDENT_TYPE_INDEX, Row.CREATE_NULL_AS_BLANK).setCellType(Cell.CELL_TYPE_STRING);
				
				OnlineExamMarksBean bean = new OnlineExamMarksBean();

				String sapid = row.getCell(SAPID_INDEX, Row.CREATE_NULL_AS_BLANK).getStringCellValue().trim();
				String name = row.getCell(NAME_INDEX, Row.CREATE_NULL_AS_BLANK).getStringCellValue();
				double total = row.getCell(TOTAL_INDEX).getNumericCellValue();
				double part1marks = row.getCell(SECTION1_MARKS_INDEX).getNumericCellValue();
				double part2marks = row.getCell(SECTION2_MARKS_INDEX).getNumericCellValue();
				double part3marks = row.getCell(SECTION3_MARKS_INDEX).getNumericCellValue();
				double part4marks = row.getCell(SECTION4_MARKS_INDEX).getNumericCellValue();
				String studentType = row.getCell(STUDENT_TYPE_INDEX, Row.CREATE_NULL_AS_BLANK).getStringCellValue().trim();
				
				if("".equals(sapid.trim()) && "".equals(name.trim()) ){
					break;
				}

				bean.setYear(fileBean.getYear());
				bean.setMonth(fileBean.getMonth());
				bean.setSubject(fileBean.getSubject());
				bean.setSapid(sapid.trim());
				bean.setName(name);
				bean.setTotal(total);
				bean.setPart1marks(Math.ceil(part1marks));
				bean.setPart2marks(Math.ceil(part2marks));
				bean.setPart3marks(Math.ceil(part3marks));
				bean.setPart4marks(Math.ceil(part4marks));
				bean.setStudentType(studentType);
				bean.setCreatedBy(userId);
				bean.setLastModifiedBy(userId);

				sapid = sapid.trim();


				Matcher m = p.matcher(sapid);

				while(m.find()) {
					sapid = m.group();
					break;
				}


				if(sapid.trim().length() != 11){
					bean.setErrorMessage(bean.getErrorMessage()+" Row : "+ (i+1) +" Invalid SAPID '"+sapid+"'");
					bean.setErrorRecord(true);
				}

				//Check if student had registered for exam
				ArrayList<ExamBookingTransactionBean> bookingList = (ArrayList<ExamBookingTransactionBean>)dao.getConfirmedBookingForGivenYearMonth(sapid, fileBean.getSubject(), fileBean.getYear(), fileBean.getMonth());
				if(bookingList == null || bookingList.size() == 0 || bookingList.size() > 1){
					bean.setErrorMessage(bean.getErrorMessage()+" Row : "+ (i+1) +" Exam Registration not found for record with SAPID:"+bean.getSapid()+ " & SUBJECT:"+bean.getSubject());
					bean.setErrorRecord(true);
				}else{
					bean.setSem(bookingList.get(0).getSem());
					bean.setProgram(bookingList.get(0).getProgram());
				}

				//Check if assignment marks exist for a student before entering written marks
				/*List<String> assignmentMarksList = dao.getAStudentsMarksForSubject(sapid, fileBean.getSubject());
				if(assignmentMarksList == null || assignmentMarksList.size() == 0){
					bean.setErrorMessage(bean.getErrorMessage()+" Row : "+ (i+1) +" Assignment marks not found for record with SAPID:"+bean.getSapid()+ " & SUBJECT:"+bean.getSubject());
					bean.setErrorRecord(true);
				}else{
					boolean hasAssignmentMarks = false;
					for (int j = 0; j < assignmentMarksList.size(); j++) {
						String assignmentMarks = assignmentMarksList.get(j);
						try {
							Double.parseDouble(assignmentMarks);
							hasAssignmentMarks = true;
							break;
						} catch (Exception e) {
						}
					}
					if(!hasAssignmentMarks){
						bean.setErrorMessage(bean.getErrorMessage()+" Row : "+ (i+1) +" Assignment marks not found for record with SAPID:"+bean.getSapid()+ " & SUBJECT:"+bean.getSubject());
						bean.setErrorRecord(true);
					}
				}*/	
				

				//Check if total of 4 sections matches with total given
				double totalForComparision = part1marks + part2marks + part3marks + part4marks;
				totalForComparision = Math.round(totalForComparision * 100.0) / 100.0;
				if(totalForComparision != total){
					bean.setErrorMessage(bean.getErrorMessage()+" Row : "+ (i+1) +" Total of 4 sections "+totalForComparision + " is not matching with total given in file as "+total);
					bean.setErrorRecord(true);
				}else{
					total = bean.getPart1marks() + bean.getPart2marks() + bean.getPart3marks() + bean.getPart4marks();
					if(total < 0){
						int roundedTotal = 0;
						bean.setRoundedTotal(roundedTotal+"");
					}else{
						int roundedTotal = (int) Math.ceil(total);
						bean.setRoundedTotal(roundedTotal+"");
					}
				}

				//Check if written marks are not more than Max marks 70
				if(total > 70){
					bean.setErrorMessage(bean.getErrorMessage()+" Row : "+ (i+1) +" Marks cannot be above 70 marks.");
					bean.setErrorRecord(true);
				}

				//Check if marks are not negative
				/*if(total < 0){
					bean.setErrorMessage(bean.getErrorMessage()+" Row : "+ (i+1) +" Marks cannot Negative.");
					bean.setErrorRecord(true);
				}*/

				if(bean.isErrorRecord()){
					errorBeanList.add(bean);
				}else{
					marksBeanList.add(bean);
				}


				if(i % 1000 == 0){
				}
			}
		}

		resultList.add(marksBeanList);
		resultList.add(errorBeanList);
		return resultList;

	}

	private void validateWrittenMarks(StudentMarksBean bean, int i) {
		String writtenMarks = bean.getWritenscore().trim();
		try {
			int value = Integer.parseInt(writtenMarks);
			String subject = bean.getSubject();
			//Temp: Make this configuation driven
			/*if(value > 70 && (!"Project".equalsIgnoreCase(subject))){
				bean.setErrorMessage(bean.getErrorMessage()+" Row : "+ (i+1) +" Written marks cannot be more than 70,for record with SAPID:"+bean.getSapid()+ " & SUBJECT:"+bean.getSubject());
				bean.setErrorRecord(true);
			}*/
		} catch (Exception e) {
			//if("AB".equals(writtenMarks) || "#CC".equals(writtenMarks) || "NV".equals(writtenMarks) 
			//		|| "".equals(writtenMarks) || "RIA".equals(writtenMarks)) {

			if(STATUS_AB.equals(writtenMarks) || STATUS_HASH_CC.equals(writtenMarks) || STATUS_NV.equals(writtenMarks) 
						|| "".equals(writtenMarks) || STATUS_RIA.equals(writtenMarks)) { //by Vilpesh 2022-02-25
				
			}else{
				logger.error("validateWrittenMarks : "+bean.getErrorMessage()+" Row : "+ (i+1) +" Invalid value '"+writtenMarks+"' for Written Marks for record with SAPID:"+bean.getSapid()+ " & Subject:"+bean.getSubject());
				bean.setErrorMessage(bean.getErrorMessage()+" Row : "+ (i+1) +" Invalid value '"+writtenMarks+"' for Written Marks for record with SAPID:"+bean.getSapid()+ " & Subject:"+bean.getSubject());
				bean.setErrorMessage(bean.getErrorMessage()+" Only Non-numeric valid values are AB/NV/#CC/RIA");
				bean.setErrorRecord(true);
			}
		}

	}

	private void validateAssignmentMarks(StudentMarksBean bean, int i) {
		String asignmentMarks = bean.getAssignmentscore().trim();
		try {
			int value = Integer.parseInt(asignmentMarks);
			if(value > 50){
				bean.setErrorMessage(bean.getErrorMessage()+" Row : "+ (i+1) +" Invalid value '"+asignmentMarks+"' for Assignment Marks for record with SAPID:"+bean.getSapid()+ " & Subject:"+bean.getSubject());
				bean.setErrorRecord(true);
			}
		} catch (Exception e) {
			if("AB".equals(asignmentMarks) || "#CC".equals(asignmentMarks) || "NV".equals(asignmentMarks) || "ANS".equals(asignmentMarks) ) {

			}else{
				bean.setErrorMessage(bean.getErrorMessage()+" Row : "+ (i+1) +" Invalid value '"+asignmentMarks+"' for Assignment Marks for record with SAPID:"+bean.getSapid()+ " & Subject:"+bean.getSubject());
				bean.setErrorMessage(bean.getErrorMessage()+" Only Non-numeric valid values are AB/NV/#CC/ANS");
				bean.setErrorRecord(true);
			}
		}

	}

	public ArrayList<List> readAssignmentMarksExcel(FileBean fileBean, ArrayList<String> programList, ArrayList<String> subjectList,
			ArrayList<ProgramSubjectMappingExamBean> programSubjectMappingList, String userId, StudentMarksDAO dao){

		int  SAPID_INDEX = 0;
		int  NAME_INDEX = 1;
		int  PROGRAM_INDEX = 2;
		int  SUBJECT_INDEX = 3;
		int  ASSIGNMENT_INDEX = 4;
		int  REMARKS_INDEX = 5;


		ByteArrayInputStream bis = new ByteArrayInputStream(fileBean.getFileData().getBytes());
		Workbook workbook;
		List<StudentMarksBean> marksBeanList = new ArrayList<StudentMarksBean>();
		List<StudentMarksBean> errorBeanList = new ArrayList<StudentMarksBean>();
		ArrayList<List> resultList = new ArrayList<>();
		HashMap<String, String> sapIdSujectKeys = new HashMap<>();

		try {
			if (fileBean.getFileData().getOriginalFilename().endsWith("xls")) {
				workbook = new HSSFWorkbook(bis);
			} else if (fileBean.getFileData().getOriginalFilename().endsWith("xlsx")) {
				workbook = new XSSFWorkbook(bis);
			} else {
				throw new IllegalArgumentException("Received file does not have a standard excel extension.");
			}

			//Get first/desired sheet from the workbook
			XSSFSheet sheet = (XSSFSheet) workbook.getSheetAt(0);

			Iterator<Row> rowIterator = sheet.iterator();

			int i = 0;
			//Skip first row since it contains column names, not data.
			if(rowIterator.hasNext()){
				Row row = rowIterator.next();
			}
			while(rowIterator.hasNext()) {
				i++;
				Row row = rowIterator.next();


				if(row!=null){

					row.getCell(SAPID_INDEX, Row.CREATE_NULL_AS_BLANK).setCellType(Cell.CELL_TYPE_STRING);
					row.getCell(NAME_INDEX, Row.CREATE_NULL_AS_BLANK).setCellType(Cell.CELL_TYPE_STRING);
					row.getCell(PROGRAM_INDEX, Row.CREATE_NULL_AS_BLANK).setCellType(Cell.CELL_TYPE_STRING);

					row.getCell(SUBJECT_INDEX, Row.CREATE_NULL_AS_BLANK).setCellType(Cell.CELL_TYPE_STRING);

					row.getCell(ASSIGNMENT_INDEX, Row.CREATE_NULL_AS_BLANK).setCellType(Cell.CELL_TYPE_STRING);
					row.getCell(REMARKS_INDEX, Row.CREATE_NULL_AS_BLANK).setCellType(Cell.CELL_TYPE_STRING);


					StudentMarksBean bean = new StudentMarksBean();


					String sapid = row.getCell(SAPID_INDEX, Row.CREATE_NULL_AS_BLANK).getStringCellValue();
					String studentname = row.getCell(NAME_INDEX, Row.CREATE_NULL_AS_BLANK).getStringCellValue();
					String program = row.getCell(PROGRAM_INDEX, Row.CREATE_NULL_AS_BLANK).getStringCellValue();

					String subject = row.getCell(SUBJECT_INDEX, Row.CREATE_NULL_AS_BLANK).getStringCellValue();

					String assignmentscore = row.getCell(ASSIGNMENT_INDEX, Row.CREATE_NULL_AS_BLANK).getStringCellValue();
					String remarks = row.getCell(REMARKS_INDEX, Row.CREATE_NULL_AS_BLANK).getStringCellValue();


					if("".equals(sapid.trim()) && "".equals(subject.trim()) && "".equals(program)){
						break;
					}
					bean.setYear(fileBean.getYear());
					bean.setMonth(fileBean.getMonth());
					bean.setSyllabusYear("");

					bean.setGrno("Not Available");
					bean.setSapid(sapid.trim());
					bean.setStudentname(studentname.trim());
					bean.setProgram(program.trim());

					bean.setSubject(subject.trim());
					bean.setWritenscore("");
					bean.setAssignmentscore(assignmentscore);
					bean.setRemarks(remarks);
					bean.setCreatedBy(userId);
					bean.setLastModifiedBy(userId);

					validateSAPIDInMarksBean(bean, i);
					validateAssignmentMarks(bean, i);

					/*if(!programList.contains(bean.getProgram())){
						bean.setErrorMessage(bean.getErrorMessage()+" Row : "+ (i+1) +" Invalid Program for record with SAPID:"+bean.getSapid()+ " & PROGRAM:"+bean.getProgram());
						bean.setErrorRecord(true);
					}*/

					/*ArrayList<ExamBookingTransactionBean> bookingList = (ArrayList<ExamBookingTransactionBean>)dao.getConfirmedBooking(sapid, subject);
					if(bookingList == null || bookingList.size() == 0 || bookingList.size() > 1){
						bean.setErrorMessage(bean.getErrorMessage()+" Row : "+ (i+1) +" Exam Registration not found for record with SAPID:"+bean.getSapid()+ " & SUBJECT:"+bean.getSubject());
						bean.setErrorRecord(true);
					}else{
						bean.setSem(bookingList.get(0).getSem());
						bean.setProgram(bookingList.get(0).getProgram());
					}*/

					/* temporarily commented to upload reval assignment score of June2019
					 * 
					 * StudentBean student = dao.getSingleStudentWithValidity(bean.getSapid());
					if(student == null){
						bean.setErrorMessage(bean.getErrorMessage()+" Row : "+ (i+1) +" Student Details not found for record with SAPID:"+bean.getSapid()+ " & SUBJECT:"+bean.getSubject());
						bean.setErrorRecord(true);
					}else{*/
					StudentExamBean student = dao.getSingleStudentsData(bean.getSapid()); //temporarily added to upload reval assignment score of June2019
						bean.setProgram(student.getProgram());
						bean.setStudentname(student.getFirstName()+ " "+student.getLastName());
						for (int j = 0; j < programSubjectMappingList.size(); j++) {
							ProgramSubjectMappingExamBean programSubjectBean = programSubjectMappingList.get(j);

							if(programSubjectBean.getPrgmStructApplicable().equals(student.getPrgmStructApplicable()) && 
									programSubjectBean.getProgram().equals(student.getProgram()) && 
									programSubjectBean.getSubject().equals(bean.getSubject())){
								bean.setSem(programSubjectBean.getSem());
							}
						}
					//}temporarily commented to upload reval assignment score of June2019

					if(!subjectList.contains(bean.getSubject())){
						bean.setErrorMessage(bean.getErrorMessage()+" Row : "+ (i+1) +" Invalid Subject for record with SAPID:"+bean.getSapid()+ " & SUBJECT:"+bean.getSubject());
						bean.setErrorRecord(true);
					}

					/*if(!(yearList.contains(bean.getYear()) && ("Jun".equals(bean.getMonth()) || "Dec".equals(bean.getMonth())))){
						bean.setErrorMessage(bean.getErrorMessage()+" Row : "+ (i+1) +" Invalid Year/Month for record with SAPID:"+bean.getSapid());
						bean.setErrorRecord(true);
					}*/
					if(!sapIdSujectKeys.containsKey(sapid.trim()+subject.trim())){
						sapIdSujectKeys.put(sapid.trim()+subject.trim(), null);
					}else{
						bean.setErrorMessage(bean.getErrorMessage()+" Row : "+ (i+1) +" Duplicate Entry for record with SAPID:"+bean.getSapid()+ " & SUBJECT:"+bean.getSubject());
						bean.setErrorRecord(true);
					}

					if(bean.isErrorRecord()){
						errorBeanList.add(bean);
					}else{
						marksBeanList.add(bean);
					}


					if(i % 1000 == 0){
					}

				}
			}
		} catch (IOException e) {
			
		}
		resultList.add(marksBeanList);
		resultList.add(errorBeanList);
		return resultList;
	}
	
	public ArrayList<List> readSeatReleaseExcel(FileBean fileBean, ArrayList<String> subjectList){

		int  SAPID_INDEX = 0;
		int  SUBJECT_INDEX = 1;


		ByteArrayInputStream bis = new ByteArrayInputStream(fileBean.getFileData().getBytes());
		Workbook workbook;
		List<ExamBookingTransactionBean> marksBeanList = new ArrayList<ExamBookingTransactionBean>();
		List<ExamBookingTransactionBean> errorBeanList = new ArrayList<ExamBookingTransactionBean>();
		ArrayList<List> resultList = new ArrayList<>();

		try {
			if (fileBean.getFileData().getOriginalFilename().endsWith("xls")) {
				workbook = new HSSFWorkbook(bis);
			} else if (fileBean.getFileData().getOriginalFilename().endsWith("xlsx")) {
				workbook = new XSSFWorkbook(bis);
			} else {
				throw new IllegalArgumentException("Received file does not have a standard excel extension.");
			}

			//Get first/desired sheet from the workbook
			XSSFSheet sheet = (XSSFSheet) workbook.getSheetAt(0);

			Iterator<Row> rowIterator = sheet.iterator();

			int i = 0;
			//Skip first row since it contains column names, not data.
			if(rowIterator.hasNext()){
				Row row = rowIterator.next();
			}
			while(rowIterator.hasNext()) {
				i++;
				Row row = rowIterator.next();


				if(row!=null){

					row.getCell(SAPID_INDEX, Row.CREATE_NULL_AS_BLANK).setCellType(Cell.CELL_TYPE_STRING);
					row.getCell(SUBJECT_INDEX, Row.CREATE_NULL_AS_BLANK).setCellType(Cell.CELL_TYPE_STRING);

					ExamBookingTransactionBean bean = new ExamBookingTransactionBean();


					String sapid = row.getCell(SAPID_INDEX, Row.CREATE_NULL_AS_BLANK).getStringCellValue();
					String subject = row.getCell(SUBJECT_INDEX, Row.CREATE_NULL_AS_BLANK).getStringCellValue();


					if("".equals(sapid.trim()) && "".equals(subject.trim()) ){
						break;
					}
					bean.setYear(fileBean.getYear());
					bean.setMonth(fileBean.getMonth());
					bean.setSapid(sapid.trim());
					bean.setSubject(subject.trim());


					if(!subjectList.contains(bean.getSubject())){
						bean.setErrorMessage(bean.getErrorMessage()+" Row : "+ (i+1) +" Invalid Subject for record with SAPID:"+bean.getSapid()+ " & SUBJECT:"+bean.getSubject());
						bean.setErrorRecord(true);
					}

					if(bean.isErrorRecord()){
						errorBeanList.add(bean);
					}else{
						marksBeanList.add(bean);
					}


					if(i % 1000 == 0){
					}

				}
			}
		} catch (IOException e) {
			
		}
		resultList.add(marksBeanList);
		resultList.add(errorBeanList);
		return resultList;
	}

	public ArrayList<List> readCopyCasesExcel(FileBean fileBean){

		
		int  SUBJECT_INDEX = 0;
		int  SAPID1_INDEX = 1;
		int  SAPID1FNAME_INDEX = 2;
		int  SAPID1LNAME_INDEX = 3;
		int  PROGRAM1_INDEX = 4;
		int  IC1_INDEX = 5;
		int  SAPID2_INDEX = 6;
		int  SAPID2FNAME_INDEX = 7;
		int  SAPID2LNAME_INDEX = 8;
		int  PROGRAM2_INDEX = 9;
		int  IC2_INDEX = 10;
		int  MATCHPERCENT_INDEX = 11;
		int  CONSECUTIVELINES_INDEX = 12;
		int  FILE1LINES_INDEX = 13;
		int  FILE2LINES_INDEX = 14;
		int  TOTALLINESMATCHED_INDEX = 15;


		ByteArrayInputStream bis = new ByteArrayInputStream(fileBean.getFileData().getBytes());
		Workbook workbook;
		List<StudentMarksBean> marksBeanList = new ArrayList<StudentMarksBean>();
		List<StudentMarksBean> errorBeanList = new ArrayList<StudentMarksBean>();
		ArrayList<List> resultList = new ArrayList<>();
		HashMap<String, String> sapIdSujectKeys = new HashMap<>();

		try {
			if (fileBean.getFileData().getOriginalFilename().endsWith("xls")) {
				workbook = new HSSFWorkbook(bis);
			} else if (fileBean.getFileData().getOriginalFilename().endsWith("xlsx")) {
				workbook = new XSSFWorkbook(bis);
			} else {
				throw new IllegalArgumentException("Received file does not have a standard excel extension.");
			}

			//Get first/desired sheet from the workbook
			XSSFSheet sheet = (XSSFSheet) workbook.getSheetAt(0);

			Iterator<Row> rowIterator = sheet.iterator();

			int i = 0;
			//Skip first row since it contains column names, not data.
			if(rowIterator.hasNext()){
				Row row = rowIterator.next();
			}
			while(rowIterator.hasNext()) {
				i++;
				Row row = rowIterator.next();


				if(row!=null){
					row.getCell(SUBJECT_INDEX, Row.CREATE_NULL_AS_BLANK).setCellType(Cell.CELL_TYPE_STRING);
					row.getCell(SAPID1_INDEX, Row.CREATE_NULL_AS_BLANK).setCellType(Cell.CELL_TYPE_STRING);
					row.getCell(SAPID1FNAME_INDEX, Row.CREATE_NULL_AS_BLANK).setCellType(Cell.CELL_TYPE_STRING);
					row.getCell(SAPID1LNAME_INDEX, Row.CREATE_NULL_AS_BLANK).setCellType(Cell.CELL_TYPE_STRING);
					row.getCell(PROGRAM1_INDEX, Row.CREATE_NULL_AS_BLANK).setCellType(Cell.CELL_TYPE_STRING);
					row.getCell(IC1_INDEX, Row.CREATE_NULL_AS_BLANK).setCellType(Cell.CELL_TYPE_STRING);
					
					row.getCell(SAPID2_INDEX, Row.CREATE_NULL_AS_BLANK).setCellType(Cell.CELL_TYPE_STRING);
					row.getCell(SAPID2FNAME_INDEX, Row.CREATE_NULL_AS_BLANK).setCellType(Cell.CELL_TYPE_STRING);
					row.getCell(SAPID2LNAME_INDEX, Row.CREATE_NULL_AS_BLANK).setCellType(Cell.CELL_TYPE_STRING);
					row.getCell(PROGRAM2_INDEX, Row.CREATE_NULL_AS_BLANK).setCellType(Cell.CELL_TYPE_STRING);
					row.getCell(IC2_INDEX, Row.CREATE_NULL_AS_BLANK).setCellType(Cell.CELL_TYPE_STRING);
					
					row.getCell(MATCHPERCENT_INDEX, Row.CREATE_NULL_AS_BLANK).setCellType(Cell.CELL_TYPE_STRING);
					row.getCell(CONSECUTIVELINES_INDEX, Row.CREATE_NULL_AS_BLANK).setCellType(Cell.CELL_TYPE_STRING);
					row.getCell(FILE1LINES_INDEX, Row.CREATE_NULL_AS_BLANK).setCellType(Cell.CELL_TYPE_STRING);
					row.getCell(FILE2LINES_INDEX, Row.CREATE_NULL_AS_BLANK).setCellType(Cell.CELL_TYPE_STRING);
					row.getCell(TOTALLINESMATCHED_INDEX, Row.CREATE_NULL_AS_BLANK).setCellType(Cell.CELL_TYPE_STRING);


					StudentMarksBean bean = new StudentMarksBean();

					String subject = row.getCell(SUBJECT_INDEX, Row.CREATE_NULL_AS_BLANK).getStringCellValue();
					String sapid1 = row.getCell(SAPID1_INDEX, Row.CREATE_NULL_AS_BLANK).getStringCellValue();
					String sapid1FName = row.getCell(SAPID1FNAME_INDEX, Row.CREATE_NULL_AS_BLANK).getStringCellValue();
					String sapid1LName = row.getCell(SAPID1LNAME_INDEX, Row.CREATE_NULL_AS_BLANK).getStringCellValue();
					String program1 = row.getCell(PROGRAM1_INDEX, Row.CREATE_NULL_AS_BLANK).getStringCellValue();
					String ic1 = row.getCell(IC1_INDEX, Row.CREATE_NULL_AS_BLANK).getStringCellValue();
					String sapid2 = row.getCell(SAPID2_INDEX, Row.CREATE_NULL_AS_BLANK).getStringCellValue();
					String sapid2FName = row.getCell(SAPID2FNAME_INDEX, Row.CREATE_NULL_AS_BLANK).getStringCellValue();
					String sapid2LName = row.getCell(SAPID2LNAME_INDEX, Row.CREATE_NULL_AS_BLANK).getStringCellValue();
					String program2 = row.getCell(PROGRAM2_INDEX, Row.CREATE_NULL_AS_BLANK).getStringCellValue();
					String ic2 = row.getCell(IC2_INDEX, Row.CREATE_NULL_AS_BLANK).getStringCellValue();
					String matchPercent = row.getCell(MATCHPERCENT_INDEX, Row.CREATE_NULL_AS_BLANK).getStringCellValue();
					String consecutiveLines = row.getCell(CONSECUTIVELINES_INDEX, Row.CREATE_NULL_AS_BLANK).getStringCellValue();
					String file1Lines = row.getCell(FILE1LINES_INDEX, Row.CREATE_NULL_AS_BLANK).getStringCellValue();
					String file2Lines = row.getCell(FILE2LINES_INDEX, Row.CREATE_NULL_AS_BLANK).getStringCellValue();
					String totalLinesMatched = row.getCell(TOTALLINESMATCHED_INDEX, Row.CREATE_NULL_AS_BLANK).getStringCellValue();


					if("".equals(sapid1.trim()) && "".equals(subject.trim())){
						break;
					}
					bean.setYear(fileBean.getYear());
					bean.setMonth(fileBean.getMonth());
					bean.setSubject(subject.trim());
					bean.setSapid1(sapid1);
					bean.setSapid1FName(sapid1FName);
					bean.setSapid1LName(sapid1LName);
					bean.setProgram1(program1);
					bean.setIc1(ic1);
					bean.setSapid2(sapid2);
					bean.setSapid2FName(sapid2FName);
					bean.setSapid2LName(sapid2LName);
					bean.setProgram2(program2);
					bean.setIc2(ic2);
					bean.setMatchPercent(matchPercent);
					bean.setConsecutiveLines(consecutiveLines);
					bean.setFile1Lines(file1Lines);
					bean.setFile2Lines(file2Lines);
					bean.setTotalLinesMatched(totalLinesMatched);

					if(bean.isErrorRecord()){
						errorBeanList.add(bean);
					}else{
						marksBeanList.add(bean);
					}

					if(i % 1000 == 0){
					}

				}
			}
		} catch (IOException e) {
			
		}
		resultList.add(marksBeanList);
		resultList.add(errorBeanList);
		return resultList;
	}
	
	
	public ArrayList<List> readQuestionFileMCQExcel(FileBean fileBean){

		int index = 0;
		
		//int  QUESTION_ID_INDEX = index++;
		int  EXAM_CODE_INDEX = index++;
		int  SUBJECT_CODE_INDEX = index++;
		int  SECTION_CODE_INDEX = index++;
		int  QUESTION_TEXT_INDEX = index++;
		int  OPTION1_INDEX = index++;
		int  OPTION2_INDEX = index++;
		int  OPTION3_INDEX = index++;
		int  OPTION4_INDEX = index++;
		int  OPTION5_INDEX = index++;
		int  CORRECT_ANSWER_INDEX = index++;
		int  MARKS_INDEX = index++;
		int  NEGATIVE_MARKS_INDEX = index++;
		int  QUESTION_TYPE_INDEX = index++;
		int  CASE_ID_INDEX = index++;
		int  DIFFICULTY_INDEX = index++;
		int  SUB_SECTION_INDEX = index++;


		ByteArrayInputStream bis = new ByteArrayInputStream(fileBean.getFileData().getBytes());
		Workbook workbook;
		List<QuestionFileBean> marksBeanList = new ArrayList<QuestionFileBean>();
		List<QuestionFileBean> errorBeanList = new ArrayList<QuestionFileBean>();
		ArrayList<List> resultList = new ArrayList<>();
		HashMap<String, String> sapIdSujectKeys = new HashMap<>();

		try {
			if (fileBean.getFileData().getOriginalFilename().endsWith("xls")) {
				workbook = new HSSFWorkbook(bis);
			} else if (fileBean.getFileData().getOriginalFilename().endsWith("xlsx")) {
				workbook = new XSSFWorkbook(bis);
			} else {
				throw new IllegalArgumentException("Received file does not have a standard excel extension.");
			}

			//Get first/desired sheet from the workbook
			XSSFSheet sheet = (XSSFSheet) workbook.getSheetAt(0);

			Iterator<Row> rowIterator = sheet.iterator();

			int i = 0;
			//Skip first row since it contains column names, not data.
			if(rowIterator.hasNext()){
				Row row = rowIterator.next();
			}
			while(rowIterator.hasNext()) {
				i++;
				Row row = rowIterator.next();


				if(row!=null){
					//row.getCell(QUESTION_ID_INDEX, Row.CREATE_NULL_AS_BLANK).setCellType(Cell.CELL_TYPE_STRING);
					row.getCell(EXAM_CODE_INDEX, Row.CREATE_NULL_AS_BLANK).setCellType(Cell.CELL_TYPE_STRING);
					row.getCell(SUBJECT_CODE_INDEX, Row.CREATE_NULL_AS_BLANK).setCellType(Cell.CELL_TYPE_STRING);
					row.getCell(SECTION_CODE_INDEX, Row.CREATE_NULL_AS_BLANK).setCellType(Cell.CELL_TYPE_STRING);
					row.getCell(QUESTION_TEXT_INDEX, Row.CREATE_NULL_AS_BLANK).setCellType(Cell.CELL_TYPE_STRING);
					row.getCell(OPTION1_INDEX, Row.CREATE_NULL_AS_BLANK).setCellType(Cell.CELL_TYPE_STRING);
					row.getCell(OPTION2_INDEX, Row.CREATE_NULL_AS_BLANK).setCellType(Cell.CELL_TYPE_STRING);
					row.getCell(OPTION3_INDEX, Row.CREATE_NULL_AS_BLANK).setCellType(Cell.CELL_TYPE_STRING);
					row.getCell(OPTION4_INDEX, Row.CREATE_NULL_AS_BLANK).setCellType(Cell.CELL_TYPE_STRING);
					row.getCell(OPTION5_INDEX, Row.CREATE_NULL_AS_BLANK).setCellType(Cell.CELL_TYPE_STRING);
					row.getCell(CORRECT_ANSWER_INDEX, Row.CREATE_NULL_AS_BLANK).setCellType(Cell.CELL_TYPE_STRING);
					row.getCell(MARKS_INDEX, Row.CREATE_NULL_AS_BLANK).setCellType(Cell.CELL_TYPE_STRING);
					row.getCell(NEGATIVE_MARKS_INDEX, Row.CREATE_NULL_AS_BLANK).setCellType(Cell.CELL_TYPE_STRING);
					row.getCell(CASE_ID_INDEX, Row.CREATE_NULL_AS_BLANK).setCellType(Cell.CELL_TYPE_STRING);
					row.getCell(QUESTION_TYPE_INDEX, Row.CREATE_NULL_AS_BLANK).setCellType(Cell.CELL_TYPE_STRING);
					row.getCell(DIFFICULTY_INDEX, Row.CREATE_NULL_AS_BLANK).setCellType(Cell.CELL_TYPE_STRING);
					row.getCell(SUB_SECTION_INDEX, Row.CREATE_NULL_AS_BLANK).setCellType(Cell.CELL_TYPE_STRING);


					
					QuestionFileBean bean = new QuestionFileBean();

					//String questionId = row.getCell(QUESTION_ID_INDEX, Row.CREATE_NULL_AS_BLANK).getStringCellValue();
					String examCode = row.getCell(EXAM_CODE_INDEX, Row.CREATE_NULL_AS_BLANK).getStringCellValue();
					String subjectCode = row.getCell(SUBJECT_CODE_INDEX, Row.CREATE_NULL_AS_BLANK).getStringCellValue();
					String sectionCode = row.getCell(SECTION_CODE_INDEX, Row.CREATE_NULL_AS_BLANK).getStringCellValue();
					String questionText = row.getCell(QUESTION_TEXT_INDEX, Row.CREATE_NULL_AS_BLANK).getStringCellValue();
					String option1 = row.getCell(OPTION1_INDEX, Row.CREATE_NULL_AS_BLANK).getStringCellValue();
					String option2 = row.getCell(OPTION2_INDEX, Row.CREATE_NULL_AS_BLANK).getStringCellValue();
					String option3 = row.getCell(OPTION3_INDEX, Row.CREATE_NULL_AS_BLANK).getStringCellValue();
					String option4 = row.getCell(OPTION4_INDEX, Row.CREATE_NULL_AS_BLANK).getStringCellValue();
					String option5 = row.getCell(OPTION5_INDEX, Row.CREATE_NULL_AS_BLANK).getStringCellValue();
					String correctAnswer = row.getCell(CORRECT_ANSWER_INDEX, Row.CREATE_NULL_AS_BLANK).getStringCellValue();
					String marks = row.getCell(MARKS_INDEX, Row.CREATE_NULL_AS_BLANK).getStringCellValue();
					String negativeMarks = row.getCell(NEGATIVE_MARKS_INDEX, Row.CREATE_NULL_AS_BLANK).getStringCellValue();
					String questionType = row.getCell(QUESTION_TYPE_INDEX, Row.CREATE_NULL_AS_BLANK).getStringCellValue();
					String caseId = row.getCell(CASE_ID_INDEX, Row.CREATE_NULL_AS_BLANK).getStringCellValue();
					String difficulty = row.getCell(DIFFICULTY_INDEX, Row.CREATE_NULL_AS_BLANK).getStringCellValue();
					String subsectionCode = row.getCell(SUB_SECTION_INDEX, Row.CREATE_NULL_AS_BLANK).getStringCellValue();


					if( "".equals(examCode.trim()) 
							&& "".equals(subjectCode.trim())  && "".equals(sectionCode.trim())  && "".equals(questionText.trim())){
						break;
					}
					//bean.setQuestionId(questionId);
					bean.setExamCode(examCode);
					bean.setSubjectCode(subjectCode);
					bean.setSectionCode(sectionCode);
					bean.setQuestionText(StringEscapeUtils.unescapeHtml(questionText.replaceAll("\\<.*?>","")));
					
					String firstLetterCapitalQuestionText = Character.toUpperCase(bean.getQuestionText().charAt(0)) + bean.getQuestionText().substring(1);
					bean.setQuestionText(firstLetterCapitalQuestionText);
					
					bean.setOption1(StringEscapeUtils.unescapeHtml(option1.replaceAll("\\<.*?>","")));
					bean.setOption2(StringEscapeUtils.unescapeHtml(option2.replaceAll("\\<.*?>","")));
					bean.setOption3(StringEscapeUtils.unescapeHtml(option3.replaceAll("\\<.*?>","")));
					bean.setOption4(StringEscapeUtils.unescapeHtml(option4.replaceAll("\\<.*?>","")));
					bean.setOption5(StringEscapeUtils.unescapeHtml(option5.replaceAll("\\<.*?>","")));
					bean.setCorrectAnswer(correctAnswer);
					bean.setMarks(marks);
					bean.setNegativeMarks(negativeMarks);
					bean.setQuestionType(questionType);
					bean.setCaseId(caseId);
					bean.setDifficulty(difficulty);
					bean.setSubsectionCode(subsectionCode);
					bean.setRowNumber((i + 1) + "");
					
					/*if("".equals(questionId.trim()) || "".equals(examCode.trim()) || "".equals(subjectCode.trim()) || "".equals(sectionCode.trim())){
						bean.setErrorMessage(bean.getErrorMessage()+" Row : "+ (i+1) +": Question ID OR Exam Code OR Subject Code or Section Code is blank ");
						bean.setErrorRecord(true);
					}*/
					
					
					if(bean.isErrorRecord()){
						errorBeanList.add(bean);
					}else{
						marksBeanList.add(bean);
					}

					if(i % 1000 == 0){
					}

				}
			}
		} catch (IOException e) {
			
		}
		resultList.add(marksBeanList);
		resultList.add(errorBeanList);
		return resultList;
	}
	
	
	public ArrayList<List> readQuestionFileDescriptiveExcel(FileBean fileBean){

		int index = 0;
		
		//int  QUESTION_ID_INDEX = index++;
		int  EXAM_CODE_INDEX = index++;
		int  SUBJECT_CODE_INDEX = index++;
		int  SECTION_CODE_INDEX = index++;
		int  QUESTION_TEXT_INDEX = index++;
		int  MARKS_INDEX = index++;
		int  QUESTION_TYPE_INDEX = index++;
		int  DIFFICULTY_INDEX = index++;
		int  SUB_SECTION_INDEX = index++;


		ByteArrayInputStream bis = new ByteArrayInputStream(fileBean.getFileData().getBytes());
		Workbook workbook;
		List<QuestionFileBean> marksBeanList = new ArrayList<QuestionFileBean>();
		List<QuestionFileBean> errorBeanList = new ArrayList<QuestionFileBean>();
		ArrayList<List> resultList = new ArrayList<>();
		HashMap<String, String> sapIdSujectKeys = new HashMap<>();

		try {
			if (fileBean.getFileData().getOriginalFilename().endsWith("xls")) {
				workbook = new HSSFWorkbook(bis);
			} else if (fileBean.getFileData().getOriginalFilename().endsWith("xlsx")) {
				workbook = new XSSFWorkbook(bis);
			} else {
				throw new IllegalArgumentException("Received file does not have a standard excel extension.");
			}

			//Get first/desired sheet from the workbook
			XSSFSheet sheet = (XSSFSheet) workbook.getSheetAt(1);

			Iterator<Row> rowIterator = sheet.iterator();

			int i = 0;
			//Skip first row since it contains column names, not data.
			if(rowIterator.hasNext()){
				Row row = rowIterator.next();
			}
			while(rowIterator.hasNext()) {
				i++;
				Row row = rowIterator.next();


				if(row!=null){
					//row.getCell(QUESTION_ID_INDEX, Row.CREATE_NULL_AS_BLANK).setCellType(Cell.CELL_TYPE_STRING);
					row.getCell(EXAM_CODE_INDEX, Row.CREATE_NULL_AS_BLANK).setCellType(Cell.CELL_TYPE_STRING);
					row.getCell(SUBJECT_CODE_INDEX, Row.CREATE_NULL_AS_BLANK).setCellType(Cell.CELL_TYPE_STRING);
					row.getCell(SECTION_CODE_INDEX, Row.CREATE_NULL_AS_BLANK).setCellType(Cell.CELL_TYPE_STRING);
					row.getCell(QUESTION_TEXT_INDEX, Row.CREATE_NULL_AS_BLANK).setCellType(Cell.CELL_TYPE_STRING);
					row.getCell(MARKS_INDEX, Row.CREATE_NULL_AS_BLANK).setCellType(Cell.CELL_TYPE_STRING);
					row.getCell(QUESTION_TYPE_INDEX, Row.CREATE_NULL_AS_BLANK).setCellType(Cell.CELL_TYPE_STRING);
					row.getCell(DIFFICULTY_INDEX, Row.CREATE_NULL_AS_BLANK).setCellType(Cell.CELL_TYPE_STRING);
					row.getCell(SUB_SECTION_INDEX, Row.CREATE_NULL_AS_BLANK).setCellType(Cell.CELL_TYPE_STRING);

					QuestionFileBean bean = new QuestionFileBean();

					//String questionId = row.getCell(QUESTION_ID_INDEX, Row.CREATE_NULL_AS_BLANK).getStringCellValue();
					String examCode = row.getCell(EXAM_CODE_INDEX, Row.CREATE_NULL_AS_BLANK).getStringCellValue();
					String subjectCode = row.getCell(SUBJECT_CODE_INDEX, Row.CREATE_NULL_AS_BLANK).getStringCellValue();
					String sectionCode = row.getCell(SECTION_CODE_INDEX, Row.CREATE_NULL_AS_BLANK).getStringCellValue();
					String questionText = row.getCell(QUESTION_TEXT_INDEX, Row.CREATE_NULL_AS_BLANK).getStringCellValue();
					String marks = row.getCell(MARKS_INDEX, Row.CREATE_NULL_AS_BLANK).getStringCellValue();
					String questionType = row.getCell(QUESTION_TYPE_INDEX, Row.CREATE_NULL_AS_BLANK).getStringCellValue();
					String difficulty = row.getCell(DIFFICULTY_INDEX, Row.CREATE_NULL_AS_BLANK).getStringCellValue();
					String subsectionCode = row.getCell(SUB_SECTION_INDEX, Row.CREATE_NULL_AS_BLANK).getStringCellValue();


					if( "".equals(examCode.trim()) 
							&& "".equals(subjectCode.trim())  && "".equals(sectionCode.trim())  && "".equals(questionText.trim())){
						break;
					}
					//bean.setQuestionId(questionId);
					bean.setExamCode(examCode);
					bean.setSubjectCode(subjectCode);
					bean.setSectionCode(sectionCode);
					bean.setQuestionText(StringEscapeUtils.unescapeHtml(questionText.replaceAll("\\<.*?>","")));

					bean.setMarks(marks);
					bean.setQuestionType(questionType);
					bean.setDifficulty(difficulty);
					bean.setSubsectionCode(subsectionCode);
					bean.setRowNumber((i + 1) + "");
					
					if(!"4".equals(sectionCode)){
						bean.setErrorMessage(bean.getErrorMessage()+" Row : "+ (i+1) +": Section Code must be 4 for Descriptive questions");
						bean.setErrorRecord(true);
					}
					
					
					if(bean.isErrorRecord()){
						errorBeanList.add(bean);
					}else{
						marksBeanList.add(bean);
					}

					if(i % 1000 == 0){
					}

				}
			}
		} catch (IOException e) {
			
		}
		resultList.add(marksBeanList);
		resultList.add(errorBeanList);
		return resultList;
	}
	
	public ArrayList<List> readStudentMasterExcel(FileBean fileBean, ArrayList<String> programList, ArrayList<String> subjectList,
			ArrayList<String> centersList, String userId){


		int SAPID_INDEX = 0;
		int LASTNAME_INDEX = 1;
		int FIRSTNAME_INDEX = 2;
		int MIDDLENAME_INDEX = 3;
		int FATHERNAME_INDEX = 4;
		int HUSBANDNAME_INDEX = 5;
		int MOTHERNAME_INDEX = 6;
		int GENDER_INDEX = 7;
		int PROGRAM_INDEX = 8;
		int ENROLLMENTMONTH_INDEX = 9;
		int ENROLLMENTYEAR_INDEX = 10;
		int SEM_INDEX = 11;
		int EMAILID_INDEX = 12;
		int MOBILE_INDEX = 13;
		int ALTPHONE_INDEX = 14;
		int DOB_INDEX = 15;
		int REGDATE_INDEX = 16;
		int ISLATERAL_INDEX = 17;
		int ADDRESS_INDEX = 18;
		int CITY_INDEX = 19;
		int STATE_INDEX = 20;
		int COUNTRY_INDEX = 21;
		int PIN_INDEX = 22;
		int CENTERCODE_INDEX = 23;
		int CENTERNAME_INDEX = 24;
		int VALIDITYENDMONTH_INDEX = 25;
		int VALIDITYENDYEAR_INDEX = 26;
		int PROGRAM_STUCTURE_INDEX = 27;
		int STUDENT_IMAGE_URL_INDEX = 28;
		int PREVIOUS_STUDENT_ID = 29;


		ByteArrayInputStream bis = new ByteArrayInputStream(fileBean.getFileData().getBytes());
		Workbook workbook;
		List<StudentExamBean> marksBeanList = new ArrayList<StudentExamBean>();
		List<StudentExamBean> errorBeanList = new ArrayList<StudentExamBean>();
		ArrayList<List> resultList = new ArrayList<>();
		HashMap<String, String> sapIdSemKeys = new HashMap<>();

		try {
			if (fileBean.getFileData().getOriginalFilename().endsWith("xls")) {
				workbook = new HSSFWorkbook(bis);
			} else if (fileBean.getFileData().getOriginalFilename().endsWith("xlsx")) {
				workbook = new XSSFWorkbook(bis);
			} else {
				throw new IllegalArgumentException("Received file does not have a standard excel extension.");
			}

			//Get first/desired sheet from the workbook
			XSSFSheet sheet = (XSSFSheet) workbook.getSheetAt(0);

			Iterator<Row> rowIterator = sheet.iterator();

			int i = 0;
			//Skip first row since it contains column names, not data.
			if(rowIterator.hasNext()){
				Row row = rowIterator.next();
			}
			while(rowIterator.hasNext()) {
				i++;
				Row row = rowIterator.next();


				if(row!=null){

					row.getCell(SAPID_INDEX, Row.CREATE_NULL_AS_BLANK).setCellType(Cell.CELL_TYPE_STRING);
					row.getCell(LASTNAME_INDEX, Row.CREATE_NULL_AS_BLANK).setCellType(Cell.CELL_TYPE_STRING);
					row.getCell(FIRSTNAME_INDEX, Row.CREATE_NULL_AS_BLANK).setCellType(Cell.CELL_TYPE_STRING);
					row.getCell(MIDDLENAME_INDEX, Row.CREATE_NULL_AS_BLANK).setCellType(Cell.CELL_TYPE_STRING);
					row.getCell(FATHERNAME_INDEX, Row.CREATE_NULL_AS_BLANK).setCellType(Cell.CELL_TYPE_STRING);
					row.getCell(HUSBANDNAME_INDEX, Row.CREATE_NULL_AS_BLANK).setCellType(Cell.CELL_TYPE_STRING);
					row.getCell(MOTHERNAME_INDEX, Row.CREATE_NULL_AS_BLANK).setCellType(Cell.CELL_TYPE_STRING);
					row.getCell(GENDER_INDEX, Row.CREATE_NULL_AS_BLANK).setCellType(Cell.CELL_TYPE_STRING);
					row.getCell(PROGRAM_INDEX, Row.CREATE_NULL_AS_BLANK).setCellType(Cell.CELL_TYPE_STRING);
					row.getCell(ENROLLMENTMONTH_INDEX, Row.CREATE_NULL_AS_BLANK).setCellType(Cell.CELL_TYPE_STRING);
					row.getCell(ENROLLMENTYEAR_INDEX, Row.CREATE_NULL_AS_BLANK).setCellType(Cell.CELL_TYPE_STRING);
					row.getCell(SEM_INDEX, Row.CREATE_NULL_AS_BLANK).setCellType(Cell.CELL_TYPE_STRING);
					row.getCell(EMAILID_INDEX, Row.CREATE_NULL_AS_BLANK).setCellType(Cell.CELL_TYPE_STRING);
					row.getCell(MOBILE_INDEX, Row.CREATE_NULL_AS_BLANK).setCellType(Cell.CELL_TYPE_STRING);
					row.getCell(ALTPHONE_INDEX, Row.CREATE_NULL_AS_BLANK).setCellType(Cell.CELL_TYPE_STRING);
					row.getCell(DOB_INDEX, Row.CREATE_NULL_AS_BLANK).setCellType(Cell.CELL_TYPE_STRING);
					row.getCell(REGDATE_INDEX, Row.CREATE_NULL_AS_BLANK).setCellType(Cell.CELL_TYPE_STRING);
					row.getCell(ISLATERAL_INDEX, Row.CREATE_NULL_AS_BLANK).setCellType(Cell.CELL_TYPE_STRING);
					row.getCell(ADDRESS_INDEX, Row.CREATE_NULL_AS_BLANK).setCellType(Cell.CELL_TYPE_STRING);
					row.getCell(CITY_INDEX, Row.CREATE_NULL_AS_BLANK).setCellType(Cell.CELL_TYPE_STRING);
					row.getCell(STATE_INDEX, Row.CREATE_NULL_AS_BLANK).setCellType(Cell.CELL_TYPE_STRING);
					row.getCell(COUNTRY_INDEX, Row.CREATE_NULL_AS_BLANK).setCellType(Cell.CELL_TYPE_STRING);
					row.getCell(PIN_INDEX, Row.CREATE_NULL_AS_BLANK).setCellType(Cell.CELL_TYPE_STRING);
					row.getCell(CENTERCODE_INDEX, Row.CREATE_NULL_AS_BLANK).setCellType(Cell.CELL_TYPE_STRING);
					row.getCell(CENTERNAME_INDEX, Row.CREATE_NULL_AS_BLANK).setCellType(Cell.CELL_TYPE_STRING);
					row.getCell(VALIDITYENDMONTH_INDEX, Row.CREATE_NULL_AS_BLANK).setCellType(Cell.CELL_TYPE_STRING);
					row.getCell(VALIDITYENDYEAR_INDEX, Row.CREATE_NULL_AS_BLANK).setCellType(Cell.CELL_TYPE_STRING);
					row.getCell(PROGRAM_STUCTURE_INDEX, Row.CREATE_NULL_AS_BLANK).setCellType(Cell.CELL_TYPE_STRING);
					row.getCell(STUDENT_IMAGE_URL_INDEX, Row.CREATE_NULL_AS_BLANK).setCellType(Cell.CELL_TYPE_STRING);
					row.getCell(PREVIOUS_STUDENT_ID, Row.CREATE_NULL_AS_BLANK).setCellType(Cell.CELL_TYPE_STRING);

					StudentExamBean bean = new StudentExamBean();

					String sapId = row.getCell(SAPID_INDEX, Row.CREATE_NULL_AS_BLANK).getStringCellValue().trim();
					String lastName = row.getCell(LASTNAME_INDEX, Row.CREATE_NULL_AS_BLANK).getStringCellValue().trim();
					String firstName = row.getCell(FIRSTNAME_INDEX, Row.CREATE_NULL_AS_BLANK).getStringCellValue().trim();
					String middleName = row.getCell(MIDDLENAME_INDEX, Row.CREATE_NULL_AS_BLANK).getStringCellValue().trim();
					String fatherName = row.getCell(FATHERNAME_INDEX, Row.CREATE_NULL_AS_BLANK).getStringCellValue().trim();
					String husbandName = row.getCell(HUSBANDNAME_INDEX, Row.CREATE_NULL_AS_BLANK).getStringCellValue().trim();
					String motherName = row.getCell(MOTHERNAME_INDEX, Row.CREATE_NULL_AS_BLANK).getStringCellValue().trim();
					String gender = row.getCell(GENDER_INDEX, Row.CREATE_NULL_AS_BLANK).getStringCellValue().trim();
					String program = row.getCell(PROGRAM_INDEX, Row.CREATE_NULL_AS_BLANK).getStringCellValue().trim();
					String enrollmentMonth = row.getCell(ENROLLMENTMONTH_INDEX, Row.CREATE_NULL_AS_BLANK).getStringCellValue().trim();

					if("Jan".equalsIgnoreCase(enrollmentMonth)){
						enrollmentMonth = "Jan";
					}else if("Jul".equalsIgnoreCase(enrollmentMonth)){
						enrollmentMonth = "Jul";
					}
					String enrollmentYear = row.getCell(ENROLLMENTYEAR_INDEX, Row.CREATE_NULL_AS_BLANK).getStringCellValue().trim();
					String sem = row.getCell(SEM_INDEX, Row.CREATE_NULL_AS_BLANK).getStringCellValue().trim();
					String emailId = row.getCell(EMAILID_INDEX, Row.CREATE_NULL_AS_BLANK).getStringCellValue().trim();
					String mobile = row.getCell(MOBILE_INDEX, Row.CREATE_NULL_AS_BLANK).getStringCellValue().trim();
					String altPhone = row.getCell(ALTPHONE_INDEX, Row.CREATE_NULL_AS_BLANK).getStringCellValue().trim();
					String dob = row.getCell(DOB_INDEX, Row.CREATE_NULL_AS_BLANK).getStringCellValue().trim();
					String regDate = row.getCell(REGDATE_INDEX, Row.CREATE_NULL_AS_BLANK).getStringCellValue().trim();
					String isLateral = row.getCell(ISLATERAL_INDEX, Row.CREATE_NULL_AS_BLANK).getStringCellValue().trim();

					if("1".equals(isLateral)){
						//Salesforce gives value as 1/0 not as Y/N, So convert to Y/N
						isLateral = "Y";
					}else if("0".equals(isLateral)){
						//Salesforce gives value as 1/0 not as Y/N, So convert to Y/N
						isLateral = "N";
					}

					String address = row.getCell(ADDRESS_INDEX, Row.CREATE_NULL_AS_BLANK).getStringCellValue().trim();
					String city = row.getCell(CITY_INDEX, Row.CREATE_NULL_AS_BLANK).getStringCellValue().trim();
					String state = row.getCell(STATE_INDEX, Row.CREATE_NULL_AS_BLANK).getStringCellValue().trim();
					String country = row.getCell(COUNTRY_INDEX, Row.CREATE_NULL_AS_BLANK).getStringCellValue().trim();
					String pin = row.getCell(PIN_INDEX, Row.CREATE_NULL_AS_BLANK).getStringCellValue().trim();
					String centerCode = row.getCell(CENTERCODE_INDEX, Row.CREATE_NULL_AS_BLANK).getStringCellValue().trim();
					String centerName = row.getCell(CENTERNAME_INDEX, Row.CREATE_NULL_AS_BLANK).getStringCellValue().trim();
					String validityEndMonth = row.getCell(VALIDITYENDMONTH_INDEX, Row.CREATE_NULL_AS_BLANK).getStringCellValue().trim();
					String validityEndYear = row.getCell(VALIDITYENDYEAR_INDEX, Row.CREATE_NULL_AS_BLANK).getStringCellValue().trim();
					String programStructure = row.getCell(PROGRAM_STUCTURE_INDEX, Row.CREATE_NULL_AS_BLANK).getStringCellValue().trim();
					String studentImageUrl = row.getCell(STUDENT_IMAGE_URL_INDEX, Row.CREATE_NULL_AS_BLANK).getStringCellValue().trim();
					String previousStudentId = row.getCell(PREVIOUS_STUDENT_ID, Row.CREATE_NULL_AS_BLANK).getStringCellValue().trim();

					if("Jun".equalsIgnoreCase(validityEndMonth) || "June".equalsIgnoreCase(validityEndMonth)){
						validityEndMonth = "Jun";
					}else if("Dec".equalsIgnoreCase(validityEndMonth) || "December".equalsIgnoreCase(validityEndMonth)){
						validityEndMonth = "Dec";
					}

					if("".equals(sapId.trim()) && "".equals(lastName.trim()) && 
							"".equals(firstName.trim()) && "".equals(program.trim()) && "".equals(sem)){
						break;
					}

					if(middleName == null || "".equals(middleName.trim())){
						middleName = fatherName;
					}


					bean.setSapid(sapId);
					bean.setLastName(lastName);
					bean.setFirstName(firstName);
					bean.setMiddleName(middleName);
					bean.setFatherName(fatherName);
					bean.setHusbandName(husbandName);
					bean.setMotherName(motherName);
					bean.setGender(gender);
					bean.setProgram(program);
					bean.setOldProgram(program);//Before student has not changed program, Old and New Program values will be same
					bean.setEnrollmentMonth(enrollmentMonth);
					bean.setEnrollmentYear(enrollmentYear);
					bean.setSem(sem);
					bean.setEmailId(emailId);
					bean.setMobile(mobile);
					bean.setAltPhone(altPhone);
					bean.setDob(dob);
					bean.setRegDate(regDate);
					bean.setIsLateral(isLateral);
					bean.setAddress(address.replaceAll("\"", ""));
					bean.setCity(city);
					bean.setState(state);
					bean.setCountry(country);
					bean.setPin(pin);
					bean.setCenterCode(centerCode);
					bean.setCenterName(centerName);
					bean.setValidityEndMonth(validityEndMonth);
					bean.setValidityEndYear(validityEndYear);
					bean.setPrgmStructApplicable(programStructure);
					bean.setCreatedBy(userId);
					bean.setLastModifiedBy(userId);
					bean.setImageUrl(studentImageUrl);
					bean.setPreviousStudentId(previousStudentId);

					if(sapId.length() != 11){
						bean.setErrorMessage(bean.getErrorMessage()+" Row : "+ (i+1) +" Invalid SAPID '"+sapId+"'");
						bean.setErrorRecord(true);
					}
					/*if(!("India".equalsIgnoreCase(country))){
						bean.setErrorMessage(bean.getErrorMessage()+" Row : "+ (i+1) +" Invalid Country for SAPID '"+sapId+"'");
						bean.setErrorRecord(true);
					}*/
					if((!"1".equals(sem)) &&  (!"2".equals(sem))  &&   (!"3".equals(sem))  &&  (!"4".equals(sem))){
						bean.setErrorMessage(bean.getErrorMessage()+" Row : "+ (i+1) +" Invalid Semester for record with SAPID:"+bean.getSapid()+ " & Sem:"+sem);
						bean.setErrorRecord(true);
					}

					if((!"Jul2008".equals(programStructure)) &&  (!"Jul2009".equals(programStructure))  &&   (!"Jul2013".equals(programStructure))  &&  (!"Jul2014".equals(programStructure)) &&  (!"Jul2017".equals(programStructure))){
						bean.setErrorMessage(bean.getErrorMessage()+" Row : "+ (i+1) +" Invalid Program Structure for record with SAPID: "+bean.getSapid());
						bean.setErrorRecord(true);
					}

					if("".equals(firstName) || "".equals(lastName) || "".equals(gender)){
						bean.setErrorMessage(bean.getErrorMessage()+" Row : "+ (i+1) +" First Name / Last Name / Gender cannot be empty.");
						bean.setErrorRecord(true);
					}

					if(!("Male".equals(gender) || "Female".equals(gender))){
						bean.setErrorMessage(bean.getErrorMessage()+" Row : "+ (i+1) +" Invalid value for Gender.");
						bean.setErrorRecord(true);
					}

					if(!("Y".equals(isLateral) || "N".equals(isLateral))){
						bean.setErrorMessage(bean.getErrorMessage()+" Row : "+ (i+1) +" Invalid value for Is Lateral Column. Enter Y/N");
						bean.setErrorRecord(true);
					}

					if("".equals(enrollmentMonth) || "".equals(enrollmentYear) || "".equals(validityEndMonth) | "".equals(validityEndYear)){
						bean.setErrorMessage(bean.getErrorMessage()+" Row : "+ (i+1) +" Validity and Enrollment Month/Year cannot be empty.");
						bean.setErrorRecord(true);
					}

					if(!programList.contains(bean.getProgram())){
						bean.setErrorMessage(bean.getErrorMessage()+" Row : "+ (i+1) +" Invalid Program for record with SAPID:"+bean.getSapid()+ " & PROGRAM:"+bean.getProgram());
						bean.setErrorRecord(true);
					}

					if(!centersList.contains(bean.getCenterCode())){
						bean.setErrorMessage(bean.getErrorMessage()+" Row : "+ (i+1) +" Invalid Center Code for record with SAPID:"+bean.getSapid()+ " & Center Code:"+bean.getCenterCode() + " & Center Name: "+ bean.getCenterName());
						bean.setErrorRecord(true);
					}

					if(!(yearList.contains(enrollmentYear) && ("Jan".equals(enrollmentMonth) || "Jul".equals(enrollmentMonth)))){
						bean.setErrorMessage(bean.getErrorMessage()+" Row : "+ (i+1) +" Invalid Enrollment Year/Month for record with SAPID:"+bean.getSapid()+" Valid Enrollment Month are Jan/Jul");
						bean.setErrorRecord(true);
					}
					if(!(yearList.contains(validityEndYear) && ("Jun".equals(validityEndMonth) || "Dec".equals(validityEndMonth)))){
						bean.setErrorMessage(bean.getErrorMessage()+" Row : "+ (i+1) +" Invalid Validity Year/Month for record with SAPID:"+bean.getSapid()+" Valid Enrollment Month are Jun/Dec");
						bean.setErrorRecord(true);
					}
					if(!sapIdSemKeys.containsKey(sapId+sem)){
						sapIdSemKeys.put(sapId+sem, null);
					}else{
						bean.setErrorMessage(bean.getErrorMessage()+" Row : "+ (i+1) +" Duplicate Entry for record with SAPID:"+sapId+ " & Sem :"+sem);
						bean.setErrorRecord(true);
					}

					if(bean.isErrorRecord()){
						errorBeanList.add(bean);
					}else{
						marksBeanList.add(bean);
					}


					if(i % 1000 == 0){
					}

				}
			}
		} catch (IOException e) {
			
		}
		resultList.add(marksBeanList);
		resultList.add(errorBeanList);
		return resultList;
	}


	public ArrayList<List> readStudentImageExcel(FileBean fileBean, String userId){


		int SAPID_INDEX = 0;
		int IMAGEURL_INDEX = 1;


		ByteArrayInputStream bis = new ByteArrayInputStream(fileBean.getFileData().getBytes());
		Workbook workbook;
		List<StudentExamBean> marksBeanList = new ArrayList<StudentExamBean>();
		List<StudentExamBean> errorBeanList = new ArrayList<StudentExamBean>();
		ArrayList<List> resultList = new ArrayList<>();
		HashMap<String, String> sapIdSemKeys = new HashMap<>();

		try {
			if (fileBean.getFileData().getOriginalFilename().endsWith("xls")) {
				workbook = new HSSFWorkbook(bis);
			} else if (fileBean.getFileData().getOriginalFilename().endsWith("xlsx")) {
				workbook = new XSSFWorkbook(bis);
			} else {
				throw new IllegalArgumentException("Received file does not have a standard excel extension.");
			}

			//Get first/desired sheet from the workbook
			XSSFSheet sheet = (XSSFSheet) workbook.getSheetAt(0);

			Iterator<Row> rowIterator = sheet.iterator();

			int i = 0;
			//Skip first row since it contains column names, not data.
			if(rowIterator.hasNext()){
				Row row = rowIterator.next();
			}
			while(rowIterator.hasNext()) {
				i++;
				Row row = rowIterator.next();


				if(row!=null){

					row.getCell(SAPID_INDEX, Row.CREATE_NULL_AS_BLANK).setCellType(Cell.CELL_TYPE_STRING);
					row.getCell(IMAGEURL_INDEX, Row.CREATE_NULL_AS_BLANK).setCellType(Cell.CELL_TYPE_STRING);


					StudentExamBean bean = new StudentExamBean();

					String sapId = row.getCell(SAPID_INDEX, Row.CREATE_NULL_AS_BLANK).getStringCellValue().trim();
					String imageUrl = row.getCell(IMAGEURL_INDEX, Row.CREATE_NULL_AS_BLANK).getStringCellValue().trim();


					if("".equals(sapId.trim()) && "".equals(imageUrl.trim()) ){
						break;
					}



					bean.setSapid(sapId);
					bean.setImageUrl(imageUrl);
					bean.setLastModifiedBy(userId);



					if(sapId.length() != 11){
						bean.setErrorMessage(bean.getErrorMessage()+" Row : "+ (i+1) +" Invalid SAPID '"+sapId+"'");
						bean.setErrorRecord(true);
					}

					if(bean.isErrorRecord()){
						errorBeanList.add(bean);
					}else{
						marksBeanList.add(bean);
					}


					if(i % 1000 == 0){
					}

				}
			}
		} catch (IOException e) {
			
		}
		resultList.add(marksBeanList);
		resultList.add(errorBeanList);
		return resultList;
	}

	private void validateSAPID(StudentExamBean bean, int i) {
		String sapId = bean.getSapid();
		try {
			if(sapId.length() != 11){
				bean.setErrorMessage(bean.getErrorMessage()+" Row : "+ (i+1) +" Invalid SAPID '"+sapId+"'");
				bean.setErrorRecord(true);
			}
		} catch (Exception e) {
			bean.setErrorMessage(bean.getErrorMessage()+" Row : "+ (i+1) +" Unknown Error in this row. Please verify");
			bean.setErrorRecord(true);
		}

	}

	private void validateSAPIDInMarksBean(StudentMarksBean bean, int i) {
		String sapId = bean.getSapid();
		try {
			if(sapId.length() != SAP_ID_LENGTH){ //11){
				logger.error("validateSAPIDInMarksBean : "+bean.getErrorMessage()+" Row : "+ (i+1) +" Invalid SAPID '"+sapId+"'");
				bean.setErrorMessage(bean.getErrorMessage()+" Row : "+ (i+1) +" Invalid SAPID '"+sapId+"'");
				bean.setErrorRecord(true);
			}
		} catch (Exception e) {
			logger.error("validateSAPIDInMarksBean : "+bean.getErrorMessage()+" Row : "+ (i+1) +" Unknown Error in this row. Please verify");
			bean.setErrorMessage(bean.getErrorMessage()+" Row : "+ (i+1) +" Unknown Error in this row. Please verify");
			bean.setErrorRecord(true);
		}

	}

	public ArrayList<List> readHDFCTransactionExcel(FileBean fileBean,	ArrayList<String> programList, ArrayList<String> subjectList,	String userId) throws IOException {

		int index = 0;

		int  PAYMENTID_INDEX = index++;
		int  DATE_INDEX = index++;
		int  ACCOUNTID_INDEX = index++;
		int  PAYMENT_METHOD_INDEX = index++;
		int  MERCHANT_REF_NO_INDEX = index++;
		int  CUSTOMER_INDEX = index++;
		int  EMAIL_INDEX = index++;
		int  AMOUNT_INDEX = index++;
		int  STATUS_INDEX = index++;


		ByteArrayInputStream bis = new ByteArrayInputStream(fileBean.getFileData().getBytes());
		Workbook workbook;
		List<ExamBookingTransactionBean> transactionStatusList = new ArrayList<ExamBookingTransactionBean>();
		List<ExamBookingTransactionBean> errorBeanList = new ArrayList<ExamBookingTransactionBean>();
		ArrayList<List> resultList = new ArrayList<>();
		HashMap<String, String> sapidSubjectKeys = new HashMap<>();
		HashMap<String, String> subjectDateMap = new HashMap<>();

		try {
			if (fileBean.getFileData().getOriginalFilename().endsWith("xls")) {
				workbook = new HSSFWorkbook(bis);
			} else if (fileBean.getFileData().getOriginalFilename().endsWith("xlsx")) {
				workbook = new XSSFWorkbook(bis);
			} else {
				throw new IllegalArgumentException("Received file does not have a standard excel extension.");
			}

			//Get first/desired sheet from the workbook
			XSSFSheet sheet = (XSSFSheet) workbook.getSheetAt(0);

			Iterator<Row> rowIterator = sheet.iterator();

			int i = 0;
			//Skip first row since it contains column names, not data.
			if(rowIterator.hasNext()){
				Row row = rowIterator.next();
			}
			while(rowIterator.hasNext()) {
				i++;
				Row row = rowIterator.next();

				if(row!=null){

					row.getCell(PAYMENTID_INDEX, Row.CREATE_NULL_AS_BLANK).setCellType(Cell.CELL_TYPE_STRING);
					row.getCell(DATE_INDEX, Row.CREATE_NULL_AS_BLANK).setCellType(Cell.CELL_TYPE_STRING);
					row.getCell(ACCOUNTID_INDEX, Row.CREATE_NULL_AS_BLANK).setCellType(Cell.CELL_TYPE_STRING);
					row.getCell(PAYMENT_METHOD_INDEX, Row.CREATE_NULL_AS_BLANK).setCellType(Cell.CELL_TYPE_STRING);
					row.getCell(MERCHANT_REF_NO_INDEX, Row.CREATE_NULL_AS_BLANK).setCellType(Cell.CELL_TYPE_STRING);
					row.getCell(CUSTOMER_INDEX, Row.CREATE_NULL_AS_BLANK).setCellType(Cell.CELL_TYPE_STRING);
					row.getCell(EMAIL_INDEX, Row.CREATE_NULL_AS_BLANK).setCellType(Cell.CELL_TYPE_STRING);
					row.getCell(AMOUNT_INDEX, Row.CREATE_NULL_AS_BLANK).setCellType(Cell.CELL_TYPE_STRING);
					row.getCell(STATUS_INDEX, Row.CREATE_NULL_AS_BLANK).setCellType(Cell.CELL_TYPE_STRING);

					ExamBookingTransactionBean bean = new ExamBookingTransactionBean();


					String paymentId = row.getCell(PAYMENTID_INDEX, Row.CREATE_NULL_AS_BLANK).getStringCellValue();
					String respTranDateTime = row.getCell(DATE_INDEX, Row.CREATE_NULL_AS_BLANK).getStringCellValue();
					String accountId = row.getCell(ACCOUNTID_INDEX, Row.CREATE_NULL_AS_BLANK).getStringCellValue();
					String respPaymentMethod = row.getCell(PAYMENT_METHOD_INDEX, Row.CREATE_NULL_AS_BLANK).getStringCellValue();
					String merchantRefNo = row.getCell(MERCHANT_REF_NO_INDEX, Row.CREATE_NULL_AS_BLANK).getStringCellValue();
					String customer = row.getCell(CUSTOMER_INDEX, Row.CREATE_NULL_AS_BLANK).getStringCellValue();
					String email = row.getCell(EMAIL_INDEX, Row.CREATE_NULL_AS_BLANK).getStringCellValue();
					String respAmount = row.getCell(AMOUNT_INDEX, Row.CREATE_NULL_AS_BLANK).getStringCellValue();
					String respTranStatus = row.getCell(STATUS_INDEX, Row.CREATE_NULL_AS_BLANK).getStringCellValue();


					if("".equals(paymentId.trim()) && "".equals(respTranDateTime.trim()) && "".equals(accountId)){
						break;
					}

					bean.setPaymentID(paymentId);
					bean.setRespTranDateTime(respTranDateTime);
					bean.setRespPaymentMethod(respPaymentMethod);
					bean.setMerchantRefNo(merchantRefNo);
					bean.setFirstName(customer);
					bean.setEmailId(email);
					bean.setRespAmount(respAmount);
					bean.setRespTranStatus(respTranStatus);


					if(bean.isErrorRecord()){
						errorBeanList.add(bean);
					}else if("Captured".equalsIgnoreCase(respTranStatus) || "Authorized".equalsIgnoreCase(respTranStatus)){
						transactionStatusList.add(bean);
					}


					if(i % 1000 == 0){
					}
				}
			}
		} catch (IOException e) {
			
			throw e;
		}
		resultList.add(transactionStatusList);
		resultList.add(errorBeanList);
		return resultList;
	}

	public ArrayList<List> readCentersExcel(FileBean fileBean, String userId) {	// TODO Auto-generated method stub

		int index = 0;

		int  CENTER_NAME_INDEX = index++;
		int  SFDC_ID_INDEX = index++;
		int  CENTER_CODE_INDEX = index++;
		int  ADDRESS_INDEX = index++;
		int  STATE_NO_INDEX = index++;
		int  CITY_INDEX = index++;
		int  LC_INDEX = index++;
		int  ACTIVE_INDEX = index++;



		ByteArrayInputStream bis = new ByteArrayInputStream(fileBean.getFileData().getBytes());
		Workbook workbook;
		List<CenterExamBean> centersList = new ArrayList<CenterExamBean>();
		List<CenterExamBean> errorBeanList = new ArrayList<CenterExamBean>();
		ArrayList<List> resultList = new ArrayList<>();
		HashMap<String, String> sapidSubjectKeys = new HashMap<>();
		HashMap<String, String> subjectDateMap = new HashMap<>();

		try {
			if (fileBean.getFileData().getOriginalFilename().endsWith("xls")) {
				workbook = new HSSFWorkbook(bis);
			} else if (fileBean.getFileData().getOriginalFilename().endsWith("xlsx")) {
				workbook = new XSSFWorkbook(bis);
			} else {
				throw new IllegalArgumentException("Received file does not have a standard excel extension.");
			}

			//Get first/desired sheet from the workbook
			XSSFSheet sheet = (XSSFSheet) workbook.getSheetAt(0);

			Iterator<Row> rowIterator = sheet.iterator();

			int i = 0;
			//Skip first row since it contains column names, not data.
			if(rowIterator.hasNext()){
				Row row = rowIterator.next();
			}
			while(rowIterator.hasNext()) {
				i++;
				Row row = rowIterator.next();

				if(row!=null){

					row.getCell(CENTER_NAME_INDEX, Row.CREATE_NULL_AS_BLANK).setCellType(Cell.CELL_TYPE_STRING);
					row.getCell(SFDC_ID_INDEX, Row.CREATE_NULL_AS_BLANK).setCellType(Cell.CELL_TYPE_STRING);
					row.getCell(CENTER_CODE_INDEX, Row.CREATE_NULL_AS_BLANK).setCellType(Cell.CELL_TYPE_STRING);
					row.getCell(ADDRESS_INDEX, Row.CREATE_NULL_AS_BLANK).setCellType(Cell.CELL_TYPE_STRING);
					row.getCell(STATE_NO_INDEX, Row.CREATE_NULL_AS_BLANK).setCellType(Cell.CELL_TYPE_STRING);
					row.getCell(CITY_INDEX, Row.CREATE_NULL_AS_BLANK).setCellType(Cell.CELL_TYPE_STRING);
					row.getCell(LC_INDEX, Row.CREATE_NULL_AS_BLANK).setCellType(Cell.CELL_TYPE_STRING);
					row.getCell(ACTIVE_INDEX, Row.CREATE_NULL_AS_BLANK).setCellType(Cell.CELL_TYPE_STRING);

					CenterExamBean bean = new CenterExamBean();


					String centerName = row.getCell(CENTER_NAME_INDEX, Row.CREATE_NULL_AS_BLANK).getStringCellValue();
					String sfdcId = row.getCell(SFDC_ID_INDEX, Row.CREATE_NULL_AS_BLANK).getStringCellValue();
					String centerCode = row.getCell(CENTER_CODE_INDEX, Row.CREATE_NULL_AS_BLANK).getStringCellValue();
					String address = row.getCell(ADDRESS_INDEX, Row.CREATE_NULL_AS_BLANK).getStringCellValue();
					String state = row.getCell(STATE_NO_INDEX, Row.CREATE_NULL_AS_BLANK).getStringCellValue();
					String city = row.getCell(CITY_INDEX, Row.CREATE_NULL_AS_BLANK).getStringCellValue();
					String lc = row.getCell(LC_INDEX, Row.CREATE_NULL_AS_BLANK).getStringCellValue();
					String active = row.getCell(ACTIVE_INDEX, Row.CREATE_NULL_AS_BLANK).getStringCellValue();



					if("".equals(centerName.trim()) && "".equals(sfdcId.trim()) && "".equals(centerCode)){
						break;
					}

					bean.setCenterName(centerName);
					bean.setSfdcId(sfdcId);
					bean.setCenterCode(sfdcId);//Setting SFDC ID for centerCode since centerCodes are set manually in Salesforce and too many mistakes
					bean.setAddress(address);
					bean.setState(state);
					bean.setCity(city);
					bean.setLc(lc);
					bean.setActive(active);


					if(bean.isErrorRecord()){
						errorBeanList.add(bean);
					}else{
						centersList.add(bean);
					}


					if(i % 1000 == 0){
					}
				}
			}
		} catch (IOException e) {
			
		}
		resultList.add(centersList);
		resultList.add(errorBeanList);
		return resultList;
	}
		/*
		 * 11/16/2017 
		 * public void buildExcelDocumentForRegistrationReportHome(
				HashMap<String,String> mapOfStudentNumberSemAndCountOfFailedSubjects,
				HashMap<String,String> mapOfStudentNumberAndSemAndCountOfANSSubjects,
				HashMap<String,String> mapOfStudentNumberAndSemAndCountOfTEEMissingSubjects,
				HashMap<String,String> mapOfStudentNumberAndSemAndGAPInReReg,
				HashMap<String,String> mapOfStudentNumberAndSemAndPendingNumberOfExamBookings,
				HashMap<String,String> mapOfStudentNumberAndSemAndCountOfSessionsAttended,
				HashMap<String,String> mapOfStudentNumberAndSemAndDriveMonthYear,
				HttpServletResponse response,
				ArrayList<String> listOfSapIdOfActiveStudents
				){
			XSSFWorkbook workbook =new XSSFWorkbook();
			
			XSSFSheet sheet = workbook.createSheet("ReRegistrationReport");
			int index = 0,attributeIndex = 1;
			XSSFRow header = sheet.createRow(0);
			XSSFRow parametersHeader = sheet.createRow(1);
			header.createCell(0).setCellValue("SAPID");
			header.createCell(1).setCellValue("Semester 1");
			header.createCell(8).setCellValue("Semester 2");
			header.createCell(15).setCellValue("Semester 3");
			header.createCell(22).setCellValue("Semester 4");
			
			
			sheet.addMergedRegion(new CellRangeAddress(0,0,1,7));
			sheet.addMergedRegion(new CellRangeAddress(0,0,8,14));
			sheet.addMergedRegion(new CellRangeAddress(0,0,15,21));
			sheet.addMergedRegion(new CellRangeAddress(0,0,22,28));
			
			
			parametersHeader.createCell(attributeIndex++).setCellValue("Failed Subject");
			parametersHeader.createCell(attributeIndex++).setCellValue("GAP");
			parametersHeader.createCell(attributeIndex++).setCellValue("TEE Absent");
			parametersHeader.createCell(attributeIndex++).setCellValue("ANS");
			parametersHeader.createCell(attributeIndex++).setCellValue("Pending Booking Count");
			parametersHeader.createCell(attributeIndex++).setCellValue("Sessions Attended");
			parametersHeader.createCell(attributeIndex++).setCellValue("Date Of Re-Registering");
			
			parametersHeader.createCell(attributeIndex++).setCellValue("Failed Subject");
			parametersHeader.createCell(attributeIndex++).setCellValue("GAP");
			parametersHeader.createCell(attributeIndex++).setCellValue("TEE Absent");
			parametersHeader.createCell(attributeIndex++).setCellValue("ANS");
			parametersHeader.createCell(attributeIndex++).setCellValue("Pending Booking Count");
			parametersHeader.createCell(attributeIndex++).setCellValue("Sessions Attended");
			parametersHeader.createCell(attributeIndex++).setCellValue("Date Of Re-Registering");
			
			
			parametersHeader.createCell(attributeIndex++).setCellValue("Failed Subject");
			parametersHeader.createCell(attributeIndex++).setCellValue("GAP");
			parametersHeader.createCell(attributeIndex++).setCellValue("TEE Absent");
			parametersHeader.createCell(attributeIndex++).setCellValue("ANS");
			parametersHeader.createCell(attributeIndex++).setCellValue("Pending Booking Count");
			parametersHeader.createCell(attributeIndex++).setCellValue("Sessions Attended");
			parametersHeader.createCell(attributeIndex++).setCellValue("Date Of Re-Registering");
			
			parametersHeader.createCell(attributeIndex++).setCellValue("Failed Subject");
			parametersHeader.createCell(attributeIndex++).setCellValue("GAP");
			parametersHeader.createCell(attributeIndex++).setCellValue("Failed Subject");
			parametersHeader.createCell(attributeIndex++).setCellValue("GAP");
			parametersHeader.createCell(attributeIndex++).setCellValue("Failed Subject");
			parametersHeader.createCell(attributeIndex++).setCellValue("GAP");
			parametersHeader.createCell(attributeIndex++).setCellValue("Failed Subject");
			
			int rowNum = 2;
			for(int i = 0;i<listOfSapIdOfActiveStudents.size();i++){
				index = 0;
				String sapid = listOfSapIdOfActiveStudents.get(i);
				XSSFRow row = sheet.createRow(rowNum++);
				
				row.createCell(index++).setCellValue(sapid);
				row.createCell(index++).setCellValue(mapOfStudentNumberSemAndCountOfFailedSubjects.get(sapid + "-1") == null ? "0": mapOfStudentNumberSemAndCountOfFailedSubjects.get(sapid + "-1"));
				row.createCell(index++).setCellValue(mapOfStudentNumberAndSemAndCountOfANSSubjects.get(sapid + "-1") == null ? "0": mapOfStudentNumberAndSemAndCountOfANSSubjects.get(sapid + "-1"));
				row.createCell(index++).setCellValue(mapOfStudentNumberAndSemAndCountOfTEEMissingSubjects.get(sapid + "-1") == null ? "0": mapOfStudentNumberAndSemAndCountOfTEEMissingSubjects.get(sapid + "-1"));
				row.createCell(index++).setCellValue(mapOfStudentNumberAndSemAndGAPInReReg.get(sapid + "-1") == null ? "0": mapOfStudentNumberAndSemAndGAPInReReg.get(sapid + "-1"));
				row.createCell(index++).setCellValue(mapOfStudentNumberAndSemAndPendingNumberOfExamBookings.get(sapid + "-1") == null ? "0": mapOfStudentNumberAndSemAndPendingNumberOfExamBookings.get(sapid + "-1"));
				row.createCell(index++).setCellValue(mapOfStudentNumberAndSemAndCountOfSessionsAttended.get(sapid + "-1") == null ? "0": mapOfStudentNumberAndSemAndCountOfSessionsAttended.get(sapid + "-1"));
				row.createCell(index++).setCellValue(mapOfStudentNumberAndSemAndDriveMonthYear.get(sapid + "-1") == null ? "0": mapOfStudentNumberAndSemAndDriveMonthYear.get(sapid + "-1"));
				
				row.createCell(index++).setCellValue(mapOfStudentNumberSemAndCountOfFailedSubjects.get(sapid + "-2") == null ? "0": mapOfStudentNumberSemAndCountOfFailedSubjects.get(sapid + "-2"));
				row.createCell(index++).setCellValue(mapOfStudentNumberAndSemAndCountOfANSSubjects.get(sapid + "-2") == null ? "0": mapOfStudentNumberAndSemAndCountOfANSSubjects.get(sapid + "-2"));
				row.createCell(index++).setCellValue(mapOfStudentNumberAndSemAndCountOfTEEMissingSubjects.get(sapid + "-2") == null ? "0": mapOfStudentNumberAndSemAndCountOfTEEMissingSubjects.get(sapid + "-2"));
				row.createCell(index++).setCellValue(mapOfStudentNumberAndSemAndGAPInReReg.get(sapid + "-2") == null ? "0": mapOfStudentNumberAndSemAndGAPInReReg.get(sapid + "-2"));
				row.createCell(index++).setCellValue(mapOfStudentNumberAndSemAndPendingNumberOfExamBookings.get(sapid + "-2") == null ? "0": mapOfStudentNumberAndSemAndPendingNumberOfExamBookings.get(sapid + "-2"));
				row.createCell(index++).setCellValue(mapOfStudentNumberAndSemAndCountOfSessionsAttended.get(sapid + "-2") == null ? "0": mapOfStudentNumberAndSemAndCountOfSessionsAttended.get(sapid + "-2"));
				row.createCell(index++).setCellValue(mapOfStudentNumberAndSemAndDriveMonthYear.get(sapid + "-2") == null ? "0": mapOfStudentNumberAndSemAndDriveMonthYear.get(sapid + "-2"));
				
				row.createCell(index++).setCellValue(mapOfStudentNumberSemAndCountOfFailedSubjects.get(sapid + "-3") == null ? "0": mapOfStudentNumberSemAndCountOfFailedSubjects.get(sapid + "-3"));
				row.createCell(index++).setCellValue(mapOfStudentNumberAndSemAndCountOfANSSubjects.get(sapid + "-3") == null ? "0": mapOfStudentNumberAndSemAndCountOfANSSubjects.get(sapid + "-3"));
				row.createCell(index++).setCellValue(mapOfStudentNumberAndSemAndCountOfTEEMissingSubjects.get(sapid + "-3") == null ? "0": mapOfStudentNumberAndSemAndCountOfTEEMissingSubjects.get(sapid + "-3"));
				row.createCell(index++).setCellValue(mapOfStudentNumberAndSemAndGAPInReReg.get(sapid + "-3") == null ? "0": mapOfStudentNumberAndSemAndGAPInReReg.get(sapid + "-3"));
				row.createCell(index++).setCellValue(mapOfStudentNumberAndSemAndPendingNumberOfExamBookings.get(sapid + "-3") == null ? "0": mapOfStudentNumberAndSemAndPendingNumberOfExamBookings.get(sapid + "-3"));
				row.createCell(index++).setCellValue(mapOfStudentNumberAndSemAndCountOfSessionsAttended.get(sapid + "-3") == null ? "0": mapOfStudentNumberAndSemAndCountOfSessionsAttended.get(sapid + "-3"));
				row.createCell(index++).setCellValue(mapOfStudentNumberAndSemAndDriveMonthYear.get(sapid + "-3") == null ? "0": mapOfStudentNumberAndSemAndDriveMonthYear.get(sapid + "-3"));
				
				
				row.createCell(index++).setCellValue(mapOfStudentNumberSemAndCountOfFailedSubjects.get(sapid + "-4") == null ? "0": mapOfStudentNumberSemAndCountOfFailedSubjects.get(sapid + "-4"));
				row.createCell(index++).setCellValue(mapOfStudentNumberAndSemAndCountOfANSSubjects.get(sapid + "-4") == null ? "0": mapOfStudentNumberAndSemAndCountOfANSSubjects.get(sapid + "-4"));
				row.createCell(index++).setCellValue(mapOfStudentNumberAndSemAndCountOfTEEMissingSubjects.get(sapid + "-4") == null ? "0": mapOfStudentNumberAndSemAndCountOfTEEMissingSubjects.get(sapid + "-4"));
				row.createCell(index++).setCellValue(mapOfStudentNumberAndSemAndGAPInReReg.get(sapid + "-4") == null ? "0": mapOfStudentNumberAndSemAndGAPInReReg.get(sapid + "-4"));
				row.createCell(index++).setCellValue(mapOfStudentNumberAndSemAndPendingNumberOfExamBookings.get(sapid + "-4") == null ? "0": mapOfStudentNumberAndSemAndPendingNumberOfExamBookings.get(sapid + "-4"));
				row.createCell(index++).setCellValue(mapOfStudentNumberAndSemAndCountOfSessionsAttended.get(sapid + "-4") == null ? "0": mapOfStudentNumberAndSemAndCountOfSessionsAttended.get(sapid + "-4"));
				row.createCell(index++).setCellValue(mapOfStudentNumberAndSemAndDriveMonthYear.get(sapid + "-4") == null ? "0": mapOfStudentNumberAndSemAndDriveMonthYear.get(sapid + "-4"));
				
				
				
				
			}
			 try{
				 	ByteArrayOutputStream outByteStream = new ByteArrayOutputStream();
					workbook.write(outByteStream);
					byte [] outArray = outByteStream.toByteArray();
					response.setContentType("application/ms-excel");
					response.setContentLength(outArray.length);
					response.setHeader("Expires:", "0"); // eliminates browser caching
					response.setHeader("Content-Disposition", "attachment; filename=PassFailRecords.xls");
					ServletOutputStream outStream = response.getOutputStream();
					outStream.write(outArray);
					outStream.flush();
				 
			 }catch(Exception e){
				 
			 }
			 
			
			
		}*/
		
	// download PassFail Records Report 
		/*11/16/2017
		 * 
		 * 
		 * 
		 * public void buildExcelDocumentForPassFailReport(List<PassFailBean> studentMarksList,HttpServletResponse response,StudentMarksDAO dao) throws IOException
		{
			HashMap<String, StudentBean> sapIdStudentsMap = dao.getAllStudents();
			HashMap<String, CenterBean> icLcMap = dao.getICLCMap();
			
			//create a worksheet
			 SXSSFWorkbook workbook = new SXSSFWorkbook();
				SXSSFSheet sheet = (SXSSFSheet)workbook.createSheet("Pass Fail Results");
			XSSFWorkbook workbook =new XSSFWorkbook();
			XSSFSheet sheet = workbook.createSheet("Pass Fail Results");
				
			int index = 0;
			Row header = sheet.createRow(0);
			
			header.createCell(0).setCellValue("Sr. No.");
			header.createCell(1).setCellValue("Written Year");
			header.createCell(2).setCellValue("Written Month");
			header.createCell(3).setCellValue("Assignment Year");
			header.createCell(4).setCellValue("Assignment Month");
			header.createCell(5).setCellValue("SAP ID");
			
			header.createCell(6).setCellValue("Email ID");
			header.createCell(7).setCellValue("Mobile No.");
			header.createCell(8).setCellValue("Validity End Year");
			header.createCell(9).setCellValue("Validity End Month");
			
			header.createCell(10).setCellValue("Student Name");
			header.createCell(11).setCellValue("Program");
			header.createCell(12).setCellValue("Sem");
			header.createCell(13).setCellValue("Subject");
			header.createCell(14).setCellValue("TEE");
			header.createCell(15).setCellValue("Assignment");
			header.createCell(16).setCellValue("Grace");
			header.createCell(17).setCellValue("Total");
			header.createCell(18).setCellValue("Pass");
			header.createCell(19).setCellValue("Fail Reason");
			header.createCell(20).setCellValue("IC");
			header.createCell(21).setCellValue("LC");
			header.createCell(22).setCellValue("Exam Mode");
			header.createCell(23).setCellValue("Enrollment Year");
			header.createCell(24).setCellValue("Enrollment Month");
			header.createCell(25).setCellValue("Program Structure");
	 
			int rowNum = 1;
			for (int i = 0 ; i < studentMarksList.size(); i++) {
				PassFailBean bean = studentMarksList.get(i);
				StudentBean student = sapIdStudentsMap.get(bean.getSapid());
				
				String ic = "";
				String lc = "";
				String examMode = "Offline";
				if(student != null){
					ic = student.getCenterName();
					CenterBean center = icLcMap.get(student.getCenterCode()); 
					if(center != null){
						lc = center.getLc();
					}
					
					if("Online".equals(student.getExamMode())){
						examMode = "Online";
					}
				}
				
				//create the row data
				SXSSFRow row = (SXSSFRow) sheet.createRow(rowNum++);
				
				row.createCell(0).setCellValue(rowNum);
				row.createCell(1).setCellValue(bean.getWrittenYear());
				row.createCell(2).setCellValue(bean.getWrittenMonth());
				row.createCell(3).setCellValue(bean.getAssignmentYear());
				row.createCell(4).setCellValue(bean.getAssignmentMonth());
				row.createCell(5).setCellValue(bean.getSapid());
				
				row.createCell(6).setCellValue(student.getEmailId());
				row.createCell(7).setCellValue(student.getMobile());
				row.createCell(8).setCellValue(student.getValidityEndMonth());
				row.createCell(9).setCellValue(student.getValidityEndYear());
				
				
				row.createCell(10).setCellValue(bean.getName());
				row.createCell(11).setCellValue(bean.getProgram());
				row.createCell(12).setCellValue(bean.getSem());
				row.createCell(13).setCellValue(bean.getSubject());
				row.createCell(14).setCellValue(bean.getWrittenscore());
				row.createCell(15).setCellValue(bean.getAssignmentscore());
				row.createCell(16).setCellValue(bean.getGracemarks());
				row.createCell(17).setCellValue(bean.getTotal());
				row.createCell(18).setCellValue(bean.getIsPass());
				row.createCell(19).setCellValue(bean.getFailReason());
				row.createCell(20).setCellValue(ic);
				row.createCell(21).setCellValue(lc);
				row.createCell(22).setCellValue(examMode);
				row.createCell(23).setCellValue(student.getEnrollmentMonth());
				row.createCell(24).setCellValue(student.getEnrollmentYear());
				row.createCell(25).setCellValue(student.getPrgmStructApplicable());
			}
			
			//Written to write excel file to outputStream and flush it out of controller itself//
			ByteArrayOutputStream outByteStream = new ByteArrayOutputStream();
			workbook.write(outByteStream);
			byte [] outArray = outByteStream.toByteArray();
			response.setContentType("application/ms-excel");
			response.setContentLength(outArray.length);
			response.setHeader("Expires:", "0"); // eliminates browser caching
			response.setHeader("Content-Disposition", "attachment; filename=PassFailRecords.xls");
			ServletOutputStream outStream = response.getOutputStream();
			outStream.write(outArray);
			outStream.flush();
			//End//
		}*/
	public void generateReRegistrationReport() throws Exception{
		XSSFWorkbook workbook =new XSSFWorkbook();
		XSSFSheet sheet = workbook.createSheet("Master Detail");
		int headerIndex = 0,firstRowIndex = 0;
		XSSFRow header = sheet.createRow(0);
		XSSFRow firstRow = sheet.createRow(1);
		header.createCell(headerIndex++).setCellValue("SapId");
		header.createCell(headerIndex++).setCellValue("Failed Subjects");
		header.createCell(headerIndex++).setCellValue("Passed Subjects");
		header.createCell(headerIndex++).setCellValue("Assignment Not Submitted");
		header.createCell(headerIndex++).setCellValue("No Term End");
		header.createCell(headerIndex++).setCellValue("Absent for TEE");
		header.createCell(headerIndex++).setCellValue("No of Drives Since Re-Reg");
		header.createCell(headerIndex++).setCellValue("Absent for TEE");
		header.createCell(headerIndex++).setCellValue("No Of Copy Cases");
		header.createCell(headerIndex++).setCellValue("No Of Sessions Attended");
		
		for(int i = 0;i<40;i++){
			firstRow.createCell(firstRowIndex++).setCellValue("Sem 1");
			firstRow.createCell(firstRowIndex++).setCellValue("Sem 2");
			firstRow.createCell(firstRowIndex++).setCellValue("Sem 3");
			firstRow.createCell(firstRowIndex++).setCellValue("Sem 4");
		}
		sheet.addMergedRegion(new CellRangeAddress(
		            1, //first row (0-based)
		            1, //last row  (0-based)
		            1, //first column (0-based)
		            2  //last column  (0-based)
		    ));
		
		FileOutputStream fileOut = new FileOutputStream("D:/workbook.xls");
		workbook.write(fileOut);
	    fileOut.close();
	}
	/*
	 * 11/16/2017
	 * 
	 * public void buildExcelDocument(ArrayList<Object> studentAndCenterCountObject,HttpServletResponse response) throws Exception 
	{
		int run = 0;
		ArrayList<Object> data = studentAndCenterCountObject;

		ArrayList<StudentBean> allStudentsList =  (ArrayList<StudentBean>)data.get(0);
		TreeMap<String, Integer> sortedSubjectCityStudentCountMap = (TreeMap<String, Integer>)data.get(1);

		//create a worksheet
		XSSFWorkbook workbook =new XSSFWorkbook();
		XSSFSheet sheet = workbook.createSheet("Subject-City-Count");
		int index = 0;
		XSSFRow header = sheet.createRow(0);
		header.createCell(index++).setCellValue("Sr. No.");
		header.createCell(index++).setCellValue("Subject");
		header.createCell(index++).setCellValue("City");
		header.createCell(index++).setCellValue("Count");

		int rowNum = 1;
		for (Map.Entry<String, Integer> entry : sortedSubjectCityStudentCountMap.entrySet()) {
			index = 0;
			String key = entry.getKey();
			String subject = key.substring(0, key.indexOf("~"));
			String city = key.substring(key.indexOf("~")+1, key.length());
			int studentCount = entry.getValue().intValue();

			//create the row data
			XSSFRow row = sheet.createRow(rowNum++);

			row.createCell(index++).setCellValue(rowNum-1);
			row.createCell(index++).setCellValue(subject);
			row.createCell(index++).setCellValue(city);
			row.createCell(index++).setCellValue(new Double(studentCount));

		}


		//create a second sheet
		sheet = workbook.createSheet("Student Details");
		index = 0;
		header = sheet.createRow(0);
		header.createCell(index++).setCellValue("Sr. No.");
		header.createCell(index++).setCellValue("Subject");
		header.createCell(index++).setCellValue("Student ID");
		header.createCell(index++).setCellValue("City");

		rowNum = 1;
		for (int i = 0 ; i < allStudentsList.size(); i++) {
			index = 0;
			//create the row data
			XSSFRow row = sheet.createRow(rowNum++);
			StudentBean bean = allStudentsList.get(i);

			row.createCell(index++).setCellValue(rowNum-1);
			row.createCell(index++).setCellValue(bean.getSubject());
			row.createCell(index++).setCellValue(new Double(bean.getSapid()));
			row.createCell(index++).setCellValue(bean.getCity());
		}
		//Written to write excel file to outputStream and flush it out of controller itself//
		ByteArrayOutputStream outByteStream = new ByteArrayOutputStream();
		workbook.write(outByteStream);
		byte [] outArray = outByteStream.toByteArray();
		response.setContentType("application/ms-excel");
		response.setContentLength(outArray.length);
		response.setHeader("Expires:", "0"); // eliminates browser caching
		response.setHeader("Content-Disposition", "attachment; filename=ExceptedRegistration.xls");
		ServletOutputStream outStream = response.getOutputStream();
		outStream.write(outArray);
		outStream.flush();
		//End//
		
		
	}*/
	//Build Excel document for ANS//
	
	public void buildExcelDocumentForSearchAssignment(List<AssignmentFileBean> allSubmittedAssignmentList,HttpServletResponse response,AssignmentsDAO dao ) throws Exception 
	{
		HashMap<String, StudentExamBean> sapIdStudentsMap = dao.getAllStudents();
		HashMap<String, CenterExamBean> icLcMap = dao.getICLCMap();
		//create a worksheet
		XSSFWorkbook workbook =new XSSFWorkbook();
		XSSFSheet sheet = workbook.createSheet("Registrations Not Done");
		int index = 0;
		XSSFRow header = sheet.createRow(0);
		header.createCell(index++).setCellValue("Sr. No.");
		header.createCell(index++).setCellValue("Exam Month");
		header.createCell(index++).setCellValue("Exam Year");
		
		header.createCell(index++).setCellValue("Student ID");
		
		header.createCell(index++).setCellValue("First Name");
		header.createCell(index++).setCellValue("Last Name");
		header.createCell(index++).setCellValue("Sem");
		header.createCell(index++).setCellValue("Email ID");
		header.createCell(index++).setCellValue("Mobile");
		header.createCell(index++).setCellValue("Alt Phone");
		
		header.createCell(index++).setCellValue("Program");
		//header.createCell(index++).setCellValue("Sem");
		header.createCell(index++).setCellValue("Subject");
		header.createCell(index++).setCellValue("Center Code");
		header.createCell(index++).setCellValue("Center Name");
		
		header.createCell(index++).setCellValue("Program Structure");
		header.createCell(index++).setCellValue("Enrollment Month");
		header.createCell(index++).setCellValue("Enrollment Year");
		
		header.createCell(index++).setCellValue("Validity End Month");
		header.createCell(index++).setCellValue("Validity End Year");
		
		header.createCell(index++).setCellValue("IC");
		header.createCell(index++).setCellValue("LC");
		header.createCell(index++).setCellValue("Exam Mode");
		header.createCell(index++).setCellValue("End Date");
	    
	    
		
	    
 
		int rowNum = 1;
		for (int i = 0 ; i < allSubmittedAssignmentList.size(); i++) {
			index = 0;
			//create the row data
			XSSFRow row = sheet.createRow(rowNum++);
			AssignmentFileBean bean = allSubmittedAssignmentList.get(i);
			StudentExamBean student = sapIdStudentsMap.get(bean.getSapId());
			
			String ic = "";
			String lc = "";
			String examMode = "Offline";
			if(student != null){
				ic = student.getCenterName();
				CenterExamBean center = icLcMap.get(student.getCenterCode()); 
				if(center != null){
					lc = center.getLc();
				}
				
				if("Online".equalsIgnoreCase(student.getExamMode())){
					examMode = "Online";
				}
			}
			
			row.createCell(index++).setCellValue(rowNum-1);
			
			row.createCell(index++).setCellValue(bean.getMonth());
			row.createCell(index++).setCellValue(new Double(bean.getYear()));
			
			row.createCell(index++).setCellValue(new Double(bean.getSapId()));
			
			row.createCell(index++).setCellValue(bean.getFirstName());
			row.createCell(index++).setCellValue(bean.getLastName());
			row.createCell(index++).setCellValue(bean.getSem());
			row.createCell(index++).setCellValue(bean.getEmailId());
			row.createCell(index++).setCellValue(bean.getMobile());
			row.createCell(index++).setCellValue(bean.getAltPhone());
			row.createCell(index++).setCellValue(bean.getProgram());
			
			//row.createCell(index++).setCellValue(new Double(bean.getSem()));
			row.createCell(index++).setCellValue(bean.getSubject());
			
			row.createCell(index++).setCellValue(bean.getCenterCode());
			row.createCell(index++).setCellValue(bean.getCenterName());
			
			row.createCell(index++).setCellValue(bean.getPrgmStructApplicable());
			row.createCell(index++).setCellValue(bean.getEnrollmentMonth());
			row.createCell(index++).setCellValue(bean.getEnrollmentYear());
			
			row.createCell(index++).setCellValue(bean.getValidityEndMonth());
			row.createCell(index++).setCellValue(bean.getValidityEndYear());
			
			row.createCell(index++).setCellValue(ic);
			row.createCell(index++).setCellValue(lc);
			row.createCell(index++).setCellValue(examMode);
			row.createCell(index++).setCellValue(bean.getEndDate());
			
		}
		
		//Written to write excel file to outputStream and flush it out of controller itself//
			ByteArrayOutputStream outByteStream = new ByteArrayOutputStream();
			workbook.write(outByteStream);
			byte [] outArray = outByteStream.toByteArray();
			response.setContentType("application/ms-excel");
			response.setContentLength(outArray.length);
			response.setHeader("Expires:", "0"); // eliminates browser caching
			response.setHeader("Content-Disposition", "attachment; filename=SubmittedAssignmentReport.xls");
			ServletOutputStream outStream = response.getOutputStream();
			outStream.write(outArray);
			outStream.flush();
			//End//
	}
	
	
	
	
	//End//
	
	// download Exam Booking Pending Report
	/*11/16/2017
	 * 
	 * 
	 * public void buildExcelDocumentForExamBookingPending(ArrayList<AssignmentStatusBean> regIncompleteStudetsList,HttpServletResponse response,StudentMarksDAO dao ) throws Exception 
	{
		HashMap<String, StudentBean> sapIdStudentsMap = dao.getAllStudents();
		HashMap<String, CenterBean> icLcMap = dao.getICLCMap();
		//create a worksheet
		XSSFWorkbook workbook =new XSSFWorkbook();
		XSSFSheet sheet = workbook.createSheet("Registrations Not Done");
		int index = 0;
		XSSFRow header = sheet.createRow(0);
		header.createCell(index++).setCellValue("Sr. No.");
		header.createCell(index++).setCellValue("Exam Year");
	    header.createCell(index++).setCellValue("Exam Month");
		header.createCell(index++).setCellValue("SAP ID");
		header.createCell(index++).setCellValue("First Name");
		header.createCell(index++).setCellValue("Last Name");
		header.createCell(index++).setCellValue("Email");
		header.createCell(index++).setCellValue("Mobile");
		header.createCell(index++).setCellValue("Alternate Phone");
		header.createCell(index++).setCellValue("Program");
	    header.createCell(index++).setCellValue("Subject");
	    header.createCell(index++).setCellValue("Sem");
	    header.createCell(index++).setCellValue("IC");
		header.createCell(index++).setCellValue("LC");
		header.createCell(index++).setCellValue("Exam Mode");
		
	    header.createCell(index++).setCellValue("Center Code");
	    header.createCell(index++).setCellValue("Center Name");
	    header.createCell(index++).setCellValue("LC");
	    header.createCell(index++).setCellValue("Enrollment Month");
	    header.createCell(index++).setCellValue("Enrollment Year");
	    
	    
		
	    
 
		int rowNum = 1;
		for (int i = 0 ; i < regIncompleteStudetsList.size(); i++) {
			index = 0;
			//create the row data
			XSSFRow row = sheet.createRow(rowNum++);
			AssignmentStatusBean bean = regIncompleteStudetsList.get(i);
			
			StudentBean student = sapIdStudentsMap.get(bean.getSapid());
			
			String ic = "";
			String lc = "";
			String examMode = "Offline";
			if(student != null){
				ic = student.getCenterName();
				CenterBean center = icLcMap.get(student.getCenterCode()); 
				if(center != null){
					lc = center.getLc();
				}
				
				if("Online".equalsIgnoreCase(student.getExamMode())){
					examMode = "Online";
				}
			}
			
			row.createCell(index++).setCellValue((i+1));
			row.createCell(index++).setCellValue(bean.getExamYear());
			row.createCell(index++).setCellValue(bean.getExamMonth());
			row.createCell(index++).setCellValue(new Double(bean.getSapid()));
		
			row.createCell(index++).setCellValue(bean.getFirstName());
			row.createCell(index++).setCellValue(bean.getLastName());
			row.createCell(index++).setCellValue(bean.getEmailId());
			row.createCell(index++).setCellValue(bean.getMobile());
			row.createCell(index++).setCellValue(bean.getAltPhone());
			
			
			row.createCell(index++).setCellValue(bean.getProgram());
			row.createCell(index++).setCellValue(bean.getSubject());
			row.createCell(index++).setCellValue(bean.getSem());
			row.createCell(index++).setCellValue(ic);
			row.createCell(index++).setCellValue(lc);
			row.createCell(index++).setCellValue(examMode);
			
			row.createCell(index++).setCellValue(bean.getCenterCode());
			row.createCell(index++).setCellValue(bean.getCenterName());
			row.createCell(index++).setCellValue(bean.getLc());
			row.createCell(index++).setCellValue(bean.getEnrollmentMonth());
			row.createCell(index++).setCellValue(new Double(bean.getEnrollmentYear()));
		}
		
		//Written to write excel file to outputStream and flush it out of controller itself//
			ByteArrayOutputStream outByteStream = new ByteArrayOutputStream();
			workbook.write(outByteStream);
			byte [] outArray = outByteStream.toByteArray();
			response.setContentType("application/ms-excel");
			response.setContentLength(outArray.length);
			response.setHeader("Expires:", "0"); // eliminates browser caching
			response.setHeader("Content-Disposition", "attachment; filename=ExamBookingPending.xls");
			ServletOutputStream outStream = response.getOutputStream();
			outStream.write(outArray);
			outStream.flush();
			//End//
	}*/
	public static void main(String [] args){
		ExcelHelper ex = new ExcelHelper();
		
		try {
			//ex.buildExcelDocumentForRegistrationReportHome();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			
		}
	}

	public void buildExcelDocumentForOperationRevenue(ArrayList<OperationsRevenueBean> revenueList,HttpServletResponse response) throws IOException {
		
		XSSFWorkbook workbook =new XSSFWorkbook();
		XSSFSheet sheet = workbook.createSheet("Revenue Deatils");
		
		int index = 0;
		XSSFRow header = sheet.createRow(0);
		header.createCell(index++).setCellValue("Sr. No.");
		header.createCell(index++).setCellValue("Student Name");
		header.createCell(index++).setCellValue("SAP ID");
		header.createCell(index++).setCellValue("Amount");
		header.createCell(index++).setCellValue("Date Of Transaction");
		header.createCell(index++).setCellValue("Revenue Type");
		header.createCell(index++).setCellValue("LC");
		int rowNum = 1;
		for (int i = 0 ; i < revenueList.size(); i++) {
			//create the row data
			index = 0;
			XSSFRow row = sheet.createRow(rowNum++);
			OperationsRevenueBean bean = revenueList.get(i);
			row.createCell(index++).setCellValue(rowNum-1);
			row.createCell(index++).setCellValue(bean.getFullName());
			row.createCell(index++).setCellValue(new Double(bean.getSapid()));
			row.createCell(index++).setCellValue(bean.getAmount());
			row.createCell(index++).setCellValue(bean.getTranDateTime());
			row.createCell(index++).setCellValue(bean.getRevenueSource());
			row.createCell(index++).setCellValue(bean.getLc());
        }
		
		//Written to write excel file to outputStream and flush it out of controller itself//
		ByteArrayOutputStream outByteStream = new ByteArrayOutputStream();
		workbook.write(outByteStream);
		byte [] outArray = outByteStream.toByteArray();
		response.setContentType("application/ms-excel");
		response.setContentLength(outArray.length);
		response.setHeader("Expires:", "0"); // eliminates browser caching
		response.setHeader("Content-Disposition", "attachment; filename=RevenueDeatils.xls");
		ServletOutputStream outStream = response.getOutputStream();
		outStream.write(outArray);
		outStream.flush();
		//End//
		
	}
	
public void buildExcelDocumentForANSRecords(List<AssignmentFileBean> assignmentFilesList,HttpServletRequest request,HttpServletResponse response,
		HashMap<String, StudentExamBean> sapIdStudentsMap,HashMap<String, CenterExamBean> icLcMap,String ASSIGNMENT_REPORTS_GENERATE_PATH) throws IOException {
		
	    SXSSFWorkbook workbook = new SXSSFWorkbook();
		SXSSFSheet sheet = (SXSSFSheet)workbook.createSheet("ANS List");
		
		int index = 0;
		Row header = sheet.createRow(0);
		header.createCell(index++).setCellValue("Sr. No.");
		header.createCell(index++).setCellValue("Student ID");
		
		header.createCell(index++).setCellValue("First Name");
		header.createCell(index++).setCellValue("Last Name");
		header.createCell(index++).setCellValue("Email ID");
		header.createCell(index++).setCellValue("Mobile");
		header.createCell(index++).setCellValue("Alt Phone");
		
		header.createCell(index++).setCellValue("Program");
		header.createCell(index++).setCellValue("Sem");
		header.createCell(index++).setCellValue("Subject");
		header.createCell(index++).setCellValue("Center Code");
		header.createCell(index++).setCellValue("Center Name");
		header.createCell(index++).setCellValue("LC");
		
		header.createCell(index++).setCellValue("Program Structure");
		header.createCell(index++).setCellValue("Enrollment Month");
		header.createCell(index++).setCellValue("Enrollment Year");
		
		header.createCell(index++).setCellValue("Validity End Month");
		header.createCell(index++).setCellValue("Validity End Year");
		
		header.createCell(index++).setCellValue("IC");
		header.createCell(index++).setCellValue("LC");
		header.createCell(index++).setCellValue("Exam Mode");
		
		int rowNum = 1;
		for (int i = 0 ; i < assignmentFilesList.size(); i++) {
			//create the row data
			index = 0;
			Row row = sheet.createRow(rowNum++);
			AssignmentFileBean bean = assignmentFilesList.get(i);
			
			StudentExamBean student = sapIdStudentsMap.get(bean.getSapId());
			
			String ic = "";
			String lc = "";
			String examMode = "Offline";
			if(student != null){
				ic = student.getCenterName();
				CenterExamBean center = icLcMap.get(student.getCenterCode()); 
				if(center != null){
					lc = center.getLc();
				}
				
				if("Online".equalsIgnoreCase(student.getExamMode())){
					examMode = "Online";
				}
			}
			
			row.createCell(index++).setCellValue(rowNum-1);
			row.createCell(index++).setCellValue(new Double(bean.getSapId()));
			
			row.createCell(index++).setCellValue(bean.getFirstName());
			row.createCell(index++).setCellValue(bean.getLastName());
			row.createCell(index++).setCellValue(bean.getEmailId());
			row.createCell(index++).setCellValue(bean.getMobile());
			row.createCell(index++).setCellValue(bean.getAltPhone());
			row.createCell(index++).setCellValue(bean.getProgram());
			
			row.createCell(index++).setCellValue(new Double(bean.getSem()));
			row.createCell(index++).setCellValue(bean.getSubject());
			
			row.createCell(index++).setCellValue(bean.getCenterCode());
			row.createCell(index++).setCellValue(bean.getCenterName());
			row.createCell(index++).setCellValue(lc);
			
			row.createCell(index++).setCellValue(bean.getPrgmStructApplicable());
			row.createCell(index++).setCellValue(bean.getEnrollmentMonth());
			row.createCell(index++).setCellValue(new Double(bean.getEnrollmentYear()));
			
			row.createCell(index++).setCellValue(bean.getValidityEndMonth());
			row.createCell(index++).setCellValue(new Double(bean.getValidityEndYear()));
			
			row.createCell(index++).setCellValue(ic);
			row.createCell(index++).setCellValue(lc);
			row.createCell(index++).setCellValue(examMode);
			
        }
		
		try {
			//writing data in a file and saving
			String fileName = "ANS_Report.xlsx";
				String filePath = ASSIGNMENT_REPORTS_GENERATE_PATH + "ANS"+ "/" +fileName;
				FileOutputStream fileOut = new FileOutputStream(filePath);
				workbook.write(fileOut);
			    fileOut.close();
				//started file download
				File downloadFile = new File(filePath);
				FileInputStream inputStream = new FileInputStream(downloadFile);
				ServletContext context = request.getSession().getServletContext();
				
				// get MIME type of the file
				String mimeType = context.getMimeType(filePath);
				if (mimeType == null) {
					// set to binary type if MIME mapping not found
					mimeType = "application/octet-stream";
				}

				// set content attributes for the response
				response.setContentType(mimeType);
				response.setContentLength((int) downloadFile.length());

				// set headers for the response
				String headerKey = "Content-Disposition";
				String headerValue = String.format("attachment; filename=\"%s\"",
						downloadFile.getName());
				response.setHeader(headerKey, headerValue);

				// get output stream of the response
				OutputStream outStream = response.getOutputStream();
				int BUFFER_SIZE = 4096;
				byte[] buffer = new byte[BUFFER_SIZE];
				int bytesRead = -1;

				// write bytes read from the input stream into the output stream
				while ((bytesRead = inputStream.read(buffer)) != -1) {
					outStream.write(buffer, 0, bytesRead);
				}
				inputStream.close();
				outStream.close();
		} catch (Exception e) {
			
		} 
	}



	//added because of excel upload for non-corporate centers- START
	public ArrayList<List> readNonCorporateCentersExcel(FileBean fileBean,String userId){
	
	int examMode=5;
	int nonCorporateCenter=0;
	int capacity=3;
	int locality=6;
	int address=4;
	int city=1;
	int state=2;
	int googleMapURL=7;
	
	ByteArrayInputStream bis = new ByteArrayInputStream(fileBean.getFileData().getBytes());
	Workbook workbook;
	
	List<ExamCenterBean> nonCorporateExamCenterList = new ArrayList<ExamCenterBean>();
	List<ExamCenterBean> errorBeanList = new ArrayList<ExamCenterBean>();
	
	ArrayList<List> resultList = new ArrayList<>();
	try{
		if (fileBean.getFileData().getOriginalFilename().endsWith("xls")) {
			workbook = new HSSFWorkbook(bis);
		} else if (fileBean.getFileData().getOriginalFilename().endsWith("xlsx")) {
			workbook = new XSSFWorkbook(bis);
		} else {
			throw new IllegalArgumentException("Received file does not have a standard excel extension.");
		}
		XSSFSheet sheet = (XSSFSheet) workbook.getSheetAt(0);
		Iterator<Row> rowIterator = sheet.iterator();
		int i = 0;
		//Skip first row since it contains column names, not data.
		if(rowIterator.hasNext()){
			Row row = rowIterator.next();
		}
		while(rowIterator.hasNext()){
			i++;
			Row row = rowIterator.next();
			ExamCenterBean examCenterBean = new ExamCenterBean();
			
			if(row!=null){
				row.getCell(nonCorporateCenter, Row.CREATE_NULL_AS_BLANK).setCellType(Cell.CELL_TYPE_STRING);
				row.getCell(city, Row.CREATE_NULL_AS_BLANK).setCellType(Cell.CELL_TYPE_STRING);
				row.getCell(state, Row.CREATE_NULL_AS_BLANK).setCellType(Cell.CELL_TYPE_STRING);
				row.getCell(capacity, Row.CREATE_NULL_AS_BLANK).setCellType(Cell.CELL_TYPE_STRING);
				row.getCell(address, Row.CREATE_NULL_AS_BLANK).setCellType(Cell.CELL_TYPE_STRING);
				row.getCell(examMode, Row.CREATE_NULL_AS_BLANK).setCellType(Cell.CELL_TYPE_STRING);
				row.getCell(locality, Row.CREATE_NULL_AS_BLANK).setCellType(Cell.CELL_TYPE_STRING);
				row.getCell(googleMapURL, Row.CREATE_NULL_AS_BLANK).setCellType(Cell.CELL_TYPE_STRING);
				
	
				String nonCorporate = row.getCell(nonCorporateCenter, Row.CREATE_NULL_AS_BLANK).getStringCellValue();
				String cities = row.getCell(city, Row.CREATE_NULL_AS_BLANK).getStringCellValue();
				String states = row.getCell(state, Row.CREATE_NULL_AS_BLANK).getStringCellValue();
				String capacities = row.getCell(capacity, Row.CREATE_NULL_AS_BLANK).getStringCellValue();
				String addr = row.getCell(address, Row.CREATE_NULL_AS_BLANK).getStringCellValue();
				String str = row.getCell(examMode, Row.CREATE_NULL_AS_BLANK).getStringCellValue().toLowerCase();
				String mode = StringUtils.capitalize(str);
				String localities= row.getCell(locality, Row.CREATE_NULL_AS_BLANK).getStringCellValue();
				String url = row.getCell(googleMapURL, Row.CREATE_NULL_AS_BLANK).getStringCellValue();
				
				
				if("".equals(examMode) ||  "".equals(nonCorporateCenter) ){
					errorBeanList.add(examCenterBean);
				}
				examCenterBean.setYear(fileBean.getYear());
				examCenterBean.setMonth(fileBean.getMonth());
				examCenterBean.setMode(mode);
				examCenterBean.setExamCenterName(nonCorporate);
				examCenterBean.setCapacity(capacities);
				examCenterBean.setLocality(localities);
				examCenterBean.setAddress(addr);
				examCenterBean.setCity(cities);
				examCenterBean.setState(states);
				examCenterBean.setGoogleMapUrl(url);
				examCenterBean.setCreatedBy(userId);
				examCenterBean.setLastModifiedBy(userId);
				examCenterBean.setIc("All");

				if(i % 1000 == 0){
				}
			}
			nonCorporateExamCenterList.add(examCenterBean);
		}
		
		
	}catch(Exception e){
		
	}
	resultList.add(nonCorporateExamCenterList);
	resultList.add(errorBeanList);
	return resultList;
}

	//END
	
	//added for batch upload of executive exam center: START
	public ArrayList<List> readExecutiveExamCenters(FileBean fileBean,String userId){
		
		int executiveExamCenter=0;
		int capacity=3;
		int locality=5;
		int address=4;
		int city=1;
		int state=2;
		int googleMapURL=6;
		
		ByteArrayInputStream bis = new ByteArrayInputStream(fileBean.getFileData().getBytes());
		Workbook workbook;
		
		List<ExecutiveExamCenter> executiveExamCenterList = new ArrayList<ExecutiveExamCenter>();
		List<ExecutiveExamCenter> errorBeanList = new ArrayList<ExecutiveExamCenter>();
		
		ArrayList<List> resultList = new ArrayList<>();
		try{
			if (fileBean.getFileData().getOriginalFilename().endsWith("xls")) {
				workbook = new HSSFWorkbook(bis);
			} else if (fileBean.getFileData().getOriginalFilename().endsWith("xlsx")) {
				workbook = new XSSFWorkbook(bis);
			} else {
				throw new IllegalArgumentException("Received file does not have a standard excel extension.");
			}
			XSSFSheet sheet = (XSSFSheet) workbook.getSheetAt(0);
			Iterator<Row> rowIterator = sheet.iterator();
			int i = 0;
			//Skip first row since it contains column names, not data.
			if(rowIterator.hasNext()){
				Row row = rowIterator.next();
			}
			while(rowIterator.hasNext()){
				i++;
				Row row = rowIterator.next();
				ExecutiveExamCenter executiveExamBean = new ExecutiveExamCenter();
				
				if(row!=null){
					row.getCell(executiveExamCenter, Row.CREATE_NULL_AS_BLANK).setCellType(Cell.CELL_TYPE_STRING);
					row.getCell(city, Row.CREATE_NULL_AS_BLANK).setCellType(Cell.CELL_TYPE_STRING);
					row.getCell(state, Row.CREATE_NULL_AS_BLANK).setCellType(Cell.CELL_TYPE_STRING);
					row.getCell(capacity, Row.CREATE_NULL_AS_BLANK).setCellType(Cell.CELL_TYPE_STRING);
					row.getCell(address, Row.CREATE_NULL_AS_BLANK).setCellType(Cell.CELL_TYPE_STRING);
					row.getCell(locality, Row.CREATE_NULL_AS_BLANK).setCellType(Cell.CELL_TYPE_STRING);
					row.getCell(googleMapURL, Row.CREATE_NULL_AS_BLANK).setCellType(Cell.CELL_TYPE_STRING);
					
		
					String executiveCenter = row.getCell(executiveExamCenter, Row.CREATE_NULL_AS_BLANK).getStringCellValue();
					String cities = row.getCell(city, Row.CREATE_NULL_AS_BLANK).getStringCellValue();
					String states = row.getCell(state, Row.CREATE_NULL_AS_BLANK).getStringCellValue();
					String capacities = row.getCell(capacity, Row.CREATE_NULL_AS_BLANK).getStringCellValue();
					String addr = row.getCell(address, Row.CREATE_NULL_AS_BLANK).getStringCellValue();
					String localities= row.getCell(locality, Row.CREATE_NULL_AS_BLANK).getStringCellValue();
					String url = row.getCell(googleMapURL, Row.CREATE_NULL_AS_BLANK).getStringCellValue();
					
					
					if("".equals(executiveCenter) ){
						errorBeanList.add(executiveExamBean);
					}
					executiveExamBean.setYear(fileBean.getYear());
					executiveExamBean.setMonth(fileBean.getMonth());
					executiveExamBean.setExamCenterName(executiveCenter);
					executiveExamBean.setCapacity(capacities);
					executiveExamBean.setLocality(localities);
					executiveExamBean.setAddress(addr);
					executiveExamBean.setCity(cities);
					executiveExamBean.setState(states);
					executiveExamBean.setGoogleMapUrl(url);
					executiveExamBean.setCreatedBy(userId);
					executiveExamBean.setLastModifiedBy(userId);
					executiveExamBean.setBatchYear(fileBean.getEnrollmentYear()+"");
					executiveExamBean.setBatchMonth(fileBean.getEnrollmentMonth());
					
					if(i % 1000 == 0){
					}
				}
				executiveExamCenterList.add(executiveExamBean);
			}
			
			
		}catch(Exception e){
			
		}
		resultList.add(executiveExamCenterList);
		resultList.add(errorBeanList);
		return resultList;
	}
	//END
	
	//added for batch upload of test chapterwise questions: START
	public ArrayList<List> readTestQuestionsExcel(FileBean fileBean,String userId, HashMap<String,TestTypeBean> testTypesMap){
		
		int CHAPTER_INDEX=0;
		int MARKS_INDEX=1;
		int TYPE_INDEX=2;
		int QUESTION_INDEX=3;
		int DESCRIPTION_INDEX=4;
		int OPTION1_INDEX=5;
		int OPTION2_INDEX=6;
		int OPTION3_INDEX=7;
		int OPTION4_INDEX=8;
		int OPTION5_INDEX=9;
		int OPTION6_INDEX=10;
		int OPTION7_INDEX=11;
		int OPTION8_INDEX=12;
		int CORRECTOPTION_INDEX=13;
		
		ByteArrayInputStream bis = new ByteArrayInputStream(fileBean.getFileData().getBytes());
		Workbook workbook;
		
		List<TestQuestionExamBean> testQuestionList = new ArrayList<TestQuestionExamBean>();
		List<TestQuestionExamBean> errorBeanList = new ArrayList<TestQuestionExamBean>();
		
		ArrayList<List> resultList = new ArrayList<>();
		try{
			if (fileBean.getFileData().getOriginalFilename().endsWith("xls")) {
				workbook = new HSSFWorkbook(bis);
			} else if (fileBean.getFileData().getOriginalFilename().endsWith("xlsx")) {
				workbook = new XSSFWorkbook(bis);
			} else {
				throw new IllegalArgumentException("Received file does not have a standard excel extension.");
			}
			Long testId = new Long(fileBean.getFileId());
			
			XSSFSheet sheet = (XSSFSheet) workbook.getSheetAt(0);
			Iterator<Row> rowIterator = sheet.iterator();
			int i = 0;
			//Skip first row since it contains column names, not data.
			if(rowIterator.hasNext()){
				Row row = rowIterator.next();
			}
			while(rowIterator.hasNext()){
				i++;
				Row row = rowIterator.next();
				TestQuestionExamBean bean = new TestQuestionExamBean();
				
				try {
					if(row!=null){
						row.getCell(CHAPTER_INDEX, Row.CREATE_NULL_AS_BLANK).setCellType(Cell.CELL_TYPE_STRING);
						row.getCell(DESCRIPTION_INDEX, Row.CREATE_NULL_AS_BLANK).setCellType(Cell.CELL_TYPE_STRING);
						row.getCell(MARKS_INDEX, Row.CREATE_NULL_AS_BLANK).setCellType(Cell.CELL_TYPE_STRING);
						row.getCell(TYPE_INDEX, Row.CREATE_NULL_AS_BLANK).setCellType(Cell.CELL_TYPE_STRING);
						row.getCell(OPTION1_INDEX, Row.CREATE_NULL_AS_BLANK).setCellType(Cell.CELL_TYPE_STRING);
						row.getCell(OPTION2_INDEX, Row.CREATE_NULL_AS_BLANK).setCellType(Cell.CELL_TYPE_STRING);
						row.getCell(OPTION3_INDEX, Row.CREATE_NULL_AS_BLANK).setCellType(Cell.CELL_TYPE_STRING);
						row.getCell(OPTION4_INDEX, Row.CREATE_NULL_AS_BLANK).setCellType(Cell.CELL_TYPE_STRING);
						row.getCell(OPTION5_INDEX, Row.CREATE_NULL_AS_BLANK).setCellType(Cell.CELL_TYPE_STRING);
						row.getCell(OPTION6_INDEX, Row.CREATE_NULL_AS_BLANK).setCellType(Cell.CELL_TYPE_STRING);
						row.getCell(OPTION7_INDEX, Row.CREATE_NULL_AS_BLANK).setCellType(Cell.CELL_TYPE_STRING);
						row.getCell(OPTION8_INDEX, Row.CREATE_NULL_AS_BLANK).setCellType(Cell.CELL_TYPE_STRING);
						row.getCell(CORRECTOPTION_INDEX, Row.CREATE_NULL_AS_BLANK).setCellType(Cell.CELL_TYPE_STRING);
						row.getCell(QUESTION_INDEX, Row.CREATE_NULL_AS_BLANK).setCellType(Cell.CELL_TYPE_STRING);
						
						//Chapter	description	marks	type	option1	option2	option3	option4	option5	option6	option7	option8	correctOption


						String chapter = row.getCell(CHAPTER_INDEX, Row.CREATE_NULL_AS_BLANK).getStringCellValue();
						String description = row.getCell(DESCRIPTION_INDEX, Row.CREATE_NULL_AS_BLANK).getStringCellValue();
						String marks = row.getCell(MARKS_INDEX, Row.CREATE_NULL_AS_BLANK).getStringCellValue();
						String type = row.getCell(TYPE_INDEX, Row.CREATE_NULL_AS_BLANK).getStringCellValue();
						String option1 = row.getCell(OPTION1_INDEX, Row.CREATE_NULL_AS_BLANK).getStringCellValue();
						String option2= row.getCell(OPTION2_INDEX, Row.CREATE_NULL_AS_BLANK).getStringCellValue();
						String option3 = row.getCell(OPTION3_INDEX, Row.CREATE_NULL_AS_BLANK).getStringCellValue();
						String option4 = row.getCell(OPTION4_INDEX, Row.CREATE_NULL_AS_BLANK).getStringCellValue();
						String option5= row.getCell(OPTION5_INDEX, Row.CREATE_NULL_AS_BLANK).getStringCellValue();
						String option6 = row.getCell(OPTION6_INDEX, Row.CREATE_NULL_AS_BLANK).getStringCellValue();
						String option7 = row.getCell(OPTION7_INDEX, Row.CREATE_NULL_AS_BLANK).getStringCellValue();
						String option8= row.getCell(OPTION8_INDEX, Row.CREATE_NULL_AS_BLANK).getStringCellValue();
						String correctOption = row.getCell(CORRECTOPTION_INDEX, Row.CREATE_NULL_AS_BLANK).getStringCellValue();
						String question = row.getCell(QUESTION_INDEX, Row.CREATE_NULL_AS_BLANK).getStringCellValue();
						
						if(StringUtils.isBlank(chapter) && StringUtils.isBlank(description) && StringUtils.isBlank(marks) && StringUtils.isBlank(type)) {
							break;
						}
						
							type= type.toUpperCase().trim();
							if(!StringUtils.isBlank(type)) {
								if(!testTypesMap.containsKey(type)) {
									throw new Exception("Entered question type: "+type+" does not match with type in system. ");
								}
							}else {
								throw new Exception("Question type cannot be blank. ");
								
							}
						
						bean.setTestId(testId);
						bean.setChapter(chapter);
						bean.setDescription(description);
						try {
							bean.setMarks(Double.parseDouble(marks));
						} catch (Exception e) {
							// TODO Auto-generated catch block
							
							throw new Exception("Marks cannot be blank or Not in correct format. ");
						}
						bean.setType(testTypesMap.get(type).getId().intValue());
						bean.setOption1(option1);
						bean.setOption3(option3);
						bean.setOption2(option2);
						bean.setOption5(option5);
						bean.setOption4(option4);
						bean.setOption6(option6);
						bean.setOption7(option7);
						bean.setOption8(option8);
						
						if(!StringUtils.isBlank(correctOption)) {

							bean.setCorrectOption((correctOption.trim()));
						}else {
							throw new Exception("Correct Option Value cannot be blank.");
						}
						
						bean.setQuestion(question);
						bean.setCreatedBy(userId);
						bean.setLastModifiedBy(userId);
						bean.setActive("Y");
						bean.setSectionId(fileBean.getSectionId());;
						
						bean = placeOptionsInOptionsList(bean);
						
					}
					testQuestionList.add(bean);
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
					bean.setErrorRecord(true);
					bean.setErrorMessage("Error in row "+(i+1)+", Error :"+e.getMessage());
					errorBeanList.add(bean);
				}
				
			}
			
			
		}catch(Exception e){
			e.printStackTrace();
		}
		resultList.add(testQuestionList);
		resultList.add(errorBeanList);
		return resultList;
	}
	//END
	
	private TestQuestionExamBean placeOptionsInOptionsList(TestQuestionExamBean bean) {
		List<TestQuestionOptionExamBean> options = new LinkedList<>();
		String[] correctOptions=null;
		TestQuestionOptionExamBean tempOptionBean=null;
		if(!StringUtils.isBlank(bean.getCorrectOption())) {
			correctOptions= bean.getCorrectOption().split(",",-1);
			for(int i =0;i<correctOptions.length;i++) {
			}
		}
		
		if(bean.getType() == 4) {
			return placeOptionsInOptionsListForTRUEFALSE(bean,correctOptions);
		}
		
		
		if(!StringUtils.isBlank(bean.getOption1())) {
			tempOptionBean = new TestQuestionOptionExamBean();
			tempOptionBean.setOptionData(bean.getOption1());
			if(ArrayUtils.contains(correctOptions, "1")) {
				tempOptionBean.setIsCorrect("Y");
			}else {
				tempOptionBean.setIsCorrect("N");
			}
			options.add(tempOptionBean);
		}
		
		if(!StringUtils.isBlank(bean.getOption2())) {
			tempOptionBean = new TestQuestionOptionExamBean();
			tempOptionBean.setOptionData(bean.getOption2());
			if(ArrayUtils.contains(correctOptions, "2")) {
				tempOptionBean.setIsCorrect("Y");
			}else {
				tempOptionBean.setIsCorrect("N");
			}
			options.add(tempOptionBean);
		}
		
		if(!StringUtils.isBlank(bean.getOption3())) {
			tempOptionBean = new TestQuestionOptionExamBean();
			tempOptionBean.setOptionData(bean.getOption3());
			if(ArrayUtils.contains(correctOptions,  "3")) {
				tempOptionBean.setIsCorrect("Y");
			}else {
				tempOptionBean.setIsCorrect("N");
			}
			options.add(tempOptionBean);
		}
		
		if(!StringUtils.isBlank(bean.getOption4())) {
			tempOptionBean = new TestQuestionOptionExamBean();
			tempOptionBean.setOptionData(bean.getOption4());
			if(ArrayUtils.contains(correctOptions,  "4")) {
				tempOptionBean.setIsCorrect("Y");
			}else {
				tempOptionBean.setIsCorrect("N");
			}
			options.add(tempOptionBean);
		}
		
		if(!StringUtils.isBlank(bean.getOption5())) {
			tempOptionBean = new TestQuestionOptionExamBean();
			tempOptionBean.setOptionData(bean.getOption5());
			if(ArrayUtils.contains(correctOptions,  "5")) {
				tempOptionBean.setIsCorrect("Y");
			}else {
				tempOptionBean.setIsCorrect("N");
			}
			options.add(tempOptionBean);
		}
		
		if(!StringUtils.isBlank(bean.getOption6())) {
			tempOptionBean = new TestQuestionOptionExamBean();
			tempOptionBean.setOptionData(bean.getOption6());
			if(ArrayUtils.contains(correctOptions,  "6")) {
				tempOptionBean.setIsCorrect("Y");
			}else {
				tempOptionBean.setIsCorrect("N");
			}
			options.add(tempOptionBean);
		}
		
		if(!StringUtils.isBlank(bean.getOption7())) {
			tempOptionBean = new TestQuestionOptionExamBean();
			tempOptionBean.setOptionData(bean.getOption7());
			if(ArrayUtils.contains(correctOptions,  "7")) {
				tempOptionBean.setIsCorrect("Y");
			}else {
				tempOptionBean.setIsCorrect("N");
			}
			options.add(tempOptionBean);
		}
		
		if(!StringUtils.isBlank(bean.getOption8())) {
			tempOptionBean = new TestQuestionOptionExamBean();
			tempOptionBean.setOptionData(bean.getOption8());
			if(ArrayUtils.contains(correctOptions,  "8")) {
				tempOptionBean.setIsCorrect("Y");
			}else {
				tempOptionBean.setIsCorrect("N");
			}
			options.add(tempOptionBean);
		}
		
		bean.setOptionsList(options);
		return bean;
	}
	
	private TestQuestionExamBean placeOptionsInOptionsListForTRUEFALSE(TestQuestionExamBean bean, String[] correctOptions) {
			
			if(correctOptions.length > 1 ) {
				return bean;
			}
			List<TestQuestionOptionExamBean> options = new LinkedList<>();
			TestQuestionOptionExamBean trueAns = new TestQuestionOptionExamBean();
			
			trueAns.setOptionData("True");
			
			if(ArrayUtils.contains(correctOptions, "1")) {
				trueAns.setIsCorrect("Y");
			}else {
				trueAns.setIsCorrect("N");
			}
			options.add(trueAns);
			
			TestQuestionOptionExamBean falseAns = new TestQuestionOptionExamBean();
			falseAns.setOptionData("False");
			if(ArrayUtils.contains(correctOptions, "2")) {
				falseAns.setIsCorrect("Y");
			}else {
				falseAns.setIsCorrect("N");
			}
			options.add(falseAns);

		bean.setOptionsList(options);
		return bean;
	}

	//added for batch upload of test weightage: START
	public ArrayList<List> readTestWeightageExcel(FileBean fileBean,String userId){
		
		int CHAPTER_INDEX=0;
		int MARKS_INDEX=1;
		int NOOFQUESTIONSTOMARKS_INDEX=2;
		
		ByteArrayInputStream bis = new ByteArrayInputStream(fileBean.getFileData().getBytes());
		Workbook workbook;
		
		List<TestWeightageBean> testQuestionList = new ArrayList<TestWeightageBean>();
		List<TestWeightageBean> errorBeanList = new ArrayList<TestWeightageBean>();
		
		ArrayList<List> resultList = new ArrayList<>();
		try{
			if (fileBean.getFileData().getOriginalFilename().endsWith("xls")) {
				workbook = new HSSFWorkbook(bis);
			} else if (fileBean.getFileData().getOriginalFilename().endsWith("xlsx")) {
				workbook = new XSSFWorkbook(bis);
			} else {
				throw new IllegalArgumentException("Received file does not have a standard excel extension.");
			}
			Long testId = new Long(fileBean.getFileId());
			
			XSSFSheet sheet = (XSSFSheet) workbook.getSheetAt(0);
			Iterator<Row> rowIterator = sheet.iterator();
			int i = 0;
			//Skip first row since it contains column names, not data.
			if(rowIterator.hasNext()){
				Row row = rowIterator.next();
			}
			while(rowIterator.hasNext()){
				i++;
				Row row = rowIterator.next();
				TestWeightageBean bean = new TestWeightageBean();
				
				try {
					if(row!=null){
						row.getCell(CHAPTER_INDEX, Row.CREATE_NULL_AS_BLANK).setCellType(Cell.CELL_TYPE_STRING);
						row.getCell(NOOFQUESTIONSTOMARKS_INDEX, Row.CREATE_NULL_AS_BLANK).setCellType(Cell.CELL_TYPE_STRING);
						row.getCell(MARKS_INDEX, Row.CREATE_NULL_AS_BLANK).setCellType(Cell.CELL_TYPE_STRING);
						

						String chapter = row.getCell(CHAPTER_INDEX, Row.CREATE_NULL_AS_BLANK).getStringCellValue();
						String noOfQuestionToMarks = row.getCell(NOOFQUESTIONSTOMARKS_INDEX, Row.CREATE_NULL_AS_BLANK).getStringCellValue();
						String marks = row.getCell(MARKS_INDEX, Row.CREATE_NULL_AS_BLANK).getStringCellValue();
						
						if(StringUtils.isBlank(chapter) && StringUtils.isBlank(noOfQuestionToMarks) && StringUtils.isBlank(marks) ) {
							break;
						}
						
						bean.setTestId(testId);
						bean.setChapter(chapter);
						bean.setNoOfQuestionToMarks(noOfQuestionToMarks);
						bean.setMaxMarks(marks);
						bean.setCreatedBy(userId);
						bean.setLastModifiedBy(userId);
						
						if(i % 1000 == 0){
						}
					}
					testQuestionList.add(bean);
				} catch (Exception e) {
					// TODO Auto-generated catch block
					
					bean.setErrorRecord(true);
					bean.setErrorMessage("Error in row "+i+1+", Error :"+e.getMessage());
					errorBeanList.add(bean);
				}
				
			}
			
			
		}catch(Exception e){
			
		}
		resultList.add(testQuestionList);
		resultList.add(errorBeanList);
		return resultList;
	}
	//END
	
	public ArrayList<List> readSapIdFromExcel(StudentSubjectConfigExamBean fileBean, ArrayList<String> studentList, String userId,String subject,ServiceRequestDAO serviceRequestDao){
		
		int SAPID_INDEX = 0;
		int STUDENT_TYPE_INDEX = 1;
		ByteArrayInputStream bis = new ByteArrayInputStream(fileBean.getFileData().getBytes());
		Workbook workbook;
		
		List<TimeBoundUserMapping> studentSapIdList = new ArrayList<TimeBoundUserMapping>();
		List<TimeBoundUserMapping> errorBeanList = new ArrayList<TimeBoundUserMapping>();
		ArrayList<List> resultList = new ArrayList<>();
		Iterator<Row> rowIterator = null;
		try {
			if (fileBean.getFileData().getOriginalFilename().endsWith(".xls")) {
				workbook = new HSSFWorkbook(bis);
				HSSFSheet sheet = (HSSFSheet)workbook.getSheetAt(0);
				rowIterator = sheet.iterator();
			} else if (fileBean.getFileData().getOriginalFilename().endsWith(".xlsx")) {
				workbook = new XSSFWorkbook(bis);
				XSSFSheet sheet = (XSSFSheet) workbook.getSheetAt(0);
				rowIterator = sheet.iterator();
			} else {
				throw new IllegalArgumentException("Received file does not have a standard excel extension.");
			}
			
			int i = 0;
			//Skip first row since it contains column names, not data.
			if(rowIterator.hasNext()){
				Row row = rowIterator.next();
			}
			while(rowIterator.hasNext()) {
				i++;
				Row row = rowIterator.next();
				
				if(row!=null){
					row.getCell(SAPID_INDEX, Row.CREATE_NULL_AS_BLANK).setCellType(Cell.CELL_TYPE_STRING);
					row.getCell(STUDENT_TYPE_INDEX, Row.CREATE_NULL_AS_BLANK).setCellType(Cell.CELL_TYPE_STRING);
					TimeBoundUserMapping bean = new TimeBoundUserMapping();
					String sapid = row.getCell(SAPID_INDEX, Row.CREATE_NULL_AS_BLANK).getStringCellValue();
					String studentType = row.getCell(STUDENT_TYPE_INDEX, Row.CREATE_NULL_AS_BLANK).getStringCellValue();
					
					if("".equals(sapid.trim()) ){
						break;
					}
					
					if(bean.isErrorRecord()){
						errorBeanList.add(bean);
						continue;
					}
					
					bean.setUserId(sapid);
					bean.setCreatedBy(userId);
					bean.setTimebound_subject_config_id(Integer.parseInt(fileBean.getId()));
					
					//Replace Regular studentType to 'Student' to save in DB.
					bean.setStudentType(studentType.equalsIgnoreCase("Regular") ? "Student" : studentType);
					
					if (!"Resit".equalsIgnoreCase(bean.getStudentType())) {
						if(!studentList.contains(bean.getUserId().trim())){
							bean.setErrorMessage(bean.getErrorMessage()+" Row : "+ (i+1) +" "+bean.getUserId().trim()+" Invalid Sapid / Student Don't have this subject.");
							bean.setErrorRecord(true);
						}
					}
					
					List<String> studentTypeList = Arrays.asList("Regular","Resit");
					if (!studentTypeList.contains(studentType)) {
						bean.setErrorMessage(bean.getErrorMessage()+" Row : "+ (i+1) +" "+bean.getStudentType().trim()+" Please enter valid Student type ("+studentTypeList+")");
						bean.setErrorRecord(true);
					}
					
					if(bean.isErrorRecord()) {
						boolean isAppliedForSubjectRepeat=serviceRequestDao.checkIfAppliedForSubjectRepeat(bean.getUserId(),subject);
						if(isAppliedForSubjectRepeat) {
							bean.setErrorRecord(false);
						}						
					}
					
					if(bean.isErrorRecord()){
						errorBeanList.add(bean);
					}else{
						studentSapIdList.add(bean);
					}
					
					if(i % 1000 == 0){
					}
				}
			}
			
		} catch (Exception e) {
			
		}
		
		resultList.add(studentSapIdList);
		resultList.add(errorBeanList);
		return resultList;
		
	}

	//added for bulk upload of program name entry: Start
	public ArrayList<List> readProgramNameExcel(ProgramExamBean fileBean,String userId){
		int CODE_INDEX=0;
		int NAME_INDEX=1;
		ByteArrayInputStream bis = new ByteArrayInputStream(fileBean.getFileData().getBytes());
		Workbook workbook;
		List<ProgramExamBean> programNameList = new ArrayList<ProgramExamBean>();
		List<ProgramExamBean> errorBeanList = new ArrayList<ProgramExamBean>();
		ArrayList<List> resultList = new ArrayList<>();
		try{
			if (fileBean.getFileData().getOriginalFilename().endsWith("xls")) {
				workbook = new HSSFWorkbook(bis);
			} else if (fileBean.getFileData().getOriginalFilename().endsWith("xlsx")) {
				workbook = new XSSFWorkbook(bis);
			} else {
				throw new IllegalArgumentException("Received file does not have a standard excel extension.");
			}
			
			XSSFSheet sheet = (XSSFSheet) workbook.getSheetAt(0);
			Iterator<Row> rowIterator = sheet.iterator();
			int i = 0;
			//Skip first row since it contains column names, not data.
			if(rowIterator.hasNext()){
				Row row = rowIterator.next();
			}
			while(rowIterator.hasNext()){
				i++;
				Row row = rowIterator.next();
				ProgramExamBean bean = new ProgramExamBean();
				
				try {
					if(row!=null){
						
						row.getCell(CODE_INDEX, Row.CREATE_NULL_AS_BLANK).setCellType(Cell.CELL_TYPE_STRING);
						row.getCell(NAME_INDEX, Row.CREATE_NULL_AS_BLANK).setCellType(Cell.CELL_TYPE_STRING);
						
						String code = row.getCell(CODE_INDEX, Row.CREATE_NULL_AS_BLANK).getStringCellValue();
						String name = row.getCell(NAME_INDEX, Row.CREATE_NULL_AS_BLANK).getStringCellValue();
						
						if(StringUtils.isBlank(code) || StringUtils.isBlank(name)  ) {
							break;
						}
						
						bean.setCode(code);
						bean.setName(name);
						bean.setCreatedBy(userId);
						bean.setLastModifiedBy(userId);
						
						if(i % 1000 == 0){
						}
					}
					programNameList.add(bean);
				} catch (Exception e) {
					// TODO Auto-generated catch block
					
					bean.setErrorRecord(true);
					bean.setErrorMessage("Error in row "+i+1+", Error :"+e.getMessage());
					errorBeanList.add(bean);
				}
				
			}
			
			
		}catch(Exception e){
			
		}
		resultList.add(programNameList);
		resultList.add(errorBeanList);
		return resultList;
	}
	
	//added for bulk upload of program details entry: Start
	public ArrayList<List> readProgramDetailsExcel(ProgramExamBean fileBean,String userId){
		int PROGRAM_INDEX=0;
		int PROGRAMNAME_INDEX=1;
		int PROGRAMCODE_INDEX=2;
		int PROGRAMDURATION_INDEX=3;
		int PROGRAMDURATIONUNIT_INDEX=4;
		int PROGRAMTYPE_INDEX=5;
		int NOSUBJECTSTOCLEAR_INDEX=6;
		int NOSUBJECTSTOCLEARLATERAL_INDEX=7;
		int PROGRAMSTRUCTURE_INDEX=8;
		int EXAMDURATIONINMINUTES_INDEX=9;
		int NOSEMESTERS_INDEX=10;
		int NOSUBJECTSTOCLEARSEM_INDEX=11;
		int ACTIVE_INDEX=12;
		
		
		ByteArrayInputStream bis = new ByteArrayInputStream(fileBean.getFileData().getBytes());
		Workbook workbook;
		List<ProgramExamBean> programNameList = new ArrayList<ProgramExamBean>();
		List<ProgramExamBean> errorBeanList = new ArrayList<ProgramExamBean>();
		ArrayList<List> resultList = new ArrayList<>();
		try{
			if (fileBean.getFileData().getOriginalFilename().endsWith("xls")) {
				workbook = new HSSFWorkbook(bis);
			} else if (fileBean.getFileData().getOriginalFilename().endsWith("xlsx")) {
				workbook = new XSSFWorkbook(bis);
			} else {
				throw new IllegalArgumentException("Received file does not have a standard excel extension.");
			}
			
			XSSFSheet sheet = (XSSFSheet) workbook.getSheetAt(0);
			Iterator<Row> rowIterator = sheet.iterator();
			int i = 0;
			//Skip first row since it contains column names, not data.
			if(rowIterator.hasNext()){
				Row row = rowIterator.next();
			}
			while(rowIterator.hasNext()){
				i++;
				Row row = rowIterator.next();
				ProgramExamBean bean = new ProgramExamBean();
				
				try {
					if(row!=null){
						
						row.getCell(PROGRAM_INDEX, Row.CREATE_NULL_AS_BLANK).setCellType(Cell.CELL_TYPE_STRING);
						row.getCell(PROGRAMNAME_INDEX, Row.CREATE_NULL_AS_BLANK).setCellType(Cell.CELL_TYPE_STRING);
						row.getCell(PROGRAMCODE_INDEX, Row.CREATE_NULL_AS_BLANK).setCellType(Cell.CELL_TYPE_STRING);
						row.getCell(PROGRAMDURATION_INDEX, Row.CREATE_NULL_AS_BLANK).setCellType(Cell.CELL_TYPE_STRING);
						row.getCell(PROGRAMDURATIONUNIT_INDEX, Row.CREATE_NULL_AS_BLANK).setCellType(Cell.CELL_TYPE_STRING);
						row.getCell(PROGRAMTYPE_INDEX, Row.CREATE_NULL_AS_BLANK).setCellType(Cell.CELL_TYPE_STRING);
						row.getCell(NOSUBJECTSTOCLEAR_INDEX, Row.CREATE_NULL_AS_BLANK).setCellType(Cell.CELL_TYPE_STRING);
						row.getCell(NOSUBJECTSTOCLEARLATERAL_INDEX, Row.CREATE_NULL_AS_BLANK).setCellType(Cell.CELL_TYPE_STRING);
						row.getCell(PROGRAMSTRUCTURE_INDEX, Row.CREATE_NULL_AS_BLANK).setCellType(Cell.CELL_TYPE_STRING);
						row.getCell(EXAMDURATIONINMINUTES_INDEX, Row.CREATE_NULL_AS_BLANK).setCellType(Cell.CELL_TYPE_STRING);
						row.getCell(NOSEMESTERS_INDEX, Row.CREATE_NULL_AS_BLANK).setCellType(Cell.CELL_TYPE_STRING);
						row.getCell(NOSUBJECTSTOCLEARSEM_INDEX, Row.CREATE_NULL_AS_BLANK).setCellType(Cell.CELL_TYPE_STRING);
						row.getCell(ACTIVE_INDEX, Row.CREATE_NULL_AS_BLANK).setCellType(Cell.CELL_TYPE_STRING);
						
						/*String code = row.getCell(CODE_INDEX, Row.CREATE_NULL_AS_BLANK).getStringCellValue();
						String name = row.getCell(NAME_INDEX, Row.CREATE_NULL_AS_BLANK).getStringCellValue();
						
						if(StringUtils.isBlank(code) || StringUtils.isBlank(name)  ) {
							break;
						}
						
						bean.setCode(code);
						bean.setName(name);*/
						bean.setCreatedBy(userId);
						bean.setLastModifiedBy(userId);
						
						if(i % 1000 == 0){
						}
					}
					programNameList.add(bean);
				} catch (Exception e) {
					// TODO Auto-generated catch block
					
					bean.setErrorRecord(true);
					bean.setErrorMessage("Error in row "+i+1+", Error :"+e.getMessage());
					errorBeanList.add(bean);
				}
				
			}
			
			
		}catch(Exception e){
			
		}
		resultList.add(programNameList);
		resultList.add(errorBeanList);
		return resultList;
	}
	
	public ArrayList<MBACentersBean> addExcelMBACenters(MBACentersBean mbawxCentersBean) {
		int NAME = 0;
		int CITY = 1;
		int STATE = 2;
		int CAPACITY = 3;
		int ADDRESS = 4;
		int GOOGLEMAPURL = 5;
		int LOCALITY = 6;
		
		
		ByteArrayInputStream bis = new ByteArrayInputStream(mbawxCentersBean.getFileData().getBytes());
		Workbook workbook;
		ArrayList<MBACentersBean> mbawxCentersBeanList = new ArrayList<MBACentersBean>();
		try {
			if (mbawxCentersBean.getFileData().getOriginalFilename().toLowerCase().endsWith("xls")) {
				workbook = new HSSFWorkbook(bis);
			} else if (mbawxCentersBean.getFileData().getOriginalFilename().toLowerCase().endsWith("xlsx")) {
				workbook = new XSSFWorkbook(bis);
			} else {
				throw new IllegalArgumentException("Received file does not have a standard excel extension.");
			}
			
			XSSFSheet sheet = (XSSFSheet) workbook.getSheetAt(0);
			Iterator<Row> rowIterator = sheet.iterator();
			
			int i = 0;
			//Skip first row since it contains column names, not data.
			if(rowIterator.hasNext()){
				Row row = rowIterator.next();
			}
			
			while(rowIterator.hasNext()) {
				i++;
				Row row = rowIterator.next();
				if(row!=null){

					MBACentersBean centersBean = new MBACentersBean();
					centersBean.setCreatedDate(mbawxCentersBean.getCreatedDate());
					centersBean.setLastModifiedBy(mbawxCentersBean.getLastModifiedBy());
					try {
						
						row.getCell(NAME, Row.CREATE_NULL_AS_BLANK).setCellType(Cell.CELL_TYPE_STRING);
						String name = row.getCell(NAME, Row.CREATE_NULL_AS_BLANK).getStringCellValue();
						centersBean.setName(name);

						row.getCell(CITY, Row.CREATE_NULL_AS_BLANK).setCellType(Cell.CELL_TYPE_STRING);
						String city = row.getCell(CITY, Row.CREATE_NULL_AS_BLANK).getStringCellValue();
						centersBean.setCity(city);

						row.getCell(STATE, Row.CREATE_NULL_AS_BLANK).setCellType(Cell.CELL_TYPE_STRING);
						String state = row.getCell(STATE, Row.CREATE_NULL_AS_BLANK).getStringCellValue();
						centersBean.setState(state);

						row.getCell(CAPACITY, Row.CREATE_NULL_AS_BLANK).setCellType(Cell.CELL_TYPE_STRING);
						String capacity = row.getCell(CAPACITY, Row.CREATE_NULL_AS_BLANK).getStringCellValue();
						if(StringUtils.isBlank(capacity) || !StringUtils.isNumeric(capacity)) {
							centersBean.setError("Invalid Capacity : " + capacity);
						} else {
							centersBean.setCapacity(Integer.parseInt(capacity));
						}

						row.getCell(ADDRESS, Row.CREATE_NULL_AS_BLANK).setCellType(Cell.CELL_TYPE_STRING);
						String address = row.getCell(ADDRESS, Row.CREATE_NULL_AS_BLANK).getStringCellValue();
						centersBean.setAddress(address);

						row.getCell(GOOGLEMAPURL, Row.CREATE_NULL_AS_BLANK).setCellType(Cell.CELL_TYPE_STRING);
						String googleMapUrl = row.getCell(GOOGLEMAPURL, Row.CREATE_NULL_AS_BLANK).getStringCellValue();
						centersBean.setGoogleMapUrl(googleMapUrl);

						row.getCell(LOCALITY, Row.CREATE_NULL_AS_BLANK).setCellType(Cell.CELL_TYPE_STRING);
						String locality = row.getCell(LOCALITY, Row.CREATE_NULL_AS_BLANK).getStringCellValue();
						centersBean.setLocality(locality);

						centersBean.setActive("Y");
						
						if(name == null || name.isEmpty() || name.trim() == "") {
							break;
						}
						
					}catch (Exception e) {
						String error = e.getMessage();
						centersBean.setError("Error while mapping record : " + error);
					}
					
					mbawxCentersBeanList.add(centersBean);
				}
			}
			return mbawxCentersBeanList;
		} catch (Exception e) {
			
			return mbawxCentersBeanList;
		}
	}
	

	public ArrayList<MBATimeTableBean> addExcelMBATimeTable(MBATimeTableBean inputBean) throws IOException {
		int colIn = 0;
		
		
		int SUBJECT_NAME = colIn++;
		int TERM = colIn++;
		int EXAM_YEAR = colIn++;
		int EXAM_MONTH = colIn++;

		int DATE_INDEX = colIn++;
		int START_TIME_INDEX = colIn++;
		int END_TIME_INDEX = colIn++;
		
		ByteArrayInputStream bis = new ByteArrayInputStream(inputBean.getFileData().getBytes());
		Workbook workbook;

		if (inputBean.getFileData().getOriginalFilename().toLowerCase().endsWith("xls")) {
			workbook = new HSSFWorkbook(bis);
		} else if (inputBean.getFileData().getOriginalFilename().toLowerCase().endsWith("xlsx")) {
			workbook = new XSSFWorkbook(bis);
		} else {
			throw new IllegalArgumentException("Received file does not have a standard excel extension.");
		}
		
		XSSFSheet sheet = (XSSFSheet) workbook.getSheetAt(0);
		Iterator<Row> rowIterator = sheet.iterator();
		
		int i = 0;
		//Skip first row since it contains column names, not data.
		if(rowIterator.hasNext()){
			Row row = rowIterator.next();
		}

		ArrayList<MBATimeTableBean> mbaTimeTableBeans = new ArrayList<MBATimeTableBean>();

		SimpleDateFormat dateTimeFormatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		while(rowIterator.hasNext()) {
			i++;
			Row row = rowIterator.next();
			if(row!=null){
				
				MBATimeTableBean mbaTimeTableBean = new MBATimeTableBean();
				mbaTimeTableBean.setCreatedBy(inputBean.getCreatedBy());
				mbaTimeTableBean.setLastModifiedBy(inputBean.getLastModifiedBy());
				
				try {
					row.getCell(SUBJECT_NAME, Row.CREATE_NULL_AS_BLANK).setCellType(Cell.CELL_TYPE_STRING);
					String subjectName = row.getCell(SUBJECT_NAME, Row.CREATE_NULL_AS_BLANK).getStringCellValue();
					mbaTimeTableBean.setSubjectName(subjectName);

					row.getCell(EXAM_YEAR, Row.CREATE_NULL_AS_BLANK).setCellType(Cell.CELL_TYPE_STRING);
					String examYear = row.getCell(EXAM_YEAR, Row.CREATE_NULL_AS_BLANK).getStringCellValue();
					mbaTimeTableBean.setExamYear(examYear);
					
					row.getCell(EXAM_MONTH, Row.CREATE_NULL_AS_BLANK).setCellType(Cell.CELL_TYPE_STRING);
					String examMonth = row.getCell(EXAM_MONTH, Row.CREATE_NULL_AS_BLANK).getStringCellValue();
					mbaTimeTableBean.setExamMonth(examMonth);
					
					row.getCell(TERM, Row.CREATE_NULL_AS_BLANK).setCellType(Cell.CELL_TYPE_STRING);
					String term = row.getCell(TERM, Row.CREATE_NULL_AS_BLANK).getStringCellValue();
					mbaTimeTableBean.setTerm(term);
					
					Cell dateCell = row.getCell(DATE_INDEX);
					String examDate = formatDateCell(dateCell, "yyyy-MM-dd");
					mbaTimeTableBean.setExamDate(examDate);
					
					Cell startTimeCell = row.getCell(START_TIME_INDEX);
					String examStartTime = formatDateCell(startTimeCell, "HH:mm:ss");
					Date examStartDateTime = dateTimeFormatter.parse(examDate + " " + examStartTime);
					
					mbaTimeTableBean.setExamStartTime(examStartTime);
					mbaTimeTableBean.setExamStartDateTime(examStartDateTime);
					
					Cell endTimeCell = row.getCell(END_TIME_INDEX);
					String examEndTime = formatDateCell(endTimeCell, "HH:mm:ss");
					mbaTimeTableBean.setExamEndTime(examEndTime);
					
					Date examEndDateTime = dateTimeFormatter.parse(examDate + " " + examEndTime);
					mbaTimeTableBean.setExamEndDateTime(examEndDateTime);
					
					mbaTimeTableBean.setProgram(inputBean.getProgram());
					
				}catch (Exception e) {
					String error = e.getMessage();
					mbaTimeTableBean.setError("Error while mapping record : " + error);
				}
				
				mbaTimeTableBeans.add(mbaTimeTableBean);
			}
		}
		
		return mbaTimeTableBeans;
			
	}
	
	private String formatDateCell(Cell cell, String format) {
		SimpleDateFormat sdf = new java.text.SimpleDateFormat(format);
		Date date = null;
		if (DateUtil.isCellDateFormatted(cell)) {
			date = cell.getDateCellValue();
		} else {
			date = HSSFDateUtil.getJavaDate(cell.getNumericCellValue());
		}
		
		return sdf.format(date);
	}

	public ArrayList<List> readMBAXAbsentExcel(TEEResultBean resultBean,String userId){

		
		int  SAPID_INDEX = 0;
		

		ByteArrayInputStream bis = new ByteArrayInputStream(resultBean.getFileData().getBytes());
		Workbook workbook;
		List<TEEResultBean> successBeanList = new ArrayList<TEEResultBean>();
		List<TEEResultBean> errorBeanList = new ArrayList<TEEResultBean>();
		ArrayList<List> resultList = new ArrayList<>();
		HashMap<String, String> sapIdSujectKeys = new HashMap<>();

		try {
			if (resultBean.getFileData().getOriginalFilename().toLowerCase().endsWith("xls")) {
				workbook = new HSSFWorkbook(bis);
			} else if (resultBean.getFileData().getOriginalFilename().toLowerCase().endsWith("xlsx")) {
				workbook = new XSSFWorkbook(bis);
			} else {
				throw new IllegalArgumentException("Received file does not have a standard excel extension.");
			}
			
			//Get first/desired sheet from the workbook
			XSSFSheet sheet = (XSSFSheet) workbook.getSheetAt(0);

			Iterator<Row> rowIterator = sheet.iterator();

			int i = 0;
			//Skip first row since it contains column names, not data.
			if(rowIterator.hasNext()){
				Row row = rowIterator.next();
			}
			while(rowIterator.hasNext()) {
				i++;
				Row row = rowIterator.next();


				if(row!=null){
					
					row.getCell(SAPID_INDEX, Row.CREATE_NULL_AS_BLANK).setCellType(Cell.CELL_TYPE_STRING);
					
					TEEResultBean bean = new TEEResultBean();
				
					String sapid = row.getCell(SAPID_INDEX, Row.CREATE_NULL_AS_BLANK).getStringCellValue();
					
					  bean.setSapid(sapid.trim()); 
					  bean.setPrgm_sem_subj_id(resultBean.getPrgm_sem_subj_id());
					  bean.setScore(0);
					  bean.setStatus("AB");
					  bean.setTimebound_id(resultBean.getTimebound_id());
					  bean.setCreatedBy(userId);
					  bean.setLastModifiedBy(userId);
					if("".equals(sapid.trim())){
						errorBeanList.add(bean);
					}else {
						successBeanList.add(bean);
					}


					if(i % 1000 == 0){
					}
				}
			}
		} catch (IOException e) {
			
		}
		resultList.add(successBeanList);
		resultList.add(errorBeanList);
		return resultList;
	}
	

	public List<MBALiveSettings> addExcelMBALiveSettings(MBALiveSettings inputBean) {

		int PROGRAM_STRUCTURE = 0;
		
		int ACADS_YEAR = 1;
		int ACADS_MONTH = 2;
		int EXAM_YEAR = 3;
		int EXAM_MONTH = 4;

		int START_DATE_INDEX = 5;
		int START_TIME_INDEX = 6;
		int END_DATE_INDEX = 7;
		int END_TIME_INDEX = 8;
		
		ByteArrayInputStream bis = new ByteArrayInputStream(inputBean.getFileData().getBytes());
		Workbook workbook;
		try {
			if (inputBean.getFileData().getOriginalFilename().toLowerCase().endsWith("xls")) {
				workbook = new HSSFWorkbook(bis);
			} else if (inputBean.getFileData().getOriginalFilename().toLowerCase().endsWith("xlsx")) {
				workbook = new XSSFWorkbook(bis);
			} else {
				throw new IllegalArgumentException("Received file does not have a standard excel extension.");
			}
			
			
			XSSFSheet sheet = (XSSFSheet) workbook.getSheetAt(0);
			Iterator<Row> rowIterator = sheet.iterator();
			
			int i = 0;
			//Skip first row since it contains column names, not data.
			if(rowIterator.hasNext()){
				Row row = rowIterator.next();
			}

			ArrayList<MBALiveSettings> liveSettingsBeans = new ArrayList<MBALiveSettings>();
			
			while(rowIterator.hasNext()) {
				i++;
				Row row = rowIterator.next();
				if(row!=null){
					MBALiveSettings liveSettings = new MBALiveSettings();
					
					row.getCell(PROGRAM_STRUCTURE, Row.CREATE_NULL_AS_BLANK).setCellType(Cell.CELL_TYPE_STRING);
					String programStructure = row.getCell(PROGRAM_STRUCTURE, Row.CREATE_NULL_AS_BLANK).getStringCellValue();
					liveSettings.setProgramStructure(programStructure);

					row.getCell(ACADS_YEAR, Row.CREATE_NULL_AS_BLANK).setCellType(Cell.CELL_TYPE_STRING);
					String acadYear = row.getCell(ACADS_YEAR, Row.CREATE_NULL_AS_BLANK).getStringCellValue();
					liveSettings.setAcadsYear(acadYear);

					row.getCell(ACADS_MONTH, Row.CREATE_NULL_AS_BLANK).setCellType(Cell.CELL_TYPE_STRING);
					String acadMonth = row.getCell(ACADS_MONTH, Row.CREATE_NULL_AS_BLANK).getStringCellValue();
					liveSettings.setAcadsMonth(acadMonth);

					row.getCell(EXAM_YEAR, Row.CREATE_NULL_AS_BLANK).setCellType(Cell.CELL_TYPE_STRING);
					String examYear = row.getCell(EXAM_YEAR, Row.CREATE_NULL_AS_BLANK).getStringCellValue();
					liveSettings.setExamYear(examYear);
					
					row.getCell(EXAM_MONTH, Row.CREATE_NULL_AS_BLANK).setCellType(Cell.CELL_TYPE_STRING);
					String examMonth = row.getCell(EXAM_MONTH, Row.CREATE_NULL_AS_BLANK).getStringCellValue();
					liveSettings.setExamMonth(examMonth);
					
					Cell startDateCell = row.getCell(START_DATE_INDEX);
					String startDate = formatDateCell(startDateCell, "yyyy-MM-dd");
					liveSettings.setStartDateStr(startDate);
					
					Cell startTimeCell = row.getCell(START_TIME_INDEX);
					String startTime = formatDateCell(startTimeCell, "HH:mm:ss");
					liveSettings.setStartTimeStr(startTime);
					
					Cell endDateCell = row.getCell(END_DATE_INDEX);
					String endDate = formatDateCell(endDateCell, "yyyy-MM-dd");
					liveSettings.setEndDateStr(endDate);
					
					Cell endTimeCell = row.getCell(END_TIME_INDEX);
					String endTime = formatDateCell(endTimeCell, "HH:mm:ss");
					liveSettings.setEndTimeStr(endTime);

					String type = inputBean.getType();
					liveSettings.setType(type);
					liveSettings.setProgram(inputBean.getProgram());

					
					liveSettingsBeans.add(liveSettings);
				}
			}
			return liveSettingsBeans;
			
		} catch (Exception e) {
			
			return null;
		}
	}
	
		public List<ProjectTitle> addExcelProjectTitles(ProjectTitle bean) {
				int TITLE = 0;
				
				ByteArrayInputStream bis = new ByteArrayInputStream(bean.getFileData().getBytes());
				Workbook workbook;
				List<ProjectTitle> list = new ArrayList<ProjectTitle>();
				
				List<ProjectTitle> listOfConsumerPrograms = new ArrayList<ProjectTitle>();
				List<String> listOfProgramIds = bean.getProgramId() != null ? Arrays.asList(bean.getProgramId().split(",")) : new ArrayList<String>();
				List<String> listOfProgramStructureIds = bean.getProgramStructureId() != null ? Arrays.asList(bean.getProgramStructureId().split(",")) : new ArrayList<String>();
				String consumerTypeId = bean.getConsumerTypeId();
				
				for (String programId : listOfProgramIds) {
					for (String programStructureId : listOfProgramStructureIds) {
						ProjectTitle cps = new ProjectTitle();
						cps.setConsumerTypeId(consumerTypeId);
						cps.setProgramId(programId);;
						cps.setProgramStructureId(programStructureId);
						listOfConsumerPrograms.add(cps);
					}
				}
				
				try {
					if (bean.getFileData().getOriginalFilename().toLowerCase().endsWith("xls")) {
						workbook = new HSSFWorkbook(bis);
				} else if (bean.getFileData().getOriginalFilename().toLowerCase().endsWith("xlsx")) {
						workbook = new XSSFWorkbook(bis);
					} else {
						throw new IllegalArgumentException("Received file does not have a standard excel extension.");
					}
					
					XSSFSheet sheet = (XSSFSheet) workbook.getSheetAt(0);
					Iterator<Row> rowIterator = sheet.iterator();
					
					int i = 0;
					//Skip first row since it contains column names, not data.
					if(rowIterator.hasNext()){
						Row row = rowIterator.next();
					}
					
					while(rowIterator.hasNext()) {
						i++;
						Row row = rowIterator.next();
						if(row!=null){
		
							try {
								row.getCell(TITLE, Row.CREATE_NULL_AS_BLANK).setCellType(Cell.CELL_TYPE_STRING);
								String title = row.getCell(TITLE, Row.CREATE_NULL_AS_BLANK).getStringCellValue();
								for (ProjectTitle cpsBean : listOfConsumerPrograms) {
									ProjectTitle titleBean = new ProjectTitle();
									
									titleBean.setCreatedBy(bean.getCreatedBy());
									titleBean.setUpdatedBy(bean.getUpdatedBy());
									titleBean.setSubject(bean.getSubject());
									titleBean.setActive("Y");
									titleBean.setTitle(title);
									titleBean.setConsumerTypeId(cpsBean.getConsumerTypeId());
									titleBean.setProgramId(cpsBean.getProgramId());;
									titleBean.setProgramStructureId(cpsBean.getProgramStructureId());
									
									
									list.add(titleBean);
								}
							}catch (Exception e) {
								String error = e.getMessage();
		 
								ProjectTitle titleBean = new ProjectTitle();
								titleBean.setError("Error while mapping record : " + error);
								list.add(titleBean);
							}
						}
					}
					return list;
				} catch (Exception e) {
					
					return list;
				}
		}
		
	public ArrayList<String> readExcelBodInput(BodBean bod) throws IOException
	{
		int QUESTIONID=0;
		
		ByteArrayInputStream bis = new ByteArrayInputStream(bod.getFileData().getBytes());
		Workbook workbook;
			if (bod.getFileData().getOriginalFilename().toLowerCase().endsWith("xls")) {
				workbook = new HSSFWorkbook(bis);
			} else if (bod.getFileData().getOriginalFilename().toLowerCase().endsWith("xlsx")) {
				workbook = new XSSFWorkbook(bis);
			} else {
				throw new IllegalArgumentException("Received file does not have a standard excel extension.");
			}
			
			XSSFSheet sheet = (XSSFSheet) workbook.getSheetAt(0);
			Iterator<Row> rowIterator = sheet.iterator();
			
			//Skip first row since it contains column names, not data.
			if(rowIterator.hasNext()){
				Row row = rowIterator.next();
			}
			
			ArrayList<String> questionIdsList = new ArrayList<String>();
			
			excelReadLoop: while (rowIterator.hasNext()) {
				Row row = rowIterator.next();
				if (row != null) {
					row.getCell(QUESTIONID, Row.CREATE_NULL_AS_BLANK).setCellType(Cell.CELL_TYPE_STRING);
					String questionId = row.getCell(QUESTIONID, Row.CREATE_NULL_AS_BLANK).getStringCellValue();

					if (null == questionId || "".equals(questionId))
						continue excelReadLoop;
					else
						questionIdsList.add(questionId);
				}
			}
			return questionIdsList;
	}
	
	public FailedregistrationExcelBean readFailedRegistrationExcel(FailedRegistrationBean bean)
	{
		int FirstName=0;
		int LastName=1;
		int Sapid=2;
		int EmailId=3;
		int ImageUrl=4;
		int OpenLinkFlag=5;
		int ScheduleAccessKey=6;
		int TimeBoundId=7;
		int ExamStartDateTime=8;
		int ExamEndDateTime=9;
		int AccessExamStartDateTime=10;
		int AccessExamEndDateTime=11;
		int AssessmentName=12;
		int AssessmentDuration=13;
		int ScheduleId=14;
		int ScheduleAccessUrl=15;
		int ScheduleName=16;
		int SubjectName=17;
		int MaxScore=18;
		int ReportingStartTime=19;
		int ReportingFinishTime=20;

		boolean runOnce=true;
		
		ByteArrayInputStream bis = new ByteArrayInputStream(bean.getFileData().getBytes());
		Workbook workbook;
		try
		{
			if (bean.getFileData().getOriginalFilename().toLowerCase().endsWith("xls")) {
				workbook = new HSSFWorkbook(bis);
			} else if (bean.getFileData().getOriginalFilename().toLowerCase().endsWith("xlsx")) {
				workbook = new XSSFWorkbook(bis);
			} else {
				throw new IllegalArgumentException("Received file does not have a standard excel extension.");
			}
			
			XSSFSheet sheet = (XSSFSheet) workbook.getSheetAt(0);
			Iterator<Row> rowIterator = sheet.iterator();
			
			//Skip first row since it contains column names, not data.
			if(rowIterator.hasNext()){
				Row row = rowIterator.next();
			}
			
			FailedregistrationExcelBean failedRegistrationExcelBean = new FailedregistrationExcelBean();
			ArrayList<MettlRegisterCandidateBean> userList = new ArrayList<MettlRegisterCandidateBean>();
			ExamsAssessmentsBean examBean = new ExamsAssessmentsBean();
			String subject="";
			String endTime="";
			
			while(rowIterator.hasNext()){
				Row row = rowIterator.next();
				if(row!=null)
				{
					row.getCell(FirstName, Row.CREATE_NULL_AS_BLANK).setCellType(Cell.CELL_TYPE_STRING);
					String firstName = row.getCell(FirstName, Row.CREATE_NULL_AS_BLANK).getStringCellValue();
					
					row.getCell(LastName, Row.CREATE_NULL_AS_BLANK).setCellType(Cell.CELL_TYPE_STRING);
					String lastName = row.getCell(LastName, Row.CREATE_NULL_AS_BLANK).getStringCellValue();
					
					row.getCell(Sapid, Row.CREATE_NULL_AS_BLANK).setCellType(Cell.CELL_TYPE_STRING);
					String sapid = row.getCell(Sapid, Row.CREATE_NULL_AS_BLANK).getStringCellValue();
					
					row.getCell(EmailId, Row.CREATE_NULL_AS_BLANK).setCellType(Cell.CELL_TYPE_STRING);
					String emailId = row.getCell(EmailId, Row.CREATE_NULL_AS_BLANK).getStringCellValue();
					
					row.getCell(ImageUrl, Row.CREATE_NULL_AS_BLANK).setCellType(Cell.CELL_TYPE_STRING);
					String imageUrl = row.getCell(ImageUrl, Row.CREATE_NULL_AS_BLANK).getStringCellValue();
					
					row.getCell(OpenLinkFlag, Row.CREATE_NULL_AS_BLANK).setCellType(Cell.CELL_TYPE_BOOLEAN);
					boolean openLinkFlag = row.getCell(OpenLinkFlag, Row.CREATE_NULL_AS_BLANK).getBooleanCellValue();
					
					row.getCell(ScheduleAccessKey, Row.CREATE_NULL_AS_BLANK).setCellType(Cell.CELL_TYPE_STRING);
					String scheduleAccessKey = row.getCell(ScheduleAccessKey, Row.CREATE_NULL_AS_BLANK).getStringCellValue();
					
					row.getCell(TimeBoundId, Row.CREATE_NULL_AS_BLANK).setCellType(Cell.CELL_TYPE_STRING);
					String timeBoundId = row.getCell(TimeBoundId, Row.CREATE_NULL_AS_BLANK).getStringCellValue();
					
					row.getCell(ExamStartDateTime, Row.CREATE_NULL_AS_BLANK).setCellType(Cell.CELL_TYPE_STRING);
					String examStartDateTime = row.getCell(ExamStartDateTime, Row.CREATE_NULL_AS_BLANK).getStringCellValue();
					
					row.getCell(ExamEndDateTime, Row.CREATE_NULL_AS_BLANK).setCellType(Cell.CELL_TYPE_STRING);
					String examEndDateTime = row.getCell(ExamEndDateTime, Row.CREATE_NULL_AS_BLANK).getStringCellValue();
					
					row.getCell(AccessExamStartDateTime, Row.CREATE_NULL_AS_BLANK).setCellType(Cell.CELL_TYPE_STRING);
					String accessExamStartDateTime = row.getCell(AccessExamStartDateTime, Row.CREATE_NULL_AS_BLANK).getStringCellValue();
					
					row.getCell(AccessExamEndDateTime, Row.CREATE_NULL_AS_BLANK).setCellType(Cell.CELL_TYPE_STRING);
					String accessExamEndDateTime = row.getCell(AccessExamEndDateTime, Row.CREATE_NULL_AS_BLANK).getStringCellValue();
					
					row.getCell(AssessmentName, Row.CREATE_NULL_AS_BLANK).setCellType(Cell.CELL_TYPE_STRING);
					String assessmentName = row.getCell(AssessmentName, Row.CREATE_NULL_AS_BLANK).getStringCellValue();
					
					row.getCell(AssessmentDuration, Row.CREATE_NULL_AS_BLANK).setCellType(Cell.CELL_TYPE_STRING);
					String assessmentDuration = row.getCell(AssessmentDuration, Row.CREATE_NULL_AS_BLANK).getStringCellValue();
					
					row.getCell(ScheduleId, Row.CREATE_NULL_AS_BLANK).setCellType(Cell.CELL_TYPE_STRING);
					String scheduleId = row.getCell(ScheduleId, Row.CREATE_NULL_AS_BLANK).getStringCellValue();
					
					row.getCell(ScheduleAccessUrl, Row.CREATE_NULL_AS_BLANK).setCellType(Cell.CELL_TYPE_STRING);
					String scheduleAccessUrl = row.getCell(ScheduleAccessUrl, Row.CREATE_NULL_AS_BLANK).getStringCellValue();
					
					row.getCell(ScheduleName, Row.CREATE_NULL_AS_BLANK).setCellType(Cell.CELL_TYPE_STRING);
					String scheduleName = row.getCell(ScheduleName, Row.CREATE_NULL_AS_BLANK).getStringCellValue();
					
					row.getCell(SubjectName, Row.CREATE_NULL_AS_BLANK).setCellType(Cell.CELL_TYPE_STRING);
					String subjectName = row.getCell(SubjectName, Row.CREATE_NULL_AS_BLANK).getStringCellValue();
					
					row.getCell(MaxScore, Row.CREATE_NULL_AS_BLANK).setCellType(Cell.CELL_TYPE_STRING);
					String maxScore = row.getCell(MaxScore, Row.CREATE_NULL_AS_BLANK).getStringCellValue();
					
					row.getCell(ReportingStartTime, Row.CREATE_NULL_AS_BLANK).setCellType(Cell.CELL_TYPE_STRING);
					String reportingStartTime = row.getCell(ReportingStartTime, Row.CREATE_NULL_AS_BLANK).getStringCellValue();
					
					row.getCell(ReportingFinishTime, Row.CREATE_NULL_AS_BLANK).setCellType(Cell.CELL_TYPE_STRING);
					String reportingFinishTime = row.getCell(ReportingFinishTime, Row.CREATE_NULL_AS_BLANK).getStringCellValue();
					
					
					if(runOnce)
					{
						examBean.setSchedule_accessKey(scheduleAccessKey);
						examBean.setTimebound_id(timeBoundId);
						examBean.setExam_start_date_time(examStartDateTime);
						examBean.setExam_end_date_time(accessExamEndDateTime);
						examBean.setName(assessmentName);
						examBean.setDuration(Integer.parseInt(assessmentDuration));
						examBean.setSchedule_id(scheduleId);
						examBean.setSchedule_accessUrl(scheduleAccessUrl);
						examBean.setSchedule_name(scheduleName);
						examBean.setMax_score(maxScore);
						examBean.setReporting_start_date_time(reportingStartTime);
						examBean.setReporting_finish_date_time(reportingFinishTime);
						subject=subjectName;
						endTime=examEndDateTime;
						runOnce=false;
					}
					
					MettlRegisterCandidateBean candidateBean = new MettlRegisterCandidateBean();
					candidateBean.setFirstName(firstName);
					candidateBean.setLastName(lastName);
					candidateBean.setSapId(sapid);
					candidateBean.setEmailAddress(emailId);
					candidateBean.setCandidateImage(imageUrl);
					candidateBean.setRegistrationImage(imageUrl);
					candidateBean.setOpenLinkFlag(openLinkFlag);
					userList.add(candidateBean);
					
				}
			}
			
			failedRegistrationExcelBean.setUserList(userList);
			failedRegistrationExcelBean.setExamBean(examBean);
			failedRegistrationExcelBean.setSubject(subject);
			failedRegistrationExcelBean.setEndTime(endTime);
			
			return failedRegistrationExcelBean;
		}
		catch(Exception e)
		{
			examRegisterPG.error("Error is:"+e.getMessage());
			return null;
		}
		
	}
	
	public List<MettlPGResponseBean> readExcelMettlExamInput(MettlResultsSyncBean inputBean) {

		int SAPID = 0;
//		int SCHEDULE_ACCESS_KEY = 1;
		int SUBJECT = 1;
		
		ByteArrayInputStream bis = new ByteArrayInputStream(inputBean.getFileData().getBytes());
		Workbook workbook;
		try {
			if (inputBean.getFileData().getOriginalFilename().toLowerCase().endsWith("xls")) {
				workbook = new HSSFWorkbook(bis);
			} else if (inputBean.getFileData().getOriginalFilename().toLowerCase().endsWith("xlsx")) {
				workbook = new XSSFWorkbook(bis);
			} else {
				throw new IllegalArgumentException("Received file does not have a standard excel extension.");
			}
			
			
			XSSFSheet sheet = (XSSFSheet) workbook.getSheetAt(0);
			Iterator<Row> rowIterator = sheet.iterator();
			
			int i = 0;
			//Skip first row since it contains column names, not data.
			if(rowIterator.hasNext()){
				Row row = rowIterator.next();
			}

			ArrayList<MettlPGResponseBean> toReturn = new ArrayList<MettlPGResponseBean>();
			
			while(rowIterator.hasNext()) {
				i++;
				Row row = rowIterator.next();
				if(row!=null){
					MettlPGResponseBean bean = new MettlPGResponseBean();
					
					row.getCell(SAPID, Row.CREATE_NULL_AS_BLANK).setCellType(Cell.CELL_TYPE_STRING);
					String sapid = row.getCell(SAPID, Row.CREATE_NULL_AS_BLANK).getStringCellValue();
					bean.setSapid(sapid);
					
					row.getCell(SUBJECT, Row.CREATE_NULL_AS_BLANK).setCellType(Cell.CELL_TYPE_STRING);
					String subject = row.getCell(SUBJECT, Row.CREATE_NULL_AS_BLANK).getStringCellValue();
					bean.setSubject(subject.replace("'", "\\'"));

					toReturn.add(bean);
				}
			}

			return toReturn;
			
		} catch (Exception e) {
			
			return null;
		}
	}
	
	
	public List<ProjectConfiguration> addExcelProjectConfigurations(ProjectConfiguration inputBean) {
				int PROGRAM = 0;
				int PROGRAM_STRUCTURE = 1;
				int SUBJECT = 2;
				int HAS_TITLE = 3; 
				int HAS_SOP = 4;
				int HAS_VIVA = 5;
				int HAS_SYNOPSIS = 6;
				int HAS_SUBMISSION = 7; 
				ByteArrayInputStream bis = new ByteArrayInputStream(inputBean.getFileData().getBytes());
				Workbook workbook;
				List<ProjectConfiguration> list = new ArrayList<ProjectConfiguration>();
				String consumerTypeId = inputBean.getConsumerTypeId();
				
				try {
					if (inputBean.getFileData().getOriginalFilename().toLowerCase().endsWith("xls")) {
						workbook = new HSSFWorkbook(bis);
					} else if (inputBean.getFileData().getOriginalFilename().toLowerCase().endsWith("xlsx")) {
						workbook = new XSSFWorkbook(bis);
					} else {
						throw new IllegalArgumentException("Received file does not have a standard excel extension.");
					}
					
					XSSFSheet sheet = (XSSFSheet) workbook.getSheetAt(0);
					Iterator<Row> rowIterator = sheet.iterator();
					
					int i = 0;
					//Skip first row since it contains column names, not data.
					if(rowIterator.hasNext()){
						Row row = rowIterator.next();
					}
					
					while(rowIterator.hasNext()) {
						i++;
						Row row = rowIterator.next();
						if(row!=null){
		
							try {
								row.getCell(PROGRAM, Row.CREATE_NULL_AS_BLANK).setCellType(Cell.CELL_TYPE_STRING);
								row.getCell(PROGRAM_STRUCTURE, Row.CREATE_NULL_AS_BLANK).setCellType(Cell.CELL_TYPE_STRING);
								row.getCell(SUBJECT, Row.CREATE_NULL_AS_BLANK).setCellType(Cell.CELL_TYPE_STRING);
		
								row.getCell(HAS_TITLE, Row.CREATE_NULL_AS_BLANK).setCellType(Cell.CELL_TYPE_STRING); 
								row.getCell(HAS_SOP, Row.CREATE_NULL_AS_BLANK).setCellType(Cell.CELL_TYPE_STRING);
								row.getCell(HAS_VIVA, Row.CREATE_NULL_AS_BLANK).setCellType(Cell.CELL_TYPE_STRING);
								row.getCell(HAS_SYNOPSIS, Row.CREATE_NULL_AS_BLANK).setCellType(Cell.CELL_TYPE_STRING);
								row.getCell(HAS_SUBMISSION, Row.CREATE_NULL_AS_BLANK).setCellType(Cell.CELL_TYPE_STRING); 
								
								String programCode = row.getCell(PROGRAM, Row.CREATE_NULL_AS_BLANK).getStringCellValue();
								String programStructure = row.getCell(PROGRAM_STRUCTURE, Row.CREATE_NULL_AS_BLANK).getStringCellValue();
								String subject = row.getCell(SUBJECT, Row.CREATE_NULL_AS_BLANK).getStringCellValue();
								
								String hasTitle = row.getCell(HAS_TITLE, Row.CREATE_NULL_AS_BLANK).getStringCellValue();
								String hasSOP = row.getCell(HAS_SOP, Row.CREATE_NULL_AS_BLANK).getStringCellValue();
								String hasViva = row.getCell(HAS_VIVA, Row.CREATE_NULL_AS_BLANK).getStringCellValue();
								String hasSynopsis = row.getCell(HAS_SYNOPSIS, Row.CREATE_NULL_AS_BLANK).getStringCellValue();
								String hasSubmission = row.getCell(HAS_SUBMISSION, Row.CREATE_NULL_AS_BLANK).getStringCellValue();
								
								String consumerType = inputBean.getConsumerType();
								String examYear = inputBean.getExamYear();
								String examMonth = inputBean.getExamMonth();
								
								ProjectConfiguration bean = new ProjectConfiguration();
								
								bean.setCreatedBy(inputBean.getCreatedBy());
								bean.setUpdatedBy(inputBean.getUpdatedBy());
								
								bean.setExamMonth(examMonth);
								bean.setExamYear(examYear);
								
								bean.setSubject(subject);
								
								bean.setConsumerType(consumerType);
								bean.setProgramCode(programCode);;
								bean.setProgramStructure(programStructure);
								
								bean.setHasSOP(hasSOP);
								bean.setHasSubmission(hasSubmission);
								bean.setHasSynopsis(hasSynopsis);
								bean.setHasTitle(hasTitle);
								bean.setHasViva(hasViva);
								
								list.add(bean);
							}catch (Exception e) {
								String error = e.getMessage();
		
								ProjectConfiguration bean = new ProjectConfiguration();
								bean.setError("Error while mapping record : " + error + " Row : " + (i - 1));
								list.add(bean);
							}
						}
					}
					return list;
				} catch (Exception e) {
					
					return list;
				}
		}
	
	//public void buildExcelDocumentForAssgEvaluationReport(List<AssignmentFileBean> allSubmittedAssignmentList,HttpServletResponse response,AssignmentsDAO dao ) throws Exception 
	public void buildExcelDocumentForAssgEvaluationReport(List<AssignmentFileBean> allSubmittedAssignmentList,HttpServletRequest request,HttpServletResponse response,
			String ASSIGNMENT_REPORTS_GENERATE_PATH,AssignmentsDAO dao) throws IOException { 

		HashMap<String, StudentExamBean> sapIdStudentsMap = dao.getAllStudents();
		HashMap<String, CenterExamBean> icLcMap = dao.getICLCMap();
		//create a worksheet
		XSSFWorkbook workbook =new XSSFWorkbook();
		XSSFSheet sheet = workbook.createSheet("Evaluation Report");
		int index = 0;
		XSSFRow header = sheet.createRow(0);
		header.createCell(index++).setCellValue("Sr. No.");
		header.createCell(index++).setCellValue("Exam Year"); 
		header.createCell(index++).setCellValue("Exam Month");
		header.createCell(index++).setCellValue("subject");
		header.createCell(index++).setCellValue("StudentId");
		header.createCell(index++).setCellValue("StudentName");
		header.createCell(index++).setCellValue("program");
		header.createCell(index++).setCellValue("faculty1");
		header.createCell(index++).setCellValue("facultyName");
		header.createCell(index++).setCellValue("faculty1Evaluated");
		header.createCell(index++).setCellValue("faculty1Score"); 
		header.createCell(index++).setCellValue("faculty1Remarks");
		header.createCell(index++).setCellValue("faculty1Reason");
		header.createCell(index++).setCellValue("faculty2");
		header.createCell(index++).setCellValue("faculty2Evaluated");
		header.createCell(index++).setCellValue("faculty2Score");
		header.createCell(index++).setCellValue("faculty2Remarks");
		header.createCell(index++).setCellValue("faculty2Reason");
		header.createCell(index++).setCellValue("faculty3");
		header.createCell(index++).setCellValue("faculty3Evaluated");
		header.createCell(index++).setCellValue("faculty3Score");
		header.createCell(index++).setCellValue("faculty3Remarks"); 
		header.createCell(index++).setCellValue("faculty3Reason");
		header.createCell(index++).setCellValue("OptedForReval");
		header.createCell(index++).setCellValue("revaluationScore");
		header.createCell(index++).setCellValue("revaluationRemarks");
		header.createCell(index++).setCellValue("revaluationReason");
		header.createCell(index++).setCellValue("q1Marks");
		header.createCell(index++).setCellValue("q1Remarks");
		header.createCell(index++).setCellValue("q2Marks");
		header.createCell(index++).setCellValue("q2Remarks");
		header.createCell(index++).setCellValue("q3Marks");
		header.createCell(index++).setCellValue("q3Remarks");
		header.createCell(index++).setCellValue("q4Marks");
		header.createCell(index++).setCellValue("q4Remarks");
		header.createCell(index++).setCellValue("q5Marks");
		header.createCell(index++).setCellValue("q5Remarks");
		header.createCell(index++).setCellValue("q6Marks");
		header.createCell(index++).setCellValue("q6Remarks");
		header.createCell(index++).setCellValue("q7Marks");
		header.createCell(index++).setCellValue("q7Remarks");
		header.createCell(index++).setCellValue("q8Marks");
		header.createCell(index++).setCellValue("q8Remarks");
		header.createCell(index++).setCellValue("q9Marks");
		header.createCell(index++).setCellValue("q9Remarks");
		header.createCell(index++).setCellValue("q10Marks");
		header.createCell(index++).setCellValue("q10Remarks");
				 
		int rowNum = 1;
		for (int i = 0 ; i < allSubmittedAssignmentList.size(); i++) {
			index = 0;
			//create the row data
			XSSFRow row = sheet.createRow(rowNum++);
			AssignmentFileBean bean = allSubmittedAssignmentList.get(i);
			
			StudentExamBean student = sapIdStudentsMap.get(bean.getSapId());
			 
			row.createCell(index++).setCellValue(rowNum-1); 
			row.createCell(index++).setCellValue(bean.getYear());
			row.createCell(index++).setCellValue(bean.getMonth());
			row.createCell(index++).setCellValue(bean.getSubject());
			row.createCell(index++).setCellValue(bean.getSapId()); 
			row.createCell(index++).setCellValue(bean.getStudentName());
			row.createCell(index++).setCellValue(bean.getProgram());
			row.createCell(index++).setCellValue(bean.getFaculty1());
			row.createCell(index++).setCellValue(bean.getFacultyName());
			row.createCell(index++).setCellValue(bean.getFaculty1Evaluated());
			row.createCell(index++).setCellValue(bean.getFaculty1Score());
			row.createCell(index++).setCellValue(bean.getFaculty1Remarks());
			row.createCell(index++).setCellValue(bean.getFaculty1Reason());
			row.createCell(index++).setCellValue(bean.getFaculty2()); 
			row.createCell(index++).setCellValue(bean.getFaculty2Evaluated());
			row.createCell(index++).setCellValue(bean.getFaculty2Score());
			row.createCell(index++).setCellValue(bean.getFaculty2Remarks());
			row.createCell(index++).setCellValue(bean.getFaculty2Reason());
			row.createCell(index++).setCellValue(bean.getFaculty3());
			row.createCell(index++).setCellValue(bean.getFaculty3Evaluated());
			row.createCell(index++).setCellValue(bean.getFaculty3Score());
			row.createCell(index++).setCellValue(bean.getFaculty3Remarks());
			row.createCell(index++).setCellValue(bean.getFaculty3Reason());
			row.createCell(index++).setCellValue(bean.getOptedForReval());
			row.createCell(index++).setCellValue(bean.getRevaluationScore());
			row.createCell(index++).setCellValue(bean.getRevaluationRemarks());
			row.createCell(index++).setCellValue(bean.getRevaluationReason());
			row.createCell(index++).setCellValue(bean.getQ1Marks());
			row.createCell(index++).setCellValue(bean.getQ1Remarks());
			row.createCell(index++).setCellValue(bean.getQ2Marks());
			row.createCell(index++).setCellValue(bean.getQ2Remarks());
			row.createCell(index++).setCellValue(bean.getQ3Marks());
			row.createCell(index++).setCellValue(bean.getQ3Remarks());
			row.createCell(index++).setCellValue(bean.getQ4Marks());
			row.createCell(index++).setCellValue(bean.getQ4Remarks());
			row.createCell(index++).setCellValue(bean.getQ5Marks());
			row.createCell(index++).setCellValue(bean.getQ5Remarks());
			row.createCell(index++).setCellValue(bean.getQ6Marks());
			row.createCell(index++).setCellValue(bean.getQ6Remarks());
			row.createCell(index++).setCellValue(bean.getQ7Marks());
			row.createCell(index++).setCellValue(bean.getQ7Remarks());
			row.createCell(index++).setCellValue(bean.getQ8Marks());
			row.createCell(index++).setCellValue(bean.getQ8Remarks());
			row.createCell(index++).setCellValue(bean.getQ9Marks());
			row.createCell(index++).setCellValue(bean.getQ9Remarks());
			row.createCell(index++).setCellValue(bean.getQ10Marks());
			row.createCell(index++).setCellValue(bean.getQ10Remarks());


			
		}
		
		//Written to write excel file to outputStream and flush it out of controller itself//
		/*
		 * ByteArrayOutputStream outByteStream = new ByteArrayOutputStream();
		 * workbook.write(outByteStream); byte [] outArray =
		 * outByteStream.toByteArray(); response.setContentType("application/ms-excel");
		 * response.setContentLength(outArray.length); response.setHeader("Expires:",
		 * "0"); // eliminates browser caching response.setHeader("Content-Disposition",
		 * "attachment; filename=EvaluationReport.xls"); ServletOutputStream outStream =
		 * response.getOutputStream(); outStream.write(outArray); outStream.flush();
		 * //End//
		 */	
		try {
			//writing data in a file and saving
			String fileName = "EvaluationReport.xlsx";
				String filePath = ASSIGNMENT_REPORTS_GENERATE_PATH + "Bifurcation"+ "/" +fileName;
				FileOutputStream fileOut = new FileOutputStream(filePath);
				workbook.write(fileOut);
			    fileOut.close();
				//started file download
				File downloadFile = new File(filePath);
				FileInputStream inputStream = new FileInputStream(downloadFile);
				ServletContext context = request.getSession().getServletContext();
				
				// get MIME type of the file
				String mimeType = context.getMimeType(filePath);
				if (mimeType == null) {
					// set to binary type if MIME mapping not found
					mimeType = "application/octet-stream";
				}

				// set content attributes for the response
				response.setContentType(mimeType);
				response.setContentLength((int) downloadFile.length());

				// set headers for the response
				String headerKey = "Content-Disposition";
				String headerValue = String.format("attachment; filename=\"%s\"",
						downloadFile.getName());
				response.setHeader(headerKey, headerValue);

				// get output stream of the response
				OutputStream outStream = response.getOutputStream();
				int BUFFER_SIZE = 4096;
				byte[] buffer = new byte[BUFFER_SIZE];
				int bytesRead = -1;

				// write bytes read from the input stream into the output stream
				while ((bytesRead = inputStream.read(buffer)) != -1) {
					outStream.write(buffer, 0, bytesRead);
				}
				inputStream.close();
				outStream.close();
		} catch (Exception e) {
		}
	}
	public ArrayList<List> readAssignmentQpExcel(AssignmentFilesSetbean fileBean){ 
		
		int  EXAMYEAR_INDEX = 0;
		int  EXAMMONTH_INDEX = 1;
		int  STARTDATE_INDEX = 2;
		int  ENDDATE_INDEX = 3;
		int  CONSUMERTYPE = 4;
		int  PROGRAMSTRUCTURE = 5;
		int  PROGRAM = 6;
		int  SUBJECT = 7;
		int  FACULTYID_INDEX = 8;
		int  REVIEWER_INDEX = 9;
		int  DUEDATE_INDEX = 10;
		ByteArrayInputStream bis = new ByteArrayInputStream(fileBean.getFileData().getBytes());
		Workbook workbook;
		List<AssignmentFilesSetbean> beanList = new ArrayList<AssignmentFilesSetbean>();
		List<AssignmentFilesSetbean> errorBeanList = new ArrayList<AssignmentFilesSetbean>();
		ArrayList<List> resultList = new ArrayList<>();
		HashMap<String, String> sapIdSujectKeys = new HashMap<>();

		try {
			if (fileBean.getFileData().getOriginalFilename().endsWith("xls")) {
				workbook = new HSSFWorkbook(bis);
			} else if (fileBean.getFileData().getOriginalFilename().endsWith("xlsx")) {
				workbook = new XSSFWorkbook(bis);
			} else {
				throw new IllegalArgumentException("Received file does not have a standard excel extension.");
			}

			//Get first/desired sheet from the workbook
			XSSFSheet sheet = (XSSFSheet) workbook.getSheetAt(0);

			Iterator<Row> rowIterator = sheet.iterator();

			int i = 0;
			//Skip first row since it contains column names, not data.
			if(rowIterator.hasNext()){
				Row row = rowIterator.next();
			}
			while(rowIterator.hasNext()) {
				i++;
				Row row = rowIterator.next();

				if(row!=null){
					row.getCell(EXAMYEAR_INDEX, Row.CREATE_NULL_AS_BLANK).setCellType(Cell.CELL_TYPE_STRING);
					row.getCell(EXAMMONTH_INDEX, Row.CREATE_NULL_AS_BLANK).setCellType(Cell.CELL_TYPE_STRING);
					row.getCell(STARTDATE_INDEX, Row.CREATE_NULL_AS_BLANK).setCellType(Cell.CELL_TYPE_STRING);
					row.getCell(ENDDATE_INDEX, Row.CREATE_NULL_AS_BLANK).setCellType(Cell.CELL_TYPE_STRING);
					row.getCell(CONSUMERTYPE, Row.CREATE_NULL_AS_BLANK).setCellType(Cell.CELL_TYPE_STRING);
					row.getCell(PROGRAMSTRUCTURE, Row.CREATE_NULL_AS_BLANK).setCellType(Cell.CELL_TYPE_STRING);
					row.getCell(PROGRAM, Row.CREATE_NULL_AS_BLANK).setCellType(Cell.CELL_TYPE_STRING);
					row.getCell(SUBJECT, Row.CREATE_NULL_AS_BLANK).setCellType(Cell.CELL_TYPE_STRING);
					row.getCell(FACULTYID_INDEX, Row.CREATE_NULL_AS_BLANK).setCellType(Cell.CELL_TYPE_STRING);
					row.getCell(REVIEWER_INDEX, Row.CREATE_NULL_AS_BLANK).setCellType(Cell.CELL_TYPE_STRING);
					row.getCell(DUEDATE_INDEX, Row.CREATE_NULL_AS_BLANK).setCellType(Cell.CELL_TYPE_STRING);
					
					AssignmentFilesSetbean bean = new AssignmentFilesSetbean();

					String examYear = row.getCell(EXAMYEAR_INDEX, Row.CREATE_NULL_AS_BLANK).getStringCellValue();
					String examMonth = row.getCell(EXAMMONTH_INDEX, Row.CREATE_NULL_AS_BLANK).getStringCellValue();
					String startDate = row.getCell(STARTDATE_INDEX, Row.CREATE_NULL_AS_BLANK).getStringCellValue();
					String endDate = row.getCell(ENDDATE_INDEX, Row.CREATE_NULL_AS_BLANK).getStringCellValue();
					String consumerType = row.getCell(CONSUMERTYPE, Row.CREATE_NULL_AS_BLANK).getStringCellValue();
					String programStructure = row.getCell(PROGRAMSTRUCTURE, Row.CREATE_NULL_AS_BLANK).getStringCellValue();
					String program = row.getCell(PROGRAM, Row.CREATE_NULL_AS_BLANK).getStringCellValue();
					String subject = row.getCell(SUBJECT, Row.CREATE_NULL_AS_BLANK).getStringCellValue();
					String facultyId = row.getCell(FACULTYID_INDEX, Row.CREATE_NULL_AS_BLANK).getStringCellValue();
					String reviewerId = row.getCell(REVIEWER_INDEX, Row.CREATE_NULL_AS_BLANK).getStringCellValue();
					String dueDate = row.getCell(DUEDATE_INDEX, Row.CREATE_NULL_AS_BLANK).getStringCellValue();
					
					if("".equals(facultyId.trim()) && "".equals(facultyId.trim())){
						break;
					}
					bean.setExamYear(examYear);
					bean.setExamMonth(examMonth);
					bean.setStartDate(startDate);
					bean.setEndDate(endDate);
					bean.setConsumerType(consumerType);
					bean.setProgramStructure(programStructure);
					bean.setProgram(program);
					bean.setSubject(subject);
					bean.setFacultyId(facultyId);
					bean.setReviewer(reviewerId);
					bean.setDueDate(dueDate);

					if(bean.isErrorRecord()){
						errorBeanList.add(bean);
					}else{
						beanList.add(bean);
					}

					if(i % 1000 == 0){
					}

				}
			}
		} catch (IOException e) {
			
		}
		resultList.add(beanList);
		resultList.add(errorBeanList);
		return resultList;
	}
	
	/**
	 * This method is used used to read uploaded project marks excel file data and prepare list of bean.
	 * @param teeResultBean - Bean contains selected values of form and uploaded excel.
	 * @param errorList - empty error list to store error message occurred while reading excel file..
	 * @return list of prepared project marks bean
	 */
	public List<TEEResultBean> getMarksToUpsertForProject(TEEResultBean teeResultBean, List<String> errorList) {
		timeboundProjectMarksUploadLogger.info("ExcelHelper.getMarksToUpsertForProject() - START");
		int SAP_ID = 0;
		int SIMULATIONSTATUS =1;
		int SIMULATIONSCORE=2;
		int SIMULATIONMAXSCORE=3;
		int COMPXMSTATUS =4;
		int COMPXMSCORE=5;
		int COMPXMMAXSCORE=6;
		//Create empty list to store valid project marks record.
		List<TEEResultBean> teeResultBeanList = new ArrayList<TEEResultBean>();
		//return an error if excel file not uploaded.
		if(teeResultBean.getFileData().isEmpty()) {
			errorList.add("No excel file found, please upload excel file!");
			return teeResultBeanList;
		}
		
		//Get file data in byte array stream.
		ByteArrayInputStream bis = new ByteArrayInputStream(teeResultBean.getFileData().getBytes());
		Workbook workbook;
		try {
			//Check the uploaded excel file is having standard excel extension else throw exception with error message
			if (teeResultBean.getFileData().getOriginalFilename().endsWith("xls")) {
				workbook = new HSSFWorkbook(bis);
			} else if (teeResultBean.getFileData().getOriginalFilename().endsWith("xlsx")) {
				workbook = new XSSFWorkbook(bis);
			} else {
				errorList.add("Received file does not have a standard excel extension.");
				return teeResultBeanList;
			}
			
			XSSFSheet sheet = (XSSFSheet) workbook.getSheetAt(0);
			Iterator<Row> rowIterator = sheet.iterator();
			
			if(rowIterator.hasNext()){
				rowIterator.next();
			}
			// rowCount for maintaining the row number for which data not supplied as expected.
			int rowCount=2;
			while(rowIterator.hasNext()) {
				try {
					Row row = rowIterator.next();
					if(!checkIfRowIsEmpty(row)){
						TEEResultBean resultBean = new TEEResultBean();
						//Create a blank cell of string type.
						row.getCell(SAP_ID, Row.CREATE_NULL_AS_BLANK).setCellType(Cell.CELL_TYPE_STRING);
						row.getCell(SIMULATIONSTATUS,Row.CREATE_NULL_AS_BLANK).setCellType(Cell.CELL_TYPE_STRING);;
						row.getCell(SIMULATIONSCORE, Row.CREATE_NULL_AS_BLANK).setCellType(Cell.CELL_TYPE_STRING);
						row.getCell(SIMULATIONMAXSCORE, Row.CREATE_NULL_AS_BLANK).setCellType(Cell.CELL_TYPE_STRING);
						row.getCell(COMPXMSTATUS, Row.CREATE_NULL_AS_BLANK).setCellType(Cell.CELL_TYPE_STRING);
						row.getCell(COMPXMSCORE, Row.CREATE_NULL_AS_BLANK).setCellType(Cell.CELL_TYPE_STRING);
						row.getCell(COMPXMMAXSCORE, Row.CREATE_NULL_AS_BLANK).setCellType(Cell.CELL_TYPE_STRING);
						
						//Read cell values of a row.
						String SAPID = row.getCell(SAP_ID, Row.CREATE_NULL_AS_BLANK).getStringCellValue();
						String simulationStatus = row.getCell(SIMULATIONSTATUS,Row.CREATE_NULL_AS_BLANK).getStringCellValue();
						String simulationScore = row.getCell(SIMULATIONSCORE, Row.CREATE_NULL_AS_BLANK).getStringCellValue();
						String simulationMaxScore = row.getCell(SIMULATIONMAXSCORE, Row.CREATE_NULL_AS_BLANK).getStringCellValue();
						String compXMStatus = row.getCell(COMPXMSTATUS,Row.CREATE_NULL_AS_BLANK).getStringCellValue();
						String compXMScore = row.getCell(COMPXMSCORE, Row.CREATE_NULL_AS_BLANK).getStringCellValue();
						String compXMMaxScore = row.getCell(COMPXMMAXSCORE, Row.CREATE_NULL_AS_BLANK).getStringCellValue();
						
						//Check supplied sapId is valid else add error message to error list and continue reading next row.
						if(!StringUtility.isNumeric(SAPID)) {
							errorList.add("Student number(SAPId) should be numeric at row ("+ rowCount++ +")");
							continue;
						}
						//Check supplied simulationStatus and compXMStatus is valid else add error message to error list and continue reading next row.
						//simulationStatus and compXMStatus should contains - 'Attempted' or 'Not Attempted' status
						if(StringUtils.isBlank(simulationStatus) || StringUtils.isBlank(compXMStatus)) {
							errorList.add("Simulation Status or CompXM Status should not be empty at row ("+ rowCount++ +") for SAPID:"+SAPID);
							continue;
						}
						//Check supplied simulationScore,compXMScore,compXMMaxScore and simulationMaxScore is valid else add error message to error list and continue reading next row.
						//simulationScore and compXMScore should contain at list '0' score.
						if(!StringUtility.isNumeric(simulationScore) || !StringUtility.isNumeric(compXMScore)  
								 || !StringUtility.isNumeric(compXMMaxScore) || !StringUtility.isNumeric(simulationMaxScore) ) {
							errorList.add("Simulation Score and CompXM Score and Max Score's should be numeric at row ("+ rowCount++ +") for SAPID:"+SAPID);
							continue;
						}
						//Set sapId and project marks to bean.
						resultBean.setSapid(SAPID.trim());
						resultBean.setSimulation_status(simulationStatus.trim());
						resultBean.setSimulation_score(Float.parseFloat(simulationScore));
						resultBean.setSimulation_max_score(Integer.parseInt(simulationMaxScore));
						resultBean.setCompXM_status(compXMStatus.trim());
						resultBean.setCompXM_score(Float.parseFloat(compXMScore));
						resultBean.setCompXM_max_score(Integer.parseInt(compXMMaxScore));
						
						//Check supplied simulationStatus,compXMStatus should be 'Attempted' OR 'Not Attempted' or else add error message to error list and continue reading next row.
						if(!("Attempted".equals(resultBean.getSimulation_status())) && !("Not Attempted".equals(resultBean.getSimulation_status()))) {
							errorList.add("Simulation status should be 'Attempted' OR 'Not Attempted' at row ("+ rowCount++ +") for SAPID:"+SAPID);
							continue;
						}
						if(!("Attempted".equals(resultBean.getCompXM_status())) && !("Not Attempted".equals(resultBean.getCompXM_status()))) {
							errorList.add("CompXM status should be 'Attempted' OR 'Not Attempted' at row ("+ rowCount++ +") for SAPID:"+SAPID);
							continue;
						}
						
						//Check supplied simulationScore,compXMScore is less than Simulation Max Score and CompXM Max Score or else add error message to error list and continue reading next row.
						//Simulation Score and CompXM Score should not be grater than Max Score's
						if(resultBean.getSimulation_score()>resultBean.getSimulation_max_score() || resultBean.getCompXM_score()>resultBean.getCompXM_max_score()) {
							errorList.add("Simulation Score and CompXM Score should not be grater than Max Score's at row ("+ rowCount++ +") for SAPID:"+SAPID);
							continue;
						}
						
						//Get student details based on the sapId to set student name in the project marks bean.
						StudentExamBean student = examsAssessmentsDAO.getSingleStudentsData(resultBean.getSapid());
						
						resultBean.setStudent_name(student.getFirstName() + " " + student.getLastName());
						resultBean.setCreatedBy(teeResultBean.getCreatedBy());
						resultBean.setLastModifiedBy(teeResultBean.getLastModifiedBy());
						resultBean.setTimebound_id(teeResultBean.getTimebound_id());
						//add project marks bean to list.
						teeResultBeanList.add(resultBean);
						rowCount++;
					}//if block
				}catch (Exception e) {
					timeboundProjectMarksUploadLogger.error("Exception occured while processing row ("+ rowCount +") with error as :"+e.getStackTrace());
					//Add error message to the list along with the index/row number for which it occurred.
					errorList.add("Exception occured while processing row ("+ rowCount++ +") with error as :"+e.getMessage());
				}//catch block
			}//while loop
			if(errorList.isEmpty() && teeResultBeanList.size()==0)
				errorList.add("No data found in excel, Please check uploaded excel file!");
			
		} catch (Exception e) {
			timeboundProjectMarksUploadLogger.error("Exception occured while reading excel sheet with error as:"+e.getStackTrace());
			//Add error to list
			errorList.add("Exception occured while reading excel sheet with error as :"+e.getMessage());
		}//catch block
		timeboundProjectMarksUploadLogger.info("ExcelHelper.getMarksToUpsertForProject() - END");
		//return list of project marks bean.
		return teeResultBeanList;
	}//getMarksToUpsertForProject(-,-)
	
	private boolean checkIfRowIsEmpty(Row row) {
	    if (row == null) {
	        return true;
	    }
	    if (row.getLastCellNum() <= 0) {
	        return true;
	    }
	    for (int cellNum = row.getFirstCellNum(); cellNum < row.getLastCellNum(); cellNum++) {
	        Cell cell = row.getCell(cellNum);
	        if (cell != null && cell.getCellType() != Cell.CELL_TYPE_BLANK && StringUtils.isNotBlank(cell.toString())) {
	            return false;
	        }
	    }
	    return true;
	}//checkIfRowIsEmpty(-)
	
	//Reading data from excel file
	public BlockStudentExamCenterBean readCenterNotAllowedExcel(MultipartFile file) throws IOException {
		
		int SAPID=0;
		
		List<BlockStudentExamCenterBean> studentList = new ArrayList<BlockStudentExamCenterBean>();
		List<BlockStudentExamCenterBean> errorList = new ArrayList<BlockStudentExamCenterBean>();
		BlockStudentExamCenterBean bean = new BlockStudentExamCenterBean();
		
		ByteArrayInputStream bis = new ByteArrayInputStream(file.getBytes());
		Workbook workbook;
		try {
			if (file.getOriginalFilename().endsWith("xls")) {
				workbook = new HSSFWorkbook(bis);
			} else if (file.getOriginalFilename().endsWith("xlsx")) {
				workbook = new XSSFWorkbook(bis);
			} else {
				throw new IllegalArgumentException("Received file does not have a standard excel extension.");
			}
			
			XSSFSheet sheet = (XSSFSheet) workbook.getSheetAt(0);
			Iterator<Row> rowIterator = sheet.iterator();
			int i = 1;
			//Skip first row since it contains column names, not data.
			if(rowIterator.hasNext()){
				Row row = rowIterator.next();
			}
			while(rowIterator.hasNext()){
				i++;
				Row row = rowIterator.next();
				BlockStudentExamCenterBean studentBean = new BlockStudentExamCenterBean();
				BlockStudentExamCenterBean errorBean = new BlockStudentExamCenterBean();
				try {
					
					if(row!=null){
						
						//setting type of cell to String
						
						row.getCell(SAPID, Row.CREATE_NULL_AS_BLANK).setCellType(Cell.CELL_TYPE_STRING);
						
						//Reading values from the cell
						
						String sapid = row.getCell(SAPID, Row.CREATE_NULL_AS_BLANK).getStringCellValue();
						
						if(StringUtil.isBlank(sapid)) {
							continue;
						}
						
						if(!StringUtil.isNumeric(sapid)) {
							throw new Exception("Student SAPID should be numeric, Please correct the sapid and try again!!");
						}
						
						studentBean.setSapid(sapid);
						studentBean.setRow(i);
						
						studentList.add(studentBean);
				} 
			} catch (Exception e) {
				errorBean.setErrorRecord(true);
				errorBean.setRow(i);
				errorBean.setErrorMessage(e.getMessage());
				errorList.add(errorBean);
				break;
				}
			}
		}
		catch (IllegalArgumentException ie) {
			logger.info(ie.getMessage()+" {}", ie);
			throw new IllegalArgumentException(ie.getMessage());
		}
		catch (Exception e) {
			logger.info("Failed to read data from excel file due to {} ", e);
		}
		bean.setStudentList(studentList);
		bean.setErrorList(errorList);
		return bean;
	}
	
	//To Read Uploaded COC Show Cause File
	public List<UFMNoticeBean> readShowCauseList(UFMNoticeBean inputBean) throws Exception {

		int SAPID = 0;
		int SUBJECT = 1;
		int EXAM_DATE = 2;
		int EXAM_TIME = 3;
		int UFM_REASON = 4;
		
		ByteArrayInputStream bis = new ByteArrayInputStream(inputBean.getFileData().getBytes());
		Workbook workbook;

		int currentRow = 0;
		
		try {
			if (inputBean.getFileData().getOriginalFilename().toLowerCase().endsWith("xls")) {
				workbook = new HSSFWorkbook(bis);
			} else if (inputBean.getFileData().getOriginalFilename().toLowerCase().endsWith("xlsx")) {
				workbook = new XSSFWorkbook(bis);
			} else {
				ufm.info("Received file does not have a standard excel extension.");
				throw new IllegalArgumentException("Received file does not have a standard excel extension.");
			}
			
			
			XSSFSheet sheet = (XSSFSheet) workbook.getSheetAt(0);
			Iterator<Row> rowIterator = sheet.iterator();
			
			if(rowIterator.hasNext()){
				rowIterator.next();
			}

			List<UFMNoticeBean> toReturn = new ArrayList<UFMNoticeBean>();
			
			while(rowIterator.hasNext()) {
				Row row = rowIterator.next();
				if(row!=null){
					UFMNoticeBean bean = new UFMNoticeBean();
					currentRow++;
					
					row.getCell(SAPID, Row.CREATE_NULL_AS_BLANK).setCellType(Cell.CELL_TYPE_STRING);
					String sapid = row.getCell(SAPID, Row.CREATE_NULL_AS_BLANK).getStringCellValue();
					
					// If sapid is empty, dont process row.
					if(StringUtils.isBlank(sapid)) {
						continue;
					}
					bean.setSapid(sapid);
					
					row.getCell(SUBJECT, Row.CREATE_NULL_AS_BLANK).setCellType(Cell.CELL_TYPE_STRING);
					String subject = row.getCell(SUBJECT, Row.CREATE_NULL_AS_BLANK).getStringCellValue();
					bean.setSubject(subject);
					
					Date examDate = null, examTime = null;
					String examDateStr, examTimeStr;
					Cell dateCell = row.getCell(EXAM_DATE, Row.CREATE_NULL_AS_BLANK);
					Cell startTimeCell = row.getCell(EXAM_TIME, Row.CREATE_NULL_AS_BLANK);
					

					if(dateCell.getCellType() == Cell.CELL_TYPE_NUMERIC){
						if (DateUtil.isCellDateFormatted(dateCell)) {
							examDate = dateCell.getDateCellValue();
						} else {
							examDate = HSSFDateUtil.getJavaDate(dateCell.getNumericCellValue());
						}
						SimpleDateFormat sdfDate = new SimpleDateFormat("yyyy-MM-dd");
						examDateStr = sdfDate.format(examDate);

					}else {
						dateCell.setCellType(Cell.CELL_TYPE_STRING);
						examDateStr = dateCell.getStringCellValue();
					}

					if(startTimeCell.getCellType() == Cell.CELL_TYPE_NUMERIC){
						if (DateUtil.isCellDateFormatted(startTimeCell)) {
							examTime = startTimeCell.getDateCellValue();
						} else {
							examTime = HSSFDateUtil.getJavaDate(startTimeCell.getNumericCellValue());
						}
						SimpleDateFormat sdfTime = new java.text.SimpleDateFormat("HH:mm:ss");
						 examTimeStr = sdfTime.format(examTime);

					}else {
						startTimeCell.setCellType(Cell.CELL_TYPE_STRING);
						examTimeStr = startTimeCell.getStringCellValue();
					}
					
					bean.setExamDate(examDateStr);
					bean.setExamTime(examTimeStr);
					
					row.getCell(UFM_REASON, Row.CREATE_NULL_AS_BLANK).setCellType(Cell.CELL_TYPE_STRING);
					String ufmMarkReason = row.getCell(UFM_REASON, Row.CREATE_NULL_AS_BLANK).getStringCellValue();
					
					bean.setUfmMarkReason(ufmMarkReason);
					bean.setYear(inputBean.getYear());
					bean.setMonth(inputBean.getMonth());
					bean.setShowCauseDeadline(inputBean.getShowCauseDeadline());
					bean.setStage(UFMNoticeBean.UFM_STAGE_SHOW_CAUSE_AWAITING_STUDENT_RESPONSE);
					bean.setCreatedBy(inputBean.getCreatedBy());
					bean.setLastModifiedBy(inputBean.getCreatedBy());
					bean.setCategory(inputBean.getCategory());
					
					toReturn.add(bean);
				}
			}
			return toReturn;
		} catch (Exception e) {
			//e.printStackTrace();
			ufm.info("Error Reading uploaded file at row " + currentRow + ".\nError : " + e.getMessage());
			throw new Exception("Error Reading uploaded file at row " + currentRow + ".\nError : " + e.getMessage());
		}
	}
	
	public List<UFMNoticeBean> readActionFileList(UFMNoticeBean inputBean) throws Exception {

		int SAPID = 0;
		int SUBJECT = 1;
		int EXAM_DATE = 2;
		int EXAM_TIME = 3;
		
		ByteArrayInputStream bis = new ByteArrayInputStream(inputBean.getFileData().getBytes());
		Workbook workbook;

		int currentRow = 0;
		
		try {
			if (inputBean.getFileData().getOriginalFilename().toLowerCase().endsWith("xls")) {
				workbook = new HSSFWorkbook(bis);
			} else if (inputBean.getFileData().getOriginalFilename().toLowerCase().endsWith("xlsx")) {
				workbook = new XSSFWorkbook(bis);
			} else {
				ufm.info("Received file does not have a standard excel extension.");
				throw new IllegalArgumentException("Received file does not have a standard excel extension.");
			}
			
			
			XSSFSheet sheet = (XSSFSheet) workbook.getSheetAt(0);
			Iterator<Row> rowIterator = sheet.iterator();
			
			if(rowIterator.hasNext()){
				rowIterator.next();
			}

			List<UFMNoticeBean> toReturn = new ArrayList<UFMNoticeBean>();
			
			while(rowIterator.hasNext()) {
				Row row = rowIterator.next();
				if(row!=null){
					UFMNoticeBean bean = new UFMNoticeBean();
					currentRow++;
					
					row.getCell(SAPID, Row.CREATE_NULL_AS_BLANK).setCellType(Cell.CELL_TYPE_STRING);
					String sapid = row.getCell(SAPID, Row.CREATE_NULL_AS_BLANK).getStringCellValue();
					
					// If sapid is empty, dont process row.
					if(StringUtils.isBlank(sapid)) {
						continue;
					}
					bean.setSapid(sapid);
					
					row.getCell(SUBJECT, Row.CREATE_NULL_AS_BLANK).setCellType(Cell.CELL_TYPE_STRING);
					String subject = row.getCell(SUBJECT, Row.CREATE_NULL_AS_BLANK).getStringCellValue();
					bean.setSubject(subject);
					
					Date examDate = null, examTime = null;
					String examDateStr, examTimeStr;
					Cell dateCell = row.getCell(EXAM_DATE, Row.CREATE_NULL_AS_BLANK);
					Cell startTimeCell = row.getCell(EXAM_TIME, Row.CREATE_NULL_AS_BLANK);
					

					if(dateCell.getCellType() == Cell.CELL_TYPE_NUMERIC){
						if (DateUtil.isCellDateFormatted(dateCell)) {
							examDate = dateCell.getDateCellValue();
						} else {
							examDate = HSSFDateUtil.getJavaDate(dateCell.getNumericCellValue());
						}
						SimpleDateFormat sdfDate = new SimpleDateFormat("yyyy-MM-dd");
						examDateStr = sdfDate.format(examDate);
					}else {
						dateCell.setCellType(Cell.CELL_TYPE_STRING);
						examDateStr = dateCell.getStringCellValue();
					}

					if(startTimeCell.getCellType() == Cell.CELL_TYPE_NUMERIC){
						if (DateUtil.isCellDateFormatted(startTimeCell)) {
							examTime = startTimeCell.getDateCellValue();
						} else {
							examTime = HSSFDateUtil.getJavaDate(startTimeCell.getNumericCellValue());
						}
						SimpleDateFormat sdfTime = new java.text.SimpleDateFormat("HH:mm:ss");
						 examTimeStr = sdfTime.format(examTime);

					}else {
						startTimeCell.setCellType(Cell.CELL_TYPE_STRING);
						examTimeStr = startTimeCell.getStringCellValue();
					}
					
					bean.setExamDate(examDateStr);
					bean.setExamTime(examTimeStr);
					bean.setYear(inputBean.getYear());
					bean.setMonth(inputBean.getMonth());
					bean.setStage(inputBean.getStage());
					bean.setCreatedBy(inputBean.getCreatedBy());
					bean.setLastModifiedBy(inputBean.getCreatedBy());
					bean.setCategory(inputBean.getCategory());
					
					toReturn.add(bean);
				}
			}
			return toReturn;
		} catch (Exception e) {
			ufm.info("Error Reading uploaded file at row " + currentRow + ".\nError : " + e.getMessage());
			throw new Exception("Error Reading uploaded file at row " + currentRow + ".\nError : " + e.getMessage());
		}
	}

	public List<String> readCCSubjectListFromExcel(final CommonsMultipartFile fileData) {
		int Subject = 0;
		ByteArrayInputStream b = new ByteArrayInputStream(fileData.getBytes());
		Workbook workBook;
		List<String> subjectList = new ArrayList<String>();
		try {
			if(fileData.getOriginalFilename().toLowerCase().endsWith("xls")) {
				workBook = new HSSFWorkbook(b);
			}else if(fileData.getOriginalFilename().endsWith("xlsx")) {
				workBook = new XSSFWorkbook(b);
			}else {
				throw new IllegalArgumentException("Received file does not have a standard excel extension.");
			}
			XSSFSheet sheet = (XSSFSheet) workBook.getSheetAt(0);
			Iterator<Row> rowIterator = sheet.iterator();
			int i=0;
			//Skip first row since it contains column names, not data.
			if(rowIterator.hasNext()) {
				Row row = rowIterator.next();
			}
			while(rowIterator.hasNext()) {
				i++;
				Row row = rowIterator.next();
				if(row != null) {
					try {
						row.getCell(Subject, Row.CREATE_NULL_AS_BLANK).setCellType(Cell.CELL_TYPE_STRING);
						String subject  = row.getCell(Subject, Row.CREATE_NULL_AS_BLANK).getStringCellValue();
						if(subject != null && !subject.isEmpty()) {
							subjectList.add(subject);
						}
					}catch (Exception e) {
						assignmentCopyCaseLogger.error("Exception Error in exporting subject from excel || SubjectList:{} || Error:{} ", subjectList, e);
					}
				}
			}
			return subjectList;
		}catch (Exception e) {
			assignmentCopyCaseLogger.error("Exception Error: getCCSubjectListFromExcel() "+e);
			return null;
		}
		
	}
	
	
	public List<String> readCCSubjectListFromExcelForCheckWebCC(final CommonsMultipartFile fileData) throws Exception{
		int Subject = 0;
		ByteArrayInputStream b = new ByteArrayInputStream(fileData.getBytes());
		Workbook workBook;
		List<String> subjectList = new ArrayList<String>();
		
			if(fileData.getOriginalFilename().toLowerCase().endsWith("xls")) {
				workBook = new HSSFWorkbook(b);
			}else if(fileData.getOriginalFilename().endsWith("xlsx")) {
				workBook = new XSSFWorkbook(b);
			}else {
				throw new IllegalArgumentException("Received file does not have a standard excel extension.");
			}
			XSSFSheet sheet = (XSSFSheet) workBook.getSheetAt(0);
			Iterator<Row> rowIterator = sheet.iterator();
			int i=0;
			//Skip first row since it contains column names, not data.
			if(rowIterator.hasNext()) {
				Row row = rowIterator.next();
			}
			while(rowIterator.hasNext()) {
				i++;
				Row row = rowIterator.next();
				if(row != null) {
					try {
						row.getCell(Subject, Row.CREATE_NULL_AS_BLANK).setCellType(Cell.CELL_TYPE_STRING);
						String subject  = row.getCell(Subject, Row.CREATE_NULL_AS_BLANK).getStringCellValue();
						if(subject != null && !subject.isEmpty()) {
							subjectList.add(subject);
						}
					}catch (Exception e) {
						assignmentCopyCaseLogger.error("Exception Error in exporting subject from excel || SubjectList:{} || Error:{} ", subjectList, e);
					}
				}
			}
			return subjectList;
	}
	
	
	
}
