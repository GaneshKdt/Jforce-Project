package com.nmims.helpers;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.WordUtils;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.poi.hssf.usermodel.HSSFDateUtil;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.DateUtil;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.nmims.beans.FacultyAvailabilityBean;
import com.nmims.beans.FacultyAcadsBean;
import com.nmims.beans.FacultyCourseBean;
import com.nmims.beans.FacultyCourseMappingBean;
import com.nmims.beans.FileAcadsBean;
import com.nmims.beans.ModuleContentAcadsBean;
import com.nmims.beans.SessionDayTimeAcadsBean;
import com.nmims.beans.SessionReviewBean;
import com.nmims.beans.SyllabusBean;
import com.nmims.beans.VideoContentAcadsBean;
import com.nmims.daos.TimeTableDAO;

@Component
public class ExcelHelper {

	@Autowired(required=false)
	ApplicationContext act;
	

	private ArrayList<String> yearList = new ArrayList<String>(Arrays.asList( 
			"2000","2001","2002","2003","2004","2005","2006","2007",
			"2008","2009","2010","2011","2012","2013","2014","2015","2016","2017","2018","2019","2020",
			"2021","2022","2023","2024","2025","2026","2027","2028","2029","2030")); 


	/*public ArrayList<List> readFacultyUnavailabilityExcel(FileBean fileBean, ArrayList<String> facultyList, String userId){

		int  FACULTY_ID_INDEX = 0;
		int  NON_AVAILABILITY_DATE_INDEX = 1;

		ByteArrayInputStream bis = new ByteArrayInputStream(fileBean.getFileData().getBytes());
		Workbook workbook;
		List<FacultyUnavailabilityBean> facultyDatesList = new ArrayList<FacultyUnavailabilityBean>();
		List<FacultyUnavailabilityBean> errorBeanList = new ArrayList<FacultyUnavailabilityBean>();
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


					row.getCell(FACULTY_ID_INDEX, Row.CREATE_NULL_AS_BLANK).setCellType(Cell.CELL_TYPE_STRING);
					FacultyUnavailabilityBean bean = new FacultyUnavailabilityBean();


					String facultyId = row.getCell(FACULTY_ID_INDEX, Row.CREATE_NULL_AS_BLANK).getStringCellValue();

					if("".equals(facultyId.trim()) ){
						break;
					}

					Date date = null;
					Cell dateCell = row.getCell(NON_AVAILABILITY_DATE_INDEX);


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


					if(bean.isErrorRecord()){
						errorBeanList.add(bean);
						continue;
					}

					SimpleDateFormat sdfDate = new SimpleDateFormat("yyyy-MM-dd");
					String unavailabilityDate = sdfDate.format(date);


					bean.setYear(fileBean.getYear());
					bean.setMonth(fileBean.getMonth());
					bean.setFacultyId(facultyId);
					bean.setUnavailabilityDate(unavailabilityDate);
					bean.setCreatedBy(userId);

					if(!facultyList.contains(bean.getFacultyId().trim())){
						bean.setErrorMessage(bean.getErrorMessage()+" Row : "+ (i+1) +" Invalid Faculty ID : "+bean.getFacultyId().trim());
						bean.setErrorRecord(true);
					}


					if(bean.isErrorRecord()){
						errorBeanList.add(bean);
					}else{
						facultyDatesList.add(bean);
					}


					if(i % 1000 == 0){
						System.out.println("Read "+i+" rows");
					}
				}
			}
			System.out.println("Total Records = "+(errorBeanList.size()+facultyDatesList.size()));
			System.out.println("Error Records = "+errorBeanList.size());
			System.out.println("Valid Records = "+facultyDatesList.size());
		} catch (IOException e) {
			  
		}
		resultList.add(facultyDatesList);
		resultList.add(errorBeanList);
		return resultList;
	}*/
	
	public ArrayList<List> readFacultyAvailabilityExcel(FileAcadsBean fileBean, ArrayList<String> facultyList, String userId){

		int  FACULTY_ID_INDEX = 0;
		int  AVAILABILITY_DATE_INDEX = 1;
		int  START_TIME_INDEX = 2;
		
		ByteArrayInputStream bis = new ByteArrayInputStream(fileBean.getFileData().getBytes());
		Workbook workbook;
		List<FacultyAvailabilityBean> facultyDatesList = new ArrayList<FacultyAvailabilityBean>();
		List<FacultyAvailabilityBean> errorBeanList = new ArrayList<FacultyAvailabilityBean>();
		ArrayList<List> resultList = new ArrayList<>();
		SimpleDateFormat sdfTime = new java.text.SimpleDateFormat("HH:mm");
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

			//Get first/desired sheet from the workbook
			

			

			int i = 0;
			//Skip first row since it contains column names, not data.
			if(rowIterator.hasNext()){
				Row row = rowIterator.next();
			}
			while(rowIterator.hasNext()) {
				i++;
				Row row = rowIterator.next();


				if(row!=null){


					row.getCell(FACULTY_ID_INDEX, Row.CREATE_NULL_AS_BLANK).setCellType(Cell.CELL_TYPE_STRING);
					FacultyAvailabilityBean bean = new FacultyAvailabilityBean();


					String facultyId = row.getCell(FACULTY_ID_INDEX, Row.CREATE_NULL_AS_BLANK).getStringCellValue();

					if("".equals(facultyId.trim()) ){
						break;
					}

					Date date = null;
					Cell dateCell = row.getCell(AVAILABILITY_DATE_INDEX);


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
					
					Date startTime = null;
					
					Cell startTimeCell = row.getCell(START_TIME_INDEX);
					
					if(startTimeCell.getCellType() == Cell.CELL_TYPE_NUMERIC){
						if (DateUtil.isCellDateFormatted(startTimeCell)) {
							startTime = startTimeCell.getDateCellValue();
						} else {
							startTime = HSSFDateUtil.getJavaDate(startTimeCell.getNumericCellValue());
						}
					}else{
						bean.setErrorMessage(bean.getErrorMessage()+" Row : "+ (i+1) +" Invalid Format Start time cell : Not a valid Time ");
						bean.setErrorRecord(true);
					}
					
					if(bean.isErrorRecord()){
						errorBeanList.add(bean);
						continue;
					}

					SimpleDateFormat sdfDate = new SimpleDateFormat("yyyy-MM-dd");
					String AvailabilityDate = sdfDate.format(date);


					bean.setYear(fileBean.getYear());
					bean.setMonth(fileBean.getMonth());
					bean.setFacultyId(facultyId);
					bean.setDate(AvailabilityDate);
					bean.setTime(sdfTime.format(startTime));
					bean.setCreatedBy(userId);

					if(!facultyList.contains(bean.getFacultyId().trim())){
						bean.setErrorMessage(bean.getErrorMessage()+" Row : "+ (i+1) +" Invalid Faculty ID : "+bean.getFacultyId().trim());
						bean.setErrorRecord(true);
					}


					if(bean.isErrorRecord()){
						errorBeanList.add(bean);
					}else{
						facultyDatesList.add(bean);
					}


					if(i % 1000 == 0){
//						System.out.println("Read "+i+" rows");
					}
				}
			}
//			System.out.println("Total Records = "+(errorBeanList.size()+facultyDatesList.size()));
//			System.out.println("Error Records = "+errorBeanList.size());
//			System.out.println("Valid Records = "+facultyDatesList.size());
		} catch (IOException e) {
			  
		}
		resultList.add(facultyDatesList);
		resultList.add(errorBeanList);
		return resultList;
	}
	
	public ArrayList<List> readFacultyAvailabilityExcelNew(FileAcadsBean fileBean, ArrayList<String> facultyList, String userId){

		int  FACULTY_ID_INDEX = 0;
		
		ByteArrayInputStream bis = new ByteArrayInputStream(fileBean.getFileData().getBytes());
		Workbook workbook;
		List<FacultyAvailabilityBean> facultyDatesList = new ArrayList<FacultyAvailabilityBean>();
		List<FacultyAvailabilityBean> errorBeanList = new ArrayList<FacultyAvailabilityBean>();
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

			//Get first/desired sheet from the workbook
			
			int i = 0;
			//Skip first row since it contains column names, not data.
			if(rowIterator.hasNext()){
				Row row = rowIterator.next();
			}
			while(rowIterator.hasNext()) {
				i++;
				Row row = rowIterator.next();

				if(row!=null) {

					row.getCell(FACULTY_ID_INDEX, Row.CREATE_NULL_AS_BLANK).setCellType(Cell.CELL_TYPE_STRING);
					FacultyAvailabilityBean bean = new FacultyAvailabilityBean();
					String facultyId = row.getCell(FACULTY_ID_INDEX, Row.CREATE_NULL_AS_BLANK).getStringCellValue();

					if("".equals(facultyId.trim()) ){
						break;
					}
					
					bean.setYear(fileBean.getYear());
					bean.setMonth(fileBean.getMonth());
					bean.setFacultyId(facultyId);
					bean.setCreatedBy(userId);

					if(!facultyList.contains(bean.getFacultyId().trim())){
						bean.setErrorMessage(bean.getErrorMessage()+" Row : "+ (i+1) +" Invalid Faculty ID : "+bean.getFacultyId().trim());
						bean.setErrorRecord(true);
					}

					if(bean.isErrorRecord()){
						errorBeanList.add(bean);
					}else{
						facultyDatesList.add(bean);
					}

					if(i % 1000 == 0){
//						System.out.println("Read "+i+" rows");
					}
				}
			}
//			System.out.println("Total Records = "+(errorBeanList.size()+facultyDatesList.size()));
//			System.out.println("Error Records = "+errorBeanList.size());
//			System.out.println("Valid Records = "+facultyDatesList.size());
		} catch (IOException e) {
			  
		}
		resultList.add(facultyDatesList);
		resultList.add(errorBeanList);
		return resultList;
	}

	public ArrayList<List> readFacultyCourseAccessExcel(FileAcadsBean fileBean, ArrayList<String> subjectList, ArrayList<String> facultyList, String userId,Map<String, String> subjectcodeMap,ArrayList<String> subjectCodeList){

		int  FACULTY_ID_INDEX = 0;
		int  SUBJECT_INDEX = 1;
		int SUBJECT_CODE_INDEX = 2;
		ByteArrayInputStream bis = new ByteArrayInputStream(fileBean.getFileData().getBytes());
		Workbook workbook;
		List<FacultyCourseBean> facultyCourseList = new ArrayList<FacultyCourseBean>();
		List<FacultyCourseBean> errorBeanList = new ArrayList<FacultyCourseBean>();
		ArrayList<List> resultList = new ArrayList<>();
		Sheet sheet = null;

		try {
			if (fileBean.getFileData().getOriginalFilename().endsWith("xls")) {
				workbook = new HSSFWorkbook(bis);
				//Get first/desired sheet from the workbook
				sheet = (HSSFSheet) workbook.getSheetAt(0);
			} else if (fileBean.getFileData().getOriginalFilename().endsWith("xlsx")) {
				workbook = new XSSFWorkbook(bis);
				//Get first/desired sheet from the workbook
				sheet = (XSSFSheet) workbook.getSheetAt(0);
			} else {
				throw new IllegalArgumentException("Received file does not have a standard excel extension.");
			}

			

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
					row.getCell(FACULTY_ID_INDEX, Row.CREATE_NULL_AS_BLANK).setCellType(Cell.CELL_TYPE_STRING);
					FacultyCourseBean bean = new FacultyCourseBean();

					String facultyId = row.getCell(FACULTY_ID_INDEX, Row.CREATE_NULL_AS_BLANK).getStringCellValue();
					String subject = row.getCell(SUBJECT_INDEX, Row.CREATE_NULL_AS_BLANK).getStringCellValue();
					String subjectCode = row.getCell(SUBJECT_CODE_INDEX, Row.CREATE_NULL_AS_BLANK).getStringCellValue();
					if("".equals(facultyId.trim()) && "".equals(subject.trim()) ){
						break;
					}

					if("".equals(subjectCode.trim())){
						break;
					}
					
					if(bean.isErrorRecord()){
						errorBeanList.add(bean);
						continue;
					}

					bean.setYear(fileBean.getYear());
					bean.setMonth(fileBean.getMonth());
					bean.setFacultyId(facultyId.trim());
					bean.setSubject(subject);
					bean.setCreatedBy(userId);
					bean.setSubjectcode(subjectCode);
					if(!subjectList.contains(bean.getSubject().trim())){
						bean.setErrorMessage(bean.getErrorMessage()+" Row : "+ (i+1) +" Invalid Subject : "+bean.getSubject().trim());
						bean.setErrorRecord(true);
					}
					if(!facultyList.contains(bean.getFacultyId().trim())){
						bean.setErrorMessage(bean.getErrorMessage()+" Row : "+ (i+1) +" Invalid Faculty ID : "+bean.getFacultyId().trim());
						bean.setErrorRecord(true);
					}
					
					//check if correct SubjectCodeId start
					if(!subjectCodeList.contains(subjectCode.trim())){
						
						bean.setErrorMessage(bean.getErrorMessage()+" Row : "+ (i+1) +" Invalid Subject Code Id "+subjectCode+" for Subject "+subject);
						bean.setErrorRecord(true);
					}else
					{
						if(!(subjectcodeMap.get(subjectCode.trim()).equals(subject.trim())))
						{
							bean.setErrorMessage(bean.getErrorMessage()+" Row : "+ (i+1) +" Invalid Subject And Subject Code Id doesnt match with each other. ["+subject+" - "+subjectCode+"]");
							bean.setErrorRecord(true);
						}
					}
					
					//check if correct SubjectCodeId End
				

					
					if(bean.isErrorRecord()){
						errorBeanList.add(bean);
					}else{
						facultyCourseList.add(bean);
					}

					if(i % 1000 == 0){
//						System.out.println("Read "+i+" rows");
					}
				}
			}
