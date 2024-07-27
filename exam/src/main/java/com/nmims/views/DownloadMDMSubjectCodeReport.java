/**
 * 
 */
package com.nmims.views;

import java.util.ArrayList;
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

import com.nmims.beans.MDMSubjectCodeBean;

/**
 * @author vil_m
 *
 */
public class DownloadMDMSubjectCodeReport extends AbstractXlsxStreamingView implements ApplicationContextAware {

	private ApplicationContext act = null;
	
	public static final String N_ZERO = "0";
	public static final String N_ONE = "1";
	public static final String KEY_Y = "Y";
	public static final String KEY_N = "N";
	public static final String V_YES = "Yes";
	public static final String V_NO = "No";
	//public static final String V_TIMEBOUND = "TimeBound";
	//public static final String V_REGULAR = "Regular";
	
	public static final String COL_SRNO  = "Sr. No.";
	public static final String COL_SUBJECT_CODE  = "SubjectCode";
	public static final String COL_SUBJECT_NAME  = "Subject Name";
	public static final String COL_COMMON_SUBJECT = "Common Subject";
	//public static final String COL_SIFY_SUBJCODE  = "Sify Subject Code";
	public static final String COL_ACTIVE_STATUS  = "Active Status";
	public static final String COL_IS_PROJECT  = "Is Project";
	/*public static final String COL_PASS_SCORE  = "Program Pass Score";
	public static final String COL_HAS_ASSIGNMENT = "Assignment Applicable";
	public static final String COL_HASIA = "HasIA";
	public static final String COL_HAS_TEST = "Test Applicable";
	public static final String COL_ASSIGNMENT_NEEDED_WRITTEN = "Assignment Needed Before Written";
	public static final String COL_WRITTEN_SCORE = "Written Score Model";
	public static final String COL_ASSIGNMENT_SCORE = "Assignment Score Model";
	public static final String COL_CREATECASE_QUERY = "Create Case For Query?";
	public static final String COL_ASSIGNQUERY_FACULTY = "Assign Query To Faculty?";
	public static final String COL_GRACE_APPLICABLE = "Is Grace Applicable?";
	public static final String COL_MAX_GRACE = "Max Grace Marks";*/
	public static final String COL_SPECIALIZATION = "Specialization";
	public static final String COL_STUDENT_TYPE = "Student Type";
	public static final String COL_DESCRIPTION = "Description";
	public static final String COL_SESSION_TIME = "Session Time (Minute)";
	
	@Override
	protected void buildExcelDocument(Map model, Workbook workbook, HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		List<MDMSubjectCodeBean> listSubjCode = (ArrayList<MDMSubjectCodeBean>) model.get("mdmSubjectCodeList");

		Sheet sheet = workbook.createSheet("MDMSubjectCode Report");

		int index = 0;
		Row header = sheet.createRow(0);
		header.createCell(index++).setCellValue(COL_SRNO);
		header.createCell(index++).setCellValue(COL_SUBJECT_CODE);
		header.createCell(index++).setCellValue(COL_SUBJECT_NAME);
		header.createCell(index++).setCellValue(COL_COMMON_SUBJECT);
		header.createCell(index++).setCellValue(COL_ACTIVE_STATUS);
		header.createCell(index++).setCellValue(COL_IS_PROJECT);
		//header.createCell(index++).setCellValue(COL_SIFY_SUBJCODE);
		/*header.createCell(index++).setCellValue(COL_PASS_SCORE);
		header.createCell(index++).setCellValue(COL_HASIA);
		header.createCell(index++).setCellValue(COL_HAS_ASSIGNMENT);
		header.createCell(index++).setCellValue(COL_HAS_TEST);
		header.createCell(index++).setCellValue(COL_ASSIGNMENT_NEEDED_WRITTEN);
		header.createCell(index++).setCellValue(COL_WRITTEN_SCORE);
		header.createCell(index++).setCellValue(COL_ASSIGNMENT_SCORE);
		header.createCell(index++).setCellValue(COL_CREATECASE_QUERY);
		header.createCell(index++).setCellValue(COL_ASSIGNQUERY_FACULTY);
		header.createCell(index++).setCellValue(COL_GRACE_APPLICABLE);
		header.createCell(index++).setCellValue(COL_MAX_GRACE);*/
		header.createCell(index++).setCellValue(COL_SPECIALIZATION);
		header.createCell(index++).setCellValue(COL_STUDENT_TYPE);
		header.createCell(index++).setCellValue(COL_DESCRIPTION);
		header.createCell(index++).setCellValue(COL_SESSION_TIME);

		int rowNum = 1;
		for (int j = 0; j < listSubjCode.size(); j++) {
			index = 0;
			Row row = sheet.createRow(rowNum++);
			row.createCell(index++).setCellValue(j + 1);
			row.createCell(index++).setCellValue(listSubjCode.get(j).getSubjectcode());
			row.createCell(index++).setCellValue(listSubjCode.get(j).getSubjectname());
			row.createCell(index++).setCellValue(listSubjCode.get(j).getCommonSubject());
			row.createCell(index++).setCellValue(replaceWithYesNo(listSubjCode.get(j).getActive()));
			row.createCell(index++).setCellValue(replaceWithYesNo(listSubjCode.get(j).getIsProject()));
			//row.createCell(index++).setCellValue(listSubjCode.get(j).getSifySubjectCode());
			/*row.createCell(index++).setCellValue(listSubjCode.get(j).getPassScore());
			row.createCell(index++).setCellValue(listSubjCode.get(j).getHasIA());
			row.createCell(index++).setCellValue(listSubjCode.get(j).getHasAssignment());
			row.createCell(index++).setCellValue(listSubjCode.get(j).getHasTest());
			row.createCell(index++).setCellValue(listSubjCode.get(j).getAssignmentNeededBeforeWritten());
			row.createCell(index++).setCellValue(listSubjCode.get(j).getWrittenScoreModel());
			row.createCell(index++).setCellValue(listSubjCode.get(j).getAssignmentScoreModel());
			row.createCell(index++).setCellValue(listSubjCode.get(j).getCreateCaseForQuery());
			row.createCell(index++).setCellValue(listSubjCode.get(j).getAssignQueryToFaculty());
			row.createCell(index++).setCellValue(listSubjCode.get(j).getIsGraceApplicable());
			row.createCell(index++).setCellValue(listSubjCode.get(j).getMaxGraceMarks());*/
			row.createCell(index++).setCellValue(listSubjCode.get(j).getSpecializationType());
			//row.createCell(index++).setCellValue(replaceStudentType(listSubjCode.get(j).getStudentType()));
			row.createCell(index++).setCellValue(listSubjCode.get(j).getStudentType());
			row.createCell(index++).setCellValue(listSubjCode.get(j).getDescription());
			row.createCell(index++).setCellValue(listSubjCode.get(j).getSessionTime());
		}
	}
	
	public static String replaceWithYesNo(String arg) {
		if(arg.equals(N_ONE) || arg.equals(KEY_Y)) {
			return V_YES;
		} else if(arg.equals(N_ZERO) || arg.equals(KEY_N)) {
			return V_NO;
		} else {
			return "";
		}
	}
	
	/*public static String replaceStudentType(String arg) {
		if (null != arg) {
			if (arg.equals(KEY_Y)) {
				return V_TIMEBOUND;
			} else if (arg.equals(KEY_N)) {
				return V_REGULAR;
			} else {
				return "";
			}
		} else {
			return "";
		}
	}*/
}