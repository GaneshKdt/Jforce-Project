package com.nmims.helpers;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.WordUtils;
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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

import com.nmims.beans.CounsellingBean;
import com.nmims.beans.FileCareerservicesBean;
import com.nmims.beans.InterviewBean;
import com.nmims.beans.ProgressDetailsBean;
import com.nmims.beans.SessionDayTimeBean;

@Component
public class ExcelHelper {

	@Autowired(required=false)
	ApplicationContext act;

	private static final Logger logger = LoggerFactory.getLogger(ExcelHelper.class);
 
	//Read batch session schedulling start
	@SuppressWarnings({ "rawtypes", "unused" })
	public ArrayList<List> readBatchSessionScheduleExcel(FileCareerservicesBean fileBean,ArrayList<String> facultyList, ArrayList<String> subjectList, String userId,List<String> locationList){

		
		int DATE_INDEX = 0;
		int START_TIME_INDEX = 1;
		int END_TIME_INDEX = 2; 
		int SESSION_NAME_INDEX = 3;
		int FACULTYID__INDEX = 4;
		int FACULTY_LOCATION__INDEX = 5;
		int DESCRIPTION_INDEX = 6;
		
		
		ByteArrayInputStream bis = new ByteArrayInputStream(fileBean.getFileData().getBytes());
		Workbook workbook;
		List<SessionDayTimeBean> sessionList = new ArrayList<SessionDayTimeBean>();
		List<SessionDayTimeBean> errorSessionList = new ArrayList<SessionDayTimeBean>();
		ArrayList<List> resultList = new ArrayList<>();
		
		SimpleDateFormat sdfTime = new java.text.SimpleDateFormat("HH:mm:ss");
		
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

					
					row.getCell(SESSION_NAME_INDEX, Row.CREATE_NULL_AS_BLANK).setCellType(Cell.CELL_TYPE_STRING);
					
					row.getCell(FACULTYID__INDEX, Row.CREATE_NULL_AS_BLANK).setCellType(Cell.CELL_TYPE_STRING);
					row.getCell(FACULTY_LOCATION__INDEX, Row.CREATE_NULL_AS_BLANK).setCellType(Cell.CELL_TYPE_STRING);
					row.getCell(DESCRIPTION_INDEX, Row.CREATE_NULL_AS_BLANK).setCellType(Cell.CELL_TYPE_STRING);
					
					String sessionName = row.getCell(SESSION_NAME_INDEX, Row.CREATE_NULL_AS_BLANK).getStringCellValue();
					String facultyId = row.getCell(FACULTYID__INDEX, Row.CREATE_NULL_AS_BLANK).getStringCellValue();
					String facultyLocation= row.getCell(FACULTY_LOCATION__INDEX, Row.CREATE_NULL_AS_BLANK).getStringCellValue();
					String description = row.getCell(DESCRIPTION_INDEX, Row.CREATE_NULL_AS_BLANK).getStringCellValue();
					
					 try {
						facultyLocation= WordUtils.capitalize(facultyLocation.trim());
						
					} catch (Exception e) {
						logger.info("exception : "+e.getMessage());
					}
					

					SessionDayTimeBean bean = new SessionDayTimeBean();

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
						bean.setErrorMessage(bean.getErrorMessage()+" Row : "+ (i+1) +" Invalid Format of cell : Not a date ");
						bean.setErrorRecord(true);
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
						bean.setErrorMessage(bean.getErrorMessage()+" Row : "+ (i+1) +" Invalid Format Start time cell : Not a valid Time ");
						bean.setErrorRecord(true);
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
						bean.setErrorMessage(bean.getErrorMessage()+" Row : "+ (i+1) +" Invalid Format Start time cell : Not a valid Time ");
						bean.setErrorRecord(true);
					}
					
					//check if correct End Time Format --> End
					
					SimpleDateFormat sdfDate = new SimpleDateFormat("yyyy-MM-dd");

					String dates="";
					try {
						dates = sdfDate.format(date);
					} catch (Exception e) {
						logger.info("exception : "+e.getMessage());
						bean.setErrorMessage(bean.getErrorMessage()+" Row : "+ (i+1) +" Error in formatting date... ");
						bean.setErrorRecord(true);
					
					}

//		
//					bean.setYear(fileBean.getYear());
//					bean.setMonth(fileBean.getMonth());
					bean.setFacultyId(facultyId.trim());
					
