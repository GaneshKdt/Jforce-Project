package com.nmims.controllers;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.RandomStringUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
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
import org.springframework.web.multipart.commons.CommonsMultipartFile;
import org.springframework.web.servlet.ModelAndView;

import com.nmims.beans.ConsumerProgramStructure;
import com.nmims.beans.ContentBean;
import com.nmims.beans.ContentFilesSetbean;
import com.nmims.beans.FacultyCourseBean;
import com.nmims.beans.FacultyCourseMappingBean;
import com.nmims.beans.FileBean;
import com.nmims.beans.Page;
import com.nmims.beans.Post;
import com.nmims.beans.Posts;
import com.nmims.beans.ProgramSubjectMappingBean;
import com.nmims.beans.StudentBean;
import com.nmims.beans.VideoContentBean;
import com.nmims.daos.ContentDAO;
import com.nmims.daos.TimeTableDAO;
import com.nmims.daos.VideoContentDAO;
import com.nmims.factory.ContentFactory;
import com.nmims.factory.ContentFactory.StudentType;
import com.nmims.helpers.ExcelHelper;
import com.nmims.interfaces.ContentInterface;


@Controller
public class ContentController extends BaseController{

	@Autowired
	ApplicationContext act;
	
	@Value("#{'${ACAD_YEAR_LIST}'.split(',')}")
	private List<String> ACAD_YEAR_LIST;
	
	@Value("#{'${ACAD_MONTH_LIST}'.split(',')}")
	private List<String> ACAD_MONTH_LIST;

	@Value( "${CONTENT_PATH}" )
	private String CONTENT_PATH;
	
	@Value( "${CURRENT_ACAD_MONTH}" )
	private String CURRENT_ACAD_MONTH;

	@Value( "${CURRENT_ACAD_YEAR}" )
	private String CURRENT_ACAD_YEAR;
	
	@Autowired
    private ContentFactory contentFactory;  

	 
	private final int pageSize = 10;
	private static final int BUFFER_SIZE = 4096;
	private static final Logger logger = Logger.getLogger(ContentController.class);
	private ArrayList<String> currentYearList = new ArrayList<String>(Arrays.asList( "2015","2016","2017","2018","2019","2020")); 
	private ArrayList<String> subjectList = null; 
	private ArrayList<String> facultyList = null;
	private ArrayList<String> programStrutureList = null;
	private ArrayList<ProgramSubjectMappingBean> programSubjectMappingList = null;
	
	private ArrayList<ConsumerProgramStructure> consumerTypesList = null;

	
	@ModelAttribute("subjectList")
	public ArrayList<String> getSubjectList(){
		if(this.subjectList == null){
			ContentDAO dao = (ContentDAO)act.getBean("contentDAO");
			this.subjectList = dao.getActiveSubjects();
		}
		
		return subjectList;
	}
	
	public ArrayList<String> getFacultyList(){
		//if(this.facultyList == null){
			TimeTableDAO dao = (TimeTableDAO)act.getBean("timeTableDAO");
			this.facultyList = dao.getAllFaculties();
		//}
		return facultyList;
	}
	
	public ArrayList<String> getProgramStrutureList(){
		if (programStrutureList == null) {
			ContentDAO dao = (ContentDAO)act.getBean("contentDAO");
			this.programStrutureList = dao.getProgramStrutureList();
		}
		return programStrutureList;
	}
	
	public ContentController(){
	}
	
	@ModelAttribute("yearList")
	public ArrayList<String> getCurrentYearList(){
		return currentYearList;
	}

	public ArrayList<ProgramSubjectMappingBean> getProgramSubjectMappingList(){
		if(this.programSubjectMappingList == null || this.programSubjectMappingList.size() == 0){
			ContentDAO dao = (ContentDAO)act.getBean("contentDAO");
			this.programSubjectMappingList = dao.getProgramSubjectMappingList();
		}
		return programSubjectMappingList;
	} 
	
	public ArrayList<ConsumerProgramStructure> getConsumerTypesList(){
		if(this.consumerTypesList == null){
			ContentDAO dao = (ContentDAO)act.getBean("contentDAO");
			this.consumerTypesList = dao.getConsumerTypes();
		}
		return consumerTypesList;
	}
	
	
	
	@RequestMapping(value = "/uploadContentForm", method = {RequestMethod.GET, RequestMethod.POST})
	public String uploadContentForm(HttpServletRequest request, HttpServletResponse respnse, Model m) {
		ContentDAO contentDao = (ContentDAO)act.getBean("contentDAO");
		ContentFilesSetbean  filesSet = new ContentFilesSetbean();
		
		m.addAttribute("filesSet",filesSet);
		String userId = (String)request.getSession().getAttribute("userId_acads");
		
		ContentInterface content = contentFactory.getStudentType(StudentType.PG);
		content.getSubjects();
		ArrayList<String> facultySubjects = contentDao.getFacultySubjectList(userId);
		if(userId.equalsIgnoreCase("NMSCEMUADMIN01")){
			m.addAttribute("subjectList", getSubjectList());	
		}else{
			m.addAttribute("subjectList", facultySubjects);	
		}
		m.addAttribute("consumerType", getConsumerTypeList());
		//Added By Riya as per new logic by subject code
		m.addAttribute("subjectcodes",getSubjectCodeLists());
		m.addAttribute("masterKeysWithSubjectCodes",contentDao.getMasterKeyMapSubjectCode());
		
		
		m.addAttribute("yearList", ACAD_YEAR_LIST);
		return "uploadContentFilesNew";
	}
	
	@RequestMapping(value = "/uploadContentFilesNew",  method = {RequestMethod.GET, RequestMethod.POST})
	public ModelAndView uploadContentFilesNew(HttpServletRequest request, HttpServletResponse response, @ModelAttribute ContentFilesSetbean filesSet,Model m){
		ModelAndView modelnView = new ModelAndView("uploadContentFilesNew");
		 
		ContentDAO contentDao = (ContentDAO)act.getBean("contentDAO");
		String subject = "";
		
		
		ContentInterface content = contentFactory.getStudentType(StudentType.valueOf(filesSet.getProductType()));
	  	HashMap<String, String> upload_response = new HashMap<String, String>() ;
		try {
			if(!StringUtils.isBlank(filesSet.getMasterKey()))
				 subject=contentDao.getSubjectNameByPssId(filesSet.getMasterKey());
			if(subject == null)
			{
				 request.setAttribute("error","true");
				 request.setAttribute("errorMessage","More than one subject selected, Please varify selected subject");
				 filesSet = new ContentFilesSetbean();
				 modelnView.addObject("filesSet",filesSet);
				 modelnView.addObject("yearList", ACAD_YEAR_LIST);
				 m.addAttribute("subjectcodes",getSubjectCodeLists());
				 m.addAttribute("masterKeysWithSubjectCodes",contentDao.getMasterKeyMapSubjectCode());
				 return modelnView;
			}
			
			if(!StringUtils.isBlank(filesSet.getSubjectCodeId()))
				subject = contentDao.getSubjectNameById(filesSet.getSubjectCodeId());
			
			filesSet.setSubject(subject);
			
			
			upload_response =content.createContent(filesSet);
			request.setAttribute("success", upload_response.get("success"));
			request.setAttribute("successMessage", upload_response.get("successMessage"));
			request.setAttribute("error",upload_response.get("error"));
			request.setAttribute("errorMessage", upload_response.get("errorMessage"));
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			request.setAttribute("error",upload_response.get("error"));
			request.setAttribute("errorMessage", e.getMessage());
		}
		
		filesSet = new ContentFilesSetbean();
	  	modelnView.addObject("filesSet",filesSet);
		modelnView.addObject("yearList", ACAD_YEAR_LIST);
		
		//Commented By Riya as it was not needed
		//modelnView.addObject("subjectList", getSubjectList());
		//modelnView.addObject("consumerType", getConsumerTypeList());
		
		//Added By Riya 
		m.addAttribute("subjectcodes",getSubjectCodeLists());
	    m.addAttribute("masterKeysWithSubjectCodes",contentDao.getMasterKeyMapSubjectCode());
		 
		
		return modelnView;
	}
	
	@RequestMapping(value = "/previewContent", method = {RequestMethod.GET, RequestMethod.POST})
	public ModelAndView previewContent(HttpServletRequest request, HttpServletResponse respnse, Model m) {
		if(!checkSession(request, respnse)){
			return new ModelAndView("studentPortalRediret");
		}
		return new ModelAndView("previewContent");
	}

