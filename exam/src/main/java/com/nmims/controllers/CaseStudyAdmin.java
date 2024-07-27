package com.nmims.controllers;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.commons.lang.RandomStringUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.commons.CommonsMultipartFile;
import org.springframework.web.servlet.ModelAndView;

import com.itextpdf.text.log.SysoCounter;
import com.nmims.beans.AssignmentFileBean;
import com.nmims.beans.AssignmentFilesSetbean;
import com.nmims.beans.CaseStudyExamBean;
import com.nmims.beans.CenterExamBean;
import com.nmims.beans.FacultyExamBean;
import com.nmims.beans.Page;
import com.nmims.beans.PassFailExamBean;
import com.nmims.beans.Person;
import com.nmims.beans.ProgramSubjectMappingExamBean;
import com.nmims.beans.StudentExamBean;
import com.nmims.beans.StudentMarksBean;
import com.nmims.daos.AssignmentsDAO;
import com.nmims.daos.CaseStudyDao;
import com.nmims.daos.FacultyDAO;
import com.nmims.daos.ProjectSubmissionDAO;
import com.nmims.daos.StudentMarksDAO;
import com.nmims.helpers.EmailHelper;
import com.nmims.helpers.MailSender;

@Controller
@RequestMapping("/admin")
public class CaseStudyAdmin extends BaseController {
	@Autowired
	ApplicationContext act;
	
	@Value("#{'${ACAD_YEAR_SAS_LIST}'.split(',')}")
	private List<String> ACAD_YEAR_SAS_LIST;
	
	@Value( "${CASESTUDY_FILES_PATH}" )
	private String CASESTUDY_FILES_PATH;
	
	@Value( "${SAS_EXAM_MONTH_LIST}" )
	private List<String> SAS_EXAM_MONTH_LIST;
	
	@Value( "${CASESTUDY_TOPICS}" )
	private  List<String> CASESTUDY_TOPICS;
	
	@Value( "${REPORTS}" )
	private  String REPORTS;
	
	
	/*Not required as only single faculty will evaluate
	 * @Value( "${MAX_CASE_PER_FACULTY}" )
	private String MAX_CASE_PER_FACULTY;
	*/
	
	@Autowired
	MailSender mailSender;
	
	@Autowired
	ProjectSubmissionDAO projectSubmissionDAO;
	
	@ModelAttribute("q1MarksOptionsList")
	public ArrayList<String> getQ1MarksOptionsList(){
		return new ArrayList<String>(Arrays.asList( "0","1", "2","3","4","5","6","7","8","9","10")); 
	}
	
	@ModelAttribute("q2MarksOptionsList")
	public ArrayList<String> getQ2MarksOptionsList(){
		return new ArrayList<String>(Arrays.asList( "0","1", "2","3","4","5","6","7","8","9","10","11","12","13","14","15")); 
	}
	
	@ModelAttribute("q3MarksOptionsList")
	public ArrayList<String> getQ3MarksOptionsList(){
		return new ArrayList<String>(Arrays.asList( "0","1", "2","3","4","5","6","7","8","9","10","11","12","13","14","15","16","17","18","19","20","21","22","23","24","25")); 
	}
	
	@ModelAttribute("q4MarksOptionsList")
	public ArrayList<String> getQ4MarksOptionsList(){
		return new ArrayList<String>(Arrays.asList( "0","1", "2","3","4","5","6","7","8","9","10","11","12","13","14","15","16","17","18","19","20","21","22","23","24","25","26","27","28","29","30")); 
	}
	
	@ModelAttribute("q5MarksOptionsList")
	public ArrayList<String> getQ5MarksOptionsList(){
		return new ArrayList<String>(Arrays.asList( "0","1", "2","3","4","5","6","7","8","9","10")); 
	}
	
	@ModelAttribute("q6MarksOptionsList")
	public ArrayList<String> getQ6MarksOptionsList(){
		return new ArrayList<String>(Arrays.asList( "0","1", "2","3","4","5","6","7","8","9","10")); 
	}

