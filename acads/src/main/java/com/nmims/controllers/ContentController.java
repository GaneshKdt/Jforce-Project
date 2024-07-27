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
import com.nmims.beans.ResponseAcadsBean;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang.RandomStringUtils;
import org.apache.commons.lang3.StringUtils;

import org.json.simple.JSONObject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.security.SecurityProperties.Headers.ContentSecurityPolicyMode;
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
import org.springframework.http.HttpHeaders;
import com.nmims.beans.ConsumerProgramStructureAcads;
import com.nmims.beans.ContentAcadsBean;
import com.nmims.beans.ContentFilesSetbean;
import com.nmims.beans.FacultyAcadsBean;
import com.nmims.beans.FacultyCourseBean;
import com.nmims.beans.FacultyCourseMappingBean;
import com.nmims.beans.FileAcadsBean;
import com.nmims.beans.PageAcads;
import com.nmims.beans.PersonAcads;
import com.nmims.beans.Post;
import com.nmims.beans.PostsAcads;
import com.nmims.beans.ProgramSubjectMappingAcadsBean;
import com.nmims.beans.SearchTimeBoundContent;
import com.nmims.beans.StudentAcadsBean;
import com.nmims.beans.VideoContentAcadsBean;
import com.nmims.daos.ContentDAO;

import com.nmims.daos.SessionPlanDAO;

import com.nmims.daos.FacultyDAO;

import com.nmims.daos.TimeTableDAO;
import com.nmims.daos.VideoContentDAO;
import com.nmims.factory.ContentFactory;
import com.nmims.factory.ContentFactory.StudentType;
import com.nmims.helpers.ExcelHelper;
import com.nmims.interfaces.ContentInterface;
import com.nmims.services.ContentMBAWXService;


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

	private static final Logger logger = LoggerFactory.getLogger("contentService");

	private ArrayList<String> currentYearList = new ArrayList<String>(Arrays.asList( "2015","2016","2017","2018","2019","2020")); 
	private ArrayList<String> subjectList = null; 
	private ArrayList<String> facultyList = null;
	private ArrayList<String> programStrutureList = null;
	private ArrayList<ProgramSubjectMappingAcadsBean> programSubjectMappingList = null;
	
	private HashMap<String, FacultyAcadsBean> mapOfFacultyIdAndFacultyRecord = null;
	
	private ArrayList<ConsumerProgramStructureAcads> consumerTypesList = null;

	@Autowired
	ContentMBAWXService contentservice;
	
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

	public ArrayList<ProgramSubjectMappingAcadsBean> getProgramSubjectMappingList(){
		if(this.programSubjectMappingList == null || this.programSubjectMappingList.size() == 0){
			ContentDAO dao = (ContentDAO)act.getBean("contentDAO");
			this.programSubjectMappingList = dao.getProgramSubjectMappingList();
		}
		return programSubjectMappingList;
	} 
	
	public ArrayList<ConsumerProgramStructureAcads> getConsumerTypesList(){
		if(this.consumerTypesList == null){
			ContentDAO dao = (ContentDAO)act.getBean("contentDAO");
			this.consumerTypesList = dao.getConsumerTypes();
		}
		return consumerTypesList;
	}
	
	public HashMap<String, FacultyAcadsBean> mapOfFacultyIdAndFacultyRecord() {
		FacultyDAO facultyDao = (FacultyDAO) act.getBean("facultyDAO");
		ArrayList<FacultyAcadsBean> listOfAllFaculties = facultyDao.getAllFacultyRecords();
		if (this.mapOfFacultyIdAndFacultyRecord == null) {
			this.mapOfFacultyIdAndFacultyRecord = new HashMap<String, FacultyAcadsBean>();
			for (FacultyAcadsBean faculty : listOfAllFaculties) {
				this.mapOfFacultyIdAndFacultyRecord.put(faculty.getFacultyId(), faculty);
			}
		}
		return mapOfFacultyIdAndFacultyRecord;

	}

/*	@RequestMapping(value = "/admin/uploadVideoContentForm", method = {RequestMethod.GET, RequestMethod.POST})
=======
	public HashMap<String, FacultyBean> mapOfFacultyIdAndFacultyRecord() {
		FacultyDAO facultyDao = (FacultyDAO) act.getBean("facultyDAO");
		ArrayList<FacultyBean> listOfAllFaculties = facultyDao.getAllFacultyRecords();
		if (this.mapOfFacultyIdAndFacultyRecord == null) {
			this.mapOfFacultyIdAndFacultyRecord = new HashMap<String, FacultyBean>();
			for (FacultyBean faculty : listOfAllFaculties) {
				this.mapOfFacultyIdAndFacultyRecord.put(faculty.getFacultyId(), faculty);
			}
		}
		return mapOfFacultyIdAndFacultyRecord;

	}
	
	
/*	@RequestMapping(value = "/uploadVideoContentForm", method = {RequestMethod.GET, RequestMethod.POST})
>>>>>>> branch 'master' of https://ngasce@bitbucket.org/ngasce/acads.git
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
			  
			request.setAttribute("error", "true");
			request.setAttribute("errorMessage", "Error in inserting rows.");
		}
		return modelAndView;
	}
*/	
	
	
	@RequestMapping(value = "/admin/uploadContentForm", method = {RequestMethod.GET, RequestMethod.POST})
	public ModelAndView uploadContentForm(HttpServletRequest request, HttpServletResponse respnse, Model m) {
		//ContentDAO contentDao = (ContentDAO)act.getBean("contentDAO");
		
		String userId = (String)request.getSession().getAttribute("userId_acads");		
		if(!userId.equalsIgnoreCase("NMSCEMUADMIN01")){
			request.setAttribute("error", "true");
			setError(request, "You can not access this page.");
			return new ModelAndView("home");
		}
		
		
		ContentInterface content = contentFactory.getStudentType(StudentType.PG);
		/*content.getSubjects();
		ArrayList<String> facultySubjects = contentDao.getFacultySubjectList(userId);
		if(userId.equalsIgnoreCase("NMSCEMUADMIN01")){
			logger.info("_____________________________ userId:" + userId);
			m.addAttribute("subjectList", getSubjectList());	
			m.addAttribute("consumerType", getConsumerTypeList());*/
		
		
		//Added By Riya as per new logic by subject code
		ModelAndView modelnView = new ModelAndView("uploadContentFilesNew");
		m.addAttribute("subjectcodes",content.getSubjectCodeLists());
		m.addAttribute("masterKeysWithSubjectCodes",content.getMasterKeyMapSubjectCode());
        ContentFilesSetbean  filesSet = new ContentFilesSetbean();
		
		m.addAttribute("filesSet",filesSet);
		
		m.addAttribute("yearList", ACAD_YEAR_LIST);
		return modelnView;
	}
	
	
		
	/* Shifted in Content Student Controller
	@RequestMapping(value = "/previewContent", method = {RequestMethod.GET, RequestMethod.POST})
	public ModelAndView previewContent(HttpServletRequest request, HttpServletResponse respnse, Model m) {
		if(!checkSession(request, respnse)){
			return new ModelAndView("studentPortalRediret");
		}
		return new ModelAndView("previewContent");
	}*/
	
	@RequestMapping(value = "/admin/previewContentForAdmin", method = {RequestMethod.GET, RequestMethod.POST})
	public ModelAndView previewContentForAdmin(HttpServletRequest request, HttpServletResponse respnse, Model m) {
		if(!checkSession(request, respnse)){
			return new ModelAndView("studentPortalRediret");
		}
		return new ModelAndView("previewContentForAdmin");
	}

	@RequestMapping(value = "/admin/previewContentAlt", method = {RequestMethod.GET, RequestMethod.POST})
	public ModelAndView previewContentAlt(HttpServletRequest request, HttpServletResponse respnse, Model m) {
		if(!checkSession(request, respnse)){
			return new ModelAndView("studentPortalRediret");
		}
		return new ModelAndView("previewContentAlt");
	}
	
/*
 * Commented and kept for later by PS 28May
 * 	@RequestMapping(value = "/uploadContentFiles",  method = {RequestMethod.GET, RequestMethod.POST})
	public ModelAndView uploadContentFiles(HttpServletRequest request, HttpServletResponse response, @ModelAttribute ContentFilesSetbean filesSet){
		ModelAndView modelnView = new ModelAndView("uploadContentFiles");
		modelnView.addObject("filesSet",filesSet);
		modelnView.addObject("yearList", ACAD_YEAR_LIST);
		modelnView.addObject("subjectList", getSubjectList());
		modelnView.addObject("consumerType", getConsumerTypeList());
		
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

	/*
	 * Commented By Somesh as merge wrongly with leads branch
	 *
	@RequestMapping(value = "/uploadContentFiles",  method = {RequestMethod.GET, RequestMethod.POST})
	public ModelAndView uploadContentFiles(HttpServletRequest request, HttpServletResponse response, @ModelAttribute ContentFilesSetbean filesSet){
		ModelAndView modelnView = new ModelAndView("uploadContentFiles");

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

			if(bean.getFileData().isEmpty() || "".equals(bean.getSubject())){
				//If no file is selected, do not upload any file
				errorMessage = "File Not selected for "+bean.getName();
			}else{
				errorMessage = uploadContentFile(bean, filesSet.getSubject());
			}

			//Check if file saved to Disk successfully
			if(errorMessage == null){
				String userId = (String)request.getSession().getAttribute("userId_acads");
				bean.setCreatedBy(userId);
				bean.setLastModifiedBy(userId);
				
				if(bean.getSessionPlanModuleId() == null) {
					bean.setSessionPlanModuleId(new Long("0"));
				}

				long contentId = dao.saveContentFileDetails(bean, filesSet.getSubject(), filesSet.getYear(), filesSet.getMonth());
				if(contentId > 0) {

					//Create mappings of contentId and masterkey
					String createMappingsError = createContentIdMasterkeyMappings(dao,filesSet,contentId);
					
					if(!StringUtils.isBlank(createMappingsError)) {
						int deleteContentRow = dao.deleteContentById(contentId);
						request.setAttribute("error", "true");
						request.setAttribute("errorMessage", "Error in creating mappings of contentId and masterkey, FileName : "+bean.getName()+" Rows deleted of content details: "+deleteContentRow
											+ "<br> Files Uploaded successfully : File Names : "+fileNames);
						return modelnView;
						
					}
					
					successCount++;
					fileNames = fileNames + " : " +bean.getName() ;
					
				}else {

					request.setAttribute("error", "true");
					request.setAttribute("errorMessage", "Error in update to db. ");
					modelnView.addObject("filesSet",filesSet);
					modelnView.addObject("yearList", ACAD_YEAR_LIST);
					modelnView.addObject("subjectList", getSubjectList());
					modelnView.addObject("consumerType", getConsumerTypeList());
					return modelnView;
				}

			}else{
				if (successCount > 0) {
					request.setAttribute("success","true");
					request.setAttribute("successMessage",successCount + " Files Uploaded successfully : File Names : "+fileNames);
				}
				
				request.setAttribute("error", "true");
				request.setAttribute("errorMessage", "Error in file Upload: "+errorMessage);
				
				modelnView.addObject("filesSet",filesSet);
				modelnView.addObject("yearList", ACAD_YEAR_LIST);
				modelnView.addObject("subjectList", getSubjectList());
				modelnView.addObject("consumerType", getConsumerTypeList());
				return modelnView;
			}
		}

		request.setAttribute("success","true");
		request.setAttribute("successMessage",successCount + " Files Uploaded successfully : File Names : "+fileNames);

		filesSet = new ContentFilesSetbean();
		modelnView.addObject("filesSet",filesSet);
		modelnView.addObject("yearList", ACAD_YEAR_LIST);
		modelnView.addObject("subjectList", getSubjectList());
		modelnView.addObject("consumerType", getConsumerTypeList());

		return modelnView;
	}
	*/
	@RequestMapping(value = "/admin/uploadContentFilesNew",  method = {RequestMethod.GET, RequestMethod.POST})
	public ModelAndView uploadContentFilesNew(HttpServletRequest request, HttpServletResponse response, @ModelAttribute ContentFilesSetbean filesSet,Model m){
		//ModelAndView modelnView = new ModelAndView("uploadContentFilesNew");
		 
		//ContentDAO contentDao = (ContentDAO)act.getBean("contentDAO");


		try {

		
		String userId = (String)request.getSession().getAttribute("userId_acads");

		ContentInterface content = contentFactory.getStudentType(StudentType.valueOf(filesSet.getProductType()));
	  	HashMap<String, String> upload_response = new HashMap<String, String>() ;
	  	
		filesSet.setCreatedBy(userId);
		filesSet.setLastModifiedBy(userId);
		
		//If user select the content through subject code Id then find the subject name
		if(!StringUtils.isBlank(filesSet.getSubjectCodeId()) && StringUtils.isBlank(filesSet.getSubject()))
			filesSet.setSubject(content.getSubjectNameBySubjectCodeId(filesSet.getSubjectCodeId()));
		
		
			
			//Strategy Called For create content.
			upload_response =content.createContent(filesSet);
			request.setAttribute("success", upload_response.get("success"));
			request.setAttribute("successMessage", upload_response.get("successMessage"));
			request.setAttribute("error",upload_response.get("error"));
			request.setAttribute("errorMessage", upload_response.get("errorMessage"));
			
		} catch (Exception e) {
			// TODO Auto-generated catch block

			request.setAttribute("error","true");
			request.setAttribute("errorMessage", "Error In Uploading File. Please try again "+e.getMessage());

		}
		
		//Commented as it was not needed
		/*filesSet = new ContentFilesSetbean();
	  	modelnView.addObject("filesSet",filesSet);
		/modelnView.addObject("yearList", ACAD_YEAR_LIST);
		
		
		/*modelnView.addObject("subjectList", getSubjectList());
		modelnView.addObject("consumerType", getConsumerTypeList());*/

		//Added By Riya
		return uploadContentForm(request,response,m);
		//return modelnView;
	}
	
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
//			String errorMessage = "";
//			
//			if(contentName == null || "".equals(contentName.trim()) || "".equals(bean.getSubject())){
//				//If no name mentioned for Content, then do not store in Database
//				continue;
//			}
//
//			if(bean.getFileData().isEmpty() || "".equals(bean.getSubject())){
//				//If no file is selected, do not upload any file
//				errorFileNames = errorFileNames + " : "+bean.getName();
//				errorMessage = errorMessage + "File Not selected for "+errorFileNames;
//			}else{
//				errorMessage = uploadContentFile(bean, filesSet.getSubject());
//			}
//
//			//Check if file saved to Disk successfully
//			if(StringUtils.isBlank(errorMessage)){
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
	
	
	@RequestMapping(value = "/admin/updateContents",  method = {RequestMethod.GET, RequestMethod.POST})
	public ModelAndView updateContents(HttpServletRequest request, HttpServletResponse response, @ModelAttribute ContentAcadsBean content,Model m){
		ContentInterface contentInterface = contentFactory.getStudentType(ContentFactory.StudentType.PG);
	  	

       
		try {
			
		
        String userId = (String)request.getSession().getAttribute("userId_acads");

		content.setCreatedBy(userId);
		content.setLastModifiedBy(userId);
		request.setAttribute("searchType",request.getSession().getAttribute("searchType"));
	  	

		//------------Update Whole Content by Distinct Strategy called---------
		

		HashMap<String,String> update_response  = contentInterface.updateContentByDistinct(content,request.getParameter("masterKeys"));

	  	
	  	request.setAttribute("success", update_response.get("success"));
		request.setAttribute("successMessage",update_response.get("successMessage"));
		
		
		

		request.setAttribute("searchType",request.getSession().getAttribute("searchType"));

		

		
		
		}catch(Exception e)
		{
			
			request.setAttribute("error", "true");
			request.setAttribute("errorMessage", "Error In Updating Content.  ."+e.getMessage());
			logger.error("Error In Updating Content By Distinct.Method Name :-updateContents  ",e);
		}
		
		ContentAcadsBean searchBean = (ContentAcadsBean)request.getSession().getAttribute("searchBean_acads");
		return searchContent(request,response,searchBean,m);
	}