	@RequestMapping(value = "/previewContentAlt", method = {RequestMethod.GET, RequestMethod.POST})
	public ModelAndView previewContentAlt(HttpServletRequest request, HttpServletResponse respnse, Model m) {
		if(!checkSession(request, respnse)){
			return new ModelAndView("studentPortalRediret");
		}
		return new ModelAndView("previewContentAlt");
	}
	



	
	@RequestMapping(value = "/editContents", method = {RequestMethod.GET, RequestMethod.POST})
	public ModelAndView editContents(HttpServletRequest request, HttpServletResponse respnse, @ModelAttribute ContentBean content) {
		ModelAndView modelnView = new ModelAndView("addContent");

		ContentDAO dao = (ContentDAO)act.getBean("contentDAO");
		content = getContentWithConfigIdsById(dao,content,request.getParameter("id"));
				
		if(content != null) {
			if(content.getConsumerTypeId() != null) {
				if(content.getConsumerTypeId().split(",").length > 1) {
					content.setAllowedToUpdate("false");
				}else {
					content.setAllowedToUpdate("true");
				}
			}else {
				content.setAllowedToUpdate("false");
			}
		}
		
		modelnView.addObject("content",content);
		modelnView.addObject("yearList", ACAD_YEAR_LIST);
		modelnView.addObject("subjectList", getSubjectList());
		modelnView.addObject("edit", "true");
		modelnView.addObject("consumerType", getConsumerTypeList());
		modelnView.addObject("programStructureIdNameMap", getProgramStructureIdNameMap());
		modelnView.addObject("programIdNameMap", getProgramIdNameMap());
		modelnView.addObject("editMapping", "true");
		
		return modelnView;
	}
	
	@RequestMapping(value = "/updateContents",  method = {RequestMethod.GET, RequestMethod.POST})
	public ModelAndView updateContents(HttpServletRequest request, HttpServletResponse response, @ModelAttribute ContentBean content){
		ContentInterface contentInterface = contentFactory.getStudentType(ContentFactory.StudentType.PG);
	  	HashMap<String, String> upload_response = new HashMap<String, String>() ;
	  	upload_response = contentInterface.updateContent(content);
		return null;
	}

	
	@RequestMapping(value = "/deleteContents", method = {RequestMethod.GET, RequestMethod.POST})
	public ModelAndView deleteContents(HttpServletRequest request, HttpServletResponse response){
		
		
		ContentInterface content = contentFactory.getStudentType(ContentFactory.StudentType.PG);
		content.deleteContent(request.getParameter("id"), request.getParameter("consumerProgramStructureId"));
		ContentBean searchBean = (ContentBean)request.getSession().getAttribute("searchBean_acads");
		if(searchBean == null){
			searchBean = new ContentBean();
		}
		return searchContent(request,response, searchBean);
	}
	
	@RequestMapping(value = "/makeContentLiveForm", method = RequestMethod.GET)
	public String makeContentLiveForm(HttpServletRequest request, HttpServletResponse response, Model m) {

		ContentDAO dao = (ContentDAO)act.getBean("contentDAO");
		if(!m.containsAttribute("searchBean")){
			m.addAttribute("searchBean", new ContentBean());	
		}
		m.addAttribute("yearList", currentYearList);
		m.addAttribute("contentLiveConfigList", dao.getContentLiveConfigList());
		m.addAttribute("acadsYearList", ACAD_YEAR_LIST);
		m.addAttribute("acadsMonthList", ACAD_MONTH_LIST);
		m.addAttribute("consumerType", getConsumerTypeList());
		
		return "makeContentLiveForm";
	}
	
	@RequestMapping(value = "/saveContentLiveConfig",  method = RequestMethod.POST)
	public String saveContentLiveConfig(HttpServletRequest request,
									 HttpServletResponse response, 
									 @ModelAttribute ContentBean  searchBean,
									 Model m) {
		ContentInterface content = contentFactory.getStudentType(ContentFactory.StudentType.PG);
	  	HashMap<String, String> make_live_response = new HashMap<String, String>() ;

		make_live_response = content.makeLiveContent(searchBean);
		return makeContentLiveForm(request, response, m);
	}
	
	
	@RequestMapping(value = "/editSingleContentFromCommonSetup", method = RequestMethod.GET)
	public ModelAndView editSingleContentFromCommonSetup(HttpServletRequest request,
														 HttpServletResponse respnse,
														 @ModelAttribute ContentBean content,
														 @RequestParam("contentId") String contentId,
														 @RequestParam("consumerTypeId") String consumerTypeId,
														 @RequestParam("programStructureId") String programStructureId,
														 @RequestParam("programId") String programId,
														 @RequestParam("consumerProgramStructureId") String consumerProgramStructureId
														 
														 ) {
		ModelAndView modelnView = new ModelAndView("addContent");

		ContentDAO dao = (ContentDAO)act.getBean("contentDAO");
		content = dao.findById(contentId);

		content.setConsumerTypeId(consumerTypeId);
		content.setProgramStructureId(programStructureId);
		content.setProgramId(programId);
		content.setConsumerProgramStructureId(consumerProgramStructureId);;
		content.setEditSingleContentFromCommonSetup("true");
		
		//content = getContentWithConfigIdsById(dao,content,request.getParameter("id"));
		
				
		
		modelnView.addObject("content",content);
		modelnView.addObject("yearList", ACAD_YEAR_LIST);
		modelnView.addObject("subjectList", getSubjectList());
		modelnView.addObject("edit", "true");
		modelnView.addObject("consumerType", getConsumerTypeList());
		modelnView.addObject("programStructureIdNameMap", getProgramStructureIdNameMap());
		modelnView.addObject("programIdNameMap", getProgramIdNameMap());
		
		return modelnView;
	}

	
	@RequestMapping(value = "/updateSingleContentFromCommonSetup",  method = {RequestMethod.GET, RequestMethod.POST})
	public String updateSingleContentFromCommonSetup(HttpServletRequest request,
													 HttpServletResponse response, 
													 @ModelAttribute ContentBean contentFromForm){
	  	HashMap<String, String> update_response = new HashMap<String, String>() ;
		ContentInterface content = contentFactory.getStudentType(ContentFactory.StudentType.PG);
		try {
			update_response = content.updateContent(contentFromForm);
			request.setAttribute("success", update_response.get("success"));
			request.setAttribute("successMessage", update_response.get("successMessage"));
		}catch(Exception e) {
			request.setAttribute("error",update_response.get("error"));
			request.setAttribute("errorMessage", update_response.get("errorMessage"));
		}
		//+content.getSubject()
		return "forward:/viewContentForSubject?subject=";
	}
	

