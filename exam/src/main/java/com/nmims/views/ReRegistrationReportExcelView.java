package com.nmims.views;
import java.io.ByteArrayOutputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;





/*
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;*/
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.util.CellRangeAddress;
/*import org.apache.poi.hssf.util.CellRangeAddress;*/
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
/*import org.springframework.web.servlet.view.document.AbstractExcelView;*/
import org.springframework.web.servlet.view.document.AbstractXlsxStreamingView;

import com.nmims.beans.StudentExamBean;



public class ReRegistrationReportExcelView extends AbstractXlsxStreamingView  implements ApplicationContextAware{
	private static ApplicationContext act = null;
	
	@Override
	protected void buildExcelDocument(Map model, Workbook workbook,	HttpServletRequest request, HttpServletResponse response){
		act = getApplicationContext();
		
		String userId=(String) request.getSession().getAttribute("userId");
		List<String> ICLCRESTRICTED_USER_LIST=(List<String>) request.getSession().getAttribute("ICLCRESTRICTED_USER_LIST");
		
		List<String> listOfActiveSapId = (List<String>) model.get("listOfActiveSapId");
		HashMap<String,StudentExamBean> allStudentsMap = (HashMap<String,StudentExamBean>)request.getSession().getAttribute("allStudentsMap");
		HashMap<String,String> mapOfStudentNumberSemAndCountOfFailedSubjects = (HashMap<String,String>)request.getSession().getAttribute("mapOfStudentNumberSemAndCountOfFailedSubjects");
		HashMap<String,String> mapOfStudentNumberAndSemAndCountOfANSSubjects = (HashMap<String,String>) request.getSession().getAttribute("mapOfStudentNumberAndSemAndCountOfANSSubjects");
		HashMap<String,String> mapOfStudentNumberAndSemAndCountOfTEEMissingSubjects = (HashMap<String,String>) request.getSession().getAttribute("mapOfStudentNumberAndSemAndCountOfTEEMissingSubjects");
		HashMap<String,String> mapOfStudentNumberAndSemAndCountOfABSubjects = (HashMap<String,String>) request.getSession().getAttribute("mapOfStudentNumberAndSemAndCountOfABSubjects");
		HashMap<String,String> mapOfStudentNumberAndSemAndGAPInReReg = (HashMap<String,String>) request.getSession().getAttribute("mapOfStudentNumberAndSemAndGAPInReReg");
		HashMap<String,String> mapOfStudentNumberAndSemAndPendingNumberOfExamBookings = (HashMap<String,String>) request.getSession().getAttribute("mapOfStudentNumberAndSemAndPendingNumberOfExamBookings");
		HashMap<String,String> mapOfStudentNumberAndSemAndCountOfSessionsAttended = (HashMap<String,String>) request.getSession().getAttribute("mapOfStudentNumberAndSemAndCountOfSessionsAttended");
		HashMap<String,String> mapOfStudentNumberAndSemAndDriveMonthYear = (HashMap<String,String>) request.getSession().getAttribute("mapOfStudentNumberAndSemAndDriveMonthYear");
		
		Sheet sheet = workbook.createSheet("ReRegistrationReport");
		int index = 0,attributeIndex = 7;int colIndex=0;
		Row header = sheet.createRow(0);
		Row parametersHeader = sheet.createRow(1);
		header.createCell(colIndex++).setCellValue("SAPID");
		
	/*	header.createCell(1).setCellValue("Semester 1");
		header.createCell(8).setCellValue("Semester 2");
		header.createCell(15).setCellValue("Semester 3");
		header.createCell(22).setCellValue("Semester 4");
		
		
		sheet.addMergedRegion(new CellRangeAddress(0,0,1,7));
		sheet.addMergedRegion(new CellRangeAddress(0,0,8,14));
		sheet.addMergedRegion(new CellRangeAddress(0,0,15,21));
		sheet.addMergedRegion(new CellRangeAddress(0,0,22,28));*/
		
		header.createCell(colIndex++).setCellValue("Student's Name");
		header.createCell(colIndex++).setCellValue("Mobile");
		header.createCell(colIndex++).setCellValue("Email Id");
		if(!ICLCRESTRICTED_USER_LIST.contains(userId)) {
		header.createCell(colIndex++).setCellValue("Center Name");
		}
		header.createCell(colIndex++).setCellValue("ValidityEnd");
		header.createCell(colIndex++).setCellValue("Program");
		header.createCell(colIndex++).setCellValue("Semester 1");
		header.createCell(colIndex+6).setCellValue("Semester 2");
		header.createCell(colIndex+6).setCellValue("Semester 3");
		header.createCell(colIndex+6).setCellValue("Semester 4");
		
		
		sheet.addMergedRegion(new CellRangeAddress(0,0,7,12));
		sheet.addMergedRegion(new CellRangeAddress(0,0,13,18));
		sheet.addMergedRegion(new CellRangeAddress(0,0,19,24));
		sheet.addMergedRegion(new CellRangeAddress(0,0,25,30));
		
		
		parametersHeader.createCell(attributeIndex++).setCellValue("Failed Subject");
		/*parametersHeader.createCell(attributeIndex++).setCellValue("GAP");*/
		parametersHeader.createCell(attributeIndex++).setCellValue("TEE Absent");
		parametersHeader.createCell(attributeIndex++).setCellValue("ANS");
		parametersHeader.createCell(attributeIndex++).setCellValue("Pending Booking Count");
		parametersHeader.createCell(attributeIndex++).setCellValue("Sessions Attended");
		parametersHeader.createCell(attributeIndex++).setCellValue("Re-Reg Cycle");
		
		parametersHeader.createCell(attributeIndex++).setCellValue("Failed Subject");
		/*parametersHeader.createCell(attributeIndex++).setCellValue("GAP");*/
		parametersHeader.createCell(attributeIndex++).setCellValue("TEE Absent");
		parametersHeader.createCell(attributeIndex++).setCellValue("ANS");
		parametersHeader.createCell(attributeIndex++).setCellValue("Pending Booking Count");
		parametersHeader.createCell(attributeIndex++).setCellValue("Sessions Attended");
		parametersHeader.createCell(attributeIndex++).setCellValue("Re-Reg Cycle");
		
		
		parametersHeader.createCell(attributeIndex++).setCellValue("Failed Subject");
		/*parametersHeader.createCell(attributeIndex++).setCellValue("GAP");*/
		parametersHeader.createCell(attributeIndex++).setCellValue("TEE Absent");
		parametersHeader.createCell(attributeIndex++).setCellValue("ANS");
		parametersHeader.createCell(attributeIndex++).setCellValue("Pending Booking Count");
		parametersHeader.createCell(attributeIndex++).setCellValue("Sessions Attended");
		parametersHeader.createCell(attributeIndex++).setCellValue("Re-Reg Cycle");
		
		parametersHeader.createCell(attributeIndex++).setCellValue("Failed Subject");
		/*parametersHeader.createCell(attributeIndex++).setCellValue("GAP");*/
		parametersHeader.createCell(attributeIndex++).setCellValue("TEE Absent");
		parametersHeader.createCell(attributeIndex++).setCellValue("ANS");
		parametersHeader.createCell(attributeIndex++).setCellValue("Pending Booking Count");
		parametersHeader.createCell(attributeIndex++).setCellValue("Sessions Attended");
		parametersHeader.createCell(attributeIndex++).setCellValue("Re-Reg Cycle");
		
		int rowNum = 2;
		for(int i = 0;i<listOfActiveSapId.size();i++){
			index = 0;
			String sapid = listOfActiveSapId.get(i);
			StudentExamBean student = allStudentsMap.get(sapid);
			Row row = sheet.createRow(rowNum++);
			String firstName = student.getFirstName() == null ? "":student.getFirstName();
			String lastName = student.getLastName() == null ? "":student.getLastName();
			String studentName = firstName+" "+lastName;
			row.createCell(index++).setCellValue(sapid);
			row.createCell(index++).setCellValue(studentName);

			row.createCell(index++).setCellValue(student.getMobile());
			row.createCell(index++).setCellValue(student.getEmailId());
			if(!ICLCRESTRICTED_USER_LIST.contains(userId)) {
			row.createCell(index++).setCellValue(student.getCenterName());
			}
			row.createCell(index++).setCellValue(student.getValidityEndMonth()+ "-" +student.getValidityEndYear());
			row.createCell(index++).setCellValue(student.getProgram());
			row.createCell(index++).setCellValue(mapOfStudentNumberSemAndCountOfFailedSubjects.get(sapid + "-1") == null ? "0": mapOfStudentNumberSemAndCountOfFailedSubjects.get(sapid + "-1"));
			/*row.createCell(index++).setCellValue(mapOfStudentNumberAndSemAndGAPInReReg.get(sapid + "-1") == null ? "0": mapOfStudentNumberAndSemAndGAPInReReg.get(sapid + "-1"));*/
			row.createCell(index++).setCellValue(mapOfStudentNumberAndSemAndCountOfTEEMissingSubjects.get(sapid + "-1") == null ? "0": mapOfStudentNumberAndSemAndCountOfTEEMissingSubjects.get(sapid + "-1"));
			row.createCell(index++).setCellValue(mapOfStudentNumberAndSemAndCountOfANSSubjects.get(sapid + "-1") == null ? "0": mapOfStudentNumberAndSemAndCountOfANSSubjects.get(sapid + "-1"));
			row.createCell(index++).setCellValue(mapOfStudentNumberAndSemAndPendingNumberOfExamBookings.get(sapid + "-1") == null ? "0": mapOfStudentNumberAndSemAndPendingNumberOfExamBookings.get(sapid + "-1"));
			row.createCell(index++).setCellValue(mapOfStudentNumberAndSemAndCountOfSessionsAttended.get(sapid + "-1") == null ? "0": mapOfStudentNumberAndSemAndCountOfSessionsAttended.get(sapid + "-1"));
			row.createCell(index++).setCellValue(mapOfStudentNumberAndSemAndDriveMonthYear.get(sapid + "-1") == null ? "--": mapOfStudentNumberAndSemAndDriveMonthYear.get(sapid + "-1"));
			
			row.createCell(index++).setCellValue(mapOfStudentNumberSemAndCountOfFailedSubjects.get(sapid + "-2") == null ? "0": mapOfStudentNumberSemAndCountOfFailedSubjects.get(sapid + "-2"));
			/*row.createCell(index++).setCellValue(mapOfStudentNumberAndSemAndGAPInReReg.get(sapid + "-2") == null ? "0": mapOfStudentNumberAndSemAndGAPInReReg.get(sapid + "-2"));*/
			row.createCell(index++).setCellValue(mapOfStudentNumberAndSemAndCountOfTEEMissingSubjects.get(sapid + "-2") == null ? "0": mapOfStudentNumberAndSemAndCountOfTEEMissingSubjects.get(sapid + "-2"));
			row.createCell(index++).setCellValue(mapOfStudentNumberAndSemAndCountOfANSSubjects.get(sapid + "-2") == null ? "0": mapOfStudentNumberAndSemAndCountOfANSSubjects.get(sapid + "-2"));
			row.createCell(index++).setCellValue(mapOfStudentNumberAndSemAndPendingNumberOfExamBookings.get(sapid + "-2") == null ? "0": mapOfStudentNumberAndSemAndPendingNumberOfExamBookings.get(sapid + "-2"));
			row.createCell(index++).setCellValue(mapOfStudentNumberAndSemAndCountOfSessionsAttended.get(sapid + "-2") == null ? "0": mapOfStudentNumberAndSemAndCountOfSessionsAttended.get(sapid + "-2"));
			row.createCell(index++).setCellValue(mapOfStudentNumberAndSemAndDriveMonthYear.get(sapid + "-2") == null ? "--": mapOfStudentNumberAndSemAndDriveMonthYear.get(sapid + "-2"));
			
			row.createCell(index++).setCellValue(mapOfStudentNumberSemAndCountOfFailedSubjects.get(sapid + "-3") == null ? "0": mapOfStudentNumberSemAndCountOfFailedSubjects.get(sapid + "-3"));
			/*row.createCell(index++).setCellValue(mapOfStudentNumberAndSemAndGAPInReReg.get(sapid + "-3") == null ? "0": mapOfStudentNumberAndSemAndGAPInReReg.get(sapid + "-3"));*/
			row.createCell(index++).setCellValue(mapOfStudentNumberAndSemAndCountOfTEEMissingSubjects.get(sapid + "-3") == null ? "0": mapOfStudentNumberAndSemAndCountOfTEEMissingSubjects.get(sapid + "-3"));
			row.createCell(index++).setCellValue(mapOfStudentNumberAndSemAndCountOfANSSubjects.get(sapid + "-3") == null ? "0": mapOfStudentNumberAndSemAndCountOfANSSubjects.get(sapid + "-3"));
			row.createCell(index++).setCellValue(mapOfStudentNumberAndSemAndPendingNumberOfExamBookings.get(sapid + "-3") == null ? "0": mapOfStudentNumberAndSemAndPendingNumberOfExamBookings.get(sapid + "-3"));
			row.createCell(index++).setCellValue(mapOfStudentNumberAndSemAndCountOfSessionsAttended.get(sapid + "-3") == null ? "0": mapOfStudentNumberAndSemAndCountOfSessionsAttended.get(sapid + "-3"));
			row.createCell(index++).setCellValue(mapOfStudentNumberAndSemAndDriveMonthYear.get(sapid + "-3") == null ? "--": mapOfStudentNumberAndSemAndDriveMonthYear.get(sapid + "-3"));
			
			
			row.createCell(index++).setCellValue(mapOfStudentNumberSemAndCountOfFailedSubjects.get(sapid + "-4") == null ? "0": mapOfStudentNumberSemAndCountOfFailedSubjects.get(sapid + "-4"));
		/*	row.createCell(index++).setCellValue(mapOfStudentNumberAndSemAndGAPInReReg.get(sapid + "-4") == null ? "0": mapOfStudentNumberAndSemAndGAPInReReg.get(sapid + "-4"));*/
			row.createCell(index++).setCellValue(mapOfStudentNumberAndSemAndCountOfTEEMissingSubjects.get(sapid + "-4") == null ? "0": mapOfStudentNumberAndSemAndCountOfTEEMissingSubjects.get(sapid + "-4"));
			row.createCell(index++).setCellValue(mapOfStudentNumberAndSemAndCountOfANSSubjects.get(sapid + "-4") == null ? "0": mapOfStudentNumberAndSemAndCountOfANSSubjects.get(sapid + "-4"));
			row.createCell(index++).setCellValue(mapOfStudentNumberAndSemAndPendingNumberOfExamBookings.get(sapid + "-4") == null ? "0": mapOfStudentNumberAndSemAndPendingNumberOfExamBookings.get(sapid + "-4"));
			row.createCell(index++).setCellValue(mapOfStudentNumberAndSemAndCountOfSessionsAttended.get(sapid + "-4") == null ? "0": mapOfStudentNumberAndSemAndCountOfSessionsAttended.get(sapid + "-4"));
			row.createCell(index++).setCellValue(mapOfStudentNumberAndSemAndDriveMonthYear.get(sapid + "-4") == null ? "--": mapOfStudentNumberAndSemAndDriveMonthYear.get(sapid + "-4"));
			
			
			
			
		}
		
		
		 try{
			 	ByteArrayOutputStream outByteStream = new ByteArrayOutputStream();
				workbook.write(outByteStream);
				byte [] outArray = outByteStream.toByteArray();
				response.setContentType("application/ms-excel");
				response.setContentLength(outArray.length);
				response.setHeader("Expires:", "0"); // eliminates browser caching
				response.setHeader("Content-Disposition", "attachment; filename=ReRegistrationReport.xls");
				ServletOutputStream outStream = response.getOutputStream();
				outStream.write(outArray);
				outStream.flush();
			 
		 }catch(Exception e){
			 
		 }
	}

}
