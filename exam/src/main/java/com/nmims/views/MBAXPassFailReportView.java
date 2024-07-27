package com.nmims.views;

	import java.util.HashMap;
	import java.util.List;
	import java.util.Map;

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
	import org.springframework.context.ApplicationContext;
	import org.springframework.context.ApplicationContextAware;
	import org.springframework.web.servlet.view.document.AbstractExcelView;
	import org.springframework.web.servlet.view.document.AbstractXlsxStreamingView;

	import com.nmims.beans.CenterExamBean;
import com.nmims.beans.EmbaPassFailBean;
import com.nmims.beans.ExamBookingTransactionBean;
	import com.nmims.beans.ExamCenterBean;
	import com.nmims.beans.ExamCenterSlotMappingBean;
	import com.nmims.beans.StudentExamBean;
	import com.nmims.daos.StudentMarksDAO;


	public class MBAXPassFailReportView extends AbstractXlsxStreamingView  implements ApplicationContextAware{
		
		private static ApplicationContext act = null;
		
		@Override
		protected void buildExcelDocument(Map model, Workbook workbook,	HttpServletRequest request, HttpServletResponse response)
			throws Exception {
	 
			act = getApplicationContext();
			StudentMarksDAO dao = (StudentMarksDAO)act.getBean("studentMarksDAO");
			HashMap<String, StudentExamBean> sapIdStudentsMap = dao.getAllStudents();
			HashMap<String, CenterExamBean> icLcMap = dao.getICLCMap();
			
			List<EmbaPassFailBean> passFailResultsList = (List<EmbaPassFailBean>) model.get("passFailResultsList");
			//create a wordsheet
			Sheet sheet = workbook.createSheet("Pass Fail List");
			int index = 0;
			Row header = sheet.createRow(0);
			header.createCell(index++).setCellValue("Sr. No.");
			header.createCell(index++).setCellValue("Batch Id.");
			header.createCell(index++).setCellValue("Batch Name");
			header.createCell(index++).setCellValue("SAP Id");
			header.createCell(index++).setCellValue("First Name");
			header.createCell(index++).setCellValue("Last Name");
			header.createCell(index++).setCellValue("Program");
		    header.createCell(index++).setCellValue("Subject");
		    
		    header.createCell(index++).setCellValue("Status");
		    header.createCell(index++).setCellValue("IA Score");
		    header.createCell(index++).setCellValue("TEE Score");
		    header.createCell(index++).setCellValue("Grace Marks");
		    header.createCell(index++).setCellValue("Total");
		    header.createCell(index++).setCellValue("Is Pass");
		    header.createCell(index++).setCellValue("Fail Reason");
		    header.createCell(index++).setCellValue("Is Result Live");
		    header.createCell(index++).setCellValue("LC");
	 
			int rowNum = 1;
			for (int i = 0 ; i < passFailResultsList.size(); i++) {
				index = 0;
				//create the row data
				Row row = sheet.createRow(rowNum++);
				EmbaPassFailBean bean = passFailResultsList.get(i);
				
				StudentExamBean student = sapIdStudentsMap.get(bean.getSapid());
				
				row.createCell(index++).setCellValue((i+1));
				row.createCell(index++).setCellValue(bean.getBatch_id());
				row.createCell(index++).setCellValue(bean.getBatchName());
				row.createCell(index++).setCellValue(new Double(bean.getSapid()));
				row.createCell(index++).setCellValue(student.getFirstName());
				row.createCell(index++).setCellValue(student.getLastName());
				row.createCell(index++).setCellValue(student.getProgram());
				row.createCell(index++).setCellValue(bean.getSubject());
				row.createCell(index++).setCellValue(bean.getStatus());
				row.createCell(index++).setCellValue(bean.getIaScore());
				row.createCell(index++).setCellValue(bean.getTeeScore());
				row.createCell(index++).setCellValue(bean.getGraceMarks());
				row.createCell(index++).setCellValue(bean.getTotal());
				row.createCell(index++).setCellValue(bean.getIsPass());
				row.createCell(index++).setCellValue(bean.getFailReason());
				row.createCell(index++).setCellValue(bean.getIsResultLive());
				row.createCell(index++).setCellValue(bean.getLc());
	        }
			
		
		}

	}


