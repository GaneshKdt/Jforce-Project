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
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.view.document.AbstractXlsxStreamingView;

import com.nmims.beans.AcadCycleFeedback;
import com.nmims.beans.ExamBookingTransactionStudentPortalBean;

@Component("acadCycleFeedbackExcelView")
public class AcadCycleFeedbackExcelView extends AbstractXlsxStreamingView  implements ApplicationContextAware{
	
	@Override
	protected void buildExcelDocument(@SuppressWarnings("rawtypes") Map model, Workbook workbook,	HttpServletRequest request, HttpServletResponse response)
		throws Exception {
 
		@SuppressWarnings("unchecked")
		ArrayList<AcadCycleFeedback> feedbackList = (ArrayList<AcadCycleFeedback>) model.get("feedbackList");
		//create a wordsheet
		Sheet sheet = workbook.createSheet("Survey");
		
		int index = 0;
		Row header = sheet.createRow(0);
		header.createCell(index++).setCellValue("Sr. No.");
		header.createCell(index++).setCellValue("Feedback Given");
		header.createCell(index++).setCellValue("Acad Year");
		header.createCell(index++).setCellValue("Acad Month");
		header.createCell(index++).setCellValue("SAP ID");
		header.createCell(index++).setCellValue("Program");
		header.createCell(index++).setCellValue("Sem");
		header.createCell(index++).setCellValue("q1Response (The Orientation program was in line with your Academic cycle? / Re-registration process was easy?)");
		header.createCell(index++).setCellValue("q1Remark");
		header.createCell(index++).setCellValue("q2Response (Student Portal navigation for the first time was easy /  Student Portal navigation is user friendly)");
		header.createCell(index++).setCellValue("q2Remark");
		header.createCell(index++).setCellValue("q3aResponse (Ease of attending online sessions )");
		header.createCell(index++).setCellValue("q3aRemark");
		header.createCell(index++).setCellValue("q3bResponse (Access recordings (Watch and download )");
		header.createCell(index++).setCellValue("q3bRemark");
		header.createCell(index++).setCellValue("q3cResponse (Assignment preparation process )");
		header.createCell(index++).setCellValue("q3cRemark");
		header.createCell(index++).setCellValue("q3dResponse (Curriculum and content met my expectation )");
		header.createCell(index++).setCellValue("q3dRemark");
		header.createCell(index++).setCellValue("q3eResponse (Exam facilitation was seamless )");
		header.createCell(index++).setCellValue("q3eRemark");
		header.createCell(index++).setCellValue("q3fResponse (Information you need is easily available on Student )");
		header.createCell(index++).setCellValue("q3fRemark");
		header.createCell(index++).setCellValue("q4Response (Online sessions are sufficient and effective for preparation of examination )");
		header.createCell(index++).setCellValue("q4Remark");
		header.createCell(index++).setCellValue("q5Response (Queries raised so far were answered as per my expectation by the University )");
		header.createCell(index++).setCellValue("q5Remark");
		header.createCell(index++).setCellValue("q6Response (I get enough Support from the Regional office  )");
		header.createCell(index++).setCellValue("q6Remark");
		header.createCell(index++).setCellValue("q7Response (I get enough Support from the Authorised Partner  )");
		header.createCell(index++).setCellValue("q7Remark");
		header.createCell(index++).setCellValue("q8Response (. I received all notifications on time during the Academic cycle (Exams, Assignment submission, Online sessions, etc..))");
		header.createCell(index++).setCellValue("q8Remark");


		
		
		
		int rowNum = 1;
		for (int i = 0 ; i < feedbackList.size(); i++) {
			//create the row data
			index = 0;
			Row row = sheet.createRow(rowNum++);
			AcadCycleFeedback bean = feedbackList.get(i);
			row.createCell(index++).setCellValue(rowNum-1);
			row.createCell(index++).setCellValue(bean.getFeedbackGiven());
			row.createCell(index++).setCellValue(bean.getYear());
			row.createCell(index++).setCellValue(bean.getMonth());
			row.createCell(index++).setCellValue(new Double(bean.getSapid()));
			row.createCell(index++).setCellValue(bean.getProgram());
			row.createCell(index++).setCellValue(new Double(bean.getSem()));
			row.createCell(index++).setCellValue(bean.getQ1Response());
			row.createCell(index++).setCellValue(bean.getQ1Remark());
			row.createCell(index++).setCellValue(bean.getQ2Response());
			row.createCell(index++).setCellValue(bean.getQ2Remark());
			row.createCell(index++).setCellValue(bean.getQ3aResponse());
			row.createCell(index++).setCellValue(bean.getQ3aRemark());
			row.createCell(index++).setCellValue(bean.getQ3bResponse());
			row.createCell(index++).setCellValue(bean.getQ3bRemark());
			row.createCell(index++).setCellValue(bean.getQ3cResponse());
			row.createCell(index++).setCellValue(bean.getQ3cRemark());
			row.createCell(index++).setCellValue(bean.getQ3dResponse());
			row.createCell(index++).setCellValue(bean.getQ3dRemark());
			row.createCell(index++).setCellValue(bean.getQ3eResponse());
			row.createCell(index++).setCellValue(bean.getQ3eRemark());
			row.createCell(index++).setCellValue(bean.getQ3fResponse());
			row.createCell(index++).setCellValue(bean.getQ3fRemark());
			row.createCell(index++).setCellValue(bean.getQ4Response());
			row.createCell(index++).setCellValue(bean.getQ4Remark());
			row.createCell(index++).setCellValue(bean.getQ5Response());
			row.createCell(index++).setCellValue(bean.getQ5Remark());
			row.createCell(index++).setCellValue(bean.getQ6Response());
			row.createCell(index++).setCellValue(bean.getQ6Remark());
			row.createCell(index++).setCellValue(bean.getQ7Response());
			row.createCell(index++).setCellValue(bean.getQ7Remark());
			row.createCell(index++).setCellValue(bean.getQ8Response());
			row.createCell(index++).setCellValue(bean.getQ8Remark());

        }


	
	}
}
