package com.nmims.controllers;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.imageio.ImageIO;
import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;

import org.jsoup.helper.StringUtil;

import org.apache.pdfbox.rendering.ImageType;
import org.apache.pdfbox.rendering.PDFRenderer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.commons.CommonsMultipartFile;
import org.springframework.web.servlet.ModelAndView;


import com.nmims.beans.ContentStudentPortalBean;
import com.nmims.beans.FileStudentPortalBean;
import com.nmims.beans.ModuleContentStudentPortalBean;
import com.nmims.beans.ProgramSubjectMappingStudentPortalBean;
import com.nmims.beans.StudentStudentPortalBean;
import com.nmims.beans.VideoContentStudentPortalBean;
import com.nmims.daos.ContentDAO;
import com.nmims.daos.LearningResourcesDAO;
import com.nmims.daos.PortalDao;
import com.nmims.daos.StudentInfoCheckDAO;
import com.nmims.helpers.ExcelHelper;

@Controller
public class LearningResourcesController extends BaseController {
	
	@Autowired(required = false)
	ApplicationContext act;
	private ArrayList<String> subjectList = null;
	Map<String,String> programSubjectSemMap = null;
	List<StudentStudentPortalBean> allSemList = null;
	
	private static final int BUFFER_SIZE = 4096;
	@Value( "${LEARNING_RESOURCES_BASE_PATH}" )
	private String LEARNING_RESOURCES_BASE_PATH;
	
	@ModelAttribute("subjectList")
	public ArrayList<String> getSubjectList() {
		if (this.subjectList == null) {
			ContentDAO dao = (ContentDAO) act.getBean("contentDAO");
			this.subjectList = dao.getActiveSubjects();
		}
		return subjectList;
	}
	
	
	
	
	private ArrayList<ProgramSubjectMappingStudentPortalBean> programSubjectMappingList = null;

	@Value( "${SERVER_PATH}" )
	private String SERVER_PATH;


@RequestMapping(value="/moduleLibraryList",method=RequestMethod.GET)
      public ModelAndView ModuleLibraryList(@RequestParam("moduleId") Integer moduleId, 
                                                              @RequestParam(required=false) String subject,
                                                              @RequestParam(required=false) String view,
                                                                                    HttpServletRequest request,
                                                                                    HttpServletResponse response){
            if(!checkSession(request, response))
            {
                  return new ModelAndView("jsp/studentRedirect");
            }
            /*String subject = request.getParameter("subject");*/
            String userId = (String)request.getSession().getAttribute("userId");
            ModelAndView mv = new ModelAndView("jsp/moduleLibraryList");
            LearningResourcesDAO dao=(LearningResourcesDAO) act.getBean("learningResourcesDAO");
            ModuleContentStudentPortalBean moduleContentBean =  dao.getModuleContentById(moduleId);
            Integer modulePercentage=dao.getModuleDocumentPercentage(userId, moduleId);
            moduleContentBean.setPercentComplete(modulePercentage);
           
            List<ModuleContentStudentPortalBean> moduleDocumnentList =  dao.getModuleDocumentDataById(moduleId);
            List<VideoContentStudentPortalBean> videoTopicsList=dao.getVideoSubTopicsListByModuleId(moduleId);
          
            List<VideoContentStudentPortalBean> VideoContentsList = dao.getAllVideoContentList();
            mv.addObject("VideoContentsList", VideoContentsList);
            request.getSession().setAttribute("VideoContentsList", VideoContentsList);
            mv.addObject("moduleContentBean",moduleContentBean);
            mv.addObject("moduleDocumnentList",moduleDocumnentList);
         
            mv.addObject("SERVER_PATH",SERVER_PATH);
            Integer noOFSeenVideos=dao.getModuleVideoPercentage(userId, moduleId);
            Integer videoPercentage=0;
            if(!videoTopicsList.isEmpty()) {
            videoPercentage=(noOFSeenVideos*100)/videoTopicsList.size();
           
            }
                  
            moduleContentBean.setVideoPercentage(videoPercentage);
           
            //Added to session to be used in viewVideoModuleTopic it will override each time this method is called.
            request.getSession().setAttribute("moduleDocumnentList", moduleDocumnentList);
            request.getSession().setAttribute("videoPercentage", videoPercentage);
            request.getSession().setAttribute("modulePercentage", modulePercentage);
            request.getSession().setAttribute("videoTopicsList", videoTopicsList);
            request.getSession().setAttribute("moduleId", moduleId);
            request.getSession().setAttribute("subject",subject);
           
            mv.addObject("subject",subject);
            mv.addObject("view",view);
            return mv;
      }
      

	
	//viewModuleVideoPage Start
	@RequestMapping(value="/viewModuleVideoPage",method=RequestMethod.GET)
	public ModelAndView viewModuleVideoPage(@RequestParam("moduleId") Integer moduleId, 
			 								@RequestParam("videoSubtopicId") long videoSubtopicId,
			 								@RequestParam("type") String type, 
											HttpServletRequest request,
											HttpServletResponse response){
		if(!checkSession(request, response))
		{
			return new ModelAndView("jsp/studentRedirect");
		}
		
		String userId = (String)request.getSession().getAttribute("userId");
		
		ModelAndView mv = new ModelAndView("jsp/viewModuleVideoPage");
		LearningResourcesDAO dao=(LearningResourcesDAO) act.getBean("learningResourcesDAO");
		ModuleContentStudentPortalBean moduleContentBean =  dao.getModuleContentById(moduleId);
		Integer modulePercentage=dao.getModuleDocumentPercentage(userId, moduleId);
		moduleContentBean.setPercentComplete(modulePercentage);
		Integer videoPercentage=dao.getModuleVideoPercentage(userId, moduleId);
		moduleContentBean.setVideoPercentage(videoPercentage);
	
		List<ModuleContentStudentPortalBean> moduleDocumnentList =  (List<ModuleContentStudentPortalBean>)request.getSession().getAttribute("moduleDocumnentList");
		List<VideoContentStudentPortalBean> videoTopicsList= (List<VideoContentStudentPortalBean>)request.getSession().getAttribute("videoTopicsList");
		VideoContentStudentPortalBean videoToBePlayed=null;
		List<VideoContentStudentPortalBean> relatedTopics =new ArrayList<VideoContentStudentPortalBean>();
		relatedTopics.addAll(videoTopicsList);
		if(videoTopicsList!=null && !videoTopicsList.isEmpty()) {
			for(VideoContentStudentPortalBean  tempVideo : videoTopicsList) {
				
				
				if( (tempVideo.getId().longValue() == videoSubtopicId) && tempVideo.getType().equalsIgnoreCase(type)) {
				
					videoToBePlayed =tempVideo;
					if("Main Video".equalsIgnoreCase(tempVideo.getType())) {
					
						String url=dao.getMobileUrlHd(tempVideo.getId());
						videoToBePlayed.setMobileUrlHd(url);
					}
					
				
				}
			}
			relatedTopics.remove(videoToBePlayed);
		}
		mv.addObject("moduleContentBean",moduleContentBean);
		mv.addObject("moduleDocumnentList",moduleDocumnentList);
		mv.addObject("videoToBePlayed",videoToBePlayed);
		mv.addObject("relatedTopics",relatedTopics);
		
		return mv;

	}
	//viewModuleVideoPage	 End
	
	//viewModulePdfPage Start
		@RequestMapping(value="/viewModulePdfPage",method=RequestMethod.GET)
		public ModelAndView viewModulePdfPage(@RequestParam("moduleId") Integer moduleId, 
											  @RequestParam(required=false) String subject,
											  @RequestParam(required=false) String view,
															HttpServletRequest request,
															HttpServletResponse response){
			if(!checkSession(request, response))
			{
				return new ModelAndView("jsp/studentRedirect");
			}
			/*String subject = request.getParameter("subject");*/
			
			String userId = (String)request.getSession().getAttribute("userId");
			ModelAndView mv = new ModelAndView("jsp/viewModulePdfPages");
			LearningResourcesDAO dao=(LearningResourcesDAO) act.getBean("learningResourcesDAO");
			ModuleContentStudentPortalBean moduleContentBean =  dao.getModuleContentById(moduleId);
			Integer modulePercentage=dao.getModuleDocumentPercentage(userId, moduleId);
			moduleContentBean.setPercentComplete(modulePercentage);
			Integer videoPercentage=dao.getModuleVideoPercentage(userId, moduleId);
			moduleContentBean.setVideoPercentage(videoPercentage);
			
			List<ModuleContentStudentPortalBean> moduleDocumnentList =  dao.getModuleDocumentDataById(moduleId);
			List<VideoContentStudentPortalBean> videoTopicsList=dao.getVideoSubTopicsListByModuleId(moduleId);
		
			VideoContentStudentPortalBean videoToBePlayed=null;
			List<VideoContentStudentPortalBean> relatedTopics =new ArrayList<VideoContentStudentPortalBean>();
			relatedTopics.addAll(videoTopicsList);
			
			if(videoTopicsList!=null && videoTopicsList.size()!=0) {
			 videoToBePlayed = videoTopicsList.get(0);
			 relatedTopics.remove(videoToBePlayed);
			}
			if(moduleDocumnentList!=null && !moduleDocumnentList.isEmpty()) {
				mv.addObject("documentId",moduleDocumnentList.get(0).getId());
			}else {
				mv.addObject("documentId",0);
			}
			mv.addObject("moduleContentBean",moduleContentBean);
			mv.addObject("moduleDocumnentList",moduleDocumnentList);
			mv.addObject("videoToBePlayed",videoToBePlayed);
			mv.addObject("relatedTopics",relatedTopics);
			
			mv.addObject("SERVER_PATH",SERVER_PATH);
			
			//Added to session to be used in viewVideoModuleTopic it will override each time this method is called.
			/*request.getSession().setAttribute("moduleDocumnentList", moduleDocumnentList);
			request.getSession().setAttribute("videoTopicsList", videoTopicsList);
			request.getSession().setAttribute("subject",subject);
			*/
			mv.addObject("subject",subject);
			mv.addObject("view",view);
			return mv;
		}
		//viewModulePdfPage End
	
