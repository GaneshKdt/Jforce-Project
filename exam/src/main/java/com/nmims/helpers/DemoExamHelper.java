package com.nmims.helpers;

import java.io.ByteArrayInputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;

import com.nmims.beans.DemoExamBean;


@Service("DemoExamHelper")
public class DemoExamHelper {
	
	public List<DemoExamBean> readExcelSheet(HttpServletRequest request,DemoExamBean demoExamBean){
		int SUBJECT_ID = 0;
		int SUBJECT_NAME = 1;
		int LINK = 2;
		
		ByteArrayInputStream bis = new ByteArrayInputStream(demoExamBean.getFileData().getBytes());
		Workbook workbook;
		
		List<DemoExamBean> demoExamBeansList = new ArrayList<DemoExamBean>();
		try {
			if (demoExamBean.getFileData().getOriginalFilename().endsWith("xls")) {
				workbook = new HSSFWorkbook(bis);
			} else if (demoExamBean.getFileData().getOriginalFilename().endsWith("xlsx")) {
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
				DemoExamBean tmp_demoexambean = new DemoExamBean();

				if(row!=null){
					row.getCell(SUBJECT_ID, Row.CREATE_NULL_AS_BLANK).setCellType(Cell.CELL_TYPE_STRING);
					row.getCell(SUBJECT_NAME, Row.CREATE_NULL_AS_BLANK).setCellType(Cell.CELL_TYPE_STRING);
					row.getCell(LINK, Row.CREATE_NULL_AS_BLANK).setCellType(Cell.CELL_TYPE_STRING);
					
					String subject_name = row.getCell(SUBJECT_NAME, Row.CREATE_NULL_AS_BLANK).getStringCellValue();
					//-------
					String[] subject_name_tmp_array = subject_name.split(" ");
					int len = subject_name_tmp_array.length - 1;
					String[] newArray=new String[len];
					System.arraycopy(subject_name_tmp_array,1,newArray,0,len);
					len = newArray.length - 1;
					subject_name_tmp_array=new String[len];
					System.arraycopy(newArray,0,subject_name_tmp_array,0,len);
					subject_name = Arrays.toString(subject_name_tmp_array);
					subject_name = subject_name.substring(1, subject_name.length()-1).replace(",", "");
					//------
					String link = row.getCell(LINK, Row.CREATE_NULL_AS_BLANK).getStringCellValue();
					String subject_code = row.getCell(SUBJECT_ID, Row.CREATE_NULL_AS_BLANK).getStringCellValue();
					tmp_demoexambean.setSubject(subject_name);
					tmp_demoexambean.setLink(link);
					tmp_demoexambean.setSubject_code(subject_code);
					demoExamBeansList.add(tmp_demoexambean);
				}
			}
			return demoExamBeansList;
		}
		catch (Exception e) {
			// TODO: handle exception
			
			request.setAttribute("errorMessage", "Error: " + e.getMessage());
			request.setAttribute("error", "true");
			return demoExamBeansList;
		}
	}
	

}
