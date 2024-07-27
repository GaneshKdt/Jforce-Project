package com.nmims.views;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.exception.ExceptionUtils;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.streaming.SXSSFRow;
import org.apache.poi.xssf.streaming.SXSSFSheet;
import org.jsoup.Jsoup;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.web.servlet.view.document.AbstractXlsxStreamingView;

import com.nmims.beans.SessionPlanModuleBean;


public class SessionModulesPlanReportExcelView extends AbstractXlsxStreamingView implements ApplicationContextAware{

	private static ApplicationContext act = null;
	private static final Logger logger = LoggerFactory.getLogger(SessionModulesPlanReportExcelView.class);
	
	@SuppressWarnings("unchecked")
	@Override
	protected void buildExcelDocument(Map<String, Object> model, Workbook workbook, HttpServletRequest request,
			HttpServletResponse response) throws Exception {		
				
		logger.info("Entered buildExcelDocument() method of SessionModulesPlanReportExcelView");		
	
		act = getApplicationContext();		
		int index = 0;
		
		List<SessionPlanModuleBean> sessionPlanModulesList = new ArrayList();
				
		try {
			sessionPlanModulesList = (List<SessionPlanModuleBean>) model.get("sessionPlanModulesList");
			
			SXSSFSheet sessionPlanSheet = (SXSSFSheet) workbook.createSheet("Session Plan");			
			SXSSFRow header = (SXSSFRow) sessionPlanSheet.createRow(0);
			header.createCell(index++).setCellValue("Sr. No.");
			header.createCell(index++).setCellValue("Acads Year");
			header.createCell(index++).setCellValue("Acads Month");
			header.createCell(index++).setCellValue("Consumer Type");
			header.createCell(index++).setCellValue("Program Structure");
			header.createCell(index++).setCellValue("Program");
			header.createCell(index++).setCellValue("Applicable Batch");
			header.createCell(index++).setCellValue("Subject");
			header.createCell(index++).setCellValue("Faculty Name");
			header.createCell(index++).setCellValue("Faculty ID");
			header.createCell(index++).setCellValue("No Of Sessions");
			header.createCell(index++).setCellValue("No Of Group work (if Any)");
			header.createCell(index++).setCellValue("No Of Assessments");
			header.createCell(index++).setCellValue("Course Rationale");

			header.createCell(index++).setCellValue("Course Objectives");
			header.createCell(index++).setCellValue("Learning Outcomes");
			header.createCell(index++).setCellValue("Pre-requisites");
			header.createCell(index++).setCellValue("Pedagogy");
			header.createCell(index++).setCellValue("TextBook");
			header.createCell(index++).setCellValue("Journals for session plan module");
			header.createCell(index++).setCellValue("Links To Websites");
			header.createCell(index++).setCellValue("Innovative Pedagogy Used");
			header.createCell(index++).setCellValue("Case Study Name");
			header.createCell(index++).setCellValue("Pedagogical Tool");
			header.createCell(index++).setCellValue("Innovative Method For Teaching");
			header.createCell(index++).setCellValue("Case Study Source");
			header.createCell(index++).setCellValue("Case Study Type");
			header.createCell(index++).setCellValue("Time Stamp");
			 
			int rowNum = 1;			
			for (int i = 0; i < 1; i++) {
				index = 0;
				Row row = sessionPlanSheet.createRow(rowNum++);				
				SessionPlanModuleBean bean = sessionPlanModulesList.get(i);
				
				row.createCell(index++).setCellValue(rowNum-1);
				row.createCell(index++).setCellValue(bean.getAcadYear());
				row.createCell(index++).setCellValue(bean.getAcadMonth());
				row.createCell(index++).setCellValue(bean.getConsumerType());
				row.createCell(index++).setCellValue(bean.getProgramStructure());
 
				row.createCell(index++).setCellValue(bean.getProgram());
				row.createCell(index++).setCellValue(bean.getBatchName());
				row.createCell(index++).setCellValue(bean.getSubject());
				row.createCell(index++).setCellValue(bean.getFacultyName());
				row.createCell(index++).setCellValue(bean.getFacultyId());
				row.createCell(index++).setCellValue(bean.getNoOfClassroomSessions());
				row.createCell(index++).setCellValue(bean.getNoOf_Practical_Group_Work());
				row.createCell(index++).setCellValue(bean.getNoOfAssessments());
				row.createCell(index++).setCellValue(Jsoup.parse(bean.getCourseRationale()).text());
				row.createCell(index++).setCellValue(Jsoup.parse(bean.getObjectives()).text());
				row.createCell(index++).setCellValue(Jsoup.parse(bean.getLearningOutcomes()).text());
				row.createCell(index++).setCellValue(Jsoup.parse(bean.getPrerequisites()).text());
				row.createCell(index++).setCellValue(Jsoup.parse(bean.getPedagogy()).text());
				row.createCell(index++).setCellValue(Jsoup.parse(bean.getTextbook()).text());
				row.createCell(index++).setCellValue(Jsoup.parse(bean.getJournals()).text());
				row.createCell(index++).setCellValue(Jsoup.parse(bean.getLinks()).text());
				row.createCell(index++).setCellValue(Jsoup.parse(bean.getPedagogyUsed()).text());
				row.createCell(index++).setCellValue(Jsoup.parse(bean.getCasestudyName()).text());
				row.createCell(index++).setCellValue(Jsoup.parse(bean.getPedagogicalTool()).text());
				row.createCell(index++).setCellValue(Jsoup.parse(bean.getTeachingMethod()).text());
				row.createCell(index++).setCellValue(bean.getCasestudySource());
				row.createCell(index++).setCellValue(bean.getCasestudyType());
				row.createCell(index++).setCellValue(bean.getSessionPlanTimeStamp());
			}
			
			logger.info("Exiting buildExcelDocument() method of SessionModulesPlanReportExcelView");
			
		} catch (RuntimeException e) {
			logger.error(ExceptionUtils.getFullStackTrace(e));
		}
		
		index = 0;		
		try {
			SXSSFSheet moduleCreationSheet = (SXSSFSheet) workbook.createSheet("Module Creation");
			SXSSFRow header = (SXSSFRow) moduleCreationSheet.createRow(0);
			header.createCell(index++).setCellValue("Sr. No.");
			header.createCell(index++).setCellValue("Subject");
			header.createCell(index++).setCellValue("Faculty Name");
			header.createCell(index++).setCellValue("Faculty ID");
			header.createCell(index++).setCellValue("Module Topic");
			header.createCell(index++).setCellValue("Chapter");
			header.createCell(index++).setCellValue("Session Number");
			header.createCell(index++).setCellValue("Module Outcomes");
			header.createCell(index++).setCellValue("Module Pedagogical Tool");
			header.createCell(index++).setCellValue("Time Stamp");
			
			int rowNum = 1;
			for (int i = 0; i < sessionPlanModulesList.size(); i++) {
				index = 0;
				Row row = moduleCreationSheet.createRow(rowNum++);				
				SessionPlanModuleBean bean = sessionPlanModulesList.get(i);
				
				row.createCell(index++).setCellValue(rowNum-1);
				row.createCell(index++).setCellValue(bean.getSubject());
				row.createCell(index++).setCellValue(bean.getFacultyName());
				row.createCell(index++).setCellValue(bean.getFacultyId());
				row.createCell(index++).setCellValue(bean.getTopic());
				row.createCell(index++).setCellValue(bean.getChapter());
				row.createCell(index++).setCellValue(bean.getSessionName());				
				row.createCell(index++).setCellValue(Jsoup.parse(bean.getOutcomes()).text());
				row.createCell(index++).setCellValue(Jsoup.parse(bean.getModulePedagogicalTool()).text());
				row.createCell(index++).setCellValue(bean.getSessionPlanModuleTimeStamp());
			}
			
		} catch (RuntimeException e) {
			logger.error(ExceptionUtils.getFullStackTrace(e));
		}
		
		index = 0;
		try {
			SXSSFSheet dateMappingSheet = (SXSSFSheet) workbook.createSheet("Date Mapping");
			SXSSFRow header = (SXSSFRow) dateMappingSheet.createRow(0);
			header.createCell(index++).setCellValue("Sr. No.");
			header.createCell(index++).setCellValue("Session No.");
			header.createCell(index++).setCellValue("Date");
			header.createCell(index++).setCellValue("Start Time");
			header.createCell(index++).setCellValue("Time Stamp");
			
			int rowNum = 1;
			for (int i = 0; i < sessionPlanModulesList.size(); i++) {
				index = 0;
				Row row = dateMappingSheet.createRow(rowNum++);				
				SessionPlanModuleBean bean = sessionPlanModulesList.get(i);
				
				row.createCell(index++).setCellValue(rowNum-1);
				row.createCell(index++).setCellValue(bean.getSessionName());				
				row.createCell(index++).setCellValue(bean.getDate());
				row.createCell(index++).setCellValue(bean.getStartTime());
				row.createCell(index++).setCellValue(bean.getSessionTimeStamp());
			}
			
		} catch (RuntimeException e) {
			logger.error(ExceptionUtils.getFullStackTrace(e));
		}
	}
}