//			System.out.println("Total Records = "+(errorBeanList.size()+facultyCourseList.size()));
//			System.out.println("Error Records = "+errorBeanList.size());
//			System.out.println("Valid Records = "+facultyCourseList.size());
		} catch (IOException e) {
			  
		}
		resultList.add(facultyCourseList);
		resultList.add(errorBeanList);
		return resultList;
	}


	public ArrayList<List> readFacultyCourseMappingExcel(FileAcadsBean fileBean, ArrayList<String> facultyList, ArrayList<String> subjectList, String userId){

		int  SUBJECT_INDEX = 0;
		int  FACULTY_PREF_1_INDEX = 1;
		int  FACULTY_PREF_2_INDEX = 2;
		int  FACULTY_PREF_3_INDEX = 3;
		int  SESSION_INDEX = 4;
		int  DURATION_INDEX = 5;
		int  ADDITIONAL_FLAG_INDEX = 6;



		ByteArrayInputStream bis = new ByteArrayInputStream(fileBean.getFileData().getBytes());
		Workbook workbook;
		List<FacultyCourseMappingBean> facultyCourseMapingList = new ArrayList<FacultyCourseMappingBean>();
		List<FacultyCourseMappingBean> errorBeanList = new ArrayList<FacultyCourseMappingBean>();
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


					row.getCell(SUBJECT_INDEX, Row.CREATE_NULL_AS_BLANK).setCellType(Cell.CELL_TYPE_STRING);
					row.getCell(FACULTY_PREF_1_INDEX, Row.CREATE_NULL_AS_BLANK).setCellType(Cell.CELL_TYPE_STRING);
					row.getCell(FACULTY_PREF_2_INDEX, Row.CREATE_NULL_AS_BLANK).setCellType(Cell.CELL_TYPE_STRING);
					row.getCell(FACULTY_PREF_3_INDEX, Row.CREATE_NULL_AS_BLANK).setCellType(Cell.CELL_TYPE_STRING);
					row.getCell(SESSION_INDEX, Row.CREATE_NULL_AS_BLANK).setCellType(Cell.CELL_TYPE_STRING);
					row.getCell(DURATION_INDEX, Row.CREATE_NULL_AS_BLANK).setCellType(Cell.CELL_TYPE_STRING);
					row.getCell(ADDITIONAL_FLAG_INDEX, Row.CREATE_NULL_AS_BLANK).setCellType(Cell.CELL_TYPE_STRING);

					FacultyCourseMappingBean bean = new FacultyCourseMappingBean();



					String subject = row.getCell(SUBJECT_INDEX, Row.CREATE_NULL_AS_BLANK).getStringCellValue();
					String facultyIdPref1 = row.getCell(FACULTY_PREF_1_INDEX, Row.CREATE_NULL_AS_BLANK).getStringCellValue();
					String facultyIdPref2 = row.getCell(FACULTY_PREF_2_INDEX, Row.CREATE_NULL_AS_BLANK).getStringCellValue();
					String facultyIdPref3 = row.getCell(FACULTY_PREF_3_INDEX, Row.CREATE_NULL_AS_BLANK).getStringCellValue();
					String session = row.getCell(SESSION_INDEX, Row.CREATE_NULL_AS_BLANK).getStringCellValue();
					String duration = row.getCell(DURATION_INDEX, Row.CREATE_NULL_AS_BLANK).getStringCellValue();
					String isAdditionalSession = row.getCell(ADDITIONAL_FLAG_INDEX, Row.CREATE_NULL_AS_BLANK).getStringCellValue();

					if("".equals(subject.trim()) && "".equals(facultyIdPref1.trim()) && "".equals(facultyIdPref2) && "".equals(facultyIdPref3)){
						break;
					}


					bean.setYear(fileBean.getYear());
					bean.setMonth(fileBean.getMonth());
					bean.setSubject(subject);
					bean.setFacultyIdPref1(facultyIdPref1.trim());
					bean.setFacultyIdPref2(facultyIdPref2.trim());
					bean.setFacultyIdPref3(facultyIdPref3.trim());
					bean.setSession(session);
					bean.setDuration(duration);
					bean.setIsAdditionalSession(isAdditionalSession);
					bean.setCreatedBy(userId);

					if(!facultyList.contains(facultyIdPref1.trim())){
						bean.setErrorMessage(bean.getErrorMessage()+" Row : "+ (i+1) +" Invalid Faculty ID "+facultyIdPref1);
						bean.setErrorRecord(true);
					}
					if(!facultyList.contains(facultyIdPref2.trim()) && (!"".equals(facultyIdPref2))){
						bean.setErrorMessage(bean.getErrorMessage()+" Row : "+ (i+1) +" Invalid Faculty ID "+facultyIdPref2);
						bean.setErrorRecord(true);
					}
					if(!facultyList.contains(facultyIdPref3.trim()) && (!"".equals(facultyIdPref3))){
						bean.setErrorMessage(bean.getErrorMessage()+" Row : "+ (i+1) +" Invalid Faculty ID "+facultyIdPref3);
						bean.setErrorRecord(true);
					}
					if(!subjectList.contains(subject.trim())){
						bean.setErrorMessage(bean.getErrorMessage()+" Row : "+ (i+1) +" Invalid Course "+subject);
						bean.setErrorRecord(true);
					}

					if(bean.isErrorRecord()){
						errorBeanList.add(bean);
					}else{
						facultyCourseMapingList.add(bean);
					}


					if(i % 1000 == 0){
//						System.out.println("Read "+i+" rows");
					}
				}
			}
//			System.out.println("Total Records = "+(errorBeanList.size()+facultyCourseMapingList.size()));
//			System.out.println("Error Records = "+errorBeanList.size());
//			System.out.println("Valid Records = "+facultyCourseMapingList.size());
		} catch (IOException e) {
			  
		}
		resultList.add(facultyCourseMapingList);
		resultList.add(errorBeanList);
		return resultList;
	}
	
	public ArrayList<List> readSessionReviewFacultyMappingExcel(FileAcadsBean fileBean, ArrayList<String> facultyList,ArrayList<String> subjectList, String userId){

		int  SUBJECT_INDEX = 0;
		int  FACULTY_REVIEW_INDEX = 1;
		
		ByteArrayInputStream bis = new ByteArrayInputStream(fileBean.getFileData().getBytes());
		Workbook workbook;
		List<SessionReviewBean> sessionReviewFacultyMapingList = new ArrayList<SessionReviewBean>();
		List<SessionReviewBean> errorBeanList = new ArrayList<SessionReviewBean>();
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


					row.getCell(SUBJECT_INDEX, Row.CREATE_NULL_AS_BLANK).setCellType(Cell.CELL_TYPE_STRING);
					row.getCell(FACULTY_REVIEW_INDEX, Row.CREATE_NULL_AS_BLANK).setCellType(Cell.CELL_TYPE_STRING);
					

					SessionReviewBean bean = new SessionReviewBean();

					String subjectName = row.getCell(SUBJECT_INDEX, Row.CREATE_NULL_AS_BLANK).getStringCellValue();
					String sessionReviewFacultyId = row.getCell(FACULTY_REVIEW_INDEX, Row.CREATE_NULL_AS_BLANK).getStringCellValue();
					
					if(!facultyList.contains(sessionReviewFacultyId.trim())){
						bean.setErrorMessage(bean.getErrorMessage()+" Row : "+ (i+1) +" Invalid Faculty ID "+sessionReviewFacultyId);
						bean.setErrorRecord(true);
					}
					
					
					if(!subjectList.contains(subjectName.trim())){
						bean.setErrorMessage(bean.getErrorMessage()+" Row : "+ (i+1) +" Invalid Subject Name "+subjectName);
						bean.setErrorRecord(true);
					}
					
					bean.setSubject(subjectName);
					bean.setReviewerFacultyId(sessionReviewFacultyId);
					bean.setCreatedBy(userId);
					
					if(bean.isErrorRecord()){
						errorBeanList.add(bean);
					}else{
						sessionReviewFacultyMapingList.add(bean);
					}


					if(i % 1000 == 0){
//						System.out.println("Read "+i+" rows");
					}
				}
			}
