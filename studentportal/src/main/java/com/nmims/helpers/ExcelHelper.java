package com.nmims.helpers;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.multipart.MultipartFile;

import com.nmims.beans.FileStudentPortalBean;
import com.nmims.beans.MassUploadTrackingSRBean;
import com.nmims.beans.ModuleContentStudentPortalBean;
import com.nmims.beans.WalletBean;
import com.nmims.beans.programPreference;
import com.nmims.daos.PortalDao;


public class ExcelHelper {
	
	private static final Logger loggerForEmails = LoggerFactory.getLogger("bulkEmailFromExcel");
	
	private static final Logger logger = LoggerFactory.getLogger(ExcelHelper.class);
	
	private static final String URL_FORMAT = "^(https?://)*[w|W]{3}(.[a-zA-z0-9-]+).([a-zA-z0-9]+){1,7}.*([a-zA-z0-9]+)$";

	public ArrayList<List> readProgramPreferenceExcel(FileStudentPortalBean fileBean){
		int STUDENT_NAME_INDEX = 0;
		int Mobile_NO_INDEX = 1;
		int Email_INDEX = 2;
		int REG_NO_INDEX = 3;
		int PREFERENCE_1_INDEX = 4;
		int PREFERENCE_2_INDEX = 5;
		int PREFERENCE_3_INDEX = 6;
		int PREFERENCE_4_INDEX = 7;
		int PREFERENCE_5_INDEX = 8;
		int PREFERENCE_6_INDEX = 9;
		int PREFERENCE_7_INDEX = 10;
		
		int LATEST_PROGRAM_CATEGORY_INDEX = 11;
		
		ByteArrayInputStream bis = new ByteArrayInputStream(fileBean.getFileData().getBytes());
		Workbook workbook;
		List<programPreference> preferenceList = new ArrayList<programPreference>();

		ArrayList<List> resultList = new ArrayList<>();
		List<programPreference> preferenceBeanList = new ArrayList<programPreference>();
		List<programPreference> errorBeanList = new ArrayList<programPreference>();
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
					row.getCell(STUDENT_NAME_INDEX, Row.CREATE_NULL_AS_BLANK).setCellType(Cell.CELL_TYPE_STRING);
					row.getCell(Mobile_NO_INDEX, Row.CREATE_NULL_AS_BLANK).setCellType(Cell.CELL_TYPE_STRING);
					row.getCell(Email_INDEX, Row.CREATE_NULL_AS_BLANK).setCellType(Cell.CELL_TYPE_STRING);
					row.getCell(REG_NO_INDEX, Row.CREATE_NULL_AS_BLANK).setCellType(Cell.CELL_TYPE_STRING);
					row.getCell(PREFERENCE_1_INDEX, Row.CREATE_NULL_AS_BLANK).setCellType(Cell.CELL_TYPE_STRING);
					row.getCell(PREFERENCE_2_INDEX, Row.CREATE_NULL_AS_BLANK).setCellType(Cell.CELL_TYPE_STRING);
					row.getCell(PREFERENCE_3_INDEX, Row.CREATE_NULL_AS_BLANK).setCellType(Cell.CELL_TYPE_STRING);
					row.getCell(PREFERENCE_4_INDEX, Row.CREATE_NULL_AS_BLANK).setCellType(Cell.CELL_TYPE_STRING);
					row.getCell(PREFERENCE_5_INDEX, Row.CREATE_NULL_AS_BLANK).setCellType(Cell.CELL_TYPE_STRING);
					row.getCell(PREFERENCE_6_INDEX, Row.CREATE_NULL_AS_BLANK).setCellType(Cell.CELL_TYPE_STRING);
					row.getCell(PREFERENCE_7_INDEX, Row.CREATE_NULL_AS_BLANK).setCellType(Cell.CELL_TYPE_STRING);
					row.getCell(LATEST_PROGRAM_CATEGORY_INDEX, Row.CREATE_NULL_AS_BLANK).setCellType(Cell.CELL_TYPE_STRING);

					programPreference bean = new programPreference();

					String studentName = row.getCell(STUDENT_NAME_INDEX, Row.CREATE_NULL_AS_BLANK).getStringCellValue();
					String mobileNo = row.getCell(Mobile_NO_INDEX, Row.CREATE_NULL_AS_BLANK).getStringCellValue();
					String email = row.getCell(Email_INDEX, Row.CREATE_NULL_AS_BLANK).getStringCellValue();
					String regNo = row.getCell(REG_NO_INDEX, Row.CREATE_NULL_AS_BLANK).getStringCellValue();
					String preference1 = row.getCell(PREFERENCE_1_INDEX, Row.CREATE_NULL_AS_BLANK).getStringCellValue();
					String preference2 = row.getCell(PREFERENCE_2_INDEX, Row.CREATE_NULL_AS_BLANK).getStringCellValue();
					String preference3 = row.getCell(PREFERENCE_3_INDEX, Row.CREATE_NULL_AS_BLANK).getStringCellValue();
					String preference4 = row.getCell(PREFERENCE_4_INDEX, Row.CREATE_NULL_AS_BLANK).getStringCellValue();
					String preference5 = row.getCell(PREFERENCE_5_INDEX, Row.CREATE_NULL_AS_BLANK).getStringCellValue();
					String preference6 = row.getCell(PREFERENCE_6_INDEX, Row.CREATE_NULL_AS_BLANK).getStringCellValue();
					String preference7 = row.getCell(PREFERENCE_7_INDEX, Row.CREATE_NULL_AS_BLANK).getStringCellValue();
					String latestProgramCategory = row.getCell(LATEST_PROGRAM_CATEGORY_INDEX, Row.CREATE_NULL_AS_BLANK).getStringCellValue();
					
					bean.setStudentName(studentName);
					bean.setMobileNo(mobileNo);
					bean.setEmail(email);
					bean.setRegNo(regNo);
					bean.setPreference1(preference1);
					bean.setPreference2(preference2);
					bean.setPreference3(preference3);
					bean.setPreference4(preference4);
					bean.setPreference5(preference5);
					bean.setPreference6(preference6);
					bean.setPreference7(preference7);
					
					bean.setLatestProgramCategory(latestProgramCategory);
					preferenceBeanList.add(bean);
				}
				
