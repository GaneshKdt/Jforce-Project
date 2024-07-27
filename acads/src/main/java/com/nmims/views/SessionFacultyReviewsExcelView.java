package com.nmims.views;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.util.CellRangeAddress;
import org.springframework.context.ApplicationContextAware;
import org.springframework.web.servlet.view.document.AbstractExcelView;
import org.springframework.web.servlet.view.document.AbstractXlsxStreamingView;

import com.nmims.beans.FacultyAcadsBean;
import com.nmims.beans.SessionReviewBean;

public class SessionFacultyReviewsExcelView extends AbstractXlsxStreamingView  implements ApplicationContextAware{
	
	@Override
	protected void buildExcelDocument(Map model, Workbook workbook,	HttpServletRequest request, HttpServletResponse response)
		throws Exception {
 
		//Read list
		List<SessionReviewBean> reviewListBasedOnCriteria = (List<SessionReviewBean>)request.getSession().getAttribute("reviewListBasedOnCriteria");
		HashMap<String,FacultyAcadsBean> facultyIdAndFacultyBeanMap = (HashMap<String,FacultyAcadsBean>)request.getSession().getAttribute("facultyIdAndFacultyBeanMap");
		//create a wordsheet
		Sheet sheet = workbook.createSheet("Faculty List");
		
 		int index = 0;
		Row header = sheet.createRow(0);
		header.createCell(index++).setCellValue("Sr. No.");
		header.createCell(index++).setCellValue("Subject Name");
		header.createCell(index++).setCellValue("Session Id");
		header.createCell(index++).setCellValue("Session Name");
		header.createCell(index++).setCellValue("Session Date");
		header.createCell(index++).setCellValue("Session Time");
		header.createCell(index++).setCellValue("Reviewer Faculty");
		header.createCell(index++).setCellValue("First Name");
		header.createCell(index++).setCellValue("Last Name");
		header.createCell(index++).setCellValue("Faculty ID");
		header.createCell(index++).setCellValue("Faculty Name");
		header.createCell(index++).setCellValue("Reviewer Email");
		header.createCell(index++).setCellValue("Reviewed");
		header.createCell(index++).setCellValue("Q1. Response (Adhering to the Session plan (Yes/No))");
		header.createCell(index++).setCellValue("Q2. Response (Addressing Student queries (Yes/No))");
		header.createCell(index++).setCellValue("Q3. Response (Aligning Case study with course content(Yes/No))");
		header.createCell(index++).setCellValue("Q4. Response (Lecture delivery (Poor/Needs Improvement/Good/Excellent))");
		header.createCell(index++).setCellValue("Q5. Response (Communicaton- Language (On a scale of 1-7))");
		header.createCell(index++).setCellValue("Q6. Response (Communicaton- Clarity (On a scale of 1-7))");
		header.createCell(index++).setCellValue("Q1. Remark (Adhering to the Session plan (Yes/No))");
		header.createCell(index++).setCellValue("Q2. Remark (Addressing Student queries (Yes/No))");
		header.createCell(index++).setCellValue("Q3. Remark (Aligning Case study with course content(Yes/No))");
		header.createCell(index++).setCellValue("Q4. Remark (Lecture delivery (Poor/Needs Improvement/Good/Excellent))");
		header.createCell(index++).setCellValue("Q5. Remark (Communicaton- Language (On a scale of 1-7))");
		header.createCell(index++).setCellValue("Q6. Remark (Communicaton- Clarity (On a scale of 1-7))");
		
		int rowNum = 1;
		for (int i = 0 ; i < reviewListBasedOnCriteria.size(); i++) {
			index = 0;
			//create the row data
			Row row = sheet.createRow(rowNum++);
			SessionReviewBean reviewBean = reviewListBasedOnCriteria.get(i);
			if(StringUtils.isBlank(reviewBean.getReviewerFacultyId()) || StringUtils.isEmpty(reviewBean.getReviewerFacultyId())){
				continue;
			}
			FacultyAcadsBean bean = facultyIdAndFacultyBeanMap.get(reviewBean.getReviewerFacultyId());
			row.createCell(index++).setCellValue((i+1));
			row.createCell(index++).setCellValue(reviewBean.getSubject());
			row.createCell(index++).setCellValue(reviewBean.getSessionId());
			row.createCell(index++).setCellValue(reviewBean.getSessionName());
			row.createCell(index++).setCellValue(reviewBean.getDate());
			row.createCell(index++).setCellValue(reviewBean.getStartTime());
			row.createCell(index++).setCellValue(reviewBean.getReviewerFacultyId());
			row.createCell(index++).setCellValue(bean.getFirstName());
			row.createCell(index++).setCellValue(bean.getLastName());
			row.createCell(index++).setCellValue(reviewBean.getFacultyId());
			row.createCell(index++).setCellValue(facultyIdAndFacultyBeanMap.get(reviewBean.getFacultyId()).getFirstName() + " " + facultyIdAndFacultyBeanMap.get(reviewBean.getFacultyId()).getLastName());
			row.createCell(index++).setCellValue(bean.getEmail());
			row.createCell(index++).setCellValue(reviewBean.getReviewed());
			row.createCell(index++).setCellValue(reviewBean.getQ1Response());
			row.createCell(index++).setCellValue(reviewBean.getQ2Response());
			row.createCell(index++).setCellValue(reviewBean.getQ3Response());
			row.createCell(index++).setCellValue(reviewBean.getQ4Response());
			row.createCell(index++).setCellValue(reviewBean.getQ5Response());
			row.createCell(index++).setCellValue(reviewBean.getQ6Response());
			row.createCell(index++).setCellValue(reviewBean.getQ1Remarks());
			row.createCell(index++).setCellValue(reviewBean.getQ2Remarks());
			row.createCell(index++).setCellValue(reviewBean.getQ3Remarks());
			row.createCell(index++).setCellValue(reviewBean.getQ4Remarks());
			row.createCell(index++).setCellValue(reviewBean.getQ5Remarks());
			row.createCell(index++).setCellValue(reviewBean.getQ6Remarks());
        }
		sheet.autoSizeColumn(0); sheet.autoSizeColumn(1);
		sheet.autoSizeColumn(2); sheet.autoSizeColumn(3);
		sheet.autoSizeColumn(4); sheet.autoSizeColumn(5);
		sheet.autoSizeColumn(6); sheet.autoSizeColumn(7);
		
	
	}
}