//			System.out.println("Total Records = "+(errorBeanList.size()+sessionReviewFacultyMapingList.size()));
//			System.out.println("Error Records = "+errorBeanList.size());
//			System.out.println("Valid Records = "+sessionReviewFacultyMapingList.size());
		} catch (IOException e) {
			  
		}
		resultList.add(sessionReviewFacultyMapingList);
		resultList.add(errorBeanList);
		return resultList;
	}

	public ArrayList<List> readScheduleSessionForCorporateExcel(FileAcadsBean fileBean,ArrayList<String> facultyList, ArrayList<String> subjectList, String userId){

		
		int DATE_INDEX = 0;
		int START_TIME_INDEX = 1;
		int END_TIME_INDEX = 2;
		int DAY_INDEX = 3;
		int SUBJECT_INDEX = 4;
		int SESSION_NAME_INDEX = 5;
		int FACULTYID__INDEX = 6;
		int MEETINGKEY__INDEX = 7;
		int MEETINGPWD__INDEX = 8;
		int HOSTURL__INDEX = 9;
		int HOSTKEY__INDEX = 10;
		int HOSTPWD__INDEX = 11;
		int ROOM__INDEX = 12;
		int HOSTID__INDEX = 13;
		int ALT_FacultyId_INDEX =14;
		int ALT_MeetingKey_INDEX = 15;
		int ALT_MeetingPwd_INDEX = 16;
		int ALT_HostId_INDEX = 17;
		int ALT_FacultyId2_INDEX = 18;
		int ALT_MeetingKey2_INDEX = 19;
		int ALT_MeetingPwd2_INDEX = 20;
		int ALT_HostId2_INDEX = 21;
		int ALT_FacultyId3_INDEX = 22;
		int ALT_MeetingKey3_INDEX = 23;
		int ALT_MeetingPwd3_INDEX = 24;
		int ALT_HostId3_INDEX = 25;
		
		


		ByteArrayInputStream bis = new ByteArrayInputStream(fileBean.getFileData().getBytes());
		Workbook workbook;
		List<SessionDayTimeAcadsBean> corporateSessionList = new ArrayList<SessionDayTimeAcadsBean>();
		List<SessionDayTimeAcadsBean> errorBeanList = new ArrayList<SessionDayTimeAcadsBean>();
		ArrayList<List> resultList = new ArrayList<>();
		HashMap<String, String> programSubjectKeys = new HashMap<>();
		HashMap<String, String> subjectDateMap = new HashMap<>();
		SimpleDateFormat sdfTime = new java.text.SimpleDateFormat("HH:mm");
		
		ArrayList<String> daysList = new ArrayList<String>(Arrays.asList( 
				"Sunday", "Monday","Tuesday", "Wednesday", "Thursday", "Friday", "Saturday"));
		
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

					
					row.getCell(DAY_INDEX, Row.CREATE_NULL_AS_BLANK).setCellType(Cell.CELL_TYPE_STRING);
					row.getCell(SUBJECT_INDEX, Row.CREATE_NULL_AS_BLANK).setCellType(Cell.CELL_TYPE_STRING);
					row.getCell(SESSION_NAME_INDEX, Row.CREATE_NULL_AS_BLANK).setCellType(Cell.CELL_TYPE_STRING);
					row.getCell(FACULTYID__INDEX, Row.CREATE_NULL_AS_BLANK).setCellType(Cell.CELL_TYPE_STRING);
					row.getCell(MEETINGKEY__INDEX, Row.CREATE_NULL_AS_BLANK).setCellType(Cell.CELL_TYPE_STRING);
					row.getCell(MEETINGPWD__INDEX, Row.CREATE_NULL_AS_BLANK).setCellType(Cell.CELL_TYPE_STRING);
					row.getCell(HOSTURL__INDEX, Row.CREATE_NULL_AS_BLANK).setCellType(Cell.CELL_TYPE_STRING);
					row.getCell(HOSTKEY__INDEX, Row.CREATE_NULL_AS_BLANK).setCellType(Cell.CELL_TYPE_STRING);
					row.getCell(HOSTPWD__INDEX, Row.CREATE_NULL_AS_BLANK).setCellType(Cell.CELL_TYPE_STRING);
					row.getCell(ROOM__INDEX, Row.CREATE_NULL_AS_BLANK).setCellType(Cell.CELL_TYPE_STRING);
					row.getCell(HOSTID__INDEX, Row.CREATE_NULL_AS_BLANK).setCellType(Cell.CELL_TYPE_STRING);
					
					row.getCell(ALT_FacultyId_INDEX, Row.CREATE_NULL_AS_BLANK).setCellType(Cell.CELL_TYPE_STRING);
					row.getCell(ALT_MeetingKey_INDEX, Row.CREATE_NULL_AS_BLANK).setCellType(Cell.CELL_TYPE_STRING);
					row.getCell(ALT_MeetingPwd_INDEX, Row.CREATE_NULL_AS_BLANK).setCellType(Cell.CELL_TYPE_STRING);
					row.getCell(ALT_HostId_INDEX, Row.CREATE_NULL_AS_BLANK).setCellType(Cell.CELL_TYPE_STRING);
					
					row.getCell(ALT_FacultyId2_INDEX, Row.CREATE_NULL_AS_BLANK).setCellType(Cell.CELL_TYPE_STRING);
					row.getCell(ALT_MeetingKey2_INDEX, Row.CREATE_NULL_AS_BLANK).setCellType(Cell.CELL_TYPE_STRING);
					row.getCell(ALT_MeetingPwd2_INDEX, Row.CREATE_NULL_AS_BLANK).setCellType(Cell.CELL_TYPE_STRING);
					row.getCell(ALT_HostId2_INDEX, Row.CREATE_NULL_AS_BLANK).setCellType(Cell.CELL_TYPE_STRING);
					
					row.getCell(ALT_FacultyId3_INDEX, Row.CREATE_NULL_AS_BLANK).setCellType(Cell.CELL_TYPE_STRING);
					row.getCell(ALT_MeetingKey3_INDEX, Row.CREATE_NULL_AS_BLANK).setCellType(Cell.CELL_TYPE_STRING);
					row.getCell(ALT_MeetingPwd3_INDEX, Row.CREATE_NULL_AS_BLANK).setCellType(Cell.CELL_TYPE_STRING);
					row.getCell(ALT_HostId3_INDEX, Row.CREATE_NULL_AS_BLANK).setCellType(Cell.CELL_TYPE_STRING);

					SessionDayTimeAcadsBean bean = new SessionDayTimeAcadsBean();

					Date date = null;
					Cell dateCell = row.getCell(DATE_INDEX);


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
					
					Date startTime = null;
					
					Cell startTimeCell = row.getCell(START_TIME_INDEX);
					
					if(startTimeCell.getCellType() == Cell.CELL_TYPE_NUMERIC){
						if (DateUtil.isCellDateFormatted(startTimeCell)) {
							startTime = startTimeCell.getDateCellValue();
						} else {
							startTime = HSSFDateUtil.getJavaDate(startTimeCell.getNumericCellValue());
						}
					}else{
						bean.setErrorMessage(bean.getErrorMessage()+" Row : "+ (i+1) +" Invalid Format Start time cell : Not a valid Time ");
						bean.setErrorRecord(true);
					}
					
                    Date endTime = null;
					
					Cell endTimeCell = row.getCell(START_TIME_INDEX);
					
					if(endTimeCell.getCellType() == Cell.CELL_TYPE_NUMERIC){
						if (DateUtil.isCellDateFormatted(endTimeCell)) {
							endTime = endTimeCell.getDateCellValue();
						} else {
							endTime = HSSFDateUtil.getJavaDate(endTimeCell.getNumericCellValue());
						}
					}else{
						bean.setErrorMessage(bean.getErrorMessage()+" Row : "+ (i+1) +" Invalid Format Start time cell : Not a valid Time ");
						bean.setErrorRecord(true);
					}
					
					SimpleDateFormat sdfDate = new SimpleDateFormat("yyyy-MM-dd");
					String dates = sdfDate.format(date);
					
					String day = row.getCell(DAY_INDEX, Row.CREATE_NULL_AS_BLANK).getStringCellValue();
					String subject = row.getCell(SUBJECT_INDEX, Row.CREATE_NULL_AS_BLANK).getStringCellValue();
					String sessionName = row.getCell(SESSION_NAME_INDEX, Row.CREATE_NULL_AS_BLANK).getStringCellValue();
					String facultyId = row.getCell(FACULTYID__INDEX, Row.CREATE_NULL_AS_BLANK).getStringCellValue();
					String meetingKey = row.getCell(MEETINGKEY__INDEX, Row.CREATE_NULL_AS_BLANK).getStringCellValue();
					String meetingPWD = row.getCell(MEETINGPWD__INDEX, Row.CREATE_NULL_AS_BLANK).getStringCellValue();
					String hostUrl = row.getCell(HOSTURL__INDEX, Row.CREATE_NULL_AS_BLANK).getStringCellValue();
					String hostKey = row.getCell(HOSTKEY__INDEX, Row.CREATE_NULL_AS_BLANK).getStringCellValue();
					String hostPWD = row.getCell(HOSTPWD__INDEX, Row.CREATE_NULL_AS_BLANK).getStringCellValue();
					String room = row.getCell(ROOM__INDEX, Row.CREATE_NULL_AS_BLANK).getStringCellValue();
					String hostId = row.getCell(HOSTID__INDEX, Row.CREATE_NULL_AS_BLANK).getStringCellValue();
					
					String altFacultyId = row.getCell(ALT_FacultyId_INDEX, Row.CREATE_NULL_AS_BLANK).getStringCellValue();
					String altMeetingKey = row.getCell(ALT_MeetingKey_INDEX, Row.CREATE_NULL_AS_BLANK).getStringCellValue();
					String altMeetingPWD = row.getCell(ALT_MeetingPwd_INDEX, Row.CREATE_NULL_AS_BLANK).getStringCellValue();
					String altHostId = row.getCell(ALT_HostId_INDEX, Row.CREATE_NULL_AS_BLANK).getStringCellValue();
					
					String altFacultyId2 = row.getCell(ALT_FacultyId2_INDEX, Row.CREATE_NULL_AS_BLANK).getStringCellValue();
					String altMeetingKey2 = row.getCell(ALT_MeetingKey2_INDEX, Row.CREATE_NULL_AS_BLANK).getStringCellValue();
					String altMeetingPWD2 = row.getCell(ALT_MeetingPwd2_INDEX, Row.CREATE_NULL_AS_BLANK).getStringCellValue();
					String altHostId2 = row.getCell(ALT_HostId2_INDEX, Row.CREATE_NULL_AS_BLANK).getStringCellValue();
					
					String altFacultyId3 = row.getCell(ALT_FacultyId3_INDEX, Row.CREATE_NULL_AS_BLANK).getStringCellValue();
					String altMeetingKey3 = row.getCell(ALT_MeetingKey3_INDEX, Row.CREATE_NULL_AS_BLANK).getStringCellValue();
					String altMeetingPWD3 = row.getCell(ALT_MeetingPwd3_INDEX, Row.CREATE_NULL_AS_BLANK).getStringCellValue();
					String altHostId3 = row.getCell(ALT_HostId3_INDEX, Row.CREATE_NULL_AS_BLANK).getStringCellValue();
					

					if("".equals(subject.trim()) && "".equals(facultyId.trim())){
						break;
					}

					if(!daysList.contains(day)){
						bean.setErrorMessage(bean.getErrorMessage()+" Row : "+ (i+1) +" Invalid Day " + day);
						bean.setErrorRecord(true);
					}

					String CorporateName = fileBean.getCorporateName();
					bean.setYear(fileBean.getYear());
					bean.setMonth(fileBean.getMonth());
					bean.setSubject(subject);
					bean.setFacultyId(facultyId);
					bean.setStartTime(sdfTime.format(startTime));
					bean.setEndTime(sdfTime.format(endTime));
					bean.setDay(day);
					bean.setDate(dates);
					bean.setCorporateName(CorporateName);
					bean.setMeetingKey(meetingKey);
					bean.setMeetingPwd(meetingPWD);
					bean.setHostUrl(hostUrl);
					bean.setHostKey(hostKey);
					bean.setSessionName(sessionName);
					bean.setHostPassword(hostPWD);
					bean.setCreatedBy(userId);
					bean.setRoom(room);
					bean.setHostId(hostId);
					
					bean.setAltFacultyId(altFacultyId);
					bean.setAltMeetingKey(altMeetingKey);
					bean.setMeetingPwd(altMeetingPWD);
					bean.setAltHostId(altHostId);
					
					bean.setAltFacultyId2(altFacultyId2);
					bean.setAltMeetingKey2(altMeetingKey2);
					bean.setAltMeetingPwd2(altMeetingPWD2);
					bean.setAltHostId2(altHostId2);
					
					bean.setAltFacultyId3(altFacultyId3);
					bean.setAltMeetingKey3(altMeetingKey3);
					bean.setAltMeetingPwd3(altMeetingPWD3);
					bean.setAltHostId3(altHostId3);

					if(!facultyList.contains(facultyId.trim())){
						bean.setErrorMessage(bean.getErrorMessage()+" Row : "+ (i+1) +" Invalid Faculty ID "+facultyId);
						bean.setErrorRecord(true);
					}
					
					if(!subjectList.contains(subject.trim())){
						bean.setErrorMessage(bean.getErrorMessage()+" Row : "+ (i+1) +" Invalid Course "+subject);
						bean.setErrorRecord(true);
					}

					if(bean.isErrorRecord()){
						errorBeanList.add(bean);
					}else{
						corporateSessionList.add(bean);
					}


					if(i % 1000 == 0){
//						System.out.println("Read "+i+" rows");
					}
				}
			}
