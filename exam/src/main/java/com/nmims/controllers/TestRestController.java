package com.nmims.controllers;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.RandomStringUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;

import com.nmims.beans.ConsumerProgramStructureExam;
import com.nmims.beans.FacultyExamBean;
import com.nmims.beans.ResponseDataBean;
import com.nmims.beans.StudentExamBean;
import com.nmims.beans.StudentQuestionResponseExamBean;
import com.nmims.beans.StudentsTestDetailsExamBean;
import com.nmims.beans.TestExamBean;
import com.nmims.beans.TestQuestionExamBean;
import com.nmims.beans.TestQuestionConfigBean;
import com.nmims.beans.TestQuestionOptionExamBean;
import com.nmims.beans.TestTypeBean;
import com.nmims.daos.AuditTrailsDAO;
import com.nmims.daos.TestDAO;
import com.nmims.helpers.AmazonS3Helper;
import com.nmims.helpers.IATestHelper;
import com.nmims.services.IATestService;

@RestController
@RequestMapping("m")
public class TestRestController extends BaseController {
	
	private List<String> programList;
	private List<String> subjectList;
	private List<FacultyExamBean> facultyList;
	private HashMap<String,TestTypeBean> testTypesMap;

	private String TEST_QUESTION_IMAGE_BASE_PATH= "C:/TEST_QUESTION_IMAGES/";

	//private String TEST_QUESTION_ASSIGNMENT_BASE_PATH= "D:/TEST_QUESTION_ASSIGNMENT/"; for prod
	private String TEST_QUESTION_ASSIGNMENT_BASE_PATH= "C:/TEST_QUESTION_ASSIGNMENT/"; //for local
	@Value( "${SERVER_PATH}" )
	private String SERVER_PATH;

	@Value("${CURRENT_ACAD_MONTH}")
	private String CURRENT_ACAD_MONTH;

	@Value("${CURRENT_ACAD_YEAR}")
	private String CURRENT_ACAD_YEAR;

	@Value("#{'${ACAD_YEAR_LIST}'.split(',')}")
	private List<String> ACAD_YEAR_LIST;

	@Value("#{'${ACAD_MONTH_LIST}'.split(',')}") 
	private List<String> ACAD_MONTH_LIST; 
	
	@Value( "${CKEDITORS_UPLOADEDFILES_PATH}" )
	private String CKEDITORS_UPLOADEDFILES_PATH;
	
	@Value("${LEAD_CURRENT_ACAD_YEAR}")
	private Integer LEAD_CURRENT_ACAD_YEAR;
	
	@Value("${LEAD_CURRENT_ACAD_MONTH}")
	private String LEAD_CURRENT_ACAD_MONTH;
	
	@Value("${LEAD_CURRENT_EXAM_YEAR}")
	private String LEAD_CURRENT_EXAM_YEAR;
	
	@Value("${LEAD_CURRENT_EXAM_MONTH}")
	private String LEAD_CURRENT_EXAM_MONTH;
	
	@Value("${AWS_IA_FILE_BUCKET}")
	private String awsIAFileBucket;

    @Value("${ENVIRONMENT}")
	private String environment;
	
    @Autowired
    TestDAO tDao; 
    
    @Autowired
    AuditTrailsDAO auditDao;
    
    @Autowired
    private IATestService iaTestService;
    
    @Autowired
    private AmazonS3Helper amazonS3Helper;
    
	@Value("#{'${CURRENT_YEAR_LIST}'.split(',')}")
	private ArrayList<String> currentYearList; 


	private ArrayList<ConsumerProgramStructureExam> consumerTypeList;

	private Map<String,String> consumerTypeIdNameMap;

	private Map<String,String> programStructureIdNameMap;

	private Map<String,String> programIdNameMap;

	private final int pageSize = 20;
	
	private static final Logger logger = LoggerFactory.getLogger(TestRestController.class);
	
	/**
	 * Refresh Cache function to refresh cache
	 * @param 
	 * none
	 * @return 
	 * none
	 * */
	public String RefreshCache() {

		subjectList = null;
		getSubjectList();

		programList = null;
		getProgramList();

		typeIdBeanMap = null;
		getTypeIdNBeanMap();

		testTypesMap = null;
		getTestTypesMap();

		facultyList = null;
		getFacultyList();

		consumerTypeList = null;
		getConsumerTypeList();

		consumerTypeIdNameMap = null;
		getConsumerTypeIdNameMap();

		programStructureIdNameMap = null;
		getProgramStructureIdNameMap();

		programIdNameMap = null;
		getProgramIdNameMap();

		return null;
	}