//	@RequestMapping(value = "/updateContent",  method = {RequestMethod.GET, RequestMethod.POST})
//	public String updateContent(HttpServletRequest request, HttpServletResponse response, @ModelAttribute ContentBean content){
//		ContentDAO dao = (ContentDAO)act.getBean("contentDAO");
//
//		
//		try {
//			if("true".equalsIgnoreCase(content.getAllowedToUpdate())) {
//				int deleteMappingsrows = dao.deleteContentIdMasterkeyMappingsById(content.getId());
//				
//				
//				if(deleteMappingsrows < 1 ) {
//					request.setAttribute("error", "true");
//					setError(request, "Error in deleting mappings of contentId and masterkey");
//					return "forward:/viewContentForSubject?subject="+content.getSubject();
//				}
//				
//				String createMappingsError = createContentIdMasterkeyMappings(dao,content,content.getId());
//				
//				if(!StringUtils.isBlank(createMappingsError)) {
//					request.setAttribute("error", "true");
//					setError(request, "Error in creating mappings of contentId and masterkey");
//					return "forward:/viewContentForSubject?subject="+content.getSubject();	
//				}
//			}
//			
//			dao.updateContent(content);
//			setSuccess(request, "Content details updated successfully");
//		} catch (Exception e) {
//			  
//			setError(request, "Error in updating content");
//		}
//		return "forward:/viewContentForSubject?subject="+content.getSubject();
//	}
	
	@RequestMapping(value = "/admin/deleteContents", method = {RequestMethod.GET, RequestMethod.POST})
	public ModelAndView deleteContents(HttpServletRequest request, HttpServletResponse response,Model m){
		
		
		ContentInterface content = contentFactory.getStudentType(ContentFactory.StudentType.PG);
		ContentAcadsBean searchBean = (ContentAcadsBean)request.getSession().getAttribute("searchBean_acads");
		
		try {

		

		String masterKey = request.getParameter("consumerProgramStructureId");
		
		
		//----------Delete Single Mapping Strategy called--------
		HashMap<String,String> delete_response = content.deleteContentSingleSetup(request.getParameter("id"),masterKey);
		
		request.setAttribute("success", delete_response.get("success"));
		request.setAttribute("successMessage", delete_response.get("successMessage"));
		
		request.setAttribute("error",delete_response.get("error"));
		request.setAttribute("errorMessage", delete_response.get("errorMessage"));
		}catch(Exception e)
		{

			request.setAttribute("error","true");
			request.setAttribute("errorMessage","Error in delete single content "+e.getMessage() );
			logger.error("Error in delete single content.Method Name:- deleteContents ",e);
		}
		
		
		return searchContent(request,response, searchBean,m);
	}
	
//	@RequestMapping(value = "/deleteContents", method = {RequestMethod.GET, RequestMethod.POST})
//	public ModelAndView deleteContents(HttpServletRequest request, HttpServletResponse response){
//		try{
//			String id = request.getParameter("id");
//			String consumerProgramStructureId = request.getParameter("consumerProgramStructureId");
//			ContentDAO cDao = (ContentDAO)act.getBean("contentDAO");
//			
//			int countOfProgramsContentApplicableFor = cDao.getCountOfProgramsContentApplicableToById(id);
//			
//			if ((consumerProgramStructureId.split(",").length == 1) && (countOfProgramsContentApplicableFor == 1)) {
//				cDao.deleteContentById(id);
//				request.setAttribute("success","true");
//				request.setAttribute("successMessage","Record deleted successfully from Database");
//				
//			}else if (consumerProgramStructureId.split(",").length <= 1) {
//				int deleted = cDao.deleteContentIdConsumerPrgmStrIdMapping(id, consumerProgramStructureId);
//				if(deleted > -1) {
//					request.setAttribute("success", "true");
//					request.setAttribute("successMessage", "Content Deleted Successfully for "+consumerProgramStructureId+ ".");
//				}
//			}else {
//				cDao.deleteContentById(id);
//				request.setAttribute("success","true");
//				request.setAttribute("successMessage","Record deleted successfully from Database");
//			}
//			
//		}catch(Exception e){
//			  
//			request.setAttribute("error", "true");
//			request.setAttribute("errorMessage", "Error in deleting Record.");
//		}
//		ContentBean searchBean = (ContentBean)request.getSession().getAttribute("searchBean_acads");
//		if(searchBean == null){
//			searchBean = new ContentBean();
//		}
//		return searchContent(request,response, searchBean);
//	}
	
	

	
	@RequestMapping(value = "/admin/transferContentForm", method = {RequestMethod.GET, RequestMethod.POST})
	public String transferContentForm(HttpServletRequest request, HttpServletResponse respnse, Model m) {

		ContentAcadsBean  content = new ContentAcadsBean();
		ContentInterface contents = contentFactory.getStudentType(StudentType.PG);
		m.addAttribute("subjectcodes",contents.getSubjectCodeLists());
		m.addAttribute("searchBean", content);
		m.addAttribute("yearList", ACAD_YEAR_LIST); 
		return "transferContent";
	}
	
	@RequestMapping(value = "/admin/searchContentToTransfer", method = {RequestMethod.GET, RequestMethod.POST})
	public ModelAndView searchContentToTransfer(HttpServletRequest request, HttpServletResponse respnse, @ModelAttribute ContentAcadsBean searchBean,Model m) {
		ModelAndView modelnView = new ModelAndView("transferContent");
		//ContentDAO dao = (ContentDAO)act.getBean("contentDAO");

		//List<ContentBean> contentList = dao.getContents(searchBean);
		try {
		//Added By SubjectCodeId
		ContentInterface content = contentFactory.getStudentType(StudentType.PG);

		List<ContentAcadsBean> contentList = content.getContentsBySubjectCodeId(searchBean.getSubjectCodeId(),searchBean.getMonth(),searchBean.getYear());

		modelnView.addObject("contentList",contentList);
		int rowCount = (contentList == null ? 0 : contentList.size());
		modelnView.addObject("rowCount", rowCount);

		modelnView.addObject("searchBean", searchBean);
		modelnView.addObject("yearList", ACAD_YEAR_LIST);
		
		m.addAttribute("subjectcodes",content.getSubjectCodeLists());
		modelnView.addObject("subjectCodeId",searchBean.getSubjectCodeId());
		
		
		
		
		if(contentList.size() == 0){
			request.setAttribute("error", "true");
			request.setAttribute("errorMessage", "No Study Material found for this subject.");
			return modelnView;
		}
		
		}catch(Exception e)
		{
			request.setAttribute("error", "true");
			request.setAttribute("errorMessage", "Error in finding the content."+e.getMessage());
			logger.info("Error in finding the content.Method Name:- searchContentToTransfer "+e.getMessage());
		}
		return modelnView;
	}
	
	@RequestMapping(value = "/admin/transferContent", method = {RequestMethod.GET, RequestMethod.POST})
	public ModelAndView transferContent(HttpServletRequest request, HttpServletResponse respnse, @ModelAttribute ContentAcadsBean searchBean,Model m) {

		ModelAndView modelnView = new ModelAndView("transferContent");
		ContentInterface content = contentFactory.getStudentType(ContentFactory.StudentType.PG);
		
		try {
		
		String userId = (String)request.getSession().getAttribute("userId_acads");

		 searchBean.setCreatedBy(userId);
		 searchBean.setLastModifiedBy(userId);
		 
		
		
		//Content according To year and Month 
		searchBean.setYear(searchBean.getToYear());
		searchBean.setMonth(searchBean.getToMonth());
		modelnView.addObject("searchBean", new ContentAcadsBean());
		modelnView.addObject("yearList", ACAD_YEAR_LIST);
		m.addAttribute("subjectcodes",content.getSubjectCodeLists());
		List<ContentAcadsBean> contentList = content.getContentsBySubjectCodeId(searchBean.getSubjectCodeId(),searchBean.getMonth(),searchBean.getYear());

		modelnView.addObject("contentList",contentList);
		int rowCount = contentList.size();
		modelnView.addObject("rowCount", rowCount);
		
		 /* ---------Transfer Content Strategy Called----------*/
		 HashMap<String, String> upload_response =content.transferContent(searchBean);
		
		request.setAttribute("success", upload_response.get("success"));
		request.setAttribute("successMessage", upload_response.get("successMessage"));
		
		request.setAttribute("error",upload_response.get("error"));
		request.setAttribute("errorMessage", upload_response.get("errorMessage"));
	
		

		}catch(Exception e)
		{
			request.setAttribute("error", "true");
			request.setAttribute("errorMessage", "Error in transfering the content."+e.getMessage());
			logger.info("Error in transfering the content.Method Name:-transferContent ",e);
		}

		return modelnView;
		
	}
	