//			System.out.println("Total Records = "+(errorBeanList.size()+corporateSessionList.size()));
//			System.out.println("Error Records = "+errorBeanList.size());
//			System.out.println("Valid Records = "+corporateSessionList.size());
		} catch (IOException e) {
			  
		}
		resultList.add(corporateSessionList);
		resultList.add(errorBeanList);
		return resultList;
	}
	
	//Read batch session schedulling start
	public ArrayList<List> readBatchSessionScheduleExcel(FileAcadsBean fileBean,ArrayList<String> facultyList, 
					ArrayList<String> subjectCodeList, String userId,List<String> locationList, ArrayList<String> sessionTypeList,Map<String,String> subjectcodemap){

		
		int DATE_INDEX = 0;
		int START_TIME_INDEX = 1;
		int END_TIME_INDEX = 2; 
		int SUBJECT_INDEX = 3;
		int SUBJECT_CODE_INDEX = 4;
		int SESSION_NAME_INDEX = 5;
		int FACULTYID__INDEX = 6;
		int FACULTY_LOCATION__INDEX = 7;
		int ALT_FacultyId_INDEX = 8;
		int ALT_Faculty_LOCATION_INDEX = 9;
		int ALT_FacultyId2_INDEX = 10;
		int ALT_Faculty2_LOCATION_INDEX = 11;
		int ALT_FacultyId3_INDEX = 12;
		int ALT_Faculty3_LOCATION_INDEX = 13;
		int CORPORATE_NAME = 14;
		int TRACK_INDEX = 15;
		int SESSION_TYPE_INDEX = 16;
		
		ByteArrayInputStream bis = new ByteArrayInputStream(fileBean.getFileData().getBytes());
		Workbook workbook;
		List<SessionDayTimeAcadsBean> corporateSessionList = new ArrayList<SessionDayTimeAcadsBean>();
		List<SessionDayTimeAcadsBean> errorBeanList = new ArrayList<SessionDayTimeAcadsBean>();
		ArrayList<List> resultList = new ArrayList<>();
		
		SimpleDateFormat sdfTime = new java.text.SimpleDateFormat("HH:mm:ss");
		
		ArrayList<String> daysList = new ArrayList<String>(Arrays.asList( 
				"Sunday", "Monday","Tuesday", "Wednesday", "Thursday", "Friday", "Saturday"));
		
		try {
			if (fileBean.getFileData().getOriginalFilename().endsWith("xls")) {
				workbook = new HSSFWorkbook(bis);
			} else if (fileBean.getFileData().getOriginalFilename().endsWith("xlsx")) {
				workbook = new XSSFWorkbook(bis);
			} else {
				throw new IllegalArgumentException("Received file does not have a standard excel extension.");
			}
			
			//Adding remote of location list, Remote means Faculty is taking sessions from home
			locationList.add("Remote");
			
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
					row.getCell(SUBJECT_CODE_INDEX, Row.CREATE_NULL_AS_BLANK).setCellType(Cell.CELL_TYPE_STRING);
					row.getCell(SESSION_NAME_INDEX, Row.CREATE_NULL_AS_BLANK).setCellType(Cell.CELL_TYPE_STRING);
					
					row.getCell(FACULTYID__INDEX, Row.CREATE_NULL_AS_BLANK).setCellType(Cell.CELL_TYPE_STRING);
					row.getCell(ALT_FacultyId_INDEX, Row.CREATE_NULL_AS_BLANK).setCellType(Cell.CELL_TYPE_STRING);
					row.getCell(ALT_FacultyId2_INDEX, Row.CREATE_NULL_AS_BLANK).setCellType(Cell.CELL_TYPE_STRING);
					row.getCell(ALT_FacultyId3_INDEX, Row.CREATE_NULL_AS_BLANK).setCellType(Cell.CELL_TYPE_STRING);
					
					row.getCell(CORPORATE_NAME, Row.CREATE_NULL_AS_BLANK).setCellType(Cell.CELL_TYPE_STRING);
					row.getCell(TRACK_INDEX, Row.CREATE_NULL_AS_BLANK).setCellType(Cell.CELL_TYPE_STRING);
					row.getCell(SESSION_TYPE_INDEX, Row.CREATE_NULL_AS_BLANK).setCellType(Cell.CELL_TYPE_STRING);
					
					row.getCell(FACULTY_LOCATION__INDEX, Row.CREATE_NULL_AS_BLANK).setCellType(Cell.CELL_TYPE_STRING);
					row.getCell(ALT_Faculty_LOCATION_INDEX, Row.CREATE_NULL_AS_BLANK).setCellType(Cell.CELL_TYPE_STRING);
					row.getCell(ALT_Faculty2_LOCATION_INDEX, Row.CREATE_NULL_AS_BLANK).setCellType(Cell.CELL_TYPE_STRING);
					row.getCell(ALT_Faculty3_LOCATION_INDEX, Row.CREATE_NULL_AS_BLANK).setCellType(Cell.CELL_TYPE_STRING);
					
					String subject = row.getCell(SUBJECT_INDEX, Row.CREATE_NULL_AS_BLANK).getStringCellValue();
					
					String subjectCode = row.getCell(SUBJECT_CODE_INDEX, Row.CREATE_NULL_AS_BLANK).getStringCellValue();
					String sessionName = row.getCell(SESSION_NAME_INDEX, Row.CREATE_NULL_AS_BLANK).getStringCellValue();
					
					String facultyId = row.getCell(FACULTYID__INDEX, Row.CREATE_NULL_AS_BLANK).getStringCellValue();
					String altFacultyId = row.getCell(ALT_FacultyId_INDEX, Row.CREATE_NULL_AS_BLANK).getStringCellValue();
					String altFacultyId2 = row.getCell(ALT_FacultyId2_INDEX, Row.CREATE_NULL_AS_BLANK).getStringCellValue();
					String altFacultyId3 = row.getCell(ALT_FacultyId3_INDEX, Row.CREATE_NULL_AS_BLANK).getStringCellValue();
					
					String corporateName = row.getCell(CORPORATE_NAME, Row.CREATE_NULL_AS_BLANK).getStringCellValue();
					String track = row.getCell(TRACK_INDEX, Row.CREATE_NULL_AS_BLANK).getStringCellValue();
					String sessionType = row.getCell(SESSION_TYPE_INDEX, Row.CREATE_NULL_AS_BLANK).getStringCellValue();
					
					 String facultyLocation= row.getCell(FACULTY_LOCATION__INDEX, Row.CREATE_NULL_AS_BLANK).getStringCellValue();
					 String altFacultyLocation= row.getCell(ALT_Faculty_LOCATION_INDEX, Row.CREATE_NULL_AS_BLANK).getStringCellValue();
					 String altFaculty2Location= row.getCell(ALT_Faculty2_LOCATION_INDEX, Row.CREATE_NULL_AS_BLANK).getStringCellValue();
					 String altFaculty3Location= row.getCell(ALT_Faculty3_LOCATION_INDEX, Row.CREATE_NULL_AS_BLANK).getStringCellValue();
					
					 try {
						facultyLocation= WordUtils.capitalize(facultyLocation.trim());
						 altFacultyLocation= WordUtils.capitalize(altFacultyLocation.trim());
						 altFaculty2Location= WordUtils.capitalize(altFaculty2Location.trim());
						 altFaculty3Location= WordUtils.capitalize(altFaculty3Location.trim());
					} catch (Exception e) {
						// TODO Auto-generated catch block
						  
					}
					
					if(StringUtils.isBlank(subject) && StringUtils.isBlank(sessionName)){
						break;
					}
					
					if(StringUtils.isBlank(subjectCode) && StringUtils.isBlank(sessionName)){
						break;
					}
					
					SessionDayTimeAcadsBean bean = new SessionDayTimeAcadsBean();

					//check if correct Date Format --> start

					Date date = null;
					Cell dateCell = row.getCell(DATE_INDEX);
					
					if(dateCell.getCellType() == Cell.CELL_TYPE_NUMERIC){
						if (DateUtil.isCellDateFormatted(dateCell)) {
							date = dateCell.getDateCellValue();
						} else {
							date = HSSFDateUtil.getJavaDate(dateCell.getNumericCellValue());
						}
					}else{
//						bean.setErrorMessage(bean.getErrorMessage()+" Row : "+ (i+1) +" Invalid Format of cell : Not a date ");
//						bean.setErrorRecord(true);
						
						String date1 = row.getCell(DATE_INDEX, Row.CREATE_NULL_AS_BLANK).getStringCellValue();
						try {
							date = new SimpleDateFormat("dd-MM-yyyy").parse(date1);
						} catch (Exception e) {
							bean.setErrorMessage(bean.getErrorMessage()+" Row : "+ (i+1) +" Invalid Format of cell : Not a date ");
							bean.setErrorRecord(true);
						}
					}
					
					//check if correct Date Format --> End
					
					//check if correct Start Time Format --> start
					
					Date startTime = null;
					
					Cell startTimeCell = row.getCell(START_TIME_INDEX);
					
					if(startTimeCell.getCellType() == Cell.CELL_TYPE_NUMERIC){
						if (DateUtil.isCellDateFormatted(startTimeCell)) {
							startTime = startTimeCell.getDateCellValue();
						} else {
							startTime = HSSFDateUtil.getJavaDate(startTimeCell.getNumericCellValue());
						}
					}else{
//						bean.setErrorMessage(bean.getErrorMessage()+" Row : "+ (i+1) +" Invalid Format Start time cell : Not a valid Time ");
//						bean.setErrorRecord(true);
						
						String startTime1 = row.getCell(START_TIME_INDEX, Row.CREATE_NULL_AS_BLANK).getStringCellValue();
						try {
							startTime = sdfTime.parse(startTime1);
						} catch (Exception e) {
							bean.setErrorMessage(bean.getErrorMessage()+" Row : "+ (i+1) +" Invalid Format Start time cell : Not a valid Time ");
							bean.setErrorRecord(true);
						}					
					}
					
					//check if correct Start Time Format --> End
					
					//check if correct End Time Format --> start
					
                    Date endTime = null;
					
					Cell endTimeCell = row.getCell(END_TIME_INDEX);
					
					if(endTimeCell.getCellType() == Cell.CELL_TYPE_NUMERIC){
						if (DateUtil.isCellDateFormatted(endTimeCell)) {
							endTime = endTimeCell.getDateCellValue();
						} else {
							endTime = HSSFDateUtil.getJavaDate(endTimeCell.getNumericCellValue());
						}
					}else{
//						bean.setErrorMessage(bean.getErrorMessage()+" Row : "+ (i+1) +" Invalid Format Start time cell : Not a valid Time ");
//						bean.setErrorRecord(true);
						
						String endTime1 = row.getCell(END_TIME_INDEX, Row.CREATE_NULL_AS_BLANK).getStringCellValue();
						try {
							endTime = sdfTime.parse(endTime1);
						} catch (Exception e) {
							bean.setErrorMessage(bean.getErrorMessage()+" Row : "+ (i+1) +" Invalid Format End time cell : Not a valid Time ");
							bean.setErrorRecord(true);
						}
					}
					
					//check if correct End Time Format --> End
					
					SimpleDateFormat sdfDate = new SimpleDateFormat("yyyy-MM-dd");
					String dates="";
					try {
						dates = sdfDate.format(date);
					} catch (Exception e) {
						// TODO Auto-generated catch block
						  
						bean.setErrorMessage(bean.getErrorMessage()+" Row : "+ (i+1) +" Error in formatting date... ");
						bean.setErrorRecord(true);
					
					}
		
					bean.setYear(fileBean.getYear());
					bean.setMonth(fileBean.getMonth());
					bean.setSubject(subject.trim());
					bean.setSubjectCode(subjectCode.trim());
					
					try {
						bean.setStartTime(sdfTime.format(startTime));
						bean.setEndTime(sdfTime.format(endTime));
					} catch (Exception e) {
						// TODO Auto-generated catch block
						  
						bean.setErrorMessage(bean.getErrorMessage()+" Row : "+ (i+1) +" Error in formatting starttime n endtime... ");
						bean.setErrorRecord(true);
					
					}

					bean.setDate(dates);
					bean.setCorporateName(corporateName);
					bean.setSessionName(sessionName);
					bean.setCreatedBy(userId);
					bean.setSessionType(sessionType);
					
					bean.setFacultyId(facultyId.trim());
					bean.setAltFacultyId(altFacultyId.trim());
					bean.setAltFacultyId2(altFacultyId2.trim());
					bean.setAltFacultyId3(altFacultyId3.trim());
					
					bean.setFacultyLocation(facultyLocation);
					bean.setAltFacultyLocation(altFacultyLocation);
					bean.setAltFaculty2Location(altFaculty2Location);
					bean.setAltFaculty3Location(altFaculty3Location);
					
//					if("Retail".equalsIgnoreCase(corporateName)) {
//						bean.setCorporateName("");
//					}else {
//						bean.setCorporateName(corporateName);
//					}
					bean.setCorporateName(corporateName);
					
					//Check Session Type (Webinar/Meeting)
					if (!StringUtils.isBlank(bean.getSessionType())) {
						if (!sessionTypeList.contains(bean.getSessionType())) {
							bean.setErrorMessage(bean.getErrorMessage()+" Row : "+ (i+1) +" Invalid Session Type "+bean.getSessionType());
							bean.setErrorRecord(true);
						}
						//Setting Integer value of Session Type
						bean.setSessionType(bean.getSessionType().replace("Webinar","1"));
						bean.setSessionType(bean.getSessionType().replace("Meeting","2"));
					}else {
						bean.setErrorMessage(bean.getErrorMessage()+" Row : "+ (i+1) +" Blank Session Type "+bean.getSessionType());
						bean.setErrorRecord(true);
					}
					
					//check if correct location --> start
					if(!StringUtils.isBlank(bean.getFacultyLocation())) {
						if(!locationList.contains(bean.getFacultyLocation())){
							bean.setErrorMessage(bean.getErrorMessage()+" Row : "+ (i+1) +" Invalid Host Faculty Location "+bean.getFacultyLocation());
							bean.setErrorRecord(true);
						}
					}

					if(!StringUtils.isBlank(bean.getAltFacultyLocation())) {
						if(!locationList.contains(bean.getAltFacultyLocation())){
							bean.setErrorMessage(bean.getErrorMessage()+" Row : "+ (i+1) +" Invalid parallel 1 Faculty Location "+bean.getAltFacultyLocation());
							bean.setErrorRecord(true);
						}
					}
					if(!StringUtils.isBlank(bean.getAltFaculty2Location())) {
						if(!locationList.contains(bean.getAltFaculty2Location())){
							bean.setErrorMessage(bean.getErrorMessage()+" Row : "+ (i+1) +" Invalid parallel 2 Faculty Location "+bean.getAltFaculty2Location());
							bean.setErrorRecord(true);
						}
					}
					if(!StringUtils.isBlank(bean.getAltFaculty3Location())) {
						if(!locationList.contains(bean.getAltFaculty3Location())){
							bean.setErrorMessage(bean.getErrorMessage()+" Row : "+ (i+1) +" Invalid parallel 3 Faculty Location "+bean.getAltFaculty3Location());
							bean.setErrorRecord(true);
						}
					}
					//check if correct location end
					
					//check if correct facultyId start
					if(!facultyList.contains(facultyId.trim())){
						bean.setErrorMessage(bean.getErrorMessage()+" Row : "+ (i+1) +" Invalid Faculty ID "+facultyId);
						bean.setErrorRecord(true);
					}
					
					if(!StringUtils.isBlank(altFacultyId)) {
						if(!facultyList.contains(altFacultyId.trim())){
							bean.setErrorMessage(bean.getErrorMessage()+" Row : "+ (i+1) +" Invalid Parallel Faculty 1 ID "+altFacultyId);
							bean.setErrorRecord(true);
						}
					}
					
					if(!StringUtils.isBlank(altFacultyId2)) {
						if(!facultyList.contains(altFacultyId2.trim())){
							bean.setErrorMessage(bean.getErrorMessage()+" Row : "+ (i+1) +" Invalid Parallel Faculty 2 ID "+altFacultyId2);
							bean.setErrorRecord(true);
						}
					}
					
					if(!StringUtils.isBlank(altFacultyId3)) {
						if(!facultyList.contains(altFacultyId3.trim())){
							bean.setErrorMessage(bean.getErrorMessage()+" Row : "+ (i+1) +" Invalid Parallel Faculty 3 ID "+altFacultyId3);
							bean.setErrorRecord(true);
						}
					}
					
					//check if correct facultyId End
					
					
					//check if correct Subject start
					/*if(!subjectList.contains(subject.trim())){
						bean.setErrorMessage(bean.getErrorMessage()+" Row : "+ (i+1) +" Invalid Course "+subject);
						bean.setErrorRecord(true);
					}*/
					//check if correct Subject End
					
					//check if correct SubjectCodeId start
					if(!subjectCodeList.contains(subjectCode.trim())){
						bean.setErrorMessage(bean.getErrorMessage()+" Row : "+ (i+1) +" Invalid Subject Code Id "+subjectCode);
						bean.setErrorRecord(true);
					}
					//check if correct SubjectCodeId End
					if(!(subjectcodemap.get(subjectCode.trim()).equals(subject.trim())))
					{
						bean.setErrorMessage(bean.getErrorMessage()+" Row : "+ (i+1) +" Invalid Subject And Subject Code Id doesnt match with each other. ["+subject+" - "+subjectCode+"]");
						bean.setErrorRecord(true);
					}
					
					
					//check if correct Track start
					
					if(StringUtils.isBlank(track)) {
						bean.setTrack("");
					}else {
						if(!"Weekend Batch - Slow Track".equalsIgnoreCase(track.trim()) && 
								!"Weekend Batch - Fast Track".equalsIgnoreCase(track.trim()) && 
								!"Weekday batch".equalsIgnoreCase(track.trim()) &&
								!"Weekend batch".equalsIgnoreCase(track.trim()) &&
								!"WeekDay batch - Slow Track".equalsIgnoreCase(track.trim()) &&
								!"WeekDay Batch - Fast Track".equalsIgnoreCase(track.trim()) &&
								!"Weekend Slow - Track 1".equalsIgnoreCase(track.trim()) &&
								!"Weekday Slow - Track 2".equalsIgnoreCase(track.trim()) &&
								!"Weekend Slow - Track 3".equalsIgnoreCase(track.trim()) &&
								!"Weekend Fast - Track 4".equalsIgnoreCase(track.trim()) &&
								!"Weekday Fast - Track 5".equalsIgnoreCase(track.trim()) &&
								!"Weekday Batch - Track 1".equalsIgnoreCase(track.trim()) &&
								!"Weekday Batch - Track 2".equalsIgnoreCase(track.trim()) &&
								!"Sem I - All Week - Track 5".equalsIgnoreCase(track.trim()) &&
								!"Sem II - All Week".equalsIgnoreCase(track.trim())) {
							bean.setErrorMessage(bean.getErrorMessage()+" Row : "+ (i+1) +" entered track name "+track.trim()+" does not match with any given track names.  ");
							bean.setErrorRecord(true);
						}else {
							bean.setTrack(track.trim());
						}
					}
					
					//check if correct Track start
					
					bean.setIsCancelled("N");
					
					if(bean.isErrorRecord()){
						errorBeanList.add(bean);
					}else{
						corporateSessionList.add(bean);
					}

					if(i % 1000 == 0){
//						System.out.println("Read "+i+" rows");
					}
				}
			}
