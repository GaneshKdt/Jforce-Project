package com.nmims.interfaces;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletResponse;

import com.nmims.beans.IAReportsBean;
import com.nmims.beans.SectionBean;
import com.nmims.dto.IAVerifyParametersReportDto;

public interface IAReportsServiceInterface {
	Map<String, String> testFacultyIdNameMap();
	Map<Integer, String> testConsumerTypeMap();
	Map<Integer, String> testProgramMap();
	Map<Integer, HashMap<Integer, String>> programStructureByProgramIdMap(Integer[] programs);
	List<String> testNameList();
	List<String> testSubjectList();
	Map<Integer, String> testQuestionTypeMap();
	List<SectionBean> testSectionQuestionsConfigList(long testId);
	List<SectionBean> testSectionQuestionsUploadList(long testId);
	int liveTestCountByReferenceId(long referenceId);
	
	void downloadIaExcelReportServletResponse(HttpServletResponse response, ArrayList<IAReportsBean> iaReportDataList) throws IOException;
	List<IAReportsBean> iaVerifyParametersReportData(IAVerifyParametersReportDto iaVerifyParametersReportDto, HashMap<String, String> facultyMap, HashMap<Integer, String> consumerTypeMap, 
														HashMap<Integer, String> programMap, HashMap<Integer, HashMap<Integer, String>> programStructureByProgramMap);
}
