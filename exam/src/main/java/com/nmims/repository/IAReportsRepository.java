package com.nmims.repository;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.nmims.beans.IAReportsBean;
import com.nmims.beans.SectionBean;

public interface IAReportsRepository {
	Map<String, String> getActiveFacultyForTest();
	Map<Integer, String> getActiveConsumerTypesForTest();
	Map<Integer, String> getActiveProgramsForTest();
	Map<Integer, HashMap<Integer, String>> getProgramStructureByProgramMap(Integer[] programs);
	List<String> getTestNameList();
	List<String> getSubjectsForTest();
	Map<Integer, String> getTestQuestionTypes();
	List<SectionBean> getSectionQuestionsConfigForTest(long testId);
	List<SectionBean> getSectionQuestionsUploadedForTest(long testId);
	
	String batchNameByReferenceId(long referenceId);
	String batchNameByModuleId(long referenceId);
	String moduleNameByReferenceId(long referenceId);
	Integer getLiveTestCountByReferenceId(long referenceId);
	
	List<IAReportsBean> getTestsData(IAReportsBean iaReportsBean);
}
