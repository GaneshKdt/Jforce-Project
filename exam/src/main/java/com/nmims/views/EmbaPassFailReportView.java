package com.nmims.views;

	import java.util.ArrayList;
import java.util.HashMap;
	import java.util.List;
	import java.util.Map;

	import javax.servlet.http.HttpServletRequest;
	import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
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
import com.nmims.beans.DissertationResultDTO;
import com.nmims.beans.EmbaPassFailBean;
import com.nmims.beans.ExamBookingTransactionBean;
	import com.nmims.beans.ExamCenterBean;
	import com.nmims.beans.ExamCenterSlotMappingBean;
import com.nmims.beans.Q7Q8DissertationResultBean;
import com.nmims.beans.StudentExamBean;
	import com.nmims.daos.StudentMarksDAO;


	public class EmbaPassFailReportView extends AbstractXlsxStreamingView  implements ApplicationContextAware{
		
		private static ApplicationContext act = null;
		
		@Override
		protected void buildExcelDocument(Map model, Workbook workbook,	HttpServletRequest request, HttpServletResponse response)
			throws Exception {
	 
			act = getApplicationContext();
			StudentMarksDAO dao = (StudentMarksDAO)act.getBean("studentMarksDAO");
			HashMap<String, StudentExamBean> sapIdStudentsMap = dao.getAllStudents();
			HashMap<String, CenterExamBean> icLcMap = dao.getICLCMap();
			Q7Q8DissertationResultBean dissertionReportBean=(Q7Q8DissertationResultBean) model.get("dissertionReportBean");
			List<EmbaPassFailBean> passFailResultsList = dissertionReportBean.getPassFailResultsList();
			ArrayList<DissertationResultDTO> q7ResultList= dissertionReportBean.getQ7ResultList();
			ArrayList<DissertationResultDTO> q8ResultList= dissertionReportBean.getQ8ResultList();
			//create a wordsheet
			
			Sheet sheet = null;
			int index = 0;
			Row header = null;
			int rowNum = 0;
			if(passFailResultsList.size()>0) {
			 sheet = workbook.createSheet("Pass Fail List");
			 index = 0;
			 header = sheet.createRow(0);
			
			header.createCell(index++).setCellValue("Sr. No.");
//			header.createCell(index++).setCellValue("Batch Id.");
			header.createCell(index++).setCellValue("Batch Name.");
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
	 
		     rowNum = 1;
			for (int i = 0 ; i < passFailResultsList.size(); i++) {
				index = 0;
				//create the row data
				Row row = sheet.createRow(rowNum++);
				EmbaPassFailBean bean = passFailResultsList.get(i);
				
				StudentExamBean student = sapIdStudentsMap.get(bean.getSapid());
				
				row.createCell(index++).setCellValue((i+1));
//				row.createCell(index++).setCellValue(bean.getBatch_id());
				row.createCell(index++).setCellValue(bean.getBatchName());
				row.createCell(index++).setCellValue(new Double(bean.getSapid()));
				row.createCell(index++).setCellValue(student.getFirstName());
				row.createCell(index++).setCellValue(student.getLastName());
				row.createCell(index++).setCellValue(student.getProgram());
				row.createCell(index++).setCellValue(bean.getSubject());
				row.createCell(index++).setCellValue(bean.getStatus());
				row.createCell(index++).setCellValue(bean.getIaScore());
				if(bean.getTeeScore() != null) {
					row.createCell(index++).setCellValue(bean.getTeeScore());
				}else {
					row.createCell(index++).setCellValue("");
				}
				row.createCell(index++).setCellValue(bean.getGraceMarks());
				row.createCell(index++).setCellValue(bean.getTotal());
				row.createCell(index++).setCellValue(bean.getIsPass());
				row.createCell(index++).setCellValue(bean.getFailReason());
				row.createCell(index++).setCellValue(bean.getIsResultLive());
				row.createCell(index++).setCellValue(bean.getLc());
	        }
			}
			if(!(q7ResultList.isEmpty())){
			 sheet = null;
			 sheet = workbook.createSheet("Dissertation Part - I Pass Fail Report");
			 index = 0;
			 header = null;
			 header = sheet.createRow(0);
			header.createCell(index++).setCellValue("Sr. No.");
//			header.createCell(index++).setCellValue("Batch Id.");
			header.createCell(index++).setCellValue("Batch Name.");
			header.createCell(index++).setCellValue("SAP Id");
			header.createCell(index++).setCellValue("First Name");
			header.createCell(index++).setCellValue("Last Name");
			header.createCell(index++).setCellValue("Program");
			header.createCell(index++).setCellValue("Subject");
			header.createCell(index++).setCellValue("Sem");
			
			header.createCell(index++).setCellValue("Component A Status");
			header.createCell(index++).setCellValue("Component B Status");
			header.createCell(index++).setCellValue("Component A Score");
			header.createCell(index++).setCellValue("Component B Score");
//			header.createCell(index++).setCellValue("Grace Marks");
			
			header.createCell(index++).setCellValue("Total");
			header.createCell(index++).setCellValue("Is Pass");
			header.createCell(index++).setCellValue("Fail Reason");
			header.createCell(index++).setCellValue("Is Result Live");
			header.createCell(index++).setCellValue("LC");
			
			 rowNum = 1;
			for (int i = 0 ; i < q7ResultList.size(); i++) {
				index = 0;
				//create the row data
				Row row = sheet.createRow(rowNum++);
				DissertationResultDTO bean = q7ResultList.get(i);
				
				
				row.createCell(index++).setCellValue((i+1));
				row.createCell(index++).setCellValue(bean.getBatchName());
				row.createCell(index++).setCellValue(bean.getSapid());
				row.createCell(index++).setCellValue(bean.getFirstName());
				row.createCell(index++).setCellValue(bean.getLastName());
				row.createCell(index++).setCellValue(bean.getProgram());
				row.createCell(index++).setCellValue(bean.getSubject());
				row.createCell(index++).setCellValue(bean.getSem());
				
				row.createCell(index++).setCellValue(bean.getComponent_a_status());
				row.createCell(index++).setCellValue(bean.getComponent_b_status());
				row.createCell(index++).setCellValue(bean.getComponent_a_score());
				row.createCell(index++).setCellValue(bean.getComponent_b_score());
				
//				row.createCell(index++).setCellValue(bean.getGraceMarks());
				row.createCell(index++).setCellValue((bean.getComponent_a_score())+(bean.getComponent_b_score()));
				row.createCell(index++).setCellValue(bean.getIsPass());
				row.createCell(index++).setCellValue(bean.getFailReason());
				row.createCell(index++).setCellValue(bean.getIsResultLive());
				row.createCell(index++).setCellValue(bean.getCenterName());
			}
			}
			if(!(q8ResultList.isEmpty())){
				sheet = null;
				sheet = workbook.createSheet("Dissertation Part- II Pass Fail Report");
				index = 0;
				header = null;
				header = sheet.createRow(0);
				header.createCell(index++).setCellValue("Sr. No.");
//			header.createCell(index++).setCellValue("Batch Id.");
				header.createCell(index++).setCellValue("Batch Name.");
				header.createCell(index++).setCellValue("SAP Id");
				header.createCell(index++).setCellValue("First Name");
				header.createCell(index++).setCellValue("Last Name");
				header.createCell(index++).setCellValue("Program");
				header.createCell(index++).setCellValue("Subject");
				header.createCell(index++).setCellValue("Sem");
				
				header.createCell(index++).setCellValue("Component C Status");
				header.createCell(index++).setCellValue("Component C Score");
				header.createCell(index++).setCellValue("Grace Marks");
				
				header.createCell(index++).setCellValue("Total");
				header.createCell(index++).setCellValue("Is Pass");
				header.createCell(index++).setCellValue("Fail Reason");
				header.createCell(index++).setCellValue("Is Result Live");
				header.createCell(index++).setCellValue("LC");
				
				rowNum = 1;
				for (int i = 0 ; i < q8ResultList.size(); i++) {
					index = 0;
					//create the row data
					Row row = sheet.createRow(rowNum++);
					DissertationResultDTO bean = q8ResultList.get(i);
					
					
					row.createCell(index++).setCellValue((i+1));
					row.createCell(index++).setCellValue(bean.getBatchName());
					row.createCell(index++).setCellValue(bean.getSapid());
					row.createCell(index++).setCellValue(bean.getFirstName());
					row.createCell(index++).setCellValue(bean.getLastName());
					row.createCell(index++).setCellValue(bean.getProgram());
					row.createCell(index++).setCellValue(bean.getSubject());
					row.createCell(index++).setCellValue(bean.getSem());
					
					row.createCell(index++).setCellValue(bean.getComponent_c_status());
					row.createCell(index++).setCellValue(bean.getComponent_c_score());
					
					row.createCell(index++).setCellValue(bean.getGraceMarks());
					row.createCell(index++).setCellValue(bean.getTotal());
					row.createCell(index++).setCellValue(bean.getIsPass());
					row.createCell(index++).setCellValue(bean.getFailReason());
					row.createCell(index++).setCellValue(bean.getIsResultLive());
					row.createCell(index++).setCellValue(bean.getCenterName());
				}
			
			}
		
		
	}

	}


