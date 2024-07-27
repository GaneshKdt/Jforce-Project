package com.nmims.controllers;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.RandomStringUtils;
import org.apache.commons.lang.StringUtils;
import org.codehaus.jackson.JsonParseException;
import org.codehaus.jackson.map.JsonMappingException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.commons.CommonsMultipartFile;
import org.springframework.web.servlet.ModelAndView;

import com.google.common.base.Throwables;
import com.nmims.beans.AssignmentFileBean;
import com.nmims.beans.AssignmentFilesSetbean;
import com.nmims.beans.AssignmentLiveSetting;
import com.nmims.beans.AssignmentStatusBean;
import com.nmims.beans.CenterExamBean;
import com.nmims.beans.ConsumerProgramStructureExam;
import com.nmims.beans.ConsumerType;
import com.nmims.beans.ExamAssignmentResponseBean;
import com.nmims.beans.ExamOrderExamBean;
import com.nmims.beans.FacultyExamBean;
import com.nmims.beans.FileBean;
import com.nmims.beans.Page;
import com.nmims.beans.Person;
import com.nmims.beans.ProgramSubjectMappingExamBean;
import com.nmims.beans.ReadCopyCasesListBean;
import com.nmims.beans.ResponseBean;
import com.nmims.beans.ResponseListBean;
import com.nmims.beans.ResultDomain;
import com.nmims.beans.StudentExamBean;
import com.nmims.beans.StudentMarksBean;
import com.nmims.beans.WebCopycaseBean;
import com.nmims.daos.AssignmentsDAO;
import com.nmims.daos.ExamBookingDAO;
import com.nmims.daos.FacultyDAO;
import com.nmims.daos.PassFailDAO;
import com.nmims.daos.StudentMarksDAO;
import com.nmims.factory.CopyCaseFactory;
import com.nmims.helpers.AmazonS3Helper;
import com.nmims.helpers.CopyCaseHelper;
import com.nmims.helpers.EmailHelper;
import com.nmims.helpers.ExcelHelper;
import com.nmims.interfaces.CopyCaseInterface;
import com.nmims.interfaces.IEmbaPassFailReportService;
import com.nmims.services.AssignmentService;
import com.nmims.services.StudentService;
import com.nmims.views.AssignmentWebCopyCaseReportExcelView;
import com.sun.org.apache.xalan.internal.xsltc.compiler.sym;


/**
 * Handles requests for the application home page.
 */
@Controller
@RequestMapping("/admin")
public class AssignmentController extends BaseController{

	@Autowired
	ApplicationContext act;

	@Autowired
	EmailHelper emailHelper;

	@Autowired
	CopyCaseHelper copyCaseHelper;

	@Autowired
	StudentService studentService;
	
	@Autowired
	AssignmentService asgService;
	
	@Autowired
	CopyCaseFactory CCFactory;
	
	@Value( "${ASSIGNMENT_FILES_PATH}" )
	private String ASSIGNMENT_FILES_PATH;
	
	@Value( "${ASSIGNMENT_REPORTS_GENERATE_PATH}" )
	private String ASSIGNMENT_REPORTS_GENERATE_PATH;

	@Value( "${MAX_ASSIGNMENTS_PER_FACULTY}" )
	private String MAX_ASSIGNMENTS_PER_FACULTY;
	
	@Value("${CURRENT_ACAD_MONTH}")
	private String CURRENT_ACAD_MONTH;
	
	@Value("${CURRENT_ACAD_YEAR}")
	private String CURRENT_ACAD_YEAR;
	
	@Value("${CURRENT_EXAM_MONTH}")
	private String CURRENT_EXAM_MONTH;
			
	@Value("${CURRENT_EXAM_YEAR}")
	private String CURRENT_EXAM_YEAR;
	
	@Value("#{'${ACAD_YEAR_LIST}'.split(',')}")
	private List<String> ACAD_YEAR_LIST;
	
	@Value("#{'${ACAD_MONTH_LIST}'.split(',')}") 
	private List<String> ACAD_MONTH_LIST; 
	
	@Value("${LOCAL_ACTIVE_PROCESSORS}")
	private int LOCAL_ACTIVE_PROCESSORS;
	
	@Value("#{'${EXAM_MONTH_LIST}'.split(',')}")
	private List<String> EXAM_MONTH_LIST;
	
	private static final int BUFFER_SIZE = 4096;
	private static final Logger logger = LoggerFactory.getLogger(AssignmentController.class);
	private static final Logger assignmentCopyCaseLogger = LoggerFactory.getLogger("assignmentCopyCase");
	private static final Logger assignmentMakeLive = LoggerFactory.getLogger("assignmentMakeLive");
	@Value("#{'${CURRENT_YEAR_LIST}'.split(',')}")
	private ArrayList<String> currentYearList; 
	
	private ArrayList<String> subjectList = null; 
	private ArrayList<ConsumerType> consumerTypeList = null; 
	private final int pageSize = 20;
	private HashMap<String, CenterExamBean> centerCodeNameMap = null; 
	private ArrayList<ProgramSubjectMappingExamBean> programSubjectMappingList = null;
	HashMap<String, FacultyExamBean> facultyMap = new HashMap<>();
	
	private List<String> usersWithNullOrEmptyAuthorizedCodes =  new ArrayList<String>(Arrays.asList("sanketpanaskar","nmscemuexam")); 
	
	@Autowired
	AmazonS3Helper amazonS3Helper;
	
	@Autowired
	AssignmentWebCopyCaseReportExcelView AssignmentWebCopyCaseReportExcelView;
	
	@Autowired
	IEmbaPassFailReportService EmbaPassFailReportService;
	
	@ModelAttribute("marksOptionsList")
	public ArrayList<String> getMarksOptionsList(){
		return new ArrayList<String>(Arrays.asList( "0","1", "2","3","4","5","6","7","8","9","10","11","12","13","14","15","16","17","18","19","20")); 
	}
	/**
	 * Refresh Cache function to refresh cache of AssignmentController
	 * @param 
	 * none
	 * @return 
	 * none
	 * */
	public String RefreshCache() {
		programSubjectMappingList = null;
		getProgramSubjectMappingList();	
		
		subjectList = null;
		consumerTypeList = null;
		getSubjectList();
		
		
		
		
		return null;
	}

	public ArrayList<ProgramSubjectMappingExamBean> getProgramSubjectMappingList(){
		if(this.programSubjectMappingList == null || this.programSubjectMappingList.size() == 0){
			ExamBookingDAO eDao = (ExamBookingDAO)act.getBean("examBookingDAO");
			this.programSubjectMappingList = eDao.getProgramSubjectMappingList();
		}
		return programSubjectMappingList;
	}


	public HashMap<String, CenterExamBean> getCentersMap(){
		StudentMarksDAO dao = (StudentMarksDAO)act.getBean("studentMarksDAO");
		ArrayList<CenterExamBean> centers = dao.getAllCenters();
		centerCodeNameMap = new HashMap<>();
		for (int i = 0; i < centers.size(); i++) {
			CenterExamBean cBean = centers.get(i);
			centerCodeNameMap.put(cBean.getCenterCode(), cBean);
		}
		return centerCodeNameMap;
	}

	public ArrayList<String> getSubjectList(){
		if(this.subjectList == null){
			StudentMarksDAO dao = (StudentMarksDAO)act.getBean("studentMarksDAO");
			this.subjectList = dao.getActiveSubjects();
		}
		return subjectList;
	}

	public HashMap<String, FacultyExamBean> getFacultyMap(){
		FacultyDAO dao = (FacultyDAO)act.getBean("facultyDAO");
		this.facultyMap = dao.getFacultiesMap();
		return facultyMap;
	}

	public HashMap<String, String> getFacultyList(){
		FacultyDAO dao = (FacultyDAO)act.getBean("facultyDAO");
		HashMap<String, String> map = dao.getFacultyIdNameMap();
		return (HashMap<String, String>)sortByValue(map);
	}
	
	// get Consumer Type List

	

	public AssignmentController(){
	}

	@RequestMapping(value="/InsertIntoAssignments",method=RequestMethod.GET)
	public ModelAndView insertIntoNewAssignments() {
	AssignmentsDAO dao = (AssignmentsDAO)act.getBean("asignmentsDAO");
	
	ArrayList<AssignmentFileBean> assignments = dao.getAssignmentsData();
	
	
	AssignmentFileBean assignments_tmp;
	int count = 0;
	for(int i=0;i < assignments.size();i++) {
		assignments_tmp = assignments.get(i);
		ArrayList<String> consumerIds = dao.getConsumerProgramStructureIdsNew(assignments_tmp.getSubject());
		for(int j=0;j < consumerIds.size();j++) {
			assignments_tmp.setConsumerProgramStructureId(consumerIds.get(j).toString());
			dao.insertIntoAssignments_Backup(assignments_tmp);
		}
		
	}
	return new ModelAndView("RequestTesting");
}
	
	@RequestMapping(value="/InsertIntoProgramSemSubject",method=RequestMethod.GET)
	public ModelAndView insertIntoProgramSemSubject() {
	AssignmentsDAO dao = (AssignmentsDAO)act.getBean("asignmentsDAO");
	//Get all data from program_subject table
	ArrayList<ProgramSubjectMappingExamBean> PSMbean =  dao.getProgramSubjectData();
	//Get all data from program_sem_subject table
	ArrayList<String> PSSbean =  dao.getProgramSemSubjectData();
	ProgramSubjectMappingExamBean PSMbean2;
	for(int i=0;i < PSMbean.size();i++) {
	PSMbean2 = PSMbean.get(i);
	//For every record in program_subject table get list of masterkey ids
	ArrayList<String> consumerProgramStructureIds = dao.getConsumerTypeId(PSMbean2.getProgram(), PSMbean2.getPrgmStructApplicable());
	//For every masterKey id enter record into program_sem_subject if it does not exist
	for(int j=0;j < consumerProgramStructureIds.size();j++) {
	PSMbean2.setConsumerProgramStructureId(consumerProgramStructureIds.get(j));
	String ps_key = PSMbean2.getConsumerProgramStructureId()+"|"+PSMbean2.getSubject()+"|"+PSMbean2.getSem();	
			if(!PSSbean.contains(ps_key)){
				PSMbean2.setCreatedBy("System");
				PSMbean2.setLastModifiedBy("System");
				dao.insertIntoProgramSemSubject(PSMbean2);
			}
		}
	}
	return new ModelAndView("RequestTesting");
}

	@RequestMapping(value = "/uploadCopyCasesForm", method = {RequestMethod.GET, RequestMethod.POST})
	public String uploadCopyCasesForm(HttpServletRequest request, HttpServletResponse response, Model m) {
		logger.info("Add New Company Page");

		FileBean fileBean = new FileBean();
		m.addAttribute("fileBean",fileBean);
		m.addAttribute("yearList", currentYearList);
		return "uploadCopyCases";
	}

	@RequestMapping(value = "/uploadCopyCases", method = {RequestMethod.GET, RequestMethod.POST})
	public ModelAndView uploadCopyCases(FileBean fileBean, BindingResult result,HttpServletRequest request, Model m){
		ModelAndView modelnView = new ModelAndView("uploadCopyCases");
		try{
			StudentMarksDAO dao = (StudentMarksDAO)act.getBean("studentMarksDAO");
			String userId = (String)request.getSession().getAttribute("userId");
			ExcelHelper excelHelper = new ExcelHelper();
			ArrayList<List> resultList = excelHelper.readCopyCasesExcel(fileBean);
			//List<StudentMarksBean> marksBeanList = excelHelper.readMarksExcel(fileBean, programList, subjectList);

			List<StudentMarksBean> marksBeanList = (ArrayList<StudentMarksBean>)resultList.get(0);
			List<StudentMarksBean> errorBeanList = (ArrayList<StudentMarksBean>)resultList.get(1);

			if(errorBeanList.size() > 0){
				request.setAttribute("errorBeanList", errorBeanList);
				return modelnView;
			}


			ArrayList<String> errorList = dao.batchUpdate(marksBeanList, "Assignment");

			if(errorList.size() == 0){
				request.setAttribute("success","true");
				request.setAttribute("successMessage",marksBeanList.size() +" rows out of "+ marksBeanList.size()+" inserted successfully.");
			}else{
				request.setAttribute("error", "true");
				request.setAttribute("errorMessage", errorList.size() + " records were NOT inserted. Please see row number of rows not inserted. Error row numbers "+errorList);
			}

		}catch(Exception e){
			
			request.setAttribute("error", "true");
			request.setAttribute("errorMessage", "Error in inserting marks records.");

		}
		fileBean = new FileBean();
		m.addAttribute("fileBean",fileBean);
		m.addAttribute("yearList", currentYearList);
		return modelnView;
	}

	@RequestMapping(value = "/uploadAssignmentFilesForm", method = {RequestMethod.GET, RequestMethod.POST})
	public String uploadAssignmentFilesForm(HttpServletRequest request, HttpServletResponse response, Model m) {

		AssignmentsDAO dao = (AssignmentsDAO)act.getBean("asignmentsDAO");
		AssignmentFilesSetbean  filesSet = new AssignmentFilesSetbean();
		m.addAttribute("filesSet",filesSet);
		m.addAttribute("yearList", currentYearList);
//		m.addAttribute("subjectList", getSubjectList());
		m.addAttribute("consumerType", dao.getConsumerTypeList());
		

		return "uploadAssignmentFiles";
	}
	
	//Fill Programs , Programs Structure , Subject DropDown Data Based On ConsumerType Selected

	@RequestMapping(value = "/getDataByConsumerType",  method = RequestMethod.POST , consumes="application/json", produces="application/json")
	public ResponseEntity<ResponseBean> getDataByConsumerType(@RequestBody ConsumerProgramStructureExam consumerProgramStructure){
	
		
		ResponseBean response = (ResponseBean) new ResponseBean();
		
		HttpHeaders headers = new HttpHeaders();
		headers.add("Content-Type", "application/json");
		try {
			AssignmentsDAO dao = (AssignmentsDAO)act.getBean("asignmentsDAO");
			
			ArrayList<ConsumerProgramStructureExam> programStructureData = dao.getProgramStructureByConsumerType(consumerProgramStructure.getId());
			ArrayList<ConsumerProgramStructureExam> programData = dao.getProgramByConsumerType(consumerProgramStructure.getId());
			
			String programDataId = "";
			for(int i=0;i < programData.size();i++){
				programDataId = programDataId + ""+ programData.get(i).getId() +",";
			}
			programDataId = programDataId.substring(0,programDataId.length()-1);
			
			String programStructureDataId = "";
			for(int i=0;i < programStructureData.size();i++){
				programStructureDataId = programStructureDataId + ""+ programStructureData.get(i).getId() +",";
			}
			programStructureDataId = programStructureDataId.substring(0,programStructureDataId.length()-1);
			
			response.setStatus("success");
			response.setProgramStructureData(programStructureData);
			response.setProgramsData(programData);
			response.setSubjectsData(dao.getSubjectByConsumerType(consumerProgramStructure.getId(),programDataId,programStructureDataId));
		

		} catch (Exception e) {
			
			response.setStatus("fail");
		}
		

		return new ResponseEntity(response, HttpStatus.OK);	
		
	}
	
	//Fill Programs , Subject DropDown Data Based On ConsumerType Selected
	
	@RequestMapping(value = "/getDataByProgramStructure",  method = RequestMethod.POST , consumes="application/json", produces="application/json")
	public ResponseEntity<ResponseBean> getDataByProgramStructure(@RequestBody ConsumerProgramStructureExam consumerProgramStructure){
	
		
		ResponseBean response = (ResponseBean) new ResponseBean();
		
		HttpHeaders headers = new HttpHeaders();
		headers.add("Content-Type", "application/json");
		try {
			AssignmentsDAO dao = (AssignmentsDAO)act.getBean("asignmentsDAO");
			
		
			ArrayList<ConsumerProgramStructureExam> programData = dao.getProgramByConsumerTypeAndPrgmStructure(consumerProgramStructure.getConsumerTypeId(),consumerProgramStructure.getProgramStructureId());
			
			String programDataId = "";
			for(int i=0;i < programData.size();i++){
				programDataId = programDataId + ""+ programData.get(i).getId() +",";
			}
			programDataId = programDataId.substring(0,programDataId.length()-1);
			response.setStatus("success");
			
			
			response.setProgramsData(programData);
			response.setSubjectsData(dao.getSubjectByConsumerType(consumerProgramStructure.getConsumerTypeId(),programDataId,consumerProgramStructure.getProgramStructureId()));
		

		} catch (Exception e) {
			
			response.setStatus("fail");
		}
		

		return new ResponseEntity(response, HttpStatus.OK);	
		
	}
	
	@RequestMapping(value = "/getDataByProgram",  method = RequestMethod.POST , consumes="application/json", produces="application/json")
	public ResponseEntity<ResponseBean> getDataByProgram(@RequestBody ConsumerProgramStructureExam consumerProgramStructure){
	
		ResponseBean response = (ResponseBean) new ResponseBean();
		HttpHeaders headers = new HttpHeaders();
		headers.add("Content-Type", "application/json");
		try {
			AssignmentsDAO dao = (AssignmentsDAO)act.getBean("asignmentsDAO");
			response.setStatus("success");
			response.setSubjectsData(dao.getSubjectByConsumerType(consumerProgramStructure.getConsumerTypeId(),consumerProgramStructure.getProgramId(),consumerProgramStructure.getProgramStructureId()));
		} catch (Exception e) {
			
			response.setStatus("fail");
		}
		

		return new ResponseEntity(response, HttpStatus.OK);	
		
	}
	