	private ArrayList<String> evaluationStatus = new ArrayList<String>(Arrays.asList( "Y","N"));  

	
	@RequestMapping(value = "/uploadCaseStudyFilesForm", method = {RequestMethod.GET, RequestMethod.POST})
	public String uploadCaseStudyFilesForm(HttpServletRequest request, HttpServletResponse respnse, Model m) {

		CaseStudyExamBean  csBean = new CaseStudyExamBean();
		m.addAttribute("csBean",csBean);
		m.addAttribute("yearList", ACAD_YEAR_SAS_LIST);
		m.addAttribute("monthList", SAS_EXAM_MONTH_LIST);
		m.addAttribute("caseStudyTopicList", CASESTUDY_TOPICS);

		return "caseStudyFiles/uploadCaseStudyFiles";
	}
	
	@RequestMapping(value = "/uploadCaseStudyFiles", method = {RequestMethod.POST})
	public ModelAndView uploadCaseStudyFiles(HttpServletRequest request, HttpServletResponse respnse, 
			@ModelAttribute CaseStudyExamBean csBean) {
		ModelAndView modelnView = new ModelAndView("caseStudyFiles/uploadCaseStudyFiles");
		modelnView.addObject("csBean",csBean);
		modelnView.addObject("yearList", ACAD_YEAR_SAS_LIST);
		modelnView.addObject("monthList", SAS_EXAM_MONTH_LIST);
		modelnView.addObject("caseStudyTopicList", CASESTUDY_TOPICS);
		List<CaseStudyExamBean> caseStudyFiles = csBean.getCaseStudyFiles();
		
		CaseStudyDao dao = (CaseStudyDao)act.getBean("caseDAO");
		int successCount = 0;
		String topics = "";
		for (int i = 0; i < caseStudyFiles.size(); i++) {

			CaseStudyExamBean bean = caseStudyFiles.get(i);

			String fileName = bean.getFileData().getOriginalFilename();  

			if(fileName == null || "".equals(fileName.trim()) || "".equals(bean.getTopic())){
				//If no file is selected, do not do any operation.
				continue;
			}

			String errorMessage = uploadAssignmentFile(bean, csBean.getBatchYear(), csBean.getBatchMonth());

			//Check if file saved to Disk successfully
			if(errorMessage == null){
				String userId = (String)request.getSession().getAttribute("userId");
				
				bean.setLastModifiedBy(userId);
				
				dao.saveCaseStudyDetails(bean, csBean.getBatchYear(), csBean.getBatchMonth(),csBean.getStartDate(),csBean.getEndDate());
				successCount++;
				topics = topics + " : " +bean.getTopic() ;

			}else{
				request.setAttribute("error", "true");
				request.setAttribute("errorMessage", "Error in file Upload: "+errorMessage);
				modelnView.addObject("csBean",csBean);
				modelnView.addObject("yearList", ACAD_YEAR_SAS_LIST);
				
				return modelnView;
			}
		}

		request.setAttribute("success","true");
		request.setAttribute("successMessage",successCount + " Files Uploaded successfully for "+topics);

		csBean = new CaseStudyExamBean();
		modelnView.addObject("csBean",csBean);
		modelnView.addObject("yearList", ACAD_YEAR_SAS_LIST);
		

		return modelnView;
	}

	private String uploadAssignmentFile(CaseStudyExamBean bean, String year, String month) {

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
		try {   
			inputStream = file.getInputStream();   
			String filePath = CASESTUDY_FILES_PATH + month + year + "/" +fileName;

			String previewPath = month + year + "/" + fileName;
			//Check if Folder exists which is one folder per Exam (Jun2015, Dec2015 etc.) 
			File folderPath = new File(CASESTUDY_FILES_PATH  + month + year );
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
			errorMessage = "Error in uploading file for "+bean.getTopic() + " : "+ e.getMessage();
			   
		}   catch(Exception e){
			
		}
	
		return errorMessage;
	}
	
