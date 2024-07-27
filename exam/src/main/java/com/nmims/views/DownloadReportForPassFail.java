package com.nmims.views;

import java.util.List;
import java.util.Map;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.springframework.context.ApplicationContextAware;
import org.springframework.web.servlet.view.document.AbstractXlsxStreamingView;
import com.nmims.beans.StudentMarksBean;

public class DownloadReportForPassFail extends AbstractXlsxStreamingView
implements ApplicationContextAware {
	
	public static final String COL_SRNO = "Sr. No.";
	public static final String COL_ACAD_YEAR = "Acad Year";
	public static final String COL_ACAD_MONTH = "Acad Month";
	public static final String COL_EXAM_YEAR = "Exam Year";
	public static final String COL_EXAM_MONTH = "Exam Month";
	public static final String COL_SAPID = "Sap Id";
	public static final String COL_NAME = "Student Name";
	public static final String COL_SUBJECT = "Subject";
	public static final String COL_SEM = "Sem";
	public static final String COL_PROGRAM = "Program";
	public static final String COL_PROGRAM_STRUCTURE = "Program Structure";
	public static final String COL_WRITTERN_SCORE = "Written Score";
	public static final String COL_ASSIGNMENT_SCORE = "Assignment Score";
	public static final String COL_GRACE_SCORE = "Grace Marks";
	public static final String COL_ATTEMPT = "Attempt";
	public static final String COL_SOURCE = "Source";
	public static final String COL_CENTER_CODE = "Center Code";
	public static final String COL_LOCATION = "Location";
	public static final String COL_PROCESSED = "Processed";
	public static final String COL_REVALUATED = "Revaluated";
	public static final String COL_MARKED_PHOTOCOPY = "Marked For PhotoCopy";
	public static final String COL_REVALUATION_RESULT_DECLARED = "Revaulation Result Declared";
	public static final String COL_ASSIGNEMNT_BEFORE_REVAL = "Assignment Before Reval";
	public static final String COL_WRITTEN_BEFORE_REVEL = "Written Before Reval";
	public static final String COL_OLD_WRITTERN_SCORE = "Old Written Score";
	public static final String COL_OLD_ASSIGNMENT_SCORE = "Old Assignment Score";
	public static final String COL_WRITTERN_BEFORE_RIANV = "Written Before RIA/NV";
	public static final String COL_IS_RESULT_LIVE = "Is Result Live";
	public static final String COL_EXAM_MODE = "Exam Mode";


	


	@Override
	protected void buildExcelDocument(Map<String, Object> model, Workbook workbook, HttpServletRequest request,
			HttpServletResponse response) throws Exception {
			
		try{

			String reportType=(String) request.getSession().getAttribute("reportType");
			response.setContentType("application/vnd.ms-excel");
			response.setHeader("Content-Disposition", "attachment; filename="+reportType+".xlsx");
			List<StudentMarksBean> reportForPassFail= (List<StudentMarksBean>) model.get(reportType);
		Row row=null;
		Row header=null;
		int index = 0;
		
		
		Sheet sheet=workbook.createSheet(reportType);
		
		header = sheet.createRow(0);
		
		header.createCell(index++).setCellValue(COL_SRNO);
		header.createCell(index++).setCellValue(COL_SAPID);
		header.createCell(index++).setCellValue(COL_NAME);
		header.createCell(index++).setCellValue(COL_EXAM_MONTH);
		header.createCell(index++).setCellValue(COL_EXAM_YEAR);
		header.createCell(index++).setCellValue(COL_SEM);
		header.createCell(index++).setCellValue(COL_SUBJECT);
		header.createCell(index++).setCellValue(COL_PROGRAM);
		header.createCell(index++).setCellValue(COL_PROGRAM_STRUCTURE);
		header.createCell(index++).setCellValue(COL_WRITTERN_SCORE);
		header.createCell(index++).setCellValue(COL_ASSIGNMENT_SCORE);

		header.createCell(index++).setCellValue(COL_CENTER_CODE);
		header.createCell(index++).setCellValue(COL_LOCATION);

		header.createCell(index++).setCellValue(COL_REVALUATION_RESULT_DECLARED);
		header.createCell(index++).setCellValue(COL_ASSIGNEMNT_BEFORE_REVAL);

		

		
		
		
		int rowNum = 1;
			for (int j = 0; j < reportForPassFail.size(); j++) {
				index = 0;
				row = sheet.createRow(rowNum++);
				row.createCell(index++).setCellValue(j + 1);
				row.createCell(index++).setCellValue(reportForPassFail.get(j).getSapid());
				row.createCell(index++).setCellValue(reportForPassFail.get(j).getStudentname());
				row.createCell(index++).setCellValue(reportForPassFail.get(j).getMonth());
				row.createCell(index++).setCellValue(reportForPassFail.get(j).getYear());
				row.createCell(index++).setCellValue(reportForPassFail.get(j).getSem());
				row.createCell(index++).setCellValue(reportForPassFail.get(j).getSubject());
				row.createCell(index++).setCellValue(reportForPassFail.get(j).getProgram());
				row.createCell(index++).setCellValue(reportForPassFail.get(j).getPrgmStructApplicable());
				row.createCell(index++).setCellValue(reportForPassFail.get(j).getWritenscore());
				row.createCell(index++).setCellValue(reportForPassFail.get(j).getAssignmentscore());
//				row.createCell(index++).setCellValue(reportForPassFail.get(j).getGracemarks());
//				row.createCell(index++).setCellValue(reportForPassFail.get(j).getAttempt());
//				row.createCell(index++).setCellValue(reportForPassFail.get(j).getSource());
				row.createCell(index++).setCellValue(reportForPassFail.get(j).getCentercode());
				row.createCell(index++).setCellValue(reportForPassFail.get(j).getLocation());
				row.createCell(index++).setCellValue(reportForPassFail.get(j).getMarkedForPhotocopy());
				row.createCell(index++).setCellValue(reportForPassFail.get(j).getRevaluationScore());
//				row.createCell(index++).setCellValue(reportForPassFail.get(j).getAssignmentBeforeReval());
//				row.createCell(index++).setCellValue(reportForPassFail.get(j).getWrittenBeforeReval());
//				row.createCell(index++).setCellValue(reportForPassFail.get(j).getOldWrittenScore());
//				row.createCell(index++).setCellValue(reportForPassFail.get(j).getOldAssignmentScore());
//				row.createCell(index++).setCellValue(reportForPassFail.get(j).getWrittenBeforeRIANV());
//				row.createCell(index++).setCellValue(reportForPassFail.get(j).getExamMode());
			
			}
	
		request.getSession().removeAttribute("reportType");
		}
		catch(Exception e) {
			e.printStackTrace();
		}
	}
	

}
