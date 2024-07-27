package com.nmims.views;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.web.servlet.view.document.AbstractXlsxStreamingView;

import com.nmims.beans.CenterExamBean;
import com.nmims.beans.ExamBookingRefundRequestReportBean;
import com.nmims.beans.StudentExamBean;
import com.nmims.daos.StudentMarksDAO;
public class ExamBookingRefundRequestReportExcelView  extends AbstractXlsxStreamingView  implements ApplicationContextAware{

    private static ApplicationContext act = null;
    @Override
    protected void buildExcelDocument(Map model, Workbook workbook,	HttpServletRequest request, HttpServletResponse response)
            throws Exception {

        act = getApplicationContext();
        List<ExamBookingRefundRequestReportBean> examBookingRefundRequestReportBeanList = (List<ExamBookingRefundRequestReportBean>) model.get("reportList");
        StudentMarksDAO dao = (StudentMarksDAO)act.getBean("studentMarksDAO");
        HashMap<String,CenterExamBean> getICLCMap = dao.getICLCMap();
        HashMap<String, StudentExamBean> sapIdStudentsMap = dao.getAllStudents();
        Sheet sheet = workbook.createSheet("Report");

        Row header = sheet.createRow(0);
        header.createCell(0).setCellValue("Sr. No.");
        header.createCell(1).setCellValue("Student Number");
        header.createCell(2).setCellValue("Track ID");
        header.createCell(3).setCellValue("Name");
        header.createCell(4).setCellValue("Email ID");
        header.createCell(5).setCellValue("Mobile Number");
        header.createCell(6).setCellValue("LC");
        header.createCell(7).setCellValue("IC");
        header.createCell(8).setCellValue("Total Fees");
        header.createCell(9).setCellValue("Options");
        header.createCell(10).setCellValue("Description");
        header.createCell(11).setCellValue("Submission (Date and Time)");
        header.createCell(12).setCellValue("Subject");

        int rowNum = 1;
        for (int i = 0 ; i < examBookingRefundRequestReportBeanList.size(); i++) {

            Row row = sheet.createRow(rowNum++);
            ExamBookingRefundRequestReportBean bean = examBookingRefundRequestReportBeanList.get(i);

            StudentExamBean student = sapIdStudentsMap.get(bean.getSapid());
            String ic = "";
            String lc = "";
            if(student != null){
                CenterExamBean center = getICLCMap.get(student.getCenterCode());
                if(center != null){
                    lc = center.getLc();
                    ic = center.getCenterName();
                }
            }

            row.createCell(0).setCellValue(new Double(i+1));
            row.createCell(1).setCellValue(bean.getSapid());
            row.createCell(2).setCellValue(bean.getTrackId());
            row.createCell(3).setCellValue(bean.getName());
            row.createCell(4).setCellValue(bean.getEmailId());
            row.createCell(5).setCellValue(bean.getMobile());
            row.createCell(6).setCellValue(ic);
            row.createCell(7).setCellValue(lc);
            row.createCell(8).setCellValue(bean.getAmount());
            row.createCell(9).setCellValue(bean.getOptions());
            row.createCell(10).setCellValue(bean.getDescription());
            row.createCell(11).setCellValue(bean.getSubmissionDate());
            if("Carry Forward".equalsIgnoreCase(bean.getOptions())) {
            	row.createCell(12).setCellValue(bean.getSubject());
            }
        }


    }
}