	@RequestMapping(value = "/searchSubmittedCaseStudyFilesForm", method = {RequestMethod.GET})
	public ModelAndView searchSubmittedCaseStudyFilesForm(HttpServletRequest request,HttpServletResponse response,@ModelAttribute CaseStudyExamBean csBean){
		if(!checkSession(request, response)){
			return new ModelAndView("login");
		}
		ModelAndView modelAndView = new ModelAndView("caseStudyFiles/searchSubmittedCaseStudyFiles");
			modelAndView.addObject("csBean",csBean);
			modelAndView.addObject("yearList", ACAD_YEAR_SAS_LIST);
			modelAndView.addObject("monthList", SAS_EXAM_MONTH_LIST);
			modelAndView.addObject("submittedCaseStudySize",0);
		return modelAndView;
	}

	
	@RequestMapping(value = "/searchSubmittedCaseStudyFiles", method = {RequestMethod.POST})
	public ModelAndView searchSubmittedCaseStudyFiles(HttpServletRequest request,HttpServletResponse response,@ModelAttribute CaseStudyExamBean csBean){
		if(!checkSession(request, response)){
			return new ModelAndView("login");
		}
		ModelAndView modelAndView = new ModelAndView("caseStudyFiles/searchSubmittedCaseStudyFiles");
			modelAndView.addObject("csBean",csBean);
			modelAndView.addObject("yearList", ACAD_YEAR_SAS_LIST);
			modelAndView.addObject("monthList", SAS_EXAM_MONTH_LIST);
			
		CaseStudyDao dao = (CaseStudyDao)act.getBean("caseDAO");
		List<CaseStudyExamBean> submittedCaseStudy = dao.getAllSubmittedCaseStudyDetails(csBean.getBatchYear(), csBean.getBatchMonth());
		modelAndView.addObject("submittedCaseStudy",submittedCaseStudy);
		modelAndView.addObject("submittedCaseStudySize",submittedCaseStudy.size());
		return modelAndView;
	}
	
	@RequestMapping(value = "/assignSubmittedCaseStudyToFacultyForm", method = {RequestMethod.GET})
	public ModelAndView assignSubmittedCaseStudyToFacultyForm(HttpServletRequest request,HttpServletResponse response,@ModelAttribute CaseStudyExamBean csBean){
		if(!checkSession(request, response)){
			return new ModelAndView("login");
		}
		ModelAndView modelAndView = new ModelAndView("caseStudyFiles/assignSubmittedCaseStudyToFaculty");
			modelAndView.addObject("csBean",csBean);
			modelAndView.addObject("yearList", ACAD_YEAR_SAS_LIST);
			modelAndView.addObject("monthList", SAS_EXAM_MONTH_LIST);
	
		return modelAndView;
	}
	
	@RequestMapping(value = "/assignSubmittedCaseStudyToFaculty", method = {RequestMethod.POST})
	public ModelAndView assignSubmittedCaseStudyToFaculty(HttpServletRequest request,HttpServletResponse response,@ModelAttribute CaseStudyExamBean csBean){
		if(!checkSession(request, response)){
			return new ModelAndView("login");
		}
		ModelAndView modelAndView = new ModelAndView("caseStudyFiles/assignSubmittedCaseStudyToFaculty");
			modelAndView.addObject("csBean",csBean);
			modelAndView.addObject("yearList", ACAD_YEAR_SAS_LIST);
			modelAndView.addObject("monthList", SAS_EXAM_MONTH_LIST);
			ArrayList<FacultyExamBean> facultyList = getFacultyAndCurrentCaseStudyNumber(csBean);
			CaseStudyDao dao = (CaseStudyDao)act.getBean("caseDAO");
			List<CaseStudyExamBean> uncheckedCaseStudy = dao.getAllUncheckedCaseStudyDetails(csBean.getBatchYear(), csBean.getBatchMonth(), csBean.getProgram());
			modelAndView.addObject("uncheckedCaseStudy",uncheckedCaseStudy);
			modelAndView.addObject("uncheckedCaseStudySize",uncheckedCaseStudy.size());
			modelAndView.addObject("facultyList", facultyList);
		//	modelAndView.addObject("MAX_CASE_PER_FACULTY", MAX_CASE_PER_FACULTY); Exam team test update :: No max limit to be given.
		return modelAndView;
	}
	