//			System.out.println("Total Records = "+(errorBeanList.size()+corporateSessionList.size()));
//			System.out.println("Error Records = "+errorBeanList.size());
//			System.out.println("Valid Records = "+corporateSessionList.size());
		} catch (IOException e) {
			  
		}
		resultList.add(corporateSessionList);
		resultList.add(errorBeanList);
		return resultList;
	}
	//read batch session scheduling end

	public ArrayList<List> readSessionDayTimeExcel(FileAcadsBean fileBean, String userId){

		int  DAY_INDEX = 0;
		int  START_TIME_INDEX = 1;

		Date startTime = null;
		ArrayList<String> daysList = new ArrayList<String>(Arrays.asList( 
				"Sunday", "Monday","Tuesday", "Wednesday", "Thursday", "Friday", "Saturday"));

		ByteArrayInputStream bis = new ByteArrayInputStream(fileBean.getFileData().getBytes());
		Workbook workbook;
		List<SessionDayTimeAcadsBean> sessionDayTimeList = new ArrayList<SessionDayTimeAcadsBean>();
		List<SessionDayTimeAcadsBean> errorBeanList = new ArrayList<SessionDayTimeAcadsBean>();
		ArrayList<List> resultList = new ArrayList<>();
		SimpleDateFormat sdfTime = new java.text.SimpleDateFormat("HH:mm");

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
					row.getCell(DAY_INDEX, Row.CREATE_NULL_AS_BLANK).setCellType(Cell.CELL_TYPE_STRING);

					SessionDayTimeAcadsBean bean = new SessionDayTimeAcadsBean();

					String day = row.getCell(DAY_INDEX, Row.CREATE_NULL_AS_BLANK).getStringCellValue();
					Cell startTimeCell = row.getCell(START_TIME_INDEX);

					if(startTimeCell.getCellType() == Cell.CELL_TYPE_NUMERIC){
						if (DateUtil.isCellDateFormatted(startTimeCell)) {
							startTime = startTimeCell.getDateCellValue();
						} else {
							startTime = HSSFDateUtil.getJavaDate(startTimeCell.getNumericCellValue());
						}
					}else{
						bean.setErrorMessage(bean.getErrorMessage()+" Row : "+ (i+1) +" Invalid Format Start time cell : Not a valid Time ");
						bean.setErrorRecord(true);
					}

					if("".equals(day.trim()) ){
						break;
					}


					bean.setYear(fileBean.getYear());
					bean.setMonth(fileBean.getMonth());
					bean.setDay(day);
					bean.setStartTime(sdfTime.format(startTime));
					bean.setCreatedBy(userId);

					if(!daysList.contains(day)){
						bean.setErrorMessage(bean.getErrorMessage()+" Row : "+ (i+1) +" Invalid Day " + day);
						bean.setErrorRecord(true);
					}

					if(bean.isErrorRecord()){
						errorBeanList.add(bean);
					}else{
						sessionDayTimeList.add(bean);
					}

					if(i % 1000 == 0){
//						System.out.println("Read "+i+" rows");
					}
				}
			}
//			System.out.println("Total Records = "+(errorBeanList.size()+sessionDayTimeList.size()));
//			System.out.println("Error Records = "+errorBeanList.size());
//			System.out.println("Valid Records = "+sessionDayTimeList.size());
		} catch (IOException e) {
			  
		}
		resultList.add(sessionDayTimeList);
		resultList.add(errorBeanList);
		return resultList;
	}

	public ArrayList<List> readVideoContentExcel(FileAcadsBean fileBean, 
												ArrayList<String> subjectList, 
												String userId, 
												SessionDayTimeAcadsBean session){
		
		List<VideoContentAcadsBean> videoContentBeanList = new ArrayList<VideoContentAcadsBean>();
		List<VideoContentAcadsBean> errorBeanList = new ArrayList<VideoContentAcadsBean>();
		ArrayList<List> resultList = new ArrayList<>();
		
		int YEAR_INDEX = 0;
		int MONTH_INDEX = 1;
		int SUBJECT_INDEX = 2;
		int FILE_NAME_INDEX = 3;
		int KEY_WORDS_INDEX = 5;
		int DESCRIPTION_INDEX = 6;
		int MOBILEURL_HD_INDEX = 7;
		int MOBILEURL_SD1_INDEX = 8;
		int MOBILEURL_SD2_INDEX = 9; 
		int FACULTY_INDEX = 4;
		
		Integer sessionId = fileBean.getFileId();

		
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

				VideoContentAcadsBean bean = new VideoContentAcadsBean();
				if(row!=null){
					
					row.getCell(YEAR_INDEX, Row.CREATE_NULL_AS_BLANK).setCellType(Cell.CELL_TYPE_STRING);
					row.getCell(MONTH_INDEX, Row.CREATE_NULL_AS_BLANK).setCellType(Cell.CELL_TYPE_STRING);
					row.getCell(SUBJECT_INDEX, Row.CREATE_NULL_AS_BLANK).setCellType(Cell.CELL_TYPE_STRING);
					row.getCell(FILE_NAME_INDEX, Row.CREATE_NULL_AS_BLANK).setCellType(Cell.CELL_TYPE_STRING);
					row.getCell(KEY_WORDS_INDEX, Row.CREATE_NULL_AS_BLANK).setCellType(Cell.CELL_TYPE_STRING);
					row.getCell(DESCRIPTION_INDEX, Row.CREATE_NULL_AS_BLANK).setCellType(Cell.CELL_TYPE_STRING);
					row.getCell(MOBILEURL_HD_INDEX, Row.CREATE_NULL_AS_BLANK).setCellType(Cell.CELL_TYPE_STRING); 
					row.getCell(MOBILEURL_SD1_INDEX, Row.CREATE_NULL_AS_BLANK).setCellType(Cell.CELL_TYPE_STRING); 
					row.getCell(MOBILEURL_SD2_INDEX, Row.CREATE_NULL_AS_BLANK).setCellType(Cell.CELL_TYPE_STRING); 
					row.getCell(FACULTY_INDEX, Row.CREATE_NULL_AS_BLANK).setCellType(Cell.CELL_TYPE_STRING); 
					
					String year = row.getCell(YEAR_INDEX, Row.CREATE_NULL_AS_BLANK).getStringCellValue();
					String month = row.getCell(MONTH_INDEX, Row.CREATE_NULL_AS_BLANK).getStringCellValue();
					String subject = row.getCell(SUBJECT_INDEX, Row.CREATE_NULL_AS_BLANK).getStringCellValue();
					String fileName  = row.getCell(FILE_NAME_INDEX, Row.CREATE_NULL_AS_BLANK).getStringCellValue();
					String keywords = row.getCell(KEY_WORDS_INDEX, Row.CREATE_NULL_AS_BLANK).getStringCellValue();
					String description = row.getCell(DESCRIPTION_INDEX, Row.CREATE_NULL_AS_BLANK).getStringCellValue();
					String mobileUrlHd = row.getCell(MOBILEURL_HD_INDEX, Row.CREATE_NULL_AS_BLANK).getStringCellValue(); 
					String mobileUrlSd1 = row.getCell(MOBILEURL_SD1_INDEX, Row.CREATE_NULL_AS_BLANK).getStringCellValue(); 
					String mobileUrlSd2 = row.getCell(MOBILEURL_SD2_INDEX, Row.CREATE_NULL_AS_BLANK).getStringCellValue(); 
					String facultyId = row.getCell(FACULTY_INDEX, Row.CREATE_NULL_AS_BLANK).getStringCellValue(); 
					if(!subjectList.contains(subject.trim())){
						bean.setErrorMessage(bean.getErrorMessage()+" Row : "+ (i+1) +" Invalid Course "+subject);
						bean.setErrorRecord(true);
					}
					
					bean.setSubject(subject);
					bean.setYear(year);
					bean.setMonth(month);
					bean.setFileName(fileName);
					bean.setKeywords(keywords);
					bean.setDescription(description);
					bean.setDefaultVideo("Yet To Decided"); 
					bean.setMobileUrlHd(mobileUrlHd);
					bean.setMobileUrlSd1(mobileUrlSd1);
					bean.setMobileUrlSd2(mobileUrlSd2);
					bean.setCreatedBy(userId);
					bean.setCreatedDate(new Date()); 
					
					//Code to generate videolink for website  from mobileUrlHd Start
					String videoLink="";
					String vimeoId="";
					if("".equals(mobileUrlHd) || mobileUrlHd==null){
						bean.setErrorMessage(bean.getErrorMessage()+" Row : "+ (i+1) +" Mobile Url for HD video not present  ");
						bean.setErrorRecord(true);
					}else {
						if(mobileUrlHd.contains("progressive_redirect/playback")) {
							vimeoId = mobileUrlHd.split("/playback/")[1].split("/rendition/")[0];
						}
						else {
							String[] linkSplitArray1 = mobileUrlHd.split("/external/", -1);
							String[] linkSplitArray2 = linkSplitArray1[1].split(".hd.", -1);
							vimeoId = linkSplitArray2[0];
						}
						videoLink="https://player.vimeo.com/video/"+vimeoId;
						bean.setVideoLink(videoLink);
					}
					//Code to generate videolink for website  from mobileUrlHd End
					
					//Code to get thumbnail URL start
					String thumbnailUrl=getVideoXml(vimeoId);
					bean.setThumbnailUrl(thumbnailUrl);
					//Code to get thumbnail URL end
					
					//Code to set sessionId start
					if(sessionId==null) {
						bean.setSessionId(0);
					}else {
					
						bean.setSessionId(sessionId);
					}
					//Code to set sessionId end

					if(session!=null) {
						if(facultyId.equalsIgnoreCase(session.getFacultyId())  || 
						   facultyId.equalsIgnoreCase(session.getAltFacultyId()) ||
						   facultyId.equalsIgnoreCase(session.getAltFacultyId2()) ||
						   facultyId.equalsIgnoreCase(session.getAltFacultyId3())
								   	  ) {
							
							bean.setFacultyId(facultyId);
							bean.setSessionDate(session.getDate());
						}else {
							bean.setErrorMessage(bean.getErrorMessage()+" FacultyId does not match with sessions faculties ");
							bean.setErrorRecord(true);
						}
					}
					
					if(bean.isErrorRecord()){
						errorBeanList.add(bean);
					}else{
						videoContentBeanList.add(bean);
					}

					if(i % 1000 == 0){
//						System.out.println("Read "+i+" rows");
					}
				}
			}
		}catch(Exception e){
			  
		}
		