					try {
						bean.setStartTime(sdfTime.format(startTime));
						bean.setEndTime(sdfTime.format(endTime));
					} catch (Exception e) {
						logger.info("exception : "+e.getMessage());
						bean.setErrorMessage(bean.getErrorMessage()+" Row : "+ (i+1) +" Error in formatting starttime n endtime... ");
						bean.setErrorRecord(true);
					
					}

				    bean.setDate(dates);
					bean.setSessionName(sessionName);
					bean.setDescription(description);
					bean.setCreatedBy(userId);

					bean.setFacultyLocation(facultyLocation);
					
					
					if(!facultyList.contains(facultyId.trim())){
						bean.setErrorMessage(bean.getErrorMessage()+" Row : "+ (i+1) +" Invalid Faculty ID "+facultyId);
						bean.setErrorRecord(true);
					}
					//check if correct location --> start
					if(!StringUtils.isBlank(bean.getFacultyLocation())) {
						if(!locationList.contains(bean.getFacultyLocation())){
							bean.setErrorMessage(bean.getErrorMessage()+" Row : "+ (i+1) +" Invalid Host Faculty Location "+bean.getFacultyLocation());
							bean.setErrorRecord(true);
						}
					}

					//check if correct location end
					
					bean.setIsCancelled("N");
					
					if(bean.isErrorRecord()){
						errorSessionList.add(bean);
					}else{
						sessionList.add(bean);
					}