	private ArrayList<FacultyExamBean> getFacultyAndCurrentCaseStudyNumber(CaseStudyExamBean searchBean) {
		FacultyDAO fDao = (FacultyDAO)act.getBean("facultyDAO");
		ArrayList<FacultyExamBean> facultyListWithAssignmentCount = fDao.getFacultiesWithCaseStudyCount(searchBean);
		HashMap<String, String> facultyAssignmentCountMap = new HashMap<>();
		for (FacultyExamBean facultyBean : facultyListWithAssignmentCount) {
			facultyAssignmentCountMap.put(facultyBean.getFacultyId(), facultyBean.getAssignmentsAllocated());
		}
		ArrayList<FacultyExamBean> facultyList = fDao.getFacultiesForCaseStudyEvaluation();

		for (FacultyExamBean facultyBean : facultyList) {
			if(facultyAssignmentCountMap.containsKey(facultyBean.getFacultyId())){
				facultyBean.setAssignmentsAllocated(facultyAssignmentCountMap.get(facultyBean.getFacultyId()));
				//facultyBean.setAvailable(Integer.parseInt(MAX_CASE_PER_FACULTY) - Integer.parseInt(facultyBean.getAssignmentsAllocated()));
			}else{
				facultyBean.setAssignmentsAllocated("0");
				//facultyBean.setAvailable(Integer.parseInt(MAX_CASE_PER_FACULTY));
			}
		}

		return facultyList;
	}
	
	
	@RequestMapping(value="/allocateCaseStudyEvaluation", method = {RequestMethod.POST})
	public ModelAndView allocateCaseStudyEvaluation(HttpServletRequest request, HttpServletResponse response , @ModelAttribute CaseStudyExamBean csBean) {
		if(!checkSession(request, response)){
			return new ModelAndView("login");
		}

		ModelAndView modelAndView = new ModelAndView("caseStudyFiles/assignSubmittedCaseStudyToFaculty");
		CaseStudyDao dao = (CaseStudyDao)act.getBean("caseDAO");
		modelAndView.addObject("csBean",csBean);
		modelAndView.addObject("yearList", ACAD_YEAR_SAS_LIST);
		modelAndView.addObject("monthList", SAS_EXAM_MONTH_LIST);

		try{

			ArrayList<String> faculties = csBean.getFaculties();
			ArrayList<String> numberOfCaseStudies = csBean.getNumberOfCaseStudies();
			ArrayList<String> indexes = csBean.getIndexes();
	
			List<CaseStudyExamBean> uncheckedCaseStudy = dao.getAllUncheckedCaseStudyDetails(csBean.getBatchYear(), csBean.getBatchMonth(), csBean.getProgram());

			int startIndex = 0;


			startIndex = 0;
			for (int i = 0; i < indexes.size(); i++) {
				int index = Integer.parseInt(indexes.get(i))-1;
				String facultyId = faculties.get(index);

				int lastIndex = startIndex + Integer.parseInt(numberOfCaseStudies.get(i));


				List<CaseStudyExamBean> caseStudySubSet = uncheckedCaseStudy.subList(startIndex, lastIndex);
				dao.allocateCaseStudy(caseStudySubSet, facultyId);

				startIndex = startIndex + Integer.parseInt(numberOfCaseStudies.get(i));
			}

			setSuccess(request, "Case Study Evaluation allocated successfully!");

		}catch(Exception e){
			
			setError(request, "Error in allocating Case Study Evaluation");
		}


		ArrayList<FacultyExamBean> facultyList = getFacultyAndCurrentCaseStudyNumber(csBean);
		modelAndView.addObject("facultyList", facultyList);

		int numberOfCaseStudy = dao.getNumberOfCaseStudyNotAssignedToFaculty(csBean);
		modelAndView.addObject("numberOfCaseStudy", numberOfCaseStudy);

		return modelAndView;
	}
	
	
	@RequestMapping(value = "/searchAssignedCaseStudyFilesForm", method = {RequestMethod.GET,RequestMethod.POST})
	public ModelAndView searchAssignedCaseStudyFilesForm(HttpServletRequest request,HttpServletResponse response,@ModelAttribute CaseStudyExamBean csBean){
		if(!checkSession(request, response)){
			return new ModelAndView("login");
		}
		ModelAndView modelAndView = new ModelAndView("caseStudyFiles/searchAssignedCaseStudyFiles");
		String userId = (String)request.getSession().getAttribute("userId");
		CaseStudyExamBean csSessionBean = (CaseStudyExamBean)request.getSession().getAttribute("csBean");
		if(csSessionBean != null){
			modelAndView.addObject("csBean",csSessionBean);
			modelAndView.addObject("yearList", ACAD_YEAR_SAS_LIST);
			modelAndView.addObject("monthList", SAS_EXAM_MONTH_LIST);
			modelAndView.addObject("evaluationStatus", evaluationStatus);
			
			CaseStudyDao dao = (CaseStudyDao)act.getBean("caseDAO");
			List<CaseStudyExamBean> caseStudy = dao.getAllCaseStudyAssignedToAFaculty(csSessionBean, userId);
			modelAndView.addObject("cases", caseStudy.size());
			modelAndView.addObject("caseStudy", caseStudy);
			return modelAndView;
		}
			modelAndView.addObject("csBean",csBean);
			modelAndView.addObject("yearList", ACAD_YEAR_SAS_LIST);
			modelAndView.addObject("monthList", SAS_EXAM_MONTH_LIST);
			modelAndView.addObject("evaluationStatus", evaluationStatus);
		return modelAndView;
	}
	