	@RequestMapping(value = "/getBatchByMsterKey",  method = RequestMethod.POST , consumes="application/json", produces="application/json")
	public ResponseEntity<ResponseBean> getBatchByProgramStucture(@RequestBody ConsumerProgramStructureExam consumerProgramStructure){
		
		ResponseBean response = (ResponseBean) new ResponseBean();
		HttpHeaders headers = new HttpHeaders();
		headers.add("Content-Type", "application/json");
		
		try {
			 ArrayList<ConsumerProgramStructureExam>batchesByMsterKey= EmbaPassFailReportService.getBatchesByMsterKey(consumerProgramStructure);
			
			response.setStatus("success");
			response.setSubjectsData(batchesByMsterKey);
			return new ResponseEntity<ResponseBean>(response, HttpStatus.OK);
		} catch (Exception e) {
			response.setStatus("fail");
			return new ResponseEntity<ResponseBean>(response, HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}
	
	@RequestMapping(value = "/getConsumerTypeByExamYearMonth",  method = RequestMethod.POST , consumes="application/json", produces="application/json")
	public ResponseEntity<ResponseBean> getConsumerTypeByExamYearMonth(@RequestBody ConsumerProgramStructureExam consumerProgramStructure){
		
		ResponseBean response = (ResponseBean) new ResponseBean();
		HttpHeaders headers = new HttpHeaders();
		headers.add("Content-Type", "application/json");
		
		try {
			List<String> masterKeyList=EmbaPassFailReportService.getMsterKeysByBatch(consumerProgramStructure);
			if(masterKeyList.isEmpty()) {
				response.setStatus("masterKeyNotFound");
				return new ResponseEntity<ResponseBean>(response, HttpStatus.NOT_FOUND);
			}else {
			Map<String,ArrayList<ConsumerProgramStructureExam>> searchMap=EmbaPassFailReportService.getConsumerTypeByExamYearMonth(masterKeyList);
			ArrayList<ConsumerProgramStructureExam> consumerTypeList = searchMap.get("consumerTypeList");
			ArrayList<ConsumerProgramStructureExam> programStructurList = searchMap.get("programStructurList");
			ArrayList<ConsumerProgramStructureExam> programList = searchMap.get("programList");
			
			
			response.setStatus("success");
			response.setSubjectsData(consumerTypeList);
			response.setProgramStructureData(programStructurList);
			response.setProgramData(programList);
			return new ResponseEntity<ResponseBean>(response, HttpStatus.OK);
			}
		}
		
		catch (Exception e) {
			response.setStatus("fail");
			return new ResponseEntity<ResponseBean>(response, HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}
	

	
/// For Inserting Program_sem_Subject Table
	/*@RequestMapping(value="/InsertIntoProgramSemSubject",method=RequestMethod.GET)
		public ModelAndView insertIntoProgramSemSubject() {
		AssignmentsDAO dao = (AssignmentsDAO)act.getBean("asignmentsDAO");
		ArrayList<ProgramSubjectMappingBean> PSMbean =  dao.getProgramSubjectData();
		ProgramSubjectMappingBean PSMbean2;
		for(int i=0;i < PSMbean.size();i++) {
		PSMbean2 = PSMbean.get(i);
		ArrayList<String> consumerIds = dao.getConsumerTypeId(PSMbean2.getProgram(), PSMbean2.getPrgmStructApplicable());
		for(int j=0;j < consumerIds.size();j++) {
		PSMbean2.setConsumerProgramStructureId(consumerIds.get(j));
		dao.insertIntoProgramSemSubject(PSMbean2);
		}
		}
		return new ModelAndView("RequestTesting");
	}*/
	
	
	/*@RequestMapping(value="/InsertIntoAssignments",method=RequestMethod.GET)
		public ModelAndView insertIntoNewAssignments() {
		AssignmentsDAO dao = (AssignmentsDAO)act.getBean("asignmentsDAO");
		
		ArrayList<AssignmentFileBean> assignments = dao.getAssignmentsData();
		
		
		AssignmentFileBean assignments_tmp;
		int count = 0;
		for(int i=0;i < assignments.size();i++) {
			assignments_tmp = assignments.get(i);
			ArrayList<String> consumerIds = dao.getConsumerProgramStructureIdsNew(assignments_tmp.getSubject());
			for(int j=0;j < consumerIds.size();j++) {
				assignments_tmp.setConsumerProgramStructureId(consumerIds.get(j).toString());
				dao.insertIntoAssignments_Backup(assignments_tmp);
			}
			
		}
		return new ModelAndView("RequestTesting");
	}*/
	
	@RequestMapping(value="/InsertIntoAssignmentsFormNewTable",method=RequestMethod.GET)
	public ModelAndView InsertIntoAssignmentsFormNewTable() {
	AssignmentsDAO dao = (AssignmentsDAO)act.getBean("asignmentsDAO");
	
	ArrayList<AssignmentFileBean> assignments = dao.getNewAssignmentsData();
	
	
	AssignmentFileBean assignments_tmp;
	
	for(int i=0;i < assignments.size();i++) {
		assignments_tmp = assignments.get(i);

			dao.insertIntoNewAssignments_Backup(assignments_tmp);
		
	}
	return new ModelAndView("RequestTesting");
}

	
	


	@RequestMapping(value = "/uploadAssignmentFiles",  method = {RequestMethod.GET, RequestMethod.POST})
	public ModelAndView uploadAssignmentFiles(HttpServletRequest request, HttpServletResponse response, @ModelAttribute AssignmentFilesSetbean filesSet) {
		ModelAndView modelnView = new ModelAndView("uploadAssignmentFiles");

		AssignmentsDAO dao = (AssignmentsDAO)act.getBean("asignmentsDAO");
	
	  
		List<AssignmentFileBean> assignmentFiles = filesSet.getAssignmentFiles();
		List<ResultDomain> qpcopycaseList=new ArrayList<ResultDomain>();
//		AssignmentsDAO dao = (AssignmentsDAO)act.getBean("asignmentsDAO");
		
		int successCount = 0;
		String subjects = "";
		for (int i = 0; i < assignmentFiles.size(); i++) {

			AssignmentFileBean bean = assignmentFiles.get(i);

			String fileName = bean.getFileData().getOriginalFilename();  

			if(fileName == null || "".equals(fileName.trim()) || "".equals(bean.getSubject()) || "".equals(filesSet.getConsumerTypeId())
					||  filesSet.getConsumerTypeId()  == null || bean.getSubject() == null || "".equals(bean.getSubject().trim()))
			{
				
//				request.setAttribute("error", "true");
//				request.setAttribute("errorMessage", "Please Fill All The Data.");
//				modelnView.addObject("filesSet",filesSet);
//				modelnView.addObject("yearList", currentYearList);
//				modelnView.addObject("subjectList", getSubjectList());
//				return modelnView;
				//If no file is selected, do not do any operation.
				continue;
			}
			
			
			String errorMessage = uploadAssignmentFile(bean, filesSet.getYear(), filesSet.getMonth());
			

			//Check if file saved to Disk successfully
			if(errorMessage == null){
				if(filesSet.getProgramId().split(",").length>1|| filesSet.getProgramStructureId().split(",").length>1||filesSet.getConsumerTypeId().split(",").length>1 )
				{
					// If Any Option is Selected Is "All"
				ArrayList<String> consumerProgramStructureIds = dao.getconsumerProgramStructureIdsWithSubject(filesSet.getProgramId(),filesSet.getProgramStructureId(),filesSet.getConsumerTypeId(),bean.getSubject());
			
				
				try {
					List<ResultDomain> qpcopycase=asgService.checkQPCopyCase(bean.getSubject(), bean.getFileData(),consumerProgramStructureIds) ;
					qpcopycase.stream().forEach(x->{
						x.setFirstFile(bean.getQuestionFilePreviewPath());
					});
					qpcopycaseList.addAll(qpcopycase);
					
				} catch (Exception e) {
					// TODO Auto-generated catch block
					logger.error("Error in QP File Copycase check : ",Throwables.getStackTraceAsString(e));
					request.setAttribute("error", "true");
					request.setAttribute("errorMessage", "Error in QP File Copycase check : "+e.getMessage());
					modelnView.addObject("filesSet",filesSet);
					modelnView.addObject("yearList", currentYearList);
					//modelnView.addObject("subjectList", getSubjectList());
					modelnView.addObject("consumerType", dao.getConsumerTypeList());
					
					return modelnView;
				}

				
				String userId = (String)request.getSession().getAttribute("userId");
				
				bean.setCreatedBy(userId);
				bean.setLastModifiedBy(userId);
				bean.setStartDate(filesSet.getStartDate());
				bean.setEndDate(filesSet.getEndDate());
				dao.batchInsertOfAssignmentsIfAll(bean, filesSet.getYear(), filesSet.getMonth(),consumerProgramStructureIds);
				successCount++;
				subjects = subjects + " : " +bean.getSubject() ;	
				}
					
				else {
					
				
				
				String Key = dao.getAssignmentKey(filesSet.getProgramId(),filesSet.getProgramStructureId(), filesSet.getConsumerTypeId());
				
				String userId = (String)request.getSession().getAttribute("userId");
				bean.setConsumerProgramStructureId(Key);
				bean.setCreatedBy(userId);
				bean.setLastModifiedBy(userId);
				bean.setStartDate(filesSet.getStartDate());
				bean.setEndDate(filesSet.getEndDate());
				dao.saveAssignmentDetails(bean, filesSet.getYear(), filesSet.getMonth());
				successCount++;
				subjects = subjects + " : " +bean.getSubject() ;
				ArrayList<String> consumerProgramStructureIds=new ArrayList<String>();
				consumerProgramStructureIds.add(bean.getConsumerProgramStructureId());
				try {
					List<ResultDomain> qpcopycase=asgService.checkQPCopyCase(bean.getSubject(), bean.getFileData(),consumerProgramStructureIds) ;
					qpcopycase.stream().forEach(x->{
						x.setFirstFile(bean.getQuestionFilePreviewPath());
					});
					qpcopycaseList.addAll(qpcopycase);
					
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
					request.setAttribute("error", "true");
					request.setAttribute("errorMessage", "Error in file Upload: "+e.getMessage());
					modelnView.addObject("filesSet",filesSet);
					modelnView.addObject("yearList", currentYearList);
					//modelnView.addObject("subjectList", getSubjectList());
					modelnView.addObject("consumerType", dao.getConsumerTypeList());
					
					return modelnView;
				}
				}
			}else{
				request.setAttribute("error", "true");
				request.setAttribute("errorMessage", "Error in file Upload: "+errorMessage);
				modelnView.addObject("filesSet",filesSet);
				modelnView.addObject("yearList", currentYearList);
				//modelnView.addObject("subjectList", getSubjectList());
				modelnView.addObject("consumerType", dao.getConsumerTypeList());
				
				return modelnView;
			}
		}
		
//		qpcopycaseList.stream().forEach(System.out::println);

		request.setAttribute("success","true");
		request.setAttribute("successMessage",successCount + " Files Uploaded successfully for "+subjects);
		filesSet = new AssignmentFilesSetbean();
		modelnView.addObject("filesSet",filesSet);
		modelnView.addObject("yearList", currentYearList);
		//modelnView.addObject("subjectList", getSubjectList());
		modelnView.addObject("consumerType", dao.getConsumerTypeList());
		modelnView.addObject("qpcopycaseList",qpcopycaseList);
		

		return modelnView;
	}

	private String uploadAssignmentFile(AssignmentFileBean bean, String year, String month) {

		String errorMessage = null;
		InputStream inputStream = null;   
		OutputStream outputStream = null;   

		CommonsMultipartFile file = bean.getFileData(); 
		String fileName = file.getOriginalFilename();   

		//Replace special characters in file
		fileName = fileName.replaceAll("'", "_");
		fileName = fileName.replaceAll(",", "_");
		fileName = fileName.replaceAll("&", "and");
		fileName = fileName.replaceAll(" ", "_");

		fileName = fileName.substring(0, fileName.lastIndexOf(".")) + "_" + RandomStringUtils.randomAlphanumeric(10) + fileName.substring(fileName.lastIndexOf("."), fileName.length());
		
		if(!(fileName.toUpperCase().endsWith(".PDF") ) ){
			errorMessage = "File type not supported. Please upload .pdf file.";
			return errorMessage;
		}
		/*try {   
			inputStream = file.getInputStream();   
			String filePath = ASSIGNMENT_REPORTS_GENERATE_PATH + month + year + "/" +fileName;

			String previewPath = month + year + "/" + fileName;
			//Check if Folder exists which is one folder per Exam (Jun2015, Dec2015 etc.) 
			File folderPath = new File(ASSIGNMENT_REPORTS_GENERATE_PATH  + month + year );
			if (!folderPath.exists()) {   
				folderPath.mkdirs();   
			}   

			File newFile = new File(filePath);   


			outputStream = new FileOutputStream(newFile);   
			int read = 0;   
			byte[] bytes = new byte[1024];   

			while ((read = inputStream.read(bytes)) != -1) {   
				outputStream.write(bytes, 0, read);   
			}
			bean.setFilePath(filePath);
			bean.setQuestionFilePreviewPath(previewPath);
			outputStream.close();
			inputStream.close();
		} catch (IOException e) {   
			errorMessage = "Error in uploading file for "+bean.getSubject() + " : "+ e.getMessage();
			   
		}   catch(Exception e){
			
		}*/
		String folderPath =   month + year + "/" ;
		// Add Random number to avoid student guessing names of other student's
		// assignment files 
		bean.setFilePath(month + year +"/"+fileName);
		bean.setQuestionFilePreviewPath(month + year +"/"+fileName);
		
		HashMap<String,String> s3_response = amazonS3Helper.uploadFiles(file,folderPath,"assignment-files",folderPath+fileName);
		if(!s3_response.get("status").equalsIgnoreCase("success")) {
			errorMessage =  "Error in uploading file "+s3_response.get("fileUrl");
		}
		return errorMessage;
	}
	
	///Make Assignment Live Page
	@RequestMapping(value = "/makeAssignmentLiveForm", method = {RequestMethod.GET, RequestMethod.POST})
	public String makeAssignmentLiveForm(HttpServletRequest request, HttpServletResponse response, Model m) {

		AssignmentsDAO dao = (AssignmentsDAO)act.getBean("asignmentsDAO");
		AssignmentFilesSetbean  filesSet = new AssignmentFilesSetbean();
		m.addAttribute("filesSet",filesSet);
		m.addAttribute("yearList", currentYearList);
		m.addAttribute("assignmentList", dao.getAssignmentList());
		m.addAttribute("acadsYearList", ACAD_YEAR_LIST);
		m.addAttribute("acadsMonthList", ACAD_MONTH_LIST);
		m.addAttribute("consumerType", dao.getConsumerTypeList());
		

		return "makeAssignmentLiveForm";
	}
	
	@RequestMapping(value = "/submitAssignmentLiveData",  method = {RequestMethod.GET, RequestMethod.POST})
	public ModelAndView submitAssignmentLiveData(HttpServletRequest request, HttpServletResponse response, @ModelAttribute final AssignmentFilesSetbean filesSet) {
		ModelAndView modelnView = new ModelAndView("makeAssignmentLiveForm");
		Executor executor = Executors.newFixedThreadPool(LOCAL_ACTIVE_PROCESSORS);
		AssignmentsDAO dao = (AssignmentsDAO)act.getBean("asignmentsDAO");
		assignmentMakeLive.info("---------------------------------Started  Assignment Make Live---------------------------------");
		try
		{
			String userId = (String)request.getSession().getAttribute("userId");
			
			// If Any Option is Selected Is "All"
			ArrayList<String> consumerProgramStructureIds = new ArrayList<String>();
			try {
				consumerProgramStructureIds = dao.getconsumerProgramStructureIds(filesSet.getProgramId(),filesSet.getProgramStructureId(),filesSet.getConsumerTypeId());
			} catch (Exception e1) {
				assignmentMakeLive.error("Exception Error getconsumerProgramStructureIds Error-{} ",e1);
				//e1.printStackTrace();
			}
			assignmentMakeLive.info("Assignmetn Make Live Masterkey's : {}", consumerProgramStructureIds);
			
			try {
				dao.batchInsertOfMakeAssignmentLive(filesSet,consumerProgramStructureIds);
				assignmentMakeLive.info("Successfully Saved Masterkey's in Assignment Live Setting table");
			} catch (Exception e) {
				assignmentMakeLive.error("Exception Error batchInsertOfMakeAssignmentLive Error-{} ",e);
				//e.printStackTrace();
			}
			
			assignmentMakeLive.info("Getting Students List for Assignments Make Live......");
			ArrayList<StudentExamBean> students =  dao.getStudentsApplicableForAssignments(filesSet,consumerProgramStructureIds);
			assignmentMakeLive.info(" Assignment Make Live Students list: {}", students.size());
//			double  i=1;
			
//		    final List<Future<?>> futures = new ArrayList<>();
//		    final AtomicInteger studentCount = new AtomicInteger(1);
//		    List<Integer> completeStatus = (List<Integer>) Arrays.asList(10,20,30,40,50,60,70,80,90,100);
//		    try {
//		    	Future<?> future = executor.submit(
//			
//		    			() -> students.parallelStream().forEach((student) -> {
//				
////	        	assignmentMakeLive.info("entry for student:"+i+"/"+students.size());
////	        	i++;
//		    				int runningCount = studentCount.get();
//		    				studentCount.incrementAndGet();
//		    				assignmentMakeLive.info("Entry for Sapid: {} and count: {}",student.getSapid(),runningCount);
//		    					try {
//									ExamAssignmentResponseBean respons = asgService.getAssignments(student.getSapid(),filesSet.getLiveType());
//									List<AssignmentFileBean>allAssignmentFilesList = respons.getAllAssignmentFilesList(); 
//									ArrayList<String> subjectsNotAllowedToSubmit=respons.getSubjectsNotAllowedToSubmit();
//    		 
//									for(AssignmentFileBean assignment : allAssignmentFilesList) {
//										//String status = assignment.getStatus();  
//										if(subjectsNotAllowedToSubmit!=null ) {
//											if(subjectsNotAllowedToSubmit.contains(assignment.getSubject())){
//												assignment.setStatus("Results Awaited");
//												assignment.setSubmissionAllow("N");
//											}  
//										}
//									}
//									dao.insertIntoQuickAssignments(userId,student.getSapid(),allAssignmentFilesList,filesSet.getExamYear(),filesSet.getExamMonth());
//								} catch (Exception e) {
//									// TODO Auto-generated catch block
//									//e.printStackTrace();
//									assignmentMakeLive.error("Error in Assignment make live Sapid - {} Count - {} Error- {}",student.getSapid(),runningCount,e);
//								}
//					
//		    				
//		    				float completedCount = ((float) runningCount/(float) students.size()) * 100;
//		    				assignmentMakeLive.info(" Assignment make live completed status - {}%",(int) completedCount);
//		    				int countLeft = students.size() - runningCount;
//		    				if(completeStatus.contains((int) completedCount)) {
//		    					assignmentMakeLive.info(" Assignment make live {}% completed || Students inserted entries count = {} || Students left count = {}/{}",(int) completedCount,runningCount,countLeft,students.size());
//		    				}
//		    			})
//		    	);
//		    	futures.add(future);
//		    	for(Future<?> f: futures) {
//		    	  	f.get();
//		    	}
//		    }
//			catch(InterruptedException | ExecutionException ex) {
//				assignmentMakeLive.error("Error Assignemnt Make Live Multi-threading : {}", ex);
//				response.setError(ex.getMessage());
//				return new ResponseEntity<>(response,headers,HttpStatus.INTERNAL_SERVER_ERROR);
//			}
//			finally {
//				executor.shutdown();
//			}
		    
		    
		    List<CompletableFuture<String>> pageContentFutures =
		    		IntStream.range(0, students.size())
		    		.mapToObj(index->
		    					getStudentsAssignment(executor, userId,students.get(index), filesSet, students.size(),index )
		    				)
		    		.collect(Collectors.toList());
		    
		    CompletableFuture<Void> allFutures = CompletableFuture.allOf(
		    		pageContentFutures.toArray(new CompletableFuture[pageContentFutures.size()])
		    		);
		    
		    CompletableFuture<List<String>> allPageContentsFuture = allFutures.thenApply(v ->{
		    	return pageContentFutures.stream()
		    			.map(pageContentFuture -> pageContentFuture.join())
		    			.collect(Collectors.toList());
		    });
		    
		    CompletableFuture<Long> countFuture = allPageContentsFuture.thenApply(pageContents ->{
		    	return pageContents.stream()
		    			.filter(pageContent -> pageContent.contains("CompletableFuture"))
		    			.count();
		    });
		    assignmentMakeLive.info("Number of Web Pages having CompletableFuture keyword - " + 
			        countFuture.get());
			assignmentMakeLive.info("---------------------------------Ended  Assignment Make Live---------------------------------");
			request.setAttribute("success","true");
			request.setAttribute("successMessage", " Assignment is Live");
		}
		catch(Exception e)
		{
			assignmentMakeLive.error("Error in Assignment make live - {}",e);
			request.setAttribute("error", "true");
			request.setAttribute("errorMessage", "Error in Assignment make live : " + e.getMessage());
		}
		
//		filesSet = new AssignmentFilesSetbean();
		modelnView.addObject("filesSet", new AssignmentFilesSetbean());
		modelnView.addObject("yearList", currentYearList);
		modelnView.addObject("acadYearList", ACAD_YEAR_LIST);
		modelnView.addObject("acadMonthList", ACAD_MONTH_LIST);
		modelnView.addObject("assignmentList", dao.getAssignmentList());
		modelnView.addObject("consumerType", dao.getConsumerTypeList());
		return modelnView;
	}
	

	@RequestMapping(value = "/markCopyCasesForm", method = {RequestMethod.GET, RequestMethod.POST})
	public String markCopyCasesForm(HttpServletRequest request, HttpServletResponse response, Model m) {

		AssignmentFileBean searchBean = new AssignmentFileBean();
		m.addAttribute("searchBean",searchBean);
		m.addAttribute("yearList", currentYearList);
		m.addAttribute("subjectList", getSubjectList());

		return "assignment/markCopyCases";
	}
	
	
	@RequestMapping(value = "/markCopyCases", method = {RequestMethod.GET, RequestMethod.POST})
	public ModelAndView markCopyCases(HttpServletRequest request, HttpServletResponse response, @ModelAttribute AssignmentFileBean searchBean){
		ModelAndView modelnView = new ModelAndView("assignment/markCopyCases");
		String sapidlist = "";
		request.getSession().setAttribute("searchBean", searchBean);
		modelnView.addObject("searchBean", searchBean);
		modelnView.addObject("yearList", currentYearList);
		modelnView.addObject("subjectList", getSubjectList());
		try {
			if(searchBean.getSapIdList() == null || "".equalsIgnoreCase(searchBean.getSapIdList())){
				setError(request, "Please enter Student Ids to mark copy cases");
				return modelnView;
			}
			sapidlist = searchBean.getSapIdList();
			assignmentCopyCaseLogger.info("Marking CC Month-Year:{}{} Subject:{} Sapid List:{}", searchBean.getMonth(), searchBean.getYear(), searchBean.getSubject(), sapidlist.replaceAll("(\\r|\\n|\\r\\n)+",","));
			AssignmentsDAO dao = (AssignmentsDAO)act.getBean("asignmentsDAO");
			dao.markCopyCases(searchBean);
			dao.markCopyCasesInQuickTable(searchBean);
			setSuccess(request, "Copy cases marked successfully");
			assignmentCopyCaseLogger.info("Copy cases marked successfully");
		} catch (Exception e) {
			assignmentCopyCaseLogger.error("Exception Error marking Copy Cases Month-Year:{}{} Subject:{} SapidList:{} Error:{}", searchBean.getMonth(), searchBean.getYear(), searchBean.getSubject(), sapidlist.replaceAll("(\\r|\\n|\\r\\n)+",","), e);
			setError(request, "Error in marking Copy Cases");
		}
		return modelnView;
	}

	@RequestMapping(value = "/searchCopyCases", method = {RequestMethod.GET, RequestMethod.POST})
	public ModelAndView searchCopyCases(HttpServletRequest request, HttpServletResponse response, @ModelAttribute AssignmentFileBean searchBean) {
		ModelAndView modelnView = new ModelAndView("assignment/markCopyCases");
		request.getSession().setAttribute("searchBean", searchBean);

		AssignmentsDAO dao = (AssignmentsDAO)act.getBean("asignmentsDAO");
		List<AssignmentFileBean> assignmentFilesList = null;
		try {
			assignmentFilesList = asgService.getMarkedCopyCases(searchBean.getMonth(), searchBean.getYear(), searchBean.getSubject(), searchBean.getSapIdList());
		} catch (Exception e) {
			request.setAttribute("error", "true");
			request.setAttribute("errorMessage", "Error getting records "+e.getMessage());
		}
//		Page<AssignmentFileBean> page = dao.searchCopyCases(1, pageSize, searchBean);
//		List<AssignmentFileBean> assignmentFilesList = page.getPageItems();

		modelnView.addObject("assignmentFilesList", assignmentFilesList);
//		modelnView.addObject("page", page);

		modelnView.addObject("searchBean", searchBean);
		modelnView.addObject("rowCount", assignmentFilesList.size());

		modelnView.addObject("yearList", currentYearList);
		modelnView.addObject("subjectList", getSubjectList());
		if(assignmentFilesList == null || assignmentFilesList.size() == 0){
			request.setAttribute("error", "true");
			request.setAttribute("errorMessage", "No records found.");
		}
		return modelnView;
	}

	@RequestMapping(value = "/downloadCopyCases", method = {RequestMethod.GET, RequestMethod.POST})
	public ModelAndView downloadCopyCases(HttpServletRequest request, HttpServletResponse response, @ModelAttribute AssignmentFileBean searchBean) {

		AssignmentsDAO dao = (AssignmentsDAO)act.getBean("asignmentsDAO");
		List<AssignmentFileBean> assignmentSubmittedList = dao.getCopyCases(searchBean);

		return new ModelAndView("assignmentCopyCasesExcelView","assignmentSubmittedList",assignmentSubmittedList);
	}


	@RequestMapping(value = "/searchAssignmentFilesForm", method = {RequestMethod.GET, RequestMethod.POST})
	public String searchAssignmentFilesForm(HttpServletRequest request, HttpServletResponse response, Model m) {

		AssignmentFileBean searchBean = new AssignmentFileBean();
		AssignmentsDAO adao = (AssignmentsDAO)act.getBean("asignmentsDAO");
		ArrayList<ConsumerProgramStructureExam> consumerType = adao.getConsumerTypeList();
		m.addAttribute("consumerType", consumerType);
		m.addAttribute("searchBean",searchBean);
		m.addAttribute("yearList", currentYearList);
		m.addAttribute("subjectList", getSubjectList());
	
		return "searchAssignmentFiles";
	}
	
	

	@RequestMapping(value = "/searchAssignmentFiles", method = {RequestMethod.POST})
	public ModelAndView searchAssignmentFiles(HttpServletRequest request, HttpServletResponse response, @ModelAttribute AssignmentFileBean searchBean){
		ModelAndView modelnView = new ModelAndView("searchAssignmentFiles");
		request.getSession().setAttribute("searchBean", searchBean);
		try {
		AssignmentsDAO dao = (AssignmentsDAO)act.getBean("asignmentsDAO");
		//new logic added for consumer type/program/program structure
		if(searchBean.getConsumerProgramStructureId() == null) {
			ArrayList<String> consumerProgramStructureIds =new ArrayList<String>();
			if(!StringUtils.isBlank(searchBean.getProgramId()) && !StringUtils.isBlank(searchBean.getProgramStructureId()) && !StringUtils.isBlank(searchBean.getConsumerTypeId())){
				consumerProgramStructureIds = dao.getconsumerProgramStructureIds(searchBean.getProgramId(),searchBean.getProgramStructureId(),searchBean.getConsumerTypeId());
			}
			
			String consumerProgramStructureIdsSaperatedByComma = "";
			if(!consumerProgramStructureIds.isEmpty()){
				for(int i=0;i < consumerProgramStructureIds.size();i++){
					consumerProgramStructureIdsSaperatedByComma = consumerProgramStructureIdsSaperatedByComma + ""+ consumerProgramStructureIds.get(i) +",";
				}
				consumerProgramStructureIdsSaperatedByComma = consumerProgramStructureIdsSaperatedByComma.substring(0,consumerProgramStructureIdsSaperatedByComma.length()-1);
			}
			searchBean.setConsumerProgramStructureId(consumerProgramStructureIdsSaperatedByComma);
		}
		String searchType = request.getParameter("searchType") == null?"distinct":request.getParameter("searchType");
		Page<AssignmentFileBean> page = dao.getAssignmentFilesPage(1, pageSize, searchBean,searchType);
		List<AssignmentFileBean> assignmentFilesList = page.getPageItems();

		AssignmentsDAO adao = (AssignmentsDAO)act.getBean("asignmentsDAO");
		ArrayList<ConsumerProgramStructureExam> consumerType = adao.getConsumerTypeList();
		modelnView.addObject("consumerType", consumerType);
		modelnView.addObject("assignmentFilesList", assignmentFilesList);
		modelnView.addObject("page", page);
		modelnView.addObject("searchType", request.getParameter("searchType"));
		modelnView.addObject("searchBean", searchBean);
		modelnView.addObject("rowCount", page.getRowCount());

		modelnView.addObject("yearList", currentYearList);
		modelnView.addObject("subjectList", getSubjectList());
		if(assignmentFilesList == null || assignmentFilesList.size() == 0){
			request.setAttribute("error", "true");
			request.setAttribute("errorMessage", "No records found.");
		}
	}catch (Exception e) {
		//e.printStackTrace();
		// TODO: handle exception
	}
		return modelnView;
	}
	
	@RequestMapping(value = "/getCommonAssignmentProgramsList", method = {RequestMethod.POST})
	public ResponseEntity<ArrayList<AssignmentFileBean>> getCommonAssignmentProgramsList(@RequestBody AssignmentFileBean assignmentFiles) {
		AssignmentsDAO dao = (AssignmentsDAO)act.getBean("asignmentsDAO");
		return new ResponseEntity<ArrayList<AssignmentFileBean>>(dao.getCommonGroupProgramList(assignmentFiles),HttpStatus.OK);
	}
	
	@RequestMapping(value = "/deleteAssignmentRecordByFilter", method = {RequestMethod.POST})
	public ResponseEntity<HashMap<String, String>> deleteAssignmentRecordByFilter(@RequestBody AssignmentFileBean assignmentFiles) {
		AssignmentsDAO dao = (AssignmentsDAO)act.getBean("asignmentsDAO");
		int rows = dao.deleteAssignmentRecordByFilter(assignmentFiles);
		HashMap<String, String> responseData = new HashMap<String,String>();
		if(rows > 0) {
			responseData.put("status", "success");
			responseData.put("message", "Successfully delete record: " + rows);
		}
		else {
			responseData.put("status", "error");
			responseData.put("message", "Failed to delete record");
		}
		return new ResponseEntity<HashMap<String,String>>(responseData,HttpStatus.OK);
	}
	
	@RequestMapping(value = "/deleteCommongAssignmentRecordByFilter", method = {RequestMethod.POST})
	public ResponseEntity<HashMap<String, String>> deleteCommongAssignmentRecordByFilter(@RequestBody AssignmentFileBean assignmentFiles) {
		AssignmentsDAO dao = (AssignmentsDAO)act.getBean("asignmentsDAO");
		int rows = dao.deleteCommongAssignmentRecordByFilter(assignmentFiles);
		HashMap<String, String> responseData = new HashMap<String,String>();
		if(rows > 0) {
			responseData.put("status", "success");
			responseData.put("message", "Successfully delete record: " + rows);
		}
		else {
			responseData.put("status", "error");
			responseData.put("message", "Failed to delete record");
		}
		return new ResponseEntity<HashMap<String,String>>(responseData,HttpStatus.OK);
	}
	
	@RequestMapping(value = "/searchAllAssignmentFiles", method = {RequestMethod.POST})
	public ModelAndView searchAllAssignmentFiles(HttpServletRequest request, HttpServletResponse response, @ModelAttribute AssignmentFileBean searchBean){
		ModelAndView modelnView = new ModelAndView("searchAssignmentFiles");
		request.getSession().setAttribute("searchBean", searchBean);

		AssignmentsDAO dao = (AssignmentsDAO)act.getBean("asignmentsDAO");
		//new logic added for consumer type/program/program structure
		ArrayList<String> consumerProgramStructureIds = dao.getconsumerProgramStructureIds(searchBean.getProgramId(),searchBean.getProgramStructureId(),searchBean.getConsumerTypeId());
		String consumerProgramStructureIdsSaperatedByComma = "";
	
		for(int i=0;i < consumerProgramStructureIds.size();i++){
			consumerProgramStructureIdsSaperatedByComma = consumerProgramStructureIdsSaperatedByComma + ""+ consumerProgramStructureIds.get(i) +",";
		}
		consumerProgramStructureIdsSaperatedByComma = consumerProgramStructureIdsSaperatedByComma.substring(0,consumerProgramStructureIdsSaperatedByComma.length()-1);
		searchBean.setConsumerProgramStructureId(consumerProgramStructureIdsSaperatedByComma);
		Page<AssignmentFileBean> page = dao.getAssignmentFilesPage(1, pageSize, searchBean,"all");
		List<AssignmentFileBean> assignmentFilesList = page.getPageItems();

		AssignmentsDAO adao = (AssignmentsDAO)act.getBean("asignmentsDAO");
		ArrayList<ConsumerProgramStructureExam> consumerType = adao.getConsumerTypeList();
		modelnView.addObject("consumerType", consumerType);
		modelnView.addObject("assignmentFilesList", assignmentFilesList);
		modelnView.addObject("page", page);
		modelnView.addObject("searchType", request.getParameter("searchType"));
		modelnView.addObject("searchBean", searchBean);
		modelnView.addObject("rowCount", page.getRowCount());

		modelnView.addObject("yearList", currentYearList);
		modelnView.addObject("subjectList", getSubjectList());
		if(assignmentFilesList == null || assignmentFilesList.size() == 0){
			request.setAttribute("error", "true");
			request.setAttribute("errorMessage", "No records found.");
		}
		return modelnView;
	}

	@RequestMapping(value = "/searchAssignmentFilesPage", method = {RequestMethod.GET, RequestMethod.POST})
	public ModelAndView searchAssignmentFilesPage(HttpServletRequest request, HttpServletResponse response){
		ModelAndView modelnView = new ModelAndView("searchAssignmentFiles");
		AssignmentFileBean searchBean = (AssignmentFileBean)request.getSession().getAttribute("searchBean");
		int pageNo = Integer.parseInt(request.getParameter("pageNo"));
		AssignmentsDAO dao = (AssignmentsDAO)act.getBean("asignmentsDAO");
		Page<AssignmentFileBean> page = dao.getAssignmentFilesPage(pageNo, pageSize, searchBean,"distinct");
		List<AssignmentFileBean> assignmentFilesList = page.getPageItems();
		AssignmentsDAO adao = (AssignmentsDAO)act.getBean("asignmentsDAO");
		ArrayList<ConsumerProgramStructureExam> consumerType = adao.getConsumerTypeList();
		modelnView.addObject("consumerType", consumerType);
		modelnView.addObject("assignmentFilesList", assignmentFilesList);
		modelnView.addObject("page", page);

		modelnView.addObject("searchBean", searchBean);
		modelnView.addObject("rowCount", page.getRowCount());

		modelnView.addObject("yearList", currentYearList);
		modelnView.addObject("subjectList", getSubjectList());
		if(assignmentFilesList == null || assignmentFilesList.size() == 0){
			request.setAttribute("error", "true");
			request.setAttribute("errorMessage", "No records found.");
		}
		return modelnView;
	}


	@RequestMapping(value = "/searchAssignmentSubmissionForm", method = {RequestMethod.GET, RequestMethod.POST})
	public ModelAndView searchAssignmentSubmissionForm(HttpServletRequest request, HttpServletResponse response) {
		if(!checkSession(request, response)){
			redirectToPortalApp(response);
			return null;
		}
		ModelAndView modelAndView = new ModelAndView("searchAssignmentSubmission");
		AssignmentFileBean searchBean = new AssignmentFileBean();
		AssignmentsDAO adao = (AssignmentsDAO)act.getBean("asignmentsDAO");
		ArrayList<ConsumerProgramStructureExam> consumerType = adao.getConsumerTypeList();
		modelAndView.addObject("consumerType", consumerType);
		modelAndView.addObject("searchBean",searchBean);
		modelAndView.addObject("yearList", currentYearList);
		modelAndView.addObject("subjectList", getSubjectList());
	

		return modelAndView;
	}

	@RequestMapping(value = "/searchAssignmentSubmission", method = {RequestMethod.GET, RequestMethod.POST})
	public ModelAndView searchAssignmentSubmission(HttpServletRequest request, HttpServletResponse response, @ModelAttribute AssignmentFileBean searchBean){
		ModelAndView modelnView = new ModelAndView("searchAssignmentSubmission");
		request.getSession().setAttribute("searchBean", searchBean);

		AssignmentsDAO dao = (AssignmentsDAO)act.getBean("asignmentsDAO");
		ArrayList<String> consumerProgramStructureIds = dao.getconsumerProgramStructureIds(searchBean.getProgramId(),searchBean.getProgramStructureId(),searchBean.getConsumerTypeId());
		String consumerProgramStructureIdsSaperatedByComma = "";
		for(int i=0;i < consumerProgramStructureIds.size();i++){
			consumerProgramStructureIdsSaperatedByComma = consumerProgramStructureIdsSaperatedByComma + ""+ consumerProgramStructureIds.get(i) +",";
		}
		consumerProgramStructureIdsSaperatedByComma = consumerProgramStructureIdsSaperatedByComma.substring(0,consumerProgramStructureIdsSaperatedByComma.length()-1);
		searchBean.setConsumerProgramStructureId(consumerProgramStructureIdsSaperatedByComma);
		Page<AssignmentFileBean> page = dao.getAssignmentSubmissionPage(1, pageSize, searchBean);
		List<AssignmentFileBean> assignmentFilesList = page.getPageItems();
		ArrayList<ConsumerProgramStructureExam> consumerType = dao.getConsumerTypeList();
		modelnView.addObject("consumerType", consumerType);
		modelnView.addObject("assignmentFilesList", assignmentFilesList);
		modelnView.addObject("page", page);

		modelnView.addObject("searchBean", searchBean);
		modelnView.addObject("rowCount", page.getRowCount());

		modelnView.addObject("yearList", currentYearList);
		modelnView.addObject("subjectList", getSubjectList());
		if(assignmentFilesList == null || assignmentFilesList.size() == 0){
			request.setAttribute("error", "true");
			request.setAttribute("errorMessage", "No records found.");
		}
		return modelnView;
	}

	@RequestMapping(value = "/searchAssignmentSubmissionPage", method = {RequestMethod.GET, RequestMethod.POST})
	public ModelAndView searchAssignmentSubmissionPage(HttpServletRequest request, HttpServletResponse response){
		ModelAndView modelnView = new ModelAndView("searchAssignmentSubmission");
		AssignmentFileBean searchBean = (AssignmentFileBean)request.getSession().getAttribute("searchBean");
		int pageNo = Integer.parseInt(request.getParameter("pageNo"));
		AssignmentsDAO dao = (AssignmentsDAO)act.getBean("asignmentsDAO");
		Page<AssignmentFileBean> page = dao.getAssignmentSubmissionPage(pageNo, pageSize, searchBean);
		List<AssignmentFileBean> assignmentFilesList = page.getPageItems();
		ArrayList<ConsumerProgramStructureExam> consumerType = dao.getConsumerTypeList();
		modelnView.addObject("consumerType", consumerType);
		modelnView.addObject("assignmentFilesList", assignmentFilesList);
		modelnView.addObject("page", page);

		modelnView.addObject("searchBean", searchBean);
		modelnView.addObject("rowCount", page.getRowCount());

		modelnView.addObject("yearList", currentYearList);
		modelnView.addObject("subjectList", getSubjectList());
		if(assignmentFilesList == null || assignmentFilesList.size() == 0){
			request.setAttribute("error", "true");
			request.setAttribute("errorMessage", "No records found.");
		}
		return modelnView;
	}

	@RequestMapping(value = "/searchANS", method = {RequestMethod.GET, RequestMethod.POST})
	public ModelAndView searchANS(HttpServletRequest request, HttpServletResponse response, @ModelAttribute AssignmentFileBean searchBean){
		if(!checkSession(request, response)){
			redirectToPortalApp(response);
			return null;
		}
		
		ModelAndView modelnView = new ModelAndView("searchAssignmentSubmission");
		request.getSession().setAttribute("searchBean", searchBean);
		AssignmentsDAO dao = (AssignmentsDAO)act.getBean("asignmentsDAO");
		ArrayList<String> consumerProgramStructureIds = dao.getConsumerProgramStructureIdsForANS(searchBean.getProgramId(),searchBean.getProgramStructureId(),searchBean.getConsumerTypeId());
		String consumerProgramStructureIdsSaperatedByComma = "";
		
		for(int i=0;i < consumerProgramStructureIds.size();i++){
			consumerProgramStructureIdsSaperatedByComma = consumerProgramStructureIdsSaperatedByComma + ""+ consumerProgramStructureIds.get(i) +",";
		}
		
		consumerProgramStructureIdsSaperatedByComma = consumerProgramStructureIdsSaperatedByComma.substring(0,consumerProgramStructureIdsSaperatedByComma.length()-1);
		searchBean.setConsumerProgramStructureId(consumerProgramStructureIdsSaperatedByComma);
		
		HashMap<String, CenterExamBean> centerCodeNameMap = getCentersMap();
		
		Page<AssignmentFileBean> page = dao.getANS(1, Integer.MAX_VALUE, searchBean, getAuthorizedCodes(request));
		List<AssignmentFileBean> assignmentFilesList = page.getPageItems();

		/*modelnView.addObject("assignmentFilesList", assignmentFilesList);
		modelnView.addObject("page", page);*/
		ArrayList<ConsumerProgramStructureExam> consumerType = dao.getConsumerTypeList();
		modelnView.addObject("consumerType", consumerType);
		modelnView.addObject("searchBean", searchBean);
		modelnView.addObject("rowCount", page.getRowCount());

		modelnView.addObject("yearList", currentYearList);
		modelnView.addObject("subjectList", getSubjectList());
		
		if(assignmentFilesList == null || assignmentFilesList.size() == 0){
			request.setAttribute("error", "true");
			request.setAttribute("errorMessage", "No records found.");
			return modelnView;
		}
		
		for (AssignmentFileBean assignmentFileBean : assignmentFilesList) {
			String lc = centerCodeNameMap.get(assignmentFileBean.getCenterCode()) != null ? centerCodeNameMap.get(assignmentFileBean.getCenterCode()).getLc() : "No LC";
			assignmentFileBean.setLc(lc);
		}

		return new ModelAndView("ansExcelView","ansStudentList",assignmentFilesList);
	}

	@RequestMapping(value = "/insertANSRecordsForm", method = {RequestMethod.GET, RequestMethod.POST})
	public ModelAndView insertANSRecordsForm(HttpServletRequest request, HttpServletResponse response, @ModelAttribute AssignmentFileBean assignmentFile) {
		ModelAndView modelnView = new ModelAndView("insertANSRecords");
		
		AssignmentsDAO dao = (AssignmentsDAO)act.getBean("asignmentsDAO");		
		ArrayList<ConsumerProgramStructureExam> consumerType = dao.getConsumerTypeList();
		modelnView.addObject("consumerType", consumerType);
		modelnView.addObject("searchBean",assignmentFile);
		modelnView.addObject("yearList", currentYearList);
		modelnView.addObject("acadsYearList", ACAD_YEAR_LIST);
		modelnView.addObject("acadsMonthList", ACAD_MONTH_LIST);
		return modelnView;
	}

	@RequestMapping(value = "/searchANSRecordsToInsert", method = {RequestMethod.GET, RequestMethod.POST})
	public ModelAndView searchANSRecordsToInsert(HttpServletRequest request, HttpServletResponse response, @ModelAttribute AssignmentFileBean searchBean){
		ModelAndView modelnView = new ModelAndView("insertANSRecords");
		AssignmentsDAO dao = (AssignmentsDAO)act.getBean("asignmentsDAO");
		ArrayList<String> consumerProgramStructureIds = dao.getconsumerProgramStructureIds(searchBean.getProgramId(), searchBean.getProgramStructureId(), searchBean.getConsumerTypeId());
		searchBean.setConsumerProgramStructureId(consumerProgramStructureIds.toString());
		String tmp_masterId = consumerProgramStructureIds.toString();
		tmp_masterId = tmp_masterId.substring(1, tmp_masterId.length() - 1);
		searchBean.setConsumerProgramStructureId(tmp_masterId);
		Page<AssignmentFileBean> page = dao.getANS(1, Integer.MAX_VALUE, searchBean, getAuthorizedCodes(request));
		List<AssignmentFileBean> assignmentFilesList = page.getPageItems();
		ArrayList<ConsumerProgramStructureExam> consumerType = dao.getConsumerTypeList();
		modelnView.addObject("consumerType", consumerType);
		modelnView.addObject("searchBean", searchBean);
		modelnView.addObject("rowCount", page.getRowCount());
		modelnView.addObject("yearList", currentYearList);
		modelnView.addObject("subjectList", getSubjectList());
		modelnView.addObject("acadsYearList", ACAD_YEAR_LIST);
		modelnView.addObject("acadsMonthList", ACAD_MONTH_LIST);
		if(assignmentFilesList == null || assignmentFilesList.size() == 0){
			request.setAttribute("error", "true");
			request.setAttribute("errorMessage", "No records found.");
			return modelnView;
		}

		return modelnView;
	}

	@RequestMapping(value = "/insertANSRecords", method = {RequestMethod.GET, RequestMethod.POST})
	public ModelAndView insertANSRecords(HttpServletRequest request, HttpServletResponse response, @ModelAttribute AssignmentFileBean searchBean){
		ModelAndView modelnView = new ModelAndView("insertANSRecords");
		
		
		AssignmentsDAO dao = (AssignmentsDAO)act.getBean("asignmentsDAO");
		PassFailDAO pDao = (PassFailDAO)act.getBean("passFailDAO");
		ArrayList<String> consumerProgramStructureIds = dao.getconsumerProgramStructureIds(searchBean.getProgramId(), searchBean.getProgramStructureId(), searchBean.getConsumerTypeId());
		String tmp_masterId = consumerProgramStructureIds.toString();
		tmp_masterId = tmp_masterId.substring(1, tmp_masterId.length() - 1);
		searchBean.setConsumerProgramStructureId(tmp_masterId);
		Page<AssignmentFileBean> page = dao.getANS(1, Integer.MAX_VALUE, searchBean, getAuthorizedCodes(request));
		List<AssignmentFileBean> assignmentFilesList = page.getPageItems();
		String userId = (String)request.getSession().getAttribute("userId");
		ArrayList<ConsumerProgramStructureExam> consumerType = dao.getConsumerTypeList();	
		modelnView.addObject("consumerType", consumerType);
		modelnView.addObject("searchBean", searchBean);
		modelnView.addObject("rowCount", page.getRowCount());
		modelnView.addObject("yearList", currentYearList);
		modelnView.addObject("subjectList", getSubjectList());
		modelnView.addObject("acadsYearList", ACAD_YEAR_LIST);
		modelnView.addObject("acadsMonthList", ACAD_MONTH_LIST);

		int projectCount = 0;
		int insertCount = 0;
		int waivedOffSubjectsCount = 0;
		Map<String,StudentExamBean> mapOfSapIdandstudent = dao.getAllStudents();
		
		if(assignmentFilesList == null || assignmentFilesList.size() == 0){
			request.setAttribute("error", "true");
			request.setAttribute("errorMessage", "No records found.");
			return modelnView;
		}else{
			for (AssignmentFileBean bean : assignmentFilesList) {
				StudentMarksBean marksBean = new StudentMarksBean();
				if("Project".equalsIgnoreCase(bean.getSubject()) || "Module 4 - Project".equalsIgnoreCase(bean.getSubject())){
					projectCount++;
					continue;
				}
				// excluding WaiveOff Subject from InsertANS Records 
				StudentExamBean student =mapOfSapIdandstudent.get(bean.getSapId());
//				Removed waived off logic from here in favor of common logic from student helper.
				studentService.mgetWaivedOffSubjects(student);
				if(student.getWaivedOffSubjects().contains(bean.getSubject()))
				{
					waivedOffSubjectsCount++;
					continue;
					
				}
				
				marksBean.setSapid(bean.getSapId());
				marksBean.setSem(bean.getSem());
				marksBean.setAssignmentscore("ANS");
				marksBean.setStudentname(bean.getFirstName() + " " + bean.getLastName());
				marksBean.setSubject(bean.getSubject());
				marksBean.setProgram(bean.getProgram());
				marksBean.setSyllabusYear("");
				marksBean.setGrno("Not Available");
				marksBean.setYear(searchBean.getYear());
				marksBean.setMonth(searchBean.getMonth());
				marksBean.setWritenscore("");
				marksBean.setCreatedBy(userId);
				marksBean.setLastModifiedBy(userId);

				dao.upsertAssignmentMarks(marksBean);

				insertCount++;
			}
			modelnView.addObject("insertCount", insertCount);
			modelnView.addObject("projectCount", projectCount);
			modelnView.addObject("waivedOffSubjectsCount", waivedOffSubjectsCount);
			setSuccess(request, insertCount + " records inserted successfully. " +projectCount + " Project records skipped from ANS entries" + " and " + waivedOffSubjectsCount + " waived off subjects skipped.");

		}

		return modelnView;
	}

	@RequestMapping(value = "/findANSListForEmail", method = {RequestMethod.GET, RequestMethod.POST})
	public ModelAndView findANSListForEmail(HttpServletRequest request, HttpServletResponse response, @ModelAttribute AssignmentFileBean searchBean){
		ModelAndView modelnView = new ModelAndView("searchAssignmentSubmission");
		request.getSession().setAttribute("searchBean", searchBean);

		
		AssignmentsDAO dao = (AssignmentsDAO)act.getBean("asignmentsDAO");
		
		ArrayList<String> consumerProgramStructureIds = dao.getConsumerProgramStructureIdsForANS(searchBean.getProgramId(),searchBean.getProgramStructureId(),searchBean.getConsumerTypeId());
		String consumerProgramStructureIdsSaperatedByComma = "";
		for(int i=0;i < consumerProgramStructureIds.size();i++){
			consumerProgramStructureIdsSaperatedByComma = consumerProgramStructureIdsSaperatedByComma + ""+ consumerProgramStructureIds.get(i) +",";
		}
		consumerProgramStructureIdsSaperatedByComma = consumerProgramStructureIdsSaperatedByComma.substring(0,consumerProgramStructureIdsSaperatedByComma.length()-1);
		searchBean.setConsumerProgramStructureId(consumerProgramStructureIdsSaperatedByComma);
		Page<AssignmentFileBean> page = dao.getANS(1, pageSize, searchBean, getAuthorizedCodes(request));
		List<AssignmentFileBean> assignmentFilesList = page.getPageItems();

		ArrayList<ConsumerProgramStructureExam> consumerType = dao.getConsumerTypeList();
		modelnView.addObject("consumerType", consumerType);
		modelnView.addObject("assignmentFilesList", assignmentFilesList);
		modelnView.addObject("page", page);

		modelnView.addObject("searchBean", searchBean);
		modelnView.addObject("rowCount", page.getRowCount());

		modelnView.addObject("yearList", currentYearList);
		modelnView.addObject("subjectList", getSubjectList());
		if(assignmentFilesList == null || assignmentFilesList.size() == 0){
			request.setAttribute("error", "true");
			request.setAttribute("errorMessage", "No records found.");

		}
		return modelnView;
	}

	@RequestMapping(value = "/sendANSReminderEmail", method = {RequestMethod.GET, RequestMethod.POST})
	public ModelAndView sendANSReminderEmail(HttpServletRequest request, HttpServletResponse response, @ModelAttribute AssignmentFileBean searchBean){
		ModelAndView modelnView = new ModelAndView("searchAssignmentSubmission");
		modelnView.addObject("searchBean", searchBean);
		modelnView.addObject("yearList", currentYearList);
		modelnView.addObject("subjectList", getSubjectList());
		
		request.getSession().setAttribute("searchBean", searchBean);
		try {

			AssignmentsDAO dao = (AssignmentsDAO)act.getBean("asignmentsDAO");
			ExamOrderExamBean assignmentLiveCycle = dao.getAssignmentLiveCycle();
			
			if((!searchBean.getMonth().equals(assignmentLiveCycle.getMonth())) || (!searchBean.getYear().equals(assignmentLiveCycle.getYear()))){
				setError(request, "Cannot send Email Reminders for Exam Cycle that is not live. Current Live Cycle: "
						+assignmentLiveCycle.getMonth()+"-"+assignmentLiveCycle.getYear());
				return modelnView;
			}
			
			Page<AssignmentFileBean> page = dao.getANS(1, Integer.MAX_VALUE, searchBean, getAuthorizedCodes(request));
			List<AssignmentFileBean> assignmentFilesList = page.getPageItems();


			if(assignmentFilesList == null || assignmentFilesList.size() == 0){
				setError(request, "No records found.");
			}

			emailHelper.sendAssignmentReminderEmails(assignmentFilesList, searchBean);

			setSuccess(request, "Email Reminder intiated for " + page.getRowCount() + " ANS records.");

		} catch (Exception e) {
			
			setError(request, "Error in sending Email reminders");
		}
		return modelnView;
	}

	@RequestMapping(value = "/sendEvaluationReminderEmail", method = {RequestMethod.GET, RequestMethod.POST})
	public ModelAndView sendEvaluationReminderEmail(HttpServletRequest request, HttpServletResponse response, @ModelAttribute AssignmentFileBean searchBean){
		ModelAndView modelnView = new ModelAndView("searchAssignmentToEvaluate");
		request.getSession().setAttribute("searchBean", searchBean);
		try {
			AssignmentsDAO dao = (AssignmentsDAO)act.getBean("asignmentsDAO");

			Page<AssignmentFileBean> page = dao.getAssignmentsForFacultyPage(1, pageSize, searchBean);
			List<AssignmentFileBean> assignmentFilesList = page.getPageItems();

			modelnView.addObject("assignmentFilesList", assignmentFilesList);
			modelnView.addObject("page", page);

			modelnView.addObject("searchBean", searchBean);
			modelnView.addObject("rowCount", page.getRowCount());

			modelnView.addObject("yearList", currentYearList);
			modelnView.addObject("subjectList", getSubjectList());
			if(assignmentFilesList == null || assignmentFilesList.size() == 0){
				setError(request, "No records found.");
			}

			emailHelper.sendAssignmentEvaluationReminderEmails(assignmentFilesList, searchBean, getFacultyMap());

			setSuccess(request, "Email Reminder intiated for " + page.getRowCount() + " records.");

		} catch (Exception e) {
			
			setError(request, "Error in sending Email reminders");
		}
		return modelnView;
	}

	@RequestMapping(value = "/sendGenericANSReminderEmail", method = {RequestMethod.GET, RequestMethod.POST})
	public ModelAndView sendGenericANSReminderEmail(HttpServletRequest request, HttpServletResponse response, @ModelAttribute AssignmentFileBean searchBean){
		ModelAndView modelnView = new ModelAndView("searchAssignmentSubmission");
		modelnView.addObject("searchBean", searchBean);
		modelnView.addObject("yearList", currentYearList);
		modelnView.addObject("subjectList", getSubjectList());
		
		request.getSession().setAttribute("searchBean", searchBean);
		try {


			AssignmentsDAO dao = (AssignmentsDAO)act.getBean("asignmentsDAO");
			ExamOrderExamBean assignmentLiveCycle = dao.getAssignmentLiveCycle();
			
			if((!searchBean.getMonth().equals(assignmentLiveCycle.getMonth())) || (!searchBean.getYear().equals(assignmentLiveCycle.getYear()))){
				setError(request, "Cannot send Email Reminders for Exam Cycle that is not live. Current Live Cycle: "
							+assignmentLiveCycle.getMonth()+"-"+assignmentLiveCycle.getYear());
				return modelnView;
			}
			
			Page<AssignmentFileBean> page = dao.getANS(1, Integer.MAX_VALUE, searchBean, getAuthorizedCodes(request));
			List<AssignmentFileBean> assignmentFilesList = page.getPageItems();

			
			if(assignmentFilesList == null || assignmentFilesList.size() == 0){
				setError(request, "No records found.");
			}

			emailHelper.sendGenericAssignmentReminderEmails(assignmentFilesList, searchBean);

			setSuccess(request, "Generic Email Reminder intiated for " + page.getRowCount() + " ANS records.");

		} catch (Exception e) {
			
			setError(request, "Error in sending Email reminders");
		}
		return modelnView;
	}

	@RequestMapping(value = "/downloadAssignmentSubmittedExcel", method = {RequestMethod.GET, RequestMethod.POST})
	public ModelAndView downloadAssignmentSubmittedExcel(HttpServletRequest request, HttpServletResponse response) {

		AssignmentFileBean searchBean = (AssignmentFileBean)request.getSession().getAttribute("searchBean");
		ExcelHelper excelHelper = new ExcelHelper();
		AssignmentsDAO dao = (AssignmentsDAO)act.getBean("asignmentsDAO");
		Page<AssignmentFileBean> page = dao.getAssignmentSubmissionPage(1, Integer.MAX_VALUE, searchBean);
		List<AssignmentFileBean> assignmentSubmittedList = page.getPageItems();
		
		List<AssignmentFileBean> studentEndDateList=new ArrayList<AssignmentFileBean>();
		try {
			 studentEndDateList = dao.getstudentEndDate(searchBean.getMonth(),searchBean.getYear());
		} catch (Exception e) {
			e.printStackTrace();
			// TODO: handle exception
		}
		Map<String,String> studEndateMap=new HashMap<String,String>();
		
		studentEndDateList.stream().forEach(studentEndDate->{
			studEndateMap.put(studentEndDate.getSapId(), studentEndDate.getEndDate());
		});
		
		assignmentSubmittedList.stream().forEach(student->{
			
			student.setEndDate(studEndateMap.get(student.getSapId()));
		});
		
		try{
			excelHelper.buildExcelDocumentForSearchAssignment(assignmentSubmittedList, response, dao);
			return searchAssignmentSubmissionForm(request,response);
		}catch(Exception e){
			
			setError(request,"ERROR");
			return searchAssignmentSubmissionForm(request,response);
		}
		/*return new ModelAndView("assignmentSubmittedExcelView","assignmentSubmittedList",assignmentSubmittedList);*/
	}

	//@RequestMapping(value = "/downloadNormalizedScore", method = {RequestMethod.GET, RequestMethod.POST})
	public ModelAndView downloadNormalizedScoreOldNoLongerInUse(HttpServletRequest request, HttpServletResponse response) {

		AssignmentFileBean searchBean = (AssignmentFileBean)request.getSession().getAttribute("searchBean");

		AssignmentsDAO dao = (AssignmentsDAO)act.getBean("asignmentsDAO");
		List<AssignmentFileBean> assignmentListLevel1 = dao.getMeanStdDev("1");
		List<AssignmentFileBean> assignmentListLevel2 = dao.getMeanStdDev("2");
		List<AssignmentFileBean> assignmentListLevel3 = dao.getMeanStdDev("3");

		HashMap<String, String> studentMeanMapLevel1 = new HashMap<>();
		HashMap<String, String> studentMeanMapLevel2 = new HashMap<>();
		HashMap<String, String> studentMeanMapLevel3 = new HashMap<>();

		HashMap<String, String> studentPopulationMapLevel1 = new HashMap<>();
		HashMap<String, String> studentPopulationMapLevel2 = new HashMap<>();
		HashMap<String, String> studentPopulationMapLevel3 = new HashMap<>();

		HashMap<String, String> studentStdDevMapLevel1 = new HashMap<>();
		HashMap<String, String> studentStdDevMapLevel2 = new HashMap<>();
		HashMap<String, String> studentStdDevMapLevel3 = new HashMap<>();

		for (AssignmentFileBean assignmentFileBean : assignmentListLevel1) {
			studentMeanMapLevel1.put(assignmentFileBean.getFacultyId()+assignmentFileBean.getSubject(), assignmentFileBean.getMean());
			studentStdDevMapLevel1.put(assignmentFileBean.getFacultyId()+assignmentFileBean.getSubject(), assignmentFileBean.getStddev());
			studentPopulationMapLevel1.put(assignmentFileBean.getFacultyId()+assignmentFileBean.getSubject(), assignmentFileBean.getPopulationCount());
		}

		for (AssignmentFileBean assignmentFileBean : assignmentListLevel2) {
			studentMeanMapLevel2.put(assignmentFileBean.getFaculty2()+assignmentFileBean.getSubject(), assignmentFileBean.getMean());
			studentStdDevMapLevel2.put(assignmentFileBean.getFaculty2()+assignmentFileBean.getSubject(), assignmentFileBean.getStddev());
			studentPopulationMapLevel2.put(assignmentFileBean.getFaculty2()+assignmentFileBean.getSubject(), assignmentFileBean.getPopulationCount());
		}

		for (AssignmentFileBean assignmentFileBean : assignmentListLevel3) {
			studentMeanMapLevel3.put(assignmentFileBean.getFaculty3()+assignmentFileBean.getSubject(), assignmentFileBean.getMean());
			studentStdDevMapLevel3.put(assignmentFileBean.getFaculty3()+assignmentFileBean.getSubject(), assignmentFileBean.getStddev());
			studentPopulationMapLevel3.put(assignmentFileBean.getFaculty3()+assignmentFileBean.getSubject(), assignmentFileBean.getPopulationCount());
		}


		List<AssignmentFileBean> allAssignments = dao.getAllAsignments();

		for (AssignmentFileBean assignmentFileBean : allAssignments) {
			double score1 = Double.parseDouble(assignmentFileBean.getScore());
			double score2 = Double.parseDouble(assignmentFileBean.getFaculty2Score());


			double m4 = 0;
			double stddev4 = 0;
			double z4 = 0;

			if((score1 + score2) > 0.0){
				double m1 = Double.parseDouble(studentMeanMapLevel1.get(assignmentFileBean.getFacultyId()+assignmentFileBean.getSubject()));
				double m2 = Double.parseDouble(studentMeanMapLevel2.get(assignmentFileBean.getFaculty2()+assignmentFileBean.getSubject()));
				double m3 = -1;

				double stddev1 = Double.parseDouble(studentStdDevMapLevel1.get(assignmentFileBean.getFacultyId()+assignmentFileBean.getSubject()));
				double stddev2 = Double.parseDouble(studentStdDevMapLevel2.get(assignmentFileBean.getFaculty2()+assignmentFileBean.getSubject()));
				double stddev3 = -1;

				double population1 = Double.parseDouble(studentPopulationMapLevel1.get(assignmentFileBean.getFacultyId()+assignmentFileBean.getSubject()));
				double population2 = Double.parseDouble(studentPopulationMapLevel2.get(assignmentFileBean.getFaculty2()+assignmentFileBean.getSubject()));
				double population3 = -1;

				double z1 = 0;
				double z2 = 0;
				double z3 = 0;

				if(stddev1 > 0){
					z1 = (score1 - m1)/stddev1;
				}else{
					z1 = 0;
				}

				if(stddev2 > 0){
					z2 = (score2 - m2)/stddev2;
				}else{
					z2 = 0;
				}

				String faculty3 = assignmentFileBean.getFaculty3();
				if(faculty3 != null && !"".equalsIgnoreCase(faculty3)){
					double score3 = Double.parseDouble(assignmentFileBean.getFaculty3Score());
					m3 = Double.parseDouble(studentMeanMapLevel3.get(assignmentFileBean.getFaculty3()+assignmentFileBean.getSubject()));
					stddev3 = Double.parseDouble(studentStdDevMapLevel3.get(assignmentFileBean.getFaculty3()+assignmentFileBean.getSubject()));
					population3 = Double.parseDouble(studentPopulationMapLevel3.get(assignmentFileBean.getFaculty3()+assignmentFileBean.getSubject()));

					if(stddev3 > 0){
						z3 = (score3 - m3)/stddev3;
					}else{
						z3 = 0;
					}

					m4 = ((m1*population1) + (m2*population2) + (m3*population3)) / (population1 + population2 + population3);
					stddev4 = ((stddev1*population1) + (stddev2*population2) + (stddev3*population3)) / (population1 + population2 + population3);
					z4 = ((z1*population1) + (z2*population2) + (z3*population3)) / (population1 + population2 + population3);

				}else{
					m4 = ((m1*population1) + (m2*population2)) / (population1 + population2 );
					stddev4 = ((stddev1*population1) + (stddev2*population2)) / (population1 + population2 );
					z4 = ((z1*population1) + (z2*population2) ) / (population1 + population2 );
				}

				if(assignmentFileBean.getSubject().equals("Insurance & Risk Management")){
				}
				double normalizedScore = (z4 * stddev4) + m4;
				if(normalizedScore > 30){
					normalizedScore = 30;
				}
				assignmentFileBean.setNormalizedScore(normalizedScore+"");
				assignmentFileBean.setRoundedNormalizedScore((int)Math.round(normalizedScore)+"");
			}else{
				assignmentFileBean.setNormalizedScore("0");
				assignmentFileBean.setRoundedNormalizedScore("0");
			}

		}

		return new ModelAndView("assignmntNormalizedScoreExcelView","assignmentSubmittedList",allAssignments);

	}

	@RequestMapping(value = "/downloadNormalizedScoreForm", method = {RequestMethod.GET, RequestMethod.POST})
	public ModelAndView downloadNormalizedScoreForm(HttpServletRequest request, HttpServletResponse response) {
		ModelAndView modelnView = new ModelAndView("downloadNormalizedScore");

		modelnView.addObject("searchBean",new AssignmentFileBean());
		modelnView.addObject("yearList", currentYearList);
		return modelnView;
	}

	//Use this instead of above one from next cycle, as this is for single faculty. Also this needs to be modified for smaller set, as it fails for smaller population
	@RequestMapping(value = "/downloadNormalizedScore", method = {RequestMethod.GET, RequestMethod.POST})
	public ModelAndView downloadNormalizedScore(HttpServletRequest request, HttpServletResponse response, @ModelAttribute AssignmentFileBean searchBean) {
		try {
			List<AssignmentFileBean> allAssignments = getNormalizedAssignments(searchBean);

			return new ModelAndView("assignmntNormalizedScoreExcelView","assignmentSubmittedList",allAssignments);
		} catch (Exception e) {
			
			setError(request, e.getMessage());
			return new ModelAndView("downloadNormalizedScore");
		}

	}
	
	@RequestMapping(value = "/moveNormalizedScoreToMarksTableForm", method = {RequestMethod.GET, RequestMethod.POST})
	public ModelAndView moveNormalizedScoreToMarksTableForm(HttpServletRequest request, HttpServletResponse response) {
		ModelAndView modelnView = new ModelAndView("moveAssignmentScoreToMarksTable");

		modelnView.addObject("searchBean",new AssignmentFileBean());
		modelnView.addObject("yearList", currentYearList);
		return modelnView;
	}
	
	@RequestMapping(value = "/moveNormalizedScoreToMarksTable", method = {RequestMethod.GET, RequestMethod.POST})
	public ModelAndView moveNormalizedScoreToMarksTable(HttpServletRequest request, HttpServletResponse response, @ModelAttribute AssignmentFileBean searchBean) {
		ModelAndView modelnView = new ModelAndView("moveAssignmentScoreToMarksTable");
		List<AssignmentFileBean> allAssignments = getNormalizedAssignments(searchBean);
		String userId = (String)request.getSession().getAttribute("userId");
		AssignmentsDAO dao = (AssignmentsDAO)act.getBean("asignmentsDAO");
		
		int projectCount = 0;
		int insertCount = 0;
		
		for (AssignmentFileBean bean : allAssignments) {
			StudentMarksBean marksBean = new StudentMarksBean();
			//Added to avoid Normalization in Diageo Corporate 
			if(bean.getConsumerType() != "Diageo" && !bean.getSubject().equals("Soft Skills for Managers"))
			{
				
				/*
				 * if(bean.getSapId() == null) { bean.setSapId(bean.getSapid()); }
				 * 
				 * marksBean.setSapid(bean.getSapid());
				 */
				marksBean.setSapid(bean.getSapId());
				marksBean.setSem(getAssignmentSemester(bean));
				marksBean.setAssignmentscore(bean.getRoundedNormalizedScore());
				marksBean.setStudentname(bean.getFirstName() + " " + bean.getLastName());
				marksBean.setSubject(bean.getSubject());
				marksBean.setProgram(bean.getProgram());
				marksBean.setSyllabusYear("");
				marksBean.setGrno("Not Available");
				marksBean.setYear(searchBean.getYear());
				marksBean.setMonth(searchBean.getMonth());
				marksBean.setWritenscore("");
				marksBean.setCreatedBy(userId);
				marksBean.setLastModifiedBy(userId);
				dao.upsertAssignmentMarks(marksBean);
			}
		

			

			insertCount++;
		}
		setSuccess(request, insertCount + " assignments normalized marks moved to Marks table");
		modelnView.addObject("searchBean", searchBean);
		modelnView.addObject("yearList", currentYearList);
		return modelnView;

	}
	
	@RequestMapping(value = "/moveCopyScoreToMarksTable", method = {RequestMethod.GET, RequestMethod.POST})
	public ModelAndView moveCopyScoreToMarksTable(HttpServletRequest request, HttpServletResponse response, @ModelAttribute AssignmentFileBean searchBean) {
		ModelAndView modelnView = new ModelAndView("moveAssignmentScoreToMarksTable");
		AssignmentsDAO dao = (AssignmentsDAO)act.getBean("asignmentsDAO");
		List<AssignmentFileBean> assignmentCopyCaseList = dao.getCopyCases(searchBean);
		
		String userId = (String)request.getSession().getAttribute("userId");
		
		int insertCount = 0;
		
		for (AssignmentFileBean bean : assignmentCopyCaseList) {
			StudentMarksBean marksBean = new StudentMarksBean();
			/*
			 * if(bean.getSapId() == null) { bean.setSapId(bean.getSapid()); }
			 */
			marksBean.setSapid(bean.getSapId());
			marksBean.setSem(getAssignmentSemester(bean));
			marksBean.setAssignmentscore(bean.getFinalScore());// 0 of copy cases is maintained here
			marksBean.setStudentname(bean.getFirstName() + " " + bean.getLastName());
			marksBean.setSubject(bean.getSubject());
			marksBean.setProgram(bean.getProgram());
			marksBean.setSyllabusYear("");
			marksBean.setGrno("Not Available");
			marksBean.setYear(searchBean.getYear());
			marksBean.setMonth(searchBean.getMonth());
			marksBean.setWritenscore("");
			marksBean.setCreatedBy(userId);
			marksBean.setLastModifiedBy(userId);

			dao.upsertAssignmentMarks(marksBean);

			insertCount++;
		}
		setSuccess(request, insertCount + " assignments copy case marks moved to Marks table");
		modelnView.addObject("searchBean", searchBean);
		modelnView.addObject("yearList", currentYearList);
		
		return modelnView;

	}
	

	private String getAssignmentSemester(AssignmentFileBean assignment) {
		ArrayList<ProgramSubjectMappingExamBean> programSubjectMappingList = getProgramSubjectMappingList();

		for (int i = 0; i < programSubjectMappingList.size(); i++) {
			ProgramSubjectMappingExamBean bean = programSubjectMappingList.get(i);

			if(bean.getPrgmStructApplicable().equals(assignment.getPrgmStructApplicable()) && bean.getProgram().equals(assignment.getProgram())
					&& bean.getSubject().equals(assignment.getSubject())){
				//Setting the semester for Subject 
				return bean.getSem();
			}
		}
		
		return "1";
	}

	private List<AssignmentFileBean> getNormalizedAssignments(AssignmentFileBean searchBean) {
		AssignmentsDAO dao = (AssignmentsDAO)act.getBean("asignmentsDAO");
		List<AssignmentFileBean> allAssignments = dao.getAllAsignmentsForLevel1(searchBean);
		
		try {
			
		
		List<AssignmentFileBean> assignmentListLevel1 = dao.getMeanStdDevPerSubjectFaculty(searchBean);

		List<AssignmentFileBean> assignmentListLevel = dao.getMeanStdDevPerSubject(searchBean);

		HashMap<String, String> subjectMeanMap = new HashMap<>();
		HashMap<String, String> subjectStdDevMap = new HashMap<>();


		HashMap<String, String> facultySubjectMeanMap = new HashMap<>();
		HashMap<String, String> facultySubjectStdDevMap = new HashMap<>();

		for (AssignmentFileBean assignmentFileBean : assignmentListLevel1) {
			facultySubjectMeanMap.put(assignmentFileBean.getFacultyId()+assignmentFileBean.getSubject(), assignmentFileBean.getMean());
			facultySubjectStdDevMap.put(assignmentFileBean.getFacultyId()+assignmentFileBean.getSubject(), assignmentFileBean.getStddev());
		}

		for (AssignmentFileBean assignmentFileBean : assignmentListLevel) {
			subjectMeanMap.put(assignmentFileBean.getSubject(), assignmentFileBean.getMean());
			subjectStdDevMap.put(assignmentFileBean.getSubject(), assignmentFileBean.getStddev());
		}



		

		for (AssignmentFileBean assignmentFileBean : allAssignments) {
			try {
				 double score1 = 0;
				 if(assignmentFileBean.getScore() != null) {
					 score1 = Double.parseDouble(assignmentFileBean.getScore());
				 }//if
				
				if("ACBM".equals(assignmentFileBean.getProgram())){ 
					//If the student is ACBM then do not perform normalization//
					assignmentFileBean.setNormalizedScore(score1+"");
					assignmentFileBean.setRoundedNormalizedScore((int)Math.round(score1)+"");
					continue;
				}

				if((score1) > 0.0){
					double m1 = Double.parseDouble(facultySubjectMeanMap.get(assignmentFileBean.getFacultyId()+assignmentFileBean.getSubject()));
					double stdDevForSubjectFaculty = Double.parseDouble(facultySubjectStdDevMap.get(assignmentFileBean.getFacultyId()+assignmentFileBean.getSubject()));

					double meanForSubject = Double.parseDouble(subjectMeanMap.get(assignmentFileBean.getSubject()));
					double stdDevForSubject = Double.parseDouble(subjectStdDevMap.get(assignmentFileBean.getSubject()));

					double z1 = 0;
					if(stdDevForSubjectFaculty > 0){
						z1 = (score1 - m1)/stdDevForSubjectFaculty;
					}else{
						z1 = 0;
					}
					double normalizedScore = 0;
					if(stdDevForSubject == stdDevForSubjectFaculty){
						//i.e. there is only one faculty for subject
						normalizedScore = (z1 * stdDevForSubject) + meanForSubject;
					}else{
						normalizedScore = (z1 * stdDevForSubject * 0.75) + meanForSubject; //0.75 adjustment done since output was giving high variation
					}
					
					//Temporary changes by Sanket: 23-Jan-2017: START
					double differenceWithOriginalScore = 0.0;
					double MAX_DIFFERENCE_ALLOWED = 30 * 0.10; //10% variance allowed
					if(normalizedScore - score1 > 0){
						//Normalized score has increased
						differenceWithOriginalScore = normalizedScore-score1;
						if(differenceWithOriginalScore > MAX_DIFFERENCE_ALLOWED){
							normalizedScore = score1 + MAX_DIFFERENCE_ALLOWED;//Increase by MAX DIFFERENCE
						}
					}else{
						//Normalized score has decreased
						differenceWithOriginalScore = score1-normalizedScore;
						if(differenceWithOriginalScore > MAX_DIFFERENCE_ALLOWED){
							normalizedScore = score1 - MAX_DIFFERENCE_ALLOWED;//Decrease by MAX DIFFERENCE
						}
					}
					//Temporary changes by Sanket: 23-Jan-2017: END
					
					
					if(normalizedScore > 30){
						normalizedScore = 30;
					}
					
					if(normalizedScore < 0){
						normalizedScore = 0;
					}
					assignmentFileBean.setNormalizedScore(normalizedScore+"");
					assignmentFileBean.setRoundedNormalizedScore((int)Math.round(normalizedScore)+"");
				}else{
					assignmentFileBean.setNormalizedScore("0");
					assignmentFileBean.setRoundedNormalizedScore("0");
				}
			} catch (Exception e) {
				
				assignmentFileBean.setError(e.getCause() + " " + e.getMessage());
			}

		}
		} catch (Exception e) {
			
		}
		return allAssignments;
	}

	@RequestMapping(value = "/downloadAssignmentFile", method = {RequestMethod.GET, RequestMethod.POST})
	public ModelAndView downloadAssignmentFile(HttpServletRequest request, HttpServletResponse response ){
		ModelAndView modelnView = new ModelAndView("downloadAssignmentFile");

		String fullPath = request.getParameter("filePath");
		try{
			// get absolute path of the application
			ServletContext context = request.getSession().getServletContext();
			String appPath = context.getRealPath("");

			// construct the complete absolute path of the file
			//String fullPath = appPath + filePath;		
			File downloadFile = new File(fullPath);
			FileInputStream inputStream = new FileInputStream(downloadFile);

			// get MIME type of the file
			String mimeType = context.getMimeType(fullPath);
			if (mimeType == null) {
				// set to binary type if MIME mapping not found
				mimeType = "application/octet-stream";
			}

			// set content attributes for the response
			response.setContentType(mimeType);
			response.setContentLength((int) downloadFile.length());

			// set headers for the response
			String headerKey = "Content-Disposition";
			String headerValue = String.format("attachment; filename=\"%s\"",
					downloadFile.getName());
			response.setHeader(headerKey, headerValue);

			// get output stream of the response
			OutputStream outStream = response.getOutputStream();

			byte[] buffer = new byte[BUFFER_SIZE];
			int bytesRead = -1;

			// write bytes read from the input stream into the output stream
			while ((bytesRead = inputStream.read(buffer)) != -1) {
				outStream.write(buffer, 0, bytesRead);
			}
			inputStream.close();
			outStream.close();
		}catch(Exception e){
			
			request.setAttribute("error", "true");
			request.setAttribute("errorMessage", "Error in downloading file.");
		}
		AssignmentFileBean searchBean = (AssignmentFileBean)request.getSession().getAttribute("searchBean");
		modelnView.addObject("searchBean",searchBean);
		modelnView.addObject("yearList", currentYearList);
		modelnView.addObject("subjectList", getSubjectList());
		return modelnView;
	}

	@RequestMapping(value = "/editAssignmentFileForm", method = {RequestMethod.GET, RequestMethod.POST})
	public ModelAndView editAssignmentFileForm(HttpServletRequest request, HttpServletResponse response, @ModelAttribute AssignmentFileBean assignmentFile) {
		ModelAndView modelnView = new ModelAndView("addAssignmentFile");
		try {
		AssignmentsDAO dao = (AssignmentsDAO)act.getBean("asignmentsDAO");
		assignmentFile = dao.findAssignment(assignmentFile);
		//assignmentFile.setConsumerProgramStructureId();
		/*
		modelnView.addObject("consumerType", consumerType);
		 * */
		modelnView.addObject("assignmentFile",assignmentFile);
		/*modelnView.addObject("yearList", currentYearList);
		modelnView.addObject("subjectList", getSubjectList());*/
		modelnView.addObject("edit", "true");
		}catch (Exception e) {
			//e.printStackTrace();
			// TODO: handle exception
		}
		return modelnView;
	}

	@RequestMapping(value = "/updateAssignmentFile",  method = {RequestMethod.GET, RequestMethod.POST})
	public ModelAndView updateAssignmentFile(HttpServletRequest request, HttpServletResponse response, @ModelAttribute AssignmentFileBean assignmentFile){
		ModelAndView modelnView = new ModelAndView("addAssignmentFile");
		String errorMessage = null;
		AssignmentsDAO dao = (AssignmentsDAO)act.getBean("asignmentsDAO");
		try {
		if(assignmentFile.getFileData() != null ){
			String fileName = assignmentFile.getFileData().getOriginalFilename(); 
			if(fileName == null || "".equals(fileName.trim())){
				//If no file is selected, do not do any operation.
				AssignmentFileBean assignmentFileFromDB = dao.findAssignment(assignmentFile);
				assignmentFile.setInstructions(assignmentFileFromDB.getInstructions());
				assignmentFile.setFilePath(assignmentFileFromDB.getFilePath());
				assignmentFile.setQuestionFilePreviewPath(assignmentFileFromDB.getQuestionFilePreviewPath());
			}
			else{
				errorMessage = uploadAssignmentFile(assignmentFile, assignmentFile.getYear(), assignmentFile.getMonth());
			}
		}
		//Check if file saved to Disk successfully
		if(errorMessage == null){
			String userId = (String)request.getSession().getAttribute("userId");
			assignmentFile.setLastModifiedBy(userId);
			// new assignment logic
			List<String> consumerProgramStructureIds = new ArrayList<String>();
			if(assignmentFile.getConsumerProgramStructureId().split(",").length>1)
			{
				// If Any Option is Selected Is "All"
				String[] consumerProgramStructureIds_tmp = assignmentFile.getConsumerProgramStructureId().split(",");
				consumerProgramStructureIds = (List<String>) Arrays.asList(consumerProgramStructureIds_tmp);
				dao.batchInsertOfAssignmentsIfAll(assignmentFile, assignmentFile.getYear(), assignmentFile.getMonth(),consumerProgramStructureIds);
				List<String> sapidIds = dao.getSapidList(consumerProgramStructureIds);
				dao.updateQPFileAssignmentTemp(sapidIds, assignmentFile);
			}
			else {
				//String Key = dao.getAssignmentKey(assignmentFile.getProgramId(),assignmentFile.getProgramStructureId(), assignmentFile.getConsumerTypeId());
				dao.saveAssignmentDetails(assignmentFile, assignmentFile.getYear(), assignmentFile.getMonth());
				consumerProgramStructureIds.add(assignmentFile.getConsumerProgramStructureId());
				List<String> sapidIds = dao.getSapidList(consumerProgramStructureIds);
				dao.updateQPFileAssignmentTemp(sapidIds, assignmentFile);
			}
			
			
			/// end
			//dao.saveAssignmentDetails(assignmentFile, assignmentFile.getYear(), assignmentFile.getMonth());

		}else{
			request.setAttribute("error", "true");
			request.setAttribute("errorMessage", "Error in file Upload: "+errorMessage);
			modelnView.addObject("assignmentFile",assignmentFile);
			modelnView.addObject("yearList", currentYearList);
			modelnView.addObject("subjectList", getSubjectList());
			modelnView.addObject("edit", "true");
			return modelnView;
		}
		request.setAttribute("success","true");
		request.setAttribute("successMessage", "Assignment details updated successfully");
		
		}catch (Exception e) {
			//e.printStackTrace();
			// TODO: handle exception
		}
		AssignmentFileBean searchBean = (AssignmentFileBean)request.getSession().getAttribute("searchBean");
		return searchAssignmentFiles(request, response, searchBean); 

	}

	@RequestMapping(value = "/allocateAssignmentEvaluationForm", method = {RequestMethod.GET, RequestMethod.POST})
	public ModelAndView allocateAssignmentEvaluationForm(HttpServletRequest request, HttpServletResponse response, Model m) {

		AssignmentFileBean searchBean = new AssignmentFileBean();

		m.addAttribute("subjectList", getSubjectList());
		m.addAttribute("searchBean",searchBean);
		m.addAttribute("yearList", currentYearList);
		AssignmentsDAO adao = (AssignmentsDAO)act.getBean("asignmentsDAO");
		ArrayList<ConsumerProgramStructureExam> consumerType = adao.getConsumerTypeList();
		m.addAttribute("consumerType", consumerType);
		FacultyDAO dao = (FacultyDAO)act.getBean("facultyDAO");
		ArrayList<FacultyExamBean> facultyList = dao.getFaculties();
		m.addAttribute("facultyList", facultyList);

		String level = request.getParameter("level");
		searchBean.setLevel(level);
		if("1".equalsIgnoreCase(level)){
			return new ModelAndView("evaluation/allocateAssignmentEvaluationLevel1");
		}else if("2".equalsIgnoreCase(level)){//Reval-Second Level
			return new ModelAndView("evaluation/allocateAssignmentEvaluationLevel2");
		}else if("3".equalsIgnoreCase(level)){
			return new ModelAndView("evaluation/allocateAssignmentEvaluationLevel3");
		}else if("4".equalsIgnoreCase(level)){//Reval
			return new ModelAndView("evaluation/allocateAssignmentEvaluationLevel4");
		}

		return new ModelAndView("evaluation/allocateAssignmentEvaluationLevel1");
	}

	@RequestMapping(value="/findNoOfAssignments", method = {RequestMethod.GET, RequestMethod.POST})
	public @ResponseBody String findNoOfAssignments(HttpServletRequest request, HttpServletResponse response , @ModelAttribute AssignmentFileBean searchBean) throws JsonParseException, JsonMappingException, IOException {
		if(!checkSession(request, response)){
			return "login";
		}
		String level = request.getParameter("level");
		int numberOfSubjects = 0;
		try{
			
			AssignmentsDAO dao = (AssignmentsDAO)act.getBean("asignmentsDAO");
			numberOfSubjects = dao.getNumberOfAssignments(searchBean, level);

		}catch(Exception e){
			
		}

		return numberOfSubjects + "";

	}

	@RequestMapping(value="/getNoOfAssignments", method = {RequestMethod.GET, RequestMethod.POST})
	public ModelAndView getNoOfAssignments(HttpServletRequest request, HttpServletResponse response , @ModelAttribute AssignmentFileBean searchBean) throws JsonParseException, JsonMappingException, IOException {
		if(!checkSession(request, response)){
			return new ModelAndView("login");
		}
		ModelAndView modelnView = null;
		String level = searchBean.getLevel();
		AssignmentsDAO dao = (AssignmentsDAO)act.getBean("asignmentsDAO");
		int numberOfSubjects = 0;
		ArrayList<String> consumerProgramStrcutureId = dao.getconsumerProgramStructureIds(searchBean.getProgramId(), searchBean.getProgramStructureId(), searchBean.getConsumerTypeId());
		searchBean.setConsumerProgramStructureId(consumerProgramStrcutureId.toString().replaceAll("\\[|\\]|",""));
		if("1".equalsIgnoreCase(level)){ 
			modelnView = new ModelAndView("evaluation/allocateAssignmentEvaluationLevel1");
			numberOfSubjects = dao.getNumberOfAssignments(searchBean, level);
		}else if("2".equalsIgnoreCase(level)){
			modelnView = new ModelAndView("evaluation/allocateAssignmentEvaluationLevel2");
			numberOfSubjects = dao.getNumberOfAssignments(searchBean, level);
		}else if("3".equalsIgnoreCase(level)){
			modelnView = new ModelAndView("evaluation/allocateAssignmentEvaluationLevel3");
			List<AssignmentFileBean> assignments = dao.getNumberOfAssignmentsForLevel3(searchBean);
			numberOfSubjects = assignments != null ? assignments.size() : 0;
			request.getSession().setAttribute("level3EvaluationDifferenceList", assignments);
		}

		ArrayList<FacultyExamBean> facultyList = getFacultyAndCurrentAssignmentsNumber(searchBean, level);
		try{

		}catch(Exception e){
			
		}
		modelnView.addObject("consumerType", dao.getConsumerTypeList());
		modelnView.addObject("yearList", currentYearList);
		modelnView.addObject("searchBean", searchBean);
		modelnView.addObject("facultyList", facultyList);
		modelnView.addObject("numberOfSubjects", numberOfSubjects);
		modelnView.addObject("showFaculties", "true");
		return modelnView;

	}


	//Pending
	@RequestMapping(value="/searchRevalAssignmentAllocation", method = {RequestMethod.GET, RequestMethod.POST})
	public ModelAndView searchRevalAssignmentAllocation(HttpServletRequest request, HttpServletResponse response , @ModelAttribute AssignmentFileBean searchBean)  {
		if(!checkSession(request, response)){
			return new ModelAndView("login");
		}
		ModelAndView modelnView = new ModelAndView("evaluation/allocateAssignmentEvaluationLevel4");
		AssignmentsDAO dao = (AssignmentsDAO)act.getBean("asignmentsDAO");
		int rowCount = 0;

		ArrayList<AssignmentFileBean> revalAssignments = (ArrayList<AssignmentFileBean>)dao.getAssignmentsForReval(searchBean);
		searchBean.setRevalAssignments(revalAssignments);
		rowCount = revalAssignments != null ? revalAssignments.size() : 0;
		try{

		}catch(Exception e){
			
		}

		modelnView.addObject("yearList", currentYearList);
		modelnView.addObject("subjectList", getSubjectList());
		//modelnView.addObject("facultyList", getFacultyList());
		ArrayList<ConsumerProgramStructureExam> consumerType = dao.getConsumerTypeList();
		modelnView.addObject("consumerType", consumerType);
		modelnView.addObject("searchBean", searchBean);
		modelnView.addObject("rowCount", rowCount);
		modelnView.addObject("revalAssignments", revalAssignments);
		
		request.getSession().setAttribute("facultyMap", getFacultyList());
		return modelnView;

	}
	
	
	//Earlier level 2 will now be used for Second Level of Revaluation
	@RequestMapping(value="/searchReRevalAssignmentAllocation", method = {RequestMethod.GET, RequestMethod.POST})
	public ModelAndView searchReRevalAssignmentAllocation(HttpServletRequest request, HttpServletResponse response , @ModelAttribute AssignmentFileBean searchBean)  {
		if(!checkSession(request, response)){
			return new ModelAndView("login");
		}
		ModelAndView modelnView = new ModelAndView("evaluation/allocateAssignmentEvaluationLevel2");
		modelnView.addObject("subjectList", getSubjectList());
		AssignmentsDAO dao = (AssignmentsDAO)act.getBean("asignmentsDAO");
		int rowCount = 0;
		ArrayList<String> consumerProgramStrcutureId = dao.getconsumerProgramStructureIds(searchBean.getProgramId(), searchBean.getProgramStructureId(), searchBean.getConsumerTypeId());
		searchBean.setConsumerProgramStructureId(consumerProgramStrcutureId.toString().replaceAll("\\[|\\]|",""));
		ArrayList<AssignmentFileBean> revalAssignments = (ArrayList<AssignmentFileBean>)dao.getAssignmentsForReReval(searchBean);
		searchBean.setRevalAssignments(revalAssignments);
		rowCount = revalAssignments != null ? revalAssignments.size() : 0;
		try{

		}catch(Exception e){
			
		}

		modelnView.addObject("yearList", currentYearList);
		modelnView.addObject("subjectList", getSubjectList());
		ArrayList<ConsumerProgramStructureExam> consumerType = dao.getConsumerTypeList();
		modelnView.addObject("consumerType", consumerType);
		if(searchBean.getConsumerTypeId() != null) {
			
		}
		//modelnView.addObject("facultyList", getFacultyList());
		modelnView.addObject("searchBean", searchBean);
		modelnView.addObject("rowCount", rowCount);
		modelnView.addObject("revalAssignments", revalAssignments);
		
		// For download
		request.getSession().setAttribute("revalAssignments", revalAssignments);
		
		request.getSession().setAttribute("facultyMap", getFacultyList());
		return modelnView;

	}

	private ArrayList<FacultyExamBean> getFacultyAndCurrentAssignmentsNumber(AssignmentFileBean searchBean, String level) {
		FacultyDAO fDao = (FacultyDAO)act.getBean("facultyDAO");
		ArrayList<FacultyExamBean> facultyListWithAssignmentCount = fDao.getFacultiesWithAssignmentCount(searchBean, level);
		HashMap<String, String> facultyAssignmentCountMap = new HashMap<>();
		for (FacultyExamBean facultyBean : facultyListWithAssignmentCount) {
			facultyAssignmentCountMap.put(facultyBean.getFacultyId(), facultyBean.getAssignmentsAllocated());
		}
		ArrayList<FacultyExamBean> facultyList = fDao.getFaculties();

		for (FacultyExamBean facultyBean : facultyList) {
			if(facultyAssignmentCountMap.containsKey(facultyBean.getFacultyId())){
				facultyBean.setAssignmentsAllocated(facultyAssignmentCountMap.get(facultyBean.getFacultyId()));
				facultyBean.setAvailable(Integer.parseInt(MAX_ASSIGNMENTS_PER_FACULTY) - Integer.parseInt(facultyBean.getAssignmentsAllocated()));
			}else{
				facultyBean.setAssignmentsAllocated("0");
				facultyBean.setAvailable(Integer.parseInt(MAX_ASSIGNMENTS_PER_FACULTY));
			}
		}

		return facultyList;
	}


	@RequestMapping(value="/allocateAssignmentEvaluation", method = {RequestMethod.GET, RequestMethod.POST})
	public ModelAndView allocateAssignmentEvaluation(HttpServletRequest request, HttpServletResponse response , @ModelAttribute AssignmentFileBean searchBean, Model m) {
		if(!checkSession(request, response)){
			return new ModelAndView("login");
		}

		ModelAndView modelnView = null;
		String level = searchBean.getLevel();

		if("1".equalsIgnoreCase(level)){
			modelnView =  new ModelAndView("evaluation/allocateAssignmentEvaluationLevel1");
		}else if("2".equalsIgnoreCase(level)){
			modelnView =  new ModelAndView("evaluation/allocateAssignmentEvaluationLevel2");
		}else if("3".equalsIgnoreCase(level)){
			modelnView =  new ModelAndView("evaluation/allocateAssignmentEvaluationLevel3");
		}

		modelnView.addObject("searchBean",searchBean);
		modelnView.addObject("yearList", currentYearList);
		modelnView.addObject("subjectList", getSubjectList());
		

		try{
			AssignmentsDAO dao = (AssignmentsDAO)act.getBean("asignmentsDAO");
			ArrayList<ConsumerProgramStructureExam> consumerType = dao.getConsumerTypeList();
			modelnView.addObject("consumerType", consumerType);
			ArrayList<String> consumerProgramStrcutureId = dao.getconsumerProgramStructureIds(searchBean.getProgramId(), searchBean.getProgramStructureId(), searchBean.getConsumerTypeId());
			searchBean.setConsumerProgramStructureId(consumerProgramStrcutureId.toString().replaceAll("\\[|\\]|",""));
			ArrayList<String> faculties = searchBean.getFaculties();
			ArrayList<String> numberOfAssignments = searchBean.getNumberOfAssignments();
			ArrayList<String> indexes = searchBean.getIndexes();
			List<AssignmentFileBean> assignments = dao.getAssignments(searchBean, level);//Get actual assignments to allocate

			int startIndex = 0;

			//Check for all faculties if they are assigned any other subjects
			for (int i = 0; i < indexes.size(); i++) {
				int index = Integer.parseInt(indexes.get(i));
				String facultyId = faculties.get(index);

				boolean validFaculty =  dao.isValidFaculty(facultyId, searchBean);
				if(!validFaculty){
					setError(request, "Faculty " + facultyId + " is already assigned same subject at different level of evaluation. Please reassign subject.");
					return modelnView;
				}
			}

			startIndex = 0;
			for (int i = 0; i < indexes.size(); i++) {
				int index = Integer.parseInt(indexes.get(i));
				String facultyId = faculties.get(index);

				int lastIndex = startIndex + Integer.parseInt(numberOfAssignments.get(i));


				List<AssignmentFileBean> assignmentsSubSet = assignments.subList(startIndex, lastIndex);
				dao.allocateAssignment(assignmentsSubSet, facultyId, level);
				dao.allocateAssignmentInQuickTable(assignmentsSubSet, facultyId, level);
				startIndex = startIndex + Integer.parseInt(numberOfAssignments.get(i));
			}

			setSuccess(request, "Assignment Evaluation allocated successfully for subject "+searchBean.getSubject());

		}catch(Exception e){
			
			setError(request, "Error in allocating Assignment Evaluation");
		}

		modelnView.addObject("showFaculties", "true");
		modelnView.addObject("numberOfAssignments", null);
		modelnView.addObject("indexes", null);

		ArrayList<FacultyExamBean> facultyList = getFacultyAndCurrentAssignmentsNumber(searchBean, level);
		modelnView.addObject("facultyList", facultyList);

		AssignmentsDAO dao = (AssignmentsDAO)act.getBean("asignmentsDAO");
		int numberOfSubjects = dao.getNumberOfAssignments(searchBean, level);
		modelnView.addObject("numberOfSubjects", numberOfSubjects);
		

		return modelnView;
	}

	
	@RequestMapping(value="/allocateAssignmentsForReval", method = {RequestMethod.GET, RequestMethod.POST})
	public ModelAndView allocateAssignmentsForReval(HttpServletRequest request, HttpServletResponse response , @ModelAttribute AssignmentFileBean searchBean, Model m) {
		if(!checkSession(request, response)){
			return new ModelAndView("login");
		}

		ModelAndView modelnView = new ModelAndView("evaluation/allocateAssignmentEvaluationLevel4");

		modelnView.addObject("searchBean",searchBean);
		modelnView.addObject("yearList", currentYearList);
		modelnView.addObject("subjectList", getSubjectList());

		try{
			AssignmentsDAO dao = (AssignmentsDAO)act.getBean("asignmentsDAO");

			List<AssignmentFileBean> revalAssignments = searchBean.getRevalAssignments();
			List<AssignmentFileBean> assignments = new ArrayList<>();
			
			for (AssignmentFileBean assignmentFileBean : revalAssignments) {
				if(assignmentFileBean.getFacultyId() != null && !"".equals(assignmentFileBean.getFacultyId())){
					assignments.add(assignmentFileBean);
				}
			}

			for (AssignmentFileBean assignmentFileBean : assignments) {
				boolean validFaculty =  dao.isValidFacultyForReval(assignmentFileBean);
				if(!validFaculty){
					setError(request, "Faculty " + searchBean.getFacultyId() + " is already assigned same subject at different level of evaluation. Please reassign subject.");
					return modelnView;
				}
			}
			
			dao.allocateAssignmentForReval(assignments);
			dao.allocateAssignmentForRevalQuickTable(assignments);

			setSuccess(request, "Assignment Revaluation allocated successfully for subject "+searchBean.getSubject());

		}catch(Exception e){
			
			setError(request, "Error in allocating Assignment Evaluation");
			return modelnView;
		}

		return searchRevalAssignmentAllocation(request, response, searchBean);
	}
	
	@RequestMapping(value="/allocateAssignmentsForReReval", method = {RequestMethod.GET, RequestMethod.POST})
	public ModelAndView allocateAssignmentsForReReval(HttpServletRequest request, HttpServletResponse response , @ModelAttribute AssignmentFileBean searchBean, Model m) {
		if(!checkSession(request, response)){
			return new ModelAndView("login");
		}

		ModelAndView modelnView = new ModelAndView("evaluation/allocateAssignmentEvaluationLevel2");

		modelnView.addObject("searchBean",searchBean);
		modelnView.addObject("yearList", currentYearList);
		modelnView.addObject("subjectList", getSubjectList());

		try{
			AssignmentsDAO dao = (AssignmentsDAO)act.getBean("asignmentsDAO");

			List<AssignmentFileBean> revalAssignments = searchBean.getRevalAssignments();
			List<AssignmentFileBean> assignments = new ArrayList<>();
			
			for (AssignmentFileBean assignmentFileBean : revalAssignments) {
				if(assignmentFileBean.getFacultyId() != null && !"".equals(assignmentFileBean.getFacultyId())){
					assignments.add(assignmentFileBean);
				}
			}

			for (AssignmentFileBean assignmentFileBean : assignments) {
				boolean validFaculty =  dao.isValidFacultyForReval(assignmentFileBean);
				if(!validFaculty){
					setError(request, "Faculty " + searchBean.getFacultyId() + " is already assigned same subject at different level of evaluation. Please reassign subject.");
					return modelnView;
				}
			}
			
			dao.allocateAssignmentForReReval(assignments); 
			dao.allocateAssignmentForReRevalQuickTable(assignments);

			setSuccess(request, "Assignment Re-Revaluation allocated successfully ");

		}catch(Exception e){
			
			setError(request, "Error in allocating Assignment Evaluation");
			return modelnView;
		}

		return searchReRevalAssignmentAllocation(request, response, searchBean);
	}


	@RequestMapping(value = "/searchAssignmentToEvaluateForm", method = {RequestMethod.GET, RequestMethod.POST})
	public String searchAssignmentToEvaluateForm(HttpServletRequest request, HttpServletResponse response, Model m) {
		if(!checkSession(request, response)){
			return "login";
		}

		AssignmentFileBean searchBean = new AssignmentFileBean();
		m.addAttribute("searchBean",searchBean);
		m.addAttribute("yearList", currentYearList);
		m.addAttribute("subjectList", getSubjectList());

		String facultyId = (String)request.getSession().getAttribute("userId");
		Person user = (Person)request.getSession().getAttribute("user");
		String roles = user != null ? user.getRoles() : "";
		//String roles = "Faculty";


		if(roles.indexOf("Faculty") != -1){
			//When its faculty show only their assignment, otherwise show all assignments
			searchBean.setFacultyId(facultyId);
		}
		searchBean.setEvaluated("N");
		request.getSession().setAttribute("searchBean", searchBean);

		AssignmentsDAO dao = (AssignmentsDAO)act.getBean("asignmentsDAO");
		try {
		if(roles.indexOf("Faculty") != -1){
			//No Paging for Faculty
			List<AssignmentFileBean> assignmentFilesList = new ArrayList<>();
			
			//get faculty name from cache for all list-Prashant
//			Method execution time: 11 milliseconds.
			HashMap<String, FacultyExamBean> facultyNamemap = getFacultyMap();
			FacultyExamBean facultyName = facultyNamemap.get(facultyId);
			
			//get month and year for other query for slow query-Prashant
			List<String> months = new ArrayList<String>();
			List<String> years = new ArrayList<String>();
//			Method execution time: 81 milliseconds.
			List<AssignmentFileBean> monthYearFaculty = asgService.getMonthYearForEvaluateAssignment(searchBean);
			
			for(AssignmentFileBean assignmentFiles : monthYearFaculty) {
				months.add(assignmentFiles.getMonth());
				years.add(assignmentFiles.getYear());
			}
			
			String month = asgService.getSingleMonthYear(months);
			String year = asgService.getSingleMonthYear(years);
			
			searchBean.setMonth(month);
			searchBean.setYear(year);
			Page<AssignmentFileBean> page = new Page<AssignmentFileBean>();
			if (asgService.getAssignmentsForFaculty1Count(searchBean.getFacultyId(),searchBean.getMonth(),searchBean.getYear())) {
			//Page<AssignmentFileBean> page = dao.getAssignmentsForFaculty1(1, Integer.MAX_VALUE, searchBean);
//			Method execution time: 3494 milliseconds.
			 page = asgService.getAssignmentsSubmissionForFaculty1(1, Integer.MAX_VALUE, searchBean);
			List<AssignmentFileBean> faculty1List = page.getPageItems();
			assignmentFilesList.addAll(faculty1List);
			}
			
			if (asgService.getAssignmentsForFaculty2Count(searchBean.getFacultyId(),searchBean.getMonth(),searchBean.getYear())) {
			//page = dao.getAssignmentsForFaculty2(1, Integer.MAX_VALUE, searchBean);
			  page = asgService.getAssignmentsSubmissionForFaculty2(1, Integer.MAX_VALUE, searchBean);
			  List<AssignmentFileBean> faculty2List = page.getPageItems();
			  assignmentFilesList.addAll(faculty2List);
			}
			
			if (asgService.getAssignmentsForFaculty3Count(searchBean.getFacultyId(),searchBean.getMonth(),searchBean.getYear())) {
			  //page = dao.getAssignmentsForFaculty3(1, Integer.MAX_VALUE, searchBean);
			 page = asgService.getAssignmentsSubmissionForFaculty3(1, Integer.MAX_VALUE, searchBean);
			  List<AssignmentFileBean> faculty3List = page.getPageItems();
			  assignmentFilesList.addAll(faculty3List);
			}
			
			if (asgService.getAssignmentsForRevalCount(searchBean.getFacultyId(),searchBean.getMonth(),searchBean.getYear())) {
			  //page = dao.getAssignmentsForReval(1, Integer.MAX_VALUE, searchBean);
			  page = asgService.getAssignmentsSubmissionForReval(1, Integer.MAX_VALUE, searchBean);
			  List<AssignmentFileBean> faculty4List = page.getPageItems();
			  assignmentFilesList.addAll(faculty4List);
			}
			  for(AssignmentFileBean assignmentFiles: assignmentFilesList) {
				  assignmentFiles.setFirstName(facultyName.getFirstName());
				  assignmentFiles.setLastName(facultyName.getLastName()); 
				  }

			m.addAttribute("assignmentFilesList", assignmentFilesList);
			m.addAttribute("pendingEvaluations", assignmentFilesList.size());
			m.addAttribute("rowCount", assignmentFilesList.size());
			m.addAttribute("page", page);



		}else{
			Page<AssignmentFileBean> page = dao.getAssignmentsForFacultyPage(1, pageSize, searchBean);
			List<AssignmentFileBean> assignmentFilesList = page.getPageItems();
			m.addAttribute("assignmentFilesList", assignmentFilesList);
			m.addAttribute("pendingEvaluations", page.getRowCount());
			m.addAttribute("rowCount", page.getRowCount());
			m.addAttribute("page", page);
		}

		}catch (Exception e) {
			//e.printStackTrace();
			// TODO: handle exception
		}
		return "searchAssignmentToEvaluate";
	}

	@RequestMapping(value = "/searchAssignmentToEvaluate", method = {RequestMethod.GET, RequestMethod.POST})
	public ModelAndView searchAssignmentToEvaluate(HttpServletRequest request, HttpServletResponse response, @ModelAttribute AssignmentFileBean searchBean){

		if(!checkSession(request, response)){
			return new ModelAndView("login");
		}
		
		ModelAndView modelnView = new ModelAndView("searchAssignmentToEvaluate");
		request.getSession().setAttribute("searchBean", searchBean);

		String facultyId = (String)request.getSession().getAttribute("userId");
		//String facultyId = "NMSCEMU200303521";



		Person user = (Person)request.getSession().getAttribute("user");
		String roles = user != null ? user.getRoles() : "";
		//String roles = "Faculty";

		if(roles.indexOf("Faculty") != -1){
			//When its faculty show only their assignment, otherwise show all assignments
			searchBean.setFacultyId(facultyId);
		}

		AssignmentsDAO dao = (AssignmentsDAO)act.getBean("asignmentsDAO");

		if(roles.indexOf("Faculty") != -1){
			//No Paging for Faculty
			List<AssignmentFileBean> assignmentFilesList = new ArrayList<>();
			//AssignmentFileBean facultyName = asgService.getFacultyName(searchBean);
			
			//get faculty name from cache for all list-Prashant
			HashMap<String, FacultyExamBean> facultyNamemap = getFacultyMap();
			FacultyExamBean facultyName = facultyNamemap.get(facultyId);
			
			
			//Page<AssignmentFileBean> page = dao.getAssignmentsForFaculty1(1, Integer.MAX_VALUE, searchBean);
			Page<AssignmentFileBean> page = asgService.getAssignmentsSubmissionForFaculty1(1, Integer.MAX_VALUE, searchBean);
			List<AssignmentFileBean> faculty1List = page.getPageItems();
			assignmentFilesList.addAll(faculty1List);
			
			
			//page = dao.getAssignmentsForFaculty2(1, Integer.MAX_VALUE, searchBean);
			page = asgService.getAssignmentsSubmissionForFaculty2(1, Integer.MAX_VALUE, searchBean);
			List<AssignmentFileBean> faculty2List = page.getPageItems();
			assignmentFilesList.addAll(faculty2List);

			//page = dao.getAssignmentsForFaculty3(1, Integer.MAX_VALUE, searchBean);
			page = asgService.getAssignmentsSubmissionForFaculty3(1, Integer.MAX_VALUE, searchBean);
			List<AssignmentFileBean> faculty3List = page.getPageItems();
			assignmentFilesList.addAll(faculty3List);


			//page = dao.getAssignmentsForReval(1, Integer.MAX_VALUE, searchBean);
			page = asgService.getAssignmentsSubmissionForReval(1, Integer.MAX_VALUE, searchBean);
			List<AssignmentFileBean> faculty4List = page.getPageItems();
			assignmentFilesList.addAll(faculty4List);

			for(AssignmentFileBean assignmentFiles: assignmentFilesList) {
				  assignmentFiles.setFirstName(facultyName.getFirstName());
				  assignmentFiles.setLastName(facultyName.getLastName()); 
				  }

			modelnView.addObject("assignmentFilesList", assignmentFilesList);
			modelnView.addObject("page", page);

			modelnView.addObject("searchBean", searchBean);
			modelnView.addObject("rowCount", assignmentFilesList.size());

			modelnView.addObject("yearList", currentYearList);
			modelnView.addObject("subjectList", getSubjectList());

			if(assignmentFilesList == null || assignmentFilesList.size() == 0){
				request.setAttribute("error", "true");
				request.setAttribute("errorMessage", "No records found.");
			}

		}else{
			
			Page<AssignmentFileBean> page = dao.getAssignmentsForFacultyPage(1, pageSize, searchBean);
			List<AssignmentFileBean> assignmentFilesList = page.getPageItems();

			modelnView.addObject("assignmentFilesList", assignmentFilesList);
			modelnView.addObject("page", page);

			modelnView.addObject("searchBean", searchBean);
			modelnView.addObject("rowCount", page.getRowCount());

			modelnView.addObject("yearList", currentYearList);
			modelnView.addObject("subjectList", getSubjectList());

			if(assignmentFilesList == null || assignmentFilesList.size() == 0){
				request.setAttribute("error", "true");
				request.setAttribute("errorMessage", "No records found.");
			}
		}


		return modelnView;
	}



	@RequestMapping(value = "/searchAssignmentToEvaluatePage", method = {RequestMethod.GET, RequestMethod.POST})
	public ModelAndView searchAssignmentToEvaluatePage(HttpServletRequest request, HttpServletResponse response){
		if(!checkSession(request, response)){
			return new ModelAndView("login");
		}

		ModelAndView modelnView = new ModelAndView("searchAssignmentToEvaluate");
		AssignmentFileBean searchBean = (AssignmentFileBean)request.getSession().getAttribute("searchBean");
		String pageNoStr = request.getParameter("pageNo");

		int pageNo = 1; 
		if(pageNoStr != null){
			pageNo = Integer.parseInt(pageNoStr);
		}

		String facultyId = (String)request.getSession().getAttribute("userId");
		Person user = (Person)request.getSession().getAttribute("user");
		String roles = user != null ? user.getRoles() : "";
		//String roles = "Faculty";

		if(roles.indexOf("Faculty") != -1){
			//When its faculty show only their assignment, otherwise show all assignments
			searchBean.setFacultyId(facultyId);
		}


		//String facultyId = "NMSCEMU200303521";
		searchBean.setFacultyId(facultyId);


		AssignmentsDAO dao = (AssignmentsDAO)act.getBean("asignmentsDAO");

		if(roles.indexOf("Faculty") != -1){
			//No Paging for Faculty
			List<AssignmentFileBean> assignmentFilesList = new ArrayList<>();
			
			//get faculty name from cache for all list-Tushar
			HashMap<String, FacultyExamBean> facultyNamemap = getFacultyMap();
			FacultyExamBean facultyName = facultyNamemap.get(facultyId);

//			Page<AssignmentFileBean> page = dao.getAssignmentsForFaculty1(pageNo, Integer.MAX_VALUE, searchBean);
			Page<AssignmentFileBean> page = dao.getAssignmentsSubmissionForFaculty1(pageNo, Integer.MAX_VALUE, searchBean);
			List<AssignmentFileBean> faculty1List = page.getPageItems();
			assignmentFilesList.addAll(faculty1List);

//			page = dao.getAssignmentsForFaculty2(pageNo, Integer.MAX_VALUE, searchBean);
			page = dao.getAssignmentsSubmissionForFaculty2(pageNo, Integer.MAX_VALUE, searchBean);
			List<AssignmentFileBean> faculty2List = page.getPageItems();
			assignmentFilesList.addAll(faculty2List);

//			page = dao.getAssignmentsForFaculty3(pageNo, Integer.MAX_VALUE, searchBean);
			page = dao.getAssignmentsSubmissionForFaculty3(pageNo, Integer.MAX_VALUE, searchBean);
			List<AssignmentFileBean> faculty3List = page.getPageItems();
			assignmentFilesList.addAll(faculty3List);


//			page = dao.getAssignmentsForReval(pageNo, Integer.MAX_VALUE, searchBean);
			page = dao.getAssignmentsSubmissionForReval(pageNo, Integer.MAX_VALUE, searchBean);
			List<AssignmentFileBean> faculty4List = page.getPageItems();
			assignmentFilesList.addAll(faculty4List);


			for(AssignmentFileBean assignmentFiles: assignmentFilesList) {
				  assignmentFiles.setFirstName(facultyName.getFirstName());
				  assignmentFiles.setLastName(facultyName.getLastName()); 
				  }
			
			modelnView.addObject("assignmentFilesList", assignmentFilesList);
			modelnView.addObject("page", page);

			modelnView.addObject("searchBean", searchBean);
			modelnView.addObject("rowCount", assignmentFilesList.size());

			modelnView.addObject("yearList", currentYearList);
			modelnView.addObject("subjectList", getSubjectList());

			if(assignmentFilesList == null || assignmentFilesList.size() == 0){
				request.setAttribute("error", "true");
				request.setAttribute("errorMessage", "No records found.");
			}

		}else{
			Page<AssignmentFileBean> page = dao.getAssignmentsForFacultyPage(pageNo, pageSize, searchBean);
			List<AssignmentFileBean> assignmentFilesList = page.getPageItems();

			modelnView.addObject("assignmentFilesList", assignmentFilesList);
			modelnView.addObject("page", page);

			modelnView.addObject("searchBean", searchBean);
			modelnView.addObject("rowCount", page.getRowCount());

			modelnView.addObject("yearList", currentYearList);
			modelnView.addObject("subjectList", getSubjectList());

			if(assignmentFilesList == null || assignmentFilesList.size() == 0){
				request.setAttribute("error", "true");
				request.setAttribute("errorMessage", "No records found.");
			}
		}


		/*


		Page<AssignmentFileBean> page = dao.getAssignmentsForFacultyPage(pageNo, pageSize, searchBean);
		List<AssignmentFileBean> assignmentFilesList = page.getPageItems();

		modelnView.addObject("assignmentFilesList", assignmentFilesList);
		modelnView.addObject("page", page);

		modelnView.addObject("searchBean", searchBean);
		modelnView.addObject("rowCount", page.getRowCount());

		modelnView.addObject("yearList", currentYearList);
		modelnView.addObject("subjectList", getSubjectList());
		if(assignmentFilesList == null || assignmentFilesList.size() == 0){
			request.setAttribute("error", "true");
			request.setAttribute("errorMessage", "No records found.");
		}*/
		return modelnView;
	}

	@RequestMapping(value = "/resetFacultyAssignmentAllocationForm", method = {RequestMethod.GET, RequestMethod.POST})
	public String resetFacultyAssignmentAllocationForm(HttpServletRequest request, HttpServletResponse response, Model m) {
		if(!checkSession(request, response)){
			return "login";
		}

		AssignmentFileBean searchBean = new AssignmentFileBean();
		searchBean.setLevel(request.getParameter("level"));
		m.addAttribute("searchBean",searchBean);
		m.addAttribute("yearList", currentYearList);
		m.addAttribute("subjectList", getSubjectList());

		return "resetFacultyAssignmentAllocation";
	}

	@RequestMapping(value = "/searchFacultyAssignmentAllocation", method = {RequestMethod.GET, RequestMethod.POST})
	public String searchFacultyAssignmentAllocation(HttpServletRequest request, HttpServletResponse response, 
			@ModelAttribute AssignmentFileBean searchBean, Model m){
		if(!checkSession(request, response)){
			return "login";
		}

		m.addAttribute("searchBean",searchBean);
		m.addAttribute("yearList", currentYearList);
		m.addAttribute("subjectList", getSubjectList());

		searchBean.setEvaluated("N");
		request.getSession().setAttribute("searchBean", searchBean);

		AssignmentsDAO dao = (AssignmentsDAO)act.getBean("asignmentsDAO");

		Page<AssignmentFileBean> page = null;

		if("1".equalsIgnoreCase(searchBean.getLevel())){
			page = dao.getAssignmentsForFaculty1(1, Integer.MAX_VALUE, searchBean);
		}else if("2".equalsIgnoreCase(searchBean.getLevel())){
			page = dao.getAssignmentsForFaculty2(1, Integer.MAX_VALUE, searchBean);
		}else if("3".equalsIgnoreCase(searchBean.getLevel())){
			page = dao.getAssignmentsForFaculty3(1, Integer.MAX_VALUE, searchBean);
		}

		List<AssignmentFileBean> assignmentFilesList = page.getPageItems();

		m.addAttribute("assignmentFilesList", assignmentFilesList);
		m.addAttribute("pendingEvaluations", page.getRowCount());
		m.addAttribute("rowCount", page.getRowCount());
		m.addAttribute("page", page);
		return "resetFacultyAssignmentAllocation";
	}

	@RequestMapping(value = "/resetFacultyAssignmentAllocation", method = {RequestMethod.GET, RequestMethod.POST})
	public String resetFacultyAssignmentAllocation(HttpServletRequest request, HttpServletResponse response, 
			@ModelAttribute AssignmentFileBean searchBean, Model m){
		if(!checkSession(request, response)){
			return "login";
		}
		m.addAttribute("searchBean",searchBean);
		m.addAttribute("yearList", currentYearList);
		m.addAttribute("subjectList", getSubjectList());

		searchBean.setEvaluated("N");
		request.getSession().setAttribute("searchBean", searchBean);

		AssignmentsDAO dao = (AssignmentsDAO)act.getBean("asignmentsDAO");
		dao.resetFacultyAssignmentAllocation(searchBean);
		dao.resetFacultyAssignmentAllocationQuickTable(searchBean);
		setSuccess(request, "Faculty Allocation reset!");
		return searchFacultyAssignmentAllocation(request, response, searchBean, m);
	}

	@RequestMapping(value = "/evaluateAssignmentForm", method = {RequestMethod.GET, RequestMethod.POST})
	public ModelAndView evaluateAssignmentForm(HttpServletRequest request, HttpServletResponse response, @ModelAttribute AssignmentFileBean searchBean) {
		if(!checkSession(request, response)){
			return new ModelAndView("login");
		}
		
		ModelAndView modelnView = new ModelAndView("evaluateAssignment");
		modelnView.addObject("searchBean",searchBean);
		modelnView.addObject("yearList", currentYearList);
		modelnView.addObject("subjectList", getSubjectList());
		String programName = request.getParameter("programName");
		//TO show on evaluate Assignment page the marks weightage//
		if("All".equals(programName)){
			modelnView.addObject("weightage",30);
		}else if("ACBM".equals(programName)){
			modelnView.addObject("weightage",40);
		}
		String facultyId = (String)request.getSession().getAttribute("userId");
		//String facultyId = "NMSCEMU200303521";
		searchBean.setFacultyId(facultyId);
		AssignmentsDAO dao = (AssignmentsDAO)act.getBean("asignmentsDAO");
		StudentExamBean student = dao.getSingleStudentsData(searchBean.getSapId());
		searchBean.setConsumerProgramStructureId(student.getConsumerProgramStructureId());
		AssignmentFileBean assignmentFile = dao.getSingleAssignmentForFaculty(searchBean);
		
		if("All".equals(programName)){
			assignmentFile.setWeightage("30");
		}else if("ACBM".equals(programName)){
			assignmentFile.setWeightage("40");
		}
		populateAssignBsaedOnFaculty(facultyId, assignmentFile);
		modelnView.addObject("assignmentFile", assignmentFile);
		return modelnView; 
	}

	private void populateAssignBsaedOnFaculty(String facultyId,	AssignmentFileBean assignmentFile) {
		String level = "";
		if(facultyId.equalsIgnoreCase(assignmentFile.getFacultyId())){
			level = "1";
		}else if(facultyId.equalsIgnoreCase(assignmentFile.getFaculty2())){
			level = "2";
			assignmentFile.setEvaluationCount(assignmentFile.getFaculty2EvaluationCount());
			assignmentFile.setScore(assignmentFile.getFaculty2Score());
			assignmentFile.setRemarks(assignmentFile.getFaculty2Remarks());
			assignmentFile.setReason(assignmentFile.getFaculty2Reason());
		}else if(facultyId.equalsIgnoreCase(assignmentFile.getFaculty3())){
			level = "3";
			assignmentFile.setEvaluationCount(assignmentFile.getFaculty3EvaluationCount());
			assignmentFile.setScore(assignmentFile.getFaculty3Score());
			assignmentFile.setRemarks(assignmentFile.getFaculty3Remarks());
			assignmentFile.setReason(assignmentFile.getFaculty3Reason());
		}else if(facultyId.equalsIgnoreCase(assignmentFile.getFacultyIdRevaluation())){
			level = "4";
			assignmentFile.setEvaluationCount(assignmentFile.getRevaluationCount());
			assignmentFile.setScore(assignmentFile.getRevaluationScore());
			assignmentFile.setRemarks(assignmentFile.getRevaluationRemarks());
			assignmentFile.setReason(assignmentFile.getRevaluationReason());
		}
	}

	@RequestMapping(value = "/evaluateAssignment", method = {RequestMethod.GET, RequestMethod.POST})
	public ModelAndView evaluateAssignment(HttpServletRequest request, HttpServletResponse response, @ModelAttribute AssignmentFileBean searchBean) {
		if(!checkSession(request, response)){
			return new ModelAndView("login");
		}

		String facultyId = (String)request.getSession().getAttribute("userId");
		logger.info("evaluate assignment api call started for sapid:"+searchBean.getSapId()+" facultyId:"+facultyId+" subject:"+searchBean.getSubject());
		//String facultyId = "NMSCEMU200303521";
		searchBean.setFacultyId(facultyId);

		AssignmentsDAO dao = (AssignmentsDAO)act.getBean("asignmentsDAO");
		ModelAndView modelnView = new ModelAndView("evaluateAssignment");
		modelnView.addObject("searchBean",searchBean);
		modelnView.addObject("yearList", currentYearList);
		modelnView.addObject("subjectList", getSubjectList());
		StudentExamBean sbean= dao.getSingleStudentsData(searchBean.getSapId());
		logger.info("single student data fetched");
		searchBean.setConsumerProgramStructureId(sbean.getConsumerProgramStructureId());
		
		if(searchBean.getScore() == null || "".equals(searchBean.getScore().trim())){
			setError(request, "Score cannot be empty. Please enter a score.");
			AssignmentFileBean assignmentFile = dao.getSingleAssignmentForFaculty(searchBean);
			logger.info("single assignment for faculty data fetched");
			modelnView.addObject("assignmentFile", assignmentFile);
			return modelnView;
		}
		try {
			
			String level = "";
			AssignmentFileBean assignmentFileFromDB = dao.getSingleAssignmentForFaculty(searchBean);
			logger.info("single assignment for faculty data fetched");
			if(facultyId.equalsIgnoreCase(assignmentFileFromDB.getFacultyId())){
				level = "1";
			}else if(facultyId.equalsIgnoreCase(assignmentFileFromDB.getFaculty2())){
				level = "2";
			}else if(facultyId.equalsIgnoreCase(assignmentFileFromDB.getFaculty3())){
				level = "3";
			}else if(facultyId.equalsIgnoreCase(assignmentFileFromDB.getFacultyIdRevaluation())){
				level = "4";
			}
			logger.info("updating in assignment submission table for sapid:"+searchBean.getSapId()+" level:+"+level+" facultyId:"+facultyId);
			dao.evaluateAssignment(searchBean, level, facultyId);
			logger.info("updating in quick assignment submission table");
			dao.evaluateAssignmentQuickTable(searchBean, level);
			StudentExamBean student = dao.getSingleStudentsData(searchBean.getSapId());


			ArrayList<ProgramSubjectMappingExamBean> programSubjectMappingList = getProgramSubjectMappingList();

			for (int i = 0; i < programSubjectMappingList.size(); i++) {
				ProgramSubjectMappingExamBean bean = programSubjectMappingList.get(i);

				if(bean.getPrgmStructApplicable().equals(student.getPrgmStructApplicable()) && bean.getProgram().equals(student.getProgram())
						&& bean.getSubject().equals(searchBean.getSubject())){
					//Setting the semester for Subject 
					student.setSem(bean.getSem());
					break;
				}
			}

			StudentMarksBean marksBean = new StudentMarksBean();
			marksBean.setSapid(student.getSapid());
			marksBean.setSem(student.getSem());
			marksBean.setAssignmentscore(searchBean.getScore());
			marksBean.setStudentname(student.getFirstName() + " " + student.getLastName());
			marksBean.setSubject(searchBean.getSubject());
			marksBean.setProgram(student.getProgram());
			marksBean.setSyllabusYear("");
			marksBean.setGrno("Not Available");
			marksBean.setYear(searchBean.getYear());
			marksBean.setMonth(searchBean.getMonth());
			marksBean.setWritenscore("");
			marksBean.setCreatedBy(facultyId);
			marksBean.setLastModifiedBy(facultyId);

			//Commented as assignment final score will move after a separate Job is run
			//dao.upsertAssignmentMarks(marksBean);

			AssignmentFileBean assignmentFile = dao.getSingleAssignmentForFaculty(searchBean);
			logger.info("getting single assignment for faculty data before returning to jsp ");
			populateAssignBsaedOnFaculty(facultyId, assignmentFile);
			modelnView.addObject("assignmentFile", assignmentFile);

			setSuccess(request, "Assignment score saved successfully");
		} catch (Exception e) {
			
			setError(request, "Error in saving assignment score");
		}
		return modelnView; 
	}


	@RequestMapping(value = "/reEvaluateAssignment", method = {RequestMethod.GET, RequestMethod.POST})
	public ModelAndView reEvaluateAssignment(HttpServletRequest request, HttpServletResponse response, @ModelAttribute AssignmentFileBean searchBean) {
		if(!checkSession(request, response)){
			return new ModelAndView("login");
		}

		String facultyId = (String)request.getSession().getAttribute("userId");
		//String facultyId = "NMSCEMU200303521";
		searchBean.setFacultyId(facultyId);

		AssignmentsDAO dao = (AssignmentsDAO)act.getBean("asignmentsDAO");
		ModelAndView modelnView = new ModelAndView("evaluateAssignment");
		modelnView.addObject("searchBean",searchBean);
		modelnView.addObject("yearList", currentYearList);
		modelnView.addObject("subjectList", getSubjectList());

		if(searchBean.getRevaluationScore() == null || "".equals(searchBean.getRevaluationScore().trim())){
			setError(request, "Score cannot be empty. Please enter a score.");
			AssignmentFileBean assignmentFile = dao.getSingleAssignmentForFaculty(searchBean);
			modelnView.addObject("assignmentFile", assignmentFile);
			return modelnView;
		}
		try {

			dao.reEvaluateAssignment(searchBean);
			dao.reEvaluateAssignmentQuickTable(searchBean);
			StudentExamBean student = dao.getSingleStudentsData(searchBean.getSapId());

			ArrayList<ProgramSubjectMappingExamBean> programSubjectMappingList = getProgramSubjectMappingList();

			for (int i = 0; i < programSubjectMappingList.size(); i++) {
				ProgramSubjectMappingExamBean bean = programSubjectMappingList.get(i);

				if(bean.getPrgmStructApplicable().equals(student.getPrgmStructApplicable()) && bean.getProgram().equals(student.getProgram())
						&& bean.getSubject().equals(searchBean.getSubject())){
					//Setting the semester for Subject 
					student.setSem(bean.getSem());
					break;
				}
			}

			StudentMarksBean marksBean = new StudentMarksBean();
			marksBean.setSapid(student.getSapid());
			marksBean.setSem(student.getSem());
			marksBean.setAssignmentscore(searchBean.getRevaluationScore());//IMPORTANT
			marksBean.setStudentname(student.getFirstName() + " " + student.getLastName());
			marksBean.setSubject(searchBean.getSubject());
			marksBean.setProgram(student.getProgram());
			marksBean.setSyllabusYear("");
			marksBean.setGrno("Not Available");
			marksBean.setYear(searchBean.getYear());
			marksBean.setMonth(searchBean.getMonth());
			marksBean.setWritenscore("");
			marksBean.setCreatedBy(facultyId);
			marksBean.setLastModifiedBy(facultyId);

			//dao.upsertAssignmentMarks(marksBean);

			AssignmentFileBean assignmentFile = dao.getSingleAssignmentForFaculty(searchBean);
			modelnView.addObject("assignmentFile", assignmentFile);

			setSuccess(request, "Assignment revaluation score saved successfully");
		} catch (Exception e) {
			
			setError(request, "Error in saving assignment score");
		}
		return modelnView; 
	}

	@RequestMapping(value = "/revisitAssignment", method = {RequestMethod.GET, RequestMethod.POST})
	public ModelAndView revisitAssignment(HttpServletRequest request, HttpServletResponse response, @ModelAttribute AssignmentFileBean searchBean) {
		if(!checkSession(request, response)){
			return new ModelAndView("login");
		}

		String facultyId = (String)request.getSession().getAttribute("userId");
		//String facultyId = "NMSCEMU200303521";
		searchBean.setFacultyId(facultyId);

		AssignmentsDAO dao = (AssignmentsDAO)act.getBean("asignmentsDAO");
		ModelAndView modelnView = new ModelAndView("evaluateAssignment");
		modelnView.addObject("searchBean",searchBean);
		modelnView.addObject("yearList", currentYearList);
		modelnView.addObject("subjectList", getSubjectList());

		if(searchBean.getRevisitScore() == null || "".equals(searchBean.getRevisitScore().trim())){
			setError(request, "Score cannot be empty. Please enter a score.");
			AssignmentFileBean assignmentFile = dao.getSingleAssignmentForFaculty(searchBean);
			modelnView.addObject("assignmentFile", assignmentFile);
			return modelnView;
		}
		try {

			dao.revisitAssignment(searchBean);
			dao.revisitAssignmentQuickTable(searchBean);
			AssignmentFileBean assignmentFile = dao.getSingleAssignmentForFaculty(searchBean);
			modelnView.addObject("assignmentFile", assignmentFile);

			setSuccess(request, "Assignment revisit score saved successfully");
		} catch (Exception e) {
			
			setError(request, "Error in saving assignment revisit score");
		}
		return modelnView; 
	}


	@RequestMapping(value = "/downloadAssignmentEvaluatedExcel", method = {RequestMethod.GET, RequestMethod.POST})
	public ModelAndView downloadAssignmentEvaluatedExcel(HttpServletRequest request, HttpServletResponse response,@ModelAttribute AssignmentFileBean searchBean) {
		if(!checkSession(request, response)){
			return new ModelAndView("login");
		}
		searchBean = (AssignmentFileBean)request.getSession().getAttribute("searchBean");
		
		AssignmentsDAO dao = (AssignmentsDAO)act.getBean("asignmentsDAO");
		Page<AssignmentFileBean> page = dao.getAssignmentsForEvaluations(1, Integer.MAX_VALUE, searchBean);
		List<AssignmentFileBean> assignmentEvaluatedList = page.getPageItems();
		
		List<AssignmentFileBean> studentEndDateList=new ArrayList<AssignmentFileBean>();
		try {
			 studentEndDateList = dao.getstudentEndDate(searchBean.getMonth(),searchBean.getYear());
		} catch (Exception e) {
            //e.printStackTrace();
			// TODO: handle exception
		}
		Map<String,String> studEndateMap=new HashMap<String,String>();
		
		studentEndDateList.stream().forEach(studentEndDate->{
			studEndateMap.put(studentEndDate.getSapId(), studentEndDate.getEndDate());
		});
		
		assignmentEvaluatedList.stream().forEach(student->{
			
			student.setEndDate(studEndateMap.get(student.getSapId()));
		});
		
		
		/*ArrayList<AssignmentFileBean> assignmentEvaluatedList2 = dao.getAssignmentsForEvaluations2(searchBean);
		request.getSession().setAttribute("assignmentEvaluatedList2", assignmentEvaluatedList2);*/
		return new ModelAndView("assignmentEvaluatedExcelView","assignmentEvaluatedList",assignmentEvaluatedList);
	}

	@RequestMapping(value = "/downloadLevel3EligibleEvaluations", method = {RequestMethod.GET, RequestMethod.POST})
	public ModelAndView downloadLevel3EligibleEvaluations(HttpServletRequest request, HttpServletResponse response) {
		if(!checkSession(request, response)){
			return new ModelAndView("login");
		}
		List<AssignmentFileBean> assignmentEvaluatedList = (List<AssignmentFileBean>)request.getSession().getAttribute("level3EvaluationDifferenceList");
		return new ModelAndView("assignmentEvaluationLevel3EligibleExcelView","assignmentEvaluatedList",assignmentEvaluatedList);
	}
	
	@RequestMapping(value = "/downloadAssignmentReEvaluationExcel", method = {RequestMethod.GET, RequestMethod.POST})
	public ModelAndView downloadAssignmentReEvaluationExcel(HttpServletRequest request, HttpServletResponse response) {
		if(!checkSession(request, response)){
			return new ModelAndView("login");
		}
		ArrayList<AssignmentFileBean> revalAssignments = (ArrayList<AssignmentFileBean>)request.getSession().getAttribute("revalAssignments");
		
		return new ModelAndView("assignmentEvaluationLevel2ExcelView","assignmentEvaluatedList",revalAssignments);
	}

	@RequestMapping(value = "/changeEvaluationCount", method = {RequestMethod.GET, RequestMethod.POST})
	public @ResponseBody String changeEvaluationCount(HttpServletRequest request, HttpServletResponse response,
			@ModelAttribute AssignmentFileBean assignment, @RequestParam String value) {

		try {
			AssignmentsDAO dao = (AssignmentsDAO)act.getBean("asignmentsDAO");
			dao.changeEvaluationCount(assignment, value);
			dao.changeEvaluationCountQuickTable(assignment, value);
			return "Count changed successfully";
		}catch (Exception e) {
			
			return "Error in changing evaluation count";
		}
	}
	
	@RequestMapping(value = "/changeSubmissionCount", method = {RequestMethod.GET, RequestMethod.POST})
	public @ResponseBody String changeSubmissionCount(HttpServletRequest request, HttpServletResponse response,
			@ModelAttribute AssignmentFileBean assignment, @RequestParam int value) {

		try {
			AssignmentsDAO dao = (AssignmentsDAO)act.getBean("asignmentsDAO");
			String year = assignment.getYear();
			String month = assignment.getMonth();
			String sapid = assignment.getSapId();
			String subject = assignment.getSubject();
			dao.changeSubmissionCount(assignment, value);
			dao.changeSubmissionCountQuickTable(year,month,sapid,subject, value);
			
			return "Count changed successfully";
		}catch (Exception e) {
			
			return "Error in changing submission count";
		}
	}

	@RequestMapping(value = "/copyCaseCheckForm", method = {RequestMethod.GET, RequestMethod.POST})
	public String copyCaseCheckForm(HttpServletRequest request, HttpServletResponse response, Model m) {

		AssignmentFileBean searchBean = new AssignmentFileBean();
		//searchBean.setSubjectList(getSubjectList());
		m.addAttribute("searchBean",searchBean);
		m.addAttribute("yearList", currentYearList);
		//m.addAttribute("subjectList", getSubjectList());
		return "assignment/copyCaseCheck";
	}


	@RequestMapping(value = "/copyCaseCheck", method = {RequestMethod.GET, RequestMethod.POST})
	public ModelAndView copyCaseCheck(HttpServletRequest request, HttpServletResponse response, @ModelAttribute AssignmentFileBean searchBean){
		ModelAndView modelnView = new ModelAndView("assignment/copyCaseCheck");
		request.getSession().setAttribute("searchBean", searchBean);
		
		assignmentCopyCaseLogger.info("------------------Started CopyCase Selected Subject Count:{}  || Month-Year:{}-{} || Threshold 1-Threshold 2: {}-{} || Selected Subject List:{}-----------------",
				searchBean.getSubjectList().size(), searchBean.getMonth(), searchBean.getYear(),searchBean.getMinMatchPercent(),searchBean.getThreshold2(),searchBean.getSubjectList());
		
		AssignmentsDAO dao = (AssignmentsDAO)act.getBean("asignmentsDAO");
		if(StringUtils.isBlank(searchBean.getYear()) || StringUtils.isBlank(searchBean.getMonth()) || StringUtils.isBlank(searchBean.getSubjectList().toString())){
			setError(request,"Please enter all the fields before processing");
			return modelnView;
		}
		
		try {
			String userId = (String) request.getSession().getAttribute("userId");
			searchBean.setLastModifiedBy(userId); // save in dao calls

			for (String subject : searchBean.getSubjectList()) {
				searchBean.setSubject(subject);
				assignmentCopyCaseLogger.info("------------------ Started CopyCase Subject:{}  || Month-Year:{}-{} || Threshold 1-Threshold 2: {}-{} -----------------",
						searchBean.getSubject(), searchBean.getMonth(), searchBean.getYear(),searchBean.getMinMatchPercent(), searchBean.getThreshold2());

				Page<AssignmentFileBean> page = new Page<AssignmentFileBean>();
				try {
					page = dao.getAssignmentSubmissionPage(1, Integer.MAX_VALUE, searchBean);
				} catch (Exception e1) {
					// e1.printStackTrace();
					assignmentCopyCaseLogger.info("Exception Error getAssignmentSubmissionPage() Subject:{} || Month-Year:{}-{} || Threshold 1-Threshold 2: {}-{} || Error:{}",
							searchBean.getSubject(), searchBean.getMonth(), searchBean.getYear(),searchBean.getMinMatchPercent(), searchBean.getThreshold2(), e1);
				}
				List<AssignmentFileBean> assignmentFilesList = page.getPageItems();
				assignmentCopyCaseLogger.info("Student Assignment files found size:{} || Subject:{} || Month-Year:{}-{} || Threshold 1-Threshold 2: {}-{}",
						assignmentFilesList.size(), searchBean.getSubject(), searchBean.getMonth(),searchBean.getYear(), searchBean.getMinMatchPercent(), searchBean.getThreshold2());

				Page<AssignmentFileBean> pageQuestions = new Page<AssignmentFileBean>();
				try {
					pageQuestions = dao.getAssignmentFilesPage(1, pageSize, searchBean, "distinct");
				} catch (Exception e1) {
					// e1.printStackTrace();
					assignmentCopyCaseLogger.info("Exception Error getAssignmentFilesPage() Subject:{} || Month-Year:{}-{} || Threshold 1-Threshold 2: {}-{} || Error:{}",
							searchBean.getSubject(), searchBean.getMonth(), searchBean.getYear(),searchBean.getMinMatchPercent(), searchBean.getThreshold2(), e1);
				}
				List<AssignmentFileBean> assignmentQuestionFilesList = pageQuestions.getPageItems();
				assignmentCopyCaseLogger.info("Assignment QP files found size:{} || Subject:{} || Month-Year:{}-{} || Threshold 1-Threshold 2: {}-{}",
						assignmentQuestionFilesList.size(), searchBean.getSubject(), searchBean.getMonth(),searchBean.getYear(), searchBean.getMinMatchPercent(), searchBean.getThreshold2());

				modelnView.addObject("assignmentFilesList", assignmentFilesList);
				modelnView.addObject("page", page);
				modelnView.addObject("rowCount", page.getRowCount());

				if (assignmentFilesList == null || assignmentFilesList.size() == 0) {
					request.setAttribute("error", "true");
					request.setAttribute("errorMessage", "No records found.");
					return modelnView;
				}

				// ArrayList<ResultDomain> copyResult;
				try {
					assignmentCopyCaseLogger.info("--------------------CopyCase Algorithm Started Subject:{} || Month-Year:{}-{} || Threshold 1-Threshold 2: {}-{}-------------------",
							searchBean.getSubject(), searchBean.getMonth(), searchBean.getYear(),searchBean.getMinMatchPercent(), searchBean.getThreshold2());
					// copyResult = copyCaseHelper.checkCopyCases(assignmentFilesList);
					copyCaseHelper.checkAssignmentCopyCases(assignmentFilesList, searchBean, assignmentQuestionFilesList);

				} catch (Exception e) {
					assignmentCopyCaseLogger.info("Exception Error CopyCase Subject:{} || Month-Year:{}-{} || Threshold 1-Threshold 2: {}-{} || Error:{}",
							searchBean.getSubject(), searchBean.getMonth(), searchBean.getYear(),searchBean.getMinMatchPercent(), searchBean.getThreshold2(), e);
				}

				assignmentCopyCaseLogger.info("------------------ Ended CopyCase Subject:{}  || Month-Year:{}-{} || Threshold 1-Threshold 2: {}-{} -----------------",
						searchBean.getSubject(), searchBean.getMonth(), searchBean.getYear(),searchBean.getMinMatchPercent(), searchBean.getThreshold2());
			}
			setSuccess(request,"Copy check procedure initiated successfully. File will be created and saved on Server disk");
			assignmentCopyCaseLogger.info("------------------ Ended CopyCase Selected Subject Count:{}  || Month-Year:{}-{} || Threshold 1-Threshold 2: {}-{} || Selected Subject List:{}-----------------",
					searchBean.getSubjectList().size(), searchBean.getMonth(), searchBean.getYear(),searchBean.getMinMatchPercent(), searchBean.getThreshold2(), searchBean.getSubjectList());
			// return new ModelAndView("copyCaseExcelView","copyResult",copyResult);
		} catch (Exception e) {
			assignmentCopyCaseLogger.info(
					"Exception Error CopyCase Selected Subject Count:{} || Month-Year:{}-{} || Threshold 1-Threshold 2: {}-{} || Selected Subject List:{} || Error:{}",
					searchBean.getSubjectList().size(), searchBean.getMonth(), searchBean.getYear(),
					searchBean.getMinMatchPercent(), searchBean.getThreshold2(), searchBean.getSubjectList(), e);
			setError(request, "Error: " + e.getMessage());
		}
		searchBean.setSubjectList(getSubjectList());
		modelnView.addObject("yearList", currentYearList);
		modelnView.addObject("searchBean", searchBean);
		return modelnView;
	}
	
	
	@RequestMapping(value = "/searchAssignmentStatusForm", method = {RequestMethod.GET})
	public ModelAndView searchAssignmentStatusForm(HttpServletRequest request, HttpServletResponse response) {
		if(!checkSession(request, response)){
			redirectToPortalApp(response);
			return null;
		}
		ModelAndView modelnView = new ModelAndView("searchAssignmentStatus");
		
		AssignmentFileBean assignmentStatus = new AssignmentFileBean();
		AssignmentsDAO dao = (AssignmentsDAO)act.getBean("asignmentsDAO");		
		ArrayList<ConsumerProgramStructureExam> consumerType = dao.getConsumerTypeList();
		modelnView.addObject("consumerType", consumerType);
		modelnView.addObject("assignmentStatus",assignmentStatus);
		modelnView.addObject("yearList", currentYearList);
		modelnView.addObject("subjectList", getSubjectList());

		return modelnView;
	}

	@RequestMapping(value = "/searchAssignmentStatus", method = RequestMethod.POST)
	public ModelAndView searchAssignmentStatus(HttpServletRequest request, HttpServletResponse response, @ModelAttribute AssignmentFileBean assignmentStatus){
		if(!checkSession(request, response)){
			redirectToPortalApp(response);
			return null;
		}
		ModelAndView modelnView = new ModelAndView("searchAssignmentStatus");
		request.getSession().setAttribute("assignmentStatus", assignmentStatus);

		StudentMarksDAO dao = (StudentMarksDAO)act.getBean("studentMarksDAO");
		AssignmentsDAO adao = (AssignmentsDAO)act.getBean("asignmentsDAO");
		ArrayList<String> consumerProgramStructureIds =new ArrayList<String>();
		if(!StringUtils.isBlank(assignmentStatus.getProgramId()) && !StringUtils.isBlank(assignmentStatus.getProgramStructureId()) && !StringUtils.isBlank(assignmentStatus.getConsumerTypeId())){
			consumerProgramStructureIds = adao.getconsumerProgramStructureIds(assignmentStatus.getProgramId(),assignmentStatus.getProgramStructureId(),assignmentStatus.getConsumerTypeId());
		}
		
		String consumerProgramStructureIdsSaperatedByComma = "";
		if(!consumerProgramStructureIds.isEmpty()){
			for(int i=0;i < consumerProgramStructureIds.size();i++){
				consumerProgramStructureIdsSaperatedByComma = consumerProgramStructureIdsSaperatedByComma + ""+ consumerProgramStructureIds.get(i) +",";
			}
			consumerProgramStructureIdsSaperatedByComma = consumerProgramStructureIdsSaperatedByComma.substring(0,consumerProgramStructureIdsSaperatedByComma.length()-1);
		}
		
		assignmentStatus.setConsumerProgramStructureId(consumerProgramStructureIdsSaperatedByComma);
		Page<AssignmentStatusBean> page;
		
		if(CURRENT_EXAM_YEAR.equalsIgnoreCase(assignmentStatus.getYear()) && CURRENT_EXAM_MONTH.equalsIgnoreCase(assignmentStatus.getMonth())) {
			page = dao.getQuickAssignmentStatus(1, pageSize, assignmentStatus, getAuthorizedCodes(request));
		}else{
			page = dao.getAssignmentStatusPage(1, pageSize, assignmentStatus, getAuthorizedCodes(request));
		}
		
		List<AssignmentStatusBean> assignmentStatusList = page.getPageItems();

		modelnView.addObject("assignmentStatusList", assignmentStatusList);
		modelnView.addObject("page", page);
		ArrayList<ConsumerProgramStructureExam> consumerType = adao.getConsumerTypeList();
		modelnView.addObject("consumerType", consumerType);
		modelnView.addObject("assignmentStatus", assignmentStatus);
		modelnView.addObject("rowCount", page.getRowCount());

		modelnView.addObject("yearList", currentYearList);
		modelnView.addObject("subjectList", getSubjectList());
		if(assignmentStatusList == null || assignmentStatusList.size() == 0){
			request.setAttribute("error", "true");
			request.setAttribute("errorMessage", "No records found.");
		}
		return modelnView;
	}

	@RequestMapping(value = "/searchAssignmentStatusPage",  method = {RequestMethod.GET, RequestMethod.POST})
	public ModelAndView searchAssignmentStatusPage(HttpServletRequest request, HttpServletResponse response){
		if(!checkSession(request, response)){
			redirectToPortalApp(response);
			return null;
		}
		
		ModelAndView modelnView = new ModelAndView("searchAssignmentStatus");

		AssignmentFileBean assignmentStatus = (AssignmentFileBean)request.getSession().getAttribute("assignmentStatus");
		int pageNo = Integer.parseInt(request.getParameter("pageNo"));

		StudentMarksDAO dao = (StudentMarksDAO)act.getBean("studentMarksDAO");
		AssignmentsDAO adao = (AssignmentsDAO)act.getBean("asignmentsDAO");	
		Page<AssignmentStatusBean> page = dao.getAssignmentStatusPage(pageNo, pageSize, assignmentStatus, getAuthorizedCodes(request));
		List<AssignmentStatusBean> assignmentStatusList = page.getPageItems();

		modelnView.addObject("assignmentStatusList", assignmentStatusList);
		modelnView.addObject("page", page);
		ArrayList<ConsumerProgramStructureExam> consumerType = adao.getConsumerTypeList();
		modelnView.addObject("consumerType", consumerType);
		modelnView.addObject("assignmentStatus", assignmentStatus);
		modelnView.addObject("rowCount", page.getRowCount());

		modelnView.addObject("yearList", currentYearList);
		modelnView.addObject("subjectList", getSubjectList());
		if(assignmentStatusList == null || assignmentStatusList.size() == 0){
			request.setAttribute("error", "true");
			request.setAttribute("errorMessage", "No records found.");
		}
		return modelnView;
	}
	
	@RequestMapping(value = "/downloadAssignmentStatus", method = {RequestMethod.GET, RequestMethod.POST})
	public ModelAndView downloadAssignmentStatus(HttpServletRequest request, HttpServletResponse response) {
		if(!checkSession(request, response)){
			redirectToPortalApp(response);
			return null;
		}
		
		AssignmentFileBean assignmentStatus = (AssignmentFileBean)request.getSession().getAttribute("assignmentStatus");
		StudentMarksDAO dao = (StudentMarksDAO)act.getBean("studentMarksDAO");
		
		String userId = (String)request.getSession().getAttribute("userId");
		String authorizedCodes = getAuthorizedCodes(request);
		
		boolean isAuthorizedCodesValid = isAuthorizedCodesValid(authorizedCodes,userId);
		
		if(!isAuthorizedCodesValid) {//isAuthorizedCodesValid is false, then redirect to reports page

			request.setAttribute("error", "true");
			request.setAttribute("errorMessage", "Issue in generating Assignment Status report, Please verfiy if you have the authorization to access the report. ");
			return searchAssignmentStatusForm(request, response);
			
		}
		
		Page<AssignmentStatusBean> page;
		if(CURRENT_EXAM_YEAR.equalsIgnoreCase(assignmentStatus.getYear()) && CURRENT_EXAM_MONTH.equalsIgnoreCase(assignmentStatus.getMonth())) {
			page = dao.getQuickAssignmentStatus(1, Integer.MAX_VALUE, assignmentStatus, authorizedCodes);
		}else {
			page = dao.getAssignmentStatusPage(1, Integer.MAX_VALUE, assignmentStatus, authorizedCodes);
		}
		List<AssignmentStatusBean> assignmentStatusList = page.getPageItems();
		/*List<AssignmentStatusBean> assignmentStatusList = new ArrayList<AssignmentStatusBean>();
		HashMap<String,String> mapOfCurrentSemAndSapid = dao.mapOfCurrentSemAndSapid();
		for(AssignmentStatusBean assignmentBean : assignmentList){
			assignmentBean.setSem(mapOfCurrentSemAndSapid.get(assignmentBean.getSapid()));
			assignmentStatusList.add(assignmentBean);
		}*/
		
		return new ModelAndView("assignmentStatusReportExcelView","assignmentStatusList",assignmentStatusList);
	}
	
	private boolean isAuthorizedCodesValid(String authorizedCodes, String userId) {
		// TODO Auto-generated method stub
		try {
			String userIdToLowerCase = userId.toLowerCase();
			if(StringUtils.isBlank(authorizedCodes) 
				&& !usersWithNullOrEmptyAuthorizedCodes.contains(userIdToLowerCase)
					) {
				return false;
			}
			
			return true;
		} catch (Exception e) {
			// TODO Auto-generated catch block
			
			return false;
		}
	}
	@RequestMapping(value = "/searchANSForCenters", method =  RequestMethod.POST)
	public ModelAndView searchANSForCenters(HttpServletRequest request, HttpServletResponse response, @ModelAttribute AssignmentFileBean assignmentStatus){
		if(!checkSession(request, response)){
			redirectToPortalApp(response);
			return null;
		}
		
		ModelAndView modelnView = new ModelAndView("searchAssignmentStatus");
		request.getSession().setAttribute("searchBean", assignmentStatus);
		AssignmentsDAO adao = (AssignmentsDAO)act.getBean("asignmentsDAO");
		ArrayList<String> consumerProgramStructureIds =new ArrayList<String>();
		if(!StringUtils.isBlank(assignmentStatus.getProgramId()) && !StringUtils.isBlank(assignmentStatus.getProgramStructureId()) && !StringUtils.isBlank(assignmentStatus.getConsumerTypeId())){
			consumerProgramStructureIds = adao.getConsumerProgramStructureIdsForANS(assignmentStatus.getProgramId(),assignmentStatus.getProgramStructureId(),assignmentStatus.getConsumerTypeId());
		}
		
		String consumerProgramStructureIdsSaperatedByComma = "";
		if(!consumerProgramStructureIds.isEmpty()){
			for(int i=0;i < consumerProgramStructureIds.size();i++){
				consumerProgramStructureIdsSaperatedByComma = consumerProgramStructureIdsSaperatedByComma + ""+ consumerProgramStructureIds.get(i) +",";
			}
			consumerProgramStructureIdsSaperatedByComma = consumerProgramStructureIdsSaperatedByComma.substring(0,consumerProgramStructureIdsSaperatedByComma.length()-1);
		}
		
		assignmentStatus.setConsumerProgramStructureId(consumerProgramStructureIdsSaperatedByComma);
		AssignmentsDAO dao = (AssignmentsDAO)act.getBean("asignmentsDAO");
		Page<AssignmentFileBean> page = dao.getANS(1, Integer.MAX_VALUE, assignmentStatus, getAuthorizedCodes(request));
		List<AssignmentFileBean> assignmentFilesList = page.getPageItems();
		HashMap<String, StudentExamBean> sapIdStudentsMap = dao.getAllStudents();
		HashMap<String, CenterExamBean> centerCodeNameMap = getCentersMap();
		
		/*modelnView.addObject("assignmentFilesList", assignmentFilesList);
		modelnView.addObject("page", page);*/

		modelnView.addObject("searchBean", assignmentStatus);
		modelnView.addObject("rowCount", page.getRowCount());
		modelnView.addObject("assignmentStatus",assignmentStatus);
		modelnView.addObject("yearList", currentYearList);
		modelnView.addObject("subjectList", getSubjectList());
		
		modelnView.addObject("yearList", currentYearList);
		modelnView.addObject("subjectList", getSubjectList());
		if(assignmentFilesList == null || assignmentFilesList.size() == 0){
			request.setAttribute("error", "true");
			request.setAttribute("errorMessage", "No records found.");
			return modelnView;
		}
		/*HashMap<String, CenterBean> centerCodeNameMap = getCentersMap();
		for (AssignmentFileBean assignmentFileBean : assignmentFilesList) {
			String lc = centerCodeNameMap.get(assignmentFileBean.getCenterCode()) != null ? centerCodeNameMap.get(assignmentFileBean.getCenterCode()).getLc() : "No LC";
			assignmentFileBean.setLc(lc);
		}*/
		ExcelHelper excelHelper = new ExcelHelper();
			try {
				excelHelper.buildExcelDocumentForANSRecords(assignmentFilesList, request,response, sapIdStudentsMap, centerCodeNameMap,ASSIGNMENT_REPORTS_GENERATE_PATH);
			} catch (IOException e) {
					// TODO Auto-generated catch block
				logger.info("failed calling excel helper");
					
			}
			logger.info("ans file generation complete");
			return modelnView;
	}

	public  <K, V extends Comparable<? super V>> Map<K, V> 
	sortByValue( Map<K, V> map )
	{
		List<Map.Entry<K, V>> list =
				new LinkedList<Map.Entry<K, V>>( map.entrySet() );
		Collections.sort( list, new Comparator<Map.Entry<K, V>>()
				{
			public int compare( Map.Entry<K, V> o1, Map.Entry<K, V> o2 )
			{
				return (o1.getValue()).compareTo( o2.getValue() );
			}
				} );

		Map<K, V> result = new LinkedHashMap<K, V>();
		for (Map.Entry<K, V> entry : list)
		{
			result.put( entry.getKey(), entry.getValue() );
		}
		return result;
	}
	



	@RequestMapping(value = "/assignmentEvalReportWithMarksBifurcationNQuestionRemarksForm", method = {RequestMethod.GET})
	public String assignmentEvalReportWithMarksBifurcationNQuestionRemarksForm(HttpServletRequest request, HttpServletResponse response, Model m) {
		if(!checkSession(request, response)){
			redirectToPortalApp(response);
			return null;
		}
		
		AssignmentFileBean assignmentStatus = new AssignmentFileBean();
		AssignmentsDAO dao = (AssignmentsDAO)act.getBean("asignmentsDAO");		 
	 
		m.addAttribute("yearList", currentYearList);
		m.addAttribute("subjectList", getSubjectList());
		m.addAttribute("assignmentStatus",assignmentStatus);
		return "assgEvalReportWithMarksBifurcation";
	}
	
	@RequestMapping(value = "/asssignmentEvalReportWithMarksBifurcationNQuestionRemarks", method = {RequestMethod.GET, RequestMethod.POST})
	public ModelAndView asssignmentEvalReportWithMarksBifurcationNQuestionRemarks(HttpServletRequest request, HttpServletResponse response, @ModelAttribute AssignmentFileBean assignmentStatus) throws Exception {
		if(!checkSession(request, response)){
			redirectToPortalApp(response);
			return null;
		}
		ModelAndView modelnView = new ModelAndView("assgEvalReportWithMarksBifurcation");
		AssignmentsDAO dao = (AssignmentsDAO)act.getBean("asignmentsDAO");
		int pageSizeForReport=500000;
		Page<AssignmentFileBean> page  = dao.downloadEvaluationReport(assignmentStatus,1, pageSizeForReport );
		//Page<AssignmentStatusBean> page = dao.getAssignmentStatusPage(1, Integer.MAX_VALUE, assignmentStatus, getAuthorizedCodes(request));
		//List<AssignmentStatusBean> assignmentStatusList = page.getPageItems();
		List<AssignmentFileBean> assignmentFilesList = page.getPageItems();
		/*
		 * ExcelHelper excelHelper = new ExcelHelper();
		 * excelHelper.buildExcelDocumentForAssgEvaluationReport(assignmentFilesList,
		 * response, dao);
		 * 
		 * modelnView.addObject("yearList", currentYearList);
		 * modelnView.addObject("subjectList", getSubjectList()); AssignmentFileBean
		 * assignmentStatus1 = new AssignmentFileBean();
		 * modelnView.addObject("assignmentStatus",assignmentStatus1);
		 * 
		 * return modelnView;
		 */
		modelnView.addObject("yearList", currentYearList);
		modelnView.addObject("subjectList", getSubjectList());
		AssignmentFileBean assignmentStatus1 = new AssignmentFileBean();
		modelnView.addObject("assignmentStatus",assignmentStatus1);
		try {
			ExcelHelper excelHelper = new ExcelHelper();
			excelHelper.buildExcelDocumentForAssgEvaluationReport(assignmentFilesList, request,response,ASSIGNMENT_REPORTS_GENERATE_PATH, dao);
			} catch (IOException e) {
				//e.printStackTrace();
				// TODO Auto-generated catch block
			logger.info("failed calling excel helper");
				
		}
		logger.info("ans file generation complete");
		return modelnView;
	}
	@RequestMapping(value = "/submitAssignmentLiveDataForAStudent",  method = {RequestMethod.GET, RequestMethod.POST})
	public ResponseEntity<String>  submitAssignmentLiveDataForAStudent(HttpServletRequest request, HttpServletResponse response, @RequestParam String sapid, @RequestParam String sem,@RequestParam String month,@RequestParam String year) {
		 
		HttpHeaders headers = new HttpHeaders();
		headers.add("Content-Type", "application/json");
		AssignmentsDAO dao = (AssignmentsDAO)act.getBean("asignmentsDAO");
		
		String userId = "System";
	    String liveType = "Regular";
	    String examMonth="";
        if(month.equalsIgnoreCase("Jan")) {
        	examMonth="Jun";
        }else if (month.equalsIgnoreCase("Jul")) {
        	examMonth="Dec";
        }
        StudentExamBean student =  dao.getSingleStudentsData(sapid);
        AssignmentLiveSetting regularLive = dao.getCurrentLiveAssignment(student.getConsumerProgramStructureId(),"Regular");
		String regularLiveYear = regularLive.getExamYear();
		String regularLiveMonth = regularLive.getExamMonth();
					// If Any Option is Selected Is "All"
		logger.info("assg live cycle:"+regularLiveYear+regularLiveMonth+"---student's exam cycle:"+year+examMonth);
		if(!(regularLiveYear+regularLiveMonth).equalsIgnoreCase(year+examMonth)) {
			logger.info("assignment not live");
			return new ResponseEntity<String>("", headers, HttpStatus.OK);
		}
		
		logger.info("student to enter in quick table:"+student);
	 
	        	try {
	        		ExamAssignmentResponseBean respons = asgService.getAssignments(student.getSapid(),liveType);
	        		List<AssignmentFileBean>allAssignmentFilesList = respons.getAllAssignmentFilesList(); 
	        		logger.info("allAssignmentFilesList:"+allAssignmentFilesList);
	        		ArrayList<String> subjectsNotAllowedToSubmit=respons.getSubjectsNotAllowedToSubmit();
	        		List<AssignmentFileBean>beanList= new ArrayList<AssignmentFileBean>();
	        		for(AssignmentFileBean assignment : allAssignmentFilesList) {
	        			//String status = assignment.getStatus();  
	        			if(subjectsNotAllowedToSubmit!=null ) {
	        			if(subjectsNotAllowedToSubmit.contains(assignment.getSubject())){
	        				assignment.setStatus("Results Awaited");
	        				assignment.setSubmissionAllow("N");
	        			}  
	        			}
	        			if(assignment.getSem().equalsIgnoreCase(sem)) {
	        				beanList.add(assignment);
	        			}
	        		} 
	        		logger.info("final list of assignments to insert for student-"+student.getSapid()+":"+beanList);
	        		dao.insertIntoQuickAssignments(userId,student.getSapid(),beanList,year,examMonth);
				
				} catch (Exception e) { 
				
				} 
	      return new ResponseEntity<String>("", headers, HttpStatus.OK);
	}
	
	CompletableFuture<String> getStudentsAssignment( Executor executor, String adminId, StudentExamBean student, AssignmentFilesSetbean filesSet, int listSize, int runningCount){
		return CompletableFuture.supplyAsync(() -> {
			List<Integer> completeStatus = (List<Integer>) Arrays.asList(10, 20, 30, 40, 50, 60, 70, 80, 90, 100);
			assignmentMakeLive.info("Entry for Sapid: {} and count: {}", student.getSapid(), runningCount);
			AssignmentsDAO dao = (AssignmentsDAO) act.getBean("asignmentsDAO");
			try {
				ExamAssignmentResponseBean respons = asgService.getAssignments(student.getSapid(),
						filesSet.getLiveType());
				List<AssignmentFileBean> allAssignmentFilesList = respons.getAllAssignmentFilesList();
				ArrayList<String> subjectsNotAllowedToSubmit = respons.getSubjectsNotAllowedToSubmit();

				for (AssignmentFileBean assignment : allAssignmentFilesList) {
					// String status = assignment.getStatus();
					if (subjectsNotAllowedToSubmit != null) {
						if (subjectsNotAllowedToSubmit.contains(assignment.getSubject())) {
							assignment.setStatus("Results Awaited");
							assignment.setSubmissionAllow("N");
						}
					}
				}
				dao.insertIntoQuickAssignments(adminId, student.getSapid(), allAssignmentFilesList,
						filesSet.getExamYear(), filesSet.getExamMonth());
			} catch (Exception e) {
				// TODO Auto-generated catch block
				// e.printStackTrace();
				assignmentMakeLive.error("Error in Assignment make live Sapid - {} Count - {} Error- {}",
						student.getSapid(), runningCount, e);
			}

			float completedCount = ((float) runningCount / (float) listSize) * 100;
//			assignmentMakeLive.info(" Assignment make live completed status - {}%", (int) completedCount);
			if (completeStatus.contains((int) completedCount)) {
				int countLeft = listSize - runningCount;
				assignmentMakeLive.info(
						" Assignment make live {}% completed || Students inserted entries count = {} || Students left count = {}/{}",
						(int) completedCount, runningCount, countLeft, listSize);
			}

			return "abc";
		}, executor
		);
	}
	
	@RequestMapping(value = "/getCopyCaseReportForm", method = {RequestMethod.GET})
	public String getCopyCaseReportForm(HttpServletRequest request, HttpServletResponse response, Model m) {
		ResultDomain searchBean = new ResultDomain();
		m.addAttribute("searchBean", searchBean);
		m.addAttribute("yearList", currentYearList);
		m.addAttribute("examMonthList", EXAM_MONTH_LIST);
		m.addAttribute("subjectList", getSubjectList());
		return "assignment/getCopyCaseReport";
	}
	
	@RequestMapping(value = "/downloadAssignmentCopyCaseReport", method = {RequestMethod.POST})
	public ModelAndView downloadAssignmentCCReport(HttpServletRequest request, HttpServletResponse response, @ModelAttribute ResultDomain searchBean) throws Exception {
		ModelAndView model = new ModelAndView("assignment/getCopyCaseReport");
		model.addObject("searchBean", searchBean);
		model.addObject("yearList", currentYearList);
		model.addObject("examMonthList", EXAM_MONTH_LIST);
		model.addObject("subjectList", getSubjectList());
		ModelAndView excelView = new ModelAndView("AssignmentCopyCaseReportExcelView");
		/*String tableName = "";
		List<ResultDomain> unique1CCList = null;
		assignmentCopyCaseLogger.info("Downloading CC report Month-Year:{}{} Subject:{} Threshold 1-Threshold 2: {}-{}", searchBean.getMonth(), searchBean.getYear(), searchBean.getSubject(), searchBean.getMinMatchPercent(), searchBean.getThreshold2());
		try {
			tableName = "unique_threshold_1";
			unique1CCList = asgService.getCopyCaseReport(searchBean,tableName);
			if(unique1CCList != null && unique1CCList.size() >0) {
				excelView.addObject("unique1CCList",unique1CCList);
			}
		} catch (Exception e) {
			assignmentCopyCaseLogger.error("Exception Error getCopyCaseReport() for table:{} Month-Year:{}{} Subject:{} Threshold 1-Threshold 2: {}-{} Error:{}",
					tableName, searchBean.getMonth(), searchBean.getYear(), searchBean.getSubject(), searchBean.getMinMatchPercent(), searchBean.getThreshold2(), e);
//			e.printStackTrace();
		}
		List<ResultDomain> above90CCList = null;
		try {
			tableName = "students_above_90";
			above90CCList = asgService.getCopyCaseReport(searchBean,tableName);
			if(above90CCList != null && above90CCList.size() >0) {
				excelView.addObject("above90CCList",above90CCList);
			}
		} catch (Exception e) {
			assignmentCopyCaseLogger.error("Exception Error getCopyCaseReport() for table:{} Month-Year:{}{} Subject:{} Threshold 1-Threshold 2: {}-{} Error:{}",
					tableName, searchBean.getMonth(), searchBean.getYear(), searchBean.getSubject(), searchBean.getMinMatchPercent(), searchBean.getThreshold2(), e);
//			e.printStackTrace();
		}
		
		// If threshold 2 is same as threshold 1 then avoid threshold 2 list
		if(searchBean.getMinMatchPercent() != searchBean.getThreshold2()) {
			List<ResultDomain> unique2CCList = null;
			try {
				tableName = "unique_threshold_2";
				unique2CCList = asgService.getCopyCaseReport(searchBean,tableName);
				if(unique2CCList != null && unique2CCList.size() >0) {
					excelView.addObject("unique2CCList",unique2CCList);
				}
			} catch (Exception e) {
				assignmentCopyCaseLogger.error("Exception Error getCopyCaseReport() for table:{} Month-Year:{}{} Subject:{} Threshold 1-Threshold 2: {}-{} Error:{}",
						tableName ,searchBean.getMonth(), searchBean.getYear(), searchBean.getSubject(), searchBean.getMinMatchPercent(), searchBean.getThreshold2(), e);
				// e.printStackTrace();
			}
		}*/
		excelView.addObject("searchBean", searchBean);
		excelView.addObject("CCLogger",assignmentCopyCaseLogger);
		ReadCopyCasesListBean listBean = new  ReadCopyCasesListBean();
		CopyCaseInterface CCInterface = CCFactory.getProductType(CopyCaseFactory.ProductType.ASSIGNMENT);
		
		//get all unique and above 90 students
		listBean = CCInterface.readCopyCasesList(searchBean, listBean, assignmentCopyCaseLogger);
		if(listBean.getErrorMessage() != null && !listBean.getErrorMessage().equals("")){
			setError(request, "Error getting Copy Cases records for Month-Year:"+searchBean.getMonth()+searchBean.getYear()+" Subject:"+searchBean.getSubject()+" Error:"+listBean.getErrorMessage());
			return model;
		}
		
		// If no records found in any threshold
		if(listBean.getUnique1CCList().size() == 0 && listBean.getAbove90CCList().size() == 0) {
			setError(request, "Error No Copy Cases records for Month-Year:"+searchBean.getMonth()+searchBean.getYear()+" Subject:"+searchBean.getSubject());
			return model;
		}
		
		// create excel report
		CCInterface.createCopyCasesList(searchBean, listBean, excelView, assignmentCopyCaseLogger);
		if(listBean.getErrorMessage() != null && !listBean.getErrorMessage().equals("")){
			setError(request, "Error creating Copy Cases report for Month-Year:"+searchBean.getMonth()+searchBean.getYear()+" Subject:"+searchBean.getSubject()+" Error:"+listBean.getErrorMessage());
			return model;
		}
		
		return excelView;
	}
	
	@RequestMapping(value = "/assignmentDetailedThresholdCCForm", method = {RequestMethod.GET})
	public String getAssignmentDetailedThresholdCCForm(HttpServletRequest request, HttpServletResponse response, Model m) {
		ResultDomain searchBean = new ResultDomain();
		m.addAttribute("searchBean", searchBean);
		m.addAttribute("yearList", currentYearList);
		m.addAttribute("examMonthList", EXAM_MONTH_LIST);
		m.addAttribute("subjectList", getSubjectList());
		return "assignment/detailedThresholdCopyCases";
	}
	
	@RequestMapping(value="/searchAssignmentDetailedThresholdCC", method= {RequestMethod.POST,RequestMethod.GET})
	public ModelAndView getAssignmentDetailedThresholdCC(HttpServletRequest request, HttpServletResponse response, @ModelAttribute ResultDomain searchBean) {
		ModelAndView view = new ModelAndView("assignment/detailedThresholdCopyCases");
		view.addObject("yearList", currentYearList);
		view.addObject("examMonthList", EXAM_MONTH_LIST);
		view.addObject("subjectList", getSubjectList());
		view.addObject("searchBean", searchBean);
		try {
			/*
			// Detailed Threshold 1
			List<ResultDomain> detailedThreshold1List = null;
			try {
				detailedThreshold1List = asgService.getDetailedThreshold1CC(searchBean.getMonth(), searchBean.getYear(), searchBean.getSubject(), searchBean.getSapId1()) ;
				if(detailedThreshold1List != null && detailedThreshold1List.size() > 0) {
					view.addObject("DT1ListCount",detailedThreshold1List.size());
					view.addObject("detailedThreshold1List",detailedThreshold1List);
				}
			} catch (Exception e) {
				setError(request, "Error getting records for Detailed Threshold 1 for Month:"+searchBean.getMonth()+" Year:"+searchBean.getYear()+" Subject:"+searchBean.getSubject()+" Error:"+e.getMessage());
				assignmentCopyCaseLogger.error("Exception Error getDetailedThresholdCopyCases() for Detailed Threshold 2 Month-Year:{}{} Subject:{} Sapid:{} Error:{}",
						searchBean.getMonth(), searchBean.getYear(), searchBean.getSubject(), searchBean.getSapId1(), e);
//				e.printStackTrace();
				return view;
			}
			
			// Detailed Threshold 2
			List<ResultDomain> detailedThreshold2List = null;
			try {
				detailedThreshold2List = asgService.getDetailedThreshold2CC(searchBean.getMonth(), searchBean.getYear(), searchBean.getSubject(), searchBean.getSapId1());
				if(detailedThreshold2List != null && detailedThreshold2List.size() > 0) {
					view.addObject("DT2ListCount",detailedThreshold2List.size());
					view.addObject("detailedThreshold2List",detailedThreshold2List);
				}
			} catch (Exception e) {
				setError(request, "Error getting Detailed Threshold 2 for Month:"+searchBean.getMonth()+" Year:"+searchBean.getYear()+" Subject:"+searchBean.getSubject()+" Error:"+e.getMessage());
				assignmentCopyCaseLogger.error("Exception Error getDetailedThresholdCopyCases() for Detailed Threshold 2 Month-Year:{}{} Subject:{} Sapid:{} Error:{}",
						searchBean.getMonth(), searchBean.getYear(), searchBean.getSubject(), searchBean.getSapId1(), e);
			
//				e.printStackTrace();
			}
			String erroMessage = "";
			String sapidError ="";
			if(detailedThreshold1List.size() == 0) {
				erroMessage = " Detailed Threshold 1 and ";
			}
			if(detailedThreshold2List.size() == 0) {
				erroMessage = erroMessage + " Detailed Threshold 2 ";
			}
			if(searchBean.getSapId1() != null && !(searchBean.getSapId1().equals(""))) {
				sapidError = "   and Sapid:" + searchBean.getSapId1();
			}
			if(erroMessage != null && !erroMessage.equals("")) {
				setError(request, "Error: No records found for "+erroMessage+"   Month-Year:"+searchBean.getMonth()+searchBean.getYear()+"   Subject:"+searchBean.getSubject()+sapidError);
				assignmentCopyCaseLogger.error("Error: No records found for Detailed Threshold 2 Month-Year:{}{} Subject:{} Sapid:{}",
						searchBean.getMonth(), searchBean.getYear(), searchBean.getSubject(), searchBean.getSapId1());
			}
			*/
			
			ReadCopyCasesListBean listBean = new  ReadCopyCasesListBean();
			CopyCaseInterface CCInterface = CCFactory.getProductType(CopyCaseFactory.ProductType.ASSIGNMENT);
			
			//get all Detailed Threshold 1 and 2 students
			CCInterface.readDetailedThresholdCCList(searchBean, listBean, assignmentCopyCaseLogger);
			if(listBean.getErrorMessage() != null && !listBean.getErrorMessage().equals("")){
				setError(request, "Error getting Detailed Threshold records for Month-Year:"+searchBean.getMonth()+searchBean.getYear()+" Subject:"+searchBean.getSubject()+" Error:"+listBean.getErrorMessage());
				return view;
			}
			
			//create filter report in front end
			CCInterface.createDetailedThresholdCCList(searchBean, listBean, view, assignmentCopyCaseLogger);
			if(listBean.getErrorMessage() != null && !listBean.getErrorMessage().equals("")){
				setError(request, listBean.getErrorMessage());
			}
		} catch (Exception e) {
			setError(request, "Error getting Detailed Threshold data for Month:"+searchBean.getMonth()+" Year:"+searchBean.getYear()+" Subject:"+searchBean.getSubject()+" Error:"+e.getMessage());
			assignmentCopyCaseLogger.error("Exception Error getting Detailed Threshold data Month-Year:{}{} Subject:{} Sapid:{} Error:{}",
					searchBean.getMonth(), searchBean.getYear(), searchBean.getSubject(), searchBean.getSapId1(), e);
		}
		return view;
	}
		
}