	public List<String> getProgramList(){
		if(this.programList == null){
			TestDAO dao = (TestDAO)act.getBean("testDao");
			this.programList = dao.getAllPrograms();
		}
		return programList;
	}

	public List<String> getSubjectList(){
		if(this.subjectList == null){
			TestDAO dao = (TestDAO)act.getBean("testDao");
			this.subjectList = dao.getActiveSubjects();
		}
		return subjectList;
	}

	public List<FacultyExamBean> getFacultyList(){
		if(this.facultyList == null){
			TestDAO dao = (TestDAO)act.getBean("testDao");
			this.facultyList = dao.getFaculties();
		}
		return facultyList;
	}

	public ArrayList<ConsumerProgramStructureExam> getConsumerTypeList(){
		if(this.consumerTypeList == null){
			TestDAO dao = (TestDAO)act.getBean("testDao");
			this.consumerTypeList = dao.getConsumerTypeList();
		}
		return consumerTypeList;
	}
	public Map<String,String>  getConsumerTypeIdNameMap(){
		if(this.consumerTypeIdNameMap == null){
			TestDAO dao = (TestDAO)act.getBean("testDao");
			this.consumerTypeIdNameMap = dao.getConsumerTypeIdNameMap();
		}
		return consumerTypeIdNameMap;
	}
	public Map<String,String>  getProgramStructureIdNameMap(){
		if(this.programStructureIdNameMap == null){
			TestDAO dao = (TestDAO)act.getBean("testDao");
			this.programStructureIdNameMap = dao.getProgramStructureIdNameMap();
		}
		return programStructureIdNameMap;
	}
	public Map<String,String>  getProgramIdNameMap(){
		if(this.programIdNameMap == null){
			TestDAO dao = (TestDAO)act.getBean("testDao");
			this.programIdNameMap = dao.getProgramIdNameMap();
		}
		return programIdNameMap;
	}


	public HashMap<String,TestTypeBean> getTestTypesMap(){
		if(this.testTypesMap == null){
			TestDAO dao = (TestDAO)act.getBean("testDao");
			this.testTypesMap = dao.getTestTypesMap();
		}
		return testTypesMap;
	}

	public HashMap<Long,TestTypeBean> typeIdBeanMap = null;
	public HashMap<Long,TestTypeBean> getTypeIdNBeanMap(){
		if(this.typeIdBeanMap == null){
			TestDAO dao = (TestDAO)act.getBean("testDao");
			this.typeIdBeanMap = dao.getTypeIdNTypeMap();
		}
		return typeIdBeanMap;
	}
	
	
	
	//saveTestAnswersMarks start
	@PostMapping(path = "/saveTestAnswersMarks", consumes = "application/json", produces = "application/json")
	public ResponseEntity<HashMap<String,String>> m_saveTestAnswersMarks(@RequestBody StudentQuestionResponseExamBean answer){

		HttpHeaders headers = new HttpHeaders();
		headers.add("Content-Type", "application/json");
		TestDAO dao = (TestDAO)act.getBean("testDao");
		HashMap<String,String> response = new HashMap<>();

		List<StudentQuestionResponseExamBean> answers =  dao.getTestAnswerBySapidAndQuestionId(answer.getSapid(), answer.getQuestionId(), answer.getAttempt());
		if(answers.isEmpty()) {
			response.put("Status", "Fail");
			return new ResponseEntity<>(response,headers, HttpStatus.INTERNAL_SERVER_ERROR);

		}else {
			//Do update
			boolean updated = dao.updateQuestionMarks(answer);
			if(!updated) {
				response.put("Status", "Fail");
				return new ResponseEntity<>(response,headers, HttpStatus.INTERNAL_SERVER_ERROR);
			}
			else {
				response.put("Status", "Success");
				return new ResponseEntity<>(response,headers, HttpStatus.OK);
			}
		}
	}

	//end 
	
