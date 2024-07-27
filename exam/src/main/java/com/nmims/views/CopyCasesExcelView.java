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

import com.nmims.beans.ResultDomain;
import com.nmims.beans.TestExamBean;

public class CopyCasesExcelView   extends AbstractXlsxStreamingView  implements ApplicationContextAware {
	
	@Override
	protected void buildExcelDocument(Map model, Workbook workbook,	HttpServletRequest request, HttpServletResponse response)
		throws Exception {
		List<ResultDomain> copyCases =   (List<ResultDomain>) model.get("copyCases");
		
		//create a wordsheet :- START
		Sheet sheet = workbook.createSheet("List Of Copy Cases");
		
		int index = 0;
		Row header = sheet.createRow(0);

		header.createCell(index++).setCellValue("Sr NO.");
		header.createCell(index++).setCellValue("Subject");
		header.createCell(index++).setCellValue("SAP ID 1");
		header.createCell(index++).setCellValue("SAP ID 1 First Name");
		header.createCell(index++).setCellValue("SAP ID 1 Last Name");
		header.createCell(index++).setCellValue("SAP ID 1 Status");
		header.createCell(index++).setCellValue("SAP ID 2");
		header.createCell(index++).setCellValue("SAP ID 2 First Name");
		header.createCell(index++).setCellValue("SAP ID 2 Last Name");
		header.createCell(index++).setCellValue("SAP ID 2 Status");
		header.createCell(index++).setCellValue("Matching %");
		header.createCell(index++).setCellValue("Max Consecutive Lines Matched");
		header.createCell(index++).setCellValue("# of Lines in First Answer");
		header.createCell(index++).setCellValue("# of Lines in Second Answer");
		header.createCell(index++).setCellValue("# of Lines matched");
		header.createCell(index++).setCellValue("Sapid1 Answered DateTime");
		header.createCell(index++).setCellValue("Sapid2 Answered DateTime");
		
 
		int rowNum = 1;
		for (int i = 0 ; i < copyCases.size(); i++) {
			//create the row data
			index = 0;
			Row row = sheet.createRow(rowNum++);
			ResultDomain copyResult = copyCases.get(i);
			
			
 
			
			 
			row.createCell(index++).setCellValue(i+1);			 
			row.createCell(index++).setCellValue(copyResult.getSubject());
			
			 
			row.createCell(index++).setCellValue(copyResult.getSapId1());
			

			 
			row.createCell(index++).setCellValue(copyResult.getFirstName1());

			 
			row.createCell(index++).setCellValue(copyResult.getLastName1());
			row.createCell(index++).setCellValue(copyResult.getSapid1AttemptStatus());
			
			 
			row.createCell(index++).setCellValue(copyResult.getSapId2());
			

			 
			row.createCell(index++).setCellValue(copyResult.getFirstName2());

			 
			row.createCell(index++).setCellValue(copyResult.getLastName2());
			row.createCell(index++).setCellValue(copyResult.getSapid2AttemptStatus());
			
			 
			row.createCell(index++).setCellValue(copyResult.getMatching());
			
			
			 
			row.createCell(index++).setCellValue(copyResult.getMaxConseutiveLinesMatched());
			
			 
			row.createCell(index++).setCellValue(copyResult.getNumberOfLinesInFirstFile());
			
			 
			row.createCell(index++).setCellValue(copyResult.getNumberOfLinesInSecondFile());
			
			 
			row.createCell(index++).setCellValue(copyResult.getNoOfMatches());
			
			 
			row.createCell(index++).setCellValue(copyResult.getFirstSapidTestAnswerCreatedDate());

			 
			row.createCell(index++).setCellValue(copyResult.getSecondSapidTestAnswerCreatedDate());


        }
		
	
		
	
	}
}

