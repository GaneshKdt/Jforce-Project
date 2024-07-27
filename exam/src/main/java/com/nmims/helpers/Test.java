package com.nmims.helpers;

import java.io.File;
import java.io.FileInputStream;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;









import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.RandomStringUtils;
import org.apache.commons.lang.StringEscapeUtils;
import org.apache.poi.hssf.record.crypto.Biff8EncryptionKey;
import org.apache.poi.hssf.usermodel.HSSFDateUtil;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.poifs.crypt.Decryptor;
import org.apache.poi.poifs.crypt.EncryptionInfo;
import org.apache.poi.poifs.filesystem.NPOIFSFileSystem;
import org.apache.poi.ss.formula.functions.Vlookup;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.DateUtil;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import com.nmims.beans.BaseExamBean;
import com.nmims.beans.StudentExamBean;

public class Test {

	public static void main(String[] args) throws ParseException {
		ArrayList<List> resultList = new ArrayList<>();
		int SRNO = 0;
		int SAPID = 1;
		int nameOfStudent = 2;
		int month = 3;
		int year = 4;
		int semester = 5;
		int program = 6;
		int issued = 7;
		try
        {
            FileInputStream file = new FileInputStream(new File("D:/Marksheet_data_prior_Dec_2015.xlsx"));
 
            //Create Workbook instance holding reference to .xlsx file
            XSSFWorkbook workbook = new XSSFWorkbook(file);
 
            //Get first/desired sheet from the workbook
            XSSFSheet sheet = workbook.getSheetAt(0);
 
            //Iterate through each rows one by one
            Iterator<Row> rowIterator = sheet.iterator();
            //Skip first row since it has no data
            if(rowIterator.hasNext()){
				Row row = rowIterator.next();
			}
            while (rowIterator.hasNext()) 
            {
                Row row = rowIterator.next();
                //For each row, iterate through all the columns
                	if(row!=null){
                		row.getCell(SRNO, Row.CREATE_NULL_AS_BLANK).setCellType(Cell.CELL_TYPE_STRING);
    					row.getCell(SAPID, Row.CREATE_NULL_AS_BLANK).setCellType(Cell.CELL_TYPE_STRING);
    					row.getCell(nameOfStudent, Row.CREATE_NULL_AS_BLANK).setCellType(Cell.CELL_TYPE_STRING);
    					row.getCell(month, Row.CREATE_NULL_AS_BLANK).setCellType(Cell.CELL_TYPE_STRING);
    					row.getCell(year, Row.CREATE_NULL_AS_BLANK).setCellType(Cell.CELL_TYPE_STRING);
    					row.getCell(semester, Row.CREATE_NULL_AS_BLANK).setCellType(Cell.CELL_TYPE_STRING);
    					row.getCell(program, Row.CREATE_NULL_AS_BLANK).setCellType(Cell.CELL_TYPE_STRING);
    					row.getCell(issued, Row.CREATE_NULL_AS_BLANK).setCellType(Cell.CELL_TYPE_STRING);
    					
    					String srNumber = row.getCell(SRNO, Row.CREATE_NULL_AS_BLANK).getStringCellValue();
    					String sapid = row.getCell(SAPID, Row.CREATE_NULL_AS_BLANK).getStringCellValue();
    					String studentName = row.getCell(nameOfStudent, Row.CREATE_NULL_AS_BLANK).getStringCellValue();
    					String m = row.getCell(month, Row.CREATE_NULL_AS_BLANK).getStringCellValue();
    					String y = row.getCell(year, Row.CREATE_NULL_AS_BLANK).getStringCellValue();
    					String sem = row.getCell(semester, Row.CREATE_NULL_AS_BLANK).getStringCellValue();
    					String programm = row.getCell(program, Row.CREATE_NULL_AS_BLANK).getStringCellValue();
    					String issuedd = row.getCell(issued, Row.CREATE_NULL_AS_BLANK).getStringCellValue();
    					
                	}
    					
                
            }
            file.close();
        } 
        catch (Exception e) 
        {
            
        }

	}
	
	public static <E extends BaseExamBean> void printNumber(E bean){
		
		
		String sapid = bean.getSapid();
		
	}

}
