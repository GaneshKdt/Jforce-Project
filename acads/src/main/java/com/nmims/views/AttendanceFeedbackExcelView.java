package com.nmims.views;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.util.CellRangeAddress;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.web.servlet.view.document.AbstractExcelView;

import com.nmims.beans.CenterAcadsBean;
import com.nmims.beans.SessionAttendanceFeedbackAcads;
import com.nmims.beans.StudentAcadsBean;
import com.nmims.daos.PCPBookingDAO;

public class AttendanceFeedbackExcelView extends AbstractExcelView implements ApplicationContextAware{
	private static ApplicationContext act = null;
	
	@Override
	protected void buildExcelDocument(Map model, HSSFWorkbook workbook,	HttpServletRequest request, HttpServletResponse response){
		String roles = (String)request.getAttribute("roles");
		HashMap<String,SessionAttendanceFeedbackAcads> mapOfSessionIdAndFeedBackBean = (HashMap<String,SessionAttendanceFeedbackAcads>) request.getAttribute("mapOfSessionIdAndFeedBackBean");
		
		HashMap<String,SessionAttendanceFeedbackAcads> mapOfSubjectFacultySessionWiseAverage = (HashMap<String,SessionAttendanceFeedbackAcads>) request.getAttribute("mapOfSubjectFacultySessionWiseAverage");
		
		HashMap<String, SessionAttendanceFeedbackAcads> mapOfSessionWiseAttendaceCount =(HashMap<String,SessionAttendanceFeedbackAcads>) request.getAttribute("mapOfSessionWiseAttendaceCount");
		/*HashMap<String,SessionAttendanceFeedback> mapOfSessionWiseAverage = (HashMap<String,SessionAttendanceFeedback>) request.getAttribute("mapOfSessionWiseAverage");
		*/
		String userId=(String) request.getSession().getAttribute("userId_acads");
		List<String> ICLCRESTRICTED_USER_LIST=(List<String>) request.getSession().getAttribute("ICLCRESTRICTED_USER_LIST");
		
		act = getApplicationContext();
		PCPBookingDAO dao = (PCPBookingDAO)act.getBean("pcpBookingDAO");
		HashMap<String, StudentAcadsBean> sapIdStudentsMap = dao.getAllStudents();
		HashMap<String, CenterAcadsBean> icLcMap = dao.getICLCMap();
		List<SessionAttendanceFeedbackAcads> attendanceListAllRecords = (List<SessionAttendanceFeedbackAcads>) model.get("attendanceList");
		//create a wordsheet
		
		int sheetNum = 0;
		
		int rowsPerSheet = 65000;

 		int index = 0;
 		int sheetCount=1;
		while(sheetNum * rowsPerSheet < attendanceListAllRecords.size()) {
			int startIndex = sheetNum * rowsPerSheet;
			int endIndex = attendanceListAllRecords.size();

			// check if the size is greater than the max size of the array
			int maxEndOfSheet = startIndex + 65000;
			if(maxEndOfSheet < attendanceListAllRecords.size()) {
				endIndex = maxEndOfSheet - 1;
			}
			sheetNum++;
			
			List<SessionAttendanceFeedbackAcads> attendanceList = attendanceListAllRecords.subList(startIndex, endIndex);
			HSSFSheet sheet = workbook.createSheet("Attendance & Feedback (" + startIndex+ " - " + endIndex + ")");
			HSSFRow header = sheet.createRow(0);
			header.createCell(index++).setCellValue("Sr. No.");
			header.createCell(index++).setCellValue("Year");
			header.createCell(index++).setCellValue("Month");
			header.createCell(index++).setCellValue("First Name");
			header.createCell(index++).setCellValue("Last Name");
			header.createCell(index++).setCellValue("SAPID");
			header.createCell(index++).setCellValue("Program");
			header.createCell(index++).setCellValue("Semester");
			header.createCell(index++).setCellValue("Subject");
			header.createCell(index++).setCellValue("Session Name");
			header.createCell(index++).setCellValue("Session Date");
			header.createCell(index++).setCellValue("Faculty Name");
			header.createCell(index++).setCellValue("Attended");
			header.createCell(index++).setCellValue("Attend Time");
			header.createCell(index++).setCellValue("Re-Attend Time");
			header.createCell(index++).setCellValue("Meeting Key");
			if(roles.indexOf("Acads Admin") != -1 || roles.indexOf("Learning Center") != -1 || roles.indexOf("Student Support") != -1 || roles.indexOf("Diageo Corporate Center") != -1)
			{
				header.createCell(index++).setCellValue("Faculty ID");
				
				header.createCell(index++).setCellValue("Feedback Given");
				header.createCell(index++).setCellValue("Feedback Time");
				header.createCell(index++).setCellValue("Q.1 Response (The subject matter covered in this session helped you to understand and learn effectively)");
				header.createCell(index++).setCellValue("Q.1 Remark");
				header.createCell(index++).setCellValue("Q.2 Response (The course material used was helpful towards today's session)");
				header.createCell(index++).setCellValue("Q.2 Remark");
				header.createCell(index++).setCellValue("Q.3 Response (Audio quality was upto the mark.)");
				header.createCell(index++).setCellValue("Q.3 Remark");
				header.createCell(index++).setCellValue("Q.4 Response (Video quality was upto the mark.)");
				header.createCell(index++).setCellValue("Q.4 Remark");
				header.createCell(index++).setCellValue("Q.5 Response (The Faculty was organized and well prepared for the class)");
				header.createCell(index++).setCellValue("Q.5 Remark");
				header.createCell(index++).setCellValue("Q.6 Response (The Faculty was effective in communicating the concept in the class (in terms of clarity and presenting the concepts in understandable manner).");
				header.createCell(index++).setCellValue("Q.6 Remark");
				header.createCell(index++).setCellValue("Q.7 Response (The Faculty was responsive to student's learning difficulties and dealt with questions appropriately.)");
				header.createCell(index++).setCellValue("Q.7 Remark");
				header.createCell(index++).setCellValue("Q.8 Response (The learning process adopted (e.g. case studies, relevant examples and presentation work etc.) were helpful towards learning from the session.)");
				header.createCell(index++).setCellValue("Q.8 Remark");
				
				header.createCell(index++).setCellValue("Student Confirmation For Attendance ");
				header.createCell(index++).setCellValue("Reason for Not Attending");
				header.createCell(index++).setCellValue("Other Reason for Not Attending");
				
				header.createCell(index++).setCellValue("Session Id");
				header.createCell(index++).setCellValue("Avg Q.1 Response");
				header.createCell(index++).setCellValue("Avg Q.2 Response");
				header.createCell(index++).setCellValue("Avg Q.3 Response");
				header.createCell(index++).setCellValue("Avg Q.4 Response");
				header.createCell(index++).setCellValue("Avg Q.5 Response");
				header.createCell(index++).setCellValue("Avg Q.6 Response");
				header.createCell(index++).setCellValue("Avg Q.7 Response");
				header.createCell(index++).setCellValue("Avg Q.8 Response");
				
				header.createCell(index++).setCellValue("Feedback Remarks");
				header.createCell(index++).setCellValue("Track");
				header.createCell(index++).setCellValue("Consumer Type");
				header.createCell(index++).setCellValue("Device");
			}
			if(!ICLCRESTRICTED_USER_LIST.contains(userId)) {
			header.createCell(index++).setCellValue("IC");
			header.createCell(index++).setCellValue("LC");
			}
			header.createCell(index++).setCellValue("Exam Mode");
				
	 
			int rowNum = 1;
			for (int i = 0 ; i < attendanceList.size(); i++) {
				index = 0;
				//create the row data
				HSSFRow row = sheet.createRow(rowNum++);
				SessionAttendanceFeedbackAcads bean = attendanceList.get(i);
				
				StudentAcadsBean student = sapIdStudentsMap.get(bean.getSapId());
				
				String ic = "";
				String lc = "";
				String examMode = "Offline";
				if(student != null){
					ic = student.getCenterName();
					CenterAcadsBean center = icLcMap.get(student.getCenterCode()); 
					if(center != null){
						lc = center.getLc();
					}
					
					if("Online".equalsIgnoreCase(student.getPrgmStructApplicable())){
						examMode = "Online";
					}
				}
				
				try {
				row.createCell(index++).setCellValue((i+1));
				row.createCell(index++).setCellValue(new Double(bean.getYear()));
				row.createCell(index++).setCellValue(bean.getMonth());
				row.createCell(index++).setCellValue(bean.getFirstName());
				row.createCell(index++).setCellValue(bean.getLastName());
				row.createCell(index++).setCellValue(bean.getSapId());
				row.createCell(index++).setCellValue(bean.getProgram());
				row.createCell(index++).setCellValue(bean.getSem());
				row.createCell(index++).setCellValue(bean.getSubject());
				row.createCell(index++).setCellValue(bean.getSessionName());
				row.createCell(index++).setCellValue(bean.getDate());
				row.createCell(index++).setCellValue(bean.getFacultyFirstName() + " " + bean.getFacultyLastName());
				row.createCell(index++).setCellValue(bean.getAttended());
				row.createCell(index++).setCellValue(bean.getAttendTime());
				row.createCell(index++).setCellValue(bean.getReAttendTime());
				row.createCell(index++).setCellValue(bean.getMeetingKey());
				
				if(roles.indexOf("Acads Admin") != -1 || roles.indexOf("Learning Center") != -1 || roles.indexOf("Student Support") != -1 || roles.indexOf("Diageo Corporate Center") != -1)
				{
					row.createCell(index++).setCellValue(bean.getFacultyId());
					
					row.createCell(index++).setCellValue(bean.getFeedbackGiven());
					row.createCell(index++).setCellValue(bean.getFeedbackTime());
					row.createCell(index++).setCellValue(bean.getQ1Response());
					row.createCell(index++).setCellValue(bean.getQ1Remark());
					row.createCell(index++).setCellValue(bean.getQ2Response());
					row.createCell(index++).setCellValue(bean.getQ2Remark());
					row.createCell(index++).setCellValue(bean.getQ3Response());
					row.createCell(index++).setCellValue(bean.getQ3Remark());
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
					
					row.createCell(index++).setCellValue(bean.getStudentConfirmationForAttendance());
					row.createCell(index++).setCellValue(bean.getReasonForNotAttending());
					row.createCell(index++).setCellValue(bean.getOtherReasonForNotAttending());
					
					row.createCell(index++).setCellValue(bean.getSessionId());
					row.createCell(index++).setCellValue(!StringUtils.isBlank(mapOfSessionIdAndFeedBackBean.get(bean.getSessionId()).getQ1Average()) ? new BigDecimal(mapOfSessionIdAndFeedBackBean.get(bean.getSessionId()).getQ1Average()).doubleValue():new BigDecimal("0").doubleValue());
					row.createCell(index++).setCellValue(!StringUtils.isBlank(mapOfSessionIdAndFeedBackBean.get(bean.getSessionId()).getQ2Average()) ? new BigDecimal(mapOfSessionIdAndFeedBackBean.get(bean.getSessionId()).getQ2Average()).doubleValue():new BigDecimal("0").doubleValue());
					row.createCell(index++).setCellValue(!StringUtils.isBlank(mapOfSessionIdAndFeedBackBean.get(bean.getSessionId()).getQ3Average()) ? new BigDecimal(mapOfSessionIdAndFeedBackBean.get(bean.getSessionId()).getQ3Average()).doubleValue():new BigDecimal("0").doubleValue());
					row.createCell(index++).setCellValue(!StringUtils.isBlank(mapOfSessionIdAndFeedBackBean.get(bean.getSessionId()).getQ4Average()) ? new BigDecimal(mapOfSessionIdAndFeedBackBean.get(bean.getSessionId()).getQ4Average()).doubleValue():new BigDecimal("0").doubleValue());
					row.createCell(index++).setCellValue(!StringUtils.isBlank(mapOfSessionIdAndFeedBackBean.get(bean.getSessionId()).getQ5Average()) ? new BigDecimal(mapOfSessionIdAndFeedBackBean.get(bean.getSessionId()).getQ5Average()).doubleValue():new BigDecimal("0").doubleValue());
					row.createCell(index++).setCellValue(!StringUtils.isBlank(mapOfSessionIdAndFeedBackBean.get(bean.getSessionId()).getQ6Average()) ? new BigDecimal(mapOfSessionIdAndFeedBackBean.get(bean.getSessionId()).getQ6Average()).doubleValue():new BigDecimal("0").doubleValue());
					row.createCell(index++).setCellValue(!StringUtils.isBlank(mapOfSessionIdAndFeedBackBean.get(bean.getSessionId()).getQ7Average()) ? new BigDecimal(mapOfSessionIdAndFeedBackBean.get(bean.getSessionId()).getQ7Average()).doubleValue():new BigDecimal("0").doubleValue());
					row.createCell(index++).setCellValue(!StringUtils.isBlank(mapOfSessionIdAndFeedBackBean.get(bean.getSessionId()).getQ8Average()) ? new BigDecimal(mapOfSessionIdAndFeedBackBean.get(bean.getSessionId()).getQ8Average()).doubleValue():new BigDecimal("0").doubleValue());
					
					row.createCell(index++).setCellValue(bean.getFeedbackRemarks());
					row.createCell(index++).setCellValue(bean.getTrack());
					row.createCell(index++).setCellValue(bean.getConsumerType());
					row.createCell(index++).setCellValue(bean.getDevice());
				}
				if(!ICLCRESTRICTED_USER_LIST.contains(userId)) {
				row.createCell(index++).setCellValue(ic);
				row.createCell(index++).setCellValue(lc);
				}
				row.createCell(index++).setCellValue(student.getExamMode());
				}catch (Exception e) {
					// TODO: handle exception
				}
	        }
//			sheet.autoSizeColumn(0); sheet.autoSizeColumn(1);
//			sheet.autoSizeColumn(2); sheet.autoSizeColumn(3);
//			sheet.autoSizeColumn(4); sheet.autoSizeColumn(5);
//			sheet.autoSizeColumn(6); sheet.autoSizeColumn(7);
//			sheet.autoSizeColumn(8); sheet.autoSizeColumn(9);
//			sheet.autoSizeColumn(10); sheet.autoSizeColumn(11);
//			sheet.autoSizeColumn(12); sheet.autoSizeColumn(13);
//			sheet.autoSizeColumn(14); sheet.autoSizeColumn(15);
//			sheet.autoSizeColumn(16);sheet.autoSizeColumn(17);
//			sheet.autoSizeColumn(18);sheet.autoSizeColumn(19);
			
			sheet.setAutoFilter(CellRangeAddress.valueOf("A1:Q"+(attendanceList.size()+1)));
			sheetCount++;
		}
		
		
		if(roles.indexOf("Acads Admin") != -1 || roles.indexOf("Student Support") != -1) {
			//create a wordsheet for Subject Faculty Session Average
			HSSFSheet sheet = workbook.createSheet("Subject-Faculty-Session");
			index = 0;
			HSSFRow header = sheet.createRow(0);
			header.createCell(index++).setCellValue("Sr. No.");
			header.createCell(index++).setCellValue(" Subject ");
			header.createCell(index++).setCellValue(" Faculty ");
			header.createCell(index++).setCellValue(" Session Name");
			header.createCell(index++).setCellValue(" Session Date");
			header.createCell(index++).setCellValue(" Total Number of Response");
			header.createCell(index++).setCellValue("Avg Q.1 Response");
			header.createCell(index++).setCellValue("Avg Q.2 Response");
			header.createCell(index++).setCellValue("Avg Q.3 Response");
			header.createCell(index++).setCellValue("Avg Q.4 Response");
			header.createCell(index++).setCellValue("Avg Q.5 Response");
			header.createCell(index++).setCellValue("Avg Q.6 Response");
			header.createCell(index++).setCellValue("Avg Q.7 Response");
			header.createCell(index++).setCellValue("Avg Q.8 Response");
			header.createCell(index++).setCellValue(" Grand Session Average ");
			header.createCell(index++).setCellValue(" Grand Faculty Average ");
			header.createCell(index++).setCellValue(" Corporate Name ");
			header.createCell(index++).setCellValue(" Enrolled Students Count");
			header.createCell(index++).setCellValue(" Attended Students Count");
			header.createCell(index++).setCellValue(" Attendance Percentage ");
			int rowNum = 1;
			int i = 1;
			for(String key : mapOfSubjectFacultySessionWiseAverage.keySet()) {
				index = 0;
				
				//create the row data
				HSSFRow row = sheet.createRow(rowNum++);
				
				SessionAttendanceFeedbackAcads bean = mapOfSubjectFacultySessionWiseAverage.get(key);
				SessionAttendanceFeedbackAcads bean1 = mapOfSessionWiseAttendaceCount.get(bean.getSessionId());
				
				try {
				row.createCell(index++).setCellValue((i++));
				row.createCell(index++).setCellValue(bean.getSubject());
				row.createCell(index++).setCellValue(bean.getFacultyFirstName()+" "+bean.getFacultyLastName());
				row.createCell(index++).setCellValue(bean.getSessionName());
				row.createCell(index++).setCellValue(bean.getDate());
				row.createCell(index++).setCellValue(bean.getTotalResponse() != null ? new BigDecimal(bean.getTotalResponse()).doubleValue() : 0);
				row.createCell(index++).setCellValue(bean.getQ1Average() != null ? new BigDecimal(bean.getQ1Average()).doubleValue() : 0);
				row.createCell(index++).setCellValue(bean.getQ2Average() != null ? new BigDecimal(bean.getQ2Average()).doubleValue() : 0);
				row.createCell(index++).setCellValue(bean.getQ3Average() != null ? new BigDecimal(bean.getQ3Average()).doubleValue() : 0);
				row.createCell(index++).setCellValue(bean.getQ4Average() != null ? new BigDecimal(bean.getQ4Average()).doubleValue() : 0);
				row.createCell(index++).setCellValue(bean.getQ5Average() != null ? new BigDecimal(bean.getQ5Average()).doubleValue() : 0);
				row.createCell(index++).setCellValue(bean.getQ6Average() != null ? new BigDecimal(bean.getQ6Average()).doubleValue() : 0);
				row.createCell(index++).setCellValue(bean.getQ7Average() != null ? new BigDecimal(bean.getQ7Average()).doubleValue() : 0);
				row.createCell(index++).setCellValue(bean.getQ8Average() != null ? new BigDecimal(bean.getQ8Average()).doubleValue() : 0);
				row.createCell(index++).setCellValue(bean.getGrandSessionAverage() != null ? new BigDecimal(bean.getGrandSessionAverage()).doubleValue() : 0);
				row.createCell(index++).setCellValue(bean.getGrandSessionAverage() != null ? new BigDecimal(bean.getGrandFacultyAverage()).doubleValue() : 0);
				row.createCell(index++).setCellValue(bean.getCorporateName());
				row.createCell(index++).setCellValue(bean1.getApplicableStudentsForSession());
				row.createCell(index++).setCellValue(bean1.getAttendedStudentForSession());
				int percentage;
				try {
				percentage=(bean1.getAttendedStudentForSession()*100)/bean1.getApplicableStudentsForSession();
				}catch (ArithmeticException e) {
					// TODO: handle exception
					percentage=0;
				}
				row.createCell(index++).setCellValue(percentage+"%");
				}catch (Exception e) {
				// TODO: handle exception
				}
			}
		}
	}
}