	//saveTestQuestionsConfig start
	@PostMapping(path = "/saveTestQuestionsConfig", consumes = "application/json", produces = "application/json")
	public ResponseEntity<HashMap<String,String>> m_saveTestQuestionsConfig(@RequestBody TestQuestionConfigBean config){

		HttpHeaders headers = new HttpHeaders();
		headers.add("Content-Type", "application/json");
		TestDAO dao = (TestDAO)act.getBean("testDao");
		HashMap<String,String> response = new HashMap<>();

		TestQuestionConfigBean tempConfig = dao.getTestConfigByTestIdNQuestionType(config.getTestId(), config.getType());
		if(tempConfig == null) {
			//Do insert
			long saved = dao.saveTestConfig(config);
			if(saved == 0) {
				response.put("Status", "Fail");
				return new ResponseEntity<>(response,headers, HttpStatus.INTERNAL_SERVER_ERROR);
			}
			else {
				response.put("Status", "Success");
				setTestQuestionsInRedisByTestId(config.getTestId());
				return new ResponseEntity<>(response,headers, HttpStatus.OK);
			}


		}else {
			//Do update
			boolean updated = dao.updateTestConfig(config);
			if(!updated) {
				response.put("Status", "Fail");
				return new ResponseEntity<>(response,headers, HttpStatus.INTERNAL_SERVER_ERROR);
			}
			else {
				response.put("Status", "Success");
				setTestQuestionsInRedisByTestId(config.getTestId());
				return new ResponseEntity<>(response,headers, HttpStatus.OK);
			}
		}
	}
	//end 
	
	private void setTestQuestionsInRedisByTestId(Long testId) {
		IATestHelper iATestHelper = (IATestHelper)act.getBean("iATestHelper");
		String response = iATestHelper.setTestQuestionsInRedisByTestId(testId);
	}
	
	//saveTestFacultyAnswersAllocation start
	@PostMapping(path = "/saveTestFacultyAnswersAllocation", consumes = "application/json", produces = "application/json")
	public ResponseEntity<HashMap<String,String>> saveTestFacultyAnswersAllocations(@RequestBody StudentQuestionResponseExamBean allocation){

		HttpHeaders headers = new HttpHeaders();
		headers.add("Content-Type", "application/json");
		TestDAO dao = (TestDAO)act.getBean("testDao");
		HashMap<String,String> response = new HashMap<>();

		boolean clearOldAllocations = dao.clearFacultyAllocation(allocation);
		if(!clearOldAllocations) {
			response.put("Status", "Fail");
			return new ResponseEntity<>(response,headers, HttpStatus.INTERNAL_SERVER_ERROR);
		}

		boolean updateAllocation = dao.updateFacultyAllocations(allocation);

		Integer noOfAllocationsLeft = dao.getNoOfAnswersNotAllocated(allocation.getTestId());
		response.put("noOfAllocationsLeft", noOfAllocationsLeft.toString());
		
		Integer noOfAllocated = dao.getNoOfAnsweresAllocatedToFaculty(allocation.getFacultyId(), allocation.getTestId());
		response.put("noOfAllocated", noOfAllocated.toString());
		if(!updateAllocation) {
			response.put("Status", "Fail");
			return new ResponseEntity<>(response,headers, HttpStatus.INTERNAL_SERVER_ERROR);
		}else {	
			response.put("Status", "Success");
			return new ResponseEntity<>(response,headers, HttpStatus.OK);

		}
	}
	//end
	
	//deleteTestFacultyAnswersAllocation start
	@PostMapping(path = "/deleteTestFacultyAnswersAllocation", consumes = "application/json", produces = "application/json")
	public ResponseEntity<HashMap<String,String>> deleteTestFacultyAnswersAllocation(@RequestBody StudentQuestionResponseExamBean allocation){

		HttpHeaders headers = new HttpHeaders();
		headers.add("Content-Type", "application/json");
		TestDAO dao = (TestDAO)act.getBean("testDao");
		HashMap<String,String> response = new HashMap<>();

		boolean clearOldAllocations = dao.clearFacultyAllocation(allocation);
		Integer noOfAllocationsLeft = dao.getNoOfAnswersNotAllocated(allocation.getTestId());
		response.put("noOfAllocationsLeft", noOfAllocationsLeft.toString());

		if(!clearOldAllocations) {
			response.put("Status", "Fail");
			return new ResponseEntity<>(response,headers, HttpStatus.INTERNAL_SERVER_ERROR);
		}
		else {	
			response.put("Status", "Success");
			return new ResponseEntity<>(response,headers, HttpStatus.OK);

		}
	}
	//end