	@RequestMapping(value = "/uploadLearningResourcesExcelForm", method = { RequestMethod.GET, RequestMethod.POST })
	public ModelAndView uploadLearningResourcesExcelForm(HttpServletRequest request, HttpServletResponse response) {
		/* Uncomment after whole module is complete
		 * if(!checkSession(request, response)){
			return new ModelAndView("jsp/studentPortalRediret");
		}
		*/
		ModelAndView modelAndView = new ModelAndView("jsp/uploadLearningResourcesExcelForm");
		LearningResourcesDAO dao = (LearningResourcesDAO) act.getBean("learningResourcesDAO");
		List<ModuleContentStudentPortalBean> moduleContentsList = dao.getAllModuleContentsList();
		List<ModuleContentStudentPortalBean> downloadCenterContentsList = dao.getAllDownloadCenterContents();
		modelAndView.addObject("fileBean", new FileStudentPortalBean());
		modelAndView.addObject("moduleContentsList", moduleContentsList);
		modelAndView.addObject("downloadCenterContentsList", downloadCenterContentsList);
		modelAndView.addObject("moduleContentBean", new ModuleContentStudentPortalBean());
		modelAndView.addObject("subjectList", getSubjectList());
		
		return modelAndView;
	}
	
	@RequestMapping(value = "/uploadLearningResourcesFiles", method = { RequestMethod.POST })
	public ModelAndView uploadLearningResourcesFiles(HttpServletRequest request,ModuleContentStudentPortalBean moduleContent,	
												HttpServletResponse response,
												@ModelAttribute FileStudentPortalBean fileBean) {

		/* Uncomment later
		 * if(!checkSession(request, response)){
			return new ModelAndView("jsp/studentPortalRediret");
		}*/
	
		ModelAndView modelAndView = new ModelAndView("jsp/uploadLearningResourcesExcelForm");
		modelAndView.addObject("fileBean", fileBean);

		String userId = (String) request.getSession().getAttribute("userId");
		LearningResourcesDAO lrDao = (LearningResourcesDAO) act.getBean("learningResourcesDAO");
		MultipartFile file = fileBean.getFileData();
		if (file.isEmpty()) {// Check if File was attached
			request.setAttribute("error", "true");
			request.setAttribute("errorMessage", "Please Select File to Upload...");

			List<ModuleContentStudentPortalBean> moduleContentsList = lrDao.getAllModuleContentsList();
			modelAndView.addObject("moduleContentsList", moduleContentsList);
			modelAndView.addObject("moduleContent", new ModuleContentStudentPortalBean()); 

			return modelAndView;
		}
		String errorMessage="Error while uploading data caused due to bad data of rows with topic name : ";
		
		try {
			ExcelHelper excelHelper = new ExcelHelper();

			ArrayList<List> resultList = excelHelper.readModuleContentExcel(fileBean, subjectList, userId);
			ArrayList<ModuleContentStudentPortalBean> moduleContentsList = (ArrayList<ModuleContentStudentPortalBean>) resultList.get(0);
			ArrayList<ModuleContentStudentPortalBean> errorBeanList = (ArrayList<ModuleContentStudentPortalBean>) resultList.get(1);

		
			if (errorBeanList.size() > 0) {
			
				request.setAttribute("error", "true");
				for(ModuleContentStudentPortalBean errorBean : errorBeanList) {
					errorMessage = errorMessage +"\n"+errorBean.getModuleName();
					
				}
				
				request.setAttribute("errorMessage", errorMessage+". Please Select File to Upload...");
				request.setAttribute("errorBeanList", errorBeanList);

				modelAndView.addObject("fileBean", fileBean);
				List<ModuleContentStudentPortalBean> moduleContentList = lrDao.getAllModuleContentsList();
				modelAndView.addObject("moduleContentsList", moduleContentsList);
				modelAndView.addObject("moduleContent", new ModuleContentStudentPortalBean());
				
				return modelAndView;
			}

			ArrayList<String> errorList = lrDao.batchUpdateModuleContent(moduleContentsList);

			if (errorList.size() == 0) {
				
				request.setAttribute("success", "true");
				request.setAttribute("successMessage", moduleContentsList.size() + " rows out of "
						+ moduleContentsList.size() + " inserted successfully.");

				List<ModuleContentStudentPortalBean> moduleContentList = lrDao.getAllModuleContentsList();
				modelAndView.addObject("moduleContentsList", moduleContentList);
				modelAndView.addObject("moduleContent", new ModuleContentStudentPortalBean());

			} else {
			
				request.setAttribute("error", "true");
				request.setAttribute("errorMessage", errorList.size()
						+ " records were NOT inserted. Please see row number of rows not inserted. Error row numbers "
						+ errorList);
				List<ModuleContentStudentPortalBean> moduleContentList = lrDao.getAllModuleContentsList();
				modelAndView.addObject("moduleContentsList", moduleContentList);
				modelAndView.addObject("moduleContent", new ModuleContentStudentPortalBean());

			}
		} catch (Exception e) {
			//e.printStackTrace();
			request.setAttribute("error", "true");
			request.setAttribute("errorMessage", "Error in inserting rows.");

			List<ModuleContentStudentPortalBean> moduleContentsList = lrDao.getAllModuleContentsList();
			modelAndView.addObject("moduleContentsList", moduleContentsList);
			modelAndView.addObject("moduleContent", new ModuleContentStudentPortalBean());
		}
		moduleContent.setLastModifiedBy(userId);
		moduleContent.setCreatedBy(userId);
		return modelAndView;
	}
	
	@RequestMapping(value = "/saveModuleContents", method = RequestMethod.POST)
	public ModelAndView saveModuleContent(@ModelAttribute ModuleContentStudentPortalBean moduleContent, HttpServletRequest request,
			HttpServletResponse response) {
		ModelAndView modelAndView = new ModelAndView("jsp/uploadLearningResourcesExcelForm");
		LearningResourcesDAO lrDao = (LearningResourcesDAO) act.getBean("learningResourcesDAO");
		String userId = (String) request.getSession().getAttribute("userId");
		try {
			if ( moduleContent.getId() == null) {
				moduleContent.setCreatedBy(userId);

				long key = lrDao.saveModuleContent(moduleContent);
			
				setSuccess(request, "ModuleContent has been saved successfully");
			} else {
				moduleContent.setLastModifiedBy(userId);
				boolean ModuleContentUpdated = lrDao.updateModuleContent(moduleContent);
				if (ModuleContentUpdated) {
					setSuccess(request, "ModuleContent has been updated successfully");
				}else {
					request.setAttribute("error", "true");
					request.setAttribute("errorMessage", "Module Content Failed to upload. Try Again.");
					
				}
			}
		} catch (Exception e) {
			//e.printStackTrace();
		}
		modelAndView.addObject("fileBean", new FileStudentPortalBean());

		List<ModuleContentStudentPortalBean> moduleContentsList = lrDao.getAllModuleContentsList();
		modelAndView.addObject("moduleContentsList", moduleContentsList);
		modelAndView.addObject("moduleContent", new ModuleContentStudentPortalBean());
		return modelAndView;
	}

	
	@RequestMapping(value = "/editModuleContents", method = RequestMethod.GET)
	public ModelAndView editModuleContent(@RequestParam("id") int id, HttpServletRequest request,
			HttpServletResponse response) {
		ModelAndView modelAndView = new ModelAndView("jsp/moduleContentDetails");
		LearningResourcesDAO lrDao = (LearningResourcesDAO) act.getBean("learningResourcesDAO");
		String userId = (String) request.getSession().getAttribute("userId");
		ModuleContentStudentPortalBean moduleContent = lrDao.getModuleContentById(id);
		List<ModuleContentStudentPortalBean> moduleDocumnentList = new ArrayList<ModuleContentStudentPortalBean>();
		moduleDocumnentList =  lrDao.getModuleDocumentDataById(id);
		modelAndView.addObject("fileBean", new FileStudentPortalBean());
		modelAndView.addObject("moduleDocumnentList", moduleDocumnentList); 
		modelAndView.addObject("moduleDocument", new ModuleContentStudentPortalBean()); 
		if (moduleContent == null) {
			request.setAttribute("error", "true");
			request.setAttribute("errorMessage", "Module Content Not Found");
			modelAndView.addObject("moduleContent", new ModuleContentStudentPortalBean()); 

		} else {
			modelAndView.addObject("moduleContent", moduleContent); 
		}
		
		return modelAndView;
	}