					if(i % 1000 == 0){
						logger.info("Read "+i+" rows");
					}
				}
			}

		} catch (IOException e) {
			logger.info("exception : "+e.getMessage());
		}
		resultList.add(sessionList);
		resultList.add(errorSessionList);
		return resultList;
	}
	//read batch session schedulling end

	public ArrayList<ArrayList<ProgressDetailsBean>> readProgressDetailsFromExcel( ProgressDetailsBean progressBean, 
			String featureId, String packageId){

		int  SAPID_INDEX = 0;
		int  ACTIVATION_DATE_INDEX = 1;

		Workbook workbook;
		
		ByteArrayInputStream bis = new ByteArrayInputStream(progressBean.getFileData().getBytes());
		ArrayList<ProgressDetailsBean> progressList = new ArrayList<ProgressDetailsBean>();
		ArrayList<ProgressDetailsBean> errorBeanList = new ArrayList<ProgressDetailsBean>();
		ArrayList<ArrayList<ProgressDetailsBean>> resultList = new ArrayList<>();
		
		Sheet sheet = null;
		
		try {
			if (progressBean.getFileData().getOriginalFilename().endsWith("xls")) {
				workbook = new HSSFWorkbook(bis);
				//Get first/desired sheet from the workbook
				sheet = (HSSFSheet) workbook.getSheetAt(0);
			} else if (progressBean.getFileData().getOriginalFilename().endsWith("xlsx")) {
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
					row.getCell(SAPID_INDEX, Row.CREATE_NULL_AS_BLANK).setCellType(Cell.CELL_TYPE_STRING);
					ProgressDetailsBean bean = new ProgressDetailsBean();
					Date date = null;
					String sapid = row.getCell(SAPID_INDEX, Row.CREATE_NULL_AS_BLANK).getStringCellValue();
					Cell dateCell = row.getCell(ACTIVATION_DATE_INDEX);
					String activationDate = "";
					
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
					
					SimpleDateFormat sdfDate = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");  
					activationDate = sdfDate.format(date);
					Calendar calendar = Calendar.getInstance();
					calendar.setTime(date);
					calendar.add(Calendar.DATE, 15);
					String piStartDate = sdfDate.format(calendar.getTime());
					calendar.add(Calendar.DATE, 30);
					String piEndDate = sdfDate.format(calendar.getTime());
					String entitlementId = progressBean.getEntitlementId();
					
					bean.setSapid(sapid);
					bean.setActivationDate(activationDate);
					bean.setActivated(true);
					bean.setEntitlementId(entitlementId);
					bean.setPiStartDate(piStartDate);
					bean.setPiEndDate(piEndDate);
					bean.setPackageId(packageId);
					
					if(bean.isErrorRecord()){
						errorBeanList.add(bean);
					}else{
						progressList.add(bean);
					}
					
				}
			}
			
		} catch (IOException e) {
			logger.info("exception : "+e.getMessage());
		}
		
		resultList.add(progressList);
		resultList.add(errorBeanList);
		
		return resultList;
	}
	
	
	public void readInterviewAvailabilityExcel(
		InterviewBean fileBean, ArrayList<String> facultyList, String userId, 
		List<InterviewBean> interviewDateList, List<InterviewBean> errorBeanList
	){

		int  FACULTY_ID_INDEX = 0;
		int  AVAILABILITY_DATE_INDEX = 1;
		int  START_TIME_INDEX = 2;
		int  END_TIME_INDEX = 3;
		
		ByteArrayInputStream bis = new ByteArrayInputStream(fileBean.getFileData().getBytes());
		Workbook workbook;
		SimpleDateFormat sdfTime = new java.text.SimpleDateFormat("HH:mm:ss");
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
					
					row.getCell(FACULTY_ID_INDEX, Row.CREATE_NULL_AS_BLANK).setCellType(Cell.CELL_TYPE_STRING);
					InterviewBean bean = new InterviewBean();


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
					Date endTime = null;
					
					Cell startTimeCell = row.getCell(START_TIME_INDEX);
					Cell endTimeCell = row.getCell(END_TIME_INDEX);
					
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
					
					if(bean.isErrorRecord()){
						errorBeanList.add(bean);
						continue;
					}

					SimpleDateFormat sdfDate = new SimpleDateFormat("yyyy-MM-dd");
					String AvailabilityDate = sdfDate.format(date);

					bean.setFacultyId(facultyId);
					bean.setDate(AvailabilityDate);
					bean.setStartTime(sdfTime.format(startTime));
					bean.setEndTime(sdfTime.format(endTime));
					bean.setCreatedBy(userId);
					bean.setLastModifiedBy(userId);

//					if(!facultyList.contains(bean.getFacultyId().trim())){
//						bean.setErrorMessage(bean.getErrorMessage()+" Row : "+ (i+1) +" Invalid Faculty ID : "+bean.getFacultyId().trim());
//						bean.setErrorRecord(true);
//					}


					if(bean.isErrorRecord()){
						errorBeanList.add(bean);
					}else{
						interviewDateList.add(bean);
					}

				}
			}

		} catch (IOException e) {
			logger.info("exception : "+e.getMessage());
		}
		return;
	}

	public void readCounsellingAvailabilityExcel( CounsellingBean fileBean, ArrayList<String> facultyList, 
			List<CounsellingBean> counsellingDateList, List<CounsellingBean> errorBeanList ){

		int  FACULTY_ID_INDEX = 0;
		int  AVAILABILITY_DATE_INDEX = 1;
		int  START_TIME_INDEX = 2;
		int  END_TIME_INDEX = 3;

		ByteArrayInputStream bis = new ByteArrayInputStream(fileBean.getFileData().getBytes());
		Workbook workbook;
		SimpleDateFormat sdfTime = new java.text.SimpleDateFormat("HH:mm:ss");
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

					row.getCell(FACULTY_ID_INDEX, Row.CREATE_NULL_AS_BLANK).setCellType(Cell.CELL_TYPE_STRING);
					CounsellingBean bean = new CounsellingBean();


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
					Date endTime = null;

					Cell startTimeCell = row.getCell(START_TIME_INDEX);
					Cell endTimeCell = row.getCell(END_TIME_INDEX);

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

					if(bean.isErrorRecord()){
						errorBeanList.add(bean);
						continue;
					}

					SimpleDateFormat sdfDate = new SimpleDateFormat("yyyy-MM-dd");
					String AvailabilityDate = sdfDate.format(date);

					bean.setFacultyId(facultyId);
					bean.setDate(AvailabilityDate);
					bean.setStartTime(sdfTime.format(startTime));
					bean.setEndTime(sdfTime.format(endTime));
					bean.setCreatedBy( fileBean.getUserId() );
					bean.setLastModifiedBy( fileBean.getUserId() );

					if(bean.isErrorRecord()){
						errorBeanList.add(bean);
					}else{
						counsellingDateList.add(bean);
					}

				}
			}

		} catch (IOException e) {
			logger.info("exception : "+e.getMessage());
		}
		return;
	}
}
