package com.nmims.helpers;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.regex.Pattern;

import org.apache.commons.lang.StringUtils;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.web.multipart.MultipartFile;

import com.nmims.beans.DocumentBean;
import com.nmims.beans.DispatchedDocumentMergeResponseBean;
import com.nmims.beans.FedExMergerBean;

public class ExcelHelper {
	
	private static final String URL_FORMAT = "^(https?://)(.[a-zA-z0-9-]+).([a-zA-z0-9]+){1,7}.*([a-zA-z0-9]+)$";
	
	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}
	
	public DocumentBean readFedExInValidPinCodeList(DocumentBean fileBean) throws IOException{
		ByteArrayInputStream bis = new ByteArrayInputStream(fileBean.getFile().getBytes());
		Workbook workbook;
		int PINCODE_INDEX = 0;
		
		ArrayList<String> fedExInValidPincodeList = new ArrayList<String>();
		try{
			if (fileBean.getFile().getOriginalFilename().endsWith("xls")) {
				workbook = new HSSFWorkbook(bis);
			} else if (fileBean.getFile().getOriginalFilename().endsWith("xlsx")) {
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
				
				if(row!=null){
					row.getCell(PINCODE_INDEX, Row.CREATE_NULL_AS_BLANK).setCellType(Cell.CELL_TYPE_STRING);
					String pincode = row.getCell(PINCODE_INDEX, Row.CREATE_NULL_AS_BLANK).getStringCellValue();
					
					if(!pincode.isEmpty() && pincode !=null && !"".equals(pincode)){
						fedExInValidPincodeList.add(pincode);
					}
					if(i % 1000 == 0){
						System.out.println("Read "+i+" rows");
					}
				}
			}	
		}catch(Exception e){
			e.printStackTrace();
		}
		fileBean.setFedExInValidPinCodeList(fedExInValidPincodeList);
		return fileBean;
	}

	public DispatchedDocumentMergeResponseBean readDispatchOrderDocumentMergeExcel(MultipartFile file) throws IOException {
		Integer URL=0;
		
		List<FedExMergerBean> successList = new ArrayList<FedExMergerBean>();
		List<FedExMergerBean> errorList = new ArrayList<FedExMergerBean>();
		DispatchedDocumentMergeResponseBean mergerBean = new DispatchedDocumentMergeResponseBean();
		
		ByteArrayInputStream bis = new ByteArrayInputStream(file.getBytes());
		Workbook workbook;
		try {
			if (file.getOriginalFilename().endsWith("xls")) {
				workbook = new HSSFWorkbook(bis);
			} else if (file.getOriginalFilename().endsWith("xlsx")) {
				workbook = new XSSFWorkbook(bis);
			} else {
				errorList.add(getMergerErrorBean("Received file does not have a standard excel extension."));
				mergerBean.setErrorList(errorList);
				
				return mergerBean;
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
				FedExMergerBean successBean = new FedExMergerBean();
				
				try {
					if(row!=null){
						
						//setting type of cell to String
						row.getCell(URL, Row.CREATE_NULL_AS_BLANK).setCellType(Cell.CELL_TYPE_STRING);
						
						//Reading values from the cell
						String url = row.getCell(URL, Row.CREATE_NULL_AS_BLANK).getStringCellValue().trim();
						
						//skip the row if URL field is blank
						if(StringUtils.isBlank(url)) 
							continue;
						
						//check URL format 
						if(!Pattern.matches(URL_FORMAT, url))
							errorList.add(getMergerErrorBean("Invalid Url on row no : "+rowNumber));
						
						successBean.setUrl(url);
						successBean.setRow(rowNumber);
						successList.add(successBean);
				} 
			}catch (Exception e) {
				errorList.add(getMergerErrorBean("Invalid cell inputs, please enter values only in Text format"));
				continue;
				}
			}
		}
		catch (Exception e) {
			errorList.add(getMergerErrorBean("Error in inserting rows from excel file"));
			mergerBean.setErrorList(errorList);
			
			return mergerBean;
		}
		
		//checks for excel file cannot be blank
		if(!(successList.size() > 0) && !(errorList.size() > 0)) {
			errorList.add(getMergerErrorBean("Excel file cannot be blank, please insert data and try again"));
			mergerBean.setErrorList(errorList);
			
			return mergerBean;
		}
		mergerBean.setSuccessList(successList);
		mergerBean.setErrorList(errorList);
		
		return mergerBean;
	}

	private FedExMergerBean getMergerErrorBean(String errorMessage) {
		FedExMergerBean errorBean = new FedExMergerBean();
		errorBean.setErrorMessage(errorMessage);
		
		return errorBean;
	}
}