	@RequestMapping(value = "/searchAssignedCaseStudyFiles", method = {RequestMethod.POST})
	public ModelAndView searchAssignedCaseStudyFiles(HttpServletRequest request,HttpServletResponse response,@ModelAttribute CaseStudyExamBean csBean){
		if(!checkSession(request, response)){
			return new ModelAndView("login");
		}
		ModelAndView modelAndView = new ModelAndView("caseStudyFiles/searchAssignedCaseStudyFiles");
		String userId = (String)request.getSession().getAttribute("userId");
			modelAndView.addObject("csBean",csBean);
			modelAndView.addObject("yearList", ACAD_YEAR_SAS_LIST);
			modelAndView.addObject("monthList", SAS_EXAM_MONTH_LIST);
			modelAndView.addObject("evaluationStatus", evaluationStatus);
			
			CaseStudyDao dao = (CaseStudyDao)act.getBean("caseDAO");
			List<CaseStudyExamBean> caseStudy = dao.getAllCaseStudyAssignedToAFaculty(csBean, userId);
			modelAndView.addObject("cases", caseStudy.size());
			modelAndView.addObject("caseStudy", caseStudy);
			
			request.getSession().setAttribute("caseStudy", caseStudy);
			request.getSession().setAttribute("csBean", csBean);
		return modelAndView;
	}
	