	@RequestMapping(value = "/deleteModuleContents", method = RequestMethod.GET)
	public ModelAndView deleteModuleContent(@RequestParam("id") int id, HttpServletRequest request) {
		ModelAndView modelAndView = new ModelAndView("jsp/uploadLearningResourcesExcelForm");
		LearningResourcesDAO lrDao = (LearningResourcesDAO) act.getBean("learningResourcesDAO");
		
		int deletedRow = lrDao.deleteModuleContent(id);
		if (deletedRow == 0) {
			request.setAttribute("error", "true");
			request.setAttribute("errorMessage", "Error Occured while deleting the Module Content.");
		} else {
			setSuccess(request, "ModuleContent has been deleted successfully");
		}
		modelAndView.addObject("fileBean", new FileStudentPortalBean());

		List<ModuleContentStudentPortalBean> moduleContentsList = lrDao.getAllModuleContentsList();
		modelAndView.addObject("moduleContentsList", moduleContentsList);
		modelAndView.addObject("moduleContent", new ModuleContentStudentPortalBean()); 
		return modelAndView;
	}

	@RequestMapping(value = "/uploadModulesPdf",  method = RequestMethod.POST)
	public ModelAndView uploadModulesPdf(HttpServletRequest request,	
										 HttpServletResponse response,
										 @ModelAttribute FileStudentPortalBean fileBean){

		/*if(!checkSession(request, response)){
			return new ModelAndView("jsp/studentPortalRediret");
		}*/

		ModelAndView modelAndView = new ModelAndView("jsp/moduleContentDetails");
		String userId = (String) request.getSession().getAttribute("userId");
		LearningResourcesDAO lrDao = (LearningResourcesDAO) act.getBean("learningResourcesDAO");
		List<ModuleContentStudentPortalBean> moduleContentsList = lrDao.getAllModuleContentsList();
		modelAndView.addObject("moduleContentsList", moduleContentsList);
		modelAndView.addObject("fileBean", new FileStudentPortalBean());
		
		Integer id = fileBean.getFileId();
		ModuleContentStudentPortalBean moduleContent = lrDao.getModuleContentById(id);
		if (moduleContent == null) {
			request.setAttribute("error", "true");
			request.setAttribute("errorMessage", "Module Content Not Found");
			modelAndView.addObject("moduleContent", new ModuleContentStudentPortalBean()); 
			return modelAndView;
		}
		
		if(fileBean == null || fileBean.getFileData() == null  ){
			request.setAttribute("error", "true");
			request.setAttribute("errorMessage", "Error in file Upload: No File Selected");
			modelAndView.addObject("moduleContent", new ModuleContentStudentPortalBean()); 
			return modelAndView;
		}
		moduleContent.setModuleId(id);
		moduleContent.setCreatedBy(userId);
		moduleContent.setLastModifiedBy(userId);
		String fileName = fileBean.getFileData().getOriginalFilename();  
		if(fileName == null || "".equals(fileName.trim()) ){
			request.setAttribute("error", "true");
			request.setAttribute("errorMessage", "Error in file Upload Inner: No File Selected");
			modelAndView.addObject("moduleContent", new ModuleContentStudentPortalBean()); 
			return modelAndView;
		}
		String error=uploadPDF(fileBean, moduleContent,userId);
		if(error==null) {
			request.setAttribute("success", "true");
			request.setAttribute("successMessage", "PDF saved sucessfully.");
			
		}else {
			
			
			request.setAttribute("error", "true");
			request.setAttribute("errorMessage", "Error in Saving File. Try Again.");
			modelAndView.addObject("moduleContent", moduleContent); 
			return modelAndView;
		}
		moduleContent = lrDao.getModuleContentById(id);
		modelAndView.addObject("moduleContent", moduleContent); 
		return modelAndView;
	}
	


private String uploadPDF(FileStudentPortalBean bean, ModuleContentStudentPortalBean moduleContent, String userId) {

            String errorMessage = null;
            InputStream inputStream = null;   
            OutputStream outputStream = null;
            String subject=moduleContent.getSubject();
            String moduleName=moduleContent.getModuleName();

            CommonsMultipartFile file = bean.getFileData(); 
            String fileName = file.getOriginalFilename();   
           

            /*long fileSizeInBytes = bean.getFileData().getSize();
            if(fileSizeInBytes > MAX_FILE_SIZE){
                  errorMessage = "File size exceeds 5MB. Please upload a file with size less than 1MB";
                  return errorMessage;
            }*/

            //Replace special characters in file name
            subject = subject.replaceAll("'", "_");
            subject = subject.replaceAll(",", "_");
            subject = subject.replaceAll("&", "and");
            subject = subject.replaceAll(" ", "_");
            subject = subject.replaceAll(":", "");
            
            
            moduleName = moduleName.replaceAll("'", "_");
            moduleName = moduleName.replaceAll(",", "_");
            moduleName = moduleName.replaceAll("&", "and");
            moduleName = moduleName.replaceAll(" ", "_");
            moduleName = moduleName.replaceAll(":", "");
            
            
          

            if(!(fileName.toUpperCase().endsWith(".PDF") ) ){
                  errorMessage = "File type not supported. Please upload .pdf file.";
                  return errorMessage;
            }
            fileName = moduleName + ".pdf";
            try {  
                  //PDF stores first 4 letters as %PDF, which can be used to check if a file is actually a pdf file and not just going by extension
                  InputStream tempInputStream = file.getInputStream();  ;
                  byte[] initialbytes = new byte[4];   
                  tempInputStream.read(initialbytes);
                  tempInputStream.close();
                  String fileType = new String(initialbytes);

                  if(!"%PDF".equalsIgnoreCase(fileType)){
                        errorMessage = "File is not a PDF file. Please upload .pdf file.";
                        return errorMessage;
                  }

                  inputStream = file.getInputStream();   
                  String filePath = LEARNING_RESOURCES_BASE_PATH + subject + "/"+moduleContent.getId()+"/"+fileName;
              
                  //Check if Folder exists which is one folder per Exam (Jun2015, Dec2015 etc.) 
                  File folderPath = new File(LEARNING_RESOURCES_BASE_PATH + subject+"/"+moduleContent.getId()+"/");
                  if (!folderPath.exists()) {
                       
                        boolean created = folderPath.mkdirs();
                       
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
                  
                  
                  //code for saving images to directory start
                  File folderPathForImages = new File(LEARNING_RESOURCES_BASE_PATH + subject + "/" + moduleContent.getModuleId());
                  moduleContent.setType(bean.getFileData().getContentType());
                  moduleContent.setFolderPath(LEARNING_RESOURCES_BASE_PATH + subject + "/" + moduleContent.getModuleId());
                  moduleContent.setDocumentName(bean.getFileName());
                  moduleContent.setCreatedBy(userId);
                  moduleContent.setLastModifiedBy(userId);
                  moduleContent.setPreviewPath("/lr/"+subject+"/"+moduleContent.getId()+"/"+fileName);
                  moduleContent.setDocumentPath(LEARNING_RESOURCES_BASE_PATH+subject+"/"+moduleContent.getId()+"/"+fileName);
                  if (!folderPathForImages.exists()) {
                       
                        boolean created = folderPathForImages.mkdirs();
                     
                  }
                  try {
                  // String sourceDir = moduleContentBean.getFilePath() ; // Pdf files are read from this folder
                  File sourceFile = new File(filePath);
                   FileStudentPortalBean fb ;
                   if (sourceFile.exists()) {
                	   
                       PDDocument document = PDDocument.load(sourceFile);
                       PDFRenderer pdfRenderer = new PDFRenderer(document);
                       
                       Integer noOfPages = document.getNumberOfPages();
                       moduleContent.setNoOfPages(noOfPages);           
                       moduleContent.setActive("Y");
                       int pageNumber = 1;
                       
                     //Code to  save docu details in DB start
                        /*LearningResourcesDAO lrDao = (LearningResourcesDAO) act.getBean("learningResourcesDAO");
                        boolean ModuleContentUpdated = lrDao.updateModuleContent(moduleContent);
                        if (!ModuleContentUpdated) {
                              errorMessage = "Module Content Not Updated.";
                              return errorMessage;
                        }*/
                       
                      //changed the folderPath data here as old data was not useful while showing images in slider
                     // moduleContent.setFolderPath("/lr/"+subject+"/"+moduleContent.getModuleId()+"/Module_"+moduleContent.getModuleId()+"_Doc_"+createdDocument+"_Page_"+pageNumber);
                       moduleContent.setFolderPath("lr/"+subject+"/"+moduleContent.getModuleId()+"/Module_"+moduleContent.getModuleId()+"_Doc_");
                       
                        LearningResourcesDAO dao=(LearningResourcesDAO) act.getBean("learningResourcesDAO");
                        long createdDocument = dao.saveModuleDocument(moduleContent);
                       
                        if(createdDocument==0) {
                              errorMessage = "Error in saving document details to dB. Pdf images too not not created  ";
                              return errorMessage;
                        }
                        //Code to  save docu details in DB end
                        
                        int dpi = 600;
                        
                        for (int i = 0; i < noOfPages; ++i) {
                        	
                        	String imageName = "Module_"+moduleContent.getModuleId()+"_Doc_"+createdDocument+"_Page_"+pageNumber;
                           
                            File outPutFile = new File(LEARNING_RESOURCES_BASE_PATH + subject + "/" + moduleContent.getModuleId() + "/"+ imageName +".png");
                              
                            BufferedImage bImage = pdfRenderer.renderImageWithDPI(i, dpi, ImageType.RGB);
//                          BufferedImage image = page.convertToImage();
//                          File outputfile = new File(LEARNING_RESOURCES_BASE_PATH + subject + "/" + moduleContent.getModuleId() + "/"+ imageName +".png");
                           
                            ImageIO.write(bImage, "png", outPutFile);
                            pageNumber++;
                           
                        }

                       outputStream.close();
                              inputStream.close(); 

                           pageNumber++;
                       
                       document.close();
                   }
                        else {
                       System.err.println(sourceFile.getName() +" File not exists");
                   }

               } catch (Exception e) {
                   //e.printStackTrace();
               }
                  
                  //code for saving images to directory end
                  
                  
            } catch (IOException e) {   
                  errorMessage = "Error in uploading file for "+bean.getFileId();
                  //e.printStackTrace();   
            }   
            
            return errorMessage;
      }
     




 	
 	//Code for modulevideos admin view start
 	@RequestMapping(value = "/editModuleVideoContents", method = RequestMethod.GET)
	public ModelAndView editModuleVideoContents(@RequestParam("moduleId") int moduleId, HttpServletRequest request,
			HttpServletResponse response) {
		ModelAndView modelAndView = new ModelAndView();
		LearningResourcesDAO lrDao = (LearningResourcesDAO) act.getBean("learningResourcesDAO");
		
		ModuleContentStudentPortalBean moduleContent = lrDao.getModuleContentById(moduleId);
		if (moduleContent == null) {
			modelAndView.addObject("fileBean", new FileStudentPortalBean());
			request.setAttribute("error", "true");
			request.setAttribute("errorMessage", "Module Content Not Found");
			modelAndView.addObject("moduleContent", new ModuleContentStudentPortalBean()); 
			modelAndView.setViewName("moduleContentDetails");
			List<ModuleContentStudentPortalBean> moduleContentsList = lrDao.getAllModuleContentsList();
			modelAndView.addObject("moduleContentsList", moduleContentsList);
			
			return modelAndView;
		} else {
			modelAndView.setViewName("editModuleVideoContents");
			modelAndView.addObject("moduleContent", moduleContent); 
			ArrayList<VideoContentStudentPortalBean> videoTopicsList = (ArrayList<VideoContentStudentPortalBean>)lrDao.getVideoSubTopicsListBySubject(moduleContent.getSubject());
			ArrayList<VideoContentStudentPortalBean> mainVideosList = (ArrayList<VideoContentStudentPortalBean>)lrDao.getMainVideoListBySubject(moduleContent.getSubject());
			modelAndView.addObject("videoTopicsList", videoTopicsList);
			modelAndView.addObject("mainVideosList", mainVideosList);
			List<VideoContentStudentPortalBean> mapped=new ArrayList<VideoContentStudentPortalBean>();
			List<VideoContentStudentPortalBean> unMapped=new ArrayList<VideoContentStudentPortalBean>();
			List<ModuleContentStudentPortalBean> moduleVideoMapeedData = lrDao.getModuleVideDataById(moduleId);
			modelAndView.addObject("moduleVideoMapeedData", moduleVideoMapeedData);
			
			for(VideoContentStudentPortalBean topicVideo : videoTopicsList){
				
				topicVideo.setType("Topic Video");
				for(ModuleContentStudentPortalBean moduleVideo : moduleVideoMapeedData){
					
					if( (topicVideo.getId().intValue() == moduleVideo.getVideoSubtopicId().intValue()) && (moduleVideo.getType().equalsIgnoreCase(topicVideo.getType())) ) {
						
							mapped.add(topicVideo);
					}
					
				}
			}
			for(VideoContentStudentPortalBean mainVideo : mainVideosList){
			
				mainVideo.setType("Main Video");
				for(ModuleContentStudentPortalBean moduleVideo2 : moduleVideoMapeedData){
					
					if( (mainVideo.getId().intValue() == moduleVideo2.getVideoSubtopicId().intValue()) && (moduleVideo2.getType().equalsIgnoreCase(mainVideo.getType())) ) {
						
							mapped.add(mainVideo);
					}
					
				}
			}
			unMapped.addAll(mainVideosList);
			unMapped.addAll(videoTopicsList);
			unMapped.removeAll(mapped);
			
			modelAndView.addObject("mapped", mapped);
			modelAndView.addObject("unmapped", unMapped);
			
			return modelAndView;
			
		}
	}

	//Start 
 	@RequestMapping(value = "/mapModuleVideo", method = RequestMethod.GET)
	public ModelAndView mapModuleVideo(@RequestParam("moduleId") int moduleId,
										@RequestParam("videoId") int videoId,
										@RequestParam("type") String type,
										HttpServletRequest request,
										HttpServletResponse response) {
		ModelAndView modelAndView = new ModelAndView();
		LearningResourcesDAO lrDao = (LearningResourcesDAO) act.getBean("learningResourcesDAO");
		
		ModuleContentStudentPortalBean moduleContent = lrDao.getModuleContentById(moduleId);
		if (moduleContent == null) {
			modelAndView.addObject("fileBean", new FileStudentPortalBean());
			request.setAttribute("error", "true");
			request.setAttribute("errorMessage", "Module Content Not Found");
			modelAndView.addObject("moduleContent", new ModuleContentStudentPortalBean()); 
			modelAndView.setViewName("moduleContentDetails");
			List<ModuleContentStudentPortalBean> moduleContentsList = lrDao.getAllModuleContentsList();
			modelAndView.addObject("moduleContentsList", moduleContentsList);
			
		} else {
			modelAndView.setViewName("editModuleVideoContents");
			modelAndView.addObject("moduleContent", moduleContent); 
			List<VideoContentStudentPortalBean> videoTopicsList = lrDao.getVideoSubTopicsListBySubject(moduleContent.getSubject());
			modelAndView.addObject("videoTopicsList", videoTopicsList);
			List<ModuleContentStudentPortalBean> moduleVideoMapeedData = lrDao.getModuleVideDataById(moduleId);
			modelAndView.addObject("moduleVideoMapeedData", moduleVideoMapeedData);
			
			moduleContent.setVideoSubtopicId(videoId);
			moduleContent.setType(type);
		
			long mapedId = lrDao.mapModuleVideo(moduleContent);
			if(mapedId==0) {
				request.setAttribute("error", "true");
				request.setAttribute("errorMessage", "Error While Saving Mapping Data");
				
			}
			request.setAttribute("success", "true");
			request.setAttribute("successMessage","Module and Video Mapped Successfully ");
			return editModuleVideoContents(moduleId,request,  response);
		}
		return modelAndView;
		
	}
 	//end
 	
	//Start 
 	@RequestMapping(value = "/deleteModuleVideoMap", method = RequestMethod.GET)
	public ModelAndView deleteModuleVideoMap(@RequestParam("moduleId") int moduleId,
										@RequestParam("videoId") int videoId,
										@RequestParam("type") String type,
										HttpServletRequest request,
										HttpServletResponse response) {
		ModelAndView modelAndView = new ModelAndView();
		LearningResourcesDAO lrDao = (LearningResourcesDAO) act.getBean("learningResourcesDAO");
		
		ModuleContentStudentPortalBean moduleContent = lrDao.getModuleContentById(moduleId);
		if (moduleContent == null) {
			modelAndView.addObject("fileBean", new FileStudentPortalBean());
			request.setAttribute("error", "true");
			request.setAttribute("errorMessage", "Module Content Not Found");
			modelAndView.addObject("moduleContent", new ModuleContentStudentPortalBean()); 
			modelAndView.setViewName("moduleContentDetails");
			List<ModuleContentStudentPortalBean> moduleContentsList = lrDao.getAllModuleContentsList();
			modelAndView.addObject("moduleContentsList", moduleContentsList);
			
		} else {
			modelAndView.setViewName("editModuleVideoContents");
			modelAndView.addObject("moduleContent", moduleContent); 
			List<VideoContentStudentPortalBean> videoTopicsList = lrDao.getVideoSubTopicsListBySubject(moduleContent.getSubject());
			modelAndView.addObject("videoTopicsList", videoTopicsList);
			List<ModuleContentStudentPortalBean> moduleVideoMapeedData = lrDao.getModuleVideDataById(moduleId);
			modelAndView.addObject("moduleVideoMapeedData", moduleVideoMapeedData);
			
			moduleContent.setVideoSubtopicId(videoId);
			moduleContent.setType(type);
			
			int mapedId = lrDao.deleteModuleVideoMap(moduleId, videoId, type);
			if(mapedId==0) {
				request.setAttribute("error", "true");
				request.setAttribute("errorMessage", "Error While Deleting Data ");
				
			}
			request.setAttribute("success", "true");
			request.setAttribute("successMessage","Module and Vidoe Mapping Deleted Successfully ");
			return editModuleVideoContents(moduleId,request,  response);
			
		}
		return modelAndView;
		
	}
 	//end
 	
 	//Code for modulevideo admin view end
 
	@RequestMapping(value = "/learningModule", method = RequestMethod.GET)
	public String learningModule(Model m, HttpServletRequest request, HttpServletResponse respnse) {
		if (!checkSession(request, respnse)) {
			return "studentPortalRedirect";
		}
		ArrayList<String> allsubjects = applicableSubjectsForStudent(request);
		String s1 = "";
		HashMap<String, List<ModuleContentStudentPortalBean>> listOfContent = new HashMap<String, List<ModuleContentStudentPortalBean>>();
		ModuleContentStudentPortalBean moduleContentBean = new ModuleContentStudentPortalBean();
		List<ModuleContentStudentPortalBean> getContentList = new ArrayList<ModuleContentStudentPortalBean>();
		String userId = (String) request.getSession().getAttribute("userId");
		LearningResourcesDAO dao = (LearningResourcesDAO) act.getBean("learningResourcesDAO");
		
		List<String> getSubjectList = dao.getSubjectList(userId);
	
		Integer percentageOverall=0;
		List<ModuleContentStudentPortalBean> moduleDocumnentList=null;
		List<String> listOfPercentage=new ArrayList<>();
		List<Integer> listOfModuleDocumentsCount=new ArrayList<>();
		List<Integer> listOfModuleVideosCount=new ArrayList<>();
		for (int i = 0; i < allsubjects.size(); i++) {
			s1 = allsubjects.get(i);
			
			ArrayList<String> tempSubject = new ArrayList<String>();
			tempSubject.add(s1);
			getContentList = dao.getContentList(tempSubject);
			//tempSubject = null;
			
			int contentCount = getContentList != null ? getContentList.size() : 0;
			if (!(contentCount == 0)) {
				listOfContent.put(s1, getContentList);
				
				for (ModuleContentStudentPortalBean bean : getContentList) {
					 moduleDocumnentList = dao.getModuleDocumentDataById( bean.getId());
					Integer percentageForDoc=dao.getModuleDocumentPercentage(userId, bean.getId());
				
					Integer percentageForVideo=dao.getModuleVideoPercentage(userId, bean.getId());
				
					Integer n=dao.getModuleProgressCount(userId, bean.getId());
				
					Integer noOfModuleDocuments=dao.getModuleDocumentsCount(bean.getId());
					Integer noOfModuleVideos=dao.getModuleVideosCount(bean.getId());
					
					if(n!=0) {
						
						percentageOverall=(percentageForDoc+percentageForVideo)/n;
						
					}
					else {
						 percentageOverall=(percentageForDoc+percentageForVideo);
						
					}
					listOfModuleDocumentsCount.add(noOfModuleDocuments);
					listOfModuleVideosCount.add(noOfModuleVideos);
					listOfPercentage.add((Double.toString(percentageOverall)));
					//uncomment after testing
					ArrayList<Integer> allDocPercentage = new ArrayList<Integer>();
					for (ModuleContentStudentPortalBean docBean : moduleDocumnentList) {
						int percentage;
						
						allDocPercentage.add(docBean.getPercentComplete());
					}
					if(noOfModuleDocuments!=0) {
						moduleContentBean.setNoOfModuleDocuments(noOfModuleDocuments); }
						else {
						moduleContentBean.setNoOfModuleDocuments(0);	}
					
					if(noOfModuleVideos!=0) {
						moduleContentBean.setNoOfModuleVideos(noOfModuleVideos); }
						else {
						moduleContentBean.setNoOfModuleVideos(0);	}
					
					int sum = 0;
					int total = allDocPercentage.size();
					for (Integer docBean : allDocPercentage) {

						sum = sum + docBean;
					}

					Double overallModulePercentage = (double) sum / total;
					float tempFloat = percentageOverall.floatValue();
					int tempInt = Math.round(tempFloat);

					if (total != 0) {
						bean.setPercentageCombined(percentageOverall);
						bean.setPercentComplete(tempInt);
					} else {
						bean.setPercentage(0);
						bean.setPercentageCombined(0);
					}
					
				
				}
			}
		}
		m.addAttribute("moduleDocumnentList",moduleDocumnentList);
		m.addAttribute("listOfContent", listOfContent);
		m.addAttribute("moduleContentBean", moduleContentBean);
		return "jsp/learningModule";
	}

	
	
	@RequestMapping(value="/viewVideoModuleTopic",method=RequestMethod.GET)
	public ModelAndView viewVideoModuleTopic(@RequestParam("moduleId") Integer moduleId, 
											 @RequestParam("videoSubtopicId") long videoSubtopicId,
											 @RequestParam("type") String type, 
											 HttpServletRequest request,
											 HttpServletResponse response){
		if(!checkSession(request, response))
		{
			return new ModelAndView("jsp/studentRedirect");
		}
	
		String userId = (String)request.getSession().getAttribute("userId");
		
		ModelAndView mv = new ModelAndView("jsp/viewVideoModuleTopic");
		LearningResourcesDAO dao=(LearningResourcesDAO) act.getBean("learningResourcesDAO");
		ModuleContentStudentPortalBean moduleContentBean =  dao.getModuleContentById(moduleId);
		Integer modulePercentage=dao.getModuleDocumentPercentage(userId, moduleId);
		moduleContentBean.setPercentComplete(modulePercentage);
		
	
		
		List<ModuleContentStudentPortalBean> moduleDocumnentList =  (List<ModuleContentStudentPortalBean>)request.getSession().getAttribute("moduleDocumnentList");
		List<VideoContentStudentPortalBean> videoTopicsList= (List<VideoContentStudentPortalBean>)request.getSession().getAttribute("videoTopicsList");
		VideoContentStudentPortalBean videoToBePlayed=null;
		List<VideoContentStudentPortalBean> relatedTopics =new ArrayList<VideoContentStudentPortalBean>();
		relatedTopics.addAll(videoTopicsList);
		if(videoTopicsList!=null && videoTopicsList.size()!=0) {
			for(VideoContentStudentPortalBean  tempVideo : videoTopicsList) {
			
				if( (tempVideo.getId().longValue() == videoSubtopicId) && tempVideo.getType().equalsIgnoreCase(type)) {
				
					videoToBePlayed =tempVideo;
				
				}
			}
			relatedTopics.remove(videoToBePlayed);
		}

		mv.addObject("moduleContentBean",moduleContentBean);
		mv.addObject("moduleDocumnentList",moduleDocumnentList);
		mv.addObject("videoToBePlayed",videoToBePlayed);
		mv.addObject("relatedTopics",relatedTopics);
		Integer videoPercent=dao.getModuleVideoPercentage(userId, moduleId);
		Integer videoPercentage=(videoPercent/videoTopicsList.size());
		moduleContentBean.setVideoPercentage(videoPercentage);
		
		return mv;
	}
	
	
	
	/*@RequestMapping(value="/viewPdfModule",method={RequestMethod.GET,RequestMethod.POST})
	public ModelAndView viewPdfModule(@RequestParam("moduleId") Integer moduleId,
														Model m, 
														HttpServletRequest request,
														HttpServletResponse response){
		if(!checkSession(request, response))
		{
			return new ModelAndView("jsp/studentRedirect");
		}
		
		ModelAndView mv = new ModelAndView("jsp/viewPdfModule");
		LearningResourcesDAO dao=(LearningResourcesDAO) act.getBean("learningResourcesDAO");
		ModuleContentBean moduleContentBean =  dao.getModuleContentById(moduleId);
		ArrayList<String> images= new ArrayList<String>();
        try {
        String sourceDir = moduleContentBean.getFilePath() ; // Pdf files are read from this folder
        File sourceFile = new File(sourceDir);
        FileBean fb ;
        if (sourceFile.exists()) {
            PDDocument document = PDDocument.load(sourceDir);
            List<PDPage> list = document.getDocumentCatalog().getAllPages();

            String fileName = sourceFile.getName().replace(".pdf", "");             
            int pageNumber = 1;
            for (PDPage page : list) {
                BufferedImage image = page.convertToImage();
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                ImageIO.write( image, "jpg", baos );
                baos.flush();
                byte[] imageInByte = baos.toByteArray();
                baos.close();
                fb=new FileBean();
                pageNumber++;
                byte[] encodeBase64 = Base64.getEncoder().encode(imageInByte);
                String base64Encoded = new String(encodeBase64, "UTF-8");
                images.add(base64Encoded);
            }
            document.close();
            } else {
          
        }

    } catch (Exception e) {
        e.printStackTrace();
    }
		
		mv.addObject("images",images);
		mv.addObject("moduleContentBean",moduleContentBean);
		return mv;
	}
	*/

	@RequestMapping(value="/viewVideoModule",method=RequestMethod.GET)
	public ModelAndView viewVideoModule(@RequestParam("moduleId") Integer moduleId,
														HttpServletRequest request,
														HttpServletResponse response){
		if(!checkSession(request, response))
		{
			return new ModelAndView("jsp/studentRedirect");
		}
		
		ModelAndView mv = new ModelAndView("jsp/viewVideoModuleTopic");
		LearningResourcesDAO dao=(LearningResourcesDAO) act.getBean("learningResourcesDAO");
		ModuleContentStudentPortalBean moduleContentBean =  dao.getModuleContentById(moduleId);
		mv.addObject("moduleContentBean",moduleContentBean);
		return mv;
	}

	//coded to get applicabel subjects start 
	public ArrayList<String> applicableSubjectsForStudent(HttpServletRequest request) {
		ArrayList<ProgramSubjectMappingStudentPortalBean> failSubjectsBeans = new ArrayList<>();
		ArrayList<ProgramSubjectMappingStudentPortalBean> allsubjects = new ArrayList<>();
		
		ArrayList<ProgramSubjectMappingStudentPortalBean> unAttemptedSubjectsBeans = new ArrayList<>();
		
		String sapId = (String)request.getSession().getAttribute("userId");
		//So admins/faculty would see Videos Page with all videos 
		if(!sapId.startsWith("7")) {
			request.getSession().setAttribute("applicableSubjects", subjectList);
			return subjectList;
		}
		ContentDAO cdao = (ContentDAO)act.getBean("contentDAO");
		StudentStudentPortalBean student = (StudentStudentPortalBean)request.getSession().getAttribute("student_studentportal");
		StudentStudentPortalBean studentRegistrationData = cdao.getStudentRegistrationData(sapId);


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
			//student.setPrgmStructApplicable(studentRegistrationData.getPrgmStructApplicable());
			//student.setWaivedOffSubjects(studentRegistrationData.getWaivedOffSubjects());
			ArrayList<ProgramSubjectMappingStudentPortalBean> currentSemSubjects = getSubjectsForStudent(student);
			if(currentSemSubjects != null && currentSemSubjects.size() > 0){
				allsubjects.addAll(currentSemSubjects);
				request.getSession().setAttribute("currentSemSubjects_studentportal", currentSemSubjects);
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
		unAttemptedSubjectsBeans = cdao.getUnAttemptedSubjects(sapId);
		if(unAttemptedSubjectsBeans != null && unAttemptedSubjectsBeans.size() > 0){
			allsubjects.addAll(unAttemptedSubjectsBeans);
		}


		//Sort all subjects semester wise.
		Collections.sort(allsubjects);
		
		if(allsubjects.size() == 0){
			request.setAttribute("error", "true");
			request.setAttribute("errorMessage", "No subjects found for you."); 
		}
		ArrayList<String> applicableSubjects=new ArrayList<>();
		for(ProgramSubjectMappingStudentPortalBean psmb:allsubjects){


			applicableSubjects.add(psmb.getSubject());
		}
		
		request.getSession().setAttribute("failSubjectsBeans", failSubjectsBeans);
		request.getSession().setAttribute("applicableSubjects", applicableSubjects);
		return applicableSubjects;
				
	}
	private ArrayList<ProgramSubjectMappingStudentPortalBean> getFailSubjects(StudentStudentPortalBean student) {
		ContentDAO dao = (ContentDAO)act.getBean("contentDAO");
		ArrayList<ProgramSubjectMappingStudentPortalBean> failSubjectList;
		try {
			failSubjectList = dao.getFailSubjectsForAStudent(student.getSapid());
		} catch (Exception e) {
			failSubjectList=new ArrayList<ProgramSubjectMappingStudentPortalBean>();
			//e.printStackTrace();
		}
		return failSubjectList;
	}
	private ArrayList<ProgramSubjectMappingStudentPortalBean> getSubjectsForStudent(StudentStudentPortalBean student) {
		ArrayList<ProgramSubjectMappingStudentPortalBean> programSubjectMappingList = getProgramSubjectMappingList();
		ArrayList<ProgramSubjectMappingStudentPortalBean> subjects = new ArrayList<>();
		for (int i = 0; i < programSubjectMappingList.size(); i++) {
			ProgramSubjectMappingStudentPortalBean bean = programSubjectMappingList.get(i);

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
	public ArrayList<ProgramSubjectMappingStudentPortalBean> getProgramSubjectMappingList(){
		if(this.programSubjectMappingList == null || this.programSubjectMappingList.size() == 0){
			ContentDAO dao = (ContentDAO)act.getBean("contentDAO");
			this.programSubjectMappingList = dao.getProgramSubjectMappingList();
		}
		return programSubjectMappingList;
	} 
	
	//CRUD For Module Document Start
	
	@RequestMapping(value = "/sMD", method = RequestMethod.POST)
	public ModelAndView saveModuleDocuments(@ModelAttribute ModuleContentStudentPortalBean moduleDocument, HttpServletRequest request,
			HttpServletResponse response) {
		ModelAndView modelAndView = new ModelAndView("jsp/moduleContentDetails");
		LearningResourcesDAO lrDao = (LearningResourcesDAO) act.getBean("learningResourcesDAO");
		Integer id= moduleDocument.getModuleId();
		String userId = (String)request.getSession().getAttribute("userId");
		ModuleContentStudentPortalBean moduleContent = lrDao.getModuleContentById(id);
		if (moduleContent == null) {
			request.setAttribute("error", "true");
			request.setAttribute("errorMessage", "Module Content Not Found");
			modelAndView.addObject("moduleContent", new ModuleContentStudentPortalBean()); 

		} else {
		try {
			if ( moduleDocument.getId()==null) {
				moduleDocument.setCreatedBy(userId);

				long key = lrDao.saveModuleDocument(moduleDocument);
				
				setSuccess(request, "Module Document has been saved successfully");
			} else {
				moduleDocument.setLastModifiedBy(userId);
				
				boolean ModuleContentUpdated = lrDao.updateModuleDocuments(moduleDocument);
				if (ModuleContentUpdated) {
					setSuccess(request, "Module Document has been updated successfully");
				}else {
					request.setAttribute("error", "true");
					request.setAttribute("errorMessage", "Module Document Failed to upload. Try Again.");
					
				}
			}
		} catch (Exception e) {
			//e.printStackTrace();
		}
		}
		List<ModuleContentStudentPortalBean> moduleDocumnentList = new ArrayList<ModuleContentStudentPortalBean>();
	//	moduleDocumnentList =  lrDao.getModuleDocumentDataById(userId,id);
		modelAndView.addObject("fileBean", new FileStudentPortalBean());
		modelAndView.addObject("moduleDocumnentList", moduleDocumnentList); 
		modelAndView.addObject("moduleDocument", new ModuleContentStudentPortalBean()); 
		modelAndView.addObject("moduleContent", moduleContent); 
		
		return modelAndView;
	}

	
	@RequestMapping(value = "/deleteModuleDocument", method = RequestMethod.GET)
	public ModelAndView deleteModuleDocument(@RequestParam("id") int id, HttpServletRequest request) {
		ModelAndView modelAndView = new ModelAndView("jsp/moduleContentDetails");
		LearningResourcesDAO lrDao = (LearningResourcesDAO) act.getBean("learningResourcesDAO");
		String userId = (String) request.getSession().getAttribute("userId");
		ModuleContentStudentPortalBean moduleDocument = lrDao.getModuleDocumentById(id);

		ModuleContentStudentPortalBean moduleContent = lrDao.getModuleContentById(moduleDocument.getModuleId());	
		List<ModuleContentStudentPortalBean> moduleDocumnentList = new ArrayList<ModuleContentStudentPortalBean>();
		//moduleDocumnentList =  lrDao.getModuleDocumentDataById(userId,moduleDocument.getModuleId());
		modelAndView.addObject("fileBean", new FileStudentPortalBean());
		modelAndView.addObject("moduleDocumnentList", moduleDocumnentList); 
		modelAndView.addObject("moduleDocument", new ModuleContentStudentPortalBean()); 
		modelAndView.addObject("moduleContent", moduleContent); 
		if (moduleContent == null) {
			request.setAttribute("error", "true");
			request.setAttribute("errorMessage", "Module Document Not Found");
			modelAndView.addObject("moduleContent", new ModuleContentStudentPortalBean()); 

		} else {

		int deletedRow = lrDao.deleteModuleDocumentsContent(id);
		if (deletedRow == 0) {
			request.setAttribute("error", "true");
			request.setAttribute("errorMessage", "Error Occured while deleting the Module Document.");
		} else {
			setSuccess(request, "ModuleDocument has been deleted successfully");
		}
		}
		return modelAndView;
		
	}
	//CRUD for module document end
	
	//REST API Start
	//pageViewed Start
	@RequestMapping(value = "/pageViewed", method = RequestMethod.GET)
	public ResponseEntity<String> pageViewed(@RequestParam("moduleId") Integer moduleId,
											@RequestParam("documentId") Integer documentId,
											@RequestParam("pageNo") Integer pageNo,
											@RequestParam(required=false) Double percentage,
											HttpServletRequest request,HttpServletResponse response) {

	
		
		String sapId = (String) request.getSession().getAttribute("userId");
		
		HttpHeaders headers = new HttpHeaders();
		if (moduleId != null && documentId != null && pageNo != null) {
			LearningResourcesDAO lrDao = (LearningResourcesDAO) act.getBean("learningResourcesDAO");
			ModuleContentStudentPortalBean moduleContent = lrDao.getModuleContentById(moduleId);	
			Integer noOfPage=lrDao.getNoOfPages(moduleId,documentId);
			
			percentage=((double)pageNo/(double)noOfPage)*100;
			float tempFloat = percentage.floatValue();
			int tempInt= Math.round(tempFloat);
			
		
			String subject=moduleContent.getSubject();
			boolean isSuccess = lrDao.updatePageViewedNo(sapId,subject,moduleId,documentId,pageNo,tempInt);
			if(isSuccess) {
				
				return new ResponseEntity<String>("success", headers, HttpStatus.OK);
			}
		}
		
		return new ResponseEntity<String>("error", headers, HttpStatus.BAD_REQUEST);
	}
	//pageViewed End
	
	//videoViewed Start
		@RequestMapping(value = "/videoViewed", method = RequestMethod.GET)
		public ResponseEntity<String> videoViewed(@RequestParam("moduleId") Integer moduleId,
												@RequestParam("videoTopicId") Integer videoTopicId, 
												@RequestParam("type") String type, 
												HttpServletRequest request,HttpServletResponse response) {

			
			
			String sapId = (String) request.getSession().getAttribute("userId");
			HttpHeaders headers = new HttpHeaders();
			if (moduleId != null && videoTopicId != null && type!=null && !"".equals(type) ) {
				LearningResourcesDAO lrDao = (LearningResourcesDAO) act.getBean("learningResourcesDAO");
				ModuleContentStudentPortalBean moduleContent = lrDao.getModuleContentById(moduleId);	
				
				ModuleContentStudentPortalBean moduleVideoContent = lrDao.getModuleVideDataByTopicIdAndType(moduleId,videoTopicId,type);	
				
				String subject=moduleContent.getSubject();
				boolean isSuccess = lrDao.updateVideoViewed(sapId,subject,moduleId,videoTopicId,type);
				if(isSuccess) {
				
					return new ResponseEntity<String>("success", headers, HttpStatus.OK);
				}
			}
			return new ResponseEntity<String>("error", headers, HttpStatus.BAD_REQUEST);
		}
		//videoViewed End
		
	//REST API End
	
	
	//download file added on 5/3/2018
	@RequestMapping(value = "/downloadDocument", method = {RequestMethod.GET, RequestMethod.POST})
	public ModelAndView downloadDocument(HttpServletRequest request, HttpServletResponse response ){
		ModelAndView modelnView = new ModelAndView("jsp/downloadDocument");

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
			//e.printStackTrace();
			request.setAttribute("error", "true");
			request.setAttribute("errorMessage", "Error in downloading file.");
		}
		return modelnView;
	}
	
	
	//download center
	@RequestMapping(value = "/saveDownloadCenterContents", method = RequestMethod.POST)
	public ModelAndView saveDownloadCenterContents(@ModelAttribute ModuleContentStudentPortalBean moduleContent, HttpServletRequest request,
			HttpServletResponse response,@RequestParam(required=false) String subject) {
		ModelAndView modelAndView = new ModelAndView("jsp/uploadLearningResourcesExcelForm");
		LearningResourcesDAO lrDao = (LearningResourcesDAO) act.getBean("learningResourcesDAO");
		String userId = (String) request.getSession().getAttribute("userId");
		try {
			if ( moduleContent.getId() == null) {
				moduleContent.setCreatedBy(userId);

				long key = lrDao.saveModuleContent(moduleContent);
				setSuccess(request, "ModuleContent has been saved successfully");
			} else {
				moduleContent.setLastModifiedBy(userId);
				boolean ModuleContentUpdated = lrDao.updateModuleContent(moduleContent);
				if (ModuleContentUpdated) {
					setSuccess(request, "ModuleContent has been updated successfully");
				}else {
					request.setAttribute("error", "true");
					request.setAttribute("errorMessage", "Module Content Failed to upload. Try Again.");
					
				}
			}
		} catch (Exception e) {
			//e.printStackTrace();
		}
		modelAndView.addObject("fileBean", new FileStudentPortalBean());

		List<ModuleContentStudentPortalBean> moduleContentsList = lrDao.getAllModuleContentsList();
		modelAndView.addObject("moduleContentsList", moduleContentsList);
		modelAndView.addObject("moduleContent", new ModuleContentStudentPortalBean());
		return modelAndView;
	}
	
	
	
	@RequestMapping(value = "/uploadDownloadModulesPdf",  method = RequestMethod.POST)
	public ModelAndView uploadDownloadModulesPdf(HttpServletRequest request,	
										 		HttpServletResponse response,
										 		@RequestParam(required=false) String subject,
										 		@ModelAttribute FileStudentPortalBean fileBean) throws IOException{
		/*if(!checkSession(request, response)){
			return new ModelAndView("jsp/studentPortalRediret");
		}*/
		String userId = (String) request.getSession().getAttribute("userId");
		
		CommonsMultipartFile file = fileBean.getFileData();
		Integer id = fileBean.getFileId();
		if (file==null) {// Check if File was attached
			request.setAttribute("error", "true");
			request.setAttribute("errorMessage", "Please Select File to Upload...");
			return uploadLearningResourcesExcelForm(request,response) ;
		}
		
		String fileName = fileBean.getFileData().getOriginalFilename();
		
		ModuleContentStudentPortalBean moduleContent= new ModuleContentStudentPortalBean();
		moduleContent.setSubject(subject);
		moduleContent.setFolderPath(LEARNING_RESOURCES_BASE_PATH+"Download_Center"+"/"+subject+"/"+fileName);
		moduleContent.setFileName(fileName);
		moduleContent.setDescription(fileBean.getDescription());
		moduleContent.setCreatedBy(userId);
		moduleContent.setLastModifiedBy(userId);
		moduleContent.setYear(Integer.parseInt(fileBean.getYear()));
		moduleContent.setMonth(fileBean.getMonth());
		
		String error=uploadDownloadPDF(fileBean,moduleContent);
		if(error==null) {
			request.setAttribute("success", "true");
			request.setAttribute("successMessage", "File saved sucessfully.");
			
		}else {
			request.setAttribute("error", "true");
			request.setAttribute("errorMessage", "Error in Saving File. \n"+error);
		}
		
		
		return uploadLearningResourcesExcelForm(request,response) ;
		
	}
	
	
	
	
private String uploadDownloadPDF(FileStudentPortalBean bean, ModuleContentStudentPortalBean moduleContent) {

	LearningResourcesDAO lrDao = (LearningResourcesDAO) act.getBean("learningResourcesDAO");
	String errorMessage = null;
    InputStream inputStream = null;   
    OutputStream outputStream = null;
    String subject=moduleContent.getSubject();
    CommonsMultipartFile file = bean.getFileData(); 
    String fileName = file.getOriginalFilename();   

    /*long fileSizeInBytes = bean.getFileData().getSize();
    if(fileSizeInBytes > MAX_FILE_SIZE){
          errorMessage = "File size exceeds 5MB. Please upload a file with size less than 1MB";
          return errorMessage;
    }*/

    //Replace special characters in file name
    subject = subject.replaceAll("'", "_");
    subject = subject.replaceAll(",", "_");
    subject = subject.replaceAll("&", "and");
    subject = subject.replaceAll(" ", "_");
    subject = subject.replaceAll(":", "");
    
    fileName = fileName.replaceAll("'", "_");
    fileName = fileName.replaceAll(",", "_");
    fileName = fileName.replaceAll("&", "and");
    fileName = fileName.replaceAll(" ", "_");
    fileName = fileName.replaceAll(":", "");
    
    
    /*if(!(fileName.toUpperCase().endsWith("PDF")) || !(fileName.toUpperCase().endsWith(".TXT")) || !(fileName.toUpperCase().endsWith(".PPT"))
    		|| !(fileName.toUpperCase().endsWith(".DOCX"))) {
          errorMessage = "File type not supported. Please upload .pdf file.";
          return errorMessage;
    }*/
    try {  
          InputStream tempInputStream = file.getInputStream();  ;
          byte[] initialbytes = new byte[4];   
          tempInputStream.read(initialbytes);
          tempInputStream.close();
          String fileType = new String(initialbytes);

          inputStream = file.getInputStream();   
          String filePath = LEARNING_RESOURCES_BASE_PATH+subject+"/"+"Download_Center"+"/"+fileName;
          
          File folderPath = new File(LEARNING_RESOURCES_BASE_PATH+subject+"/"+"Download_Center"+"/");
          if (!folderPath.exists()) {
                boolean created = folderPath.mkdirs();
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
          
        //filePath is changed here as it will be used for file download in portal
          moduleContent.setFilePath("lr/"+subject+"/Download_Center/"+fileName);
  		long downloadCenter = lrDao.saveDownloadCenterContent(moduleContent);
  		if(downloadCenter > 0) {
  		
  		}else {
  			errorMessage= "Error in Saving File details in DB . ";
  		}
          
    } catch (IOException e) {   
          errorMessage = "Error in uploading file. \n Gave error "+e.getMessage();
          //e.printStackTrace();   
    }   
    return errorMessage;
      }
	
	@RequestMapping(value = "/deleteDownloadCenterContents", method = RequestMethod.GET)
	public ModelAndView deleteDownloadCenterContents(@RequestParam("id") int id, HttpServletRequest request, HttpServletResponse response) {
		LearningResourcesDAO lrDao = (LearningResourcesDAO) act.getBean("learningResourcesDAO");
		
		int deletedRow = lrDao.deleteDownloadCenterContent(id);
		if (deletedRow == 0) {
			request.setAttribute("error", "true");
			request.setAttribute("errorMessage", "Error Occured while deleting the Module Content.");
		} else {
			setSuccess(request, "ModuleContent has been deleted successfully");
		}
		return uploadLearningResourcesExcelForm(request,response) ;
		}

	//Module to Video Mapping Via Excel Start uploadModuleVideoMapXl
	@RequestMapping(value = "/uploadModuleVideoMapXl",  method = RequestMethod.POST)
	public ModelAndView uploadModuleVideoMapXl(HttpServletRequest request,	
										 		HttpServletResponse response,
										 		@RequestParam(required=false) String subject,
										 		@ModelAttribute FileStudentPortalBean fileBean) throws IOException{
		/*if(!checkSession(request, response)){
			return new ModelAndView("jsp/studentPortalRediret");
		}*/
		String userId = (String) request.getSession().getAttribute("userId");
		LearningResourcesDAO lrDao = (LearningResourcesDAO) act.getBean("learningResourcesDAO");
		
		CommonsMultipartFile file = fileBean.getFileData();
		if (file==null) {// Check if File was attached
			request.setAttribute("error", "true");
			request.setAttribute("errorMessage", "Please Select File to Upload...");
			return uploadLearningResourcesExcelForm(request,response) ;
		}
		String errorMessage="Error while reading excel Sheet. ";
		ExcelHelper excelHelper = new ExcelHelper();

		try {
			ArrayList<List> resultList = excelHelper.readModuleVideoMapExcel(fileBean);
			ArrayList<ModuleContentStudentPortalBean> contentList = (ArrayList<ModuleContentStudentPortalBean>) resultList.get(0);
			ArrayList<ModuleContentStudentPortalBean> errorBeanList = (ArrayList<ModuleContentStudentPortalBean>) resultList.get(1);

			if (errorBeanList.size() > 0) {
				request.setAttribute("error", "true");
				for(ModuleContentStudentPortalBean errorBean : errorBeanList) {
					errorMessage = errorMessage +"\n"+errorBean.getModuleName();
				}
				request.setAttribute("error", "true");
				request.setAttribute("errorMessage", errorMessage+". Please Select File to Upload...");
				request.setAttribute("errorBeanList", errorBeanList);
				return uploadLearningResourcesExcelForm(request,response) ;
			}
			String mapErrorMessage="";
			for(ModuleContentStudentPortalBean bean : contentList) {
				ModuleContentStudentPortalBean tempBean = lrDao.getModuleVideoMapBean(bean.getModuleName(), bean.getTitle(), subject);
				if(tempBean==null) {
					mapErrorMessage = mapErrorMessage+"\n <br> No match for Mapping row with Chapter Name : "+bean.getModuleName()+" Topic Name : "+bean.getTitle();
				}else {
					tempBean.setType("Topic Video");
					tempBean.setCreatedBy(userId);
					tempBean.setLastModifiedBy(userId);
					long mapedId = lrDao.mapModuleVideo(tempBean);
				if(mapedId==0) {
					mapErrorMessage = mapErrorMessage+"\n Error while saving in DB row with Chapter Name : "+bean.getModuleName()+" Topic Name : "+bean.getTitle();
					}
				}
			}
			if("".equals(mapErrorMessage)) {
				setSuccess(request, "All records mapped successfully.");
			}else {
			request.setAttribute("error", "true");
			request.setAttribute("errorMessage", mapErrorMessage);
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			//e.printStackTrace();
		}
		return uploadLearningResourcesExcelForm(request,response) ;
	}
	//Module to Video Mapping Via Excel End
	
	
	@RequestMapping(value="/student/lastCycleContent",method={RequestMethod.GET})
	public ModelAndView lastCycleContent(HttpServletRequest request,HttpServletResponse response){
		ModelAndView mav = new ModelAndView("jsp/common/lastCycleContent");
		//added to validate for unauthenticated access from lead to the normal pages 
		request.getSession().setAttribute("access", true);
//		if(isLead.equals("true")) {
//			mav = new ModelAndView("jsp/home");
//			request.getSession().setAttribute("access", false);
//			return mav;
//		}	
		String subject = request.getParameter("subject");

		if(subject == null){
			ArrayList<String> studentCourses = (ArrayList<String>)request.getSession().getAttribute("studentCourses_studentportal");
			if(studentCourses != null && studentCourses.size() > 0){
				subject = studentCourses.get(0).trim();
			}else{
				mav.addObject("subject", "");
				setError(request, "No Active Course Available for you");
				return mav;
			}
		}
		mav.addObject("subject", subject);
		
		StudentStudentPortalBean student = (StudentStudentPortalBean)request.getSession().getAttribute("student_studentportal");
		PortalDao pDao = (PortalDao)act.getBean("portalDAO");
		
		List<ContentStudentPortalBean> contentLastCycleList = new ArrayList<ContentStudentPortalBean>();
		List<ContentStudentPortalBean> finalContentLastCycleList = new ArrayList<ContentStudentPortalBean>();
		List<ContentStudentPortalBean> allLastCycleContentListForSubject = new ArrayList<ContentStudentPortalBean>();
		
		if(!"BBA".equalsIgnoreCase(student.getProgram()) && !"B.Com".equalsIgnoreCase(student.getProgram())){
			allLastCycleContentListForSubject=pDao.getContentsForSubjectsForLastCycles(subject,student.getConsumerProgramStructureId());
		}
			
		String programStructureForStudent = student.getPrgmStructApplicable();
		for (ContentStudentPortalBean contentBean : allLastCycleContentListForSubject) {
			String programStructureForContent = contentBean.getProgramStructure();

			if(programStructureForContent == null || "".equals(programStructureForContent.trim()) || "All".equals(programStructureForContent)){
				contentLastCycleList.add(contentBean);
			}else if(programStructureForContent.equals(programStructureForStudent)){
				contentLastCycleList.add(contentBean);
			}
		}

		for(ContentStudentPortalBean contentBean : contentLastCycleList){
			if(pDao.checkIfBookmarked(student.getSapid(),contentBean.getId())){
				contentBean.setBookmarked("Y");
			}
			finalContentLastCycleList.add(contentBean);
		}
		
		request.getSession().setAttribute("contentLastCycleList", finalContentLastCycleList);
		mav.addObject("contentLastCycleList", finalContentLastCycleList);

		return mav;
		
	}
	

	@RequestMapping(value = "/student/setBookmark", method = {RequestMethod.POST}, consumes="application/json")
	@ResponseBody
	public void setBookmark(HttpServletRequest request,@RequestBody ContentStudentPortalBean contentBean) {
		PortalDao dao = (PortalDao)act.getBean("portalDAO");
		String userId = (String) request.getSession().getAttribute("userId");
		
		if(StringUtil.isBlank(userId))
			userId = contentBean.getSapId();
		
		dao.setBookmark(contentBean,userId);
	}

	@RequestMapping(value="/student/bookmarks", method={RequestMethod.GET})
	public ModelAndView getBookmarks(HttpServletRequest request,HttpServletResponse response){
		ModelAndView modelAndView = new ModelAndView("jsp/courseHome/bookmarksDemo");
		PortalDao dao = (PortalDao)act.getBean("portalDAO");
		String userId = (String) request.getSession().getAttribute("userId");
		String isLoginAsLead = (String) request.getSession().getAttribute("isLoginAsLead");
		List<ContentStudentPortalBean> contentBeanList = dao.getContentBookmarks(userId);
		List<VideoContentStudentPortalBean> videoContentBeanList = dao.getVideoContentBookmarks(userId);

		modelAndView.addObject("isLoginAsLead", isLoginAsLead);
		request.getSession().setAttribute("bookmarkContentList",contentBeanList);
		request.getSession().setAttribute("bookmarkVideoCotentList",videoContentBeanList);

		return modelAndView;
	}
	
	@CrossOrigin(origins = "*", allowedHeaders = "*")
	@RequestMapping(value="/getBookmarksResources", method={RequestMethod.POST})
	public ResponseEntity<List<ContentStudentPortalBean>> getBookmarksResources(@RequestBody ContentStudentPortalBean content){
		HttpHeaders headers = new HttpHeaders();
		PortalDao dao = (PortalDao)act.getBean("portalDAO");		
		List<ContentStudentPortalBean> contentBeanList = dao.getContentBookmarks(content.getSapId());		
		return new ResponseEntity<>(contentBeanList,headers,HttpStatus.OK);		
	}
	
	@CrossOrigin(origins = "*", allowedHeaders = "*")
	@RequestMapping(value="/getBookmarksVideos", method={RequestMethod.POST})
	public ResponseEntity<List<VideoContentStudentPortalBean>> getBookmarksVideos(@RequestBody ContentStudentPortalBean content){
		HttpHeaders headers = new HttpHeaders();
		PortalDao dao = (PortalDao)act.getBean("portalDAO");				
		List<VideoContentStudentPortalBean> videoContentBeanList = dao.getVideoContentBookmarks(content.getSapId());
		return new ResponseEntity<>(videoContentBeanList,headers,HttpStatus.OK);		
	}
}

