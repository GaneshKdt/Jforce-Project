/**
 * 
 */
package com.nmims.views;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.springframework.context.ApplicationContextAware;
import org.springframework.web.servlet.view.document.AbstractXlsxStreamingView;

import com.nmims.beans.RemarksGradeBean;

/**
 * @author vil_m
 *
 */
public class DownloadRemarksGradedPFReport extends AbstractXlsxStreamingView implements ApplicationContextAware {
	
	public static final String COL_SRNO = "Sr. No.";
	public static final String COL_EXAM_YEAR = "Exam Year";
	public static final String COL_EXAM_MONTH = "Exam Month";
	public static final String COL_SAPID = "Sap Id";
	public static final String COL_NAME = "Student Name";
	public static final String COL_SUBJECT = "Subject";
	public static final String COL_SEM = "Sem";
	public static final String COL_PROGRAM = "Program";
	public static final String COL_PROGRAM_STRUCTURE = "Program Structure";
	public static final String COL_STUDENT_TYPE = "Student Type";
	public static final String COL_WRITTEN_SCORE = "Written Score";
	public static final String COL_ASSIGN_SCORE = "Assign Score";
	public static final String COL_TOTAL_SCORE = "Total Score";
	public static final String COL_STATUS = "Status";
	public static final String COL_ISPASS = "Is Pass";
	public static final String COL_REMARKS = "Remarks";
	public static final String COL_GRADE = "Grade";
	public static final String COL_FAILREASON = "Fail Reason";
	
	//start - PROGRAMMING Constants
	public static final String V_PASS = "Pass";
	public static final String V_FAIL = "Fail";
	public static final String V_EMPTY = "";
	
	//end - PROGRAMMING Constants

	@Override
	protected void buildExcelDocument(Map<String, Object> arg0, Workbook arg1, HttpServletRequest arg2,
			HttpServletResponse arg3) throws Exception {
		// TODO Auto-generated method stub
		Row row = null;
		Row header = null;
		int index = 0;
		String status = null;
		
		List<RemarksGradeBean> list1 = (ArrayList<RemarksGradeBean>) arg0.get("studentGradedForTransferList");


		Sheet sheet = arg1.createSheet("DownloadRemarksGradedP Report");

		header = sheet.createRow(0);
		header.createCell(index++).setCellValue(COL_SRNO);
		header.createCell(index++).setCellValue(COL_EXAM_YEAR);
		header.createCell(index++).setCellValue(COL_EXAM_MONTH);
		header.createCell(index++).setCellValue(COL_SAPID);
		header.createCell(index++).setCellValue(COL_NAME);
		header.createCell(index++).setCellValue(COL_SUBJECT);
		header.createCell(index++).setCellValue(COL_SEM);
		header.createCell(index++).setCellValue(COL_PROGRAM);
		header.createCell(index++).setCellValue(COL_PROGRAM_STRUCTURE);
		header.createCell(index++).setCellValue(COL_STUDENT_TYPE);
		header.createCell(index++).setCellValue(COL_WRITTEN_SCORE);
		header.createCell(index++).setCellValue(COL_ASSIGN_SCORE);
		header.createCell(index++).setCellValue(COL_TOTAL_SCORE);
		header.createCell(index++).setCellValue(COL_STATUS);
		header.createCell(index++).setCellValue(COL_ISPASS);
		header.createCell(index++).setCellValue(COL_REMARKS);
		header.createCell(index++).setCellValue(COL_GRADE);
		header.createCell(index++).setCellValue(COL_FAILREASON);

		int rowNum = 1;
		for (int j = 0; j < list1.size(); j++) {
			index = 0;
			row = sheet.createRow(rowNum++);
			row.createCell(index++).setCellValue(j + 1);
			row.createCell(index++).setCellValue(list1.get(j).getYear());
			row.createCell(index++).setCellValue(list1.get(j).getMonth());
			row.createCell(index++).setCellValue(list1.get(j).getSapid());
			row.createCell(index++).setCellValue(list1.get(j).getName());
			row.createCell(index++).setCellValue(list1.get(j).getSubject());
			row.createCell(index++).setCellValue(list1.get(j).getSem());
			row.createCell(index++).setCellValue(list1.get(j).getProgram());
			row.createCell(index++).setCellValue(list1.get(j).getProgramStructure());
			row.createCell(index++).setCellValue(list1.get(j).getStudentType());
			status = list1.get(j).getStatus();
			if(null != status && (RemarksGradeBean.ATTEMPTED.equals(status) || RemarksGradeBean.CC.equals(status))) {
				row.createCell(index++).setCellValue(list1.get(j).getScoreWritten());
				row.createCell(index++).setCellValue(list1.get(j).getScoreIA());
				row.createCell(index++).setCellValue(list1.get(j).getScoreTotal());
			} else {
				row.createCell(index++).setCellValue(V_EMPTY);
				row.createCell(index++).setCellValue(V_EMPTY);
				row.createCell(index++).setCellValue(V_EMPTY);
			}
			row.createCell(index++).setCellValue(status);
			if (null != status && (RemarksGradeBean.ATTEMPTED.equals(status) || RemarksGradeBean.CC.equals(status))) {
				if (list1.get(j).isPass()) {
					row.createCell(index++).setCellValue(V_PASS);
				} else {
					row.createCell(index++).setCellValue(V_FAIL);
				}
			} else {
				row.createCell(index++).setCellValue(V_EMPTY);
			}
			row.createCell(index++).setCellValue(list1.get(j).getRemarks());
			row.createCell(index++).setCellValue(list1.get(j).getGrade());
			row.createCell(index++).setCellValue(list1.get(j).getFailReason());
		}
	}

}
