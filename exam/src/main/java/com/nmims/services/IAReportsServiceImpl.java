package com.nmims.services;

import java.io.BufferedOutputStream;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.nmims.beans.IAReportsBean;
import com.nmims.beans.SectionBean;
import com.nmims.dto.IAVerifyParametersReportDto;
import com.nmims.helpers.IAReportsExcelViewHelper;
import com.nmims.interfaces.IAReportsServiceInterface;
import com.nmims.repository.IAReportsRepositoryImpl;

@Service
public class IAReportsServiceImpl implements IAReportsServiceInterface {
	private IAReportsRepositoryImpl iaReportsRepositoryImpl;
	private static final Logger logger = LoggerFactory.getLogger(IAReportsServiceImpl.class);
	
	@Autowired
	public IAReportsServiceImpl(IAReportsRepositoryImpl iaReportsRepositoryImpl) {
		this.iaReportsRepositoryImpl = iaReportsRepositoryImpl;
	}
	
	/**
	 * FullName (firstName + lastName) of the Faculty Retrieved via the Test Faculty Ids and stored in a HashMap.
	 */
	@Override
	public HashMap<String, String> testFacultyIdNameMap() {
		HashMap<String, String> facultyIdNameMap = new HashMap<>();
		try {
			facultyIdNameMap = iaReportsRepositoryImpl.getActiveFacultyForTest();
			logger.info(facultyIdNameMap.size() + " records stored of Faculty Name via Test Faculty Ids.");
		}
		catch(EmptyResultDataAccessException ex) {
			logger.error("No records found while retrieving Faculty Name via Test Faculty Ids, Exception thrown: " + ex.toString());
		}
		catch(Exception ex) {
			logger.error("Error while retrieving Faculty Name via Test Faculty Ids, Exception thrown: " + ex.toString());
		}
		
		return facultyIdNameMap;
	}
	
	/**
	 * HashMap of ConsumerType Id & Name of corresponding Test records stored.
	 */
	@Override
	public HashMap<Integer, String> testConsumerTypeMap() {
		HashMap<Integer, String> consumerTypeIdNameMap = new HashMap<>();
		try {
			consumerTypeIdNameMap = iaReportsRepositoryImpl.getActiveConsumerTypesForTest();
			logger.info(consumerTypeIdNameMap.size() + " records stored of ConsumerType Id & Name corresponding to Test records.");
		}
		catch(EmptyResultDataAccessException ex) {
			logger.error("No records found while retrieving ConsumerType Id & Name corresponding to Test records, Exception thrown: " + ex.toString());
		}
		catch(Exception ex) {
			logger.error("Error while retrieving ConsumerType Id & Name corresponding to Test records, Exception thrown: " + ex.toString());
		}
		
		return consumerTypeIdNameMap;
	}
	
	/**
	 * HashMap of Program Id & Code of corresponding Test records stored.
	 */
	@Override
	public HashMap<Integer, String> testProgramMap() {
		HashMap<Integer, String> programIdCodeMap = new HashMap<>(); 
		try {
			programIdCodeMap = iaReportsRepositoryImpl.getActiveProgramsForTest();
			logger.info(programIdCodeMap.size() + " records stored of Program Id & Code corresponding to Test records.");
		}
		catch(EmptyResultDataAccessException ex) {
			logger.error("No records found while retrieving Program Id & Code corresponding to Test records, Exception thrown: " + ex.toString());
		}
		catch(Exception ex) {
			logger.error("Error while retrieving Program Id & Code corresponding to Test records, Exception thrown: " + ex.toString());
		}
		
		return programIdCodeMap;
	}

