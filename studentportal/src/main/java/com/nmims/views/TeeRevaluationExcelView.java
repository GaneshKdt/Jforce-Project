package com.nmims.views;

import java.util.ArrayList;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.view.document.AbstractExcelView;

import com.nmims.beans.SRTeeRevaluationReportBean;

@Component("teeRevaluationExcelView")
public class TeeRevaluationExcelView extends AbstractExcelView implements ApplicationContextAware {
	@Override
	protected void buildExcelDocument(Map<String, Object> model, HSSFWorkbook workbook, HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		try {
			
			ArrayList<SRTeeRevaluationReportBean> reportData =(ArrayList<SRTeeRevaluationReportBean>)request.getSession().getAttribute("reportData");
			System.out.println("This is Report Data "+ reportData);
		//	HSSFWorkbook sxssfWorkbook= new HSSFWorkbook();
			HSSFSheet sheet = workbook.createSheet("srTeeReport");
			
			int index = 0;
			HSSFRow header = sheet.createRow(0);
			header.createCell(index++).setCellValue("Sr. No.");
			header.createCell(index++).setCellValue("Exam Year"); 
			header.createCell(index++).setCellValue("Exam Month");
			header.createCell(index++).setCellValue("ServiceRequest No ");
			header.createCell(index++).setCellValue("SAPID");
			header.createCell(index++).setCellValue("StudentName");
			header.createCell(index++).setCellValue("program");
			header.createCell(index++).setCellValue("Sem");
			header.createCell(index++).setCellValue("Assessment Name");
			header.createCell(index++).setCellValue("SubCode");
			header.createCell(index++).setCellValue("Test Taker's Email Id"); 
			header.createCell(index++).setCellValue("Test Invitation Key");
			header.createCell(index++).setCellValue("Test Id (Mettl Assessment Id )");
			header.createCell(index++).setCellValue("Test Taker's Unique id");
			header.createCell(index++).setCellValue("Original Evaluator Name");
			header.createCell(index++).setCellValue("Original Sec 4 Marks");
			header.createCell(index++).setCellValue("Original Total Score Rounded");
		
			int rowNum = 1;
			System.out.println("Size of ReportData list "+reportData.size());
			for (SRTeeRevaluationReportBean bean : reportData ) {
				index = 0;
				System.out.println("SRTeeRevaluationReportBean  "+ bean.toString());
				//create the row data
				try {
					HSSFRow row = sheet.createRow(rowNum++);
					if(StringUtils.isEmpty(bean.getEvaluatorName())) {
						bean.setEvaluatorName("");
					}
					if(StringUtils.isEmpty(bean.getTestID())) {
						bean.setTestID("");
					}if(StringUtils.isEmpty(bean.getTestTaker_UniqueID())) {
						bean.setTestTaker_UniqueID("");
					}if(StringUtils.isEmpty(bean.getEvaluatorName())) {
						bean.setEvaluatorName("");
					}if(StringUtils.isEmpty(bean.getsNo())) {
						bean.setsNo("");
					}

				
					  row.createCell(index++).setCellValue(rowNum-1);
					  row.createCell(index++).setCellValue(bean.getExamYear());
					  row.createCell(index++).setCellValue(bean.getExamMonth());
					  row.createCell(index++).setCellValue(bean.getServiceRequest_no());
					  row.createCell(index++).setCellValue(bean.getSapid());
					  row.createCell(index++).setCellValue(bean.getStudentName());
					  row.createCell(index++).setCellValue(bean.getProgram());
					  row.createCell(index++).setCellValue(bean.getSem());
					  row.createCell(index++).setCellValue(bean.getAssessment_Name());
					  row.createCell(index++).setCellValue(bean.getSubCode());
					  row.createCell(index++).setCellValue(bean.getTestTaker_EmailId());
					  row.createCell(index++).setCellValue(bean.getTestInvitation_Key());
					  row.createCell(index++).setCellValue(bean.getTestID());
					  row.createCell(index++).setCellValue(bean.getTestTaker_UniqueID());
					  row.createCell(index++).setCellValue(bean.getEvaluatorName());
					  row.createCell(index++).setCellValue(bean.getOriginal_Sec_4_Marks());
					  row.createCell(index++).setCellValue(bean.getOriginal_Total());
					 				
					 
//					 String fileName = " SR Tee Report.xlsx"; //Your file name here.
//					 response.setContentType("application/octet-stream"); //Tell the browser to expect an excel file
//					 response.setHeader("Content-Disposition", "attachment; filename="+fileName); //Tell the browser it should be named as the custom file name
//					 
//					 
//					 //Written to write excel file to outputStream and flush it out of controller itself//
//					 ByteArrayOutputStream outByteStream = new ByteArrayOutputStream();
//					 workbook.write(outByteStream);
//					 byte [] outArray = outByteStream.toByteArray();
//					 response.setContentLength(outArray.length);
//					 response.setHeader("Expires:", "0"); // eliminates browser caching
//					 ServletOutputStream outStream = response.getOutputStream();
//					 outStream.write(outArray);
//					 outStream.flush();
//					 
				} catch (Exception e) {
					
					e.printStackTrace();
				}
			}
			
		}
			catch (Exception e) {
				
				e.printStackTrace();
			}
			
			
			
			
		 
						
	}
	
		

		
	}
