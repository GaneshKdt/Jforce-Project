package com.nmims.repository;

import java.util.ArrayList;
import java.util.HashMap;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.nmims.beans.IAReportsBean;
import com.nmims.beans.SectionBean;
import com.nmims.daos.TestDAO;

@Repository
public class IAReportsRepositoryImpl implements IAReportsRepository {
	private TestDAO testDao;

	@Autowired
	public IAReportsRepositoryImpl(TestDAO testDao) {
		this.testDao = testDao;
	}

	@Override
	public HashMap<String, String> getActiveFacultyForTest() {
		return testDao.getTestFacultyIdFullNameMap();
	}

	@Override
	public HashMap<Integer, String> getActiveConsumerTypesForTest() {
		return testDao.getTestConsumerTypeIdNameMap();
	}

	@Override
	public HashMap<Integer, String> getActiveProgramsForTest() {
		return testDao.getTestProgramIdCodeMap();
	}

	@Override
	public HashMap<Integer, HashMap<Integer, String>> getProgramStructureByProgramMap(Integer[] programs) {
		HashMap<Integer, HashMap<Integer, String>> programStructureByProgramMap = new HashMap<>();
		for (int program : programs) {
			//Storing a Nested Map of ProgramStructureId & Name with programId as the Key 
			programStructureByProgramMap.put(program, testDao.getProgramStructureMapByProgram(program));
		}
		return programStructureByProgramMap;
	}
	
	@Override
	public ArrayList<String> getTestNameList() {
		return testDao.getTestNameList();
	}

	@Override
	public ArrayList<String> getSubjectsForTest() {
		//List of Unique CPS Ids of Tests
		ArrayList<Integer> testCpsIdList = testDao.getTestCpsIdList();

		//returning subjects of CPS Ids retrieved above
		return testDao.getSubjectsByCpsIdList(testCpsIdList);
	}

	@Override
	public HashMap<Integer, String> getTestQuestionTypes() {
		return testDao.getTestQuestionTypeMap();
	}

	@Override
	public ArrayList<SectionBean> getSectionQuestionsConfigForTest(long testId) {
		return testDao.getTestSectionQuestionsConfigList(testId);
	}
	
	@Override
	public ArrayList<SectionBean> getSectionQuestionsUploadedForTest(long testId) {
		return testDao.getTestSectionQuestionsUploadedList(testId);
	}
	
	@Override
	public String batchNameByReferenceId(long referenceId) {
		return testDao.getBatchNameById(referenceId);
	}
	
	@Override
	public String batchNameByModuleId(long referenceId) {
		long timeboundIdByModuleId = testDao.getTimeboundIdByModuleId(referenceId);		//Get Timebound Id By Module
		String batchName = testDao.getBatchNameByTimeboundId(timeboundIdByModuleId);	//Using timebound Id to get BatchName
		return batchName;
	}
	
	@Override
	public String moduleNameByReferenceId(long referenceId) {
		return testDao.getModuleTopicById(referenceId);
	}
	
	@Override
	public Integer getLiveTestCountByReferenceId(long referenceId) {
		return testDao.getLiveTestByModuleId(referenceId);
	}
	
	@Override
	public ArrayList<IAReportsBean> getTestsData(IAReportsBean iaReportsBean) {
		return testDao.getFilteredTestsData(iaReportsBean);
	}
}