//		System.out.println("Total Records = "+(errorBeanList.size()+videoContentBeanList.size()));
//		System.out.println("Error Records = "+errorBeanList.size());
//		System.out.println("Valid Records = "+videoContentBeanList.size());
		
		resultList.add(videoContentBeanList);
		resultList.add(errorBeanList);
		return resultList;
	}
	
	//Code for reading Video content Batch Start
	public ArrayList<List> readVideoContentBatchExcel(FileAcadsBean fileBean, 
					ArrayList<String> subjectList, 
					String userId,
					ArrayList<String> sessionIdList,
					HashMap<String,SessionDayTimeAcadsBean> sessionsMap){
		
		List<VideoContentAcadsBean> videoContentBeanList = new ArrayList<VideoContentAcadsBean>();
		List<VideoContentAcadsBean> errorBeanList = new ArrayList<VideoContentAcadsBean>();
		ArrayList<List> resultList = new ArrayList<>();
		
		int YEAR_INDEX = 0;
		int MONTH_INDEX = 1;
		int SUBJECT_INDEX = 2;
		int FILE_NAME_INDEX = 3;
		int FACULTY_INDEX = 4;
		int SESSIONID_INDEX = 5;
		int KEY_WORDS_INDEX = 6;
		int DESCRIPTION_INDEX = 7;
		int MOBILEURL_HD_INDEX = 8;
		int MOBILEURL_SD1_INDEX = 9;
		int MOBILEURL_SD2_INDEX = 10; 
		int AUDIO_FILE_URL = 11;
		
		//Integer sessionId = fileBean.getFileId();
		
		
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
		
		VideoContentAcadsBean bean = new VideoContentAcadsBean();
		if(row!=null){
		
		row.getCell(YEAR_INDEX, Row.CREATE_NULL_AS_BLANK).setCellType(Cell.CELL_TYPE_STRING);
		row.getCell(MONTH_INDEX, Row.CREATE_NULL_AS_BLANK).setCellType(Cell.CELL_TYPE_STRING);
		row.getCell(SUBJECT_INDEX, Row.CREATE_NULL_AS_BLANK).setCellType(Cell.CELL_TYPE_STRING);
		row.getCell(FILE_NAME_INDEX, Row.CREATE_NULL_AS_BLANK).setCellType(Cell.CELL_TYPE_STRING);
		row.getCell(KEY_WORDS_INDEX, Row.CREATE_NULL_AS_BLANK).setCellType(Cell.CELL_TYPE_STRING);
		row.getCell(DESCRIPTION_INDEX, Row.CREATE_NULL_AS_BLANK).setCellType(Cell.CELL_TYPE_STRING);
		row.getCell(MOBILEURL_HD_INDEX, Row.CREATE_NULL_AS_BLANK).setCellType(Cell.CELL_TYPE_STRING); 
		row.getCell(MOBILEURL_SD1_INDEX, Row.CREATE_NULL_AS_BLANK).setCellType(Cell.CELL_TYPE_STRING); 
		row.getCell(MOBILEURL_SD2_INDEX, Row.CREATE_NULL_AS_BLANK).setCellType(Cell.CELL_TYPE_STRING); 
		row.getCell(FACULTY_INDEX, Row.CREATE_NULL_AS_BLANK).setCellType(Cell.CELL_TYPE_STRING);
		row.getCell(SESSIONID_INDEX, Row.CREATE_NULL_AS_BLANK).setCellType(Cell.CELL_TYPE_STRING);
		row.getCell(AUDIO_FILE_URL, Row.CREATE_NULL_AS_BLANK).setCellType(Cell.CELL_TYPE_STRING);
		
		String year = row.getCell(YEAR_INDEX, Row.CREATE_NULL_AS_BLANK).getStringCellValue();
		String month = row.getCell(MONTH_INDEX, Row.CREATE_NULL_AS_BLANK).getStringCellValue();
		String subject = row.getCell(SUBJECT_INDEX, Row.CREATE_NULL_AS_BLANK).getStringCellValue();
		String fileName  = row.getCell(FILE_NAME_INDEX, Row.CREATE_NULL_AS_BLANK).getStringCellValue();
		String keywords = row.getCell(KEY_WORDS_INDEX, Row.CREATE_NULL_AS_BLANK).getStringCellValue();
		String description = row.getCell(DESCRIPTION_INDEX, Row.CREATE_NULL_AS_BLANK).getStringCellValue();
		String mobileUrlHd = row.getCell(MOBILEURL_HD_INDEX, Row.CREATE_NULL_AS_BLANK).getStringCellValue(); 
		String mobileUrlSd1 = row.getCell(MOBILEURL_SD1_INDEX, Row.CREATE_NULL_AS_BLANK).getStringCellValue(); 
		String mobileUrlSd2 = row.getCell(MOBILEURL_SD2_INDEX, Row.CREATE_NULL_AS_BLANK).getStringCellValue(); 
		String facultyId = row.getCell(FACULTY_INDEX, Row.CREATE_NULL_AS_BLANK).getStringCellValue(); 
		String sessionId = row.getCell(SESSIONID_INDEX, Row.CREATE_NULL_AS_BLANK).getStringCellValue();
		String audioFile = row.getCell(AUDIO_FILE_URL, Row.CREATE_NULL_AS_BLANK).getStringCellValue();
		
		if(StringUtils.isBlank(sessionId) && StringUtils.isBlank(facultyId) && StringUtils.isBlank(subject) && StringUtils.isBlank(fileName)) {
			break;
		}
		
		if(!containsCaseInsensitive(subject, subjectList)){
		bean.setErrorMessage(bean.getErrorMessage()+" Row : "+ (i+1) +" Invalid Course : "+subject);
		bean.setErrorRecord(true);
		}
		bean.setAudioFile(audioFile);
		bean.setSubject(subject);
		bean.setYear(year);
		bean.setMonth(month);
		bean.setFileName(fileName);
		bean.setKeywords(keywords);
		bean.setDescription(description);
		bean.setDefaultVideo("Yet To Decided"); 
		bean.setMobileUrlHd(mobileUrlHd);
		bean.setMobileUrlSd1(mobileUrlSd1);
		bean.setMobileUrlSd2(mobileUrlSd2);
		bean.setCreatedBy(userId);
		bean.setCreatedDate(new Date()); 
		
		//Code to generate videolink for website  from mobileUrlHd Start
		String videoLink="";
		String vimeoId="";
		if("".equals(mobileUrlHd) || mobileUrlHd==null){
		bean.setErrorMessage(bean.getErrorMessage()+" Row : "+ (i+1) +" Mobile Url for HD video not present  ");
		bean.setErrorRecord(true);
		}else {
			if(mobileUrlHd.contains("progressive_redirect/playback")) {
				vimeoId = mobileUrlHd.split("/playback/")[1].split("/rendition/")[0];
			}
			else {
				String[] linkSplitArray1 = mobileUrlHd.split("/external/", -1);
				String[] linkSplitArray2 = linkSplitArray1[1].split(".hd.", -1);
				vimeoId = linkSplitArray2[0];
			}
			videoLink="https://player.vimeo.com/video/"+vimeoId;
			bean.setVideoLink(videoLink);
		}
		//Code to generate videolink for website  from mobileUrlHd End
		
		//Code to get thumbnail URL start
		String thumbnailUrl=getVideoXml(vimeoId);
		bean.setThumbnailUrl(thumbnailUrl);
		//Code to get thumbnail URL end
		
		//Code to set sessionId start
		if(sessionId==null) {
		bean.setSessionId(0);
		}else {
			int matchedId=0;
			for(String tmmpSessionId : sessionIdList) {
				if(tmmpSessionId.equals(sessionId)){
					matchedId=1;
				} 
			}
			if(matchedId==1) {
				bean.setSessionId(Integer.parseInt(sessionId));
			}else{
				bean.setErrorMessage(" SessionID didnot match, ID : "+sessionId);
				bean.setErrorRecord(true);
			}
		}
		//Code to set sessionId end
		bean.setFacultyId(facultyId);
		bean.setSessionDate(" ");
		
		//SessionDayTimeBean session=getSessionById(sessionId);
		
		SessionDayTimeAcadsBean session = sessionsMap.get(sessionId);
		if(session!=null) {
		if(facultyId.equalsIgnoreCase(session.getFacultyId())  || 
		facultyId.equalsIgnoreCase(session.getAltFacultyId()) ||
		facultyId.equalsIgnoreCase(session.getAltFacultyId2()) ||
		facultyId.equalsIgnoreCase(session.getAltFacultyId3())
			  ) {
		
		bean.setFacultyId(facultyId);
		bean.setSessionDate(session.getDate());
		}else {
		bean.setErrorMessage(bean.getErrorMessage()+" FacultyId does not match with sessions faculties ");
		bean.setErrorRecord(true);
		}
		}
		
		if(bean.isErrorRecord()){
		errorBeanList.add(bean);
		}else{
		videoContentBeanList.add(bean);
		}
		
		if(i % 1000 == 0){
//			System.out.println("Read "+i+" rows");
		}
		}
		}
		}catch(Exception e){
		  
		}
		
