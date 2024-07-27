package com.nmims.controllers;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang.RandomStringUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;
import org.springframework.web.servlet.ModelAndView;


import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.web.servlet.support.RequestContextUtils;
import org.springframework.web.servlet.view.RedirectView;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.reflect.TypeToken;
import com.nmims.beans.AuditTrailExamBean;
import com.nmims.beans.CKeditorErrorBean;
import com.nmims.beans.CKeditorResponseBean;
import com.nmims.beans.ConsumerProgramStructureExam;
import com.nmims.beans.ErrorAnalyticsBean;
import com.nmims.beans.FacultyExamBean;
import com.nmims.beans.FileBean;
import com.nmims.beans.AccumulateAuditTrailsBean;
import com.nmims.beans.GetTestsBySapidResponseBean;
import com.nmims.beans.LogFileAnalysisBean;
import com.nmims.beans.NetworkLogsExamBean;
import com.nmims.beans.PageVisitExamBean;
import com.nmims.beans.PageVisitsBean;
import com.nmims.beans.Person;
import com.nmims.beans.PostExamBean;
import com.nmims.beans.ResponseBean;
import com.nmims.beans.ResultDomain;
import com.nmims.beans.SectionBean;
import com.nmims.beans.StudentExamBean;
import com.nmims.beans.StudentQuestionResponseExamBean;
import com.nmims.beans.StudentsTestDetailsExamBean;
import com.nmims.beans.TEEResultBean;
import com.nmims.beans.TestAuditTrailsApiResponseExamBean;
import com.nmims.beans.TestExamBean;
import com.nmims.beans.TestLogResponseExamBean;
import com.nmims.beans.TestQuestionExamBean;
import com.nmims.beans.TestQuestionConfigBean;
import com.nmims.beans.TestQuestionOptionExamBean;
import com.nmims.beans.TestTypeBean;
import com.nmims.beans.TestWeightageBean;
import com.nmims.daos.AuditTrailsDAO;
import com.nmims.daos.FacultyDAO;
import com.nmims.daos.TestDAO;
import com.nmims.helpers.AmazonS3Helper;
import com.nmims.helpers.EmailHelper;
import com.nmims.helpers.ExamLogFileHelper;
import com.nmims.helpers.ExcelHelper;
import com.nmims.helpers.IATestHelper;
import com.nmims.helpers.MailSender;
import com.nmims.services.IATestService;
import com.nmims.beans.LostFocusLogExamBean;

@Controller
@CrossOrigin(origins="*", allowedHeaders="*")
public class TestController extends BaseController {


	private List<String> programList;
	private List<String> subjectList;
	private List<FacultyExamBean> facultyList;
	private HashMap<String,TestTypeBean> testTypesMap;

	private String TEST_QUESTION_IMAGE_BASE_PATH= "E:/TEST_QUESTION_IMAGES/";

	//private String TEST_QUESTION_ASSIGNMENT_BASE_PATH= "D:/TEST_QUESTION_ASSIGNMENT/"; for prod
	private String TEST_QUESTION_ASSIGNMENT_BASE_PATH= "E:/TEST_QUESTION_ASSIGNMENT/"; //for local
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
	
	
    @Autowired
    TestDAO tDao; 
    
    @Autowired
    AuditTrailsDAO auditDao;
    
    @Autowired
    private IATestService iaTestService;
    
	@Value("#{'${CURRENT_YEAR_LIST}'.split(',')}")
	private ArrayList<String> currentYearList; 
	

    @Autowired
    private AmazonS3Helper amazonS3Helper;
    
    @Value("${AWS_IA_FILE_BUCKET}")
	private String awsIAFileBucket;

    @Value("${ENVIRONMENT}")
	private String environment;
	
    private static final String TEST_TYPE_ASSIGNMENT = "Assignment";
    private static final String TEST_TYPE_PROJECT = "Project";

	private ArrayList<ConsumerProgramStructureExam> consumerTypeList;

	private Map<String,String> consumerTypeIdNameMap;

	private Map<String,String> programStructureIdNameMap;

	private Map<String,String> programIdNameMap;

	private final int pageSize = 20;
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
		
		TestDAO dao = (TestDAO)act.getBean("testDao");
		this.facultyList = dao.getFaculties();
		
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

	@RequestMapping(value = "/addTestForm", method =  RequestMethod.POST)

	public String addTestForm(HttpServletRequest request, HttpServletResponse response,
			@RequestParam("id") Integer id,Model m,@ModelAttribute TestExamBean module ) throws ParseException {
		
/*    	if(!checkSession(request, response)){
			return "studentPortalRediret";
		} */ 
		String sessionPlanId=request.getParameter("sessionPlanId");
		List<FacultyExamBean>facultyList = getFacultyList();
		module.setSubjectCode(getInitials(module.getSubject())); 
		//get facultyName from list
		for(FacultyExamBean faculty :facultyList) {
			 
			if(faculty.getFacultyId().equalsIgnoreCase(module.getFacultyId())) { 
				module.setFacultyName(faculty.getFirstName()+" "+faculty.getLastName());
			}
		} 
		
		m.addAttribute("facultyList", facultyList);
		m.addAttribute("test", new TestExamBean());
		
		//for add test old logic
		if(id == null ||sessionPlanId == null) {
			m.addAttribute("consumerType", getConsumerTypeList());
			return "test/addTestFromModuleId";
		}
		
		m.addAttribute("module", module);
		m.addAttribute("moduleId", id);
		m.addAttribute("sessionPlanId",sessionPlanId);
		m.addAttribute("templateTypes",tDao.getTemplateTypes());
		return "test/addTestFromModuleId"; 
	}
	

	@RequestMapping(value = "/addTest", method = RequestMethod.POST)
	public RedirectView addTest(HttpServletRequest request,
			HttpServletResponse response,
			@ModelAttribute TestExamBean test,RedirectAttributes redirectAttrs,
			Model m) {

		String userId = (String) request.getSession().getAttribute("userId");

		try { 
			test.setCreatedBy(userId);
			test.setLastModifiedBy(userId);
			test.setActive("Y");
			
			if("old".equalsIgnoreCase(test.getApplicableType())) {
				test.setReferenceId(0);
			}
            
			if(test.getId() == null) { // save new test

					 long saveTest=0;
					 saveTest = tDao.saveTest(test);
					 if(saveTest==0) {
						 	redirectAttrs.addFlashAttribute("error", "true");
						 	redirectAttrs.addFlashAttribute("errorMessage", "Error in saving test to DB");
						}else {
							//test.setId(saveTest);
							test.setId(saveTest);
							saveTestConfig(test);
							String createTestIdConfigurationMappingsError = createTestIdConfigurationMappings(tDao,test);
							
							if(StringUtils.isBlank(createTestIdConfigurationMappingsError)) {
								if(!test.getTestType().equals(TEST_TYPE_ASSIGNMENT) && !test.getTestType().equals(TEST_TYPE_PROJECT)) {
									tDao.insertMCQInPost(test);
									PostExamBean post  = tDao.findPostByReferenceId(test.getId());
									//insertToRedis(post);
									refreshRedis(post);
								}
								redirectAttrs.addFlashAttribute("success", "true");
								redirectAttrs.addFlashAttribute("successMessage", "Test Created Successfully.");
								
								return new RedirectView("viewTestDetails?id="+saveTest);
							}else {

								int deleted = tDao.deleteTest(test.getId());
								if(deleted < 0) {
									redirectAttrs.addFlashAttribute("error", "true");
									redirectAttrs.addFlashAttribute("errorMessage", "Error in deleting test.");
								}
								
								redirectAttrs.addFlashAttribute("error", "true");
								redirectAttrs.addFlashAttribute("errorMessage", "Error in saving test to DB, createTestIdConsumerTypeIdMappingsError : "+createTestIdConfigurationMappingsError);
								
							}
							
						}
				
				
			}else { // update test
				TestExamBean testBeforeUpdate = tDao.getTestById(test.getId());
				boolean updateTest = tDao.updateTest(test); 
			    if(test.getTemplateId().length()>0) {
			    	tDao.deleteQuestionConfig(test.getId());
				    saveTestConfig(test);
				    
			    }
				if(!updateTest) {
					redirectAttrs.addFlashAttribute("error", "true");
					redirectAttrs.addFlashAttribute("errorMessage", "Error in saving test to DB");
					return new RedirectView("editTest?id="+test.getId()); 

				}else {

					String isTestConfigChanged = checkTestConfigChanged(tDao,testBeforeUpdate);

					if("true".equalsIgnoreCase(isTestConfigChanged)) {

						//Delete old entries 
						String deleteConsumerProgramStructureIdbyTestIdError = tDao.deleteTestIdNConfigMappingbyTestId(test.getId());

						if(!StringUtils.isBlank(deleteConsumerProgramStructureIdbyTestIdError)) {

							redirectAttrs.addFlashAttribute("error", "true");
							redirectAttrs.addFlashAttribute("errorMessage", "Error in saving test to DB, deleteConsumerProgramStructureIdbyTestIdError : "+deleteConsumerProgramStructureIdbyTestIdError);
							return new RedirectView("viewTestDetails?id="+test.getId()); 
						}

						String createTestIdConfigurationMappingsError = createTestIdConfigurationMappings(tDao,test);

						if(StringUtils.isBlank(createTestIdConfigurationMappingsError)) {
							if(!test.getTestType().equals(TEST_TYPE_ASSIGNMENT) && !test.getTestType().equals(TEST_TYPE_PROJECT)) {
								tDao.updateMCQInPost(test);
								PostExamBean post  = tDao.findPostByReferenceId(test.getId());
								refreshRedis(post);
							}

							redirectAttrs.addFlashAttribute("success", "true");
							redirectAttrs.addFlashAttribute("successMessage", "Test Updated Successfully.");
							return new RedirectView("viewTestDetails?id="+test.getId()); 
						}else {
							redirectAttrs.addFlashAttribute("error", "true");
							redirectAttrs.addFlashAttribute("errorMessage", "Error in saving test to DB, createTestIdConsumerTypeIdMappingsError : "+createTestIdConfigurationMappingsError);
						}

					}else if("false".equalsIgnoreCase(isTestConfigChanged)) { //do nothing
					}else {
						redirectAttrs.addFlashAttribute("error", "true");
						redirectAttrs.addFlashAttribute("errorMessage", "Error in saving to DB, Please click on update again till success / contact developer team <br> Error : "+isTestConfigChanged);
					}

				}
			}
			
		} catch (Exception e) {
			
			redirectAttrs.addFlashAttribute("error", "true");
			redirectAttrs.addFlashAttribute("errorMessage", "Error : "+e.getMessage());
			e.printStackTrace();
		}
		return new RedirectView("addTestForm");  
	}


	/*	public String insertToRedis(Post posts) {
		RestTemplate restTemplate = new RestTemplate();
		try {
	  	    String url = SERVER_PATH+"timeline/api/post/savePostInRedis";
			HttpHeaders headers = new HttpHeaders();
			  headers.setAccept(Arrays.asList(MediaType.APPLICATION_JSON));
			  HttpEntity<Post> entity = new HttpEntity<Post>(posts,headers);

			  return restTemplate.exchange(
				 url,
			     HttpMethod.POST, entity, String.class).getBody();
		} catch (RestClientException e) {
			
			return "Error IN rest call got "+e.getMessage();
		}
	}*/
	public String refreshRedis(PostExamBean posts) {
		RestTemplate restTemplate = new RestTemplate();
		try {
			posts.setTimeboundId(Integer.parseInt(posts.getSubject_config_id()));
			String url = SERVER_PATH+"timeline/api/post/refreshRedisDataByTimeboundIdForAllIntances";
			HttpHeaders headers = new HttpHeaders();
			headers.setAccept(Arrays.asList(MediaType.APPLICATION_JSON));
			HttpEntity<PostExamBean> entity = new HttpEntity<PostExamBean>(posts,headers);

			return restTemplate.exchange(
					url,
					HttpMethod.POST, entity, String.class).getBody();
		} catch (RestClientException e) {
			
			return "Error IN rest call got "+e.getMessage();
		}
	}
 
	private String createTestIdConfigurationMappings(TestDAO dao, TestExamBean test) {

		List<Long> configIds = getConfigIdsForTestLiveSettings(dao,test);
		return dao.insertTestIdNConfigurationMappings(test,configIds);


		/*to be deleted kept for reference
		 * if("module".equalsIgnoreCase(test.getApplicableType()) ) {
			List<Long> temp = new ArrayList<>();
			temp.add((long)0);
			return dao.insertTestIdNConfigurationMappings(test,temp);
		}else if("batch".equalsIgnoreCase(test.getApplicableType())) {
			//1. Get programSemSubjectIds
			ArrayList<String> programSemSubjectIds =
			dao.getProgramSemSubjectIdsBySubjectNProgramConfig(test.getProgramId()
					 ,test.getProgramStructureId()
					 ,test.getConsumerTypeId()
					 ,test.getSubject());
			if(programSemSubjectIds.isEmpty()) {
				return " Error in getting programSemSubjectIds. ";
			}

			//2. get timeboundIds by programSemSubjectIds
			List<Long> timeboundIds = 
				dao.getTimeboundIdsByProgramSemSubjectIdsNBatchId(programSemSubjectIds,test.getAcadYear(),test.getAcadMonth(),test.getReferenceId());

			if(timeboundIds.isEmpty()) {
				return " Error in getting timeboundIds. ";
			}

			return dao.insertTestIdNConfigurationMappings(test,timeboundIds);

		}else if("old".equalsIgnoreCase(test.getApplicableType())) {
			ArrayList<String> programSemSubjectIds =
					dao.getProgramSemSubjectIdsBySubjectNProgramConfig(test.getProgramId()
							 ,test.getProgramStructureId()
							 ,test.getConsumerTypeId()
							 ,test.getSubject());

					if(programSemSubjectIds.isEmpty()) {
						return " Error in getting programSemSubjectIds. ";
					}

					//convert programSemSubjectIds to array of long id to be used in common insert method
					List<Long> programSemSubjectIdsToLong = new ArrayList<>();
					for( String id : programSemSubjectIds) {

						try {
							programSemSubjectIdsToLong.add(Long.parseLong(id));
						} catch (NumberFormatException e) {
							return "Error in converting programSemSubjectIds to Long Array : ";
						}

					}

					return dao.insertTestIdNConfigurationMappings(test,programSemSubjectIdsToLong);

			}else {
				return "Unknown applicable type : "+test.getApplicableType();
		}*/
	}