	@RequestMapping(value = "/evaluateCaseStudyForm", method = {RequestMethod.GET})
	public ModelAndView evaluateProjectForm(HttpServletRequest request, HttpServletResponse response, @ModelAttribute CaseStudyExamBean csBean) {
		if(!checkSession(request, response)){
			return new ModelAndView("login");
		}

		HttpSession session = request.getSession();
		session.setMaxInactiveInterval(60*60);//Set session timeout to 60 minutes only for faculties when they evaluate CaseStudy
		ModelAndView modelnView = new ModelAndView("caseStudyFiles/evaluateCaseStudy");
		modelnView.addObject("csBean",csBean);
		String facultyId = (String)request.getSession().getAttribute("userId");
		csBean.setFacultyId(facultyId);
		CaseStudyDao dao = (CaseStudyDao)act.getBean("caseDAO");
		CaseStudyExamBean caseStudyFile = dao.getSingleCaseStudyForFaculty(csBean,facultyId);
		if(StringUtils.isBlank(caseStudyFile.getEvaluationCount())){
			caseStudyFile.setEvaluationCount("0");
		}
		modelnView.addObject("caseStudyFile", caseStudyFile);
		return modelnView; 
	}
	
	
	@RequestMapping(value = "/evaluateCaseStudy", method = {RequestMethod.POST})
	public ModelAndView evaluateCaseStudy(HttpServletRequest request, HttpServletResponse response, @ModelAttribute CaseStudyExamBean csBean) {
		if(!checkSession(request, response)){
			return new ModelAndView("login");
		}
		CaseStudyDao dao = (CaseStudyDao)act.getBean("caseDAO");
		String facultyId = (String)request.getSession().getAttribute("userId");
		csBean.setFacultyId(facultyId);
		ModelAndView modelnView = new ModelAndView("caseStudyFiles/evaluateCaseStudy");
		modelnView.addObject("csBean",csBean);

		if(csBean.getScore() == null || "".equals(csBean.getScore().trim())){
			setError(request, "Score cannot be empty. Please enter a score.");
			CaseStudyExamBean caseStudyFile = dao.getSingleCaseStudyForFaculty(csBean,facultyId);
			modelnView.addObject("caseStudyFile", caseStudyFile);
			return modelnView;
		}
		
		try {
			CaseStudyExamBean caseStudyFile = dao.getSingleCaseStudyForFaculty(csBean,facultyId);
			if(StringUtils.isBlank(caseStudyFile.getEvaluationCount())){
				csBean.setEvaluationCount("0");
			}else{
				csBean.setEvaluationCount(""+(Integer.parseInt(caseStudyFile.getEvaluationCount())+1));
			}
			
			if("Blank Submission".equalsIgnoreCase(csBean.getReason()) || "Submitted Question paper".equalsIgnoreCase(csBean.getReason())){
				csBean.setGrade("E");
			}else if(Integer.parseInt(csBean.getScore()) < 50){
				csBean.setGrade("D");
			}else if(Integer.parseInt(csBean.getScore()) == 50 || Integer.parseInt(csBean.getScore()) <= 74){
				csBean.setGrade("C");
			}else if(Integer.parseInt(csBean.getScore()) == 75 || Integer.parseInt(csBean.getScore()) <= 89){
				csBean.setGrade("B");
			}else if(Integer.parseInt(csBean.getScore()) == 90 || Integer.parseInt(csBean.getScore()) > 90){
				csBean.setGrade("A");
			}
			dao.evaluateCaseStudy(csBean);
			caseStudyFile = dao.getSingleCaseStudyForFaculty(csBean,facultyId);
			modelnView.addObject("caseStudyFile", caseStudyFile);

			setSuccess(request, "Case Study score saved successfully");
		} catch (Exception e) {
			
			setError(request, "Error in saving Case Study score");
		}
		return modelnView; 
	}
	
	@RequestMapping(value = "/searchEvaluatedCaseStudyForm", method = {RequestMethod.GET})
	public ModelAndView searchEvaluatedCaseStudyForm(HttpServletRequest request,HttpServletResponse response,@ModelAttribute CaseStudyExamBean csBean){
		if(!checkSession(request, response)){
			return new ModelAndView("login");
		}
		ModelAndView modelAndView = new ModelAndView("caseStudyFiles/searchEvaluatedCaseStudy");
		CaseStudyDao dao = (CaseStudyDao)act.getBean("caseDAO");
		ArrayList<String> facultyList = dao.getFacultyForCaseStudy();
		
			modelAndView.addObject("csBean",csBean);
			modelAndView.addObject("yearList", ACAD_YEAR_SAS_LIST);
			modelAndView.addObject("monthList", SAS_EXAM_MONTH_LIST);
			modelAndView.addObject("evaluationStatus", evaluationStatus);
			modelAndView.addObject("facultyList", facultyList);
		return modelAndView;
	}
	