				System.out.println("Total Records = "+(errorBeanList.size()+preferenceBeanList.size()));
				System.out.println("Error Records = "+errorBeanList.size());
				System.out.println("Valid Records = "+preferenceBeanList.size());
			}
		}catch (IOException e) {
			//e.printStackTrace();
		}
		resultList.add(preferenceBeanList);
		resultList.add(errorBeanList);
		return resultList;
	}

	public ArrayList<List> readUsersExcel(FileStudentPortalBean fileBean){


		int LAST_NAME_INDEX = 0;
		int FIRST_NAME_INDEX = 1;
		int EMAIL_INDEX = 2;
		int USERID_INDEX = 3;
		int PROGRAM_INDEX = 4;
		int MOBILE_INDEX = 5;
		int PASSWORD_INDEX = 6;
		int ALT_PHONE_INDEX = 7;
		int ADDRESS_INDEX = 8;

		System.out.println("FileBen = "+fileBean);
		System.out.println("Data = "+fileBean.getFileData());
		ByteArrayInputStream bis = new ByteArrayInputStream(fileBean.getFileData().getBytes());
		Workbook workbook;
		List<PersonStudentPortalBean> usersList = new ArrayList<PersonStudentPortalBean>();

		ArrayList<List> resultList = new ArrayList<>();
		List<PersonStudentPortalBean> studentBeanList = new ArrayList<PersonStudentPortalBean>();
		List<PersonStudentPortalBean> errorBeanList = new ArrayList<PersonStudentPortalBean>();
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
					row.getCell(LAST_NAME_INDEX, Row.CREATE_NULL_AS_BLANK).setCellType(Cell.CELL_TYPE_STRING);
					row.getCell(FIRST_NAME_INDEX, Row.CREATE_NULL_AS_BLANK).setCellType(Cell.CELL_TYPE_STRING);
					row.getCell(EMAIL_INDEX, Row.CREATE_NULL_AS_BLANK).setCellType(Cell.CELL_TYPE_STRING);
					row.getCell(USERID_INDEX, Row.CREATE_NULL_AS_BLANK).setCellType(Cell.CELL_TYPE_STRING);
					row.getCell(PROGRAM_INDEX, Row.CREATE_NULL_AS_BLANK).setCellType(Cell.CELL_TYPE_STRING);
					row.getCell(MOBILE_INDEX, Row.CREATE_NULL_AS_BLANK).setCellType(Cell.CELL_TYPE_STRING);
					row.getCell(PASSWORD_INDEX, Row.CREATE_NULL_AS_BLANK).setCellType(Cell.CELL_TYPE_STRING);
					row.getCell(ALT_PHONE_INDEX, Row.CREATE_NULL_AS_BLANK).setCellType(Cell.CELL_TYPE_STRING);
					row.getCell(ADDRESS_INDEX, Row.CREATE_NULL_AS_BLANK).setCellType(Cell.CELL_TYPE_STRING);

					PersonStudentPortalBean bean = new PersonStudentPortalBean();

					String lastName = row.getCell(LAST_NAME_INDEX, Row.CREATE_NULL_AS_BLANK).getStringCellValue();
					String firstName = row.getCell(FIRST_NAME_INDEX, Row.CREATE_NULL_AS_BLANK).getStringCellValue();
					String email = row.getCell(EMAIL_INDEX, Row.CREATE_NULL_AS_BLANK).getStringCellValue();
					String userId = row.getCell(USERID_INDEX, Row.CREATE_NULL_AS_BLANK).getStringCellValue();
					String program = row.getCell(PROGRAM_INDEX, Row.CREATE_NULL_AS_BLANK).getStringCellValue();
					String mobile = row.getCell(MOBILE_INDEX, Row.CREATE_NULL_AS_BLANK).getStringCellValue();
					String password = row.getCell(PASSWORD_INDEX, Row.CREATE_NULL_AS_BLANK).getStringCellValue();
					String altPhone = row.getCell(ALT_PHONE_INDEX, Row.CREATE_NULL_AS_BLANK).getStringCellValue();
					String postalAddress = row.getCell(ADDRESS_INDEX, Row.CREATE_NULL_AS_BLANK).getStringCellValue();

					if("".equals(lastName.trim())){
						lastName = ".";
					}
					if("".equals(firstName.trim())){
						firstName = ".";
					}
					/*if("".equals(email.trim())){
						email = "Not Available";
					}*/
					if("".equals(program.trim())){
						program = ".";
					}
					/*if("".equals(mobile.trim())){
						mobile = "Not Available";
					}
					if("".equals(altPhone.trim())){
						altPhone = "Not Available";
					}
					if("".equals(postalAddress.trim())){
						postalAddress = "Not Available";
					}*/


					if("".equals(userId.trim()) && "".equals(password.trim()) && "".equals(program)){
						break;
					}

					bean.setLastName(lastName);
					bean.setFirstName(firstName);
					bean.setEmail(email);
					bean.setUserId(userId);
					bean.setProgram(program);
					bean.setContactNo(mobile);
					bean.setPassword(password);
					bean.setAltContactNo(altPhone);
					bean.setPostalAddress(postalAddress);


					if("".equals(bean.getUserId().trim())){
						bean.setErrorMessage(bean.getErrorMessage()+" Row : "+ (i+1) +" Blank User Id.");
						bean.setErrorRecord(true);
					}

					if("".equals(bean.getPassword().trim())){
						bean.setErrorMessage(bean.getErrorMessage()+" Row : "+ (i+1) +" Blank Password.");
						bean.setErrorRecord(true);
					}

					if(bean.isErrorRecord()){
						errorBeanList.add(bean);
					}else{
						studentBeanList.add(bean);
					}


					if(i % 1000 == 0){
						System.out.println("Read "+i+" rows");
					}
				}
			}
			System.out.println("Total Records = "+(errorBeanList.size()+studentBeanList.size()));
			System.out.println("Error Records = "+errorBeanList.size());
			System.out.println("Valid Records = "+studentBeanList.size());
		} catch (IOException e) {
			//e.printStackTrace();
		}
		resultList.add(studentBeanList);
		resultList.add(errorBeanList);
		return resultList;
	}


	public ArrayList<List> readEmailsExcel(MultipartFile file) throws IOException{

		
		int SAPID_INDEX = 0;
		int EMAIL_INDEX = 1;
		ByteArrayInputStream bis = new ByteArrayInputStream(file.getBytes());
		Workbook workbook;

		ArrayList<List> resultList = new ArrayList<>();
		List<String> emailList = new ArrayList<String>();
		List<String> sapIdList = new ArrayList<String>();
		List<String> errorList = new ArrayList<String>();
		try {
			if (file.getOriginalFilename().endsWith("xls")) {
				workbook = new HSSFWorkbook(bis);
			} else if (file.getOriginalFilename().endsWith("xlsx")) {
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
					row.getCell(EMAIL_INDEX, Row.CREATE_NULL_AS_BLANK).setCellType(Cell.CELL_TYPE_STRING);
					row.getCell(SAPID_INDEX, Row.CREATE_NULL_AS_BLANK).setCellType(Cell.CELL_TYPE_STRING);
					
					String email = row.getCell(EMAIL_INDEX, Row.CREATE_NULL_AS_BLANK).getStringCellValue();
					String sapid = row.getCell(SAPID_INDEX, Row.CREATE_NULL_AS_BLANK).getStringCellValue();
					
					if(StringUtils.isBlank(email)){
						errorList.add(" Row : "+ (i+1) +"Blank Email ID: ");
					}
					if(StringUtils.isBlank(sapid)){
						errorList.add(" Row : "+ (i+1) +"Blank SAPID: ");
					}
					
					if(StringUtils.isBlank(sapid) && StringUtils.isBlank(email)){
						break;
					}
					
					if(email.indexOf("@") == -1){
						errorList.add(" Row : "+ (i+1) +"Invalid Email ID: "+email);
					}else{
						emailList.add(email);
					}
					
					if(sapid.length()==11){
						sapIdList.add(sapid);
					}
					
					if(i % 1000 == 0){
						System.out.println("Read "+i+" rows");
					}
				}
			}
			System.out.println("Total Records = "+(errorList.size()+emailList.size()));
			System.out.println("Error Records = "+errorList.size());
			System.out.println("Valid Records = "+emailList.size());
			
			loggerForEmails.info("Total Records = "+(errorList.size()+emailList.size()));
			loggerForEmails.info("Error Records = "+errorList.size());
			loggerForEmails.info("Valid Records = "+emailList.size());
			
		} catch (IOException e) {
			//e.printStackTrace();
		}
		resultList.add(sapIdList);
		resultList.add(emailList);
		resultList.add(errorList);
		return resultList;
	}

	public void buildWalletTransactionExcelReport(ArrayList<WalletBean> walletTransactionList,HttpServletResponse response,PortalDao pDao) throws IOException
	{
		/*HashMap<String, StudentBean> sapIdStudentsMap = pDao.getAllStudents();*/
		/*HashMap<String, CenterBean> icLcMap = dao.getICLCMap();*/
		
		//create a worksheet
		XSSFWorkbook workbook =new XSSFWorkbook();
		XSSFSheet sheet = workbook.createSheet("Wallet Transactions");
		int index = 0;
		XSSFRow header = sheet.createRow(0);
		
		header.createCell(0).setCellValue("Sr. No.");
		header.createCell(1).setCellValue("Student Number");
		header.createCell(2).setCellValue("User ID");
		header.createCell(3).setCellValue("Transaction Amount");
		header.createCell(4).setCellValue("Wallet Balance");
		header.createCell(5).setCellValue("Transaction Date");
		header.createCell(6).setCellValue("Transaction Description");
		header.createCell(7).setCellValue("Type");
		header.createCell(8).setCellValue("Transcation ID");
 
		int rowNum = 1;
		for (int i = 0 ; i < walletTransactionList.size(); i++) {
			WalletBean walletBean = walletTransactionList.get(i);
			
			
			//create the row data
			XSSFRow row = sheet.createRow(rowNum++);
			
			row.createCell(0).setCellValue(rowNum);
			row.createCell(1).setCellValue(walletBean.getCreatedBy());
			row.createCell(2).setCellValue(walletBean.getUserId());
			row.createCell(3).setCellValue(walletBean.getAmount());
			row.createCell(4).setCellValue(walletBean.getWalletBalance());
			row.createCell(5).setCellValue(walletBean.getCreatedDate());
			row.createCell(6).setCellValue(walletBean.getDescription());
			if("Finance".equals(walletBean.getUserId())){
				row.createCell(7).setCellValue("Refund By Finanace");
			}else{
				row.createCell(7).setCellValue("Transaction by "+walletBean.getUserId());
			}
			row.createCell(8).setCellValue(walletBean.getTid());
		}
		
		
		ByteArrayOutputStream outByteStream = new ByteArrayOutputStream();
		workbook.write(outByteStream);
		byte [] outArray = outByteStream.toByteArray();
		response.setContentType("application/ms-excel");
		response.setContentLength(outArray.length);
		response.setHeader("Expires:", "0"); // eliminates browser caching
		response.setHeader("Content-Disposition", "attachment; filename=WalletTransaction.xls");
		ServletOutputStream outStream = response.getOutputStream();
		outStream.write(outArray);
		outStream.flush();
		//End//
	}
	
	private void buildExcelDocument(Map model, HSSFWorkbook workbook,	HttpServletRequest request, HttpServletResponse response)
			throws Exception {
		
		HSSFSheet sheet = workbook.createSheet("Test");
		int index = 0;
		
		CellStyle style = workbook.createCellStyle();
		style.setAlignment(CellStyle.ALIGN_CENTER);
		style.setWrapText(true);
		//cell.setCellStyle(style);
		
		HSSFRow header = sheet.createRow(0);
		createCell(workbook, header, (short)index++,"Sr.No");
		createCell(workbook, header, (short)index++,"SEM1");
	
		ArrayList<String> lstSubject = new ArrayList<String>(
				Arrays.asList("Marketing Management","Business Management"));
		
		HSSFRow row = sheet.createRow(1);
		int rowNum = 1;
		index = 0;
		
		createCell(workbook, row, (short)0,"1");
		createCell(workbook, row, (short)1,"Business Management");
		createCell(workbook, row, (short)(3),"Marketing Management");
		
		HSSFRow rows = sheet.createRow(2);
		index = 1;
		for(int i=0;i<lstSubject.size();i++)
		{
			createCell(workbook, rows, (short)index++,"TEE");
			createCell(workbook, rows, (short)index++,"ASSIN");
		}
		//sheet.addMergedRegion(rowFrom,rowTo,colFrom,colTo);
		sheet.addMergedRegion(new CellRangeAddress(0,0,1,4));
		sheet.addMergedRegion(new CellRangeAddress(1,1,1,2));
		sheet.addMergedRegion(new CellRangeAddress(1,1,3,4));
	}
	private static void createCell(HSSFWorkbook workbook, HSSFRow row, short column,String cellValue) {
		@SuppressWarnings("deprecation")
		Cell cell = row.createCell(column);
		cell.setCellValue(cellValue);
		CellStyle cellStyle = workbook.createCellStyle();
		cellStyle.setAlignment(CellStyle.ALIGN_CENTER_SELECTION);
		cell.setCellStyle(cellStyle);
	}
	
	
	//Code to read module content excel sheet Start
	public ArrayList<List> readModuleContentExcel(FileStudentPortalBean fileBean, ArrayList<String> subjectList, String userId){
			
			List<ModuleContentStudentPortalBean> moduleContentBeanList = new ArrayList<ModuleContentStudentPortalBean>();
			List<ModuleContentStudentPortalBean> errorBeanList = new ArrayList<ModuleContentStudentPortalBean>();
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

					ModuleContentStudentPortalBean bean = new ModuleContentStudentPortalBean();
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
							System.out.println("Error while readExcel "+ bean.getModuleName());
							errorBeanList.add(bean);
						}else{
							moduleContentBeanList.add(bean);
						}

						if(i % 1000 == 0){
							System.out.println("Read "+i+" rows");
						}
					}
				}
			}catch(Exception e){
				//e.printStackTrace();
			}
			
			System.out.println("Total Records = "+(errorBeanList.size()+moduleContentBeanList.size()));
			System.out.println("Error Records = "+errorBeanList.size());
			System.out.println("Valid Records = "+moduleContentBeanList.size());
			
			resultList.add(moduleContentBeanList);
			resultList.add(errorBeanList);
			return resultList;
		}

		//Code to read module content excel sheet End

	//Code to read module video map excel sheet Start
		public ArrayList<List> readModuleVideoMapExcel(FileStudentPortalBean fileBean){
				
				List<ModuleContentStudentPortalBean> moduleContentBeanList = new ArrayList<ModuleContentStudentPortalBean>();
				List<ModuleContentStudentPortalBean> errorBeanList = new ArrayList<ModuleContentStudentPortalBean>();
				ArrayList<List> resultList = new ArrayList<>();

				int CHAPTER_NAME_INDEX = 0;
				int TOPIC_NAME_INDEX = 1;
				
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

						ModuleContentStudentPortalBean bean = new ModuleContentStudentPortalBean();
						if(row!=null){
							
							row.getCell(CHAPTER_NAME_INDEX, Row.CREATE_NULL_AS_BLANK).setCellType(Cell.CELL_TYPE_STRING);
							row.getCell(TOPIC_NAME_INDEX, Row.CREATE_NULL_AS_BLANK).setCellType(Cell.CELL_TYPE_STRING); 
							
												
							String chapterName = row.getCell(CHAPTER_NAME_INDEX, Row.CREATE_NULL_AS_BLANK).getStringCellValue();
							String topicName = row.getCell(TOPIC_NAME_INDEX, Row.CREATE_NULL_AS_BLANK).getStringCellValue(); 
							
							bean.setModuleName(chapterName);
							bean.setTitle(topicName);
							
							if(bean.isErrorRecord()){
								System.out.println("Error while readExcel with row : "+i+" Chapter Name : "+ bean.getModuleName()+" Topic Name : "+bean.getTitle());
								errorBeanList.add(bean);
							}else{
								moduleContentBeanList.add(bean);
							}

							if(i % 1000 == 0){
								System.out.println("Read "+i+" rows");
							}
						}
					}
				}catch(Exception e){
					//e.printStackTrace();
				}
				
				System.out.println("Total Records = "+(errorBeanList.size()+moduleContentBeanList.size()));
				System.out.println("Error Records = "+errorBeanList.size());
				System.out.println("Valid Records = "+moduleContentBeanList.size());
				
				resultList.add(moduleContentBeanList);
				resultList.add(errorBeanList);
				return resultList;
			}

			//Code to read module video map excel sheet End
		
		//Reading data from excel file
		public MassUploadTrackingSRBean readSRTrackingExcel(MultipartFile file) throws Exception {
			
			int SERVICE_REQUEST_ID=0;
			int TRACK_ID=1;
			int COURIER_NAME=2;
			int URL=3;
			
			List<MassUploadTrackingSRBean> successList = new ArrayList<MassUploadTrackingSRBean>();
			List<MassUploadTrackingSRBean> errorList = new ArrayList<MassUploadTrackingSRBean>();
			MassUploadTrackingSRBean trackingBean = new MassUploadTrackingSRBean();
			
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
				int rowNumber = 1;
				//Skip first row since it contains column names, not data.
				if(rowIterator.hasNext()){
					Row row = rowIterator.next();
				}
				while(rowIterator.hasNext()){
					rowNumber++;
					Row row = rowIterator.next();
					MassUploadTrackingSRBean successBean = new MassUploadTrackingSRBean();
					List<MassUploadTrackingSRBean> tempErrorList = new ArrayList<MassUploadTrackingSRBean>();
					try {
						
						if(row!=null){
							
							//setting type of cell to String
							
							row.getCell(SERVICE_REQUEST_ID, Row.CREATE_NULL_AS_BLANK).setCellType(Cell.CELL_TYPE_STRING);
							row.getCell(TRACK_ID, Row.CREATE_NULL_AS_BLANK).setCellType(Cell.CELL_TYPE_STRING);
							row.getCell(COURIER_NAME, Row.CREATE_NULL_AS_BLANK).setCellType(Cell.CELL_TYPE_STRING);
							row.getCell(URL, Row.CREATE_NULL_AS_BLANK).setCellType(Cell.CELL_TYPE_STRING);
							
							//Reading values from the cell
							
							String serviceRequestId = row.getCell(SERVICE_REQUEST_ID, Row.CREATE_NULL_AS_BLANK).getStringCellValue();
							String trackId = row.getCell(TRACK_ID, Row.CREATE_NULL_AS_BLANK).getStringCellValue();
							String courierName = row.getCell(COURIER_NAME, Row.CREATE_NULL_AS_BLANK).getStringCellValue().toLowerCase();
							String url = row.getCell(URL, Row.CREATE_NULL_AS_BLANK).getStringCellValue().toLowerCase();
							
							//skip the row if serviceRequestId, trackId, courierName, url field is blank
							if(StringUtils.isBlank(serviceRequestId) && StringUtils.isBlank(trackId) && StringUtils.isBlank(courierName) && StringUtils.isBlank(url)) 
								continue;
							
							//validating the tracking records for isBlank check
							tempErrorList = validateTrackingRecords(rowNumber, serviceRequestId, trackId, courierName, url);
							if(tempErrorList.size() > 0) {
								errorList.addAll(tempErrorList);
								continue;
							}
							successBean.setServiceRequestId(Integer.parseInt(serviceRequestId));
							successBean.setTrackId(trackId);
							successBean.setCourierName(courierName);
							
							//checking and adding valid url with prefix https:// if not present
							successBean.setUrl(formatTrackingUrl(url));
							successBean.setRow(rowNumber);
							
							successList.add(successBean);
					} 
				}catch (Exception e) {
					e.printStackTrace();
					errorList.add(getTrackingErrorBean("Records cannot be blank on row number "+rowNumber));
					logger.info("Failed to read data from Excel file on row number {} due to {} ", rowNumber,  e);
					continue;
					}
				}
			}
			catch (IllegalArgumentException ie) {
				logger.info("Error : "+ie.getMessage()+ "{} ", ie);
				throw new IllegalArgumentException(ie.getMessage());
			}
			catch (IllegalStateException se) {
				logger.info("Error : Invalid cell inputs, cell values not in Text/Number format due to {} ", se);
				throw new IllegalArgumentException("Invalid cell inputs, please enter values only in Text/Number format");
			}
			catch (Exception e) {
				logger.info("Failed to insert records from excel file due to {} ", e);
				throw new Exception("Error in inserting rows from excel file");
			}
			
			//checks for excel file cannot be blank
			if(!(successList.size() > 0) && !(errorList.size() > 0)) 
				throw new Exception("Excel file cannot be blank, please insert data and try again");
				
			trackingBean.setSuccessList(successList);
			trackingBean.setErrorList(errorList);
			
			return trackingBean;
			
		}

		/**
		 * validating and filtering the tracking records
		 * @param rowNumber
		 * @param serviceRequestId
		 * @param trackId
		 * @param courierName
		 * @param url	
		 * @return
		 * @throws Exception
		 */
		private List<MassUploadTrackingSRBean> validateTrackingRecords(int rowNumber, String serviceRequestId, String trackId, String courierName,
				String url) throws Exception {
			List<MassUploadTrackingSRBean> errorList = new ArrayList<MassUploadTrackingSRBean>(); 
			
			if(StringUtils.isBlank(serviceRequestId)) 
				errorList.add(getTrackingErrorBean("serviceRequestId cannot be blank on Row no : "+rowNumber));
			
			if(StringUtils.isBlank(trackId)) 
				errorList.add(getTrackingErrorBean("trackId cannot be blank on Row no : "+rowNumber));
				
			if(StringUtils.isBlank(courierName)) 
				errorList.add(getTrackingErrorBean("courierName cannot be blank on Row no : "+rowNumber));
		
			if(StringUtils.isBlank(url)) {
				errorList.add(getTrackingErrorBean("url cannot be blank on Row no : "+rowNumber));
			}
			
			if(StringUtils.isNotBlank(url) && !Pattern.matches(URL_FORMAT, url)) 
				errorList.add(getTrackingErrorBean("Invalid url on row no : "+rowNumber));
				
			return errorList;
		}

		private MassUploadTrackingSRBean getTrackingErrorBean(String errorMessage) {
			MassUploadTrackingSRBean errorBean = new MassUploadTrackingSRBean();
			errorBean.setErrorMessage(errorMessage);
			
			return errorBean;
		}
		
		private String formatTrackingUrl(String url) {
		  String prefix1 = "https://";
		  String prefix2 = "http://";
		  String validUrl;
		  
		  if(!url.startsWith(prefix1) && !url.startsWith(prefix2))
			  validUrl = prefix1+url;
		  else
			  validUrl = url;
		  
		  return validUrl;
		}
		
}
