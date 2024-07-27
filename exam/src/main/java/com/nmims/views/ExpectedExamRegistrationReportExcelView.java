package com.nmims.views;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;


/*import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;*/
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.util.CellRangeAddress;
/*import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;*/
import org.springframework.context.ApplicationContextAware;
/*import org.springframework.web.servlet.view.document.AbstractExcelView;*/
import org.springframework.web.servlet.view.document.AbstractXlsxStreamingView;

import com.nmims.beans.CenterExamBean;
import com.nmims.beans.PassFailExamBean;
import com.nmims.beans.StudentExamBean;

public class ExpectedExamRegistrationReportExcelView extends AbstractXlsxStreamingView  implements ApplicationContextAware{

	@Override
	protected void buildExcelDocument(Map model, Workbook workbook,	HttpServletRequest request, HttpServletResponse response)
			throws Exception {

		/*ArrayList<Object> data =  (ArrayList<Object>)model.get("data");

		ArrayList<StudentBean> allStudentsList =  (ArrayList<StudentBean>)data.get(0);
		TreeMap<String, Integer> sortedSubjectCityStudentCountMap = (TreeMap<String, Integer>)data.get(1);

		//create a wordsheet
		Sheet sheet = workbook.createSheet("Subject-City-Count");
		int index = 0;
		Row header = sheet.createRow(0);
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
			HSSFRow row = sheet.createRow(rowNum++);

			row.createCell(index++).setCellValue(rowNum-1);
			row.createCell(index++).setCellValue(subject);
			row.createCell(index++).setCellValue(city);
			row.createCell(index++).setCellValue(new Double(studentCount));

		}
		sheet.setAutoFilter(CellRangeAddress.valueOf("A1:D"+rowNum));


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
			Row row = sheet.createRow(rowNum++);
			StudentBean bean = allStudentsList.get(i);

			row.createCell(index++).setCellValue(rowNum-1);
			row.createCell(index++).setCellValue(bean.getSubject());
			row.createCell(index++).setCellValue(new Double(bean.getSapid()));
			row.createCell(index++).setCellValue(bean.getCity());
		}

		sheet.setAutoFilter(CellRangeAddress.valueOf("A1:D"+rowNum));

	}*/

		int run = 0;
		//ArrayList<Object> data = studentAndCenterCountObject;
		ArrayList<Object> data = (ArrayList<Object>) model.get("data");
		ArrayList<StudentExamBean> allStudentsList =  (ArrayList<StudentExamBean>)data.get(0);
		TreeMap<String, Integer> sortedSubjectCityStudentCountMap = (TreeMap<String, Integer>)data.get(1);

		//create a worksheet
		
		Sheet sheet = workbook.createSheet("Subject-City-Count");
		int index = 0;
		Row header = sheet.createRow(0);
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
			Row row = sheet.createRow(rowNum++);

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
			Row row = sheet.createRow(rowNum++);
			StudentExamBean bean = allStudentsList.get(i);

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
		}}