	//saveSingleTestQusetion start
	@PostMapping(path = "/saveSingleTestQuestion", consumes = "application/json", produces = "application/json")
	public ResponseEntity<HashMap<String,String>> saveSingleTestQusetion(@RequestBody TestQuestionExamBean question){

		for(TestQuestionOptionExamBean o : question.getOptionsList()) {
		}
		HttpHeaders headers = new HttpHeaders();
		headers.add("Content-Type", "application/json");
		TestDAO dao = (TestDAO)act.getBean("testDao");
		HashMap<String,String> response = new HashMap<>();

		//Escape HTML characters Start
		question = escapeHTMLFromString(question);
		if(!StringUtils.isBlank(question.getErrorMessage())) {
			response.put("Status", "Failed");
			response.put("errroMessage", question.getErrorMessage());
			return new ResponseEntity<>(response,headers, HttpStatus.INTERNAL_SERVER_ERROR);
		}
		//END

		String errorMessage = dao.addSingleTestQuestion(question);
		try{
			Integer returnedKey = Integer.parseInt(errorMessage.trim());
			response.put("mainQuestionId", ""+returnedKey);
			response.put("Status", "Success");
			
			setTestQuestionsInRedisByTestId(question.getTestId());
			
			return new ResponseEntity<>(response,headers, HttpStatus.OK);	
		}catch(Exception e){
			//
			response.put("Status", "Failed");
			response.put("errroMessage", errorMessage);
			return new ResponseEntity<>(response,headers, HttpStatus.INTERNAL_SERVER_ERROR);

		}


	}
	//end
	
private TestQuestionExamBean escapeHTMLFromString(TestQuestionExamBean question) {
		
		try {
			//question.setQuestion(customEscapeHTML(question.getQuestion()));
			question.setDescription(customEscapeHTML(question.getDescription()));
			
			if(question.getOptionsList() !=null && !question.getOptionsList().isEmpty()) {
				for(TestQuestionOptionExamBean o : question.getOptionsList()) {
					o.setOptionData(customEscapeHTML(o.getOptionData()));
				}
			}
			
			if(question.getSubQuestionsList() !=null && !question.getSubQuestionsList().isEmpty()) {
				for(TestQuestionExamBean s : question.getSubQuestionsList()) {
					s = escapeHTMLFromString(s);
					if(!StringUtils.isBlank(s.getErrorMessage())) {
						question.setErrorMessage(question.getErrorMessage()+"  "+s.getErrorMessage());
					}
				}
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			
			question.setErrorMessage("Error in escaping HTML special characters : "+e.getMessage());
		}
		
		return question;
	}
	public String customEscapeHTML(String s) {
		if(s != null) {
			s = s.replaceAll("'", "&sbquo;");
			s = s.replaceAll("\"", "&quot;");
			s = s.replaceAll("<", "&lt;");
			s = s.replaceAll(">", "&gt;");
			//s = s.replaceAll("\\", "&bsol;");
			s = s.replaceAll("/", "&sol;");
		}
		return s;
	}
	
	
	//updateTestQusetion start
	@PostMapping(path = "/updateTestQusetion", consumes = "application/json", produces = "application/json")
	public ResponseEntity<HashMap<String,String>> updateTestQusetion(@RequestBody TestQuestionExamBean question){

		HttpHeaders headers = new HttpHeaders();
		headers.add("Content-Type", "application/json");
		TestDAO dao = (TestDAO)act.getBean("testDao");
		HashMap<String,String> response = new HashMap<>();

		String errorMessage = dao.updateTestQusetion(question);
		if(StringUtils.isBlank(errorMessage)){
			response.put("Status", "Success");
			TestQuestionExamBean questionForTestId = dao.getTestQuestionById(question.getId());
			setTestQuestionsInRedisByTestId(questionForTestId.getTestId());
			return new ResponseEntity<>(response,headers, HttpStatus.OK);	
		}else{
			response.put("Status", "Failed");
			response.put("errroMessage", errorMessage);
			return new ResponseEntity<>(response,headers, HttpStatus.INTERNAL_SERVER_ERROR);

		}

	}
	//end

	//deleteTestQusetion start
	@PostMapping(path = "/deleteTestQusetion", consumes = "application/json", produces = "application/json")
	public ResponseEntity<HashMap<String,String>> deleteTestQusetion(@RequestBody TestQuestionExamBean question){

		HttpHeaders headers = new HttpHeaders();
		headers.add("Content-Type", "application/json");
		TestDAO dao = (TestDAO)act.getBean("testDao");
		HashMap<String,String> response = new HashMap<>();

		TestQuestionExamBean questionForTestId = dao.getTestQuestionById(question.getId());
		
		String errorMessage = dao.deleteTestQuestionById(question.getId());

		if("Error".equalsIgnoreCase(errorMessage)) {
			response.put("Status", "Failed");
			response.put("errroMessage", "Error in deleting question.");
			return new ResponseEntity<>(response,headers, HttpStatus.INTERNAL_SERVER_ERROR);
		}
		else {
			response.put("Status", "Success");
			response.put("successMessage", errorMessage);
			setTestQuestionsInRedisByTestId(questionForTestId.getTestId());
			
			return new ResponseEntity<>(response,headers, HttpStatus.OK);	
		}


	}
	//end
	
	//FileBean
	//Upload image for question start
	@PostMapping(path = "/uploadTestQuestionImage", produces = "application/json")
	public ResponseEntity<HashMap<String,String>> uploadTestQuestionImage(MultipartHttpServletRequest request){

		HttpHeaders headers = new HttpHeaders();
		headers.add("Content-Type", "application/json");
		TestDAO dao = (TestDAO)act.getBean("testDao");

		HashMap<String,String> response = new HashMap<>();

		String errorMessage = "";
		String returnLink="";
		Iterator<String> it = request.getFileNames();
		while (it.hasNext()) {
			String uploadFile = it.next();
			MultipartFile file = request.getFile(uploadFile);
			returnLink= uploadTestQuestionImageToServer(file);
			//returnLink= uploadTestQuestionImageToAWS(file);//to be update with aws folder later, contact Pranit 25/1/22
		}
		//MultipartFile multipartFile = request.getFile("user-file");

		//String name = multipartFile.getOriginalFilename();

		//imageBean = uploadTestQuestionImageToServer(imageBean);
		//errorMessage = imageBean.getErrorMessage();  
		if(StringUtils.isBlank(returnLink)) {
			errorMessage ="Error";
		}
		
		if("Error".equalsIgnoreCase(errorMessage)) {
			response.put("Status", "Failed");
			response.put("errroMessage", "Error in saving file to DB.");
			return new ResponseEntity<>(response,headers, HttpStatus.INTERNAL_SERVER_ERROR);
		} else {
			response.put("Status", "Success");
			response.put("imageUrl", returnLink);
			response.put("successMessage", errorMessage);
			return new ResponseEntity<>(response,headers, HttpStatus.OK);	
		}


	}
	
	private String uploadTestQuestionImageToServer(MultipartFile imageBean) {

		String imageFileOriginalName = imageBean.getOriginalFilename();  
		File file = convertFile(imageBean);

		imageFileOriginalName = imageFileOriginalName.replaceAll("'", "_");
		imageFileOriginalName = imageFileOriginalName.replaceAll(",", "_");
		imageFileOriginalName = imageFileOriginalName.replaceAll("&", "and");
		imageFileOriginalName = imageFileOriginalName.replaceAll(" ", "_");
		imageFileOriginalName = imageFileOriginalName.replaceAll(":", "");


		InputStream inputStream = null;   
		OutputStream outputStream = null;
		String returnUrl = "";
		try {
			inputStream = new FileInputStream(file);   
			String imagePathToReturn ="assignment_qp_" + RandomStringUtils.randomAlphanumeric(12) +imageFileOriginalName;
			String filePath = TEST_QUESTION_IMAGE_BASE_PATH+imagePathToReturn;
			File folderPath = new File(TEST_QUESTION_IMAGE_BASE_PATH);
			if (!folderPath.exists()) {
				boolean created = folderPath.mkdirs();
			}else {

			}


			File newFile = new File(filePath);   
			outputStream = new FileOutputStream(newFile);   
			int read = 0;   
			byte[] bytes = new byte[1024];   

			while ((read = inputStream.read(bytes)) != -1) {   
				outputStream.write(bytes, 0, read);   
			}
			outputStream.close();
			inputStream.close();
			returnUrl=SERVER_PATH+"TestQuestionImages/"+imagePathToReturn;
		} catch (Exception e) {
			// TODO Auto-generated catch block
			
			returnUrl="";
		}

		return returnUrl;
	}
	public File convertFile(MultipartFile file)
	{    
		File convFile = new File(file.getOriginalFilename());
		try {
			convFile.createNewFile(); 
			FileOutputStream fos = new FileOutputStream(convFile); 
			fos.write(file.getBytes());
			fos.close();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			
		} 
		return convFile;
	}
	
	
	@PostMapping(path = "/checkIfAnyTestsActive")
	public ResponseEntity<Map<String, Object>> checkIfAnyTestsActive(HttpServletRequest request, @RequestBody StudentExamBean inputObj ){
		Map<String, Object> returnObject = new HashMap<String, Object>();
		HttpHeaders headers = new HttpHeaders();
		headers.add("Content-Type", "application/json");

		String sapid = inputObj.getSapid();

		boolean testsActive = false;
		try {

			TestDAO dao = (TestDAO)act.getBean("testDao");
			if(dao.checkIfAnyTestsActive(sapid)) {
				testsActive = true;
			}
		}catch (Exception e) {
			
			returnObject.put("error", e.getMessage());
		}
		returnObject.put("testsActive", testsActive);

		return new ResponseEntity<Map<String, Object>>(returnObject, headers, HttpStatus.OK);
	}
	
	@PostMapping(path = "/getAttemptsDetailsBySapidNTestId")
	public ResponseEntity<List<StudentsTestDetailsExamBean>> getAttemptsDetailsBySapidNTestId(@RequestBody TestExamBean test){
		HttpHeaders headers = new HttpHeaders();
		headers.add("Content-Type", "application/json");
		HashMap<String,String> response = new HashMap<>();
		TestDAO dao = (TestDAO)act.getBean("testDao");
		List<StudentsTestDetailsExamBean>studentTest = dao.getAttemptsDetailsBySapidNTestId(test.getSapid(), test.getTestId());
		return new ResponseEntity<List<StudentsTestDetailsExamBean>>(studentTest,headers, HttpStatus.OK);
	} 	
	
	//Upload assignment for question start
	@PostMapping(path = "/uploadTestAssignmentQuestionFile", produces = "application/json")
	public ResponseEntity<HashMap<String,String>> uploadTestAssignmentQuestionFile(MultipartHttpServletRequest request){

		HttpHeaders headers = new HttpHeaders();
		headers.add("Content-Type", "application/json");
		TestDAO dao = (TestDAO)act.getBean("testDao");

		HashMap<String,String> response = new HashMap<>();

		String errorMessage = "";
		String returnLink="";

		String testId = (String)request.getParameter("testId");
		Iterator<String> it = request.getFileNames();
		while (it.hasNext()) {
			String uploadFile = it.next();
			MultipartFile file = request.getFile(uploadFile);
			returnLink= uploadTestQuestionAssignmentToAWS(file,testId);
		}
		//MultipartFile multipartFile = request.getFile("user-file");

		//String name = multipartFile.getOriginalFilename();

		//imageBean = uploadTestQuestionImageToServer(imageBean);
		//errorMessage = imageBean.getErrorMessage();  

		if( errorMessage.indexOf("Error") != -1 ) {
			response.put("Status", "Failed");
			response.put("errroMessage", errorMessage);
			return new ResponseEntity<>(response,headers, HttpStatus.INTERNAL_SERVER_ERROR);
		}
		else {
			response.put("Status", "Success");
			response.put("imageUrl", returnLink);
			response.put("successMessage", errorMessage);
			return new ResponseEntity<>(response,headers, HttpStatus.OK);	
		}


	}
	
	
	private String uploadTestQuestionAssignmentToServer(MultipartFile imageBean,String testId) {

		String imageFileOriginalName = imageBean.getOriginalFilename();  
		//File file = convertFile(imageBean);
		MultipartFile file = imageBean;
		imageFileOriginalName = imageFileOriginalName.replaceAll("'", "_");
		imageFileOriginalName = imageFileOriginalName.replaceAll(",", "_");
		imageFileOriginalName = imageFileOriginalName.replaceAll("&", "and");
		imageFileOriginalName = imageFileOriginalName.replaceAll(" ", "_");
		imageFileOriginalName = imageFileOriginalName.replaceAll(":", "");


		InputStream inputStream = null;   
		OutputStream outputStream = null;
		String returnUrl = "";
		try {
			inputStream = file.getInputStream();   
			String imagePathToReturn ="assignment_qp_"+testId+"_"+ RandomStringUtils.randomAlphanumeric(12) +imageFileOriginalName;
			String filePath = TEST_QUESTION_ASSIGNMENT_BASE_PATH+imagePathToReturn;
			File folderPath = new File(TEST_QUESTION_ASSIGNMENT_BASE_PATH);
			if (!folderPath.exists()) {
				boolean created = folderPath.mkdirs();
			}else {

			}


			File newFile = new File(filePath);   
			outputStream = new FileOutputStream(newFile);   
			int read = 0;   
			byte[] bytes = new byte[1024];   

			while ((read = inputStream.read(bytes)) != -1) {   
				outputStream.write(bytes, 0, read);   
			}
			outputStream.close();
			inputStream.close();
			returnUrl=SERVER_PATH+"TestAssignmentQuestion/"+imagePathToReturn;
		} catch (Exception e) {
			// TODO Auto-generated catch block
			
			returnUrl="";
		}

		return returnUrl;
	}
	

	private String uploadTestQuestionAssignmentToAWS(MultipartFile fileBean, String testId) {

		String fileOriginalName = fileBean.getOriginalFilename();  

		fileOriginalName = fileOriginalName.replaceAll("'", "_");
		fileOriginalName = fileOriginalName.replaceAll(",", "_");
		fileOriginalName = fileOriginalName.replaceAll("&", "and");
		fileOriginalName = fileOriginalName.replaceAll(" ", "_");
		fileOriginalName = fileOriginalName.replaceAll(":", "");

		String folderPath = "qpfiles/";
		String fileName = folderPath + environment+ "_assignment_qp_"+testId+"_"+ RandomStringUtils.randomAlphanumeric(12) +fileOriginalName;
		String returnUrl = "";
		
		try {

			returnUrl = amazonS3Helper.uploadMiltipartFile( fileBean, folderPath, awsIAFileBucket, fileName);
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			returnUrl = "Error uploading file : "+e.getMessage();
			
		}

		return returnUrl;
	}
	
    
    @PostMapping(value = "/saveFacultyEditRights", consumes = "application/json", produces = "application/json")
    public ResponseEntity<HashMap<String,String>> saveFacultyEditRights( HttpServletRequest request, @RequestBody TestExamBean bean){  
            
            HttpHeaders headers = new HttpHeaders();
            headers.add("Content-Type", "application/json");
            HashMap<String,String> response = new HashMap<>();
            
            try {
                    tDao.saveFacultyEditRights(bean);
                    response.put("status", "success");
                    return new ResponseEntity<>(response, headers, HttpStatus.OK);
            } catch (Exception e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                    response.put("status", "error");
                    return new ResponseEntity<>(response, headers, HttpStatus.INTERNAL_SERVER_ERROR);
            }
            
    }
    
    /**
     * Apply Benefit of Doubt to a particular question of the test,
     * and re-run results for students who were assigned the particular question if test results live.
     * @param testId - ID of the test
     * @param questionId - ID of the test question
     * @param userId - ID of the user
     * @return Map containing boolean value indicating if BoD is applied
     */
    @PostMapping(value = "/admin/applyBenefitOfDoubt", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ResponseDataBean> applyBenefitOfDoubt(@RequestParam final Long testId, @RequestParam final Long questionId, @RequestParam final String userId) {
    	ResponseDataBean response = new ResponseDataBean();
    	try {
    		Map<String, Boolean> bodResponseMap = iaTestService.applyingBenefitOfBoubt(testId, questionId, userId);
    		
    		response.setSuccess(true);
			response.setCode(HttpStatus.OK.value());
			response.setMessage("Successfully applied BoD for questionId: " + questionId + " of testId: " + testId);
			response.setData(bodResponseMap);
			
			logger.info("Apply BoD response: {}", response);
			return new ResponseEntity<>(response, HttpStatus.OK);
    	}
    	catch(IllegalArgumentException ex) {
    		response.setSuccess(false);
			response.setCode(HttpStatus.BAD_REQUEST.value());
			response.setMessage(ex.getMessage());
			response.setData(Collections.singletonMap("benefitOfDoubt", false));
			
			logger.error("Unable to apply BoD for testId: {}, Error message: {}", testId, ex.getMessage());
			return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    	}
    	catch(Exception ex) {
    		response.setSuccess(false);
			response.setCode(HttpStatus.INTERNAL_SERVER_ERROR.value());
			response.setMessage("Failed to apply BoD for questionId: " + questionId + " of testId: " + testId);
			response.setData(Collections.singletonMap("benefitOfDoubt", false));
			
			logger.error("Error while applying Benefit of Doubt for testId: {} and questionId: {}, Exception thrown: ", testId, questionId, ex);
			return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
    	}
    }
    
    /**
     * Remove Benefit of Doubt of a particular test question,
     * and re-run results for students who were assigned the particular question if test results live.
     * @param testId - ID of the test
     * @param questionId - ID of the test question
     * @param userId - ID of the user
     * @return Map containing boolean value indicating if BoD is removed
     */
    @PostMapping(value = "/admin/removeBenefitOfDoubt", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ResponseDataBean> removeBenefitOfDoubt(@RequestParam final Long testId, @RequestParam final Long questionId, @RequestParam final String userId) {
    	ResponseDataBean response = new ResponseDataBean();
    	try {
    		Map<String, Boolean> bodResponseMap = iaTestService.removingBenefitOfBoubt(testId, questionId, userId);
    		
    		response.setSuccess(true);
			response.setCode(HttpStatus.OK.value());
			response.setMessage("Successfully removed BoD for questionId: " + questionId + " of testId: " + testId);
			response.setData(bodResponseMap);
			
			logger.info("Remove BoD and re-run results response: {}", response);
			return new ResponseEntity<>(response, HttpStatus.OK);
    	}
    	catch(IllegalArgumentException ex) {
    		response.setSuccess(false);
			response.setCode(HttpStatus.BAD_REQUEST.value());
			response.setMessage(ex.getMessage());
			response.setData(Collections.singletonMap("benefitOfDoubt", false));
			
			logger.error("Unable to remove BoD for testId: {}, Error message: {}", testId, ex.getMessage());
			return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    	}
    	catch(Exception ex) {
    		response.setSuccess(false);
			response.setCode(HttpStatus.INTERNAL_SERVER_ERROR.value());
			response.setMessage("Failed to remove BoD for questionId: " + questionId + " of testId: " + testId);
			response.setData(Collections.singletonMap("benefitOfDoubt", false));
			
			logger.error("Error while removing Benefit of Doubt for testId: {} and questionId: {}, Exception thrown: ", testId, questionId, ex);
			return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
    	}
    }
    
    /**
     * Fetches student question attempt data of how many students attempted the test,
     * how many students where assigned the questionId passed as the parameter,
     * how many selected correct and incorrect options and how many did not attempt the assigned question.
     * @param testId - ID of the test
     * @param questionId - ID of the question
     * @param questionType - type of question
     * @param testMaxAttempts - max attempts
     * @return Map containing the student question attempt details
     */
    @GetMapping(value = "/admin/questionAttemptData", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ResponseDataBean> questionAttemptData(@RequestParam final Long testId, @RequestParam final Long questionId, 
    															@RequestParam final Integer questionType, @RequestParam final Integer testMaxAttempts) {
    	ResponseDataBean response = new ResponseDataBean();
    	try {
    		Map<String,Integer> dataMap = iaTestService.questionAttemptDataMap(testId, questionId, questionType, testMaxAttempts);
    		
    		response.setSuccess(true);
			response.setCode(HttpStatus.OK.value());
			response.setMessage("Successfully fetched question attempt data for questionId: " + questionId);
			response.setData(dataMap);
			
			logger.info("Question attempt data response: {}", response);
			return new ResponseEntity<>(response, HttpStatus.OK);
    	}
    	catch(Exception ex) {
    		response.setSuccess(false);
			response.setCode(HttpStatus.INTERNAL_SERVER_ERROR.value());
			response.setMessage("Failed to fetch question attempt data for questionId: " + questionId);
			
			logger.error("Error while fetching question attempt data for testId: {} and questionId: {}, Exception thrown: ", testId, questionId, ex);
			return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
    	}
    }
}