//	@RequestMapping(value = "/transferContentForm", method = {RequestMethod.GET, RequestMethod.POST})
//	public String transferContentForm(HttpServletRequest request, HttpServletResponse respnse, Model m) {
//
//		ContentBean  content = new ContentBean();
//		m.addAttribute("searchBean", content);
//		m.addAttribute("yearList", ACAD_YEAR_LIST); 
//		return "transferContent";
//	}
//	 
//	@RequestMapping(value = "/searchContentToTransfer", method = {RequestMethod.GET, RequestMethod.POST})
//	public ModelAndView searchContentToTransfer(HttpServletRequest request, HttpServletResponse respnse, @ModelAttribute ContentBean searchBean) {
//		ModelAndView modelnView = new ModelAndView("transferContent");
//		ContentDAO dao = (ContentDAO)act.getBean("contentDAO");
//
//		List<ContentBean> contentList = dao.getContents(searchBean);
//
//		modelnView.addObject("contentList",contentList);
//		int rowCount = (contentList == null ? 0 : contentList.size());
//		modelnView.addObject("rowCount", rowCount);
//		modelnView.addObject("searchBean", searchBean);
//		modelnView.addObject("yearList", ACAD_YEAR_LIST);
//		
//		if(contentList == null || contentList.size() == 0){
//			request.setAttribute("error", "true");
//			request.setAttribute("errorMessage", "No Study Material found for this subject.");
//			return modelnView;
//		}
//		return modelnView;
//	}
//	
//	@RequestMapping(value = "/transferContent", method = {RequestMethod.GET, RequestMethod.POST})
//	public ModelAndView transferContent(HttpServletRequest request, HttpServletResponse respnse, @ModelAttribute ContentBean searchBean) {
//		ModelAndView modelnView = new ModelAndView("transferContent");
//		ContentDAO dao = (ContentDAO)act.getBean("contentDAO");
//		modelnView.addObject("searchBean", searchBean);
//		modelnView.addObject("yearList", ACAD_YEAR_LIST);
//		
//		if(searchBean.getContentToTransfer() == null || searchBean.getContentToTransfer().size() == 0){
//			request.setAttribute("error", "true");
//			request.setAttribute("errorMessage", "Please select at least one content to transfer.");
//			return modelnView;
//		}
//		
//		List<ContentBean> contentToTransferList = dao.getContentsForIds(searchBean);
//
//		for (ContentBean contentBean : contentToTransferList) {
//			dao.saveContentDetails(contentBean, searchBean.getSubject(), searchBean.getToYear(), searchBean.getToMonth());
//		}
//		
//		List<ContentBean> contentList = dao.getContents(searchBean);
//		modelnView.addObject("contentList",contentList);
//		int rowCount = (contentList == null ? 0 : contentList.size());
//		modelnView.addObject("rowCount", rowCount);
//		
//		
//		setSuccess(request, "Content Transferred Successfully");
//		return modelnView;
//	}
	