	@RequestMapping(value = "/searchEvaluatedCaseStudy", method = {RequestMethod.POST})
	public ModelAndView searchEvaluatedCaseStudy(HttpServletRequest request,HttpServletResponse response,@ModelAttribute CaseStudyExamBean csBean){
		if(!checkSession(request, response)){
			return new ModelAndView("login");
		}
		ModelAndView modelAndView = new ModelAndView("caseStudyFiles/searchEvaluatedCaseStudy");
		CaseStudyDao dao = (CaseStudyDao)act.getBean("caseDAO");
		ArrayList<String> facultyList = dao.getFacultyForCaseStudy();
		Person p = (Person)request.getSession().getAttribute("user");
			modelAndView.addObject("csBean",csBean);
			modelAndView.addObject("yearList", ACAD_YEAR_SAS_LIST);
			modelAndView.addObject("monthList", SAS_EXAM_MONTH_LIST);
			modelAndView.addObject("evaluationStatus", evaluationStatus);
			modelAndView.addObject("facultyList", facultyList);
			modelAndView.addObject("role", p.getRoles());
			List<CaseStudyExamBean> getEvaluatedCaseStudyList = dao.getEvaluatedCaseStudyList(csBean);
			modelAndView.addObject("cases", getEvaluatedCaseStudyList.size());
			modelAndView.addObject("casesList", getEvaluatedCaseStudyList);
			request.getSession().setAttribute("getEvaluatedCaseStudyList",getEvaluatedCaseStudyList);
			
		return modelAndView;
	}
	
	@RequestMapping(value = "/changeCaseStudyEvaluationCount", method = {RequestMethod.GET, RequestMethod.POST})
	public @ResponseBody String changeCaseStudyEvaluationCount(HttpServletRequest request, HttpServletResponse response,
			@ModelAttribute  CaseStudyExamBean csBean, @RequestParam String value) {

		try {
			CaseStudyDao dao = (CaseStudyDao)act.getBean("caseDAO");
			dao.changeCaseStudyEvaluationCount(csBean, value);
			return "Count changed successfully";
		}catch (Exception e) {
			
			return "Error in changing submission count";
		}
	}
	
	@SuppressWarnings("unchecked")
	@RequestMapping(value = "/downloadEvalutedCaseStudyDetails", method = {RequestMethod.GET, RequestMethod.POST})
	//OldCode
	  public ModelAndView downloadProgramCompleteReport(HttpServletRequest request, HttpServletResponse response) {
		if(!checkSession(request, response)){
			redirectToPortalApp(response);
			return null;
		}
		
		List<CaseStudyExamBean> getEvaluatedCaseStudyList = (ArrayList<CaseStudyExamBean>)request.getSession().getAttribute("getEvaluatedCaseStudyList");
		return new ModelAndView("evaluatedCaseStudyReport","getEvaluatedCaseStudyList",getEvaluatedCaseStudyList);
	}
	