	/**
	 * HashMap of ProgramStructure Id & Name of ProgramIds passed as the method parameter.
	 */
	@Override
	public HashMap<Integer, HashMap<Integer, String>> programStructureByProgramIdMap(Integer[] programs) {
		HashMap<Integer, HashMap<Integer, String>> programStructureByprogramIdMap = new HashMap<>();
		try {
			programStructureByprogramIdMap = iaReportsRepositoryImpl.getProgramStructureByProgramMap(programs);
			logger.info(programStructureByprogramIdMap.size() + " records stored of ProgramStructure Id & Name of ProgramIds passed as the method parameter.");
		}
		catch(EmptyResultDataAccessException ex) {
			logger.error("No records found while retrieving ProgramStructure Id & Name of a passed ProgramId, Exception thrown: " + ex.toString());
		}
		catch(Exception ex) {
			logger.error("Error while retrieving ProgramStructure Id & Name of ProgramIds passed, Exception thrown: " + ex.toString());
		}
		
		return programStructureByprogramIdMap;
	}
	
	/**
	 * ArrayList of Test Names stored in Test table.
	 */
	@Override
	public ArrayList<String> testNameList() {
		ArrayList<String> testNameList = new ArrayList<>();
		try {
			testNameList = iaReportsRepositoryImpl.getTestNameList();
			logger.info(testNameList.size() + " records stored of Test Names of tests present in Test table.");
		}
		catch(EmptyResultDataAccessException ex) {
			logger.error("No records found while retrieving Test Names of tests present in Test table, Exception thrown: " + ex.toString());
		}
		catch(Exception ex) {
			logger.error("Error while retrieving Test Names of tests present in Test table, Exception thrown: " + ex.toString());
		}
		
		return testNameList;
	}
	
	/**
	 * ArrayList of unique Subject Names by Test CPS Ids
	 */
	@Override
	public ArrayList<String> testSubjectList() {
		ArrayList<String> testSubjectList = new ArrayList<>();
		try {	
			testSubjectList = iaReportsRepositoryImpl.getSubjectsForTest();
			logger.info(testSubjectList.size() + " records stored of Subject Names corresponding to Test records.");
			
			//Creating a TreeSet of elements stored in the above ArrayList to eliminate duplicate Subject Names and store them in an ascending order 
			Set<String> removeDuplicateSubjects = new TreeSet<>(testSubjectList);
			
			testSubjectList.clear();		//removes all elements present in the ArrayList
			testSubjectList.addAll(removeDuplicateSubjects);	//re-populating the list with subject names
			logger.info(testSubjectList.size() + " records stored of Unique Subject Names corresponding to Test records.");
		}
		catch(EmptyResultDataAccessException ex) {
			logger.error("No records found while retrieving Subject Names corresponding to Test records, Exception thrown: " + ex.toString());
		}
		catch(Exception ex) {
			logger.error("Error while retrieving Subject Names corresponding to Test records, Exception thrown: " + ex.toString());
		}
		
		return testSubjectList;
	}
	
	/**
	 * HashMap of Test Question Types and their corresponding Ids.
	 */
	@Override
	public HashMap<Integer, String> testQuestionTypeMap() {
		HashMap<Integer, String> testQuestionTypeMap = new HashMap<>();
		try {	
			testQuestionTypeMap = iaReportsRepositoryImpl.getTestQuestionTypes();
			logger.info(testQuestionTypeMap.size() + " records stored of Test Question Types.");
		}
		catch(EmptyResultDataAccessException ex) {
			logger.error("No records found while retrieving Test Question Types, Exception thrown: " + ex.toString());
		}
		catch(Exception ex) {
			logger.error("Error while retrieving Test Question Types, Exception thrown: " + ex.toString());
		}
		
		return testQuestionTypeMap;
	}
	
	/**
	 * ArrayList of Section-wise Questions configurations by testId
	 */
	@Override
	public ArrayList<SectionBean> testSectionQuestionsConfigList(long testId) {
		ArrayList<SectionBean> testSectionQuestionsConfigList = new ArrayList<>();
		try {
			testSectionQuestionsConfigList = iaReportsRepositoryImpl.getSectionQuestionsConfigForTest(testId);
		}
		catch(EmptyResultDataAccessException ex) {
			logger.error("No records found while retrieving Section-wise Questions configurations by testId, Exception thrown: " + ex.toString());
		}
		catch(Exception ex) {
			logger.error("Error while retrieving Section-wise Questions configurations by testId, Exception thrown: " + ex.toString());
		}
		
		return testSectionQuestionsConfigList;
	}
	
