package com.nmims.views;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.view.document.AbstractExcelView;

import com.nmims.beans.AdhocPaymentStudentPortalBean;
import com.nmims.beans.CenterStudentPortalBean;
import com.nmims.beans.StudentStudentPortalBean;
import com.nmims.daos.ServiceRequestDao;

@Component("adHocPaymentExcelView")
public class AdHocPaymentExcelView extends AbstractExcelView implements ApplicationContextAware{
	
	private ApplicationContext act = null;
	
	@Override
	protected void buildExcelDocument(Map model, HSSFWorkbook workbook,	HttpServletRequest request, HttpServletResponse response)
			throws Exception {
		String userId=(String) request.getSession().getAttribute("userId");
		List<String> ICLCRESTRICTED_USER_LIST=(List<String>) request.getSession().getAttribute("ICLCRESTRICTED_USER_LIST");
		
		act = getApplicationContext();
		ServiceRequestDao dao = (ServiceRequestDao)act.getBean("serviceRequestDao");
		HashMap<String,CenterStudentPortalBean> getICLCMap = dao.getICLCMap();
		HashMap<String,StudentStudentPortalBean> mapOfSapIdAndStudent= dao.getAllStudents();
		List<AdhocPaymentStudentPortalBean> listOfAdhocPaymentsMade = (List<AdhocPaymentStudentPortalBean>)model.get("listOfAdhocPaymentsMade");
		
		HSSFSheet sheet = workbook.createSheet("AD HOC Payments");
		int index = 0;
		HSSFRow header = sheet.createRow(0);
		header.createCell(index++).setCellValue("Sr. No.");
		header.createCell(index++).setCellValue("SAP ID");
		header.createCell(index++).setCellValue("Student Name");
		//header.createCell(index++).setCellValue("EmailID");
		//header.createCell(index++).setCellValue("Mobile Number");
		header.createCell(index++).setCellValue("Program");
		
		if(!ICLCRESTRICTED_USER_LIST.contains(userId)) {
		header.createCell(index++).setCellValue("IC Name");
		header.createCell(index++).setCellValue("LC Name");
		}
		header.createCell(index++).setCellValue("Description");
		header.createCell(index++).setCellValue("Transaction Status");
		header.createCell(index++).setCellValue("Amount");
		header.createCell(index++).setCellValue("Merchant Reference Number");
		header.createCell(index++).setCellValue("Transaction Time");
		int rowNum = 1;
		for(int i = 0;i<listOfAdhocPaymentsMade.size();i++){
			AdhocPaymentStudentPortalBean adHocBean = listOfAdhocPaymentsMade.get(i);
			index = 0;
			StudentStudentPortalBean student = mapOfSapIdAndStudent.get(adHocBean.getSapId());
			HSSFRow row = sheet.createRow(rowNum++);
			String learningCenter = "";
			if(student ==null)
			{
				row.createCell(index++).setCellValue(i+1);
				row.createCell(index++).setCellValue(adHocBean.getSapId());
				row.createCell(index++).setCellValue("");
				//row.createCell(index++).setCellValue("");
				//row.createCell(index++).setCellValue("");
				row.createCell(index++).setCellValue("");
				if(!ICLCRESTRICTED_USER_LIST.contains(userId)) {
				row.createCell(index++).setCellValue("");
				row.createCell(index++).setCellValue(learningCenter);
				}
				row.createCell(index++).setCellValue(adHocBean.getDescription());
				row.createCell(index++).setCellValue(adHocBean.getTranStatus());
				row.createCell(index++).setCellValue(adHocBean.getRespAmount());
				row.createCell(index++).setCellValue(adHocBean.getMerchantRefNo());
				row.createCell(index++).setCellValue(adHocBean.getRespTranDateTime());
				continue;
			}
			if(student != null && student.getCenterCode() != null && !"".equalsIgnoreCase(student.getCenterCode())){
				CenterStudentPortalBean center = getICLCMap.get(student.getCenterCode());
				if(center != null && center.getLc() != null) {
					learningCenter = center.getLc();
				}
			}
			
			row.createCell(index++).setCellValue(i+1);
			row.createCell(index++).setCellValue(adHocBean.getSapId());
			row.createCell(index++).setCellValue(student.getFirstName());
			//row.createCell(index++).setCellValue(student.getEmailId());
			//row.createCell(index++).setCellValue(student.getMobile());
			row.createCell(index++).setCellValue(student.getProgram());
			if(!ICLCRESTRICTED_USER_LIST.contains(userId)) {
			row.createCell(index++).setCellValue(student.getCenterName());
			row.createCell(index++).setCellValue(learningCenter);
			}
			row.createCell(index++).setCellValue(adHocBean.getDescription());
			row.createCell(index++).setCellValue(adHocBean.getTranStatus());
			row.createCell(index++).setCellValue(adHocBean.getRespAmount());
			row.createCell(index++).setCellValue(adHocBean.getMerchantRefNo());
			row.createCell(index++).setCellValue(adHocBean.getRespTranDateTime());
		}
	}

}