	/*returns true if value is changed in form*/
	private String checkTestConfigChanged(TestDAO dao, TestExamBean test) {

		try {
			TestExamBean testFromDB = dao.getTestById(test.getId());
			/*
			 * "ConsumerType "+test.getConsumerTypeIdFormValue()+" "+testFromDB.
			 * getConsumerTypeIdFormValue() +
			 * "\n ProgramStructure "+test.getProgramStructureIdFormValue()+" "+testFromDB.
			 * getProgramStructureIdFormValue() +
			 * "\n Program "+test.getProgramIdFormValue()+" "+testFromDB.
			 * getProgramIdFormValue() +
			 * "\n applicableType : "+test.getApplicableType()+" "+testFromDB.
			 * getApplicableType() +
			 * "\n referenceID : "+test.getReferenceId()+" "+testFromDB.getReferenceId() +
			 * "\n Subject : "+test.getSubject()+" "+testFromDB.getSubject() +
			 * "\n AcadYear : "+test.getAcadYear()+" "+testFromDB.getAcadYear() +
			 * "\n AcadMonth : "+test.getAcadMonth()+" "+testFromDB.getAcadMonth() );
			 */

			if( test.getConsumerTypeIdFormValue().equalsIgnoreCase(testFromDB.getConsumerTypeIdFormValue())
					&& test.getProgramStructureIdFormValue().equalsIgnoreCase(testFromDB.getProgramStructureIdFormValue())
					&& test.getProgramIdFormValue().equalsIgnoreCase(testFromDB.getProgramIdFormValue())
					&& test.getApplicableType().equalsIgnoreCase(testFromDB.getApplicableType())
					&& test.getReferenceId() == testFromDB.getReferenceId()
					&& test.getSubject().equalsIgnoreCase(testFromDB.getSubject())
					&& test.getAcadYear() == testFromDB.getAcadYear()
					&& test.getAcadMonth().equalsIgnoreCase(testFromDB.getAcadMonth())
					) {
				return "false";
			}else {
				return "true";
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			
			return "Error in checkTestConfigChanged : "+e.getMessage();
		}

	}

	private String createTestIdConsumerTypeIdMappings_NOTINUSE(TestDAO dao,TestExamBean test) {

		if(test.getProgramId().split(",").length>1 
				|| test.getProgramStructureId().split(",").length>1
				|| test.getConsumerTypeId().split(",").length>1 
				)
		{
			// If Any Option is Selected Is "All"
			ArrayList<String> consumerProgramStructureIds = dao.getconsumerProgramStructureIdsWithSubject(test.getProgramId()
					,test.getProgramStructureId()
					,test.getConsumerTypeId()
					,test.getSubject());

			return dao.batchInsertTestIdConsumerProgramStructureIdMappingsForMultipleConsumerIds(test,consumerProgramStructureIds); 
		}
		else {

			String consumerProgramStructureId = dao.getConsumerProgramStructureIdByProgramProgramStructureConsumerTypeId(test.getProgramId()
					,test.getProgramStructureId()
					, test.getConsumerTypeId());

			return dao.insertSingleTestIdConsumerProgramStructureIdMapping(test,consumerProgramStructureId);
		}

	}

	@RequestMapping(value = "/deleteTest", method =  RequestMethod.GET)
	public String deleteTest(HttpServletRequest request, HttpServletResponse response, Model m,
			@RequestParam("id") Long id)  {
		try {
			TestDAO dao = (TestDAO)act.getBean("testDao");
			int deleted = dao.deleteTest(id);
			if(deleted > -1) {
				request.setAttribute("success", "true");
				request.setAttribute("successMessage", "Test Deleted Successfully.");
	
			}
			else {
				request.setAttribute("error", "true");
				request.setAttribute("errorMessage", "Eror in deleting test.");
	
			}
		}
		catch(Exception ex) {
			//Custom error message is thrown in IllegalArgumentException
			String errorMessage = (ex instanceof IllegalArgumentException) ? ex.getMessage() : "Failed to delete the test! Please try again.";
			request.setAttribute("error", "true");
			request.setAttribute("errorMessage", errorMessage);
		}
		
		return viewAllTests("false", request, response, m);
	}
	
	@RequestMapping(value = "/viewAllTests", method =  RequestMethod.GET)
	public String viewAllTests(@RequestParam(name = "loadAllTest", defaultValue = "false")String loadAllTest, HttpServletRequest request,
            HttpServletResponse response, Model m) {

		if(!checkSession(request, response)){
			redirectToPortalApp(response);
			return null;
		}
		
		if(isCurrentUserFaculty(request)) {
			return viewTestsForFaculty(request, response, m);
		}
		
		TestDAO dao = (TestDAO)act.getBean("testDao");
		List<TestExamBean> allTests = new ArrayList<>();
		 
		if(loadAllTest.equals("true"))
			allTests = dao.getAllTests();
		else
            try {
            	allTests = dao.getCurrentLiveTests();
            } catch (Exception e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
		 
		m.addAttribute("allTests", addConsumerProgramProgramStructureNameToEachTest(dao,allTests));
		m.addAttribute("subjectList", getSubjectList());
		m.addAttribute("programList", getProgramList());
		m.addAttribute("searchBean", new TestExamBean());
		m.addAttribute("programList", getProgramList());
		m.addAttribute("facultyList", getFacultyList());
		m.addAttribute("consumerType", getConsumerTypeList());

		return "test/viewAllTests";
	}
	
	//returns true if current user is faculty
	private boolean isCurrentUserFaculty(HttpServletRequest request) {

		TestDAO dao = (TestDAO)act.getBean("testDao");
		Person currentUser = (Person) request.getSession().getAttribute("user");
		
		String roles = currentUser!= null ? currentUser.getRoles() : "";
		
		if((roles.indexOf("Insofe") == -1) && (roles.indexOf("Faculty") == -1)){
			return false;
		}else {
			return true;
		}
		
	}

	private List<TestExamBean> addConsumerProgramProgramStructureNameToEachTest(TestDAO dao,List<TestExamBean> allTests) {
		// TODO Auto-generated method stub
		int size = 0, i = 1;
		String testIds = "";

		if(allTests != null ) {
			size = allTests.size();
		}

		for(TestExamBean t : allTests) {
			if(i == size) {
				testIds += t.getId()+"";
			}else {
				testIds += t.getId()+",";
			}
			i++;
		}

		// to be implemented later by PS 25Jun
		Map<Long,Integer> typeIdNCountOfProgramsApplicableToMap =  dao.getTypeIdNCountOfProgramsApplicableToMap(testIds);

		for(TestExamBean t : allTests) {

			try {
				t = setProgramConfigNamesForSingleTest(t);

				t = dao.getReference_Batch_Or_Module_Name(t);


				t.setCountOfProgramsApplicableTo(typeIdNCountOfProgramsApplicableToMap.get(t.getId()));
			} catch (Exception e) {
				// TODO Auto-generated catch block
				
			}
		}

		return allTests;
	}

	private TestExamBean setProgramConfigNamesForSingleTest(TestExamBean t) {
		try {
			t.setConsumerType(getConsumerTypeIdNameMap().get(t.getConsumerTypeIdFormValue()));

			if(t.getProgramStructureIdFormValue().split(",").length>1) {
				t.setProgramStructure("All");;
			}else {
				t.setProgramStructure(getProgramStructureIdNameMap().get(t.getProgramStructureIdFormValue()));;
			}

			if(t.getProgramIdFormValue().split(",").length>1) {
				t.setProgram("All");;
			}else {
				t.setProgram(getProgramIdNameMap().get(t.getProgramIdFormValue()));; 
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			
		}
		return t;
	}

	@RequestMapping(value = "/editTestQuestion", method =  RequestMethod.GET)
	public String editTestQuestion(HttpServletRequest request,
			HttpServletResponse response,
			Model m,
			@RequestParam("id") Long id)  {

		if(!checkSession(request, response)){
			//return new ModelAndView("studentPortalRediret");
			redirectToPortalApp(response);
			return null;
		}
		
		
		TestDAO dao = (TestDAO)act.getBean("testDao");

		TestQuestionExamBean testQuestion = dao.getTestQuestionById(id);

		TestExamBean test = dao.getTestById(testQuestion.getTestId());
		List<TestQuestionExamBean> testQuestions = dao.getTestQuestions(testQuestion.getTestId());
		
		List<SectionBean> sectionList = iaTestService.getApplicableSectionList(testQuestion.getTestId());
		m.addAttribute("sectionList", sectionList);
		
		m.addAttribute("testQuestions", testQuestions);
		m.addAttribute("test", test);
		m.addAttribute("testQuestion", testQuestion);

		return "test/editTestQuestion";
	}

	@RequestMapping(value = "/deleteTestQuestionOptionById", method =  RequestMethod.GET)
	public String deleteTestQuestionOptionById(HttpServletRequest request,
			HttpServletResponse response,
			Model m,
			@RequestParam("id") Long id,
			@RequestParam("testId") Long testId,
			@RequestParam("questionId") Long questionId)  {

		if(!checkSession(request, response)){
			//return new ModelAndView("studentPortalRediret");
			redirectToPortalApp(response);
			return null;
		}
		
		
		TestDAO dao = (TestDAO)act.getBean("testDao");

		String errorMessage = dao.deleteTestQuestionOptionById(id);
		
		setTestQuestionsInRedisByTestId(testId);
		
		if(StringUtils.isNumeric(errorMessage)) {
			request.setAttribute("success", "true");
			request.setAttribute("successMessage", "Option deleted successfully.");
		}else {
			request.setAttribute("error", "true");
			request.setAttribute("errorMessage", errorMessage);
		}
		
		return editTestQuestion(request, response, m, questionId);
	}
	
	@RequestMapping(value = "/editTest", method =  RequestMethod.GET)
	public String editTest(HttpServletRequest request,
			HttpServletResponse response,
			Model m,
			@RequestParam("id") Long id)  {
 
		 try {//flash message from redirectview
				Map<String, ?> flashMap = RequestContextUtils.getInputFlashMap(request);
					request.setAttribute("success", flashMap.get("success"));
					request.setAttribute("successMessage", flashMap.get("successMessage")); 
					request.setAttribute("error", flashMap.get("error"));
					request.setAttribute("errorMessage", flashMap.get("errorMessage")); 
			} catch (Exception e) { }
	        
		TestDAO dao = (TestDAO)act.getBean("testDao");
		TestExamBean test = dao.getTestById(id);
		test = setProgramConfigNamesForSingleTest(test);
		test = dao.getReference_Batch_Or_Module_Name(test);
		List<TestQuestionConfigBean> config =  tDao.getQuestionsConfigured(test.getId());
		test.setTestQuestionConfigBean(config);
		m.addAttribute("subjectList", getSubjectList());
		m.addAttribute("programList", getProgramList());
		m.addAttribute("test", test);
		m.addAttribute("facultyList", getFacultyList());
		m.addAttribute("templateTypes",tDao.getTemplateTypes());
		m.addAttribute("consumerType", getConsumerTypeList());
		return "test/addTestFromModuleId";  
	}
	@RequestMapping(value = "/showResultsForTest", method =  RequestMethod.GET)
	public String showResultsForTest(HttpServletRequest request,
			HttpServletResponse response,
			Model m,
			@RequestParam("id") Long id)  {

		TestDAO dao = (TestDAO)act.getBean("testDao");
		TestExamBean test = dao.getTestById(id);
		String userId =(String)request.getSession().getAttribute("userId");
		if(test!=null) {
			test.setLastModifiedBy(userId);
			test.setShowResultsToStudents("Y");
			boolean testUpdated = false; 
			if(StringUtils.isBlank(test.getInitialResultLiveDateTime()))
				testUpdated = dao.updateInitialResultLiveTest(test);
			else
				testUpdated = dao.updateLastModifiedResultLiveTest(test);
			
			if(!testUpdated) {
				request.setAttribute("error", "true");
				request.setAttribute("errorMessage", "Failed To Update Result Status.");

			}else {

				List<StudentsTestDetailsExamBean> testAttemptDetails = dao.getStudentsTestDetailsByTestId(id);
				String calculateScoreErrorMessage = "";
				for(StudentsTestDetailsExamBean studentsTestDetails : testAttemptDetails) {
					if(studentsTestDetails.getId() != null) {
						//Do update
						studentsTestDetails.setAttempt(studentsTestDetails.getAttempt());
						studentsTestDetails.setTestCompleted("Y");
						studentsTestDetails.setLastModifiedBy("Admin Result Declared");
						try {
							if("CopyCase".equalsIgnoreCase(studentsTestDetails.getAttemptStatus())) {
								studentsTestDetails.setScore(0);
							}else {
								studentsTestDetails.setScore(dao.caluclateTestScore(studentsTestDetails.getSapid(),studentsTestDetails.getTestId()));	
							}
						} catch (Exception e) {
							// TODO Auto-generated catch block
							
							studentsTestDetails.setScore(0);
						}
						boolean updated = dao.updateStudentsTestDetailsAfterShowResults(studentsTestDetails);
						if(!updated) {
							calculateScoreErrorMessage = calculateScoreErrorMessage +" Error in saving score for sapid : "+studentsTestDetails.getSapid()+". ";
						}
					}
				}

				if(StringUtils.isBlank(calculateScoreErrorMessage)) {


					boolean updated = dao.updateStudentsTestsResultStatusByTestId(test.getShowResultsToStudents(), test.getId());
					if(updated) {
						try {
                            test.setCanFacultyEditIA("N");
                            dao.upsertFacultyRightsForTest(test.getId(), userId);
	                    } catch (Exception e) {
	                            // TODO Auto-generated catch block
	                    }
						request.setAttribute("success", "true");
						request.setAttribute("successMessage", "Result Status Updated For "+test.getTestName()+" Successfully.");
					}else {

						request.setAttribute("error", "true");
						request.setAttribute("errorMessage", "Failed To Update Result Status.");
					}


				}else {
					request.setAttribute("error", "true");
					request.setAttribute("errorMessage", "Failed To Update Result Status."+calculateScoreErrorMessage);

				}
			}
		}else {
			request.setAttribute("error", "true");
			request.setAttribute("errorMessage", "Failed To Update Result Status.");

		}
		return viewAllTests("false", request, response, m);
	}
	
	@RequestMapping(value = "/hideResultsForTest", method =  RequestMethod.GET)
	public String hideResultsForTest(HttpServletRequest request,
			HttpServletResponse response,
			Model m,
			@RequestParam("id") Long id)  {

		TestDAO dao = (TestDAO)act.getBean("testDao");
		TestExamBean test = dao.getTestById(id);
		String userId =(String)request.getSession().getAttribute("userId");
		if(test!=null) {
			test.setLastModifiedBy(userId);
			test.setShowResultsToStudents("N");
			boolean testUpdated = dao.updateTest(test);
			if(!testUpdated) {
				request.setAttribute("error", "true");
				request.setAttribute("errorMessage", "Failed To Update Result Status.");

			}else {
				boolean updated = dao.updateStudentsTestsResultStatusByTestId(test.getShowResultsToStudents(), test.getId());
				if(updated) {
					try {
                        test.setCanFacultyEditIA("Y");
                        dao.upsertFacultyRightsForTest(test.getId(), userId);
	                }catch (Exception e) {
	                        // TODO: handle exception
	                }
					request.setAttribute("success", "true");
					request.setAttribute("successMessage", "Result Status Updated For "+test.getTestName()+" Successfully.");
				}else {

					request.setAttribute("error", "true");
					request.setAttribute("errorMessage", "Failed To Update Result Status.");
				}
			}
		}else {
			request.setAttribute("error", "true");
			request.setAttribute("errorMessage", "Failed To Update Result Status.");

		}
		return viewAllTests("false", request, response, m);
	}

	@RequestMapping(value = "/uploadTestQuestionForm", method =  RequestMethod.GET)
	public String uploadTestQuestionForm(HttpServletRequest request,
			HttpServletResponse response,
			Model m,
			@RequestParam("id") Long id)  {

		TestDAO dao = (TestDAO)act.getBean("testDao");
		TestExamBean test = dao.getTestById(id);

		HashMap<Long,TestTypeBean> typeIdBeanMap= getTypeIdNBeanMap();		
		List<TestQuestionExamBean> testQuestions = dao.getTestQuestions(id);

		for(TestQuestionExamBean q : testQuestions) {
			try {
				q.setTypeInString(typeIdBeanMap.get(new Long(q.getType())).getType());
			} catch (Exception e) {
				// TODO Auto-generated catch block
				
			}
		}
		
		List<SectionBean> sectionList = iaTestService.getApplicableSectionList(id);
		m.addAttribute("sectionList", sectionList);
		
		m.addAttribute("test", test);
		m.addAttribute("fileBean", new FileBean());
		m.addAttribute("testQuestions", testQuestions);
		m.addAttribute("typeIdBeanMap", typeIdBeanMap);

		return "test/uploadTestQuestion";
	}

	@RequestMapping(value = "/uploadTestQuestion", method =  RequestMethod.POST)
	public String uploadTestQuestion(HttpServletRequest request,	
			HttpServletResponse response,
			Model m,
			@ModelAttribute FileBean fileBean) {

		if(!checkSession(request, response)){
			return "studentPortalRediret";
		}

		String userId = (String) request.getSession().getAttribute("userId");
		MultipartFile file = fileBean.getFileData();
		Long testId = new Long(fileBean.getFileId());
		if (file.isEmpty()) {// Check if File was attached
			request.setAttribute("error", "true");
			request.setAttribute("errorMessage", "Please Select File to Upload...");
			return uploadTestQuestionForm(request,response,m,testId);
		}
		TestDAO dao = (TestDAO)act.getBean("testDao");
		TestExamBean test = dao.getTestById(testId);
		HashMap<String,TestTypeBean> testTypesMap= getTestTypesMap();


		try {
			ExcelHelper excelHelper = new ExcelHelper();

			ArrayList<List> resultList = excelHelper.readTestQuestionsExcel(fileBean,userId,testTypesMap);
			ArrayList<TestQuestionExamBean> testList = (ArrayList<TestQuestionExamBean>) resultList.get(0);
			ArrayList<TestQuestionExamBean> errorBeanList = (ArrayList<TestQuestionExamBean>) resultList.get(1);

			if (!errorBeanList.isEmpty()) {
				request.setAttribute("error", "true");
				String errorMessage="Error while uploading data caused due to bad data of rows  : ";
				for(TestQuestionExamBean errorBean : errorBeanList) {
					errorMessage = errorMessage +"\n  "+errorBean.getErrorMessage()+"<br>";
				}

				request.setAttribute("errorMessage", errorMessage);
				return uploadTestQuestionForm(request,response,m,testId);
			}

			for(TestQuestionExamBean q : testList) {
				//Escape HTML characters Start
				q = escapeHTMLFromString(q);
				if(!StringUtils.isBlank(q.getErrorMessage())) {
					request.setAttribute("error", "true");
					request.setAttribute("errorMessage", "Error in  uploadTestQuestion got "+q.getErrorMessage());

					return uploadTestQuestionForm(request,response,m,testId);
				}
				//END
			}


			ArrayList<String> errorList = dao.batchUpdateTestQuestion(testList);

			if (errorList.size() == 0) {
				request.setAttribute("success", "true");
				request.setAttribute("successMessage", testList.size() + " rows out of "
						+ testList.size() + " inserted successfully.");

				setTestQuestionsInRedisByTestId(testId);
				
				return viewTestDetails(request,response,m,testId);
			} else {
				request.setAttribute("error", "true");
				request.setAttribute("errorMessage", errorList.size()
						+ " records were NOT inserted. Please see row number of rows not inserted. Error row numbers "
						+ errorList);
			}
		} catch (Exception e) {
			
			request.setAttribute("error", "true");
			request.setAttribute("errorMessage", "Error in inserting rows. Error : "+e.getMessage());

		}
		return uploadTestQuestionForm(request,response,m,testId);
	}

	//code for uploadtestsubquestions start
	@RequestMapping(value = "/uploadTestSubQuestion", method =  RequestMethod.POST)
	public String uploadTestSubQuestion(HttpServletRequest request,	
			HttpServletResponse response,
			Model m,
			@ModelAttribute FileBean fileBean) {

		if(!checkSession(request, response)){
			return "studentPortalRediret";
		}

		String userId = (String) request.getSession().getAttribute("userId");
		MultipartFile file = fileBean.getFileData();
		Long testId = new Long(fileBean.getFileId());
		Long questionId= fileBean.getId();
		if (file.isEmpty()) {// Check if File was attached
			request.setAttribute("error", "true");
			request.setAttribute("errorMessage", "Please Select File to Upload...");
			return uploadTestQuestionForm(request,response,m,testId);
		}
		TestDAO dao = (TestDAO)act.getBean("testDao");
		TestExamBean test = dao.getTestById(testId);
		HashMap<String,TestTypeBean> testTypesMap= getTestTypesMap();


		try {
			ExcelHelper excelHelper = new ExcelHelper();

			ArrayList<List> resultList = excelHelper.readTestQuestionsExcel(fileBean,userId,testTypesMap);
			ArrayList<TestQuestionExamBean> testList = (ArrayList<TestQuestionExamBean>) resultList.get(0);
			ArrayList<TestQuestionExamBean> errorBeanList = (ArrayList<TestQuestionExamBean>) resultList.get(1);

			if (!errorBeanList.isEmpty()) {
				request.setAttribute("error", "true");
				String errorMessage="Error while uploading data caused due to bad data of rows  : ";
				for(TestQuestionExamBean errorBean : errorBeanList) {
					errorMessage = errorMessage +"\n  "+errorBean.getErrorMessage()+"<br>";
				}

				request.setAttribute("errorMessage", errorMessage);
				return uploadTestQuestionForm(request,response,m,testId);
			}



			for(TestQuestionExamBean q : testList) {
				q.setIsSubQuestion(1);
				q.setMainQuestionId(questionId);
			}

			ArrayList<String> errorList = dao.batchUpdateTestQuestion(testList);

			if (errorList.size() == 0) {
				request.setAttribute("success", "true");
				request.setAttribute("successMessage", testList.size() + " rows out of "
						+ testList.size() + " inserted successfully.");

				return viewTestDetails(request,response,m,testId);
			} else {
				request.setAttribute("error", "true");
				request.setAttribute("errorMessage", errorList.size()
						+ " records were NOT inserted. Please see row number of rows not inserted. Error row numbers "
						+ errorList);
			}
		} catch (Exception e) {
			
			request.setAttribute("error", "true");
			request.setAttribute("errorMessage", "Error in inserting rows. Error : "+e.getMessage());

		}
		return uploadTestQuestionForm(request,response,m,testId);
	}
	//code for uploadtestsubquestion end


	@RequestMapping(value = "/viewTestDetails", method =  RequestMethod.GET)
	public String viewTestDetails(HttpServletRequest request,
			HttpServletResponse response,
			Model m,
			@RequestParam("id") Long id)  {
		try {//flash message from redirectview
			Map<String, ?> flashMap = RequestContextUtils.getInputFlashMap(request);
				request.setAttribute("success", flashMap.get("success"));
				request.setAttribute("successMessage", flashMap.get("successMessage")); 
				request.setAttribute("error", flashMap.get("error"));
				request.setAttribute("errorMessage", flashMap.get("errorMessage")); 
		} catch (Exception e) { }

		TestDAO dao = (TestDAO)act.getBean("testDao");
		TestExamBean test = dao.getTestById(id);
		test = setProgramConfigNamesForSingleTest(test);

		String testStudentId = dao.getTestStudentForTestByTestId(id);
		//get applicable sections 
		ArrayList<SectionBean> sectionList = tDao.getSectionsByTestId(id);
		//get configurations for each sections
		if(sectionList.size()>0) {
			for(SectionBean sectionBean : sectionList){ 
				ArrayList<SectionBean> s =tDao.getQuestionsConfiguredBasedOnSection(sectionBean.getId(),id);
				sectionBean.setSectionQnTypeConfigbean(s);
			}
		}
		List<SectionBean> sectionsInqnsUpoaded=  tDao.getSectionsInQuestionsUploaded(id); 
		for(SectionBean sectionBean : sectionsInqnsUpoaded){ 
			sectionBean.setSectionQnTypeConfigbean(tDao.getQuestionsUploadedBySection(id,sectionBean.getId()));
		}
		
		HashMap<Long,TestTypeBean> typeIdNBeanMap =  dao.getTypeIdNTypeMap();
		m.addAttribute("questionConfiguredList", dao.getQuestionConfigsListByTestId(id));
		m.addAttribute("typeIdNBeanMap", typeIdNBeanMap); 
		m.addAttribute("qnsConfigured", sectionList);
		m.addAttribute("qnsUpoaded", sectionsInqnsUpoaded);
		
		m.addAttribute("test", test);
		m.addAttribute("noOfQuestions", dao.getNoOfQuestionsByTestId(id));
		m.addAttribute("noOfQuestionWeightages", dao.getNoOfQuestionsWeightageEntriesByTestId(id));
		m.addAttribute("ifTestlive", dao.getLiveTestByModuleId(test.getReferenceId())>0?true:false);
		if(testStudentId != null) {
			m.addAttribute("testStudentId",testStudentId);
		}

		return "test/testDetails";
	}

	@RequestMapping(value = "/configureTestQuestions", method =  RequestMethod.GET)
	public String configureTestQuestions(HttpServletRequest request,
			HttpServletResponse response,
			Model m,
			@RequestParam("id") Long id)  {

		TestDAO dao = (TestDAO)act.getBean("testDao");
		TestExamBean test = dao.getTestById(id);
		HashMap<Long,TestTypeBean> typeIdNBeanMap =  dao.getTypeIdNTypeMap();
		List<Long> applicableTypes = dao.getQuestionTypesByTestId(id);
		HashMap<Long,TestQuestionConfigBean> testIdNConfigMap  =  dao.getQuestionConfigsByTestId(id, applicableTypes);

		m.addAttribute("test", test);
		m.addAttribute("typeIdNBeanMap", typeIdNBeanMap);
		m.addAttribute("applicableTypes", applicableTypes);
		m.addAttribute("testIdNConfigMap", testIdNConfigMap);

		return "test/configureTestQuestions";
	}

	@RequestMapping(value = "/viewTestQuestionDetails", method =  RequestMethod.GET)
	public String viewTestQuestionDetails(HttpServletRequest request,
			HttpServletResponse response,
			Model m,
			@RequestParam("id") Long id)  {

		TestDAO dao = (TestDAO)act.getBean("testDao");
		TestQuestionExamBean question = dao.getTestQuestionById(id);
		HashMap<Long,TestTypeBean> typeIdBeanMap= getTypeIdNBeanMap();		
		try {
			question.setTypeInString(typeIdBeanMap.get(new Long(question.getType())).getType());
		} catch (Exception e) {
			// TODO Auto-generated catch block
			
		}

		m.addAttribute("question", question);
		m.addAttribute("fileBean", new FileBean());


		return "test/testQuestionDetails";
	}





	@RequestMapping(value = "/uploadTestQuestionWeightageForm", method =  RequestMethod.GET)
	public String uploadTestQuestionWeightageForm(HttpServletRequest request,
			HttpServletResponse response,
			Model m,
			@RequestParam("testId") Long id)  {

		TestDAO dao = (TestDAO)act.getBean("testDao");
		TestExamBean test = dao.getTestById(id);
		m.addAttribute("test", test);
		m.addAttribute("fileBean", new FileBean());

		return "test/addTestWeightage";
	}


	@RequestMapping(value = "/uploadTestQuestionWeightage", method =  RequestMethod.POST)
	public String uploadTestQuestionWeightage(HttpServletRequest request,	
			HttpServletResponse response,
			Model m,
			@ModelAttribute FileBean fileBean) {

		if(!checkSession(request, response)){
			return "studentPortalRediret";
		}

		String userId = (String) request.getSession().getAttribute("userId");
		MultipartFile file = fileBean.getFileData();
		Long testId = new Long(fileBean.getFileId());
		if (file.isEmpty()) {// Check if File was attached
			request.setAttribute("error", "true");
			request.setAttribute("errorMessage", "Please Select File to Upload...");
			return uploadTestQuestionWeightageForm(request,response,m,testId);
		}
		TestDAO dao = (TestDAO)act.getBean("testDao");


		try {
			ExcelHelper excelHelper = new ExcelHelper();

			ArrayList<List> resultList = excelHelper.readTestWeightageExcel(fileBean,userId);
			ArrayList<TestWeightageBean> testList = (ArrayList<TestWeightageBean>) resultList.get(0);
			ArrayList<TestWeightageBean> errorBeanList = (ArrayList<TestWeightageBean>) resultList.get(1);

			if (!errorBeanList.isEmpty()) {
				request.setAttribute("error", "true");
				String errorMessage="Error while uploading data caused due to bad data of rows  : ";
				for(TestWeightageBean errorBean : errorBeanList) {
					errorMessage = errorMessage +"\n  "+errorBean.getErrorMessage()+"<br>";
				}

				request.setAttribute("errorMessage", errorMessage);
				return uploadTestQuestionWeightageForm(request,response,m,testId);
			}

			//Delete all old weightages of the test :> To be changed just for now.
			try {
				int deleteTestQuestionByTestId = dao.deleteTestWeightageByTestId(testId);
			} catch (Exception e) {
				
				request.setAttribute("error", "true");
				request.setAttribute("errorMessage", "Ubable to delete old questions.");
				return uploadTestQuestionWeightageForm(request,response,m,testId);
			}

			ArrayList<String> errorList = dao.batchUpdateTestWeightage(testList);

			if (errorList.size() == 0) {
				request.setAttribute("success", "true");
				request.setAttribute("successMessage", testList.size() + " rows out of "
						+ testList.size() + " inserted successfully.");

				return viewTestDetails(request,response,m,testId);
			} else {
				request.setAttribute("error", "true");
				request.setAttribute("errorMessage", errorList.size()
						+ " records were NOT inserted. Please see row number of rows not inserted. Error row numbers "
						+ errorList);
			}
		} catch (Exception e) {
			
			request.setAttribute("error", "true");
			request.setAttribute("errorMessage", "Error in inserting rows. Error : "+e.getMessage());

		}
		return uploadTestQuestionForm(request,response,m,testId);
	}


	@RequestMapping(value = "/checkTestQuestionsForm", method =  RequestMethod.GET)
	public String checkTestQuestionsForm(HttpServletRequest request,
			HttpServletResponse response,
			Model m
			){
		if(!checkSession(request, response)){
			return "studentPortalRediret";
		}


		String userId = (String) request.getSession().getAttribute("userId");
		TestDAO dao = (TestDAO)act.getBean("testDao");
		HashMap<Long,List<StudentQuestionResponseExamBean>> testIdAndAnswersToCheckMap = dao.getTestAnswerByFacultyId(userId);
		Set<Long> testIds = testIdAndAnswersToCheckMap.keySet();
		List<TestExamBean> testsDetails = dao.getTestsForFaculty(testIds);

		m.addAttribute("testsDetails",testsDetails);
		m.addAttribute("testIdAndAnswersToCheckMap",testIdAndAnswersToCheckMap);
		return "test/checkTestQuestionsFaculty";
	}

//	to be deleted, api shifted to rest controller
//	//saveTestAnswersMarks start
//	@RequestMapping(value = "/m/saveTestAnswersMarks", method = RequestMethod.POST, consumes = "application/json", produces = "application/json")
//	public ResponseEntity<HashMap<String,String>> m_saveTestAnswersMarks(@RequestBody StudentQuestionResponseBean answer){
//
//		HttpHeaders headers = new HttpHeaders();
//		headers.add("Content-Type", "application/json");
//		TestDAO dao = (TestDAO)act.getBean("testDao");
//		HashMap<String,String> response = new HashMap<>();
//
//		List<StudentQuestionResponseBean> answers =  dao.getTestAnswerBySapidAndQuestionId(answer.getSapid(), answer.getQuestionId(), answer.getAttempt());
//		if(answers.isEmpty()) {
//			response.put("Status", "Fail");
//			return new ResponseEntity<>(response,headers, HttpStatus.INTERNAL_SERVER_ERROR);
//
//		}else {
//			//Do update
//			boolean updated = dao.updateQuestionMarks(answer);
//			if(!updated) {
//				response.put("Status", "Fail");
//				return new ResponseEntity<>(response,headers, HttpStatus.INTERNAL_SERVER_ERROR);
//			}
//			else {
//				response.put("Status", "Success");
//				return new ResponseEntity<>(response,headers, HttpStatus.OK);
//			}
//		}
//	}
//
//	//end 

//	//saveTestQuestionsConfig start
//	@RequestMapping(value = "/m/saveTestQuestionsConfig", method = RequestMethod.POST, consumes = "application/json", produces = "application/json")
//	public ResponseEntity<HashMap<String,String>> m_saveTestQuestionsConfig(@RequestBody TestQuestionConfigBean config){
//
//		HttpHeaders headers = new HttpHeaders();
//		headers.add("Content-Type", "application/json");
//		TestDAO dao = (TestDAO)act.getBean("testDao");
//		HashMap<String,String> response = new HashMap<>();
//
//		TestQuestionConfigBean tempConfig = dao.getTestConfigByTestIdNQuestionType(config.getTestId(), config.getType());
//		if(tempConfig == null) {
//			//Do insert
//			long saved = dao.saveTestConfig(config);
//			if(saved == 0) {
//				response.put("Status", "Fail");
//				return new ResponseEntity<>(response,headers, HttpStatus.INTERNAL_SERVER_ERROR);
//			}
//			else {
//				response.put("Status", "Success");
//				setTestQuestionsInRedisByTestId(config.getTestId());
//				return new ResponseEntity<>(response,headers, HttpStatus.OK);
//			}
//
//
//		}else {
//			//Do update
//			boolean updated = dao.updateTestConfig(config);
//			if(!updated) {
//				response.put("Status", "Fail");
//				return new ResponseEntity<>(response,headers, HttpStatus.INTERNAL_SERVER_ERROR);
//			}
//			else {
//				response.put("Status", "Success");
//				setTestQuestionsInRedisByTestId(config.getTestId());
//				return new ResponseEntity<>(response,headers, HttpStatus.OK);
//			}
//		}
//	}
//	//end 

	// for allocateFacultyToTestAnswers
	@RequestMapping(value = "/allocateFacultyToTestAnswersForm", method =  RequestMethod.GET)
	public String allocateFacultyToTestAnswersForm(HttpServletRequest request,
			HttpServletResponse response,
			Model m,
			@RequestParam("id") Long id)  {

		TestDAO dao = (TestDAO)act.getBean("testDao");
		TestExamBean test = dao.getTestById(id);
		
		FacultyDAO fDao = (FacultyDAO)act.getBean("facultyDAO");
		ArrayList<FacultyExamBean> facultyList = fDao.getFaculties();
		
		List<StudentQuestionResponseExamBean> facultyNAllocatedAnswers = dao.getFacultyIdNNoOfAllocatedAnswers(id);
		Integer noOfAllocationsLeft = dao.getNoOfAnswersNotAllocated(id);
		
		for(StudentQuestionResponseExamBean f : facultyNAllocatedAnswers) {
		}
		
		m.addAttribute("noOfAllocationsLeft", noOfAllocationsLeft);
		m.addAttribute("facultyNAllocatedAnswers", facultyNAllocatedAnswers);
		m.addAttribute("facultyList", facultyList);
		m.addAttribute("test", test);
		
		return "test/allocateFacultyToTestAnswers";
	}

//	//saveTestFacultyAnswersAllocation start
//	@RequestMapping(value = "/m/saveTestFacultyAnswersAllocation", method = RequestMethod.POST, consumes = "application/json", produces = "application/json")
//	public ResponseEntity<HashMap<String,String>> saveTestFacultyAnswersAllocations(@RequestBody StudentQuestionResponseBean allocation){
//
//		HttpHeaders headers = new HttpHeaders();
//		headers.add("Content-Type", "application/json");
//		TestDAO dao = (TestDAO)act.getBean("testDao");
//		HashMap<String,String> response = new HashMap<>();
//
//		boolean clearOldAllocations = dao.clearFacultyAllocation(allocation);
//		if(!clearOldAllocations) {
//			response.put("Status", "Fail");
//			return new ResponseEntity<>(response,headers, HttpStatus.INTERNAL_SERVER_ERROR);
//		}
//
//		boolean updateAllocation = dao.updateFacultyAllocations(allocation);
//
//		Integer noOfAllocationsLeft = dao.getNoOfAnswersNotAllocated(allocation.getTestId());
//		response.put("noOfAllocationsLeft", noOfAllocationsLeft.toString());
//		
//		Integer noOfAllocated = dao.getNoOfAnsweresAllocatedToFaculty(allocation.getFacultyId(), allocation.getTestId());
//		response.put("noOfAllocated", noOfAllocated.toString());
//		if(!updateAllocation) {
//			response.put("Status", "Fail");
//			return new ResponseEntity<>(response,headers, HttpStatus.INTERNAL_SERVER_ERROR);
//		}else {	
//			response.put("Status", "Success");
//			return new ResponseEntity<>(response,headers, HttpStatus.OK);
//
//		}
//	}
//	//end
//
//	//deleteTestFacultyAnswersAllocation start
//	@RequestMapping(value = "/m/deleteTestFacultyAnswersAllocation", method = RequestMethod.POST, consumes = "application/json", produces = "application/json")
//	public ResponseEntity<HashMap<String,String>> deleteTestFacultyAnswersAllocation(@RequestBody StudentQuestionResponseBean allocation){
//
//		HttpHeaders headers = new HttpHeaders();
//		headers.add("Content-Type", "application/json");
//		TestDAO dao = (TestDAO)act.getBean("testDao");
//		HashMap<String,String> response = new HashMap<>();
//
//		boolean clearOldAllocations = dao.clearFacultyAllocation(allocation);
//		Integer noOfAllocationsLeft = dao.getNoOfAnswersNotAllocated(allocation.getTestId());
//		response.put("noOfAllocationsLeft", noOfAllocationsLeft.toString());
//
//		if(!clearOldAllocations) {
//			response.put("Status", "Fail");
//			return new ResponseEntity<>(response,headers, HttpStatus.INTERNAL_SERVER_ERROR);
//		}
//		else {	
//			response.put("Status", "Success");
//			return new ResponseEntity<>(response,headers, HttpStatus.OK);
//
//		}
//	}
//	//end

	@RequestMapping(value = "/addTestQuestionsForFacultyForm", method =  RequestMethod.GET)
	public String addTestQuestionsForFacultyForm(HttpServletRequest request, HttpServletResponse response, Model m,
			@RequestParam("id") Long id) {

		/*if(!checkSession(request, response)){
			//return new ModelAndView("studentPortalRediret");
			redirectToPortalApp(response);
			return null;
		}*/
		
		TestDAO dao = (TestDAO)act.getBean("testDao");
		TestExamBean test = dao.getTestById(id);
		List<TestQuestionExamBean> testQuestions = dao.getTestQuestions(id);
		m.addAttribute("test", test);
		m.addAttribute("testQuestions", testQuestions);

		HashMap<Long,TestTypeBean> typeIdBeanMap= getTypeIdNBeanMap();

		for(TestQuestionExamBean q : testQuestions) {
			try {
				q.setTypeInString(typeIdBeanMap.get(new Long(q.getType())).getType());
			} catch (Exception e) {
				// TODO Auto-generated catch block
				
			}
		}
		List<SectionBean> sectionList = iaTestService.getApplicableSectionList(id);
		m.addAttribute("sectionList", sectionList);
		
		m.addAttribute("typeIdBeanMap", typeIdBeanMap);
		
		boolean canCurrentUserUpdateQuestions = canCurrentUserUpdateQuestions(request,test);
		
		m.addAttribute("canCurrentUserUpdateQuestions",canCurrentUserUpdateQuestions);
		
		return "test/addTestQuestionsForFacultyForm";
	}
	
	public boolean canCurrentUserUpdateQuestions(HttpServletRequest request, TestExamBean test) {

		TestDAO dao = (TestDAO)act.getBean("testDao");
		Person currentUser = (Person) request.getSession().getAttribute("user");
		
		String roles = currentUser!=null ? currentUser.getRoles() : "";
		boolean isTestlive = (dao.getLiveTestByModuleId(test.getReferenceId())>0?true:false);
		
		if(roles.indexOf("Insofe") == -1){
			return true;
		}else {
			if(isTestlive) {
				return false;
			}else {
				return true;
			}
		}
		
	}

//	//saveSingleTestQusetion start
//	@RequestMapping(value = "/m/saveSingleTestQuestion", method = RequestMethod.POST, consumes = "application/json", produces = "application/json")
//	public ResponseEntity<HashMap<String,String>> saveSingleTestQusetion(@RequestBody TestQuestionBean question){
//
//		for(TestQuestionOptionBean o : question.getOptionsList()) {
//		}
//		HttpHeaders headers = new HttpHeaders();
//		headers.add("Content-Type", "application/json");
//		TestDAO dao = (TestDAO)act.getBean("testDao");
//		HashMap<String,String> response = new HashMap<>();
//
//		//Escape HTML characters Start
//		question = escapeHTMLFromString(question);
//		if(!StringUtils.isBlank(question.getErrorMessage())) {
//			response.put("Status", "Failed");
//			response.put("errroMessage", question.getErrorMessage());
//			return new ResponseEntity<>(response,headers, HttpStatus.INTERNAL_SERVER_ERROR);
//		}
//		//END
//
//		String errorMessage = dao.addSingleTestQuestion(question);
//		try{
//			Integer returnedKey = Integer.parseInt(errorMessage.trim());
//			response.put("mainQuestionId", ""+returnedKey);
//			response.put("Status", "Success");
//			
//			setTestQuestionsInRedisByTestId(question.getTestId());
//			
//			return new ResponseEntity<>(response,headers, HttpStatus.OK);	
//		}catch(Exception e){
//			//
//			response.put("Status", "Failed");
//			response.put("errroMessage", errorMessage);
//			return new ResponseEntity<>(response,headers, HttpStatus.INTERNAL_SERVER_ERROR);
//
//		}
//
//
//	}
//	//end

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
	

//	//updateTestQusetion start
//	@RequestMapping(value = "/m/updateTestQusetion", method = RequestMethod.POST, consumes = "application/json", produces = "application/json")
//	public ResponseEntity<HashMap<String,String>> updateTestQusetion(@RequestBody TestQuestionBean question){
//
//		HttpHeaders headers = new HttpHeaders();
//		headers.add("Content-Type", "application/json");
//		TestDAO dao = (TestDAO)act.getBean("testDao");
//		HashMap<String,String> response = new HashMap<>();
//
//		String errorMessage = dao.updateTestQusetion(question);
//		if(StringUtils.isBlank(errorMessage)){
//			response.put("Status", "Success");
//			TestQuestionBean questionForTestId = dao.getTestQuestionById(question.getId());
//			setTestQuestionsInRedisByTestId(questionForTestId.getTestId());
//			return new ResponseEntity<>(response,headers, HttpStatus.OK);	
//		}else{
//			response.put("Status", "Failed");
//			response.put("errroMessage", errorMessage);
//			return new ResponseEntity<>(response,headers, HttpStatus.INTERNAL_SERVER_ERROR);
//
//		}
//
//	}
//	//end
//
//	//deleteTestQusetion start
//	@RequestMapping(value = "/m/deleteTestQusetion", method = RequestMethod.POST, consumes = "application/json", produces = "application/json")
//	public ResponseEntity<HashMap<String,String>> deleteTestQusetion(@RequestBody TestQuestionBean question){
//
//		HttpHeaders headers = new HttpHeaders();
//		headers.add("Content-Type", "application/json");
//		TestDAO dao = (TestDAO)act.getBean("testDao");
//		HashMap<String,String> response = new HashMap<>();
//
//		TestQuestionBean questionForTestId = dao.getTestQuestionById(question.getId());
//		
//		String errorMessage = dao.deleteTestQuestionById(question.getId());
//
//		if("Error".equalsIgnoreCase(errorMessage)) {
//			response.put("Status", "Failed");
//			response.put("errroMessage", "Error in deleting question.");
//			return new ResponseEntity<>(response,headers, HttpStatus.INTERNAL_SERVER_ERROR);
//		}
//		else {
//			response.put("Status", "Success");
//			response.put("successMessage", errorMessage);
//			setTestQuestionsInRedisByTestId(questionForTestId.getTestId());
//			
//			return new ResponseEntity<>(response,headers, HttpStatus.OK);	
//		}
//
//
//	}
//	//end

	@RequestMapping(value = "/viewTestsForFaculty", method =  RequestMethod.GET)
	public String viewTestsForFaculty(HttpServletRequest request, HttpServletResponse response, Model m) {
		String userId = (String) request.getSession().getAttribute("userId");
		
		if(!checkSession(request, response)){
			//return new ModelAndView("studentPortalRediret");
			redirectToPortalApp(response);
			return null;
		}
		
		
		TestDAO dao = (TestDAO)act.getBean("testDao");
		List<TestExamBean> allTests = dao.getTestsForFaculty(userId);
		int getNoOfTestsForFaculty = dao.getNoOfTestsForFaculty(userId);
		m.addAttribute("allTests", allTests);
		m.addAttribute("getNoOfTestsForFaculty", getNoOfTestsForFaculty);
		m.addAttribute("subjectList", getSubjectList());
		m.addAttribute("programList", getProgramList());
		m.addAttribute("test", new TestExamBean());
		return "test/viewTestsForFaculty";
	}



//	//FileBean
//	//Upload image for question start
//	@RequestMapping(value = "/m/uploadTestQuestionImage", method = RequestMethod.POST,  produces = "application/json")
//	public ResponseEntity<HashMap<String,String>> uploadTestQuestionImage(MultipartHttpServletRequest request){
//
//		HttpHeaders headers = new HttpHeaders();
//		headers.add("Content-Type", "application/json");
//		TestDAO dao = (TestDAO)act.getBean("testDao");
//
//		HashMap<String,String> response = new HashMap<>();
//
//		String errorMessage = "";
//		String returnLink="";
//		Iterator<String> it = request.getFileNames();
//		while (it.hasNext()) {
//			String uploadFile = it.next();
//			MultipartFile file = request.getFile(uploadFile);
//			returnLink= uploadTestQuestionImageToServer(file);
//		}
//		//MultipartFile multipartFile = request.getFile("user-file");
//
//		//String name = multipartFile.getOriginalFilename();
//
//		//imageBean = uploadTestQuestionImageToServer(imageBean);
//		//errorMessage = imageBean.getErrorMessage();  
//		if(StringUtils.isBlank(returnLink)) {
//			errorMessage ="Error in saving file to DB.";
//		}
//		if("Error".equalsIgnoreCase(errorMessage)) {
//			response.put("Status", "Failed");
//			response.put("errroMessage", "Error in deleting question.");
//			return new ResponseEntity<>(response,headers, HttpStatus.INTERNAL_SERVER_ERROR);
//		}
//		else {
//			response.put("Status", "Success");
//			response.put("imageUrl", returnLink);
//			response.put("successMessage", errorMessage);
//			return new ResponseEntity<>(response,headers, HttpStatus.OK);	
//		}
//
//
//	}
	//Upload image for question end

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


	@RequestMapping(value = "/makeTestLiveForm", method = RequestMethod.GET)
	public String makeTestLiveForm(HttpServletRequest request, HttpServletResponse response, Model m) {

		TestDAO dao = (TestDAO)act.getBean("testDao");


		m.addAttribute("searchBean", new TestExamBean());	
		m.addAttribute("yearList", currentYearList);
		m.addAttribute("testsLiveConfigList", dao.getTestsLiveConfigList());
		m.addAttribute("acadsYearList", ACAD_YEAR_LIST);
		m.addAttribute("acadsMonthList", ACAD_MONTH_LIST);
		m.addAttribute("consumerType", getConsumerTypeList());
		m.addAttribute("subjectList", getSubjectList());

		return "test/makeTestLiveForm";
	}
	
	@RequestMapping(value = "/testNotLive", method = RequestMethod.GET)
	public String TestNotLive(HttpServletRequest request, HttpServletResponse response, Model m) {

		TestDAO dao = (TestDAO)act.getBean("testDao");
		
		m.addAttribute("testsNotLive", dao.getTestsNotLive());
		
		return "test/testNotLive";
	}

	
	@RequestMapping(value = "/saveTestLiveConfig",  method = RequestMethod.POST)
	public String saveTestLiveConfig(HttpServletRequest request,
									 HttpServletResponse response, 
									 @ModelAttribute TestExamBean  searchBean,
									 Model m) {
		
		
		String userId = (String) request.getSession().getAttribute("userId");
		searchBean.setCreatedBy(userId);
		searchBean.setLastModifiedBy(userId);
		
		//String errorMessage = dao.batchInsertOfMakeTestsLiveConfigs(searchBean,consumerProgramStructureIds);
		TestDAO dao = (TestDAO)act.getBean("testDao");
		String errorMessage = "";
		String errorMessageForConfigCheck = "";
		
		ArrayList<String> previewPendigForTests = new ArrayList<String>();
		try{
//			previewPendigForTests = dao.checkIfTestPreviewedByFaculty(searchBean);
//			new logic
			ArrayList<TestExamBean> testsToLive = dao.getTestsToMakeLiveBySearchBean(searchBean);
			String errorMsg = dao.checkIfQuestionsConfigured(testsToLive);
			
			if(!errorMsg.equalsIgnoreCase("")) {
				request.setAttribute("error", "true");
				request.setAttribute("errorMessage", errorMsg);
				m.addAttribute("searchBean", searchBean);	
				return makeTestLiveForm(request, response, m);
			}
			previewPendigForTests = dao.checkIfTestPreviewedByFacultyByQuestions(testsToLive,request);
			
			try{
				if(request.getAttribute("error").equals("true")) {
					m.addAttribute("searchBean", searchBean);	
					return makeTestLiveForm(request, response, m);
				}
			}catch(Exception e) {}
			//check for question configuration erros
		}
		catch(NullPointerException e) {
			
			errorMessage = e.getMessage();
		}
		catch(Exception e) {
			e.printStackTrace();
			errorMessage = "Error in fetching preview tests.";
		}
		
		try {
//			temporarily removing config check
//			errorMessageForConfigCheck = dao.checkIfQuestionsAndQuestionConfigMatches(searchBean);
		}catch(Exception e) {
			
			errorMessage = "Error in fetching configuration for tests.";
		}
		if(StringUtils.isBlank(errorMessage)) {
			if(previewPendigForTests.size() > 0) {
				if(StringUtils.isBlank(errorMessageForConfigCheck))
					

					errorMessage = "Kindly Preview Tests " + previewPendigForTests + " before Current Config can be made live.";
				else
					errorMessage = errorMessageForConfigCheck + "<br/> Kindly Preview Tests " + previewPendigForTests + " before Current Config can be made live.";
			}
			else if(previewPendigForTests.size() == 0 && !StringUtils.isBlank(errorMessageForConfigCheck)) {
				errorMessage = errorMessageForConfigCheck ;
			}
			else {
				errorMessage = insertMakeTestsLiveConfigs(searchBean);
			}
		}
		

		if(StringUtils.isBlank(errorMessage)) {
			request.setAttribute("success","true");
			request.setAttribute("successMessage", " Test is Live for "+ 
					": Type : "+searchBean.getLiveType()+  
					"; ApplicableType : "+searchBean.getApplicableType()+
					"; acadsMonth : "+searchBean.getAcadMonth()+ 
					"; acadsYear : "+searchBean.getAcadYear()+ 
					"; examMonth : "+searchBean.getExamMonth()+
					"; examYear : "+searchBean.getExamYear());
			m.addAttribute("searchBean", searchBean);	

			ArrayList<TestExamBean> testsBySearchBean = dao.getTestsByExamYearMonthAcadsYearMonthReferenceId(searchBean);
			
			for(TestExamBean test : testsBySearchBean ) {
				setTestQuestionsInRedisByTestId(test.getId());
			}
			return makeTestLiveForm(request, response, m);
		}else {
			request.setAttribute("error", "true");
			request.setAttribute("errorMessage", errorMessage);
			m.addAttribute("searchBean", searchBean);	
			return makeTestLiveForm(request, response, m);
		}
	}

	@RequestMapping(value = "/deleteTestLiveConfig",  method = RequestMethod.POST)
	public String deleteTestLiveConfig(HttpServletRequest request,
									 HttpServletResponse response, 
									 @ModelAttribute TestExamBean  searchBean,
									 Model m) {
		
		
		String userId = (String) request.getSession().getAttribute("userId");
		searchBean.setCreatedBy(userId);
		searchBean.setLastModifiedBy(userId);
		
		//String errorMessage = dao.batchInsertOfMakeTestsLiveConfigs(searchBean,consumerProgramStructureIds);
		
		String errorMessage = deleteTestsLiveConfigs(searchBean);
		
		if(StringUtils.isNumeric(errorMessage)) {
			request.setAttribute("success","true");
			request.setAttribute("successMessage", " Diabled Live Config for "+ 
					": Type : "+searchBean.getLiveType()+  
					"; ApplicableType : "+searchBean.getApplicableType()+
					"; Subject : "+searchBean.getSubject()+
					"; acadsMonth : "+searchBean.getAcadMonth()+ 
					"; acadsYear : "+searchBean.getAcadYear()+ 
					"; examMonth : "+searchBean.getExamMonth()+
					"; examYear : "+searchBean.getExamYear());
			m.addAttribute("searchBean", searchBean);	
			return makeTestLiveForm(request, response, m);
		}else {
			request.setAttribute("error", "true");
			request.setAttribute("errorMessage", errorMessage);
			m.addAttribute("searchBean", searchBean);	
			return makeTestLiveForm(request, response, m);
		}
	}

	private String deleteTestsLiveConfigs(TestExamBean searchBean) {
		
		TestDAO dao = (TestDAO)act.getBean("testDao");
		List<Long> configIds = getConfigIdsForTestLiveSettings(dao,searchBean);
		
		return dao.deleteTestLiveConfig(searchBean,configIds);
	}

	private String insertMakeTestsLiveConfigs(TestExamBean test) {
		TestDAO dao = (TestDAO)act.getBean("testDao");
		
		List<Long> configIds = getConfigIdsForTestLiveSettings(dao,test);
		return dao.insertTestLiveConfig(test,configIds);
	}



	private List<Long> getConfigIdsForTestLiveSettings(TestDAO dao, TestExamBean test) {
		
		List<Long> returnEmptyList = new ArrayList<>();
		
		
		if("module".equalsIgnoreCase(test.getApplicableType()) ) {
			
			List<Long> moduleIds =new ArrayList<Long>();
			moduleIds.add((long) test.getReferenceId());
			/*
			List<Long> moduleIds = dao.getModuleIdByProgramConfigYearMonthBatchIdNId(
					 test.getConsumerTypeId()
					 ,test.getProgramStructureId()
					 ,test.getProgramId()
					 ,test.getAcadYear()
					 ,test.getAcadMonth()
					 ,test.getReferenceId()
					 ,test.getSubject()
					 ,test.getModuleBatchId() 
					);
             */
			//return dao.insertTestIdNConfigurationMappings(test,moduleIds);
			return moduleIds;
		}else if("batch".equalsIgnoreCase(test.getApplicableType())) {
				//1. Get programSemSubjectIds
				ArrayList<String> programSemSubjectIds =
				dao.getProgramSemSubjectIdsBySubjectNProgramConfig(test.getProgramId()
						 ,test.getProgramStructureId()
						 ,test.getConsumerTypeId()
						 ,test.getSubject());
				if(programSemSubjectIds.isEmpty()) {
					return returnEmptyList;
//>>>>>>> branch 'master' of https://ngasce@bitbucket.org/ngasceteam/exam.git
				}
				
				//2. get timeboundIds by programSemSubjectIds
				List<Long> timeboundIds = 
					dao.getTimeboundIdsByProgramSemSubjectIdsNBatchId(programSemSubjectIds,test.getAcadYear(),test.getAcadMonth(),test.getReferenceId());
				
				if(timeboundIds.isEmpty()) {
					return returnEmptyList;
				}
				
				

				return timeboundIds;
				
			}else if("old".equalsIgnoreCase(test.getApplicableType())) {
				ArrayList<String> programSemSubjectIds =
						dao.getProgramSemSubjectIdsBySubjectNProgramConfig(test.getProgramId()
								 ,test.getProgramStructureId()
								 ,test.getConsumerTypeId()
								 ,test.getSubject());
				
						if(programSemSubjectIds.isEmpty()) {
							return returnEmptyList;
						}
						
						ArrayList<String> programSemSubjectIdsFromTimeboundIdTable =
								dao.getProgramSemSubjectIdsInTimeboundIdTable();
						
						
						programSemSubjectIds.removeAll(programSemSubjectIdsFromTimeboundIdTable);

						
						//convert programSemSubjectIds to array of long id to be used in common insert method
						List<Long> programSemSubjectIdsToLong = new ArrayList<>();
						for( String id : programSemSubjectIds) {
							
							try {
								programSemSubjectIdsToLong.add(Long.parseLong(id));
							} catch (NumberFormatException e) {
								return returnEmptyList;
							}
							
						}
					
						return programSemSubjectIdsToLong;
						
			

			}else {
				return returnEmptyList;
				
			}
	}

	public String createTestLiveSettingsEnteries(TestExamBean bean) {

		/* to be changes later
					TestDAO dao = (TestDAO)act.getBean("testDao");



					if("module".equalsIgnoreCase(test.getApplicableType()) ) {
						List<Long> temp = new ArrayList<>();
						temp.add((long)0);
						return dao.insertTestIdNConfigurationMappings(test,temp);
					}else if("batch".equalsIgnoreCase(test.getApplicableType())) {
						//1. Get programSemSubjectIds
						ArrayList<String> programSemSubjectIds =
						dao.getProgramSemSubjectIdsBySubjectNProgramConfig(test.getProgramId()
								 ,test.getProgramStructureId()
								 ,test.getConsumerTypeId()
								 ,test.getSubject());
						if(programSemSubjectIds.isEmpty()) {
							return " Error in getting programSemSubjectIds. ";
						}

						//2. get timeboundIds by programSemSubjectIds
						List<Long> timeboundIds = 
							dao.getTimeboundIdsByProgramSemSubjectIdsNBatchId(programSemSubjectIds,test.getAcadYear(),test.getAcadMonth(),test.getReferenceId());

						if(timeboundIds.isEmpty()) {
							return " Error in getting timeboundIds. ";
						}

						return dao.insertTestIdNConfigurationMappings(test,timeboundIds);

					}else if("old".equalsIgnoreCase(test.getApplicableType())) {
						ArrayList<String> programSemSubjectIds =
								dao.getProgramSemSubjectIdsBySubjectNProgramConfig(test.getProgramId()
										 ,test.getProgramStructureId()
										 ,test.getConsumerTypeId()
										 ,test.getSubject());

								if(programSemSubjectIds.isEmpty()) {
									return " Error in getting programSemSubjectIds. ";
								}

								//convert programSemSubjectIds to array of long id to be used in common insert method
								List<Long> programSemSubjectIdsToLong = new ArrayList<>();
								for( String id : programSemSubjectIds) {

									try {
										programSemSubjectIdsToLong.add(Long.parseLong(id));
									} catch (NumberFormatException e) {
										return "Error in converting programSemSubjectIds to Long Array : ";
									}

								}

								return dao.insertTestIdNConfigurationMappings(test,programSemSubjectIdsToLong);

						}else {
							return "Unknown applicable type : "+test.getApplicableType();
					}


		 */
		return null;
	}


	@RequestMapping(value = "/searchTests", method =  RequestMethod.POST)
	public String searchTests(HttpServletRequest request,	
			HttpServletResponse response,
			Model m,
			@ModelAttribute TestExamBean searchBean) {

		/*if(!checkSession(request, response)){
						return "studentPortalRediret";
					}*/
		TestDAO dao = (TestDAO)act.getBean("testDao");
		List<TestExamBean> allTests = dao.getTestsBySearchBean(searchBean);
		m.addAttribute("allTests", addConsumerProgramProgramStructureNameToEachTest(dao,allTests));

		return viewAllTests("false", request, response, m);
	}

	@RequestMapping(value = "/getProgramsListForCommonTest", method = {RequestMethod.POST})
	public ResponseEntity<List<TestExamBean>> getProgramsListForCommonTest(@RequestBody TestExamBean test) {
		TestDAO dao = (TestDAO)act.getBean("testDao");

		return new ResponseEntity<List<TestExamBean>>(dao.getProgramsListForCommonTest(test.getId()),HttpStatus.OK);
	}

	@RequestMapping(value = "/editTestFromSearchTestsForm", method =  RequestMethod.GET)
	public String editTestFromSearchTestsForm(HttpServletRequest request,
			HttpServletResponse response,
			Model m,
			@RequestParam("testId") Long id,
			@RequestParam("testConfigIds") String testConfigIds,
			@RequestParam("consumerProgramStructureId") Integer consumerProgramStructureId)  {



		TestDAO dao = (TestDAO)act.getBean("testDao");
		TestExamBean test = dao.getTestByIdNConsumerProgramStructureId(id,consumerProgramStructureId);
		if(test.getId() == null) {
			request.setAttribute("error", "true");
			request.setAttribute("errorMessage", "No data for selected testid : "+id+" consumerProgramStructureId : "+consumerProgramStructureId);
			return viewAllTests("false", request, response, m);
		}
		m.addAttribute("subjectList", getSubjectList());
		m.addAttribute("programList", getProgramList());
		test.setId(null);
		test.setTestId(id);
		test.setConsumerProgramStructureId(consumerProgramStructureId);
		m.addAttribute("test", test);
		m.addAttribute("testConfigIds", testConfigIds);
		m.addAttribute("facultyList", getFacultyList());
		m.addAttribute("facultyList", getFacultyList());

		m.addAttribute("consumerType", getConsumerTypeList());
		String errorMessageCheck ="";
		try {
			errorMessageCheck = (String)request.getAttribute("errorMessage");
		} catch (Exception e) {}

		String errorMessage = "Please Once Again Select ConsumerType : <b>"+test.getConsumerType()
		+"</b><br> ProgramStructure : <b>"+test.getProgramStructure()
		+"</b><br> Program : <b>"+test.getProgram()
		+"</b><br> Subject : <b>"+test.getSubject()+"</b>";
		if(StringUtils.isBlank(errorMessageCheck)) {

			request.setAttribute("errorMessage", errorMessage);
		}else {

			request.setAttribute("errorMessage", errorMessageCheck+".<br>"+errorMessage);
		}
		request.setAttribute("error", "true");

		return "test/addTestFromModuleId";
	}

	@RequestMapping(value = "/addTestForConfigEdit", method = RequestMethod.POST)
	public RedirectView addTestForConfigEdit(HttpServletRequest request,
			HttpServletResponse response,
			@ModelAttribute TestExamBean test,RedirectAttributes redirectAttrs,
			Model m) {

		String userId = (String) request.getSession().getAttribute("userId");

		if(!( test.getTestConfigIds().equalsIgnoreCase(test.getConsumerTypeId()
				+"~"+test.getProgramStructureId()
				+"~"+test.getProgramId()
				+"~"+test.getSubject().replaceAll("&", "and")
				)
				)) {

			redirectAttrs.addFlashAttribute("error", "true"); 
			redirectAttrs.addFlashAttribute("errorMessage", "Saved Test Config : "+test.getTestConfigIds()
			+" does not match with updated values : "+test.getConsumerTypeId()
			+"~"+test.getProgramStructureId()
			+"~"+test.getProgramId()
			+"~"+test.getSubject().replaceAll("&", "and"));

			return new RedirectView("editTestFromSearchTestsForm?testId="+test.getTestId()+"&testConfigIds="
					+test.getTestConfigIds()+"&consumerProgramStructureId="+test.getConsumerProgramStructureId()
			);
		}

		TestDAO dao = (TestDAO)act.getBean("testDao");

		int deletedRows = dao.deleteTestIdNConsumerProgramStructureIdMapping(test.getTestId(),test.getConsumerProgramStructureId());

		if(deletedRows == 1 ) {
			return new RedirectView("addTestForm");
		}else {
			redirectAttrs.addFlashAttribute("error", "true");
			redirectAttrs.addFlashAttribute("errorMessage", "Error in deleting saved mapping, Deleted rows : "+deletedRows);

			redirectAttrs.addFlashAttribute("testId", test.getTestId());
			redirectAttrs.addFlashAttribute("testConfigIds", test.getTestConfigIds());
			redirectAttrs.addFlashAttribute("consumerProgramStructureId", test.getConsumerProgramStructureId());
			return new RedirectView("editTestFromSearchTestsForm");
		}

	}

	//getBatchDataByMasterKeyConfig start

	@RequestMapping(value = "/api/getBatchDataByMasterKeyConfig", method = {RequestMethod.POST})
	public ResponseEntity<ResponseBean> getBatchDataByMasterKeyConfig(@RequestBody TestExamBean test) {

		ResponseBean responseBean = getBatchDataDetailsByMasterKeyConfig(
				test.getConsumerTypeId(),
				test.getProgramStructureId(),
				test.getProgramId(),
				test.getSubject(),
				test.getAcadYear(),
				test.getAcadMonth()
				);

		return new ResponseEntity<ResponseBean>(responseBean,HttpStatus.OK);
	}

	private ResponseBean getBatchDataDetailsByMasterKeyConfig(String consumerTypeId,
			String programStructureId, String programId, String subject, Integer acadYear, String acadMonth) {

		ResponseBean responseBean = new ResponseBean();
		TestDAO dao = (TestDAO)act.getBean("testDao");

		//1. Get programSemSubjectIds
		ArrayList<String> programSemSubjectIds =
				dao.getProgramSemSubjectIdsBySubjectNProgramConfig(programId
						,programStructureId
						,consumerTypeId
						,subject);
		if(programSemSubjectIds.isEmpty()) {
			return responseBean;
		}

		//2. get timeboundIds by programSemSubjectIds
		List<Long> timeboundIds = 
				dao.getTimeboundIdsByProgramSemSubjectIds(programSemSubjectIds,acadYear,acadMonth);

		if(timeboundIds.isEmpty()) {
			return responseBean;
		}


		List<TestExamBean> batchDetailsByMasterkeys = dao.getBatchDetailsByMasterkeys(timeboundIds,acadYear,acadMonth);

		responseBean.setDataForReferenceId(batchDetailsByMasterkeys);

		List<String> subjectsList = dao.getSubjectsListByMastKeyAndBatch(consumerTypeId, programStructureId,
				programId,  acadYear,  acadMonth,0);// Pass "0" here to get all subjects for config

		responseBean.setListOfStringData(subjectsList);


		return responseBean;
	}
	//getBatchDataByMasterKeyConfig end


	//getModuleDataByMasterKeyConfig start

	@RequestMapping(value = "/api/getModuleDataByMasterKeyConfig", method = {RequestMethod.POST})
	public ResponseEntity<ResponseBean> getModuleDataByMasterKeyConfig(@RequestBody TestExamBean test) {

		ResponseBean responseBean = getModuleDataDetailsByMasterKeyConfig(
				test.getConsumerTypeId(),
				test.getProgramStructureId(),
				test.getProgramId(),
				test.getSubject(),
				test.getAcadYear(),
				test.getAcadMonth(),
				test.getReferenceId()
				);

		return new ResponseEntity<ResponseBean>(responseBean,HttpStatus.OK);
	}

	private ResponseBean getModuleDataDetailsByMasterKeyConfig(String consumerTypeId,
			String programStructureId, String programId, String subject, Integer acadYear, String acadMonth, Integer batchId) {

		ResponseBean responseBean = new ResponseBean();
		TestDAO dao = (TestDAO)act.getBean("testDao");

		//1. Get programSemSubjectIds
		ArrayList<String> programSemSubjectIds =
				dao.getProgramSemSubjectIdsBySubjectNProgramConfig(programId
						,programStructureId
						,consumerTypeId
						,subject);
		if(programSemSubjectIds.isEmpty()) {
			return responseBean;
		}

		//2. get timeboundIds by programSemSubjectIds
		List<Long> timeboundIds = 
				dao.getTimeboundIdsByProgramSemSubjectIds(programSemSubjectIds,acadYear,acadMonth);

		if(timeboundIds.isEmpty()) {
			return responseBean;
		}



		List<TestExamBean> moduleDetailsByMasterkeys = dao.getModulesDetailsByMasterkeys(timeboundIds,batchId);

		responseBean.setDataForReferenceId(moduleDetailsByMasterkeys);


		return responseBean;
	}
	//getBatchDataByMasterKeyConfig end

	//getSubjectsByMastKeyAndBatch start
	@RequestMapping(value = "/api/getSubjectsByMastKeyAndBatch", method = {RequestMethod.POST})
	public ResponseEntity<ResponseBean> getSubjectsByMastKeyAndBatch(@RequestBody TestExamBean test) {

		ResponseBean responseBean = getSubjectsListByMastKeyAndBatch(
				test.getConsumerTypeId(),
				test.getProgramStructureId(),
				test.getProgramId(),
				test.getAcadYear(),
				test.getAcadMonth(),
				test.getReferenceId()
				);

		return new ResponseEntity<ResponseBean>(responseBean,HttpStatus.OK);
	}
	//getSubjectsByMastKeyAndBatch end

	private ResponseBean getSubjectsListByMastKeyAndBatch(String consumerTypeId, String programStructureId,
			String programId, Integer acadYear, String acadMonth, Integer referenceId) {

		TestDAO dao = (TestDAO)act.getBean("testDao");
		ResponseBean responseBean = new ResponseBean();
		List<String> subjectsList = dao.getSubjectsListByMastKeyAndBatch(consumerTypeId, programStructureId,
				programId,  acadYear,  acadMonth,referenceId);

		responseBean.setListOfStringData(subjectsList);
		return responseBean;
	}


//	@RequestMapping(value = "/m/checkIfAnyTestsActive", method = RequestMethod.POST )
//	public ResponseEntity<Map<String, Object>> checkIfAnyTestsActive(HttpServletRequest request, @RequestBody StudentBean inputObj ){
//		Map<String, Object> returnObject = new HashMap<String, Object>();
//		HttpHeaders headers = new HttpHeaders();
//		headers.add("Content-Type", "application/json");
//
//		String sapid = inputObj.getSapid();
//
//		boolean testsActive = false;
//		try {
//
//			TestDAO dao = (TestDAO)act.getBean("testDao");
//			if(dao.checkIfAnyTestsActive(sapid)) {
//				testsActive = true;
//			}
//		}catch (Exception e) {
//			
//			returnObject.put("error", e.getMessage());
//		}
//		returnObject.put("testsActive", testsActive);
//
//		return new ResponseEntity<Map<String, Object>>(returnObject, headers, HttpStatus.OK);
//	}
	@RequestMapping(value = "/extendedTestsStartEndTimeBySapidForm", method =  RequestMethod.GET)
	public String extendedTestsStartEndTimeBySapidForm(HttpServletRequest request,
			HttpServletResponse response,
			Model m,
			@RequestParam("id") Long id)  {

		TestDAO dao = (TestDAO)act.getBean("testDao");
		TestExamBean test = dao.getTestById(id);
		test = setProgramConfigNamesForSingleTest(test);

		List<TestExamBean> extendedList =  dao.getExtendedStartEndTimeSapidListByTestId(id);

		m.addAttribute("test", test);
		m.addAttribute("bean", new TestExamBean());
		m.addAttribute("extendedList", extendedList);
		return "test/extendedTestsStartEndTimeBySapidForm";
	}


	@RequestMapping(value = "/extendedTestsStartEndTimeBySapid", method = RequestMethod.POST)
	public String extendedTestsStartEndTimeBySapid(HttpServletRequest request,
			HttpServletResponse response,
			@ModelAttribute TestExamBean test,
			Model m) {

		String userId = (String) request.getSession().getAttribute("userId");

		String est= test.getExtendedStartTime().replaceAll("T", " ");
		String eet= test.getExtendedEndTime().replaceAll("T", " ");

		test.setExtendedStartTime(est+":00"); // added +":00" as was not coming from page by PS
		test.setExtendedEndTime(eet+":00");

		List<String> sapids = generateListFromSapidInNextLine(test.getSapidList());

		TestDAO dao = (TestDAO)act.getBean("testDao");

		String error = auditDao.batchInsertExtendedTestTime(sapids,test);
		if(StringUtils.isBlank(error)) {
			request.setAttribute("success","true");
			request.setAttribute("successMessage", "Extension Added.");
		}else {
			request.setAttribute("error", "true");
			request.setAttribute("errorMessage", error);
		}
		return extendedTestsStartEndTimeBySapidForm(request, response, m, test.getId());
	}

	private List<String> generateListFromSapidInNextLine(String sapIdList) {
		String commaSeparatedList = sapIdList.replace("(\\r|\\n|\\r\\n)+", ",");
		/*
		 * if(commaSeparatedList.endsWith(",")){ commaSeparatedList =
		 * commaSeparatedList.substring(0, commaSeparatedList.length()-1); }
		 */

		List<String> sapids = Arrays.asList(sapIdList.split("\\s*,\\s*"));
		return sapids;
	}

	@RequestMapping(value = "/deleteExtendedTestsTimeBySapidNTestId",  method = RequestMethod.GET)
	public String deleteExtendedTestsTimeBySapidNTestId(HttpServletRequest request,
			HttpServletResponse response, 
			@RequestParam("testId") Long testId,
			@RequestParam("sapid") String sapid,
			Model m) {

		TestDAO dao = (TestDAO)act.getBean("testDao");

		String errorMessage = ""+dao.deleteExtendedTestTmeByTestIdNSapid(testId, sapid);
		if(StringUtils.isNumeric(errorMessage)) {
			request.setAttribute("success","true");
			request.setAttribute("successMessage", "Deleted Extension");
		}else {
			request.setAttribute("error", "true");
			request.setAttribute("errorMessage", errorMessage);
		}

		return extendedTestsStartEndTimeBySapidForm(request, response, m, testId);
	}


	@RequestMapping(value = "/TestDetailsReport", method =  RequestMethod.GET)
	public ModelAndView downloadtestinexcel(HttpServletRequest request,
			HttpServletResponse response,
			Model m,
			@RequestParam("id") Long id) throws Exception  {

		TestDAO dao = (TestDAO) act.getBean("testDao");
		if(!checkSession(request, response)){
			//return new ModelAndView("studentPortalRediret");
			redirectToPortalApp(response);
			return null;
		}


		List<StudentsTestDetailsExamBean> testDetailsList = dao.getTestResultsById(id);


		return new ModelAndView("testResultsExcelView","testDetailsList",testDetailsList);

	}

	@RequestMapping(value = "/TestAttendenceDetailsReport", method =  RequestMethod.GET)
	public ModelAndView downloadTestAttendenceInExcel(HttpServletRequest request,
			HttpServletResponse response,
			Model m,
			@RequestParam("id") Long id) throws Exception  {

		TestDAO dao = (TestDAO) act.getBean("testDao");
		if(!checkSession(request, response)){
			//return new ModelAndView("studentPortalRediret");
			redirectToPortalApp(response);
			return null;
		}


		List<StudentsTestDetailsExamBean> testAttendenceDetailsList = dao.getTestAttendenceResultsById(id);


		return new ModelAndView("testAttendenceExcelView","testAttendenceDetailsList",testAttendenceDetailsList);

	}

	@RequestMapping(value = "/consolidatedStudentsWiseReportsForm", method = RequestMethod.GET)
	public String consolidatedStudentsWiseReportsForm(HttpServletRequest request, HttpServletResponse response, Model m) {

		TestDAO dao = (TestDAO)act.getBean("testDao");
		if(!checkSession(request, response)){
			//return new ModelAndView("studentPortalRediret");
			redirectToPortalApp(response);
			return null;
		}


		m.addAttribute("searchBean", new TestExamBean());	
		//	m.addAttribute("yearList", currentYearList);
		//	m.addAttribute("testsLiveConfigList", dao.getTestsLiveConfigList());
		//	m.addAttribute("acadsYearList", ACAD_YEAR_LIST);
		//	m.addAttribute("acadsMonthList", ACAD_MONTH_LIST);
		//	m.addAttribute("consumerType", getConsumerTypeList());
		//	m.addAttribute("subjectList", getSubjectList());

		return "test/consolidatedStudentsWiseReportsForm";
	}

	@RequestMapping(value = "/consolidatedStudentsWiseReports",  method = RequestMethod.POST)
	public ModelAndView consolidatedStudentsWiseReports(HttpServletRequest request,
			HttpServletResponse response, 
			@ModelAttribute TestExamBean  searchBean,
			Model m) {
		if(!checkSession(request, response)){
			//return new ModelAndView("studentPortalRediret");
			redirectToPortalApp(response);
			return null;
		}

		String userId = (String) request.getSession().getAttribute("userId");
		searchBean.setCreatedBy(userId);
		searchBean.setLastModifiedBy(userId);


		List<StudentsTestDetailsExamBean> testDetailsList  = getTestIdsByTestBean(searchBean, getAuthorizedCodes(request));

		/*	if(StringUtils.isBlank(errorMessage)) {
						request.setAttribute("success","true");
						request.setAttribute("successMessage", " Test is Live for "+ 
								": Type : "+searchBean.getLiveType()+  
								"; ApplicableType : "+searchBean.getApplicableType()+
								"; acadsMonth : "+searchBean.getAcadMonth()+ 
								"; acadsYear : "+searchBean.getAcadYear()+ 
								"; examMonth : "+searchBean.getExamMonth()+
								"; examYear : "+searchBean.getExamYear());
						m.addAttribute("searchBean", searchBean);	
						return makeTestLiveForm(request, response, m);
					}else {
						request.setAttribute("error", "true");
						request.setAttribute("errorMessage", errorMessage);
						m.addAttribute("searchBean", searchBean);	
						return makeTestLiveForm(request, response, m);
					}*/
		return new ModelAndView("consolidatedStudentsWiseExcelView","testDetailsList",testDetailsList);

	}

	private List<StudentsTestDetailsExamBean> getTestIdsByTestBean(TestExamBean test, String authorizedCenterCodes) {
		TestDAO dao = (TestDAO)act.getBean("testDao");
		StringBuilder sb = new StringBuilder();

		List<Long> configIds = getConfigIdsForTestLiveSettings(dao,test);

		String commaSeperatedConfigList = StringUtils.join(configIds, ',');
		List<TestExamBean> testList = dao.getTestListDetailsByConfigIdList(commaSeperatedConfigList);

		String commaSeparatedTestids = "";
		if(testList.size() > 0) {
			for(TestExamBean testBean : testList ) {

				sb.append(testBean.getTestId()).append(",");

			}
			commaSeparatedTestids = sb.deleteCharAt(sb.length() - 1).toString();
		}else {
			commaSeparatedTestids = "0";
		}
		return dao.getTestResultsByListId(commaSeparatedTestids, authorizedCenterCodes);


	}



	/*
	 * @RequestMapping(value = "/m/getAttemptsDetailsBySapidNTestId", method =
	 * RequestMethod.POST) public ResponseEntity<List<StudentsTestDetailsBean>>
	 * getAttemptsDetailsBySapidNTestId(@RequestBody TestBean test){ HttpHeaders
	 * headers = new HttpHeaders(); headers.add("Content-Type", "application/json");
	 * HashMap<String,String> response = new HashMap<>(); TestDAO dao =
	 * (TestDAO)act.getBean("testDao");
	 * )+"----"+ test.getTestId()); List<StudentsTestDetailsBean>studentTest =
	 * dao.getAttemptsDetailsBySapidNTestId(test.getSapid(), test.getTestId());
	 * ResponseEntity<List<StudentsTestDetailsBean>>(studentTest,headers,
	 * HttpStatus.OK); }
	 */




	@RequestMapping(value = "/evaluateTestAnswersForm", method =  RequestMethod.GET)
	public String evaluateTestAnswersForm(HttpServletRequest request, HttpServletResponse response, Model m,
			@RequestParam("id") Long id) {

		String userId = (String) request.getSession().getAttribute("userId");


		TestDAO dao = (TestDAO)act.getBean("testDao");
		TestExamBean test = dao.getTestById(id);
		m.addAttribute("test", test);
		String canFacultyEditIA = "N";

		StudentQuestionResponseExamBean answerBean = new StudentQuestionResponseExamBean();

		List<StudentQuestionResponseExamBean> answersToBeEvaluated = dao.getAnswersToBeEvaluatedByFacultyIdNTestId(userId, id);
		int countOfanswerToBeEvaluated = answersToBeEvaluated  !=null ? answersToBeEvaluated.size() : 0;

		List<StudentQuestionResponseExamBean> answersEvaluated = dao.getEvaluatedAnswersByFacultyIdNTestId(userId, id);
		int countOfEvaluatedAnswersByFacultyIdNTestId = answersEvaluated  !=null ? answersEvaluated.size() : 0;

		List<StudentQuestionResponseExamBean> copyCasesAnswers = dao.getCopyCaseAnswersByFacultyIdNTestId(userId, id);
		int countOfCopyCasesAnswers = copyCasesAnswers !=null ? copyCasesAnswers.size() : 0;

		/*
		 * if faculty edit rights date > result declared date - then allow
		 * if result declared date > faculty rights date - don't allow
		 * */
		try {
			canFacultyEditIA = dao.canFacultyEditIA( id, userId );
		} catch (Exception e) {
			// TODO Auto-generated catch block
			System.out.println( "Exception: "+e.getMessage() );
		}

		m.addAttribute("test", test);
		m.addAttribute("answerBean", answerBean);
		m.addAttribute("answersToBeEvaluated", answersToBeEvaluated);
		m.addAttribute("countOfanswerToBeEvaluated", countOfanswerToBeEvaluated);
		m.addAttribute("answersEvaluated", answersEvaluated);
		m.addAttribute("countOfEvaluatedAnswersByFacultyIdNTestId", countOfEvaluatedAnswersByFacultyIdNTestId);
		m.addAttribute("copyCasesAnswers", copyCasesAnswers);
		m.addAttribute("countOfCopyCasesAnswers", countOfCopyCasesAnswers);
		m.addAttribute("canFacultyEditIA", canFacultyEditIA);

		return "test/evaluateTestAnswersForm";
		
	}

	//saveTestAnswerEvaluation start

	@RequestMapping(value = "/saveTestAnswerEvaluation", method = RequestMethod.POST)
	public String saveTestAnswerEvaluation(HttpServletRequest request,
			HttpServletResponse response,
			@ModelAttribute StudentQuestionResponseExamBean answer,
			Model m) {


		TestDAO dao = (TestDAO)act.getBean("testDao");
		String userId = (String) request.getSession().getAttribute("userId");

		answer.setUserId(userId);


		//Do update
		boolean updated = dao.updateQuestionMarks(answer);
		if(!updated) {

			request.setAttribute("error", "true");
			request.setAttribute("errorMessage", "Error in saving evaluation marks.");

			return evaluateTestAnswersForm(request, response, m, answer.getTestId());
		}
		else {
			ResponseBean unmarkTEEResponse = iaTestService.unmarkTEEProcessed(answer.getTestId(), answer.getSapid(), userId);
			if("error".equals(unmarkTEEResponse.getStatus())) {
				request.setAttribute("error", "true");
				request.setAttribute("errorMessage", unmarkTEEResponse.getMessage());
				return evaluateTestAnswersForm(request, response, m, answer.getTestId());
			}

			request.setAttribute("success", "true");
			request.setAttribute("successMessage", "Evaluation Marks Saved Successfully.");

			return evaluateTestAnswersForm(request, response, m, answer.getTestId());
		}

	}

	//end 



//	@RequestMapping(value = "/m/getAttemptsDetailsBySapidNTestId", method = RequestMethod.POST)
//	public ResponseEntity<List<StudentsTestDetailsBean>> getAttemptsDetailsBySapidNTestId(@RequestBody TestBean test){
//		HttpHeaders headers = new HttpHeaders();
//		headers.add("Content-Type", "application/json");
//		HashMap<String,String> response = new HashMap<>();
//		TestDAO dao = (TestDAO)act.getBean("testDao");
//		List<StudentsTestDetailsBean>studentTest = dao.getAttemptsDetailsBySapidNTestId(test.getSapid(), test.getTestId());
//		return new ResponseEntity<List<StudentsTestDetailsBean>>(studentTest,headers, HttpStatus.OK);
//	} 	

	//searchIaToEvaluateForm mapping has been moved to internal-assessment microservice
	/*
	@RequestMapping(value = "/searchIaToEvaluateForm", method = {RequestMethod.GET, RequestMethod.POST})
	public String searchIaToEvaluateForm(HttpServletRequest request, HttpServletResponse response, Model m) {
		if(!checkSession(request, response)){
			return "login";
		}

		TestExamBean testBean = new TestExamBean();
		m.addAttribute("testBean",testBean);
		m.addAttribute("yearList", currentYearList);
		m.addAttribute("subjectList", getSubjectList());


		String facultyId = (String)request.getSession().getAttribute("userId");
		Person user = (Person)request.getSession().getAttribute("user");
		String roles = user != null ? user.getRoles() : "";


		if(roles.indexOf("Faculty") != -1){
			testBean.setFacultyId(facultyId);
		}
		request.getSession().setAttribute("testBean", testBean);



		return "searchIaToEvaluate";
	}
	*/

	//searchIaToEvaluate mapping has been moved to internal-assessment microservice
	/*
	@RequestMapping(value = "/searchIaToEvaluate", method = {RequestMethod.GET, RequestMethod.POST})
	public ModelAndView searchIaToEvaluate(HttpServletRequest request, HttpServletResponse response, @ModelAttribute TestExamBean testBean){


		if(!checkSession(request, response)){
			return new ModelAndView("login");
		}

		String pageNoStr = request.getParameter("pageNo");

		int pageNo = 1; 
		if(pageNoStr != null){
			pageNo = Integer.parseInt(pageNoStr);
		}

		ModelAndView modelnView = new ModelAndView("searchIaToEvaluate");
		request.getSession().setAttribute("testBean", testBean);

		//String facultyId = (String)request.getSession().getAttribute("userId");

		Person user = (Person)request.getSession().getAttribute("user");
		String roles = user != null ? user.getRoles() : "";

		//testBean.setFacultyId(facultyId);

		TestDAO dao = (TestDAO)act.getBean("testDao");

		List <TestExamBean> iaList = new ArrayList<>();
		try {
			iaList = dao.getIaForFaculty(testBean, getAuthorizedCodes(request));
		}catch(Exception e) {
			
		}
		request.getSession().setAttribute("iaList", iaList);
		modelnView.addObject("iaList", iaList);

		modelnView.addObject("testBean", testBean);
		modelnView.addObject("rowCount", iaList.size());

		modelnView.addObject("yearList", currentYearList);
		modelnView.addObject("subjectList", getSubjectList());

		if(iaList == null || iaList.size() == 0){
			request.setAttribute("error", "true");
			request.setAttribute("errorMessage", "No records found.");
		}
		return modelnView;
	}
	*/

	//downloadIaToEvaluate mapping has been moved to internal-assessment microservice
	/*
	@RequestMapping(value = "/downloadIaToEvaluate", method =  RequestMethod.GET)
	public ModelAndView downloadIaToEvaluate(HttpServletRequest request,
			HttpServletResponse response ) {


		if(!checkSession(request, response)){
			//return new ModelAndView("studentPortalRediret");
			redirectToPortalApp(response);
			return null;
		}
		List<TestExamBean> iaList = new ArrayList<TestExamBean>();
		try {
			iaList = (List<TestExamBean>) request.getSession().getAttribute("iaList");
		}catch(Exception e) {
			
		}


		return new ModelAndView("IAtoEvaluateExcelView","iaList",iaList);

	}
	*/

//	//Upload assignment for question start
//	@RequestMapping(value = "/m/uploadTestAssignmentQuestionFile", method = RequestMethod.POST,  produces = "application/json")
//	public ResponseEntity<HashMap<String,String>> uploadTestAssignmentQuestionFile(MultipartHttpServletRequest request){
//
//		HttpHeaders headers = new HttpHeaders();
//		headers.add("Content-Type", "application/json");
//		TestDAO dao = (TestDAO)act.getBean("testDao");
//
//		HashMap<String,String> response = new HashMap<>();
//
//		String errorMessage = "";
//		String returnLink="";
//
//		String testId = (String)request.getParameter("testId");
//		Iterator<String> it = request.getFileNames();
//		while (it.hasNext()) {
//			String uploadFile = it.next();
//			MultipartFile file = request.getFile(uploadFile);
//			returnLink= uploadTestQuestionAssignmentToServer(file,testId);
//		}
//		//MultipartFile multipartFile = request.getFile("user-file");
//
//		//String name = multipartFile.getOriginalFilename();
//
//		//imageBean = uploadTestQuestionImageToServer(imageBean);
//		//errorMessage = imageBean.getErrorMessage();  
//		if(StringUtils.isBlank(returnLink)) {
//			errorMessage ="Error in saving file to DB.";
//		}
//		if("Error".equalsIgnoreCase(errorMessage)) {
//			response.put("Status", "Failed");
//			response.put("errroMessage", "Error in deleting question.");
//			return new ResponseEntity<>(response,headers, HttpStatus.INTERNAL_SERVER_ERROR);
//		}
//		else {
//			response.put("Status", "Success");
//			response.put("imageUrl", returnLink);
//			response.put("successMessage", errorMessage);
//			return new ResponseEntity<>(response,headers, HttpStatus.OK);	
//		}
//
//
//	}

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
	//Upload assignment for question end

	//for testCopyCaseForm start

	@RequestMapping(value = "/testCopyCaseForm", method =  RequestMethod.GET)
	public String testCopyCaseForm(HttpServletRequest request, HttpServletResponse response, Model m,
			@RequestParam("id") Long id) {

		String userId = (String) request.getSession().getAttribute("userId");


		TestDAO dao = (TestDAO)act.getBean("testDao");
		TestExamBean test = dao.getTestById(id);
		m.addAttribute("test", test);

		//List<StudentsTestDetailsBean> copyCaseStudents =  dao.getStudentsTestDetailsByTestIdHavingCopyCaseMatchedPercentageAbove70(id);
		//m.addAttribute("copyCaseStudents", copyCaseStudents);

		List<ResultDomain> copyCases =  dao.getCopyCasesByTestId(id);
		iaTestService.storeAnswerUrlForCopyCases(copyCases);					//replace descriptive answers with stored descriptive answer URL
		m.addAttribute("copyCases", copyCases);

		/*
		 * for(StudentsTestDetailsBean s : copyCaseStudents) {
		 * }
		 */

		return "test/testCopyCaseForm";
	}

	@RequestMapping(value = "/toggleIATestsCopyCaseStatus", method =  RequestMethod.GET)
	public String toggleIATestsCopyCaseStatus(HttpServletRequest request, 
			HttpServletResponse response, 
			Model m,
			@RequestParam("testId") Long testId,
			@RequestParam("sapid") String sapid,
			@RequestParam("attempt") String attempt) {




		String userId = (String) request.getSession().getAttribute("userId");

		TestDAO dao = (TestDAO)act.getBean("testDao");
		TestExamBean test = dao.getTestById(testId);
		m.addAttribute("test", test);

		String toggleIATestsCopyCaseStatusError = dao.toggleIATestsCopyCaseStatus(testId,sapid,attempt);

		if(!StringUtils.isBlank(toggleIATestsCopyCaseStatusError)) {
			request.setAttribute("error", "true");
			request.setAttribute("errorMessage", "Error in updating status for "+sapid+". Error : "+toggleIATestsCopyCaseStatusError);
		}

		return testCopyCaseForm(request, response, m, testId);
	}


	///exam/markIATestsCopyCaseFromAdminView?testId=${test.id}&sapid=${copyCasesapId1}&attempt=${copyCase.attempt}&questionId=${copyCase.questionId}&markedForCopyCase=
	@RequestMapping(value = "/markIATestsCopyCaseFromAdminView", method =  RequestMethod.GET)
	public String markIATestsCopyCaseFromAdminView(HttpServletRequest request, 
			HttpServletResponse response, 
			Model m,
			@RequestParam("testId") Long testId,
			@RequestParam("sapid") String sapid,
			@RequestParam("attempt") String attempt,
			@RequestParam("questionId") Long questionId) {

		String userId = (String) request.getSession().getAttribute("userId");

		TestDAO dao = (TestDAO)act.getBean("testDao");
		//TestBean test = dao.getTestById(testId);
		//m.addAttribute("test", test);

		String markIATestsCopyCaseFromAdminViewError = dao.markIATestsCopyCaseFromAdminView(testId,sapid,attempt,questionId);

		String updateStatusInCopyCaseTableError = "";//dao.updateStatusInCopyCaseTable(testId,sapid,attempt,questionId,markedForCopyCase);


		if(!StringUtils.isBlank(markIATestsCopyCaseFromAdminViewError) || !StringUtils.isBlank(updateStatusInCopyCaseTableError) ) {
			request.setAttribute("error", "true");
			request.setAttribute("errorMessage", "Error in updating status for "+sapid+". Error : "+markIATestsCopyCaseFromAdminViewError+","+updateStatusInCopyCaseTableError);
		}else {
			request.setAttribute("success", "true");
			request.setAttribute("successMessage", "Marked "+sapid+" for CopyCase Successfully.");

		}

		return testCopyCaseForm(request, response, m, testId);
	}

	@RequestMapping(value = "/unMarkIATestsCopyCaseFromAdminView", method =  RequestMethod.GET)
	public String unMarkIATestsCopyCaseFromAdminView(HttpServletRequest request, 
			HttpServletResponse response, 
			Model m,
			@RequestParam("testId") Long testId,
			@RequestParam("sapid") String sapid,
			@RequestParam("attempt") String attempt,
			@RequestParam("questionId") Long questionId) {

		String userId = (String) request.getSession().getAttribute("userId");

		TestDAO dao = (TestDAO)act.getBean("testDao");
		//TestBean test = dao.getTestById(testId);
		//m.addAttribute("test", test);

		String unMarkIATestsCopyCaseFromAdminViewError = dao.unMarkIATestsCopyCaseFromAdminView(testId,sapid,attempt,questionId);

		if(!StringUtils.isBlank(unMarkIATestsCopyCaseFromAdminViewError)) {
			request.setAttribute("error", "true");
			request.setAttribute("errorMessage", "Error in updating status for "+sapid+". Error : "+unMarkIATestsCopyCaseFromAdminViewError);
		}
		else {
			request.setAttribute("success", "true");
			request.setAttribute("successMessage", "Unmarked "+sapid+" for CopyCase Successfully.");

		}

		return testCopyCaseForm(request, response, m, testId);
	}

	@RequestMapping(value = "/markBothStudentsForIATestsCopyCaseFromAdminView", method =  RequestMethod.GET)
	public String markBothStudentsForIATestsCopyCaseFromAdminView(HttpServletRequest request, 
			HttpServletResponse response, 
			Model m,
			@RequestParam("testId") Long testId,
			@RequestParam("sapids") String sapids,
			@RequestParam("attempt") String attempt,
			@RequestParam("questionId") Long questionId) {

		String userId = (String) request.getSession().getAttribute("userId");

		TestDAO dao = (TestDAO)act.getBean("testDao");
		//TestBean test = dao.getTestById(testId);
		//m.addAttribute("test", test);

		String[] sapidsArray = sapids.split("~", -1);
		String markIATestsCopyCaseFromAdminViewError ="";
		String updateStatusInCopyCaseTableError = "";
		for(int i =0;i<sapidsArray.length;i++) {
			String sapid =sapidsArray[i];
			markIATestsCopyCaseFromAdminViewError += dao.markIATestsCopyCaseFromAdminView(testId,sapid,attempt,questionId);

			updateStatusInCopyCaseTableError += "";//dao.updateStatusInCopyCaseTable(testId,sapid,attempt,questionId,markedForCopyCase);


			if(!StringUtils.isBlank(markIATestsCopyCaseFromAdminViewError) || !StringUtils.isBlank(updateStatusInCopyCaseTableError) ) {
				markIATestsCopyCaseFromAdminViewError = "Error in updating status for "+sapid+". Error : "+markIATestsCopyCaseFromAdminViewError+","+updateStatusInCopyCaseTableError;
			}	
		}

		if(!StringUtils.isBlank(markIATestsCopyCaseFromAdminViewError) || !StringUtils.isBlank(updateStatusInCopyCaseTableError) ) {
			request.setAttribute("error", "true");
			request.setAttribute("errorMessage", "Error in updating status for "+sapidsArray+". Error : "+markIATestsCopyCaseFromAdminViewError+","+updateStatusInCopyCaseTableError);
		}
		else {
			request.setAttribute("success", "true");
			request.setAttribute("successMessage", "Marked Both students "+Arrays.toString(sapidsArray)+" for CopyCase Successfully.");

		}



		return testCopyCaseForm(request, response, m, testId);
	}

	@RequestMapping(value = "/downloadCopyCasesByTestId", method =  RequestMethod.GET)
	public ModelAndView downloadCopyCasesByTestId(HttpServletRequest request,
			HttpServletResponse response,
			@RequestParam("testId")Long testId) {


		if(!checkSession(request, response)){
			//return new ModelAndView("studentPortalRediret");
			redirectToPortalApp(response);
			return null;
		}
		TestDAO dao = (TestDAO)act.getBean("testDao");
		List<ResultDomain> copyCases =  dao.getCopyCasesByTestId(testId);

		return new ModelAndView("CopyCasesExcelView","copyCases",copyCases);

	}		

	//for testCopyCaseForm end


	//studentTest audit trail analysis start

	//api for ckeditor file upload start
	@RequestMapping(value = "/ckeditorFileUpload", method = RequestMethod.POST,  produces = "application/json")
	public ResponseEntity<CKeditorResponseBean> ckeditorFileUpload(MultipartHttpServletRequest request){
		
		HttpHeaders headers = new HttpHeaders();
		headers.add("Content-Type", "application/json");
		TestDAO dao = (TestDAO)act.getBean("testDao");
		
		CKeditorResponseBean response = new CKeditorResponseBean();

		String errorMessage = "";
		String returnLink="";
		String fileName = "";
		MultipartFile file = null;
		Iterator<String> it = request.getFileNames();
        while (it.hasNext()) {
            String uploadFile = it.next();
            file = request.getFile(uploadFile);
            fileName = file.getOriginalFilename();
            //returnLink= uploadTestQuestionAssignmentToServer(file,testId);
        }
		//MultipartFile multipartFile = request.getFile("user-file");
        
        //returnLink = uploadCKEditorFileToSystemAndReturnLink(file);
        returnLink = uploadCKEditorFileToAWSAndReturnLink(file);
        
        if(returnLink.contains("Error in ")) {

			response.setUploaded(0);
			CKeditorErrorBean err = new CKeditorErrorBean();
			err.setMessage("Error in File upload : "+returnLink);
			response.setError(err);
			
		}
		else {
			response.setUploaded(1);
			response.setFileName(returnLink.replaceAll(SERVER_PATH+"editorFiles/", ""));
			response.setUrl(returnLink);
		}
		
    	return new ResponseEntity<>(response,headers, HttpStatus.OK);	
		
	}

	private String uploadCKEditorFileToSystemAndReturnLink(MultipartFile fileBean) {

        InputStream inputStream = null;   
        OutputStream outputStream = null;
		String returnUrl = "";
		File file = convertFile(fileBean);

		try {
            inputStream = new FileInputStream(file); 
			
            String extension = "."+FilenameUtils.getExtension(fileBean.getOriginalFilename());
            
            
            long currentUnixTime = System.currentTimeMillis() / 1000L;
            
            String fileName = currentUnixTime+extension;
            
			String folderName = CKEDITORS_UPLOADEDFILES_PATH +"";
			
			String filePath = folderName + fileName;
            
			File folder = new File(folderName);
			if (!folder.exists()) {   
				folder.mkdirs();   
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
		      returnUrl=SERVER_PATH+"editorFiles/"+fileName;
              return returnUrl;

		} catch (Exception e) {
			
			return "Error in saving file to system, Error : "+e.getMessage();
		}
		
	}

	private String uploadCKEditorFileToAWSAndReturnLink(MultipartFile fileBean) {

		String returnUrl = "";

		try {  

			long currentUnixTime = System.currentTimeMillis() / 1000L;

			String extension = "."+FilenameUtils.getExtension(fileBean.getOriginalFilename());
			String folderPath = "editorfiles/";
			String fileName = folderPath + environment + "_" + currentUnixTime + extension;

			returnUrl = amazonS3Helper.uploadMiltipartFile( fileBean, folderPath, awsIAFileBucket, fileName);

			return returnUrl;
			
		} catch (Exception e) {
			
			return "Error in saving file to aws, Error : "+e.getMessage();
		}

	}

	//
	@RequestMapping(value = "/createNewTemplateForm", method = RequestMethod.GET)
	public String createNewTemplateForm(HttpServletRequest request,Model m ){ 
		
        try {//flash message from redirectview
			Map<String, ?> flashMap = RequestContextUtils.getInputFlashMap(request);
				request.setAttribute("success", flashMap.get("success"));
				request.setAttribute("successMessage", flashMap.get("successMessage")); 
				request.setAttribute("error", flashMap.get("error"));
				request.setAttribute("errorMessage", flashMap.get("errorMessage")); 
		} catch (Exception e) { }
        
		List<TestQuestionConfigBean>  templateMap = tDao.getAllTemplates();
		List<String> types = tDao.getAllTypes();     
		List<TestQuestionConfigBean> tTypes = tDao.getTemplateTypes();
		TestQuestionConfigBean formBean = new TestQuestionConfigBean(); 
		m.addAttribute("tTypes", tTypes); 
		m.addAttribute("templateMap", templateMap); 
		m.addAttribute("types", types);
		TestQuestionConfigBean innerBean = new TestQuestionConfigBean();
		innerBean.setTypeName("SINGLESELECT");
		innerBean.setType(1); 
		innerBean.setMinNoOfQuestions(2);
		innerBean.setQuestionMarks(2.0); 
		ArrayList<TestQuestionConfigBean> beanList= new ArrayList<TestQuestionConfigBean>();
		beanList.add(innerBean);
		formBean.setTestQuestionConfigBean(beanList); 
		m.addAttribute("formBean", formBean);
		SectionBean sectionBean = new SectionBean(); 
		m.addAttribute("sectionBean", sectionBean);
		m.addAttribute("AllQuestionTypes", tDao.getAllQuestionTypes()); 
		m.addAttribute("sectionQnTypes", tDao.getSectionQuestionTypes()); 
			return "test/createNewTemplate"; 
	} 
	@RequestMapping(value = "/createNewTemplate", method = RequestMethod.POST)
	public RedirectView createNewTemplate(HttpServletRequest request,Model m,
			@ModelAttribute TestQuestionConfigBean bean ,RedirectAttributes redirectAttrs){
		
		 try {   
			//added sectionwise question configuration
			//save section and get all section ids to save in template
			tDao.saveSectionsForTemplate(bean.getSectionBean()); 
			
			tDao.createNewTemplate(bean);  
			redirectAttrs.addFlashAttribute("success", "true");
			redirectAttrs.addFlashAttribute("successMessage", "Template Created Successfully.");
			 
		} catch (Exception e) {
			 
			redirectAttrs.addFlashAttribute("error", "true"); 
			redirectAttrs.addFlashAttribute("errorMessage", "Error in creating template,"+e.getMessage());
			
		}  
		 
		 return new RedirectView("createNewTemplateForm"); 
	} 
	@RequestMapping(value = "/editTestQnTemplate", method = RequestMethod.POST)
	public RedirectView editTestQnTemplate(HttpServletRequest request,Model m,
			@ModelAttribute TestQuestionConfigBean bean ,RedirectAttributes redirectAttrs){
		 try {
			 
			tDao.editQnTemplate(bean); 
			redirectAttrs.addFlashAttribute("success", "true");
			redirectAttrs.addFlashAttribute("successMessage", "Template Updated Successfully.");
			 
		} catch (Exception e) {
			 
			redirectAttrs.addFlashAttribute("error", "true"); 
			redirectAttrs.addFlashAttribute("errorMessage", "Failed to Update Template.");
			
		}  
	    return new RedirectView("createNewTemplateForm");
	}
	@RequestMapping(value = "/deleteTestQnTemplate", method = RequestMethod.GET)
	public RedirectView deleteTestQnTemplate(HttpServletRequest request,Model m,
			@RequestParam String id,RedirectAttributes redirectAttrs ){
		 try {
			tDao.deleteQnTemplate(id); 
			redirectAttrs.addFlashAttribute("success", "true");
			redirectAttrs.addFlashAttribute("successMessage", "Deleted Successfully.");
			  
		} catch (Exception e) {
			 
			redirectAttrs.addFlashAttribute("error", "true"); 
			redirectAttrs.addFlashAttribute("errorMessage", "Failed to Delete Template.");
			
		}  
		return new RedirectView("createNewTemplateForm");
	}
	public void saveTestConfig(TestExamBean test){
		List<TestQuestionConfigBean> configList =   tDao.getTemplateById(test.getTemplateId());
		for(TestQuestionConfigBean config :configList) {
			config.setTestId(test.getId());
			tDao.saveTestConfig(config);
		}
	}

	public String getInitials(String str) {
        String[] arr = str.split(" ");
        String finalResult = "";
        for (String s : arr) {
            if (s.equals("-") || s.equals(":") || Character.isLowerCase(s.charAt(0)) || s.equals("&")) {
                continue;
            }
            finalResult += Character.toUpperCase(s.charAt(0));
            if (s.contains(":")) {
                finalResult += s.split(":").length>1?Character.toUpperCase(s.split(":")[1].charAt(0)):"";
            }
            if (s.contains("-")) {
                finalResult += s.split("-").length>1?Character.toUpperCase(s.split("-")[1].charAt(0)):"";
            }
            if (s.contains("&")) {
                finalResult += s.split("&").length>1?Character.toUpperCase(s.split("&")[1].charAt(0)):"";
            }
        }
        return finalResult;
	}
	
	
	private void setTestQuestionsInRedisByTestId(Long testId) {
		IATestHelper iATestHelper = (IATestHelper)act.getBean("iATestHelper");
		String response = iATestHelper.setTestQuestionsInRedisByTestId(testId);
	}
	
	//Code for addTestForLeadsForm start
	@RequestMapping(value = "/admin/addTestForLeadsForm", method =  RequestMethod.GET)
	public String addTestForLeadsForm(HttpServletRequest request, 
			HttpServletResponse response,
			Model m){
		
/*    	if(!checkSession(request, response)){
			return "studentPortalRediret";
		} */ 
		List<FacultyExamBean>facultyList = getFacultyList();
		 
		m.addAttribute("facultyList", facultyList);
		TestExamBean test = new TestExamBean();
		test.setProctoringEnabled("Y");
		test.setShowCalculator("Y");
		
		//start added by Abhay 
		test.setAcadMonth(LEAD_CURRENT_ACAD_MONTH);
		test.setAcadYear(LEAD_CURRENT_ACAD_YEAR);
		test.setYear(LEAD_CURRENT_EXAM_YEAR);
		test.setMonth(LEAD_CURRENT_EXAM_MONTH);
		test.setConsumerTypeId("6");
		test.setShowResultsToStudents("Y");
		// end  added by Abhay 
		
		m.addAttribute("test", test);
		
		m.addAttribute("subjectList", getSubjectList());
		m.addAttribute("programList", getProgramList());
		m.addAttribute("consumerType", getConsumerTypeList());
		
		
		return "test/addTestForLeadsForm"; 
	}
	//Code for addTestForLeadsForm end
	
	
	//Code for editTestForLead Start By Abhay
	@RequestMapping(value = "/editTestForLeadsForm", method =  RequestMethod.GET)
	public String editTestForLeadsForm(HttpServletRequest request,
			HttpServletResponse response,
			Model m,
			@RequestParam("id") Long id)  {
 
		 try {//flash message from redirectview
				Map<String, ?> flashMap = RequestContextUtils.getInputFlashMap(request);
					request.setAttribute("success", flashMap.get("success"));
					request.setAttribute("successMessage", flashMap.get("successMessage")); 
					request.setAttribute("error", flashMap.get("error"));
					request.setAttribute("errorMessage", flashMap.get("errorMessage")); 
			} catch (Exception e) { }
	        
		TestDAO dao = (TestDAO)act.getBean("testDao");
		TestExamBean test = dao.getTestForLeadsById(id);
		test = setProgramConfigNamesForSingleTest(test);
		test = dao.getReference_Batch_Or_Module_Name(test);
		List<TestQuestionConfigBean> config =  tDao.getQuestionsConfiguredForLeads(test.getId());
		test.setTestQuestionConfigBean(config);
		m.addAttribute("subjectList", getSubjectList());
		m.addAttribute("programList", getProgramList());
		m.addAttribute("test", test);
		m.addAttribute("facultyList", getFacultyList());
//		m.addAttribute("templateTypes",tDao.getTemplateTypes());
		m.addAttribute("consumerType", getConsumerTypeList());
		return "test/addTestForLeadsForm";  
	}
	//Code for editTestForLead  end By Abhay
	

	/* changes by Abhay start checkTestConfigChanged returns true if value is changed in form */
	private String checkTestConfigChangedForLeads(TestDAO dao, TestExamBean test) {

		try {
			TestExamBean testFromDB = dao.getTestForLeadsById(test.getId());
			/*
			 * "ConsumerType "+test.getConsumerTypeIdFormValue()+" "+testFromDB.
			 * getConsumerTypeIdFormValue() +
			 * "\n ProgramStructure "+test.getProgramStructureIdFormValue()+" "+testFromDB.
			 * getProgramStructureIdFormValue() +
			 * "\n Program "+test.getProgramIdFormValue()+" "+testFromDB.
			 * getProgramIdFormValue() +
			 * "\n applicableType : "+test.getApplicableType()+" "+testFromDB.
			 * getApplicableType() +
			 * "\n referenceID : "+test.getReferenceId()+" "+testFromDB.getReferenceId() +
			 * "\n Subject : "+test.getSubject()+" "+testFromDB.getSubject() +
			 * "\n AcadYear : "+test.getAcadYear()+" "+testFromDB.getAcadYear() +
			 * "\n AcadMonth : "+test.getAcadMonth()+" "+testFromDB.getAcadMonth() );
			 */

			if( test.getConsumerTypeIdFormValue().equalsIgnoreCase(testFromDB.getConsumerTypeIdFormValue())
					&& test.getProgramStructureIdFormValue().equalsIgnoreCase(testFromDB.getProgramStructureIdFormValue())
					&& test.getProgramIdFormValue().equalsIgnoreCase(testFromDB.getProgramIdFormValue())
					&& test.getApplicableType().equalsIgnoreCase(testFromDB.getApplicableType())
					&& test.getReferenceId() == testFromDB.getReferenceId()
					&& test.getSubject().equalsIgnoreCase(testFromDB.getSubject())
					&& test.getAcadYear() == testFromDB.getAcadYear()
					&& test.getAcadMonth().equalsIgnoreCase(testFromDB.getAcadMonth())
					) {
				return "false";
			}else {
				return "true";
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			
			return "Error in checkTestConfigChanged : "+e.getMessage();
		}

	}
	/* changes by Abhay End checkTestConfigChanged returns true if value is changed in form */

	

	//Code for addTestForLeads start
	@RequestMapping(value = "/addTestForLeads", method = RequestMethod.POST)
	public RedirectView addTestForLeads(HttpServletRequest request,
			HttpServletResponse response,
			@ModelAttribute TestExamBean test,RedirectAttributes redirectAttrs,
			Model m) {

		String userId = (String) request.getSession().getAttribute("userId");

		try { 
			test.setCreatedBy(userId);
			test.setLastModifiedBy(userId);
			test.setActive("Y");
			
			test.setConsumerTypeIdFormValue(test.getConsumerTypeId());
			test.setProgramStructureIdFormValue(test.getProgramStructureId());
			test.setProgramIdFormValue(test.getProgramId());
			
			
			
			if("old".equalsIgnoreCase(test.getApplicableType())) {
				test.setReferenceId(0);
			}
            
			if(test.getId() == null) { // save new test

					 long saveTest=0;
					 saveTest = tDao.saveTestForLeads(test);
					 if(saveTest==0) {
						 	redirectAttrs.addFlashAttribute("error", "true");
						 	redirectAttrs.addFlashAttribute("errorMessage", "Error in saving test to DB");
						}else {
							//test.setId(saveTest);
							test.setId(saveTest);
							//saveTestConfig(test); ask erin 8JUNE2020
							String createTestIdConfigurationMappingsError = createTestIdConfigurationMappingsForLeads(tDao,test);
							
							if(StringUtils.isBlank(createTestIdConfigurationMappingsError)) {
								//tDao.insertMCQInPost(test);
								//Post post  = tDao.findPostByReferenceId(test.getId());
								//insertToRedis(post);
								//refreshRedis(post); 
								redirectAttrs.addFlashAttribute("success", "true");
								redirectAttrs.addFlashAttribute("successMessage", "Test Created Successfully.");
								
								return new RedirectView("viewTestDetailsForLeads?id="+saveTest);
							}else {

								int deleted = tDao.deleteTestForLeads(test.getId());
								if(deleted < 0) {
									redirectAttrs.addFlashAttribute("error", "true");
									redirectAttrs.addFlashAttribute("errorMessage", "Error in deleting test.");
								}
								
								redirectAttrs.addFlashAttribute("error", "true");
								redirectAttrs.addFlashAttribute("errorMessage", "Error in saving test to DB, createTestIdConsumerTypeIdMappingsError : "+createTestIdConfigurationMappingsError);
								
							}
							
						}
				
				
			}else { // edit test for leads changes By Abhay 
				TestExamBean testBeforeUpdate = tDao.getTestForLeadsById(test.getId());
				boolean updateTest = tDao.updateTestForLeads(test); 
//			    if(test.getTemplateId().length()>0) {
//			    	tDao.deleteQuestionConfig(test.getId());
//				    saveTestConfig(test);
//				    
//			    }
				if(!updateTest) {
					redirectAttrs.addFlashAttribute("error", "true");
					redirectAttrs.addFlashAttribute("errorMessage", "Error in saving test to DB");
					return new RedirectView("editTestForLeadsForm?id="+test.getId()); 

				}else {

					String isTestConfigChanged = checkTestConfigChangedForLeads(tDao,testBeforeUpdate);

					if("true".equalsIgnoreCase(isTestConfigChanged)) {

						//Delete old entries 
						String deleteConsumerProgramStructureIdbyTestIdError = tDao.deleteTestIdNConfigMappingbyTestIdForLeads(test.getId());

						if(!StringUtils.isBlank(deleteConsumerProgramStructureIdbyTestIdError)) {

							redirectAttrs.addFlashAttribute("error", "true");
							redirectAttrs.addFlashAttribute("errorMessage", "Error in saving test to DB, deleteConsumerProgramStructureIdbyTestIdError : "+deleteConsumerProgramStructureIdbyTestIdError);
							return new RedirectView("viewTestDetailsForLeads?id="+test.getId()); 
						}
 	
						String createTestIdConfigurationMappingsError = createTestIdConfigurationMappingsForLeads(tDao,test);
 
						if(StringUtils.isBlank(createTestIdConfigurationMappingsError)) {
// Commented By Abhay Not required yet
//							tDao.updateMCQInPost(test); 
//							Post post  = tDao.findPostByReferenceId(test.getId());
//							refreshRedis(post);

							redirectAttrs.addFlashAttribute("success", "true");
							redirectAttrs.addFlashAttribute("successMessage", "Test Updated Successfully.");
							return new RedirectView("viewTestDetailsForLeads?id="+test.getId()); // changes by abhay for edit leads 
						}else {
							redirectAttrs.addFlashAttribute("error", "true");
							redirectAttrs.addFlashAttribute("errorMessage", "Error in saving test to DB, createTestIdConsumerTypeIdMappingsError : "+createTestIdConfigurationMappingsError);
						}

					}else if("false".equalsIgnoreCase(isTestConfigChanged)) { //do nothing
					}else {
						redirectAttrs.addFlashAttribute("error", "true");
						redirectAttrs.addFlashAttribute("errorMessage", "Error in saving to DB, Please click on update again till success / contact developer team <br> Error : "+isTestConfigChanged);
					}

				}
			}
			
		} catch (Exception e) {
			
			redirectAttrs.addFlashAttribute("error", "true");
			redirectAttrs.addFlashAttribute("errorMessage", "Error : "+e.getMessage());
		}
		return new RedirectView("addTestForLeadsForm");  
	}
	//Code for addTestForLeads end
	

	private String createTestIdConfigurationMappingsForLeads(TestDAO dao, TestExamBean test) {

		List<Long> configIds = getConfigIdsForTestLiveSettings(dao,test);
		return dao.insertTestIdNConfigurationMappingsForLeads(test,configIds);

		
	}


	@RequestMapping(value = "/viewTestDetailsForLeads", method =  RequestMethod.GET)
	public String viewTestDetailsForLeads(HttpServletRequest request,
			HttpServletResponse response,
			Model m,
			@RequestParam("id") Long id)  {
		try {//flash message from redirectview
			Map<String, ?> flashMap = RequestContextUtils.getInputFlashMap(request);
				request.setAttribute("success", flashMap.get("success"));
				request.setAttribute("successMessage", flashMap.get("successMessage")); 
				request.setAttribute("error", flashMap.get("error"));
				request.setAttribute("errorMessage", flashMap.get("errorMessage")); 
		} catch (Exception e) { }

		TestDAO dao = (TestDAO)act.getBean("testDao");
		TestExamBean test = dao.getTestByIdForLeads(id);
		test = setProgramConfigNamesForSingleTest(test);

		//String testStudentId = dao.getTestStudentForTestByTestId(id);
		String testStudentId = "77777777777";
		
		//List<TestQuestionConfigBean> qnsConfigured=  tDao.getQuestionsConfigured(id);
		List<TestQuestionConfigBean> qnsConfigured = new ArrayList<>();
		//List<TestQuestionConfigBean> qnsUpoaded=  tDao.getQuestionsUploadedCount(id);
		List<TestQuestionConfigBean> qnsUpoaded = new ArrayList<>();
		
		//HashMap<Long,TestTypeBean> typeIdNBeanMap =  dao.getTypeIdNTypeMap();
		HashMap<Long,TestTypeBean> typeIdNBeanMap =new HashMap<>();
		
		List<TestQuestionConfigBean> questionConfiguredList = new ArrayList<>();
		//m.addAttribute("questionConfiguredList", dao.getQuestionConfigsListByTestId(id));
		m.addAttribute("questionConfiguredList", questionConfiguredList);
		m.addAttribute("typeIdNBeanMap", typeIdNBeanMap);
		m.addAttribute("qnsConfigured", qnsConfigured);
		m.addAttribute("qnsUpoaded", qnsUpoaded);

		m.addAttribute("test", test);
		m.addAttribute("noOfQuestions", dao.getNoOfQuestionsByTestIdForLeads(id));
		//m.addAttribute("noOfQuestionWeightages", dao.getNoOfQuestionsWeightageEntriesByTestId(id));
		m.addAttribute("noOfQuestionWeightages", "0");
		m.addAttribute("ifTestlive", dao.getLiveTestByModuleId(test.getReferenceId())>0?true:false);
		if(testStudentId != null) {
			m.addAttribute("testStudentId",testStudentId);
		}

		return "test/testDetailsForLeads";
	}
	
	//uploadTestQuestionFormForLeads

	@RequestMapping(value = "/uploadTestQuestionFormForLeads", method =  RequestMethod.GET)
	public String uploadTestQuestionFormForLeads(HttpServletRequest request,
			HttpServletResponse response,
			Model m,
			@RequestParam("id") Long id)  {

		TestDAO dao = (TestDAO)act.getBean("testDao");
		TestExamBean test = dao.getTestByIdForLeads(id);

		HashMap<Long,TestTypeBean> typeIdBeanMap= dao.getTypeIdNTypeMapForLeads();		
		List<TestQuestionExamBean> testQuestions = dao.getTestQuestionsForLeads(id);

		for(TestQuestionExamBean q : testQuestions) {
			try {
				q.setTypeInString(typeIdBeanMap.get(new Long(q.getType())).getType());
			} catch (Exception e) {
				// TODO Auto-generated catch block
				
			}
		}

		m.addAttribute("test", test);
		m.addAttribute("fileBean", new FileBean());
		m.addAttribute("testQuestions", testQuestions);
		m.addAttribute("typeIdBeanMap", typeIdBeanMap);

		return "test/uploadTestQuestionForLeads";
	}
	
	//uploadTestQuestionForLeads
	
	// Start deleteTestQuestionForLeads By Abhay
	@RequestMapping(value = "/deleteTestQuestionForLeads", method = RequestMethod.GET)
	public RedirectView deleteTestQuestionForLeads(HttpServletRequest request,
			HttpServletResponse response,
			@RequestParam("id") Long id, @RequestParam("testId") Long testId,RedirectAttributes redirectAttrs,
			Model m) {
		String status = tDao.deleteTestQuestionForLeads(id);
		if("true".equals(status)) {
			redirectAttrs.addFlashAttribute("success", "true");
			redirectAttrs.addFlashAttribute("successMessage", "Question Deleted Successfully.");
			return new RedirectView("uploadTestQuestionFormForLeads?id="+testId); 
		}else {
			redirectAttrs.addFlashAttribute("error", "true");
			redirectAttrs.addFlashAttribute("errorMessage", "Error : "+status);
			return new RedirectView("uploadTestQuestionFormForLeads?id="+testId); 
		}
	}
	// End deleteTestQuestionForLeads By Abhay
	
	@RequestMapping(value = "/uploadTestQuestionForLeads", method =  RequestMethod.POST)
	public String uploadTestQuestionForLeads(HttpServletRequest request,	
			HttpServletResponse response,
			Model m,
			@ModelAttribute FileBean fileBean) {

		/*if(!checkSession(request, response)){
			return "studentPortalRediret";
		}*/

		String userId = (String) request.getSession().getAttribute("userId");
		MultipartFile file = fileBean.getFileData();
		Long testId = new Long(fileBean.getFileId());
		if (file.isEmpty()) {// Check if File was attached
			request.setAttribute("error", "true");
			request.setAttribute("errorMessage", "Please Select File to Upload...");
			return uploadTestQuestionFormForLeads(request,response,m,testId);
		}
		TestDAO dao = (TestDAO)act.getBean("testDao");
		TestExamBean test = dao.getTestByIdForLeads(testId);
		HashMap<String,TestTypeBean> testTypesMap= dao.getTestTypesMapForLeads();


		try {
			ExcelHelper excelHelper = new ExcelHelper();

			ArrayList<List> resultList = excelHelper.readTestQuestionsExcel(fileBean,userId,testTypesMap);
			ArrayList<TestQuestionExamBean> testList = (ArrayList<TestQuestionExamBean>) resultList.get(0);
			ArrayList<TestQuestionExamBean> errorBeanList = (ArrayList<TestQuestionExamBean>) resultList.get(1);

			if (!errorBeanList.isEmpty()) {
				request.setAttribute("error", "true");
				String errorMessage="Error while uploading data caused due to bad data of rows  : ";
				for(TestQuestionExamBean errorBean : errorBeanList) {
					errorMessage = errorMessage +"\n  "+errorBean.getErrorMessage()+"<br>";
				}

				request.setAttribute("errorMessage", errorMessage);
				return uploadTestQuestionFormForLeads(request,response,m,testId);
			}

			for(TestQuestionExamBean q : testList) {
				//Escape HTML characters Start
				q = escapeHTMLFromString(q);
				if(!StringUtils.isBlank(q.getErrorMessage())) {
					request.setAttribute("error", "true");
					request.setAttribute("errorMessage", "Error in  uploadTestQuestion got "+q.getErrorMessage());

					return uploadTestQuestionFormForLeads(request,response,m,testId);
				}
				//END
			}


			ArrayList<String> errorList = dao.batchUpdateTestQuestionForLeads(testList);

			if (errorList.size() == 0) {
				request.setAttribute("success", "true");
				request.setAttribute("successMessage", testList.size() + " rows out of "
						+ testList.size() + " inserted successfully.");

				//setTestQuestionsInRedisByTestId(testId);
				
				return viewTestDetailsForLeads(request,response,m,testId);
			} else {
				request.setAttribute("error", "true");
				request.setAttribute("errorMessage", errorList.size()
						+ " records were NOT inserted. Please see row number of rows not inserted. Error row numbers "
						+ errorList);
			}
		} catch (Exception e) {
			
			request.setAttribute("error", "true");
			request.setAttribute("errorMessage", "Error in inserting rows. Error : "+e.getMessage());

		}
		return uploadTestQuestionFormForLeads(request,response,m,testId);
	}



	@RequestMapping(value = "/admin/makeTestLiveFormForLeads", method = RequestMethod.GET)
	public String makeTestLiveFormForLeads(HttpServletRequest request, HttpServletResponse response, Model m) {

		TestDAO dao = (TestDAO)act.getBean("testDao");


		m.addAttribute("searchBean", new TestExamBean());	
		m.addAttribute("yearList", currentYearList);
		m.addAttribute("testsLiveConfigList", dao.getTestsLiveConfigListForLeads());
		m.addAttribute("acadsYearList", ACAD_YEAR_LIST);
		m.addAttribute("acadsMonthList", ACAD_MONTH_LIST);
		m.addAttribute("consumerType", getConsumerTypeList());
		m.addAttribute("subjectList", getSubjectList());

		return "test/makeTestLiveFormForLeads";
	}

	//saveTestLiveConfigForLeads
	
	@RequestMapping(value = "/saveTestLiveConfigForLeads",  method = RequestMethod.POST)
	public String saveTestLiveConfigForLeads(HttpServletRequest request,
									 HttpServletResponse response, 
									 @ModelAttribute TestExamBean  searchBean,
									 Model m) {
		
		String userId = (String) request.getSession().getAttribute("userId");
		searchBean.setCreatedBy(userId);
		searchBean.setLastModifiedBy(userId);
		
		//String errorMessage = dao.batchInsertOfMakeTestsLiveConfigs(searchBean,consumerProgramStructureIds);
		TestDAO dao = (TestDAO)act.getBean("testDao");
		String errorMessage = "";
		String errorMessageForConfigCheck = "";
		
		ArrayList<String> previewPendigForTests = new ArrayList<String>();
		/*
		try{
			
//			previewPendigForTests = dao.checkIfTestPreviewedByFaculty(searchBean);
//			new logic
			ArrayList<TestBean> testsToLive = dao.getTestsToMakeLiveBySearchBean(searchBean);
									String errorMsg = dao.checkIfQuestionsConfigured(testsToLive);
									if(!errorMsg.equalsIgnoreCase("")) {
										request.setAttribute("error", "true");
										request.setAttribute("errorMessage", errorMsg);
										m.addAttribute("searchBean", searchBean);	
										return makeTestLiveForm(request, response, m);
									}
			previewPendigForTests = dao.checkIfTestPreviewedByFacultyByQuestions(testsToLive,request);
			
			try{
				if(request.getAttribute("error").equals("true")) {
					m.addAttribute("searchBean", searchBean);	
					return makeTestLiveForm(request, response, m);
				}
			}catch(Exception e) {}
			//check for question configuration erros
		}
		catch(NullPointerException e) {
			
			errorMessage = e.getMessage();
		}
		catch(Exception e) {
			
			errorMessage = "Error in fetching preview tests.";
		}
		
		try {
//			temporarily removing config check
//			errorMessageForConfigCheck = dao.checkIfQuestionsAndQuestionConfigMatches(searchBean);
		}catch(Exception e) {
			
			errorMessage = "Error in fetching configuration for tests.";
		}
		if(StringUtils.isBlank(errorMessage)) {
			if(previewPendigForTests.size() > 0) {
				if(StringUtils.isBlank(errorMessageForConfigCheck))
					

					errorMessage = "Kindly Preview Tests " + previewPendigForTests + " before Current Config can be made live.";
				else
					errorMessage = errorMessageForConfigCheck + "<br/> Kindly Preview Tests " + previewPendigForTests + " before Current Config can be made live.";
			}
			else if(previewPendigForTests.size() == 0 && !StringUtils.isBlank(errorMessageForConfigCheck)) {
				errorMessage = errorMessageForConfigCheck ;
			}
			else {
				errorMessage = insertMakeTestsLiveConfigs(searchBean);
			}
		}*/
		
		errorMessage = insertMakeTestsLiveConfigsForLeads(searchBean);
			

		if(StringUtils.isBlank(errorMessage)) {
			request.setAttribute("success","true");
			request.setAttribute("successMessage", " Test is Live for "+ 
					": Type : "+searchBean.getLiveType()+  
					"; ApplicableType : "+searchBean.getApplicableType()+
					"; acadsMonth : "+searchBean.getAcadMonth()+ 
					"; acadsYear : "+searchBean.getAcadYear()+ 
					"; examMonth : "+searchBean.getExamMonth()+
					"; examYear : "+searchBean.getExamYear());
			m.addAttribute("searchBean", searchBean);	

			/*ArrayList<TestBean> testsBySearchBean = dao.getTestsByExamYearMonthAcadsYearMonthReferenceId(searchBean);
			
			for(TestBean test : testsBySearchBean ) {
				setTestQuestionsInRedisByTestId(test.getId());
			}*/
			return makeTestLiveFormForLeads(request, response, m);
		}else {
			request.setAttribute("error", "true");
			request.setAttribute("errorMessage", errorMessage);
			m.addAttribute("searchBean", searchBean);	
			return makeTestLiveFormForLeads(request, response, m);
		}
	}
	

	private String insertMakeTestsLiveConfigsForLeads(TestExamBean test) {
		TestDAO dao = (TestDAO)act.getBean("testDao");
		
		List<Long> configIds = getConfigIdsForTestLiveSettings(dao,test);
		return dao.insertTestLiveConfigForLeads(test,configIds);
	}
	
	
	@RequestMapping(value = "/admin/viewAllTestsForLeads", method =  RequestMethod.GET)
	public String viewAllTestsForLeads(HttpServletRequest request, HttpServletResponse response, Model m) {

		TestDAO dao = (TestDAO)act.getBean("testDao");
		//if(!m.containsAttribute("allTests")) {
			List<TestExamBean> allTests = dao.getAllTestsForLeads();
			m.addAttribute("allTests", addConsumerProgramProgramStructureNameToEachTest(dao,allTests));
		//}
		m.addAttribute("subjectList", getSubjectList());
		m.addAttribute("programList", getProgramList());
		m.addAttribute("searchBean", new TestExamBean());

		m.addAttribute("programList", getProgramList());
		m.addAttribute("facultyList", getFacultyList());
		m.addAttribute("consumerType", getConsumerTypeList());


		return "test/viewAllTestsForLeads";
	}
	

	@RequestMapping(value = "/deleteTestForLeads", method =  RequestMethod.GET)
	public String deleteTestForLeads(HttpServletRequest request, HttpServletResponse response, Model m,
			@RequestParam("id") Long id)  {
		TestDAO dao = (TestDAO)act.getBean("testDao");
		int deleted = dao.deleteTestForLeads(id);
		if(deleted > -1) {
			request.setAttribute("success", "true");
			request.setAttribute("successMessage", "Test Deleted Successfully.");

		}
		else {
			request.setAttribute("error", "true");
			request.setAttribute("errorMessage", "Eror in deleting test.");

		}
		return viewAllTestsForLeads(request, response, m);
	}
	
	@RequestMapping(value = "/testIAAnswersErrorAnalysis", method =  RequestMethod.GET)
	public String testIAAnswersErrorAnalysis(HttpServletRequest request, HttpServletResponse response, Model m) {
		return "test/testIAAnswersErrorAnalysis";
	}
	
	@RequestMapping(value = "/api/getAnswerInCacheNDBMismatch", method = RequestMethod.POST)
	public ResponseEntity<List<StudentsTestDetailsExamBean>> getAnswerInCacheNDBMismatch(){
		HttpHeaders headers = new HttpHeaders();
		headers.add("Content-Type", "application/json");
		TestDAO dao = (TestDAO)act.getBean("testDao");
		List<StudentsTestDetailsExamBean>studentTest = dao.getAnswerInCacheNDBMismatch();
		return new ResponseEntity<List<StudentsTestDetailsExamBean>>(studentTest,headers, HttpStatus.OK);
	}


	@RequestMapping(value = "/deleteTestAttemptDataBySapidAndTestIdAndAttempt", method =  RequestMethod.GET)
	public String deleteTestAttemptDataBySapidAndTestIdAndAttempt(HttpServletRequest request,
			HttpServletResponse response,
			Model m,
			@RequestParam("sapid") String sapid,
			@RequestParam("testId") Long testId,
			@RequestParam("attempt") Integer attempt)  {
		
		TestDAO dao = (TestDAO)act.getBean("testDao");
		
		String errorMessage = dao.deleteTestAttemptDataBySapidAndTestIdAndAttempt(sapid,testId,attempt);
		
		if(StringUtils.isBlank(errorMessage)) {
			request.setAttribute("success", "true");
			request.setAttribute("successMessage", "Test Attempt Deleted Successfully.");

		}
		else {
			request.setAttribute("error", "true");
			request.setAttribute("errorMessage", "Eror in deleting test attempt data. Error : "+errorMessage);

		}
		
		return viewTestDetails(request,response,m,testId);
	}

	
	@RequestMapping(value = "/editSectionForm", method = RequestMethod.GET)
	public String editSection(HttpServletRequest request,Model m,
			@RequestParam("id") String templateId ,RedirectAttributes redirectAttrs){
		
		 try {  
			 
			ArrayList<SectionBean> sectionList = tDao.getSectionsFromTemplate(templateId);
			m.addAttribute("sectionQnTypes", tDao.getSectionQuestionTypes());
			for(SectionBean section : sectionList) {
				ArrayList<SectionBean> configList = tDao.getSectionQuestionConfig(section.getId());
				section.setSectionQnTypeConfigbean(configList);
			}
			m.addAttribute("sectionList", sectionList);   
			m.addAttribute("templateId", templateId); 
			SectionBean section = new SectionBean();
			m.addAttribute("section", section);  
			redirectAttrs.addFlashAttribute("success", "true");
			redirectAttrs.addFlashAttribute("successMessage", "Template Created Successfully.");
			 
		} catch (Exception e) {
			 
			redirectAttrs.addFlashAttribute("error", "true"); 
			redirectAttrs.addFlashAttribute("errorMessage", "Failed to Create Template.");
			
		}  
		 
		 return "test/editSectionsForTemplate"; 
	} 
	@RequestMapping(value = "/deleteSectionbyId", method = RequestMethod.POST)
	public String deleteSectionbyId(HttpServletRequest request,Model m,
			@RequestBody SectionBean section ){ 
		try {
			tDao.deleteSectionbyId(section.getId());
		} catch (Exception e) { 
			
		}  
		return "test/editSectionsForTemplate";    
	}  
	//edit section.
	//add new section to existing template
	@RequestMapping(value = "/saveSectionsForTemplate", method = RequestMethod.POST)
	public RedirectView saveSectionsForTemplate(HttpServletRequest request,Model m,
			@ModelAttribute SectionBean section ){  
		 ArrayList<SectionBean> bean = new ArrayList<SectionBean>();
		 
		try {
			bean.add(section);  
			tDao.saveSectionsForTemplate(bean);  
			TestQuestionConfigBean templateBean = tDao.getTemplateById(section.getTemplateId()).get(0);
			templateBean.setSectionBean(bean);
			//save section to template table
			tDao.saveToTestQnConfigTemplate(templateBean);
		} catch (Exception e) { 
			
		} 
		
		return new RedirectView("editSectionForm?id="+section.getTemplateId());         
	}  
	
	
	public String addIAANSAttemptsByTestId(Long testId) {
		//1. get ans students by testid
		System.out.println("IN addIAANSAttemptsByTestId testId : "+testId);
		String errorMessage="";
		List<StudentsTestDetailsExamBean> testDetailsANS = tDao.getIAANSAttemptsByTestId(testId);
		
		//2. save 
		for(StudentsTestDetailsExamBean testDetail  : testDetailsANS) {
		  Long id =	tDao.saveStudentsTestDetails(testDetail);
		  if(id.equals(new Long(0))){
			  errorMessage = errorMessage +" Sapid : "+testDetail.getSapid()+" - testId : "+testDetail.getTestId()+". ";
		  }
		}
		
		if(!"".equals(errorMessage)) {
			errorMessage = "Error in saving IA ANS entries for :  " + errorMessage;
		}
		
		
		return errorMessage;
	} 

	@RequestMapping(value = "/grantFacultyEditRights", method = RequestMethod.GET )
	public ModelAndView grantFacultyEditRights(HttpServletRequest request, @RequestParam("testId") Long testId ){  

		ModelAndView modelAndView= new ModelAndView("test/facultyAccess");
		/*
		 * if faculty edit rights date > result declared date - then allow
		 * if result declared date > faculty rights date - don't allow
		 * */
		TestExamBean test = tDao.getTestById(testId);
		List<StudentQuestionResponseExamBean> facultyAndAllocatedAnswer = new ArrayList<>();

		try {
			facultyAndAllocatedAnswer = tDao.getFacultyAndAnswerDetailsForTestId(testId);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		modelAndView.addObject("test", test);
		modelAndView.addObject("facultyAndAllocatedAnswer", facultyAndAllocatedAnswer);

		return modelAndView;  

	}

	/**
	 * Details required for Benefit of Doubt are added as Model attributes.
	 * @param request - HttpServletRequest
	 * @param testId - ID of the test
	 * @param model - Object to add page attributes
	 * @return JSP path
	 */
	@GetMapping(value = "/admin/iaBenefitOfDoubt")
	public String iaBenefitOfDoubt(HttpServletRequest request, @RequestParam final Long testId, Model model) {
		String userId = (String) request.getSession().getAttribute("userId");
		model.addAttribute("userName", userId);
		model.addAttribute("testId", testId);
		model.addAttribute("maxAttempts", iaTestService.testMaxAttempts(testId));						//Max attempts of test
		
		model.addAttribute("sectionQuestionMap", iaTestService.sectionQuestionsList(testId));			//Map of section-wise questions list
		model.addAttribute("bodQuestionIdList", iaTestService.bodAppliedQuestions(testId));				//Benefit of Doubt applied question IDs list
		model.addAttribute("testCompleted", iaTestService.checkIfTestCompleted(testId));				//Check if test is completed
		model.addAttribute("resultsLive", iaTestService.checkIfTestResultsLive(testId));				//Check if test results are live
		
		return "test/benefitOfDoubt";
	}
	
	/**
	 * Creates the question attempt report as an Excel file.
	 * The created Excel file is download at the users end.
	 * Sheet 1 of the excel contains details about each student attempts.
	 * Sheet 2 contains data count of how many students attempted the test and selected which options.
	 * @param testId - ID of the test
	 * @param questionId - ID of the test question
	 * @param questionType - type of question
	 * @param testMaxAttempts - max attempts of test
	 * @return AbstractXlsxStreamingView class file which creates and downloads the Excel file 
	 */
	@GetMapping(value = "/admin/downloadQuestionAttemptsReport")
	public ModelAndView downloadQuestionAttemptsReport(@RequestParam final Long testId, @RequestParam final Long questionId, @RequestParam final Integer questionType, 
														@RequestParam final Integer testMaxAttempts) {
		List<StudentQuestionResponseExamBean> questionAttemptsList = iaTestService.questionDetails(testId, questionId, questionType);					//Student question attempt details
		Map<String, Integer> questionAttemptCountMap = iaTestService.questionAttemptDataMap(testId, questionId, questionType, testMaxAttempts);			//Map containing attempt count details
		Map<String, Integer> optionSelectCountMap = iaTestService.optionSelectedDataMap(testId, questionId, questionType);								//Map containing option selection details
		
		Map<String, Object> questionDataMap = new HashMap<>();
		questionDataMap.put("questionType", questionType);
		questionDataMap.put("attemptList", questionAttemptsList);
		questionDataMap.put("questionAttemptCount", questionAttemptCountMap);
		questionDataMap.put("selectedOptionCount", optionSelectCountMap);
		
		return new ModelAndView("iaBenefitOfDoubtQuestionAttemptsExcelView", questionDataMap);
	}
}