	/**
	 * ArrayList of Section-wise Questions uploaded by testId
	 */
	@Override
	public ArrayList<SectionBean> testSectionQuestionsUploadList(long testId) {
		ArrayList<SectionBean> testSectionQuestionsUploadedList = new ArrayList<>();
		try {
			testSectionQuestionsUploadedList = iaReportsRepositoryImpl.getSectionQuestionsUploadedForTest(testId);
		}
		catch(EmptyResultDataAccessException ex) {
			logger.error("No records found while retrieving Section-wise Questions uploaded by testId, Exception thrown: " + ex.toString());
		}
		catch(Exception ex) {
			logger.error("Error while retrieving Section-wise Questions uploaded by testId, Exception thrown: " + ex.toString());
		}
		
		return testSectionQuestionsUploadedList;
	}
	
	/**
	 * Count of live Test by Reference Id
	 */
	@Override
	public int liveTestCountByReferenceId(long referenceId) {
		int testCount = 0;
		try {
			testCount = iaReportsRepositoryImpl.getLiveTestCountByReferenceId(referenceId);
		}
		catch(Exception ex) {
			logger.error("Error while retrieving live Test count by Reference Id, Exception thrown: " + ex.toString());
		}
		
		return testCount;
	}

	/**
	 * returns the test data as filtered by the user, then populates each test with additional information.
	 */
	@Override
	public ArrayList<IAReportsBean> iaVerifyParametersReportData(IAVerifyParametersReportDto iaVerifyParametersReportDto, HashMap<String, String> facultyMap, HashMap<Integer, String> consumerTypeMap, 
																	HashMap<Integer, String> programMap, HashMap<Integer, HashMap<Integer, String>> programStructureByProgramMap) {
		
		//Mapping IAVerifyParametersReportDto fields to IAReportsBean fields via Jackson ObjectMapper
		IAReportsBean iaReportsBean = new ObjectMapper().configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false).convertValue(iaVerifyParametersReportDto, IAReportsBean.class);
		
		ArrayList<IAReportsBean> filteredTestsDataList = iaReportsRepositoryImpl.getTestsData(iaReportsBean);
		logger.info(filteredTestsDataList.size() + " records stored as List of Test data as filtered by the user!");
		HashMap<Integer, String> questionTypeMap = testQuestionTypeMap();			//Storing the Test Question Type & Id Map to use while storing section-wise questions data
		ArrayList<IAReportsBean> populatedTestsData = new ArrayList<>();			//Creating a new ArrayList to store updated Test data 
		