//	@RequestMapping(value = "/viewContentForSubject", method = {RequestMethod.GET, RequestMethod.POST})
//	public ModelAndView viewContentForSubject(HttpServletRequest request, HttpServletResponse respnse, Model m, @ModelAttribute ContentBean content) {
//			
//
//	}
	// added the method for leads
		@RequestMapping(value = "/admin/uploadContentFormForLeads", method = {RequestMethod.GET, RequestMethod.POST})
		public String uploadContentFormForLeads(HttpServletRequest request, HttpServletResponse respnse, Model m) {
			ContentDAO contentDao = (ContentDAO)act.getBean("contentDAO");
			ContentFilesSetbean  filesSet = new ContentFilesSetbean();

			m.addAttribute("filesSet",filesSet);
			String userId = (String)request.getSession().getAttribute("userId_acads");
			ArrayList<String> facultySubjects = contentDao.getFacultySubjectList(userId);
			if(userId.equalsIgnoreCase("NMSCEMUADMIN01")){
				m.addAttribute("subjectList", getSubjectList());	
			}else{
				m.addAttribute("subjectList", facultySubjects);	
			}
			m.addAttribute("yearList", ACAD_YEAR_LIST);

			m.addAttribute("programStructure", contentDao.getProgramrStructureForLeads());
			m.addAttribute("consumerLead", contentDao.getConsumerDataForLeads());
			m.addAttribute("programsForLeads", contentDao.getProgramsForLeads());
			return "uploadContentFilesForLeads";
		}
	
	//reason
	@RequestMapping(value = "/admin/uploadContentFilesForLeads",  method = {RequestMethod.GET, RequestMethod.POST})
	public ModelAndView uploadContentFilesForLeads(HttpServletRequest request, HttpServletResponse response, @ModelAttribute ContentFilesSetbean filesSet){
		ModelAndView modelnView = new ModelAndView("uploadContentFilesForLeads");

		List<ContentAcadsBean> contentFiles = filesSet.getContentFiles();
		
		ContentDAO dao = (ContentDAO)act.getBean("contentDAO");
		int successCount = 0;
		String fileNames = "";
		for (int i = 0; i < contentFiles.size(); i++) {

			ContentAcadsBean bean = contentFiles.get(i);

			String fileName = bean.getFileData().getOriginalFilename();  
			String contentName = bean.getName();
			String programStructure = filesSet.getProgramIdFormValue();
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
				
				if(bean.getSessionPlanModuleId() == null) {
					bean.setSessionPlanModuleId(new Long("0"));
				}

				long contentId = dao.saveContentFileDetailsForLeads(bean, filesSet.getSubject());
				if(contentId > 0) {

					//Create mappings of contentId and masterkey
					String createMappingsError = createContentIdMasterkeyMappingsForLeads(dao,filesSet,contentId);
					
					if(!StringUtils.isBlank(createMappingsError)) {
						int deleteContentRow = dao.deleteContentById(contentId);
						request.setAttribute("error", "true");
						request.setAttribute("errorMessage", "Error in creating mappings of contentId and masterkey, FileName : "+bean.getName()+" Rows deleted of content details: "+deleteContentRow
											+ "<br> Files Uploaded successfully : File Names : "+fileNames);
						return modelnView;
						
					}
					
					successCount++;
					fileNames = fileNames + " : " +bean.getName() ;
					
				}else {

					request.setAttribute("error", "true");
					request.setAttribute("errorMessage", "Error in update to db. ");
					modelnView.addObject("filesSet",filesSet);
					modelnView.addObject("yearList", ACAD_YEAR_LIST);
					modelnView.addObject("subjectList", getSubjectList());
					modelnView.addObject("programStructure", dao.getProgramrStructureForLeads());
					modelnView.addObject("consumerLead", dao.getConsumerDataForLeads());
					modelnView.addObject("programsForLeads", dao.getProgramsForLeads());
					return modelnView;
				}

			}else{
				request.setAttribute("error", "true");
				request.setAttribute("errorMessage", "Error in file Upload: "+errorMessage);
				modelnView.addObject("filesSet",filesSet);
				modelnView.addObject("yearList", ACAD_YEAR_LIST);
				modelnView.addObject("subjectList", getSubjectList());
				modelnView.addObject("consumerType", getConsumerTypeList());
				modelnView.addObject("programStructure", dao.getProgramrStructureForLeads());
				modelnView.addObject("consumerLead", dao.getConsumerDataForLeads());
				modelnView.addObject("programsForLeads", dao.getProgramsForLeads());
				return modelnView;
			}
		}

		request.setAttribute("success","true");
		request.setAttribute("successMessage",successCount + " Files Uploaded successfully : File Names : "+fileNames);

		filesSet = new ContentFilesSetbean();
		modelnView.addObject("filesSet",filesSet);
		modelnView.addObject("yearList", ACAD_YEAR_LIST);
		modelnView.addObject("subjectList", getSubjectList());
		modelnView.addObject("consumerType", getConsumerTypeList());
		modelnView.addObject("programStructure", dao.getProgramrStructureForLeads());
		modelnView.addObject("consumerLead", dao.getConsumerDataForLeads());
		modelnView.addObject("programsForLeads", dao.getProgramsForLeads());
		return modelnView;
	}
	
	
	private String createContentIdMasterkeyMappings(ContentDAO dao, ContentFilesSetbean filesSet, long contentId) {
		// TODO Auto-generated method stub
		
		if(filesSet.getProgramId().split(",").length>1 
				|| filesSet.getProgramStructureId().split(",").length>1
				|| filesSet.getConsumerTypeId().split(",").length>1 
			)
			{
				// If Any Option is Selected Is "All"
			ArrayList<ContentAcadsBean> consumerProgramStructureIds = dao.getconsumerProgramStructureIdsWithSubject(filesSet.getProgramId()
																										 ,filesSet.getProgramStructureId()
																										 ,filesSet.getConsumerTypeId()
																										 ,filesSet.getSubject());
			
			return dao.batchInsertContentIdConsumerProgramStructureIdMappingsForMultipleConsumerIds(contentId,consumerProgramStructureIds); 
			}
			else {
				
			ContentAcadsBean consumerProgramStructureId = dao.getConsumerProgramStructureIdByProgramProgramStructureConsumerTypeId(filesSet.getProgramId()
																									,filesSet.getProgramStructureId()
																									,filesSet.getConsumerTypeId()
																									,filesSet.getSubject());
			ArrayList<ContentAcadsBean> consumerProgramStructureIds = new ArrayList<>();
			consumerProgramStructureIds.add(consumerProgramStructureId);
			
			return dao.batchInsertContentIdConsumerProgramStructureIdMappingsForMultipleConsumerIds(contentId,consumerProgramStructureIds); 
			}
		
	
	}
	
	
	// mapping for leads 
	private String createContentIdMasterkeyMappingsForLeads(ContentDAO dao, ContentFilesSetbean filesSet, long contentId) {
		// TODO Auto-generated method stub
		
		if(filesSet.getProgramId().split(",").length>1 || filesSet.getProgramStructureId().split(",").length>1 || filesSet.getConsumerTypeId().split(",").length>1 )
			{
				// If Any Option is Selected Is "All"
			ArrayList<ContentAcadsBean> consumerProgramStructureIds = dao.getconsumerProgramStructureIdsWithSubject(filesSet.getProgramId()
																										 ,filesSet.getProgramStructureId()
																										 ,filesSet.getConsumerTypeId()
																										 ,filesSet.getSubject());
			
			return dao.batchInsertContentIdConsumerProgramStructureIdMappingsForMultipleConsumerIdsForLeads(contentId,consumerProgramStructureIds); 
			}
			else {
				
			ContentAcadsBean consumerProgramStructureId = dao.getConsumerProgramStructureIdByProgramProgramStructureConsumerTypeId(filesSet.getProgramId()
																									,filesSet.getProgramStructureId()
																									,filesSet.getConsumerTypeId()
																									,filesSet.getSubject());
			ArrayList<ContentAcadsBean> consumerProgramStructureIds = new ArrayList<>();
			consumerProgramStructureIds.add(consumerProgramStructureId);
			
			return dao.batchInsertContentIdConsumerProgramStructureIdMappingsForMultipleConsumerIdsForLeads(contentId,consumerProgramStructureIds); 
			}
		
	
	}
	
	

	private String createContentIdMasterkeyMappings(ContentDAO dao, ContentAcadsBean filesSet, String contentId) {
		// TODO Auto-generated method stub
		
		if(filesSet.getProgramId().split(",").length>1 
				|| filesSet.getProgramStructureId().split(",").length>1
				|| filesSet.getConsumerTypeId().split(",").length>1 
			)
			{
				// If Any Option is Selected Is "All"
			ArrayList<ContentAcadsBean> consumerProgramStructureIds = dao.getconsumerProgramStructureIdsWithSubject(filesSet.getProgramId()
																										 ,filesSet.getProgramStructureId()
																										 ,filesSet.getConsumerTypeId()
																										 ,filesSet.getSubject());
			
			return dao.batchInsertContentIdConsumerProgramStructureIdMappingsForMultipleConsumerIds((long)Long.parseLong(contentId),consumerProgramStructureIds); 
			}
			else {
				
			ContentAcadsBean consumerProgramStructureId = dao.getConsumerProgramStructureIdByProgramProgramStructureConsumerTypeId(filesSet.getProgramId()
																									,filesSet.getProgramStructureId()
																									,filesSet.getConsumerTypeId()
																									,filesSet.getSubject());
			ArrayList<ContentAcadsBean> consumerProgramStructureIds = new ArrayList<>();
			consumerProgramStructureIds.add(consumerProgramStructureId);
			
			return dao.batchInsertContentIdConsumerProgramStructureIdMappingsForMultipleConsumerIds((long)Long.parseLong(contentId),consumerProgramStructureIds); 
			}
		
	
	}

	private String uploadContentFile(ContentAcadsBean bean, String subject) {

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
		fileName = fileName.replaceAll(":", "_");
		fileName = fileName.replaceAll("'", "_");
		
		subject = subject.replaceAll(":", "_");
		subject = subject.replaceAll("'", "_");

		fileName = fileName.substring(0, fileName.lastIndexOf(".")) + "_" + RandomStringUtils.randomAlphanumeric(10) + fileName.substring(fileName.lastIndexOf("."), fileName.length());
		String tempFileNameLowerCase = fileName.toLowerCase();

		if(!(tempFileNameLowerCase.endsWith(".pdf")  || tempFileNameLowerCase.endsWith(".xls") || tempFileNameLowerCase.endsWith(".xlsx") || 
				tempFileNameLowerCase.endsWith(".ppt") || tempFileNameLowerCase.endsWith(".pptx") || tempFileNameLowerCase.endsWith(".doc") || tempFileNameLowerCase.endsWith(".docx") 
				|| tempFileNameLowerCase.endsWith(".flv") || tempFileNameLowerCase.endsWith(".mov") || tempFileNameLowerCase.endsWith(".mpeg") || tempFileNameLowerCase.endsWith(".mov") || tempFileNameLowerCase.endsWith(".avi") ) ){
			errorMessage = "File type not supported.";
			return errorMessage;
		}

		try {   
			inputStream = file.getInputStream();
			String extension = "."+FilenameUtils.getExtension(file.getOriginalFilename());
			long currentUnixTime = System.currentTimeMillis() / 1000L;
			fileName = currentUnixTime+RandomStringUtils.randomAlphanumeric(5)+extension;
			
			String filePath = CONTENT_PATH + subject + "/" +fileName;
			String previewPath = subject + "/" + fileName;
			//Check if Folder exists which is one folder per Subject 
			
			File folderPath = new File(CONTENT_PATH  + subject );
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
			bean.setPreviewPath(previewPath);
			outputStream.close();
			inputStream.close();
		} catch (IOException e) {   
			errorMessage = "Error in uploading file for "+bean.getSubject() + " : "+ e.getMessage();
			     
		}   

		return errorMessage;
	}

	@RequestMapping(value = "/admin/downloadFile", method = {RequestMethod.GET, RequestMethod.POST})
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
			  
			request.setAttribute("error", "true");
			request.setAttribute("errorMessage", "Error in downloading file.");
		}
		return modelnView;
	}

	/*@RequestMapping(value = "/viewApplicableSubjectsForm", method = {RequestMethod.GET, RequestMethod.POST})
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
	
	@RequestMapping(value = "/admin/viewApplicableSubjectsForFacultyForm", method = {RequestMethod.GET, RequestMethod.POST})
	public ModelAndView viewApplicableSubjectsForFacultyForm(HttpServletRequest request, HttpServletResponse respnse, Model m) {
		if(!checkSession(request, respnse)){
			return new ModelAndView("studentPortalRediret");
		}
		
		//ArrayList<ProgramSubjectMappingBean> allsubjects = new ArrayList<>();
		ArrayList<ConsumerProgramStructureAcads> allsubjects = new ArrayList<>();
		

		try {
		String facultyId = (String)request.getSession().getAttribute("userId_acads");

		//ContentDAO dao = (ContentDAO)act.getBean("contentDAO");
		ContentInterface content = contentFactory.getStudentType(StudentType.PG);
		allsubjects = content.getFacultySubjectsCodes(facultyId,CURRENT_ACAD_MONTH,CURRENT_ACAD_YEAR); 

		m.addAttribute("yearList", ACAD_YEAR_LIST);
		//m.addAttribute("subjectList", getSubjectList());

		if(allsubjects.size() == 0){
			request.setAttribute("error", "true");
			request.setAttribute("errorMessage", "No subjects found for you.");
			
		}else{
			for (ConsumerProgramStructureAcads programSubjectMappingBean : allsubjects) {
				programSubjectMappingBean.setProgram("NA");
				//programSubjectMappingBean.setSem("NA");
				
			}
		}

		m.addAttribute("subjects",allsubjects);
		int rowCount = allsubjects.size();
		m.addAttribute("rowCount", rowCount);
		

		}catch(Exception e)
		{
			request.setAttribute("error", "true");
			request.setAttribute("errorMessage", "Error in getting subjects for faculty. "+e.getMessage());
			logger.error("Error in getting subjects for faculty. Method :- viewApplicableSubjectsForFacultyForm ",e);

		}
		m.addAttribute("currentMonth", CURRENT_ACAD_MONTH);
		m.addAttribute("currentYear", CURRENT_ACAD_YEAR);
		m.addAttribute("currentType", "PG");
		m.addAttribute("content", new ContentAcadsBean());
		m.addAttribute("monthList", ACAD_MONTH_LIST);
		return new ModelAndView("viewApplicableSubjectsForFaculty");
	}

	private ArrayList<ProgramSubjectMappingAcadsBean> getFailSubjects(StudentAcadsBean student) {
		ContentDAO dao = (ContentDAO)act.getBean("contentDAO");
		ArrayList<ProgramSubjectMappingAcadsBean> failSubjectList = dao.getFailSubjectsForAStudent(student.getSapid());
		return failSubjectList;
	}

	private ArrayList<ProgramSubjectMappingAcadsBean> getSubjectsForStudent(StudentAcadsBean student) {
		ArrayList<ProgramSubjectMappingAcadsBean> programSubjectMappingList = getProgramSubjectMappingList();
		ArrayList<ProgramSubjectMappingAcadsBean> subjects = new ArrayList<>();
		for (int i = 0; i < programSubjectMappingList.size(); i++) {
			ProgramSubjectMappingAcadsBean bean = programSubjectMappingList.get(i);

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

	/*@RequestMapping(value = "/viewContentForSubject", method = {RequestMethod.GET, RequestMethod.POST})
	public ModelAndView viewContentForSubject(HttpServletRequest request, HttpServletResponse respnse, Model m, @ModelAttribute ContentBean content) {

		if(!checkSession(request, respnse)){
			return new ModelAndView("studentPortalRediret");
		}

		StudentBean student = (StudentBean)request.getSession().getAttribute("student_acads");
		Person user = (Person)request.getSession().getAttribute("user_acads");
		String roles = user.getRoles();
		String subject = request.getParameter("subject").trim();
		ContentDAO dao = (ContentDAO)act.getBean("contentDAO");
		
		List<ContentBean> allContentListForSubject = dao.getContentsForSubjectsForCurrentSession(subject, content);
		
		VideoContentDAO vDao = (VideoContentDAO) act.getBean("videoContentDAO");
		List<VideoContentBean> videoContentList = vDao.getVideoContentForSubject(subject, content);
		 
		request.getSession().setAttribute("videoContentList", videoContentList);

		if(allContentListForSubject == null || allContentListForSubject.size() == 0){
			request.setAttribute("error", "true");
			request.setAttribute("errorMessage", "No Study Material found for this subject.");
			return new ModelAndView("viewContentForSubject");
		}
		
		List<ContentBean> contentList = new ArrayList<ContentBean>();
		
		 
		request.getSession().setAttribute("videoContentList", videoContentList);
	
		
		String userId = (String)request.getSession().getAttribute("userId_acads");
		if(student != null && (userId.startsWith("77") || userId.startsWith("79") )){
			
			if(student.getWaivedOffSubjects().contains(subject)){
				//If subject is waived off, dont go to assignment submission page.
				setError(request, subject + " subject is not applicable for you.");
				return viewApplicableSubjectsForm(request, respnse, m);
			}
			
			String programStructureForStudent = student.getPrgmStructApplicable();
			for (ContentBean contentBean : allContentListForSubject) {
				String programStructureForContent = contentBean.getProgramStructure();
				
				if(programStructureForContent == null || "".equals(programStructureForContent.trim()) || "All".equals(programStructureForContent)){
					contentList.add(contentBean);
				}else if(programStructureForContent.equals(programStructureForStudent)){
					contentList.add(contentBean);
				}
			}
		}else{
			contentList = allContentListForSubject;
			contentList = addConsumerProgramProgramStructureNameToEachContentFile(dao,contentList);
		}
		
		//Added check for UG faculty
		if(roles.indexOf("Faculty") != -1) {
			List<ContentBean> newContentList = new ArrayList<ContentBean>();
			if (userId.equalsIgnoreCase("NGASCE24072020") && subject.equalsIgnoreCase("Business Communication")) {
				for (ContentBean contentBean : contentList) {
					if (contentBean.getProgramStructure().equalsIgnoreCase("Jul2020")) {
						newContentList.add(contentBean);
					}
				}
				contentList = newContentList;
			}else if(subject.equalsIgnoreCase("Business Communication") && !userId.equalsIgnoreCase("NMSCEMU200303331")) {
				for (ContentBean contentBean : contentList) {
					if (contentBean.getProgramStructure().equalsIgnoreCase("Jul2020")) {
						newContentList.add(contentBean);
					}
				}
				contentList.removeAll(newContentList);
			}
		}
		 
		m.addAttribute("contentList",contentList);
		int rowCount = (contentList == null ? 0 : contentList.size());
		m.addAttribute("rowCount", rowCount);
		m.addAttribute("subject", subject);
		
		return new ModelAndView("viewContentForSubject");
	}*/
	 

	private List<ContentAcadsBean> addConsumerProgramProgramStructureNameToEachContentFile( List<ContentAcadsBean> contentList) {
		try {
			/*int size = 0, i = 1;
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
			
			for(ContentBean t : contentList) {*/
				
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
					
					//t.setCountOfProgramsApplicableTo(contentIdNCountOfProgramsApplicableToMap.get(t.getId()));
			ContentInterface contents = contentFactory.getStudentType(StudentType.PG);
			contentList = contents.addConsumerProgramProgramStructureNameToEachContentFile(contentList);
				
				
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			  
		}
		
		return contentList;
	}

	@RequestMapping(value = "/admin/viewLastCycleRecordings", method = {RequestMethod.GET, RequestMethod.POST})
	public ModelAndView viewLastCycleRecordings(HttpServletRequest request, HttpServletResponse respnse, Model m) {

		try {
		if(!checkSession(request, respnse)){
			return new ModelAndView("studentPortalRediret");
		}

		ContentInterface content = contentFactory.getStudentType(StudentType.PG);
		String subjectCodeId = request.getParameter("subjectCodeId");
		String month = request.getParameter("month");
		String year = request.getParameter("year");
		m.addAttribute("month",month);
		m.addAttribute("year",year);
		m.addAttribute("subjectCodeId",subjectCodeId);
		m.addAttribute("subject", content.getSubjectNameBySubjectCodeId(subjectCodeId));
		//ContentDAO dao = (ContentDAO)act.getBean("contentDAO");


		List<ContentAcadsBean> contentList = content.getRecordingForLastCycleBySubjectCode(subjectCodeId,month,year);


		if(contentList.size() == 0){
			request.setAttribute("error", "true");
			request.setAttribute("errorMessage", "No Session recordings found for this subject.");
			//return new ModelAndView("viewLastCycleRecordings");
		}
		
		//Get the count of mappings
		contentList = addConsumerProgramProgramStructureNameToEachContentFile(contentList);
		
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
		
		
		}catch(Exception e)
		{
			request.setAttribute("error","true");
			request.setAttribute("errorMessage", "Error in Getting content of last recording. "+e.getMessage());
			logger.info("Error in Getting  content of last recording .Method Name:-viewLastCycleRecordings",e);
		}
		return new ModelAndView("viewLastCycleRecordings");
	}

	@RequestMapping(value = "/admin/viewAllSubjectsForContent", method = {RequestMethod.GET, RequestMethod.POST})
	public String viewAllSubjectsForContent(HttpServletRequest request, HttpServletResponse respnse, Model m) {

		//ArrayList<String> subjects = getSubjectList();
		try {
        ContentInterface content = contentFactory.getStudentType(StudentType.PG);
        ArrayList<ConsumerProgramStructureAcads> subjects = content.getSubjectCodeLists();
		
        m.addAttribute("currentMonth", CURRENT_ACAD_MONTH);
		m.addAttribute("currentYear", CURRENT_ACAD_YEAR);
		
		
		m.addAttribute("monthList", ACAD_MONTH_LIST);
		m.addAttribute("yearList", ACAD_YEAR_LIST);
		m.addAttribute("content", new ContentAcadsBean());
		
		if(subjects.size() == 0){
			request.setAttribute("error", "true");
			request.setAttribute("errorMessage", "No subjects found.");
			return "viewAllSubjects";
		}

		m.addAttribute("subjects",subjects);  
		
		int rowCount = subjects.size();
		m.addAttribute("rowCount", rowCount);
		
		//Commented as it was not needed
		

		}catch(Exception e)
		{
			request.setAttribute("error","true");
			request.setAttribute("errorMessage", "Error in Getting  subjects. "+e.getMessage());
			logger.info("Error in Getting subjects.Method Name:-viewAllSubjectsForContent",e);
		}

		
		//m.addAttribute("monthList", ACAD_MONTH_LIST);

		return "viewAllSubjects";
	}

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
//			  
//			request.setAttribute("error", "true");
//			request.setAttribute("errorMessage", "Error in deleting Record.");
//		}
//		FacultyCourseMappingBean searchBean = (FacultyCourseMappingBean)request.getSession().getAttribute("searchBean_acads");
//		if(searchBean == null){
//			searchBean = new FacultyCourseMappingBean();
//		}
//		return viewContentForSubject(request,response, m, newContentBean);
//	}
	
	/*@RequestMapping(value = "/deleteSingleContentFromCommonSetup", method = RequestMethod.GET)
	public String deleteSingleContentFromCommonSetup(HttpServletRequest request,
														   HttpServletResponse response,
														   Model m,
														   @RequestParam("contentId") String contentId,
														   @RequestParam("consumerProgramStructureId") String consumerProgramStructureId  
															){
		ContentDAO dao = (ContentDAO)act.getBean("contentDAO");
		ContentBean content = dao.findById(contentId);
			
			int countOfProgramsContentApplicableTo = dao.getCountOfProgramsContentApplicableToById(contentId);

			if(countOfProgramsContentApplicableTo > 0) {
				int deletedRows =  dao.deleteContentIdAndMasterKeyMappingByIdAndMasterkey(contentId, consumerProgramStructureId);
				
				if(deletedRows > 0) {
					if(countOfProgramsContentApplicableTo == 1) {
						int deletedContentRows =  dao.deleteContentById((long)Long.parseLong(contentId));

						if(deletedContentRows < 1){
								request.setAttribute("error", "true");
								request.setAttribute("errorMessage", "Error in deleting Content Record.");
								return "forward:/viewContentForSubject?subject="+content.getSubject();
						}
					}
				}else {
				request.setAttribute("error", "true");
				request.setAttribute("errorMessage", "Error in deleting Mapping Record.");
				return "forward:/viewContentForSubject?subject="+content.getSubject();
				}
				request.setAttribute("success","true");
				request.setAttribute("successMessage","Record deleted successfully");
			}else {
				request.setAttribute("error", "true");
				request.setAttribute("errorMessage", "Error in getting countOfProgramsContentApplicableTo.");
				return "forward:/viewContentForSubject?subject="+content.getSubject();
			}
		
		request.setAttribute("success", "true");
		request.setAttribute("successMessage", "Record Deleted Successfully.");
		return "forward:/viewContentForSubject?subject="+content.getSubject();
	}*/

	@RequestMapping(value = "/admin/editContent", method = {RequestMethod.GET, RequestMethod.POST})
	public ModelAndView editContent(HttpServletRequest request, HttpServletResponse respnse, @ModelAttribute ContentAcadsBean content) {
		ModelAndView modelnView = new ModelAndView("addContent");
		try {
		//ContentDAO dao = (ContentDAO)act.getBean("contentDAO");
		//content = dao.findById(request.getParameter("id"));

		modelnView.addObject("edit", "true");
		modelnView.addObject("consumerType", getConsumerTypeList());
		modelnView.addObject("programStructureIdNameMap", getProgramStructureIdNameMap());
		modelnView.addObject("programIdNameMap", getProgramIdNameMap());
		modelnView.addObject("programStrutureList", getProgramStrutureList());
			
		content = getContentWithConfigIdsById(content,request.getParameter("id"));
				

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

		content.setSubjectCodeId(request.getParameter("subjectCodeId"));
		modelnView.addObject("content",content);
		
		//Commented as it was not needed
		/*modelnView.addObject("yearList", ACAD_YEAR_LIST);
		modelnView.addObject("subjectList", getSubjectList());*/
		

		}catch(Exception e)
		{
			request.setAttribute("error","true");
			request.setAttribute("errorMessage", "Error in Getting   content. "+e.getMessage());
			logger.info("Error in Getting content.Method Name:-editContent",e);
		}
		return modelnView;
	}
	
	@RequestMapping(value = "/admin/editSingleContentFromCommonSetup", method = RequestMethod.GET)
	public ModelAndView editSingleContentFromCommonSetup(HttpServletRequest request,
														 HttpServletResponse respnse,
														 @ModelAttribute ContentAcadsBean content,
														 @RequestParam("contentId") String contentId,
														 @RequestParam("consumerTypeId") String consumerTypeId,
														 @RequestParam("programStructureId") String programStructureId,
														 @RequestParam("programId") String programId,
														 @RequestParam("consumerProgramStructureId") String consumerProgramStructureId,
														 @RequestParam("programSemSubjectId") String programSemSubjectId
														 ) {
		ModelAndView modelnView = new ModelAndView("addContent");

		try {
		//ContentDAO dao = (ContentDAO)act.getBean("contentDAO");
			
		modelnView.addObject("edit", "true");
		modelnView.addObject("consumerType", getConsumerTypeList());
		modelnView.addObject("programStructureIdNameMap", getProgramStructureIdNameMap());
		modelnView.addObject("programIdNameMap", getProgramIdNameMap());
		ContentInterface contents = contentFactory.getStudentType(StudentType.PG);
		
		//Find Content By it's Id
		content = contents.findById(contentId);
		
		content.setProgramSemSubjectId(programSemSubjectId);
		content.setConsumerTypeId(consumerTypeId);
		content.setProgramStructureId(programStructureId);
		content.setProgramId(programId);
		content.setConsumerProgramStructureId(consumerProgramStructureId);;
		content.setEditSingleContentFromCommonSetup("true");
		content.setSubjectCodeId(request.getParameter("subjectCodeId"));

		
		modelnView.addObject("content",content);
		
		//Commented as it was not needed
		/*modelnView.addObject("yearList", ACAD_YEAR_LIST);
		modelnView.addObject("subjectList", getSubjectList());*/
		
		
		}catch(Exception e)
		{
			request.setAttribute("error","true");
			request.setAttribute("errorMessage", "Error in Getting   content. "+e.getMessage());
			logger.info("Error in Getting content.Method Name:-editSingleContentFromCommonSetup",e);
		}
		return modelnView;
	}


	private ContentAcadsBean getContentWithConfigIdsById(ContentAcadsBean content, String id) {
		
		ContentInterface contents = contentFactory.getStudentType(StudentType.PG);
		
		content = contents.findById(id);
		
		List<ContentAcadsBean> configDetails= contents.getProgramsListForCommonContent(id);
		

		 String consumerTypeId = "";
		 String programStructureId = "";
		 String programId = "";
		 int consumerTypeIdCounter = 0;
		 int programStructureIdCounter = 0;
		 int programIdCounter = 0;
		Map<String,String> checkForConsumerTypeId = new HashMap<>();
		Map<String,String> checkForProgramId = new HashMap<>();
		Map<String,String> checkForProgramStructureId = new HashMap<>();
		
		for(ContentAcadsBean c :configDetails) {
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

	
	
	/*@RequestMapping(value = "/updateSingleContentFromCommonSetup",  method = {RequestMethod.GET, RequestMethod.POST})
	public String updateSingleContentFromCommonSetup(HttpServletRequest request,
													 HttpServletResponse response, 
													 @ModelAttribute ContentBean contentFromForm){
		ContentDAO dao = (ContentDAO)act.getBean("contentDAO");
		
		/*try {
			
			ContentBean content =  dao.findById(contentFromForm.getId());
			content.setConsumerTypeId(contentFromForm.getConsumerTypeId());
			content.setProgramStructureId(contentFromForm.getProgramStructureId());
			content.setProgramId(contentFromForm.getProgramId());
			content.setConsumerProgramStructureId(contentFromForm.getConsumerProgramStructureId());
			content.setName(contentFromForm.getName());
			content.setDescription(contentFromForm.getDescription());
			content.setWebFileurl(contentFromForm.getWebFileurl());
			content.setUrlType(contentFromForm.getUrlType());
			content.setContentType(contentFromForm.getContentType());
			
			ContentFilesSetbean fileset = new ContentFilesSetbean();
			fileset.setConsumerTypeId(content.getConsumerTypeId());
			fileset.setProgramStructureId(content.getProgramStructureId());
			fileset.setProgramId(content.getProgramId());
			fileset.setConsumerProgramStructureId(Integer.parseInt(content.getConsumerProgramStructureId()));;
			fileset.setSubject(content.getSubject());
			

			String userId = (String)request.getSession().getAttribute("userId_acads");
			content.setCreatedBy(userId);
			content.setLastModifiedBy(userId);

			long newContentId = dao.saveContentFileDetails(content, content.getSubject(), content.getYear(), content.getMonth());
			
			if(newContentId < 1 ) {
				request.setAttribute("error", "true");
				request.setAttribute("errorMessage", "Error in file saving file details to DB, FileName : "+content.getName()
									+ "");
				return "forward:/viewContentForSubject?subject="+content.getSubject();
				
			}else {//Create mappings of contentId and masterkey
				
				String createMappingsError = createContentIdMasterkeyMappings(dao,fileset,newContentId);
				
				if(!StringUtils.isBlank(createMappingsError)) {
					int deleteContentRow = dao.deleteContentById(newContentId);
					request.setAttribute("error", "true");
					request.setAttribute("errorMessage", "Error in creating mappings of contentId and masterkey, FileName : "+content.getName()+" Rows deleted of content details: "+deleteContentRow
										+ "");
					return "forward:/viewContentForSubject?subject="+content.getSubject();
					
				}
				
			}
			
			//delete old mappings of masterkey and id 
			int rowsDeletedOfOldMapping = dao.deleteContentIdMasterkeyMappingsByIdNMasterKey(content.getId(), contentFromForm.getConsumerProgramStructureId());
			

			if(rowsDeletedOfOldMapping < 1 ) {
				request.setAttribute("error", "true");
				request.setAttribute("errorMessage", "Error in file saving file details to DB, FileName : "+content.getName()
									+ "");
				return "forward:/viewContentForSubject?subject="+content.getSubject();
				
			}
			
			
			setSuccess(request, "Content details created successfully");
		} catch (Exception e) {
			  
			setError(request, "Error in updating content");
		}
		return "forward:/viewContentForSubject?subject="+contentFromForm.getSubject();
	}*/
	
//	@RequestMapping(value = "/admin/uploadFacultyCourseForm", method = {RequestMethod.GET, RequestMethod.POST})
	public String uploadFacultyCourseForm(HttpServletRequest request, HttpServletResponse respnse, Model m) {

		FileAcadsBean  facultyCourse = new FileAcadsBean();
		m.addAttribute("facultyCourse",facultyCourse);
		m.addAttribute("subjectList", getSubjectList());
		m.addAttribute("yearList", ACAD_YEAR_LIST);
		return "uploadFacultyCourseMapping";
	}
	
//	@RequestMapping(value = "/admin/uploadFacultyCourseMapping", method = {RequestMethod.GET, RequestMethod.POST})
	public ModelAndView uploadFacultyCourseMapping(@ModelAttribute FileAcadsBean facultyCourse, BindingResult result,HttpServletRequest request, Model m){
		ModelAndView modelnView = new ModelAndView("uploadFacultyCourseMapping");
		try{
			String userId = (String)request.getSession().getAttribute("userId_acads");
			ExcelHelper excelHelper = new ExcelHelper();
			ArrayList<List> resultList = excelHelper.readFacultyCourseAccessExcel(facultyCourse, getSubjectList(), getFacultyList(), userId,getsubjectCodeMap(),getSubjectCodesList());

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
			  
			request.setAttribute("error", "true");
			request.setAttribute("errorMessage", "Error in inserting rows.");
		}
		return modelnView;
	}
	
	
	
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
		      mav.addObject("listOfFiles",listOfFiles[i].getAbsolutePath());
		    }	
		  
		    mav.setViewName("pdfView");
		return mav;
	}*/

	@RequestMapping(value = "/admin/createMappingsOfCententIdAndMasterKey", method = {RequestMethod.GET, RequestMethod.POST})
	public String createMappingsOfCententIdAndMasterKey(HttpServletRequest request, HttpServletResponse respnse, Model m) {

		ContentDAO dao = (ContentDAO)act.getBean("contentDAO");
		List<ContentAcadsBean> contentList = dao.getAllContents();
		List<ContentAcadsBean> cententIdAndMasterKeyMappings  = new ArrayList<>();
		StringBuffer errorMessage = new StringBuffer("");
		
		//create mappings
		for(ContentAcadsBean c : contentList) {
			String subject = c.getSubject();
			String programStructure = c.getProgramStructure();
			Long moduleId = c.getSessionPlanModuleId();
			
				List<ContentAcadsBean> consumerProgramStructureIdList = dao.getconsumerProgramStructureIdList(subject, programStructure, moduleId);
				for (ContentAcadsBean bean : consumerProgramStructureIdList) {
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

		FileAcadsBean  facultyCourse = new FileAcadsBean();
		m.addAttribute("facultyCourse",facultyCourse);
		m.addAttribute("subjectList", getSubjectList());
		m.addAttribute("yearList", ACAD_YEAR_LIST);
		return uploadFacultyCourseForm(request, respnse, m);
	}

	private List<ContentAcadsBean> createCententIdAndMasterKeyMappings(List<ContentAcadsBean> cententIdAndMasterKeyMappings, String id, List<String> masterKeys) {
		for(String k : masterKeys) {
			ContentAcadsBean tempBean = new ContentAcadsBean();
			tempBean.setId(id);
			tempBean.setConsumerProgramStructureId(k);
			cententIdAndMasterKeyMappings.add(tempBean);
		}
		return cententIdAndMasterKeyMappings;
	}
	@RequestMapping(value = "/admin/makeContentLiveForm", method = RequestMethod.GET)
	public String makeContentLiveForm(HttpServletRequest request, HttpServletResponse response, Model m) {

		ContentDAO dao = (ContentDAO)act.getBean("contentDAO");
		if(!m.containsAttribute("searchBean")){
			m.addAttribute("searchBean", new ContentAcadsBean());	
		}
		m.addAttribute("yearList", currentYearList);
		m.addAttribute("contentLiveConfigList", dao.getContentLiveConfigList());
		m.addAttribute("acadsYearList", ACAD_YEAR_LIST);
		m.addAttribute("acadsMonthList", ACAD_MONTH_LIST);
		m.addAttribute("consumerType", getConsumerTypeList());
		
		return "makeContentLiveForm";
	}
	
	@RequestMapping(value = "/admin/saveContentLiveConfig",  method = RequestMethod.POST)
	public String saveContentLiveConfig(HttpServletRequest request,
									 HttpServletResponse response, 
									 @ModelAttribute ContentAcadsBean  searchBean,
									 Model m) {
		
		ContentDAO dao = (ContentDAO)act.getBean("contentDAO");
		List<String> consumerProgramStructureIds = dao.getconsumerProgramStructureIds(searchBean.getProgramId(),
																						   searchBean.getProgramStructureId(),
																						   searchBean.getConsumerTypeId());
				

		String errorMessage = dao.batchInsertOfMakeContentLiveConfigs(searchBean,consumerProgramStructureIds);
				
		if(StringUtils.isBlank(errorMessage)) {
			request.setAttribute("success","true");
			request.setAttribute("successMessage", " Content Is Live Successfully. ");
			m.addAttribute("searchBean", searchBean);	
			return makeContentLiveForm(request, response, m);
		}else {
			request.setAttribute("error", "true");
			request.setAttribute("errorMessage", errorMessage);
			m.addAttribute("searchBean", searchBean);	
			return makeContentLiveForm(request, response, m);
		}
	}
	

	@RequestMapping(value = "/admin/getProgramsListForCommonContent", method = {RequestMethod.POST})
	public ResponseEntity<List<ContentAcadsBean>> getProgramsListForCommonContent(@RequestBody ContentAcadsBean bean) {
		//ContentDAO dao = (ContentDAO)act.getBean("contentDAO");
		ContentInterface content = contentFactory.getStudentType(StudentType.PG);
		
		return new ResponseEntity<List<ContentAcadsBean>>(content.getProgramsListForCommonContent(bean.getId()),HttpStatus.OK);
	}

	
	@RequestMapping(value = "/admin/searchContentForm", method = RequestMethod.GET)
	public String searchContentForm(HttpServletRequest request, HttpServletResponse response, Model m) {


		try {

		ContentAcadsBean searchBean = new ContentAcadsBean();

		ContentInterface content = contentFactory.getStudentType(StudentType.PG);
		m.addAttribute("searchBean",searchBean);
		m.addAttribute("yearList", ACAD_YEAR_LIST);
		m.addAttribute("monthList", ACAD_MONTH_LIST);
		m.addAttribute("consumerTypeList", getConsumerTypesList());
		m.addAttribute("subjectcodes",content.getSubjectCodeLists());
		}catch(Exception e)
		{
			request.setAttribute("error","true");
			request.setAttribute("errorMessage", "Error in searching   content. "+e.getMessage());
			logger.info("Error in searching content.Method Name:-searchContentForm",e);
		}
		return "searchContent";
	}
	

//	@RequestMapping(value = "/searchContent",  method = {RequestMethod.GET, RequestMethod.POST})
//	public ModelAndView searchContent(HttpServletRequest request, HttpServletResponse response, @ModelAttribute ContentBean searchBean){
//		
//		ModelAndView modelnView = new ModelAndView("searchContent");
//		request.getSession().setAttribute("searchBean_acads", searchBean);
//		TimeTableDAO dao = (TimeTableDAO)act.getBean("timeTableDAO");
//		ContentDAO cDao = (ContentDAO)act.getBean("contentDAO");
//				
//		if(searchBean.getConsumerProgramStructureId() == null) {
//			List<String> consumerProgramStructureIds =new ArrayList<String>();
//			if(!StringUtils.isBlank(searchBean.getProgramId()) && !StringUtils.isBlank(searchBean.getProgramStructureId()) && !StringUtils.isBlank(searchBean.getConsumerTypeId())){
//				consumerProgramStructureIds = dao.getconsumerProgramStructureIds(searchBean.getProgramId(),searchBean.getProgramStructureId(),searchBean.getConsumerTypeId());
//			}
//			
//			String consumerProgramStructureIdsSaperatedByComma = "";
//			if(!consumerProgramStructureIds.isEmpty()){
//				for(int i=0;i < consumerProgramStructureIds.size();i++){
//					consumerProgramStructureIdsSaperatedByComma = consumerProgramStructureIdsSaperatedByComma + ""+ consumerProgramStructureIds.get(i) +",";
//				}
//				consumerProgramStructureIdsSaperatedByComma = consumerProgramStructureIdsSaperatedByComma.substring(0,consumerProgramStructureIdsSaperatedByComma.length()-1);
//			}
//			searchBean.setConsumerProgramStructureId(consumerProgramStructureIdsSaperatedByComma);
//		}
//		
//		String searchType = request.getParameter("searchType") == null ? "distinct" : request.getParameter("searchType");
//		request.getSession().setAttribute("searchType", searchType);
//		Page<ContentBean> page = cDao.getResourcesContent(1, pageSize, searchBean, searchType);
//		List<ContentBean> resourcesContentList = page.getPageItems();
//		
//		modelnView.addObject("page", page);
//		modelnView.addObject("searchBean", searchBean);
//		modelnView.addObject("rowCount", page.getRowCount());
//		modelnView.addObject("yearList", ACAD_YEAR_LIST);
//		modelnView.addObject("monthList", ACAD_MONTH_LIST);
//		modelnView.addObject("subjectList", getSubjectList());
//		modelnView.addObject("consumerTypeList", getConsumerTypesList());
//		modelnView.addObject("subject", searchBean.getSubject());
//		
//		modelnView.addObject("searchBean", searchBean);
//		modelnView.addObject("searchType", searchType);
//		if(resourcesContentList == null || resourcesContentList.size() == 0){
//			request.setAttribute("error", "true");
//			request.setAttribute("errorMessage", "No Records found.");
//		}
//		modelnView.addObject("resourcesContentList", resourcesContentList);
//		
//		return modelnView;
//	}
//	

	@RequestMapping(value = "/admin/searchContentPage",  method = {RequestMethod.GET, RequestMethod.POST})
	public ModelAndView searchScheduledSessionPage(HttpServletRequest request, HttpServletResponse response,Model m){
		
		ModelAndView modelnView = new ModelAndView("searchContent");
		try {
		
		//ContentDAO cDao = (ContentDAO)act.getBean("contentDAO");
		ContentAcadsBean searchBean = (ContentAcadsBean)request.getSession().getAttribute("searchBean_acads");
		String searchType = (String) request.getSession().getAttribute("searchType");
		int pageNo = Integer.parseInt(request.getParameter("pageNo"));

		ContentInterface content = contentFactory.getStudentType(StudentType.PG);
		
		
		//-----------searchContent Strategy Called -----------
		PageAcads<ContentAcadsBean> page = content.searchContent(pageNo,searchBean, searchType);
		List<ContentAcadsBean> resourcesContentList = page.getPageItems();

		modelnView.addObject("page", page);
		modelnView.addObject("rowCount", page.getRowCount());
		modelnView.addObject("searchBean",searchBean);
		modelnView.addObject("yearList", ACAD_YEAR_LIST);
		
		//Commented as it was not needed
		//modelnView.addObject("monthList", ACAD_MONTH_LIST);
		//modelnView.addObject("subjectList", getSubjectList());
		
		m.addAttribute("searchType",searchType);
		
		modelnView.addObject("consumerTypeList", getConsumerTypesList());
		m.addAttribute("subjectcodes",content.getSubjectCodeLists());

		if(resourcesContentList.size() == 0){
			request.setAttribute("error", "true");
			request.setAttribute("errorMessage", "No Resources Content found.");
		}
		modelnView.addObject("resourcesContentList", resourcesContentList);
		
		}catch(Exception e)
		{
			request.setAttribute("error", "true");
			request.setAttribute("errorMessage", "Error In Getting Content ."+e.getMessage());
			logger.error("Error In Getting Content by searchScheduledSessionPage . ",e);
		}
	
		return modelnView;
	}
	

	@RequestMapping(value = "/admin/getCommonContentProgramsList", method = {RequestMethod.POST})
		public ResponseEntity<ArrayList<ContentAcadsBean>> getCommonContentProgramsList(@RequestBody ContentAcadsBean bean) {
			//ContentDAO cDao = (ContentDAO)act.getBean("contentDAO");
			ContentInterface contents = contentFactory.getStudentType(StudentType.PG);
			
			return new ResponseEntity<ArrayList<ContentAcadsBean>>(contents.getCommonGroupProgramList(bean),HttpStatus.OK);
		}

	

	@RequestMapping(value = "/editContents", method = {RequestMethod.GET, RequestMethod.POST})
	public ModelAndView editContents(HttpServletRequest request, HttpServletResponse respnse, @ModelAttribute ContentAcadsBean content) {
		ModelAndView modelnView = new ModelAndView("addContent");
		try {
		//ContentDAO dao = (ContentDAO)act.getBean("contentDAO");
			
		modelnView.addObject("edit", "true");
		modelnView.addObject("consumerType", getConsumerTypeList());
		modelnView.addObject("programStructureIdNameMap", getProgramStructureIdNameMap());
		modelnView.addObject("programIdNameMap", getProgramIdNameMap());
		modelnView.addObject("editMapping", "true");
		content = getContentWithConfigIdsById(content,request.getParameter("id"));
				
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
		
		//Commented as it was not needed
		/*modelnView.addObject("yearList", ACAD_YEAR_LIST);
		modelnView.addObject("subjectList", getSubjectList());*/
		
	
		}catch(Exception e)
		{
			request.setAttribute("error","true");
			request.setAttribute("errorMessage", "Error in Getting  content. "+e.getMessage());
			logger.info("Error in Getting content.Method Name:-editContents",e);
		}
		return modelnView;
	}
	

	@RequestMapping(value = "/admin/editSingleContentMapping", method = RequestMethod.GET)
	public ModelAndView editSingleContentMapping(HttpServletRequest request,
														 HttpServletResponse respnse,
														 @ModelAttribute ContentAcadsBean content,
														 @RequestParam("contentId") String contentId,
														 @RequestParam("consumerTypeId") String consumerTypeId,
														 @RequestParam("programStructureId") String programStructureId,
														 @RequestParam("programId") String programId,
														 @RequestParam("consumerProgramStructureId") String consumerProgramStructureId,
														 @RequestParam("programSemSubjectId") String programSemSubjectId
														 ) {
		ModelAndView modelnView = new ModelAndView("addContent");

		try {
		modelnView.addObject("edit", "true");
		modelnView.addObject("consumerType", getConsumerTypeList());
		modelnView.addObject("programStructureIdNameMap", getProgramStructureIdNameMap());
		modelnView.addObject("programIdNameMap", getProgramIdNameMap());
		modelnView.addObject("editMapping", "true");
		modelnView.addObject("yearList", ACAD_YEAR_LIST);
		//ContentDAO dao = (ContentDAO)act.getBean("contentDAO");
		ContentInterface contents = contentFactory.getStudentType(StudentType.PG);
		
		//Find content By it's Id
		content = contents.findById(contentId);
		
		content.setProgramSemSubjectId(programSemSubjectId);
		content.setConsumerTypeId(consumerTypeId);
		content.setProgramStructureId(programStructureId);
		content.setProgramId(programId);
		content.setConsumerProgramStructureId(consumerProgramStructureId);;
		content.setEditSingleContentFromCommonSetup("true");
		
		modelnView.addObject("content",content);
	
		
		//Commented as it was not needed
		//modelnView.addObject("subjectList", getSubjectList());
		
		
		}catch(Exception e)
		{
			request.setAttribute("error","true");
			request.setAttribute("errorMessage", "Error in Getting  content. "+e.getMessage());
			logger.info("Error in Getting content.Method Name:-editSingleContentMapping",e);
		}
		return modelnView;
	}
	

//	@RequestMapping(value = "/updateSingleContentMapping",  method = {RequestMethod.GET, RequestMethod.POST})
//	public ModelAndView updateSingleContentMapping(HttpServletRequest request, HttpServletResponse response, 
//												 	@ModelAttribute ContentBean contentFromForm){
//		
//		ContentDAO dao = (ContentDAO)act.getBean("contentDAO");
//		
//		try {
//			
//			ContentBean content =  dao.findById(contentFromForm.getId());
//			content.setConsumerTypeId(contentFromForm.getConsumerTypeId());
//			content.setProgramStructureId(contentFromForm.getProgramStructureId());
//			content.setProgramId(contentFromForm.getProgramId());
//			content.setConsumerProgramStructureId(contentFromForm.getConsumerProgramStructureId());
//			content.setName(contentFromForm.getName());
//			content.setDescription(contentFromForm.getDescription());
//			content.setWebFileurl(contentFromForm.getWebFileurl());
//			content.setUrlType(contentFromForm.getUrlType());
//			content.setContentType(contentFromForm.getContentType());
//			
//			ContentFilesSetbean fileset = new ContentFilesSetbean();
//			fileset.setConsumerTypeId(content.getConsumerTypeId());
//			fileset.setProgramStructureId(content.getProgramStructureId());
//			fileset.setProgramId(content.getProgramId());
//			fileset.setConsumerProgramStructureId(Integer.parseInt(content.getConsumerProgramStructureId()));;
//			fileset.setSubject(content.getSubject());
//			
//			String userId = (String)request.getSession().getAttribute("userId_acads");
//			content.setCreatedBy(userId);
//			content.setLastModifiedBy(userId);
//
//			long newContentId = dao.saveContentFileDetails(content, content.getSubject(), content.getYear(), content.getMonth());
//			
//			if(newContentId < 1 ) {
//				request.setAttribute("error", "true");
//				request.setAttribute("errorMessage", "Error in file saving file details to DB, FileName : "+content.getName()+ ".");
//				return searchContent(request,response, content);
//				
//			}else {//Create mappings of contentId and masterkey
//				
//				String createMappingsError = createContentIdMasterkeyMappings(dao,fileset,newContentId);
//				
//				if(!StringUtils.isBlank(createMappingsError)) {
//					int deleteContentRow = dao.deleteContentById(newContentId);
//					request.setAttribute("error", "true");
//					request.setAttribute("errorMessage", "Error in creating mappings of contentId and masterkey, FileName : "+content.getName()
//										+" Rows deleted of content details: "+deleteContentRow + ".");
//					return searchContent(request,response, content);
//				}
//			}
//			
//			//delete old mappings of masterkey and id 
//			int rowsDeletedOfOldMapping = dao.deleteContentIdMasterkeyMappingsByIdNMasterKey(content.getId(), contentFromForm.getConsumerProgramStructureId());
//			
//			if(rowsDeletedOfOldMapping < 1 ) {
//				request.setAttribute("error", "true");
//				request.setAttribute("errorMessage", "Error in file saving file details to DB, FileName : "+content.getName()+ ".");
//				return searchContent(request,response, content);
//			}
//			
//			setSuccess(request, "Content details created successfully");
//			
//		} catch (Exception e) {
//			  
//			setError(request, "Error in updating content");
//		}
//		
//		return searchContent(request,response,contentFromForm);
//	}
//	

	
	//Added by Riya for content upload via subjectcode
	
	
	//To find out whether the pssIds belong to same subject or not
	@RequestMapping(value = "/admin/getSubjectNameByPssId",method = RequestMethod.POST , consumes="application/json", produces="application/json")
	public ResponseEntity getSubjectNameByPssId(@RequestBody List<String> masterKey) {
		ContentInterface content = contentFactory.getStudentType(StudentType.PG);
			
		String pssIds = String.join(", ", masterKey);
		String subject = content.getSubjectNameByPssId(pssIds);
				
		return new ResponseEntity(subject,HttpStatus.OK);
				
	}
	
	//View All Content using subjectcodeId
	@RequestMapping(value = "/admin/viewContentForSubject", method = {RequestMethod.GET, RequestMethod.POST})
	public ModelAndView viewContentForSubject(HttpServletRequest request, HttpServletResponse respnse, Model m, @ModelAttribute ContentAcadsBean content) {

		if(!checkSession(request, respnse)){
			return new ModelAndView("studentPortalRediret");
		}
		PersonAcads person = (PersonAcads) request.getSession().getAttribute("user_acads");
		String userId = (String) request.getSession().getAttribute("userId_acads");
		String roles = person.getRoles();
		ModelAndView modelnew = new ModelAndView("viewContentForSubject"); 
		try{
		ContentInterface contents = contentFactory.getStudentType(content.getStudentType());
		m.addAttribute("type",content.getStudentType());
		StudentAcadsBean student = (StudentAcadsBean)request.getSession().getAttribute("student_acads");
		//Person user = (Person)request.getSession().getAttribute("user_acads");
		
		//String roles = user.getRoles();
	
		
		
		String subjectCodeId = request.getParameter("subjectCodeId").trim();
		
		String subject = contents.getSubjectNameBySubjectCodeId(subjectCodeId);
		
		modelnew.addObject("subjectCodeId", subjectCodeId);
		m.addAttribute("subject",subject);
		m.addAttribute("month",content.getMonth());
		m.addAttribute("year",content.getYear());
		
		content.setSubjectCodeId(subjectCodeId);
		
		//Get The list Of Content Using SubjectCode.
		List<ContentAcadsBean> allContentListForSubject = contents.getContentsBySubjectCodeId(content.getSubjectCodeId(),content.getMonth(),content.getYear());
		
		//VideoContentDAO vDao = (VideoContentDAO) act.getBean("videoContentDAO");
		//List<VideoContentBean> videoContentList = vDao.getVideoContentForSubject(subject, content);
		List<VideoContentAcadsBean> videoContentList=new ArrayList<VideoContentAcadsBean>();
		if(!roles.contains("Faculty")) {
		//Get the video content list using subjectcode 
		videoContentList = contents.getVideoContentForSubject(content);
		}else {
		videoContentList = contents.getVideoContentForSubjectAndFaculty(content, userId);
		}
		request.getSession().setAttribute("videoContentList", videoContentList);

		if(allContentListForSubject == null || allContentListForSubject.size() == 0){
			request.setAttribute("error", "true");
			request.setAttribute("errorMessage", "No Study Material found for this subject.");
			modelnew.addObject("subjectCodeId", subjectCodeId);
			m.addAttribute("month",content.getMonth());
			m.addAttribute("year",content.getYear());
			m.addAttribute("subject",subject);
			return modelnew;
		}
		
		List<ContentAcadsBean> contentList = new ArrayList<ContentAcadsBean>();
		
		if(student != null && (userId.startsWith("77") || userId.startsWith("79") )){
			
			
			if(student.getWaivedOffSubjects().contains(subject)){
				//If subject is waived off, dont go to assignment submission page.
				setError(request, subject + " subject is not applicable for you.");
				return new ModelAndView("redirect:student/viewApplicableSubjectsForm");
			}
			
			String programStructureForStudent = student.getPrgmStructApplicable();
			for (ContentAcadsBean contentBean : allContentListForSubject) {
				String programStructureForContent = contentBean.getProgramStructure();
				
				if(programStructureForContent == null || "".equals(programStructureForContent.trim()) || "All".equals(programStructureForContent)){
					contentList.add(contentBean);
				}else if(programStructureForContent.equals(programStructureForStudent)){
					contentList.add(contentBean);
				}
			}
		}else{
			contentList = allContentListForSubject;
			contentList = addConsumerProgramProgramStructureNameToEachContentFile(contentList);
		}
		
		// Commented By Riya as no longer needed as content is being uploaded by subjectcodeId
		//Added check for UG faculty
		/*if(roles.indexOf("Faculty") != -1) {
			List<ContentBean> newContentList = new ArrayList<ContentBean>();
			
			if (userId.equalsIgnoreCase("NGASCE24072020") && subject.equalsIgnoreCase("Business Communication")) {
				for (ContentBean contentBean : contentList) {
					if (contentBean.getProgramStructure().equalsIgnoreCase("Jul2020")) {
						newContentList.add(contentBean);
					}
				}
				contentList = newContentList;
			}else if(subject.equalsIgnoreCase("Business Communication") && !userId.equalsIgnoreCase("NMSCEMU200303331")) {
				for (ContentBean contentBean : contentList) {
					if (contentBean.getProgramStructure().equalsIgnoreCase("Jul2020")) {
						newContentList.add(contentBean);
					}
				}
				contentList.removeAll(newContentList);
			}

		}
		
		

		}*/
		 

		m.addAttribute("contentList",contentList);
		int rowCount = contentList.size();
		m.addAttribute("rowCount", rowCount);

		
		
		}catch(Exception e)
		{
			request.setAttribute("error","true");
			request.setAttribute("errorMessage", "Error in Getting  content. "+e.getMessage());
			logger.info("Error in Getting content.Method Name:-viewContentForSubject",e);
		}


	

		return modelnew;
	}
	 
	@RequestMapping(value = "/admin/updateSingleContentFromCommonSetup",  method = {RequestMethod.GET, RequestMethod.POST})
	public ModelAndView updateSingleContentFromCommonSetup(HttpServletRequest request,
													 HttpServletResponse response, 
						                             @ModelAttribute ContentAcadsBean contentFromForm,Model m){
		ContentInterface content = contentFactory.getStudentType(StudentType.PG);
		

		String userId = (String)request.getSession().getAttribute("userId_acads");
		
		contentFromForm.setCreatedBy(userId);
		contentFromForm.setLastModifiedBy(userId);
		
		try {
		
		//---------------Update Single Content Strategy Called-----------
		HashMap<String,String> update_response = content.updateContentSingleSetup(contentFromForm);
		
		
		request.setAttribute("success", update_response.get("success"));
		request.setAttribute("successMessage", update_response.get("successMessage"));
		
		
		
		request.setAttribute("subjectCodeId",contentFromForm.getSubjectCodeId());
		}catch(Exception e)
		{
				request.setAttribute("error","true");
				request.setAttribute("errorMessage", "Error in updating  content. "+e.getMessage());
				logger.info("Error in updating single content.Method Name:-updateSingleContentFromCommonSetup",e);
			
		}
		
		return viewContentForSubject(request,response,m,contentFromForm);

		
	}
	@RequestMapping(value = "/admin/deleteSingleContentFromCommonSetup", method = RequestMethod.GET)
	public ModelAndView deleteSingleContentFromCommonSetup(HttpServletRequest request,
														   HttpServletResponse response,
														   Model m,
														   @RequestParam("contentId") String contentId,
														   @RequestParam("subjectCodeId") String subjectCodeId,
														   @RequestParam("consumerProgramStructureId") String consumerProgramStructureId  
															){
		ContentInterface content = contentFactory.getStudentType(StudentType.PG);
		try {
	
		//------------Delete For Single Content Strategy Called-----------
		HashMap<String,String> delete_response  = content.deleteContentSingleSetup(contentId,consumerProgramStructureId);
		
		request.setAttribute("success", delete_response.get("success"));
		request.setAttribute("successMessage", delete_response.get("successMessage"));
		
		request.setAttribute("error",delete_response.get("error"));
		request.setAttribute("errorMessage", delete_response.get("errorMessage"));
		
		request.setAttribute("subjectCodeId",subjectCodeId);
		

		}catch(Exception e)
		{

			request.setAttribute("error","true");
			request.setAttribute("errorMessage","Error in delete single content "+e.getMessage() );
			logger.error("Error in delete single content.Method Name:- deleteSingleContentFromCommonSetup ",e);
		}

		ContentAcadsBean contents=new ContentAcadsBean();

		contents.setYear(request.getParameter("year"));
		contents.setMonth(request.getParameter("month"));
		
		return viewContentForSubject(request,response,m,contents);
	}
	
	@RequestMapping(value = "/admin/updateContent",  method = {RequestMethod.GET, RequestMethod.POST})
	public ModelAndView updateContent(HttpServletRequest request, HttpServletResponse response,Model m, @ModelAttribute ContentAcadsBean content){
		
		ContentInterface contents = contentFactory.getStudentType(StudentType.PG);
		
		String userId = (String)request.getSession().getAttribute("userId_acads");
		
		content.setCreatedBy(userId);
		content.setLastModifiedBy(userId);
		
		try {
		
		//----------------Upload Whole Content Strategy Called-----------
		HashMap<String,String> update_response = contents.updateContent(content);
		request.setAttribute("success", update_response.get("success"));
		request.setAttribute("successMessage", update_response.get("successMessage"));
		

	
		}catch(Exception e)
		{
			request.setAttribute("error","true");
			request.setAttribute("errorMessage", "Error in updating  content. "+e.getMessage());
			logger.info("Error in updating Whole content.Method Name:-updateContent",e);
		}

		request.setAttribute("subjectCodeId",content.getSubjectCodeId());

		return viewContentForSubject(request,response,m,content);
	}
	
	@RequestMapping(value = "/admin/deleteContent", method = {RequestMethod.GET, RequestMethod.POST})
	public ModelAndView deleteContent(HttpServletRequest request, HttpServletResponse response, Model m){
		ContentAcadsBean newContentBean = new ContentAcadsBean();
		
			String id = request.getParameter("id");
			ContentInterface content = contentFactory.getStudentType(StudentType.PG);

			try {
			
			
			//--------------Delete Whole Content Strategy Called-----------
			HashMap<String,String> delete_response  = content.deleteContent(id);
			
			request.setAttribute("success", delete_response.get("success"));
			request.setAttribute("successMessage", delete_response.get("successMessage"));
			
			
			request.setAttribute("subjectCodeId",request.getParameter("subjectCodeId"));
			}catch(Exception e)
			{
				request.setAttribute("error","true");
				request.setAttribute("errorMessage","Error in deleting  Content. "+e.getMessage());
				logger.info("Error in deleting Whole Content. Method:-  deleteContent",e);
			}
			
			newContentBean.setYear(request.getParameter("year"));
			newContentBean.setMonth(request.getParameter("month"));
		
		return viewContentForSubject(request,response, m, newContentBean);
	}
	

	
	
	//For Shifting data In Content Temporary Table
	@RequestMapping(value = "/admin/shiftingLRDataInTempTable",  method = {RequestMethod.GET, RequestMethod.POST})
	public void ShiftingLRDataInTempTable(HttpServletRequest request, HttpServletResponse response){
		
		ContentDAO dao = (ContentDAO)act.getBean("contentDAO");
		System.out.println("shiftingLRDataInTempTable Start=>");
		List<ContentAcadsBean> bean = dao.getAllActivePGContentsX();
		List<ContentAcadsBean> error_list = dao.getAllActivePGContentsX();
		System.out.println("content size: "+bean.size());
		System.out.println("shiftingLRDataInTempTable Start");
		System.out.println("Data to Move==>" + bean.size());
		int success_counter = 0;
		int error_counter = 0;
		for(ContentAcadsBean content:bean){
			System.out.println("Content "+content.getId());
			
			if(!StringUtils.isBlank(content.getYear()))
			{
				try {
					
			int count = dao.insertIntoContentTempTable(content);
			success_counter++;
			System.out.println("Moved: "  + success_counter+ "/" +  bean.size());
			if (count > 0) {
				continue;
			}else {
			
				//logger.info("Error in inserting in data in contentMapping table and temp table For This Subject  : "+content.getSubject()+" and contentId is : "+content.getId());
				//System.out.println("Error in inserting in data in contentMapping table and temp table For This Subject  : "+content.getSubject()+" and contentId is : "+content.getId());
				error_list.add(content);
				error_counter++;
				
			}
				}
			catch(Exception e){
				  
				//logger.error(e);
				//logger.info("Error in inserting in data in contentMapping table and temp table For This Subject  : "+content.getSubject()+" and contentId is : "+content.getId());
				//System.out.println("Error in inserting in data in contentMapping table and temp table For This Subject  : "+content.getSubject()+" and contentId is : "+content.getId());
				error_list.add(content);
				error_counter++;

			}
				

		}
		
		System.out.println("shiftingLRDataInTempTable End");
		
	}
		System.out.println("Moved Success: "  + success_counter+ "/" +  bean.size());
		System.out.println("Move Failed: "  + error_counter+ "/" +  bean.size());

		//logger.error("Error in inserting in data in contentMapping table and temp table For This Subject  : " + error_list.toString());

		//System.out.println("Error in inserting in data in contentMapping table and temp table For This Subject  : " + error_list.toString());
	}	

	
	
	@RequestMapping(value = "/admin/viewApplicableSubjectsForFaculty", method = {RequestMethod.GET,RequestMethod.POST})
	public ModelAndView viewApplicableSubjectsForFaculty(HttpServletRequest request, HttpServletResponse respnse, Model m,
			@RequestParam("year") String year,
			@RequestParam("month") String month,
			@RequestParam("type") StudentType type) {
		if(!checkSession(request, respnse)){
			return new ModelAndView("studentPortalRediret");
		}

		try {
	

		
		ArrayList<ConsumerProgramStructureAcads> allsubjects = new ArrayList<>();

		String facultyId = (String)request.getSession().getAttribute("userId_acads");

		
		ContentInterface content = contentFactory.getStudentType(type);
		allsubjects = content.getFacultySubjectsCodes(facultyId,month,year); 


		if(allsubjects.size() == 0){
			request.setAttribute("error", "true");
			request.setAttribute("errorMessage", "No subjects found for you.");
		} 

		m.addAttribute("subjects",allsubjects);
		int rowCount = allsubjects.size();
		m.addAttribute("rowCount", rowCount);


		}catch(Exception e)
		{
			request.setAttribute("error","true");
			request.setAttribute("errorMessage","Error in getting applicable subjects. "+e.getMessage());
			logger.info("Error in deleting applicable subjects. Method:- viewApplicableSubjectsForFaculty",e);
		}
		
		m.addAttribute("currentMonth", month);
		m.addAttribute("currentYear", year);
		m.addAttribute("currentType", type);
		m.addAttribute("monthList", ACAD_MONTH_LIST);
		m.addAttribute("yearList", ACAD_YEAR_LIST);
		m.addAttribute("content", new ContentAcadsBean());
		
		return new ModelAndView("viewApplicableSubjectsForFaculty");
	}

	
	/** Get the Report of MBA-Wx content 
	 * Logic :- Displaying content details  with session details for analysis of delayed content upload  
	 * Start
	 */
	@RequestMapping(value = "/admin/searchTimeBoundContentForm", method = {RequestMethod.GET})
	public ModelAndView searchScheduledContentForm(HttpServletRequest request, HttpServletResponse respnse, Model m) {

		
		String userId = (String)request.getSession().getAttribute("userId_acads");		
		if(!userId.equalsIgnoreCase("NMSCEMUADMIN01")){
			request.setAttribute("error", "true");
			setError(request, "You can not access this page.");
			return new ModelAndView("home");
		}
		
		
		ContentInterface content = contentFactory.getStudentType(StudentType.MBAWX);
		
		ModelAndView modelnView = new ModelAndView("searchTimeBoundContent");
		m.addAttribute("subjectcodes",content.getSubjectCodeLists());
		m.addAttribute("yearList", ACAD_YEAR_LIST);

		m.addAttribute("monthList", ACAD_MONTH_LIST);
		m.addAttribute("contentList",new SearchTimeBoundContent());
		return modelnView;
	}
	
	
	@RequestMapping(value = "/admin/searchTimeBoundContent", method = {RequestMethod.POST})
	public ModelAndView searchScheduledContent(HttpServletRequest request, HttpServletResponse respnse, Model m,@ModelAttribute SearchTimeBoundContent searchBean) {
		
		
		
		List<SearchTimeBoundContent> contentlist = contentservice.scheduledContentList(searchBean.getYear(),searchBean.getMonth(),searchBean.getBatchId(),searchBean.getProgramSemSubjectId(),searchBean.getFacultyId(),searchBean.getDate());
		
		if (contentlist.size() == 0) {
			request.setAttribute("error", "true");
			request.setAttribute("errorMessage", "No Records Found.");
		}
		
		request.getSession().setAttribute("contentData",contentlist);
		m.addAttribute("totalRows", contentlist.size());
		request.getSession().setAttribute("searchBean_acads",searchBean);
		return searchScheduledContentForm(request,respnse,m);
	}
	
	@RequestMapping(value = "/admin/downloadTimeBoundContentReport", method = {RequestMethod.GET})
	public ModelAndView downloadTimeBoundContentReport(HttpServletRequest request, HttpServletResponse respnse, Model m) {
		
		
		SearchTimeBoundContent search =(SearchTimeBoundContent) request.getSession().getAttribute("searchBean_acads");
		List<SearchTimeBoundContent> contentlist = contentservice.scheduledContentList(search.getYear(),search.getMonth(),search.getBatchId(),search.getProgramSemSubjectId(),search.getFacultyId(),search.getDate());
		request.getSession().setAttribute("mapOfFacultyIdAndFacultyRecord", mapOfFacultyIdAndFacultyRecord());
		
		return new ModelAndView("searchTimeBoundContentExcelView","contentlist", contentlist);
	}
	
	
	@RequestMapping(value = "/admin/viewBatchNameBypssId",method = RequestMethod.POST , consumes="application/json", produces="application/json")
	public ResponseEntity viewBatchNameBypssId(@RequestBody ConsumerProgramStructureAcads consumerProgramStructures) {
			List<SearchTimeBoundContent> batchDetails = contentservice.getbatchDetails(consumerProgramStructures.getYear(),consumerProgramStructures.getMonth(),consumerProgramStructures.getProgramSemSubjectId());
		return new ResponseEntity(batchDetails,HttpStatus.OK);
	}
	
	
	@RequestMapping(value = "/admin/viewFacultyIdBypssId",method = RequestMethod.POST , consumes="application/json", produces="application/json")
	public ResponseEntity viewFacultyIdBypssId(@RequestBody  ConsumerProgramStructureAcads searchBean) {
		
		ContentInterface content = contentFactory.getStudentType(StudentType.MBAWX);
		List<String> FacultyDetails = content.getFacultyIdsByPssIds(searchBean.getYear(),searchBean.getMonth(),searchBean.getProgramSemSubjectId());
		return new ResponseEntity(FacultyDetails,HttpStatus.OK); 
	}
	
	/** Get the Report of MBA-Wx content 
	 * Logic :- Displaying content details  with session details for analysis of delayed content upload  
	 * End
	 */


		
		@RequestMapping(value = "/admin/searchContent",  method = {RequestMethod.GET, RequestMethod.POST})
		public ModelAndView searchContent(HttpServletRequest request, HttpServletResponse response, @ModelAttribute ContentAcadsBean searchBean,Model m){
			
			ModelAndView modelnView = new ModelAndView("searchContent");

			
			try {

			request.getSession().setAttribute("searchBean_acads", searchBean);

			
					
			ContentInterface content = contentFactory.getStudentType(StudentType.PG);
			
			String searchType = request.getParameter("searchType") == null ? "distinct" : request.getParameter("searchType");
			
						
			
			request.getSession().setAttribute("searchType", searchType);
			
			
			
			//------------Search Content Strategy Called---------------
			PageAcads<ContentAcadsBean> page = content.searchContent(1,searchBean,searchType);
			
			List<ContentAcadsBean> resourcesContentList = page.getPageItems();
			
		
			modelnView.addObject("page", page);
			modelnView.addObject("searchBean", searchBean);
			modelnView.addObject("rowCount", page.getRowCount());
			modelnView.addObject("yearList", ACAD_YEAR_LIST);
			
			//Commented By Riya as it was not needed
			//modelnView.addObject("monthList", ACAD_MONTH_LIST);
			
			request.getSession().setAttribute("searchBean_acads", searchBean);
			modelnView.addObject("consumerTypeList", getConsumerTypesList());
			m.addAttribute("subject", searchBean.getSubject());
			m.addAttribute("subjectcodes",content.getSubjectCodeLists());
			
			
			if(resourcesContentList.size() == 0){
				request.setAttribute("error", "true");
				request.setAttribute("errorMessage", "No Records found.");
			}
			modelnView.addObject("resourcesContentList", resourcesContentList);
			
			}catch(Exception e)
			{
				request.setAttribute("error", "true");
				request.setAttribute("errorMessage", "Error In Getting Content ."+e.getMessage());
				logger.error("Error In Getting Content by searchScheduledSessionPage . ",e);
			}
			return modelnView;
		}
		
		@RequestMapping(value = "/admin/updateSingleContentMapping",  method = {RequestMethod.GET, RequestMethod.POST})
		public ModelAndView updateSingleContentMapping(HttpServletRequest request,
														 HttpServletResponse response, 
							                             @ModelAttribute ContentAcadsBean contentFromForm,Model m){
			ContentInterface content = contentFactory.getStudentType(StudentType.PG);
			
			try {

			
			String userId = (String)request.getSession().getAttribute("userId_acads");

			
			contentFromForm.setCreatedBy(userId);
			contentFromForm.setLastModifiedBy(userId);
			
			
			//---------------Update Single Content Strategy Called------------
			HashMap<String,String> update_response = content.updateContentSingleSetup(contentFromForm);
			
			
			request.setAttribute("success", update_response.get("success"));
			request.setAttribute("successMessage", update_response.get("successMessage"));

			}catch(Exception e)
			{
				request.setAttribute("error","true");
				request.setAttribute("errorMessage", "Error in updating  content. "+e.getMessage());
				logger.info("Error in updating single content.Method Name:-updateSingleContentMapping",e);
			}
		

			
			ContentAcadsBean searchBean = (ContentAcadsBean)request.getSession().getAttribute("searchBean_acads");
			
			return searchContent(request,response,searchBean,m);

			
		}
		
		
		//Edit Content By Distinct
		@RequestMapping(value = "/admin/editcontentsByDistinct", method = {RequestMethod.GET, RequestMethod.POST})
		public ModelAndView editcontentsByDistinct(HttpServletRequest request, HttpServletResponse respnse, @ModelAttribute ContentAcadsBean content,Model m) {
			ModelAndView modelnView = new ModelAndView("addContent");

			//ContentDAO dao = (ContentDAO)act.getBean("contentDAO");
			content = getContentWithConfigIdsById(content,request.getParameter("id"));
			
			
					
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
			
			//Commented For Now as it was not needed
			//modelnView.addObject("subjectList", getSubjectList());
			
			modelnView.addObject("edit", "true");
			modelnView.addObject("consumerType", getConsumerTypeList());
			modelnView.addObject("programStructureIdNameMap", getProgramStructureIdNameMap());
			modelnView.addObject("programIdNameMap", getProgramIdNameMap());
			modelnView.addObject("editMapping", "true");
			m.addAttribute("consumerProgramStructureId",request.getParameter("consumerProgramStructureId"));
			
			return modelnView;
		}
		
		@RequestMapping(value = "/admin/deleteContentsByDistinct", method = {RequestMethod.GET, RequestMethod.POST})
		public ModelAndView deleteContentsByDistinct(HttpServletRequest request, HttpServletResponse respnse, @ModelAttribute ContentAcadsBean contents,Model m) {
			

			ContentAcadsBean searchBean = (ContentAcadsBean)request.getSession().getAttribute("searchBean_acads");
			try {



			ContentInterface content = contentFactory.getStudentType(ContentFactory.StudentType.PG);
			
			
			
			
			
			

			
			

			String masterKey = request.getParameter("consumerProgramStructureId");
					
			//---------Delete with distinct strategy called----------
			HashMap<String,String> delete_response = content.deleteContentByDistinct(request.getParameter("id"),masterKey);
			 
			request.setAttribute("success", delete_response.get("success"));
			request.setAttribute("successMessage", delete_response.get("successMessage"));
				
			
			request.setAttribute("searchType", request.getSession().getAttribute("searchType"));
			}catch(Exception e)
			{
				
				request.setAttribute("error", "true");
				request.setAttribute("errorMessage", "Error In Deleting Content. Please Contact the developer regarding the error ."+e.getMessage());
				logger.error("Error In Deleting Content By Distinct. ",e);
			}
			
			return searchContent(request,respnse, searchBean,m);  
		}




} 