//		System.out.println("Total Records = "+(errorBeanList.size()+videoContentBeanList.size()));
//		System.out.println("Error Records = "+errorBeanList.size());
//		System.out.println("Valid Records = "+videoContentBeanList.size());
		
		resultList.add(videoContentBeanList);
		resultList.add(errorBeanList);
		return resultList;
		}

	//End
	
	public boolean containsCaseInsensitive(String subject, List<String> subjectList){
	     for (String string : subjectList){
	        if (string.equalsIgnoreCase(subject.trim())){
	            return true;
	         }
	     }
	    return false;
	  }
	
	public SessionDayTimeAcadsBean getSessionById(String id){
		 TimeTableDAO tdao = (TimeTableDAO) act.getBean("timeTableDAO");
		 SessionDayTimeAcadsBean session=null;

		try {
			 session = tdao.findScheduledSessionById(id);
		} catch (Exception e) {
			  
			session=null;
		}
		return session;
	}

	//Code for reading Video Topic Excel start
	public ArrayList<List> readVideoTopicsExcel(FileAcadsBean fileBean, ArrayList<String> subjectList, String userId, VideoContentAcadsBean parentVideo){
		
		List<VideoContentAcadsBean> videoTopicContentBeanList = new ArrayList<VideoContentAcadsBean>();
		List<VideoContentAcadsBean> errorBeanList = new ArrayList<VideoContentAcadsBean>();
		ArrayList<List> resultList = new ArrayList<>();
		
		int FILE_NAME_INDEX = 0;
		int START_TIME_INDEX = 1;
		int END_TIME_INDEX = 2;
		int KEY_WORDS_INDEX = 3;
		int DESCRIPTION_INDEX = 4; 
		
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

				VideoContentAcadsBean bean = new VideoContentAcadsBean();
				if(row!=null){
					
					row.getCell(FILE_NAME_INDEX, Row.CREATE_NULL_AS_BLANK).setCellType(Cell.CELL_TYPE_STRING);
					row.getCell(KEY_WORDS_INDEX, Row.CREATE_NULL_AS_BLANK).setCellType(Cell.CELL_TYPE_STRING); 
					row.getCell(DESCRIPTION_INDEX, Row.CREATE_NULL_AS_BLANK).setCellType(Cell.CELL_TYPE_STRING); 
					
					//Code to read START TIME AND END TIME AS THE FORMAT IS DIFFERENT START
					Date startTime = new Date();
					
					Cell startTimeCell = row.getCell(START_TIME_INDEX);
					
					if(startTimeCell.getCellType() == Cell.CELL_TYPE_NUMERIC){
						if (DateUtil.isCellDateFormatted(startTimeCell)) {
							startTime = startTimeCell.getDateCellValue();
						} else {
							startTime = HSSFDateUtil.getJavaDate(startTimeCell.getNumericCellValue());
						}
					}else{
						bean.setErrorMessage(bean.getErrorMessage()+" Row : "+ (i+1) +" Invalid Format Start time cell : Not a valid Time ");
						bean.setErrorRecord(true);
					}
					
                    Date endTime = new Date();
					
					Cell endTimeCell = row.getCell(END_TIME_INDEX);
					
					if(endTimeCell.getCellType() == Cell.CELL_TYPE_NUMERIC){
						if (DateUtil.isCellDateFormatted(endTimeCell)) {
							endTime = endTimeCell.getDateCellValue();
						} else {
							endTime = HSSFDateUtil.getJavaDate(endTimeCell.getNumericCellValue());
						}
					}else{
						bean.setErrorMessage(bean.getErrorMessage()+" Row : "+ (i+1) +" Invalid Format Start time cell : Not a valid Time ");
						bean.setErrorRecord(true);
					}
					//Code to read START TIME AND END TIME AS THE FORMAT IS DIFFERENT END	
					
					String fileName = row.getCell(FILE_NAME_INDEX, Row.CREATE_NULL_AS_BLANK).getStringCellValue();
					String keywords = row.getCell(KEY_WORDS_INDEX, Row.CREATE_NULL_AS_BLANK).getStringCellValue(); 
					String description = row.getCell(DESCRIPTION_INDEX, Row.CREATE_NULL_AS_BLANK).getStringCellValue(); 
					
					bean.setParentVideoId(parentVideo.getId());
					bean.setThumbnailUrl(parentVideo.getThumbnailUrl());
					bean.setFileName(fileName);
					bean.setKeywords(keywords);
					bean.setDescription(description);
					bean.setDefaultVideo("Yet To Decided"); 
					bean.setCreatedBy(userId);
					bean.setCreatedDate(new Date());
					bean.setSessionId(parentVideo.getSessionId());

					SimpleDateFormat sdfTime = new java.text.SimpleDateFormat("HH:mm:ss");
					bean.setStartTime(sdfTime.format(startTime));
					bean.setEndTime(sdfTime.format(endTime));
					
					//Code to set custom video link for topic Start
					String[] timeArray=	bean.getStartTime().split(":",-1);
					String timeStamp="#t=";
					int count=0;
					for(String t : timeArray){
						if(count==0){
						timeStamp=timeStamp+t+"h";
						}
						if(count==1){
							timeStamp=timeStamp+t+"m";
						}
						if(count==2){
							timeStamp=timeStamp+t+"s";
						}
						count++;
					}
					String tempLink = parentVideo.getVideoLink()+timeStamp;
					bean.setVideoLink(tempLink);
					//Code to set custom video link for topic end
					
					//Code to calculate duration of video topic Start
					long second = 1000l;
			        long minute = 60l * second;
			        long hour = 60l * minute;

			        // calculation
			        long diff = endTime.getTime() - startTime.getTime();

			        // printing output
			        String calculatedDuration = diff / hour+":"+(diff % hour) / minute+":"+(diff % minute) / second;
			        bean.setDuration(calculatedDuration);
			        //Code to calculate duration of video topic End
					

					SimpleDateFormat sdf=new SimpleDateFormat("dd/MM/YYYY");
					bean.setAddedOn(sdf.format(new Date()));
					bean.setAddedBy(userId);
					bean.setSubject(parentVideo.getSubject());
					
					if(bean.isErrorRecord()){
						errorBeanList.add(bean);
					}else{
						videoTopicContentBeanList.add(bean);
					}

					if(i % 1000 == 0){
//						System.out.println("Read "+i+" rows");
					}
				}
			}
		}catch(Exception e){
			  
		}
		
//		System.out.println("Total Records = "+(errorBeanList.size()+videoTopicContentBeanList.size()));
//		System.out.println("Error Records = "+errorBeanList.size());
//		System.out.println("Valid Records = "+videoTopicContentBeanList.size());
		
		resultList.add(videoTopicContentBeanList);
		resultList.add(errorBeanList);
		return resultList;
	}
	//Code for reading Video Topic Excel End
	
	//Code to get Vimeo Video Thumbnail url Start
	public String getVideoXml(String id) throws Exception {
		
		CloseableHttpClient client = HttpClientBuilder.create().build();
		String url = "https://api.vimeo.com/videos/"+id;
		
		try {
			HttpHeaders headers =  new HttpHeaders();
			headers.add("Content-Type", "application/json");
			headers.add("Accept", "application/vnd.vimeo.*+json;version=3.4");
			headers.add("Authorization", "bearer e150a7667f821ace37c300797c7b1d74");
			
			RestTemplate restTemplate = new RestTemplate();
			HttpEntity<String> entity = new HttpEntity<>("parameters", headers);

			ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.GET, entity, String.class);
			JsonObject jsonObject = new JsonParser().parse(response.getBody()).getAsJsonObject();
			jsonObject = jsonObject.get("pictures").getAsJsonObject();
			JsonArray jsonArray = jsonObject.get("sizes").getAsJsonArray();
			
			JsonObject jobj = (JsonObject) jsonArray.get(6);
			String result = jobj.get("link").getAsString();
			return result;
			
		}catch(Exception vimeoException) {
				
			}
		finally{
			     //Important: Close the connect
				 client.close();
			 }
			 
			 return "https://studentzone-ngasce.nmims.edu/acads/resources_2015/images/thumbnailLogo.png";
		
		}

	//Code to get Vimeo Video Thumbnail url Start
	
	//Code to read module content excel sheet Start
	public ArrayList<List> readModuleContentExcel(FileAcadsBean fileBean, ArrayList<String> subjectList, String userId){
		
		List<ModuleContentAcadsBean> moduleContentBeanList = new ArrayList<ModuleContentAcadsBean>();
		List<ModuleContentAcadsBean> errorBeanList = new ArrayList<ModuleContentAcadsBean>();
		ArrayList<List> resultList = new ArrayList<>();

		int SUBJECT_INDEX = 0;
		int MODULE_NAME_INDEX = 1;
		int DESCRIPTION_INDEX = 2;
		
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

				ModuleContentAcadsBean bean = new ModuleContentAcadsBean();
				if(row!=null){
					
					row.getCell(SUBJECT_INDEX, Row.CREATE_NULL_AS_BLANK).setCellType(Cell.CELL_TYPE_STRING);
					row.getCell(MODULE_NAME_INDEX, Row.CREATE_NULL_AS_BLANK).setCellType(Cell.CELL_TYPE_STRING); 
					row.getCell(DESCRIPTION_INDEX, Row.CREATE_NULL_AS_BLANK).setCellType(Cell.CELL_TYPE_STRING); 
					
										
					String moduleName = row.getCell(MODULE_NAME_INDEX, Row.CREATE_NULL_AS_BLANK).getStringCellValue();
					String subject = row.getCell(SUBJECT_INDEX, Row.CREATE_NULL_AS_BLANK).getStringCellValue(); 
					String description = row.getCell(DESCRIPTION_INDEX, Row.CREATE_NULL_AS_BLANK).getStringCellValue(); 
					

					bean.setSubject(subject);
					bean.setModuleName(moduleName);
					bean.setDescription(description); 
					bean.setCreatedBy(userId); 
					bean.setLastModifiedBy(userId);

					bean.setActive("Y");
					
					if(bean.isErrorRecord()){
						errorBeanList.add(bean);
					}else{
						moduleContentBeanList.add(bean);
					}

					if(i % 1000 == 0){
//						System.out.println("Read "+i+" rows");
					}
				}
			}
		}catch(Exception e){
			  
		}
		