		for (IAReportsBean testDataBean : filteredTestsDataList) {
			testDataBean.setFacultyName(facultyMap.get(testDataBean.getFacultyId()));			//Get Faculty Name via Faculty Id

			if(testDataBean.getConsumerTypeIdFormValue() != null && !testDataBean.getConsumerTypeIdFormValue().isEmpty())
				testDataBean.setConsumerType(consumerTypeMap.get(Integer.valueOf(testDataBean.getConsumerTypeIdFormValue())));			//if ConsumerTypeId is not null or empty, get ConsumerTypeName By Id

			if(testDataBean.getProgramIdFormValue() != null && !testDataBean.getProgramIdFormValue().isEmpty()) {
				if (testDataBean.getProgramIdFormValue().split(",").length > 1)
					testDataBean.setProgram("All");				//if ProgramId is not null or empty and more than one ProgramId exists for a test, insert All as Program Name
				else
					testDataBean.setProgram(programMap.get(Integer.valueOf(testDataBean.getProgramIdFormValue())));				//if ProgramId is not null or empty, get ProgramCode By Id
			}

			if(testDataBean.getProgramStructureIdFormValue() != null  && !testDataBean.getProgramStructureIdFormValue().isEmpty()) {
				if (testDataBean.getProgramStructureIdFormValue().split(",").length > 1)
					testDataBean.setProgramStructure("All");			//if ProgramStructureId is not null or empty and more than one ProgramStructureId exists for a test, insert All as ProgramStructure Name
				else if(testDataBean.getProgramIdFormValue() != null && !testDataBean.getProgramIdFormValue().isEmpty())
					testDataBean.setProgramStructure(programStructureByProgramMap
								.get(Integer.valueOf(testDataBean.getProgramIdFormValue()))
								.get(Integer.valueOf(testDataBean.getProgramStructureIdFormValue())));		//if ProgramStructureId is not null or empty and more than one ProgramStructureId exists for a test, insert All as ProgramStructure Name
			}
			
			setModuleOrBatchNameByReferenceId(testDataBean);			//Set BatchName/ModuleName of Test
			int windowTime = getWindowTime(testDataBean.getStartDate(), testDataBean.getEndDate());			//Time Difference between Test StartTime and EndTime
			testDataBean.setWindowTime(windowTime);
			testDataBean.setTestInitials(getTestNameInitials(testDataBean.getTestName()));					//Set TestName Initials By testName
			
			String isTestLive = liveTestCountByReferenceId(testDataBean.getReferenceId()) > 0 ? "Y" : "N";				//if count of live test is greater than 0, set flag as 'Y', else 'N'
			testDataBean.setTestLive(isTestLive);
			
			ArrayList<SectionBean> questionsConfigList = testSectionQuestionsConfigList(testDataBean.getId());			//storing questions configuration data as a list
			String questionsConfiguredForTest = getQuestionsDataForTest(questionsConfigList, questionTypeMap);			//section-wise questions configuration stored as a String
			testDataBean.setQuestionsConfigured(questionsConfiguredForTest);
			
			ArrayList<SectionBean> questionsUploadedList = testSectionQuestionsUploadList(testDataBean.getId());		//storing questions uploaded data as a list
			String questionsUploadedForTest = getQuestionsDataForTest(questionsUploadedList, questionTypeMap);			//section-wise questions uploaded stored as a String
			testDataBean.setQuestionsUploaded(questionsUploadedForTest);
			
			populatedTestsData.add(testDataBean);
		}
		
