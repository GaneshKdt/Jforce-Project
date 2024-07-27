package com.nmims.views;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.util.CellRangeAddress;
import org.springframework.context.ApplicationContextAware;
import org.springframework.web.servlet.view.document.AbstractExcelView;
import org.springframework.web.servlet.view.document.AbstractXlsxStreamingView;

import com.nmims.beans.ExamBookingTransactionBean;
import com.nmims.beans.ExamCenterBean;
import com.nmims.beans.ExamCenterSlotMappingBean;


public class ExamBookingStudentCountReportExcelView  extends AbstractXlsxStreamingView  implements ApplicationContextAware{
	
	@Override
	protected void buildExcelDocument(Map model, Workbook workbook,	HttpServletRequest request, HttpServletResponse response)
		throws Exception {
 
		List<ExamBookingTransactionBean> examBookingList = (List<ExamBookingTransactionBean>) model.get("examBookingList");
		//create a wordsheet
		
		HashMap<String, ArrayList<String>> perSubjectOnlineExamStudentsMap = new HashMap<>();
		HashMap<String, ArrayList<String>> perSubjectOfflineExamStudentsMap = new HashMap<>();
		
		HashMap<String, ArrayList<String>> perCityOnlineExamStudentsMap = new HashMap<>();
		HashMap<String, ArrayList<String>> perCityOfflineExamStudentsMap = new HashMap<>();
		
		HashMap<String, ArrayList<String>> perProgramOnlineExamStudentsMap = new HashMap<>();
		HashMap<String, ArrayList<String>> perProgramOfflineExamStudentsMap = new HashMap<>();
		
		HashMap<String, ArrayList<String>> perSemOfflineExamStudentsMap = new HashMap<>();
		HashMap<String, ArrayList<String>> perSemOnlineExamStudentsMap = new HashMap<>();
		
		for (int i = 0 ; i < examBookingList.size(); i++) {
			ExamBookingTransactionBean bean = examBookingList.get(i);
			String sapid = bean.getSapid();
			String program = bean.getProgram();
			String examMode = bean.getExamMode();
			String subject = bean.getSubject();
			String sem = bean.getSem();
			String city = bean.getCity();
			
			if("Offline".equals(examMode) && (!perSubjectOfflineExamStudentsMap.containsKey(subject))){
				ArrayList<String> students = new ArrayList<>();
				students.add(sapid);
				perSubjectOfflineExamStudentsMap.put(subject, students);
			}else if("Offline".equals(examMode) ){
				ArrayList<String> students = (ArrayList<String>)perSubjectOfflineExamStudentsMap.get(subject);
				if(!students.contains(sapid)){
					students.add(sapid);
				}
			}
			
			if("Online".equals(examMode) && (!perSubjectOnlineExamStudentsMap.containsKey(subject))){
				ArrayList<String> students = new ArrayList<>();
				students.add(sapid);
				perSubjectOnlineExamStudentsMap.put(subject, students);
			}else if("Online".equals(examMode) ){
				ArrayList<String> students = (ArrayList<String>)perSubjectOnlineExamStudentsMap.get(subject);
				if(!students.contains(sapid)){
					students.add(sapid);
				}
			}
			
			if("Offline".equals(examMode) && (!perSemOfflineExamStudentsMap.containsKey(sem))){
				ArrayList<String> students = new ArrayList<>();
				students.add(sapid);
				perSemOfflineExamStudentsMap.put(sem, students);
			}else if("Offline".equals(examMode) ){
				ArrayList<String> students = (ArrayList<String>)perSemOfflineExamStudentsMap.get(sem);
				if(!students.contains(sapid)){
					students.add(sapid);
				}
			}
			
			if("Online".equals(examMode) && (!perSemOnlineExamStudentsMap.containsKey(sem))){
				ArrayList<String> students = new ArrayList<>();
				students.add(sapid);
				perSemOnlineExamStudentsMap.put(sem, students);
			}else if("Online".equals(examMode) ){
				ArrayList<String> students = (ArrayList<String>)perSemOnlineExamStudentsMap.get(sem);
				if(!students.contains(sapid)){
					students.add(sapid);
				}
			}
			
			
			
			if("Offline".equals(examMode) && (!perProgramOfflineExamStudentsMap.containsKey(program))){
				ArrayList<String> students = new ArrayList<>();
				students.add(sapid);
				perProgramOfflineExamStudentsMap.put(program, students);
			}else if("Offline".equals(examMode) ){
				ArrayList<String> students = (ArrayList<String>)perProgramOfflineExamStudentsMap.get(program);
				if(!students.contains(sapid)){
					students.add(sapid);
				}
			}
			
			if("Online".equals(examMode) && (!perProgramOnlineExamStudentsMap.containsKey(program))){
				ArrayList<String> students = new ArrayList<>();
				students.add(sapid);
				perProgramOnlineExamStudentsMap.put(program, students);
			}else if("Online".equals(examMode) ){
				ArrayList<String> students = (ArrayList<String>)perProgramOnlineExamStudentsMap.get(program);
				if(!students.contains(sapid)){
					students.add(sapid);
				}
			}
			

			if("Project".equalsIgnoreCase(subject)){
				continue;
			}
			if("Module 4 - Project".equalsIgnoreCase(subject)){
				continue;
			}
			//Number of students per city
			if("Offline".equals(examMode) && (!perCityOfflineExamStudentsMap.containsKey(city))){
				ArrayList<String> students = new ArrayList<>();
				students.add(sapid);
				perCityOfflineExamStudentsMap.put(city, students);
			}else if("Offline".equals(examMode) ){
				ArrayList<String> students = (ArrayList<String>)perCityOfflineExamStudentsMap.get(city);
				if(!students.contains(sapid)){
					students.add(sapid);
				}
			}
			
			if("Online".equals(examMode) && (!perCityOnlineExamStudentsMap.containsKey(city))){
				ArrayList<String> students = new ArrayList<>();
				students.add(sapid);
				perCityOnlineExamStudentsMap.put(city, students);
			}else if("Online".equals(examMode) ){
				ArrayList<String> students = (ArrayList<String>)perCityOnlineExamStudentsMap.get(city);
				if(!students.contains(sapid)){
					students.add(sapid);
				}
			}
			
			
			
		}
		
		
		
		int index = 0;
		Sheet sheet = workbook.createSheet("Offline Exam Bookings");
		Row header = sheet.createRow(0);
		header.createCell(index++).setCellValue("Sr. No.");
		header.createCell(index++).setCellValue("Subject");
	    header.createCell(index++).setCellValue("Number of Students");
		
 
		int rowNum = 1;
		for (Map.Entry<String, ArrayList<String>> entry : new TreeMap<>(perSubjectOfflineExamStudentsMap).entrySet())
		{
		    
		    String subject = entry.getKey();
		    int noOfStudents = entry.getValue().size();
		
			index = 0;
			//create the row data
			Row row = sheet.createRow(rowNum++);

			row.createCell(index++).setCellValue((rowNum-1));
			row.createCell(index++).setCellValue(subject);
			row.createCell(index++).setCellValue(noOfStudents);
		
        }

		rowNum = perSubjectOfflineExamStudentsMap.size() + 3;
		header = sheet.createRow(rowNum++);
		index = 0;
		header.createCell(index++).setCellValue("Sr. No.");
		header.createCell(index++).setCellValue("City");
	    header.createCell(index++).setCellValue("Number of Students");
		
	    int counter = 1;
		for (Map.Entry<String, ArrayList<String>> entry : new TreeMap<>(perCityOfflineExamStudentsMap).entrySet())
		{
		    
		    String city = entry.getKey();
		    int noOfStudents = entry.getValue().size();
		
			index = 0;
			//create the row data
			Row row = sheet.createRow(rowNum++);

			row.createCell(index++).setCellValue((counter++));
			row.createCell(index++).setCellValue(city);
			row.createCell(index++).setCellValue(noOfStudents);
		
        }
		
		
		rowNum +=  2;
		header = sheet.createRow(rowNum++);
		index = 0;
		header.createCell(index++).setCellValue("Sr. No.");
		header.createCell(index++).setCellValue("Program");
	    header.createCell(index++).setCellValue("Number of Students");
	    counter = 1;
		for (Map.Entry<String, ArrayList<String>> entry : new TreeMap<>(perProgramOfflineExamStudentsMap).entrySet())
		{
		    
		    String program = entry.getKey();
		    int noOfStudents = entry.getValue().size();
		
			index = 0;
			//create the row data
			Row row = sheet.createRow(rowNum++);

			row.createCell(index++).setCellValue((counter++));
			row.createCell(index++).setCellValue(program);
			row.createCell(index++).setCellValue(noOfStudents);
		
        }
		
		rowNum += 2;
		header = sheet.createRow(rowNum++);
		index = 0;
		header.createCell(index++).setCellValue("Sr. No.");
		header.createCell(index++).setCellValue("Sem");
	    header.createCell(index++).setCellValue("Number of Students");
	    counter = 1;
		for (Map.Entry<String, ArrayList<String>> entry : new TreeMap<>(perSemOfflineExamStudentsMap).entrySet())
		{
		    
		    String sem = entry.getKey();
		    int noOfStudents = entry.getValue().size();
		
			index = 0;
			//create the row data
			Row row = sheet.createRow(rowNum++);

			row.createCell(index++).setCellValue((counter++));
			row.createCell(index++).setCellValue(new Double(sem));
			row.createCell(index++).setCellValue(noOfStudents);
		
        }
		

		sheet = workbook.createSheet("Online Exam Bookings");
		index = 0;
		header = sheet.createRow(0);
		header.createCell(index++).setCellValue("Sr. No.");
		header.createCell(index++).setCellValue("Subject");
	    header.createCell(index++).setCellValue("Number of Students");
		
 
		rowNum = 1;
		for (Map.Entry<String, ArrayList<String>> entry : new TreeMap<>(perSubjectOnlineExamStudentsMap).entrySet())
		{

		    String subject = entry.getKey();
		    int noOfStudents = entry.getValue().size();
		
			index = 0;
			//create the row data
			Row row = sheet.createRow(rowNum++);

			row.createCell(index++).setCellValue((rowNum-1));
			row.createCell(index++).setCellValue(subject);
			row.createCell(index++).setCellValue(noOfStudents);
		
        }

		rowNum = rowNum + 3;
		index = 0;
		header = sheet.createRow(rowNum++);
		header.createCell(index++).setCellValue("Sr. No.");
		header.createCell(index++).setCellValue("City");
	    header.createCell(index++).setCellValue("Number of Students");
		
	    counter = 1;
		for (Map.Entry<String, ArrayList<String>> entry : new TreeMap<>(perCityOnlineExamStudentsMap).entrySet())
		{
		    
		    String city = entry.getKey();
		    int noOfStudents = entry.getValue().size();
		
			index = 0;
			//create the row data
			Row row = sheet.createRow(rowNum++);

			row.createCell(index++).setCellValue((counter++));
			row.createCell(index++).setCellValue(city);
			row.createCell(index++).setCellValue(noOfStudents);
		
        }

 
		rowNum += 2;
		header = sheet.createRow(rowNum++);
		index = 0;
		header.createCell(index++).setCellValue("Sr. No.");
		header.createCell(index++).setCellValue("Program");
	    header.createCell(index++).setCellValue("Number of Students");
	    counter = 1;
		for (Map.Entry<String, ArrayList<String>> entry : new TreeMap<>(perProgramOnlineExamStudentsMap).entrySet())
		{
		    
		    String program = entry.getKey();
		    int noOfStudents = entry.getValue().size();
		
			index = 0;
			//create the row data
			Row row = sheet.createRow(rowNum++);

			row.createCell(index++).setCellValue((counter++));
			row.createCell(index++).setCellValue(program);
			row.createCell(index++).setCellValue(noOfStudents);
		
        }
		
		rowNum += 2;
		header = sheet.createRow(rowNum++);
		index = 0;
		header.createCell(index++).setCellValue("Sr. No.");
		header.createCell(index++).setCellValue("Sem");
	    header.createCell(index++).setCellValue("Number of Students");
	    counter = 1;
		for (Map.Entry<String, ArrayList<String>> entry : new TreeMap<>(perSemOnlineExamStudentsMap).entrySet())
		{
		    
		    String sem = entry.getKey();
		    int noOfStudents = entry.getValue().size();
		
			index = 0;
			//create the row data
			Row row = sheet.createRow(rowNum++);

			row.createCell(index++).setCellValue((counter++));
			row.createCell(index++).setCellValue(new Double(sem));
			row.createCell(index++).setCellValue(noOfStudents);
		
        }
		
		
		
	}

}
