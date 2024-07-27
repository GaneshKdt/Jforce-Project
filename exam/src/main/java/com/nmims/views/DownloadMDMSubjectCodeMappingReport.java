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
import org.springframework.context.ApplicationContextAware;
import org.springframework.web.servlet.view.document.AbstractXlsxStreamingView;

import com.nmims.beans.MDMSubjectCodeMappingBean;

/**
 * @author vil_m
 *
 */
public class DownloadMDMSubjectCodeMappingReport extends AbstractXlsxStreamingView implements ApplicationContextAware {

	//private ApplicationContext act = null;
	
	public static final String KEY_Y = "Y";
	public static final String KEY_N = "N";
	public static final String KEY_NA = "NA";
	public static final String V_YES = "Yes";
	public static final String V_NO = "No";
	public static final String V_NOT_APP = "Not Applicable";
	
	public static final String COL_SRNO  = "Sr. No.";
	public static final String COL_SUBJECT_NAME  = "SubjectName";
	public static final String COL_CONSUMER_TYPE = "Consumer Type";
	public static final String COL_PRGM_STRUCTURE  = "Program Structure";
	public static final String COL_PRGM_NAME = "Program Name";
	public static final String COL_SEM  = "Semester";
	public static final String COL_SUBJECT_CODE  = "SubjectCode";
	public static final String COL_ACTIVE_STATUS  = "Active Status";
	public static final String COL_SIFY_SUBJCODE  = "Sify Subject Code";
	public static final String COL_PASS_SCORE  = "Program Pass Score";
	public static final String COL_HASIA = "HasIA";
	public static final String COL_HAS_TEST = "Test Applicable";
	public static final String COL_HAS_ASSIGNMENT = "Assignment Applicable";
	public static final String COL_ASSIGNMENT_NEEDED_WRITTEN = "Assignment Needed Before Written";
	public static final String COL_ASSIGNMENT_SCORE = "Assignment Score Model";
	public static final String COL_WRITTEN_SCORE = "Written Score Model";
	public static final String COL_CREATECASE_QUERY = "Create Case For Query?";
	public static final String COL_ASSIGNQUERY_FACULTY = "Assign Query To Faculty?";
	public static final String COL_GRACE_APPLICABLE = "Is Grace Applicable?";
	public static final String COL_MAX_GRACE = "Max Grace Marks";
	public static final String COL_SUBJECT_CREDITS = "Subject Credits";
	public static final String COL_PRGM_FULL_NAME = "Program Full Name";
	
	@Override
	protected void buildExcelDocument(Map model, Workbook workbook, HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		List<MDMSubjectCodeMappingBean> listSubjCodeMapp = (ArrayList<MDMSubjectCodeMappingBean>) model.get("mdmSubjectCodeMappingList");
		
		Sheet sheet = workbook.createSheet("MDMSubjectCodeMapping Report");

		int index = 0;
		Row header = sheet.createRow(0);
		header.createCell(index++).setCellValue(COL_SRNO);
		header.createCell(index++).setCellValue(COL_CONSUMER_TYPE);
		header.createCell(index++).setCellValue(COL_PRGM_STRUCTURE);
		header.createCell(index++).setCellValue(COL_PRGM_NAME);
		header.createCell(index++).setCellValue(COL_PRGM_FULL_NAME);
		header.createCell(index++).setCellValue(COL_SEM);
		header.createCell(index++).setCellValue(COL_SUBJECT_NAME);
		header.createCell(index++).setCellValue(COL_SUBJECT_CODE);
		header.createCell(index++).setCellValue(COL_SUBJECT_CREDITS);
		header.createCell(index++).setCellValue(COL_ACTIVE_STATUS);
		header.createCell(index++).setCellValue(COL_SIFY_SUBJCODE);
		header.createCell(index++).setCellValue(COL_PASS_SCORE);
		header.createCell(index++).setCellValue(COL_HASIA);
		header.createCell(index++).setCellValue(COL_HAS_TEST);
		header.createCell(index++).setCellValue(COL_HAS_ASSIGNMENT);
		header.createCell(index++).setCellValue(COL_ASSIGNMENT_NEEDED_WRITTEN);
		header.createCell(index++).setCellValue(COL_ASSIGNMENT_SCORE);
		header.createCell(index++).setCellValue(COL_WRITTEN_SCORE);
		header.createCell(index++).setCellValue(COL_CREATECASE_QUERY);
		header.createCell(index++).setCellValue(COL_ASSIGNQUERY_FACULTY);
		header.createCell(index++).setCellValue(COL_GRACE_APPLICABLE);
		header.createCell(index++).setCellValue(COL_MAX_GRACE);
		
		int rowNum = 1;
		for (int j = 0; j < listSubjCodeMapp.size(); j++) {
			index = 0;
			Row row = sheet.createRow(rowNum++);
			row.createCell(index++).setCellValue(listSubjCodeMapp.get(j).getId());
			row.createCell(index++).setCellValue(listSubjCodeMapp.get(j).getConsumerType());
			row.createCell(index++).setCellValue(listSubjCodeMapp.get(j).getPrgmStructApplicable());
			row.createCell(index++).setCellValue(listSubjCodeMapp.get(j).getProgram());
			row.createCell(index++).setCellValue(listSubjCodeMapp.get(j).getProgramFullName());
			row.createCell(index++).setCellValue(listSubjCodeMapp.get(j).getSem());
			row.createCell(index++).setCellValue(listSubjCodeMapp.get(j).getSubjectName());
			row.createCell(index++).setCellValue(listSubjCodeMapp.get(j).getSubjectCode());
			row.createCell(index++).setCellValue(listSubjCodeMapp.get(j).getSubjectCredits());
			row.createCell(index++).setCellValue(replaceWithYesNo(listSubjCodeMapp.get(j).getActive()));
			row.createCell(index++).setCellValue(listSubjCodeMapp.get(j).getSifySubjectCode());
			row.createCell(index++).setCellValue(listSubjCodeMapp.get(j).getPassScore());
			row.createCell(index++).setCellValue(replaceWithYesNo(listSubjCodeMapp.get(j).getHasIA()));
			row.createCell(index++).setCellValue(replaceWithYesNo(listSubjCodeMapp.get(j).getHasTest()));
			row.createCell(index++).setCellValue(replaceWithYesNo(listSubjCodeMapp.get(j).getHasAssignment()));
			row.createCell(index++).setCellValue(replaceWithYesNo(listSubjCodeMapp.get(j).getAssignmentNeededBeforeWritten()));
			row.createCell(index++).setCellValue(listSubjCodeMapp.get(j).getAssignmentScoreModel());
			row.createCell(index++).setCellValue(listSubjCodeMapp.get(j).getWrittenScoreModel());
			row.createCell(index++).setCellValue(replaceWithYesNo(listSubjCodeMapp.get(j).getCreateCaseForQuery()));
			row.createCell(index++).setCellValue(replaceWithYesNo(listSubjCodeMapp.get(j).getAssignQueryToFaculty()));
			row.createCell(index++).setCellValue(replaceWithYesNo(listSubjCodeMapp.get(j).getIsGraceApplicable()));
			row.createCell(index++).setCellValue(listSubjCodeMapp.get(j).getMaxGraceMarks());
		}
	}
	
	public static String replaceWithYesNo(String arg) {
		if(arg.equals(KEY_Y)) {
			return V_YES;
		} else if(arg.equals(KEY_N)) {
			return V_NO;
		} else if(arg.equals(KEY_NA)) {
			return V_NOT_APP;
		} else {
			return "";
		}
	}
}