		return populatedTestsData;
	}
	
	/**
	 * Creating a Buffered OutputStream which writes the created InputStream data (using Apache POI) in the ServletOutputStream of HttpServletResponse
	 */
	@Override
	public void downloadIaExcelReportServletResponse(HttpServletResponse response, ArrayList<IAReportsBean> iaReportDataList) throws IOException {
		ByteArrayInputStream inStream = IAReportsExcelViewHelper.testDataWorksheet(iaReportDataList);
		BufferedOutputStream outStream = new BufferedOutputStream(response.getOutputStream());
		
		response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");			// Content-Type for Excel 2007 or higher .xlsx files
        response.setHeader(HttpHeaders.CONTENT_DISPOSITION, "attachment;filename=IA-verifyParametersTestReport.xlsx");		// Content-Disposition to indicate the content is an attachment
        response.setContentLength(inStream.available());				// Content-Length indicates the size of the entity body in bytes
        
		byte[] buffer = new byte[1024];			//Creating a byte array of size 1024
        int bytesRead = 0;
        //iterating through the inputStream, reading 1024 bytes at a time and storing the read bytes into the buffer array 
        while ((bytesRead = inStream.read(buffer)) != -1) {
            outStream.write(buffer, 0, bytesRead);			//writing the bytes stored in the buffer array onto the outputStream
        }
        
        outStream.close();
        inStream.close();
	}
	
	
	/**
	 * A Method which concatenates the key and value of a HashMap, separating the two with a hyphen (-),
	 * these values are stored in an ArrayList of String.
	 * @param HashMap containing the key (String) and value (String) pairs
	 * @return ArrayList of String
	 */
	public ArrayList<String> convertHashMapToHyphenSeparatedList(HashMap<String, String> map) {
		ArrayList<String> list = new ArrayList<>();
		
		// using for-each loop for iteration over Map.entrySet()
        for(Map.Entry<String,String> entry : map.entrySet()) {
        	list.add(entry.getValue() + " - " + entry.getKey());
		}
		
        Collections.sort(list);		//sorting the list in ascending order
        logger.info(list.size() + " records concatinated and sorted in ascending order.");
        
		return list;
	}
	
	/**
	 * If the applicable type of test is 'batch', retrieve and set BatchName for test,
	 * if the applicable type of test is 'module', retrieve and set BatchName & ModuleName for test
	 * @param IAReportsBean containing the test data
	 */
	private void setModuleOrBatchNameByReferenceId(IAReportsBean testDataBean) {
		if ("batch".equalsIgnoreCase(testDataBean.getApplicableType())) {
			try {
				testDataBean.setBatchName(iaReportsRepositoryImpl.batchNameByReferenceId(testDataBean.getReferenceId()));
			}
			catch(EmptyResultDataAccessException ex) {
				logger.error("No records found while retrieving BatchName by ReferenceId (BatchId), Exception thrown: " + ex.toString());
			}
			catch(Exception ex) {
				logger.error("Error while retrieving BatchName by ReferenceId (BatchId), Exception thrown: " + ex.toString());
			}
		}
		else if ("module".equalsIgnoreCase(testDataBean.getApplicableType())) {
			try {
				testDataBean.setModuleName(iaReportsRepositoryImpl.moduleNameByReferenceId(testDataBean.getReferenceId()));
				testDataBean.setBatchName(iaReportsRepositoryImpl.batchNameByModuleId(testDataBean.getReferenceId()));
			}
			catch(EmptyResultDataAccessException ex) {
				logger.error("No records found while retrieving ModuleName/BatchName by ReferenceId (ModuleId), Exception thrown: " + ex.toString());
			}
			catch(Exception ex) {
				logger.error("Error while retrieving ModuleName/BatchName by ReferenceId (ModuleId), Exception thrown: " + ex.toString());
			}
		}
	}
	
	/**
	 * Method to calculate the time difference between the test startDateTime and endDateTime
	 * @param startDate - startDateTime of the test
	 * @param endDate - endDateTime of the test
	 * @return time difference as an integer
	 */
	private int getWindowTime(String startDate, String endDate) {
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
		
		//Parsing StartDate and EndDate of test as LocalDateTime Objects
		LocalDateTime startDateTime = LocalDateTime.parse(startDate, formatter);
		LocalDateTime endDateTime = LocalDateTime.parse(endDate, formatter);
		long minutes = ChronoUnit.MINUTES.between(startDateTime, endDateTime);	//Calculates the time difference
		
		return (int) minutes;
	}
	
	/**
	 * Get Name Initials of a test
	 * @param testName - name of the test
	 * @return abbreviated initials of a testName
	 */
	private String getTestNameInitials(String testName) {
		try {
			String[] splitTestNameArray = testName.split("-");		
			String initialStringName = splitTestNameArray[0].trim();		//first String contains the abbreviated testName
			
			//if conditions contain the edge cases
			if("CP".equals(initialStringName) || "MAML".equals(initialStringName) || "SPDM".equals(initialStringName)) 
				return splitTestNameArray[0] + "-" + splitTestNameArray[1] + "-" + splitTestNameArray[2];
			else if(initialStringName.startsWith("IA") && Character.isDigit(initialStringName.charAt(initialStringName.length() - 1))) 
				return splitTestNameArray[0];
			else if(initialStringName.startsWith("Internal")) 
				return splitTestNameArray[0];
			else 
				return splitTestNameArray[0] + "-" + splitTestNameArray[1];		//normal scenario wherein the testName initials is in the first two String values
		}
		catch(ArrayIndexOutOfBoundsException ex) {
			logger.info("Array Out of Bounds for testName: " + testName + ", Exception thrown: " + ex.toString());
		}
		catch(Exception ex) {
			logger.info("Error encountered while retrieveing the testName initils for testName: " + testName + ", Exception thrown: " + ex.toString());
		}
		
		//testName is returned as the testName initial, incase of exit from try block
		return testName;		
	}
	
	/**
	 * Convert List of Section Questions data into a String, specifying the question type | quest count | question marks within a specific Section.
	 * @param testSectionDataList - List of Section Questions data
	 * @param testQuestionTypeMap - test Question Type & Id Map
	 * @return Section-wise Questions data as a String
	 */
	private String getQuestionsDataForTest(ArrayList<SectionBean> testSectionDataList, HashMap<Integer, String> testQuestionTypeMap) {
		String questionsData = "";

		if (testSectionDataList.size() > 0) {
			//Creating SringBuilders for each section
			StringBuilder section1questionsDataSb = new StringBuilder();
			StringBuilder section2questionsDataSb = new StringBuilder();
			StringBuilder sectionNquestionsDataSb = new StringBuilder();
			
			//Flag to denote if the Section heading has been initialized (added)
			boolean section1Init = false, section2Init = false, sectionNInit = false;
			
			for (SectionBean sectionData : testSectionDataList) {
				String sectionName = sectionData.getSectionName();
				if (sectionName == null || sectionName.equalsIgnoreCase("Section1"))	//To keep Section Name consistent
					sectionName = "Section 1";
				else if (sectionName.equalsIgnoreCase("Section2"))
					sectionName = "Section 2";

				//Creating the section-wise question type | quest count | question marks appended String
				if (sectionName.equalsIgnoreCase("Section 1")) {
					if(!section1Init) {
						section1Init = true;
						section1questionsDataSb.append(sectionName +":\r\n");
					}
					section1questionsDataSb.append(testQuestionTypeMap.get(Integer.valueOf(sectionData.getType())) + " | ");
					section1questionsDataSb.append("count: " + sectionData.getSectionQnCount() + " | ");
					section1questionsDataSb.append("marks: " + sectionData.getQuestionMarks() + ", \r\n");
				} 
				else if (sectionName.equalsIgnoreCase("Section 2")) {
					if(!section2Init) {
						section2Init = true;
						section2questionsDataSb.append(sectionName +":\r\n");
					}
					section2questionsDataSb.append(testQuestionTypeMap.get(Integer.valueOf(sectionData.getType())) + " | ");
					section2questionsDataSb.append("count: " + sectionData.getSectionQnCount() + " | ");
					section2questionsDataSb.append("marks: " + sectionData.getQuestionMarks() + ", \r\n");
				}
				else {
					if(!sectionNInit) {
						sectionNInit = true;
						sectionNquestionsDataSb.append(sectionName +":\r\n");
					}
					sectionNquestionsDataSb.append(testQuestionTypeMap.get(Integer.valueOf(sectionData.getType())) + " | ");
					sectionNquestionsDataSb.append("count: " + sectionData.getSectionQnCount() + " | ");
					sectionNquestionsDataSb.append("marks: " + sectionData.getQuestionMarks() + ", \r\n");
				}
			}

			questionsData = section1questionsDataSb.append(section2questionsDataSb).append(sectionNquestionsDataSb).toString();		//appending the sections 1 by 1
			questionsData = questionsData.trim();		//to remove the trailing \r\n
			if (questionsData.endsWith(","))			//to remove the training comma at the end of the String
				questionsData = questionsData.substring(0, questionsData.length() - 1);
		}
		
		return questionsData;
	}
}