//		System.out.println("Total Records = "+(errorBeanList.size()+moduleContentBeanList.size()));
//		System.out.println("Error Records = "+errorBeanList.size());
//		System.out.println("Valid Records = "+moduleContentBeanList.size());
		
		resultList.add(moduleContentBeanList);
		resultList.add(errorBeanList);
		return resultList;
	}

	//Code to read module content excel sheet End

	//code to read faculty excel sheet :start
	public ArrayList<List> readFacultyExcel(FacultyAcadsBean facultyBean, String userId) {		

		ByteArrayInputStream bis = new ByteArrayInputStream(facultyBean.getFacultyUpload().getBytes());
		Workbook workbook;
		List<FacultyAcadsBean> facultyList = new ArrayList<FacultyAcadsBean>();
		List<FacultyAcadsBean> errorBeanList = new ArrayList<FacultyAcadsBean>();
		ArrayList<List> resultList = new ArrayList<>();
		SimpleDateFormat sdfTime = new java.text.SimpleDateFormat("DD-MM-YYYY");
		Iterator<Row> rowIterator = null;
		try {
			if (facultyBean.getFacultyUpload().getOriginalFilename().endsWith(".xls")) {
				workbook = new HSSFWorkbook(bis);
				HSSFSheet sheet = (HSSFSheet) workbook.getSheetAt(0);
				rowIterator = sheet.iterator();
			} else if (facultyBean.getFacultyUpload().getOriginalFilename().endsWith(".xlsx")) {
				workbook = new XSSFWorkbook(bis);
				XSSFSheet sheet = (XSSFSheet) workbook.getSheetAt(0);
				rowIterator = sheet.iterator();
			} else {
				throw new IllegalArgumentException("Received file does not have a standard excel extension.");
			}

			// Get first/desired sheet from the workbook

			int i = 0;
			// Skip first row since it contains column names, not data.
			if (rowIterator.hasNext()) {
				Row row = rowIterator.next();
			}
			while (rowIterator.hasNext()) {
				i++;
				Row row = rowIterator.next();

				if (row != null) {

					row.getCell(1, Row.CREATE_NULL_AS_BLANK).setCellType(Cell.CELL_TYPE_STRING);
					FacultyAcadsBean bean = new FacultyAcadsBean();

					String facultyId = row.getCell(1, Row.CREATE_NULL_AS_BLANK).getStringCellValue();

					if ("".equals(facultyId.trim())) {
						break;
					}
					String title = 	row.getCell(2, Row.CREATE_NULL_AS_BLANK).getStringCellValue();
					String fname = row.getCell(3, Row.CREATE_NULL_AS_BLANK).getStringCellValue();
					String mname = row.getCell(4, Row.CREATE_NULL_AS_BLANK).getStringCellValue();
					String lname = row.getCell(5, Row.CREATE_NULL_AS_BLANK).getStringCellValue();					
					String email = row.getCell(6, Row.CREATE_NULL_AS_BLANK).getStringCellValue();
					String sEmail = row.getCell(7, Row.CREATE_NULL_AS_BLANK).getStringCellValue();
					
					String mobString="";
					try {
						mobString =  row.getCell(8, Row.CREATE_NULL_AS_BLANK).getStringCellValue();
					} catch(Exception ex) {
						int mob =  (int)row.getCell(8, Row.CREATE_NULL_AS_BLANK).getNumericCellValue();
						mobString = String.valueOf(mob);
					}					
					
					String altMobString ="";
					try {
						altMobString =  row.getCell(9, Row.CREATE_NULL_AS_BLANK).getStringCellValue();
					} catch(Exception ex) {
						int mob =  (int)row.getCell(9, Row.CREATE_NULL_AS_BLANK).getNumericCellValue();
						altMobString = String.valueOf(mob);
					}
					
					
					
					String officeMobString = "";
					try {
						officeMobString =  row.getCell(10, Row.CREATE_NULL_AS_BLANK).getStringCellValue();
					} catch(Exception ex) {
						int mob =  (int)row.getCell(10, Row.CREATE_NULL_AS_BLANK).getNumericCellValue();
						officeMobString = String.valueOf(mob);
					}
					
					
					String homeMobString = "";
					try {
						homeMobString =  row.getCell(11, Row.CREATE_NULL_AS_BLANK).getStringCellValue();
					} catch(Exception ex) {
						int mob =  (int)row.getCell(11, Row.CREATE_NULL_AS_BLANK).getNumericCellValue();
						homeMobString = String.valueOf(mob);
					}
					
					String dob = row.getCell(12, Row.CREATE_NULL_AS_BLANK).getStringCellValue();
					String edu = row.getCell(13, Row.CREATE_NULL_AS_BLANK).getStringCellValue();
					String subjPref1 = row.getCell(14, Row.CREATE_NULL_AS_BLANK).getStringCellValue();
					String subjPref2 = row.getCell(15, Row.CREATE_NULL_AS_BLANK).getStringCellValue();
					String subjPref3 = row.getCell(16, Row.CREATE_NULL_AS_BLANK).getStringCellValue();	
					
					String yearsOfNGASCEExp = "";
					try {
						yearsOfNGASCEExp =  row.getCell(17, Row.CREATE_NULL_AS_BLANK).getStringCellValue();
					} catch(Exception ex) {
						int year =  (int)row.getCell(17, Row.CREATE_NULL_AS_BLANK).getNumericCellValue();
						yearsOfNGASCEExp = String.valueOf(year);
					}
					
					String yearsOfTeachingExp = "";
					try {
						yearsOfTeachingExp =  row.getCell(18, Row.CREATE_NULL_AS_BLANK).getStringCellValue();
					} catch(Exception ex) {
						int year =  (int)row.getCell(18, Row.CREATE_NULL_AS_BLANK).getNumericCellValue();
						yearsOfTeachingExp = String.valueOf(year);
					}
					
					String yearsOfCorpExp = "";
					try {
						yearsOfCorpExp =  row.getCell(19, Row.CREATE_NULL_AS_BLANK).getStringCellValue();
					} catch(Exception ex) {
						int year =  (int)row.getCell(19, Row.CREATE_NULL_AS_BLANK).getNumericCellValue();
						yearsOfCorpExp = String.valueOf(year);
					}
					
					String loc = row.getCell(20, Row.CREATE_NULL_AS_BLANK).getStringCellValue();
					String addr = row.getCell(21, Row.CREATE_NULL_AS_BLANK).getStringCellValue();
					String gradDetails = row.getCell(22, Row.CREATE_NULL_AS_BLANK).getStringCellValue();
					
					String yearGradPass = "";
					try {
						yearGradPass =  row.getCell(23, Row.CREATE_NULL_AS_BLANK).getStringCellValue();
					} catch(Exception ex) {
						int year =  (int)row.getCell(23, Row.CREATE_NULL_AS_BLANK).getNumericCellValue();
						yearGradPass = String.valueOf(year);
					}
					
					String phdDetails = row.getCell(24, Row.CREATE_NULL_AS_BLANK).getStringCellValue();
					
					String yearPhdPass = "";
					try {
						yearPhdPass =  row.getCell(25, Row.CREATE_NULL_AS_BLANK).getStringCellValue();
					} catch(Exception ex) {
						int year =  (int)row.getCell(25, Row.CREATE_NULL_AS_BLANK).getNumericCellValue();
						yearPhdPass = String.valueOf(year);
					}
					
					String cvUrl = row.getCell(26, Row.CREATE_NULL_AS_BLANK).getStringCellValue();
					String imgUrl = row.getCell(27, Row.CREATE_NULL_AS_BLANK).getStringCellValue();
					String currOrgn = row.getCell(28, Row.CREATE_NULL_AS_BLANK).getStringCellValue();
					String design = row.getCell(29, Row.CREATE_NULL_AS_BLANK).getStringCellValue();					
					String natureAppt = row.getCell(30, Row.CREATE_NULL_AS_BLANK).getStringCellValue();
					String areaSpec = row.getCell(31, Row.CREATE_NULL_AS_BLANK).getStringCellValue();
					String otherAreaSpec = row.getCell(32, Row.CREATE_NULL_AS_BLANK).getStringCellValue();
					
					
					String aadharString = "";
					try {
						aadharString =  row.getCell(33, Row.CREATE_NULL_AS_BLANK).getStringCellValue();
					} catch(Exception ex) {
						int mob =  (int)row.getCell(33, Row.CREATE_NULL_AS_BLANK).getNumericCellValue();
						aadharString = String.valueOf(mob);
					}
					
					String progGroup = row.getCell(34, Row.CREATE_NULL_AS_BLANK).getStringCellValue();
					String progName = row.getCell(35, Row.CREATE_NULL_AS_BLANK).getStringCellValue();
					String apprInSlab = row.getCell(36, Row.CREATE_NULL_AS_BLANK).getStringCellValue();
					String dateECMeeting = row.getCell(37, Row.CREATE_NULL_AS_BLANK).getStringCellValue();
					String consentMarkRelease = row.getCell(38, Row.CREATE_NULL_AS_BLANK).getStringCellValue();
					String consentMarkReleaseReason = row.getCell(39, Row.CREATE_NULL_AS_BLANK).getStringCellValue();
					String honorsAwards = row.getCell(40, Row.CREATE_NULL_AS_BLANK).getStringCellValue();
					String memberships = row.getCell(41, Row.CREATE_NULL_AS_BLANK).getStringCellValue();
					String researchInterest = row.getCell(42, Row.CREATE_NULL_AS_BLANK).getStringCellValue();
					String articleInternation = row.getCell(43, Row.CREATE_NULL_AS_BLANK).getStringCellValue();
					String articleNational = row.getCell(44, Row.CREATE_NULL_AS_BLANK).getStringCellValue();
					String summaryABCDJourn = row.getCell(45, Row.CREATE_NULL_AS_BLANK).getStringCellValue();
					String paperPresInternation = row.getCell(46, Row.CREATE_NULL_AS_BLANK).getStringCellValue();
					String paperPresNational = row.getCell(47, Row.CREATE_NULL_AS_BLANK).getStringCellValue();
					String caseStudies = row.getCell(48, Row.CREATE_NULL_AS_BLANK).getStringCellValue();
					String bookPublished = row.getCell(49, Row.CREATE_NULL_AS_BLANK).getStringCellValue();
					String bookChapter = row.getCell(50, Row.CREATE_NULL_AS_BLANK).getStringCellValue();
					String patents = row.getCell(51, Row.CREATE_NULL_AS_BLANK).getStringCellValue();
					String consultProj = row.getCell(52, Row.CREATE_NULL_AS_BLANK).getStringCellValue();
					String researchProj = row.getCell(53, Row.CREATE_NULL_AS_BLANK).getStringCellValue();
					String active = row.getCell(54, Row.CREATE_NULL_AS_BLANK).getStringCellValue();
					
					/* Added For Grader */
					if(title.equalsIgnoreCase("Grader"))
					{
						if (email == null || email.isEmpty() || mobString == null || mobString.isEmpty()) {
							bean.setErrorMessage(bean.getErrorMessage() + " Row : " + (i + 1) + " Blank cell! (Check Whether the Email-Id,Mobile No is Empty or Not.)");
							bean.setErrorRecord(true);
						}
					}
					
					if (title == null || title.isEmpty()) {
						bean.setErrorMessage(bean.getErrorMessage() + " Row : " + (i + 1) + " Blank cell! (Check Whether the title is empty or not.)");
						bean.setErrorRecord(true);
					}
					if (fname == null || fname.isEmpty() || lname == null || lname.isEmpty()) {
						bean.setErrorMessage(bean.getErrorMessage() + " Row : " + (i + 1) + " Blank cell! (Check Whether the firstName,lastName is empty or not.)");
						bean.setErrorRecord(true);
					}
					if (bean.isErrorRecord()) {
						errorBeanList.add(bean);
						continue;
					}

					bean.setFacultyId(facultyId);
					bean.setTitle(title);					
					bean.setFirstName(fname);
					bean.setMiddleName(mname);
					bean.setLastName(lname);
					bean.setEmail(email);
					bean.setSecondaryEmail(sEmail);
					bean.setMobile(mobString);
					bean.setAltContact(altMobString);
					bean.setOfficeContact(officeMobString);
					bean.setHomeContact(homeMobString);
					bean.setDob(dob);
					bean.setEducation(edu);									
					bean.setAddress(addr);
					bean.setLocation(loc);
					bean.setSubjectPref1(subjPref1);
					bean.setSubjectPref2(subjPref2);
					bean.setSubjectPref3(subjPref3);
					bean.setNgasceExp(yearsOfNGASCEExp);
					bean.setTeachingExp(yearsOfTeachingExp);
					bean.setCorporateExp(yearsOfCorpExp);
					bean.setGraduationDetails(gradDetails);
					bean.setYearOfPassingGraduation(yearGradPass);
					bean.setPhdDetails(phdDetails);
					bean.setYearOfPassingPhd(yearPhdPass);
					bean.setCvUrl(cvUrl);
					bean.setImgUrl(imgUrl);
					bean.setCurrentOrganization(currOrgn);
					bean.setDesignation(design);
					bean.setNatureOfAppointment(natureAppt);
					bean.setAreaOfSpecialisation(areaSpec);
					bean.setOtherAreaOfSpecialisation(otherAreaSpec);
					bean.setAadharNumber(aadharString);
					bean.setProgramGroup(progGroup);
					bean.setProgramName(progName);
					bean.setApprovedInSlab(apprInSlab);
					bean.setDateOfECMeetingApprovalTaken(dateECMeeting);
					bean.setConsentForMarketingCollateralsOrPhotoAndProfileRelease(consentMarkRelease);
					bean.setConsentForMarketingCollateralsOrPhotoAndProfileReleaseReason(consentMarkReleaseReason);
					bean.setResearchInterest(researchInterest);
					bean.setResearchProjects(researchProj);
					bean.setHonorsAndAwards(honorsAwards);
					bean.setMemberships(memberships);
					bean.setCaseStudiesPublished(caseStudies);
					bean.setArticlesPublishedInInternationalJournals(articleInternation);
					bean.setArticlesPublishedInNationalJournals(articleNational);
					bean.setSummaryOfPapersPublishedInABDCJournals(summaryABCDJourn);
					bean.setPaperPresentationsAtInternationalConference(paperPresInternation);
					bean.setPaperPresentationAtNationalConference(paperPresNational);
					bean.setBookChaptersPublished(bookChapter);
					bean.setBooksPublished(bookPublished);
					bean.setListOfPatents(patents);
					bean.setConsultingProjects(consultProj);
					bean.setActive(active);
					
					
					bean.setCreatedBy(userId);
					
					if (bean.isErrorRecord()) {
						errorBeanList.add(bean);
					} else {
						facultyList.add(bean);
					}

					if (i % 1000 == 0) {
//						System.out.println("Read " + i + " rows");
					}
				}
			}
//			System.out.println("Total Records = " + (errorBeanList.size() + facultyList.size()));
//			System.out.println("Error Records = " + errorBeanList.size());
//			System.out.println("Valid Records = " + facultyList.size());
		} catch (IOException e) {
			  
		}
		resultList.add(facultyList);
		resultList.add(errorBeanList);
		return resultList;
	}

	//code to read faculty excel sheet :end
	
	//read syllabus begins

	public ArrayList<List<SyllabusBean>> readSyllabusExcel(SyllabusBean bean, String userId){
		
		List<SyllabusBean> syllabusContentBeanList = new ArrayList<SyllabusBean>();
		List<SyllabusBean> errorBeanList = new ArrayList<SyllabusBean>();
		ArrayList<List<SyllabusBean>> resultList = new ArrayList<>();

		int CHAPTER_INDEX = 0;
		int TITLE_INDEX = 1;
		int TOPIC_INDEX = 2;
		int OUTCOMES_INDEX = 3;
		int PEDAGOGICALTOOL_INDEX = 4;
		
		ByteArrayInputStream bis = new ByteArrayInputStream(bean.getFile().getBytes());
		Workbook workbook;
		
		try {
			if (bean.getFile().getOriginalFilename().endsWith("xls")) {
				workbook = new HSSFWorkbook(bis);
			} else if (bean.getFile().getOriginalFilename().endsWith("xlsx")) {
				workbook = new XSSFWorkbook(bis);
			} else {
				throw new IllegalArgumentException("Received file does not have a standard excel extension.");
			}

			//Get first/desired sheet from the workbook
			XSSFSheet sheet = (XSSFSheet) workbook.getSheetAt(0);

			Iterator<Row> rowIterator = sheet.iterator();

			int i = 0;
			// Skip first row since it contains column names, not data.
			if (rowIterator.hasNext()) {
				Row row = rowIterator.next();
			}
			
			while(rowIterator.hasNext()) {
				
				i++;
				Row row = rowIterator.next();
				SyllabusBean contentBean = new SyllabusBean();
				
				if(row!=null){
										
					String chapter = row.getCell(CHAPTER_INDEX, Row.CREATE_NULL_AS_BLANK).getStringCellValue(); 
					String title = row.getCell(TITLE_INDEX, Row.CREATE_NULL_AS_BLANK).getStringCellValue(); 
					String topic = row.getCell(TOPIC_INDEX, Row.CREATE_NULL_AS_BLANK).getStringCellValue(); 
					String outcomes = row.getCell(OUTCOMES_INDEX, Row.CREATE_NULL_AS_BLANK).getStringCellValue(); 
					String pedagogicalTool = row.getCell(PEDAGOGICALTOOL_INDEX, Row.CREATE_NULL_AS_BLANK).getStringCellValue(); 

					contentBean.setSubjectname(bean.getSubjectname());
					contentBean.setSem(bean.getSem());
					contentBean.setChapter(chapter);
					contentBean.setTitle(title);
					contentBean.setTopic(topic);
					contentBean.setOutcomes(outcomes);
					contentBean.setPedagogicalTool(pedagogicalTool);
					contentBean.setCreatedBy(userId); 
					contentBean.setLastModifiedBy(userId);

					if(contentBean.isErrorRecord()){
						errorBeanList.add(contentBean);
					}else{
						syllabusContentBeanList.add(contentBean);
					}

					if(i % 1000 == 0){
//						System.out.println("Read "+i+" rows");
					}
				}
			}
		}catch(Exception e){
			  
		}
		
//		System.out.println("Total Records = "+(errorBeanList.size()+syllabusContentBeanList.size()));
//		System.out.println("Error Records = "+errorBeanList.size());
//		System.out.println("Valid Records = "+syllabusContentBeanList.size());
		
		resultList.add(syllabusContentBeanList);
		resultList.add(errorBeanList);
		
		return resultList;
	}
	
	//read syllabus ends
}