	@RequestMapping(value = "/downloadFile", method = {RequestMethod.GET, RequestMethod.POST})
	public ModelAndView downloadFile(HttpServletRequest request, HttpServletResponse response ){
		ModelAndView modelnView = new ModelAndView("downloadFile");

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
			e.printStackTrace();
			request.setAttribute("error", "true");
			request.setAttribute("errorMessage", "Error in downloading file.");
		}
		return modelnView;
	}

	
	@RequestMapping(value = "/viewApplicableSubjectsForFacultyForm", method = {RequestMethod.GET, RequestMethod.POST})
	public ModelAndView viewApplicableSubjectsForFacultyForm(HttpServletRequest request, HttpServletResponse respnse, Model m) {
		if(!checkSession(request, respnse)){
			return new ModelAndView("studentPortalRediret");
		}

		ArrayList<ProgramSubjectMappingBean> allsubjects = new ArrayList<>();
		
		String facultyId = (String)request.getSession().getAttribute("userId_acads");
		ContentDAO dao = (ContentDAO)act.getBean("contentDAO");
		allsubjects = dao.getFacultySubjects(facultyId); 

		m.addAttribute("yearList", ACAD_YEAR_LIST);
		m.addAttribute("subjectList", getSubjectList());


		if(allsubjects.size() == 0){
			request.setAttribute("error", "true");
			request.setAttribute("errorMessage", "No subjects found for you.");
			return new ModelAndView("viewApplicableSubjectsForFaculty");
		}else{
			for (ProgramSubjectMappingBean programSubjectMappingBean : allsubjects) {
				programSubjectMappingBean.setProgram("NA");
				programSubjectMappingBean.setSem("NA");
				
			}
		}

		m.addAttribute("subjects",allsubjects);
		int rowCount = (allsubjects == null ? 0 : allsubjects.size());
		m.addAttribute("rowCount", rowCount);
		m.addAttribute("currentMonth", CURRENT_ACAD_MONTH);
		m.addAttribute("currentYear", CURRENT_ACAD_YEAR);
		m.addAttribute("content", new ContentBean());

		return new ModelAndView("viewApplicableSubjectsForFaculty");
	}

	private ArrayList<ProgramSubjectMappingBean> getFailSubjects(StudentBean student) {
		ContentDAO dao = (ContentDAO)act.getBean("contentDAO");
		ArrayList<ProgramSubjectMappingBean> failSubjectList = dao.getFailSubjectsForAStudent(student.getSapid());
		return failSubjectList;
	}

	private ArrayList<ProgramSubjectMappingBean> getSubjectsForStudent(StudentBean student) {
		ArrayList<ProgramSubjectMappingBean> programSubjectMappingList = getProgramSubjectMappingList();
		ArrayList<ProgramSubjectMappingBean> subjects = new ArrayList<>();
		for (int i = 0; i < programSubjectMappingList.size(); i++) {
			ProgramSubjectMappingBean bean = programSubjectMappingList.get(i);

			if(
					bean.getPrgmStructApplicable().equals(student.getPrgmStructApplicable()) 
					&& bean.getProgram().equals(student.getProgram())
					&& bean.getSem().equals(student.getSem())
					&& !student.getWaivedOffSubjects().contains(bean.getSubject())//Subjects has not already cleared it
					){
				subjects.add(bean);

			}
		}
		return subjects;
	}


	 

	private List<ContentBean> addConsumerProgramProgramStructureNameToEachContentFile(ContentDAO dao, List<ContentBean> contentList) {
		try {
			int size = 0, i = 1;
			String contentIds = "";
			
			if(contentList != null ) {
				size = contentList.size();
			}
			
			for(ContentBean c : contentList) {
				if(i == size) {
					contentIds += c.getId()+"";
				}else {
					contentIds += c.getId()+",";
				}
				i++;
			}
			
			Map<String,Integer> contentIdNCountOfProgramsApplicableToMap =  dao.getContentIdNCountOfProgramsApplicableToMap(contentIds);
			
			for(ContentBean t : contentList) {
				
					/*t.setConsumerType(getConsumerTypeIdNameMap().get(t.getConsumerTypeIdFormValue()));
					
					if(t.getProgramStructureIdFormValue().split(",").length>1) {
						t.setProgramStructure("All");;
					}else {
						t.setProgramStructure(getProgramStructureIdNameMap().get(t.getProgramStructureIdFormValue()));;
					}
					
					if(t.getProgramIdFormValue().split(",").length>1) {
						t.setProgram("All");;
					}else {
						t.setProgram(getProgramIdNameMap().get(t.getProgramIdFormValue()));;
					}*/
					
					t.setCountOfProgramsApplicableTo(contentIdNCountOfProgramsApplicableToMap.get(t.getId()));
				
				
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return contentList;
	}

	@RequestMapping(value = "/viewLastCycleRecordings", method = {RequestMethod.GET, RequestMethod.POST})
	public ModelAndView viewLastCycleRecordings(HttpServletRequest request, HttpServletResponse respnse, Model m) {

		if(!checkSession(request, respnse)){
			return new ModelAndView("studentPortalRediret");
		}

		String subject = request.getParameter("subject");
		ContentDAO dao = (ContentDAO)act.getBean("contentDAO");

		List<ContentBean> contentList = dao.getRecordingForLastCycle(subject);

		if(contentList == null || contentList.size() == 0){
			request.setAttribute("error", "true");
			request.setAttribute("errorMessage", "No Session recordings found for this subject.");
			return new ModelAndView("viewLastCycleRecordings");
		}
		
		/*StudentBean student = (StudentBean)request.getSession().getAttribute("student_acads");
		String userId = (String)request.getSession().getAttribute("userId_acads");
		if(student != null && (userId.startsWith("77") || userId.startsWith("79") )){
			String programStructureForStudent = student.getPrgmStructApplicable();
			for (ContentBean contentBean : contentList) {
				String programStructureForContent = contentBean.getProgramStructure();
				
				if(programStructureForContent == null || "".equals(programStructureForContent.trim()) || "All".equals(programStructureForContent)){
					continue;
				}else if(!programStructureForContent.equals(programStructureForStudent)){
					contentList.remove(contentBean);
				}
			}
		}*/

		m.addAttribute("contentList",contentList);
		int rowCount = (contentList == null ? 0 : contentList.size());
		m.addAttribute("rowCount", rowCount);
		m.addAttribute("subject", subject);
		return new ModelAndView("viewLastCycleRecordings");
	}

	@RequestMapping(value = "/viewAllSubjectsForContent", method = {RequestMethod.GET, RequestMethod.POST})
	public String viewAllSubjectsForContent(HttpServletRequest request, HttpServletResponse respnse, Model m) {

		ArrayList<String> subjects = getSubjectList();

		if(subjects == null || subjects.size() == 0){
			request.setAttribute("error", "true");
			request.setAttribute("errorMessage", "No subjects found.");
			return "viewAllSubjects";
		}

		m.addAttribute("subjects",subjects);
		int rowCount = (subjects == null ? 0 : subjects.size());
		m.addAttribute("rowCount", rowCount);
		m.addAttribute("currentMonth", CURRENT_ACAD_MONTH);
		m.addAttribute("currentYear", CURRENT_ACAD_YEAR);
		m.addAttribute("monthList", ACAD_MONTH_LIST);
		m.addAttribute("yearList", ACAD_YEAR_LIST);
		m.addAttribute("content", new ContentBean());

		return "viewAllSubjects";
	}

	@RequestMapping(value = "/editContent", method = {RequestMethod.GET, RequestMethod.POST})
	public ModelAndView editContent(HttpServletRequest request, HttpServletResponse respnse, @ModelAttribute ContentBean content) {
		ModelAndView modelnView = new ModelAndView("addContent");

		ContentDAO dao = (ContentDAO)act.getBean("contentDAO");
		//content = dao.findById(request.getParameter("id"));
		
		content = getContentWithConfigIdsById(dao,content,request.getParameter("id"));
		
		
		if(content != null) {
			if(!StringUtils.isBlank(content.getConsumerTypeId())) {
				if(content.getConsumerTypeId().split(",").length > 1) {
					content.setAllowedToUpdate("false");
				}else {
					content.setAllowedToUpdate("true");
				}
			}else {
				content.setAllowedToUpdate("false");
			}
			
		}
		
		modelnView.addObject("content",content);
		modelnView.addObject("yearList", ACAD_YEAR_LIST);
		modelnView.addObject("subjectList", getSubjectList());
		modelnView.addObject("edit", "true");
		modelnView.addObject("consumerType", getConsumerTypeList());
		modelnView.addObject("programStructureIdNameMap", getProgramStructureIdNameMap());
		modelnView.addObject("programIdNameMap", getProgramIdNameMap());
		modelnView.addObject("programStrutureList", getProgramStrutureList());
		
		return modelnView;
	}
	



	private ContentBean getContentWithConfigIdsById(ContentDAO dao, ContentBean content, String id) {
		
		content = dao.findById(id);
		
		List<ContentBean> configDetails= dao.getProgramsListForCommonContent(id);
		

		 String consumerTypeId = "";
		 String programStructureId = "";
		 String programId = "";
		 int consumerTypeIdCounter = 0;
		 int programStructureIdCounter = 0;
		 int programIdCounter = 0;
		Map<String,String> checkForConsumerTypeId = new HashMap<>();
		Map<String,String> checkForProgramId = new HashMap<>();
		Map<String,String> checkForProgramStructureId = new HashMap<>();
		
		for(ContentBean c :configDetails) {
			if(!checkForConsumerTypeId.containsKey(c.getConsumerTypeId())) {
				if(consumerTypeIdCounter==0) {
					consumerTypeId += c.getConsumerTypeId();
					consumerTypeIdCounter++;
				}else {
					consumerTypeId += ","+c.getConsumerTypeId();
					consumerTypeIdCounter++;
				}
				checkForConsumerTypeId.put(c.getConsumerTypeId(), "");
			}
			
			if(!checkForProgramStructureId.containsKey(c.getProgramStructureId())) {
				if(programStructureIdCounter==0) {
					programStructureId += c.getProgramStructureId();
					programStructureIdCounter++;
				}else {
					programStructureId += ","+c.getProgramStructureId();
					programStructureIdCounter++;
				}
				checkForProgramStructureId.put(c.getProgramStructureId(), "");
			}

			if(!checkForProgramId.containsKey(c.getProgramId())) {
				if(programIdCounter==0) {
					programId += c.getProgramId();
					programIdCounter++;
				}else {
					programId += ","+c.getProgramId();
					programIdCounter++;
				}
				checkForProgramId.put(c.getProgramId(), "");
			}
		}
		
		content.setConsumerTypeId(consumerTypeId);
		content.setProgramStructureId(programStructureId);
		content.setProgramId(programId);
		
		return content;
	}

	@RequestMapping(value = "/updateContent",  method = {RequestMethod.GET, RequestMethod.POST})
	public String updateContent(HttpServletRequest request, HttpServletResponse response, @ModelAttribute ContentBean content){
		ContentDAO dao = (ContentDAO)act.getBean("contentDAO");

		try {
			if("true".equalsIgnoreCase(content.getAllowedToUpdate())) {
				int deleteMappingsrows = dao.deleteContentIdMasterkeyMappingsById(content.getId());
				
				
				if(deleteMappingsrows < 1 ) {
					request.setAttribute("error", "true");
					setError(request, "Error in deleting mappings of contentId and masterkey");
					return "forward:/viewContentForSubject?subject="+content.getSubject();
				}
				
				String createMappingsError = createContentIdMasterkeyMappings(dao,content,content.getId());
				
				if(!StringUtils.isBlank(createMappingsError)) {
					request.setAttribute("error", "true");
					setError(request, "Error in creating mappings of contentId and masterkey");
					return "forward:/viewContentForSubject?subject="+content.getSubject();	
				}
			}
			
			dao.updateContent(content);
			setSuccess(request, "Content details updated successfully");
		} catch (Exception e) {
			e.printStackTrace();
			setError(request, "Error in updating content");
		}
		return "forward:/viewContentForSubject?subject="+content.getSubject();
	}
	

	@RequestMapping(value = "/uploadFacultyCourseForm", method = {RequestMethod.GET, RequestMethod.POST})
	public String uploadFacultyCourseForm(HttpServletRequest request, HttpServletResponse respnse, Model m) {

		FileBean  facultyCourse = new FileBean();
		m.addAttribute("facultyCourse",facultyCourse);
		m.addAttribute("subjectList", getSubjectList());
		m.addAttribute("yearList", ACAD_YEAR_LIST);
		return "uploadFacultyCourseMapping";
	}

	
	@RequestMapping(value = "/transferContentForm", method = {RequestMethod.GET, RequestMethod.POST})
	public String transferContentForm(HttpServletRequest request, HttpServletResponse respnse, Model m) {

		ContentBean  content = new ContentBean();
		m.addAttribute("searchBean", content);
		return "transferContent";
	}
	
	@RequestMapping(value = "/searchContentToTransfer", method = {RequestMethod.GET, RequestMethod.POST})
	public ModelAndView searchContentToTransfer(HttpServletRequest request, HttpServletResponse respnse, @ModelAttribute ContentBean searchBean) {
		ModelAndView modelnView = new ModelAndView("transferContent");
		ContentDAO dao = (ContentDAO)act.getBean("contentDAO");

		List<ContentBean> contentList = dao.getContents(searchBean);

		modelnView.addObject("contentList",contentList);
		int rowCount = (contentList == null ? 0 : contentList.size());
		modelnView.addObject("rowCount", rowCount);
		modelnView.addObject("searchBean", searchBean);
		
		if(contentList == null || contentList.size() == 0){
			request.setAttribute("error", "true");
			request.setAttribute("errorMessage", "No Study Material found for this subject.");
			return modelnView;
		}
		return modelnView;
	}
	
	@RequestMapping(value = "/transferContent", method = {RequestMethod.GET, RequestMethod.POST})
	public ModelAndView transferContent(HttpServletRequest request, HttpServletResponse respnse, @ModelAttribute ContentBean searchBean) {
		ModelAndView modelnView = new ModelAndView("transferContent");
		ContentInterface content = contentFactory.getStudentType(ContentFactory.StudentType.PG);
		content.transferContent(searchBean);
		return modelnView;
		
	}
	
	@RequestMapping(value = "/createMappingsOfCententIdAndMasterKey", method = {RequestMethod.GET, RequestMethod.POST})
	public String createMappingsOfCententIdAndMasterKey(HttpServletRequest request, HttpServletResponse respnse, Model m) {

		ContentDAO dao = (ContentDAO)act.getBean("contentDAO");
		List<ContentBean> contentList = dao.getAllContents();
		List<ContentBean> cententIdAndMasterKeyMappings  = new ArrayList<>();
		StringBuffer errorMessage = new StringBuffer("");
		
		//create mappings
		for(ContentBean c : contentList) {
			String subject = c.getSubject();
			String programStructure = c.getProgramStructure();
			Long moduleId = c.getSessionPlanModuleId();
				List<ContentBean> consumerProgramStructureIdList = dao.getconsumerProgramStructureIdList(subject, programStructure, moduleId);
				for (ContentBean bean : consumerProgramStructureIdList) {
						bean.setId(c.getId());
				}
				cententIdAndMasterKeyMappings.addAll(consumerProgramStructureIdList);

			/*
			
			Map<String,List<String>> contentIdSubjectAndMasterKeyMap = new HashMap<>();
			String key = subject+"-"+programStructure;
			List<String> masterKeys = new ArrayList<>();
			if(contentIdSubjectAndMasterKeyMap.containsKey(key)) {
				masterKeys = contentIdSubjectAndMasterKeyMap.get(key);
			}else {
				if( !StringUtils.isBlank(c.getProgramStructure())){
					 masterKeys = dao.getConsumerProgramStructureIdsBySubjectAndProgramStructure(subject,programStructure);
					 if(!masterKeys.isEmpty()) {
						 contentIdSubjectAndMasterKeyMap.put(key, masterKeys);
					 }
				}else {
					errorMessage.append("<br> No getProgramStructure for id : "+c.getId());
				}
			}
			
			cententIdAndMasterKeyMappings = createCententIdAndMasterKeyMappings(cententIdAndMasterKeyMappings,c.getId(),masterKeys);
			
			*/
		}

		String insertError = dao.batchInsertCententIdAndMasterKeyMappings(cententIdAndMasterKeyMappings);
		
		if(!StringUtils.isBlank(insertError)) {
			errorMessage.append(" <br> "+insertError);
		}
		if(!StringUtils.isBlank(errorMessage.toString())) {
			request.setAttribute("error", "true");
			request.setAttribute("errorMessage",errorMessage.toString());
		}
		FileBean  facultyCourse = new FileBean();
		m.addAttribute("facultyCourse",facultyCourse);
		m.addAttribute("subjectList", getSubjectList());
		m.addAttribute("yearList", ACAD_YEAR_LIST);
		return uploadFacultyCourseForm(request, respnse, m);
	}

	private List<ContentBean> createCententIdAndMasterKeyMappings(List<ContentBean> cententIdAndMasterKeyMappings, String id, List<String> masterKeys) {
		for(String k : masterKeys) {
			ContentBean tempBean = new ContentBean();
			tempBean.setId(id);
			tempBean.setConsumerProgramStructureId(k);
			cententIdAndMasterKeyMappings.add(tempBean);
		}
		return cententIdAndMasterKeyMappings;
	}

	@RequestMapping(value = "/getProgramsListForCommonContent", method = {RequestMethod.POST})
	public ResponseEntity<List<ContentBean>> getProgramsListForCommonContent(@RequestBody ContentBean bean) {
		ContentDAO dao = (ContentDAO)act.getBean("contentDAO");
		
		return new ResponseEntity<List<ContentBean>>(dao.getProgramsListForCommonContent(bean.getId()),HttpStatus.OK);
	}
	
	@RequestMapping(value = "/searchContentForm", method = RequestMethod.GET)
	public String searchContentForm(HttpServletRequest request, HttpServletResponse response, Model m) {

		ContentBean searchBean = new ContentBean();
		m.addAttribute("searchBean",searchBean);
		m.addAttribute("yearList", ACAD_YEAR_LIST);
		m.addAttribute("monthList", ACAD_MONTH_LIST);
		m.addAttribute("consumerTypeList", getConsumerTypesList());
		
		return "searchContent";
	}
	
	@RequestMapping(value = "/searchContent",  method = {RequestMethod.GET, RequestMethod.POST})
	public ModelAndView searchContent(HttpServletRequest request, HttpServletResponse response, @ModelAttribute ContentBean searchBean){
		
		ModelAndView modelnView = new ModelAndView("searchContent");
		request.getSession().setAttribute("searchBean_acads", searchBean);
		TimeTableDAO dao = (TimeTableDAO)act.getBean("timeTableDAO");
		ContentDAO cDao = (ContentDAO)act.getBean("contentDAO");
				
		if(searchBean.getConsumerProgramStructureId() == null) {
			List<String> consumerProgramStructureIds =new ArrayList<String>();
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
		
		String searchType = request.getParameter("searchType") == null ? "distinct" : request.getParameter("searchType");
		request.getSession().setAttribute("searchType", searchType);
		Page<ContentBean> page = cDao.getResourcesContent(1, pageSize, searchBean, searchType);
		List<ContentBean> resourcesContentList = page.getPageItems();
		
		modelnView.addObject("page", page);
		modelnView.addObject("searchBean", searchBean);
		modelnView.addObject("rowCount", page.getRowCount());
		modelnView.addObject("yearList", ACAD_YEAR_LIST);
		modelnView.addObject("monthList", ACAD_MONTH_LIST);
		modelnView.addObject("subjectList", getSubjectList());
		modelnView.addObject("consumerTypeList", getConsumerTypesList());
		modelnView.addObject("subject", searchBean.getSubject());
		
		modelnView.addObject("searchBean", searchBean);
		modelnView.addObject("searchType", searchType);
		if(resourcesContentList == null || resourcesContentList.size() == 0){
			request.setAttribute("error", "true");
			request.setAttribute("errorMessage", "No Records found.");
		}
		modelnView.addObject("resourcesContentList", resourcesContentList);
		
		return modelnView;
	}
	
	@RequestMapping(value = "/searchContentPage",  method = {RequestMethod.GET, RequestMethod.POST})
	public ModelAndView searchScheduledSessionPage(HttpServletRequest request, HttpServletResponse response){
		
		ModelAndView modelnView = new ModelAndView("searchContent");
		ContentDAO cDao = (ContentDAO)act.getBean("contentDAO");
		ContentBean searchBean = (ContentBean)request.getSession().getAttribute("searchBean_acads");
		String searchType = (String) request.getSession().getAttribute("searchType");
		int pageNo = Integer.parseInt(request.getParameter("pageNo"));

		Page<ContentBean> page = cDao.getResourcesContent(pageNo, pageSize, searchBean, searchType);
		List<ContentBean> resourcesContentList = page.getPageItems();

		modelnView.addObject("page", page);
		modelnView.addObject("rowCount", page.getRowCount());
		modelnView.addObject("searchBean", searchBean);
		modelnView.addObject("yearList", ACAD_YEAR_LIST);
		modelnView.addObject("monthList", ACAD_MONTH_LIST);
		modelnView.addObject("subjectList", getSubjectList());
		modelnView.addObject("consumerTypeList", getConsumerTypesList());

		if(resourcesContentList == null || resourcesContentList.size() == 0){
			request.setAttribute("error", "true");
			request.setAttribute("errorMessage", "No Resources Content found.");
		}
		modelnView.addObject("resourcesContentList", resourcesContentList);
		return modelnView;
	}
	
	@RequestMapping(value = "/getCommonContentProgramsList", method = {RequestMethod.POST})
		public ResponseEntity<ArrayList<ContentBean>> getCommonContentProgramsList(@RequestBody ContentBean bean) {
			ContentDAO cDao = (ContentDAO)act.getBean("contentDAO");
			
			return new ResponseEntity<ArrayList<ContentBean>>(cDao.getCommonGroupProgramList(bean),HttpStatus.OK);
		}

	private String createContentIdMasterkeyMappings(ContentDAO dao, ContentFilesSetbean filesSet, long contentId) {
		// TODO Auto-generated method stub
		
		if(filesSet.getProgramId().split(",").length>1 
				|| filesSet.getProgramStructureId().split(",").length>1
				|| filesSet.getConsumerTypeId().split(",").length>1 
			)
			{
				// If Any Option is Selected Is "All"
			ArrayList<ContentBean> consumerProgramStructureIds = dao.getconsumerProgramStructureIdsWithSubject(filesSet.getProgramId()
																										 ,filesSet.getProgramStructureId()
																										 ,filesSet.getConsumerTypeId()
																										 ,filesSet.getSubject());
			
			return dao.batchInsertContentIdConsumerProgramStructureIdMappingsForMultipleConsumerIds(contentId,consumerProgramStructureIds); 
			}
			else {
				
			ContentBean consumerProgramStructureId = dao.getConsumerProgramStructureIdByProgramProgramStructureConsumerTypeId(filesSet.getProgramId()
																									,filesSet.getProgramStructureId()
																									,filesSet.getConsumerTypeId()
																									,filesSet.getSubject());
			ArrayList<ContentBean> consumerProgramStructureIds = new ArrayList<>();
			consumerProgramStructureIds.add(consumerProgramStructureId);
			
			return dao.batchInsertContentIdConsumerProgramStructureIdMappingsForMultipleConsumerIds(contentId,consumerProgramStructureIds); 
			}
		
	
	}

	private String createContentIdMasterkeyMappings(ContentDAO dao, ContentBean filesSet, String contentId) {
		// TODO Auto-generated method stub
		
		if(filesSet.getProgramId().split(",").length>1 
				|| filesSet.getProgramStructureId().split(",").length>1
				|| filesSet.getConsumerTypeId().split(",").length>1 
			)
			{
				// If Any Option is Selected Is "All"
			ArrayList<ContentBean> consumerProgramStructureIds = dao.getconsumerProgramStructureIdsWithSubject(filesSet.getProgramId()
																										 ,filesSet.getProgramStructureId()
																										 ,filesSet.getConsumerTypeId()
																										 ,filesSet.getSubject());
			
			return dao.batchInsertContentIdConsumerProgramStructureIdMappingsForMultipleConsumerIds((long)Long.parseLong(contentId),consumerProgramStructureIds); 
			}
			else {
				
			ContentBean consumerProgramStructureId = dao.getConsumerProgramStructureIdByProgramProgramStructureConsumerTypeId(filesSet.getProgramId()
																									,filesSet.getProgramStructureId()
																									,filesSet.getConsumerTypeId()
																									,filesSet.getSubject());
			ArrayList<ContentBean> consumerProgramStructureIds = new ArrayList<>();
			consumerProgramStructureIds.add(consumerProgramStructureId);
			
			return dao.batchInsertContentIdConsumerProgramStructureIdMappingsForMultipleConsumerIds((long)Long.parseLong(contentId),consumerProgramStructureIds); 
			}
		
	
	}
	
//	@RequestMapping(value = "/viewContentForSubject", method = {RequestMethod.GET, RequestMethod.POST})
//	public ModelAndView viewContentForSubject(HttpServletRequest request, HttpServletResponse respnse, Model m, @ModelAttribute ContentBean content) {
//			
//
//	}
	


	
	
	@RequestMapping(value = "/uploadFacultyCourseMapping", method = {RequestMethod.GET, RequestMethod.POST})
	public ModelAndView uploadFacultyCourseMapping(@ModelAttribute FileBean facultyCourse, BindingResult result,HttpServletRequest request, Model m){
		ModelAndView modelnView = new ModelAndView("uploadFacultyCourseMapping");
		try{
			String userId = (String)request.getSession().getAttribute("userId_acads");
			ExcelHelper excelHelper = new ExcelHelper();
			ArrayList<List> resultList = excelHelper.readFacultyCourseAccessExcel(facultyCourse, getSubjectList(), getFacultyList(), userId);

			List<FacultyCourseBean> facultyCourseList = (ArrayList<FacultyCourseBean>)resultList.get(0);
			List<FacultyCourseBean> errorBeanList = (ArrayList<FacultyCourseBean>)resultList.get(1);

			m.addAttribute("facultyCourse",facultyCourse);
			m.addAttribute("yearList", ACAD_YEAR_LIST);
			m.addAttribute("facultyCourse",facultyCourse);
			if(errorBeanList.size() > 0){
				request.setAttribute("errorBeanList", errorBeanList);
				return modelnView;
			}

			TimeTableDAO dao = (TimeTableDAO)act.getBean("timeTableDAO");
			ArrayList<String> errorList = dao.batchUpdateFacultyCourse(facultyCourseList);

			if(errorList.size() == 0){
				request.setAttribute("success","true");
				request.setAttribute("successMessage",facultyCourseList.size() +" rows out of "+ facultyCourseList.size()+" inserted successfully.");
			}else{
				request.setAttribute("error", "true");
				request.setAttribute("errorMessage", errorList.size() + " records were NOT inserted. Please see row number of rows not inserted. Error row numbers "+errorList);
			}

		}catch(Exception e){
			e.printStackTrace();
			request.setAttribute("error", "true");
			request.setAttribute("errorMessage", "Error in inserting rows.");
		}
		return modelnView;
	}
	
//	try{
//	String id = ;
//	String consumerProgramStructureId = ;
//	ContentDAO cDao = (ContentDAO)act.getBean("contentDAO");
//	
//	int countOfProgramsContentApplicableFor = cDao.getCountOfProgramsContentApplicableToById(id);
//	
//	if ((consumerProgramStructureId.split(",").length == 1) && (countOfProgramsContentApplicableFor == 1)) {
//		cDao.deleteContentById(id);
//		request.setAttribute("success","true");
//		request.setAttribute("successMessage","Record deleted successfully from Database");
//		
//	}else if (consumerProgramStructureId.split(",").length <= 1) {
//		int deleted = cDao.deleteContentIdConsumerPrgmStrIdMapping(id, consumerProgramStructureId);
//		if(deleted > -1) {
//			request.setAttribute("success", "true");
//			request.setAttribute("successMessage", "Content Deleted Successfully for "+consumerProgramStructureId+ ".");
//		}
//	}else {
//		cDao.deleteContentById(id);
//		request.setAttribute("success","true");
//		request.setAttribute("successMessage","Record deleted successfully from Database");
//	}
//	
//}catch(Exception e){
//	e.printStackTrace();
//	request.setAttribute("error", "true");
//	request.setAttribute("errorMessage", "Error in deleting Record.");
//}
	
//	@RequestMapping(value = "/deleteSingleContentFromCommonSetup", method = RequestMethod.GET)
//	public String deleteSingleContentFromCommonSetup(HttpServletRequest request,
//														   HttpServletResponse response,
//														   Model m,
//														   @RequestParam("contentId") String contentId,
//														   @RequestParam("consumerProgramStructureId") String consumerProgramStructureId  
//															){
//		ContentInterface content = contentFactory.getStudentType(StudentType.PG);
//	  	HashMap<String, String> delete_response = new HashMap<String, String>() ;
//			try {
//				delete_response = content.deleteContent(contentId, consumerProgramStructureId);
//				request.setAttribute("success", delete_response.get("success"));
//				request.setAttribute("successMessage", delete_response.get("successMessage"));
//			} catch(Exception e) {
//				e.printStackTrace();
//				request.setAttribute("error",delete_response.get("error"));
//				request.setAttribute("errorMessage", delete_response.get("errorMessage"));
//			}
//			
//			 return "forward:/viewContentForSubject?subject="+delete_response.get("subject");
//	}
	

	

	
//	@RequestMapping(value = "/uploadContentFiles",  method = {RequestMethod.GET, RequestMethod.POST})
//	public ModelAndView uploadContentFiles(HttpServletRequest request, HttpServletResponse response, @ModelAttribute ContentFilesSetbean filesSet){
//		ModelAndView modelnView = new ModelAndView("uploadContentFiles");
//
//		List<ContentBean> contentFiles = filesSet.getContentFiles();
//		ContentDAO dao = (ContentDAO)act.getBean("contentDAO");
//		int count = 0;
//		int successCount = 0;
//		String fileNames = "";
//		String errorFileNames = "";
//		for (int i = 0; i < contentFiles.size(); i++) {
//
//			ContentBean bean = contentFiles.get(i);
//
//			String fileName = bean.getFileData().getOriginalFilename();
//			String contentName = bean.getName();
//			String errorMessage = null;
//			
//			if(contentName == null || "".equals(contentName.trim()) || "".equals(bean.getSubject())){
//				//If no name mentioned for Content, then do not store in Database
//				continue;
//			}
//
//			if(bean.getFileData().isEmpty() || "".equals(bean.getSubject())){
//				//If no file is selected, do not upload any file
//				errorFileNames = errorFileNames + " : "+bean.getName();
//				errorMessage = "File Not selected for "+errorFileNames;
//			}else{
//				errorMessage = uploadContentFile(bean, filesSet.getSubject());
//			}
//
//			//Check if file saved to Disk successfully
//			if(errorMessage == null){
//				String userId = (String)request.getSession().getAttribute("userId_acads");
//				bean.setCreatedBy(userId);
//				bean.setLastModifiedBy(userId);
//				
//				if(bean.getSessionPlanModuleId() == null) {
//					bean.setSessionPlanModuleId(new Long("0"));
//				}
//
//				long contentId = dao.saveContentFileDetails(bean, filesSet.getSubject(), filesSet.getYear(), filesSet.getMonth());
//				if(contentId > 0) {
//
//					successCount++;
//					fileNames = fileNames + " : " +bean.getName() ;	
//				}else {
//					request.setAttribute("error", "true");
//					request.setAttribute("errorMessage", "Error in update to db. ");
//					modelnView.addObject("filesSet",filesSet);
//					modelnView.addObject("yearList", ACAD_YEAR_LIST);
//					modelnView.addObject("subjectList", getSubjectList());
//					return modelnView;
//				}
//
//			}else{
//				request.setAttribute("error", "true");
//				request.setAttribute("errorMessage", "Error in file Upload: "+errorMessage);
//				count++;
//				if (count == contentFiles.size()) {
//					modelnView.addObject("filesSet",filesSet);
//					modelnView.addObject("yearList", ACAD_YEAR_LIST);
//					modelnView.addObject("subjectList", getSubjectList());
//					return modelnView;
//				}else {
//					continue;
//				}
//			}
//		}
//
//		request.setAttribute("success","true");
//		request.setAttribute("successMessage",successCount + " Files Uploaded successfully : File Names : "+fileNames);
//
//		filesSet = new ContentFilesSetbean();
//		modelnView.addObject("filesSet",filesSet);
//		modelnView.addObject("yearList", ACAD_YEAR_LIST);
//		modelnView.addObject("subjectList", getSubjectList());
//
//		return modelnView;
//	}
	
	//
//	@RequestMapping(value = "/deleteContent", method = {RequestMethod.GET, RequestMethod.POST})
//	public ModelAndView deleteContent(HttpServletRequest request, HttpServletResponse response, Model m){
//		ContentBean newContentBean = new ContentBean();
//		try{
//			String id = request.getParameter("id");
//			ContentDAO dao = (ContentDAO)act.getBean("contentDAO");
//			ContentBean content = dao.findById(id);
//			newContentBean.setYear(content.getYear());
//			newContentBean.setMonth(content.getMonth());
//			if(content.getSessionPlanModuleId() != null && content.getSessionPlanModuleId() != 0) {
//				Post post = dao.findPostByReferenceId(id); 
////				dao.deleteFromRedis(post);
//				dao.deleteContentFromPost(post.getPost_id()+"");
//
//				dao.refreshRedis(post); 
//			}
//
//			dao.deleteContent(id);
//			request.setAttribute("success","true");
//			request.setAttribute("successMessage","Record deleted successfully");
//		}catch(Exception e){
//			e.printStackTrace();
//			request.setAttribute("error", "true");
//			request.setAttribute("errorMessage", "Error in deleting Record.");
//		}
//		FacultyCourseMappingBean searchBean = (FacultyCourseMappingBean)request.getSession().getAttribute("searchBean_acads");
//		if(searchBean == null){
//			searchBean = new FacultyCourseMappingBean();
//		}
//		return viewContentForSubject(request,response, m, newContentBean);
//	}
	
/*	@RequestMapping(value="/demoOne",method={RequestMethod.GET,RequestMethod.POST})
	public ModelAndView demoOne(Model m,HttpServletRequest request,HttpServletResponse respnse,ContentBean contentBean){
		if(!checkSession(request, respnse)){
			return new ModelAndView("studentPortalRedirect");
		}
		ModelAndView mav=new ModelAndView();
		mav.setViewName("demoOne");
		StudentBean student=(StudentBean) request.getSession().getAttribute("student_acads");
		String userId = (String)request.getSession().getAttribute("userId_acads");
		String subject = request.getParameter("subject");
		mav.addObject("subject", subject);
		ContentDAO dao=(ContentDAO) act.getBean("contentDAO");
		List<String> getSubjectList=dao.getSubjectList(userId);
		mav.addObject("getSubjectList",getSubjectList);
		for(int i=0;i<getSubjectList.size();i++){
			String s1=getSubjectList.get(i);
			List<ContentBean> getContentList=dao.getContentList(userId,s1);
			
			for (ContentBean cb : getContentList) {
				contentBean.setFilePath(cb.getFilePath());
				contentBean.setPreviewPath(cb.getPreviewPath());
				contentBean.setName(cb.getName());
				contentBean.setSubject(cb.getSubject());
				if(s1.equals(cb.getSubject())) {
				mav.addObject("getContentList",getContentList);
			}
			}
			}
		
		return  mav;
	}
	@RequestMapping(value="/tabs",method={RequestMethod.GET,RequestMethod.POST})
	public String tabs(Model m, HttpServletRequest request, HttpServletResponse response){
		return "tabs";
	}
	
	
	@RequestMapping(value="/videoJsp",method={RequestMethod.GET,RequestMethod.POST})
	public String videoJsp(Model m, HttpServletRequest request, HttpServletResponse response,VideoContentBean videContentBean){
		return "videoJsp";
	}
	

	
	@RequestMapping(value="/pdfView",method={RequestMethod.GET,RequestMethod.POST})
	public ModelAndView pdfView(Model m, HttpServletRequest request, HttpServletResponse response,ContentBean contentBean){
		ModelAndView mav=new ModelAndView();
		mav.addObject("contentBean",contentBean);
		File folder = new File("E:/PdfImage/");
		File[] listOfFiles = folder.listFiles();

		    for (int i = 0; i < listOfFiles.length; i++) {
		      if (listOfFiles[i].isFile()) {
		      } else if (listOfFiles[i].isDirectory()) {
		      }
		      mav.addObject("listOfFiles",listOfFiles[i].getAbsolutePath());
		    }	
		  
		    mav.setViewName("pdfView");
		return mav;
	}*/

	
	/*
	 * Commented and kept for later by PS 28May
	 * 	@RequestMapping(value = "/uploadContentFiles",  method = {RequestMethod.GET, RequestMethod.POST})
		public ModelAndView uploadContentFiles(HttpServletRequest request, HttpServletResponse response, @ModelAttribute ContentFilesSetbean filesSet){
			ModelAndView modelnView = new ModelAndView("uploadContentFiles");
			modelnView.addObject("filesSet",filesSet);
			modelnView.addObject("yearList", ACAD_YEAR_LIST);
			modelnView.addObject("subjectList", getSubjectList());
			modelnView.addObject("consumerType", getConsumerTypeList());
			
					+ "\n consumerType : "+filesSet.getConsumerTypeId()+" "
					+ "\n programStructure : "+filesSet.getProgramStructureId()+""
					+ "\n programid : "+filesSet.getProgramId()+""
					+ "\n subject : "+filesSet.getSubject()+""
					+ "\n Year : "+filesSet.getYear()+" \n month : "+filesSet.getMonth());
			
			List<ContentBean> contentFiles = filesSet.getContentFiles();
			ContentDAO dao = (ContentDAO)act.getBean("contentDAO");
			int successCount = 0;
			String fileNames = "";
			for (int i = 0; i < contentFiles.size(); i++) {

				ContentBean bean = contentFiles.get(i);

				String fileName = bean.getFileData().getOriginalFilename();  
				String contentName = bean.getName();
				String errorMessage = null;
				if(contentName == null || "".equals(contentName.trim()) || "".equals(bean.getSubject())){
					//If no name mentioned for Content, then do not store in Database
					continue;
				}

				if(fileName == null || "".equals(fileName.trim()) || "".equals(bean.getSubject())){
					//If no file is selected, do not upload any file
				}else{
					errorMessage = uploadContentFile(bean, filesSet.getSubject());
				}

				//Check if file saved to Disk successfully
				if(errorMessage == null){
					String userId = (String)request.getSession().getAttribute("userId_acads");
					bean.setCreatedBy(userId);
					bean.setLastModifiedBy(userId);

					long contentId = dao.saveContentFileDetails(bean, filesSet.getSubject(), filesSet.getYear(), filesSet.getMonth());
					
					if(contentId < 1 ) {
						request.setAttribute("error", "true");
						request.setAttribute("errorMessage", "Error in file saving file details to DB, FileName : "+bean.getName()
											+ "<br> Files Uploaded successfully : File Names : "+fileNames);
						return modelnView;
						
					}else {//Create mappings of contentId and masterkey
						String createMappingsError = createContentIdMasterkeyMappings(dao,filesSet,contentId);
						
						if(!StringUtils.isBlank(createMappingsError)) {
							int deleteContentRow = dao.deleteContentById(contentId);
							request.setAttribute("error", "true");
							request.setAttribute("errorMessage", "Error in creating mappings of contentId and masterkey, FileName : "+bean.getName()+" Rows deleted of content details: "+deleteContentRow
												+ "<br> Files Uploaded successfully : File Names : "+fileNames);
							return modelnView;
							
						}
						
					}
					
					successCount++;
					fileNames = fileNames + " : " +bean.getName() ;

				}else{
					request.setAttribute("error", "true");
					request.setAttribute("errorMessage", "Error in file Upload: "+errorMessage);
					return modelnView;
				}
			}

			request.setAttribute("success","true");
			request.setAttribute("successMessage",successCount + " Files Uploaded successfully : File Names : "+fileNames);

			filesSet = new ContentFilesSetbean();
			modelnView.addObject("filesSet",filesSet);
			
			return modelnView;
		}*/
	//reason
//	@RequestMapping(value = "/uploadContentFilesForLeads",  method = {RequestMethod.GET, RequestMethod.POST})
//	public ModelAndView uploadContentFilesForLeads(HttpServletRequest request, HttpServletResponse response, @ModelAttribute ContentFilesSetbean filesSet){
//		ModelAndView modelnView = new ModelAndView("uploadContentFilesForLeads");
//		List<ContentBean> contentFiles = filesSet.getContentFiles();
//		
//		ContentDAO dao = (ContentDAO)act.getBean("contentDAO");
//		int successCount = 0;
//		String fileNames = "";
//		for (int i = 0; i < contentFiles.size(); i++) {
//
//			ContentBean bean = contentFiles.get(i);
//
//			String fileName = bean.getFileData().getOriginalFilename();  
//			String contentName = bean.getName();
//			String programStructure = filesSet.getProgramIdFormValue();
//			String errorMessage = null;
//			if(contentName == null || "".equals(contentName.trim()) || "".equals(bean.getSubject())){
//				//If no name mentioned for Content, then do not store in Database
//				continue;
//			}
//
//			if(fileName == null || "".equals(fileName.trim()) || "".equals(bean.getSubject())){
//				//If no file is selected, do not upload any file
//			}else{
//				errorMessage = uploadContentFile(bean, filesSet.getSubject());
//			}
//
//			//Check if file saved to Disk successfully
//			if(errorMessage == null){
//				String userId = (String)request.getSession().getAttribute("userId_acads");
//				bean.setCreatedBy(userId);
//				bean.setLastModifiedBy(userId);
//				
//				if(bean.getSessionPlanModuleId() == null) {
//					bean.setSessionPlanModuleId(new Long("0"));
//				}
//
//				long contentId = dao.saveContentFileDetailsForLeads(bean, filesSet.getSubject());
//				if(contentId > 0) {
//
//					//Create mappings of contentId and masterkey
//					String createMappingsError = createContentIdMasterkeyMappingsForLeads(dao,filesSet,contentId);
//					
//					if(!StringUtils.isBlank(createMappingsError)) {
//						int deleteContentRow = dao.deleteContentById(contentId);
//						request.setAttribute("error", "true");
//						request.setAttribute("errorMessage", "Error in creating mappings of contentId and masterkey, FileName : "+bean.getName()+" Rows deleted of content details: "+deleteContentRow
//											+ "<br> Files Uploaded successfully : File Names : "+fileNames);
//						return modelnView;
//						
//					}
//					
//					successCount++;
//					fileNames = fileNames + " : " +bean.getName() ;
//					
//				}else {
//
//					request.setAttribute("error", "true");
//					request.setAttribute("errorMessage", "Error in update to db. ");
//					modelnView.addObject("filesSet",filesSet);
//					modelnView.addObject("yearList", ACAD_YEAR_LIST);
//					modelnView.addObject("subjectList", getSubjectList());
//					modelnView.addObject("programStructure", dao.getProgramrStructureForLeads());
//					modelnView.addObject("consumerLead", dao.getConsumerDataForLeads());
//					modelnView.addObject("programsForLeads", dao.getProgramsForLeads());
//					return modelnView;
//				}
//
//			}else{
//				request.setAttribute("error", "true");
//				request.setAttribute("errorMessage", "Error in file Upload: "+errorMessage);
//				modelnView.addObject("filesSet",filesSet);
//				modelnView.addObject("yearList", ACAD_YEAR_LIST);
//				modelnView.addObject("subjectList", getSubjectList());
//				modelnView.addObject("consumerType", getConsumerTypeList());
//				modelnView.addObject("programStructure", dao.getProgramrStructureForLeads());
//				modelnView.addObject("consumerLead", dao.getConsumerDataForLeads());
//				modelnView.addObject("programsForLeads", dao.getProgramsForLeads());
//				return modelnView;
//			}
//		}
//
//		request.setAttribute("success","true");
//		request.setAttribute("successMessage",successCount + " Files Uploaded successfully : File Names : "+fileNames);
//
//		filesSet = new ContentFilesSetbean();
//		modelnView.addObject("filesSet",filesSet);
//		modelnView.addObject("yearList", ACAD_YEAR_LIST);
//		modelnView.addObject("subjectList", getSubjectList());
//		modelnView.addObject("consumerType", getConsumerTypeList());
//		modelnView.addObject("programStructure", dao.getProgramrStructureForLeads());
//		modelnView.addObject("consumerLead", dao.getConsumerDataForLeads());
//		modelnView.addObject("programsForLeads", dao.getProgramsForLeads());
//		return modelnView;
//	}
	

/*	@RequestMapping(value = "/uploadVideoContentForm", method = {RequestMethod.GET, RequestMethod.POST})
	public ModelAndView uploadVideoContentForm(HttpServletRequest request, HttpServletResponse respnse) {
		
		ModelAndView modelAndView = new ModelAndView("uploadVideoContentFiles");
		FileBean fileBean = new FileBean();
		modelAndView.addObject("fileBean",fileBean);
		return modelAndView;
	}
	
	@RequestMapping(value = "/admin/uploadVideoContentFiles", method = {RequestMethod.POST})
	public ModelAndView uploadVideoContentFiles(HttpServletRequest request, HttpServletResponse respnse ,@ModelAttribute FileBean fileBean) {
		
		ModelAndView modelAndView = new ModelAndView("uploadVideoContentFiles");
		modelAndView.addObject("fileBean",fileBean);
		
		String userId = (String)request.getSession().getAttribute("userId_acads");
		
		MultipartFile file = fileBean.getFileData();
		if(file.isEmpty()){//Check if File was attached
			request.setAttribute("error", "true");
			request.setAttribute("errorMessage", "Please Select File to Upload...");
			return modelAndView;
		}
		
		try{
			ExcelHelper excelHelper = new ExcelHelper();
			
			ArrayList<List> resultList = excelHelper.readVideoContentExcel(fileBean, subjectList, userId);
			ArrayList<VideoContentBean> videoContentList = (ArrayList<VideoContentBean>) resultList.get(0);
			ArrayList<VideoContentBean> errorBeanList = (ArrayList<VideoContentBean>) resultList.get(1);
			
			if(errorBeanList.size() > 0){
				request.setAttribute("errorBeanList", errorBeanList);
				return modelAndView;
			}
			
			ContentDAO dao = (ContentDAO)act.getBean("contentDAO");
			ArrayList<String> errorList = dao.batchUpdateVideoContent(videoContentList);
			
			if(errorList.size() == 0){
				request.setAttribute("success","true");
				request.setAttribute("successMessage",videoContentList.size() +" rows out of "+ videoContentList.size()+" inserted successfully.");
			}else{
				request.setAttribute("error", "true");
				request.setAttribute("errorMessage", errorList.size() + " records were NOT inserted. Please see row number of rows not inserted. Error row numbers "+errorList);
			}
		}catch(Exception e){
			e.printStackTrace();
			request.setAttribute("error", "true");
			request.setAttribute("errorMessage", "Error in inserting rows.");
		}
		return modelAndView;
	}
*/	
	//Discontinued Page for Students Subject Resource Listing
	/*	@RequestMapping(value = "/viewApplicableSubjectsForm", method = {RequestMethod.GET, RequestMethod.POST})
	public ModelAndView viewApplicableSubjectsForm(HttpServletRequest request, HttpServletResponse respnse, Model m) {
		if(!checkSession(request, respnse)){
			return new ModelAndView("studentPortalRediret");
		}

		ArrayList<ProgramSubjectMappingBean> failSubjectsBeans = new ArrayList<>();
		ArrayList<ProgramSubjectMappingBean> allsubjects = new ArrayList<>();
		ArrayList<ProgramSubjectMappingBean> unAttemptedSubjectsBeans = new ArrayList<>();
		
		String sapId = (String)request.getSession().getAttribute("userId_acads");
		ContentDAO dao = (ContentDAO)act.getBean("contentDAO");
		StudentBean student = (StudentBean)request.getSession().getAttribute("student_acads");
		StudentBean studentRegistrationData = dao.getStudentRegistrationData(sapId);

		m.addAttribute("yearList", ACAD_YEAR_LIST);
		m.addAttribute("subjectList", getSubjectList());

		if(studentRegistrationData == null){
			//Get fail subjects content if studnet does not have registration for current sem.
			failSubjectsBeans = getFailSubjects(student);

			if(failSubjectsBeans != null && failSubjectsBeans.size() > 0){
				allsubjects.addAll(failSubjectsBeans);
			}
			
		}else{
			//Take program from Registration data and not Student data. 
			student.setProgram(studentRegistrationData.getProgram());
			student.setSem(studentRegistrationData.getSem());
			ArrayList<ProgramSubjectMappingBean> currentSemSubjects = getSubjectsForStudent(student);
			if(currentSemSubjects != null && currentSemSubjects.size() > 0){
				allsubjects.addAll(currentSemSubjects);
			}
			
			//If current sem is 1, then there will be no failed subjects. Get failed subjects only when he is in higher semesters
			if(!"1".equals(studentRegistrationData.getSem())){
				failSubjectsBeans = getFailSubjects(student);

				if(failSubjectsBeans != null && failSubjectsBeans.size() > 0){
					allsubjects.addAll(failSubjectsBeans);
				}
			}
		}
		
		//Get subjects never attempted or results not declared
		unAttemptedSubjectsBeans = dao.getUnAttemptedSubjects(sapId);
		if(unAttemptedSubjectsBeans != null && unAttemptedSubjectsBeans.size() > 0){
			allsubjects.addAll(unAttemptedSubjectsBeans);
		}


		//Sort all subjects semester wise.
		Collections.sort(allsubjects);

		if(allsubjects.size() == 0){
			request.setAttribute("error", "true");
			request.setAttribute("errorMessage", "No subjects found for you.");
			return new ModelAndView("viewApplicableSubjects");
		}

		m.addAttribute("subjects",allsubjects);
		int rowCount = (allsubjects == null ? 0 : allsubjects.size());
		m.addAttribute("rowCount", rowCount);


		return new ModelAndView("viewApplicableSubjects");
	}*/
	
}