	/*@ResponseBody String buildExcelDocument(HttpServletRequest request, HttpServletResponse response)
			throws Exception {
	 
		try{
			if(!checkSession(request, response)){
				redirectToPortalApp(response);
				return null;
			}
			CaseStudyDao cdao = (CaseStudyDao)act.getBean("caseDAO");
			String user = (String)request.getSession().getAttribute("userId");
			Person userBean = (Person)request.getSession().getAttribute("user");
			String folderName = REPORTS+"/"+user ;
			String fileName = "/CaseStudyReport_"+RandomStringUtils.randomAlphanumeric(12)+".xlsx";
			File folder = new File(folderName);
			
			if(!folder.exists()){
				folder.mkdirs();
			}
			File f = new File(folderName+fileName);
			if(!f.exists()){
				f.createNewFile();
			}
			
			StudentMarksDAO dao = (StudentMarksDAO)act.getBean("studentMarksDAO");
			HashMap<String, StudentBean> sapIdProgramStudentsMap = dao.getAllStudentProgramMap();
			//HashMap<String, CenterBean> icLcMap = dao.getICLCMap();
			
			List<CaseStudyBean> studentMarksList = (List<CaseStudyBean>) request.getSession().getAttribute("getEvaluatedCaseStudyList");
			
			
			SXSSFWorkbook workbook = new SXSSFWorkbook();
			//create a word sheet
			Sheet sheet = workbook.createSheet("CaseStudy Completed Students");
			
			int index = 0;
			Row header = sheet.createRow(0);
			header.createCell(index++).setCellValue("Sr. No.");
			header.createCell(index++).setCellValue("SAP ID");
			header.createCell(index++).setCellValue("Program");
			header.createCell(index++).setCellValue("Program Structure");
			header.createCell(index++).setCellValue("Enrollment Month");
			header.createCell(index++).setCellValue("Enrollment Year");
			header.createCell(index++).setCellValue("Exam Mode");
			header.createCell(index++).setCellValue("Faculty Id ");
			header.createCell(index++).setCellValue("Faculty Name");
			header.createCell(index++).setCellValue("Topic");
			header.createCell(index++).setCellValue("Grade");
			header.createCell(index++).setCellValue("Total Score");
			header.createCell(index++).setCellValue("Remarks");
			header.createCell(index++).setCellValue("Reason");
			header.createCell(index++).setCellValue("Q1 Marks");
			header.createCell(index++).setCellValue("Q1 Remarks");
			header.createCell(index++).setCellValue("Q2 Marks");
			header.createCell(index++).setCellValue("Q2 Remarks");
			header.createCell(index++).setCellValue("Q3 Marks");
			header.createCell(index++).setCellValue("Q3 Remarks");
			header.createCell(index++).setCellValue("Q4 Marks");
			header.createCell(index++).setCellValue("Q4 Remarks");
			header.createCell(index++).setCellValue("Q5 Marks");
			header.createCell(index++).setCellValue("Q5 Remarks");
			header.createCell(index++).setCellValue("Q6 Marks");
			header.createCell(index++).setCellValue("Q6 Remarks");
	 
			int rowNum = 1;
			for (int i = 0 ; i < studentMarksList.size(); i++) {
				//create the row data
				index = 0;
				Row row = sheet.createRow(rowNum++);
				CaseStudyBean bean = studentMarksList.get(i);
		
				StudentBean student = sapIdProgramStudentsMap.get(bean.getSapid().trim()+bean.getProgram().trim());
			
				String examMode = "Offline";
				String enrollmentMonth = "";
				String enrollmentYear = "";
				
				if(student != null){
					if("Online".equals(student.getExamMode())){
						examMode = "Online";
					}
					enrollmentMonth = student.getEnrollmentMonth();
					enrollmentYear = student.getEnrollmentYear();
				}
				
				row.createCell(index++).setCellValue(i+1);
				row.createCell(index++).setCellValue(new Double(bean.getSapid()));
				row.createCell(index++).setCellValue(bean.getProgram());
				row.createCell(index++).setCellValue(bean.getPrgmStructApplicable());
				row.createCell(index++).setCellValue(enrollmentMonth);
				row.createCell(index++).setCellValue(enrollmentYear);
				row.createCell(index++).setCellValue(examMode);
				row.createCell(index++).setCellValue(bean.getFacultyId());
				row.createCell(index++).setCellValue(bean.getFirstName() +" "+bean.getLastName());
				row.createCell(index++).setCellValue(bean.getTopic());
				row.createCell(index++).setCellValue(bean.getGrade());
				row.createCell(index++).setCellValue(bean.getScore());
				row.createCell(index++).setCellValue(bean.getRemarks());
				row.createCell(index++).setCellValue(bean.getReason());
				row.createCell(index++).setCellValue(bean.getQ1Marks());
				row.createCell(index++).setCellValue(bean.getQ1Remarks());
				row.createCell(index++).setCellValue(bean.getQ2Marks());
				row.createCell(index++).setCellValue(bean.getQ2Remarks());
				row.createCell(index++).setCellValue(bean.getQ3Marks());
				row.createCell(index++).setCellValue(bean.getQ3Remarks());
				row.createCell(index++).setCellValue(bean.getQ4Marks());
				row.createCell(index++).setCellValue(bean.getQ4Remarks());
				row.createCell(index++).setCellValue(bean.getQ5Marks());
				row.createCell(index++).setCellValue(bean.getQ5Remarks());
				row.createCell(index++).setCellValue(bean.getQ6Marks());
				row.createCell(index++).setCellValue(bean.getQ6Remarks());
	        }
			FileOutputStream fileOut = new FileOutputStream(f);
			workbook.write(fileOut);
			fileOut.close();
			MailSender mailSender = (MailSender)act.getBean("mailer");
			mailSender.sendCaseStudyReport("Case Study Report",fileName,folderName,userBean.getEmail());
			cdao.insertDownloadDetails("Case Study Report",user);
			return "Success to send mail";
		}catch(Exception e){
			
			return "Unable to send mail";
		}
	
	}*/
	
}

